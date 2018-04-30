package com.winit.baskinrobbin.salesman;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.parsers.GetAllMovements;
import com.winit.baskinrobbin.parsers.GetCorrectErpApproved;
import com.winit.baskinrobbin.parsers.HoldOrderStatusParser;
import com.winit.baskinrobbin.parsers.ImageUploadParser;
import com.winit.baskinrobbin.parsers.VersionCheckingHandler;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.FilesStorage;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.SyncData;
import com.winit.baskinrobbin.salesman.common.SyncData.SyncProcessListner;
import com.winit.baskinrobbin.salesman.common.UploadData;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataobject.CheckVersionDO;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.ItemWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.SettingsDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.listeners.MakePaymentListner;
import com.winit.baskinrobbin.salesman.listeners.VersionChangeListner;
import com.winit.baskinrobbin.salesman.utilities.BitmapConvertor;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.FileUtils;
import com.winit.baskinrobbin.salesman.utilities.FileUtils.DownloadListner;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.utilities.NumberToArabic;
import com.winit.baskinrobbin.salesman.utilities.OnMonochromeCreated;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.utilities.ZipUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import static android.R.attr.key;

public abstract class BaseActivity extends FragmentActivity implements ConnectionExceptionListener
{
	// declaration of variables px
	public ProgressDialog progressdialog;
	public Button btnPrevious, btnNext, btnChkOutVan, btnChkInVan;
	public Button btnSetting, btnMessage, btnLoginLogout, btnBack, btnCheckOut, btnMenu;
	public LinearLayout llHeader, llBody, llSecurityHeader, llDashBoard, llBodyRight, llMenu;
	public LayoutInflater inflater;
	public static String strPreviouslySelectedOption = "";
	public static String ACTION_QUIT = "com.winit.baskinrobbin.ACTION_QUIT";
	public Handler handler = new Handler();
	public TextView tvSecurityHeader;
	protected TextView tvUserName, tvUserType;
	public AlertDialog.Builder alertBuilder;
	public static float px;
	public Preference preference;
	public CustomDialog customDialog,upgradeDialog;
	private String mEnteredPasscode = "", mEnteredReason = "";
	private ListView lvDashBoard;
	public ImageView btnLogo, ivLogOut;
	private Toast toast;
	public PendingIntent pIntent ;
	public String curencyCode;
	public DecimalFormat diffAmt, diffStock, diffPreview, diffLagLong;
	private VersionCheckingHandler versionCheckingHandler;
	protected String user_id = "";
	public String vehicleNO="";

	public String endTripTime;
	public String endTripDate;
	public String startTripTime;
	public String startTripDate;
	public int noOfRoundingOffdigits=2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		pIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(getBaseContext(), SplashScreenActivity.class), getIntent().getFlags());
		Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BaseActivity.this));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base);

 		Resources r 			= 		getResources();
		px 						= 		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		preference 				= 		new Preference(getApplicationContext());
		inflater 				= 		this.getLayoutInflater();// get the inflater object for inflating layouts.
		llBody 					= 		(LinearLayout) findViewById(R.id.llBody);
		llHeader 				= 		(LinearLayout) findViewById(R.id.llHeader);

		llDashBoard 			= 		(LinearLayout) findViewById(R.id.llDashBoard);
		llBodyRight 			= 		(LinearLayout) findViewById(R.id.llBodyRight);

		btnMenu					= 		(Button) findViewById(R.id.btnMenu);
		btnLogo 				= 		(ImageView) findViewById(R.id.btnLogo);
		btnBack 				= 		(Button) findViewById(R.id.btnBack);
		btnSetting 				= 		(Button) findViewById(R.id.btnSetting);
		btnCheckOut 			= 		(Button) findViewById(R.id.btnLogOut);
		ivLogOut				= 		(ImageView) findViewById(R.id.ivLogOut);
		btnLoginLogout 			= 		(Button) findViewById(R.id.btnLoginLogout);
		btnMessage 				= 		(Button) findViewById(R.id.btnMessage);
		btnChkOutVan 			= 		(Button) findViewById(R.id.btnChkOutVan);
		btnChkInVan 			= 		(Button) findViewById(R.id.btnChkInVan);
		llSecurityHeader 		= 		(LinearLayout) findViewById(R.id.llSecurityHeader);
		tvSecurityHeader 		= 		(TextView) findViewById(R.id.tvSecurityHeader);
		tvUserName				= 		(TextView) findViewById(R.id.tvUsername);
		tvUserType				= 		(TextView) findViewById(R.id.tvUserType);
		lvDashBoard				= 		(ListView) findViewById(R.id.lvDashBoard);
		llMenu					= 		(LinearLayout) findViewById(R.id.llMenu);

		//setting Type-faces Here
		tvUserName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUserType.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnChkOutVan.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnChkInVan.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		llBodyRight.setLayoutParams(new LinearLayout.LayoutParams(preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 800), LayoutParams.FILL_PARENT));
		//setting the User name and User-type as header
		curencyCode = preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
		if(preference.getStringFromPreference(Preference.USER_NAME, "") != null && !preference.getStringFromPreference(Preference.USER_NAME, "").equalsIgnoreCase(""))
			tvUserName.setText(preference.getStringFromPreference(Preference.USER_NAME, ""));

		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "") != null && !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(""))
		{
			tvUserType.setText(preference.getStringFromPreference(Preference.SALESMAN_TYPE, ""));
		}

		Log.e("siteNo", "siteNo from Pref- "+preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
		user_id = preference.getStringFromPreference(Preference.USER_ID, "");
//		decimalFormat = new DecimalFormat("##.##");
//		decimalFormat.setMinimumFractionDigits(2);
//		decimalFormat.setMaximumFractionDigits(2);

		diffAmt = new DecimalFormat("##.##");
		diffAmt.setMinimumFractionDigits(2);
		diffAmt.setMaximumFractionDigits(2);

		diffStock = new DecimalFormat("##.##");
		diffStock.setMinimumFractionDigits(0);
		diffStock.setMaximumFractionDigits(3);

		diffPreview = new DecimalFormat("##.##");
		diffPreview.setMinimumFractionDigits(0);
		diffPreview.setMaximumFractionDigits(3);

		diffLagLong = new DecimalFormat("#.#####");

		btnSetting.setEnabled(true);
		btnSetting.setClickable(true);

		btnMessage.setEnabled(true);
		btnMessage.setClickable(true);

		llMenu.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnMenu.performClick();
			}
		});
		btnMenu.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				TopBarMenuClick();
			}
		});

		btnLogo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnMenu.performClick();
			}
		});
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		btnLoginLogout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.do_you_want_to_logout), getResources().getString(R.string.Yes), getResources().getString(R.string.No), "logout");
			}
		});
		btnMessage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

		btnCheckOut.setTag("");
		btnCheckOut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
			}
		});

		btnCheckOut.setVisibility(View.VISIBLE);
		ivLogOut.setVisibility(View.VISIBLE);
		btnChkOutVan.setVisibility(View.GONE);
		btnChkInVan.setVisibility(View.GONE);

		btnSetting.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
//		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry("BHD");
		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry(preference.getStringFromPreference(Preference.CURRENCY_CODE,""));
		if(noOfRoundingOffdigits==0)
			noOfRoundingOffdigits=2;
		diffAmt = new DecimalFormat("##.##");
		diffAmt.setMinimumFractionDigits(noOfRoundingOffdigits);
		diffAmt.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffStock = new DecimalFormat("##.##");
		diffStock.setMinimumFractionDigits(0);
		diffStock.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffPreview = new DecimalFormat("##.##");
		diffPreview.setMinimumFractionDigits(0);
		diffPreview.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffLagLong = new DecimalFormat("#.#####");
		initialize();
