package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.winit.baskinrobbin.salesman.adapter.AddItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class ReturnStockFromVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView, llReturnedStock, llUnloadStock ;
	private Button btnOrdersheetVerify,btnOrdersheetReport, btnScan, btnAdd, btnFinish;
	private InventoryItemAdapter inventoryItemAdapter;
	private CustomeListAdapter customeListAdapter;
	private ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
	private TextView tvItemCode, tvCases, tvUnits, tvOrdersheetHeader, tvNoItemFound, tvItemList, tvGatePass;
	private TextView tvOrdersheetHeaderReturn, tvItemListReturn, tvItemCodeReturn, tvCasesReturn, tvUnitsReturn;
	private ListView lvInventoryItems;
	private Vector<String> vecCategory;
	private Vector<DeliveryAgentOrderDetailDco> vecSearchedItemd;
	private Vector<InventoryObject> vecInventoryItems;
	private Paint mPaint;
	private AddItemAdapter adapter;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.return_stock_from_vehicle, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		setTypeFace(llOrderSheet);
		
		preference.saveBooleanInPreference("invoiceprinted", false);
		preference.commitPreference();
		
		tvOrdersheetHeader.setText("Stock Unload");
		tvOrdersheetHeaderReturn.setText("Returned Inventory");
		
		inventoryItemAdapter = new InventoryItemAdapter(vecOrdProduct);
		lvInventoryItems.setAdapter(customeListAdapter = new CustomeListAdapter(new Vector<InventoryObject>()));
		
		//showing Loader
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				
				vecOrdProduct	=	new VehicleDA().getAllItemToUnload(CalendarUtils.getOrderPostDate());
				vecInventoryItems = new OrderDetailsDA().getReturnInventoryQty(CalendarUtils.getOrderPostDate(), AppConstants.RETURNORDER);
				final boolean isReturnStatus = new VehicleDA().getReturnstockStatus(CalendarUtils.getOrderPostDate());
		
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(vecInventoryItems == null)
							vecInventoryItems = new Vector<InventoryObject>();
						
						inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
						if(vecInventoryItems != null && vecInventoryItems.size()>0)
						{
							if(vecOrdProduct == null || vecOrdProduct.size() < 0)
								llUnloadStock.setVisibility(View.GONE);
							customeListAdapter.refresh(vecInventoryItems);
						}
						else
						{
							llReturnedStock.setVisibility(View.GONE);
						}
						btnOrdersheetVerify.setVisibility(View.VISIBLE);
						hideLoader();
					}
				});
			}
		}).start();
		
		btnOrdersheetVerify.setText(" Submit ");
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isItemAdded(vecOrdProduct))
				{
					showLoader("Loading");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
							new VehicleDA().updateReturnstock(vecOrdProduct, empNo);
							
							Vector<InventoryObject> vecInventoryObjects = new Vector<InventoryObject>();
							for (int i = 0; i < vecOrdProduct.size(); i++)
							{
								DeliveryAgentOrderDetailDco dco = vecOrdProduct.get(i);
								if(dco.totalCases > 0)
								{
									InventoryObject inventoryObject = new InventoryObject();
									inventoryObject.itemCode=dco.itemCode;
									inventoryObject.itemDescription=dco.itemDescription;
									inventoryObject.PrimaryQuantity=dco.preCases;
									inventoryObject.deliveredQty=dco.delvrdCases;
									inventoryObject.availQty=0;
									inventoryObject.Qty_out=0.0f;
									inventoryObject.Qty_In=0.0f;
									inventoryObject.SecondaryQuantity=dco.preUnits;
									inventoryObject.unitPerCases=dco.unitPerCase;
									inventoryObject.VMSalesmanInventoryId="";
									inventoryObject.Date="";
									inventoryObject.SalesmanCode="";
									inventoryObject.IsAllVerified = false;
									inventoryObject.UOM="";
									
									inventoryObject.availCases		=	0.0f;
									inventoryObject.deliveredCases	=	dco.delvrdCases;
									
									vecInventoryObjects.add(inventoryObject);
								}
							}
							
							new VehicleDA().insertReturnInventory(vecInventoryObjects);
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									hideLoader();
									showCustomDialog(ReturnStockFromVehicle.this, getString(R.string.successful), "Unload stock request has been submitted successfully.", "OK", null, "verify", false);
								}
							});
						}
					}).start();
				}
				else
				{
					showCustomDialog(ReturnStockFromVehicle.this, getString(R.string.warning), "Please add at least one item having quantity more than zero.", "OK", null, "");
				}
			}
		});
		btnOrdersheetReport.setText("Print");
		btnOrdersheetReport.setVisibility(View.GONE);
		//Button order sheet Report
		btnOrdersheetReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(ReturnStockFromVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.LOAD_VERIFICATION);
