package frc.team670.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.REVLibError;
import com.revrobotics.CANSparkBase.IdleMode;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXFactory;
import frc.team670.mustanglib.utils.motorcontroller.SparkMAXLite;
import frc.team670.robot.constants.RobotConstants;

public class Indexer extends MustangSubsystemBase {

	public SparkMAXLite roller;
	private static Indexer mInstance;
	
	private static final String INDEXER_CURRENT = "Indexer/IndexerCurrent";
	private static final String INDEXER_MODE  = "Indexer/IndexerMode";



	public enum Mode {
		OFF,
		INTAKING,
		EJECTING
	}

	private Mode status = Mode.OFF;
    
	public Indexer(){
		SparkMAXFactory.Config rollerConfig = SparkMAXFactory.Config.copy(SparkMAXFactory.defaultLowUpdateRateConfig);
        rollerConfig.INVERTED=true;
		roller = SparkMAXFactory.buildSparkMAX(RobotConstants.Indexer.kMotorID, rollerConfig, Motor_Type.NEO);
		roller.setIdleMode(IdleMode.kBrake);
		roller.setInverted(true);

	}

	public static synchronized Indexer getInstance() {
		mInstance = mInstance == null ? new Indexer() : mInstance;
		return mInstance;
	} 
	
	 public boolean isRunning(){
	 	return status != Mode.OFF;
	}

	public void setIndexerMode(Indexer.Mode status) {
		this.status = status;

		switch(status) {
			case INTAKING:
				this.run(true);
				break;
			case EJECTING:
				this.run(false);
				break;
			case OFF:
				roller.stopMotor();
				break;
			default: 
				break;
				
		}	
	}
	
	// Runs the Indexer in the specified direction
	public void run(boolean intaking) {
		if (intaking) {
			roller.set(-RobotConstants.Indexer.kIntakeSpeed); 
		} else {
			roller.set(RobotConstants.Indexer.kIntakeSpeed);	
		}
	} 

	@Override
	public HealthState checkHealth() {
		REVLibError indexerError = roller.getLastError();
		if (indexerError != null && indexerError != REVLibError.kOk){
			return HealthState.RED;
		}
		return HealthState.GREEN;
	}

	@Override
	public void mustangPeriodic() {
		debugSubsystem();			
	}

	@Override
	public void debugSubsystem() {
		Logger.recordOutput(INDEXER_MODE, status);
		Logger.recordOutput(INDEXER_CURRENT, roller.getOutputCurrent());
	}

	
}