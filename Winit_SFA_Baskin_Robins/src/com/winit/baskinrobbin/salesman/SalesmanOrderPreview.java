package com.winit.baskinrobbin.salesman;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.adapter.OrderPreviewAdapter;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.CustomListView;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.SiteCreditLimitDO;
import com.winit.baskinrobbin.salesman.listeners.MakePaymentListner;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SalesmanOrderPreview extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llTotalPrice, llTrxTypeName, llOrderPreview , llMiddleLayout, llEsignature, llCustomerSign, llPresellerSign, llHeaderLayout, llPresellerSignature, llCustomerSignature,llCNReason;
	private TextView tvHeaderPreview, tvItemName, tvOrderPreviewunits , tvTotalAmount, tvTotalAmountValue, tvOrderPreviewCases,
					 tvCustomerPasscode, tvPresellerSign, tvCustomerSign, tvOrderPreviewPriceValue , tvOrderPreviewFooterCases, tvOrderPreviewFooterunits
					 ,tvDeliveryDate, tvDeliveryDateValue, tvDeliveryToTimeValue,tvDeliveryFromTimeValue, tvDeliveryToTime, tvDeliveryFromTime
					 ,tvOrderActualAmount,tvOrderTaxAmt, tvImage,tvlanguage, tvLu , tvTotalItemAmt, tvDscount, tvTotalInvoiceAmt
					 ,tvTotalItemAmtValue, tvDscountValue,tvTaxVal, tvTotalInvoiceAmtValue, tvTrxTypeName, tvTrxTypeNameV, tvBatchSourceName,
					 tvBatchSourceNameV, tvRoundOff, tvRoundOffValue;
	private Button btnRefresh, btnFinalize, btnPrintSalesOrder , btnPrintMerchant , btnCustomerSignClear , btnPresellerSignClear,
				   btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
	private EditText etLPO,etCNReason;
	private TextView tvAddressPreview,tvMasafiLogoTitle;
	private CustomListView lvPreviewOrder;
	private OrderPreviewAdapter orderPreviewAdapter;
	public static Vector<ProductDO> vecMainProducts;
	public ArrayList<ProductDO> vecOfferProducts;
	private double totalPrice = 0, totalAmount = 0,   totalPerItemAmount = 0, overDue = 0,
				  discount = 0.0, totalSalesPrice = 0,totalInvoicedPriceWithoutVatAmt =0.0, totalInvoicedPrice =0.0,totVatAmt=0.0,tvDscountVal=0.0f, totalTax,  LOW_RANGE = 0,
			HEIGH_RANGE = 0;
	private float TotalOrderPrice=0.0f,VatAmount=0.0f,TotalAmountWithVat=0.0f,ProrataTaxAmount=0.0f,TotalTax=0.0f;
	private float totalCases = 0,totalUnits = 0,roundOff = 0;
	private JourneyPlanDO mallsDetails;
	private boolean isSalesOrderGerenaterd = false, isPosted = false;
	private MyView customerSignature, presellerSignature;
	private static Paint mPaint;
	private boolean isPresellerSigned = false, isCustomerSigned = false;
	private final int ID_DATEPICKER =1;
	private OrderDO orderDO;
	private static final int CAMERA_PIC_REQUEST = 2500;
	private  Uri mCapturedImageURI;
	private ImageView ivStampImage;
	private Bitmap stampBitmap;
	private int mCount = 0, STATUS = 0;
	private CustomerCreditLimitDo customerLimit;
	private String batchSourceName, orderId, strSymbol = "+", strUUId, lpoVehicle = "";
	private boolean isConfirmOnHoldOrder = false, isToSetText = true,forHoldOrder=false;
	private final String [] arrSymbol = {"+","-"};
	private CustomDialog mCustomDialog;
	private boolean isLPOOrder = false, isLPOOrderDelivery,isSignatureRequired=true;
	private View tempView;
	private LinearLayout llRoundOff;
	private OfflineDA offlineDA = new OfflineDA();
	private CustomerDA customerDA=new CustomerDA();
	private String tvCountry="";

	private float totalTaxAmt=0;
	boolean isFromHold=false;
	private Vector<ProductDO> vecOrdProduct01;
	private ArrayList<ProductDO> vecOrdProductNew;

	private Object sync_Order = new Object();
	private Boolean isOrderProcessingStarted = false;
	@Override
	public void initialize()
	{
		//inflate the preview_order_list layout 
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_order_list, null);
		llBody.addView(llOrderPreview,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		preference.removeFromPreference(Preference.ORDER_NO);
		preference.commitPreference();
		vecMainProducts = new Vector<ProductDO>();
		
		if(getIntent().getExtras() != null)
		{
			totalPrice 				= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalPrice"));
			discount   				= 	StringUtils.round(""+StringUtils.getFloat(getIntent().getExtras().getString("Discount")),noOfRoundingOffdigits
			);

			//=============Added For VAT==============
			if(getIntent().getExtras().getString("TotalOrderPrice")!=null)
			TotalOrderPrice 				= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalOrderPrice").replaceAll("[^\\d.]", ""));
			if(getIntent().getExtras().getString("VatAmount")!=null)
			VatAmount 				= 	StringUtils.getFloat(getIntent().getExtras().getString("VatAmount").replaceAll("[^\\d.]", ""));
			if(getIntent().getExtras().getString("TotalAmountWithVat")!=null)
			TotalAmountWithVat   				= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalAmountWithVat").replaceAll("[^\\d.]", ""));
			if(getIntent().getExtras().getString("ProrataTaxAmount")!=null)
			ProrataTaxAmount 				= 	StringUtils.getFloat(getIntent().getExtras().getString("ProrataTaxAmount").replaceAll("[^\\d.]", ""));
			if(getIntent().getExtras().getString("TotalTax")!=null)
			TotalTax   				= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalTax").replaceAll("[^\\d.]", ""));

			mallsDetails 			= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			orderDO					= 	(OrderDO) getIntent().getExtras().get("objOrder");
			vecOfferProducts 		= 	(ArrayList<ProductDO>) getIntent().getExtras().get("offerVector");
			if(getIntent().getExtras().containsKey("isConfirmOnHoldOrder"))
				isConfirmOnHoldOrder = getIntent().getExtras().getBoolean("isConfirmOnHoldOrder");


			if(getIntent().getExtras().containsKey("lpoVehicle"))
				lpoVehicle = getIntent().getExtras().getString("lpoVehicle");

			if(getIntent().getExtras().containsKey("isLPOOrder"))
				isLPOOrder = getIntent().getExtras().getBoolean("isLPOOrder");

			if(getIntent().getExtras().containsKey("isSignatureRequired"))
				isSignatureRequired = getIntent().getExtras().getBoolean("isSignatureRequired");

			if(getIntent().getExtras().containsKey("isLPOOrderDelivery"))
				isLPOOrderDelivery = getIntent().getExtras().getBoolean("isLPOOrderDelivery");

			if(getIntent().getExtras().containsKey("totalTaxAmt"))
				totalTaxAmt = getIntent().getExtras().getFloat("totalTaxAmt");//Added on 03JAN
			if(getIntent().getExtras().containsKey("isFromHold"))
				isFromHold = getIntent().getExtras().getBoolean("isFromHold");//Added on 03JAN
				//======================================================
			if(getIntent().getExtras().containsKey("forHoldOrder"))
				forHoldOrder = getIntent().getExtras().getBoolean("forHoldOrder");
			vecOrdProductNew					= (ArrayList<ProductDO>) getIntent().getExtras().get("vecOrdProduct");

		}
		
		InitializeControls();
		
		setTypeFace(llOrderPreview);
		preference.saveBooleanInPreference("salesOrderPrited", false);
		preference.commitPreference();
		
		lvPreviewOrder = new CustomListView(SalesmanOrderPreview.this);
		llMiddleLayout.addView(lvPreviewOrder,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lvPreviewOrder.setCacheColorHint(0);
		lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
		lvPreviewOrder.setVerticalScrollBarEnabled(false); 
		lvPreviewOrder.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvPreviewOrder.addFooterView(llEsignature, null, false);
		lvPreviewOrder.addHeaderView(llHeaderLayout,null,false);
		lvPreviewOrder.setAdapter(orderPreviewAdapter = new OrderPreviewAdapter(SalesmanOrderPreview.this, vecMainProducts, false, true, mallsDetails.channelCode));
	
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{

				vecOrdProduct01	=	new OrderDetailsDA().getOrderDetails(orderDO, null);
				vecMainProducts 	= 	new Vector<ProductDO>();
				
				if(orderDO == null)
					//orderId        	= 	new OrderDA().getOrderId();
					orderId        	= 	offlineDA.getNextSequenceNumber(OfflineDataType.ORDER);
				else
					orderId			=   orderDO.OrderId;
				
				strUUId 			= 	StringUtils.getUniqueUUID();
				batchSourceName 	= 	new CommonDA().getBatchSourceName();
				customerLimit   	= 	customerDA.getCustomerCreditLimit(mallsDetails);
				
				//initializing all Main Vector which contain all the product list
				Vector<String> vecCategoryIds = new Vector<String>();
				if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
				{
					hideLoader();
					return;
				}
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				mCount = 0;


				if(!forHoldOrder)
				{
					for(int i=0; i<vecCategoryIds.size(); i++)
					{
						Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(vecCategoryIds.get(i));
						for(ProductDO objProductDO : vecOrderedProduct)
						{
							if((StringUtils.getFloat(objProductDO.preUnits) > 0) && !objProductDO.isAdvanceOrder)
							{
								totalPerItemAmount 		= 	StringUtils.getFloat((totalPerItemAmount + objProductDO.itemPrice)+"");
								totalSalesPrice 		= 	StringUtils.getFloat((totalSalesPrice + objProductDO.itemPrice*StringUtils.getFloat(objProductDO.preUnits))+"");
								totalCases 				= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
								totalUnits 				= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);

								objProductDO.reason 	= 	"";
								totalInvoicedPrice 	= 	StringUtils.round(""+totalInvoicedPrice,3)+StringUtils.round(""+objProductDO.invoiceAmount,noOfRoundingOffdigits);
								totalInvoicedPriceWithoutVatAmt 		= 	StringUtils.round(""+totalInvoicedPriceWithoutVatAmt,noOfRoundingOffdigits)+StringUtils.round(""+objProductDO.invoiceAmountwithoutTax,noOfRoundingOffdigits);//=============Added For VAT
								totVatAmt		 		= 	StringUtils.round(""+totVatAmt,noOfRoundingOffdigits)+StringUtils.round(""+objProductDO.LineTaxAmount,noOfRoundingOffdigits);
								tvDscountVal			+= StringUtils.round(""+objProductDO.DiscountAmt,noOfRoundingOffdigits);

								mCount++;

								objProductDO.discountAmount = objProductDO.DiscountAmt;
								vecMainProducts.add(objProductDO);
							}
							else if(objProductDO.isAdvanceOrder)
							{
								totalSalesPrice  	= 	StringUtils.getFloat(totalSalesPrice + objProductDO.totalPrice+"");
								totalPerItemAmount 	=  	StringUtils.getFloat(totalPerItemAmount +objProductDO.itemPrice+"");
								totalCases 			= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
								totalUnits			= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);
								totalInvoicedPrice 	= 	totalInvoicedPrice+objProductDO.invoiceAmount;
								totalInvoicedPriceWithoutVatAmt 	= 	totalInvoicedPriceWithoutVatAmt+objProductDO.invoiceAmountwithoutTax; // =======Commented on 11th DEC 2017
								totVatAmt		 		= 	totVatAmt+objProductDO.LineTaxAmount;///=============Added For VAT
								totalTax		    	+= objProductDO.TaxPercentage;
								tvDscountVal			+= objProductDO.DiscountAmt;

								mCount++;
								objProductDO.reason = 	"";

								objProductDO.discountAmount = objProductDO.DiscountAmt;
								vecMainProducts.add(objProductDO);
							}
						}
					}
				}else{
					for(ProductDO objProductDO : vecOrdProductNew)
					{
						if((StringUtils.getFloat(objProductDO.preUnits) > 0) && !objProductDO.isAdvanceOrder)
						{
							totalPerItemAmount 		= 	StringUtils.getFloat((totalPerItemAmount + objProductDO.itemPrice)+"");
							totalSalesPrice 		= 	StringUtils.getFloat((totalSalesPrice + objProductDO.itemPrice*StringUtils.getFloat(objProductDO.preUnits))+"");
							totalCases 				= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits 				= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);

							objProductDO.reason 	= 	"";
							totalInvoicedPrice 	= 	totalInvoicedPrice+objProductDO.invoiceAmount;
							totalInvoicedPriceWithoutVatAmt 		= 	totalInvoicedPriceWithoutVatAmt+objProductDO.invoiceAmountwithoutTax;//=============Added For VAT
							totVatAmt		 		= 	totVatAmt+objProductDO.LineTaxAmount;
							tvDscountVal			+= objProductDO.DiscountAmt;

							mCount++;

							objProductDO.discountAmount = objProductDO.DiscountAmt;
							vecMainProducts.add(objProductDO);
						}
						else if(objProductDO.isAdvanceOrder)
						{
							totalSalesPrice  	= 	StringUtils.getFloat(totalSalesPrice + objProductDO.totalPrice+"");
							totalPerItemAmount 	=  	StringUtils.getFloat(totalPerItemAmount +objProductDO.itemPrice+"");
							totalCases 			= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits			= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);
							totalInvoicedPrice 	= 	totalInvoicedPrice+objProductDO.invoiceAmount;
							totalInvoicedPriceWithoutVatAmt 	= 	totalInvoicedPriceWithoutVatAmt+objProductDO.invoiceAmountwithoutTax; // =======Commented on 11th DEC 2017
							totVatAmt		 		= 	totVatAmt+objProductDO.LineTaxAmount;///=============Added For VAT
							totalTax		    	+= objProductDO.TaxPercentage;
							tvDscountVal			+= objProductDO.DiscountAmt;

							mCount++;
							objProductDO.reason = 	"";

							objProductDO.discountAmount = objProductDO.DiscountAmt;
							vecMainProducts.add(objProductDO);
						}
					}

				}

				//getting customer's total price by adding the total price of current order + pending balance
				totalAmount = totalInvoicedPrice;
				
				totalTax = totalTax / mCount;
				tvCountry=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);//added on 02JAN
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						orderPreviewAdapter.refresh(vecMainProducts);
						//setting text for the total-Layout
						tvOrderPreviewFooterunits.setText(" "+(int)totalUnits);
						tvOrderPreviewPriceValue.setText(" "+diffAmt.format(totalPerItemAmount));
						tvTotalAmountValue.setText(" "+diffAmt.format(totalSalesPrice));
						//tvDeliveryDateValue.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getDeliverydate()));
						tvDeliveryDateValue.setTag(CalendarUtils.getDeliverydate());
						tvOrderActualAmount.setText(" "+diffAmt.format(totalInvoicedPrice));
						tvOrderTaxAmt.setText(" "+diffAmt.format(totVatAmt));//=============Need To add For VAT

						llTotalPrice.setVisibility(View.VISIBLE);
						
						tvTotalItemAmtValue.setText(""+curencyCode+" "+diffAmt.format(totalSalesPrice));
						tvDscountValue.setText(""+curencyCode+" "+diffAmt.format(discount)); //===Added For VAT & commented Below lines
