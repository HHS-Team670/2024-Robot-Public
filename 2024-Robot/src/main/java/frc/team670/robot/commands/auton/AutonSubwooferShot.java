package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Indexer.Mode;

public class AutonSubwooferShot extends Command implements MustangCommand{

    Shooter shooter;
    public AutonSubwooferShot(Shooter shooter){
        this.shooter = shooter;
    }

    @Override
    public void initialize(){
        shooter.shoot();
    }

    @Override
    public boolean isFinished(){
        return !shooter.hasNote();
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
}
    
}
