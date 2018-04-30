package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class BarcodeScanInfoDA 
{
	private Vector<ScanResultObject> vctScanBarcodeList;
	private ScanResultObject objScanResultObject;
	public boolean insertBarcodeScanInfo(ScanResultObject objScanResultObject)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO BarcodeInfo(CustomerCode, type, location,barcode, comments, time,EmpId, Quantity) VALUES(?,?,?,?,?,?,?,?)");
				
				if(objScanResultObject!=null)
				{
					stmtInsert.bindString(1, objScanResultObject.EmpId);
					stmtInsert.bindString(2,objScanResultObject.type);
					stmtInsert.bindString(3, objScanResultObject.location);
					stmtInsert.bindBlob(4, objScanResultObject.barcodeImage);
					stmtInsert.bindString(5,objScanResultObject.comments);
					stmtInsert.bindString(6, objScanResultObject.time);
					stmtInsert.bindString(7, objScanResultObject.EmpId);
					stmtInsert.bindString(8, objScanResultObject.Quantity);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
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
	public Vector<ScanResultObject> getBarcodeScanList(String strCutomerID)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				vctScanBarcodeList= new Vector<ScanResultObject>();
				DictionaryEntry[][] data=null;
				data = DatabaseHelper.get("select rowid,* from BarcodeInfo");
				if(data !=null && data.length>0)
				{
					for(int i=0;i<data.length;i++)
					{
						objScanResultObject 				= 	new ScanResultObject();
						objScanResultObject.rowId			=	StringUtils.getInt(data[i][0].value.toString());
						objScanResultObject.type			=	data[i][2].value.toString();
						objScanResultObject.location		=	data[i][3].value.toString();
						objScanResultObject.barcodeImage	=	(byte[]) data[i][4].value;
						objScanResultObject.comments		=	data[i][5].value.toString();
						objScanResultObject.time			=	data[i][6].value.toString();
						objScanResultObject.Quantity		=	data[i][9].value.toString();
						vctScanBarcodeList.add(objScanResultObject);
	//					LogUtils.infoLog("objScanResultObject.rowId ", ""+objScanResultObject.rowId);
					}	
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return vctScanBarcodeList;
		}
	}

	
	public void updateBarcodeScanInfo(ScanResultObject objScanResultObject)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE BarcodeInfo set type=?, location=?, barcode=?, comments=?, time=?, Quantity=? Where rowid = "+objScanResultObject.rowId );
				
				if(objScanResultObject!=null)
				{
					stmtUpdate.bindString(1,objScanResultObject.type);
					stmtUpdate.bindString(2, objScanResultObject.location);
					stmtUpdate.bindBlob(3, objScanResultObject.barcodeImage);
					stmtUpdate.bindString(4,objScanResultObject.comments);
					stmtUpdate.bindString(5, objScanResultObject.time);
					stmtUpdate.bindString(6, objScanResultObject.Quantity);
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
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
		}
	}
	

	
	public void insertSeletedInfo(ScanResultObject objScanResultObject)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO BarcodeInfo(CustomerCode, type, location, comments, time, Quantity) VALUES(?,?,?,?,?,?)");
				
				if(objScanResultObject!=null)
				{
					stmtInsert.bindString(1, "SM01");
					stmtInsert.bindString(2,objScanResultObject.type);
					stmtInsert.bindString(3, objScanResultObject.location);
					stmtInsert.bindString(4,objScanResultObject.comments);
					stmtInsert.bindString(5, objScanResultObject.time);
					stmtInsert.bindString(6, objScanResultObject.Quantity);
					stmtInsert.executeInsert();
				}
				
				
				stmtInsert.close();
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
	
	public void updateSeletedInfo(ScanResultObject objScanResultObject)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE BarcodeInfo set type=?, location=?, comments=?, time=?, Quantity=? Where rowid = "+objScanResultObject.rowId );
				
				if(objScanResultObject != null)
				{
					stmtUpdate.bindString(1,objScanResultObject.type);
					stmtUpdate.bindString(2, objScanResultObject.location);
					stmtUpdate.bindString(3,objScanResultObject.comments);
					stmtUpdate.bindString(4, objScanResultObject.time);
					stmtUpdate.bindString(5, objScanResultObject.Quantity);
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
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
		}
	}
}
