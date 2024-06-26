package frc.robot.util;

import java.util.TreeMap;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;

public class ValueFromDistance {

    public static TreeMap<Double, AngleSpeed> shootMap = new TreeMap<>();
    static {
        //put values here
    }

    public static AngleSpeed getAngleSpeed(double distance) {
        if(distance > shootMap.firstKey() && distance < shootMap.lastKey()) {
            double floorDistance = shootMap.floorKey(distance);
            double ceilingDistance = shootMap.ceilingKey(distance);
            double remainderFrac = (distance % floorDistance) / (ceilingDistance - floorDistance);

            double speed = shootMap.get(floorDistance).getSpeed() + remainderFrac * (Math.abs(shootMap.get(ceilingDistance).getSpeed() - shootMap.get(floorDistance).getSpeed()));
            double angle = shootMap.get(floorDistance).getAngle() + remainderFrac * (Math.abs(shootMap.get(ceilingDistance).getAngle() - shootMap.get(floorDistance).getAngle()));
            System.out.println("WORKING!");
            return new AngleSpeed(angle, speed);
        }
        else return (distance < shootMap.firstKey()) ?
                //THIS WORKS DONT THINK TOO HARD ABOUT IT
                //gets the angle speed of the floor or ceiling key
                new AngleSpeed(shootMap.get(shootMap.firstKey()).getAngle(), shootMap.get(shootMap.firstKey()).getSpeed()):
                new AngleSpeed(shootMap.get(shootMap.lastKey()).getAngle(), shootMap.get(shootMap.lastKey()).getSpeed());
    }

    public static AngleSpeed getAngleSpeedLinearized(double distance) {
        if (distance > shootMap.firstKey() && distance < shootMap.lastKey()) {
                double floorDistance = shootMap.floorKey(distance); 
                double ceilingDistance = shootMap.ceilingKey(distance);

                double run = ceilingDistance - floorDistance;
                
                double speed = shootMap.get(floorDistance).getSpeed() + (distance - floorDistance) * ((shootMap.get(ceilingDistance).getSpeed() - shootMap.get(floorDistance).getSpeed())/run);
                double angle = shootMap.get(floorDistance).getAngle() + (distance - floorDistance) * ((shootMap.get(ceilingDistance).getAngle() - shootMap.get(floorDistance).getAngle())/run);

                return new AngleSpeed(angle, speed);
        }
        return distance <= shootMap.firstKey() ?
            shootMap.get(shootMap.firstKey()) : shootMap.get(shootMap.lastKey());
    }

    public static AngleSpeed getAngleSpeedFloored(double distance) {
        double flooredVal = shootMap.floorKey(distance);
        return new AngleSpeed(shootMap.get(flooredVal).getAngle(),
                shootMap.get(flooredVal).getSpeed());
    }

    public static double getDistanceToTarget(Pose2d current, Translation2d target) {
        return current.getTranslation().getDistance(target);
    }

}
