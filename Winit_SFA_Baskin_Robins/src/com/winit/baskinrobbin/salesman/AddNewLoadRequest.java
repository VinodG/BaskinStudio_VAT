package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.google.zxing.client.android.CaptureActivity;
import com.winit.baskinrobbin.salesman.AddNewLoadRequest.InventoryItemAdapter.TextChangeListener;
import com.winit.baskinrobbin.salesman.adapter.AddItemVanAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class AddNewLoadRequest extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader, llTopLayout, llSelectWereHouse, llSelectVehicle, llRequeredDate;
	private Button btnOrdersheetVerify,btnAdd, btnScan, btnOrdersheetReport;
	private InventoryItemAdapter inventoryItemAdapter;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView tvItemCode, tvDescription, tvUOM, tvQty, tvTotalQt, tvOrdersheetHeader, tvNoItemFoundBase, tvItemList,
	                 tvTotalQty, tvTotalAmount, tvRequiredDate, tvRequestedDate, tvSelectVehicle, tvSelectWereHouse, tvWareHouseTitle;
	private ImageView ivCheckAllItems;
	private Vector<String> vecCategory;
	private Vector<VanLoadDO> vecSearchedItemd;
	private Paint mPaint;
	private AddItemVanAdapter adapter;
	private ScrollView svLoadStock;
	private int load_type = -1;
	private LoadRequestDO loadRequestDO;
	private HashMap<String, HHInventryQTDO> hmInventory, hmReturnInventory, hmWarehouseQty;
	private HashMap<String, Vector<String>> hmAddedItems;
	private boolean isOpen = true,isFromItemClick =false,isaddRequest=false,isEditable=false,isUnload;
	private Vector<NameIDDo> vecWareHouse, vecVehicle;
	private VehicleDO vehicleDO;
	private final int DATE_DIALOG_ID = 0;
	private static String isfrom = "";
	HashMap<String, ArrayList<LoadRequestDO>> hmRequests;
	private String deviceID = "";
	private OfflineDA offlineDA=new OfflineDA();
	private int precision=0,time=0;
	private boolean isValidShip=true;
	private ListView lvStocks;
	private int listScrollState;
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.load_request, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		Bundle bundle  = getIntent().getExtras();
		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceID = telephonyManager.getDeviceId();
		
		if(bundle != null)
		{
			load_type	 	= 	bundle.getInt("load_type");
			loadRequestDO	= 	(LoadRequestDO)bundle.get("object");
			isfrom          = bundle.getString("isfrom");
			isFromItemClick = bundle.getBoolean("isFromItemClick");
			isUnload= bundle.getBoolean("isUnload");
			isEditable= bundle.getBoolean("isEditable");
			isaddRequest    = bundle.getBoolean("isaddRequest");
			vehicleDO=(VehicleDO) getIntent().getExtras().get("vehicle");
		}
		intialiseControls();
		
		if(isUnload && loadRequestDO!=null && loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_EBSAPPROVED) && load_type!=AppStatus.UNLOAD_COLLECTED_STOCK){
			btnOrdersheetVerify.setText("Continue");
		}
		else if(loadRequestDO!=null && ((loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_SHIPPED))
			|| (loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_PENDING_FROM_ERP)))){
			btnOrdersheetVerify.setText("Finish");
		}
		else if(isaddRequest){
			btnOrdersheetVerify.setText("Submit");
		}
		else if((loadRequestDO!=null && loadRequestDO.IsVarified!=null && !loadRequestDO.IsVarified.equalsIgnoreCase("1") && loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_PENDING_MW)) &&( load_type==AppStatus.UNLOAD_S_STOCK || load_type==AppStatus.UNLOAD_STOCK)){
			btnOrdersheetVerify.setText("Finish");
		}
		else if((isEditable && loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_EBSAPPROVED))|| load_type==AppStatus.UNLOAD_S_STOCK || load_type==AppStatus.UNLOAD_STOCK){
			btnOrdersheetVerify.setText("Continue");
		}
		else{
			btnOrdersheetVerify.setText("Finish");
		}
//		
		if(loadRequestDO == null)
			tvOrdersheetHeader.setText("Add New Request");
		else
			tvOrdersheetHeader.setText("Request Code: - "+loadRequestDO.MovementCode+" ("+loadRequestDO.MovementStatus+")");
		
		svLoadStock.setVisibility(View.GONE);
		tvNoItemFoundBase.setVisibility(View.VISIBLE);
		
		if(load_type != AppStatus.LOAD_STOCK && loadRequestDO == null)
		{
			tvOrdersheetHeader.setText("Unload Items");
			tvNoItemFoundBase.setText("No items to display.");
		}
		else
			tvNoItemFoundBase.setText("Please tap on Add Item button to add items for load request.");
	
		inventoryItemAdapter = new InventoryItemAdapter(new ArrayList<VanLoadDO>());
		
