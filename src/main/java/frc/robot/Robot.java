// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Command scheduler runs one of these methods depending on the robot's state.
 * Put next to nothing in here, put everything in the <code>RobotContainer.java</code>.
 */
public class Robot extends LoggedRobot {
  private Command autoCommand;
  private Command driveCommand;
  private RobotContainer robotContainer;

  @Override
  public void robotInit() {
    robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    autoCommand = robotContainer.getAutonomousCommand();
    if (autoCommand != null) {
      autoCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    robotContainer.resetOdometry();
    if (autoCommand != null) {
      autoCommand.cancel();
    }
    driveCommand = robotContainer.getDriveCommand();
    if (driveCommand != null) {
      driveCommand.schedule();
    }
  }
  
  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationInit() {
  }

  @Override
  public void simulationPeriodic() {}
}
