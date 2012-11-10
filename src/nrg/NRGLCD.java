package nrg;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author Matthew
 */
public class NRGLCD
{
    public static final boolean FATAL_EXCEPTIONS = true;
    public static final boolean ROBOT_ROUTINES = true;
    public static final boolean AUTONOMOUS = false;
    public static final boolean IR = false;
    public static final boolean DRIVE = false;
    public static final boolean JOYSTICK = false;
    public static final boolean GYRO = false;
    public static final boolean KINECT = false;
    public static final boolean MAE = false;
    public static final boolean IO = false;
    public static final boolean MANIPULATOR = false;
    public static final boolean BRIDGE = false;
    public static final boolean CALIBRATION = false;
    public static final boolean PID = false;
    
    private final static DriverStationLCD lcd = DriverStationLCD.getInstance();
    
    //will clear the LCD screen
    public static void clear()
    {
	String empty = "";
	for (byte a = 0; a < DriverStationLCD.kLineLength; a++)
	{
	    empty += "  ";
	}
	println(true, 1, empty);
	println(true, 2, empty);
	println(true, 3, empty);
	println(true, 4, empty);
	println(true, 5, empty);
	println(true, 6, empty);

    }

    //the print method to the classmate/driver station
    //different from last year, instead of making 6 diff println methods, just gonna pass in a parameter with the # of lines
    public static void println(boolean flag, int line, String message)
    {
	if (flag)
	{
	    switch (line)
	    {
		case 1:
		    lcd.println(DriverStationLCD.Line.kMain6, 1, message);
		    break;
		case 2:
		    lcd.println(DriverStationLCD.Line.kUser2, 1, message);
		    break;
		case 3:
		    lcd.println(DriverStationLCD.Line.kUser3, 1, message);
		    break;
		case 4:
		    lcd.println(DriverStationLCD.Line.kUser4, 1, message);
		    break;
		case 5:
		    lcd.println(DriverStationLCD.Line.kUser5, 1, message);
		    break;
		case 6:
		    lcd.println(DriverStationLCD.Line.kUser6, 1, message);
		    break;
	    }
	}
    }

    // updates the "physical" LCD
    public static void update()
    {
	clear();
	updateText();
	
	lcd.updateLCD();
    }
    
    private static void updateText()
    {
	
	String bridgeMode = "DSBL";
	if (NRGManipulator.sbState == NRGManipulator.SBDS_CALIBRATE)
	{
	    bridgeMode = "CALB";
	}
	else if (NRGManipulator.sbState == NRGManipulator.SBDS_PID)
	{
	    bridgeMode = "PID ";
	}
	
	//"G:-XXX.XK:XXX.X ----+"
	// 123456789012345678901
	// first line
	lcd.println(DriverStationLCD.Line.kMain6, 1, "G:");
	lcd.println(DriverStationLCD.Line.kMain6, 3, String.valueOf(NRGSensors.GYRO.getAngle()));
	lcd.println(DriverStationLCD.Line.kMain6, 9, "K:");
	lcd.println(DriverStationLCD.Line.kMain6, 11, String.valueOf(NRGIO.getKnobAngle()));
	lcd.println(DriverStationLCD.Line.kMain6, 16, " ");
	lcd.println(DriverStationLCD.Line.kMain6, 17, NRGDebug.getCurrentString());
	lcd.println(DriverStationLCD.Line.kMain6, 21, NRGDebug.getCurrentState() ? "+" : "-");
	
	// second line
	lcd.println(DriverStationLCD.Line.kUser2, 1, "DR:");
	lcd.println(DriverStationLCD.Line.kUser2, 4, NRGDrive.isTank ? "TANK " : "SWERVE");
	lcd.println(DriverStationLCD.Line.kUser2, 10, " SHOOT:");
	lcd.println(DriverStationLCD.Line.kUser2, 17, NRGMathHelper.roundPadded(NRGManipulator.shootSpeed, 3));
	
	// third line
	lcd.println(DriverStationLCD.Line.kUser3, 1, "POT:");
	lcd.println(DriverStationLCD.Line.kUser3, 5, String.valueOf(NRGManipulator.sbPot));
	lcd.println(DriverStationLCD.Line.kUser3, 8, "/");
	lcd.println(DriverStationLCD.Line.kUser3, 9, String.valueOf(NRGManipulator.pidSet));
	lcd.println(DriverStationLCD.Line.kUser3, 12, " BRDG:");
	lcd.println(DriverStationLCD.Line.kUser3, 18, bridgeMode);
	
	// sixth line
	try
	{
	    lcd.println(DriverStationLCD.Line.kUser6, 1, "F:");
	    lcd.println(DriverStationLCD.Line.kUser6, 3, String.valueOf(NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.LEFT].getAnalogValue()));
	    lcd.println(DriverStationLCD.Line.kUser6, 6, NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.LEFT].isInverted() ? "v" : "^");
	    lcd.println(DriverStationLCD.Line.kUser6, 7, String.valueOf(NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.RIGHT].getAnalogValue()));
	    lcd.println(DriverStationLCD.Line.kUser6, 10, NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.RIGHT].isInverted() ? "v" : "^");
	    lcd.println(DriverStationLCD.Line.kUser6, 11, " B:");
	    lcd.println(DriverStationLCD.Line.kUser6, 14, String.valueOf(NRGSensors.MAE[NRGSensors.BACK][NRGSensors.LEFT].getAnalogValue()));
	    lcd.println(DriverStationLCD.Line.kUser6, 17, NRGSensors.MAE[NRGSensors.BACK][NRGSensors.LEFT].isInverted() ? "v" : "^");
	    lcd.println(DriverStationLCD.Line.kUser6, 18, String.valueOf(NRGSensors.MAE[NRGSensors.BACK][NRGSensors.RIGHT].getAnalogValue()));
	    lcd.println(DriverStationLCD.Line.kUser6, 21, NRGSensors.MAE[NRGSensors.BACK][NRGSensors.RIGHT].isInverted() ? "v" : "^");
	}
	catch (IllegalAccessException ex)
	{
	    System.err.println(ex);
	}
	
	//                                            123456789012345678901
//	lcd.println(DriverStationLCD.Line.kMain6, 1, "G:XXX.X K:XXX.X ----+");
//	lcd.println(DriverStationLCD.Line.kUser2, 1, "DR:SWERVE SHOOT:X.XXX");
//	lcd.println(DriverStationLCD.Line.kUser3, 1, "POT:XXX/XXX BRDG:AAAA");
//	lcd.println(DriverStationLCD.Line.kUser4, 1, "                     ");
//	lcd.println(DriverStationLCD.Line.kUser5, 1, "                     ");
//	lcd.println(DriverStationLCD.Line.kUser6, 1, "F:XXX^XXXv B:XXX^XXXv");
    }
}