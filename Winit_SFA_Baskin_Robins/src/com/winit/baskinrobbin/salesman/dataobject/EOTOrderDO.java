package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

public class EOTOrderDO 
{
	public String strOrderId ="";
	public String strOrderType ="";
	public String strSiteName = "";
	public String strSiteId = "";
	public String strUUID = "";
	public String strCustomerId = "";
	public Vector<ProductDO> vecProductList = new Vector<ProductDO>();
}
