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

package abanyu.transphone.taxi;

import java.io.IOException;

import com.example.taxi.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import abanyu.transphone.taxi.login.LoginTaxiActivity;
import actors.*;
import data.*;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class TaxiMap extends FragmentActivity implements LocationListener{
  // needs to extend fragment activity since fragments are needed in order to display map contents

	  
  /*******************************************************************/
  /******************** * VARIABLE DECLARATIONS * ********************/
  /*******************************************************************/

  //MAP CONTENT VARIABLES
  public static Polyline line; //static because it is being manipulated by DrawPathAsyncTask thread
  public static GoogleMap map; //static also because it is also being manipulated by the DrawPathAsyncTask thread
  private MarkerOptions srcMarkerOptions; //global because it is dependent on the returned location estimation
  private MarkerOptions desMarkerOptions; //global because it is dependent on the returned passenger object by the server
	  
  //LOCATION DATA VARIABLES
  private LocationManager locationManager; 
  private LatLng taxiCoords; //global only not static since its values is only manipulated through onLocationChange
  private String locationProvider; //GPS or Wifi
  private int locationUpdateByTime = 600000; //location will be updated every 10 minutes
  private int locationUpdateByDist = 10; //location will be updated every 10 meters
  
  //UI elements variables
  private Button vacantButton, occupied, disconnect;
	
//  public static Context context;  //EXPERIMENTAL ONLY. try asynctask instead of threads
  public static LatLng desCoords; //have to be static since it will be change by our server listener thread
  public static actors.MyPassenger myPassenger;
  public static actors.MyTaxi myTaxi;
  
  @Override
  protected void onCreate(Bundle savedInstanceState){
	requestWindowFeature(Window.FEATURE_NO_TITLE); //removes title bar
	super.onCreate(savedInstanceState);
	setContentView(R.layout.taxi_map);
//	context = this; //EXPERIMENTAL ONLY. try asynctask instead of threads
			
    //creates a new MyTaxi object
    myTaxi = new MyTaxi();
    
    new GetTaxiData(this, "http://transphone.freetzi.com/thesis/dbmanager.php?fname=getTaxiData&arg1="+LoginTaxiActivity.plateNo).execute();
    
	//the taxi must have an initial status of a vacant status
	myTaxi.setStatus(TaxiStatus.vacant);
	myTaxi.setPlateNumber(LoginTaxiActivity.plateNo);
	//defines the system behavior when the vacant button is clicked
	vacantButton = (Button) findViewById(R.id.vacantButton);
	vacantButton.setOnClickListener(new OnClickListener(){	
	  @Override
      public void onClick(View v){
	    myTaxi.setStatus(data.TaxiStatus.vacant);

		new SendServerAsyncTask(myTaxi).execute();
		new Thread(new ServerListener(myTaxi)).start();
      }
	});

	//defines the system behavior when the occupy button is clicked
	occupied = (Button) findViewById(R.id.occupyButton);
	occupied.setOnClickListener(new OnClickListener(){	
	  @Override
	  public void onClick(View v){
	    myTaxi.setStatus(data.TaxiStatus.occupied);
	    //the destination will temporarily be the location of the requesting passenger
  	    desCoords = new LatLng(myTaxi.getClientLat(),myTaxi.getClientLng()); 
	  }
	});
	  
	//defines the system behavior when the occupy button is clicked
	disconnect = (Button) findViewById(R.id.unavailableButton);
	disconnect.setOnClickListener(new OnClickListener(){	
	  @Override
	  public void onClick(View v){
        try{
          if(ServerListener.taxiSocket != null)
		    ServerListener.taxiSocket.close();
		}catch (IOException e){
          e.printStackTrace();
        }
          
	   	TaxiMap.this.finish();
      }
	});
		
	/**********************************************************************************************************/
	/************************************* UI DEFINITION ENDS HERE ********************************************/
	/**********************************************************************************************************/
	
    //get a Google Map
    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    
    if (map != null){
      //create a new criteria for getting a location provider of preference when a map is available
      Criteria criteria = new Criteria();
      criteria.setAccuracy(Criteria.ACCURACY_FINE);
      criteria.setPowerRequirement(Criteria.POWER_LOW);
      
	  locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);       
	  locationProvider = locationManager.getBestProvider(criteria, true);
	  //NOTE: on obstructed places, GPS may took longer times to have a position fix compared to wireless network
    
	  //initialize marker options to dummy values, our main purposes is to just instantiate them before requesting location updates
	  srcMarkerOptions = new MarkerOptions().position(new LatLng(0,0))
                                            .title("Source");
      desMarkerOptions = new MarkerOptions().position(new LatLng(0,0)) 
                                            .title("Destination");    

	  //manages the drawables to be shown in the google map
      locationManager.requestLocationUpdates(locationProvider, locationUpdateByTime, locationUpdateByDist, this); 
      new SendServerAsyncTask(myTaxi).execute();
	  new Thread(new ServerListener(myTaxi)).start();
    }
  }
	
  @Override
  public void onLocationChanged(Location location) {	  
	taxiCoords = new LatLng(location.getLatitude(), location.getLongitude());
	//update the coordinate data of the taxi object
	myTaxi.setCurrentLocation(taxiCoords.latitude, taxiCoords.longitude);

	//only the source marker will only be displayed until a server request has been established
	if(desCoords==null){
      srcMarkerOptions.position(taxiCoords);
	  map.addMarker(srcMarkerOptions);
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(taxiCoords, 16), 2000, null); 
	}else{
	  map.clear();
	    
	  //updates the coordinates of the source location and the destination location
	  srcMarkerOptions.position(taxiCoords); //the source coords will only be defined inside this function
	  desMarkerOptions.position(desCoords); //the des coords will be defined outside this function since it is dependent
	                                          //on outside processes like when receiving new requests or clicking the occupy button

	  //add the markers to the map
      map.addMarker(srcMarkerOptions);
      map.addMarker(desMarkerOptions);	
        
      //finally draw a route that will connect the two markers
	  String url = makeJsonCompatibleUrlStr(taxiCoords.latitude, taxiCoords.longitude, 
	    		                            desCoords.latitude, desCoords.longitude);
	  new DrawPathAsyncTask(TaxiMap.this, url).execute();
	}
      
	
	new SendServerAsyncTask(myTaxi).execute();
  }

  @Override
  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onProviderEnabled(String provider) {
	Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
		
  }
		
  private static String makeJsonCompatibleUrlStr(double srclatt, double srclong, double destlatt, double destlong) {
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
}
