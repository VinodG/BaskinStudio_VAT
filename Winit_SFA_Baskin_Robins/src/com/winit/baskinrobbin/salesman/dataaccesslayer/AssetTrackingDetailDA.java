package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDetailDo;

public class AssetTrackingDetailDA
{
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertAssetTrackingDetails(Vector<AssetTrackingDetailDo> vecAssetTrackingDetailDos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetTrackingDetail WHERE AssetTrackingId =? AND barCode = ?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetTrackingDetail (AssetTrackingDetailId, barCode, ScanningTime, AssetTrackingId, IsUploaded, Status,AssetId,ImagePath) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetTrackingDetail set  ScanningTime = ?,  IsUploaded = ? , Status = ?, AssetId = ?, ImagePath =?  where AssetTrackingId =? AND barCode = ?");
				
				if(vecAssetTrackingDetailDos != null && vecAssetTrackingDetailDos.size() > 0)
				{
					for (AssetTrackingDetailDo assetTrackingDetailDo : vecAssetTrackingDetailDos) 
					{
						if(assetTrackingDetailDo!=null)
						{
							stmtSelectRec.bindString(1, assetTrackingDetailDo.assetTrackingDetailId);
							long countRec = stmtSelectRec.simpleQueryForLong();
							if(countRec >0)
							{
								stmtUpdate.bindString(1,assetTrackingDetailDo.scanningTime);
								stmtUpdate.bindString(2,assetTrackingDetailDo.isUploaded);
								stmtUpdate.bindString(3,assetTrackingDetailDo.status);
								stmtUpdate.bindString(4,assetTrackingDetailDo.assetId);
								stmtUpdate.bindString(5,assetTrackingDetailDo.imagepath);
								stmtUpdate.bindString(6,assetTrackingDetailDo.assetTrackingId);
								stmtUpdate.bindString(7,assetTrackingDetailDo.barCode);
								
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,assetTrackingDetailDo.assetTrackingDetailId);
								stmtInsert.bindString(2,assetTrackingDetailDo.barCode);
								stmtInsert.bindString(3,assetTrackingDetailDo.scanningTime);
								stmtInsert.bindString(4,assetTrackingDetailDo.assetTrackingId);
								stmtInsert.bindString(5,assetTrackingDetailDo.isUploaded);
								stmtInsert.bindString(6,assetTrackingDetailDo.status);
								stmtInsert.bindString(7,assetTrackingDetailDo.assetId);
								stmtInsert.bindString(8,assetTrackingDetailDo.imagepath);
								
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
	
	public void insertAssetTrackingDetail(AssetTrackingDetailDo assetTrackingDetailDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetTrackingDetail WHERE AssetTrackingId =? AND barCode = ?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetTrackingDetail (AssetTrackingDetailId, barCode, ScanningTime, AssetTrackingId, IsUploaded, Status,AssetId,ImagePath) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetTrackingDetail set  ScanningTime = ?,  IsUploaded = ? , Status = ? , AssetId = ?, ImagePath =?  where AssetTrackingId =? AND barCode = ?");
				
				if(assetTrackingDetailDo!=null)
				{
					stmtSelectRec.bindString(1, assetTrackingDetailDo.assetTrackingId);
					stmtSelectRec.bindString(2, assetTrackingDetailDo.barCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec >0)
					{
						stmtUpdate.bindString(1,assetTrackingDetailDo.scanningTime);
						stmtUpdate.bindString(2,assetTrackingDetailDo.isUploaded);
						stmtUpdate.bindString(3,assetTrackingDetailDo.status);
						stmtUpdate.bindString(4,assetTrackingDetailDo.assetId);
						stmtUpdate.bindString(5,assetTrackingDetailDo.imagepath);
						stmtUpdate.bindString(6,assetTrackingDetailDo.assetTrackingId);
						stmtUpdate.bindString(7,assetTrackingDetailDo.barCode);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1,assetTrackingDetailDo.assetTrackingDetailId);
						stmtInsert.bindString(2,assetTrackingDetailDo.barCode);
						stmtInsert.bindString(3,assetTrackingDetailDo.scanningTime);
						stmtInsert.bindString(4,assetTrackingDetailDo.assetTrackingId);
						stmtInsert.bindString(5,assetTrackingDetailDo.isUploaded);
						stmtInsert.bindString(6,assetTrackingDetailDo.status);
						stmtInsert.bindString(7,assetTrackingDetailDo.assetId);
						stmtInsert.bindString(8,assetTrackingDetailDo.imagepath);
						
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
	
	public Vector<AssetTrackingDetailDo> getAllAssetTrackingDetails()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<AssetTrackingDetailDo> vecAssetTrackingDetailDos = new Vector<AssetTrackingDetailDo>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTrackingDetail";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						AssetTrackingDetailDo assetTrackingDetailDo = new AssetTrackingDetailDo();
						assetTrackingDetailDo.assetTrackingDetailId		= cursor.getString(0);
						assetTrackingDetailDo.barCode			= cursor.getString(1);
						assetTrackingDetailDo.scanningTime		= cursor.getString(2);
						assetTrackingDetailDo.assetTrackingId	 		= cursor.getString(3);
						assetTrackingDetailDo.assetId	= cursor.getString(6);
						assetTrackingDetailDo.imagepath	= cursor.getString(7);
						vecAssetTrackingDetailDos.add(assetTrackingDetailDo);
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
			return vecAssetTrackingDetailDos;
		}
	}
	
	public Vector<AssetTrackingDetailDo> getAllAssetTrackingDetailsByTrackinId(String assetTrackingId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<AssetTrackingDetailDo> vecAssetTrackingDetailDos = new Vector<AssetTrackingDetailDo>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTrackingDetail where AssetTrackingId = '"+assetTrackingId+"' AND IsUploaded = '0'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						AssetTrackingDetailDo assetTrackingDetailDo = new AssetTrackingDetailDo();
						assetTrackingDetailDo.assetTrackingDetailId		= cursor.getString(0);
						assetTrackingDetailDo.barCode			= cursor.getString(1);
						assetTrackingDetailDo.scanningTime		= cursor.getString(2);
						assetTrackingDetailDo.assetTrackingId	 		= cursor.getString(3);
						assetTrackingDetailDo.assetId	= cursor.getString(6);
						assetTrackingDetailDo.imagepath	= cursor.getString(7);
						vecAssetTrackingDetailDos.add(assetTrackingDetailDo);
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
			return vecAssetTrackingDetailDos;
		}
	}
	
	public AssetTrackingDetailDo getAssetTrackingDetail(String assetTypeId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetTrackingDetailDo assetTrackingDetailDo = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetTrackingDetail where AssetTrackingDetailId = '"+assetTypeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
						assetTrackingDetailDo = new AssetTrackingDetailDo();
						assetTrackingDetailDo.assetTrackingDetailId		= cursor.getString(0);
						assetTrackingDetailDo.barCode			= cursor.getString(1);
						assetTrackingDetailDo.scanningTime		= cursor.getString(2);
						assetTrackingDetailDo.assetTrackingId	 		= cursor.getString(3);
						assetTrackingDetailDo.assetId	= cursor.getString(6);
						assetTrackingDetailDo.imagepath	= cursor.getString(7);
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
			return assetTrackingDetailDo;
		}
	}
	
	public void updateTrackingIsUpload(Vector<AssetTrackingDetailDo> vecAssetTrackingDetailDos , String assetTrackingId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{  
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetTrackingDetail SET AssetTrackingDetailId = ?, AssetTrackingId = ?, IsUploaded = ?, Status = ? WHERE AssetTrackingDetailId = ?");
				
				for (AssetTrackingDetailDo assetTrackingDetailDo : vecAssetTrackingDetailDos) 
				{
					stmtUpdate.bindString(1, assetTrackingDetailDo.assetTrackingDetailId);
					stmtUpdate.bindString(2, assetTrackingId);
					stmtUpdate.bindString(3, "1");
					stmtUpdate.bindString(4, assetTrackingDetailDo.status);
					stmtUpdate.bindString(5, assetTrackingDetailDo.assetTrackingDetailAppId);
					stmtUpdate.execute();
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
}
