/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrg;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.ColorImage;

/**
 *
 * @author Matthew
 */
public class NRGCamera
{
    public AxisCamera cam;
    private static final Servo vertServo = new Servo(1,9); //or 2
    public NRGCamera()
    {
        
        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k160x120); //TODO: Cory is it too bright?
        cam.writeMaxFPS(25);

    }
    
    public ColorImage getImage()
    {
        ColorImage image = null;
        try
        {
            if ( cam.freshImage() )
            {
                image = cam.getImage();
            }
        }
        catch(Exception e)
        {
            NRGDebug.printException(e);
        }   
        
        return image;
    }    
    public void update()
    {
        //NRGDebug.println(true, "Servo Angle: " + vertServo.getAngle());
        double moveZCamera = NRGJoystick.getThrottle();
        double revisedMove = ((1.0 - moveZCamera) / 2) *170;
        vertServo.setAngle(revisedMove);
        //NRGDebug.println(true, "Servo Angle 2: " + vertServo.getAngle());
        //NRGDebug.println(true,"Throttle Z: " + moveZCamera);
        //NRGDebug.println(true,"Servo Z: " + revisedMove);
        //vertServo.setAngle(NRGIO.getCameraTilt());
    }
}
