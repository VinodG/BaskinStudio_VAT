package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class VerifyMovementParser  extends BaseHandler{
	private boolean status = false;
	private boolean isMove = false;
	public VerifyMovementParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("MovementStatusList"))
		{
			isMove = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(isMove && localName.equalsIgnoreCase("Status"))
		{
			if(StringUtils.getInt(currentValue.toString()) == 0)
				status = true;
			else
				status = false;
		}
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getStatus()
	{
		return status;
	}
}
