package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.dataobject.ARInvoiceDo;
import com.winit.baskinrobbin.salesman.dataobject.AdvanceOrderDO;
import com.winit.baskinrobbin.salesman.dataobject.AllUsersDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CheckInDemandInventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.DamageImageDO;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.ItemWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.MallsDetails;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.NewCustomerDO;
import com.winit.baskinrobbin.salesman.dataobject.NonPriceItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDONew;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentInviceDO;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.PricingDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.ReceiptMethodNameDo;
import com.winit.baskinrobbin.salesman.dataobject.SubInventoriesParserDo;
import com.winit.baskinrobbin.salesman.dataobject.TRXTYPEDo;
import com.winit.baskinrobbin.salesman.dataobject.UnUploadedDataDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class CommonDA 
{
	DecimalFormat dff = new DecimalFormat("##.##");
	private OfflineDA offlinDA=new OfflineDA();
	public boolean insertAllCategories(Vector<CategoryDO> vecCategories)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
//			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCategory WHERE CategoryId =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCategory (CategoryId, CategoryName, CategoryIcon) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCategory set CategoryName=?, CategoryIcon=? WHERE CategoryId =?");
			
			for(CategoryDO objCategoryDO :  vecCategories)
			{
//				stmtSelectRec.bindString(1, objCategoryDO.categoryId);
//				long countRec = stmtSelectRec.simpleQueryForLong();
//				if(countRec != 0)
//				{	
				if(objCategoryDO != null )
				{
						stmtUpdate.bindString(1, objCategoryDO.categoryName);
						stmtUpdate.bindString(2, ""+objCategoryDO.categoryIcon);
						stmtUpdate.bindString(3, objCategoryDO.categoryId);
//						stmtUpdate.execute();
//					}
//				}
//				else
						if(stmtUpdate.executeUpdateDelete()<=0)
						{
//					if(objCategoryDO != null )
//					{
						stmtInsert.bindString(1, objCategoryDO.categoryId);
						stmtInsert.bindString(2, objCategoryDO.categoryName);
						stmtInsert.bindString(3, ""+objCategoryDO.categoryIcon);
						stmtInsert.executeInsert();
						}
				}
			}
			
//			stmtSelectRec.close();
			stmtInsert.close();
			stmtUpdate.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
		}
	}
	
	//for Device Varification
	
	public boolean isDeviceVerificationNeeded() 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			boolean isEnabled = true;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "select SettingValue from tblSettings where SettingName = '"+AppConstants.SETTINGS_IS_DEVICE_VERIFICATION_NEEDED+"'";
				cursor 	=  objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					if(cursor.getInt(0) == 0)
						isEnabled = false;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return isEnabled;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return isEnabled;
		}
	}
	public boolean isCanDoClearData() {
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement stmtCount = null;
			boolean isCanDoClearData = true;
			String queryPendingOrder = "Select count(*) from tblOrderHeader where Status=0";
			String queryPendingPayment = "Select count(*) from tblPaymentHeader where Status=0";
			String queryPendingCustomerVisit = "Select count(*) from tblCustomerVisit where Status=0";
			String queryPendingStoreChekc = "Select count(*) from tblStoreCheck where Status=0";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				stmtCount = sqLiteDatabase.compileStatement(queryPendingOrder);
				long count = stmtCount.simpleQueryForLong();
				if (count > 0) {
					isCanDoClearData = false;
				}
				if (isCanDoClearData) {
					stmtCount = sqLiteDatabase
							.compileStatement(queryPendingPayment);
					count = stmtCount.simpleQueryForLong();
					if (count > 0) {
						isCanDoClearData = false;
					}
				}
				if (isCanDoClearData) {
					stmtCount = sqLiteDatabase
							.compileStatement(queryPendingCustomerVisit);
					count = stmtCount.simpleQueryForLong();
					if (count > 0) {
						isCanDoClearData = false;
					}
				}
				if (isCanDoClearData) {
					stmtCount = sqLiteDatabase
							.compileStatement(queryPendingStoreChekc);
					count = stmtCount.simpleQueryForLong();
					if (count > 0) {
						isCanDoClearData = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return isCanDoClearData;
		}
	}
	public Vector<PostPaymentDONew> getAllPaymentsToPostNew(String strPresellerId, String date, String userID, int UPLOAD_DATA)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<PostPaymentDONew> vecPaymentDONews = new Vector<PostPaymentDONew>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase  =  DatabaseHelper.openDataBase();
				String strQuery = "";
				
//				if(UPLOAD_DATA == AppStatus.TODAY_DATA)
//					strQuery =  "SELECT DISTINCT PH.* FROM tblPaymentHeader PH INNER JOIN tblPaymentInvoice PI ON PI.ReceiptId = PH.ReceiptId where PH.Status <= 0 "+ 
//								"AND PI.TrxCode NOT IN (SELECT OrderId FROM tblOrderHeader WHERE Status <= 0) AND PH.PaymentDate like '"+date+"%'";
//				else
				
					/*strQuery =  "SELECT DISTINCT PH.* FROM tblPaymentHeader PH INNER JOIN tblPaymentInvoice PI ON PI.ReceiptId = PH.ReceiptId where PH.Status <= 0 "+ 
							    "AND PI.TrxCode NOT IN (SELECT OrderId FROM tblOrderHeader WHERE Status <= 0) LIMIT "+AppConstants.ORDER_LIMIT;
		*/			
				strQuery="SELECT DISTINCT PH.* FROM tblPaymentHeader PH " +
							"INNER JOIN tblPaymentInvoice PI ON PI.ReceiptId = PH.ReceiptId " +
							"where PH.Status <= 0 AND " +
							"PI.ReceiptId NOT IN " +
							"(select PI.ReceiptId from tblPaymentInvoice PI inner join tblOrderHeader TH on PI.TrxCode=TH.OrderId  where TH.Status = 0) LIMIT "+AppConstants.ORDER_LIMIT;
						
						cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
						
						if(cursor.moveToFirst())
						{
							do
							{
								PostPaymentDONew postPaymentDONew 			= 	new PostPaymentDONew();
								
								postPaymentDONew.AppPaymentId 				= 	cursor.getString(0);
								postPaymentDONew.RowStatus 					= 	cursor.getString(1);
								postPaymentDONew.ReceiptId 					= 	cursor.getString(2);
								postPaymentDONew.PreReceiptId 				= 	cursor.getString(3);
								postPaymentDONew.PaymentDate 				= 	cursor.getString(4);
								postPaymentDONew.SiteId 					= 	cursor.getString(5);
								postPaymentDONew.EmpNo						= 	cursor.getString(6);
								postPaymentDONew.Amount 					= 	cursor.getString(7);
								postPaymentDONew.CurrencyCode				= 	cursor.getString(8);
								postPaymentDONew.Rate 						= 	cursor.getString(9);
								postPaymentDONew.VisitCode 					= 	cursor.getString(10);
								postPaymentDONew.JourneyCode 				= 	userID+CalendarUtils.getOrderPostDate();
								postPaymentDONew.PaymentStatus 				= 	cursor.getString(11);
								postPaymentDONew.CustomerSignature 			= 	cursor.getString(12);
								postPaymentDONew.Status 					= 	cursor.getString(13);
								postPaymentDONew.AppPayementHeaderId 		= 	cursor.getString(14);
								postPaymentDONew.PaymentType 				= 	cursor.getString(15);
								postPaymentDONew.vehicleNo 					= 	cursor.getString(16);
								postPaymentDONew.salesmanCode 				= 	cursor.getString(17);
								postPaymentDONew.Receipt_Method_Name		= 	cursor.getString(18);
								
								String strSubQuery =  "SELECT * FROM tblPaymentDetail where ReceiptNo = '"+postPaymentDONew.ReceiptId+"'";
								Cursor subCursor 	=  sqLiteDatabase.rawQuery(strSubQuery, null);
								if(subCursor.moveToFirst())
								{
									do 
									{
										PostPaymentDetailDONew objPaymentDetailDO = new PostPaymentDetailDONew();
										
										objPaymentDetailDO.RowStatus 			= subCursor.getString(0);
										objPaymentDetailDO.ReceiptNo 			= subCursor.getString(1);
										objPaymentDetailDO.LineNo 				= subCursor.getString(2);
										objPaymentDetailDO.PaymentTypeCode 		= subCursor.getString(3);
										objPaymentDetailDO.BankCode				= subCursor.getString(4);
										objPaymentDetailDO.ChequeDate 			= subCursor.getString(5);
										objPaymentDetailDO.ChequeNo 			= subCursor.getString(6);
										objPaymentDetailDO.CCNo 				= subCursor.getString(7);
										objPaymentDetailDO.CCExpiry 			= subCursor.getString(8);
										objPaymentDetailDO.PaymentStatus 		= subCursor.getString(9);
										objPaymentDetailDO.PaymentNote			= subCursor.getString(10);
										objPaymentDetailDO.UserDefinedBankName  = subCursor.getString(11);
										objPaymentDetailDO.Status 				= subCursor.getString(12);
										objPaymentDetailDO.Amount				= subCursor.getString(13);
										postPaymentDONew.vecPaymentDetailDOs.add(objPaymentDetailDO);
									}
									while (subCursor.moveToNext());
								}
								if(subCursor!=null && !subCursor.isClosed())
									subCursor.close();
								
								String strSubQuery1 =  "SELECT * FROM tblPaymentInvoice where ReceiptId = '"+postPaymentDONew.ReceiptId+"' ORDER BY Amount";
								Cursor subCursor1 	=  sqLiteDatabase.rawQuery(strSubQuery1, null);
								if(subCursor1.moveToFirst())
								{
									do 
									{
										PostPaymentInviceDO paymentInviceDO = new PostPaymentInviceDO();
										
										paymentInviceDO.RowStatus 		= subCursor1.getString(0);
										paymentInviceDO.ReceiptId 		= subCursor1.getString(1);
										paymentInviceDO.TrxCode 		= subCursor1.getString(2);
										paymentInviceDO.TrxType			= subCursor1.getString(3);
										paymentInviceDO.Amount			= subCursor1.getString(4);
										paymentInviceDO.CurrencyCode  	= subCursor1.getString(5);
										paymentInviceDO.Rate 			= subCursor1.getString(6);
										paymentInviceDO.PaymentStatus 	= subCursor1.getString(7);
										paymentInviceDO.PaymentType 	= subCursor1.getString(8);
										paymentInviceDO.CashDiscount	= subCursor1.getString(9);
										
										if(subCursor1.getString(10) != null)
											paymentInviceDO.ebs_ref_no		= subCursor1.getString(10);
										
										
										postPaymentDONew.vecPostPaymentInviceDOs.add(paymentInviceDO);
									}
									while (subCursor1.moveToNext());
								}
								if(subCursor1!=null && !subCursor1.isClosed())
									subCursor1.close();
								vecPaymentDONews.add(postPaymentDONew);
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecPaymentDONews;
		}
	}
	
	
	public Vector<UnUploadedDataDO> getAllPaymentsUnload(String strPresellerId, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecUploadedDataDOs = new Vector<UnUploadedDataDO>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery =  "SELECT ReceiptId, Status FROM tblPaymentHeader where PaymentDate like '"+date+"%'";
				
				cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						UnUploadedDataDO uploadedDataDO	= 	new UnUploadedDataDO();
						uploadedDataDO.strId 		= 	cursor.getString(0);
						if(cursor.getString(1).equalsIgnoreCase("N"))
							uploadedDataDO.status = 0 ;
						else
							uploadedDataDO.status = 1 ;
						
						vecUploadedDataDOs.add(uploadedDataDO);
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecUploadedDataDOs;
		}
	}
	
	public Vector<String> getFreeDeliveryResion()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vecReason = new Vector<String>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase = DatabaseHelper.openDataBase();
					String query = "SELECT Name from tblReasons where Type = '"+AppConstants.FREE_DELIVERY_ORDER+"'";
					cursor 	=  sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do 
						{
							vecReason.add(cursor.getString(0));
						} while (cursor.moveToNext());
						
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecReason;
		}
	}
	//Need to change based on new service
	public Vector<OrderDO> getAllSalesOrderToPost(String empNo, int UPLOAD_DATA)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "";
				
//				if(UPLOAD_DATA == AppStatus.TODAY_DATA)
//					query = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' And " +
//						      "(OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' And ((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='')) " +
//						      "AND (subType !='"+AppConstants.FREE_DELIVERY_ORDER+"' AND SubType !='"+AppConstants.LPO_ORDER+"') LIMIT "+AppConstants.ORDER_LIMIT;
//				
//				else
					query = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' And " +
						      "(((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='' OR Status = 0)) " +
						      "AND (subType !='"+AppConstants.FREE_DELIVERY_ORDER+"' AND SubType !='"+AppConstants.LPO_ORDER+"') LIMIT "+AppConstants.ORDER_LIMIT;
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 					= new OrderDO();
						
						orderDO.OrderId    					= cursor.getString(0);
						orderDO.strUUID						= cursor.getString(1);
						orderDO.JourneyCode					= cursor.getString(2);
						orderDO.VisitCode					= cursor.getString(3);
						orderDO.empNo						= cursor.getString(4);
						orderDO.CustomerSiteId				= cursor.getString(5);
						orderDO.InvoiceDate					= cursor.getString(6);
						orderDO.orderType					= cursor.getString(7);
						orderDO.orderSubType				= cursor.getString(8);
						orderDO.CurrencyCode				= cursor.getString(9);	
						orderDO.PaymentType					= cursor.getString(10);
						
						/*if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || orderDO.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
							orderDO.TotalAmount				= -cursor.getFloat(11);
						else*/
							orderDO.TotalAmount				= cursor.getDouble(11);
						
						orderDO.TrxReasonCode				= cursor.getString(12);
						orderDO.strCustomerSign				= cursor.getString(13);
						orderDO.strPresellerSign			= cursor.getString(14);
						orderDO.PaymentCode					= cursor.getString(15);
						orderDO.LPOCode						= cursor.getString(16);
						orderDO.DeliveryDate				= cursor.getString(17);
						orderDO.StampDate					= cursor.getString(18);
						orderDO.StampImage					= cursor.getString(19);
						
						orderDO.TRXStatus					= cursor.getString(20);
						orderDO.pushStatus					= cursor.getInt(21);
						orderDO.strCustomerName				= cursor.getString(22);
						orderDO.Discount					= cursor.getDouble(24);
						orderDO.vehicleNo					= cursor.getString(25);
						orderDO.salesmanCode				= cursor.getString(26);
						
						orderDO.TRANSACTION_TYPE_VALUE		= cursor.getString(27);
						orderDO.TRANSACTION_TYPE_KEY		= cursor.getString(28);
						
						orderDO.Batch_Source_Name			= cursor.getString(29);
						orderDO.Trx_Type_Name				= cursor.getString(30);
						orderDO.roundOffVal					= cursor.getFloat(31);
						
						orderDO.LPOStatus					= cursor.getString(32);
						orderDO.SourceVehicleCode			= cursor.getString(33);
				//=================Added For VAT=====================
						orderDO.VatAmount					= cursor.getDouble(34);
						orderDO.TotalAmountWithVat			= cursor.getDouble(35);
						orderDO.ProrataTaxAmount			= cursor.getDouble(36);
						orderDO.TotalTax					= cursor.getDouble(37);

						orderDO.vecProductDO 				= getProductsOfOrder(slDatabase, orderDO.OrderId, orderDO.orderType);
						orderDO.vecProductDOPromotions		= getTRXPromotions(slDatabase, orderDO.OrderId);
						if(orderDO.vecProductDO != null && orderDO.vecProductDO.size() > 0)
							vecOrderList.add(orderDO);
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
			return vecOrderList;
		}
	}
	
	//Need to change based on new service
	public  OrderDO  getSalesOrder(String orderId)
	{

		synchronized(MyApplication.MyLock)
		{
			OrderDO orderDO=new OrderDO();
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "";

//				if(UPLOAD_DATA == AppStatus.TODAY_DATA)
//					query = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' And " +
//						      "(OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' And ((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='')) " +
//						      "AND (subType !='"+AppConstants.FREE_DELIVERY_ORDER+"' AND SubType !='"+AppConstants.LPO_ORDER+"') LIMIT "+AppConstants.ORDER_LIMIT;
//
//				else
					query = "SELECT * from tblOrderHeader where orderid='"+orderId+"'";

				cursor		=	slDatabase.rawQuery(query, null);


				if(cursor.moveToFirst())
				{


						orderDO.OrderId    					= cursor.getString(0);
						orderDO.strUUID						= cursor.getString(1);
						orderDO.JourneyCode					= cursor.getString(2);
						orderDO.VisitCode					= cursor.getString(3);
						orderDO.empNo						= cursor.getString(4);
						orderDO.CustomerSiteId				= cursor.getString(5);
						orderDO.InvoiceDate					= cursor.getString(6);
						orderDO.orderType					= cursor.getString(7);
						orderDO.orderSubType				= cursor.getString(8);
						orderDO.CurrencyCode				= cursor.getString(9);
						orderDO.PaymentType					= cursor.getString(10);

						/*if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || orderDO.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
							orderDO.TotalAmount				= -cursor.getFloat(11);
						else*/
							orderDO.TotalAmount				= cursor.getDouble(11);

						orderDO.TrxReasonCode				= cursor.getString(12);
						orderDO.strCustomerSign				= cursor.getString(13);
						orderDO.strPresellerSign			= cursor.getString(14);
						orderDO.PaymentCode					= cursor.getString(15);
						orderDO.LPOCode						= cursor.getString(16);
						orderDO.DeliveryDate				= cursor.getString(17);
						orderDO.StampDate					= cursor.getString(18);
						orderDO.StampImage					= cursor.getString(19);

						orderDO.TRXStatus					= cursor.getString(20);
						orderDO.pushStatus					= cursor.getInt(21);
						orderDO.strCustomerName				= cursor.getString(22);
						orderDO.Discount					= cursor.getDouble(24);
						orderDO.vehicleNo					= cursor.getString(25);
						orderDO.salesmanCode				= cursor.getString(26);

						orderDO.TRANSACTION_TYPE_VALUE		= cursor.getString(27);
						orderDO.TRANSACTION_TYPE_KEY		= cursor.getString(28);

						orderDO.Batch_Source_Name			= cursor.getString(29);
						orderDO.Trx_Type_Name				= cursor.getString(30);
						orderDO.roundOffVal					= cursor.getFloat(31);

						orderDO.LPOStatus					= cursor.getString(32);
						orderDO.SourceVehicleCode			= cursor.getString(33);
				//=================Added For VAT=====================
						orderDO.VatAmount					= cursor.getFloat(34);
						orderDO.TotalAmountWithVat			= cursor.getFloat(35);
						orderDO.ProrataTaxAmount			= cursor.getFloat(36);
						orderDO.TotalTax					= cursor.getFloat(37);

						orderDO.vecProductDO 				= getProductsOfOrder(slDatabase, orderDO.OrderId, orderDO.orderType);
						orderDO.vecProductDOPromotions		= getTRXPromotions(slDatabase, orderDO.OrderId);


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
			return orderDO;
		}
	}

	//Need to change based on new service
	public Vector<OrderDO> getAllLPOOrderToPost(String empNo, int UPLOAD_DATA)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "";
				
				if(UPLOAD_DATA == AppStatus.TODAY_DATA)
					query = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' AND  "+
							"SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'  "+ 
							"and LPOStatus = -1 AND Status != 1 LIMIT "+AppConstants.ORDER_LIMIT;
				else
					query = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' AND  "+
							"SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'  "+ 
							"and LPOStatus = -1 AND Status != 1 LIMIT "+AppConstants.ORDER_LIMIT;
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 					= new OrderDO();
						
						orderDO.OrderId    					= cursor.getString(0);
						orderDO.strUUID						= cursor.getString(1);
						orderDO.JourneyCode					= cursor.getString(2);
						orderDO.VisitCode					= cursor.getString(3);
						orderDO.empNo						= cursor.getString(4);
						orderDO.CustomerSiteId				= cursor.getString(5);
						orderDO.InvoiceDate					= cursor.getString(6);
						orderDO.orderType					= cursor.getString(7);
						orderDO.orderSubType				= cursor.getString(8);
						orderDO.CurrencyCode				= cursor.getString(9);	
						orderDO.PaymentType					= cursor.getString(10);
						
						if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) 
								|| orderDO.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
							orderDO.TotalAmount				= -cursor.getDouble(11);
						else
							orderDO.TotalAmount				= cursor.getDouble(11);
						
						orderDO.TrxReasonCode				= cursor.getString(12);
						orderDO.strCustomerSign				= cursor.getString(13);
						orderDO.strPresellerSign			= cursor.getString(14);
						orderDO.PaymentCode					= cursor.getString(15);
						orderDO.LPOCode						= cursor.getString(16);
						orderDO.DeliveryDate				= cursor.getString(17);
						orderDO.StampDate					= cursor.getString(18);
						orderDO.StampImage					= cursor.getString(19);
						
						orderDO.TRXStatus					= cursor.getString(20);
						orderDO.pushStatus					= cursor.getInt(21);
						orderDO.strCustomerName				= cursor.getString(22);
						orderDO.Discount					= cursor.getDouble(24);
						orderDO.vehicleNo					= cursor.getString(25);
						orderDO.salesmanCode				= cursor.getString(26);
						
						orderDO.TRANSACTION_TYPE_VALUE		= cursor.getString(27);
						orderDO.TRANSACTION_TYPE_KEY		= cursor.getString(28);
						
						orderDO.Batch_Source_Name			= cursor.getString(29);
						orderDO.Trx_Type_Name				= cursor.getString(30);
						orderDO.roundOffVal					= cursor.getFloat(31);
						
						orderDO.LPOStatus					= cursor.getString(32);
						orderDO.SourceVehicleCode			= cursor.getString(33);
						
						orderDO.vecProductDO 				= getProductsOfOrder(slDatabase, orderDO.OrderId, orderDO.orderType);
						orderDO.vecProductDOPromotions		= getTRXPromotions(slDatabase, orderDO.OrderId);
						if(orderDO.vecProductDO != null && orderDO.vecProductDO.size() > 0)
							vecOrderList.add(orderDO);
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
			return vecOrderList;
		}
	}
	
	//Need to change based on new service
		public Vector<OrderDO> getLPOOrderToUpdate(String empNo, int UPLOAD_DATA)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "";
					
					query = "SELECT * from tblOrderHeader where subType ='"+AppConstants.LPO_ORDER+"' AND LPOStatus = "+AppStatus.LPO_STATUS_DELIVERED+" LIMIT "+AppConstants.ORDER_LIMIT;
