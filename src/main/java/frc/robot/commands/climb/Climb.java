package frc.robot.commands.climb;

import java.util.Arrays;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Winch;
import frc.robot.util.Constants;

public class Climb extends Command implements Constants.Winch
{
    Winch winch;
    long startTime;
    boolean initialRun;
    boolean finished;
    long extendStopTime;
    long liveTime;
    // long retractStopTime;

    public Climb(Winch winch, Shooter shooter) {
        this.winch = winch;
        addRequirements(winch);
        addRequirements(shooter);
        initialRun = true;
        finished = false;
        SmartDashboard.putNumber("winch/extend speed", extendSpeed);
        SmartDashboard.putNumber("winch/retract speed", retractSpeed);
        SmartDashboard.putNumber("winch/extendTime", extendTime);
    }

    @Override
    public void initialize() {
        if (initialRun) {
            startTime = System.currentTimeMillis();
            initialRun = false;
            liveTime = 0;
        } else {
            startTime = System.currentTimeMillis() - liveTime;
        }
        extendStopTime = startTime + (long)SmartDashboard.getNumber("winch/extendTime", extendTime);
    }

    /*
     * Moves the winch, changing speeds after a set time, and stopping if it detects a significant current difference
     */
    @Override
    public void execute() {
        //if (Winch.leftSwitch.isPressed() || Winch.rightSwitch.isPressed()) {
        //     winch.setSpeed(0);
        //     finished = true;
        //     return;
        // }
        // if (Math.abs((winch.getCurrentDifference()/2)) >= currentDifferenceThreshold) {
        //     System.out.println("lbbuehhel");
        //     winch.setSpeed(0);
        //     finished = true;
        //     return;
        // } else if (System.currentTimeMillis() < extendStopTime) {
        //     winch.setSpeed(SmartDashboard.getNumber("winch/extend speed", extendSpeed));
        // } else {
        //     winch.setSpeed(SmartDashboard.getNumber("winch/retract speed", retractSpeed));
        // }
    }

    @Override
    public void end(boolean interrupted) {
        liveTime = System.currentTimeMillis() - startTime;
        winch.setSpeed(0);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}