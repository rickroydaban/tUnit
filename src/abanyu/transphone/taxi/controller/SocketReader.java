/*
 * This class manages the objects that are sent via the server
 * 
 * the object maybe a passenger object during a successful connection,
 * but sometimes this class can manage to ask the server to resend the 
 * passenger if ever the intended object for sending was not received by
 * this manager successfully
 */

package abanyu.transphone.taxi.controller;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.android.gms.maps.model.LatLng;

import abanyu.transphone.taxi.model.MappingMVC;
import actors.MyPassenger;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import data.TaxiStatus;

public class SocketReader implements Runnable{
	MappingMVC mappingMVC;
	private ServerSocket taxiServerSocket;
	private Socket receivedConnection;
	private ObjectInputStream objectInputStream;
	private MyPassenger receivedPassengerObj;
	private Handler handler;
	
  public SocketReader(MappingMVC pMappingMVC){	
    mappingMVC = pMappingMVC;

	  try {
			taxiServerSocket = new ServerSocket(mappingMVC.getModel().getConnectionData().getTaxiPort());
	  } catch (Exception e) {
	  	System.out.println("Taxi Log: Exception at Socket Reader Constructor: "+e.getMessage());
			Toast.makeText(mappingMVC.getView(), "Exception at Socket Reader Constructor: "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
  }
	
	@Override
	public void run() { 
		while(!taxiServerSocket.isClosed()){
			try{
				receivedConnection = taxiServerSocket.accept();
				//a passenger object has been set by the server
				if(mappingMVC.getModel().getTaxi().getStatus()==TaxiStatus.vacant){ //only accepts passenger request if vacant
					objectInputStream = new ObjectInputStream(receivedConnection.getInputStream());
					Object receivedObj = objectInputStream.readObject();
					
					if(receivedObj != null){ //if an object data exist on the received object
						if(receivedObj instanceof MyPassenger){
							System.out.println("Taxi Log: Received passenger data");
							receivedPassengerObj = (MyPassenger)receivedObj;
       	
	      	  	System.out.println("Taxi Log: registering the passenger");
	      	  	mappingMVC.getModel().setPassenger(receivedPassengerObj);
	      	    mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.requested);
	      	    mappingMVC.getModel().getTaxi().setPassengerIp(receivedPassengerObj.getIp());
	      	    
	    	  	  handler = new Handler(Looper.getMainLooper());
	    	  	  handler.post(new Runnable() {				
	    	  	  	@Override
	    	  	  	public void run() {
	  	      	  	System.out.println("Taxi Log: Prompting the taxi driver");
	          	    mappingMVC.getView().getAcceptButton().setVisibility(View.VISIBLE);
	          	    mappingMVC.getView().getRejectButton().setVisibility(View.VISIBLE);
	          	    mappingMVC.getView().getDisconnectButton().setVisibility(View.GONE);
	          	    mappingMVC.getView().getVacantButton().setVisibility(View.GONE);
	          	    mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
	          	    mappingMVC.getView().getUnavailableButton().setVisibility(View.GONE);
	    	  	  	}
	    	  	  });
	      	  
	    	  	  System.out.println("Taxi Log: drawing the route for the taxi driver to decide whether accept or reject the request");
		  	  	  handler = new Handler(Looper.getMainLooper());
		  	  	  handler.post(new Runnable() {				
		  	  	  	@Override
		  	  	  	public void run() {
		  	      	  mappingMVC.getController().updateMarkers(new LatLng(mappingMVC.getModel().getTaxi().getCurLat(), 
		  	      	  																									  mappingMVC.getModel().getTaxi().getCurLng()),true);
		  	  	  	}
		  	  	  });
		  	  	  
		  	  	  System.out.println("Taxi Log: The system has been succesful on updating the markers with the server request");
						}else if(receivedObj instanceof String){ //error occured after trying to connect to the server
							if(receivedObj.equals("disconnect")){
								System.out.println("Taxi Log: The application will not update the db and exit since the server permitted it to do the action");
								mappingMVC.getController().updateData(true); //update the online db before disconnecting the app
							}else if(receivedObj.equals("resend")){      	  	
								System.out.println("Taxi Log: Request resend from passenger");
								new Thread(new SocketWriter(mappingMVC, "Server")).start(); //recends taxi data to the server
							}else if(receivedObj.equals("cancel")){ //the passenger cancelled the transaction
		    	  	  System.out.println("Taxi Log: The passenger has cancelled the request. vacating the taxi");
								handler = new Handler(Looper.getMainLooper());
		    	  	  
		    	  	  handler.post(new Runnable() {				
		    	  	  	@Override
		    	  	  	public void run() {
		          	  	mappingMVC.getController().vacateTaxi(); //reset the taxi status to vacant
		    	  	  	}
		    	  	  });
							}
						}else{
							System.out.println("Taxi Log: Object Recieved Failed on Socket Reader");
						}      
					}else{
						System.out.println("taxi Log: Received object is null");
					}
				}
			}catch (Exception e) {
				System.out.println("Taxi Log: Exception at socket reader: "+e.getMessage());
			}
		}
	}	
	
	public ServerSocket getTaxiServerSocket(){
		return taxiServerSocket;
	}
	
	public void stopAcceptingPassengers(){
    try {
    	System.out.println("Taxi Log: stopped accepting passengers");
			taxiServerSocket.close();
		} catch (Exception e) {
			System.out.println("Taxi Log: Encountered an error while closing the taxi server: "+e.getMessage());
		}      
		
	}
}
