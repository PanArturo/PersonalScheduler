package core;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


// Requires Gson-2.8.6 dependency
public class ImportJson
{
    Schedule currentSched = new Schedule();
    private String Name;
    private String Type;
    private String StartDate;
    private double StartTime;
    private double Duration;
    private String EndDate;
    private int Frequency;
    private String Date;

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
        currentSched = new Schedule();
        try
        {
            //Creates an ArrayList of individual task objects
            //Iterate through list
//            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(importedSet));
            ImportJson[] scheduleImported = new Gson().fromJson(reader, ImportJson[].class);
            List<ImportJson> scheduleList = Arrays.asList(scheduleImported);

            //Sort into appropriate task category then create task objects
            for (int i = 0; i < scheduleList.size(); i++)
            {
                //Transient = true
                if (!(scheduleList.get(i).Type.equals("Cancellation")) && (scheduleList.get(i).EndDate == null))
                {
                    String StartDate = scheduleList.get(i).Date;
                    int day = Integer.parseInt(StartDate.substring(6));
                    int month = Integer.parseInt(StartDate.substring(4, 6));
                    int year = Integer.parseInt(StartDate.substring(0, 4));
                    currentSched.addTask(new TransientTask(scheduleList.get(i).Name, scheduleList.get(i).Type, new Timeframe(scheduleList.get(i).StartTime,
                            scheduleList.get(i).Duration), new Date(month, day, year)));
                }

                //Recurring = true
                else if (!(scheduleList.get(i).Type.equals("Cancellation")) && !(scheduleList.get(i).Frequency == 0)) {
                    String startDate2 = scheduleList.get(i).StartDate;
                    int dayBegin = Integer.parseInt(startDate2.substring(6));
                    int monthBegin = Integer.parseInt(startDate2.substring(4, 6));
                    int yearBegin = Integer.parseInt(startDate2.substring(0, 4));
                    String endDate = scheduleList.get(i).EndDate;
                    int dayEnd = Integer.parseInt(endDate.substring(6));
                    int monthEnd = Integer.parseInt(endDate.substring(4, 6));
                    int yearEnd = Integer.parseInt(endDate.substring(0, 4));

                    currentSched.addTask(new RecurringTask(scheduleList.get(i).Name, scheduleList.get(i).Type, new Timeframe(scheduleList.get(i).StartTime,
                            scheduleList.get(i).Duration),
                            new Date(monthBegin, dayBegin, yearBegin), new Date(monthEnd, dayEnd, yearEnd), TaskFrequency.getFrequency(scheduleList.get(i).Frequency)));
                }
                //Anti Task
                else {
                    String startDate3 = scheduleList.get(i).Date;
                    int day3 = Integer.parseInt(startDate3.substring(6));
                    int month3 = Integer.parseInt(startDate3.substring(4, 6));
                    int year3 = Integer.parseInt(startDate3.substring(0, 4));
                    currentSched.addTask(new AntiTask(scheduleList.get(i).Name, new Timeframe(scheduleList.get(i).StartTime, scheduleList.get(i).Duration), new Date(month3, day3, year3)));

                }
            }

        }
        catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
