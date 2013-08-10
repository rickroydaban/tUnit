package abanyu.transphone.taxi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import connections.*;
import android.os.AsyncTask;

/*
 * This class will send this taxi object to the server
 * 
 */
public class SendServerAsyncTask extends AsyncTask<Void, Void, Void>{
  private Socket socket;
  private ObjectOutputStream output;
  private actors.MyTaxi myTaxi;
  private MyConnection conn;
  
  public SendServerAsyncTask(actors.MyTaxi pMyTaxi, MyConnection pConn){
	myTaxi=pMyTaxi;  
	conn = pConn;
  }
  @Override
  protected Void doInBackground(Void... args){
	try{
	  System.out.println("server ip: " + conn.getServerIp() + "server port: " + conn.getServerPort());
	  socket = new Socket(conn.getServerIp(), conn.getServerPort()); //creates a new socket to connect to the server
	  output = new ObjectOutputStream(socket.getOutputStream());			

	  output.writeObject(myTaxi); //adds a taxi instance on the output stream
	  output.flush();
	}catch(UnknownHostException e){
	      System.out.println("taxi unknown host exception"+e.getMessage());
    }catch(IOException e){
      System.out.println("taxi io exception"+e.getMessage());
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