//						tvDscountValue.setText(""+curencyCode+" "+diffAmt.format(totalSalesPrice+totVatAmt-totalInvoicedPrice)); //===Added For VAT & commented Below lines
//						tvDscountValue.setText(""+curencyCode+" "+diffAmt.format(totalSalesPrice-totalInvoicedPrice));
						if(isFromHold)
						tvTaxVal.setText(""+curencyCode+" "+diffAmt.format(totalTaxAmt));
						else
						tvTaxVal.setText(""+curencyCode+" "+diffAmt.format(totVatAmt));

						float roundOff = 0;
						
						if(orderDO != null)
							roundOff = orderDO.roundOffVal;
						
						totalInvoicedPrice = totalInvoicedPrice + roundOff;
//						tvTotalInvoiceAmtValue.setText(""+curencyCode+" "+diffAmt.format(totalInvoicedPrice));//======Commented On 11th DEC 2012
//						tvTotalInvoiceAmtValue.setText(""+curencyCode+" "+diffAmt.format(totalInvoicedPrice));
						if(isFromHold)
							tvTotalInvoiceAmtValue.setText(""+curencyCode+" "+diffAmt.format(totalTaxAmt+totalSalesPrice));
						else
							tvTotalInvoiceAmtValue.setText(""+curencyCode+" "+diffAmt.format(totalInvoicedPrice));

						if(Math.abs(roundOff) >0)
						{
							llRoundOff.setVisibility(View.VISIBLE);
							tvRoundOffValue.setText(""+curencyCode+" "+diffAmt.format(roundOff));
						}
						
						tvBatchSourceNameV.setText(batchSourceName);
						
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
						{
							tvCustomerSign.setText("Received By");
							tvPresellerSign.setText("Delivered By");
							
							tvOrderPreviewPriceValue.setVisibility(View.GONE);
							tvTotalAmountValue.setVisibility(View.GONE);
							tvOrderActualAmount.setVisibility(View.GONE);
							tvOrderTaxAmt.setVisibility(View.GONE);
							llTotalPrice.setVisibility(View.GONE);
							tvOrderPreviewunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
							tvOrderPreviewFooterunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
						}
						else
						{
							tvCustomerSign.setText(getString(R.string.Customer_Signature));
							tvPresellerSign.setText(getString(R.string.Preseller_Signature));
						}

						//added on 02JAN
						if(tvCountry.equalsIgnoreCase("AE")) {
							tvAddressPreview.setText("" + getApplicationContext().getResources().getString(R.string.baskin_robbin_address));
						}
						else if(tvCountry.equalsIgnoreCase("SA")) {
							tvMasafiLogoTitle.setText("Jumeirah Trading Co. Ltd.");
							tvAddressPreview.setText("" + getApplicationContext().getResources().getString(R.string.baskin_robbin_address_SAR));

						}



						hideLoader();

					}
				});

				/*//added on 02JAN for address
				tvCountry=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(tvCountry.equalsIgnoreCase("AE"))
						tvAddressPreview.setText(""+getApplicationContext().getResources().getString(R.string.baskin_robbin_address));
					else if(tvCountry.equalsIgnoreCase("SA"))
							tvAddressPreview.setText(""+getApplicationContext().getResources().getString(R.string.baskin_robbin_address_SAR));
					}
				});*/
			}
		}).start();
        
		
		if(orderDO != null && orderDO.LPOCode != null && orderDO.LPOCode.length()>0)
		{
			etLPO.setText(orderDO.LPOCode+"");
			disableView(etLPO);
		}
		
		btnFinalize.setTag("Finalize");
		btnFinalize.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				isOrderProcessingStarted=false;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run()
					{
						synchronized (sync_Order)
						{
							if(!isOrderProcessingStarted)
							{
								String currCode=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);
								int roundOff=new CustomerDA().getCustomerCurrencyStatus(currCode);
								isSalesOrderGerenaterd = false;
								disableView(btnFinalize);

								if(v.getTag().toString().equalsIgnoreCase("Finalize"))
								{
									if(isSignatureRequired){
										if(!isCustomerSigned)
										{
											if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
												showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");

											else
												showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
										}

										else if(!isPresellerSigned)
											showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please sign before submitting the order.", getString(R.string.OK), null, "scroll");


										else if(tvTrxTypeNameV.getText().toString().equalsIgnoreCase(""))
											showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please select trx type name.", getString(R.string.OK), null, "scroll");
										else if(tvBatchSourceNameV.getText().toString().equalsIgnoreCase(""))
											showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please select batch source name.", getString(R.string.OK), null, "scroll");
										else
										{
//											totalInvoicedPrice  = StringUtils.getFloat(diffAmt.format(totalInvoicedPrice));
											if(totalInvoicedPrice == 0)
											{
												if(orderDO == null)
													postOrder(AppConstants.HHOrder);
											}
											else
											{
												float decValue = StringUtils.getFloat(diffAmt.format(totalInvoicedPrice % 1));
												if((roundOff > 0) && (decValue != 0 && decValue != .50f) && !isLPOOrderDelivery)
												{
													showRoundoffPopup();
												}
												else {
													performOrderInsertAction();
												}
											}
										}
									}
									else{

										totalInvoicedPrice  = StringUtils.getFloat(diffAmt.format(totalInvoicedPrice));
										if(totalInvoicedPrice == 0)
										{
											if(orderDO == null)
												postOrder(AppConstants.HHOrder);
										}

										else
										{
											float decValue = StringUtils.getFloat(diffAmt.format(totalInvoicedPrice % 1));
											if((( decValue != 0 && decValue != .50f) || roundOff > 0) && !isLPOOrderDelivery && roundOff == 1)
											{
												showRoundoffPopup();
											}
											else {
												performOrderInsertAction();
											}
										}

									}
								}
								else
									showCustomDialog(SalesmanOrderPreview.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");

								isOrderProcessingStarted=true;
							}

						}
					}
				},1000);

			}
		});
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(isPosted || isSalesOrderGerenaterd)
					btnOrderPreviewContinue.performClick();
				else 
					finish();
			}
		});
		
	//	btnOrderPreviewContinue.setText("Continue ");
		btnOrderPreviewContinue.setText("Finish ");
		
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showOrderCompletePopup();
//				Intent intentBrObj = new Intent();
//				intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
//				sendBroadcast(intentBrObj);
			}
		});
		
		btnPrintSalesOrder.setText(" Print Order ");
		btnPrintSalesOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				processPrint();
			}
		});
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        presellerSignature = new MyView(SalesmanOrderPreview.this, "psign");
		presellerSignature.setDrawingCacheEnabled(true);
		presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		customerSignature = new MyView(SalesmanOrderPreview.this, "csign");
		customerSignature.setDrawingCacheEnabled(true);
		customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		if(customerSignature != null)
			llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		if(presellerSignature != null)
			llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	
		btnCustomerSignClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				isCustomerSigned = false;
				llCustomerSign.removeAllViews();
				customerSignature = new MyView(SalesmanOrderPreview.this, "csign");
				customerSignature.setDrawingCacheEnabled(true);
				customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_AUTO);
