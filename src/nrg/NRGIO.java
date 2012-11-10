package nrg;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;

/**
 * @author Ben, Dustin
 * This is for the IO board. Each method will correspond to an element on the board.
 * Write your own methods for getting stuff, the helpers are here.
 */
public class NRGIO 
{
    // This represents the io board.
    private static final DriverStationEnhancedIO io = DriverStation.getInstance().getEnhancedIO();
    
    // digital input channels
    private static final int DRIVE_SWERVE_TANK = 2;
    private static final int TWIST_POT_JOY = 4;
    private static final int GYRO = 6;
    private static final int TOWER = 8;
    private static final int CAMERA = 10;
    private static final int POSSESSOR_ATTRACT = 12;
    private static final int POSSESSOR_REPEL = 14;
    private static final int DELAY = 13;
    private static final int BRIDGE_MODE = 15;
    private static final int SHOOT = 16;
    
    // analog input channels
    private static final int KNOB = 1;
    private static final int SHOOT_BRIDGE = 2;
    private static final int SHOOT_SPEED = 6;
    private static final int TOWER_CONTROL = 4;
            
    private static double knobOffsetDegrees;
    private static final double MIN_CYPRESS_VOLTAGE = -0.001; //TODO: Set these values
    private static final double MAX_CYPRESS_VOLTAGE = 3.312;
    
    //Member variables for the get Knob  previous and last good values
    private static double previousKnobAngle = 0.0;
    private static double lastGoodKnobAngle = 0.0;
    private static int knobCount = 0;
    
    // autonomous switches
    public static final int AUTO_DELAY = 13;
    public static final int AUTO_BRIDGE_CENTER = 9;
    public static final int AUTO_BRIDGE_SIDE = 11;
    public static final int POSITION_LEFT = 1;
    public static final int POSITION_CENTER = 3;
    public static final int POSITION_RIGHT = 5;
    public static final int POSITION_FRONT_BACK = 7;
    
    private static final double SHOOT_SPEED_DEAD_ZONE = 0.1;
    private static final double MINIMUM_SHOOT_SPEED = 0.3;
    /**
     * | o o o  |Drivers Station| o o o  |
     * -----------------------------------
     * 
     * 
     *             |--------|
     *             |  Back  |
     *     |-------|--------|-------|
     *     | Right | Front  |  Left |
     *     |-------|--------|-------|
     * 
     *             _--------_
     *            /          \
     *          --    KEY     --
     *        _/                \_
     *        |                  |
     * -------|------------------|--------
     */
    // Helper method for setting digital output on a specific channel
    private static void setDigital(int channel, boolean value)
    {
        try
        {
            io.setDigitalOutput(channel, value);            
        }
        catch(EnhancedIOException ex)
        {
	    NRGDebug.println(NRGDebug.IO, ex.getMessage());
        }
    }
    
    // Helper method for getting digital input on a specific channel   
    static boolean getDigital(int channel)
    {
        boolean value = false;
        try
        {
            value = io.getDigital(channel);
        }
        catch(EnhancedIOException ex)
        {
            NRGDebug.println(NRGDebug.IO, ex.getMessage());
        }
        return value;
    }
    
    // Helper method for getting analog input on a specific channel
    static double getAnalog(int channel)
    {
        double value = 0.0;
        try
        {
            value = io.getAnalogIn(channel);
        }
        catch(EnhancedIOException ex)
        {
            NRGDebug.println(NRGDebug.IO, ex.getMessage());
        }
        return value;
    }
    
    /**
     * Reads the current raw knob position in volts.
     */
    public static double getRawKnobVolts()
    {
        return getAnalog(KNOB);
    }

    /**
     * Reads the current raw knob position in degrees (0 <= degress < 360)
     */
    private static double getRawKnobDegrees()
    {
        return NRGMathHelper.normalizeValue(getRawKnobVolts(), MIN_CYPRESS_VOLTAGE, MAX_CYPRESS_VOLTAGE) * 359.99;
    }

