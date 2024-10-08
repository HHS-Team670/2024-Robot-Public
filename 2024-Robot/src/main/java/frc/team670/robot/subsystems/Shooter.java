package frc.team670.robot.subsystems;

import org.littletonrobotics.junction.Logger;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkBase;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.SparkMaxRotatingSubsystem;
import frc.team670.mustanglib.subsystems.LEDSubsystem.LEDColor;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.mustanglib.utils.functions.MathUtils;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory.Config;
import frc.team670.robot.constants.RobotConstants;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXLite;
import frc.team670.mustanglib.dataCollection.sensors.BeamBreak;

public class Shooter extends MustangSubsystemBase {

    private static Shooter mInstance;

    private SparkMAXLite shooterController, feederController, directionController;
    private RelativeEncoder shooterEncoder, directionEncoder;
    private SparkPIDController shooterPIDController, directionPIDController;

    
    protected Timer mTimer = new Timer();

    private boolean isShooting = false;
    private boolean isFeedingFromIndexer = false;

    private BeamBreak beamBreak;
   
    private LED led;
    private Tilter tilter;
    private double time=0.1;
    private HealthState health = HealthState.GREEN;
    private Notifier notifier;

    

    private final String SHOOTER_VELOCITY_KEY = this.getName() + "/Shooter Velocity (RPM)";
    private final String SHOOTER_DIRECT_VELOCITY_KEY = this.getName() + "/Direction Velocity (RPM)";
    private final String SHOOTER_FEEDER_PERCENTAGE_KEY = this.getName() + "/Feeder Power Percentage";
    private final String SHOOTER_MOTOR_VELOCITY_KEY =  this.getName() + "/Shooter Motor Velocity";
    private final String FEEDER_MOTOR_CURRENT_KEY = this.getName() + "/Feeder Motor Current";
    private final String SHOOTER_MOTOR_CURRENT_KEY = this.getName() + "/Shooter Motor Current";
    private final String SHOOTER_STATE_KEY = this.getName() + "/Shooter State";
    
    public enum Modes {
        SPEAKER, AMP, INTAKE, TRAP;
    }

    private Modes targetMode = Modes.SPEAKER;

    public Shooter() {
        setName("Shooter");
        tilter = new Tilter();
        // setLogFileHeader("Shooter Velocity Setpoint", "Shooter velocity", "Speed",
        // "Ultrasonic distance", "P", "I", "D", "FF", "Ramp Rate");
        beamBreak = new BeamBreak(RobotConstants.Shooter.kBeamBreakID);
        Config shooterConfig = Config.copy(SparkMAXFactory.defaultLowUpdateRateConfig);
        shooterConfig.INVERTED=true;
        shooterController = SparkMAXFactory.buildSparkMAX(RobotConstants.Shooter.kVelocityShooterID, shooterConfig, Motor_Type.NEO);
        led = LED.getInstance();

        Config feederConfig = Config.copy(SparkMAXFactory.defaultLowUpdateRateConfig);
        feederConfig.INVERTED=true;
        feederController = SparkMAXFactory.buildSparkMAX(RobotConstants.Shooter.kFeederMotorID, feederConfig, Motor_Type.NEO);
        
        Config directionConfig = Config.copy(SparkMAXFactory.defaultLowUpdateRateConfig);
        directionConfig.INVERTED=true;
        directionController = SparkMAXFactory.buildSparkMAX(RobotConstants.Shooter.kDirectionMotorID, directionConfig, Motor_Type.NEO);

        shooterEncoder = shooterController.getEncoder();
        shooterPIDController = shooterController.getPIDController();

        feederController.setIdleMode(IdleMode.kBrake);
        shooterController.setIdleMode(IdleMode.kCoast);
        directionController.setIdleMode(IdleMode.kCoast);

        shooterPIDController.setP(RobotConstants.Shooter.kP, RobotConstants.Shooter.kVelocitySlot);
        shooterPIDController.setI(RobotConstants.Shooter.kI, RobotConstants.Shooter.kVelocitySlot);
        shooterPIDController.setD(RobotConstants.Shooter.kD, RobotConstants.Shooter.kVelocitySlot);
        shooterPIDController.setFF(RobotConstants.Shooter.kFF, RobotConstants.Shooter.kVelocitySlot);

        directionEncoder = directionController.getEncoder();
        directionPIDController = directionController.getPIDController();

        //Might need different values for the constants
        directionPIDController.setP(RobotConstants.Shooter.kP, RobotConstants.Shooter.kVelocitySlot);
        directionPIDController.setI(RobotConstants.Shooter.kI, RobotConstants.Shooter.kVelocitySlot);
        directionPIDController.setD(RobotConstants.Shooter.kD, RobotConstants.Shooter.kVelocitySlot);
        directionPIDController.setFF(RobotConstants.Shooter.kFF, RobotConstants.Shooter.kVelocitySlot);


        SmartDashboard.putNumber("Shooter Target Angle", 0);

        Notifier.setHALThreadPriority(true, 45);
        notifier = new Notifier(this::shooterPeriodicCheck);
        notifier.startPeriodic(0.005);
    }

