package algorithms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

import org.jfree.ui.RefineryUtilities;

public class VB {
	
	final static int bitMap_size=100000000;
	final static int virtualVector_size=200;
	final static int randomArray_size=1000000;
	
	int[] B;
	int[] R;
	HashMap<String,int[]> Xsrc;
	
	public VB()
	{
		B= new int[bitMap_size];
		R= new int[randomArray_size];
		Xsrc= new HashMap<String, int[]>();
	}
	
	public void fillRandomArray()
	{
		 int random;
		 for(int i = 0; i < virtualVector_size; i++){
			 Random rand = new Random();
			 random = (int)rand.nextInt(randomArray_size);
			 R[i] = random;
		 	}	 
	}
	
	public int countZeroes_B()
	{
		int zeroes_B=0;
		for(int num : B)
		{
			if(num==0)
			{
				zeroes_B++;
			}
		}
		return zeroes_B;
		
	}
	
	public int countZeroes_X(String src)
	{
		int zeroes_X=0;
		for(int val: Xsrc.get(src))
		{
			if(val==0)
			{
				zeroes_X++;
			}
		}
		
		return zeroes_X;
	}
	
	public void set_BitMap(String src, String dest)
	{
		 int bit_B = 0;
		 int bit_Xsrc = 0;
		 
		 CRC32 crc = new CRC32();
		 crc.update(dest.getBytes());
		 bit_Xsrc= (int)(crc.getValue() % virtualVector_size);
		 
		int[] X;
		if(Xsrc.get(src)!=null)
		{
			X=Xsrc.get(src);
		}
		else
		{
			X=new int[virtualVector_size];
		}
		X[bit_Xsrc]=1;
		Xsrc.put(src, X);
		
		bit_B = (int) ((crc.getValue() ^ R[(int)(crc.getValue() % virtualVector_size)] ) % bitMap_size);
		
	    B[bit_B]=1;
		
		
		 
	}
	public static Map<String,List<String>> readFlow(String filename) throws IOException 
	{
		
		
		Map<String, List<String>> twoLevelHashTable = new HashMap<String, List<String>>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String inputLine = br.readLine();
			
			while((inputLine=br.readLine())!=null)
			{
				String[] addresses= inputLine.split("\\s+");
				String source= addresses[0];
				String destination =addresses[1];
				
				if(twoLevelHashTable.containsKey(source))
				{
					List<String> destinationAdresses = twoLevelHashTable.get(source);
					destinationAdresses.add(destination);
					twoLevelHashTable.put(source, destinationAdresses);
					
				}
				else
				{
					List<String> destinationAdresses= new ArrayList<String>();
					destinationAdresses.add(destination);
					twoLevelHashTable.put(source, destinationAdresses);
				}

			
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return twoLevelHashTable;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VB virtualBitmap= new VB();
		virtualBitmap.fillRandomArray();
		String file ="traffic.txt";
		
		Map<String,List<String>> flow= new HashMap<String,List<String>>();
		HashMap<Integer,Integer> actualVestimated = new HashMap<Integer,Integer>();// for graph
		try{
		 flow= readFlow(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Map.Entry<String, List<String>> entry : flow.entrySet()) {
			
			 List<String> destinationList = entry.getValue();
			 //int n = destinationList.size();
			 
			 for(String destination : destinationList)
			 {
				 virtualBitmap.set_BitMap(entry.getKey(), destination);
			 }
		}
		
		int Vm_Count = virtualBitmap.countZeroes_B();
		int n_cap = 0;
		double Vs_frac = 0;
		double Vm_frac = 0;
		
		Vm_frac =(double) Vm_Count / bitMap_size;
		
			
		for (Map.Entry<String, List<String>> entry : flow.entrySet()) {
			
			String src = entry.getKey();
			List<String> destinations = entry.getValue();
		    int n = destinations.size();
			int Vs_Count = virtualBitmap.countZeroes_X(src);
			Vs_frac = (double) Vs_Count / virtualVector_size;
			
			n_cap = (int) (( virtualVector_size * java.lang.Math.log(Vm_frac)) - ( virtualVector_size * java.lang.Math.log(Vs_frac) ));
			if(n<=5000 && n_cap<= 5000){
			actualVestimated.put(n, n_cap);
			}
			
		}
		for(Map.Entry<Integer, Integer> entry : actualVestimated.entrySet()){  // for graph
			int key = entry.getKey();
			int value = entry.getValue();
			System.out.println("n: "+key+" n^: "+value);
		}
		
		
		

	}

}

