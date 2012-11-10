package nrg;

/**
 * This class outputs data passed to it to debug code.
 * @author someone
 */
public class NRGDebug
{
    
    /**
     * Constants
     * flag constants that you change to true when you want something to print out.
     */
    public static final boolean DEBUG = false;
//    public static final boolean FATAL_EXCEPTIONS = true;
//    public static final boolean ROBOT_ROUTINES = true;
    public static final int FATAL_EXCEPTIONS = -1;
    public static final int ROBOT_ROUTINES = -1;
    public static final int AUTONOMOUS = 0;
    public static final int IR = 1;
    public static final int DRIVE = 2;
    public static final int JOYSTICK = 3;
    public static final int GYRO = 4;
    public static final int KINECT = 5;
    public static final int MAE = 6;
    public static final int IO = 7;
    public static final int MANIPULATOR = 8;
    public static final int BRIDGE = 9;
    public static final int MAE_CALIBRATION = 10;
    public static final int TIMERS = 11;
    public static final int BALL_COUNTER = 12;
    
    public static final int NUMBER_DEBUGS = 13;
    
    private static int toggleIndex = 0;
    private static boolean[] isEnabled = {true, false, false, false, false, false, false, false, true, false, false, false, false};
    private static final String[] string = {"AUTO", "IR  ", "DRIV", "JOY ", "GYRO", "KNCT", "MAE ", "IO  ", "MPTR", "BRDG", "MAEC", "TIME", "BCTR"};
    
    /**
     * do not initialize
     */
    private NRGDebug()
    {
    }
    
    public static void toggleDebug()
    {
	
	isEnabled[toggleIndex] = !isEnabled[toggleIndex];
	
//	switch (toggleIndex)
//	{
//	    case 0:
//		AUTONOMOUS = !AUTONOMOUS;
//		return;
//	    case 1:
//		IR = !IR;
//		return;
//	    case 2:
//		DRIVE = !DRIVE;
//		return;
//	    case 3:
//		JOYSTICK = !JOYSTICK;
//		return;
//	    case 4:
//		GYRO = !GYRO;
//		return;
//	    case 5:
//		KINECT = !KINECT;
//		return;
//	    case 6:
//		MAE = !MAE;
//		return;
//	    case 7:
//		IO = !IO;
//		return;
//	    case 8:
//		MANIPULATOR = !MANIPULATOR;
//		return;
//	    case 9:
//		BRIDGE = !BRIDGE;
//		return;
//	    case 10:
//		MAE_CALIBRATION = !MAE_CALIBRATION;
//		return;
//	    case 11:
//		TIMERS = !TIMERS;
//		return;
//	}
    }
    
    public static void incrementIndex()
    {
	toggleIndex = (toggleIndex + 1) % 12;
    }
    
    public static String getCurrentString()
    {
	return string[toggleIndex];
    }
    
    public static boolean getCurrentState()
    {
	return isEnabled[toggleIndex];
    }
    
    /**
     * Print a String. Does not print a line return.
     * @param debug the flag
     * @param s the message
     */
    public static void print(int debug, String s)
    {
	if (DEBUG && isEnabled(debug))
	{
	    System.out.print(s);
	}
    }

    /**
     * Prints a string and returns.
     * @param debug the flag
     * @param s the message
     */
    public static void println(int debug, String s)
    {
	if (DEBUG && isEnabled(debug))
	{
	    System.out.println(s);
	}
    }

    public static void printRound(int debug, String s, int maxDecimalPlaces)
    {
	//DO NOT USE THIS METHOD UNLESS YOU ARE PRINTING A DOUBLE/INT ONLY
	//Print the string with set amount of decimals
	if (DEBUG && isEnabled(debug))
	{
	    double stringd = Double.parseDouble(s);
	    double power = NRGMathHelper.pow(10, maxDecimalPlaces);
	    stringd *= power;
	    System.out.print(((int) stringd) / power);
	}
    }
    /**
     * in this method, where you want to place your output numbers after the string should be /0, then in ur double array, put the double corresponding to the /#
     * example, if output[] was a double array that had [joysticks.getDriveXValue(), joysticks.getDriveYValue(), 53.56789]: NRGDebug.printRound(NRGDebug.JOYSTICKS, "joysticks x:/0 y:/1 randomnumber:/2", output, 2)
     * would print "joysticks x:(joysticks x value rounded to 2 places) y:(joysticsk y value rounded to 2 places), random:(53.56789 rounded to 2 places)"
     * @param debug the flag
     * @param s the message WITH number placeholders
     * @param output the array of output values
     * @param maxDecimalPlaces 
     */
    public static void printRoundln(int debug, String s, double[] output, int maxDecimalPlaces)
    {
	//DO NOT USE THIS METHOD UNLESS YOU ARE PRINTING A DOUBLE/INT ONLY
	//Print the string with set amount of decimals
	if (DEBUG && isEnabled(debug))
	{
	    String printString = "";
	    for (int i = 0; i < s.length(); i++)
	    {
		char c = s.charAt(i);
		if (c == '/')
		{
		    int numberint = 0;
		    try
		    {
			numberint = Integer.parseInt(s.substring(i + 1, i + 2));
			double power = NRGMathHelper.pow(10, maxDecimalPlaces);
			printString += ((int) (output[numberint] * power)) / power;
			i++;
		    }
		    catch (Exception e)
		    {
			printException(e);
		    }
		}
		else
		{
		    printString += c;
		}
	    }
	    System.out.println(printString);
	}
    }
    
    /**
     * This takes an exception and prints the (very useful) message and the stack trace
     * @param e the exception thrown
     */
    public static void printException(Exception e)
    {
	System.err.println(e.getMessage());
	e.printStackTrace();
    }
    
    private static boolean isEnabled(int n)
    {
	if (n < 0)
	{
	    return true;
	}
	
	return isEnabled[n];
    }
}
