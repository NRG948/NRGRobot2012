package nrg;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Victor;
/**
 * Pick up balls. Shoot them.
 * 
 * @author Irving
 */
public class NRGManipulator implements INRGComponent, Runnable, INRGPIDSet
{
    
    private static final int CONVEYOR_MODULE = 2;
    private static final int CONVEYOR_CHANNEL = 7;
    
    private static final int POSSESSOR_MODULE = 2;
    private static final int POSSESSOR_FR_CHANNEL = 5;
    private static final int POSSESSOR_BK_CHANNEL = 6;
    
    private static final int LAUNCHER_MODULE = 2;
    private static final int LAUNCHER_CHANNEL = 8;
    
    private static final int SHOOTRELEASE_MODULE = 1;
    private static final int SHOOTRELEASE_CHANNEL = 5;
    //514, 232, 536
    //547
    private static final double SHOOTRELEASE_SET_STOW =         NRGRobot.isOriginalRobot ? 561.0 : 645.0;
    private static final double SHOOTRELEASE_SET_BRIDGEDOWN =   NRGRobot.isOriginalRobot ? 273.0 : 407.0;
    private static final double SHOOTRELEASE_SET_SHOOT =        NRGRobot.isOriginalRobot ? 589.0 : 667.0;
    //private static final int LIMIT_SWITCH_CHANNEL = 8; //TODO: Change
    
    private static final double POSSESSOR_FR_POSSESS =          NRGRobot.isOriginalRobot ? 1.0 : -1.0;	// direction for possessor rollers to possess balls
    private static final double POSSESSOR_BK_POSSESS =          NRGRobot.isOriginalRobot ? 1.0 : -1.0;	// direction for 
    private static final double CONVEYOR_LIFT =                 NRGRobot.isOriginalRobot ? -1.0 : -1.0;	// direction for conveyor motor to lift balls
    private static final double LAUNCHER_LAUNCH =               NRGRobot.isOriginalRobot ? 1.0 : 1.0;	// direction for launcher motor to launch balls
    
    private static final double MINIMUM_SHOOT_SPEED = 0.3;
    
    public static final boolean RELEASE_BALL = false;
    public static final boolean HOLD_BALL = true;
    
    // for picking up balls
    private Victor conveyor;
    private Victor possessorFront;
    private Victor possessorBack;
    
    private Jaguar launcher;
    
    // member variables for PIDReader
    private NRGPIDReader PIDReader;
    private static final String MANIPULATOR_PID_FILE = "file:///ManipPID.txt";
    
    // -------this motor right here shoots bridges-------
    // ----------------------beware----------------------
    private Victor shootBridge;
    private PIDController shootBridgePID;
    // ----------------we also have drugs----------------
    private AnalogChannel shootBridgePot = NRGSensors.shootReleasePot;
    
    private static final double ARM_P_ORIG = 0.03;
    private static final double ARM_I_ORIG = 0.003;
    private static final double ARM_D_ORIG = 0;
    
    private static final double ARM_P_TEST = 0.05;
    private static final double ARM_I_TEST = 0.01;
    private static final double ARM_D_TEST = 0;
    
    private INRGDataSource dataSource;
    private boolean isRunning;
    
    public static final int SBDS_DISABLED = 0;
    public static final int SBDS_CALIBRATE = 1;
    public static final int SBDS_PID = 2;
    private static final String[] sbdsStrings = {"DIS", "CAL", "PID"};
    private boolean shootBridgePIDMode;
    
    private static final double SHOOT_SPEED_DEAD_ZONE = 0.1;
    
    public static double shootSpeed = 0.0;
    public static double sbPot = 0.0;
    public static double pidSet = 0.0;
    public static int sbState = SBDS_DISABLED;
    
    private double lastSetpoint = 0.0;
    
    public NRGManipulator()
    {
	conveyor = new Victor(CONVEYOR_MODULE, CONVEYOR_CHANNEL);
	possessorFront = new Victor(POSSESSOR_MODULE, POSSESSOR_FR_CHANNEL);
	possessorBack = new Victor(POSSESSOR_MODULE, POSSESSOR_BK_CHANNEL);
	launcher = new Jaguar(LAUNCHER_MODULE, LAUNCHER_CHANNEL);

	shootBridge = new Victor(SHOOTRELEASE_MODULE, SHOOTRELEASE_CHANNEL);
	
	if (NRGRobot.isOriginalRobot)
	{
	    shootBridgePID = new PIDController(ARM_P_ORIG, ARM_I_ORIG, ARM_D_ORIG, shootBridgePot, shootBridge);
	}
	else
	{
	    shootBridgePID = new PIDController(ARM_P_TEST, ARM_I_TEST, ARM_D_TEST, shootBridgePot, shootBridge);
	}
	
	shootBridgePID.setInputRange(SHOOTRELEASE_SET_BRIDGEDOWN, SHOOTRELEASE_SET_SHOOT);
        shootBridgePID.setOutputRange(-1.0, 1.0);
        shootBridgePID.setTolerance(0.4);
	shootBridgePID.disable();
	
        PIDReader = new NRGPIDReader(MANIPULATOR_PID_FILE, NRGJoystick.PID_INTIALIZE_MANIPULATOR_BUTTON, this);
        
	this.init(null);
        
    }
    
