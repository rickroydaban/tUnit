/*
 * Test for Git
 */

/*
=======
>>>>>>> parent of 8b61ed6... test commit
 * The first activity that will be loadded by the taxi application
 * 
 * in this activity all of the registered taxi plate number will be retrieved from the database
 * and is shown in the screen as a listview for user-friendly login
 */

package abanyu.transphone.taxi.login;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class PlatenumberActivity extends ListActivity{	  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); //removes title bar	
	    
    //site - freetzi.com
    //username - transphone.freetzi.com
    //password - 4graduation
    
    //gets the json encoded data returned by the specified url
    new GetPlateNumberList(this, "http://transphone.freetzi.com/thesis/dbmanager.php?fname=getPlateNumberList").execute();
  }
}
