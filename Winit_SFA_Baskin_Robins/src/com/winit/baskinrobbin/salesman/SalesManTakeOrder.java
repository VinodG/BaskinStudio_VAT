package com.winit.baskinrobbin.salesman;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.winit.baskinrobbin.salesman.adapter.AddRecomendedItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TaxDa;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.ItemWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;



@SuppressLint("DefaultLocale")
public class SalesManTakeOrder extends BaseActivity
{ 
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave, llOrderVal, llPricing, llCreditLimit;
	private TextView tvLu, tvCI, tvHeaderText, tvOrderValue, tvOrder, evTotalValue,tvTaxAmt,  etDiscValue , tvNoOrder,
					 tvDisHeader, tvCreditLimitVal;
	private Button  btnAddIetem, btnAddItems, btnCancel1, btnConfirmOrder, btnAddFOC, btnScan;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd;
	private Thread threadForCheckDiscount ;
	private AddRecomendedItemAdapter adapter;
	private float orderTPrice = 0.0f, totalDiscount = 0.0f, totalIPrice = 0.0f,totalTax=0.0f;
	private String orderedItems ="";
	private LinearLayout llLayoutMiddle;
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, DiscountDO> hmPromoDisc;
	private HashMap<String, Vector<String>> hmUOMFactors;
	private HashMap<String, Float> hmConversion; 
	private OrderDO objOrder;
	private CustomerCreditLimitDo creditLimit;
	private boolean isLPOOrder = false,isSignatureRequired=true;
	private String lpoVehicle = "";
	private HashMap<String, HHInventryQTDO> hmWarehouseQty;
	//private int precision=0;
	//=================Added For VAT================
	private HashMap<String, ArrayList<ItemWiseTaxViewDO>> hmAlltaxItemdetails = new HashMap<String, ArrayList<ItemWiseTaxViewDO>>();
	private ArrayList<OrderWiseTaxViewDO> arrOrderwiseApplicableTax=new ArrayList<OrderWiseTaxViewDO>();
	private ArrayList<OrderWiseTaxViewDO> arrOrderwiseAppliedTax=new ArrayList<OrderWiseTaxViewDO>();
	@Override
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.recommendedorder, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			objOrder  	 = (OrderDO) getIntent().getExtras().get("orderid"); 
			
			if(getIntent().getExtras().containsKey("isLPOOrder"))
				isLPOOrder = getIntent().getExtras().getBoolean("isLPOOrder");
			
			if(getIntent().getExtras().containsKey("lpoVehicle"))
				lpoVehicle = getIntent().getExtras().getString("lpoVehicle");
		}
		
		intialiseControls();

		boolean isSalesOrgActiveforTax=new TaxDa().getActivationStatusInSalesOrgForTAX(preference.getStringFromPreference(Preference.ORG_CODE, ""));
		if(true){//if(isSalesOrgActiveforTax){
			loadTaxDataforAllItems();
		}
		if(isLPOOrder)
			tvCI.setText("LPO Order");
		else if(objOrder == null)
			tvCI.setText("Sales Order");
		else
			tvCI.setText("Order Fulfillment");
	
		AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
		
		llBottomButtons.setVisibility(View.GONE);
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.VISIBLE);
		
		if(isLPOOrder)
			btnAddFOC.setVisibility(View.GONE);
		else
			btnAddFOC.setVisibility(View.VISIBLE);
		etDiscValue.setEnabled(true);
		etDiscValue.setFocusable(true);
		
		if(objOrder != null)
		{
			if(isLPOOrder)
				btnAddFOC.setVisibility(View.GONE);
			else
				btnAddFOC.setVisibility(View.VISIBLE);
			
			btnAddItems.setVisibility(View.GONE);
			llPricing.setVisibility(View.GONE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			llPricing.setVisibility(View.GONE);
		else
			llPricing.setVisibility(View.VISIBLE);
		
		btnCancel1.setText("Cancel");
		btnCancel1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnConfirmOrder.setText("Confirm Order ");
		btnConfirmOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				disableView(btnConfirmOrder);

				final int isAvail  = getAddedItemAvailability(AppConstants.hmCapturedInventory);
				if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() <= 0 )
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
				}
				else if(isAvail == -1)
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Please select atleast one Order item having quantity more than zero.", getString(R.string.OK), null, "");
				}
				else if(isAvail == 2)
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showPassCodeDialog(null, "", false);
				}
				else if(!checkCreditLimit(totalIPrice))
				{
					isSignatureRequired=false;
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					creditLimiPopup(mallsDetails, 0, null, false, false);
				}
				else
					gotoSalesPreview();
			}
		});
		
		IntentFilter filterForJourney = new IntentFilter();
		filterForJourney.addAction(AppConstants.ACTION_GOTO_TELEORDERS);
		registerReceiver(GotoTeleOrders, filterForJourney);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				String order_type = "";
				if(objOrder != null && objOrder.orderType != null)
					order_type = objOrder.orderType;
				
				//precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				hmInventory  = new OrderDetailsDA().getAvailInventoryQtys_Temp(order_type);
				hmConversion = new OrderDetailsDA().getUOMFactor();
				hmPromoDisc	 = new CaptureInventryDA().getPromoDisocunt(mallsDetails.site);
				hmUOMFactors = new OrderDetailsDA().getPricingUOM(mallsDetails.priceList, AppStatus.SALES_ORDER_TYPE);
				if(isLPOOrder)
					hmWarehouseQty = new OrderDetailsDA().getWarehouseStock(""+lpoVehicle);
				if(AppConstants.vecCategories == null)
					AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
				
				if(objOrder != null && objOrder.OrderId != null)
					AppConstants.hmCapturedInventory  = new CaptureInventryDA().getOrder_LPO_Detail(objOrder.OrderId, hmInventory, CaptureInventryDA.DELIVERY, mallsDetails.priceList, objOrder.orderSubType, hmConversion);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						onViewClickListners();
						
						if(AppConstants.vecCategories == null || AppConstants.vecCategories.size() > 0)
							AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
						
						if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
						{
							tvNoOrder.setVisibility(View.GONE);
							calTotPrices(true, true);
						}
						else
							tvNoOrder.setVisibility(View.VISIBLE);
						
						etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
						
						if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
						{
							evTotalValue.setText(""+curencyCode+" "+"0.00");
                            tvTaxAmt.setText(""+curencyCode+" "+"0.00");
							tvOrderValue.setText(""+curencyCode+" "+"0.00");
							etDiscValue.setText(""+curencyCode+" "+"0.00");
						}
						else
							etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
						
						hideLoader();
					}
				});
			}
		}).start();
		
		btnScan.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppConstants.objScanResultObject = null;
				Intent intent	=	new Intent(SalesManTakeOrder.this, CaptureActivity.class);
				startActivityForResult(intent, AppStatus.REQUEST_CODE);
			}
		});
		
		setTypeFace(llCapture_Inventory);
	}
