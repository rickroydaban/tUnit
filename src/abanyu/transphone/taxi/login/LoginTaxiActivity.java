/*
 * After a plate number has been chosen by the plate number activity,
 * a password will be required before a driver will be registered as the driver of the 
 * taxi with the specified plate number
 */

package abanyu.transphone.taxi.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.taxi.R;

public class LoginTaxiActivity extends Activity{
  TextView passwordLabel;
  EditText passwordField;
  String password;
  public static String plateNo;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); //removes title bar
    setContentView(R.layout.passwordbox);
	
    Bundle intentExtra = getIntent().getExtras();
    plateNo = intentExtra.getString("plateNumber");
 
    passwordLabel=(TextView)findViewById(R.id.passwordLabel);
    passwordLabel.setText("Please enter password for "+plateNo);
    
    Button loginButton = (Button)findViewById(R.id.loginButton);
    
    loginButton.setOnClickListener(new OnClickListener() {
	  @Override
      public void onClick(View v) {
		    System.out.println("hellow");
		    passwordField = (EditText)findViewById(R.id.passwordField);
		    password = passwordField.getText().toString();
		    System.out.println("password is " + password);
		    
		    new LoginTaxiTask(LoginTaxiActivity.this, "http://transphone.freetzi.com/thesis/dbmanager.php?fname=authenticateTaxi&arg1="+plateNo+"&arg2="+password).execute();
  
	  }
	});    
  }
}
