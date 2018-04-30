package com.winit.baskinrobbin.salesman.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class VPNConnectionService extends Service implements ConnectionExceptionListener{
	
	private static final int CALLING_TIME=10;
	private Timer timer;
	private TimerTask timerTask;
	private ConnectionHelper connectionHelper;
	public static boolean isTestDisplay = false;
	private static final String VPN_CONNECTION_SERVICE = "VPN_CONNECTION_SERVICE";
	@Override
	public void onCreate() {
		super.onCreate();
		connectionHelper = new ConnectionHelper(VPNConnectionService.this);
		if (timer == null) 
			startTimer();
		else 
		{
			stopTimer();
			startTimer();
		}
	}
	
	public VPNConnectionService() {
	}
	
	private void stopTimer() {
		if (timer != null) {
			AppConstants.IS_BR_NETWORK_REACHABLE = false;
			timer.cancel();
			timer.purge();
			timer = null;
			timerTask.cancel();
			timerTask = null;
		}
	}
	private void startTimer()
	{
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				
				LogUtils.errorLog("VPNConnectionService","VPNConnectionService startTimer");
				if(isConnectionAvailable())
				{
					
					synchronized (VPN_CONNECTION_SERVICE) {
						new Thread(new Runnable() 
						{
							
							@Override
							public void run() 
							{
								 AppConstants.IS_BR_NETWORK_REACHABLE = connectionHelper.sendRequest(VPNConnectionService.this, BuildXMLRequest.helloRequest(), ServiceURLs.Hello, new Preference(VPNConnectionService.this));
								 writeIntoLog(CalendarUtils.getCurrentDateAsString()+"\t PE Reachable-"+(AppConstants.IS_BR_NETWORK_REACHABLE?"Yes":"No")+"\t VPN Connection-"+("No")+"\n");
								 Handler h = new Handler(MyApplication.mContext.getMainLooper());

								    h.post(new Runnable() {
								        @Override
								        public void run() {
								        
								          if(isTestDisplay)
								        	Toast.makeText(VPNConnectionService.this, "IS_KR_NETWORK_REACHABLE = "+AppConstants.IS_BR_NETWORK_REACHABLE,Toast.LENGTH_SHORT).show();
								        }
								    });
								    
								LogUtils.errorLog("VPNConnectionService IS_KR_NETWORK_REACHABLE", ""+AppConstants.IS_BR_NETWORK_REACHABLE );

							}
						}).start();
						
					}
				}
				else
					AppConstants.IS_BR_NETWORK_REACHABLE = false;
				
				LogUtils.errorLog("VPNConnectionService","VPNConnectionService endTimer");
			}
		};
		timer.schedule(timerTask, 0, 1 * CALLING_TIME * 1000);
	}
	
	public static void writeIntoLog(String str)
	{
		try
		{
			File connectionFolder = new File(Environment.getExternalStorageDirectory().toString()+"/SFAConnectionLogs");
			if(!connectionFolder.exists())
				connectionFolder.mkdir();
//			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/ConnectionLog_"Cal.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/SFAConnectionLogs/ConnectionLog_"+CalendarUtils.getCurrentDate()+".txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			 
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	 }
	
	public static void deleteLogFile(String path)
	{
		try
		{
			File file = new File(path);
			if(file.exists())
			{
				long sizeInMB = file.length()/1048576;
				if(sizeInMB >= 5)
					file.delete();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private boolean isConnectionAvailable()
	{
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if (activeNetworkInfo != null)
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		return isNetworkConnectionAvailable;
	}
	
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		stopTimer();
	}
	
	@Override
	public void onConnectionException(Object msg) {
		AppConstants.IS_BR_NETWORK_REACHABLE  = false;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
