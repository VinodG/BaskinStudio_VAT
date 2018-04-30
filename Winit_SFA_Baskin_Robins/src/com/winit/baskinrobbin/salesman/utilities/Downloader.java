/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.winit.baskinrobbin.salesman.utilities;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.winit.baskinrobbin.salesman.common.AppConstants;

public class Downloader extends IntentService 
{
	public static final String EXTRA_MESSENGER="com.commonsware.android.downloader.EXTRA_MESSENGER";
	private HttpClient client=null;
	public Downloader() 
	{
		super("Downloader");
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		client=new DefaultHttpClient();
		
		
	}
  
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		client.getConnectionManager().shutdown();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}
	
	
	@Override
	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
	}
	
	@Override 
	public void onHandleIntent(Intent i) 
	{
		Bundle extras=i.getExtras();
		downloadFileAndSaveAtPath(i.getData().toString());
		
		Intent intent = new Intent();
		intent.setAction(AppConstants.ACTION_SQLITE_FILE_DOWNLOAD);
		sendBroadcast(intent);
	}
	
	public String downloadFileAndSaveAtPath(String sUrl)
	{
		System.gc();
		InputStream strm = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		String LocalFile = null;
		try
		{			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(sUrl);	
			response = httpclient.execute(httpget);		
			if (response == null) 
				return null;
			entity = response.getEntity();
			strm = entity.getContent();			
			Load(strm);			
			strm.close();	
			return LocalFile;
			
		}
		catch(UnknownHostException e)	
		{			
			e.printStackTrace();
		}
		catch(Exception e)	
		{		
			e.printStackTrace();
		}
		finally
		{
			
		}
		return LocalFile;
	}
	public boolean Load(InputStream isDataBase)
    {
        boolean installed = true;
//        try
//        {
//            InputStream is = openFileInput(AppConstants.DATABASE_NAME);
//            is.close();
//        }
//        catch (FileNotFoundException e) 
//        {
//        	e.printStackTrace();
//            installed = false;
//        }
//        catch (IOException e) 
//        {
//            installed = false;
//        }
//        
//        if(!installed)
//        {
            try
            {
                InputStream is = isDataBase;
                OutputStream os = null;
                
                os = openFileOutput(AppConstants.DATABASE_NAME, Context.MODE_WORLD_READABLE);
                
                int len;
                byte[] buffer = new byte[4096];
                while ((len = is.read(buffer)) >= 0)
                {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                installed = true;
            }
            catch (Exception e) 
            {
            	e.printStackTrace();
            }
//        }
//        else
//        {
//        }
        return installed;
    }
}