//		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry("BHD");
		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry(preference.getStringFromPreference(Preference.CURRENCY_CODE,""));
		if(noOfRoundingOffdigits==0)
			noOfRoundingOffdigits=2;
		diffAmt = new DecimalFormat("##.##");
		diffAmt.setMinimumFractionDigits(noOfRoundingOffdigits);
		diffAmt.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffStock = new DecimalFormat("##.##");
		diffStock.setMinimumFractionDigits(0);
		diffStock.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffPreview = new DecimalFormat("##.##");
		diffPreview.setMinimumFractionDigits(0);
		diffPreview.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffLagLong = new DecimalFormat("#.#####");
		registerBroadCasts();

		enableTouchToChilds(llBodyRight);

		if(AppConstants.productCatalogPath == null || AppConstants.productCatalogPath.length() <= 0)
			AppConstants.productCatalogPath = 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Baskin/";

		if(AppConstants.baskinLogoPath == null || AppConstants.baskinLogoPath.length() <= 0)
			AppConstants.baskinLogoPath 	= 	AppConstants.productCatalogPath+"/BaskinLogo";
	}


	private void unregisterBroadCast()
	{
		unregisterReceiver(LogoutReceiver);
		unregisterReceiver(ActionHouseList);
		unregisterReceiver(ActionHouseListNew);
		unregisterReceiver(ActionAR);
		unregisterReceiver(ActionCRL);
		unregisterReceiver(ActionHome);
		unregisterReceiver(ActionHome1);
		unregisterReceiver(ActionSQLitefileDownload);
		unregisterReceiver(ActionJourney);
		unregisterReceiver(ActionCustomerList);
	}

	public void registerBroadCasts()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_LOGOUT);
		registerReceiver(LogoutReceiver, filter);

		IntentFilter filters = new IntentFilter();
		filters.addAction(AppConstants.ACTION_HOUSE_LIST);
		registerReceiver(ActionHouseList, filters);


		IntentFilter filtersNew = new IntentFilter();
		filtersNew.addAction(AppConstants.ACTION_HOUSE_LIST_NEW);
		registerReceiver(ActionHouseListNew, filtersNew);

		IntentFilter filtersCURL = new IntentFilter();
		filtersCURL.addAction(AppConstants.ACTION_GOTO_CRLMAIN);
		registerReceiver(ActionCustomerList, filtersCURL);

		IntentFilter filtersAR = new IntentFilter();
		filtersAR.addAction(AppConstants.ACTION_GOTO_AR);
		registerReceiver(ActionAR, filtersAR);

		IntentFilter filtersCRL = new IntentFilter();
		filtersCRL.addAction(AppConstants.ACTION_GOTO_CRL);
		registerReceiver(ActionCRL, filtersCRL);

		IntentFilter filtersHome = new IntentFilter();
		filtersHome.addAction(AppConstants.ACTION_FINISH_LIST);
		registerReceiver(ActionHome, filtersHome);

		IntentFilter filtersHome2 = new IntentFilter();
		filtersHome2.addAction(AppConstants.ACTION_GOTO_JOURNEY);
		registerReceiver(ActionJourney, filtersHome2);

		IntentFilter filtersHome1 = new IntentFilter();
		filtersHome1.addAction(AppConstants.ACTION_GOTO_HOME1);
		registerReceiver(ActionHome1, filtersHome1);

		IntentFilter sqliteFileDownload = new IntentFilter();
		sqliteFileDownload.addAction(AppConstants.ACTION_SQLITE_FILE_DOWNLOAD);
		registerReceiver(ActionSQLitefileDownload, sqliteFileDownload);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("DBPATH", AppConstants.DATABASE_PATH);
		savedInstanceState.putInt("DIVICE_WIDTH", AppConstants.DIVICE_WIDTH);
		savedInstanceState.putInt("DIVICE_HEIGHT", AppConstants.DIVICE_HEIGHT);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		AppConstants.DATABASE_PATH =  savedInstanceState.getString("DBPATH");
		AppConstants.DIVICE_WIDTH  =  savedInstanceState.getInt("DIVICE_WIDTH");
		AppConstants.DIVICE_HEIGHT =  savedInstanceState.getInt("DIVICE_HEIGHT");

		if(AppConstants.productCatalogPath == null || AppConstants.productCatalogPath.length() <= 0)
			AppConstants.productCatalogPath = 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Baskin/";

		if(AppConstants.baskinLogoPath == null || AppConstants.baskinLogoPath.length() <= 0)
			AppConstants.baskinLogoPath 	= 	AppConstants.productCatalogPath+"/BaskinLogo";

		if(AppConstants.Helvetica_LT_57_Condensed == null)
			AppConstants.Helvetica_LT_57_Condensed   = Typeface.createFromAsset(getApplicationContext().getAssets(), "Helvetica_LT_57_Condensed.ttf");
		if(AppConstants.Helvetica_LT_Condensed_Bold == null)
		AppConstants.Helvetica_LT_Condensed_Bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "Helvetica_LT_Condensed_Bold.ttf");
	}
	/**
	 * Method to get the refreshed van load data
	 */
	public boolean loadVanStock_Sync(String mgs, String empNo)
	{
		showLoader(mgs);

		GetVanLogParsar getVanLogParsar = new GetVanLogParsar(BaseActivity.this);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetVanStockLogDetail);

		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}

		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getVanStockLogDetail(empNo, lsd, lst), getVanLogParsar, ServiceURLs.GetVanStockLogDetail,preference);

		return getVanLogParsar.getStatus();
	}
	private void updateCheckoutStatus(String siteId)
	{
		String empId    = preference.getStringFromPreference(Preference.EMP_NO, "");
		String checkOut = CalendarUtils.getCurrentTime();
		String date     = CalendarUtils.getOrderPostDate();
//		new CustomerDetailsDA().updateCheckOutTimeByService(empId, date, siteId, checkOut);
	}
	/*public void setTypeFaceRobotoBold(ViewGroup group)
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditTextetc.)
	             ((TextView)v).setTypeface(AppConstants.Roboto_Condensed_Bold);
	         else if(v instanceof ViewGroup)
	        	 setTypeFaceRobotoBold((ViewGroup)v);
	     }
	}*/
	/*public void setTypeFaceRobotoNormal(ViewGroup group)
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditTextetc.)
	             ((TextView)v).setTypeface(AppConstants.Roboto_Condensed);
	         else if(v instanceof ViewGroup)
	        	 setTypeFaceRobotoNormal((ViewGroup)v);
	     }
	}*/
	public void copyVanStockToSDCard(){

		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
						String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
						ArrayList<File> arr=new ArrayList<File>();
						arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/VanStockLog.txt"));
						String zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
						ZipUtils.zipFiles(arr, new File(zipFiilePath));


						if(!TextUtils.isEmpty(zipFiilePath)){
							File logfile = new File(zipFiilePath);
							if(logfile.exists())
							{
								uploadDB(zipFiilePath,ServiceURLs.UploadVanstockLogFromSettings);
								logfile.delete();
							}
						}
						hideLoader();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Log files uploaded successfully.", getResources().getString(R.string.OK), null, "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


	}
	public void copyDeliveryToSDCard(){
		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
						String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
						ArrayList<File> arr=new ArrayList<File>();
						arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/DeliveryLog.txt"));
						String zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
						ZipUtils.zipFiles(arr, new File(zipFiilePath));


						if(!TextUtils.isEmpty(zipFiilePath)){
							File logfile = new File(zipFiilePath);
							if(logfile.exists())
							{
								//uploadDB(zipFiilePath,ServiceURLs.UploadSQLiteFromSettings);
								uploadDB(zipFiilePath,ServiceURLs.UploadDeliveryLogFromSettings);
								logfile.delete();
							}
						}
						hideLoader();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Log files uploaded successfully.", getResources().getString(R.string.OK), null, "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	public void uploadDatabaseIntoSDCARD()
	{
		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
						String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
						ArrayList<File> arr=new ArrayList<File>();
						arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME));
						File filePath   = new File(AppConstants.DATABASE_PATH+"/"+AppConstants.DATABASE_NAME);

						String zipPath 	= Environment.getExternalStorageDirectory()+"/Baskin/salesman.zip";

						String zipFiilePath  	= FileUtils.convertFileToZip(filePath.getAbsolutePath().toString(), zipPath);
