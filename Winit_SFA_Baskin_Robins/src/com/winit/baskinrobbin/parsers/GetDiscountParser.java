package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.DiscountDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.DiscountMasterDO;
import com.winit.baskinrobbin.salesman.dataobject.DiscountPromoDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetDiscountParser extends BaseHandler
{
	private String status = "";
	private DiscountMasterDO objDiscountMasterDO;
	private Vector<DiscountMasterDO> vecDiscountMasterDOs;
	private DiscountPromoDO objDiscountPromoDO;
	private Vector<DiscountPromoDO> vecDiscountPromoDOs;
	SynLogDO synLogDO = new SynLogDO();
	private int PARSING_SCOPE = -1;
	private final int PARSING_SCOPE_DISCOUNT_MASTER  = 1;
	private final int PARSING_SCOPE_DISCOUNT_DETAILS = 2;
	int count=0;
	public GetDiscountParser(Context context) 
	{
		super(context); 
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("discounts"))
		{
			PARSING_SCOPE = PARSING_SCOPE_DISCOUNT_MASTER;
			vecDiscountMasterDOs = new Vector<DiscountMasterDO>();
		}
		else if(localName.equalsIgnoreCase("DiscountDco"))
		{
			objDiscountMasterDO = new DiscountMasterDO();
		}
		else if(localName.equalsIgnoreCase("objDiscountDetailDcos"))
		{
			PARSING_SCOPE = PARSING_SCOPE_DISCOUNT_DETAILS;
			vecDiscountPromoDOs = new Vector<DiscountPromoDO>();
		}
		else if(localName.equalsIgnoreCase("DiscountDetailDco"))
		{
			objDiscountPromoDO = new DiscountPromoDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("ServerDateTime"))
		{
			synLogDO.TimeStamp  = currentValue.toString();;
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ  = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT  = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GetDiscountsResult"))
		{
			synLogDO.entity = ServiceURLs.GET_DISCOUNTS;
			new SynLogDA().insertSynchLog(synLogDO);
			
		}
		switch (PARSING_SCOPE) 
		{
			case PARSING_SCOPE_DISCOUNT_MASTER:
				if(localName.equalsIgnoreCase("DiscountId"))
				{
					objDiscountMasterDO.DiscountId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("SiteNumber"))
				{
					objDiscountMasterDO.Site_Number = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Level"))
				{
					objDiscountMasterDO.Level = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Code"))
				{
					objDiscountMasterDO.Code = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DiscountType"))
				{
					objDiscountMasterDO.DiscountType = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Discount"))
				{
					objDiscountMasterDO.Discount = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("UOM"))
				{
					objDiscountMasterDO.UOM = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MinQty"))
				{
					objDiscountMasterDO.MinQty = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MaxQty"))
				{
					objDiscountMasterDO.MaxQty = currentValue.toString();
				}
				
				else if(localName.equalsIgnoreCase("Name"))
				{
					objDiscountMasterDO.Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Description"))
				{
					objDiscountMasterDO.Description = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Discount_Modifier"))
				{
					objDiscountMasterDO.Discount_Modifier = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DiscountDco"))
				{
					vecDiscountMasterDOs.add(objDiscountMasterDO);
					if(vecDiscountMasterDOs.size()>AppConstants.SYNC_COUNT)
					{
						count+=+vecDiscountMasterDOs.size();
						insertIntoDatebase(vecDiscountMasterDOs);
						Log.e("DiscountDco", ""+count);
						LogUtils.errorLog("DiscountDco",""+vecDiscountMasterDOs.size());
						vecDiscountMasterDOs.clear();
					}
				}
				else if(localName.equalsIgnoreCase("discounts"))
				{
					
					insertIntoDatebase(vecDiscountMasterDOs);
				}
				break;
			case PARSING_SCOPE_DISCOUNT_DETAILS:
				if(localName.equalsIgnoreCase("DiscountId"))
				{
					objDiscountPromoDO.DiscountId  = currentValue.toString();;
				}
				else if(localName.equalsIgnoreCase("SiteNumber"))
				{
					objDiscountPromoDO.SiteNumber = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Level"))
				{
					objDiscountPromoDO.Level  = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Code"))
				{
					objDiscountPromoDO.Code  = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DiscountType"))
				{
					objDiscountPromoDO.DiscountType = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Discount"))
				{
					objDiscountPromoDO.Discount = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("UOM"))
				{
					objDiscountPromoDO.UOM = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MinQty"))
				{
					objDiscountPromoDO.MinQty = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MaxQty"))
				{
					objDiscountPromoDO.MaxQty = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Name"))
				{
					objDiscountPromoDO.Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Description"))
				{
					objDiscountPromoDO.Description = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Discount_Modifier"))
				{
					objDiscountPromoDO.Discount_Modifier = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DiscountDetailDco"))
				{
					vecDiscountPromoDOs.add(objDiscountPromoDO);
					if(vecDiscountPromoDOs.size()>AppConstants.SYNC_COUNT)
					{
						insertPromDissIntoDatebase(vecDiscountPromoDOs);
						LogUtils.errorLog("vecDiscountPromoDOs", vecDiscountPromoDOs.size()+"");
						vecDiscountPromoDOs.clear();
					}
				}
				else if(localName.equalsIgnoreCase("objDiscountDetailDcos"))
				{
					insertPromDissIntoDatebase(vecDiscountPromoDOs);
				}
				break;
			default:
				break;
				
		}
	}
	
	private boolean insertIntoDatebase(Vector<DiscountMasterDO> vec) 
	{
		if(vec != null && vec.size() > 0)
			return new DiscountDA().insertDiscounts(vec);
		
		return false;
	}
	
	private boolean insertPromDissIntoDatebase(Vector<DiscountPromoDO> vec) 
	{
		if(vec != null && vec.size() > 0)
		{
			LogUtils.errorLog("DiscountDetailDco",""+vecDiscountPromoDOs.size());
			return new DiscountDA().insertPromoDiscounts(vec);
		}
		
		return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getStatus()
	{
		if(!status.equalsIgnoreCase("Success"))
			return false;
		
		return true;
	}
}
