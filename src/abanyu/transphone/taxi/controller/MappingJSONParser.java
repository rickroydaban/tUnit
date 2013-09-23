package abanyu.transphone.taxi.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.maps.model.LatLng;

import abanyu.transphone.taxi.model.MappingMVC;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import data.TaxiStatus;

public class MappingJSONParser implements Runnable{	
	private String url;	
	private MappingMVC mappingMVC;
	private ProgressDialog progressDialog;
	private TaxiStatus status;
	private boolean isFromClick;
	private Handler handler;
	
	public MappingJSONParser(String pUrl, MappingMVC pMappingMVC, boolean pIsFromClick) {
		url = pUrl;
		isFromClick = pIsFromClick;
		mappingMVC = pMappingMVC;
		status = mappingMVC.getModel().getTaxi().getStatus();
		progressDialog = new ProgressDialog(mappingMVC.getView());
		progressDialog.setIndeterminate(true);
	}
	
	@Override
	public void run() {
		try{
			
			if(isFromClick){
	  	  handler = new Handler(Looper.getMainLooper());
	  	  handler.post(new Runnable() {				
	  	  	@Override
	  	  	public void run() {
	  	  		progressDialog.setMessage("Please wait while updating the database status...");
	  	  		progressDialog.show();
	  	  	}
	  	  });
			}
			
  	DefaultHttpClient client = new DefaultHttpClient();
	  HttpGet get = new HttpGet(url);
	  String result;
			
	  HttpResponse response = client.execute(get);
	  result = EntityUtils.toString(response.getEntity());  
    if(result.equals("1")){
    	if(isFromClick){
    		System.out.println("Taxi Log: A button is clicked and requires to be synchronous to the db with their task");
    		if(status.equals(TaxiStatus.disconnected)){
    			System.out.println("Taxi Log: Doing on unavailable things....");
    		  handler = new Handler(Looper.getMainLooper());
      	  handler.post(new Runnable() {				
      	  	@Override
      	  	public void run() {
        			mappingMVC.getView().finish();
        			Process.killProcess(Process.myPid());				      	  	      		
      	  	}
      	  });
    		}else if(status.equals(TaxiStatus.unavailable)){
    			System.out.println("Taxi Log: Doing on unavailable things....");
    		}else if(status.equals(TaxiStatus.occupied)){
    			System.out.println("Taxi Log: Doing on occupied things....");    		
    		}else if(status.equals(TaxiStatus.requested)){
    			System.out.println("Taxi Log: Doing on requested things....");    		
    		}else if(status.equals(TaxiStatus.vacant)){
    			System.out.println("Taxi Log: Doing on vacant things....");    		
    		}
    	}
    	
  	  handler = new Handler(Looper.getMainLooper());
  	  handler.post(new Runnable() {				
  	  	@Override
  	  	public void run() {
  	    	progressDialog.hide();
  	  	}
  	  });
    	System.out.println("Taxi Log: Succesffully updated the track in the database");
      }else
      	System.out.println("Taxi Log: Error while updating the track in the database");
		} catch (Exception e) {
    	System.out.println("Taxi Log: Exception at mapping json parser: "+e.getMessage());
    } 	
	}
}
