package nrg;

import java.util.Vector;

/**
 * This class is used to get min/max/avg of PID values.
 * @author Dustin
 */
public class NRGTimer
{
    private static Vector timerList = new Vector();
    private String name;
    private long secTotal;
    private int secCount;
    private long secMax;
    private long secMin;
    private long loopTotal;
    private int loopCount;
    private long loopMax;
    private long loopMin;
    private long startTime;
    private boolean enabled;
    private static final int NAME_LEN = 5;
    private static final int ENTRY_LEN = 3;
    
    public NRGTimer(String name)
    {
        if(name.length() > NAME_LEN)
            name = name.substring(0, NAME_LEN);
        while(name.length() < NAME_LEN)
            name += " ";
        this.name = name;
        timerList.addElement(this);
    }
    
    public void enabledOrDisabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public void reset()
    {
        secTotal = 0;
        secCount = 0;
        loopTotal = 0;
        loopCount = -1;
    }
    
    public void start()
    {
        loopCount++;
        if(loopCount > 0)
        {
            long diff = startTime - System.currentTimeMillis();
            if(loopCount == 1)
            {
                loopMax = diff;
                loopMin = diff;
            }
            else
            {
                loopMax = Math.max(loopMax, diff);
                loopMin = Math.min(loopMin, diff);
            }
            loopTotal += diff;
        }
        startTime = System.currentTimeMillis();
    }
    
    public void stop()
    {
        long diff = System.currentTimeMillis() - startTime;
        if(secCount == 0)
        {
            secMax = diff;
            secMin = diff;
        }
        else
        {
            secMax = Math.max(secMax, diff);
            secMin = Math.min(secMin, diff);
        }
        secTotal += diff;
        secCount++;
    }
    
    /*
     * lAV = loopAverage, lMX = loopMax, lMN = loopMin
     * sAV = secAverage, sMX = secMax, sMN = secMin
     *        |lAV|lMX|lMN|sAV|sMX|sMN
     * NAME1  | 89|123| 12|   |   |   
     * NAME2  |123|345| 34|   |   |   
     */
    public static void print()
    {
        NRGDebug.println(NRGDebug.TIMERS, "     |lAV|lMX|lMN|sAV|sMX|sMN");
        for(int i = 0; i < timerList.size(); i++)
        {
            NRGTimer c = (NRGTimer)timerList.elementAt(i);
            if(c.enabled)
            {
                try
                {
                    String lAV = formatLong(c.loopTotal / c.loopCount);
                    String lMX = formatLong(c.loopMax);
                    String lMN = formatLong(c.loopMin);
                    String sAV = formatLong(c.secTotal / c.secCount);
                    String sMX = formatLong(c.secMax);
                    String sMN = formatLong(c.secMin);
                    NRGDebug.println(NRGDebug.TIMERS, c.name+"|"+lAV+"|"+lMX+"|"+lMN+"|"+sAV+"|"+sMX+"|"+sMN);
                }
                catch(ArithmeticException ae)
                {
                    NRGDebug.println(NRGDebug.TIMERS, c.name+"|DivByZero");
                }
            }
        }
    }
    
    private static String formatLong(long l)
    {
        if(l > 999)
            l = 999;
        String s = l+"";
        while(s.length() < ENTRY_LEN)
            s = " "+s;
        return s;
    }
}