//					if(UPLOAD_DATA == AppStatus.TODAY_DATA)
//					else
//						query = "SELECT * from tblOrderHeader where LPOStatus = "+AppStatus.LPO_STATUS_DELIVERED+" AND SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' LIMIT "+AppConstants.ORDER_LIMIT;
					
					cursor		=	slDatabase.rawQuery(query, null);
				
					if(cursor.moveToFirst())
					{
						do
						{
							OrderDO orderDO 					= new OrderDO();
							
							orderDO.OrderId    					= cursor.getString(0);
							orderDO.strUUID						= cursor.getString(1);
							orderDO.JourneyCode					= cursor.getString(2);
							orderDO.VisitCode					= cursor.getString(3);
							orderDO.empNo						= cursor.getString(4);
							orderDO.CustomerSiteId				= cursor.getString(5);
							orderDO.InvoiceDate					= cursor.getString(6);
							orderDO.orderType					= cursor.getString(7);
							orderDO.orderSubType				= cursor.getString(8);
							orderDO.CurrencyCode				= cursor.getString(9);	
							orderDO.PaymentType					= cursor.getString(10);
							
							if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || orderDO.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
								orderDO.TotalAmount				= -cursor.getDouble(11);
							else
								orderDO.TotalAmount				= cursor.getDouble(11);
							
							orderDO.TrxReasonCode				= cursor.getString(12);
							orderDO.strCustomerSign				= cursor.getString(13);
							orderDO.strPresellerSign			= cursor.getString(14);
							orderDO.PaymentCode					= cursor.getString(15);
							orderDO.LPOCode						= cursor.getString(16);
							orderDO.DeliveryDate				= cursor.getString(17);
							orderDO.StampDate					= cursor.getString(18);
							orderDO.StampImage					= cursor.getString(19);
							
							orderDO.TRXStatus					= cursor.getString(20);
							orderDO.pushStatus					= cursor.getInt(21);
							orderDO.strCustomerName				= cursor.getString(22);
							orderDO.Discount					= cursor.getDouble(24);
							orderDO.vehicleNo					= cursor.getString(25);
							orderDO.salesmanCode				= cursor.getString(26);
							
							orderDO.TRANSACTION_TYPE_VALUE		= cursor.getString(27);
							orderDO.TRANSACTION_TYPE_KEY		= cursor.getString(28);
							
							orderDO.Batch_Source_Name			= cursor.getString(29);
							orderDO.Trx_Type_Name				= cursor.getString(30);
							orderDO.roundOffVal					= cursor.getFloat(31);
							
							orderDO.vecProductDO 				= getProductsOfOrder(slDatabase, orderDO.OrderId, orderDO.orderType);
							orderDO.vecProductDOPromotions		= getTRXPromotions(slDatabase, orderDO.OrderId);
							if(orderDO.vecProductDO != null && orderDO.vecProductDO.size() > 0)
								vecOrderList.add(orderDO);
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
				return vecOrderList;
			}
		}
	
	//Need to change based on new service
		public boolean getAllSalesUnuploaded(String empNo, int TYPE)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				boolean isNoRecord= true;
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "";
					
					if(TYPE == AppStatus.TODAY_DATA)
						query = "SELECT COUNT(*) from tblOrderHeader where EmpNo ='"+empNo+"' And " +
								"(OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' And ((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='')) " +
								"AND (subType !='"+AppConstants.FREE_DELIVERY_ORDER+"' AND SubType !='"+AppConstants.LPO_ORDER+"') LIMIT "+AppConstants.ORDER_LIMIT;
					else
						query = "SELECT COUNT(*) from tblOrderHeader where EmpNo ='"+empNo+"' And " +
						      "(((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='' OR Status = 0)) " +
						      "AND (subType !='"+AppConstants.FREE_DELIVERY_ORDER+"' AND SubType !='"+AppConstants.LPO_ORDER+"') LIMIT "+AppConstants.ORDER_LIMIT;
					
					
					cursor		=	slDatabase.rawQuery(query, null);
				
					if(cursor.moveToFirst())
					{
						if(cursor.getInt(0) > 0)
							isNoRecord = false;
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
				return isNoRecord;
			}
		}
		
		public boolean getAllLpoUnuploaded(String empNo, int TYPE)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				boolean isNoRecord= true;
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "";
					
					if(TYPE == AppStatus.TODAY_DATA)
						query = "SELECT COUNT(*) from tblOrderHeader where EmpNo ='"+empNo+"' And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' AND "+
								"SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'  "+ 
								"and LPOStatus = -1 AND Status != 1 LIMIT "+AppConstants.ORDER_LIMIT;
					else
						query = "SELECT COUNT(*) from tblOrderHeader where EmpNo ='"+empNo+"' AND "+
								"SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'  "+ 
								"and LPOStatus = -1 AND Status != 1 LIMIT "+AppConstants.ORDER_LIMIT;
					
					
					cursor		=	slDatabase.rawQuery(query, null);
				
					if(cursor.moveToFirst())
					{
						if(cursor.getInt(0) > 0)
							isNoRecord = false;
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
				return isNoRecord;
			}
		}
		
		public boolean getAllLpoUpdated(String empNo, int TYPE)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				boolean isNoRecord= true;
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "";
					
					query = "SELECT COUNT(*) from tblOrderHeader where subType ='"+AppConstants.LPO_ORDER+"' AND LPOStatus = "+AppStatus.LPO_STATUS_DELIVERED+" LIMIT "+AppConstants.ORDER_LIMIT;
					
					cursor		=	slDatabase.rawQuery(query, null);
				
					if(cursor.moveToFirst())
					{
						if(cursor.getInt(0) > 0)
							isNoRecord = false;
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
				return isNoRecord;
			}
		}
		
		public boolean getAllOnHoldOrderUploaded(String empNo, int TYPE)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				boolean isNoRecord= true;
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "";
					
					query = "SELECT COUNT(*) from tblOrderHeader where EmpNo ='"+empNo+"' AND TRXStatus = 'D' and Status = 10 AND SubType != 'LPO Order'";
					
					cursor		=	slDatabase.rawQuery(query, null);
				
					if(cursor.moveToFirst())
					{
						if(cursor.getInt(0) > 0)
							isNoRecord = false;
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
				return isNoRecord;
			}
		}
		
		public Vector<String> getOnHoldOrderToDeliverNew(String empNo)
		{
			synchronized(MyApplication.MyLock) 
			{
				SQLiteDatabase slDatabase = null;
				Cursor cursor = null;
				Vector<String> vecOrderList = new Vector<String>();
				try
				{
					slDatabase 	 = 	DatabaseHelper.openDataBase();
					String query = "SELECT OrderId from tblOrderHeader where EmpNo ='"+empNo+"' AND TRXStatus = 'D' and Status = 10 AND SubType != 'LPO Order' LIMIT 1";
					cursor		 =	slDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do
						{
							vecOrderList.add(cursor.getString(0));
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
				return vecOrderList;
			}
		}
		
		
	//Need to change based on new service
	public Vector<OrderDO> getOnHoldOrderToDeliver(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			OrderDO orderdo;
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "SELECT OrderId,CustomerSignature,SalesmanSignature from tblOrderHeader where EmpNo ='"+empNo+"' AND TRXStatus = 'D' and Status = 10 AND SubType != 'LPO Order'";
				cursor		 =	slDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						orderdo=new OrderDO();
						orderdo.OrderId=cursor.getString(0);
						orderdo.strCustomerSign=cursor.getString(1);
						orderdo.strPresellerSign=cursor.getString(2);
						
						vecOrderList.add(orderdo);
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
			return vecOrderList;
		}
	}
	//Need to change based on new service
	public Vector<OrderDO> getSignatureforAllOrder(String empNo,String signType)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			OrderDO orderdo;
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query="";
				if(signType.equalsIgnoreCase("Customer"))
				 query = "SELECT OrderId, CustomerSignature from tblOrderHeader where IsCustomerSigPushed='false' COLLATE NOCASE";
				else
					query = "SELECT OrderId, SalesmanSignature from tblOrderHeader where IsSalesmanSigPushed='false' COLLATE NOCASE";
				cursor		 =	slDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						orderdo=new OrderDO();
						orderdo.OrderId=cursor.getString(0);
						orderdo.strCustomerSign=cursor.getString(1);


						vecOrderList.add(orderdo);
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
			return vecOrderList;
		}
	}
	public Vector<OrderDO> getSignaturePaymentforAllOrder(String empNo,String signType)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			OrderDO orderdo;
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "SELECT ReceiptId,CustomerSignature from tblPaymentHeader where IsCustomerSigPushed='false' COLLATE NOCASE";
				cursor		 =	slDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						orderdo=new OrderDO();
						orderdo.OrderId=cursor.getString(0);
						orderdo.strCustomerSign=cursor.getString(1);


						vecOrderList.add(orderdo);
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
			return vecOrderList;
		}
	}

	//Need to change based on new service
	public Vector<String> getLPOToDeliver(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<String> vecOrderList = new Vector<String>();
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String query = "SELECT OrderId from tblOrderHeader where EmpNo ='"+empNo+"' AND TRXStatus = 'D' and Status = 10 AND SubType = '"+AppConstants.LPO_ORDER+"' LIMIT 1";
				cursor		 =	slDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						vecOrderList.add(cursor.getString(0));
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
			return vecOrderList;
		}
	}
	
	public Vector<OrderDO> getAllFreeDeliveryOrderToPost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<OrderDO> vecOrderList = new Vector<OrderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String QUERY = "SELECT * from tblOrderHeader where EmpNo ='"+empNo+"' And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' " +
						       "and (Status != 1 OR Status ='') and SubType ='"+AppConstants.FREE_DELIVERY_ORDER+"'";
				
				cursor		=	slDatabase.rawQuery(QUERY, null);
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 					= new OrderDO();
						orderDO.OrderId    					= cursor.getString(0);
						orderDO.strUUID						= cursor.getString(1);
						orderDO.JourneyCode					= cursor.getString(2);
						orderDO.VisitCode					= cursor.getString(3);
						orderDO.empNo						= cursor.getString(4);
						orderDO.CustomerSiteId				= cursor.getString(5);
						orderDO.InvoiceDate					= cursor.getString(6);
						orderDO.orderType					= cursor.getString(7);
						orderDO.orderSubType				= cursor.getString(8);
						orderDO.CurrencyCode				= cursor.getString(9);	
						orderDO.PaymentType					= cursor.getString(10);
						orderDO.TotalAmount					= cursor.getDouble(11);
						orderDO.TrxReasonCode				= cursor.getString(12);
						orderDO.strCustomerSign				= cursor.getString(13);
						orderDO.strPresellerSign			= cursor.getString(14);
						orderDO.PaymentCode					= cursor.getString(15);
						orderDO.LPOCode						= cursor.getString(16);
						orderDO.DeliveryDate				= cursor.getString(17);
						orderDO.StampDate					= cursor.getString(18);
						orderDO.StampImage					= cursor.getString(16);
						
						orderDO.TRXStatus					= cursor.getString(20);
						orderDO.pushStatus					= cursor.getInt(21);
						orderDO.strCustomerName				= cursor.getString(22);
						orderDO.vehicleNo					= cursor.getString(25);
						orderDO.salesmanCode				= cursor.getString(26);
						
						orderDO.vecProductDO 				= getProductsOfOrder(slDatabase, orderDO.OrderId, orderDO.orderType);
						orderDO.vecProductDOPromotions		= getTRXPromotions(slDatabase, orderDO.OrderId);
						if(orderDO.vecProductDO != null && orderDO.vecProductDO.size() > 0)
							vecOrderList.add(orderDO);
						
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
			return vecOrderList;
		}
	}
	
	//Need to change.
	public boolean isOrderPlace(String siteNo, String orderDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			boolean isOderPlace = false;
			try
			{
				slDatabase 	 = 	DatabaseHelper.openDataBase();
				String QUERY =  "Select COUNT(*) from tblOrderHeader where SiteNo = '"+siteNo+"' and ((OrderDate like '"+orderDate+"%' AND TRXStatus = 'D') OR " +
								"(DeliveryDate like '"+orderDate+"%' AND LPOStatus > "+AppStatus.LPO_STATUS_APROOVED+" " +
								"AND SubType = '"+AppConstants.LPO_ORDER+"'))";
				cursor		=	slDatabase.rawQuery(QUERY, null);
				
				if(cursor.moveToFirst())
				{
					if(cursor.getInt(0) > 0)
						isOderPlace = true;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return isOderPlace;
		}
	}
	
	public Vector<AdvanceOrderDO> getAllAdvanceOrderToPost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<AdvanceOrderDO> vecOrderList = new Vector<AdvanceOrderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String QUERY = "SELECT OrderId, AppOrderId, TRXStatus from tblOrderHeader where EmpNo ='"+empNo+"'" +
						       " And DeliveryDate like '%"+CalendarUtils.getOrderPostDate()+"%' " +
						       "and (Status == 1 OR Status ='') " +
						       "and ((SubType ='"+AppConstants.LPO_ORDER+"' OR SubType ='"+AppConstants.MOVE_ORDER+"') and TRXStatus = 'D')";
				
				cursor		=	slDatabase.rawQuery(QUERY, null);
				if(cursor.moveToFirst())
				{
					do
					{
						AdvanceOrderDO orderDO 				= new AdvanceOrderDO();
						
						orderDO.OrderId    					= cursor.getString(0);
						orderDO.AppId						= cursor.getString(1);
						orderDO.TRXStatus					= cursor.getString(2);
						vecOrderList.add(orderDO);
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
			return vecOrderList;
		}
	}
	public Vector<UnUploadedDataDO> getAllSalesOrderUnupload(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecUnUploadedDataDOs = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct OrderId, Status from tblOrderHeader where EmpNo ='"+strPresellerId+"'  And OrderDate like '"+CalendarUtils.getOrderPostDate()+"%' and SubType != '"+AppConstants.FREE_DELIVERY_ORDER+"' and SubType != '"+AppConstants.LPO_ORDER+"'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO unUploadedDataDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						unUploadedDataDO.strId 			= 	data[i][0].value.toString();
						unUploadedDataDO.status 		= 	StringUtils.getInt(data[i][1].value.toString());
						vecUnUploadedDataDOs.add(unUploadedDataDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
			return vecUnUploadedDataDOs;
		}
	}
	
	// Need to change based on the new services
//	public Vector<FinalOrderDO> getAllFreeDeliveryItemsOrderToPost(String strPresellerId)
//	{
//		synchronized(MyApplication.MyLock) 
//		{
//			Vector<FinalOrderDO> vecOrderList = new Vector<FinalOrderDO>();
//			try
//			{
//				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct o.orderid,o.PresellerId,o.PresellerId, o.PresellerId, o.DeliveryStatus, o.InvoiceDate, o.CustomerSiteId,o.CustomerSiteId,o.CustomerSiteId,o.PresellerSign,o.CustomerSign,o.DeliveryDate,o.TRANS_TYPE_NAME,o.LPONO,o.UUID,o.Message from tblOrderHeader o where o.presellerid ='"+strPresellerId+"'  And o.InvoiceDate like '%"+CalendarUtils.getOrderPostDate()+"%' and PushStatus == 1 and o.LPONO ='"+AppConstants.FREE_DELIVERY_ORDER+"'");
//				if(data != null && data.length > 0)
//				{
//					for(int i = 0 ; i < data.length ; i++)
//					{
//						FinalOrderDO finalOrderDO 			= 	new FinalOrderDO();
//						if(data[i][0].value!=null)
//						finalOrderDO.strOrderNo 			= 	data[i][0].value.toString();
//						finalOrderDO.strSalesmanCode		= 	data[i][1].value.toString();
//						finalOrderDO.strSalesmanName		=	data[i][2].value.toString();
//						finalOrderDO.strCustomerPriceClass 	= 	data[i][3].value.toString();
//						
//						if(finalOrderDO.strOrderStatus != null)
//							finalOrderDO.strOrderStatus		= 	data[i][4].value.toString();
//						else
//							finalOrderDO.strOrderStatus		= 	"E";
//						
//						finalOrderDO.strOrderDate			= 	data[i][5].value.toString();
//						finalOrderDO.strCustomerName 		= 	data[i][6].value.toString();
//						if(data[i][7].value!=null && data[i][7].value.toString()!=null)
//							finalOrderDO.strCustomerSiteId		= 	data[i][7].value.toString();
//						else
//							finalOrderDO.strCustomerSiteId = "";
//						finalOrderDO.strCustomerId			= 	data[i][8].value.toString();
//						finalOrderDO.strLineNumber			= 	"1";
//						finalOrderDO.strCurrencyCode		= 	"AED";
//						finalOrderDO.strPresellerSign		= 	data[i][9].value.toString();
//						finalOrderDO.strCustomerSign		= 	data[i][10].value.toString();
//						finalOrderDO.strDeliveryDate		= 	""+data[i][11].value.toString();
//						finalOrderDO.strOrder_Type			= 	""+data[i][12].value.toString();
//						finalOrderDO.orderSubType			= 	""+data[i][13].value.toString();
//						
//						if(data[i][14].value != null)
//							finalOrderDO.strUUID			= 	""+data[i][14].value.toString();
//						else
//							finalOrderDO.strUUID			= 	"";
//						
//						if(data[i][15].value != null)
//							finalOrderDO.strMessage			= 	""+data[i][15].value.toString();
//						else
//							finalOrderDO.strMessage			= 	"";
//						
//						finalOrderDO.vecProducts 			=   getProductsOfOrder(finalOrderDO.strOrderNo);
//						if(finalOrderDO.vecProducts != null && finalOrderDO.vecProducts.size() > 0)
//							vecOrderList.add(finalOrderDO);
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				DatabaseHelper.closedatabase();
//			}
//			return vecOrderList;
//		}
//	}
	
	
	public Vector<UnUploadedDataDO> getAllFreeDeliveryUnuploadData(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecOrderList = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct OrderId, Status from tblOrderHeader where EmpNo ='"+strPresellerId+"'  And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' and SubType ='"+AppConstants.FREE_DELIVERY_ORDER+"'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO finalOrderDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						finalOrderDO.strId 			= 	data[i][0].value.toString();
						finalOrderDO.status			= 	StringUtils.getInt(data[i][1].value.toString());
						vecOrderList.add(finalOrderDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
	
			return vecOrderList;
		}
	}
	
	public Vector<UnUploadedDataDO> getAllAdvanceDeliveryUnuploadData(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecOrderList = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct OrderId, Status from tblOrderHeader where EmpNo ='"+strPresellerId+"'  And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' and SubType ='"+AppConstants.LPO_ORDER+"'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO finalOrderDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						finalOrderDO.strId 			= 	data[i][0].value.toString();
						finalOrderDO.status			= 	StringUtils.getInt(data[i][1].value.toString());
						vecOrderList.add(finalOrderDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
	
			return vecOrderList;
		}
	}
	public Vector<ProductDO> getProductsOfOrder(SQLiteDatabase sqLiteDatabase, String orderID, String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<ProductDO> vecItemList = new Vector<ProductDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblOrderDetail where OrderNo ='"+orderID+"'", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 	= 	new ProductDO();
						
						productDO.LineNo				= cursor.getString(0);
						productDO.OrderNo				= cursor.getString(1);
						productDO.SKU					= cursor.getString(2);
						productDO.ItemType				= cursor.getString(3);
						productDO.Description			= cursor.getString(4);
						productDO.itemPrice				= cursor.getFloat(5);
						productDO.unitSellingPrice		= cursor.getFloat(6);
						productDO.UOM					= cursor.getString(7);
						
						productDO.cases				= cursor.getString(8);
						
						productDO.units					= cursor.getString(9);
						productDO.totalCases			= cursor.getFloat(11);
						
						productDO.totalPrice			= cursor.getDouble(12);
						productDO.invoiceAmount			= cursor.getDouble(13);
						productDO.priceUsedLevel1		= cursor.getDouble(12);
						productDO.priceUsedLevel2		= cursor.getDouble(13);
						productDO.TaxPercentage		= cursor.getFloat(14);
						productDO.discountAmount		= 	cursor.getDouble(15);
						
						productDO.promoCode				= 	cursor.getString(17);
						productDO.Description1			= 	cursor.getString(18);
						productDO.strExpiryDate			= 	cursor.getString(20);
						productDO.reason				= 	cursor.getString(21);
						productDO.RelatedLineId			=	cursor.getString(22);
						
						if(cursor.getString(24) != null)
							productDO.recomUnits		= StringUtils.getFloat(cursor.getString(24));
						
						productDO.depositPrice			= cursor.getFloat(25);
						
						if(type.equalsIgnoreCase(AppConstants.RETURNORDER) || type.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
						{
							if(TextUtils.isEmpty(productDO.strExpiryDate))
								productDO.strExpiryDate = CalendarUtils.getOrderPostDate();
							
							productDO.totalPrice		= -productDO.totalPrice;
							productDO.invoiceAmount		= -productDO.invoiceAmount;
							productDO.priceUsedLevel1	= -productDO.priceUsedLevel1;
							productDO.priceUsedLevel2	= -productDO.priceUsedLevel2;
							productDO.depositPrice		= -productDO.depositPrice;
						}
						else
							productDO.strExpiryDate		= "";
						
						productDO.BatchCode				= cursor.getString(26);
						productDO.discountDesc			= cursor.getString(27);
						
						productDO.remarks			= cursor.getString(28);
						productDO.LotNumber			= cursor.getString(29);

					//===========================Added For TAX==============
						productDO.LineTaxAmount			= cursor.getDouble(31);
						productDO.ProrataTaxAmount		= cursor.getDouble(32);
						productDO.TotalTax				= cursor.getDouble(33);
						productDO.QuantityInStock		= cursor.getFloat(34);
						productDO.RefTrxCode			= cursor.getString(35);
						productDO.arrAppliedTaxes	 	= getappliedTax(sqLiteDatabase, orderID,productDO.LineNo);
						if(TextUtils.isEmpty(productDO.LotNumber))
							productDO.LotNumber = "";
						
						if(productDO.cases == null || productDO.cases.trim().equalsIgnoreCase(""))
							productDO.cases = "0";

						if(productDO.units == null || productDO.units.trim().equalsIgnoreCase(""))
							productDO.units = "0";
						
						
						productDO.arrDiscList = getDiscountDetail(sqLiteDatabase, productDO);
						
						vecItemList.add(productDO);
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
	
			return vecItemList;
		}
	}
	public ArrayList<ItemWiseTaxViewDO> getappliedTax(SQLiteDatabase sqLiteDatabase, String orderID,String lineNo)
	{
		synchronized(MyApplication.MyLock)
		{
			ArrayList<ItemWiseTaxViewDO> vecTaxDo = new ArrayList<ItemWiseTaxViewDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();

				cursor = sqLiteDatabase.rawQuery("SELECT *FROM SalesOrderTax where SalesOrderUID='"+orderID+"' AND SalesOrderLineUID='"+lineNo+"' AND ApplicableAt='Item' ", null);

				if(cursor.moveToFirst())
				{
					do
					{
						ItemWiseTaxViewDO itemwiseDo = new ItemWiseTaxViewDO();
//						itemwiseDo.Id      = cursor.getString(0);
//						itemwiseDo.UID     = cursor.getString(1);
//						itemwiseDo.SalesOrderUID       = cursor.getString(2);
//						itemwiseDo.SalesOrderLineUID    = cursor.getString(3);
						itemwiseDo.TaxUID = cursor.getString(4);
						itemwiseDo.TaxSlabUID = cursor.getString(5);
						itemwiseDo.TaxAmount = StringUtils.getFloat(cursor.getString(6));
						itemwiseDo.TaxName = cursor.getString(7);
						itemwiseDo.ApplicableAt = cursor.getString(8);
						itemwiseDo.DependentTaxUID = cursor.getString(9);
//						itemwiseDo.DependentTaxName = cursor.getString(10);
						itemwiseDo.TaxCalculationType = cursor.getString(11);
						itemwiseDo.BaseTaxRate = StringUtils.getFloat(cursor.getString(12));
						itemwiseDo.RangeStart = StringUtils.getFloat(cursor.getString(13));
						itemwiseDo.RangeEnd = StringUtils.getFloat(cursor.getString(14));
						itemwiseDo.TaxRate = StringUtils.getFloat(cursor.getString(15));
						vecTaxDo.add(itemwiseDo);
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

			return vecTaxDo;
		}
	}
	public ArrayList<DiscountDO> getDiscountDetail(SQLiteDatabase sqLiteDatabase, ProductDO productDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DiscountDO> vecDisc = new ArrayList<DiscountDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblOrderDiscountDetail where ItemLineNo ="+productDO.LineNo+" "+
				               "AND OrderNumber ='"+productDO.OrderNo+"' AND ItemCode ='"+productDO.SKU+"'";
				
				cursor = sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						DiscountDO discountDO 	= 	new DiscountDO();
						
						discountDO.lineNo		= cursor.getString(0);
						discountDO.ItemLineNo	= cursor.getString(1);
						discountDO.OrderNo		= cursor.getString(2);
						discountDO.ItemCode		= cursor.getString(3);
						discountDO.UOM			= cursor.getString(4);
						discountDO.discount		= cursor.getFloat(5);
						discountDO.Quantity		= cursor.getFloat(6);
						discountDO.description	= cursor.getString(7);
						
						vecDisc.add(discountDO);
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
	
			return vecDisc;
		}
	}
	public Vector<DamageImageDO> getDamageIMagePic(int Status, int LIMIT)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase  = null;
			Vector<DamageImageDO> vecImage = new Vector<DamageImageDO>();
			Cursor cursor = null;
			try
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				cursor 			= 	sqLiteDatabase.rawQuery("SELECT * FROM tblOrderImage where CapturedDate ="+Status +" LIMIT "+LIMIT , null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						DamageImageDO damageImageDO = new DamageImageDO();
						damageImageDO.OrderNo      = cursor.getString(0);
						damageImageDO.ItemCode     = cursor.getString(1);
						damageImageDO.LineNo       = cursor.getString(2);
						damageImageDO.ImagePath    = cursor.getString(3);
						damageImageDO.CapturedDate = cursor.getString(4);
						vecImage.add(damageImageDO);
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
	
			return vecImage;
		}
	}
	
	public Vector<ProductDO> getTRXPromotions(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<ProductDO> vecItemList = new Vector<ProductDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				cursor = sqLiteDatabase.rawQuery("SELECT * FROM tblTrxPromotion where OrderId ='"+orderID+"'", null);
				int count = 1;
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 	= 	new ProductDO();
						
						productDO.LineNo				= ""+count++;
						productDO.OrderNo				= cursor.getString(1);
						productDO.SKU					= cursor.getString(2);
						productDO.discountAmount		= cursor.getDouble(3);
						productDO.Discount				= cursor.getFloat(4);
						productDO.promotionId			= cursor.getString(6);
						productDO.promotionType			= cursor.getString(11);
						productDO.ItemType				= cursor.getString(13);
						productDO.IsStructural			= cursor.getString(14);
						
						vecItemList.add(productDO);
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
	
			return vecItemList;
		}
	}
	public boolean updatePaymentStatus(PostPaymentDONew objPaymentDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentHeader set Status = ? where ReceiptId =?");
				
				stmtUpdateOrder.bindString(1, "1");
				stmtUpdateOrder.bindString(2, objPaymentDO.ReceiptId);
				stmtUpdateOrder.execute();
				
				stmtUpdateOrder.close();
				
				
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
	public boolean updatePaymentStatus(Vector<PostPaymentDONew> vecPayments)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentHeader set Status = ? where ReceiptId =?");
				for(PostPaymentDONew objPaymentDO : vecPayments)
				{
					stmtUpdateOrder.bindString(1, "1");
					stmtUpdateOrder.bindString(2, objPaymentDO.ReceiptId);
					stmtUpdateOrder.execute();
				}
				stmtUpdateOrder.close();
				
				
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
	
	public boolean updatePayments(Vector<AllUsersDo> vecPayments)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdatePayment 	    = objSqliteDB.compileStatement("UPDATE tblPaymentHeader SET ReceiptId =?, status =? where ReceiptId=?");
				//updating the table tblPaymentDetail
				SQLiteStatement stmtUpdatePaymentDetail = objSqliteDB.compileStatement("UPDATE tblPaymentDetail SET ReceiptNo =? where ReceiptNo=?");
				
				SQLiteStatement stmtUpdateInvoiceDetail = objSqliteDB.compileStatement("UPDATE tblPaymentInvoice SET ReceiptId =? where ReceiptId=?");
				
				
				for(AllUsersDo objAllUsersDo : vecPayments)
				{
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);
					LogUtils.errorLog("objAllUsersDo.New", ""+objAllUsersDo.strNewOrderNumber);
					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdatePayment.bindString(1, objAllUsersDo.strNewOrderNumber);
						stmtUpdatePayment.bindString(2, "1");
						stmtUpdatePayment.bindString(3, objAllUsersDo.strOldOrderNumber);
						stmtUpdatePayment.execute();
					}
					else
					{
						String strReceiptNo =offlinDA.getNextSequenceNumber(OfflineDataType.PAYMENT,objSqliteDB);
						if(!TextUtils.isEmpty(strReceiptNo))
						{
							
						
							stmtUpdatePayment.bindString(1, strReceiptNo);
							stmtUpdatePayment.bindString(2, "0");
							stmtUpdatePayment.bindString(3, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdatePaymentDetail.bindString(1, strReceiptNo);
							stmtUpdatePaymentDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							
							stmtUpdateInvoiceDetail.bindString(1, strReceiptNo);
							stmtUpdateInvoiceDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdatePayment.execute();
							stmtUpdatePaymentDetail.execute();
							stmtUpdateInvoiceDetail.execute();
							offlinDA.updateSequenceNumberStatus(strReceiptNo,objSqliteDB);
						}
						/*String strReceiptNo = "";
						String query 	= 	"SELECT id from tblOfflineData where Type ='"+AppConstants.Receipt+"' AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
							strReceiptNo = cursor.getString(0);
						
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						if(!TextUtils.isEmpty(strReceiptNo))
						{
							objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strReceiptNo+"'");
						
							stmtUpdatePayment.bindString(1, strReceiptNo);
							stmtUpdatePayment.bindString(2, "0");
							stmtUpdatePayment.bindString(3, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdatePaymentDetail.bindString(1, strReceiptNo);
							stmtUpdatePaymentDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							
							stmtUpdateInvoiceDetail.bindString(1, strReceiptNo);
							stmtUpdateInvoiceDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdatePayment.execute();
							stmtUpdatePaymentDetail.execute();
							stmtUpdateInvoiceDetail.execute();
						}*/
					}
				}
				
				stmtUpdatePayment.close();
				stmtUpdatePaymentDetail.close();
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
	
	public boolean updateOrderNumbers(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null, 
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET Status = ?,StampImage=? where OrderId=?");
//				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET Status = ?,CustomerSignature=?,SalesmanSignature=?,StampImage=? where OrderId=?");

				stmtUpdateOrder_empty = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET OrderId =? where OrderId=?");
				
				//updating the table tblOrderDetail
				stmtUpdateDetail = objSqliteDB.compileStatement("UPDATE tblOrderDetail SET OrderNo =? where OrderNo=?");
				
				//updating the table tblTrxPromotion
				stmtUpdatePromo = objSqliteDB.compileStatement("UPDATE tblTrxPromotion SET OrderId =? where OrderId=?");
				
				//updating the table tblPaymentInvoice
				stmtUpdateInvoiceNo = objSqliteDB.compileStatement("UPDATE tblPaymentInvoice SET TrxCode =? where TrxCode=?");
				
				
				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					LogUtils.errorLog("objAllUsersDo.type", ""+objAllUsersDo.strOrderType);
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);
					
					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdateOrder.bindString(1, ""+objAllUsersDo.pushStatus);
						stmtUpdateOrder.bindString(2, "");
