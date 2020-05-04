package core;

import com.google.gson.JsonObject;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
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
    private Map<Date, Set<Timeframe>> activeTimes;

    /**
     * Initializes a recurring task.
     * 
     * @param taskName      The name of the task.
     * @param category      The name of the category this task is part of.
     * @param timeframe     The timeframe of the task.
     * @param startingDate  The date this task becomes active.
     * @param endingDate    The last date this task could be scheduled.
     * @param frequency The number of days between each occurence of the task.
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
        if (activeTimes.containsKey(date)
            && activeTimes.get(date).size() > 0)
            return true;
        return false;
    }

    /**
     * Determines whether an anti-task affects this recurring task on a
     * particular date.
     * CAUTION: A recurring task may still be active on dates with an anti-task if
     *          the recurring task has multiple timeframes on that date!
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
     * Returns a set of dates on which this recurring task is affected by
     * an anti-task.
     * CAUTION: A recurring task may still be active on these dates if the recurring
     *          task has multiple timeframes on that date!
     * 
     * @return A set of dates on which the recurring task is affected by an anti-task.
     */
    public Set<Date> getAntiTaskDates()
    {
        Set<Date> antiTaskDates = new HashSet<>();
        for (AntiTask t : antiTasks)
            antiTaskDates.add(t.getActiveDate());
        return antiTaskDates;
    }

    /**
     * Returns a set of anti-tasks attached
     * to this recurring task.
     * @return A set of applicable anti-tasks.
     */
    public Set<AntiTask> getAntiTasks()
    {
        return new HashSet<>(antiTasks);
    }

    /**
     * Attaches an anti-task to the recurring task.
     * If the anti-task is valid, the occurence of this recurring task
     * corresponding to the anti-task is canceled.
     * CAUTION: Do not independently call this method, the schedule must
     *          be updated to properly reflect these changes.
     * 
     * @param antiTask The anti-task to attach to the recurring task.
     * @return A set of affected dates along with updated timeframes.
     *         This allows us to only modify the necessary dates in the schedule.
     * @throws InvalidTaskException If the anti-task does not exactly cancel a
     *                              occurence of the recurring task or that
     *                              occurence has already been canceled by an
     *                              existing anti-task.
     */
    public Map<Date, Set<Timeframe>> addAntiTask(AntiTask antiTask)
    {
        Date antiTaskDate = antiTask.getActiveDate();
        Timeframe antiTaskTimeframe = antiTask.getGeneralTimeframe();
        for (AntiTask t : antiTasks)
        {
            if (antiTask.timeFrameConflictsWith(t))
                throw new InvalidTaskException("An anti-task " + t.getTaskName()
                        + " already cancels the recurring task " + getTaskName() + " at this time.");
        }
        if (!isActiveOn(antiTaskDate))
        {
            throw new InvalidTaskException("The recurring task " + getTaskName()
                    + " is not active on the date of the anti-task " + antiTask.getTaskName() + ".");
        }
        if (!getGeneralTimeframe().equals(antiTaskTimeframe))
        {
            throw new InvalidTaskException("The recurring task " + getTaskName()
                    + " has a different timeframe than the anti-task " + antiTask.getTaskName() + ".");
        }
        antiTasks.add(antiTask);
        antiTask.setCancelledTask(this);
        removeScheduledDate(antiTaskDate, antiTaskTimeframe, antiTaskTimeframe.getNextDayRunoff());
        // Return Updated Timeframes
        Date date = antiTask.getActiveDate();
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = timeframe.getNextDayRunoff();
        Map<Date, Set<Timeframe>> updatedTimes = new HashMap<>();
        if (activeTimes.containsKey(date))
            updatedTimes.put(date, new HashSet<>(activeTimes.get(date)));
        else
            updatedTimes.put(date, new HashSet<>());
        if (nextDayRunoff > 0)
        {
            Date nextDay = date.getNextDay();
            updatedTimes.put(nextDay, new HashSet<>(activeTimes.get(nextDay)));
        }
        return updatedTimes;   
    }

    /**
     * Detaches an anti-task from this recurring task.
     * If the anti-task is valid, the occurence of this recurring task
     * corresponding to the anti-task is restored.
     * CAUTION: Do not independently call this method, the schedule must
     *          be updated to properly reflect these changes.
     * 
     * @param antiTask The anti-task to attach to the recurring task.
     * @return A set of affected dates along with updated timeframes.
     *         This allows us to only modify the necessary dates in the schedule.
     * @throws InvalidTaskException If the anti-task does not exactly cancel a
     *                              occurence of the recurring task or that
     *                              occurence has already been canceled by an
     *                              existing anti-task.
     */
    public Map<Date, Set<Timeframe>> removeAntiTask(AntiTask antiTask)
    {
        if (!antiTasks.contains(antiTask))
        {
            throw new InvalidTaskException("The anti-task " + antiTask.getTaskName() + " does not apply to "
                                           + getTaskName() + " and cannot be removed from it!");
        }
        Date date = antiTask.getActiveDate();
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = timeframe.getNextDayRunoff();
        addTimesForDate(antiTask.getActiveDate(), timeframe, nextDayRunoff);
        antiTasks.remove(antiTask);
        antiTask.setCancelledTask(null);
        // Return Updated Timeframes
        Map<Date, Set<Timeframe>> updatedTimes = new HashMap<>();
        updatedTimes.put(date, new HashSet<>(activeTimes.get(date)));
        if (nextDayRunoff > 0)
        {
            Date nextDay = date.getNextDay();
            updatedTimes.put(nextDay, new HashSet<>(activeTimes.get(nextDay)));
        }
        return updatedTimes;    
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
        Set<Date> antiTaskDates = getAntiTaskDates();
        while (currentDate.compareTo(endingDate) <= 0)
        {
            if (!antiTaskDates.contains(currentDate))
                addTimesForDate(currentDate, timeframe, nextDayRunoff);
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
     * @param generalTimeframe The original timeframe of the task.
     * @param nextDayRunoff The number of minutes the task runs into the next day.
     */
    private void addTimesForDate(Date date, Timeframe generalTimeframe, int nextDayRunoff)
    {
        if (nextDayRunoff > 0)
        {
            addDailyTimeframe(date, generalTimeframe.truncate(false));
            addDailyTimeframe(date.getNextDay(), generalTimeframe.truncate(true));
        }
        else
            addDailyTimeframe(date, generalTimeframe);
    }

    /**
     * Adds a daily timeframe to the corresponding date.
     * 
     * @param date The date to associate the timeframe with.
     * @param dailyTimeframe The timeframe to add.
     */
    private void addDailyTimeframe(Date date, Timeframe dailyTimeframe)
    {
        Set<Timeframe> dailyTimeframes;
        if (!activeTimes.containsKey(date))
        {
            dailyTimeframes = new TreeSet<>();
            activeTimes.put(date, dailyTimeframes);
        }
        else
            dailyTimeframes = activeTimes.get(date);
        dailyTimeframes.add(dailyTimeframe);
    }

    /**
     * Removes a starting date from the set of those this task affects,
     * removing runoff into the next consecutive date if necessary as well.
     * "Undoes" addScheduledDate for a particular date.
     * 
     * @param date The date the instance of the task begins on.
     * @param generalTimeframe The original timeframe of the task.
     * @param nextDayRunoff The number of minutes the task runs into the next day.
     */
    private void removeScheduledDate(Date date, Timeframe generalTimeframe, int nextDayRunoff)
    {
        if (nextDayRunoff > 0)
        {
            removeDailyTimeframe(date, generalTimeframe.truncate(false));
            removeDailyTimeframe(date.getNextDay(), generalTimeframe.truncate(true));
        }
        else
            removeDailyTimeframe(date, generalTimeframe);
    }

    /**
     * Removes a daily timeframe from the corresponding date.
     * "Undoes" addDailyTimeframe for a particular date and timeframe.
     * 
     * @param date The date to disassociate the timeframe with.
     * @param dailyTimeframe The timeframe to remove.
     */
    private void removeDailyTimeframe(Date date, Timeframe dailyTimeframe)
    {
        if (activeTimes.containsKey(date))
        {
            Set<Timeframe> timeframes = activeTimes.get(date);
            timeframes.remove(dailyTimeframe);
            if (timeframes.size() == 0)
                activeTimes.remove(date);
        }
    }

    /**
     * Returns a copy of the dates and corresponding timeframes
     * this task will be active. Tasks that extend into a second day
     * have been accounted for and their timeframes have been truncated
     * according to each of the applicable days.
     * 
     * @return Every date and timeframe this task will be active.
     */
    @Override
    public Map<Date, Set<Timeframe>> getScheduledTimes()
    {
        return new HashMap<Date, Set<Timeframe>>(activeTimes);
    }

    /**
     * Gets an array of valid categories for the task.
     * 
     * @return An array of valid categories for the task.
     */
    @Override
    public String[] getValidCategories()
    {
        return validCategories;
    }

    /**
     * Returns a JsonObject with the properties corresponding to
     * the recurring task.
     *
     * @param task - The task to retrieve properties from.
     * @return A JsonObject containing the properties corresponding to the task.
     */
    @Override
    public JsonObject getJsonObject()
    {
        JsonObject temp = new JsonObject();
        Timeframe timeframe = getGeneralTimeframe();
        temp.addProperty("Name", getTaskName());
        temp.addProperty("Type", getCategory());
        temp.addProperty("StartDate", startingDate.getConcatenatedDate());
        temp.addProperty("StartTime",timeframe.getStartingTimeHours());
        temp.addProperty("Duration",timeframe.getDurationHours());
        temp.addProperty("EndDate", endingDate.getConcatenatedDate());
        temp.addProperty("Frequency", getFrequency().getValue());
        return temp;
    }
}
