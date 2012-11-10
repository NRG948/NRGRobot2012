package nrg.cmd;

/**
 * @author Mohammad Adib
 * Repels, Possesses, or stops the possesser
 */

import nrg.NRGAutonomous;

public class PossessCommand extends CommandBase {
    
    private String info;
    public static final int POSSESS = 1, REPEL = -1, STOP = 0;
    private int state;
    
    public PossessCommand(int state) {
        this.state = state;
        info = "new PossessCommand(" + state + ")";
    }

    public void init() {
    }

    public boolean run() {
        NRGAutonomous.setPossessorState(state);
        return true;
    }

    public void finalize() {
        
    }

    public String getInfo() {
        return info;
    }
    
}
