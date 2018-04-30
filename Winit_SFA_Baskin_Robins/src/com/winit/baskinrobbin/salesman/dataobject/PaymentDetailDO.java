package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

public class PaymentDetailDO implements Serializable
{
	public String RowStatus = "";
	public String ReceiptNo= "";
	public String LineNo= "";
	public String PaymentTypeCode= "";
	public String BankCode= "";
	public String BankName= "";
	public String ChequeDate= "";
	public String ChequeNo= "";
	public String CCNo= "";
	public String CCExpiry= "";
	public String PaymentStatus= "";
	public String PaymentNote;
	public String UserDefinedBankName= "";
	public String Status= "";
	public String Amount= "";
	//Need to remove
	public int paymentId;
	public String invoiceNumber;
	public String invoiceAmount;
	
	private static final String PAYMENT_TYPE_CASH = "CASH";
	private static final String PAYMENT_TYPE_CHEQUE = "CHEQUE";
	
	public static String getPaymentTypeCash(){
		return PAYMENT_TYPE_CASH;
	}
	public static String getPaymentTypeCheque(){
		return PAYMENT_TYPE_CHEQUE;
	}
}
