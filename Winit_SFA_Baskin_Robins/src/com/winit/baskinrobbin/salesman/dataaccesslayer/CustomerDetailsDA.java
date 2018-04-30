package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.dataobject.CustomerDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerOrdersDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSite_NewDO;
import com.winit.baskinrobbin.salesman.dataobject.Customer_GroupDO;
import com.winit.baskinrobbin.salesman.dataobject.InsertCustoDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.MallsDetails;
import com.winit.baskinrobbin.salesman.dataobject.SyncLogDO;
import com.winit.baskinrobbin.salesman.dataobject.UnUploadedDataDO;
import com.winit.baskinrobbin.salesman.dataobject.UserJourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class CustomerDetailsDA
{
	OfflineDA offlineDA=new OfflineDA();
	public ArrayList<JourneyPlanDO> getJourneyPlanForTeleOrder(String site)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
			try 
			{
				String query = "";
				if(TextUtils.isEmpty(site))
					query = "SELECT * FROM tblCustomer where isActive=1 ORDER BY SiteName";
				else
					query = "SELECT * FROM tblCustomer WHERE Site = '"+site+"'";
				
				objSqliteDB = DatabaseHelper.openDataBase();
				c 			= objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.site =c.getString(1);
						j.siteName =c.getString(2);
						j.customerId =c.getString(3);
						j.custmerStatus =c.getString(4);
						j.custAccCreationDate =c.getString(5);
						j.partyName =c.getString(6);
						j.channelCode =c.getString(7);
						j.subChannelCode =c.getString(8);
						j.regionCode =c.getString(9);
						j.coutryCode =c.getString(10);
						j.category = c.getString(11);
						j.addresss1 =c.getString(12);
						j.addresss2 =c.getString(13);
						j.addresss3 =c.getString(14);
						j.addresss4 =c.getString(15);
					
						//As per the new logic.
						j.poNumber =c.getString(16);
						j.city =c.getString(17);
						j.paymentType =c.getString(18);
						j.paymentTermCode =c.getString(19);
						j.creditLimit =c.getString(20);
						j.geoCodeX =c.getString(21);
						j.geoCodeY =c.getString(22);
						j.Passcode =c.getString(23);
						j.email=c.getString(24);
						j.contectPersonName=c.getString(25);
						j.phoneNumber =c.getString(26);
						j.appCustomerId =c.getString(27);
						j.mobileNo1 =c.getString(28);
						j.mobileNo2 =c.getString(29);
						j.website =c.getString(30);
						j.customerType = c.getString(31);
						j.createdby = c.getString(32);
						j.modifiedBy = c.getString(33);
						j.source = c.getString(34);
						j.customerCategory = c.getString(35);
						j.customerSubCategory = c.getString(36);
						j.customerGroupCode = c.getString(37);
						j.modifiedDate = c.getString(38);
						j.modifiedTime = c.getString(39);
						j.currencyCode = c.getString(40);
						j.StoreGrowth = c.getString(41);
						j.priceList = c.getString(42);
						j.salesmanCode = c.getString(43);
						
						j.Order_Type_Id = c.getString(44);
						j.Order_Type_Name = c.getString(45);
						
						j.CREDIT_LEVEL = c.getString(46);
						j.salesmanName = c.getString(47);
						
						j.SalesPersonMobileNumber = c.getString(48);
						j.Max_Days_Past_Due = c.getInt(49);
						
						if(!TextUtils.isEmpty(c.getString(51))){
							j.IsOverCredit = c.getString(51).equalsIgnoreCase("Yes")?true:false;
						}
						j.VatNumber = c.getString(52);//added
						if(j.CREDIT_LEVEL != null 
							&& j.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT) 
							&& TextUtils.isEmpty(j.paymentTermCode))
						{
							j.paymentTermCode = getPaymentTermCode(objSqliteDB, j);
						}
						
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return arJourneyPlan;
		}
	}
	
	private String getPaymentTermCode(SQLiteDatabase objSqliteDB, JourneyPlanDO j) 
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor c = null;
			try 
			{
				String query = "SELECT PaymentTermCode FROM tblCustomer WHERE CustomerId = '"+j.customerId+"' AND PaymentTermCode !='' LIMIT 1";
					
				if(objSqliteDB == null || !objSqliteDB.isOpen())		
					objSqliteDB = DatabaseHelper.openDataBase();
				
				c  = objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
					j.paymentTermCode =c.getString(0);
				
				if(c != null && !c.isClosed())
					c.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
			}
			return j.paymentTermCode;
		}
	}

	public Vector<JourneyPlanDO> getCustomerDetailsForJourney(String strUserId)
	{
		synchronized(MyApplication.MyLock) 
		{
			JourneyPlanDO objCustomer;
			Vector<JourneyPlanDO>vectCustomer;
			DictionaryEntry[][] data=null;
			vectCustomer = new Vector<JourneyPlanDO>();
			String query="SELECT rowid, *FROM tblCustomer ORDER BY Site";
			
			try
			{
				data = DatabaseHelper.get(query);
				if(data !=null && data.length>0)
				{
					for(int i=0;i<data.length;i++)
					{
						 objCustomer 			    	= 	new JourneyPlanDO();
						 objCustomer.rowid				=	StringUtils.getInt(data[i][0].value.toString());
						 objCustomer.site		=	data[i][2].value.toString();
						 objCustomer.siteName			=	data[i][3].value.toString();
						 objCustomer.customerId			=	data[i][4].value.toString();
						 objCustomer.addresss1			=	""+data[i][13].value.toString();
						 
						 if(data[i][5].value != null)
							 objCustomer.addresss2		=	""+data[i][14].value.toString();
						 else
							 objCustomer.addresss2		=	"N/A";
						 
						 objCustomer.city				=	data[i][18].value.toString();
						 
						 if(data[0][7].value!=null)
							 objCustomer.geoCodeX		=	StringUtils.getFloat(data[0][22].value.toString())+"";
						 if(data[0][8].value!=null)
							 objCustomer.geoCodeY		=	StringUtils.getFloat(data[0][23].value.toString())+"";
						 
						 objCustomer.paymentType		=	data[i][19].value.toString();
						 objCustomer.paymentTermCode	=	data[i][20].value.toString();
						 objCustomer.email				=	data[i][25].value.toString();
//						 objCustomer.isSchemeAplicable	=	StringUtils.getInt(data[i][34].value.toString());
						 vectCustomer.add(objCustomer);
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
			
			 return vectCustomer;
		}
	}
	
	/*public boolean updateCreatedCustomers(Vector<InsertCustoDo> vecInsertCustoDos, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement updateCustomerRecord = objSqliteDB.compileStatement("update tblNewHouseHoldCustomer set isPushed ='1', CustomerId = ? where CustomerId =?");
				SQLiteStatement updateCustomerRecord_empty = objSqliteDB.compileStatement("update tblNewHouseHoldCustomer set CustomerId = ? where CustomerId =?");
				
				SQLiteStatement order = objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments = objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				for(InsertCustoDo insertCustoDo :vecInsertCustoDos)
				{
					if(!insertCustoDo.strNewSiteId.equalsIgnoreCase("-1"))
					{
						order.bindString(1, insertCustoDo.strNewSiteId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, insertCustoDo.strNewSiteId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord.bindString(1, insertCustoDo.strNewSiteId);
						updateCustomerRecord.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord.execute();
					}
					else
					{
						
						String strCustomerId = "";
						//Opening the database
						String query = "SELECT id from tblOfflineData where SalesmanCode ='"+strSalesCode+"' AND Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerId FROM tblNewHouseHoldCustomer) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							strCustomerId = cursor.getString(0);
						}
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
						
						order.bindString(1, strCustomerId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, strCustomerId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord_empty.bindString(1, strCustomerId);
						updateCustomerRecord_empty.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord_empty.execute();
					}
				}
				
				order.close();
				payments.close();
				updateCustomerRecord.close();
				
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
	}*/
	
	public boolean updateCreatedCustomersNew(Vector<InsertCustoDo> vecInsertCustoDos, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement updateCustomerRecord = objSqliteDB.compileStatement("update tblCustomerSites set source ='1', CustomerSiteId = ? where CustomerSiteId =?");
				SQLiteStatement updateCustomerRecord_empty = objSqliteDB.compileStatement("update tblCustomerSites set CustomerSiteId = ? where CustomerSiteId =?");
				
				SQLiteStatement order = objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments = objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				SQLiteStatement updateCustomerSub = objSqliteDB.compileStatement("update tblCustomerSitesSub set CustomerSiteId =? where CustomerSiteId =?");
				
				
				for(InsertCustoDo insertCustoDo :vecInsertCustoDos)
				{
					if(!insertCustoDo.strNewSiteId.equalsIgnoreCase("-1"))
					{
						order.bindString(1, insertCustoDo.strNewSiteId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, insertCustoDo.strNewSiteId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord.bindString(1, insertCustoDo.strNewSiteId);
						updateCustomerRecord.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord.execute();
					}
					else
					{
						
						//String strCustomerId = "";
						String strCustomerId = offlineDA.getNextSequenceNumber(OfflineDataType.CUSTOMER, strSalesCode,objSqliteDB);
						
						//Opening the database
						/*String query = "SELECT id from tblOfflineData where SalesmanCode ='"+strSalesCode+"' AND Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							strCustomerId = cursor.getString(0);
						}
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");*/
						
						order.bindString(1, strCustomerId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, strCustomerId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerSub.bindString(1, strCustomerId);
						updateCustomerSub.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerSub.execute();
						
						updateCustomerRecord_empty.bindString(1, strCustomerId);
						updateCustomerRecord_empty.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord_empty.execute();
						offlineDA.updateSequenceNumberStatus(strCustomerId);
					}
				}
				
				order.close();
				payments.close();
				updateCustomerRecord.close();
				updateCustomerSub.close();
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
	
	public boolean insertAllPendingInvoices(Vector<CustomerOrdersDO> vecCustomerPendingInvoices,String customerID,boolean isForFirst)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result =true;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try 
			{
				
				if((vecCustomerPendingInvoices== null || vecCustomerPendingInvoices.size() == 0) && TextUtils.isEmpty(customerID)){
					
				}else{
					
					objSqliteDB = DatabaseHelper.openDataBase();
					objSqliteDB.beginTransaction();
					String offlineSequenceNumbers = "";
					String offlinePaymentQuery = "SELECT distinct TrxCode from tblPaymentInvoice TPI INNER JOIN tblPaymentHeader TPH ON TPH.ReceiptId = TPI.ReceiptId AND TPH.Status='0'";
					String offlineOrderQuery   = "SELECT distinct OrderId from tblOrderHeader Where Status='0'";
					SQLiteStatement stmtInsertPendingIn 	= objSqliteDB.compileStatement("INSERT INTO tblPendingInvoices(OrderId , PresellerId,CustomerSiteId,TotalAmount, DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,IsOutStanding,DocType,DueDate,ReferenceDocument,ERPReference) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");	
					//	SQLiteStatement stmtInsertPendingIn 	= objSqliteDB.compileStatement("INSERT INTO tblPendingInvoices(OrderId , PresellerId,CustomerSiteId,TotalAmount, DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,IsOutStanding,DocType,DueDate,ReferenceDocument,OrgCode,ProfitCentre) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					
					cursor =  objSqliteDB.rawQuery(offlinePaymentQuery, null);
					
					if(cursor.moveToFirst())
					{
						do
						{
							offlineSequenceNumbers = offlineSequenceNumbers+"'"+cursor.getString(0)+"'"+",";
						}
						while(cursor.moveToNext());
					}
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
					
					cursor =  objSqliteDB.rawQuery(offlineOrderQuery, null);
					
					if(cursor.moveToFirst())
					{
						do
						{
							offlineSequenceNumbers = offlineSequenceNumbers+"'"+cursor.getString(0)+"'"+",";
						}
						while(cursor.moveToNext());
					}
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
					
					if(isForFirst){
						
						if(!TextUtils.isEmpty(offlineSequenceNumbers))
						{
							offlineSequenceNumbers = offlineSequenceNumbers.substring(0,offlineSequenceNumbers.length()-1);
							if(TextUtils.isEmpty(customerID))
								objSqliteDB.execSQL("Delete from tblPendingInvoices Where InvoiceNumber NOT IN ("+offlineSequenceNumbers+")");//added by anil, suggested by venky sir
							else
								objSqliteDB.execSQL("Delete from tblPendingInvoices Where CustomerSiteId IN(SELECT Site FROM tblcustomer where CustomerId='"+customerID+"')");
							//objSqliteDB.execSQL("Delete from tblPendingInvoices Where CustomerSiteId='"+customerID+"' AND InvoiceNumber NOT IN ("+offlineSequenceNumbers+")");//added by anil, suggested by venky sir
						}
						else
						{
							if(TextUtils.isEmpty(customerID))
								objSqliteDB.execSQL("Delete from tblPendingInvoices");//added by anil, suggested by venky sir
							else
								objSqliteDB.execSQL("Delete from tblPendingInvoices Where CustomerSiteId IN(SELECT Site FROM tblcustomer where CustomerId='"+customerID+"')");
							//objSqliteDB.execSQL("Delete from tblPendingInvoices Where CustomerSiteId='"+customerID+"'");//added by anil, suggested by venky sir
							
						}
						
					}
					if(vecCustomerPendingInvoices != null && vecCustomerPendingInvoices.size() > 0)
					{
						
						for(int index = 0 ; index < vecCustomerPendingInvoices.size() ; index++)
						{
							CustomerOrdersDO objCustomerOrders = vecCustomerPendingInvoices.get(index);
//						stmtSelectPendingIn.bindString(1, objCustomerOrders.invoiceNumber);
							/*long countRecord = stmtSelectPendingIn.simpleQueryForLong();
						if(countRecord != 0)
						{
							stmtUpdatePendingIn.bindString(1, objCustomerOrders.salesManCode);
							stmtUpdatePendingIn.bindString(2, objCustomerOrders.siteNumber);
							stmtUpdatePendingIn.bindString(3, objCustomerOrders.invoiceAmount);
							stmtUpdatePendingIn.bindString(4, objCustomerOrders.invoiceDate);
							stmtUpdatePendingIn.bindString(5, objCustomerOrders.balanceAmount);
							stmtUpdatePendingIn.bindString(6, objCustomerOrders.orderId);
							stmtUpdatePendingIn.bindString(7, objCustomerOrders.invoiceDate);
							stmtUpdatePendingIn.bindString(8, objCustomerOrders.IsOutStanding);
							stmtUpdatePendingIn.bindString(9, objCustomerOrders.Doc_Type);
							stmtUpdatePendingIn.bindString(10, objCustomerOrders.Due_Date);
							stmtUpdatePendingIn.bindString(11, objCustomerOrders.Reference_Document);
							stmtUpdatePendingIn.bindString(12, objCustomerOrders.invoiceNumber);
							stmtUpdatePendingIn.execute();
						}
						else*/
							if(TextUtils.isEmpty(offlineSequenceNumbers) || !offlineSequenceNumbers.contains(objCustomerOrders.invoiceNumber))
							{
								stmtInsertPendingIn.bindString(1,  objCustomerOrders.orderId);
								stmtInsertPendingIn.bindString(2,  objCustomerOrders.salesManCode);
								stmtInsertPendingIn.bindString(3,  objCustomerOrders.siteNumber);
								stmtInsertPendingIn.bindString(4,  objCustomerOrders.invoiceAmount);
								stmtInsertPendingIn.bindString(5,  objCustomerOrders.invoiceDate);
								stmtInsertPendingIn.bindString(6,  objCustomerOrders.balanceAmount);
								stmtInsertPendingIn.bindString(7,  objCustomerOrders.invoiceNumber);
								stmtInsertPendingIn.bindString(8,  objCustomerOrders.invoiceDate);
								stmtInsertPendingIn.bindString(9,  objCustomerOrders.IsOutStanding);
								stmtInsertPendingIn.bindString(10, objCustomerOrders.Doc_Type);
								stmtInsertPendingIn.bindString(11, objCustomerOrders.Due_Date);
								stmtInsertPendingIn.bindString(12, objCustomerOrders.Reference_Document);
								stmtInsertPendingIn.bindString(13, objCustomerOrders.ebs_ref_no);
								stmtInsertPendingIn.executeInsert();
							}
						}
					}
					objSqliteDB.setTransactionSuccessful();
					stmtInsertPendingIn.close();
				}
				result = true;
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
				
				if(objSqliteDB!=null && objSqliteDB.isOpen())
				{
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
	public boolean insertAllCustomerPendingInvoices(Vector<CustomerOrdersDO> vecCustomerPendingInvoices,String customerCode,
			boolean isForFirst)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result =true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
//				SQLiteStatement stmtSelectPendingIn		= objSqliteDB.compileStatement("SELECT COUNT(*) from tblPendingInvoices WHERE InvoiceNumber = ?");
				SQLiteStatement stmtInsertPendingIn 	= objSqliteDB.compileStatement("INSERT INTO tblPendingInvoices(OrderId , PresellerId,CustomerSiteId,TotalAmount, DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,IsOutStanding,DocType,DueDate,ReferenceDocument,ERPReference) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtDeletePendingIn 	= null;
//				SQLiteStatement stmtUpdatePendingIn 	= objSqliteDB.compileStatement("UPDATE tblPendingInvoices SET PresellerId =?,CustomerSiteId=?,TotalAmount=?,DeliveryDate=?,BalanceAmount=?,OrderId=?,InvoiceDate=?,IsOutStanding =?,DocType=?,DueDate=?,ReferenceDocument=?, ERPReference = ? WHERE InvoiceNumber = ?");
				 
				if(isForFirst){
					
					if(TextUtils.isEmpty(customerCode)){
						stmtDeletePendingIn = objSqliteDB.compileStatement("DELETE FROM tblPendingInvoices");
					}else{
						stmtDeletePendingIn = objSqliteDB.compileStatement("DELETE FROM tblPendingInvoices WHERE CustomerSiteId='"+customerCode+"'");
					}
				}
				
				stmtDeletePendingIn.executeUpdateDelete();
				if(vecCustomerPendingInvoices != null && vecCustomerPendingInvoices.size() > 0)
				{
					for(int index = 0 ; index < vecCustomerPendingInvoices.size() ; index++)
					{
						CustomerOrdersDO objCustomerOrders = vecCustomerPendingInvoices.get(index);
//						stmtSelectPendingIn.bindString(1, objCustomerOrders.invoiceNumber);
//						long countRecord = stmtSelectPendingIn.simpleQueryForLong();
//						if(countRecord != 0)
//						{
//							if(isToupdate)
//							{
//								stmtUpdatePendingIn.bindString(1, objCustomerOrders.salesManCode);
//								stmtUpdatePendingIn.bindString(2, objCustomerOrders.siteNumber);
//								stmtUpdatePendingIn.bindString(3, objCustomerOrders.invoiceAmount);
//								stmtUpdatePendingIn.bindString(4, objCustomerOrders.invoiceDate);
//								stmtUpdatePendingIn.bindString(5, objCustomerOrders.balanceAmount);
//								stmtUpdatePendingIn.bindString(6, objCustomerOrders.orderId);
//								stmtUpdatePendingIn.bindString(7, objCustomerOrders.invoiceDate);
//								stmtUpdatePendingIn.bindString(8, objCustomerOrders.IsOutStanding);
//								stmtUpdatePendingIn.bindString(9, objCustomerOrders.Doc_Type);
//								stmtUpdatePendingIn.bindString(10, objCustomerOrders.Due_Date);
//								stmtUpdatePendingIn.bindString(11, objCustomerOrders.Reference_Document);
//								stmtUpdatePendingIn.bindString(12, objCustomerOrders.ebs_ref_no);
//								stmtUpdatePendingIn.bindString(13, objCustomerOrders.invoiceNumber);
////								stmtUpdatePendingIn.execute();
////							}
//						}
//						else
//						if(stmtUpdatePendingIn.executeUpdateDelete()<=0)
						{
							stmtInsertPendingIn.bindString(1,  objCustomerOrders.orderId);
							stmtInsertPendingIn.bindString(2,  objCustomerOrders.salesManCode);
							stmtInsertPendingIn.bindString(3,  objCustomerOrders.siteNumber);
							stmtInsertPendingIn.bindString(4,  objCustomerOrders.invoiceAmount);
							stmtInsertPendingIn.bindString(5,  objCustomerOrders.invoiceDate);
							stmtInsertPendingIn.bindString(6,  objCustomerOrders.balanceAmount);
							stmtInsertPendingIn.bindString(7,  objCustomerOrders.invoiceNumber);
							stmtInsertPendingIn.bindString(8,  objCustomerOrders.invoiceDate);
							stmtInsertPendingIn.bindString(9,  objCustomerOrders.IsOutStanding);
							stmtInsertPendingIn.bindString(10, objCustomerOrders.Doc_Type);
							stmtInsertPendingIn.bindString(11, objCustomerOrders.Due_Date);
							stmtInsertPendingIn.bindString(12, objCustomerOrders.Reference_Document);
							stmtInsertPendingIn.bindString(13, objCustomerOrders.ebs_ref_no);
							stmtInsertPendingIn.executeInsert();
						}
					}
				}
				
//				stmtSelectPendingIn.close();
				stmtInsertPendingIn.close();
//				stmtUpdatePendingIn.close();
				if(stmtDeletePendingIn!=null){
					stmtDeletePendingIn.close();
				}
				
				result = true;
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
			return result;
		}
	}
	
	public void deletePendingInvoices()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			LogUtils.errorLog("deleting","tblPendingInvoices");
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblPendingInvoices");
				
				LogUtils.errorLog("deleted","tblPendingInvoices");
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
	
	public boolean deleteCustomers(Vector<SyncLogDO> vecSyncLogDO)
	{
		boolean isDeleted = false;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement delete = objSqliteDB.compileStatement("DELETE  FROM tblCustomerSites WHERE CustomerSiteId = ?");
				if(vecSyncLogDO != null && vecSyncLogDO.size() > 0)
				{
					for(SyncLogDO syncLogDO : vecSyncLogDO)
					{
						LogUtils.errorLog("syncLogDO - ", syncLogDO.entityId);
						delete.bindString(1, syncLogDO.entityId);
						delete.execute();
					}
				}
				isDeleted = true;
				delete.close();
			}
			catch (Exception e) 
			{
				isDeleted = false;
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return isDeleted;
		}
	}
	
	public int getTotalStock(String strUserId,String DateOfJourney)
	{
		synchronized(MyApplication.MyLock) 
		{
			int stock=0;
			DictionaryEntry[][] data=null;
			String query = "SELECT sum(Cases) FROM tblJourneyPlan where PresellerId='"+strUserId+"' AND (DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan(DateOfJourney)+"%' or DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan2(DateOfJourney)+"%') and CustomerSiteId in (select CustomerSiteId from tblInvoiceOrders ) ORDER BY Stop";
			try
			{
				data = DatabaseHelper.get(query);
				if(data !=null && data.length>0)
					stock =	(int)StringUtils.getFloat(data[0][0].value.toString());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
			return stock;
		}
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
				 String suery = "SELECT CustomerSiteId FROM tblJourneyLog WHERE OutTime =''";
				 nCursor = objSqliteDB.rawQuery(suery, null);
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set IsServed=? ,OutTime = ?,TotalTimeAtOutLet =? where CustomerSiteId =? and DateOfJourney like ? ");
				 
				 if(nCursor.moveToFirst())
				 {
					 do
					 {
						 cursor	 =	objSqliteDB.rawQuery("select ArrivalTime from tblJourneyLog where CustomerSiteId ='"+nCursor.getString(0)+"'", null);
						 if(cursor.moveToFirst())
						 {
							 String outDate = CalendarUtils.getCurrentDateAsString()+"T"+CalendarUtils.getRetrunTime()+":00";
							 stmtUpdate.bindString(1, "true");
							 stmtUpdate.bindString(2, outDate);
							 long minutes = CalendarUtils.getDateDifferenceInMinutes(cursor.getString(0), outDate);
							 stmtUpdate.bindString(3, ""+minutes);
							 
							 stmtUpdate.bindString(4, nCursor.getString(0));
							 stmtUpdate.bindString(5, "%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%");
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
	public void insertIntoJourneyLog(JourneyPlanDO objMallsDetails)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase database =null;
			 try
			 {
				 DictionaryEntry[][] data=null;
				 data = DatabaseHelper.get("SELECT * FROM tblJourneyLog where CustomerSiteId='"+objMallsDetails.site+"' and DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%'");
				 if(data ==null || data.length==0)
				 {
					 database = DatabaseHelper.openDataBase();
					 if(database!=null)
					 {
						 SQLiteStatement stmt = database.compileStatement("INSERT INTO tblJourneyLog (DateOfJourney, PresellerId, CustomerSiteId,CustomerPasscode,Stop,Distance,TravelTime,ArrivalTime,SeviceTime,ReasonForSkip,isServed,isPosted,OutTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
						 stmt.bindString(1,  ""+objMallsDetails.dateOfJourny);
						 stmt.bindString(2,  ""+objMallsDetails.userID);
						 stmt.bindString(3,  ""+objMallsDetails.site);
						 stmt.bindString(4,  ""+objMallsDetails.Passcode);
						 stmt.bindString(5,  ""+objMallsDetails.stop);
						 stmt.bindString(6,  ""+objMallsDetails.Distance);
						 stmt.bindString(7,  ""+objMallsDetails.TravelTime);
						 stmt.bindString(8 , ""+objMallsDetails.ActualArrivalTime);
						 stmt.bindString(9,  ""+objMallsDetails.SeviceTime);
						 stmt.bindString(10, ""+objMallsDetails.reasonForSkip);
						 stmt.bindString(11, ""+objMallsDetails.isServed);
						 stmt.bindString(12, "N");
						 stmt.bindString(13, "");
						 stmt.executeInsert();
						 stmt.close();
					 }
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(database != null)
					 database.close();
			 }
		}
		 
	}
	
	public boolean insertCustomerGroup(Vector<Customer_GroupDO> vectCustomerGroupDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result= true;
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerGroup WHERE CUSTOMER_SITE_ID =?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerGroup (COMPANYID, CUSTPRICECLASS, CUSTOMER_SITE_ID) VALUES(?,?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomerGroup SET " +
						"COMPANYID =? , CUSTPRICECLASS =? WHERE CUSTOMER_SITE_ID =?");
				for(int i=0;i<vectCustomerGroupDOs.size();i++)
				{
					Customer_GroupDO objCustomerGroupDO = vectCustomerGroupDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerGroupDO.SiteNumber);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerGroupDO != null )
						{
							stmtUpdate.bindString(1, objCustomerGroupDO.CompanyId);
							stmtUpdate.bindString(2, objCustomerGroupDO.CustpriceClass);
							stmtUpdate.bindString(3, objCustomerGroupDO.SiteNumber);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerGroupDO != null )
						{
							stmtInsert.bindString(1, objCustomerGroupDO.CompanyId);
							stmtInsert.bindString(2, objCustomerGroupDO.CustpriceClass);
							stmtInsert.bindString(3, objCustomerGroupDO.SiteNumber);
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
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean insertCustomer(Vector<CustomerDO> vecCustomerDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomers WHERE CustomerId = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomers(CustomerId, Name) VALUES(?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomers SET " +
						"CustomerId =? , Name =? WHERE CustomerId = ?");
				for(int i= 0 ;i<vecCustomerDOs.size();i++)
				{
					CustomerDO objCustomerDO =  vecCustomerDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerDO.Customer_Id);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerDO != null )
						{
							stmtUpdate.bindString(1, objCustomerDO.Customer_Id);
							stmtUpdate.bindString(2, objCustomerDO.customerName);
							stmtUpdate.bindString(3, objCustomerDO.Customer_Id);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerDO != null )
						{
							stmtInsert.bindString(1, objCustomerDO.Customer_Id);
							stmtInsert.bindString(2, objCustomerDO.customerName);
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
	
	
	public boolean insertCustomerInforWithSync(Vector<JourneyPlanDO> vecCustomerDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
//				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomer WHERE Site = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomer(" +
						"Site,SiteName,CustomerId,CustomerStatus,CustAcctCreationDate,PartyName," +
						"ChannelCode,SubChannelCode,RegionCode,CountryCode,Category,Address1," +
						"Address2,Address3,	Address4,PoNumber,City,	PaymentType,PaymentTermCode,CreditLimit," +
						" GeoCodeX,	GeoCodeY, PASSCODE,	Email, ContactPersonName, PhoneNumber," +
						" AppCustomerId, MobileNumber1,MobileNumber2, Website, CustomerType, CreatedBy," +
						" ModifiedBy, Source, CustomerCategory,	CustomerSubCategory, CustomerGroupCode," +
						" ModifiedDate,	ModifiedTime, CurrencyCode, StoreGrowth, PriceList, id,SalesPerson, Order_Type_Name,Order_Type_Id, CREDIT_LEVEL, SalesPersonName,"+
						" SalesPersonMobileNumber, Max_Days_Past_Due,IsOverCredit,VatNumber,IsActive," +
						"SiteNameInArabic, Address1_AR, Address2_AR, Address3_AR,AreaName_AR,PostalCode_AR,LocationName_AR,City_AR) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomer SET " +
						"CustomerId = ?,SiteName = ?,CustomerStatus = ?,CustAcctCreationDate = ?,PartyName = ?," +
						"ChannelCode = ?,SubChannelCode = ?,RegionCode = ?,CountryCode = ?,Category = ?,Address1 = ?," +
						"Address2 = ?,Address3 = ?,	Address4 = ?,PoNumber = ?,City = ?,	PaymentType = ?,PaymentTermCode = ?,CreditLimit = ?," +
						" GeoCodeX = ?,	GeoCodeY = ?,	PASSCODE = ?,	Email = ?,	ContactPersonName = ?,	PhoneNumber = ?," +
						" AppCustomerId = ?,	MobileNumber1 = ?,MobileNumber2 = ?,	Website = ?,	CustomerType = ?,	CreatedBy = ?," +
						" ModifiedBy = ?,	Source = ?,	CustomerCategory = ?,	CustomerSubCategory = ?,	CustomerGroupCode = ?," +
						" ModifiedDate = ?,	ModifiedTime = ?, CurrencyCode = ?, StoreGrowth = ?,PriceList=?, SalesPerson=?, Order_Type_Name=?,Order_Type_Id=?, CREDIT_LEVEL=?,SalesPersonName=?, " +
						" SalesPersonMobileNumber = ?, Max_Days_Past_Due = ?,IsOverCredit=?,VatNumber=?,IsActive=?, " +
						"SiteNameInArabic=?, Address1_AR=?, Address2_AR=?, Address3_AR=?,AreaName_AR=?,PostalCode_AR=?,LocationName_AR=?,City_AR=? WHERE Site = ?");
				
				for(int i= 0 ;i<vecCustomerDOs.size();i++)
				{
					JourneyPlanDO objCustomerDO =  vecCustomerDOs.get(i);
//					stmtSelectRec.bindString(1, objCustomerDO.site);
//					
//					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
						if(objCustomerDO != null )
						{
							stmtUpdate.bindString(1, ""+objCustomerDO.customerId);
							stmtUpdate.bindString(2, ""+objCustomerDO.siteName);
							stmtUpdate.bindString(3, ""+objCustomerDO.custmerStatus);
							stmtUpdate.bindString(4, ""+objCustomerDO.custAccCreationDate);
							stmtUpdate.bindString(5, ""+objCustomerDO.partyName);
							stmtUpdate.bindString(6, ""+objCustomerDO.channelCode);
							stmtUpdate.bindString(7, ""+objCustomerDO.subChannelCode);
							stmtUpdate.bindString(8, ""+objCustomerDO.regionCode);
							stmtUpdate.bindString(9, ""+objCustomerDO.coutryCode);
							stmtUpdate.bindString(10, ""+objCustomerDO.category);
							stmtUpdate.bindString(11, ""+objCustomerDO.addresss1);
							stmtUpdate.bindString(12, ""+objCustomerDO.addresss2);
							stmtUpdate.bindString(13, ""+objCustomerDO.addresss3);
							stmtUpdate.bindString(14, ""+objCustomerDO.addresss4);
							stmtUpdate.bindString(15, "");
							stmtUpdate.bindString(16, ""+objCustomerDO.city);
							stmtUpdate.bindString(17, ""+objCustomerDO.paymentType);
							stmtUpdate.bindString(18, ""+objCustomerDO.paymentTermCode);
							stmtUpdate.bindString(19, ""+objCustomerDO.creditLimit);
							stmtUpdate.bindString(20, ""+objCustomerDO.geoCodeX);
							stmtUpdate.bindString(21, ""+objCustomerDO.geoCodeY);
							stmtUpdate.bindString(22, ""+objCustomerDO.Passcode);
							stmtUpdate.bindString(23, ""+objCustomerDO.email);
							stmtUpdate.bindString(24, ""+objCustomerDO.contectPersonName);
							stmtUpdate.bindString(25, ""+objCustomerDO.phoneNumber);
							stmtUpdate.bindString(26, ""+objCustomerDO.appCustomerId);
							stmtUpdate.bindString(27, ""+objCustomerDO.mobileNo1);
							stmtUpdate.bindString(28, ""+objCustomerDO.mobileNo2);
							stmtUpdate.bindString(29, ""+objCustomerDO.website);
							stmtUpdate.bindString(30, ""+objCustomerDO.customerType);
							stmtUpdate.bindString(31, ""+objCustomerDO.createdby);
							stmtUpdate.bindString(32, ""+objCustomerDO.modifiedBy);
							stmtUpdate.bindString(33, ""+objCustomerDO.source);
							stmtUpdate.bindString(34, ""+objCustomerDO.customerCategory);
							stmtUpdate.bindString(35, ""+objCustomerDO.customerSubCategory);
							stmtUpdate.bindString(36, ""+objCustomerDO.customerGroupCode);
							stmtUpdate.bindString(37, ""+objCustomerDO.modifiedDate);
							stmtUpdate.bindString(38, ""+objCustomerDO.modifiedTime);
							stmtUpdate.bindString(39, ""+objCustomerDO.currencyCode);
							stmtUpdate.bindString(40, ""+objCustomerDO.StoreGrowth);
							stmtUpdate.bindString(41, ""+objCustomerDO.priceList);
							stmtUpdate.bindString(42, ""+objCustomerDO.salesmanCode);
							stmtUpdate.bindString(43, ""+objCustomerDO.Order_Type_Name);
							stmtUpdate.bindString(44, ""+objCustomerDO.Order_Type_Id);
							stmtUpdate.bindString(45, ""+objCustomerDO.CREDIT_LEVEL);
							stmtUpdate.bindString(46, ""+objCustomerDO.salesmanName);
							stmtUpdate.bindString(47, ""+objCustomerDO.SalesPersonMobileNumber);
							stmtUpdate.bindString(48, ""+objCustomerDO.Max_Days_Past_Due);
							stmtUpdate.bindString(49, ""+objCustomerDO.IsOverCredit);
							stmtUpdate.bindString(50, ""+objCustomerDO.VatNumber);
							stmtUpdate.bindString(51, ""+objCustomerDO.isActive);
							stmtUpdate.bindString(52, ""+objCustomerDO.SiteNameInArabic);
							stmtUpdate.bindString(53, ""+objCustomerDO.Address1_AR);
							stmtUpdate.bindString(54, ""+objCustomerDO.Address2_AR);
							stmtUpdate.bindString(55, ""+objCustomerDO.Address3_AR);
							stmtUpdate.bindString(56, ""+objCustomerDO.AreaName_AR);
							stmtUpdate.bindString(57, ""+objCustomerDO.PostalCode_AR);
							stmtUpdate.bindString(58, ""+objCustomerDO.LocationName_AR);
							stmtUpdate.bindString(59, ""+objCustomerDO.City_AR);
							stmtUpdate.bindString(60, ""+objCustomerDO.site);
//							stmtUpdate.execute();
							if(stmtUpdate.executeUpdateDelete() <=0)
							{
								stmtInsert.bindString(1, ""+objCustomerDO.site);
								stmtInsert.bindString(2, ""+objCustomerDO.siteName);
								stmtInsert.bindString(3, ""+objCustomerDO.customerId);
								stmtInsert.bindString(4, ""+objCustomerDO.custmerStatus);
								stmtInsert.bindString(5, ""+objCustomerDO.custAccCreationDate);
								stmtInsert.bindString(6, ""+objCustomerDO.partyName);
								stmtInsert.bindString(7, ""+objCustomerDO.channelCode);
								stmtInsert.bindString(8, ""+objCustomerDO.subChannelCode);
								stmtInsert.bindString(9, ""+objCustomerDO.regionCode);
								stmtInsert.bindString(10, ""+objCustomerDO.coutryCode);
								stmtInsert.bindString(11, ""+objCustomerDO.category);
								stmtInsert.bindString(12, ""+objCustomerDO.addresss1);
								stmtInsert.bindString(13, ""+objCustomerDO.addresss2);
								stmtInsert.bindString(14, ""+objCustomerDO.addresss3);
								stmtInsert.bindString(15, ""+objCustomerDO.addresss4);
								stmtInsert.bindString(16, "");
								stmtInsert.bindString(17, ""+objCustomerDO.city);
								stmtInsert.bindString(18, ""+objCustomerDO.paymentType);
								stmtInsert.bindString(19, ""+objCustomerDO.paymentTermCode);
								stmtInsert.bindString(20, ""+objCustomerDO.creditLimit);
								stmtInsert.bindString(21, ""+objCustomerDO.geoCodeX);
								stmtInsert.bindString(22, ""+objCustomerDO.geoCodeY);
								stmtInsert.bindString(23, ""+objCustomerDO.Passcode);
								stmtInsert.bindString(24, ""+objCustomerDO.email);
								stmtInsert.bindString(25, ""+objCustomerDO.contectPersonName);
								stmtInsert.bindString(26, ""+objCustomerDO.phoneNumber);
								stmtInsert.bindString(27, ""+objCustomerDO.appCustomerId);
								stmtInsert.bindString(28, ""+objCustomerDO.mobileNo1);
								stmtInsert.bindString(29, ""+objCustomerDO.mobileNo2);
								stmtInsert.bindString(30, ""+objCustomerDO.website);
								stmtInsert.bindString(31, ""+objCustomerDO.customerType);
								stmtInsert.bindString(32, ""+objCustomerDO.createdby);
								stmtInsert.bindString(33, ""+objCustomerDO.modifiedBy);
								stmtInsert.bindString(34, ""+objCustomerDO.source);
								stmtInsert.bindString(35, ""+objCustomerDO.customerCategory);
								stmtInsert.bindString(36, ""+objCustomerDO.customerSubCategory);
								stmtInsert.bindString(37, ""+objCustomerDO.customerGroupCode);
								stmtInsert.bindString(38, ""+objCustomerDO.modifiedDate);
								stmtInsert.bindString(39, ""+objCustomerDO.modifiedTime);
								stmtInsert.bindString(40, ""+objCustomerDO.currencyCode);
								stmtInsert.bindString(41, ""+objCustomerDO.StoreGrowth);
								stmtInsert.bindString(42, ""+objCustomerDO.priceList);
								stmtInsert.bindString(43, ""+objCustomerDO.site);
								stmtInsert.bindString(44, ""+objCustomerDO.salesmanCode);
								
								stmtInsert.bindString(45, ""+objCustomerDO.Order_Type_Name);
								stmtInsert.bindString(46, ""+objCustomerDO.Order_Type_Id);
								stmtInsert.bindString(47, ""+objCustomerDO.CREDIT_LEVEL);
								stmtInsert.bindString(48, ""+objCustomerDO.salesmanName);
								stmtInsert.bindString(49, ""+objCustomerDO.SalesPersonMobileNumber);
								stmtInsert.bindString(50, ""+objCustomerDO.Max_Days_Past_Due);
								stmtInsert.bindString(51, ""+objCustomerDO.IsOverCredit);
								stmtInsert.bindString(52, ""+objCustomerDO.VatNumber);
								stmtInsert.bindString(53, ""+objCustomerDO.isActive);
								stmtInsert.bindString(54, ""+objCustomerDO.SiteNameInArabic);
								stmtInsert.bindString(55, ""+objCustomerDO.Address1_AR);
								stmtInsert.bindString(56, ""+objCustomerDO.Address2_AR);
								stmtInsert.bindString(57, ""+objCustomerDO.Address3_AR);
								stmtInsert.bindString(58, ""+objCustomerDO.AreaName_AR);
								stmtInsert.bindString(59, ""+objCustomerDO.PostalCode_AR);
								stmtInsert.bindString(60, ""+objCustomerDO.LocationName_AR);
								stmtInsert.bindString(61, ""+objCustomerDO.City_AR);
								stmtInsert.executeInsert();
							}
						}
//					}
//					else
//					{
//						if(objCustomerDO != null )
//						{
//							
//						}
//					}
				}
				
//				stmtSelectRec.close();
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
	
	public boolean insertCustomerSite(Vector<CustomerSite_NewDO> vecCustomerSiteNewDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			boolean result= true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSites WHERE CustomerSiteId = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
						"CountryId,DOB,AnniversaryDate,ParentGroup,CustomerPostingGroup,CustomerCategory,CustomerSubCategory," +
						"CustomerGroupCode, IsSchemeApplicable) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomerSites SET " +
						"CustomerId =?, SiteName =?,ADDRESS1 =?, ADDRESS2 =?,CITY =?,GEO_CODE_X =?, GEO_CODE_Y=?,CREDIT_LIMIT =?," +
						"PresellerId =?, PAYMENT_TYPE =?,PAYMENT_TERM_CODE =?,PAYMENT_TERM_DESCRIPTION =?,TotalOutstandingBalance=? ," +
						"SubChannelCode =?,CustomerStatus=? ,MOBILENO1=?,MOBILENO2=?,Website=?,CustomerGrade=?,CustomerType=?," +
						"LandmarkId=?,SalesmanlandmarkId=?,Source=?,BlaseCustId=?,CountryId=?,DOB=?,AnniversaryDate=?," +
						"ParentGroup=?,CustomerPostingGroup=?,CustomerCategory=?,CustomerSubCategory=?,CustomerGroupCode=?, IsSchemeApplicable = ? WHERE CustomerSiteId = ?");
				 
				for(int i=0;i<vecCustomerSiteNewDOs.size();i++)
				{
					CustomerSite_NewDO objCustomerSite_NewDO=vecCustomerSiteNewDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerSite_NewDO.CustomerSiteId);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerSite_NewDO != null )
						{
							
							stmtUpdate.bindString(1, objCustomerSite_NewDO.CustomerId);
							stmtUpdate.bindString(2, objCustomerSite_NewDO.SiteName);
							stmtUpdate.bindString(3, objCustomerSite_NewDO.Address1);
							stmtUpdate.bindString(4, objCustomerSite_NewDO.Address2);
							stmtUpdate.bindString(5, objCustomerSite_NewDO.City);
							stmtUpdate.bindString(6, objCustomerSite_NewDO.Latitude);
							stmtUpdate.bindString(7, objCustomerSite_NewDO.Longitude);
							stmtUpdate.bindString(8, objCustomerSite_NewDO.CreditLimit);
							stmtUpdate.bindString(9, objCustomerSite_NewDO.Salesman);
							stmtUpdate.bindString(10, objCustomerSite_NewDO.PaymentType);
							stmtUpdate.bindString(11, objCustomerSite_NewDO.PaymentTermCode);
							stmtUpdate.bindString(12, objCustomerSite_NewDO.PaymentTermDescription);
							stmtUpdate.bindString(13, objCustomerSite_NewDO.TotalOutstandingBalance);
							stmtUpdate.bindString(14, objCustomerSite_NewDO.SubChannel);
							stmtUpdate.bindString(15, objCustomerSite_NewDO.CustomerStatus);
							stmtUpdate.bindString(16, objCustomerSite_NewDO.MOBILENO1);
							stmtUpdate.bindString(17, objCustomerSite_NewDO.MOBILENO2);
							stmtUpdate.bindString(18, objCustomerSite_NewDO.Website);
							stmtUpdate.bindString(19, objCustomerSite_NewDO.CustomerGrade);
							stmtUpdate.bindString(20, objCustomerSite_NewDO.CustomerType);
							stmtUpdate.bindString(21, objCustomerSite_NewDO.LandmarkId);
							stmtUpdate.bindString(22, objCustomerSite_NewDO.SalesmanlandmarkId);
							stmtUpdate.bindString(23, objCustomerSite_NewDO.Source);
							stmtUpdate.bindString(24, objCustomerSite_NewDO.BlaseCustId);
							stmtUpdate.bindString(25, objCustomerSite_NewDO.CountryId);
							stmtUpdate.bindString(26, objCustomerSite_NewDO.DOB);
							stmtUpdate.bindString(27, objCustomerSite_NewDO.AnniversaryDate);
							
							stmtUpdate.bindString(28, objCustomerSite_NewDO.ParentGroup);
							stmtUpdate.bindString(29, objCustomerSite_NewDO.CustomerPostingGroup);
							stmtUpdate.bindString(30, objCustomerSite_NewDO.CustomerCategory);
							stmtUpdate.bindString(31, objCustomerSite_NewDO.CustomerSubCategory);
							stmtUpdate.bindString(32, objCustomerSite_NewDO.CustomerGroupCode);
							stmtUpdate.bindString(33, objCustomerSite_NewDO.isSchemeApplicable);
							
							stmtUpdate.bindString(34, objCustomerSite_NewDO.CustomerSiteId);
							
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerSite_NewDO != null )
						{
							stmtInsert.bindString(1, objCustomerSite_NewDO.CustomerSiteId);
							stmtInsert.bindString(2, objCustomerSite_NewDO.CustomerId);
							stmtInsert.bindString(3, objCustomerSite_NewDO.SiteName);
							stmtInsert.bindString(4, objCustomerSite_NewDO.Address1);
							stmtInsert.bindString(5, objCustomerSite_NewDO.Address2);
							stmtInsert.bindString(6, objCustomerSite_NewDO.City);
							stmtInsert.bindString(7, objCustomerSite_NewDO.Latitude);
							stmtInsert.bindString(8, objCustomerSite_NewDO.Longitude);
							stmtInsert.bindString(9, objCustomerSite_NewDO.CreditLimit);
							stmtInsert.bindString(10, objCustomerSite_NewDO.Salesman);
							stmtInsert.bindString(11, objCustomerSite_NewDO.PaymentType);
							stmtInsert.bindString(12, objCustomerSite_NewDO.PaymentTermCode);
							stmtInsert.bindString(13, objCustomerSite_NewDO.PaymentTermDescription);
							stmtInsert.bindString(14, objCustomerSite_NewDO.TotalOutstandingBalance !=null ? objCustomerSite_NewDO.TotalOutstandingBalance : "0");
							stmtInsert.bindString(15, objCustomerSite_NewDO.SubChannel);
							stmtInsert.bindString(16, objCustomerSite_NewDO.CustomerStatus);
							stmtInsert.bindString(17, objCustomerSite_NewDO.MOBILENO1);
							stmtInsert.bindString(18, objCustomerSite_NewDO.MOBILENO2);
							stmtInsert.bindString(19, objCustomerSite_NewDO.Website);
							stmtInsert.bindString(20, objCustomerSite_NewDO.CustomerGrade);
							stmtInsert.bindString(21, objCustomerSite_NewDO.CustomerType);
							stmtInsert.bindString(22, objCustomerSite_NewDO.LandmarkId);
							stmtInsert.bindString(23, objCustomerSite_NewDO.SalesmanlandmarkId);
							stmtInsert.bindString(24, objCustomerSite_NewDO.Source);
							stmtInsert.bindString(25, objCustomerSite_NewDO.BlaseCustId);
							stmtInsert.bindString(26, objCustomerSite_NewDO.CountryId );
							stmtInsert.bindString(27, objCustomerSite_NewDO.DOB);
							stmtInsert.bindString(28, objCustomerSite_NewDO.AnniversaryDate);
							
							
							stmtInsert.bindString(29, objCustomerSite_NewDO.ParentGroup);
							stmtInsert.bindString(30, objCustomerSite_NewDO.CustomerPostingGroup);
							stmtInsert.bindString(31, objCustomerSite_NewDO.CustomerCategory);
							stmtInsert.bindString(32, objCustomerSite_NewDO.CustomerSubCategory);
							stmtInsert.bindString(33, objCustomerSite_NewDO.CustomerGroupCode);
							stmtInsert.bindString(34, objCustomerSite_NewDO.isSchemeApplicable);
							
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
	
	public boolean updateCustomerSiteGEOLocation(JourneyPlanDO mallsDetails)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			boolean result= true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomer SET " +
						"GeoCodeX =?, GeoCodeY=? WHERE Site = ?");
				 
				if(mallsDetails!=null)
				{
					stmtUpdate.bindString(1, ""+mallsDetails.geoCodeX);
					stmtUpdate.bindString(2, ""+mallsDetails.geoCodeY);
					stmtUpdate.bindString(3, mallsDetails.site);
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
	
	public ArrayList<String> getServedCustomerList(String salesmanCode, Preference preference)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<String>vectCustomer 		= 	new ArrayList<String>();
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query		=	"SELECT DISTINCT ClientCode FROM tblCustomerVisit WHERE (Date LIKE '%"+CalendarUtils.getDateAsString(new Date())+"%')";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						vectCustomer.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
				{
					query				=	"SELECT distinct SiteNo from tblOrderHeader where OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'";
					cursor 				= 	sqLiteDatabase .rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do
						{
							if(!vectCustomer.contains(cursor.getString(0)))
								vectCustomer.add(cursor.getString(0));
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
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return vectCustomer;
		}
	}
	
	public ArrayList<String> getOrderTobePost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<String>vectCustomer 		= 	new ArrayList<String>();
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query					=	"SELECT distinct SiteNo from tblOrderHeader where  EmpNo ='"+empNo+"'" +
													" And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' AND status<=0";
				cursor 						    = 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						if(!vectCustomer.contains(cursor.getString(0)))
							vectCustomer.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				query					=	"SELECT SiteId FROM tblPaymentHeader where status='0' and PaymentDate like '"+CalendarUtils.getOrderPostDate()+"%'";
				cursor 					= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						if(!vectCustomer.contains(cursor.getString(0)))
							vectCustomer.add(cursor.getString(0));
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
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return vectCustomer;
		}
	}
	
	public boolean insertJourneyPlan(Vector<UserJourneyPlanDO> vecJourneyPlanDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblJourneyPlan WHERE PresellerId = ? and DateOfJourney = ? and CustomerSiteId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourneyPlan(DateOfJourney,PresellerId,CustomerSiteId,CustomerPasscode,Stop,Distance,TravelTime,ArrivalTime,ServiceTime,Cases,KG) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourneyPlan SET CustomerPasscode = ?,Stop = ?,Distance = ?,TravelTime = ?,ArrivalTime = ?,ServiceTime = ?,Cases = ?,KG = ? WHERE CustomerSiteId = ?  and DateOfJourney = ? and PresellerId = ?");
				 
				for(int i=0;i<vecJourneyPlanDOs.size();i++)
				{
					UserJourneyPlanDO userJourneyPlan = vecJourneyPlanDOs.get(i);
					stmtSelectRec.bindString(1, userJourneyPlan.strSalesmancode);
					stmtSelectRec.bindString(2, userJourneyPlan.strRoutePlanDetails);
					stmtSelectRec.bindString(3, userJourneyPlan.strSiteNumber);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.strPassCode);
							stmtUpdate.bindString(2, userJourneyPlan.strStop);
							stmtUpdate.bindString(3, userJourneyPlan.strDistance);
							stmtUpdate.bindString(4, userJourneyPlan.strTravelTime);
							stmtUpdate.bindString(5, userJourneyPlan.strArrivalTime);
							stmtUpdate.bindString(6, userJourneyPlan.strServiceTime);
							stmtUpdate.bindString(7, userJourneyPlan.strCases);
							stmtUpdate.bindString(8, userJourneyPlan.strKG);
							stmtUpdate.bindString(9, userJourneyPlan.strSiteNumber);
							stmtUpdate.bindString(10, userJourneyPlan.strRoutePlanDetails);
							stmtUpdate.bindString(11, userJourneyPlan.strSalesmancode);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindString(1, userJourneyPlan.strRoutePlanDetails);
							stmtInsert.bindString(2, userJourneyPlan.strSalesmancode);
							stmtInsert.bindString(3, userJourneyPlan.strSiteNumber);
							stmtInsert.bindString(4, userJourneyPlan.strPassCode);
							stmtInsert.bindString(5, userJourneyPlan.strStop);
							stmtInsert.bindString(6, userJourneyPlan.strDistance);
							stmtInsert.bindString(7, userJourneyPlan.strTravelTime);
							stmtInsert.bindString(8, userJourneyPlan.strArrivalTime);
							stmtInsert.bindString(9, userJourneyPlan.strServiceTime);
							stmtInsert.bindString(10, userJourneyPlan.strCases);
							stmtInsert.bindString(11, userJourneyPlan.strKG);
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
	
	public ArrayList<JourneyPlanDO> getJourneyPlan(long todayTimeStamp, int date, String day, String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				c = objSqliteDB.rawQuery("SELECT DISTINCT RC.Sequence as Sequence,'0' as lifeCycle, C.*, RC.EndTime as TimeOut,RC.StartTime as TimeIn,C.Site as ClientCode FROM tblCustomer C " +
						                 "INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode WHERE IsDeleted !='true' Order by Sequence", null);
				ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.stop =c.getInt(0);
						j.lifeCycle =c.getString(1);
						j.id =c.getString(2);
						j.site =c.getString(3);
						j.siteName =c.getString(4);
						j.customerId =c.getString(5);
						j.custmerStatus =c.getString(6);
						j.custAccCreationDate =c.getString(7);
						j.partyName =c.getString(8);
						j.channelCode =c.getString(9);
						j.subChannelCode =c.getString(10);
						j.regionCode =c.getString(11);
						j.coutryCode =c.getString(12);
						j.category = c.getString(13);
						j.addresss1 =c.getString(14);
						j.addresss2 =c.getString(15);
						j.addresss3 =c.getString(16);
						j.addresss4 =c.getString(17);
						
						//As per the new logic.
						j.poNumber =c.getString(18);
						j.city =c.getString(19);
						j.paymentType =c.getString(20);
						j.paymentTermCode =c.getString(21);
						j.creditLimit =c.getString(22);
						j.geoCodeX =c.getString(23);
						j.geoCodeY =c.getString(24);
						j.Passcode =c.getString(25);
						j.email=c.getString(26);
						j.contectPersonName=c.getString(27);
						j.phoneNumber =c.getString(28);
						j.appCustomerId =c.getString(29);
						j.mobileNo1 =c.getString(30);
						j.mobileNo2 =c.getString(31);
						j.website =c.getString(32);
						j.customerType = c.getString(33);
						j.createdby = c.getString(34);
						j.modifiedBy = c.getString(35);
						j.source = c.getString(36);
						j.customerCategory = c.getString(37);
						j.customerSubCategory = c.getString(38);
						j.customerGroupCode = c.getString(39);
						j.modifiedDate = c.getString(40);
						j.modifiedTime = c.getString(41);
						j.currencyCode = c.getString(42);
						j.StoreGrowth = c.getString(43);
						j.priceList = c.getString(44);
						j.salesmanCode = c.getString(45);
						
						j.Order_Type_Id = c.getString(46);
						j.Order_Type_Name = c.getString(47);
						j.CREDIT_LEVEL = c.getString(48);
						j.salesmanName = c.getString(49);
						
						j.SalesPersonMobileNumber = c.getString(50);
						j.Max_Days_Past_Due = c.getInt(51);
						
						//Other columns
						j.timeOut = c.getString(52);
						j.timeIn = c.getString(53);
						j.clientCode = c.getString(54);
						
						j.userID = presellerId;
						j.dateOfJourny = day;
						
						if(j.CREDIT_LEVEL != null 
								&& j.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT) 
								&& TextUtils.isEmpty(j.paymentTermCode))
							{
								j.paymentTermCode = getPaymentTermCode(objSqliteDB, j);
							}
						
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
				return arJourneyPlan;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		return null;
	}
	
	public int getJourneyPlanCount(long todayTimeStamp, int date, String day, String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			int count = 0;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				Cursor c = objSqliteDB.rawQuery("SELECT COUNT(*) FROM tblDailyJourneyPlan WHERE IsDeleted !='true'", null);
				
				if(c.moveToFirst())
				{
					count = c.getInt(0);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return count;
		}
	}
	
	public int getServedCustomerCount(String strSelectedDate,String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			long countRec =0;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT count(*) FROM tblCustomerVisit where Date Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(strSelectedDate)+"%'");
				countRec 						= 	stmtSelectRec.simpleQueryForLong();
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
			return (int) countRec;
		}
	}
	
	//need to change
		public int getServedCustomerCount(int stopNumber ,String Day,String PresellerId, int Date, long DateInMS, String currentDate)
		{
			synchronized(MyApplication.MyLock) 
			{
				int stopNo=stopNumber;
				SQLiteDatabase mDatabase = null;
				String query = "SELECT count(DISTINCT ClientCode) FROM tblCustomerVisit where Date like '%"+CalendarUtils.getCurrentDateAsString()+"%'";
				Cursor cursor  = null,cursor2 = null;
				try
				{
					synchronized(MyApplication.MyLock) 
			    	{
						mDatabase = DatabaseHelper.openDataBase();
						cursor  = mDatabase.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							stopNo =stopNo-StringUtils.getInt(cursor.getString(0));
							
							if(stopNo>1)
							{
								String queryCustomers = "SELECT C.SiteName, C.Site as ClientCode FROM tblCustomer C INNER JOIN  tblDailyJourneyPlan RC " +
				                "ON C.Site = RC.ClientCode WHERE VisitStatus = 0 AND Sequence < "+stopNumber+" AND IsDeleted !='true' AND C.Site NOT IN(SELECT  CustomerSiteId FROM tblSkipReasons) Order by Sequence";
								
								cursor2  = mDatabase.rawQuery(queryCustomers, null);
								
								if(cursor2.moveToFirst())
								{
									int i=1;
									AppConstants.skippedCustomerSitIds = new ArrayList<String>();
									do
									{
										AppConstants.SKIPPED_CUSTOMERS =AppConstants.SKIPPED_CUSTOMERS+ i+") "+cursor2.getString(0)+"\n";
										AppConstants.skippedCustomerSitIds.add(cursor2.getString(1));
										i++;
									}while(cursor2.moveToNext());
									if(cursor2!=null && !cursor2.isClosed())
										cursor2.close();
								}
							}
							if(cursor!=null && !cursor.isClosed())
								cursor.close();	
						}
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
					if(cursor2!=null && !cursor2.isClosed())
						cursor2.close();
					if(mDatabase!=null)
						mDatabase.close();
				}
				return stopNo;
			}
		}

	public void updateCheckOutTimeByService(String empId,String date, String siteId, String checkOut)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase database =null;
			 try
			 {
				 database 					= DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = database.compileStatement("Update tblCheckinout set checkout=? WHERE empNo = ? AND  siteId =? AND date like '"+date+"%' AND checkout=?");
				 
				 stmtUpdate.bindString(1, checkOut);
				 stmtUpdate.bindString(2, empId);
				 stmtUpdate.bindString(3, siteId);
				 stmtUpdate.bindString(4, "");
				 stmtUpdate.execute();
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(database != null)
					 database.close();
			 }
		}
	}
	public void updateJourneyLog(String strCustomerSiteId,long totaltime)
	{
		synchronized(MyApplication.MyLock) 
		{
			 LogUtils.errorLog("totaltime","totaltime" +totaltime);
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 int timeInMinute = 0;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set IsServed=? ,OutTime = ?,TotalTimeAtOutLet =? where CustomerSiteId =? and DateOfJourney like ? ");
				 cursor						=	objSqliteDB.rawQuery("select TotalTimeAtOutLet from tblJourneyLog where CustomerSiteId ='"+strCustomerSiteId+"'", null);
				 stmtUpdate.bindString(1, "true");
				 stmtUpdate.bindString(2, CalendarUtils.getCurrentDateAsString()+"T"+CalendarUtils.getRetrunTime()+":00");
				 if(cursor.moveToFirst())
				 {
					 if(totaltime>0)
						 timeInMinute = (int)Math.ceil(StringUtils.getFloat(cursor.getString(0))+(float)((CalendarUtils.getCurrentTimeInMilli()-totaltime)/(float)(60000)));
					 else
						 timeInMinute = (int)Math.ceil(StringUtils.getFloat(cursor.getString(0)));
					 if(timeInMinute>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else if((int)Math.ceil(StringUtils.getFloat(cursor.getString(0)))>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else
						 stmtUpdate.bindString(3, "0");

				 }
				 else
				 {
					 timeInMinute = (int)Math.ceil(((CalendarUtils.getCurrentTimeInMilli()-totaltime)/(60*1000)));
					 if(timeInMinute>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else
						 stmtUpdate.bindString(3, "0"); 

				 }
					 
				 stmtUpdate.bindString(4, strCustomerSiteId);
				 stmtUpdate.bindString(5, "%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%");
				 stmtUpdate.execute();
					 
				 stmtUpdate.close();
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
	
	
	public void updateJourneyLogStatus()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set isPosted=?");
				 stmtUpdate.bindString(1, "Y");
				 stmtUpdate.execute();
				 
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
	
	public ArrayList<MallsDetails> getJournyLog(String presellerId,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<MallsDetails> arrayList = new ArrayList<MallsDetails>();
			SQLiteDatabase database = null;
			String strQuery  = "";
			strQuery 		= "SELECT CustomerSiteId,ArrivalTime,OutTime,TotalTimeAtOutLet FROM tblJourneyLog where DateOfJourney Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(date)+"%' And PresellerId ='"+presellerId+"' AND OutTime !='' AND isPosted ='N'";
			Cursor cursor 			= null;
			try 
			{
				database = DatabaseHelper.openDataBase();
				cursor = database.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						MallsDetails objDetails 	=  	new MallsDetails();
						objDetails.customerSiteId 	= 	cursor.getString(0);
						objDetails.ActualArrivalTime= 	cursor.getString(1);
						objDetails.ActualOutTime 	= 	cursor.getString(2);
						objDetails.TotalTime	 	= 	cursor.getString(3);
						arrayList.add(objDetails);
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
				
				if(database != null)
					database.close();
			}
			return arrayList;
		}
	}
	
	public Vector<UnUploadedDataDO> getJournyLogUnUpload(String presellerId,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> uploadedDataDOs = new Vector<UnUploadedDataDO>();
			SQLiteDatabase database = null;
			String strQuery  = "";
			strQuery 		= "SELECT CustomerSiteId, isPosted FROM tblJourneyLog where DateOfJourney Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(date)+"%' And PresellerId ='"+presellerId+"' AND OutTime !=''";
			Cursor cursor 			= null;
			try 
			{
				database = DatabaseHelper.openDataBase();
				cursor = database.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						UnUploadedDataDO unUploadedDataDO 	=  	new UnUploadedDataDO();
						unUploadedDataDO.strId 	= 	cursor.getString(0);
						if(cursor.getString(1).equalsIgnoreCase("N"))
							unUploadedDataDO.status = 0;
						else
							unUploadedDataDO.status = 1;
						
						uploadedDataDOs.add(unUploadedDataDO);
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
				
				if(database != null)
					database.close();
			}
			return uploadedDataDOs;
		}
	}
	
	public void insertCurrentInvoice(String strCustomerSIteID,String BalanceAmount,String InvoiceNumber, String DOC_TYPE)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement sqlStatement = null;
			SQLiteStatement sqlSelectStatement = null;
			SQLiteStatement sqlUpdateStatement = null;
			
			String query 		= "INSERT INTO tblPendingInvoices(CustomerSiteId,DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,OrderId,TotalAmount, DocType,TRANS_TYPE_NAME) VALUES(?,?,?,?,?,?,?,?,?)";
			String querySelect 	= "Select count(*) from tblPendingInvoices where InvoiceNumber =?";
			String queryUpdate 	= "Update tblPendingInvoices set BalanceAmount =?,TotalAmount=?,CustomerSiteId=?  where InvoiceNumber =?";
			try
			{
				
				sqLiteDatabase 	=  DatabaseHelper.openDataBase();
				
				sqlSelectStatement = sqLiteDatabase.compileStatement(querySelect);
				sqlSelectStatement.bindString(1,InvoiceNumber);
				long countRecTruck = sqlSelectStatement.simpleQueryForLong();
				
				if(countRecTruck ==0)
				{
					sqlStatement 			=	sqLiteDatabase.compileStatement(query);
					sqlStatement.bindString(1, strCustomerSIteID);
					sqlStatement.bindString(2,	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00");
					sqlStatement.bindString(3, BalanceAmount);
					sqlStatement.bindString(4, InvoiceNumber);
					sqlStatement.bindString(5, CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00");
					sqlStatement.bindString(6, InvoiceNumber);
					sqlStatement.bindString(7, BalanceAmount);
					sqlStatement.bindString(8, DOC_TYPE);
					sqlStatement.bindString(9, AppConstants.CURRENTORDER);
					sqlStatement.executeInsert();
				}
				else
				{
					sqlUpdateStatement 	=	sqLiteDatabase.compileStatement(queryUpdate);
					sqlUpdateStatement.bindString(1, BalanceAmount);
					sqlUpdateStatement.bindString(2, BalanceAmount);
					sqlUpdateStatement.bindString(3, strCustomerSIteID);
					sqlUpdateStatement.bindString(4, InvoiceNumber);
					sqlUpdateStatement.execute();
				}
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqlStatement!=null)
					sqlStatement.close();
				if(sqlUpdateStatement!=null)
					sqlUpdateStatement.close();
				if(sqlSelectStatement!=null)
					sqlSelectStatement.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public void deletePendingInvoice(String orderNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM tblPendingInvoices WHERE InvoiceNumber='"+orderNo+"'");
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

	public void getGEOBySiteID(JourneyPlanDO object) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try 
			{
				String query =  "SELECT GeoCodeX,GeoCodeY FROM tblCustomer WHERE Site = '"+object.site+"'";
				
				objSqliteDB = DatabaseHelper.openDataBase();
				c 			= objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
				{
					object.geoCodeX = c.getString(0);
					object.geoCodeY = c.getString(1);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
}
