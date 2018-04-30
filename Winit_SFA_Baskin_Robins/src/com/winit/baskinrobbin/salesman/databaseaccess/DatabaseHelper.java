package com.winit.baskinrobbin.salesman.databaseaccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;

/**
 * 
 *  Description of class : This class having Database creation using SQLiteOpenHelper. 
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper 
{
	
    public static SQLiteDatabase _database; 
    
    private final Context myContext;
    public static String apstorphe = "'";
	public static String sep = ",";	
	
	public DatabaseHelper(Context context) 
    {	 
    	super(context, AppConstants.DATABASE_NAME, null, 1);
        this.myContext = context;
    }
	
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws Exception
    {
       	boolean dbExist = checkDataBase();
 
    	if(!dbExist)
    	{
    		//By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
    		try
    		{
    			copyDataBase();
    		} 
    		catch (Exception e) 
    		{	 
        		throw e;
         	}
    	} 
    }
    
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase()
    {	 
    	SQLiteDatabase checkDB = null;
    	try
    	{
    		String myPath = AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE); 
    	}
    	catch(SQLiteException e)
    	{
    		//database does't exist yet.	 
    	}
    	if(checkDB != null)
    	{	 
    		checkDB.close();	 
    	}
 
    	return checkDB != null ? true : false;
    }
    
    /**
     * To Copy the database
     * @throws IOException
     */
    public void copyDataBase() throws IOException
    {
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(AppConstants.DATABASE_NAME);
 
    	// Path to the just created empty db
    	String outFileName = AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[2048];
    	int length;
    	while ((length = myInput.read(buffer))>0)
    	{
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
    
    //To open the database
    public static SQLiteDatabase openDataBase() throws SQLException
    {	
    	try
    	{
	    	//Open the database
    		if(MyApplication.mContext!=null && TextUtils.isEmpty(AppConstants.DATABASE_PATH))
    			AppConstants.DATABASE_PATH = MyApplication.mContext.getFilesDir().toString() + "/";

	    	if(_database == null)    
	    	{
	    		_database = SQLiteDatabase.openDatabase(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
	    				| SQLiteDatabase.CREATE_IF_NECESSARY);
	    	}
	    	else if(!_database.isOpen())
	    	{
	    		_database = SQLiteDatabase.openDatabase(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
	                    | SQLiteDatabase.CREATE_IF_NECESSARY);
	    	}
	    	else
	    	{
	    		if(_database != null && _database.isOpen())
	    			_database.close();
	    		
	    		_database = SQLiteDatabase.openDatabase(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
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
    public static void closedatabase() 
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

	public static synchronized DictionaryEntry[][] get(String query_str)
	{
		DictionaryEntry dir = null;
		String[] columns;
		int index;
		int rowIndex = 0;
		DictionaryEntry[] row_obj = null; //An array of columns and their values
		DictionaryEntry[][] data_arr = null;
		Cursor c = null;	
		
		openDataBase();
		
		if(_database != null)
		{
			try 
			{
				c = _database.rawQuery(query_str, null);
				if(c.moveToFirst())
				{
					rowIndex = 0;
					data_arr = new DictionaryEntry[c.getCount()][];
					do
					{
						columns = c.getColumnNames();
						row_obj = new DictionaryEntry[columns.length]; //(columns.length);
						for(int i=0; i<columns.length; i++)
						{
							dir = new DictionaryEntry(); 							
							dir.key = columns[i];
							index = c.getColumnIndex(dir.key);
							if(dir.key.equals("barcode") ||dir.key.equals("ImageLarge"))
							{
								dir.value = c.getBlob(index);
							}
							else if(dir.key.equals("BalanceAmount") ||dir.key.equals("TotalAmount"))
							{
								dir.value = c.getFloat(index);
							}
							else
								dir.value = c.getString(index);
							row_obj[i] = dir;
						}
						data_arr[rowIndex] = row_obj;
						rowIndex++;
					}
					while(c.moveToNext());
				}
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				closedatabase();
			}
		}
		return data_arr;
	}

	
	public static boolean deleteDir(File dir) 
	{ 
		if (dir.isDirectory()) 
		{ 
			String[] children = dir.list(); 
			for (int i=0; i<children.length; i++) 
			{ 
				boolean success = deleteDir(new File(dir, children[i])); 
				if (!success) 
				{ 
					return false; 
				} 
			} 
		} 
		// The directory is now empty so delete it  
		return dir.delete();
	}
	
	public boolean Load()
    {
        boolean installed = true;
        try
        {
            InputStream is = myContext.openFileInput(AppConstants.DATABASE_NAME);
            is.close();
        }
        catch (FileNotFoundException e) 
        {
        	e.printStackTrace();
            installed = false;
        }
        catch (IOException e) 
        {
            installed = false;
        }
        
        
        ////////////Need to comment this code////////////////
 //       installed = false;
       /////////////////////////////
        
        if(!installed)
        {
        	//If database not copyied from assets
            try
            {
            	boolean isFound = false;
            	int i=0;
            	String str = "";
            	ZipFile zip = null;
            	ZipEntry zipen = null;
            	while(!isFound)
            	{
            		try
            		{
            			if(i == 0)
            			{
            				str = "";
            			}
            			else
            			{
            				str = "-"+i;
            			}
            			zip = new ZipFile("/data/app/com.winit.baskinrobbin.salesman"+str+".apk");
            			zipen = zip.getEntry("assets/" + AppConstants.DATABASE_NAME);
            			isFound = true;
            		}
                    catch(Exception e)
                    {
                    	isFound = false;
                    }
                    ++i;
            	}
            	
                InputStream is = zip.getInputStream(zipen);
                OutputStream os = null;
                
                os = myContext.openFileOutput(AppConstants.DATABASE_NAME, Context.MODE_WORLD_READABLE);
                
                int len;
                byte[] buffer = new byte[4096];
                while ((len = is.read(buffer)) >= 0)
                {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                  
                openDataBase();
                installed = true;
            }
            catch (Exception e) 
            {
            	e.printStackTrace();
            }
        }
        else
        {
        	openDataBase();
        }
        return installed;
    }
}
