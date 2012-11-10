package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * This command extends or retracts the arm which lowers the bridge for either a given amount of time, or forever (not like !Forever! but like until another command or object decides to call this
 * method or changes the values in INRGDataSource...
 * @author Mohammad Adib
 */
public class SetBridgeCommand extends CommandBase {

    private double extension;
    private long duration;
    private String info;
    private long startTime;
    public static final int FOREVER = -1;
    public static final double EXTENDED = 0.0, STOWED = 1.0;

    /**
     * @param state: the state of the arm which lowers the bridge. SetBridgeCommand.STOWED stows the arm, SetBridgeCommand.EXTENDED extends the arm
     * @param duration: the duration(in ms) for which the arm will be set to that state. use SetBridgeCommand.FOREVER to keep the arm pinned at a certain state.
     */
    public SetBridgeCommand(double extension, long duration) {
        this.extension = extension;
        this.duration = duration;
        this.info = "new SetBridgeCommand(" + extension + "," + duration + ")";
    }

    public void init() {
        startTime = System.currentTimeMillis();
        NRGAutonomous.setLowerBridge(extension);
    }

    public boolean run() {
        if (duration == FOREVER) {
            return true;
        }
        /**
         * In the case that the arm should not stay in a position forever Keep running the command until the time runs out
         */
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void finalize() {
        /**
         * Toggle the arm back to its original position if the duration was a set amount of time (in ms)
         */
        if (duration != FOREVER) {
            NRGAutonomous.setLowerBridge(0);
        }
    }

    public String getInfo() {
        return info;
    }
}
