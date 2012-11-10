package nrg;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * I'm going to create DrivePID here
 * cypress board - switches on DI
 * 
 * FL = front left
 * FR = front right
 * BL = back left
 * BR = back right
 * 
 * @author Matthew
 */
public class NRGDrive implements INRGComponent, Runnable, INRGPIDSet, PIDOutput
{
    //these are accurate, plz do not change
    private static final int MOTOR_FL_CHANNEL = 1;
    private static final int MOTOR_BL_CHANNEL = 2;
    private static final int MOTOR_FR_CHANNEL = 1;
    private static final int MOTOR_BR_CHANNEL = 2;
    private static final int LEFT_SIDE_MOTOR_MODULE = 1;
    private static final int RIGHT_SIDE_MOTOR_MODULE = 2;
    private static final int ANGLE_FL_CHANNEL = 3;
    private static final int ANGLE_BL_CHANNEL = 4;
    private static final int ANGLE_FR_CHANNEL = 3;
    private static final int ANGLE_BR_CHANNEL = 4;

    //assuming that we're using jaguars for everything(angle and drive)
    private Jaguar FLDRIVEMOTOR; //CIM
    private Jaguar BLDRIVEMOTOR; //CIM
    private Jaguar FRDRIVEMOTOR; //CIM
    private Jaguar BRDRIVEMOTOR; //CIM
    private Jaguar FLANGLEMOTOR; //Banebots
    private Jaguar BLANGLEMOTOR; //Banebots
    private Jaguar FRANGLEMOTOR; //Banebots
    private Jaguar BRANGLEMOTOR; //Banebots
    private static final boolean REVERSE = false;
    //TODO: PID constants, make sure to tune them
    //PID tolerance 
    private static final double P = (NRGRobot.isOriginalRobot) ? 0.01 : -0.01; //proportional
    private static final double I = (NRGRobot.isOriginalRobot) ? (P / 10) : (P / 10); //integral
    private static final double D = (NRGRobot.isOriginalRobot) ? 0.0 : 0.0; //derivative
    //TODO: Not calibrated
    private static final double P2 = 0.01; //proportional
    private static final double I2 = (P / 10); //integral
    private static final double D2 = 0.0; //derivative
    private NRGMAE MAE_FL;
    private NRGMAE MAE_BL;
    private NRGMAE MAE_FR;
    private NRGMAE MAE_BR;
    private NRGPID pidAngleFL;
    private NRGPID pidAngleBL;
    private NRGPID pidAngleFR;
    private NRGPID pidAngleBR;
    
    private PIDController orientationPID; //pid to use orientation to set heading of robot
    //TODO: Tune value 
    private static final double PID_PERIOD = 0.05;
    //Need PID for the angles, not the speed/drive tho
    protected final int m_invertedMotors[] = new int[8];
    public boolean stop;
    private INRGDataSource dataSource;
    //distances should be measured from CENTER
    //28 BY 18 is widht and length of robot
    private static final double RADIUSX = 18 / 2;
    private static final double RADIUSY = 28 / 2;
    private static final double RADIUS = Math.sqrt(RADIUSX * RADIUSX + RADIUSY * RADIUSY);
    private static final double K_TWISTX = RADIUSX / RADIUS;
    private static final double K_TWISTY = RADIUSY / RADIUS;
    //TODO: Calibrate this value
    //Reason of making this 10: 10% of error in case joystick is accidentally pushed, the robot doesnt always stop, then fix heading, then move again
    //SO allows it to move with at most 10% error in wheel direction
    private final double SWERVE_STEERING_TOLERANCE = 0.5; //TODO: Use NRGOriginalRobot?
    private final double TANK_STEERING_TOLERANCE = 1.0;
    //tune this value as well
    private final double STEERING_TOLERANCE_ANGLE = 10.0; // in degrees
    private double originalPower;
    private double desiredPower;
    //robot starts with all wheels relative the same way
    //this is used to determine whether robots motors are inverted so that it goes opposite direction easier