//						stmtUpdateOrder.bindString(3, "");
//						stmtUpdateOrder.bindString(4, "");
						stmtUpdateOrder.bindString(3, objAllUsersDo.strOldOrderNumber);
						stmtUpdateOrder.execute();
					}
					else
					{
						String orderType = "", orderId = "";
						Cursor cursor = null;
						String query = "SELECT OrderType FROM tblOrderHeader WHERE OrderId = '"+objAllUsersDo.strOldOrderNumber+"'";
						
						cursor = objSqliteDB.rawQuery(query, null);
						
						if(cursor.moveToFirst())
							orderType = cursor.getString(0);
						
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						if(orderType != null && orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
							orderId=offlinDA.getNextSequenceNumber(OfflineDataType.RETURN);
						else
							orderId=offlinDA.getNextSequenceNumber(OfflineDataType.ORDER);
						
						if(!TextUtils.isEmpty(orderId)){
							
							
							Log.e("orderId", "orderId "+orderId);
							stmtUpdateOrder_empty.bindString(1, orderId);
							stmtUpdateOrder_empty.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateDetail.bindString(1, orderId);
							stmtUpdateDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							
							stmtUpdatePromo.bindString(1, orderId);
							stmtUpdatePromo.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateInvoiceNo.bindString(1, orderId);
							stmtUpdateInvoiceNo.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateOrder_empty.execute();
							stmtUpdateDetail.execute();
							stmtUpdatePromo.execute();
							stmtUpdateInvoiceNo.execute();
							offlinDA.updateSequenceNumberStatus(orderId);
						}
						
						/*if(orderType != null && orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
							query = "SELECT id from tblOfflineData WHERE Type ='"+AppConstants.Return+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
						else
							query = "SELECT id from tblOfflineData WHERE Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
						
						cursor = objSqliteDB.rawQuery(query, null);
						
						if(cursor.moveToFirst())
							orderId = cursor.getString(0);
						
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						if(!TextUtils.isEmpty(orderId))
						{
							objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
							
							Log.e("orderId", "orderId "+orderId);
							stmtUpdateOrder_empty.bindString(1, orderId);
							stmtUpdateOrder_empty.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateDetail.bindString(1, orderId);
							stmtUpdateDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							
							stmtUpdatePromo.bindString(1, orderId);
							stmtUpdatePromo.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateInvoiceNo.bindString(1, orderId);
							stmtUpdateInvoiceNo.bindString(2, objAllUsersDo.strOldOrderNumber);
							
							stmtUpdateOrder_empty.execute();
							stmtUpdateDetail.execute();
							stmtUpdatePromo.execute();
							stmtUpdateInvoiceNo.execute();
						}*/
					}
				}
				
				stmtUpdateDetail.close();
				stmtUpdateOrder.close();
				stmtUpdateInvoiceNo.close();
				stmtUpdateOrder_empty.close();
				
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
	public boolean updateCustomerOrderNumbers(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET IsCustomerSigPushed=? where OrderId=?");




				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					LogUtils.errorLog("objAllUsersDo.type", ""+objAllUsersDo.strOrderType);
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);

					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, objAllUsersDo.strOldOrderNumber);
						stmtUpdateOrder.execute();
					}


				}


				stmtUpdateOrder.close();


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
	public boolean updatePaymentOrderNumbers(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblPaymentHeader SET IsCustomerSigPushed=? where ReceiptId=?");




				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					LogUtils.errorLog("objAllUsersDo.type", ""+objAllUsersDo.strOrderType);
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);

					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, objAllUsersDo.strOldOrderNumber);
						stmtUpdateOrder.execute();
					}


				}


				stmtUpdateOrder.close();


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
	public boolean updateSalesmanOrderNumbers(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET IsSalesmanSigPushed=? where OrderId=?");




				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					LogUtils.errorLog("objAllUsersDo.type", ""+objAllUsersDo.strOrderType);
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);

					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, objAllUsersDo.strOldOrderNumber);
						stmtUpdateOrder.execute();
					}


				}


				stmtUpdateOrder.close();


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
	public boolean updateSalesmanOrderNumbers(OrderDO vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET IsSalesmanSigPushed=? where OrderId=?");








						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, vecOrderlist.OrderId);
						stmtUpdateOrder.execute();


				stmtUpdateOrder.close();


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
	public boolean updateOrderNumbers(OrderDO vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET IsCustomerSigPushed=? where OrderId=?");








						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, vecOrderlist.OrderId);
						stmtUpdateOrder.execute();


				stmtUpdateOrder.close();


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
	public boolean updatePaymentNumbers(OrderDO vecOrderlist)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null,
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null;
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblPaymentHeader SET IsCustomerSigPushed=? where ReceiptId=?");


						stmtUpdateOrder.bindString(1, "true");

						stmtUpdateOrder.bindString(2, vecOrderlist.OrderId);
						stmtUpdateOrder.execute();


				stmtUpdateOrder.close();


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


	public boolean updateLpoOrderStatus(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteStatement stmtUpdateOrder = null;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblOrderHeader SET LPOStatus = "+AppStatus.LPO_STATUS_UPDATED+" where OrderId=?");
				
				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					stmtUpdateOrder.bindString(1, objAllUsersDo.strOldOrderNumber);
					stmtUpdateOrder.execute();
				}
				
				stmtUpdateOrder.close();
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
	
	public Vector<NameIDDo> getReasonsByType(String strType)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<NameIDDo> vecReasons = new Vector<NameIDDo>();
			String strQuery = "Select ReasonId,Name,Type from tblReasons where Type like '%"+strType+"%'";
				
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 
				 cursor 				= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 NameIDDo objReasons 	=  new NameIDDo();
						 objReasons.strId 		=  cursor.getString(0);
						 objReasons.strName		=  cursor.getString(1);
						 objReasons.strType		=  cursor.getString(2);
						 vecReasons.add(objReasons);
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
			 return vecReasons;
		}
	}
	public boolean insertItemPricing(Vector<PricingDO> vecPricingDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblPricing (ITEMCODE, CUSTOMERPRICINGKEY, PRICECASES,ENDDATE,STARTDATE,DISCOUNT,IsExpired,emptyCasePrice, TaxGroupCode,TaxPercentage,ModifiedDate, ModifiedTime, UOM) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblPricing set  PRICECASES=?,ENDDATE=?, STARTDATE=?, DISCOUNT=?,IsExpired=?,emptyCasePrice=?,TaxGroupCode=?,TaxPercentage=?,ModifiedDate=?, ModifiedTime=? WHERE ITEMCODE =? and CUSTOMERPRICINGKEY=? AND UOM=?");
				
				for(PricingDO pricingDO :  vecPricingDOs)
				{
						stmtUpdate.bindString(1, pricingDO.priceCases);
						stmtUpdate.bindString(2, pricingDO.endDate);
						stmtUpdate.bindString(3, pricingDO.startDate);
						stmtUpdate.bindString(4, pricingDO.dicount);
						stmtUpdate.bindString(5, pricingDO.IsExpired);
						stmtUpdate.bindString(6, pricingDO.emptyCasePrice);
						
						stmtUpdate.bindString(7, pricingDO.TaxGroupCode);
						stmtUpdate.bindString(8, pricingDO.TaxPercentage);
						stmtUpdate.bindString(9, pricingDO.ModifiedDate);
						stmtUpdate.bindString(10, pricingDO.ModifiedTime);
						
						stmtUpdate.bindString(11, pricingDO.itemCode);
						stmtUpdate.bindString(12, ""+pricingDO.customerPricingClass);
						stmtUpdate.bindString(13, ""+pricingDO.UOM);
					if(stmtUpdate.executeUpdateDelete()<=0)
					{
						stmtInsert.bindString(1, pricingDO.itemCode);
						stmtInsert.bindString(2, pricingDO.customerPricingClass);
						stmtInsert.bindString(3, pricingDO.priceCases);
						stmtInsert.bindString(4, pricingDO.endDate);
						stmtInsert.bindString(5, pricingDO.startDate);
						stmtInsert.bindString(6, pricingDO.dicount);
						stmtInsert.bindString(7, pricingDO.IsExpired);
						stmtInsert.bindString(8, ""+pricingDO.emptyCasePrice);
						
						stmtInsert.bindString(9, ""+pricingDO.TaxGroupCode);
						stmtInsert.bindString(10, ""+pricingDO.TaxPercentage);
						stmtInsert.bindString(11, ""+pricingDO.ModifiedDate);
						stmtInsert.bindString(12, ""+pricingDO.ModifiedTime);
						stmtInsert.bindString(13, ""+pricingDO.UOM);
						stmtInsert.executeInsert();
					}
				}
				
				stmtInsert.close();
				stmtUpdate.close();
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
			}
			
			return true;
		}
	}
	
	public Vector<NameIDDo> getCategoryProductsAndImages(String categoryId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			
			Vector<NameIDDo> vecCategoryList = null;
			NameIDDo objCategory;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("select PI.ImagePath,P.Description from tblProductImages PI,tblProducts P where  PI.ItemCode = P.SKU and ItemCode in (Select SKU from tblProducts where CategoryId ='"+categoryId+"')", null);
				if(cursor.moveToFirst())
				{
					vecCategoryList = new Vector<NameIDDo>();
					do
					{
						objCategory = new NameIDDo();
						objCategory.strId = cursor.getString(0);
						objCategory.strName = cursor.getString(1);
						vecCategoryList.add(objCategory);
						
					}while(cursor.moveToNext());
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
				
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return vecCategoryList;
		}
	}
	
	public Vector<CategoryDO> getCategoryList()
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase mDatabase = null;
		Cursor cursor  = null;
		
		Vector<CategoryDO> vecCategoryList=null;

		try
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory order by CategoryName", null);
			if(cursor.moveToFirst())
			{
				vecCategoryList = new Vector<CategoryDO>();
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					objCategory.categoryIcon = cursor.getString(2);
					vecCategoryList.add(objCategory);
					
				}while(cursor.moveToNext());
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return vecCategoryList;
		}
	}
	
	public String isAllOrderPushed(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			String OrderId = "";
			SQLiteDatabase sqLiteDatabase =null;
			Cursor cursor = null;
			try
			{
			String query = "SELECT OrderId from tblOrderHeader where " +
					      "(" +
					      "(OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' And ((Status != 1 AND Status != 2 AND Status != 10 AND Status != -10) OR Status ='')) " +
					      "OR " +
					      "(SubType ='"+AppConstants.LPO_ORDER+"' AND OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'  and TRXStatus = 'E' AND Status != 1)" +
					      ") " +
					      "AND subType !='"+AppConstants.FREE_DELIVERY_ORDER+"'" ;
			
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				cursor			=	sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						OrderId = OrderId + cursor.getString(0) +", ";
					}
					while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(OrderId.contains(","))
					OrderId = OrderId.substring(0, OrderId.lastIndexOf(","));
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return OrderId;
		}
	}
	
	public String isAllPaymentPushed(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			String paymentId = "";
			SQLiteDatabase sqLiteDatabase =null;
			Cursor cursor = null;
			try
			{
				String strQuery =  "SELECT DISTINCT ReceiptId FROM tblPaymentHeader where Status <= 0 "+ 
						           "AND PaymentDate like '"+CalendarUtils.getOrderPostDate()+"%'";
				
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				cursor			=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						paymentId = paymentId + cursor.getString(0) +", ";
					}
					while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(paymentId.contains(","))
					paymentId = paymentId.substring(0, paymentId.lastIndexOf(","));
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return paymentId;
		}
	}
	
	//method to get the reason vector by reason type
	public Vector<NameIDDo> getAllBanks()
	{
		synchronized(MyApplication.MyLock) 
		{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select *from tblBanks ORDER BY BankName";
			
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 NameIDDo objBanks 		=  new NameIDDo();
					 objBanks.strId 		=  cursor.getString(0);
					 objBanks.strName		=  cursor.getString(1);
					 objBanks.strType		=  cursor.getString(2);
					 vecBanks.add(objBanks);
				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
			 if(cursor != null)
				 cursor.close();
		 }
		 return vecBanks;
		}
	}
	public int getRoundOffValueFromDatabaseBasedonCountry(String countryName)
	{
		synchronized(MyApplication.MyLock)
		{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select RoundOffDecimals from tblCountry where CurrencyCode ='"+countryName+"'";
         int roundoffNos=2;
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {

					 roundoffNos 		=  cursor.getInt(0);

				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
			 if(cursor != null)
				 cursor.close();
		 }
		 return roundoffNos;
		}
	}

	public Vector<NameIDDo> getAllBanksNew()
	{
		synchronized(MyApplication.MyLock) 
		{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select *from tblBanks ORDER BY BankName";
			
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 NameIDDo objBanks 		=  new NameIDDo();
					 objBanks.strId 		=  cursor.getString(0);
					 objBanks.strName		=  cursor.getString(1);
					 objBanks.strType		=  cursor.getString(2);
					 vecBanks.add(objBanks);
				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor != null)
				 cursor.close();
			 
			 if(objSqliteDB != null)
			 	objSqliteDB.close();
			
		 }
		 return vecBanks;
		}
	}
	
		
	//method to insert or update the banks Detail
	public boolean insertBankDetails(Vector<NameIDDo> vecBankNames)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
