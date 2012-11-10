package nrg;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * I had to fix this thing because no one finished it!
 * @author Eric
 */
public class NRGMAE implements INRGSensor, PIDSource
{
    private AnalogChannel m_analog;
    private double m_rawAngle;
    private double m_offset; // from 0.0 to 360.0; any other value = problems
    private boolean m_reverse;
    private boolean m_inverted;
    private double lastAngle;
    private static final double VOLTS_TO_DEGREES = 72.0d * 360.0 / ((NRGRobot.isOriginalRobot) ? 366.1 : 355);
    private static final double RAW_OFFSET = -0.8; //physical offset needed to calc the raw degree

    public void init(int slot, int channel)
    {
	NRGDebug.println(NRGDebug.MAE, "NRGMAE.init() slot=" + slot + " channel=" + channel);
	m_analog = new AnalogChannel(slot, channel);
        
    }

    public synchronized void setOffset(double offset)
    {
	m_offset = offset;
        m_inverted = false;
    }

    public synchronized void invertOffset()
    {
	m_offset = (m_offset + 180) % 360;
        m_inverted = !m_inverted;
        
    }
    public boolean isInverted()
    {
        return m_inverted;
    }
    
    public void clearInversion()
    {
	if (isInverted())
	{
	    invertOffset();
	}
    }
    
    public int getDigitalValue() throws IllegalAccessException
    {
	throw new IllegalAccessException();
    }

    public double getRawAngle()
    {
	if (m_analog != null)
	{
	    double volts = m_analog.getAverageVoltage();

	    // This test was added because we were getting spurious zero volt samples once in
	    // a while. So if this happens, we just return the last 'good' angle we sampled.
//	    if (volts < 0.1)
//		return m_rawAngle;

	    m_rawAngle = volts * VOLTS_TO_DEGREES + RAW_OFFSET;

	    if (this.m_reverse)
	    {
		m_rawAngle = 360.0 - m_rawAngle;
	    }

//	    NRGDebug.println(NRGDebug.MAE, "  " + NRGMathHelper.round(volts, 2) + "V = " + NRGMathHelper.round(m_rawAngle, 1) + "deg  ");
	}
	return m_rawAngle;
    }
    
    public double getAnalogValue() throws IllegalAccessException
    {
        double angle = 0.0;
        if (NRGRobot.enableSwerve)
        {
            angle = (getRawAngle() - m_offset + 360) % 360;
            //NRGDebug.println(NRGDebug.MAE, "m_offset=" + m_offset + " angle=" + angle);
        }
        else
        {
            angle = 90;
        }
	return angle;
    }

    public double getAnalogMax()
    {
	return 360.0;
    }

    public double getAnalogMin()
    {
	return 0.0;
    }

    public double pidGet()
    {
	try
	{
	    lastAngle = getAnalogValue();
	}
	catch (Exception e)
	{
	}
	return lastAngle;
    }
    /*
     * Writing the class to a file so that you guys can later on read it.
     */
}
