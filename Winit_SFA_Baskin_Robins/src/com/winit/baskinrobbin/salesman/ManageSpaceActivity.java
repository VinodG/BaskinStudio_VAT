package com.winit.baskinrobbin.salesman;


import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.parsers.ImageUploadParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.SyncData;
import com.winit.baskinrobbin.salesman.common.SyncData.SyncProcessListner;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ClearDataDA;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.utilities.UploadSQLite;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class ManageSpaceActivity extends BaseActivity implements UploadDataListener,SyncProcessListner
{
	//declaration of variables
	private LinearLayout llSettings,llClearData;
	private TextView  tvSettings,  tvUploadData;
	private Button btnSync, btnUploadData;
	private boolean isSyncCompleted=true;
	
	@Override
	public void initialize()
	{
		//inflate the settings layout
		llSettings = (LinearLayout)inflater.inflate(R.layout.managespace, null);
		llBody.addView(llSettings,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		lockDrawer("ManageSpaceActivity");
		tvUserName.setVisibility(View.GONE);
		tvUserType.setVisibility(View.GONE);
		
		intialiseControls();
		bindingControl();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_GOTO_SETTINGS_FINISH);
		registerReceiver(FinishReceiver, filter);
		if(!isSyncCompleted)
			showLoader("Syncing...");
		btnMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}	
		});
//		initializeRequiredVariables();
	}
	
	BroadcastReceiver FinishReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equalsIgnoreCase(AppConstants.ACTION_GOTO_SETTINGS_FINISH))
				finish();
		}
	};
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unregisterReceiver(FinishReceiver);
	}
	
	private void intialiseControls() 
	{
		tvSettings				=	(TextView) llSettings.findViewById(R.id.tvSettings);
		tvUploadData			=	(TextView) llSettings.findViewById(R.id.tvUploadData);
		btnSync					=	(Button) llSettings.findViewById(R.id.btnSync);
		btnUploadData			=	(Button) llSettings.findViewById(R.id.btnUploadData);
		llClearData				=	(LinearLayout) llSettings.findViewById(R.id.llClearData);
		btnCheckOut.setVisibility(View.INVISIBLE);
		ivLogOut.setVisibility(View.INVISIBLE);
		btnUploadData.setVisibility(View.VISIBLE);
		
		
		setTypeFace(llSettings);
	}
	
	private void bindingControl()
	{
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		tvUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo))
				{
					String path = Environment.getExternalStorageDirectory().toString();
					uploadDatabaseIntoSDCARD(path, AppConstants.DATABASE_NAME, false);
				}
			}
		});
		
		llClearData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo))
					clearData();
			}
		});
		
		btnSync.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo)){
					if(!isNetworkConnectionAvailable(ManageSpaceActivity.this))
						showCustomDialog(ManageSpaceActivity.this, "Alert!", getString(R.string.no_internet), "OK", null, "");
					else
					{
						if(!TextUtils.isEmpty(empNo)){
							showLoader("Syncing...");
							syncData(ManageSpaceActivity.this);
						}
//						showLoader("Syncing...");
//						isSyncCompleted = false;
//						Intent intent = new Intent(ManageSpaceActivity.this, SyncData.class);
//						startService(intent);
						
//						SyncData.setListener(new UploadDataListener() 
//						{
//							@Override
//							public void updateStatus(int status, String message) 
//							{
//								isSyncCompleted = true;
//								hideLoader();
//							}
//						});
					}
				}
			}
		});
		
		//Sending date as null to upload all the data.
		btnUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo)){
					if(!isNetworkConnectionAvailable(ManageSpaceActivity.this))
						showCustomDialog(ManageSpaceActivity.this, "Alert!", getString(R.string.no_internet), "OK", null, "");
					else 
					{
					}
				}
				
			}
		});
		
	}
