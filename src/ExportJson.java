import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

// Requires Gson-2.8.6 dependency
public class ExportJson
{
    public void export(Schedule schedulePassed)
    {
        Set<RecurringTask> RecTasks = schedulePassed.getRecurringTasks();
        Set<TransientTask> TransTasks = schedulePassed.getTransientTasks();
        Set<AntiTask> AntiTasks = schedulePassed.getAntiTasks();
        try
        {
            Writer writer = new FileWriter("Schedule.json");

            // convert taskTemp to JSON file
            new Gson().toJson(RecTasks, writer);
            new Gson().toJson(TransTasks, writer);
            new Gson().toJson(AntiTasks, writer);

            writer.close();

        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}