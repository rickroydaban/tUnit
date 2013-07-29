/*
 * This class will be used when the taxi has been successfully authenticated.
 * this class will retrieve a list of taxi driver name to be displayed
 */

package abanyu.transphone.taxi.login;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class GetDatabaseActivity extends ListActivity{
  String plateNo;
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); //removes title bar
	
    Bundle intentExtra = getIntent().getExtras();
    plateNo = intentExtra.getString("plateNumber");
 
    new GetDatabaseThread(GetDatabaseActivity.this, "http://transphone.freetzi.com/thesis/dbmanager.php?fname=getDriverList").execute();

  }
  
	/*	
	plateNo = (EditText)findViewById(R.id.platenoField); 
	password = (EditText)findViewById(R.id.passwordField);
	failed = (TextView)findViewById(R.id.failed); //the textviw which will be shown when the login process fails
		
	Button loginButton = (Button)findViewById(R.id.loginButton);
    loginButton.setOnClickListener(new OnClickListener() {
	  @Override
      public void onClick(View v) {
        String IP = getIPAddress();//retrieves the ip of the mobile device
				
        if(IP != null){
          //encapsulates all login data on an array since asynctask can only receive one data parameter at a time
          String[] loginData = new String[3]; 
          loginData[0] = plateNo.getText().toString();
          loginData[1] = password.getText().toString();
          loginData[2] = IP; 
				
		  if(!loginData[1].equals(""))
		    new ConnectServerAsyncTask(Login.this).execute(loginData);
		  else
			failed.setText("Error: Please input your password.");
		}else
          failed.setText("Error: Cannot retrieve IP!");
	  }
	});
  }
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		String extra = getIntent().getStringExtra("calling_class");
		
		if(extra.equals("ConnectServerAsyncTask"))
			failed.setText("Connection Failed!");
	}
	
	private String getIPAddress() {
        try 
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            
            for (NetworkInterface intf : interfaces) 
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
            
                for (InetAddress addr : addrs) 
                {
                    if (!addr.isLoopbackAddress()) 
                    {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        
                        if (isIPv4) 
                        	return sAddr;
                    }
                }
            }
        } catch (Exception e) { 
        	e.printStackTrace();
        } 
        return null;*/

}
