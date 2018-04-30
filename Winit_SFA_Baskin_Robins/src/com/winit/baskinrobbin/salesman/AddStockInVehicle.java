package com.winit.baskinrobbin.salesman;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.VerifyUnloadApproved;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.GetOrdersToUpload;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class AddStockInVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader;
	private Button btnOrdersheetVerify,btnOrdersheetReport, btnAdd, btnLoadReq, btnUnloadReq, btnStartDay;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private EditText etSearch;
	private TextView tvItemCode, tvDescription, tvUOM, tvQty, tvOrdersheetHeader, tvNoItemFound, tvItemList,
					 tvTotalQt;
	private ImageView ivCheckAllItems;
	private Paint mPaint;
	private DecimalFormat decimalFormat;
	private boolean isMenu = false;
	private VehicleDO vehicleDO;
	private ListView lvItemList; 
	private OrderSheetAdapter ordersheetadapter;
	private int precision=0;
	//private boolean isStartDay=false;
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.add_stock_inventory, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras() != null)
		{
			isMenu = getIntent().getExtras().getBoolean("isPreview");
		}
		if(getIntent().getExtras().containsKey("object"))
		{
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
		}else
			if(getIntent().getExtras().containsKey(Preference.VEHICLE_DO)){
				vehicleDO = (VehicleDO) getIntent().getExtras().get(Preference.VEHICLE_DO);
			}
		preference.saveVehicleObjectInPreference(Preference.VEHICLE_DO, vehicleDO);
		preference.commitPreference();
		
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		tvOrdersheetHeader.setText("Stock Loading");
		
		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setMinimumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);
		
		lvItemList.setVerticalScrollBarEnabled(false);
		lvItemList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvItemList.setCacheColorHint(0);
		lvItemList.setFadingEdgeLength(0);
		lvItemList.setAdapter(ordersheetadapter = new OrderSheetAdapter(new ArrayList<VanLoadDO>()));
		llOrderListView.addView(lvItemList, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		btnLoadReq.setVisibility(View.VISIBLE);
		btnUnloadReq.setVisibility(View.VISIBLE);
		btnLoadReq.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(AddStockInVehicle.this, LoadRequestActivity.class);
				intent.putExtra("load_type", AppStatus.LOAD_STOCK);
				intent.putExtra("object", vehicleDO);
				startActivity(intent);
			}
		});
		
		btnUnloadReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				showLoader("loading....");
