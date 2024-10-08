package frc.team670.robot.commands.elevator;

import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;

import java.util.HashMap;
import java.util.Map;


import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.robot.subsystems.Elevator;

public class StopElevator extends InstantCommand implements MustangCommand{

    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private Elevator elevator;
    
    public StopElevator(Elevator elevator) {
        addRequirements(elevator);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(elevator, HealthState.GREEN);
        this.elevator = elevator;
    }

    public void execute() {
        elevator.stop();
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}