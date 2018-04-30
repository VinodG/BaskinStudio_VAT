package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class EOTSummaryPrintDO implements Serializable
{
	public HashMap<String, Vector<OrderDO>> hmOrders;
	public HashMap<String, ArrayList<Customer_InvoiceDO>> hmPayments;
	public Vector<InventoryObject> vecInventoryItems;
	public Vector<InventoryObject> vecNonInventoryItems;
	public Vector<InventoryObject> vecSalableInventoryItems;
	public Vector<ProductDO> vecReplaceOrder;
}
