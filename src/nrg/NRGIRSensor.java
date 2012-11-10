package nrg;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.SensorBase;

/**
 * 
 * @author Charles L
 */
public class NRGIRSensor extends SensorBase implements INRGSensor
{
    private double minRange;
    private double maxRange;
    // when channel sensor is plugged in, values taken in
    private AnalogChannel m_analogChannel;
    private double m_rawAngle;
    public static final double MAX = 0;
    public static final double CAL_CONSTANT_M = 0; // TODO: need values
    public static final double CAL_CONSTANT_B = 0;
    //Delete this if you're copying it
    public static final int ANALOG_CHANNEL = 2;
    public static final int ANALOG_MODULE = 1;

    // Constructors
    public NRGIRSensor()
    {
	minRange = 0.0;
	maxRange = MAX;
	//m_analogChannel = new AnalogChannel(ANALOG_MODULE, ANALOG_CHANNEL);
	m_rawAngle = 0.0;
    }

    public double getVoltage()
    {
	if (m_analogChannel != null)
	{
	    double voltage = m_analogChannel.getVoltage();
	    NRGDebug.println(NRGDebug.IR, "IR: " + voltage);
	    return voltage;
	}
	return 0.0;
    }

    public double getDistance() // Returns the distance to the wall
    {
	double averageVoltage = m_analogChannel.getAverageVoltage();
	NRGDebug.print(NRGDebug.IR, "IR Voltage: " + NRGMathHelper.round(averageVoltage, 2) + "   ");

	if (averageVoltage < 1E-3)
	{
	    NRGDebug.println(NRGDebug.IR, "voltage out of range");
	    return -1;
	}

	double distance = 0; // TODO: convert voltage to distance
	distance = NRGMathHelper.clamp(distance, minRange, maxRange);
	NRGDebug.println(NRGDebug.IR, "Distance (in.): " + NRGMathHelper.round(distance, 2));

	return distance;
    }

    public double getAnalogValue() throws IllegalAccessException
    {
	return getVoltage(); //or getDistance()
    }

    public double getAnalogMax()
    {
	throw new RuntimeException("Error");
    }

    public double getAnalogMin()
    {
	throw new RuntimeException("Error");
    }

    public int getDigitalValue() throws IllegalAccessException
    {
	throw new IllegalAccessException();
    }
}
