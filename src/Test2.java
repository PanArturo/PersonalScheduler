import java.util.Set;

/**
 * A quick example of a daily recurring task that extends into the next day,
 * which effectively makes the task have two timeframes in a single day.
 */
public class Test2
{
    public static void main(String[] args)
    {
        Schedule schedule = new Schedule();

        Date startingDate;
        Date endingDate;
        Timeframe taskTimeframe;
        Timeframe otherTaskTimeframe;
        RecurringTask weeklyRecurringTask;
        RecurringTask dailyRecurringTask;
        TransientTask transientTask;


        // Set Dates
        startingDate = new Date(1, 3, 2020);
        endingDate = startingDate.getNextMonth();

        // Set Timeframe
        taskTimeframe = new Timeframe(1380, 120);
        otherTaskTimeframe = new Timeframe(0, 450);

        // Set Tasks
        weeklyRecurringTask = new RecurringTask("Weekly", "Study", taskTimeframe,
                                          startingDate, endingDate, TaskFrequency.WEEKLY);
        dailyRecurringTask = new RecurringTask("Daily", "Sleep", taskTimeframe,
                                                                     startingDate.getNextDay(), endingDate.getNextMonth(), TaskFrequency.DAILY);
        transientTask = new TransientTask("Transient", "Visit", otherTaskTimeframe, startingDate);
        
        // Add To Schedule
        schedule.addTask(dailyRecurringTask);

        // Task Conflict Example
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