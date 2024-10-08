package frc.team670.robot.subsystems.drivebase;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import frc.team670.mustanglib.subsystems.drivebase.SwerveDrive;
import frc.team670.mustanglib.utils.SwervePoseEstimatorBase;

public class SwervePoseEstimator extends SwervePoseEstimatorBase {

    public SwervePoseEstimator(SwerveDrive swerve) {
        super(swerve);
    }
 
    @Override
    protected Pose2d getAbsoluteFieldOrientedPoseFromAllianceOriented(Pose2d pose) {
        return pose;
    }

    @Override
    protected List<Pose2d> getTargets() {
        List<Pose2d> targets = new ArrayList<>();
        return targets;
    }
    
}
