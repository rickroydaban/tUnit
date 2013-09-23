package abanyu.transphone.taxi;

import abanyu.transphone.taxi.controller.LoginController;
import abanyu.transphone.taxi.model.LoginData;
import abanyu.transphone.taxi.view.LoginView;

public class LoginMVC{
	LoginData loginModel;
	LoginView loginView;
	LoginController loginController;
	
	public LoginMVC(LoginData pLoginModel, LoginView pLoginView, LoginController pLoginController){
		loginModel = pLoginModel;
		loginView = pLoginView;
		loginController = pLoginController;
	}
	
	public LoginMVC(LoginData pLoginModel, LoginView pLoginView){
		loginModel = pLoginModel;
		loginView = pLoginView;
	}

	public void setLoginController(LoginController pLoginController){
		loginController = pLoginController;
	}
	
	public LoginData getLoginModel(){
		return loginModel;
	}
	
	public LoginView getLoginView(){
		return loginView;
	}
	
	public LoginController getLoginController(){
		return loginController;
	}
}
