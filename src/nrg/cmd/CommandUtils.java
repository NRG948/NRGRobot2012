package nrg.cmd;

import nrg.NRGDebug;
import nrg.NRGMathHelper;

/**
 * This class stores methods and functions that CommandBase objects can access and execute.
 * @author Mohammad Adib
 */
public class CommandUtils {

    /**
     * @param minDriveValue the minimum speed needed to move the robot
     */
    public static final double minDriveValue = 0.3;

    /**
     * @description: Converts a motor speed or twist value from -100 to 100 scale to a -1 to 1 scale.
     * @param neg100to100scale a motor speed or twist value that is in the -100 to 100 scale
     * @param threshold when the motor speed or twist is below this value, it is changed to 0.0, otherwise it is converted from -100 to 100 scale to a -1 to 1 scale (TODO)
     * @return a motor speed value that is in the -1 to 1 scale, more friendly for drive
     */
    public static double convertScales(double neg100to100scale) {
        final double threshold = 0.10;
        double neg1to1scale = neg100to100scale / 100.0;
        return neg1to1scale < threshold ? 0.0 : neg1to1scale;
    }

    /**
     * @description: Used by other classes to print debug messages via the NRGDebug class.
     * @param str the string that should be displayed.
     */
    public static void debugPrintln(String str) {
        NRGDebug.println(NRGDebug.AUTONOMOUS, str);
    }

    /**
     * @description: A quadratic equation that smooths the drive value as the robot drives.
     * @param distance the amount of total distance that the robot must travel to get from point A to B
     * @param distTravelled the amount of distance traveled
     * @param maxSpeed the maximum speed of the robot
     * @return the smoothed drive value using a double clamp
     */
    public static double smoothDriveValueQuadratic(double distance, double distTravelled, double maxSpeed) {
        double x = distTravelled;
        return (-(x - distance) * (x + distance)) / ((distance * distance) / (maxSpeed - minDriveValue)) + minDriveValue;
    }

    /**
     * @description: A quadratic equation that smooths the twist value as the robot twists.
     * @param degrees the amount of total degrees that the robot is turning
     * @param degreesTurned the number of degrees that have been turned
     * @param maxTwist the maximum twist value that will be supplied to drive
     * @return the smoothed twist value using a quadratic to smooth the value
     */
    public static double smoothTwistValueQuadratic(double degrees, double degreesTurned, double maxTwist) {
        double x = degreesTurned;
        return (-(x - degrees) * (x + degrees)) / ((degrees * degrees) / maxTwist);
    }

    /**
     * @description: A quadratic equation that smooths the drive value as the robot moves (time based).
     * @param delay the amount of delay
     * @param timeSpent the amount of time that has gone by
     * @param maxSpeed the maximum speed of the robot
     * @return the smoothed timed drive value using quadratics 
     */
    public static double smoothTimedDriveValueQuadratic(double delay, double timeSpent, double maxSpeed) {
        double x = timeSpent;
        return (-(x - delay) * (x + delay)) / ((delay * delay) / maxSpeed);
    }

    /**
     * @description: A double clamp method that smooths the drive value as the robot drives.
     * @param distance the amount of total distance that the robot must travel to get from point A to B
     * @param distTravelled the amount of distance traveled
     * @param maxSpeed the maximum speed of the robot
     * @return the smoothed drive value using a double clamp
     */
    public static double smoothDriveValueClamp(double distance, double distTravelled, double maxSpeed) {
        return NRGMathHelper.clamp(distTravelled / distance, minDriveValue, maxSpeed);
    }
    
    /**
     * @description: A double clamp method that smooths the drive value as the robot moves (time based).
     * @param delay the amount of delay
     * @param timeSpent the amount of time that has gone by
     * @param maxSpeed the maximum speed of the robot
     * @return the smoothed timed drive value using a double clamp to ramp down the value
     */
    public static double smoothTimedDriveValueClamp(double delay, double timeSpent, double maxSpeed) {
        return NRGMathHelper.clamp(timeSpent / delay, minDriveValue, maxSpeed);
    }
}
