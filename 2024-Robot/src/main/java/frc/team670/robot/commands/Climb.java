package frc.team670.robot.commands;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Bars;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.Shooter;


public class Climb extends InstantCommand implements MustangCommand{

    private Bars bars;
    private Shooter shooter;
    private Elevator elevator;
    private Intake intake;
    private static int count = 0;
    private Map<MustangSubsystemBase, HealthState> healthReqs;


    public Climb(Bars bars, Shooter shooter, Elevator elevator, Intake intake) {
        this.bars = bars;
        this.shooter = shooter;
        this.elevator = elevator;
        this.intake = intake;
    }


    public void initialize() {
        switch (count){
            case 0:
                shooter.setAngle(160);
                break;
            case 1:
               elevator.moveToTarget(Elevator.ElevatorState.AMP);
                break;
            case 2:
                if(intake.getDeployer().isDeployed()){
                    intake.getDeployer().toggleDeployer(intake);
                }
                bars.deployBars();
                elevator.climb();
                break;
            case 3:
                elevator.moveToTarget(Elevator.ElevatorState.INDEXER_RETRIEVAL);
                count = 0;
                break;
        }

        count++;
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
