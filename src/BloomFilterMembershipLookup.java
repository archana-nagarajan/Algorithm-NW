import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BloomFilterMembershipLookup {
	static Set<String> flowSet = new LinkedHashSet<>();
	static PrintWriter writer = null;
	private static int flowCount = 0;
	static String traffic = "traffic.txt";
	final static int SIZE = 150;
	final static int QUERYSIZE = 1500;
	static int R [] = new int [SIZE];
	static int n = 0;
	static int m = 0;
	static int k = 0;
	static int bloomFilter[] = null;
	static List<String> queries = new ArrayList<>();
	public static void main(String[] args) {
		try {
			writer = new PrintWriter("Bloomfilter-output.txt", "UTF-8");
			parseFile();
			initializeRandomArray();
			m = flowCount *10;
			n = flowCount;
			bloomFilter = new int[m];
			k = (int) (Math.log(2) * m / n);
			onlineOperation(flowSet);
			List<String> flowlist = new ArrayList<>(flowSet);
			pickRandomFlows(flowlist);
			offlineOperation(queries);
			System.out.println();
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

	private static void pickRandomFlows(List<String> flowlist) {
		int temp;
		for(int i = 0; i < QUERYSIZE; i++){
			Random rand = new Random();
			temp = (int)rand.nextInt(flowCount);
			queries.add(flowlist.get(temp));
		}
	}

	private static void offlineOperation(List<String> queries) {
		boolean exist = true;
		for(String query : queries){
			exist = true;
			for(int i = 0; i < k; i++){
				int hashValue = hash(query.hashCode() ^ R[i]);
				int index = Math.floorMod(hashValue, m);
				if(bloomFilter[index] != 1){
					exist = false;
				}
			}
			writer.println(query + "\t\t" + exist);
		}
	}

	private static void onlineOperation(Set<String> flowSet) {
		for (String id : flowSet){
			for(int i = 0; i < k; i++){
				int hashValue = hash(id.hashCode() ^ R[i]);
				int index = Math.floorMod(hashValue, m);
				bloomFilter[index] =  1;
			}
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
                String flowId = vals[0] + "&" + vals[1];
                flowSet.add(flowId);
                flowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
