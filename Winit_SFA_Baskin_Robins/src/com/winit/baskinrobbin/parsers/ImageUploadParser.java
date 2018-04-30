package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

public class ImageUploadParser extends BaseHandler 
{
	public ImageUploadParser(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private boolean executionStatus;
	
	private String filPath = "";
	
	private String errorMessage = "";
	
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement = true;
		currentValue = new StringBuilder();
	}
	
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement = false;
		
		if(localName.equalsIgnoreCase("FileName"))
		{
			filPath = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Message"))
		{
			if(currentValue.toString().equalsIgnoreCase("successful"))
			{
				executionStatus = true;
			}
			else
			{
				errorMessage = currentValue.toString();
				executionStatus = false;
			}
		}
		else
		{
			String filPath1 = currentValue.toString();
		}
	}
	
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement)
		{
			currentValue.append(ch, start, length);
			currentElement = false;
		}
	}
	
	public String getUploadedFileName()
	{
		return filPath;
	}
}
