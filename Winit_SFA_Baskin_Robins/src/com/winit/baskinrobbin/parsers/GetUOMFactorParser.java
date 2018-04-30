package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class GetUOMFactorParser extends BaseHandler
{
	private Vector<NameIDDo> vecUOMFactor ;
	private NameIDDo objNameIDDo;
	
	public GetUOMFactorParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("UOMFactors"))
		{
			vecUOMFactor = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("UOMFactorDco"))
		{
			objNameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ItemCode"))
		{
			objNameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			objNameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Factor"))
		{
			objNameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOMFactorDco"))
		{
			vecUOMFactor.add(objNameIDDo);
			if(vecUOMFactor.size()>AppConstants.SYNC_COUNT){
				insertUOMFactors(vecUOMFactor);
				LogUtils.errorLog("UOMFactorDco",""+vecUOMFactor.size());
				vecUOMFactor.clear();
			}
		}
		else if(localName.equalsIgnoreCase("UOMFactors"))
		{
			if(vecUOMFactor != null && vecUOMFactor.size() > 0)
				insertUOMFactors(vecUOMFactor);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertUOMFactors(Vector<NameIDDo> vecUOMFactor) 
	{
		new CommonDA().insertUOMFactorDetails(vecUOMFactor);
	}
}
