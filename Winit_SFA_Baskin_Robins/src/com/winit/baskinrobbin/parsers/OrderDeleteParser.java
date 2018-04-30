package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

public class OrderDeleteParser extends BaseHandler
{
	private String  Status = "";
	private String count= "-1";
	public OrderDeleteParser(Context context) 
	{
		super(context);
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Status"))
		{
			Status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Count"))
		{
			count = currentValue.toString();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getOrderDeleteStatus()
	{
		return Status.equalsIgnoreCase("Success");
	}
	public String getId()
	{
		return count;
	}
}
