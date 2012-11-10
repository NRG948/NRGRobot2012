package nrg;

/**
 * This is the interface that must be implemented for a drive/manipulator class.
 * Make sure the component also implements the Runnable class
 * @author Eric
 */
public interface INRGComponent
{
    //TODO: finalize design
    /**
     * This should return a string that can be used to distinguish different versions of the same component.
     */
    String getVersionString();
    /**
     * Initializes the component. Must be called before running
     */
    void init(INRGDataSource s);

    /**
     * This starts the Runnable thread. Make sure init() is called before running
     */
    void start();

    /**
     * This is the method that the Base class calls when it need to kill the execution thread.
     */
    void stop();
}
