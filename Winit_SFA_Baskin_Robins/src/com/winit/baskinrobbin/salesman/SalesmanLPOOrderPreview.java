package com.winit.baskinrobbin.salesman;
import java.io.ByteArrayOutputStream;
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
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;

public class SalesmanLPOOrderPreview extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llTotalPrice, llTrxTypeName, llOrderPreview , llMiddleLayout, llEsignature, llCustomerSign, llPresellerSign, llHeaderLayout, llPresellerSignature, llCustomerSignature;
	private TextView tvHeaderPreview, tvItemName, tvOrderPreviewunits , tvTotalAmount, tvTotalAmountValue, tvOrderPreviewCases,
					 tvCustomerPasscode, tvPresellerSign, tvCustomerSign, tvOrderPreviewPriceValue , tvOrderPreviewFooterCases, tvOrderPreviewFooterunits
					 ,tvDeliveryDate, tvDeliveryDateValue, tvDeliveryToTimeValue,tvDeliveryFromTimeValue, tvDeliveryToTime, tvDeliveryFromTime
					 ,tvOrderActualAmount, tvImage,tvlanguage, tvLu
					 , tvTotalItemAmt, tvDscount, tvTotalInvoiceAmt
					 ,tvTotalItemAmtValue, tvDscountValue, tvTotalInvoiceAmtValue, tvTrxTypeName, tvTrxTypeNameV, tvSourceSubInventory, tvSourceSubInventoryV;
	
	private Button btnFinalize, btnPrintSalesOrder , btnPrintMerchant , btnCustomerSignClear , btnPresellerSignClear,btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
	private EditText etCustomerPasscode;
	private CustomListView lvPreviewOrder;
	private OrderPreviewAdapter orderPreviewAdapter;
	public static Vector<ProductDO> vecMainProducts;
	private float totalCases = 0, totalUnits = 0;
	private JourneyPlanDO mallsDetails;
	private boolean isSalesOrderGerenaterd = false, isPosted = false;
	private MyView customerSignature, presellerSignature;
	private static Paint mPaint;
	private boolean isPresellerSigned = false, isCustomerSigned = false;
	private final int ID_DATEPICKER =1;
	private OrderDO orderDO;
	
	@Override
	public void initialize()
	{
		//inflate the preview_order_list layout 
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_order_list, null);
		llBody.addView(llOrderPreview,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		vecMainProducts = new Vector<ProductDO>();
		
		preference.removeFromPreference(Preference.ORDER_NO);
		preference.commitPreference();
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails 			= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			orderDO					= 	(OrderDO) getIntent().getExtras().get("objOrder");
		}
		
		InitializeControls();
		
		setTypeFace(llOrderPreview);
		preference.saveBooleanInPreference("salesOrderPrited", false);
		preference.commitPreference();
		
		lvPreviewOrder = new CustomListView(SalesmanLPOOrderPreview.this);
		llMiddleLayout.addView(lvPreviewOrder,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		lvPreviewOrder.setCacheColorHint(0);
		lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
		lvPreviewOrder.setVerticalScrollBarEnabled(false); 
		lvPreviewOrder.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvPreviewOrder.addFooterView(llEsignature, null, false);
		lvPreviewOrder.addHeaderView(llHeaderLayout,null,false);
		lvPreviewOrder.setAdapter(orderPreviewAdapter = new OrderPreviewAdapter(SalesmanLPOOrderPreview.this, vecMainProducts, false, true, AppConstants.CUSTOMER_CHANNEL_PARLOUR));
	
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//initializing all Main Vector which contain all the product list
				if(AppConstants.hmCapturedInventoryAdvance == null || AppConstants.hmCapturedInventoryAdvance.size() == 0)
				{
					hideLoader();
					return;
				}
				
				Set<String> set = AppConstants.hmCapturedInventoryAdvance.keySet();
				for(String key : set)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventoryAdvance.get(key);
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if((StringUtils.getFloat(objProductDO.preUnits) > 0))
						{
							totalCases 				= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits 				= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);
							
							objProductDO.reason 	= 	"";
							vecMainProducts.add(objProductDO);
						}
					}
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						orderPreviewAdapter.refresh(vecMainProducts);
						//setting text for the total-Layout
						tvOrderPreviewFooterCases.setText(""+(int)totalCases);
						tvOrderPreviewFooterunits.setText(""+(int)totalUnits);
						tvDeliveryDateValue.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
						tvDeliveryDateValue.setTag(CalendarUtils.getOrderPostDate());
						tvSourceSubInventoryV.setText(orderDO.Batch_Source_Name);
						tvCustomerSign.setText(getString(R.string.Customer_Signature));
						tvPresellerSign.setText(getString(R.string.Preseller_Signature));
						
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
				btnFinalize.setClickable(false);
				btnFinalize.setEnabled(false);
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run()
					{
						btnFinalize.setClickable(true);
						btnFinalize.setEnabled(true);
					}
				}, 500);
				
				if(v.getTag().toString().equalsIgnoreCase("Finalize"))
				{
					if(!isCustomerSigned)
					{
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
							showCustomDialog(SalesmanLPOOrderPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
						
						else
							showCustomDialog(SalesmanLPOOrderPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
					}
					
					else if(!isPresellerSigned)
						showCustomDialog(SalesmanLPOOrderPreview.this, "Alert !", "Please sign before submitting the order.", getString(R.string.OK), null, "scroll");
					
					else if(tvTrxTypeNameV.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(SalesmanLPOOrderPreview.this, "Alert !", "Please select trx type name.", getString(R.string.OK), null, "scroll");
					
					else if(tvSourceSubInventoryV.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(SalesmanLPOOrderPreview.this, "Alert !", "Please select subinventory.", getString(R.string.OK), null, "scroll");
					else
						updateLPOStatus();
				}
				else
					showCustomDialog(SalesmanLPOOrderPreview.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
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
		
		btnOrderPreviewContinue.setText("Continue ");
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showOrderCompletePopup();
			}
		});
		
		btnPrintSalesOrder.setText(" Print Order ");
		btnPrintSalesOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SalesmanLPOOrderPreview.this, WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.LPO_DELIVERY_NOTE);
				intent.putExtra("OrderId", orderDO);
				intent.putExtra("mallsDetails", mallsDetails);
				startActivity(intent);
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
        
        presellerSignature = new MyView(SalesmanLPOOrderPreview.this, "psign");
		presellerSignature.setDrawingCacheEnabled(true);
		presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		customerSignature = new MyView(SalesmanLPOOrderPreview.this, "csign");
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
				customerSignature = new MyView(SalesmanLPOOrderPreview.this, "csign");
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
				presellerSignature = new MyView(SalesmanLPOOrderPreview.this,"psign");
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
					showDialog(ID_DATEPICKER);
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
					showDialog(ID_DATEPICKER);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tvlanguage.setVisibility(View.GONE);
		
		tvHeaderPreview.setText("Preview "+AppConstants.LPO_ORDER +"( "+orderDO.OrderId+" )");
		tvTrxTypeNameV.setText(orderDO.Trx_Type_Name);
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
	
	private void updateLPOStatus() 
	{
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				orderDO.InvoiceDate 	= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				orderDO.PresellerId 	= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.message			=	"";
			
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.LPOCode			=	"0";
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"";
			
				orderDO.pushStatus		=	10;
				orderDO.TRXStatus		=	AppStatus.TRX_STATUS_DELIVRED;
				
				orderDO.salesmanCode	= 	mallsDetails.salesmanCode+"";
				orderDO.vehicleNo		= 	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
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
							uploadData(AppStatus.POST_ORDER, AppStatus.TODAY_DATA);
						}
						else
							showCustomDialog(SalesmanLPOOrderPreview.this, "Warning !", "Error occured while confirming the LPO, please try again.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
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
		
		//Signature Layout as footer of list view
		llEsignature 				= (LinearLayout)getLayoutInflater().inflate(R.layout.esignature_order_preview_lpo, null);
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
		LinearLayout llTotalAmt		= (LinearLayout)llEsignature.findViewById(R.id.llTotalAmt);
		
		llPresellerSignature	= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature);
		llCustomerSignature		= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature);
		
		tvImage						= (TextView)llEsignature.findViewById(R.id.tvImage);
		llTotalPrice	= (LinearLayout)llEsignature.findViewById(R.id.llTotalPrice);
		
		llTrxTypeName = (LinearLayout)llEsignature.findViewById(R.id.llTrxTypeName);
		tvTrxTypeName= (TextView)llEsignature.findViewById(R.id.tvTrxTypeName);
		tvTrxTypeNameV= (TextView)llEsignature.findViewById(R.id.tvTrxTypeNameV);

		tvSourceSubInventory= (TextView)llEsignature.findViewById(R.id.tvSourceSubInventory);
		tvSourceSubInventoryV  = (TextView)llEsignature.findViewById(R.id.tvSourceSubInventoryV);
		
		tvTotalItemAmt				= (TextView)llEsignature.findViewById(R.id.tvTotalItemAmt);
		tvDscount					= (TextView)llEsignature.findViewById(R.id.tvDscount);
		tvTotalInvoiceAmt			= (TextView)llEsignature.findViewById(R.id.tvTotalInvoiceAmt);
		
		tvTotalItemAmtValue		= (TextView)llEsignature.findViewById(R.id.tvTotalItemAmtValue);
		tvDscountValue			= (TextView)llEsignature.findViewById(R.id.tvDscountValue);
		tvTotalInvoiceAmtValue	= (TextView)llEsignature.findViewById(R.id.tvTotalInvoiceAmtValue);
		
		btnCustomerSignClear		= (Button)llEsignature.findViewById(R.id.btnCustomerSignClear);
		btnPresellerSignClear		= (Button)llEsignature.findViewById(R.id.btnPresellerSignClear);
		LinearLayout llCustomer_Passcode	= (LinearLayout)llEsignature.findViewById(R.id.llCustomer_Passcode);
		LinearLayout llDeliveryDate			= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDate);
		LinearLayout llDeliveryDeliveryTime	= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDeliveryTime);
		
		TextView tvBottomDist	= (TextView)llEsignature.findViewById(R.id.tvBottomDist);
		
		llDeliveryDate.setVisibility(View.VISIBLE);
		llCustomer_Passcode.setVisibility(View.GONE);
		llDeliveryDeliveryTime.setVisibility(View.GONE);
		tvBottomDist.setVisibility(View.INVISIBLE);
		llTotalAmt.setVisibility(View.GONE);
		llTotalPrice.setVisibility(View.GONE);
		tvDiscount.setVisibility(View.INVISIBLE);
		tvInvoiceAmount.setVisibility(View.GONE);
		tvPrice.setVisibility(View.GONE);
		tvTotalPrice.setVisibility(View.GONE);
		tvBottomDist.setVisibility(View.GONE);
		
		tvOrderPreviewunits.setLayoutParams(new LinearLayout.LayoutParams((int)(40 * px ), LayoutParams.WRAP_CONTENT));
		tvDiscount.setLayoutParams(new LinearLayout.LayoutParams((int)(20 * px ), LayoutParams.WRAP_CONTENT));
		
		btnPrintSalesOrderMerchant.setVisibility(View.GONE);
		btnPrintSalesOrder.setVisibility(View.GONE);
		
		showVisibleButton(false);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		tvLu.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
		
		setTypeFace(llEsignature);
		setTypeFace(llMiddleLayout);
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("scroll"))
		{
			if(lvPreviewOrder.isScrolled())
			{
				lvPreviewOrder.setScrolled(false);
				lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount()-1).getTop());
			}
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
		}
		else if(from.equalsIgnoreCase("Task"))
		{
			Intent intent = new Intent(SalesmanLPOOrderPreview.this, TaskToDoActivity.class);
			intent.putExtra("object", mallsDetails);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
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
			super.onBackPressed();
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
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanLPOOrderPreview.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1			  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		Button btn_popup_print		 	  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment	  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_done			  = (Button) view.findViewById(R.id.btn_popup_done);
		
		btn_popup_collectpayment.setVisibility(View.GONE);
		
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				ShowOptionPopupForPrinter(SalesmanLPOOrderPreview.this,new PrintPopup() {
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
								intent=new Intent(SalesmanLPOOrderPreview.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(SalesmanLPOOrderPreview.this, PrinterConnectorArabic.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.LPO_DELIVERY_NOTE);
							intent.putExtra("OrderId", orderDO);
							intent.putExtra("mallsDetails", mallsDetails);
							startActivity(intent);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(SalesmanLPOOrderPreview.this, WoosimPrinterActivity.class);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.LPO_DELIVERY_NOTE);
//				intent.putExtra("OrderId", orderDO);
//				intent.putExtra("mallsDetails", mallsDetails);
//				startActivity(intent);
			}
		});
		
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("payment");
			}
		});

		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("served");
			}
		});
		
		try{
			setTypeFace((LinearLayout)view);
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
}
