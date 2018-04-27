import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

public class VirtualFM {
	static Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	private static HashMap<Integer, Integer> output = new HashMap<>();
	final static int arraySize = 10000000;
	final static int randSize = 1000000;
	static int fmArray[] = new int[arraySize];
	static int noOfFmSketches = 128;
	static PrintWriter writer = null;
	static int R [] = new int [noOfFmSketches];
	static Random rand = new Random();
	static double phi = 0.77351;
	static double k = 1.75;
	public static void main(String[] args) {
		DoubleHashing db = new DoubleHashing();
		initializeRandomArray();
		try {
			writer = new PrintWriter("virtual-fm-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			onlineOperation();
			offlineOperation();
			printGraph(output);
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally{
			writer.flush();
			writer.close();
		}
	}
	private static void offlineOperation() {
		int countWhole = 0;
		double zWhole = 0.0;
		for(int i = 0; i < arraySize; i++){
			countWhole += calculateZ(fmArray[i]);
		}
		zWhole = (double) countWhole / arraySize;
		double tempValue = Math.pow(2, zWhole/arraySize) - Math.pow(2, (-k * (zWhole/arraySize)));
		double nCap = (arraySize * tempValue) / phi;
		int fmSketches[] = null;
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			String source = entry.getKey();
			fmSketches = new int[noOfFmSketches];
			int count = 0;
			for(int i = 0; i < noOfFmSketches; i++){
				fmSketches[i] = Math.floorMod(source.hashCode() ^ R[i], arraySize);
				count += calculateZ(fmSketches[i]);
			}
			double z = (double) count / noOfFmSketches;
			double temp = Math.pow(2, z/noOfFmSketches) - Math.pow(2, (-k * (z/noOfFmSketches)));
			double flowSpread = (noOfFmSketches * temp) / phi;
			double estimatedCardinality = ((arraySize * noOfFmSketches) / (arraySize - noOfFmSketches)) * 
					((flowSpread / noOfFmSketches) - (nCap / arraySize));
			if(entry.getValue().size() <= 100 && (int) Math.ceil(estimatedCardinality) <= 100){
				writer.println(entry.getKey() + "\t\t" + entry.getValue().size() + "\t\t" + (int) Math.ceil(estimatedCardinality));
				output.put(entry.getValue().size(), (int) Math.ceil(estimatedCardinality));
			}
		}
	}
	
	private static void onlineOperation() {
		int fmSketches[] = null;
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			String source = entry.getKey();
			fmSketches = new int[noOfFmSketches];
			for(int i = 0; i < noOfFmSketches; i++){
				fmSketches[i] = Math.floorMod(source.hashCode() ^ R[i], arraySize);
			}
			Set<String> destinations = entry.getValue();
			for(String dest : destinations){
				int fmSketchIndex = Math.floorMod(dest.hashCode(), noOfFmSketches);
				// index in the entire array
				int index = fmSketches[fmSketchIndex];
				int gHash = geometricHash(index);
				fmArray[index] |= 1 << gHash;
			}
		}
	}
	
	private static int geometricHash(int index)
	{
		if (index == 0)
			return 32;
		int count = 0;
		if ((index & 0xFFFF0000) == 0){
			count += 16; 
			index =index << 16;
		} //1111 1111 1111 1111 0000 0000 0000 0000 // 16 bits from left are zero! so we omit 16left bits
		if ((index & 0xFF000000) == 0){ 
			count = count +  8; 
			index = index <<  8;
		} // 8 left bits are 0
		if ((index & 0xF0000000) ==0){ 
			count = count +  4; 
			index = index <<  4;
		} // 4 left bits are 0
		if ((index & 0xC0000000) == 0){ 
			count =count +  2;
			index = index <<  2;
		}  // 110000....0 2 left bits are zero
		if ((index & 0x80000000) == 0){
			count = count +  1; 
			index = index <<  1;
		} // first left bit is zero
		return count;
	}
	
	private static int calculateZ (int index) {
		int zeroes;
		zeroes = ~index & (index+1);   // this gives a 1 to the left of the trailing 1's
		zeroes--;              // this gets us just the trailing 1's that need counting
		zeroes = (zeroes & 0x55555555) + ((zeroes>>1) & 0x55555555);  // 2 bit sums of 1 bit numbers
		zeroes = (zeroes & 0x33333333) + ((zeroes>>2) & 0x33333333);  // 4 bit sums of 2 bit numbers
		zeroes = (zeroes & 0x0f0f0f0f) + ((zeroes>>4) & 0x0f0f0f0f);  // 8 bit sums of 4 bit numbers
		zeroes = (zeroes & 0x00ff00ff) + ((zeroes>>8) & 0x00ff00ff);  // 16 bit sums of 8 bit numbers
		zeroes = (zeroes & 0x0000ffff) + ((zeroes>>16) & 0x0000ffff); // sum of 16 bit numbers
		return zeroes;
	    
	}
	private static void printGraph(HashMap<Integer, Integer> output) {
		GraphGenerator chart = new GraphGenerator("Flow Cardinality", "Orginal vs Estimated", output);
	    chart.pack();          
	    RefineryUtilities.centerFrameOnScreen(chart);          
	    chart.setVisible(true); 
		
	}
	
	static void initializeRandomArray(){
		int temp;
		for(int i = 0; i < noOfFmSketches; i++){
			Random rand = new Random();
			temp = (int)rand.nextInt(randSize);
			R[i] = temp;
		}
	}
	 
	public static int hash(long key){
		key = (~key) + (key << 18); // key = (key << 18) - key - 1;
		key = key ^ (key >>> 31);
		key = key * 21; // key = (key + (key << 2)) + (key << 4);
		key = key ^ (key >>> 11);
		key = key + (key << 6);
		key = key ^ (key >>> 22);
		return (int) key;
	}
}
