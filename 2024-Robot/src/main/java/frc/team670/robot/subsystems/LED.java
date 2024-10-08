package frc.team670.robot.subsystems;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team670.mustanglib.subsystems.LEDSubsystem;
import frc.team670.robot.constants.RobotConstants;

public class LED extends LEDSubsystem {

    public enum Mode {
        OFF, AMP, COOPERTITION, INTAKING, HASNOTE, VISIONON, READYTOSHOOT;
    }
   
    private static LED mInstance;
    private LEDColor allianceColor;
    private int previousPathID;

    // The commented out `getInstance()` method is a common design pattern called the Singleton
    // pattern. It ensures that only one instance of the `LED` class is created and provides a way to
    // access that instance globally.
    public static synchronized LED getInstance() {
        mInstance = mInstance == null ? new LED() : mInstance;
        return mInstance;
    }

    public LED() {
        super(RobotConstants.Led.kPort, RobotConstants.Led.kStartIndex, RobotConstants.Led.kEndindex);
        previousPathID = -1;
    }

    public void setLedMode(LED.Mode status) {
        Logger.recordOutput("LED/LedStatus", "" + status.name());
         
        switch (status){
            case AMP:
                solidhsv(LEDColor.SEXY_PURPLE);
                break;
            case VISIONON:
                solidhsv(LEDColor.GREEN);
                break;
            case COOPERTITION:
                solidhsv(LEDColor.YELLOW);
                break;
            case INTAKING:
                solidhsv(LEDColor.RED);
                break;
            case HASNOTE:
                solidhsv(LEDColor.WHITE);
                break;
            case READYTOSHOOT:
                solidhsv(LEDColor.BLUE);
                break;
        } 
    }
    

    public void setAllianceColors(LEDColor alliance) {
        this.allianceColor = alliance;
    }

    public LEDColor getAllianceColor() {
        return allianceColor;
    }

    public void updateAutonPathColor(int pathID) {
        
        if (pathID != previousPathID) {
            previousPathID = pathID;
            switch (pathID) {
                case 0:
                    solidhsv(LEDColor.RED);
                    break;
                case 1:
                    solidhsv(LEDColor.BLUE);
                    break;
                case 2:
                    solidhsv(LEDColor.GREEN);
                    break;
                case 3:
                    solidhsv(LEDColor.SEXY_YELLOW);
                    break;
                default:
                    solidhsv(LEDColor.SPOOKY_ORANGE);
            }
        } 
    }

    @Override
    public void mustangPeriodic() {

        super.mustangPeriodic();

        if (DriverStation.isDisabled()) {

            updateAutonPathColor((int)(SmartDashboard.getNumber("auton-chooser",-1))); 

        } 

    }
}