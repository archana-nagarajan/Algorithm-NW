import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class CountMin {

	private static int width;
	private static int depth;
	private static int[][] dataStructure = null;
	static Map<String, Integer> flowMap = new LinkedHashMap<>();
	static PrintWriter writer = null;
	private static int flowCount = 0;
	static String traffic = "traffic.txt";
	final static int SIZE = 150;
	static int R [] = new int [SIZE];

	public CountMin(int d, int w) {
		width = w;
		depth = d;
		dataStructure = new int[depth][width];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getDepth() {
		return depth;
	}
	public static void main(String[] args) {
		try {
			writer = new PrintWriter("CountMin-output.txt", "UTF-8");
			parseFile();
			new CountMin(3, flowCount);
			onlineOperation(flowMap);
			offlineOperation(flowMap);
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

	private static void parseFile() {
		BufferedReader buffer;
		try {
			buffer = new BufferedReader(new FileReader(traffic)); 
			String line;
        	buffer.readLine();
            while ((line = buffer.readLine()) != null) {
            	line = line.trim().replaceAll(" +", " ");
                String[] vals = line.trim().split(" ");
                String flowId = vals[0] + vals[1];
                flowMap.put(flowId,Integer.parseInt(vals[2]));
                flowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private static void offlineOperation(Map<String, Integer> flowMap2) {
		for (Entry<String, Integer> entry : flowMap.entrySet()){
			int min = Integer.MAX_VALUE;
			for(int i = 0; i < depth; i++){
				int hash = hash(entry.getKey().hashCode() ^ R[i]);
				int index = Math.floorMod(hash, width);
				min = Math.min(min, dataStructure[i][index]);
			}
			writer.println(entry.getKey() + "\t\t" + min + "\t\t" + entry.getValue());
		}
	}

	private static void onlineOperation(Map<String, Integer> flowMap2) {
		for (Entry<String, Integer> entry : flowMap.entrySet()){
			for(int i = 0; i < depth; i++){
				for(int j = 0; j < entry.getValue(); j++){
					int hash = hash(entry.getKey().hashCode() ^ R[i]);
					int index = Math.floorMod(hash, width);
					dataStructure[i][index] = dataStructure[i][index] + 1;
				}
			}
		}
	}
	
	static void initializeRandomArray(){
		int temp;
		for(int i = 0; i < SIZE; i++){
			Random rand = new Random();
			temp = (int)rand.nextInt(flowCount);
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
