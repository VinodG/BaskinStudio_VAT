package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDo;

public class AssetTrackingDA
{
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertAssetTrackings(Vector<AssetTrackingDo> vecAssetTrackingDos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetTracking WHERE AssetTrackingId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetTracking (AssetTrackingId, UserCode, SiteNo, VisitedCode, JourneyCode, Date , IsUploaded , Status) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetTracking set UserCode = ?, SiteNo = ?, VisitedCode = ?, JourneyCode = ?, Date = ?, IsUploaded = ? , Status = ? where AssetTrackingId =?");
				
				if(vecAssetTrackingDos != null && vecAssetTrackingDos.size() > 0)
				{
					for (AssetTrackingDo assetTrackingDo : vecAssetTrackingDos) 
					{
						if(assetTrackingDo!=null)
						{
							stmtSelectRec.bindString(1, assetTrackingDo.assetTrackingId);
							long countRec = stmtSelectRec.simpleQueryForLong();
							if(countRec >0)
							{
								stmtUpdate.bindString(1,assetTrackingDo.userCode);
								stmtUpdate.bindString(2,assetTrackingDo.siteNo);
								stmtUpdate.bindString(3,assetTrackingDo.visitedCode);
								stmtUpdate.bindString(4,assetTrackingDo.journeyCode);
								stmtUpdate.bindString(5,assetTrackingDo.date);
								stmtUpdate.bindString(6,assetTrackingDo.isUploaded);
								stmtUpdate.bindString(7,assetTrackingDo.status);
								stmtUpdate.bindString(8,assetTrackingDo.assetTrackingId);
								
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,assetTrackingDo.assetTrackingId);
								stmtInsert.bindString(2,assetTrackingDo.userCode);
								stmtInsert.bindString(3,assetTrackingDo.siteNo);
								stmtInsert.bindString(4,assetTrackingDo.visitedCode);
								stmtInsert.bindString(5,assetTrackingDo.journeyCode);
								stmtInsert.bindString(6,assetTrackingDo.date);
								stmtInsert.bindString(7,assetTrackingDo.isUploaded);
								stmtInsert.bindString(8,assetTrackingDo.status);
								
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
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
	
	public void insertAssetTracking(AssetTrackingDo	assetTrackingDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetTracking WHERE AssetTrackingId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetTracking (AssetTrackingId, UserCode, SiteNo, VisitedCode, JourneyCode, Date , IsUploaded , Status) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetTracking set UserCode = ?, SiteNo = ?, VisitedCode = ?, JourneyCode = ?, Date = ?, IsUploaded = ? , Status = ? where AssetTrackingId =?");
				
				if(assetTrackingDo!=null)
				{
					stmtSelectRec.bindString(1, assetTrackingDo.assetTrackingId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec >0)
					{
						stmtUpdate.bindString(1,assetTrackingDo.userCode);
						stmtUpdate.bindString(2,assetTrackingDo.siteNo);
						stmtUpdate.bindString(3,assetTrackingDo.visitedCode);
						stmtUpdate.bindString(4,assetTrackingDo.journeyCode);
						stmtUpdate.bindString(5,assetTrackingDo.date);
						stmtUpdate.bindString(6,assetTrackingDo.isUploaded);
						stmtUpdate.bindString(7,assetTrackingDo.status);
						stmtUpdate.bindString(8,assetTrackingDo.assetTrackingId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1,assetTrackingDo.assetTrackingId);
						stmtInsert.bindString(2,assetTrackingDo.userCode);
						stmtInsert.bindString(3,assetTrackingDo.siteNo);
						stmtInsert.bindString(4,assetTrackingDo.visitedCode);
						stmtInsert.bindString(5,assetTrackingDo.journeyCode);
						stmtInsert.bindString(6,assetTrackingDo.date);
						stmtInsert.bindString(7,assetTrackingDo.isUploaded);
						stmtInsert.bindString(8,assetTrackingDo.status);
						
						stmtInsert.executeInsert();
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
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
	
	public Vector<AssetTrackingDo> getAllAssetTrackings()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<AssetTrackingDo> vecAssetTrackingDos = new Vector<AssetTrackingDo>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTracking where IsUploaded = '0'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						AssetTrackingDo assetTrackingDo  = new AssetTrackingDo();
						assetTrackingDo.assetTrackingId	 = cursor.getString(0);
						assetTrackingDo.userCode		 = cursor.getString(1);
						assetTrackingDo.siteNo		   	 = cursor.getString(2);
						assetTrackingDo.visitedCode	 	 = cursor.getString(3);
						assetTrackingDo.journeyCode	 	 = cursor.getString(4);
						assetTrackingDo.date			 = cursor.getString(5);
						assetTrackingDo.imagepath		 = cursor.getString(8);
						assetTrackingDo.tempimagepath	 = cursor.getString(9);
						assetTrackingDo.temperature	 = cursor.getString(10);
						assetTrackingDo.vAssetTrackingDetailDos = new AssetTrackingDetailDA().getAllAssetTrackingDetailsByTrackinId(assetTrackingDo.assetTrackingId);
						vecAssetTrackingDos.add(assetTrackingDo);
					} while (cursor.moveToNext());
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
			return vecAssetTrackingDos;
		}
	}
	
	public AssetTrackingDo getAssetTracking(String assetTypeId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetTrackingDo assetTrackingDo = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTracking where AssetTypeId = '"+assetTypeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					assetTrackingDo = new AssetTrackingDo();
					assetTrackingDo.assetTrackingId		= cursor.getString(0);
					assetTrackingDo.userCode			= cursor.getString(1);
					assetTrackingDo.siteNo		= cursor.getString(2);
					assetTrackingDo.visitedCode	 		= cursor.getString(3);
					assetTrackingDo.journeyCode	 		= cursor.getString(4);
					assetTrackingDo.date		= cursor.getString(5);
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
			return assetTrackingDo;
		}
	}
	
	
	public String getAssetSite(String userCode)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			String siteno = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select SiteNo from tblAssetTracking where UserCode = '"+userCode+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					
					siteno	= cursor.getString(0);
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
			return siteno;
		}
	}
	
	public AssetTrackingDo getAssetTracking(String siteNo , String date)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetTrackingDo assetTrackingDo = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTracking where SiteNo = '"+siteNo+"' AND Date = '"+date+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					assetTrackingDo = new AssetTrackingDo();
					assetTrackingDo.assetTrackingId		= cursor.getString(0);
					assetTrackingDo.userCode			= cursor.getString(1);
					assetTrackingDo.siteNo		= cursor.getString(2);
					assetTrackingDo.visitedCode	 		= cursor.getString(3);
					assetTrackingDo.journeyCode	 		= cursor.getString(4);
					assetTrackingDo.date		= cursor.getString(5);
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
			return assetTrackingDo;
		}
	}
	
	public void updateTrackingIsUpload(String assetTrackingId, String isUploaded)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetTracking SET IsUploaded = ? WHERE AssetTrackingId = ?");
				
				stmtUpdate.bindString(1, isUploaded);
				stmtUpdate.bindString(2, assetTrackingId);
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
	
	public void updateTrackings(Vector<AssetTrackingDo> vecAssetTrackingDos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetTracking SET AssetTrackingId = ?, IsUploaded = ?, Status = ? WHERE AssetTrackingId = ?");
				
				for (AssetTrackingDo assetTrackingDo : vecAssetTrackingDos) 
				{
					stmtUpdate.bindString(1, assetTrackingDo.assetTrackingId);
					stmtUpdate.bindString(2, "1");
					stmtUpdate.bindString(3, assetTrackingDo.status);
					stmtUpdate.bindString(4, assetTrackingDo.assetTrackingAppId);
					stmtUpdate.execute();
					new AssetTrackingDetailDA().updateTrackingIsUpload(assetTrackingDo.vAssetTrackingDetailDos,assetTrackingDo.assetTrackingId);
				}
				
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
	
	public void updateAssetPlanogramImage(String siteNo, String imagePath)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetTracking SET ImagePath = ? WHERE SiteNo = '"+siteNo+"' ");
				
				stmtUpdate.bindString(1, imagePath);
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
	
	public void updateAssetTemparatureImage(String siteNo, String imagePath, String assetTemp)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetTracking SET TempImagePath = ?,Temparature = ? WHERE SiteNo = '"+siteNo+"' ");
				
				stmtUpdate.bindString(1, imagePath);
				stmtUpdate.bindString(2, assetTemp);
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
