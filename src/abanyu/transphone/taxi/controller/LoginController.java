package abanyu.transphone.taxi.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abanyu.transphone.taxi.LoginMVC;
import abanyu.transphone.taxi.model.JSONMode;
import abanyu.transphone.taxi.model.LoginData;
import abanyu.transphone.taxi.view.ErrorView;
import abanyu.transphone.taxi.view.LoginBox;
import abanyu.transphone.taxi.view.PasswordBox;
import abanyu.transphone.taxi.view.MappingView;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taxi.R;

import connections.MyConnection;

/*
 * All of the overridable methods in this class will be dynamically called by the JSONGetter thread
 */
public class LoginController implements LoginInterface{
	//There will be a lot of variable declarations here because we want to avoid too much parameters to be passed
	//on the overridable methods. So, as a solution, we created global variables instead

	/** CUSTOM CLASS DECLARATIONS  **/
	private LoginMVC loginMVC; //the class that contains the instance of each MVC class component that are crucial in this login process
	private JSONMode nextMode, thisMode; //global declaration for the two types in managing JSON Data: *SET and *GET

	/** CUSTOM MULTIDATA STORAGE DECLARATIONS  **/
	private List<HashMap<String, String>> dbTableEntryList; //global declaration for temporary holder of list data that will be retrieved online and will then be synchronized with the application's login model
	private List<String> urlParams; //global declaration to dynamically create parameters which will be appended to the JSON url

	private boolean requirePassword; //global declaration that will determine if a password will be prompted during list selections
	private String box;
	private String setterParamClass, //parameter data type for the setter class which is required for dynamic method invocations on the login model methods right after list selections
//				 getterParamClass, //parameter data type for the getter class which is required for dynamic method invocations on the login model methods in retrieving specific table primary keys
				 idSetterFunction, //function name for the setter class which is required for dynamic method invocations on the login model methods right after list selections
				 mapComparator,    //the associative key for comparing its corresponding value for each item in the list of specificTableEntries
				 keyGet, 					 //the defined associative key that is to be returned when calling login model's getKeyValue method
				 nextUrl, 				 //the url which will be processed by the JSONManager after the listing method has been finished
				 progDialogMsg,    //the string which will be echoed during the JSONManager's doInBackground method
				 nextFunction,		 //the method name which will be invoke right after an item of  a list view has been selected
				 itemClicked; 		 //global declaration that will hold the name of the selected item in the listView
//	private Object functionClass;		 //the data type of the function which will be invoked by the login getter method

	private PasswordBox passwordBox;
	private LoginBox loginBox;
	public MyConnection conn;
	public LoginController(LoginMVC pLoginMVC){
		loginMVC = pLoginMVC;		
		loginMVC.setLoginController(this);
		conn = new MyConnection();
		
		urlParams = new ArrayList<String>();
		//thread that will retrieve company data
		
		nextUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=getCompanies";
		progDialogMsg = "Retrieving all Company Data";
		nextFunction = "listCompanies";
		new JSONManager(loginMVC, nextUrl, progDialogMsg, nextFunction,JSONMode.GET).execute();
	}

	//this function will be dynamically called by the JSONManager thread
	@Override
	public void listCompanies(JSONArray companies) {
	  //list variable
	  List<String> listElems = new ArrayList<String>();
	  List<HashMap<String, String>> companyList = new ArrayList<HashMap<String, String>>();

	  try{
	  	for (int i = 0; i < companies.length(); i++) {
	  		Map<String, String> company = new HashMap<String, String>();
	  		// GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
	  		JSONObject jo = companies.getJSONObject(i);
      
	  		// RETRIEVE EACH JSON OBJECT'S FIELDS
	  		company.put("id", String.valueOf(jo.getString("id")));
	  		company.put("name", jo.getString("name"));
	  		company.put("contact", jo.getString("contact"));
	  		company.put("ip", jo.getString("serverip"));
      	  		
	  		listElems.add(company.get("name"));
	  		companyList.add((HashMap<String, String>) company);
	  	}
  
	  	loginMVC.getLoginModel().listCompanies(companyList);

	  	dbTableEntryList = companyList;
	  	requirePassword = true;
	  	box = "username";
	  	setterParamClass = "int";
	  	idSetterFunction = "setSelCompanyID";
	  	mapComparator = "name";
	  	keyGet = "id";
	  	thisMode = JSONMode.GET;
	  	nextMode = JSONMode.GET;
	  	nextUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=getTaxiInfos";
	  	progDialogMsg = "Retrieving All Taxi Data";
	  	nextFunction = "listTaxis"; 
//	  	getterParamClass = "string";	  	
	  	urlParams.clear();
	  	urlParams.add("getSelectedCompanyID");  		  
	  	
	  	
  		((TextView)loginMVC.getLoginView().findViewById(R.id.listview_header)).setText("COMPANY LIST");
	  	makeListView(listElems);
		} catch (JSONException e) {
	  	stopAndShowError("JSON Exception at get company list: "+e.getMessage());
		}	  
		
	}

