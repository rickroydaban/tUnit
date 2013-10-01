package abanyu.transphone.taxi.model;

import java.util.HashMap;
import java.util.List;

import connections.MyConnection;

public class LoginData{
  private List<HashMap<String, String>> companies, taxis, drivers;
  private int selCompID;
  private String selTaxiPlateNo, selDriverLicense;
  public LoginData(){
	  
  }
  
  public void setSelCompanyID(int pID){
  	selCompID = pID;
  }
  
  public void setSelTaxiPN(String pTaxiPlateNo){
  	selTaxiPlateNo = pTaxiPlateNo;
  }
  
  public void setSelDriverLicense(String pDriverLicense){
  	selDriverLicense = pDriverLicense;
  }
  
  public void listCompanies(List<HashMap<String, String>> pCompanies){
	  companies = pCompanies;
  }
  
  public void listTaxis(List<HashMap<String, String>> pTaxis){
	  taxis = pTaxis;
  }
  
  public void listDrivers(List<HashMap<String, String>> pDrivers){
	  drivers = pDrivers;
  }
    
  public List<HashMap<String,String>> getCompanyList(){
  	return companies;
  }
  
  public List<HashMap<String,String>> getTaxiList(){
  	return taxis;
  }
  
  public List<HashMap<String,String>> getDriverList(){
  	return drivers;
  }
    
  public Object getKeyValueByName(String mapComparator, Object selName, String getKeyValue, List<HashMap<String, String>> pMap){
  	for(HashMap<String, String> map: pMap) {
      if(selName instanceof String){
      	System.out.println("Checking mapComparator: "+map.get(mapComparator)+" == selected: "+selName);
    		if(map.get(mapComparator).equals(selName)){
    			return getKeyValue(getKeyValue, map);
    		}
    	}else if(selName instanceof Integer){
    		if(Integer.parseInt(map.get(mapComparator))==(Integer)selName){
        	return getKeyValue(getKeyValue, map);
    		}    		
    	}
    }
    
    return null;
  }
  
  public Object getKeyValue(String getKeyValue, HashMap<String, String> map){
		Object value = map.get(getKeyValue);

		System.out.println("Returning "+value);

		if(!(value instanceof String))
			return String.valueOf(value);
		else
			return value;

		
  }
  
  public int getSelectedCompanyID(){
	  return selCompID;
  }

  public String getSelectedTaxiPN(){
	  return selTaxiPlateNo;
  }
  
  public String getSelectedDriverLicense(){
  	return selDriverLicense;
  }
}