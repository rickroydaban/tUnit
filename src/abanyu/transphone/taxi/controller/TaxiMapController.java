package abanyu.transphone.taxi.controller;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import abanyu.transphone.taxi.model.MappingData;
import abanyu.transphone.taxi.model.MappingMVC;
import abanyu.transphone.taxi.view.MappingView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.taxi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import connections.MyConnection;

import data.TaxiStatus;

public class TaxiMapController implements LocationListener, OnClickListener{
	private MappingMVC mappingMVC; //contains instances of model,view and controller for mapping process
  private LocationManager locationManager; 	
  private boolean isRegistered = false;
  private boolean showRouteToPassenger =  false;
  private int updateCount = -1;
  private MyConnection conn;

	public TaxiMapController(MappingData pMappingData, MappingView pTaxiMap){
		mappingMVC = new MappingMVC(pMappingData,pTaxiMap,this);
  	conn = mappingMVC.getModel().getConnectionData();
		mappingMVC.getModel().getTaxi().setIp(getIPAddress());
		
		//add click listeners to interact with the buttons
		mappingMVC.getView().getVacantButton().setOnClickListener(this);
		mappingMVC.getView().getOccupyButton().setOnClickListener(this);
		mappingMVC.getView().getUnavailableButton().setOnClickListener(this);
		mappingMVC.getView().getDisconnectButton().setOnClickListener(this);
		mappingMVC.getView().getAcceptButton().setOnClickListener(this);
		mappingMVC.getView().getRejectButton().setOnClickListener(this);		
		mappingMVC.getView().getAcceptButton().setVisibility(View.GONE);
		mappingMVC.getView().getRejectButton().setVisibility(View.GONE);			
	  mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
	  mappingMVC.getView().getUnavailableButton().setVisibility(View.GONE);
		mappingMVC.getModel().setTaxiStatus(TaxiStatus.unavailable);	//unit will initially be available only if its location has been set					
		mappingMVC.getView().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	//keep screen on
		System.out.println("Taxi Log: =");
		System.out.println("Taxi Log: TaxiMapController Constructor has just finished");
		System.out.println("Taxi Log: =");
	}
	