//=============================Added For TAX===================================
	private void loadTaxDataforAllItems() {
		Object taxObject[] = new CommonDA().getAllTaxDetailsInfo("181");// Added For VAT
//		Object taxObject[] = new CommonDA().getAllTaxDetailsInfo(preference.getStringFromPreference(Preference.ORG_CODE, ""));// Added For VAT
		hmAlltaxItemdetails = (HashMap<String, ArrayList<ItemWiseTaxViewDO>>) taxObject[0];
		arrOrderwiseApplicableTax = (ArrayList<OrderWiseTaxViewDO>) taxObject[1];
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
		llOrderVal			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llOrderVal);
		llCreditLimit		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llCreditLimit);
		
		btnConfirmOrder 	= (Button)llCapture_Inventory.findViewById(R.id.btnFinalize);
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddItems			= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		btnAddFOC			= (Button)llCapture_Inventory.findViewById(R.id.btnAddFOC);
		evTotalValue		= (TextView)llCapture_Inventory.findViewById(R.id.evTotalValue);
        tvTaxAmt	     	= (TextView)llCapture_Inventory.findViewById(R.id.tvTaxAmt);//====Added For VAT=======
		btnCancel1 			= (Button)llCapture_Inventory.findViewById(R.id.btnSave);
		tvLu				= (TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvCI				= (TextView)llCapture_Inventory.findViewById(R.id.tvCI);
		tvNoOrder			= (TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
		
		etDiscValue			= (TextView)llCapture_Inventory.findViewById(R.id.etDiscValue);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		tvHeaderText		= (TextView)llCapture_Inventory.findViewById(R.id.tvHeaderText);
		
		tvCreditLimitVal	= (TextView)llCapture_Inventory.findViewById(R.id.tvCreditLimitVal);
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		tvOrder				= (TextView)llCapture_Inventory.findViewById(R.id.tvOrder);
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		
		btnScan				= (Button)llCapture_Inventory.findViewById(R.id.btnScan);
		btnScan.setVisibility(View.VISIBLE);
		
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		btnAddIetem.setTextColor(Color.WHITE);
		
		llOrderVal.setVisibility(View.VISIBLE);
		llTotalValue.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.GONE);
		
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
		
		btnAddIetem.setVisibility(View.GONE);
	}
	
	private synchronized void calTotPrices(boolean isLoaderShown, final boolean isRefresh)
	{
		if(isLoaderShown)
			showLoader(getString(R.string.loading));
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				orderTPrice 		= 	0.0f;
				totalIPrice 		= 	0.0f;
				totalDiscount 		= 	0.0f;
				totalTax            =   0.0f;
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				int count = 1;
				for(String key : set)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
					
					if(objOrder == null)
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						objProductDO.LineNo = ""+count++;
						
						if(!objProductDO.isPromotional)
						{
							DiscountDO temp 		= new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
							DiscountDO objDiscount  = getApplicableDiscounts(objProductDO, mallsDetails, hmPromoDisc, temp);
							
							if(temp != null)
							{
								objDiscount.perCaseValue  = temp.perCaseValue;
								objDiscount.TaxGroupCode  = temp.TaxGroupCode;
								objDiscount.TaxPercentage = temp.TaxPercentage;
							}
							else
							{
								objDiscount.perCaseValue  = 0+"";
								objDiscount.TaxGroupCode  = 0+"";
								objDiscount.TaxPercentage = 0;
							}
							
							if(StringUtils.getFloat(objProductDO.preUnits) <= 0 && objProductDO.etUnits != null)
								objProductDO.preUnits = objProductDO.etUnits.getText().toString();
							
							
							objProductDO.discountPercent = objDiscount.discount;
							float discount = 0;
							if(objDiscount.discountType == AppStatus.DISCOUNT_PERCENTAGE)
//								discount = StringUtils.getFloat(diffAmt.format(StringUtils.round(""+StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100),2)));
								discount = StringUtils.getFloat(/*diffAmt.format(StringUtils.round(*/""+StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100));
							else
								discount = StringUtils.getFloat(/*diffAmt.format(StringUtils.round(*/""+objDiscount.discount);
							float discountNew = Float.parseFloat(diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(totalDiscount)));
							discount=discount;
							objProductDO.discountAmountONEach=StringUtils.getDoubleFromFloatWithoutError(discount);//newly added by rouding off issue
							objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * StringUtils.roundOff(discount,noOfRoundingOffdigits);
							objProductDO.TaxGroupCode		=	objDiscount.TaxGroupCode;
							objProductDO.inventoryQty		=	0;
							objProductDO.itemPrice  		= 	StringUtils.getFloat(objDiscount.perCaseValue);//objDiscount.fPricePerCase + objDiscount.fDiscountAmt;
							objProductDO.totalPrice 		= 	StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objDiscount.perCaseValue);