	//this function will be dynamically called by the JSONManager thread
	@Override
	public void listTaxis(JSONArray taxis) {
	  //list variable
		List<String> listElems = new ArrayList<String>();
	  List<HashMap<String, String>> taxiList = new ArrayList<HashMap<String, String>>();

	  try{
	  	for (int i = 0; i < taxis.length(); i++) {
	  		Map<String, String> taxi = new HashMap<String, String>();
	  		// GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
	  		JSONObject jo = taxis.getJSONObject(i);
      
	  		// RETRIEVE EACH JSON OBJECT'S FIELDS
	  		taxi.put("plateNo", String.valueOf(jo.getString("plateNo")));
	  		taxi.put("bodyNo", jo.getString("bodyNo"));
	  		taxi.put("description", jo.getString("description"));
      
	  		listElems.add(taxi.get("plateNo"));
	  		taxiList.add((HashMap<String, String>) taxi);
	  	}
	  	
	  	
	  	loginMVC.getLoginModel().listTaxis(taxiList);
	  	
//	  	functionClass = loginMVC.getLoginModel();
	  	dbTableEntryList = taxiList;
	  	requirePassword = false;
	  	setterParamClass = "string";
	  	idSetterFunction = "setSelTaxiPN";
	  	mapComparator = "plateNo";
	  	keyGet = "plateNo";
	  	thisMode = JSONMode.GET;
	  	nextMode = JSONMode.GET;

	  	nextUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=getDriverInfos";
	  	progDialogMsg = "Retrieving All Driver Data";
	  	nextFunction = "listDrivers"; 
	  	setterParamClass = "string";
	  	urlParams.clear();
	  	urlParams.add("getSelectedCompanyID");

  		((TextView)loginMVC.getLoginView().findViewById(R.id.listview_header)).setText("TAXI LIST");
	  	makeListView(listElems);	    	  	
		} catch (JSONException e) {
	  	stopAndShowError("JSON Exception at get taxi list: "+e.getMessage());
		}
	}

	//this function will be dynamically called by the JSONManager thread
	@Override
	public void listDrivers(JSONArray drivers) {
	  //list variable
		List<String> listElems = new ArrayList<String>();
	  List<HashMap<String, String>> driverList = new ArrayList<HashMap<String, String>>();

	  try{
	  	for (int i = 0; i < drivers.length(); i++) {
	  		Map<String, String> driver = new HashMap<String, String>();
	  		// GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
	  		JSONObject jo = drivers.getJSONObject(i);
      
	  		// RETRIEVE EACH JSON OBJECT'S FIELDS
	  		driver.put("name", String.valueOf(jo.getString("name")));
	  		driver.put("license", jo.getString("license"));
	  		driver.put("password", jo.getString("password"));
      
	  		listElems.add(driver.get("name"));
	  		driverList.add((HashMap<String, String>) driver);
	  	}
	  	
	  	loginMVC.getLoginModel().listDrivers(driverList);
	  		  	
//	  	functionClass = this;
	  	dbTableEntryList = driverList;
	  	requirePassword = true;
	  	box = "password";
	  	setterParamClass = "None";
	  	idSetterFunction = "setSelDriverLicense";
	  	mapComparator = "name";
	  	keyGet = "license";
	  	thisMode = JSONMode.GET;
	  	nextMode = JSONMode.SET;
	  	
	  	nextUrl = conn.getDBUrl()+"/thesis/dbmanager.php?fname=registerDriver";
	  	progDialogMsg = "Registering taxi driver to the server";
	  	nextFunction = "registerAndStart"; 
//	  	getterParamClass = "None";
	  	
	  	urlParams.clear();
	  	urlParams.add("getSelectedDriverLicense");	  		  		  	
	  	urlParams.add("getSelectedTaxiPN");
  		((TextView)loginMVC.getLoginView().findViewById(R.id.listview_header)).setText("DRIVER LIST");
	  	makeListView(listElems);
	  } catch (JSONException e) {
	  	stopAndShowError("JSON Exception at get driver list: "+e.getMessage());
	  }	  
	}
	
	@Override
	public void registerAndStart() {
		LoginData data = loginMVC.getLoginModel();
		passwordBox.dismiss();
	  loginMVC.getLoginView().finish();

	  Intent i = new Intent(loginMVC.getLoginView(), MappingView.class);
	  i.putExtra("serverip", (String)data.getKeyValueByName("id", loginMVC.getLoginModel().getSelectedCompanyID(), "ip", loginMVC.getLoginModel().getCompanyList()));
	  i.putExtra("plateNo", data.getSelectedTaxiPN());
	  i.putExtra("bodyNo", (String)data.getKeyValueByName("plateNo", data.getSelectedTaxiPN(), "bodyNo", data.getTaxiList()));
	  i.putExtra("desc", (String)data.getKeyValueByName("plateNo", data.getSelectedTaxiPN(), "description", data.getTaxiList()));
	  i.putExtra("compName", (String)data.getKeyValueByName("id", data.getSelectedCompanyID(), "name", data.getCompanyList()));
	  i.putExtra("compContact", (String)data.getKeyValueByName("id", data.getSelectedCompanyID(), "contact", data.getCompanyList()));
	  i.putExtra("drivName", (String)data.getKeyValueByName("license", data.getSelectedDriverLicense(), "name", data.getDriverList()));
	  loginMVC.getLoginView().startActivity(i);		
	}
		
