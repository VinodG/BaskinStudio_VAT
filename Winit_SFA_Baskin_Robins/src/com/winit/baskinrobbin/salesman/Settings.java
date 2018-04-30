package com.winit.baskinrobbin.salesman;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.GenerateOfflineDataParserNew;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.FilesStorage;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.SyncData;
import com.winit.baskinrobbin.salesman.common.SyncData.SyncProcessListner;
import com.winit.baskinrobbin.salesman.common.UploadData;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.FileUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.utilities.ZipUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class Settings extends BaseActivity implements SyncProcessListner
{
	//declaration of variables
	private LinearLayout llSettings;
	private TextView tvChangePassword, tvSettings, tvSplashScreenTheme, tvUploadData,tvUpLogs,tvVanstockLogs, tvUpdateJourneyPlan, 
					 tvServiceSyncTimeLog,tvSettingVersionNo, tvURL, tvSequence, tvUploadUnpostedData,
					 tvUploadSQLite, tvSyncData;
	private boolean isuploaded;
	
	@Override
	public void initialize()
	{
		//inflate the settings layout
		llSettings = (LinearLayout)inflater.inflate(R.layout.settings, null);
		llBody.addView(llSettings,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
		
		setTypeFace(llSettings);
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		
		tvChangePassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Settings.this, ChangePasswordActivity.class);
				startActivity(intent);
			}
		});
		
		tvSplashScreenTheme.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Settings.this, SplashScreenTheme.class);
				startActivity(intent);
			}
		});
		
		tvUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				uploadDatabaseIntoSDCARD();
//				String path = Environment.getExternalStorageDirectory().toString();
//				uploadDatabaseIntoSDCARD(path, AppConstants.DATABASE_NAME, false);
			}
		});
		
		tvUpLogs.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				uploadDatabaseIntoSDCARD();
				copyDeliveryToSDCard();
			//	copyOrdersToSDCard();
			}
		});
		tvVanstockLogs.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				uploadDatabaseIntoSDCARD();
				copyVanStockToSDCard();
				//	copyOrdersToSDCard();
			}
		});
		
		tvServiceSyncTimeLog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Settings.this, SyncLogsActivityNew.class);
				startActivity(intent);
			}
		});
		
		tvUploadSQLite.setEnabled(true);
		tvUploadSQLite.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, "Alert !", getString(R.string.no_internet), "OK", null, "");
				else
				{
					setDownloadProgressBar();
					new Thread(new Runnable()
					{
						boolean isErrorWhilUploading;
						@Override
						public void run() 
						{
							isErrorWhilUploading = uploadDatabaseIntoServer(ServiceURLs.UploadSQLiteFromSettings);
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									if(isErrorWhilUploading)
									{
										showCustomDialog(Settings.this, "Alert !", "Error occurred while uploading SQLite file, Please try again.", "OK", null, "");
										dismissProgressDIalog();
									}
									else
									{
										dismissProgressDIalog();
										showCustomDialog(Settings.this, "Alert !", "SQLite file has been uploaded successfully.", "OK", null, "");
									}
								}
							});
//							new UploadSQLite().uploadSQLite(Settings.this, ServiceURLs.UploadSQLiteFromSettings, new UploadDataListener() 
//							{
//								@Override
//								public void updateStatus(final int status, final String message)
//								{
//									hideLoader();
//									
//									if(TextUtils.isEmpty(message))
//									{
//										showCustomDialog(Settings.this, "Alert !", "Error occurred while uploading SQLite file, Please try again.", "OK", null, "");
//										dismissProgressDIalog();
//									}
//									else if(status == AppStatus.DEFAULT && !TextUtils.isEmpty(message))
//									{
//										dismissProgressDIalog();
//										showCustomDialog(Settings.this, "Alert !", "SQLite file has been uploaded successfully.", "OK", null, "");
//									}
//									else
//										updateProgress(status);
//								}
//							});
						}
					}).start();
				}
			}
		});
		
		tvSequence.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, "Alert !", getString(R.string.no_internet), "OK", null, "");
				else
				{
					showLoader("Syncing..");
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							String empNO = preference.getStringFromPreference(Preference.EMP_NO, "");
							GenerateOfflineDataParserNew getOfflineDataParser = new GenerateOfflineDataParserNew(Settings.this, empNO);
							new ConnectionHelper(null).sendRequest_Bulk(Settings.this,BuildXMLRequest.getSequenceNoBySalesmanForHH(empNO), getOfflineDataParser, ServiceURLs.GET_SEQUENCE_NO, preference);
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									showCustomDialog(Settings.this, "Alert !", "Offline sequence numbers are synced successfully.", "OK", null, "");
								}
							});
						}
					}).start();
				}
			}
		});
		
		
		tvUpdateJourneyPlan.setVisibility(View.GONE);
		
		//////////////////////////////////////////////
		final String [] URL 		 = new String[2];
		final String [] name	 	 = new String[2];
		final String [] DATABASE_URL = new String[2];
		
		name[0] 			= 	"LIVE";
		URL [0] 			= 	ServiceURLs.MAIN_GLOBAL_URL;
		DATABASE_URL [0] 	= 	ServiceURLs.IMAGE_GLOBAL_URL;
		
		name[1] 			= 	"LOCAL";
		URL [1]	 			= 	ServiceURLs.MAIN_LOCAL_URL;
		DATABASE_URL [1] 	= 	ServiceURLs.IMAGE_LOCAL_URL;
		
		////////////////////////////////////////////////
		setUpURL(name, URL, DATABASE_URL);
		///////////////////////////////////////////////
		tvURL.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
				builder.setSingleChoiceItems(name, StringUtils.getInt(tvURL.getTag().toString()), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if(NetworkUtility.isWifiConnected(Settings.this) || name[which].equalsIgnoreCase("LIVE"))
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
							showCustomDialog(Settings.this, "Warning !", "You have connected with mobile data, please select Live.", "OK", null, "");
					}
				});
				builder.show();
			}
		});
		
		tvUploadUnpostedData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, "Alert !", getString(R.string.no_internet), "OK", null, "");
				else if(UploadData.isRunning)
					showToast("Already running...");
				else
				{
					showLoader("Preparing to upload...");
					uploadData(AppStatus.ALL, AppStatus.ALL_DATA);
					UploadData.setListener(new UploadDataListener()
					{
						@Override
						public void updateStatus(int status, String message)
						{
							showLoader(message);
							if(status == UploadData.END)
							{
								hideLoader();
								showCustomDialog(Settings.this, "Alert !", "Data uploaded successfully.", "OK", null, "");
								UploadData.setListener(null);
							}
						}
					});
				}
			}
		});
		
		tvSyncData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
