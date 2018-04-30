package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.UOMConversionFactorDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.dataobject.VerifyRequestDO;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;
public class VerifyItemInVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader ;
	private Button btnOrdersheetVerify,btnOrdersheetReport,btnPrint, btnContinue;
	private OrderSheetAdapter ordersheetadapter;
	private int count ;
	private LoadRequestDO unloadDo;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView tvItemCode, tvCases, tvUnits,tvRecivedUnits, tvOrdersheetHeader, tvNoItemFound, tvItemList,
	 				 tvUsername, tvDate, tvVehicleCode,tvRequestedUnits,tvMo;
	private ListView lvItemList ; 
	private ArrayList<Integer> isClicked;
	private ImageView ivCheckAllItems;
	private MyView myViewManager;
	private MyView myViewDriver;
	private Paint mPaint;
	private boolean isMenu;
	private String movementId = "", unloadMovementCode="", movementType,Source,Destination;
	private boolean isUnload = false,printApproved=false;
	private  boolean hasItemsToUnload;
	private int listScrollState,load_type,precision;
	private VehicleDO vehicleDO;
	private InventoryDA inventoryDA;
	
	private ArrayList<LoadRequestDetailDO> arrUnloadItems = null;

	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.item_list_to_verify_latest, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras()!=null)
		{
			movementId = getIntent().getExtras().getString("movementId");
			movementType = getIntent().getExtras().getString("movementType");
			//this source n destinationis collected here for auto unload purpose
			Source = getIntent().getExtras().getString("Source");
			Destination = getIntent().getExtras().getString("Destination");
			isUnload = getIntent().getExtras().getBoolean("isUnload");
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
			load_type	 	= 	getIntent().getExtras().getInt("load_type");
		}
		
//		if(getIntent().getExtras()!=null)
//		{
//			vecOrdProduct = (ArrayList<VanLoadDO>)getIntent().getExtras().get("updatededDate");
//			isMenu = getIntent().getExtras().getBoolean("isMenu");
//		}
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		 if(load_type==AppStatus.UNLOAD_S_STOCK||load_type==AppStatus.UNLOAD_STOCK){
			 tvRequestedUnits.setVisibility(View.VISIBLE);
			 tvRecivedUnits.setVisibility(View.GONE);
			 tvUnits.setVisibility(View.GONE); 
		 }else{
			 tvRequestedUnits.setVisibility(View.GONE);
			 tvRecivedUnits.setVisibility(View.VISIBLE);
			 tvUnits.setVisibility(View.VISIBLE); 
		 }
		
		tvMo.setText(movementId);
		
		isClicked		=	new ArrayList<Integer>();
		
		lvItemList.setVerticalScrollBarEnabled(false);
		lvItemList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvItemList.setCacheColorHint(0);
		lvItemList.setFadingEdgeLength(0);
		lvItemList.setAdapter(ordersheetadapter = new OrderSheetAdapter(new ArrayList<VanLoadDO>()));
		llOrderListView.addView(lvItemList, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lvItemList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				listScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				
			}
		});
		tvOrdersheetHeader.setText("Stock Verification");
		loadData();
		
		ivCheckAllItems.setTag("unchecked");
		ivCheckAllItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("unchecked"))
				{
					v.setTag("checked");
					((ImageView)v).setImageResource(R.drawable.check_hover);
					isClicked.clear();
					for(int j = 0; j < vecOrdProduct.size() ; j++)
					{
						isClicked.add(j);
					}
					ordersheetadapter.refreshAll();
				}
				else
				{
					v.setTag("unchecked");
					((ImageView)v).setImageResource(R.drawable.check_normal);
					isClicked.clear();
					ordersheetadapter.refresh();
				}
			}
		});
		
		//Button order sheet verify
//		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				btnOrdersheetVerify.setClickable(false);
//				btnOrdersheetVerify.setEnabled(false);
//				
//				new Handler().postDelayed(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//				
//					btnOrdersheetVerify.setClickable(true);
//					btnOrdersheetVerify.setEnabled(true);
//					}
//
//
//				}, 500);
//				
//				
//				if(isAllVerified())
//					inserMovementData();
//				else
//					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "Please select all items.", "OK", null, null);
//			}
//		});
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				btnOrdersheetVerify.setClickable(false);
				btnOrdersheetVerify.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
					btnOrdersheetVerify.setClickable(true);
					btnOrdersheetVerify.setEnabled(true);
					}
				}, 500);

				switch (validatingAllItems()) {
				case 1:
					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "There is no item for verification.", "OK", null, null);				
					break;
				case 2:
					showCustomDialog(VerifyItemInVehicle.this, "Alert !", "Please Select All Items", getString(R.string.OK), null, "");
					break;
					default:
						new Thread(new  Runnable()
						{
							public void run() 
							{
								showLoader("please wait...");
										
								if(isAllVerified()){
									inserMovementData();
									
									hasItemsToUnload = hasAnyItemToUnload();
									if(hasItemsToUnload && TextUtils.isEmpty(unloadMovementCode))

									{
										showCustomDialog(VerifyItemInVehicle.this,
												"Alert !",
												"Movement codes are not synced properly from server. Please sync from Settings.",
												"OK", null, "");
										arrUnloadItems = new ArrayList<LoadRequestDetailDO>();
									}

									else 
									{
										if(hasItemsToUnload && StringUtils.getInt(movementType) == AppStatus.LOAD_STOCK)
											updateLoadQuantity();
										
										runOnUiThread(new Runnable() 
										{
											@Override
											public void run() 
											{
												showSignatureDialog();
											}
										});
									}
									
									/*runOnUiThread(new Runnable() 
									{
										@Override
										public void run() 
										{
											showSignatureDialog();
										}
									});*/
								}
								else
									showCustomDialog(VerifyItemInVehicle.this, "Alert !", "Please select all items.", "OK", null, null);
										
								hideLoader();
							}
							
						}).start();
						break;
				}
			
			}
		});
		
		
		
		btnOrdersheetReport.setText("STOCK VERIFICATION");
		//Button order sheet Report
		btnOrdersheetReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.LOAD_VERIFICATION);