    public void init(INRGDataSource s)
    {
	dataSource = s;
	isRunning = false;
        PIDReader.init();
	shootBridgePID.setSetpoint(SHOOTRELEASE_SET_STOW);
        NRGBallCounter.reset();
    }

    public void start()
    {
	isRunning = true;
	
	Thread manipulatorThread = new Thread(this);
	manipulatorThread.start();
    }

    public void stop()
    {
	isRunning = false;
    }

    public void run()
    {
	
	shootBridgePIDMode = NRGIO.bridgeStartInPID();
	sbState = shootBridgePIDMode ? SBDS_PID : SBDS_CALIBRATE;
	
	while (isRunning)
	{
            
            //PIDReader.update();
	    
	    shootBridgePIDMode = NRGIO.bridgeStartInPID();
	    sbState = shootBridgePIDMode ? SBDS_PID : SBDS_CALIBRATE;
	    
	    // update possessor rollers and conveyor
	    possessorFront.set(NRGManipulator.POSSESSOR_FR_POSSESS * dataSource.getPossessState());
	    possessorBack.set(NRGManipulator.POSSESSOR_BK_POSSESS * dataSource.getPossessState());
	    conveyor.set(dataSource.raiseTower() ? CONVEYOR_LIFT : 0);

	    //if (NRGJoystick.lowerTowerIO() && NRGRobot.useIOBoard)
            if (NRGIO.getLowerTower() && NRGRobot.useIOBoard)
	    {
		conveyor.set(-CONVEYOR_LIFT);
	    }

	    // update launcher
	    double speed = dataSource.shootSpeed();
	    
	    launcher.set(speed * NRGManipulator.LAUNCHER_LAUNCH);
	    
	    if (!shootBridgePIDMode)
	    {
		// if in calibrate mode
		
		if (NRGJoystick.dumpBridgePot())
		{
		    System.out.println("[NRGManipulator] Pot: " + shootBridgePot.pidGet());
		}
		
		shootBridgePID.disable();
		
		double drive = NRGJoystick.getBackupJoyY();
		
		if (Math.abs(drive) < 0.1)
		{
		    drive = 0;
		}
		
		shootBridge.set(drive);
	    }
	    else if (shootBridgePIDMode)
	    {
		// if in PID mode
		PIDReader.update();

		// update ball release/bridge mechanism
                double newSetpoint;
		if (dataSource.shoot())
		{
		    conveyor.set(NRGManipulator.CONVEYOR_LIFT);
                    newSetpoint = SHOOTRELEASE_SET_SHOOT;
		}
		else
		{
		    newSetpoint = SHOOTRELEASE_SET_STOW - (SHOOTRELEASE_SET_STOW - SHOOTRELEASE_SET_BRIDGEDOWN) * dataSource.lowerBridge();
		}
//                if (!NRGIO.getDigital(LIMIT_SWITCH_CHANNEL))
//                {
//                    shootBridgePID.reset(); //disables and resets error
//                }
                if (newSetpoint != lastSetpoint)
                {
                    NRGDebug.println(NRGDebug.MANIPULATOR, "[Manipulator] setting PID setpoint to " + newSetpoint);
		    shootBridgePID.setSetpoint(newSetpoint);
                    shootBridgePID.enable();
                    lastSetpoint = newSetpoint;
                }
	    }

	    shootSpeed = speed;
	    sbPot = shootBridgePot.pidGet();
	    pidSet = shootBridgePID.getSetpoint();
	    
//	    NRGLCD.println(NRGLCD.MANIPULATOR, 4, "shootSp: " + launcher.get());
	    try
	    {
		Thread.sleep(50);
	    }
	    catch (InterruptedException ex)
	    {
		System.out.println(ex);
		return;
	    }
	    if (shootBridgePID.onTarget())
            {
                shootBridgePID.disable();
            }
	}
    }

    public void setPid(double P, double I, double D)
    {
        NRGDebug.println(NRGDebug.MANIPULATOR, "P: " + P + " I: " + I + " D: " + D);
        shootBridgePID.setPID(P, I, D);
    }

    public String getVersionString()
    {
	return "4/4EL";
    }

}
