package abanyu.transphone.taxi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

/*
 * This class will send this taxi object to the server
 * 
 */
public class SendServerAsyncTask extends AsyncTask<Void, Void, Void>{
  private Socket socket;
  private ObjectOutputStream output;
  private actors.MyTaxi myTaxi;
  
  public SendServerAsyncTask(actors.MyTaxi pMyTaxi){
	myTaxi=pMyTaxi;  
  }
  @Override
  protected Void doInBackground(Void... args){
	try{
	  socket = new Socket(connections.MyConnection.serverIp, connections.MyConnection.serverPort); //creates a new socket to connect to the server
	  output = new ObjectOutputStream(socket.getOutputStream());			

//	  System.out.println("taxi obj: "+myTaxi);
//	  System.out.println("curr lat: "+myTaxi.get);
//	  System.out.println("curr lng: "+myTaxi.clientLng);

	  output.writeObject(myTaxi); //adds a taxi instance on the output stream
	  output.flush();
	}catch(UnknownHostException e){
      e.printStackTrace();
    }catch(IOException e){
      System.out.println("rick io exception"+e.getMessage());
    }finally{
      try{
        if(socket != null)
		  socket.close();

	    if(output != null)
		  output.close();
	  }catch(IOException e){
	    e.printStackTrace();
	  }
	}
	return null;
  }		
}