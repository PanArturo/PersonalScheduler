/**
 * Oscar Bedolla
 * Charles Bickham - UI Team
 * Natalie Dinh - UI Team
 * Markus Hernandez - Coding Team
 * Christopher Leung - JSON Team
 * Arturo Pan Loo - UI Team
 * Adam VanRiper - JSON Team
 */
package core;

/**
 * An exception for conflicting tasks.
 */
public class TaskConflictException extends RuntimeException
{
    /**
     * Creates an exception with additional information.
     * @param attemptedTask The task the program attempted to add.
     * @param conflictingTask The existing task conflicting with the attempted task.
     */
    public TaskConflictException(Task attemptedTask, Task conflictingTask)
    {
        super("The task \"" + attemptedTask + "\" conflicts with an exiting task: \"" + conflictingTask + "\"");
    }

    /**
     * Creates an exception with additional information.
     * @param message A message about the exception.
     */
    public TaskConflictException(String message)
    {
        super(message);
    }

    /**
     * Creates an exception without additional information.
     */
    public TaskConflictException()
    {
        super();
    }
}
