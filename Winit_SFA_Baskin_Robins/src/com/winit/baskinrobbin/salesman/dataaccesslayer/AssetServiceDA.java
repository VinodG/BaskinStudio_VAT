package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetServiceDO;

public class AssetServiceDA
{

	/**
	 * Method to Insert the AssetService information in to Asset Table
	 * @param Assetinfo
	 */
	
	public boolean insertAssetService(Vector<AssetServiceDO> vecAssestserdo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetServiceRequest WHERE AssetServiceRequestId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetServiceRequest(AssetServiceRequestId, UserCode, SiteNo, RequestDate, RequestImage, Status , Notes, IsApproved, ServiceRequestAppId,JourneyCode,VisitCode) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetServiceRequest SET UserCode = ?, SiteNo = ?, RequestDate = ?, RequestImage = ?, Status = ?, Notes = ?, IsApproved = ?, ServiceRequestAppId = ?, JourneyCode = ?, VisitCode = ? where AssetServiceRequestId= ?");
				
				if(vecAssestserdo!=null && vecAssestserdo.size()>0)
				{
					
					for(AssetServiceDO assestserdo : vecAssestserdo)
					{
						stmtSelectRec.bindString(1, assestserdo.assetServiceRequestId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, assestserdo.userCode);
							stmtUpdate.bindString(2, assestserdo.siteNo);
							stmtUpdate.bindString(3, assestserdo.requestDate);
							stmtUpdate.bindString(4, assestserdo.multipart_Url);
							stmtUpdate.bindString(5, assestserdo.status);
							stmtUpdate.bindString(6, assestserdo.notes);
							stmtUpdate.bindString(7, assestserdo.isApproved);
							stmtUpdate.bindString(8, assestserdo.serviceRequestAppId);
							stmtUpdate.bindString(9, assestserdo.journeyCode);
							stmtUpdate.bindString(10, assestserdo.visitCode);
							stmtUpdate.bindString(11, assestserdo.assetServiceRequestId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, assestserdo.assetServiceRequestId);
							stmtInsert.bindString(2, assestserdo.userCode);
							stmtInsert.bindString(3, assestserdo.siteNo);
							stmtInsert.bindString(4, assestserdo.requestDate);
							stmtInsert.bindString(5, assestserdo.multipart_Url);
							stmtInsert.bindString(6, assestserdo.status);
							stmtInsert.bindString(7, assestserdo.notes);
							stmtInsert.bindString(8, assestserdo.isApproved);
							stmtInsert.bindString(9, assestserdo.serviceRequestAppId);
							stmtInsert.bindString(10, assestserdo.journeyCode);
							stmtInsert.bindString(11, assestserdo.visitCode);
							stmtInsert.executeInsert();
						}
						
					}
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
	
	/* For Inserting Single Record */
	
	public boolean insertAsset(AssetServiceDO assestserdo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetServiceRequest WHERE AssetServiceRequestId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetServiceRequest(AssetServiceRequestId, UserCode, SiteNo, RequestDate, RequestImage, Status , Notes, IsApproved, ServiceRequestAppId,JourneyCode,VisitCode) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetServiceRequest SET UserCode = ?, SiteNo = ?, RequestDate = ?, RequestImage = ?, Status = ?, Notes = ?, IsApproved = ?, ServiceRequestAppId = ?, JourneyCode = ?, VisitCode = ? where AssetServiceRequestId= ?");
				
					if(assestserdo !=null)
					{
						stmtSelectRec.bindString(1, assestserdo.assetServiceRequestId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, assestserdo.userCode);
							stmtUpdate.bindString(2, assestserdo.siteNo);
							stmtUpdate.bindString(3, assestserdo.requestDate);
							stmtUpdate.bindString(4, assestserdo.multipart_Url);
							stmtUpdate.bindString(5, assestserdo.status);
							stmtUpdate.bindString(6, assestserdo.notes);
							stmtUpdate.bindString(7, assestserdo.isApproved);
							stmtUpdate.bindString(8, assestserdo.serviceRequestAppId);
							stmtUpdate.bindString(9, assestserdo.journeyCode);
							stmtUpdate.bindString(10, assestserdo.visitCode);
							stmtUpdate.bindString(11, assestserdo.assetServiceRequestId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, assestserdo.assetServiceRequestId);
							stmtInsert.bindString(2, assestserdo.userCode);
							stmtInsert.bindString(3, assestserdo.siteNo);
							stmtInsert.bindString(4, assestserdo.requestDate);
							stmtInsert.bindString(5, assestserdo.multipart_Url);
							stmtInsert.bindString(6, assestserdo.status);
							stmtInsert.bindString(7, assestserdo.notes);
							stmtInsert.bindString(8, assestserdo.isApproved);
							stmtInsert.bindString(9, assestserdo.serviceRequestAppId);
							stmtInsert.bindString(10, assestserdo.journeyCode);
							stmtInsert.bindString(11, assestserdo.visitCode);
							stmtInsert.executeInsert();
						}
						
						
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
	
	public Vector<AssetServiceDO> getAllAssetServiceDetails()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetServiceDO obj = null;
			Vector<AssetServiceDO> vecAssetser = new Vector<AssetServiceDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblAssetServiceRequest";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new AssetServiceDO();
						obj.assetServiceRequestId 	 = cursor.getString(0);
						obj.userCode		 = cursor.getString(1);
						obj.siteNo	 = cursor.getString(2);
						obj.requestDate		 = cursor.getString(3);
						obj.requestImage = cursor.getString(4);
						obj.status = cursor.getString(5);
						obj.notes = cursor.getString(6);
						obj.isApproved = cursor.getString(7);
						obj.serviceRequestAppId = cursor.getString(8);
						obj.journeyCode = cursor.getString(9);
						obj.visitCode = cursor.getString(10);
						vecAssetser.add(obj);
						
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

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return vecAssetser;
		}
		
	}
			
	public AssetDO getAssetByAssetId(String assetId)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetDO obj = null;
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblAsset where assetId = '"+assetId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
						obj = new AssetDO();
						obj.assetId 	 = cursor.getString(0);
						obj.barCode		 = cursor.getString(1);
						obj.assetType	 = cursor.getString(2);
						obj.name		 = cursor.getString(3);
						obj.modifiedDate = cursor.getString(4);
						obj.modifiedTime = cursor.getString(5);
						obj.capacity     = cursor.getString(6);
						obj.imagePath    = cursor.getString(7);
						obj.installationDate = cursor.getString(8);
						obj.lastServiceDate = cursor.getString(9);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return obj;
		}
		
	}
			
	
	public void updateAssetImage(String assetId, String imagePath)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAsset SET ImagePath = ? WHERE assetId = ?");
				
				stmtUpdate.bindString(1, imagePath);
				stmtUpdate.bindString(2, assetId);
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

	public void updateAssetService(Vector<AssetServiceDO> vecAssetser2) 
	{
		
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					objSqliteDB = DatabaseHelper.openDataBase();
					SQLiteStatement stmtUpdate = null;
					stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAssetServiceRequest SET AssetServiceRequestId = ?, Status = ? WHERE AssetServiceRequestId = ?");
					
					for (AssetServiceDO assetserDo : vecAssetser2) 
					{
						stmtUpdate.bindString(1, assetserDo.assetServiceRequestId);
						stmtUpdate.bindString(2, assetserDo.status);
						stmtUpdate.bindString(3, assetserDo.assetServiceRequestId);
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
