package abanyu.transphone.taxi.model;

import abanyu.transphone.taxi.view.LoginView;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetData {
	LoginView loginView;
	ConnectivityManager connManager;
	NetworkInfo mWifi;
	
	public InternetData(LoginView pLoginView){
		loginView = pLoginView;
		connManager = (ConnectivityManager) loginView.getSystemService(Context.CONNECTIVITY_SERVICE);
	    mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);		
	}
      
  public boolean hasInternetConnection(){
  	if(mWifi.isConnected())
  		return true;
    		
    return false;
  }
}
