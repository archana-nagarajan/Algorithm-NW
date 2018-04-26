import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
	static double e= 0.001;
    static double a= -1* Math.log(e);
    static double b=Math.pow(Math.log(2), 2);
	static int n = 0;
	static int m = 0;
	static int k = 0;
	static int bloomFilter[] = null;
	static List<String> queries = new ArrayList<>();
	public static void main(String[] args) {
		parseFile();
		initializeRandomArray();
		n= flowCount;
		m = (int) (((a*n)/(b)));
		bloomFilter = new int[m];
		k = (int) (Math.log(2) * m / n);
		onlineOperation(flowSet);
		Random rand = new Random();
		int count = 0;
		for(int i = 0; i < 100000; i++){
			int length = rand.nextInt(100);
			String query = generateString(length);
			boolean exist = membershipLookup(query);
			if(exist){
				count++;
			}
		}
		double falsePositive = count /100000.00;
		double falseNegative = (100000 - count) / 100000.00;
		System.out.println("Size of Bloom Filter (Optimal): "+ m );
		System.out.println("Number of elements encoded: "+ n );
        System.out.println("Number of elements queried: "+ 10000 );
        System.out.println("Number of hash functions: k = "+ k);
		System.out.printf("Theoretical false positive: %.3f", e);
		System.out.print("\n");
		System.out.printf("Actual False Positive rate: %.3f", falsePositive);
		System.out.print("\n");
		System.out.printf("Actual False Negative rate: %.3f", falseNegative);
	}

	private static String generateString(int count) {
		final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		for(int j = 0; j < count; j++){
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	private static boolean membershipLookup(String query) {
		boolean exist = true;
		for(int i = 0; i < k; i++){
			int hashValue = hash(query.hashCode() ^ R[i]);
			int index = Math.floorMod(hashValue, m);
			if(bloomFilter[index] != 1){
				exist = false;
				break;
			}
		}
		return exist;
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
