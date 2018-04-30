package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.NonSellableItemDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VanStockDO;
import com.winit.baskinrobbin.salesman.dataobject.VerifyRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.WareHouseStockDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;


public class InventoryDA 
{
	OfflineDA offlineDA=new OfflineDA();
	
	public boolean insertMovementDetailsDetail(HashMap<String, LoadRequestDO> hashMap,Vector<VanStockDO> vecVanStockDOs,Vector<NonSellableItemDO> vecNonSellableItemDO,Vector<WareHouseStockDO> vecWStockDO){
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.beginTransaction();
				insertLoadRequests(objSqliteDB,hashMap);
				insertVanLoad(objSqliteDB,vecVanStockDOs);
				insertUpdateNonSaleableStock(objSqliteDB,vecNonSellableItemDO);
				insertWareHouseQty(objSqliteDB,vecWStockDO);
				objSqliteDB.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
				result=false;
			}
			finally{
				if(objSqliteDB!=null){
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			
			return result;}
	}
	public int getPendingStatus(String moveMentId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			int status = -1;
			String query= "SELECT MovementStatus FROM tblMovementHeader WHERE MovementCode ='"+moveMentId+"'";
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				cursor 		   = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					if(cursor.getString(0).equalsIgnoreCase("Pending"))
						status = 0;
					else if(cursor.getString(0).equalsIgnoreCase("Rejected"))
						status = 2;
					else if(cursor.getString(0).equalsIgnoreCase("Approved"))
						status = 1;
					else
						status = 3;
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return status;
		}
	}
	public boolean updateMovemetStatuStatus(String movementId, int Status, ArrayList<VanLoadDO> vecVanLodDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(Status == 100)
				{
					String strUpdate = "UPDATE tblMovementHeader SET MovementStatus = ? WHERE MovementCode=?";
					String strUpdateMovementDetail = "UPDATE tblMovementDetail SET MovementStatus = ?,InProcessQuantity = ?,ShippedQuantity = ? WHERE MovementCode=? and ItemCode=? AND BatchNumber=? AND UOM=?";
					SQLiteStatement stmtUpdateMovement		= objSqliteDB.compileStatement(strUpdate);
					stmtUpdateMovement.bindString(1, Status+"");
					stmtUpdateMovement.bindString(2, movementId);
					stmtUpdateMovement.execute();
					SQLiteStatement stmtUpdateMovementDetal	= objSqliteDB.compileStatement(strUpdateMovementDetail);
					for(VanLoadDO vanLoadDO:vecVanLodDOs){
						stmtUpdateMovementDetal.bindDouble(1, Status);
						stmtUpdateMovementDetal.bindDouble(2, vanLoadDO.inProccessQty);
						stmtUpdateMovementDetal.bindDouble(3, vanLoadDO.ShippedQuantity);
						stmtUpdateMovementDetal.bindString(4, movementId);
						stmtUpdateMovementDetal.bindString(5, vanLoadDO.ItemCode);
						stmtUpdateMovementDetal.bindString(6, vanLoadDO.BatchCode);
						stmtUpdateMovementDetal.bindString(7, vanLoadDO.UOM);
						stmtUpdateMovementDetal.execute();
					}
				}
				else
				{
					String strUpdate = "UPDATE tblMovementHeader SET MovementStatus = ? WHERE MovementCode=?";
					String strUpdateMovementDetail = "UPDATE tblMovementDetail SET MovementStatus = ?,ShippedQuantity = ? WHERE MovementCode=? and ItemCode=? AND BatchNumber=? AND UOM=?";
					SQLiteStatement stmtUpdateMovement		= objSqliteDB.compileStatement(strUpdate);
					stmtUpdateMovement.bindString(1, Status+"");
					stmtUpdateMovement.bindString(2, movementId);
					stmtUpdateMovement.execute();
					SQLiteStatement stmtUpdateMovementDetal	= objSqliteDB.compileStatement(strUpdateMovementDetail);
					for(VanLoadDO vanLoadDO:vecVanLodDOs){
						stmtUpdateMovementDetal.bindDouble(1, Status);
						stmtUpdateMovementDetal.bindDouble(2, vanLoadDO.ShippedQuantity);
						stmtUpdateMovementDetal.bindString(3, movementId);
						stmtUpdateMovementDetal.bindString(4, vanLoadDO.ItemCode);
						stmtUpdateMovementDetal.bindString(5, vanLoadDO.BatchCode);
						stmtUpdateMovementDetal.bindString(6, vanLoadDO.UOM);
						stmtUpdateMovementDetal.execute();
					}
				}
				
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
	
	
	public boolean updateMovemetStatuStatus(String movementId,int loadType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			boolean result=true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.beginTransaction();
				String strUpdate = "UPDATE tblMovementHeader SET IsVerified='1' WHERE MovementCode='"+movementId+"'";
				String selectItems="SELECT ItemCode FROM tblMovementDetail where movementCode='"+movementId+"'";
				String strDelete="DELETE FROM tblNonSellableItems WHERE ItemCode = ?";
				
				SQLiteStatement stmtUpdateMovement		= objSqliteDB.compileStatement(strUpdate);
				stmtUpdateMovement.execute();
				if(loadType==AppStatus.UNLOAD_STOCK){
					cursor 		   = objSqliteDB.rawQuery(selectItems, null);
					if(cursor.moveToFirst()){
						do{
							String id=cursor.getString(0);
							SQLiteStatement deletItem		= objSqliteDB.compileStatement(strDelete);
							deletItem.bindString(1, cursor.getString(0));
							deletItem.execute();
						}while (cursor.moveToNext());
					}
					cursor.close();
				}
				objSqliteDB.setTransactionSuccessful();
				
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				result=false;
			}
			finally
			{
				if(objSqliteDB != null){
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
	public void insertUpdateInventory(LoadRequestDO loadRequestDO , int load_type, Vector<NameIDDo> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty, totalQty ) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectU		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
				SQLiteStatement stmtUpdateU 	= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,SecondaryQuantity = ?, totalQty = ? WHERE ItemCode = ?");
				
				for(NameIDDo object : vec)
				{
					stmtSelectU.bindString(1, object.strName);
					long countRec = stmtSelectU.simpleQueryForLong();
					
					if(countRec != 0)
					{	
						float lastUnits = 0, totalUnits = 0, availUnits = 0;
						Cursor cursor   = objSqliteDB.rawQuery("SELECT SI.SecondaryQuantity,SI.totalQty, SI.availQty " +
								          "FROM tblVMSalesmanInventory SI WHERE SI.ItemCode = '"+object.strName+"'", null);
						
						if(cursor.moveToFirst())
						{
							lastUnits  	= 	cursor.getFloat(0);
							totalUnits 	= 	cursor.getFloat(1);
							availUnits 	= 	cursor.getFloat(2);
							
							if(load_type == AppStatus.LOAD_STOCK)
							{
								lastUnits  = 	lastUnits  + StringUtils.getFloat(object.strType);
								totalUnits = 	totalUnits + StringUtils.getFloat(object.strType);
								availUnits = 	availUnits + StringUtils.getFloat(object.strType);
							}
							else
							{
								lastUnits  = 	lastUnits  - StringUtils.getFloat(object.strType);
								totalUnits = 	totalUnits - StringUtils.getFloat(object.strType);
								availUnits = 	availUnits - StringUtils.getFloat(object.strType);
							}
							
							lastUnits   = lastUnits  < 0 ? 0 : lastUnits;
							totalUnits  = totalUnits < 0 ? 0 : totalUnits;
							availUnits  = availUnits < 0 ? 0 : availUnits;
							
							stmtUpdateU.bindString(1, ""+lastUnits);
							stmtUpdateU.bindString(2, ""+availUnits);
							stmtUpdateU.bindString(3, ""+lastUnits);
							stmtUpdateU.bindString(4, ""+totalUnits);
							stmtUpdateU.bindString(5, object.strName);
							
							stmtUpdateU.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
					else if(load_type == AppStatus.LOAD_STOCK)
					{
						stmtInsert.bindString(1, loadRequestDO.MovementCode);
						stmtInsert.bindString(2, loadRequestDO.MovementDate);
						stmtInsert.bindString(3, loadRequestDO.UserCode);
						stmtInsert.bindString(4, object.strName);
						stmtInsert.bindString(5, ""+object.strType);
						stmtInsert.bindString(6, ""+object.strType);
						stmtInsert.bindString(7, "false");
						stmtInsert.bindString(8, ""+object.strType);
						stmtInsert.bindString(9, ""+(object.strType));
						stmtInsert.executeInsert();
					}
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
	
	public HashMap<String, ArrayList<LoadRequestDO>> getAllRequestMapByType(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, ArrayList<LoadRequestDO>> hmRequests = null;
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			Cursor cursor = null;
			String query="";
			SQLiteDatabase sqLiteDatabase = null;
			if(type.equalsIgnoreCase(""+AppStatus.UNLOAD_S_STOCK))
					query= "SELECT DISTINCT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId,Status," +
							"SourceVehicleCode,DestinationVehicleCode,IsVerified "
					+ "FROM tblMovementHeader WHERE MovementType ='"+type+"' OR MovementType ='"+AppStatus.UNLOAD_COLLECTED_STOCK+"' order by ModifiedOn DESC";
			else
				query= "SELECT DISTINCT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId,Status,SourceVehicleCode,DestinationVehicleCode,IsVerified "
						+ "FROM tblMovementHeader WHERE MovementType ='"+type+"' order by ModifiedOn DESC";

			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				hmRequests=new HashMap<String, ArrayList<LoadRequestDO>>();
				if(cursor.moveToFirst())
				{
					do
					{
						LoadRequestDO loadRequestDO = 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus=cursor.getString(3);
						loadRequestDO.IsVarified=cursor.getString(8);//new changes
						
						if(loadRequestDO.IsVarified!=null && loadRequestDO.IsVarified.equalsIgnoreCase("1")){
							loadRequestDO.MovementStatus=""+loadRequestDO.MOVEMENT_STATUS_PENDING_FROM_ERP;
						}
						else if(loadRequestDO.MovementType.equalsIgnoreCase(""+AppStatus.UNLOAD_COLLECTED_STOCK) && loadRequestDO.MovementStatus.equalsIgnoreCase(""+loadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY) )
							loadRequestDO.MovementStatus=""+loadRequestDO.MOVEMENT_STATUS_APPROVED_FROM_EBS;
						//this is to set them in page
						if(loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED)
								||loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_FROM_ERP)
								||loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_PENDING_FROM_ERP))
							loadRequestDO.temStatus=LoadRequestDO.STATUS_PENDING_MW;
						//This is to show the status in sting format
						loadRequestDO.MovementStatus=loadRequestDO.getMovementStatus(StringUtils.getInt(loadRequestDO.MovementStatus));
						
						loadRequestDO.AppMovementId =	cursor.getString(4);
						loadRequestDO.Status 		=	cursor.getString(5);
						loadRequestDO.SourceVehicleCode	=	cursor.getString(6);
						loadRequestDO.DestinationVehicleCode=cursor.getString(7);
						
						if(TextUtils.isEmpty(loadRequestDO.Status))
							loadRequestDO.Status = "";
						vectLoadRequestDO= hmRequests.get(loadRequestDO.temStatus);
						if(vectLoadRequestDO==null){
							vectLoadRequestDO=new ArrayList<LoadRequestDO>();
							vectLoadRequestDO.add(loadRequestDO);
							hmRequests.put(loadRequestDO.temStatus, vectLoadRequestDO);
						}
						else
						vectLoadRequestDO.add(loadRequestDO);
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return hmRequests;
		}
	}
	
	public int getApprovedCount(){

		synchronized(MyApplication.MyLock) 
		{
		    int count=0;
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT COUNT(*) FROM tblMovementHeader WHERE MovementType ='1' AND MovementStatus = 101 or MovementStatus='Approved' order by ModifiedOn DESC";
					  

			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					count=cursor.getInt(0);
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return count;
		}
	
	}
	public ArrayList<LoadRequestDO> getAllApproved(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId,Status,SourceVehicleCode FROM tblMovementHeader WHERE MovementType ='"+type+"' AND MovementStatus = 101 or MovementStatus='Approved' order by ModifiedOn DESC";
					  

			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						LoadRequestDO loadRequestDO = 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus=loadRequestDO.getMovementStatus(StringUtils.getInt(cursor.getString(3)));
						/*if(cursor.getString(3).equalsIgnoreCase("101"))
							loadRequestDO.MovementStatus =	"Approved from EBS";*/
						loadRequestDO.AppMovementId =	cursor.getString(4);
						loadRequestDO.Status 		=	cursor.getString(5);
						loadRequestDO.SourceVehicleCode	=	cursor.getString(6);
						
						if(TextUtils.isEmpty(loadRequestDO.Status))
							loadRequestDO.Status = "";
						
						vectLoadRequestDO.add(loadRequestDO);
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectLoadRequestDO;
		}
	}
	public int getPendingListCount(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			int count=0;
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT COUNT(*) FROM tblMovementHeader WHERE MovementType ='"+type+"' AND MovementStatus = 99";
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
						count=cursor.getInt(0);
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return count;
		}
	}
	public int getEbsAprovedListCount(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			int count=0;
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT COUNT(*) FROM tblMovementHeader WHERE MovementType ='"+type+"' AND MovementStatus = 101 or MovementStatus='Approved'";
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
						count=cursor.getInt(0);
				}
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return count;
		}
	}
	
	public ArrayList<LoadRequestDO> getAllRequestByType(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId,Status,SourceVehicleCode FROM tblMovementHeader WHERE MovementType ='"+type+"' order by ModifiedOn DESC";
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						LoadRequestDO loadRequestDO = 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus=loadRequestDO.getMovementStatus(StringUtils.getInt(cursor.getString(3)));
						/*if(cursor.getString(3).equalsIgnoreCase("100"))
							loadRequestDO.MovementStatus =	"Collected";
						else if(cursor.getString(3).equalsIgnoreCase("101"))
							loadRequestDO.MovementStatus =	"Approved";
						else if(cursor.getString(3).equalsIgnoreCase("99"))
							loadRequestDO.MovementStatus =	"Pending";
						
						else if(cursor.getString(3).equalsIgnoreCase("-13"))
							loadRequestDO.MovementStatus =	"Rejected";
						
						else if(cursor.getString(3).equalsIgnoreCase("1"))
							loadRequestDO.MovementStatus =	"Pending";
						
						else
							loadRequestDO.MovementStatus =	cursor.getString(3);*/
						
						loadRequestDO.AppMovementId =	cursor.getString(4);
						loadRequestDO.Status 		=	cursor.getString(5);
						loadRequestDO.SourceVehicleCode	=	cursor.getString(6);
						
						if(TextUtils.isEmpty(loadRequestDO.Status))
							loadRequestDO.Status = "";
						
						vectLoadRequestDO.add(loadRequestDO);
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectLoadRequestDO;
		}
	}
	
	public HashMap<String,ArrayList<LoadRequestDO>> getAllNewRequestByType(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId,Status,SourceVehicleCode FROM tblMovementHeader WHERE MovementType ='"+type+"' order by ModifiedOn DESC";
			HashMap<String,ArrayList<LoadRequestDO>> hmList = new HashMap<String, ArrayList<LoadRequestDO>>();
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						LoadRequestDO loadRequestDO = 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						String key = "History";
						loadRequestDO.MovementStatus=loadRequestDO.getMovementStatus(StringUtils.getInt(cursor.getString(3)));
						/*if(cursor.getString(3).equalsIgnoreCase("100"))
							loadRequestDO.MovementStatus =	"Approved";
						
						else if(cursor.getString(3).equalsIgnoreCase("99"))
							loadRequestDO.MovementStatus =	"Pending from EBS";
						
						else if(cursor.getString(3).equalsIgnoreCase("-13"))
							loadRequestDO.MovementStatus =	"Rejected";
						
						else*/ if(loadRequestDO.MovementStatus.equalsIgnoreCase("Pending")){
							key =	"Pending";
						}
						/*else
							loadRequestDO.MovementStatus =	cursor.getString(3);*/
						
						loadRequestDO.AppMovementId =	cursor.getString(4);
						loadRequestDO.Status 		=	cursor.getString(5);
						loadRequestDO.SourceVehicleCode	=	cursor.getString(6);
						
						if(TextUtils.isEmpty(loadRequestDO.Status))
							loadRequestDO.Status = "";
						
						if(hmList.containsKey(key)){
							vectLoadRequestDO = hmList.get(key);
						}
						
						vectLoadRequestDO.add(loadRequestDO);
						hmList.put(key, vectLoadRequestDO);
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return hmList;
		}
	}
	
	public ArrayList<VanLoadDO> getAllItemToVerifyByLoadId(String loadId,int movementType/*,HashMap<String, HHInventryQTDO> hmInventory*/) 
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<VanLoadDO> vectorOrderList = new ArrayList<VanLoadDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			/*INV.Date like '%"+date+"%' AND */
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT ItemCode,ItemDescription, UOM, QuantityLevel1,ShippedQuantity, MovementReasonCode,ExpiryDate,InProcessQuantity " +
										"FROM tblMovementDetail WHERE MovementCode = '"+loadId+"' ORDER BY ItemCode";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							VanLoadDO OrderDetail			= 	new VanLoadDO();
							OrderDetail.ItemCode 			= 	cursor.getString(0);
							OrderDetail.Description			= 	cursor.getString(1);
							OrderDetail.UOM					= 	cursor.getString(2);
							OrderDetail.SellableQuantity	= 	cursor.getFloat(3); 
							OrderDetail.ShippedQuantity	= 	cursor.getFloat(4); 
							//OrderDetail.ShippedQuantity		= 	OrderDetail.SellableQuantity;//cursor.getFloat(4); 
							OrderDetail.reason				= 	cursor.getString(5);
							OrderDetail.ExpiryDate			= 	cursor.getString(6);
							OrderDetail.inProccessQty       = 	cursor.getFloat(7);
							
							//if(movementType == AppStatus.UNLOAD_STOCK)
								//isInventoryAvail(OrderDetail,hmInventory);
							
							//if(OrderDetail.inProccessQty>0)
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
	
	
	private void isInventoryAvail(VanLoadDO objItem,HashMap<String, HHInventryQTDO> hmInventory)
	{
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.ItemCode))
		{
			float availQty = hmInventory.get(objItem.ItemCode).totalQt;
			if(objItem.inProccessQty> availQty)
				objItem.inProccessQty = availQty;
		}
		else
			objItem.inProccessQty = 0;
	}
	
	
	public boolean insertLoadRequest_(LoadRequestDO loadRequestDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				objSqliteDB.beginTransaction();
			
				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementHeader WHERE MovementCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						 														 "(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						 														 "MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						 														 ",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						 														 "IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn, ProductType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						 														 "MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						 														 ",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						 														 "IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?, ProductType= ? WHERE MovementCode=?");
				 
				
				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=? AND UOM=?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
																				"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
																				"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
																				"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
																				"ShippedQuantity,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
																				"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
																				"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
																				"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
																				"ShippedQuantity=?,ModifiedOn=? WHERE MovementCode=? AND ItemCode=? AND UOM=?");
				
				if(loadRequestDO != null)
				{
					stmtSelectR.bindString(1, loadRequestDO.MovementCode);
					long count = stmtSelectR.simpleQueryForLong();
					
					if(count > 0)
					{	
						stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
						stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
						stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
						stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
						stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
						stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
						stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
						stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
						stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
						stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
						stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
						stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
						stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
						stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);
						
						stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
						stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
						stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
						stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
						stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
						stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
						stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
						stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);
						
						stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
						stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
						stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
						stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
						stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
						stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
						stmtUpdateR.bindString(29, ""+loadRequestDO.ProductType);
						stmtUpdateR.bindString(30, ""+loadRequestDO.MovementCode);
						stmtUpdateR.execute();
					}
					else
					{
						stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
						stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
						stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
						stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
						stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
						stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
						stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
						stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
						stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
						stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
						stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
						stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
						stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
						stmtInsertR.bindString(14, ""+loadRequestDO.Status);
						stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);
						
						stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
						stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
						stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
						stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
						stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
						stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
						stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
						stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);
						
						stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
						stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
						stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
						stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
						stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
						stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
						stmtInsertR.bindString(30, ""+loadRequestDO.ProductType);
						stmtInsertR.executeInsert();
					}
					
					for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
					{
						stmtSelectD.bindString(1, inventoryObject.MovementCode);
						stmtSelectD.bindString(2, inventoryObject.ItemCode);
						stmtSelectD.bindString(3, inventoryObject.UOM);
						long countRec = stmtSelectD.simpleQueryForLong();
							
						if(!loadRequestDO.MovementType.equalsIgnoreCase(""+AppStatus.UNLOAD_STOCK) && countRec != 0)
						{	
							stmtUpdateD.bindString(1, ""+inventoryObject.LineNo);
							stmtUpdateD.bindString(2, inventoryObject.OrgCode);
							stmtUpdateD.bindString(3, inventoryObject.ItemDescription);
							stmtUpdateD.bindString(4, ""+inventoryObject.ItemAltDescription);
							stmtUpdateD.bindString(5, inventoryObject.MovementStatus);
							stmtUpdateD.bindString(6, inventoryObject.UOM);
							stmtUpdateD.bindString(7, ""+inventoryObject.QuantityLevel1);
							stmtUpdateD.bindString(8, ""+inventoryObject.QuantityLevel2);
							stmtUpdateD.bindString(9, ""+inventoryObject.QuantityLevel3);
							stmtUpdateD.bindString(10, ""+inventoryObject.QuantityBU);
							stmtUpdateD.bindString(11, ""+inventoryObject.QuantitySU);
							stmtUpdateD.bindString(12, ""+inventoryObject.NonSellableQty);
							
							stmtUpdateD.bindString(13, ""+inventoryObject.CurrencyCode);
							stmtUpdateD.bindString(14, ""+inventoryObject.PriceLevel1);
							stmtUpdateD.bindString(15, ""+inventoryObject.PriceLevel2);
							stmtUpdateD.bindString(16, ""+inventoryObject.PriceLevel3);
							stmtUpdateD.bindString(17, inventoryObject.MovementReasonCode);
							stmtUpdateD.bindString(18, inventoryObject.ExpiryDate);
							stmtUpdateD.bindString(19, ""+inventoryObject.Note);
							stmtUpdateD.bindString(20, inventoryObject.AffectedStock);
							stmtUpdateD.bindString(21, loadRequestDO.Status);
							
							stmtUpdateD.bindString(22, ""+inventoryObject.DistributionCode);
							stmtUpdateD.bindString(23, inventoryObject.CreatedOn);
							stmtUpdateD.bindString(24, loadRequestDO.ModifiedDate);
							stmtUpdateD.bindString(25, ""+inventoryObject.ModifiedTime);
							stmtUpdateD.bindString(26, inventoryObject.PushedOn);
							stmtUpdateD.bindString(27, ""+inventoryObject.CancelledQuantity);
							
							stmtUpdateD.bindString(28, ""+inventoryObject.InProcessQuantity);
							stmtUpdateD.bindString(29, ""+inventoryObject.ShippedQuantity);
							stmtUpdateD.bindString(30, loadRequestDO.ModifiedOn);
							stmtUpdateD.bindString(31, loadRequestDO.MovementCode);
							stmtUpdateD.bindString(32, inventoryObject.ItemCode);
							stmtUpdateD.bindString(33, inventoryObject.UOM);
							stmtUpdateD.execute();
						}
						else
						{
							stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
							stmtInsertD.bindString(2, loadRequestDO.MovementCode);
							stmtInsertD.bindString(3, inventoryObject.ItemCode);
							stmtInsertD.bindString(4, inventoryObject.OrgCode);
							stmtInsertD.bindString(5, inventoryObject.ItemDescription);
							stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
							stmtInsertD.bindString(7, inventoryObject.MovementStatus);
							stmtInsertD.bindString(8, inventoryObject.UOM);
							stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
							stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
							stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
							stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
							stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
							stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
							stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
							stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
							stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
							stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
							stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
							stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
							stmtInsertD.bindString(21, ""+inventoryObject.Note);
							stmtInsertD.bindString(22, inventoryObject.AffectedStock);
							stmtInsertD.bindString(23, loadRequestDO.Status);
						
							stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
							stmtInsertD.bindString(25, inventoryObject.CreatedOn);
							stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
							stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
							stmtInsertD.bindString(28, inventoryObject.PushedOn);
							stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);
							
							stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
							stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
							stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);
							stmtInsertD.executeInsert();
						}
					}
				}
				
				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
				
				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
				objSqliteDB.setTransactionSuccessful();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null){
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
					
			}
			return result;
		}
	}
	
	public boolean updateInprocess(LoadRequestDO loadRequestDO){

		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean isSuccess=true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET InProcessQuantity=? WHERE MovementCode=? AND ItemCode=?");

				if(loadRequestDO != null && loadRequestDO.vecItems!=null && loadRequestDO.vecItems.size()>0 )
				{
					for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
					{
						stmtUpdateD.bindString(1, ""+inventoryObject.InProcessQuantity);
						stmtUpdateD.bindString(2, inventoryObject.MovementCode);
						stmtUpdateD.bindString(3, inventoryObject.ItemCode);
						stmtUpdateD.execute();
					}
					stmtUpdateD.close();
				}
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				isSuccess= false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return isSuccess;
		}
	
	}
	
	public void insertLoadRequests(SQLiteDatabase objSqliteDB,HashMap<String, LoadRequestDO> hashMap)
	{
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						 														 "(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						 														 "MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						 														 ",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						 														 "IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn,IsVerified) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						 														 "MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						 														 ",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						 														 "IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?,IsVerified=? WHERE MovementCode=?");
				 
				
//				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=? AND UOM = ?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
																				"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
																				"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
																				"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
																				"ShippedQuantity,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
																				"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
																				"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
																				"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
																				"ShippedQuantity=?,ModifiedOn=? WHERE MovementCode=? AND ItemCode=? AND UOM = ?");
				
				
