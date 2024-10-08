package frc.team670.robot.commands.elevator;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;

public class ZeroElevator extends InstantCommand implements MustangCommand {

    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private Elevator elevator;
    private Shooter shooter;

    public ZeroElevator(Elevator elevator, Shooter shooter) {
        addRequirements(elevator);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(elevator, HealthState.GREEN);
        this.elevator = elevator;
        this.shooter = shooter;
    }


    public void initialize() {
        shooter.setAngle(135);
        elevator.setHaBeenZeroedToFalse();
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}