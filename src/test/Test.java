/**
 * Oscar Bedolla
 * Charles Bickham - UI Team
 * Natalie Dinh - UI Team
 * Markus Hernandez - Coding Team
 * Christopher Leung - JSON Team
 * Arturo Pan Loo - UI Team
 * Adam VanRiper - JSON Team
 */
package test;

import core.*;
import java.util.Set;

/**
 * A quick example of various runtime exceptions and
 * a conflict between a transient and recurring task.
 */
public class Test
{
    public static void main(String[] args)
    {
        Schedule schedule = new Schedule();

        Date startingDate;
        Date endingDate;
        Timeframe taskTimeframe;
        Timeframe otherTaskTimeframe;
        RecurringTask recurringTask;
        TransientTask conflictingTransientTask;
        TransientTask transientTask;

        // Bad Date Example
        try
        {
            startingDate = new Date(1, 32, 2020);
        }
        catch (InvalidDateException e)
        {
            System.out.println(e.getMessage());
        }

        // Set Dates
        startingDate = new Date(1, 3, 2020);
        endingDate = startingDate.getNextMonth();

        // Bad Timeframe Example
        try
        {
            taskTimeframe = new Timeframe(1415, 15);
        }
        catch (InvalidTimeframeException e)
        {
            System.out.println(e.getMessage());
        }

        // Another Bad Timeframe Example
        try
        {
            taskTimeframe = new Timeframe(1380, 10);
        }
        catch (InvalidTimeframeException e)
        {
            System.out.println(e.getMessage());
        }

        // Set Timeframe
        taskTimeframe = new Timeframe(1380, 120);
        otherTaskTimeframe = new Timeframe(0, 450);

        // Bad Category Example
        try
        {
            recurringTask = new RecurringTask("Recurring", "Random", taskTimeframe,
                                              startingDate, endingDate, TaskFrequency.WEEKLY);
        }
        catch (InvalidTaskException e)
        {
            System.out.println(e.getMessage());
        }

        // Set Tasks
        recurringTask = new RecurringTask("Recurring", "Study", taskTimeframe,
                                          startingDate, endingDate, TaskFrequency.WEEKLY);
        conflictingTransientTask = new TransientTask("Conflict", "Visit", taskTimeframe, startingDate);
        transientTask = new TransientTask("Transient", "Visit", otherTaskTimeframe, startingDate);
        
        // Add To Schedule
        schedule.addTask(recurringTask);

        // Task Conflict Example
        try
        {
            schedule.addTask(conflictingTransientTask);
        }
        catch (TaskConflictException e)
        {
            System.out.println(e.getMessage());
        }

        // Add Transient Task
        schedule.addTask(transientTask);

        // Check Tasks For January 2020
        Date currentDate = new Date(1, 1, 2020);
        for (int i = 0; i < Date.getMaxDay(1, 2020); i++)
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