//		lvStocks = new CustomListView(AddNewLoadRequest.this);
//		llOrderListView.addView(lvStocks,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		lvStocks.setCacheColorHint(0);
		lvStocks.setVerticalFadingEdgeEnabled(false);
		lvStocks.setVerticalScrollBarEnabled(false); 
		lvStocks.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvStocks.setAdapter(inventoryItemAdapter);
		
		lvStocks.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				listScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		lvStocks.setVisibility(View.VISIBLE);
		svLoadStock.setVisibility(View.GONE);
		if(loadRequestDO != null)
		{
			llTopLayout.setVisibility(View.GONE);
			llSelectVehicle.setVisibility(View.GONE);
		}
		else
		{
			tvTotalQt.setVisibility(View.GONE);
			llTopLayout.setVisibility(View.VISIBLE);
		}
		
		//showing Loader
		showLoader(getResources().getString(R.string.loading));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				String strVehicleNumber = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				time=new SettingsDA().getSettingsByName(AppConstants.SyncSeconds);
				vecVehicle				= new CommonDA().getAllSubInventories(strVehicleNumber, load_type);
				vecWareHouse			= new CommonDA().getAllTRXTYPEs();
				
				new CategoriesDA().getCategoryList();
				hmInventory 		= 	new OrderDetailsDA().getAvailInventoryQtys_Temp("",precision,load_type);
				//hmInventory 		= 	new OrderDetailsDA().getAvailInventoryQtys_Temp("",precision);
				
				if(load_type == AppStatus.UNLOAD_STOCK)
					hmReturnInventory = new OrderDetailsDA().getNonSealableInventoryQtys_Temp("",precision);
				if(loadRequestDO != null)
					hmWarehouseQty = new OrderDetailsDA().getWarehouseStock(""+loadRequestDO.SourceVehicleCode);
				
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						if((load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK) && loadRequestDO == null)
						{
							btnAdd.setVisibility(View.GONE);
							btnScan.setVisibility(View.GONE);
							loadUnLaodData();
							
							if(vecVehicle != null && vecVehicle.size() > 0)
								tvSelectVehicle.performClick();
							else
								showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "There no return sub-inventory mapped. Please contact administrator.", "OK", null, "Alert");
						}
						else
						{
							new Thread(new Runnable() 
							{
								@Override
								public void run() 
								{
									if(loadRequestDO != null)
//										vecOrdProduct	=	new InventoryDA().getAllItemToVerifyByLoadId(loadRequestDO.MovementCode,load_type,hmInventory);
										vecOrdProduct	=	new InventoryDA().getAllItemToVerifyByLoadId(loadRequestDO.MovementCode,load_type/*,hmInventory*/);

									runOnUiThread(new Runnable() 
									{
										@Override
										public void run() 
										{
											if(vecOrdProduct != null && vecOrdProduct.size() > 0)
											{
												tvSelectVehicle.setText(loadRequestDO.SourceVehicleCode);
//												inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
												inventoryItemAdapter.refresh(vecOrdProduct);
//												svLoadStock.setVisibility(View.VISIBLE);
												tvNoItemFoundBase.setVisibility(View.GONE);
											}
											else
											{
												if(vecVehicle != null && vecVehicle.size() > 0)
													tvSelectVehicle.performClick();
												else
													showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "There no sub-inventory mapped. Please contact administrator.", "OK", null, "Alert");
											}
											
											hideLoader();
										}
									});
								}
							}).start();
						}
					}
				});
			}
		}).start();
		
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Refresh"))
					loadRequestStatus();
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Finish"))
					finish();
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Continue"))
				{
					
					boolean unloadStatus = true;
//					if(isUnload)
//						unloadStatus = checkItemAvailability();
					
					if(!unloadStatus)
					{	if(customDialog!=null && customDialog.isShowing())
						customDialog.dismiss();
						showCustomDialog(AddNewLoadRequest.this,getString(R.string.alert),"Unload Requested quantity is not available for following items \n "+unloadMessage+" "+" \n in your inventory \n Do you want to continue? \n",getString(R.string.Yes),getString(R.string.No),"vanunload");
					}else{
						if(load_type==AppStatus.LOAD_STOCK){
							isValidShip=validateShipQty(vecOrdProduct);
						}
						if(isValidShip){
							Intent intent = new Intent(AddNewLoadRequest.this, VerifyItemInVehicle.class);
							intent.putExtra("object", vehicleDO);
							intent.putExtra("Source", loadRequestDO.DestinationVehicleCode);
							intent.putExtra("Destination", loadRequestDO.SourceVehicleCode);
							intent.putExtra("movementId", loadRequestDO.MovementCode);
							intent.putExtra("movementType", loadRequestDO.MovementType);
							intent.putExtra("load_type", load_type);
							if(load_type == AppStatus.UNLOAD_STOCK)
								intent.putExtra("isUnload", true);
							else
								intent.putExtra("isUnload", false);
							intent.putExtra("isMenu", false);
							startActivityForResult(intent, 1111);
							finish();
						}else{
							showCustomDialog(AddNewLoadRequest.this, getString(R.string.alert), getString(R.string.No_approved_qty), getString(R.string.refresh), null, "Refresh");
						}
						
					}
				}
				else if(vecOrdProduct != null && vecOrdProduct.size() > 0)
				{
					if(load_type == AppStatus.LOAD_STOCK||load_type == AppStatus.UNLOAD_S_STOCK){
						if(validateItems(vecOrdProduct) && !tvSelectVehicle.getText().toString().equalsIgnoreCase("")){
							insertUpdateRequest();
							
						}
						else if(tvSelectVehicle.getText().toString().equalsIgnoreCase(""))
							showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select warehouse.", "OK", null, "");
						else
							showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item having quantity greater than zero.", "OK", null, "");
					}
					else{
						if(validateUnloadItems(vecOrdProduct) && !tvSelectVehicle.getText().toString().equalsIgnoreCase(""))
							insertUpdateRequest();
						else if(tvSelectVehicle.getText().toString().equalsIgnoreCase(""))
							showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select warehouse.", "OK", null, "");
						else
							showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select all the items.", "OK", null, "");
						}
				}
				else
				{
					if(load_type == AppStatus.LOAD_STOCK)
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item for load request.", "OK", null, "");
					else
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item for unload request.", "OK", null, "");
				}
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
		
		if(vecOrdProduct == null)
			vecOrdProduct = new ArrayList<VanLoadDO>();
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnMenu.setVisibility(View.INVISIBLE);
		
		tvSelectVehicle.setTag(-1);
		tvSelectVehicle.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				String title = "";
				
				if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
					title = "Select To Warehouse";
				else
					title = "Select From Warehouse";
				
				CustomBuilder builder = new CustomBuilder(AddNewLoadRequest.this, title, false);
				builder.setSingleChoiceItems(vecVehicle, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, final Object selectedObject) 
					{
						builder.dismiss();
						showLoader("Loading data...");
						
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								final NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
								
								if(load_type  == AppStatus.LOAD_STOCK)
									hmWarehouseQty = new OrderDetailsDA().getWarehouseStock(""+ObjNameIDDo.strName);
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										tvSelectVehicle.setText(""+ObjNameIDDo.strName);
										tvSelectVehicle.setTag(ObjNameIDDo);
										
										if(isOpen && load_type == AppStatus.LOAD_STOCK)
											showAddNewSkuPopUp();
										
										if(inventoryItemAdapter != null)
											inventoryItemAdapter.refresh(vecOrdProduct);
//											inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
										
										hideLoader();
									}
								});
								
							}
						}).start();
		    		}
			   }); 
			   builder.show();
			}
		});
		
		tvRequiredDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		tvSelectWereHouse.setTag(-1);
		tvSelectWereHouse.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(AddNewLoadRequest.this, "Select Warehouse", true);
				builder.setSingleChoiceItems(vecWareHouse, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
						tvSelectWereHouse.setText(""+ObjNameIDDo.strName);
						tvSelectWereHouse.setTag(ObjNameIDDo);
						builder.dismiss();
		    		}
			   }); 
			   builder.show();
			}
		});
		
		if(load_type == AppStatus.LOAD_STOCK)
		{
			hmAddedItems = new HashMap<String, Vector<String>>();
			tvWareHouseTitle.setText("Select From Warehouse:");
			tvSelectVehicle.setHint("Select From Warehouse");
		}
		else
		{
			tvWareHouseTitle.setText("Select To Warehouse:");
			tvSelectVehicle.setHint("Select To Warehouse");
		}
		
		btnScan.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				AppConstants.objScanResultObject = null;
				Intent intent	=	new Intent(AddNewLoadRequest.this, CaptureActivity.class);
				startActivityForResult(intent, AppStatus.REQUEST_CODE);
			}
		});
		
		if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
			llRequeredDate.setVisibility(View.GONE);
		
		btnOrdersheetReport.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final ArrayList<VanLoadDO> vecTemp = (ArrayList<VanLoadDO>) vecOrdProduct.clone();
//						for(VanLoadDO vanLoadDO : vecTemp)
//							vanLoadDO.etQty = null;
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									hideLoader();
									ShowOptionPopupForPrinter(AddNewLoadRequest.this,new BaseActivity.PrintPopup() {
										@Override
										public void selectedOption(int selectedPrinter)
										{

											Intent intent=null;
											if(selectedPrinter== AppConstants.CANCEL)
											{
												hideLoader();

											}
											else
											{
												if(selectedPrinter== AppConstants.WOOSIM)
													intent=new Intent(AddNewLoadRequest.this, WoosimPrinterActivity.class);
												else if(selectedPrinter==AppConstants.DOTMATRIX)
													intent=new Intent(AddNewLoadRequest.this, PrinterConnectorArabic.class);
												intent.putExtra("array", vecTemp);
												intent.putExtra("loadRequestDO", loadRequestDO);
												intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_LOAD_REQUEST);
												startActivityForResult(intent, 1000);
											}
										}
									});
