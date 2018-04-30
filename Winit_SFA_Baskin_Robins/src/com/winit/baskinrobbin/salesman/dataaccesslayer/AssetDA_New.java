package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;

public class AssetDA_New {
	
	
	public boolean insertAsset(AssetDo_New objassetAsset,String site,String usercode)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			
			try {
				mDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert = mDatabase.compileStatement("INSERT INTO tblAssetCategory(AssetId,Site, UserCode, AssetName,Level1,Level2,Level3,Level4,Level5,Status )VALUES(?,?,?,?,?,?,?,?,?,?)");
				
					
					if(objassetAsset !=null)
					{
						
							stmtInsert.bindString(1, objassetAsset.assetId);
							stmtInsert.bindString(2, site);
							stmtInsert.bindString(3, usercode);
							stmtInsert.bindString(4, objassetAsset.assetName);
							stmtInsert.bindString(5, objassetAsset.assetCatLevel1);
							stmtInsert.bindString(6, objassetAsset.assetCatLevel2);
							stmtInsert.bindString(7, objassetAsset.assetCatLevel3);
							stmtInsert.bindString(8, objassetAsset.assetCatLevel4);
							stmtInsert.bindString(9, objassetAsset.assetCatLevel5);
							stmtInsert.bindString(10, objassetAsset.status);
							stmtInsert.execute();
						
						stmtInsert.close();
						
					}
					
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally{
				if(mDatabase!=null)
					mDatabase.close();
			}
		}
		return true;
		
	}
	
	
	
	
	public Vector<AssetDo_New> getAllAssetsByCustomer(String usercode,String site)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetDo_New assetDO = null;
		Vector<AssetDo_New> vecAssetDos = new Vector<AssetDo_New>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			
			String query = "SELECT AssetId,AssetName,Level1,Level2,Level3,Level4,Level5,Status FROM tblAssetCategory WHERE Site= '"+site+"' AND UserCode= '"+usercode+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					assetDO = new AssetDo_New();
					assetDO.assetId = cursor.getString(0);
					assetDO.assetName = cursor.getString(1);
					assetDO.assetCatLevel1 = cursor.getString(2);
					assetDO.assetCatLevel2= cursor.getString(3);
					assetDO.assetCatLevel3 	= cursor.getString(4);
					assetDO.assetCatLevel4 	= cursor.getString(5);
					assetDO.assetCatLevel5 	= cursor.getString(6);
					assetDO.status 	= cursor.getString(7);
					vecAssetDos.add(assetDO);
				}while(cursor.moveToNext());
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		
		return vecAssetDos;
		
	}
	
	public boolean updateAssetStatus(String AssetId)
	{
		SQLiteDatabase objSqliteDB = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetCategory SET Status = '1' where AssetId = ?");
			stmtUpdate.bindString(1, ""+AssetId);
			stmtUpdate.execute();
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
	
	
//	public Vector<AssetDO> getAllAssetsRequest()
//	{
//		SQLiteDatabase mDatabase = null;
//		Cursor cursor=null;
//		AssetDO assetDO = null;
//		Vector<AssetDO> vecAssetDos = new Vector<AssetDO>();
//		
//		try {
//			mDatabase = DatabaseHelper.openDataBase();
//			
//			
//			String query = "SELECT AssetId,BarCode,AssetType,Name,Capacity,ImagePath FROM tblAsset";
//			cursor = mDatabase.rawQuery(query, null);
//			
//			if(cursor.moveToFirst())
//			{
//				do
//				{
//					assetDO = new AssetDO();
//					assetDO.assetId = cursor.getString(0);
//					assetDO.barCode = cursor.getString(1);
//					assetDO.assetType= cursor.getString(2);
//					assetDO.name 	= cursor.getString(3);
//					assetDO.capacity 	= cursor.getString(4);
//					vecAssetDos.add(assetDO);
//				}while(cursor.moveToNext());
//				if(cursor!=null && !cursor.isClosed())
//					cursor.close();
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}
//		finally{
//			if(cursor!=null && !cursor.isClosed())
//				cursor.close();
//			if(mDatabase!=null)
//				mDatabase.close();
//		}
//		
//		return vecAssetDos;
//		
//	}

}
