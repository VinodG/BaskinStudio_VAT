package com.winit.baskinrobbin.salesman.common;

import android.content.Context;

import com.winit.baskinrobbin.parsers.InsertOrdersImagesParser;
import com.winit.baskinrobbin.parsers.InsertOrdersParser;
import com.winit.baskinrobbin.parsers.InsertPaymetImagesParser;
import com.winit.baskinrobbin.parsers.InsertSalesmanSImagesParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import java.util.Vector;

public class CompleteOnHoldOrder 
{
	private Context mContext;
	private Preference preference;
	private int TYPE = AppStatus.TODAY_DATA;
	
	public CompleteOnHoldOrder(Context context, Preference preference, int TYPE)
	{
		this.mContext 	= context;
		this.preference = preference;
		this.TYPE 		= TYPE;
	}
	
	public boolean uploadOrders(String empNo)
	{
		boolean isUploaded = true;
		final Vector<OrderDO> vecSalesOrders  	= 	new CommonDA().getOnHoldOrderToDeliver(preference.getStringFromPreference(Preference.EMP_NO, ""));
		
		if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (OrderDO order : vecSalesOrders)
			{
				final InsertOrdersParser completeOnHoldOrderParser 	= new InsertOrdersParser(mContext);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.completOnHoldOrdernew(order, CalendarUtils.getCurrentDateTime()), completeOnHoldOrderParser, ServiceURLs.CompleteOnHoldOrder, preference);

			}
		}
        
        isUploaded = new CommonDA().getAllOnHoldOrderUploaded(empNo, TYPE);
        return isUploaded;
	}
	public boolean uploadSalesmanImagesOrders(String empNo,String trxType,String signType)
	{
		boolean isUploaded = true;


		final Vector<OrderDO> vecSalesOrders  	= 	new CommonDA().getSignatureforAllOrder(preference.getStringFromPreference(Preference.EMP_NO, ""),signType);

		if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (OrderDO order : vecSalesOrders)
			{
				final InsertSalesmanSImagesParser completeOnHoldOrderParser 	= new InsertSalesmanSImagesParser(mContext);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.postSignatureXML(order, CalendarUtils.getCurrentDateTime(),trxType,signType), completeOnHoldOrderParser, ServiceURLs.PostSignature, preference);
				if(completeOnHoldOrderParser.getStatus())
					new CommonDA().updateSalesmanOrderNumbers(order);
			}
		}

//        isUploaded = new CommonDA().getAllOnHoldOrderUploaded(empNo, TYPE);
        return isUploaded;
	}
	public boolean uploadImagesOrders(String empNo,String trxType,String signType)
	{
		boolean isUploaded = true;


		final Vector<OrderDO> vecSalesOrders  	= 	new CommonDA().getSignatureforAllOrder(preference.getStringFromPreference(Preference.EMP_NO, ""),signType);

		if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (OrderDO order : vecSalesOrders)
			{
				final InsertOrdersImagesParser completeOnHoldOrderParser 	= new InsertOrdersImagesParser(mContext);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.postSignatureXML(order, CalendarUtils.getCurrentDateTime(),trxType,signType), completeOnHoldOrderParser, ServiceURLs.PostSignature, preference);
				if(completeOnHoldOrderParser.getStatus())
					new CommonDA().updateOrderNumbers(order);
			}
		}

//        isUploaded = new CommonDA().getAllOnHoldOrderUploaded(empNo, TYPE);
        return isUploaded;
	}
	public boolean uploadPaymetImagesOrders(String empNo,String trxType,String signType)
	{
		boolean isUploaded = true;


		final Vector<OrderDO> vecSalesOrders  	= 	new CommonDA().getSignaturePaymentforAllOrder(preference.getStringFromPreference(Preference.EMP_NO, ""),signType);

		if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (OrderDO order : vecSalesOrders)
			{
				final InsertPaymetImagesParser completeOnHoldOrderParser 	= new InsertPaymetImagesParser(mContext);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.postSignatureXML(order, CalendarUtils.getCurrentDateTime(),trxType,signType), completeOnHoldOrderParser, ServiceURLs.PostSignature, preference);
				if(completeOnHoldOrderParser.getStatus())
					new CommonDA().updatePaymentNumbers(order);
			}
		}

//        isUploaded = new CommonDA().getAllOnHoldOrderUploaded(empNo, TYPE);
        return isUploaded;
	}
}
