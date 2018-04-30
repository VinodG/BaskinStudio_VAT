package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.AddRecomendedItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.Item;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import static com.winit.baskinrobbin.salesman.R.id.btnAdd;
import static com.winit.baskinrobbin.salesman.R.id.tvTaxAmt;

public class SalesManTakeReturnOrder extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave,llPricing, llOrderVal, llLayoutMiddle;
	private TextView tvLu, tvCI, tvHeaderText, tvOrderValue, tvDisHeader, etDiscValue,etTaxVal, tvNoOrder, evTotalValue,tvOverduelimitVal,tvAvailablelimitVal,tvCreditLimitVal;
	private Button  btnAddIetem, btnAddIetems,  btnCancel1, btnConfirmOrder;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd;
	private CaptureInventaryAdapter adapterForCapture;
	private float orderTPrice = 0.0f, totalDiscount = 0.0f, totalIPrice = 0.0f, totalInvoicedPrice = 0.0f ,totTaxAmt=0.0f;
	private AddRecomendedItemAdapter adapter;
	private String orderId, strErrorMsg, strKeyNew = "",  from, orderedItems = "";
	private JourneyPlanDO mallsDetails ;
	private Thread threadForCheckDiscount ;
	private boolean isAdvance = false, isMenu = false, isTask = false;
	private int cyear, cmonth, cday, mPosition = 0;
	private TextView tempView;
	private ProductDO productDOImage;
	private HashMap<String, Float> hmConversion; 
	private HashMap<String, Vector<String>> hmUOMFactors;
	private HashMap<String, DiscountDO> hmPromoDisc;
	private CustomerCreditLimitDo creditLimit;
	//private int precision=0;

	private int daysLIMIT = 1;
	private int returnInvoiceLIMIT = 1;

	@Override
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.recomendedorder_new, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails 			= (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isAdvance				= (boolean) getIntent().getExtras().getBoolean("isAdvance");
			orderId					= getIntent().getExtras().getString("invoicenum");
			totalInvoicedPrice		= getIntent().getExtras().getFloat("invoiceamt");
			from					= getIntent().getExtras().getString("from");
			isMenu					= getIntent().getExtras().getBoolean("isMenu");
			
			isTask = false;
			if((orderId == null || orderId.length() <= 0) && (from == null || from.length() <= 0))
			{
				from = "replacement";
				isTask = true;
			}
			else if(from == null)
				from = "";
				 
			if(isMenu)
			{
				btnCheckOut.setVisibility(View.GONE);
				ivLogOut.setVisibility(View.GONE);
			}
		}
		
		intialiseControls();
		
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
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hmConversion =  new OrderDetailsDA().getUOMFactor();
				hmUOMFactors = 	new OrderDetailsDA().getPricingUOM(mallsDetails.priceList, AppStatus.SALES_ORDER_TYPE);
				hmPromoDisc	 = new CaptureInventryDA().getPromoDisocunt(mallsDetails.site);
				if(from != null && from.equalsIgnoreCase("replacement"))
					AppConstants.hmCateogories = new CategoriesDA().getCategoryListForReturn();
				else
					AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
			}
		}).start();
		
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
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
						final boolean isAvail  = getAddedItemAvailability(AppConstants.hmCapturedInventory);
						final int passcodeEnable = new SettingsDA().getSettingsByName(AppConstants.PASSCODE_GRV);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() <= 0 )
								{
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
								}
								else if(!isAvail)
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
								else if(!isAmountValid())
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please decrease the quantity. Amount should not be greater then "+mallsDetails.currencyCode+" "+diffAmt.format(totalInvoicedPrice)+".", getString(R.string.OK), null, "");
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
			calTotPrices(true, true);
		}
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
		etTaxVal.setText(""+curencyCode+" "+diffAmt.format(totTaxAmt));
		tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));
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
		loadData();
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
		etTaxVal			= (TextView)llCapture_Inventory.findViewById(R.id.etTaxVal);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		tvHeaderText		= (TextView)llCapture_Inventory.findViewById(R.id.tvHeaderText);
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		llOrderVal			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llOrderVal);
//        tvTaxAmt	     	= (TextView)llCapture_Inventory.findViewById(R.id.tvTaxAmt);//====Added For VAT=======
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		
		tvOverduelimitVal= (TextView)llCapture_Inventory.findViewById(R.id.tvOverduelimitVal);
		tvAvailablelimitVal= (TextView)llCapture_Inventory.findViewById(R.id.tvAvailablelimitVal);
		tvCreditLimitVal= (TextView)llCapture_Inventory.findViewById(R.id.tvCreditLimitVal);
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		
		tvOrderValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvNoOrder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnConfirmOrder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddIetem.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddIetems.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddIetem.setTextColor(Color.WHITE);
		tvLu.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCI.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDisHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCancel1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		llTotalValue.setVisibility(View.VISIBLE);
		btnAddIetems.setVisibility(View.VISIBLE);
		etDiscValue.setVisibility(View.VISIBLE);
		tvDisHeader.setVisibility(View.VISIBLE);
		llOrderVal.setVisibility(View.VISIBLE);
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName);
		
		btnAddIetems.setVisibility(View.GONE);
	}
	
	private void calTotPrices(boolean isLoaderShown, final boolean isReload)
	{
		if(isLoaderShown)
			showLoader(getString(R.string.loading));
		
		orderTPrice 		= 	0.0f;
		totalIPrice 		= 	0.0f;
		totalDiscount 	= 	0.0f;
		totTaxAmt       =0.0f;
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
						objProductDO.LineNo 			= ""+count++;
						DiscountDO temp        = new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
						
						DiscountDO objDiscount = getApplicableDiscounts(objProductDO, mallsDetails, hmPromoDisc, temp);
						if(objDiscount == null)

							objDiscount = new DiscountDO();
						
						if(StringUtils.getFloat(objProductDO.preUnits) <= 0 && objProductDO.etUnits != null)
							objProductDO.preUnits = objProductDO.etUnits.getText().toString();
						
						objDiscount.perCaseValue = temp.perCaseValue;
						objDiscount.TaxGroupCode = temp.TaxGroupCode;
						objDiscount.TaxPercentage = temp.TaxPercentage;

						//========newly added for return=========================================
						float discountPer=0.0f;
						float discountPerTemp=0.0f;
						if(objProductDO.DiscountAmtReturnBackup>0){
							discountPer=StringUtils.getFloat(""+(objProductDO.DiscountAmtReturnBackup*100)/(StringUtils.getDouble(objProductDO.units)*StringUtils.getDouble(objProductDO.preCases)));
						}
						discountPerTemp=discountPer;
//						discountPerTemp=StringUtils.getInt(""+StringUtils.round(""+discountPer,0));
						objProductDO.discountPerCentReturn=discountPer;
						//===========================================================
							objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objProductDO.preCases) * (discountPerTemp/100.0);
