import java.io.File;

public class Test3 {

    public static void main(String args[]){
        String set1 = args[0];
        String set2 = args[1];
        Schedule schedule = new Schedule();
        ImportJson inTest = new ImportJson(set1);
//        inTest.getFromUI(new File("/Users/adamv/Nextcloud/Cal Poly Documents/2020 Spring/CS 3560 -Object Oriented Design and Programming/Set1.json"));
        schedule.merge(inTest.currentSched);
        ExportJson exTest = new ExportJson();



    }
}
