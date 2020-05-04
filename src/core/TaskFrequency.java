package core;

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

    /**
     * Gets the enum constant corresponding to the given value.
     * @param value The value associated with the enum constant.
     * @return The corresponding enum constant, defaulting to MONTHLY
     *         in the case of an invalid input.
     */
    public static TaskFrequency getFrequency(int value)
    {
        if (value == 1)
            return TaskFrequency.DAILY;
        else if (value == 7)
            return TaskFrequency.WEEKLY;
        else
            return TaskFrequency.MONTHLY;
    }
}