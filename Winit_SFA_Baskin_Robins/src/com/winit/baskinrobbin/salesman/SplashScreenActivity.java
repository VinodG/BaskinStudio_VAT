package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gcm.GCMConstants;
import com.google.android.gcm.GCMRegistrar;
import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SplashScreenActivity extends BaseActivity implements ConnectionExceptionListener 
{
    /** Called when the activity is first created. Order_Table*/
	
	private Preference preference;
	private boolean isAllow = true;
	private Handler handler;
	private Runnable mRunnable;
	private Thread mThread;
	
	private LinearLayout llSplash;
	private TelephonyManager telephonyManager = null;
	
	private ConnectionHelper coh;
	private String deviceID = "";
	
	private boolean isAdministrator = false;
	
	@Override
	public void initialize() {
		
		llSplash = (LinearLayout)inflater.inflate(R.layout.main, null);
		llBody.addView(llSplash,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		llHeader.setVisibility(View.GONE);
		coh = new ConnectionHelper(SplashScreenActivity.this);
		initializeControlls();
		
	    handler = new Handler();
	    mRunnable = new Runnable() 
	    {
			@Override
			public void run()
			{
				if(mThread != null && mThread.isAlive())
					mThread.interrupt();
				moveToNextActivity();
			}
		};
	    handler.postDelayed(mRunnable, AppStatus.SPLASH_SCREEN_TIME);
	    
	    if(isNetworkConnectionAvailable(SplashScreenActivity.this))
		{
	    	mThread = new Thread(new Runnable() 
		    {
				@Override
				public void run() 
				{
					isAllow = getAppAccessStatus();
					insertInstallation();
					
					AppConstants.isDeviceVerificationNeeded = new CommonDA().isDeviceVerificationNeeded();
					
					//Need to remove this code//
					//copyDatabase();
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							if(mRunnable != null)
								handler.removeCallbacks(mRunnable);
							
//							if(isAdministrator){
//								moveToAdmistratorLogin();
//							}
//							else 
							if(isAllow)
							{
//								moveToNextActivity();
								checkDeviceActivationState();
							}
							else {
								Toast.makeText(SplashScreenActivity.this, "Application is disabled by administrator. Please contact service provider.", Toast.LENGTH_LONG).show();
								finish();
							}
						}
					});
				}
			});
		    mThread.start();
		}
	    else
	    {
	    	new Handler().postDelayed(new Runnable()
	    	{
				@Override
				public void run()
				{
					if(mRunnable != null)
						handler.removeCallbacks(mRunnable);
					moveToNextActivity();
				}
			}, 3000);
	    }
	    
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_NOTIFICATION);
		filter.addAction(AppConstants.ERROR_NOTIFICATION);
		registerReceiver(gcmReceiver, filter);
		
		gcmRegister();
	}
	
	
	private void moveToAdmistratorLogin(){
		Intent intent		=	new Intent(SplashScreenActivity.this,LoginAcivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
		finish();
	}
	
	/*@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		
  	}*/

	public void checkDeviceActivationState()
	{
		new Thread(new Runnable() 
		{
			public void run() 
			{
				isServerException = false;
				final BooleanParser booleanParser= new BooleanParser(SplashScreenActivity.this);
				
				if(NetworkUtility.isNetworkConnectionAvailable(SplashScreenActivity.this)){
					deviceID ="351692093878758";
					coh.sendRequest(SplashScreenActivity.this, BuildXMLRequest.GetUserDeviceStatus(deviceID), booleanParser, ServiceURLs.GetUserDeviceStatus, preference);
				}
				final int isActive =(Integer) booleanParser.getData();
//				final int isActive =1;// NEED TO REMOVE

				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						hideLoader();
						if(isActive!=-1)
						{
							preference.saveIntInPreference(Preference.IS_DEVICE_ACTIVE, isActive);
							preference.commitPreference();
						}
						if(isActive==1 || !NetworkUtility.isNetworkConnectionAvailable(SplashScreenActivity.this)){
							moveToNextActivity();
						}else if(isActive==0){
							showCustomDialog(SplashScreenActivity.this, getString(R.string.warning), "Your device is not registered.", getString(R.string.OK), null, "device_active");
						}else if(isActive==-1){
							String oldEmpNo = preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
							String newEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
							if(preference.getIntFromPreference(Preference.IS_DEVICE_ACTIVE, -1)==-1){
								if(!TextUtils.isEmpty(oldEmpNo) && oldEmpNo.equalsIgnoreCase(newEmpNo) && !isNetworkConnectionAvailable(SplashScreenActivity.this)){
									moveToNextActivity();
								}else if(isServerException){
									showCustomDialog(SplashScreenActivity.this, getString(R.string.warning), "Server Error \n Please try after some time.", getString(R.string.OK), null, "device_active");
								}else{
									showCustomDialog(SplashScreenActivity.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "device_active");
								}
							}
							else if(preference.getIntFromPreference(Preference.IS_DEVICE_ACTIVE, -1)==1)
								moveToNextActivity();
						}
					}
				});
				
			}
		}).start();
	}
	
	@Override
	public void onButtonYesClick(String from) {
		
		if(from.equalsIgnoreCase("device_active")){
//			Intent intent = new Intent(SplashScreenActivity.this,DeviceActivation.class);
//			startActivity(intent);
			finish();
		}
	}
	
	public void gcmRegister() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				/**
				 * If the registration registration id is null, then we can
				 * register as follows
				 */
				if (!GCMRegistrar.isRegistered(SplashScreenActivity.this)) {
					if (NetworkUtility.isNetworkConnectionAvailable(SplashScreenActivity.this)) {
						try {
							/**
							 * To receive GCM push notifications, device must be
							 * at least API Level 8
							 */
							GCMRegistrar.checkDevice(SplashScreenActivity.this);

							/**
							 * Check manifest whether it is having
							 * "permission.C2D_MESSAGE",
							 * "com.google.android.c2dm.permission.SEND",
							 * "com.google.android.c2dm.intent.REGISTRATION",
							 * "com.google.android.c2dm.intent.RECEIVE"
							 * permissions or not. By using manifest tag
							 * "<service android:name=".GCMIntentService" />" we
							 * can start GCM service,
							 * 
							 */
							GCMRegistrar.checkManifest(SplashScreenActivity.this);
							AppConstants.GCMRegistrationAttempts++;
							GCMRegistrar.register(SplashScreenActivity.this,
									AppConstants.SENDER_ID);
						} catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				} else {
				}
			}
		}).start();
	}
	
	BroadcastReceiver gcmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(
					AppConstants.ACTION_NOTIFICATION)) {
				new PostRegId(SplashScreenActivity.this).execute(intent.getExtras()
						.getString("result"));
				// moveInside(true);
			} else if (intent.getAction().equalsIgnoreCase(
					AppConstants.ERROR_NOTIFICATION)) {
				onGCMError(intent.getExtras().getString("result"));
			}
		}
	};
	private void onGCMError(String errorMsg) {
		String displayMsg = "";
		if (errorMsg.equalsIgnoreCase(GCMConstants.ERROR_ACCOUNT_MISSING)) {
			// Google account missing.
			displayMsg = "Please make sure you are signed into your Google account";
		} else if (errorMsg
				.equalsIgnoreCase(GCMConstants.ERROR_SERVICE_NOT_AVAILABLE)) {
			// Google server error.
			displayMsg = "Error enabling notifications. Please try later.";
		} else if (errorMsg
				.equalsIgnoreCase(GCMConstants.ERROR_PHONE_REGISTRATION_ERROR)
				|| errorMsg
						.equalsIgnoreCase(GCMConstants.ERROR_INVALID_PARAMETERS)) {
			// This phone doesn't currently support GCM.
			displayMsg = "Device doesn't support  notifications.";
		} else if (errorMsg
				.equalsIgnoreCase(GCMConstants.ERROR_AUTHENTICATION_FAILED)) {
			// Bad password..
			displayMsg = "Error enabling notifications. Google authentication failed.";
		} else if (errorMsg.equalsIgnoreCase(GCMConstants.ERROR_INVALID_SENDER)) {
			// Invalid sender id.
			displayMsg = "Error enabling notifications. Invalid sender id";
		} else {
			// Gcm registration fails.
			displayMsg = "Error enabling notifications.";
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		/** To Clear internal resources. */
		GCMRegistrar.onDestroy(SplashScreenActivity.this);
		//GCMRegistrar.onDestroy(this);
		unregisterReceiver(gcmReceiver);
	}
	
	public class PostRegId extends AsyncTask<String, String, String> {
		private String strResponse = "";

		public PostRegId(Context context) 
		{
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				// Write the code for posting the registrationId to the server

			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			return strResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
	
	//method to copy Masafi logos to sdcard from assests 
	private boolean copyLogotoSdCard()
	{
		if(!(new File(AppConstants.baskinLogoPath).exists()))
			new File(AppConstants.baskinLogoPath).mkdirs();
		//Creating the file if not exist
		File file 		= new File(AppConstants.baskinLogoPath);
		
//		if(!preference.getbooleanFromPreference(Preference.IS_INSTALLED, false))
//		{
			if(file.exists())
			{
				if (file.isDirectory()) 
				{
			        String[] children = file.list();
			        for (int i = 0; i < children.length ; i++) 
			        {
			            new File(file, children[i]).delete();
			        }
			    }
//		}
		preference.saveBooleanInPreference(Preference.IS_INSTALLED, true);
		preference.commitPreference();
	}

		if(!file.exists())
		{
			file.mkdir();
		}
		
		File[] fileslist = file.listFiles();
		if(fileslist != null && fileslist.length > 0)
		{
			for(File tempFile : fileslist)
			{
				if(tempFile.getName().toString().contains(".bmp"))
				return false;
			}
		}
		//creating instance to AssetManager
	    AssetManager assetManager = getAssets();
	    //to getting the all files from sdcard
	    String[] files = null;
	    try 
	    {
	    	//getting the all files from sdcard
	        files = assetManager.list("");
	    }
	    catch (IOException e) 
	    {
	    }
	    //loop up to file count
	    for(String filename : files)
	    {
	    	//creating instance to InputStream and OutputStream
	        InputStream inputStream   = null;
	        OutputStream outputStream = null;
	        try 
	        {
	        	//coping only the files having the extension .bmp
	        	if(filename.contains(".bmp"))
	        	{
		          inputStream 	= assetManager.open(filename);
		          outputStream 	= new FileOutputStream(AppConstants.baskinLogoPath+ "/"+filename);
		          copyFile(inputStream, outputStream);
		          inputStream.close();
		          inputStream = null;
		          outputStream.flush();
		          outputStream.close();
		          outputStream = null;
	        	}
	        } 
	        catch(Exception e) 
	        {
	        	e.printStackTrace();
	        }  
	    }
		return true;
	}
	/**
	 * method to writing files 
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException 
	{
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1)
	    {
	      out.write(buffer, 0, read);
	    }
	}
	
	private void copyDatabase()
	{
		try
		{
			new DatabaseHelper(SplashScreenActivity.this).Load();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean getAppAccessStatus()
	{
		boolean isAllowed = true;
		/*try
		{
			isAllowed = new ConnectionHelper(null).sendDEVRequest(BuildXMLRequest.getAppAccessStatus(), ServiceURLs.GetAppAccessStatus);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
		
		return isAllowed;
	}
	
	/**
	 * Method to check the Internet availability
	 * 
	 * @param context
	 * @return boolean
	 */
	public boolean isNetworkConnectionAvailable(Context context) 
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		@SuppressLint("WrongConstant") ConnectivityManager connectivityManager = (ConnectivityManager) context	.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
		{
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}

		return isNetworkConnectionAvailable;
	}
	
	public void insertInstallation()
	{
		try
		{
			String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			String dateTime = CalendarUtils.getCurrentDateTime();
			String version  = getString(R.string.version);

		    new ConnectionHelper(null).sendDEVRequest(BuildXMLRequest.insertApplicationInsatallation(deviceId, dateTime, version), ServiceURLs.InsertApplicationInsatallation);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void moveToNextActivity()
	{
		AppConstants.DATABASE_PATH = getApplication().getFilesDir().toString() + "/";
		AppConstants.Helvetica_LT_57_Condensed   = Typeface.createFromAsset(getApplicationContext().getAssets(), "Helvetica_LT_57_Condensed.ttf");
		AppConstants.Helvetica_LT_Condensed_Bold = Typeface.createFromAsset(getApplicationContext().getAssets(), "Helvetica_LT_Condensed_Bold.ttf");
		copyLogotoSdCard();
		//deleting all the data from the Customer order Table
		finish();
		Intent intent		=	new Intent(SplashScreenActivity.this,LoginAcivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
	}
	
	private boolean isServerException = false;
	@Override
	public void onConnectionException(Object msg) {
		super.onConnectionException(msg);
		isServerException = true;
		
	}
	
	  public void initializeControlls(){
			preference	= new Preference(getApplicationContext());
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			
			preference.saveIntInPreference("DEVICE_DISPLAY_WIDTH", displaymetrics.widthPixels);
			preference.saveIntInPreference("DEVICE_DISPLAY_HEIGHT",displaymetrics.heightPixels);
			preference.commitPreference();
			
			/*preference.saveIntInPreference(Preference.IS_ADMINISTRATOR, 0);
			preference.commitPreference();*/
			
			telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			deviceID =telephonyManager.getDeviceId();
			isAdministrator = preference.getbooleanFromPreference(Preference.IS_ADMINISTRATOR, true);
			
			// setting divice width and height
			Display display 				= 	getWindowManager().getDefaultDisplay();
			AppConstants.DIVICE_WIDTH 		= 	display.getWidth();
			AppConstants.DIVICE_HEIGHT 		= 	display.getHeight();
			AppConstants.CategoryIconsPath 	= 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Baskin/CategoryIcons/";
		    AppConstants.productCatalogPath = 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Baskin/";
		    AppConstants.baskinLogoPath 	= 	AppConstants.productCatalogPath+"BaskinLogo";
		  readAssetFiles();
		
	  }

	private void readAssetFiles()  {
		InputStream input = null;
		ByteArrayOutputStream output = null;
		AssetManager assetManager = getAssets();
		String[] files = { "printer_profiles.JSON", "honeywell_logo.bmp" };
		int fileIndex = 0;   int initialBufferSize;
		try   {
			for (String filename : files)    {
				input = assetManager.open(filename);
				initialBufferSize = (fileIndex == 0) ? 8000 : 2500;
				output = new ByteArrayOutputStream(initialBufferSize);
				byte[] buf = new byte[1024];
				int len;
				while ((len = input.read(buf)) > 0)     {
					output.write(buf, 0, len);
				}     input.close();
				input = null;     output.flush();
				output.close();
				switch (fileIndex)     {
					case 0:
						AppConstants.jsonCmdAttribStr = output.toString();
						break;
					case 1:
						AppConstants.base64LogoPng = Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);
						break;
				}    fileIndex++;     output = null;    }   }
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{   if (input != null)
			{
				input.close();
				input = null;
			}
				if (output != null)
				{
					output.close();
					output = null;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}