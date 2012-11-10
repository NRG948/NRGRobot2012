package nrg;

/**
 * This is the interface that must be implemented for sensors.
 * @author Eric
 */
public interface INRGSensor
{
    /**
     * Gets the current analog value from the sensor.
     * If the sensor doesn't return an analog value by default,
     * it will throw an IllegalAccessException
     * @return the floating point value
     */
    double getAnalogValue() throws IllegalAccessException;

    /**
     * Returns the maximum value for the particular sensor.
     * @return max possible return value
     */
    double getAnalogMax();

    /**
     * Returns the minimum value for the particular sensor.
     * @return min possible return value
     */
    double getAnalogMin();

    /**
     * Gets the current digital value from the sensor.
     * If the sensor doesn't return an digital value by default,
     * it will throw an IllegalAccessException
     * @return the integer value
     */
    int getDigitalValue() throws IllegalAccessException;
}
