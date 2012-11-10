package nrg.cmd;

/**
 * @author Matthew F, Mohammad Adib
 */
public class DelayCommand extends CommandBase {

    private long startTime;
    private long duration;
    private String info;

    /**
     * This command delays the robot for a certain amount of time in milliseconds
     * @param duration the amount of milliseconds to wait
     */
    public DelayCommand(long duration) {
        this.duration = duration;
        info = duration + "";
    }

    public void init() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Runs the delay command
     * @return true when the time has passed by, else false.
     */
    public boolean run() {
        CommandUtils.debugPrintln("DelayCommand time left: " + (duration - (System.currentTimeMillis() - startTime) + "ms"));
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void finalize() {
    }

    public String getInfo() {
        return info;
    }
}