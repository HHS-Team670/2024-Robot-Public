package frc.team670.robot.commands.shooter;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Shooter.Modes;

public class Subwoofer extends InstantCommand implements MustangCommand {
    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public Subwoofer(Shooter shooter) {
        this.shooter = shooter;
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        // healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        shooter.setShooterSpeed(Modes.SPEAKER);
        shooter.setAngle(147);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}
   