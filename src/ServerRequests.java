import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Servlet implementation class ServerRequests
 */
@WebServlet("/ServerRequests")
public class ServerRequests extends HttpServlet implements Runnable {

    ArrayList < String > stack = new ArrayList < String > ();
    static int temperature = 20;
    static int maximum_temp = 50; //once this point is reached, turn on the cooling system
    static int minimum_temp = 20; //once this point is reached, turn off the cooling system
    Instant start_time = Instant.now();
	Instant end_time = Instant.now();
    Duration responsetime;
    int max_threshold_perserver = 30;
    HashMap<String, String> serverObject = new HashMap<>(); 

    static Map < String, Integer > serverMap = new LinkedHashMap < String, Integer > ();
    //static Map<String,Integer> intialserverMap= new HashMap<String,Integer>();
    private final static Logger LOGGER = Logger.getLogger(ServerRequests.class.getName());
    String server_nm = "192.11.12";
    int total_load = 0;
    Integer i = 3;
    static {
        serverMap.put("192.11.12.0", 30);
        serverMap.put("192.11.12.1", 30);
    }

    public ServerRequests() {
        checktotalload();
    }
    public int checktotalload() {

        for (Map.Entry mapElement: serverMap.entrySet()) { //int server_capacity

            total_load = total_load + ((int) mapElement.getValue());
            LOGGER.log(Level.FINE, "checking the server load");
        }
        return total_load;
    }

    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        Runnable runnable = new ServerRequests();
        Thread thread = new Thread(runnable);
        thread.start();
        response.setContentType("text/html;charset=UTF-8");
        start_time = Instant.now();
        int count = Integer.parseInt(request.getParameter("server_count"));
        LOGGER.log(Level.FINE, "number of servers active" + count);
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {}
        for (Integer i = 0; i < count; i++) {
            stack.add("Job " + i.toString());
        }
        System.out.println(count + " jobs on the stack");
        System.out.println("server count" + count);
        System.out.println("server time " + start_time);
        addServer(count);
        getServer(count);
        end_time = Instant.now();
        responsetime = Duration.between(start_time, end_time);
        for (Integer i = count - 1; i > -1; i--) {
            stack.remove(i);
        }
        System.out.println("Stack empty");
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {}
        LOGGER.log(Level.FINE, "Time taken: "+ responsetime.toMillis() +" milliseconds");
        System.out.println("Time taken: "+ responsetime.toMillis() +" milliseconds");
        thread.stop();
        /*
         * HttpSession session =request.getSession(true); String count =(String)
         * session.getAttribute("server_count"); System.out.println("server count"
         * +count);
         */
		 request.setAttribute("responsetime", responsetime.toMillis());
		 request.setAttribute("request_count", count);
		 request.setAttribute("max_threshold_perserver", max_threshold_perserver);
		 int j = 0;
		 Iterator it = serverMap.entrySet().iterator();
		 List<String> serverNames = new ArrayList<String>();
		 while (it.hasNext()) {
	    	j++;
	        Map.Entry pair = (Map.Entry)it.next();
	        serverNames.add((String) pair.getKey());
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
		 }

		 request.setAttribute("serverMap", serverNames);
		 request.setAttribute("server_count", j);
		 request.getRequestDispatcher("/flow.jsp").forward(request,response);
    }

    public void getServer(int count) {
        System.out.println(temperature);
        boolean flag = true;
        int counter;
        int index = 0;
        Map < String, Integer > latestServerMap = new HashMap < String, Integer > ();
        int position = 0;
        Set < String > keySet = serverMap.keySet();
        ArrayList < String > serverList = new ArrayList < String > ();
        serverList.addAll(keySet);
        counter = serverList.size();
        while (counter != 0) {

            String server_name = serverList.get(index);
            if (serverMap.containsKey(server_name)) { //if null, it has been removed, so no need to check any further
                int server_capacity = serverMap.get(server_name);
                if (count < server_capacity)

                {
                    LOGGER.log(Level.ALL, " server assigned " + server_name);
                    System.out.println(" server assigned " + server_name);
                    serverMap.replace(server_name, Math.abs(server_capacity - count));
                    flag = false;
                    deleteServer(count);
                }
            }
            counter--;
            index++;
        }
        if (flag) {
            int difference = 0;
            for (Map.Entry mapElement: serverMap.entrySet()) {
                int server_capacity = (int) mapElement.getValue();
                difference = Math.abs(count - server_capacity); //50-30=20; 20-30=10;
                int temp = server_capacity; //30
                if (difference >= 0 && count > server_capacity) {
                    serverMap.replace((String) mapElement.getKey(), temp - server_capacity); //30;
                    LOGGER.log(Level.FINE, "Server Allocated " + mapElement.getKey() + " " + mapElement.getValue());
                    System.out.println("Server Allocated " + mapElement.getKey() + " " + mapElement.getValue());
                } else if (count <= server_capacity) {
                    serverMap.replace((String) mapElement.getKey(), server_capacity - count); //30;
                    LOGGER.log(Level.FINE, "Server Allocated " + mapElement.getKey() + " " + mapElement.getValue());
                    System.out.println("Server Allocated " + mapElement.getKey() + " " + mapElement.getValue());

                }
                temperature += 5; //turning on a server will heat up data centre
                if (temperature > maximum_temp) {
                    while (temperature > minimum_temp) {
                        temperature -= 5; //must cool off before running any more jobs
                        System.out.println(temperature);
                    }
                }
                count = difference; //20
            }
        }

        System.out.println("Servers being used :");
        displayServerDetails();

    }
    //if requests more than current server capacity, add new server
    // if request less than current server capacity , remove the servers.
    public void addServer(int count) {
        boolean flag = true;
        if (count > total_load) //
        {
            int difference = Math.abs(total_load - count); //90
            while (flag) {
                //60-150 =90
                if (difference <= max_threshold_perserver) //90<30 ,60<30
                {
                    LOGGER.log(Level.ALL, "capacity available in new server added");
                    serverMap.put(server_nm.concat(i.toString()), difference);
                    flag = false;
                } else {
                    int capacity_created = Math.abs(max_threshold_perserver - difference); //90-30 =60 ,30-60 =30
                    LOGGER.log(Level.ALL, "capacity unavailable in new server added");
                    serverMap.put(server_nm.concat(i.toString()), max_threshold_perserver); //30
                    difference = capacity_created; //60,30

                }
                i++;
            }
            System.out.println("new Server added ");

            displayServerDetails();
        }


    }
    public void deleteServer(int count) {

        if (count < total_load) {
            ArrayList < String > toBeRemoved = new ArrayList < String > ();
            Set < String > keys = serverMap.keySet();
            for (String k: keys) {
                int capacity = serverMap.get(k);
                if (count < total_load - capacity) {
                    LOGGER.log(Level.ALL, "Remove server");
                    toBeRemoved.add(k); //can't remove from a map within a loop, so must remove later
                    if (temperature > minimum_temp) {
                        temperature -= 5; //turning off a server reduces heat, but can't go below minimum
                    }
                }

            }
            System.out.println(" Server Deleted ");
            displayServerDetails();
        }
    }
    public void displayServerDetails() {
        //No need to test this, all it does is print
        for (Map.Entry mapElement: serverMap.entrySet()) { //int server_capacity
            System.out.println(mapElement.getKey() + " " + mapElement.getValue());
        }
    }


    public static void main(String[] args) {

    }
    @Override
    public void run() {
        // No need to test this, all it does is print
        System.out.println("MY class is running");
    }

}