	public void start(){
		//get all usable data of the selected company and taxi
		System.out.println("Taxi Log: Setting Taxi Data from previous intent data");	
		mappingMVC.getModel().setServerIP(mappingMVC.getView().getIntent().getStringExtra("serverip"));
		mappingMVC.getModel().setTaxiPlateNo(mappingMVC.getView().getIntent().getStringExtra("plateNo"));		
		mappingMVC.getModel().getTaxi().setBodyNumber(mappingMVC.getView().getIntent().getStringExtra("bodyNo"));
		mappingMVC.getModel().getTaxi().setDescription(mappingMVC.getView().getIntent().getStringExtra("desc"));
		mappingMVC.getModel().getTaxi().setCompanyNumber(mappingMVC.getView().getIntent().getStringExtra("compContact"));
		mappingMVC.getModel().getTaxi().setCompanyName(mappingMVC.getView().getIntent().getStringExtra("compName"));
		mappingMVC.getModel().getTaxi().setDriverName(mappingMVC.getView().getIntent().getStringExtra("drivName"));
		
		if(mappingMVC.getView().getMap()!=null){
			System.out.println("Taxi Log: Request Location Update");
			locationManager = (LocationManager)mappingMVC.getView().getSystemService(MappingView.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			System.out.println("Updating data on the initial process..");
			updateData(false); //updatee the data on the initial process
		}  		
		
		System.out.println("Taxi Log: Creating a new Socket Reader");
		new Thread(new SocketReader(mappingMVC)).start(); //start receiving server requests
	}
	
	public void registerUnit(){
		System.out.println("Taxi Log: Registering Taxi");
		isRegistered = true;
	}
	
	public void unregisterUnit(){
		System.out.println("Taxi Log: UnRegistering Taxi");
		isRegistered = false;
	}
		
	public void updateData(boolean willExit){
		System.out.println("Taxi Log: Updating Data");
		if(mappingMVC.getModel().getTaxi()!=null){
			//check if the retrieved location data is within the parameter of cebu
			if((mappingMVC.getModel().getTaxi().getCurLat()>9.40 && mappingMVC.getModel().getTaxi().getCurLng()>123.27)&&
				 (mappingMVC.getModel().getTaxi().getCurLat()<11.3 && mappingMVC.getModel().getTaxi().getCurLng()<124.20)){
					String trackUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=addTrack"+
						"&arg1="+mappingMVC.getModel().getTaxi().getPlateNumber()+
						"&arg2="+mappingMVC.getModel().getTaxi().getCurLat()+
						"&arg3="+mappingMVC.getModel().getTaxi().getCurLng()+
						"&arg4="+mappingMVC.getModel().getTaxi().getStatus();
  	
					System.out.println("Taxi Log: url for adding rows in the track table is set: "+ trackUrl);
					if(!willExit){
						updateMarkers(new LatLng(mappingMVC.getModel().getTaxi().getCurLat(),mappingMVC.getModel().getTaxi().getCurLng()),true);
						new Thread(new SocketWriter(mappingMVC, "Server")).start();//send updates to server
					}
					
					new Thread(new MappingJSONParser(trackUrl,mappingMVC,true)).start();//send updates to database
					//update the map views
			}else{
				System.out.println("Taxi Log: Sorry but the retrieved location is out bounds from Cebu");
			}
		}else{
			System.out.println("Taxi Log: Taxi Data has to be created in order to update its data");
		}
	}
	
	public void updateDBDataByCounter(){
		System.out.println("Taxi Log: Updating Data With Limit");
		if(mappingMVC.getModel().getTaxi()!=null){
			//check if the retrieved location data is within the parameter of cebu
			if((mappingMVC.getModel().getTaxi().getCurLat()>9.40 && mappingMVC.getModel().getTaxi().getCurLng()>123.27)&&
				 (mappingMVC.getModel().getTaxi().getCurLat()<11.3 && mappingMVC.getModel().getTaxi().getCurLng()<124.20)){
					String trackUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=addTrack"+
						"&arg1="+mappingMVC.getModel().getTaxi().getPlateNumber()+
						"&arg2="+mappingMVC.getModel().getTaxi().getCurLat()+
						"&arg3="+mappingMVC.getModel().getTaxi().getCurLng()+
						"&arg4="+mappingMVC.getModel().getTaxi().getStatus();
  	
					System.out.println("url for adding rows in the track table is set: "+ trackUrl);
					//second parameter determines wether a progress dialog is to be shown while redrawing the routes
					updateMarkers(new LatLng(mappingMVC.getModel().getTaxi().getCurLat(),mappingMVC.getModel().getTaxi().getCurLng()),false);
			    
					System.out.println("Taxi Log: updateCount: "+updateCount%10);
					new Thread(new SocketWriter(mappingMVC, "Server")).start();//send updates to server
					if(updateCount%10==0){
						System.out.println("Taxi Log: Update count max Reached! Updating DB...");
						new Thread(new MappingJSONParser(trackUrl,mappingMVC,false)).start();//send updates to database						
					}
					updateCount++;
			}else{
				System.out.println("Taxi Log: Sorry but the retrieved location is out bounds from Cebu");
			}
		}else{
			System.out.println("Taxi Log: Taxi Data has to be created in order to update its data");
		}		
	}
	
	@Override
	public void onLocationChanged(Location location) { //defines a set of instructions every location change update
		if(mappingMVC.getModel().getPassenger()==null)
			System.out.println("Updating Location No Passenger is in this taxi");
//			Toast.makeText(mappingMVC.getView(), "Updating Location No Passenger is in this taxi", Toast.LENGTH_LONG).show();
		else
			System.out.println("Updating Location 1 Passenger is in this taxi");
//			Toast.makeText(mappingMVC.getView(), "Updating Location 1 Passenger is in this taxi", Toast.LENGTH_LONG).show();

		LatLng currentCoordinates = new LatLng(location.getLatitude(), location.getLongitude());

		mappingMVC.getModel().getTaxi().setCurrentLocation(currentCoordinates.latitude, currentCoordinates.longitude);  
		updateDBDataByCounter();
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}
	
	public void updateMarkers(LatLng currentCoordinates, boolean showDialog) {
		System.out.println("Taxi Log: Reset map overlays");
		mappingMVC.getView().getMap().clear(); //removes all map overlays on every location update
		mappingMVC.getView().getSrcMO().position(currentCoordinates); //reset the position of the source marker in the map on every location update
		mappingMVC.getView().getMap().addMarker(mappingMVC.getView().getSrcMO()); //reattaches the source marker in the map

		if(isRegistered){ //blocks any pending server request if ever this taxi has not been set
			System.out.println("Taxi Log: updating markers where taxi is already registered");
			if(mappingMVC.getModel().getPassenger()!=null){ //the map will have a target location since 
				System.out.println("Taxi Log: updating markers where taxi also has passenger request");
				LatLng targetCoordinates; //contains the data of the requesting client

				if(showRouteToPassenger){
					System.out.println("Taxi Log: Showing the route to the passenger");
//					Toast.makeText(mappingMVC.getView(), "target coordinates: current location", Toast.LENGTH_LONG).show();
					targetCoordinates = new LatLng(mappingMVC.getModel().getPassenger().getCurLat(), mappingMVC.getModel().getPassenger().getCurLng());  
				}else{ //shows the route to arrive at the client's desired destination
//					Toast.makeText(mappingMVC.getView(), "target coordinates: destination location", Toast.LENGTH_LONG).show();
					System.out.println("Taxi Log: Showing the route to the destination of the passenger");
					targetCoordinates = new LatLng(mappingMVC.getModel().getPassenger().getDesLat(), mappingMVC.getModel().getPassenger().getDesLng());
				}
				
				mappingMVC.getView().getDesMO().position(targetCoordinates); //resets the position of the destination marker in the map on every location update
				mappingMVC.getView().getMap().addMarker(mappingMVC.getView().getDesMO());	//reattaches the destination marker in the map
			
				//resets the route data
				String url = makeJsonCompatibleUrlStr(currentCoordinates.latitude, currentCoordinates.longitude, 
				  				 														targetCoordinates.latitude, targetCoordinates.longitude);

				System.out.println("Taxi Log: Drawing the routes");
				//draws the route on the map depending on the specified source and destination locations
				new Thread(new RouteDrawer(mappingMVC, url, showDialog)).start();
				System.out.println("Taxi Log: Route has been redrawn successfully");
			}
		}else{
			System.out.println("Taxi Log: Warning. updating markers where taxi is not yet registered");
		  mappingMVC.getView().getUnavailableButton().setVisibility(View.GONE);
		  mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
		  mappingMVC.getView().getVacantButton().setVisibility(View.VISIBLE);
			mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.unavailable);
		}

		//animates the camera to the update location
    mappingMVC.getView().getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(currentCoordinates, 16), 1000, null);				
	}

	private String makeJsonCompatibleUrlStr(double srclatt, double srclong, double destlatt, double destlong) {
		StringBuilder url = new StringBuilder();
		url.append("http://maps.googleapis.com/maps/api/directions/json");
		url.append("?origin=");
		url.append(Double.toString(srclatt));
		url.append(",");
		url.append(Double.toString(srclong));
		url.append("&destination=");
		url.append(Double.toString(destlatt));
		url.append(",");
		url.append(Double.toString(destlong));
		url.append("&sensor=false&mode=driving&alternatives=true");
		return url.toString();
    }
	
  private String getIPAddress() {
  	try {
  		List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
          
  		for (NetworkInterface intf : interfaces) {
  			List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
          
  			for (InetAddress addr : addrs) {
  				if (!addr.isLoopbackAddress()) {
  					String sAddr = addr.getHostAddress().toUpperCase(Locale.ENGLISH);
  					boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                      
  					if (isIPv4) 
  						return sAddr;
  				}
  			}
  		}
  	} catch (Exception e) { 
    	System.out.println("IO exception on socketwriter, getIPAddress(): "+e.getMessage());
  	} 

  	return null;
  }
	
	
  public void vacateTaxi(){
	  mappingMVC.getView().getVacantButton().setVisibility(View.GONE); //vacant button is not clickable anymore since the unit`s status is already vacant
	  mappingMVC.getView().getUnavailableButton().setVisibility(View.VISIBLE); //vacant button is not clickable anymore since the unit`s status is already vacant
  	mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
	  mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.vacant);
	  mappingMVC.getModel().setPassenger(null);
	  
	  updateData(false);//update data and will not exit the app
  }
  
  public void makeUnavailable(){
	  mappingMVC.getView().getUnavailableButton().setVisibility(View.GONE);
	  mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
	  mappingMVC.getView().getVacantButton().setVisibility(View.VISIBLE);
		mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.unavailable);
  	mappingMVC.getModel().setPassenger(null);
		
		updateData(false);//update data and will not exit the app
  }
	
	@Override
	public void onClick(View v) {
		updateCount++;
		switch(v.getId()){
			case R.id.vacantButton:
				System.out.println("Taxi Log: Vacant Button Pressed. Vacate Taxi!");
		  	vacateTaxi();		  		
		  	break;
				
			case R.id.occupyButton:
				System.out.println("Taxi Log: Occupy Button Pressed. Change Route!");
				showRouteToPassenger = false;
		  	mappingMVC.getView().getOccupyButton().setVisibility(View.GONE);
		  	mappingMVC.getView().getVacantButton().setVisibility(View.VISIBLE);
		  	mappingMVC.getView().getDisconnectButton().setVisibility(View.VISIBLE);
		  	mappingMVC.getView().getUnavailableButton().setVisibility(View.VISIBLE);
			  mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.occupied);
			  
			  updateData(false);//update data and will not exit the app
				break;
				
			case R.id.unavailableButton:
				System.out.println("Taxi Log: Unavailable Button Pressed. Make Unavailable!");
				makeUnavailable();
				break;
				
			case R.id.disconnectButton:
				System.out.println("Taxi Log: Disconnect Button Pressed. Ask for permission from the server to exit!");
		  	mappingMVC.getModel().getTaxi().setStatus(TaxiStatus.disconnected);
		  	updateData(false);//update data and will not exit the app still have to wait for server reply to exit
		  	break;

			case R.id.acceptButton:
				System.out.println("Taxi Log: Accept Button Pressed. Change Route!");
				showRouteToPassenger = true;
		  	mappingMVC.getView().getAcceptButton().setVisibility(View.GONE);
		  	mappingMVC.getView().getRejectButton().setVisibility(View.GONE);
		  	mappingMVC.getView().getOccupyButton().setVisibility(View.VISIBLE);
		  	mappingMVC.getView().getUnavailableButton().setVisibility(View.VISIBLE);
		  	mappingMVC.getView().getDisconnectButton().setVisibility(View.VISIBLE);
		  	
		  	updateMarkers(new LatLng(mappingMVC.getModel().getTaxi().getCurLat(), mappingMVC.getModel().getTaxi().getCurLng()),true);
		  	break;
				
			case R.id.rejectButton:
				System.out.println("Taxi Log: Reject Button Pressed. Make Unavailable!");
		  	mappingMVC.getView().getAcceptButton().setVisibility(View.GONE);
		  	mappingMVC.getView().getRejectButton().setVisibility(View.GONE);
		  	mappingMVC.getView().getDisconnectButton().setVisibility(View.VISIBLE);
		  	makeUnavailable();
		  	break;
		}
	}
}
