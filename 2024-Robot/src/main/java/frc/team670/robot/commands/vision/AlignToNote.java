package frc.team670.robot.commands.vision;

import java.util.HashMap;
import java.util.Map;

import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonCamera;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public class AlignToNote extends Command implements MustangCommand {
    private DriveBase driveBase;
    private PhotonCamera colorCam;
    private double angleToTurn;

    protected Map<MustangSubsystemBase, HealthState> healthReqs;

    public AlignToNote(DriveBase driveBase){
        this.driveBase = driveBase;
        this.healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        this.healthReqs.put(driveBase, HealthState.GREEN);
        SmartDashboard.putNumber("Target Decimal",0);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }

    @Override
    public void initialize() {
        colorCam = driveBase.getPoseEstimator().getVision().getCameras()[2];
    }
    @Override
    public void execute(){
        angleToTurn = angleToNote();
        Logger.recordOutput("Vision/Theta Velocity", angleToTurn);
        driveBase.setmDesiredHeading(new Rotation2d(driveBase.getGyroscopeRotation().getRadians() + angleToTurn));    
    }

    public boolean isFinished(){
        if(Math.abs(angleToTurn) < 1 * Math.PI / 180){
            return true;
        }
        return false;
    }

    public void end(boolean isInteruppted){
        driveBase.setmDesiredHeading(null);
    }

  
    
    public double angleToNote(){
        try{
            var result = colorCam.getLatestResult();
            Logger.recordOutput("Vision/Note Present",result.hasTargets());
            if(result.hasTargets()){
                Logger.recordOutput("Vision/Yaw", result.getTargets().get(0).getYaw());
                return -result.getTargets().get(0).getYaw() * Math.PI / 180;
            }
            else
                return 0;
        }
        
        catch(java.lang.IllegalArgumentException e){
                    
        }
        return 0;
    }
}