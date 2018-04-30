package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.MasterDA;
import com.winit.baskinrobbin.salesman.dataobject.SourceDO;

public class SouceParser extends BaseHandler
{
	private Vector<SourceDO> vecSourceDO;
	private SourceDO sourceDO;
	
	public SouceParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("objSource"))
		{
			vecSourceDO = new Vector<SourceDO>();
		}
		else if(localName.equalsIgnoreCase("SourceDco"))
		{
			sourceDO = new SourceDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Id"))
		{
			sourceDO.Id = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Sourcename"))
		{
			sourceDO.sourcename = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SourceDco"))
		{
			vecSourceDO.add(sourceDO);
		}
		else if(localName.equalsIgnoreCase("objSource"))
		{
			if(vecSourceDO.size() > 0)
			{
				if(saveIntoUserTable(vecSourceDO))
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
	
	private boolean saveIntoUserTable(Vector<SourceDO> vector)
	{
		return new MasterDA().insertttblSource(vector);
	}

}
