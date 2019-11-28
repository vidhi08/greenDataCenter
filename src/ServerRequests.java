

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

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
	
	
	int start_time ;
	 int end_time;
	 int responsetime;
	 
		static Map<String,Integer> serverMap= new LinkedHashMap<String,Integer>();
		//static Map<String,Integer> intialserverMap= new HashMap<String,Integer>();
		private final static Logger LOGGER= Logger.getLogger(ServerRequests.class.getName());
		String server_nm ="192.11.12";
		int total_load = 0;
		Integer i=3;
		static {
			serverMap.put("192.11.12.0",30); 
			serverMap.put("192.11.12.1",30);
		}
		
		public ServerRequests()
		{
			checktotalload();
		}
		public int checktotalload()
		{	 
			
			for (Map.Entry mapElement : serverMap.entrySet()) { //int server_capacity
				
	        total_load =total_load + ((int)mapElement.getValue());
	        LOGGER.log(Level.FINE,"checking the server load");
			 }
		return total_load;
		}
		
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			Runnable runnable = new ServerRequests();
			Thread thread = new Thread(runnable);
			thread.start();
			response.setContentType("text/html");
		   start_time =(int) System.currentTimeMillis();
			  int count =Integer.parseInt(request.getParameter("server_count"));
			  LOGGER.log(Level.FINE,"number of servers active"+count);
			  try {
			 Thread.currentThread().sleep(1000);
			  }
			  catch(InterruptedException e) {}
			 System.out.println("server count" +count);
			 addServer(count);
			 getServer(count);
			 displayServerDetails();
			 end_time =(int) System.currentTimeMillis();
			 
			 responsetime =end_time-start_time;
			 try {
			 Thread.currentThread().sleep(1000);
			 }catch(InterruptedException e) {}
			 LOGGER.log(Level.FINE,"response time"+responsetime);
			 System.out.println ("total time taken " + responsetime);
			thread.stop();
			/*
			 * HttpSession session =request.getSession(true); String count =(String)
			 * session.getAttribute("server_count"); System.out.println("server count"
			 * +count);
			 */
		}
		
		public  void getServer(int count)
		{
			Runnable runnable = new ServerRequests();
			Thread thread = new Thread(runnable);
			thread.start();
			boolean flag=true;
			int counter; int index =0;
			Map<String,Integer> latestServerMap= new HashMap<String,Integer>();
			int position =0;
			Set<String> keySet = serverMap.keySet();      
	        ArrayList<String> serverList = new ArrayList<String>();      
	        serverList.addAll(keySet);
	        counter=serverList.size();
	        while (counter!=0) {
	         String server_name =serverList.get(index);
	          int server_capacity=serverMap.get(server_name);
	          if(count <server_capacity)
	 
	           {  
	        	  try {
	     			 Thread.currentThread().sleep(1000);
	     			  }
	     			  catch(InterruptedException e) {}
	        	  LOGGER.log(Level.ALL," server assigned "+ server_name);
	        	  System.out.println(" server assigned "+ server_name);
	        	   serverMap.replace(server_name,Math.abs(server_capacity - count)); 
	        	 flag =false;
	        	 deleteServer(count);
	           } 
	         counter --;
	         index ++;  
	        } 
	        if(flag)
	        {
	        	int difference=0;
	        	for (Map.Entry mapElement : serverMap.entrySet()) { 
	        	int server_capacity =(int)mapElement.getValue();
	        	difference=Math.abs(count -server_capacity); //50-30=20; 20-30=10;
	        	int temp =server_capacity; //30
	        	if(difference>=0 && count >server_capacity) {
	        		serverMap.replace((String)mapElement.getKey(),temp-server_capacity);//30;
	        		try {
	       			 Thread.currentThread().sleep(1000);
	       			  }
	       			  catch(InterruptedException e) {}
	        		LOGGER.log(Level.FINE,"Server Allocated " +mapElement.getKey() + " " +  mapElement.getValue());
	        		System.out.println("Server Allocated " +mapElement.getKey() + " " +  mapElement.getValue());
	        	}
	        	else if(count <= server_capacity) 
	        	{     
	        		serverMap.replace((String)mapElement.getKey(),server_capacity-count); //30;
	        		try {
		       			 Thread.currentThread().sleep(1000);
		       			  }
		       			  catch(InterruptedException e) {}
	   		         LOGGER.log(Level.FINE,"Server Allocated "+mapElement.getKey() + " " +  mapElement.getValue());
	        		System.out.println("Server Allocated "+mapElement.getKey() + " " +  mapElement.getValue());
	        		
	        	}
	        	count =difference ;//20
	        	}
	        }
	        try {
      			 Thread.currentThread().sleep(1000);
      			  }
      			  catch(InterruptedException e) {}
	       
	         System.out.println("Servers being used :");
	         displayServerDetails();
	         thread.stop();
	         
		}
		//if requests more than current server capacity, add new server
		 // if request less than current server capacity , remove the servers.
		public void addServer(int count)
		{  
			Runnable runnable = new ServerRequests();
			Thread thread = new Thread(runnable);
			thread.start();
			boolean flag =true;
			int max_threshold_perserver =30;
			if(count > total_load)  //
			{
				int difference =Math.abs(total_load -count); //90
				while (flag)
				{
				 //60-150 =90
				if (difference <= max_threshold_perserver) //90<30 ,60<30
					
				{   
					try {
		       			 Thread.currentThread().sleep(1000);
		       			  }
		       			  catch(InterruptedException e) {}
					LOGGER.log(Level.ALL,"capacity available in new server added");
					serverMap.put(server_nm.concat(i.toString()),difference);
					flag =false;
				}
				else 
				{ 
						int capacity_created= Math.abs(max_threshold_perserver -difference); //90-30 =60 ,30-60 =30
						try {
			       			 Thread.currentThread().sleep(1000);
			       			  }
			       			  catch(InterruptedException e) {}
						LOGGER.log(Level.ALL,"capacity unavailable in new server added");
						serverMap.put(server_nm.concat(i.toString()),max_threshold_perserver); //30
				       difference =capacity_created; //60,30
						
					}
				i++;
				}
				try {
	       			 Thread.currentThread().sleep(1000);
	       			  }
	       			  catch(InterruptedException e) {}
				System.out.println("new Server added ");
				
				displayServerDetails();
				thread.stop();
			}
			
			
		}
		public void deleteServer (int count)
		{
			Runnable runnable = new ServerRequests();
			Thread thread = new Thread(runnable);
			thread.start();
			if(count <total_load) 
			{
				Set<String> keys = serverMap.keySet();
		        for(String k:keys){
		        	int capacity=serverMap.get(k);
		        	if(count <total_load- capacity) {
		        		try {
			       			 Thread.currentThread().sleep(1000);
			       			  }
			       			  catch(InterruptedException e) {}
		        		LOGGER.log(Level.ALL,"Remove server");
		        		serverMap.remove(k);
		        	}
		      
		        }
		        try {
	       			 Thread.currentThread().sleep(1000);
	       			  }
	       			  catch(InterruptedException e) {}
		        System.out.println(" Server Deleted ");
		        displayServerDetails();
		        thread.stop();
			}
		}
		public void displayServerDetails()
		{ 
			Runnable runnable = new ServerRequests();
			Thread thread = new Thread(runnable);
			thread.start();
			try {
      			 Thread.currentThread().sleep(1000);
      			  }
      			  catch(InterruptedException e) {}
			for (Map.Entry mapElement : serverMap.entrySet()) { //int server_capacity
				System.out.println (mapElement.getKey() + " " + mapElement.getValue());
				 }
			thread.stop();
		}
		
	 
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			

		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("MY class is running");
		}

}
