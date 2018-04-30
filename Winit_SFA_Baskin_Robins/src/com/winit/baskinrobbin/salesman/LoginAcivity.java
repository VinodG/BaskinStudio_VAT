package com.winit.baskinrobbin.salesman;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.parsers.GetAppActiveParser;
import com.winit.baskinrobbin.parsers.GetMasterDataParser;
import com.winit.baskinrobbin.parsers.UserLoginParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.SyncData.SyncProcessListner;
import com.winit.baskinrobbin.salesman.common.UploadData;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ClearDataDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.LoginUserInfo;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.listeners.VersionChangeListner;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.FileUtils;
import com.winit.baskinrobbin.salesman.utilities.FileUtils.DownloadListner;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import java.io.File;

import static com.winit.baskinrobbin.salesman.common.Preference.gcmId;

public class LoginAcivity extends BaseActivity implements DownloadListner,SyncProcessListner
{
	//declaration of variables
	private EditText etUserName,etPassword;
	private TextView tvURL,tvadminLogin;
	private Button btnLogin;
	private ImageView ivCheck_RememberMe;
	private TextView tvForgotPassword;
	private String strUserName,strPassword, strLastPassword;
	private LinearLayout llLogin, ll_rememberme,llLoginBackground;
	private ConnectionHelper connectionHelper;
	public static final int REQUEST_CODE_ADMIN = 500;
	
	
	@Override
	public void initialize() 
	{
		//inflate the login layout
		llLogin = (LinearLayout)inflater.inflate(R.layout.login,null);
		llBody.addView(llLogin,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		llHeader.setVisibility(View.GONE);
		
		intialiseControls();
		tvadminLogin.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
		//check if there is any vehicle available or not
				new Thread(new Runnable() {
					public void run() {
						final String vehicleNO= new VehicleDA().getVehicleNo(preference.getStringFromPreference(preference.EMP_NO, ""));
						runOnUiThread(new Runnable() {
							public void run() {
								if(TextUtils.isEmpty(vehicleNO)){
									Toast.makeText(LoginAcivity.this,"No Salesman login to this HHD", Toast.LENGTH_SHORT).show();
									
								}else {
								//	showLoader(getString(R.string.please_wait));
									Intent intent		=	new Intent(LoginAcivity.this,AdministratorLoginActivity.class);
									startActivityForResult(intent, REQUEST_CODE_ADMIN);
									overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
//									finish();
								}
							}
						});
					}
				}).start();

			}
			
		});
		ivCheck_RememberMe.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(preference.getbooleanFromPreference(Preference.REMEMBER_ME, false))
				{
					ivCheck_RememberMe.setImageResource(R.drawable.unchecked);
					preference.saveBooleanInPreference(Preference.REMEMBER_ME, false);
					preference.commitPreference();
				}
				else
				{
					ivCheck_RememberMe.setImageResource(R.drawable.checked);
					preference.saveBooleanInPreference(Preference.REMEMBER_ME, true);
					preference.commitPreference();
				}
			}
		});
		
	
		final String [] URL 		 = new String[2];
		final String [] name	 	 = new String[2];
		final String [] DATABASE_URL = new String[2];
		
		name[0] 			= 	"LIVE";
		URL [0] 			= 	ServiceURLs.MAIN_GLOBAL_URL;
		DATABASE_URL [0] 	= 	ServiceURLs.IMAGE_GLOBAL_URL;
		
		name[1] 			= 	"LOCAL";
		URL [1]	 			= 	ServiceURLs.MAIN_GLOBAL_URL;
		DATABASE_URL [1] 	= 	ServiceURLs.IMAGE_GLOBAL_URL;
		
		////////////////////////////////////////////////
		setUpURL(name, URL, DATABASE_URL);
		///////////////////////////////////////////////
		
		tvURL.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginAcivity.this);
				builder.setSingleChoiceItems(name, StringUtils.getInt(tvURL.getTag().toString()), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if(NetworkUtility.isWifiConnected(LoginAcivity.this) || name[which].equalsIgnoreCase("LIVE"))
						{
							tvURL.setText(name[which]);
							tvURL.setTag(which);
							preference.saveStringInPreference(Preference.MAIN_URL, URL[which]);
							preference.saveStringInPreference(Preference.ALIAS_NAME, name[which]);
							preference.saveStringInPreference(Preference.DATABASE_URL, DATABASE_URL[which]);
							preference.commitPreference();
							dialog.dismiss();
						}
						else
							showCustomDialog(LoginAcivity.this, "Warning !", "You have connected with mobile data, please select Live.", "OK", null, "");
					}
				});
				builder.show();
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etPassword.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				//need to be removed

				strUserName		= 	etUserName.getText().toString();
		        strPassword		=   etPassword.getText().toString();
		        strLastPassword 	   = preference.getStringFromPreference(Preference.PASSWORD, "");
		        String date     =   preference.getStringFromPreference(Preference.SYNC_DATE, "");
				String currentDate = CalendarUtils.getOrderPostDate();
		       
		        if(strUserName.equals("") || strPassword.equals(""))
		        {
		        	if(strUserName.equals("") && strPassword.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_username_password), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strUserName.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_username), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strPassword.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_password), getString(R.string.OK), null, "");
		        		etPassword.requestFocus();
		        	}
		        }
		        else
		        {
		        	validateUser(strUserName, strPassword,strLastPassword);
		        }
			}
		});
		
		tvForgotPassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
		