//				startActivityForResult(intent, 1000);
//				showToast("Print functionality is in progress.");
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			//	finish();
					navigateFromVerification();
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				ShowOptionPopupForPrinter(VerifyItemInVehicle.this,new PrintPopup() {
					@Override
					public void selectedOption(int selectedPrinter)
					{

						Intent intent=null;
						if(selectedPrinter==AppConstants.CANCEL)
						{
							hideLoader();

						}
						else
						{
							if(selectedPrinter==AppConstants.WOOSIM)
								intent=new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(VerifyItemInVehicle.this, PrinterConnectorArabic.class);
							intent.putExtra("itemforVerification", vecOrdProduct);
							intent.putExtra("strMovementNo", movementId);
							intent.putExtra("movementType", movementType);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
							startActivityForResult(intent, 1000);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("strMovementNo", movementId);
//				intent.putExtra("movementType", movementType);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
//				startActivityForResult(intent, 1000);
			}
		});
		
		setTypeFace(llOrderSheet);
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		tvCases					=	(TextView)llOrderSheet.findViewById(R.id.tvCases);
		tvUnits					=	(TextView)llOrderSheet.findViewById(R.id.tvUnits);
		tvRecivedUnits					=	(TextView)llOrderSheet.findViewById(R.id.tvRecivedUnits);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		btnPrint				=	(Button)llOrderSheet.findViewById(R.id.btnPrint);
		btnContinue				=	(Button)llOrderSheet.findViewById(R.id.btnContinue);
		
		tvUsername				=	(TextView)llOrderSheet.findViewById(R.id.tvUsername);
		tvDate					=	(TextView)llOrderSheet.findViewById(R.id.tvDate);
		tvVehicleCode			=	(TextView)llOrderSheet.findViewById(R.id.tvVehicleCode);
		tvRequestedUnits		=	(TextView)llOrderSheet.findViewById(R.id.tvRequestedUnits);
		tvMo                    =	(TextView)llOrderSheet.findViewById(R.id.tvMo);
		
		if(preference.getbooleanFromPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false))
			btnContinue.setText("Finish");
		else
			btnContinue.setText("Continue");
		
		lvItemList = new ListView(this);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		inventoryDA = new InventoryDA();
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        tvUsername.setText(""+preference.getStringFromPreference(Preference.USER_NAME, ""));
		tvDate.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
		tvVehicleCode.setText("Vehicle Code : "+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, ""));
		
		blockMenu();
		
		tvCases.setText("UOM");
		tvUnits.setText("Recv.Qty");
	}
	private boolean hasAnyItemToUnload() 
	{

		boolean status = false;
		if(vecOrdProduct!=null && vecOrdProduct.size()>0)
		{
			for(VanLoadDO vanLoadDO: vecOrdProduct)
			{
				if(vanLoadDO.tempQuantity > 0)
				{
					status = true;
					break;
				}
			}
		}
		
//		if(status)
//			unloadMovementCode = ""
//					+ new InventoryDA().getMovementId(preference
//							.getStringFromPreference(
//									Preference.SALESMANCODE, ""));
		
		if(status)
		{
			OfflineDA offlineDA = new OfflineDA();
			unloadMovementCode = "";
			String code = offlineDA.getNextSequenceNumber(OfflineDataType.MOVEMENT);
			if(code!=null && !code.equalsIgnoreCase(""))
				unloadMovementCode = code;
			
		}
		
		return status;
	
	}
	public class OrderSheetAdapter extends BaseAdapter
	{
		ArrayList<VanLoadDO> vecOrder;
		private boolean isCheckVisible = true;
		public OrderSheetAdapter(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder=vec;
		}
		public void refresh() 
		{
			notifyDataSetChanged();
		}
		public void refresh(boolean isCheckVisible) 
		{
			this.isCheckVisible = isCheckVisible;
			notifyDataSetChanged();
		}
		public void refreshAll() 
		{
			notifyDataSetChanged();
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
			VanLoadDO ordPro = vecOrder.get(position);
			final ViewHolder viewHolder ;
			//inflate invoice_list_cell layout here
			if(convertView == null)
			{
				convertView			     =	(LinearLayout)getLayoutInflater().inflate(R.layout.item_list_verify_cell,null);
				viewHolder = new ViewHolder();
				
				viewHolder.requestedUnits				 =	(EditText)convertView.findViewById(R.id.requestedUnits);
				viewHolder.receivedQty				 =	(EditText)convertView.findViewById(R.id.receivedQty);
				viewHolder.etQty				 =	(EditText)convertView.findViewById(R.id.etQty);
				viewHolder.llCellClick	 =	(LinearLayout)convertView.findViewById(R.id.llCellClick);
				viewHolder.tvProductKey		 =	(TextView)convertView.findViewById(R.id.tvProductKey);
				viewHolder.tvVendorName		 =	(TextView)convertView.findViewById(R.id.tvVendorName);
				viewHolder.etUOM		 		 =	(EditText)convertView.findViewById(R.id.etUOM);
				
				viewHolder.requestedUnits.setEnabled(false);
				viewHolder.etQty.setEnabled(false);
				viewHolder.receivedQty.setFocusable(false);
				viewHolder.textChangeListener = new TextChangeListener();
				viewHolder.receivedQty.addTextChangedListener(viewHolder.textChangeListener);
				convertView.setTag(viewHolder);
			}//getting id's here
			else
			{
				viewHolder	= (ViewHolder) convertView.getTag();
			}
			final ImageView ivAcceptCheck=	(ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
			
			viewHolder.tvProductKey.setText(ordPro.ItemCode);
			viewHolder.tvVendorName.setText(""+ordPro.Description);
			viewHolder.etQty.setText(""+diffStock.format(ordPro.inProccessQty));
		//	viewHolder.requestedUnits.setText(""+diffStock.format(ordPro.SellableQuantity));
			viewHolder.requestedUnits.setText(""+StringUtils.round(""+ordPro.SellableQuantity, precision));
			
			viewHolder.textChangeListener.setActive(false);
			if(ordPro.inProccessQty > 0)
				viewHolder.receivedQty.setText(""+diffStock.format(ordPro.inProccessQty-ordPro.tempQuantity));
			else
				viewHolder.receivedQty.setText(""+0);
			
			viewHolder.receivedQty.setTag(ordPro);
			viewHolder.textChangeListener.setActive(true);

			
			viewHolder.etUOM.setText(""+ordPro.UOM);
			//Setting Type-faces here
			if(isClicked != null && isClicked.contains(position))
				ivAcceptCheck.setImageResource(R.drawable.check_hover);
			else
				ivAcceptCheck.setImageResource(R.drawable.check_normal);
			
			setActionInViews(viewHolder.etUOM, false);
			
			if( (load_type==AppStatus.UNLOAD_S_STOCK||load_type==AppStatus.UNLOAD_STOCK)){
				viewHolder.requestedUnits.setVisibility(View.VISIBLE);
				viewHolder.etQty.setVisibility(View.GONE);
				viewHolder.receivedQty.setVisibility(View.GONE);
			}else{
				viewHolder.requestedUnits.setVisibility(View.GONE);
				viewHolder.etQty.setVisibility(View.VISIBLE);
				viewHolder.receivedQty.setVisibility(View.VISIBLE);
			}
			
			if(isCheckVisible){
				ivAcceptCheck.setVisibility(View.VISIBLE);
				if(load_type==AppStatus.LOAD_STOCK){
					viewHolder.requestedUnits.setVisibility(View.GONE);
				}
				setActionInViews(viewHolder.receivedQty, true);
			}
			else{
				ivAcceptCheck.setVisibility(View.GONE);
				viewHolder.requestedUnits.setVisibility(View.VISIBLE);
				setActionInViews(viewHolder.receivedQty, false);
			}
			ivAcceptCheck.setTag(position);
			viewHolder.llCellClick.setTag(position);
			//Click event for llLayout
			viewHolder.llCellClick.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_hover);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_normal);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.check_hover);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.check_normal);
					}
				}
			});
			
			//Click event for ivAcceptCheck
			ivAcceptCheck.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_hover);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_normal);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.check_hover);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.check_normal);
					}
				}
			});
			
