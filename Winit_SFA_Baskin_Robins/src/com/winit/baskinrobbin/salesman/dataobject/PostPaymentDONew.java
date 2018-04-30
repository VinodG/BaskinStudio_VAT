package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;



@SuppressWarnings("serial")
public class PostPaymentDONew extends BaseComparableDO
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
	public String Rate = "";
	public String VisitCode = "";
	public String JourneyCode = "";
	public String PaymentStatus = "";
	public String CustomerSignature = "";
	public String Status = "";
	public String AppPayementHeaderId = "";
	public String PaymentType = "";
	
	public String salesmanCode = "";
	public String Receipt_Method_Name = "";
	public String vehicleNo = "";
	
	public Vector<PostPaymentDetailDONew> vecPaymentDetailDOs = new Vector<PostPaymentDetailDONew>();
	public Vector<PostPaymentInviceDO> vecPostPaymentInviceDOs = new Vector<PostPaymentInviceDO>();
}
