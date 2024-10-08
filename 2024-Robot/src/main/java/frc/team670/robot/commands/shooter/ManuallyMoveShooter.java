package frc.team670.robot.commands.shooter;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Shooter;


public class ManuallyMoveShooter extends InstantCommand implements MustangCommand {

    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private Shooter shooter;
    private boolean directionPositive;
    
    public ManuallyMoveShooter(Shooter shooter, boolean directionPositive) {
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
        this.shooter = shooter;
        this.directionPositive = directionPositive;
    }

    public void initialize() {
        if (directionPositive) {
            shooter.getTilter().addOffset(2);
        } else {
            shooter.getTilter().addOffset(-2);
        }
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
}
