/*
 * This is a Thread class used by the PlateNumberActivity to query the specified url
 * and retrieve a list of plate numbers
 */

package abanyu.transphone.taxi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import abanyu.transphone.taxi.LoginMVC;
import abanyu.transphone.taxi.model.JSONMode;
import abanyu.transphone.taxi.view.ErrorView;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class JSONManager extends AsyncTask<Void, Void, String>{
  //parameters for onPreExecute, doInBackground, onPostExecute respectively
	
  //outside class variable fetchers	
  private ListActivity loginView;
  private String url, nextFunction, progDialogMsg;
  private JSONMode mode;
  //asynctask variable
  private ProgressDialog progressDialog;
	
  //JSON manipulation variables
  static InputStream input = null;
  static JSONObject jObj = null;
  static String json = "";
	
  private LoginController loginController;
  
  //CONSTRUCTOR	
  public JSONManager(LoginMVC pLoginMVC, String pUrl, JSONMode pMode){
  	loginView = pLoginMVC.getLoginView();
  	loginController = pLoginMVC.getLoginController();
  	url = pUrl;
  	mode = pMode;
  	nextFunction = ""; //no function will be called after the json data retrieval
  	progDialogMsg = "";
  }
  
  public JSONManager(LoginMVC loginMVC, String stringUrl, String pPDM, String pFname, JSONMode pMode){
    loginView = loginMVC.getLoginView();
    loginController = loginMVC.getLoginController();
    url = stringUrl; 
    nextFunction = pFname;
    progDialogMsg = pPDM;
    mode = pMode;
  }
		
  protected void onPreExecute() {
  	super.onPreExecute();
  		progressDialog = new ProgressDialog(loginView);
  		progressDialog.setMessage(progDialogMsg+", please wait...");
  		progressDialog.setIndeterminate(true);
  		progressDialog.show();
  }
		
  @Override
  protected String doInBackground(Void... arg0) {
    String result=null;

    try{
	  DefaultHttpClient client = new DefaultHttpClient();
	  HttpGet get = new HttpGet(url);
			
	  HttpResponse response = client.execute(get);
      result = EntityUtils.toString(response.getEntity());  
    }catch (UnsupportedEncodingException e) {
    	stopAndShowError(e.getMessage());
    } catch (ClientProtocolException e) {
    	stopAndShowError(e.getMessage());
    } catch (IOException e) {
    	stopAndShowError(e.getMessage());
    } 	
	
    return result;		
  }
		
  protected void onPostExecute(String result) {
  	super.onPostExecute(result);
  	progressDialog.dismiss();
  	
		try {
			if(mode == JSONMode.GET)
				loginController.getClass().getMethod(nextFunction, JSONArray.class).invoke(loginController, new JSONArray(result));
			else{
				if(result.equals("1")){ //server value reset was SUCCESSFULL						
					loginController.getClass().getMethod(nextFunction).invoke(loginController);
				}else{ //stops the login process and shows its error message whenever an error during the process occur
					stopAndShowError(result);
				}
			}
		} catch (Exception e) {
			stopAndShowError("Exception: "+e.getMessage());
		}
	}		
  
  public void stopAndShowError(String errorMsg){
		Intent intent = new Intent(loginView,ErrorView.class);
		intent.putExtra("errorMsg", errorMsg);
		intent.putExtra("intentType", "show");
		loginView.startActivity(intent);
  	
  }
}
