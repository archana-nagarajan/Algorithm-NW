

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DoubleHashing {
	String traffic = "traffic.txt";
	private Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	

	public static void main(String args[]) throws FileNotFoundException{
		DoubleHashing db = new DoubleHashing();
		db.parseFile(db.flowMap);
		db.generateOutputFile(db.flowMap);
//		printMap(flowMap);
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
//                long sourceIp = getDecimal(vals[0]);
//                long destIp = getDecimal(vals[1]);
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
	
	private void generateOutputFile(Map<String, Set<String>> flowMap2){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("double-hashing-output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (Entry<String, Set<String>> entry : flowMap2.entrySet()){
			writer.println(entry.getKey() + "\t" + entry.getValue().size());
		}
		writer.close();
	}
	
	public long getDecimal(String ip){
		String[] sourceAddress = ip.split("\\.");
    	long sourceIp = 0;
    	for (int i = 0; i < sourceAddress.length; i++) {

    		int power = 3 - i;
    		int ipAddress = Integer.parseInt(sourceAddress[i]);
    		sourceIp += ipAddress * Math.pow(256, power);
    	}
		return sourceIp;
	}
	
	public String getIpAddress(long decimal){
		StringBuilder result = new StringBuilder(15);
		for (int i = 0; i < 4; i++){
			result.insert(0,Long.toString(decimal & 0xff));
			if (i < 3){
				result.insert(0,'.');
			}
			decimal = decimal >> 8;
		}
		return result.toString();
	}
	
//	private static void printMap(Map<String,Set<String>> flowMap){
//		for (Map.Entry<String,Set<String>> entry : flowMap.entrySet()){
//			System.out.println(entry.getKey() + "\t" + entry.getValue().size());
//		}
//	}
}
