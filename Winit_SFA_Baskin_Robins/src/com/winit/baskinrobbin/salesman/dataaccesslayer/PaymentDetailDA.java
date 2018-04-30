package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDetailDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class PaymentDetailDA 
{
	public boolean insertPaymentDetails(PaymentHeaderDO paymentHeaderDO,String SalesmanCode, Preference preference)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean isInserted = true;
			try 
			{
				objSqliteDB  = DatabaseHelper.openDataBase();
				objSqliteDB.beginTransaction();
				
				//generating the Order ID here
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentHeader (AppPaymentId,RowStatus,ReceiptId,PreReceiptId," +
																		 "PaymentDate,SiteId,EmpNo,Amount,CurrencyCode,Rate,VisitCode,PaymentStatus," +
																		 "CustomerSignature,Status,AppPayementHeaderId,PaymentType, VehicleCode, CollectedBy, Receipt_Method_Name,IsCustomerSigPushed) " +
																		 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtDetailInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentDetail (RowStatus,ReceiptNo,LineNo,PaymentTypeCode," +
						 "BankCode,ChequeDate,ChequeNo,CCNo,CCExpiry,PaymentStatus,PaymentNote,UserDefinedBankName,Status,Amount) " +
						 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								
				SQLiteStatement stmtInvoiceInsert = objSqliteDB.compileStatement("INSERT INTO tblPaymentInvoice (RowStatus,ReceiptId,TrxCode,TrxType," +
						 "Amount,CurrencyCode,Rate,PaymentStatus,PaymentType, CashDiscount, ERPReference) " +
						 "VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				if(paymentHeaderDO != null)
				{
					stmtInsert.bindString(1,  paymentHeaderDO.AppPaymentId);
					stmtInsert.bindString(2,  paymentHeaderDO.RowStatus);
					stmtInsert.bindString(3,  paymentHeaderDO.ReceiptId);
					stmtInsert.bindString(4,  paymentHeaderDO.PreReceiptId);
					stmtInsert.bindString(5,  paymentHeaderDO.PaymentDate);
					stmtInsert.bindString(6,  paymentHeaderDO.SiteId);
					stmtInsert.bindString(7,  paymentHeaderDO.EmpNo);
					stmtInsert.bindString(8,  paymentHeaderDO.Amount);
					stmtInsert.bindString(9,  paymentHeaderDO.CurrencyCode);
					stmtInsert.bindString(10, paymentHeaderDO.Rate);
					stmtInsert.bindString(11, paymentHeaderDO.VisitCode);
					stmtInsert.bindString(12, paymentHeaderDO.PaymentStatus);
					stmtInsert.bindString(13, paymentHeaderDO.CustomerSignature);
					stmtInsert.bindString(14, paymentHeaderDO.Status);
					stmtInsert.bindString(15, ""+paymentHeaderDO.AppPayementHeaderId);
					stmtInsert.bindString(16, ""+paymentHeaderDO.PaymentType);
					stmtInsert.bindString(17, ""+paymentHeaderDO.vehicleNo);
					stmtInsert.bindString(18, ""+paymentHeaderDO.salesmanCode);
					stmtInsert.bindString(19, ""+paymentHeaderDO.Receipt_Method_Name);
					stmtInsert.bindString(20, "false");

					for(PaymentDetailDO paymentDetailDO : paymentHeaderDO.vecPaymentDetails)
					{
						stmtDetailInsert.bindString(1,  paymentDetailDO.RowStatus);
						stmtDetailInsert.bindString(2,  paymentDetailDO.ReceiptNo);
						stmtDetailInsert.bindString(3,  paymentDetailDO.LineNo);
						stmtDetailInsert.bindString(4,  paymentDetailDO.PaymentTypeCode);
						stmtDetailInsert.bindString(5,  paymentDetailDO.BankCode);
						stmtDetailInsert.bindString(6,  paymentDetailDO.ChequeDate);
						stmtDetailInsert.bindString(7,  paymentDetailDO.ChequeNo);
						stmtDetailInsert.bindString(8,  paymentDetailDO.CCNo);
						stmtDetailInsert.bindString(9,  paymentDetailDO.CCExpiry);
						stmtDetailInsert.bindString(10, paymentDetailDO.PaymentStatus);
						stmtDetailInsert.bindString(11, paymentDetailDO.PaymentNote);
						stmtDetailInsert.bindString(12, paymentDetailDO.UserDefinedBankName);
						stmtDetailInsert.bindString(13, paymentHeaderDO.Status);
						stmtDetailInsert.bindString(14, paymentDetailDO.Amount);
						stmtDetailInsert.executeInsert();
					}
					
					for(PaymentInvoiceDO paymentInvoiceDO : paymentHeaderDO.vecPaymentInvoices)
					{
						stmtInvoiceInsert.bindString(1, paymentInvoiceDO.RowStatus);
						stmtInvoiceInsert.bindString(2, paymentInvoiceDO.ReceiptId);
						stmtInvoiceInsert.bindString(3, paymentInvoiceDO.TrxCode);
						stmtInvoiceInsert.bindString(4, paymentInvoiceDO.TrxType);
						stmtInvoiceInsert.bindString(5, paymentInvoiceDO.Amount);
						stmtInvoiceInsert.bindString(6, paymentInvoiceDO.CurrencyCode);
						stmtInvoiceInsert.bindString(7, paymentInvoiceDO.Rate);
						stmtInvoiceInsert.bindString(8, paymentInvoiceDO.PaymentStatus);
						stmtInvoiceInsert.bindString(9, paymentInvoiceDO.PaymentType);
						stmtInvoiceInsert.bindString(10,paymentInvoiceDO.CashDiscount);
						stmtInvoiceInsert.bindString(11,paymentInvoiceDO.Ebs_Ref_No);
						stmtInvoiceInsert.executeInsert();
					}
					stmtInsert.executeInsert();
					
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+paymentHeaderDO.ReceiptId+"'");
				}
				
				stmtInvoiceInsert.close();
				stmtDetailInsert.close();
				stmtInsert.close();
				objSqliteDB.setTransactionSuccessful();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				isInserted =  false;
			}
			finally
			{
				if(objSqliteDB != null)
				{
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			return isInserted;
		}
	}
	
	public PaymentHeaderDO getPaymentDetailByInvoiceNumber(String invoiceNumber)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null, cursorDetail = null;
			PaymentHeaderDO paymentHeaderDO = null;
			try 
			{
				objSqliteDB  = DatabaseHelper.openDataBase();
				
				String query    = "SELECT PH.ReceiptId, PH.PaymentDate,PH.SiteId,PH.EmpNo,PH.Amount,PH.PaymentType FROM tblPaymentHeader PH " +
						          "INNER JOIN tblPaymentInvoice PI ON PI.ReceiptId = PH.ReceiptId AND PI.TrxCode = '"+invoiceNumber+"'";
				cursor 			= objSqliteDB.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					paymentHeaderDO 			= new PaymentHeaderDO();
					
					paymentHeaderDO.ReceiptId 	= cursor.getString(0);
					paymentHeaderDO.PaymentDate = cursor.getString(1);
					paymentHeaderDO.SiteId 		= cursor.getString(2);
					paymentHeaderDO.EmpNo 		= cursor.getString(3);
					paymentHeaderDO.Amount 		= cursor.getString(4);
					paymentHeaderDO.PaymentType = cursor.getString(5);
					
					String queryDetail  = "SELECT ReceiptNo, PaymentTypeCode, BankCode,ChequeDate,ChequeNo, UserDefinedBankName, " +
							              "Amount FROM tblPaymentDetail WHERE ReceiptNo = '"+paymentHeaderDO.ReceiptId+"'";
					
					cursorDetail = objSqliteDB.rawQuery(queryDetail, null);
					
					if(cursorDetail.moveToFirst())
					{
						do
						{
							PaymentDetailDO paymentDetailDO 	= new PaymentDetailDO();
							
							paymentDetailDO.ReceiptNo 			= cursorDetail.getString(0);
							paymentDetailDO.PaymentTypeCode 	= cursorDetail.getString(1);
							paymentDetailDO.BankCode			= cursorDetail.getString(2);
							paymentDetailDO.ChequeDate 			= cursorDetail.getString(3);
							paymentDetailDO.ChequeNo 			= cursorDetail.getString(4);
							paymentDetailDO.UserDefinedBankName	= cursorDetail.getString(5);
							paymentDetailDO.BankName			= paymentDetailDO.UserDefinedBankName;
							paymentDetailDO.Amount				= cursorDetail.getString(6);
							paymentHeaderDO.vecPaymentDetails.add(paymentDetailDO);
						} 
						while (cursorDetail.moveToNext());
					}
					
					if(cursorDetail != null && !cursorDetail.isClosed())
						cursorDetail.close();
					
					String queryInv  	= "SELECT ReceiptId, TrxCode,TrxType, Amount, PaymentType FROM tblPaymentInvoice WHERE " +
										  "ReceiptId = '"+paymentHeaderDO.ReceiptId+"'";
					
					cursorDetail = objSqliteDB.rawQuery(queryInv, null);
					
					if(cursorDetail.moveToFirst())
					{
						do 
						{
							PaymentInvoiceDO paymentInvoiceDO 	= new PaymentInvoiceDO();
							
							paymentInvoiceDO.ReceiptId 			= cursorDetail.getString(0);
							paymentInvoiceDO.TrxCode 			= cursorDetail.getString(1);
							paymentInvoiceDO.TrxType			= cursorDetail.getString(2);
							paymentInvoiceDO.Amount 			= cursorDetail.getString(3);
							paymentInvoiceDO.totalAmt 			= paymentInvoiceDO.Amount;
							paymentInvoiceDO.PaymentType 		= cursorDetail.getString(4);
							paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
						} 
						while (cursorDetail.moveToNext());
					}
					
					if(cursorDetail != null && !cursorDetail.isClosed())
						cursorDetail.close();
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
			return paymentHeaderDO;
		}
	}
	
	public int getProductivePayments(boolean isTotalBill)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase =  null;
			Cursor cursor = null;
			int status = 0;
			try 
			{
				sqLiteDatabase =  DatabaseHelper.openDataBase();
				
				String strQuery = "";
				// Old
//				if(isTotalBill)
//				{
//					strQuery = "select Count(SiteId) from tblPaymentHeader";
//				}
//				else
//				{
//					strQuery = "select Count(DISTINCT SiteId) from tblPaymentHeader";
//				}
//				
				if(isTotalBill)
				{
					strQuery = "select Count(SiteId) from tblPaymentHeader where PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				}
				else
				{
					strQuery = "select Count(DISTINCT TH.SiteId) from tblPaymentHeader TH Inner Join   tblDailyJourneyPlan TC ON TC.ClientCode = TH.SiteId where PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				}
				
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					status = cursor.getInt(0);
					
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
			
			return status;
		}
	}
	
	/*public String getReceiptNo(String SalesmanCode, Preference preference)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String strReceiptNo = "";
			try 
			{
				objSqliteDB 	= 	DatabaseHelper.openDataBase();
				String query 	= 	"SELECT id from tblOfflineData where Type ='"+AppConstants.Receipt+"' AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
				cursor 			= 	objSqliteDB.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					strReceiptNo = cursor.getString(0);
					preference.saveStringInPreference(Preference.RECIEPT_NO, strReceiptNo);
					preference.commitPreference();
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
	
	public boolean updatePaymentStatus(JourneyPlanDO mallsDetails, ArrayList<PendingInvicesDO> arrInvoiceNumbers, String status, String receiptNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			boolean result =false;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPendingInvoices set Status = ?, BalanceAmount=? where InvoiceNumber =?");
				for(PendingInvicesDO objInvicesDO : arrInvoiceNumbers)
				{
					if(StringUtils.getFloat(objInvicesDO.lastbalance) == StringUtils.getFloat(objInvicesDO.balance) || objInvicesDO.INV_TYPE.equalsIgnoreCase(AppConstants.RETURN_INV))
					{
						stmtUpdateOrder.bindString(1, status);
						stmtUpdateOrder.bindString(2, "0");
						stmtUpdateOrder.bindString(3, objInvicesDO.invoiceNo);
						stmtUpdateOrder.execute();
					}
					else
					{
						float amount = StringUtils.getFloat(objInvicesDO.lastbalance)-StringUtils.getFloat(objInvicesDO.balance);
						stmtUpdateOrder.bindString(1, "N");
						stmtUpdateOrder.bindString(2, ""+amount);
						stmtUpdateOrder.bindString(3, objInvicesDO.invoiceNo);
						stmtUpdateOrder.execute();
					}
					
					if(objInvicesDO.INV_TYPE.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
						new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+(-StringUtils.getFloat(objInvicesDO.balance)), receiptNo, AppConstants.OPEN_CREDIT_CODE);
				}
				
				stmtUpdateOrder.close();
				result = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean insertOfflineData(Vector<NameIDDo> vecPaymentReceipts, String salesmanCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement insertStament = null, selectStament = null;
			String selectStmt 	= "SELECT COUNT(*) from tblOfflineData WHERE Id =?";
			String query 		= "INSERT INTO tblOfflineData (Id, SalesmanCode, Type, status) VALUES(?,?,?,?)";
			
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
						insertStament.bindString(2, salesmanCode);
						insertStament.bindString(3, nameIDDo.strType);
						insertStament.bindString(4, "0");
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
	
	//method to get the reason vector by reason type
	public ArrayList<PostPaymentDetailDO> getDetailByReceiptId(String strReceiptId)
	{
		ArrayList<PostPaymentDetailDO> arrInvicesDOs = new ArrayList<PostPaymentDetailDO>();
		String strQuery = "SELECT INVOICE_NUMBER, INVOICE_AMOUNT, COUPON_NO, INVOICE_TOTAL FROM tblPaymentDetail where  RECEIPT_NUMBER= '"+strReceiptId+"'";
			
		 SQLiteDatabase objSqliteDB =	null;
		 Cursor cursor 				= 	null;
		 try
		 {
			 objSqliteDB 	= DatabaseHelper.openDataBase();
			 cursor 		= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 PostPaymentDetailDO objInvicesDO	=  new PostPaymentDetailDO();
					 objInvicesDO.invoiceNumber 		=  cursor.getString(0);
					 objInvicesDO.invoiceAmount			=  cursor.getString(1);
					 objInvicesDO.CouponNo				=  cursor.getString(2);
					 objInvicesDO.totalAmount			=  cursor.getString(3);
					 arrInvicesDOs.add(objInvicesDO);
				 }
				 while(cursor.moveToNext());
			 } 
			 
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
		 }
		 return arrInvicesDOs;
	}

	public String getReceiptNo(String orderId)
	{
		 String strQuery = "SELECT PN.ReceiptId FROM tblPaymentHeader PN, tblPaymentInvoice PD WHERE PD.ReceiptID= PN.ReceiptId  AND TrxCode = '"+orderId+"' ORDER BY PaymentDate DESC LIMIT 1";
		 String receiptNo = "";
		 SQLiteDatabase objSqliteDB =	null;
		 Cursor cursor 				= 	null;
		 try
		 {
			 objSqliteDB 	= DatabaseHelper.openDataBase();
			 cursor 		= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 receiptNo = cursor.getString(0);
			 } 
			 
			 if(cursor != null && !cursor.isClosed())
				 cursor.close();
			 
		 }
		 catch(Exception e)
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
		 return receiptNo;
	}
	
	public boolean isPaymentDone(String orderId)
	{
		synchronized(MyApplication.MyLock)
		{
			boolean isPaymentDone = false;
			 String strQuery = "SELECT * from tblPaymentInvoice where TrxCode = '"+orderId+"'";
			 SQLiteDatabase objSqliteDB =	null;
			 Cursor cursor 				= 	null;
			 try
			 {
				 objSqliteDB 	= DatabaseHelper.openDataBase();
				 cursor 		= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 isPaymentDone = true;
				 } 
				 
				 if(cursor != null && !cursor.isClosed())
					 cursor.close();
				 
			 }
			 catch(Exception e)
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
			 return isPaymentDone;
		}
	}
}