    /**
     * Returns the angle (supposedly) of the driver knob.
     * Might have to filter the angle eventually...
     * TODO: test the knob's output
     * @return 
     */
    public static double getKnobAngle()
    {
        int stableCount = 4;
        double maxDelta = 2.0;
        double knobAngle = NRGMathHelper.normalizeAngle(getRawKnobDegrees() - knobOffsetDegrees);
        double deltaKnobAngle = NRGMathHelper.shortestArc(knobAngle, previousKnobAngle); 
        
        if(deltaKnobAngle < maxDelta)
        {
            knobCount++;
            if(knobCount >= stableCount)
            {
                lastGoodKnobAngle = knobAngle;
            }
        }
        else
        {
            knobCount = 0;
        }
        previousKnobAngle = knobAngle;
        return lastGoodKnobAngle;
    }
    
    /**
     * Sets the current knob position as the zero position. Should be called whenever teleop starts.
     */
    public static void setKnobOffset()
    {
	knobOffsetDegrees = getRawKnobDegrees();
    }
    
    /**
     * Gets the state of one of the AUTONOMOUS switches on the dashboard.
     * 
     * NOTE: This method is *ONLY* to be used by autonomous mode.
     * 
     * @param switchNumber the number of the switch, use the constants in NRGIO
     * @return boolean indicating the state of a switch
     */
    public static boolean getSwitch(int switchNumber)
    {
        switch (switchNumber)
        {
            case POSITION_LEFT:
            case POSITION_CENTER:
            case POSITION_RIGHT:
            case POSITION_FRONT_BACK:
            case AUTO_BRIDGE_CENTER:
            case AUTO_BRIDGE_SIDE:
            case AUTO_DELAY:
                return !getDigital(switchNumber);

            default:
                return false;
        }
    }
    
    /**
     * Gets the state of the possessor 3-way switch.
     * @return 1 if attract, 0 if none, -1 if repel
     */
    public static int getPossessor()
    {
        boolean attract = getDigital(POSSESSOR_ATTRACT);
        boolean repel = getDigital(POSSESSOR_REPEL);
        if(attract && !repel)
            return 1;
        else if(!attract && repel)
            return -1;
        else
            return 0;
    }
    
    /**
     * Gets the state of the shoot button.
     * @return whether the button is being pressed
     */
    public static boolean getShootPressed()
    {
        return !getDigital(SHOOT);
    }
    
    public static double getBridgeLower()
    {
	return NRGMathHelper.reverseAndNormalizeValue(NRGIO.getAnalog(NRGIO.SHOOT_BRIDGE), MIN_CYPRESS_VOLTAGE, MAX_CYPRESS_VOLTAGE);
    }
    
    public static double getRawShootSpeed()
    {
        double speed = NRGMathHelper.reverseAndNormalizeValue(NRGIO.getAnalog(NRGIO.SHOOT_SPEED), MIN_CYPRESS_VOLTAGE, MAX_CYPRESS_VOLTAGE);
        if (speed < SHOOT_SPEED_DEAD_ZONE)
	{
            speed = 0;
	}
        else
        {
             speed = speed * (1 - MINIMUM_SHOOT_SPEED) + MINIMUM_SHOOT_SPEED;
	}
	return speed;
    }
    
    public static boolean getRaiseTower()
    {
	//return !getDigital(TOWER);
        double towerControl = NRGMathHelper.reverseAndNormalizeValue(NRGIO.getAnalog(NRGIO.TOWER_CONTROL), MIN_CYPRESS_VOLTAGE, MAX_CYPRESS_VOLTAGE);
        return (towerControl > 0.75);
    }
    
    public static boolean getLowerTower()
    {
        double towerControl = NRGMathHelper.reverseAndNormalizeValue(NRGIO.getAnalog(NRGIO.TOWER_CONTROL), MIN_CYPRESS_VOLTAGE, MAX_CYPRESS_VOLTAGE);
        return (towerControl < 0.25);
    }
    
    public static boolean useTwist()
    {
	return getDigital(TWIST_POT_JOY);
    }
    
    public static boolean isSwerveMode()
    {
	return NRGRobot.enableSwerve && !getDigital(DRIVE_SWERVE_TANK);
        //return false;
    }

    public static boolean gyroEnabled()
    {
	return !getDigital(GYRO);
    }
    
    public static boolean bridgeStartInPID()
    {
	return !getDigital(BRIDGE_MODE);
    }
}