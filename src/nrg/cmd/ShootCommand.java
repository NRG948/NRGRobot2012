package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * This command releases the catch on the manipulator
 * @author Mohammad Adib
 */
public class ShootCommand extends CommandBase {

    private String info;
    private long startTime;
    private long delay = 0;

    /**
     * @param delay the amount of time that this command waits before releasing the catch. 
     */
    public ShootCommand(long delay) {
        this.delay = delay;
        this.info = "new ShootCommand(" +  delay + ")";
    }

    public void init() {
        startTime = System.currentTimeMillis();
        NRGAutonomous.setShoot(true);
    }
    
    public boolean run() {
        if (System.currentTimeMillis() - startTime < delay) {
            return false;
        }
        //Tell the manipulator to release the catch
        return true;
    }

    public void finalize() {
        //Tell the manipulator to activate the catch        
        NRGAutonomous.setShoot(false);
    }

    public String getInfo() {
        return info;
    }
}
