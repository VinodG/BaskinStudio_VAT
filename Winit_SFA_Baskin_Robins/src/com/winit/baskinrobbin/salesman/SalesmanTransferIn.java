package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.AddItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TransferInOutDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.EmployeeDo;
import com.winit.baskinrobbin.salesman.dataobject.Item;
import com.winit.baskinrobbin.salesman.dataobject.TransferInoutDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

@SuppressLint("DefaultLocale")
public class SalesmanTransferIn extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llReturnSave;
	private TextView tvLu, tvCode, tvCases, tvUnits;
	private Button  btnAddItems, btnSave, btnFinish, btnPrint;
	private TextView tvNoOrder;
	private InventoryItemAdapter inventoryItemAdapter;
	private AddItemAdapter adapter;
	private LinearLayout llLayoutMiddle;
	private TextView tvEmpNo, tvEmpNoValue;
	public static ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
	private Vector<String> vecCategory;
	private Vector<DeliveryAgentOrderDetailDco> vecSearchedItemd;
	private Vector<VehicleDO> vector;
	private Vector<TransferInoutDO> veTransferInoutDOs;
	private TextView tvSaleman, tvSalemanValue;
	private Vector<EmployeeDo> vectorEmployees;
	private TransferInoutDO transferInoutDO;
	@Override 
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.transfer_in_new, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		intialiseControls();
		setTypeFace(llCapture_Inventory);
		
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.VISIBLE);
		
		onViewClickListners();
		
		if(getIntent().getExtras() != null)
		{
			transferInoutDO = 	(TransferInoutDO) getIntent().getExtras().get("TransferID");
		}
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vectorEmployees = new UserInfoDA().getEmployeesNew(preference.getStringFromPreference(Preference.EMP_NO, ""));
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				vecOrdProduct = new TransferInOutDA().getTransferedProductNew(transferInoutDO.InventoryUID);
				
				tvEmpNoValue.setText(transferInoutDO.sourceVNO);
				tvSalemanValue.setText(new UserInfoDA().getNameByEmpNO(transferInoutDO.fromEmpNo));
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						inventoryItemAdapter.refresh(vecOrdProduct, llLayoutMiddle);
						hideLoader();
					}
				});
			}
		}).start();
		
		btnSave.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(tvEmpNoValue.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Please select a vehicle.", "OK", null, "");
				else
				{
					final ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem = inventoryItemAdapter.getModifiedData();
					boolean isValidate = getValidate(vecModifiedItem);
					
					if(vecModifiedItem == null || vecModifiedItem.size() <= 0)
						showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Please add atleast one item.", "OK", null, "");
					else if(!isValidate)
						showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Cases or units quantity should not be zero.", "OK", null, "");
					else if(vecModifiedItem != null && vecModifiedItem.size() > 0)
					{
						showLoader("Please wait...");
						new Thread(new Runnable() 
						{
							public void run() 
							{
								final String strFromEmpNo = transferInoutDO.fromEmpNo;
								final String strToEmpNo   = preference.getStringFromPreference(Preference.EMP_NO,"");
								final String destNo 	  = transferInoutDO.destVNO;
								final String sourceVNO 	  = transferInoutDO.sourceVNO;
								
								ArrayList<DeliveryAgentOrderDetailDco> vecItems = new ArrayList<DeliveryAgentOrderDetailDco>();
								for(DeliveryAgentOrderDetailDco object : vecModifiedItem)
								{
									if(object.preCases > 0 || object.preUnits >0)
										vecItems.add(object);
								}
								vecOrdProduct = vecItems;
								String strSalesmanCode = preference.getStringFromPreference(Preference.EMP_NO, "");
								final String orderId = new TransferInOutDA().insertTransferInOut(vecItems, strSalesmanCode, strFromEmpNo, strToEmpNo, AppConstants.TRNS_TYPE_IN, "N", sourceVNO, destNo, CalendarUtils.getCurrentDateTime(), transferInoutDO.InventoryUID+"", transferInoutDO.destOrderID);
//								new TransferInOutDA().insertTransferInOut(vecItems, vehicleNo, strEmpNo, AppConstants.TRNS_TYPE_IN, "N");
								
								runOnUiThread(new Runnable() 
								{
									@Override
									public void run() 
									{
										if(orderId != null && !orderId.equals(""))
										{
											tvEmpNoValue.setClickable(false);
											hideLoader();
											showCustomDialog(SalesmanTransferIn.this, "Message !", "Inventory transferred in successfully.", "OK", null, "UpdateInventory", false);
											uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
										}
										else
										{
											showCustomDialog(SalesmanTransferIn.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
										}
									}
								});
							}
						}).start();
					}
				}
			}
		});
		
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
//		showLoader(getResources().getString(R.string.loading));
//		new Thread(new Runnable() 
//		{
//			@Override
//			public void run() 
//			{
//				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
//				vector = new VehicleDA().getTruckListForStockExchange(preference.getStringFromPreference(Preference.EMP_NO, ""), preference.getStringFromPreference(Preference.VEHCLE_NO, ""));
//				runOnUiThread(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
//						hideLoader();
//					}
//				});
//			}
//		}).start();
		vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
		inventoryItemAdapter = new InventoryItemAdapter(vecOrdProduct);
		inventoryItemAdapter.refresh(vecOrdProduct, llLayoutMiddle);
		
