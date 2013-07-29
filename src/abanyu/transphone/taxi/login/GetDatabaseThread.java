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
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GetDatabaseThread extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //Constructor Fetcher Variables	
  private GetDatabaseActivity getDatabaseActivity; //the activity context
  private String url; //the url passed
  
  private List<String> jsonDecodedList; //the arrayed form of the json encoded return object of the specified url
  
  //asynctask variables
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  InputStream input = null;
  JSONObject jObj = null;
  String json = "";
	
  //CONSTRUCTOR	
  public GetDatabaseThread(GetDatabaseActivity pActivityContext, String pStringUrl){
    getDatabaseActivity = pActivityContext;
	url = pStringUrl;
	jsonDecodedList = new ArrayList<String>();
  }
		
  protected void onPreExecute() {
	super.onPreExecute();
	progressDialog = new ProgressDialog(getDatabaseActivity);
	progressDialog.setMessage("Retrieving Database Information, Please wait...");
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
	  //result is the string format of the JSON object returned by the doInBackground method
	  JSONArray jsonArray = new JSONArray(result);
	        
	  //the usual structure of the returned JSON array will be one-dimensional array
      for (int i=0; i<jsonArray.length(); i++) {
        // GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
        JSONObject jo = jsonArray.getJSONObject(i);
	             
	    // RETRIEVE EACH JSON OBJECT'S FIELDS
        String license = jo.getString("license");
        String name = jo.getString("name");
        
        jsonDecodedList.add(license);
	  }
	    
      //CONVERTS the list of string into an array of string since listview only accepts array of strings
      String convertedJSONList[];        
      convertedJSONList=new String[jsonDecodedList.size()];

      for (int idx=0; idx<jsonDecodedList.size(); idx++) {
          String elem = jsonDecodedList.get(idx);
    	  convertedJSONList[idx] = elem;
      }
      
      getDatabaseActivity.setListAdapter(new ArrayAdapter<String>(getDatabaseActivity, R.layout.listview, convertedJSONList));
	    	 
	  ListView listView = getDatabaseActivity.getListView();
	  listView.setTextFilterEnabled(true);

	  listView.setOnItemClickListener(new OnItemClickListener() {  		
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	      String driverLicense=((TextView) view).getText().toString(); //get the selected driver name
	      System.out.println("hello " + driverLicense + ", " + getDatabaseActivity.plateNo);
	      new RegisterDriverTask(getDatabaseActivity,"http://transphone.freetzi.com/thesis/dbmanager.php?fname=registerDriver&arg1="+driverLicense+"&arg2="+getDatabaseActivity.plateNo).execute();
        }
      });    	        
	}catch (JSONException e) {
      e.printStackTrace();
    }
  }	
}