    public static synchronized Shooter getInstance() {
        mInstance = mInstance == null ? new Shooter() : mInstance;
        return mInstance; 

    }

    public Tilter getTilter(){
        return tilter;
    }
    
    public double getShooterVelocity() {
        return shooterEncoder.getVelocity();
    }

    private double getDirectionVelocity() {
        return directionEncoder.getVelocity();
    }
    
    //Uses smart dashboard to set values
    public void setShooterSpeed(Modes mode) {
        
        if (mode == Modes.SPEAKER) {
            directionController.set(ControlType.kVelocity, RobotConstants.Shooter.MaxRPM);
            shooterController.set(ControlType.kVelocity, RobotConstants.Shooter.MaxRPM);
            this.targetMode = Modes.SPEAKER;
        } else if (mode == Modes.AMP) {
            directionController.set(ControlType.kVelocity,0.5* RobotConstants.Shooter.MaxRPM);
            shooterController.set(ControlType.kVelocity, 0.5*RobotConstants.Shooter.MaxRPM);
            
            this.targetMode = Modes.AMP;
        }
        else if(mode == Modes.TRAP){
            directionController.set(ControlType.kVelocity,0.2* RobotConstants.Shooter.MaxRPM);
            shooterController.set(ControlType.kVelocity, 0.2*RobotConstants.Shooter.MaxRPM);
            
            this.targetMode = Modes.AMP;
        }
        else {
            directionController.set(ControlType.kVelocity,-0.5* RobotConstants.Shooter.MaxRPM);
            shooterController.set(ControlType.kVelocity, -0.5*RobotConstants.Shooter.MaxRPM);
            
            this.targetMode = Modes.INTAKE;
        }
        SmartDashboard.putBoolean("Target is Speaker?", targetMode == Modes.SPEAKER ? true : false);
    }

    public BeamBreak getBeamBreak() {
        return beamBreak;
    }

    public boolean readyToShoot(){
        return (hasNote() || DriverStation.isTeleopEnabled()) ;
    }
    
    //Runs after top two motors get to speed
    public void shoot(){
        this.isShooting = true;
        mTimer.restart();
        feederController.set(RobotConstants.Shooter.kFeederShootPercentage);
    }

    public boolean isShooting(){
        return isShooting;
    }

    public boolean setAngle(double angle) {
        return tilter.setSystemTargetAngleInDegrees(angle);
    }

    public double getAbsoluteAngle() {
        return tilter.shooterAngleEncoder.getAbsolutePosition();
    }

    public boolean isAtTargetAngle() {
        return tilter.hasReachedTargetPosition();
    }

    public Modes getCurrentState(){
        return this.targetMode;
    }

    public void stopShooting() {
        shooterController.setIdleMode(IdleMode.kCoast);
        directionController.setIdleMode(IdleMode.kCoast);
        shooterController.set(CANSparkBase.ControlType.kVoltage, 0);
        directionController.set(CANSparkBase.ControlType.kVoltage, 0);
        stopFeeder();
    }

    public void runFeeder() {
        this.isFeedingFromIndexer = true;
        feederController.set(RobotConstants.Shooter.kFeederIntakePercentage);
    }

    
	public void runFeederReversed() {
		feederController.set(-RobotConstants.Shooter.kFeederIntakePercentage);
	}

    public void stopFeeder() {
        this.isShooting = false;
        this.isFeedingFromIndexer = false;
        mTimer.stop();
        feederController.setIdleMode(IdleMode.kCoast);
        feederController.set(0);

        ConsoleLogger.consoleLog("Feeder has been stopped");
    }

    @Override
    public HealthState checkHealth() {
        //record error, but don't kill the subsystem
        if (shooterController.isErrored() || directionController.isErrored() || feederController.isErrored()) {
            if(health != HealthState.RED){
                ConsoleLogger.consoleLog("Shooter Error " + shooterController.getLastError().toString() + ", " + directionController.getLastError().toString() + ", " + feederController.getLastError().toString());
            }
            health = HealthState.RED;
        }else{
            health = HealthState.GREEN;
        }
        return HealthState.GREEN;
    }

