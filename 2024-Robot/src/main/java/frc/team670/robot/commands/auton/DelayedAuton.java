package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;

public class DelayedAuton extends SequentialCommandGroup implements MustangCommand{

    public DelayedAuton(double seconds, ParallelCommandGroup auton, Shooter shooter, Elevator elevator) {
        super(new WaitCommand(seconds), auton, new Shoot(shooter, elevator));
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }

    
}
