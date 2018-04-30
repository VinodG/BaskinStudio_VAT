package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.Vector;

@SuppressWarnings("serial")
public class Customer_InvoiceDO implements Serializable
{
	public String customerId;
	public String customerSiteId;
	public String reciptType;
	public String siteName;
	public int paymentId;
	public String invoiceTotal;
	public String receiptNo;
	public String creditCardNo;
	public String chequeNo;
	public String chequeDate;
	public String bankName;
	public String reciptDate;
	public String uuid;
	public String couponAmount;
	public String couponNo;
	public String payment_Id;//this variable is used only to show the coupon payment
	public Vector<PaymentDetailDO> vecPaymentDetailDOs;
	public String totalVal = "";
	public String currencyCode = "AED";
	public int status = 0;
}
