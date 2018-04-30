package com.winit.baskinrobbin.salesman.common;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.winit.baskinrobbin.parsers.AllDataSyncParser;
import com.winit.baskinrobbin.parsers.CustomerLastOrderDetailParser;
import com.winit.baskinrobbin.parsers.CustomerPendingInvoiceParser;
import com.winit.baskinrobbin.parsers.DAPassCodeParser;
import com.winit.baskinrobbin.parsers.GenerateOfflineDataParserNew;
import com.winit.baskinrobbin.parsers.GetAllDeleteLogsParser;
import com.winit.baskinrobbin.parsers.GetAllMovements;
import com.winit.baskinrobbin.parsers.GetCustomersByUserIdParser;
import com.winit.baskinrobbin.parsers.GetDailyJPAndRoute;
import com.winit.baskinrobbin.parsers.GetDiscountParser;
import com.winit.baskinrobbin.parsers.GetTrxHeaderForApp;
import com.winit.baskinrobbin.parsers.GetVehiclesParser;
import com.winit.baskinrobbin.parsers.SplashDataSyncParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SyncData extends IntentService
{
	public final int STARTED = 0;
	public static final int END = 1, ERROR = -1, NO_INTERNET = -10;
	private Preference preference;
//	private static UploadDataListener uploadDataListener;
	private static SyncProcessListner syncProcessListner;
	private ConnectionHelper connectionHelper;
	public SyncData() 
	{
		super("SyncData");
	}
	
	public static void setListener(SyncProcessListner listener)
	{
		syncProcessListner = listener;
	}
	public static void removeListner(){
		syncProcessListner=null;
	}
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		preference 	     = 	new Preference(this);
		if(syncProcessListner!=null)
			syncProcessListner.start();
		preference.saveIntInPreference(Preference.SYNC_STATUS, 0);
		preference.commitPreference();
//		sendUpdates(STARTED, "Syncing data...");
		
		
		try 
		{
			if(NetworkUtility.isNetworkConnectionAvailable(SyncData.this))
			{
				String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
				syncDeletedLogs();
				loadAllMovements_Sync("Refreshing data...",empId);
				loadAllDataSync(empId);
			}
			
			if(syncProcessListner!=null)
				syncProcessListner.error();
		} 
		catch (Exception e)
		{
			if(syncProcessListner!=null)
				syncProcessListner.error();
			e.printStackTrace();
		}
		
		if(syncProcessListner!=null)
			syncProcessListner.end();
	}
	public void syncDeletedLogs()
	{
		try {
			ConnectionHelper connectionHelper= new ConnectionHelper(null);
			GetAllDeleteLogsParser allDeleteLogsParser = new GetAllDeleteLogsParser(this);
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllDeleteLogs);
			String lsd = 0+"";
			String lst = 0+"";
			if(synLogDO != null)
			{
				lsd = synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else if((synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllDataSync)) != null)
			{
				lsd =synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else
			{
				lsd = preference.getStringFromPreference(Preference.LSD, lsd);
				lst = preference.getStringFromPreference(Preference.LST, lst);
			}
			connectionHelper.sendRequest(this,BuildXMLRequest.getAllDeleteLogs(lsd, lst), allDeleteLogsParser, ServiceURLs.GetAllDeleteLogs,preference);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	public void loadAllDataSync(String empNo)
	{
			ConnectionHelper connectionHelper = new ConnectionHelper(null);;
			AllDataSyncParser allDataSyncParser = new AllDataSyncParser(this, empNo,syncProcessListner);
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllDataSync);//please don't change this entity
			String lsd = 0+"";
			String lst = 0+"";
			if(synLogDO != null)
			{
				lsd =synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else 
			{
				lsd = preference.getStringFromPreference(Preference.LSD, lsd);
				lst = preference.getStringFromPreference(Preference.LST, lst);
			}
//			lsd="118107";
//			lst="40949";
			Log.d("SyncData", lsd+" "+lst);
			connectionHelper.sendRequest(this,BuildXMLRequest.getAllDataSync(preference.getStringFromPreference(Preference.EMP_NO, ""), lsd, lst), allDataSyncParser, ServiceURLs.GetAllDataSync,preference);
		
		
	}
	private void loadIncrementalData(String empId)
	{
		if(NetworkUtility.isNetworkConnectionAvailable(SyncData.this))
		{
//			loadAdvancedOrder(empId, empId, "Loading Orders...");
			loadDiscounts(empId,"Loading Discounts...");//Done
			
//			sendUpdates(STARTED, "Checking Transactions...");
			final boolean isAllUploaded = new GetOrdersToUpload(SyncData.this, preference, AppStatus.TODAY_DATA).uploadOrders(empId);
			
			if(isAllUploaded)
				loadAllMovements_Sync("Loading All Movements...", empId); // Done
			
			loadVehicleList(empId,"Loading Vehicle List...");//Done
			
			if(!preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "").equalsIgnoreCase(""))
			{
				if(!preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(CalendarUtils.getOrderPostDate()))
				{
					preference.removeFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME);
					preference.saveStringInPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+Preference.LAST_JOURNEY_DATE, CalendarUtils.getOrderPostDate());
					preference.commitPreference();
					
					loadPendingInvoices(empId,"Loading Customers Pending Invoices...", false);
					
					preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
					preference.saveBooleanInPreference(Preference.IsStockVerified, false);
					preference.saveIntInPreference(Preference.STARTDAY_VALUE, 0);
					preference.saveStringInPreference(Preference.STARTDAY_TIME, "");
					preference.commitPreference();
				}
				else
					loadPendingInvoices(empId,"Loading Customers Pending Invoices...", false);
			}
			else
			{
				preference.saveStringInPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+Preference.LAST_JOURNEY_DATE, CalendarUtils.getOrderPostDate());
				preference.commitPreference();
				loadPendingInvoices(empId, "Loading Customers Pending Invoices...", true);
			}
			
			//////////////////For off-line Data///////////////////////  Done
			if(!preference.getStringFromPreference(Preference.OFFLINE_DATE, "").equalsIgnoreCase(""))
			{
				if(!preference.getStringFromPreference(Preference.OFFLINE_DATE, "").equalsIgnoreCase(CalendarUtils.getOrderPostDate()))
				{
					new CommonDA().deleteOfflineData();
					loadOfflineData("Loading Offline data...", empId);
				}
			}
			else
				loadOfflineData("Loading Offline data...", empId);
			
			/////////////////////////////////////////////////////////////////////
			loadSplashScreenData("Loading master data...", empId);  // Done
			
