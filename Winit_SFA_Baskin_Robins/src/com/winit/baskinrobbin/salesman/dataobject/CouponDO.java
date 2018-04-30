package com.winit.baskinrobbin.salesman.dataobject;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class CouponDO extends BaseComparableDO
{
	public String oldReceiptId = ""; 
	public String newReceiptId = ""; 
	
	public ArrayList<PendingInvicesDO> arrPendingInvices = new ArrayList<PendingInvicesDO>();
}
