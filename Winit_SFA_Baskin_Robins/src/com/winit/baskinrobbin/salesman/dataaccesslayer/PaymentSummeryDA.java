package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;

public class PaymentSummeryDA 
{
	public HashMap<String, ArrayList<Customer_InvoiceDO>> getCustomerInvoice(String startdate,String endDate, JourneyPlanDO journeyPlanDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, ArrayList<Customer_InvoiceDO>> hmPayments = new HashMap<String, ArrayList<Customer_InvoiceDO>>();
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			
			if(journeyPlanDO == null)
				query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo," +
						" PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName , " +
						"PH.Status,PH.Amount FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS " +
						"where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND " +
						"julianday(PH.PaymentDate)>=julianday('"+startdate+"') AND julianday(PH.PaymentDate)<=julianday( '"+endDate+"') ORDER BY PD.PaymentTypeCode";
			
			else
				query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName " +
						", PH.Status,PH.Amount FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.SiteId = '"+journeyPlanDO.site+"' AND" +
								" julianday(PH.PaymentDate)>=julianday('"+startdate+"') AND julianday(PH.PaymentDate)<=julianday( '"+endDate+"') ORDER BY PD.PaymentTypeCode";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						Customer_InvoiceDO object 	= new Customer_InvoiceDO();
						object.customerSiteId 		= cursor.getString(0);
						object.reciptType 			= cursor.getString(1);
						object.siteName 			= cursor.getString(2);
						object.receiptNo 			= cursor.getString(3);
						object.invoiceTotal 	    = cursor.getString(4);
						object.creditCardNo 		= cursor.getString(5);
						object.chequeNo 			= cursor.getString(6);
						object.uuid 				= cursor.getString(7);
						object.invoiceTotal 		= cursor.getString(8);
						object.reciptDate 			= cursor.getString(9);
						object.currencyCode			= cursor.getString(10);
						object.chequeDate			= cursor.getString(11);
						object.bankName				= cursor.getString(12);
						object.status				= cursor.getInt(13);
						object.totalVal				= ""+cursor.getFloat(14);
						
						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId FROM tblPaymentInvoice where ReceiptId ='"+object.receiptNo+"'", null);
						if(innerCursor.moveToFirst())
						{
							object.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1); 
								object.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						
						if(hmPayments != null && hmPayments.containsKey(object.reciptType))
						{
							ArrayList<Customer_InvoiceDO> arr = hmPayments.get(object.reciptType);
							arr.add(object);
							hmPayments.put(object.reciptType, arr);
						}
						else
						{
							ArrayList<Customer_InvoiceDO> arr = new ArrayList<Customer_InvoiceDO>();
							arr.add(object);
							hmPayments.put(object.reciptType, arr);
						}
						
					}while(cursor.moveToNext());
					
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
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			
			return hmPayments;
		}
	}
	public HashMap<String, ArrayList<Customer_InvoiceDO>> getCustomerInvoice(String date, JourneyPlanDO journeyPlanDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, ArrayList<Customer_InvoiceDO>> hmPayments = new HashMap<String, ArrayList<Customer_InvoiceDO>>();
			Cursor cursor  = null;
			Cursor innerCursor = null;
			String query   = "";
			
			if(journeyPlanDO == null)
				query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName " +
						", PH.Status,PH.Amount FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.PaymentDate LIKE '"+date+"%' ORDER BY PD.PaymentTypeCode";
			else
				query = "SELECT DISTINCT PH.SiteId, PD.PaymentTypeCode, CS.SiteName, PH.ReceiptId, PH.AMOUNT,PD.CCNo,PD.ChequeNo, PH.AppPaymentId,PD.Amount,PH.PaymentDate,CS.CurrencyCode,PD.ChequeDate, PD.UserDefinedBankName " +
						", PH.Status,PH.Amount FROM tblPaymentHeader PH,tblPaymentDetail PD,tblCustomer CS where PH.ReceiptId=PD.ReceiptNo and PH.SiteId=CS.Site AND PH.SiteId = '"+journeyPlanDO.site+"' AND PH.PaymentDate LIKE '"+date+"%' ORDER BY PD.PaymentTypeCode";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						Customer_InvoiceDO object 	= new Customer_InvoiceDO();
						object.customerSiteId 		= cursor.getString(0);
						object.reciptType 			= cursor.getString(1);
						object.siteName 			= cursor.getString(2);
						object.receiptNo 			= cursor.getString(3);
						object.invoiceTotal 	    = cursor.getString(4);
						object.creditCardNo 		= cursor.getString(5);
						object.chequeNo 			= cursor.getString(6);
						object.uuid 				= cursor.getString(7);
						object.invoiceTotal 		= cursor.getString(8);
						object.reciptDate 			= cursor.getString(9);
						object.currencyCode			= cursor.getString(10);
						object.chequeDate			= cursor.getString(11);
						object.bankName				= cursor.getString(12);
						object.status				= cursor.getInt(13);
						object.totalVal				= ""+cursor.getFloat(14);
						
						innerCursor = DatabaseHelper._database.rawQuery("SELECT TrxCode,Amount,ReceiptId FROM tblPaymentInvoice where ReceiptId ='"+object.receiptNo+"'", null);
						if(innerCursor.moveToFirst())
						{
							object.vecPaymentDetailDOs = new Vector<PaymentDetailDO>();
							do 
							{
								PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
								paymentDetailDO.invoiceNumber = innerCursor.getString(0);
								paymentDetailDO.invoiceAmount = innerCursor.getString(1); 
								object.vecPaymentDetailDOs.add(paymentDetailDO);
								
							}while(innerCursor.moveToNext());
						}
						if(innerCursor!=null && !innerCursor.isClosed())
							innerCursor.close();
						
						if(hmPayments != null && hmPayments.containsKey(object.reciptType))
						{
							ArrayList<Customer_InvoiceDO> arr = hmPayments.get(object.reciptType);
							arr.add(object);
							hmPayments.put(object.reciptType, arr);
						}
						else
						{
							ArrayList<Customer_InvoiceDO> arr = new ArrayList<Customer_InvoiceDO>();
							arr.add(object);
							hmPayments.put(object.reciptType, arr);
						}
						
					}while(cursor.moveToNext());
					
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
				if(innerCursor!=null && !innerCursor.isClosed())
					innerCursor.close();
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				DatabaseHelper.closedatabase();
			}
			
			return hmPayments;
		}
	}
	
	public HashMap<String, String> getTotalAmount(boolean isPreseller,String date, JourneyPlanDO mallsDetails)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, String> hsTotal = new HashMap<String, String>();
			Cursor cursor  = null;
			String query   = "";
			
			if(mallsDetails == null)
				query = "SELECT PD.PaymentTypeCode,SUM(PD.Amount) FROM tblPaymentDetail PD INNER JOIN tblPaymentHeader PH ON PH.ReceiptId = PD.ReceiptNo WHERE PH.PaymentDate LIKE '"+date+"%' GROUP BY PD.PaymentTypeCode";
			else
				query = "SELECT PD.PaymentTypeCode,SUM(PD.Amount) FROM tblPaymentDetail PD INNER JOIN tblPaymentHeader PH ON PH.ReceiptId = PD.ReceiptNo WHERE PH.SiteId = '"+mallsDetails.site+"' AND PH.PaymentDate LIKE '"+date+"%' GROUP BY PD.PaymentTypeCode";
					
			try
			{
				DatabaseHelper.openDataBase();
				cursor = DatabaseHelper._database.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						hsTotal.put(cursor.getString(0), cursor.getString(1));
						
					}while(cursor.moveToNext());
					
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
				DatabaseHelper.closedatabase();
			}
			return hsTotal;
		}
	}
	
	
	public Vector<OrderDO> getPresellerReturnOrderList()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<OrderDO> vecOrderList 	= new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			String query ="SELECT  TG.GRVId, TG.CustomerSiteId ,TCS.SiteName,TG .GRVDate  from tblGRV TG , tblCustomerSites TCS where TG.CustomerSiteId = TCS .CustomerSiteId";
					
			try
			{
				sqLiteDatabase 	= DatabaseHelper.openDataBase();
				cursor 			= sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDO object 			= 	new OrderDO();
						object.OrderId 			= 	cursor.getString(0);
						object.CustomerSiteId 	= 	cursor.getString(1);
						object.strCustomerName	= 	cursor.getString(2);
						object.InvoiceDate		= 	cursor.getString(3);
						object.orderType 		= 	"RETURNORDER";
						
						vecOrderList.add(object);
						
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
				return vecOrderList;
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
			return new Vector<OrderDO>();
		}
	}
}