//							objProductDO.unitSellingPrice 	= 	Double.parseDouble(new Float((StringUtils.getFloat(objDiscount.perCaseValue) - discount)).toString());
							objProductDO.unitSellingPrice 	= 	  Double.parseDouble(diffAmt.format(getDoubleFromFloatWithoutError(StringUtils.getFloat(objDiscount.perCaseValue) - discount)));
							objProductDO.invoiceAmount 		= 	(objProductDO.unitSellingPrice * StringUtils.getFloat(objProductDO.preUnits) );
							objProductDO.invoiceAmountwithoutTax 		= 	objProductDO.unitSellingPrice * StringUtils.getFloat(objProductDO.preUnits)  ; //==========Without TAX
							objProductDO.discountDesc  		= 	objDiscount.description;
							if(StringUtils.getFloat(objProductDO.preUnits)>0)
								getTaxforItem(hmAlltaxItemdetails,objProductDO);// Added For VAT
							else
								objProductDO.LineTaxAmount=0.0;

							formatPrices(objProductDO);

							objProductDO.invoiceAmount 	=	(objProductDO.invoiceAmount - objProductDO.depositPrice)+objProductDO.LineTaxAmount;
							objProductDO.invoiceAmountwithoutTax 		= 	objProductDO.unitSellingPrice * StringUtils.getFloat(objProductDO.preUnits)  ;

							//objProductDO.quantityBU = getQuantityBU(objProductDO, hmConversion,precision);
							objProductDO.quantityBU = getQuantityBU(objProductDO, hmConversion);
 							orderTPrice 		+= 	StringUtils.round(""+objProductDO.totalPrice,noOfRoundingOffdigits);
							totalDiscount 		+=	StringUtils.round(""+objProductDO.discountAmount,noOfRoundingOffdigits);;
							totalTax 		    +=	StringUtils.round(""+objProductDO.LineTaxAmount,noOfRoundingOffdigits);
							totalIPrice 		+= 	StringUtils.round(""+objProductDO.invoiceAmount,noOfRoundingOffdigits);
							objProductDO.DiscountAmt =  getDoubleFromFloatWithoutError(discount);
							objProductDO.Discount = objDiscount.discount;
							objProductDO.discountType = objDiscount.discountType;
						}
						else
						{
							objProductDO.discountPercent = 0;
							objProductDO.discountAmount  = 0.0;
							
							objProductDO.TaxGroupCode		=	"";
							objProductDO.TaxPercentage		=	0;
							objProductDO.inventoryQty		=	0;	
							objProductDO.itemPrice  		= 	0;
							objProductDO.totalPrice 		= 	0.0;
							objProductDO.unitSellingPrice 	= 	0;
							objProductDO.invoiceAmount 		= 	0.0;
							objProductDO.discountDesc  		= 	"";
							objProductDO.DiscountAmt 		=  	0;
							objProductDO.Discount 			= 	0;
							objProductDO.discountType 		= 	0;
							objProductDO.quantityBU = getQuantityBU(objProductDO, hmConversion);
						}
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
						
						if(isRefresh)
							new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
						
						etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
						evTotalValue.setText(""+curencyCode+" "+diffAmt.format(totalIPrice));
                        tvTaxAmt.setText(""+curencyCode+" "+diffAmt.format(totalTax));//======Added For VAT
						tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));
					}
				});
			}

		}).start();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
			tvNoOrder.setVisibility(View.GONE);
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
					creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetails);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(creditLimit != null)
						{
							llCreditLimit.setVisibility(View.VISIBLE);
							tvCreditLimitVal.setText(mallsDetails.currencyCode +" "+diffAmt.format(StringUtils.getFloat(creditLimit.availbleLimit)));
						}
					}
				});
			}
		}).start();
	}
	
	public void onViewClickListners()
	{
		btnAddItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				disableView(arg0);
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
		
		btnAddFOC.setOnClickListener(new OnClickListener()
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
						final HashMap<String, ProductDO> hmSelectedItems = new HashMap<String, ProductDO>();
						
						if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.containsKey(AppStatus.FOC_ITEM_TYPE))
						{
							Vector<ProductDO> vecTemp = AppConstants.hmCapturedInventory.get(AppStatus.FOC_ITEM_TYPE);
							if(vecTemp != null && vecTemp.size() > 0 )
							{
								for(ProductDO productDO : vecTemp)
								{
									productDO.etUnits = null;
									productDO.etCases = null;
									hmSelectedItems.put(productDO.SKU, productDO);
								}
							}
						}
						
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								 
								hideLoader();
							
									Intent intent = new Intent(SalesManTakeOrder.this, CaptureInventoryCategory.class);
									intent.putExtra("mallsDetails", mallsDetails);
									intent.putExtra("hmInventory", hmInventory);
									intent.putExtra("hmConversion", hmConversion);
									intent.putExtra("hmSelectedItems", hmSelectedItems);
									startActivityForResult(intent, 1000);
						}
								
						});
					}
				}).start();
			}
		});
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManTakeOrder.this);
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
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		cbLists.setVisibility(View.INVISIBLE);
		
		vecCategory = new CategoriesDA().getAvailableCategory_WithPricing(mallsDetails.priceList);
		
		if(vecCategory == null || vecCategory.size() == 0)
		{
			objAddNewSKUDialog.dismiss();
//			showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), getString(R.string.Stock_not_available), getString(R.string.OK), null, "");
			showCustomDialog(SalesManTakeOrder.this, "Warning!", "Customer Pricing Key and Items Pricing Key are not matching, Please contact your supervisor.", "OK", null, "");
		}
		
		final Button btnSearch = new Button(SalesManTakeOrder.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeOrder.this, "Select Category", true);
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
						ProductDO obj = (ProductDO) vecSearchedItemd.get(index);
						String strText = ((ProductDO)obj).SKU;
						String strDes = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
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
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etCategory.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
				}
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<ProductDO>();
					else
						vecSearchedItemd.clear();
					
					orderedItems ="";
					if(AppConstants.hmCapturedInventory!=null)
					{
						Set<String> set = AppConstants.hmCapturedInventory.keySet();
						for(String key : set)
						{
							Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
							for(ProductDO objProductDO : vecOrderedProduct)
								if(!objProductDO.isPromotional)
									orderedItems = orderedItems + "'"+objProductDO.SKU+"',";
						}
						if(orderedItems.contains(","))
							orderedItems = orderedItems.substring(0, orderedItems.lastIndexOf(","));
					}
					
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
							
							if(isLPOOrder)
								vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(null, catgId, orderedItems, mallsDetails.priceList, hmUOMFactors, false, hmInventory);
							else
								vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(null, catgId, orderedItems, mallsDetails.priceList, hmUOMFactors, true, hmInventory);
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
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManTakeOrder.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManTakeOrder.this));
										tvNoItemFound.setVisibility(View.VISIBLE);
										llBottomButtons.setVisibility(View.GONE);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Vector<ProductDO> veciItems= new Vector<ProductDO>();
				if(adapter != null)
					veciItems = getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
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
						objAddNewSKUDialog.dismiss();
						calTotPrices(true, true);
				}
				else
					showCustomDialog(SalesManTakeOrder.this, "Warning !", "Please select Items.", "OK", null, "");
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
			{
				objItem = (ProductDO) view.getTag();
			}
			
			if(objItem != null)
			{
				objItem.preUnits   = s.toString();
				objItem.quantityBU = getQuantityBU(objItem, hmConversion);
				
				if(StringUtils.getFloat(objItem.preUnits) > 0 && objItem.OrderType.equalsIgnoreCase(AppConstants.LPO_ORDER))
				{
					if(!checkItem(objItem))
					{
						objItem.preUnits = objItem.ActpreUnits;
						showToast("Entered quantity should not be greater than ordered quantity.");
						if(objItem.etUnits != null)
							objItem.etUnits.setText(objItem.ActpreUnits);   
					}
					else 
						asignTemValue(objItem);
				}
				else if(StringUtils.getFloat(objItem.preUnits) > 0 && !objItem.OrderType.equalsIgnoreCase(AppConstants.LPO_ORDER) && (!isInventoryAvail(objItem, hmInventory) && !isLPOOrder))
				{
					objItem.preUnits = "";
					showToast("Entered quantity should not be greater than available quantity.");
					if(objItem.etUnits != null)
						objItem.etUnits.setText(""); 
					
					objItem.quantityBU = getQuantityBU(objItem, hmConversion);
				}
			}
			
			if(threadForCheckDiscount != null && threadForCheckDiscount.isAlive())
				threadForCheckDiscount.interrupt();
			
			threadForCheckDiscount = new Thread(new Runnable() 
			{
				public void run() 
				{
					float tPrice = 0.0f, tDeposit = 0.0f, tOrderVal = 0.0f, tDiscVal = 0.0f,tLineTax=0.0f;
					
					if(hmItems == null || hmItems.size() == 0)
						return;

					Set<String> set = hmItems.keySet();
					
					vecCategoryIds.clear();
					
					outer :
					{
						tPrice 		= 0.0f;
						tDeposit 	= 0.0f;
						tOrderVal 	= 0.0f;
						tLineTax	= 0.0f;
						tDiscVal 	= 0.0f;
						for(String key : set)
						{
							vecCategoryIds.add(key);
							
							Vector<ProductDO> vecItems = hmItems.get(key);
							
							for(ProductDO objProductDO : vecItems)
							{
								if(Thread.interrupted())
									break outer;

								if(!objProductDO.isAdvanceOrder)
								{
									objProductDO.totalPrice 	=   ((objProductDO.itemPrice*StringUtils.getFloat(objProductDO.preUnits)));
									Double totalDiscountOnOrderItem=StringUtils.round(""+(new BigDecimal(objProductDO.discountAmountONEach+"").multiply(new BigDecimal((objProductDO.preUnits.length()==0?BigDecimal.ZERO:objProductDO.preUnits)+""))),2);
//									Double totalDiscountOnOrderItem=StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(diffAmt.format((objProductDO.discountAmountONEach*StringUtils.getInt(objProductDO.preUnits)))));
									objProductDO.invoiceAmount 	= StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(diffAmt.format((objProductDO.totalPrice-totalDiscountOnOrderItem))))	;
//									objProductDO.invoiceAmount 	= 	StringUtils.getDouble((objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preUnits))+"");
									if(StringUtils.getFloat(objProductDO.preUnits)>0)
									getTaxforItem(hmAlltaxItemdetails,objProductDO); //==========Added For VAT to check VAT is applicable or not for particular Item==============
									else
										objProductDO.LineTaxAmount=0.0;

									formatPrices(objProductDO);
									objProductDO.invoiceAmount 	=	StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(""+((objProductDO.invoiceAmount - objProductDO.depositPrice)+StringUtils.roundOff(objProductDO.LineTaxAmount,noOfRoundingOffdigits))));
//									objProductDO.invoiceAmount 	=	(objProductDO.invoiceAmount - objProductDO.depositPrice)+StringUtils.roundOff(objProductDO.LineTaxAmount,noOfRoundingOffdigits);

									objProductDO.invoiceAmountwithoutTax 		= 	StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(diffAmt.format((objProductDO.totalPrice-totalDiscountOnOrderItem))))	; //==========Without TAX
//									objProductDO.invoiceAmountwithoutTax 		= 	StringUtils.getDoubleFromFloatWithoutError(new Float(objProductDO.unitSellingPrice) * StringUtils.getFloat(objProductDO.preUnits) ) ; //==========Without TAX
									tOrderVal 		+= 	StringUtils.round(""+objProductDO.invoiceAmount,noOfRoundingOffdigits);
									tPrice 			+=	StringUtils.round(""+(objProductDO.totalPrice),noOfRoundingOffdigits);
									tDeposit 		+=	StringUtils.round(""+objProductDO.depositPrice,noOfRoundingOffdigits);
									tLineTax 		+=	StringUtils.round(""+objProductDO.LineTaxAmount,noOfRoundingOffdigits);
									tDiscVal 		+=	new Float(""+StringUtils.round(""+(new BigDecimal(objProductDO.discountAmountONEach+"").multiply(new BigDecimal((objProductDO.preUnits.length()==0?BigDecimal.ZERO:objProductDO.preUnits)+""))),2) );
//									tDiscVal 		+=	new Float(diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(new Float (objProductDO.DiscountAmt * StringUtils.getDouble(objProductDO.preUnits)+"") )) );
								}
								else
								{
									objProductDO.invoiceAmount 	= 	StringUtils.getDouble((objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preUnits))+"");
									objProductDO.invoiceAmountwithoutTax 		= 	objProductDO.unitSellingPrice * StringUtils.getFloat(objProductDO.preUnits)  ; //==========Without TAX
									tOrderVal 					+= 	objProductDO.invoiceAmount;
//									objProductDO.invoiceAmount 	= 	objProductDO.invoiceAmount - objProductDO.depositPrice; // Commented by Me for VAT
									objProductDO.invoiceAmount 	= 	objProductDO.invoiceAmount - objProductDO.depositPrice+objProductDO.LineTaxAmount;
									objProductDO.totalPrice 	= 	objProductDO.invoiceAmount;
									tPrice += StringUtils.round(""+objProductDO.totalPrice,noOfRoundingOffdigits);
									tDeposit 	+=	StringUtils.round(""+objProductDO.depositPrice,noOfRoundingOffdigits);
									if(StringUtils.getFloat(objProductDO.preUnits)>0)
									getTaxforItem(hmAlltaxItemdetails,objProductDO); //==========Added For VAT to check VAT is applicable or not for particular Item==============
									else
										objProductDO.LineTaxAmount=0.0;
									tLineTax 		+=	StringUtils.round(""+objProductDO.LineTaxAmount,noOfRoundingOffdigits);
								}
								
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
								etDiscValue.setText(""+curencyCode+" "+diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(totalDiscount)));
							}
						});
					}
					
					if(!Thread.interrupted())
					{
						orderTPrice	 = tPrice;
						totalIPrice  = tOrderVal;
						totalTax 	 = tLineTax;//=======Added For VAT

						runOnUiThread(new  Runnable() 
						{
							public void run()
							{
								LogUtils.errorLog("totPrice", "totPrice last "+orderTPrice );
								evTotalValue.setText(""+curencyCode+" "+diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(totalIPrice)));
								tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(orderTPrice)));
								tvTaxAmt.setText(""+curencyCode+" "+diffAmt.format(StringUtils.getDoubleFromFloatWithoutError(totalTax)));//===========Added For VAT
								
								if(!checkCreditLimit(totalIPrice))
									evTotalValue.setTextColor(Color.RED);
								else
									evTotalValue.setTextColor(getResources().getColor(R.color.amt_color));
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

		@SuppressWarnings("deprecation")
		public View getChildView(int groupPosition) 
		{
			final LinearLayout llChildViews = new LinearLayout(getApplicationContext());
			llChildViews.setOrientation(1);
			for(int childPosition =0;childPosition<hmItems.get(vecCategoryIds.get(groupPosition)).size();childPosition++)
			{
				final ProductDO objProduct = hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				
				view = null;
				final LinearLayout convertView= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell_ohq,null);
				final TextView tvHeaderText		 = (TextView)convertView.findViewById(R.id.tvHeaderText);
				final TextView tvWareHouseQty 	 = (TextView)convertView.findViewById(R.id.tvWareHouseQty);
				final TextView tvTaxLable		 = (TextView)convertView.findViewById(R.id.tvTaxLable);
				TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				final Button btnDelete		= (Button)convertView.findViewById(R.id.btnDelete);
				final TextView evCases		= (TextView)convertView.findViewById(R.id.evCases);
				final EditText evUnits		= (EditText)convertView.findViewById(R.id.evUnits);
				final ImageView ivDelete	= (ImageView)convertView.findViewById(R.id.ivDelete);
				
				objProduct.etUnits			=  evUnits;
				
				if(objOrder ==null && objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
					evCases.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.arrow_2));
				
				tvDescription.setText(objProduct.Description);
				//evUnits.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(precision)});
				
				evUnits.setTag(objProduct);
				convertView.setTag(objProduct);
				String ohqty = "", key = objProduct.SKU + objProduct.UOM; 
				if(hmInventory != null && hmInventory.containsKey(key))
				{	
					HHInventryQTDO qty 	= 	hmInventory.get(key);
				//	ohqty 				= 	"OHQ : " + StringUtils.round(""+qty.totalQt, precision)+" "+qty.UOM;
					ohqty 				= 	"OHQ : " + diffStock.format(qty.totalQt) +" "+qty.UOM;
				}
				else
					ohqty 				= 	"OHQ : " + diffStock.format(0) +" "+objProduct.UOM;
				
				tvHeaderText.setText(objProduct.SKU +" # "+ohqty);
				
				if(isLPOOrder)
				{
					String whQty = "";
					tvWareHouseQty.setVisibility(View.VISIBLE);
					if(hmWarehouseQty != null && hmWarehouseQty.containsKey(key))
					{	
						HHInventryQTDO qty 	= 	hmWarehouseQty.get(key);
						whQty 				= 	lpoVehicle+ " : "+ diffStock.format(qty.totalQt) +" "+qty.UOM;
					}
					else
						whQty 				= 	lpoVehicle+ " : "+ diffStock.format(0) +" "+objProduct.UOM;
					tvWareHouseQty.setText(whQty);
				}
				
				if(objProduct.preUnits.equalsIgnoreCase("") || objProduct.preUnits.equalsIgnoreCase("0"))
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preUnits);
				
				if(objProduct.isPromotional)
					convertView.setBackgroundResource(R.drawable.round_transparent_blue_bg);
				
				if(objProduct.OrderType.equalsIgnoreCase(AppConstants.LPO_ORDER))
					setActionInViews(evUnits, false);
				
				else
					setActionInViews(evUnits, true);
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
				tvHeaderText.setTag(childPosition);
				
				btnDelete.setVisibility(View.GONE);
				evCases.setText(objProduct.UOM);
				evCases.setTag(objProduct);
				ivDelete.setTag(objProduct);
				tvTaxLable.setTag(objProduct);

				if(objOrder == null && objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
				evCases.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						final ProductDO objProduct = (ProductDO) v.getTag();
						
						if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
						{
							CustomBuilder customDialog = new CustomBuilder(SalesManTakeOrder.this, "Select UOM", true);
							customDialog.setSingleChoiceItems(objProduct.vecUOM, objProduct.UOM, new CustomBuilder.OnClickListener() 
							{
								@Override
								public void onClick(CustomBuilder builder, Object selectedObject) 
								{
									String lastTag = ""+objProduct.UOM;
									evCases.setText((String)selectedObject);
									objProduct.UOM =(String)selectedObject;
									
									evCases.setTag(objProduct);
									
									if(!lastTag.equalsIgnoreCase(objProduct.UOM))
									{
										String ohqty = "";
										if(hmInventory != null && hmInventory.containsKey(objProduct.SKU + objProduct.UOM))
										{	
											HHInventryQTDO qty 	= 	hmInventory.get(objProduct.SKU + objProduct.UOM);
											ohqty 				= 	"OHQ : " + diffStock.format(qty.totalQt) +" "+qty.UOM;
											
											if(qty.totalQt < StringUtils.getFloat(objProduct.preUnits))
											{
												objProduct.preUnits = ""+(int)qty.totalQt;
												
												if(objProduct.etUnits != null)
													objProduct.etUnits.setText(objProduct.preUnits);
											}
										}
										
										tvHeaderText.setText(objProduct.SKU +" # "+ohqty);
										
										calTotPrices(true, false);
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
								
								if(productDO.isPromotional || AppConstants.hmCapturedInventory != null)
								{
									String key = "";
									if(productDO.isPromotional)
										key = AppStatus.FOC_ITEM_TYPE;
									else
										key = productDO.CategoryId;
									
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
				
				if(isLPOOrder)
					convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(55 * px)));
				else
					convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(45 * px)));
				llChildViews.addView(convertView);
			}
			return llChildViews;
		}

		public void getGroupView(LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle!=null)
				llLayoutMiddle.removeAllViews();
			for(int groupPosition = 0;groupPosition<vecCategoryIds.size();groupPosition++)
			{
				String strCategory = "";
				CategoryDO objCategoryDO = null;
				if(AppConstants.hmCateogories != null && vecCategoryIds != null)
					objCategoryDO = AppConstants.hmCateogories.get(vecCategoryIds.get(groupPosition));
				
				int childItemsSize = 0;
				
				if(vecCategoryIds != null)
					childItemsSize = hmItems.get(vecCategoryIds.get(groupPosition)).size();
				else 
					childItemsSize = hmItems.size();
				
				try 
				{
					if(objCategoryDO != null)
						strCategory = objCategoryDO.categoryName;
					else
						strCategory = vecCategoryIds.get(groupPosition);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					strCategory = "Others";
				}
				final LinearLayout convertView		=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llBottomLayout 		= (LinearLayout)convertView.findViewById(R.id.llBottomLayout);
				final LinearLayout llCode = (LinearLayout)convertView.findViewById(R.id.llCode);
				TextView tvCode 	= (TextView)convertView.findViewById(R.id.tvCode);
				TextView tvCases 	= (TextView)convertView.findViewById(R.id.tvCases);
				final ImageView ivArrow	= (ImageView)convertView.findViewById(R.id.ivArrow);
				TextView tvUnits 	= (TextView)convertView.findViewById(R.id.tvUnits);
				
				tvCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				
				TextView tvTitleText	= (TextView)convertView.findViewById(R.id.tvInventryText);
				TextView tvNo			= (TextView)convertView.findViewById(R.id.tvNo);
				tvNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvTitleText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				
				if(childItemsSize > 0 && objCategoryDO != null)
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
				llLayoutMiddle.addView(convertView);
				
				setTypeFace(llLayoutMiddle);
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
			super.onBackPressed();
	}
	
	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("notInstock"))
			finish();
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from); 
		if(from.equalsIgnoreCase("validate"))
		{
			Intent intent = new Intent(SalesManTakeOrder.this, SalesManTakeOrder.class);
			intent.putExtra("name",""+getString(R.string.Recommended_Order) );
			startActivity(intent);
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
					showCustomDialog(SalesManTakeOrder.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
		else if(from.equalsIgnoreCase("notInstock"))
		{
			new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
			etDiscValue.setText(""+curencyCode+" "+diffAmt.format(totalDiscount));
			tvTaxAmt.setText(""+curencyCode+" "+diffAmt.format(totalTax));//================Added For VAT
			evTotalValue.setText(""+curencyCode+" "+diffAmt.format(totalIPrice));
			tvOrderValue.setText(""+curencyCode+" "+diffAmt.format(orderTPrice));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == AppStatus.REQUEST_CODE && AppConstants.objScanResultObject!=null)
			loadScannedItem(AppConstants.objScanResultObject);
		
		else if(requestCode == 1000 && resultCode == 1000)
			addFOCToOrder(data);
		
		else if(resultCode == 10000)
			finish();
		
		else if(resultCode == 900)
			finish();
	}
	
	private void loadScannedItem(final ScanResultObject objScanResultObject)
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				orderedItems ="";
				if(AppConstants.hmCapturedInventory!=null)
				{
					Set<String> set = AppConstants.hmCapturedInventory.keySet();
					for(String key : set)
					{
						Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
						for(ProductDO objProductDO : vecOrderedProduct)
							if(!objProductDO.isPromotional)
								orderedItems = orderedItems + "'"+objProductDO.SKU+"',";
					}
					if(orderedItems.contains(","))
						orderedItems = orderedItems.substring(0, orderedItems.lastIndexOf(","));
				}
				
				final Vector<ProductDO> veciItems = new ProductsDA().getProductsDetailsByCategoryId(objScanResultObject.barcodeId,"", orderedItems, mallsDetails.priceList, hmUOMFactors, true, hmInventory);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						hideLoader();
						
						if(veciItems != null && veciItems.size() > 0)
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
								calTotPrices(true, true);
						}
						else
							showCustomDialog(SalesManTakeOrder.this, "Warning !", "Scaned Item not exist or pricing not maped to the customer.", "OK", null, "");
					}
				});
			}
		}).start();
	}
	
	private void addFOCToOrder(final Intent data) 
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(data != null)
				{
					if(AppConstants.hmCapturedInventory == null)
						AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
					
					HashMap<String, ProductDO> hmSelectedItems = (HashMap<String, ProductDO>) data.getExtras().get("hmSelectedItems");
					
					if(hmSelectedItems != null && hmSelectedItems.size() > 0)
					{
						Vector<ProductDO> vecTemp = new Vector<ProductDO>();
						Set<String> keys = hmSelectedItems.keySet();
						for(String key : keys)
						{
							ProductDO productDO = hmSelectedItems.get(key);
							vecTemp.add(productDO);
						}
						AppConstants.hmCapturedInventory.put(AppStatus.FOC_ITEM_TYPE, vecTemp);
						calTotPrices(true, true);
					}
					else
					{
						Vector<ProductDO> vecTemp = AppConstants.hmCapturedInventory.get(AppStatus.FOC_ITEM_TYPE);
						if(vecTemp != null && vecTemp.size()>0)
						{
							for (ProductDO productDO : vecTemp) 
							{
								productDO.units = 0+"";
								productDO.preUnits = 0+"";
							}
						}
						calTotPrices(true, true);
						hideLoader();
					}
				}
				else 
					hideLoader();
			}
		}).start();
	}

	private int getAddedItemAvailability(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		int isAvail = -1;
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size()>0)
		{
			Set<String> set = AppConstants.hmCapturedInventory.keySet();
			for(String strKey : set)
			{
				Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(strKey);
				if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
				{
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(StringUtils.getFloat(objProductDO.preUnits) > 0 && !objProductDO.isPromotional)
						{
							isAvail  = 1;
							break;
						}
						else if(StringUtils.getFloat(objProductDO.preUnits) > 0 && objProductDO.isPromotional)
						{
							isAvail  = 2;
							break;
						}
					}
				}
			}
		}
		return isAvail;
	}
	
	private void asignTemValue(ProductDO objItem)
	{
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.SKU))
		{
			HHInventryQTDO inventryDO = hmInventory.get(objItem.SKU + objItem.UOM);
			inventryDO.tempTotalQt = StringUtils.getInt(objItem.preUnits);
		}
	}
	
	private boolean checkItem(ProductDO objItem)
	{
		boolean isAvail = false;
		if(StringUtils.getFloat(objItem.preUnits) > StringUtils.getFloat(objItem.ActpreUnits))
		{
			isAvail = false;
		}
		else
			isAvail = true;
		
		if(isLPOOrder)
			return true;
		else
			return isAvail;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(GotoTeleOrders);
	}
	
	private boolean checkCreditLimit(float amount)
	{
		if(isLPOOrder)
			return true;
		
		if(creditLimit != null && StringUtils.getFloat(creditLimit.availbleLimit) < amount)
			return false;
		else
			return true;
	}
	
	@Override
	public void performSalesOrder()
	{
		gotoSalesPreview();
	}
	
	String errorItems = "";
	private void gotoSalesPreview() 
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0 && AppConstants.hmCapturedInventory.containsKey("PROMO"))
					AppConstants.hmCapturedInventory.remove("PROMO");
				
