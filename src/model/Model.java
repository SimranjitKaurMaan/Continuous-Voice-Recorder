package model;

import java.util.List;

import com.vbsglobal.cvr.RecorderFragment;

import helper.DatabaseHandler;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Model {
	 private static Model m=null;
	 DatabaseHandler db=null;
	private List<Recordings> rList;
	 
	 public Model(Context context) {
		
		 }
	public static Model getInstance(Context context) {
	     if(m == null ) {
	        m = new Model(context);
	     }
	     Log.d("m",""+m);
	     return m;
	  }
	

	public DatabaseHandler getDatabaseObj(Context context){
		if(db==null)
		db = new DatabaseHandler(context);
		return db;
		
	}

	

public List<Recordings> getList(Context context){
	db=getDatabaseObj(context);
	rList=db.getAllRec();
	Log.d("PlayerList ",""+rList);
	 return rList;
		
	}






}

