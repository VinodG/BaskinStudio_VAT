package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PresellerPassCodeDO;

public class PasscodesDA
{
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserPresellerPasscode(Vector<PresellerPassCodeDO> vecPresellerPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblVMPassCode (VMPasscodeId, PresellerId, Passcode, IsUsed) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblVMPassCode");
				for(int i = 0; vecPresellerPassCodeDO !=null && i < vecPresellerPassCodeDO.size(); i++)
				{
					PresellerPassCodeDO objPresellerPassCodeDO = vecPresellerPassCodeDO.get(i);
					
					sqLiteStatement.bindLong(1, objPresellerPassCodeDO.presellerPasscodeId);
					sqLiteStatement.bindString(2, objPresellerPassCodeDO.presellerId);
					sqLiteStatement.bindLong(3, objPresellerPassCodeDO.passCode);
					sqLiteStatement.bindString(4, String.valueOf(objPresellerPassCodeDO.isUsed));
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
	
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserDAPasscode(Vector<NameIDDo> vecPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
						 
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblPassCode (EmpId,PassCode,IsUsed,UsedDate) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblPassCode");
				for(int i = 0; vecPassCodeDO !=null && i < vecPassCodeDO.size(); i++)
				{
					NameIDDo objNameIDDo = vecPassCodeDO.get(i);
					
					sqLiteStatement.bindString(1, objNameIDDo.strName);
					sqLiteStatement.bindString(2, objNameIDDo.strType);
					sqLiteStatement.bindString(3, "0");
					sqLiteStatement.bindString(4, "");
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	public boolean validatePassCode(String strEmpNo, String passCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isValid = false;
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase = DatabaseHelper.openDataBase();
					String query = "SELECT IsUsed from tblPassCode where EmpId = '"+strEmpNo+"' AND PassCode = '"+passCode+"'";
					cursor 	=  sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						if(cursor.getInt(0) == 0)
							isValid = true;
						else
							isValid = false;
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isValid;
		}
	}
	
	public void updatePasscode(String passCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblPassCode SET IsUsed=? WHERE PassCode = ?");
				stmtUpdate.bindString(1, "1");
				stmtUpdate.bindString(2, passCode);
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
	}
}
