/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.winit.baskinrobbin.salesman;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService 
{
	private static  int notificationId = 1;
	public static int notificationCount=0;
//	private static final String TAG = "GCMIntentService";

	public GCMIntentService() 
	{
		super(AppConstants.SENDER_ID);//956495984703
	}
	@Override
	protected void onError(Context context, String arg1) 
	{
		broadcastNotification(arg1, AppConstants.ERROR_NOTIFICATION);
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		String title = intent.getExtras().getString("type");
		String subject = intent.getExtras().getString("subject");
		int notificationType = intent.getExtras().getInt("notificationType");
		generateNotification(context, title, subject,notificationType);
	}
	
	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		if(registrationId.length() > 0)
		{
			LogUtils.errorLog("registrationId",""+registrationId);
			register(registrationId);
			Preference pre = new Preference(GCMIntentService.this);
			pre.saveStringInPreference(Preference.gcmId, registrationId);
			pre.commitPreference();
		}
		else
		{
			if(AppConstants.GCMRegistrationAttempts > AppConstants.MaximumGCMRegistrationAttempts)
			{
				broadcastNotification(registrationId, AppConstants.ERROR_NOTIFICATION);
			}
			else
			{
				AppConstants.GCMRegistrationAttempts++;
				GCMRegistrar.register(this, AppConstants.SENDER_ID);
			}
		}
	}

	public void register(final String gcmId)
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				String userID = new Preference(GCMIntentService.this).getStringFromPreference(Preference.EMP_NO, "");
				if(!userID.equalsIgnoreCase(""))
				{
					new ConnectionHelper(null).sendRequest(GCMIntentService.this,BuildXMLRequest.registerGCMOnServer(userID, gcmId), ServiceURLs.updateDeviceId, null);
				}
			}
		}).start();
	}
	
	@Override
	protected void onUnregistered(Context arg0, String arg1)
	{
	}
		
	 @SuppressWarnings("deprecation")
	private  void generateNotification(Context context, String title, String message,int notificationType)
	{
		 if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message))
		 {
			 Intent i = new Intent();
			 i.setClass(this, NotificationActivity.class);
			 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 i.putExtra("message", message);
			 i.putExtra("title", title);
			 i.putExtra("notificationType", notificationType);
			 startActivity(i);
		 }
	 }
	 
	private void broadcastNotification(String msg,String action)
	{
		 Intent intent2 = new Intent();
		 intent2.setAction(action);
		 intent2.putExtra("result", ""+msg);
		 getApplication().sendBroadcast(intent2);
	 }
}
