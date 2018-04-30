package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;


@SuppressWarnings("serial")
public class HHInventryQTDO implements Serializable
{
	public float totalQt = 0;
	public float onHandQty = 0;
	public float tempTotalQt = 0;
	public float totalUnits = 0;
	public float quantityBU = 0;
	
	public String batchCode = "";
	public String expiryDate = "";
	
	public float nonSellQty = 0;
	public String UOM = "";
	public String Reason = "";
}
