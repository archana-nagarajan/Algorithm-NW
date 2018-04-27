import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.ui.RefineryUtilities;

import java.util.Random;
import java.util.Set;

public class VirtualBitmap {
	
	static Map<String, Set<String>> flowMap = new LinkedHashMap<>();
	private static HashMap<Integer, Integer> output = new HashMap<>();
	final static int BIT_ARRAY_SIZE = 100000000;
	final static int VIRTUAL_VECTOR_SIZE = 200;
	final static int RAND_SIZE=1000000;
	static BitSet bitArray = new BitSet(BIT_ARRAY_SIZE);
	static int R [] = new int [VIRTUAL_VECTOR_SIZE];
	static Map<String, BitSet> virtual = new LinkedHashMap<>();
	static PrintWriter writer = null;
	public static void main(String[] args) {
		DoubleHashing db = new DoubleHashing();
		initializeRandomArray();
		try {
			writer = new PrintWriter("virtual-bitmap-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			onlineOperation(flowMap);
			offlineOperation(flowMap);
			printGraph(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally{
			writer.flush();
			writer.close();
		}
	}
	
	private static void printGraph(HashMap<Integer, Integer> output) {
		GraphGenerator chart = new GraphGenerator("Flow Cardinality", "Orginal vs Estimated", output);
	    chart.pack();          
	    RefineryUtilities.centerFrameOnScreen(chart);          
	    chart.setVisible(true); 
	}
	
	private static void offlineOperation(Map<String, Set<String>> flowMap2) {
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			String source = entry.getKey();
			BitSet virtualVector  = new BitSet(VIRTUAL_VECTOR_SIZE);
			for(int i = 0; i < VIRTUAL_VECTOR_SIZE; i++){
				int value = Math.floorMod(hash(source.hashCode() ^ R[i]), BIT_ARRAY_SIZE);
				int virtualValue = Math.floorMod(hash(source.hashCode() ^ R[i]) , VIRTUAL_VECTOR_SIZE);
				if(bitArray.get(value))
					virtualVector.set(virtualValue);
			}
			virtual.put(source, virtualVector);
		}
//		BitSet virtualVector = null;
//		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
//			String source = entry.getKey();
//			int i = 0;
//			Set<String> destinations = entry.getValue();
//			for(String dest : destinations){
//				if(virtual.containsKey(source))
//					virtualVector  = virtual.get(source);
//				else{
//					virtualVector = new BitSet(VIRTUAL_VECTOR_SIZE);
//					virtual.put(source, virtualVector);
//				}
//				int destHash = Math.floorMod(dest.hashCode() , BIT_ARRAY_SIZE);
//				int value = Math.floorMod(hash(source.hashCode() ^ R[destHash]) , BIT_ARRAY_SIZE);
//				int virtualValue = Math.floorMod(hash(source.hashCode() ^ R[i]) , VIRTUAL_VECTOR_SIZE);
//				if(bitArray.get(value))
//					virtualVector.set(virtualValue);
//				i++;
//			}
//		}
		long numberOfZeroBitsInBitArray =  BIT_ARRAY_SIZE - bitArray.cardinality();
		double bBitsFraction = (double) numberOfZeroBitsInBitArray / BIT_ARRAY_SIZE;
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			BitSet v = virtual.get(entry.getKey());
			int sourceVectorCount = VIRTUAL_VECTOR_SIZE - v.cardinality();
			double virtualVectorFraction = (double) sourceVectorCount / VIRTUAL_VECTOR_SIZE;
			double flowSpread = (double) Math.abs (VIRTUAL_VECTOR_SIZE * Math.log(bBitsFraction) - VIRTUAL_VECTOR_SIZE * Math.log(virtualVectorFraction));
			if(entry.getValue().size() <= 150 && flowSpread <= 150){
				output.put(entry.getValue().size(), (int) Math.ceil(flowSpread));
				writer.println(entry.getKey() + "\t" + entry.getValue().size() + "\t" + (int) Math.ceil(flowSpread));
			}
			
		}
	}

	private static void onlineOperation(Map<String, Set<String>> flowMap2) {
		for (Entry<String, Set<String>> entry : flowMap.entrySet()){
			Set<String> destinations = entry.getValue();
			String source = entry.getKey();
			for(String dest : destinations){
				int destHash = Math.floorMod(dest.hashCode() , VIRTUAL_VECTOR_SIZE);
				Long xor = (long) (source.hashCode() ^ R[(int) destHash]);
				int sourceHash = hash(xor);
				int index = Math.floorMod(sourceHash, BIT_ARRAY_SIZE);
				bitArray.set(index);
			}
		}
	}

	static void initializeRandomArray(){
		int temp;
		for(int i = 0; i < VIRTUAL_VECTOR_SIZE; i++){
			Random rand = new Random();
			temp = (int)rand.nextInt(RAND_SIZE);
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
