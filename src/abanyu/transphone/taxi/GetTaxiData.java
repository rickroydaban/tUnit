package abanyu.transphone.taxi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.taxi.R;

import abanyu.transphone.taxi.login.GetDatabaseActivity;
import abanyu.transphone.taxi.login.LoginTaxiActivity;
import abanyu.transphone.taxi.login.RegisterDriverTask;
import actors.MyTaxi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GetTaxiData extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //Constructor Fetcher Variables	
  private TaxiMap taxiMapActivity; //the activity context
  private String url; //the url passed
    
  //asynctask variables
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  InputStream input = null;
  JSONObject jObj = null;
  String json = "";
	
  //CONSTRUCTOR	
  public GetTaxiData(TaxiMap pActivityContext, String pStringUrl){
    taxiMapActivity = pActivityContext;
	url = pStringUrl;
  }
		
  protected void onPreExecute() {
	super.onPreExecute();
	progressDialog = new ProgressDialog(taxiMapActivity);
	progressDialog.setMessage("Retrieving Taxi Information, Please wait...");
	progressDialog.setIndeterminate(true);
	progressDialog.show();
  }
		
  @Override
  protected String doInBackground(Void... arg0) {
    String result=null;

    //retrieve the json as returned by the url
    try{
	  DefaultHttpClient client = new DefaultHttpClient();
	  HttpGet get = new HttpGet(url);
			
	  HttpResponse response = client.execute(get);
      result = EntityUtils.toString(response.getEntity());  
	}catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } 	
    
	return result;		
  }
		
  protected void onPostExecute(String result) {
	super.onPostExecute(result);
	progressDialog.hide();

	try {
		JSONObject jo = new JSONObject(result);

		// RETRIEVE EACH JSON OBJECT'S FIELDS
		TaxiMap.myTaxi.setCompanyName(jo.getString("compName"));
		TaxiMap.myTaxi.setCompanyNumber(jo.getString("compNo"));
		TaxiMap.myTaxi.setDriverName(jo.getString("driverName"));        
        TaxiMap.myTaxi.setBodyNumber(jo.getString("bodyNo"));
        TaxiMap.myTaxi.setDescription(jo.getString("desc"));
        
		System.out.println("plateNo: "+TaxiMap.myTaxi.getPlateNumber());
		System.out.println("compName: "+TaxiMap.myTaxi.getCompanyName());
		System.out.println("compNo: "+TaxiMap.myTaxi.getCompanyNumber());
		System.out.println("driverName: "+TaxiMap.myTaxi.getDriverName());
		System.out.println("desc: "+TaxiMap.myTaxi.getDescription());
		
        
//      FYI get license is available for retrievals here;
        
	}catch (JSONException e) {
      e.printStackTrace();
    }
  }	
}
