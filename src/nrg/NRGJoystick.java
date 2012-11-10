package nrg;

import edu.wpi.first.wpilibj.Joystick;

/*
 * @author Charles C & Eric L
 */
public class NRGJoystick implements INRGDataSource
{
    // joystick objects
    private static Joystick baseJoystick;
    private static Joystick backupJoystick;
    
    // joystick ports
    final static int BASE_JOY_PORT = 1;
    final static int BACKUP_JOY_PORT = 2;
    
    // base joystick buttons
    final static int GYRO_RESET_BUTTON = 7;
    final static int KNOB_RESET_BUTTON = 8;    
    final static int MAE_CALIBRATE_BUTTON = 10;

    // backup joystick buttons
    final static int FL_UPDATE_BUTTON = 2;
    final static int FR_UPDATE_BUTTON = 3;
    final static int BL_UPDATE_BUTTON = 4;
    final static int BR_UPDATE_BUTTON = 5;
    final static int DUMP_POT = 6;
    final static int LOWER_TOWER = 7;
    final static int PID_INITIALIZE_STEERING_BUTTON = 9;
    final static int PID_INTIALIZE_MANIPULATOR_BUTTON = 9;
    final static int CALIBRATE_BRIDGE = 10;
    final static int CALIBRATE_MANIP = 11;

    //TODO: should we make this 2 constants or 1 instance variable
    private static final double JOY_THRESHOLD = 0.1;
    
    // button states, used to keep track of whether a button was "triggered" vs "pressed"
    private static final int NUMBER_BASE_BUTTONS = 12;
    private static final int NUMBER_BACKUP_BUTTONS = 11;
    private static boolean[] baseButtonStates = new boolean[NUMBER_BASE_BUTTONS];
    private static boolean[] backupButtonStates = new boolean[NUMBER_BACKUP_BUTTONS];

    public NRGJoystick()
    {
	baseJoystick = new Joystick(BASE_JOY_PORT);
	backupJoystick = new Joystick(BACKUP_JOY_PORT);
    }

    public void start() // does nothing
    {
    }

    public double getDriveXValue()
    {
	double xVal = baseJoystick.getX();
	
	if (Math.abs(xVal) < JOY_THRESHOLD)
	{
	    xVal = 0.0;
	}
	NRGDebug.println(NRGDebug.JOYSTICK, "NRGJoystick.getDriveX() returning " + xVal);
	return xVal;
    }

    public double getDriveYValue()
    {
	//Default value on joystick is negative when you push forward, so I'm negating it so that
	//we don't have to change all our trig
	double yVal = -1 * baseJoystick.getY();
	if (Math.abs(yVal) < JOY_THRESHOLD)
	{
	    yVal = 0.0;
	}
        NRGDebug.println(NRGDebug.JOYSTICK, "NRGJoystick.getDriveY() returning " + yVal);
	return yVal;
    }

    /**
     * This feeds a PID controller the desired angle,
     * and this class collects the output and returns it as the twist
     * THIS MIGHT NOT WORK
     * @return twist from -1.0 to 1.0
     */
    public double getTwistValue()
    {
	double twist = baseJoystick.getTwist();
	if (Math.abs(twist) < JOY_THRESHOLD)
	{
	    twist = 0.0;
	}
        NRGDebug.println(NRGDebug.JOYSTICK, "NRGJoystick.getTwist() returning " + twist);
	return twist;
    }
    
    public boolean precise()
    {
        return baseJoystick.getTrigger();
    }
    //TODO: finish the analog outputs from the DriveStation
    public boolean shoot()
    {
	return (NRGRobot.useIOBoard) ? NRGIO.getShootPressed() : backupJoystick.getTrigger();
    }

    public double shootSpeed()
    {
	return (NRGRobot.useIOBoard) ? NRGIO.getRawShootSpeed() : ((backupJoystick.getZ() + 1.0) / 2.0);
    }

    public boolean raiseTower()
    {
	return (NRGRobot.useIOBoard) ? NRGIO.getRaiseTower() : backupJoystick.getRawButton(6);
    }

