package com.winit.baskinrobbin.salesman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.winit.baskinrobbin.salesman.common.UploadData;

public class AlaramReceiver  extends BroadcastReceiver 
{
	public static String ACTION_ALARM = "com.winit.baskinrobbin";
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Bundle bundle = intent.getExtras();
		String action = bundle.getString(ACTION_ALARM);
		if (action.equals(ACTION_ALARM)) 
		{
			try
			{
				if(isNetworkConnectionAvailable(context) && !UploadData.isRunning)
				{
					Intent intent2 = new Intent(context, UploadData.class);
					intent2.putExtra("TYPE", -1);
					context.startService(intent2);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
		}	
	 }
	
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
}
