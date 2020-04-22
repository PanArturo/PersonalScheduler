/**
 * An enum to represent the possible frequency options for recurring tasks.
 */
public enum TaskFrequency
{
    DAILY(1), WEEKLY(7), MONTHLY(30);

    private int value;

    /**
     * Initializes the weekday constants with readable names.
     * @param displayText A display-friendly name for the weekday constant.
     */
    TaskFrequency(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}