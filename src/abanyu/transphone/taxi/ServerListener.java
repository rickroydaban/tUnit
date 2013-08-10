package abanyu.transphone.taxi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import connections.*;
import com.google.android.gms.maps.model.LatLng;

import android.os.Handler;
import android.os.Looper;

public class ServerListener implements Runnable{
  public static ServerSocket taxiSocket;
  private actors.MyTaxi myTaxi;
  private MyConnection conn;
  
  public ServerListener(actors.MyTaxi pMyTaxi, MyConnection pConn){
	  myTaxi=pMyTaxi;
	  conn = pConn;
  }
  
  @Override
  public void run() {
	Socket socket = null;
	ObjectInputStream input = null;
		
	try {
	  taxiSocket = new ServerSocket(conn.getTaxiPort());
	  socket = taxiSocket.accept();
	  input = new ObjectInputStream(socket.getInputStream());
		
	  //statically sets our adapter to the received requesting client information
	  actors.MyPassenger passenger = (actors.MyPassenger) input.readObject();
	  myTaxi.setClientLocation(passenger.srcLat, passenger.srcLng); //the current lattitude of the client
	  myTaxi.setDestination(passenger.desLat, passenger.desLng);
			
	  //now there will be two markers in the map since a request has been established from the server
	  TaxiMap.desCoords = new LatLng(passenger.desLat, passenger.desLng);
	  //update the status of the taxi since this taxi is going to fetch the requesting passenger
	  myTaxi.setStatus(data.TaxiStatus.requested);
			
	  //sends this taxi object to the server to notify the requesting client about this taxi information that will be fetching him/her
	  new SendServerAsyncTask(myTaxi,conn).execute();
				

	  Handler handler = new Handler(Looper.getMainLooper());
	  handler.post(new Runnable() {				
	    @Override
		public void run() {
		}
	  });
/*	  
	  ********************************************************************************************
	  ********************************************************************************************
	  ********************************************************************************************
	  ********************************************************************************************
	  ********************************************************************************************
	  */
	}catch (IOException e){
	      System.out.println("taxi listener io exception"+e.getMessage());
	}catch (ClassNotFoundException e){
	      System.out.println("taxi listener class not found exception"+e.getMessage());
	}finally{
	  //close the opened connections
	  try{
        if(taxiSocket != null)
		  taxiSocket.close();

		if(socket != null)
		  socket.close();

		if(input != null)
		  input.close();
				
	  }catch (IOException e){
		  System.out.println("taxi listener close io exception"+e.getMessage());
	  }
	}
  }
}