//			loadAssetsMaster(empId,"Loading Assets...");
//			loadTaskMaster(empId,"Loading Tasks...");
//			loadSurveyMasters(empId, "Loading Survey...");
			
			loadCustomers(empId, "Loading Customers...");//Done
			loadTransactions(empId, "Loading Transactions...");//Done
			loadDailyRoute(empId, "Loading Journey Plan and Route Details...");//Done
			getAllPasscodeforDA("Loading passcode...", empId);//Done
			preference.saveStringInPreference(Preference.EMP_NO, empId);
	        preference.commitPreference();
		}
	}
	
	private void loadDiscounts(String strSalesmanCode, String mgs)
	{
//		sendUpdates(STARTED, mgs);
		GetDiscountParser getDiscountParser 	= 	new GetDiscountParser(SyncData.this);
		
		String lsd = "0", lst = "0";
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_DISCOUNTS);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		connectionHelper.sendRequest_Bulk(SyncData.this,BuildXMLRequest.getDiscount(strSalesmanCode,lsd, lst), getDiscountParser, ServiceURLs.GET_DISCOUNTS, preference);
	}
	
	/**
	 * Method to get the refreshed van load data
	 */
	private void loadAllMovements_Sync(String mgs, String empNo)
	{
//		sendUpdates(STARTED, mgs);
		GetAllMovements getAllMovements = new GetAllMovements(SyncData.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);
		
		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		new ConnectionHelper(null).sendRequest(SyncData.this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus, preference);
	}
	
	private void loadVehicleList(String strSalesmanCode,String mgs)
	{
//		sendUpdates(STARTED, mgs);
		GetVehiclesParser getVehiclesParser = new GetVehiclesParser(SyncData.this);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_ALL_VEHICLES);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}		
		connectionHelper.sendRequest_Bulk(SyncData.this,BuildXMLRequest.getVehicles(strSalesmanCode, lsd, lst), getVehiclesParser, ServiceURLs.GET_ALL_VEHICLES, preference);
	}
	
	private void loadPendingInvoices(String strSalesmanCode, String mgs, boolean isToupdate)
	{
//		sendUpdates(STARTED, mgs);
		CustomerPendingInvoiceParser customerPendingInvoiceParser = new CustomerPendingInvoiceParser(SyncData.this, isToupdate);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_PENDING_SALES_INVOICE);
		
		String lastSyncTime = "";
		if(synLogDO != null)
		{
			lastSyncTime = synLogDO.TimeStamp;
		}
		
		connectionHelper.sendRequest_Bulk(SyncData.this,BuildXMLRequest.getetCustomersPendingInvoice(strSalesmanCode, lastSyncTime), customerPendingInvoiceParser, ServiceURLs.GET_PENDING_SALES_INVOICE, preference);
	}
	
	private void loadOfflineData(String mgs, String strSalesmanCode)
	{
//		sendUpdates(STARTED, mgs);
		GenerateOfflineDataParserNew getOfflineDataParser = new GenerateOfflineDataParserNew(SyncData.this, strSalesmanCode);
		connectionHelper.sendRequest_Bulk(SyncData.this,BuildXMLRequest.getSequenceNoBySalesmanForHH(strSalesmanCode), getOfflineDataParser, ServiceURLs.GET_SEQUENCE_NO, preference);
	}
	
	private void loadSplashScreenData(String mgs, String userCode)
	{
//		sendUpdates(STARTED, mgs);
		String lsd = "0", lst = "0";
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest_Bulk(SyncData.this,BuildXMLRequest.getSplashScreenDataforSync(userCode,lsd,  lst), new SplashDataSyncParser(SyncData.this), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC, preference);
	}
	
	private void loadCustomers(String strEmpNo, String msg) 
	{
//		sendUpdates(STARTED, msg);
		String lsd = 0+"", lst = 0+"";
		GetCustomersByUserIdParser getCustomersByUserIdParser = new GetCustomersByUserIdParser(SyncData.this, strEmpNo);
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_CUSTOMER_SITE);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest(SyncData.this,BuildXMLRequest.getAllCustomersByUserIDWithLastSynch(strEmpNo, lsd, lst), getCustomersByUserIdParser, ServiceURLs.GET_CUSTOMER_SITE, preference);
	}
	
	private void loadDailyRoute(String strEmpNo, String msg) 
	{
//		sendUpdates(STARTED, msg);
		GetDailyJPAndRoute getJPAndRouteDetails = new GetDailyJPAndRoute(SyncData.this, strEmpNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetJPAndRouteDetails);
		String lsd = 0+"", lst = 0+"";
		if(synLogDO!=null)
		{
			 lsd = synLogDO.UPMJ;   
			 lst = synLogDO.UPMT;   
		}
		connectionHelper.sendRequest(SyncData.this,BuildXMLRequest.getAllJPAndRouteDetailsWithLastSynch(strEmpNo, lsd, lst), getJPAndRouteDetails, ServiceURLs.GetJPAndRouteDetails, preference);
	}
	
	private void getAllPasscodeforDA(String mgs,String strEmpNo)
	{
//		sendUpdates(STARTED, mgs);
		int count = new CommonDA().getPasscodeAvailibility();
		if(count < 20)
		{
			DAPassCodeParser daPassCodeParser = new DAPassCodeParser(SyncData.this);
			connectionHelper.sendRequest(SyncData.this, BuildXMLRequest.getPasscodeForDa(strEmpNo), daPassCodeParser, ServiceURLs.GET_DA_PASSCODE, preference);
		}
	}
	
