package nrg.cmd;

import nrg.*;

/**
 *
 * @author Stephen
 */
public class TankMoveCommand extends CommandBase {

    NRGDrive drive;
    double distanceInInches;
    double startDistanceLeft;
    double startDistanceRight;
    double motorSpeed;
    double heading;
    public static final double AUTO_MOVE_DISTANCE_THRESHOLD = 0.5;
    private String info;

    private static final int FRONT = 0;
    private static final int BACK = 1;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    /**
     *
     * @param distanceInInches Distance to move in inches.
     * @param motorSpeed Speed to move motor. 
     */
    public TankMoveCommand(double distanceInInches, double motorSpeed) {
        this.distanceInInches = distanceInInches;
        this.motorSpeed = motorSpeed;
        info = "new TankMoveCommand(" + distanceInInches + "," + motorSpeed + ")";
    }

    /**
     * Initializes command.
     */
    public void init() {
        this.drive = NRGRobot.getDrive();
        NRGSensors.quadrature[BACK][LEFT].update();
        NRGSensors.quadrature[BACK][RIGHT].update();
        
        startDistanceLeft = NRGSensors.quadrature[BACK][LEFT].getDistance();
        startDistanceRight = NRGSensors.quadrature[BACK][RIGHT].getDistance();
        //tests if Quadratures even works slightly?
        CommandUtils.debugPrintln("Left Dist Init: " + startDistanceLeft + ". Right Dist Init: " + startDistanceRight);
        
    }

    /**
     *
     * @return True if the DistanceMove is done. False otherwise.
     */
    public boolean run()
    {
        NRGSensors.quadrature[BACK][LEFT].update();
        NRGSensors.quadrature[BACK][RIGHT].update();
        double currentLeftDist = NRGSensors.quadrature[BACK][LEFT].getDistance();
        double currentRightDist = NRGSensors.quadrature[BACK][RIGHT].getDistance();
        CommandUtils.debugPrintln("Left Distance: " + currentLeftDist + ". Right Distance:" + currentRightDist);
        double dLeft  = Math.abs(currentLeftDist - startDistanceLeft);
        double dRight = Math.abs(currentRightDist - startDistanceRight);
        double inchesRemaining = distanceInInches - (dLeft + dRight) / 2;
        CommandUtils.debugPrintln("Distance Remaining: " + inchesRemaining);
        if (inchesRemaining <= AUTO_MOVE_DISTANCE_THRESHOLD)
        {
            return true;
        }
        else
        {
            // Ramp down the speed for the last 3 inches of movement
            double adjSpeed = motorSpeed * NRGMathHelper.clamp(inchesRemaining / 3.0, 0.3, 1.0);
            drive.driveStraight(adjSpeed, lastHeading);
            return false;
        }        
    }

    public String getInfo() {
        return info;
    }

    public void finalize()
    {
        //drive.tankDrive(0, 0);
        drive.driveStraight(0, lastHeading);
    }
}
