package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.AssetTypeDo;

public class AssetTypeDA
{
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertAssetTypes(Vector<AssetTypeDo> vecAssetTypeDos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetType WHERE AssetTypeId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetType (AssetTypeId, Code, Name) VALUES(?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetType set Code = ?, Name = ? where AssetTypeId =?");
				
				if(vecAssetTypeDos != null && vecAssetTypeDos.size() > 0)
				{
					for (AssetTypeDo assetTypeDo : vecAssetTypeDos) 
					{
						if(assetTypeDo!=null)
						{
							stmtSelectRec.bindString(1, assetTypeDo.assetTypeId);
							long countRec = stmtSelectRec.simpleQueryForLong();
							if(countRec >0)
							{
								stmtUpdate.bindString(1,assetTypeDo.code);
								stmtUpdate.bindString(2,assetTypeDo.name);
								stmtUpdate.bindString(3,assetTypeDo.assetTypeId);
								
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,assetTypeDo.assetTypeId);
								stmtInsert.bindString(2,assetTypeDo.code);
								stmtInsert.bindString(3,assetTypeDo.name);
								
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
	
	public void insertAssetType(AssetTypeDo	assetTypeDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblAssetType WHERE AssetTypeId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAssetType (AssetTypeId, Code, Name) VALUES(?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblAssetType set Code = ?, Name = ? where AssetTypeId =?");
				
				if(assetTypeDo!=null)
				{
					stmtSelectRec.bindString(1, assetTypeDo.assetTypeId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec >0)
					{
						stmtUpdate.bindString(1,assetTypeDo.code);
						stmtUpdate.bindString(2,assetTypeDo.name);
						stmtUpdate.bindString(3,assetTypeDo.assetTypeId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1,assetTypeDo.assetTypeId);
						stmtInsert.bindString(2,assetTypeDo.code);
						stmtInsert.bindString(3,assetTypeDo.name);
						
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
	
	public Vector<AssetTypeDo> getAllAssetTypes()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<AssetTypeDo> vecAssetTypeDos = new Vector<AssetTypeDo>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetType";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						AssetTypeDo assetTypeDo = new AssetTypeDo();
						assetTypeDo.assetTypeId		= cursor.getString(0);
						assetTypeDo.code			= cursor.getString(1);
						assetTypeDo.name		= cursor.getString(2);
						vecAssetTypeDos.add(assetTypeDo);
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
			return vecAssetTypeDos;
		}
	}
	
	public AssetTypeDo getAssetType(String assetTypeId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetTypeDo assetTypeDo = null;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblAssetType where AssetTypeId = '"+assetTypeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
						assetTypeDo = new AssetTypeDo();
						assetTypeDo.assetTypeId		= cursor.getString(0);
						assetTypeDo.code			= cursor.getString(1);
						assetTypeDo.name		= cursor.getString(2);
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
			return assetTypeDo;
		}
	}
}
