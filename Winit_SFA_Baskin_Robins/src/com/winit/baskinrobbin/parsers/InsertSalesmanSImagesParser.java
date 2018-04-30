package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.AllUsersDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

public class InsertSalesmanSImagesParser extends BaseHandler
{
	private final int ENABLE = 100, DISABLE = 200;
	private AllUsersDo objOrders;
	private Vector<AllUsersDo> vecOrderNumbers;
	private Preference preference;
	private String newOrderId="";
	private int ENABLE_PARSING = DISABLE;
	private String strStatus = "";
	private boolean isPosted = false;
	private int hodOrderStatus = 0;
	
	public InsertSalesmanSImagesParser(Context context) 
	{
		super(context);
		preference  = new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("PostSignatureResult"))
		{
			vecOrderNumbers = new Vector<AllUsersDo>();
		}
		else if(localName.equalsIgnoreCase("TrxStatusDco"))
		{
			ENABLE_PARSING = ENABLE;
			objOrders = new AllUsersDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		switch (ENABLE_PARSING) 
		{
		case ENABLE:
			if(localName.equalsIgnoreCase("OrderNumber"))
			{
				objOrders.strOldOrderNumber = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("AppId"))
			{
				objOrders.strUUID = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("Status"))
			{
				objOrders.pushStatus = StringUtils.getInt(currentValue.toString());
				hodOrderStatus = objOrders.pushStatus;
				if(objOrders.pushStatus == 1)
					isPosted = true;
			}
			//need to verify with service
			else if(localName.equalsIgnoreCase("Message"))
			{
				objOrders.message = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("TrxStatusDco"))
			{
				if(objOrders.pushStatus != AppConstants.SERVER_ERROR)
					vecOrderNumbers.add(objOrders);
			}
			else if(localName.equalsIgnoreCase("PostSignatureResult"))
			{
				ENABLE_PARSING = DISABLE;
				updateOrders(vecOrderNumbers);
			}
			break;

		case DISABLE:
			if(localName.equalsIgnoreCase("Status"))
				strStatus = currentValue.toString();
			break;
			
		default:
			break;
		}
	}
	
	public String getNewOrderId()
	{
		return newOrderId;
	}
	public boolean updateOrders(Vector<AllUsersDo> vecOrderNumbers)
	{
		boolean result = false;
		result = new CommonDA().updateSalesmanOrderNumbers(vecOrderNumbers);
		return result;
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}

	public boolean getStatus()
	{
		if(strStatus != null && strStatus.equalsIgnoreCase("Success"))
			return true;
		return false;
	}
	
	public boolean getPostStatus()
	{
		return isPosted;
	}
	
	public int getHoldOrderStatus()
	{
		return hodOrderStatus;
	}
}
