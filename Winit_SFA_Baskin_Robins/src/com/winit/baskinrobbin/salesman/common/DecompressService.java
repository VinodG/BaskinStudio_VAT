package com.winit.baskinrobbin.salesman.common;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;

public class DecompressService extends IntentService
{
	public DecompressService() 
	{
		super("DecompressService");
	}
	
	public DecompressService(String name) 
	{
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		try 
		{
			String productCatalogPath 	= 	intent.getExtras().getString("productCatalogPath");
			Decompress decompress 		= 	new Decompress(getAssets().open("ProductCatalogImages.zip"), productCatalogPath);
			decompress.unzip();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