    private static final int THREAD_PAUSE = 50; //milliseconds
    //Member variables for PID Reader
    private NRGPIDReader PIDReader;
    protected boolean m_isCANInitialized = true;
    private static final String DRIVE_PID_FILE = (NRGRobot.isOriginalRobot) ? "file:///DrivePID.txt" : "file:///DrivePID2.txt";
    final static double PRECISE_MODIFIER = .6;
    /*
    private MotorSafetyHelper FLDriveMotorSafety = new MotorSafetyHelper(FLDRIVEMOTOR);
    private MotorSafetyHelper FRDriveMotorSafety = new MotorSafetyHelper(FRDRIVEMOTOR);
    private MotorSafetyHelper BLDriveMotorSafety = new MotorSafetyHelper(BLDRIVEMOTOR);
    private MotorSafetyHelper BRDriveMotorSafety = new MotorSafetyHelper(FLDRIVEMOTOR);
    private MotorSafetyHelper FLAngleMotorSafety = new MotorSafetyHelper(FLDRIVEMOTOR);
    private MotorSafetyHelper FRAngleMotorSafety = new MotorSafetyHelper(FLDRIVEMOTOR);
    private MotorSafetyHelper BLMotorSafetyHelper = new MotorSafetyHelper(FLDRIVEMOTOR);
    private MotorSafetyHelper BRMotorSafetyHelper = new MotorSafetyHelper(FLDRIVEMOTOR); */
    
    private NRGTimer driveTimer;
    
    public static boolean isTank = false;
            
    private double m_maximumOutput = 1.0;   // motor maximum output
    private double m_minimumOutput = -1.0;  // motor minimum output
    private double m_error = 0.0;           // the current error term
    private double m_prevError = 0.0;	    // the prior error (used to compute velocity)
    private double m_totalError = 0.0;      // the sum of the errors for use in the integral calc
    private double m_result = 0.0;          // the sum of the P+I+D terms
    
    private boolean m_enabled = false;      // is the pid controller currently enabled?
    private Encoder leftEncoder;            // encoder to read for the left track
    private Encoder rightEncoder;           // encoder to read for the right track
    
    private int prevEncL = 0;               // value of the left track encoder from the prev iteration
    private int prevEncR = 0;               // value of the right track encoder from the prev iteration

    public NRGDrive()
    {
	FLDRIVEMOTOR = new Jaguar(LEFT_SIDE_MOTOR_MODULE, MOTOR_FL_CHANNEL); //CIM
	BLDRIVEMOTOR = new Jaguar(LEFT_SIDE_MOTOR_MODULE, MOTOR_BL_CHANNEL);
	FRDRIVEMOTOR = new Jaguar(RIGHT_SIDE_MOTOR_MODULE, MOTOR_FR_CHANNEL);
	BRDRIVEMOTOR = new Jaguar(RIGHT_SIDE_MOTOR_MODULE, MOTOR_BR_CHANNEL);

	FLANGLEMOTOR = new Jaguar(LEFT_SIDE_MOTOR_MODULE, ANGLE_FL_CHANNEL);
	BLANGLEMOTOR = new Jaguar(LEFT_SIDE_MOTOR_MODULE, ANGLE_BL_CHANNEL);
	FRANGLEMOTOR = new Jaguar(RIGHT_SIDE_MOTOR_MODULE, ANGLE_FR_CHANNEL);
	BRANGLEMOTOR = new Jaguar(RIGHT_SIDE_MOTOR_MODULE, ANGLE_BR_CHANNEL);

	MAE_FL = NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.LEFT]; // 0,0
	MAE_BL = NRGSensors.MAE[NRGSensors.BACK][NRGSensors.LEFT]; // 1, 0
	MAE_FR = NRGSensors.MAE[NRGSensors.FRONT][NRGSensors.RIGHT]; //0, 1
	MAE_BR = NRGSensors.MAE[NRGSensors.BACK][NRGSensors.RIGHT]; // 1, 1

