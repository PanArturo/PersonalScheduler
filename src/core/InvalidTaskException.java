package core;

/**
 * An exception for invalid tasks.
 */
public class InvalidTaskException extends RuntimeException
{
    /**
     * Creates an exception without any additional information.
     */
    public InvalidTaskException()
    {
        super();
    }

    /**
     * Creates an exception with a message displaying the error.
     */
    public InvalidTaskException(String message)
    {
        super(message);
    }
}