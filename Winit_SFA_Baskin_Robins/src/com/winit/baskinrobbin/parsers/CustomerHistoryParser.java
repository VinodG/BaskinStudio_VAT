package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerHistoryBL;
import com.winit.baskinrobbin.salesman.dataobject.CustomerHistoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerReportDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class CustomerHistoryParser extends BaseHandler
{
	private CustomerHistoryDO objCustomerHistoryDO;
	private CustomerReportDO objReportDO;
	private Vector<CustomerHistoryDO> veCustomerHistoryDO;
	
	public CustomerHistoryParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement = true;
		currentValue =new StringBuilder();
		if (localName.equals("Customers"))
		{
			veCustomerHistoryDO = new Vector<CustomerHistoryDO>();
		}
		else if (localName.equals("CustomerHistoryDco"))
		{
			objCustomerHistoryDO  = new CustomerHistoryDO();
		}
		else if (localName.equals("CustomerReport"))
		{
			objCustomerHistoryDO.vecReportDO  = new Vector<CustomerReportDO>();
		}
		else if (localName.equals("CustomerReportDco"))
		{
			objReportDO  = new CustomerReportDO();
		}
	}

	/** Called when tag closing ( ex:- <event>AndroidPeople</event> 
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement = false;
		
		/** set value */ 
		if(localName.equalsIgnoreCase("ServerTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CustomerSiteID"))
		{
			objCustomerHistoryDO.CustomerSiteID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Month"))
		{
			objReportDO.month = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CurrentMonthAchieved"))
		{
			objReportDO.CurrentMonthAchieved = (int)StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("LastYearCurrentMonthAchieved"))
		{
			objReportDO.PreviousMonthAchieved = (int)StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CustomerReportDco"))
		{
			objCustomerHistoryDO.vecReportDO.add(objReportDO);
		}
		else if(localName.equalsIgnoreCase("CustomerHistoryDco"))
		{
			veCustomerHistoryDO.add(objCustomerHistoryDO);
		}
		else if(localName.equalsIgnoreCase("Customers"))
		{
			LogUtils.errorLog("veCustomerHistoryDO - ", "veCustomerHistoryDO  - "+veCustomerHistoryDO.size());
			CustomerHistoryBL  customerHistoryBL = new CustomerHistoryBL();
			customerHistoryBL.inserCustomerHistory(veCustomerHistoryDO);
			preference.commitPreference();
		}
	}

	/** Called to get tag characters ( ex:- <event>AndroidPeople</event> 
	 * -- to get event Character ) */
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
	}
}
