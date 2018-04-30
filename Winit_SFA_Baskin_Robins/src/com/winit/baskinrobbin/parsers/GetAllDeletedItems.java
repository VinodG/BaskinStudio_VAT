package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.DeleteAllItemsDA;
import com.winit.baskinrobbin.salesman.dataobject.SyncLogDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllDeletedItems extends BaseHandler{

	private Vector<SyncLogDO> vecSyncLogDOs;
	private SyncLogDO objSyncLogDO;
	
	public GetAllDeletedItems(Context context) {
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = true;
		currentValue=new StringBuilder();  
		
		if(localName.equalsIgnoreCase("SyncLogs"))
		{
			vecSyncLogDOs = new Vector<SyncLogDO>();
		}
		else if(localName.equalsIgnoreCase("SyncLogDco"))
		{
			objSyncLogDO = new SyncLogDO();
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentElement = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_DELETED_ITEMS+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SyncLogId"))
		{
			objSyncLogDO.syncLogId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Module"))
		{
			objSyncLogDO.module = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId"))
		{
			objSyncLogDO.entityId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId2"))
		{
			objSyncLogDO.entityId2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId3"))
		{
			objSyncLogDO.entityId3 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Action"))
		{
			objSyncLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LastModifiedOn"))
		{
			objSyncLogDO.lastModified = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerTime"))
		{
			objSyncLogDO.serverTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SyncLogDco"))
		{
			vecSyncLogDOs.add(objSyncLogDO);
		}
		else if(localName.equalsIgnoreCase("SyncLogs") && vecSyncLogDOs != null)
		{
			DeleteAllItemsDA allItemsDA = new DeleteAllItemsDA();
			if(vecSyncLogDOs!=null && vecSyncLogDOs.size()>0)
				allItemsDA.deleteEntitys(vecSyncLogDOs);
			preference.commitPreference();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
