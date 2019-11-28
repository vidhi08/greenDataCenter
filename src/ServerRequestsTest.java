import java.util.LinkedHashMap;
import java.util.Map;

public class ServerRequestsTest {
	
	static int temperature = 20;
	static int maximum_temp = 50; //once this point is reached, turn on the cooling system
	static int minimum_temp = 20; //once this point is reached, turn off the cooling system
	static Map<String,Integer> serverMap= new LinkedHashMap<String,Integer>();
	
	static {
		serverMap.put("192.11.12.0",30); 
		serverMap.put("192.11.12.1",30);
	}
	
	public static void main (String[] args) {
		
		ServerRequests test = new ServerRequests();
		test.getServer(50);
		assert(serverMap.size() == 2) : "Error";
		assert(temperature == 30) : "Error";
	}
	
}
