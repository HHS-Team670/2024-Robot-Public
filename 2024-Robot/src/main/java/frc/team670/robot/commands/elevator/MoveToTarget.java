package frc.team670.robot.commands.elevator;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Elevator.ElevatorState;


public class MoveToTarget extends Command implements MustangCommand {
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private Elevator elevator;
    private ElevatorState target;

    public MoveToTarget(Elevator elevator, ElevatorState target) {
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(elevator, HealthState.GREEN);
        this.elevator = elevator;
        this.target = target;
    }

    @Override
    public void initialize() {
        elevator.moveToTarget(target);
        ConsoleLogger.consoleLog("Ran MoveDirectlyToTarget with the target " + target.toString());
    }

    @Override
    public boolean isFinished() {
        return elevator.hasReachedTargetPosition();
    }

    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}