//				startActivityForResult(intent, 1000);
				showToast("Print functionality is in progress.");
				//Harcoded
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
		
		btnScan.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ReturnStockFromVehicle.this, CaptureActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
		
		btnFinish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		llReturnedStock 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llReturnedStock);
		llUnloadStock			=	(LinearLayout)llOrderSheet.findViewById(R.id.llUnloadStock);
		
		btnScan					=	(Button)llOrderSheet.findViewById(R.id.btnScan);
		btnAdd					=	(Button)llOrderSheet.findViewById(R.id.btnAdd);
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		tvCases					=	(TextView)llOrderSheet.findViewById(R.id.tvCases);
		tvUnits					=	(TextView)llOrderSheet.findViewById(R.id.tvUnits);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		btnFinish				=	(Button)llOrderSheet.findViewById(R.id.btnFinish);
		
		tvOrdersheetHeaderReturn=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeaderReturn);
		tvItemListReturn		=	(TextView)llOrderSheet.findViewById(R.id.tvItemListReturn);
		tvItemCodeReturn		=	(TextView)llOrderSheet.findViewById(R.id.tvItemCodeReturn);
		tvCasesReturn			=	(TextView)llOrderSheet.findViewById(R.id.tvCasesReturn);
		tvUnitsReturn			=	(TextView)llOrderSheet.findViewById(R.id.tvUnitsReturn);
		tvGatePass				=	(TextView)llOrderSheet.findViewById(R.id.tvGatePass);
		lvInventoryItems		=	(ListView)llOrderSheet.findViewById(R.id.lvInventoryItems);
		
		/*//setting type-faces
		tvOrdersheetHeaderReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemListReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCasesReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnitsReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvGatePass.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		tvGatePass.setText("Cases");
		tvUnits.setText("Units");
		
		/*tvOrdersheetHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetReport.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemList.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnScan.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAdd.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinish.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnScan.setVisibility(View.GONE);
		btnAdd.setVisibility(View.GONE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
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
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					vecUpdatedData = inventoryItemAdapter.getModifiedData();
					final ArrayList<DeliveryAgentOrderDetailDco> vecTemp = new ArrayList<DeliveryAgentOrderDetailDco>();
					for(DeliveryAgentOrderDetailDco obj : vecUpdatedData)
					{
						if(obj.totalCases > 0)
						{
							obj.etCases = null;
							obj.etUnits = null;
							vecTemp.add(obj);
						}
					}
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							Intent intent = new Intent(ReturnStockFromVehicle.this, VerifyReturnItemFromVehicle.class);
							intent.putExtra("updatededDate", vecTemp);
							startActivityForResult(intent, 1111);							
						}
					});
				}
			}).start();
		}
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(ReturnStockFromVehicle.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		objAddNewSKUDialog.show();
		
		TextView tvItemCodeLabel			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItemCodeLabel);
		TextView tvItem_DescriptionLabel	=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItem_DescriptionLabel);
		TextView tvAdd_New_SKU_Item			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvAdd_New_SKU_Item);
		
		final EditText etCategory	 		=	(EditText)objAddNewSKUDialog.findViewById(R.id.etCategory);
		final EditText etSearch	 			=	(EditText)objAddNewSKUDialog.findViewById(R.id.etSearch);
		
		final ImageView cbList 				=	(ImageView)objAddNewSKUDialog.findViewById(R.id.cbList);
		final ListView lvPopupList		 	=	(ListView)objAddNewSKUDialog.findViewById(R.id.lvPopupList);
		
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
		
		final LinearLayout llList			=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llList);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		/*tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		vecCategory = new Vector<String>();
		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
		final Button btnSearch = new Button(ReturnStockFromVehicle.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(ReturnStockFromVehicle.this, "Select Category", true);
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
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etSearch.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(ReturnStockFromVehicle.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
					showLoader(getResources().getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId = "", catgName = etCategory.getText().toString(), orderedItems = "";
							
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
							
							for(int i=0; i<vecOrdProduct.size(); i++)
							{
								for(DeliveryAgentOrderDetailDco objProductDO : vecOrdProduct)
									orderedItems = orderedItems + "'"+objProductDO.itemCode+"',";
							}
							
//							vecSearchedItemd = new ProductsDA().getProductsByCategoryId_(catgId, orderedItems);
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
										lvPopupList.setAdapter(adapter = new AddItemAdapter(vecSearchedItemd,ReturnStockFromVehicle.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddItemAdapter( new Vector<DeliveryAgentOrderDetailDco>(),ReturnStockFromVehicle.this));
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
					Vector<DeliveryAgentOrderDetailDco> vecTemp = new Vector<DeliveryAgentOrderDetailDco>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						DeliveryAgentOrderDetailDco obj = (DeliveryAgentOrderDetailDco) vecSearchedItemd.get(index);
						String strText = obj.itemCode;
						String strDesc = obj.itemDescription;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDesc.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
					{
						adapter.refresh(vecTemp);
						lvPopupList.setVisibility(View.VISIBLE);
						tvNoItemFound.setVisibility(View.GONE);
					}
					else
					{
						lvPopupList.setVisibility(View.GONE);
						tvNoItemFound.setVisibility(View.VISIBLE);
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
				Vector<DeliveryAgentOrderDetailDco> veciItems= new Vector<DeliveryAgentOrderDetailDco>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(vecOrdProduct == null)
					vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
				
				vecOrdProduct.addAll(veciItems);
				
				objAddNewSKUDialog.dismiss();
				inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
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
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			DeliveryAgentOrderDetailDco dco = (DeliveryAgentOrderDetailDco) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
				inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	ArrayList<DeliveryAgentOrderDetailDco> vecUpdatedData;
	private void updateStock()
	{
		vecUpdatedData = inventoryItemAdapter.getModifiedData();
		
		if(vecUpdatedData != null && vecUpdatedData.size()> 0)
			showCustomDialog(ReturnStockFromVehicle.this, getString(R.string.successful), "Stock has been submitted successfully, Please press continue to verify.", getResources().getString(R.string.Continue), null, "verify", false);
		else
			showCustomDialog(ReturnStockFromVehicle.this, getResources().getString(R.string.warning), "Error occurred while verifying. Please try again.", getResources().getString(R.string.OK), null, "");
	}
	
	public class InventoryItemAdapter
	{
		ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
		private View view;
		
		class TextChangeListener implements TextWatcher
		{
			String type = "";
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
				DeliveryAgentOrderDetailDco objItem = null;
				if(view != null)
				{
					objItem = (DeliveryAgentOrderDetailDco) view.getTag();
				}
				if(type.equalsIgnoreCase("case"))
				{
					if(objItem != null)
					{
						objItem.preCases = StringUtils.getFloat(s.toString());
						objItem.totalCases =  objItem.preCases + (float)objItem.preUnits/(float)objItem.unitPerCase;
						
						if(objItem.totalCases > objItem.availQty)
						{
							objItem.preCases = 0;
							objItem.totalCases = (float)objItem.preUnits/(float)objItem.unitPerCase;
							showToast("Entered quantity should not be greater than inventory quantity.");
							if(objItem.etCases != null)
							{
								objItem.etCases.setText("");    
							}
						}
					}
				}
				else if(type.equalsIgnoreCase("unit"))
				{
					if(objItem != null)
					{
						objItem.preUnits = StringUtils.getInt(s.toString());
						objItem.totalCases =  objItem.preCases + (float)objItem.preUnits/(float)objItem.unitPerCase;
						
						if(objItem.totalCases > objItem.availQty)
						{
							objItem.preUnits = 0;
							objItem.totalCases = objItem.preCases;
							showToast("Entered quantity should not be greater than inventory quantity.");
							if(objItem.etUnits != null)
							{
								objItem.etUnits.setText("");    
							}
						}
					}
				}
			}
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
		}
		
		class FocusChangeListener implements OnFocusChangeListener
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					view = v;
				}
			}
		}
		
		public InventoryItemAdapter(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct)
		{
			this.vecOrdProduct = vecOrdProduct;
		}
	
		public ArrayList<DeliveryAgentOrderDetailDco> getModifiedData()
		{
			return vecOrdProduct;
		}

		public void getChildView(LinearLayout llLayoutMiddle)
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			for(final DeliveryAgentOrderDetailDco productDO : vecOrdProduct)
			{
				LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_add_stock,null);
				
				TextView tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
				TextView tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
				TextView evCases			= (TextView)convertView.findViewById(R.id.etCases);
//				EditText evUnits			= (EditText)convertView.findViewById(R.id.etGatePassQt_pcs);
//				EditText etGatePassQt		= (EditText)convertView.findViewById(R.id.etGatePassQt);
				
				ImageView ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
				
//				if(productDO.itemCode.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).itemCode))
//					etGatePassQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
				
				ivAcceptCheckItems.setVisibility(View.GONE);
				
				tvProductKey.setText(productDO.itemCode);
//				tvProductKey.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvVendorName.setText(productDO.itemDescription);
//				tvVendorName.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				
//				etGatePassQt.setTag(productDO);
//				evUnits.setTag(productDO);
//				
//				evCases.setText(""+productDO.availQty);
//				
//				if(productDO.preCases > 0)
//					etGatePassQt.setText(""+productDO.preCases);
//				else
//					etGatePassQt.setText("");
//				
//				if(productDO.preUnits > 0)
//					evUnits.setText(""+productDO.preUnits);
//				else
//					evUnits.setText("");
//					
//				etGatePassQt.setOnFocusChangeListener(new FocusChangeListener());
//				etGatePassQt.addTextChangedListener(new TextChangeListener("case"));
//				
//				evUnits.setOnFocusChangeListener(new FocusChangeListener());
//				evUnits.addTextChangedListener(new TextChangeListener("unit"));
//				
//				productDO.etCases = etGatePassQt;
//				productDO.etUnits = evUnits;
				
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(40 * px)));
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						
					}
				});
				llLayoutMiddle.addView(convertView);
			}
		}
		
		//Method to refresh the List View
		private void refresh(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProducts,LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			this.vecOrdProduct = vecOrdProducts;
			if(vecOrdProduct != null && vecOrdProducts.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				llLayoutMiddle.setVisibility(View.VISIBLE);
				getChildView(llLayoutMiddle);
			}
			else
			{
				tvNoItemFound.setVisibility(View.VISIBLE);
				llLayoutMiddle.setVisibility(View.GONE);
			}
		}
	}
	
	public class CustomeListAdapter extends BaseAdapter
	{

		Vector<InventoryObject> vecInventoryItems;
		public CustomeListAdapter(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
		}
		@Override
		public int getCount() 
		{
			return vecInventoryItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecInventoryItems.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			InventoryObject inventoryObject = vecInventoryItems.get(position);
			if(convertView == null)
				convertView = inflater.inflate(R.layout.inventory_qty_cell, null);
			
			TextView tvItemCodeText = (TextView)convertView.findViewById(R.id.tvItemCodeText);
			TextView tvDescription 	= (TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvTotalQty 	= (TextView)convertView.findViewById(R.id.tvTotalQty);
			TextView tvDeliveredQty = (TextView)convertView.findViewById(R.id.tvDeliveredQty);
			TextView tvAvailQty 	= (TextView)convertView.findViewById(R.id.tvAvailQty);
			TextView tvUOM 			= (TextView)convertView.findViewById(R.id.tvUOM);
			
			/*tvItemCodeText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvUOM.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvTotalQty.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			*/
			tvDeliveredQty.setVisibility(View.GONE);
			tvAvailQty.setVisibility(View.GONE);
			
			tvItemCodeText.setText(inventoryObject.itemCode);
			tvDescription.setText(inventoryObject.itemDescription);
			tvTotalQty.setText(""+(inventoryObject.PrimaryQuantity >= 0 ? inventoryObject.PrimaryQuantity : 0));
			tvUOM.setText(""+inventoryObject.UOM);
			
			return convertView;
		}
		public void refresh(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
			if(vecInventoryItems!=null && vecInventoryItems.size()>0)
			{
				lvInventoryItems.setVisibility(View.VISIBLE);
//				tvResultOfSearch.setVisibility(View.GONE);
				notifyDataSetChanged();
			}
			else
			{
				lvInventoryItems.setVisibility(View.GONE);
//				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private boolean isItemAdded(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct)
	{
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) {
			if(deliveryAgentOrderDetailDco.preCases >0 || deliveryAgentOrderDetailDco.preUnits > 0)
			{
				return true;
			}
		}
		return false;
	}
}
