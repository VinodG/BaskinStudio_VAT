package com.winit.baskinrobbin.salesman.common;

import java.util.Vector;

import android.content.Context;

import com.winit.baskinrobbin.parsers.InsertPaymentParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDONew;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetPaymentsToUpload 
{
	private Context mContext;
	private Preference preference;
	private int TYPE = AppStatus.TODAY_DATA;
	private CommonDA commonDA;
	
	public GetPaymentsToUpload(Context context, Preference preference, int TYPE)
	{
		this.mContext 	= context;
		this.preference = preference;
		this.TYPE 		= TYPE;
		commonDA 		= new CommonDA();
	}
	
	public boolean uploadPayments(String empNo)
	{
		boolean isPaymentsUploaded = false;
		Vector<PostPaymentDONew> vecPayments =   new Vector<PostPaymentDONew>();
		vecPayments						     =   commonDA.getAllPaymentsToPostNew(empNo, CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.USER_ID, ""), TYPE);
		ConnectionHelper connectionHelper = new ConnectionHelper(null);
		if(vecPayments != null && vecPayments.size() > 0)
        {
			for (PostPaymentDONew postPaymentDONew : vecPayments) 
			{
				InsertPaymentParser insertPaymentParser	= new InsertPaymentParser(mContext);
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.postSinglePayments(postPaymentDONew),insertPaymentParser, ServiceURLs.INSERT_PAYMENT, preference);
				isPaymentsUploaded = insertPaymentParser.getStatus();
				
				if(isPaymentsUploaded)
					commonDA.updatePaymentStatus(postPaymentDONew);
			}
        }
        
        isPaymentsUploaded = commonDA.getAllPaymentsStatus(CalendarUtils.getOrderPostDate(), TYPE);
        return isPaymentsUploaded;
	}
}
