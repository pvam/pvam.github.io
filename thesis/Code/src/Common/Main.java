package Common;

import Utils.Database;
import Utils.FileWriter;
import Utils.ScriptExecutor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static int numberOfExperiments = 1;
    public static boolean isActual;
    public static boolean debugging = false;
    static Database database;
    //CONFIGURATION PARAMETERS TO BE SET

    static String db = "jdbc:postgresql://localhost:5432/tpch1gb";
    public static FileWriter fout;
    public static ScriptExecutor sinstance;
    static String[][] cmd = {
            {
                    "/bin/bash",
                    "-c",
                    "/home/vamshi/postgresql/bin/pg_ctl -D /home/vamshi/postgresql/dirb start"
            },

            {
                    "/bin/bash",
                    "-c",
                    "/home/vamshi/postgresql/bin/pg_ctl -D /home/vamshi/postgresql/dirb stop -m fast"
            }
            ,
            {
                    "/bin/bash",
                    "-c",
                    "sync; echo 3 | sudo tee /proc/sys/vm/drop_caches"
            }

    };
    private static int totalQueries = 210;

    public static void main(String[] args) throws Exception {
        sinstance = new ScriptExecutor();


        fout = new FileWriter();
//        database.executeQuery("set enable_nestloop to false");

        for (int i = 0; i < totalQueries; i++) {
        	  
//            shortest training
//            String query = ReadQuery("/home/vamshi/tpch-dbgen-master/training/tpch22/" + i + ".sql");

//            moderate training 10 instances of each tpch
//              String query = ReadQuery("/home/vamshi/tpch-dbgen-master/training/tpch220/" + i + ".sql");

//            large training time 100 instances of each tpch
            String query = ReadQuery("/home/vamshi/tpch-dbgen-master/training/tpch2200/" + i + ".sql");

//        	To benchmark sort.
//        	String query = "select l_orderkey from lineitem where l_orderkey <= "+ i + " order by l_commitdate";
        	
        	//To benchmark HashJoin
//        	String query = "select * from lineitem,orders where lineitem.l_orderkey = orders.o_orderkey and o_orderkey <= "+ i;
        	
        	//To benchmark Indexed Nested Loop
//        	String query = "select * from lineitem,orders where lineitem.l_orderkey = orders.o_orderkey and l_orderkey<= " + i;
        	
//            if (query==null) {
//                System.out.println("Error reading query from file ..Skipping this training query");
//                continue;
//            }
        	
            train(query);
            System.out.println("Training query " + i + " executed successfully");
        }
        
//        database.executeQuery("set enable_nestloop to true");

        String tablenames[] = {"customer","lineitem","nation","orders","part","partsupp","region","supplier"};

        database.close();
    }

    static void train(String query) {
        try {
            //Start database
            sinstance.executeCommand(cmd[0]);
            database = new Database(db);
            database.connectDB();


//            String yaml = database.getQueryResult("Explain (ANALYZE TRUE,  FORMAT YAML) " + query);
//            System.out.println("Query : \n" + query + "\n");
//            System.out.println("Node output " + yaml);
//            new Parser().parseExplain(yaml);

            //Stop database
            sinstance.executeCommand(cmd[1]);
            sinstance.executeCommand(cmd[2]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String ReadQuery(String filePath) {
        try {
            Scanner in = new Scanner(new File(filePath));
            in.nextLine();
            in.nextLine();
            String query  = in.useDelimiter("\\Z").next();
            return query;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
