package frc.team670.robot.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.team670.mustanglib.commands.drive.teleop.swerve.SetSwerveForwardDirection;
import frc.team670.mustanglib.commands.drive.teleop.swerve.XboxSwerveDrive;
import frc.team670.mustanglib.utils.MustangController;
import frc.team670.mustanglib.utils.MustangController.XboxButtons;
import frc.team670.robot.commands.Climb;
import frc.team670.robot.commands.DeployBars;
import frc.team670.robot.commands.Intake.EjectIntake;
import frc.team670.robot.commands.Intake.StartIntake;
import frc.team670.robot.commands.Intake.StopIntake;
import frc.team670.robot.commands.Intake.ToggleDeployer;
import frc.team670.robot.commands.auton.AutonSetAngle;
import frc.team670.robot.commands.elevator.ManuallyMoveElevator;
import frc.team670.robot.commands.elevator.Retrieval;
import frc.team670.robot.commands.elevator.ZeroElevator;
import frc.team670.robot.subsystems.Indexer;
import frc.team670.robot.subsystems.Intake;
import frc.team670.robot.subsystems.LED;
import frc.team670.robot.subsystems.Bars;
import frc.team670.robot.commands.shooter.Amp;
import frc.team670.robot.commands.shooter.ManuallyMoveShooter;
import frc.team670.robot.commands.shooter.SetAngle;
import frc.team670.robot.commands.shooter.SetSpeed;
import frc.team670.robot.commands.shooter.Shoot;
import frc.team670.robot.commands.shooter.ShooterIntakeV2;
import frc.team670.robot.commands.shooter.StopShooter;
import frc.team670.robot.commands.shooter.Subwoofer;
import frc.team670.robot.commands.vision.AlignToAmp;
import frc.team670.robot.commands.vision.AlignToNote;
import frc.team670.robot.commands.vision.MoveToTargetVision;
import frc.team670.robot.subsystems.Shooter;
import frc.team670.robot.subsystems.Elevator;
import frc.team670.robot.subsystems.Elevator.ElevatorState;
import frc.team670.robot.subsystems.drivebase.DriveBase;

public final class OI {


    // Controllers
    private static MustangController driverController = new MustangController(0);
    private static MustangController operatorController = new MustangController(1);

    // Driver buttons
    private static JoystickButton zeroGyroDriver = new JoystickButton(driverController, XboxButtons.START);
    
    // shooter
    private static JoystickButton shoot = new JoystickButton(driverController, XboxButtons.LEFT_BUMPER);
    private static POVButton stop = new POVButton(driverController, 90);
    private static POVButton shooterUp = new POVButton(driverController, 0);
    private static POVButton shooterDown = new POVButton(driverController, 180);

    private static JoystickButton setAngle = new JoystickButton(driverController, XboxButtons.BACK);

    // Align to cardinal directions
    private static JoystickButton rotateTo90 = new JoystickButton(driverController, XboxButtons.X);
    private static JoystickButton rotateTo180 = new JoystickButton(driverController, XboxButtons.A);
    private static JoystickButton rotateTo270 = new JoystickButton(driverController, XboxButtons.B);

    //Vision
    private static JoystickButton speakerTracking = new JoystickButton(driverController, XboxButtons.RIGHT_BUMPER);
    private static POVButton setSpeed = new POVButton(driverController, 270);
    private static Trigger alignToAmp = driverController.rightTrigger();
    private static JoystickButton alignToNote=new JoystickButton(driverController, XboxButtons.Y);


    //operator

    //intake
    private static JoystickButton startIntake = new JoystickButton(operatorController, XboxButtons.RIGHT_BUMPER);
    private static JoystickButton stopIntake = new JoystickButton(operatorController, XboxButtons.BACK);
    private static JoystickButton ejectIntake = new JoystickButton(operatorController, XboxButtons.LEFT_BUMPER); //can be moved to backup controller
    private static JoystickButton toggleDeployer = new JoystickButton(operatorController, XboxButtons.X); //will add if needed 

