package nrg;

/**
 * Tracks the field-relative position of the robot using the quadrature outputs.
 * 
 * Starting position of robot is (0, 0) unless specified otherwise.
 * 
 * At the moment, we aren't sure if this will even be necessary.
 * 
 * @author Irving
 */
public class NRGPositionTracker
{
    // if it needs to be static, just make all of it static
    private static final NRGPositionTracker tracker = new NRGPositionTracker();
    
    private Vector currentPosition = new Vector(Vector.vCartesian, 0, 0);
    
    private NRGPositionTracker()
    {
    }
    
    public static void setPosition(double newX, double newY)
    {
	NRGPositionTracker.getInstance().currentPosition.x = newX;
	NRGPositionTracker.getInstance().currentPosition.y = newY;
    }
    
    public static NRGPositionTracker getInstance()
    {
	return tracker;
    }
    
    /**
     * Update the position. Also updates the quadratures.
     */
    public void update()
    {
	// local reference...makes code more concise
	NRGQuadrature[][] quadratures = NRGSensors.quadrature;
	NRGMAE[][] MAEs = NRGSensors.MAE;
	
	for (int i = 0; i < 2; i++)
	{
	    for (int j = 0; j < 2; j++)
	    {
		quadratures[i][j].update();
	    }
	}
	
	/*
	 * Steps:
	 * 1. Construct vectors for each swerve
	 * 2. Average the four vectors
	 * 3. Add the vectors to the current position
	 */
	
	// "Construct vectors for each swerve"
	Vector vectors[][] = new Vector[2][2];
	
        double gyro = NRGSensors.GYRO.getAngle();
        
	for (int i = 0; i < 2; i++)
	{
	    for (int j = 0; j < 2; j++)
	    {
		try
		{
                    double mae = MAEs[i][j].getAnalogValue();
                    double theta = NRGMathHelper.normalizeAngle(mae - gyro);
		    vectors[i][j] = new Vector(Vector.vPolar, Math.abs(quadratures[i][j].getLastTickDistance()), theta);
		}
		catch (IllegalAccessException ex)
		{
		    NRGDebug.println(NRGDebug.FATAL_EXCEPTIONS, ex.toString());
		    NRGDebug.println(NRGDebug.FATAL_EXCEPTIONS, "[NRGPositionTracker] caught runtime exception from NRGMAE.getAnalogValue(), exiting!");
		    return;
		}
	    }
	}
	
	// "Average the four vectors"
	Vector average = new Vector(Vector.vCartesian, 0, 0);
	for (int i = 0; i < 2; i++)
	{
	    for (int j = 0; j < 2; j++)
	    {
		average.add(vectors[i][j]);
	    }
	}
	// this is probably how it works.
	average.x /= 4;
	average.y /= 4;
	
	// "Add the vectors to the current position"
	NRGPositionTracker.getInstance().currentPosition.add(average);
	
    }
    
    public double getCurrentX()
    {
	return NRGPositionTracker.getInstance().currentPosition.x;
    }
    
    public double getCurrentY()
    {
	return NRGPositionTracker.getInstance().currentPosition.y;
    }
    
    private class Vector
    {
	public static final int vPolar = 1;
	public static final int vCartesian = 2;
	
	private double x;
	private double y;
	
	public Vector(int type, double arg1, double arg2)
	{
	    if (type == vPolar)
	    {
		// arg1 is radius, arg2 is angle
		x = Math.cos(NRGMathHelper.degreesToRadians(arg2)) * arg1;
		y = Math.sin(NRGMathHelper.degreesToRadians(arg2)) * arg1;
	    }
	    else if (type == vCartesian)
	    {
		// arg1 is x, arg2 is y
		x = arg1;
		y = arg2;
	    }
	}
	
	public void add(Vector v)
	{
	    x += v.x;
	    y += v.y;
	}
    }
}
