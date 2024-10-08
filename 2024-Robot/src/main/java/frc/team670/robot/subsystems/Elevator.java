package frc.team670.robot.subsystems;

import frc.team670.mustanglib.subsystems.MustangSubsystemBase;

import java.util.List;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkLimitSwitch;
import com.revrobotics.SparkPIDController;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkBase.SoftLimitDirection;

import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.mustanglib.utils.functions.MathUtils;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXLite;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.robot.constants.RobotConstants;


public class Elevator extends MustangSubsystemBase {

    public static final float[] kSoftLimits= RobotConstants.Elevator.SOFT_LIMITS;
    
    private SparkLimitSwitch bottomLimitSwitch;
    private SparkLimitSwitch topLimitSwitch;
    private static Elevator mInstance;

    public ElevatorState state = ElevatorState.INDEXER_RETRIEVAL;
    private double height;
    private double targetPosition;
    private SparkMAXLite leadMotor;
    private SparkMAXLite followerMotor;
    private SparkPIDController leadController;
    private RelativeEncoder leadEncoder;
    private double offset;
    private List <SparkMAXLite> motorControllers;
    private boolean hasBeenZeroed; 
    private Shooter shooter;


    private final String ELEVATOR_CURRENT_KEY = this.getName() + "/Current";
    private final String ELEVATOR_HEIGHT_KEY = this.getName() + "/Height";
    private final String ELEVATOR_ROTATIONS_KEY = this.getName() + "/Rotations";
    private final String ELEVATOR_TOP_TRIGGERED_KEY = this.getName() + "/Top";
    private final String ELEVATOR_BOTTOM_TRIGGERED_KEY = this.getName() + "/Bottom";
    private final String ELEVATOR_POSITION = this.getName() + "/target position";

    private final String ELEVATOR_UPPER_SOFTLIMIT = this.getName() + "/Upper Soft Limit";
    private final String ELEVATOR_LOWER_SOFTLIMIT = this.getName() + "/Lower Soft Limit";

    private final String ELEVATOR_LEAD_MOTOR_ERROR = this.getName() + "/Lead Motor Error";
    private final String ELEVATOR_FOLLOWER_MOTOR_ERROR = this.getName() + "/Follower Motor Error";
    private final String ELEVATOR_STATE = this.getName() + "/State";

    public enum ElevatorState{
        INDEXER_RETRIEVAL(0), // Change to as low as possible
        AMP(RobotConstants.Elevator.MAX_HEIGHT),
        PODIUM(1), // Change to as low as possible
        STOWED(0),
        SOURCE(RobotConstants.Elevator.MAX_HEIGHT-.15);


        private double height;
        private ElevatorState(double height) {
            this.height = height;
        }

        public double getHeight() {
            return this.height;
        }
    }

