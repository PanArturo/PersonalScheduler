import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Set;

// Requires Gson-2.8.6 dependency
public class ExportJSON {


    public void Export(Schedule schedulePassed){

        // Get calendar map keyset
        Set<Date> keySetpr = schedulePassed.calendar.keySet();

        // For each date in hashmap, place hashset into temp and write to json
        for (Date outerMap : keySetpr) {
            Set<Task> taskTemp;
            taskTemp = schedulePassed.calendar.get(outerMap);

            try {

                Writer writer = new FileWriter("Schedule.json");

                // convert taskTemp to JSON file
                new Gson().toJson(taskTemp, writer);

                writer.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
