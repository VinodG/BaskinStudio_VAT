package com.winit.baskinrobbin.salesman;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class SalesManTakeReplacementOrder extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave,llPricing, llOrderVal, 
						 llLayoutMiddle;
	private TextView tvLu, tvCI, tvOrderValue, tvDisHeader, etDiscValue , tvNoOrder, evTotalValue;
	private Button  btnAddIetem, btnAddIetems,  btnCancel1, btnConfirmOrder;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd, vecToReplace;
	private CaptureInventaryAdapter adapterForCapture;
	private float orderTPrice = 0.0f, totalDiscount = 0.0f, totalIPrice = 0.0f, totalInvoicedPrice = 0.0f;
	private String strKeyNew = "", from;
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, Float> hmFactors;
	private int cyear, cmonth, cday;
	private TextView tempView;
	private View vLine;
	private HashMap<String, Float> hmConversion;
	private HashMap<String, Vector<String>> hmUOMFactors;
	private boolean canReplaceSame=false;
//	private int precision=0;
	@Override
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.recommendedorder, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails 			= (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			totalInvoicedPrice		= getIntent().getExtras().getFloat("invoiceamt");
			from					= getIntent().getExtras().getString("from");
		}
		
		intialiseControls();
		loadData();
		setTypeFace(llCapture_Inventory);
		
		if(from.equalsIgnoreCase("replacement"))
			tvCI.setText("Capture Replacement Order");
		else 
			tvCI.setText("Capture Return Order");
		
		llBottomButtons.setVisibility(View.GONE);
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddIetems.setVisibility(View.VISIBLE);
		etDiscValue.setEnabled(true);
		etDiscValue.setFocusable(true);
		
		llPricing.setVisibility(View.GONE);
		
		btnCancel1.setText("Cancel");
		btnCancel1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnConfirmOrder.setText(" Confirm Order ");
		btnConfirmOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						final boolean isAvail    =  getAddedItemAvailability(AppConstants.hmCapturedInventory);
						final int passcodeEnable =  new SettingsDA().getSettingsByName(AppConstants.PASSCODE_REPLACEMENT);
						
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() <= 0 )
									showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
								
								else if(!isAvail)
									showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
								
								else
								{
									if(passcodeEnable == AppStatus.ENABLE || passcodeEnable == AppStatus.NOT_AVAIL)
										showPassCodeDialog(null, from, false);
									else
										performPasscodeAction(null, from, false);
								}
							}
						});
					}
				}).start();
			}
		});
		
		onViewClickListners();

		if(AppConstants.hmCapturedInventory != null)
			AppConstants.hmCapturedInventory.clear();
		else 
			AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
		
	
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
		{
			tvNoOrder.setVisibility(View.GONE);
			calTotPrices(true);
		}
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
		if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
		{
			evTotalValue.setText(""+curencyCode+" "+"0.00");
			etDiscValue.setText(""+curencyCode+" "+"0.00");
		}
		else
		{
			etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
		}
		
		IntentFilter filterForJourney = new IntentFilter();
		filterForJourney.addAction(AppConstants.ACTION_GOTO_TELEORDERS);
		registerReceiver(GotoTeleOrders, filterForJourney);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				AppConstants.hmCateogories 	= 	new CategoriesDA().getCategoryListForReturn();
				hmConversion 				= 	new OrderDetailsDA().getUOMFactor();
				hmInventory  				= 	new OrderDetailsDA().getAvailInventoryQtys_Replace();
