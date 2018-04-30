package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;


@SuppressWarnings("serial")
public class PaymentHeaderDO extends BaseComparableDO
{
	public String AppPaymentId = "";
	public String RowStatus = "";
	public String ReceiptId = "";
	public String PreReceiptId = "";
	public String PaymentDate = "";
	public String SiteId = "";
	public String EmpNo = "";
	public String Amount = "";
	public String CurrencyCode = "";
	public String Rate = "1";
	public String VisitCode = "";
	public String PaymentStatus = "";
	public String CustomerSignature = "";
	public String Status = "";
	public String AppPayementHeaderId = "";
	public String PaymentType ="";
	public String vehicleNo ="";
	
	public Vector<PaymentDetailDO> vecPaymentDetails   = new Vector<PaymentDetailDO>();
	public Vector<PaymentInvoiceDO> vecPaymentInvoices = new Vector<PaymentInvoiceDO>();
	public String salesmanCode = "";
	public String Receipt_Method_Name = "";
}
