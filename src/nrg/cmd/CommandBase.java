package nrg.cmd;

/**
 * @author Mohammad Adib
 */
public abstract class CommandBase {

    //used in TankMove and TankRotate Commands
    public static double lastHeading = 0.0;
    public abstract void init();

    public boolean initAndRun() {
        init();
        boolean done = run();
        if (done) {
            finalize();
        }
        return done;
    }

    /**
     * Runs a specific command
     * @return if the command was run successfully
     */
    public abstract boolean run();
    
    /** 
     * @return the parameters the command was fed
     */
    public abstract String getInfo();

    public abstract void finalize();
}
