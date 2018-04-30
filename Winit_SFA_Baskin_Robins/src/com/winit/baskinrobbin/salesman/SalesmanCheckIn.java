package com.winit.baskinrobbin.salesman;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Vector;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.CustomerPendingInvoiceParser;
import com.winit.baskinrobbin.parsers.GetAllDeleteLogsParser;
import com.winit.baskinrobbin.salesman.chart.BarChartDemo01View;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.CustomerVisitDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.SalesTargetDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.map.RouteMapActivity;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SalesmanCheckIn extends BaseActivity implements LocationResult
{
	//declaration of variables
	private LinearLayout llMain, llMap, llGraphView,CutomerTypeInfo, llCreditLimit,llOverDueAmount,llOSPLimit,llAvailableLimit,llGraphLayout, llPaymentTerm;
	private Button btnCheckIn, btnFindRoute, btnEditDetail;
	private TextView tvtitle, tvCustomerAddress, tvCustomerName, tvCustomerCreditAvail,  tvCustomerCredit,  tvPaymentType, 
					 tvPaymentTermDesc,tvCustomerCreditAvailValue,tvCustomerOutStandingBalance, tvCustomerCreditValue,
					 tvCustomerOutStandingBalanceValue, tvHeadTitleHistory, tvTargetValue, tvAchievedValue,tvOverDueAmount,
					 tvOverDueAmountValue, tvOverDueAmountAed, tvPaymentTermCode, tv_storegrowthpercentage, tv_channelName, tv_paymentName, 
					 tv_storegrowthmonthly, tv_creditAed,tv_availAed,tv_outsatndAed, tvCustomerSiteId;
	private TextView tvCustomerChannel;
	private JourneyPlanDO object;
	private LocationUtility locationUtility;
	private Vector<SalesTargetDO> vecTarget;
	private String strTypeOfCall = "";
	private boolean isLocationCapture = false;
	
	@Override
	public void initialize() 
	{
		//inflate the list-item-detail-view layout
		llMain 		= (LinearLayout)inflater.inflate(R.layout.checkin_new, null);
		llBody.addView(llMain,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(btnCheckOut != null)
		{
			ivLogOut.setVisibility(View.GONE);
			btnCheckOut.setVisibility(View.GONE);
		}
		
		locationUtility  = new LocationUtility(SalesmanCheckIn.this);
		
		intialiseControls();	
		setTypeFace(llMain);
		if(getIntent().getExtras() != null)
		{
			object	            	= 	(JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
			object.reasonForSkip 	= 	getIntent().getExtras().getString("reason");
			
			if(getIntent().getExtras().containsKey("strTypeOfCall"))
				strTypeOfCall = getIntent().getExtras().getString("strTypeOfCall");
			else 
				strTypeOfCall = "";
		}
		
		tvtitle.setText("Checkin");
		if(object != null)
		{
			if(TextUtils.isEmpty(object.channelCode))
				object.channelCode = "N/A";
			
			if(TextUtils.isEmpty(object.customerType))
				object.customerType = "N/A";
			
			
			tv_channelName.setText(object.channelCode);
			tv_paymentName.setText(object.customerType+"");
			tvCustomerName.setText(object.siteName + " (" +object.partyName+")");
			
			tvCustomerSiteId.setText("Site Id: "+object.site);
			
			tvCustomerAddress.setText(object.addresss1 + "\n" + object.addresss2 + "\n" + object.addresss3);
			
			if(object.channelCode == null || object.channelCode.equalsIgnoreCase(""))
				object.channelCode = "N/A";
			tvCustomerChannel.setText(object.channelCode);
			
			if(object.paymentTermCode == null || object.paymentTermCode.equalsIgnoreCase(""))
				object.paymentTermCode = "N/A";
			tvPaymentTermCode.setText(object.paymentTermCode);
		}
		
		llGraphLayout.setVisibility(View.GONE);	
		if(object.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			CutomerTypeInfo.setVisibility(View.GONE);
			llCreditLimit.setVisibility(View.GONE);
			
			llOSPLimit.setVisibility(View.GONE);
			llAvailableLimit.setVisibility(View.GONE);	
			
		}
		
		if(object.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			llOverDueAmount.setVisibility(View.GONE);
			llCreditLimit.setVisibility(View.GONE);
			llAvailableLimit.setVisibility(View.GONE);
			llPaymentTerm.setVisibility(View.GONE);
			llOSPLimit.setVisibility(View.GONE);
		}
		
		btnCheckIn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				disableView(v);
				//Live
				if(isNetworkConnectionAvailable(SalesmanCheckIn.this) || isGPSEnable(SalesmanCheckIn.this))
				{
					isLocationCapture = false;
					showLoader("Validating your current location !","Checking you in !");
					locationUtility .getLocation(SalesmanCheckIn.this);
					
					performCheckinAction();
				}
				else
					showCustomDialog(SalesmanCheckIn.this, "Warning !", "GPS settings are not enabled, Please check your GPS settings and try again.", "OK", null, "");
			}
		});
		
		btnFindRoute.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Need to work on it
				Intent intent = new Intent(SalesmanCheckIn.this, RouteMapActivity.class);
				intent.putExtra("Latitude", object.geoCodeX);
				intent.putExtra("Longitude", object.geoCodeY);
				startActivity(intent);
			}
		});
		
		btnEditDetail.setVisibility(View.GONE);
		btnEditDetail.setText(" Update Location ");
		btnEditDetail.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				//Need to work on it
				Intent intent = new Intent(SalesmanCheckIn.this, UpdateCustomerDetail.class);
				intent.putExtra("mallsDetails", object);
				startActivityForResult(intent, 5000);
			}
		});
		
		llGraphView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showZoomPopup();
			}
		});
		
		startLocationUpdates();
		loadData();
	}
	
	private void syncPendingInvoices()
	{
		
		try 
		{
			LogUtils.errorLog("syncPendingInvoices","syncPendingInvoices start");
			if(isNetworkConnectionAvailable(SalesmanCheckIn.this))
			{
				showLoader(getString(R.string.sync_pending_invoices));
				ConnectionHelper connectionHelper= new ConnectionHelper(null);
			//	CustomerPendingInvoiceParser pendingInvoiceParser = new CustomerPendingInvoiceParser(SalesmanCheckIn.this,object.site);
				CustomerPendingInvoiceParser pendingInvoiceParser = new CustomerPendingInvoiceParser(SalesmanCheckIn.this,object.customerId);
				connectionHelper.sendRequest(SalesmanCheckIn.this,BuildXMLRequest.getPendingInvoicesFromSAP(object.site,"",user_id),pendingInvoiceParser, ServiceURLs.GetPendingInvoiceDataByCode,preference);		
			}
			LogUtils.errorLog("syncPendingInvoices","syncPendingInvoices end");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	
	private void startLocationUpdates()
	{
		if(checkPlayStoreAvailability())
			showCustomDialog(SalesmanCheckIn.this,getString(R.string.warning),"Unable to fetch your location. Please enable location access and network.", "Turn on GPS","No", "GpsTurnOn");
	}
	
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("GpsTurnOn"))
			enableLocationSettings();
	}
	
	public void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(settingsIntent, 112);
	}
	
	
	public boolean checkPlayStoreAvailability()
	{
        // If Google Play services is available
        if (isNetworkConnectionAvailable(SalesmanCheckIn.this) || isGPSEnable(SalesmanCheckIn.this))
            return false;
        else 
            return true;
	}
	
	protected void performCheckinAction() 
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if(!isLocationCapture)
				{
					isLocationCapture = true;
					locationUtility.stopGpsLocUpdation();
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							performCheckIn();
						}
					});
				}
			}
		}, 30000);		
	//}, 80000);		
	}

	public void getCustomerLimit()
	{
//		new Thread(new Runnable() 
//		{
//			@Override
//			public void run() 
//			{
				final CustomerCreditLimitDo creditLimit = new CustomerDA().getCustomerCreditLimit(object);
				final float overdue 		= 	new CustomerDA().getOverDueAmount(object);
				final float customerGrowth 	=  	new CustomerDA().getCustomerStoreGrowth(object.customerId);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						tvCustomerCreditValue.setText(diffAmt.format(StringUtils.getFloat(creditLimit.creditLimit))+"");
						tvCustomerCreditAvailValue.setText(diffAmt.format(StringUtils.getFloat(creditLimit.availbleLimit))+"");
						tvCustomerOutStandingBalanceValue.setText(diffAmt.format(StringUtils.getFloat(creditLimit.outStandingAmount))+"");
						tvOverDueAmountValue.setText(""+diffAmt.format(overdue));
						tv_storegrowthpercentage.setText(customerGrowth+"%");
						
						if(object.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
						{
							tvCustomerOutStandingBalanceValue.setText("N/A");
							tvCustomerCreditAvailValue.setText("N/A");
							tvCustomerCreditValue.setText("N/A");
							tvOverDueAmountValue.setText("N/A");
							tv_creditAed.setText("");
							tv_availAed.setText("");
							tv_outsatndAed.setText("");
							tvOverDueAmountAed.setText("");
						}
						else
						{
							tv_creditAed.setText(curencyCode);
							tv_availAed.setText(curencyCode);
							tv_outsatndAed.setText(curencyCode);
							tvOverDueAmountAed.setText(curencyCode);

							
						}
					}
				});
