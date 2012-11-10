package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * This command turns the robot along the local Z axis a certain amount of degrees. Features PID
 * TODO: finish this
 * @author Mohammad Adib
 */
public class SetOrientationCommand extends CommandBase {

    private String info;
    private double orientation;

    public SetOrientationCommand(double orientation) {
        this.orientation = orientation;
        info = "new SetOrientationCommand(" + orientation + ")";
    }

    public void init() {
    }

    public boolean run() {
        NRGAutonomous.setOrientation(orientation);
        return true;
    }

    public void finalize() {
    }

    public String getInfo() {
        return info;
    }
}
