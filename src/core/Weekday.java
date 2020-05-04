package core;

/**
 * An enum to represent every day of the week.
 * The purpose of this enum is to allow weekdays to be added to a weekday set intuitively.
 */
public enum Weekday
{
    MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"), FRIDAY("Friday"), SATURDAY("Saturday"), SUNDAY("Sunday");

    private String displayText;

    /**
     * Initializes the weekday constants with readable names.
     * @param displayText A display-friendly name for the weekday constant.
     */
    Weekday(String displayText)
    {
        this.displayText = displayText;
    }

    /**
     * Gives a more readable name for weekday constants.
     * @return The name of the weekday.
     */
    @Override
    public String toString()
    {
        return displayText;
    }
}