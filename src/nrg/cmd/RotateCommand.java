package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * This command appends a certain amount to the orientation
 * TODO: finish this
 * @author Mohammad Adib
 */
public class RotateCommand extends CommandBase {
    
    private String info;

    private double degrees;

    public RotateCommand(double degrees) {
        this.degrees = degrees;
        info = "new RotateCommand(" + degrees + ")";
    }

    public void init() {
        
    }

    public boolean run() {
        NRGAutonomous.setOrientation(NRGAutonomous.orientation + degrees);
        return true;
    }

    public void finalize() {
    }

    public String getInfo() {
        return info;
    }
}
