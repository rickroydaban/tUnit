package abanyu.transphone.taxi.controller;

import java.io.ObjectOutputStream;
import java.net.Socket;

import abanyu.transphone.taxi.model.MappingMVC;
import actors.MyTaxi;

public class SocketWriter implements Runnable{
  private Socket taxiSocket;
  private ObjectOutputStream taxiOutputStream; 
  private String recipient;
  private MappingMVC mappingMVC;
  private MyTaxi myTaxi;
	
  public SocketWriter(MappingMVC pMappingMvc, String pRecipient){
	  mappingMVC = pMappingMvc;
	  recipient = pRecipient;
	  myTaxi = mappingMVC.getModel().getTaxi();
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
    	taxiOutputStream.writeObject(myTaxi); //adds a taxi instance on the taxiOutputStream stream
    	taxiOutputStream.flush(); //needed so that the data sent will surely be sent
    	System.out.println("Taxi Object Successfully sent!");
  		taxiOutputStream.close();
  		taxiSocket.close();
    } catch (Exception e){
    	System.out.println("Encountered an exception on SocketWriter: "+e.getMessage());
    } 
  }
}