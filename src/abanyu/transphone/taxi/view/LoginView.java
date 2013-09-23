package abanyu.transphone.taxi.view;


import com.example.taxi.R;

import abanyu.transphone.taxi.LoginMVC;
import abanyu.transphone.taxi.controller.LoginController;
import abanyu.transphone.taxi.model.InternetData;
import abanyu.transphone.taxi.model.LoginData;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
/*
 * this class is the instance of view for the login process
 */
public class LoginView extends ListActivity{ 
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.listview);

    if(new InternetData(this).hasInternetConnection()){
    	 new LoginController(new LoginMVC(new LoginData(), this)); //builds a new controller for the login process    
    }else{ //blocks the user to proceed to login when internet connection is not available
    	finish();
    	Intent intent = new Intent(this,ErrorView.class);
    	intent.putExtra("errorMsg", "Please enable Wi-Fi"); 
    	intent.putExtra("intentType", "refresh");
    	startActivity(intent);
    }  
  }
}
