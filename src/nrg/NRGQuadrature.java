package nrg;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Handles quadrature outputs from "incremental rotary encoders," encoders which have two outputs
 * that are offset 90 degrees. This allows us to differentiate between clockwise and
 * counter-clockwise rotation.
 * 
 * Should probably only be interfaced through NRGPositionTracker.
 * 
 * @author Irving
 */
public class NRGQuadrature
{
    
    /*
     * <THINKING-ALOUD>
     * I don't really know how this class will shape up, so some of this might be wishful/wrong
     * thinking. For instance, will the location affect this class, or position tracker? Etc.
     * 
     * One thing I need to clarify is how the encoder increments...It seems that every time the
     * robot changes direction, we need to reset them? Because you can run 10 feet, but it might be
     * 5 feet straight forward, 5 feet at an angle...OR, the distance at the previous tick needs to
     * be stored. Hmm. In either case, we need to make sure that some method in this class is
     * called every tick.
     * </THINKING-ALOUD>
     */
    
    // TODO: calibrate distancePerPulse for the quadratures
    // For clarity, this could be, say, inchesPerPulse, metersPerPulse...
    //0.52383
    private static final double inchesPerPulse = 0.25 * (32/36);
    //private static final double inchesPerPulse = 0.25 * (36/32);
    
    private Encoder encoder;
    
    private static final int FRONT = 0;
    private static final int BACK = 1;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    
    private double lastDistance;
    private double currentDistance;
    
    private double cumDistance;
    
    private double m_offset;
    
    /**
     * Constructor for an NRGQuadrature object.
     * @param slot module of the quadrature
     * @param aChannel channel for A output
     * @param bChannel channel for B output
     */
    public NRGQuadrature(int slot, int aChannel, int bChannel)
    {
	encoder = new Encoder(slot, aChannel, slot, bChannel);
	
	encoder.setDistancePerPulse(inchesPerPulse);
	encoder.reset();
	encoder.start();
	
	cumDistance = 0;
	
	currentDistance = encoder.getDistance();
	this.update();
	
	m_offset = 0.0;
	
    }
    
    /**
     * Updates this quadrature.
     * 
     * The goal is to keep numbers consistent by only taking one value in every program loop.
     */
    public void update()
    {
	lastDistance = currentDistance;
	currentDistance = encoder.getDistance();
	
	cumDistance += Math.abs(currentDistance - lastDistance);
    }
    
    /**
     * 
     * @return returns the distance reported by the encoder at the last update
     */
    public double getDistance()
    {
	return currentDistance;
    }
    
    /**
     * 
     * @return returns the distance traveled between the two most recent updates
     */
    public double getLastTickDistance()
    {
	return currentDistance - lastDistance;
    }
    
    /**
     * 
     * @return returns total distance traveled, according to the encoder
     */
    public double getAccumulativeDistance()
    {
	return cumDistance;
    }
    
    public void setOffset(double offset)
    {
	m_offset = offset;
    }
    
    public void invertOffset()
    {
	m_offset = (currentDistance - lastDistance) * -1;
    }
}
