import java.io.*;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


// Requires Gson-2.8.6 dependency
public class ImportJSON{

    Schedule currentSched = new Schedule();
    private String Name;
    private String Type;
    private String StartDate;
    private int StartTime;
    private double Duration;
    private String EndDate;
    private int Frequency;
    private String Date;

    //From UI: call this method
    public Schedule getFromUI(File passed){
        //File location
        File imported = passed;
        Importing(imported);

        return currentSched;
    }

    //Example Merge Imported Schedule
    //ImportJSON test = new ImportJSON("/Set1.json");
    //schedule.merge(test.passImport());

    //Constructor
    public ImportJSON(String s) {
        //File location
        File imported = new File(s);
        Importing(imported);

    }

    //In main: added example to instantiate the object and request merge to existing schedule
    public Schedule passImport(){
        return currentSched;
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


                //Transient = true
                if (!(importJSON.Type.equals("Cancellation")) && (importJSON.EndDate == null)) {
                    String StartDate = importJSON.Date;
                    int day = Integer.parseInt(StartDate.substring(6));
                    int month = Integer.parseInt(StartDate.substring(4, 6));
                    int year = Integer.parseInt(StartDate.substring(0, 4));
                    new TransientTask("Transient", importJSON.Type, new Timeframe(importJSON.StartTime,
                            importJSON.Duration), new Date(month, day, year));
                    currentSched.addTask(new TransientTask("Transient", importJSON.Type, new Timeframe(importJSON.StartTime,
                            importJSON.Duration), new Date(month, day, year)));
                }

                //Recurring = true
                else if (!(importJSON.Type.equals("Cancellation")) && !(importJSON.Frequency == 0)) {
                    String StartDate2 = importJSON.StartDate;
                    int dayBegin = Integer.parseInt(StartDate2.substring(6));
                    int monthBegin = Integer.parseInt(StartDate2.substring(4, 6));
                    int yearBegin = Integer.parseInt(StartDate2.substring(0, 4));
                    String EndDate = importJSON.EndDate;
                    int dayEnd = Integer.parseInt(EndDate.substring(6));
                    int monthEnd = Integer.parseInt(EndDate.substring(4, 6));
                    int yearEnd = Integer.parseInt(EndDate.substring(0, 4));

                    currentSched.addTask(new RecurringTask("Recurring", importJSON.Type, new Timeframe(importJSON.StartTime,
                            importJSON.Duration),
                            new Date(monthBegin, dayBegin, yearBegin), new Date(monthEnd, dayEnd, yearEnd), TaskFrequency.getFrequency(importJSON.Frequency)));
                }
                //Anti Task
                else {
                    String StartDate3 = importJSON.Date;
                    int day3 = Integer.parseInt(StartDate.substring(6));
                    int month3 = Integer.parseInt(StartDate.substring(4, 6));
                    int year3 = Integer.parseInt(StartDate.substring(0, 4));
                    currentSched.addTask(new AntiTask(importJSON.Name, new Timeframe(importJSON.StartTime, importJSON.Duration), new Date(month3, day3, year3)));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

