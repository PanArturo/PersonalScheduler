import java.io.*;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


// Requires Gson-2.8.6 dependency
public class ImportJSON {

    private String Name;
    private String Type;
    private String StartDate;
    private int StartTime;
    private double Duration;
    private String EndDate;
    private int Frequency;
    private String Date;

    //In main: ImportJSON test = new ImportJSON("/Set1.json");

    //Constructor
    public ImportJSON(String s) {
        //File location
        File imported = new File(s);
        Importing(imported);
    }


    public void Importing(File importedSet) {

        try {

            //Creates an ArrayList of individual task objects
            //Iterate through list
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(importedSet));
            ImportJSON[] scheduleImported = new Gson().fromJson(reader, ImportJSON[].class);
            List<ImportJSON> scheduleList = Arrays.asList(scheduleImported);

            //Sort into appropriate task category then create task objects
            for (ImportJSON importJSON : scheduleList) {
                String StartDate = importJSON.Date;
                int day = Integer.parseInt(StartDate.substring(6));
                int month = Integer.parseInt(StartDate.substring(4, 6));
                int year = Integer.parseInt(StartDate.substring(0, 4));

                //Transient = true
                if (!(importJSON.Type == "Cancellation") && (importJSON.EndDate == null)) {
                    new TransientTask("Transient", importJSON.Type, new Timeframe(importJSON.StartTime,
                            importJSON.Duration), new Date(month, day, year));
                }

                //Recurring = true
                else if (!(importJSON.Type == "Cancellation") && !(importJSON.Frequency == 0)) {
                    String EndDate = importJSON.EndDate;
                    int dayEnd = Integer.parseInt(EndDate.substring(6));
                    int monthEnd = Integer.parseInt(EndDate.substring(4, 6));
                    int yearEnd = Integer.parseInt(EndDate.substring(0, 4));

                    // taskFrequency class causing issues since I cannot extend (commented out temporarily)
//                    new RecurringTask("Recurring", scheduleList.get(i).Type, new Timeframe(scheduleList.get(i).StartTime,
//                            scheduleList.get(i).Duration,
//                            new Date(month, day, year), new Date(monthEnd, dayEnd, yearEnd),  new TaskFrequency(scheduleList.get(i).Frequency)));
                }
                //Anti Task
                else
                    new AntiTask(importJSON.Name, new Timeframe(importJSON.StartTime, importJSON.Duration), new Date(month, day, year));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

