/**
 * An exception for invalid timeframes.
 */
public class InvalidTimeframeException extends RuntimeException
{
    /**
     * Creates an exception without any additional information.
     */
    public InvalidTimeframeException()
    {
        super();
    }

    /**
     * Creates an exception with a message displaying the error.
     */
    public InvalidTimeframeException(String message)
    {
        super(message);
    }
}
