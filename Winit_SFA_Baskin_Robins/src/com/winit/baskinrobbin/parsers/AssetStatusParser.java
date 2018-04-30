package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetTrackingDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDetailDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDo;

public class AssetStatusParser extends BaseHandler
{
	private Vector<AssetTrackingDo> vecAssetTrackingDos;
	private AssetTrackingDo assetTrackingDo;
	private Vector<AssetTrackingDetailDo> vecAssetTrackingDetailDos;
	private AssetTrackingDetailDo assetTrackingDetailDo;
	private boolean isAssetTracking = false , isAssetTrackingDetail = false;
	public AssetStatusParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("PostAssetResult"))
		{
		}
		else if(localName.equalsIgnoreCase("AssetStatusList"))
		{
			vecAssetTrackingDos = new Vector<AssetTrackingDo>();
		}
		else if(localName.equalsIgnoreCase("AssetStatusDco"))
		{
			assetTrackingDo = new AssetTrackingDo();
			isAssetTracking = true;
		}
		else if(localName.equalsIgnoreCase("ObjAssetDetailsStatusDco"))
		{
			vecAssetTrackingDetailDos = new Vector<AssetTrackingDetailDo>();
		}
		else if(localName.equalsIgnoreCase("AssetDetailsStatusDco"))
		{
			assetTrackingDetailDo = new AssetTrackingDetailDo();
			isAssetTrackingDetail = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		 if(localName.equalsIgnoreCase("Status")) // check Later
		{
			 if(isAssetTracking && isAssetTrackingDetail)
				 assetTrackingDetailDo.status = currentValue.toString();
			 else if(isAssetTracking)
				 assetTrackingDo.status = currentValue.toString();
			 else
			 {
				 
			 }
		}
		else if(localName.equalsIgnoreCase("Message"))
		{
		}
		else if(localName.equalsIgnoreCase("Count"))
		{
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
		}
		else if(localName.equalsIgnoreCase("AssetId"))
		{
			assetTrackingDo.assetTrackingId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AssetTrackingAppId"))
		{
			assetTrackingDo.assetTrackingAppId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AssetTrackingDetailId"))
		{
			assetTrackingDetailDo.assetTrackingDetailId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AssetTrackingDetailAppId"))
		{
			assetTrackingDetailDo.assetTrackingDetailAppId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AssetDetailsStatusDco"))
		{
			vecAssetTrackingDetailDos.add(assetTrackingDetailDo);
			isAssetTrackingDetail = false;
		}
		else if(localName.equalsIgnoreCase("ObjAssetDetailsStatusDco"))
		{
			assetTrackingDo.vAssetTrackingDetailDos = vecAssetTrackingDetailDos;
		}
		else if(localName.equalsIgnoreCase("AssetStatusDco"))
		{
			vecAssetTrackingDos.add(assetTrackingDo);
			isAssetTracking = false;
		}
		else if(localName.equalsIgnoreCase("AssetStatusList"))
		{
			updateDataBase(vecAssetTrackingDos);
		}
		 
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private void updateDataBase(Vector<AssetTrackingDo> vecAssetTrackingDos) 
	{
		new AssetTrackingDA().updateTrackings(vecAssetTrackingDos);
	}
	
}
