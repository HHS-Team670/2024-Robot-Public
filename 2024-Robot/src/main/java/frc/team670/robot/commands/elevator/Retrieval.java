package frc.team670.robot.commands.elevator;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Elevator.ElevatorState;

public class Retrieval extends InstantCommand implements MustangCommand {
    Elevator elevator;
    Shooter shooter;
    ElevatorState state;

    public Retrieval(Elevator elevator, ElevatorState state, Shooter shooter){
        this.elevator = elevator;
        this.shooter = shooter;
        this.state = state;
        addRequirements(elevator);
    }

    public void initialize(){
        elevator.moveToTarget(state);
        shooter.setAngle(110);
    } 
    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
