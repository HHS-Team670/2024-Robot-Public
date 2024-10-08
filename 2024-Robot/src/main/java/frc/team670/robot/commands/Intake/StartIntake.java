package frc.team670.robot.commands.Intake;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.Shooter;

public class StartIntake extends InstantCommand implements MustangCommand{
    private Intake intake;
    private Indexer indexer;
    private Shooter shooter;
    private Elevator elevator;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public StartIntake(Intake intake, Indexer indexer,Shooter shooter, Elevator elevator) {
        this.intake = intake;
        this.indexer = indexer;
        this.shooter = shooter;
        this.elevator = elevator;
        addRequirements(intake, indexer,shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(intake, HealthState.GREEN);
        healthReqs.put(indexer, HealthState.GREEN);
    }

    @Override
    public void execute(){
        if(DriverStation.isAutonomousEnabled()){
            shooter.stopFeeder();
        }
        if(elevator.getHeightInMeters() <= 0.15 && !shooter.hasNote()){
            intake.setIntakeMode(Intake.Mode.INTAKING);
            indexer.setIndexerMode(Indexer.Mode.INTAKING);
            shooter.runFeeder();
            shooter.setAngle(115);
        }
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
