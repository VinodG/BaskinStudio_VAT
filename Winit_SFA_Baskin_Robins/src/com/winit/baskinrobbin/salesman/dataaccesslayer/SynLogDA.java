package com.winit.baskinrobbin.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class SynLogDA
{
	public SynLogDO getSynchLog(String entity)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SynLogDO synLogDO = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblSynLog where Entity = '"+entity+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					synLogDO 				= new SynLogDO();
					synLogDO.entity 		= cursor.getString(0);
					synLogDO.action			= cursor.getString(1);
					synLogDO.UPMJ			= cursor.getString(2);
					synLogDO.UPMT			= cursor.getString(3);
					synLogDO.TimeStamp		= cursor.getString(4);
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return synLogDO;
		}
	}
	public boolean insertSynchLog(SynLogDO synLogDO)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSynLog WHERE Entity =?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSynLog(Entity, Action, UPMJ, UPMT, TimeStamp  ) VALUES(?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblSynLog SET Action = ?, UPMJ = ?, UPMT = ?, TimeStamp = ? WHERE Entity = ?");
				
				stmtSelectRec.bindString(1, synLogDO.entity);
				long countRec = stmtSelectRec.simpleQueryForLong();

				if(countRec >0)
				{
					 if(!synLogDO.UPMJ.equalsIgnoreCase("0"))
				     {
						stmtUpdate.bindString(1, synLogDO.action);
						stmtUpdate.bindString(2, synLogDO.UPMJ);
						stmtUpdate.bindString(3, synLogDO.UPMT);
						stmtUpdate.bindString(4, synLogDO.TimeStamp);
						stmtUpdate.bindString(5, synLogDO.entity);
						stmtUpdate.execute();
				     }
				}
				else
				{
					stmtInsert.bindString(1, synLogDO.entity);
					stmtInsert.bindString(2, synLogDO.action);
					stmtInsert.bindString(3, synLogDO.UPMJ);
					stmtInsert.bindString(4, synLogDO.UPMT);
					stmtInsert.bindString(5, synLogDO.TimeStamp);
					stmtInsert.executeInsert();
				}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
			
		}
		
	}
	
	public void clearSynchLog()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{  
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				
				Cursor cursor = objSqliteDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tblSynLog'", null);
				if(cursor.moveToFirst())
				{
					stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSynLog SET UPMJ = '0', UPMT = '0', TimeStamp = ''");
					stmtUpdate.execute();
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(stmtUpdate != null)
					stmtUpdate.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	
/*	public void updateSynchLog(SynLogDO synLogDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{  
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSynLog SET Action = ?, UPMJ = ?, UPMT = ?, TimeStamp = ? WHERE Entity = ?");
				
					stmtUpdate.bindString(1, synLogDO.action);
					stmtUpdate.bindString(2, synLogDO.UPMJ);
					stmtUpdate.bindString(3, synLogDO.UPMT);
					stmtUpdate.bindString(4, synLogDO.TimeStamp);
					stmtUpdate.bindString(5, synLogDO.entity);
					stmtUpdate.execute();
				
				stmtUpdate.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}*/
}