	public void makeListView(List<String> listElems){
	  String arr[];        
  	arr=new String[listElems.size()];
  	
  	for (int idx=0; idx<listElems.size(); idx++) {
      String elem = listElems.get(idx);
      arr[idx] = elem;
  	}
  
  	loginMVC.getLoginView().setListAdapter(new ArrayAdapter<String>(loginMVC.getLoginView(), R.layout.listnode, arr)); 
  	ListView listView = loginMVC.getLoginView().getListView();
  	listView.setTextFilterEnabled(true);
  	
  	listView.setOnItemClickListener(new OnItemClickListener() {  		
  		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
  			itemClicked=((TextView) view).getText().toString();   				
  			
  			if(thisMode==JSONMode.GET)
  				setLoginDataIDs();	  		  	
  			else
  				getServerData(); //dont have anything to set
  		}
  	}); 		
	}
	
	public void setLoginDataIDs(){
		if(requirePassword){
			System.out.println("Box: "+box);
			if(box.equals("password")){
				passwordBox=new PasswordBox(loginMVC,dbTableEntryList,itemClicked, idSetterFunction, setterParamClass, mapComparator, keyGet);
				passwordBox.show();  	    	
			}else{
				loginBox=new LoginBox(loginMVC,dbTableEntryList,itemClicked, idSetterFunction, setterParamClass, mapComparator, keyGet);
				loginBox.show();  	    	
			}
		}else{
    	try {
    		if(setterParamClass.equals("int"))
    			loginMVC.getLoginModel().getClass().getMethod(idSetterFunction, int.class).invoke(loginMVC.getLoginModel(), Integer.parseInt((String) loginMVC.getLoginModel().getKeyValueByName(mapComparator, itemClicked, keyGet, dbTableEntryList)));
    		else	
    			loginMVC.getLoginModel().getClass().getMethod(idSetterFunction, String.class).invoke(loginMVC.getLoginModel(), loginMVC.getLoginModel().getKeyValueByName(mapComparator, itemClicked, keyGet, dbTableEntryList));

    		thisMode = nextMode;//change of mode when the target setter function has been invoked    		
    	} catch (SecurityException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered IO exception on LoginController, setLoginDataIDs(): "+e.getMessage(), Toast.LENGTH_LONG).show();
  		} catch (NoSuchMethodException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered No such method exception on LoginControler, setLoginDataIDs(): "+e.getMessage(), Toast.LENGTH_LONG).show();
  		} catch (IllegalArgumentException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Illegal Argument exception on LoginController, setLoginDataIDs(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	} catch (IllegalAccessException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Illegal Access exception on LoginController, setLoginDataIDs(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	} catch (InvocationTargetException e) {  	
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Invocation Target exception on LoginController, setLoginDataIDS(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	}        	
    	
    	getServerData();
		}		
		
	}
	
	public void getServerData(){		
		int i=1;
		for(String param: urlParams){
			Object value = "";
  		
			try {
					value = loginMVC.getLoginModel().getClass().getMethod(param).invoke(loginMVC.getLoginModel());
    	} catch (SecurityException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered IO exception on LoginController, getServerData(): "+e.getMessage(), Toast.LENGTH_LONG).show();
  		} catch (NoSuchMethodException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered No such method exception on LoginControler, getServerData(): "+e.getMessage(), Toast.LENGTH_LONG).show();
  		} catch (IllegalArgumentException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Illegal Argument exception on LoginController, getServerData(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	} catch (IllegalAccessException e) {
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Illegal Access exception on LoginController, getServerData(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	} catch (InvocationTargetException e) {  	
		  	Toast.makeText(loginMVC.getLoginView(), "Encountered Invocation Target exception on LoginController, getServerData(): "+e.getMessage(), Toast.LENGTH_LONG).show();
    	}        	
  		
 			if(setterParamClass.equals("int"))
 				nextUrl= new StringBuilder().append(nextUrl).append("&arg").append(i).append("=").append(value).toString();    
 			else
 				nextUrl= new StringBuilder().append(nextUrl).append("&arg").append(i).append("=").append(String.valueOf(value)).toString();    
  				
 			i++;
  	}
		
  	new JSONManager(loginMVC, nextUrl, progDialogMsg, nextFunction, nextMode).execute();  				
	}
	
  public void stopAndShowError(String errorMsg){
//  	loginView.finish();
  	Intent intent = new Intent(loginMVC.getLoginView(),ErrorView.class);  	
  	intent.putExtra("errorMsg", errorMsg); 
  	intent.putExtra("intentType", "show");
  	loginMVC.getLoginView().startActivity(intent);
  }
}