    public boolean hasNote(){
        return beamBreak.isTriggered();
    }

    
    public void shooterPeriodicCheck() {
        if (beamBreak.isTriggered() && this.isFeedingFromIndexer && !this.isShooting) {
            stopFeeder();
            led.setLedMode(LED.Mode.HASNOTE);
            if(!DriverStation.isAutonomousEnabled()){
                setAngle(135);
            }
            ConsoleLogger.consoleLog("bb && feeding && !shooting");
        }
        

    }

    @Override
    public void debugSubsystem() {
        SmartDashboard.putNumber("Shooter Velocity (RPM)", shooterController.getEncoder().getVelocity());
        SmartDashboard.putNumber("Direction Velocity (RPM)", directionController.getEncoder().getVelocity());
        beamBreak.sendBeamBreakDataToDashboard();
        Logger.recordOutput(SHOOTER_VELOCITY_KEY, shooterController.getEncoder().getVelocity());
        Logger.recordOutput(SHOOTER_DIRECT_VELOCITY_KEY, directionController.getEncoder().getVelocity());
        Logger.recordOutput(SHOOTER_FEEDER_PERCENTAGE_KEY, feederController.get());
        Logger.recordOutput(SHOOTER_MOTOR_VELOCITY_KEY, shooterController.getEncoder().getVelocity());
        Logger.recordOutput(FEEDER_MOTOR_CURRENT_KEY, feederController.getOutputCurrent());
        Logger.recordOutput(SHOOTER_MOTOR_CURRENT_KEY, shooterController.getOutputCurrent());
        Logger.recordOutput(SHOOTER_STATE_KEY, getCurrentState());
        Logger.recordOutput("Shooter"+  "/Direction Motor Current", directionController.getOutputCurrent());
        Logger.recordOutput("Shooter/is Shooting", isShooting);
        Logger.recordOutput("Shooter/feeding from indexer", isFeedingFromIndexer);
        Logger.recordOutput("Shooter/Timer Time", mTimer.get());
        
    }

    public class Tilter extends SparkMaxRotatingSubsystem {
        //For rotating
        private DutyCycleEncoder shooterAngleEncoder;
        private boolean hasSetAbsolutePosition = false;
        private double previousReading = 0.0;
        private double calculatedRelativePosition = 0.0;
        private boolean relativePositionIsSet = false;
        private double offset = 0;
        private double orgTargetAngle = 0;
        private Debouncer encoderDebouncer = new Debouncer(0.5);
        private boolean encoderDebouncerThreshold = false;
        private final String TILTER_CURRENT_POSITION = "Tilter"+"/Tilter position (deg)";
        private final String TILTER_ABSOLUTE_ENCODER_POSITION = "Tilter"+"/Tilter abs encoder position";
        private final String TILTER_CURRENT = "Tilter"+"/Tilter current";
        private final String TILTER_TARGET_ANGLE = "Tilter"+"/Tilter target angle (deg)";
        private final String TILTER_VELOCITY = "Tilter" + "/Tilter velocity (rpm)";
        private final String TITLE_MOTOR_ENCODER_POSITION = "Tilter" + "/Tilter Motor Encoder Position";
        private HealthState health = HealthState.GREEN;
    
        public Tilter() {
            super(RobotConstants.Shooter.Tilter.kConfig);
            shooterAngleEncoder = new DutyCycleEncoder(RobotConstants.Shooter.Tilter.kAbsoluteEncoderID);
            shooterAngleEncoder.setPositionOffset(RobotConstants.Shooter.Tilter.kAbsoluteEncoderOffset);
        }

        /**
         * Public method to reset the position from the absolute position.
         */
        public void resetPositionFromAbsolute() {
            setEncoderPositionFromAbsolute();
        }

        public double getOffset(){
            return offset;
        }

        /**
         * PRIVATE method to set position from absolute. DO NOT USE DIRECTLY. Instead,
         * use
         * resetPositionFromAbsolute()
         */
        private void setEncoderPositionFromAbsolute() {
            //zeroing 180 and above does not work
            double absEncoderPosition = shooterAngleEncoder.getAbsolutePosition();
            double previousPositionRot = super.mEncoder.getPosition();

            if (absEncoderPosition != 0.0) {
                if(RobotConstants.Shooter.Tilter.kAbsoluteEncoderOffset < .5){
                    if(absEncoderPosition > .5){
                        absEncoderPosition  -= 1;
                    }
                }

                double relativePosition = ((1 * (absEncoderPosition
                        - RobotConstants.Shooter.Tilter.kAbsoluteEncoderOffset))
                        * RobotConstants.Shooter.Tilter.kGearRatio) % RobotConstants.Shooter.Tilter.kGearRatio;
                

                if(relativePosition < 0.0){
                    relativePosition += RobotConstants.Shooter.Tilter.kGearRatio;
                }
                // First time zeroing
                if (calculatedRelativePosition == 0.0) {
                    clearSetpoint();

                    REVLibError error = mEncoder.setPosition(relativePosition);
                    SmartDashboard.putNumber("Shooter absEncoder position when reset",absEncoderPosition);
                    SmartDashboard.putNumber("Shooter relEncoder position when reset", relativePosition);
                    SmartDashboard.putString("Shooter error", error.toString());
                    calculatedRelativePosition = relativePosition;
                }
            }
        }
        