//			etQty.setTag(R.string.key_field_type,ProductDO.getItemUomLevel3());
			viewHolder.receivedQty.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						view  = v;
						viewHolder.textChangeListener.setActive(true);

						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								
							}
						}, 10);
					}
				}
			});
			
			
			setTypeFace((LinearLayout)convertView);
			return convertView;
		}
	}
	
	private boolean isAllVerified()
	{
		if(isClicked.size() == vecOrdProduct.size())
			return true;
		
		return false;
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("verify"))
		{
			disableVerify();
			//automatic print 
			//check if it is approved 
			if(printApproved){
				ShowOptionPopupForPrinter(VerifyItemInVehicle.this,new PrintPopup() {
					@Override
					public void selectedOption(int selectedPrinter)
					{

						Intent intent=null;
						if(selectedPrinter==AppConstants.CANCEL)
						{
							hideLoader();

						}
						else
						{
							if(selectedPrinter==AppConstants.WOOSIM)
								intent=new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(VerifyItemInVehicle.this, PrinterConnectorArabic.class);
							intent.putExtra("itemforVerification", vecOrdProduct);
							intent.putExtra("strMovementNo", movementId);
							intent.putExtra("movementType", movementType);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
							startActivityForResult(intent, 1000);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("strMovementNo", movementId);
//				intent.putExtra("movementType", movementType);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
//				startActivityForResult(intent, 1000);
				
			}
		}
	}
	private void disableVerify(){
		btnPrint.setVisibility(View.VISIBLE);
		btnContinue.setVisibility(View.VISIBLE);
		btnOrdersheetVerify.setVisibility(View.GONE);
		tvRequestedUnits.setVisibility(View.VISIBLE);
		ivCheckAllItems.setVisibility(View.GONE);
		
		ordersheetadapter.refresh(false);
		
		if(preference.getbooleanFromPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false))
			btnContinue.setText("Finish");
		else
			btnContinue.setText("Finish");
	}
	private void inserMovementData() 
	{
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
				for(VanLoadDO vanLoadDO:vecOrdProduct)
				{
//					UOMConversionFactorDO uomConversionFactorDO=hashArrUoms.get(vanLoadDO.ItemCode);
//					if(uomConversionFactorDO!=null)
//					{
//						int exConversion=(int) uomConversionFactorDO.eaConversion;
						vanLoadDO.ShippedQuantity=vanLoadDO.inProccessQty;//vanLoadDO.shippedQuantityLevel1*exConversion+vanLoadDO.shippedQuantityLevel3;
						vanLoadDO.inProcessQuantityLevel1 = (int) (vanLoadDO.inProcessQuantityLevel1-vanLoadDO.shippedQuantityLevel1);
						vanLoadDO.inProcessQuantityLevel3 = (int) (vanLoadDO.inProcessQuantityLevel3-vanLoadDO.shippedQuantityLevel3);
//					}
				}
//			}
//		}).start();
	}
	private String filepath =null, filepathDriver = null;
	private boolean isVarifcationSignatureDone = false;
	private boolean isVarSignDoneDriver = false;
	@SuppressWarnings("deprecation")
	private void showSignatureDialog()
	{
		final Dialog dialog 			= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		final LinearLayout llSignSupervisor = (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout)llSignature.findViewById(R.id.llSignDriver);
	
		Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnSKCear 				= (Button)llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear 			= (Button)llSignature.findViewById(R.id.btnDriverCear);
		final EditText etPassCode    	= (EditText)llSignature.findViewById(R.id.etPassCode);
		
		dialog.addContentView(llSignature,new LayoutParams(LayoutParams.FILL_PARENT, (int)(420 * px)));
		dialog.show();
		
		isVarifcationSignatureDone = false;
		isVarSignDoneDriver = false;
		
		myViewManager  = new MyView(this, false);
		myViewManager.setDrawingCacheEnabled(true);
		myViewManager.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , (int)(180 * px)));
		myViewManager.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignSupervisor.addView(myViewManager);
		
		
		myViewDriver  = new MyView(this, true);
		myViewDriver.setDrawingCacheEnabled(true);
		myViewDriver.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , (int)(180 * px)));
		myViewDriver.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignDriver.addView(myViewDriver);
		
		setTypeFace(llSignature);
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					preference.saveStringInPreference(Preference.IsStockVerifiedToday, CalendarUtils.getOrderPostDate());
					preference.commitPreference();
					if(!isVarifcationSignatureDone && !isVarSignDoneDriver)
					{
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", getString(R.string.OK), null, "");
					}
					else if(!isVarifcationSignatureDone)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", getString(R.string.OK), null, "");
					else if(!isVarSignDoneDriver)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), getString(R.string.please_sign_before_submit), getString(R.string.OK), null, "");
					else if(TextUtils.isEmpty(etPassCode.getText().toString()))
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), getString(R.string.pls_enter_passcode), getString(R.string.OK), null, "");
					else	
					{
						/*if(load_type==AppStatus.LOAD_STOCK){
							if (hasItemsToUnload)
								submitLoadWithunloadItem(dialog);
							else
								submitLoad(dialog);
						}
						else*/
						validatePasscode(etPassCode.getText().toString(), dialog);
					}
			}
		});
		/*btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				preference.saveStringInPreference(Preference.IsStockVerifiedToday, CalendarUtils.getOrderPostDate());
				preference.commitPreference();
				if (hasItemsToUnload)
				{
					if(!isVarifcationSignatureDone && !isVarSignDoneDriver)
					{
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
					}
					else if(!isVarifcationSignatureDone)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
					else if(!isVarSignDoneDriver)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please sign before submitting the stock verification.", "OK", null, "");
					else if(TextUtils.isEmpty(etPassCode.getText().toString()))
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please enter passcode.", "OK", null, "");
					else	
					{
							showLoader("Please wait...");
							new Thread(new Runnable()
							{
								private int status=0;
								private boolean isCollected;

								@Override
								public void run()
								{
									VerifyRequestDO verifyRequestDO = new VerifyRequestDO();
									verifyRequestDO.movementCode 		= movementId;
									verifyRequestDO.movementType 		= movementType;
									verifyRequestDO.movementStatus 		= "99";
									verifyRequestDO.vecVanLodDOs = vecOrdProduct;
									Bitmap bitmap = getBitmap(myViewManager);
									if(bitmap != null)
									{
										verifyRequestDO.logisticSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
									}

									bitmap = getBitmap(myViewDriver);
									//									final int isUpdated;
									if(bitmap != null)
										verifyRequestDO.salesmanSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
									String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
									if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
									{
										if(StringUtils.getInt(movementType) == AppStatus.LOAD_STOCK) 
										{
											int isUpdated = uploadLoadRequest(verifyRequestDO);
											if(isUpdated==1)
											{
												status = 1;
												new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
												if(hasItemsToUnload){
													generateUnloadRequest();
												}
													
												loadVanStock_Sync("Loading Stock...", empNo);
												loadAllMovements_Sync("Loading Stock...", empNo);
												isCollected=true;
												
												//automatic print 
												//check if it is approved 
												if(printApproved){
													Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
													intent.putExtra("itemforVerification", vecOrdProduct);
													intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
													startActivityForResult(intent, 1000);
												}
												
											}
										}
										else
										{
											int isUpdated = uploadLoadRequest(verifyRequestDO);

											if(isUpdated==1)
											{
												status = 1;
												new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
												isCollected=true;
												Log.e("isUpdated", "sync start");
												loadVanStock_Sync("Loading Stock...",empNo);
												Log.e("isUpdated", "sync end");
												
											}
										}

									}
									else
										status = 2;
									runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{
											dialog.dismiss();
											if(status == 1)
											{
												showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
														getString(R.string.OK), null, "verify", false);
											}
											else if(status == 2)
												showCustomDialog(VerifyItemInVehicle.this, "Alert !", "There is no internet connection. Please check your internet connection.", "OK", null, null);
											else
												showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Error occurred while verifying. Please try again.", getString(R.string.OK), null, "");
											hideLoader();
										}
									});
								}
							}).start();
					}
				
				}
				else
				{
					if(!isVarifcationSignatureDone && !isVarSignDoneDriver)
					{
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
					}
					else if(!isVarifcationSignatureDone)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
					else if(!isVarSignDoneDriver)
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please sign before submitting the stock verification.", "OK", null, "");
					else if(TextUtils.isEmpty(etPassCode.getText().toString()))
						showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Please enter passcode.", "OK", null, "");
					else	
					{
						showLoader("Please wait...");
						new Thread(new Runnable()
						{
							private int status=0;
							private boolean isCollected;
							
							@Override
							public void run()
							{
								VerifyRequestDO verifyRequestDO = new VerifyRequestDO();
								verifyRequestDO.movementCode 		= movementId;
								verifyRequestDO.movementType 		= movementType;
								verifyRequestDO.movementStatus 		= "99";
								verifyRequestDO.vecVanLodDOs = vecOrdProduct;
								Bitmap bitmap = getBitmap(myViewManager);
								if(bitmap != null)
								{
									verifyRequestDO.logisticSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
								}
								
								bitmap = getBitmap(myViewDriver);
								if(bitmap != null)
									verifyRequestDO.salesmanSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
								if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
								{
									final int isUpdated = uploadLoadRequest(verifyRequestDO);
									if(isUpdated==1)
									{
										status = 1;
										new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
										String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
										//loadVanStock_Sync("Loading Stock...", empNo);
										loadAllMovements_Sync("Loading Stock...", empNo);
										isCollected=true;
										
										
									}
								}
								else 
									status = 2;
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										dialog.dismiss();
										if(status == 1)
										{
											showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
													getString(R.string.OK), null, "verify", false);
										}
										else if(status == 2)
											showCustomDialog(VerifyItemInVehicle.this, "Alert !", "There is no internet connection. Please check your internet connection.", "OK", null, null);
										else
											showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), "Error occurred while verifying. Please try again.", getString(R.string.OK), null, "");
										hideLoader();
									}
								});
							}
						}).start();
					}
				}
				}
		});*/
		
		btnDriverCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				isVarifcationSignatureDone = false;
				isVarSignDoneDriver = false;
				if(myViewDriver != null)
					myViewDriver.clearCanvas();
			}
		});
		
		btnSKCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isVarifcationSignatureDone = false;
