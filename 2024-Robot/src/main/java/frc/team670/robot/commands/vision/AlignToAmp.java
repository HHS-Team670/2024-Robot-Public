package frc.team670.robot.commands.vision;

import java.util.HashMap;
import java.util.Map;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.mustanglib.utils.SwervePoseEstimatorBase;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.drivebase.DriveBase;

/**
 * MoveToPose - moves to specified pose. Cancels when button is released.
 */
public class AlignToAmp extends Command implements MustangCommand {
    protected DriveBase driveBase;
    // private final Pose2d endPose;
    private PhotonCamera[] cameras;
    // private boolean backOut = false;
    protected Map<MustangSubsystemBase, HealthState> healthReqs;
    private MustangController mController;
    private Shooter shooter;
    private LED led;
    private int ampTag;

    public AlignToAmp(DriveBase driveBase,MustangController mController, Shooter shooter){
        led = LED.getInstance();
        this.driveBase = driveBase;
        this.mController=mController;
        this.shooter = shooter;
        this.healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        this.healthReqs.put(driveBase, HealthState.GREEN);

    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }

    @Override
    public void initialize() {
        ampTag = SwervePoseEstimatorBase.getAlliance() == Alliance.Red ? 5 : 6;
        cameras = driveBase.getPoseEstimator().getVision().getCameras();
    }

    @Override
    public void execute(){
        double result = angleToTarget();
        Logger.recordOutput("drivebase rotation", driveBase.getGyroscopeRotation());
        if(Math.abs(result) > 0.00001){
            driveBase.setmDesiredHeading(new Rotation2d(driveBase.getGyroscopeRotation().getRadians() + result));
        }
        else{
         }
    }

    public boolean isFinished(){
        return shooter.isShooting();
    }



    public boolean hasTarget(){
       int targets = 0;
       for (PhotonCamera c : cameras){
            var result = c.getLatestResult();
            targets += result.getTargets().size();
       }
       return targets > 0;
    }
    //
    public  double angleToTarget(){
        PhotonTrackedTarget bestTarget = null; 
        double angle = 0;
        try{
            for (PhotonCamera c : cameras){
                try{
                    var result = c.getLatestResult();
                    if(result.hasTargets()){
                        for (PhotonTrackedTarget target : result.getTargets()) {
                            if(target.getFiducialId() == ampTag){
                                bestTarget = target;
                        } 
                    }
                      if(bestTarget != null){
                        angle = (Math.PI/2-bestTarget.getYaw());
                     }
                }
                }
                catch(java.lang.IllegalArgumentException e){
                    
                }
            }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){

        }
       return angle;
    }

    public void end(boolean interrupted) {
        driveBase.setmDesiredHeading(null); 
          
        led.setLedMode(LED.Mode.READYTOSHOOT);
    }

    public double calcLatencyFactor(PhotonTrackedTarget bestTarget, double bestTargetLatency){
        double distanceX = bestTarget.getBestCameraToTarget().getX();
        return bestTargetLatency* driveBase.getChassisSpeeds().vyMetersPerSecond * (100/distanceX);
    }
    
}
