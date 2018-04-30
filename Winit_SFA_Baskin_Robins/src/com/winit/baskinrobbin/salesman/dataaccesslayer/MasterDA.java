package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CountryDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerTypeDO;
import com.winit.baskinrobbin.salesman.dataobject.LandmarkDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.SourceDO;

public class MasterDA 
{
	public boolean inserttblLandMark(Vector<LandmarkDO> vecLandmarkDOs)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblLandMark WHERE LandmarkId =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblLandMark (LandmarkId, LandmarkName, LOCATIONID) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblLandMark set LandmarkName=?, LOCATIONID=? WHERE LandmarkId =?");
			
			for(LandmarkDO landmarkDO :  vecLandmarkDOs)
			{
				stmtSelectRec.bindLong(1, landmarkDO.LandmarkId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					if(landmarkDO != null )
					{
						stmtUpdate.bindString(1, landmarkDO.LandmarkName);
						stmtUpdate.bindString(2, landmarkDO.LOCATIONID);
						stmtUpdate.bindLong(3, landmarkDO.LandmarkId);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(landmarkDO != null )
					{
						stmtInsert.bindLong(1, landmarkDO.LandmarkId);
						stmtInsert.bindString(2, landmarkDO.LandmarkName);
						stmtInsert.bindString(3, landmarkDO.LOCATIONID);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
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
	
	public boolean inserttblCountry(Vector<CountryDO> vecCountryDOs)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCountry WHERE CountryId =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCountry (CountryId, CountryName, CountryDesc) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCountry set CountryName=?, CountryDesc=? WHERE CountryId =?");
			
			for(CountryDO countryDO :  vecCountryDOs)
			{
				stmtSelectRec.bindString(1, countryDO.countryId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					if(countryDO != null )
					{
						stmtUpdate.bindString(1, countryDO.countryName);
						stmtUpdate.bindString(2, countryDO.countryDesc);
						stmtUpdate.bindString(3, countryDO.countryId);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(countryDO != null )
					{
						stmtInsert.bindString(1, countryDO.countryId);
						stmtInsert.bindString(2, countryDO.countryName);
						stmtInsert.bindString(3, countryDO.countryDesc);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
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
	
	public boolean insertttblSource(Vector<SourceDO> veSourceDOs)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSource WHERE Id =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSource (Id, Sourcename) VALUES(?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSource set Sourcename=? WHERE Id =?");
			
			for(SourceDO sourceDO :  veSourceDOs)
			{
				stmtSelectRec.bindLong(1, sourceDO.Id);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					if(sourceDO != null )
					{
						stmtUpdate.bindString(1, sourceDO.sourcename);
						stmtUpdate.bindLong(2, sourceDO.Id);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(sourceDO != null )
					{
						stmtInsert.bindLong(1, sourceDO.Id);
						stmtInsert.bindString(2, sourceDO.sourcename);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
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
	
	public boolean inserttblCustomerType(Vector<CustomerTypeDO> vecCustomerTypeDOs)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerType WHERE CustomerTypeId =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomerType (CustomerTypeId, CustomerTypeName) VALUES(?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCustomerType set CustomerTypeName=? WHERE CustomerTypeId =?");
			
			for(CustomerTypeDO customerTypeDO :  vecCustomerTypeDOs)
			{
				stmtSelectRec.bindString(1, customerTypeDO.customerTypeId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					if(customerTypeDO != null )
					{
						stmtUpdate.bindString(1, customerTypeDO.customerTypeName);
						stmtUpdate.bindString(2, customerTypeDO.customerTypeId);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(customerTypeDO != null )
					{
						stmtInsert.bindString(1, customerTypeDO.customerTypeId);
						stmtInsert.bindString(2, customerTypeDO.customerTypeName);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
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
	
	public  Vector<NameIDDo>  getLandMarks()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select LandmarkId, LandmarkName from tblLandMark", null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo = new NameIDDo();
						nameIDDo.strId= cursor.getString(0);
						nameIDDo.strName= cursor.getString(1);
						vector.add(nameIDDo);
					}while(cursor.moveToNext());
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
				if(mDatabase!=null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public  Vector<NameIDDo> getRegion()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select LocationID, LocationName from tblRegions", null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo = new NameIDDo();
						nameIDDo.strId= cursor.getString(0);
						nameIDDo.strName= cursor.getString(1);
						vector.add(nameIDDo);
					}while(cursor.moveToNext());
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
				if(mDatabase!=null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public  Vector<NameIDDo>  getCountry()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select CountryId, CountryName,CountryDesc from tblCountry", null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo = new NameIDDo();
						nameIDDo.strId= cursor.getString(0);
						nameIDDo.strName= cursor.getString(1);
						nameIDDo.strType= cursor.getString(2);
						vector.add(nameIDDo);
					}while(cursor.moveToNext());
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
				if(mDatabase!=null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public Vector<NameIDDo> getCustomerType()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select CustomerTypeId,CustomerTypeName from tblCustomerType", null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo = new NameIDDo();
						nameIDDo.strId= cursor.getString(0);
						nameIDDo.strName= cursor.getString(1);
						vector.add(nameIDDo);
					}while(cursor.moveToNext());
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
				if(mDatabase!=null)
					mDatabase.close();
			}
			return vector;
		}
	}
	public Vector<NameIDDo> getSource()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select Id, Sourcename from tblSource", null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo = new NameIDDo();
						nameIDDo.strId= cursor.getString(0);
						nameIDDo.strName= cursor.getString(1);
						vector.add(nameIDDo);
					}while(cursor.moveToNext());
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
				if(mDatabase!=null)
					mDatabase.close();
			}
			return vector;
		}
	}
}