//				customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		});
		

		btnPresellerSignClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				isPresellerSigned = false;
				llPresellerSign.removeAllViews();
				presellerSignature = new MyView(SalesmanOrderPreview.this,"psign");
				presellerSignature.setDrawingCacheEnabled(true);
				presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_AUTO);
//				presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		});
		
		tvDeliveryFromTimeValue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etLPO.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				try
				{
					tempView = v;
					showDialog(ID_DATEPICKER);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tvDeliveryDateValue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etLPO.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				try
				{
					tempView = v;
					showDialog(ID_DATEPICKER);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tvlanguage.setClickable(false);
		tvlanguage.setEnabled(false);
		tvlanguage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			}
		});
		
		if(isConfirmOnHoldOrder)
		{
			btnRefresh.setText("Confirm On-Hold Order");
			btnRefresh.setTag("Confirm On-Hold Order");
			/*btnRefresh.setText("Refresh On-Hold Order");
			btnRefresh.setTag("Refresh On-Hold Order");*/
			
			btnRefresh.setVisibility(View.VISIBLE);
			btnFinalize.setVisibility(View.GONE);
	
			
		//	btnPrintSalesOrder.setVisibility(View.VISIBLE);

			/*llPresellerSignature.setVisibility(View.GONE);
			llCustomerSignature.setVisibility(View.GONE);*/
			
			setActionInViews(etLPO, false);
			if(orderDO != null)
				etLPO.setText(orderDO.LPOCode);
		}
		else
		{
			btnRefresh.setText("Refresh On-Hold Order");
			btnRefresh.setTag("Refresh On-Hold Order");
		}
		
		btnRefresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				disableView(btnRefresh);
				disableView(btnOrderPreviewContinue);
				
				if(v.getTag().toString().equalsIgnoreCase("Refresh On-Hold Order"))
				{
					if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
					{
						showLoader("Please wait...");
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								uploadData(AppStatus.POST_ORDER, AppStatus.TODAY_DATA);
								final int isHoldeOrderAproved = getHoldOrderStatus(orderDO.OrderId, orderDO.strUUID);
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										hideLoader();
										if(isHoldeOrderAproved == 10)
											showCustomDialog(SalesmanOrderPreview.this, "Warning!", "Your On-Hold order is approved.", "OK", null, "approved");
										else if(isHoldeOrderAproved == -10)
											showCustomDialog(SalesmanOrderPreview.this, "Warning!", "Your On-Hold order is rejected.", "OK", null, "rejected");
										else
											showCustomDialog(SalesmanOrderPreview.this, "Warning!", "This order is in On-Hold status, Please wait for Finance Manager clearance.", "OK", null, "");
									}
								});
							}
						}).start();
					}
				}
				else 
					if(v.getTag().toString().equalsIgnoreCase("Confirm On-Hold Order")){
						if(!isCustomerSigned)
						{
							if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
								showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
							
							else
								showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
						}
						
						else if(!isPresellerSigned)
							showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please sign before confirming the order.", getString(R.string.OK), null, "scroll");
						else{
							//pre-seller signature
							Bitmap bitmap = getBitmap(presellerSignature);
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
							orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
							storeImage(bitmap, AppConstants.SALESMAN_SIGN);
							
							//customer signature
							Bitmap image = getBitmap(customerSignature);
							ByteArrayOutputStream streams = new ByteArrayOutputStream();
							image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
							orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
							storeImage(image, AppConstants.CUSTOMER_SIGN);
							completOnHoldOrder();
							
						}
						
					}
					else
						completOnHoldOrder();
			}
		});
		
		if(orderDO != null)
			tvHeaderPreview.setText(getString(R.string.Preview_Order) +" ("+orderDO.OrderId+")");
		
		tvTrxTypeNameV.setText(mallsDetails.Order_Type_Name);
		
		etLPO.setFilters(new InputFilter[] { filter });
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		 Calendar c 	= 	Calendar.getInstance();
		 c.add(Calendar.DAY_OF_MONTH, 1);
		 int cyear 	= 	c.get(Calendar.YEAR);
		 int cmonth 	= 	c.get(Calendar.MONTH);
		 int cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		 return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	}
	
	/** method for dateofJorney picker **/
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	{
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar currentCal = Calendar.getInstance();
			  currentCal.add(Calendar.HOUR_OF_DAY, 1);
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
		    	
			  if(!tempCal.before(currentCal))
			  {
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
					  
					  String date = year+"-"+strMonth+"-"+strDate;
					  
					  ((TextView)tempView).setTag(date);
					  ((TextView)tempView).setText(CalendarUtils.getFormatedDatefromString(date));
				  }
			  }
			  else
				  showCustomDialog(SalesmanOrderPreview.this, getResources().getString(R.string.warning), "Delivery date should be greater than current date.", getResources().getString(R.string.OK), null, "");
		  }
	};
	private void performCashOrder()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(orderId!=null && !orderId.equalsIgnoreCase("")){
					new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+totalInvoicedPrice, orderId, AppConstants.SALES_INV_CODE);
					final String LPO = etLPO.getText().toString();
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							registerBroadCast();
							hideLoader();
							float availebleLimit  = StringUtils.getFloat(customerLimit.availbleLimit);
							double fToPay  = totalInvoicedPrice - availebleLimit;
							Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("AR", false);
							intent.putExtra("fToPay", fToPay);
							intent.putExtra("OrderId", orderId);
							intent.putExtra("LPO", LPO);
							intent.putExtra("roundOffVal", roundOff);
							intent.putExtra("orderDO", orderDO);
							startActivityForResult(intent, AppStatus.PAYMENT_DONE);
						}
					});
				}else{
					hideLoader();
					showCustomDialog(SalesmanOrderPreview.this, getString(R.string.warning), "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");	
				}
				
			}
		}).start();
	}
	
	private void showRoundoffPopup()
	{
		LinearLayout view 				= 	(LinearLayout) inflater.inflate(R.layout.rounded_off_popup, null);
		final CustomDialog customDialog = 	new CustomDialog(SalesmanOrderPreview.this, view, preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
		final Button btnCancel 			= 	(Button) view.findViewById(R.id.btnCancel);
		final EditText etEnterValue 	= 	(EditText) view.findViewById(R.id.etEnterValue);
		final TextView tvInvoiceAmt 	= 	(TextView) view.findViewById(R.id.tvInvoiceAmt);
		final TextView tvFinalInvoiceAmt= 	(TextView) view.findViewById(R.id.tvFinalInvoiceAmt);
		final TextView tvSelectSymbol	= 	(TextView) view.findViewById(R.id.tvSelectSymbol);
		
		setvalues(tvSelectSymbol);
		customDialog.setCancelable(false);
		
		float amountToShow 	= 	StringUtils.getFloat(strSymbol+roundOff);
		double actAMT 		=	 totalInvoicedPrice + amountToShow;
		
		tvInvoiceAmt.setText(""+curencyCode+" "+totalInvoicedPrice);
		tvFinalInvoiceAmt.setText(""+curencyCode+" "+diffAmt.format(actAMT));
		tvFinalInvoiceAmt.setTag(actAMT);
		
		setTypeFace(view);
		
		btnOkPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				float amt       = StringUtils.getFloat(tvFinalInvoiceAmt.getTag().toString());
				float mRoundOff = StringUtils.getFloat(tvSelectSymbol.getText().toString() + etEnterValue.getText().toString());
				
				if(amt == 0 || totalInvoicedPrice == amt)
					showCustomDialog(SalesmanOrderPreview.this, getString(R.string.warning), "Please enter value.", "OK", null, "");
				else if(mRoundOff >= .5 || mRoundOff <= -.5 || !validateAmount(totalInvoicedPrice, amt))
					showCustomDialog(SalesmanOrderPreview.this, getString(R.string.warning), "Enter Appropriate amount to round off the Invoice amount.", "OK", null, "");
				else
				{
					if(customDialog != null)
						customDialog.dismiss();
					
					totalInvoicedPrice = amt;
					roundOff           = mRoundOff;
					performOrderInsertAction();
				}
			}
		});
		
		tvSelectSymbol.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(SalesmanOrderPreview.this);
				builder.setSingleChoiceItems(arrSymbol, StringUtils.getInt(tvSelectSymbol.getTag().toString()), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						strSymbol = arrSymbol[which];
						tvSelectSymbol.setTag(which);
						tvSelectSymbol.setText(strSymbol);
						dialog.dismiss();
						etEnterValue.setText("");
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
				mCustomDialog = customDialog;
				showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Are you sure you want to discard entered/selected values?", "Yes", "No", "discard");
			}
		});
		
		if(roundOff != 0)
			etEnterValue.setText(""+roundOff);
		
		etEnterValue.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(isToSetText)
				{
					if(s == null)
						s = "";
					String amt 	   = s.toString();
					
					if(!TextUtils.isEmpty(amt) && !amt.startsWith("."))
					{
						if(amt.contains("."));
						else if(!amt.contains("."))
							amt = "."+amt;
					}
					
					float mRoundOff = StringUtils.getFloat(amt);
					float val       = StringUtils.getFloat(tvSelectSymbol.getText().toString() + amt);
					double finalVal  = totalInvoicedPrice + val;
					tvFinalInvoiceAmt.setTag(finalVal);
					tvFinalInvoiceAmt.setText(""+curencyCode+" "+/*diffAmt.format(*/finalVal);
					isToSetText = false;
					etEnterValue.setText("");
					isToSetText = false;
					etEnterValue.append(amt);
					isToSetText = true;

					if(!validateAmount(totalInvoicedPrice, finalVal))
						etEnterValue.setError("Enter Appropriate amount to round off the Invoice amount.");
					else
						etEnterValue.setError(null);

//					if(mRoundOff >= .5 || mRoundOff <= -.5 || !validateAmount(totalInvoicedPrice, finalVal))
//						etEnterValue.setError("Enter Appropriate amount to round off the Invoice amount.");
//					else
//						etEnterValue.setError(null);
				}
				else
					isToSetText = true;
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
		
		if (!customDialog.isShowing())
			customDialog.show();
	}
	
	private void setvalues(TextView tvSelectSymbol) 
	{
		if(roundOff == 0)
		{
			float val = StringUtils.getFloat(""+(totalInvoicedPrice % 1));
			if(val >= .5)
			{
				strSymbol 	= "+";
				val 		= 1-val;
				HEIGH_RANGE = totalInvoicedPrice + val;
				LOW_RANGE	= HEIGH_RANGE - .5f;
				tvSelectSymbol.setTag(0);
			}
			else
			{
				strSymbol 	= "-";
				val 		= -val;
				LOW_RANGE   = totalInvoicedPrice + val;
				HEIGH_RANGE = .5f + LOW_RANGE;
				tvSelectSymbol.setTag(1);
			}
			
			roundOff 		= 	StringUtils.getFloat(""+Math.abs(val));
			tvSelectSymbol.setText(strSymbol);
		}
		else
		{
			if(strSymbol.equalsIgnoreCase("-") || (""+roundOff).startsWith("-"))
			{
				strSymbol = "-";
				tvSelectSymbol.setTag(1);
			}
			else
			{
				strSymbol = "+";
				tvSelectSymbol.setTag(0);
			}
			
			tvSelectSymbol.setText(strSymbol);
			roundOff  = StringUtils.getFloat(""+(Math.abs(roundOff)));
		}		
	}

