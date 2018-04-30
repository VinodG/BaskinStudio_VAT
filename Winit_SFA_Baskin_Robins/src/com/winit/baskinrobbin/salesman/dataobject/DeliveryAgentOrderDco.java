package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

public class DeliveryAgentOrderDco 
{
	public String loocation = "";
	public String blaseOrderNo = "";
	public String orderDate = "";
	public String orderNumber = "";
	public String orderType = "";
	public String salesManName = "";
	public String invOrganizationName = "";
	public String invSubinventory = "";
	public String customerNumber = "";
	public String customerName = "";
	public String siteNumber = "";
	public String shipToPostNumber = "";
	public String createdBy = "";
	public String createdOn = "";
	public String lastUpdatedBy = "";
	public String deliveryDate = "";
	public String lastUpdatedOn = "";
	public String blaseInvoiceNumber = "";
	public String blaseUserId = "";
	public String deliveredBy = "";
	public String isVerified = "N";
	public String lpoNo = "";
	public boolean isDelivered = false;
	public Vector<DeliveryAgentOrderDetailDco> vecDeliveryAgentOrderDetailDcos = new Vector<DeliveryAgentOrderDetailDco>();
	public String type = "";
}
