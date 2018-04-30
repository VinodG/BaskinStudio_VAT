package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import android.content.Context;

public class VerifyUnloadApproved extends BaseHandler{
	private boolean status = false;
	private boolean isMove = false;
	private String message = "";
	public VerifyUnloadApproved(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
		}else if(localName.equalsIgnoreCase("Message")){
			message = currentValue.toString();
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
	public String getServerMessage(){
		return message.toString();
	}

	
}
