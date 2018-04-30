package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PasscodesDA;
import com.winit.baskinrobbin.salesman.dataobject.PresellerPassCodeDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class PresellerPassCodeParser extends BaseHandler
{
	private PresellerPassCodeDO objPresellerPassCode;
	private Vector<PresellerPassCodeDO> vecPresellerPassCodeDOs;
	
	public PresellerPassCodeParser(Context context) 
	{
		super(context);
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("PresellerPassCodes"))
		  vecPresellerPassCodeDOs = new Vector<PresellerPassCodeDO>();
		else if(localName.equalsIgnoreCase("PresellerPassCodeDco"))
			objPresellerPassCode = new PresellerPassCodeDO();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		currentElement = false;
		
		if(localName.equalsIgnoreCase("PreSellerPassCodeId"))
			objPresellerPassCode.presellerPasscodeId = StringUtils.getInt(currentValue.toString());
		else if(localName.equalsIgnoreCase("PreSellerId"))
			objPresellerPassCode.presellerId = currentValue.toString();
		else if(localName.equalsIgnoreCase("PassCode"))
			objPresellerPassCode.passCode = StringUtils.getInt(currentValue.toString());
		else if(localName.equalsIgnoreCase("PresellerPassCodeDco"))
		{
			objPresellerPassCode.isUsed = false;
			vecPresellerPassCodeDOs.add(objPresellerPassCode);
		}
		else if(localName.equalsIgnoreCase("PresellerPassCodes"))
		{
			LogUtils.errorLog("vecPresellerPassCodeDOs", "vecPresellerPassCodeDOs - "+vecPresellerPassCodeDOs.size());
			PasscodesDA presellerDA = new PasscodesDA();
			boolean isInserted = presellerDA.inserPresellerPasscode(vecPresellerPassCodeDOs);
			if(isInserted)
			{
				preference.saveStringInPreference(Preference.PASSCODE_SYNC, CalendarUtils.getSyncDateFormat());
				preference.commitPreference();
			}
		}
	}
	
	public Vector<PresellerPassCodeDO> getPresellerCode()
	{
		return vecPresellerPassCodeDOs;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