	pidAngleFL = new NRGPID(P, I, D, MAE_FL, FLANGLEMOTOR, PID_PERIOD, "flAng");
	pidAngleBL = new NRGPID(P, I, D, MAE_BL, BLANGLEMOTOR, PID_PERIOD, "blAng");
	pidAngleFR = new NRGPID(P, I, D, MAE_FR, FRANGLEMOTOR, PID_PERIOD, "frAng");
	pidAngleBR = new NRGPID(P, I, D, MAE_BR, BRANGLEMOTOR, PID_PERIOD, "blAng");

	pidAngleFL.setInputRange(0, 360);
	pidAngleBL.setInputRange(0, 360);
	pidAngleFR.setInputRange(0, 360);
	pidAngleBR.setInputRange(0, 360);
	pidAngleFL.setContinuous();
	pidAngleBL.setContinuous();
	pidAngleFR.setContinuous();
	pidAngleBR.setContinuous();
	pidAngleFL.setTolerance(SWERVE_STEERING_TOLERANCE);
	pidAngleBL.setTolerance(SWERVE_STEERING_TOLERANCE);
	pidAngleFR.setTolerance(SWERVE_STEERING_TOLERANCE);
	pidAngleBR.setTolerance(SWERVE_STEERING_TOLERANCE);
	pidAngleFL.setDegreeTolerance(STEERING_TOLERANCE_ANGLE);
	pidAngleBL.setDegreeTolerance(STEERING_TOLERANCE_ANGLE);
	pidAngleFR.setDegreeTolerance(STEERING_TOLERANCE_ANGLE);
	pidAngleBR.setDegreeTolerance(STEERING_TOLERANCE_ANGLE);
	
	orientationPID = new PIDController(P2, I2, D2, NRGSensors.GYRO, this);
	orientationPID.setInputRange(0, 360);
	orientationPID.setOutputRange(-1.0, 1.0);
	orientationPID.setContinuous();
	orientationPID.setTolerance(SWERVE_STEERING_TOLERANCE);

	FLDRIVEMOTOR.setExpiration(1.0);
	FRDRIVEMOTOR.setExpiration(1.0);
	BLDRIVEMOTOR.setExpiration(1.0);
	BRDRIVEMOTOR.setExpiration(1.0);

	FLANGLEMOTOR.setExpiration(1.0);
	FRANGLEMOTOR.setExpiration(1.0);
	BLANGLEMOTOR.setExpiration(1.0);
	BRANGLEMOTOR.setExpiration(1.0);
        
        PIDReader = new NRGPIDReader(DRIVE_PID_FILE,NRGJoystick.PID_INITIALIZE_STEERING_BUTTON,this);
        
