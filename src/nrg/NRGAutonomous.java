package nrg;

import nrg.cmd.*;

/**
 * Autonomous 2011 - 2012
 * @author Mohammad Adib
 */
public class NRGAutonomous implements INRGDataSource {

    public static double driveX = 0, driveY = 0, driveTwist = 0, orientation = 0, shootSpeed = 0, lowerBridge = 0;
    private static boolean shootFlag = false, raiseTowerFlag = false;
    private static int possessState = 0;
    private CommandBase[] cmds = CommandShack.DoNothingCommand;
    private final int DELAYMS = 5000;
    
    private Thread autonomousThread;
    private boolean stop;

    /**
     * Constructor used by NRGRobot to instantiate a new NRGAutonomous object.
     * @param cmds the commands that will be executed during autonomous.
     */
    public NRGAutonomous() {
        CommandUtils.debugPrintln("New NRGAutonomous object instantiated.");
        
    }

    //TODO: Algorithm
    public void init() {
        driveX = 0;
        driveY = 0;
        driveTwist = 0;
        orientation = 0;
        shootSpeed = 0;
        lowerBridge = 0;
        stop = false;
        CommandUtils.debugPrintln("Initializing Autonomous. Calculating scenarios.");
        boolean[] states = CommandIO.getSwitchStates();
        final boolean DELAY = states[0];
        final boolean FRONT_BACK = states[1];
        final boolean LEFT = states[2];
        final boolean CENTER = states[3];
        final boolean RIGHT = states[4];
        final boolean BRIDGE = states[5];
        if (CommandIO.isAutonomousEnabled()) {

            if (DELAY) {
                try {
                    CommandUtils.debugPrintln("Delaying for " + DELAYMS + "ms");
                    Thread.sleep(DELAYMS);
                }
                catch (InterruptedException ex) {
                    CommandUtils.debugPrintln("Error during delay. Message: " + ex.getMessage());
                }
            }
            String forDebug = "Scenario:\nThe robot is positioned ";
            int position = CommandIO.POSITION_FRONT_CENTER;
            if (FRONT_BACK) {
                forDebug += "front";
                if (LEFT) {
                    position = CommandIO.POSITION_FRONT_LEFT;
                    forDebug += "left";
                }
                else if (RIGHT) {
                    position = CommandIO.POSITION_FRONT_RIGHT;
                    forDebug += "right";
                }
                else if (CENTER) {
                    position = CommandIO.POSITION_FRONT_CENTER;
                }
            }
            else {
                forDebug += "back";
                if (LEFT) {
                    position = CommandIO.POSITION_BACK_LEFT;
                    forDebug += "left";
                }
                else if (RIGHT) {
                    position = CommandIO.POSITION_BACK_RIGHT;
                    forDebug += "right";
                }
                else if (CENTER) {
                    position = CommandIO.POSITION_BACK_CENTER;
                }
            }
            boolean bridge = BRIDGE;
            CommandUtils.debugPrintln(forDebug);
            cmds = CommandShack.createCommands(position, bridge);
            //Reset gyro
            NRGSensors.GYRO.reset();
        }
    }

