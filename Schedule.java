import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * A schedule keeps track of the complete list of tasks a user has scheduled.
 * The schedule can use this information to compile a list of tasks on any specific date.
 */
public class Schedule
{
    // Contains Daily Tasks & Duplicate References For Tasks On Multiple Days
    public Map<Date, Set<Task>> calendar;

    // Sets Of Every Scheduled Task
    private Set<TransientTask> transientTasks;
    private Set<RecurringTask> recurringTasks;
    private Set<AntiTask> antiTasks;

    /**
     * Initializes a schedule.
     */
    public Schedule()
    {
        calendar = new HashMap<>();
        transientTasks = new HashSet<>();
        recurringTasks = new HashSet<>();
        antiTasks = new HashSet<>();
    }

    /**
     * Initializes a schedule (copy constructor).
     */
    public Schedule(Schedule existingSchedule)
    {
        calendar = new HashMap<>();
        Map<Date, Set<Task>> existingCalendar = existingSchedule.calendar;
        Set<Date> existingDates = existingCalendar.keySet();
        for (Date date : existingDates)
            calendar.put(date, existingCalendar.get(date));
        transientTasks = new HashSet<>(existingSchedule.transientTasks);
        recurringTasks = new HashSet<>(existingSchedule.recurringTasks);
        antiTasks = new HashSet<>(existingSchedule.antiTasks);
    }

    /**
     * Adds a transient task to the schedule.
     * 
     * @param newTask The transient task to add to the schedule.
     * @throws TaskConflictException If an existing task conflicts with the one being added.
     */
    public void addTask(TransientTask newTask)
    {
        generalAddTask(newTask);
        transientTasks.add(newTask);
    }

    /**
     * Adds a recurring task to the schedule.
     * 
     * @param newTask The recurring task to add to the schedule.
     * @throws TaskConflictException If an existing task conflicts with the one being added.
     */
    public void addTask(RecurringTask newTask)
    {
        generalAddTask(newTask);
        recurringTasks.add(newTask);
    }

    /**
     * Adds a antitask task to the schedule.
     *
     * @param newTask The recurring task to add to the schedule.
     * @throws TaskConflictException If an existing task conflicts with the one being added.
     */
    public void addTask(AntiTask newTask)
    {

    }

    /**
     * Adds a task to the calendar.
     * 
     * @param newTask The task to add to the calendar.
     * @throws TaskConflictException If an existing task conflicts with the one being added.
     */
    private void generalAddTask(Task newTask)
    {
        Map<Date, Set<Timeframe>> newTimes = newTask.getScheduledTimes();
        Set<Date> newDates = newTimes.keySet();
        // Check For Conflicts
        for (Date date : newDates)
        {
            if (calendar.containsKey(date))
            {
                Set<Timeframe> dailyTimeframes = newTimes.get(date);
                for (Task existingTask : calendar.get(date))
                {
                    for (Timeframe timeframe : dailyTimeframes)
                    {
                        for (Timeframe existTimeframe : existingTask.getDailyTimeframes(date))
                        {
                            if (timeframe.conflictsWith(existTimeframe))
                                throw new TaskConflictException(newTask, existingTask);
                        }
                    }
                }
            }
        }
        // Add To Calendar
        for (Date date : newDates)
        {
            Set<Task> taskSet;
            if (!calendar.containsKey(date))
            {
                taskSet = new HashSet<>();
                calendar.put(date, taskSet);
            }
            else
                taskSet = calendar.get(date);
            taskSet.add(newTask);
        }
    }

    /**
     * Removes a transient task from the schedule.
     * 
     * @param removeTask The transient task to remove from the schedule.
     */
    public void removeTask(TransientTask removeTask)
    {
        generalRemoveTask(removeTask);
        transientTasks.remove(removeTask);
    }

    /**
     * Removes a recurring task from the schedule.
     * 
     * @param removeTask The recurring task to remove from the schedule.
     */
    public void removeTask(RecurringTask removeTask)
    {
        generalRemoveTask(removeTask);
        recurringTasks.remove(removeTask);
    }

    /**
     * Removes a task from the calandar.
     * 
     * @param removeTask The task to remove from the schedule.
     */
    private void generalRemoveTask(Task removeTask)
    {
        Map<Date, Set<Timeframe>> times = removeTask.getScheduledTimes();
        Set<Date> dates = times.keySet();
        for (Date date : dates)
            calendar.get(date).remove(removeTask);
    }

    /**
     * Gets the first transient task with the given name.
     * 
     * @param taskName The name of the task.
     * @return A reference to the task, or null if not found.
     */
    public TransientTask getTransientTask(String taskName)
    {
        for (TransientTask task : transientTasks)
        {
            if (task.getTaskName().equals(taskName))
                return task;
        }
        return null;
    }

    /**
     * Gets the first recurring task with the given name.
     * 
     * @param taskName The name of the task.
     * @return A reference to the task, or null if not found.
     */
    public RecurringTask getRecurringTask(String taskName)
    {
        for (RecurringTask task : recurringTasks)
        {
            if (task.getTaskName().equals(taskName))
                return task;
        }
        return null;
    }

    /**
     * Gets the first task with the given name.
     * Avoid using this when possible as it must
     * iterate through every possible task.
     * 
     * @param taskName The name of the task.
     * @return A reference to the task, or null if not found.
     */
    public Task getTask(String taskName)
    {
        Set<Date> dates = calendar.keySet();
        for (Date date : dates)
        {
            Set<Task> tasks = calendar.get(date);
            for (Task task : tasks)
            {
                if (task.getTaskName().equals(taskName))
                    return task;
            }
        }
        return null;
    }

    /**
     * Returns a set of tasks applicable to the provided day
     * including all types of tasks, considering tasks from the
     * previous day that extend into the provided day as well.
     * 
     * @param date The date to check.
     * @return A set of tasks applicable to that date,
     *         or null if there are no tasks scheduled that day.
     */
    public Set<Task> getDailyTasks(Date date)
    {
        if (calendar.containsKey(date))
            return new HashSet<>(calendar.get(date));
        return null;
    }
    
    /**
     * Gets a set of all transient tasks across the entire calendar.
     * 
     * @return A set of all scheduled transient tasks.
     */
    public Set<TransientTask> getTransientTasks()
    {
        return new HashSet<TransientTask>(transientTasks);
    }

    /**
     * Gets a set of all recurring tasks across the entire calendar.
     * 
     * @return A set of all scheduled recurring tasks.
     */
    public Set<RecurringTask> getRecurringTasks()
    {
        return new HashSet<RecurringTask>(recurringTasks);
    }

    /**
     * Gets a set of all anti-tasks across the entire calendar.
     * 
     * @return A set of all scheduled anti-tasks.
     */
    public Set<AntiTask> getAntiTasks()
    {
        return new HashSet<AntiTask>(antiTasks);
    }

    /**
     * Attempts to merge two schedules into a new schedule.
     * @param otherSchedule The other schedule to merge.
     * @return A new schedule containing all tasks from
     *         both original Schedule objects.
     */
    public Schedule merge(Schedule otherSchedule)
    {
        Schedule newSchedule = new Schedule(this);
        for (TransientTask task : otherSchedule.transientTasks)
            newSchedule.addTask(task);
        for (TransientTask task : otherSchedule.transientTasks)
            newSchedule.addTask(task);
        for (AntiTask task : otherSchedule.antiTasks)
            newSchedule.antiTasks.add(task);
        return newSchedule;
    }
    
}
