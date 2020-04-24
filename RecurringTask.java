import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * A task that occurs on given weekdays at a certain time within a range of
 * dates.
 */
public class RecurringTask extends Task
{
    public static final String[] validCategories = { "Class", "Study", "Sleep", "Exercise", "Work", "Meal" };

    private Date startingDate, endingDate;
    private TaskFrequency frequency;

    private Set<AntiTask> antiTasks;
    private Map<Date, Timeframe> activeTimes;

    /**
     * Initializes a recurring task.
     * 
     * @param taskName      The name of the task.
     * @param category      The name of the category this task is part of.
     * @param timeframe     The timeframe of the task.
     * @param taskFrequency The number of days between each occurence of the task.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public RecurringTask(String taskName, String category, Timeframe timeframe, Date startingDate, Date endingDate,
            TaskFrequency frequency) 
    {
        super(taskName, category, timeframe);
        this.startingDate = new Date(startingDate);
        this.endingDate = new Date(endingDate);
        this.frequency = frequency;
        this.antiTasks = new HashSet<>();
        generateScheduledTimes();
    }

    /**
     * Checks whether a task is active on a particular date.
     * 
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    @Override
    public boolean isActiveOn(Date date)
    {
        if (hasAntiTaskOn(date))
            return false;
        if (activeTimes.containsKey(date))
            return true;
        return false;
    }

    /**
     * Determines whether an anti-task affects this recurring task on a particular
     * date.
     * 
     * @param date The date to check.
     * @return True if an anti-task is active on this date, false otherwise.
     */
    public boolean hasAntiTaskOn(Date date)
    {
        for (AntiTask t : antiTasks)
        {
            if (t.isActiveOn(date))
                return true;
        }
        return false;
    }

    /**
     * Attaches an anti-task to the recurring task.
     * 
     * @param antiTask The anti-task to attach to the recurring task.
     * @throws InvalidTaskException If the anti-task does not exactly cancel a
     *                              occurence of the recurring task or that
     *                              occurence has already been canceled by an
     *                              existing anti-task.
     */
    public void addAntiTask(AntiTask antiTask)
    {
        for (AntiTask t : antiTasks)
        {
            if (antiTask.timeFrameConflictsWith(t))
                throw new InvalidTaskException("An anti-task " + t.getTaskName()
                        + " already cancels the recurring task " + getTaskName() + " at this time.");
        }
        if (!isActiveOn(antiTask.getActiveDate()))
        {
            throw new InvalidTaskException("The recurring task " + getTaskName()
                    + " is not active on the date of the anti-task " + antiTask.getTaskName() + ".");
        }
        if (!getGeneralTimeframe().equals(antiTask.getGeneralTimeframe()))
        {
            throw new InvalidTaskException("The recurring task " + getTaskName()
                    + " has a different timeframe than the anti-task " + antiTask.getTaskName() + ".");
        }
        antiTasks.add(antiTask);
    }

    /**
     * Gets the starting date. (new object to protect internal copy)
     * 
     * @return The date this task becomes active.
     */
    public Date getStartingDate()
    {
        return new Date(startingDate);
    }

    /**
     * Gets the ending date. (new object to protect internal copy)
     * 
     * @return The last date this task is active.
     */
    public Date getEndingDate()
    {
        return new Date(endingDate);
    }

    /**
     * Gets the frequency of the task.
     * 
     * @return The frequency of the task.
     */
    public TaskFrequency getFrequency()
    {
        return frequency;
    }

    /**
     * Generates a mapping of dates this task is active
     * to the corresponding timeframes the task will take place.
     * This includes dates affected by tasks from the previous day.
     * This method must be called again if changes are made to the task dates/times.
     */
    private void generateScheduledTimes()
    {
        activeTimes = new HashMap<>();
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = getGeneralTimeframe().getNextDayRunoff();
        Date currentDate = startingDate;
        while (currentDate.compareTo(endingDate) <= 0)
        {
            addScheduledDate(currentDate, timeframe, nextDayRunoff);
            if (frequency == TaskFrequency.DAILY)
                currentDate = currentDate.getNextDay();
            else if (frequency == TaskFrequency.WEEKLY)
                currentDate = currentDate.getNextWeek();
            else
                currentDate = currentDate.getNextMonth();
        }
    }

    /**
     * Adds a date to the set of those this task affects,
     * adding the next consecutive date if necessary as well.
     * 
     * @param date The date the task will be active.
     * @param timeframe The original timeframe of the task.
     * @param nextDayRunoff The number of minutes the task runs into the next day.
     */
    private void addScheduledDate(Date date, Timeframe timeframe, int nextDayRunoff)
    {
        if (nextDayRunoff > 0)
        {
            activeTimes.put(date, timeframe.truncate(false));
            activeTimes.put(date.getNextDay(), timeframe.truncate(true));
        }
        else
            activeTimes.put(date, timeframe);
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
