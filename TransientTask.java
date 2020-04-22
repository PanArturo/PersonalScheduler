/**
 * A task object to represent tasks that only occur once on a specific date.
 */
public class TransientTask extends Task
{
    public static final String[] validCategories = {"Visit", "Shopping", "Appointment"};

    private Date activeDate;

    /**
     * Initializes a transient task.
     * @param taskName The name of the task.
     * @param category The name of the category this task is part of.
     * @param timeframe The timeframe of the task.
     * @param date A date object representing the specific date this task applies to.
     * @throws InvalidTaskException If the provided category is not valid.
     */
    public TransientTask(String taskName, String category, Timeframe timeframe, Date date)
    {
        super(taskName, category, timeframe);
        this.activeDate = new Date(date);
    }

    @Override
    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    public boolean isActiveOn(Date date)
    {
        if (activeDate.equals(date))
            return true;
        return false;
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
     * Gets an array of valid categories for the task.
     * @return An array of valid categories for the task.
     */
	@Override
    public String[] getValidCategories()
    {
		return validCategories;
	}
}
