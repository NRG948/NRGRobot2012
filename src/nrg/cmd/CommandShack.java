package nrg.cmd;

import nrg.NRGRobot;

/**
 * CommandShack is a place to store all commands that autonomous may need to call.
 * Acts as a storage method for commands that can be statically accessed by autonomous. Keep the NRGAutonomous class clean and less cluttered.
 * @author Mohammad Adib
 */
public class CommandShack {
    private static final double SHOOT_FRONT_SPEED = (NRGRobot.isOriginalRobot) ? 0.606 : 0.85;
    private static final double SHOOT_FRONT_SIDE_SPEED = (NRGRobot.isOriginalRobot) ? 0.62 : 0.85; //or 0.606
    private static final double SHOOT_BACK_SPEED = (NRGRobot.isOriginalRobot) ? 0.67 : 0.825;    
    private static final double SHOOT_BACK_SIDE_SPEED = (NRGRobot.isOriginalRobot) ? 0.682 : 0.825;
    
    /**
     * Test routines
     */
    public static CommandBase[] moveSquare = {
        new TimedMoveCommand(TimedMoveCommand.FORWARD, 3000, 0.6),
        new TimedMoveCommand(TimedMoveCommand.RIGHT, 3000, 0.6),
        new TimedMoveCommand(TimedMoveCommand.BACKWARD, 3000, 0.61),
        new TimedMoveCommand(TimedMoveCommand.LEFT, 3000, 0.6)
    };
    public static CommandBase[] DoNothingCommand = { 
        //Do absolutely nothing!    
    };
    
    /**
     * Production code routines
     */
    //do this for all others
    //new RevShooterCommand(1000, SHOOT_FRONT_SPEED),
    //new DelayCommand(2500),
        //new ShootBallsCommand(2),
        //new RunShooterCommand(0),
    public static CommandBase[] shootFront = {
        new RevShooterCommand(1000, SHOOT_FRONT_SPEED),
        new DelayCommand(2000),
        new ShootBallsCommand(1),
        new RevShooterCommand(750, SHOOT_FRONT_SPEED),
        new DelayCommand(1500),
        new ShootBallsCommand(1),
        new RunShooterCommand(0)
    };
    
    public static CommandBase[] shootFrontSide = {
        new RevShooterCommand(1000, SHOOT_FRONT_SIDE_SPEED),
        new DelayCommand(1500),
        new ShootBallsCommand(1),
        new RevShooterCommand(500, SHOOT_FRONT_SIDE_SPEED),
        new DelayCommand(1000),
        new ShootBallsCommand(1),
        new RunShooterCommand(0)
    };
    
    public static CommandBase[] shootBack = {
        new RevShooterCommand(1000, SHOOT_BACK_SPEED),
        new DelayCommand(2000),
        new ShootBallsCommand(1),
        new RevShooterCommand(500, SHOOT_BACK_SPEED),
        new DelayCommand(1500),
        new ShootBallsCommand(1),
        new RunShooterCommand(0)
    };
    
    public static CommandBase[] shootBackBridge = {
        new RevShooterCommand(1000, SHOOT_BACK_SPEED),
        new DelayCommand(2000),
        new ShootBallsCommand(1),
        new RevShooterCommand(500, SHOOT_BACK_SPEED),
        new DelayCommand(1500),
        new ShootBallsCommand(1),
        new RunShooterCommand(0)
        //TODO
    };
    
    public static CommandBase[] shootFrontBridge =
    {
        //new RevShooterCommand(1000, SHOOT_FRONT_SPEED),
        //new DelayCommand(2000),
        //new ShootBallsCommand(1),
        //new RevShooterCommand(500, SHOOT_FRONT_SPEED),
        //new DelayCommand(1500),
        //new ShootBallsCommand(1),
        //new RunShooterCommand(0),
        new TankMoveCommand(120, -0.7),
        //new SetBridgeCommand(SetBridgeCommand.EXTENDED, 3)
        //TODO
    };
    
    public static CommandBase[] shootBackSide = {
        new RevShooterCommand(1000, SHOOT_BACK_SIDE_SPEED),
        new DelayCommand(2000),
        new ShootBallsCommand(1),
        new RevShooterCommand(500, SHOOT_BACK_SIDE_SPEED),
        new DelayCommand(1500),
        new ShootBallsCommand(1),
        new RunShooterCommand(0)
    };
    
    public static CommandBase[] createCommands(int position, boolean bridge) {
        CommandBase[] result = DoNothingCommand;
        switch (position) {
            case CommandIO.POSITION_FRONT_CENTER:
                result = shootFront;
                if(bridge) {
                    result = shootFrontBridge;
                }
                break;
            case CommandIO.POSITION_FRONT_LEFT:
            case CommandIO.POSITION_FRONT_RIGHT:
                result = shootFrontSide;
                break;
            case CommandIO.POSITION_BACK_CENTER:
                result = shootBack;
                if(bridge) {
                    result = shootBackBridge;
                }
                break;
            case CommandIO.POSITION_BACK_LEFT:
            case CommandIO.POSITION_BACK_RIGHT:
                result = shootBackSide;
                break;
        }
        CommandUtils.debugPrintln("Built CommandBase[] array:");
        for (int i = 0; i < result.length; i++) {
            CommandUtils.debugPrintln(result[i].getClass().getName());
        }
        return result;
    }
}