//			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblBanks WHERE BankId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblBanks (BankId, BankName, BankCode) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblBanks SET BankName = ?, BankCode =? WHERE BankId = ?");
			
			for(NameIDDo objNameIDDo : vecBankNames)
			{
//				stmtSelectRec.bindString(1, objNameIDDo.strId);
//				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objNameIDDo != null)
				{
//					if (countRec != 0) 
//					{
						stmtUpdate.bindString(1, objNameIDDo.strName);
						stmtUpdate.bindString(2, objNameIDDo.strType);
						stmtUpdate.bindString(3, objNameIDDo.strId);
//						stmtUpdate.execute();
//					}
//					else
					if(stmtUpdate.executeUpdateDelete()<= 0)
					{
						stmtInsert.bindString(1,objNameIDDo.strId);
						stmtInsert.bindString(2, objNameIDDo.strName);
						stmtInsert.bindString(3, objNameIDDo.strType);
						stmtInsert.executeInsert();
					}
				}
			}
			
//			stmtSelectRec.close();
			stmtUpdate.close();
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
	
	//method to insert or update the UOM Detail
		public boolean insertUOMFactorDetails(Vector<NameIDDo> vecUOMFactor)
		{
			synchronized(MyApplication.MyLock) 
			{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				 objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
					
				//Query to Insert the User information in to UserInfo Table
//				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblUOMFactor WHERE ItemCode = ? AND UOM = ?");
				SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUOMFactor (ItemCode, UOM, Factor) VALUES(?,?,?)");
				SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblUOMFactor SET Factor = ? WHERE UOM = ? AND ItemCode = ?");
				
				for(NameIDDo objNameIDDo : vecUOMFactor)
				{
//					stmtSelectRec.bindString(1, objNameIDDo.strId);
//					stmtSelectRec.bindString(2, objNameIDDo.strName);
//					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(objNameIDDo != null)
					{
//						if (countRec != 0) 
//						{
							stmtUpdate.bindString(1, objNameIDDo.strType); // Factor
							stmtUpdate.bindString(2, objNameIDDo.strName); // UOM
							stmtUpdate.bindString(3, objNameIDDo.strId); // ItemCode  
//							stmtUpdate.execute();
//						}
							
//						else
						if(stmtUpdate.executeUpdateDelete()<=0)
						{
							stmtInsert.bindString(1,objNameIDDo.strId);
							stmtInsert.bindString(2, objNameIDDo.strName);
							stmtInsert.bindString(3, objNameIDDo.strType);
							stmtInsert.executeInsert();
						}
					}
				}
				
//				stmtSelectRec.close();
				stmtUpdate.close();
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
	//method to insert or update the AR_INV_METHOD
		public boolean insertArInvoiceMethod(Vector<ARInvoiceDo> vecARInvoice2)
		{
			synchronized(MyApplication.MyLock) 
			{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				 objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
					
				//Query to Insert the AR_INV information in to AR_INV_METHOD Table
//				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblAR_INV_METHOD WHERE CUSTOMER_TRX_TYPE_NAME=? AND CUSTOMER_TRX_TYPE_KEY=? AND BATCH_SOURCE_NAME = ?");
				SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblAR_INV_METHOD (ORG_ID, CUSTOMER_TRX_TYPE_NAME, CUSTOMER_TRX_TYPE_KEY,BATCH_SOURCE_NAME) VALUES(?,?,?,?)");
				SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblAR_INV_METHOD SET ORG_ID = ? WHERE CUSTOMER_TRX_TYPE_NAME=? AND CUSTOMER_TRX_TYPE_KEY=? AND BATCH_SOURCE_NAME = ?");
				
				for(ARInvoiceDo objARInvoiceDo : vecARInvoice2)
				{
//					stmtSelectRec.bindString(1, objARInvoiceDo.strCUSTOMER_TRX_TYPE_NAME);
//					stmtSelectRec.bindString(2, objARInvoiceDo.strCUSTOMER_TRX_TYPE_KEY);
//					stmtSelectRec.bindString(3, objARInvoiceDo.strBATCH_SOURCE_NAME);
//					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(objARInvoiceDo != null)
					{
//						if (countRec != 0) 
//						{
							stmtUpdate.bindLong(1, objARInvoiceDo.ORG_ID);
							stmtUpdate.bindString(2, objARInvoiceDo.strCUSTOMER_TRX_TYPE_NAME);
							stmtUpdate.bindString(3, objARInvoiceDo.strCUSTOMER_TRX_TYPE_KEY);
							stmtUpdate.bindString(4, objARInvoiceDo.strBATCH_SOURCE_NAME);
//							stmtUpdate.execute();
//						}
//						else
						if(stmtUpdate.executeUpdateDelete()<=0)
						{
							stmtInsert.bindLong(1,objARInvoiceDo.ORG_ID);
							stmtInsert.bindString(2, objARInvoiceDo.strCUSTOMER_TRX_TYPE_NAME);
							stmtInsert.bindString(3, objARInvoiceDo.strCUSTOMER_TRX_TYPE_KEY);
							stmtInsert.bindString(4, objARInvoiceDo.strBATCH_SOURCE_NAME);
							stmtInsert.executeInsert();
						}
					}
				}
				
//				stmtSelectRec.close();
				stmtUpdate.close();
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
		//method to get all the AR_INV_METHODs
		
		public Vector<NameIDDo> getAllArInvoiceMethod()
		{
			synchronized(MyApplication.MyLock) 
			{
			Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
			String strQuery = "Select *from tblAR_INV_METHOD";
				
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
						 NameIDDo objARInvoice 		=  new NameIDDo();
						 objARInvoice.strId 		=  cursor.getString(0);
						 objARInvoice.strName		=  cursor.getString(1);
						 vecBanks.add(objARInvoice);
					 }
					 while(cursor.moveToNext());
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor != null)
					 cursor.close();
				 
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return vecBanks;
		}
		}
		public Vector<NameIDDo> getAllArInvoiceMethodByBatchSrc(String strBatchSourceNmae, String TRX_KEY)
		{
			synchronized(MyApplication.MyLock) 
			{
				Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
				String strQuery = "Select *from tblAR_INV_METHOD WHERE BATCH_SOURCE_NAME = '"+strBatchSourceNmae+"' AND CUSTOMER_TRX_TYPE_KEY = '"+TRX_KEY+"'";
					
				 SQLiteDatabase objSqliteDB =null;
				 Cursor cursor 				= null;
				 try
				 {
					 objSqliteDB = DatabaseHelper.openDataBase();
					 cursor 				= objSqliteDB.rawQuery(strQuery, null);
					 if(cursor.moveToFirst())
					 {
						 do
						 {
							 NameIDDo objARInvoice 		=  new NameIDDo();
							 objARInvoice.strId 		=  cursor.getString(0);
							 objARInvoice.strName		=  cursor.getString(1);
							 vecBanks.add(objARInvoice);
						 }
						 while(cursor.moveToNext());
					 }
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
				 finally
				 {
					 if(cursor != null)
						 cursor.close();
					 
					 if(objSqliteDB != null)
					 	objSqliteDB.close();
				 }
				 return vecBanks;
			}
		}
		
		public String getBatchSourceName()
		{
			synchronized(MyApplication.MyLock) 
			{
				String strBatchSourceName = "";
				String strQuery = "Select DISTINCT BATCH_SOURCE_NAME FROM tblAR_INV_METHOD LIMIT 1";
					
				 SQLiteDatabase objSqliteDB =null;
				 Cursor cursor 				= null;
				 try
				 {
					 objSqliteDB = DatabaseHelper.openDataBase();
					 cursor 				= objSqliteDB.rawQuery(strQuery, null);
					 if(cursor.moveToFirst())
					 {
						 do
						 {
							 strBatchSourceName		=  cursor.getString(0);
						 }
						 while(cursor.moveToNext());
					 }
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
				 finally
				 {
					 if(cursor != null)
						 cursor.close();
					 
					 if(objSqliteDB != null)
					 	objSqliteDB.close();
				 }
				 return strBatchSourceName;
			}
		}
		
		//method to insert or update SubInventories
			public boolean insertSubInventories(Vector<SubInventoriesParserDo> vecSubInventories2) 
			{
				synchronized(MyApplication.MyLock) 
				{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the SubInventories information in to tblFromSubInventories Table
//					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblFromSubInventories WHERE VEHICLE_NO = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblFromSubInventories (VEHICLE_NO, VEHICLE_MODEL,SubInventoryType) VALUES(?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblFromSubInventories SET VEHICLE_MODEL = ?,SubInventoryType=? WHERE VEHICLE_NO = ?");
					
					for(SubInventoriesParserDo objSubInventoriesParserDo : vecSubInventories2)
					{
//						stmtSelectRec.bindString(1, objSubInventoriesParserDo.strVEHICLE_NO);
//						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objSubInventoriesParserDo != null)
						{
//							if (countRec != 0) 
//							{
								stmtUpdate.bindString(1, objSubInventoriesParserDo.strVEHICLE_MODEL);
								stmtUpdate.bindString(2, objSubInventoriesParserDo.subInventoryType);
								stmtUpdate.bindString(3, objSubInventoriesParserDo.strVEHICLE_NO);
//								stmtUpdate.execute();
//							}
//							else
							if(stmtUpdate.executeUpdateDelete()<=0)
							{
								stmtInsert.bindString(1, objSubInventoriesParserDo.strVEHICLE_NO);
								stmtInsert.bindString(2, objSubInventoriesParserDo.strVEHICLE_MODEL);
								stmtInsert.bindString(3, objSubInventoriesParserDo.subInventoryType);
								stmtInsert.executeInsert();
							}
						}
					}
					
//					stmtSelectRec.close();
					stmtUpdate.close();
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
		
			//method to get all the SubInventories
			
			public Vector<NameIDDo> getAllSubInventories(String strVehicleNumber, int TYPE)
			{
				synchronized(MyApplication.MyLock) 
				{
				Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
				String strQuery = "";
				
				strQuery = "Select DISTINCT * from tblFromSubInventories Where VEHICLE_NO != '"+strVehicleNumber+"' AND (SubInventoryType = "+TYPE+" OR SubInventoryType ='')";
				
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
							 NameIDDo objSubInventories   =  new NameIDDo();
							 objSubInventories.strId 	  =  cursor.getString(0);
							 objSubInventories.strName	  =  cursor.getString(1);
							 vecBanks.add(objSubInventories);
						 }
						 while(cursor.moveToNext());
					 }
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
				 finally
				 {
					 if(objSqliteDB != null)
					 {
					 	objSqliteDB.close();
					 }
					 if(cursor != null)
						 cursor.close();
				 }
				 return vecBanks;
			}
		}
		
			
			//method to insert or update the ReceiptMethodNam
			public boolean insertReceiptMethodName(Vector<ReceiptMethodNameDo> vecReceiptMethodName2) 
			{
				synchronized(MyApplication.MyLock) 
				{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the ReceiptMethodNam information in to tblRECEIPT_METHOD Table
//					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblRECEIPT_METHOD WHERE RECEIPT_METHOD_NAME = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblRECEIPT_METHOD (ORG_ID, RECEIPT_METHOD_NAME) VALUES(?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblRECEIPT_METHOD SET ORG_ID = ? WHERE RECEIPT_METHOD_NAME = ?");
					
					for(ReceiptMethodNameDo objReceiptMethodNameDo : vecReceiptMethodName2)
					{
//						stmtSelectRec.bindString(1, objReceiptMethodNameDo.strRECEIPT_METHOD_NAME);
//						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objReceiptMethodNameDo != null)
						{
//							if (countRec != 0) 
//							{
								stmtUpdate.bindLong(1, objReceiptMethodNameDo.ORG_ID);
								stmtUpdate.bindString(2, objReceiptMethodNameDo.strRECEIPT_METHOD_NAME);
//								stmtUpdate.execute();
//							}
//							else
							if(stmtUpdate.executeUpdateDelete()<=0)
							{
								stmtInsert.bindString(1,objReceiptMethodNameDo.ORG_ID+"");
								stmtInsert.bindString(2, objReceiptMethodNameDo.strRECEIPT_METHOD_NAME);
								stmtInsert.executeInsert();
							}
						}
					}
					
//					stmtSelectRec.close();
					stmtUpdate.close();
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
					{
						
						objSqliteDB.close();
					}
				}
				return true;
			}
		}
			
				
				//method to get all the SubInventories
				
				public Vector<NameIDDo> getAllReceiptMethodNames()
				{
					synchronized(MyApplication.MyLock) 
				
					{
					Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
					String strQuery = "Select *from tblRECEIPT_METHOD";
						
					 SQLiteDatabase objSqliteDB = null;
					 Cursor cursor 				= null;
					 try
					 {
						 objSqliteDB = DatabaseHelper.openDataBase();
						 cursor 	 = objSqliteDB.rawQuery(strQuery, null);
						 if(cursor.moveToFirst())
						 {
							 do
							 {
								 NameIDDo objSubInventories 		=  new NameIDDo();
								 objSubInventories.strId 			=  cursor.getString(0);
								 objSubInventories.strName			=  cursor.getString(1);
								 vecBanks.add(objSubInventories);
							 }
							 while(cursor.moveToNext());
						 }
					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
					 }
					 finally
					 {
						 if(objSqliteDB != null)
						 {
						 	objSqliteDB.close();
						 }
						 if(cursor != null)
							 cursor.close();
					 }
					 return vecBanks;
				}
				}
				
				
				
				//method to insert or update the TRXTYPE
				public boolean insertTRXTYPE(Vector<TRXTYPEDo> vecTRXTYPE2)
				{
					synchronized(MyApplication.MyLock) 
					{
					SQLiteDatabase objSqliteDB = null;
					try
					{
						 objSqliteDB = DatabaseHelper.openDataBase();
						//Opening the database
							
						//Query to Insert the TRXTYPE information in to tblTRXTYPE_METHOD Table
//						SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblTRXTYPE_METHOD WHERE TRANSACTION_TYPE_NAME = ?");
						SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblTRXTYPE_METHOD (TRANSACTION_TYPE_NAME, TRANSACTION_TYPE_KEY) VALUES(?,?)");
						SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblTRXTYPE_METHOD SET TRANSACTION_TYPE_KEY = ? WHERE TRANSACTION_TYPE_NAME = ?");
						
						for(TRXTYPEDo objTRXTYPEDo : vecTRXTYPE2)
						{
//							stmtSelectRec.bindString(1, objTRXTYPEDo.strTRANSACTION_TYPE_NAME);
//							long countRec = stmtSelectRec.simpleQueryForLong();
							
							if(objTRXTYPEDo != null)
							{
//								if (countRec != 0) 
//								{
									stmtUpdate.bindString(1, objTRXTYPEDo.strTRANSACTION_TYPE_KEY);
									stmtUpdate.bindString(2, objTRXTYPEDo.strTRANSACTION_TYPE_NAME);
//									stmtUpdate.execute();
//								}
//								else
								if(stmtUpdate.executeUpdateDelete()<=0)
								{
									stmtInsert.bindString(1,objTRXTYPEDo.strTRANSACTION_TYPE_NAME);
									stmtInsert.bindString(2, objTRXTYPEDo.strTRANSACTION_TYPE_KEY);
									stmtInsert.executeInsert();
								}
							}
						}
						
//						stmtSelectRec.close();
						stmtUpdate.close();
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
						{
							
							objSqliteDB.close();
						}
					}
					return true;
				}
				}
				
	//method to get all the TRXTYPE
	
	public Vector<NameIDDo> getAllTRXTYPEs()
	{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select *from tblTRXTYPE_METHOD";
			
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 NameIDDo objTRXTYPE 		=  new NameIDDo();
					 objTRXTYPE.strId 			=  cursor.getString(0);
					 objTRXTYPE.strName			=  cursor.getString(1);
					 vecBanks.add(objTRXTYPE);
				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
			 if(cursor != null)
				 cursor.close();
		 }
		 return vecBanks;
	}
	
//	public HashMap<String, Vector<OrderDO>> getDeliveryStatusOrderList(String startdate,String endDate, JourneyPlanDO mallDetails)
//	{
//		synchronized(MyApplication.MyLock)
//		{
//			HashMap<String, Vector<OrderDO>> hmOrders = new HashMap<String, Vector<OrderDO>>();
//			SQLiteDatabase sqLiteDatabase   = null;
//			String strQuery = "";
//			Cursor cursor = null;
//			try
//			{
//				sqLiteDatabase   = DatabaseHelper.openDataBase();
//
//				if(mallDetails == null)
//
////				strQuery ="SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName, " +
////						"CST.ADDRESS1,OT.EmpNo,OT.OrderType,OT.SubType, OT.TRXStatus, OT.Status, OT.DeliveryDate, " +
////						"OT.Batch_Source_Name, OT.Cust_Trx_Type_Name, OT.TotalAmount, OT.LPOCode, OT.DeliveredBy, " +
////						"OT.LPOStatus, OT.RoundOffValue FROM tblOrderHeader OT  LEFT JOIN tblCustomer CST  ON OT.SiteNo=CST.Site" +
////						" where (Date(OT.OrderDate) BETWEEN Date('"+startdate+"')  AND Date( '"+endDate+"') OR (Date(OT.DeliveryDate) BETWEEN Date('"+startdate+"')  AND Date('"+endDate+"') AND OT.SubType LIKE '%App order%') OR OT.TRXStatus = 'H') ORDER BY OT.OrderId DESC";
////
//
//				strQuery="SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName," +
//						" CST.ADDRESS1,OT.EmpNo,OT.OrderType,OT.SubType, OT.TRXStatus, OT.Status, OT.DeliveryDate," +
//						" OT.Batch_Source_Name, OT.Cust_Trx_Type_Name, OT.TotalAmount, OT.LPOCode, OT.DeliveredBy," +
//						" OT.LPOStatus, OT.RoundOffValue,OT.VatAmount FROM tblOrderHeader OT  LEFT JOIN tblCustomer CST  ON OT.SiteNo=CST.Site" +
//						" where (julianday(OT.OrderDate)>=julianday('"+startdate+"')  AND julianday(OT.OrderDate)<=julianday( '"+endDate+"') " +
//								"AND OT.SubType LIKE '%App order%' OR OT.TRXStatus = 'H') AND OT.Status!='-10'  ORDER BY OT.OrderId DESC";
//
//				else
//					strQuery =  "SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName, CST.ADDRESS1,OT.EmpNo,OT.OrderType,OT.SubType, OT.TRXStatus, OT.Status, OT.DeliveryDate, OT.Batch_Source_Name, OT.Cust_Trx_Type_Name, OT.TotalAmount, OT.LPOCode, OT.DeliveredBy, OT.LPOStatus, OT.RoundOffValue " +
//								",OT.VatAmount FROM tblOrderHeader OT  LEFT JOIN tblCustomer CST  ON OT.SiteNo=CST.Site " +
//								"where OT.SiteNo = '"+mallDetails.site+"' AND (julianday(OT.OrderDate)>=julianday('"+startdate+"')  AND julianday(OT.OrderDate)<=julianday( '"+endDate+"') AND OT.SubType LIKE '%LPO%' OR OT.TRXStatus = 'H') AND OT.Status!='-10' ORDER BY OT.OrderId DESC";
//
//				cursor	=	sqLiteDatabase.rawQuery(strQuery,null);
//
//				if(cursor != null && cursor.moveToFirst())
//				{
//					do
//					{
//						OrderDO orderDO 		= 	new OrderDO();
//						orderDO.OrderId  		= 	cursor.getString(0);
//						orderDO.DeliveryStatus 	= 	cursor.getString(1);
//						orderDO.InvoiceDate 	= 	cursor.getString(2);
//						orderDO.CustomerSiteId 	= 	cursor.getString(3);
//						orderDO.strAddress2 	= 	cursor.getString(5);
//						orderDO.strCustomerName = 	cursor.getString(4);
//						orderDO.strAddress1 	= 	cursor.getString(6);
//						orderDO.DeliveryAgentId = 	cursor.getString(7);
//						orderDO.orderType 		= 	cursor.getString(8);
//						orderDO.orderSubType 	= 	cursor.getString(9);
//						orderDO.TRXStatus	 	= 	cursor.getString(10);
//						orderDO.pushStatus	 	= 	cursor.getInt(11);
//
//						orderDO.DeliveryDate	= 	cursor.getString(12);
//						orderDO.Batch_Source_Name= 	cursor.getString(13);
//						orderDO.Trx_Type_Name	= 	cursor.getString(14);
//						orderDO.TotalAmount		= 	cursor.getDouble(15);
//						orderDO.LPOCode			= 	cursor.getString(16);
//						orderDO.salesmanCode	= 	cursor.getString(17);
//						orderDO.LPOStatus		= 	cursor.getString(18);
//						orderDO.roundOffVal 	= 	cursor.getFloat(19);
//						orderDO.VatAmount 	= 	cursor.getFloat(20);
//
//						if(orderDO.LPOStatus == null)
//							orderDO.LPOStatus = "0";
//
//						if(TextUtils.isEmpty(orderDO.DeliveryDate))
//							orderDO.DeliveryDate= 	CalendarUtils.getOrderPostDate();
//
//						if(TextUtils.isEmpty(orderDO.strAddress1))
//							orderDO.strAddress1 = "";
//
//						if(TextUtils.isEmpty(orderDO.strAddress2))
//							orderDO.strAddress2 = "";
//
//						if(orderDO.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER))
//						{
//							if(hmOrders != null && hmOrders.containsKey(AppConstants.LPO_ORDER))
//							{
//								Vector<OrderDO> vec = hmOrders.get(AppConstants.LPO_ORDER);
//								vec.add(orderDO);
//								hmOrders.put(AppConstants.LPO_ORDER, vec);
//							}
//							else
//							{
//								Vector<OrderDO> vec = new Vector<OrderDO>();
//								vec.add(orderDO);
//								hmOrders.put(AppConstants.LPO_ORDER, vec);
//							}
//						}
//						else if(orderDO.TRXStatus.equalsIgnoreCase(AppStatus.TRX_STATUS_HOLD))
//						{
//							if(hmOrders != null && hmOrders.containsKey(AppConstants.HOLD_ORDER))
//							{
//								Vector<OrderDO> vec = hmOrders.get(AppConstants.HOLD_ORDER);
//								vec.add(orderDO);
//								hmOrders.put(AppConstants.HOLD_ORDER, vec);
//							}
//							else
//							{
//								Vector<OrderDO> vec = new Vector<OrderDO>();
//								vec.add(orderDO);
//								hmOrders.put(AppConstants.HOLD_ORDER, vec);
//							}
//						}
//						else if(hmOrders != null && hmOrders.containsKey(orderDO.orderType))
//						{
//							Vector<OrderDO> vec = hmOrders.get(orderDO.orderType);
//							vec.add(orderDO);
//							hmOrders.put(orderDO.orderType, vec);
//						}
//						else
//						{
//							Vector<OrderDO> vec = new Vector<OrderDO>();
//							vec.add(orderDO);
//							hmOrders.put(orderDO.orderType, vec);
//						}
//					}
//					while(cursor.moveToNext());
//					if(cursor!=null && !cursor.isClosed())
//						cursor.close();
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(cursor!=null && !cursor.isClosed())
//					cursor.close();
//				if(sqLiteDatabase != null)
//					sqLiteDatabase.close();
//			}
//			return hmOrders;
//		}
//	}
	
	public HashMap<String, Vector<OrderDO>> getDeliveryStatusOrderList(String FromDate,String TODate, JourneyPlanDO mallDetails)
	{
		synchronized(MyApplication.MyLock)
		{
			HashMap<String, Vector<OrderDO>> hmOrders = new HashMap<String, Vector<OrderDO>>();
			SQLiteDatabase sqLiteDatabase   = null;
			String strQuery = "";
			Cursor cursor = null;
			try
			{
				sqLiteDatabase   = DatabaseHelper.openDataBase();

				if(mallDetails == null)
					strQuery =  "SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName, CST.ADDRESS1,OT.EmpNo,OT.OrderType,OT.SubType, OT.TRXStatus, OT.Status, OT.DeliveryDate, OT.Batch_Source_Name, OT.Cust_Trx_Type_Name, OT.TotalAmount, OT.LPOCode, OT.DeliveredBy, OT.LPOStatus, OT.RoundOffValue,OT.VatAmount,OT.TotalAmountWithVat,OT.TrxReasonCode " +
								"FROM tblOrderHeader OT  LEFT JOIN tblCustomer CST  ON OT.SiteNo=CST.Site " +
								"where (Date(OT.OrderDate) BETWEEN Date('"+FromDate+"') AND Date('"+TODate+"') OR (Date(OT.DeliveryDate) BETWEEN Date('"+FromDate+"') AND Date('"+TODate+"') AND OT.SubType LIKE '%LPO%') OR OT.TRXStatus = 'H') ORDER BY OT.OrderId DESC";
				else
					strQuery =  "SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName, CST.ADDRESS1,OT.EmpNo,OT.OrderType,OT.SubType, OT.TRXStatus, OT.Status, OT.DeliveryDate, OT.Batch_Source_Name, OT.Cust_Trx_Type_Name, OT.TotalAmount, OT.LPOCode, OT.DeliveredBy, OT.LPOStatus, OT.RoundOffValue,OT.VatAmount,OT.TotalAmountWithVat,OT.TrxReasonCode " +
								"FROM tblOrderHeader OT  LEFT JOIN tblCustomer CST  ON OT.SiteNo=CST.Site " +
								"where OT.SiteNo = '"+mallDetails.site+"' AND (Date(OT.OrderDate) BETWEEN Date('"+FromDate+"') AND Date('"+TODate+"') OR (Date(OT.DeliveryDate) BETWEEN Date('"+FromDate+"') AND Date('"+TODate+"') AND OT.SubType LIKE '%LPO%') OR OT.TRXStatus = 'H') ORDER BY OT.OrderId DESC";

				cursor	=	sqLiteDatabase.rawQuery(strQuery,null);

				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 		= 	new OrderDO();
						orderDO.OrderId  		= 	cursor.getString(0);
						orderDO.DeliveryStatus 	= 	cursor.getString(1);
						orderDO.InvoiceDate 	= 	cursor.getString(2);
						orderDO.CustomerSiteId 	= 	cursor.getString(3);
						orderDO.strAddress2 	= 	cursor.getString(5);
						orderDO.strCustomerName = 	cursor.getString(4);
						orderDO.strAddress1 	= 	cursor.getString(6);
						orderDO.DeliveryAgentId = 	cursor.getString(7);
						orderDO.orderType 		= 	cursor.getString(8);
						orderDO.orderSubType 	= 	cursor.getString(9);
						orderDO.TRXStatus	 	= 	cursor.getString(10);
						orderDO.pushStatus	 	= 	cursor.getInt(11);

						orderDO.DeliveryDate	= 	cursor.getString(12);
						orderDO.Batch_Source_Name= 	cursor.getString(13);
						orderDO.Trx_Type_Name	= 	cursor.getString(14);
						orderDO.TotalAmount		= 	cursor.getDouble(15);
						orderDO.LPOCode			= 	cursor.getString(16);
						orderDO.salesmanCode	= 	cursor.getString(17);
						orderDO.LPOStatus		= 	cursor.getString(18);
						orderDO.roundOffVal 	= 	cursor.getFloat(19);

						orderDO.VatAmount 	= 	cursor.getFloat(20);
						orderDO.TotalAmountWithVat 	= 	cursor.getFloat(21);
						orderDO.TrxReasonCode 	= 	cursor.getString(22);
						if(orderDO.LPOStatus == null)
							orderDO.LPOStatus = "0";

						if(TextUtils.isEmpty(orderDO.DeliveryDate))
							orderDO.DeliveryDate= 	CalendarUtils.getOrderPostDate();

						if(TextUtils.isEmpty(orderDO.strAddress1))
							orderDO.strAddress1 = "";

						if(TextUtils.isEmpty(orderDO.strAddress2))
							orderDO.strAddress2 = "";

						if(orderDO.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER))
						{
							if(hmOrders != null && hmOrders.containsKey(AppConstants.LPO_ORDER))
							{
								Vector<OrderDO> vec = hmOrders.get(AppConstants.LPO_ORDER);
								vec.add(orderDO);
								hmOrders.put(AppConstants.LPO_ORDER, vec);
							}
							else
							{
								Vector<OrderDO> vec = new Vector<OrderDO>();
								vec.add(orderDO);
								hmOrders.put(AppConstants.LPO_ORDER, vec);
							}
						}
						else if(orderDO.TRXStatus.equalsIgnoreCase(AppStatus.TRX_STATUS_HOLD)|| orderDO.TRXStatus.equalsIgnoreCase(AppStatus.TRX_STATUS_REJECT))
						{
							if(hmOrders != null && hmOrders.containsKey(AppConstants.HOLD_ORDER))
							{
								Vector<OrderDO> vec = hmOrders.get(AppConstants.HOLD_ORDER);
								vec.add(orderDO);
								hmOrders.put(AppConstants.HOLD_ORDER, vec);
							}
							else
							{
								Vector<OrderDO> vec = new Vector<OrderDO>();
								vec.add(orderDO);
								hmOrders.put(AppConstants.HOLD_ORDER, vec);
							}
						}
						else if(hmOrders != null && hmOrders.containsKey(orderDO.orderType))
						{
							Vector<OrderDO> vec = hmOrders.get(orderDO.orderType);
							vec.add(orderDO);
							hmOrders.put(orderDO.orderType, vec);
						}
						else
						{
							Vector<OrderDO> vec = new Vector<OrderDO>();
							vec.add(orderDO);
							hmOrders.put(orderDO.orderType, vec);
						}
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
			return hmOrders;
		}
	}
	
	public ArrayList<ProductDO> getDeliveryStatusOrderProducts(String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<ProductDO> vecItemList = new ArrayList<ProductDO>();
			SQLiteDatabase sqLiteDatabase   = null;
			Cursor cursor	=	null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor		   =	sqLiteDatabase.rawQuery("SELECT  OD.ItemCode, TP.UnitPerCase, OD.Units, OD.Cases,TP.Description,OD.QuantityBU,OD.PriceUsedLevel2," +
															"OD.unitSellingPrice,OD.UOM , OD.ReasonCode FROM tblOrderDetail OD,tblProducts TP " +
															"where OrderNo ='"+orderID+"' and OD.ItemCode=TP.ItemCode", null);
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 		= 	new ProductDO();
						productDO.SKU 				= 	cursor.getString(0);
						productDO.UnitsPerCases 	= 	cursor.getInt(1);
						productDO.preUnits 			= 	cursor.getFloat(2)+"";
						productDO.preCases 			= 	cursor.getString(3);
						productDO.Description 		= 	cursor.getString(4);
						productDO.totalCases 		= 	cursor.getFloat(5);
						productDO.invoiceAmount 	=	cursor.getDouble(6);
						productDO.unitSellingPrice 	=	cursor.getFloat(7);
						productDO.depositPrice	 	=	0;
						productDO.UOM 				= 	cursor.getString(8);
						productDO.itemPrice 		= 	0;
						productDO.Discount 			= 	0;
						productDO.TaxGroupCode		= 	"";
						productDO.TaxPercentage		= 	0;
						productDO.reason			= 	cursor.getString(9);
						
						if(productDO.preUnits == null || productDO.preUnits.equalsIgnoreCase(""))
							productDO.preUnits = "0";
						
						if(productDO.preCases == null || productDO.preCases.equalsIgnoreCase(""))
							productDO.preCases = "0";
						
						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecItemList;
		}
	}
	
	//method to insert or update the banks Detail
	public boolean saveCustomer(MallsDetails mallsDetails) 
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSites WHERE CustomerSiteId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId, CustomerId , SiteName, ADDRESS1, ADDRESS2, CITY, GEO_CODE_X, GEO_CODE_Y, PresellerId, TYPE, PaymentType,email) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCustomerSites SET CustomerId=?, SiteName=?, ADDRESS1=?, ADDRESS2=?, CITY=?, GEO_CODE_X=?, GEO_CODE_Y=?, PresellerId=?, TYPE=?, PaymentType=?,email=? WHERE CustomerSiteId = ?");
			if(mallsDetails != null)
			{
				stmtSelectRec.bindString(1, mallsDetails.customerSiteId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if (countRec != 0) 
				{
					stmtUpdate.bindString(1, mallsDetails.CustomerId);
					stmtUpdate.bindString(2, mallsDetails.SiteName);
					stmtUpdate.bindString(3, mallsDetails.Address1);
					stmtUpdate.bindString(4, mallsDetails.Address2);
					stmtUpdate.bindString(5, mallsDetails.City);
					stmtUpdate.bindString(6, ""+mallsDetails.Latitude);
					stmtUpdate.bindString(7, ""+mallsDetails.Longitude);
					stmtUpdate.bindString(8, mallsDetails.presellerId);
					stmtUpdate.bindString(9, mallsDetails.paymentType);
					stmtUpdate.bindString(10, mallsDetails.paymentCode);
					stmtUpdate.bindString(11, mallsDetails.email);
					stmtUpdate.bindString(12, mallsDetails.customerSiteId);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1,mallsDetails.customerSiteId);
					stmtInsert.bindString(2, mallsDetails.CustomerId);
					stmtInsert.bindString(3, mallsDetails.SiteName);
					stmtInsert.bindString(4,mallsDetails.Address1);
					stmtInsert.bindString(5, mallsDetails.Address2);
					stmtInsert.bindString(6, mallsDetails.City);
					stmtInsert.bindString(7, ""+mallsDetails.Latitude);
					stmtInsert.bindString(8, ""+mallsDetails.Longitude);
					stmtInsert.bindString(9, mallsDetails.presellerId);
					stmtInsert.bindString(10,mallsDetails.paymentType);
					stmtInsert.bindString(11, mallsDetails.paymentCode);
					stmtInsert.bindString(12, mallsDetails.email);
					stmtInsert.executeInsert();
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
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
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public Vector<NewCustomerDO> getNewCustomerToUpload_()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase = null;
			Vector<NewCustomerDO> vector = new Vector<NewCustomerDO>();
			Cursor cursor  = null;
			
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor    = mDatabase.rawQuery("select * from tblCustomerSites where Source == 0 AND BlaseCustId !=''", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						NewCustomerDO newCustomerDO = new NewCustomerDO();
						
						newCustomerDO.customerName = cursor.getString(cursor.getColumnIndex("SiteName")); 
						newCustomerDO.siteName = cursor.getString(cursor.getColumnIndex("SiteName")); 
						newCustomerDO.contactPerson = ""; 
						newCustomerDO.mobileNo = cursor.getString(cursor.getColumnIndex("MOBILENO1")); 
						newCustomerDO.region = cursor.getString(cursor.getColumnIndex("LandmarkId")); 
						newCustomerDO.landline = ""; 
						newCustomerDO.area = cursor.getString(cursor.getColumnIndex("LandmarkId")); 
						newCustomerDO.salesman = cursor.getString(cursor.getColumnIndex("PresellerId")); 
						newCustomerDO.customerType = cursor.getString(cursor.getColumnIndex("CustomerType")); 
						
						newCustomerDO.address1 = cursor.getString(cursor.getColumnIndex("ADDRESS1")); 
						newCustomerDO.address2 = cursor.getString(cursor.getColumnIndex("ADDRESS2")); 
						
						newCustomerDO.source = ""; 
						newCustomerDO.billTo = ""; 
						newCustomerDO.shipTo = ""; 
						newCustomerDO.countryName = cursor.getString(cursor.getColumnIndex("CountryId")); 
						newCustomerDO.email = ""; 
						newCustomerDO.nationality = cursor.getString(cursor.getColumnIndex("CountryId")); 
						newCustomerDO.longitude = cursor.getFloat(cursor.getColumnIndex("GEO_CODE_Y")); 
						newCustomerDO.latitude = cursor.getFloat(cursor.getColumnIndex("GEO_CODE_X")); 
						newCustomerDO.AppUUID = cursor.getString(cursor.getColumnIndex("BlaseCustId")); 
						newCustomerDO.CustomerSiteId = cursor.getString(cursor.getColumnIndex("CustomerSiteId"));
						newCustomerDO.CITY = cursor.getString(cursor.getColumnIndex("CITY"));
						newCustomerDO.countryId = cursor.getString(cursor.getColumnIndex("AnniversaryDate"));
						getCustomerSubData(mDatabase,newCustomerDO);
						vector.add(newCustomerDO);
					} 
					while (cursor.moveToNext());
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
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return vector;
		}
	}
	
	public Vector<UnUploadedDataDO> getNewCustomerUnUpload()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase = null;
			Vector<UnUploadedDataDO> uploadedDataDOs = new Vector<UnUploadedDataDO>();
			Cursor cursor  = null;
			
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select CustomerId, ISPushed from tblNewHouseHoldCustomer", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						UnUploadedDataDO unUploadedDataDO = new UnUploadedDataDO();
						
						unUploadedDataDO.strId  = cursor.getString(0); 
						unUploadedDataDO.status = StringUtils.getInt(cursor.getString(1)); 
						uploadedDataDOs.add(unUploadedDataDO);
					} 
					while (cursor.moveToNext());
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
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return uploadedDataDOs;
		}
	}
	
	public String insertCustomerSite(MallsDetails mallsDetails, NameIDDo nameIDDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			String strCustomerId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
				/*String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
				Cursor cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strCustomerId = cursor.getString(0);
					mallsDetails.customerSiteId = strCustomerId;
					mallsDetails.CustomerId = strCustomerId;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");*/
				strCustomerId= offlinDA.getNextSequenceNumber(OfflineDataType.CUSTOMER,objSqliteDB);

				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
						"CountryId,DOB,AnniversaryDate,CustomerCategory,CustomerSubCategory) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsert2 = objSqliteDB.compileStatement("INSERT INTO tblCustomerSitesSub (CustomerSiteId," +
						" ContactPerson, Landline,OutLetType,OutLetTypeId,CompetitionBrand,CompetitionBrandId,SKU,Email,BuyerStatus) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(mallsDetails != null && mallsDetails.customerSiteId != null && !mallsDetails.customerSiteId.equalsIgnoreCase(""))
				{
					stmtInsert.bindString(1, mallsDetails.customerSiteId);
					stmtInsert.bindString(2, mallsDetails.CustomerId);
					stmtInsert.bindString(3, mallsDetails.SiteName);
					stmtInsert.bindString(4, mallsDetails.Address1);
					stmtInsert.bindString(5, mallsDetails.Address2);
					stmtInsert.bindString(6, mallsDetails.City);
					stmtInsert.bindString(7, ""+mallsDetails.Latitude);
					stmtInsert.bindString(8, ""+mallsDetails.Longitude);
					stmtInsert.bindString(9, ""+mallsDetails.CreditLimit);
					stmtInsert.bindString(10, mallsDetails.presellerId);
					stmtInsert.bindString(11, mallsDetails.paymentType);
					stmtInsert.bindString(12, mallsDetails.paymentCode);
					stmtInsert.bindString(13, "");
					stmtInsert.bindString(14, "0");
					stmtInsert.bindString(15, mallsDetails.subChannelCode);
					stmtInsert.bindString(16, mallsDetails.customerStatus);
					stmtInsert.bindString(17, mallsDetails.mobileNo);
					stmtInsert.bindString(18, mallsDetails.mobileNo);
					stmtInsert.bindString(19, "");
					stmtInsert.bindString(20, "");
					stmtInsert.bindString(21, mallsDetails.CustomerType);
					stmtInsert.bindString(22, mallsDetails.landId);
					stmtInsert.bindString(23, mallsDetails.landId);
					stmtInsert.bindString(24, "0");
					stmtInsert.bindString(25, mallsDetails.AppUUID);
					stmtInsert.bindString(26, mallsDetails.countryDesc);
					stmtInsert.bindString(27, "");
					stmtInsert.bindString(28, ""+nameIDDo.strId);
					stmtInsert.bindString(29, "ROUTE");
					stmtInsert.bindString(30, "OTHERS");
					stmtInsert.executeInsert();
					
					stmtInsert2.bindString(1, mallsDetails.customerSiteId);
					stmtInsert2.bindString(2, mallsDetails.contactPerson);
					stmtInsert2.bindString(3, mallsDetails.landline);
					stmtInsert2.bindString(4, mallsDetails.outLetType);
					stmtInsert2.bindString(5, mallsDetails.outLetTypeId);
					stmtInsert2.bindString(6, mallsDetails.competitionBrand);
					stmtInsert2.bindString(7, ""+mallsDetails.competitionBrandId);
					stmtInsert2.bindString(8, ""+mallsDetails.sku);
					stmtInsert2.bindString(9, ""+mallsDetails.email);
					stmtInsert2.bindString(10, mallsDetails.buyerStatus);
					stmtInsert2.executeInsert();
					
					
				}
				offlinDA.updateSequenceNumberStatus(strCustomerId,objSqliteDB);
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return strCustomerId;
		}
	}
	
	public String insertCustomerSiteInfo(JourneyPlanDO mallsDetails, NameIDDo nameIDDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			String strCustomerId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
			/*	String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
				Cursor cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strCustomerId = cursor.getString(0);
					mallsDetails.customerId = strCustomerId;
					mallsDetails.customerId = strCustomerId;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");*/
				
				strCustomerId= offlinDA.getNextSequenceNumber(OfflineDataType.CUSTOMER,objSqliteDB);
				
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
						"CountryId,DOB,AnniversaryDate,CustomerCategory,CustomerSubCategory) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsert2 = objSqliteDB.compileStatement("INSERT INTO tblCustomerSitesSub (CustomerSiteId," +
						" ContactPerson, Landline,OutLetType,OutLetTypeId,CompetitionBrand,CompetitionBrandId,SKU,Email,BuyerStatus) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(mallsDetails != null && mallsDetails.customerId != null && !mallsDetails.customerId.equalsIgnoreCase(""))
				{
					stmtInsert.bindString(1, ""+mallsDetails.customerId);
					stmtInsert.bindString(2, ""+mallsDetails.customerId);
					stmtInsert.bindString(3, ""+mallsDetails.siteName);
					stmtInsert.bindString(4, ""+mallsDetails.addresss1);
					stmtInsert.bindString(5, ""+mallsDetails.addresss2);
					stmtInsert.bindString(6, ""+mallsDetails.city);
					stmtInsert.bindString(7, ""+mallsDetails.geoCodeX);
					stmtInsert.bindString(8, ""+mallsDetails.geoCodeY);
					stmtInsert.bindString(9, ""+mallsDetails.creditLimit);
					stmtInsert.bindString(10, ""+mallsDetails.userID);
					stmtInsert.bindString(11, ""+mallsDetails.paymentType);
					stmtInsert.bindString(12, ""+mallsDetails.paymentTermCode);
					stmtInsert.bindString(13, "");
					stmtInsert.bindString(14, "0");
					stmtInsert.bindString(15, ""+mallsDetails.subChannelCode);
					stmtInsert.bindString(16, ""+mallsDetails.custmerStatus);
					stmtInsert.bindString(17, ""+mallsDetails.mobileNo1);
					stmtInsert.bindString(18, ""+mallsDetails.mobileNo2);
					stmtInsert.bindString(19, "");
					stmtInsert.bindString(20, "");
					stmtInsert.bindString(21, ""+mallsDetails.customerType);
					stmtInsert.bindString(22, "");
					stmtInsert.bindString(23, "");
					stmtInsert.bindString(24, "0");
					stmtInsert.bindString(25, ""+mallsDetails.AppUUID);
					stmtInsert.bindString(26, ""+mallsDetails.coutryCode);
					stmtInsert.bindString(27, "");
					stmtInsert.bindString(28, ""+nameIDDo.strId);
					stmtInsert.bindString(29, "ROUTE");
					stmtInsert.bindString(30, "OTHERS");
					stmtInsert.executeInsert();
					
					stmtInsert2.bindString(1, ""+mallsDetails.customerId);
					stmtInsert2.bindString(2, ""+mallsDetails.contectPersonName);
					stmtInsert2.bindString(3, ""+mallsDetails.mobileNo2);
					stmtInsert2.bindString(4, ""+mallsDetails.outLetType);
					stmtInsert2.bindString(5, ""+mallsDetails.outLetTypeId);
					stmtInsert2.bindString(6, ""+mallsDetails.competitionBrand);
					stmtInsert2.bindString(7, ""+mallsDetails.competitionBrandId);
					stmtInsert2.bindString(8, ""+mallsDetails.sku);
					stmtInsert2.bindString(9, ""+mallsDetails.email);
					stmtInsert2.bindString(10,""+mallsDetails.buyerStatus);
					stmtInsert2.executeInsert();
					
					
				}
				offlinDA.updateSequenceNumberStatus(strCustomerId,objSqliteDB);
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return strCustomerId;
		}
	}
	
	public void deleteAllRecords()
	{
		synchronized(MyApplication.MyLock) 
		{
			LogUtils.errorLog("CustomerOrderDA", "Deleting all tables data");
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM BarcodeInfo");
				sqlDB.execSQL("DELETE FROM tblCustomerGroup");
				sqlDB.execSQL("DELETE FROM tblCustomerSites");
				sqlDB.execSQL("DELETE FROM tblCustomers");
				sqlDB.execSQL("DELETE FROM tblHouseHoldCustomers");
				sqlDB.execSQL("DELETE FROM tblNewHouseHoldCustomer");
				sqlDB.execSQL("DELETE FROM tblOrderDetail");
				sqlDB.execSQL("DELETE FROM tblOrderHeader");
				sqlDB.execSQL("DELETE FROM tblPaymentDetail");
				sqlDB.execSQL("DELETE FROM tblPaymentHeader");
				sqlDB.execSQL("DELETE FROM tblPendingInvoices");
				sqlDB.execSQL("DELETE FROM tblLandMark");
				sqlDB.execSQL("DELETE FROM tblCustomerHistory");
				sqlDB.execSQL("DELETE FROM tblOfflineData");
				sqlDB.execSQL("DELETE FROM tblPricing");
				sqlDB.execSQL("DELETE FROM tblVMSalesmanInventory");
				sqlDB.execSQL("DELETE FROM tblVehicle");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqlDB!=null)
					sqlDB.close();
			}
		}
	}

	public void deleteInventory()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				sqLiteDatabase.execSQL("DELETE FROM tblVMSalesmanInventory");
				sqLiteDatabase.execSQL("DELETE FROM tblVehicle");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public void deleteOfflineData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				sqLiteDatabase.execSQL("DELETE FROM tblOfflineData");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public NameIDDo validatePassCode(String userCode, String passCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery   =  null; 
			strQuery 		  =  "SELECT PassCode,IsUsed from tblPassCode where Passcode ='"+passCode+"' and EmpId='"+userCode+"'";
			NameIDDo nameIDDo =  null;
			SQLiteDatabase sqLiteDatabase   = 	null;
			Cursor cursor					=	null;
			try
			{
				sqLiteDatabase   = 	DatabaseHelper.openDataBase();
				cursor			 =	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					nameIDDo 		 = new NameIDDo();
					nameIDDo.strName = cursor.getString(0);
					nameIDDo.strId   = cursor.getString(1);
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return nameIDDo;
		}
	}
	
	public boolean deletePasscode(String passcode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				String strQuery = "DELETE FROM tblPassCode where PassCode = ?";
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPresellerPassCode
				SQLiteStatement stmtDeletePasscode = objSqliteDB.compileStatement(strQuery);
				
				stmtDeletePasscode.bindString(1, passcode);
				stmtDeletePasscode.execute();
				
				stmtDeletePasscode.close();
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

	public ArrayList<NameIDDo> getUnpostedCustomerId()
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery  	= 	"SELECT CT.CustomerId, OT.SiteNo FROM tblOrderHeader OT, tblNewHouseHoldCustomer CT  WHERE OT.SiteNo = CT.AppUUID";
			ArrayList<NameIDDo> arrayList 	= new ArrayList<NameIDDo>();
			SQLiteDatabase sqLiteDatabase   = 	null;
			Cursor cursor = null, cursor2	=	null;
			try
			{
				sqLiteDatabase   		= 	DatabaseHelper.openDataBase();
				cursor					=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo 	= 	new NameIDDo();
						nameIDDo.strId		=	cursor.getString(0);
						nameIDDo.strName	=	cursor.getString(1);
						nameIDDo.strType	=	"order";
						arrayList.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				strQuery  				= 	"SELECT CT.CustomerId, PN.SiteId FROM tblPaymentHeader PN, tblNewHouseHoldCustomer CT  WHERE PN.SiteId = CT.AppUUID";
				cursor2					=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor2.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo 	= 	new NameIDDo();
						nameIDDo.strId		=	cursor2.getString(0);
						nameIDDo.strName	=	cursor2.getString(1);
						nameIDDo.strType	=	"payment";
						arrayList.add(nameIDDo);
					}
					while(cursor2.moveToNext());
				}
				
				if(cursor2!=null && !cursor2.isClosed())
					cursor2.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(cursor2!=null && !cursor2.isClosed())
					cursor2.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return arrayList;
		}
	}
	
	public boolean updateCreatedCustomers(ArrayList<NameIDDo> arrList)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB 				= 	DatabaseHelper.openDataBase();
				SQLiteStatement order 		= 	objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments 	= 	objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				for(NameIDDo nameIDDo :arrList)
				{
					if(nameIDDo.strType.equalsIgnoreCase("order"))
					{
						order.bindString(1, nameIDDo.strId);
						order.bindString(2, nameIDDo.strName);
						order.execute();
					}
					else
					{
						payments.bindString(1, nameIDDo.strId);
						payments.bindString(2, nameIDDo.strName);
						payments.execute();
					}
				}
				
				order.close();
				payments.close();
				
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
			}
			
			return true;
		}
	}

	
	/*public String getReceiptNo(String SalesmanCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String strReceiptNo = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "SELECT id from tblOfflineData where SalesmanCode ='"+SalesmanCode+"' AND Type ='"+AppConstants.Receipt+"' AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strReceiptNo = cursor.getString(0);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return strReceiptNo;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return strReceiptNo;
		}
	}*/

