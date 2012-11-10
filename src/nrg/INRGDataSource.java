package nrg;

/**
 * This is the interface that must be implemented for Drive or Manipulator or 
 * any other component to receive data. In this case only Joystick and Autonomous will
 * implement.
 * @author Eric
 */
public interface INRGDataSource
{
    /**
     * Allows Autonomous to initialize a thread. Not for Joystick.
     */
    void start();
    
    /**
     * Gets the x value for the drive (since we're using mecanum)
     * @return the x value (from -1.0 to 1.0)
     */
    double getDriveXValue();

    /**
     * Gets the y value for the drive
     * @return the y value (from -1.0 to 1.0)
     */
    double getDriveYValue();

    /**
     * Gets the rotation for the drive
     * @return the rotation value (from -1.0 to 1.0)
     */
    double getTwistValue();
    
    /**
     * Gets the desired orientation of the robot
     * @return the orientation in heading angles (0 for straight, 90 for right, etc)
     */
    double getOrientation();
    
    /**
     * 
     * @return Whether the datasource is telling robot to drive in precision mode
     */
    boolean precise();
    
    //TODO: decide operator control for shooting & pickup
    /**
     * Method to shoot.
     * If true, that means shoot. If false, don't shoot.
     * This will probably need to stay true to keep the shooter running.
     * @return whether to shoot
     */
    boolean shoot();
    
    /**
     * This method gets the current speed desired for the launcher. However, this does NOT state that
     * the manipulator should shoot! It only returns the speed.
     * @return A value from 0 to 1 for the speed of the launcher
     */
    double shootSpeed();
    
    /**
     * Method to run the tower.
     * True means run the tower. False means stop the tower.
     * This will need to stay true to keep the tower mechanism running.
     * @return whether to pickup
     */
    boolean raiseTower();
    
    /**
     * This controls the state of the possessor (rollers on the bottom).
     * 1 means possess (run rollers inward), 0 means off (for emergencies), -1 means repel (run outward)
     * @return -1, 0, or 1 ONLY
     */
    int getPossessState();
    
    /**
     * Method to lower bridge.
     * Outputs from 0 to 1, 0 meaning stowed, 1 meaning completely out.
     * This will NOT control the ball stop mechanism
     * @return whether to lower bridge
     */
    double lowerBridge();
    
    /**
     * Returns a flag to use either twist or orientation to drive
     * @return true to use twist, false to use orientation
     */
    boolean useTwist();
}