//						String zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
//						ZipUtils.zipFiles(arr, new File(zipFiilePath));

						if(!TextUtils.isEmpty(zipFiilePath)){
							File databaseFile = new File(zipFiilePath);
							if(databaseFile.exists())
							{
								uploadDB(zipFiilePath,ServiceURLs.UploadSQLiteFromSettings);
								databaseFile.delete();
							}
						}
						hideLoader();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Uploaded Debug logs successfully.", getResources().getString(R.string.OK), null, "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	public boolean uploadDatabaseIntoServer(String Module)
	{
		boolean isError =false;
		try
		{
			FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
			String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
			ArrayList<File> arr=new ArrayList<File>();
			arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME));
			File filePath   = new File(AppConstants.DATABASE_PATH+"/"+AppConstants.DATABASE_NAME);

			String zipPath 	= Environment.getExternalStorageDirectory()+"/Baskin/salesman.zip";

			String zipFiilePath  	= FileUtils.convertFileToZip(filePath.getAbsolutePath().toString(), zipPath);

			if(!TextUtils.isEmpty(zipFiilePath)){
				File databaseFile = new File(zipFiilePath);
				if(databaseFile.exists())
				{
					isError = uploadDB(zipFiilePath,Module);
					databaseFile.delete();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			isError = true;
		}
		return isError;
	}
	private boolean uploadDB(String dbPath,String Module) {
		boolean isError = false;
		String URL = getSQLiteURL(BaseActivity.this);
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL + "uploadfile/upload.aspx?Module="+Module);
			File filePath = new File(dbPath);

			if (filePath.exists()) {
				Log.e("uplaod", "called");
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody cbFile = new FileBody(filePath, "application/octet-stream");
				mpEntity.addPart("FileName", cbFile);
				httppost.setEntity(mpEntity);
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				is = resEntity.getContent();
				if(is!=null)
					isError=false;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			isError = true;
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
		} finally {
		}
		return isError;
	}
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	public static String parseImageUploadResponse(Context context,
			InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ImageUploadParser handler = new ImageUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private String getSQLiteURL(Context mContext)
	{
		String URL = new Preference(mContext).getStringFromPreference(Preference.DATABASE_URL, "");

		if(!NetworkUtility.isWifiConnected(mContext))
			URL = ServiceURLs.IMAGE_GLOBAL_URL;

		if(URL == null || URL.length() <= 0)
			URL = ServiceURLs.IMAGE_GLOBAL_URL;

		return URL;
	}
	public void uploadDatabaseIntoSDCARD(final String path, final String fileName, final boolean isLogin)
	{
		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, path+ "/" + fileName);
						if(!isLogin)
						{
							hideLoader();
							showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Uploaded Debug logs successfully.", getResources().getString(R.string.OK), null, "");
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						if(!isLogin)
						{
							showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
							hideLoader();
						}
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void uploadSqliteToServer()
	{
		try
		{
			showLoader(getResources().getString(R.string.please_wait_data_uploading));
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						new UploadImage().uploadImage(BaseActivity.this, AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, ServiceURLs.ORDER_IMAGES, true);
						ServiceURLs.UPLOAD_DATABASE_URL = ServiceURLs.UPLOAD_DATABASE_URL.replace("{psid}", preference.getStringFromPreference(Preference.USER_ID, ""));
						ServiceURLs.UPLOAD_DATABASE_URL = ServiceURLs.UPLOAD_DATABASE_URL.replace("{date}", CalendarUtils.getCurrentSalesDate()+ "-"+ CalendarUtils.getCurrentTimeToUpload());
						//to be deleted, temporary
//						new HTTPHelper().uploadFileByMultipart(ServiceURLs.UPLOAD_DATABASE_URL,AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME);
//						hideLoader();
//						showCustomDialog(BaseActivity.this, getResources().getString(R.string.successful),"Uploaded Debug logs successfully.", getResources().getString(R.string.OK), null, "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Error occurred while uploading.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				}
			}).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void performCheckouts(final boolean isCheckOut)
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				Log.e("siteNo", "siteNo - "+siteNo);
				updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
//				new CustomerDetailsDA().updateJourneyLog(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,	""), 100);
//				new CustomerDA().updateCustomerCheckOutTime(CalendarUtils.getCurrentDateTime());

				new CustomerDA().updateLastJourneyLog();
				preference.removeFromPreference(Preference.LAST_CUSTOMER_SITE_ID);
				preference.removeFromPreference(Preference.CUSTOMER_NAME);
				preference.commitPreference();

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(!isCheckOut || new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsString()) || !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
						{
							uploadData(AppStatus.POST_CUSTOMER_VISIT, AppStatus.TODAY_DATA);
							if(BaseActivity.this instanceof CheckInOptionActivity)
							{
								finish();
							}
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
							sendBroadcast(intentBrObj);
						}
						else
						{
							showReasonOfCheckout(isCheckOut);
//							showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
						}
					}
				});
			}
		}).start();
	}

	public void performCustomerCheckout()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				Log.e("siteNo", "siteNo - "+siteNo);
				updateCheckoutStatus(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
//				new CustomerDetailsDA().updateJourneyLog(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,	""),
//														100);
//				new CustomerDA().updateCustomerCheckOutTime(CalendarUtils.getCurrentDateTime());

				new CustomerDA().updateLastJourneyLog();
				preference.removeFromPreference(Preference.LAST_CUSTOMER_SITE_ID);
				preference.removeFromPreference(Preference.CUSTOMER_NAME);
				preference.commitPreference();

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsString()))
						{
							uploadData(AppStatus.POST_CUSTOMER_VISIT, AppStatus.TODAY_DATA);

							if(BaseActivity.this instanceof CheckInOptionActivity)
								finish();

							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
							sendBroadcast(intentBrObj);
						}
						else
						{
							showReasonOfCheckout(false);
//							showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
						}
					}
				});
			}
		}).start();
	}

	BroadcastReceiver LogoutReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(!(BaseActivity.this instanceof LoginAcivity))
				finish();
			else{
				if(intent.hasExtra("error_reset_stock")){
					onButtonYesClick("error_reset_stock",intent.getExtras().getString("error_reset_stock"));
				}
			else if(intent.hasExtra("Data_cleared")){
						onButtonYesClick("Data_cleared",intent.getExtras().getString("Data_cleared"));
					}else{
					onButtonYesClick("logoutAdmin");
				}
			}
		}
	};

	BroadcastReceiver ActionHouseList = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan)
					|| (BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof SalesmanCustomerList)
					||  (BaseActivity.this instanceof SalesmanARCustomerList)
					||  (BaseActivity.this instanceof SalesmanReplacementCustomerList));
			else
				finish();
		}
	};


	BroadcastReceiver ActionHouseListNew = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan) || (BaseActivity.this instanceof VehicleList) || (BaseActivity.this instanceof SalesmanCustomerList)
					|| (BaseActivity.this instanceof CheckInOptionActivity) || (BaseActivity.this instanceof SalesmanARCustomerList));
			else
				finish();
		}
	};

	BroadcastReceiver ActionAR = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PendingInvoices)
					|| (BaseActivity.this instanceof ReceivePaymentBySalesman))
				finish();
		}
	};

	BroadcastReceiver ActionCustomerList = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof ReceivePaymentBySalesman)
			   || (BaseActivity.this instanceof SalesManTakeReturnOrder))
				finish();
		}
	};

	BroadcastReceiver ActionCRL = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan)
					|| (BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof SalesmanCustomerList)
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};

	BroadcastReceiver ActionHome1 = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof PresellerJourneyPlan)
					|| (BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};

	BroadcastReceiver ActionJourney = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof VehicleList)
					|| (BaseActivity.this instanceof LoginAcivity))
			{

			}
			else
				finish();
		}
	};
	BroadcastReceiver ActionHome = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if((BaseActivity.this instanceof SalesmanCustomerList)
					|| (BaseActivity.this instanceof SalesmanARCustomerList))
			{
				finish();
			}
		}
	};

	BroadcastReceiver ActionSQLitefileDownload = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Toast.makeText(BaseActivity.this, "SQLite file downloaded successfully.", Toast.LENGTH_SHORT).show();
		}
	};


	/**
	 * to show security buttons
	 */
	public void showSecurityButtons()
	{
		btnChkOutVan.setVisibility(View.VISIBLE);
		btnChkInVan.setVisibility(View.VISIBLE);
	}

	/**
	 * to hide security buttons
	 */
	public void hideSecurityButtons()
	{
		btnChkOutVan.setVisibility(View.GONE);
		btnChkInVan.setVisibility(View.GONE);
	}

	public abstract void initialize();

	/** Method to Show the alert dialog **/
	public void showDialog(Context context, String strTitle, String strMessage)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage, null,null));
	}

	/** Method to Show the alert dialog with click listener **/
	public void showDialog(Context context, String strTitle, String strMessage,	DialogInterface.OnClickListener dialogClickListener)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage,	dialogClickListener, null));
	}

	/** Method to Show the alert dialog with click listener **/
	public void showDialog(Context context, String strTitle, String strMessage,	DialogInterface.OnClickListener posDialogClickListener,	DialogInterface.OnClickListener negDialogClickListener)
	{
		runOnUiThread(new RunShowDialog(context, strTitle, strMessage,	posDialogClickListener, negDialogClickListener));
	}

	/**
	 * This method is to show the loading progress dialog when some other
	 * functionality is taking place.
	 **/
	public void showLoader(String msg)
	{
		runOnUiThread(new RunShowLoader(msg, ""));
	}

	/**
	 * This method is to show the loading progress dialog when some other
	 * functionality is taking place.
	 **/
	public void showLoader(String msg, String title)
	{
		runOnUiThread(new RunShowLoader(msg, title));
	}

	/** For hiding progress dialog (Loader ). **/
	public void hideLoader()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if (progressdialog != null && progressdialog.isShowing())
						progressdialog.dismiss();
					progressdialog = null;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	// For showing Dialog message.
	class RunShowDialog implements Runnable
	{
		private String strTitle;// Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private Context context; // Context for showing dialog
		private DialogInterface.OnClickListener posDialogClickListener,	negDialogClickListener;

		public RunShowDialog(Context context, String strTitle,String strMessage,DialogInterface.OnClickListener posDialogClickListener,	DialogInterface.OnClickListener negDialogClickListener)
		{
			this.context = context;
			this.strTitle = strTitle;
			this.strMessage = strMessage;
			this.posDialogClickListener = posDialogClickListener;
			this.negDialogClickListener = negDialogClickListener;
		}

		@Override
		public void run()
		{
			// Creating Alert Dialog object
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(strTitle);// setting the title of the alert dialog
			builder.setCancelable(false);
			builder.setMessage(strMessage);// setting the message of the alert
										  // dialog

			// Clicking on OK button, dismiss the dialog.
			if (posDialogClickListener == null)
			{
				builder.setPositiveButton(getResources().getString(R.string.OK)/* * getResources().getString * (R.string.Ok)*/,	new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog,	int which)
					{
						dialog.dismiss(); // dismiss the dialog.
						onDialogClick(strMessage);
					}
				});
			}
			else
			{
				builder.setPositiveButton(getResources().getString(R.string.OK)/** getResources().getString * (R.string.Ok) */,	posDialogClickListener);
			}
			if (negDialogClickListener != null)
			{
				builder.setNegativeButton(getResources().getString(	R.string.cancel), negDialogClickListener);
			}
			AlertDialog dialog = builder.create();
			if (!dialog.isShowing())
				dialog.show();// this is to show the dialog.
		}
	}

	public void onDialogClick(String mssg)
	{
	}

	// This is to show the loading progress dialog when some other functionality
	// is taking place.
	class RunShowLoader implements Runnable
	{
		private String strMsg;
		private String title;

		public RunShowLoader(String strMsg, String title)
		{
			this.strMsg = strMsg;
			this.title = title;
		}

		@Override
		public void run()
		{
			try
			{
				if (progressdialog == null|| (progressdialog != null && !progressdialog.isShowing()))
				{
					progressdialog = ProgressDialog.show(BaseActivity.this,	title, strMsg);
				}
				else if (progressdialog == null|| (progressdialog != null && progressdialog.isShowing()))
				{
					progressdialog.setMessage(strMsg);
				}
			}
			catch (Exception e)
			{
				progressdialog = null;
			}
		}
	}


	private String strDashBoardOptions[]; //= AppConstants.presellerOption;
	private int DashBoardLoogs[];// = AppConstants.presellerOptionLoogs;


	private ListView lvDashboardOptions;
	private PopupWindow optionMenuPopup;
	public void showOptionPopup(View view)
	{
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
		{
			strDashBoardOptions = AppConstants.presellerOptionGT;
			DashBoardLoogs = AppConstants.presellerOptionLoogsGT;
		}
		else
		{
			strDashBoardOptions = AppConstants.presellerOptionAll;
			DashBoardLoogs = AppConstants.presellerOptionLoogsAll;
		}

//		btnLogo.setBackgroundResource(R.drawable.logo_hov);
		optionMenuPopup = new PopupWindow(BaseActivity.this);
		LayoutInflater inflater = (LayoutInflater) BaseActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View llEntirechrt = inflater.inflate(R.layout.option_layout, null, true);
		optionMenuPopup = new PopupWindow(llEntirechrt, preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 50, LayoutParams.WRAP_CONTENT, true);
		optionMenuPopup.setOutsideTouchable(true);
		optionMenuPopup.setTouchInterceptor(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					optionMenuPopup.dismiss();
					return true;
				}
				return false;
			}
		});
		optionMenuPopup.setBackgroundDrawable(new BitmapDrawable());
		optionMenuPopup.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{
				btnLogo.setBackgroundResource(R.drawable.logo_click);
			}
		});
		LinearLayout llPopup = (LinearLayout) llEntirechrt.findViewById(R.id.llPopup);
		lvDashboardOptions = new ListView(BaseActivity.this);
		if (strDashBoardOptions != null)
		{
			lvDashboardOptions.setCacheColorHint(0);
			lvDashboardOptions.setScrollBarStyle(0);
			lvDashboardOptions.setScrollbarFadingEnabled(true);
			lvDashboardOptions.setDivider(null);
			lvDashboardOptions.setAdapter(new DashBoardOptionsCustomAdapter());
		}
		llPopup.addView(lvDashboardOptions,	new LinearLayout.LayoutParams(preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 50,LayoutParams.WRAP_CONTENT));
		optionMenuPopup.showAsDropDown(view, 10, 0);
	}

	@Override
	public void onBackPressed() {
		/*if(BaseActivity.this instanceof VerifyItemInVehicle)
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
			sendBroadcast(intentBrObj);
		}*/

		if(llDashBoard.isShown())
			TopBarMenuClick();
		else
			super.onBackPressed();

	}
	public class DashBoardOptionsCustomAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return strDashBoardOptions.length;
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			convertView 			= 	inflater.inflate(R.layout.dashboard_options_cell, null);
			ImageView ivOptionIcon 	= 	(ImageView) convertView.findViewById(R.id.ivOptionIcon);
			TextView tvOptionName 	= 	(TextView) convertView.findViewById(R.id.tvOptionName);
			LinearLayout rlCalender = 	(LinearLayout) convertView.findViewById(R.id.rlCalender);
			ImageView ivFooter		= 	(ImageView) convertView.findViewById(R.id.ivFooter);
			ivOptionIcon.setImageResource(DashBoardLoogs[position]);
			tvOptionName.setText(strDashBoardOptions[position]);
			tvOptionName.setTypeface(AppConstants.Helvetica_LT_57_Condensed);

			if(strDashBoardOptions[position].equalsIgnoreCase("footer"))
			{
				rlCalender.setVisibility(View.GONE);
				ivFooter.setVisibility(View.VISIBLE);
			}
			else
			{
				rlCalender.setVisibility(View.VISIBLE);
				ivFooter.setVisibility(View.GONE);
			}

			convertView.setTag(strDashBoardOptions[position]);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
				{
					disableView(v);

					TopBarMenuClick();
					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							if(strDashBoardOptions[position].equalsIgnoreCase("Today`s Dashboard"))
							{
								if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
									moveToNextAcivityGT(v.getTag().toString());
								else
									moveToNextAcivityAllAL(v.getTag().toString());
							}
							else
							{
								if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
									moveToNextAcivityGT(v.getTag().toString());
								else
									moveToNextAcivityAllAL(v.getTag().toString());
							}
						}
					}, 400);
				}
			});

			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int) (55 * px)));
			return convertView;
		}
	}

	public void moveToNextAcivityGT(String strOptionSelected)
	{
		if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[0])&& !(BaseActivity.this instanceof PresellerJourneyPlan))
			showCustomDialog(BaseActivity.this, getString(R.string.warning), "Do you want to finish the current task?", "Yes", "No", "journeyplan");

		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[1])&& !(BaseActivity.this instanceof AddStockInVehicle))
		{
			preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, true);
			preference.commitPreference();

			Intent intent = new Intent(BaseActivity.this, AddStockInVehicle.class);
			intent.putExtra(Preference.VEHICLE_DO, (VehicleDO)preference.getVehicleObjectFromPreference("VehicleDO"));
			intent.putExtra("isPreview", true);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[2])&& !(BaseActivity.this instanceof SalesmanCustomerList))
		{
			String lastChecked  = preference.getStringFromPreference(Preference.LAST_CUSTOMER_SITE_ID , "");
			String name 		= preference.getStringFromPreference(Preference.CUSTOMER_NAME , "");
			if(TextUtils.isEmpty(lastChecked) || (BaseActivity.this instanceof PresellerJourneyPlan))
			{
				//Customer list
				Intent intent = new Intent(BaseActivity.this, SalesmanCustomerList.class);
				intent.putExtra("isHistory", false);
				startActivity(intent);
			}
			else
				showCustomDialog(BaseActivity.this, "Alert !", "You are already checked in at "+name +". Please checkout the current customer.", "OK", null, "");
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[3]) && !(BaseActivity.this instanceof SalesmanARCustomerList))
		{
			Intent intent = new Intent(BaseActivity.this, SalesmanARCustomerList.class);
			intent.putExtra("callfrom", "ArCollection");
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[4])  && !(BaseActivity.this instanceof SalesmanVanStock))
		{
			Intent intent = new Intent(BaseActivity.this, SalesmanVanStock.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[5]) && !(BaseActivity.this instanceof CustomerContactDetail))
		{
			Intent intent = new Intent(BaseActivity.this, CustomerContactDetail.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[6]) && !(BaseActivity.this instanceof OrderSummary))
		{
			Intent intent = new Intent(BaseActivity.this, OrderSummary.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[7])  && !(BaseActivity.this instanceof CustomerInvoiceActivity))
		{
			//Payment Summary
			Intent intent = new Intent(BaseActivity.this, CustomerInvoiceActivity.class);
			startActivity(intent);
		}

		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[8])  && !(BaseActivity.this instanceof CustomerInvoiceActivity))
		{
			Intent intent = new Intent(BaseActivity.this,	ProductCatalogActivity.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[9])  && !(BaseActivity.this instanceof TodaysDashboradActivity))
		{
			Intent intent = new Intent(BaseActivity.this, TodaysDashboradActivity.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[10]) && !(BaseActivity.this instanceof SalesmanSummaryofDay))
		{
			Intent intents = new Intent(BaseActivity.this,	SalesmanSummaryofDay.class);
			intents.putExtra("dateofJorney", getCurrentdate());
			preference.saveStringInPreference("EOTType", "Normal");
			preference.saveStringInPreference("EOTReason", "");
			preference.commitPreference();
			startActivity(intents);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[11]) && !(BaseActivity.this instanceof Settings))
		{
			Intent intent = new Intent(BaseActivity.this, Settings.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[12]) && !(BaseActivity.this instanceof AboutApplicationActivity))
		{
			Intent intent = new Intent(BaseActivity.this, AboutApplicationActivity.class);
			startActivity(intent);
		}
		else if (strOptionSelected.equalsIgnoreCase(strDashBoardOptions[13]))
		{
			if (btnLoginLogout != null)
				btnLoginLogout.performClick();
		}
	}


	public void moveToNextAcivityAllAL(String strOptionSelected)
	{
	}

	public void moveToNextAcivityPD(String strOptionSelected)
	{
	}

	public void ShowAlertDialog(String Tilte, String Mesg)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
		builder.setTitle(Tilte);// setting the title of the alert dialog
		builder.setMessage(Mesg);// setting the message of the alert dialog

		// Clicking on OK button, dismiss the dialog.
		builder.setPositiveButton(getResources().getString(R.string.OK)/*
																		 * getResources(
																		 * ).
																		 * getString
																		 * (
																		 * R.string
																		 * .Ok)
																		 */,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // dismiss the dialog.
					}
				});
		builder.create().show();// this is to s
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	// For showing Dialog message.
	class RunshowCustomDialogs implements Runnable
	{
		private String strTitle;// Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private String firstBtnName;
		private String secondBtnName;
		private String from;
		private boolean isCancelable=false;
		private OnClickListener posClickListener;
		private OnClickListener negClickListener;

		public RunshowCustomDialogs(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, boolean isCancelable)
		{
			this.strTitle 		= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName 	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			this.isCancelable 	= isCancelable;
			if (from != null)
				this.from = from;
			else
				this.from = "";
		}

		@Override
		public void run()
		{
			if (customDialog != null && customDialog.isShowing())
				customDialog.dismiss();

			View view = inflater.inflate(R.layout.custom_common_popup, null);
			customDialog = new CustomDialog(BaseActivity.this, view, preference
					.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
					LayoutParams.WRAP_CONTENT, true);
			customDialog.setCancelable(isCancelable);
			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
			TextView tvMessage = (TextView) view
					.findViewById(R.id.tvMessagePopup);
			Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
			Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);

			tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			btnYes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			btnNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);

			tvTitle.setText("" + strTitle);
			tvMessage.setText("" + strMessage);
			btnYes.setText("" + firstBtnName);

			if (secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
				btnNo.setText("" + secondBtnName);
			else
				btnNo.setVisibility(View.GONE);

			if(posClickListener == null)
				btnYes.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						customDialog.dismiss();
						onButtonYesClick(from);
					}
				});
			else
				btnYes.setOnClickListener(posClickListener);

			if(negClickListener == null)
				btnNo.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						customDialog.dismiss();
						onButtonNoClick(from);
					}
				});
			else
				btnNo.setOnClickListener(negClickListener);
			try{
			if (!customDialog.isShowing())
				customDialog.show();
			}catch(Exception e){}
		}
	}

	public void onButtonNoClick(String from)
	{
	}

	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from, OnClickListener posClickListener)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from,true));
	}
	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, true));
	}
	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from, boolean isCancelable)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, isCancelable));
	}

	@Override
	protected void onDestroy()
	{
		final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
		Log.e("SiteNo","site = "+siteNo);
		hideLoader();
		super.onDestroy();
		try
		{
			unregisterBroadCast();
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (Exception e)
		{
		}
	}

	@Override
	protected void onPause()
	{
		if(!(BaseActivity.this instanceof LoginAcivity))
			hideLoader();
		super.onPause();
	}

	public void hideKeyBoard(View v)
	{
		if(v != null)
		{
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	public String getCurrentdate()
	{
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		String strMonth = "", strDate = "";

		if (month < 9)
			strMonth = "0" + (month + 1);
		else
			strMonth = "" + (month + 1);

		if (day < 10)
			strDate = "0" + (day);
		else
			strDate = "" + (day);

		String strSelectedDate = year + "-" + strMonth + "-" + strDate;
		return strSelectedDate;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3000)
		{
			finish();
			setResult(3000);
		}
	}
	public void onButtonYesClick(String from)
	{

		if (from.equalsIgnoreCase("logout"))
		{
			// sending broadcast message for logout action
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
			sendBroadcast(intentBrObj);
			Intent intent = null;
			intent = new Intent(BaseActivity.this, LoginAcivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right1, R.anim.slide_left1);
		}
		else if(from.equalsIgnoreCase("checkout"))
		{
			performCheckouts(true);
		}
		else if(from.equalsIgnoreCase("journeyplan"))
		{
			Intent objIntent = new Intent();
			objIntent.setAction(AppConstants.ACTION_GOTO_JOURNEY);
			sendBroadcast(objIntent);

			Intent intent = new Intent(BaseActivity.this, PresellerJourneyPlan.class);
			startActivity(intent);
		}
	}
	public void onButtonYesClick(String from,String params)
	{

	}

	public void showReasonOfCheckout(final boolean isCheckout)
	{
		final Vector<NameIDDo> vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_JOURNEY_PLAN);
		CustomBuilder builder = new CustomBuilder(this, "Select Reason", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
	    		builder.dismiss();
	    		new Thread(new Runnable()
	    		{
					@Override
					public void run()
					{
					    Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
						PostReasonDO objPostReasonDO 			   = new PostReasonDO();
						objPostReasonDO.customerSiteID = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
	    				objPostReasonDO.presellerId    = preference.getStringFromPreference(Preference.EMP_NO, "");
	    				objPostReasonDO.reason         = ""+objNameIDDo.strName;
	    				objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
	    				objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
	    				objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
	    				vecPostReasons.add(objPostReasonDO);

						if(vecPostReasons != null && vecPostReasons.size() > 0)
						{
							new CommonDA().insertAllReasons(vecPostReasons);
							uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
						}

						final int passcodeEnable = new SettingsDA().getSettingsByName(AppConstants.PASSCODE_CHECKOUT);

						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								if(passcodeEnable == AppStatus.ENABLE)
									showPassCodeDialog(null, "finish", isCheckout);
								else
									performPasscodeAction(null, "finish", isCheckout);
							}
						});
					}
				}).start();
		    }
	   });

	 builder.show();
	}

	/**
	 * Method to check the Internet availability
	 * @param context
	 * @return boolean
	 */
	public boolean isNetworkConnectionAvailable(Context context)
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context	.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
		{
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}

		return isNetworkConnectionAvailable;
	}
	/**
	 * Method to check the Internet is Slow
	 * @param context
	 * @return boolean
	 */
	public boolean isGPSEnable(Context context)
	{
		LocationManager	lm 			= 	(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		try
		{
			boolean gps_enabled		=	lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if(!gps_enabled)
				return false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return true;
	}
	public void onConnectionException(Object msg)
	{
	}
//	public void getPaymentReceipts()
//	{
//		GenerateReceiptParser generateReceiptParser = new GenerateReceiptParser(BaseActivity.this);
//		HashMap<String,String> hashMap 				= new PaymentDetailDA().getReceiptCount();
//		ConnectionHelper connectionHelper 			= new ConnectionHelper(BaseActivity.this);
//		connectionHelper.sendRequest(BuildXMLRequest.getReceiptNumbers(hashMap,preference.getStringFromPreference(Preference.SALESMANCODE, "")),generateReceiptParser , ServiceURLs.GENRATE_RECEIPT);
//	}

	public void scheduleBackgroundtask()
	{
		try
		{
			AlarmManager alarms	 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			Intent intent 		 = new Intent(getApplicationContext(),AlaramReceiver.class);
			intent.putExtra(AlaramReceiver.ACTION_ALARM, AlaramReceiver.ACTION_ALARM);
			final PendingIntent pIntent = PendingIntent.getBroadcast(this,1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AppConstants.TIME_FOR_BACKGROUND_TASK, pIntent);
		}
		catch (Exception e)
		{
			   e.printStackTrace();
		}
	}

	public void cancelScheduleTask()
	{
		Intent intent = new Intent(getApplicationContext(), AlaramReceiver.class);
		intent.putExtra(AlaramReceiver.ACTION_ALARM,AlaramReceiver.ACTION_ALARM);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, 1234567,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pIntent);
	}

	public void showEOTDialog(Context context, String strTitle)
	{
		runOnUiThread(new RunshowEOTDialog(context, strTitle));
	}

	// For showing EOT popup.
	class RunshowEOTDialog implements Runnable
	{
		private String strTitle;// Title of the dialog
		private Context context;

		public RunshowEOTDialog(Context context, String strTitle)
		{
			this.strTitle = strTitle;
			this.context = context;
		}

		@Override
		public void run()
		{
			View view 						= 	inflater.inflate(R.layout.eot_popup, null);
			final CustomDialog customDialog = 	new CustomDialog(BaseActivity.this, view, preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
			TextView tvTitle 				= 	(TextView) view.findViewById(R.id.tvTitlePopup);
			final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
			final EditText etEnterValue 	= 	(EditText) view.findViewById(R.id.etEnterValue);
			final EditText etEnterReason	= 	(EditText) view.findViewById(R.id.etEnterReason);
			TextView tvSelectReason			= 	(TextView) view.findViewById(R.id.tvSelectReason);

			customDialog.setCancelable(true);

			if(mEnteredPasscode != null && !mEnteredPasscode.equalsIgnoreCase(""))
				etEnterValue.setText(mEnteredPasscode);

			if(mEnteredReason != null && !mEnteredReason.equalsIgnoreCase(""))
				etEnterReason.setText(mEnteredReason);

			tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			btnOkPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			etEnterValue.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			etEnterReason.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			tvSelectReason.setVisibility(View.GONE);

			tvTitle.setText("" + strTitle);
			btnOkPopup.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (etEnterValue.getText().toString().equalsIgnoreCase(""))
					{
						mEnteredReason = ""+etEnterReason.getText().toString();
						mEnteredPasscode = "";
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Code field should not be empty.",getResources().getString(R.string.OK),null, "");
					}
					else if (etEnterReason.getText().toString().equalsIgnoreCase(""))
					{
						mEnteredPasscode = ""+etEnterValue.getText().toString();
						mEnteredReason = "";
						showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter reason.",getResources().getString(R.string.OK),null, "");
					}
					else
					{
						new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),etEnterValue.getText().toString());
								if (nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("0"))
								{
									new CommonDA().updatePasscodeStatus(nameIDDO.strName);

									runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{
											customDialog.dismiss();
											Intent intents = new Intent(BaseActivity.this,	SalesmanSummaryofDay.class);
											intents.putExtra("dateofJorney", getCurrentdate());
											preference.saveStringInPreference("EOTType", "Normal");
											preference.saveStringInPreference("EOTReason", etEnterReason.getText().toString());
											preference.commitPreference();
											startActivity(intents);
										}
									});
								}
								else if(nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("1"))
									showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Entered passcode is already used, please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
								else
									showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
							}
						}).start();
					}
				}
			});
			if (!customDialog.isShowing())
				customDialog.show();
		}
	}

	protected Animation animationBody1, animationBody2;
	private DashBoardOptionsCustomAdapter adapter;
	/**Method for Top Menu click **/
	public void TopBarMenuClick()
	{
		llDashBoard.clearAnimation();
		llBodyRight.clearAnimation();
//		llBaseMenu.clearAnimation();
//		llBaseRight.clearAnimation();

		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
		{
			strDashBoardOptions = AppConstants.presellerOptionGT;
			DashBoardLoogs = AppConstants.presellerOptionLoogsGT;
		}
//		else if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
//		{
//			strDashBoardOptions = AppConstants.presellerOptionPD;
//			DashBoardLoogs = AppConstants.presellerOptionLogosPD;
//		}
		else
		{
			strDashBoardOptions = AppConstants.presellerOptionAll;
			DashBoardLoogs = AppConstants.presellerOptionLoogsAll;
		}

		if (llDashBoard.isShown())
		{
			animationBody1 = new TranslateAnimation(0, -(AppConstants.DIVICE_WIDTH*(0.75f)), 0, 0);
			animationBody2 = new TranslateAnimation(0, -(AppConstants.DIVICE_WIDTH*(0.75f)), 0, 0);

			animationBody1.setDuration(400);
			animationBody2.setDuration(400);
			animationBody1.setFillAfter(true);
			animationBody2.setFillAfter(true);
			animationBody1.setInterpolator(new LinearInterpolator());
			animationBody2.setInterpolator(new LinearInterpolator());
			llBodyRight.setVisibility(View.VISIBLE);
			animationBody2.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}
				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					llDashBoard.clearAnimation();
					llBodyRight.clearAnimation();
					llDashBoard.setVisibility(View.GONE);

				}
			});


			llDashBoard.setAnimation(animationBody1);
			llBodyRight.setAnimation(animationBody2);

			lvDashBoard.invalidate();
