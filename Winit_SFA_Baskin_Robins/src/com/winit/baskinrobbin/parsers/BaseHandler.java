package com.winit.baskinrobbin.parsers;

import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class BaseHandler extends DefaultHandler
{
	public StringBuilder currentValue;
	public boolean currentElement = false;
	
	public final static String apostrophe = "'";
	public final static String sep = ",";
	public Context context;
	public Preference preference;
	
	public BaseHandler(Context context)
	{
		this.context = context;
		preference = new Preference(context);
	}
	
	/**
	 * Method to convert StringBuffer to String.
	 * @param sb
	 * @return String
	 */
	public String sb2String(StringBuffer sb)
	{
		if(sb == null)
			return "";
		try
		{
			return sb.toString();
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2String exception:"+e.getMessage() );
		}
		return null;
	}
	
	/**
	 * Method to convert StringBuffer to Long.
	 * @param sb
	 * @return long
	 */
	public long sb2Long(StringBuffer sb)
	{
		if(sb == null)
			return 0;
		try
		{
			return Long.parseLong(sb.toString());
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2Long exception:"+e.getMessage() );
		}
		return 0;
	}
	
	/**
	 * Method to convert StringBuffer to double.
	 * @param sb
	 * @return double
	 */
	public double sb2Double(StringBuffer sb)
	{
		if(sb == null)
			return 0;
		try
		{
			return Double.parseDouble(sb.toString());
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2Long exception:"+e.getMessage() );
		}
		return 0;
	}
	
	/**
	 * Method to convert StringBuffer to boolean.
	 * @param sb
	 * @return boolean
	 */
	public boolean sb2Boolean(StringBuffer sb)
	{
		boolean result = false;
		
		if(sb == null)
			return result;
		
		if (sb.length() > 0)
		{
			try
			{
				result = sb.toString().equalsIgnoreCase("true");
			}
			catch(Exception e)
			{
		   		LogUtils.errorLog(this.getClass().getName(), "sb2Boolean exception:"+e.getMessage() );
			}
			
		}
		return result;
	}
}
