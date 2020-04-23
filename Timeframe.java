/**
 * The timeframe class holds a specific slot of time to make reporting/transferring times more intuitive.
 */
public class Timeframe
{
    private int startingTime;
    private int duration;

    /**
     * Initializes the timeframe.
     * @param startingTime The starting time of the activity in minutes.
     *                     Must be a multiple of 15 ranging from 0 (00:00) to 1425 (23:45).
     * @param duration The duration of the activity in minutes.
     *                 Must be a multiple of 15 ranging from 15 (00:15) to 1425 (23:45).
     * @throws InvalidTaskException If the given time is out of range or not a multiple of 15.
     */
    public Timeframe(int startingTime, int duration) throws InvalidTaskException
    {
        setStartingTime(startingTime);
        setDuration(duration);
    }

    /**
     * Initializes the timeframe.
     * @param startingTime The starting time of the activity in hours ranging from 0.00 to 23.75.
     *                     Will be rounded to the nearest quarter hour.
     * @param duration The duration of the activity in hours ranging from 0.25 to 23.75.
     *                 Will be rounded to the nearest quarter hour.
     * @throws InvalidTaskException If the given time is out of range.
     */
    public Timeframe(double startingTime, double duration) throws InvalidTaskException
    {
        setStartingTime(calculateMinutes(startingTime));
        setDuration(calculateMinutes(duration));
    }

    /**
     * Initializes the timeframe with an existing timeframe (copy constructor).
     * @param timeframe The timeframe to copy data from.
     */
    public Timeframe(Timeframe timeframe)
    {
        this.startingTime = timeframe.startingTime;
        this.duration = timeframe.duration;
    }

    /**
     * Sets the starting time.
     * @param startingTime The starting time of the activity in minutes.
     *                     Must be a multiple of 15 ranging from 0 (00:00) to 1425 (23:45).
     * @throws InvalidTaskException If the given time is out of range or not a multiple of 15.
     */
    public void setStartingTime(int startingTime) throws InvalidTaskException
    {
        if (startingTime % 15 != 0)
            throw new InvalidTaskException("The time " + startingTime + " is not a multiple of 15!");
        if (startingTime < 0 || startingTime > 1425)
            throw new InvalidTaskException("The time " + startingTime + " is out of range!");
        this.startingTime = startingTime;
    }

    /**
     * Sets the duration.
     * @param duration The duration of the activity in minutes.
     *                 Must be a multiple of 15 ranging from 15 (00:15) to 1425 (23:45).
     * @throws InvalidTaskException If the given duration is out of range or not a multiple of 15.
     */
    public void setDuration(int duration)
    {
        if (duration % 15 != 0)
            throw new InvalidTaskException("The duration " + duration + " is not a multiple of 15!");
        if (duration < 15 || duration > 1425)
            throw new InvalidTaskException("The duration " + duration + " is out of range!");
        this.duration = duration;
    }

    /**
     * Gets the starting time.
     * @return The starting time of the activity ranging from 0 (00:00) to 1425 (23:45).
     */
    public int getStartingTime()
    {
        return startingTime;
    }

    /**
     * Gets the activity duration.
     * @return The duration of the activity in minutes.
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * Gets the number of minutes in the timeframe leading into the next day.
     * For example, a timeframe at 1425 with duration 30 would run 15 minutes into the next day.
     * @return The number of minutes leading into the next day.
     */
    public int getNextDayRunoff()
    {
        if (startingTime + duration > 1440)
            return (startingTime + duration) % 1440;
        else
            return 0;
    }

    /**
     * Gets the number of minutes in a floating point number of hours,
     * rounded to the nearest 15 minutes.
     * @param hours A floating point number of hours.
     * @return The number of minutes in these hours, rounded to the nearest 15 minutes.
     */
    private int calculateMinutes(double hours)
    {
        return (int) Math.round((hours * 60) / 15.0) * 15;
    }

    /**
     * Checks whether this timeframe conflicts with a given timeframe.
     * @param t The timeframe to compare against this one.
     * @return True if there is a time conflict, false otherwise.
     */
    public boolean conflictsWith(Timeframe t)
    {
        if (startingTime >= t.startingTime && startingTime < (t.startingTime + t.duration)
            || t.startingTime >= startingTime && t.startingTime < (startingTime + duration))
            return true;
        return false;
    }

    /**
     * Formats a time in minutes into a 24 hour format.
     * @param minutes The time in minutes.
     * @return The time in 24 hour formatting.
     */
    private String getFormattedTime(int minutes)
    {
        return ((minutes / 60) % 24) + ":" + String.format("%02d", (minutes % 60));
    }

    /**
     * Gets a string with a textual representation of the timeframe.
     * @return A string representing the timeframe.
     */
    @Override
    public String toString()
    {
        return getFormattedTime(startingTime) + " to " + getFormattedTime(startingTime + duration);
    }
}