//			animationBody1.start();
//			animationBody2.start();
		}
		else
		{
			if(adapter == null)
			{
				adapter = new DashBoardOptionsCustomAdapter();
				lvDashBoard.setAdapter(adapter);
				lvDashBoard.setCacheColorHint(0);
				lvDashBoard.setScrollBarStyle(0);
				lvDashBoard.setScrollbarFadingEnabled(true);
				lvDashBoard.setDividerHeight(1);
				lvDashBoard.setDivider(getResources().getDrawable(R.drawable.saparetor_dash));
			}

			animationBody1 = new TranslateAnimation(-(AppConstants.DIVICE_WIDTH*(0.75f)), 0, 0, 0);
			animationBody2 = new TranslateAnimation(-(AppConstants.DIVICE_WIDTH*(0.75f)), 0, 0, 0);
			animationBody1.setDuration(400);
			animationBody2.setDuration(400);
			animationBody1.setFillAfter(true);
			animationBody2.setFillAfter(true);
			animationBody1.setInterpolator(new LinearInterpolator());
			animationBody2.setInterpolator(new LinearInterpolator());

			llBodyRight.setAnimation(animationBody2);
			llDashBoard.setAnimation(animationBody1);
			llDashBoard.setVisibility(View.VISIBLE);
			animationBody1.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					llDashBoard.setVisibility(View.VISIBLE);
					llDashBoard.clearAnimation();
					llBodyRight.clearAnimation();
				}
			});

			hideKeyBoard(llBodyRight);
		}
	}

	public void uploadData(int Type, int UPLOAD_TYPE)
	{
		if(isNetworkConnectionAvailable(BaseActivity.this) && !UploadData.isRunning)
		{
			Intent intent = new Intent(BaseActivity.this, UploadData.class);
			intent.putExtra("TYPE", Type);
			intent.putExtra(AppStatus.ALL_DATA_KEY, UPLOAD_TYPE);
			startService(intent);
		}
	}

	OnTouchListener touch = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			 {
				 if(v.getId() != R.id.btnMenu)
				 {
					 if(llDashBoard.getVisibility() == View.VISIBLE)
						 btnMenu.performClick();
				 }
			 }
			return false;
		}
	};

	public void enableTouchToChilds(ViewGroup viewGroup)
	 {
		 viewGroup.setOnTouchListener(touch);
		 if(viewGroup.getChildCount() > 0)
		 {
			 for(int i=0;i<viewGroup.getChildCount();i++)
			 {
				 if(viewGroup.getChildAt(i) instanceof ViewGroup)
					 enableTouchToChilds((ViewGroup)viewGroup.getChildAt(i));
				 else
					 viewGroup.getChildAt(i).setOnTouchListener(touch);
			 }
		 }
	 }

	public void showToast(String message)
	{
		if(toast != null)
			toast.cancel();

		toast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	public void setTypeFace(ViewGroup group)
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
	             ((TextView)v).setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	         else if(v instanceof ViewGroup)
	        	 setTypeFace((ViewGroup)v);
	     }
	}

	public boolean downloadSQLITE(final String downloadUrl) {

		String strFile = FileUtils.downloadSQLITE(downloadUrl,
				AppConstants.DATABASE_PATH, BaseActivity.this,
				"salesman");

		if(strFile == null)
			return false;
		else
			return true;
	}

	public boolean downloadSQLITE(final String downloadUrl, DownloadListner downloadListner) {

		FileUtils.count = 0;
		String strFile = FileUtils.downloadSQLITE(downloadUrl,
				AppConstants.DATABASE_PATH, BaseActivity.this,
				"salesman",downloadListner);

		if(strFile == null)
			return false;
		else
			return true;
	}

	public void creditLimiPopup(final JourneyPlanDO mallsDetails, final int invoiceCount,
			final MakePaymentListner paymentListner, boolean isExceeded, boolean isOverDue)
	{
		View view = inflater.inflate(R.layout.cradit_limit_popup, null);
		final CustomDialog mCustomDialog = new CustomDialog(BaseActivity.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		ImageView ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
		TextView tv_poptitle = (TextView) view.findViewById(R.id.tv_poptitle);
		Button btn_ModifyOrder = (Button) view.findViewById(R.id.btn_ModifyOrder);
		Button btn_MakePayment = (Button) view.findViewById(R.id.btn_MakePayment);
		Button btn_MakePayOrder = (Button) view.findViewById(R.id.btn_MakePayOrder);
		Button btn_OrderOnHold  = (Button) view.findViewById(R.id.btn_OrderOnHold);
		Button btn_Continue  = (Button) view.findViewById(R.id.btn_Continue);

		if(!isExceeded && isOverDue)
			tv_poptitle.setText("Customer is having overdue amount.\n Please select the \nbelow option.");

		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
			}
		});
		btn_ModifyOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(paymentListner != null)
					paymentListner.makePayment(mCustomDialog, AppStatus.MODIFY_ORDER);
				else
					mCustomDialog.dismiss();
			}
		});

		if(paymentListner == null)
		{
			btn_Continue.setVisibility(View.VISIBLE);
			btn_MakePayOrder.setVisibility(View.GONE);
			btn_OrderOnHold.setVisibility(View.GONE);
		}

		btn_Continue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				performSalesOrder();
			}
		});

		btn_OrderOnHold.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				paymentListner.makePayment(mCustomDialog, AppStatus.ONHOLD_ORDER);
			}
		});

		btn_MakePayOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				paymentListner.makePayment(mCustomDialog, -1);
			}
		});

		if(invoiceCount <= 0)
			btn_MakePayment.setVisibility(View.GONE);

		btn_MakePayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
				Intent intent = new Intent(BaseActivity.this, PendingInvoices.class);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("AR", false);
				intent.putExtra("isExceed", true);
				if(mallsDetails!= null && mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
					intent.putExtra("isCredit", true);
				startActivityForResult(intent, 10001);
			}
		});

		setTypeFace((LinearLayout)view);
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}

	public void storeImage(Bitmap imageData,final String filename)
	{
		try {
			imageData = Bitmap.createScaledBitmap(imageData, 250, 80, false);
			BitmapConvertor convertor = new BitmapConvertor(BaseActivity.this, new OnMonochromeCreated()
			{
				@Override
				public void onCompleted(String path)
				{
					preference.saveStringInPreference(filename, path);
					preference.commitPreference();
				}
			});
	        convertor.convertBitmap(imageData, filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void performSalesOrder()
	{
	}

	public void showPassCodeDialog(final NameIDDo nameIDDo, final String from, final boolean isCheckOut)
	{
		View view 						= 	inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialog = 	new CustomDialog(BaseActivity.this, view, preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		TextView tvTitle 				= 	(TextView) view.findViewById(R.id.tvTitlePopup);
		final Button btnOkPopup 		= 	(Button) view.findViewById(R.id.btnOkPopup);
		final EditText etEnterValue 	= 	(EditText) view.findViewById(R.id.etEnterValue);
		final EditText etEnterReason	= 	(EditText) view.findViewById(R.id.etEnterReason);
		TextView tvSelectReason			= 	(TextView) view.findViewById(R.id.tvSelectReason);

		etEnterReason.setVisibility(View.GONE);
		tvSelectReason.setVisibility(View.GONE);
		customDialog.setCancelable(true);

		tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOkPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etEnterValue.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		etEnterReason.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		tvSelectReason.setVisibility(View.GONE);

		tvTitle.setText("" + "Enter Passcode");
		btnOkPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final String passcode = etEnterValue.getText().toString();

				if (TextUtils.isEmpty(passcode))
					showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Passcode field should not be empty.",getResources().getString(R.string.OK),null, "PassCodeshowAgain");
				else
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),etEnterValue.getText().toString());
							//Need to remove it.
							if(nameIDDO == null && passcode.equalsIgnoreCase(AppConstants.PASSCODE))
							{
								nameIDDO         = new NameIDDo();
								nameIDDO.strId   = passcode;
								nameIDDO.strName = passcode;
							}

							if(nameIDDO != null && nameIDDO.strId!= null && (nameIDDO.strId.equalsIgnoreCase("0") || nameIDDO.strId.equalsIgnoreCase(AppConstants.PASSCODE)))
							{
								new CommonDA().updatePasscodeStatus(nameIDDO.strName);

								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										hideLoader();
										customDialog.dismiss();
										performPasscodeAction(nameIDDo, from, isCheckOut);
									}
								});
							}
							else if(nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("1"))
								showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Entered passcode is already used, please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
							else
								showCustomDialog(BaseActivity.this, getResources().getString(R.string.warning),"Please enter a valid passcode.",getResources().getString(R.string.OK),null, "");
							hideLoader();
						}
					}).start();
				}
			}
		});
		if (!customDialog.isShowing())
			customDialog.show();
	}
	public void syncData(SyncProcessListner syncProcessListner){
		SyncData.setListener(syncProcessListner);
		Intent uploadTraIntent=new Intent(this,SyncData.class);
		startService(uploadTraIntent);
	}
	public void performPasscodeAction(NameIDDo nameIDDo, String from, boolean isCheckOut)
	{
		if(isCheckOut)
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
			sendBroadcast(intentBrObj);
		}
		else if(from.equalsIgnoreCase("finish"))
			finish();
	}


	public void correctDetailErrors(final String mgs, final String MovementCode)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				showLoader(mgs);
				GetCorrectErpApproved getcorrectERP = new GetCorrectErpApproved(BaseActivity.this,MovementCode);

				new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getCorrectInprocess(MovementCode), getcorrectERP, ServiceURLs.GetAppCorrectInProcessResponse, preference);

			}
		}).start();


	}

	/**
	 * Method to get the refreshed van load data
	 */
	public void loadAllMovements_Sync(String mgs, String empNo)
	{
		showLoader(mgs);
		GetAllMovements getAllMovements = new GetAllMovements(BaseActivity.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);

		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
//		lsd = "116270";
//		lst = "135912";

		new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus, preference);

	//	return getAllMovements.getStatus();
	}

	public void performCustomerServed()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final String siteNo = preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(new CommonDA().isOrderPlace(siteNo, CalendarUtils.getCurrentDateAsString()))
						{
							uploadData(AppStatus.POST_ORDER, AppStatus.TODAY_DATA);
							if(BaseActivity.this instanceof CheckInOptionActivity)
							{
								finish();
							}
							Intent intentBrObj = new Intent();
							intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
							sendBroadcast(intentBrObj);
						}
						else
						{
							showReasonOfCheckout(false);
						}
					}
				});
			}
		}).start();
	}

	public void setActionInViews(View view, boolean isEnable)
	{
		view.setClickable(isEnable);
		view.setEnabled(isEnable);
		view.setFocusable(isEnable);
		view.setFocusableInTouchMode(isEnable);
	}

	public void sort(List<VanLoadDO> itemLocationList)
	{
	    Collections.sort(itemLocationList, new Comparator<VanLoadDO>()
	    {
	        @Override
	        public int compare(VanLoadDO o1, VanLoadDO o2)
	        {
	           return o1.ItemCode.compareTo(o2.ItemCode);
	        }
	    });
	}

	public float getQuantityBU(ProductDO objItem, HashMap<String, Float> hmConversion)
	{
		String key      =  objItem.SKU+objItem.UOM;
		float quantityBU  =  0;
		if(hmConversion != null && hmConversion.containsKey(key))
		{
			float factor = hmConversion.get(key);
			quantityBU   = Math.round(StringUtils.getFloat(objItem.preUnits) * factor);
			//quantityBU   = (float) StringUtils.round(StringUtils.getString(StringUtils.getFloat(objItem.preUnits) * factor));
		}
		return quantityBU;
	}

	public void loadAnimation(final View view, final boolean isLeft)
	{
		Animation animation;
		if(isLeft)
			animation = AnimationUtils.loadAnimation(this,R.anim.swip_left);
		else
			animation = AnimationUtils.loadAnimation(this,R.anim.swip_right);

		view.setVisibility(View.VISIBLE);
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				view.clearAnimation();
				if(isLeft)
					view.setVisibility(View.VISIBLE);
				else
					view.setVisibility(View.GONE);
			}
		});
	}

	public boolean isInventoryAvail(ProductDO objItem, HashMap<String, HHInventryQTDO> hmInventory)
	{
		boolean isAvail = false;
		String key = objItem.SKU + objItem.UOM;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(key))
		{
			HHInventryQTDO inventryDO = hmInventory.get(key);
			objItem.BatchCode 	  	  = inventryDO.batchCode;
			int availQty 			  = Math.round(inventryDO.quantityBU);
			if(objItem.quantityBU > availQty)
			{
				isAvail = false;
				hmInventory.get(key).tempTotalQt = 0;
			}
			else
			{
				isAvail = true;
				hmInventory.get(key).tempTotalQt = StringUtils.getInt(objItem.preUnits);
			}
		}
		else
			isAvail = false;

		return isAvail;
	}

	public boolean isInventoryAvail_Replace(ProductDO objItem, HashMap<String, HHInventryQTDO> hmInventory)
	{
		boolean isAvail = false;
		String key = objItem.SKU;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(key))
		{
			HHInventryQTDO inventryDO = hmInventory.get(key);
			objItem.BatchCode 	  = inventryDO.batchCode;

			float availQty = StringUtils.getFloat(diffAmt.format(inventryDO.quantityBU));

			if(objItem.quantityBU > availQty)
				isAvail = false;
			else
				isAvail = true;
		}
		else
			isAvail = false;

		return isAvail;
	}

	public void maintainQtyinHMap(ProductDO objItem, HashMap<String, HHInventryQTDO> hmInventory, boolean isAdd)
	{
		String key      = isAdd ? objItem.RelatedLineId : objItem.SKU;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(key))
		{
			HHInventryQTDO inventryDO = hmInventory.get(key);
			objItem.BatchCode 	  = inventryDO.batchCode;

			if(isAdd)
				inventryDO.quantityBU = inventryDO.quantityBU  + objItem.quantityBU;
			else
				inventryDO.quantityBU = inventryDO.quantityBU  - objItem.quantityBU;

			hmInventory.put(key, inventryDO);
		}
	}

	public int getHoldOrderStatus(String orderId, String appId)
	{
		final HoldOrderStatusParser insertOrdersParser 	= new HoldOrderStatusParser(BaseActivity.this);
		ConnectionHelper connectionHelper = new ConnectionHelper(null);
    	connectionHelper.sendRequest_Bulk(BaseActivity.this,BuildXMLRequest.getHoldOrderStaus(orderId, appId), insertOrdersParser, ServiceURLs.GetHoldOrderStatus, preference);
        return insertOrdersParser.getHoldOrderStatus();
	}


	public boolean isCreditCustomer(JourneyPlanDO mallsDetails)
	{
		if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			return true;
		else
			return false;
	}

	public boolean isCashCustomer(JourneyPlanDO mallsDetails)
	{
		if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			return true;
		else
			return false;
	}

	public void formatPrices(ProductDO objProductDO)
	{
//		objProductDO.itemPrice  		= 	StringUtils.getFloat(diffStock.format(objProductDO.itemPrice ));
//		objProductDO.totalPrice 		= 	StringUtils.getFloat(diffAmt.format(objProductDO.totalPrice ));
//		objProductDO.unitSellingPrice 	= 	StringUtils.getFloat(diffStock.format(objProductDO.unitSellingPrice));
//		objProductDO.invoiceAmount		= 	StringUtils.getFloat(diffAmt.format(objProductDO.invoiceAmount));
	}

	//==============================Added For VAT=============================
	public void getTaxforItem(HashMap<String, ArrayList<ItemWiseTaxViewDO>> hmAlltaxItemdetails,ProductDO objProductDO) {
		ArrayList<ItemWiseTaxViewDO> arrItemTax = null;
		if(hmAlltaxItemdetails!=null && objProductDO!=null)
			arrItemTax = hmAlltaxItemdetails.get(objProductDO.SKU);

		if (arrItemTax != null) {
			objProductDO.arrApplicableTaxonItems = arrItemTax;
		}
		CalculateTaxForItem(objProductDO,objProductDO.invoiceAmount);
//		CalculateTaxForItem(objProductDO,objProductDO.unitSellingPrice*(StringUtils.getFloat(objProductDO.preUnits)));

	}

	public String getSettingsValueByName(String name)
	{
		SettingsDO settings = new SettingsDA().getSettingValueByName(name);
		if(settings != null && !TextUtils.isEmpty(settings.SettingValue))
			return settings.SettingValue;
		else
			return "";
	}
	private void CalculateTaxForItem(ProductDO objProductDO, double amount) {
		objProductDO.arrAppliedTaxes.clear();
		{
			float LineTaxAmount = 0.0f;
			ArrayList<ItemWiseTaxViewDO> arrApplicableTaxonItems = objProductDO.arrApplicableTaxonItems;
			for (ItemWiseTaxViewDO itemWiseTaxViewDO : arrApplicableTaxonItems) {
				if (itemWiseTaxViewDO.TaxCalculationType.equalsIgnoreCase("Percentage")) {
					if (itemWiseTaxViewDO != null /*&& itemWiseTaxViewDO.Count() > 0*/) {
						{
							ItemWiseTaxViewDO itemWiseTaxViewClone = new ItemWiseTaxViewDO();
							itemWiseTaxViewClone.TaxAmount = (float) ((amount) * itemWiseTaxViewDO.BaseTaxRate * (.01));
							LineTaxAmount += StringUtils.round(""+itemWiseTaxViewClone.TaxAmount,noOfRoundingOffdigits);
							itemWiseTaxViewClone.TaxRate =itemWiseTaxViewDO.BaseTaxRate; // changed from TAX rate to Base Tax Rate
//                            itemWiseTaxViewClone.TaxRate = itemWiseTaxViewDO.TaxRate;
							itemWiseTaxViewClone.TaxUID = itemWiseTaxViewDO.TaxUID;
							itemWiseTaxViewClone.TaxSlabUID = itemWiseTaxViewDO.TaxSlabUID;
							itemWiseTaxViewClone.TaxSKUMapUID = itemWiseTaxViewDO.TaxSKUMapUID;
							itemWiseTaxViewClone.TaxName = itemWiseTaxViewDO.TaxName;
							itemWiseTaxViewClone.ApplicableAt = itemWiseTaxViewDO.ApplicableAt;
							itemWiseTaxViewClone.DependentTaxUID = itemWiseTaxViewDO.DependentTaxUID;
							itemWiseTaxViewClone.TaxCalculationType = itemWiseTaxViewDO.TaxCalculationType;
							itemWiseTaxViewClone.BaseTaxRate = itemWiseTaxViewDO.BaseTaxRate;
							itemWiseTaxViewClone.RangeStart = itemWiseTaxViewDO.RangeStart;
							itemWiseTaxViewClone.RangeEnd = itemWiseTaxViewDO.RangeEnd;
							itemWiseTaxViewClone.SKUUID = itemWiseTaxViewDO.SKUUID;
							itemWiseTaxViewClone.TaxGroupName = itemWiseTaxViewDO.TaxGroupName;
							itemWiseTaxViewClone.TaxGroupDescription = itemWiseTaxViewDO.TaxGroupDescription;
							objProductDO.arrAppliedTaxes.add(itemWiseTaxViewClone);
							objProductDO.TaxPercentage		=	itemWiseTaxViewClone.TaxRate;
						}
					}

				} else if (itemWiseTaxViewDO.TaxCalculationType.equalsIgnoreCase("PercentageSlab")) {
					if (itemWiseTaxViewDO != null)
					{
						{
							ItemWiseTaxViewDO itemWiseTaxViewCloneSlab = new ItemWiseTaxViewDO();
							itemWiseTaxViewCloneSlab.TaxAmount = (float) (((amount)) * itemWiseTaxViewDO.TaxRate * (.01));
							LineTaxAmount += itemWiseTaxViewCloneSlab.TaxAmount;
							itemWiseTaxViewCloneSlab.TaxRate = itemWiseTaxViewDO.TaxRate;
							itemWiseTaxViewCloneSlab.TaxUID = itemWiseTaxViewDO.TaxUID;
							itemWiseTaxViewCloneSlab.TaxSlabUID = itemWiseTaxViewDO.TaxSlabUID;
							itemWiseTaxViewCloneSlab.TaxSKUMapUID = itemWiseTaxViewDO.TaxSKUMapUID;
							itemWiseTaxViewCloneSlab.TaxName = itemWiseTaxViewDO.TaxName;
							itemWiseTaxViewCloneSlab.ApplicableAt = itemWiseTaxViewDO.ApplicableAt;
							itemWiseTaxViewCloneSlab.DependentTaxUID = itemWiseTaxViewDO.DependentTaxUID;
							itemWiseTaxViewCloneSlab.TaxCalculationType = itemWiseTaxViewDO.TaxCalculationType;
							itemWiseTaxViewCloneSlab.BaseTaxRate = itemWiseTaxViewDO.BaseTaxRate;
							itemWiseTaxViewCloneSlab.RangeStart = itemWiseTaxViewDO.RangeStart;
							itemWiseTaxViewCloneSlab.RangeEnd = itemWiseTaxViewDO.RangeEnd;
							itemWiseTaxViewCloneSlab.SKUUID = itemWiseTaxViewDO.SKUUID;
							itemWiseTaxViewCloneSlab.TaxGroupName = itemWiseTaxViewDO.TaxGroupName;
							itemWiseTaxViewCloneSlab.TaxGroupDescription = itemWiseTaxViewDO.TaxGroupDescription;
							objProductDO.arrAppliedTaxes.add(itemWiseTaxViewCloneSlab);
							objProductDO.TaxPercentage		=	itemWiseTaxViewCloneSlab.TaxRate;
						}
					}
				}
			}
			float ProrataTaxAmount = 0.0f;
			float TotalTax =  LineTaxAmount + ProrataTaxAmount;
			objProductDO.TotalTax = StringUtils.getDoubleFromFloatWithoutError( TotalTax);
//			objProductDO.LineTaxAmount = /*(float) StringUtils.round(*/LineTaxAmount,2);
			objProductDO.LineTaxAmount = /*(float) StringUtils.round(*/StringUtils.getDoubleFromFloatWithoutError(LineTaxAmount);
		}

	}

	//==============================Added For VAT=============================


	public Bitmap getBitmap(View myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}

	public void saveDynamicAddress()
	{
		String address, facebook, twitter;

		HashMap<String, String> hmAddress = new UserInfoDA().getAddress();

		if(hmAddress != null && hmAddress.containsKey(AppConstants.BASKIN_ADDRESS))
			address = hmAddress.get(AppConstants.BASKIN_ADDRESS);
		else
			address = getString(R.string.baskin_robbin_title) +"\n"+getString(R.string.baskin_robbin_address);

		if(hmAddress != null && hmAddress.containsKey(AppConstants.BASKIN_FACEBOOK))
			facebook = hmAddress.get(AppConstants.BASKIN_FACEBOOK);
		else
			facebook = getString(R.string.baskin_facebook);

		if(hmAddress != null && hmAddress.containsKey(AppConstants.BASKIN_TWITTER))
			twitter = hmAddress.get(AppConstants.BASKIN_TWITTER);
		else
			twitter = getString(R.string.baskin_twitter);

		preference.saveStringInPreference(AppConstants.BASKIN_FACEBOOK, facebook);
		preference.saveStringInPreference(AppConstants.BASKIN_TWITTER, twitter);
		preference.commitPreference();

		//Need to get the address here.
		new BitmapsUtiles(BaseActivity.this, new OnMonochromeCreated()
		{
			@Override
			public void onCompleted(String path)
			{
				Log.e("path", path);
			}
		}).drawTextToBitmap(address, "address");
	}

	public void deletePendingInvoices()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new ARCollectionDA().deleteUnUsedPendingInvoices();
			}
		}).start();
	}

	public void blockMenu()
	{
		btnLogo.setEnabled(false);
		btnLogo.setClickable(false);

		llMenu.setEnabled(false);
		llMenu.setClickable(false);

		btnMenu.setEnabled(false);
		btnMenu.setClickable(false);
		btnMenu.setVisibility(View.INVISIBLE);
	}

 	private String blockCharacterSet = "+~#^|$%&*!')(@:;_-/\\.,?\"";

 	public InputFilter filter = new InputFilter()
    {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
        {
            if (source != null && blockCharacterSet.contains(("" + source)))
                return "";

            return null;
        }
    };

    public void disableView(final View view)
    {
    	view.setClickable(false);
    	view.setEnabled(false);
    	new Handler().postDelayed(new Runnable()
    	{
			@Override
			public void run()
			{
				view.setClickable(true);
		    	view.setEnabled(true);
			}
		}, 1000);
    }

    public DiscountDO getApplicableDiscounts(ProductDO objProductDO, JourneyPlanDO mallsDetails,
    		HashMap<String, DiscountDO> hmPromoDisc, DiscountDO objPricingDO)
	{
    	objProductDO.arrDiscList = new ArrayList<DiscountDO>();
		DiscountDO objMainDisc  	 = new CaptureInventryDA().getDisocunt(mallsDetails.site, objProductDO.CategoryId, objProductDO.SKU);

		DiscountDO objPromoDiscSKU = null;
		DiscountDO objPromoDiscCAT = null;
		DiscountDO objPromoDiscALL = null;
		if(hmPromoDisc != null && hmPromoDisc.containsKey("ALL"))
		{
			objPromoDiscALL = hmPromoDisc.get("ALL");
		}
		if(hmPromoDisc != null && hmPromoDisc.containsKey(objProductDO.CategoryId))
		{
			objPromoDiscCAT = hmPromoDisc.get(objProductDO.CategoryId);
		}
		if(hmPromoDisc != null && hmPromoDisc.containsKey(objProductDO.SKU))
		{
			objPromoDiscSKU = hmPromoDisc.get(objProductDO.SKU);
		}

		if(objMainDisc != null)
			objProductDO.arrDiscList.add(getCalculatedDiscount(objPricingDO, objMainDisc, objProductDO));

		if(objPromoDiscALL != null)
			objProductDO.arrDiscList.add(getCalculatedDiscount(objPricingDO, objPromoDiscALL, objProductDO));
		if(objPromoDiscCAT != null)
			objProductDO.arrDiscList.add(getCalculatedDiscount(objPricingDO, objPromoDiscCAT, objProductDO));
		if(objPromoDiscSKU != null)
			objProductDO.arrDiscList.add(getCalculatedDiscount(objPricingDO, objPromoDiscSKU, objProductDO));

		if(objMainDisc == null)
			objMainDisc = new DiscountDO();

		if(objPromoDiscALL == null)
			objPromoDiscALL = new DiscountDO();
		if(objPromoDiscCAT == null)
			objPromoDiscCAT = new DiscountDO();
		if(objPromoDiscSKU == null)
			objPromoDiscSKU = new DiscountDO();
		DiscountDO temp   = new DiscountDO();
		temp.discount     = objMainDisc.discount + objPromoDiscALL.discount+ objPromoDiscCAT.discount+ objPromoDiscSKU.discount;
		temp.discountType = objMainDisc.discountType;
		if(!TextUtils.isEmpty(objPromoDiscALL.description))
		{
			if(!TextUtils.isEmpty(objPromoDiscSKU.description))
				objPromoDiscSKU.description = objPromoDiscSKU.description+", "+objPromoDiscALL.description;
			else
				objPromoDiscSKU.description=objPromoDiscALL.description;
		}
		if(!TextUtils.isEmpty(objPromoDiscCAT.description) )
		{
			if(!TextUtils.isEmpty(objPromoDiscSKU.description))
				objPromoDiscSKU.description = objPromoDiscSKU.description+", "+objPromoDiscCAT.description;
			else
				objPromoDiscSKU.description=objPromoDiscCAT.description;
		}
		temp.description  = getDescrition(objMainDisc, objPromoDiscSKU);
		return temp;
	}


	private String getDescrition(DiscountDO objMainDisc, DiscountDO objPromoDisc)
	{
		String desc = "";

		if(!TextUtils.isEmpty(objMainDisc.description) && !TextUtils.isEmpty(objPromoDisc.description))
			desc = objMainDisc.description + ", "+objPromoDisc.description;
		else if(!TextUtils.isEmpty(objMainDisc.description) && TextUtils.isEmpty(objPromoDisc.description))
			desc = objMainDisc.description;
		else if(TextUtils.isEmpty(objMainDisc.description) && !TextUtils.isEmpty(objPromoDisc.description))
			desc = objPromoDisc.description;

		return desc;
	}

	private DiscountDO getCalculatedDiscount(DiscountDO objPricingDO, DiscountDO objDisc, ProductDO objProductDO)
	{
		DiscountDO temp 	= new DiscountDO();
		temp.discount       = objDisc.discount;
		temp.description 	= objDisc.description ;
		temp.ItemCode		= objProductDO.SKU;
		temp.UOM 			= objProductDO.UOM;
		temp.Quantity		= objProductDO.quantityBU;
		temp.perCaseValue   = objPricingDO.perCaseValue;
		float discountAmount= StringUtils.getFloat(objProductDO.preUnits) * StringUtils.getFloat(objPricingDO.perCaseValue) * (objDisc.discount/100);

		temp.fDiscountAmt	= discountAmount;
		return temp;
	}

	public void calculatedDiscountOnText(ProductDO objProductDO)
	{
		if(objProductDO.arrDiscList != null && objProductDO.arrDiscList.size() > 0)
		{
			for(DiscountDO temp : objProductDO.arrDiscList)
			{
				temp.Quantity		= objProductDO.quantityBU;
				float discountAmount= (new BigDecimal((StringUtils.getFloat(objProductDO.preUnits) * StringUtils.getFloat(temp.perCaseValue)) +"").multiply( new BigDecimal((temp.discount/100)+""))).floatValue();
//				float discountAmount= StringUtils.getFloat(objProductDO.preUnits) * StringUtils.getFloat(temp.perCaseValue) * (temp.discount/100);
				temp.fDiscountAmt	= discountAmount;
			}
		}
	}
	public void callCheckVersionWebService(VersionChangeListner changeListner, int methodOrigin)
	{
		if(isNetworkConnectionAvailable(BaseActivity.this))
		{
			versionCheckingHandler = new VersionCheckingHandler(BaseActivity.this);
			ConnectionHelper coh = new ConnectionHelper(null);
			coh.sendRequest(BaseActivity.this, BuildXMLRequest.getVersionDetails("Android", ""+getVersionCode(),preference.getStringFromPreference(Preference.USER_ID,"")), versionCheckingHandler, ServiceURLs.GetVersionDetails,preference);
			if(versionCheckingHandler.getData()!=null)
			{
				if(versionCheckingHandler.getData() instanceof CheckVersionDO)
					doVersionManagement((CheckVersionDO)versionCheckingHandler.getData(), changeListner, methodOrigin);
				else
					changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
			}
			else
				changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
		}
		else
			changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
	}
	private String getURL(Context mContext)
	{
		String URL = "";

		if(!NetworkUtility.isWifiConnected(mContext))
			URL = ServiceURLs.IMAGE_GLOBAL_URL;

		if(URL == null || URL.length() <= 0)
			URL = ServiceURLs.IMAGE_GLOBAL_URL;

		return URL;
	}
	public String getVersionCode()
	{
		String number = "1";
		try
		{
			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			number = versionCode+"";
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return number;
	}

	private int versionStatusCode = AppConstants.MAJOR_APP_UPDATE;

	/**
	 * */
	public void doVersionManagement(CheckVersionDO checkVersion, VersionChangeListner changeListner, int methodOrigin)
	{
			String sampleUrl=checkVersion.APKFileName;
			if(sampleUrl.contains("Data"))
				sampleUrl = getURL(BaseActivity.this)+sampleUrl;

			if(checkVersion.StatusCode==AppConstants.MINOR_APP_UPDATE || checkVersion.StatusCode==AppConstants.NORMAL_APP_UPDATE)
			{
				versionStatusCode = AppConstants.MINOR_APP_UPDATE;
				showDialogueForUpGrade("Please update the app.", checkVersion.StatusCode,sampleUrl, changeListner);
			}
			else if(checkVersion.StatusCode==AppConstants.MAJOR_APP_UPDATE)
			{
				showDialogueForUpGrade("We have a Major Update release for you. It's Mandatory that you update the app to continue using.", checkVersion.StatusCode,sampleUrl, changeListner);
			}
			else
			{
				changeListner.onVersionChanged(AppConstants.VER_NOT_CHANGED);
			}
	}

	private String apkFileUrl = "";
	private VersionChangeListner changeListner;
	private void showDialogueForUpGrade(final String strMessage,final int ResultCode, final String aPKFilePath, final VersionChangeListner listener)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					hideLoader();

					if (customDialog != null && customDialog.isShowing())
						customDialog.dismiss();

					if(upgradeDialog != null && !upgradeDialog.isShowing())
						upgradeDialog.dismiss();

					View view = getLayoutInflater().inflate(R.layout.custom_common_popup, null);
					int devic_width 	= preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 480);
					if(BaseActivity.px > 1)
						upgradeDialog = new CustomDialog(BaseActivity.this, view, devic_width, LayoutParams.WRAP_CONTENT);
					else
						upgradeDialog = new CustomDialog(BaseActivity.this, view, devic_width, LayoutParams.WRAP_CONTENT);

					if(upgradeDialog!=null)
						upgradeDialog.setOnKeyListener(dialogKeyListner);

					TextView tvTitleCustomPopup  = (TextView) view.findViewById(R.id.tvTitlePopup);
					TextView tvMsgCustomPopup    = (TextView) view.findViewById(R.id.tvMessagePopup);
					final Button btnPositivePopup = (Button) view.findViewById(R.id.btnYesPopup);
					final Button btnNegetivePopup = (Button) view.findViewById(R.id.btnNoPopup);

					tvTitleCustomPopup.setVisibility(View.VISIBLE);
					tvTitleCustomPopup.setText(getResources().getString(R.string.alert));
					tvMsgCustomPopup.setText(strMessage);
					btnNegetivePopup.setVisibility(View.VISIBLE);
					android.view.ViewGroup.LayoutParams layoutParams = btnNegetivePopup.getLayoutParams();
					btnPositivePopup.setVisibility(View.GONE);

					if(ResultCode==AppConstants.MAJOR_APP_UPDATE)
					{
						((MarginLayoutParams) layoutParams).setMargins(0, 0, 0, 0);
						btnNegetivePopup.setLayoutParams(layoutParams);
					}

					btnNegetivePopup.setText(getResources().getString(R.string.upgrade_now));
					btnNegetivePopup.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							if(upgradeDialog!=null)
								upgradeDialog.dismiss();
							try
							{
								apkFileUrl    = aPKFilePath;
								changeListner = listener;
								changeListner.onVersionChanged(AppConstants.VER_CHANGED);

							}
							catch (Exception e)
							{
								changeListner.onVersionChanged(AppConstants.VER_NO_BUTTON_CLICK);
							}
						}
					});

					btnPositivePopup.setText(getResources().getString(R.string.latter));
					btnPositivePopup.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							if(upgradeDialog!=null)
								upgradeDialog.dismiss();
							changeListner.onVersionChanged(AppConstants.VER_NO_BUTTON_CLICK);
						}
					});

					upgradeDialog.setCancelable(false);

					if(upgradeDialog != null  && !upgradeDialog.isShowing())
			    		upgradeDialog.show();// this is to show the dialog.

					setTypeFace((ViewGroup)view);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}


	DialogInterface.OnKeyListener dialogKeyListner = new DialogInterface.OnKeyListener()
	{

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				LogUtils.errorLog("Keywork Back", "Called");
	           if(dialog!=null)
	        	   dialog.dismiss();
	            return true;
	        }
			return false;
		}
	};

	public void goToPlayStoreOrDownloadAPKFile()
	{
		 showLoader("Downloading .apk file...");

		 new Thread(new Runnable()
		 {
			public void run()
			{
				final String apkFilePath = FileUtils.downloadAPKFile(apkFileUrl,BaseActivity.this);

				LogUtils.errorLog("FilePath", apkFilePath);
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						hideLoader();

						if(!TextUtils.isEmpty(apkFilePath))
						{
							try
							{
								if(versionStatusCode == AppConstants.MAJOR_APP_UPDATE)
								{
									clearApplicationData();
									if(preference==null)
										preference = new Preference(BaseActivity.this);
									preference.clearPreferences();
								}
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							Intent intent = new Intent(Intent.ACTION_VIEW);
				            intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
				            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				            startActivity(intent);
				            finish();
						}
						else
							changeListner.onVersionChanged(AppConstants.VER_UNABLE_TO_UPGRADE);
					}
				});
			}
		}).start();
	}
	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}
	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				if (!s.equals("lib")) {
					deleteDir(new File(appDir, s));
					Log.i("TAG", "File /data/data/APP_PACKAGE/" + s
							+ " DELETED");
				}
			}
		}
	}
	public boolean uploadLogData(LogDO logDO)
	{
		boolean isUpload=true;
		try
		{
			if(logDO != null)
			{
			Vector<LogDO> vecLogs= new Vector<LogDO>();
			vecLogs.add(logDO);
			BooleanParser booleanParser = new BooleanParser(BaseActivity.this);
				new ConnectionHelper(null).sendRequest(BaseActivity.this,BuildXMLRequest.getAllLogsXml(vecLogs), booleanParser, ServiceURLs.InsertVechileTracking,preference);
				if((Integer)booleanParser.getData()>0)
					isUpload=true;
				else
					isUpload=false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			isUpload=false;
		}

		return isUpload;
	}
	//-----------------------------------------------
	public void lockDrawer(final String from)
	{
		LogUtils.errorLog("Menu Drawer Locked in ", from);
//		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	public   interface  PrintPopup
	{
		public  void  selectedOption(int selectedPrinter);
		//returns either
		// public static final int WOOSIM=1;
//		public static final int DOTMATRIX=1;
	};
	public void ShowOptionPopupForPrinter(Context context, final PrintPopup p)
	{
		View view = inflater.inflate(R.layout.printer_selection_popup, null);
		final CustomDialog mCustomDialog = new CustomDialog( context, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		final Button btn_popup_English	  = (Button) view.findViewById(R.id.btn_popup_English);
		final Button btn_popup_Arabic	  = (Button) view.findViewById(R.id.btn_popup_Arabic);
		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);

		setTypeFace((ViewGroup)view);

		btn_popup_English.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				p.selectedOption(AppConstants.WOOSIM);
			}
		});

		btn_popup_Arabic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				p.selectedOption(AppConstants.DOTMATRIX);
			}
		});

		btn_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				p.selectedOption(AppConstants.CANCEL);

				mCustomDialog.dismiss();
			}
		});

		setTypeFace((LinearLayout)view);
		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}
	}
	public void  addArabicDetails(JourneyPlanDO mallsdetails)
	{
		Object obj[] = new Object[9];
		if(mallsdetails!=null) {
			obj = new CommonDA().getCustomerArabicDetails(mallsdetails.site);
			mallsdetails.SiteNameInArabic = (String) obj[0];
			mallsdetails.Address1_AR = (String) obj[1];
			mallsdetails.Address2_AR = (String) obj[2];
			mallsdetails.Address3_AR = (String) obj[3];
			mallsdetails.AreaName_AR = (String) obj[4];
			mallsdetails.PostalCode_AR = (String) obj[5];
			mallsdetails.LocationName_AR = (String) obj[6];
			mallsdetails.City_AR = (String) obj[7];
		}


	}
	public  String numToArabicWord(String str)
	{
		String s="";
		// create a BigDecimal object
//		BigDecimal bg;
//
//		// create a String object

//
//		MathContext mc = new MathContext(2); // 3 precision
//
//		bg = new BigDecimal(StringUtils.getFloat(str)+"", mc);
//
//		// assign the string value of bg to s
//		s = bg.toString();
//		return new NumberToArabic().convertToArabic(bg, "SAR");
		try {
			s =new NumberToArabic(BaseActivity.this).convertNumberToArabicWords(str);

		}catch (NumberFormatException ex)
		{
			ex.printStackTrace();
		}
		return  s;


	}
}


