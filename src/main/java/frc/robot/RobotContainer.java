// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Optional;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.acquisition.Collect;
import frc.robot.commands.acquisition.Reject;
import frc.robot.commands.climb.Climb;
import frc.robot.commands.drivetrain.DDRDrive;
import frc.robot.commands.drivetrain.DriveFast;
import frc.robot.commands.drivetrain.DriveSlow;
import frc.robot.commands.drivetrain.DriveToAmp;
import frc.robot.commands.drivetrain.DriveToPoint;
import frc.robot.commands.drivetrain.DriveToRing;
import frc.robot.commands.drivetrain.JoystickDrive;
import frc.robot.commands.drivetrain.TurnToSpeaker;
import frc.robot.commands.shooter.AmpShot;
import frc.robot.commands.shooter.BumbperShot;
import frc.robot.commands.shooter.CenterNote;
import frc.robot.commands.shooter.DriveAdjustShoot;
import frc.robot.commands.shooter.SpeakerShot;
import frc.robot.commands.shooter.WindUp;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LED;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Winch;
import frc.robot.util.Constants;
import frc.robot.util.Constants.Auto.NotePositions;
import frc.robot.util.Constants.Field;
import frc.robot.util.DistanceXY;
import frc.robot.util.DriveStates;
import frc.robot.util.ShooterState;

/**
 * The container for the robot. Contains subsystems, OI devices, and commands.
 */
public class RobotContainer {
  // SUBSYSTEMS
  private final DriveTrain driveTrain = new DriveTrain();
  private final Intake acq = new Intake();
  private final Indexer indexer = new Indexer();
  private final Shooter shooter = new Shooter();
  private final Winch winch = new Winch();
  public static LED led = new LED(50, 2, 15); // TODO: Set LED length

  public static boolean useVisions;

  // STATES
  /** Speed modes */
  public static DriveStates driveStates;
  /** Manages the speed of the shooter */
  public static ShooterState shooterState;
  /** I think this is supposed to just always have the distance to the speaker, not sure
   *  why its not implemented though, might be @deprecated TODO: Delete or implement */
  private static DistanceXY distanceXY;

  // FINAL IO
  private final XboxController driver = new XboxController(Constants.Controller.driverControllerPort);
  private final XboxController operator = new XboxController(Constants.Controller.operatorControllerPort);
  private final SendableChooser<Command> autoChooser;

  // STATIC IO
  /** Between DDR pad and controller input */
  public static SendableChooser<String> inputMode;
  public static SendableChooser<String> testOrVisionsShooter;
  public static SendableChooser<Pose2d> ringPositionChooser;
  public static SendableChooser<String> autoCommandChooser;
  public static SendableChooser<Pose2d> startingPos;

  /**
   * Constructs a new RobotContainer. This constructor is responsible for setting up the robot's subsystems and commands.
   * It initializes the LED, drive states, distance measurement, input modes, and shooter state.
   * It also sets the default command for the drive train to be JoystickDrive.
   */
  public RobotContainer() {
    // SUBSYTEM PIV INITIALIZATION
    // led = new LED(60);
    // STATE INITIALIZATION
    useVisions = false;
    driveStates = DriveStates.normal;
    distanceXY = new DistanceXY(driveTrain, getAlliance());
    // IO INITIALIZATION
    inputMode = new SendableChooser<>();
    testOrVisionsShooter = new SendableChooser<>();
    ringPositionChooser = new SendableChooser<>();
    autoCommandChooser = new SendableChooser<>();
    startingPos = new SendableChooser<>();
    autoChooser = AutoBuilder.buildAutoChooser(); SmartDashboard.putData("Auto Chooser", autoChooser);
    ringPositionChooser.addOption("LeftRed", new Pose2d(NotePositions.kNotesStartingRedWing[2], new Rotation2d()));
    ringPositionChooser.addOption("MiddleRed",  new Pose2d(NotePositions.kNotesStartingRedWing[1], new Rotation2d()));
    ringPositionChooser.addOption("RightRed",  new Pose2d(NotePositions.kNotesStartingRedWing[0], new Rotation2d()));
    ringPositionChooser.addOption("LeftBlue", new Pose2d(NotePositions.kNotesStartingBlueWing[2], new Rotation2d()));
    ringPositionChooser.addOption("MiddleBlue",  new Pose2d(NotePositions.kNotesStartingBlueWing[1], new Rotation2d()));
    ringPositionChooser.addOption("RightBlue",  new Pose2d(NotePositions.kNotesStartingBlueWing[0], new Rotation2d()));
    ringPositionChooser.setDefaultOption("LeftRed", new Pose2d(NotePositions.kNotesStartingRedWing[2], new Rotation2d()));
    SmartDashboard.putData(ringPositionChooser);
    autoCommandChooser.addOption("OneRing", "OneRing");
    autoCommandChooser.addOption("Shoot", "Shoot");
    autoCommandChooser.addOption("Nothing", "Nothing");
    autoCommandChooser.setDefaultOption("Nothing", "Nothing");
    SmartDashboard.putData(autoCommandChooser);

    startingPos.addOption("1", new Pose2d(0.68, 6.58, Rotation2d.fromDegrees(60)));
    startingPos.addOption("2", new Pose2d(1.19, 5.57, Rotation2d.fromDegrees(0)));
    startingPos.addOption("3", new Pose2d(0.68, 4.57, Rotation2d.fromDegrees(-60)));
    startingPos.addOption("4", new Pose2d(0.39, 3.1, Rotation2d.fromDegrees(0)));
    startingPos.setDefaultOption("1", new Pose2d(0.68, 6.58, Rotation2d.fromDegrees(60)));
    SmartDashboard.putData(startingPos);
    

    // ROBOT CONFIGURATION
    configureBindings();
    addInputModes();
    addShootModes();
    setShooterState(ShooterState.notSpinning);
    driveTrain.setDefaultCommand(new JoystickDrive(driveTrain, driver));
  }

