import java.util.*;

/**
 * A schedule keeps track of the complete list of tasks a user has scheduled.
 * The schedule can use this information to compile a list of tasks on any specific date.
 */
public class Schedule
{
    // Contains Daily Tasks & Duplicate References For Tasks On Multiple Days
    private Map<Date, Set<Task>> calendar;

    // Sets Of Every Scheduled Task
    private Set<TransientTask> transientTasks;
    private Set<RecurringTask> recurringTasks;
    private Set<AntiTask> antiTasks;

    // Organize Scheduled Tasks By Category For Quick Access
    private Map<String, Set<Task>> categories;

    /**
     * Initializes a schedule.
     */
    public Schedule()
    {
        calendar = new HashMap<>();
        transientTasks = new HashSet<>();
        recurringTasks = new HashSet<>();
        antiTasks = new HashSet<>();
        categories = new HashMap<>();
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
            calendar.put(date, new HashSet<>(existingCalendar.get(date)));
        categories = new HashMap<>();
        Map<String, Set<Task>> existingCategoryDatabase = existingSchedule.categories;
        Set<String> existingCategories = existingCategoryDatabase.keySet();
        for (String category : existingCategories)
            categories.put(category, new HashSet<>(existingCategoryDatabase.get(category)));
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
            Task conflictingTask = findConflictingTask(date, newTimes.get(date));
            if (conflictingTask != null)
                throw new TaskConflictException(newTask, conflictingTask);
        }
        // Add To Calendar
        for (Date date : newDates)
            addTaskOnDate(date, newTask);
        // Categorize Task For Quick Lookup
        categorizeTask(newTask);
    }

    /**
     * Attempts to find an existing task in conflict with the
     * provided timeframes on the given date.
     * 
     * @param date The date to search for timeframe conflicts.
     * @param timeframes The potentially conflicting timeframes.
     * @return A conflicting task if found, or null otherwise.
     */
    private Task findConflictingTask(Date date, Set<Timeframe> timeframes)
    {
        if (calendar.containsKey(date))
        {
            for (Task existingTask : calendar.get(date))
            {
                for (Timeframe timeframe : timeframes)
                {
                    for (Timeframe existingTimeframe : existingTask.getDailyTimeframes(date))
                    {
                        if (timeframe.conflictsWith(existingTimeframe))
                            return existingTask;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Attempts to find an existing task in conflict with the
     * provided task on the given date, ignoring itself.
     * 
     * @param date The date to search for timeframe conflicts.
     * @param task The potentially conflicting task.
     * @return A conflicting task if found, or null otherwise.
     */
    private Task findConflictingTask(Date date, Task task)
    {
        if (calendar.containsKey(date))
        {
            for (Task existingTask : calendar.get(date))
            {
                if (!task.equals(existingTask) && task.conflictsWith(existingTask))
                    return existingTask;
            }
        }
        return null;
    }

    /**
     * Associates a task with the given date.
     * 
     * @param date The date to associate the task with.
     * @param newTask The task to be associated with the date.
     */
    private void addTaskOnDate(Date date, Task newTask)
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

    /**
     * Adds a task to the category database for quick access.
     * 
     * @param task The task to categorize.
     */
    private void categorizeTask(Task newTask)
    {
        String category = newTask.getCategory();
        Set<Task> taskSet;
        if (!categories.containsKey(category))
        {
            taskSet = new HashSet<>();
            categories.put(category, taskSet);
        }
        else
            taskSet = categories.get(category);
        taskSet.add(newTask);
    }

    /**
     * Removes a task from the category database.
     * 
     * @param task The task to remove from the categorization database.
     */
    private void uncategorizeTask(Task removeTask)
    {
        String category = removeTask.getCategory();
        if (categories.containsKey(category))
        {
            Set<Task> tasks = categories.get(category);
            tasks.remove(removeTask);
            if (tasks.size() == 0)
                categories.remove(category);
        }
    }

    /**
     * Gets a set of categories which 
     */
    public Set<String> getActiveCategories()
    {
        return categories.keySet();
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
            removeTaskOnDate(date, removeTask);
        uncategorizeTask(removeTask);
    }

    /**
     * Associates a task with the given date.
     * 
     * @param date The date to associate the task with.
     * @param newTask The task to be associated with the date.
     */
    private void removeTaskOnDate(Date date, Task removeTask)
    {
        if (calendar.containsKey(date))
        {
            Set<Task> dailyTasks = calendar.get(date);
            dailyTasks.remove(removeTask);
            if (dailyTasks.size() == 0)
                calendar.remove(date);
        }
    }

    /**
     * Adds a anti-task task.
     *
     * @param newTask The anti-task to add.
     * @throws InvalidTaskException If the anti-task does not properly correspond to a recurring task.
     */
    public void addTask(AntiTask newTask)
    {
        Date antiTaskDate = newTask.getActiveDate();
        if (!calendar.containsKey(antiTaskDate))
        {
            throw new InvalidTaskException("There are no tasks on " + antiTaskDate + " for the anti-task \""
                                           + newTask.getTaskName()+ "\" to affect!");
        }
        Timeframe generalTimeframe = newTask.getGeneralTimeframe();
        RecurringTask matchingTask = null;
        Set<Task> dailyTasks = calendar.get(antiTaskDate);
        for (Task existingTask : dailyTasks)
        {
            if (existingTask instanceof RecurringTask
                && existingTask.getGeneralTimeframe().equals(generalTimeframe))
            {
                matchingTask = (RecurringTask) existingTask;
                break;
            }
        }
        if (matchingTask != null)
        {
            antiTasks.add(newTask);
            categorizeTask(newTask);
            Map<Date, Set<Timeframe>> affectedTimes = matchingTask.addAntiTask(newTask);
            Set<Date> affectedDates = affectedTimes.keySet();
            for (Date date : affectedDates)
            {
                if (affectedTimes.get(date).size() == 0)
                    removeTaskOnDate(date, matchingTask);
            }
        }
        else
        {
            throw new InvalidTaskException("There are no applicable tasks on " + antiTaskDate + " for the anti-task \""
                                           + newTask.getTaskName()+ "\" to affect!");
        }
    }

    /**
     * Removes an anti-task.
     * 
     * @param removeTask The anti-task to remove.
     * @throws TaskConflictException If the anti-task removal would cause task conflicts.
     */
    public void removeTask(AntiTask removeTask)
    {
        RecurringTask restoreTask = removeTask.getCancelledTask();
        // Ensure the task can be restored without conflicts.
        Map<Date, Set<Timeframe>> restoredTimes = restoreTask.removeAntiTask(removeTask);
        Set<Date> affectedDates = restoredTimes.keySet();
        for (Date date : affectedDates)
        {
            Task conflictingTask = findConflictingTask(date, restoreTask);
            if (conflictingTask != null)
            {
                restoreTask.addAntiTask(removeTask);
                throw new TaskConflictException("The anti-task could not be removed since this would "
                                                + "cause the recurring task \"" + restoreTask.getTaskName()
                                                + "\" to conflict with the task \"" + conflictingTask.getTaskName()
                                                + "\", please remove the conflicting task to perform this operation.");
            }
        }
        // Reassociate the recurring task with the affected dates.
        for (Date date : affectedDates)
            addTaskOnDate(date, restoreTask);
        antiTasks.remove(removeTask);
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
     * Gets the first anti-task with the given name.
     * 
     * @param taskName The name of the task.
     * @return A reference to the task, or null if not found.
     */
    public AntiTask getAntiTask(String taskName)
    {
        for (AntiTask task : antiTasks)
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
     * Instead, use the type specific methods if possible.
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
     * Returns a set of tasks applicable to the provided day,
     * including tasks that were not created on that specific day
     * but extend in from the previous day.
     * Anti-tasks are not included in this set of tasks, and tasks
     * that no longer have an occurence on the day due to an anti-task
     * will not be included in this set.
     * 
     * @param date The date to check.
     * @return A set of tasks applicable to that date,
     *         or null if there are no tasks scheduled that day.
     */
    public Set<Task> getDailyTasks(Date date)
    {
        if (calendar.containsKey(date))
            return new TreeSet<>(calendar.get(date));
        return null;
    }

    /**
     * Returns a set of tasks in the given category.
     * 
     * @param category The category to look under.
     * @return A set of tasks in that category or null
     *         if there are no scheduled tasks within that category.
     */
    public Set<Task> getTasksByCategory(String category)
    {
        if (categories.containsKey(category))
            return new HashSet<>(categories.get(category));
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

    public ArrayList<RecurringTask> getrecTask()
    {
        return new ArrayList<RecurringTask>(recurringTasks);
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
        for (RecurringTask task : otherSchedule.recurringTasks)
            newSchedule.addTask(task);
        for (AntiTask task : otherSchedule.antiTasks)
            newSchedule.antiTasks.add(task);
        for (TransientTask task : otherSchedule.transientTasks)
            newSchedule.addTask(task);
        return newSchedule;
    }
    
}
