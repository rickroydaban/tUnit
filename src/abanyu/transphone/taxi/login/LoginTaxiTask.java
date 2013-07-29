/*
 * This is a Thread class used by the LoginTaxiActivity to query the specified url
 * and check if the password matches to the specified taxi plate number credentials
 */

package abanyu.transphone.taxi.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;

public class LoginTaxiTask extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //outside class variable fetchers	
  private LoginTaxiActivity loginActivity;
  private String url;
  
  Intent intent;

  TextView failedLable;
  
  //asynctask variable
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  static InputStream input = null;
  static JSONObject jObj = null;
  static String json = "";
	
  //CONSTRUCTOR	
  public LoginTaxiTask(LoginTaxiActivity taxiMapContext, String stringUrl){
    loginActivity = taxiMapContext;
	url = stringUrl; //this url is created by the makeUrl() function
  }
		
  protected void onPreExecute() {
	super.onPreExecute();
	progressDialog = new ProgressDialog(loginActivity);
	progressDialog.setMessage("Authenticating Taxi. Please Wait");
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

	System.out.println("result is "+result);
	if(result.equals("1")){
	  System.out.println("Performing intent to getdriveractivity");
      intent = new Intent(loginActivity, GetDatabaseActivity.class);
      intent.putExtra("plateNumber", loginActivity.plateNo);
      loginActivity.startActivity(intent);
	}else{
//
	}
  }	
}