  /**
   * Binds specific commands to specific buttons and axis on the driver and operator controllers.
   */
  private void configureBindings() {
    // DRIVER BINDINGS
    new JoystickButton(driver, XboxController.Button.kRightBumper.value).whileTrue(new DriveSlow());
    new JoystickButton(driver, XboxController.Button.kLeftBumper.value).whileTrue(new DriveFast());
    new JoystickButton(driver, XboxController.Button.kB.value).whileTrue(new DriveToRing(driveTrain, acq, indexer, shooter));
    new JoystickButton(driver, XboxController.Button.kBack.value).whileTrue(new Climb(winch, shooter).withInterruptBehavior(Command.InterruptionBehavior.kCancelIncoming));
    new JoystickButton(driver, XboxController.Button.kLeftStick.value).onTrue(new RunCommand(() -> CommandScheduler.getInstance().cancelAll()));
    new JoystickButton(driver, XboxController.Button.kA.value).whileTrue(new DriveToAmp(driveTrain));
    new JoystickButton(driver, XboxController.Button.kX.value).onTrue(new RunCommand(() -> useVisions = true)).onFalse(new RunCommand(() -> useVisions = false));

    // OPERATOR BINDINGS
    // new JoystickButton(operator, XboxController.Button.kA.value).whileTrue(new DriveAdjustShoot(driveTrain, shooter, indexer).withInterruptBehavior(Command.InterruptionBehavior.kCancelSelf));
    new JoystickButton(operator, XboxController.Button.kA.value).whileTrue(new SpeakerShot(shooter, indexer, driveTrain).withInterruptBehavior(Command.InterruptionBehavior.kCancelSelf));
    new JoystickButton(operator, XboxController.Button.kB.value).whileTrue(new BumbperShot(shooter, indexer, driveTrain).withInterruptBehavior(Command.InterruptionBehavior.kCancelSelf));
    new JoystickButton(operator, XboxController.Button.kX.value).whileTrue(new Collect(acq, indexer, shooter));
    new JoystickButton(operator, XboxController.Button.kX.value).onFalse(new CenterNote(shooter, indexer));
    new JoystickButton(operator, XboxController.Button.kY.value).whileTrue(new Reject(acq, indexer, shooter));
    new JoystickButton(operator, XboxController.Button.kStart.value).onTrue(new AmpShot(shooter, driveTrain, indexer));
    new JoystickButton(operator, XboxController.Button.kRightBumper.value).onTrue(new WindUp(ShooterState.maxSpeed, shooter));
    new JoystickButton(operator, XboxController.Button.kLeftBumper.value).onTrue(new WindUp(ShooterState.notSpinning, shooter));
    new JoystickButton(operator, XboxController.Button.kLeftStick.value).onTrue(new RunCommand(() -> CommandScheduler.getInstance().cancelAll()));
  }
  
  /**
   * Adds the input modes to the SmartDashboard.
   */
  private static void addInputModes() {
    inputMode.setDefaultOption("controller", "controller");
    inputMode.addOption("ddr", "ddr");
    SmartDashboard.putData("Input Mode", inputMode);
    

    SmartDashboard.putNumber("pivot setpoint", 4.3);
        
    SmartDashboard.putNumber("velocity setpoint", 160);
  }

