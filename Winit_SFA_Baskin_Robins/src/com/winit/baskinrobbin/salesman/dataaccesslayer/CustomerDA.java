package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.CustomerDao;
import com.winit.baskinrobbin.salesman.dataobject.CustomerVisitDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.SiteCreditLimitDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;


public class CustomerDA 
{
	public boolean insertCutomers(ArrayList<CustomerDao> arCustomerDao)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomer(id, Site, SiteName, CustomerId, CustomerStatus,CustAcctCreationDate," +
						"PartyName, ChannelCode,SubChannelCode,RegionCode,CountryCode,Category,Address1,Address2,Address3,Address4,PoNumber, City,PaymentType,PaymentTermCode," +
						"CreditLimit,GeoCodeX,GeoCodeY,PASSCODE,Email,ContactPersonName,PhoneNumber,AppCustomerId,MobileNumber1,MobileNumber2,Website,CustomerType," +
						"CreatedBy,ModifiedBy,Source,CustomerCategory,CustomerSubCategory,CustomerGroupCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomer WHERE id = ?");
				
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCustomer SET Site=?, SiteName=?, CustomerId=?, CustomerStatus=?,CustAcctCreationDate=?," +
						"PartyName=?, ChannelCode=?,SubChannelCode=?,RegionCode=?,CountryCode=?,Category=?,Address1=?,Address2=?,Address3=?,Address4=?,PoNumber=?, City=?,PaymentType=?,PaymentTermCode=?," +
						"CreditLimit=?,GeoCodeX=?,GeoCodeY=?,PASSCODE=?,Email=?,ContactPersonName=?,PhoneNumber=?,AppCustomerId=?,MobileNumber1=?,MobileNumber2=?,Website=?,CustomerType=?," +
						"CreatedBy=?,ModifiedBy=?,Source=?,CustomerCategory=?,CustomerSubCategory=?,CustomerGroupCode=?  WHERE id = ?");
				 
				for(int i=0;i<arCustomerDao.size();i++)
				{
					CustomerDao userJourneyPlan = arCustomerDao.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.id);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.site);
							stmtUpdate.bindString(2, userJourneyPlan.SiteName);
							stmtUpdate.bindString(3, userJourneyPlan.CustomerId);
							stmtUpdate.bindString(4, userJourneyPlan.CustomerStatus);
							stmtUpdate.bindString(5, userJourneyPlan.CustAcctCreationDate);
							stmtUpdate.bindString(6, userJourneyPlan.PartyName);
							stmtUpdate.bindString(7, userJourneyPlan.ChannelCode);
							stmtUpdate.bindString(8, userJourneyPlan.SubChannelCode);
							stmtUpdate.bindString(9, userJourneyPlan.RegionCode);
							stmtUpdate.bindString(10, userJourneyPlan.CountryCode);
							stmtUpdate.bindString(11, userJourneyPlan.Category);
							stmtUpdate.bindString(12, userJourneyPlan.Address1);
							stmtUpdate.bindString(13, userJourneyPlan.Address2);
							stmtUpdate.bindString(14, userJourneyPlan.Address3);
							stmtUpdate.bindString(15, userJourneyPlan.Address4);
							stmtUpdate.bindString(16, userJourneyPlan.PoNumber);
							stmtUpdate.bindString(17, userJourneyPlan.City);
							stmtUpdate.bindString(18, userJourneyPlan.PaymentType);
							stmtUpdate.bindString(19, userJourneyPlan.PaymentTermCode);
							stmtUpdate.bindString(20, userJourneyPlan.CreditLimit);
							stmtUpdate.bindString(21, userJourneyPlan.GeoCodeX);
							stmtUpdate.bindString(22, userJourneyPlan.GeoCodeY);
							stmtUpdate.bindString(23, userJourneyPlan.PASSCODE);
							stmtUpdate.bindString(24, userJourneyPlan.Email);
							stmtUpdate.bindString(25, userJourneyPlan.ContactPersonName);
							stmtUpdate.bindString(26, userJourneyPlan.PhoneNumber);
							stmtUpdate.bindString(27, userJourneyPlan.AppCustomerId);
							stmtUpdate.bindString(28, userJourneyPlan.MobileNumber1);
							stmtUpdate.bindString(29, userJourneyPlan.MobileNumber2);
							stmtUpdate.bindString(30, userJourneyPlan.Website);
							stmtUpdate.bindString(31, userJourneyPlan.CustomerType);
							stmtUpdate.bindString(32, userJourneyPlan.CreatedBy);
							stmtUpdate.bindString(33, userJourneyPlan.ModifiedBy);
							stmtUpdate.bindString(34, userJourneyPlan.Source);
							stmtUpdate.bindString(35, userJourneyPlan.CustomerCategory);
							stmtUpdate.bindString(36, userJourneyPlan.CustomerSubCategory);
							stmtUpdate.bindString(37, userJourneyPlan.CustomerGroupCode);
							stmtUpdate.bindLong(38, userJourneyPlan.id);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.id);
							stmtInsert.bindString(2, userJourneyPlan.site);
							stmtInsert.bindString(3, userJourneyPlan.SiteName);
							stmtInsert.bindString(4, userJourneyPlan.CustomerId);
							stmtInsert.bindString(5, userJourneyPlan.CustomerStatus);
							stmtInsert.bindString(6, userJourneyPlan.CustAcctCreationDate);
							stmtInsert.bindString(7, userJourneyPlan.PartyName);
							stmtInsert.bindString(8, userJourneyPlan.ChannelCode);
							stmtInsert.bindString(9, userJourneyPlan.SubChannelCode);
							stmtInsert.bindString(10, userJourneyPlan.RegionCode);
							stmtInsert.bindString(11, userJourneyPlan.CountryCode);
							stmtInsert.bindString(12, userJourneyPlan.Category);
							stmtInsert.bindString(13, userJourneyPlan.Address1);
							stmtInsert.bindString(14, userJourneyPlan.Address2);
							stmtInsert.bindString(15, userJourneyPlan.Address3);
							stmtInsert.bindString(16, userJourneyPlan.Address4);
							stmtInsert.bindString(17, userJourneyPlan.PoNumber);
							stmtInsert.bindString(18, userJourneyPlan.City);
							stmtInsert.bindString(19, userJourneyPlan.PaymentType);
							stmtInsert.bindString(20, userJourneyPlan.PaymentTermCode);
							stmtInsert.bindString(21, userJourneyPlan.CreditLimit);
							stmtInsert.bindString(22, userJourneyPlan.GeoCodeX);
							stmtInsert.bindString(23, userJourneyPlan.GeoCodeY);
							stmtInsert.bindString(24, userJourneyPlan.PASSCODE);
							stmtInsert.bindString(25, userJourneyPlan.Email);
							stmtInsert.bindString(26, userJourneyPlan.ContactPersonName);
							stmtInsert.bindString(27, userJourneyPlan.PhoneNumber);
							stmtInsert.bindString(28, userJourneyPlan.AppCustomerId);
							stmtInsert.bindString(29, userJourneyPlan.MobileNumber1);
							stmtInsert.bindString(30, userJourneyPlan.MobileNumber2);
							stmtInsert.bindString(31, userJourneyPlan.Website);
							stmtInsert.bindString(32, userJourneyPlan.CustomerType);
							stmtInsert.bindString(33, userJourneyPlan.CreatedBy);
							stmtInsert.bindString(34, userJourneyPlan.ModifiedBy);
							stmtInsert.bindString(35, userJourneyPlan.Source);
							stmtInsert.bindString(36, userJourneyPlan.CustomerCategory);
							stmtInsert.bindString(37, userJourneyPlan.CustomerSubCategory);
							stmtInsert.bindString(38, userJourneyPlan.CustomerGroupCode);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean insertCustomerVisits(CustomerVisitDO userJourneyPlan)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCustomerVisit(CustomerVisitId, UserCode, JourneyCode, VisitCode, ClientCode," +
						"Date, ArrivalTime,OutTime,TotalTimeInMins,Latitude,Longitude,CustomerVisitAppId,IsProductiveCall,TypeOfCall,VehicleCode,DriverCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerVisit WHERE CustomerVisitId = ?");
				
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCustomerVisit SET UserCode=?, JourneyCode=?, VisitCode=?, ClientCode=?," +
						"Date=?, ArrivalTime=?,OutTime=?,TotalTimeInMins=?,Latitude=?,Longitude=?,CustomerVisitAppId=?,IsProductiveCall=?,TypeOfCall=?  WHERE CustomerVisitId = ?");
				 
				SQLiteStatement stmtUpdateVisit = objSqliteDB.compileStatement("UPDATE tblDailyJourneyPlan SET VisitStatus = ? WHERE ClientCode =?");
				
				stmtSelectRec.bindString(1, userJourneyPlan.CustomerVisitId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					stmtUpdate.bindString(1, userJourneyPlan.UserCode);
					stmtUpdate.bindString(2, userJourneyPlan.JourneyCode);
					stmtUpdate.bindString(3, userJourneyPlan.VisitCode);
					stmtUpdate.bindString(4, userJourneyPlan.ClientCode);
					stmtUpdate.bindString(5, userJourneyPlan.Date);
					stmtUpdate.bindString(6, userJourneyPlan.ArrivalTime);
					stmtUpdate.bindString(7, userJourneyPlan.OutTime);
					stmtUpdate.bindString(8, userJourneyPlan.TotalTimeInMins);
					stmtUpdate.bindString(9, userJourneyPlan.Latitude);
					stmtUpdate.bindString(10, userJourneyPlan.Longitude);
					stmtUpdate.bindString(11, userJourneyPlan.CustomerVisitAppId);
					stmtUpdate.bindString(12, userJourneyPlan.IsProductiveCall);
					stmtUpdate.bindString(13, userJourneyPlan.TypeOfCall);
					stmtUpdate.bindString(14, userJourneyPlan.CustomerVisitId);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1, userJourneyPlan.CustomerVisitId);
					stmtInsert.bindString(2, userJourneyPlan.UserCode);
					stmtInsert.bindString(3, userJourneyPlan.JourneyCode);
					stmtInsert.bindString(4, userJourneyPlan.VisitCode);
					stmtInsert.bindString(5, userJourneyPlan.ClientCode);
					stmtInsert.bindString(6, userJourneyPlan.Date);
					stmtInsert.bindString(7, userJourneyPlan.ArrivalTime);
					stmtInsert.bindString(8, userJourneyPlan.OutTime);
					stmtInsert.bindString(9, userJourneyPlan.TotalTimeInMins);
					stmtInsert.bindString(10, userJourneyPlan.Latitude);
					stmtInsert.bindString(11, userJourneyPlan.Longitude);
					stmtInsert.bindString(12, userJourneyPlan.CustomerVisitAppId);
					stmtInsert.bindString(13, userJourneyPlan.IsProductiveCall);
					stmtInsert.bindString(14, userJourneyPlan.TypeOfCall);
					stmtInsert.bindString(15, userJourneyPlan.vehicleNo);
					stmtInsert.bindString(16, userJourneyPlan.UserId);
					stmtInsert.executeInsert();
				}
				
				stmtUpdateVisit.bindString(1, "1");
				stmtUpdateVisit.bindString(2, userJourneyPlan.ClientCode);
				stmtUpdateVisit.execute();
				
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				stmtUpdateVisit.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	public void updateCustomerCheckOutTime(String outTime)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
					cv.put("OutTime", outTime);
				objSqliteDB.update("tblCustomerVisit", cv, " IFNULL(OutTime,'') = ''", null);
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
	
	//Journey Call
	public void updateCustomerProductivity(String site, String date, String typeOfCall, String productive)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				cv.put("TypeOfCall", typeOfCall);
				cv.put("IsProductiveCall", productive);
				
				objSqliteDB.update("tblCustomerVisit", cv, "ClientCode = '"+site+"' AND Date LIKE '"+date+"%'", null);
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
	
	//Journey Call
	public boolean updateCustomerCreditLimit(String site, float Amt)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				Cursor cursor 						= 	null;
				objSqliteDB = DatabaseHelper.openDataBase();
				String strUpdate 					= 	"Update tblCustomer set CreditLimit=? where Site = ?";
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				
				String strQuery 	= 	"SELECT CreditLimit where Site = '"+site+"'";
				cursor 	= objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					float creditLimit 		= cursor.getFloat(0);
					creditLimit = creditLimit + Amt;
					
					stmtUpdateQty.bindString(1, ""+creditLimit);
					stmtUpdateQty.bindString(2, ""+site);
					stmtUpdateQty.execute();
				}
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
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		
		return true;
	}
	
	public void updateLastJourneyLog()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor nCursor = null, cursor = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ClientCode FROM tblCustomerVisit WHERE OutTime =''";
				 nCursor = objSqliteDB.rawQuery(suery, null);
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblCustomerVisit set Status=? ,OutTime = ?,TotalTimeInMins =? where ClientCode =? AND ArrivalTime =?");
				 
				 if(nCursor.moveToFirst())
				 {
					 do
					 {
						 cursor	 =	objSqliteDB.rawQuery("select ArrivalTime from tblCustomerVisit where ClientCode ='"+nCursor.getString(0)+"' AND OutTime =''", null);
						 if(cursor.moveToFirst())
						 {
							 String outDate = CalendarUtils.getCurrentDateTime();
							 stmtUpdate.bindString(1, "N");
							 stmtUpdate.bindString(2, outDate);
							 long minutes = CalendarUtils.getDateDifferenceInMinutes(cursor.getString(0), outDate);
							 stmtUpdate.bindString(3, ""+minutes);
							 
							 stmtUpdate.bindString(4, nCursor.getString(0));
							 stmtUpdate.bindString(5, cursor.getString(0));
							 stmtUpdate.execute();
						 }
					 }
					 while(nCursor.moveToNext());
				 }
				 
				 if(nCursor!=null && !nCursor.isClosed())
					 nCursor.close();
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
	
	public void updateCustomerVisitUploadStatus(boolean isUploaded, String CustomerVisitAppId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				if(isUploaded)
					cv.put("Status", "Y");
				else
					cv.put("Status", "N");
				objSqliteDB.update("tblCustomerVisit", cv, "CustomerVisitAppId = ?", new String[]{CustomerVisitAppId});
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
	
	public Vector<CustomerVisitDO>  getCustomerVisit()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 Vector<CustomerVisitDO> vecCustomerVisit = new Vector<CustomerVisitDO>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT * FROM tblCustomerVisit WHERE IFNULL(Status, 'N') = 'N' and IFNULL(OutTime, '') != ''";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 CustomerVisitDO  c = new CustomerVisitDO();
					 c.CustomerVisitId = cursor.getString(0);
					 c.UserCode = cursor.getString(1);
					 c.JourneyCode = cursor.getString(2);
					 c.VisitCode = cursor.getString(3);
					 c.ClientCode = cursor.getString(4);
					 c.Date = cursor.getString(5);
					 c.ArrivalTime = cursor.getString(6);
					 c.OutTime = cursor.getString(7);
					 c.TotalTimeInMins = ""+cursor.getInt(8);
					 c.Latitude = cursor.getString(9);
					 c.Longitude = cursor.getString(10);
					 c.CustomerVisitAppId = cursor.getString(11);
					 c.IsProductiveCall = ""+cursor.getInt(12);
					 c.TypeOfCall = cursor.getString(13);
					 c.vehicleNo = cursor.getString(16);
					 c.UserId = cursor.getString(17);
					 vecCustomerVisit.add(c);
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return vecCustomerVisit;
		}
	}
	
	public SurveyCustomerDeatislDO  getCustomerSurveyDetails(String Usercode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			SurveyCustomerDeatislDO surveyDo = new SurveyCustomerDeatislDO();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT ClientCode,VisitCode FROM tblCustomerVisit WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 surveyDo.clientCode = cursor.getString(0);
					 surveyDo.visitCode  = cursor.getString(1);
					
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return surveyDo;
		}
	}
	public HashMap<String, Double> getAllOnAccount(){
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;		
			 String site="",customerId="";
			 HashMap<String, Double> hmOnAccount=new HashMap<String, Double>();
			 double onAccountPayment =0;
			 try
			 {
					 objSqliteDB = DatabaseHelper.openDataBase();
				 
				 String query  = "Select TC.CustomerId,SUM(TPH.Amount) " +
				 		"from tblPaymentHeader TPH INNER JOIN tblPaymentDetail TPD ON TPD.ReceiptNo = TPH.ReceiptId" +
				 		" AND ((TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCash()+"' COLLATE NOCASE) OR (TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCheque()+"' COLLATE NOCASE " +
				 		"AND Date(TPD.ChequeDate)<=Date('2016-07-22'))) " +
				 		"INNER JOIN tblPaymentInvoice TPI ON TPI.ReceiptId = TPH.ReceiptId " +
				 		"AND TPI.TrxCode='"+PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT+"' " +
				 		"INNER JOIN tblCustomer TC ON TC.Site=TPH.SiteId " +
				 		"where  PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%' GROUP BY TPH.SiteId"; 
				//String query  = "Select Site, CustomerId from tblCustomer";
				 
				 cursor 		=  	objSqliteDB.rawQuery(query, null);
				 
				 if(cursor!=null && cursor.moveToFirst()){
					 do{
						 customerId = cursor.getString(0)+AppConstants.CREDIT_LEVEL_ACCOUNT;
						 onAccountPayment=cursor.getDouble(1);
						 if(!hmOnAccount.containsKey(customerId)){
							 hmOnAccount.put(customerId, onAccountPayment);
						 }
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
			 }
			 return hmOnAccount;
		}
	
	
	}
		public double getOnAccoutAmount(String site, SQLiteDatabase objSqliteDB){

		synchronized(MyApplication.MyLock) 
		{
			 Cursor  cursor = null;
			 HashMap<String, Double> hmOnAccount=new HashMap<String, Double>();
			 double onAccountPayment =0;
			 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
			 try
			 {
				 if(objSqliteDB == null || !objSqliteDB.isOpen())
					 objSqliteDB = DatabaseHelper.openDataBase();
				 
				 String paymentQuery  = "Select SUM(TPH.Amount) from tblPaymentHeader TPH "
					      + "INNER JOIN tblPaymentDetail TPD ON TPD.ReceiptNo = TPH.ReceiptId AND "
					      + "((TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCash()+"' COLLATE NOCASE) OR "
					      + "(TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCheque()+"' COLLATE NOCASE AND Date(TPD.ChequeDate)<=Date('"+CalendarUtils.getCurrentDateAsString()+"'))) "
					      + "INNER JOIN tblPaymentInvoice TPI ON TPI.ReceiptId = TPH.ReceiptId AND TPI.TrxCode='"+PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT+"' "
					      + "where SiteId ='"+site+"' AND  "
					      + "PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				 
				 cursor 		=  	objSqliteDB.rawQuery(paymentQuery, null);
				 
				 if(cursor!=null && cursor.moveToFirst()){
					 onAccountPayment =  cursor.getDouble(0);
						 
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
			 }
			 return onAccountPayment;
		}
	
	}
		public ArrayList<SiteCreditLimitDO> getCustomerIdCreditLimit(JourneyPlanDO journeyPlanDO){

			synchronized(MyApplication.MyLock) 
			{
				 SQLiteDatabase objSqliteDB = null;
				 Cursor  cursor = null;
				 ArrayList<SiteCreditLimitDO> arrSiteCredits=new  ArrayList<SiteCreditLimitDO>();
				 
				 SiteCreditLimitDO sitecreditlimit;
				 try
				 {
					 objSqliteDB 	= 	DatabaseHelper.openDataBase();
					 
					 String query="select site from vw_CustomerCreditLimit where CustomerId= '"+journeyPlanDO.customerId+"'";
					 cursor 		=  	objSqliteDB.rawQuery(query, null);
					 if(cursor.moveToNext()){
						  do{
							  String site = cursor.getString(0);
							  sitecreditlimit = getsiteCreditLimit(objSqliteDB,site,journeyPlanDO.customerId);
							  arrSiteCredits.add(sitecreditlimit);
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
					 if(objSqliteDB!=null)
						 objSqliteDB.close();
				 }
				 return arrSiteCredits;
			}
		
		}
	
		public SiteCreditLimitDO getsiteCreditLimit(SQLiteDatabase objSqliteDB,String site, String CustomerId){
				 Cursor  cursor = null;
				 double onAccountPayment =0;
				 SiteCreditLimitDO sitecreditlimit = new SiteCreditLimitDO();
				 try
				 {
					 if(objSqliteDB==null)
						 objSqliteDB 	= 	DatabaseHelper.openDataBase();
					 
					 String paymentQuery  = "Select SUM(TPH.Amount) from tblPaymentHeader TPH "
						      + "INNER JOIN tblPaymentDetail TPD ON TPD.ReceiptNo = TPH.ReceiptId AND "
						      + "((TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCash()+"' COLLATE NOCASE) OR "
						      + "(TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCheque()+"' COLLATE NOCASE AND Date(TPD.ChequeDate)<=Date('"+CalendarUtils.getCurrentDateAsString()+"'))) "
						      + "INNER JOIN tblPaymentInvoice TPI ON TPI.ReceiptId = TPH.ReceiptId AND TPI.TrxCode='"+PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT+"' "
						      + "where SiteId ='"+site+"' AND  "
						      + "PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
					 
					 cursor 		=  	objSqliteDB.rawQuery(paymentQuery, null);
					 
					 if(cursor!=null && cursor.moveToFirst()){
						 onAccountPayment =  cursor.getDouble(0);
					 }
					 if(cursor!=null && !cursor.isClosed())
						 cursor.close();
					 
					 String suery 	= 	"";
					 if(site.equalsIgnoreCase("0"))
						 suery = "SELECT BalanceAmount, CreditLimit, AvailableLimit  FROM vw_CustomerCreditLimit WHERE CustomerId = '"+CustomerId+"'";
					 else
						 suery = "SELECT BalanceAmount, CreditLimit, AvailableLimit  FROM vw_CustomerCreditLimit WHERE Site = '"+site+"'";

					 
					 cursor 		=  	objSqliteDB.rawQuery(suery, null);
					 sitecreditlimit.site=site;
					 sitecreditlimit.customerID	=CustomerId;
					 if(cursor.moveToNext())
					 {
						 sitecreditlimit.site_outStandingAmount = cursor.getDouble(0);
						 sitecreditlimit.site_creditLimit 		 = cursor.getDouble(1);
						 sitecreditlimit.site_availbleLimit     =  (cursor.getDouble(2)+onAccountPayment);
						 double outstandingAmount = sitecreditlimit.site_outStandingAmount-onAccountPayment;
						 if(outstandingAmount<=0)
							 outstandingAmount = 0;
						 sitecreditlimit.site_outStandingAmount =  outstandingAmount;
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
				 }
				 return sitecreditlimit;
		}
	public SiteCreditLimitDO getsiteCreditLimit(JourneyPlanDO journeyPlanDO){

		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 double onAccountPayment =0;
			 SiteCreditLimitDO sitecreditlimit=new SiteCreditLimitDO();
			 {
				 try{
				 objSqliteDB 	= 	DatabaseHelper.openDataBase();
				 
				 String paymentQuery  = "Select SUM(TPH.Amount) from tblPaymentHeader TPH "
					      + "INNER JOIN tblPaymentDetail TPD ON TPD.ReceiptNo = TPH.ReceiptId AND "
					      + "((TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCash()+"' COLLATE NOCASE) OR "
					      + "(TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCheque()+"' COLLATE NOCASE AND Date(TPD.ChequeDate)<=Date('"+CalendarUtils.getCurrentDateAsString()+"'))) "
					      + "INNER JOIN tblPaymentInvoice TPI ON TPI.ReceiptId = TPH.ReceiptId AND TPI.TrxCode='"+PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT+"' "
					      + "where SiteId ='"+journeyPlanDO.site+"' AND  "
					      + "PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				 
				 String suery 	= 	"";
				
					 suery = "SELECT BalanceAmount, CreditLimit, AvailableLimit FROM vw_CustomerCreditLimit WHERE Site = '"+journeyPlanDO.site+"'";
				 
				 cursor 		=  	objSqliteDB.rawQuery(paymentQuery, null);
				 
				 if(cursor!=null && cursor.moveToFirst()){
					 onAccountPayment =  cursor.getDouble(0);
				 }

				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 cursor 		=  	objSqliteDB.rawQuery(suery, null);
				 sitecreditlimit.site 			 = journeyPlanDO.site;
				 sitecreditlimit.customerID	=journeyPlanDO.customerId;
				 if(cursor.moveToNext())
				 {
					 sitecreditlimit.site_outStandingAmount = cursor.getDouble(0);
					 sitecreditlimit.site_creditLimit 		 = cursor.getDouble(1);
					 sitecreditlimit.site_availbleLimit     =  (cursor.getDouble(2)+onAccountPayment);
					 double outstandingAmount = sitecreditlimit.site_outStandingAmount-onAccountPayment;
					 if(outstandingAmount<=0)
						 outstandingAmount = 0;
					 sitecreditlimit.site_outStandingAmount = outstandingAmount;
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
			 return sitecreditlimit;
		}
	
	}
	public CustomerCreditLimitDo getCustomerCreditLimit(JourneyPlanDO journeyPlanDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 double onAccountPayment =0;
			 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
			 try
			 {
				 objSqliteDB 	= 	DatabaseHelper.openDataBase();
				 
				 String paymentQuery  = "Select SUM(TPH.Amount) from tblPaymentHeader TPH "
					      + "INNER JOIN tblPaymentDetail TPD ON TPD.ReceiptNo = TPH.ReceiptId AND "
					      + "((TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCash()+"' COLLATE NOCASE) OR "
					      + "(TPD.PaymentTypeCode = '"+PaymentDetailDO.getPaymentTypeCheque()+"' COLLATE NOCASE AND Date(TPD.ChequeDate)<=Date('"+CalendarUtils.getCurrentDateAsString()+"'))) "
					      + "INNER JOIN tblPaymentInvoice TPI ON TPI.ReceiptId = TPH.ReceiptId AND TPI.TrxCode='"+PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT+"' "
					      + "where SiteId ='"+journeyPlanDO.site+"' AND  "
					      + "PaymentDate like '"+CalendarUtils.getCurrentDateAsString()+"%'";
				 
				 String suery 	= 	"";
				 
				 if(journeyPlanDO.CREDIT_LEVEL != null && journeyPlanDO.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT))
					 suery = "SELECT * FROM vw_CustomerCreditLimit WHERE CustomerId = '"+journeyPlanDO.customerId+"'";
				 else
					 suery = "SELECT * FROM vw_CustomerCreditLimit WHERE Site = '"+journeyPlanDO.site+"'";
				 
				 cursor 		=  	objSqliteDB.rawQuery(paymentQuery, null);
				 
				 if(cursor!=null && cursor.moveToFirst()){
					 onAccountPayment =  cursor.getDouble(0);
				 }

				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 cursor 		=  	objSqliteDB.rawQuery(suery, null);
				 
				 if(cursor.moveToNext())
				 {
					 customerLimit.site 			 = cursor.getString(0);
					 customerLimit.customerID		 = cursor.getString(1);
					 customerLimit.outStandingAmount = ""+cursor.getFloat(2);
					 customerLimit.creditLimit 		 = ""+cursor.getFloat(3);
					 customerLimit.availbleLimit     = ""+(cursor.getFloat(4)+onAccountPayment);
					 double outstandingAmount = cursor.getFloat(2)-onAccountPayment;
					 if(outstandingAmount<=0)
						 outstandingAmount = 0;
					 customerLimit.outStandingAmount = ""+outstandingAmount;
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return customerLimit;
		}
	}
	
	public float getOverDueAmount(JourneyPlanDO journeyPlanDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 float overDue  = 0;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "";
				 
				 if(journeyPlanDO.CREDIT_LEVEL != null && journeyPlanDO.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT))
					 suery = "SELECT BalanceAmount FROM vm_OverDue WHERE CustomerId = '"+journeyPlanDO.customerId+"'";
				 else
					 suery = "SELECT BalanceAmount FROM vm_OverDue WHERE SiteId = '"+journeyPlanDO.site+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 
				 if(cursor.moveToFirst())
					overDue = cursor.getFloat(0);
				 
				 if(overDue < 0)
					 overDue = 0;
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return overDue;
		}
	}
	
	public boolean isOutStanding(String siteNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 boolean isOutStanding = false;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT IsOutStanding FROM tblPendingInvoices WHERE CustomerSiteId = '"+siteNo+"' LIMIT 1";
				 
//				 String suery = "SELECT CCL.Site, IFNULL(CCL.CREDIT_LIMIT, 0) + IFNULL(SUM(TPH.Amount) , 0) AS CREDIT_LIMIT, CCL.Outstanding, CCL.BalanceLimit FROM vw_CustomerCreditLimit CCL INNER JOIN tblPaymentHeader TPH ON TPH.SiteID = CCL.Site WHERE CCL.Site = '"+siteNo+"' AND TPH.PaymentType = '"+AppConstants.Payment_Type+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToNext())
				 {
					 isOutStanding = StringUtils.getBoolean(cursor.getString(0));
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return isOutStanding;
		}
	}

	public HashMap<String, CustomerCreditLimitDo> getCreditLimits()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 double onAccountAmt=0;
			 HashMap<String, CustomerCreditLimitDo> hmLimits = new HashMap<String, CustomerCreditLimitDo>();
			 try
			 {
				 
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT * FROM vw_CustomerCreditLimit";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 CustomerCreditLimitDo customerLimit = new CustomerCreditLimitDo();
						 customerLimit.site 			= 	cursor.getString(0);
						 customerLimit.customerID 		= 	cursor.getString(1);
						 customerLimit.outStandingAmount= 	cursor.getString(2);
						 customerLimit.creditLimit 		= 	cursor.getString(3);
						 
						 
						 customerLimit.availbleLimit	= 	""+cursor.getFloat(4);
						 
						
						 if(StringUtils.getFloat(customerLimit.site) <= 0)
							hmLimits.put(customerLimit.customerID+AppConstants.CREDIT_LEVEL_ACCOUNT, customerLimit);
						 else
							hmLimits.put(customerLimit.site+AppConstants.CREDIT_LEVEL_SITE, customerLimit);
					 }
					 while(cursor.moveToNext());
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmLimits;
		}
	}
	
	public HashMap<String, Float> getOverDueAmount()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 HashMap<String, Float> hmOverDue = new HashMap<String, Float>();
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT DISTINCT * FROM vm_OverDue";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 String SITE 		= cursor.getString(0);
						 String CUSTOMER_ID = cursor.getString(1);
						 float OVER_DUE     = cursor.getFloat(2);
						 
						 if(OVER_DUE < 0)
							 OVER_DUE = 0;
						 
						 if(SITE.equalsIgnoreCase("0"))
							 hmOverDue.put(CUSTOMER_ID + AppConstants.CREDIT_LEVEL_ACCOUNT, OVER_DUE);
						 else
							 hmOverDue.put(SITE + AppConstants.CREDIT_LEVEL_SITE, OVER_DUE);
					 }
					 while(cursor.moveToNext());
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return hmOverDue;
		}
	}
	
	public float getCustomerStoreGrowth(String customerID)
	{
		synchronized(MyApplication.MyLock) 
		{
			 float storeGrowth = 0;
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT StoreGrowth FROM tblCustomer WHERE CustomerId = '"+customerID+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 if(cursor.moveToNext())
				 {
					 storeGrowth = cursor.getFloat(0);
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return storeGrowth;
		}
	}

	public boolean insertLog(LogDO logDO) {
		synchronized(MyApplication.MyLock) 
		{ 
			boolean isFailed=false;
			SQLiteDatabase objSqliteDB = null;
			try {
					objSqliteDB = DatabaseHelper.openDataBase();
					SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblVehicleTracking(VehicleTrkingId,Type,Key,Data,USERCODE,DeviceDate,status) VALUES(?,?,?,?,?,?,?)");
					stmtInsert.bindString(1,  StringUtils.getUniqueUUID());
					stmtInsert.bindString(2, logDO.type);
					stmtInsert.bindString(3, logDO.key);
					stmtInsert.bindString(4, logDO.data);
					stmtInsert.bindString(5, logDO.userId);
					stmtInsert.bindString(6, logDO.deviceTime);
					stmtInsert.bindString(7, "N");
					stmtInsert.execute();
					stmtInsert.close();
		} catch (Exception e) {
			e.printStackTrace();
			isFailed=true;
		}
		 finally
		 {
			 if(objSqliteDB!=null)
				 objSqliteDB.close();
		 }	
			return isFailed;
		}
		
	}

//added for round of popup
	public String getCustomerCurrencyCode(String customerID)
	{
		synchronized(MyApplication.MyLock)
		{
			String CurrCode = "";
			SQLiteDatabase objSqliteDB = null;
			Cursor  cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String suery = "SELECT CountryCode FROM tblCustomer WHERE Site = '"+customerID+"'";

				cursor =  objSqliteDB.rawQuery(suery, null);
				if(cursor.moveToNext())
				{
					CurrCode = cursor.getString(0);
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
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return CurrCode;
		}
	}

	public int getCustomerCurrencyStatus(String countryId)
	{
		synchronized(MyApplication.MyLock)
		{
			int CurrCode = 0;
			SQLiteDatabase objSqliteDB = null;
			Cursor  cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String suery = "SELECT RoundOff FROM tblCountry WHERE CountryName = '"+countryId+"'";

				cursor =  objSqliteDB.rawQuery(suery, null);
				if(cursor.moveToNext())
				{
					 CurrCode = cursor.getInt(0);
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
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return CurrCode;
		}
	}

}
