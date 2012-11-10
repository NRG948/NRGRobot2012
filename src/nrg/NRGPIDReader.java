/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nrg;

/*
 *
 * @author Foris Kuang
 */
import com.sun.squawk.microedition.io.FileConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.Connector;
import com.sun.squawk.util.LineReader;
import java.util.Vector;

public class NRGPIDReader
{
    //Name of file from which the txt is coming from

    private String fileName;
    // The joystick button number we want to take in the values from 
    private int buttonNumber;
    private double P;
    private double I;
    private double D;
    private boolean buttonPressed;
    private INRGPIDSet pidObject;

    public NRGPIDReader(String fileName, int buttonNumber, INRGPIDSet pidObject)
    {
        this.fileName = fileName;
        this.buttonNumber = buttonNumber;
        this.pidObject = pidObject;
    }

    private boolean readPIDFile()
    {
        try
        {
            FileConnection pidFile = null;
            pidFile = (FileConnection) Connector.open(fileName, Connector.READ);
            if (pidFile.exists())
            {

                InputStream read = pidFile.openInputStream();
                InputStreamReader reader = new InputStreamReader(read);
                LineReader lineReader = new LineReader(reader);
                Vector v = lineReader.readLines(null);
                P = Double.parseDouble((String) v.elementAt(0));
                I = Double.parseDouble((String) v.elementAt(1));
                D = Double.parseDouble((String) v.elementAt(2));
                return true;
            }

        }
        catch (IOException e)
        {
            NRGDebug.printException(e);
        }
        return false;
    }
    
    public void init()
    {
        if (readPIDFile())
        {
            pidObject.setPid(P, I, D);
        }
    }

    public void update()
    {
        if (NRGJoystick.backupButtonTriggered(buttonNumber))
        {
            if (readPIDFile())
            {
                pidObject.setPid(P, I, D);
            }
        }
    }
}
