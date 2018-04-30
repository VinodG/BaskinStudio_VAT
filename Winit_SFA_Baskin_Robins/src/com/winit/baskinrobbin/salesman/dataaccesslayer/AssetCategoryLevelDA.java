package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;

public class AssetCategoryLevelDA {
	
	
	
	
	
	public Vector<NameIDDo> getAllAssetLevel1()
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		NameIDDo object= null;
		Vector<NameIDDo> vecAssetLevel1 = new Vector<NameIDDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT DISTINCT Category_Level1 FROM tblAssetCategoryDetails";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					object = new NameIDDo();
					object.strName = cursor.getString(0);
					vecAssetLevel1.add(object);
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
		
		return vecAssetLevel1;
		
	}
	
	public Vector<NameIDDo> getAllAssetLevel2(String levelname)
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		NameIDDo object= null;
		Vector<NameIDDo> vecAssetLevel1 = new Vector<NameIDDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT DISTINCT Category_Level2 FROM tblAssetCategoryDetails where Category_Level1 = '"+levelname+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					object = new NameIDDo();
					object.strName = cursor.getString(0);
					vecAssetLevel1.add(object);
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
		
		return vecAssetLevel1;
		
	}
	
	public Vector<NameIDDo> getAllAssetLevel3(String levelname)
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		NameIDDo object= null;
		Vector<NameIDDo> vecAssetLevel = new Vector<NameIDDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT DISTINCT Category_Level3 FROM tblAssetCategoryDetails where Category_Level2 = '"+levelname+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					object = new NameIDDo();
					object.strName = cursor.getString(0);
					vecAssetLevel.add(object);
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
		
		return vecAssetLevel;
		
	}
	
	public Vector<NameIDDo> getAllAssetLevel4(String levelname)
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		NameIDDo object= null;
		Vector<NameIDDo> vecAssetLevel = new Vector<NameIDDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT DISTINCT Category_Level4 FROM tblAssetCategoryDetails where Category_Level3 = '"+levelname+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					object = new NameIDDo();
					object.strName = cursor.getString(0);
					vecAssetLevel.add(object);
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
		
		return vecAssetLevel;
		
	}
	public Vector<NameIDDo> getAllAssetLevel5(String levelname)
	{
		
		
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		NameIDDo object= null;
		Vector<NameIDDo> vecAssetLevel = new Vector<NameIDDo>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			String query = "SELECT DISTINCT Category_Level5 FROM tblAssetCategoryDetails where Category_Level4 = '"+levelname+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				
				do
				{
					object = new NameIDDo();
					object.strName = cursor.getString(0);
					vecAssetLevel.add(object);
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
		
		return vecAssetLevel;
		
	}
	
	
}
