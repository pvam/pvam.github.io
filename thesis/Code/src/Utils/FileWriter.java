package Utils;

import Common.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileWriter {
    PrintWriter outFile;

    public void print(String operator, String s) {
        try {
            String dir = "Training/data/";
//            dir += new SimpleDateFormat("yyyyMMdd'.txt'").format(new Date());
//            dir += "/";
            new File(dir).mkdirs();

            outFile = new PrintWriter(new FileOutputStream(new File(dir+ operator + ".train"), true));
            outFile.print(s);
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void println(String operator, String s) {
        print(operator, s + "\n");
    }
}

