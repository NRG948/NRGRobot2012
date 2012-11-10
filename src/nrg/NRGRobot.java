/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package nrg;

import edu.wpi.first.wpilibj.SimpleRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory. 
 */
/**
 * Main class for the robot framework, where the flow of execution begins.
 * @author Eric
 */
public class NRGRobot extends SimpleRobot
{
    public static boolean isOriginalRobot = true; //switch constants between robots
    public static boolean useIOBoard = true; //switch between using the IO board or the backup joystick
    public static boolean enableSwerve = false;
    
    private INRGDataSource joystick;
    private NRGAutonomous autonomous;
//    private INRGComponent test;
    //private INRGComponent drive;
    private static NRGDrive drive;
    private INRGComponent manipulator;
    
    private NRGTimer autoTimer;
    private NRGTimer teleTimer;
    
    private NRGCamera cam;
    /**
     * This is where the global fields are instantiated.
     */
    protected void robotInit()
    {
	NRGSensors.init();
	joystick = new NRGJoystick();
	autonomous = new NRGAutonomous();
	drive = new NRGDrive();
	manipulator = new NRGManipulator();
	//test = new NRGTest();
        
        autoTimer = new NRGTimer("Auto");
        teleTimer = new NRGTimer("TelOp");
        
        //cam = new NRGCamera();
    }

    
    /**
     * The beginning of execution of our program.
     */
    public void robotMain()
    {
	NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Starting to Initialize! THE SUN WILL NEVER SET ON THE BRITISH EMPIRE!");
	robotInit();
	NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Finished Initializing! We are ready to RUMBLE!!!!");

	while (true) 
	{
	    if (isDisabled())
	    {
		NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Entering Disabled mode!");
		disabled();
	    }
	    else if (isAutonomous()) //enter autonomous mode
	    {
		NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Entering Autonomous!");
		autonomous();
	    }
	    else //enter operator control mode
	    {
		NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Entering Teleop!");
		operatorControl();
	    }
	}
    }

    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous()
    {
	drive.init(autonomous);
	manipulator.init(autonomous);

	drive.start();
	manipulator.start();

	autonomous.start();
	autoTimer.enabledOrDisabled(true);
        autoTimer.reset();
        
	while (!isDisabled() && isAutonomous())// && !drive.isComplete() && !arm.isComplete())
	{
            autoTimer.start();
            
	    getWatchdog().feed();
	    NRGLCD.update();
	    try
	    {
		/** @author Mohammad Adib **/ Thread.sleep(100);
	    }
	    catch (Exception e)
	    {
		NRGDebug.printException(e);
	    }
            
            autoTimer.stop();
	}

	autoTimer.enabledOrDisabled(false);
        
        drive.stop();
	manipulator.stop();
        autonomous.stop();
        NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Terminating autonomous.");
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl()
    {
            
	NRGIO.setKnobOffset(); //sets the current position of the knob as 0
	
	drive.init(joystick);
	manipulator.init(joystick);

	drive.start();
	manipulator.start();
        
        teleTimer.enabledOrDisabled(true);
        teleTimer.reset();

	while (!isDisabled() && isOperatorControl())// && !drive.isComplete() && !arm.isComplete())
	{
            teleTimer.start();
            
	    NRGSensors.update();
	    getWatchdog().feed();
            //cam.update();
//	    NRGLCD.println(NRGLCD.JOYSTICK, 1, "Joy y: " + NRGMathHelper.round(joystick.getDriveYValue(),2)
//		    + ", x: " + NRGMathHelper.round(joystick.getDriveXValue(),2));
////	    NRGLCD.println(NRGLCD.JOYSTICK, 2, "Joystick twist: " + NRGMathHelper.round(joystick.getTwistValue(),2));
//
//	    try
//	    {
//		NRGLCD.println(NRGLCD.GYRO, 2, "Gyro: " + NRGMathHelper.round(NRGSensors.GYRO.getAngle(), 2));
//		NRGLCD.println(NRGLCD.MAE, 3,
//			"MAEFL:" + NRGMathHelper.round(NRGSensors.MAE[0][0].getAnalogValue(), 0) +
//			",FR:" + NRGMathHelper.round(NRGSensors.MAE[0][1].getAnalogValue(), 0));
//                NRGLCD.println(NRGLCD.MAE, 4,
//			",BL:" + NRGMathHelper.round(NRGSensors.MAE[1][0].getAnalogValue(), 0) +
//			",BR:" + NRGMathHelper.round(NRGSensors.MAE[1][1].getAnalogValue(), 0));
//	    }
//	    catch (Exception e)
//	    {
//		NRGDebug.println(NRGDebug.FATAL_EXCEPTIONS, "Exception in NRGRobot, MAE get analog value");
//		NRGDebug.printException(e);
//	    }
	    NRGLCD.update();
	    
	    try
	    {
		/** @author Mohammad Adib**/ Thread.sleep(100); //lets the cpu rest for 100 milliseconds
	    }
	    catch (Exception e)
	    {
		System.out.println(e.getMessage());
	    }
            
            teleTimer.stop();
	}

        teleTimer.enabledOrDisabled(false);
        
	NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Terminating teleop.");
	drive.stop();
	manipulator.stop();
    }

    /**
     * This function is called once each time the robot enters disabled mode.
     */
    protected void disabled()
    {
	//Uncomment if testing
//	test.init(joystick);
//	test.start();
	while (isDisabled()) //enter disabled mode
	{
//	    NRGLCD.println(NRGDebug.ROBOT_ROUTINES, 6, "Drive:" + drive.getVersionString() + " Manip:" + manipulator.getVersionString());
	    
	    NRGSensors.update();
	    getWatchdog().feed();

	    try
	    {
		Thread.sleep(100);
	    }
	    catch (InterruptedException ex)
	    {
		NRGDebug.printException(ex);
	    }
	    NRGLCD.update();
	}
//	test.stop();
	NRGDebug.println(NRGDebug.ROBOT_ROUTINES, "Exiting Disabled mode.");
    }
    
    public static NRGDrive getDrive()
    {
        return drive;
    }
}
