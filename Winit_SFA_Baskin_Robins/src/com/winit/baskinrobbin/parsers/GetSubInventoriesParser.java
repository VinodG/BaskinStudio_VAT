package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.SubInventoriesParserDo;

public class GetSubInventoriesParser extends BaseHandler
{
	private Vector<SubInventoriesParserDo> vecSubInventories;
	private SubInventoriesParserDo objSubInventoriesParserDo;
	
	public GetSubInventoriesParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("FromSubInventories"))
		{
			vecSubInventories = new Vector<SubInventoriesParserDo>();
		}
		else if(localName.equalsIgnoreCase("VehicleDco"))
		{
			objSubInventoriesParserDo = new SubInventoriesParserDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("VEHICLE_NO"))
		{
			objSubInventoriesParserDo.strVEHICLE_NO = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VEHICLE_MODEL"))
		{
			objSubInventoriesParserDo.strVEHICLE_MODEL = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SubInventoryType"))
		{
			objSubInventoriesParserDo.subInventoryType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VehicleDco"))
		{
			vecSubInventories.add(objSubInventoriesParserDo);
		}
		else if(localName.equalsIgnoreCase("FromSubInventories"))
		{
			if(vecSubInventories != null && vecSubInventories.size() > 0)
				insertSubInventories(vecSubInventories);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	
	private void insertSubInventories(Vector<SubInventoriesParserDo> vecSubInventories2) 
	{
		new CommonDA().insertSubInventories(vecSubInventories2) ;
	}
}