//	private void initializeRequiredVariables(){
//		try {
//			AppConstants.DATABASE_PATH = getApplication().getFilesDir().toString() + "/";
//			AppConstants.Roboto_Condensed        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed.ttf");
//			AppConstants.Roboto_Condensed_Bold        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed_bold.ttf");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
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
	protected void clearData() {
		final ClearDataDA clearDataDA=new ClearDataDA();
		try {
			if(isNetworkConnectionAvailable(this)){
				showLoader(getString(R.string.please_wait));
				new Thread(new Runnable() {
					boolean isCanDoClearData=true;
					boolean isClearDataAllowed=false;
					boolean isDataCleared=false;
					boolean isErrorWhilUploading=true;
					@Override
					public void run() {
						try {
							isClearDataAllowed=new ConnectionHelper(ManageSpaceActivity.this).sendRequest(ManageSpaceActivity.this,BuildXMLRequest.getClearDataPermission(preference.getStringFromPreference(Preference.EMP_NO, "")), ServiceURLs.GetClearDataPermission, preference);
							if(isClearDataAllowed)
							{
//								new UploadSQLite().uploadSQLite(ManageSpaceActivity.this, ServiceURLs.UploadSQLiteFromSettings, new UploadDataListener() {
//									@Override
//									public void updateStatus(int status, String message) {
//										if(status == AppStatus.DEFAULT && !TextUtils.isEmpty(message))
//										{
//											isErrorWhilUploading = true;
//										}
//										else
//											isErrorWhilUploading = false;
//									}
//								});
								isErrorWhilUploading = uploadDatabaseIntoServer(ServiceURLs.UploadSQLiteFromSettings);
									
							}
							if(!isErrorWhilUploading){
								isCanDoClearData = clearDataDA.isCanDoClearData();
								if(isCanDoClearData){
									clearApplicationData();
									preference.clearPreferences();
									
									DisplayMetrics displaymetrics = new DisplayMetrics();
									getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
									
									preference.saveIntInPreference("DEVICE_DISPLAY_WIDTH", displaymetrics.widthPixels);
									preference.saveIntInPreference("DEVICE_DISPLAY_HEIGHT",displaymetrics.heightPixels);
									preference.commitPreference();
									isDataCleared=true;
								}else{
									
								}	
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								hideLoader();
								if(!isClearDataAllowed){
									showCustomDialog(ManageSpaceActivity.this, "Alert!", "You don't have access permissions to use clear data option.", "OK", null, "");
								}else{
									if(isDataCleared){
										Toast.makeText(ManageSpaceActivity.this, "Data got cleared please do re-login to use the app.", Toast.LENGTH_LONG).show();
										Intent intentBrObj = new Intent();
										intentBrObj.putExtra("Data_cleared","Relogin To Use The App");
										intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
										sendBroadcast(intentBrObj);
										finish();
										
									}else if(!isCanDoClearData){
										Toast.makeText(ManageSpaceActivity.this, "Please try after sometime.", Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(ManageSpaceActivity.this, "Error while clearing the data, please check your internet connection and try again.", Toast.LENGTH_LONG).show();
									}
								}
							}
						});
					}
				}).start();
			}else{
				showCustomDialog(ManageSpaceActivity.this, "Alert!", getString(R.string.no_internet), "OK", null, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			finish();
			overridePendingTransition(R.anim.hold, R.anim.push_up_out);
		}
	}

	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("Datacleared")){
			finish();
			System.exit(0);
		}
	}
//	@Override
//	public void transactionStatus(Transactions transactions, TransactionSatus transactionSatus) 
//	{
//		if(transactions == Transactions.NONE && transactionSatus == TransactionSatus.START)
//			showLoader("Uploading data...");
//		else if(transactions == Transactions.NONE && transactionSatus == TransactionSatus.END)
//			hideLoader();
//	}
//
//	@Override
//	public void error(TransactionSatus transactionSatus)
//	{
//		hideLoader();
//	}
//
//	@Override
//	public void currentTransaction(Transactions transactions) 
//	{
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
	}

	@Override
	public void progress(String msg) {
		showLoader(msg);		
	}
	@Override
	public void updateStatus(int status, String message) 
	{
		// TODO Auto-generated method stub
		
	}
}
