package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

import android.widget.EditText;

@SuppressWarnings("serial")
public class DeliveryAgentOrderDetailDco implements Serializable, Cloneable
{
	public String orderItemId = "";
	public String blaseOrderNumber = "";
	public String lineNumber = "";
	public String itemCode = "";
	public String itemDescription = "";
	public String orderedQty = "";
	public String unitSellingPrice = "";
	public int unitPerCase ;
	public String recommendedQty = "";
	public String actualShipedDate = "";
	public String actualQtyShiped = "";
	public String totalQtyShiped = "";
	public String customerPriceClass = "";
	public String currencyCode = "";
	public String blaseOrederStatus = "";
	public String deliveredQty = "";
	public String strUOM = "";
	public String suggestedQty = "";
	public String strSiteName = "";
	public String strDiscount = "";
	public String NextPaymentDate = "";
	public String orderStatus = "";
	public boolean isDiscountApplied=false;
	public float  cases ;
	public int    preUnits ;
	public float  preCases ;
	public float  totalCases ;
	public float  delvrdCases ;
	public String  orderInvoiceNumber ;
	public String  itemType = "" ;
	public float  itemPrice ;
	public float invoiceAmount    = 0;
	public float discountAmount   = 0;
	public float unitActSellingPrice   = 0;
	public EditText etCases = null,etUnits = null;
	public String CategoryId = "";
	public String secondaryUOM = "";
	public String type = "";
	public String returnStatus = "";
	public int inventoryQty;
	public float gatePassQt;
	public float availQty;
	public String transferDetailID = 0+"";
	
	public float    checkInCases ;
	public int  checkInPcs ;
	public float  advnCases ;
	public int  advnPcs ;
	
	public String expiryDate = "";
	public String itemBatchCode = "";
	
	@Override
	public Object clone() throws CloneNotSupportedException 
	{
		DeliveryAgentOrderDetailDco clone = new DeliveryAgentOrderDetailDco();
		
		clone.orderItemId 	= 	this.orderItemId;
		clone.blaseOrderNumber 			= 	this.blaseOrderNumber;
		clone.lineNumber 	=	this.lineNumber;
		clone.itemCode 	= 	this.itemCode;
		clone.itemDescription	=	this.itemDescription;
		clone.orderedQty 	= 	this.orderedQty;
		clone.unitSellingPrice 			= 	this.unitSellingPrice;
		clone.unitPerCase 	= 	this.unitPerCase;
		clone.recommendedQty	= 	this.recommendedQty;
		clone.actualShipedDate		= 	this.actualShipedDate;
		clone.actualQtyShiped	= 	this.actualQtyShiped;
		clone.customerPriceClass			= 	this.customerPriceClass;
		clone.cases 		= 	this.cases;
		clone.currencyCode 		= 	this.currencyCode;
		clone.preCases 		= 	this.preCases;
		clone.preUnits 		= 	this.preUnits;
		clone.blaseOrederStatus	= 	this.blaseOrederStatus;
		clone.deliveredQty	= 	this.deliveredQty;
		clone.strDiscount    = 	this.strDiscount;
		
		return clone;
	}
}
