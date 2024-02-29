package frc.robot.commands.climb;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Winch;
import frc.robot.util.Constants;

public class Extend extends Command implements Constants.Winch
{
    Winch winch;

    public Extend(Winch winch) {
        this.winch = winch;
        addRequirements(winch);
    }

    @Override
    public void initialize() {
        winch.setSpeed(extendSpeed);
    }

    @Override
    public void execute() {
        winch.setSpeed(extendSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        winch.setSpeed(0);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}