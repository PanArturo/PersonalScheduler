/**
 * A task that occurs on given weekdays at a certain time within a range of dates.
 */
public class RecurringTask extends Task
{
    public static final String[] validCategories = {"Class", "Study", "Sleep", "Exercise", "Work", "Meal"};

    private Date startingDate, endingDate;
    private TaskFrequency frequency;

    /**
     * Initializes a recurring task.
     * @param taskName The name of the task.
     * @param category The name of the category this task is part of.
     * @param startingTime A time ranging from 0 to 1440 minutes (24 hours) to represent the start of the timeframe.
     * @param duration The duration of the activity in minutes.
     * @param timeframe The timeframe of the task.
     * @param taskFrequency The number of days between each occurence of the task.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public RecurringTask(String taskName, String category, Timeframe timeframe,
                         Date startingDate, Date endingDate, TaskFrequency frequency)
    {
        super(taskName, category, timeframe);
        this.startingDate = new Date(startingDate);
        this.endingDate = new Date(endingDate);
        this.frequency = frequency;
    }

    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    @Override
    public boolean isActiveOn(Date date)
    {
        if (date.compareTo(startingDate) >= 0 && date.compareTo(endingDate) <= 0)
        {
            if (frequency.equals(TaskFrequency.DAILY))
                return true;
            else if (frequency.equals(TaskFrequency.WEEKLY))
                return startingDate.getWeekday().equals(date.getWeekday());
            else
                return startingDate.getDay() == date.getDay();
        }
        return false;
    }

    /**
     * Gets the frequency of the task.
     * @return The frequency of the task.
     */
    public TaskFrequency getFrequency()
    {
        return frequency;
    }

    /**
     * Gets the starting date. (new object to protect internal copy)
     * @return The date this task becomes active.
     */
    public Date getStartingDate()
    {
        return new Date(startingDate);
    }

    /**
     * Gets the ending date. (new object to protect internal copy)
     * @return The last date this task is active.
     */
    public Date getEndingDate()
    {
        return new Date(endingDate);
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


}
