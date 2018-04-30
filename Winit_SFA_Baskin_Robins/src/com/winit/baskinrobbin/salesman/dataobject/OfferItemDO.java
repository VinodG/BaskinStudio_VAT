package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OfferItemDO implements Serializable {
	public String orderId;
	public String itemcode;
	public String offerItemCode;
	public float qty;
	public String promotionCode = "";
}