//	private boolean validateAmount(double actAmt, double finalAmt)
//	{
//		boolean isValid = true;
//
//		if(Math.abs(actAmt - finalAmt) > .5)
//			isValid = false;
//		else if(LOW_RANGE > finalAmt || HEIGH_RANGE < finalAmt)
//			isValid = false;
//
//		return isValid;
//	}

	private boolean validateAmount(double actAmt, double finalAmt)
	{
		boolean isValid = false;

		if(LOW_RANGE == finalAmt || HEIGH_RANGE == finalAmt)
			isValid = true;

		return isValid;
	}
	@Override
	protected void onResume() 
	{
		setCreditLimit();
		super.onResume();
	}
	
	private void setCreditLimit()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails != null && isCreditCustomer(mallsDetails))
					customerLimit   	= 	customerDA.getCustomerCreditLimit(mallsDetails);
				
				if(mallsDetails != null && isCreditCustomer(mallsDetails))
					overDue = customerDA.getOverDueAmount(mallsDetails);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
					}
				});
			}
		}).start();
	}
	
	public void postLPOOrder(final OrderDO orderDO)
	{
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.TRXStatus		=	AppStatus.TRX_STATUS_DELIVRED;
				orderDO.salesmanCode	= 	mallsDetails.salesmanCode+"";
				orderDO.vehicleNo		= 	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				orderDO.LPOStatus		= 	AppStatus.LPO_STATUS_DELIVERED+"";
				
				//pre-seller signature
				Bitmap bitmap = getBitmap(presellerSignature);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
				storeImage(bitmap, AppConstants.SALESMAN_SIGN);
				
				//customer signature
				Bitmap image = getBitmap(customerSignature);
				ByteArrayOutputStream streams = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
				orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
				storeImage(image, AppConstants.CUSTOMER_SIGN);
				
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					isSalesOrderGerenaterd = true;
					
					new OrderDA().updateLPOPushStatus(orderDO);
					
					new OrderDA().updateInventoryStatus_New(vecMainProducts, CalendarUtils.getOrderPostDate());
					
					if(mallsDetails.customerType!= null && !mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
						new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+totalInvoicedPrice, orderDO.OrderId, AppConstants.SALES_INV_CODE);
					
					customerDA.updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
				}
				else
					isSalesOrderGerenaterd = false;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isSalesOrderGerenaterd)
						{
							btnPrintSalesOrder.setVisibility(View.VISIBLE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
							showOrderCompletePopup();
							uploadData(AppStatus.UPDATE_LPO_ORDER, AppStatus.TODAY_DATA);
						}
						else
							showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
	}
	
	public void createLPOOrder()
	{
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails.currencyCode == null || mallsDetails.currencyCode.length() <= 0)
					mallsDetails.currencyCode = curencyCode;
				
				//getting all the values for Order Table
				orderDO 				= 	new OrderDO();
				
				orderDO.BalanceAmount 	= 	totalAmount;
				orderDO.CustomerSiteId 	= 	mallsDetails.site;
				orderDO.strCustomerName	= 	mallsDetails.siteName;
				orderDO.DeliveryAgentId = 	"1";
				orderDO.DeliveryStatus 	= 	"E";
				orderDO.Discount 		= 	discount;
				orderDO.InvoiceDate 	= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				orderDO.PresellerId 	= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.TotalAmount 	= 	totalInvoicedPriceWithoutVatAmt; //totalInvoicedPrice
				orderDO.orderType		= 	AppConstants.HHOrder;
				orderDO.DeliveryDate 	= 	tvDeliveryDateValue.getTag().toString();
				orderDO.strUUID			= 	StringUtils.getUniqueUUID();
				orderDO.orderSubType	=	AppConstants.LPO_ORDER;	
				orderDO.strCustomerPriceKey	=  mallsDetails.priceList;
				orderDO.pushStatus		=	0;
				orderDO.message			=	"";
				
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.CurrencyCode	=	mallsDetails.currencyCode+"";
				orderDO.PaymentType		=	""+mallsDetails.paymentType;
