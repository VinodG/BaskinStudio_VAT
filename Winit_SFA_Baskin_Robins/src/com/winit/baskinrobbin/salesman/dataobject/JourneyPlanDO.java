package com.winit.baskinrobbin.salesman.dataobject;

import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;


@SuppressWarnings("serial")
public class JourneyPlanDO extends BaseComparableDO
{
	public int rowid = 0;
	public int stop;//sequence;
	public String lifeCycle="";
	public String id="";
	public String site="";
	public String siteName="";
	public String customerId="";
	public String custmerStatus = "1";
	public String custAccCreationDate = CalendarUtils.getOrderPostDate();
	public String partyName="";
	public String channelCode="";
	public String subChannelCode="";
	public String regionCode="";
	public String coutryCode="";
	public String category="";
	public String addresss1="";
	public String addresss2="";
	public String addresss3="";
	public String addresss4="";
	public String poNumber="";
	public String city="";
	public String paymentType="";
	public String paymentTermCode="";
	public String creditLimit="";
	public String geoCodeX="";
	public String geoCodeY="";
	public String Passcode="";
	public String email="";
	public String contectPersonName="";
	public String phoneNumber="";
	public String appCustomerId="";
	public String mobileNo1="";
	public String mobileNo2="";
	public String website="";
	public String customerType="";
	public String createdby="";
	public String modifiedBy="";
	public String source="";
	public String customerCategory="";
	public String customerSubCategory="";
	public String customerGroupCode="";
	public String modifiedDate="";
	public String modifiedTime="";
	public String currencyCode="";
	public String timeOut="";
	public String timeIn="";
	public String clientCode="";
	public String routeId="";
	
	public String CustomerPostingGroup="";
	public String ParentGroup="";
	public String SalesmanlandmarkId="";
	public String LandmarkId="";
	public String CustomerGrade="";
	public String StoreGrowth="";
	
	public int mPosition;
	public String ActualArrivalTime="";
	public String reasonForSkip="";
	public String dateOfJourny="";
	
	public String Distance="", TravelTime="", SeviceTime="", isServed="";
	public int isSchemeAplicable = 1;
	
	public String balanceAmount="";
	public String freeDeliveryResion = "";
	public String AppUUID = "";
	public String outLetType = "";
	public String outLetTypeId = "";
	public String competitionBrand = "";
	public String competitionBrandId = "";
	public String sku = "";
	public String buyerStatus = "";
	public String countryDesc = "";
	
	public String JourneyCode = "0";
	public String VisitCode = "0";
	public String priceList = "";
	
	public String salesmanName = "";
	public String salesmanCode = "";
	public String userID= "";
	
	public String Order_Type_Id = "";
	public String Order_Type_Name = "";
	public String CREDIT_LEVEL = "";
	
	public String SalesPersonMobileNumber = "";
	public int Max_Days_Past_Due = 0;
	public boolean IsOverCredit = false;
	public String VatNumber = "";

	//Arabic
	public String SiteNameInArabic="";
	public String Address1_AR="";
	public String  Address2_AR="";
	public String Address3_AR="";
	public String  AreaName_AR="";
	public String PostalCode_AR="";
	public String LocationName_AR="";
	public String City_AR="";

	public String partyNameArabic="";
	public int isActive;
}
