package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.TransferDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.TransferInoutDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class TransferInOutDA
{
	public String insertTransferInOut(ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem, String salemanCode, String fromEmpNo, String toEmpNo, String transferType, String transferStatus, String sourceVNO, String destVno, String date, String transferID, String mDestOrderId)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "SELECT id from tblOfflineData where SalesmanCode ='"+salemanCode+"' AND Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status = 1 WHERE Id='"+orderId+"'");
					
					String sourceOrderNo = "";
					String destOrderNo = "";
					
					if(transferType.equalsIgnoreCase(AppConstants.TRNS_TYPE_OUT))
					{
						sourceOrderNo = orderId+"";
						destOrderNo = orderId+"";
					}
					else
					{
						sourceOrderNo = orderId+"";
						destOrderNo = mDestOrderId+"";
					}
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferInOut (InventoryUID, fromEmpNo, toEmpNo, trnsferType, transferStatus, sourceVNO, destVNO, date, sourceOrdeNumber, destOrdeNumber) VALUES(?,?,?,?,?,?,?,?,?,?)");
					String inventoryId = transferID;
					
					stmtInsert.bindString(1,inventoryId);
					stmtInsert.bindString(2,fromEmpNo);
					stmtInsert.bindString(3,toEmpNo);
					stmtInsert.bindString(4,transferType);
					stmtInsert.bindString(5,transferStatus);
					stmtInsert.bindString(6,sourceVNO);
					stmtInsert.bindString(7,destVno);
					stmtInsert.bindString(8,date);
					stmtInsert.bindString(9,sourceOrderNo);
					stmtInsert.bindString(10,destOrderNo);
					stmtInsert.executeInsert();
					insertTransferInOutDetails(objSqliteDB, vecModifiedItem, sourceOrderNo);
					if(transferType.equalsIgnoreCase(AppConstants.TRNS_TYPE_IN))
					{
						updateInventoryInStatus(objSqliteDB, vecModifiedItem, fromEmpNo, inventoryId, transferType);
						deleteDetailsFromList(inventoryId);
						deleteFromList(inventoryId);
					}
					else
						updateInventoryInStatus(objSqliteDB, vecModifiedItem, toEmpNo, inventoryId, transferType);
					
					stmtInsert.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	private void deleteFromList(String transferID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblTransferInOutNew Where InventoryUID = '"+transferID+"'");
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
	
	private void deleteDetailsFromList(String transferID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblTransferedInventoryNew Where InventoryUID = '"+transferID+"'");
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
	
	public boolean insertTransferInOutNew(Vector<TransferInoutDO> transferInoutDOs)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferInOutNew (InventoryUID, fromEmpNo, toEmpNo, trnsferType, transferStatus, sourceVNO, destVNO, date, sourceOrdeNumber, destOrdeNumber) VALUES(?,?,?,?,?,?,?,?,?,?)");
			
				for (TransferInoutDO transferInoutDO : transferInoutDOs) 
				{
					String inventoryId = transferInoutDO.InventoryUID;
					stmtInsert.bindString(1,inventoryId);
					stmtInsert.bindString(2,transferInoutDO.fromEmpNo);
					stmtInsert.bindString(3,transferInoutDO.toEmpNo);
					stmtInsert.bindString(4,transferInoutDO.trnsferType);
					stmtInsert.bindString(5,transferInoutDO.transferStatus);
					stmtInsert.bindString(6,transferInoutDO.sourceVNO);
					stmtInsert.bindString(7,transferInoutDO.destVNO);
					stmtInsert.bindString(8,transferInoutDO.Date);
					stmtInsert.bindString(9,transferInoutDO.sourceOrderID);
					stmtInsert.bindString(10,transferInoutDO.destOrderID);
					stmtInsert.executeInsert();
//					insertTransferInOutDetails(objSqliteDB, transferInoutDO.vecOrderDetailDcos, inventoryId);
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
	
	public boolean insertTransferInOutDetailsNew(Vector<TransferDetailDO> vecTransferDetailDOs)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferedInventoryNew (InventoryUID, itemCode, itemDescription, cases,units,totalCases, requestedTotaCases, transferDetailId) VALUES(?,?,?,?,?,?,?,?)");
			
				for (TransferDetailDO transferDetailDO : vecTransferDetailDOs) 
				{
					stmtInsert.bindString(1,transferDetailDO.InventoryUID);
					stmtInsert.bindString(2, transferDetailDO.itemCode);
					stmtInsert.bindString(3, getPoroductNameByID(objSqliteDB, transferDetailDO.itemCode));
					stmtInsert.bindString(4, transferDetailDO.cases+"");
					stmtInsert.bindString(5, transferDetailDO.units+"");
					transferDetailDO.totalCases = (StringUtils.getFloat(transferDetailDO.cases) + StringUtils.getFloat(transferDetailDO.units)/getUniPerCasesBySKU(objSqliteDB, transferDetailDO.itemCode))+"";
					stmtInsert.bindString(6, transferDetailDO.totalCases+"");
					stmtInsert.bindString(7, "");
					stmtInsert.bindString(8, transferDetailDO.transferDetailID);
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
	
	
	public String getPoroductNameByID(SQLiteDatabase slDatabase, String SKU)
	{
		
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			String str = "";
			try
			{
				if(slDatabase == null)
					slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("Select Description from tblProducts where SKU = '"+SKU+"'", null);
				if(cursor.moveToFirst())
				{
					str = cursor.getString(0);
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return str;
		}
	}
	
//	public boolean insertTransferInOutNew(Vector<TransferInoutDO> transferInoutDOs)
//	{
//		synchronized (MyApplication.MyLock)
//		{
//			SQLiteDatabase objSqliteDB = null;
//			try
//			{
//				//Opening the database
//				objSqliteDB = DatabaseHelper.openDataBase();
//				
//				//Query to Insert the User information in to UserInfo Table
//				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferInOut (InventoryUID, fromEmpNo, toEmpNo, trnsferType, transferStatus, sourceVNO, destVNO, date) VALUES(?,?,?,?,?,?,?,?)");
//			
//				for (TransferInoutDO transferInoutDO : transferInoutDOs) 
//				{
//					String inventoryId = transferInoutDO.InventoryUID;
//					stmtInsert.bindString(1,inventoryId);
//					stmtInsert.bindString(2,transferInoutDO.fromEmpNo);
//					stmtInsert.bindString(3,transferInoutDO.toEmpNo);
//					stmtInsert.bindString(4,transferInoutDO.trnsferType);
//					stmtInsert.bindString(5,transferInoutDO.transferStatus);
//					stmtInsert.bindString(6,transferInoutDO.sourceVNO);
//					stmtInsert.bindString(7,transferInoutDO.destVNO);
//					stmtInsert.bindString(8,transferInoutDO.Date);
//					stmtInsert.executeInsert();
//					insertTransferInOutDetails(objSqliteDB, transferInoutDO.vecOrderDetailDcos, inventoryId);
//				}				
//				stmtInsert.close();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				return false;
//			}
//			finally
//			{
//				if(objSqliteDB != null)
//					objSqliteDB.close();
//			}
//			
//			return false;
//		}
//	}
	public void insertTransferInOutDetails(SQLiteDatabase objSqliteDB, ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem, String InventoryUID)
	{
		synchronized (MyApplication.MyLock)
		{
			try
			{
				//Opening the database
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferedInventory (InventoryUID, itemCode, itemDescription, cases,units,totalCases, requestedTotaCases, transferDetailId) VALUES(?,?,?,?,?,?,?,?)");
				for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecModifiedItem) 
				{
					stmtInsert.bindString(1,InventoryUID);
					stmtInsert.bindString(2, deliveryAgentOrderDetailDco.itemCode);
					stmtInsert.bindString(3, deliveryAgentOrderDetailDco.itemDescription);
					stmtInsert.bindString(4, deliveryAgentOrderDetailDco.preCases+"");
					stmtInsert.bindString(5, deliveryAgentOrderDetailDco.preUnits+"");
					stmtInsert.bindString(6, deliveryAgentOrderDetailDco.totalCases+"");
					stmtInsert.bindString(7, "");
					stmtInsert.bindString(8, deliveryAgentOrderDetailDco.transferDetailID);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean updateInventoryInStatus(SQLiteDatabase objSqliteDB, ArrayList<DeliveryAgentOrderDetailDco> vecProductDO, String empNo, String inventoryId, String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor 			   = 	null;
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
//				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=?,PrimaryQuantity=? where ItemCode = ?";
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set totalQty=?,availQty=?,PrimaryQuantity=?, SecondaryQuantity=? where ItemCode = ?";
				
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				SQLiteStatement stmtInsert 			= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty,totalQty, uploadStatus) VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(DeliveryAgentOrderDetailDco productDO : vecProductDO)
					{
						stmtSelectRec.bindString(1, productDO.itemCode);
						long count = stmtSelectRec.simpleQueryForLong();
						
						if(count > 0)
						{
							String strQuery 	= 	"SELECT totalQty,availQty, PrimaryQuantity, SecondaryQuantity From  tblVMSalesmanInventory where ItemCode = '"+productDO.itemCode+"'";
							cursor 	= objSqliteDB.rawQuery(strQuery, null);
							if(cursor.moveToFirst())
							{
								float totalQty 			= cursor.getFloat(0);
								float availQty 			= cursor.getFloat(1);
								float primaryQuantity 	= cursor.getFloat(2);
								float secondaryQuantity = cursor.getFloat(3);
								
								if(type.equalsIgnoreCase(AppConstants.TRNS_TYPE_IN))
								{
									totalQty		= totalQty + productDO.totalCases;
									availQty 		= availQty + productDO.totalCases;
									primaryQuantity = primaryQuantity + productDO.preCases;
									secondaryQuantity = secondaryQuantity + productDO.preUnits;
									
									stmtUpdateQty.bindString(1, ""+totalQty);
									
									stmtUpdateQty.bindString(2, ""+availQty);
									stmtUpdateQty.bindString(3, ""+primaryQuantity);
									stmtUpdateQty.bindString(4, ""+secondaryQuantity);
								}
								else
								{
									
									availQty 			= availQty - productDO.totalCases;
									
									stmtUpdateQty.bindString(1, ""+totalQty);
									
									if(availQty > 0 )
										stmtUpdateQty.bindString(2, ""+availQty);
									else
										stmtUpdateQty.bindString(2, "0");
									
									stmtUpdateQty.bindString(3, ""+primaryQuantity);
									stmtUpdateQty.bindString(4, ""+secondaryQuantity);
								}
								
								stmtUpdateQty.bindString(5, ""+productDO.itemCode);
								stmtUpdateQty.execute();
								
								if(cursor != null && !cursor.isClosed())
									cursor.close();
							}
						}
						else
						{
							stmtInsert.bindString(1, inventoryId);
							stmtInsert.bindString(2, CalendarUtils.getOrderPostDate());
							stmtInsert.bindString(3, empNo);
							stmtInsert.bindString(4, productDO.itemCode);
							stmtInsert.bindString(5, ""+productDO.preCases);
							stmtInsert.bindString(6, ""+productDO.preUnits);
							stmtInsert.bindString(7, "true");
							stmtInsert.bindString(8, ""+productDO.totalCases);
							stmtInsert.bindString(9, ""+productDO.totalCases);
							stmtInsert.bindString(10, "Y");
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
	}
	
	public Vector<TransferInoutDO> getUnuploadedTransferData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferInOut where transferStatus ='N'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						TransferInoutDO transferInoutDO 	= new TransferInoutDO();
						transferInoutDO.InventoryUID   		= cursor.getString(0);
						transferInoutDO.fromEmpNo		 	= cursor.getString(1);
						transferInoutDO.toEmpNo 		 	= cursor.getString(2);
						transferInoutDO.trnsferType    		= cursor.getString(3);
						
						transferInoutDO.sourceVNO    		= cursor.getString(5);
						transferInoutDO.destVNO    			= cursor.getString(6);
						transferInoutDO.Date    			= cursor.getString(7);
						transferInoutDO.sourceOrderID		= cursor.getString(8);
						transferInoutDO.destOrderID  		= cursor.getString(9);
						transferInoutDO.vecOrderDetailDcos	= getTransferedProduct(transferInoutDO.sourceOrderID);
						vecTransferInoutDOs.add(transferInoutDO);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecTransferInoutDOs;
		}
	}
	
	public Vector<TransferInoutDO> getTransferInOutList()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferInOutNew", null);
				if(cursor.moveToFirst())
				{
					do
					{
						TransferInoutDO transferInoutDO 	= new TransferInoutDO();
						transferInoutDO.InventoryUID   		= cursor.getString(0);
						transferInoutDO.fromEmpNo		 	= cursor.getString(1);
						transferInoutDO.toEmpNo 		 	= cursor.getString(2);
						transferInoutDO.trnsferType    		= cursor.getString(3);
						transferInoutDO.sourceVNO    		= cursor.getString(5);
						transferInoutDO.destVNO    			= cursor.getString(6);
						transferInoutDO.Date    			= cursor.getString(7);
						transferInoutDO.sourceOrderID    	= cursor.getString(8);
						transferInoutDO.destOrderID    		= cursor.getString(9);
						
						transferInoutDO.customerName = new UserInfoDA().getNameByEmpNO(transferInoutDO.fromEmpNo);
						vecTransferInoutDOs.add(transferInoutDO);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecTransferInoutDOs;
		}
	}
	
	
	public void updateTransferInOUTStatus(String UUID, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblTransferInOut SET transferStatus =? WHERE InventoryUID =?");
				stmtUpdate.bindString(1,status);
				stmtUpdate.bindString(2, UUID);
				stmtUpdate.executeInsert();
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
	
	public void updateTransferInOUTStatusNew(Vector<TransferInoutDO> transferInoutDOs, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				for (TransferInoutDO transferInoutDO : transferInoutDOs) {
					
					SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblTransferInOut SET transferStatus =? WHERE InventoryUID =?");
					stmtUpdate.bindString(1,status);
					stmtUpdate.bindString(2, transferInoutDO.InventoryUID);
					stmtUpdate.executeInsert();
				}
				//Query to Insert the User information in to UserInfo Table
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
	public ArrayList<DeliveryAgentOrderDetailDco> getTransferedProduct(String orderNumber)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> arrOrderDetailDcos = new ArrayList<DeliveryAgentOrderDetailDco>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferedInventory where InventoryUID = '"+orderNumber+"'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						
						orderDetailDco.itemCode				= cursor.getString(1);
						orderDetailDco.itemDescription		= cursor.getString(2);
						orderDetailDco.preCases				= StringUtils.getFloat(cursor.getString(3));
						orderDetailDco.preUnits				= StringUtils.getInt(cursor.getString(4));
						orderDetailDco.totalCases			= StringUtils.getFloat(cursor.getString(5));
						orderDetailDco.transferDetailID		= cursor.getString(7);
						arrOrderDetailDcos.add(orderDetailDco);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return arrOrderDetailDcos;
		}
	}
	
	public ArrayList<DeliveryAgentOrderDetailDco> getTransferedProductNew(String uuID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> arrOrderDetailDcos = new ArrayList<DeliveryAgentOrderDetailDco>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferedInventoryNew where InventoryUID = '"+uuID+"'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						
						orderDetailDco.itemCode				= cursor.getString(1);
						orderDetailDco.itemDescription		= cursor.getString(2);
						orderDetailDco.preCases				= StringUtils.getFloat(cursor.getString(3));
						orderDetailDco.preUnits				= StringUtils.getInt(cursor.getString(4));
						orderDetailDco.totalCases			= StringUtils.getFloat(cursor.getString(5));
						orderDetailDco.transferDetailID		= cursor.getString(7);
						arrOrderDetailDcos.add(orderDetailDco);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return arrOrderDetailDcos;
		}
	}
	
	public int getUniPerCasesBySKU(SQLiteDatabase sqLiteDatabase, String SKU)
	{
		synchronized(MyApplication.MyLock) 
		{
			int unitPerCases = 0;
			Cursor cursor	 = null;
			try 
			{
				if(sqLiteDatabase == null)
					sqLiteDatabase  =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT UnitPerCase FROM tblProducts where SKU ='"+SKU+"'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
					unitPerCases = cursor.getInt(0);
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return unitPerCases;
		}
	}
}
