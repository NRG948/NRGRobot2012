package nrg;

import edu.wpi.first.wpilibj.*;
/**
 *  Autonomous uses this class to find when 2 shots have been made
 * @author Matthew
 */
public class NRGBallCounter
{
    private static double lastReading;
    //0.6 is the value that determines shoot
    //so therefore, if voltage crosses 0.6, then it counts as a "shot"
    private static int numShots;
    //private static NRGIRSensor irSensorTest = new NRGIRSensor();
    public static final int ANALOG_CHANNEL = 2;
    public static final int ANALOG_MODULE = 1;
    private static AnalogChannel m_analogChannel = new AnalogChannel(ANALOG_MODULE, ANALOG_CHANNEL);

    public NRGBallCounter()
    {
    }
    
    public static void updateShots()
    {
        double reading = m_analogChannel.getAverageVoltage(); //or try getAverageValue() or try getVoltage()
        NRGDebug.println(NRGDebug.BALL_COUNTER, "Voltage: " + reading);
        if (reading < 0.85 && lastReading > 0.85)
        {
            numShots += 1;
            NRGDebug.println(NRGDebug.BALL_COUNTER, "KABOOM! " + numShots + " balls shot!");
        }
        lastReading = reading;
    }
    
    public static double getNumShots()
    {
        return numShots;
    }
    
    public static void reset()
    {
        numShots = 0;
    }
}