//			}
//			
//		}).start();
	}
	
	public void performCheckIn()
	{
		object.ActualArrivalTime = CalendarUtils.getCurrentDateTime();
		object.reasonForSkip = object.reasonForSkip;
		
		if(!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
			showLoader("Validating Credit Limit");
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					double outletLat = StringUtils.getDouble(object.geoCodeX);
					double outletLon = StringUtils.getDouble(object.geoCodeY);
//					if(LocationUtils.getDist(outletLat, outletLon, preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.261987), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.398717)) > 2000)
//					{
//						hideLoader();
//						showCustomDialog(SalesmanCheckIn.this, "Warning !", "Current location is not matching with customer location, please try again.", "OK", "Try again", "CheckInHere");
//					}
//					else
					{
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								object.JourneyCode = preference.getStringFromPreference(Preference.USER_ID, "")+CalendarUtils.getOrderPostDate();
								object.VisitCode   = preference.getStringFromPreference(Preference.USER_ID, "")+object.site+CalendarUtils.getCurrentDateTime();
								//to save checked in customer detail in preference for future use
								saveCustomerDetail();
								new CustomerDA().updateLastJourneyLog();
								insertCustomerVisit();
								
								preference.saveStringInPreference(Preference.CUSTOMER_NAME, object.siteName + " (" +object.partyName+")");
								preference.saveStringInPreference(Preference.LAST_CUSTOMER_SITE_ID, object.site);
								preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, object.site);
								preference.commitPreference();
								object.reasonForSkip = "";
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										hideLoader();
										if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_AM))
										{
											Intent intent = new Intent(SalesmanCheckIn.this, CustomerAssetsListActivity.class);
											intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
											intent.putExtra("mallsDetails",object);
											startActivity(intent);
											finish();
										}
										else
										{
											Intent intent = new Intent(SalesmanCheckIn.this, CheckInOptionActivity.class);
											intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
											intent.putExtra("mallsDetails",object);
											intent.putExtra("from", "checkin");
											startActivity(intent);
											finish();
										}
									}
								});
							}
						}).start();
					}
				} 
				catch (Exception e)
				{
					e.printStackTrace();	
				}
			}
		},1000);
	}
	public void saveCustomerDetail()
	{
		if(object != null)
		{
			AppConstants.hmCapturedInventory = null;
			AppConstants.VendingMachineName =  object.siteName;
			preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, object.site);
			preference.commitPreference();
		}
	}
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		btnCheckIn						=	(Button)llMain.findViewById(R.id.btnCheckIn);
		btnFindRoute					=	(Button)llMain.findViewById(R.id.btnFindRoute);
		btnEditDetail					=	(Button)llMain.findViewById(R.id.btnEditDetail);
		tvtitle          			 	=	(TextView)llMain.findViewById(R.id.tvHeadTitle);
		tvCustomerName                  =	(TextView)llMain.findViewById(R.id.tvCustomerMallName);
		tvCustomerAddress				=	(TextView)llMain.findViewById(R.id.tvCustomerAddress);
		tvCustomerCredit				=	(TextView)llMain.findViewById(R.id.tvCustomerCredit);
		tvCustomerOutStandingBalance	=	(TextView)llMain.findViewById(R.id.tvCustomerOutStandingBalance);
		tvPaymentType					=	(TextView)llMain.findViewById(R.id.tvPaymentType);
		tvPaymentTermDesc				=	(TextView)llMain.findViewById(R.id.tvPaymentTermDesc);
		llGraphView 					= (LinearLayout) llMain.findViewById(R.id.llGraphView);
		tvCustomerCreditValue				=	(TextView)llMain.findViewById(R.id.tvCustomerCreditValue);
