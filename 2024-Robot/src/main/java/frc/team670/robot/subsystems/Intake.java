package frc.team670.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXLite;
import frc.team670.robot.constants.RobotConstants;

public class Intake extends MustangSubsystemBase {

    public enum Mode {
        EJECTING, INTAKING, OFF;
    }

    private static Intake mInstance;
    private SparkMAXLite roller;
    private static final String INTAKE_CURRENT = "Intake/IntakeCurrent";
    private static final String INTAKE_MODE = "Intake/IntakeMode";
    private int exceededCurrentLimitCount = 0;
    private Mode status = Mode.OFF;
    private Deployer deployer;
    private Indexer indexer;
    private Shooter shooter;
    private LED led;

    public Deployer getDeployer() {
        return deployer;
    }

    public static synchronized Intake getInstance() {
        mInstance = mInstance == null ? new Intake() : mInstance;
        return mInstance;
    }

    public Intake() {
        SparkMAXFactory.Config rollerConfig = SparkMAXFactory.Config.copy(SparkMAXFactory.defaultLowUpdateRateConfig);
        rollerConfig.INVERTED=true;
        roller = SparkMAXFactory.buildSparkMAX(RobotConstants.Intake.kIntakeRollerID,
                rollerConfig, Motor_Type.NEO);
        deployer = Deployer.getInstance();
        roller.setIdleMode(IdleMode.kBrake);

        shooter = Shooter.getInstance();
        indexer = Indexer.getInstance();
        led = LED.getInstance();
    }

    public void setIntakeMode(Intake.Mode status) {
        this.status = status;

        switch (status) {
            case INTAKING:
                intake();
                indexer.setIndexerMode(Indexer.Mode.INTAKING);
                led.setLedMode(LED.Mode.INTAKING);
                break;
            case EJECTING:
                eject();
                indexer.setIndexerMode(Indexer.Mode.EJECTING);
                break;
            case OFF:
                roller.stopMotor();
                indexer.setIndexerMode(Indexer.Mode.OFF);
                led.setLedMode(LED.Mode.OFF);
                break;
            default:
                break;
        }
    }

    private void intake() {
        roller.set(RobotConstants.Intake.kIntakeRollSpeed);
        if (!deployer.isDeployed() && deployer.checkHealth() != HealthState.RED){
            deployer.deploy(true);
        }
    }

    private void eject() {

        if (deployer.isDeployed() || deployer.checkHealth() == HealthState.RED) {
            roller.set(RobotConstants.Intake.kIntakeRollSpeed * -1);
        } else {
            toggleDeployer();
        }

    }

 
    public boolean isRolling() {
        return (roller.get() != 0);

    }

    public void toggleDeployer() {
        deployer.toggleDeployer(this);
    }

    @Override
    public HealthState checkHealth() {
        // fix isErrored for deployer
        if (roller == null || roller.isErrored()) {
            return HealthState.RED;
        }
        return HealthState.GREEN;
    }

    @Override
    public void mustangPeriodic() {
        switch (status) {
            case INTAKING:
                if (!DriverStation.isAutonomousEnabled()) {
                    if (shooter.hasNote()) {
                        roller.stopMotor();
                        setIntakeMode(Mode.OFF);
                        indexer.setIndexerMode(Indexer.Mode.OFF);
                        toggleDeployer();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void debugSubsystem() {
        Logger.recordOutput(INTAKE_CURRENT, roller.getOutputCurrent());
        Logger.recordOutput(INTAKE_MODE, status);

    }

    public static class Deployer extends MustangSubsystemBase {
        public record Config(int kMotorID, MotorConfig.Motor_Type kMotorType, int kContinuousCurrent,
                int kPeakCurrent) {
        }

        private double time = 0;
        private boolean deployed = false;
        private SparkMAXLite mRotator;
        private static Deployer mInstance;
        protected Timer m_timer = new Timer();
        private Config kConfig;
        private final String DEPLOYER_CURRENT = "Deployer/DeployerCurrent";
        private final String DEPLOYER_DEPLOYED = "Deployer/DeployerDeployed";

        // down positive

        public Deployer() {
            kConfig = RobotConstants.Intake.Deployer.kConfig;
            mRotator = SparkMAXFactory.buildSparkMAX(kConfig.kMotorID, SparkMAXFactory.defaultLowUpdateRateConfig,
                    kConfig.kMotorType);
            mRotator.setSmartCurrentLimit(kConfig.kPeakCurrent, kConfig.kContinuousCurrent);
            mRotator.setIdleMode(IdleMode.kBrake);
        }

        @Override
        public HealthState checkHealth() {
            if (mRotator == null || mRotator.isErrored()) {
                return HealthState.RED;
            }
            return HealthState.GREEN;

        }

        @Override
        public void debugSubsystem() {
            Logger.recordOutput(DEPLOYER_DEPLOYED, deployed);
            Logger.recordOutput(DEPLOYER_CURRENT, mRotator.getOutputCurrent());
        }

        @Override
        public void mustangPeriodic() {
            if (m_timer.hasElapsed(time)) {
                m_timer.reset();
                mRotator.stopMotor();
                if(!deployed){
                    mRotator.set(-.05);
                }else{
                    mRotator.set(.03);
                }
            }
            Logger.recordOutput("isDeployed", deployed);
        }

        public static synchronized Deployer getInstance() {
            mInstance = mInstance == null ? new Deployer() : mInstance;
            return mInstance;
        }

        public void toggleDeployer(Intake intake) {
            if (!isDeployed()) {
                deploy(true);
            } else {
                deploy(false);
                intake.setIntakeMode(Mode.OFF);
            }
        }

        public void deploy(boolean deploy) {
            if (deploy) {
                time = RobotConstants.Intake.Deployer.kTimeDown;
                m_timer.restart();
                mRotator.set(RobotConstants.Intake.Deployer.kDownMotorSpeed);
                setCoastMode(true); //should be true
                deployed = true;
            } else {
                time = RobotConstants.Intake.Deployer.kTimeUp;
                m_timer.restart();
                mRotator.set(-RobotConstants.Intake.Deployer.kUpMotorSpeed);
                setCoastMode(false);
                deployed = false;
            }
        }

        /**
         * @param true to set flipout to coast mode, false to set it to brake mode
         */
        public void setCoastMode(boolean coast) {
            if (coast) {
                mRotator.setIdleMode(IdleMode.kCoast);
            } else {
                mRotator.setIdleMode(IdleMode.kBrake);
            }
        }

        public boolean isDeployed() {
            return deployed;
        }

    }
}