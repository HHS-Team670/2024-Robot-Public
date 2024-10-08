package frc.team670.robot.commands;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Bars;

public class DeployBars extends InstantCommand implements MustangCommand{
    Bars bars;
    boolean deployed;
    public DeployBars(Bars bars, boolean deployed){
        this.bars = bars;
        this.deployed = deployed;
    }

    public void initialize(){
        if(deployed){
            bars.undeployBars();
        }else{
            bars.deployBars();
        }
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
    
}