//		btnFinish.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v)
//			{
//				Intent intent = new Intent();
//				intent.setAction(AppConstants.ACTION_GOTO_CRL);
//				sendBroadcast(intent); 
//			}
//		});
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				Intent intent = new Intent(SalesmanTransferIn.this, BluetoothFilePrinter.class);
				Intent intent = new Intent(SalesmanTransferIn.this, WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.TRANSFER_IN);
				intent.putExtra("salemanName", tvSalemanValue.getText().toString());
				intent.putExtra("vehicleNo", tvEmpNoValue.getText().toString());
				startActivityForResult(intent, 1000);
//				showToast("Print functionality is in progress.");
			}
		});
		
		btnAddItems.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	public void intialiseControls()
	{
		llLayoutMiddle		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llLayoutMiddle);
		btnAddItems			= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		btnSave				= (Button)llCapture_Inventory.findViewById(R.id.btnSave);
		btnFinish			= (Button)llCapture_Inventory.findViewById(R.id.btnFinish);
		btnPrint 			= (Button)llCapture_Inventory.findViewById(R.id.btnPrint);
		
		tvEmpNo				= (TextView)llCapture_Inventory.findViewById(R.id.tvEmpNo);
		tvEmpNoValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvEmpNoValue);
		
		tvSaleman			= (TextView)llCapture_Inventory.findViewById(R.id.tvSaleman);
		tvSalemanValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvSalemanValue);
		
		tvCode				= (TextView)llCapture_Inventory.findViewById(R.id.tvCode);
		tvCases				= (TextView)llCapture_Inventory.findViewById(R.id.tvCases);
		tvUnits				= (TextView)llCapture_Inventory.findViewById(R.id.tvUnits);
		
		tvLu				= (TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvNoOrder			= (TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
		
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		
		
		tvNoOrder.setText("Please add items.");
		/*tvNoOrder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddItems.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSave.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinish.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvEmpNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvEmpNoValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvSaleman.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSalemanValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvLu.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnAddItems.setVisibility(View.GONE);
		
		tvLu.setText("Transfer In");
		tvUnits.setText("Units");
//		tvEmpNoValue.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				if(tvSalemanValue != null && !tvSalemanValue.getText().toString().equals(""))
//				{
//					showLoader("Please wait...");
//					new Thread(new Runnable() {
//						@Override
//						public void run()
//						{
//							vector = new VehicleDA().getTruckListForStockExchange(((EmployeeDo)tvSalemanValue.getTag()).strEmpId, preference.getStringFromPreference(Preference.VEHCLE_NO, ""));
//							runOnUiThread(new Runnable() 
//							{
//								@Override
//								public void run()
//								{
//									hideLoader();
//									showVehicleListDialog(tvEmpNoValue, vector);
//								}
//							});
//						}
//					}).start();
//				}
//				else
//				{
//					showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Please select salesman.", getString(R.string.OK), null, "");
//				}
//			}
//		});
		
//		tvSalemanValue.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v)
//			{
//				showSalemanDailog(tvSalemanValue);
//			}
//		});
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	public void ShowDeleteButton(Vector<Item> vecItems)
	{
		for(int i = 0; vecItems!= null && i <vecItems.size() ; i++)
		{
			Button btnDelete = (Button)findViewById(i);
			if(btnDelete != null)
			{
				btnDelete.setVisibility(View.GONE);
				btnDelete.setTag("false");
			}
		}
	}
	
	public void onViewClickListners()
	{
		btnAddItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				adapter = null;
				showSelectItemPopup();
			}
		});
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
				if(type.equalsIgnoreCase("cases"))
				{
					if(objItem != null)
					{
						objItem.preCases = StringUtils.getFloat(s.toString());
						objItem.totalCases = objItem.preCases+(float)objItem.preUnits/objItem.unitPerCase;
					}
				}
				else
				{
					if(objItem != null)
					{
						objItem.preUnits = StringUtils.getInt(s.toString());
						objItem.totalCases = objItem.preCases+(float)objItem.preUnits/objItem.unitPerCase;
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
			
			for(DeliveryAgentOrderDetailDco productDO : vecOrdProduct)
			{
				LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_verify_cell,null);
				
				TextView tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
				TextView tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
				EditText evCases			= (EditText)convertView.findViewById(R.id.etCases);
				EditText evUnits			= (EditText)convertView.findViewById(R.id.etInvoice1);
				ImageView ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
				
				productDO.etCases =  evCases;
				productDO.etUnits =  evUnits;
				
				if(productDO.itemCode.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).itemCode))
					evUnits.setImeOptions(EditorInfo.IME_ACTION_DONE);
				ivAcceptCheckItems.setVisibility(View.GONE);
				
				tvProductKey.setText(productDO.itemCode);
//				tvProductKey.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvVendorName.setText(productDO.itemDescription);
//				tvVendorName.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				
				evCases.setTag(productDO);
				evUnits.setTag(productDO);
				
				if(productDO.preCases > 0)
					evCases.setText(""+productDO.preCases);
				else
					evCases.setText("");
				
				if(productDO.preUnits > 0)
					evUnits.setText(""+productDO.preUnits);
				else
					evUnits.setText("");
				
				evCases.setEnabled(false);
				evCases.setFocusable(false);
				evCases.setFocusableInTouchMode(false);
				evCases.setCursorVisible(false);
				evCases.setSingleLine(false);
				
				if(productDO.itemType != null && productDO.itemType.equalsIgnoreCase("2"))
				{
					evUnits.setEnabled(false);
					evUnits.setFocusable(false);
					evUnits.setFocusableInTouchMode(false);
					evUnits.setText("");
					evUnits.setHint("");
				}
				else
				{
					evUnits.setEnabled(false);
					evUnits.setFocusable(false);
					evUnits.setFocusableInTouchMode(false);
					evUnits.setCursorVisible(false);
					evUnits.setSingleLine(false);
				}
				
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
				tvNoOrder.setVisibility(View.GONE);
				llLayoutMiddle.setVisibility(View.VISIBLE);
				getChildView(llLayoutMiddle);
			}
			else
			{
				tvNoOrder.setVisibility(View.VISIBLE);
				llLayoutMiddle.setVisibility(View.GONE);
			}
		}
		private boolean isFieldEditable = true;
		//Method to refresh the List View
		private void refresh(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProducts,LinearLayout llLayoutMiddle, boolean isFieldEditable) 
		{
			this.isFieldEditable = isFieldEditable;
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			this.vecOrdProduct = vecOrdProducts;
			if(vecOrdProduct != null && vecOrdProducts.size() > 0)
			{
				tvNoOrder.setVisibility(View.GONE);
				llLayoutMiddle.setVisibility(View.VISIBLE);
				getChildView(llLayoutMiddle);
			}
			else
			{
				tvNoOrder.setVisibility(View.VISIBLE);
				llLayoutMiddle.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			super.onBackPressed();
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("UpdateInventory"))
		{
			btnSave.setVisibility(View.GONE);
			btnAddItems.setVisibility(View.GONE);
			btnPrint.setVisibility(View.VISIBLE);
			btnFinish.setVisibility(View.VISIBLE);
			
//			inventoryItemAdapter.refresh(vecOrdProduct, llLayoutMiddle, false);
		}
	}
	
	private void showVehicleListDialog(final TextView tvTextView, Vector<VehicleDO> vector)
	{
		if(vector != null && vector.size() > 0)
		{
			CustomBuilder builder = new CustomBuilder(SalesmanTransferIn.this, "Select Vehicle", true);
			builder.setSingleChoiceItems(vector, tvTextView.getTag(), new CustomBuilder.OnClickListener() 
			{
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject) 
				{
					VehicleDO vehicleDO = (VehicleDO) selectedObject;
					tvTextView.setText(vehicleDO.VEHICLE_NO);
					tvTextView.setTag(vehicleDO);
					builder.dismiss();
			    }
			}); 
			builder.show();
		}
		else
			showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Vehicle list in not available.", "OK", null, "");
	}
	
	private void showSelectItemPopup()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesmanTransferIn.this);
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
		
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
//		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		
//		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		vecCategory = new Vector<String>();
		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
		llList.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				cbList.performClick();
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
		final Button btnSearch = new Button(SalesmanTransferIn.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesmanTransferIn.this, "Select Category", true);
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
					showCustomDialog(SalesmanTransferIn.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
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
										lvPopupList.setAdapter(adapter = new AddItemAdapter(vecSearchedItemd ,SalesmanTransferIn.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddItemAdapter( new Vector<DeliveryAgentOrderDetailDco>(),SalesmanTransferIn.this));
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
						String strText = ((DeliveryAgentOrderDetailDco)obj).itemCode;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()))
						{
							vecTemp.add(vecSearchedItemd.get(index));
						}
					}
					if(vecTemp!=null && adapter!= null)
						adapter.refresh(vecTemp);
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
				
				if(veciItems != null && veciItems.size()>0)
				{
					vecOrdProduct.addAll(veciItems);
					
					objAddNewSKUDialog.dismiss();
					inventoryItemAdapter.refresh(vecOrdProduct, llLayoutMiddle);
				}
				else
				{
					showCustomDialog(SalesmanTransferIn.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	private boolean getValidate(ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem)
	{
		boolean isAvail = false;
		if(vecModifiedItem != null && vecModifiedItem.size() > 0)
		{
			for(DeliveryAgentOrderDetailDco d : vecModifiedItem)
			{
				if(d.preCases > 0 || d.preUnits >0)
				{
					isAvail = true;
					break;
				}
			}
		}
		return isAvail;
	}
	
	private void showSalemanDailog(final TextView tvTextView)
	{
		if(vectorEmployees != null && vectorEmployees.size() > 0)
		{
			CustomBuilder builder = new CustomBuilder(SalesmanTransferIn.this, "Select Salesman", true);
			builder.setSingleChoiceItems(vectorEmployees, tvTextView.getTag(), new CustomBuilder.OnClickListener() 
			{
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject) 
				{
					EmployeeDo employeeDo = (EmployeeDo) selectedObject;
					tvTextView.setText(employeeDo.strEmpName);
					tvTextView.setTag(employeeDo);
					builder.dismiss();
			    }
			}); 
			builder.show();
		}
		else
			showCustomDialog(SalesmanTransferIn.this, getString(R.string.warning), "Vehicle list in not available.", "OK", null, "");
	}
}