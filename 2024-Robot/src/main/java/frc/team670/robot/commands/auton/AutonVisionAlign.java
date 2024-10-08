package frc.team670.robot.commands.auton;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import frc.team670.robot.subsystems.drivebase.DriveBase;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.mustanglib.utils.SwervePoseEstimatorBase;
import frc.team670.robot.commands.vision.MoveToTargetVision;
import frc.team670.robot.subsystems.Shooter;

/**
 * MoveToPose - moves to specified pose. Cancels when button is released.
 */
public class AutonVisionAlign extends MoveToTargetVision {
    private Debouncer debouncer;
    private Timer timer;

    public AutonVisionAlign(DriveBase driveBase, Shooter shooter){
        super(driveBase, (MustangController)null, shooter);
        this.debouncer=new Debouncer(.3);
        timer = new Timer();
        
    }

    @Override
    public void initialize() {
        super.initialize();

        this.debouncer.calculate(false);
        timer.restart();
    
        
    }
    @Override
    public void execute(){
        super.execute();
        driveBase.drive(0, 0, 0);  
    }

    @Override
    public boolean isFinished(){
        Logger.recordOutput("Visionangle",super.angle);
        
        return (shooter.isAtTargetAngle() && this.debouncer.calculate(speakerDistance > 0 && Math.abs(angle) < Units.degreesToRadians(2)) || timer.hasElapsed(1)); // placeholder that must change
    }

    public void end(boolean isInteruppted){
        driveBase.setmDesiredHeading(null);
        driveBase.drive(0,0,0);
        ConsoleLogger.consoleLog("Shot note at " + shooter.getTilter().getCurrentAngleInDegrees() + " at a distance of "+ speakerDistance);
    }
}