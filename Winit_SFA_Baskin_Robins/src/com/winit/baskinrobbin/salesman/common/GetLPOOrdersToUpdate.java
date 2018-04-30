package com.winit.baskinrobbin.salesman.common;

import java.util.Vector;

import android.content.Context;

import com.winit.baskinrobbin.parsers.UpdateLpoOrdersParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetLPOOrdersToUpdate 
{
	private Context mContext;
	private Preference preference;
	private int TYPE = AppStatus.TODAY_DATA;
	
	public GetLPOOrdersToUpdate(Context context, Preference preference, int TYPE)
	{
		this.mContext 	= context;
		this.preference = preference;
		this.TYPE 		= TYPE;
	}
	
	public boolean uploadOrders(String empNo)
	{
		boolean isUploaded = true;
		final Vector<OrderDO> vecSalesOrders  		= 	new CommonDA().getLPOOrderToUpdate(empNo, TYPE);
		final UpdateLpoOrdersParser insertOrdersParser = 	new UpdateLpoOrdersParser(mContext);
        if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
        	ConnectionHelper connectionHelper = new ConnectionHelper(null);
			connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.updateLPOOrder(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.UpdateMTDeliveryStatus, preference);
        }
        
        isUploaded = new CommonDA().getAllLpoUpdated(empNo, TYPE);
        return isUploaded;
	}
}
