import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This is an abstraction for a task that has methods and variables common to all tasks
 * but lacks a specific implementation for determining which dates the task is active.
 */
public abstract class Task implements Comparable<Task>
{
    private String taskName, category;
    private Timeframe timeSlot;

    /**
     * Initializes a task given a name, starting time, and duration.
     * 
     * @param taskName The name of the task.
     * @param category The name of the category this task is part of.
     * @param timeframe The timeframe of the task.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public Task(String taskName, String category, Timeframe timeframe)
    {
        this.taskName = taskName;
        setCategory(category);
        this.timeSlot = new Timeframe(timeframe);
    }

    /**
     * Renames the task.
     * 
     * @param taskName The new name of the task.
     */
    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    /**
     * Changes the category.
     * 
     * @param category The new category of the task.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public void setCategory(String category) throws InvalidTaskException
    {
        for (String c : getValidCategories())
        {
            if (c.equals(category))
            {
                this.category = category;
                return;
            }
        }
        throw new InvalidTaskException("Error: " + category + " Is not a valid category!");
    }

    /**
     * Changes the task starting time.
     * 
     * @param startingTime A time ranging from 0 to 1440 minutes (24 hours) to represent the start of the timeframe.
     */
    public void setStartingTime(int startingTime)
    {
        timeSlot.setStartingTime(startingTime);
    }

    /**
     * Changes the duration of the task.
     * 
     * @param duration The duration of the activity in minutes.
     */
    public void setDuration(int duration)
    {
        timeSlot.setDuration(duration);
    }

    /**
     * Gets the category of the task.
     * 
     * @return The category of the task.
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Gets the general timeframe of the task.
     * 
     * @return A timeframe object holding the starting time and duration of the task.
     */
    public Timeframe getGeneralTimeframe()
    {
        return timeSlot;
    }

    /**
     * Gets the timeframe of a task specific to a particular day.
     * If a task runs into the next day then getting the daily
     * timeframe will only get the portion applicable to that specific day.
     * 
     * @param date The date to get the active timeframe for.
     * @return The timeframe specific to the particular day
     *         or null if the task has no times on the given day.
     */
    public Set<Timeframe> getDailyTimeframes(Date date)
    {
        Map<Date, Set<Timeframe>> times = getScheduledTimes();
        if (times.containsKey(date))
            return times.get(date);
        return null;
    }

    /**
     * Gets the name of the task.
     * 
     * @return The name of the task.
     */
    public String getTaskName()
    {
        return taskName;
    }

    /**
     * A general method to determine whether two tasks conflict or not.
     * 
     * @param task The task to compare against.
     * @return True if the tasks conflict, false otherwise.
     */
    public boolean conflictsWith(Task task)
    {
        Map<Date, Set<Timeframe>> times = getScheduledTimes();
        Map<Date, Set<Timeframe>> otherTimes = task.getScheduledTimes();
        Set<Date> sharedDates = times.keySet();
        sharedDates.retainAll(otherTimes.keySet());
        if (sharedDates.size() > 0)
        {
            for (Date sharedDate : sharedDates)
            {
                for (Timeframe time : times.get(sharedDate))
                {
                    for (Timeframe otherTime : otherTimes.get(sharedDate))
                    {
                        if (time.conflictsWith(otherTime))
                            return true;
                    }
                }
            }
        }
        return false; 
    }

    /**
     * Checks whether the timeframe of this task conflicts with that of the given task.
     * Note: This does not consider conflicting dates, only time slots.
     * 
     * @param task The task to compare against this one.
     * @return True if there is a time slot conflict, false otherwise.
     */
    public boolean timeFrameConflictsWith(Task task)
    {
        return timeSlot.conflictsWith(task.timeSlot);
    }

    /**
     * Checks whether the timeframe of this task conflicts with the one provided.
     * Note: This does not consider conflicting dates, only time slots.
     * 
     * @param timeframe The timeframe tio compare against this one.
     * @return True if there is a time slot conflict, false otherwise.
     */
    public boolean timeFrameConflictsWith(Timeframe timeframe)
    {
        return timeSlot.conflictsWith(timeframe);
    }

    /**
     * Returns a hash code for quick name lookup.
     * 
     * @return A hash code for the task.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(taskName, category);
    }

    /**
     * Compares tasks by their timeframes for sorting.
     */
    @Override
    public int compareTo(Task task)
    {
        return timeSlot.compareTo(task.timeSlot);
    }

    /**
     * Gives the name of the task.
     * 
     * @return The name of the task.
     */
    @Override
    public String toString()
    {
        return taskName;
    }

    /**
     * Checks whether a task is active on a particular date.
     * 
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    public abstract boolean isActiveOn(Date date);
    
    /**
     * Generates the dates this task is active along
     * with their corresponding timeframes.
     * 
     * @return A mapping of active dates to the corresponding active times.
     */
    public abstract Map<Date, Set<Timeframe>> getScheduledTimes();

    /**
     * Gets an array of valid categories for the task.
     * 
     * @return An array of valid categories for the task.
     */
    public abstract String[] getValidCategories();
}
