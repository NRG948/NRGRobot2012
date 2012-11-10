package nrg.cmd;

import nrg.NRGAutonomous;
import nrg.NRGBallCounter;
import nrg.NRGDebug;

/**
 * This command releases the catch on the manipulator
 * @author Matthew Zhao, Mohammad Adib
 */
public class ShootBallsCommand extends CommandBase {

    private String info;
    private int numBalls;

    /**
     * @param delay the amount of time that this command waits before releasing the catch. 
     */
    public ShootBallsCommand(int numberBalls) {
        numBalls = numberBalls;
        this.info = "new ShootBallsCommand(" + numberBalls  + ")";
    }

    public void init() {
        NRGAutonomous.setShoot(true);
    }
    
    public boolean run()
    {
        NRGBallCounter.updateShots();
        return (NRGBallCounter.getNumShots() >= numBalls);
    }

    public void finalize() {
        //Tell the manipulator to activate the catch
        try
        {
            Thread.sleep(200);
        }
        catch(Exception e)
        {
            NRGDebug.printException(e);
        }
        NRGAutonomous.setShoot(false);
        NRGBallCounter.reset();
    }

    public String getInfo() {
        return info;
    }
}
