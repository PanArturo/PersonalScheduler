/**
 * This is an abstraction for a task that has methods and variables common to all tasks
 * but lacks a specific implementation for determining which dates the task is active.
 */
public abstract class Task
{
    private String taskName, category;
    private Timeframe timeSlot;

    /**
     * Initializes a task given a name, starting time, and duration.
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
     * @param taskName The new name of the task.
     */
    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    /**
     * Changes the category.
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
     * @param startingTime A time ranging from 0 to 1440 minutes (24 hours) to represent the start of the timeframe.
     */
    public void setStartingTime(int startingTime)
    {
        timeSlot.setStartingTime(startingTime);
    }

    /**
     * Changes the duration of the task.
     * @param duration The duration of the activity in minutes.
     */
    public void setDuration(int duration)
    {
        timeSlot.setDuration(duration);
    }

    /**
     * Gets the category of the task.
     * @return The category of the task.
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Gets the timeframe of the task.
     * @return A timeframe object holding the starting time and duration of the task.
     */
    public Timeframe getTimeframe()
    {
        return timeSlot;
    }

    /**
     * Explicitly gets the name of the task.
     * @return The name of the task.
     */
    public String getTaskName()
    {
        return taskName;
    }

    /**
     * Checks whether the timeframe of this task conflicts with that of the given task.
     * Note: This does not consider conflicting dates, only time slots.
     * @param task The task to compare against this one.
     * @return True if there is a time slot conflict, false otherwise.
     */
    public boolean hasConflictingTimeframesWith(Task task)
    {
        return timeSlot.conflictsWith(task.timeSlot);
    }

    /**
     * Gives the name of the task.
     * @return The name of the task.
     */
    @Override
    public String toString()
    {
        return taskName;
    }

    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    public abstract boolean isActiveOn(Date date);
    
    /**
     * Gets an array of valid categories for the task.
     * @return An array of valid categories for the task.
     */
    public abstract String[] getValidCategories();
}
