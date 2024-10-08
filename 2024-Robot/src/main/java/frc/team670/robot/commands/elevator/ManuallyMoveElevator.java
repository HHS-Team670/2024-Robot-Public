package frc.team670.robot.commands.elevator;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;


public class ManuallyMoveElevator extends InstantCommand implements MustangCommand {

    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private Elevator elevator;
    private boolean directionPositive;
    
    public ManuallyMoveElevator(Elevator elevator, boolean directionPositive) {
        addRequirements(elevator);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(elevator, HealthState.GREEN);
        this.elevator = elevator;
        this.directionPositive = directionPositive;
    }

    public void execute() {
        if (directionPositive) {
            elevator.addOffset(0.05);
        } else {
            elevator.addOffset(-0.05);
        }
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
