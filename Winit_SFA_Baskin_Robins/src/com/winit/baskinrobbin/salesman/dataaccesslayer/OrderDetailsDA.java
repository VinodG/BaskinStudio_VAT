package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class OrderDetailsDA 
{
	//format get up 2 decimal points
	private DecimalFormat df = new DecimalFormat("##.##");
	public Vector<ProductDO> getOrderDetails(OrderDO objOrders, String startDate,String endDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery = "";
			SQLiteDatabase sqLiteDatabase 			= 	null;
			Cursor cursor 	 						=	null;
			Vector<ProductDO> vectorOrderList 		= 	new Vector<ProductDO>();
			try
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				if(!TextUtils.isEmpty(endDate) && objOrders == null)
					strQuery = "SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, OD.ItemDescription, 1 AS UnitPerCase, " +
			                   "OD.PriceUsedLevel2, OD.UnitSellingPrice, OD.TotalDiscountAmount, OD.ItemPrice, OD.RelatedLineID,  "
			                   + "(SELECT TP.Description FROM tblProducts TP WHERE  OD.RelatedLineID = TP.ItemCode) AS Description, "
			                   + "OD.ExpiryDate, OD.ReasonCode, OD.LotNo,OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock  " +
			                   "FROM tblOrderDetail OD where "+
			                   "OD.OrderNo IN (SELECT OrderId FROM tblOrderHeader WHERE OrderType = '"+AppConstants.REPLACEMETORDER+"' AND OrderDate BETWEEN '"+startDate+"'  AND '"+endDate+"') "+
			                   " GROUP BY OD.ItemCode, OD.LineNo, OD.OrderNo";
				else
					strQuery = 	"SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, OD.ItemDescription, 1 AS UnitPerCase, OD.PriceUsedLevel2, OD.UnitSellingPrice, "
							    + "OD.TotalDiscountAmount, OD.ItemPrice, OD.RelatedLineID, (SELECT TP.Description FROM tblProducts TP WHERE  OD.RelatedLineID = TP.ItemCode) "
							    + "AS Description , OD.ExpiryDate, OD.ReasonCode, OD.LotNo,OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock  FROM tblOrderDetail OD where OD.OrderNo='"+objOrders.OrderId+"' "
							    + "GROUP BY OD.ItemCode, OD.LineNo, OD.OrderNo";
				
				cursor 		 = 	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO orderDO 		= 	new ProductDO();
						orderDO.OrderNo  		= 	cursor.getString(0);
						orderDO.SKU 			= 	cursor.getString(1);
						orderDO.cases 			=	""+StringUtils.getFloat(cursor.getString(2));
						orderDO.units 			= 	""+cursor.getInt(3);
						orderDO.preUnits 		=   ""+orderDO.units;
						orderDO.UOM		 		= 	cursor.getString(4);
						orderDO.UnitsPerCases	= 	StringUtils.getInt(cursor.getString(6));
						orderDO.Description		= 	cursor.getString(5);
						orderDO.invoiceAmount	= 	StringUtils.getDouble(cursor.getString(7));
						orderDO.unitSellingPrice= 	StringUtils.getFloat(cursor.getString(8));
						orderDO.DiscountAmt		= 	StringUtils.getFloat(cursor.getString(9));
						orderDO.itemPrice		= 	StringUtils.getFloat(cursor.getString(10));
						
						orderDO.RelatedLineId	= 	cursor.getString(11);
						orderDO.Description1 	= 	cursor.getString(12);
						
						orderDO.strExpiryDate	= 	cursor.getString(13);
						orderDO.reason 			= 	cursor.getString(14);
						orderDO.LotNumber		= 	cursor.getString(15);

						orderDO.LineTaxAmount		= 	cursor.getDouble(16);
						orderDO.ProrataTaxAmount	= 	cursor.getDouble(17);
						orderDO.TotalTax			= 	cursor.getDouble(18);
						orderDO.QuantityInStock		= 	cursor.getFloat(19);

						orderDO.Discount	    =   orderDO.DiscountAmt;
						orderDO.discountAmount  =   orderDO.DiscountAmt;
						
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	}
	public Vector<ProductDO> getOrderDetailsnew(String startdate, String endDate){

		synchronized(MyApplication.MyLock) 
		{
			String strQuery = "";
			SQLiteDatabase sqLiteDatabase 			= 	null;
			Cursor cursor 	 						=	null;
			Vector<ProductDO> vectorOrderList 		= 	new Vector<ProductDO>();
			try
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
					
				strQuery="SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, OD.ItemDescription, 1 AS UnitPerCase," +
						" OD.PriceUsedLevel2, OD.UnitSellingPrice, OD.TotalDiscountAmount, OD.ItemPrice, OD.RelatedLineID,  " +
						"(SELECT TP.Description FROM tblProducts TP WHERE  OD.RelatedLineID = TP.ItemCode) AS Description," +
						" OD.ExpiryDate, OD.ReasonCode, OD.LotNo,OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock  " +
						"FROM tblOrderDetail OD where OD.OrderNo IN (SELECT OrderId FROM tblOrderHeader " +
						"WHERE OrderType = 'Replace Order' AND julianday(OrderDate)>=julianday('"+startdate+"')  AND julianday(OrderDate)<=julianday('"+endDate+"'))  GROUP BY OD.ItemCode, OD.LineNo, OD.OrderNo";
				cursor 		 = 	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO orderDO 		= 	new ProductDO();
						orderDO.OrderNo  		= 	cursor.getString(0);
						orderDO.SKU 			= 	cursor.getString(1);
						orderDO.cases 			=	""+StringUtils.getFloat(cursor.getString(2));
						orderDO.units 			= 	""+cursor.getInt(3);
						orderDO.preUnits 		=   ""+orderDO.units;
						orderDO.UOM		 		= 	cursor.getString(4);
						orderDO.UnitsPerCases	= 	StringUtils.getInt(cursor.getString(6));
						orderDO.Description		= 	cursor.getString(5);
						orderDO.invoiceAmount	= 	StringUtils.getDouble(cursor.getString(7));
						orderDO.unitSellingPrice= 	StringUtils.getFloat(cursor.getString(8));
						orderDO.DiscountAmt		= 	StringUtils.getFloat(cursor.getString(9));
						orderDO.itemPrice		= 	StringUtils.getFloat(cursor.getString(10));
						
						orderDO.RelatedLineId	= 	cursor.getString(11);
						orderDO.Description1 	= 	cursor.getString(12);
						
						orderDO.strExpiryDate	= 	cursor.getString(13);
						orderDO.reason 			= 	cursor.getString(14);
						orderDO.LotNumber		= 	cursor.getString(15);

						orderDO.LineTaxAmount		= 	cursor.getDouble(16);
						orderDO.ProrataTaxAmount		= 	cursor.getDouble(17);
						orderDO.TotalTax		= 	cursor.getDouble(18);
						orderDO.QuantityInStock		= 	cursor.getFloat(19);
						orderDO.TaxPercentage		= 	cursor.getFloat(20);
						orderDO.Discount	    =   orderDO.DiscountAmt;
						orderDO.discountAmount  =   orderDO.DiscountAmt;
						
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	
	}
	public Vector<ProductDO> getOrderDetails(OrderDO objOrders, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery = "";
			SQLiteDatabase sqLiteDatabase 			= 	null;
			Cursor cursor 	 						=	null;
			Vector<ProductDO> vectorOrderList 		= 	new Vector<ProductDO>();
			try
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				if(!TextUtils.isEmpty(date) && objOrders == null)
					strQuery = "SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, OD.ItemDescription, 1 AS UnitPerCase, " +
			                   "OD.PriceUsedLevel2, OD.UnitSellingPrice, OD.TotalDiscountAmount, OD.ItemPrice, OD.RelatedLineID,  "
			                   + "(SELECT TP.Description FROM tblProducts TP WHERE  OD.RelatedLineID = TP.ItemCode) AS Description, "
			                   + "OD.ExpiryDate, OD.ReasonCode, OD.LotNo ,OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock,OD.TaxPercentage,OD.RefTrxCode " +
			                   "FROM tblOrderDetail OD where "+
			                   "OD.OrderNo IN (SELECT OrderId FROM tblOrderHeader WHERE OrderType = '"+AppConstants.REPLACEMETORDER+"' AND OrderDate LIKE '%"+date+"%') "+
			                   " GROUP BY OD.ItemCode, OD.LineNo, OD.OrderNo,OD.RefTrxCode";
				//LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax
				else
					strQuery = 	"SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, OD.ItemDescription, 1 AS UnitPerCase, OD.PriceUsedLevel2, OD.UnitSellingPrice, "
							    + "OD.TotalDiscountAmount, OD.ItemPrice, OD.RelatedLineID, (SELECT TP.Description FROM tblProducts TP WHERE  OD.RelatedLineID = TP.ItemCode) "
							    + "AS Description , OD.ExpiryDate, OD.ReasonCode, OD.LotNo,OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock,OD.TaxPercentage,OD.RefTrxCode FROM tblOrderDetail OD where OD.OrderNo='"+objOrders.OrderId+"' "
							    + "GROUP BY OD.ItemCode, OD.LineNo, OD.OrderNo,OD.RefTrxCode";
				//LineTaxAmount" FLOAT, "ProrataTaxAmount" FLOAT, "TotalTax
				cursor 		 = 	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO orderDO 		= 	new ProductDO();
						orderDO.OrderNo  		= 	cursor.getString(0);
						orderDO.SKU 			= 	cursor.getString(1);
						orderDO.cases 			=	""+StringUtils.getFloat(cursor.getString(2));
						orderDO.units 			= 	""+cursor.getInt(3);
			//			orderDO.quantityBU 			= 	cursor.getFloat(3);//
						orderDO.preUnits 		=   ""+orderDO.units;
						orderDO.UOM		 		= 	cursor.getString(4);
						orderDO.UnitsPerCases	= 	StringUtils.getInt(cursor.getString(6));
						orderDO.Description		= 	cursor.getString(5);
						orderDO.invoiceAmount	= 	StringUtils.getDouble(cursor.getString(7));
						orderDO.unitSellingPrice= 	StringUtils.getFloat(cursor.getString(8));
						orderDO.DiscountAmt		= 	StringUtils.getFloat(cursor.getString(9));
						orderDO.itemPrice		= 	StringUtils.getFloat(cursor.getString(10));
						
						orderDO.RelatedLineId	= 	cursor.getString(11);
						orderDO.Description1 	= 	cursor.getString(12);
						
						orderDO.strExpiryDate	= 	cursor.getString(13);
						orderDO.reason 			= 	cursor.getString(14);
						orderDO.LotNumber		= 	cursor.getString(15);
					//OD.LineTaxAmount,OD.ProrataTaxAmount,OD.TotalTax,OD.QuantityInStock
						orderDO.LineTaxAmount		= 	cursor.getDouble(16);
						orderDO.ProrataTaxAmount	= 	cursor.getDouble(17);
						orderDO.TotalTax			= 	cursor.getDouble(18);
						orderDO.QuantityInStock		= 	cursor.getFloat(19);
						orderDO.TaxPercentage		= 	cursor.getFloat(20);
						orderDO.RefTrxCode		= 	cursor.getString(21);

						orderDO.Discount	    =   orderDO.DiscountAmt;
						orderDO.discountAmount  =   orderDO.DiscountAmt;
						
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	}
	public String getPaymentDueDate(String invoiceNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strPamentDueDate = "";
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			String query ="Select NextPaymentDate from tblDeliveryOrderDetail where LineNumber ='"+invoiceNo+"'  and typeof(NextPaymentDate)!='null'  Limit 1";
			try
			{
				sqLiteDatabase 	=	DatabaseHelper.openDataBase();
				cursor			=	sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strPamentDueDate = cursor.getString(0);
					strPamentDueDate =CalendarUtils.getFormatedDatefromString(strPamentDueDate);
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
				if(sqLiteDatabase!=null )
					sqLiteDatabase.close();
			}
			return strPamentDueDate;
		}
	}
	
	
	public Vector<ProductDO> getOrderProductsDetails(String orderID, String strSKUs)
	{
		synchronized(MyApplication.MyLock) 
		{
			ProductDO objProducts;
			Vector<ProductDO> vecProductList = new Vector<ProductDO>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			
			try
			{
				_database = DatabaseHelper.openDataBase();
				cursor	=	_database.rawQuery("SELECT DISTINCT OD.SKU,OD.Cases,OD.Units, P.UnitPerCase, P.Description ,P.UOM, P.SECONDARY_UOM, P.PricingKey from tblInvoiceOrderDetails OD,tblProducts P where  P.SKU = OD.SKU and  OD.OrderId='"+orderID+"' and OD.SKU not in("+strSKUs+") and OD.Units > 0", null);
				if(cursor != null)
				{
					cursor.moveToFirst();
					do
					{
						objProducts 			= new ProductDO();
						objProducts.SKU 		= cursor.getString(0);
						if(cursor.getString(2) != null && !cursor.getString(2).equalsIgnoreCase(""))
							objProducts.orderedUnits 	= StringUtils.getInt(cursor.getString(2));
						else
							objProducts.orderedUnits = 0;
						objProducts.UnitsPerCases 		= StringUtils.getInt(cursor.getString(3));
						
						if(objProducts.orderedUnits != 0 && objProducts.UnitsPerCases != 0)
							objProducts.orderedCases 	= (float)objProducts.orderedUnits/objProducts.UnitsPerCases;
						else
							objProducts.orderedCases = 0;
						
						objProducts.Description  = cursor.getString(4);
						objProducts.itemPrice  	 = 0;
						objProducts.UOM	 		 = cursor.getString(5);
						objProducts.secondaryUOM = cursor.getString(6);
						objProducts.PricingKey	 = cursor.getString(7);
						vecProductList.add(objProducts); 
					}
					while(cursor.moveToNext());
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
				if(_database != null)
					_database.close();
			}
			return vecProductList;
		}
	}
	
	public Vector<ProductDO> getAllProductsDetails( String strSKUs)
	{
		synchronized(MyApplication.MyLock) 
		{
			ProductDO objProducts;
			Vector<ProductDO> vecProductList = new Vector<ProductDO>();
			SQLiteDatabase _database = null;
			Cursor cursor = null;
			
			try
			{
				_database = DatabaseHelper.openDataBase();
				cursor	=	_database.rawQuery("SELECT DISTINCT  P.SKU, P.UnitPerCase, P.Description ,P.UOM, P.SECONDARY_UOM, P.PricingKey from  tblProducts P where  P.SKU not in("+strSKUs+")", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objProducts 				= 	new ProductDO();
						objProducts.SKU 			= 	cursor.getString(0);
						objProducts.orderedUnits 	= 	0;
						objProducts.UnitsPerCases 	= 	StringUtils.getInt(cursor.getString(1));
						objProducts.orderedCases 	= 	0;
						objProducts.Description  	= 	cursor.getString(2);
						objProducts.itemPrice 		=	0;
						objProducts.UOM	 		 	= 	cursor.getString(3);
						objProducts.secondaryUOM 	= 	cursor.getString(4);
						objProducts.PricingKey	 	= 	cursor.getString(5);
						vecProductList.add(objProducts); 
					}
					while(cursor.moveToNext());
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
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecProductList;
		}
	}
	
	public boolean updateDeliveryOrder(Vector<DeliveryAgentOrderDetailDco>  vecDeliveryAgentOrderDetail,String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 SQLiteStatement stmtUpdate = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 	
				 stmtUpdate = objSqliteDB.compileStatement("Update tblDeliveryOrderDetail set OrderStatus=?, DeliveredQty=? where ItemCode=? and OrderId = ?");
				 for(int i =0 ; vecDeliveryAgentOrderDetail != null && i < vecDeliveryAgentOrderDetail.size() ; i++)
				 {
					 stmtUpdate.bindString(1, status);
					 stmtUpdate.bindString(2, vecDeliveryAgentOrderDetail.get(i).etCases.getText().toString());
					 stmtUpdate.bindString(3, vecDeliveryAgentOrderDetail.get(i).itemCode);
					 stmtUpdate.bindString(4, vecDeliveryAgentOrderDetail.get(i).blaseOrderNumber);
					 stmtUpdate.execute();
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
				 stmtUpdate.close();
				
			 }
		 }
	}
	
	//public Vector<InventoryObject> getInventoryQty(String strDate)
	public Vector<InventoryObject> getInventoryQty()
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	 = 	DatabaseHelper.openDataBase();
				String query =  "SELECT DISTINCT VI.ItemCode, TP.Description, VI.TotalQuantity/TF.Factor, VI.SellableQuantity/TF.Factor, " +
								"(VI.TotalQuantity- VI.SellableQuantity) / TF.Factor, TP.UOM,TP.UnitPerCase FROM " +
								"tblVanStock VI INNER JOIN tblProducts TP ON VI.ItemCode = TP.ItemCode " +
								"INNER JOIN tblUOMFactor TF ON VI.ItemCode = TF.ItemCode AND TF.UOM = TP.UOM WHERE VI.SellableQuantity > 0 ORDER BY VI.ItemCode";
				cursor		 =	_database.rawQuery(query , null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.availCases		=	cursor.getFloat(2);
						objInventoryObject.availQty			=	cursor.getFloat(3);
						objInventoryObject.deliveredCases	=	cursor.getFloat(4) > 0 ? cursor.getFloat(4) : 0;
						objInventoryObject.UOM				=	cursor.getString(5);
						objInventoryObject.unitPerCases		=	(int) cursor.getFloat(6);
						vecInventoryItems.add(objInventoryObject); 
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
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public Vector<InventoryObject> getInventoryQty_New(String strDate, String orderType)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT VI.ItemCode, TP.Description, VI.PrimaryQuantity, (SELECT SUM(QuantityBU) FROM tblOrderDetail" +
													" WHERE OrderNo IN (SELECT OrderNo FROM tblOrderHeader WHERE ((DeliveryDate like '"+strDate+"%' AND TRXStatus ='D')" +
													" OR " +
													"OrderDate LIKE '"+strDate+"%') AND OrderType='"+orderType+"' ) AND VI.ItemCode = ItemCode) AS Delivered" +
													",TP.UOM,TP.UnitPerCase,VI.availQty from tblVMSalesmanInventory VI LEFT JOIN  tblProducts TP "+
													"ON VI.ItemCode = TP.SKU WHERE VI.Date like '"+strDate+"%'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	cursor.getFloat(2);
						objInventoryObject.deliveredCases	=	cursor.getFloat(3) +(objInventoryObject.PrimaryQuantity - cursor.getFloat(6));
						objInventoryObject.UOM				=	cursor.getString(4);
						objInventoryObject.unitPerCases		=	StringUtils.getInt(cursor.getString(5));
						objInventoryObject.availCases		=	objInventoryObject.PrimaryQuantity - objInventoryObject.deliveredCases;
						
						if(objInventoryObject.availCases < 0)
							objInventoryObject.availCases = 0;
						
						vecInventoryItems.add(objInventoryObject); 
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
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public Vector<InventoryObject> getReturnInventoryQtyNew(DecimalFormat decimalFormat, DecimalFormat diff)
	{
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				String strQuery = "SELECT (INV.ReceivedQty-INV.UnloadedQty) /TF.Factor, INV.ItemCode, TP.Description, TP.UOM, INV.Reason, INV.ExpiryDate " +
						          "FROM tblProducts TP  INNER JOIN tblNonSellableItems INV ON TP.ItemCode= INV.ItemCode " +
						          "INNER JOIN tblUOMFactor TF ON TF.ItemCode = INV.ItemCode AND TF.UOM= TP.UOM " +
						          "WHERE (INV.ReceivedQty-INV.UnloadedQty)> 0 ORDER BY INV.ItemCode";
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(1);
						objInventoryObject.itemDescription	=	cursor.getString(2);
						objInventoryObject.PrimaryQuantity	=	StringUtils.getFloat(decimalFormat.format(cursor.getFloat(0)));
						objInventoryObject.UOM				=	cursor.getString(3);
						objInventoryObject.reason			=	cursor.getString(4);
						objInventoryObject.expiryDate		=	cursor.getString(5);
						
						if(StringUtils.getFloat(diff.format(objInventoryObject.PrimaryQuantity)) > 0)
							vecInventoryItems.add(objInventoryObject); 
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
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	public Vector<InventoryObject> getSalableReturnedQty(DecimalFormat decimalFormat, DecimalFormat diff)
	{
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				/*String strQuery = "SELECT (INV.ReceivedQty-INV.UnloadedQty) /TF.Factor, INV.ItemCode, TP.Description, TP.UOM, INV.Reason, INV.ExpiryDate " +
						          "FROM tblProducts TP  INNER JOIN tblNonSellableItems INV ON TP.ItemCode= INV.ItemCode " +
						          "INNER JOIN tblUOMFactor TF ON TF.ItemCode = INV.ItemCode AND TF.UOM= TP.UOM " +
						          "WHERE (INV.ReceivedQty-INV.UnloadedQty)> 0 ORDER BY INV.ItemCode";*/
				String strQuery="SELECT tvm.ItemCode,(tvm.SellableQuantity+tvm.ReturnedQuantity), " +
						"TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason,TF.Factor  FROM tblProducts TP " +
						"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND " +
						"TF.UOM = TP.UOM WHERE SellableQuantity+ReturnedQuantity > 0 ORDER BY INV.ItemCode";	
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(1);
						objInventoryObject.itemDescription	=	cursor.getString(2);
						objInventoryObject.PrimaryQuantity	=	StringUtils.getFloat(decimalFormat.format(cursor.getFloat(0)));
						objInventoryObject.UOM				=	cursor.getString(3);
						objInventoryObject.reason			=	cursor.getString(4);
						objInventoryObject.expiryDate		=	cursor.getString(5);
						
						if(StringUtils.getFloat(diff.format(objInventoryObject.PrimaryQuantity)) > 0)
							vecInventoryItems.add(objInventoryObject); 
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
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public Vector<InventoryObject> getReturnInventoryQty(String strDate, String orderType)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT OD.ItemCode, TP.Description,SUM(OD.QuantityBU),TP.UOM, TP.UnitPerCase FROM tblOrderDetail OD " +
												   "INNER JOIN tblProducts TP ON TP.SKU = OD.ItemCode " +
												   "WHERE OrderNo IN (SELECT OrderId FROM tblOrderHeader WHERE OrderDate LIKE '"+strDate+"%' AND OrderType = '"+orderType+"' ) " +
												   "GROUP BY OD.ItemCode, OD.UOM", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	StringUtils.getFloat(decimalFormat.format(cursor.getFloat(2)));
						objInventoryObject.UOM				=	cursor.getString(3);
						objInventoryObject.unitPerCases		=	cursor.getInt(4);
						
						vecInventoryItems.add(objInventoryObject); 
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
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys_Replace()
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null;
			try
			{
				query 	=  "SELECT ST.ItemCode, (ST.SellableQuantity) AS SellableQuantity, ST.BatchNumber, ST.ExpiryDate, TF.UOM " +
						   "From tblVanStock ST INNER JOIN tblUOMFactor TF ON TF.ItemCode = ST.ItemCode AND TF.Factor = 1 " +
						   "WHERE SellableQuantity > 0 GROUP BY ST.ItemCode";
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						//hhInventryQTDO.totalQt 		  = (float) StringUtils.round(cursor.getString(1), precision);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.batchCode 	  = cursor.getString(2);
						hhInventryQTDO.expiryDate	  = cursor.getString(3);
						hhInventryQTDO.UOM		  	  = cursor.getString(4);
						
						hhInventryQTDO.quantityBU  	  = hhInventryQTDO.totalQt;
						
						hmInventory.put(cursor.getString(0), hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	/**
	 * 
	 * @param ORDER_TYPE
	 * @return
	 */
	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys_Temp(String ORDER_TYPE)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null;
			try
			{
				query 	=  "SELECT SI.ItemCode, (SellableQuantity) AS SellableQuantity, SI.BatchNumber,SI.ExpiryDate, TP.UOM, "+ 
						   "TP.Factor From tblVanStock SI INNER JOIN tblProducts P on P.ItemCode=SI.ItemCode "+
						   "INNER JOIN tblUOMFactor TP ON (TP.ItemCode = SI.ItemCode OR (P.Category='TUB' and TP.ItemCode='110010001')) AND round(SI.SellableQuantity/TP.Factor,3) > 0 ";
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.batchCode 	  = cursor.getString(2);
						hhInventryQTDO.expiryDate	  = cursor.getString(3);
						hhInventryQTDO.UOM		  	  = cursor.getString(4);
						
						hhInventryQTDO.quantityBU  	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.totalQt 		  = hhInventryQTDO.totalQt / cursor.getFloat(5);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						
						hmInventory.put(cursor.getString(0) + hhInventryQTDO.UOM, hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
//	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys_Temp(String ORDER_TYPE,int precision)
	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys_Temp(String ORDER_TYPE,int precision,int load_type)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null,penCursor=null;
			try
			{
				
				_database 	= 	DatabaseHelper.openDataBase();
				
				String strquery = "Select TMD.ItemCode,Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
						   "INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode "+
						   "AND  THD.MovementType='"+load_type+"'  "+
						   "Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  "+
						   "where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR "
						   + "TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' )  GROUP BY TMD .ItemCode";
				penCursor	=	_database.rawQuery(strquery, null);
			HashMap<String, Double> hmItemPending = new HashMap<String, Double>();
			if(penCursor.moveToFirst())
			{
				do{
					String key="";
					key=penCursor.getString(0);
					hmItemPending.put(key,penCursor.getDouble(1));
				}
				while (penCursor.moveToNext());
			}
//				query 	=  "SELECT SI.ItemCode, (SellableQuantity) AS SellableQuantity, SI.BatchNumber,SI.ExpiryDate, TP.UOM, "+ 
//						   "TP.Factor From tblVanStock SI INNER JOIN tblProducts P on P.ItemCode=SI.ItemCode "+
//						   "INNER JOIN tblUOMFactor TP ON TP.ItemCode = SI.ItemCode  AND round(SI.SellableQuantity/TP.Factor,3) > 0 ";
//				
			if(load_type==AppStatus.UNLOAD_S_STOCK)
				query 	=  "SELECT SI.ItemCode, (SI .SellableQuantity+SI .ReturnedQuantity) AS SellableQuantity, SI.BatchNumber,SI.ExpiryDate, TP.UOM, "+ 
						   "TP.Factor From tblVanStock SI INNER JOIN tblProducts P on P.ItemCode=SI.ItemCode "+
						   "INNER JOIN tblUOMFactor TP ON TP.ItemCode = SI.ItemCode  AND round(SI.SellableQuantity/TP.Factor,3) > 0 ";
			else
				query = "SELECT tvm.ItemCode,(tvm.ReceivedQty-tvm.UnloadedQty), TP.Description, TP.Category,TP.UOM, "
						+ "TP.ItemType,tvm.ExpiryDate,tvm.Reason,TF.Factor " +
						   "FROM tblProducts TP INNER JOIN tblNonSellableItems tvm on tvm.ItemCode = TP.ItemCode " +
						   "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE (tvm.ReceivedQty - tvm.UnloadedQty)> 0";
			
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						if(!hmItemPending.isEmpty()&& hmItemPending.containsKey(cursor.getString(0)))
							hhInventryQTDO.totalQt 		  = (float) (cursor.getFloat(1)-hmItemPending.get(cursor.getString(0)));
						else
							hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						
						hhInventryQTDO.onHandQty	  = (float) (cursor.getFloat(1));
						hhInventryQTDO.onHandQty	  = (float) StringUtils.round(""+hhInventryQTDO.onHandQty / cursor.getFloat(5), precision);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.batchCode 	  = cursor.getString(2);
						hhInventryQTDO.expiryDate	  = cursor.getString(3);
						hhInventryQTDO.UOM		  	  = cursor.getString(4);
						
						hhInventryQTDO.quantityBU  	  = hhInventryQTDO.totalQt;
						//hhInventryQTDO.totalQt 		  = hhInventryQTDO.totalQt / cursor.getFloat(5);
						hhInventryQTDO.totalQt 		  = (float) StringUtils.round(""+hhInventryQTDO.totalQt / cursor.getFloat(5), precision);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						
						hmInventory.put(cursor.getString(0) + hhInventryQTDO.UOM, hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
	/**
	 * 
	 * @param ORDER_TYPE
	 * @return
	 */
	public HashMap<String, HHInventryQTDO> getNonSealableInventoryQtys_Temp(String ORDER_TYPE,int precision)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				
				query 	=  "SELECT SI.ItemCode, (SI.ReceivedQty - SI.UnloadedQty) AS SellableQuantity, SI.BatchNumber,SI.ExpiryDate, (SI.ReceivedQty - SI.UnloadedQty), " +
						   "TF .UOM, TF .Factor,SI.Reason From tblNonSellableItems SI INNER JOIN tblProducts P on P.ItemCode=SI.ItemCode " +
						   "INNER JOIN tblUOMFactor TF ON (TF .ItemCode = SI.ItemCode OR (P.Category='TUB' and TF .ItemCode='110010001')) " +
						   "AND (SI.ReceivedQty - SI.UnloadedQty) > 0 ";
				
			
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.batchCode 	  = cursor.getString(2);
						hhInventryQTDO.expiryDate	  = cursor.getString(3);
						hhInventryQTDO.nonSellQty	  = cursor.getFloat(4);
						hhInventryQTDO.UOM		  	  = cursor.getString(5);
						
						hhInventryQTDO.Reason	  	  = cursor.getString(7);
						
						hhInventryQTDO.totalQt 		  = hhInventryQTDO.totalQt / cursor.getFloat(6);
						hhInventryQTDO.nonSellQty     = (float) StringUtils.round(hhInventryQTDO.nonSellQty / cursor.getFloat(6)+"", precision);//hhInventryQTDO.nonSellQty / cursor.getFloat(6);
						//hhInventryQTDO.nonSellQty     = (float)hhInventryQTDO.nonSellQty / cursor.getFloat(6);//hhInventryQTDO.nonSellQty / cursor.getFloat(6);
						
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						
						String key = cursor.getString(0) + hhInventryQTDO.UOM + hhInventryQTDO.Reason + hhInventryQTDO.expiryDate;
						hmInventory.put(key, hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
	
	/**
	 * 
	 * @param ORDER_TYPE
	 * @return
	 */
	public HashMap<String, HHInventryQTDO> getWarehouseStock(String ORDER_TYPE)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null;
			try
			{
				query 	=  "SELECT DISTINCT WQ.ItemCode, (WQ.SellableQuantity) AS SellableQuantity, WQ.BatchNumber,WQ.ExpiryDate, (WQ.SellableQuantity), "+
						   "TF .UOM, TF .Factor From  tblWareHouseStock WQ INNER JOIN tblProducts P on P.ItemCode=WQ.ItemCode " +
						   "INNER JOIN tblUOMFactor TF ON (TF .ItemCode = WQ.ItemCode OR (P.Category='TUB' and TF .ItemCode='110010001')) WHERE WQ.VehicleCode = '"+ORDER_TYPE+"'";
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						hhInventryQTDO.batchCode 	  = cursor.getString(2);
						hhInventryQTDO.expiryDate	  = cursor.getString(3);
						hhInventryQTDO.nonSellQty	  = cursor.getFloat(4);
						hhInventryQTDO.UOM		  	  = cursor.getString(5);
						
						hhInventryQTDO.totalQt 		  = hhInventryQTDO.totalQt / cursor.getFloat(6);
						hhInventryQTDO.nonSellQty     = hhInventryQTDO.nonSellQty / cursor.getFloat(6);
						
						hhInventryQTDO.totalUnits	  = hhInventryQTDO.totalQt;
						
						hmInventory.put(cursor.getString(0) + hhInventryQTDO.UOM, hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
	/**
	 * 
	 * @param ORDER_TYPE
	 * @return
	 */
	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys_Old(String ORDER_TYPE)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			String query;
			Cursor cursor = null;
			try
			{
				if(ORDER_TYPE.equalsIgnoreCase(AppConstants.LPO_ORDER))
					query 	=  "SELECT SI.ItemCode, SellableQuantity, TP.UnitPerCase, SI.BatchNumber,SI.ExpiryDate, SI.UOM from tblVanStock SI " +
						       "INNER JOIN tblProducts TP ON TP.ItemCode = SI.ItemCode AND SI.SellableQuantity > 0";
				else
					query 	=  "SELECT SI.ItemCode, (SellableQuantity - LPOQuantity) AS SellableQuantity, TP.UnitPerCase, SI.BatchNumber,SI.ExpiryDate, SI.UOM from tblVanStock SI " +
						       "INNER JOIN tblProducts TP ON TP.ItemCode = SI.ItemCode AND SI.SellableQuantity > 0";
				
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getFloat(1);
						hhInventryQTDO.totalUnits	  = (int) hhInventryQTDO.totalQt;
						
						hhInventryQTDO.batchCode 	  = cursor.getString(3);
						hhInventryQTDO.expiryDate	  = cursor.getString(4);
						hhInventryQTDO.UOM		  	  = cursor.getString(5);
						
						hmInventory.put(cursor.getString(0), hhInventryQTDO);
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
	public HashMap<String, Vector<String>> getPricingUOM(String pricing, int orderType)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, Vector<String>> hmUOMFactors = new HashMap<String, Vector<String>>();
			try
			{
				SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
				
				String strQuery ="";
				
				if(orderType == AppStatus.REPLACEMENT_ORDER_TYPE)
					strQuery = "SELECT ItemCode, UOM FROM tblUOMFactor";
				else
					strQuery = "SELECT ItemCode, UOM FROM tblPricing WHERE CUSTOMERPRICINGKEY ='"+pricing+"'";
				
				Cursor c=sqlLite.rawQuery(strQuery, null); 
				if(c.moveToFirst())
				{
					do
					{
						String itemcode = c.getString(0);
						String UOM      = c.getString(1);
						
						Vector<String> arrUOM = new Vector<String>();
						if(hmUOMFactors.containsKey(itemcode))
							arrUOM = hmUOMFactors.get(itemcode);
						
						arrUOM.add(UOM);
						hmUOMFactors.put(itemcode, arrUOM);
					}
					while(c.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		
			return hmUOMFactors;
		}
	}
	public HashMap<String, Float> getUOMFactor()
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, Float> hmInventory = new HashMap<String, Float>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT ItemCode||UOM, Factor FROM tblUOMFactor", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						hmInventory.put(cursor.getString(0), cursor.getFloat(1));
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
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
}
