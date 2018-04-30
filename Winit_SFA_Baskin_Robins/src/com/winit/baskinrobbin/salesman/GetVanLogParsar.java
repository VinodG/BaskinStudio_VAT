package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.parsers.BaseHandler;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.dataobject.VanStockDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetVanLogParsar extends BaseHandler
{
	private final int TYPE_VAN_LOAD = 3;
	private int PARSE_TYPE = -1;
	private Vector<VanStockDO> vecVanStockDOs;
	private VanStockDO vanStockDO;
	private SynLogDO synLogDO = new SynLogDO();
	private boolean considerSyncTime=true;
	
	public GetVanLogParsar(Context context) 
	{
		super(context);
	}
	
	public boolean getStatus()
	{
		if(synLogDO.action.equalsIgnoreCase("true"))
			return true;
		else
			return false;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("VanStockLogs"))
		{
			considerSyncTime=false;
			PARSE_TYPE = TYPE_VAN_LOAD;
			vecVanStockDOs = new Vector<VanStockDO>();
		}
		
		else if(localName.equalsIgnoreCase("VanStockLogDco"))
			vanStockDO = new VanStockDO();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			if(considerSyncTime)
				synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			if(considerSyncTime)
				synLogDO.UPMT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VanStockLogs"))
		{
			insertMovementDetailsDetail();
		}
		
		switch (PARSE_TYPE) 
		{
			case TYPE_VAN_LOAD:
				
				if(localName.equalsIgnoreCase("VanStockLogId"))
					vanStockDO.VanStockId = StringUtils.getInt(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("OrgCode"))
					vanStockDO.OrgCode = currentValue.toString();
				
//				else if(localName.equalsIgnoreCase("TrxCode"))
//					vanStockDO.TrxCode = currentValue.toString();
//				
//				else if(localName.equalsIgnoreCase("TrxType"))
//					vanStockDO.TrxType = currentValue.toString();
//				
//				else if(localName.equalsIgnoreCase("TrxDate"))
//					vanStockDO.TrxDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemCode"))
					vanStockDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					vanStockDO.UserCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("BatchNumber"))
					vanStockDO.BatchNumber = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("QuantityEach"))
					vanStockDO.QuantityEach = StringUtils.getInt(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("ExpiryDate"))
					vanStockDO.ExpiryDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ModifiedDate"))
					vanStockDO.ModifiedDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ModifiedTime"))
					vanStockDO.ModifiedTime = currentValue.toString();
				
//				else if(localName.equalsIgnoreCase("VehicleCode"))
//					vanStockDO.VehicleCode =  currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VanStockLogDco"))
					vecVanStockDOs.add(vanStockDO);
				
				break;
				
			default:
				break;
		}
	}
	
	private void insertMovementDetailsDetail()
	{
		InventoryDA inventoryDA = new InventoryDA();
		if(vecVanStockDOs != null && vecVanStockDOs.size() > 0 
				&& inventoryDA.insertVanLoad(vecVanStockDOs))
		{
			synLogDO.entity = ServiceURLs.GetVanStockLogDetail;
			new SynLogDA().insertSynchLog(synLogDO);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
