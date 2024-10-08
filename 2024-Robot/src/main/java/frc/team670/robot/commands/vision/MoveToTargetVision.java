package frc.team670.robot.commands.vision;

import java.util.HashMap;
import java.util.Map;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.mustanglib.utils.SwervePoseEstimatorBase;
import frc.team670.robot.constants.RobotConstants;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.drivebase.DriveBase;
import frc.team670.robot.subsystems.Shooter.Modes;

/**
 * MoveToPose - moves to specified pose. Cancels when button is released.
 */
public class MoveToTargetVision extends Command implements MustangCommand {
    protected DriveBase driveBase;
    private PhotonCamera[] cameras;
    protected Map<MustangSubsystemBase, HealthState> healthReqs;
    private MustangController mController;
    protected Shooter shooter;
    private LED led;
    private final static double kSensitivity=0.5;
    private double prevTimestamp = Double.MIN_VALUE;
    private double currentTimestamp = Double.MIN_VALUE;
    private final String check = "A Controller Axis";
    public static final InterpolatingDoubleTreeMap shooterAngles = new InterpolatingDoubleTreeMap();
    private final String SHOOTER_ANGLE_DISTANCE = "Shooter" + "/Distance";
    protected double angle = 0;
    protected boolean foundTarget;
    public static double speakerDistance = 0;

    private int shooterTag;

    public MoveToTargetVision(DriveBase driveBase,MustangController mController, Shooter shooter){
        this.shooter = shooter;
        led = LED.getInstance();
        this.driveBase = driveBase;
        this.mController=mController;
        this.healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        this.healthReqs.put(driveBase, HealthState.GREEN);

        shooterAngles.put(1.2744, 154.); //3 feet
        shooterAngles.put(1.5792, 148.); //4 feet
        shooterAngles.put(1.884, 145.); //5 feet
        shooterAngles.put(2.1888, 142.); //6 feet
        shooterAngles.put(2.4966, 136.); //7 feet
        shooterAngles.put(2.794, 133.); //8 feet
        shooterAngles.put(3.1032, 130.); //9 feet
        shooterAngles.put(3.408, 127.5); //10 feet
        shooterAngles.put(3.4511, 126.3); //shoot note 3 specific angle
        shooterAngles.put(3.7128, 125.5); //11 feet
        shooterAngles.put(4.036, 123.); //12 feet
        shooterAngles.put(4.689, 117.);
        shooterAngles.put(5.481, 116.5);
        shooterAngles.put(5.922, 116.);
        shooterAngles.put(6.755, 113.);

    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }

    @Override
    public void initialize() {
        shooterTag = SwervePoseEstimatorBase.getAlliance() == Alliance.Red ? 4 : 7;
        cameras = driveBase.getPoseEstimator().getVision().getCameras();
        shooter.setShooterSpeed(Modes.SPEAKER);
        speakerDistance = 0;
        foundTarget = false;
    }

    @Override
    public void execute(){
        double[] results = angleToTarget();
        this.angle = results[0];
        Logger.recordOutput("angle", angle*(180./Math.PI));
        speakerDistance = results[1];
        SmartDashboard.putNumber("Vision Speaker Distance", results[1]);
        Logger.recordOutput("drivebase rotation", driveBase.getGyroscopeRotation());
        if(Math.abs(angle) > 0.00001){
            //if(shooter.hasNote())
            shooter.setAngle(shooterAngles.get(results[1]));
            driveBase.setmDesiredHeading(new Rotation2d(driveBase.getGyroscopeRotation().getRadians() + angle));
            foundTarget = true;
        }
        else{
         }

        Logger.recordOutput(SHOOTER_ANGLE_DISTANCE, results[1]);
        Logger.recordOutput("vision has found target", foundTarget);
    }

    public boolean isFinished(){
        return mController.getRightTriggerAxis() > 0.1 || shooter.isShooting();
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
    public  double[] angleToTarget(){
        double distance = 0;
        double bestDistance = 1000;
        PhotonTrackedTarget bestTarget = null; 
        double bestTargetLatency = 0;
        try{
            for (PhotonCamera c : cameras){
                try{
                    var result = c.getLatestResult();
                    if(result.hasTargets()){
                        for (PhotonTrackedTarget target : result.getTargets()) {
                            if(target.getFiducialId() == shooterTag){
                                var transform = target.getBestCameraToTarget();
                                distance = Math.sqrt(Math.pow(transform.getX(), 2) + Math.pow(transform.getY(), 2));

                                if(distance < bestDistance) {
                                    bestTarget = target;
                                    bestDistance = distance;
                                    bestTargetLatency = result.getLatencyMillis()/1000;
                                    Logger.recordOutput("bestDistance changed", bestDistance);
                                }
                            } 
                        }

                        if(bestTarget != null){
                            double factor = calcLatencyFactor(bestTarget, bestTargetLatency);
                            // SmartDashboard.putNumber("A Latency Factor", factor);
                            //offsets the distance of the camera from the center
                            double offset =  Math.atan2(-RobotConstants.Vision.kCameraOffset, bestDistance);
                            double angle = (-Math.toRadians(bestTarget.getYaw())) + offset;
                            Logger.recordOutput("target yaw", bestTarget.getYaw());
                            Logger.recordOutput("offset",  offset);
                            Logger.recordOutput("bestDistance", bestDistance);
                            return new double[] {angle, bestDistance};
                    }

                }

                }
                catch(java.lang.IllegalArgumentException e){
                    
                }
            }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){

        }
        
       return new double[] {0,0};
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