        public void mustangPeriodic() {
            if (!hasSetAbsolutePosition) { // before it's set an absolute position...
                double position = shooterAngleEncoder.getAbsolutePosition();
                
                if (Math.abs(previousReading - position) < 0.02 && position != 0.0) { // If the current
                                                                                    // reading is
                                                                                    // PRECISELY
                                                                                    // 0, then it's
                    encoderDebouncerThreshold = encoderDebouncer.calculate(true);  
                } else {
                    encoderDebouncerThreshold = encoderDebouncer.calculate(false);
                    previousReading = position;
                }
                if(encoderDebouncerThreshold){
                    setEncoderPositionFromAbsolute();
                    hasSetAbsolutePosition = true;
                }
            } else if (!relativePositionIsSet) {
                if (Math.abs(super.mEncoder.getPosition() - calculatedRelativePosition) < 0.5) {
                    relativePositionIsSet = true;
                } else {
                    super.mEncoder.setPosition(calculatedRelativePosition);
                }
            }
        }

        //0 degrees is straight down
        public boolean setSystemTargetAngleInDegrees(double targetAngle) {
            if(targetAngle > RobotConstants.Shooter.Tilter.kMinAngle && targetAngle < RobotConstants.Shooter.Tilter.kMaxAngle){
                orgTargetAngle = targetAngle; 
                return super.setSystemTargetAngleInDegrees(targetAngle + offset);
            }
            return false;
    }

        protected void setOffset(double offset) {
            this.offset = offset;
            setSystemTargetAngleInDegrees(orgTargetAngle+offset);
        }
        public void addOffset(double offset){
            setOffset(this.offset+offset);
        }
        public void resetOffset(){
            setOffset(0);
        }

        @Override
        public boolean hasReachedTargetPosition() {
            double pos = mEncoder.getPosition();
            boolean reached =  (MathUtils.doublesEqual(pos, mSetpoint, RobotConstants.Shooter.Tilter.kAllowedErrorRotations));
            Logger.recordOutput("Shooter/Encoder pos", pos);
            Logger.recordOutput("Shooter/setpoint", mSetpoint);
            Logger.recordOutput("Shooter/allowed error rot",RobotConstants.Shooter.Tilter.kAllowedErrorRotations );
            Logger.recordOutput("Shooter/calculated at angle", reached);
            return reached;
        }

        @Override
        public boolean getTimeout() {
        return false;
        }

        @Override
        public HealthState checkHealth() {
            REVLibError rotatorError = super.mRotator.getLastError();

            if (rotatorError != null && rotatorError != REVLibError.kOk) {
                if(health != HealthState.RED){
                    ConsoleLogger.consoleLog("Tilter error! Rotator Error is " + rotatorError.toString());
                    health =  HealthState.RED;          
                }
            } 
            return HealthState.GREEN;
        }

        @Override
        public void debugSubsystem() {
            Logger.recordOutput(TILTER_CURRENT_POSITION, getCurrentAngleInDegrees());
            Logger.recordOutput(TILTER_TARGET_ANGLE, orgTargetAngle);
            Logger.recordOutput(TILTER_ABSOLUTE_ENCODER_POSITION, shooterAngleEncoder.getAbsolutePosition());
            Logger.recordOutput(TILTER_CURRENT, super.getRotator().getOutputCurrent());
            Logger.recordOutput(TILTER_VELOCITY, super.getRotator().getEncoder().getVelocity());
            Logger.recordOutput(TITLE_MOTOR_ENCODER_POSITION, super.getRotator().getEncoder().getPosition());
            Logger.recordOutput("Tilter/has set absolute pos", hasSetAbsolutePosition);
            Logger.recordOutput("Tilter"+"/setpoint rotations", mSetpoint);
        }
    }

    @Override
    public void mustangPeriodic() {
        //if the beambreak in the shooter is no longer triggered turn off the shooter feeder motor? should be clear 
        if(!DriverStation.isAutonomousEnabled() && !beamBreak.isTriggered() && isShooting && mTimer.hasElapsed(time)){
            stopFeeder();
            led.setLedMode(LED.Mode.OFF);
            ConsoleLogger.consoleLog("!bb && shooting");
        }
    }



}