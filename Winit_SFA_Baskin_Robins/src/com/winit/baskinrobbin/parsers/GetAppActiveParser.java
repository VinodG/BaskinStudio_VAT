package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import android.content.Context;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.common.Preference;

public class GetAppActiveParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private String strName;
	public GetAppActiveParser(Context context) 
	{
		super(context);
		preference 		= 	new Preference(context);
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
		
		if(localName.equalsIgnoreCase("Status"))
		{
			strName = currentValue.toString();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public String getAppStatus()
	{
		if(TextUtils.isEmpty(strName))
			return "True";
		else if(strName.equalsIgnoreCase("Active"))
			return "True";
		else
			return "False";
	}
}
