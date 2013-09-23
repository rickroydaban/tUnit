package abanyu.transphone.taxi.model;

import abanyu.transphone.taxi.controller.TaxiMapController;
import abanyu.transphone.taxi.view.MappingView;

public class MappingMVC {
	MappingData mappingData;
	MappingView taxiMapView;
	TaxiMapController taxiMapController;
	
	public MappingMVC(MappingData pMappingData, MappingView pTaxiMapView, TaxiMapController pTaxiMapController){
		mappingData = pMappingData;
		taxiMapView = pTaxiMapView;
		taxiMapController = pTaxiMapController;
	}
	
	public MappingData getModel(){
		return mappingData;
	}
	
	public MappingView getView(){
		return taxiMapView;
	}
	
	public TaxiMapController getController(){
		return taxiMapController;
	}
}