//				if(mallsDetails.isSchemeAplicable == 1)
//				{
//					filterItems(AppConstants.hmCapturedInventory);
//					PromotionalItemsDA promotionalItemsDA = new PromotionalItemsDA();
//					promotionalItemsDA.getPromotionInTemp(preference.getStringFromPreference(Preference.EMP_NO, ""), mallsDetails.site);
//					promotionalItemsDA.insertOrderItemsInTemp(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
//					promotionalItemsDA.insertOrderItemsInTemp_New(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
//					offerVectorNew =  promotionalItemsDA.getPromotionItems();
//				}
				if(AppConstants.hmCapturedInventory.containsKey(AppStatus.FOC_ITEM_TYPE) && !isLPOOrder)
					errorItems = checkTotalInventory();
					
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(TextUtils.isEmpty(errorItems))
						{
							Intent intent = new Intent(SalesManTakeOrder.this, SalesmanOrderPreview.class);
							intent.putExtra("isReturnRequest", true);
//							intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
							intent.putExtra("TotalPrice", ""+totalIPrice);
//							intent.putExtra("TotalOrderPrice", tvOrderValue.getText().toString().trim());
							intent.putExtra("TotalOrderPrice", ""+orderTPrice);
//							intent.putExtra("Discount", etDiscValue.getText().toString().trim());
							intent.putExtra("Discount",""+totalDiscount);
//							intent.putExtra("VatAmount", tvTaxAmt.getText().toString().trim());
							intent.putExtra("VatAmount", totalTax);
//							intent.putExtra("TotalAmountWithVat", evTotalValue.getText().toString().trim());
							intent.putExtra("TotalAmountWithVat", totalIPrice);
							intent.putExtra("ProrataTaxAmount", "0");
//							intent.putExtra("TotalTax", evTotalValue.getText().toString().trim());
							intent.putExtra("TotalTax", totalIPrice);
							intent.putExtra("objOrder",objOrder);
							intent.putExtra("isSignatureRequired",isSignatureRequired);
							intent.putExtra("isLPOOrder",isLPOOrder);
							intent.putExtra("mallsDetails",mallsDetails);
							intent.putExtra("lpoVehicle",lpoVehicle);
							startActivityForResult(intent, 1000);
						}
						else
						{
							if(errorItems.contains(",")){
								errorItems = errorItems.substring(0, errorItems.lastIndexOf(","));
								showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "For the following item(s) : " +
										"\n"+errorItems+"\navailable quantity is less than ordered quantity. Please reduce the FOC item quantity.", "OK", null, "");
								errorItems="";
							}
							
						}
						
						hideLoader();
					}
				});
			}
		}).start();				
	}
	
	private String checkTotalInventory()
	{
		String errorItems = "";
		HashMap<String, Float> hmTotalQty = new HashMap<String, Float>();
		Set<String> keys  = AppConstants.hmCapturedInventory.keySet();
		for(String key : keys)
		{
			Vector<ProductDO> vec = AppConstants.hmCapturedInventory.get(key);
			
			for(ProductDO productDO : vec)
			{
				float qty = 0;
				if(hmTotalQty.containsKey(productDO.SKU))
				{
					qty  	  = hmTotalQty.get(productDO.SKU);
					qty       += productDO.quantityBU;
					hmTotalQty.put(productDO.SKU, qty);
				}
				else
				{
					qty       = productDO.quantityBU;
					hmTotalQty.put(productDO.SKU, (float)productDO.quantityBU);
				}
				String invKey = productDO.SKU + productDO.UOM;
				HHInventryQTDO inventryDO = hmInventory.get(invKey);
				
				if(inventryDO == null || qty > inventryDO.quantityBU)
					errorItems = errorItems+productDO.SKU+",";
			}
		}
		
		return errorItems;
	}
	
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String from,
			boolean isCheckOut) 
	{
		gotoSalesPreview();
	}
	
	public Vector<ProductDO> getSelectedItems()
	{
		Vector<ProductDO> vecTemp = new Vector<ProductDO>();
		if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
		{
			for(ProductDO productDO : vecSearchedItemd)
				if(productDO.isSelected)
					vecTemp.add(productDO);
		}
		return vecTemp;
	}
	Double getDoubleFromFloatWithoutError(float val)
	{
		return Double.parseDouble(new Float(val).toString());

	}
}