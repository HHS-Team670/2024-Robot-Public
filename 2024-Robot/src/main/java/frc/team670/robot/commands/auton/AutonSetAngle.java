package frc.team670.robot.commands.auton;

import java.util.HashMap;
import java.util.Map;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.robot.subsystems.Shooter;

public class AutonSetAngle extends Command implements MustangCommand {
    
    private Shooter shooter;
    private Map<MustangSubsystemBase, HealthState> healthReqs;
    private double angle;
    

    //This command is used to manually set the speed using smart dashboard
    public AutonSetAngle(Shooter shooter, double angle) {
        this.shooter = shooter;
        this.angle = angle;
        // addRequirements(shooter);
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(shooter, HealthState.GREEN);
    }

    @Override
    public void initialize() {
        // double angle = SmartDashboard.getNumber("Shooter Target Angle", 0);
        boolean setAngle = shooter.setAngle(angle);
        Logger.recordOutput("Shooter /Set Angle", setAngle);
    }

    @Override
    public boolean isFinished() {
        if(shooter.isAtTargetAngle()){
            ConsoleLogger.consoleLog("Has reached target angle");
            return true;
        }
        return false;
    }
    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
}
