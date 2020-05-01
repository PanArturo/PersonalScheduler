import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * A task to eliminate a recurrence of conflicting tasks.
 */
public class AntiTask extends Task
{
    public static final String[] validCategories = {"Cancellation"};

    private Date activeDate;
    private Map<Date, Set<Timeframe>> activeTimes;

    private RecurringTask cancelledTask;

    /**
     * Initializes an anti-task.
     * @param taskName The name of the task / reason for cancellation.
     * @param timeframe The timeframe of the task.
     * @param date A date object representing the specific date this task applies to.
     */
    public AntiTask(String taskName, Timeframe timeframe, Date date)
    {
        super(taskName, validCategories[0], timeframe);
        this.activeDate = new Date(date);
        cancelledTask = null;
        generateActiveTimes();
    }

    /**
     * Link the anti-task with the recurring task it affects.
     * @param cancelledTask A reference to the task this anti-task affects.
     */
    public void setCancelledTask(RecurringTask cancelledTask)
    {
        this.cancelledTask = cancelledTask;
    }

    /**
     * Gets a reference to the affected recurring task.
     * @return The recurring task affected,
     *         or null if no recurring task is associated with the anti-task.
     */
    public RecurringTask getCancelledTask()
    {
        return cancelledTask;
    }

    /**
     * Generates a mapping of dates this task is active
     * to the corresponding timeframes the task will take place.
     * This includes dates affected by tasks from the previous day.
     * This method must be called again if changes are made to the task dates/times.
     */
    private void generateActiveTimes()
    {
        activeTimes = new HashMap<>();
        Set<Timeframe> dailyTimeframes;
        dailyTimeframes = new HashSet<>();
        activeTimes.put(activeDate, dailyTimeframes);
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = timeframe.getNextDayRunoff();
        if (nextDayRunoff > 0)
        {
            addDailyTimeframe(activeDate, timeframe.truncate(false));
            addDailyTimeframe(activeDate.getNextDay(), timeframe.truncate(true));
        }
        else
            addDailyTimeframe(activeDate, timeframe);
    }

    /**
     * Adds a daily timeframe to the corresponding date.
     * @param date The date to associate the timeframe with.
     * @param timeframe The timeframe to add.
     */
    private void addDailyTimeframe(Date date, Timeframe timeframe)
    {
        Set<Timeframe> dailyTimeframes;
        dailyTimeframes = new HashSet<>();
        activeTimes.put(date, dailyTimeframes);
        dailyTimeframes.add(timeframe);
    }

    /**
     * Returns a copy of the dates and corresponding timeframes
     * this task will be active. Tasks that extend into a second day
     * have been accounted for and their timeframes have been truncated
     * according to each of the applicable days.
     */
    @Override
    public Map<Date, Set<Timeframe>> getScheduledTimes()
    {
        return new HashMap<Date, Set<Timeframe>>(activeTimes);
    }

    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    @Override
    public boolean isActiveOn(Date date)
    {
        if (activeDate.equals(date))
            return true;
        return false;
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
    
    /**
     * Gets the active date of the task. (new object to protect internal copy)
     * @return The active date of the task.
     */
    public Date getActiveDate()
    {
        return new Date(activeDate);
    }
}