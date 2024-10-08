package frc.team670.robot.commands.leds;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.LED.Mode;

public class LEDOff extends InstantCommand implements MustangCommand{

    private LED led;
    HashMap<MustangSubsystemBase, HealthState> healthreqs = new HashMap<MustangSubsystemBase, HealthState>();

    public LEDOff(LED led) {
        this.led = led;
        addRequirements(led);
        healthreqs.put(led, HealthState.GREEN);
    }

    public void initialize() {
        led.setLedMode(Mode.OFF);
    }


    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthreqs;
    }
    
}
