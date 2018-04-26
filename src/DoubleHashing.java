import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jfree.ui.RefineryUtilities;

public class DoubleHashing {
	String traffic = "traffic.txt";
	String outputFile = "output_example.txt";
	private Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	private List<String> outputList = new ArrayList<>();
	private HashMap<Integer, Integer> output = new HashMap<>();
	static PrintWriter writer = null;

	public static void main(String args[]) throws FileNotFoundException{
		DoubleHashing db = new DoubleHashing();
		try {
			writer = new PrintWriter("double-hashing-output.txt", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		db.parseFile(db.flowMap);
		db.readOutputFile();
		db.generateOutputFile(db.flowMap);
//		printMap(flowMap);
	}
	
	private void readOutputFile() {
		String line;
		BufferedReader buffer = null;
        try {
        	buffer = new BufferedReader(new FileReader(outputFile));
            while ((line = buffer.readLine()) != null) {
            	line = line.trim().replaceAll(" +", " ");
                String[] vals = line.trim().split("\t");
                outputList.add(vals[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        	try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
	}

	public Map<String, Set<String>> getFlowMap() {
		return this.flowMap;
	}

	public void setFlowMap(Map<String, Set<String>> flowMap) {
		this.flowMap = flowMap;
	}

	public Map<String, Set<String>> parseFile(Map<String, Set<String>> flowMap) throws FileNotFoundException {
		BufferedReader buffer = new BufferedReader(new FileReader(traffic));
		String line;
        try {
        	buffer.readLine();
            while ((line = buffer.readLine()) != null) {
            	line = line.trim().replaceAll(" +", " ");
                String[] vals = line.trim().split(" ");
                if(flowMap.containsKey(vals[0])){
                	flowMap.get(vals[0]).add(vals[1]);
                }
                else{
                	Set<String> destSet = new HashSet<>();
                	destSet.add(vals[1]);
                	flowMap.put(vals[0],destSet);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        	try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return flowMap;
	}
	
	private void generateOutputFile(Map<String, Set<String>> flowMap){
		int index = 0;
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			output.put(Integer.parseInt(outputList.get(index)), entry.getValue().size());
			writer.println(entry.getKey() + "\t" + entry.getValue().size() + "\t" + Integer.parseInt(outputList.get(index)));
			index++;
		}
		GraphGenerator chart = new GraphGenerator("Flow Cardinality", "Orginal vs Estimated", output);
	    chart.pack();          
	    RefineryUtilities.centerFrameOnScreen(chart);          
	    chart.setVisible(true); 
	}
	
//	public long getDecimal(String ip){
//		String[] sourceAddress = ip.split("\\.");
//    	long sourceIp = 0;
//    	for (int i = 0; i < sourceAddress.length; i++) {
//
//    		int power = 3 - i;
//    		int ipAddress = Integer.parseInt(sourceAddress[i]);
//    		sourceIp += ipAddress * Math.pow(256, power);
//    	}
//		return sourceIp;
//	}
//	
//	public String getIpAddress(long decimal){
//		StringBuilder result = new StringBuilder(15);
//		for (int i = 0; i < 4; i++){
//			result.insert(0,Long.toString(decimal & 0xff));
//			if (i < 3){
//				result.insert(0,'.');
//			}
//			decimal = decimal >> 8;
//		}
//		return result.toString();
//	}
	
//	private static void printMap(Map<String,Set<String>> flowMap){
//		for (Map.Entry<String,Set<String>> entry : flowMap.entrySet()){
//			System.out.println(entry.getKey() + "\t" + entry.getValue().size());
//		}
//	}
}
