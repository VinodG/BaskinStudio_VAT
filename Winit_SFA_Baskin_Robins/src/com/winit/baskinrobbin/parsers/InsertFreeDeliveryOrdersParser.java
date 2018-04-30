package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.AllUsersDo;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class InsertFreeDeliveryOrdersParser extends BaseHandler
{
	private AllUsersDo objOrders;
	private Vector<AllUsersDo> vecOrderNumbers;
	private String strStatus = "",message= "";
	private Preference preference;
	private int statusCode;
	private String newOrderId="";
	
	public InsertFreeDeliveryOrdersParser(Context context) 
	{
		super(context);
		statusCode =0;
		message ="";
		strStatus ="";
		preference  = new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("OrderNumbers"))
		{
			vecOrderNumbers = new Vector<AllUsersDo>();
		}
		else if(localName.equalsIgnoreCase("OrderNumbersDco"))
		{
			objOrders = new AllUsersDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Status"))
		{
			strStatus = currentValue.toString();
			LogUtils.errorLog("strStatus", "strStatus "+strStatus);
		}
		if(localName.equalsIgnoreCase("OldNumber"))
		{
			objOrders.strOldOrderNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("NewNumber"))
		{
			objOrders.strNewOrderNumber = currentValue.toString();
			newOrderId=objOrders.strNewOrderNumber;
		}
		else if(localName.equalsIgnoreCase("OrderType"))
		{
			objOrders.strOrderType = currentValue.toString();
		}
		//need to verify with service
		else if(localName.equalsIgnoreCase("StatusCode"))
		{
			statusCode = StringUtils.getInt(currentValue.toString());
			if(StringUtils.getInt(currentValue.toString()) == 1)
				objOrders.pushStatus = 2;
			else
				objOrders.pushStatus = 1;
			LogUtils.errorLog("StatusCode", "statusCode "+statusCode);
		}
		else if(localName.equalsIgnoreCase("StatusMessage"))
		{
			message = currentValue.toString();
			objOrders.message = currentValue.toString();
//			if(objOrders.message.equalsIgnoreCase("Success"))
//				objOrders.pushStatus = 1;
		}
		//
		else if(localName.equalsIgnoreCase("OrderNumbersDco"))
		{
			vecOrderNumbers.add(objOrders);
		}
		else if(localName.equalsIgnoreCase("OrderNumbers"))
		{
			updateOrders(vecOrderNumbers);
		}
	}
	
	public boolean getStatus()
	{
		if(strStatus.equalsIgnoreCase("Success"))
			return true;
		else if(strStatus.equalsIgnoreCase("Failure"))
			return false;
		return false; 
	}
	public int getStatusCode()
	{
		return statusCode;
	}
	public String getMessage()
	{
		return message;
	}
	public String getNewOrderId()
	{
		return newOrderId;
	}
	public boolean updateOrders(Vector<AllUsersDo> vecOrderNumbers)
	{
		boolean result = false;
		result = new CommonDA().updateOrderNumbers(vecOrderNumbers);
		return result;
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
