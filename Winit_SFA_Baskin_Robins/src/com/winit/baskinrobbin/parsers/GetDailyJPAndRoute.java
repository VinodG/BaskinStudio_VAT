package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.RouteClientDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetDailyJPAndRoute extends BaseHandler 
{
	private Vector<RouteClientDO> vecsJourneyPlanDO;
	private RouteClientDO objRouteClientDO;
	private final int JourneyPlan= 4;
	private int SELECTED_TYPE = 0;
	SynLogDO synLogDO = new SynLogDO();
	
	public GetDailyJPAndRoute(Context context, String strEmpNo) 
	{
		super(context); 
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("DailyJourneyPlans"))
		{
			vecsJourneyPlanDO = new Vector<RouteClientDO>();
		}
		else if(localName.equalsIgnoreCase("DailyJourneyPlanDco"))
		{
			SELECTED_TYPE = JourneyPlan;
			objRouteClientDO = new RouteClientDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		 
		if(localName.equalsIgnoreCase("ServerDateTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMJ =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMT =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status")  && SELECTED_TYPE <= 0)
		{
			synLogDO.action =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DailyJourneyPlans"))
		{
			if(vecsJourneyPlanDO != null && vecsJourneyPlanDO.size() > 0)
			{
				if(new JourneyPlanDA().insertDailyRouteClient(vecsJourneyPlanDO))
				{
					synLogDO.entity = ServiceURLs.GetJPAndRouteDetails;
					new SynLogDA().insertSynchLog(synLogDO);
				}
			}
		}
		switch (SELECTED_TYPE)
		{
			case JourneyPlan:
				
				if(localName.equalsIgnoreCase("DailyJourneyPlanId"))
					objRouteClientDO.DailyJourneyPlanId =  currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					objRouteClientDO.UserCode =  currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ClientCode"))
					objRouteClientDO.ClientCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("JourneyDate"))
					objRouteClientDO.JourneyDate =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("RouteClientId"))
					objRouteClientDO.RouteClientId = StringUtils.getInt(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("StartTime"))
					objRouteClientDO.TimeIn =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("EndTime"))
					objRouteClientDO.TimeOut =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Sequence"))
					objRouteClientDO.Sequence =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VisitStatus"))
					objRouteClientDO.Status =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VisitCode"))
					objRouteClientDO.VisitCode =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("IsDeleted"))
					objRouteClientDO.IsDeleted =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ModifiedDate"))
					objRouteClientDO.ModifiedDate =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ModifiedTime"))
					objRouteClientDO.ModifiedTime =currentValue.toString();
				
				else if(localName.equalsIgnoreCase("DailyJourneyPlanDco"))
					vecsJourneyPlanDO.add(objRouteClientDO);
				
				break;
			default:
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	

}
