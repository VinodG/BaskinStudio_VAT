package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.ARInvoiceDo;

public class GetARInvoiceParser extends BaseHandler
{
	private Vector<ARInvoiceDo> vecARInvoice ;
	private ARInvoiceDo objARInvoiceDo;
	
	public GetARInvoiceParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("AR_INV_METHODs"))
		{
			vecARInvoice = new Vector<ARInvoiceDo>();
		}
		else if(localName.equalsIgnoreCase("AR_INV_METHODDco"))
		{
			objARInvoiceDo = new ARInvoiceDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CUSTOMER_TRX_TYPE_NAME"))
		{
			objARInvoiceDo.strCUSTOMER_TRX_TYPE_NAME = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CUSTOMER_TRX_TYPE_KEY"))
		{
			objARInvoiceDo.strCUSTOMER_TRX_TYPE_KEY = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BATCH_SOURCE_NAME"))
		{
			objARInvoiceDo.strBATCH_SOURCE_NAME = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ORG_ID"))
		{
			objARInvoiceDo.ORG_ID = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objARInvoiceDo.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objARInvoiceDo.ModifiedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AR_INV_METHODDco"))
		{
			vecARInvoice.add(objARInvoiceDo);
		}
		else if(localName.equalsIgnoreCase("AR_INV_METHODs"))
		{
			if(vecARInvoice != null && vecARInvoice.size() > 0)
				insertARInvoice(vecARInvoice);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	
	private void insertARInvoice(Vector<ARInvoiceDo> vecARInvoice2) 
	{
		new CommonDA().insertArInvoiceMethod(vecARInvoice2);
	}
}
