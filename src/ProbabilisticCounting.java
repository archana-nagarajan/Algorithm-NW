
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

import java.util.Set;

public class ProbabilisticCounting {
	
	static Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	private static HashMap<Integer, Integer> output = new HashMap<>();
	final static int SIZE = 200;
	static BitSet bitset = new BitSet(SIZE);
	static PrintWriter writer = null;
	public static void main(String[] args) {
		try {
			DoubleHashing db = new DoubleHashing();
			writer = new PrintWriter("probabilistic-counting-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			performEstimation(flowMap);
			printGraph(output);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	private static void printGraph(HashMap<Integer, Integer> output) {
		GraphGenerator chart = new GraphGenerator("Flow Cardinality", "Orginal vs Estimated", output);
	    chart.pack();          
	    RefineryUtilities.centerFrameOnScreen(chart);          
	    chart.setVisible(true); 
		
	}
	private static void performEstimation(Map<String, Set<String>> flowMap2) {
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			Set<String> destinations = entry.getValue();
			for(String dest : destinations){
				bitset.set(Math.floorMod(dest.hashCode() , SIZE));
			}
			int zeroes = SIZE - bitset.cardinality();
			double emptyBitsFraction = (double) zeroes / SIZE;
			double spreadEstimate = -1 * SIZE * (Math.log(emptyBitsFraction));
			if(spreadEstimate <= 3000){
				output.put(entry.getValue().size(), (int) spreadEstimate);
				writer.println(entry.getKey() + "\t" + entry.getValue().size() + "\t" + (int) spreadEstimate);
			}
			bitset.clear();
		}
		
	}
}
