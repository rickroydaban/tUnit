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

import connections.MyConnection;
import data.TaxiStatus;

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

public class GetServerIP extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //Constructor Fetcher Variables	
  private TaxiMap taxiMapActivity; //the activity context
  private String url; //the url passed
  private MyConnection conn;
  private MyTaxi myTaxi;
    
  //asynctask variables
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  InputStream input = null;
  JSONObject jObj = null;
  String json = "";
	
  //CONSTRUCTOR	
  public GetServerIP(TaxiMap pActivityContext, String pStringUrl, MyConnection pConn, MyTaxi pMyTaxi){
    taxiMapActivity = pActivityContext;
	url = pStringUrl;
	conn = pConn;
	myTaxi = pMyTaxi;
  }
		
  protected void onPreExecute() {
	super.onPreExecute();
	progressDialog = new ProgressDialog(taxiMapActivity);
	progressDialog.setMessage("Retrieving Server IP, Please wait...");
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
		System.out.println(e.getMessage());
	} catch (ClientProtocolException e) {
		System.out.println(e.getMessage());
    } catch (IOException e) {
		System.out.println(e.getMessage());
    } 	
	System.out.println("server ip: " + result);
    
	return result;		
  }
		
  protected void onPostExecute(String result) {
	super.onPostExecute(result);
	progressDialog.hide();
	conn.setServerIp(result);
	
    new GetTaxiData(taxiMapActivity, "http://transphone.freetzi.com/thesis/dbmanager.php?fname=getTaxiData&arg1="+LoginTaxiActivity.plateNo, myTaxi, conn).execute();
	myTaxi.setStatus(TaxiStatus.vacant);
	myTaxi.setPlateNumber(LoginTaxiActivity.plateNo);
	System.out.println("got taxi data");
  }	
}