//				orderDO.PaymentCode		=	""+mallsDetails.paymentCode;
				orderDO.TrxReasonCode	=	"";
				orderDO.LPOCode			=	""+etLPO.getText().toString();
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"";
				orderDO.roundOffVal		=	 roundOff;
				orderDO.LPOStatus		=	 AppStatus.LPO_STATUS_CREATED+"";
				//===========================================Added for TAX======================
				orderDO.VatAmount 				= 	VatAmount;
				orderDO.TotalAmountWithVat 		= 	TotalAmountWithVat;
				orderDO.ProrataTaxAmount 		= 	ProrataTaxAmount;
				orderDO.TotalTax 				= 	VatAmount;


				orderDO.pushStatus		=	0;
				orderDO.TRXStatus		=	AppStatus.TRX_STATUS_ADVANCE;
			
				orderDO.TotalTaxAmt				= 	0+"";
				orderDO.salesmanCode			= 	"";//mallsDetails.salesmanCode+"";
				orderDO.vehicleNo				= 	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
				orderDO.TRANSACTION_TYPE_KEY	= 	AppConstants.IBS_Sales_K;
				orderDO.TRANSACTION_TYPE_VALUE	= 	AppConstants.IBS_Sales_V;
				
				orderDO.Batch_Source_Name		= 	tvBatchSourceNameV.getText().toString();
				orderDO.Trx_Type_Name			= 	tvTrxTypeNameV.getText().toString();
				
				orderDO.SourceVehicleCode		= 	lpoVehicle;
				
				try
				{
					//pre-seller signature
					Bitmap bitmap = getBitmap(presellerSignature);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
					storeImage(bitmap, AppConstants.SALESMAN_SIGN);
					
					//customer signature
					Bitmap image = getBitmap(customerSignature);
					ByteArrayOutputStream streams = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
					orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
					storeImage(image, AppConstants.CUSTOMER_SIGN);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				showLoader(getString(R.string.please_wait_order_pushing));
				if(vecOfferProducts == null)
					vecOfferProducts = new ArrayList<ProductDO>();
				
				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_Promo(orderDO, vecMainProducts, vecOfferProducts, preference);
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
					isSalesOrderGerenaterd = true;
				else
					isSalesOrderGerenaterd = false;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isSalesOrderGerenaterd)
						{
							btnPrintSalesOrderMerchant.setVisibility(View.GONE);
							btnPrintSalesOrderMerchant.setText(" Capture Image ");
							btnPrintSalesOrder.setVisibility(View.GONE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
							
							tvHeaderPreview.setText(getString(R.string.Preview_Order) +" ("+orderDO.OrderId+")");
							
							if(STATUS == AppStatus.ONHOLD_ORDER)
							{
								btnOrderPreviewContinue.setVisibility(View.GONE);
								btnRefresh.setVisibility(View.VISIBLE);
								showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Your order has been pushed to server. Please wait for Finance Manager clearance.", getString(R.string.OK), null, "EXITONHOLD");
							}
							else
								showOrderCompletePopup();
						
							uploadData(AppStatus.POST_LPO_ORDER, AppStatus.TODAY_DATA);
							
							customerSignature.setTouchable(false);
							presellerSignature.setTouchable(false);
						}
						else
							showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
	}
	
	public void postOrder(final String orderType)
	{
		showLoader(getString(R.string.please_wait));
		
		Thread th=new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				if(mallsDetails.currencyCode == null || mallsDetails.currencyCode.length() <= 0)
					mallsDetails.currencyCode = curencyCode;
				
				//getting all the values for Order Table
				orderDO 				= 	new OrderDO();
				orderDO.OrderId         =   orderId;
				orderDO.BalanceAmount 	= 	totalAmount;
				orderDO.CustomerSiteId 	= 	mallsDetails.site;
				orderDO.strCustomerName	= 	mallsDetails.siteName;
				orderDO.DeliveryAgentId = 	"1";
				orderDO.DeliveryStatus 	= 	"E";
				orderDO.Discount 		= 	discount;
//===========================================Added for TAX======================
				orderDO.VatAmount 				= 	VatAmount;
				orderDO.TotalAmountWithVat 		= 	TotalAmountWithVat;
				orderDO.ProrataTaxAmount 		= 	ProrataTaxAmount;
				orderDO.TotalTax 		= 	VatAmount;

				orderDO.InvoiceDate 	= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				orderDO.PresellerId 	= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.TotalAmount 	= 	totalInvoicedPriceWithoutVatAmt;//========totalInvoicedPrice, Commented As per vishal sir, we need to provide the actual amount wothout tax
				orderDO.orderType		= 	AppConstants.HHOrder;
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.strUUID			= 	strUUId;
				orderDO.orderSubType	=	AppConstants.APPORDER;	
				orderDO.strCustomerPriceKey	=  mallsDetails.priceList;
				orderDO.pushStatus		=	0;
				orderDO.message			=	"";
				
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.CurrencyCode	=	mallsDetails.currencyCode+"";
				orderDO.PaymentType		=	""+mallsDetails.paymentType;
//				orderDO.PaymentCode		=	""+mallsDetails.paymentCode;
				orderDO.TrxReasonCode	=	"";
				orderDO.LPOCode			=	""+etLPO.getText().toString();
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"";
				orderDO.roundOffVal		=	 roundOff;
				
				if(STATUS == AppStatus.ONHOLD_ORDER)
				{
					orderDO.pushStatus		=	0;
					orderDO.TRXStatus		=	AppStatus.TRX_STATUS_HOLD;
				}
				else
				{
					orderDO.pushStatus		=	0;
					orderDO.TRXStatus		=	AppStatus.TRX_STATUS_DELIVRED;
				}
				
				orderDO.TotalTaxAmt				= 	0+"";
				orderDO.salesmanCode			= 	mallsDetails.salesmanCode+"";
				orderDO.vehicleNo				= 	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
				orderDO.TRANSACTION_TYPE_KEY	= 	AppConstants.IBS_Sales_K;
				orderDO.TRANSACTION_TYPE_VALUE	= 	AppConstants.IBS_Sales_V;
				
				orderDO.Batch_Source_Name		= 	tvBatchSourceNameV.getText().toString();
				orderDO.Trx_Type_Name			= 	tvTrxTypeNameV.getText().toString();
				if(isSignatureRequired){
					Bitmap bitmap = getBitmap(presellerSignature);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
					storeImage(bitmap, AppConstants.SALESMAN_SIGN);
					
					//customer signature
					Bitmap image = getBitmap(customerSignature);
					ByteArrayOutputStream streams = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
					orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
					storeImage(image, AppConstants.CUSTOMER_SIGN);
				}
				//pre-seller signature
				
				
				showLoader(getString(R.string.please_wait_order_pushing));
				if(vecOfferProducts == null)
					vecOfferProducts = new ArrayList<ProductDO>();

				/// ****************Vat Precision issue start****************************
				double _TotalTax=0,_invoiceWithoutTax=0,_invoiceAmt=0,_lineTaxAmt=0;
				for(ProductDO productDO : vecMainProducts)
				{
					double l_TotalTax=0,l_invoiceWithoutTax=0,l_invoiceAmt=0,l_lineTaxAmt=0;
					DecimalFormat df = new DecimalFormat("#.###");
					df.setMinimumFractionDigits(3);
					df.setMaximumFractionDigits(3);
					df.setRoundingMode(RoundingMode.CEILING);

					l_TotalTax = productDO.TotalTax;
					l_TotalTax = StringUtils.roundOff(productDO.TotalTax,noOfRoundingOffdigits);
					_TotalTax =  _TotalTax+ l_TotalTax;
//					productDO.TotalTax = (float)l_TotalTax;

					l_invoiceWithoutTax = StringUtils.roundOff(productDO.invoiceAmountwithoutTax,noOfRoundingOffdigits);
//					l_invoiceWithoutTax += /*StringUtils.round(*/productDO.invoiceAmountwithoutTax;
					_invoiceWithoutTax = _invoiceWithoutTax+l_invoiceWithoutTax;
//					productDO.invoiceAmountwithoutTax = (float) l_invoiceWithoutTax;

					l_invoiceAmt =  StringUtils.roundOff(productDO.invoiceAmount,noOfRoundingOffdigits);
//					l_invoiceAmt = /*(float) StringUtils.round(*/productDO.invoiceAmount;
					_invoiceAmt = _invoiceAmt+l_invoiceAmt;
//					productDO.invoiceAmount = (float) l_invoiceAmt;

					l_lineTaxAmt =  StringUtils.roundOff(productDO.LineTaxAmount,noOfRoundingOffdigits);
//					l_lineTaxAmt = /*(float) StringUtils.round(*/productDO.LineTaxAmount;
					_lineTaxAmt =  _lineTaxAmt+l_lineTaxAmt;
//					productDO.LineTaxAmount = (float) l_lineTaxAmt;
				}


//				orderDO.TotalAmount = (float) StringUtils.round(_invoiceWithoutTax,2);
//				orderDO.TotalAmountWithVat = (float) StringUtils.round(_invoiceAmt,2);
//				orderDO.TotalTax  = StringUtils.round(_TotalTax,2);
//				orderDO.VatAmount  = StringUtils.round(_lineTaxAmt,2);

				orderDO.TotalAmount =  StringUtils.roundOff(_invoiceWithoutTax,noOfRoundingOffdigits);
				orderDO.TotalTax  = StringUtils.roundOff(_TotalTax,noOfRoundingOffdigits);
				orderDO.VatAmount  =StringUtils.roundOff(_lineTaxAmt,noOfRoundingOffdigits);
				orderDO.TotalAmountWithVat = StringUtils.roundOff(_invoiceAmt,noOfRoundingOffdigits);
				orderDO.BalanceAmount =  orderDO.TotalAmountWithVat;
				/// ****************Vat Precision issue end****************************

				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_Promo(orderDO, vecMainProducts, vecOfferProducts, preference);
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					isSalesOrderGerenaterd = true;
					
					if(STATUS != AppStatus.ONHOLD_ORDER)
					{
						new OrderDA().updateInventoryStatus_New(vecMainProducts, CalendarUtils.getOrderPostDate());
						new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+orderDO.TotalAmountWithVat, orderDO.OrderId, AppConstants.SALES_INV_CODE);
						customerDA.updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
					}
				}
				else
					isSalesOrderGerenaterd = false;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						hideLoader();	}
				});
			}
		});

		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		if(isSalesOrderGerenaterd)
		{
			btnPrintSalesOrderMerchant.setVisibility(View.GONE);
			btnPrintSalesOrderMerchant.setText(" Capture Image ");
			btnPrintSalesOrder.setVisibility(View.GONE);
			btnOrderPreviewContinue.setVisibility(View.VISIBLE);
			btnFinalize.setVisibility(View.GONE);

			tvHeaderPreview.setText(getString(R.string.Preview_Order) +" ("+orderDO.OrderId+")");

			if(STATUS == AppStatus.ONHOLD_ORDER)
			{
				btnOrderPreviewContinue.setVisibility(View.GONE);
				btnRefresh.setVisibility(View.VISIBLE);
				showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Your order has been pushed to server. Please wait for Finance Manager clearance.", getString(R.string.OK), null, "EXITONHOLD");
			}
			else
				showOrderCompletePopup();

			uploadData(AppStatus.POST_ORDER, AppStatus.TODAY_DATA);

			customerSignature.setTouchable(false);
			presellerSignature.setTouchable(false);
		}
		else
			showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");


	}
	
	private void prepareCashSalesOrder(final String orderType)
	{
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails.currencyCode == null || mallsDetails.currencyCode.length() <= 0)
					mallsDetails.currencyCode = curencyCode;
				
				//getting all the values for Order Table
				orderDO 				= 	new OrderDO();
				
				orderDO.OrderId         =   orderId; 
				orderDO.BalanceAmount 	= 	totalAmount;
				orderDO.CustomerSiteId 	= 	mallsDetails.site;
				orderDO.strCustomerName	= 	mallsDetails.siteName;
				orderDO.DeliveryAgentId = 	"1";
				orderDO.DeliveryStatus 	= 	"E";
				orderDO.Discount 		= 	discount;
				orderDO.InvoiceDate 	= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				orderDO.PresellerId 	= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.TotalAmount 	= 	totalInvoicedPriceWithoutVatAmt;// totalInvoicedPrice
				orderDO.orderType		= 	AppConstants.HHOrder;
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.strUUID			= 	strUUId;
				orderDO.orderSubType	=	AppConstants.APPORDER;	
				orderDO.strCustomerPriceKey	=  mallsDetails.priceList;
				orderDO.pushStatus		=	0;
				orderDO.message			=	"";
				//===============================VAT ====================
				orderDO.VatAmount 				= 	VatAmount;
				orderDO.TotalAmountWithVat 		= 	TotalAmountWithVat;
				orderDO.ProrataTaxAmount 		= 	ProrataTaxAmount;
				orderDO.TotalTax 		= 	VatAmount;
				//===============================VAT ====================
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.CurrencyCode	=	curencyCode+"";
				orderDO.PaymentType		=	""+mallsDetails.paymentType;
				orderDO.TrxReasonCode	=	"";
				orderDO.LPOCode			=	""+etLPO.getText().toString();;
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"";
				
				orderDO.pushStatus		=	0;
				orderDO.TRXStatus		=	AppStatus.TRX_STATUS_DELIVRED;
				orderDO.roundOffVal		=	roundOff;
				
				orderDO.TotalTaxAmt		= 	0+"";
				orderDO.salesmanCode	= 	mallsDetails.salesmanCode+"";
				orderDO.vehicleNo		= 	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
				orderDO.TRANSACTION_TYPE_KEY		= 	AppConstants.IBS_Sales_K;
				orderDO.TRANSACTION_TYPE_VALUE		= 	AppConstants.IBS_Sales_V;
				
				orderDO.Batch_Source_Name= 	tvBatchSourceNameV.getText().toString();
				orderDO.Trx_Type_Name	 = 	tvTrxTypeNameV.getText().toString();
				
				//pre-seller signature
				Bitmap bitmap = getBitmap(presellerSignature);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
				storeImage(bitmap, AppConstants.SALESMAN_SIGN);
				
				//customer signature
				Bitmap image = getBitmap(customerSignature);
				ByteArrayOutputStream streams = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
				orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
				storeImage(image, AppConstants.CUSTOMER_SIGN);
				double _TotalTax=0,_invoiceWithoutTax=0,_invoiceAmt=0,_lineTaxAmt=0;
				for(ProductDO productDO : vecMainProducts)
				{
					double l_TotalTax=0,l_invoiceWithoutTax=0,l_invoiceAmt=0,l_lineTaxAmt=0;
					DecimalFormat df = new DecimalFormat("#.###");
					df.setMinimumFractionDigits(3);
					df.setMaximumFractionDigits(3);
					df.setRoundingMode(RoundingMode.CEILING);

					l_TotalTax = productDO.TotalTax;
					l_TotalTax = StringUtils.roundOff(productDO.TotalTax,noOfRoundingOffdigits);
					_TotalTax =  _TotalTax+ l_TotalTax;
//					productDO.TotalTax = (float)l_TotalTax;

					l_invoiceWithoutTax = StringUtils.roundOff(productDO.invoiceAmountwithoutTax,noOfRoundingOffdigits);
//					l_invoiceWithoutTax += /*StringUtils.round(*/productDO.invoiceAmountwithoutTax;
					_invoiceWithoutTax = _invoiceWithoutTax+l_invoiceWithoutTax;
//					productDO.invoiceAmountwithoutTax = (float) l_invoiceWithoutTax;

					l_invoiceAmt =  StringUtils.roundOff(productDO.invoiceAmount,noOfRoundingOffdigits);
//					l_invoiceAmt = /*(float) StringUtils.round(*/productDO.invoiceAmount;
					_invoiceAmt = _invoiceAmt+l_invoiceAmt;
//					productDO.invoiceAmount = (float) l_invoiceAmt;

					l_lineTaxAmt =  StringUtils.roundOff(productDO.LineTaxAmount,noOfRoundingOffdigits);
//					l_lineTaxAmt = /*(float) StringUtils.round(*/productDO.LineTaxAmount;
					_lineTaxAmt =  _lineTaxAmt+l_lineTaxAmt;
//					productDO.LineTaxAmount = (float) l_lineTaxAmt;
				}


