package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.pinch.ListDisplayRequestsFragments;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

public class LoadRequestActivity extends BaseActivity
{
	private LinearLayout llLoadRequestScreen;
	private TextView tvCode, tvDate, tvQty, tvOrdersheetHeader, tvNoItemFound;
	private ListView lvLoadRequest;
	private Button btnAdd, btnFinish,btnContinue/*,btnStartDay*/;
	private int load_type;
//	private ArrayList<LoadRequestDO> arrayList;
	HashMap<String, ArrayList<LoadRequestDO>> hmRequests;
	private ArrayList<LoadRequestDO> arrayList;
	//private LoadViewRequestAdapter loadViewRequestAdapter;
	private VehicleDO vehicleDO;
	private String date = "",from="";
	private boolean isUnload = false, isSummary = false,isEditable=false;
	
	private String[] tabs_vanSales = {"PENDING","HISTORY"};
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private int pendingCount,ebsApvdCount;
	private Vector<NameIDDo> vecReasons = new Vector<NameIDDo>();
	private PostReasonDO objPostReasonDO ;	
	private Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
	private String filepath =null, filepathDriver = null;
	
	
	@Override
	public void initialize()
	{
		llLoadRequestScreen = (LinearLayout) inflater.inflate(R.layout.load_request_screen, null);
		llBody.addView(llLoadRequestScreen, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
		{
			load_type = getIntent().getExtras().getInt("load_type");
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
			if(getIntent().getExtras().containsKey("date"))
				date = getIntent().getExtras().getString("date");
			
			if(getIntent().getExtras().containsKey("isUnload"))
				isUnload = getIntent().getExtras().getBoolean("isUnload");
			
			if(getIntent().getExtras().containsKey("isSummary"))
				isSummary = getIntent().getExtras().getBoolean("isSummary");
			if(getIntent().getExtras().containsKey("from"))
				from = getIntent().getExtras().getString("from");
		}
		
		//loadViewRequestAdapter = new LoadViewRequestAdapter(arrayList);
		initializeControles();
		tvOrdersheetHeader.setText("Load View Request");
		/*btnStartDay.setText("Refresh");
		btnStartDay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isNetworkConnectionAvailable(LoadRequestActivity.this))
					showCustomDialog(LoadRequestActivity.this,"Alert !", getString(R.string.no_internet), "OK", null, "");
				else
				{
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							String empNo 		= preference.getStringFromPreference(Preference.EMP_NO, "");
							
							final boolean isAllUploaded = new GetOrdersToUpload(LoadRequestActivity.this, preference, AppStatus.TODAY_DATA).uploadOrders(empNo);
							
							if(isAllUploaded)
								loadAllMovements_Sync("Refreshing data...",empNo);
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									if(!isAllUploaded)
									{
										hideLoader();
										showCustomDialog(LoadRequestActivity.this,"Alert !", "Please upload the data from settings before making another sync request.", "OK", null, "");
									}
									else	
										loadData();
								}
							});
						}
					}).start();
				}
			}
		});*/
		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(ebsApvdCount>0)
				{
					showCustomDialog(LoadRequestActivity.this,getString(R.string.warning), "Some movements not yet shipped.You need to collect them .", "Ok", null, "");
				}else{
					if(pendingCount>0){
						showCustomDialog(LoadRequestActivity.this,getString(R.string.warning), "There are some pending Mo's do you want to continue?.", "Yes", "No", "pendingskip");
					}else{
						 if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0){
								Intent intent = new Intent(LoadRequestActivity.this, OdometerReadingActivity.class);
								intent.putExtra("isStartDay", true);
								intent.putExtra("image_path", "");
								intent.putExtra("image_path_driver", "");
								startActivity(intent);
								setResult(1111);
								finish();
							}else{
								Intent intent = new Intent(LoadRequestActivity.this, SalesmanCustomerList.class);
								startActivity(intent);	
							}
						
					}
				}
			}
		});
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if(load_type==2 || load_type == 3)
				{
					showLoader("loading....");
					final VehicleDO vehicle=preference.getVehicleObjectFromPreference(preference.VEHICLE_DO);
					new Thread(new Runnable()
					{
						public void run()
						{
							try {
								if(!isNetworkConnectionAvailable(LoadRequestActivity.this)){
									showCustomDialog(LoadRequestActivity.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
								}else{
									//Hit the service to check if the salesman can unload or not
									BooleanParser booleanParser = new BooleanParser(LoadRequestActivity.this);
									new ConnectionHelper(null).sendRequest(LoadRequestActivity.this,BuildXMLRequest.VerifyIsUnloadApproved(preference.getStringFromPreference(preference.EMP_NO, ""),vehicle.VEHICLE_NO), booleanParser, ServiceURLs.CompareERPVsMWStockByVehicleCodeResponse,preference);
									int resposne =(Integer) booleanParser.getData();
									
									//resposne=1;//temp conditions
									if(resposne==1){
										hideLoader();
										Intent intent = new Intent(LoadRequestActivity.this, AddNewLoadRequest.class);
										intent.putExtra("load_type", load_type);
										intent.putExtra("vehicle", vehicleDO);
										intent.putExtra("date", date);
										intent.putExtra("isaddRequest", true);
										intent.putExtra("isUnload", isUnload);
										startActivity(intent);
									}
									else{
										hideLoader();
										showCustomDialog(LoadRequestActivity.this, getString(R.string.warning), booleanParser.getServerMessage(), getString(R.string.OK), null, "");
									}
								} 
							}catch (Exception e) {
									e.printStackTrace();
								}	
							finally{
								hideLoader();
							}
							
						}
					}).start();

				}
				else
				{


					Intent intent = new Intent(LoadRequestActivity.this, AddNewLoadRequest.class);
					intent.putExtra("load_type", load_type);
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("date", date);
					intent.putExtra("isaddRequest", true);
					intent.putExtra("isUnload", isUnload);
					startActivity(intent);
				}
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		//commented temporarydata
		
		btnMenu.setVisibility(View.GONE);
		setTypeFace(llLoadRequestScreen);
		 setActionInViews(btnLogo, false);
			setActionInViews(llMenu, false);
			setActionInViews(btnMenu, false);
	}
	
	private void loadData() {
		if(isSummary)
			btnAdd.setVisibility(View.GONE);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				InventoryDA inventoryDA=new InventoryDA();
				hmRequests=inventoryDA.getAllRequestMapByType(""+load_type);
				arrayList = inventoryDA.getAllApproved(""+load_type);	
				pendingCount=inventoryDA.getPendingListCount(""+load_type);
				ebsApvdCount=inventoryDA.getEbsAprovedListCount(""+load_type);
				
			//	arrayList = new InventoryDA().getAllRequestByType(""+load_type);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						
						if(hmRequests!=null&&hmRequests.size()>0)
						{
						//	llItemHeader.setVisibility(View.VISIBLE);
							tvNoItemFound.setVisibility(View.GONE);
							
							//Log.d("Complaints", "hmItems are not null");
							int position = 0;

							if(pager!=null)
								pager.setCurrentItem(position);

							adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabs_vanSales,load_type,vehicleDO,isUnload);
							pager.setAdapter(adapter);
							tabs.setViewPager(pager);
							
							String trxStatus = LoadRequestDO.STATUS_PENDING;
							
							for(int i=0;i<tabs_vanSales.length;i++)
							{
								if(i == 0)
									trxStatus = LoadRequestDO.STATUS_PENDING;
								else if(i == 1)
									trxStatus = LoadRequestDO.STATUS_HISTORY;
								
								if(hmRequests!=null && hmRequests.get(trxStatus)!=null)
									tabs.setTabText(i, hmRequests.get(trxStatus).size());
								else
									tabs.setTabText(i,0);
							}
							pager.setCurrentItem(position);
							
							
						}
						else{
							//llItemHeader.setVisibility(View.GONE);
							tvNoItemFound.setVisibility(View.VISIBLE);
						}
						/*
						if(arrayList != null && arrayList.size() >0)
						{
							if(loadViewRequestAdapter != null)
								loadViewRequestAdapter.refreshList(arrayList);
						}*/
					}
				});
			}
		}).start();
	}
	private void initializeControles()
	{
		tvNoItemFound		= (TextView)llLoadRequestScreen.findViewById(R.id.tvNoItemFound);
		tvCode				= (TextView)llLoadRequestScreen.findViewById(R.id.tvCode);
		tvDate				= (TextView)llLoadRequestScreen.findViewById(R.id.tvDate);
		tvQty				= (TextView)llLoadRequestScreen.findViewById(R.id.tvQty);
		tvOrdersheetHeader	= (TextView)llLoadRequestScreen.findViewById(R.id.tvOrdersheetHeader);
		lvLoadRequest		= (ListView)llLoadRequestScreen.findViewById(R.id.lvLoadRequest);
		
		tabs 	 				= 	(PagerSlidingTabStrip)llLoadRequestScreen.findViewById(R.id.tabs);
		pager 					= 	(ViewPager)llLoadRequestScreen.findViewById(R.id.pager);
		
		btnAdd				= (Button)llLoadRequestScreen.findViewById(R.id.btnAdd);
		btnFinish			= (Button)llLoadRequestScreen.findViewById(R.id.btnFinish);
		btnContinue         = (Button)llLoadRequestScreen.findViewById(R.id.btnContinue);
		//btnStartDay			= (Button)llLoadRequestScreen.findViewById(R.id.btnStartDay);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		//pager.setOnPageChangeListener((OnPageChangeListener) LoadRequestActivity.this);
		//lvLoadRequest.setAdapter(loadViewRequestAdapter);
		tvNoItemFound.setVisibility(View.GONE);
		if(from.equalsIgnoreCase("isStockContinue")){
			btnAdd.setVisibility(View.GONE);
			btnContinue.setVisibility(View.VISIBLE);
			isEditable=true;
		}
		if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
		{
			tvCode.setText("Unload Req. Code");
			tvOrdersheetHeader.setText("Unload View Request");
		}
		else
		{
			tvCode.setText("Load Req. Code");
			tvOrdersheetHeader.setText("Load View Request");
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		loadData();
		
	}
	
	private class LoadViewRequestAdapter extends BaseAdapter
	{
		private ArrayList<LoadRequestDO> arrayList;
		public LoadViewRequestAdapter(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
		}

		public void refreshList(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount()
		{
			if(arrayList != null && arrayList.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				return arrayList.size();
			}
			else
				tvNoItemFound.setVisibility(View.VISIBLE);
			
			return 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final LoadRequestDO loadRequestDO = arrayList.get(position);
		
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.item_load_view_stock, null);
			
			TextView tvCode		= (TextView)convertView.findViewById(R.id.tvCode);
			TextView tvDate		= (TextView)convertView.findViewById(R.id.tvDate);
			TextView tvQty		= (TextView)convertView.findViewById(R.id.tvQty);
			View sideView		= (View)convertView.findViewById(R.id.sideView);
			
			sideView.setVisibility(View.VISIBLE);
			
			tvCode.setText(loadRequestDO.MovementCode);
			
			if(loadRequestDO.MovementDate.contains("-"))
				tvDate.setText(CalendarUtils.getFormatedDatefromString(loadRequestDO.MovementDate));
			else
				tvDate.setText(CalendarUtils.getFormatedDatefromString_(loadRequestDO.MovementDate));
			
			tvQty.setText(""+loadRequestDO.MovementStatus);
			
			if(!loadRequestDO.Status.equalsIgnoreCase("N"))
				sideView.setBackgroundColor(getResources().getColor(R.color.customer_served));
			else
				sideView.setBackgroundColor(getResources().getColor(R.color.red));
			
			if(loadRequestDO.MovementType.equalsIgnoreCase(""+AppStatus.UNLOAD_COLLECTED_STOCK)){
				convertView.setBackgroundColor(getResources().getColor(R.color.light_blue));
			}
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(LoadRequestActivity.this, AddNewLoadRequest.class);
					intent.putExtra("load_type", load_type);
					intent.putExtra("object", loadRequestDO);
					intent.putExtra("isEditable", isEditable);
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("isUnload", isUnload);
					startActivity(intent);
				}
			});
			setTypeFace((ViewGroup) convertView);
			return convertView;
		}
	}
	
	public class CategoryPagerAdapter extends FragmentStatePagerAdapter 
	{
		private  String[] tabsName;
		private int loadtype;
        private VehicleDO vehicleDO;
        private boolean isUnload;

		public CategoryPagerAdapter(FragmentManager fm,String[] tabsName,int loadType,VehicleDO vehicleDO,boolean isUnload)
		{
			super(fm);
			this.tabsName = tabsName;
			this.loadtype=loadType;
			this.vehicleDO=vehicleDO;
			this.isUnload=isUnload;
		}

		public void refresh() 
		{
			notifyDataSetChanged();
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(tabsName != null && tabsName.length > 0)
				return tabsName[position];

			return "N/A";
		}

		@Override
		public int getCount() 
		{
			if(tabsName == null || tabsName.length <= 0)
				return 0;
			return tabsName.length;
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position)
		{
			String trxStatus = LoadRequestDO.STATUS_PENDING;
			if(position == 0)
				trxStatus = LoadRequestDO.STATUS_PENDING;
			else if(position == 1)
				trxStatus = LoadRequestDO.STATUS_HISTORY;
		//	Log.d("Compalints", hmComplaintItems.size()+"");

			return new ListDisplayRequestsFragments(LoadRequestActivity.this, position, hmRequests.get(trxStatus),loadtype,vehicleDO,isUnload,isEditable);
		
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
	}
	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("pendingskip"))
		{
			showSkipReason();
		}
	}
	private void showSkipReason() 
	{
		   vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_EbsLOad_SKIP);
		CustomBuilder builder = new CustomBuilder(LoadRequestActivity.this, "Select Reason", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
	    		builder.dismiss();
	    		new Thread(new Runnable() 
	    		{
					@Override
					public void run() 
					{
						objPostReasonDO 			   = new PostReasonDO();
						objPostReasonDO.customerSiteID = preference.getStringFromPreference(preference.CUSTOMER_SITE_ID, "");
						objPostReasonDO.presellerId    = ""+preference.getStringFromPreference(preference.EMP_NO, "");
						objPostReasonDO.reason         = ""+objNameIDDo.strName;
						objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
						objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
						objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
						vecPostReasons.add(objPostReasonDO);

						if(vecPostReasons != null && vecPostReasons.size() > 0)
						{
							new CommonDA().insertAllReasons(vecPostReasons);
							uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								doContinue();
							}
						});
					}
				}).start();
		    }
	   }); 
		builder.show();
	}
	
	private void doContinue() 
	{
		 if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0){
			
			Intent intent = new Intent(LoadRequestActivity.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			intent.putExtra("image_path", "");
			intent.putExtra("image_path_driver", "");
			startActivity(intent);
			setResult(1111);
			finish();
		}else{
			Intent intent=new Intent(LoadRequestActivity.this,SalesmanCustomerList.class);
			startActivity(intent);
			finish();
		}
		
		/*	String verfiedDate = preference.getStringFromPreference(com.winit.baskinrobbin.salesman.common.Preference.IsStockVerifiedToday,"");
		if(CalendarUtils.getOrderPostDate().equalsIgnoreCase(verfiedDate)){
			Intent intent=new Intent(LoadRequestActivity.this,SalesmanCustomerList.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(EbsApprovedActivity.this, VerifyOnHandStock.class);
			intent.putExtra("updatededDate", arrayList);
			intent.putExtra("isMenu", false);
			startActivityForResult(intent, 1111);
			finish();
			
			Intent intent = new Intent(LoadRequestActivity.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			intent.putExtra("image_path", filepath);
			intent.putExtra("image_path_driver", filepathDriver);
			startActivity(intent);
			setResult(1111);
			finish();
		}	*/
	}
}
