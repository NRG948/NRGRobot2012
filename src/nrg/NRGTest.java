//package nrg;
//
//import com.sun.squawk.util.MathUtils;
//import edu.wpi.first.wpilibj.*;
//
///**
// * Contains temporary test code. Right now it's for testing motors without a drive system.
// * @author Eric
// */
//public class NRGTest implements INRGComponent, Runnable
//{
//    private INRGDataSource source;
//    
//    private boolean stop;
//    // This is dangerous, since we might have multiple instances of one motor if testing
//    private Jaguar FLDRIVEMOTOR;
//    private Jaguar BLDRIVEMOTOR;
//    private Jaguar FRDRIVEMOTOR;
//    private Jaguar BRDRIVEMOTOR;
//    private RobotDrive drive;
//    
//    public NRGTest()
//    {
//	FLDRIVEMOTOR = new Jaguar(2, 1);
//	BLDRIVEMOTOR = new Jaguar(2, 2);
//	FRDRIVEMOTOR = new Jaguar(4, 1);
//	BRDRIVEMOTOR = new Jaguar(4, 2);
//	drive = new RobotDrive(FLDRIVEMOTOR, BLDRIVEMOTOR, FRDRIVEMOTOR, BRDRIVEMOTOR);
//    }
//    
//    public void init(INRGDataSource s)
//    {
//	source = s;
//    }
//    
//    public void start()
//    {
//	stop = false;
//	Thread testThread = new Thread(this);
//	testThread.start();
//    }
//    
//    public void stop()
//    {
//	stop = true;
//    }
//    
//    /**
//     * We can test almost anything in here (with obvious exceptions). Assuming we are physically not
//     * able to test the rest of our code.
//     */
//    public void run()
//    {
//	while (source != null && !stop)
//	{
//	    double x = source.getDriveXValue();
//	    double y = source.getDriveYValue();
//	    drive.tankDrive(x, y);
//
//	    //testing some math utilities for drive
//	    System.out.println(MathUtils.atan(4/3));
//	    System.out.println(MathUtils.atan(1/0));
//	    System.out.println(MathUtils.atan2(4, 3));
//	    System.out.println(MathUtils.atan2(1, 0));
//
//	    try
//	    {
//		/** @author Mohammad Adib **/ Thread.sleep(100); //pause the thread
//	    }
//	    catch (Exception e)
//	    {
//		System.out.println(e.getMessage());
//	    }
//	}
//    }
//}