  /**
   * Adds the shoot modes to the SmartDashboard.
   */
  private static void addShootModes()
  {
    // TODO: Switch the default option from "test" to "visions" once visions is stable
    testOrVisionsShooter.setDefaultOption("test", "test");
    testOrVisionsShooter.addOption("visions", "visions");
  }

  /**
   * Returns the currently selected input mode.
   * @return the currently selected input mode
   */
  public Command getAutonomousCommand() {
    controlState = ControlState.AUTO;
    Pose2d speakerPose = (getAlliance().equals(Alliance.Blue)? new Pose2d(Field.speakerBlue, new Rotation2d()) : new Pose2d(Field.speakerRed, new Rotation2d()));
    if (autoCommandChooser.getSelected().equals("OneRing")) {
      setOdometry(getAlliance().equals(Alliance.Blue) ? startingPos.getSelected() : new Pose2d(Constants.Field.fieldWidth - startingPos.getSelected().getX(), startingPos.getSelected().getY(), new Rotation2d(Math.PI)));
      // return new OneRingAuto(acq, indexer, shooter, driveTrain, ringPositionChooser.getSelected(), speakerPose);
      return new SequentialCommandGroup(new SequentialCommandGroup(
            new SpeakerShot(shooter, indexer, driveTrain)),
            new ParallelRaceGroup
            (
                new Collect(acq, indexer, shooter),
                new DriveToPoint(driveTrain, ringPositionChooser.getSelected())
            )
            // new SpeakerShot(shooter, indexer, driveTrain)
            );

    } else {
      return null;
    }
  }

  public Command getDriveCommand() {
    controlState = ControlState.MANUAL;
    Command driveCommand;
    switch (inputMode.getSelected()) {
      default:
        driveCommand = new JoystickDrive(driveTrain, driver);
        break;
      case "controller":
        driveCommand = new JoystickDrive(driveTrain, driver);
        break;
      case "ddr":
        driveCommand = new DDRDrive(driveTrain);
        break;
    }
    return driveCommand;
  }

  public static Alliance getAlliance() {
    Optional<Alliance> ally = DriverStation.getAlliance();
    if (ally.isPresent()) {
        if (ally.get() == Alliance.Red) {
            SmartDashboard.putString("alliance", "red");
            return Alliance.Red;
        }
        if (ally.get() == Alliance.Blue) {  
            SmartDashboard.putString("alliance", "blue");
            return Alliance.Blue;
        }
    }
    return Alliance.Blue;
  }

  /**
   * Resets the odometry of the robot.
   */
  public void resetOdometry() {
    driveTrain.resetOdometry(new Pose2d());
    driveTrain.zeroHeading();
  }

  public void setOdometry(Pose2d pose) {
    driveTrain.resetOdometry(pose);
    driveTrain.zeroHeading();
  }

  public static ShooterState getShooterState() {
      return shooterState;
  }
  
  public static void setShooterState(ShooterState shooterState) {
      RobotContainer.shooterState = shooterState;
  }

  /** AUTO, PATHING, MANUAL */
  public enum ControlState {
    AUTO, PATHING, MANUAL
  }

  /** IDLE, DRIVING, COLLECTING, CENTERING, HAS_NOTE, SHOOTING, CLIMBING */
  public enum ActivityState {
    IDLE, DRIVING, COLLECTING, CENTERING, HAS_NOTE, SHOOTING, CLIMBING
  }

  /** Only use to drive LEDs, not safe for anything else */
  public static ControlState controlState;
  /** Only use to drive LEDs, not safe for anything else */
  public static ActivityState activityState;

  public static SendableChooser<Integer> blinkTickSpeedChooser = new SendableChooser<>();
  public static SendableChooser<Integer> rainbowTickSpeedChooser = new SendableChooser<>();
  static {
    rainbowTickSpeedChooser.addOption("ten", 10);
    rainbowTickSpeedChooser.addOption("five", 5);
    rainbowTickSpeedChooser.addOption("one", 1);
    rainbowTickSpeedChooser.setDefaultOption("one", 1);
    SmartDashboard.putData(rainbowTickSpeedChooser);

    blinkTickSpeedChooser.addOption("ten", 10);
    blinkTickSpeedChooser.addOption("five", 5);
    blinkTickSpeedChooser.addOption("one", 1);
    blinkTickSpeedChooser.setDefaultOption("one", 1);
    SmartDashboard.putData(blinkTickSpeedChooser);
  }

  public static void setLEDs(Color color) {LED.setColor(color);}
}
