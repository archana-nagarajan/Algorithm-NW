

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class VirtualBitmap {
	
	static Map<Long,Set<Long>> flowMap = new LinkedHashMap<>();
	final static int BIT_ARRAY_SIZE = 10000000;
	final static int VIRTUAL_VECTOR_SIZE = 150;
	final static int RAND_SIZE=1000000;
	static BitSet bitArray = new BitSet(BIT_ARRAY_SIZE);
	static int R [] = new int [VIRTUAL_VECTOR_SIZE];
	static Map<Long, BitSet> virtual = new LinkedHashMap<>();
	static PrintWriter writer = null;
	public static void main(String[] args) {
		DoubleHashing db = new DoubleHashing();
		Random rand = new Random();
		initializeRandomArray();
		try {
			writer = new PrintWriter("virtual-bitmap-output.txt", "UTF-8");
			flowMap = db.parseFile(flowMap);
			for (Map.Entry<Long,Set<Long>> entry : flowMap.entrySet()){
				Set<Long> destinations = entry.getValue();
				long source = entry.getKey();
				for(Long dest : destinations){
					int destHash = Math.floorMod(dest.hashCode() , VIRTUAL_VECTOR_SIZE);
//					long randomValue = rand.nextInt(destHash <= 0 ? 1 : destHash);
//					System.out.println(randomValue);
					Long xor = source ^ R[(int) destHash];
					int sourceHash = xor.hashCode();
					int index = Math.floorMod(sourceHash, BIT_ARRAY_SIZE);
//					System.out.println(index);
					bitArray.set(index);
				}
			}
//			for (Map.Entry<Long,Set<Long>> entry : flowMap.entrySet()){
//				long source = entry.getKey();
//				for(int i = 0; i < VIRTUAL_VECTOR_SIZE; i++){
//					long randomValue = rand.nextInt(VIRTUAL_VECTOR_SIZE);
//					Long xor = source ^ R[(int) randomValue];
//					int sourceHash = xor.hashCode();
//					int index = Math.floorMod(sourceHash, VIRTUAL_VECTOR_SIZE);
//					BitSet virtualVector = null;
//					if(virtual.containsKey(source))
//						virtualVector  = virtual.get(source);
//					else{
//						virtualVector = new BitSet(VIRTUAL_VECTOR_SIZE);
//						virtual.put(source, virtualVector);
//					}
//					virtualVector.set(index);
//				}
//			}
			for (Map.Entry<Long,Set<Long>> entry : flowMap.entrySet()){
				long source = entry.getKey();
				Set<Long> destinations = entry.getValue();
				BitSet virtualVector = null;
				for(Long dest : destinations){
					if(virtual.containsKey(source))
						virtualVector  = virtual.get(source);
					else{
						virtualVector = new BitSet(VIRTUAL_VECTOR_SIZE);
						virtual.put(source, virtualVector);
					}
					int destHash = Math.floorMod(dest.hashCode() , VIRTUAL_VECTOR_SIZE);
					virtualVector.set(destHash);
				}
			}
			
			long numberOfZeroBitsInBitArray =  BIT_ARRAY_SIZE - bitArray.cardinality();
			double bBitsFraction = (double) numberOfZeroBitsInBitArray / BIT_ARRAY_SIZE;
			for (Map.Entry<Long,Set<Long>> entry : flowMap.entrySet()){
				BitSet v = virtual.get(entry.getKey());
				int sourceVectorCount = VIRTUAL_VECTOR_SIZE - v.cardinality();
				double virtualVectorFraction = (double) sourceVectorCount / VIRTUAL_VECTOR_SIZE;
//				System.out.println(bBitsFraction);
//				System.out.println(virtualVectorFraction);
				long flowSpread = (long) (VIRTUAL_VECTOR_SIZE * Math.log(bBitsFraction) - VIRTUAL_VECTOR_SIZE * Math.log(virtualVectorFraction));
				writer.println(db.getIpAddress(entry.getKey()) + "\t" + (int) flowSpread);
			}
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
	
	 static void initializeRandomArray() {
		 int temp;
		 for(int i = 0; i < VIRTUAL_VECTOR_SIZE; i++){
			 Random rand = new Random();
			 temp = (int)rand.nextInt(RAND_SIZE);
			 R[i] = temp;
		 	}
		 }


}
