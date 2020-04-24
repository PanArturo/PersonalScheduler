import java.util.Map;
import java.util.HashMap;

/**
 * A task object to represent tasks that only occur once on a specific date.
 */
public class TransientTask extends Task
{
    public static final String[] validCategories = {"Visit", "Shopping", "Appointment"};

    private Date activeDate;
    private Map<Date, Timeframe> activeTimes;

    /**
     * Initializes a transient task.
     * @param taskName The name of the task.
     * @param category The name of the category this task is part of.
     * @param timeframe The timeframe of the task.
     * @param date A date object representing the specific date this task applies to.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public TransientTask(String taskName, String category, Timeframe timeframe, Date date)
    {
        super(taskName, category, timeframe);
        this.activeDate = new Date(date);
        generateActiveTimes();
    }

    /**
     * Adds a date to the set of those this task affects,
     * adding the next consecutive date if necessary as well.
     * 
     * @param date The date the task will be active.
     * @param timeframe The original timeframe of the task.
     * @param nextDayRunoff The number of minutes the task runs into the next day.
     */
    private void generateActiveTimes()
    {
        activeTimes = new HashMap<>();
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = timeframe.getNextDayRunoff();
        if (nextDayRunoff > 0)
        {
            activeTimes.put(activeDate, timeframe.truncate(false));
            activeTimes.put(activeDate.getNextDay(), timeframe.truncate(true));
        }
        else
            activeTimes.put(activeDate, timeframe);
    }

    /**
     * Returns a copy of the dates and corresponding timeframes
     * this task will be active. Tasks that extend into a second day
     * have been accounted for and their timeframes have been truncated
     * according to each of the applicable days.
     */
    @Override
    public Map<Date, Timeframe> getScheduledTimes()
    {
        return new HashMap<Date, Timeframe>(activeTimes);
    }

    @Override
    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    public boolean isActiveOn(Date date)
    {
        if (activeDate.equals(date))
            return true;
        return false;
    }

    /**
     * Gets the active date of the task. (new object to protect internal copy)
     * @return The active date of the task.
     */
    public Date getActiveDate()
    {
        return new Date(activeDate);
    }

    /**
     * Gets an array of valid categories for the task.
     * @return An array of valid categories for the task.
     */
	@Override
    public String[] getValidCategories()
    {
		return validCategories;
	}
}
