package com.winit.baskinrobbin.salesman.dataobject;
import java.util.Vector;
@SuppressWarnings("serial")
public class CustomerSite_NewDO extends BaseComparableDO
{
	public String CustomerSiteId="";
	public String SiteName="";
	public String CustomerId="";
	public String Address1="";
	public String Address2="";
	public String City="";
	public String Latitude="";
	public String Longitude="";
	public String Salesman="";
	public String PaymentType="";
	public String PaymentTermCode="";
	public String PaymentTermDescription="";
	public String CreditLimit="";
	public String TotalOutstandingBalance;
	public String CustomerStatus ="A";
	
	
	public String MOBILENO1="";
	public String MOBILENO2="";
	public String Website="";
	public String CustomerGrade="";
	public String CustomerType="";
	public String LandmarkId="";
	public String SalesmanlandmarkId="";
	public String Source="";
	public String BlaseCustId="";
	public String CountryId="";
	public String DOB="";
	public String AnniversaryDate ="";
	
	public String ParentGroup ="";
	public String CustomerPostingGroup ="";
	public String CustomerCategory ="";
	public String CustomerSubCategory ="";
	public String CustomerGroupCode ="";
	public String isSchemeApplicable ="";
	
	public String balCustomerNumber="";
//	public String balSiteNumber="";
	public String balCurrentBalance="";
	public String balAsOfDate="";
	public String SubChannel = "";
	public String arrivalTime="";
	public String EXPECTEDDELIVERYTIME = "";
	public String EXPECTEDDELIVERYDATE = "";
	public Vector<OrderDO> vecOrderDO = new Vector<OrderDO>();
	public Vector<CustomerOrdersDO> vecCustomerPendingInvoices;
}
