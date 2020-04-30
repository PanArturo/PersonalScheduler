import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

// Requires Gson-2.8.6 dependency
public class ExportJson
{
    public void export(Schedule schedulePassed)
    {
        Set<RecurringTask> recurringTasks = schedulePassed.getRecurringTasks();
        Set<AntiTask> antiTasks = schedulePassed.getAntiTasks();
        Set<TransientTask> transientTasks = schedulePassed.getTransientTasks();
        try
        {
            Writer writer = new FileWriter("Schedule.json");

            // convert taskTemp to JSON file
            new Gson().toJson(recurringTasks, writer);
            new Gson().toJson(antiTasks, writer);
            new Gson().toJson(transientTasks, writer);

            writer.close();

        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
