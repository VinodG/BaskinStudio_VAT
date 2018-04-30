package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;

import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import android.content.Context;

public class BooleanParser extends BaseHandler{
	
	private StringBuffer sBuffer = null;
	private int status=-1;
	private String message = "";
	public BooleanParser(Context context) {
		super(context);
	}
	public void characters(char[] ch, int start, int length) 
	{
		if (ch == null || length == 0 || sBuffer==null)
			return;
		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		e.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
	{
		sBuffer = new StringBuffer();
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		if (localName.equalsIgnoreCase("GetUserDeviceStatusResult"))
		{
			if(sBuffer.toString().equalsIgnoreCase("false"))
				status=0;
			else
				status=1;
		}
		else if (localName.equalsIgnoreCase("GetUserReconciliationResult"))
		{
			if(sBuffer.toString().equalsIgnoreCase("false"))
				status=0;
			else
				status=1;
		}
		else if(localName.equalsIgnoreCase("GetUserDeviceStatusByPasscodeResponse"))
		{
			
		}
		else if(localName.equalsIgnoreCase("InsertVechileTrackingResult"))
		{
			status=StringUtils.getInt(sBuffer.toString());
		}
		else if(localName.equalsIgnoreCase("Message")){
			message = sBuffer.toString();
		}
		else if(localName.equalsIgnoreCase("Status")){
			if(sBuffer.toString().equalsIgnoreCase("Failure"))
				status=0;
			else
				status=1;
		}
	}
	public Object getData() 
	{
//		return 1;
		return status;
	}
	
	public String getServerMessage(){
		return message.toString();
	}

}
