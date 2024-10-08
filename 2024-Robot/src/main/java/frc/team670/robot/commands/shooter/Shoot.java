package frc.team670.robot.commands.shooter;

import java.util.HashMap;
import java.util.Map;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.robot.commands.vision.MoveToTargetVision;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;


public class Shoot extends Command implements MustangCommand {

    private Shooter shooter;
    private Elevator elevator;
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private boolean hasShot = false;
    private Timer timer, ampTimer;
    private double distance;

    public Shoot(Shooter shooter, Elevator elevator) {
        this.shooter = shooter;
        this.elevator = elevator;
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
        timer = new Timer();
        ampTimer = new Timer();
    }

    @Override
    public void initialize(){
        distance = MoveToTargetVision.speakerDistance;
        ConsoleLogger.consoleLog("Shooting");
        this.hasShot = false;
        timer.restart();
        ampTimer.restart();
    }
    
    @Override
    public void execute() {
        if(shooter.readyToShoot()){
            Logger.recordOutput("Shooter/has shot note", true);
            shooter.shoot();
            this.hasShot = true;

            if(elevator.state == Elevator.ElevatorState.AMP && ampTimer.hasElapsed(0.3)){
                shooter.getTilter().setSystemTargetAngleInDegrees(115);
                elevator.moveToTarget(Elevator.ElevatorState.INDEXER_RETRIEVAL);
            }

        }else{
            Logger.recordOutput("Shooter/has shot note", false);
        }
    }

    @Override
    public boolean isFinished() {
        if((hasShot && !shooter.isShooting()) || timer.hasElapsed(.2)){
            return true;
        }
        return false;
    }

    @Override
    public void end(boolean isInteruppted){
        
        ConsoleLogger.consoleLog("Shot note at " + shooter.getTilter().getCurrentAngleInDegrees() + " at a distance of "+ distance);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}