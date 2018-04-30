package com.winit.baskinrobbin.salesman.dataobject;

public class PricingDO 
{
	public String itemCode;
	public String customerPricingClass;
	public String priceCases;
	public String endDate;
	public String startDate;
	public String dicount;
	public String IsExpired;
	public String emptyCasePrice;
	
	//Added for TAX
	public String TaxGroupCode ="";
	public String TaxPercentage = "0";
	
	public String UOM ="";
	public String ModifiedTime ="";
	public String ModifiedDate ="";
}
