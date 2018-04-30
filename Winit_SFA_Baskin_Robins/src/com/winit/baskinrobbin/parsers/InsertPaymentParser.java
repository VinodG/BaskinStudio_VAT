package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.AllUsersDo;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class InsertPaymentParser extends BaseHandler
{
	private AllUsersDo objPayments;
	private Vector<AllUsersDo> vecPaymentsNumbers;
	private String strStatus = "";
	
	public InsertPaymentParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("PaymentStatus"))
		{
			vecPaymentsNumbers = new Vector<AllUsersDo>();
		}
		else if(localName.equalsIgnoreCase("PaymentStatusDco"))
		{
			objPayments = new AllUsersDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(objPayments != null && localName.equalsIgnoreCase("Status"))
		{
			objPayments.pushStatus = StringUtils.getInt(currentValue.toString());
			LogUtils.errorLog("strStatus", "strStatus "+strStatus);
		}
		if(localName.equalsIgnoreCase("Receipt_Number"))
		{
			objPayments.strOldOrderNumber = currentValue.toString();
			objPayments.strNewOrderNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentStatusDco"))
		{
			if(objPayments.pushStatus != AppConstants.SERVER_ERROR)
				vecPaymentsNumbers.add(objPayments);
		}
		else if(localName.equalsIgnoreCase("PaymentStatus"))
		{
			updatePayments(vecPaymentsNumbers);
		}
	}
	
	
	public boolean getStatus()
	{
		if(strStatus.equalsIgnoreCase("Success"))
			return true;
		
		return false; 
	}
	public boolean updatePayments(Vector<AllUsersDo> vecPaymentsNumbers)
	{
		boolean result = false;
		result = new CommonDA().updatePayments(vecPaymentsNumbers);
		return result;
	}
	
	public String getReceiptNumber()
	{
		if(vecPaymentsNumbers != null && vecPaymentsNumbers.size()>0)
			return vecPaymentsNumbers.get(0).strNewOrderNumber;
		
		else
			return	"N/A";
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
