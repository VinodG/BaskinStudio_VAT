package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

public class PromotionOrderDO implements Serializable {
	public String PromotionId = "";
	public String UOM = "";
	public String PromotionOrderItemId = "";
	public String ItemCode = "";
	public String LineNo = "";
	public float totalCases = 0;
	public float Quantity = 0;
	public float Cases = 0;
	public int Units = 0;
	public int multiplier = 0;
}