//				orderDO.TotalAmount = (float) StringUtils.round(_invoiceWithoutTax,2);
//				orderDO.TotalAmountWithVat = (float) StringUtils.round(_invoiceAmt,2);
//				orderDO.TotalTax  = StringUtils.round(_TotalTax,2);
//				orderDO.VatAmount  = StringUtils.round(_lineTaxAmt,2);

				orderDO.TotalAmount =  StringUtils.roundOff(_invoiceWithoutTax,noOfRoundingOffdigits);
				orderDO.TotalTax  = StringUtils.roundOff(_TotalTax,noOfRoundingOffdigits);
				orderDO.VatAmount  =StringUtils.roundOff(_lineTaxAmt,noOfRoundingOffdigits);
				orderDO.TotalAmountWithVat = StringUtils.roundOff(_invoiceAmt,noOfRoundingOffdigits);
				orderDO.BalanceAmount =  orderDO.TotalAmountWithVat;
				/// ****************Vat Precision issue end****************************


				showLoader(getString(R.string.please_wait_order_pushing));
				if(vecOfferProducts == null)
					vecOfferProducts = new ArrayList<ProductDO>();
				
				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_Promo(orderDO, vecMainProducts, vecOfferProducts, preference);
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					isSalesOrderGerenaterd = true;
					
					new OrderDA().updateInventoryStatus_New(vecMainProducts, CalendarUtils.getOrderPostDate());
					
					customerDA.updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
				}
				else
					isSalesOrderGerenaterd = false;

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();

						if(isSalesOrderGerenaterd)
						{
							tvHeaderPreview.setText(getString(R.string.Preview_Order) +" ("+orderDO.OrderId+")");

							btnPrintSalesOrderMerchant.setVisibility(View.GONE);
							btnPrintSalesOrderMerchant.setText(" Capture Image ");
							btnPrintSalesOrder.setVisibility(View.GONE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
							customerSignature.setTouchable(false);
							presellerSignature.setTouchable(false);

							btnOrderPreviewContinue.performClick();
						}
						else
							showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
	}
	
	private void showVisibleButton(final boolean isVisible)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isVisible)
				{
					btnOrderPreviewContinue.setVisibility(View.VISIBLE);
					btnFinalize.setVisibility(View.GONE);
				}
				else
				{
					btnOrderPreviewContinue.setVisibility(View.GONE);
					btnFinalize.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private void InitializeControls() 
	{
		llMiddleLayout 	    		= 	(LinearLayout)llOrderPreview.findViewById(R.id.llOrderPreviewMidle);
		tvHeaderPreview 			= 	(TextView)llOrderPreview.findViewById(R.id.tvOrderPreviewHeader);
		btnFinalize	 				= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewFinalize);
		btnPrintSalesOrder			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrder);
		btnPrintMerchant			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
		btnOrderPreviewContinue		= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewContinue);
		btnPrintSalesOrderMerchant	= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
		tvlanguage					= 	(TextView)llOrderPreview.findViewById(R.id.tvlanguage);
		tvLu						= 	(TextView)llOrderPreview.findViewById(R.id.tvLu);
		
		btnRefresh					= 	(Button)llOrderPreview.findViewById(R.id.btnRefresh);
		
		//Masafi logo Layout as header of list view
		llHeaderLayout      		= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header, null);
//		llHeaderLayout   			= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_ar, null);

		tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
		tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
		tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
		TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
		TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
		TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
		TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
		 tvAddressPreview			= (TextView)llHeaderLayout.findViewById(R.id.tvAddressPreview);
		tvMasafiLogoTitle			= (TextView)llHeaderLayout.findViewById(R.id.tvMasafiLogoTitle);
		
		//Signature Layout as footer of list view
		llEsignature 				= (LinearLayout)getLayoutInflater().inflate(R.layout.esignature_order_preview, null);
		llPresellerSign 			= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature_e);
		llCustomerSign 				= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature_e);
		tvPresellerSign 			= (TextView)llEsignature.findViewById(R.id.tvPresellerSignature_e);
		tvCustomerSign    			= (TextView)llEsignature.findViewById(R.id.tvCustomerSignature_e);
		tvCustomerPasscode			= (TextView)llEsignature.findViewById(R.id.tvCustomerPasscode_e);
		etLPO						= (EditText)llEsignature.findViewById(R.id.etCustomerPasscode_e);
		tvDeliveryDate				= (TextView)llEsignature.findViewById(R.id.tvDeliveryDate);
		tvDeliveryDateValue			= (TextView)llEsignature.findViewById(R.id.tvDeliveryDateValue);
		tvDeliveryToTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTimeValue);
		tvDeliveryFromTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTimeValue);
		tvDeliveryToTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTime);
		tvDeliveryFromTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTime);
		tvOrderActualAmount			= (TextView)llEsignature.findViewById(R.id.tvOrderActualAmount);
		tvOrderTaxAmt			= (TextView)llEsignature.findViewById(R.id.tvOrderTaxAmt);//=======Added For TAX===========
		tvTotalAmount				= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmount);
		tvTotalAmountValue 			= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmountValue);
		tvOrderPreviewPriceValue	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewPriceValue);
		tvOrderPreviewFooterCases	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterCases);
		tvOrderPreviewFooterunits	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterunits);
		
		llPresellerSignature	= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature);
		llCustomerSignature		= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature);
		if(isSignatureRequired){
			llPresellerSignature.setVisibility(View.VISIBLE);
			llCustomerSignature.setVisibility(View.VISIBLE);
		}else{
			llPresellerSignature.setVisibility(View.GONE);
			llCustomerSignature.setVisibility(View.GONE);
		}
		
		tvImage						= (TextView)llEsignature.findViewById(R.id.tvImage);
		ivStampImage				= (ImageView)llEsignature.findViewById(R.id.ivImage);
		llTotalPrice	= (LinearLayout)llEsignature.findViewById(R.id.llTotalPrice);
		
		llTrxTypeName = (LinearLayout)llEsignature.findViewById(R.id.llTrxTypeName);
		llCNReason = (LinearLayout)llEsignature.findViewById(R.id.llCNReason);
		tvTrxTypeName= (TextView)llEsignature.findViewById(R.id.tvTrxTypeName);
		tvTrxTypeNameV= (TextView)llEsignature.findViewById(R.id.tvTrxTypeNameV);
		etCNReason= (EditText)llEsignature.findViewById(R.id.etCNReason);

		tvBatchSourceName= (TextView)llEsignature.findViewById(R.id.tvBatchSourceName);
		tvBatchSourceNameV = (TextView)llEsignature.findViewById(R.id.tvBatchSourceNameV);
		
		tvTotalItemAmt				= (TextView)llEsignature.findViewById(R.id.tvTotalItemAmt);
		tvDscount					= (TextView)llEsignature.findViewById(R.id.tvDscount);
		tvTotalInvoiceAmt			= (TextView)llEsignature.findViewById(R.id.tvTotalInvoiceAmt);
		
		tvTotalItemAmtValue		= (TextView)llEsignature.findViewById(R.id.tvTotalItemAmtValue);
		tvDscountValue			= (TextView)llEsignature.findViewById(R.id.tvDscountValue);
		tvTaxVal				= (TextView)llEsignature.findViewById(R.id.tvTaxVal);
		tvTotalInvoiceAmtValue	= (TextView)llEsignature.findViewById(R.id.tvTotalInvoiceAmtValue);
		
		btnCustomerSignClear		= (Button)llEsignature.findViewById(R.id.btnCustomerSignClear);
		btnPresellerSignClear		= (Button)llEsignature.findViewById(R.id.btnPresellerSignClear);
		LinearLayout llCustomer_Passcode	= (LinearLayout)llEsignature.findViewById(R.id.llCustomer_Passcode);
		LinearLayout llDeliveryDate			= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDate);
		LinearLayout llDeliveryDeliveryTime	= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDeliveryTime);
		
		llRoundOff	= (LinearLayout)llEsignature.findViewById(R.id.llRoundOff);
		tvRoundOff	= (TextView)llEsignature.findViewById(R.id.tvRoundOff);
		tvRoundOffValue = (TextView)llEsignature.findViewById(R.id.tvRoundOffValue);
		
		
		TextView tvBottomDist			= (TextView)llEsignature.findViewById(R.id.tvBottomDist);
		
		if(isLPOOrder)
			llDeliveryDate.setVisibility(View.VISIBLE);
		else
			llDeliveryDate.setVisibility(View.GONE);
		
		if(orderDO == null)
			llCustomer_Passcode.setVisibility(View.VISIBLE);
		
		llDeliveryDeliveryTime.setVisibility(View.GONE);
		tvBottomDist.setVisibility(View.INVISIBLE);
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			tvDiscount.setVisibility(View.GONE);
			tvInvoiceAmount.setVisibility(View.GONE);
			tvPrice.setVisibility(View.GONE);
			tvTotalPrice.setVisibility(View.GONE);
			tvBottomDist.setVisibility(View.GONE);
		}
		
		btnPrintSalesOrderMerchant.setVisibility(View.GONE);
		btnPrintSalesOrder.setVisibility(View.GONE);
		
		showVisibleButton(false);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		tvLu.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
		
		setTypeFace(llEsignature);
		setTypeFace(llMiddleLayout);



		blockMenu();
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("enterPasscode"))
		{
			if(lvPreviewOrder.isScrolled())
			{
				lvPreviewOrder.setScrolled(false);
				lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount()-1).getTop());
				etLPO.requestFocus();
			}
		}
		else if(from.equalsIgnoreCase("Exit"))
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
			sendBroadcast(intentBrObj);
		}
		
		else if(from.equalsIgnoreCase("approved"))
		{
			btnRefresh.setText("Confirm On-Hold Order");
			btnRefresh.setTag("Confirm On-Hold Order");
		}
		else if(from.equalsIgnoreCase("rejected"))
		{
			onButtonYesClick("served");
		}
		else if(from.equalsIgnoreCase("EXITONHOLD"))
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
			sendBroadcast(intentBrObj);
		}
		else if(from.equalsIgnoreCase("onhold"))
		{
			tvTrxTypeNameV.setFocusable(false);
			tvTrxTypeNameV.setFocusableInTouchMode(false);
			tvTrxTypeNameV.setCursorVisible(false);
			tvTrxTypeNameV.setSingleLine(false);
			
			btnCustomerSignClear.setFocusable(false);
			btnCustomerSignClear.setFocusableInTouchMode(false);
			btnCustomerSignClear.setCursorVisible(false);
			btnCustomerSignClear.setSingleLine(false);
			
			btnPresellerSignClear.setFocusable(false);
			btnPresellerSignClear.setFocusableInTouchMode(false);
			
			customerSignature.setFocusable(false);
			customerSignature.setFocusableInTouchMode(false);
			
			presellerSignature.setEnabled(false);
			presellerSignature.setClickable(false);
		}
		else if(from.equalsIgnoreCase("scroll"))
		{
			if(lvPreviewOrder.isScrolled())
			{
				lvPreviewOrder.setScrolled(false);
				lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount()-1).getTop());
			}
		}
		else if(from.equalsIgnoreCase("orderposted"))
		{
			setResult(10000);
			finish();
		}
		else if(from.equalsIgnoreCase("served"))
		{
			if(isLPOOrder)
			{
				setResult(10000);
				finish();
			}
			else
				performCustomerServed();
		}
		else if(from.equalsIgnoreCase("Task"))
		{
			Intent intent = new Intent(SalesmanOrderPreview.this, TaskToDoActivity.class);
			intent.putExtra("object", mallsDetails);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("payment"))
		{
			Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("AR", true);
			if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
				intent.putExtra("isCredit", true);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			Intent intent =	new Intent(SalesmanOrderPreview.this,  SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Capture_Inventory));
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("from", "checkINOption");
			
			if(orderDO != null)
			{
				intent.putExtra("invoiceamt", totalInvoicedPrice);
				intent.putExtra("invoicenum", orderDO.OrderId);
			}
			startActivity(intent);
		}
		
		else if(from.equalsIgnoreCase("Survey"))
		{
			Intent intent =	new Intent(SalesmanOrderPreview.this,  ConsumerBehaviourSurveyActivityNew.class);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
		}
		else if(from.equalsIgnoreCase("exceed_limit"))
		{
			finish();
		}
		else if(from.equalsIgnoreCase("discard"))
		{
			if(mCustomDialog != null)
				mCustomDialog.dismiss();
			
			roundOff  = 0;
			strSymbol = "+";
		}
	}
	
	@Override
	public void onButtonNoClick(String from) 
	{
		if(from.equalsIgnoreCase("payment"))
		{
			showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Do you want to create a Return Request ?", "Yes", "No", "ReturnRequest", false);
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			showCustomDialog(SalesmanOrderPreview.this,getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served",false);
		}
		else if(from.equalsIgnoreCase("emptyjar"))
		{
			showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Do you want to make payment?", "Yes", "No", "payment", false);
		}
		else if(from.equalsIgnoreCase("Capture"))
		{
			orderDO.StampImage = "temp";
			
//			uploadData();
			showCustomDialog(SalesmanOrderPreview.this, getString(R.string.successful), "Order confirmed successfully. Do you want to make payment?", "Yes", "No", "payment", false);
		}
		else if(from.equalsIgnoreCase("exceed_limit"))
		{
			Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("AR", false);
			intent.putExtra("isExceed", true);
			if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
				intent.putExtra("isCredit", true);
			startActivityForResult(intent, 10001);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
		unregisterBroadCast();
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == AppStatus.PAYMENT_DONE && resultCode == AppStatus.PAYMENT_CANCEL)
    	{
    		new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					if(!TextUtils.isEmpty(orderId))
						new CustomerDetailsDA().deletePendingInvoice(orderId);
					
					totalInvoicedPrice  = totalInvoicedPrice - roundOff;
				}
			}).start();
    	}
