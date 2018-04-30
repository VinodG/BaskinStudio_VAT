package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetServiceDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetServiceDO;

public class PostAssetServiceRequestParser extends BaseHandler
{
	private Vector<AssetServiceDO> vecAssetser;
	private AssetServiceDO assetserDo;
	private boolean isAssetservice = false , isSurveyResult = false;
	private String Status,message,count;
	
	
	
	public PostAssetServiceRequestParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("PostAssetServiceRequestResult"))
		{
			vecAssetser = new Vector<AssetServiceDO>();
		}
		else if(localName.equalsIgnoreCase("AssetServiceRequestStatusDco"))
		{
			assetserDo = new AssetServiceDO();
			isAssetservice =true;
			
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		 if(localName.equalsIgnoreCase("AssetServiceRequestId")) 
		{
			 assetserDo.assetServiceRequestId = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("Status"))
		{
			if(isAssetservice)
				assetserDo.status = currentValue.toString();
			else
			{
				Status = currentValue.toString();
			}
				
		}

		else if(localName.equalsIgnoreCase("AssetServiceRequestStatusDco"))
		{
			vecAssetser.add(assetserDo);
		}
		else if(localName.equalsIgnoreCase("SurveyStatusList"))
		{
			updateDataBase(vecAssetser);
		}
		 
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private void updateDataBase(Vector<AssetServiceDO> vecAssetser2) 
	{
		
		new AssetServiceDA().updateAssetService(vecAssetser2);
	}
	
	
}