    public Elevator() {
        SparkMAXFactory.Config leaderConfig = SparkMAXFactory.Config.copy(SparkMAXFactory.defaultPositionConfig);
        leaderConfig.INVERTED=true;
        motorControllers = SparkMAXFactory.buildSparkMAXPair(RobotConstants.Elevator.MOTOR_1_ID, RobotConstants.Elevator.MOTOR_2_ID, true,leaderConfig, Motor_Type.NEO);
        leadMotor = motorControllers.get(0);
        followerMotor = motorControllers.get(1);

        leadMotor.setIdleMode(IdleMode.kBrake);
        followerMotor.setIdleMode(IdleMode.kBrake);


        leadEncoder = leadMotor.getEncoder();
        leadController = leadMotor.getPIDController();

        bottomLimitSwitch = leadMotor.getReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyClosed);
        topLimitSwitch = leadMotor.getForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyClosed);

        targetPosition = 0;

        leadController.setP(RobotConstants.Elevator.P);
        leadController.setI(RobotConstants.Elevator.I);
        leadController.setD(RobotConstants.Elevator.D);
        leadController.setFF(RobotConstants.Elevator.FF);
        leadController.setIZone(RobotConstants.Elevator.IZ);
        leadController.setOutputRange(RobotConstants.Elevator.MIN_OUTPUT, RobotConstants.Elevator.MAX_OUTPUT);

        leadController.setSmartMotionMaxVelocity(RobotConstants.Elevator.MAX_ROTATOR_RPM, RobotConstants.Elevator.SLOT);
        leadController.setSmartMotionMinOutputVelocity(RobotConstants.Elevator.MIN_ROTATOR_RPM, RobotConstants.Elevator.SLOT);
        leadController.setSmartMotionMaxAccel(RobotConstants.Elevator.MAX_ACCELERATION, RobotConstants.Elevator.SLOT);
        leadController.setSmartMotionAllowedClosedLoopError(RobotConstants.Elevator.ELEVATOR_ALLOWED_ERROR_IN_METERS, RobotConstants.Elevator.SLOT);

        leadMotor.setSmartCurrentLimit(RobotConstants.Elevator.PEAK_CURRENT);

        leadMotor.setSoftLimit(SoftLimitDirection.kForward, kSoftLimits[1]);
        leadMotor.setSoftLimit(SoftLimitDirection.kReverse, kSoftLimits[0]);
        leadMotor.enableSoftLimit(SoftLimitDirection.kForward, false);
        leadMotor.enableSoftLimit(SoftLimitDirection.kReverse, false);

        shooter = Shooter.getInstance();
    }

    public static synchronized Elevator getInstance() {
        mInstance = mInstance == null ? new Elevator() : mInstance;
        return mInstance;
    }

    public void climb(){
        leadController.setSmartMotionMaxVelocity(2000, 0);
    }

    public boolean isBottomLimitSwitchTripped () 
    {
        return bottomLimitSwitch.isPressed();
    }

    public boolean isTopLimitSwitchTripped() 
    {
        return topLimitSwitch.isPressed();
    }


    public double getHeightInMeters() 
    {
        height = (this.leadEncoder.getPosition()/RobotConstants.Elevator.GEAR_RATIO) * (RobotConstants.Elevator.CIRCUMFERENCE_SPROCKET);
        return height;
    }

    private void setHeightInMeters (double meters)
    {
        targetPosition = (meters / RobotConstants.Elevator.CIRCUMFERENCE_SPROCKET)*(RobotConstants.Elevator.GEAR_RATIO);

        //just a precaution incase the operator tries to go too far down using manual controls
        if(targetPosition < 0){
            targetPosition = 0;
        }

        if (this.hasBeenZeroed){
            if(shooter.getTilter().getCurrentAngleInDegrees() < 182)
                leadController.setReference(targetPosition, CANSparkMax.ControlType.kSmartMotion, RobotConstants.Elevator.SMARTMOTION_SLOT);
            else
                ConsoleLogger.consoleLog("Shooter too high");
        }
    }

     private void zeroElevator()
    {
        leadEncoder.setPosition(0);
    }

    public boolean hasReachedTargetPosition() {
       
        return (MathUtils.doublesEqual(leadEncoder.getPosition(), this.state.getHeight(), RobotConstants.Elevator.ELEVATOR_ALLOWED_ERROR_IN_METERS));
        
    }

    public void addOffset (double offset) {
        this.offset += offset;
        this.setHeightInMeters(this.state.getHeight() + this.offset); 
    }

    public void resetOffset () {
        this.offset = 0;
        this.setHeightInMeters(this.state.getHeight() + offset); 
    }

    public void moveToTarget(ElevatorState state) 
    {
        resetOffset();
        this.state = state;
        this.setHeightInMeters(state.getHeight() + offset); 
        
    }


    @Override
    public HealthState checkHealth() {
        REVLibError leaderRotatorError = leadMotor.getLastError();
        REVLibError followerRotatorError = followerMotor.getLastError();

        boolean leaderOK = (leaderRotatorError == REVLibError.kOk);

        boolean followerOK = (followerRotatorError == REVLibError.kOk);



        if (!leaderOK && !followerOK) {
            Logger.recordOutput(ELEVATOR_LEAD_MOTOR_ERROR, leaderRotatorError);
            Logger.recordOutput(ELEVATOR_FOLLOWER_MOTOR_ERROR, followerRotatorError);
            return HealthState.RED;
        }

        if ((leaderOK && !followerOK) || (!leaderOK && followerOK)) {
            Logger.recordOutput(ELEVATOR_LEAD_MOTOR_ERROR, leaderRotatorError);
            Logger.recordOutput(ELEVATOR_FOLLOWER_MOTOR_ERROR, followerRotatorError);
            return HealthState.YELLOW;
        }

        return HealthState.GREEN;

    }

    @Override
    public void mustangPeriodic() {
        if (bottomLimitSwitch.isPressed() && this.state != ElevatorState.SOURCE)
        {
            this.stop();
            this.zeroElevator();
            leadMotor.enableSoftLimit(SoftLimitDirection.kReverse, true);
            leadMotor.enableSoftLimit(SoftLimitDirection.kForward, true);
            hasBeenZeroed = true;
            Logger.recordOutput(ELEVATOR_UPPER_SOFTLIMIT, RobotConstants.Elevator.UPPER_SOFT_LIMIT);
            Logger.recordOutput(ELEVATOR_LOWER_SOFTLIMIT, RobotConstants.Elevator.LOWER_SOFT_LIMIT);
        }

        if(!hasBeenZeroed){
            leadMotor.set(-.1);
        }

    }

    public void stop() {
        leadMotor.set(0);
    }

    public void setHaBeenZeroedToFalse() {
        hasBeenZeroed = false;
    }

    @Override
    public void debugSubsystem() {
        Logger.recordOutput(ELEVATOR_CURRENT_KEY, leadMotor.getOutputCurrent());
        Logger.recordOutput(ELEVATOR_HEIGHT_KEY, getHeightInMeters());
        Logger.recordOutput(ELEVATOR_POSITION, targetPosition);
        Logger.recordOutput(ELEVATOR_ROTATIONS_KEY, leadEncoder.getPosition());
        Logger.recordOutput(ELEVATOR_TOP_TRIGGERED_KEY, isTopLimitSwitchTripped());
        Logger.recordOutput(ELEVATOR_BOTTOM_TRIGGERED_KEY, isBottomLimitSwitchTripped());
        Logger.recordOutput(ELEVATOR_STATE, state);

    }


}


    
    