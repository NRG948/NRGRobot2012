package nrg;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * @author Foris Kuang and Ben Yan
 * Originally written for practice on Noah
 * Repurposed for 2012
 */
public class NRGGyro implements INRGSensor, PIDSource
{
    private Gyro gyro;
    private double offset;

    /**
     * Initializes the gyro.
     */
    public void init(double sensitivity, int slot, int channel)
    {
	gyro = new Gyro(slot, channel);
	gyro.reset();
	gyro.setSensitivity(sensitivity);
    }

    //This method resets the gyro.
    public void reset()
    {
	gyro.reset();
    }

    public double getAngle()
    {
	return gyro.getAngle() + offset;
    }

    /**
     * Retrieves heading of the robot in respect to the reset.
     * The angle does not go beyond 360 now. The range is 0 to 360 degrees
     * @return the heading the robot is facing 
     */
    public double getAnalogValue()
    {
	//makes sure the return value is always offset between 0 and 360 even with huge raw angles
	double angle = NRGMathHelper.normalizeAngle(gyro.getAngle());
//	alternate implementation
//	double val = 90 - gyro.getAngle() % 360;
//	return (val < 0) ? val + 360 : val;
	NRGDebug.printRoundln(NRGDebug.GYRO, "Gyro angle: /0", new double[] { angle }, 1);
	return angle;
    }

    /**
     * Gets the minimum value that our sensor returns (not the raw value)
     */
    public double getAnalogMin()
    {
	return 0.0;
    }
    
    /**
     * Gets the maximum value that our sensor returns (not the raw value)
     */
    public double getAnalogMax()
    {
	return 360.0;
    }
    
    /**
     * Don't use
     */
    public int getDigitalValue() throws IllegalAccessException
    {
	throw new IllegalAccessException("Gyro doesn't return a digital value");
    }

    public double pidGet()
    {
	return getAnalogValue();
    }
    
    public void setOffset(double angle)
    {
	offset = angle;
    }
}
