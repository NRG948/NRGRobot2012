package nrg;

import edu.wpi.first.wpilibj.*;
import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 * This class aggregates all instances of sensors
 * @author Eric
 */
public class NRGSensors
{

    /*
     * I think it's a little cleaner to have these 2x2 arrays for sensor that we have several of.
     * -Irving
     */
    //This might not be right
    /*
    Analog - Module 1
    Digital - Module 1 and 2
    Solenoid - Module 1
     * 
    Digital Module 1
    
    Port/Channel 1 - Front Left Drive Encoder Channel A
    Port/Channel 2 - Front Left Drive Encoder Channel B
    Port/Channel 3 - Gap
    Port/Channel 4 - Back Left Drive Encoder Channel A
    Port/Channel 5 - Back Left Drive Encoder Channel B
    Port/Channel 6 - Gap 
     * 
    Digital Module 2
    
    Port/Channel 1 - Front Right Drive Encoder Channel A
    Port/Channel 2 - Front Right Drive Encoder Channel B
    Port/Channel 3 - Gap
    Port/Channel 4 - Back Right Drive Encoder Channel A
    Port/Channel 5 - Back Right Drive Encoder Channel B
    Port/Channel 6 - Gap 
    
     */
    // CONSTANTS
    public static final int FRONT = 0;
    public static final int BACK = 1;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private static final double GYRO_SENSITIVITY = (NRGRobot.isOriginalRobot) ? 0.0073 : 0.00685; //TODO: calibrate the gyro sensitivity
    // e.g. NRGSensors.MAE[FRONT][LEFT]
    // SENSOR SETTINGS
    //TODO: Make sure these are right... I'm not sure but
    //I believe that it is {{Frontleft, FrontRight}, {BackLeft, BackRight}}
    // quadrature channels
    public static final int[][] QD_A_CHANNEL =
    {
	{
	    1, 1
	},
	{
	    4, 4
	}
    };
    public static final int[][] QD_B_CHANNEL =
    {
	{
	    2, 2
	},
	{
	    5, 5
	}
    };
    public static final int[][] MAE_CHANNEL =
    {
	{
	    3, 5
	},
	{
	    4, 6
	}
    };
    public static final int GYRO_MODULE = 1; //CORRECT(might wanna check slot vs module)
    public static final int GYRO_CHANNEL = 1; //CORRECT
    //temperature is 2
    public static final int MAE_MODULE = 1;
    public static final int QD_MODULE_1 = 1;
    public static final int QD_MODULE_2 = 2;
    public static final int POT_CHANNEL = 7;
    //Sensors
    public static final NRGQuadrature[][] quadrature = new NRGQuadrature[2][2];
    public static final NRGMAE[][] MAE = new NRGMAE[2][2];
    public static final NRGGyro GYRO = new NRGGyro();
    public static final AnalogChannel shootReleasePot = new AnalogChannel(POT_CHANNEL);
    //MAE url file

    public static final String MAE_URL = "file:///MAEOffsets.txt";

    // initialize all sensors here!
    // or we could split them into separate methods.
    public static void init()
    {
	for (int i = 0; i < 2; i++)
	{
	    for (int j = 0; j < 2; j++)
	    {
		//I know this isn't the best way -.-, can edit if you want
		MAE[i][j] = new NRGMAE();
		MAE[i][j].init(MAE_MODULE, MAE_CHANNEL[i][j]);

		quadrature[i][j] = new NRGQuadrature(j + 1, QD_A_CHANNEL[i][j], QD_B_CHANNEL[i][j]);
	    }
	}
	GYRO.init(GYRO_SENSITIVITY, GYRO_MODULE, GYRO_CHANNEL);
	readMAEFile();
    }

    public static void update()
    {
	if (NRGJoystick.calibrate())
	{
	    try
	    {
		writeMAEFile();
	    }
	    catch (Exception e)
	    {
		NRGDebug.println(NRGDebug.MAE, "IN NRGSensors, writing MAE calibration file: " + e.getMessage());
	    }
	    readMAEFile();
	}
	if (NRGJoystick.gyroReset())
	{
	    GYRO.reset();
	}
        if (NRGJoystick.knobReset())
        {
            NRGIO.setKnobOffset();
        }
    }

    //Throws illegal access exception
    //Also throws a IOException
    //Need debugger String.
    public static void writeMAEFile() throws IllegalAccessException, IOException
    {
	FileConnection maeFile = null;
	maeFile = (FileConnection) Connector.open(MAE_URL, Connector.READ_WRITE);
	if (!maeFile.exists())
	{
	    maeFile.create();
	}
	DataOutputStream out = maeFile.openDataOutputStream();
	for (int x = 0; x < 2; x++)
	{
	    for (int y = 0; y < 2; y++)
	    {
		//originally this was MAE.getAnalogValue(), but it should be getRawAngle() because the 1st method
		//already had offest in it
		out.writeDouble(MAE[x][y].getRawAngle());
	    }
	}
    }

    public static void readMAEFile()
    {
	try
	{
	    FileConnection maeFile = null;
	    maeFile = (FileConnection) Connector.open(MAE_URL, Connector.READ);
	    if (maeFile.exists())
	    {
		DataInputStream read = maeFile.openDataInputStream();
		for (int i = 0; i < 2; i++)
		{
		    for (int j = 0; j < 2; j++)
		    {
			double rawOffset = read.readDouble();
			MAE[i][j].setOffset(((rawOffset + 180) % 360) - 90);
			NRGDebug.print(NRGDebug.MAE_CALIBRATION, "Offset for MAE" + i + j + ": " + rawOffset);
		    }
		}
	    }
	}
	catch (IOException e)
	{
	    NRGDebug.print(NRGDebug.MAE_CALIBRATION, "Exception caught with MAE Initialization");
	}
//	MAE[FRONT][LEFT].setOffset((201.33 - 180) - 90); //front left
//	MAE[BACK][LEFT].setOffset((328.88 + 180) - 90); //back left
//	MAE[FRONT][RIGHT].setOffset((328.26 + 180) - 90); //front right
//	MAE[BACK][RIGHT].setOffset((266.72 + 180) - 90); // backright
//	//NRGDebug.printRound(NRGDebug.MAE, "Calibrated FL: ", 2);
    }
}
