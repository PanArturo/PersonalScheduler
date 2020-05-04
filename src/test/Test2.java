package test;

import core.*;
import java.util.Set;

/**
 * A quick example of a daily recurring task that extends into the next day,
 * which effectively makes the task have two timeframes in a single day.
 */
public class Test2
{
    public static void main(String[] args)
    {
        // Initialize Main Schedule
        Schedule schedule = new Schedule();

        // Set Recurring Task Testing Dates
        Date startingDate = new Date(1, 3, 2020);
        Date endingDate = startingDate.getNextMonth();
        Date antiTaskDate = new Date(1, 10, 2020);

        // Set Timeframe
        Timeframe taskTimeframe = new Timeframe(1380, 120);
        Timeframe otherTaskTimeframe = new Timeframe(0, 450);

        // Create Tasks For Testing
        RecurringTask weeklyRecurringTask = new RecurringTask("Weekly", "Study", taskTimeframe,
                                                              startingDate, endingDate, TaskFrequency.WEEKLY);
        RecurringTask dailyRecurringTask = new RecurringTask("Daily", "Sleep", taskTimeframe,
                                                             startingDate.getNextDay(), endingDate.getNextMonth(), TaskFrequency.DAILY);
        TransientTask transientTask = new TransientTask("One Time", "Visit", otherTaskTimeframe, startingDate);
        AntiTask antiTask = new AntiTask("Anti-Task For Daily", taskTimeframe, antiTaskDate);
        TransientTask fillVoid = new TransientTask("Fills The Void Left By Anti-Task", "Visit", taskTimeframe, antiTaskDate);
        
        // Add Daily Task
        schedule.addTask(dailyRecurringTask);

        // Task Conflict Example - Fails To Add
        try
        {
            schedule.addTask(weeklyRecurringTask);
        }
        catch (TaskConflictException e)
        {
            System.out.println(e.getMessage());
        }

        // Add Transient Task
        schedule.addTask(transientTask);

        // Add Anti-Task January 10th
        schedule.addTask(antiTask);

        // Add Transient Task To Fill The Spot
        schedule.addTask(fillVoid);

        // Check Tasks For January 2020
        viewMonth(schedule, 1, 2020);

        // Remove Anti-Task - Fails Because Of New Transient Task
        System.out.println("\nRemoving Anti-Task");
        try
        {
            schedule.removeTask(antiTask);
        }
        catch (TaskConflictException e)
        {
            System.out.println(e.getMessage());
        }

        // Remove Transient Task First Then Remove Anti-Task
        System.out.println("\nRemoving Transient Task & Removing Anti-Task");
        schedule.removeTask(fillVoid);
        schedule.removeTask(antiTask);
        
        // View Schedule Again
        System.out.println("\nNew Schedule:");
        viewMonth(schedule, 1, 2020);

        // View Tasks By Category
        System.out.println("\nTask Categories:");
        Set<String> categories = schedule.getActiveCategories();
        for (String category : categories)
        {
            System.out.println("Tasks Under " + category + ":");
            for (Task task : schedule.getTasksByCategory(category))
                System.out.println("    " + task);
        }
    }

    public static void viewMonth(Schedule schedule, int month, int year)
    {
        Date currentDate = new Date(month, 1, year);
        for (int i = 0; i < Date.getMaxDay(month, year); i++)
        {
            Set<Task> dailyTasks = schedule.getDailyTasks(currentDate);
            System.out.println(currentDate.getWeekday() + ", "
                                + currentDate.getMonthName() + " "
                                + currentDate.getDay() + ": ");
            if (dailyTasks != null && dailyTasks.size() > 0)
            {
                for (Task task : dailyTasks)
                {
                    for (Timeframe timeframe : task.getDailyTimeframes(currentDate))
                        System.out.println("  - " + task + " at " + timeframe);
                }
            }
            else
                System.out.println("  No tasks today!");
            currentDate = currentDate.getNextDay();
        }
    }
}