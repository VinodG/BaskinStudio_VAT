package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class RegionListParser extends BaseHandler
{
	private Vector<NameIDDo> vecRegionList;
	private NameIDDo nameIDDo;
	
	public RegionListParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Locations"))
		{
			vecRegionList = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("LocationDco"))
		{
			nameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_LOCATIONS+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("LocationId"))
		{
			nameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LocationName"))
		{
			nameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LocationDco"))
		{
			vecRegionList.add(nameIDDo);
		}
		else if(localName.equalsIgnoreCase("Locations"))
		{
			if(new UserInfoDA().insertRegionList(vecRegionList))
				preference.commitPreference();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
