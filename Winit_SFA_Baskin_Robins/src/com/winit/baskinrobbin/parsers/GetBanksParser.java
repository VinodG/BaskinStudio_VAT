package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;

public class GetBanksParser extends BaseHandler
{
	private Vector<NameIDDo> vecBankNames ;
	private NameIDDo objNameIDDo;
	
	public GetBanksParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Banks"))
		{
			vecBankNames = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("BankDco"))
		{
			objNameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("BankId"))
		{
			objNameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BankName"))
		{
			objNameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BankCode"))
		{
			objNameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BankDco"))
		{
			vecBankNames.add(objNameIDDo);
		}
		else if(localName.equalsIgnoreCase("Banks"))
		{
			if(vecBankNames != null && vecBankNames.size() > 0)
				insertBankData(vecBankNames);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertBankData(Vector<NameIDDo> vecBankNames) 
	{
		new CommonDA().insertBankDetails(vecBankNames);
	}
}
