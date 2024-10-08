package frc.team670.robot.commands.shooter;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Shooter;

public class SetAngle extends InstantCommand implements MustangCommand {
    
    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    

    //This command is used to manually set the speed using smart dashboard
    public SetAngle(Shooter shooter) {
        this.shooter = shooter;
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        double angle = SmartDashboard.getNumber("Shooter Target Angle", 0);
        shooter.setAngle(angle);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}