//				isVarSignDoneDriver = false;
				if(myViewManager != null)
					myViewManager.clearCanvas();
			}
		});
	}
	
	/*private boolean uploadLogData(LogDO logDO) 
	{
		boolean isUpload=true;
		try
		{
			if(logDO != null)
			{
			Vector<LogDO> vecLogs= new Vector<LogDO>();
			vecLogs.add(logDO);
			BooleanParser insertLoadParser = new BooleanParser(VerifyItemInVehicle.this);
				new ConnectionHelper(null).sendRequest(VerifyItemInVehicle.this,BuildXMLRequest.getAllLogsXml(vecLogs), insertLoadParser, ServiceURLs.ShipStockMovementsFromXML,preference);
				if((Integer)insertLoadParser.getData()>0)
					isUpload=true;
				else 
					isUpload=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			isUpload=false;
		}
		
		return isUpload;
	}*/
	private int uploadLoadRequest(VerifyRequestDO verifyRequestDO) 
	{
		try
		{
			if(verifyRequestDO != null)
			{
			BooleanParser insertLoadParser = new BooleanParser(VerifyItemInVehicle.this);
				new ConnectionHelper(null).sendRequest(VerifyItemInVehicle.this,BuildXMLRequest.VerifyRequestRequests(verifyRequestDO,preference), insertLoadParser, ServiceURLs.ShipStockMovementsFromXML,preference);
				return (Integer)insertLoadParser.getData();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public String bitMapToString(Bitmap bitmap)
	{
		String temp = "";
		if(bitmap != null)
		{
			 ByteArrayOutputStream baos=new  ByteArrayOutputStream();
	         bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
	         byte [] b		=	baos.toByteArray();
	         temp			=	Base64.encodeToString(b, Base64.DEFAULT);
		}
		return temp;
    }
	
	public class MyView extends View 
	{
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        int width = 480, height = 800;
        private boolean isDriver = false;
        
        @SuppressWarnings("deprecation")
		public MyView(Context c, boolean isDriver)
        {
            super(c);
            Display display = 	getWindowManager().getDefaultDisplay(); 
            width 			= 	display.getWidth();
            height 			= 	display.getHeight();
            this.isDriver = isDriver; 
            if(mBitmap != null)
            	mBitmap.recycle();
            
            mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas 		= 	new Canvas(mBitmap);
            mPath 			= 	new Path();
            mBitmapPaint	= 	new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(2);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y)
        {
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) 
        {
          float dx = Math.abs(x - mX);
          float dy = Math.abs(y - mY);
          if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
          {
           mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
           mX = x;
           mY = y;
          }
	    }
	    private void touch_up()
	    {
	         mPath.lineTo(mX, mY);
	         mCanvas.drawPath(mPath, mPaint);
	         mPath.reset();
	     }
        
	    public void clearCanvas()
	    {
	    	mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
	        mCanvas 		= 	new Canvas(mBitmap);
	    	invalidate();
	    }
	    
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
             x = event.getX();
             y = event.getY();
            
            switch (event.getAction()) 
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                	if(this.isDriver)
                		isVarSignDoneDriver = true;
                	else
                		isVarifcationSignatureDone = true;
                    touch_move(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        	return true;
       }
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	private HashMap<String, UOMConversionFactorDO> hashArrUoms;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				hmInventory 		= 	new OrderDetailsDA().getAvailInventoryQtys_Temp("");
				//vecOrdProduct = new InventoryDA().getAllItemToVerifyByLoadId(movementId,StringUtils.getInt(movementType),hmInventory);
				vecOrdProduct = new InventoryDA().getAllItemToVerifyByLoadId(movementId,StringUtils.getInt(movementType));
				unloadDo=new InventoryDA().getUnloadmovement(movementId);
			//	vecOrdProduct = new VehicleDA().getAllItemToVerifyByMovementID(movementId);
				hashArrUoms =new ProductsDA().getUOMConversionByVectorOfItem(vecOrdProduct);
				printApproved=new SettingsDA().getSettingsValue(AppConstants.MO_PRINT);
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

	private void navigateFromVerification()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
				count = new InventoryDA().getApprovedCount();;
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(count>0){
							finish();
						
						}
						else if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0){
							
							Intent intent = new Intent(VerifyItemInVehicle.this, OdometerReadingActivity.class);
							intent.putExtra("isStartDay", true);
							intent.putExtra("image_path", "");
							intent.putExtra("image_path_driver", "");
							startActivity(intent);
							setResult(1111);
							finish();
						}
						else{
							finish();
						}
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	private View view;

	class TextChangeListener implements TextWatcher {

		private boolean isActive;
		public TextChangeListener() {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			}
		
		public void setActive(boolean isActive)
		{
			this.isActive = isActive;
		}

		@Override
		public void afterTextChanged(Editable s) {
			if(view!=null && listScrollState == 0 && isActive)
			{
				VanLoadDO vanLoadDO = (VanLoadDO) view.getTag();
				float qty=StringUtils.getInt(s.toString());
				if(vanLoadDO != null)
				{
//					if(StringUtils.getInt(movementType) == LoadRequestDO.MOVEMENT_TYPE_OTHER_VAN_TRANSFER_IN)
//					{
//					
//						if(qty>vanLoadDO.quantityLevel1)
//						{
//							showToast("Entered quantity should not be greater than requested quantity.");
//							vanLoadDO.InProcessQuantity=vanLoadDO.quantityLevel1;
//							((EditText)view).setText(""+vanLoadDO.InProcessQuantity);
//						}
//						else
//							vanLoadDO.InProcessQuantity = qty;
//						
//					}
//					else
//					{ public static int LOAD_STOCK   = 1;
						
//							if(qty>vanLoadDO.InProcessQuantity)
				/*	if(qty>vanLoadDO.SellableQuantity)
							{
								vanLoadDO.tempQuantity = 0;
								showToast("Entered quantity should not be greater than approved quantity.");
//								vanLoadDO.ShippedQuantity=vanLoadDO.InProcessQuantity;
//								vanLoadDO.ShippedQuantity=vanLoadDO.shippedQuantityLevel1;
								setActive(false);
								((EditText)view).setText(""+StringUtils.getInt(""+vanLoadDO.SellableQuantity));
								setActive(true);
							}
							else if(StringUtils.getInt(movementType) == AppStatus.LOAD_STOCK){
//								vanLoadDO.tempQuantity = vanLoadDO.InProcessQuantity-qty;
								vanLoadDO.tempQuantity = vanLoadDO.SellableQuantity-qty;
								}*/
					if(qty>vanLoadDO.inProccessQty)
					{
						vanLoadDO.tempQuantity = 0;
						showToast("Entered quantity should not be greater than approved quantity.");
//						vanLoadDO.ShippedQuantity=vanLoadDO.InProcessQuantity;
//						vanLoadDO.ShippedQuantity=vanLoadDO.shippedQuantityLevel1;
						setActive(false);
						((EditText)view).setText(""+StringUtils.getInt(""+vanLoadDO.inProccessQty));
						setActive(true);
					}
					else if(StringUtils.getInt(movementType) == AppStatus.LOAD_STOCK){
//						vanLoadDO.tempQuantity = vanLoadDO.InProcessQuantity-qty;
						vanLoadDO.tempQuantity = vanLoadDO.inProccessQty-qty;
						}
					
//					}
				}
			}
			
		}
	}
	
	LoadRequestDO loadRequestDO = null;
	private boolean generateUnloadRequest()
	{
			InventoryDA inventoryDA = new InventoryDA();

				loadRequestDO = new LoadRequestDO();
				loadRequestDO.MovementCode = "" + unloadMovementCode;
				loadRequestDO.PreMovementCode=movementId;
				loadRequestDO.AppMovementId = ""
						+ StringUtils.getUniqueUUID();
				loadRequestDO.OrgCode = ""/*
						+ preference.getStringFromPreference(
								Preference.ORG_CODE, "")*/;
				loadRequestDO.UserCode = ""
						+ preference.getStringFromPreference(
								Preference.EMP_NO, "");
				loadRequestDO.WHKeeperCode = ""
						+ preference.getStringFromPreference(
								Preference.EMP_NO, "");
				loadRequestDO.CurrencyCode = ""
						+ preference.getStringFromPreference(
								Preference.CURRENCY_CODE, "");
				loadRequestDO.JourneyCode = "DUB";
				loadRequestDO.MovementDate = CalendarUtils
						.getCurrentDateTime();
				loadRequestDO.MovementNote = "TEST";
				//loadRequestDO.MovementType = "" + AppStatus.UNLOAD_STOCK;
				loadRequestDO.MovementType = "" + AppStatus.UNLOAD_COLLECTED_STOCK;
				//Source Vehicle Code
				/*loadRequestDO.SourceVehicleCode = ""+ preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
				//Destination Vehicle Code
				loadRequestDO.DestinationVehicleCode = ""+ preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");*/
				
				loadRequestDO.geoLatitude = preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0.0);
				loadRequestDO.geoLongitude = preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0.0);
				
				loadRequestDO.Status = "N";
				loadRequestDO.VisitID = "0";
				loadRequestDO.MovementStatus = ""+ loadRequestDO.MOVEMENT_STATUS_APPROVED;
				
				loadRequestDO.CreatedOn = loadRequestDO.MovementDate;
				loadRequestDO.ApproveByCode = "0";
				loadRequestDO.ApprovedDate = loadRequestDO.MovementDate;
				loadRequestDO.JDETRXNumber = loadRequestDO.MovementCode;
				loadRequestDO.ISStampDate = loadRequestDO.MovementDate;
				loadRequestDO.ISFromPC = "0";
				loadRequestDO.OperatorCode = "0";
				loadRequestDO.IsDummyCount = "0";
				loadRequestDO.ModifiedDate = CalendarUtils
						.getOrderPostDate();
				loadRequestDO.ModifiedTime = CalendarUtils.getRetrunTime();
				loadRequestDO.PushedOn = loadRequestDO.MovementDate;
				loadRequestDO.ModifiedOn = loadRequestDO.MovementDate;

//						if (WHCode != null)
//							loadRequestDO.WHCode = "" + WHCode;// Change surely ask Basant
				loadRequestDO.SourceVehicleCode	    =Source;
				loadRequestDO.DestinationVehicleCode=Destination;
				loadRequestDO.ProductType = "Sellable";
				loadRequestDO.vecItems = arrUnloadItems;
				
				
				
//						loadRequestDO.RefNumber = movementId;
				boolean hasUpdated = inventoryDA.insertLoadRequest_(loadRequestDO);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						uploadData(AppStatus.POST_LOAD_REQUEST, AppStatus.TODAY_DATA);
					}
				});
				return hasUpdated;
	}
	private boolean updateLoadQuantity()
	{
		boolean status = false;
		if(vecOrdProduct!=null && vecOrdProduct.size()>0)
		{
			arrUnloadItems = new ArrayList<LoadRequestDetailDO>();
			int count = 0;
			for(VanLoadDO vanLoadDO: vecOrdProduct)
			{
				if(vanLoadDO.tempQuantity > 0)
				{
					vanLoadDO.CancelledQuantity     	= vanLoadDO.tempQuantity;
				//	vanLoadDO.ShippedQuantity 			= vanLoadDO.ShippedQuantity-vanLoadDO.CancelledQuantity ;
					vanLoadDO.ShippedQuantity 			= vanLoadDO.inProccessQty-vanLoadDO.CancelledQuantity ;
					
				
					LoadRequestDetailDO loDetailDO = new LoadRequestDetailDO();
					loDetailDO.LineNo = "" + count++;
					loDetailDO.MovementCode = "" + unloadMovementCode;
					loDetailDO.ItemCode = "" + vanLoadDO.ItemCode;
					loDetailDO.OrgCode = ""  /*+preference.getStringFromPreference(Preference.ORG_CODE, "")*/;
					loDetailDO.ItemDescription = "" + vanLoadDO.Description;
					loDetailDO.ItemAltDescription = "" + vanLoadDO.Description;
					loDetailDO.MovementStatus = ""
							+ loadRequestDO.MOVEMENT_STATUS_APPROVED;
					loDetailDO.UOM = "" + vanLoadDO.UOM;
					loDetailDO.QuantityLevel1 = (int) vanLoadDO.CancelledQuantity;
					loDetailDO.QuantityBU = (int) vanLoadDO.CancelledQuantity;
					loDetailDO.InProcessQuantity = vanLoadDO.CancelledQuantity;
					loDetailDO.ShippedQuantity = 0;

					loDetailDO.NonSellableQty = 0;
					loDetailDO.CurrencyCode = ""
							+ preference.getStringFromPreference(
									Preference.CURRENCY_CODE, "");
					loDetailDO.PriceLevel1 = 0;
					loDetailDO.PriceLevel2 = 0;
					loDetailDO.PriceLevel3 = 0;
					loDetailDO.MovementReasonCode = "NONE";
					loDetailDO.ExpiryDate = "" + CalendarUtils
							.getCurrentDateTime();
					loDetailDO.Note = "NONE";
					loDetailDO.Status = "Pending";
					loDetailDO.DistributionCode = ""  /*+preference.getStringFromPreference(Preference.ORG_CODE, "");*/;
					loDetailDO.CreatedOn = "" + CalendarUtils
							.getCurrentDateTime();
					loDetailDO.ModifiedDate = "" + loDetailDO.CreatedOn;
					loDetailDO.ModifiedTime = "" + loDetailDO.CreatedOn;
					loDetailDO.PushedOn = "" + loDetailDO.CreatedOn;
					loDetailDO.CancelledQuantity = 0;
					
					/*if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
					{
						loadRequestDO.SourceVehicleCode	    =""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
						loadRequestDO.DestinationVehicleCode=""+tvSelectVehicle.getText().toString();
					}
					else
					{
						loadRequestDO.SourceVehicleCode	    =""+tvSelectVehicle.getText().toString();
						loadRequestDO.DestinationVehicleCode=""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					}
*/
					loDetailDO.ModifiedOn = "" + loDetailDO.CreatedOn;
					arrUnloadItems.add(loDetailDO);
					
					status = true;
				}
			}
			

		}
		
//		if(status)
//			unloadMovementCode = ""
//					+ new InventoryDA().getMovementId(preference
//							.getStringFromPreference(
//									Preference.SALESMANCODE, ""));
		
		if(status)
		{
			OfflineDA offlineDA = new OfflineDA();

			String code = offlineDA.getNextSequenceNumber(OfflineDataType.MOVEMENT);
			if(code!=null && code.equalsIgnoreCase(""))
				unloadMovementCode = code;
		}
		
		return status;
	}
	private static class ViewHolder
	{
		LinearLayout llCellClick;
		TextView tvProductKey	;
		TextView tvVendorName	;
		EditText etUOM	;
		EditText etQty,receivedQty,requestedUnits;
		TextChangeListener textChangeListener;
	}
	private int validatingAllItems()
	{
		int responce = 0;
		if(vecOrdProduct == null || vecOrdProduct.size() == 0)//tvNoItemFound.isShown()
			responce = 1;
		else if((isClicked.size() != vecOrdProduct.size()) || (vecOrdProduct.size() > ordersheetadapter.vecOrder.size()))
			responce =  2;
		return responce;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(1111);
		finish();
		
	}
	
	public void submitLoadWithunloadItem(final Dialog dialog){

		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			private int status=0;
			//private boolean isCollected;

			@Override
			public void run()
			{
				VerifyRequestDO verifyRequestDO = new VerifyRequestDO();
				verifyRequestDO.movementCode 		= movementId;
				verifyRequestDO.movementType 		= movementType;
				verifyRequestDO.movementStatus 		= "99";
				verifyRequestDO.vecVanLodDOs = vecOrdProduct;
				Bitmap bitmap = getBitmap(myViewManager);
				if(bitmap != null)
				{
					verifyRequestDO.logisticSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
				}

				bitmap = getBitmap(myViewDriver);
				//									final int isUpdated;
				if(bitmap != null)
					verifyRequestDO.salesmanSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
				String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
				if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
				{
					if(StringUtils.getInt(movementType) == AppStatus.LOAD_STOCK) 
					{
						int isUpdated = uploadLoadRequest(verifyRequestDO);
						if(isUpdated==1)
						{
							boolean hasUpdated = new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
							
							
							if(hasUpdated){
								
								boolean hastoUpdate = true;
								if(hasItemsToUnload){
									hastoUpdate = generateUnloadRequest();
								}
								
								if(hastoUpdate){
									
									status = 1;
									//loadVanStock_Sync(getString(R.string.loading_stock), empNo);
									loadAllMovements_Sync(getString(R.string.loading_stock), empNo);
									
									//isCollected=true;
								}
								
							/*	disableVerify();
								//automatic print 
								//check if it is approved 
								if(printApproved){
									Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
									intent.putExtra("itemforVerification", vecOrdProduct);
									intent.putExtra("strMovementNo", movementId);
									intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
									startActivityForResult(intent, 1000);
								}
*/								
							}
						}
					}
					else
					{
						int isUpdated = uploadLoadRequest(verifyRequestDO);

						if(isUpdated==1)
						{
							status = 1;
							new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
							//isCollected=true;
							Log.e("isUpdated", "sync start");
							loadVanStock_Sync("Loading Stock...",empNo);
							Log.e("isUpdated", "sync end");
							
						}
					}

				}
				else
					status = 2;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						dialog.dismiss();
						if(status == 1)
						{
							showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
									getString(R.string.OK), null, "verify", false);
						}
						else if(status == 2)
							showCustomDialog(VerifyItemInVehicle.this, getString(R.string.alert), getString(R.string.no_internet), getString(R.string.OK), null, null);
						else
							showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), getString(R.string.error_occured_while_verification), getString(R.string.OK), null, "");
						hideLoader();
					}
				});
			}
		}).start();
	
	}

	
	/*
	 * for unload there is no process of shipping
	 */
	public void submitLoad(final Dialog dialog){

		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			private int status=0;
			@Override
			public void run()
			{
				
				if(load_type == AppStatus.UNLOAD_S_STOCK || load_type ==  AppStatus.UNLOAD_STOCK){
					final boolean isUpdated=inventoryDA.updateMovemetStatuStatus(movementId,load_type);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							dialog.dismiss();
							if(isUpdated){
								showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
										getString(R.string.OK), null, "verify", false);
								uploadData(AppStatus.POST_UNLOAD_REQUEST, AppStatus.TODAY_DATA);
							}
						}
					});
					
				}else{
						
						VerifyRequestDO verifyRequestDO = new VerifyRequestDO();
						verifyRequestDO.movementCode 		= movementId;
						verifyRequestDO.movementType 		= movementType;
						verifyRequestDO.movementStatus 		= "99";
						verifyRequestDO.vecVanLodDOs = vecOrdProduct;
						Bitmap bitmap = getBitmap(myViewManager);
						if(bitmap != null)
						{
							verifyRequestDO.logisticSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
						}
						
						bitmap = getBitmap(myViewDriver);
						if(bitmap != null)
							verifyRequestDO.salesmanSignature  = BitmapsUtiles.saveVerifySignature(bitmap);
						if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
						{
							ShipStockLog(verifyRequestDO.movementCode,"before "+ServiceURLs.ShipStockMovementsFromXML);
							final int isUpdated = uploadLoadRequest(verifyRequestDO);
							if(isUpdated==1)
							{
								status = 1;
								boolean hasUpdated=new InventoryDA().updateMovemetStatuStatus(movementId, 100,verifyRequestDO.vecVanLodDOs);
								if(hasUpdated){
									String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
									loadAllMovements_Sync(getString(R.string.loading_stock), empNo);
									ShipStockLog(verifyRequestDO.movementCode,"after "+ServiceURLs.ShipStockMovementsFromXML);
								}
								
							}
						}
						else 
							status = 2;
						
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								dialog.dismiss();
								if(status == 1)
								{
									showCustomDialog(VerifyItemInVehicle.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
											getString(R.string.OK), null, "verify", false);
								}
								else if(status == 2)
									showCustomDialog(VerifyItemInVehicle.this, getString(R.string.alert), getString(R.string.no_internet), getString(R.string.OK), null, null);
								else
									showCustomDialog(VerifyItemInVehicle.this, getString(R.string.warning), getString(R.string.error_occured_while_verification), getString(R.string.OK), null, "");
								hideLoader();
							}
						});
					}
			}
		}).start();

	}
	