//				startSync(empNo);
				showLoader("Syncing...");
				syncData(Settings.this);
			}
		});
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
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_GOTO_SETTINGS_FINISH);
		registerReceiver(FinishReceiver, filter);
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
		SyncData.setListener(null);
	}
	
	private void intialiseControls() 
	{
		tvChangePassword		=	(TextView) llSettings.findViewById(R.id.tvChangePassword);
		tvSettings				=	(TextView) llSettings.findViewById(R.id.tvSettings);
		tvSplashScreenTheme		=	(TextView) llSettings.findViewById(R.id.tvSplashScreenTheme);
		tvUploadData			=	(TextView) llSettings.findViewById(R.id.tvUploadData);
		tvUpdateJourneyPlan		=	(TextView) llSettings.findViewById(R.id.tvUpdateJourneyPlan);
		tvServiceSyncTimeLog	=	(TextView) llSettings.findViewById(R.id.tvServiceSyncTimeLog);
		tvSettingVersionNo		=	(TextView) llSettings.findViewById(R.id.tvSettingVersionNo);
		tvURL					=	(TextView) llSettings.findViewById(R.id.tvURL);
		tvSequence				=	(TextView) llSettings.findViewById(R.id.tvSequence);
		tvUploadUnpostedData	=	(TextView) llSettings.findViewById(R.id.tvUploadUnpostedData);
		tvUploadSQLite			=	(TextView) llSettings.findViewById(R.id.tvUploadSQLite);
		tvUpLogs			=	(TextView) llSettings.findViewById(R.id.tvUpLogs);
		tvVanstockLogs			=	(TextView) llSettings.findViewById(R.id.tvVanstockLogs);
		tvSyncData				=	(TextView) llSettings.findViewById(R.id.tvSyncData);
		
		tvUpdateJourneyPlan.setVisibility(View.GONE);
		tvURL.setVisibility(View.VISIBLE);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
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
	
//	private void startSync(final String empNo)
//	{
//		if(isNetworkConnectionAvailable(Settings.this))
//		{
//			Intent intent = new Intent(Settings.this, SyncData.class);
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
//						showCustomDialog(Settings.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
//					}
//					else if(status == SyncData.ERROR)
//					{
//						SyncData.setListener(null);
//						hideLoader();
//						showCustomDialog(Settings.this, getString(R.string.warning), "Error occurred while syncing data, Please try again.", getString(R.string.OK), null, "");
//					}
//					else if(status != SyncData.END)
//						showLoader(message);
//					else
//					{
//						hideLoader();
//						saveDynamicAddress();
//						showCustomDialog(Settings.this, getString(R.string.successful), "Data has been synced.", getString(R.string.OK), null, "");
//					}
//				}
//			});
//		}
//		else
//			showCustomDialog(Settings.this, "Alert !", getString(R.string.no_internet), "OK", null, "");
//	}
	
	private TextView tvProgress;
	private ProgressBar progressBar;
	private Dialog dialogDownload;
	
	private void setDownloadProgressBar()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				View v = inflater.inflate(R.layout.progressdialog, null);
				setTypeFace((LinearLayout)v);
				progressBar  		= (ProgressBar) v.findViewById(R.id.prgbar);
				tvProgress 			= (TextView) v.findViewById(R.id.tvprogress);
				TextView tvTitle	= (TextView) v.findViewById(R.id.tvTitle);
				
				tvTitle.setText("Uploading master data file...");
				
				if(dialogDownload == null)
				{
					dialogDownload = new Dialog(Settings.this);
					dialogDownload.setTitle("");
					dialogDownload.setCancelable(false);
				}
				
				dialogDownload.setContentView(v,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				dialogDownload.getWindow().setGravity(Gravity.CENTER);
				dialogDownload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				
				progressBar.setMax(AppStatus.TOTAL_PROGRESS);
				progressBar.setProgress(0);
				tvProgress.setText("0 %");
				
				if(!dialogDownload.isShowing())
					dialogDownload.show();
				
				hideLoader();
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
	
	private void updateProgress(final int count)
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
	
}
