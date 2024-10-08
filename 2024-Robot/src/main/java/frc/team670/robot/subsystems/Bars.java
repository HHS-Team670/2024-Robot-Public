package frc.team670.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;

import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXLite;
import frc.team670.robot.constants.RobotConstants;


public class Bars extends MustangSubsystemBase{


    private static Bars mInstance;
    private SparkMAXLite roller;
    private SparkPIDController rollerController;

    private boolean barsDeployed = false;
    private final String BARS_DEPLOYED = "Bars/BarsDeployed";

    public static synchronized Bars getInstance() {
        mInstance = mInstance == null ? new Bars() : mInstance;
        return mInstance;
        // return null;
    }

    public Bars() {
        SparkMAXFactory.Config rollerConfig = SparkMAXFactory.Config.copy(SparkMAXFactory.defaultPositionConfig);
        rollerConfig.INVERTED=false;
        roller = SparkMAXFactory.buildSparkMAX(RobotConstants.Bars.kBarsRollerID, rollerConfig, Motor_Type.NEO);
        roller.setIdleMode(IdleMode.kBrake);
        rollerController = roller.getPIDController();

        rollerController.setP(0);
        rollerController.setI(0);
        rollerController.setD(0);
        rollerController.setIZone(0);
        rollerController.setFF(0);
        rollerController.setOutputRange(0, 0);

        rollerController.setSmartMotionMaxVelocity(0, 0);
        rollerController.setSmartMotionMinOutputVelocity(0, 0);
        rollerController.setSmartMotionMaxAccel(0, 0);
        rollerController.setSmartMotionAllowedClosedLoopError(0, 0);
    }

    public void deployBars(){
        rollerController.setReference(.25, CANSparkMax.ControlType.kSmartMotion, 0);
        roller.set(RobotConstants.Bars.kBarsRollSpeedDown);
        barsDeployed = true;
    }

    public void undeployBars(){
        rollerController.setReference(0, CANSparkMax.ControlType.kSmartMotion, 0);
        roller.set(RobotConstants.Bars.kBarsRollSpeedUp * -1);
        barsDeployed = false;
    }

    public boolean areDeployed(){
        return barsDeployed;
    }

    @Override
    public HealthState checkHealth() {
        if (roller.isErrored()) {
            return HealthState.RED;
        }
        return HealthState.GREEN;    
    }

    @Override
    public void mustangPeriodic() {
    }

    @Override
    public void debugSubsystem() {
        Logger.recordOutput(BARS_DEPLOYED, barsDeployed);
    }
}



