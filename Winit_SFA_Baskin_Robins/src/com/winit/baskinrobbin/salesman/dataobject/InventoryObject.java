package com.winit.baskinrobbin.salesman.dataobject;

@SuppressWarnings("serial")
public class InventoryObject  extends BaseComparableDO 
{
	public String itemCode="";
	public String itemDescription="";
	public float PrimaryQuantity=0.0f;
	public float deliveredQty=0.0f;
	public float availQty=0.0f;
	public float Qty_out=0.0f;
	public float Qty_In=0.0f;
	public int SecondaryQuantity=0;
	public int unitPerCases=0;
	public String VMSalesmanInventoryId="";
	public String Date="";
	public String SalesmanCode="";
	public boolean IsAllVerified = false;
	public String UOM="";
	public String expiryDate="";
	public String reason="";
	
	public float availCases		=	0.0f;
	public float deliveredCases	=	0.0f;
}
