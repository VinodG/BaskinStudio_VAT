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

public class InsertInventoryParser extends BaseHandler
{
	private AllUsersDo objOrders;
	private Vector<AllUsersDo> vecOrderNumbers;
	private String strStatus = "",message= "";
	private int statusCode;

	public InsertInventoryParser(Context context) 
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
		if(localName.equalsIgnoreCase("InventoryIds"))
		{
			vecOrderNumbers = new Vector<AllUsersDo>();
		}
		else if(localName.equalsIgnoreCase("InventoryIdDco"))
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
			LogUtils.errorLog("strStatus", strStatus);
		}
		if(localName.equalsIgnoreCase("OldId"))
		{
			objOrders.strOldOrderNumber = currentValue.toString();
			LogUtils.errorLog("OldId", objOrders.strOldOrderNumber);
		}
		else if(localName.equalsIgnoreCase("NewId"))
		{
			objOrders.strNewOrderNumber = currentValue.toString();
			LogUtils.errorLog("NewId", objOrders.strNewOrderNumber);
		}
		else if(localName.equalsIgnoreCase("StatusCode"))
		{
			objOrders.pushStatus = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InventoryIdDco"))
		{
			vecOrderNumbers.add(objOrders);
		}
		else if(localName.equalsIgnoreCase("InventoryIds"))
		{
			updateInventory(vecOrderNumbers);
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
	public boolean updateInventory(Vector<AllUsersDo> vecOrderNumbers)
	{
		boolean result = false;
		result = new CommonDA().updateInventory(vecOrderNumbers);
		return result;
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
