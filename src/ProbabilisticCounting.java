

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ProbabilisticCounting {
	
	static Map<Long,Set<Long>> flowMap = new LinkedHashMap<>();
	final static int SIZE = 200;
	static BitSet bitset = new BitSet(SIZE);
	static PrintWriter writer = null;
	public static void main(String[] args) {
		try {
			DoubleHashing db = new DoubleHashing();
			writer = new PrintWriter("probabilistic-counting-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			for (Map.Entry<Long,Set<Long>> entry : flowMap.entrySet()){
				Set<Long> destinations = entry.getValue();
				for(Long dest : destinations){
					bitset.set(Math.floorMod(dest.hashCode() , SIZE));
				}
				int zeroes = SIZE - bitset.cardinality();
				double emptyBitsFraction = (double) zeroes / SIZE;
				double spreadEstimate = -1 * SIZE * (Math.log(emptyBitsFraction));
				writer.println(db.getIpAddress(entry.getKey()) + "\t" + (int) spreadEstimate);
				bitset.clear();
			}
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
}
