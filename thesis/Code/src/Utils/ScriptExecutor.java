package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptExecutor {
    public String executeCommand(String cmd[]) {
        Process p;
        try {
            String ret = "";
            Runtime r = Runtime.getRuntime();
            p = r.exec(cmd);
            int exitval = p.waitFor();

            StreamGobbler errorGobbler = new
                    StreamGobbler(p.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(p.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            Thread.sleep(2000);

            return String.valueOf(exitval);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return null;
    }

}

class StreamGobbler extends Thread
{
    InputStream is;
    String type;

    StreamGobbler(InputStream is, String type)
    {
        this.is = is;
        this.type = type;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null)
                System.out.println(type + ">" + line);
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}