package frc.team670.robot.commands.auton;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Shooter;

public class SubwooferShot extends SequentialCommandGroup{
    public SubwooferShot(Shooter shooter, Indexer indexer){
        super(new AutonSubwoofer(shooter), new AutonSubwooferShot(shooter));
    }
}
