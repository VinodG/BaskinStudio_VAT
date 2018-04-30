package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetDA_New;

public class AssetStatusListParser extends BaseHandler
{
//	private AssetStatusDO assetStatusDO;
	private String AssetId;
	
	public AssetStatusListParser(Context context) 
	{
		super(context);
		preference  = new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("PostAssetCategoryResult"))
		{
			AssetId = currentValue.toString();
			updateAssetStatus(AssetId);
		}
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public String getAssetId()
	{
		return AssetId;
	}
	
	private void updateAssetStatus(String AssetId)
	{
		new AssetDA_New().updateAssetStatus(AssetId);
	}
}
