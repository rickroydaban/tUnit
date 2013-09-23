package abanyu.transphone.taxi.view;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import abanyu.transphone.taxi.LoginMVC;
import abanyu.transphone.taxi.controller.LoginController;
import abanyu.transphone.taxi.model.LoginData;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PasswordBox extends Dialog implements android.view.View.OnClickListener {

  private LoginView loginView;
  private LoginData loginData;
  private LoginController loginController;
  private Button yes, no;
  private EditText pass;
  private TextView errorView;
  private String selected, idSetterFunction, paramClass, mapComparator, keyGet;
  private List<HashMap<String, String>> map;
  
  public PasswordBox(LoginMVC loginMVC, final List<HashMap<String, String>> pMap, String pSelected, String pIdSetterFunction, String pParamClass, String pMapComparator, String pKeyValue) {
    super(loginMVC.getLoginView());
    loginData = loginMVC.getLoginModel();
    loginView = loginMVC.getLoginView();
    loginController = loginMVC.getLoginController();
    selected = pSelected;
    idSetterFunction = pIdSetterFunction;
    map = pMap;
    paramClass = pParamClass;
    keyGet = pKeyValue;
    mapComparator = pMapComparator;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.example.taxi.R.layout.passwordbox);
    yes = (Button) findViewById(com.example.taxi.R.id.loginButton);
    no = (Button) findViewById(com.example.taxi.R.id.cancelButton);
    pass = (EditText) findViewById(com.example.taxi.R.id.passwordField);
    errorView = (TextView)findViewById(com.example.taxi.R.id.errorView);
    yes.setOnClickListener(this);
    no.setOnClickListener(this);

  }

@Override
public void onClick(View v) {
    switch (v.getId()) {
    case com.example.taxi.R.id.loginButton:
    	if(loginData.getKeyValueByName(mapComparator,selected,"password",map).equals(pass.getText().toString())){
      	try {
      		if(paramClass.equals("int"))
      			loginData.getClass().getMethod(idSetterFunction, int.class).invoke(loginData, Integer.parseInt((String) loginData.getKeyValueByName(mapComparator, selected, keyGet, map)));
      		else
      			loginData.getClass().getMethod(idSetterFunction, String.class).invoke(loginData, loginData.getKeyValueByName(mapComparator, selected, keyGet, map));
      	
      	
      	} catch (SecurityException e) {
    			System.out.println("Security exception: "+e.getMessage());
    		} catch (NoSuchMethodException e) {
    			System.out.println("No such method: : "+e.getMessage());    			
    		} catch (IllegalArgumentException e) {
    			System.out.println("Illegal argument exception: "+e.getMessage());
      	} catch (IllegalAccessException e) {
    			System.out.println("Illegal access exception: "+e.getMessage());
      	} catch (InvocationTargetException e) {  	
    			System.out.println("Invocation target exception: "+e.getMessage());
      	}      	
      	
      	if(mapComparator == "name" && ((String)loginData.getKeyValueByName("id", loginData.getSelectedCompanyID(), "ip", map)).equals("")){
      		dismiss();	
      		stopAndShowError("Server is not yet online");
      	}else{ //the system will not proceed if the server is offline
      	  loginController.getServerData();      	
          dismiss();
      	}
      }else{
      	errorView.setVisibility(View.VISIBLE);
      	errorView.setText("Password is incorrect.");
      }

      break;
    case com.example.taxi.R.id.cancelButton:
      dismiss();
      break;
    default:
      break;
    }
  }

	public void stopAndShowError(String errorMsg){
		Intent intent = new Intent(loginView,ErrorView.class);
		intent.putExtra("errorMsg", errorMsg);
		intent.putExtra("intentType", "show");
		loginView.startActivity(intent);
	}

}