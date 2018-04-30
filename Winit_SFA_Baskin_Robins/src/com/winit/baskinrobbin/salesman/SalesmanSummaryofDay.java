package com.winit.baskinrobbin.salesman;

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
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.UploadData;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.baskinrobbin.salesman.dataobject.EOTSummaryPrintDO;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.utilities.UploadSQLite;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SalesmanSummaryofDay extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llSummaryofDay;
	private TextView tvOrderReceived, tvReturnRequest, tvAmountCollected, tvDaySummary, tvUnUploadedData, tvUndeliveredQT, tvReturnOrderQT, tvReplacement;
	private String date="";
	private Button btnOK ,btnUpload, btnStockUnload, btnReturnQT, btnUndeliveredQT, btnInventory, btnPrint; 
	private CommonDA commonDA;
	private MyView myViewManager, myViewDriver;
	private Paint mPaint;
	public static EOTSummaryPrintDO eotSummaryPrintDO;
	
	@Override
	public void initialize() 
	{
		//inflate the summary-of-day layout
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.summaryofday, null);
		llBody.addView(llSummaryofDay,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		blockMenu();
//		LogUtils.errorLog("", "8 "+preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false));
		
		if(getIntent().getExtras() != null)
			date			=	getIntent().getExtras().getString("dateofJorney");
		
		//Initialization of business layer
		commonDA = new CommonDA();
		
		//Initialing all the controls
		intialiseControls();
		
		setTypeFace(llSummaryofDay);
		
		tvUnUploadedData.setVisibility(View.GONE);
		btnStockUnload.setVisibility(View.VISIBLE);
		tvUndeliveredQT.setVisibility(View.VISIBLE);	
		tvReturnOrderQT.setVisibility(View.GONE);
		tvReplacement.setVisibility(View.GONE);
		btnOK.setVisibility(View.GONE);
		
		//getting current time here
		String date1[]	=	date.split("-");
		tvDaySummary.setText("Summary of "+CalendarUtils.getMonthAsString((StringUtils.getInt(date1[1])-1))+""+date1[2]+CalendarUtils.getDateNotation(StringUtils.getInt(date1[2]))+", "+date1[0]);
		tvOrderReceived.setText("Order Summary");
		
		tvUnUploadedData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SalesmanSummaryofDay.this, UnloadDataActivity.class);
				startActivity(intent);
			}
		});
		
		tvOrderReceived.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				((TextView)v).setClickable(false);
				Intent intent = new Intent(SalesmanSummaryofDay.this, OrderSummary.class);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run()
					{
						((TextView)v).setClickable(true);
					}
				}, 500);
			}
		});
		
		tvReturnRequest.setVisibility(View.GONE);
		tvAmountCollected.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				((TextView)v).setClickable(false);
				
				Intent intent = new Intent(SalesmanSummaryofDay.this, CustomerInvoiceActivity.class);
				intent.putExtra("Ispreseller", true);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run()
					{
						((TextView)v).setClickable(true);
					}
				}, 500);
			}
		});
		btnOK.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
				setResult(2000);
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override 
			public void onClick(View v)
			{
				finish();
				setResult(2000);
			}
		});
		
		if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		{
			btnUpload.setVisibility(View.GONE);
			btnStockUnload.setVisibility(View.GONE);
		}
		else
		{
			btnUpload.setVisibility(View.VISIBLE);
			btnStockUnload.setVisibility(View.VISIBLE);
		}
		
		btnUpload.setText(" Submit EOT ");
		btnUpload.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				String order_ID = commonDA.isHoldOrderIsThere(CalendarUtils.getOrderPostDate());
				
				if(TextUtils.isEmpty(order_ID))
        		{
    				btnUpload.setEnabled(false);
    				btnUpload.setClickable(false);
    				showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Are you sure you want to submit EOT?", getString(R.string.Yes), getString(R.string.No), "EOTSubmit");
        		}
				else
        			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Hold Order no : "+order_ID +" not confirmed, please go to order summary and confirm.", getString(R.string.OK), "SKIP", "SKIP");
			}
		});
		
		btnStockUnload.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SalesmanSummaryofDay.this, ReturnStockOption.class);
				startActivity(intent);
			}
		});
		
		tvUndeliveredQT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SalesmanSummaryofDay.this, SalesmanVanStock.class);
				startActivity(intent);
			}
		});	
		
		btnPrint.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						loadPrintData();
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								hideLoader();
								ShowOptionPopupForPrinter(SalesmanSummaryofDay.this,new PrintPopup() {
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
												intent=new Intent(SalesmanSummaryofDay.this, WoosimPrinterActivity.class);
											else if(selectedPrinter==AppConstants.DOTMATRIX)
												intent=new Intent(SalesmanSummaryofDay.this, PrinterConnectorArabic.class);
											intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_EOT_SUMMARY);
											intent.putExtra("EOTSummaryPrintDO", eotSummaryPrintDO);
											startActivityForResult(intent, 1000);
											startActivity(intent);
											hideLoader();
										}



									}
								});
