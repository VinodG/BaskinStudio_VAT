package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class GenerateOfflineDataParser extends BaseHandler
{
	private Vector<NameIDDo> vecReceiptst;
	private NameIDDo nameIDDo;
	private String strSalesmanCode = "";
	public GenerateOfflineDataParser(Context context, String strSalesmanCode) 
	{
		super(context);
		this.strSalesmanCode = strSalesmanCode;
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("SequenceNos"))
		{
			vecReceiptst = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("SequenceNoDco"))
		{
			nameIDDo 		 = new NameIDDo();
			nameIDDo.strType = attributes.getValue("Type");
			nameIDDo.strId 	 = attributes.getValue("Number");
			vecReceiptst.add(nameIDDo);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("SequenceNos"))
		{
			if(vecReceiptst != null && vecReceiptst.size() > 0)
			{
				if(new PaymentDetailDA().insertOfflineData(vecReceiptst, strSalesmanCode))
				{
					preference.saveStringInPreference(Preference.OFFLINE_DATE, CalendarUtils.getOrderPostDate());
					preference.commitPreference();
				}
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
