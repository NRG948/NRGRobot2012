package nrg;

import edu.wpi.first.wpilibj.KinectStick;

/**
 *
 * @author Irving
 */
public class NRGKinect
{
    private static KinectStick leftArm = new KinectStick(1);
    private static KinectStick rightArm = new KinectStick(2);
    
    public static final int BUTTON_HEAD_RIGHT = 1;
    public static final int BUTTON_HEAD_LEFT = 2;
    public static final int BUTTON_RIGHT_LEG_SIDE = 3;
    public static final int BUTTON_LEFT_LEG_SIDE = 4;
    public static final int BUTTON_RIGHT_LEG_FORWARD = 5;
    public static final int BUTTON_RIGHT_LEG_BACK = 6;
    public static final int BUTTON_LEFT_LEG_FORWARD = 7;
    public static final int BUTTON_LEFT_LEG_BACK = 8;
    public static final int BUTTON_ENABLED = 9;
    
    public static boolean getButton(int button)
    {
	// documentation indicates that values for either KinectStick are the same
	return leftArm.getRawButton(button);
    }
    
    public static double getRightY()
    {
	return rightArm.getY();
    }
    
    public static double getLeft()
    {
	return leftArm.getY();
    }

    private NRGKinect()
    {
    }
}
