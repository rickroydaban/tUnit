package abanyu.transphone.taxi.controller;

import java.io.ObjectOutputStream;
import java.net.Socket;

import abanyu.transphone.taxi.model.MappingMVC;
import actors.MyTaxi;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

public class SocketWriter implements Runnable{
  private Socket taxiSocket;
  private ObjectOutputStream taxiOutputStream; 
  private String recipient;
  private MappingMVC mappingMVC;
  private MyTaxi myTaxi;
  private String action;
	
  public SocketWriter(MappingMVC pMappingMvc, String pRecipient){
	  mappingMVC = pMappingMvc;
	  recipient = pRecipient;
	  myTaxi = mappingMVC.getModel().getTaxi();
  }  
  
  public SocketWriter(MappingMVC pMappingMvc, String pRecipient, String pAction){
	  mappingMVC = pMappingMvc;
	  recipient = pRecipient;
	  myTaxi = mappingMVC.getModel().getTaxi();
	  action = pAction;
  }  
    
  @Override
  public void run() {
    try{
    	if(recipient.equals("Server")){
    		System.out.println("Taxi Log: Attempting to connect to the server");
      	mappingMVC.getController().registerUnit();//register this unit to the server
    		taxiSocket = new Socket(mappingMVC.getModel().getConnectionData().getServerIp(), mappingMVC.getModel().getConnectionData().getServerPort()); //creates a new taxiSocket to connect to the server
    	}else if(recipient.equals("Passenger")){
    		System.out.println("Taxi Log: Attempting to connect to the passenger");
    		taxiSocket = new Socket(mappingMVC.getModel().getTaxi().getPassengerIP(), mappingMVC.getModel().getConnectionData().getPassengerPort()); //creates a new taxiSocket to connect to the passenger
    	}else{
    		System.out.println("Taxi Log: Error: invalid recipient!");
    	}
    	  	
    	//set this unit's connection writer data
    	taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());			
    	
    	if(action==null){
    		System.out.println("Action is null sending this taxi object to the server....");
	  	  
    		taxiOutputStream.writeObject(myTaxi); //adds a taxi instance on the taxiOutputStream stream
	    	taxiOutputStream.flush(); //needed so that the data sent will surely be sent
	  	  System.out.println("Taxi OBject was sent succesfully! send to "+recipient);
    	}else{
    		System.out.println("Action is NOT null sending this action "+action+"to the server....");

	    	taxiOutputStream.writeObject(action);
	    	taxiOutputStream.flush(); //needed so that the data sent will surely be sent
	  	  System.out.println("Reject Reqeust was sent succesfully! send to "+recipient);
    	}
    	
  		taxiOutputStream.close();
  		taxiSocket.close();
    } catch (Exception e){
    	System.out.println("Encountered an exception on SocketWriter!");
    	e.printStackTrace();
    } 
  }
}