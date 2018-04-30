package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataobject.BlaseUserDco;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllReasonsParser extends BaseHandler
{
	private BlaseUserDco blaseUserOb;
	private Vector<BlaseUserDco> vecReasons;

	public GetAllReasonsParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Reasons"))
		{
			vecReasons = new Vector<BlaseUserDco>();
		}
		else if(localName.equalsIgnoreCase("ReasonDco"))
		{
			blaseUserOb = new BlaseUserDco();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_REASONS+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ReasonId"))
		{
			blaseUserOb.strUserid = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Type"))
		{
			blaseUserOb.strRole = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Name"))
		{
			blaseUserOb.strUserName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ReasonDco"))
		{
			vecReasons.add(blaseUserOb);
		}
		else if(localName.equalsIgnoreCase("Reasons"))
		{
			if(vecReasons.size() > 0)
			{
				if(saveIntoUserTable(vecReasons))
					preference.commitPreference();
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveIntoUserTable(Vector<BlaseUserDco> vector)
	{
		return new UserInfoDA().insertReasons(vector);
	}
}