//						if(objDiscount.discountType == AppStatus.DISCOUNT_PERCENTAGE)
//							objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100);
//						else
//							objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * objDiscount.discount;
						double totalPrice=((StringUtils.getDouble(objProductDO.preCases)*StringUtils.getDouble(objProductDO.preUnits))-StringUtils.round(objProductDO.discountAmount+"",noOfRoundingOffdigits));
						double unitTaxAmt2=StringUtils.round((totalPrice*(objProductDO.TaxPercentage/100.0))+"",noOfRoundingOffdigits);
						double totalPricewithoutDiscount=(StringUtils.getDouble(objProductDO.preCases)*StringUtils.getDouble(objProductDO.preUnits));
						objProductDO.TaxGroupCode		=	objDiscount.TaxGroupCode;
						objProductDO.TaxPercentage		=	objProductDO.TaxPercentage; // Need to take Line level TAX %
						objProductDO.inventoryQty		=	0;

						objProductDO.itemPrice  		= 	objProductDO.unitSellingPrice;//objDiscount.fPricePerCase + objDiscount.fDiscountAmt;
						objProductDO.totalPrice 		= 	totalPrice;

					/*	objProductDO.unitSellingPrice 	= 	StringUtils.getFloat(objDiscount.perCaseValue) -
								StringUtils.getFloat(objDiscount.perCaseValue)* (objDiscount.discount/100);*/
						objProductDO.invoiceAmount 		= 	(totalPrice)
								+  (unitTaxAmt2 );
						objProductDO.invoiceAmountwithoutTax 		= 	(totalPrice);
//						objProductDO.invoiceAmount 		= 	(objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preUnits))
//								+  (objProductDO.LineTaxAmount/StringUtils.getFloat(objProductDO.units) * StringUtils.getFloat(objProductDO.preUnits) );
//						objProductDO.invoiceAmountwithoutTax 		= 	(objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preUnits));
						objProductDO.discountDesc 		= 	objDiscount.description;
//						double unittaxAmt				=   objProductDO.LineTaxAmount/StringUtils.getFloat(objProductDO.units);
						
						formatPrices(objProductDO);
						
						objProductDO.quantityBU 		= 	getQuantityBU(objProductDO, hmConversion);
						
						orderTPrice 					+= 	StringUtils.roundOff(objProductDO.totalPrice,noOfRoundingOffdigits);
						totalDiscount 					+=	StringUtils.roundOff(0,noOfRoundingOffdigits);
						totalIPrice 					+= 	StringUtils.roundOff(objProductDO.invoiceAmount,noOfRoundingOffdigits);
						totTaxAmt 			     		+= 	StringUtils.roundOff( unitTaxAmt2,noOfRoundingOffdigits );

							objProductDO.DiscountAmt =  StringUtils.getFloat(objProductDO.preCases)*discountPerTemp/100.0;
