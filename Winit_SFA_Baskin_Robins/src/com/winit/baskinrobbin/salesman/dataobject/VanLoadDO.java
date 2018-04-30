package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

import android.widget.EditText;

public class VanLoadDO extends BaseComparableDO
{
	public String id				= "";
	public String CategoryId		= "";
	public float SellableQuantity	= 0;
	public float noSellableQty		= 0;
	public float pendingQuantity	= 0;
	public float ShippedQuantity	= 0;
	public float TotalQuantity		= 0;	
	public String ItemCode			= "";
	public String Description		= "";
	public int UnitsPerCases		= 0;
	public String BatchCode			= "";
	public String ExpiryDate		= "";
	public String UOM				= "";
	public int inventoryQty;
	public String unitSellingPrice  = "";
	public float invoiceAmount      = 0;
	public float discountAmount  	= 0;
	public String customerPriceClass = "";
	public String itemType			 = "";
//	public EditText etQty = null;
	public boolean isDeleteOpen = true;
	public boolean isSelected = false;
	public Vector<String> vecUOM;
	public String reason = "";
	public float shippedQuantityLevel1= 0;
	public float shippedQuantityLevel2= 0;
	public float shippedQuantityLevel3= 0;
	public float NonSellableQuantity= 0;
	public String MovementReasonCode="";
	public String CreatedOn="";
	public float CancelledQuantity= 0;
	public float inProccessQty= 0;
	public int inProcessQuantityLevel1;
	public int inProcessQuantityLevel3;
	public String movementStatus="";
	public float editedQuantity	= 0;
	public float tempQuantity	= 0;

}
