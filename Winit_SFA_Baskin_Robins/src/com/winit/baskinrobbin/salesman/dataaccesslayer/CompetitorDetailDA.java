package com.winit.baskinrobbin.salesman.dataaccesslayer;


import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CompBrandDO;
import com.winit.baskinrobbin.salesman.dataobject.CompCategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CompDetailDO;


public class CompetitorDetailDA 
{
	public ArrayList<Object> loadData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Vector<CompCategoryDO> vector1 	= new Vector<CompCategoryDO>();
			Vector<CompBrandDO> vector2 		= new Vector<CompBrandDO>();
			ArrayList<Object> arrList = new ArrayList<Object>();
			
			Cursor cursor =null;
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query1 	= 	"SELECT * FROM tblCompetitorCategories WHERE IsActive = 'true' OR IsActive ='1'";
				String query2 	= 	"SELECT * FROM tblCompetitorBrands WHERE IsActive = 'true' OR IsActive ='1'";
				
				//--------------------------------------tblCompetitorCategories-------------------------------------//
				cursor 			= 	sqLiteDatabase.rawQuery(query1, null);
				if(cursor.moveToFirst())
				{
					do
					{
						CompCategoryDO nameIDDo  =  new CompCategoryDO();
						nameIDDo.CategoryId 	 = 	cursor.getString(0);
						nameIDDo.Category 		 = 	cursor.getString(1);
						nameIDDo.IsActive 		 = 	cursor.getString(2);
						vector1.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
		
				arrList.add(vector1);
				
				if(cursor != null && !cursor.isClosed())
						cursor.close();
				
				//--------------------------------------tblCompetitorBrands-------------------------------------//
				cursor 			= 	sqLiteDatabase.rawQuery(query2, null);
				if(cursor.moveToFirst())
				{
					do
					{
						CompBrandDO nameIDDo =  new CompBrandDO();
						nameIDDo.BrandId 	 = 	cursor.getString(0);
						nameIDDo.Brand 		 = 	cursor.getString(1);
						nameIDDo.IsActive 	 = 	cursor.getString(2);
						vector2.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
		
				if(cursor != null && !cursor.isClosed())
						cursor.close();
				
				arrList.add(vector2);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return arrList;
		}
	}

	public boolean insertData(Vector<CompCategoryDO> vecCompCategory, Vector<CompBrandDO> vecBrandDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRecCat 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCompetitorCategories WHERE CategoryId = ?");
				SQLiteStatement stmtInsertCat 	  	= objSqliteDB.compileStatement("INSERT INTO tblCompetitorCategories (CategoryId,Category,IsActive) VALUES(?,?,?)");
				SQLiteStatement stmtUpdateCat   	= objSqliteDB.compileStatement("UPDATE tblCompetitorCategories SET Category = ?, IsActive =? WHERE CategoryId = ?");
				
				SQLiteStatement stmtSelectRecBrand 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCompetitorBrands WHERE BrandId = ?");
				SQLiteStatement stmtInsertBrand 	= objSqliteDB.compileStatement("INSERT INTO tblCompetitorBrands (BrandId,Brand,IsActive) VALUES(?,?,?)");
				SQLiteStatement stmtUpdateBrand    	= objSqliteDB.compileStatement("UPDATE tblCompetitorBrands SET Brand = ?, IsActive =? WHERE BrandId = ?");
				
				if(vecCompCategory != null)
				for(CompCategoryDO compCategoryDO : vecCompCategory)
				{
					stmtSelectRecCat.bindString(1, compCategoryDO.CategoryId);
					long countRec = stmtSelectRecCat.simpleQueryForLong();
					
					if (countRec != 0) 
					{
						stmtUpdateCat.bindString(1, compCategoryDO.Category);
						stmtUpdateCat.bindString(2, compCategoryDO.IsActive);
						stmtUpdateCat.bindString(3, compCategoryDO.CategoryId);
						stmtUpdateCat.execute();
					}
					else
					{
						stmtInsertCat.bindString(1, compCategoryDO.CategoryId);
						stmtInsertCat.bindString(2, compCategoryDO.Category);
						stmtInsertCat.bindString(3, compCategoryDO.IsActive);
						stmtInsertCat.executeInsert();
					}
				}
				
				if(vecBrandDO != null)
				for(CompBrandDO compBrandDO : vecBrandDO)
				{
					stmtSelectRecBrand.bindString(1, compBrandDO.BrandId);
					long countRec = stmtSelectRecBrand.simpleQueryForLong();
					
					if (countRec != 0) 
					{
						stmtUpdateBrand.bindString(1, compBrandDO.Brand);
						stmtUpdateBrand.bindString(2, compBrandDO.IsActive);
						stmtUpdateBrand.bindString(3, compBrandDO.BrandId);
						stmtUpdateBrand.execute();
					}
					else
					{
						stmtInsertBrand.bindString(1, compBrandDO.BrandId);
						stmtInsertBrand.bindString(2, compBrandDO.Brand);
						stmtInsertBrand.bindString(3, compBrandDO.IsActive);
						stmtInsertBrand.executeInsert();
					}
				}
				
				stmtSelectRecCat.close();
				stmtUpdateCat.close();
				stmtInsertCat.close();
				
				stmtSelectRecBrand.close();
				stmtUpdateBrand.close();
				stmtInsertBrand.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
			finally
			{
				if(objSqliteDB != null)
				{
					objSqliteDB.close();
				}
			}
			return true;
		}
	}

	public boolean insertActivity(CompDetailDO compDetailDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB 					= DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsertCat 	= objSqliteDB.compileStatement("INSERT INTO tblCompetitorDetail" +
						" (AppId,BrandId,CategoryId,price,currency,description,empNo,date,status,uplaodStatus)" +
						" VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(compDetailDO != null)
				{
					stmtInsertCat.bindString(1, compDetailDO.appID);
					stmtInsertCat.bindString(2, compDetailDO.BrandId);
					stmtInsertCat.bindString(3, compDetailDO.CategoryId);
					stmtInsertCat.bindString(4, compDetailDO.price);
					stmtInsertCat.bindString(5, compDetailDO.currency);
					stmtInsertCat.bindString(6, compDetailDO.description);
					stmtInsertCat.bindString(7, compDetailDO.empNo);
					stmtInsertCat.bindString(8, compDetailDO.date);
					stmtInsertCat.bindString(9, "0");
					stmtInsertCat.bindString(10,"0");
					stmtInsertCat.executeInsert();
				}
				
				stmtInsertCat.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
			finally
			{
				if(objSqliteDB != null)
				{
					objSqliteDB.close();
				}
			}
			return true;
		}
	}
	
	public Vector<CompDetailDO> getCapturedData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Vector<CompDetailDO> vector		= new Vector<CompDetailDO>();
			Cursor cursor =null;
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query1 	= 	"SELECT  CB.Brand, CC.Category , CD.price,CD.currency,CD.description " +
									"FROM tblCompetitorDetail CD INNER JOIN tblCompetitorCategories CC ON " +
									"CC.CategoryId = CD.CategoryId INNER JOIN tblCompetitorBrands CB " +
									"ON CB.BrandId= CD.BrandId ORDER BY CD.date";
				cursor 			= 	sqLiteDatabase.rawQuery(query1, null);
				if(cursor.moveToFirst())
				{
					do
					{
						CompDetailDO nameIDDo  	=   new CompDetailDO();
						nameIDDo.BrandId 	 	= 	cursor.getString(0);
						nameIDDo.CategoryId 	= 	cursor.getString(1);
						nameIDDo.price 			= 	cursor.getString(2);
						nameIDDo.currency		= 	cursor.getString(3);
						nameIDDo.description	= 	cursor.getString(4);
						vector.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vector;
		}
	}
	
	
	public Vector<CompDetailDO> getCapturedDataToUpload()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Vector<CompDetailDO> vector		= new Vector<CompDetailDO>();
			Cursor cursor =null;
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query1 	= 	"SELECT BrandId,CategoryId,price,currency,description,empNo, AppId FROM tblCompetitorDetail WHERE uplaodStatus = '0'";
				cursor 			= 	sqLiteDatabase.rawQuery(query1, null);
				if(cursor.moveToFirst())
				{
					do
					{
						CompDetailDO nameIDDo  	=   new CompDetailDO();
						nameIDDo.BrandId 	 	= 	cursor.getString(0);
						nameIDDo.CategoryId 	= 	cursor.getString(1);
						nameIDDo.price 			= 	cursor.getString(2);
						nameIDDo.currency		= 	cursor.getString(3);
						nameIDDo.description	= 	cursor.getString(4);
						nameIDDo.empNo			= 	cursor.getString(5);
						nameIDDo.appID			= 	cursor.getString(6);
						vector.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vector;
		}
	}

	public boolean updateStatus(Vector<CompDetailDO> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB 				= DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 	= objSqliteDB.compileStatement("Update tblCompetitorDetail SET uplaodStatus=? WHERE AppId=?");
				
				for(CompDetailDO compDetailDO : vec)
				{
					stmtUpdate.bindString(1, "1");
					stmtUpdate.bindString(2, compDetailDO.appID);
					stmtUpdate.executeInsert();
				}
				stmtUpdate.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return true;
		}
	}
}
