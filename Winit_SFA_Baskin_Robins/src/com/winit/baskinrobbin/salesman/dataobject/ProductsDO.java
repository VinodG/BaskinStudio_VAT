package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

public class ProductsDO {

	public String ProductId 	= "";
	public String SKU 			= "";
	public String CategoryId	= "";
	public String Description	= "";
	public int UnitsPerCases;
	public String BatchCode		= "";
	public String UOM			= "";
	public String CaseBarCode	= "";
	public String UnitBarCode	= "";
	public String ItemType		= "";
	public String PricingKey	= "";
	public int ItemId;
	public String Brand			= "";
	public String Category		= "";
	public String CompanyId		= "";
	public String GroupId		= "";
	public String ItemDesc		= "";
	public String ItemCode		= "";
	public String secondryUOM	= "";
	public String TaxPercentage	= "";
	public String TaxGroupCode	= "";
	public String LOT_CONTROL_CODE = "";
	public String LOT_CONTROL_NAME = "";
	
	public Vector<NameIDDo> vecProductImages;
}
