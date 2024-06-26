package frc.robot.commands.drivetrain;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.util.Constants;
import frc.robot.subsystems.DriveTrain;

/**
 * Allows the DDR control system to drive
 */
public class DDRDrive extends Command implements Constants.DriveTrain {
    DriveTrain driveTrain;
    GenericHID controller = new GenericHID(0);


    public DDRDrive(DriveTrain driveTrain) 
    {
        this.driveTrain = driveTrain;
        addRequirements(driveTrain);
    }

    @Override
    public void execute() {
        // System.out.println(getButton(1));
        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
            (getButton(3) - getButton(2)) * 0.25,
            (getButton(1) - getButton(4)) * 0.25,
            (getButton(7) - getButton(8)) * 0.5, 
            Rotation2d.fromRadians(0) 
        );
        SwerveModuleState[] swerveModuleStates = driveKinematics.toSwerveModuleStates(chassisSpeeds);
        driveTrain.setModuleStates(swerveModuleStates);

    }
    /**
     * Takes in the button and checks if it is pressed
     * @param id the identity of the button
     * @return the state of the button
     */
    private double getButton(int id) {
        if (Math.abs(controller.getRawAxis(4)) > 0.1)
            return controller.getRawButton(id) ? 1 : 0;
        return 0;
    }
}
