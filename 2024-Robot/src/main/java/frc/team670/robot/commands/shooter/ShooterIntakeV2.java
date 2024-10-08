package frc.team670.robot.commands.shooter;

import java.util.Map;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Elevator.ElevatorState;
import frc.team670.robot.subsystems.LED.Mode;
import frc.team670.robot.subsystems.Shooter.Modes;

public class ShooterIntakeV2 extends Command implements MustangCommand {
    private Shooter shooter;
    private Elevator elevator;
    private LED led;

    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private boolean firstTrigger = false;
    private boolean forward = false;

    private Timer timer;

    public ShooterIntakeV2(Shooter shooter, Elevator elevator, LED led) {
        this.shooter = shooter;
        this.elevator = elevator;
        this.led = led;
        timer = new Timer();
        addRequirements(elevator);
    }

    public void initialize() {
        shooter.setShooterSpeed(Modes.INTAKE);
        shooter.runFeederReversed();
        shooter.setAngle(135);
        elevator.moveToTarget(ElevatorState.SOURCE);
        firstTrigger = false;
        forward = false;
    }

    public void execute() {
        
    }

    public boolean isFinished(){
        if (shooter.hasNote()) {
            if (!firstTrigger) {
                firstTrigger = true;
                timer.reset();
                shooter.setAngle(160);
            } else {
                if (forward || timer.hasElapsed(0.1)) {
                    led.setLedMode(Mode.HASNOTE);
                    return true;
                }
            }
        } else {
            if (firstTrigger) {
                forward = true;
                shooter.runFeeder();
            }
        }
        return false;

    }

    public void end(boolean interupted){

        shooter.stopFeeder();
        shooter.setShooterSpeed(Modes.SPEAKER);
        shooter.setAngle(135);
        elevator.moveToTarget(ElevatorState.INDEXER_RETRIEVAL);
        
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
