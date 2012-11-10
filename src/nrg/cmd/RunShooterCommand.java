package nrg.cmd;

import nrg.NRGAutonomous;
import nrg.NRGMathHelper;

/**
 * This command sets the manipulator's motor speed
 * @author Mohammad Adib
 */
public class RunShooterCommand extends CommandBase {
    
    private String info;

    private double speed;

    public RunShooterCommand(double speed) {
        this.speed = NRGMathHelper.clamp(speed, -1, 1);
        this.info = "new RunShooterCommand(" + speed + ")";
    }

    public void init() {
    }

    public boolean run() {        
        NRGAutonomous.setShootSpeed(speed);
        return true;
    }

    public void finalize() {
    }

    public String getInfo() {
        return info;
    }
}
