package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

// Requires Gson-2.8.6 dependency
public class ExportJson
{
    public void export(Schedule schedulePassed, File filepath)
    {
        try (Writer writer = new FileWriter(filepath))
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonArray jArray = new JsonArray();

            for(RecurringTask task : schedulePassed.getRecurringTasks())
                jArray.add(task.getJsonObject());

            for(AntiTask task : schedulePassed.getAntiTasks())
                jArray.add(task.getJsonObject());

            for(TransientTask task : schedulePassed.getTransientTasks())
                jArray.add(task.getJsonObject());

            gson.toJson(jArray, writer);

            writer.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
