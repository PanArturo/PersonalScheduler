/**
 * A task to eliminate a recurrence of conflicting tasks.
 */
public class AntiTask extends Task
{
    public static final String[] validCategories = {"Cancellation"};

    private Date activeDate;

    /**
     * Initializes an anti-task.
     * @param taskName The name of the task / reason for cancellation.
     * @param timeframe The timeframe of the task.
     * @param date A date object representing the specific date this task applies to.
     */
    public AntiTask(String taskName, Timeframe timeframe, Date date)
    {
        super(taskName, validCategories[0], timeframe);
        this.activeDate = new Date(date);
    }

    /**
     * Checks whether a task is active on a particular date.
     * @param date A date object representing the date to check.
     * @return True if the task is active on the specified day, false otherwise.
     */
    @Override
    public boolean isActiveOn(Date date)
    {
        if (activeDate.equals(date))
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
}
