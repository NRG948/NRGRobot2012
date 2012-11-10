/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrg.cmd;

import nrg.NRGDrive;
import nrg.NRGMathHelper;
import nrg.NRGRobot;
import nrg.NRGSensors;

/**
 *
 * @author Matthew
 */
public class TankRotateCommand extends CommandBase
{
    private double degreesClockwise;
    private double desiredHeading;
    private double motorSpeed;
    private double doneThreshold;

    private static final double TURN_COMMAND_DONE_THRESHOLD = 2.0; //TODO calibrate: Acceptable error in degrees
    private static final double TURN_SLOWDOWN_THRESHOLD_DEGREES = 35.0;
    private static final double MAX_TURN_SPEED = 0.60;
    private static final double MIN_TURN_SPEED = 0.35;   // Seattle TODO: Set just above the speed where friction stalls the turn
    
    public TankRotateCommand(double heading)
    {
        desiredHeading = heading;
        //if we don't pass in speed, assume it goes fastest
        motorSpeed = MAX_TURN_SPEED;
        doneThreshold = TURN_COMMAND_DONE_THRESHOLD;
    }
    public void init()
    {
        desiredHeading = NRGSensors.GYRO.getAngle() + degreesClockwise;
    }
    
    public boolean run()
    {
        double currentHeading = NRGSensors.GYRO.getAngle();
        double error = desiredHeading - currentHeading;
        //NRGDebug.println(Debug.AUTOCMD, "TurnCommand: curHead: " + MathHelper.round1(currentHeading) + ", error: " + MathHelper.round1(error));
        if (Math.abs(error) < doneThreshold)
        {
            return true;
        }
        // Use full motorSpeed for large errors, then taper off as the error drops below a threshold
        double directionalSpeed = motorSpeed * NRGMathHelper.clamp(error / TURN_SLOWDOWN_THRESHOLD_DEGREES, -1.0, 1.0);
        // But make sure we don't drive the motors so slow that drivetrain friction stalls us out
        if (directionalSpeed >= 0.0)
            directionalSpeed = NRGMathHelper.clamp(directionalSpeed, MIN_TURN_SPEED, 1.0);
        else
            directionalSpeed = NRGMathHelper.clamp(directionalSpeed, -1.0, -MIN_TURN_SPEED);
        NRGRobot.getDrive().tankDrive(directionalSpeed, -directionalSpeed);
        //NRGDebug.println(NRGDebug.AUTONOMOUS, "TURN Speed:" + directionalSpeed + " CurHead: " + currentHeading
         //       + " DesHead: " + desiredHeading);
        
        return false;
    }
    
    public void finalize()
    {
        NRGRobot.getDrive().tankDrive(0,0);
        lastHeading = desiredHeading;
    }
    
    public String getInfo()
    {
        return "";
    }
}
