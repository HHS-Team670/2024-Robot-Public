package frc.team670.robot.commands.shooter;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Shooter.Modes;

public class SetSpeed extends InstantCommand implements MustangCommand {
    
    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private boolean isSpeaker;
    

    //This command is used to manually set the speed using smart dashboard
    public SetSpeed(Shooter shooter) {
        this.shooter = shooter;
        this.isSpeaker = true;
        // healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        // healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        isSpeaker = SmartDashboard.getBoolean("Target is Speaker?", true);
        shooter.setShooterSpeed(isSpeaker ? Modes.SPEAKER : Modes.AMP);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}