    // elevator
    private static JoystickButton moveElevatorUp = new JoystickButton(operatorController, XboxButtons.LEFT_JOYSTICK_BUTTON);
    private static JoystickButton moveElevatorDown = new JoystickButton(operatorController, XboxButtons.RIGHT_JOYSTICK_BUTTON);
    private static JoystickButton zeroElevator = new JoystickButton(operatorController, XboxButtons.START);


    private static JoystickButton moveToAmp = new JoystickButton(operatorController, XboxButtons.Y); 
    private static JoystickButton moveToConveyorRetrieval = new JoystickButton(operatorController, XboxButtons.B);
   
    //no vision shooting
    private static Trigger undeployBars = operatorController.leftTrigger();
    private static Trigger subwoofer = operatorController.rightTrigger();


    //No Vision Shooting
    private static POVButton cycle = new POVButton(operatorController, 90);
    private static POVButton intakeshooter = new POVButton(operatorController, 270);

    //Endgame
    private static POVButton climb = new POVButton(operatorController, 0);
    private static POVButton deployBars = new POVButton(operatorController, 180);    


    public static MustangController getDriverController() {
        return driverController;
    }

    public static MustangController getOperatorController() {
        return operatorController;
    }

    public static void configureButtonBindings() {
        DriveBase driveBase = DriveBase.getInstance();
        Intake intake = Intake.getInstance();
        Indexer indexer = Indexer.getInstance();
        Shooter shooter = Shooter.getInstance();
        Elevator elevator = Elevator.getInstance();
        Bars bars = Bars.getInstance();
        LED led = LED.getInstance();

        //driver
        driveBase.initDefaultCommand(new XboxSwerveDrive(driveBase, driverController));
        zeroGyroDriver.onTrue(new SetSwerveForwardDirection(driveBase));
        shooterUp.onTrue(new ManuallyMoveShooter(shooter, true));
        shooterDown.onTrue(new ManuallyMoveShooter(shooter, false));
              
        // //Rotate to cardinal direction while driving
        XboxSwerveDrive driveCommand = (XboxSwerveDrive) driveBase.getDefaultCommand();
        rotateTo90.onTrue(driveCommand.new SetDesiredHeading(new Rotation2d(Math.PI / 2)));
        rotateTo180.onTrue(driveCommand.new SetDesiredHeading(new Rotation2d(Math.PI)));
        rotateTo270.onTrue(driveCommand.new SetDesiredHeading(new Rotation2d(3 * Math.PI / 2)));

        // //intake, indexer and deployer
        startIntake.onTrue(new StartIntake(intake, indexer,shooter, elevator));
        stopIntake.onTrue(new StopIntake(intake, indexer));
        ejectIntake.onTrue(new EjectIntake(intake, indexer));
        toggleDeployer.onTrue(new ToggleDeployer(intake));
        
        // //shooter
        shoot.onTrue(new Shoot(shooter, elevator));
        stop.onTrue(new StopShooter(shooter));
        subwoofer.onTrue(new Subwoofer(shooter));
        setAngle.onTrue(new SetAngle(shooter));
        setSpeed.onTrue(new SetSpeed(shooter));
        intakeshooter.onTrue(new ShooterIntakeV2(shooter, elevator, led));

        // //elevator
        moveElevatorUp.onTrue(new ManuallyMoveElevator(elevator, true));
        moveElevatorDown.onTrue(new ManuallyMoveElevator(elevator, false));
        moveToAmp.onTrue(new Amp(shooter, elevator));
        moveToConveyorRetrieval.onTrue(new Retrieval(elevator, ElevatorState.INDEXER_RETRIEVAL, shooter));
        zeroElevator.onTrue(new ZeroElevator(elevator, shooter));

        climb.onTrue(new Climb(bars, shooter, elevator, intake));
        deployBars.onTrue(new DeployBars(bars, false));
        undeployBars.onTrue(new DeployBars(bars, true));
        //Vision
        speakerTracking.onTrue(new MoveToTargetVision(driveBase, driverController, shooter));
        alignToAmp.onTrue(new AlignToAmp(driveBase, driverController, shooter));
        cycle.onTrue(new AutonSetAngle(shooter, 95));
        cycle.onFalse(new AutonSetAngle(shooter, 135));

        alignToNote.onTrue(new AlignToNote(driveBase));
    }
}

