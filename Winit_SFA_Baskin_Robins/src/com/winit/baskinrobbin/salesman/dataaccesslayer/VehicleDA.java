package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.dataobject.UnUploadedDataDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;


public class VehicleDA 
{
	public boolean insertVehicles(Vector<VehicleDO> vecVehicleDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
//				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVehicle WHERE VEHICLE_NO = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVehicle (VEHICLE_NO, VEHICLE_MODEL, VEHICLE_TYPE, DEPT, EMPNO, AGENT_NAME, LOCATION, ROUTE) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVehicle SET VEHICLE_MODEL = ?, VEHICLE_TYPE = ?, DEPT = ?,VEHICLE_NO = ?, AGENT_NAME = ?, LOCATION = ?, ROUTE = ? WHERE  EMPNO = ?");
				 
				for(VehicleDO  vehicleDO : vecVehicleDO)
				{
//					stmtSelectRec.bindString(1, vehicleDO.VEHICLE_NO);
//					
//					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
						stmtUpdate.bindString(1, vehicleDO.VEHICLE_MODEL);
						stmtUpdate.bindString(2, vehicleDO.VEHICLE_TYPE);
						stmtUpdate.bindString(3, vehicleDO.DEPT);
						stmtUpdate.bindString(4, vehicleDO.VEHICLE_NO);
						stmtUpdate.bindString(5, vehicleDO.AGENT_NAME);
						stmtUpdate.bindString(6, vehicleDO.LOCATION);
						stmtUpdate.bindString(7, vehicleDO.ROUTE);
						stmtUpdate.bindString(8, vehicleDO.EMPNO);
//						stmtUpdate.execute();
//					}
//					else
					if(stmtUpdate.executeUpdateDelete() <=0)
					{
						stmtInsert.bindString(1, vehicleDO.VEHICLE_NO);
						stmtInsert.bindString(2, vehicleDO.VEHICLE_MODEL);
						stmtInsert.bindString(3, vehicleDO.VEHICLE_TYPE);
						stmtInsert.bindString(4, vehicleDO.DEPT);
						stmtInsert.bindString(5, vehicleDO.EMPNO);
						stmtInsert.bindString(6, vehicleDO.AGENT_NAME);
						stmtInsert.bindString(7, vehicleDO.LOCATION);
						stmtInsert.bindString(8, vehicleDO.ROUTE);
						stmtInsert.executeInsert();
					}
				}
				
//				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
	public ArrayList<VanLoadDO> getAllItemToVerifyByMovementID(String movemetId) {
		synchronized (MyApplication.MyLock) {
			ArrayList<VanLoadDO> vecVanLoadDOs = new ArrayList<VanLoadDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;

			try {
				sLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery = "SELECT distinct INV.InProcessQuantity,  INV.ItemCode, TP.Description, TP.UnitPerCase, "
						+ "TP.ItemBatchCode, INV.UOM, INV.ExpiryDate, INV.MovementReasonCode, INV.CreatedOn, INV.WHApprovedQty,INV.BatchNumber from  tblProducts TP, tblMovementDetail INV  "
						+ "where TP.ItemCode= INV.ItemCode AND INV.MovementCode = '"+ movemetId+ "' ";

				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							VanLoadDO loadDO = new VanLoadDO();
							loadDO.SellableQuantity = cursor.getFloat(0);
							loadDO.ItemCode = cursor.getString(1);
							loadDO.Description = cursor.getString(2);
							loadDO.UnitsPerCases = StringUtils.getInt(cursor.getString(3));
							loadDO.UOM = cursor.getString(5);
							loadDO.ExpiryDate = cursor.getString(6);
							loadDO.inProccessQty = loadDO.SellableQuantity;
							loadDO.MovementReasonCode = cursor.getString(7);
							loadDO.CreatedOn = cursor.getString(8);
							loadDO.inProcessQuantityLevel1 = cursor.getInt(9);
							loadDO.shippedQuantityLevel1 = loadDO.inProcessQuantityLevel1;
							loadDO.ShippedQuantity = loadDO.shippedQuantityLevel1;
							loadDO.BatchCode = cursor.getString(10);
							vecVanLoadDOs.add(loadDO);
						} while (cursor.moveToNext());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (sLiteDatabase != null && sLiteDatabase.isOpen())
					sLiteDatabase.close();
			}

			return vecVanLoadDOs;
		}
	}
	public boolean insertVMInventory(Vector<InventoryObject> vector)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty, totalQty) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,SecondaryQuantity = ?,totalQty=? WHERE ItemCode = ?");
				 
				for(InventoryObject inventoryObject : vector)
				{
					stmtSelectRec.bindString(1, inventoryObject.itemCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(countRec != 0)
					{	
						float dlvrdQty = 0, lastQty = 0;
						Cursor cursor = objSqliteDB.rawQuery("SELECT totalQty,(totalQty- availQty) FROM tblVMSalesmanInventory WHERE ItemCode = '"+inventoryObject.itemCode+"'", null);
						if(cursor.moveToFirst())
						{
							lastQty  	= 	 cursor.getFloat(0);
							dlvrdQty 	=	 cursor.getFloat(1);
							
							if(inventoryObject.availCases > lastQty)
							{
								stmtUpdate.bindString(1, ""+inventoryObject.PrimaryQuantity);
								stmtUpdate.bindString(2, ""+(inventoryObject.availCases  - dlvrdQty));
								stmtUpdate.bindString(3, ""+inventoryObject.SecondaryQuantity);
								stmtUpdate.bindString(4, ""+inventoryObject.availCases);
								stmtUpdate.bindString(5, inventoryObject.itemCode);
								stmtUpdate.execute();
							}
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
					else
					{
						stmtInsert.bindString(1, inventoryObject.VMSalesmanInventoryId);
						stmtInsert.bindString(2, inventoryObject.Date);
						stmtInsert.bindString(3, inventoryObject.SalesmanCode);
						stmtInsert.bindString(4, inventoryObject.itemCode);
						stmtInsert.bindString(5, ""+inventoryObject.PrimaryQuantity);
						stmtInsert.bindString(6, ""+inventoryObject.SecondaryQuantity);
						stmtInsert.bindString(7, ""+inventoryObject.IsAllVerified);
						stmtInsert.bindString(8, ""+inventoryObject.availCases);
						stmtInsert.bindString(9, ""+inventoryObject.availCases);
						stmtInsert.executeInsert();
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	
	public boolean insertReturnInventory(Vector<InventoryObject> vector)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblReturnInventory WHERE ItemCode = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblReturnInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty, totalQty) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblReturnInventory SET PrimaryQuantity = ?,SecondaryQuantity = ?,postStatus=? WHERE ItemCode = ?");
				 
				for(InventoryObject inventoryObject : vector)
				{
					stmtSelectRec.bindString(1, inventoryObject.itemCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, ""+inventoryObject.PrimaryQuantity);
						stmtUpdate.bindString(2, ""+inventoryObject.SecondaryQuantity);
						stmtUpdate.bindString(3, "0");
						stmtUpdate.bindString(4, inventoryObject.itemCode);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, inventoryObject.VMSalesmanInventoryId);
						stmtInsert.bindString(2, inventoryObject.Date);
						stmtInsert.bindString(3, inventoryObject.SalesmanCode);
						stmtInsert.bindString(4, inventoryObject.itemCode);
						stmtInsert.bindString(5, ""+inventoryObject.PrimaryQuantity);
						stmtInsert.bindString(6, ""+inventoryObject.SecondaryQuantity);
						stmtInsert.bindString(7, ""+inventoryObject.IsAllVerified);
						stmtInsert.bindString(8, ""+inventoryObject.availCases);
						stmtInsert.bindString(9, ""+inventoryObject.availCases);
						stmtInsert.executeInsert();
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	public boolean updateVMInventory(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String salesman, boolean isInsert, boolean isUploaded)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			String status = "N";
			try 
			{
				if(isUploaded)
					status = "Y";
				
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty) VALUES(?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,IsAllVerified=?,uploadStatus=?,SecondaryQuantity=? WHERE ItemCode = ?");
				 
				for(DeliveryAgentOrderDetailDco objDco : vecOrdProduct)
				{
					stmtSelectRec.bindString(1, objDco.itemCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, ""+objDco.preCases);
						stmtUpdate.bindString(2, ""+(objDco.preUnits));
					
						if(isInsert)
							stmtUpdate.bindString(3, "false");
						else
							stmtUpdate.bindString(3, "true");
						
						stmtUpdate.bindString(4, status);
						stmtUpdate.bindString(5, ""+(objDco.preUnits));
						stmtUpdate.bindString(6, objDco.itemCode);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, StringUtils.getUniqueUUID());
						stmtInsert.bindString(2, CalendarUtils.getOrderPostDate());
						stmtInsert.bindString(3, salesman);
						stmtInsert.bindString(4, objDco.itemCode);
						stmtInsert.bindString(5, ""+objDco.preCases);
						stmtInsert.bindString(6, ""+objDco.preUnits);
						
						if(isInsert)
							stmtInsert.bindString(7, "false");
						else
							stmtInsert.bindString(7, "true");
						
						
						stmtInsert.bindString(8, ""+objDco.preUnits);
						stmtInsert.executeInsert();
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean updateIsVerified()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues contentValues = new ContentValues();
				contentValues.put("IsVerified", 1);
				objSqliteDB.update("tblVanStock", contentValues, null, null);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	public boolean updateVMInventoryStatus(ArrayList<VanLoadDO> vecOrdProduct)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVanStock SET IsVerified =? WHERE ItemCode = ?");
				 
				for(VanLoadDO objDco : vecOrdProduct)
				{
					stmtUpdate.bindLong(1, 1);
					stmtUpdate.bindString(2, ""+objDco.ItemCode);
					stmtUpdate.execute();
				}
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean updateVMInventoryFromService()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET uploadStatus=?");
				 
				stmtUpdate.bindString(1, "Y");
				stmtUpdate.execute(); 
				
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	
	public Vector<VehicleDO> getTruckListByDelievryAgentId(String strDeliveryAgentId, String currentdate)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<VehicleDO> vecTruckList = new Vector<VehicleDO>();
			//String strQuery = "Select * from tblFromSubInventories where EMPNO='"+strDeliveryAgentId+"' AND IFNULL(VEHICLE_NO,'') != '' LIMIT 1";
			String strQuery = "Select * from tblVehicle where EMPNO='"+strDeliveryAgentId+"' AND IFNULL(VEHICLE_NO,'') != '' LIMIT 1";
			LogUtils.errorLog("strQuery", "strQuery - "+strQuery);
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor 				= null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				cursor 				= objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						VehicleDO objVehicleDO 		=  new VehicleDO();
						objVehicleDO.VEHICLE_NO 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_NO"));
						objVehicleDO.VEHICLE_MODEL 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_MODEL"));
						objVehicleDO.VEHICLE_TYPE 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_TYPE"));
						objVehicleDO.DEPT 					=  cursor.getString(cursor.getColumnIndex("DEPT"));
						objVehicleDO.EMPNO 					=  cursor.getString(cursor.getColumnIndex("EMPNO"));
						objVehicleDO.AGENT_NAME 			=  cursor.getString(cursor.getColumnIndex("AGENT_NAME"));
						objVehicleDO.LOCATION 				=  cursor.getString(cursor.getColumnIndex("LOCATION"));
						objVehicleDO.ROUTE 					=  cursor.getString(cursor.getColumnIndex("ROUTE"));
						vecTruckList.add(objVehicleDO);
					}
					 while(cursor.moveToNext());
					 
					 if(cursor!=null && !cursor.isClosed())
						 cursor.close();
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return vecTruckList;
		}
	}
	
	
	public Vector<VehicleDO> getTruckListForStockExchange(String empNo, String vehicleNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<VehicleDO> vecTruckList = new Vector<VehicleDO>();
			String strQuery = "Select * from tblVehicle WHERE EMPNO ='"+empNo+"'";
			LogUtils.errorLog("strQuery", "strQuery - "+strQuery);
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor 				= null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				cursor 				= objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						VehicleDO objVehicleDO 				=  new VehicleDO();
						objVehicleDO.VEHICLE_NO 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_NO"));
						objVehicleDO.VEHICLE_MODEL 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_MODEL"));
						objVehicleDO.VEHICLE_TYPE 			=  cursor.getString(cursor.getColumnIndex("VEHICLE_TYPE"));
						objVehicleDO.DEPT 					=  cursor.getString(cursor.getColumnIndex("DEPT"));
						objVehicleDO.EMPNO 					=  cursor.getString(cursor.getColumnIndex("EMPNO"));
						objVehicleDO.AGENT_NAME 			=  cursor.getString(cursor.getColumnIndex("AGENT_NAME"));
						objVehicleDO.LOCATION 				=  cursor.getString(cursor.getColumnIndex("LOCATION"));
						objVehicleDO.ROUTE 					=  cursor.getString(cursor.getColumnIndex("ROUTE"));
						vecTruckList.add(objVehicleDO);
					}
					 while(cursor.moveToNext());
					 
					 
					 if(cursor!=null && !cursor.isClosed())
						 cursor.close();
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return vecTruckList;
		}
	}
	
	public boolean isAnyItemToVerify(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result 		=	false;
			String strQuery 	= 	"SELECT * from tblVanStock tvs inner join tblProducts tp on tp.itemCode = tvs.itemCode where IFNULL(IsVerified,0)=0 AND SellableQuantity > 0";
			
			LogUtils.errorLog("strQuery", "strQuery - "+strQuery);
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			try 
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					result = cursor.moveToFirst();
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				result = false;
			}
			finally 
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase != null)
					sLiteDatabase.close();
			}
			return result;
		}
	}
	
	public int isAnyItemAvail(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			int result 			=	0;
			String strQuery 	= 	"SELECT Count(*) from tblVanStock WHERE SellableQuantity > 0";
			LogUtils.errorLog("strQuery", "strQuery - "+strQuery);
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			try 
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					result =cursor.getInt(0) ;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				result = 0;
			}
			finally 
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase != null)
					sLiteDatabase.close();
			}
			return result;
		}
	}
	
	public ArrayList<VanLoadDO> getAllItemToVerify(int precision)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<VanLoadDO> vecVanLoadDOs = new ArrayList<VanLoadDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				sLiteDatabase   = DatabaseHelper.openDataBase();
				/*String strQuery = "SELECT INV.SellableQuantity/TF.Factor, INV.TotalQuantity/TF.Factor, INV.ItemCode, TP.Description, TP.UnitPerCase, TP.ItemBatchCode, " +
						          "TP.UOM, INV.BatchNumber,INV.ExpiryDate FROM " +
						          "tblProducts TP INNER JOIN tblVanStock INV ON TP.ItemCode= INV.ItemCode " +
						          "INNER JOIN tblUOMFactor TF ON TF.ItemCode = INV.ItemCode AND TF.UOM= TP.UOM WHERE round(INV.SellableQuantity/TF.Factor,3) > 0 ORDER BY INV.ItemCode";
//*/				
//				String strQuery ="SELECT  DISTINCT INV.SellableQuantity/TF.Factor, INV.TotalQuantity/TF.Factor, INV.ItemCode, TP.Description, "
//		+ "TP.UnitPerCase, TP.ItemBatchCode,TP.UOM, INV.BatchNumber,INV.ExpiryDate, (Select Sum(TMD.QuantityLevel1*TUF .Factor) "
//		+ "FROM  tblMovementDetail TMD Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  "
//		+ "where TMD.ItemCode =  INV.ItemCode  AND (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR TMD.MovementStatus = '99'))/TF.Factor "
//		+ "FROM tblProducts TP Left JOIN tblVanStock INV ON TP.ItemCode= INV.ItemCode  "
//		+ "INNER JOIN tblUOMFactor TF ON TF.ItemCode = INV.ItemCode AND "
//		+ "TF.UOM= TP.UOM WHERE round(INV.SellableQuantity/TF.Factor,3) > 0 ORDER BY INV.ItemCode";
				/*String strQuery ="SELECT  DISTINCT INV.SellableQuantity/TF.Factor, INV.TotalQuantity/TF.Factor, "
						+ "TP.ItemCode, TP.Description, TP.UnitPerCase, TP.ItemBatchCode,TP.UOM, INV.BatchNumber,"
						+ "INV.ExpiryDate, (Select Sum(TMD.QuantityLevel1*TUF .Factor) "
						+ "FROM  tblMovementDetail TMD "
						+ "Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  "
						+ "TUF.ItemCode = TMD.ItemCode  "
						+ "where TMD .ItemCode=TP.ItemCode "
						+ "AND (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR TMD.MovementStatus = '99' OR TMD.MovementStatus = '101') "
						+ "GROUP BY TMD .ItemCode)/TF.Factor AS PendingQty "
						+ "FROM tblProducts TP "
						+ "Left JOIN tblVanStock INV ON TP.ItemCode= INV.ItemCode "
						+ "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM= TP.UOM "
						+ "WHERE round(INV.SellableQuantity/TF.Factor,3) > 0 OR  PendingQty >0 ORDER BY INV.ItemCode";*/
				String strQuery="SELECT INV.SellableQuantity/TF.Factor,INV.TotalQuantity/TF.Factor, " +
						"TP.ItemCode, TP.Description, TP.UnitPerCase, TP.ItemBatchCode,TP.UOM, INV.BatchNumber," +
						"INV.ExpiryDate, (Select Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD " +
						"INNER JOIN tblMovementHeader  THD ON  " +
						"TMD.MovementCode =THD.MovementCode AND THD.MovementType='1' " +
						"Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  " +
						"where TMD .ItemCode=TP.ItemCode " +
						"AND (TMD.MovementStatus = '1'  OR " +
						"TMD.MovementStatus = 'Pending' OR TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' ) " +
						"GROUP BY TMD .ItemCode)/TF.Factor AS PendingQty, " +
						"TP.CategoryCode41, TP.CategoryCode42 " +
						"FROM tblProducts TP " +
						"Left JOIN tblVanStock INV ON TP.ItemCode= INV.ItemCode " +
						"INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM= TP.UOM " +
						"WHERE (ROUND(INV.SellableQuantity/TF.Factor,3) > 0 OR  PendingQty >0) ORDER BY INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							VanLoadDO loadDO 		= new VanLoadDO();
							loadDO.SellableQuantity	= cursor.getFloat(0);//(float) StringUtils.round(cursor.getString(0), precision);//cursor.getFloat(0);
							loadDO.TotalQuantity	= cursor.getFloat(1);//(float) StringUtils.round(cursor.getString(1), precision);//cursor.getFloat(1);	
							loadDO.ItemCode			= cursor.getString(2);
							loadDO.Description		= cursor.getString(3);
							loadDO.UnitsPerCases	= StringUtils.getInt(cursor.getString(4));
							loadDO.BatchCode		= cursor.getString(10)+"+"+cursor.getString(11);
							if(loadDO.BatchCode.contains("N/A"))
								loadDO.BatchCode="";
							loadDO.UOM				= cursor.getString(6);
							//loadDO.BatchCode		= cursor.getString(7);
							loadDO.ExpiryDate		= cursor.getString(8);
							loadDO.pendingQuantity  = cursor.getFloat(9);//(float) StringUtils.round(cursor.getString(9), precision);//cursor.getFloat(9);
							//float monUOMQuantity = cursor.getFloat(12);
							if(loadDO.UOM == null || loadDO.UOM.length() <= 0)
								loadDO.UOM = "PCS";
							
							/*if(loadDO.SellableQuantity<=0 && monUOMQuantity>0){
								loadDO.UOM = "PCS";
								loadDO.SellableQuantity = monUOMQuantity; 
							}*/
							
							if(loadDO.pendingQuantity >0 || loadDO.SellableQuantity >0)
								vecVanLoadDOs.add(loadDO);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace(); 
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vecVanLoadDOs;
		}
	}
	
	
	public Vector<DeliveryAgentOrderDetailDco> getAllItemToTransfer(String date, String sku)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<DeliveryAgentOrderDetailDco> vectorOrderList = new Vector<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				if(sku != null && sku.contains(","))
					sku = sku.substring(0, sku.lastIndexOf(","));
				
				sLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT INV.ItemCode,TP.Description,TP.UnitPerCase, TP.ItemType from  tblProducts TP,tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.ItemCode = INV.ItemCode AND INV.ItemCode NOT IN("+sku+") AND INV.availQty > 0 group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preUnits			= 	0; 
							OrderDetail.preCases			= 	0.0f;
							OrderDetail.itemCode 			= 	cursor.getString(0);
							OrderDetail.itemDescription		= 	cursor.getString(1);
							OrderDetail.unitPerCase			= 	cursor.getInt(2);
							OrderDetail.itemType			= 	cursor.getString(3);
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vectorOrderList;
		}
	}
	
	
	public ArrayList<DeliveryAgentOrderDetailDco> getAllItemToUpload(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
//				String strQuery 	= 	"SELECT SUM(INV.availQty),INV.ItemCode,TP.Description,TP.UnitsPerCases from  tblProducts TP,tblVMSalesmanInventory INV  where TP.SKU = INV.ItemCode group by INV.ItemCode";
				String strQuery 	= 	"SELECT SUM(INV.SecondaryQuantity), SUM(INV.PrimaryQuantity),INV.ItemCode,TP.Description,TP.UnitPerCase from  tblProducts TP,tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.ItemCode = INV.ItemCode AND uploadStatus='N' group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preUnits			= 	StringUtils.getInt(cursor.getString(0)); 
							OrderDetail.preCases			= 	StringUtils.getFloat(cursor.getString(1));
							OrderDetail.itemCode 			= 	cursor.getString(2);
							OrderDetail.itemDescription		= 	cursor.getString(3);
							OrderDetail.unitPerCase			= 	cursor.getInt(4);
//							OrderDetail.preUnits        	=   (int)Math.round(OrderDetail.preCases  * OrderDetail.unitPerCase);
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vectorOrderList;
		}
	}
	
	public Vector<UnUploadedDataDO> getAllItemUnUpload(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecUploadedDataDOs = new Vector<UnUploadedDataDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
//				String strQuery 	= 	"SELECT SUM(INV.availQty),INV.ItemCode,TP.Description,TP.UnitsPerCases from  tblProducts TP,tblVMSalesmanInventory INV  where TP.SKU = INV.ItemCode group by INV.ItemCode";
				String strQuery 	= 	"SELECT INV.VMSalesmanInventoryId, INV.uploadStatus from  tblProducts TP, tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.ItemCode = INV.ItemCode group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							UnUploadedDataDO uploadedDataDO	= new UnUploadedDataDO();
							uploadedDataDO.strId			= cursor.getString(0); 
							if(cursor.getString(1).equalsIgnoreCase("N"))
								uploadedDataDO.status = 0;
							else
								uploadedDataDO.status = 1;
							vecUploadedDataDOs.add(uploadedDataDO);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vecUploadedDataDOs;
		}
	}
	
	public ArrayList<DeliveryAgentOrderDetailDco> getAllItemToUnload(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT SUM(INV.SecondaryQuantity), SUM(INV.PrimaryQuantity), SUM(INV.availQty),INV.ItemCode,TP.Description, TP.UnitPerCase, INV.returnStatus from  tblProducts TP,tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.ItemCode = INV.ItemCode group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preUnits			= 	0; 
							OrderDetail.preCases			= 	0;
							OrderDetail.totalCases			= 	0;
							OrderDetail.availQty			= 	cursor.getFloat(2);
							OrderDetail.itemCode 			= 	cursor.getString(3);
							OrderDetail.itemDescription		= 	cursor.getString(4);
							OrderDetail.unitPerCase			= 	cursor.getInt(5);
							OrderDetail.returnStatus		= 	cursor.getString(6);
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vectorOrderList;
		}
	}
	
	public boolean getReturnstockStatus(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			boolean is = false;
			try
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
//				String strQuery 	= 	"SELECT SUM(INV.availQty),INV.ItemCode,TP.Description,TP.UnitsPerCases from  tblProducts TP,tblVMSalesmanInventory INV  where TP.SKU = INV.ItemCode group by INV.ItemCode";
				String strQuery 	= 	"SELECT returnStatus from  tblVMSalesmanInventory where Date like '%"+date+"%'";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							if(cursor.getString(0).equalsIgnoreCase("Y"))
								is = true;
							else
								is = false;
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return is;
		}
	}
	public ArrayList<DeliveryAgentOrderDetailDco> getAllItemToVarify(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				sLiteDatabase = DatabaseHelper.openDataBase();
//				String strQuery 	= 	"SELECT SUM(INV.availQty),INV.ItemCode,TP.Description,TP.UnitsPerCases from  tblProducts TP,tblVMSalesmanInventory INV  where TP.SKU = INV.ItemCode group by INV.ItemCode";
//				String strQuery 	= 	"SELECT SUM(INV.SecondaryQuantity), SUM(INV.PrimaryQuantity),INV.ItemCode,TP.Description, TP.UnitPerCase from  tblProducts TP,tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.SKU = INV.ItemCode group by INV.ItemCode";
				String strQuery 	= 	"SELECT INV.returnQty ,INV.ItemCode,TP.Description, TP.UnitPerCase from  tblProducts TP,tblVMSalesmanInventory INV  where INV.Date like '%"+date+"%' and  TP.ItemCode = INV.ItemCode group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preCases			= 	StringUtils.getFloat(cursor.getString(0)); 
							OrderDetail.itemCode 			= 	cursor.getString(1);
							OrderDetail.itemDescription		= 	cursor.getString(2);
							OrderDetail.unitPerCase			= 	cursor.getInt(3);
							OrderDetail.preUnits			= 	(int)(OrderDetail.preCases * OrderDetail.unitPerCase); 
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vectorOrderList;
		}
	}
	
	public boolean updateVerificationStatus(String status, String strDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String strUpdate = "Update tblVMSalesmanInventory  set IsAllVerified=? where Date like '"+strDate+"%'";
//				String strUpdate = "Update tblVMSalesmanInventory  set IsAllVerified=?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);
				stmtUpdateCustomer.bindString(1, status);
				stmtUpdateCustomer.execute();
				stmtUpdateCustomer.close();
				return true;
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
		}
	}
	
	public boolean updateReturnstock(ArrayList<DeliveryAgentOrderDetailDco> vecOrDDcos, String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "Update tblVMSalesmanInventory  set returnPrimary=?, returnSec=?, returnQty=? where ItemCode =?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);
				
				for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrDDcos)
				{
					stmtUpdateCustomer.bindString(1, ""+deliveryAgentOrderDetailDco.preCases);
					stmtUpdateCustomer.bindString(2, ""+deliveryAgentOrderDetailDco.preUnits);
					stmtUpdateCustomer.bindString(3, ""+deliveryAgentOrderDetailDco.totalCases);
					stmtUpdateCustomer.bindString(4, deliveryAgentOrderDetailDco.itemCode);
					stmtUpdateCustomer.execute();
				}
				stmtUpdateCustomer.close();
				return true;
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
		}
	}
	
	public boolean updateReturnstockStatus(ArrayList<DeliveryAgentOrderDetailDco> vecOrDDcos, String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "Update tblVMSalesmanInventory  set returnStatus=? where SalesmanCode =? AND ItemCode =?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);
				
				for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrDDcos)
				{
					stmtUpdateCustomer.bindString(1, "Y");
					stmtUpdateCustomer.bindString(2, empNo);
					stmtUpdateCustomer.bindString(3, deliveryAgentOrderDetailDco.itemCode);
					stmtUpdateCustomer.execute();
				}
				stmtUpdateCustomer.close();
				return true;
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
		}
	}
	
	public boolean updateReturnstockPostStatus(ArrayList<DeliveryAgentOrderDetailDco> vecOrDDcos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "Update tblReturnInventory  set postStatus=? where ItemCode =?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);
				
				for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrDDcos)
				{
					stmtUpdateCustomer.bindLong(1, 1);
					stmtUpdateCustomer.bindString(2, deliveryAgentOrderDetailDco.itemCode);
					stmtUpdateCustomer.execute();
				}
				stmtUpdateCustomer.close();
				return true;
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
		}
	}
	
	public boolean updateInventoryUnload(ArrayList<DeliveryAgentOrderDetailDco> vector)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET availQty = ?,totalQty=? WHERE ItemCode = ?");
				 
				for(DeliveryAgentOrderDetailDco inventoryObject : vector)
				{
					float totalQty = 0, availQty = 0;
					Cursor cursor  = objSqliteDB.rawQuery("SELECT totalQty, availQty FROM tblVMSalesmanInventory WHERE ItemCode = '"+inventoryObject.itemCode+"'", null);
					if(cursor.moveToFirst())
					{
						totalQty  	= 	 cursor.getFloat(0);
						availQty 	=	 cursor.getFloat(1);
						totalQty 	= 	 totalQty - inventoryObject.totalCases;
						availQty 	= 	 availQty - inventoryObject.totalCases;
						
						if(totalQty < 0)
							totalQty = 0;
						
						if(availQty < 0)
							availQty = 0;
						
						stmtUpdate.bindString(1, ""+availQty);
						stmtUpdate.bindString(2, ""+totalQty);
						stmtUpdate.bindString(3, inventoryObject.itemCode);
						stmtUpdate.execute();
					}
					if(cursor != null && !cursor.isClosed())
						cursor.close();
				}
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	public ArrayList<DeliveryAgentOrderDetailDco> getAllRecomendedItemToVerify(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			
			try
			{
				/*INV.Date like '%"+date+"%' and */
				sLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT SUM(INV.PrimaryQuantity), SUM(INV.SecondaryQuantity), INV.ItemCode,TP.Description,TP.UnitPerCase, TP.UOM, PT.PRICECASES, TP.ExpiryDate, TP.BatchCode, INV.totalQty, TP.SECONDARY_UOM from  tblProducts TP,tblVMSalesmanInventoryRec INV, tblPricing PT where PT.ITEMCODE =INV.ItemCode AND TP.ItemCode = INV.ItemCode group by INV.ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preCases			= 	StringUtils.getInt(cursor.getString(0)); 
							OrderDetail.preUnits			= 	StringUtils.getInt(cursor.getString(1));
							OrderDetail.itemCode 			= 	cursor.getString(2);
							OrderDetail.itemDescription		= 	cursor.getString(3);
							OrderDetail.unitPerCase			= 	cursor.getInt(4);
							OrderDetail.strUOM				= 	cursor.getString(5);
							OrderDetail.unitSellingPrice	= 	cursor.getString(6);
							OrderDetail.expiryDate			= 	cursor.getString(7);
							OrderDetail.itemBatchCode		= 	cursor.getString(8);
							OrderDetail.secondaryUOM		= 	cursor.getString(10);
							
							if(OrderDetail.secondaryUOM == null || OrderDetail.secondaryUOM.length() <= 0)
								OrderDetail.secondaryUOM = "PCS";
							
							if(StringUtils.getInt(getWMQty(OrderDetail.itemCode, sLiteDatabase)) == 0)
								OrderDetail.totalQtyShiped		= 	1000+"";
							else
								OrderDetail.totalQtyShiped		= 	StringUtils.getInt(getWMQty(OrderDetail.itemCode, sLiteDatabase))+"";
							OrderDetail.invoiceAmount 		=   StringUtils.getFloat(OrderDetail.unitSellingPrice) * OrderDetail.preUnits;
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return vectorOrderList;
		}
	}
	
	public String getWMQty (String itemCode, SQLiteDatabase sLiteDatabase)
	{
		String str = "";
		Cursor cursor = null;
		try
		{
			if(sLiteDatabase == null)
				sLiteDatabase = DatabaseHelper.openDataBase();
			String strQuery = "SELECT totalQty FROM tblVMSalesmanInventory where ItemCode = '"+itemCode+"'";
			cursor = sLiteDatabase.rawQuery(strQuery, null);
			if(cursor.moveToFirst())
			{
				str = cursor.getString(0); 
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
		}
		return str;
	}
	public String getVehicleNo(String empid) {
		SQLiteDatabase sLiteDatabase = null;
		String str = "";
		Cursor cursor = null;
		try
		{
			if(sLiteDatabase == null)
				sLiteDatabase = DatabaseHelper.openDataBase();
			String strQuery = "SELECT VEHICLE_NO FROM tblVehicle where EMPNO = '"+empid+"'";
			cursor = sLiteDatabase.rawQuery(strQuery, null);
			if(cursor.moveToFirst())
			{
				str = cursor.getString(0); 
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
		}
		return str;
		
	}
}
