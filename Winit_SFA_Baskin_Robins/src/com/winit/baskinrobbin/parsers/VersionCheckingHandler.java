package com.winit.baskinrobbin.parsers;


import org.xml.sax.Attributes;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataobject.CheckVersionDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class VersionCheckingHandler extends BaseHandler
{
	private final int PARSING_INACTIVE = -1;
	private final int PARSE_SERVICE_RESPONSE = -2;
	private int parsingScope = PARSING_INACTIVE;
	
	private StringBuffer sBuffer = null;
	
	private String errorMessage = "";
	private boolean executionStatus=false;
	private CheckVersionDO objCheckVersionDO;
	
	
	public VersionCheckingHandler(Context context) 
	{
		super(context);
	}
	
	public void characters(char[] ch, int start, int length) 
	{
		if (parsingScope == PARSING_INACTIVE || ch == null || length == 0 || sBuffer==null)
			return;

		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "XML ch[] appending exception:"+e.getMessage() );
		}
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
	{
		switch (parsingScope)
		{
			case PARSING_INACTIVE:
			{
				if (localName.equalsIgnoreCase("GetVersionDetailsResult"))
				{
					parsingScope = PARSE_SERVICE_RESPONSE;
					 objCheckVersionDO = new CheckVersionDO();
				}
			}
			break;
			
		}
		if (parsingScope != PARSING_INACTIVE)
			sBuffer = new StringBuffer();
	}
	
	public void endElement(String uri, String localName, String qName) 
	{
		switch (parsingScope)
		{
			case PARSE_SERVICE_RESPONSE: 
			{
				if (localName.equalsIgnoreCase("Status"))
				{
					objCheckVersionDO.StatusCode = StringUtils.getInt(sBuffer.toString());
				}
				else if (localName.equalsIgnoreCase("DeviceType"))
				{
					objCheckVersionDO.DeviceType = sb2String(sBuffer);
				}
				else if (localName.equalsIgnoreCase("VersionNo"))
				{
					objCheckVersionDO.VersionNo = StringUtils.getInt(sBuffer.toString());
				}
				
				 else if (localName.equalsIgnoreCase("VersionManagementId"))
				 {
					 objCheckVersionDO.VersionManagementId = StringUtils.getInt(sBuffer.toString());
				 }
				 else if (localName.equalsIgnoreCase("Description"))
				 {
					 objCheckVersionDO.Description = sb2String(sBuffer);
				 }
				 else if (localName.equalsIgnoreCase("APKFileName"))
				 {
					 objCheckVersionDO.APKFileName = sb2String(sBuffer);
				 }
				
				 else if (localName.equalsIgnoreCase("GetVersionDetailsResult"))
				 {
					parsingScope = PARSING_INACTIVE;
				 }
			}
			break;
		}
	}
	public Object getData() 
	{
		return objCheckVersionDO;
	}


}
