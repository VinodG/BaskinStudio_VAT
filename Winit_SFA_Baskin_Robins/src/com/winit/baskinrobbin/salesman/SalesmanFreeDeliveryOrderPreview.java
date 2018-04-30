package com.winit.baskinrobbin.salesman;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;

import com.winit.baskinrobbin.salesman.adapter.OrderPreviewAdapter_FreeDelivery;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomListView;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class SalesmanFreeDeliveryOrderPreview extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llOrderPreview , llMiddleLayout, llEsignature, llCustomerSign, llPresellerSign, llHeaderLayout;
	private TextView tvHeaderPreview, tvItemName, tvOrderPreviewunits , tvTotalAmount, tvTotalAmountValue, tvOrderPreviewCases,
					 tvCustomerPasscode, tvPresellerSign, tvCustomerSign, tvOrderPreviewPriceValue , tvOrderPreviewFooterCases, tvOrderPreviewFooterunits
					 ,tvDeliveryDate, tvDeliveryDateValue, tvDeliveryToTimeValue,tvDeliveryFromTimeValue, tvDeliveryToTime, tvDeliveryFromTime
					 ,tvOrderActualAmount;
	private Button btnFinalize, btnPrintSalesOrder , btnPrintMerchant , btnCustomerSignClear , btnPresellerSignClear,btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
	private EditText etCustomerPasscode;
	private CustomListView lvPreviewOrder;
	private OrderPreviewAdapter_FreeDelivery orderPreviewAdapter;
	public static Vector<ProductDO> vecMainProducts;
	private double totalPrice = 0, totalAmount = 0,  totalPerItemAmount = 0, discount = 0.0f, totalSalesPrice = 0, totalInvoicedPrice =0.0f;
	private OrderDO orderDO;
	private float totalUnits = 0.0f,totalCases = 0;
	private JourneyPlanDO mallsDetails;
	private boolean isSalesOrderGerenaterd = false, isPosted = false;
	private MyView customerSignature, presellerSignature;
	private static Paint mPaint;
	private boolean isPresellerSigned = false, isCustomerSigned = false;
	private View  tempView, tempTextView;
	private String strFromOrTo = "";
	private final int DATE_DIALOG_ID = 0, ID_DATEPICKER =1;
	
	private String batchSourceName;
	private Vector<NameIDDo> vecTrxTypeName;
	
	private LinearLayout llTrxTypeName;
	private TextView tvTrxTypeName;
	private TextView tvTrxTypeNameV;

	private TextView tvBatchSourceName;
	private TextView tvBatchSourceNameV;
	
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
		}
		
		InitializeControls();
		
		TextView tvlanguage = (TextView) llOrderPreview.findViewById(R.id.tvlanguage);
		ImageView ivArowRight = (ImageView) llOrderPreview.findViewById(R.id.ivArowRight);
		ivArowRight.setVisibility(View.GONE);
		tvlanguage.setVisibility(View.GONE);
		
		//managing the Decimal format up to  ##.##
		vecMainProducts = new Vector<ProductDO>();
		
		lvPreviewOrder = new CustomListView(SalesmanFreeDeliveryOrderPreview.this);
		llMiddleLayout.addView(lvPreviewOrder,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lvPreviewOrder.setCacheColorHint(0);
		lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
		lvPreviewOrder.setVerticalScrollBarEnabled(false);
		lvPreviewOrder.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvPreviewOrder.addFooterView(llEsignature, null, false);
		lvPreviewOrder.addHeaderView(llHeaderLayout,null,false);
		
		lvPreviewOrder.setAdapter(orderPreviewAdapter = new OrderPreviewAdapter_FreeDelivery(SalesmanFreeDeliveryOrderPreview.this, vecMainProducts));
		
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				batchSourceName = new CommonDA().getBatchSourceName();
				vecTrxTypeName  = new CommonDA().getAllArInvoiceMethodByBatchSrc(batchSourceName, AppConstants.TRX_KEY_RETURN);
				
				//initializing all Main Vector which contain all the product list
				Vector<String> vecCategoryIds = new Vector<String>();
				if(AppConstants.hmCapturedInventoryFreeDelivery == null || AppConstants.hmCapturedInventoryFreeDelivery.size() == 0)
				{
					hideLoader();
					return;
				}
				
				Set<String> set = AppConstants.hmCapturedInventoryFreeDelivery.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				for(int i=0; i<vecCategoryIds.size(); i++)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventoryFreeDelivery.get(vecCategoryIds.get(i));
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(StringUtils.getFloat(objProductDO.preUnits) > 0 )
						{
							//Calculating total price per item 
							totalPerItemAmount = StringUtils.getFloat(diffAmt.format(totalPerItemAmount + objProductDO.itemPrice));
							//getting total price after multiply with cases
							totalSalesPrice = StringUtils.getFloat(diffAmt.format(totalSalesPrice + objProductDO.totalPrice));
							//getting total Cases and Units  
							totalCases = totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits = totalUnits + StringUtils.getFloat(objProductDO.preUnits);
							
							objProductDO.reason = " ";
							objProductDO.unitSellingPrice 	= 	StringUtils.getFloat(diffAmt.format(objProductDO.itemPrice - (objProductDO.itemPrice*objProductDO.Discount)/100));
							objProductDO.invoiceAmount 		= 	StringUtils.getDouble(diffAmt.format(objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preCases)));
							objProductDO.discountAmount 	= 	StringUtils.getDouble(diffAmt.format((objProductDO.itemPrice - objProductDO.unitSellingPrice)*StringUtils.getFloat(objProductDO.preCases)));
							totalInvoicedPrice = totalInvoicedPrice+objProductDO.invoiceAmount;
							
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
						
						tvBatchSourceNameV.setText(batchSourceName);
						
						//setting text for the total-Layout
						tvOrderPreviewFooterCases.setText(""+diffPreview.format(totalCases));
						tvOrderPreviewFooterunits.setText(""+diffPreview.format(totalUnits));
						tvOrderPreviewPriceValue.setText(curencyCode+" "+diffAmt.format(totalPerItemAmount));
						tvTotalAmountValue.setText(curencyCode+" "+diffAmt.format(totalPrice));
						tvTotalAmountValue.setText(curencyCode+" "+diffAmt.format(totalSalesPrice));
						tvDeliveryDateValue.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
						tvOrderActualAmount.setText(curencyCode+" "+diffAmt.format(totalInvoicedPrice));
						
						if(mallsDetails.channelCode != null && mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
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
				if(v.getTag().toString().equalsIgnoreCase("Finalize"))
				{
					performFinalize();
				}
				else
					showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
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
		
		btnOrderPreviewContinue.setText(" Finish ");
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(1000);
				finish();
			}
		});
		
		btnPrintSalesOrder.setText("   Print Order   ");
		btnPrintSalesOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	    		if(mBtAdapter != null)
	    		{
//	    			Intent intent = new Intent(SalesmanFreeDeliveryOrderPreview.this, BluetoothFilePrinter.class);
	    			Intent intent = new Intent(SalesmanFreeDeliveryOrderPreview.this, WoosimPrinterActivity.class);
	    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_FREEDELIVERY);
	    			intent.putExtra("OrderId", orderDO.OrderId);
	    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
	    			intent.putExtra("mallsDetails", mallsDetails);
	    			intent.putExtra("customerName", tvDeliveryDateValue.getText().toString());
	    			intent.putExtra("DeliveryDate", tvDeliveryDateValue.getText().toString());
					startActivityForResult(intent, 1000);
