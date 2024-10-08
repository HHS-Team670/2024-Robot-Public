// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.team670.robot.constants;

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pathplanner.lib.path.PathConstraints;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.SerialPort;
import frc.team670.mustanglib.RobotConstantsBase;
import frc.team670.mustanglib.subsystems.SparkMaxRotatingSubsystem;
import frc.team670.mustanglib.subsystems.VisionSubsystemBase;
import frc.team670.mustanglib.subsystems.VisionSubsystemBase.TagCountDeviation;
import frc.team670.mustanglib.subsystems.VisionSubsystemBase.UnitDeviationParams;
import frc.team670.mustanglib.subsystems.drivebase.SwerveDrive;
import frc.team670.mustanglib.swervelib.Mk4iSwerveModuleHelper.GearRatio;
import frc.team670.mustanglib.swervelib.redux.AbsoluteEncoderType;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig;
import frc.team670.mustanglib.utils.motorcontroller.MotorConfig.Motor_Type;
import frc.team670.mustanglib.swervelib.ModuleConfiguration;
import frc.team670.mustanglib.swervelib.SdsModuleConfigurations;
import frc.team670.robot.subsystems.Intake.Deployer.Config;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class RobotConstants extends RobotConstantsBase {
        /**
         * Set your team number using the WPILib extension's "Set Team Number" action.
         * 0) FACTORY RESET
         * ALL MOTOR CONTROLLERS 1) Set all of the *_ANGLE_OFFSET constants to
         * -Math.toRadians(0.0). 2)
         * Deploy the code to your robot. Power cycle. NOTE: The robot isn't drivable
         * quite yet, we
         * still have to setup the module offsets 3) Turn the robot on its side and
         * align all the wheels
         * so they are facing in the forwards direction. NOTE: The wheels will be
         * pointed forwards (not
         * backwards) when modules are turned so the large bevel gears are towards the
         * LEFT side of the
         * robot. When aligning the wheels they must be as straight as possible. It is
         * recommended to
         * use a long straight edge such as a piece of 2x1 in order to make the wheels
         * straight. 4)
         * Record the angles of each module using the angle put onto Shuffleboard. The
         * values are named
         * Front Left Module Angle, Front Right Module Angle, etc. 5) Set the values of
         * the
         * *_ANGLE_OFFSET to -Math.toRadians(<the angle you recorded>) NOTE: All angles
         * must be in
         * degrees. 6) Re-deploy and power cycle and try to drive the robot forwards.
         * All the wheels
         * should stay parallel to each other. If not go back to step 3. 7) Make sure
         * all the wheels are
         * spinning in the correct direction. If not, add 180 degrees to the offset of
         * each wheel that
         * is spinning in the incorrect direction. i.e -Math.toRadians(<angle> + 180.0).
         */

    public static final String kCompAddress = "00:80:2F:34:0B:07";
    public static final String kAlphaAddress = "00:80:2F:33:D0:46";
    public static final String kBench = "00:80:2F:22:B4:F6";
    public static final String kRobotAddress = getMACAddress();
    
    private static  Map<String, Double> robotSpecificConstants = Map.ofEntries(
        entry(kCompAddress, Map.ofEntries(
                    entry("kBackRightModuleSteerOffsetRadians", -Math.toRadians(0)),
                    entry("kBackLeftModuleSteerOffsetRadians", -Math.toRadians(0)),
                    entry("kFrontRightModuleSteerOffsetRadians", -Math.toRadians(0)),
                    entry("kFrontLeftModuleSteerOffsetRadians", -Math.toRadians(0)),
                    entry("kSwerveModuleConfig", 2.0),
                    entry("kDriveMotorType",-1.0))),
            entry(kAlphaAddress,
                    Map.ofEntries(
                            entry("kBackRightModuleSteerOffsetRadians", -Math.toRadians(0)),
                            entry("kBackLeftModuleSteerOffsetRadians", -Math.toRadians(0)),
                            entry("kFrontRightModuleSteerOffsetRadians", -Math.toRadians(0)),
                            entry("kFrontLeftModuleSteerOffsetRadians", -Math.toRadians(0)),
                            entry("kSwerveModuleConfig", 2.0), //
                            entry("kDriveMotorType",1.0))))
            .get(kRobotAddress);

        public static final class DriveBase extends SwerveDriveBase {

                public static final double kWidth = Units.inchesToMeters(36);
                public static double kClearance = Math.hypot(kWidth, kWidth) / 2 + 0.05;
                public static final double kTrackWidthMeters = 0.6096;
                public static final double kWheelBaseMeters = 0.6096;

                public final static SerialPort.Port kNAVXPort = SerialPort.Port.kMXP;

                public static final double kMaxSpeedMetersPerSecond = 1;// 1. Not constant between auton path, 2. Robot
                                                                        // specific
                public static final double kMaxAccelerationMetersPerSecondSquared = 1;

                public static final PathConstraints kAutoPathConstraints = new PathConstraints(
                                kMaxSpeedMetersPerSecond, kMaxAccelerationMetersPerSecondSquared,
                                kMaxAngularSpeedRadiansPerSecond, kMaxAngularAccelerationRadiansPerSecondSquared);

                public static final Motor_Type kDriveMotorType = robotSpecificConstants.get("kDriveMotorType")>0?Motor_Type.NEO:Motor_Type.KRAKEN_X60;
                public static final Motor_Type kSteerMotorType = Motor_Type.NEO;
                
                public static final ModuleConfiguration kModuleConfig = robotSpecificConstants
                                .get("kDriveMotorType") == 1.0
                                                ? SdsModuleConfigurations.MK4I_L2
                                                : SdsModuleConfigurations.MK4I_L2K;

                public static final GearRatio kSwerveModuleGearRatio = robotSpecificConstants
                                .get("kDriveMotorType") == 1.0
                                                ? GearRatio.L2
                                                : GearRatio.L2K;
                public static final double kFrontLeftModuleSteerOffsetRadians = robotSpecificConstants
                                .get("kFrontLeftModuleSteerOffsetRadians");

                public static final double kFrontRightModuleSteerOffsetRadians = robotSpecificConstants
                                .get("kFrontRightModuleSteerOffsetRadians");

                public static final double kBackLeftModuleSteerOffsetRadians = robotSpecificConstants
                                .get("kBackLeftModuleSteerOffsetRadians");

                public static final double kBackRightModuleSteerOffsetRadians = robotSpecificConstants
                                .get("kBackRightModuleSteerOffsetRadians");

                public static final double kHeadingOffsetRadians = 0;

                public static final AbsoluteEncoderType kFrontLeftModuleEncoderType = AbsoluteEncoderType.HELIUM_CANCODER;
                
                public static final AbsoluteEncoderType kFrontRightModuleEncoderType = AbsoluteEncoderType.HELIUM_CANCODER;

                public static final AbsoluteEncoderType kBackLeftModuleEncoderType = AbsoluteEncoderType.HELIUM_CANCODER;

                public static final AbsoluteEncoderType kBackRightModuleEncoderType = AbsoluteEncoderType.HELIUM_CANCODER;
                
                public static final double kMaxVelocityMetersPerSecond = 3000.0 / 60.0
                                * kModuleConfig.getDriveReduction() * kModuleConfig.getWheelDiameter() * Math.PI;

                public static final double kMaxAngularVelocityRadiansPerSecond = kMaxVelocityMetersPerSecond
                                / Math.hypot(kTrackWidthMeters / 2.0, kWheelBaseMeters / 2.0);


                public static final SwerveDrive.Config kConfig = new SwerveDrive.Config(kTrackWidthMeters,
                        kWheelBaseMeters, kMaxVelocityMetersPerSecond,kMaxAngularVelocityRadiansPerSecond, kMaxVoltage, kMaxDriveCurrent,
                        kMaxSteerCurrent, kNAVXPort, kSwerveModuleGearRatio, kDriveMotorType,kSteerMotorType,kHeadingOffsetRadians, 
                        kFrontLeftModuleDriveMotorID, kFrontLeftModuleSteerMotorID, kFrontLeftModuleSteerEncoderID,
                        kFrontLeftModuleSteerOffsetRadians, kFrontLeftModuleEncoderType, 
                        kFrontRightModuleDriveMotorID,
                        kFrontRightModuleSteerMotorID, kFrontRightModuleSteerEncoderID,
                        kFrontRightModuleSteerOffsetRadians, kFrontRightModuleEncoderType, 
                        kBackLeftModuleDriveMotorID,
                        kBackLeftModuleSteerMotorID, kBackLeftModuleSteerEncoderID,
                        kBackLeftModuleSteerOffsetRadians, kBackLeftModuleEncoderType,
                        kBackRightModuleDriveMotorID,
                        kBackRightModuleSteerMotorID, kBackRightModuleSteerEncoderID,
                        kBackRightModuleSteerOffsetRadians, kBackRightModuleEncoderType);

    }

    public class Bars{ 
        public static final double kBarsRollSpeedDown = .1; 
        public static final double kBarsRollSpeedUp= .5;
        public static final int kBarsRollerID = 17; 
    }
    public class Intake{

        public static final int kIntakeRollerID = 6;  
        public static final double kIntakeRollSpeed = .6; 
        public static final double INTAKE_PEAK_CURRENT = 50;

        public class Deployer{

                public static final int kContinuousCurrent = 10;
                public static final int kPeakCurrent = 32;
                public static final MotorConfig.Motor_Type kMotorType = MotorConfig.Motor_Type.NEO;
                public static final int kMotorID = 12; 
                public static final Config kConfig = new Config(kMotorID, kMotorType, kContinuousCurrent, kPeakCurrent);
                public static final double kTimeDown = 0.39; 
                public static final double kUpMotorSpeed = 0.5; 
                public static final double kDownMotorSpeed=0.4;
                public static final double kTimeUp = 0.35; 
        }

    }

     public static final class Indexer{
        public static final int kBeamBreakPort = 5;
        public static final int kMotorID = 61;
        public static final double kIntakeSpeed = .9;
        public static final double kPassSpeed = 0.3;
        public static final double INDEXER_PEAK_CURRENT = 60;
     }


      public static final class Led {
                public static final int kPort = 0;
                public static final int kStartIndex = 0;
                public static final int kEndindex = 31;

        }

        // shooter controller now 2:1, so shouldnt run at max rpm

        public static final class Shooter {
                public static final IdleMode kIdleMode = IdleMode.kBrake;
                public static final int kVelocityShooterID = 60;
                public static final int kVelocitySlot = 0;

                // TUNE THE FOLLOWING CONSTANTS
                public static final double kP = 0.0003;
                public static final double kI = 0;
                public static final double kD = 0.00015;
                public static final double kFF = 0.000176;
                public static final double kIz = 0;
                public static final double kTimeUntilStop = 5.0;

                public static final double kFeederShootPercentage = 1.0;
                public static final double kFeederIntakePercentage = .5;

                public static final int kBeamBreakID = 9;

                public static final int kFeederMotorID = 58;
                public static final int kDirectionMotorID = 62; 

                public static final double kAmpSpeed = 3480 / 3;
                public static final double kAllowedSpeedError = 100;
                public static final double MaxRPM = 4500;


                public static final class Tilter {
                        public static final int kAbsoluteEncoderID = 7;
                        public static final int kTilterMotorID = 59;

                        public static final double kP = 0.000016;
                        public static final double kI = 0;
                        public static final double kD = 0;
                        public static final double kFF = 0.0002;
                        public static final double kIz = 0;
                        public static final double kAbsoluteEncoderOffset = 0.757 - 0.5+8./360; //zeroed upward 

                        public static final double kGearRatio = 52.727; 

                        public static final MotorConfig.Motor_Type kMotorType = MotorConfig.Motor_Type.NEO;
                        public static final double kMaxOutput = 1;
                        public static final double kMinOutput = -1;
                        public static final int kContinuousCurrent = 20;
                        public static final int kPeakCurrent = 40;

                        public static final double kMaxRotatorRPM = 2320;
                        public static final double kMinRotatorRPM = 0; 
                        public static final double kMaxAcceleration = kMaxRotatorRPM*2;


                        public static final double kAllowedErrorDegrees = 0.8;
                        public static final double kAllowedErrorRotations = kGearRatio * kAllowedErrorDegrees / 360;

                        public static final double kMaxAngle = 181.0;
                        public static final double kMinAngle = 45.0;

                        public static final float[] kSoftLimits = {(float)(kMaxAngle/360*kGearRatio),(float)(kMinAngle/360 * kGearRatio)};

                        public static final SparkMaxRotatingSubsystem.Config kConfig = new SparkMaxRotatingSubsystem.Config(
                                        kTilterMotorID, 0, kMotorType, kIdleMode,
                                        kGearRatio, kP, kI, kD, kFF, kIz, kMaxOutput, kMinOutput,
                                        kMaxRotatorRPM, kMinRotatorRPM, kMaxAcceleration, kAllowedErrorDegrees,
                                        kSoftLimits, kContinuousCurrent, kPeakCurrent);
                        
                }
        }
        public static final class Vision {
        public static final double kCameraOffset = Units.inchesToMeters(13.75);
        public static final String[] kVisionCameraIDs = {"Arducam_B", "Arducam_C","Microsoft_LifeCam_HD-3000"};
        public static final double kCameraDistanceOffsetCM = .36;
        public static final Transform3d[] kCameraOffsets = {
                // Cam B - RIGHT
                new Transform3d(
                        new Translation3d(Units.inchesToMeters(0), DriveBase.kWidth/2,
                                Units.inchesToMeters(12)),
                        new Rotation3d(0, Units.degreesToRadians(20), Units.degreesToRadians(0))), 
                // Cam C - LEFT
                new Transform3d(
                        new Translation3d(Units.inchesToMeters(0), -DriveBase.kWidth/2,
                                Units.inchesToMeters(12)),
                        new Rotation3d(0, Units.degreesToRadians(20), Units.degreesToRadians(180)))};

        public static final double kLockedOnErrorX = 0.3;
        public static final double xLockedOnErrorY = 0.3;
        public static final double kLockedOnErrorDegrees = 10;

        public static final double kPoseAmbiguityCutOff = 0.05;
        public static final List<Set<Integer>> kPossibleFrameFIDCombos = List.of(Set.of(1, 2, 3, 4),
                Set.of(5, 6, 7, 8));

        public static final int kMaxFrameFIDs = 4;
        public static final Map<Integer, TagCountDeviation> kVisionStdFromTagsSeen = Map.ofEntries(
            Map.entry(1, new TagCountDeviation(
                new UnitDeviationParams(.25, .4, .25),
                new UnitDeviationParams(.35, .5, .4),
                new UnitDeviationParams(.5, .7, 1.5))),
            Map.entry(2, new TagCountDeviation(
                new UnitDeviationParams(.35, .1, .3), new UnitDeviationParams(.5, .7, 1.5))),
            Map.entry(3, new TagCountDeviation(
                new UnitDeviationParams(.25, .07, .25), new UnitDeviationParams(.15, 1, 1.5)))
        );
        public static final VisionSubsystemBase.Config kConfig = new VisionSubsystemBase.Config(AprilTagFields.k2024Crescendo.loadAprilTagLayoutField(), kVisionCameraIDs, kCameraOffsets, kPoseAmbiguityCutOff, kMaxFrameFIDs, kPossibleFrameFIDCombos, kVisionStdFromTagsSeen);
    }
        public static final class Elevator
        {
    
            public static final double GEAR_RATIO = 25; 
            public static final int MOTOR_1_ID = 11; //leader 
            public static final int MOTOR_2_ID = 10; //follower 
            public static final int SMARTMOTION_SLOT = 0;
            public static final double ELEVATOR_ALLOWED_ERROR_IN_METERS = 0.01;
            public static final double P = 0.00002;
            public static final double I = 0;
            public static final double D = 0;
            public static final double FF = 0.00017618;
            public static final double IZ = 0;
            public static final double MIN_OUTPUT = -1;
            public static final double MAX_OUTPUT = 1;
            private static final double ELEVATOR_SPROCKET_RADIUS = 0.04462018; // METERS
            public static final double CIRCUMFERENCE_SPROCKET = 2*Math.PI*ELEVATOR_SPROCKET_RADIUS; // .28036
            private static final double MAX_EXTENSION = Units.inchesToMeters(26); // METERS
            public static final double MIN_ROTATOR_RPM = 0;
            public static final double MAX_ROTATOR_RPM = 4500; 
            public static final int SLOT = 0;
            public static final int PEAK_CURRENT = 60;
    
            // lower soft limit and upper soft limit were flipped because the motors were inverted
            public static final float UPPER_SOFT_LIMIT = (float) 0; // IN MOTOR ROTATIONS, 7 CM FROM BOTTOM. 7.83 rotations
            public static final float LOWER_SOFT_LIMIT = (float) ((((MAX_EXTENSION-0.01 )/ CIRCUMFERENCE_SPROCKET) * GEAR_RATIO)); // IN MOTOR ROTATIONS, 7 CM FROM TOP, 23.32 rotations
            public static final float[] SOFT_LIMITS= {UPPER_SOFT_LIMIT, LOWER_SOFT_LIMIT};
            public static final double MAX_HEIGHT = MAX_EXTENSION - 0.01;

            public static final double MAX_ACCELERATION = MAX_ROTATOR_RPM*2; 
    }
}
