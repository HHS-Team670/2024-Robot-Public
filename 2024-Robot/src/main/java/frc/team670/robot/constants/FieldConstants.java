package frc.team670.robot.constants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * ORIGIN AT BOTTOM LEFT
 * https://firstfrc.blob.core.windows.net/frc2023/Manual/Sections/2023FRCGameManual-05.pdf
 */
public class FieldConstants {
    public static final double fieldLength = Units.inchesToMeters(651.25);
    public static final double fieldWidth = Units.inchesToMeters(315.5);
    public static final double tapeWidth = Units.inchesToMeters(2.0);
    public static DriverStation.Alliance alliance = null;

    public static DriverStation.Alliance getAlliance(){
        if(DriverStation.getAlliance().isPresent()){
                alliance = DriverStation.getAlliance().get();
        }
        return alliance;
    }

    /**
     * Flips a translation to the correct side of the field based on the current
     * alliance color.
     * By default, all translations and poses in {@link FieldConstants} are stored
     * with the
     * origin at the rightmost point on the BLUE ALLIANCE wall.
     */
    public static Translation2d allianceFlip(Translation2d translation) {
        if (getAlliance() == Alliance.Red) {
            return new Translation2d(fieldLength - translation.getX(),
                    translation.getY());
        } else {
            return translation;
        }
    }

    /**
     * Flips a pose FIELD ORIENTED
     */
    public static Pose2d allianceFlip(Pose2d pose) {
        if (getAlliance() == Alliance.Red) {
            return new Pose2d(fieldLength - pose.getX(), pose.getY(),
                    pose.getRotation().times(-1));
        } else {
            return pose;
        }
    }

    public static Pose2d allianceOrientedAllianceFlip(Pose2d pose) {
        if (getAlliance() == Alliance.Red) {
            return new Pose2d(fieldLength - pose.getX(), fieldWidth - pose.getY(),
                    pose.getRotation().times(-1));
        } else {
            return pose;
        }
    }

    public static Translation2d allianceOrientedAllianceFlip(Translation2d t) {
        if (getAlliance() == Alliance.Red) {
            return new Translation2d(fieldLength - t.getX(), fieldWidth - t.getY());
        } else {
            return t;
        }
    }

    public static Rotation2d getRobotFacingRotation() {
        return getAlliance() == Alliance.Red ? new Rotation2d()
                : new Rotation2d(Math.PI);
    }
}
