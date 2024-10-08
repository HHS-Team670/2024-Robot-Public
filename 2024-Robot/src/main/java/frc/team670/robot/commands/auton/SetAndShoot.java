package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;

public class SetAndShoot extends SequentialCommandGroup implements MustangCommand {
    Shooter shooter;
    Elevator elevator;
    double angle;

    public SetAndShoot(Shooter shooter, double angle, Elevator elevator){
        super(new AutonSetAngle(shooter, angle), new Shoot(shooter, elevator));
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
