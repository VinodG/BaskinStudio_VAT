package com.winit.baskinrobbin.salesman.common;

import java.util.ArrayList;

import android.content.Context;

import com.winit.baskinrobbin.parsers.InsertLoadParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetMovementToUpload 
{
	private Context mContext;
	private Preference preference;
	private InventoryDA inventoryDA;
	
	public GetMovementToUpload(Context context, Preference preference)
	{
		this.mContext 	= context;
		this.preference = preference;
		inventoryDA 	= new InventoryDA();
	}
	
	public boolean getMovementToUpload(String empNo)
	{
		ArrayList<LoadRequestDO> vecLoad = new InventoryDA().getAllLoadRequestToPost();
		
		if(vecLoad != null && vecLoad.size() > 0)
		{
			for(LoadRequestDO loadRequestDO : vecLoad)
			{
				ArrayList<LoadRequestDO> temp = new ArrayList<LoadRequestDO>();
				temp.add(loadRequestDO);
				final InsertLoadParser insertLoadParser = new InsertLoadParser(mContext);
				new ConnectionHelper(null).sendRequest(mContext,BuildXMLRequest.uploadLoadRequests(temp), insertLoadParser, ServiceURLs.PostStockMovements, preference);
			}
		}
        
        boolean isPaymentsUploaded = inventoryDA.isAllLoadRequestToPost();
        return isPaymentsUploaded;
	}
	public boolean getMovementToUploadUnload(String empNo)
	{
		ArrayList<LoadRequestDO> vecLoad = new InventoryDA().getAllUnLoadRequestToPost();
		
		if(vecLoad != null && vecLoad.size() > 0)
		{
			for(LoadRequestDO loadRequestDO : vecLoad)
			{
				ArrayList<LoadRequestDO> temp = new ArrayList<LoadRequestDO>();
				temp.add(loadRequestDO);
				final InsertLoadParser insertLoadParser = new InsertLoadParser(mContext);
				new ConnectionHelper(null).sendRequest(mContext,BuildXMLRequest.uploadLoadRequests(temp), insertLoadParser, ServiceURLs.PostStockMovements, preference);
			}
		}
        
        boolean isPaymentsUploaded = inventoryDA.isAllLoadRequestToPost();
        return isPaymentsUploaded;
	}
}
