package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.PricingDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetPricingParser extends BaseHandler
{
	private Vector<PricingDO> vecItemPricing;
	private PricingDO pricingDO;
	private SynLogDO synLogDO = new SynLogDO();
	private CommonDA commonDA;
	public GetPricingParser(Context context)
	{
		super(context);
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Prices"))
		{
			commonDA = new CommonDA();
			vecItemPricing = new Vector<PricingDO>();
		}
		else if(localName.equalsIgnoreCase("PriceDco"))
		{
			pricingDO = new PricingDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("ITEMCODE"))
			pricingDO.itemCode = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("CUSTOMERPRICINGKEY"))
			pricingDO.customerPricingClass = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("PRICECASES"))
			pricingDO.priceCases = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ENDDATE"))
			pricingDO.endDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("STARTDATE"))
			pricingDO.startDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("DISCOUNT"))
			pricingDO.dicount = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("IsExpired"))
		{
			if(currentValue.toString().equalsIgnoreCase("false"))
				pricingDO.IsExpired = "False";
			else
				pricingDO.IsExpired = "True";
		}
		
		else if(localName.equalsIgnoreCase("DepositPrice"))
			pricingDO.emptyCasePrice = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("TaxGroupCode"))
			pricingDO.TaxGroupCode = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("TaxPercentage"))
			pricingDO.TaxPercentage = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedDate"))
			pricingDO.ModifiedDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedTime"))
			pricingDO.ModifiedTime = currentValue.toString();
	
		else if(localName.equalsIgnoreCase("UOM"))
			pricingDO.UOM = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("PriceDco")){
			
			vecItemPricing.add(pricingDO);
			if(vecItemPricing.size()>AppConstants.SYNC_COUNT){
				commonDA.insertItemPricing(vecItemPricing);
				LogUtils.errorLog("PriceDco",""+vecItemPricing.size());
				vecItemPricing.clear();
			}
		}
		
		else if(localName.equalsIgnoreCase("Prices") && vecItemPricing != null)
		{
			if(commonDA.insertItemPricing(vecItemPricing))
			{
				synLogDO.entity = ServiceURLs.GET_All_PRICE_WITH_SYNC;
				new SynLogDA().insertSynchLog(synLogDO);
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
