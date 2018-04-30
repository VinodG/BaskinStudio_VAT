package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class InsertLoadParser extends BaseHandler
{
	private boolean status = false;
	private Vector<NameIDDo> vec;
	private NameIDDo nameIDDo;
	public InsertLoadParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue    = new StringBuilder();
		if(localName.equalsIgnoreCase("MovementStatusList"))
		{
			vec = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("MovementStatusDco"))
		{
			nameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("MovementCode"))
		{
			nameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppMovementId"))
		{
			nameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status") && nameIDDo != null)
		{
			nameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementStatusDco"))
		{
			if(StringUtils.getInt(nameIDDo.strName) != AppConstants.SERVER_ERROR)
				vec.add(nameIDDo);
		}
		else if(localName.equalsIgnoreCase("MovementStatusList"))
		{
			if(vec!=null && vec.size()>0)
				new InventoryDA().updateStatus(vec);
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
