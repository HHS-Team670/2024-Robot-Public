package frc.team670.robot.commands.shooter;

import java.util.Map;
import java.util.HashMap;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.SwervePoseEstimatorBase;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Elevator.ElevatorState;
import frc.team670.robot.subsystems.Shooter.Modes;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public class Podium extends InstantCommand implements MustangCommand {
    private Shooter shooter;
    private Elevator elevator;
    private DriveBase driveBase;
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private double angle;

    public Podium(Shooter shooter, DriveBase driveBase, Elevator elevator) {
        this.shooter = shooter;
        this.driveBase = driveBase;
        this.elevator = elevator;
        addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
    }

    //negative red, positive blue
    @Override
    public void initialize() {
        angle = SwervePoseEstimatorBase.getAlliance() == Alliance.Red ? -8 : 8;
        shooter.setShooterSpeed(Modes.SPEAKER);
        shooter.setAngle(114); 
        driveBase.setmDesiredHeading(new Rotation2d(driveBase.getGyroscopeRotation().getRadians() + Math.toRadians(angle)));
        elevator.moveToTarget(ElevatorState.PODIUM);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}