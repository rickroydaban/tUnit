package abanyu.transphone.taxi.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abanyu.transphone.taxi.model.MappingMVC;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteDrawer implements Runnable{

  private String url, mJsonizedStringUrl;
  private MappingMVC mappingMVC;
	private ProgressDialog progressDialog;	
	private boolean willShow;
  
  public RouteDrawer(MappingMVC pMappingMVC, String stringUrl, boolean show){
  	willShow = show;
    mappingMVC = pMappingMVC;
  	url = stringUrl;
  	progressDialog = new ProgressDialog(mappingMVC.getView());
  	
  	if(willShow){
  		progressDialog.show();
  		progressDialog.setMessage("Please wait while drawing your route..");
  	}
  }
  
  @Override
  public void run() {
	  SimpleJSONParser jParser = new SimpleJSONParser();
	  mJsonizedStringUrl = jParser.getJSONfromURL(url);
	  
	  Handler handler = new Handler(Looper.getMainLooper());
	  handler.post(new Runnable() {				
	    @Override
	    public void run() {
	      if(mJsonizedStringUrl != null){
	        try{
	      	  final JSONObject json = new JSONObject(mJsonizedStringUrl); //json object
	      	  JSONArray routeArray = json.getJSONArray("routes");
	      	  JSONObject routes = routeArray.getJSONObject(0);
	      	  JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	      	  String encodedString = overviewPolylines.getString("points");
	      	  List<LatLng> list = decodePoly(encodedString);
	      				
	      	  for(int i = 0; i < list.size()-1; i++){ 
	      	  	//-1 because it is using a look ahead manipulation method to prevent array out of bounds  
	      	    LatLng src = list.get(i);
	      	    LatLng dest = list.get(i+1);
	      	    mappingMVC.getView().setRoute( mappingMVC.getView().getMap().addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude),
	      					                           new LatLng(dest.latitude, dest.longitude)).width(2).color(Color.BLUE).geodesic(true))); 
	      	  }		
	      	}catch (JSONException e){
	      		System.out.println("Json error on RouteDrawer, run method: "+e.getMessage());
	      	}
	      }
	    }
	  });
  }
  
  private List<LatLng> decodePoly(String encodedStr){	
    List<LatLng> poly = new ArrayList<LatLng>();
	int index = 0, len = encodedStr.length();
	int lat = 0, lng = 0;
			
	while (index < len) {
	  int b, shift = 0, result = 0;
			    
	  do{
	    b = encodedStr.charAt(index++) - 63;
		result |= (b & 0x1f) << shift;
		shift += 5;
	  }while (b >= 0x20);
		  
	  int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	  lat += dlat;
			
	  shift = 0;
	  result = 0;
			    
	  do{
	    b = encodedStr.charAt(index++) - 63;
	    result |= (b & 0x1f) << shift;
		shift += 5;
	  }while (b >= 0x20);
			    
	  int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	  lng += dlng;
			
	  LatLng p = new LatLng( (((double) lat / 1E5)), (((double) lng / 1E5) ));
	  poly.add(p);
	}
	
	if(willShow)
		progressDialog.dismiss();
	
	return poly;
  }
}