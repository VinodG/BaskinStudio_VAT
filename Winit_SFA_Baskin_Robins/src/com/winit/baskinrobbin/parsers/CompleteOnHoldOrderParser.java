package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.AllUsersDo;

public class CompleteOnHoldOrderParser extends BaseHandler
{
	private String hodOrderStatus = "";
	
	public CompleteOnHoldOrderParser(Context context) 
	{
		super(context);
		preference  = new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("OnHoldOrderStatus"))
		{
			hodOrderStatus = currentValue.toString();
		}
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
	
	public boolean getHoldOrderStatus()
	{
		if(hodOrderStatus.equalsIgnoreCase("SUCCESS"))
			return true;
		else
			return false;
	}
}
