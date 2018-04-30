package com.winit.baskinrobbin.salesman;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.adapter.ReplacementPreviewAdapter;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.CustomListView;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;


public class SalesmanReplacementPreview extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llOrderPreview , llMiddleLayout, llEsignature, llCustomerSign, llPresellerSign, llHeaderLayout
						,llPresellerSignHeader,llCustomerSignHeader, llOrderPreviewBottom, llTrxTypeName;
	private TextView tvHeaderPreview, tvItemName, tvOrderPreviewunits , tvTotalAmount, tvTotalAmountValue, tvOrderPreviewCases,
					 tvCustomerPasscode, tvPresellerSign, tvCustomerSign, tvOrderPreviewPriceValue , tvOrderPreviewFooterCases, tvOrderPreviewFooterunits
					 ,tvDeliveryDate, tvDeliveryDateValue, tvDeliveryToTimeValue,tvDeliveryFromTimeValue, tvDeliveryToTime, tvDeliveryFromTime
					 ,tvOrderActualAmount,tvlanguage, tvTrxTypeName, tvTrxTypeNameV, tvBatchSourceName, tvBatchSourceNameV;
	private Button btnFinalize, btnPrintSalesOrder , btnPrintMerchant , btnCustomerSignClear , btnPresellerSignClear,btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
	private EditText etCustomerPasscode;
	private CustomListView lvPreviewOrder;
	private ReplacementPreviewAdapter orderPreviewAdapter;
	public static Vector<ProductDO> vecMainProducts;
	private double totalPrice = 0, totalAmount = 0,  totalPerItemAmount = 0, discount = 0.0,
				  totalSalesPrice = 0, totalInvoicedPrice =0.0, orderInvoice = 0;;
				  private float totalCases = 0, totalUnits = 0;
	private OrderDO orderDO;
	private JourneyPlanDO mallsDetails;
	private boolean isSalesOrderGerenaterd = false, isPosted = false;
	private MyView customerSignature, presellerSignature;
	private static Paint mPaint;
	private boolean isCustomerSigned = false, isPresellerSigned = false;
	private boolean isMenu = false, isTask;
	private String batchSourceName, from, orderId;
	
	@Override
	public void initialize()
	{
		//inflate the preview_order_list layout 
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_order_list, null);
		llBody.addView(llOrderPreview,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		if(getIntent().getExtras() != null)
		{
			totalPrice 		= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalPrice"));
			discount   		= 	StringUtils.getFloat(getIntent().getExtras().getString("Discount"));
			mallsDetails 	= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			orderId			= getIntent().getExtras().getString("invoicenum");
			orderInvoice	= getIntent().getExtras().getFloat("invoiceamt");
			from			= getIntent().getExtras().getString("from");
			isMenu			= getIntent().getExtras().getBoolean("isMenu");
			isTask			= getIntent().getExtras().getBoolean("isTask");
		}
		
		InitializeControls();
		
		if(isMenu)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		//managing the Decimal format up to  ##.##
		vecMainProducts = new Vector<ProductDO>();
		
		preference.removeFromPreference(Preference.ORDER_NO);
		preference.commitPreference();
		
		preference.saveBooleanInPreference("salesOrderPrited", false);
		preference.commitPreference();
		
		lvPreviewOrder = new CustomListView(SalesmanReplacementPreview.this);
		llMiddleLayout.addView(lvPreviewOrder,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lvPreviewOrder.setCacheColorHint(0);
		lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
		lvPreviewOrder.setVerticalScrollBarEnabled(false);
		lvPreviewOrder.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvPreviewOrder.addFooterView(llEsignature);
		lvPreviewOrder.addHeaderView(llHeaderLayout,null,false);
		
		lvPreviewOrder.setAdapter(orderPreviewAdapter = new ReplacementPreviewAdapter(SalesmanReplacementPreview.this, vecMainProducts));
		
		tvlanguage.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				ShowLanguagePopup();
			}
		});
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//initializing all Main Vector which contain all the product list
				batchSourceName = new CommonDA().getBatchSourceName();
				Vector<String> vecCategoryIds = new Vector<String>();
				if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
				{
					hideLoader();
					return;
				}
				totalPerItemAmount = 0;
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				for(int i=0; i<vecCategoryIds.size(); i++)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(vecCategoryIds.get(i));
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(StringUtils.getInt(objProductDO.preUnits) > 0 )
						{
							totalCases 						= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits 						= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);
							
							totalPerItemAmount 				= 	StringUtils.getFloat((totalPerItemAmount + objProductDO.itemPrice)+"");
							totalSalesPrice 				= 	StringUtils.getFloat((totalSalesPrice + objProductDO.totalPrice)+"");
							
							//Calculating total price per item 
							objProductDO.unitSellingPrice 	= 	0;
							objProductDO.invoiceAmount 		= 	0.0;
							objProductDO.discountAmount 	= 	0.0;
							
							if(objProductDO.secondaryUOM == null || objProductDO.secondaryUOM.equalsIgnoreCase(""))
									objProductDO.secondaryUOM = "BOT";
							
							vecMainProducts.add(objProductDO);
						}
					}
				}
				
				//getting customer's total price by adding the total price of current order + pending balance
				totalAmount = totalInvoicedPrice;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						orderPreviewAdapter.refresh(vecMainProducts);
						//setting text for the total-Layout
						tvOrderPreviewFooterCases.setText(""+(int)totalCases);
						tvOrderPreviewFooterunits.setText(""+(int)totalUnits);
						tvOrderPreviewPriceValue.setText(curencyCode+" "+diffAmt.format(totalPerItemAmount));
						tvTotalAmountValue.setText(curencyCode+" "+diffAmt.format(totalPrice));
						tvTotalAmountValue.setText(curencyCode+" "+diffAmt.format(totalSalesPrice));
						tvDeliveryDateValue.setText(""+CalendarUtils.getOrderPostDate());
						tvOrderActualAmount.setText(curencyCode+" "+diffAmt.format(totalInvoicedPrice));
						
						tvBatchSourceNameV.setText(batchSourceName);
						
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
						{
							tvCustomerSign.setText("Received By");
							tvPresellerSign.setText("Delivered By");
						}
						else
						{
							tvCustomerSign.setText(getString(R.string.Customer_Signature));
							tvPresellerSign.setText(getString(R.string.Preseller_Signature));
						}
						
						hideLoader();
					}
				});
			}
		}).start();
        
		btnFinalize.setTag("Finalize");
		btnFinalize.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				disableView(btnFinalize);
				if(v.getTag().toString().equalsIgnoreCase("Finalize"))
				{
					if(!isCustomerSigned && !isPresellerSigned)
					{
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
							showCustomDialog(SalesmanReplacementPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReplacementPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
					}
					else if(!isCustomerSigned)
					{
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
							showCustomDialog(SalesmanReplacementPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReplacementPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
					}
					else if(!isPresellerSigned)
						if(from != null && from.equalsIgnoreCase("replacement"))
							showCustomDialog(SalesmanReplacementPreview.this, getString(R.string.warning), "Please sign before submitting the replacement order.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReplacementPreview.this, getString(R.string.warning), "Please sign before submitting the return order.", getString(R.string.OK), null, "scroll");
					else if(tvTrxTypeNameV.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(SalesmanReplacementPreview.this, "Alert !", "Please select trx type name.", getString(R.string.OK), null, "scroll");
					else
						postOrder(AppConstants.HHOrder);
				}
				else
					showCustomDialog(SalesmanReplacementPreview.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
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
		
		/*btnOrderPreviewContinue.setText(" Continue ");
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showOrderCompletePopup();
			}
		});*/
		btnOrderPreviewContinue.setText("Finish ");
		
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//showOrderCompletePopup();
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
				sendBroadcast(intentBrObj);
			}
		});
		
		btnPrintSalesOrder.setText(" Print Order ");
		btnPrintSalesOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SalesmanReplacementPreview.this, WoosimPrinterActivity.class);
    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_REPLACE);
    			intent.putExtra("mallsDetails", mallsDetails);
    			intent.putExtra("totalCases", totalCases);
    			intent.putExtra("totalUnits", totalUnits);
    			intent.putExtra("OrderId", orderDO.OrderId);
    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
    			intent.putExtra("totalPrice", totalPrice);
				startActivityForResult(intent, 1000);
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
        
        customerSignature = new MyView(SalesmanReplacementPreview.this, "csign");
		customerSignature.setDrawingCacheEnabled(true);
		customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		presellerSignature = new MyView(SalesmanReplacementPreview.this, "psign");
		presellerSignature.setDrawingCacheEnabled(true);
		presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		if(customerSignature != null)
			llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		if(presellerSignature != null)
			llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	
		btnCustomerSignClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				isCustomerSigned = false;
				llCustomerSign.removeAllViews();
				customerSignature = new MyView(SalesmanReplacementPreview.this, "csign");
				customerSignature.setDrawingCacheEnabled(true);
				customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		});
		btnPresellerSignClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				isPresellerSigned = false;
				llPresellerSign.removeAllViews();
				presellerSignature = new MyView(SalesmanReplacementPreview.this,"psign");
				presellerSignature.setDrawingCacheEnabled(true);
				presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		});
		
		tvTrxTypeNameV.setText(mallsDetails.Order_Type_Name);
		
		setTypeFace(llOrderPreview);
	}
	
	public void postOrder(final String orderType)
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
				orderDO.TotalAmount 	= 	totalInvoicedPrice;
			
				if(from != null && from.equalsIgnoreCase("replacement"))
					orderDO.orderType		= 	AppConstants.REPLACEMETORDER;
				else
					orderDO.orderType		= 	AppConstants.RETURNORDER;
				
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.strUUID			= 	StringUtils.getUniqueUUID();
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
				orderDO.LPOCode			=	"0";
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"0000";
				orderDO.TRXStatus		=	"D";
				orderDO.salesmanCode	=	""+mallsDetails.salesmanCode;
				orderDO.vehicleNo   	=   preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
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
				
				showLoader(getResources().getString(R.string.please_wait_order_pushing));
				
				if(from.equalsIgnoreCase("replacement"))
				{
					orderDO.TRANSACTION_TYPE_KEY		= 	AppConstants.IBS_Replacement_Issues_K;
					orderDO.TRANSACTION_TYPE_VALUE		= 	AppConstants.IBS_Replacement_Issues_V;
				}
				else
				{
					orderDO.TRANSACTION_TYPE_KEY		= 	AppConstants.IBS_Returns_K;
					orderDO.TRANSACTION_TYPE_VALUE		= 	AppConstants.IBS_Returns_V;
				}
				
				orderDO.Batch_Source_Name		= 	tvBatchSourceNameV.getText().toString();
				orderDO.Trx_Type_Name			= 	tvTrxTypeNameV.getText().toString();
				
				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_PromoNoOffer(orderDO, vecMainProducts, preference, AppConstants.Return);
				
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					new OrderDA().updateInventoryStatusReplacement(vecMainProducts, CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.EMP_NO, ""));
					uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
					isSalesOrderGerenaterd = true;
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
							showOrderCompletePopup();
							
							btnPrintSalesOrder.setVisibility(View.GONE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
						}
						else
							showCustomDialog(SalesmanReplacementPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
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
		llOrderPreviewBottom		= 	(LinearLayout)llOrderPreview.findViewById(R.id.llOrderPreviewBottom);
		tvHeaderPreview 			= 	(TextView)llOrderPreview.findViewById(R.id.tvOrderPreviewHeader);
		btnFinalize	 				= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewFinalize);
		btnPrintSalesOrder			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrder);
		btnPrintMerchant			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
		btnOrderPreviewContinue		= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewContinue);
		btnPrintSalesOrderMerchant	= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
		tvlanguage					= 	(TextView)llOrderPreview.findViewById(R.id.tvlanguage);
		TextView tvLu				= 	(TextView)llOrderPreview.findViewById(R.id.tvLu);
		
		//Masafi logo Layout as header of list view
		llHeaderLayout      		= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_replacement, null);
		tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
		tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
		tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
		
		//Signature Layout as footer of list view
		llEsignature 				= (LinearLayout)getLayoutInflater().inflate(R.layout.esignature_order_preview, null);
		llPresellerSign 			= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature_e);
		llCustomerSign 				= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature_e);
		llPresellerSignHeader		= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSign);
		llCustomerSignHeader		= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSign);
		LinearLayout llTotalAmt		= (LinearLayout)llEsignature.findViewById(R.id.llTotalAmt);
		
		tvPresellerSign 			= (TextView)llEsignature.findViewById(R.id.tvPresellerSignature_e);
		tvCustomerSign    			= (TextView)llEsignature.findViewById(R.id.tvCustomerSignature_e);
		tvCustomerPasscode			= (TextView)llEsignature.findViewById(R.id.tvCustomerPasscode_e);
		etCustomerPasscode			= (EditText)llEsignature.findViewById(R.id.etCustomerPasscode_e);
		tvDeliveryDate				= (TextView)llEsignature.findViewById(R.id.tvDeliveryDate);
		tvDeliveryDateValue			= (TextView)llEsignature.findViewById(R.id.tvDeliveryDateValue);
		tvDeliveryToTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTimeValue);
		tvDeliveryFromTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTimeValue);
		tvDeliveryToTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTime);
		tvDeliveryFromTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTime);
		tvOrderActualAmount			= (TextView)llEsignature.findViewById(R.id.tvOrderActualAmount);
		tvTotalAmount				= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmount);
		tvTotalAmountValue 			= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmountValue);
		tvOrderPreviewPriceValue	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewPriceValue);
		tvOrderPreviewFooterCases	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterCases);
		tvOrderPreviewFooterunits	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterunits);
		btnCustomerSignClear		= (Button)llEsignature.findViewById(R.id.btnCustomerSignClear);
		btnPresellerSignClear		= (Button)llEsignature.findViewById(R.id.btnPresellerSignClear);
		LinearLayout llCustomer_Passcode	= (LinearLayout)llEsignature.findViewById(R.id.llCustomer_Passcode);
		LinearLayout llDeliveryDate			= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDate);
		LinearLayout llDeliveryDeliveryTime	= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDeliveryTime);
		TextView tvBottomDist			    = (TextView)llEsignature.findViewById(R.id.tvBottomDist);
		
		llTrxTypeName = (LinearLayout)llEsignature.findViewById(R.id.llTrxTypeName);
		tvTrxTypeName= (TextView)llEsignature.findViewById(R.id.tvTrxTypeName);
		tvTrxTypeNameV= (TextView)llEsignature.findViewById(R.id.tvTrxTypeNameV);

		tvBatchSourceName  = (TextView)llEsignature.findViewById(R.id.tvBatchSourceName);
		tvBatchSourceNameV = (TextView)llEsignature.findViewById(R.id.tvBatchSourceNameV);
		
		tvTotalAmountValue.setVisibility(View.VISIBLE);
		tvOrderPreviewPriceValue.setVisibility(View.VISIBLE);
		tvOrderActualAmount.setVisibility(View.VISIBLE);
		 
		llDeliveryDate.setVisibility(View.GONE);
		llCustomer_Passcode.setVisibility(View.GONE);
		llDeliveryDeliveryTime.setVisibility(View.GONE);
		llTotalAmt.setVisibility(View.GONE);
		
		if(mallsDetails.channelCode != null && mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			tvOrderPreviewPriceValue.setVisibility(View.GONE);
			tvTotalAmountValue.setVisibility(View.GONE);
			tvOrderActualAmount.setVisibility(View.GONE);
			
			tvBottomDist.setVisibility(View.GONE);
			
			tvOrderPreviewunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
			tvOrderPreviewFooterunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
		}
		
		//setting TypeFaces here
		btnPrintSalesOrderMerchant.setVisibility(View.GONE);
		btnPrintSalesOrder.setVisibility(View.GONE);
		if(from != null && from.equalsIgnoreCase("replacement"))
			tvHeaderPreview.setText("Preview Replacement Order");
		else
			tvHeaderPreview.setText("Preview Return Order");
		
		showVisibleButton(false);
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
				etCustomerPasscode.requestFocus();
			}
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
			if(isMenu)
				performCheckouts(false);
			else
				performCustomerServed();	
		}
		else if(from.equalsIgnoreCase("payment_new"))
		{
			Intent intent = new Intent(SalesmanReplacementPreview.this, PendingInvoices.class);
			intent.putExtra("arcollection", false);
			intent.putExtra("isReturnOrder", true);
			intent.putExtra("AR", false);
			
			if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
				intent.putExtra("isCredit", true);
			
			if(orderId != null && orderId.length() > 0)
			{
				orderInvoice = orderInvoice - totalInvoicedPrice;
				
				intent.putExtra("selectedAmount", orderInvoice);
				intent.putExtra("invoiceAmount", orderInvoice);
				intent.putExtra("invoiceNo", orderId);
			}
			else
			{
				intent.putExtra("selectedAmount", totalInvoicedPrice);
				intent.putExtra("invoiceAmount", totalInvoicedPrice);
				intent.putExtra("invoiceNo", orderDO.OrderId);
			}
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
	}
	
	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		
		if(from.equalsIgnoreCase("payment_new"))
			showCustomDialog(SalesmanReplacementPreview.this, getString(R.string.successful), "Return order confirmed successfully. You served this customer.", "OK",null, "served", false);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode == 2222)
    		AppConstants.isServeyCompleted = true;
    	/*else if(resultCode == 20000 || resultCode == -20000)
    	{
    		showOrderCompletePopup();
    	}*/
    }
	 
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPosted || isSalesOrderGerenaterd)
			btnOrderPreviewContinue.performClick();
		else 
			finish();
	}
	
	public class MyView extends View 
	{
		private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private String strFrom= "";
        float x,y;
        
        public MyView(Context c, String strFrom)
        {
            super(c);
            this.strFrom = strFrom;
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();
            int height = display.getHeight();
            if(mBitmap != null)
            {
            	mBitmap.recycle();
            	mBitmap = null;
            	System.gc();
            }
            
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
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
	      return true;
      	}
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanReplacementPreview.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);
		
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1		  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Replacement Order Placed");
		tv_poptitle1.setText("Successfully");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment		  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			btn_popup_collectpayment.setVisibility(View.GONE);
		
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				ShowOptionPopupForPrinter(SalesmanReplacementPreview.this,new PrintPopup() {
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
								intent=new Intent(SalesmanReplacementPreview.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(SalesmanReplacementPreview.this, PrinterConnectorArabic.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_REPLACE);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("totalCases", totalCases);
							intent.putExtra("totalUnits", totalUnits);
							intent.putExtra("OrderId", orderDO.OrderId);
							intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
							intent.putExtra("totalPrice", totalPrice);
							intent.putExtra("from", from);
							startActivityForResult(intent, 1000);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(SalesmanReplacementPreview.this, WoosimPrinterActivity.class);
//    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_REPLACE);
//    			intent.putExtra("mallsDetails", mallsDetails);
//    			intent.putExtra("totalCases", totalCases);
//    			intent.putExtra("totalUnits", totalUnits);
//    			intent.putExtra("OrderId", orderDO.OrderId);
//    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
//    			intent.putExtra("totalPrice", totalPrice);
//    			intent.putExtra("from", from);
//				startActivityForResult(intent, 1000);
			}
		});
		
		if(mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			btn_popup_collectpayment.setVisibility(View.GONE);
		
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			btn_popup_collectpayment.setVisibility(View.GONE);
		else
		{
			if(totalInvoicedPrice > 0)
			{
//				btn_popup_collectpayment.setVisibility(View.VISIBLE);
//				btn_popup_collectpayment.setText("Return Amount");
				
				btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check2_new), null);
				btn_popup_collectpayment.setClickable(true);
				btn_popup_collectpayment.setEnabled(true);
			}
			else 
			{
				btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
				btn_popup_collectpayment.setClickable(false);
				btn_popup_collectpayment.setEnabled(false);
			}
		}
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				//mCustomDialog.dismiss();
				onButtonYesClick("payment_new");
			}
		});
		
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				if(!isMenu && mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && orderId != null && orderId.length() > 0)
				{
					if(! new PaymentDetailDA().isPaymentDone(orderId))
						onButtonYesClick("payment");
					else
						onButtonYesClick("served");
				}
				else
					onButtonYesClick("served");
			}
		});
		
		try{
		setTypeFace((LinearLayout)view);
		
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	
	protected void ShowLanguagePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_language, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanReplacementPreview.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);

		final Button btn_popup_English	  = (Button) view.findViewById(R.id.btn_popup_English);
		final Button btn_popup_Arabic	  = (Button) view.findViewById(R.id.btn_popup_Arabic);
		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);

		btn_popup_English.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				tvlanguage.setText("English");
				lvPreviewOrder.removeHeaderView(llHeaderLayout);
