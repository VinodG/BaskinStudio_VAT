package com.winit.baskinrobbin.salesman.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Description Class : Checking Network Connections
 * @author Neeraj
 *
 */
public class NetworkUtility 
{
	/**
	 * Method to check Network Connections 
	 * @param context
	 * @return boolean value
	 */
	public static boolean isNetworkConnectionAvailable(Context context)
	{
		boolean isNetworkConnectionAvailable = false;
		
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null) 
		{
		    isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}
		return isNetworkConnectionAvailable;
	}
	
	public static boolean isWifiConnected(Context context)
	{
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connManager = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			isNetworkConnectionAvailable = true;
		}
		return isNetworkConnectionAvailable;
	}
	
	public static void isLocalWifiConnected(Context context)
	{
		boolean isLocalWIFI = true;
		if(isWifiConnected(context))
		{
			WifiManager wifiManager =  (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
			WifiInfo info 			=   wifiManager.getConnectionInfo ();
			String LocalWIFI        =   info.getSSID();
		}
	}
}
