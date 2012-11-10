package nrg;

import com.sun.squawk.util.MathUtils;

/**
 *
 * @author Foris Kuang
 */
public class NRGMathHelper
{
    /**
     * This takes in the double base and the power that it will be raised to
     * We thought of initializing the power as 1 because anything raised to 0 is 1.
     * @param base
     * @param power
     * @return pow
     */
    public static double pow(double base, int power)
    {
	double pow = 1;

	for (int i = 0; i < Math.abs(power); i++)
	{
	    pow *= base;
	}

	return power >= 0 ? pow : 1 / pow;
    }

    /**
     * This method will round the number to the decimal place specified
     * We used the power method to round the decimal at that location with base 10 and return it
     * @param numberToBeRound
     * @param numberOfDecimal
     * @return decimalRounded
     */
    public static double round(double numberToBeRound, int numberOfDecimal)
    {
	double num = 0;
	double decimalPlace = pow(10.0, numberOfDecimal);
	double decRound = numberToBeRound * decimalPlace;
	int truncated = (int) decRound;
        if (numberToBeRound < 0)
        {
            num = ((decRound - truncated) > -0.5) ? Math.ceil(decRound) : Math.floor(decRound);
        }
        else
	{
	    num = (Math.abs(decRound - truncated) >= 0.5) ? Math.ceil(decRound) : Math.floor(decRound);
	}
        decRound = num / decimalPlace;
        return decRound;

    }
    
    /**
     * @param n number to be rounded
     * @param d number of digits after the decimal
     * @return rounded number with zero padding
     */
    public static String roundPadded(double n, int d)
    {
	String s = Double.toString(round(n, d));
	
	int numAdditional = d - (s.length() - s.indexOf('.') - 1);
	
	for (int i = 0; i < numAdditional; i++)
	{
	    s += "0";
	}
	
	return s;
    }

    /**
     * Takes the reciprocal
     * @param numberToBeReciprocal
     * @return reciprocal of the given number
     */
    public static double reciprocal(double numberToBeReciprocal)
    {
	double reciprocal = (1 / numberToBeReciprocal);
	return reciprocal;
    }

    /**
     * Calculates the factorial (!) of the given integer, defined as the product of all positive
     * integers less-than-or-equal-to the integer. Factorial is not defined for negative integers.
     * Factorial of zero is defined as one.
     * @param factor positive integer
     * @return factorial of given integer
     */
    public static int factorial(int factor)
    {
	if (factor < 0)
	{
	    throw new IllegalArgumentException("Negative integer passed to factorial()");
	}

	int total = 1;

	while (factor > 0)
	{
	    total *= factor--;
	}

	return total;
    }

    /**
     * If the values is in between max and min it will return val
     * If val is greater than max or less than min it will return max or min respectively
     * @param val 
     * @param max
     * @param min
     * @return min max val
     */
    public static int clamp(int val, int min, int max)
    {
        return (val > max) ? max : ((val < min) ? min : val);
    }

    /**
     * same thing as intClamp just double values.
     * @param val
     * @param max
     * @param min
     * @return value between min and max, inclusive
     */
    public static double clamp(double val, double min, double max)
    {
        return (val > max) ? max : ((val < min) ? min : val);
    }

    /**
     * Clamps the magnitude of a value to a max value
     * @param val
     * @param maxMag 
     * @return value between -maxMag and maxMag
     */
    public static double clampMagnitude(double val, double maxMag)
    {
	maxMag = Math.abs(maxMag);  // make sure it is positive
        return (val > maxMag) ? maxMag : ((val < -maxMag) ? -maxMag : val);
    }

     /**
     * Calculates the arithmetic mean of an arbitrary amount of integers
     * @param val
     * @return the arithmetic mean of the given integers
     */
    public static double average(int[] val)
    {
        double total = 0;
        for(int i = 0; i < val.length; i++)
        {
            total +=  val[i];
        }
        double average = total / val.length;
        return average;
    }
    
    /**
     * Fairly self explanatory I would think
     * @param val1 First value
     * @param val2 2nd value
     * @return maximum of 2 values
     */
    public static double max(double val1, double val2)
    {
	return val1 > val2 ? val1 : val2;
    }
    
    // Finds the max of 4 values
    public static double max(double val1, double val2, double val3, double val4)
    {
	return max(max(val1, val2), max(val3, val4));
    }
    
    // Finds the maximum of the absolute values of two numbers
    public static double absMax(double val1, double val2)
    {
	val1 = Math.abs(val1);
	val2 = Math.abs(val2);
	return val1 > val2 ? val1 : val2;
    }
    
    //is the double close to 0
    public static boolean closeToZero(double val)
    {
	final double epsilon = 0.05;
	return (Math.abs(val) < epsilon);
    }
    
    //modified atan2() method that handles the x = 0 case, also converts the atan2() to degrees
    public static double atan2Degree(double y, double x)
    {
	if (x == 0)
	{
	    return (y >= 0) ? 90.0 : 270.0;
	}
	return ((MathUtils.atan2(y, x) * (180 / Math.PI)) + 360) % 360; 
    }
    
    //use when taking sine or cosine of an angle. Remember Math.cos's parameters take in RADIANS, not DEGREES
    public static double degreesToRadians(double val)
    {
	return val * (Math.PI/180);
    }
    //will fix the problem where MAE is going from 359 to 1 and then Drive inversion goes wild
    //instead calculates the real error
    public static double shortestArc(double degree1, double degree2)
    {
	double error = Math.abs(degree1 - degree2);
	if (error > 180)
	{
	    error = 360 - error;
	}
	return error;
    }
    
    //many of our methods(Gyro, MAE, especially) need a normalization to make it within [0, 360) range
    public static double normalizeAngle(double angle)
    {
	angle %= 360;
	if (angle < 0)
	{
	    angle += 360;
	}
	return angle;
    }
    
    /**
     * 
     * Normalizes the specified value from the range [minValue, maxValue] to a
     * value in the range [0.0..1.0]
     * 
     * @param value The value to normalize.
     * @param minValue The minimum value of the unnormalized range.
     * @param maxValue The maximum value of the unnormalized range.
     * @return The specified value scaled to the range [0.0, 1.0]
     */
    public static double normalizeValue(double value, double minValue, double maxValue)
    {
        return (clamp(value, minValue, maxValue) - minValue) / (maxValue - minValue);
    }

    
    /**
     * 
     * Reverses the specified value in the range [minValue, maxValue] and
     * normalizes the result to a value in the range [0.0..1.0]
     * 
     * @param value The value to normalize.
     * @param minValue The minimum value of the unnormalized range.
     * @param maxValue The maximum value of the unnormalized range.
     * @return The specified value scaled to the range [0.0, 1.0]
     */
    public static double reverseAndNormalizeValue(double value, double minValue, double maxValue)
    {
        return 1.0 - normalizeValue(value, minValue, maxValue);
    }
    
    public static double convertToSmallestRelativeAngle(double angle)
    {
        angle %= 360.0;
        if (angle < -180.0)
            angle += 360.0;
        else if (angle > 180.0)
            angle -= 360.0;
        return angle;
    }
}
