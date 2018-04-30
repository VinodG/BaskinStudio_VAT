package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.TaskToDoDO;

public class TaskToDoDA {
	
	public boolean insertTaskToDo(Vector<TaskToDoDO> vecToDoDOs) 
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase objSqliteDB = null;
			try {
				objSqliteDB = DatabaseHelper.openDataBase();

				SQLiteStatement stmtInsert = objSqliteDB
						.compileStatement("INSERT INTO tblTaskToDo (taskId, taskName, taskDesc, taskDate, routeCustomerID, Status, IsAcknowledge, AcknowledgeOn, Rating, SITE) VALUES(?,?,?,?,?,?,?,?,?,?)");

				if(vecToDoDOs != null && vecToDoDOs.size()>0)
				{
					for (TaskToDoDO taskToDoDO : vecToDoDOs)
					{
						stmtInsert.bindString(1, taskToDoDO.TaskID);
						stmtInsert.bindString(2, taskToDoDO.TaskName);
						stmtInsert.bindString(3, taskToDoDO.TaskDesc);
						stmtInsert.bindString(4, taskToDoDO.TaskDate);
						stmtInsert.bindString(5, taskToDoDO.routeCustomerID);
						stmtInsert.bindString(6, taskToDoDO.Status);
						stmtInsert.bindString(7, taskToDoDO.IsAcknowledge);
						stmtInsert.bindString(8, taskToDoDO.AcknowledgeOn);
						stmtInsert.bindString(9, taskToDoDO.Rating);
						stmtInsert.bindString(10, taskToDoDO.SITE);
						stmtInsert.executeInsert();
						
					}
				}
				stmtInsert.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (objSqliteDB != null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	
	public Vector<TaskToDoDO> getTaskToDO(String customerSiteID, String strDate) 
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			TaskToDoDO obj = null;
			Vector<TaskToDoDO> vecTaskToDoDOs = new Vector<TaskToDoDO>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblTaskToDo"; /* where SITE = '"+customerSiteID+"'";and taskDate like '%"+strDate+"%'";
*/				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst())
				{
					do {
						obj = new TaskToDoDO();
						obj.TaskID 	 		= cursor.getString(0);
						obj.TaskName 		= cursor.getString(1);
						obj.TaskDesc 		= cursor.getString(2);
						obj.TaskDate		= cursor.getString(3);
						obj.routeCustomerID = cursor.getString(4);
						obj.Status			= cursor.getString(5);
						obj.IsAcknowledge	= cursor.getString(6);
						obj.AcknowledgeOn   = cursor.getString(7);
						obj.Rating		  	= cursor.getString(8);
						obj.SITE			= cursor.getString(9);
						vecTaskToDoDOs.add(obj);
					} while (cursor.moveToNext());
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return vecTaskToDoDOs;
		}
	}
	
	public int getServedTask(String customerSiteID, String strDate) 
	{
		synchronized (MyApplication.MyLock) {
			Cursor cursor = null;
			int count = 0;
			SQLiteDatabase mDatabase ;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select COUNT(*) from tblTaskToDo where SITE = '"+customerSiteID+"' and taskDate like '%"+strDate+"%' and Status = 'C'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst())
				{
					count = cursor.getInt(0);
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			}
			return count;
		}
	}
}