    public int getPossessState()
    {
	return (NRGRobot.useIOBoard) ? NRGIO.getPossessor() : ((backupJoystick.getRawButton(8)) ? 1
		: (backupJoystick.getRawButton(9)) ? -1 : 0);
    }

    public double lowerBridge()
    {
	return (NRGRobot.useIOBoard) ? NRGIO.getBridgeLower() : (NRGMathHelper.clampMagnitude(NRGJoystick.backupJoystick.getZ(), 1) + 1) * 0.5;
    }

    public static boolean calibrate()
    {
	return baseButtonTriggered(MAE_CALIBRATE_BUTTON);
    }
    
    public static boolean manualFLSet()
    {
	return backupJoystick.getRawButton(FL_UPDATE_BUTTON);
    }

    public static boolean manualFRSet()
    {
	return backupJoystick.getRawButton(FR_UPDATE_BUTTON);
    }

    public static boolean manualBLSet()
    {
	return backupJoystick.getRawButton(BL_UPDATE_BUTTON);
    }

    public static boolean manualBRSet()
    {
	return backupJoystick.getRawButton(BR_UPDATE_BUTTON);
    }

    public static boolean baseButtonTriggered(int buttonNumber)
    {
	if (buttonNumber >= 1 && buttonNumber <= NRGJoystick.NUMBER_BASE_BUTTONS)
	{
	    boolean wasPressed = baseButtonStates[buttonNumber - 1];
	    baseButtonStates[buttonNumber - 1] = baseJoystick.getRawButton(buttonNumber);
	    return (!wasPressed && baseButtonStates[buttonNumber - 1]);
	}
	else
	{
	    NRGDebug.println(NRGDebug.JOYSTICK, "Button Number out of range");
	    return false;
	}
    }

    public static boolean backupButtonTriggered(int buttonNumber)
    {
	if (buttonNumber >= 1 && buttonNumber <= NRGJoystick.NUMBER_BACKUP_BUTTONS)
	{
	    boolean wasPressed = backupButtonStates[buttonNumber - 1];
	    backupButtonStates[buttonNumber - 1] = backupJoystick.getRawButton(buttonNumber);
	    return (!wasPressed && backupButtonStates[buttonNumber - 1]);
	}
	else
	{
	    NRGDebug.println(NRGDebug.JOYSTICK, "Button Number out of range");
	    return false;
	}
    }

    //TODO: should this logic be the same as calibrate()?
    public static boolean gyroReset()
    {
	return baseButtonTriggered(GYRO_RESET_BUTTON);
    }

    // Read the button that zeroes the OrientationKnob at it's current position
    public static boolean knobReset()
    {
	return baseButtonTriggered(KNOB_RESET_BUTTON);
    }

    //returns direct heading that driver wants the robot, uses knob to do this
    public double getOrientation()
    {
	double desired = NRGIO.getKnobAngle();
	NRGDebug.println(NRGDebug.JOYSTICK, "NRGJoystick.getOrientation() returning " + desired);
	return desired;
    }

    public boolean useTwist()
    {
	return NRGIO.useTwist();
    }

    /**
     * This returns the backup joystick's y-value (forward is positive, back is negative)
     * @return 
     */
    public static double getBackupJoyY()
    {
	return -backupJoystick.getY();
    }
    
    public static double getBaseJoyY()
    {
	return -baseJoystick.getY();
    }
    
    public static boolean bridgeCalibrate()
    {
	return backupButtonTriggered(CALIBRATE_BRIDGE);
    }
    
    public static boolean dumpBridgePot()
    {
	return backupJoystick.getRawButton(DUMP_POT);
    }
    
    public static boolean calibrateManipulatorMotors()
    {
	return backupJoystick.getRawButton(CALIBRATE_MANIP);
    }
    
//    public static boolean lowerTowerIO()
//    {
//	return backupJoystick.getRawButton(LOWER_TOWER);
//    }
    
    public static double getThrottle()
    {
        return baseJoystick.getThrottle();
    }
}
