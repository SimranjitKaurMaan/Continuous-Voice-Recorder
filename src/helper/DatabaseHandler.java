package helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import model.Model;
import model.Recordings;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;
import android.content.Context;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	// All Static variables
	private static final String LOG = "DatabaseHandler";
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    public static final String DATABASE_NAME = "RecordingsList";
 
    //table name
    private static final String TABLE_REC = "Recordings";
    

    //Common Table Columns names
    private static final String KEY_ID = "id";
    
    //Table RECORDINGS column names
    private static final String KEY_LOC = "location";
    private static final String KEY_NAME = "name";
    private static final String KEY_LEN = "length";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_SIZE = "size";
    
    
    //Table GROUPING column names
    private static final String KEY_GROUP_NAME = "group_name";
    
    //Table RECORDINGS GROUP column names
    private static final String KEY_REC_ID = "rec_id";
    private static final String KEY_GRP_ID = "grp_id";
    //public SQLiteDatabase db;
    
	
    public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	//Creates table
	@Override
	public void onCreate(SQLiteDatabase db) {
		 
		System.out.println("inside database");
		// TODO Auto-generated method stub
		String CREATE_TABLE1 = "CREATE TABLE "
				+ TABLE_REC + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LOC
				+ " TEXT," + KEY_NAME + " TEXT," + KEY_LEN + " TEXT," + KEY_CREATED_AT
				+ " TEXT," + KEY_SIZE + " TEXT" + ")";
		System.out.println(CREATE_TABLE1);
	    
		
		db.execSQL(CREATE_TABLE1);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REC);
       // Create tables again
        onCreate(db);
	}

	
	
	// Adding new recording
      public long addRec(Recordings rec) {
       SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_LOC, rec.getLoc()); 
        values.put(KEY_NAME, rec.getName()); 
        values.put(KEY_LEN, rec.getLen()); 
        values.put(KEY_CREATED_AT, rec.getDateTime()); 
        values.put(KEY_SIZE, rec.getSize()); 
        // Inserting Row
        long rec_id = db.insert(TABLE_REC, null, values);
        return rec_id;
    }
    
      
    // Getting single recording
    public Recordings getRec(long id) {
       SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_REC + " WHERE "
                + KEY_ID + " = " + id;
 
        Log.e(LOG, selectQuery);
 
        
        Cursor cursor = db.query(TABLE_REC, new String[] { KEY_ID,
                KEY_LOC, KEY_NAME, KEY_LEN, KEY_CREATED_AT, KEY_SIZE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Recordings recording = new Recordings(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), 
                cursor.getString(4),cursor.getString(5));
        
        return recording;
    }
    
    // Getting All Recordings
    public List<Recordings> getAllRec() {
        
    	List<Recordings> rList = new ArrayList<Recordings>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REC;
 
        Log.e(LOG, selectQuery);
       SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Recordings rec = new Recordings();
                rec.setID(Integer.parseInt(cursor.getString(0)));
                rec.setLoc(cursor.getString(1));
                rec.setName(cursor.getString(2));
                rec.setLen(cursor.getString(3));
                rec.setDateTime(cursor.getString(4));
                rec.setSize(cursor.getString(5));
                // Adding contact to list
                rList.add(rec);
            } while (cursor.moveToNext());
        }
        
        // return contact list
        return rList;
    }
    
    
    
    // Deleting single recording
    public void deleteRec(Recordings rec) {
       SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REC, KEY_ID + " = ?",
                new String[] { String.valueOf(rec.getID()) });
        db.close();
    }
    
 
    
    // Getting Count
    public int getRecCount() {
        String countQuery = "SELECT  * FROM " + TABLE_REC;
       SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
    
 // Updating single recording
    public int updateRec(Recordings rec) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_LOC, rec.getLoc());
        values.put(KEY_NAME, rec.getName());
        values.put(KEY_LEN, rec.getLen()); 
        values.put(KEY_CREATED_AT, rec.getDateTime()); 
        values.put(KEY_SIZE, rec.getSize()); 
 
        // updating row
        return db.update(TABLE_REC, values, KEY_ID + " = ?",
                new String[] { String.valueOf(rec.getID()) });
    }

  
    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}


	

