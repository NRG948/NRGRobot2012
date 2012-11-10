package nrg.cmd;

import nrg.NRGIO;
import nrg.NRGPositionTracker;
import nrg.NRGSensors;

/**
 * Handles IO operations for autonomous
 * @author Mohammad Adib
 */
public class CommandIO {

    /**
     * Gets the switch states on the Driver Station
     * @return 
     */
    public static final int POSITION_FRONT_LEFT = 0;
    public static final int POSITION_BACK_LEFT = 1;
    public static final int POSITION_FRONT_RIGHT = 2;
    public static final int POSITION_BACK_RIGHT = 3;
    public static final int POSITION_FRONT_CENTER = 4;
    public static final int POSITION_BACK_CENTER = 5;

    public static boolean[] getSwitchStates() {
        boolean[] states = {
            NRGIO.getSwitch(NRGIO.AUTO_DELAY),
            NRGIO.getSwitch(NRGIO.POSITION_FRONT_BACK),
            NRGIO.getSwitch(NRGIO.POSITION_LEFT),
            NRGIO.getSwitch(NRGIO.POSITION_CENTER),
            NRGIO.getSwitch(NRGIO.POSITION_RIGHT),
            NRGIO.getSwitch(NRGIO.AUTO_BRIDGE_CENTER),
            NRGIO.getSwitch(NRGIO.AUTO_BRIDGE_SIDE)
        };

        return states;
    }

    public static boolean isAutonomousEnabled() {
        return NRGIO.getSwitch(NRGIO.POSITION_LEFT) || NRGIO.getSwitch(NRGIO.POSITION_RIGHT) || NRGIO.getSwitch(NRGIO.POSITION_CENTER);
    }

    /**
     * Returns the gyro analog value (modular 360)
     * @return the Gyro analog angle (modular 360)
     */
    public static double getGyroAnalogValue() {
        return NRGSensors.GYRO.getAnalogValue();
    }

    /**
     * Returns the gyro angle
     * @return the Gyro angle (without modular of 360) 
     */
    public static double getGyroAngle() {
        return NRGSensors.GYRO.getAngle();
    }

    /**
     * @return Gets the distance traveled along the X axis since the start of the command
     */
    public static double getDistanceX() {
        return NRGPositionTracker.getInstance().getCurrentX();
    }

    /**
     * @return Gets the distance traveled along the Y axis since the start of the command
     */
    public static double getDistanceY() {
        return NRGPositionTracker.getInstance().getCurrentY();
    }
}
