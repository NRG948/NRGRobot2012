package nrg.cmd;

import nrg.NRGAutonomous;

/**
 * Revs the shooter at max for a given amount of time. Helps to lower the amount of time it takes to speed up the shooter.
 * @author Mohammad Adib
 */

public class RevShooterCommand extends CommandBase {
    
    private long duration, startTime;
    private double actualSpeed;
    private String info;
    
    public RevShooterCommand(long duration, double actualSpeed) {
        this.duration = duration;
        this.actualSpeed = actualSpeed;
        this.info = "new RevShooterCommand(" + duration + ")";
    }

    public void init() {
        startTime = System.currentTimeMillis();
        NRGAutonomous.setShootSpeed(1);
    }

    public boolean run() {
        return System.currentTimeMillis() - startTime >= duration;
    }

    public String getInfo() {
        return info;
    }

    public void finalize() {
        NRGAutonomous.setShootSpeed(actualSpeed);
    }
    
}
