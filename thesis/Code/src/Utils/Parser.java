package Utils;

import Common.Main;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class Parser {

    //Contains attributes which should not be considered as a features.
    String[] excludeList = {"Actual Total Time", "Actual Startup Time", /*"Startup Cost", "Total Cost",*/ "Plan Rows", "Rows Removed by Filter"
    , "Rows Removed by Join Filter", "Rows Removed by Index Recheck"};

    public void parseExplain(String text) {
        Yaml yaml = new Yaml();
        List<LinkedHashMap<String, LinkedHashMap<String, Object>>> mm = (List<LinkedHashMap<String, LinkedHashMap<String, Object>>>) yaml.load(text);
        LinkedHashMap<String, LinkedHashMap<String, Object>> info = mm.get(0);
        LinkedHashMap<String,Object> plan = info.get("Plan");
        dfs(plan, true);
    }

    /*
    @Doc
    Returns cost in testing mode.
    Returns insignificant value in-case of testing mode.
     */
    public double dfs(LinkedHashMap<String,Object> root,boolean trainingMode) {
        List<Pair> rootFeatures = getAllParameters(root);
        List<Pair> leftFeatures = getRowsWidthParameters(null);
        List<Pair> rightFeatures = getRowsWidthParameters(null);

        if (root==null) return 0;
        String operator = String.valueOf(root.get("Node Type")).replaceAll("\\s","");

        if(root.containsKey("Sort Method"))
        	operator += "-" + String.valueOf(root.get("Sort Method")).replaceAll("\\s","");
        if(root.containsKey("Strategy"))
        	operator += "-"+ String.valueOf(root.get("Strategy")).replaceAll("\\s","");

        double leftCost = -1, rightCost = -1;
        if (root.containsKey("Plans"))
        {
            List<LinkedHashMap<String, LinkedHashMap<String, String>>> children;
            children = (List<LinkedHashMap<String, LinkedHashMap<String, String>>>) root.get("Plans");
            int size = children.size();
            
            
//            System.out.println(operator);
            LinkedHashMap leftChild = children.get(0);

            if(size ==1) {
                //Unary operator
                leftFeatures = getRowsWidthParameters(leftChild);

                leftCost = dfs(leftChild,trainingMode);
            }
            else if(size ==2) {
                LinkedHashMap rightChild = children.get(1);

                leftFeatures = getRowsWidthParameters(leftChild);

                rightFeatures = getRowsWidthParameters(rightChild);


                if(operator.equals("NestedLoop"))
                {
            		 operator += ((String) rightChild.get("Node Type")).replaceAll("\\s","");
                }
                
                leftCost = dfs(leftChild,trainingMode);
                rightCost = dfs(rightChild,trainingMode);

            } else {
                System.out.println("Currently not supported, Number of children are "+ size);
            }
        }


        double target = (double) root.get("Actual Total Time");

        /*
         * TODO : Next
         * 1. Analyze which queries are performing bad  
         * 2. In the query is there a way to know which operator is performing bad?
         * 3. For that operator, try increasing features(and if possible training data accordingly) to see whether that transforms to more an accurate model? [it should ;) ]
         * 4. It is clear that dependencies between features need to be covered to gain an accurate model. This is more important than linear/non-linear model  
         */

        if (trainingMode) {
            /*
            TODO
            pass root, left and child parameters separately
            */
            addTrainingTuple(operator,rootFeatures,leftFeatures,rightFeatures,target);
            System.out.println("Added training data to files");
            return 0;
        } else {
            return getCost(operator,rootFeatures,leftFeatures,rightFeatures) + leftCost + rightCost;
        }
    }

    private List<Pair> getRowsWidthParameters(LinkedHashMap root) {
        List<Pair> ret = new ArrayList<>();
        if(root==null) {
            Pair zDummy = new Pair("dummy",0.0);
            ret.add(zDummy);
            ret.add(zDummy);
            return ret;
        }
        ret.add(new Pair("Actual Rows" , (int) root.get("Actual Rows")));
        ret.add(new Pair("Plan Width" , (int) root.get("Plan Width")));
        return ret;
    }

    private List<Pair> getAllParameters(LinkedHashMap<String, Object> root) {
        List<Pair> ret = new ArrayList<>();

        for (Map.Entry<String,Object> entry : root.entrySet()) {
            // entry.getValue() is of type User now
            String key = entry.getKey();
            if(containedInExcludeList(key)) continue;
            double fv = -1;
            Object value = entry.getValue();
            if (value instanceof ArrayList) {
                //Find whether this contains a LinkedHashMap or not?
                ArrayList arrayList = (ArrayList) value;
                if(! (arrayList.get(0) instanceof LinkedHashMap)) {
                    fv = arrayList.size();
                }
            }
            else if (value instanceof Integer) {
                fv =  (Integer)value;
            }
            else if (value instanceof Double) {
                fv =  (Double)value;
            }

//            System.out.println("Key => "+ key);
//            System.out.println("Value  name ,=> "+ fv);

            if (fv!=-1)
                ret.add(new Pair(key,fv));
        }

        addOptionalParameters(root,ret);

        return ret;
    }

    private void addOptionalParameters(LinkedHashMap<String, Object> root, List<Pair> ret) {
        if (root.containsKey("Rows Removed by Filter"))
            ret.add(new Pair("Rows Removed by Filter", (int) root.get("Rows Removed by Filter")));
        else if (root.containsKey("Rows Removed by Join Filter"))
            ret.add(new Pair("Rows Removed by Join Filter", (int) root.get("Rows Removed by Join Filter")));
        else if (root.containsKey("Rows Removed by Index Recheck"))
            ret.add(new Pair("Rows Removed by Index Recheck", (int) root.get("Rows Removed by Index Recheck")));

        else
            ret.add(new Pair("ZDummy", 0.0));

        //Keep on adding optional parameters.
    }

    private boolean containedInExcludeList(String key) {
        for (String next: excludeList)
            if (next.compareToIgnoreCase(key)==0) return true;
        return false;
    }

    private long getActualRows(LinkedHashMap root) {
        int Nl = (int) root.get("Plan Rows");
        int removed = 0;
        if (root.containsKey("Rows Removed by Filter"))
            removed = (int) root.get("Rows Removed by Filter");
        if (root.containsKey("Rows Removed by Join Filter"))
            removed = (int) root.get("Rows Removed by Join Filter");

        return Nl + removed;
    }

    public void addTrainingTuple(String node, List<Pair> rootFeatures,List<Pair> leftFeatures,List<Pair> rightFeatures, double target) {
        StringBuilder output = getWholeStringBuilder(rootFeatures, leftFeatures, rightFeatures);
        output.append(target);
        System.out.println("Data " + node+ " , "+ output.toString());
        Main.fout.println(node, output.toString());
    }

    public StringBuilder getWholeStringBuilder(List<Pair> rootFeatures, List<Pair> leftFeatures, List<Pair> rightFeatures) {
        StringBuilder output = getStringBuilder(rootFeatures);
        if (leftFeatures != null)
            output.append(getStringBuilder(leftFeatures));
        if (rightFeatures!=null)
            output.append(getStringBuilder(rightFeatures));
        return output;
    }

    public StringBuilder getStringBuilder(List<Pair> features) {
        Collections.sort(features, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return o1.key.compareToIgnoreCase(o2.key);
            }
        });

        StringBuilder output = new StringBuilder();
        for(Pair next: features) {
            output.append(next.value);
            output.append(" ");
        }
        return output;
    }


    private double log2(double nr) {
		// TODO Auto-generated method stub
		return Math.log10(nr)/ Math.log10(2);	
	}

    public double getCost(String operator, List<Pair> rootFeatures, List<Pair> leftFeatures, List<Pair> rightFeatures) {
        double cost;
        StringBuilder parameters = new StringBuilder(operator);
        parameters.append(" ");
        parameters.append(getWholeStringBuilder(rootFeatures,leftFeatures,rightFeatures));

        String commands[] = {"/bin/bash" ,"-c", "python Training/findCost.py "+parameters};

        String returnedValue = new ScriptExecutor().executeCommand(commands);
        System.out.println("Parameters " + parameters + " Returned cost " + returnedValue);
        cost  = Double.parseDouble(returnedValue);
        return cost;
    }

}
