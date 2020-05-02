import java.io.*;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


// Requires Gson-2.8.6 dependency
public class ImportJson
{
    Schedule currentSched = new Schedule();
    private String name;
    private String type;
    private String startDate;
    private int startTime;
    private double duration;
    private String endDate;
    private int frequency;
    private String date;

    //From UI: call this method
    public Schedule getFromUI(File passed)
    {
        //File location
        File imported = passed;
        importing(imported);

        return currentSched;
    }

    public void getFromUINoReturn(File passed){
        //File location
        File imported = passed;
        importing(imported);
    }

    //Example Merge Imported Schedule
    //ImportJSON test = new ImportJSON("/Set1.json");
    //schedule.merge(test.passImport());

    //Constructor
    public ImportJson(){
        //Instantiate ImportJSON obj then call getFromUI
    }

    public ImportJson(String s)
    {
        //File location
        File imported = new File(s);
        importing(imported);

    }

    //Grab imported schedule to pass to merge method against existing schedule
    public Schedule passImport()
    {
        return currentSched;
    }


    public void importing(File importedSet)
    {
        try
        {
            //Creates an ArrayList of individual task objects
            //Iterate through list
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(importedSet));
            ImportJson[] scheduleImported = new Gson().fromJson(reader, ImportJson[].class);
            List<ImportJson> scheduleList = Arrays.asList(scheduleImported);

            //Sort into appropriate task category then create task objects
            for (ImportJson importJSON : scheduleList)
            {
                //Transient = true
                if (!(importJSON.type.equals("Cancellation")) && (importJSON.endDate == null))
                {
                    String StartDate = importJSON.date;
                    int day = Integer.parseInt(StartDate.substring(6));
                    int month = Integer.parseInt(StartDate.substring(4, 6));
                    int year = Integer.parseInt(StartDate.substring(0, 4));
                    currentSched.addTask(new TransientTask("Transient", importJSON.type, new Timeframe(importJSON.startTime,
                            importJSON.duration), new Date(month, day, year)));
                }

                //Recurring = true
                else if (!(importJSON.type.equals("Cancellation")) && !(importJSON.frequency == 0)) {
                    String startDate2 = importJSON.startDate;
                    int dayBegin = Integer.parseInt(startDate2.substring(6));
                    int monthBegin = Integer.parseInt(startDate2.substring(4, 6));
                    int yearBegin = Integer.parseInt(startDate2.substring(0, 4));
                    String endDate = importJSON.endDate;
                    int dayEnd = Integer.parseInt(endDate.substring(6));
                    int monthEnd = Integer.parseInt(endDate.substring(4, 6));
                    int yearEnd = Integer.parseInt(endDate.substring(0, 4));

                    currentSched.addTask(new RecurringTask("Recurring", importJSON.type, new Timeframe(importJSON.startTime,
                            importJSON.duration),
                            new Date(monthBegin, dayBegin, yearBegin), new Date(monthEnd, dayEnd, yearEnd), TaskFrequency.getFrequency(importJSON.frequency)));
                }
                //Anti Task
                else {
                    String startDate3 = importJSON.date;
                    int day3 = Integer.parseInt(startDate.substring(6));
                    int month3 = Integer.parseInt(startDate.substring(4, 6));
                    int year3 = Integer.parseInt(startDate.substring(0, 4));
                    currentSched.addTask(new AntiTask(importJSON.name, new Timeframe(importJSON.startTime, importJSON.duration), new Date(month3, day3, year3)));

                }
            }

        }
        catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
