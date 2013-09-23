/*
 * TaxiMap.java
 * author: Rick Royd L. Aban
 *         Stanley P. Yu
 * 
 * This class will display a google map with the overlay of the current location of this taxi
 * 
 * PSEUDOCODE:
 *   1. initialize all necessary initializations
 *   2. show the google map
 *   3. retrieve this taxi's location and show it to the map
 *   4. wait for incoming requests
 *         1. if a request has been established, automatically set this taxi's status "requested"
 *         2. get the current location of the requesting client
 *         3. if the driver has arrived to the client's location and the client has just ride into taxi,
 *            manually set this taxi's status "occupied"
 *         4. wait until the passenger has been delivered successfully
 *         5. manually set this taxi's status "vacant"
 *         
 *  * if ever the driver is taking his breakfast, lunch or dinner or other personal neccessities,
 *    the taxi can be statused as "unavailable". This will still be recorded on the tract
 */

package abanyu.transphone.taxi.view;

import abanyu.transphone.taxi.controller.TaxiMapController;
import abanyu.transphone.taxi.model.MappingData;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;

import com.example.taxi.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

public class MappingView extends FragmentActivity{
	private Button vacantButton, occupiedButton, unavailableButton, disconnectButton, acceptButton, rejectButton;

  //MAP CONTENT VARIABLES
  private Polyline route; //static because it is being manipulated by DrawPathAsyncTask thread
  private GoogleMap map; //static also because it is also being manipulated by the DrawPathAsyncTask thread
  private MarkerOptions srcMO; //global because it is dependent on the returned location estimation
  private MarkerOptions desMO; //global because it is dependent on the returned passenger object by the server

	@Override
  protected void onCreate(Bundle savedInstanceState){
  	super.onCreate(savedInstanceState);
  	setContentView(R.layout.taxi_map);
      	
  	vacantButton = (Button) findViewById(R.id.vacantButton);
  	occupiedButton = (Button) findViewById(R.id.occupyButton);
  	unavailableButton = (Button) findViewById(R.id.unavailableButton);
  	disconnectButton = (Button) findViewById(R.id.disconnectButton);
  	acceptButton = (Button) findViewById(R.id.acceptButton);
  	rejectButton = (Button) findViewById(R.id.rejectButton);
  	map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    srcMO = new MarkerOptions().position(new LatLng(0,0)).title("Current Location");
    desMO = new MarkerOptions().position(new LatLng(0,0)).title("Destination");    
      
  	new TaxiMapController(new MappingData(), this).start();  	
  }
			
	//SETTERS
	public Polyline getRoute(){
		return route;
	}
	
	public void setRoute(Polyline line){
		route = line;
	}

	//GETTERS
	public MarkerOptions getSrcMO(){
		return srcMO;
	}
	
	public MarkerOptions getDesMO(){
		return desMO;
	}
	
	public GoogleMap getMap(){
		return map;
	}
	
	public Button getVacantButton(){
		return vacantButton;
	}
	
	public Button getOccupyButton(){
		return occupiedButton;
	}
	
	public Button getUnavailableButton(){
		return unavailableButton;
	}
	
	public Button getDisconnectButton(){
		return disconnectButton;
	}	
	
	public Button getAcceptButton(){
		return acceptButton;
	}
	
	public Button getRejectButton(){
		return rejectButton;
	}
}


