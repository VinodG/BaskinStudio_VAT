package com.winit.baskinrobbin.salesman.dataaccesslayer;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;

public class DummyDA
{

	public static void updateData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate1 = null;
				stmtUpdate1 = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET Status = 10 where OrderId='1160800242'");
				stmtUpdate1.execute();
				stmtUpdate1.close();
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
}
