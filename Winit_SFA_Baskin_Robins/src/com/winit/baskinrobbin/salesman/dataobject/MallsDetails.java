package com.winit.baskinrobbin.salesman.dataobject;

import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.CollectionUtils.SortedType;

public class MallsDetails extends BaseComparableDO
{
	public int rowid;
	public String dateofJorney="";
	public String presellerId="";
	public String customerSiteId="";
	public String  customerPasscode="";
	public int Stop;
	public float Distance;
	public int TravelTime;
	public String ArrivalTime="";
	public String orderNo="";
	public String ActualArrivalTime="";
	public String ActualOutTime="";
	public String TotalTime="";
	public String reasonForSkip="";
	public int SeviceTime;
	public String CustomerId="";
	public String SiteName="";
	public String Address1="";
	public String Address2="";
	public String City="";
	public float CreditLimit;
	public float Longitude;
	public float Latitude;
	public String paymentType = "";
	public String paymentCode = "";
	public String email = "";
	public String paymentTermDesc = "";
	public String postBox = "22222";
	public int mPosition = 0;
	public String balanceAmount = "";
	public boolean isVisited = false;
	public String dueAmmount="";
	public String NameofMall="";
	public String inTime="";
	public String outTime="";
	public int Images=0;
	public int LogoImages=0;
	public double lat = 0;
	public double longt = 0;
	public boolean isActive = false;
	public int position;
	public String isServed = "false";
	public String subChannelCode = "";
	public String orderCount="0";
	public String channel = "";
	public String freeDeliveryResion = "";
	public String customerStatus;
	public String mobileNo;
	public String CustomerType;
	public String landId;
	public String AppUUID;
	public String country;
	public String contactPerson = "";
	public String landline = "";
	public String outLetType = "";
	public String competitionBrand = "";
	public String sku = "";
	public String outLetTypeId = "";
	public String competitionBrandId = "";
	public String buyerStatus = "";
	public String countryDesc = "";
	public int isSchemeAplicable = 0;
	public String customerValueType = "";
	public String salesmanCode = "";
	public String salesmanName = "";
	
	@Override
	public int compareTo(BaseComparableDO another) 
	{
		String strSrc = (String) this.getComparableValue();
		String strDes = (String) another.getComparableValue();
		
		if(comparableStringType == SortedType.DATE)
		{
			long timeSrc = CalendarUtils.getDateFromString(strSrc, CalendarUtils.DATE_STD_PATTERN).getTime();
			long timeDes = CalendarUtils.getDateFromString(strDes, CalendarUtils.DATE_STD_PATTERN).getTime();
			
			if(timeSrc < timeDes)
			{
				return -1;
			}
			else if(timeSrc > timeDes)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}

	@Override
	public Object getComparableValue() 
	{
		if(comparableStringType == SortedType.DATE)
		{
			comparableValue = this.dateofJorney;
		}
		
		return comparableValue;
	}
	
	@Override
	public Object getValue(SortedType sortedType) 
	{
		if(sortedType == SortedType.DATE)
		{
			return this.dateofJorney;
		}
		return null;
	}
}