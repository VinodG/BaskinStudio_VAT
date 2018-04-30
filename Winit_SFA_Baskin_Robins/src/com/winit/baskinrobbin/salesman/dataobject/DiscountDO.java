package com.winit.baskinrobbin.salesman.dataobject;



public class DiscountDO 
{
	public float fPricePerCase 	= 0;
	public float fDiscount 		= 0;
	public float fDiscountAmt	= 0;
	public float fEmptyCasePrice= 0;
	public float fActualItemPrice = 0;
	
	//Added for TAX
	
	
	
	
//	-------------------------------------------
	public int discountType;
	public float discount; 
	public String level;
	public String perCaseValue;
	public String TaxGroupCode = "";
	public float TaxPercentage = 0;
	
	public String description = "";
	public String ItemCode = "";
	
	
	public String lineNo = "";
	public String ItemLineNo = "";
	public String OrderNo = "";
	public String UOM = "";
	public float Quantity = 0;
}