/*	public boolean updateReceiptNo(String paymentId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isUpdated = false;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+paymentId+"'");
				isUpdated = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				isUpdated = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return isUpdated;
		}
	}*/
	
	/*public boolean getAvailablityOfCustomerNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Customer' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}*/
	
/*	public boolean getAvailablityOfOrderNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Order' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}*/
	
	/*public boolean getAvailablityOfReceiptNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Receipt' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}*/

	/**
	 * Method to save all the Reasons
	 * @return boolean
	 */
	public boolean insertAllReasons(Vector<PostReasonDO> vecPostReasons)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSkipReasons WHERE CustomerSiteId =? and SkipDate like ? ");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSkipReasons (PresellerId, SkipDate, Reason, ReasonId, ReasonType, CustomerSiteId) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set PresellerId=?, SkipDate=?,Reason=?, ReasonId=?,ReasonType=? WHERE CustomerSiteId =?");
				
				for(PostReasonDO objPostReasonDO :  vecPostReasons)
				{
					stmtSelectRec.bindString(1, objPostReasonDO.customerSiteID);
					stmtSelectRec.bindString(2, CalendarUtils.getCurrentDateAsString()+"%");
					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
//						stmtUpdate.bindString(1, objPostReasonDO.presellerId);
//						stmtUpdate.bindString(2, objPostReasonDO.skippingDate);
//						stmtUpdate.bindString(3, objPostReasonDO.reason);
//						stmtUpdate.bindString(4, objPostReasonDO.reasonId);
//						stmtUpdate.bindString(5, objPostReasonDO.reasonType);
//						stmtUpdate.bindString(6, objPostReasonDO.customerSiteID);
//						stmtUpdate.execute();
//					}
//					else
					{
						stmtInsert.bindString(1, objPostReasonDO.presellerId);
						stmtInsert.bindString(2, objPostReasonDO.skippingDate);
						stmtInsert.bindString(3, objPostReasonDO.reason);
						stmtInsert.bindString(4, objPostReasonDO.reasonId);
						stmtInsert.bindString(5, objPostReasonDO.reasonType);
						stmtInsert.bindString(6, objPostReasonDO.customerSiteID);
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
				return false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	private void getCustomerSubData(SQLiteDatabase mDatabase, NewCustomerDO newCustomerDO)
	{
		Cursor cursor  = null;
		
		try
		{
			cursor  = mDatabase.rawQuery("select * from tblCustomerSitesSub where CustomerSiteId == '"+newCustomerDO.CustomerSiteId+"'", null);
			if(cursor.moveToFirst())
			{
				do 
				{
					newCustomerDO.contactPerson = cursor.getString(cursor.getColumnIndex("ContactPerson")); 
					newCustomerDO.landline = cursor.getString(cursor.getColumnIndex("Landline")); 
					newCustomerDO.outLetType = cursor.getString(cursor.getColumnIndex("OutLetType")); 
					newCustomerDO.outLetTypeId = cursor.getString(cursor.getColumnIndex("OutLetTypeId")); 
					newCustomerDO.competitionBrand = cursor.getString(cursor.getColumnIndex("CompetitionBrand")); 
					newCustomerDO.competitionBrandId = cursor.getString(cursor.getColumnIndex("CompetitionBrandId")); 
					newCustomerDO.sku = cursor.getString(cursor.getColumnIndex("SKU")); 
					newCustomerDO.email = cursor.getString(cursor.getColumnIndex("Email")); 
					newCustomerDO.buyerStatus = cursor.getString(cursor.getColumnIndex("BuyerStatus")); 
				} 
				while (cursor.moveToNext());
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
		}
	}
	
	public boolean insertCheckInDemandInventory(String empNo, String date, ArrayList<DeliveryAgentOrderDetailDco> arrayList, int pusStatus)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory (Date, EmpNo, ItemCode, PrimaryQuantity, SecondaryQuantity, pushStatus, AdvcCases, AdvcUnits) VALUES(?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtSelect 		= objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblCheckINDemandStockInventory WHERE Date LIKE'%"+date+"%' AND EmpNo = ? AND ItemCode=?");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCheckINDemandStockInventory SET PrimaryQuantity=?, SecondaryQuantity=?, pushStatus = 0 WHERE Date LIKE'%"+date+"%' AND EmpNo = ? AND ItemCode=?");
			
			if(arrayList != null && arrayList.size() > 0)
			{
				for(DeliveryAgentOrderDetailDco orderDetailDco :  arrayList)
				{
					stmtSelect.bindString(1, empNo);
					stmtSelect.bindString(2, orderDetailDco.itemCode);
					
					long count = stmtSelect.simpleQueryForLong();
					
					if(count <= 0)
					{
						
						stmtInsert.bindString(1, date);
						stmtInsert.bindString(2, empNo);
						stmtInsert.bindString(3, orderDetailDco.itemCode);
						stmtInsert.bindString(4, orderDetailDco.preCases+"");
						stmtInsert.bindString(5, orderDetailDco.preUnits+"");
						stmtInsert.bindString(6, pusStatus+"");
						stmtInsert.bindString(7, 0+"");
						stmtInsert.bindString(8, 0+"");
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, orderDetailDco.preCases+"");
						stmtUpdate.bindString(2, orderDetailDco.preUnits+"");
						stmtUpdate.bindString(3, empNo);
						stmtUpdate.bindString(4, orderDetailDco.itemCode);
						stmtUpdate.execute();
					}
				}
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
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public boolean insertCheckInDemandInventoryNew(SQLiteDatabase objSqliteDB, String empNo, String date, ArrayList<DeliveryAgentOrderDetailDco> arrayList, int pusStatus)
	{
		try 
		{
			if(objSqliteDB == null)
				objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory (Date, EmpNo, ItemCode, PrimaryQuantity, SecondaryQuantity, pushStatus) VALUES(?,?,?,?,?,?)");
			
			if(arrayList != null && arrayList.size() > 0)
			{
				for(DeliveryAgentOrderDetailDco orderDetailDco :  arrayList)
				{
					stmtInsert.bindString(1, date);
					stmtInsert.bindString(2, empNo);
					stmtInsert.bindString(3, orderDetailDco.itemCode);
					stmtInsert.bindString(4, orderDetailDco.preCases+"");
					stmtInsert.bindString(5, orderDetailDco.preUnits+"");
					stmtInsert.bindString(6, pusStatus+"");
					stmtInsert.executeInsert();
				}
			}
			stmtInsert.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private ArrayList<DeliveryAgentOrderDetailDco> getAddvanceOrderProductDetail(SQLiteDatabase objSqliteDB, String orderId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = null;
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				cursor = objSqliteDB.rawQuery("SELECT ItemCode, Cases, Units from tblOrderDetail where OrderNO ='"+orderId+"'", null);
				if(cursor.moveToNext())
				{
					vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
					do 
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						orderDetailDco.itemCode = cursor.getString(0);
						orderDetailDco.preCases = StringUtils.getFloat(cursor.getString(1));
						orderDetailDco.preUnits = StringUtils.getInt(cursor.getString(2));
						
						vecOrdProduct.add(orderDetailDco);
					} while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return vecOrdProduct;
		}
	}
	
	public boolean UpdateDemandInventory(Vector<CheckInDemandInventoryDO> vector, int pusStatus)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCheckINDemandStockInventory SET pushStatus =? WHERE ItemCode =?");
			
			if(vector != null && vector.size() > 0)
			{
				for(CheckInDemandInventoryDO checkInDemandInventoryDO :  vector)
				{
					stmtUpdate.bindString(1, pusStatus+"");
					stmtUpdate.bindString(2, checkInDemandInventoryDO.ItemCode);
					stmtUpdate.executeInsert();
				}
			}
			stmtUpdate.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}
//	public Vector<CheckInDemandInventoryDO> getCheckINDemandInventory()
//	{
//		SQLiteDatabase mDatabase = null;
//		Vector<CheckInDemandInventoryDO> vector = new Vector<CheckInDemandInventoryDO>();
//		Cursor cursor  = null;
//		
//		try
//		{
//			mDatabase = DatabaseHelper.openDataBase();
//			cursor  = mDatabase.rawQuery("select * from tblCheckINDemandStockInventory where pushStatus == '0'", null);
//			if(cursor.moveToFirst())
//			{
//				do 
//				{
//					CheckInDemandInventoryDO checkInDemandInventoryDO = new CheckInDemandInventoryDO();
//					checkInDemandInventoryDO.Date				= cursor.getString(0);
//					checkInDemandInventoryDO.EmpNo				= cursor.getString(1);
//					checkInDemandInventoryDO.ItemCode			= cursor.getString(2);
//					checkInDemandInventoryDO.PrimaryQuantity	= cursor.getString(3);
//					checkInDemandInventoryDO.SecondaryQuantity  = cursor.getString(4);
//					checkInDemandInventoryDO.pushStatus			= cursor.getString(5);
//					
//					vector.add(checkInDemandInventoryDO);
//				} 
//				while (cursor.moveToNext());
//			}
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			if(cursor != null && !cursor.isClosed())
//				cursor.close();
//			
//			if(mDatabase != null)
//				mDatabase.close();
//		}
//		
//		return vector;
//	}

	public ArrayList<DeliveryAgentOrderDetailDco> getCheckInDemandInventory(String empNo, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase objSqliteDB = null;
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				cursor = objSqliteDB.rawQuery("SELECT TCK .ItemCode, TCK .PrimaryQuantity, TCK .SecondaryQuantity, TCK .AdvcCases, TCK .AdvcUnits, TP.Description, TP.ItemType FROM tblCheckINDemandStockInventory TCK INNER JOIN tblProducts TP ON TP.SKU = TCK.ItemCode WHERE TCK .Date LIKE '%"+date+"' AND TCK .EmpNo = '"+empNo+"'", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						orderDetailDco.itemCode = cursor.getString(0);
						orderDetailDco.preCases = cursor.getFloat(1);
						orderDetailDco.preUnits = cursor.getInt(2);
						
						orderDetailDco.checkInCases = cursor.getFloat(1);
						orderDetailDco.checkInPcs = cursor.getInt(2);
						
//						orderDetailDco.advnCases = cursor.getFloat(5);
//						orderDetailDco.advnPcs = cursor.getInt(6);
						
						orderDetailDco.advnCases = cursor.getFloat(3);
						orderDetailDco.advnPcs = cursor.getInt(4);
						
						orderDetailDco.itemDescription = cursor.getString(5);
						orderDetailDco.itemType = cursor.getString(6);
						vecOrdProduct.add(orderDetailDco);
					} while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return vecOrdProduct;
		}
	}
	
	public ArrayList<PostReasonDO> getSkipReasonsToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<PostReasonDO> vecReasons = new ArrayList<PostReasonDO>();
			String strQuery = "Select * FROM tblSkipReasons where  IFNULL(Status, '') = ''";
				
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 cursor 				= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 PostReasonDO objReasons 	=  new PostReasonDO();
						 objReasons.presellerId 	=  cursor.getString(0);
						 objReasons.skippingDate	=  cursor.getString(1);
						 objReasons.reason 			=  cursor.getString(2);
						 objReasons.reasonId		=  cursor.getString(3);
						 objReasons.reasonType		=  cursor.getString(4);
						 objReasons.customerSiteID	=  cursor.getString(5);
						 vecReasons.add(objReasons);
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
			 return vecReasons;
		}
	}

	public void deleteAllSkp(ArrayList<PostReasonDO> vecArrayList) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("DELETE FROM tblSkipReasons WHERE CustomerSiteId =? AND PresellerId =?");
				 
				 for(PostReasonDO postReasonDO : vecArrayList)
				 {
					 stmtUpdate.bindString(1, postReasonDO.customerSiteID);
					 stmtUpdate.bindString(2, postReasonDO.presellerId);
					 stmtUpdate.execute();
				 }
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
	public void updateSkipReason(String siteIds, String date) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set Status = '1' WHERE CustomerSiteId in (?) AND SkipDate like ?");
				 
				 stmtUpdate.bindString(1, siteIds);
				 stmtUpdate.bindString(2, date+"%");
				 stmtUpdate.execute();
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
	
	public void updateSkipReasonNew(ArrayList<PostReasonDO> vecArrayList, String date) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set Status = '1' WHERE CustomerSiteId = ? AND SkipDate like ?");
				 
				 for (PostReasonDO postReasonDO : vecArrayList)
				 {
					 stmtUpdate.bindString(1, postReasonDO.customerSiteID);
					 stmtUpdate.bindString(2, date+"%");
					 stmtUpdate.execute();
				 }
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
	

	public String getOrderStatus(String orderId)
	{
		synchronized(MyApplication.MyLock) 
		{
			 String query = "SELECT ReceiptId FROM tblPaymentInvoice WHERE TrxCode = '"+orderId+"'";
			 String strReceipts = "";
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 cursor 				= objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					strReceipts  = cursor.getString(0);
				 }
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
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
			 return strReceipts;
		}
	}
	
	public String getReceiptStatus(String receiptNo) 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			String status = "N";
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				
				cursor  = mDatabase.rawQuery("SELECT Status FROM tblPaymentHeader WHERE ReceiptId='"+receiptNo+"'", null);
				if(cursor.moveToFirst())
				{
					status = cursor.getString(0);
				} 
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return status;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return status;
		}
	}
	
	public boolean updatePendingInvoices(String receiptNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			Cursor cursor = null, innerCursor = null;
			try
			{
				HashMap<String, Float> hashMap = new HashMap<String, Float>();
				String select 				=   "SELECT TrxCode,Amount FROM tblPaymentInvoice WHERE ReceiptId='"+receiptNo+"'";
				objSqliteDB 				= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 	= 	objSqliteDB.compileStatement("UPDATE tblPendingInvoices SET BalanceAmount = ?, Status = 'N' WHERE InvoiceNumber =?");
				cursor = objSqliteDB.rawQuery(select, null);
				if(cursor.moveToFirst())
				{
					float amount = 0;
					do
					{
						String selectAmount =   "SELECT BalanceAmount FROM tblPendingInvoices WHERE InvoiceNumber= '"+cursor.getString(0)+"'";
						innerCursor = objSqliteDB.rawQuery(selectAmount, null);
						
						if(innerCursor.moveToFirst())
							amount = innerCursor.getFloat(0);
						
						if(innerCursor != null && !innerCursor.isClosed())
							innerCursor.close();
						
						amount = amount+cursor.getFloat(1);
						
						if(hashMap.size() > 0)
						{
							if(hashMap.containsKey(cursor.getString(0)))
							{
								float preAmount = hashMap.get(cursor.getString(0));
								amount = amount+preAmount;
								hashMap.put(cursor.getString(0), amount);
							}
							else
								hashMap.put(cursor.getString(0), amount);
						}
						else
							hashMap.put(cursor.getString(0), amount);
					}
					while(cursor.moveToNext());
				}
				
				if(hashMap != null && hashMap.size() > 0)
				{
					Set<String> keys = hashMap.keySet();
					for(String key : keys)	
					{
						stmtUpdate.bindString(1, ""+hashMap.get(key));
						stmtUpdate.bindString(2, key);
						stmtUpdate.execute();
					}
				}
				stmtUpdate.close();
				if(cursor != null && !cursor.isClosed())
					cursor.close();
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
				
				if(innerCursor != null && !innerCursor.isClosed())
					innerCursor.close();
				
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	public boolean deleteReceipt(String receiptNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtReceipts 		= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentHeader WHERE ReceiptId = ?");
				SQLiteStatement stmtReceiptDetail 	= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentDetail WHERE ReceiptNo = ?");
				SQLiteStatement stmtReceiptInvoice 	= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentInvoice WHERE ReceiptId = ?");
				
				if(receiptNo != null && !receiptNo.equalsIgnoreCase(""))
				{
					stmtReceipts.bindString(1, receiptNo);
					stmtReceipts.execute();
					
					stmtReceiptDetail.bindString(1, receiptNo);
					stmtReceiptDetail.execute();
					
					stmtReceiptInvoice.bindString(1, receiptNo);
					stmtReceiptInvoice.execute();
				}
				
				stmtReceipts.close();
				stmtReceiptDetail.close();
				
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
			}
			
			return true;
		}
	}

	public void updatePasscodeStatus(String passcode) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB 			   = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblPassCode SET IsUsed = 1 WHERE PassCode =?");
				
				if(passcode != null && !passcode.equalsIgnoreCase(""))
				{
					stmtUpdate.bindString(1, passcode);
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

	public String getUsedPasscode() 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			String passcode = null;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("SELECT PassCode from tblPassCode where IsUsed ='1' LIMIT 1", null);
				
				if(cursor.moveToFirst())
					passcode	 = 	cursor.getString(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			return passcode;
		}
	}
	
	public Vector<InventoryDO> getAllInventory(String strEmpNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null,cursorDetail=null;
			Vector<InventoryDO> vecInventoryDOs= new Vector<InventoryDO>();
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery("SELECT InventoryId,SiteNo,CreatedBy,CreatedOn from tblInventory where uploadStatus='0'", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						InventoryDO inventoryDO 	= 	new InventoryDO();
						inventoryDO.inventoryId		=	cursor.getString(0);
						inventoryDO.site			=	cursor.getString(1);
						inventoryDO.createdBy		=	cursor.getString(2);
						inventoryDO.date			=	cursor.getString(3);
						inventoryDO.vecInventoryDOs = 	new Vector<InventoryDetailDO>();
						
						cursorDetail = sqLiteDatabase.rawQuery("SELECT ItemCode,InventoryQuantity,RecomendedQuantity from tblInventoryDetails where InventoryId='"+inventoryDO.inventoryId+"'"	, null);
						if(cursorDetail.moveToFirst())
						{
							do 
							{
								InventoryDetailDO inventoryDetailDO  = new InventoryDetailDO();
								inventoryDetailDO.itemCode			 =	cursorDetail.getString(0);
								inventoryDetailDO.inventoryQty		 =	cursorDetail.getFloat(1);
								inventoryDetailDO.recmQty			 =	cursorDetail.getFloat(2);
								inventoryDO.vecInventoryDOs.add(inventoryDetailDO);
							} while (cursorDetail.moveToNext());
						}
						
						if(cursorDetail!=null && !cursorDetail.isClosed())
							cursorDetail.close();
						if(inventoryDO.vecInventoryDOs != null && inventoryDO.vecInventoryDOs.size() > 0)
							vecInventoryDOs.add(inventoryDO);
					} while (cursor.moveToNext());
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
				
				if(cursorDetail!=null && !cursorDetail.isClosed())
					cursorDetail.close();
				DatabaseHelper.closedatabase();
			}
	
			return vecInventoryDOs;
		}
	}
	
	public boolean updateInventory(Vector<AllUsersDo> vecOrderlist)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteStatement stmtUpdateInventory=null;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				stmtUpdateInventory = objSqliteDB.compileStatement("UPDATE tblInventory SET uploadStatus = ? where InventoryId=?");
				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					stmtUpdateInventory.bindLong(1, 1);
					stmtUpdateInventory.bindString(2, objAllUsersDo.strOldOrderNumber);
					stmtUpdateInventory.execute();
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
				if(stmtUpdateInventory!=null)
					stmtUpdateInventory.close();
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public int getPasscodeAvailibility() 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			int count = 0;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("SELECT COUNT(*) FROM tblPassCode WHERE IsUsed = 0", null);
				
				if(cursor.moveToFirst())
					count  =  cursor.getInt(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			return count;
		}
	}
	
	public Vector<AssetDo_New> getAllAssetsToPost(String usercode)
	{
		synchronized (MyApplication.MyLock) 
		{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		AssetDo_New assetDO = null;
		Vector<AssetDo_New> vecAssetDos = new Vector<AssetDo_New>();
		
		try {
			mDatabase = DatabaseHelper.openDataBase();
			
			
			String query = "SELECT AssetId,AssetName,Level1,Level2,Level3,Level4,Level5,Status,Site,UserCode FROM tblAssetCategory WHERE Status= '"+0+"' AND UserCode= '"+usercode+"'";
			cursor = mDatabase.rawQuery(query, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					assetDO = new AssetDo_New();
					assetDO.assetId = cursor.getString(0);
					assetDO.assetName = cursor.getString(1);
					assetDO.assetCatLevel1 = cursor.getString(2);
					assetDO.assetCatLevel2= cursor.getString(3);
					assetDO.assetCatLevel3 	= cursor.getString(4);
					assetDO.assetCatLevel4 	= cursor.getString(5);
					assetDO.assetCatLevel5 	= cursor.getString(6);
					assetDO.status 	= cursor.getString(7);
					assetDO.Site 	= cursor.getString(8);
					assetDO.UserCode 	= cursor.getString(9);
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
	
	public String isHoldOrderIsThere(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor   = null;
			String order_ID = "";
			SQLiteDatabase sqLiteDatabase =null;
			
			try
			{
				String query1 	= "SELECT OrderId FROM tblOrderHeader WHERE TRXStatus = 'H' AND Status !='-10' AND OrderDate LIKE '%"+date+"%'";
				
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	 	= 	sqLiteDatabase.rawQuery(query1, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						order_ID = order_ID+cursor.getString(0)+",";
					} 
					while (cursor.moveToNext());
				}
				
				if(order_ID.contains(","))
					order_ID = order_ID.substring(0, order_ID.lastIndexOf(","));
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
	
			return order_ID;
		}
	}
	
	public String isHoldOrderIsThere(String date, String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor   = null;
			String order_ID = "";
			SQLiteDatabase sqLiteDatabase =null;
			
			try
			{
				String query1 	= "SELECT OrderId FROM tblOrderHeader WHERE TRXStatus = 'H' AND Status !='-10' AND SiteNo = '"+site+"'";
				
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	 	= 	sqLiteDatabase.rawQuery(query1, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						order_ID = order_ID+cursor.getString(0)+",";
					} 
					while (cursor.moveToNext());
				}
				
				if(order_ID.contains(","))
					order_ID = order_ID.substring(0, order_ID.lastIndexOf(","));
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
	
			return order_ID;
		}
	}
	
	public HashMap<String, Float> getAllFactor()
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, Float> hmInv = new HashMap<String, Float>();
			Cursor cursor   = null;
			SQLiteDatabase sqLiteDatabase = null;
			
			try
			{
				String query1 	= "SELECT ItemCode, UOM, Factor FROM tblUOMFactor";
				
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	 	= 	sqLiteDatabase.rawQuery(query1, null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						String itemCode = 	cursor.getString(0);
						String UOM 		= 	cursor.getString(1);
						float factor 	= 	cursor.getFloat(2);
						
						hmInv.put(itemCode + UOM, factor);
					}
					while (cursor.moveToNext());
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
			return hmInv;
		}
	}
	
	public boolean getAllPaymentsStatus(String date, int UPLOAD_DATA)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isAllPosted = true;
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase  =  DatabaseHelper.openDataBase();
				String strQuery = "";
				
				if(UPLOAD_DATA == AppStatus.TODAY_DATA)
					strQuery =  "SELECT DISTINCT COUNT(*) FROM tblPaymentHeader where Status <= 0 AND PaymentDate like '"+date+"%'";
				else
					strQuery =  "SELECT DISTINCT COUNT(*) FROM tblPaymentHeader where Status <= 0";
				
				cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					int count = cursor.getInt(0);
					if(count > 0 )
						isAllPosted = false;
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAllPosted;
		}
	}

	public void updateImageStatus(DamageImageDO damageImageDO, int Status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB 				= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate	= 	objSqliteDB.compileStatement("UPDATE tblOrderImage SET ImagePath = ?,CapturedDate = ? WHERE OrderNo = ? AND ItemCode = ? AND LineNo= ?");
				
				if(damageImageDO != null)
				{
					stmtUpdate.bindString(1, damageImageDO.ImagePath);
					stmtUpdate.bindString(2, ""+Status);
					stmtUpdate.bindString(3, damageImageDO.OrderNo);
					stmtUpdate.bindString(4, damageImageDO.ItemCode);
					stmtUpdate.bindString(5, damageImageDO.LineNo);
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
	public ArrayList<NonPriceItemsDO> getAllNonPriceItems() 
	{
		
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			ArrayList<NonPriceItemsDO> vecnonPriceDO = new ArrayList<NonPriceItemsDO>();
			NonPriceItemsDO nonPriceItems = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT TS.ItemCode, TS.SellableQuantity, TP.Description FROM tblVanStock TS "
						+ "inner join tblProducts TP on  TS.ItemCode =  TP.ItemCode  "
						+ "WHERE TS.ItemCode NOT IN (SELECT ItemCode from tblCustomerPricing WHERE PRICECASES>0) AND SellableQuantity>0";
				cursor =  objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						nonPriceItems = new NonPriceItemsDO();
						nonPriceItems.item =  cursor.getString(0);
						nonPriceItems.vanQty =  cursor.getInt(1);
						nonPriceItems.description =  cursor.getString(2);
						vecnonPriceDO.add(nonPriceItems);
					}while(cursor.moveToNext());
					
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					 objSqliteDB.close();
			}
			
			return vecnonPriceDO;
		}
	
	}
	//=================================Added For VAT===========================
	public Object[] getAllTaxDetailsInfo(String orgcode)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;

			Object obj[]=new Object[2];

			HashMap<String,ArrayList<ItemWiseTaxViewDO>> hmAlltaxItemdetails = new HashMap<String,ArrayList<ItemWiseTaxViewDO>>();
			ArrayList<OrderWiseTaxViewDO> arrAlltaxOrderdetails = new ArrayList<OrderWiseTaxViewDO>();

			try {

				mDatabase = DatabaseHelper.openDataBase();
				String mainQuery = "Select DISTINCT T.UID AS TAXUID,T.Name AS TaxName,T.ApplicableAt,T.DependentTaxUID,T.TaxCalculationType,T.BaseTaxRate,T.Status" +
						" ,TG.Description TaxGroupDescription, TG.Name TaxGroupName" +
						" ,TSM.SKUUID, TGT.TaxGroupUID,'' TaxSKUMapUID" +  /********TaxSKUMapUID Changed on 4th Jan 2018 as suggest by Vishal Sir, Bcoz of duplication of record ****************/
						" ,TS.UID TaxSlabUID,TS.RangeStart,TS.RangeEnd,TS.TaxRate " +
						"  FROM TaxGroup TG" +
						" INNER JOIN Org O ON O.TaxGroupUID = TG.UID AND O.UID = '"+orgcode+"'" +
						" INNER JOIN TaxGroupTaxes TGT On TGT.TaxGroupUID = TG.UID" +
						" INNER JOIN TAX T ON T.UID = TGT.TaxUID AND T.Status = 'Active' " +
						" LEFT Join TaxSkuMap TSM On TGT.TaxUID=TSM.TaxUID" +
						"  LEFT Join TaxSlab TS On TS.TaxUID=T.UID AND TS.Status='Active' ";
				cursor = mDatabase.rawQuery(mainQuery, null);

				if(cursor.moveToFirst())
				{
					do
					{
						ItemWiseTaxViewDO ItemTaxViewDO = new ItemWiseTaxViewDO();
						ItemTaxViewDO.TaxUID = cursor.getString(0);
						ItemTaxViewDO.TaxName = cursor.getString(1);
						ItemTaxViewDO.ApplicableAt = cursor.getString(2);
						ItemTaxViewDO.DependentTaxUID = cursor.getString(3);
						ItemTaxViewDO.TaxCalculationType = cursor.getString(4);
						ItemTaxViewDO.BaseTaxRate = cursor.getFloat(5);
//						ItemTaxViewDO. = cursor.getString(6);
						ItemTaxViewDO.TaxGroupDescription = cursor.getString(7);
						ItemTaxViewDO.TaxGroupName = cursor.getString(8);
						ItemTaxViewDO.SKUUID = cursor.getString(9);
						ItemTaxViewDO.TaxGroupUID = cursor.getString(10);
						ItemTaxViewDO.TaxSKUMapUID = cursor.getString(11);
						ItemTaxViewDO.TaxSlabUID = cursor.getString(12);
						ItemTaxViewDO.RangeStart = cursor.getFloat(13);
						ItemTaxViewDO.RangeEnd = cursor.getFloat(14);
						ItemTaxViewDO.TaxRate = cursor.getFloat(15);


						OrderWiseTaxViewDO OrderTaxViewDO = new OrderWiseTaxViewDO();

						OrderTaxViewDO.TaxUID =  cursor.getString(0);
						OrderTaxViewDO.TaxSlabUID = cursor.getString(12);
						OrderTaxViewDO.TaxName = cursor.getString(1);
						OrderTaxViewDO.ApplicableAt = cursor.getString(2);
						OrderTaxViewDO.DependentTaxUID =  cursor.getString(3);
						OrderTaxViewDO.TaxCalculationType =cursor.getString(4);
						OrderTaxViewDO.BaseTaxRate = cursor.getFloat(5);
						ItemTaxViewDO.SKUUID = cursor.getString(9)+"";
						OrderTaxViewDO.TaxGroupUID =  cursor.getString(10);
						OrderTaxViewDO.RangeStart = cursor.getFloat(13);
						OrderTaxViewDO.RangeEnd = cursor.getFloat(14);
						OrderTaxViewDO.TaxRate = cursor.getFloat(15);
						OrderTaxViewDO.TaxAmount = 0;



						if(ItemTaxViewDO.ApplicableAt.equalsIgnoreCase("Item")){
							if(hmAlltaxItemdetails.containsKey(ItemTaxViewDO.SKUUID)){
								hmAlltaxItemdetails.get(ItemTaxViewDO.SKUUID).add(ItemTaxViewDO);
							}else{
								ArrayList<ItemWiseTaxViewDO> arrItemWiseTaxViewDO=new ArrayList<ItemWiseTaxViewDO>();
								arrItemWiseTaxViewDO.add(ItemTaxViewDO);
								hmAlltaxItemdetails.put(ItemTaxViewDO.SKUUID, arrItemWiseTaxViewDO);
							}
						}else if(OrderTaxViewDO.ApplicableAt.equalsIgnoreCase("Invoice") || OrderTaxViewDO.ApplicableAt.equalsIgnoreCase("TaxOnTax")){
							arrAlltaxOrderdetails.add(OrderTaxViewDO);
						}
					} while (cursor.moveToNext());


					obj[0]=hmAlltaxItemdetails;
					obj[1]=arrAlltaxOrderdetails;
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

	public HashMap<String ,String> getAllArabicItems(  String orderId )
	{

		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			HashMap<String ,String> hmArabic= new HashMap<String, String>();
			NonPriceItemsDO nonPriceItems = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "select   TP.itemcode , TP.description1,Categorylevel5  from tblorderdetail TOD inner join tblorderheader T  on T.orderid= TOD.orderno  " +
						"inner join tblproducts TP on TP.itemcode = tOD.itemcode where T .orderid='"+orderId+"'";
				cursor =  objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						 String key = cursor.getString(0);
						String value =  cursor.getString(1);
						String value2 =  cursor.getString(2);
						hmArabic.put(key, value);
					}while(cursor.moveToNext());

				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}

			return hmArabic;
		}

	}
	public Object [] getCustomerArabicDetails(  String customerId )
	{

		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			HashMap<String ,String> hmArabic= new HashMap<String, String>();
			NonPriceItemsDO nonPriceItems = null;
			Object object[] = new Object[9];
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = " select SiteNameInArabic,Address1_AR,Address2_AR, Address3_AR," +
						" AreaName_AR,PostalCode_AR, LocationName_AR, City_AR,SalesPersonMobileNumber from tblcustomer where site='"+customerId+"'";
				cursor =  objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					 object[0] = cursor.getString(0); //sitemname
					 object[1] = cursor.getString(1); //address1
					 object[2] = cursor.getString(2); //address2
					 object[3] = cursor.getString(3); //address3
					 object[4] = cursor.getString(4); //areaname
					 object[5] = cursor.getString(5); //postal code
					 object[6] = cursor.getString(6); //local name
					 object[7] = cursor.getString(7); //city
					 object[8] = cursor.getString(7); //SalesPersonMobileNumber


				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}

			return object;
		}

	}






}
