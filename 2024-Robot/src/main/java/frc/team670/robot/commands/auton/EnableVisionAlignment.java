package frc.team670.robot.commands.auton;

import java.util.Map;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public class EnableVisionAlignment extends InstantCommand implements MustangCommand{
    private boolean enable;
    private DriveBase driveBase;

    public EnableVisionAlignment(boolean enable, DriveBase driveBase){
        this.enable = enable;
        this.driveBase = driveBase;
    }

    public void initialize(){
        driveBase.enableVisionAlignment(enable);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }
}
