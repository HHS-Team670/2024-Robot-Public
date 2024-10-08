package frc.team670.robot.commands.Intake;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.Shooter;

public class AutonStartIntake extends Command implements MustangCommand{
    private Intake intake;
    private Indexer indexer;
    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public AutonStartIntake(Intake intake, Indexer indexer,Shooter shooter) {
        this.intake = intake;
        this.indexer = indexer;
        this.shooter = shooter;
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(intake, HealthState.GREEN);
        healthReqs.put(indexer, HealthState.GREEN);
        ConsoleLogger.consoleLog("STArtED INTAKE");
    }

    @Override
    public void execute(){
        
        intake.setIntakeMode(Intake.Mode.INTAKING);
        indexer.setIndexerMode(Indexer.Mode.INTAKING);
        shooter.runFeeder();
    }

    public boolean isFinished(){
       return shooter.hasNote();
    }
    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
