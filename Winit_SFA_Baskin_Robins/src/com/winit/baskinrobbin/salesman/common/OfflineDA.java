package com.winit.baskinrobbin.salesman.common;

import java.util.ArrayList;
import java.util.Vector;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

public class OfflineDA {

	
	public String getNextSequenceNumber(OfflineDataType offlineDataType,String strSalesCode,SQLiteDatabase objSqliteDB){
		synchronized (MyApplication.MyLock)
		{
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				String query = "";
				
				switch (offlineDataType) 
				{
					case CUSTOMER:
						query = "SELECT id from tblOfflineData where SalesmanCode ='"+strSalesCode+"' AND Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
						break;
//					

					default:
						break;
				}
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				//	AltTrxNo = cursor.getString(1);
					
					//object[1] = AltTrxNo;
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return orderId;
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	public String getNextSequenceNumber(OfflineDataType offlineDataType,SQLiteDatabase objSqliteDB)
	{
		synchronized (MyApplication.MyLock)
		{
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "";
				
				switch (offlineDataType) 
				{
				case ORDER:
					query ="SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) AND id NOT IN(SELECT InvoiceNumber FROM tblPendingInvoices) Order By id Limit 1";
					break;
//					case DRAFT:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.Draft+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;
//					case MISC:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.Misc+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;
					case PAYMENT:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Receipt+"' AND status = 0  AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) ORDER BY id ASC Limit 1";
						break;
					case MOVEMENT:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Movement+"' AND status = 0 AND id NOT IN(SELECT MovementCode FROM tblMovementHeader) ORDER BY id ASC Limit 1";
						break;
					case CUSTOMER:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
						break;
					case RETURN:
						query = "SELECT id from tblOfflineData WHERE Type ='"+AppConstants.Return+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
						break;
//					case CREDITNOTE:
//						query= "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.CreditNote+"' AND status = 0 AND id NOT IN(SELECT CreditNoteCode FROM tblCreditNote) ORDER BY id ASC Limit 1";
//						break;
//					case PRICEPROTECTION:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.CreditNote+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;

					default:
						break;
				}
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				//	AltTrxNo = cursor.getString(1);
					
					//object[1] = AltTrxNo;
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return orderId;
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	public  String getNextSequenceNumber(OfflineDataType offlineData)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "";
				
				switch (offlineData) 
				{
					case ORDER:
						query ="SELECT id from tblOfflineData where  Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) AND id NOT IN(SELECT InvoiceNumber FROM tblPendingInvoices) Order By id Limit 1";
						break;
					case RETURN:
						query = "SELECT id from tblOfflineData WHERE Type ='"+AppConstants.Return+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
						break;
					case CUSTOMER:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
						break;
//					case DRAFT:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.Draft+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;
//					case MISC:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.Misc+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;
					case PAYMENT:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Receipt+"' AND status = 0  AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) ORDER BY id ASC Limit 1";
						break;
					case MOVEMENT:
						query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Movement+"' AND status = 0 AND id NOT IN(SELECT MovementCode FROM tblMovementHeader) ORDER BY id ASC Limit 1";
						break;
//					case CREDITNOTE:
//						query= "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.CreditNote+"' AND status = 0 AND id NOT IN(SELECT CreditNoteCode FROM tblCreditNote) ORDER BY id ASC Limit 1";
//						break;
//					case PRICEPROTECTION:
//						query = "SELECT id,AltTrxNo from tblOfflineData where  Type ='"+AppConstants.CreditNote+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) ORDER BY id ASC Limit 1";
//						break;

					default:
						break;
				}
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				//	AltTrxNo = cursor.getString(1);
					
					//object[1] = AltTrxNo;
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return orderId;
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	public ArrayList<String> getSequenceNumbersBasedOnLimit(OfflineDataType offlineData,int LIMIT,SQLiteDatabase objSqliteDB)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			ArrayList<String> arrayList = new ArrayList<String>();
			try 
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = 	DatabaseHelper.openDataBase();
				
				String query 	= 	"";
				switch (offlineData) {
					case PAYMENT:
						query 	= "SELECT distinct id from tblOfflineData where Type ='"+AppConstants.Receipt+"' " +
								"AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader)  " +
								"ORDER BY id ASC Limit "+LIMIT;
					break;

					default:
						break;
				}
				cursor 			= 	objSqliteDB.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						arrayList.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
					
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			return arrayList;
		}
	}
	
	public ArrayList<String> getSequenceNumbersBasedOnLimit(OfflineDataType offlineData,int LIMIT)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase objSqliteDB = null;
			ArrayList<String> arrayList = new ArrayList<String>();
			try 
			{
				objSqliteDB = 	DatabaseHelper.openDataBase();
				
				String query 	= 	"";
				switch (offlineData) {
					case PAYMENT:
						query 	= "SELECT distinct id from tblOfflineData where Type ='"+AppConstants.Receipt+"' " +
								"AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader)  " +
								"ORDER BY id ASC Limit "+LIMIT;
					break;

					default:
						break;
				}
				cursor 			= 	objSqliteDB.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						arrayList.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
					
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return arrayList;
		}
	}
	
	public void updateSequenceNumberStatus(String offlineDataid)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(!TextUtils.isEmpty(offlineDataid))
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+offlineDataid+"'");
			}
			catch(Exception e)
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
	public void updateSequenceNumberStatus(String offlineDataid,SQLiteDatabase objSqliteDB)
	{
		synchronized (MyApplication.MyLock) 
		{
//			SQLiteDatabase objSqliteDB = null;
			try
			{
				if(objSqliteDB == null || !objSqliteDB.isOpen())
					objSqliteDB = DatabaseHelper.openDataBase();
				
				if(!TextUtils.isEmpty(offlineDataid))
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+offlineDataid+"'");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
			}
		}
	}
	
	public boolean insertOfflineData(Vector<NameIDDo> vecPaymentReceipts)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement insertStament = null, selectStament = null;
			String selectStmt 	= "SELECT COUNT(*) from tblOfflineData WHERE Id =?";
			String query 		= "INSERT INTO tblOfflineData (Id, SalesmanCode, Type, status,AltTrxNo) VALUES(?,?,?,?,?)";
			
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				selectStament 	= 	sqLiteDatabase.compileStatement(selectStmt);
				insertStament	=	sqLiteDatabase.compileStatement(query);
				for(NameIDDo nameIDDo : vecPaymentReceipts)
				{
					selectStament.bindString(1, nameIDDo.strId);
					long count = selectStament.simpleQueryForLong();
					
					if(count == 0)
					{
						insertStament.bindString(1, nameIDDo.strId);
						insertStament.bindString(2, nameIDDo.strName);
						insertStament.bindString(3, nameIDDo.strType);
						insertStament.bindString(4, "0");
						insertStament.bindString(5, ""+nameIDDo.AltTrxNo);
						insertStament.executeInsert();
					}
				}
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(insertStament!=null)
					insertStament.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return true;
		}
	}
	
	public enum OfflineDataType
	{
		MOVEMENT,
		ORDER,
		RETURN,
		DRAFT,
		PAYMENT,
		CREDITNOTE,
		MISC,
		CUSTOMER,
		PRICEPROTECTION
	}
	

}