//				final VehicleDO vehicle=preference.getVehicleObjectFromPreference(preference.VEHICLE_DO);
				new Thread(new Runnable()
				{
					public void run()
					{
						try {
							//Hit the service to check if the salesman can unload or not
//							BooleanParser booleanParser = new BooleanParser(AddStockInVehicle.this);
//							new ConnectionHelper(null).sendRequest(AddStockInVehicle.this,BuildXMLRequest.VerifyIsUnloadApproved(preference.getStringFromPreference(preference.EMP_NO, ""),vehicle.VEHICLE_NO), booleanParser, ServiceURLs.CompareERPVsMWStockByVehicleCodeResponse,preference);
//							int resposne = (Integer) booleanParser.getData();
//							if(resposne==1){
//								hideLoader();
								Intent intent = new Intent(AddStockInVehicle.this, ReturnStockOption.class);
								intent.putExtra("isNormal", true);
								startActivity(intent);
//							}
//							else{
//								hideLoader();
//								showCustomDialog(AddStockInVehicle.this, getString(R.string.warning), booleanParser.getServerMessage(), getString(R.string.OK), null, "");
//							}
						
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							hideLoader();
						}
					}
				}).start();
				
				
			}
		});

		btnOrdersheetVerify.setText("Continue ");
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				
//				if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0) {
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							// final ArrayList<LoadRequestDO> arrayList = new InventoryDA().getAllApproved(""+AppStatus.LOAD_STOCK);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									Intent intent=new Intent(AddStockInVehicle.this,LoadRequestActivity.class);
									intent.putExtra("load_type", AppStatus.LOAD_STOCK);
									intent.putExtra("from", "isStockContinue");
									intent.putExtra("object", vehicleDO);
									startActivity(intent);
									/*if(arrayList != null && arrayList.size() >0)
									{
//										intent=new Intent(AddStockInVehicle.this,EbsApprovedActivity.class);
										intent=new Intent(AddStockInVehicle.this,LoadRequestActivity.class);
										intent.putExtra("load_type", AppStatus.LOAD_STOCK);
										intent.putExtra("from", "isStockContinue");
										intent.putExtra("object", vehicleDO);
										startActivity(intent);
										
									}*/
								}
							});
						}
					}).start();
     	}
		});
		btnOrdersheetReport.setText("Print");
		btnOrdersheetReport.setVisibility(View.GONE);
		
		btnStartDay.setText("Refresh");
		btnStartDay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isNetworkConnectionAvailable(AddStockInVehicle.this))
					showCustomDialog(AddStockInVehicle.this,"Alert !", getString(R.string.no_internet), "OK", null, "");
				else
				{
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							String empNo 		= preference.getStringFromPreference(Preference.EMP_NO, "");
							
							final boolean isAllUploaded = new GetOrdersToUpload(AddStockInVehicle.this, preference, AppStatus.TODAY_DATA).uploadOrders(empNo);
							
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
										showCustomDialog(AddStockInVehicle.this,"Alert !", "Please upload the data from settings before making another sync request.", "OK", null, "");
									}
									else	
										loadData();
								}
							});
						}
					}).start();
				}
			}
		});
		
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString()!=null || s.length() > 0)
				{
					ArrayList<VanLoadDO> vecTemp = new ArrayList<VanLoadDO>();
					for(int index = 0; vecOrdProduct != null && index < vecOrdProduct.size(); index++)
					{
						VanLoadDO obj = vecOrdProduct.get(index);
						String strText =  obj.ItemCode;
						String strDes =  ""+obj.Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(obj);
					}
					ordersheetadapter.refresh(vecTemp);
				}
				else if(ordersheetadapter!= null)
					ordersheetadapter.refresh(vecOrdProduct);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		setTypeFace(llOrderSheet);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting id's
		
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		btnAdd					=	(Button)llOrderSheet.findViewById(R.id.btnAdd);
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		etSearch				=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		
		tvTotalQt				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQt);
	
		//tvTotalQt.setVisibility(View.GONE);
		
		tvDescription			=	(TextView)llOrderSheet.findViewById(R.id.tvDescription);
		tvUOM					=	(TextView)llOrderSheet.findViewById(R.id.tvUOM);
		tvQty					=	(TextView)llOrderSheet.findViewById(R.id.tvQty);
		
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		
		btnLoadReq				=	(Button)llOrderSheet.findViewById(R.id.btnLoadReq);
		btnUnloadReq			=	(Button)llOrderSheet.findViewById(R.id.btnUnloadReq);
		btnStartDay				=	(Button)llOrderSheet.findViewById(R.id.btnStartDay);
		btnStartDay.setVisibility(View.GONE);
		TextView tvUsername		=	(TextView)llOrderSheet.findViewById(R.id.tvUsername);
		TextView tvDate			=	(TextView)llOrderSheet.findViewById(R.id.tvDate);
		TextView tvVehicleCode	=	(TextView)llOrderSheet.findViewById(R.id.tvVehicleCode);
		
		lvItemList = new ListView(this);
		
		btnLoadReq.setVisibility(View.VISIBLE);
		btnUnloadReq.setVisibility(View.VISIBLE);
		btnAdd.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		ivCheckAllItems.setVisibility(View.GONE);
		btnStartDay.setVisibility(View.VISIBLE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        tvUOM.setText("UOM");
        
        setActionInViews(btnLogo, false);
		setActionInViews(llMenu, false);
		setActionInViews(btnMenu, false);
		
		btnMenu.setVisibility(View.INVISIBLE);
		
	    tvUsername.setText(""+preference.getStringFromPreference(Preference.USER_NAME, ""));
		tvDate.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
		tvVehicleCode.setText("Vhcl.Code : "+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, ""));
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("stock"))
		{
			updateStock();
		}
		else if(from.equalsIgnoreCase("verify"))
		{
//			Intent intent = new Intent(AddStockInVehicle.this, VerifyItemInVehicle.class);
//			intent.putExtra("isMenu", isMenu);
//			startActivityForResult(intent, 1111);
			Intent intent = new Intent(AddStockInVehicle.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			intent.putExtra("image_path", "");
			intent.putExtra("image_path_driver", "");
			startActivity(intent);
			setResult(1111);
			finish();
		}
		else if(from.equalsIgnoreCase("Empty")){
			Intent intent=new Intent(AddStockInVehicle.this,SalesmanCustomerList.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		loadData();
	}
	
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
			    setStartdayPreference();
				AppConstants.vecCategories 	= 	new CategoriesDA().getAllCategory();
				precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				vecOrdProduct				=	new VehicleDA().getAllItemToVerify(precision);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						ordersheetadapter.refresh(vecOrdProduct);
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			VanLoadDO dco = (VanLoadDO) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
				ordersheetadapter.refresh(vecOrdProduct);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	ArrayList<VanLoadDO> vecUpdatedData;
	private void updateStock()
	{
		ordersheetadapter.refresh(vecOrdProduct);
		
		if(vecUpdatedData != null && vecUpdatedData.size()> 0)
			updateValues();
		else
			showCustomDialog(AddStockInVehicle.this, getResources().getString(R.string.warning), "Error occured while verifying. Please try again.", getResources().getString(R.string.OK), null, "");
	}
	
	private void updateValues()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						
						Intent intent = new Intent(AddStockInVehicle.this, OdometerReadingActivity.class);
						intent.putExtra("isStartDay", true);
						startActivity(intent);
						setResult(1111);
						finish();
					}
				});
			}
		}).start();
	}
	
	private boolean checkPermission() {
		VerifyUnloadApproved vesrifyUnloadParser = new VerifyUnloadApproved(AddStockInVehicle.this);
		new ConnectionHelper(null).sendRequest(AddStockInVehicle.this,BuildXMLRequest.VerifyIsUnloadApproved(preference.getStringFromPreference(preference.EMP_NO, ""),preference.getStringFromPreference(preference.CURRENT_VEHICLE, "")), vesrifyUnloadParser, ServiceURLs.CompareERPVsMWStockByVehicleCodeResponse,preference);
		return vesrifyUnloadParser.getStatus();
	}
	public class OrderSheetAdapter extends BaseAdapter
	{
		private ArrayList<VanLoadDO> vecOrder;
		public OrderSheetAdapter(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder=vec;
		}
		
		@Override
		public int getCount() 
		{
			if(vecOrder != null && vecOrder.size() > 0)
				return vecOrder.size();
			else
				return 0;
		}
		
		@Override
		public Object getItem(int position) 
		{
			return position;
		}
		
		@Override
		public long getItemId(int position) 
		{
			return 0;
		}
		
		public void refresh(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder = vec;
			notifyDataSetChanged();
			if(vecOrder != null && vecOrder.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				llItemHeader.setVisibility(View.VISIBLE);
			}
			else
			{
				tvNoItemFound.setVisibility(View.VISIBLE);
				llItemHeader.setVisibility(View.GONE);
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final VanLoadDO vanLoadDO = vecOrder.get(position);
			
			if(convertView == null)
				convertView				= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_add_stock,null);
			
			TextView tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
			TextView evUOM				= (TextView)convertView.findViewById(R.id.etUOM);
			EditText etQt				= (EditText)convertView.findViewById(R.id.etQt);
			TextView tvExpiryDate		= (TextView)convertView.findViewById(R.id.tvExpiryDate);
			EditText etTotalQt			= (EditText)convertView.findViewById(R.id.etTotalQt);
			TextView tvItemBatchCode	= (TextView)convertView.findViewById(R.id.tvItemBatchCode);
			ImageView ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
			etTotalQt.setClickable(false);
			etTotalQt.setEnabled(false);
			ivAcceptCheckItems.setVisibility(View.GONE);
			tvProductKey.setText(vanLoadDO.ItemCode);
			tvVendorName.setText(vanLoadDO.Description);
			
			if(vanLoadDO.BatchCode != null && vanLoadDO.BatchCode.length() > 0)
				tvItemBatchCode.setText(vanLoadDO.BatchCode);
		//	tvItemBatchCode.setText("Batch No.: "+vanLoadDO.BatchCode);
			else
				tvItemBatchCode.setText("Batch No.: N/A");
			
//			if(vanLoadDO.ExpiryDate != null && vanLoadDO.ExpiryDate.length() > 0)
//				tvExpiryDate.setText("Expiry Date: "+CalendarUtils.getFormatedDeliverydate(vanLoadDO.ExpiryDate.split("T")[0]));
//			else
//				t vExpiryDate.setText("Expiry Date: "+"N/A");
			
			tvExpiryDate.setVisibility(View.VISIBLE);
			tvExpiryDate.setText("Total Qty: "+StringUtils.round(""+(vanLoadDO.SellableQuantity+vanLoadDO.pendingQuantity),precision));
		//	etTotalQt.setVisibility(View.GONE);
		//	if(vanLoadDO.SellableQuantity > 0){
				
				//etTotalQt.setText(""+diffStock.format(vanLoadDO.SellableQuantity));
				etTotalQt.setText(""+StringUtils.round(""+(vanLoadDO.SellableQuantity),precision));
		//	}
				
			/*else
				etTotalQt.setText("0");*/
			if(vanLoadDO.pendingQuantity > 0)
				etQt.setText(""+StringUtils.round(""+(vanLoadDO.pendingQuantity), precision));
			else
				etQt.setText("0");
			
			etQt.setEnabled(false);
			etQt.setClickable(false);
			etQt.setFocusable(false);
			
			evUOM.setText(""+vanLoadDO.UOM);
			evUOM.setEnabled(false);
			evUOM.setFocusable(false);
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					
				}
			});
			
			setTypeFace((LinearLayout)convertView);
			return convertView;
		}
	}
	private void setStartdayPreference(){
		final int isDayStarted = new JourneyPlanDA().isDayStarted(preference.getStringFromPreference(preference.USER_ID, ""));
			preference.saveIntInPreference(Preference.STARTDAY_VALUE, isDayStarted);
			if(isDayStarted >0)
				preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
			else
				preference.saveBooleanInPreference(Preference.IS_EOT_DONE, true);
			preference.commitPreference();
	}
	
}
