import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ExportJSON {
    private String Name;
    private String Type;
    private String StartDate;
    private int StartTime;
    private double Duration;
    private String EndDate;
    private int Frequency;
    private String Date;

    public void Export(Date dateObj, Schedule schedulePassed){

        // create a list of tasks
        // List<ExportJSON> scheduleAr = null; (Still testing, need clarification on how data is being exported)
        List<Task> scheduleAr = null;

        //Current date
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();
        Date currentDate = new Date(month, day, year);

        //Iterate through months
        for (int i = currentDate.getMonth(); i < 12; i++) {
            //Iterate through days
            for (int j = currentDate.getDay(); j < dateObj.getMaxDay(month, year); j++) {
                Set<Task> dailyTasks = schedulePassed.getDailyTasks(currentDate);

                if (dailyTasks != null && dailyTasks.size() > 0) {
                    for (Task task : dailyTasks) {
                        scheduleAr.add(task);
                    }
                } else
                currentDate = currentDate.getNextDay();
            }
            currentDate.getNextMonth();
            month += 1;
        }

        try {

            // create writer
            Writer writer = new FileWriter("Schedule.json");

            // convert users list to JSON file
            new Gson().toJson(scheduleAr, writer);

            // close writer
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
}
}