        driveTimer = new NRGTimer("drive");
    }

    public INRGDataSource getDataSource()
    {
	return dataSource;
    }

    public void pidWrite(double output)
    {
    }
    
    public void start()
    {
	//starts a new thread
	Thread driveThread = new Thread(this);
	stop = false;
	driveThread.start();
        
        driveTimer.enabledOrDisabled(true);
        driveTimer.reset();
    }

    public void stop()
    {
        driveTimer.enabledOrDisabled(false);
        
	stop = true;
	pidAngleFL.disable();
	pidAngleBL.disable();
	pidAngleFR.disable();
	pidAngleBR.disable();
	orientationPID.disable();
    }

    //instead of instantiating the datasource in update, we will do start instead  
    public void init(INRGDataSource input)
    {
	//stop = false;

	//exe = new NRGDriveRunnable();
	dataSource = input;
	orientationPID.enable();
        PIDReader.init();
	/**
	try
	{
	pidAngleFL.setSetpoint(MAE_FL.getAnalogValue());
	pidAngleFR.setSetpoint(MAE_FR.getAnalogValue());
	pidAngleBL.setSetpoint(MAE_BL.getAnalogValue());
	pidAngleBR.setSetpoint(MAE_BR.getAnalogValue());
	}
	catch (Exception e)
	{
	NRGDebug.println(NRGDebug.DRIVE, "Drive, PID 1st setting of setpoint(b4 enable):" + e.getMessage());
	} **/
//	pidAngleFL.enable();
//	pidAngleBL.enable();
//	pidAngleFR.enable();
//	pidAngleBR.enable();
	//data = input;
	originalPower = 0;
    }

    //little confused: the first if method must have a condition that checks if the data source != null
    //but since run does not pass in a data source as parameter, i guess i have to get it from exe?
    //called only once?
    public void run()
    {
        driveTimer.start();
	
	boolean wasTank = false, tankOnTarget = false;
        
        //this has the potential to cause fatal errors with drive
	NRGDebug.println(NRGDebug.DRIVE, "Drive has entered thread");
	while (getDataSource() != null && !stop)
	{
            //if this doesn't work, then plz move this down to the else(swerve vs tank one)
            if (!NRGIO.bridgeStartInPID())
            {
                tankDrive(0, 0);
                continue;
            }
            //essentially this allows resetting of wheels
	    //TODO: disable, enable might not work because itll just set it to original value anyway
	    //making the manual setpoint completely useless
 	    if (NRGJoystick.manualFLSet())
	    {
		pidAngleFL.disable();
		FLANGLEMOTOR.set(NRGJoystick.getBackupJoyY());
		sleep(50);
		continue;
	    }
	    if (NRGJoystick.manualBLSet())
	    {
		pidAngleBL.disable();
		BLANGLEMOTOR.set(NRGJoystick.getBackupJoyY());
		sleep(50);
		continue;
	    }
	    if (NRGJoystick.manualFRSet())
	    {
		pidAngleFR.disable();
		FRANGLEMOTOR.set(NRGJoystick.getBackupJoyY());
		sleep(50);
		continue;
	    }
	    if (NRGJoystick.manualBRSet())
	    {
		pidAngleBR.disable();
		BRANGLEMOTOR.set(NRGJoystick.getBackupJoyY());
		sleep(50);
		continue;
	    }
	    
            PIDReader.update();
	    
	    if (NRGLCD.DRIVE)
	    {
		NRGLCD.println(NRGLCD.DRIVE, 3, "MAE inv:" + (MAE_FL.isInverted() ? "v" : "^") + (MAE_FR.isInverted() ? "v" : "^") + (MAE_BL.isInverted() ?"v" : "^") + (MAE_BR.isInverted() ? "v" : "^"));
	    }
	    
	    if (NRGRobot.enableSwerve && NRGIO.isSwerveMode())
	    {
		NRGDrive.isTank = false;
                if (wasTank)
		{
		    pidAngleFL.setTolerance(SWERVE_STEERING_TOLERANCE);
		    pidAngleBL.setTolerance(SWERVE_STEERING_TOLERANCE);
		    pidAngleFR.setTolerance(SWERVE_STEERING_TOLERANCE);
		    pidAngleBR.setTolerance(SWERVE_STEERING_TOLERANCE);
		}
		
		double gyroHeading = NRGIO.gyroEnabled() ? NRGMathHelper.normalizeAngle(NRGSensors.GYRO.getAngle()) : 0.0;;
//		NRGDebug.println(NRGDebug.DRIVE, "gyro: " + gyroHeading);
		double translationalX = dataSource.getDriveXValue();
		double translationalY = dataSource.getDriveYValue(); //combination of x and y twists wheels
		double rotationomega = 0.0;
		if (dataSource.useTwist())
		{
//                    rotationomega = 0.0; //@bugbug
		    rotationomega = dataSource.getTwistValue(); //twists robot, knob vs joystick
		}
		else //use orientation, pid
		{
		    orientationPID.setSetpoint(dataSource.getOrientation());
		    rotationomega = orientationPID.get(); //gets orientation PID value to pass to twist
		}

		swerveDrive(translationalX, translationalY, rotationomega, gyroHeading);

		wasTank = false;
		
		NRGLCD.println(NRGLCD.DRIVE, 2, "SWERVE");
		
	    }
	    else
	    {
		
		isTank = true;
		
		//should we be making our own tankDrive method?
		//I'm not sure simply getting Y will do it, but assumingly that is how tank is driven
//                drive.tankDrive(NRGJoystick.baseJoystick.getY(), NRGJoystick.backupJoystick.getY());
		
		if (!wasTank)
		{
		    FLDRIVEMOTOR.set(0);
		    BLDRIVEMOTOR.set(0);
		    FRDRIVEMOTOR.set(0);
		    BRDRIVEMOTOR.set(0);
		    if (NRGRobot.enableSwerve)
                    {
                        pidAngleFL.enable();
                        pidAngleBL.enable();
                        pidAngleFR.enable();
                        pidAngleBR.enable();

                        pidAngleFL.setTolerance(TANK_STEERING_TOLERANCE);
                        pidAngleBL.setTolerance(TANK_STEERING_TOLERANCE);
                        pidAngleFR.setTolerance(TANK_STEERING_TOLERANCE);
                        pidAngleBR.setTolerance(TANK_STEERING_TOLERANCE);

                        setWheelAngle(pidAngleFL, 90, MAE_FL);
                        setWheelAngle(pidAngleBL, 90, MAE_BL);
                        setWheelAngle(pidAngleFR, 90, MAE_FR);
                        setWheelAngle(pidAngleBR, 90, MAE_BR);
                        tankOnTarget = false;
                    }
		    else
                    {
                        tankOnTarget = true;
                    }
		    
		    
		    NRGDebug.println(NRGDebug.DRIVE, "[Drive] Entering tank drive...");
		    
		    NRGLCD.println(NRGLCD.DRIVE, 2, "ENTERING_TANK");
		}
		
		if (tankOnTarget)
		{
		    pidAngleFL.disable();
		    pidAngleBL.disable();
		    pidAngleFR.disable();
		    pidAngleBR.disable();
		    
		    if (dataSource.precise())
		    {
			tankDrive(NRGJoystick.getBaseJoyY() * PRECISE_MODIFIER, NRGJoystick.getBackupJoyY() * PRECISE_MODIFIER);
		    }
		    else
		    {
			tankDrive(NRGJoystick.getBaseJoyY(), NRGJoystick.getBackupJoyY());
		    }
		    
		    NRGLCD.println(NRGLCD.DRIVE, 2, "TANK_ON_TARGET");
		}
		else
		{
                    
		    disableIfOnTarget(pidAngleFL);
		    disableIfOnTarget(pidAngleBL);
		    disableIfOnTarget(pidAngleFR);
		    disableIfOnTarget(pidAngleBR);
		    
		    if (NRGRobot.enableSwerve || (pidAngleFL.onTarget() && pidAngleBL.onTarget() && pidAngleFR.onTarget() && pidAngleBR.onTarget()))
		    {
			tankOnTarget = true;
			NRGDebug.println(NRGDebug.DRIVE, "[Drive] Entered tank drive.");
			
			NRGLCD.println(NRGLCD.DRIVE, 2, "ENTERED_TANK");
		    }
		}
		
		wasTank = true;
		
	    }

            driveTimer.stop();
            
	    //TODO: a make-sure pause in the thread so that it doesn't loop too fast, need this?
	    /** @author Mohammad Adib **/
	    sleep(THREAD_PAUSE);
//	    Watchdog.getInstance().feed();
        }
    }

    //might have to change this now that we have a knob, the rotationOmega will be asolute etc, ask Mr Reed
    public void swerveDrive(double translationalX, double translationalY, double rotationOmega, double gyroHeading)
    {
        boolean fullpower = true;
	if (NRGMathHelper.closeToZero(translationalX) 
	 && NRGMathHelper.closeToZero(translationalY)
	 && NRGMathHelper.closeToZero(rotationOmega))
	{
	    FLDRIVEMOTOR.set(0);
	    BLDRIVEMOTOR.set(0);
	    FRDRIVEMOTOR.set(0);
	    BRDRIVEMOTOR.set(0);

	    FLANGLEMOTOR.set(0);
	    BLANGLEMOTOR.set(0);
	    FRANGLEMOTOR.set(0);
	    BRANGLEMOTOR.set(0);
            
//    	    pidAngleFL.disable();
//	    pidAngleBL.disable();
//	    pidAngleFR.disable();
//	    pidAngleBR.disable();
            pidAngleFL.reset();
	    pidAngleBL.reset();
	    pidAngleFR.reset();
	    pidAngleBR.reset();

	    return;
	}
        if (dataSource.precise())
        {
            fullpower = false;
            translationalX *= PRECISE_MODIFIER;
            translationalY *= PRECISE_MODIFIER;
            rotationOmega *= PRECISE_MODIFIER;
        }
//	pidAngleFL.enable();
//	pidAngleBL.enable();
//        pidAngleFR.enable();
//        pidAngleBR.enable();
	NRGDebug.printRoundln(NRGDebug.DRIVE, "BR: set:/0, in:/1, error:/2, out:/3",
			    new double[]
		{
		    pidAngleBR.getSetpoint(), getAnalogValueFor(MAE_BR), pidAngleBR.getError(), pidAngleBR.get()
		}, 2);
	
	//magnitude of joystick inputs
	//might not work because we're using atan2Degree?
	double translationalR = Math.sqrt(translationalX * translationalX + translationalY * translationalY);
	double translationalTheta = NRGMathHelper.atan2Degree(translationalY, translationalX);
	
	// gyroHeading = raw gyro angle, plus or minus, 0 straight ahead
	double relativeTheta = NRGMathHelper.normalizeAngle(translationalTheta + gyroHeading);
	//double relativeTheta = (translationalTheta - (90 - gyroHeading) + 360) % 360;
	double relativeX = translationalR * Math.cos(NRGMathHelper.degreesToRadians(relativeTheta));
	double relativeY = translationalR * Math.sin(NRGMathHelper.degreesToRadians(relativeTheta));
	
	// four possibilities of vectors: double letter = power +/- radius * steer or twist;
	double a = relativeX - K_TWISTX * rotationOmega; //rotationomega will twist the entire robot
	double b = relativeX + K_TWISTX * rotationOmega;
	double c = relativeY - K_TWISTY * rotationOmega;
	double d = relativeY + K_TWISTY * rotationOmega;

	double angleFR = NRGMathHelper.atan2Degree(c, b);
	double angleFL = NRGMathHelper.atan2Degree(d, b);
	double angleBL = NRGMathHelper.atan2Degree(d, a);
	double angleBR = NRGMathHelper.atan2Degree(c, a);

	double speedFR = Math.sqrt(b * b + c * c);
	double speedFL = Math.sqrt(b * b + d * d);
	double speedBL = Math.sqrt(a * a + d * d);
	double speedBR = Math.sqrt(a * a + c * c);

	//normalizing part, the speeds should not be above 1
	double max = NRGMathHelper.max(speedFR, speedFL, speedBL, speedBR);
	//NRGDebug.printRound(NRGDebug.DRIVE, "Drive speeds: 1:/0, 2:/1, 3:/2, 4:/3",
	//		    new double[]
	//	{
	//	    speedFR, speedFL, speedBL, speedBR
	//	}, 2);
	if (max > 1)
	{
	    speedFR /= max;
	    speedFL /= max;
	    speedBL /= max;
	    speedBR /= max;
	}
	
	//this code tries to fix the jumps/jolts we get when setpoint is set but angle is not inverted
	//originally we would invert MAE + motors and then set setpoint
	setWheelAngle(pidAngleFL, angleFL, MAE_FL);
	setWheelAngle(pidAngleBL, angleBL, MAE_BL);
	setWheelAngle(pidAngleFR, angleFR, MAE_FR);
	setWheelAngle(pidAngleBR, angleBR, MAE_BR);

        boolean allCloseToTarget = pidAngleFL.closeToTarget() && pidAngleBL.closeToTarget() && pidAngleFR.closeToTarget() && pidAngleBR.closeToTarget();
        //boolean allOnTarget = pidAngleFL.onTarget() && pidAngleBL.onTarget() && pidAngleFR.onTarget() && pidAngleBR.onTarget();
        //this(reset method) will prevent integral from accumulating too much as reset will reset error after you get onTarget
        if (pidAngleFL.onTarget())
        {
            pidAngleFL.reset();
        }
        else
        {
            pidAngleFL.enable();
        }
        if (pidAngleBL.onTarget())
        {
            pidAngleBL.reset();
        }
        else
        {
            pidAngleBL.enable();
        }
        if (pidAngleFR.onTarget())
        {
            pidAngleFR.reset();
        }
        else
        {
            pidAngleFR.enable();
        }
        if (pidAngleBR.onTarget())
        {
            pidAngleBR.reset();
        }
        else
        {
            pidAngleBR.enable();
        }
	NRGDebug.printRoundln(NRGDebug.DRIVE, "Drive speeds: 1:/0, 2:/1, 3:/2, 4:/3",
			    new double[]
		{
		    speedFR, speedFL, speedBL, speedBR
		}, 2);
	//might just make fullpower = true because 

	if (fullpower || allCloseToTarget)
	{
	    FLDRIVEMOTOR.set((MAE_FL.isInverted()) ? -speedFL : speedFL);
	    BLDRIVEMOTOR.set((MAE_BL.isInverted()) ? -speedBL : speedBL);
	    FRDRIVEMOTOR.set((MAE_FR.isInverted()) ? -speedFR : speedFR);
	    BRDRIVEMOTOR.set((MAE_BR.isInverted()) ? -speedBR : speedBR);
	    //testing invertedMotors and the condition
	    //NRGDebug.printRound(NRGDebug.DRIVE, "Drive speeds: 1:/0, 2:/1, 3:/2, 4:/3",
	    //	    new double[]
	    //{
	    //speedFR, speedFL, speedBL, speedBR
	    //}, 2);
	}
	else
	{
	    FLDRIVEMOTOR.set(0);
	    BLDRIVEMOTOR.set(0);
	    FRDRIVEMOTOR.set(0);
	    BRDRIVEMOTOR.set(0);
	}
    }

    //This method sets the PID values for each of the steering motors.
    public void setPid(double P, double I, double D)
    {
        pidAngleFL.setPID(P, I, D);
        pidAngleBL.setPID(P, I, D);
        pidAngleFR.setPID(P, I, D);
        pidAngleBR.setPID(P, I, D);
        
        NRGDebug.printRoundln(NRGDebug.DRIVE, "PID Constants: P:/0, I:/1, D:/2",
			    new double[]
		{
		    P, I, D
		}, 6);
    }
    
    /**
     * Sleeps for the given time in milliseconds
     */
    private void sleep(int ms)
    {
	try
	{
	    Thread.sleep(ms);
	}
	catch (Exception e)
	{
	    NRGDebug.println(NRGDebug.DRIVE, "Exception while sleeping in Drive!");
	    NRGDebug.printException(e);
	}
    }
    
    /**
     * Helper method to return MAE analog values w/o having to try-catch or violate the interface
     * @param mae the MAE object
     * @return the analog value
     */
    private double getAnalogValueFor(NRGMAE mae)
    {
	try
	{
	    return mae.getAnalogValue();
        }
	catch (Exception e)
	{
	    NRGDebug.printException(e);
	}
	return 0.0;
    }

    public String getVersionString()
    {
	return "3/18IC";
    }
    
    public void tankDrive(double leftValue, double rightValue)
    {
	
        if (FLDRIVEMOTOR == null || FRDRIVEMOTOR == null || BLDRIVEMOTOR == null || BRDRIVEMOTOR == null)
        {
            throw new NullPointerException("in NRGDrive.tankDrive(): null motor");
        }
	
	leftValue = limit(leftValue);
	rightValue = limit(rightValue);
	FLDRIVEMOTOR.set(leftValue);
        BLDRIVEMOTOR.set(leftValue);
	FRDRIVEMOTOR.set(rightValue);
        BRDRIVEMOTOR.set(rightValue);
        
//        FLDRIVEMOTOR.set(MAE_FL.isInverted() ? -leftValue : leftValue);
//        BLDRIVEMOTOR.set(MAE_BL.isInverted() ? -leftValue : leftValue);
//	FRDRIVEMOTOR.set(MAE_FR.isInverted() ? -rightValue : rightValue);
//        BRDRIVEMOTOR.set(MAE_BR.isInverted() ? -rightValue : rightValue);
    }
    
    /**
     * Take the raw tank drive left & right joystick inputs, then calculate the outputs with PID corrections,
     * and write to the left & right motor outputs.
     * Note: we don't use the 'D' term of general PID algorithm
     * @param joyL - left joystick/motorSpeed setting (-1.0 to 1.0)
     * @param joyR - right joystick/motorSpeed seeting (-1.0 to 1.0)
     */
    public void driveStraight(double speed, double desiredHeading)
    {
        String sts;
        final double Kp = -0.02;   //TODO: Calibrate?
        final double Ki = -0.005;
        final double Kd = -0.001;
        double joyL = limit(speed);
        double joyR = joyL;
        double joyLout, joyRout;
        
        double currentHeading = NRGSensors.GYRO.getAngle();

//        if (NRGDebug.DRIVE)
//        {
//            long curTime = System.currentTimeMillis();
//            sts = (curTime-prevTime) + "ms " + "Lav:" + NRGMathHelper.round2(joyLAvg) + " Rav:" + NRGMathHelper.round2(joyRAvg) + "  ";
//            NRGDebug.print(NRGDebug.DRIVE, sts);
//            prevTime = curTime;
//        }
        if (speed == 0)
        {
            m_prevError = 0.0;
            m_result = 0.0;
        }
        else
        {   // Generic PID calculation is: error = setpoint - input;
            m_error = NRGMathHelper.convertToSmallestRelativeAngle(desiredHeading - currentHeading);
            if (((m_totalError + m_error) * Ki < m_maximumOutput) &&
                ((m_totalError + m_error) * Ki > m_minimumOutput))
            {
                m_totalError += m_error;
            }

            m_result = Kp * m_error + Ki * m_totalError + Kd * (m_error - m_prevError);
            m_prevError = m_error;
        }

//        if (NRGDebug.DRIVE)
//        {
//            sts = " e" + NRGMathHelper.round2(m_error)
//                + " t" + NRGMathHelper.round2(m_totalError)
//                + " r" + NRGMathHelper.round2(m_result) + "  ";
//            //NRGLCD.println(sts);
//            NRGDebug.print(NRGDebug.DRIVE, sts);
//        }

        joyRout = joyR;
        joyLout = joyL + m_result;
        if (joyLout > m_maximumOutput || joyLout < m_minimumOutput)
        {
            // If we're trying to drive the left motor beyond its limits, then scale back the right motor instead
            joyLout = joyL;
            joyRout = joyR - m_result;
        }
        // Adjust final output to account for motors being mounted in opposite directions
//        joyLout *= NRGJoystick.LEFT_DRIVE_MULTIPLIER;
//        joyRout *= NRGJoystick.RIGHT_DRIVE_MULTIPLIER;

        // Note: the framework drive methods clamp their arguments between -1.0 and 1.0 for us
        tankDrive(joyRout, joyLout);

        //LCD.println5(sts);
        //NRGDebug.println(NRGDebug.DRIVE, sts);
    }

    protected static double limit(double num)
    {
	return NRGMathHelper.clampMagnitude(num, 1);
    }
    
    private void setWheelAngle(NRGPID pidAngle, double setpoint, NRGMAE source)
    {
	synchronized (pidAngle)
	{
	    if (NRGMathHelper.shortestArc(getAnalogValueFor(source), setpoint) > 100)
	    {
		source.invertOffset();
//		NRGDebug.println(NRGDebug.DRIVE, s);
	    }
	    if (pidAngle.getSetpoint() != setpoint)
	    {
		pidAngle.setSetpoint(setpoint);
	    }
	}
    }

    private void disableIfOnTarget(NRGPID pid)
    {
	if (pid.onTarget())
	{
	    pid.disable();
	}
    }
}
