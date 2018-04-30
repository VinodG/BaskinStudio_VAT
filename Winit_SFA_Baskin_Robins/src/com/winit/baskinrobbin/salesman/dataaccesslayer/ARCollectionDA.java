package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class ARCollectionDA 
{
	private Vector<PendingInvicesDO> vecPendingInvoices = new Vector<PendingInvicesDO>();
	public  Vector<PendingInvicesDO> getPendingInvoices(JourneyPlanDO journeyPlanDO, String invoiceNumber,String From)
	{
		synchronized(MyApplication.MyLock) 
		{
			if(vecPendingInvoices!=null && vecPendingInvoices.size()>0)
				vecPendingInvoices.clear();
			
			String strPeningInvoices = "";
			String strOnAccountdata = "";
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
		    
			
			if(journeyPlanDO.CREDIT_LEVEL != null && journeyPlanDO.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT))
				strPeningInvoices = "Select TPI.InvoiceNumber,TPI.BalanceAmount,TPI.InvoiceDate ,TPI.OrderId,TPI.IsOutStanding," +
						" TOH.OrderType,TPI.TotalAmount, TPI.ERPReference,TPI.DocType from tblPendingInvoices TPI " +
						"LEFT JOIN tblOrderHeader TOH on TPI.OrderId = TOH .OrderId " +
						"WHERE TPI.CustomerSiteId IN (SELECT SITE FROM tblCustomer WHERE CustomerId ='"+ journeyPlanDO.customerId+"') " +
						"and TPI.BalanceAmount != 0 AND TPI.Status ='N' ORDER BY TPI.InvoiceDate";
			else
				strPeningInvoices = "Select TPI.InvoiceNumber,TPI.BalanceAmount,TPI.InvoiceDate ,TPI.OrderId,TPI.IsOutStanding," +
						" TOH.OrderType,TPI.TotalAmount, TPI.ERPReference,TPI.DocType from tblPendingInvoices TPI " +
						"LEFT JOIN tblOrderHeader TOH on TPI.OrderId = TOH .OrderId " +
						"WHERE TPI.CustomerSiteId ='"+ journeyPlanDO.site+"' and TPI.BalanceAmount != 0 AND TPI.Status ='N' ORDER BY TPI.InvoiceDate";
			
			DictionaryEntry [][] data = null;
			try
			{
				data	=	DatabaseHelper.get(strPeningInvoices);
				if(data!=null && data.length>0)
				{
					for(int i=0;i<data.length;i++)
					{
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= data[i][0].value.toString();
						pendingInvicesDO.balance 		= data[i][1].value.toString();
						pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
						pendingInvicesDO.invoiceDate 	= data[i][2].value.toString();
						if(data[i][3].value!=null)
							pendingInvicesDO.orderId 	= data[i][3].value.toString();
						if(data[i][4].value!=null)
							pendingInvicesDO.IsOutStanding	= ""+data[i][4].value.toString();
						pendingInvicesDO.isNewleyAdded	=	false;
						
						if(pendingInvicesDO.invoiceDate.contains("-"))
							pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
						else
							pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString_(pendingInvicesDO.invoiceDate);
						
						if(data[i][5].value!=null)
							pendingInvicesDO.TRX_TYPE = ""+data[i][5].value.toString();
						else
							pendingInvicesDO.TRX_TYPE = AppConstants.HHOrder;
						
						if(data[i][6].value!=null)
							pendingInvicesDO.totalAmount = ""+data[i][6].value.toString();
						
						if(data[i][7].value!=null)
							pendingInvicesDO.ebs_ref_no = ""+data[i][7].value.toString();
						
						if(data[i][8].value!=null)
							pendingInvicesDO.DOC_TYPE = ""+data[i][8].value.toString();
						
						if(StringUtils.getFloat(pendingInvicesDO.balance) < 0 || pendingInvicesDO.DOC_TYPE.equalsIgnoreCase(AppConstants.RETURN_INV_CODE))
						{
							if(StringUtils.getFloat(pendingInvicesDO.totalAmount) == 0)
								pendingInvicesDO.totalAmount = pendingInvicesDO.balance;
							
							pendingInvicesDO.INV_TYPE = AppConstants.RETURN_INV;
						}
						else if(StringUtils.getFloat(pendingInvicesDO.balance)>0)
							pendingInvicesDO.INV_TYPE = AppConstants.SALES_INV;
						
						if(pendingInvicesDO.INV_TYPE == AppConstants.SALES_INV && StringUtils.getFloat(pendingInvicesDO.balance) <= 0){
							
						}else{
							if(!TextUtils.isEmpty(invoiceNumber) && invoiceNumber.equalsIgnoreCase(pendingInvicesDO.invoiceNo))
								vecPendingInvoices.add(0, pendingInvicesDO);
							else
								vecPendingInvoices.add(pendingInvicesDO);
						}
						
					}
				} 
				
		if(From.equalsIgnoreCase("ARCollection")){
			//this is for getting the onaccount payment data
			strOnAccountdata="select TPI.Amount,TPH.PaymentDate from tblPaymentInvoice TPI  INNER JOIN tblPaymentHeader TPH ON TPI.ReceiptId=TPH.ReceiptId AND TPH.SiteId='"+journeyPlanDO.site+"' AND TPI.TrxType='ONACCOUNT' ORDER BY TPH.PaymentDate";
				mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery(strOnAccountdata, null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
					pendingInvicesDO.balance        = cursor.getString(0);
					pendingInvicesDO.invoiceDate 	= cursor.getString(1);
					pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
					pendingInvicesDO.invoiceNo 		= PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT;
				
					vecPendingInvoices.add(pendingInvicesDO);
				}while(cursor.moveToNext());
			}
		}		
				
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally{
				if(mDatabase!=null&& mDatabase.isOpen())
					mDatabase.close();
				if(cursor!=null)
				cursor.close();
			}
			return vecPendingInvoices;
		}
	}
	
	public boolean getPendingInvoicesCount(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isAnyPendingInvoice =false;
			try
			{
				String strPeningInvoices = "Select count(InvoiceNumber) from tblPendingInvoices where CustomerSiteId ='"+customerSiteId+"' and BalanceAmount > 0 AND Status ='N'";
				DictionaryEntry [][] data = DatabaseHelper.get(strPeningInvoices);
				if(data!=null && data.length>0)
				{
					int count = StringUtils.getInt(data[0][0].value.toString());
					
					if(count == 0)
						isAnyPendingInvoice= false;
					else
						isAnyPendingInvoice= true;
				}
				
				isAnyPendingInvoice = false;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return isAnyPendingInvoice;
		}
	}
	
	public boolean isOutStanding(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isOutStanding =false;
			try
			{
				String strPeningInvoices = "Select IsOutStanding from tblPendingInvoices where CustomerSiteId ='"+customerSiteId+"' LIMIT 1";
				DictionaryEntry [][] data = DatabaseHelper.get(strPeningInvoices);
				if(data!=null && data.length>0)
				{
					isOutStanding = StringUtils.getBoolean(data[0][0].value.toString());
				}
				
				isOutStanding = false;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return isOutStanding;
		}
	}
	
	public int getPendingInvoicesCountBySite(String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			int count = 0;
			try
			{
				String strPeningInvoices = "Select count(*) from tblPendingInvoices where CustomerSiteId ='"+siteId+"' and BalanceAmount > 0 AND Status ='N'";
				DictionaryEntry [][] data = DatabaseHelper.get(strPeningInvoices);
				
				if(data!=null && data.length>0)
					count = StringUtils.getInt(data[0][0].value.toString());
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return count;
		}
	}
	
	//method to get last order by customer id for return request
	public  Vector<PendingInvicesDO> getInvoicedOrderByCustomerSiteId(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strPeningInvoices = "Select OrderId, InvoiceNumber,BalanceAmount,InvoiceDate from tblInvoiceOrders where CustomerSiteId ='"+ customerSiteId+"'";
			DictionaryEntry [][] data = null;
			try
			{
				data	=	DatabaseHelper.get(strPeningInvoices);
				if(data!=null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.orderId 		= data[i][0].value.toString();
						pendingInvicesDO.invoiceNo 		= data[i][1].value.toString();
						pendingInvicesDO.balance 		= data[i][2].value.toString();
						pendingInvicesDO.invoiceDate 	= data[i][3].value.toString();
						vecPendingInvoices.add(pendingInvicesDO);
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return vecPendingInvoices;
		}
	}

	public void deleteUnUsedPendingInvoices() 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				if(objSqliteDB != null)
				{
//					String  strPeningInvoices = "DELETE FROM tblPendingInvoices WHERE InvoiceDate LIKE '"+DATE+"%' " +
//							            		"AND InvoiceNumber NOT IN (SELECT OrderId FROM tblOrderHeader) " +
//							            		"AND InvoiceNumber NOT IN (SELECT TrxCode FROM tblPaymentInvoice) " +
//							            		"AND TRANS_TYPE_NAME = '"+AppConstants.CURRENTORDER+"' " +
//							            		"AND DocType = '"+AppConstants.SALES_INV_CODE+"'";
					String  strPeningInvoices = "DELETE FROM tblPendingInvoices WHERE  " +
							"InvoiceNumber NOT IN (SELECT OrderId FROM tblOrderHeader) " +
							"AND InvoiceNumber NOT IN (SELECT TrxCode FROM tblPaymentInvoice) " +
							"AND TRANS_TYPE_NAME = '"+AppConstants.CURRENTORDER+"' " +
							"AND DocType = '"+AppConstants.SALES_INV_CODE+"'";
					
					objSqliteDB.execSQL(strPeningInvoices);
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
}
