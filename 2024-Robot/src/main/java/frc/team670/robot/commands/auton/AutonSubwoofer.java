package frc.team670.robot.commands.auton;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Shooter.Modes;

public class AutonSubwoofer extends Command implements MustangCommand{

    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public AutonSubwoofer(Shooter shooter) {
        this.shooter = shooter;
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        // healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        shooter.setShooterSpeed(Modes.SPEAKER);
        shooter.setAngle(155);
    }

    @Override
    public boolean isFinished(){
    return shooter.getTilter().hasReachedTargetPosition() && shooter.getShooterVelocity() > 3000;
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
