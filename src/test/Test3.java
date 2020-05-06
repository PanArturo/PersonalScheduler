/**
 * Oscar Bedolla
 * Charles Bickham - UI Team
 * Natalie Dinh - UI Team
 * Markus Hernandez - Coding Team
 * Christopher Leung - JSON Team
 * Arturo Pan Loo - UI Team
 * Adam VanRiper - JSON Team
 */
package test;

import core.*;
import java.io.File;

public class Test3 {

    public static void main(String args[]){
        String set1 = args[0];
        Schedule schedule = new Schedule();
        ImportJson inTest = new ImportJson(set1);
        schedule = schedule.merge(inTest.passImport());
        ExportJson exTest = new ExportJson();
        exTest.export(schedule, new File(set1.concat("exported.json")));
    }
}