//		if(preference.getbooleanFromPreference(Preference.REMEMBER_ME, false))
//		{
			etUserName.setText(preference.getStringFromPreference(Preference.USER_ID, ""));
			etPassword.setText(preference.getStringFromPreference(Preference.PASSWORD, ""));
			ivCheck_RememberMe.setImageResource(R.drawable.checked);
//		}
		
		ll_rememberme.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ivCheck_RememberMe.performClick();
			}
		});
		
		setTypeFace(llLogin);
		
		deletePendingInvoices();
	}
	 public String getMacAddress()
	   {
		   String m_deviceId = "";
		   try
		   {
			   TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
			   m_deviceId = TelephonyMgr.getDeviceId();
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		   return m_deviceId;
	   }
	private void setUpURL(String name[], String URL[], String DATABASE_URL[]) 
	{
		String ALIAS_NAME = preference.getStringFromPreference(Preference.ALIAS_NAME, "");
		
		if(ALIAS_NAME == null || ALIAS_NAME.length() <= 0)
		{
			tvURL.setTag(0);
			tvURL.setText(name[0]);
			preference.saveStringInPreference(Preference.MAIN_URL, URL[0]);
			preference.saveStringInPreference(Preference.ALIAS_NAME, name[0]);
			preference.saveStringInPreference(Preference.DATABASE_URL, DATABASE_URL[0]);
			preference.commitPreference();
		}
		else
		{
			if(ALIAS_NAME.equalsIgnoreCase(name[0]))
			{
				tvURL.setTag(0);
				tvURL.setText(name[0]);
				preference.saveStringInPreference(Preference.MAIN_URL, URL[0]);
				preference.saveStringInPreference(Preference.ALIAS_NAME, name[0]);
				preference.saveStringInPreference(Preference.DATABASE_URL, DATABASE_URL[0]);
				preference.commitPreference();
			}
			else
			{
				tvURL.setTag(1);
				tvURL.setText(name[1]);
				preference.saveStringInPreference(Preference.MAIN_URL, URL[1]);
				preference.saveStringInPreference(Preference.ALIAS_NAME, name[1]);
				preference.saveStringInPreference(Preference.DATABASE_URL, DATABASE_URL[1]);
				preference.commitPreference();
			}
		}	
	}
	
	/** initializing all the Controls  of Login class **/
	private void intialiseControls()
	{
		// all the fields from the login.xml is taken here Record
		etUserName					=	(EditText)llLogin.findViewById(R.id.etUserName);
		etPassword					=	(EditText)llLogin.findViewById(R.id.etPassword);
		ivCheck_RememberMe			=	(ImageView)llLogin.findViewById(R.id.ivCheck_rememberMe);
		btnLogin					=	(Button)llLogin.findViewById(R.id.btnLogin);
		tvForgotPassword    		=   (TextView)llLogin.findViewById(R.id.tvForgotPassword);
		tvURL			   			=   (TextView)llLogin.findViewById(R.id.tvURL);
		ll_rememberme				=   (LinearLayout)llLogin.findViewById(R.id.rlRememberMe);
		llLoginBackground			=   (LinearLayout)llLogin.findViewById(R.id.llLoginBackground);
		TextView tvCurrentDate		=   (TextView)llLogin.findViewById(R.id.tvCurrentDate);
		TextView tvCurrentversion	=   (TextView)llLogin.findViewById(R.id.tvCurrentversion);
		tvadminLogin=   (TextView)llLogin.findViewById(R.id.tvRemember);
		
		 if(ServiceURLs.MAIN_GLOBAL_URL.equalsIgnoreCase(ServiceURLs.URL_Live))
			 tvCurrentversion.setText(getString(R.string.app_name_ver_live));
	  else if(ServiceURLs.MAIN_GLOBAL_URL.equalsIgnoreCase(ServiceURLs.URL_Test))
		  tvCurrentversion.setText(getString(R.string.app_name_ver_test));
//		etUserName.setEnabled(false);
//		etUserName.setClickable(false);
		llLoginBackground.setBackgroundResource(R.drawable.loginlogo);
	}
	
	LoginUserInfo loginUserInfo =null;
	private void validateUser(final String strUserName, final String strPassword, String strLastPassword)
	{
		connectionHelper = new ConnectionHelper(LoginAcivity.this);
		showLoader(getString(R.string.Validating));
//		uploadDatabaseIntoSDCARD();
		
		final String newEmpNo = etUserName.getText().toString();//preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
		final String newPassword=etPassword.getText().toString();
		final String oldEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
		final String oldPassword=preference.getStringFromPreference(Preference.PASSWORD,"");	
		if(!isNetworkConnectionAvailable(LoginAcivity.this))
		{
			if(newEmpNo!=null && newPassword!=null
					&&!TextUtils.isEmpty(newEmpNo) && newEmpNo.equalsIgnoreCase(oldEmpNo)
					&& !TextUtils.isEmpty(newPassword) && newPassword.equalsIgnoreCase(oldPassword)){
				
        		showCustomDialog(LoginAcivity.this,getString(R.string.warning), "Are you sure want to login offline mode !", "Yes", "NO", "Offline_mode", true);
        	}
			else if(TextUtils.isEmpty(oldEmpNo))
				showCustomDialog(LoginAcivity.this, getString(R.string.warning),  getString(R.string.no_internet), getString(R.string.OK), null, "");
			else if(!TextUtils.isEmpty(newPassword) && !newPassword.equalsIgnoreCase(oldPassword))
				showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Invalid password", getString(R.string.OK), null, "");
        	else
        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Device not mapped to this user.", getString(R.string.OK), null, "");
        	hideLoader();
		}else{
			
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{

					final GetAppActiveParser getAppActiveParser 	= new GetAppActiveParser(LoginAcivity.this);
					connectionHelper.sendRequest(LoginAcivity.this,BuildXMLRequest.appActiveRequest(), getAppActiveParser, ServiceURLs.GetAppStatus,preference);
					String status = getAppActiveParser.getAppStatus();

					if(TextUtils.isEmpty(status) || status.equalsIgnoreCase("True")){
					TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					String deviceID =telephonyManager.getDeviceId();
					deviceID ="351692093878758";
					final UserLoginParser userLoginParser 	= new UserLoginParser(LoginAcivity.this);
					String gcmId = preference.getStringFromPreference(Preference.gcmId, "");
					gcmId = "APA91bHTpcrHBF88EaJDxKL7eusKGLApTXhxxmFxvjkVT6NirDEMLOwjQY-RLG1HkiOKTFXQN5u1bj-R25OfSanedq8yuQdcC6ZP9ny-KYo1q0b3N5HrteCuqZGyfPWVIc62L-5l6LxJKGqMGvpPK9EssOok3IUe5A";
					connectionHelper.sendRequest_Bulk(LoginAcivity.this,BuildXMLRequest.DeviceCheckLogin(strUserName, strPassword, gcmId,deviceID), userLoginParser, ServiceURLs.LOGIN_METHOD,preference);
					loginUserInfo = userLoginParser.getLoggedInUserInfo();//new UserInfoDA().getValidateUser(strUserName,strPassword);
					if(loginUserInfo!= null)
					{
						//Getting the LoggedIn User information
						if(!loginUserInfo.strStatus.equalsIgnoreCase("Failure") )
						{
							afterSuccessfullLogin();
						}
						else
						{
							hideLoader();
							showCustomDialog(LoginAcivity.this, getString(R.string.warning), loginUserInfo.strMessage, getString(R.string.OK), null, "");
						}
					}
					else
					{
						hideLoader();
						showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Unable to login, please try again.", getString(R.string.OK), null, "");
					}
				}
					else
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								showCustomDialog(LoginAcivity.this, getString(R.string.warning), ""+getString(R.string.please_contact_service_provider), getString(R.string.OK), null, "");
							}
						});
					}
				}
			}).start();
		}
		 
	}
	private void afterSuccessfullLogin()
	{
		callCheckVersionWebService(new VersionChangeListner() {
			
			@Override
			public void onVersionChanged(int status) {
				if(status == AppConstants.VER_CHANGED)
				{
					if(isNetworkConnectionAvailable(LoginAcivity.this))
					{
						String oldEmpNo = preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
						String newEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
//						final boolean isAllUploaded 	  		  = new GetOrdersToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllLPOUploaded 	  		  = new GetLPOOrdersToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllLPOUpdated 	  		  = new GetLPOOrdersToUpdate(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllOnHoldOrderUploaded 	  = new CompleteOnHoldOrder(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean allPaymentsUploaded 		  = new GetPaymentsToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadPayments(oldEmpNo);
//						final boolean isUploaded    	  		  = new GetMovementToUpload(LoginAcivity.this, preference).getMovementToUpload(oldEmpNo);
						
						boolean isCnDoClearData  = new ClearDataDA().isCanDoClearData();
						if(isCnDoClearData)
							goToPlayStoreOrDownloadAPKFile();
						else{
							showCustomDialog(LoginAcivity.this,getString(R.string.warning),"Some of the Offline data has not been pushed,Please go through settings to upload data ",getString(R.string.OK),null,"",false);
							hideLoader();
						}
//							uploadData(AppStatus.ALL,AppStatus.ALL_DATA);
					}
					else
						showCustomDialog(LoginAcivity.this, "Warning !", getResources().getString(R.string.no_internet), " OK ", "", "", false);
				}
				else if(status == AppConstants.VER_NO_BUTTON_CLICK)
					performLogin();
				else if(status == AppConstants.VER_UNABLE_TO_UPGRADE)
					showCustomDialog(LoginAcivity.this, "Warning !", "Unable to upgrade, please try again.", " OK ", "", "", false);
				else if(status == AppConstants.VER_NOT_CHANGED)
					performLogin();
			}
		}, AppConstants.CALL_FROM_LOGIN);
	}
	boolean isIncrementalSync=false;
	private void performLogin()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				boolean isActive = getAppAccessStatus(loginUserInfo);
				if(!isActive)
					showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Application is disabled by administrator. Please contact service provider.", getString(R.string.OK), null, "finishapp");
				else
				{
//					sendUserDetailMIM("Loading details...",loginUserInfo);
					
					preference.saveStringInPreference(Preference.USER_ID, strUserName);
					preference.saveStringInPreference(Preference.PASSWORD, strPassword);
					preference.removeFromPreference("lastservedcustomer");
					String oldEmpNo = preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
					String newEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
					String date     = preference.getStringFromPreference(Preference.SQLITE_DATE, CalendarUtils.getOrderPostDate());
					
					String currentDate = CalendarUtils.getOrderPostDate();
					LogUtils.errorLog("oldUserId", oldEmpNo+"newEmpNo "+newEmpNo);
						
					if(!TextUtils.isEmpty(oldEmpNo) && oldEmpNo.equalsIgnoreCase(newEmpNo))
//								startSync(loginUserInfo);
						syncData(LoginAcivity.this);
					else
					{
//						final boolean isAllUploaded 	  		  = new GetOrdersToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllLPOUploaded 	  		  = new GetLPOOrdersToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllLPOUpdated 	  		  = new GetLPOOrdersToUpdate(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean isAllOnHoldOrderUploaded 	  = new CompleteOnHoldOrder(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadOrders(oldEmpNo);
//						final boolean allPaymentsUploaded 		  = new GetPaymentsToUpload(LoginAcivity.this, preference, AppStatus.ALL_DATA).uploadPayments(oldEmpNo);
//						final boolean isUploaded    	  		  = new GetMovementToUpload(LoginAcivity.this, preference).getMovementToUpload(oldEmpNo);
						
//						if(TextUtils.isEmpty(oldEmpNo) || (isAllUploaded && allPaymentsUploaded && isUploaded && isAllLPOUploaded && isAllLPOUpdated && isAllOnHoldOrderUploaded))
							performDownloadMasterData(loginUserInfo, oldEmpNo);
//						else
//							uploadOLDData(loginUserInfo, oldEmpNo);
					}
				}
			}
		}).start();
	}
	private boolean loadMasterData(String strEmpNo, String mgs, LoginUserInfo loginUserInfo)
	{
		try 
		{
			copyOldDatabase();
			showLoader(mgs);
			
			GetMasterDataParser getDiscountParser 	= new GetMasterDataParser(LoginAcivity.this);
			connectionHelper.sendRequest_Bulk(LoginAcivity.this,BuildXMLRequest.getMasterDate(strEmpNo), getDiscountParser, ServiceURLs.GetMasterDataFile, preference);
			String url = getDiscountParser.getMasterDataURL();
			
			if(url != null && url.length() >= 0)
			{
				String mainURL 	= 	preference.getStringFromPreference(Preference.DATABASE_URL, ServiceURLs.IMAGE_GLOBAL_URL);
				url 			= 	String.format(url, mainURL);
			}
//			url="http://83.111.232.51/PRODSFAVAT/data/UploadSQLiteFromSettings/636605931827929072.zip";
			showDownloadProgressBar();
			
			if(!downloadSQLITE(url, LoginAcivity.this))
			{
				dismissProgressDIalog();
				preference.saveBooleanInPreference(Preference.IS_SQLITE_DOWNLOADED, false);
				preference.commitPreference();
				return false;
			}
			else
			{
				preference.saveStringInPreference(Preference.TEMP_EMP_NO, preference.getStringFromPreference(Preference.EMP_NO, ""));
				preference.saveStringInPreference(Preference.SQLITE_DATE, CalendarUtils.getOrderPostDate());
				preference.saveBooleanInPreference(Preference.IS_SQLITE_DOWNLOADED, true);
				preference.commitPreference();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	
	private void copyOldDatabase()
	{
		try
		{
			String path = Environment.getExternalStorageDirectory().toString()+"/Baskin/DBBackUp";
			
			if(!new File(path).exists())
				new File(path).mkdirs();
			
			FileUtils.deleteOldBackups(path);
			
			String fileName  = CalendarUtils.getOrderPostDate()+".sqlite";
			
			uploadDatabaseIntoSDCARD(path, fileName, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	private void clearSynchTime(String oldEmpNo)
	{
		new SynLogDA().clearSynchLog();
		
		preference.removeFromPreference(ServiceURLs.GET_DISCOUNTS+oldEmpNo+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GET_CUSTOMER_SITE+oldEmpNo+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+oldEmpNo+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+oldEmpNo+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(Preference.OFFLINE_DATE);
		
		preference.removeFromPreference(ServiceURLs.GET_ALL_VEHICLES+oldEmpNo+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GET_All_PRICE_WITH_SYNC+Preference.LAST_SYNC_TIME);
	
		preference.removeFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME);
	
		preference.removeFromPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME);
		
		preference.removeFromPreference(Preference.GetAllPromotions);
		
		preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
		
		preference.saveBooleanInPreference(Preference.IsStockVerified, false);
		
		preference.saveIntInPreference(Preference.STARTDAY_VALUE, preference.getIntFromPreference(Preference.ENDDAY_VALUE, 0));
		
		preference.saveStringInPreference(Preference.STARTDAY_TIME, "");
		
		preference.commitPreference();
	}

	@Override
	public void onProgrss(final int count) 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(dialogDownload!=null)
				{
					progressBar.setProgress(count);
					tvProgress.setText(count+" %");
				}
			}
		});
	}
	
	@Override
	public void onComplete() 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				dismissProgressDIalog();
				showLoader("Saving detail...");
			}
		});
		
	}
	
	@Override
	public void onError() 
	{
		dismissProgressDIalog();
	}
	
	private TextView tvProgress;
	private ProgressBar progressBar;
	private Dialog dialogDownload;
	
	@SuppressLint("NewApi")
	private void showDownloadProgressBar()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				View v = inflater.inflate(R.layout.progressdialog, null);
				setTypeFace((LinearLayout)v);
				progressBar  = (ProgressBar) v.findViewById(R.id.prgbar);
				tvProgress = (TextView) v.findViewById(R.id.tvprogress);
				
				if(dialogDownload == null)
				{
					dialogDownload = new Dialog(LoginAcivity.this);
					dialogDownload.setTitle("");
					dialogDownload.setCancelable(false);
				}
				
				dialogDownload.setContentView(v,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				dialogDownload.getWindow().setGravity(Gravity.CENTER);
				dialogDownload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				
				progressBar.setMax(100);
				progressBar.setProgress(0);
				tvProgress.setText("0 %");
				hideLoader();
				dialogDownload.show();
			}
		});
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("Offline_mode")) 
		{
        		showLoader(getString(R.string.please_wait)); 
    			moveToVehicleList(null);
		}
		else if(from.equalsIgnoreCase("finishapp"))
			finish();
		else if(from.equalsIgnoreCase("relogin"))
		{
			if(isNetworkConnectionAvailable(LoginAcivity.this))
				btnLogin.performClick();
			else
			{
				hideLoader();
				showCustomDialog(LoginAcivity.this, getString(R.string.warning),getString(R.string.no_internet) , getString(R.string.OK), null, "");
			}
		}
		else if(from.equalsIgnoreCase("offline"))
		{
			showLoader(getString(R.string.please_wait)); 
			moveToVehicleList(null);
		}
		else if(from.equalsIgnoreCase("logoutAdmin"))
		{
			etUserName.setText("");
			etPassword.setText("");
			hideLoader();
			showCustomDialog(LoginAcivity.this, getString(R.string.confirmation),getString(R.string.Reset_Done), getString(R.string.OK), null, "");
			
		}
	}
	
	@Override
	public void onButtonYesClick(String from, String params) {
		super.onButtonYesClick(from, params);
		
		if(from.equalsIgnoreCase("error_reset_stock"))
		{
			hideLoader();
			showCustomDialog(LoginAcivity.this, getString(R.string.confirmation),params, getString(R.string.OK), null, "");
			
		}
		else if(from.equalsIgnoreCase("Data_cleared"))
		{
			etUserName.setText("");
			etPassword.setText("");
			hideLoader();
			showCustomDialog(LoginAcivity.this, getString(R.string.confirmation),params, getString(R.string.OK), null, "");
			
		}
	}
	
