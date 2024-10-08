package frc.team670.robot.commands.Intake;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.robot.subsystems.Intake;


public class ToggleDeployer extends InstantCommand implements MustangCommand {
    private Intake intake;
    private Map<MustangSubsystemBase, HealthState> healthReqs;

    public ToggleDeployer(Intake intake) {
        this.intake = intake;
        healthReqs = new HashMap<MustangSubsystemBase, HealthState>();
        healthReqs.put(intake, HealthState.GREEN);
    }

    @Override
    public void execute(){
        ConsoleLogger.consoleLog("Deployed Deployer");
        intake.toggleDeployer();
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthReqs;
    }
    
    
}
