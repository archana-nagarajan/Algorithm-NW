package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;


public class VB {
	final static int ORIG_SIZE=100000000;
	final static int VIRTUAL_SIZE=200;
	final static int RAND_SIZE=1000000;
	
	int B[] = new int [ORIG_SIZE];
	int R [] = new int [VIRTUAL_SIZE];
	
	Map<String,int[]> Vs = new HashMap<String,int[]>();
	
	public static void main(String[] args) throws IOException {
		
		VB vb = new VB();
		vb.init_R();
		String File = "trafficsmall.txt";
		Map<String,List<String>> inputMap = new HashMap<String,List<String>>(); 
		
		try {
			inputMap = InputParser.parseInputData(File);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
		    List<String> value = entry.getValue();
		    String key = entry.getKey();
		    int size = value.size();
		    
		    while(size>0){
		    	vb.set_bitarray(key,value.get(size-1));
		    	size--;
		    			}
		}
		
		
		int Vm_Count = vb.count_zeroes_B();
		int k = 0;
		double Vs_frac = 0;
		double Vm_frac = 0;

		Vm_frac =(double) Vm_Count / ORIG_SIZE;
		for(Map.Entry<String,List<String>> entry : inputMap.entrySet()){
			
			String key = entry.getKey();
			List<String> value = entry.getValue();
		    int size = value.size();
			int Vs_Count = vb.count_zeroes_Vs(key);
			Vs_frac = (double) Vs_Count / VIRTUAL_SIZE;
			k = (int) (( VIRTUAL_SIZE * java.lang.Math.log(Vm_frac)) + (-1 * VIRTUAL_SIZE * java.lang.Math.log(Vs_frac) ));
			System.out.println("n = " + size + " n^ = " + k);
		}
	}
 
	int count_zeroes_Vs(String key)
	 {
		 int i = 0;
		 int count = 0;
		 int V[] ;
		 V = Vs.get(key);
		 
		 for(i = 0; i < VIRTUAL_SIZE; i++) {
			 if(V[i] == 0) {
				 count++;
			 }
		 }
		 return count;
	 }
	 
	 int count_zeroes_B()
	 {
		 int i = 0;
		 int count = 0;
		 for(i = 0; i < ORIG_SIZE; i++) {
			 if(B[i] == 0) {
				 count++;
			 }
		 }
		 return count;
	 }
	 
	 void set_bitarray(String key,String value)
	 {
	 int bit_B = 0;
	 int bit_Vs = 0;
	 
	 CRC32 crc = new CRC32();
	 crc.update(value.getBytes());
	 
	 int V[];
	 if(Vs.get(key)!=null)
	 {
		V = Vs.get(key);
	 }
	 else{
		 V = new int [VIRTUAL_SIZE];
		 Vs.put(key, V);
	 }
	 
	 bit_Vs= (int)(crc.getValue() % VIRTUAL_SIZE);
	 
	 V[bit_Vs]=1;
	 
	 bit_B = (int) ((crc.getValue() ^ R[(int)(crc.getValue() % VIRTUAL_SIZE)] ) % ORIG_SIZE);
	
	 B[bit_B] = 1;
	 }
	
	 void init_R() {
	 int temprand;
	 for(int i = 0; i < VIRTUAL_SIZE; i++){
		 Random rand = new Random();
		 temprand = (int)rand.nextInt(RAND_SIZE);
		 R[i] = temprand;
	 	}
	 }
 }
