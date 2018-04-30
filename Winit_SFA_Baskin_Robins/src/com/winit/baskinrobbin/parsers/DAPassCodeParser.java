package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PasscodesDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class DAPassCodeParser extends BaseHandler
{
	private NameIDDo objNameIDDo;
	private Vector<NameIDDo> vecPasscodes;
	
	public DAPassCodeParser(Context context) 
	{
		super(context);
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("PassCodes"))
		{
			vecPasscodes = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("DAPassCodeDco"))
		{
			objNameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		currentElement = false;
		
		if(localName.equalsIgnoreCase("PassCodeId"))
		{
			objNameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EmpId"))
		{
			objNameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DAPassCode"))
		{
			objNameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DAPassCodeDco"))
		{
			vecPasscodes.add(objNameIDDo);
			if(vecPasscodes.size()>AppConstants.SYNC_COUNT)
			{
				PasscodesDA presellerDA = new PasscodesDA();
					presellerDA.inserDAPasscode(vecPasscodes);
				LogUtils.errorLog("DAPassCodeDco",""+vecPasscodes.size());
				vecPasscodes.clear();
			}
		}
		else if(localName.equalsIgnoreCase("PassCodes"))
		{
			PasscodesDA presellerDA = new PasscodesDA();
			if(vecPasscodes != null && vecPasscodes.size() > 0)
				presellerDA.inserDAPasscode(vecPasscodes);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	public Vector<NameIDDo> getPresellerCode()
	{
		return vecPasscodes;
	}
}
