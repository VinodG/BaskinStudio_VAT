package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SettingsDO;

public class SettingsDA 
{
	public boolean insertAllSettings(Vector<SettingsDO> vecSettings)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
//			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSettings WHERE SettingName =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSettings (SettingId, SettingName, SettingValue, DataType, CountryId) VALUES(?,?,?,?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSettings set SettingValue=?, DataType=?,CountryId=? WHERE SettingName =?");
			
			for(SettingsDO settingsDO :  vecSettings)
			{
//				stmtSelectRec.bindString(1, settingsDO.SettingName);
//				long countRec = stmtSelectRec.simpleQueryForLong();
//				if(countRec != 0)
//				{	
					stmtUpdate.bindString(1, ""+settingsDO.SettingValue);
					stmtUpdate.bindString(2, ""+settingsDO.DataType);
					stmtUpdate.bindString(3, ""+settingsDO.CountryId);
					stmtUpdate.bindString(4, ""+settingsDO.SettingName);
					stmtUpdate.execute();
//				}
//				else
				if(stmtUpdate.executeUpdateDelete()<=0)
				{
					stmtInsert.bindString(1, ""+settingsDO.SettingId);
					stmtInsert.bindString(2, ""+settingsDO.SettingName);
					stmtInsert.bindString(3, ""+settingsDO.SettingValue);
					stmtInsert.bindString(4, ""+settingsDO.DataType);
					stmtInsert.bindString(5, ""+settingsDO.CountryId);
					stmtInsert.executeInsert();
				}
			}
			
//			stmtSelectRec.close();
			stmtInsert.close();
			stmtUpdate.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
		}
	}
	//======================Added On 11th DEC 2017

	public SettingsDO getSettingValueByName(String settingId)
	{
		synchronized(MyApplication.MyLock)  {

			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SettingsDO settingsDO = null;
			try {

				mDatabase = DatabaseHelper.openDataBase();

				String query = "SELECT * FROM tblSettings where SettingName = '"+settingId+"'";
				cursor = mDatabase.rawQuery(query, null);

				if(cursor.moveToFirst())
				{
					settingsDO = new SettingsDO();
					settingsDO.SettingId 	 = cursor.getString(0);
					settingsDO.SettingName   = cursor.getString(1);
					settingsDO.SettingValue	 = cursor.getString(2);
					settingsDO.DataType      = cursor.getString(3);
					settingsDO.SalesOrgCode  = cursor.getString(4);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}

			return settingsDO;
		}

	}

	public String getSettingByName(String settingName)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase =  null;
			Cursor cursor            =  null;
			String name        	 =  "";
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT SettingValue FROM tblSettings WHERE SettingName = '"+settingName+"'", null);
				
				if(cursor.moveToFirst())
					name = cursor.getString(0);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return name;
		}
	}
	public int getSettingsByName(String settingName)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase =  null;
			Cursor cursor            =  null;
			int settingVal        	 =  AppStatus.NOT_AVAIL;
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT SettingValue FROM tblSettings WHERE SettingName = '"+settingName+"'", null);
				
				if(cursor.moveToFirst())
					settingVal = cursor.getInt(0);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return settingVal;
		}
	}
	public boolean getSettingsValue(String settingName)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase =  null;
			Cursor cursor            =  null;
			boolean settingVal        	 =  false;
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT SettingValue FROM tblSettings WHERE SettingName = '"+settingName+"'", null);
				
				if(cursor.moveToFirst())
			      if(cursor.getInt(0)==1)
			    	  settingVal=true;
			      else 
			    	  settingVal=false;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return settingVal;
		}
	}

	public boolean getSettingsValue(String settingName,SQLiteDatabase mDatabase)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor            =  null;
			boolean settingVal        	 =  false;
			
			try 
			{
				if(mDatabase == null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT SettingValue FROM tblSettings WHERE SettingName = '"+settingName+"'", null);
				
				if(cursor.moveToFirst())
					if(cursor.getInt(0)==1)
						settingVal=true;
					else 
						settingVal=false;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return settingVal;
		}
	}
	public String getSettingTRN(String settingName)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase1 =  null;
			Cursor cursor            =  null;
			String settingVal        	 =  "";

			try
			{
				mDatabase1 = DatabaseHelper.openDataBase();
				cursor  = mDatabase1.rawQuery("SELECT SettingValue FROM tblSettings WHERE SettingName = '"+settingName+"'", null);

				if(cursor!=null && cursor.moveToFirst())
					settingVal=cursor.getString(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return settingVal;
		}
	}

	
}
