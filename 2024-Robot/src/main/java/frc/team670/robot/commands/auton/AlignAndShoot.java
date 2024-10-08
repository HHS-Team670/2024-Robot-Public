package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Vision;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public class AlignAndShoot extends SequentialCommandGroup implements MustangCommand {
    Shooter shooter;
    double angle;

    public AlignAndShoot(Shooter shooter, DriveBase db, Indexer indexer){
        super(new AutonVisionAlign(db, shooter), new AutonShoot(shooter, indexer));
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
