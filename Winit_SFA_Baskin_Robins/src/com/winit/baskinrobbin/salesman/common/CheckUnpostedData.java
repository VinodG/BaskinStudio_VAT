package com.winit.baskinrobbin.salesman.common;

import android.content.Context;
import android.os.AsyncTask;

import com.winit.baskinrobbin.salesman.dataaccesslayer.UnpostedDataDA;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class CheckUnpostedData extends AsyncTask<String, String, String>
{
	private Preference preference;
	private Context context;
	private UnpostedDataDA unpostedDataDA;
	private String path = "";
	
	public CheckUnpostedData(Context context, String path)
	{
		this.context	= 	context;
		this.path		=	path;
		preference 		= 	new Preference(context);
		unpostedDataDA  = 	new UnpostedDataDA();
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		LogUtils.errorLog("AlarmService", "called1");
		try 
		{
			unpostedDataDA.getAllPaymentsUnload(context, path);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	
	@Override
	protected void onPostExecute(String result) 
	{
		LogUtils.infoLog("Service running ", "strResponse = "+result);
		super.onPostExecute(result);
	}
}
