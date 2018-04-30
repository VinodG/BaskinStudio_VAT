package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.UserJourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class UserJourneyPlanParser extends BaseHandler
{
	private UserJourneyPlanDO objUserJourneyPlan;
	private Vector<UserJourneyPlanDO> vecJourneyPlan;
	private CustomerDetailsDA objCustomerDetails;
	public UserJourneyPlanParser(Context context) 
	{
		super(context);
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentValue = new StringBuilder();
		currentElement  = true;
		if(localName.equalsIgnoreCase("Beats"))
		{
			vecJourneyPlan = new Vector<UserJourneyPlanDO>();
		}
		else if(localName.equalsIgnoreCase("BeatsDco"))
		{
			objUserJourneyPlan = new UserJourneyPlanDO();
		}
	}
	@Override
	public void endElement(String uri, String localName, String qName)	throws SAXException
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
		}
		else if(localName.equalsIgnoreCase("Site_Number"))
		{
			objUserJourneyPlan.strSiteNumber = currentValue.toString().toString();
		}
		else if(localName.equalsIgnoreCase("Type"))
		{
			objUserJourneyPlan.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SITE_NAME"))
		{
			objUserJourneyPlan.strCustomer = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesmanCode"))
		{
			objUserJourneyPlan.strSalesmancode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Stop"))
		{
			objUserJourneyPlan.strStop = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BeatDetails"))
		{
			objUserJourneyPlan.strRoutePlanDetails = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Cases"))
		{
			objUserJourneyPlan.strCases = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("KG"))
		{
			objUserJourneyPlan.strKG = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Size3"))
		{
			objUserJourneyPlan.strSize3 = currentValue.toString();	
		}
		else if(localName.equalsIgnoreCase("Distance"))
		{
			objUserJourneyPlan.strDistance = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TravelTime"))
		{
			objUserJourneyPlan.strTravelTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ArrivalTime"))
		{
			objUserJourneyPlan.strArrivalTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServiceTime"))
		{
			objUserJourneyPlan.strServiceTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PASSCODE"))
		{
			objUserJourneyPlan.strPassCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BeatsDco"))
		{
			vecJourneyPlan.add(objUserJourneyPlan);
		}
		
		else if(localName.equalsIgnoreCase("Beats"))
		{
			objCustomerDetails = new CustomerDetailsDA();
			if(vecJourneyPlan != null && vecJourneyPlan.size() > 0)
			{
				if(storeJourneyPlan(vecJourneyPlan))
					preference.commitPreference();
			}
		}
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	/**
	 * method to insert or update journey plan into table Journey plan
	 * @param vecJourneyPlan
	 */
	private boolean storeJourneyPlan(Vector<UserJourneyPlanDO> vecJourneyPlan)
	{
		//loop up to vector size
		return objCustomerDetails.insertJourneyPlan(vecJourneyPlan);
	}
}