//	    			showToast("Print functionality is in progress.");
	    		}
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
        
        presellerSignature = new MyView(SalesmanFreeDeliveryOrderPreview.this, "psign");
		presellerSignature.setDrawingCacheEnabled(true);
		presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		customerSignature = new MyView(SalesmanFreeDeliveryOrderPreview.this, "csign");
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
				customerSignature = new MyView(SalesmanFreeDeliveryOrderPreview.this, "csign");
				customerSignature.setDrawingCacheEnabled(true);
				customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
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
				presellerSignature = new MyView(SalesmanFreeDeliveryOrderPreview.this,"psign");
				presellerSignature.setDrawingCacheEnabled(true);
				presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
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
				inputManager.hideSoftInputFromWindow(etCustomerPasscode.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				try
				{
					tempTextView = v;
					strFromOrTo = "from";
					showDialog(ID_DATEPICKER);
//					showDialog(ID_DATEPICKER);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tvDeliveryToTimeValue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etCustomerPasscode.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				try
				{
					tempTextView = v;
					strFromOrTo = "to";
					showDialog(ID_DATEPICKER);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tvTrxTypeNameV.setTag(-1);
		tvTrxTypeNameV.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesmanFreeDeliveryOrderPreview.this, "Select Trx Type Name", true);
				builder.setSingleChoiceItems(vecTrxTypeName, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
						tvTrxTypeNameV.setText(""+ObjNameIDDo.strName);
						tvTrxTypeNameV.setTag(ObjNameIDDo);
						builder.dismiss();
		    		}
			   }); 
				builder.show();
			}
		});
		
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
				orderDO.TotalAmount 	= 	totalAmount;
				orderDO.orderType		= 	AppConstants.HHOrder;
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.strUUID			= 	StringUtils.getUniqueUUID();
				orderDO.orderSubType	=	AppConstants.FREE_DELIVERY_ORDER;
				orderDO.strCustomerPriceKey	=  "0";
				orderDO.pushStatus		=	0;
				orderDO.message			=	"";
				
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.CurrencyCode	=	curencyCode+"";
				orderDO.PaymentType		=	""+mallsDetails.paymentType;
				orderDO.PaymentCode		=	""+mallsDetails.paymentTermCode;
				orderDO.TrxReasonCode	=	""+mallsDetails.freeDeliveryResion;
				orderDO.LPOCode			=	""+mallsDetails.siteName;
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"0000";
				orderDO.TRXStatus		=	"D";
				orderDO.salesmanCode	=	""+orderDO.PresellerId;
				orderDO.vehicleNo		=	 preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				orderDO.CurrencyCode 	= 	 mallsDetails.currencyCode;
				
				//pre-seller signature
				Bitmap bitmap = getBitmap(presellerSignature);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());

				//customer signature
				Bitmap image = getBitmap(customerSignature);
				ByteArrayOutputStream streams = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
				orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
				
				showLoader(getResources().getString(R.string.please_wait_order_pushing));
				
