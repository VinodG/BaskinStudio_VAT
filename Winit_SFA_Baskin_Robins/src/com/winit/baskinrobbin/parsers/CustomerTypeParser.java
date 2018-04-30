package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.MasterDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerTypeDO;

public class CustomerTypeParser extends BaseHandler
{
	private Vector<CustomerTypeDO> vecCustomerTypeDO;
	private CustomerTypeDO customerTypeDO;
	
	public CustomerTypeParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("objCustomerType"))
		{
			vecCustomerTypeDO = new Vector<CustomerTypeDO>();
		}
		else if(localName.equalsIgnoreCase("CustomerTypeDco"))
		{
			customerTypeDO = new CustomerTypeDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CustomerTypeId"))
		{
			customerTypeDO.customerTypeId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerTypeName"))
		{
			customerTypeDO.customerTypeName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerTypeDco"))
		{
			vecCustomerTypeDO.add(customerTypeDO);
		}
		else if(localName.equalsIgnoreCase("objCustomerType"))
		{
			if(vecCustomerTypeDO.size() > 0)
			{
				if(saveIntoUserTable(vecCustomerTypeDO))
					preference.commitPreference();
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveIntoUserTable(Vector<CustomerTypeDO> vector)
	{
		return new MasterDA().inserttblCustomerType(vector);
	}
	
}
