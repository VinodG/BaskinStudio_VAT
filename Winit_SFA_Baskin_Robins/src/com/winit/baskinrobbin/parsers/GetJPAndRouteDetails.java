package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyDO;
import com.winit.baskinrobbin.salesman.dataobject.RouteClientDO;
import com.winit.baskinrobbin.salesman.dataobject.RouteDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetJPAndRouteDetails extends BaseHandler 
{
	private Vector<RouteDO> vecsRouteDO;
	private RouteDO objRouteDO;
	
	private Vector<JourneyDO> vecsJourneyPlanDO;
	private JourneyDO objJourneyPlanDO;
	
	private Vector<RouteClientDO> vecsRouteClientDO;
	private RouteClientDO objRouteClientDO;
	
	
	private final int Route= 2, JourneyPlan= 4,RouteClient= 5;

	private int SELECTED_TYPE = 0;
	private String empNo;
	SynLogDO synLogDO = new SynLogDO();
	public GetJPAndRouteDetails(Context context, String strEmpNo) 
	{
		super(context); 
		this.empNo = strEmpNo;
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("Routes"))
		{
			vecsRouteDO = new Vector<RouteDO>();
		}
		else if(localName.equalsIgnoreCase("RouteDco"))
		{
			SELECTED_TYPE = Route;
			objRouteDO = new RouteDO();
		}
		else if(localName.equalsIgnoreCase("JourneyPlans"))
		{
			vecsJourneyPlanDO = new Vector<JourneyDO>();
		}
		else if(localName.equalsIgnoreCase("JourneyPlanDco"))
		{
			SELECTED_TYPE = JourneyPlan;
			objJourneyPlanDO = new JourneyDO();
		}
		
		else if(localName.equalsIgnoreCase("RouteClients"))
		{
			vecsRouteClientDO = new Vector<RouteClientDO>();
		}
		else if(localName.equalsIgnoreCase("RouteClientDco"))
		{
			SELECTED_TYPE = RouteClient;
			objRouteClientDO = new RouteClientDO();
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ServerTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
//			preference.saveStringInPreference(Preference.GetSurveyMasters , currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedDate")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMJ =  currentValue.toString();
//			preference.saveStringInPreference(Preference.LSD , currentValue.toString().trim());
		}
		else if(localName.equalsIgnoreCase("ModifiedTime")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMT =  currentValue.toString();
//			preference.saveStringInPreference(Preference.LST , currentValue.toString().trim());
		}
		else if(localName.equalsIgnoreCase("Status")  && SELECTED_TYPE <= 0)
		{
			synLogDO.action =  currentValue.toString();
//			preference.saveStringInPreference(Preference.LST , currentValue.toString().trim());
		}
		else if(localName.equalsIgnoreCase("GetJPAndRouteDetailsResult"))
		{
			insertJPAndRouteDetailsData( vecsRouteDO, vecsJourneyPlanDO, vecsRouteClientDO);
//				preference.commitPreference();
			synLogDO.entity = ServiceURLs.GetJPAndRouteDetails;
			new SynLogDA().insertSynchLog(synLogDO);
		}
		switch (SELECTED_TYPE)
		{
			case Route:
				if(localName.equalsIgnoreCase("RouteId"))
				{
					objRouteDO.routeId = Integer.parseInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("Name"))
				{
					objRouteDO.Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Description"))
				{
					objRouteDO.Description = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("JourneyPlanId"))
				{
					objRouteDO.JourneyPlanId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DayNumber"))
				{
//					objRouteDO. = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("StartDay"))
				{
					objRouteDO.StartDay = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("RouteDco"))
				{
					vecsRouteDO.add(objRouteDO);
				}
				break;
			case JourneyPlan:
				
				if(localName.equalsIgnoreCase("JourneyPlanId"))
				{
					objJourneyPlanDO.journeyPlanId = Integer.parseInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("UserCode"))
				{
					objJourneyPlanDO.UserCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Name"))
				{
					objJourneyPlanDO.Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Description"))
				{
					objJourneyPlanDO.Description = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("StartDate"))
				{
					objJourneyPlanDO.StartDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("LifeCycle"))
				{
					objJourneyPlanDO.LifeCycle = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("EndDate"))
				{
					objJourneyPlanDO.EndDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					objJourneyPlanDO.Status = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("JourneyPlanDco"))
				{
					vecsJourneyPlanDO.add(objJourneyPlanDO);
				}
				break;
				
		case RouteClient:
				
				if(localName.equalsIgnoreCase("RouteClientId"))
				{
					objRouteClientDO.RouteClientId = Integer.parseInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("RouteId"))
				{
					objRouteClientDO.RouteId =Integer.parseInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("ClientCode"))
				{
					objRouteClientDO.ClientCode =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TimeIn"))
				{
					objRouteClientDO.TimeIn =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TimeOut"))
				{
					objRouteClientDO.TimeOut =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Sequence"))
				{
					objRouteClientDO.Sequence =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					objRouteClientDO.Status =currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("RouteClientDco"))
				{
					vecsRouteClientDO.add(objRouteClientDO);
				}
				break;
			default:
				break;
		}
	}
	
	private boolean insertJPAndRouteDetailsData(Vector<RouteDO> vecsRouteDO,Vector<JourneyDO> vecsJourneyPlanDO,Vector<RouteClientDO> vecsRouteClientDO) 
	{
		try 
		{
			if(vecsRouteDO!=null&&vecsRouteDO.size()>0)
				new JourneyPlanDA().insertRoute(vecsRouteDO);
			if(vecsJourneyPlanDO!=null&&vecsJourneyPlanDO.size()>0)
				new JourneyPlanDA().insertJourneyPlan(vecsJourneyPlanDO);
			if(vecsRouteClientDO!=null&&vecsRouteClientDO.size()>0)
				new JourneyPlanDA().insertRouteClient(vecsRouteClientDO);
		} 
		catch (Exception e) 
		{
			return true;
		}
		return false;
		
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	

}
