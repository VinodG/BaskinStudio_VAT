package com.winit.baskinrobbin.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class CustomerOrderDA
{
	public boolean isCustomerOrderGenerated(String customerSiteID, String currentDate) 
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isTrue = false;
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor	 = null;
			try 
			{
				sqLiteDatabase  =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT * FROM tblOrderHeader where SiteNo ='"+customerSiteID+"' and (OrderDate like '%"+currentDate+"%' OR (DeliveryDate like '%"+currentDate+"%' AND SubType ='"+AppConstants.LPO_ORDER+"' AND TRXStatus='D'))";
				LogUtils.errorLog("strQuery - ", "strQuery - "+strQuery);
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
					isTrue = true;
				else 
					isTrue = false;
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				isTrue = true;
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			
			return isTrue;
		}
	}
	public void deleteAllRecords(String strFor,boolean isJourneyLog)
	{
		synchronized(MyApplication.MyLock) 
		{
			LogUtils.errorLog("CustomerOrderDA", "Deleting all tables data");
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM tblPresellerPassCode");
				sqlDB.execSQL("DELETE FROM tblCustomerHistory");
				sqlDB.execSQL("DELETE FROM tblPendingInvoices");
				sqlDB.execSQL("DELETE FROM tblInvoiceOrderDetails");
				sqlDB.execSQL("DELETE FROM tblInvoiceOrders");
				
				sqlDB.execSQL("DELETE FROM tblCustomerSurvey");
				sqlDB.execSQL("DELETE FROM tblCustomersInterview");
				sqlDB.execSQL("DELETE FROM tblGRV");
				sqlDB.execSQL("DELETE FROM tblGRVDetails");
				sqlDB.execSQL("DELETE FROM tblJourneyPlan");
				
				sqlDB.execSQL("DELETE FROM tblMessages");
				sqlDB.execSQL("DELETE FROM tblMonthlyTarget");
				sqlDB.execSQL("DELETE FROM tblNotes");
				sqlDB.execSQL("DELETE FROM tblOrderDetail");
				sqlDB.execSQL("DELETE FROM tblOrderHeader");
				sqlDB.execSQL("DELETE FROM tblTrxPromotion");
				sqlDB.execSQL("DELETE FROM tblPaymentDetail");
				
				sqlDB.execSQL("DELETE FROM tblPaymentHeader");
				sqlDB.execSQL("DELETE FROM tblPresellerDailyTargets");
				sqlDB.execSQL("DELETE FROM tblPresellerTarget");
				sqlDB.execSQL("DELETE FROM tblSurvey");
				
				sqlDB.execSQL("DELETE FROM tblSurveyOptions");
				sqlDB.execSQL("DELETE FROM tblSurveyQuestion");
				sqlDB.execSQL("DELETE FROM tblTopFiveSellingItems");
				sqlDB.execSQL("DELETE FROM tblTopFiveSellingItems");
				
				sqlDB.execSQL("DELETE FROM tblTopSelling");
				sqlDB.execSQL("DELETE FROM tblMustHave");
				sqlDB.execSQL("DELETE FROM tblCustomerSites");
				sqlDB.execSQL("DELETE FROM tblCustomers");
				sqlDB.execSQL("DELETE FROM tblDeliveryAgentJourneyPlan");
				sqlDB.execSQL("DELETE FROM tblDeliveryOrderDetail");
				sqlDB.execSQL("DELETE FROM tblDeliveryOrders");
				sqlDB.execSQL("DELETE FROM tblDeliverySites");
				sqlDB.execSQL("DELETE FROM tblReceiptNo");
				sqlDB.execSQL("DELETE FROM tblPetrolReport");
				
				sqlDB.execSQL("DELETE FROM tblDeliveredOrdersDetail");
				sqlDB.execSQL("DELETE FROM tblDeliveredSites");
				sqlDB.execSQL("DELETE FROM tblDeliveryAgentOldJourneyPlan");
				sqlDB.execSQL("DELETE FROM tblDeliveredOrders");
				sqlDB.execSQL("DELETE FROM tblPresellerTargetByCat");
				sqlDB.execSQL("DELETE FROM tblInventory");
				sqlDB.execSQL("DELETE FROM tblInventoryDetail");
				sqlDB.execSQL("DELETE FROM tblPaymentInvoice");
				
				if(isJourneyLog)
					sqlDB.execSQL("DELETE FROM tblJourneyLog");
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
	
	public void deleteOrder(String orderId,String uuid,String orderType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqlDB = null;
			try 
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM tblOrderHeader where OrderId='"+orderId+"' and AppOrderId='"+uuid+"'");
				sqlDB.execSQL("DELETE FROM tblOrderDetail where OrderNo='"+orderId+"'");
				sqlDB.execSQL("DELETE FROM tblTrxPromotion where OrderId='"+orderId+"'");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqlDB != null)
					sqlDB.close();
			}
		}
	}
	public void deleteEOTData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqlDB = null;
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM tblOrderHeader");
				sqlDB.execSQL("DELETE FROM tblOrderDetail");
				sqlDB.execSQL("DELETE FROM tblTrxPromotion");
				sqlDB.execSQL("DELETE FROM tblPaymentHeader");
				sqlDB.execSQL("DELETE FROM tblPaymentDetail");
				sqlDB.execSQL("DELETE FROM tblPaymentInvoice");
				sqlDB.execSQL("DELETE FROM tblGRV");
				sqlDB.execSQL("DELETE FROM tblGRVDetails");
				sqlDB.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqlDB != null)
					sqlDB.close();
			}
		}
	}
}
