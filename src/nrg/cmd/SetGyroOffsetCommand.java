package nrg.cmd;

import nrg.NRGAutonomous;
import nrg.NRGSensors;

/**
 * This command resets the gyro and offsets it based on the current heading (if on a side position)
 * @author Eric Lin
 */
public class SetGyroOffsetCommand extends CommandBase {

    private String info;
    private double offset;

    /**
     * 
     * @param angle 
     */
    public SetGyroOffsetCommand(double angle) {
        this.offset = angle;
        info = "new SetGyroOffsetCommand(" + offset + ")";
    }

    public void init() {
    }

    public boolean run() {
        NRGSensors.GYRO.reset();
	NRGSensors.GYRO.setOffset(offset);
        return true;
    }

    public void finalize() {
    }

    public String getInfo() {
        return info;
    }
}
