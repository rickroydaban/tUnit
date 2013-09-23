package abanyu.transphone.taxi.model;

import actors.MyPassenger;
import actors.MyTaxi;
import connections.MyConnection;
import data.TaxiStatus;

public class MappingData {
	private MyTaxi myTaxi;
	private MyConnection myConnection;
	private MyPassenger myPassenger;
	
  
	public MappingData(){
		myTaxi = new MyTaxi();
		myTaxi.setCurrentLocation(0, 0);
		myConnection = new MyConnection();	
	}
	
	//SETTERS
	public void setPassenger(MyPassenger pPassenger){
		myPassenger = pPassenger;
	}
		
	public void setServerIP(String serverip){
		myConnection.setServerIp(serverip);
	}
	
	public void setTaxiPlateNo(String plateNo){
		myTaxi.setPlateNumber(plateNo);
	}
	
	public void setTaxiStatus(TaxiStatus status){
		myTaxi.setStatus(status);
	}
	
		
	//GETTERS
	public String getServerIP(){
		return myConnection.getServerIp();
	}
	
	public String getTaxiPlateNo(){
		return myTaxi.getPlateNumber();
	}
	
	public MyConnection getConnectionData(){
		return myConnection;
	}
		
	public MyTaxi getTaxi(){
		return myTaxi;
	}
	
	public MyPassenger getPassenger(){
		return myPassenger;
	}	
}
