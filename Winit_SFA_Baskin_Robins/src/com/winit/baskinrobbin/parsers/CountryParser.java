package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.MasterDA;
import com.winit.baskinrobbin.salesman.dataobject.CountryDO;

public class CountryParser extends BaseHandler
{
	private Vector<CountryDO> vecCountryDO;
	private CountryDO countryDO;
	
	public CountryParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("objCountry"))
		{
			vecCountryDO = new Vector<CountryDO>();
		}
		else if(localName.equalsIgnoreCase("CountryDco"))
		{
			countryDO = new CountryDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CountryId"))
		{
			countryDO.countryId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryName"))
		{
			countryDO.countryName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryDesc"))
		{
			countryDO.countryDesc = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryDco"))
		{
			vecCountryDO.add(countryDO);
		}
		else if(localName.equalsIgnoreCase("objCountry"))
		{
			if(vecCountryDO.size() > 0)
			{
				if(saveIntoUserTable(vecCountryDO))
					preference.commitPreference();
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveIntoUserTable(Vector<CountryDO> vector)
	{
		return new MasterDA().inserttblCountry(vector);
	}
}
