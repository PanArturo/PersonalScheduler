/**
 * Oscar Bedolla
 * Charles Bickham - UI Team
 * Natalie Dinh - UI Team
 * Markus Hernandez - Coding Team
 * Christopher Leung - JSON Team
 * Arturo Pan Loo - UI Team
 * Adam VanRiper - JSON Team
 */
package core;

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
