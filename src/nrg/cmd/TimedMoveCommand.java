package nrg.cmd;

import nrg.NRGAutonomous;
import nrg.NRGMathHelper;

/**
 * This command is used by autonomous to move the robot The robot can be moved either up, down, left, or right
 * @author Mohammad Adib
 */
public class TimedMoveCommand extends CommandBase {

    private int heading;
    private long startTime, delay;
    private double maxSpeed;
    public static final int FORWARD = 0, BACKWARD = 1, LEFT = 2, RIGHT = 3, ANGLETHRESHOLD = 10;
    private String info;

    public TimedMoveCommand(int heading, long delay, double maxSpeed) {
        //@param heading: either assign FORWARD, BACKWARD, LEFT, or RIGHT to it.
        this.heading = heading;
        this.delay = delay;
        this.maxSpeed = NRGMathHelper.clamp(maxSpeed, -1, 1);
        info = "new TimedMoveCommand(" + heading + "," + delay + "," + maxSpeed + ")";
    }

    public void init() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Executes the move command
     * @return if the robot has reached it's destination
     */
    public boolean run() {
        long timeLeft = delay - getTimeSpent();
        //If it has not reached it's destination...
        if (timeLeft > 0) {
            /**
             * Get the current speed based on PID-like method, depends on where the robot is in the process of getting from A to B
             */
            //double currSpeed = NRGMathHelper.clamp(distLeft / K, maxSpeed, 0);
            double currSpeed = CommandUtils.smoothTimedDriveValueQuadratic(delay, getTimeSpent(), maxSpeed);
            /**
             * Move the robot in the direction it is going
             */
            if (heading == FORWARD || heading == BACKWARD) {
                //Set INRGDataSource values
                NRGAutonomous.setDrive(0.0, ((heading == FORWARD) ? currSpeed : -currSpeed), NRGAutonomous.driveTwist);
            }
            else if (heading == LEFT || heading == RIGHT) {
                //Set INRGDataSource values
                NRGAutonomous.setDrive(((heading == LEFT) ? -currSpeed : currSpeed), 0.0, NRGAutonomous.driveTwist);
            }
        }
        CommandUtils.debugPrintln("Time Left: " + timeLeft + "ms");
        return timeLeft <= 0;
    }

    /**
     * @return Gets the distance traveled since the start of the command.
     */
    private long getTimeSpent() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Optional method to run any final code
     */
    public void finalize() {
        NRGAutonomous.setDrive(0.0, 0.0, 0.0);
    }

    public String getInfo() {
        return info;
    }
}