//	private void sendUpdates(int STATUS, String message)
//	{
//		try
//		{
//			if(uploadDataListener != null)
//				uploadDataListener.updateStatus(STATUS, message);
//		}
//		catch (Exception e)
//		{
//		}
//	}

//	@Override
//	public void onConnectionException(Object msg)
//	{
//		sendUpdates(ERROR, ""+msg);
//	}
	
	///////////////////////Not in use//////////////////////
	
	private void loadAdvancedOrder(String salesmanCode, String empId,String mgs)
	{
//		sendUpdates(STARTED, mgs);
		CustomerLastOrderDetailParser userJourneyPlanParser = new CustomerLastOrderDetailParser(SyncData.this, empId,salesmanCode);
		connectionHelper.sendRequest(SyncData.this, BuildXMLRequest.getAdvanceOrderByEmpNo(empId,""), userJourneyPlanParser, ServiceURLs.GetAdvanceOrderByEmpNo, preference);
	}

	private void loadTransactions(String strEmpNo, String msg) 
	{
//		sendUpdates(STARTED, msg);
		GetTrxHeaderForApp getTrxHeaderForApp = new GetTrxHeaderForApp(SyncData.this, strEmpNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetTrxHeaderForApp);
		String lsd = 0+"", lst = 0+"";
		
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest(SyncData.this,BuildXMLRequest.getAllTrxHeaderForAppWithLastSynch(strEmpNo, lsd, lst), getTrxHeaderForApp, ServiceURLs.GetTrxHeaderForApp, preference);
	}
	
	public interface SyncProcessListner
	{
		public void start();
		public void progress(String msg);
		public void error();
		public void end();
	}
}
