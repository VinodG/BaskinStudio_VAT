package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;


@SuppressWarnings("serial")
public class PostPaymentDO extends BaseComparableDO
{
	public String PaymentId = "";
	public String PaymentMode = "";
	public String receiptType = "";
	public String receiptID = "";
	public String receiptNumber = "";
	public String customerId = "";
	public String customerSiteId = "";
	public String PaymentDate = "";
	public String ChequeDate = "";
	public String Amount = "";
	public String BankName = "";
	public String ChequeNumber = "";
	public String CreditCardNumber = "";
	public String ExpiryDate = "1900-01-01";
	public String CouponNo = "";
	public String signatureString = "";
	public String PassCode = "";
	public String strCardName = "";
	public String strUUID = "";
	public String CREATEDBY ="";
	public Vector<PostPaymentDetailDO> vecPaymentDetailDOs = new Vector<PostPaymentDetailDO>();
}