//
//						if(objDiscount.discountType == AppStatus.DISCOUNT_PERCENTAGE)
//							objProductDO.DiscountAmt =  StringUtils.getFloat(objDiscount.perCaseValue)*objDiscount.discount/100;
//						else
//							objProductDO.DiscountAmt =  objDiscount.discount;

						objProductDO.Discount =discountPer;


						//==============Commented As discount will be always 0
						objProductDO.Discount = 0;
						objProductDO.DiscountAmt = 0;
						objProductDO.discountType = objDiscount.discountType;
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
						
						if(isReload)
							adapterForCapture = new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
						etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
						evTotalValue.setText(""+curencyCode+" "+diffAmt.format(totalIPrice));
						tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));				
						etTaxVal.setText(""+curencyCode+" "+diffAmt.format(totTaxAmt));
//						tvTaxAmt.setText(""+curencyCode+" "+diffAmt.format(totTaxAmt));
					}
				});
			}
		}).start();
	}
	public void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetails);
				//precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				final float overDue 					= 	new CustomerDA().getOverDueAmount(mallsDetails);

				daysLIMIT 	       = StringUtils.getInt(getSettingsValueByName(AppConstants.SETTINGS_LIMIT_RETURN_ORDER_DAYS));
				returnInvoiceLIMIT = StringUtils.getInt(getSettingsValueByName(AppConstants.SETTINGS_LIMIT_RETURN_ORDER_INVOICE));

				runOnUiThread(new Runnable() {
					public void run() {
						tvCreditLimitVal.setText(mallsDetails.currencyCode+" "+diffAmt.format(StringUtils.getFloat(creditLimit.creditLimit)));
						tvAvailablelimitVal.setText(mallsDetails.currencyCode +" "+diffAmt.format(StringUtils.getFloat(creditLimit.availbleLimit)));
						tvOverduelimitVal.setText(mallsDetails.currencyCode +" "+diffAmt.format(StringUtils.getFloat(""+overDue)));
					}
				});
			}
		}).start();
		
	}
	@Override
	protected void onResume() 
	{
		super.onResume();
		loadData();
		
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
			tvNoOrder.setVisibility(View.GONE);
		else
			tvNoOrder.setVisibility(View.VISIBLE);
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
		btnAddIetems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				disableView(btnAddIetems);
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManTakeReturnOrder.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		objAddNewSKUDialog.show();
		
		TextView tvItemCodeLabel			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItemCodeLabel);
		TextView tvItem_DescriptionLabel	=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItem_DescriptionLabel);
		TextView tvAdd_New_SKU_Item			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvAdd_New_SKU_Item);
		
		final EditText etCategory	 		=	(EditText)objAddNewSKUDialog.findViewById(R.id.etCategory);
		final EditText etSearch	 			=	(EditText)objAddNewSKUDialog.findViewById(R.id.etSearch);
		final ImageView cbLists				=	(ImageView)objAddNewSKUDialog.findViewById(R.id.cbList);
		final LinearLayout llResult 		=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llResult);
		final LinearLayout llBottomButtons 	=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llBottomButtons);
		final ListView lvPopupList		 	=	(ListView)objAddNewSKUDialog.findViewById(R.id.lvPopupList);
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
		final LinearLayout llList			=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llList);
		llList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
			}
		});
		
		cbLists.setVisibility(View.INVISIBLE);
		
		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		vecCategory = new Vector<String>();
		
		if(from != null && from.equalsIgnoreCase("replacement"))
			vecCategory = new CategoriesDA().getAvailableCategory_WithPricing(mallsDetails.priceList);
		else 
			vecCategory = new CategoriesDA().getAvailableCategory_WithPricing_Return(mallsDetails.priceList);
		
		if(vecCategory == null || vecCategory.size() == 0)
		{
			objAddNewSKUDialog.dismiss();
			showCustomDialog(SalesManTakeReturnOrder.this, "Warning!", "Customer Pricing Key and Items Pricing Key are not matching, Please contact your supervisor.", "OK", null, "");
		}
		
		final Button btnSearch = new Button(SalesManTakeReturnOrder.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeReturnOrder.this, "Select Category", true);
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
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					Vector<ProductDO> vecTemp = new Vector<ProductDO>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						ProductDO obj  = (ProductDO) vecSearchedItemd.get(index);
						String strText = ((ProductDO)obj).SKU;
						String strDesc = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDesc.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
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
				else if(adapter!= null){

					adapter.refresh(vecSearchedItemd);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etCategory.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<ProductDO>();
//					else
//						vecSearchedItemd.clear();
					
					orderedItems = "";
//					if(AppConstants.hmCapturedInventory!=null)
//					{
//						Set<String> set = AppConstants.hmCapturedInventory.keySet();
//						
//						for(String key : set)
//						{
//							Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
//							for(ProductDO objProductDO : vecOrderedProduct)
//								if(!objProductDO.isPromotional)
//									orderedItems = orderedItems + "'"+objProductDO.SKU+"',";
//						}
//						
//						if(orderedItems.contains(","))
//							orderedItems = orderedItems.substring(0, orderedItems.lastIndexOf(","));
//					}
					
					final ProductsDA objItemDetailBL = new ProductsDA(); 
					showLoader(getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId = "", catgName = etCategory.getText().toString();
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
							//===============Commented by Abinash on 11th DEC 2017
//							vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(null, catgId, orderedItems, mallsDetails.priceList, hmUOMFactors, false, null);

							//============Added To consider the Return Order basing on invoice level,
//							vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryIdNew(null, catgId, orderedItems, mallsDetails.priceList, hmUOMFactors, false, null);
//							if(!isAnyitemSelected(vecSearchedItemd))
								vecSearchedItemd  = new CaptureInventryDA().getReturnItems(catgId, "", mallsDetails, true,preference.getStringFromPreference(Preference.EMP_NO, ""),daysLIMIT,returnInvoiceLIMIT);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									llResult.setVisibility(View.VISIBLE);
									if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
									{
										tvNoItemFound.setVisibility(View.GONE);
										llBottomButtons.setVisibility(View.VISIBLE);

										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManTakeReturnOrder.this, true, from, new DatePickerListner() {
											
											@Override
											public void setDate(TextView tvView, ProductDO objiItem1) 
											{
												tempView = tvView;
												tempView.setTag(objiItem1);
												showDialog(0);
											}
										}));
									}
									else
									{
										if(adapter == null)
											lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManTakeReturnOrder.this, true, from, new DatePickerListner() {
												
												@Override
												public void setDate(TextView tvView, ProductDO objiItem1)
												{
													tempView = tvView;
													tempView.setTag(objiItem1);
													showDialog(0);
												}
											}));
										else{

											adapter.refresh(vecSearchedItemd);
										}

										tvNoItemFound.setVisibility(View.VISIBLE);
										llBottomButtons.setVisibility(View.GONE);
									}
								}
							});
						}
					}).start();
				}
			}

			private boolean isAnyitemSelected(Vector<ProductDO> vecSearchedItemd) {
				boolean isSelected=false;
				for(ProductDO productDO: vecSearchedItemd)
				{
					if(productDO.isSelected) {
						isSelected = true;
						return isSelected;
					}
					}
					return isSelected;
				}
		});

		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Vector<ProductDO> veciItems= new Vector<ProductDO>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();

				
				if(veciItems != null && veciItems.size() > 0)
				{
					if(validateReason(veciItems))
					{
						tvNoOrder.setVisibility(View.GONE);
						if(AppConstants.hmCapturedInventory == null)
							AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
						
						for(int i=0; veciItems != null && i < veciItems.size(); i++)
						{
							ProductDO objProduct = veciItems.get(i);
							Vector<ProductDO> vecProducts = AppConstants.hmCapturedInventory.get(objProduct.CategoryId);
							if(vecProducts == null)
							{
								vecProducts = new Vector<ProductDO>();
								vecProducts.add(objProduct);
								AppConstants.hmCapturedInventory.put(objProduct.CategoryId, vecProducts);
							}
							else
								vecProducts.add(objProduct);

						}
						//clear already added vector
						//already added vecor .addAll(vecProducts)
				
						objAddNewSKUDialog.dismiss();
						
						calTotPrices(true, true);
					}
					else
					{
						showCustomDialog(SalesManTakeReturnOrder.this, "Warning !", strErrorMsg, "OK", null, "");
					}
				}
				else
					showCustomDialog(SalesManTakeReturnOrder.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	
	public class CaptureInventaryAdapter
	{
		private HashMap<String, Vector<ProductDO>> hmItems;
		private Vector<String> vecCategoryIds;
		private View view;
		
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			int groupPosition = -1;
			int childPosition = -1;
			public TextChangeListener(String type, int groupPosition, int childPosition)
			{
				this.type = type;
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				try 
				{
					handleText(type, s);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
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
					view = v;
				else
					view = null;
			}
		}
		
		private void handleText(String type, CharSequence s)
		{
			ProductDO objItem = null;
			if(view != null)
				objItem = (ProductDO) view.getTag();
			
			if(objItem != null)
			{
				objItem.preUnits = s.toString();
				objItem.quantityBU = getQuantityBU(objItem, hmConversion);
			}
			
			if(threadForCheckDiscount != null && threadForCheckDiscount.isAlive())
				threadForCheckDiscount.interrupt();
			//=================Added To restrict the Entered Qty (should be less than the )

			if(StringUtils.getFloat(objItem.preUnits) > 0 && StringUtils.getFloat(objItem.preUnits) > objItem.QuantityInStock)
			{
				objItem.preUnits = "";
//				showToast("Entered quantity should not be greater than available quantity.");
				showCustomDialog(SalesManTakeReturnOrder.this, "Warning !", "Entered quantity should not be greater than Ordered quantity.", "OK", null, "");
				if(objItem.etUnits != null)
					objItem.etUnits.setText("");

				objItem.quantityBU = getQuantityBU(objItem, hmConversion);
			}


			
			threadForCheckDiscount = new Thread(new Runnable() 
			{
				public void run() 
				{
					float tPrice = 0.0f, tDeposit = 0.0f, tOrderVal = 0.0f, tDiscVal = 0.0f,tTaxVal=0.0f;
					
					if(hmItems == null || hmItems.size() == 0)
						return;

					Set<String> set = hmItems.keySet();
					
					vecCategoryIds.clear();
					
					outer :
					{
						tPrice 		= 0.0f;
						tDeposit 	= 0.0f;
						tOrderVal 	= 0.0f;
						tTaxVal 	= 0.0f;
						for(String key : set)
						{
							vecCategoryIds.add(key);
							
							Vector<ProductDO> vecItems = hmItems.get(key);
							
							for(ProductDO objProductDO : vecItems)
							{
								if(Thread.interrupted())
									break outer;
								objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objProductDO.preCases) * (objProductDO.discountPerCentReturn/100.0);
								double totalPrice=((StringUtils.getDouble(objProductDO.preCases)*StringUtils.getDouble(objProductDO.preUnits))-StringUtils.round(objProductDO.discountAmount+"",noOfRoundingOffdigits));
								double unitTaxAmt2=StringUtils.round((totalPrice*(objProductDO.TaxPercentage/100.0))+"",noOfRoundingOffdigits);
//								double unitTaxAmt2= objProductDO.LineTaxAmount/(StringUtils.getFloat(objProductDO.units));
								double totalPricewithoutDiscount=(StringUtils.getDouble(objProductDO.preCases)*StringUtils.getDouble(objProductDO.preUnits));
								objProductDO.totalPrice 	= totalPrice;
								objProductDO.invoiceAmount 	= totalPrice
										+ (( unitTaxAmt2 ) );
//								objProductDO.invoiceAmount 	= totalPrice
//										+ ( (objProductDO.LineTaxAmount / StringUtils.getFloat(objProductDO.units) )*StringUtils.getFloat(objProductDO.preUnits) );
								objProductDO.invoiceAmountwithoutTax 	= (totalPrice);
//								objProductDO.invoiceAmountwithoutTax 	= (totalPrice*StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(objProductDO.preUnits)));

								formatPrices(objProductDO);

//								float unitTaxAmt= objProductDO.LineTaxAmount/StringUtils.getFloat(objProductDO.units);
//								double unitTaxAmt= objProductDO.LineTaxAmount/(StringUtils.getFloat(objProductDO.units));

								tOrderVal 		+= 	StringUtils.roundOff(objProductDO.invoiceAmount,noOfRoundingOffdigits);
								tPrice 			+=	StringUtils.roundOff(objProductDO.totalPrice,noOfRoundingOffdigits);
								tDeposit 		+=	StringUtils.roundOff(objProductDO.depositPrice,noOfRoundingOffdigits);
								tDiscVal 		+=	0;//(objProductDO.DiscountAmt * StringUtils.getFloat(objProductDO.preUnits));
								tTaxVal 		+=	StringUtils.roundOff(unitTaxAmt2 ,noOfRoundingOffdigits);
//								tTaxVal 		+=	StringUtils.roundOff(unitTaxAmt * StringUtils.getFloat(objProductDO.preUnits),noOfRoundingOffdigits);

								calculatedDiscountOnText(objProductDO);
							}
						}
					}
					
					if(!Thread.interrupted())
					{
						totalDiscount = tDiscVal; 
						runOnUiThread(new  Runnable()
						{
							public void run()
							{
								etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
							}
						});
					}
					
					if(!Thread.interrupted())
					{
						orderTPrice	 = tPrice;
						totalIPrice 	 = tOrderVal;
						totTaxAmt 	 = tTaxVal;

						runOnUiThread(new  Runnable() 
						{
							public void run()
							{
								LogUtils.errorLog("totPrice", "totPrice last "+orderTPrice );
								evTotalValue.setText(""+curencyCode+" "+diffAmt.format(totalIPrice));
								tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));
//								tvTaxAmt.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));
								etTaxVal.setText(""+curencyCode+" "+diffAmt.format(totTaxAmt));
							}
						});
					}
				}
			});
			threadForCheckDiscount.start();
		}
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
				final ProductDO objProduct 		= hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				
				view = null;
				final LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
				TextView tvHeaderText			= (TextView)convertView.findViewById(R.id.tvHeaderText);
				TextView tvDescription			= (TextView)convertView.findViewById(R.id.tvDescription);
				final Button btnDelete			= (Button)convertView.findViewById(R.id.btnDelete);
				final TextView evCases			= (TextView)convertView.findViewById(R.id.evCases);
				final TextView tvInventory		= (TextView)convertView.findViewById(R.id.tvInventory);
				
				EditText evUnits				= (EditText)convertView.findViewById(R.id.evUnits);
				final ImageView ivDelete		= (ImageView)convertView.findViewById(R.id.ivDelete);
				
			//	evUnits.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(precision)});
				objProduct.etUnits				=  evUnits;
				
				tvHeaderText.setText(objProduct.SKU);
				tvDescription.setText(objProduct.Description);
				tvInventory.setVisibility(View.VISIBLE);
				
				tvInventory.setText("Reason : "+objProduct.reason +" | "+CalendarUtils.getFormatedDatefromString(objProduct.strExpiryDate));
				
				evUnits.setTag(objProduct);
				ivDelete.setTag(objProduct);
				
				if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
					evCases.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.arrow_2));
				
				evCases.setText(objProduct.UOM);
				evCases.setTag(objProduct.UOM);
				
				if(StringUtils.getInt(objProduct.preUnits) <= 0 )
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preUnits);
				
				setActionInViews(evUnits, true);
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
				
				tvHeaderText.setTag(childPosition);
				convertView.setTag(objProduct);
				
				btnDelete.setVisibility(View.GONE);
				
				if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
					evCases.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
							{
								CustomBuilder customDialog = new CustomBuilder(SalesManTakeReturnOrder.this, "Select UOM", true);
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
											calTotPrices(false, false);
										
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
										vec.remove(productDO);
										
										if(vec == null || vec.size() <= 0)
											AppConstants.hmCapturedInventory.remove(key);
										else
											AppConstants.hmCapturedInventory.put(key, vec);
									}
									calTotPrices(true, true);
								}
									
								hideLoader();
							}
						}, 300);
					}
				});
				
				setTypeFace(convertView);
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(55 * px)));
				llChildViews.addView(convertView);
			}
			return llChildViews;
		}


		public void getGroupView(LinearLayout llLayoutMiddle) 
		{
				if(llLayoutMiddle!=null)
				llLayoutMiddle.removeAllViews();
			for(int groupPosition = 0; groupPosition<vecCategoryIds.size(); groupPosition++)
			{
				String strCategory = "";
				CategoryDO objCategoryDO = null;
				if(AppConstants.hmCateogories != null && AppConstants.hmCateogories.size()>0)
					objCategoryDO = AppConstants.hmCateogories.get(vecCategoryIds.get(groupPosition));
				
				int childItemsSize = hmItems.get(vecCategoryIds.get(groupPosition)).size();
				
				try 
				{
					strCategory =objCategoryDO.categoryName;
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					strCategory = "";//Tubs
				}
				final LinearLayout convertView		=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llBottomLayout 	= 	(LinearLayout)convertView.findViewById(R.id.llBottomLayout);
				final LinearLayout llCode			= 	(LinearLayout)convertView.findViewById(R.id.llCode);
				final ImageView ivArrow				= 	(ImageView)convertView.findViewById(R.id.ivArrow);
				TextView tvTitleText				= 	(TextView)convertView.findViewById(R.id.tvInventryText);
				TextView tvNo						= 	(TextView)convertView.findViewById(R.id.tvNo);
				//====================Commented on 22nd Dec Not showing the header
			/*	if(childItemsSize > 0 && objCategoryDO != null)
				{
					tvNo.setText("("+ childItemsSize +" Sub Products)");
					tvTitleText.setText(objCategoryDO.categoryName);
				}
				else if(!strCategory.equalsIgnoreCase("") && childItemsSize > 0)
				{
					tvTitleText.setText(strCategory);
					tvNo.setText("("+childItemsSize+" Sub Products)");
				}
				else
					tvNo.setText("(0 Sub Products)");
				*/
				llBottomLayout.setTag(groupPosition);
				llBottomLayout.setVisibility(View.GONE);
				llBottomLayout.addView(getChildView(groupPosition));
				convertView.setTag("closed");
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if( vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString())) != null && vecCategoryIds.size() > 0 && hmItems.get(vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString()))).size()>0)
						{
							if(v.getTag().toString().equalsIgnoreCase("closed"))
							{
								ivArrow.setImageResource(R.drawable.arro2);
								v.setTag("open");
								if(llBottomLayout.getChildCount() > 0)
								{
									llCode.setVisibility(View.VISIBLE);
									llBottomLayout.setVisibility(View.VISIBLE);
								}
							}
							else
							{
								v.setTag("closed");
								llBottomLayout.setVisibility(View.GONE);
								ivArrow.setImageResource(R.drawable.arro);
								llCode.setVisibility(View.GONE);
							}
						}
					}
				});
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						convertView.performClick();
					}
				}, 100);
				
				setTypeFace(convertView);
				llLayoutMiddle.addView(convertView);
			}
		}

		//Method to refresh the List View
		private void refresh(HashMap<String, Vector<ProductDO>> hmItems) 
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				if(llLayoutMiddle != null)
					getGroupView(llLayoutMiddle);
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(from != null && (from.equalsIgnoreCase("checkINOption") || from.equalsIgnoreCase("replacement")))
		{
			finish();
		}
		else
		{
			showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Are you sure you want to cancel the return request?", getString(R.string.Yes), getString(R.string.No), "returnprocess");
		}
	}
	
	@Override
	public void onButtonNoClick(String from)
	{
		if(from.equalsIgnoreCase("captureImage"))
			showPassCodeDialog(null, from, false);
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("validate"))
		{
			Intent intent = new Intent(SalesManTakeReturnOrder.this, SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Recommended_Order) );
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("captureImage"))
		{
			Intent objIntent = new Intent(SalesManTakeReturnOrder.this, CaptureDamagedItemImage.class);
			objIntent.putExtra("vecImagePaths", productDOImage.vecDamageImages);
			objIntent.putExtra("position", mPosition);
			objIntent.putExtra("fromActivity", true);
			objIntent.putExtra("itemCode", productDOImage.SKU);
			objIntent.putExtra("desc", productDOImage.Description);
			startActivityForResult(objIntent, 500);
		}
		else if(from.equalsIgnoreCase("alreadyprinted"))
		{
			showLoader(getString(R.string.your_recommended_order_is_printing));
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
					showCustomDialog(SalesManTakeReturnOrder.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
		else if(from.equalsIgnoreCase("returnprocess"))
		{
			if(isAdvance)
			{
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_GOTO_CRLMAIN);
				sendBroadcast(intentBrObj);
			}
			else
			{
				Intent intentBrObj = new Intent();
    			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
    			sendBroadcast(intentBrObj);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 200)
		{
			if(AppConstants.hmCapturedInventory != null && adapterForCapture != null)
			{
				adapterForCapture.refresh(AppConstants.hmCapturedInventory);
				calTotPrices(false, true);
				tvNoOrder.setVisibility(View.GONE);
			}
			else
				tvNoOrder.setVisibility(View.VISIBLE);
		}
		else if(resultCode == 500)
    	{
			if(data != null)
			{
				int position 		= data.getExtras().getInt("position");
				@SuppressWarnings("unchecked")
				ArrayList<String> vecImagePaths   = (ArrayList<String>) data.getExtras().get("vecImagePaths");
				boolean fromActivity = data.getExtras().getBoolean("fromActivity");
				
				if(fromActivity)
				{
					Vector<ProductDO> vecProductDOs = AppConstants.hmCapturedInventory.get(strKeyNew);
					vecProductDOs.get(mPosition).vecDamageImages = vecImagePaths;
					AppConstants.hmCapturedInventory.put(strKeyNew, vecProductDOs);
				}
				else
				{
					if(adapter != null)
					{
						adapter.setItems(position, vecImagePaths);
					}
				}
			}
    	}
		else if(resultCode == 20000)
    	{
			showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.successful), getString(R.string.your_recommended_order_printed), getString(R.string.OK), null , "");
    	}
		else if(resultCode == 10000)
		{
			finish();
		}
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
	
	private boolean validateReason(Vector<ProductDO> veciItems)
	{
		strErrorMsg = "Please select reason for the item(s) ";
		boolean isFound = false;
		int count = -1, count1 = -1, count3 = -1;
		for(int i= 0; i< veciItems.size(); i++)
		{
			if(veciItems.get(i).reason.equalsIgnoreCase(""))
			{
				count++;
				isFound = true;
				if(count == 0)
					strErrorMsg = strErrorMsg +"'"+veciItems.get(i).SKU;
				else
					strErrorMsg = strErrorMsg +"\n"+veciItems.get(i).SKU;
			}
		}
		if(isFound)
		{
			strErrorMsg = strErrorMsg +"'.";
			return false;
		}
		else
		{
			strErrorMsg = "";
			String string = "", string1 = "", string2 = "";
			isFound = false;
			count1 = -1;
			
			for(int i= 0; i< veciItems.size(); i++)
			{
				if(TextUtils.isEmpty(veciItems.get(i).strExpiryDate))
				{
					count1++;
					isFound = true;
					
					if(count1 == 0)
						string = string +"'"+veciItems.get(i).SKU;
					else
						string = string +"\n"+veciItems.get(i).SKU;
				}
				
				if(TextUtils.isEmpty(veciItems.get(i).LotNumber))
				{
					count3++;
					isFound = true;
					
					if(count3 == 0)
						string2 = string2 +"'"+veciItems.get(i).SKU;
					else
						string2 = string2 +"\n"+veciItems.get(i).SKU;
				}
			}
			if(isFound)
			{
				if(!string1.equals("") && !string.equals(""))
				{
					if(strErrorMsg != null && strErrorMsg.length() > 0)
						strErrorMsg = strErrorMsg +" and enter expiry date for the item(s) "+string+"'.";
					else
						strErrorMsg = strErrorMsg+"Please enter expiry date for the item(s) "+string+"'.";
				}
				else if(!string.equals(""))
					strErrorMsg = strErrorMsg +"Please enter expiry date for the item(s) "+string+"'.";
				else if(!string2.equals(""))
					strErrorMsg = strErrorMsg +"Please enter lot number for the item(s) "+string2+"'.";
				else
					strErrorMsg = strErrorMsg+".";
				
				return false;
			}
			else   
				return true;
		}
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
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
			  
			  if(tempView != null)
			  {
				  String strDate = "", strMonth ="";
				  if(monthOfYear <9)
					  strMonth = "0"+(monthOfYear+1);
				  else
					  strMonth = ""+(monthOfYear+1);
		    		
				  if(dayOfMonth <10)
					  strDate = "0"+(dayOfMonth);
				  else
					  strDate = ""+(dayOfMonth);
				  
				  ProductDO objiItem = (ProductDO) tempView.getTag();
				  

				  if(objiItem.reason.equalsIgnoreCase(getString(R.string.good_condition)) 
						  && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
				  {
					  objiItem.strExpiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Expiry date can not be past date.", "Ok", null, "");
				  }
				  else if(objiItem.reason.equalsIgnoreCase(getString(R.string.ReturnReason1)) 
						  && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) < 0)
				  {
					  objiItem.strExpiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Expired date can not be future date.", "Ok", null, "");
				  }
				  else if(objiItem.reason.equalsIgnoreCase(getString(R.string.ReturnReason2)) 
						  && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
				  {
					  objiItem.strExpiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be past date", "Ok", null, "");
				  }
				  else if(objiItem.reason.equalsIgnoreCase(getString(R.string.ReturnReason2)) 
						  && Math.abs(CalendarUtils.getDateDiffInInt(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime())) >=30)
				  {
					  objiItem.strExpiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be more than one month.", "Ok", null, "");
				  }
				  else if((objiItem.reason.equalsIgnoreCase(getString(R.string.ReturnReason3))) 
						  && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
				  {
					  objiItem.strExpiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Expiry date can not be past date.", "Ok", null, "");
				  }
				  else
				  {
					  ((TextView)tempView).setText(year+"-"+strMonth+"-"+strDate);
					  objiItem.strExpiryDate = year+"-"+strMonth+"-"+strDate;
				  }
			  }
		  }
	  };
	  
	  public interface DatePickerListner
	  {
		  public void setDate(TextView tvView, ProductDO objiItem);
	  }
	  
	  private boolean isAmountValid()
	  {
		  boolean isValid = true;
		  if(orderId != null && orderId.length() > 0 && totalInvoicedPrice > 0)
		  {
			  if(totalIPrice > totalInvoicedPrice)
				  isValid = false;
		  }
		  
		  return isValid;
	  }
	  
	private boolean checkImageValidation(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isTrue = true;
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size()>0)
		{
			Set<String> set = AppConstants.hmCapturedInventory.keySet();
			for(String strKey : set)
			{
				Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(strKey);
				if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
				{
					int count = 0;
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(objProductDO.vecDamageImages == null || (StringUtils.getFloat(objProductDO.preUnits) > objProductDO.vecDamageImages.size()))
						{
							productDOImage = objProductDO;
							strKeyNew = strKey;
							mPosition = count;
							showCustomDialog(SalesManTakeReturnOrder.this, "Alert!", "You have not captured image(s) of item "+objProductDO.SKU+". Please tap on OK button to capture image(s).", "OK", "Skip", "captureImage");
							isTrue  = false;
							break;
						}
						count++;
					}
				}
			}
		}
		return isTrue;
	}
	 
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String fromText, boolean isCheckOut)
	{
		Intent intent = new Intent(SalesManTakeReturnOrder.this, SalesmanReturnOrderPreview.class);
		intent.putExtra("isReturnRequest", true);
		intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
		intent.putExtra("Discount", etDiscValue.getText().toString().trim());
        intent.putExtra("TotalOrderPrice", tvOrderValue.getText().toString().trim());
        intent.putExtra("Discount", etDiscValue.getText().toString().trim());
        intent.putExtra("VatAmount", etTaxVal.getText().toString().trim());
        intent.putExtra("TotalAmountWithVat", evTotalValue.getText().toString().trim());
        intent.putExtra("ProrataTaxAmount", "0");
        intent.putExtra("TotalTax", evTotalValue.getText().toString().trim());
		intent.putExtra("from", from);	
		intent.putExtra("isTask", isTask);	
		intent.putExtra("isMenu", isMenu);
		intent.putExtra("isFromReturn", "ReturnOrder");
		intent.putExtra("mallsDetails",mallsDetails);
		
		if(orderId != null && orderId.length() > 0)
		{
			intent.putExtra("invoicenum", orderId);
			intent.putExtra("invoiceamt", totalInvoicedPrice);
		}
		startActivityForResult(intent, 1000);
	}
}