    private void printSwitchStates(boolean[] states) {
        String prefix = "Switch States:\n";
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    prefix = "DELAY:";
                    break;
                case 1:
                    prefix = "FRONT/BACK:";
                    break;
                case 2:
                    prefix = "BACK:";
                    break;
                case 3:
                    prefix = "LEFT:";
                    break;
                case 4:
                    prefix = "RIGHT:";
                    break;
                case 5:
                    prefix = "BRIDGE_SIDE:";
                    break;
                case 6:
                    prefix = "BRIDGE_CENTER:";
                    break;
            }
            CommandUtils.debugPrintln(prefix + " " + (states[i] ? "ON" : "OFF") + "\n");

        }
    }

    /**
     * Can be used by NRGRobot to start a designated thread specially for autonomous.
     */
    public void start()
    {
        Runnable runnable = new Runnable()
        {
            public void run() {
                init();
                CommandUtils.debugPrintln("Beginning command execution. run() method initiated.");
                for (int i = 0; !stop && i < cmds.length; i++) {
                    cmds[i].init();
                    while (!stop && !cmds[i].run())
                    {
                        /**
                         * Loops until cmds[i] finishes running.
                         */
                        try
                        {
                            Thread.sleep(10);
                        }
                        catch (Exception e)
                        {
                            NRGDebug.printException(e);
                        }
                    }
                    cmds[i].finalize();
                    CommandUtils.debugPrintln("Command " + i + " (" + cmds[i].getClass().getName() + ") out of " + cmds.length + " finished executing.");
                }
                CommandUtils.debugPrintln("All commands have finished executing.");
            }
        };

        if (CommandIO.isAutonomousEnabled())
        {
            autonomousThread = new Thread(runnable);
            autonomousThread.start();
            CommandUtils.debugPrintln("Designated thread for autonomous has started.");
        }
        else
        {
            CommandUtils.debugPrintln("Autonomous is not enabled. Thread not started!");
        }
    }
    
    public void stop()
    {
        stop = true;
    }

    /**
     * Gets the x value for the drive (since we're using swerve).
     * @return the x value (from -1 to 1).
     */
    public double getDriveXValue() {
        return driveX;
    }

    /**
     * Gets the y value for the drive.
     * @return the y value (from -1 to 1).
     */
    public double getDriveYValue() {
        return driveY;
    }

    /**
     * Gets the rotation for the drive.
     * @return the rotation value (from -1 to 1).
     */
    public double getTwistValue() {
        return driveTwist;
    }

    public boolean precise() {
        return false;
    }

    /**
     * Method to shoot; If true, that means shoot, If false, don't shoot.
     * @return whether to shoot.
     */
    public boolean shoot() {
        return shootFlag;
    }

    /**
     * Get the shoot speed for the launcher.
     * @return a speed from 0.0 to 1.0
     */
    public double shootSpeed() {
        return shootSpeed;
    }

    /**
     * Method to pickup, If true, that means pickup, If false, don't pickup, This will need to stay true to keep the pickup mechanism running.
     * @return whether to pickup.
     */
    public boolean raiseTower() {
        return raiseTowerFlag;
    }

    public int getPossessState() {
        return possessState;
    }

    /**
     * Method to lower bridge, If true, that means lower, If false, don't lower (& probably raise), This will need to stay true to keep the bridge lowering mechanism running.
     * @return whether to lower bridge.
     */
    public double lowerBridge() {
        return lowerBridge;
    }

    /**
     * Allows commands to set the x, y, and twist values for INRGDataSource.
     * @param x the drive x value (-1 to 1).
     * @param y the drive y value (-1 to 1).
     * @param twist the twist value (-1 to 1).
     */
    public static void setDrive(double x, double y, double twist) {
        driveX = x;
        driveY = y;
        driveTwist = twist;
        CommandUtils.debugPrintln("Setting drive values: X = " + x + " ,Y = " + y + " ,Twist = " + twist);
        NRGLCD.println(NRGLCD.AUTONOMOUS, 1, "X=" + x);
        NRGLCD.println(NRGLCD.AUTONOMOUS, 2, "Y=" + y);
        NRGLCD.println(NRGLCD.AUTONOMOUS, 3, "Twist=" + twist);
    }

    /**
     * Allows commands to set the shoot flag MAKE SURE TO SET THIS TO ZERO WHEN DONE.
     * @param shoot true to shoot, false to do nothing.
     */
    public static void setShoot(boolean shoot) {
        shootFlag = shoot;
    }

    /**
     * Allows commands to set the shoot speed of the launcher. Doesn't necessarily mean to shoot
     * @param speed a speed from 0.0 to 1.0
     */
    public static void setShootSpeed(double speed) {
        CommandUtils.debugPrintln("Setting shoot speed: " + speed);
        shootSpeed = speed;
    }

    /**
     * Allows commands to set the raise tower flag. MAKE SURE TO SET THIS TO ZERO WHEN DONE.
     * @param pickup true to raise, false to do nothing.
     */
    public static void setTower(boolean raiseTower) {
        raiseTowerFlag = raiseTower;
    }

    public static void setPossessorState(int possess) {
        possessState = possess;
    }

    /**
     * Allows commands to set the lower value. MAKE SURE TO SET THIS TO ZERO WHEN DONE.
     * @param lower true to lower bridge, false to do nothing.
     */
    public static void setLowerBridge(double lower) {
        lowerBridge = lower;
    }

    public double getOrientation() {
        return orientation;
    }

    public static void setOrientation(double heading) {
        orientation = heading;
    }

    public boolean useTwist() {
        return false;
    }
}