/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved. */
/* Open Source Software - may be modified and shared by FRC teams. The code */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project. */
/*----------------------------------------------------------------------------*/

package frc.team670.robot;




import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team670.robot.commands.Intake.StartIntake;
import frc.team670.robot.commands.auton.AlignAndShoot;
import frc.team670.robot.commands.auton.AutonVisionAlign;
import frc.team670.robot.commands.auton.SubwooferShot;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.commands.vision.AlignToNote;
import frc.team670.mustanglib.RobotContainerBase;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.swervelib.pathplanner.MustangPathPlannerAuto;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.robot.constants.OI;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Vision;
import frc.team670.robot.subsystems.Bars;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.drivebase.DriveBase;

import com.pathplanner.lib.auto.NamedCommands;
import com.reduxrobotics.canand.CanandEventLoop;
import frc.team670.robot.commands.auton.DelayedAuton;
import frc.team670.robot.commands.auton.EnableVisionAlignment;
import frc.team670.robot.commands.auton.ParallelStart;


/**
 * RobotContainer is where we put the high-level code for the robot. It contains
 * subsystems, OI
 * devices, etc, and has required methods (autonomousInit, periodic, etc)
 */

public class RobotContainer extends RobotContainerBase {
    private final DriveBase mDriveBase = DriveBase.getInstance();
    private final Intake mIntake = Intake.getInstance();
    private final Indexer mIndexer = Indexer.getInstance();
    private final Shooter mShooter = Shooter.getInstance();
    private final String kMatchStartedString = "match-started";
    private final String kAutonChooserString = "auton-chooser";
    private final Elevator mElevator = Elevator.getInstance();
    private final Vision mVision = Vision.getInstance();
    private final LED mLed = LED.getInstance();
    private final Bars mBars = Bars.getInstance();
    private boolean is87 = false;

    public RobotContainer() {
        super();
        addSubsystem(mDriveBase,mIndexer,mIntake, mIntake.getDeployer(), mShooter,mShooter.getTilter(),mVision, mLed, mElevator, mBars);
    
        OI.configureButtonBindings();

        for (MustangSubsystemBase subsystem : getSubsystems()) {
            subsystem.setDebugSubsystem(true);
        }

        // Auton

        mDriveBase.configureHolonomic();
        NamedCommands.registerCommand("RunShooter", new Shoot(mShooter, mElevator));
        NamedCommands.registerCommand("ShootNote", new Shoot(mShooter, mElevator));
        NamedCommands.registerCommand("RunIntake", new StartIntake(mIntake, mIndexer, mShooter, mElevator));
        NamedCommands.registerCommand("VisionAlign", new AutonVisionAlign(mDriveBase, mShooter));
        NamedCommands.registerCommand("VisionShoot", new AlignAndShoot(mShooter, mDriveBase, mIndexer));
        NamedCommands.registerCommand("AlignToNote", new AlignToNote(mDriveBase));
        NamedCommands.registerCommand("VisionOn", new EnableVisionAlignment(true, mDriveBase));
        NamedCommands.registerCommand("VisionOff", new EnableVisionAlignment(false, mDriveBase));
        NamedCommands.registerCommand("SubwooferShot", new SubwooferShot(mShooter, mIndexer));


    }

    

    @Override
    public void robotInit() {
        CanandEventLoop.getInstance();
        mDriveBase.initVision(mVision);
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *s
     * @return the command to run in autonomous
     */
    @Override
    public MustangCommand getAutonomousCommand() {
        int pathID=(int)(SmartDashboard.getNumber("auton-chooser",-1));
        int delayTime=(int)(SmartDashboard.getNumber("delayTime", 0));
        MustangPathPlannerAuto path = null;
        is87 = false;
        try {
            switch (pathID) {
            case 0:
                path = new MustangPathPlannerAuto("Mid321"); 
                break; 
            case 1:
                path = new MustangPathPlannerAuto("Mid3214");
                break;
            case 2:
                is87 = true;
                path = new MustangPathPlannerAuto("B87");
                break;
            case 3:
                path = new MustangPathPlannerAuto("Top456");
                break;
            default:
                return null;
            } 
        } catch (Exception e) {
            return null;
        }
        // return null;
        return new DelayedAuton(delayTime, new ParallelStart(path, mDriveBase, mIntake, mShooter, getDriverController()), mShooter, mElevator);
    }

    @Override
    public void autonomousInit() {
        mIntake.toggleDeployer();
    }

    @Override
    public void autonomousExit(){
        
        if(DriverStation.getAlliance().get() == Alliance.Red){
            if(is87){
                mDriveBase.rotateOffset(26.57+90+180);
            }
        }else{
            if(is87){
                mDriveBase.rotateOffset(-26.57-90);
            }
            else{
                mDriveBase.rotateOffset180();
            }
        }
    }

    @Override
    public void teleopInit() {
        mShooter.setAngle(135);
        mDriveBase.setmDesiredHeading(null);
        mIntake.setIntakeMode(Intake.Mode.OFF);
        mIndexer.setIndexerMode(Indexer.Mode.OFF);
        mShooter.stopFeeder();
        mIntake.toggleDeployer();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void disabled() {
        
        SmartDashboard.putBoolean(kMatchStartedString, false);
    }

    @Override
    public void disabledPeriodic() {
        int selectedPath = (int) SmartDashboard.getNumber(kAutonChooserString, 0);
    }

    @Override
    public void periodic() {

    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopPeriodic() {

    }

    public MustangController getOperatorController() {
        return OI.getOperatorController();
    }

    public MustangController getDriverController() {
        return OI.getDriverController();
    }

    public MustangController getBackupController() {
        return null;
    }

}
