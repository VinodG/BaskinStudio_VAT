package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.ReceiptMethodNameDo;

public class GetReceiptMethodParser extends BaseHandler
{
	private Vector<ReceiptMethodNameDo> vecReceiptMethodName ;
	private ReceiptMethodNameDo objReceiptMethodNameDo;
	
	public GetReceiptMethodParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("RECEIPT_METHODs"))
		{
			vecReceiptMethodName = new Vector<ReceiptMethodNameDo>();
		}
		else if(localName.equalsIgnoreCase("RECEIPT_METHODDco"))
		{
			objReceiptMethodNameDo = new ReceiptMethodNameDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("RECEIPT_METHOD_NAME"))
		{
			objReceiptMethodNameDo.strRECEIPT_METHOD_NAME = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ORG_ID"))
		{
			objReceiptMethodNameDo.ORG_ID = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objReceiptMethodNameDo.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objReceiptMethodNameDo.ModifiedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("RECEIPT_METHODDco"))
		{
			vecReceiptMethodName.add(objReceiptMethodNameDo);
		}
		else if(localName.equalsIgnoreCase("RECEIPT_METHODs"))
		{
			if(vecReceiptMethodName != null && vecReceiptMethodName.size() > 0)
				insertReceiptMethodName(vecReceiptMethodName);
		}
	}
	
	
		
	

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	
	private void insertReceiptMethodName(Vector<ReceiptMethodNameDo> vecReceiptMethodName2) 
	{
		new CommonDA().insertReceiptMethodName(vecReceiptMethodName2);
	}
}
