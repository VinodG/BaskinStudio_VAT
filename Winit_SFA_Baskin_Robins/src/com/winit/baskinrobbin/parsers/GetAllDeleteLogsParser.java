package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.PromotionalItemsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.GetAllDeleteLogDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllDeleteLogsParser extends BaseHandler
{
	private GetAllDeleteLogDO getAllDeleteLogDO;
	private Vector<GetAllDeleteLogDO> vecAllDeleteLogDOs;
	SynLogDO synLogDO = new SynLogDO();
	public GetAllDeleteLogsParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("DeleteLogs"))
		{
			vecAllDeleteLogDOs = new Vector<GetAllDeleteLogDO>();
		}
		else if(localName.equalsIgnoreCase("DeleteLogDco"))
		{
			getAllDeleteLogDO = new GetAllDeleteLogDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
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
		else if(localName.equalsIgnoreCase("DeleteLogId"))
		{
		}
		else if(localName.equalsIgnoreCase("Module"))
		{
			getAllDeleteLogDO.Module = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId"))
		{
			getAllDeleteLogDO.EntityId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId2"))
		{
			getAllDeleteLogDO.EntityId2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId3"))
		{
			getAllDeleteLogDO.EntityId3 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Action"))
		{
			getAllDeleteLogDO.Action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DeleteLogDco"))
		{
			vecAllDeleteLogDOs.add(getAllDeleteLogDO);
		}
		else if(localName.equalsIgnoreCase("DeleteLogs"))
		{
			if( vecAllDeleteLogDOs != null && vecAllDeleteLogDOs.size() > 0)
			{
				if(deleteRecords(vecAllDeleteLogDOs))
				{
					preference.commitPreference();
					synLogDO.entity		= ServiceURLs.GetAllDeleteLogs;
					new SynLogDA().insertSynchLog(synLogDO);
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
	
	private boolean deleteRecords(Vector<GetAllDeleteLogDO> vecAllDeleteLogDOs2)
	{
		return new PromotionalItemsDA().deleteRecords(vecAllDeleteLogDOs);
	}
}
