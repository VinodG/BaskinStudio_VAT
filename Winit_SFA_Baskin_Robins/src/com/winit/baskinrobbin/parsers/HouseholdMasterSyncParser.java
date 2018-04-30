package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class HouseholdMasterSyncParser extends BaseHandler
{
	private Context context;
	private StringBuilder sBuffer;
	
	BaseHandler currentHandler;
	
	private final int PARSING_SCOPE_COUNTRY = 1;
	private final int PARSING_SCOPE_CUSTOMERTYPE = 2;
	private final int PARSING_SCOPE_SOURCE = 3;
	
	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	
	
	public HouseholdMasterSyncParser(Context context)
	{
		super(context);
		this.context = context;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
			case PARSING_SCOPE_INVALID:
			{
				if(localName.equalsIgnoreCase("CurrentTime"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if(localName.equalsIgnoreCase("objCountry"))
				{
					parsingScope = PARSING_SCOPE_COUNTRY;
					currentHandler = new CountryParser(context);
				}
				else if(localName.equalsIgnoreCase("objCustomerType"))
				{
					parsingScope = PARSING_SCOPE_CUSTOMERTYPE;
					currentHandler = new CustomerTypeParser(context);
				}
				else if(localName.equalsIgnoreCase("objSource"))
				{
					parsingScope = PARSING_SCOPE_SOURCE;
					currentHandler = new SouceParser(context);
				}
				if(currentHandler != null)
					currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
			
			case PARSING_SCOPE_COUNTRY:
			case PARSING_SCOPE_CUSTOMERTYPE:
			case PARSING_SCOPE_SOURCE:
			{
				currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			sBuffer = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		switch (parsingScope)
		{
			case PARSING_SCOPE_COUNTRY:
			case PARSING_SCOPE_CUSTOMERTYPE:
			case PARSING_SCOPE_SOURCE:
			{
				currentHandler.currentValue = sBuffer;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("objCountry") ||
				   localName.equalsIgnoreCase("objCustomerType") ||
				   localName.equalsIgnoreCase("objSource"))
				{
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
				}
			}
			break;
			
			case PARSING_SCOPE_SERVERTIME:
			{
				if(localName.equalsIgnoreCase("CurrentTime"))
				{
					SynLogDO synLogDO = new SynLogDO();
					synLogDO.TimeStamp = sBuffer.toString();
					synLogDO.entity= ServiceURLs.GetHouseHoldMastersWithSync;
					new SynLogDA().insertSynchLog(synLogDO);
//					preference.saveStringInPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
			}
			break;
			
			case PARSING_SCOPE_INVALID:
			{
//				if(localName.equalsIgnoreCase("GetHouseHoldMastersWithSyncResponse"))
//					preference.commitPreference();
			}
			break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if (parsingScope == PARSING_SCOPE_INVALID || ch == null || length == 0 || sBuffer == null)
			return;

		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "XML ch[] appending exception:"+e.getMessage() );
		}
	}
}
