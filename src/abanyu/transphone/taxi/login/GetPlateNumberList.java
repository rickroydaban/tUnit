/*
 * This is a Thread class used by the PlateNumberActivity to query the specified url
 * and retrieve a list of plate numbers
 */

package abanyu.transphone.taxi.login;

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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GetPlateNumberList extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //outside class variable fetchers	
  private PlatenumberActivity platenumberChooser;
  private String url;
  
  //list variable
  private List<String> plateNumbers;
  
  //asynctask variable
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  static InputStream input = null;
  static JSONObject jObj = null;
  static String json = "";
	
  //CONSTRUCTOR	
  public GetPlateNumberList(PlatenumberActivity taxiMapContext, String stringUrl){
    platenumberChooser = taxiMapContext;
	url = stringUrl; //this url is created by the makeUrl() function
	plateNumbers = new ArrayList<String>();
  }
		
  protected void onPreExecute() {
	super.onPreExecute();
	progressDialog = new ProgressDialog(platenumberChooser);
	progressDialog.setMessage("Retrieving Plate Numbers, please wait...");
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
	  JSONArray ja = new JSONArray(result);
	  int n =ja.length();
	        
      for (int i = 0; i < n; i++) {
        // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
        JSONObject jo = ja.getJSONObject(i);
	             
	    // RETRIEVE EACH JSON OBJECT'S FIELDS
        String plateNo = jo.getString("plateNo");
        System.out.println(plateNo);
        plateNumbers.add(plateNo);
	  }
	    
      //CONVERTS the list of string into an array of string
      String arr[];        
      arr=new String[plateNumbers.size()];

      for (int idx=0; idx<plateNumbers.size(); idx++) {
          String elem = plateNumbers.get(idx);
    	  arr[idx] = elem;
      }
      
      platenumberChooser.setListAdapter(new ArrayAdapter<String>(platenumberChooser, R.layout.listview, arr));
	    	 
	  ListView listView = platenumberChooser.getListView();
	  listView.setTextFilterEnabled(true);

	  listView.setOnItemClickListener(new OnItemClickListener() {  		
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	      String plateNo=((TextView) view).getText().toString(); //get the selected plate number
          Intent i = new Intent(platenumberChooser,LoginTaxiActivity.class);
          i.putExtra("plateNumber", plateNo);
          platenumberChooser.startActivity(i);		
        }
      });    	        
	}catch (JSONException e) {
      e.printStackTrace();
    }
  }	
}
