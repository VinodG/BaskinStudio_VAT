package com.winit.baskinrobbin.salesman.dataobject;

public class CustomerDO  extends BaseObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int rowid;
	public String customerSiteID="";
	public String Customer_Id="";
	public String customerName="";
	public String Customer_Date="";
	public String customerStreet="";
	public String customerBuilding="";
	public String customerLocation="";
	public String customerEmirates="";
	public String Customer_Images="";
	public int Customer_Logo_Images;
	public String Customer_Latitude="";
	public String Customer_Longitude="";
	public String Customer_InTime="";
	public String Customer_OutTime="";
	public String isServed="";
	public String isCheckedIn="";
	public String paymentID="";
	
	public String strUUID="";
	public String post_BoxNo="";
	public String payment_Type="";
	public String payment_Terms_Code="";
	public String payment_Terms="";
	public String credit_Limit="";
	public String presellerId="";
	public String email_Id="";
	public String telephone_No="";
	public String fax_No="";
	public String contactPersonName = "";
	public String contactPersonMobileNumber = "";
	public String regionCode = "";
	public String regionDescription = "";
	public String countryCode = "DUB";
	public String countryDescription = "Dubai";
}