//				hmInventory  				= 	new OrderDetailsDA().getAvailInventoryQtys_Replace(precision);
				hmFactors					= 	new CommonDA().getAllFactor();
				hmUOMFactors 				= 	new OrderDetailsDA().getPricingUOM(mallsDetails.priceList, AppStatus.REPLACEMENT_ORDER_TYPE);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
					}
				});
			}
		}).start();
		
		setTypeFace(llCapture_Inventory);
	}
	
	BroadcastReceiver GotoTeleOrders = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			finish();
		}
	};
	
	public void intialiseControls()
	{
		llLayoutMiddle		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llLayoutMiddle);
		llTotalValue		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llTotalValue);
		btnConfirmOrder 	= (Button)llCapture_Inventory.findViewById(R.id.btnFinalize);
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddIetems		= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		evTotalValue		= (TextView)llCapture_Inventory.findViewById(R.id.evTotalValue);
		btnCancel1 			= (Button)llCapture_Inventory.findViewById(R.id.btnSave);
		tvLu				= (TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvCI				= (TextView)llCapture_Inventory.findViewById(R.id.tvCI);
		tvNoOrder			= (TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
		
		etDiscValue			= (TextView)llCapture_Inventory.findViewById(R.id.etDiscValue);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		llOrderVal			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llOrderVal);
		
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		
		vLine				= (View)llCapture_Inventory.findViewById(R.id.vLine);
		
		vLine.setVisibility(View.VISIBLE);
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		
		btnAddIetem.setTextColor(Color.WHITE);
		
		llTotalValue.setVisibility(View.VISIBLE);
		btnAddIetems.setVisibility(View.VISIBLE);
		etDiscValue.setVisibility(View.VISIBLE);
		tvDisHeader.setVisibility(View.VISIBLE);
		llOrderVal.setVisibility(View.VISIBLE);
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName);
		
		btnAddIetems.setVisibility(View.GONE);
	}
	
	private void calTotPrices(boolean isLoaderShown)
	{
		if(isLoaderShown)
			showLoader(getResources().getString(R.string.loading));
		
		orderTPrice 		= 	0.0f;
		totalIPrice 		= 	0.0f;
		totalDiscount 	= 	0.0f;
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				int count = 1;
				for(String key : set)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						objProductDO.LineNo 			=  ""+count++;
						
						objProductDO.discountAmount     =  0.0;
						
						objProductDO.TaxGroupCode		=  "0";
						objProductDO.TaxPercentage		=	0;
						objProductDO.inventoryQty		=	0;	
						objProductDO.itemPrice  		= 	0;
						objProductDO.totalPrice 		= 	0.0;
						objProductDO.unitSellingPrice 	= 	0;
						objProductDO.invoiceAmount 		= 	0.0;
						objProductDO.discountDesc 		= 	"0";
						
						orderTPrice 					+= StringUtils.roundOff(objProductDO.totalPrice,noOfRoundingOffdigits);
						totalDiscount 					+=StringUtils.roundOff(objProductDO.discountAmount,noOfRoundingOffdigits);
						totalIPrice 					+= StringUtils.roundOff(objProductDO.invoiceAmount,noOfRoundingOffdigits);
						
						objProductDO.DiscountAmt = 0;
						
						objProductDO.Discount     = 0;
						objProductDO.discountType = 0;
					}
				}
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						
						if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
							tvNoOrder.setVisibility(View.GONE);
						else
							tvNoOrder.setVisibility(View.VISIBLE);
						
						adapterForCapture = new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
						etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
						evTotalValue.setText(""+curencyCode+" "+diffAmt.format(totalIPrice));
						tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));				
					}
				});
			}
		}).start();
	}
	
	public void onViewClickListners()
	{
		btnAddIetems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showLoader("Loading...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						vecCategory = new CategoriesDA().getAvailableCategory_WithPricing(mallsDetails.priceList);
						
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								disableView(btnAddIetems);
								hideLoader();
								if(vecCategory != null && vecCategory.size() > 0)
									showAddNewSkuPopUp();
								else
									showCustomDialog(SalesManTakeReplacementOrder.this, "Warning!", "Customer Pricing Key and Items Pricing Key are not matching, Please contact your supervisor.", "OK", null, "");
							}
						});
					}
				}).start();
			}
		});
	}
	
	public void showAddNewSkuPopUp()
	{
		View view = inflater.inflate(R.layout.add_replacement_items, null);
		final CustomDialog customDialog = new CustomDialog(SalesManTakeReplacementOrder.this, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		
		final TextView tvCategory 	= (TextView) view.findViewById(R.id.tvCategory);
		final TextView tvItemcode 	= (TextView) view.findViewById(R.id.tvItemcode);
		final TextView tvUOM 		= (TextView) view.findViewById(R.id.tvUOM);
		final TextView tvSecItemcode= (TextView) view.findViewById(R.id.tvSecItemcode);
		final TextView tvExpiryDate	= (TextView) view.findViewById(R.id.tvExpiryDate);
		final TextView tvOHQQuantity= (TextView) view.findViewById(R.id.tvOHQQuantity);
		final TextView tvOHQQuantityReceive= (TextView) view.findViewById(R.id.tvOHQQuantityReceive);
		
		final EditText etQuantity 	= (EditText) view.findViewById(R.id.etQuantity);
		final EditText etLotNo 		= (EditText) view.findViewById(R.id.etLotNo);
		final Button btnAdd 		= (Button) view.findViewById(R.id.btnAdd);
		final Button btnCancel 		= (Button) view.findViewById(R.id.btnCancel);
		
		//etQuantity.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(precision)});
		setActionInViews(etQuantity, false);
		setActionInViews(tvUOM, false);
		tvCategory.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeReplacementOrder.this, "Select Category", true);
				builder.setSingleChoiceItems(vecCategory, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(final CustomBuilder builder, final Object selectedObject) 
					{
						showLoader("Loading...");
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								final String str = (String) selectedObject;
								vecSearchedItemd = new ProductsDA().getProductsDetailsByCategoryId(null, str, "", mallsDetails.priceList, hmUOMFactors, false, null);
								vecToReplace     = new ProductsDA().getProductsDetailsByCategoryId(null, str, "", mallsDetails.priceList, hmUOMFactors, true, hmInventory);
								
								runOnUiThread(new Runnable() 
								{
									@Override
									public void run() 
									{
							    		builder.dismiss();
						    			builder.dismiss();
						    			((TextView)v).setText(str);
										((TextView)v).setTag(str);
										tvItemcode.setText("");
										tvUOM.setText("");
										tvSecItemcode.setText("");
										tvItemcode.setTag(null);
										tvSecItemcode.setTag(null);
										etQuantity.setText("");
										tvOHQQuantityReceive.setVisibility(View.GONE);
										tvOHQQuantity.setVisibility(View.GONE);
										setActionInViews(etQuantity, false);
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
		
		etQuantity.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				ProductDO objItem = (ProductDO) etQuantity.getTag();
				
				if(objItem == null)
					objItem = new ProductDO();
				
				objItem.SKU 		= 	tvSecItemcode.getText().toString();
				objItem.UOM 		= 	tvUOM.getText().toString();
				objItem.preUnits 	= 	s.toString();
				objItem.quantityBU 	= 	getQuantityBU(objItem, hmConversion);
				
				if(StringUtils.getFloat(objItem.preUnits) > 0 && !isInventoryAvail_Replace(objItem, hmInventory))
				{
					objItem.preUnits = "0";
					objItem.totalCases = StringUtils.getFloat(objItem.preCases);
					showToast("Issued quantity should not be greater than inventory quantity.");
					etQuantity.setText("");    
				}
				
				objItem.quantityBU = getQuantityBU(objItem, hmConversion);
				etQuantity.setTag(objItem);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		tvItemcode.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeReplacementOrder.this, "Select Item", true, true);
				builder.setSingleChoiceItems(vecSearchedItemd, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						ProductDO productDO = (ProductDO) selectedObject;
						String ISSUE_SKU 	= productDO.SKU;
						String RECEIVE_SKU 	= tvSecItemcode.getText().toString();
						
						if(canReplaceSame /*&& TextUtils.isEmpty(RECEIVE_SKU) || !RECEIVE_SKU.equalsIgnoreCase(ISSUE_SKU)*/)
						{
				    		builder.dismiss();
			    			builder.dismiss();
			    			((TextView)v).setText(productDO.SKU);
							((TextView)v).setTag(productDO);
							
							setInventoryValue(ISSUE_SKU,productDO.UOM, tvOHQQuantityReceive);
						}
						else
							showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),
								"Issue item can't be same as receive item.", "OK", null, "");
				    }
				}); 
				builder.show();
			}
		});
		
		tvSecItemcode.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeReplacementOrder.this, "Select Item", true, true);
				builder.setSingleChoiceItems(vecToReplace, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						ProductDO productDO = (ProductDO) selectedObject;
						String ISSUE_SKU 	= tvItemcode.getText().toString();
						String RECEIVE_SKU 	= productDO.SKU;
						
						if(canReplaceSame /*&& RECEIVE_SKU.equalsIgnoreCase(ISSUE_SKU)*/)
						{
				    		builder.dismiss();
			    			builder.dismiss();
			    			((TextView)v).setText(productDO.SKU);
							((TextView)v).setTag(productDO);
							setActionInViews(tvUOM, true);
							setActionInViews(etQuantity, false);
							tvUOM.setText("");
							
							setInventoryValue(RECEIVE_SKU, productDO.UOM, tvOHQQuantity);
						}
						else
							showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),
									"Issue item can't be same as receive item.", "OK", null, "");
				    }
				}); 
				builder.show();
			}
		});
		
		tvUOM.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				ProductDO productDO = (ProductDO) tvSecItemcode.getTag();
				CustomBuilder builder = new CustomBuilder(SalesManTakeReplacementOrder.this, "Select UOM", true);
				builder.setSingleChoiceItems(productDO.vecUOM, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						String UOM = (String) selectedObject;
			    		builder.dismiss();
		    			builder.dismiss();
		    			((TextView)v).setText(UOM);
						((TextView)v).setTag(UOM);
						setActionInViews(etQuantity, true);
						etQuantity.setText("");
						
						String ISSUE_SKU 	= 	tvItemcode.getText().toString();
						setInventoryValue(ISSUE_SKU, UOM , tvOHQQuantityReceive);
						
						String RECEIVE_SKU 	= 	tvSecItemcode.getText().toString();
						setInventoryValue(RECEIVE_SKU, UOM, tvOHQQuantity);
				    }
				}); 
				builder.show();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				customDialog.dismiss();
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(tvCategory.getTag() == null)
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please select a category.", "OK", null, "");
				else if(tvItemcode.getTag() == null)
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please select receipt itemcode.", "OK", null, "");
				else if(tvSecItemcode.getTag() == null)
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please select issue itemcode.",  "OK", null, "");
				else if(TextUtils.isEmpty(tvUOM.getText().toString()) || tvUOM.getTag() == null)
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please select UOM.", "OK", null, "");
				else if(TextUtils.isEmpty(etQuantity.getText().toString()) || etQuantity.getTag() == null)
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please enter quantity.", "OK", null, "");
				else if(TextUtils.isEmpty(tvExpiryDate.getText().toString()))
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please select expiry date of receipt item.", "OK", null, "");
				else if(TextUtils.isEmpty(etLotNo.getText().toString()))
					showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning),"Please enter lot number of receipt item.", "OK", null, "");
				else
				{
					if(AppConstants.hmCapturedInventory == null)
						AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
					
					ProductDO productDO 	=  (ProductDO) tvItemcode.getTag();
					ProductDO productTempDO =  (ProductDO) tvSecItemcode.getTag();
					ProductDO productqtyDO  =  (ProductDO) etQuantity.getTag();
					productDO.RelatedLineId =  productTempDO.SKU;
					productDO.Description1  =  productTempDO.Description1;
					
					productDO.preUnits 		=  productqtyDO.preUnits;
					productDO.BatchCode 	=  productqtyDO.BatchCode;
					productDO.strExpiryDate =  tvExpiryDate.getText().toString();
					productDO.LotNumber	 	=  etLotNo.getText().toString();
					productDO.UOM           =  tvUOM.getText().toString();
					
					productDO.quantityBU    =  productqtyDO.quantityBU;
					
					maintainQtyinHMap(productqtyDO, hmInventory, false);
					
					if(AppConstants.hmCapturedInventory.containsKey(productDO.CategoryId))
					{
						Vector<ProductDO> vec = AppConstants.hmCapturedInventory.get(productDO.CategoryId);
						if(vec == null)
							vec = new Vector<ProductDO>();
						
						vec.add(productDO);
						AppConstants.hmCapturedInventory.put(productDO.CategoryId, vec);
					}
					else
					{
						Vector<ProductDO> vec = new Vector<ProductDO>();
						vec.add(productDO);
						AppConstants.hmCapturedInventory.put(productDO.CategoryId, vec);
					}
					
					calTotPrices(true);
					customDialog.dismiss();
				}
			}
		});
		
		tvExpiryDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tempView = tvExpiryDate;
				showDialog(0);
			}
		});
		
		setTypeFace((LinearLayout)view);
		
		try{
		if (!customDialog.isShowing())
			customDialog.show();
		}catch(Exception e){}
	}
	
	
	public class CaptureInventaryAdapter
	{
		private HashMap<String, Vector<ProductDO>> hmItems;
		private Vector<String> vecCategoryIds;
		
		public CaptureInventaryAdapter(HashMap<String, Vector<ProductDO>> hmItems)
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				getGroupView(llLayoutMiddle);
			}
		}

		public View getChildView(int groupPosition) 
		{
			final LinearLayout llChildViews = new LinearLayout(getApplicationContext());
			llChildViews.setOrientation(1);
			for(int childPosition =0;childPosition<hmItems.get(vecCategoryIds.get(groupPosition)).size();childPosition++)
			{
				final ProductDO objProduct = hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				final LinearLayout convertView= (LinearLayout)getLayoutInflater().inflate(R.layout.replace_inventory_cell,null);
				
				TextView tvSKU			= (TextView)convertView.findViewById(R.id.tvSKU);
				TextView tvDescription	= (TextView)convertView.findViewById(R.id.tvDescription);
				TextView tvSecSKU		= (TextView)convertView.findViewById(R.id.tvSecSKU);
				TextView tvSecDes		= (TextView)convertView.findViewById(R.id.tvSecDes);
				final ImageView ivDelete= (ImageView)convertView.findViewById(R.id.ivDelete);
				final EditText evCases	= (EditText)convertView.findViewById(R.id.evCases);
				EditText evUnits		= (EditText)convertView.findViewById(R.id.evUnits);
				
				//evUnits.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(precision)});
				objProduct.etCases		=  evCases;
				objProduct.etUnits		=  evUnits;
				
				tvSKU.setText(objProduct.SKU);
				tvDescription.setText(objProduct.Description);
				
				tvSecSKU.setText(objProduct.RelatedLineId);
				tvSecDes.setText(objProduct.Description1);
				
				evCases.setTag(objProduct);
				evUnits.setTag(objProduct);

				evCases.setText(objProduct.UOM);
				evCases.setTag(objProduct.UOM);
				
				if(StringUtils.getInt(objProduct.preUnits) <= 0 )
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preUnits);
				
				setActionInViews(evCases, false);
				setActionInViews(evUnits, false);
				
				ivDelete.setTag(objProduct);
				convertView.setTag(objProduct);
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(60 * px)));
				
				if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
					evCases.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
							{
								CustomBuilder customDialog = new CustomBuilder(SalesManTakeReplacementOrder.this, "Select UOM", true);
								customDialog.setSingleChoiceItems(objProduct.vecUOM, evCases.getTag(), new CustomBuilder.OnClickListener() 
								{
									@Override
									public void onClick(CustomBuilder builder, Object selectedObject) 
									{
										String lastTag = ""+evCases.getTag().toString();
										evCases.setText((String)selectedObject);
										evCases.setTag((String)selectedObject);
										objProduct.UOM =(String)selectedObject;
										
										if(!lastTag.equalsIgnoreCase(objProduct.UOM))
										{
											objProduct.preUnits = "0";
											calTotPrices(false);
										}
										builder.dismiss();
									}
								});
								customDialog.show();
							}
						}
					});
				
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if(ivDelete.getVisibility() == View.VISIBLE && objProduct.isDeleteOpen)
							loadAnimation(ivDelete, false);
					}
				});
				
				convertView.setOnLongClickListener(new OnLongClickListener()
				{
					@Override
					public boolean onLongClick(View v)
					{
						objProduct.isDeleteOpen = false;
						if(ivDelete.getVisibility() == View.GONE)
							loadAnimation(ivDelete, true);
						else
							loadAnimation(ivDelete, false);
						
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run() 
							{
								objProduct.isDeleteOpen = true;								
							}
						}, 1000);
						
						return false;
					}
				});
				
				ivDelete.setOnClickListener(new OnClickListener()
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
								ProductDO productDO = (ProductDO) ivDelete.getTag();
								
								if(AppConstants.hmCapturedInventory != null)
								{
									String key  = productDO.CategoryId;
									
									Vector<ProductDO> vec = AppConstants.hmCapturedInventory.get(key);
									
									if(vec != null && vec.contains(productDO))
									{
										maintainQtyinHMap(productDO, hmInventory, true);
										
										vec.remove(productDO);
										
										if(vec == null || vec.size() <= 0)
											AppConstants.hmCapturedInventory.remove(key);
										else
											AppConstants.hmCapturedInventory.put(key, vec);
									}
									calTotPrices(true);
								}
									
								hideLoader();
							}
						}, 300);
					}
				});
				
				setTypeFace(convertView);
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(80 * px)));
				llChildViews.addView(convertView);
			}
			return llChildViews;
		}


		public void getGroupView(LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle!=null)
				llLayoutMiddle.removeAllViews();
			
			for(int groupPosition = 0; groupPosition<vecCategoryIds.size(); groupPosition++)
				llLayoutMiddle.addView(getChildView(groupPosition));
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(from != null && (from.equalsIgnoreCase("checkINOption") || from.equalsIgnoreCase("replacement")))
			finish();
		else
			showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning), "Are you sure you want to cancel the return request?", getString(R.string.Yes), getString(R.string.No), "returnprocess");
	}
	
	private boolean getAddedItemAvailability(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isAvail = false;
		
		Set<String> set = AppConstants.hmCapturedInventory.keySet();
		for(String strKey : set)
		{
			Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(strKey);
			if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
			{
				for(ProductDO objProductDO : vecOrderedProduct)
				{
					if(StringUtils.getFloat(objProductDO.preUnits) > 0)
					{
						isAvail = true;
						break;
					}
				}
			}
		}
		
		return isAvail;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	  {
		  //getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 0);
		  
		  cyear 	= 	c.get(Calendar.YEAR);
		  cmonth 	= 	c.get(Calendar.MONTH);
		  cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	  }
		/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	  {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  if(tempView != null)
			  {
				  String strMonth, strDate;
				  
				  if(monthOfYear <9)
					  strMonth = "0"+(monthOfYear+1);
				  else
					  strMonth = ""+(monthOfYear+1);
		    		
				  if(dayOfMonth <10)
					  strDate = "0"+(dayOfMonth);
				  else
					  strDate = ""+(dayOfMonth);
				  
				  if(CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
					  showCustomDialog(SalesManTakeReplacementOrder.this, getString(R.string.warning), "Expiry date can not be past date.", "Ok", null, "");
				  else
					  ((TextView)tempView).setText(year+"-"+strMonth+"-"+strDate);
			  }
		  }
	  };
	  
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String fromText, boolean isCheckOut)
	{
		Intent intent = new Intent(SalesManTakeReplacementOrder.this, SalesmanReplacementPreview.class);
		intent.putExtra("isReturnRequest", true);
		intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
		intent.putExtra("Discount", etDiscValue.getText().toString().trim());
		intent.putExtra("from", from);	
		intent.putExtra("mallsDetails",mallsDetails);
		startActivityForResult(intent, 1000);
	}
	
	private void setInventoryValue(String itemCode, String UOM, TextView tvOHQQuantity)
	{
		String key = itemCode + UOM;
		if(hmFactors != null && hmFactors.containsKey(key))
		{
			tvOHQQuantity.setVisibility(View.VISIBLE);
			float factor =  hmFactors.get(key);
			if(hmInventory != null && hmInventory.containsKey(itemCode))
			{
				HHInventryQTDO hhInventryQTDO = hmInventory.get(itemCode);
				float qty = hhInventryQTDO.quantityBU / factor;
				tvOHQQuantity.setText("OHQ : "+ diffStock.format(qty) +" "+UOM);
			}
			else
				tvOHQQuantity.setText("OHQ : "+ 0 +" "+UOM);
		}
		else
			tvOHQQuantity.setText("OHQ : "+ 0 +" "+UOM);
	}
	private void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				canReplaceSame=new SettingsDA().getSettingsValue(AppConstants.SAME_REPLACE);	
				}
		}).start();
	}
}