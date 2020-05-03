import com.google.gson.JsonObject;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

/**
 * A task to eliminate a recurrence of conflicting tasks.
 */
public class AntiTask extends Task
{
    public static final String[] validCategories = {"Cancellation"};

    private Date activeDate;
    private Map<Date, Set<Timeframe>> activeTimes;

    private RecurringTask cancelledTask;

    /**
     * Initializes an anti-task.
     * 
     * @param taskName The name of the task / reason for cancellation.
     * @param timeframe The timeframe of the task.
     * @param date A date object representing the specific date this task applies to.
     */
    public AntiTask(String taskName, Timeframe timeframe, Date date)
    {
        super(taskName, validCategories[0], timeframe);
        this.activeDate = new Date(date);
        cancelledTask = null;
        generateActiveTimes();
    }

    /**
     * Link the anti-task with the recurring task it affects.
     * 
     * @param cancelledTask A reference to the task this anti-task affects.
     */
    public void setCancelledTask(RecurringTask cancelledTask)
    {
        this.cancelledTask = cancelledTask;
    }

    /**
     * Gets a reference to the affected recurring task.
     * 
     * @return The recurring task affected,
     *         or null if no recurring task is associated with the anti-task.
     */
    public RecurringTask getCancelledTask()
    {
        return cancelledTask;
    }

    /**
     * Gets the active date of the task. (new object to protect internal copy)
     * @return The active date of the task.
     */
    public Date getActiveDate()
    {
        return new Date(activeDate);
    }

    /**
     * Returns a copy of the dates and corresponding timeframes
     * this task will be active. Tasks that extend into a second day
     * have been accounted for and their timeframes have been truncated
     * according to each of the applicable days.
     */
    @Override
    public Map<Date, Set<Timeframe>> getScheduledTimes()
    {
        return new HashMap<Date, Set<Timeframe>>(activeTimes);
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
        if (activeTimes.containsKey(date))
            return true;
        return false;
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

    /**
     * Generates a mapping of dates this task is active
     * to the corresponding timeframes the task will take place.
     * This includes dates affected by tasks from the previous day.
     * This method must be called again if changes are made to the task dates/times.
     */
    private void generateActiveTimes()
    {
        activeTimes = new HashMap<>();
        Timeframe timeframe = getGeneralTimeframe();
        int nextDayRunoff = timeframe.getNextDayRunoff();
        if (nextDayRunoff > 0)
        {
            addDailyTimeframe(activeDate, timeframe.truncate(false));
            addDailyTimeframe(activeDate.getNextDay(), timeframe.truncate(true));
        }
        else
            addDailyTimeframe(activeDate, timeframe);
    }

    /**
     * Adds a daily timeframe to the corresponding date.
     * 
     * @param date The date to associate the timeframe with.
     * @param timeframe The timeframe to add.
     */
    private void addDailyTimeframe(Date date, Timeframe timeframe)
    {
        Set<Timeframe> dailyTimeframes;
        dailyTimeframes = new TreeSet<>();
        activeTimes.put(date, dailyTimeframes);
        dailyTimeframes.add(timeframe);
    }


    /**
     * exportHelper will be used on this anti task for formatting output
     *
     * @param task - anti Task object
     * @return Json object - will be used to add to Json array
     */
    public static JsonObject exportHelper(AntiTask task){
        JsonObject temp = new JsonObject();
        String d = dateHelper(task.getActiveDate());
        Timeframe timeF = task.getGeneralTimeframe();

        temp.addProperty("Name", task.getTaskName());
        temp.addProperty("Type", task.getCategory());
        temp.addProperty("Date", d);
        temp.addProperty("StartTime",timeF.getStartingTimeHours());
        temp.addProperty("Duration",timeF.getDurationHours());


        return temp;
    }

    private static String dateHelper(Date temp){
        int year = temp.getYear();
        int day = temp.getDay();
        int month = temp.getMonth();
        String yearString = Integer.toString(year);
        String monthString;
        String dayString;

        if(month<10) monthString = "0" + Integer.toString(month);
        else monthString = Integer.toString(month);

        if(day<10) dayString = "0" + Integer.toString(day);
        else dayString = Integer.toString(day);

        String totalDate = yearString+monthString+dayString;
        return totalDate;
    }
}