//		tvPaymentTypeValue					=	(TextView)llMain.findViewById(R.id.tvPaymentTypeValue);
//		tvPaymentTermDescValue				=	(TextView)llMain.findViewById(R.id.tvPaymentTermDescValue);
		tvCustomerOutStandingBalanceValue 	=	(TextView)llMain.findViewById(R.id.tvCustomerOutStandingBalanceValue);
//		tvsamestore                         =	(TextView)llMain.findViewById(R.id.tvsamestore);
//		tvsamestoreValue                    =	(TextView)llMain.findViewById(R.id.tvsamestoreValue);
		tvCustomerCreditAvail				=	(TextView)llMain.findViewById(R.id.tvCustomerCreditAvail);
		tvCustomerCreditAvailValue			=	(TextView)llMain.findViewById(R.id.tvCustomerCreditAvailValue);
		tvOverDueAmount						=	(TextView)llMain.findViewById(R.id.tvOverDueAmount);
		tvOverDueAmountValue				=	(TextView)llMain.findViewById(R.id.tvOverDueAmountValue);
		tvOverDueAmountAed					=	(TextView)llMain.findViewById(R.id.tvOverDueAmountAed);
		tvHeadTitleHistory					=	(TextView)llMain.findViewById(R.id.tvHeadTitleHistory);
		tvPaymentTermCode					=	(TextView)llMain.findViewById(R.id.tvPaymentTermCode);
		tv_storegrowthpercentage			=	(TextView)llMain.findViewById(R.id.tv_storegrowthpercentage);
		
		tvTargetValue						=	(TextView)llMain.findViewById(R.id.tvTargetValue);
		tvAchievedValue						=	(TextView)llMain.findViewById(R.id.tvAchievedValue);
		
		tvCustomerChannel 					=   (TextView)llMain.findViewById(R.id.tvCustomerChannel);	 
		CutomerTypeInfo						=   (LinearLayout)llMain.findViewById(R.id.CutomerTypeInfo);	
		llCreditLimit						=   (LinearLayout)llMain.findViewById(R.id.llCreditLimit);
		llOverDueAmount						=   (LinearLayout)llMain.findViewById(R.id.llOverDueAmount);
		llOSPLimit							=   (LinearLayout)llMain.findViewById(R.id.llOSPLimit);
		llAvailableLimit					=   (LinearLayout)llMain.findViewById(R.id.llAvailableLimit);
		llGraphLayout						=   (LinearLayout)llMain.findViewById(R.id.llGraphLayout);
		tv_creditAed						=   (TextView)llMain.findViewById(R.id.tv_creditAed);
		tv_availAed							=   (TextView)llMain.findViewById(R.id.tv_availAed);
		tv_outsatndAed						=   (TextView)llMain.findViewById(R.id.tv_outsatndAed);
		
		tv_channelName						=	(TextView)llMain.findViewById(R.id.tv_channelName);
		tv_paymentName						=	(TextView)llMain.findViewById(R.id.tv_paymentName);
		tv_storegrowthmonthly				=	(TextView)llMain.findViewById(R.id.tv_storegrowthmonthly);
		
		tvCustomerSiteId					=	(TextView)llMain.findViewById(R.id.tvCustomerSiteId);
		
		llPaymentTerm =	(LinearLayout)llMain.findViewById(R.id.llPaymentTerm);
		
		llMap =	(LinearLayout)llMain.findViewById(R.id.llMap);
		btnFindRoute.setVisibility(View.VISIBLE);
		
		llGraphView.setDrawingCacheEnabled(true);
		llGraphView.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
	}
	
	private void loadData(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				syncPendingInvoices();
				showLoader(getString(R.string.please_wait));
				getCustomerLimit();
				getGEOCodes();
				vecTarget 		= new Vector<SalesTargetDO>();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						TextView a6	= (TextView) llMain.findViewById(R.id.a1);
						TextView a7	= (TextView) llMain.findViewById(R.id.a3);
						
						a6.setBackgroundColor(Color.parseColor("#0A94D1"));
						a7.setBackgroundColor(Color.parseColor("#545454"));
						
						final BarChartDemo01View mView = new BarChartDemo01View(SalesmanCheckIn.this, vecTarget, "Months");
					
						llGraphView.addView(mView);
						hideLoader();
					}
				});
			}
		}).start();
	}
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			if(!isLocationCapture)
			{
				isLocationCapture = true;
				locationUtility.stopGpsLocUpdation();
				DecimalFormat df = new DecimalFormat("#.#######");
				
//				String lat  = df.format(loc.getLatitude());
//				String lang = df.format(loc.getLongitude());
				String lat  = loc.getLatitude()+"";
				String lang =  loc.getLongitude()+"";

				preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, ""+Double.parseDouble(lat));
				preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, ""+Double.parseDouble(lang));
				preference.commitPreference();
				
				if(StringUtils.getFloat(object.geoCodeX) <= 0 || StringUtils.getFloat(object.geoCodeY) <= 0)
					updateGOLOcation(object.site, ""+lat, ""+lang);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						performCheckIn();
					}
				});
			}
		}
	}

	@Override
	public void onConnectionException(Object msg) 
	{	
	}
	public class StartTimer extends CountDownTimer
	{
		public StartTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			hideLoader();
			locationUtility.stopGpsLocUpdation();
		}
		@Override
		public void onTick(long millisUntilFinished) 
		{
			LogUtils.errorLog("millisUntilFinished", ""+millisUntilFinished);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 5000 && resultCode == 5000)
			finish();
	}
	
	int countClick = 0;
	Bitmap bmp;
	private void showZoomPopup()
	{
		countClick++;
		if(countClick == 2)
		{
			llGraphView.setClickable(false);
			llGraphView.setEnabled(false);
			countClick = 0;
			try
			{
				bmp = llGraphView.getDrawingCache(true);
				File f = new File(Environment.getExternalStorageDirectory(),"graph.jpg");
		        FileOutputStream out = new FileOutputStream(f);
		        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
		        
		        if(bmp != null)
		        	bmp.recycle();
			}
			catch (Exception e) {
			}
			
			LayoutInflater inflater =getLayoutInflater();
			LinearLayout llGraphPopup = (LinearLayout) inflater.inflate(R.layout.graph_popup, null);
			
			final Dialog dialog = new Dialog(SalesmanCheckIn.this,R.style.Dialog);
			dialog.setContentView(llGraphPopup, new LayoutParams(preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH",320), preference.getIntFromPreference("DEVICE_DISPLAY_HEIGHT",480) - 100));
		
			WebView mWebView = (WebView) llGraphPopup.findViewById(R.id.mWebView);
			mWebView.getSettings().setAllowFileAccess(true);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setBuiltInZoomControls(true);
			mWebView.getSettings().setLoadWithOverviewMode(true);
			mWebView.getSettings().setUseWideViewPort(true);
			mWebView.setInitialScale(100);
		        
			String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			String imagePath = "file://"+ base + "/graph.jpg";
			String html = "<html><head></head><body><img src=\""+ imagePath + "\"></body></html>";
			mWebView.loadDataWithBaseURL("", html, "text/html","utf-8", "");
			
			ImageView ivCancel = (ImageView) llGraphPopup.findViewById(R.id.ivCancel);
			TextView tvGraphTitle = (TextView) llGraphPopup.findViewById(R.id.tvGraphTitle);
			
			tvGraphTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			ivCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
		
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() {
				countClick = 0;
				llGraphView.setClickable(true);
				llGraphView.setEnabled(true);
			}
		}, 300);
	}
	
	private void insertCustomerVisit()
	{
		CustomerVisitDO visitDO 		= new CustomerVisitDO();
		visitDO.CustomerVisitId			= StringUtils.getUniqueUUID();
		visitDO.UserCode				= preference.getStringFromPreference(Preference.EMP_NO, "");
		visitDO.JourneyCode				= object.JourneyCode;
		visitDO.VisitCode				= object.VisitCode;
		visitDO.ClientCode				= object.site;
		visitDO.Date					= CalendarUtils.getCurrentDateTime();
		visitDO.ArrivalTime				= CalendarUtils.getCurrentDateTime();
		visitDO.OutTime					= "";
		visitDO.TotalTimeInMins			= "";
		visitDO.Latitude				= ""+preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0);
		visitDO.Longitude				= ""+preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0);
		visitDO.CustomerVisitAppId		= visitDO.CustomerVisitId;
		visitDO.IsProductiveCall		= "";
		visitDO.TypeOfCall				= strTypeOfCall;
		visitDO.Status					= "";
		
		visitDO.vehicleNo				= ""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		visitDO.UserId					= ""+preference.getStringFromPreference(Preference.EMP_NO, "");
		
		new CustomerDA().insertCustomerVisits(visitDO);
	}
	
	private void updateGOLOcation(final String siteId, final String lat, final String lang)
	{
		if(isNetworkConnectionAvailable(SalesmanCheckIn.this))
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					object.geoCodeX = lat;
					object.geoCodeY = lang;
					ConnectionHelper connectionHelper = new ConnectionHelper(null);
			    	boolean isuploaded = connectionHelper.sendRequest_Bulk(SalesmanCheckIn.this,BuildXMLRequest.updateCustomerGeoLocation(object.site, lat, lang),  ServiceURLs.UpdateClientLocation, preference);
			    	
			    	if(isuploaded)
			    		new CustomerDetailsDA().updateCustomerSiteGEOLocation(object);
				}
			}).start();
		}
	}
	
	
	private void getGEOCodes()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(StringUtils.getFloat(object.geoCodeX) <= 0 || StringUtils.getFloat(object.geoCodeY) <= 0)
					new CustomerDetailsDA().getGEOBySiteID(object);
			}
		}).start();
	}
}
