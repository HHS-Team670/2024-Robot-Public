package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.swervelib.pathplanner.MustangPathPlannerAuto;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.robot.commands.Intake.ToggleDeployer;
import frc.team670.robot.commands.shooter.SetSpeed;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public class ParallelStart extends ParallelCommandGroup implements MustangCommand{

    public ParallelStart(MustangPathPlannerAuto auto, DriveBase driveBase, Intake intake, Shooter shooter, MustangController mController){
        super(new SetSpeed(shooter), auto);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
