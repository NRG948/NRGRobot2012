package nrg.cmd;

/**
 * Runs an array of commands pseudo-simultaneously
 * @author Mohammad Adib
 */
public class SynchronizedCommand extends CommandBase {

    private String info;
    CommandBase[] cmds;
    boolean[] cmdFinished;

    /**
     * @param cmds: a CommandBase[] array containing all the commands that need to be run sequentially
     */
    public SynchronizedCommand(CommandBase[] cmds) {
        this.cmds = cmds;
        info = "new SynchronizedCommand(new CommandBase[] {\n";
        for (int i = 0; i < cmds.length; i++) {;
            info += cmds[i].getInfo() + "\n";
        }
        info += "});";
    }

    /**
     * Initialize all commands pseudo-simultaneously
     */
    public void init() {
        cmdFinished = new boolean[cmds.length];
        for (int i = 0; i < cmds.length; i++) {
            cmds[i].init();
            cmdFinished[i] = false;
        }
    }

    /**
     * Runs all the commands by sequentially running through each one of them
     * @return if all the commands have finished running
     */
    public boolean run() {
        boolean result = true;
        for (int i = 0; i < cmds.length; i++) {
            if (!cmdFinished[i]) {
                result = result & cmds[i].run();
            }
        }
        return result;
    }

    /**
     * Finalizes all the commands
     */
    public void finalize() {
        for (int i = 0; i < cmds.length; i++) {
            cmds[i].finalize();
        }
    }

    public String getInfo() {
        return info;
    }
}