private void validatePasscode(final String passcode,final Dialog dialog){

	showLoader(getString(R.string.please_wait));
	new Thread(new Runnable()
	{
		@Override
		public void run() 
		{
			NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),passcode);
			//Need to remove it.
			if(nameIDDO == null && passcode.equalsIgnoreCase(AppConstants.PASSCODE))
			{
				nameIDDO         = new NameIDDo();
				nameIDDO.strId   = passcode;
				nameIDDO.strName = passcode;
			}
			
			if(nameIDDO != null && nameIDDO.strId!= null && (nameIDDO.strId.equalsIgnoreCase("0") || nameIDDO.strId.equalsIgnoreCase(AppConstants.PASSCODE)))
			{
				new CommonDA().updatePasscodeStatus(nameIDDO.strName);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						performPasscodeAction(dialog);
					}
				});
			}
			else if(nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("1"))
				showCustomDialog(VerifyItemInVehicle.this, getResources().getString(R.string.warning),"Entered passcode is already used, please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
			else
				showCustomDialog(VerifyItemInVehicle.this, getResources().getString(R.string.warning),"Please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
			hideLoader();
		}
	}).start();

}

public void performPasscodeAction(final Dialog dialog) {
	if (hasItemsToUnload)
		submitLoadWithunloadItem(dialog);
	else
		submitLoad(dialog);
	
	//submitLoad(dialog);
}

