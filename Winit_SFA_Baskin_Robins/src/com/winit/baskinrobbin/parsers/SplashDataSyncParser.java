package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SplashDataSyncParser extends BaseHandler
{
	private Context context;
	private final int PARSING_SCOPE_USERS = 1;
	private final int PARSING_SCOPE_REASONS = 2;
	private final int PARSING_SCOPE_LOCATIONS = 3;
	private final int PARSING_SCOPE_CATEGORIES = 4;
	private final int PARSING_SCOPE_ITEMS = 5;
	private final int PARSING_SCOPE_PRICE = 10;
	private final int PARSING_SCOPE_BANKS = 6;
	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	private final int PARSING_SCOPE_AR_INV_METHODs = 11;
	private final int PARSING_SCOPE_RECEIPT_METHODs = 12;
	private final int PARSING_SCOPE_TRXTYPE_METHODs = 13;
	private final int PARSING_SCOPE_From_SubInventories = 14;
	private final int PARSING_SCOPE_UOM_FACTOR 	= 15;
	private final int PARSING_SCOPE_SETTINGS 	= 16;
	
	private BaseHandler currentHandler;
	private SynLogDO syncLogDO = new SynLogDO();
	
	public SplashDataSyncParser(Context context)
	{
		super(context);
		this.context = context;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_INVALID):
			{
				if (localName.equalsIgnoreCase("ServerTime"))
				{
					parsingScope = PARSING_SCOPE_SERVERTIME;
				}
				else if (localName.equalsIgnoreCase("ModifiedDate"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("ModifiedTime"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("BlaseUsers"))
				{
					parsingScope   = PARSING_SCOPE_USERS;
					currentHandler = new GetAllUserParser(context);
				}
				else if (localName.equalsIgnoreCase("Reasons"))
				{
					parsingScope   = PARSING_SCOPE_REASONS;
					currentHandler = new GetAllReasonsParser(context);
				}
				else if (localName.equalsIgnoreCase("Locations"))
				{
					parsingScope   = PARSING_SCOPE_LOCATIONS;
					currentHandler = new RegionListParser(context);
				}
				else if (localName.equalsIgnoreCase("Categories"))
				{
					parsingScope   = PARSING_SCOPE_CATEGORIES;
					currentHandler = new GetAllCategories(context);
				}
				else if (localName.equalsIgnoreCase("Items"))
				{
					parsingScope   = PARSING_SCOPE_ITEMS;
					currentHandler = new GetAllItemsParser(context);
				}
				else if (localName.equalsIgnoreCase("Banks"))
				{
					parsingScope   = PARSING_SCOPE_BANKS;
					currentHandler = new GetBanksParser(context);
				}
				else if (localName.equalsIgnoreCase("UOMFactors"))
				{
					parsingScope   = PARSING_SCOPE_UOM_FACTOR;
					currentHandler = new GetUOMFactorParser(context);
				}
				else if (localName.equalsIgnoreCase("Prices"))
				{
					parsingScope   = PARSING_SCOPE_PRICE;
					currentHandler = new GetPricingParser(context);
				}
				else if (localName.equalsIgnoreCase("AR_INV_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_AR_INV_METHODs;
					currentHandler = new GetARInvoiceParser(context);
				}
				else if (localName.equalsIgnoreCase("RECEIPT_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_RECEIPT_METHODs;
					currentHandler = new GetReceiptMethodParser(context);
				}
				else if (localName.equalsIgnoreCase("TRXTYPE_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_TRXTYPE_METHODs;
					currentHandler = new GetTRXTypeMethodsParser(context);
				}
				else if (localName.equalsIgnoreCase("FromSubInventories"))
				{
					parsingScope   = PARSING_SCOPE_From_SubInventories;
					currentHandler = new GetSubInventoriesParser(context);
				}
				else if (localName.equalsIgnoreCase("FromSettings"))
				{
					parsingScope   = PARSING_SCOPE_SETTINGS;
					currentHandler = new GetSettingsParser(context);
				}
				
				if(currentHandler != null)
					currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
			
			case (PARSING_SCOPE_USERS):
			case (PARSING_SCOPE_REASONS):
			case (PARSING_SCOPE_LOCATIONS):
			case (PARSING_SCOPE_CATEGORIES):
			case (PARSING_SCOPE_ITEMS):
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_UOM_FACTOR):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_PRICE):
			case (PARSING_SCOPE_AR_INV_METHODs):
			case (PARSING_SCOPE_RECEIPT_METHODs):
			case (PARSING_SCOPE_TRXTYPE_METHODs):
			case (PARSING_SCOPE_From_SubInventories):
			{
				currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			currentValue = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_USERS):
			case (PARSING_SCOPE_REASONS):
			case (PARSING_SCOPE_LOCATIONS):
			case (PARSING_SCOPE_CATEGORIES):
			case (PARSING_SCOPE_ITEMS):
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_UOM_FACTOR):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_PRICE):
			case (PARSING_SCOPE_AR_INV_METHODs):
			case (PARSING_SCOPE_RECEIPT_METHODs):
			case (PARSING_SCOPE_TRXTYPE_METHODs):
			case (PARSING_SCOPE_From_SubInventories):
			{
				currentHandler.currentValue = currentValue;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("BlaseUsers") ||
				   localName.equalsIgnoreCase("Reasons") ||
				   localName.equalsIgnoreCase("Locations") ||
				   localName.equalsIgnoreCase("Categories") ||
				   localName.equalsIgnoreCase("Items") ||
				   localName.equalsIgnoreCase("Banks")||
				   localName.equalsIgnoreCase("UOMFactors")||
				   localName.equalsIgnoreCase("FromSettings")||
				   localName.equalsIgnoreCase("Prices") ||
				   localName.equalsIgnoreCase("AR_INV_METHODs") ||
				   localName.equalsIgnoreCase("RECEIPT_METHODs") ||
				   localName.equalsIgnoreCase("TRXTYPE_METHODs") ||
				   localName.equalsIgnoreCase("FromSubInventories"))
				{
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
				}
			}
			break;
			
			case (PARSING_SCOPE_SERVERTIME):
			{
				if(localName.equalsIgnoreCase("ServerTime"))
				{
					syncLogDO.TimeStamp = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, currentValue.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
				if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					syncLogDO.UPMJ = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, currentValue.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					syncLogDO.action =  currentValue.toString();
				}
				if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					syncLogDO.UPMT = currentValue.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, currentValue.toString());
					parsingScope = PARSING_SCOPE_INVALID;
					syncLogDO.entity = ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC;
					new SynLogDA().insertSynchLog(syncLogDO);
				}
			}
			break;
			
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("SplashScreenData"))
					preference.commitPreference();
			}
			break;
		}
	}

	public void characters(char[] ch, int start, int length) 
	{
		if (parsingScope == PARSING_SCOPE_INVALID || ch == null || length == 0 || currentValue == null)
			return;

		try
		{
			currentValue.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "XML ch[] appending exception:"+e.getMessage() );
		}
	}
}