//				orderDO.TRANSACTION_TYPE_KEY		= 	AppConstants.IBS_Sales_K;
//				orderDO.TRANSACTION_TYPE_VALUE		= 	AppConstants.IBS_Sales_V;
				
				orderDO.Batch_Source_Name		= 	tvBatchSourceNameV.getText().toString();
				orderDO.Trx_Type_Name			= 	tvTrxTypeNameV.getText().toString();
				
				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_PromoNoOffer(orderDO, vecMainProducts, preference, AppConstants.Order);
				
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					new OrderDA().updateInventoryStatus_New(vecMainProducts, CalendarUtils.getOrderPostDate());
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
							if(isPosted)
								showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.successful), getString(R.string.free_delivery_order), getString(R.string.OK), null, "");
							else
								showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.successful), getString(R.string.free_delivery_order), getString(R.string.OK), null, "");
							
							btnPrintSalesOrder.setVisibility(View.VISIBLE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
							
							btnCustomerSignClear.setClickable(false);
							btnCustomerSignClear.setEnabled(false);
							
							btnPresellerSignClear.setClickable(false);
							btnPresellerSignClear.setEnabled(false);
							
							presellerSignature.setEnabled(false);
							presellerSignature.setClickable(false);
							customerSignature.setEnabled(false);
							customerSignature.setClickable(false);
						}
						else
							showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.warning), "Error occurred while taking order.", getString(R.string.OK), null, "");
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
		TextView tvLu				= 	(TextView)llOrderPreview.findViewById(R.id.tvLu);
		
		setTypeFace(llMiddleLayout);
		//Masafi logo Layout as header of list view
		llHeaderLayout      		= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_freedelivery, null);
		tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
		tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
		tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
		TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
		TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
		TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
		TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
		
		//Signature Layout as footer of list view
		llEsignature 				= (LinearLayout)getLayoutInflater().inflate(R.layout.esignature_order_preview_free_delivery, null);
		llPresellerSign 			= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature_e);
		llCustomerSign 				= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature_e);
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
		
		llTrxTypeName = (LinearLayout)llEsignature.findViewById(R.id.llTrxTypeName);
		tvTrxTypeName= (TextView)llEsignature.findViewById(R.id.tvTrxTypeName);
		tvTrxTypeNameV= (TextView)llEsignature.findViewById(R.id.tvTrxTypeNameV);

		tvBatchSourceName= (TextView)llEsignature.findViewById(R.id.tvBatchSourceName);
		tvBatchSourceNameV = (TextView)llEsignature.findViewById(R.id.tvBatchSourceNameV);
		
		
		tvDeliveryDateValue.setBackgroundResource(R.drawable.input);
		tvDeliveryDateValue.setPadding(10, 2, 0, 2);
		
		llDeliveryDate.setVisibility(View.VISIBLE);
		llCustomer_Passcode.setVisibility(View.GONE);
		llDeliveryDeliveryTime.setVisibility(View.GONE);
		
		tvTotalPrice.setVisibility(View.GONE);
		tvOrderActualAmount.setVisibility(View.GONE);
		tvOrderPreviewCases.setVisibility(View.VISIBLE);
		tvOrderPreviewFooterCases.setVisibility(View.INVISIBLE);
		
		btnPrintSalesOrderMerchant.setVisibility(View.GONE);
		btnPrintSalesOrder.setVisibility(View.GONE);
		tvHeaderPreview.setText("Preview Free Delivery");
		showVisibleButton(false);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		setTypeFace(llEsignature);
		setTypeFace(llHeaderLayout);
		
		tvLu.setText(mallsDetails.siteName);
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
			performCustomerServed();
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			Intent intent =	new Intent(SalesmanFreeDeliveryOrderPreview.this,  SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
	}
	
	@Override
	public void onButtonNoClick(String from) 
	{
		if(from.equalsIgnoreCase("payment"))
		{
			showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, "Alert !", "Do you want to create a Return Request ?", "Yes", "No", "ReturnRequest", false);
			
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			showCustomDialog(SalesmanFreeDeliveryOrderPreview.this,getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served",false);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 20000)
    	{
			showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.successful), getString(R.string.your_sales_order_printed), getString(R.string.OK), null , "");
    	}
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
	        float x,y;
	        private String strFrom = "";
	        
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
	
	private void performFinalize()
	{
		if(tvDeliveryDateValue.getText().toString().equalsIgnoreCase(""))
		{
			showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.warning), "Please select delivery date.", getString(R.string.OK), null, "scroll");
		}
		else 
		{
			postOrder(AppConstants.HHOrder);
		}
	}
	
	 @Override
	  protected Dialog onCreateDialog(int id) 
	  {
		  //getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 1);
		  int cyear 	= 	c.get(Calendar.YEAR);
		  int cmonth 	= 	c.get(Calendar.MONTH);
		  int cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  int myHour 	= 	c.get(Calendar.HOUR_OF_DAY);
		  int myMinute 	= 	c.get(Calendar.MINUTE);
		    
		  switch (id) 
		  {
			  case DATE_DIALOG_ID:
				  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
			  case ID_DATEPICKER:
				  return new TimePickerDialog(this,  timeSetListener,  myHour, myMinute, true);
		  }
		  return null;
	  }
		
		/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	  {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar currentCal = Calendar.getInstance();
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
			    		
					  ((TextView)tempView).setText(year+"-"+strMonth+"-"+strDate);
				  }
			  }
			  else
				  showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.warning), "Delivery date should be greater than current date.", getString(R.string.OK), null, "");
		  }
	  };
	  
	  private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener()
		 {
		   @Override
		   public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
		   {
			   Calendar tempCalendar = Calendar.getInstance();
			   Calendar cuCalendar   = Calendar.getInstance();
			   int year 	= cuCalendar.get(Calendar.YEAR);
			   int month	= cuCalendar.get(Calendar.MONTH);
			   int day		= cuCalendar.get(Calendar.DAY_OF_MONTH);
			   cuCalendar.set(year, month, day, hourOfDay, minute, 0);
			   
			   if(strFromOrTo.equalsIgnoreCase("from") && !tvDeliveryToTimeValue.getText().toString().equalsIgnoreCase(""))
			   {
				   String str[] = tvDeliveryToTimeValue.getText().toString().split(":");
				   int hours 	= StringUtils.getInt(str[0]);
				   int minutes  = StringUtils.getInt(str[1]);
				   tempCalendar.set(year, month, day, hours, minutes, 0);
				   
				   if(tempCalendar.after(cuCalendar))
					   showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.warning), "Start time should be less than end time.", getString(R.string.OK), null, "");
				   else
					   setTime(hourOfDay, minute, ((TextView)tempTextView));
			   }
			   else if(strFromOrTo.equalsIgnoreCase("to") && !tvDeliveryFromTimeValue.getText().toString().equalsIgnoreCase(""))
			   {
				   String str[] = tvDeliveryFromTimeValue.getText().toString().split(":");
				   int hours 	= StringUtils.getInt(str[0]);
				   int minutes  = StringUtils.getInt(str[1]);
				   tempCalendar.set(year, month, day, hours, minutes, 0);
				   
				   if(tempCalendar.before(cuCalendar))
					   showCustomDialog(SalesmanFreeDeliveryOrderPreview.this, getString(R.string.warning), "End time should be greater than start time.", getString(R.string.OK), null, "");
				   else
					   setTime(hourOfDay, minute, ((TextView)tempTextView));
			   }
			   else
				   setTime(hourOfDay, minute, ((TextView)tempTextView));
		   }
		 };
		 
		 
		 private void setTime(int hourOfDay, int minute, TextView tvText)
		 {
			 String strhour = "", strMiutes = "";
			   if(hourOfDay < 10)
				   strhour ="0"+hourOfDay;
			   else
				   strhour =""+hourOfDay;
			   
			   if(minute < 10)
				   strMiutes = "0"+minute;
			   else
				   strMiutes =""+minute;
			   tvText.setText(strhour+":"+strMiutes);
		 }
}
