
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProbabilisticCounting {
	
	static Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	final static int SIZE = 200;
	static BitSet bitset = new BitSet(SIZE);
	static PrintWriter writer = null;
	public static void main(String[] args) {
		try {
			DoubleHashing db = new DoubleHashing();
			writer = new PrintWriter("probabilistic-counting-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			performEstimation(flowMap);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		finally{
			writer.flush();
			writer.close();
		}
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
			writer.println(entry.getKey() + "\t" + (int) spreadEstimate);
			bitset.clear();
		}
		
	}
}
