import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

// Requires Gson-2.8.6 dependency
public class ExportJson
{
    public void export(Schedule schedulePassed, File filepath){

       // String location = filepath.getPath();
        Set<RecurringTask> recurringTasks = schedulePassed.getRecurringTasks();
        Set<AntiTask> antiTasks = schedulePassed.getAntiTasks();
        Set<TransientTask> transientTasks = schedulePassed.getTransientTasks();

        RecurringTask[] recurTask = recurringTasks.toArray( new RecurringTask[recurringTasks.size()] );
        AntiTask[] antiArray = antiTasks.toArray( new AntiTask[antiTasks.size()] );
        TransientTask[] transArray = transientTasks.toArray( new TransientTask[transientTasks.size()] );

        Writer writer = null;
        try {
            writer = new FileWriter(filepath);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jArray = new JsonArray();

        for(int i = 0; i < recurTask.length; i++) {
            JsonObject temp = new JsonObject();
            temp = RecurringTask.exportHelper(recurTask[i]);
            jArray.add(temp);
        }

        for(int i = 0; i < antiArray.length;i++)
        {
            JsonObject temp = new JsonObject();
            temp = AntiTask.exportHelper((antiArray[i]));
            jArray.add(temp);
        }

        for(int i = 0; i < transArray.length;i++)
        {
            JsonObject temp = new JsonObject();
            temp = TransientTask.exportHelper((transArray[i]));
            jArray.add(temp);
        }

         gson.toJson(jArray, writer);

        writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
