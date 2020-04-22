import java.util.Hashtable;
import java.util.List;

/**
 * A schedule keeps track of the complete list of tasks a user has, including anti-tasks.
 * The schedule can use this information to compile a list of tasks on a specific date.
 */
public class Schedule
{
    private Hashtable<Date, List<Task>> calendar;
    private Hashtable<Date, List<RecurringTask>> recurringTasks;

    /**
     * Initializes a schedule.
     */
    public Schedule()
    {
        calendar = new Hashtable<>();
        recurringTasks = new Hashtable<>();
    }

    // add tasks

    // get task by name

    // remove task by name / reference

    // get tasks for dates

    // merge schedules to make new schedule
}
