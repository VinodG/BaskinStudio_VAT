package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.R.color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.CustomerLastOrderDetailParser;
import com.winit.baskinrobbin.parsers.CustomerPendingInvoiceParser;
import com.winit.baskinrobbin.parsers.GenerateOfflineDataParserNew;
import com.winit.baskinrobbin.parsers.GetAllDeletedCustomers;
import com.winit.baskinrobbin.parsers.GetAllDeletedItems;
import com.winit.baskinrobbin.parsers.GetAllMovements;
import com.winit.baskinrobbin.parsers.GetAssetMastersParser;
import com.winit.baskinrobbin.parsers.GetCustomersByUserIdParser;
import com.winit.baskinrobbin.parsers.GetDiscountParser;
import com.winit.baskinrobbin.parsers.GetJPAndRouteDetails;
import com.winit.baskinrobbin.parsers.GetPricingParser;
import com.winit.baskinrobbin.parsers.GetPromotionsParser;
import com.winit.baskinrobbin.parsers.GetSurveyMastersParser;
import com.winit.baskinrobbin.parsers.GetTrxHeaderForApp;
import com.winit.baskinrobbin.parsers.GetVehiclesParser;
import com.winit.baskinrobbin.parsers.SplashDataSyncParser;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SyncLogsActivityNew extends BaseActivity
{
	private LinearLayout llMain;
	private TextView tvTitleSync, tvServiceName, tvSyncTime, tvResultOfSearch;
	private ListView lvSyncLog;
	private SyncLogAdapter customeListAdapter;
	private Vector<NameIDDo> vecSyncLog;
	private ConnectionHelper connectionHelper;
	
	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.sync_log_layout, null);
		llBody.addView(llMain,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		connectionHelper = new ConnectionHelper(SyncLogsActivityNew.this);
		
		intialiseControls();
		setTypeFace(llMain);
		showListdata();
		setTypeFace(llMain);
	}
	
	public void showListdata()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				loadSyncLogData();
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(customeListAdapter != null && vecSyncLog != null && vecSyncLog.size() > 0)
							customeListAdapter.refresh(vecSyncLog);
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void loadSyncLogData()
	{
		vecSyncLog  = new Vector<NameIDDo>();
		
		NameIDDo nameIDDo = new NameIDDo();
		/*nameIDDo.strId    = "Deleted Customers";
		nameIDDo.strName  = ServiceURLs.GET_HH_DELETED_CUSTOMERS;
		nameIDDo.strType  = ""+preference.getStringFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		  = new NameIDDo();
		nameIDDo.strId    = "Deleted Items";
		nameIDDo.strName  = ServiceURLs.GET_ALL_DELETED_ITEMS;
		nameIDDo.strType  = ""+preference.getStringFromPreference(ServiceURLs.GET_ALL_DELETED_ITEMS+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);*/
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Item Price";
		nameIDDo.strName = ServiceURLs.GET_All_PRICE_WITH_SYNC;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_All_PRICE_WITH_SYNC+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Orders";
		nameIDDo.strName = ServiceURLs.GetAdvanceOrderByEmpNo;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetAdvanceOrderByEmpNo+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Discounts";
		nameIDDo.strName = ServiceURLs.GET_DISCOUNTS;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_DISCOUNTS+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "All Movements";
		nameIDDo.strName = ServiceURLs.GetAppActiveStatus;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetAppActiveStatus+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Vehicle List";
		nameIDDo.strName = ServiceURLs.GET_ALL_VEHICLES;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_ALL_VEHICLES+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Pending Invoices";
		nameIDDo.strName = ServiceURLs.GET_PENDING_SALES_INVOICE;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Sequence Numbers";
		nameIDDo.strName = ServiceURLs.GET_SEQUENCE_NO;
		nameIDDo.strType = ""+preference.getStringFromPreference(Preference.OFFLINE_DATE,"");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "SplashScreenData";
		nameIDDo.strName = ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Promotions";
		nameIDDo.strName = ServiceURLs.GetAllPromotions;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetAllPromotions+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Assets";
		nameIDDo.strName = ServiceURLs.GetAssetMasters;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetAssetMasters+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Survey";
		nameIDDo.strName = ServiceURLs.GetSurveyMasters;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetSurveyMasters+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Customers";
		nameIDDo.strName = ServiceURLs.GET_CUSTOMER_SITE;
		nameIDDo.strType = ""+preference.getStringFromPreference(Preference.GetCustomersByUserID, "");
		vecSyncLog.add(nameIDDo);
		

		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Transactions";
		nameIDDo.strName = ServiceURLs.GetTrxHeaderForApp;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetTrxHeaderForApp+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Journey Plan and Route Details";
		nameIDDo.strName = ServiceURLs.GetJPAndRouteDetails;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetJPAndRouteDetails+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvTitleSync			=	(TextView)llMain.findViewById(R.id.tvTitleSync);
		tvServiceName		=	(TextView)llMain.findViewById(R.id.tvServiceName);
		tvSyncTime			=	(TextView)llMain.findViewById(R.id.tvSyncTime);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		lvSyncLog			=	(ListView)llMain.findViewById(R.id.lvSyncLog);
		
		
		lvSyncLog.setCacheColorHint(0);
		lvSyncLog.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvSyncLog.setSelector(color.transparent);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		lvSyncLog.setVerticalScrollBarEnabled(false);
		lvSyncLog.setAdapter(customeListAdapter = new SyncLogAdapter(new Vector<NameIDDo>()));
		
		/*tvTitleSync.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSyncTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvServiceName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvResultOfSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public class SyncLogAdapter extends BaseAdapter
	{
		private Vector<NameIDDo> vecSyncLog;
		public SyncLogAdapter(Vector<NameIDDo> vecSyncLog) 
		{
			this.vecSyncLog = vecSyncLog;
		}
		@Override
		public int getCount() 
		{
			return vecSyncLog.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecSyncLog.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			NameIDDo nameIDDo = vecSyncLog.get(position);
			if(convertView == null)
				convertView = inflater.inflate(R.layout.sync_log_cell, null);
			
			TextView tvServiceName  = (TextView)convertView.findViewById(R.id.tvServiceName);
			TextView tvSyncTime 	= (TextView)convertView.findViewById(R.id.tvSyncTime);
			Button btnSync			= (Button)convertView.findViewById(R.id.btnSync);
			
//			tvServiceName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//			tvSyncTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			tvServiceName.setText(nameIDDo.strId);
			if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SEQUENCE_NO) || nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetSalesmanLandmarkWithSync))
			{
				if(nameIDDo.strType.contains(CalendarUtils.getOrderPostDate()))
					tvSyncTime.setTextColor(getResources().getColor(R.color.list_title));
				else
					tvSyncTime.setTextColor(getResources().getColor(R.color.red));
			}
			else if(nameIDDo.strType.contains(CalendarUtils.getSyncDateFormat()))
				tvSyncTime.setTextColor(getResources().getColor(R.color.list_title));
			else
				tvSyncTime.setTextColor(getResources().getColor(R.color.red));
			
			tvSyncTime.setText(nameIDDo.strType);
			if(nameIDDo.strType == null || nameIDDo.strType.equalsIgnoreCase(""))
			{
				tvSyncTime.setTextColor(getResources().getColor(R.color.red));
				tvSyncTime.setText("Not Synced");
			}
			
			btnSync.setTag(nameIDDo);
			btnSync.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(isNetworkConnectionAvailable(SyncLogsActivityNew.this))
					{
						NameIDDo nameIDDo = (NameIDDo) v.getTag();
						syncData(nameIDDo);
					}
					else
						showCustomDialog(SyncLogsActivityNew.this, getResources().getString(R.string.warning), "Internet connection is not available.", getResources().getString(R.string.OK), null, "");
				}
			});
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(45 * px)));
			setTypeFace((ViewGroup) convertView);
			return convertView;
		}
		public void refresh(Vector<NameIDDo> vecSyncLog) 
		{
			this.vecSyncLog = vecSyncLog;
			if(vecSyncLog!=null && vecSyncLog.size()>0)
			{
				lvSyncLog.setVisibility(View.VISIBLE);
				tvResultOfSearch.setVisibility(View.GONE);
				notifyDataSetChanged();
			}
			else
			{
				lvSyncLog.setVisibility(View.GONE);
				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void syncData(final NameIDDo nameIDDo)
	{
		showLoader("Syncing...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startServiceSync(nameIDDo);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						showListdata();
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void startServiceSync(NameIDDo nameIDDo)
	{
		if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_HH_DELETED_CUSTOMERS))
			getAllHHCustomerDeletedItems(preference.getStringFromPreference(Preference.EMP_NO, ""), "Deleting Customers...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_ALL_DELETED_ITEMS))
			deleteAllExpiredItems();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_All_PRICE_WITH_SYNC))
			getAllPriceWithSync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetAdvanceOrderByEmpNo))
			loadAdvancedOrder();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_DISCOUNTS))
			loadDiscounts();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetAppActiveStatus))
			loadAllMovements_Sync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_ALL_VEHICLES))
			loadVehicleList();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_PENDING_SALES_INVOICE))
			loadPendingInvoices();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC))
			getSplashScreenDataforSync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetAllPromotions))
			loadPromotions();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetAssetMasters))
			loadAssetsMaster();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SEQUENCE_NO))
			loadOfflineData("Loading offline data...", preference.getStringFromPreference(Preference.EMP_NO, ""));
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetSurveyMasters))
			loadSurveyMasters();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_CUSTOMER_SITE))
			loadCustomers();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetTrxHeaderForApp))
			loadTransactions();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetJPAndRouteDetails))
			loadJourneyPLansAndRoute();
	}
	
	public void loadAdvancedOrder()
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		String salesmanCode = preference.getStringFromPreference(Preference.EMP_NO, "");
		CustomerLastOrderDetailParser userJourneyPlanParser = new CustomerLastOrderDetailParser(SyncLogsActivityNew.this, empId,salesmanCode);
		connectionHelper.sendRequest(SyncLogsActivityNew.this, BuildXMLRequest.getAdvanceOrderByEmpNo(empId,""), userJourneyPlanParser, ServiceURLs.GetAdvanceOrderByEmpNo, preference);
	}
	
	public void loadDiscounts()
	{
		String salesmanCode = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetDiscountParser getDiscountParser 	= 	new GetDiscountParser(SyncLogsActivityNew.this);
		
		String lsd = "0", lst = "0";
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_DISCOUNTS);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getDiscount(salesmanCode,lsd, lst), getDiscountParser, ServiceURLs.GET_DISCOUNTS, preference);
	}
	
	public void loadAllMovements_Sync()
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetAllMovements getAllMovements = new GetAllMovements(SyncLogsActivityNew.this, empId);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);
		
		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getActiveStatus(empId, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus, preference);
	}
	
	public void loadVehicleList()
	{
		String salesmanCode = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetVehiclesParser getVehiclesParser = new GetVehiclesParser(SyncLogsActivityNew.this);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_ALL_VEHICLES);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}		
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getVehicles(salesmanCode, lsd, lst), getVehiclesParser, ServiceURLs.GET_ALL_VEHICLES, preference);
		
	}
	private void loadPendingInvoices()
	{
		String salesmanCode = preference.getStringFromPreference(Preference.EMP_NO, "");
		CustomerPendingInvoiceParser customerPendingInvoiceParser = new CustomerPendingInvoiceParser(SyncLogsActivityNew.this, false);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_PENDING_SALES_INVOICE);
		
		String lastSyncTime = "";
		if(synLogDO != null)
		{
			lastSyncTime = synLogDO.TimeStamp;
		}
		
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getetCustomersPendingInvoice(salesmanCode, lastSyncTime), customerPendingInvoiceParser, ServiceURLs.GET_PENDING_SALES_INVOICE, preference);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getetCustomersPendingInvoice(preference.getStringFromPreference(Preference.EMP_NO,""), preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), customerPendingInvoiceParser, ServiceURLs.GET_PENDING_SALES_INVOICE);
	}
	
	private void loadPromotions() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetPromotionsParser getPromotionsParser = new GetPromotionsParser(SyncLogsActivityNew.this, empId);
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllPromotions);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
//		connectionHelper.sendRequest(BuildXMLRequest.getAllPromotions(empId, lsd, lst), getPromotionsParser, ServiceURLs.GetAllPromotions);
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAllPromotionsWithLastSynch(empId, lsd,lst), getPromotionsParser, ServiceURLs.GetAllPromotions, preference);
	}
	
	private void loadAssetsMaster() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetAssetMastersParser assetMastersParser = new GetAssetMastersParser(SyncLogsActivityNew.this);
		String userCode = empId;
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAssetMasters);
		
		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAssetMasters(userCode, lsd, lst), assetMastersParser, ServiceURLs.GetAssetMasters, preference);
	}
	
	private void loadSurveyMasters() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetSurveyMastersParser getSurveyMastersParser = new GetSurveyMastersParser(SyncLogsActivityNew.this, empId);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetSurveyMasters);
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO!=null)
		{
			 lsd = synLogDO.UPMJ;   
			 lst = synLogDO.UPMT;   
		}
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAllSurveyMastersWithLastSynch(empId, lsd, lst), getSurveyMastersParser, ServiceURLs.GetSurveyMasters, preference);
	}
	
	private void loadCustomers() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetCustomersByUserIdParser getCustomersByUserIdParser = new GetCustomersByUserIdParser(SyncLogsActivityNew.this, empId);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_CUSTOMER_SITE);
		String lsd = 0+"", lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAllCustomersByUserIDWithLastSynch(empId, lsd, lst), getCustomersByUserIdParser, ServiceURLs.GET_CUSTOMER_SITE, preference);
	}
	
	private void loadTransactions() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetTrxHeaderForApp getTrxHeaderForApp = new GetTrxHeaderForApp(SyncLogsActivityNew.this, empId);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetTrxHeaderForApp);
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
//		 
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAllTrxHeaderForAppWithLastSynch(empId, lsd, lst), getTrxHeaderForApp, ServiceURLs.GetTrxHeaderForApp, preference);
	}
	
	private void loadJourneyPLansAndRoute() 
	{
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		GetJPAndRouteDetails getJPAndRouteDetails = new GetJPAndRouteDetails(SyncLogsActivityNew.this, empId);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetJPAndRouteDetails);
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO!=null)
		{
			 lsd = synLogDO.UPMJ;   
			 lst = synLogDO.UPMT;   
		}
		connectionHelper.sendRequest(SyncLogsActivityNew.this,BuildXMLRequest.getAllJPAndRouteDetailsWithLastSynch(empId, lsd, lst), getJPAndRouteDetails, ServiceURLs.GetJPAndRouteDetails, preference);
	}
	
	private void loadOfflineData(String mgs, String strSalesmanCode)
	{
		GenerateOfflineDataParserNew getOfflineDataParser = new GenerateOfflineDataParserNew(SyncLogsActivityNew.this, strSalesmanCode);
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getSequenceNoBySalesmanForHH(strSalesmanCode), getOfflineDataParser, ServiceURLs.GET_SEQUENCE_NO, preference);
	}
	/*private void loadCustomerHistory(String mgs)
	{
		showLoader(mgs);
		CustomerHistoryParser customerHistoryParser 		= 	new CustomerHistoryParser(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getCustomerHistoryWithSync(preference.getStringFromPreference(Preference.EMP_NO,""),preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), customerHistoryParser, ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC);
	}
	
	private void loadLandMarks(String mgs)
	{
		showLoader(mgs);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getSalesManLandmarkSynce(preference.getStringFromPreference(Preference.EMP_NO, ""), preference.getStringFromPreference(Preference.LAST_SYNC_TIME, "")), new LandmarkParser(SyncLogsActivity.this), ServiceURLs.GetSalesmanLandmarkWithSync);
	}
	
	private void loadHHInventory(String mgs)
	{
		showLoader(mgs);
		GetVMInventoryParser getVmInventoryParser = new GetVMInventoryParser(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getVMInventory(preference.getStringFromPreference(Preference.EMP_NO,""), preference.getStringFromPreference(ServiceURLs.GET_HH_INVENTORY+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, ""), CalendarUtils.getOrderPostDate()), getVmInventoryParser, ServiceURLs.GET_HH_INVENTORY);
	}
	
	private void loadOfflineData(String mgs, String strSalesmanCode)
	{
		GenerateOfflineDataParserNew getOfflineDataParser = new GenerateOfflineDataParserNew(SyncLogsActivityNew.this, strSalesmanCode);
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getSequenceNoBySalesmanForHH(strSalesmanCode), getOfflineDataParser, ServiceURLs.GET_SEQUENCE_NO);
	}
	
	private void loadVehicleList(String strSalesmanCode,String mgs)
	{
		showLoader(mgs);
		GetVehiclesParser getVehiclesParser = new GetVehiclesParser(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getVehicles(strSalesmanCode , preference.getStringFromPreference(ServiceURLs.GET_ALL_VEHICLES+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), getVehiclesParser, ServiceURLs.GET_ALL_VEHICLES);
	}
	private void getVMPasscodes(String strPresellerId,String mgs)
	{
		showLoader(mgs);
		PresellerPassCodeParser parser = new PresellerPassCodeParser(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllPassCode(strPresellerId), parser , ServiceURLs.GET_ALL_PASS_CODE);
	}*/
	
	private void getAllPriceWithSync()
	{
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_All_PRICE_WITH_SYNC);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		GetPricingParser getPricingParser = new GetPricingParser(SyncLogsActivityNew.this);
		String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getAllPriceWithSync(empId, lsd, lst), getPricingParser, ServiceURLs.GET_All_PRICE_WITH_SYNC, preference);
	}
	private void getSplashScreenDataforSync()
	{
		String UserCode = preference.getStringFromPreference(preference.EMP_NO, "");

		String lsd = "0", lst = "0";
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		connectionHelper.sendRequest_Bulk(SyncLogsActivityNew.this,BuildXMLRequest.getSplashScreenDataforSync(UserCode,lsd,  lst), new SplashDataSyncParser(SyncLogsActivityNew.this), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC, preference);
	}
	private void getHouseholdMastersWithSync()
	{
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getHouseholdMastersWithSync(preference.getStringFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, "")), new HouseholdMasterSyncParser(SyncLogsActivity.this), ServiceURLs.GetHouseHoldMastersWithSync);
	}
	
	private void deleteAllExpiredItems()
	{
		//connectionHelper //connectionHelper = new //connectionHelper(SyncLogsActivity.this);
		GetAllDeletedItems deletedItems = new GetAllDeletedItems(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllDeletedItems(preference.getStringFromPreference(ServiceURLs.GET_ALL_DELETED_ITEMS+Preference.LAST_SYNC_TIME, "")), 
//				deletedItems, ServiceURLs.GET_ALL_DELETED_ITEMS);
	}
	
	public void getAllHHCustomerDeletedItems(String strEmpId,String mgs)
	{
		GetAllDeletedCustomers getAllDeletedCustomers = new GetAllDeletedCustomers(SyncLogsActivityNew.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllHHCustomerDeletedItems(strEmpId, preference.getStringFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, "")), getAllDeletedCustomers, ServiceURLs.GET_HH_DELETED_CUSTOMERS);
	}
}