//									Intent intent = new Intent(AddNewLoadRequest.this, WoosimPrinterActivity.class);
//									intent.putExtra("array", vecTemp);
//									intent.putExtra("loadRequestDO", loadRequestDO);
//									intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_LOAD_REQUEST);
//									startActivityForResult(intent, 1000);
								}
							});
					}
				}).start();
			}
		});
		
		setTypeFace(llOrderSheet);
	}
	
	private String unloadMessage="";
	private boolean checkItemAvailability()
	{
		unloadMessage = "";
		boolean flag = true;
		if(loadRequestDO!=null && vecOrdProduct!=null)
		{
			for(VanLoadDO vanLoadDO : vecOrdProduct)
				isInventoryAvailForUnload(vanLoadDO);
		}
		
		if(!TextUtils.isEmpty(unloadMessage))
			flag = false;
		
		return flag;
	}
	
	private boolean isInventoryAvailForUnload(VanLoadDO objItem)
	{
		boolean isAvail = false;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.ItemCode))
		{
			float availQty = hmInventory.get(objItem.ItemCode).totalQt;
			float sellableQty = objItem.SellableQuantity;
			if(objItem.inProccessQty>0){
				sellableQty = objItem.inProccessQty;
			}
			if(sellableQty> availQty)
			{
				isAvail = false;
				unloadMessage = unloadMessage+objItem.ItemCode+",";
			}
			else
				isAvail = true;
		}
		else
		{
			isAvail = false;
			unloadMessage = unloadMessage+objItem.ItemCode+",";
		}
		return isAvail;
	}

	protected void insertUpdateRequest() 
	{
		showLoader("Uploading...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				InventoryDA inventoryDA = new InventoryDA();
				
			//	String movementId = ""+inventoryDA.getMovementId(preference.getStringFromPreference(Preference.EMP_NO, ""));
				String movementId = offlineDA.getNextSequenceNumber(OfflineDataType.MOVEMENT);
				if(movementId != null && !movementId.equals(""))
				{
					offlineDA.updateSequenceNumberStatus(movementId);
					loadRequestDO 				= 	new LoadRequestDO();
					loadRequestDO.MovementCode	=	""+movementId;
					loadRequestDO.PreMovementCode=	""+loadRequestDO.MovementCode;
					loadRequestDO.AppMovementId	=	""+StringUtils.getUniqueUUID();
					loadRequestDO.OrgCode		=	"DUB";
					loadRequestDO.UserCode		=	""+preference.getStringFromPreference(Preference.EMP_NO, "");
					loadRequestDO.WHKeeperCode	=	""+preference.getStringFromPreference(Preference.EMP_NO, "");
					loadRequestDO.CurrencyCode	=	""+preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
					loadRequestDO.JourneyCode	=	"DUB";
					loadRequestDO.MovementDate	= 	tvRequiredDate.getTag().toString() + "T"+CalendarUtils.getCurrentFormattedTime();
					
					loadRequestDO.MovementNote	=	"TEST";
					loadRequestDO.MovementType	=	""+load_type;
					
					if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
					{
						loadRequestDO.SourceVehicleCode	    =""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
						loadRequestDO.DestinationVehicleCode=""+tvSelectVehicle.getText().toString();
					}
					else
					{
						loadRequestDO.SourceVehicleCode	    =""+tvSelectVehicle.getText().toString();
						loadRequestDO.DestinationVehicleCode=""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					}
					
					loadRequestDO.Status		=	"N";
					loadRequestDO.VisitID		= 	"0";
					loadRequestDO.MovementStatus=	"99";
					//loadRequestDO.MovementStatus=	"Pending";
					loadRequestDO.CreatedOn		=	CalendarUtils.getCurrentDateTime();
					loadRequestDO.ApproveByCode	=	"0";
					loadRequestDO.ApprovedDate	=	loadRequestDO.MovementDate;
					loadRequestDO.JDETRXNumber	=	loadRequestDO.MovementCode;
					loadRequestDO.ISStampDate	=	loadRequestDO.CreatedOn;
					loadRequestDO.ISFromPC		=	"0";
					loadRequestDO.OperatorCode	=	"0";
					loadRequestDO.IsDummyCount	=	"0";
				//	loadRequestDO.Amount		=	getTotalQty();
					loadRequestDO.Amount		=	(float) StringUtils.round(getTotalQty()+"", precision);
					loadRequestDO.ModifiedDate	=	CalendarUtils.getCurrentDateTime();
					loadRequestDO.ModifiedTime	=	CalendarUtils.getRetrunTime();
					loadRequestDO.PushedOn		=	loadRequestDO.CreatedOn;
					loadRequestDO.ModifiedOn	=	loadRequestDO.CreatedOn;
					
					if(load_type == AppStatus.UNLOAD_STOCK)
						loadRequestDO.ProductType	=	"Non Sellable";
					else if(load_type == AppStatus.UNLOAD_S_STOCK)
						loadRequestDO.ProductType	=	"Sellable";
					else
						loadRequestDO.ProductType	=	"Load Sellable";
						
					loadRequestDO.vecItems 		= 	getVector(vecOrdProduct, loadRequestDO);
					
					inventoryDA.insertLoadRequest_(loadRequestDO);
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been submitted successfully.", "OK", null, "finish", false);
							uploadData(AppStatus.POST_LOAD_REQUEST, AppStatus.TODAY_DATA);
						}
					});
				}
				else
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							showCustomDialog(AddNewLoadRequest.this, "Alert !", "Sequence numbers are not synced properly. Please sync sequence numbers.", "OK", null, "");
						}
					});
				}
			}
			
		}).start();
	}
	
	private ArrayList<LoadRequestDetailDO> getVector(ArrayList<VanLoadDO> vecOrdProduct, LoadRequestDO loadRequestDO) 
	{
		int count = 1;
		ArrayList<LoadRequestDetailDO> vec = new ArrayList<LoadRequestDetailDO>();
		for(VanLoadDO object : vecOrdProduct)
		{
			if((load_type == AppStatus.LOAD_STOCK && object.SellableQuantity > 0) ||
			  (load_type != AppStatus.LOAD_STOCK && object.SellableQuantity > 0 && object.isSelected ))
			{
				LoadRequestDetailDO loDetailDO = new LoadRequestDetailDO();
				loDetailDO.LineNo			= 	""+count++;
				loDetailDO.MovementCode		=	""+loadRequestDO.MovementCode;
				loDetailDO.ItemCode			=	""+object.ItemCode;
				loDetailDO.OrgCode			=	""+loadRequestDO.OrgCode;
				loDetailDO.ItemDescription	=	""+object.Description;
				loDetailDO.ItemAltDescription=	""+object.Description;
				loDetailDO.MovementStatus	=	"Pending";
				loDetailDO.UOM				=	""+object.UOM;
				loDetailDO.QuantityLevel1	=	object.SellableQuantity;
				loDetailDO.QuantityLevel2	=	object.SellableQuantity;
				loDetailDO.QuantityLevel3	=	object.SellableQuantity;
				loDetailDO.QuantityBU		=	object.SellableQuantity;
				loDetailDO.QuantitySU		=	object.SellableQuantity;
				loDetailDO.NonSellableQty	=	0;
				loDetailDO.CurrencyCode		=	""+preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
				loDetailDO.PriceLevel1		=	0;
				loDetailDO.PriceLevel2		=	0;
				loDetailDO.PriceLevel3		=	0;
				loDetailDO.MovementReasonCode=	""+object.reason;;
				loDetailDO.ExpiryDate		=	""+object.ExpiryDate;
				loDetailDO.Note				=	"NONE";
				loDetailDO.AffectedStock	=	""+object.SellableQuantity;
				loDetailDO.Status			=	"Pending";
				loDetailDO.DistributionCode	=	""+loadRequestDO.OrgCode;;
				loDetailDO.CreatedOn		=	""+loadRequestDO.CreatedOn;;
				loDetailDO.ModifiedDate		=	""+loadRequestDO.ModifiedDate;
				loDetailDO.ModifiedTime		=	""+loadRequestDO.ModifiedTime;
				loDetailDO.PushedOn			=	""+loadRequestDO.PushedOn;
				loDetailDO.CancelledQuantity=	0;//object.SellableQuantity;
				loDetailDO.InProcessQuantity=	0;//object.SellableQuantity;
				loDetailDO.ShippedQuantity	=	0;//object.SellableQuantity;
				loDetailDO.ModifiedOn		= 	""+loadRequestDO.ModifiedOn;
				vec.add(loDetailDO);
			}
		}
		return vec;
	}

	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnAdd					=	(Button)llOrderSheet.findViewById(R.id.btnAdd);
		
		btnScan					=	(Button)llOrderSheet.findViewById(R.id.btnScan);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		
		tvTotalAmount			=	(TextView)llOrderSheet.findViewById(R.id.tvTotalAmount);
		tvTotalQty				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQty);
		
		tvDescription			=	(TextView)llOrderSheet.findViewById(R.id.tvDescription);
		tvUOM					=	(TextView)llOrderSheet.findViewById(R.id.tvUOM);
		tvQty					=	(TextView)llOrderSheet.findViewById(R.id.tvQty);
		tvTotalQt				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQt);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFoundBase		=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		llTopLayout				=	(LinearLayout)llOrderSheet.findViewById(R.id.llTopLayout);
		lvStocks				=	(ListView)llOrderSheet.findViewById(R.id.lvStocks);
		
		tvSelectVehicle			=	(TextView)llOrderSheet.findViewById(R.id.tvSelectVehicle);
		tvSelectWereHouse		=	(TextView)llOrderSheet.findViewById(R.id.tvSelectWereHouse);
		
		tvWareHouseTitle		=	(TextView)llOrderSheet.findViewById(R.id.tvWareHouseTitle);
		tvRequiredDate			=	(TextView)llOrderSheet.findViewById(R.id.tvRequiredDate);
		tvRequestedDate			=	(TextView)llOrderSheet.findViewById(R.id.tvRequestedDate);
		
		llSelectVehicle			=	(LinearLayout)llOrderSheet.findViewById(R.id.llSelectVehicle);
		llSelectVehicle.setVisibility(View.VISIBLE);
		
		llRequeredDate			=	(LinearLayout)llOrderSheet.findViewById(R.id.llRequeredDate);
		
		llSelectWereHouse		=	(LinearLayout)llOrderSheet.findViewById(R.id.llSelectWereHouse);
		llSelectWereHouse.setVisibility(View.GONE);
		
		svLoadStock				=	(ScrollView)llOrderSheet.findViewById(R.id.svLoadStock);
		ivCheckAllItems.setVisibility(View.GONE);
		llTopLayout.setVisibility(View.VISIBLE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        tvUOM.setText("UOM");
        
        if(loadRequestDO != null)
        {
        	btnOrdersheetReport.setText(" Print ");
        	if(loadRequestDO.MovementStatus != null && (loadRequestDO.MovementStatus.contains(""+LoadRequestDO.STATUS_SHIPPED) )){
        		btnOrdersheetReport.setVisibility(View.VISIBLE);
        	}else{
        		btnOrdersheetReport.setVisibility(View.GONE);
        	}
        	btnAdd.setVisibility(View.GONE);
        	btnScan.setVisibility(View.GONE);
        	if(loadRequestDO.MovementStatus != null && (loadRequestDO.MovementStatus.contains(""+AppConstants.COLLECTED_MOVEMENT_STATUS) || loadRequestDO.MovementStatus.contains("100")))
        	{
        		btnOrdersheetVerify.setText("Finish");
        		tvTotalQt.setVisibility(View.VISIBLE);
        		tvTotalQt.setText("Shpd Qty");
        	}
        	else if(loadRequestDO.MovementStatus != null && (loadRequestDO.MovementStatus.contains(""+AppConstants.APPROVED_MOVEMENT_STATUS) || loadRequestDO.MovementStatus.contains("101")))
        	{
        		btnOrdersheetVerify.setText("Continue");
        		tvTotalQt.setVisibility(View.VISIBLE);
        		tvTotalQt.setText("Apvd. Qty");
        	}
        	else
        	{
        		tvTotalQt.setVisibility(View.GONE);
        		btnOrdersheetVerify.setText("Refresh");
        	}
        }
        else
        {
        	btnOrdersheetVerify.setText("Submit ");
        	tvTotalQt.setVisibility(View.GONE);
        }
        
        tvRequiredDate.setText(CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
        tvRequiredDate.setTag(CalendarUtils.getOrderPostDate());
        tvRequestedDate.setText(CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
        
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("finish"))
		{
			setResult(1111);
			finish();
		}
		else if(from.equalsIgnoreCase("Alert"))
			finish();
		else if(from.equalsIgnoreCase("Refresh")){
			customDialog.dismiss();
			correctDetailErrors("Refreshing data...",loadRequestDO.MovementCode);
			finish();
		}
			
	}
	
	public void sortCategory()
	{
	}
	
	public void showAddNewSkuPopUp()
	{
		isOpen = false;
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(AddNewLoadRequest.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		objAddNewSKUDialog.show();
		
		TextView tvItemCodeLabel			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItemCodeLabel);
		TextView tvItem_DescriptionLabel	=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItem_DescriptionLabel);
		TextView tvAdd_New_SKU_Item			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvAdd_New_SKU_Item);
		
		final EditText etCategory	 		=	(EditText)objAddNewSKUDialog.findViewById(R.id.etCategory);
		final EditText etSearch	 			=	(EditText)objAddNewSKUDialog.findViewById(R.id.etSearch);
		
		final ImageView cbList 				=	(ImageView)objAddNewSKUDialog.findViewById(R.id.cbList);
		final ListView lvPopupList		 	=	(ListView)objAddNewSKUDialog.findViewById(R.id.lvPopupList);
		
		final LinearLayout llList			=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llList);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
		etSearch.setHint("Search by item code/ description");
		vecCategory = new Vector<String>();
		
		if(load_type == AppStatus.UNLOAD_STOCK)
			vecCategory = new CategoriesDA().getAvailableCategory();
		else
			for(int i=0; AppConstants.vecCategories!= null && i < AppConstants.vecCategories.size(); i++)
				vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
		final Button btnSearch = new Button(AddNewLoadRequest.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				etCategory.setClickable(false);
				etCategory.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						etCategory.setClickable(true);
						etCategory.setEnabled(true);
					}
				}, 500);
				
				CustomBuilder builder = new CustomBuilder(AddNewLoadRequest.this, "Select Category", true);
				builder.setSingleChoiceItems(vecCategory, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						String str = (String) selectedObject;
			    		builder.dismiss();
		    			builder.dismiss();
		    			((TextView)v).setText(str);
						((TextView)v).setTag(str);
						
						btnSearch.performClick();
				    }
				}); 
				builder.show();
			}
		});
		
		cbList.setTag("False");
		cbList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(adapter != null)
					adapter.selectAll(cbList);
			}
		});
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etSearch.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(AddNewLoadRequest.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
				else
				{
					showLoader(getResources().getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId  = "", catgName = etCategory.getText().toString();
							hmAddedItems   =  new HashMap<String, Vector<String>>();
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
							
							fillUOM();
							
							vecSearchedItemd = new ProductsDA().getProductsVanByCategoryId(catgId, load_type, hmAddedItems);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
									{
										cbList.setVisibility(View.VISIBLE);
										tvNoItemFound.setVisibility(View.GONE);
										lvPopupList.setAdapter(adapter = new AddItemVanAdapter(vecSearchedItemd,AddNewLoadRequest.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddItemVanAdapter( new Vector<VanLoadDO>(),AddNewLoadRequest.this));
										cbList.setVisibility(View.INVISIBLE);
										tvNoItemFound.setVisibility(View.VISIBLE);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					Vector<VanLoadDO> vecTemp = new Vector<VanLoadDO>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						VanLoadDO obj = (VanLoadDO) vecSearchedItemd.get(index);
						String strText = ((VanLoadDO)obj).ItemCode;
						String strTextDesc = ((VanLoadDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strTextDesc.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() >0 && adapter!= null)
					{
						adapter.refresh(vecTemp);
						tvNoItemFound.setVisibility(View.GONE);
						lvPopupList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoItemFound.setVisibility(View.VISIBLE);
						lvPopupList.setVisibility(View.GONE);
					}
				}
				else if(adapter!= null)
					adapter.refresh(vecSearchedItemd);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
	
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(etCategory.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(AddNewLoadRequest.this, getResources().getString(R.string.warning), "Please select category.", getResources().getString(R.string.OK), null, "search");
				else
				{
					final Vector<VanLoadDO> veciItems= new Vector<VanLoadDO>();
					if(adapter != null)
						veciItems.addAll(adapter.getSelectedItems());
					
					if(veciItems != null && veciItems.size() > 0)
					{
						showLoader("Please wait...");
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								if(vecOrdProduct == null)
									vecOrdProduct = new ArrayList<VanLoadDO>();
								
								removeUOMExistingItems(vecOrdProduct, veciItems);
								vecOrdProduct.addAll(veciItems);
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										objAddNewSKUDialog.dismiss();
										if(vecOrdProduct != null && vecOrdProduct.size() >0)
										{
//											inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
											inventoryItemAdapter.refresh(vecOrdProduct);
//											svLoadStock.setVisibility(View.VISIBLE);
											tvNoItemFoundBase.setVisibility(View.GONE);
										}
										
										hideLoader();
									}
								});
							}
						}).start();
					}
					else
						showCustomDialog(AddNewLoadRequest.this, "Warning !", "Please select Items.", "OK", null, "");
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				objAddNewSKUDialog.dismiss();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == AppStatus.REQUEST_CODE && AppConstants.objScanResultObject!=null)
			loadScannedItem(AppConstants.objScanResultObject);
		else if(requestCode == 1111 && resultCode == 1111)
		{
			setResult(1111);
			finish();
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			VanLoadDO dco = (VanLoadDO) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
//				inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
				inventoryItemAdapter.refresh(vecOrdProduct);
			}
		}
	}
	
	private void loadScannedItem(final ScanResultObject objScanResultObject)
	{
		showLoader("Loading...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				VanLoadDO vanLoadDO = new ProductsDA().getProductsVanByBarCode(objScanResultObject.barcodeId, load_type);
				
				if(vanLoadDO != null)
				{
					if(vecOrdProduct == null)
						vecOrdProduct = new ArrayList<VanLoadDO>();
					
					boolean isFound = false;
					for (VanLoadDO vanLoadDO1 : vecOrdProduct)
					{
						if(vanLoadDO1.ItemCode.equalsIgnoreCase(vanLoadDO.ItemCode))
						{
							isFound = true;
							break;
						}
					}
					
					if(!isFound)
						vecOrdProduct.add(vanLoadDO);
					else
						showCustomDialog(AddNewLoadRequest.this, "Alert!", "This item is allready added in the list.", "OK", null, "");
				}
					
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
//						inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
						inventoryItemAdapter.refresh(vecOrdProduct);
//						svLoadStock.setVisibility(View.VISIBLE);
						tvNoItemFoundBase.setVisibility(View.GONE);
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	public class InventoryItemAdapter extends BaseAdapter
	{
		ArrayList<VanLoadDO> vecOrdProduct;
		private View view;
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			boolean isActive;
			public void setActive(boolean isActive)
			{
				this.isActive=isActive;
			}
			public TextChangeListener(String type)
			{
				LogUtils.infoLog("TextChangeListener","TextChangeListener");
				this.type = type;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(view != null){
					
					if(type.equalsIgnoreCase("unit") && isActive && listScrollState==0){
						
						VanLoadDO objItem = null;
						objItem=(VanLoadDO) view.getTag(R.string.key_field_type);
						handleItem(objItem,s.toString(),(EditText) view);
						view.requestFocus();
					}
					
				}
			}
	
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
		}
		
		private void handleItem(VanLoadDO objItem,String enteredQty,EditText etQty){
			
			
			if(objItem != null)
			{
				objItem.SellableQuantity = StringUtils.getFloat(enteredQty);
				if((load_type == AppStatus.UNLOAD_S_STOCK && !isInventoryAvail(objItem)) 
					|| (load_type == AppStatus.UNLOAD_STOCK && !isReturnInventoryAvail(objItem)))
				{
					objItem.SellableQuantity = 0;
					showToast("Entered quantity should not be greater than available quantity.");
					if(etQty != null && !TextUtils.isEmpty(enteredQty))
						etQty.setText("");
				}
				
//				if(etQty!=null){
//				 etQty.setSelection(etQty.getText().length());
//				}
			}
		}
		
		public InventoryItemAdapter(ArrayList<VanLoadDO> vecOrdProduct)
		{
			this.vecOrdProduct = vecOrdProduct;
		}
	
		public ArrayList<VanLoadDO> getModifiedData()
		{
			return vecOrdProduct;
		}

		private void showOHS(VanLoadDO productDO, TextView tvExpiryDate) 
		{
			String ohqty = "", key = productDO.ItemCode + productDO.UOM;
			String vehicleCode     = tvSelectVehicle.getText().toString();
			
			if(hmInventory != null && hmInventory.containsKey(key))
			{	
				HHInventryQTDO qty 	= 	hmInventory.get(key);
				//ohqty 				= 	"" + diffStock.format(qty.totalQt) +" "+qty.UOM;
				ohqty 				= 	"" + StringUtils.round(qty.onHandQty+"", precision)  +" "+qty.UOM +"  PQty: "+StringUtils.round((qty.onHandQty-qty.totalQt)+"", precision) +" "+qty.UOM;
			}
			else
				ohqty = 	"0";
			
			if(hmWarehouseQty != null && hmWarehouseQty.containsKey(key))
			{	
				HHInventryQTDO qty 	= 	hmWarehouseQty.get(key);
				//ohqty 				= 	ohqty+" | " +vehicleCode+ " : "+ diffStock.format(qty.totalQt) +" "+qty.UOM;
				ohqty 				= 	ohqty+" | " +vehicleCode+ " : "+ StringUtils.round(qty.totalQt+"", precision) +" "+qty.UOM;
			}
			else
				ohqty = ohqty+" | "+vehicleCode + " : "+ 0;
			
			tvExpiryDate.setText("OHQ : "+ohqty);
		}
		
		
		private void showSaleableItems(VanLoadDO productDO, TextView tvExpiryDate, EditText etQty,boolean isFromUOM) 
		{
			String ohqty = "", key = productDO.ItemCode + productDO.UOM;
			
			if(hmInventory != null && hmInventory.containsKey(key))
			{	
				HHInventryQTDO qty 	= 	hmInventory.get(key);
				ohqty 				= 	"" + StringUtils.round(qty.onHandQty+"", precision)  +" "+qty.UOM +"  PQty: "+StringUtils.round((qty.onHandQty-qty.totalQt)+"", precision) +" "+qty.UOM;
				if(isFromUOM)
					handleItem(productDO,""+qty.totalQt,etQty);
			}
	
			etQty.setText(productDO.SellableQuantity+"");
			tvExpiryDate.setText("OHQ : "+ohqty);
		}

		private void refresh(ArrayList<VanLoadDO> vecOrdProducts) 
		{
			sort(vecOrdProducts);
			this.vecOrdProduct = vecOrdProducts;
			
			if(vecOrdProduct != null && vecOrdProducts.size() > 0)
			{
				tvNoItemFoundBase.setVisibility(View.GONE);
				notifyDataSetChanged();
			}
			else
			{
				tvNoItemFoundBase.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public int getCount() {
			return vecOrdProduct.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;
			final VanLoadDO productDO =vecOrdProduct.get(position);
			if(convertView==null){
				
					viewHolder = new ViewHolder();
					convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.add_item_list_add_stock,null);
					viewHolder.tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
					viewHolder.tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
					viewHolder.tvExpiryDate	= (TextView)convertView.findViewById(R.id.tvExpiryDate);
					viewHolder.llCellClick	= (LinearLayout)convertView.findViewById(R.id.llCellClick);
					
					viewHolder.evUOM		= (TextView)convertView.findViewById(R.id.etUOM);
					viewHolder.etQt			= (EditText)convertView.findViewById(R.id.etQt);
					viewHolder.etTotalQt			= (EditText)convertView.findViewById(R.id.etTotalQt);
					viewHolder.ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
					viewHolder.ivDelete	= (ImageView)convertView.findViewById(R.id.ivDelete);
					viewHolder.ivCheck		= (ImageView)convertView.findViewById(R.id.ivCheck);
					viewHolder.texChangeListener= new TextChangeListener("unit");
					viewHolder.etQt.addTextChangedListener(viewHolder.texChangeListener);
					if(position == vecOrdProduct.size())
						viewHolder.etQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
					
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				
					
			viewHolder.ivAcceptCheckItems.setVisibility(View.GONE);
			viewHolder.tvProductKey.setText(productDO.ItemCode);
			viewHolder.tvVendorName.setText(productDO.Description);
				
			viewHolder.etQt.setTag(productDO);
			viewHolder.evUOM.setTag(productDO);
			viewHolder.ivDelete.setTag(productDO);
				
			viewHolder.evUOM.setText(productDO.UOM);
			viewHolder.evUOM.setTag(productDO.UOM);
				
				
				if(productDO.vecUOM != null && productDO.vecUOM.size() > 0)
					viewHolder.evUOM.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.arrow_2));
				else
					viewHolder.evUOM.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				viewHolder.etQt.setTag(R.string.key_field_type,productDO);
				viewHolder.evUOM.setTag(R.string.key_field_type,productDO);
				viewHolder.evUOM.setTag(R.string.key_edit_type,viewHolder.texChangeListener);
				viewHolder.etQt.setTag(R.string.key_edit_type,viewHolder.texChangeListener);
				
				viewHolder.evUOM.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						CustomBuilder builder = new CustomBuilder(AddNewLoadRequest.this, "Select UOM", true);
						builder.setSingleChoiceItems(productDO.vecUOM, v.getTag(), new CustomBuilder.OnClickListener() 
						{
							@Override
							public void onClick(CustomBuilder builder, Object selectedObject) 
							{
								VanLoadDO productDO = (VanLoadDO) viewHolder.evUOM.getTag(R.string.key_field_type);
								TextChangeListener textChangeListener = (TextChangeListener) viewHolder.evUOM.getTag(R.string.key_edit_type);
								String UOM = (String) selectedObject.toString();
								
								viewHolder.evUOM.setText(""+UOM);
								viewHolder.evUOM.setTag(UOM);
								productDO.UOM = UOM;
								builder.dismiss();
								
								if(load_type == AppStatus.UNLOAD_STOCK)
								{
									String key = productDO.ItemCode + productDO.UOM + productDO.reason + productDO.ExpiryDate;
									if(hmReturnInventory != null && hmReturnInventory.containsKey(key))
									{	
										HHInventryQTDO qty 	= 	hmReturnInventory.get(key);
										float quantity = StringUtils.getFloat(diffStock.format(qty.nonSellQty));
										viewHolder.etQt.setText(quantity+"");
										productDO.SellableQuantity = quantity;
									}
								}
								else if(load_type == AppStatus.LOAD_STOCK)
									showOHS(productDO, viewHolder.tvExpiryDate);
								else if(load_type == AppStatus.UNLOAD_S_STOCK){
									view = null;
									showSaleableItems(productDO, viewHolder.tvExpiryDate, viewHolder.etQt,true);
								}
				    		}
					   }); 
					   builder.show();
					}
				});


				viewHolder.texChangeListener.setActive(false);
				if(productDO.SellableQuantity > 0)
					viewHolder.etQt.setText((float) StringUtils.round(""+productDO.SellableQuantity, precision)+"");
				else
					viewHolder.etQt.setText("");
				
				setActionInViews(viewHolder.etTotalQt, false);
				
				viewHolder.etQt.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						
						TextChangeListener textChangeListener = (TextChangeListener) v.getTag(R.string.key_edit_type);
						view = v;
						textChangeListener.setActive(true);
						
					}
				});
				if(loadRequestDO != null )
				{
					setActionInViews(viewHolder.etQt, false);
					setActionInViews(viewHolder.llCellClick, false);
					viewHolder.ivCheck.setVisibility(View.GONE);
					if(loadRequestDO.MovementStatus.equalsIgnoreCase(LoadRequestDO.STATUS_SHIPPED) && (load_type==AppStatus.UNLOAD_S_STOCK||load_type==AppStatus.UNLOAD_S_STOCK)){
						viewHolder.etTotalQt.setVisibility(View.VISIBLE);
						viewHolder.etTotalQt.setText(""+productDO.SellableQuantity);
					}
					else if(loadRequestDO.MovementStatus.contains(""+AppConstants.APPROVED_MOVEMENT_STATUS)
						|| loadRequestDO.MovementStatus.contains("101"))
					{
						viewHolder.etTotalQt.setVisibility(View.VISIBLE);
						viewHolder.etTotalQt.setText(""+productDO.inProccessQty);
						
						if(productDO.SellableQuantity!=productDO.inProccessQty){
							viewHolder.etTotalQt.setBackgroundColor(getResources().getColor(R.color.light_blue));
							viewHolder.etTotalQt.setTextColor(getResources().getColor(R.color.white));
						}
					}
					else if(loadRequestDO.MovementStatus.contains(""+AppConstants.COLLECTED_MOVEMENT_STATUS)
							|| loadRequestDO.MovementStatus.contains("100")){
						viewHolder.etTotalQt.setVisibility(View.VISIBLE);
						viewHolder.etTotalQt.setText(""+productDO.ShippedQuantity);
					}
				}
				else if(load_type != AppStatus.LOAD_STOCK)
					viewHolder.ivCheck.setVisibility(View.VISIBLE);
			if(loadRequestDO == null )	{
				
				if(load_type == AppStatus.UNLOAD_STOCK)
				{
					setActionInViews(viewHolder.etQt, false);
					setActionInViews(viewHolder.evUOM, false);
					viewHolder.tvExpiryDate.setText("Reason : "+productDO.reason+" | Exp. Date : "+CalendarUtils.getFormatedDatefromString(productDO.ExpiryDate));
					viewHolder.etQt.setText(StringUtils.round(""+productDO.SellableQuantity, precision)+"");
				}
				else if(load_type == AppStatus.LOAD_STOCK)
					showOHS(productDO, viewHolder.tvExpiryDate);
				else if(load_type == AppStatus.UNLOAD_S_STOCK)
					showSaleableItems(productDO, viewHolder.tvExpiryDate, viewHolder.etQt,false);
			}
				
				if(loadRequestDO == null)
					setActionInViews(viewHolder.llCellClick, true);
				else
					setActionInViews(viewHolder.llCellClick, false);
				
				if(productDO.isSelected)
					viewHolder.ivCheck.setImageResource(R.drawable.check_hover);
				else
					viewHolder.ivCheck.setImageResource(R.drawable.check_normal);
				if(isfrom!=null && isfrom.equalsIgnoreCase("EBSApproved"))
					viewHolder.tvExpiryDate.setVisibility(View.GONE);
				else
					viewHolder.tvExpiryDate.setVisibility(View.VISIBLE);
				
				if(load_type == AppStatus.LOAD_STOCK){
					
					viewHolder.llCellClick.setOnLongClickListener(new OnLongClickListener()
					{
						@Override
						public boolean onLongClick(View v)
						{
							productDO.isDeleteOpen = false;
							if(viewHolder.ivDelete.getVisibility() == View.GONE)
								loadAnimation(viewHolder.ivDelete, true);
							else
								loadAnimation(viewHolder.ivDelete, false);
							
							new Handler().postDelayed(new Runnable()
							{
								@Override
								public void run() 
								{
									productDO.isDeleteOpen = true;								
								}
							}, 1000);
							
							return false;
						}
					});
				}
				
				viewHolder.ivDelete.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						showLoader("Please wait...");
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								new Thread(new Runnable()
								{
									@Override
									public void run()
									{
										VanLoadDO productDO = (VanLoadDO) viewHolder.ivDelete.getTag();
										if(AddNewLoadRequest.this.vecOrdProduct != null && AddNewLoadRequest.this.vecOrdProduct.contains(productDO))
											AddNewLoadRequest.this.vecOrdProduct.remove(productDO);
										
										addUOMExistingItems(AddNewLoadRequest.this.vecOrdProduct, productDO);
										
										runOnUiThread(new Runnable()
										{
											@Override
											public void run() 
											{
//												refresh(AddNewLoadRequest.this.vecOrdProduct, llLayoutMiddle);
												refresh(AddNewLoadRequest.this.vecOrdProduct);
												hideLoader();												
											}
										});
									}
								}).start();
							}
						}, 300);
					}
				});
				
				viewHolder.ivCheck.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if(!productDO.isSelected)
						{
							productDO.isSelected = true;
							viewHolder.ivCheck.setImageResource(R.drawable.check_hover);
						}
						else
						{
							productDO.isSelected = false;
							viewHolder.ivCheck.setImageResource(R.drawable.check_normal);
						}
					}
				});		
				
				viewHolder.llCellClick.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(viewHolder.ivDelete.getVisibility() == View.VISIBLE && productDO.isDeleteOpen)
							loadAnimation(viewHolder.ivDelete, false);
						
						if(load_type != AppStatus.LOAD_STOCK)
							viewHolder.ivCheck.performClick();
					}
				});
				viewHolder.texChangeListener.setActive(true);

		return convertView;
		}

	}
	
	private class ViewHolder{
		
		public TextChangeListener texChangeListener;
		public LinearLayout llCellClick;
		TextView tvProductKey,tvVendorName,tvExpiryDate,evUOM;
		EditText etQt,etTotalQt	;
		ImageView ivAcceptCheckItems,ivDelete,ivCheck;
	}
	
	private float getTotalQty()
	{
		float totalQty = 0.0f;
		
		if(inventoryItemAdapter != null)
		{
			ArrayList<VanLoadDO> vecOrdProduct = inventoryItemAdapter.getModifiedData();
			if(vecOrdProduct != null && vecOrdProduct.size() > 0)
			{
				for(VanLoadDO dco : vecOrdProduct)
					totalQty += dco.SellableQuantity;
			}
		}
		return totalQty;
	}
	
	private void loadRequestStatus()
	{
		if(isNetworkConnectionAvailable(AddNewLoadRequest.this))
		{
			showLoader("Refreshing...");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
					loadAllMovements_Sync("Refreshing data...",empNo);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							int status = new InventoryDA().getPendingStatus(loadRequestDO.MovementCode);
							if(status == 1)
							{
								if(load_type == AppStatus.LOAD_STOCK)
									showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been approved, Quantity has been added in stock.", "OK", null, "finish");
								else
									showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been approved, Quantity has been deducted from stock.", "OK", null, "finish");
							}
							else if(status == 3)
							{
								if(load_type == AppStatus.LOAD_STOCK)
									showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been approved from portal and pending from EBS.", "OK", null, "finish");
								else
									showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been approved from portal and pending from EBS.", "OK", null, "finish");
							}
							else if(status == 2)
							{
								if(load_type == AppStatus.LOAD_STOCK)
									showCustomDialog(AddNewLoadRequest.this, "Alert!", "Your Load request has been rejected by admin.", "OK", null, "finish");
								else
									showCustomDialog(AddNewLoadRequest.this, "Alert!", "Your UnLoad request has been rejected by admin.", "OK", null, "finish");
								
								if(load_type == AppStatus.UNLOAD_STOCK)
									new ProductsDA().updateUnlodStatus(vecOrdProduct);
							}
							else
								showCustomDialog(AddNewLoadRequest.this, "Warning !", "Request is still pending, Please try after some time.", "OK", null, "");
						}
					});
				}
			}).start();
		}
		else
			showCustomDialog(AddNewLoadRequest.this, "Warning !", getString(R.string.no_internet), "OK", null, "");
	}
	
	private boolean isInventoryAvail(VanLoadDO objItem)
	{
		boolean isAvail = false;
		String key = objItem.ItemCode + objItem.UOM;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(key))
		{
			float availQty = hmInventory.get(key).totalQt;
			if(objItem.SellableQuantity> availQty)
				isAvail = false;
			else
				isAvail = true;
		}
		else
			isAvail = false;
		return isAvail;
	}
	
	private boolean isReturnInventoryAvail(VanLoadDO objItem)
	{
		boolean isAvail = false;
		String key = objItem.ItemCode + objItem.UOM + objItem.reason + objItem.ExpiryDate;
		if(hmReturnInventory != null && hmReturnInventory.size() > 0 && hmReturnInventory.containsKey(key))
		{
			float availQty = StringUtils.getFloat(diffStock.format(hmReturnInventory.get(key).nonSellQty));
			if(objItem.SellableQuantity> availQty)
				isAvail = false;
			else
				isAvail = true;
		}
		else
			isAvail = false;
		return isAvail;
	}
	
	private boolean validateItems(ArrayList<VanLoadDO> vecOrdProduct)
	{
		boolean isFound = false;
		for (VanLoadDO vanLoadDO : vecOrdProduct) 
		{
			if((load_type == AppStatus.LOAD_STOCK && vanLoadDO.SellableQuantity > 0) ||
			   (load_type != AppStatus.LOAD_STOCK && vanLoadDO.SellableQuantity > 0 && vanLoadDO.isSelected))
			{
				isFound = true ;
				break;
			}
		}
		return isFound;
	}
	private boolean validateUnloadItems(ArrayList<VanLoadDO> vecOrdProduct)
	{
		boolean isFound = true;
		for (VanLoadDO vanLoadDO : vecOrdProduct) 
		{
			if((load_type == AppStatus.LOAD_STOCK && vanLoadDO.SellableQuantity > 0) ||
			   (load_type != AppStatus.LOAD_STOCK && vanLoadDO.SellableQuantity > 0 && !vanLoadDO.isSelected))
			{
				isFound = false ;
				break;
			}
		}
		return isFound;
	}
	
	private void loadUnLaodData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final Vector<VanLoadDO> veciItems = new ProductsDA().getProductsUnload(load_type, diffAmt, diffStock,precision);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						hideLoader();
						if(veciItems != null && veciItems.size() > 0)
						{
							if(vecOrdProduct == null)
								vecOrdProduct = new ArrayList<VanLoadDO>();
							
							vecOrdProduct.addAll(veciItems);
							if(vecOrdProduct != null && vecOrdProduct.size() >0)
							{
//								inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
								inventoryItemAdapter.refresh(vecOrdProduct);
//								svLoadStock.setVisibility(View.VISIBLE);
								tvNoItemFoundBase.setVisibility(View.GONE);
							}
						}
					}
				});
			}
		}).start();
	}
	
	/** method for dateofJorney picker **/
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	    {
	    	//getting current date from Calendar
		     Calendar currentCal = 	Calendar.getInstance();
		     //selected date 
		     Calendar selectedCal = Calendar.getInstance();
		     selectedCal.set(year, monthOfYear, dayOfMonth);
		     
		     if(currentCal.after(selectedCal))
		     {
		    	 showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Date should not be before current date.", getString(R.string.OK), null, "");
		     }
		     else
		     {
		    	 String date = year+"-"+((monthOfYear+1)< 10?"0"+(monthOfYear+1):(monthOfYear+1))+"-"+((dayOfMonth)<10?"0"+(dayOfMonth):(dayOfMonth));
		    	 tvRequiredDate.setTag(date);
		    	 tvRequiredDate.setText(CalendarUtils.getFormatedDatefromString(date));
		     }
	    }
    };
    
    @Override
	protected Dialog onCreateDialog(int id) 
    {
		//getting current dateofJorney from Calendar
	     Calendar c = 	Calendar.getInstance();
	     int cyear 	= 	c.get(Calendar.YEAR);
	     int cmonth = 	c.get(Calendar.MONTH);
	     int cday 	=	c.get(Calendar.DAY_OF_MONTH);
	     
	     switch (id) 
	     {
		     case DATE_DIALOG_ID:
		      	return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	     }
		 return null;
	  }
    
    private void removeUOMExistingItems(ArrayList<VanLoadDO> oldVec, Vector<VanLoadDO> newVec)
	{
    	try 
    	{
    		for(VanLoadDO newOBJ : newVec)
    		{
    			for(VanLoadDO oldOBJ : oldVec)
    			{
    				if(newOBJ.ItemCode.equalsIgnoreCase(oldOBJ.ItemCode))
    				{
    					if(newOBJ.vecUOM != null && newOBJ.vecUOM.size() > 0 & oldOBJ.vecUOM != null && oldOBJ.vecUOM.size() > 0)
    					{
    						for(String UOM : newOBJ.vecUOM)
    							oldOBJ.vecUOM.remove(UOM);
    					}
    				}
    			}
    		}
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
	}
    
    private void addUOMExistingItems(ArrayList<VanLoadDO> oldVec, VanLoadDO newOBJ)
   	{
       	try 
       	{
       		int count = oldVec.size() - 1;
   			for(int i = count ; i >= 0 ; i--)
   			{
   				VanLoadDO oldOBJ = oldVec.get(i);
   				if(newOBJ.ItemCode.equalsIgnoreCase(oldOBJ.ItemCode))
   				{
   					boolean isBreak = false;
   					if(newOBJ.vecUOM != null && newOBJ.vecUOM.size() > 0 & oldOBJ.vecUOM != null && oldOBJ.vecUOM.size() > 0)
   					{
   						for(String UOM : newOBJ.vecUOM)
   							if(!oldOBJ.vecUOM.contains(UOM))
   							{
   								oldOBJ.vecUOM.add(UOM);
   								isBreak = true;
   							}
   					}
   					if(isBreak)
							break;
   				}
   			}
   		} 
       	catch (Exception e) 
       	{
   			e.printStackTrace();
   		}
   	}
    
    private void fillUOM() 
	{
		if(vecOrdProduct != null && vecOrdProduct.size() > 0)
		{
			for(VanLoadDO vanLoadDO : vecOrdProduct)
			{
				if(hmAddedItems.containsKey(vanLoadDO.ItemCode))
				{
					Vector<String> vecUOM = hmAddedItems.get(vanLoadDO.ItemCode);
					vecUOM.add(vanLoadDO.UOM);
				}
				else
				{
					Vector<String> vecUOM = new Vector<String>();
					vecUOM.add(vanLoadDO.UOM);
					hmAddedItems.put(vanLoadDO.ItemCode, vecUOM);
				}
			}
		}
	}
    public boolean validateShipQty(ArrayList<VanLoadDO> vecOrdProduct){
		boolean isValid=true;
		int count=0;
		for(VanLoadDO vanLoadDO:vecOrdProduct){
			if(vanLoadDO.inProccessQty==0)
				count++;
		}
		if(count==vecOrdProduct.size())
			isValid=false;
		
		return isValid;
	}
}