//								Intent intent = new Intent(SalesmanSummaryofDay.this, WoosimPrinterActivity.class);
//								intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_EOT_SUMMARY);
//								intent.putExtra("EOTSummaryPrintDO", eotSummaryPrintDO);
//								startActivityForResult(intent, 1000);
							}
						});
					}
				}).start();
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
        
        if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
        	tvAmountCollected.setVisibility(View.GONE);
	}
	
	
	public void intialiseControls()
	{
		//getting Id's of TextView
		tvDaySummary		=	(TextView)llSummaryofDay.findViewById(R.id.tvDaySummary);
		tvOrderReceived		=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderReceived);
		tvReturnRequest		=	(TextView)llSummaryofDay.findViewById(R.id.tvReturnRequest);
		tvAmountCollected	=	(TextView)llSummaryofDay.findViewById(R.id.tvAmountCollected);
		tvUnUploadedData	=	(TextView)llSummaryofDay.findViewById(R.id.tvUnUploadedData);
		tvUndeliveredQT		=	(TextView)llSummaryofDay.findViewById(R.id.tvUndeliveredQT);
		tvReturnOrderQT		=	(TextView)llSummaryofDay.findViewById(R.id.tvReturnOrderQT);
		tvReplacement		=	(TextView)llSummaryofDay.findViewById(R.id.tvReplacement);
		//getting Id's of Button
		btnOK				=	(Button)llSummaryofDay.findViewById(R.id.btnOK);
		btnUpload			=	(Button)llSummaryofDay.findViewById(R.id.btnUpload);
		btnReturnQT			=	(Button)llSummaryofDay.findViewById(R.id.btnReturnQT);
		btnUndeliveredQT	=	(Button)llSummaryofDay.findViewById(R.id.btnUndeliveredQT);	
		btnStockUnload		=	(Button)llSummaryofDay.findViewById(R.id.btnStockUnload);
		btnInventory		=	(Button)llSummaryofDay.findViewById(R.id.btnInventory);
		
		btnPrint			=	(Button)llSummaryofDay.findViewById(R.id.btnPrint);
		
		tvReturnRequest.setText("Goods Return Report");
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_GOTO_JOURNEY);
			sendBroadcast(intentBrObj);
		}
		else
		{
			setResult(2000);
			finish();
		}
		if(btnUpload != null)
		{
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
		}
	}
	@Override
	public void onConnectionException(Object msg) 
	{
	}
	
	public boolean uploadEot(String signature)
	{
		String empID = preference.getStringFromPreference(Preference.EMP_NO, "");
        final ConnectionHelper connectionHelper 		= new ConnectionHelper(SalesmanSummaryofDay.this);
        //start
        String strEOTType  		= 	preference.getStringFromPreference("EOTType", "");
        String strReason   		= 	preference.getStringFromPreference("EOTReason", "");
        String strDateTime 		= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
        boolean response = false;	
        if(isNetworkConnectionAvailable(SalesmanSummaryofDay.this))
        {
        	showLoader("Submitting EOT...");
        	String holdOrders= "";
        	String OrderId   			= 	commonDA.isAllOrderPushed(empID);
        	String paymentId 			= 	commonDA.isAllPaymentPushed(empID);
        	Vector<String> vecHoldOrder = 	commonDA.getOnHoldOrderToDeliverNew(empID);
        	
        	if(vecHoldOrder != null && vecHoldOrder.size() > 0)
        	{
        		for(String str : vecHoldOrder)
        			holdOrders = holdOrders+str+",";
        		
        		if(holdOrders.contains(","))
        			holdOrders = holdOrders.substring(0, holdOrders.lastIndexOf(","));
        	}
        	
        	if(TextUtils.isEmpty(OrderId) && TextUtils.isEmpty(paymentId) && TextUtils.isEmpty(holdOrders))
        	{
//        		new UploadSQLite().uploadSQLite(SalesmanSummaryofDay.this, ServiceURLs.UploadSQLiteAfterEOT, null);
        		uploadDatabaseIntoServer(ServiceURLs.UploadSQLiteAfterEOT);
        		response = connectionHelper.sendRequest_Bulk(SalesmanSummaryofDay.this, BuildXMLRequest.insertEOT(preference.getStringFromPreference(Preference.EMP_NO,""), strEOTType, strReason, strDateTime, "","","",signature), ServiceURLs.INSERT_EOT, preference);
				
            	if(response)
            	{
            		runOnUiThread(new Runnable()
            		{
            			@Override
            			public void run() 
            			{
            				preference.saveBooleanInPreference(Preference.IS_EOT_DONE, true);
            				preference.commitPreference();
            				btnUpload.setVisibility(View.GONE);
            				btnStockUnload.setVisibility(View.GONE);
            				btnPrint.setVisibility(View.VISIBLE);
            				showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.successful), "EOT has been submitted successfully.", getString(R.string.OK), null, "EOT", false);
            			}
            		});
            	}
            	else
            		showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "EOT has not been submitted, please try again.", getString(R.string.OK), null, "");
        	}
        	else
        	{
        		if(!TextUtils.isEmpty(OrderId))
        			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), OrderId+" order(s) are not submitted, please go to settings and upload the data.", getString(R.string.OK), null, "");
        		else if(!TextUtils.isEmpty(paymentId))
        			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), paymentId+" receipt(s) are not submitted, please go to settings and upload the data.", getString(R.string.OK), null, "");
        		else if(!TextUtils.isEmpty(holdOrders))
        			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), holdOrders+" hold order(s) are not submitted, please go to settings and upload the data.", getString(R.string.OK), null, "");
        	}
        }
        return response;
	}
	
	@Override
	public void onButtonNoClick(String from) 
	{
		super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("EOTSubmit"))
		{
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
		}
		else if(from.equalsIgnoreCase("SKIP"))
		{
			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Are you sure you want to submit EOT?", getString(R.string.Yes), getString(R.string.No), "EOTSubmit");
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		
		if(from.equalsIgnoreCase("finisheot"))
		{
			finish(); 
		}
		else if(from.equalsIgnoreCase("movement"))
		{
			Intent intent = new Intent(SalesmanSummaryofDay.this, LoadRequestActivity.class);
			intent.putExtra("load_type", AppStatus.UNLOAD_STOCK);
			intent.putExtra("isSummary", true);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("signature"))
		{
			Intent obIntent = new Intent();
			obIntent.setAction(AppConstants.ACTION_GOTO_HOME1);
			sendBroadcast(obIntent);
		}
		else if(from.equalsIgnoreCase("EOTSubmit"))
		{
			showSignatureDialog(); 
		}
	}
	@Override
	protected void onResume() 
	{
		super.onResume();
		if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		{
			btnUpload.setVisibility(View.GONE);
			btnPrint.setVisibility(View.VISIBLE);
			if(!preference.getbooleanFromPreference(Preference.SIGNATURE+CalendarUtils.getOrderPostDate(), false))
				showSignatureDialog();
		}
	}
	
	private String filepath =null, filepathDriver = null;
	private boolean isVarifcationSignatureDone = false, isVarSignDoneDriver = false;
	private String filePath;
	
	private void showSignatureDialog()
	{
		final Dialog dialog 			= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		final LinearLayout llSignSupervisor = (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout)llSignature.findViewById(R.id.llSignDriver);
	
		Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnSKCear 				= (Button)llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear 			= (Button)llSignature.findViewById(R.id.btnDriverCear);
		
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
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!isVarifcationSignatureDone && !isVarSignDoneDriver)
				{
					showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
				}
				else if(!isVarifcationSignatureDone)
					showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
				else if(!isVarSignDoneDriver)
					showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Please sign before submitting the stock verification.", "OK", null, "");
				else	
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							Bitmap bitmap = getBitmap(myViewManager);
							if(bitmap != null)
							{
								filepath = BitmapsUtiles.saveVerifySignature(bitmap);
							}
							
							bitmap = getBitmap(myViewDriver);
							if(bitmap != null)
								filepathDriver = BitmapsUtiles.saveVerifySignature(bitmap);
							
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dialog.dismiss();

									Intent intent = new Intent(SalesmanSummaryofDay.this, OdometerReadingActivity.class);
									intent.putExtra("isStartDay", false);
									intent.putExtra("image_path", filepath);
									intent.putExtra("image_path_driver",filepathDriver);
									startActivityForResult(intent, 5000);
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnDriverCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
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
				if(myViewManager != null)
					myViewManager.clearCanvas();
			}
		});
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() 
			{
				btnUpload.setEnabled(true);
				btnUpload.setClickable(true);
			}
		},500);
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
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
	
	private void performEOT()
	{
		if(isNetworkConnectionAvailable(SalesmanSummaryofDay.this))
		{
			showLoader("This may take time please wait...");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
					UploadData.setListener(new UploadDataListener()
					{
						@Override
						public void updateStatus(int status, String message)
						{
							showLoader(message);
							if(status == UploadData.END)
							{
								UploadData.setListener(null);
								final boolean isUpdated = uploadEot("");
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										if(isUpdated)
										{
											preference.saveBooleanInPreference(Preference.SIGNATURE+CalendarUtils.getOrderPostDate(), true);
											preference.commitPreference();
										}
										btnUpload.setEnabled(true);
										btnUpload.setClickable(true);
										hideLoader();
									}
								});
							}
						}
					});
				}
			}).start();
		}
		else
		{
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
			showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "EOT is not submitted due to problem in network connection. Please check your internet connection and try again.", getString(R.string.OK), null, "finisheot");
			preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
			preference.commitPreference();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 5000)
			performEOT();
		else
		{
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
		}
	}
	
	private void loadPrintData()
	{
		
		//get the start time and end time from the tbljourney
		//String journeyCode=preference.getStringFromPreference(Preference.EMP_NO, "")+CalendarUtils.getCurrentDateAsString();
		String UserCode=preference.getStringFromPreference(Preference.EMP_NO, "");
	//	String dates[]=new JourneyPlanDA().getStartEndtime(journeyCode);
		String dates[]=new JourneyPlanDA().getStartEndtime(UserCode);
		//String strSelectedDate 					= 	CalendarUtils.getEndDate();
		
		CommonDA commonDA 						= 	new CommonDA(); 
		eotSummaryPrintDO 						= 	new EOTSummaryPrintDO();
		eotSummaryPrintDO.hmOrders	 			= 	commonDA.getDeliveryStatusOrderList(dates[0],dates[1], null);
		eotSummaryPrintDO.hmPayments 			= 	new PaymentSummeryDA().getCustomerInvoice(dates[0],dates[1], null);
		eotSummaryPrintDO.vecNonInventoryItems 	= 	new OrderDetailsDA().getReturnInventoryQtyNew(diffStock, diffAmt);
		//eotSummaryPrintDO.vecInventoryItems 	= 	new OrderDetailsDA().getInventoryQty(CalendarUtils.getOrderPostDate());
		eotSummaryPrintDO.vecInventoryItems 	= 	new OrderDetailsDA().getInventoryQty();
		eotSummaryPrintDO.vecReplaceOrder 		= 	new OrderDetailsDA().getOrderDetailsnew(dates[0],dates[1]);
		
		/*eotSummaryPrintDO.hmOrders	 			= 	commonDA.getDeliveryStatusOrderList(strSelectedDate, null);
		eotSummaryPrintDO.hmPayments 			= 	new PaymentSummeryDA().getCustomerInvoice(strSelectedDate, null);
		eotSummaryPrintDO.vecInventoryItems 	= 	new OrderDetailsDA().getInventoryQty(CalendarUtils.getOrderPostDate());
		eotSummaryPrintDO.vecNonInventoryItems 	= 	new OrderDetailsDA().getReturnInventoryQtyNew(diffStock, diffAmt);
		eotSummaryPrintDO.vecReplaceOrder 		= 	new OrderDetailsDA().getOrderDetails(null, CalendarUtils.getCurrentDateAsString());*/
	}
}
