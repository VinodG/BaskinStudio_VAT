package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class GenerateOfflineDataParserNew extends BaseHandler
{
	private Vector<NameIDDo> vecReceiptst;
	private NameIDDo nameIDDo;
	private String strSalesmanCode = "";
	public GenerateOfflineDataParserNew(Context context, String strSalesmanCode) 
	{
		super(context);
		this.strSalesmanCode = strSalesmanCode;
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("TrxNos"))
		{
			vecReceiptst = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("AvailableTrxNoDco"))
		{
			nameIDDo 		 = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("TrxType"))
		{
			nameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TrxNo"))
		{
			nameIDDo.strId 	 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AvailableTrxNoDco"))
		{
			vecReceiptst.add(nameIDDo);
			if(vecReceiptst.size()>AppConstants.SYNC_COUNT)
			{
				new PaymentDetailDA().insertOfflineData(vecReceiptst, strSalesmanCode);
				LogUtils.errorLog("AvailableTrxNoDco",""+vecReceiptst.size());
				vecReceiptst.clear();
			}
		}
		else if(localName.equalsIgnoreCase("TrxNos"))
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