private void ShipStockLog(String MovementCode, String time){
	ArrayList<VanLoadDO> vecVanLodDOs=new InventoryDA().getMovementdata(MovementCode);
	LogDO logDO=new LogDO();
	logDO.userId=preference.getStringFromPreference(Preference.EMP_NO,"");
	logDO.type="Inprocess"+time;
	logDO.key=MovementCode;
	String data="";
	for(VanLoadDO vanload:vecVanLodDOs){
		data+="ItemCode : "+vanload.ItemCode+", movmentStatus :"+vanload.movementStatus+", InprocessQty : "+vanload.inProccessQty+", shippedQty : "+vanload.ShippedQuantity+", cancelledQty:"+vanload.CancelledQuantity+"\n";
	}
	logDO.data=data;
	logDO.deviceTime=CalendarUtils.getCurrentDateTime();
	boolean isFailed=new CustomerDA().insertLog(logDO);
	if(!isFailed){
		/*runOnUiThread(new Runnable() {
			
			@Override
			public void run() {*/
		if(isNetworkConnectionAvailable(VerifyItemInVehicle.this)){
			
			boolean isUpload=uploadLogData(logDO);	
			if(isUpload){
				new JourneyPlanDA().deleteLog(logDO.logId); 	
			}
		}
			/*}
		});*/
	}
}
}
