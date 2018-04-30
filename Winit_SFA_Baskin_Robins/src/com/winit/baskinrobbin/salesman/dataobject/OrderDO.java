package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;


@SuppressWarnings("serial")
public class OrderDO extends BaseComparableDO implements Serializable
{
	public String OrderId = "";
	public String orderType = "";
	public String orderSubType = "";
	public String PresellerId = "";
	public String empNo = "";
	public String CustomerSiteId = "";
	public Double Discount;
	public Double TotalAmount;
	public String DeliveryStatus = "";
	public String DeliveryDate = "";
	public double BalanceAmount;
	public String InvoiceNumber = "";
	public String InvoiceDate = "";
	public String DeliveryAgentId = "";
	public String strCustomerName = "";
	public String strAddress1 = "";
	public String strAddress2 = "";
	public String strCustomerPriceKey = "";
	//added recently required to save the signature images
	public String strPresellerSign = "";
	public String strCustomerSign  = "";
	public String strUUID  = "";
	
	public int pushStatus;
	public String message="";
	public String isDiscountApplied = "";
	public String strCustomerRefCode = "";
	public String freeDeliveryResion = "";
	
	public String JourneyCode = "";
	public String VisitCode = "";
	public String StampDate = "";
	public String StampImage = "";
	public String TRXStatus = "";
	public String TotalTaxAmt = "";
	public String TrxReasonCode = "";
	public String CurrencyCode = "";
	public String PaymentType = "";
	public String PaymentCode = "";
	public String LPOCode = "";
	public String salesmanCode = "";
	public String vehicleNo = "";
	
	public String TRANSACTION_TYPE_VALUE = "";
	public String TRANSACTION_TYPE_KEY   = "";
	
	public String Batch_Source_Name   = "";
	public String Trx_Type_Name		  = "";
	public String LPOStatus		  = "";
	
	public String SourceVehicleCode = "";
	
	public Vector<ProductDO> vecProductDO = new Vector<ProductDO>();
	public Vector<ProductDO> vecProductDOPromotions = new Vector<ProductDO>();
	public float roundOffVal = 0;
	public String strRoundOffVal = "0";


	/********************Added For TAX**********************************/
	public double VatAmount=0.0;
	public double TotalAmountWithVat=0.0;
	public double ProrataTaxAmount=0.0;
	public double TotalTax=0.0;
	public ArrayList<OrderWiseTaxViewDO> arrOrderwiseAppliedTax=new ArrayList<OrderWiseTaxViewDO>();
	/****************************************************************/


} 
