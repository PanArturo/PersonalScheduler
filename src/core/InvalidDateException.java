package core;

/**
 * An exception for invalid dates.
 */

public class InvalidDateException extends RuntimeException
{
    /**
     * Creates an exception without any additional information.
     */
    public InvalidDateException()
    {
        super();
    }

    /**
     * Creates an exception with a message displaying the incorrect date.
     * @param month An integer representing the month of the incorrect date.
     * @param day An integer representing the day of the incorrect date.
     * @param year An integer representing the year of the incorrect date.
     */
    public InvalidDateException(int month, int day, int year)
    {
        super("Invalid Date: " + month + "." + day + "." + year);
    }
}
