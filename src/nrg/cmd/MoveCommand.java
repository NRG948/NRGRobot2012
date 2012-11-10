package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * This command is used by autonomous to move the robot The robot can be moved either up, down, left, or right
 * @author Mohammad Adib
 */
public class MoveCommand extends CommandBase {
    
    private double srcX, srcY;
    private double deltaX, deltaY, destX, destY;
    private final int K = 24;
    private String info;

    /** 
     * @param X in inches
     * @param Y in inches
     * @param maxSpeed in inches
     */
    public MoveCommand(double X, double Y, double maxSpeed) {
        info = X + "," + Y + "," + maxSpeed;
    }

    public void init() {
    }

    /**
     * Executes the move command
     * @return if the robot has reached it's destination
     */
    public boolean run() {
        return true;
    }

    /**
     * @return Gets the distance traveled since the start of the command.
     */
    public void finalize() {
        NRGAutonomous.setDrive(0.0, 0.0, 0.0);
    }

    public String getInfo() {
        return info;
    }
}