//	private void startSync(final LoginUserInfo loginUserInfo)
//	{
//		if(isNetworkConnectionAvailable(LoginAcivity.this))
//		{
//			Intent intent = new Intent(LoginAcivity.this, SyncData.class);
//			startService(intent);
//			
//			SyncData.setListener(new UploadDataListener() 
//			{
//				@Override
//				public void updateStatus(int status, String message) 
//				{
//					if(status == SyncData.NO_INTERNET)
//					{
//						SyncData.setListener(null);
//						hideLoader();
//						showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
//					}
//					else if(status == SyncData.ERROR)
//					{
//						SyncData.setListener(null);
//						hideLoader();
//						showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Error occurred while syncing data, Please try again.", getString(R.string.OK), null, "");
//					}
//					else if(status != SyncData.END)
//						showLoader(message);
//					else
//					{
//						preference.saveStringInPreference(Preference.SYNC_DATE, CalendarUtils.getOrderPostDate());
//						preference.commitPreference();
//						moveToVehicleList(loginUserInfo);
//					}
//				}
//			});
//		}
//		else
//			showCustomDialog(LoginAcivity.this, "Alert !", getString(R.string.no_internet), "OK", null, "");
//	}
	@Override
	public void start() {
		showLoader("Syncing Data...0%");
	}

	@Override
	public void error() {
	}

	@Override
	public void end() {
		hideLoader();
		moveToVehicleList(loginUserInfo);
	}

	@Override
	public void progress(String msg) {
		showLoader(msg);		
	}
	private void moveToVehicleList(final LoginUserInfo loginUserInfo)
	{
		if(loginUserInfo != null)
		{
			new UserInfoDA().insertLoggedUserInfo(loginUserInfo);
			saveDynamicAddress();
		}
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				preference.removeFromPreference(Preference.LAST_CUSTOMER_SITE_ID);
				preference.removeFromPreference(Preference.CUSTOMER_NAME);
						
				if(loginUserInfo != null)
				{
					preference.saveStringInPreference(Preference.LAST_SYNC_TIME, CalendarUtils.getCurrentDateAsString());
					preference.saveBooleanInPreference(Preference.IS_DATA_SYNCED_FOR_USER+loginUserInfo.strUserId, true);
					
					if(preference.getbooleanFromPreference("isRememberChecked", false))
						preference.saveStringInPreference(Preference.PASSWORD, strPassword);
					else
						preference.saveStringInPreference(Preference.PASSWORD, "");
					
					preference.saveStringInPreference(Preference.EMP_NO, loginUserInfo.strEmpNo);
					preference.saveStringInPreference(Preference.USER_NAME, loginUserInfo.strUserName);
					preference.saveStringInPreference(Preference.REGION, loginUserInfo.strREGION);
		        	preference.saveStringInPreference(Preference.USER_TYPE,loginUserInfo.strRole);
					preference.saveStringInPreference(Preference.PASSWORD, strPassword);
					preference.saveStringInPreference(Preference.USER_ID, loginUserInfo.strUserId);
					
					scheduleBackgroundtask();
				}
				preference.commitPreference();
				hideLoader();
				
				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_AM))
				{
					Intent intent	=	new Intent(LoginAcivity.this, PresellerJourneyPlan.class);
					startActivity(intent);
				}
				else
				{
					Intent intent	=	new Intent(LoginAcivity.this, VehicleList.class);
					startActivity(intent);
				}
			}
		});
	}
	
	private void dismissProgressDIalog()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				if(dialogDownload!=null && dialogDownload.isShowing())
					dialogDownload.dismiss();
			}
		});
	}
	
	private void performDownloadMasterData(LoginUserInfo loginUserInfo , String oldEmpNo)
	{
		clearSynchTime(oldEmpNo);
		boolean isDownloaded = loadMasterData(loginUserInfo.strEmpNo, "Loading master data file...", loginUserInfo);
		if(!isDownloaded)
		{
			hideLoader();
			showCustomDialog(LoginAcivity.this, "Warning !", "Error occurred while downloading sqlite file. Please press 'OK' to try again.", " OK ", "Cancel", "relogin", false);
			return;
		}
		else
			moveToVehicleList(loginUserInfo);
	}
	
	private void uploadOLDData(final LoginUserInfo loginUserInfo , final String oldEmpNo) 
	{
		showLoader("Uploading data...");
		uploadData(AppStatus.ALL, AppStatus.ALL_DATA);
		UploadData.setListener(new UploadDataListener()
		{
			@Override
			public void updateStatus(int status, String message)
			{
				showLoader(message);
				if(status == UploadData.END)
				{
					UploadData.setListener(null);
					boolean isCnDoClearData  = new ClearDataDA().isCanDoClearData();
					if(isCnDoClearData)					
					if((isCnDoClearData) 
							&& !oldEmpNo.equalsIgnoreCase("") && oldEmpNo.equalsIgnoreCase(loginUserInfo.strEmpNo))
						syncData(LoginAcivity.this);
					else
						performDownloadMasterData(loginUserInfo , oldEmpNo);
				}
			}
		});
	}
	
	public boolean getAppAccessStatus(LoginUserInfo loginUserInfo)
	{
		boolean isAllowed = true;
		try
		{
			String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			isAllowed = connectionHelper.sendDEVRequest(BuildXMLRequest.getUserStatus(deviceId, loginUserInfo.strUserId, loginUserInfo.strRole, CalendarUtils.getCurrentDateTime()), ServiceURLs.getUserStatus);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return isAllowed;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode  == REQUEST_CODE_ADMIN && resultCode == RESULT_CANCELED){
			showLoader("Please Wait");
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					hideLoader();
				}
			}, 3000);
		}
		
	}
	private void sendUserDetailMIM(String mgs, LoginUserInfo loginUserInfo) 
	{
		showLoader(mgs);
		String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		connectionHelper.sendDEVRequest(BuildXMLRequest.insertLoginAction(loginUserInfo, deviceId, CalendarUtils.getCurrentDateTime()), ServiceURLs.insertLoginAction);
	}
	
	
}
