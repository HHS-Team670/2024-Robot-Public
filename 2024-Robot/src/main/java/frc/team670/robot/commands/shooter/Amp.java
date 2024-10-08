package frc.team670.robot.commands.shooter;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Elevator.ElevatorState;
import frc.team670.robot.subsystems.Shooter.Modes;

public class Amp extends InstantCommand implements MustangCommand {
    private Shooter shooter;
    private Elevator elevator;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public Amp(Shooter shooter, Elevator elevator) {
        this.shooter = shooter;
        this.elevator = elevator;
        addRequirements(shooter, elevator);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        shooter.setShooterSpeed(Modes.AMP);
        shooter.setAngle(65);
        elevator.moveToTarget(ElevatorState.AMP);
    }
    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}
