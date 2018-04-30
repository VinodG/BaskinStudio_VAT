package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.TRXTYPEDo;

public class GetTRXTypeMethodsParser extends BaseHandler
{
	private Vector<TRXTYPEDo> vecTRXTYPE ;
	private TRXTYPEDo objTRXTYPEDo;
	
	public GetTRXTypeMethodsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("TRXTYPE_METHODs"))
		{
			vecTRXTYPE = new Vector<TRXTYPEDo>();
		}
		else if(localName.equalsIgnoreCase("TRXTYPE_METHODDco"))
		{
			objTRXTYPEDo = new TRXTYPEDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("TRANSACTION_TYPE_NAME"))
		{
			objTRXTYPEDo.strTRANSACTION_TYPE_NAME = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TRANSACTION_TYPE_KEY"))
		{
			objTRXTYPEDo.strTRANSACTION_TYPE_KEY = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objTRXTYPEDo.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objTRXTYPEDo.ModifiedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TRXTYPE_METHODDco"))
		{
			vecTRXTYPE.add(objTRXTYPEDo);
		}
		else if(localName.equalsIgnoreCase("TRXTYPE_METHODs"))
		{
			if(vecTRXTYPE != null && vecTRXTYPE.size() > 0)
				insertTRXTYPE(vecTRXTYPE);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	
	private void insertTRXTYPE(Vector<TRXTYPEDo> vecTRXTYPE2) 
	{
		new CommonDA().insertTRXTYPE(vecTRXTYPE2) ;
	}
}
