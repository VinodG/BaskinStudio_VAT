package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

@SuppressWarnings("serial")
public class InventoryDO extends BaseComparableDO
{
	public String inventoryId = "";
	public String site = "";
	public String date = "";
	public int uplaodStatus = 0;
	public String inventoryAppId="";
	public String createdBy="";
	public Vector<InventoryDetailDO> vecInventoryDOs = new Vector<InventoryDetailDO>();
}
