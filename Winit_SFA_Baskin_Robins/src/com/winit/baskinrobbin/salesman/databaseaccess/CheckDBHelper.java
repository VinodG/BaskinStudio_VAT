package com.winit.baskinrobbin.salesman.databaseaccess;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.winit.baskinrobbin.salesman.common.AppConstants;

/***  Description of class : This class having Database creation using SQLiteOpenHelper. ***/
public class CheckDBHelper extends SQLiteOpenHelper 
{
	private SQLiteDatabase _database; 
    private final Context myContext;
	private String path;
	
	public CheckDBHelper(Context context, String path) 
    {	 
    	super(context, path, null, 1);
        this.myContext 	= 	context;
        this.path 		= 	path;
    }
    
    //To open the database
    public  SQLiteDatabase openDataBase() throws SQLException
    {	
    	try
    	{
	    	//Open the database
	    	if(_database == null)    
	    	{
	    		_database = SQLiteDatabase.openDatabase(path + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
	    				| SQLiteDatabase.CREATE_IF_NECESSARY);
	    	}
	    	else if(!_database.isOpen())
	    	{
	    		_database = SQLiteDatabase.openDatabase(path + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
	                    | SQLiteDatabase.CREATE_IF_NECESSARY);
	    	}
	    	else
	    	{
	    		if(_database != null && _database.isOpen())
	    			_database.close();
	    		
	    		_database = SQLiteDatabase.openDatabase(path + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
	                    | SQLiteDatabase.CREATE_IF_NECESSARY);
	    	}
	    	
	    	return _database;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return _database;
    	}
    }
    
    //To close database
    public void closedatabase() 
    { 
	    if(_database != null)
		    _database.close(); 
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{		
	}
}
