package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetCustomerDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;

public class AssetCustomerDA {
	
	
	public boolean insertAssetCustomer(Vector<AssetCustomerDo> vecAssetcustomer)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			
			try {
				mDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = mDatabase.compileStatement("SELECT COUNT(*) from tblAssetCustomer where assetCustomerId =?");
				SQLiteStatement stmtInsert = mDatabase.compileStatement("INSERT INTO tblAssetCustomer(assetCustomerId, assetId, siteNo)VALUES(?,?,?)");
				SQLiteStatement stmtUpdate = mDatabase.compileStatement("UPDATE tblAssetCustomer SET assetId = ?, siteNo = ? where  assetCustomerId =?");
				
				if(vecAssetcustomer!=null && vecAssetcustomer.size()>0)
				{
					
					for(AssetCustomerDo objassetcus : vecAssetcustomer)
					{
						
						stmtSelectRec.bindString(1, objassetcus.assetCustomerId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(countRec>0)
						{
							stmtUpdate.bindString(1, objassetcus.assetId);
							stmtUpdate.bindString(2, objassetcus.siteNo);
							stmtUpdate.bindString(3, objassetcus.assetCustomerId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, objassetcus.assetCustomerId);
							stmtInsert.bindString(2, objassetcus.assetId);
							stmtInsert.bindString(3, objassetcus.siteNo);
							stmtInsert.execute();
						}
					}
					
				}
				
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
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
	
	public boolean insertAssetCustomer(AssetCustomerDo objassetcus)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			
			try {
				mDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = mDatabase.compileStatement("SELECT COUNT(*) from tblAssetCustomer where assetCustomerId =?");
				SQLiteStatement stmtInsert = mDatabase.compileStatement("INSERT INTO tblAssetCustomer(assetCustomerId, assetId, siteNo )VALUES(?,?,?)");
				SQLiteStatement stmtUpdate = mDatabase.compileStatement("UPDATE tblAssetCustomer SET assetId = ?, siteNo = ? where  assetCustomerId =?");
				
					
					if(objassetcus !=null)
					{
						
						stmtSelectRec.bindString(1, objassetcus.assetCustomerId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(countRec>0)
						{
							stmtUpdate.bindString(1, objassetcus.assetId);
							stmtUpdate.bindString(2, objassetcus.siteNo);
							stmtUpdate.bindString(3, objassetcus.assetCustomerId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, objassetcus.assetCustomerId);
							stmtInsert.bindString(2, objassetcus.assetId);
							stmtInsert.bindString(3, objassetcus.siteNo);
							stmtInsert.execute();
						}
						
						stmtSelectRec.close();
						stmtUpdate.close();
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
	
	
	
	public Vector<AssetCustomerDo> getAllAssetCustomers()
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetCustomerDo objassetcus = null;
		Vector<AssetCustomerDo> vecAssetCus = new Vector<AssetCustomerDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT * FROM tblAssetCustomer";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					objassetcus = new AssetCustomerDo();
					objassetcus.assetCustomerId = cursor.getString(0);
					objassetcus.assetId 		= cursor.getString(1);
					objassetcus.siteNo 			= cursor.getString(2);
					vecAssetCus.add(objassetcus);
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
		
		return vecAssetCus;
		
	}
	
	public Vector<AssetCustomerDo> getAllAssetCustomers(String assetCustomerId)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetCustomerDo objassetcus = null;
		Vector<AssetCustomerDo> vecAssetCus = new Vector<AssetCustomerDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT * FROM tblAssetCustomer where assetCustomerId = '"+assetCustomerId+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					objassetcus = new AssetCustomerDo();
					objassetcus.assetCustomerId = cursor.getString(0);
					objassetcus.assetId 		= cursor.getString(1);
					objassetcus.siteNo 			= cursor.getString(2);
					vecAssetCus.add(objassetcus);
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
		
		return vecAssetCus;
		
	}
	
	
	public Vector<AssetDO> getAllAssetsByCustomer(String assetCustomerId)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetDO assetDO = null;
		Vector<AssetDO> vecAssetDos = new Vector<AssetDO>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			
			String query = "SELECT A.AssetId,A.BarCode,A.AssetType,A.Name,A.Capacity,A.ImagePath,AC.SiteNo FROM tblAsset A INNER JOIN tblAssetCustomer AC ON AC.AssetId = A.AssetId WHERE AC.SiteNo= '"+assetCustomerId+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					assetDO = new AssetDO();
					assetDO.assetId = cursor.getString(0);
					assetDO.barCode = cursor.getString(1);
					assetDO.assetType= cursor.getString(2);
					assetDO.name 	= cursor.getString(3);
					assetDO.capacity 	= cursor.getString(4);
					assetDO.imagePath 	= cursor.getString(5);
					assetDO.siteNo 	= cursor.getString(6);
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
	public Vector<AssetDO> getAllAssetsRequest()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetDO assetDO = null;
		Vector<AssetDO> vecAssetDos = new Vector<AssetDO>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			
			String query = "SELECT AssetId,BarCode,AssetType,Name,Capacity,ImagePath FROM tblAsset";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					assetDO = new AssetDO();
					assetDO.assetId = cursor.getString(0);
					assetDO.barCode = cursor.getString(1);
					assetDO.assetType= cursor.getString(2);
					assetDO.name 	= cursor.getString(3);
					assetDO.capacity 	= cursor.getString(4);
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

}
