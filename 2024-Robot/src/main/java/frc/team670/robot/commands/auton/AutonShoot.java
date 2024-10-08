package frc.team670.robot.commands.auton;

import java.util.Map;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Indexer.Mode;

public class AutonShoot extends Command implements MustangCommand{

    Indexer indexer;
    Shooter shooter;
    Timer timer;
    boolean hasNote;
    public AutonShoot(Shooter shooter, Indexer indexer){
        this.shooter = shooter;
        this.indexer = indexer;
        timer = new Timer();
    }

    @Override
    public void initialize(){
        indexer.setIndexerMode(Mode.INTAKING);
        shooter.shoot();
        timer.restart();
        hasNote = shooter.hasNote();
    }

    @Override
    public boolean isFinished(){
        return timer.hasElapsed(1) || (hasNote && !shooter.hasNote());
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
}
    
}