//    	else if(requestCode == AppStatus.PAYMENT_DONE && resultCode == AppStatus.PAYMENT_DONE)
//    	{
//    		if(!isSalesOrderGerenaterd)
//    		{
//    			if(orderDO == null)
//    				prepareCashSalesOrder(AppConstants.HHOrder);
//    			else
//    				postLPOOrder(orderDO);
//    		}
//    	}
    	/*else if(resultCode == 20000 || resultCode == -20000)
    	{
    		showOrderCompletePopup();
    	}*/
    	else if (requestCode == CAMERA_PIC_REQUEST && mCapturedImageURI != null) 
    	{
    		try
    		{
    			showLoader("Please wait...");
            	new Thread(new Runnable()
            	{
    				@Override
    				public void run()
    				{
    					System.gc();
    		        	String[] projection = { MediaStore.Images.Media.DATA}; 
    		            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null); 
    		            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
    		            cursor.moveToFirst(); 
    		            String capturedImageFilePath = cursor.getString(column_index_data);

    		            LogUtils.infoLog("capturedImageFilePath",""+capturedImageFilePath);
    		            
    		            stampBitmap = decodeFile(new File(capturedImageFilePath), (int)(100 * px), (int)(100 * px));
    		   	        if(stampBitmap != null)
    		   	        {
	    		   	    	
	    		   	    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    		   	    	stampBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    		   	    	
	    					orderDO.StampImage = Base64.encodeBytes(stream.toByteArray());
	    					final boolean isUpdated = new OrderDA().updateStampImage(orderDO);
	    					
	    					final WeakReference<Bitmap> reference = new WeakReference<Bitmap>(stampBitmap);
	    					
	    		   	    	runOnUiThread(new Runnable()
	    		   	    	{
	    						@Override
	    						public void run() 
	    						{
	    							if(isUpdated && reference != null)
	    							{
	    								ivStampImage.setVisibility(View.VISIBLE);
	    					   	    	ivStampImage.setImageBitmap(reference.get());
	    					   	    	tvImage.setVisibility(View.VISIBLE);
	    					   	    	onButtonYesClick("scroll");
	    								showCustomDialog(SalesmanOrderPreview.this, getString(R.string.successful), "Invoice image has been captured successfully.", "OK", null, "", false);
	    							}
	    							else
	    								showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Capturing invoice image has been cancelled.", "OK", "", "printout", false);
	    							
	    							hideLoader();
	    						}
	    					});
    		   	        }
    		   	        else
    		   	        {
    		   	        	hideLoader();
    		   	        	showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Capturing of invoice image has been cancelled.", "OK", "", "printout", false);
    		   	        }
    				}
    			}).start();
			}
    		catch (OutOfMemoryError e)
    		{
    			hideLoader();
    			showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Capturing of invoice image has been cancelled.", "OK", "", "printout", false);
				e.printStackTrace();
			}
    		catch (Exception e)
    		{
    			hideLoader();
    			showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Capturing of invoice image has been cancelled.", "OK", "", "printout", false);
				e.printStackTrace();
			}
    	} 
    	else if(resultCode == 900)
    	{
    		new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					if(!TextUtils.isEmpty(orderId))
						new CustomerDetailsDA().deletePendingInvoice(orderId);
				}
			}).start();
    		
    		setResult(900);
    		finish();
    	}
    }

	public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(stampBitmap != null)
			stampBitmap.recycle();
		
		unregisterBroadCast();
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPosted || isSalesOrderGerenaterd)
		{
			if(STATUS != AppStatus.ONHOLD_ORDER)
				btnOrderPreviewContinue.performClick();
			else
				showCustomDialog(SalesmanOrderPreview.this, "Alert!", "Do you want to exit?", "YES", "NO", "EXITONHOLD");
		}
		else {
			showCustomDialog(SalesmanOrderPreview.this, "Alert!", "Do you want to exit?", "YES", "NO", "EXITONHOLD");
			//super.onBackPressed();
		}
			
	}
	
	public class MyView extends View 
	{
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        private String strFrom = "";
        private boolean isTouchable = true;
        
        public MyView(Context c, String strFrom)
        {
            super(c);
            this.strFrom 	= 	strFrom;
            Display display = 	getWindowManager().getDefaultDisplay(); 
            int width 		= 	display.getWidth();
            int height 		= 	display.getHeight();
            if(mBitmap != null)
            {
            	mBitmap.recycle();
            	mBitmap = null;
            	System.gc();
            }
            
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
        	if(isTouchable)
        		lvPreviewOrder.setScrollable(false);
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
         lvPreviewOrder.setScrollable(true);
         if(strFrom.equalsIgnoreCase("psign"))
        	 isPresellerSigned = true;
         else if(strFrom.equalsIgnoreCase("csign"))
        	 isCustomerSigned = true;
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
                    touch_move(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        	return isTouchable;
       }
        
        public void setTouchable(boolean isTouchable)
        {
        	this.isTouchable = isTouchable;
        }
    }
	
	private void showOrderCompletePopup()
	{

		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanOrderPreview.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);
		
		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1			  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		Button btn_popup_print		 	  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment	  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_done			  = (Button) view.findViewById(R.id.btn_popup_done);
		
		if((mallsDetails.customerType != null 
				&& mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) || isLPOOrder)
			btn_popup_collectpayment.setVisibility(View.GONE);
		
		if(STATUS == AppStatus.ONHOLD_ORDER){
			btn_popup_print.setVisibility(View.GONE);
		}
		
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				//mCustomDialog.dismiss();
				showprintDialog();
				
				//processPrint();
			}
		});
		
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				//mCustomDialog.dismiss();
				onButtonYesClick("payment");
			}
		});
		
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				
				mCustomDialog.dismiss(); 
				orderPostlog(orderDO.OrderId);
				if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && totalInvoicedPrice > 0)
				{
					if(!orderDO.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER) && !new PaymentDetailDA().isPaymentDone(orderDO.OrderId))
						onButtonYesClick("payment");
					else
						onButtonYesClick("served");
				}
				else if(STATUS == AppStatus.ONHOLD_ORDER)
				{
					Intent intentBrObj = new Intent();
					intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
					sendBroadcast(intentBrObj);
				}
				else
					onButtonYesClick("served");
			}
		});
		
		setTypeFace((LinearLayout)view);
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	
	protected void showprintDialog() {
		View view = inflater.inflate(R.layout.custom_popup_order_print, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanOrderPreview.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1			  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		Button btn_popup_print		 	  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_done			  = (Button) view.findViewById(R.id.btn_popup_done);
		
		ImageView  ivCheck =(ImageView) view.findViewById(R.id.ivCheck);
		
		
		Button btn_dot_popup_print		 	  = (Button) view.findViewById(R.id.btn_dot_popup_print);
		
		
		tv_poptitle.setVisibility(View.GONE);
		ivCheck.setVisibility(View.GONE);
		
		//tv_poptitle1.setText("PRINT");
		tv_poptitle1.setText("PRINT INVOICE");
		
		if(STATUS == AppStatus.ONHOLD_ORDER){
			btn_popup_print.setVisibility(View.GONE);
		     btn_dot_popup_print.setVisibility(View.GONE);
		}
		
		btn_dot_popup_print.setText("Dot Matrix");
		btn_dot_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
         		dotMatrixPrint();
			}
		  });
		
		btn_popup_print.setText("Normal");
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				processPrint();
			}
		});
		
		
		btn_popup_done.setText("Cancel");
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				
			}
		});
		
		setTypeFace((LinearLayout)view);
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
		
	}

	private void completOnHoldOrder()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				STATUS = AppStatus.DEFAULT;
				OrderDA orderDA=new OrderDA();
				orderDA.updateInventoryStatus_New(vecMainProducts, CalendarUtils.getOrderPostDate());
				//orderDA.updateHoldOrderPushStatus(orderDO.OrderId, 10);
				orderDA.updateHoldOrderPushStatus(orderDO, 10);
				
				new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+totalInvoicedPrice, orderDO.OrderId, AppConstants.SALES_INV_CODE);
				
				customerDA.updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						showOrderCompletePopup();
						
						btnOrderPreviewContinue.setVisibility(View.VISIBLE);
						btnRefresh.setVisibility(View.GONE);
						uploadData(AppStatus.POST_COMPETET_ONHOLD_ORDER, AppStatus.TODAY_DATA);
					}
				});
			}
		}).start();
	}
	
	private void registerBroadCast()
	{
		try 
		{
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstants.ACTION_CASH_SALES);
			registerReceiver(performCashSales, filter);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private void unregisterBroadCast()
	{
		try 
		{
			unregisterReceiver(performCashSales);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	BroadcastReceiver performCashSales = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(!isSalesOrderGerenaterd)
			{
				if(orderDO == null)
					prepareCashSalesOrder(AppConstants.HHOrder);
				else
					postLPOOrder(orderDO);
			}
		}
	};
	
	private PaymentHeaderDO paymentHeaderDO;
	private void processPrint()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
					paymentHeaderDO = new PaymentDetailDA().getPaymentDetailByInvoiceNumber(orderDO.OrderId);	
				
				final boolean enablePrintUnits = new SettingsDA().getSettingsValue(AppConstants.EnablePrintUnits);

				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						
						if(enablePrintUnits && vecMainProducts!=null && vecMainProducts.size()>0){
							for(ProductDO productDO : vecMainProducts){
								productDO.units = productDO.preUnits;
								productDO.QuantityInStock = StringUtils.getFloat(productDO.preUnits);
							}
						}
						if(paymentHeaderDO != null)
						{
							Intent intent = new Intent(SalesmanOrderPreview.this, WoosimPrinterActivity.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT);
							intent.putExtra("totalAmount", totalPrice);
							intent.putExtra("strReceiptNo", paymentHeaderDO.ReceiptId);
							intent.putExtra("selectedAmount", totalPrice);
							intent.putExtra("paymentHeaderDO", paymentHeaderDO);
							addArabicDetails(mallsDetails);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("OrderId", orderDO.OrderId);
							intent.putExtra("OrderDo", orderDO);
							intent.putExtra("roundOffVal", orderDO.roundOffVal);
							intent.putExtra("LPO", orderDO.LPOCode);
							addArabicDetails(mallsDetails);
							startActivityForResult(intent, 1000);
						}
						else
						{
							Intent intent = new Intent(SalesmanOrderPreview.this, WoosimPrinterActivity.class);

							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
							intent.putExtra("totalPrice", totalPrice);
							intent.putExtra("OrderId",orderDO.OrderId);
							intent.putExtra("LPO",orderDO.LPOCode);
							addArabicDetails(mallsDetails);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("roundOffVal", orderDO.roundOffVal);

							//newly added
//							intent.putExtra("CALLFROM", CONSTANTOBJ.ORDER_SUMMARY);
//							intent.putExtra("orderDo", orderDO);
//							intent.putExtra("copy", "Invoice Preview");
							//startActivityForResult(intent, 1000);
							startActivity(intent);
						}
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void dotMatrixPrint()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
					paymentHeaderDO = new PaymentDetailDA().getPaymentDetailByInvoiceNumber(orderDO.OrderId);	
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(paymentHeaderDO != null)
						{
//							Intent intent = new Intent(SalesmanOrderPreview.this, PrinterConnector.class);
							Intent intent = new Intent(SalesmanOrderPreview.this, PrinterConnectorArabic.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT);
							intent.putExtra("totalAmount", totalPrice);
							intent.putExtra("strReceiptNo", paymentHeaderDO.ReceiptId);
							intent.putExtra("selectedAmount", totalPrice);
							intent.putExtra("paymentHeaderDO", paymentHeaderDO);
							addArabicDetails(mallsDetails);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("OrderId", orderDO.OrderId);
							intent.putExtra("LPO", orderDO.LPOCode);
							intent.putExtra("roundOffVal", orderDO.roundOffVal);
							addArabicDetails(mallsDetails);
							startActivityForResult(intent, 1000);
						}
						else
						{
//							Intent intent = new Intent(SalesmanOrderPreview.this, PrinterConnector.class);
							Intent intent = new Intent(SalesmanOrderPreview.this, PrinterConnectorArabic.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
							intent.putExtra("totalPrice", totalPrice);
							intent.putExtra("OrderId",orderDO.OrderId);
							intent.putExtra("LPO",orderDO.LPOCode);
							intent.putExtra("roundOffVal", orderDO.roundOffVal);
							addArabicDetails(mallsDetails);
							intent.putExtra("mallsDetails", mallsDetails);

							//newly added
//							intent.putExtra("CALLFROM", CONSTANTOBJ.ORDER_SUMMARY);
//							intent.putExtra("orderDo", orderDO);
//							intent.putExtra("copy", "Invoice Preview");

							startActivityForResult(intent, 1000);

						//	startActivity(intent);
						}
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void performOrderInsertAction()
	{
		if(mallsDetails.customerType!= null 
				&& mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT) 
				&& orderDO == null)
		{
			final int value      = new SettingsDA().getSettingsByName(AppConstants.CREDIT_DAYS_WHILE_ORDER);
			float availebleLimit = StringUtils.getFloat(customerLimit.availbleLimit);
			
			final boolean isExeeded = totalInvoicedPrice > availebleLimit;
//			final boolean isOverDue = (value == AppStatus.ENABLE && overDue > 0);
			
//			if((isExeeded || (isOverDue && mallsDetails.Max_Days_Past_Due <= 0)) && !isLPOOrder && !mallsDetails.IsOverCredit)
			if(isExeeded && !isLPOOrder && !mallsDetails.IsOverCredit)
			{
				creditLimiPopup(mallsDetails, 0, new MakePaymentListner()
				{
					@Override
					public void makePayment(CustomDialog mCustomDialog, int type) 
					{
						if(mCustomDialog != null)
							mCustomDialog.dismiss();
						
						STATUS = type;
						if(type == AppStatus.MODIFY_ORDER)
							finish();
						else if(type == AppStatus.ONHOLD_ORDER)
						{
//							if(isOverDue && mallsDetails.Max_Days_Past_Due > 0)
//								STATUS = AppStatus.DEFAULT;
							generateHoldlog(orderId, totalInvoicedPrice);
							postOrder(AppConstants.HHOrder);
							/*setResult(10000);
							finish();*/
							
							
						}
						else 
							performCashOrder();
					}
				}, isExeeded, false);
			}
			else
			{
				if(isLPOOrder)
					createLPOOrder();
				else
					postOrder(AppConstants.HHOrder);
			}
		}
		else
		{
			if(isLPOOrder)
				createLPOOrder();
			else if(orderDO == null)
				performCashOrder();
			else if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				performCashOrder();
			else
				postLPOOrder(orderDO);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) 
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("orderId", orderId);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		orderId =  savedInstanceState.getString("orderId");
	}
	private void generateHoldlog(final String orderId,final double totalInvoicedPrice){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<SiteCreditLimitDO> arrSiteCredits=customerDA.getCustomerIdCreditLimit(mallsDetails);
				SiteCreditLimitDO siteCreditLimitDO =customerDA.getsiteCreditLimit(mallsDetails);
				String vehicleNO= new VehicleDA().getVehicleNo(preference.getStringFromPreference(preference.EMP_NO, ""));
				LogDO logDO=new LogDO();
				logDO.userId=preference.getStringFromPreference(Preference.EMP_NO,"");
				logDO.type="CreditLimit";
				logDO.key=orderId;
				if(siteCreditLimitDO!=null){
					logDO.data="site :"+siteCreditLimitDO.site+" ,SiteWise_cr_limit:"+siteCreditLimitDO.site_creditLimit
							+", available : "+siteCreditLimitDO.site_availbleLimit
							+", outstanding : "+siteCreditLimitDO.site_outStandingAmount;
				}
				
				logDO.data+="CustomerIdWise";
				for(SiteCreditLimitDO CreditLimitDO:arrSiteCredits){
					logDO.data+="customerID: "+CreditLimitDO.customerID+",SiteId: "+CreditLimitDO.site+",cr_limit:"+CreditLimitDO.site_creditLimit
							+",available : "+CreditLimitDO.site_availbleLimit
							+",outstanding : "+CreditLimitDO.site_outStandingAmount;
				}
			    logDO.data+="InvoiceAmt:"+totalInvoicedPrice;
				logDO.data+="Vehicle: "+vehicleNO;
				logDO.deviceTime=CalendarUtils.getCurrentDateTime();
				boolean isFailed=new CustomerDA().insertLog(logDO);
				if(!isFailed){
					/*runOnUiThread(new Runnable() {
						
						@Override
						public void run() {*/
					if(isNetworkConnectionAvailable(SalesmanOrderPreview.this)){
						
						boolean isUpload=uploadLogData(logDO);	
						if(isUpload){
							new JourneyPlanDA().deleteLog(logDO.logId); 	
						}
					}
						/*}
					});*/
				}
				//uploadData(AppStatus.POST_LOG, AppStatus.TODAY_DATA);
			}
		}).start();
		
	}
	private void orderPostlog(final String orderId){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String dates[]=new JourneyPlanDA().getStartEndtime(preference.getStringFromPreference(Preference.EMP_NO, ""));
				LogDO logDO=new LogDO();
				logDO.userId=preference.getStringFromPreference(Preference.EMP_NO,"");
				logDO.type="Orders";
				logDO.key=orderId;
				logDO.data="trxCode :"+orderId+", dayStart :"+dates[0]+", dayEnd"+dates[1];
				logDO.deviceTime=CalendarUtils.getCurrentDateTime();
				boolean isFailed=new CustomerDA().insertLog(logDO);
				if(!isFailed){
					/*runOnUiThread(new Runnable() {
						
						@Override
						public void run() {*/
					if(isNetworkConnectionAvailable(SalesmanOrderPreview.this)){
						
						boolean isUpload=uploadLogData(logDO);	
						if(isUpload){
							new JourneyPlanDA().deleteLog(logDO.logId); 	
						}
					}
					/*}
					});*/
				}
				//uploadData(AppStatus.POST_LOG, AppStatus.TODAY_DATA);
			}
		}).start();
		
	}
	
}