//				SQLiteStatement stmtSelectDNS 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=? AND UOM = ? AND ExpiryDate = ? AND MovementReasonCode = ?");
				SQLiteStatement stmtInsertDNS 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
																				"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
																				"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
																				"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
																				"ShippedQuantity,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateDNS 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
																				"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
																				"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
																				"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
																				"ShippedQuantity=?,ModifiedOn=? WHERE MovementCode=? AND ItemCode=? AND UOM = ? AND ExpiryDate = ? AND MovementReasonCode = ?");
				
				if(hashMap != null && hashMap.size() >0)
				{
					Set<String> keys = hashMap.keySet();
					for (String string : keys)
					{
						LoadRequestDO loadRequestDO = hashMap.get(string);
						if(loadRequestDO != null)
						{
//							stmtSelectR.bindString(1, loadRequestDO.MovementCode);
//							long count = stmtSelectR.simpleQueryForLong();
							
//							if(count > 0)
//							{	
								stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
								stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
								stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
								stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
								stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
								stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
								stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
								stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
								stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
								stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
								stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
								stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
								stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
								stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);
								
								stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
								stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
								stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
								stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
								stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
								stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
								stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
								stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);
								
								stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
								stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
								stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
								stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
								stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
								stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
								stmtUpdateR.bindString(29, ""+loadRequestDO.IsVarified);
								stmtUpdateR.bindString(30, ""+loadRequestDO.MovementCode);
//								stmtUpdateR.execute();
//							}
//							else
							if(stmtUpdateR.executeUpdateDelete()<=0)
							{
								stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
								stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
								stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
								stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
								stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
								stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
								stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
								stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
								stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
								stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
								stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
								stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
								stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
								stmtInsertR.bindString(14, ""+loadRequestDO.Status);
								stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);
								
								stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
								stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
								stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
								stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
								stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
								stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
								stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
								stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);
								
								stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
								stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
								stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
								stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
								stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
								stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
								stmtInsertR.bindString(30, ""+loadRequestDO.IsVarified);
								stmtInsertR.executeInsert();
							}
							if(StringUtils.getInt(loadRequestDO.MovementType) == AppStatus.UNLOAD_STOCK)
								insertDetailNonS(loadRequestDO,/* stmtSelectDNS, */stmtUpdateDNS, stmtInsertDNS);
							else
								insertDetail(loadRequestDO,/* stmtSelectD,*/ stmtUpdateD, stmtInsertD);
						}
					}
				stmtInsertR.close();
				stmtUpdateR.close();
				stmtInsertD.close();
				stmtUpdateD.close();
	}
	}
	
				private void insertDetailNonS(LoadRequestDO loadRequestDO,SQLiteStatement stmtUpdateDNS, SQLiteStatement stmtInsertDNS) {
		for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
		{
			String expirydate = inventoryObject.ExpiryDate;
			
			if(expirydate.contains("T"))
				expirydate = expirydate.substring(0, expirydate.lastIndexOf("T"));
				
//			stmtSelectD.bindString(1, inventoryObject.MovementCode);
//			stmtSelectD.bindString(2, inventoryObject.ItemCode);
//			stmtSelectD.bindString(3, inventoryObject.UOM);
//			stmtSelectD.bindString(4, expirydate);
//			stmtSelectD.bindString(5, inventoryObject.MovementReasonCode);
			
//			long countRec = stmtSelectD.simpleQueryForLong();
			
//			if(countRec != 0)
//			{	
			stmtUpdateDNS.bindString(1, ""+inventoryObject.LineNo);
			stmtUpdateDNS.bindString(2, inventoryObject.OrgCode);
			stmtUpdateDNS.bindString(3, inventoryObject.ItemDescription);
			stmtUpdateDNS.bindString(4, ""+inventoryObject.ItemAltDescription);
			stmtUpdateDNS.bindString(5, inventoryObject.MovementStatus);
			stmtUpdateDNS.bindString(6, inventoryObject.UOM);
			stmtUpdateDNS.bindString(7, ""+inventoryObject.QuantityLevel1);
			stmtUpdateDNS.bindString(8, ""+inventoryObject.QuantityLevel2);
			stmtUpdateDNS.bindString(9, ""+inventoryObject.QuantityLevel3);
			stmtUpdateDNS.bindString(10, ""+inventoryObject.QuantityBU);
			stmtUpdateDNS.bindString(11, ""+inventoryObject.QuantitySU);
			stmtUpdateDNS.bindString(12, ""+inventoryObject.NonSellableQty);
				
			stmtUpdateDNS.bindString(13, ""+inventoryObject.CurrencyCode);
			stmtUpdateDNS.bindString(14, ""+inventoryObject.PriceLevel1);
			stmtUpdateDNS.bindString(15, ""+inventoryObject.PriceLevel2);
			stmtUpdateDNS.bindString(16, ""+inventoryObject.PriceLevel3);
			stmtUpdateDNS.bindString(17, inventoryObject.MovementReasonCode);
			stmtUpdateDNS.bindString(18, inventoryObject.ExpiryDate);
			stmtUpdateDNS.bindString(19, ""+inventoryObject.Note);
			stmtUpdateDNS.bindString(20, inventoryObject.AffectedStock);
			stmtUpdateDNS.bindString(21, loadRequestDO.Status);
				
			stmtUpdateDNS.bindString(22, ""+inventoryObject.DistributionCode);
			stmtUpdateDNS.bindString(23, inventoryObject.CreatedOn);
			stmtUpdateDNS.bindString(24, loadRequestDO.ModifiedDate);
			stmtUpdateDNS.bindString(25, ""+inventoryObject.ModifiedTime);
			stmtUpdateDNS.bindString(26, inventoryObject.PushedOn);
			stmtUpdateDNS.bindString(27, ""+inventoryObject.CancelledQuantity);
				
			stmtUpdateDNS.bindString(28, ""+inventoryObject.InProcessQuantity);
			stmtUpdateDNS.bindString(29, ""+inventoryObject.ShippedQuantity);
			stmtUpdateDNS.bindString(30, loadRequestDO.ModifiedOn);
			stmtUpdateDNS.bindString(31, loadRequestDO.MovementCode);
			stmtUpdateDNS.bindString(32, inventoryObject.ItemCode);
			stmtUpdateDNS.bindString(33, inventoryObject.UOM);
				
			stmtUpdateDNS.bindString(34, expirydate);
			stmtUpdateDNS.bindString(35, inventoryObject.MovementReasonCode);
//				stmtUpdateD.execute();
//			}
//			else
			if(stmtUpdateDNS.executeUpdateDelete()<=0)
			{
				stmtInsertDNS.bindString(1, ""+inventoryObject.LineNo);
				stmtInsertDNS.bindString(2, loadRequestDO.MovementCode);
				stmtInsertDNS.bindString(3, inventoryObject.ItemCode);
				stmtInsertDNS.bindString(4, inventoryObject.OrgCode);
				stmtInsertDNS.bindString(5, inventoryObject.ItemDescription);
				stmtInsertDNS.bindString(6, ""+inventoryObject.ItemAltDescription);
				stmtInsertDNS.bindString(7, inventoryObject.MovementStatus);
				stmtInsertDNS.bindString(8, inventoryObject.UOM);
				stmtInsertDNS.bindString(9, ""+inventoryObject.QuantityLevel1);
				stmtInsertDNS.bindString(10, ""+inventoryObject.QuantityLevel2);
				stmtInsertDNS.bindString(11, ""+inventoryObject.QuantityLevel3);
				stmtInsertDNS.bindString(12, ""+inventoryObject.QuantityBU);
				stmtInsertDNS.bindString(13, ""+inventoryObject.QuantitySU);
				stmtInsertDNS.bindString(14, ""+inventoryObject.NonSellableQty);
				stmtInsertDNS.bindString(15, ""+inventoryObject.CurrencyCode);
				stmtInsertDNS.bindString(16, ""+inventoryObject.PriceLevel1);
				stmtInsertDNS.bindString(17, ""+inventoryObject.PriceLevel2);
				stmtInsertDNS.bindString(18, ""+inventoryObject.PriceLevel3);
				stmtInsertDNS.bindString(19, inventoryObject.MovementReasonCode);
				stmtInsertDNS.bindString(20, inventoryObject.ExpiryDate);
				stmtInsertDNS.bindString(21, ""+inventoryObject.Note);
				stmtInsertDNS.bindString(22, inventoryObject.AffectedStock);
				stmtInsertDNS.bindString(23, loadRequestDO.Status);
			
				stmtInsertDNS.bindString(24, ""+inventoryObject.DistributionCode);
				stmtInsertDNS.bindString(25, inventoryObject.CreatedOn);
				stmtInsertDNS.bindString(26, loadRequestDO.ModifiedDate);
				stmtInsertDNS.bindString(27, ""+inventoryObject.ModifiedTime);
				stmtInsertDNS.bindString(28, inventoryObject.PushedOn);
				stmtInsertDNS.bindString(29, ""+inventoryObject.CancelledQuantity);
				
				stmtInsertDNS.bindString(30, ""+inventoryObject.InProcessQuantity);
				stmtInsertDNS.bindString(31, ""+inventoryObject.ShippedQuantity);
				stmtInsertDNS.bindString(32, loadRequestDO.ModifiedOn);
				stmtInsertDNS.executeInsert();
			}
		}		
	}
	
	private void insertDetail(LoadRequestDO loadRequestDO, /*SQLiteStatement stmtSelectD, */
			SQLiteStatement stmtUpdateD, SQLiteStatement stmtInsertD) 
	{
		for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
		{
//			stmtSelectD.bindString(1, inventoryObject.MovementCode);
//			stmtSelectD.bindString(2, inventoryObject.ItemCode);
//			stmtSelectD.bindString(3, inventoryObject.UOM);
//			long countRec = stmtSelectD.simpleQueryForLong();
			
//			if(countRec != 0)
//			{	
				stmtUpdateD.bindString(1, ""+inventoryObject.LineNo);
				stmtUpdateD.bindString(2, inventoryObject.OrgCode);
				stmtUpdateD.bindString(3, inventoryObject.ItemDescription);
				stmtUpdateD.bindString(4, ""+inventoryObject.ItemAltDescription);
				stmtUpdateD.bindString(5, inventoryObject.MovementStatus);
				stmtUpdateD.bindString(6, inventoryObject.UOM);
				stmtUpdateD.bindString(7, ""+inventoryObject.QuantityLevel1);
				stmtUpdateD.bindString(8, ""+inventoryObject.QuantityLevel2);
				stmtUpdateD.bindString(9, ""+inventoryObject.QuantityLevel3);
				stmtUpdateD.bindString(10, ""+inventoryObject.QuantityBU);
				stmtUpdateD.bindString(11, ""+inventoryObject.QuantitySU);
				stmtUpdateD.bindString(12, ""+inventoryObject.NonSellableQty);
				
				stmtUpdateD.bindString(13, ""+inventoryObject.CurrencyCode);
				stmtUpdateD.bindString(14, ""+inventoryObject.PriceLevel1);
				stmtUpdateD.bindString(15, ""+inventoryObject.PriceLevel2);
				stmtUpdateD.bindString(16, ""+inventoryObject.PriceLevel3);
				stmtUpdateD.bindString(17, inventoryObject.MovementReasonCode);
				stmtUpdateD.bindString(18, inventoryObject.ExpiryDate);
				stmtUpdateD.bindString(19, ""+inventoryObject.Note);
				stmtUpdateD.bindString(20, inventoryObject.AffectedStock);
				stmtUpdateD.bindString(21, loadRequestDO.Status);
				
				stmtUpdateD.bindString(22, ""+inventoryObject.DistributionCode);
				stmtUpdateD.bindString(23, inventoryObject.CreatedOn);
				stmtUpdateD.bindString(24, loadRequestDO.ModifiedDate);
				stmtUpdateD.bindString(25, ""+inventoryObject.ModifiedTime);
				stmtUpdateD.bindString(26, inventoryObject.PushedOn);
				stmtUpdateD.bindString(27, ""+inventoryObject.CancelledQuantity);
				
				stmtUpdateD.bindString(28, ""+inventoryObject.InProcessQuantity);
				stmtUpdateD.bindString(29, ""+inventoryObject.ShippedQuantity);
				stmtUpdateD.bindString(30, loadRequestDO.ModifiedOn);
				stmtUpdateD.bindString(31, loadRequestDO.MovementCode);
				stmtUpdateD.bindString(32, inventoryObject.ItemCode);
				stmtUpdateD.bindString(33, inventoryObject.UOM);
//				stmtUpdateD.execute();
//			}
//			else
			if(stmtUpdateD.executeUpdateDelete()<=0)
			{
				stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
				stmtInsertD.bindString(2, loadRequestDO.MovementCode);
				stmtInsertD.bindString(3, inventoryObject.ItemCode);
				stmtInsertD.bindString(4, inventoryObject.OrgCode);
				stmtInsertD.bindString(5, inventoryObject.ItemDescription);
				stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
				stmtInsertD.bindString(7, inventoryObject.MovementStatus);
				stmtInsertD.bindString(8, inventoryObject.UOM);
				stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
				stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
				stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
				stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
				stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
				stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
				stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
				stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
				stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
				stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
				stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
				stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
				stmtInsertD.bindString(21, ""+inventoryObject.Note);
				stmtInsertD.bindString(22, inventoryObject.AffectedStock);
				stmtInsertD.bindString(23, loadRequestDO.Status);
			
				stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
				stmtInsertD.bindString(25, inventoryObject.CreatedOn);
				stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
				stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
				stmtInsertD.bindString(28, inventoryObject.PushedOn);
				stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);
				
				stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
				stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
				stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);
				stmtInsertD.executeInsert();
			}
		}		
	}

	public void insertVanLoad(SQLiteDatabase objSqliteDB,Vector<VanStockDO> vecVanStockDOs){
		
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblVanStock " +
						 														 "(VanStockId, OrgCode, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity, ModifiedDate, ModifiedTime, BatchNumber, ExpiryDate, LPOQuantity,UOM,NonSellableQuantity) " +
						 														 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblVanStock SET VanStockId = ?, OrgCode = ?, UserCode = ?, SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?, ModifiedDate = ?, ModifiedTime = ?,BatchNumber=?, ExpiryDate=?, LPOQuantity=?,NonSellableQuantity=? WHERE ItemCode = ?");
				
				if(vecVanStockDOs != null && vecVanStockDOs.size() >0)
				{
					for (VanStockDO vanStockDO : vecVanStockDOs)
					{
//						stmtSelectR.bindString(1, vanStockDO.ItemCode);
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							String query = "SELECT (TotalQuantity- SellableQuantity) FROM tblVanStock WHERE ItemCode = '"+vanStockDO.ItemCode+"'";
							Cursor cursor = objSqliteDB.rawQuery(query, null);
							float delvQty = 0;
							if(cursor.moveToFirst())
								delvQty = cursor.getFloat(0);
							
							if(cursor != null && !cursor.isClosed())
								cursor.close();
									
							stmtUpdateR.bindString(1, ""+vanStockDO.VanStockId);
							stmtUpdateR.bindString(2, ""+vanStockDO.OrgCode);
							stmtUpdateR.bindString(3, ""+vanStockDO.UserCode);
							stmtUpdateR.bindString(4, ""+vanStockDO.SellableQuantity);
							stmtUpdateR.bindString(5, ""+vanStockDO.ReturnedQuantity);
							stmtUpdateR.bindString(6, ""+(vanStockDO.TotalQuantity+delvQty));
							stmtUpdateR.bindString(7, ""+vanStockDO.ModifiedDate);
							stmtUpdateR.bindString(8, ""+vanStockDO.ModifiedTime);
							stmtUpdateR.bindString(9, ""+vanStockDO.BatchNumber);
							stmtUpdateR.bindString(10, ""+vanStockDO.ExpiryDate);
							stmtUpdateR.bindString(11, ""+vanStockDO.LPOQty);
							stmtUpdateR.bindString(12, ""+vanStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(13, ""+vanStockDO.ItemCode);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+vanStockDO.VanStockId);
							stmtInsertR.bindString(2, ""+vanStockDO.OrgCode);
							stmtInsertR.bindString(3, ""+vanStockDO.UserCode);
							stmtInsertR.bindString(4, ""+vanStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+vanStockDO.SellableQuantity);
							stmtInsertR.bindString(6, ""+vanStockDO.ReturnedQuantity);
							stmtInsertR.bindString(7, ""+vanStockDO.TotalQuantity);
							stmtInsertR.bindString(8, ""+vanStockDO.ModifiedDate);
							stmtInsertR.bindString(9, ""+vanStockDO.ModifiedTime);
							stmtInsertR.bindString(10, ""+vanStockDO.BatchNumber);
							stmtInsertR.bindString(11, ""+vanStockDO.ExpiryDate);
							stmtInsertR.bindString(12, ""+vanStockDO.LPOQty);
							stmtInsertR.bindString(13, ""+vanStockDO.UOM);
							stmtInsertR.bindString(14, ""+vanStockDO.NonSellableQuantity);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
	
	}
	public boolean insertVanLoad(Vector<VanStockDO> vecVanStockDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
//				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVanStock WHERE ItemCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblVanStock " +
						 														 "(VanStockId, OrgCode, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity, ModifiedDate, ModifiedTime, BatchNumber, ExpiryDate, LPOQuantity,UOM,NonSellableQuantity) " +
						 														 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblVanStock SET VanStockId = ?, OrgCode = ?, UserCode = ?, SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?, ModifiedDate = ?, ModifiedTime = ?,BatchNumber=?, ExpiryDate=?, LPOQuantity=?,NonSellableQuantity=? WHERE ItemCode = ?");
				
				if(vecVanStockDOs != null && vecVanStockDOs.size() >0)
				{
					for (VanStockDO vanStockDO : vecVanStockDOs)
					{
//						stmtSelectR.bindString(1, vanStockDO.ItemCode);
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							String query = "SELECT (TotalQuantity- SellableQuantity) FROM tblVanStock WHERE ItemCode = '"+vanStockDO.ItemCode+"'";
							Cursor cursor = objSqliteDB.rawQuery(query, null);
							float delvQty = 0;
							if(cursor.moveToFirst())
								delvQty = cursor.getFloat(0);
							
							if(cursor != null && !cursor.isClosed())
								cursor.close();
									
							stmtUpdateR.bindString(1, ""+vanStockDO.VanStockId);
							stmtUpdateR.bindString(2, ""+vanStockDO.OrgCode);
							stmtUpdateR.bindString(3, ""+vanStockDO.UserCode);
							stmtUpdateR.bindString(4, ""+vanStockDO.SellableQuantity);
							stmtUpdateR.bindString(5, ""+vanStockDO.ReturnedQuantity);
							stmtUpdateR.bindString(6, ""+(vanStockDO.TotalQuantity+delvQty));
							stmtUpdateR.bindString(7, ""+vanStockDO.ModifiedDate);
							stmtUpdateR.bindString(8, ""+vanStockDO.ModifiedTime);
							stmtUpdateR.bindString(9, ""+vanStockDO.BatchNumber);
							stmtUpdateR.bindString(10, ""+vanStockDO.ExpiryDate);
							stmtUpdateR.bindString(11, ""+vanStockDO.LPOQty);
							stmtUpdateR.bindString(12, ""+vanStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(13, ""+vanStockDO.ItemCode);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+vanStockDO.VanStockId);
							stmtInsertR.bindString(2, ""+vanStockDO.OrgCode);
							stmtInsertR.bindString(3, ""+vanStockDO.UserCode);
							stmtInsertR.bindString(4, ""+vanStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+vanStockDO.SellableQuantity);
							stmtInsertR.bindString(6, ""+vanStockDO.ReturnedQuantity);
							stmtInsertR.bindString(7, ""+vanStockDO.TotalQuantity);
							stmtInsertR.bindString(8, ""+vanStockDO.ModifiedDate);
							stmtInsertR.bindString(9, ""+vanStockDO.ModifiedTime);
							stmtInsertR.bindString(10, ""+vanStockDO.BatchNumber);
							stmtInsertR.bindString(11, ""+vanStockDO.ExpiryDate);
							stmtInsertR.bindString(12, ""+vanStockDO.LPOQty);
							stmtInsertR.bindString(13, ""+vanStockDO.UOM);
							stmtInsertR.bindString(14, ""+vanStockDO.NonSellableQuantity);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
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
	
	public void insertWareHouseQty(SQLiteDatabase objSqliteDB,Vector<WareHouseStockDO> vecWStockDO)
	{
//				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblWareHouseStock WHERE ItemCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblWareHouseStock " +
						 														 "(WareHouseStockId, OrgCode,UserCode,ItemCode,SellableQuantity,ReturnedQuantity,TotalQuantity, " +
						 														 "IsVerified, status, pushStatus, IsSellableUnload, IsNonSellableUnload, NonSellableQuantity, " +
						 														 "BatchNumber, ExpiryDate, VehicleCode, LPOQuantity, UOM) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblWareHouseStock SET WareHouseStockId = ?, OrgCode = ?, UserCode = ?, SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?," +
																				 " NonSellableQuantity=?,NonSellableQuantity=?, BatchNumber=?," +
																				 "ExpiryDate=?, VehicleCode=?, LPOQuantity=?, UOM=? WHERE ItemCode = ?");
				
				if(vecWStockDO != null && vecWStockDO.size() >0)
				{
					for(WareHouseStockDO wareHouseStockDO : vecWStockDO)
					{
//						stmtSelectR.bindString(1, wareHouseStockDO.ItemCode);
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							stmtUpdateR.bindString(1, ""+wareHouseStockDO.WareHouseStockId);
							stmtUpdateR.bindString(2, ""+wareHouseStockDO.OrgCode);
							stmtUpdateR.bindString(3, ""+wareHouseStockDO.UserCode);
							stmtUpdateR.bindString(4, ""+wareHouseStockDO.SellableQuantity);
							stmtUpdateR.bindString(5, ""+wareHouseStockDO.ReturnedQuantity);
							stmtUpdateR.bindString(6, ""+(wareHouseStockDO.TotalQuantity));
							stmtUpdateR.bindString(7, ""+wareHouseStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(8, ""+wareHouseStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(9, ""+wareHouseStockDO.BatchNumber);
							stmtUpdateR.bindString(10, ""+wareHouseStockDO.ExpiryDate);
							stmtUpdateR.bindString(11, ""+wareHouseStockDO.VehicleCode);
							stmtUpdateR.bindString(12, ""+wareHouseStockDO.LPOQuantity);
							stmtUpdateR.bindString(13, ""+wareHouseStockDO.UOM);
							stmtUpdateR.bindString(14, ""+wareHouseStockDO.ItemCode);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+wareHouseStockDO.WareHouseStockId);
							stmtInsertR.bindString(2, ""+wareHouseStockDO.OrgCode);
							stmtInsertR.bindString(3, ""+wareHouseStockDO.UserCode);
							stmtInsertR.bindString(4, ""+wareHouseStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+wareHouseStockDO.SellableQuantity);
							stmtInsertR.bindString(6, ""+wareHouseStockDO.ReturnedQuantity);
							stmtInsertR.bindString(7, ""+wareHouseStockDO.TotalQuantity);
							stmtInsertR.bindString(8, ""+1);
							stmtInsertR.bindString(9, ""+1);
							stmtInsertR.bindString(10, ""+1);
							
							stmtInsertR.bindString(11, ""+1);
							stmtInsertR.bindString(12, ""+1);
							stmtInsertR.bindString(13, ""+wareHouseStockDO.NonSellableQuantity);
							stmtInsertR.bindString(14, ""+wareHouseStockDO.BatchNumber);
							
							stmtInsertR.bindString(15, ""+wareHouseStockDO.ExpiryDate);
							stmtInsertR.bindString(16, ""+wareHouseStockDO.VehicleCode);
							stmtInsertR.bindString(17, ""+wareHouseStockDO.LPOQuantity);
							stmtInsertR.bindString(18, ""+wareHouseStockDO.UOM);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
	}
	
	public boolean insertWareHouseQty(Vector<WareHouseStockDO> vecWStockDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
//				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblWareHouseStock WHERE ItemCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblWareHouseStock " +
						 														 "(WareHouseStockId, OrgCode,UserCode,ItemCode,SellableQuantity,ReturnedQuantity,TotalQuantity, " +
						 														 "IsVerified, status, pushStatus, IsSellableUnload, IsNonSellableUnload, NonSellableQuantity, " +
						 														 "BatchNumber, ExpiryDate, VehicleCode, LPOQuantity, UOM) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblWareHouseStock SET WareHouseStockId = ?, OrgCode = ?, UserCode = ?, SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?," +
																				 " NonSellableQuantity=?,NonSellableQuantity=?, BatchNumber=?," +
																				 "ExpiryDate=?, VehicleCode=?, LPOQuantity=?, UOM=? WHERE ItemCode = ?");
				
				if(vecWStockDO != null && vecWStockDO.size() >0)
				{
					for(WareHouseStockDO wareHouseStockDO : vecWStockDO)
					{
//						stmtSelectR.bindString(1, wareHouseStockDO.ItemCode);
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							stmtUpdateR.bindString(1, ""+wareHouseStockDO.WareHouseStockId);
							stmtUpdateR.bindString(2, ""+wareHouseStockDO.OrgCode);
							stmtUpdateR.bindString(3, ""+wareHouseStockDO.UserCode);
							stmtUpdateR.bindString(4, ""+wareHouseStockDO.SellableQuantity);
							stmtUpdateR.bindString(5, ""+wareHouseStockDO.ReturnedQuantity);
							stmtUpdateR.bindString(6, ""+(wareHouseStockDO.TotalQuantity));
							stmtUpdateR.bindString(7, ""+wareHouseStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(8, ""+wareHouseStockDO.NonSellableQuantity);
							stmtUpdateR.bindString(9, ""+wareHouseStockDO.BatchNumber);
							stmtUpdateR.bindString(10, ""+wareHouseStockDO.ExpiryDate);
							stmtUpdateR.bindString(11, ""+wareHouseStockDO.VehicleCode);
							stmtUpdateR.bindString(12, ""+wareHouseStockDO.LPOQuantity);
							stmtUpdateR.bindString(13, ""+wareHouseStockDO.UOM);
							stmtUpdateR.bindString(14, ""+wareHouseStockDO.ItemCode);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+wareHouseStockDO.WareHouseStockId);
							stmtInsertR.bindString(2, ""+wareHouseStockDO.OrgCode);
							stmtInsertR.bindString(3, ""+wareHouseStockDO.UserCode);
							stmtInsertR.bindString(4, ""+wareHouseStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+wareHouseStockDO.SellableQuantity);
							stmtInsertR.bindString(6, ""+wareHouseStockDO.ReturnedQuantity);
							stmtInsertR.bindString(7, ""+wareHouseStockDO.TotalQuantity);
							stmtInsertR.bindString(8, ""+1);
							stmtInsertR.bindString(9, ""+1);
							stmtInsertR.bindString(10, ""+1);
							
							stmtInsertR.bindString(11, ""+1);
							stmtInsertR.bindString(12, ""+1);
							stmtInsertR.bindString(13, ""+wareHouseStockDO.NonSellableQuantity);
							stmtInsertR.bindString(14, ""+wareHouseStockDO.BatchNumber);
							
							stmtInsertR.bindString(15, ""+wareHouseStockDO.ExpiryDate);
							stmtInsertR.bindString(16, ""+wareHouseStockDO.VehicleCode);
							stmtInsertR.bindString(17, ""+wareHouseStockDO.LPOQuantity);
							stmtInsertR.bindString(18, ""+wareHouseStockDO.UOM);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
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
	
	public void insertUpdateNonSaleableStock(SQLiteDatabase objSqliteDB,Vector<NonSellableItemDO> vecNonSellableItemDO)
	{
			{
//				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblNonSellableItems WHERE ItemCode = ? AND ExpiryDate = ? AND Reason = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblNonSellableItems " +
						 														 "(NonSellableItemId, OrgCode,UserCode,ItemCode,ReceivedQty,UnloadedQty,BatchNumber," +
						 														 " ExpiryDate, VehicleCode, UOM, Reason) " +
						 														 "VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblNonSellableItems SET UnloadedQty = ? WHERE ItemCode = ? AND ExpiryDate = ? AND Reason = ?");
				
				if(vecNonSellableItemDO != null && vecNonSellableItemDO.size() >0)
				{
					for (NonSellableItemDO vanStockDO : vecNonSellableItemDO)
					{
						String expirydate = vanStockDO.ExpiryDate;
						
						if(expirydate.contains("T"))
							expirydate = expirydate.substring(0, expirydate.lastIndexOf("T"));
						
//						stmtSelectR.bindString(1, vanStockDO.ItemCode);
//						stmtSelectR.bindString(2, expirydate);
//						stmtSelectR.bindString(3, vanStockDO.Reason);
						
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							stmtUpdateR.bindString(1, ""+vanStockDO.UnloadedQty);
							stmtUpdateR.bindString(2, ""+vanStockDO.ItemCode);
							stmtUpdateR.bindString(3, ""+expirydate);
							stmtUpdateR.bindString(4, ""+vanStockDO.Reason);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+vanStockDO.NonSellableItemId);
							stmtInsertR.bindString(2, ""+vanStockDO.Organization_Id);
							stmtInsertR.bindString(3, ""+vanStockDO.UserCode);
							stmtInsertR.bindString(4, ""+vanStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+vanStockDO.ReceivedQty);
							stmtInsertR.bindString(6, ""+vanStockDO.UnloadedQty);
							stmtInsertR.bindString(7, ""+vanStockDO.BatchNumber);
							stmtInsertR.bindString(8, ""+expirydate);
							stmtInsertR.bindString(9, ""+vanStockDO.VehicleCode);
							stmtInsertR.bindString(10,""+vanStockDO.UOM);
							stmtInsertR.bindString(11,""+vanStockDO.Reason);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
			}
		}
	public boolean insertUpdateNonSaleableStock(Vector<NonSellableItemDO> vecNonSellableItemDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
//				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblNonSellableItems WHERE ItemCode = ? AND ExpiryDate = ? AND Reason = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblNonSellableItems " +
						 														 "(NonSellableItemId, OrgCode,UserCode,ItemCode,ReceivedQty,UnloadedQty,BatchNumber," +
						 														 " ExpiryDate, VehicleCode, UOM, Reason) " +
						 														 "VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblNonSellableItems SET UnloadedQty = ? WHERE ItemCode = ? AND ExpiryDate = ? AND Reason = ?");
				
				if(vecNonSellableItemDO != null && vecNonSellableItemDO.size() >0)
				{
					for (NonSellableItemDO vanStockDO : vecNonSellableItemDO)
					{
						String expirydate = vanStockDO.ExpiryDate;
						
						if(expirydate.contains("T"))
							expirydate = expirydate.substring(0, expirydate.lastIndexOf("T"));
						
//						stmtSelectR.bindString(1, vanStockDO.ItemCode);
//						stmtSelectR.bindString(2, expirydate);
//						stmtSelectR.bindString(3, vanStockDO.Reason);
						
//						long count = stmtSelectR.simpleQueryForLong();
						
//						if(count > 0)
//						{	
							stmtUpdateR.bindString(1, ""+vanStockDO.UnloadedQty);
							stmtUpdateR.bindString(2, ""+vanStockDO.ItemCode);
							stmtUpdateR.bindString(3, ""+expirydate);
							stmtUpdateR.bindString(4, ""+vanStockDO.Reason);
//							stmtUpdateR.execute();
//						}
//						else
						if(stmtUpdateR.executeUpdateDelete()<=0)
						{
							stmtInsertR.bindString(1, ""+vanStockDO.NonSellableItemId);
							stmtInsertR.bindString(2, ""+vanStockDO.Organization_Id);
							stmtInsertR.bindString(3, ""+vanStockDO.UserCode);
							stmtInsertR.bindString(4, ""+vanStockDO.ItemCode);
							stmtInsertR.bindString(5, ""+vanStockDO.ReceivedQty);
							stmtInsertR.bindString(6, ""+vanStockDO.UnloadedQty);
							stmtInsertR.bindString(7, ""+vanStockDO.BatchNumber);
							stmtInsertR.bindString(8, ""+expirydate);
							stmtInsertR.bindString(9, ""+vanStockDO.VehicleCode);
							stmtInsertR.bindString(10,""+vanStockDO.UOM);
							stmtInsertR.bindString(11,""+vanStockDO.Reason);
							stmtInsertR.executeInsert();
						}
					}
				}
				
//				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
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
	
	/*public String getMovementId(String salesmanCode)
	{
		SQLiteDatabase objSqliteDB = null;
		String movementId = "";
		Cursor cursor = null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Movement+"' AND status = 0 AND id NOT IN(SELECT MovementCode FROM tblMovementHeader) Order By id Limit 1";
			cursor = objSqliteDB.rawQuery(query, null);
			if(cursor.moveToFirst())
			{
				movementId = cursor.getString(0);
				if(movementId != null && !movementId.equals(""))
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+movementId+"'");
			}
			
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
		
			return movementId;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
	}*/
	public ArrayList<LoadRequestDO> getAllUnLoadRequestToPost(){

		synchronized(MyApplication.MyLock) 
		{
			ArrayList<LoadRequestDO> vecLoad = new ArrayList<LoadRequestDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				//uploading only load movements
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = 'N' AND IsVerified='1' AND (MovementType='2' OR MovementType='3')";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							LoadRequestDO OrderDetail		= new LoadRequestDO();
							OrderDetail.MovementCode		= 	cursor.getString(0);
							OrderDetail.PreMovementCode		= 	cursor.getString(1);
							OrderDetail.AppMovementId 		= 	cursor.getString(2);
							OrderDetail.OrgCode				= 	cursor.getString(3);
							OrderDetail.UserCode			= 	cursor.getString(4);
							OrderDetail.WHKeeperCode		= 	cursor.getString(5);
							OrderDetail.CurrencyCode		= 	cursor.getString(6);
							OrderDetail.JourneyCode 		=   cursor.getString(7);
							
							OrderDetail.MovementDate		= 	cursor.getString(8);
							OrderDetail.MovementNote		= 	cursor.getString(9);
							OrderDetail.MovementType 		= 	cursor.getString(10);
							OrderDetail.SourceVehicleCode	= 	cursor.getString(11);
							OrderDetail.DestinationVehicleCode= 	cursor.getString(12);
							OrderDetail.Status				= 	cursor.getString(13);
							OrderDetail.VisitID				= 	cursor.getString(14);
							OrderDetail.MovementStatus		= 	cursor.getString(15);
							OrderDetail.CreatedOn 			=   cursor.getString(16);
							
							OrderDetail.ApproveByCode		= 	cursor.getString(17);
							OrderDetail.ApprovedDate		= 	cursor.getString(18);
							OrderDetail.JDETRXNumber 		= 	cursor.getString(19);
							OrderDetail.ISStampDate			= 	cursor.getString(20);
							OrderDetail.ISFromPC			= 	cursor.getString(21);
							OrderDetail.OperatorCode		= 	cursor.getString(22);
							OrderDetail.IsDummyCount		= 	cursor.getString(23);
							OrderDetail.Amount 				=   cursor.getFloat(24);

							OrderDetail.ModifiedDate		= 	cursor.getString(25);
							OrderDetail.ModifiedTime		= 	cursor.getString(26);
							OrderDetail.PushedOn 			=   cursor.getString(27);
							OrderDetail.ModifiedOn 			=   cursor.getString(28);
							OrderDetail.ProductType			=   cursor.getString(29);
							
							String strDetailQuery 			=   "SELECT * FROM tblMovementDetail WHERE MovementCode = '"+OrderDetail.MovementCode+"'";
							
							cursorDetail = sLiteDatabase.rawQuery(strDetailQuery, null);
							if(cursorDetail != null)
							{
								if(cursorDetail.moveToFirst())
								{
									do
									{
										LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
										oRequestDetailDO.LineNo			=	cursorDetail.getString(0);
										oRequestDetailDO.MovementCode	=	cursorDetail.getString(1);
										oRequestDetailDO.ItemCode		=	cursorDetail.getString(2);
										oRequestDetailDO.OrgCode		=	cursorDetail.getString(3);
										oRequestDetailDO.ItemDescription=	cursorDetail.getString(4);
										oRequestDetailDO.ItemAltDescription=	cursorDetail.getString(5);
										oRequestDetailDO.MovementStatus	=	cursorDetail.getString(6);
										oRequestDetailDO.UOM			=	cursorDetail.getString(7);
										oRequestDetailDO.QuantityLevel1	=	cursorDetail.getFloat(8);
										oRequestDetailDO.QuantityLevel2	=	cursorDetail.getFloat(9);
										oRequestDetailDO.QuantityLevel3	=	cursorDetail.getFloat(10);
										oRequestDetailDO.QuantityBU		=	cursorDetail.getFloat(11);
										oRequestDetailDO.QuantitySU		=	cursorDetail.getFloat(12);
										oRequestDetailDO.NonSellableQty	=	cursorDetail.getFloat(13);
										oRequestDetailDO.CurrencyCode	=	cursorDetail.getString(14);
										oRequestDetailDO.PriceLevel1	=	cursorDetail.getFloat(15);
										oRequestDetailDO.PriceLevel2	=	cursorDetail.getFloat(16);
										oRequestDetailDO.PriceLevel3	=	cursorDetail.getFloat(17);
										oRequestDetailDO.MovementReasonCode=	cursorDetail.getString(18);
										oRequestDetailDO.ExpiryDate		=	cursorDetail.getString(19);
										oRequestDetailDO.Note			=	cursorDetail.getString(20);
										oRequestDetailDO.AffectedStock	=	cursorDetail.getString(21);
										oRequestDetailDO.Status			=	cursorDetail.getString(22);
										oRequestDetailDO.DistributionCode=	cursorDetail.getString(23);
										oRequestDetailDO.CreatedOn		=	cursorDetail.getString(24);
										oRequestDetailDO.ModifiedDate	=	cursorDetail.getString(25);
										oRequestDetailDO.ModifiedTime	=	cursorDetail.getString(26);
										oRequestDetailDO.PushedOn		=	cursorDetail.getString(27);
										oRequestDetailDO.CancelledQuantity=	cursorDetail.getFloat(28);
										oRequestDetailDO.InProcessQuantity=	cursorDetail.getFloat(29);
										oRequestDetailDO.ShippedQuantity=	cursorDetail.getFloat(30);
										oRequestDetailDO.ModifiedOn		=	cursorDetail.getString(31);
										
										OrderDetail.vecItems.add(oRequestDetailDO);
									}
									while(cursorDetail.moveToNext());
								}
								if(cursorDetail!=null && !cursorDetail.isClosed())
									cursorDetail.close();
							}
							if(OrderDetail.vecItems != null && OrderDetail.vecItems.size() > 0)
								vecLoad.add(OrderDetail);
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
	
			return vecLoad;
		}
		
	}
	public ArrayList<LoadRequestDO> getAllLoadRequestToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<LoadRequestDO> vecLoad = new ArrayList<LoadRequestDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				//uploading only load movements
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = 'N' AND (MovementType='1' OR MovementType='10')";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							LoadRequestDO OrderDetail		= new LoadRequestDO();
							OrderDetail.MovementCode		= 	cursor.getString(0);
							OrderDetail.PreMovementCode		= 	cursor.getString(1);
							OrderDetail.AppMovementId 		= 	cursor.getString(2);
							OrderDetail.OrgCode				= 	cursor.getString(3);
							OrderDetail.UserCode			= 	cursor.getString(4);
							OrderDetail.WHKeeperCode		= 	cursor.getString(5);
							OrderDetail.CurrencyCode		= 	cursor.getString(6);
							OrderDetail.JourneyCode 		=   cursor.getString(7);
							
							OrderDetail.MovementDate		= 	cursor.getString(8);
							OrderDetail.MovementNote		= 	cursor.getString(9);
							OrderDetail.MovementType 		= 	cursor.getString(10);
							OrderDetail.SourceVehicleCode	= 	cursor.getString(11);
							OrderDetail.DestinationVehicleCode= 	cursor.getString(12);
							OrderDetail.Status				= 	cursor.getString(13);
							OrderDetail.VisitID				= 	cursor.getString(14);
							OrderDetail.MovementStatus		= 	cursor.getString(15);
							OrderDetail.CreatedOn 			=   cursor.getString(16);
							
							OrderDetail.ApproveByCode		= 	cursor.getString(17);
							OrderDetail.ApprovedDate		= 	cursor.getString(18);
							OrderDetail.JDETRXNumber 		= 	cursor.getString(19);
							OrderDetail.ISStampDate			= 	cursor.getString(20);
							OrderDetail.ISFromPC			= 	cursor.getString(21);
							OrderDetail.OperatorCode		= 	cursor.getString(22);
							OrderDetail.IsDummyCount		= 	cursor.getString(23);
							OrderDetail.Amount 				=   cursor.getFloat(24);

							OrderDetail.ModifiedDate		= 	cursor.getString(25);
							OrderDetail.ModifiedTime		= 	cursor.getString(26);
							OrderDetail.PushedOn 			=   cursor.getString(27);
							OrderDetail.ModifiedOn 			=   cursor.getString(28);
							OrderDetail.ProductType			=   cursor.getString(29);
							
							String strDetailQuery 			=   "SELECT * FROM tblMovementDetail WHERE MovementCode = '"+OrderDetail.MovementCode+"'";
							
							cursorDetail = sLiteDatabase.rawQuery(strDetailQuery, null);
							if(cursorDetail != null)
							{
								if(cursorDetail.moveToFirst())
								{
									do
									{
										LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
										oRequestDetailDO.LineNo			=	cursorDetail.getString(0);
										oRequestDetailDO.MovementCode	=	cursorDetail.getString(1);
										oRequestDetailDO.ItemCode		=	cursorDetail.getString(2);
										oRequestDetailDO.OrgCode		=	cursorDetail.getString(3);
										oRequestDetailDO.ItemDescription=	cursorDetail.getString(4);
										oRequestDetailDO.ItemAltDescription=	cursorDetail.getString(5);
										oRequestDetailDO.MovementStatus	=	cursorDetail.getString(6);
										oRequestDetailDO.UOM			=	cursorDetail.getString(7);
										oRequestDetailDO.QuantityLevel1	=	cursorDetail.getFloat(8);
										oRequestDetailDO.QuantityLevel2	=	cursorDetail.getFloat(9);
										oRequestDetailDO.QuantityLevel3	=	cursorDetail.getFloat(10);
										oRequestDetailDO.QuantityBU		=	cursorDetail.getFloat(11);
										oRequestDetailDO.QuantitySU		=	cursorDetail.getFloat(12);
										oRequestDetailDO.NonSellableQty	=	cursorDetail.getFloat(13);
										oRequestDetailDO.CurrencyCode	=	cursorDetail.getString(14);
										oRequestDetailDO.PriceLevel1	=	cursorDetail.getFloat(15);
										oRequestDetailDO.PriceLevel2	=	cursorDetail.getFloat(16);
										oRequestDetailDO.PriceLevel3	=	cursorDetail.getFloat(17);
										oRequestDetailDO.MovementReasonCode=	cursorDetail.getString(18);
										oRequestDetailDO.ExpiryDate		=	cursorDetail.getString(19);
										oRequestDetailDO.Note			=	cursorDetail.getString(20);
										oRequestDetailDO.AffectedStock	=	cursorDetail.getString(21);
										oRequestDetailDO.Status			=	cursorDetail.getString(22);
										oRequestDetailDO.DistributionCode=	cursorDetail.getString(23);
										oRequestDetailDO.CreatedOn		=	cursorDetail.getString(24);
										oRequestDetailDO.ModifiedDate	=	cursorDetail.getString(25);
										oRequestDetailDO.ModifiedTime	=	cursorDetail.getString(26);
										oRequestDetailDO.PushedOn		=	cursorDetail.getString(27);
										oRequestDetailDO.CancelledQuantity=	cursorDetail.getFloat(28);
										oRequestDetailDO.InProcessQuantity=	cursorDetail.getFloat(29);
										oRequestDetailDO.ShippedQuantity=	cursorDetail.getFloat(30);
										oRequestDetailDO.ModifiedOn		=	cursorDetail.getString(31);
										
										OrderDetail.vecItems.add(oRequestDetailDO);
									}
									while(cursorDetail.moveToNext());
								}
								if(cursorDetail!=null && !cursorDetail.isClosed())
									cursorDetail.close();
							}
							if(OrderDetail.vecItems != null && OrderDetail.vecItems.size() > 0)
								vecLoad.add(OrderDetail);
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
	
			return vecLoad;
		}
	}
	
	public LoadRequestDO getSpecifiedLoadRequestToPost(String movementCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			LoadRequestDO OrderDetail = null;
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				//uploading only load movements
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = 'N' AND MovementType='1' AND movementCode='"+movementCode+"'";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							OrderDetail		= new LoadRequestDO();
							OrderDetail.MovementCode		= 	cursor.getString(0);
							OrderDetail.PreMovementCode		= 	cursor.getString(1);
							OrderDetail.AppMovementId 		= 	cursor.getString(2);
							OrderDetail.OrgCode				= 	cursor.getString(3);
							OrderDetail.UserCode			= 	cursor.getString(4);
							OrderDetail.WHKeeperCode		= 	cursor.getString(5);
							OrderDetail.CurrencyCode		= 	cursor.getString(6);
							OrderDetail.JourneyCode 		=   cursor.getString(7);
							
							OrderDetail.MovementDate		= 	cursor.getString(8);
							OrderDetail.MovementNote		= 	cursor.getString(9);
							OrderDetail.MovementType 		= 	cursor.getString(10);
							OrderDetail.SourceVehicleCode	= 	cursor.getString(11);
							OrderDetail.DestinationVehicleCode= 	cursor.getString(12);
							OrderDetail.Status				= 	cursor.getString(13);
							OrderDetail.VisitID				= 	cursor.getString(14);
							OrderDetail.MovementStatus		= 	cursor.getString(15);
							OrderDetail.CreatedOn 			=   cursor.getString(16);
							
							OrderDetail.ApproveByCode		= 	cursor.getString(17);
							OrderDetail.ApprovedDate		= 	cursor.getString(18);
							OrderDetail.JDETRXNumber 		= 	cursor.getString(19);
							OrderDetail.ISStampDate			= 	cursor.getString(20);
							OrderDetail.ISFromPC			= 	cursor.getString(21);
							OrderDetail.OperatorCode		= 	cursor.getString(22);
							OrderDetail.IsDummyCount		= 	cursor.getString(23);
							OrderDetail.Amount 				=   cursor.getFloat(24);
							
							OrderDetail.ModifiedDate		= 	cursor.getString(25);
							OrderDetail.ModifiedTime		= 	cursor.getString(26);
							OrderDetail.PushedOn 			=   cursor.getString(27);
							OrderDetail.ModifiedOn 			=   cursor.getString(28);
							OrderDetail.ProductType			=   cursor.getString(29);
							
							String strDetailQuery 			=   "SELECT * FROM tblMovementDetail WHERE MovementCode = '"+OrderDetail.MovementCode+"'";
							
							cursorDetail = sLiteDatabase.rawQuery(strDetailQuery, null);
							if(cursorDetail != null)
							{
								if(cursorDetail.moveToFirst())
								{
									do
									{
										LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
										oRequestDetailDO.LineNo			=	cursorDetail.getString(0);
										oRequestDetailDO.MovementCode	=	cursorDetail.getString(1);
										oRequestDetailDO.ItemCode		=	cursorDetail.getString(2);
										oRequestDetailDO.OrgCode		=	cursorDetail.getString(3);
										oRequestDetailDO.ItemDescription=	cursorDetail.getString(4);
										oRequestDetailDO.ItemAltDescription=	cursorDetail.getString(5);
										oRequestDetailDO.MovementStatus	=	cursorDetail.getString(6);
										oRequestDetailDO.UOM			=	cursorDetail.getString(7);
										oRequestDetailDO.QuantityLevel1	=	cursorDetail.getFloat(8);
										oRequestDetailDO.QuantityLevel2	=	cursorDetail.getFloat(9);
										oRequestDetailDO.QuantityLevel3	=	cursorDetail.getFloat(10);
										oRequestDetailDO.QuantityBU		=	cursorDetail.getFloat(11);
										oRequestDetailDO.QuantitySU		=	cursorDetail.getFloat(12);
										oRequestDetailDO.NonSellableQty	=	cursorDetail.getFloat(13);
										oRequestDetailDO.CurrencyCode	=	cursorDetail.getString(14);
										oRequestDetailDO.PriceLevel1	=	cursorDetail.getFloat(15);
										oRequestDetailDO.PriceLevel2	=	cursorDetail.getFloat(16);
										oRequestDetailDO.PriceLevel3	=	cursorDetail.getFloat(17);
										oRequestDetailDO.MovementReasonCode=	cursorDetail.getString(18);
										oRequestDetailDO.ExpiryDate		=	cursorDetail.getString(19);
										oRequestDetailDO.Note			=	cursorDetail.getString(20);
										oRequestDetailDO.AffectedStock	=	cursorDetail.getString(21);
										oRequestDetailDO.Status			=	cursorDetail.getString(22);
										oRequestDetailDO.DistributionCode=	cursorDetail.getString(23);
										oRequestDetailDO.CreatedOn		=	cursorDetail.getString(24);
										oRequestDetailDO.ModifiedDate	=	cursorDetail.getString(25);
										oRequestDetailDO.ModifiedTime	=	cursorDetail.getString(26);
										oRequestDetailDO.PushedOn		=	cursorDetail.getString(27);
										oRequestDetailDO.CancelledQuantity=	cursorDetail.getFloat(28);
										oRequestDetailDO.InProcessQuantity=	cursorDetail.getFloat(29);
										oRequestDetailDO.ShippedQuantity=	cursorDetail.getFloat(30);
										oRequestDetailDO.ModifiedOn		=	cursorDetail.getString(31);
										
										OrderDetail.vecItems.add(oRequestDetailDO);
									}
									while(cursorDetail.moveToNext());
								}
								if(cursorDetail!=null && !cursorDetail.isClosed())
									cursorDetail.close();
							}
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
			
			return OrderDetail;
		}
	}
	
	public boolean isAllLoadRequestToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isToUpload = true;
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT COUNT(*) FROM tblMovementHeader WHERE Status = 'N'";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					int count = cursor.getInt(0);
					if(count > 0)
						isToUpload = false;
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return isToUpload;
		}
	}
	
	public ArrayList<LoadRequestDO> getAllLPOOrder()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<LoadRequestDO> vecLoad = new ArrayList<LoadRequestDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = '4'";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							LoadRequestDO OrderDetail		= new LoadRequestDO();
							OrderDetail.MovementCode		= 	cursor.getString(0);
							OrderDetail.PreMovementCode		= 	cursor.getString(1);
							OrderDetail.AppMovementId 		= 	cursor.getString(2);
							OrderDetail.OrgCode				= 	cursor.getString(3);
							OrderDetail.UserCode			= 	cursor.getString(4);
							OrderDetail.WHKeeperCode		= 	cursor.getString(5);
							OrderDetail.CurrencyCode		= 	cursor.getString(6);
							OrderDetail.JourneyCode 		=   cursor.getString(7);
							
							OrderDetail.MovementDate		= 	cursor.getString(8);
							OrderDetail.MovementNote		= 	cursor.getString(9);
							OrderDetail.MovementType 		= 	cursor.getString(10);
							OrderDetail.SourceVehicleCode	= 	cursor.getString(11);
							OrderDetail.DestinationVehicleCode= 	cursor.getString(12);
							OrderDetail.Status				= 	cursor.getString(13);
							OrderDetail.VisitID				= 	cursor.getString(14);
							OrderDetail.MovementStatus		= 	cursor.getString(15);
							OrderDetail.CreatedOn 			=   cursor.getString(16);
							
							OrderDetail.ApproveByCode		= 	cursor.getString(17);
							OrderDetail.ApprovedDate		= 	cursor.getString(18);
							OrderDetail.JDETRXNumber 		= 	cursor.getString(19);
							OrderDetail.ISStampDate			= 	cursor.getString(20);
							OrderDetail.ISFromPC			= 	cursor.getString(21);
							OrderDetail.OperatorCode		= 	cursor.getString(22);
							OrderDetail.IsDummyCount		= 	cursor.getString(23);
							OrderDetail.Amount 				=   cursor.getFloat(24);

							OrderDetail.ModifiedDate		= 	cursor.getString(25);
							OrderDetail.ModifiedTime		= 	cursor.getString(26);
							OrderDetail.PushedOn 			=   cursor.getString(27);
							OrderDetail.ModifiedOn 			=   cursor.getString(28);
							OrderDetail.ProductType			=   cursor.getString(29);
							OrderDetail.customerSite		=   cursor.getString(30);
							
							String strDetailQuery 			=   "SELECT * FROM tblMovementDetail WHERE MovementCode = '"+OrderDetail.MovementCode+"'";
							
							cursorDetail = sLiteDatabase.rawQuery(strDetailQuery, null);
							if(cursorDetail != null)
							{
								if(cursorDetail.moveToFirst())
								{
									do
									{
										LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
										oRequestDetailDO.LineNo			=	cursorDetail.getString(0);
										oRequestDetailDO.MovementCode	=	cursorDetail.getString(1);
										oRequestDetailDO.ItemCode		=	cursorDetail.getString(2);
										oRequestDetailDO.OrgCode		=	cursorDetail.getString(3);
										oRequestDetailDO.ItemDescription=	cursorDetail.getString(4);
										oRequestDetailDO.ItemAltDescription=	cursorDetail.getString(5);
										oRequestDetailDO.MovementStatus	=	cursorDetail.getString(6);
										oRequestDetailDO.UOM			=	cursorDetail.getString(7);
										oRequestDetailDO.QuantityLevel1	=	cursorDetail.getFloat(8);
										oRequestDetailDO.QuantityLevel2	=	cursorDetail.getFloat(9);
										oRequestDetailDO.QuantityLevel3	=	cursorDetail.getFloat(10);
										oRequestDetailDO.QuantityBU		=	cursorDetail.getFloat(11);
										oRequestDetailDO.QuantitySU		=	cursorDetail.getFloat(12);
										oRequestDetailDO.NonSellableQty	=	cursorDetail.getFloat(13);
										oRequestDetailDO.CurrencyCode	=	cursorDetail.getString(14);
										oRequestDetailDO.PriceLevel1	=	cursorDetail.getFloat(15);
										oRequestDetailDO.PriceLevel2	=	cursorDetail.getFloat(16);
										oRequestDetailDO.PriceLevel3	=	cursorDetail.getFloat(17);
										oRequestDetailDO.MovementReasonCode=	cursorDetail.getString(18);
										oRequestDetailDO.ExpiryDate		=	cursorDetail.getString(19);
										oRequestDetailDO.Note			=	cursorDetail.getString(20);
										oRequestDetailDO.AffectedStock	=	cursorDetail.getString(21);
										oRequestDetailDO.Status			=	cursorDetail.getString(22);
										oRequestDetailDO.DistributionCode=	cursorDetail.getString(23);
										oRequestDetailDO.CreatedOn		=	cursorDetail.getString(24);
										oRequestDetailDO.ModifiedDate	=	cursorDetail.getString(25);
										oRequestDetailDO.ModifiedTime	=	cursorDetail.getString(26);
										oRequestDetailDO.PushedOn		=	cursorDetail.getString(27);
										oRequestDetailDO.CancelledQuantity=	cursorDetail.getFloat(28);
										oRequestDetailDO.InProcessQuantity=	cursorDetail.getFloat(29);
										oRequestDetailDO.ShippedQuantity=	cursorDetail.getFloat(30);
										oRequestDetailDO.ModifiedOn		=	cursorDetail.getString(31);
										
										OrderDetail.vecItems.add(oRequestDetailDO);
									}
									while(cursorDetail.moveToNext());
								}
								if(cursorDetail!=null && !cursorDetail.isClosed())
									cursorDetail.close();
							}
							if(OrderDetail.vecItems != null && OrderDetail.vecItems.size() > 0)
								vecLoad.add(OrderDetail);
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
	
			return vecLoad;
		}
	}

	public boolean updateStatus(Vector<NameIDDo> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "UPDATE tblMovementHeader SET Status = ? WHERE MovementCode=?";
				SQLiteStatement stmtUpdateCustomer = objSqliteDB.compileStatement(strUpdate);
				
				for (NameIDDo object : vec)
				{
					stmtUpdateCustomer.bindString(1, "1");
					stmtUpdateCustomer.bindString(2, object.strId);
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
	
	public boolean isMovementUploadedToServer(String movementCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean isUploaded = false;
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "Select Status from  tblMovementHeader WHERE MovementCode='"+movementCode+"'";
				cursor = objSqliteDB.rawQuery(strUpdate,null);
				
				if(cursor!=null && cursor.moveToFirst()){
					if(cursor.getInt(0) == 1)
						isUploaded = true;
				}
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
			
			return isUploaded;
		}
		
	}	
	
	public boolean updateLPOStatus(String strId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String strUpdate = "UPDATE tblMovementHeader SET Status = ? WHERE MovementCode=?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);
				
				stmtUpdateCustomer.bindString(1, "1");
				stmtUpdateCustomer.bindString(2, strId);
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
	public LoadRequestDO getUnloadmovement(String movementId) {

		synchronized(MyApplication.MyLock) 
		{
			LoadRequestDO loadRequest = null;
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			/*INV.Date like '%"+date+"%' AND */
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				
				String strQuery 	= 	"SELECT MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode," +
										"WHKeeperCode,CurrencyCode,JourneyCode,MovementDate,MovementNote,"+
										"MovementType,SourceVehicleCode,DestinationVehicleCode,VisitID,MovementStatus," +
										"CreatedOn ,Amount,ProductType  from tblMovementHeader WHERE MovementCode='"+movementId+"'";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
							loadRequest=new LoadRequestDO();
							loadRequest.MovementCode 			= 	cursor.getString(0);
							loadRequest.PreMovementCode 		= 	cursor.getString(1);
							loadRequest.AppMovementId 			= 	cursor.getString(2);
							loadRequest.OrgCode 			    = 	cursor.getString(3);
							loadRequest.UserCode 			    = 	cursor.getString(4);
							loadRequest.WHKeeperCode 			= 	cursor.getString(5);
							loadRequest.CurrencyCode 			= 	cursor.getString(6);
							loadRequest.JourneyCode 			= 	cursor.getString(7);
							loadRequest.MovementDate 			= 	cursor.getString(8);
							loadRequest.MovementNote 			= 	cursor.getString(9);
							loadRequest.MovementType 			= 	cursor.getString(10);
							loadRequest.SourceVehicleCode 		= 	cursor.getString(11);
							loadRequest.DestinationVehicleCode 	= 	cursor.getString(12);
							loadRequest.VisitID 			    = 	cursor.getString(13);
							loadRequest.CreatedOn 			    = 	cursor.getString(14);
							loadRequest.Amount 			        = 	cursor.getFloat(15);
							loadRequest.ProductType 			= 	cursor.getString(16);
							
						}
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
	
			return loadRequest;
		}
	
	}
	public ArrayList<VanLoadDO> getMovementdata(String movementCode) {
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<VanLoadDO> vecVanLodDOs=null;
			VanLoadDO vanloadDO=null;
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			try {
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query="select ItemCode,InProcessQuantity,ShippedQuantity,CancelledQuantity,movementStatus from tblMovementDetail where movementcode='"+movementCode+"'";
				cursor = sLiteDatabase.rawQuery(query, null);
				if(cursor != null)
				{
					vecVanLodDOs=new ArrayList<VanLoadDO>();
					if(cursor.moveToFirst())
					{
						do{
							vanloadDO=new VanLoadDO();
							vanloadDO.ItemCode			= 	cursor.getString(0);
							vanloadDO.inProccessQty			= 	cursor.getInt(1);
							vanloadDO.ShippedQuantity			= 	cursor.getInt(2);
							vanloadDO.CancelledQuantity			= 	cursor.getInt(3);
							vanloadDO.movementStatus			= 	cursor.getString(4);
							
							vecVanLodDOs.add(vanloadDO);
						}while(cursor.moveToNext());
						
					}
				}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}
		return vecVanLodDOs;
		}
	}
	
}
