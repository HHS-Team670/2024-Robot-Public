// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.team670.robot.subsystems.drivebase;

import java.util.Optional;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonCamera;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import frc.team670.mustanglib.subsystems.drivebase.SwerveDrive;
import frc.team670.robot.constants.RobotConstants;
import frc.team670.robot.subsystems.Shooter;

public class DriveBase extends SwerveDrive {
    private static DriveBase mInstance;
    private boolean enableVisionAlignment;
    private PhotonCamera colorCam;
    private Shooter shooter;

    public static synchronized DriveBase getInstance() {

        mInstance = mInstance == null ? new DriveBase() : mInstance;
        return mInstance;
    }

    public DriveBase() {

        super(RobotConstants.DriveBase.kConfig); //Tried in constructor too, also not showing
        PPHolonomicDriveController.setRotationTargetOverride(this::getRotationTargetOverride);
        shooter = Shooter.getInstance();
    }

    public Optional<Rotation2d> getRotationTargetOverride(){
    // Some condition that should decide if we want to override rotation
        Logger.recordOutput("vision enabled", enableVisionAlignment);
        Logger.recordOutput("cameras null", colorCam == null);
        if(colorCam != null && !shooter.hasNote() && enableVisionAlignment) {
            // Return an optional containing the rotation override (this should be a field relative rotation)
            double angleToTurn = angleToNote();
            Logger.recordOutput("angle to note", Math.toDegrees(angleToTurn));
            if(angleToTurn != 0 ){
                return Optional.of(new Rotation2d(this.getGyroscopeRotation().getRadians() + angleToTurn));
            }else{
                return Optional.empty();
            }

        } else {
            // return an empty optional when we don't want to override the path's rotation
            return Optional.empty();
        }
    }

    public boolean hasTarget(){
        return colorCam.getLatestResult().getTargets().size() > 0;
    }
    

    public double angleToNote(){
        var result = colorCam.getLatestResult();
        Logger.recordOutput("Vision/Note Present",result.hasTargets());
        if(result.hasTargets()){
            var target = result.getTargets().get(0);
            Logger.recordOutput("Vision/Note Yaw", target.getYaw());
            return -result.getTargets().get(0).getYaw() * Math.PI / 180;
        }
        else
            return 0;
    }

    public void enableVisionAlignment(boolean enable){
        enableVisionAlignment = enable;
    }
    
    @Override
    public void mustangPeriodic() {
        super.mustangPeriodic(); //Tried here too, also not showing
        if(DriverStation.isAutonomousEnabled() && colorCam == null){
            colorCam = this.getPoseEstimator().getVision().getCameras()[2];
        }
        
    }

    @Override
    public HealthState checkHealth() {

        // for (SwerveModule curr : getModules()) {
        //     CANSparkMax motor = (CANSparkMax) curr.getDriveMotor();
        //     if (motor.getLastError() != REVLibError.kOk) {
        //         SmartDashboard.putString("Swerve Module " + motor.getDeviceId() + " ERROR:",
        //                 motor.getLastError().toString());
        //         return HealthState.RED;
        //     }
        // }
        return HealthState.GREEN;
    }

    @Override
    public void debugSubsystem() {
        super.debugSubsystem();
    }

    @Override
    protected void initPoseEstimator() {

        this.mPoseEstimator = new SwervePoseEstimator(this);
    }

}
