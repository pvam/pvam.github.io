package Common;

import Utils.ScriptExecutor;

/**
 * Created by vamshi on 22/1/16.
 */
public class TestCommand {
    public static void main(String[] args) {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String[] cmd = {"/bin/bash", "-c","python Training/findCost.py SeqScan 6004252.0 0.0 0.0 0 0 0.0 1.352010632227833E8"};
//       String cmd[] = {"/bin/bash", "-c", "./test.sh"};
        String ret = scriptExecutor.executeCommand(cmd);
        System.out.println(ret);
    }

}
