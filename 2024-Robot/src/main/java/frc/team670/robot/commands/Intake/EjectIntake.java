package frc.team670.robot.commands.Intake;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Intake;

public class EjectIntake extends InstantCommand implements MustangCommand{
    private Intake intake;
    private Indexer indexer;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public EjectIntake(Intake intake, Indexer indexer) {
        this.intake = intake;
        this.indexer = indexer;
        addRequirements(intake, indexer);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(intake, HealthState.GREEN);
        healthReqs.put(indexer, HealthState.GREEN);
    }

    @Override
    public void execute(){
        intake.setIntakeMode(Intake.Mode.EJECTING);
        indexer.setIndexerMode(Indexer.Mode.EJECTING);

    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
       
}
