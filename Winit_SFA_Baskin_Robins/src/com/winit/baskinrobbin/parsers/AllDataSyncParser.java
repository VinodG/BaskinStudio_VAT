package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.SyncData.SyncProcessListner;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;
public class AllDataSyncParser extends BaseHandler
{
	private final static float  TOTAL_MAIN_MODULES=23.0f;
	private int  SYNCED_MAIN_MODULES=0;
	
	
	private Context context;
	private StringBuilder sBuffer;
	private SyncProcessListner syncProcessListner;
	private final int PARSING_SCOPE_BANKS = 1;
	private final int PARSING_SCOPE_VEHICLES = 2;
	private final int PARSING_SCOPE_USERS = 3;
	private final int PARSING_SCOPE_REASONS = 4;
	private final int PARSING_SCOPE_AR_INV_METHODs = 5;
	private final int PARSING_SCOPE_RECEIPT_METHODs = 6;
	private final int PARSING_SCOPE_TRXTYPE_METHODs = 7;
	private final int PARSING_SCOPE_CATEGORIES = 8;
	private final int PARSING_SCOPE_ITEMS = 9;
	private final int PARSING_SCOPE_PRICE = 10;
	private final int PARSING_SCOPE_From_SubInventories = 11;
	private final int PARSING_SCOPE_UOM_FACTORS = 12;
	private final int PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE = 13;
	private final int PARSING_SCOPE_OFFLINE_DATA_RESPONSE = 14;
	private final int PARSING_SCOPE_LOCATIONS = 15;
	private final int PARSING_SCOPE_CUSTOMERS = 16;
	private final int PARSING_SCOPE_DAILY_JOURNEY_PLANS = 17;
	private final int PARSING_SCOPE_DISCOUNTS = 18;
	private final int PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE = 19;
	private final int PARSING_SCOPE_CUSTOMER_RESPONSE = 20;
	private final int PARSING_SCOPE_DAPASSCODE_RESPONSE = 21;
	private final int PARSING_SCOPE_SETTINGS=22;
	private final int PARSING_SCOPE_PENDING_INVOICES = 23;

