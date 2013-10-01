package abanyu.transphone.taxi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import abanyu.transphone.taxi.view.LoginBox;

public class ThreadJSONParser implements Runnable{	
	private InputStream input = null;
	private JSONObject jObj = null;
	private String json = "";
	private String url;
	private LoginBox box;
	
	
	public ThreadJSONParser(LoginBox pBox, String pUrl) {
		url = pUrl;
		box = pBox;
	}
	
	public void getJSONfromURL() {		
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			input = entity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
			StringBuilder string = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
				string.append(line + "\n");
				
			json = string.toString();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		box.result = json;
	}

	@Override
	public void run() {
		getJSONfromURL();
	}
}
