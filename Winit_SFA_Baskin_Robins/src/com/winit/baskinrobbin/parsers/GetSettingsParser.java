package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.SettingsDO;

public class GetSettingsParser extends BaseHandler
{
	private Vector<SettingsDO> vecSettings ;
	private SettingsDO settingsDO;
	
	public GetSettingsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("FromSettings"))
		{
			vecSettings = new Vector<SettingsDO>();
		}
		else if(localName.equalsIgnoreCase("SettingDco"))
		{
			settingsDO = new SettingsDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("SettingId"))
			settingsDO.SettingId = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("SettingName"))
			settingsDO.SettingName = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("SettingValue"))
			settingsDO.SettingValue = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("DataType"))
			settingsDO.DataType = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("CountryId"))
			settingsDO.CountryId = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("SettingDco"))
			vecSettings.add(settingsDO);
		
		else if(localName.equalsIgnoreCase("FromSettings"))
			if(vecSettings != null && vecSettings.size() > 0)
				insertSettings(vecSettings);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertSettings(Vector<SettingsDO> vecSettings) 
	{
		new SettingsDA().insertAllSettings(vecSettings);
	}
}