	//==================================================Added For VAT==============================
	private final int PARSING_SCOPE_Tax = 24;
	private final int PARSING_SCOPE_TaxGroup = 25;
	private final int PARSING_SCOPE_TaxGroupTaxes = 26;
	private final int PARSING_SCOPE_TaxSkuMap = 27;
	private final int PARSING_SCOPE_TaxSlab = 28;
	private final int PARSING_SCOPE_TaxOrg = 29;
	private final int PARSING_SCOPE_TaxSalesOrderTax = 30;
	private final int PARSING_SCOPE_SalesOrganization = 31;


	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int serverTimeCount=-1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	private String empNo;
	BaseHandler currentHandler;
	SynLogDO syncLogDO = new SynLogDO();
	SynLogDO syncCompleteLogDO = new SynLogDO();
	public Preference preference;
	//TRXStatus
	public AllDataSyncParser(Context context,SyncProcessListner syncProcessListner)
	{
		super(context);
		this.context = context;
		this.syncProcessListner = syncProcessListner;
		preference = new Preference(context);
	}
	public AllDataSyncParser(Context context, String empNo,SyncProcessListner syncProcessListner) 
	{
		super(context);
		this.context = context;
		this.empNo = empNo;
		this.syncProcessListner = syncProcessListner;
		preference = new Preference(context);
	}
	//tblSellingSKUClassification
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		if(localName.equalsIgnoreCase("ItemMasterDataResponse"))
			LogUtils.debug("startElement", ""+localName);
		switch (parsingScope)
		{
			case (PARSING_SCOPE_INVALID):
			{
				if (localName.equalsIgnoreCase("ServerTime"))
				{
					serverTimeCount++;
					parsingScope = PARSING_SCOPE_SERVERTIME;
				}
				else if (localName.equalsIgnoreCase("ModifiedDate"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("ModifiedTime"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("Vehicles"))  // Done
				{
					parsingScope   = PARSING_SCOPE_VEHICLES;
					currentHandler = new GetVehiclesParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				//DiscountAndInvoicesDataResponse
				else if (localName.equalsIgnoreCase("objDiscountResponse"))
				{
					parsingScope   = PARSING_SCOPE_DISCOUNTS;
					currentHandler = new GetDiscountParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("objCustomerResponse"))
				{
					parsingScope   = PARSING_SCOPE_CUSTOMERS;
					currentHandler = new GetCustomersByUserIdParser(context,empNo);
					LogUtils.debug("startElement", ""+localName);
				}
				//TrxHeaderResponse
				else if (localName.equalsIgnoreCase("objTrxHeaderResponse"))
				{
					parsingScope   = PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE;
					currentHandler = new GetTrxHeaderForApp(context,empNo);
					LogUtils.debug("startElement", ""+localName);
				}
				//DAPassCodeResponse
				else if (localName.equalsIgnoreCase("objDAPassCodeResponse"))
				{
					parsingScope   = PARSING_SCOPE_DAPASSCODE_RESPONSE;
					currentHandler = new DAPassCodeParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("objJourneyPlanResponse"))
				{
					parsingScope   = PARSING_SCOPE_DAILY_JOURNEY_PLANS;
					currentHandler = new GetDailyJPAndRoute(context,empNo);
					LogUtils.debug("startElement", ""+localName);
				}
				//OfflineDataResponse
				else if (localName.equalsIgnoreCase("OfflineDataResponse"))
				{
					parsingScope   = PARSING_SCOPE_OFFLINE_DATA_RESPONSE;
					currentHandler = new GenerateOfflineDataParserNew(context,empNo);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("BlaseUsers"))
				{
					parsingScope   = PARSING_SCOPE_USERS;
					currentHandler = new GetAllUserParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Reasons"))
				{
					parsingScope   = PARSING_SCOPE_REASONS;
					currentHandler = new GetAllReasonsParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Locations"))
				{
					parsingScope   = PARSING_SCOPE_LOCATIONS;
					currentHandler = new RegionListParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Categories"))
				{
					parsingScope   = PARSING_SCOPE_CATEGORIES;
					currentHandler = new GetAllCategories(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Items"))
				{
					parsingScope   = PARSING_SCOPE_ITEMS;
					currentHandler = new GetAllItemsParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Banks"))
				{
					parsingScope   = PARSING_SCOPE_BANKS;
					currentHandler = new GetBanksParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("UOMFactors"))
				{
					parsingScope   = PARSING_SCOPE_UOM_FACTORS;
					currentHandler = new GetUOMFactorParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("Prices"))
				{
					parsingScope   = PARSING_SCOPE_PRICE;
					currentHandler = new GetPricingParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("AR_INV_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_AR_INV_METHODs;
					currentHandler = new GetARInvoiceParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("RECEIPT_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_RECEIPT_METHODs;
					currentHandler = new GetReceiptMethodParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("TRXTYPE_METHODs"))
				{
					parsingScope   = PARSING_SCOPE_TRXTYPE_METHODs;
					currentHandler = new GetTRXTypeMethodsParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("FromSubInventories"))
				{
					parsingScope   = PARSING_SCOPE_From_SubInventories;
					currentHandler = new GetSubInventoriesParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("FromSettings"))
				{
					parsingScope   = PARSING_SCOPE_SETTINGS;
					currentHandler = new GetSettingsParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				else if (localName.equalsIgnoreCase("PendingSalesInvoices"))
				{
					parsingScope   = PARSING_SCOPE_PENDING_INVOICES;
					currentHandler = new CustomerPendingInvoiceParser(context);
					LogUtils.debug("startElement", ""+localName);
				}
				///AppActiveStatusResponse
				else if (localName.equalsIgnoreCase("AppActiveStatusResponse"))
				{
					parsingScope   = PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE;
					currentHandler = new GetAllMovements(context,empNo);
					LogUtils.debug("startElement", ""+localName);
				}

				//==============================Added For TAX=========================================

				else if (localName.equalsIgnoreCase("Taxes"))
				{
					parsingScope 	=	PARSING_SCOPE_Tax;
					currentHandler = new TaxParser(context);
				}
				else if (localName.equalsIgnoreCase("TaxGroups"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxGroup;
					currentHandler	= new TaxGroupParser(context);
				}
				else if (localName.equalsIgnoreCase("TaxGroupTaxes"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxGroupTaxes;
					currentHandler 	= new TaxGroupTaxesParser(context);
				}
				else if (localName.equalsIgnoreCase("TaxSkuMaps"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxSkuMap;
					currentHandler	=	new TaxSkuMapParser(context);
				}
				else if (localName.equalsIgnoreCase("TaxSlabs"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxSlab;
					currentHandler 	= 	new TaxSlabParser(context);
				}
				else if (localName.equalsIgnoreCase("Orgs"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxOrg;
					currentHandler 	= 	new OrgParser(context);
				}
				else if (localName.equalsIgnoreCase("SalesOrderTaxs"))
				{
					parsingScope 	=	PARSING_SCOPE_TaxSalesOrderTax;
					currentHandler 	= 	new SalesOrderTaxParser(context);
				}
				else if (localName.equalsIgnoreCase("SalesOrganizations"))
				{
					parsingScope 	=	PARSING_SCOPE_SalesOrganization;
					currentHandler 	= 	new SalesOrganizationParser(context);
				}

				if(currentHandler != null){
					currentHandler.startElement(uri, localName, qName, attributes);
					updateMessage(localName);					
				}
			}
			break;
			
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_VEHICLES): 
			case (PARSING_SCOPE_USERS): 
			case (PARSING_SCOPE_REASONS): 
			case (PARSING_SCOPE_CATEGORIES): 
			case (PARSING_SCOPE_ITEMS): 
			case (PARSING_SCOPE_PRICE): 
			case (PARSING_SCOPE_UOM_FACTORS):
			case (PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE):
			case (PARSING_SCOPE_OFFLINE_DATA_RESPONSE):
			case (PARSING_SCOPE_LOCATIONS): 
			case (PARSING_SCOPE_CUSTOMERS):
			case (PARSING_SCOPE_DAILY_JOURNEY_PLANS):
			case (PARSING_SCOPE_DISCOUNTS): 
			case (PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE): 
			case (PARSING_SCOPE_CUSTOMER_RESPONSE): 
			case (PARSING_SCOPE_DAPASSCODE_RESPONSE): 
			case (PARSING_SCOPE_PENDING_INVOICES): 
			case (PARSING_SCOPE_AR_INV_METHODs):
			case (PARSING_SCOPE_RECEIPT_METHODs):	
			case (PARSING_SCOPE_TRXTYPE_METHODs):	
			case (PARSING_SCOPE_From_SubInventories):
				//=====================Added For TAX===========
			case (PARSING_SCOPE_TaxOrg):
			case (PARSING_SCOPE_TaxSalesOrderTax):
			case (PARSING_SCOPE_SalesOrganization):
			case (PARSING_SCOPE_TaxGroup):
			case (PARSING_SCOPE_TaxGroupTaxes):
			case (PARSING_SCOPE_TaxSkuMap):
			case (PARSING_SCOPE_TaxSlab):
			case (PARSING_SCOPE_Tax):
			{
				currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			sBuffer = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_SETTINGS):
			case (PARSING_SCOPE_VEHICLES): 
			case (PARSING_SCOPE_USERS): 
			case (PARSING_SCOPE_REASONS): 
			case (PARSING_SCOPE_CATEGORIES): 
			case (PARSING_SCOPE_ITEMS): 
			case (PARSING_SCOPE_PRICE): 
			case (PARSING_SCOPE_UOM_FACTORS):
			case (PARSING_SCOPE_AR_INV_METHODs):
			case (PARSING_SCOPE_RECEIPT_METHODs):
			case (PARSING_SCOPE_TRXTYPE_METHODs):
			case (PARSING_SCOPE_From_SubInventories):
			case (PARSING_SCOPE_APP_ACTIVE_STATUS_RESPONSE):
			case (PARSING_SCOPE_OFFLINE_DATA_RESPONSE):
			case (PARSING_SCOPE_LOCATIONS): 
			case (PARSING_SCOPE_CUSTOMERS):
			case (PARSING_SCOPE_DAILY_JOURNEY_PLANS):
			case (PARSING_SCOPE_DISCOUNTS): 
			case (PARSING_SCOPE_TRANSACTION_HEADER_RESPONSE): 
			case (PARSING_SCOPE_CUSTOMER_RESPONSE): 
			case (PARSING_SCOPE_DAPASSCODE_RESPONSE): 
			case (PARSING_SCOPE_PENDING_INVOICES):
				//======================Added For VAT==================
			case (PARSING_SCOPE_TaxOrg):
			case (PARSING_SCOPE_TaxSalesOrderTax):
			case (PARSING_SCOPE_SalesOrganization):
			case (PARSING_SCOPE_TaxGroup):
			case (PARSING_SCOPE_TaxGroupTaxes):
			case (PARSING_SCOPE_TaxSkuMap):
			case (PARSING_SCOPE_TaxSlab):
			case (PARSING_SCOPE_Tax):
			{
				currentHandler.currentValue = sBuffer;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("Banks")||
				   localName.equalsIgnoreCase("Vehicles")||
				   localName.equalsIgnoreCase("BlaseUsers") ||
				   localName.equalsIgnoreCase("Reasons") ||
				   localName.equalsIgnoreCase("Categories") ||
				   localName.equalsIgnoreCase("Items") ||
				   localName.equalsIgnoreCase("Price") ||
				   localName.equalsIgnoreCase("UOMFactors") ||
				   localName.equalsIgnoreCase("AR_INV_METHODs") ||
				   localName.equalsIgnoreCase("RECEIPT_METHODs") ||
				   localName.equalsIgnoreCase("TRXTYPE_METHODs") ||
				   localName.equalsIgnoreCase("FromSubInventories") ||
				   localName.equalsIgnoreCase("AppActiveStatusResponse") ||
				   localName.equalsIgnoreCase("OfflineDataResponse") ||
				   localName.equalsIgnoreCase("Locations")||
				   localName.equalsIgnoreCase("objCustomerResponse")||
				   localName.equalsIgnoreCase("objJourneyPlanResponse")||
				   localName.equalsIgnoreCase("objDiscountResponse")||
				   localName.equalsIgnoreCase("objTrxHeaderResponse")||
				   localName.equalsIgnoreCase("objDAPassCodeResponse")||
				   localName.equalsIgnoreCase("FromSettings")||
						//============Added For TAX===================
						localName.equalsIgnoreCase("Taxes")||
						localName.equalsIgnoreCase("Orgs")||
						localName.equalsIgnoreCase("SalesOrderTaxs")||
						localName.equalsIgnoreCase("SalesOrganizations")||
						localName.equalsIgnoreCase("TaxGroups")||
						localName.equalsIgnoreCase("TaxSkuMaps")||
						localName.equalsIgnoreCase("TaxSlabs")||
						localName.equalsIgnoreCase("TaxGroupTaxes")||
				   localName.equalsIgnoreCase("PendingSalesInvoices"))
				{
					LogUtils.errorLog("localName", localName+" Completed");
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
					
					if(syncProcessListner!=null){
						SYNCED_MAIN_MODULES++;
						updateMessage(localName);
					}
				}
			}
			break;
			
			case (PARSING_SCOPE_SERVERTIME):
			{
				LogUtils.debug("loadIncrementalData", "serverTimeCount = > "+serverTimeCount);
				if(localName.equalsIgnoreCase("ServerTime"))
				{
					syncLogDO.TimeStamp = sBuffer.toString();
					parsingScope = PARSING_SCOPE_INVALID;
					if(serverTimeCount==0){
						preference.saveStringInPreference(ServiceURLs.GetAllDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						LogUtils.debug("loadIncrementalData", "syncLogDO.TimeStamp = > "+syncLogDO.TimeStamp);
					}
				}
				if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					syncLogDO.UPMJ = sBuffer.toString();
					parsingScope = PARSING_SCOPE_INVALID;
					if(serverTimeCount==0){
						preference.saveStringInPreference(ServiceURLs.GetAllDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
						LogUtils.debug("loadIncrementalData", "syncLogDO.UPMJ = > "+syncLogDO.UPMJ);
					}
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					syncLogDO.action =  currentValue.toString();
				}
				if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					syncLogDO.UPMT = sBuffer.toString();
					parsingScope = PARSING_SCOPE_INVALID;
					syncLogDO.entity = ServiceURLs.GetAllDataSync;//please don't change this entity
					if(serverTimeCount==0)
					{
						syncCompleteLogDO.entity = ServiceURLs.GetAllDataSync;
						syncCompleteLogDO.UPMJ = syncLogDO.UPMJ;
						syncCompleteLogDO.UPMT = syncLogDO.UPMT;
						preference.saveStringInPreference(ServiceURLs.GetAllDataSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
					}
					serverTimeCount++;
				}
			}
			break;
			
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("GetAllDataSyncResponse"))
				{
					if(syncProcessListner!=null){
						SYNCED_MAIN_MODULES=(int) TOTAL_MAIN_MODULES;
						int percentage=(int) ((SYNCED_MAIN_MODULES/TOTAL_MAIN_MODULES)*100);
						String msg="Syncing Data..."+percentage+"%";
						syncProcessListner.progress(msg);
					}
					preference.saveIntInPreference(Preference.SYNC_STATUS, 1);
					preference.commitPreference();
					new SynLogDA().insertSynchLog(syncCompleteLogDO);
					
				}
			}
			break;
		}
	}

	
	private void updateMessage(String localName)
	{
		int percentage=(int) ((SYNCED_MAIN_MODULES/TOTAL_MAIN_MODULES)*100);
		if(percentage<=100){
			String msg="Syncing "+localName+" "+percentage+"%";
			syncProcessListner.progress(msg);
		}
	}
	
	public void characters(char[] ch, int start, int length) 
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
