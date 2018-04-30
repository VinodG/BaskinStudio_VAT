package com.winit.baskinrobbin.salesman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.winit.baskinrobbin.salesman.common.UploadData;

public class MyScheduleReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent) 
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