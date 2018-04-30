package com.winit.baskinrobbin.parsers;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

public class GetCustomersByUserIdParser extends BaseHandler
{
	private JourneyPlanDO objCustomer;
	private Vector<JourneyPlanDO> vecCustomerDOs;
	private SynLogDO synLogDO = new SynLogDO();
	public GetCustomersByUserIdParser(Context context, String strEmpNo) 
	{
		super(context); 
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue	= new StringBuilder();
		
		if(localName.equalsIgnoreCase("Customers"))
		{
			vecCustomerDOs = new Vector<JourneyPlanDO>();
		}
		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			objCustomer = new JourneyPlanDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerSiteId"))
		{
			objCustomer.site = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteName"))
		{
			objCustomer.siteName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerId"))
		{
			objCustomer.customerId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerName"))
		{
			objCustomer.partyName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address1"))
		{
			objCustomer.addresss1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address2"))
		{
			objCustomer.addresss2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address3"))
		{
			objCustomer.addresss3 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address4"))
		{
			objCustomer.addresss4 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("City"))
		{
			objCustomer.city = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Latitude"))
		{
			objCustomer.geoCodeX = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Longitude"))
		{
			objCustomer.geoCodeY = currentValue.toString();	
		}
		else if(localName.equalsIgnoreCase("CreditLimit"))
		{
			objCustomer.creditLimit = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("PaymentType"))
		{
			objCustomer.paymentType = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("PaymentTermCode"))
		{
			objCustomer.paymentTermCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("SubChannelCode"))
		{
			objCustomer.subChannelCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("ChannelCode"))
		{
			objCustomer.channelCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("RegionCode"))
		{
			objCustomer.regionCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("StoreGrowth"))
		{
			objCustomer.StoreGrowth = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO1"))
		{
			objCustomer.mobileNo1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO2"))
		{
			objCustomer.mobileNo2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Website"))
		{
			objCustomer.website = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerGrade"))
		{
			objCustomer.CustomerGrade = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LandmarkId"))
		{
			objCustomer.LandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesmanlandmarkId"))
		{
			objCustomer.SalesmanlandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Source"))
		{
			objCustomer.source = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryId"))
		{
			objCustomer.coutryCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ParentGroup"))
		{
			objCustomer.ParentGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerPostingGroup"))
		{
			objCustomer.CustomerPostingGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerCategory"))
		{
			objCustomer.customerCategory = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CustomerSubCategory"))
		{
			objCustomer.customerSubCategory = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CustomerGroupCode"))
		{
			objCustomer.customerGroupCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CurrencyCode"))
		{
			objCustomer.currencyCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("IsSchemeApplicable"))
		{
			if(currentValue != null && currentValue.toString().equalsIgnoreCase("true"))
				objCustomer.isSchemeAplicable = 1;		
			else
				objCustomer.isSchemeAplicable = 0;
		}
		else if(localName.equalsIgnoreCase("PriceList"))
		{
			objCustomer.priceList = currentValue.toString();			
		}
		else if(localName.equalsIgnoreCase("CustomerType"))
		{
			objCustomer.customerType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerStatus"))
		{
			objCustomer.custmerStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryCode"))
		{
			objCustomer.coutryCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Category"))
		{
			objCustomer.category = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PASSCODE"))
		{
			objCustomer.Passcode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Email"))
		{
			objCustomer.email = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ContactPersonName"))
		{
			objCustomer.contectPersonName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PhoneNumber"))
		{
			objCustomer.phoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppCustomerId"))
		{
			objCustomer.phoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PoNumber"))
		{
			objCustomer.poNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesPerson"))
		{
			objCustomer.salesmanCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Order_Type_Id"))
		{
			objCustomer.Order_Type_Id = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Order_Type_Name"))
		{
			objCustomer.Order_Type_Name = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CREDIT_LEVEL"))
		{
			objCustomer.CREDIT_LEVEL = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesPersonName"))
		{
			objCustomer.salesmanName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesPersonMobileNumber"))
		{
			objCustomer.SalesPersonMobileNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Max_Days_Past_Due"))
		{
			objCustomer.Max_Days_Past_Due = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsOverCredit"))
		{
			objCustomer.IsOverCredit = StringUtils.getBoolean(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("VatNumber"))
		{
			objCustomer.VatNumber = currentValue.toString();
		}
		//=============================================
		else if(localName.equalsIgnoreCase("SiteNameInArabic"))
		{
			objCustomer.SiteNameInArabic = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address1_AR"))
		{
			objCustomer.Address1_AR = currentValue.toString();

		}
		else if(localName.equalsIgnoreCase("Address2_AR"))
		{
			objCustomer.Address2_AR = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address3_AR"))
		{
			objCustomer.Address3_AR = currentValue.toString();

		}
		else if(localName.equalsIgnoreCase("AreaName_AR"))
		{
			objCustomer.AreaName_AR = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PostalCode_AR"))
		{
			objCustomer.PostalCode_AR = currentValue.toString();

		}
		else if(localName.equalsIgnoreCase("LocationName_AR"))
		{
			objCustomer.LocationName_AR = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("City_AR"))
		{
			objCustomer.City_AR = currentValue.toString();

		}
		//====================================================
		else if(localName.equalsIgnoreCase("IsActive"))
		{
			objCustomer.isActive = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			vecCustomerDOs.add(objCustomer);
			if(vecCustomerDOs.size()>AppConstants.SYNC_COUNT)
			{
				insertCustomer(vecCustomerDOs);
				LogUtils.errorLog("CustomerDco",""+vecCustomerDOs.size());
				vecCustomerDOs.clear();
			}
		}
		else if(localName.equalsIgnoreCase("Customers"))
		{
			insertCustomer(vecCustomerDOs) ;
		}
	}
	
	private boolean insertCustomer(Vector<JourneyPlanDO> vecCustomerDOs) 
	{
		synLogDO.entity = ServiceURLs.GET_CUSTOMER_SITE;
		if(vecCustomerDOs != null && vecCustomerDOs.size() > 0)
		{
			boolean isInserted = new CustomerDetailsDA().insertCustomerInforWithSync(vecCustomerDOs);
			if(isInserted)
				new SynLogDA().insertSynchLog(synLogDO);
			return isInserted;
		}
		else
			return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
