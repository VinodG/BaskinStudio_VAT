package com.winit.baskinrobbin.salesman;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.AdminLoginParser;
import com.winit.baskinrobbin.parsers.UserLoginParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataobject.LoginUserInfo;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class AdministratorLoginActivity extends BaseActivity{

	private EditText etUserName,etPassword;
	private Button btnLogin;

	private ImageView ivCheck_RememberMe;
	private TextView tvForgotPassword, tvURL, tvCurrentDate,tvCurrentversion,tvRemember;
	private String strUserName,strPassword;
	private LinearLayout llLogin, ll_rememberme,llLoginBackground;
	private ConnectionHelper connectionHelper;

	@Override
	public void initialize() {

		llLogin = (LinearLayout)inflater.inflate(R.layout.login,null); 
		llBody.addView(llLogin,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llHeader.setVisibility(View.GONE);
		

		intialiseControls();
	/*	ivCheck_RememberMe.setOnClickListener(new OnClickListener()
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
		});*/


		//////////////////////////////////////////////
		final String [] URL 		 = new String[2];
		final String [] name	 	 = new String[2];
		final String [] DATABASE_URL = new String[2];

		name[0] 			= 	"LIVE";
		URL [0] 			= 	ServiceURLs.MAIN_GLOBAL_URL;
		DATABASE_URL [0] 	= 	ServiceURLs.IMAGE_GLOBAL_URL;
		
		
		preference.saveStringInPreference(Preference.MAIN_URL, URL[0]);
		preference.saveStringInPreference(Preference.ALIAS_NAME, name[0]);
		preference.saveStringInPreference(Preference.DATABASE_URL, DATABASE_URL[0]);

		/*name[1] 			= 	"LOCAL";
		URL [1]	 			= 	ServiceURLs.MAIN_LOCAL_URL;
		DATABASE_URL [1] 	= 	ServiceURLs.IMAGE_LOCAL_URL;*/

		////////////////////////////////////////////////
		//setUpURL(name, URL, DATABASE_URL);
		///////////////////////////////////////////////
		
		tvURL.setVisibility(View.GONE);

		tvURL.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(AdministratorLoginActivity.this);
				builder.setSingleChoiceItems(name, StringUtils.getInt(tvURL.getTag().toString()), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if(NetworkUtility.isWifiConnected(AdministratorLoginActivity.this) || name[which].equalsIgnoreCase("LIVE"))
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
							showCustomDialog(AdministratorLoginActivity.this, "Warning !", "You have connected with mobile data, please select Live.", "OK", null, "");
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
//		        
		        String date     =   preference.getStringFromPreference(Preference.SYNC_DATE, "");
				String currentDate = CalendarUtils.getOrderPostDate();
		       
		        if(strUserName.equals("") || strPassword.equals(""))
		        {
		        	if(strUserName.equals("") && strPassword.equals(""))
		        	{
		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), getString(R.string.enter_username_password), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strUserName.equals(""))
		        	{
		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), getString(R.string.enter_username), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strPassword.equals(""))
		        	{
		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), getString(R.string.enter_password), getString(R.string.OK), null, "");
		        		etPassword.requestFocus();
		        	}
		        }
		        else if(!isNetworkConnectionAvailable(AdministratorLoginActivity.this))
				{
		        	
//		        	String strLastUserName = preference.getStringFromPreference(Preference.USER_ID, "");
//		        	String strPassword 	   = preference.getStringFromPreference(Preference.PASSWORD, "");
//		        	
//		        	if(currentDate.contains(date) && strLastUserName != null && strPassword != null && !strLastUserName.equalsIgnoreCase("") && !strPassword.equalsIgnoreCase("") && strLastUserName.equalsIgnoreCase(etUserName.getText().toString()) && strPassword.equals(etPassword.getText().toString()))
//		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), "Internet connection is not available. Do you want to continue login in offline mode?", getString(R.string.Yes), "No", "offline");
//		        	else
		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
				}
		        else
		        {
		        	validateUser(strUserName, strPassword);
		        }
			}
		});
	}
	

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
	LoginUserInfo loginUserInfo =null;
	private void validateUser(final String strUserName, final String strPassword)
	{
		connectionHelper = new ConnectionHelper(AdministratorLoginActivity.this);
		showLoader(getString(R.string.Validating));
		new Thread(new Runnable()
		{
			@SuppressLint("DefaultLocale")
			@Override
			public void run() 
			{
				/*TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String deviceID = "352901063521986"*//*"352832064114788"*/;//telephonyManager.getDeviceId();// "352901063521887"; //352901063521986;// 352901063521903;
				final AdminLoginParser adminLoginParser 	= new AdminLoginParser(AdministratorLoginActivity.this);
				String gcmId = preference.getStringFromPreference(Preference.gcmId, "");
				connectionHelper.sendRequest_Bulk(AdministratorLoginActivity.this,BuildXMLRequest.DeviceCheckLogin(strUserName, strPassword, gcmId,""), adminLoginParser, ServiceURLs.LOGIN_METHOD,preference);
			      
//		        connectionHelper.sendRequest_Bulk(LoginAcivity.this,BuildXMLRequest.loginRequest(strUserName, strPassword, gcmId), userLoginParser, ServiceURLs.LOGIN_METHOD, preference);
		       

				loginUserInfo = adminLoginParser.getLoggedInUserInfo();//new UserInfoDA().getValidateUser(strUserName,strPassword);
				if(loginUserInfo!= null)
				{
					//Getting the LoggedIn User information
					if(!loginUserInfo.strStatus.equalsIgnoreCase("Failure"))
					{
						if(!loginUserInfo.strSalemanType.toLowerCase().contains(AppConstants.USER_TYPE_ADMIN.toLowerCase())){
			        		hideLoader();
			        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), getString(R.string.you_are_not_authorized_to_login), getString(R.string.OK), null, "");
						}else{
							preference.saveStringInPreference(preference.ADMIN_CODE,strUserName);
							preference.commitPreference();
							afterSuccessfullLogin();
						}
					}
		        	else
		        	{
		        		hideLoader();
		        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), loginUserInfo.strMessage, getString(R.string.OK), null, "");
		        	}
				}
				else
				{
					hideLoader();
	        		showCustomDialog(AdministratorLoginActivity.this, getString(R.string.warning), "Unable to login, please try again.", getString(R.string.OK), null, "");
				}
			
			}
		}).start();
	}
	
	private void afterSuccessfullLogin()
	{
		Intent intent = new Intent(AdministratorLoginActivity.this,ResetActivity.class);
		startActivity(intent);
		finish();
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

	private void intialiseControls()
	{
		// all the fields from the login.xml is taken here Record
		etUserName					=	(EditText)llLogin.findViewById(R.id.etUserName);
		etPassword					=	(EditText)llLogin.findViewById(R.id.etPassword);
		ivCheck_RememberMe			=	(ImageView)llLogin.findViewById(R.id.ivCheck_rememberMe);
		btnLogin					=	(Button)llLogin.findViewById(R.id.btnLogin);
		tvForgotPassword    		=   (TextView)llLogin.findViewById(R.id.tvForgotPassword);
		tvRemember    				=   (TextView)llLogin.findViewById(R.id.tvRemember);
		tvURL			   			=   (TextView)llLogin.findViewById(R.id.tvURL);
		ll_rememberme				=   (LinearLayout)llLogin.findViewById(R.id.rlRememberMe);
		tvCurrentDate				=   (TextView)llLogin.findViewById(R.id.tvCurrentDate);
		tvCurrentversion			=   (TextView)llLogin.findViewById(R.id.tvCurrentversion);
		llLoginBackground			=   (LinearLayout)llLogin.findViewById(R.id.llLoginBackground);
		llLoginBackground.setBackgroundResource(R.drawable.loginlogo_admin);
		tvRemember.setVisibility(View.GONE);
		etUserName.setHint("Admin Code");
		tvCurrentDate.setVisibility(View.GONE);
		tvCurrentversion.setVisibility(View.GONE);
	}

}
