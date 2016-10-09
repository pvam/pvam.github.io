package Common;

import Utils.Database;
import Utils.Parser;
import Utils.ScriptExecutor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;


public class Test {
    static Database database;
    //CONFIGURATION PARAMETERS TO BE SET


    //TO-DO: Test in night for 2GB.
    static String db = "jdbc:postgresql://localhost:5432/tpch1gb";
    static String root = "/home/vamshi/";
    private static int totalQueries = 20;
    static ScriptExecutor sinstance;
    static String[] drop = {
            "/bin/bash",
            "-c",
            "echo 3 | sudo tee /proc/sys/vm/drop_caches"
    };

    static double[] actual,estimated,error,optimizer;
    static boolean invalid[];
    static int i;

    public static void main(String[] args) {
        try {
            database = new Database(db);
            database.connectDB();
            sinstance = new ScriptExecutor();
            
            actual = new double[totalQueries];
            estimated = new double[totalQueries];
            optimizer = new double[totalQueries];
            invalid = new boolean[totalQueries];
            error = new double[totalQueries];
            
            
            for (i = 0; i < totalQueries; i++) {
                estimated[i] = 0.0;
                String query = ReadQuery(root + "tpch-dbgen-master/testing/tpch22/" + i + ".sql");
                test(query);
                System.out.println("Testing query "+i +"done");
            }

            compareResults(actual, optimizer, "Actual vs Optimizers");
            compareResults(actual, estimated, "Actual vs Current model");
            
            System.out.println("Optimizer model : Individual queries performance");
            for(int i=0 ; i <totalQueries; ++i) {
            	if(!invalid[i])
            		System.out.println("Query "+ i + ": " + RE(actual[i], optimizer[i]));
            }
            
            System.out.println("Current model : Individual queries performance");
            for(int i=0 ; i <totalQueries; ++i) {
            	if(!invalid[i])
            		System.out.println("Query "+ i + ": " + RE(actual[i], estimated[i]));
            }
            
            
            for(int i=0 ; i <totalQueries; ++i) {
            	if(!invalid[i]) {
            		//Optimizer cost estimate VS Actual time
            		System.out.println(optimizer[i] + " "+ actual[i]);
            	}
            }
            
 
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    static void compareResults(double act[],double[] est, String title) {
    	int cnt = 0;
        double min = Double.MAX_VALUE , max = -1;
        double MRE = 0;
        for (int j = 0; j < totalQueries; j++) {
//            System.out.println("Query "+ j+ " considered : "  +  (invalid[j] ? "no" : "yes"));
            if (!invalid[j]) {
                ++cnt;
                double error = RE(act[j],est[j]);
                MRE += error;
                min = Math.min(min, error);
                max = Math.max(max, error);
            }
        }
        MRE /= cnt;

        System.out.println("\n-------------------------------");
        System.out.println("Results "+ title);
        System.out.println("-------------------------------");


        System.out.println("Total queries: 22");
        System.out.println("Total trained queries: 21");
        System.out.println("Total tested queries: " + cnt + "/" + "21");
        System.out.println("Mean relative error: " +MRE);
        System.out.println("Minimum relative error: " +min);
        System.out.println("Maximum relative error: " +max);

    }

    static double RE(double a,double b) {
        return Math.abs(a-b)/a;
    }

    private static void test(String query) {
        sinstance.executeCommand(drop);
        //Experiment with true cardinalities
        String yaml = database.getQueryResult("Explain (ANALYZE TRUE,  FORMAT YAML) " + query);
//        System.out.println(yaml);
        parseExplain(yaml);
    }

    public static void parseExplain(String text) {
        Yaml yaml = new Yaml();
        List<LinkedHashMap<String, LinkedHashMap<String, String>>> mm = (List<LinkedHashMap<String, LinkedHashMap<String, String>>>) yaml.load(text);
        LinkedHashMap<String, LinkedHashMap<String, String>> info = mm.get(0);
        LinkedHashMap plan = info.get("Plan");

        //Store actual time for explain analyze command
        actual[i] = (double) plan.get("Actual Total Time");
        optimizer[i] = (double) plan.get("Total Cost");

        Parser parser = new Parser();
        estimated[i] = parser.dfs(plan, false);
    }


    private static double getCost(String operator, List<Double> features) {
        double cost;
        StringBuilder output = new StringBuilder();
        
    	double nl = features.get(0);
    	double nr = features.get(1);
    	
    	output.append(operator+" ");
        output.append(nl+" ");
        output.append(nr+" ");
        output.append(nl*nr+" ");
        if(nr!=0) {
            output.append(nl * log2(nr)+" ");
            output.append(nr * log2(nr)+" ");
            }else {
            	output.append("0 0 ");
            }
            
            if(nl!=0) {
            output.append(nr * log2(nl)+" ");
            output.append(nl * log2(nl)+" ");
            }else {
            	output.append("0 0 ");
            }
            
        String parameters = output.toString();

        String commands[] = {"/bin/bash" ,"-c", "python Training/findCost.py "+parameters};

        String returnedValue = sinstance.executeCommand(commands);
        System.out.println("Parameters " + parameters + " Returned cost " + returnedValue);
        cost  = Double.parseDouble(returnedValue);
        return cost;
    }
    
	private static double log2(double nr) {
		// TODO Auto-generated method stub
		return Math.log10(nr)/ Math.log10(2);	
	}

    private static long getActualRows(LinkedHashMap root) {
        int Nl = (int) root.get("Plan Rows");
        int removed = 0;
        if (root.containsKey("Rows Removed by Filter"))
            removed = (int) root.get("Rows Removed by Filter");

        return Nl + removed;
    }

    static String ReadQuery(String filePath) {
        try {
            Scanner in = new Scanner(new File(filePath));
            in.nextLine();
            in.nextLine();
            String query  = in.useDelimiter("\\Z").next();
            return query;
        } catch (IOException e) {
            return null;
        }
    }
}

//Results with input normalization