//				llHeaderLayout.removeAllViews();
				llHeaderLayout = null;
				llHeaderLayout   = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header, null);

				lvPreviewOrder.addHeaderView(llHeaderLayout);
				
				tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
				tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
				tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
				TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
				TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
				TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
				TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
				orderPreviewAdapter.refresh(vecMainProducts);
				
				tvPresellerSign.setText(getString(R.string.Preseller_Signature));
				tvCustomerSign.setText(getString(R.string.Customer_Signature));
				btnCustomerSignClear.setText(getString(R.string.Clear));
				btnPresellerSignClear.setText(getString(R.string.Clear));
				mCustomDialog.dismiss();
			}
		});

		btn_popup_Arabic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
//				Intent in = new Intent(SalesmanOrderPreview.this, SalesmanOrderPreview.class);
				lvPreviewOrder.removeHeaderView(llHeaderLayout);
//				llHeaderLayout.removeAllViews();
				llHeaderLayout = null;
				llHeaderLayout   = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_ar, null);

				lvPreviewOrder.addHeaderView(llHeaderLayout);
				
				tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
				tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
				tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
				TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
				TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
				TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
				TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
				
				orderPreviewAdapter.refresh(vecMainProducts);
				tvTotalPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvInvoiceAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvDiscount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvPresellerSign.setText(getString(R.string.salesmansign_ar));
				tvCustomerSign.setText(getString(R.string.customersign_ar));
				tvlanguage.setText("Arabic");
				btnCustomerSignClear.setText(getString(R.string.clear_ar));
				btnPresellerSignClear.setText(getString(R.string.clear_ar));
				mCustomDialog.dismiss();
			}
		});
		btn_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{

				mCustomDialog.dismiss();
			}
		});

		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}

	}
}
