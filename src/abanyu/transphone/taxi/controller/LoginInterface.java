package abanyu.transphone.taxi.controller;

import org.json.JSONArray;

public interface LoginInterface{
	public void listCompanies(JSONArray companies);
	public void listTaxis(JSONArray taxis);
	public void listDrivers(JSONArray drivers);
	public void registerAndStart();
}
