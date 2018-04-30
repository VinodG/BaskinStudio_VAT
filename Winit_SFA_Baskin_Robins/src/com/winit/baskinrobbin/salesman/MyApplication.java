package com.winit.baskinrobbin.salesman;

import android.content.Context;


public class MyApplication extends android.app.Application
{
	 public static String MyLock = "Lock";
	 public static String SERVICE_LOCK = "SERVICE_LOCK";
	 public static Context mContext;
	@Override
    public void onCreate() 
	{
        super.onCreate();
        if(mContext ==null)
			mContext = this;
    }
}
