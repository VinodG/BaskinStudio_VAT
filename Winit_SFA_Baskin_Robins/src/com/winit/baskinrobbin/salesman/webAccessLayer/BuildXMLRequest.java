package com.winit.baskinrobbin.salesman.webAccessLayer;

import android.text.TextUtils;
import android.util.Log;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;
import com.winit.baskinrobbin.salesman.dataobject.AdvanceOrderDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;
import com.winit.baskinrobbin.salesman.dataobject.AssetServiceDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDetailDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDo;
import com.winit.baskinrobbin.salesman.dataobject.CheckInDemandInventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDONew;
import com.winit.baskinrobbin.salesman.dataobject.CustomerVisitDO;
import com.winit.baskinrobbin.salesman.dataobject.DamageImageDO;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.ItemWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.JouneyStartDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.LoginUserInfo;
import com.winit.baskinrobbin.salesman.dataobject.MallsDetails;
import com.winit.baskinrobbin.salesman.dataobject.MyActivityDO;
import com.winit.baskinrobbin.salesman.dataobject.NewCustomerDO;
import com.winit.baskinrobbin.salesman.dataobject.NotesObject;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderWiseTaxViewDO;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDO;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDONew;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.baskinrobbin.salesman.dataobject.PostPaymentInviceDO;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.QuestionOptionDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;
import com.winit.baskinrobbin.salesman.dataobject.TransferInoutDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VerifyRequestDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BuildXMLRequest 
{
	public static ArrayList<String> arrOrderNumbers;
	private final static String SOAP_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
										"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
										"<soap:Body>";

	private final static String SOAP_FOOTER="</soap:Body>"+
									"</soap:Envelope>";
	public static String DeviceCheckLogin(String UserName, String password, String gcmId,String deviceID)
	{
		LogUtils.errorLog("CheckLogin","CheckLogin - "+UserName);
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
		.append("<CheckLogin xmlns=\"http://tempuri.org/\">")
		.append("<UserName>").append(UserName).append("</UserName>" )
		.append("<Password>").append(password).append("</Password>" )
		.append("<GCMKey>").append(gcmId).append("</GCMKey>" )
		.append("<DeviceNo>").append(deviceID).append("</DeviceNo>" )
		.append("</CheckLogin>")
		.append(SOAP_FOOTER);
		
		LogUtils.errorLog("CheckLogin",""+sb.toString());
		
		return sb.toString();
	}
	//login request
	public static String loginRequest(String UserName, String password, String gcmId)
	{
		LogUtils.errorLog("CheckLogin","CheckLogin - "+UserName);
		String strXML = SOAP_HEADER+
						"<CheckLogin xmlns=\"http://tempuri.org/\">" +
							"<UserName>"+UserName+"</UserName>" +
							"<Password>"+password+"</Password>" +
							 "<GCMKey>"+gcmId+"</GCMKey>"+
						"</CheckLogin>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String VerifyUnloadRequests(VerifyRequestDO verifyRequestDO,LoadRequestDO loadDo,Preference prefrence)
	{
		StringBuilder strOrder = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
		.append("<ShipStockMovementsFromXML xmlns=\"http://tempuri.org/\">")
		.append("<objMovementHeaderDcos>");
		strOrder.append("<MovementHeaderDco>")
		.append("<MovementCode>").append(verifyRequestDO.movementCode).append("</MovementCode>")
		.append("PreMovementCode").append(loadDo.PreMovementCode).append("PreMovementCode")
		.append("AppMovementId").append(loadDo.AppMovementId).append("AppMovementId")
		.append("OrgCode").append(loadDo.OrgCode).append("OrgCode")
		.append("UserCode").append(loadDo.UserCode).append("UserCode")
		.append("WHKeeperCode").append(loadDo.WHKeeperCode).append("WHKeeperCode")
		.append("CurrencyCode").append(loadDo.CurrencyCode).append("CurrencyCode")
		.append("JourneyCode").append(loadDo.CurrencyCode).append("CurrencyCode")
		.append("MovementDate").append(loadDo.MovementDate).append("MovementDate")
		.append("MovementNote").append(loadDo.MovementNote).append("MovementNote")
		.append("MovementType").append(loadDo.MovementType).append("MovementType")
		.append("ProductType").append(loadDo.ProductType).append("ProductType")
		.append("SourceVehicleCode").append(loadDo.SourceVehicleCode).append("SourceVehicleCode")
		.append("DestinationVehicleCode").append(loadDo.DestinationVehicleCode).append("DestinationVehicleCode")
		.append("VisitID").append(loadDo.VisitID).append("VisitID")
		.append("CreatedOn").append(loadDo.CreatedOn).append("CreatedOn")
		.append("Amount").append(loadDo.Amount).append("Amount")
		.append("<_MovementStatus>99</_MovementStatus>")
		.append("<MovementStatus>99</MovementStatus>")
		.append("<SalesmanSignature>").append(URLEncoder.encode(verifyRequestDO.salesmanSignature+"")).append("</SalesmanSignature>")
		.append("<WHManagerSignature>").append(URLEncoder.encode(verifyRequestDO.logisticSignature+"")).append("</WHManagerSignature>")
		.append(getVerifiItem(verifyRequestDO.vecVanLodDOs, verifyRequestDO.movementCode, verifyRequestDO.movementType))
		.append("</MovementHeaderDco>");
		strXML.append(strOrder)
		.append("</objMovementHeaderDcos>")
		.append("</ShipStockMovementsFromXML>")
		.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", ""+strXML);
		
		return strXML.toString();
	}
	public static String VerifyRequestRequests(VerifyRequestDO verifyRequestDO,Preference prefrence)
	{
		StringBuilder strOrder = new StringBuilder();
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER) 
		.append("<ShipStockMovementsFromXML xmlns=\"http://tempuri.org/\">")
		.append("<objMovementHeaderDcos>");
		strOrder.append("<MovementHeaderDco>")
		.append("<MovementCode>").append(verifyRequestDO.movementCode).append("</MovementCode>")
		.append("<MovementStatus>100</MovementStatus>")
		.append("<SalesmanSignature>").append(URLEncoder.encode(verifyRequestDO.salesmanSignature+"")).append("</SalesmanSignature>")
		.append("<WHManagerSignature>").append(URLEncoder.encode(verifyRequestDO.logisticSignature+"")).append("</WHManagerSignature>")
		.append(getVerifiItem(verifyRequestDO.vecVanLodDOs, verifyRequestDO.movementCode, verifyRequestDO.movementType))
		.append("</MovementHeaderDco>");
		strXML.append(strOrder)
		.append("</objMovementHeaderDcos>")
		.append("</ShipStockMovementsFromXML>")
		.append(SOAP_FOOTER) ;
		LogUtils.errorLog("strXML", ""+strXML);
		
		return strXML.toString();
	}
	private static String getVerifiItem(ArrayList<VanLoadDO> vecItems, String movementId, String movementType)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append("<MovementDetailDcos>");
		if(vecItems != null && vecItems.size() >0)
		{
			int count =0;
			for (VanLoadDO productDO : vecItems)
			{
				count++;
				try {
					strXML.append("<MovementDetailDco>")
					.append("<LineNo>").append(count).append("</LineNo>")
					.append("<MovementCode>").append(movementId).append("</MovementCode>")
					.append("<ItemCode>").append(productDO.ItemCode).append("</ItemCode>")
					.append("<OrgCode></OrgCode>")
					.append("<ItemDescription>").append(URLEncoder.encode(productDO.Description,"UTF-8")).append("</ItemDescription>")
					.append("<ItemAltDescription></ItemAltDescription>")
					.append("<UOM>").append(productDO.UOM).append("</UOM>")
					.append("<QuantityLevel1>0</QuantityLevel1>")
					.append("<QuantityLevel2>0</QuantityLevel2>")
					.append("<QuantityLevel3>0</QuantityLevel3>")

					.append("<InProcessQuantityLevel1>0</InProcessQuantityLevel1>")
					.append("<InProcessQuantityLevel2>0</InProcessQuantityLevel2>")
					.append("<InProcessQuantityLevel3>0</InProcessQuantityLevel3>")

					.append("<ShippedQuantityLevel1>").append(productDO.shippedQuantityLevel1).append("</ShippedQuantityLevel1>")
					.append("<ShippedQuantityLevel2>").append(productDO.shippedQuantityLevel2).append("</ShippedQuantityLevel2>")
					.append("<ShippedQuantityLevel3>").append(productDO.shippedQuantityLevel3).append("</ShippedQuantityLevel3>")
					.append("<NonSellableQty>").append(productDO.NonSellableQuantity).append("</NonSellableQty>")
					.append("<QuantityBU>0</QuantityBU>")
					.append("<CurrencyCode>0</CurrencyCode>")
					.append("<PriceLevel1>0</PriceLevel1>")
					.append("<PriceLevel2>0</PriceLevel2>")
					.append("<PriceLevel3>0</PriceLevel3>")
					.append("<MovementReasonCode>").append(productDO.MovementReasonCode).append("</MovementReasonCode>")
					.append("<ExpiryDate>").append(productDO.ExpiryDate).append("</ExpiryDate>")
					.append("<CreatedOn>").append(productDO.CreatedOn).append("</CreatedOn>")
					.append("<MovementType>").append(movementType).append("</MovementType>")
					.append("<CancelledQuantity>").append(productDO.CancelledQuantity).append("</CancelledQuantity>")
					.append("<InProcessQuantity>").append(productDO.inProccessQty).append("</InProcessQuantity>")
					.append("<ShippedQuantity>").append(productDO.ShippedQuantity).append("</ShippedQuantity>")

					.append("<BatchNumber>").append(productDO.BatchCode).append("</BatchNumber>")
					.append("</MovementDetailDco>");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}	
			}
		}
		strXML.append("</MovementDetailDcos>");
		return strXML.toString();
	}
	
	
	public static String getPendingInvoicesFromSAP(String customerCode,String sales_org,String user_id)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetPendingInvoiceDataByCode xmlns=\"http://tempuri.org/\">")
	     			.append("<CustomerCode>").append(customerCode).append("</CustomerCode>")
//	     			.append("<SalesOrgCode>").append(sales_org).append("</SalesOrgCode>")
	     			.append("<UserCode>").append(user_id).append("</UserCode>")
	     			.append("<lsd>").append("0").append("</lsd>")
	     			.append("<lst>").append("0").append("</lst>")
	     			.append("</GetPendingInvoiceDataByCode>")
					.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	
	public static String helloRequest()
	{
//		LogUtils.errorLog("Hello","helloRequest - ");
		StringBuilder sb = new StringBuilder();
		try
		{
			sb.append(SOAP_HEADER)
			.append("<Hello xmlns=\"http://tempuri.org/\" />")
			.append(SOAP_FOOTER);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
//		LogUtils.errorLog("Hello",""+sb.toString());
		
		return sb.toString();
	}
	
	
	public static String validatePassCode(String deviceNO, String passcode, String userCode)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
				.append("<GetUserDeviceStatusByPasscode xmlns=\"http://tempuri.org/\">")
				.append("<DeviceNO>").append(deviceNO).append("</DeviceNO>")
				.append("<Passcode>").append(passcode).append("</Passcode>")
				.append("<UserCode>").append(userCode).append("</UserCode>")
				.append("</GetUserDeviceStatusByPasscode  >")
				.append(SOAP_FOOTER);
		
		LogUtils.errorLog("GetTrxLogDataSync", "" + strXML);
		return strXML.toString();
	}
	
	public static String GetUserDeviceStatus(String deviceNO){
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
				.append("<GetUserDeviceStatus   xmlns=\"http://tempuri.org/\">")
				.append("<DeviceNO>").append(deviceNO).append("</DeviceNO>")
				.append("</GetUserDeviceStatus >").append(SOAP_FOOTER);
		LogUtils.errorLog("GetTrxLogDataSync", "" + strXML);
		return strXML.toString();
	}
	
	public static String getVersionDetails(String deviceType,String versionNumber,String userCode)
	{
		String strXML = SOAP_HEADER +
				"<GetVersionDetails xmlns=\"http://tempuri.org/\">"+
				"<UserCode>"+userCode+"</UserCode>"+
				"<DeviceType>"+deviceType+"</DeviceType>"+
				"<VersionNo>"+versionNumber+"</VersionNo>"+
			    "</GetVersionDetails>"+
				SOAP_FOOTER;
		LogUtils.errorLog("VersionRequest", strXML);
		return strXML;
	}
	public static String postPayments(Vector<PostPaymentDONew> vecPayments)
	{
		String strPayments = "";
		String strXML = SOAP_HEADER+
						"<InsertPayment xmlns=\"http://tempuri.org/\">" +
						"<objPaymentHeaderDcos>";
		
		for(PostPaymentDONew postPaymentDO : vecPayments)
		{
			strPayments +=  "<PaymentHeaderDco>"+
							"<Receipt_Number>"+postPaymentDO.ReceiptId+"</Receipt_Number>"+
							"<AppId>"+postPaymentDO.AppPaymentId+"</AppId>"+
							"<SITE_NUMBER>"+postPaymentDO.SiteId+"</SITE_NUMBER>"+
							"<ReceiptDate>"+postPaymentDO.PaymentDate+"</ReceiptDate>"+
							"<EmpNo>"+postPaymentDO.EmpNo+"</EmpNo>"+
							"<Amount>"+postPaymentDO.Amount+"</Amount>"+
							"<JourneyCode>"+postPaymentDO.JourneyCode+"</JourneyCode>" +
					  		"<VisitCode>"+postPaymentDO.VisitCode+"</VisitCode>" +
							"<PaymentType>"+postPaymentDO.PaymentType+"</PaymentType>"+
							"<VehicleCode>"+postPaymentDO.vehicleNo+"</VehicleCode>"+
							"<CollectedBy>"+postPaymentDO.salesmanCode+"</CollectedBy>"+
							"<Receipt_Method_Name>"+postPaymentDO.Receipt_Method_Name+"</Receipt_Method_Name>"+
							"<CustomerSignature>"+URLEncoder.encode(postPaymentDO.CustomerSignature)+"</CustomerSignature>";
			                
							String strPaymentDetails = "";
							for(PostPaymentDetailDONew obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
							{
								strPaymentDetails += "<PaymentDetailDco>"+
									 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptNo+"</Receipt_Number>"+
									 				 "<LineNo>"+obPaymentDetailDO.LineNo+"</LineNo>"+
									 				 "<PaymentMode>"+obPaymentDetailDO.PaymentTypeCode+"</PaymentMode>"+
									 				 "<BankCode>"+obPaymentDetailDO.BankCode+"</BankCode>"+
									 				 "<OtherBankName>"+obPaymentDetailDO.UserDefinedBankName+"</OtherBankName>"+//enable it once it is done frombackend
									 				 "<ChequeDate>"+obPaymentDetailDO.ChequeDate+"</ChequeDate>"+
									 				 "<ChequeNo>"+obPaymentDetailDO.ChequeNo+"</ChequeNo>"+
									 				 "<CCNo>"+obPaymentDetailDO.CCNo+"</CCNo>"+
									 				 "<CCExpiry>"+obPaymentDetailDO.CCExpiry+"</CCExpiry>"+
									 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
									 				 "</PaymentDetailDco>";
							}
							String strPaymentInvice = "";
							for(PostPaymentInviceDO obPaymentDetailDO : postPaymentDO.vecPostPaymentInviceDOs)
							{
								strPaymentInvice += "<PaymentInvoiceDco>"+
									 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptId+"</Receipt_Number>"+
									 				 "<Invoice_Number>"+obPaymentDetailDO.TrxCode+"</Invoice_Number>"+
									 				 "<TrxType>"+obPaymentDetailDO.TrxType+"</TrxType>"+
									 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
									 				 "<ERPReference>"+obPaymentDetailDO.ebs_ref_no+"</ERPReference>"+
									 				 "</PaymentInvoiceDco>";
							}
							strPayments	+="<PaymentDetails>"+strPaymentDetails+"</PaymentDetails><PaymentInvoices>"+strPaymentInvice+"</PaymentInvoices></PaymentHeaderDco>";
		}
		strXML = strXML+strPayments+ "</objPaymentHeaderDcos>"+
									"</InsertPayment>"+
						SOAP_FOOTER;
//		writeToSdcards(strXML);
		return strXML;
	}
	
	public static String postSinglePayments(PostPaymentDONew postPaymentDO)
	{
		if(TextUtils.isEmpty(postPaymentDO.CurrencyCode))
			postPaymentDO.CurrencyCode = AppConstants.CURRENCY_CODE;
		
		String strPayments = "";
		String strXML = SOAP_HEADER+
						"<InsertPayment xmlns=\"http://tempuri.org/\">" +
						"<objPaymentHeaderDcos>";

		strPayments +=  "<PaymentHeaderDco>"+
						"<Receipt_Number>"+postPaymentDO.ReceiptId+"</Receipt_Number>"+
						"<AppId>"+postPaymentDO.AppPaymentId+"</AppId>"+
						"<SITE_NUMBER>"+postPaymentDO.SiteId+"</SITE_NUMBER>"+
						"<ReceiptDate>"+postPaymentDO.PaymentDate+"</ReceiptDate>"+
						"<EmpNo>"+postPaymentDO.EmpNo+"</EmpNo>"+
						"<Amount>"+postPaymentDO.Amount+"</Amount>"+
						"<JourneyCode>"+postPaymentDO.JourneyCode+"</JourneyCode>" +
				  		"<VisitCode>"+postPaymentDO.VisitCode+"</VisitCode>" +
						"<PaymentType>"+postPaymentDO.PaymentType+"</PaymentType>"+
						"<VehicleCode>"+postPaymentDO.vehicleNo+"</VehicleCode>"+
						"<CollectedBy>"+postPaymentDO.salesmanCode+"</CollectedBy>"+
						"<CurrencyCode>"+postPaymentDO.CurrencyCode+"</CurrencyCode>" +
						"<Receipt_Method_Name>"+postPaymentDO.Receipt_Method_Name+"</Receipt_Method_Name>";
//						+"<CustomerSignature>"+URLEncoder.encode(""+postPaymentDO.CustomerSignature)+"</CustomerSignature>";
		                ;
						String strPaymentDetails = "";
						for(PostPaymentDetailDONew obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
						{
							strPaymentDetails += "<PaymentDetailDco>"+
								 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptNo+"</Receipt_Number>"+
								 				 "<LineNo>"+obPaymentDetailDO.LineNo+"</LineNo>"+
								 				 "<PaymentMode>"+obPaymentDetailDO.PaymentTypeCode+"</PaymentMode>"+
								 				 "<BankCode>"+URLEncoder.encode(""+obPaymentDetailDO.BankCode)+"</BankCode>"+
								 				 "<OtherBankName>"+URLEncoder.encode(""+obPaymentDetailDO.UserDefinedBankName)+"</OtherBankName>"+//enable it once it is done frombackend
								 				 "<ChequeDate>"+obPaymentDetailDO.ChequeDate+"</ChequeDate>"+
								 				 "<ChequeNo>"+obPaymentDetailDO.ChequeNo+"</ChequeNo>"+
								 				 "<CCNo>"+obPaymentDetailDO.CCNo+"</CCNo>"+
								 				 "<CCExpiry>"+obPaymentDetailDO.CCExpiry+"</CCExpiry>"+
								 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
								 				 "</PaymentDetailDco>";
						}
						String strPaymentInvice = "";
						for(PostPaymentInviceDO obPaymentDetailDO : postPaymentDO.vecPostPaymentInviceDOs)
						{
							strPaymentInvice += "<PaymentInvoiceDco>"+
								 				 "<Receipt_Number>"+obPaymentDetailDO.ReceiptId+"</Receipt_Number>"+
								 				 "<Invoice_Number>"+obPaymentDetailDO.TrxCode+"</Invoice_Number>"+
								 				 "<TrxType>"+obPaymentDetailDO.TrxType+"</TrxType>"+
								 				 "<Amount>"+obPaymentDetailDO.Amount+"</Amount>"+
								 				 "<ERPReference>"+obPaymentDetailDO.ebs_ref_no+"</ERPReference>"+
								 				 "</PaymentInvoiceDco>";
						}
						strPayments	+="<PaymentDetails>"+strPaymentDetails+"</PaymentDetails><PaymentInvoices>"+strPaymentInvice+"</PaymentInvoices></PaymentHeaderDco>";
	
		strXML = strXML+strPayments+ "</objPaymentHeaderDcos>"+
									"</InsertPayment>"+
						SOAP_FOOTER;
//		writeToSdcards(strXML);
		return strXML;
	}
	
	public static String postSinglePayments(PostPaymentDO postPaymentDO)
	{
		String strPayments = "", strPaymentDetails= "";
		String strXML = SOAP_HEADER+
						"<InsertRecipt xmlns=\"http://tempuri.org/\">" +
						"<ReceiptResponse>"+
						"<Receipts>"+
						"<Receipt>";
			strPayments +=  "<RECEIPT_TYPE>"+postPaymentDO.receiptType+"</RECEIPT_TYPE>"+
							"<RECEIPT_METHOD>"+postPaymentDO.PaymentMode+"</RECEIPT_METHOD>"+
							"<RECEIPT_NUMBER>"+postPaymentDO.PaymentId+"</RECEIPT_NUMBER>"+
							"<RECEIPT_ID>"+0+"</RECEIPT_ID>"+
							"<CUSTOMER_NO>"+postPaymentDO.customerId+"</CUSTOMER_NO>"+
							"<SITE_NUMBER>"+postPaymentDO.customerSiteId+"</SITE_NUMBER>"+
							"<RECEIPT_DATE>"+postPaymentDO.PaymentDate+"</RECEIPT_DATE>"+
							"<CHECK_DATE>"+postPaymentDO.ChequeDate+"</CHECK_DATE>"+
							"<AMOUNT>"+postPaymentDO.Amount+"</AMOUNT>"+
							"<BANK>"+postPaymentDO.BankName+"</BANK>"+
							"<CHECK_NO>"+postPaymentDO.ChequeNumber+"</CHECK_NO>"+
							"<CC_NO>"+postPaymentDO.CreditCardNumber+"</CC_NO>"+
							"<CC_EXPIRY>"+postPaymentDO.ExpiryDate+"</CC_EXPIRY>"+
							"<COUPON_NO>"+postPaymentDO.CouponNo+"</COUPON_NO>"+
							"<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +
							"<CREATEDBY>"+postPaymentDO.CREATEDBY+"</CREATEDBY>" +
							"<ReceiptDetails>";
			for(PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
			{
				strPaymentDetails += "<ReceiptDetail>"+
									 "<INVOICE_NUMBER>"+obPaymentDetailDO.invoiceNumber+"</INVOICE_NUMBER>"+
									 "<INVOICE_AMOUNT>"+obPaymentDetailDO.invoiceAmount+"</INVOICE_AMOUNT>"+
									 "<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +//added new by anil
									 "</ReceiptDetail>";
			}
		strXML = strXML+strPayments+strPaymentDetails+ "</ReceiptDetails>"+"</Receipt>"+"</Receipts>"+
						"</ReceiptResponse>"+
						"</InsertRecipt>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String postSingleCouponPayments(PostPaymentDO postPaymentDO)
	{
		String strPayments = "", strPaymentDetails= "";
		String strXML = SOAP_HEADER+
						"<InsertReciptOnlineCoupons xmlns=\"http://tempuri.org/\">"+
						"<Receipt>";
			strPayments +=  "<RECEIPT_TYPE>"+postPaymentDO.receiptType+"</RECEIPT_TYPE>"+
							"<RECEIPT_METHOD>"+postPaymentDO.PaymentMode+"</RECEIPT_METHOD>"+
							"<RECEIPT_NUMBER>"+postPaymentDO.PaymentId+"</RECEIPT_NUMBER>"+
							"<RECEIPT_ID>"+0+"</RECEIPT_ID>"+
							"<CUSTOMER_NO>"+postPaymentDO.customerId+"</CUSTOMER_NO>"+
							"<SITE_NUMBER>"+postPaymentDO.customerSiteId+"</SITE_NUMBER>"+
							"<RECEIPT_DATE>"+postPaymentDO.PaymentDate+"</RECEIPT_DATE>"+
							"<CHECK_DATE>"+postPaymentDO.ChequeDate+"</CHECK_DATE>"+
							"<AMOUNT>"+postPaymentDO.Amount+"</AMOUNT>"+
							"<BANK>"+postPaymentDO.BankName+"</BANK>"+
							"<CHECK_NO>"+postPaymentDO.ChequeNumber+"</CHECK_NO>"+
							"<CC_NO>"+postPaymentDO.CreditCardNumber+"</CC_NO>"+
							"<CC_EXPIRY>"+postPaymentDO.ExpiryDate+"</CC_EXPIRY>"+
							"<COUPON_NO>"+postPaymentDO.CouponNo+"</COUPON_NO>"+
							"<AppReceiptId>"+postPaymentDO.strUUID+"</AppReceiptId>" +
							"<CREATEDBY>"+postPaymentDO.CREATEDBY+"</CREATEDBY>" +
							"<ReceiptDetails>";
			
			for(PostPaymentDetailDO obPaymentDetailDO : postPaymentDO.vecPaymentDetailDOs)
			{
				strPaymentDetails += "<ReceiptDetail>"+
									 "<INVOICE_NUMBER>"+obPaymentDetailDO.invoiceNumber+"</INVOICE_NUMBER>"+
									 "<INVOICE_AMOUNT>"+obPaymentDetailDO.invoiceAmount+"</INVOICE_AMOUNT>"+
									 "<AppReceiptId>"+obPaymentDetailDO.detailUUID+"</AppReceiptId>" +//added new by anil
									 "</ReceiptDetail>";
			}
		strXML = strXML+strPayments+strPaymentDetails+ "</ReceiptDetails>"+"</Receipt>"+
	    				"</InsertReciptOnlineCoupons>"+
						SOAP_FOOTER;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String deleteOrder(String strOrderId,String appId)
	{
		String strXML = SOAP_HEADER+
						"<DeleteOrderFromApp xmlns=\"http://tempuri.org/\">"+
						"<OrderNumber>"+strOrderId+"</OrderNumber>"+
						"<AppOrderId>"+appId+"</AppOrderId>"+
						"</DeleteOrderFromApp>"+
						SOAP_FOOTER;
		LogUtils.errorLog("deleteOrder",""+strXML);
		return strXML;
	}
	
	public static String getAllDataSync(String userCode,String lastDate,String lastTime)
	{
		StringBuilder strXML = new StringBuilder();
				strXML.append(SOAP_HEADER)
					.append("<GetAllDataSync  xmlns=\"http://tempuri.org/\">")
					.append("<UserCode>").append(userCode).append("</UserCode>")
					.append("<lsd>").append(lastDate).append("</lsd>")
					.append("<lst>").append(lastTime).append("</lst>")
					.append("</GetAllDataSync>")
					.append(SOAP_FOOTER) ;
		LogUtils.errorLog("GetAllDataSync",""+strXML);
		return strXML.toString();
	}
	//changePasswordRequest
	public static String changePasswordRequest(String UserName, String OldPassword, String NewPassword)
	{
		String strXML =SOAP_HEADER+ "<ChangePassword xmlns=\"http://tempuri.org/\">"+
							"<UserName>"+UserName+"</UserName>" +
							"<OldPassword>"+OldPassword+"</OldPassword>" +
							"<NewPassword>"+NewPassword+"</NewPassword>" +
						"</ChangePassword>"+
						SOAP_FOOTER	;
		return strXML;
	}
	
	//getJourneyPlanReqest
	public static String getJourneyPlanReqest(String UserId, String Token, String Date)
	{
		String strXML = "<JourneyPlan>" +
							"<UserId>"+UserId+"</UserId>" +
							"<strToken>"+Token+"</strToken>" +
							"<Date>"+Date+"</Date>" +
						"</JourneyPlan>";
		return strXML;
	}
	
	//getPreseller Messages
	public static String getPresellerMessages(String UserId, String strSyncTime)
	{
		LogUtils.errorLog("getPresellerMessages - ", ""+strSyncTime);
		String strXML = SOAP_HEADER + "<getMessages xmlns=\"http://tempuri.org/\">"+
							"<UserId>"+UserId+"</UserId>" +
							"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</getMessages>"+
						SOAP_FOOTER ;
		return strXML;
	}
	public static String getAllPriceWithSync(String strEmpId, String lsd, String lst)
	{
		String strXML = SOAP_HEADER+
						"<GetAllHHPriceWithSync xmlns=\"http://tempuri.org/\">"+
						"<UserCode>"+strEmpId+"</UserCode>"+
					    "<lsd>"+lsd+"</lsd>"+
					    "<lst>"+lst+"</lst>"+
					    "</GetAllHHPriceWithSync>"+
						SOAP_FOOTER;
		return strXML;
	}
	public static String getAllRegions(String strSyncTime)
	{
		LogUtils.errorLog("getAllRegions - ", ""+strSyncTime);
		String strXML = SOAP_HEADER + 
		 				"<GetAllLocations xmlns=\"http://tempuri.org/\">"+
	      				"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
	      				"</GetAllLocations>"+
						SOAP_FOOTER ;
		return strXML;
	}
	//getAllItems for products
		public static String getAllItems(String strSyncTime)
		{
			LogUtils.errorLog("getAllItems - ", ""+strSyncTime);
			String strXML = SOAP_HEADER +
							"<GetAllItems xmlns=\"http://tempuri.org/\">"+
						    "<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
							"</GetAllItems>"+
							SOAP_FOOTER ;
			return strXML;
		}
	
	
	//sendMessageRequest
	public static String deleteMessageRequest(String messagesIds,String userId)
	{
		String strXML = SOAP_HEADER+    
						"<DeleteMessages xmlns=\"http://tempuri.org/\">"+
						"<MessagesIds>"+messagesIds+"</MessagesIds>"+
						"<UserId>"+userId+"</UserId>"+
						"</DeleteMessages>"+
	      				SOAP_FOOTER;
		return strXML;
	}

	//AddCustomerNotesRequest
	public static String addCustomerNotesRequest(String PreSellerId, String CustomerId, String Subject, String Description, String Image, String Date)
	{
		String strXML = SOAP_HEADER+
							"<AddCustomerNotesRequest>" +
								"<PreSellerId>"+PreSellerId+"</PreSellerId>" +
								"<CustomerId>"+CustomerId+"</CustomerId>" +
								"<Subject>"+Subject+"</Subject>" +
								"<Description>"+Description+"</Description>" +
								"<Image>"+Image+"</Image>" +
								"<Date>"+Date+"</Date>" +
							"</AddCustomerNotesRequest>"+
						SOAP_FOOTER;
		
		return strXML;
	}
	
	public static String getMasterDate(String empNo)
	{
		String strXML = SOAP_HEADER+
						"<GetMasterDataFile xmlns=\"http://tempuri.org/\">" +
						"<UserCode>"+empNo+"</UserCode>"+
						 "<lsd>0</lsd>"+
					      "<lst>0</lst>"+
						"</GetMasterDataFile>"+
	      				SOAP_FOOTER;;
	      				LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	public static String getDiscount(String salemanCode, String lsd,  String lst)
	{
		String strXML = SOAP_HEADER+
				 "<GetDiscounts xmlns=\"http://tempuri.org/\">"+
				      "<UserCode>"+salemanCode+"</UserCode>"+
				      "<lsd>"+lsd+"</lsd>"+
				      "<lst>"+lst+"</lst>"+
				    "</GetDiscounts>"+
	      				SOAP_FOOTER;;
	      				LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	public static String getCustomerGroupById(String UserId, String strSyncTime)
	{
		LogUtils.errorLog("getCustomerGroupById - ", "getCustomerGroupById"+strSyncTime);
		String strXML =  SOAP_HEADER+"<GetCustomerGroupByUserId xmlns=\"http://tempuri.org/\">" +
							"<UserId>"+UserId+"</UserId>" +
							"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetCustomerGroupByUserId>"+
	      				SOAP_FOOTER;;
	      				LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	
	public static String getetCustomersByUserIdWithoutInvoice(String UserId, String strSyncTime)
	{
		LogUtils.errorLog("GetCustomersByUserIdWithoutInvoice  - ", ""+strSyncTime);
		String strXML =  SOAP_HEADER+
						"<GetCustomersByUserIdWithoutInvoice xmlns=\"http://tempuri.org/\">" +
						"<UserId>"+UserId+"</UserId>" +
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetCustomersByUserIdWithoutInvoice>"+
						SOAP_FOOTER;;
		return strXML;
	}
	public static String getetCustomersPendingInvoice(String UserId, String strSyncTime)
	{
		LogUtils.errorLog("GetPendingSalesInvoice   - ", "strSyncTime - "+strSyncTime);
		String strXML =  SOAP_HEADER+
						"<GetPendingSalesInvoice xmlns=\"http://tempuri.org/\">" +
						"<UserId>"+UserId+"</UserId>" +
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetPendingSalesInvoice>"+
						SOAP_FOOTER;;
		return strXML;
	}
	public static String getTopSelling(String Selesman, String strSyncTime)
	{
		LogUtils.errorLog("getTopSelling - ", ""+strSyncTime);
		String strXML =  SOAP_HEADER+
						"<GetTopSellingItems xmlns=\"http://tempuri.org/\">" +
						"<SALESMAN>"+Selesman+"</SALESMAN>" +
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetTopSellingItems>"+
						SOAP_FOOTER;;
		return strXML;
	}
	
	public static String getMustHave(String Selesman, String strSyncTime)
	{
		LogUtils.errorLog("getMustHave - ", ""+strSyncTime);
		String strXML =  SOAP_HEADER+
						"<GetMustHaveItems xmlns=\"http://tempuri.org/\">" +
						"<SALESMAN>"+Selesman+"</SALESMAN>" +
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetMustHaveItems>"+
						SOAP_FOOTER;;
		return strXML;
	}

	//method to get the Journey plan by Salesman Id
	public static String getJourneyPlan(String SalesmanId, String strSyncTime)
	{
		LogUtils.errorLog("getJourneyPlan - ", ""+strSyncTime);
		String strXML =  SOAP_HEADER+"<GetBeats xmlns=\"http://tempuri.org/\">"+
						"<SalesManCode>"+SalesmanId+"</SalesManCode>"+
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetBeats>"+
						SOAP_FOOTER;;
		return strXML;
	}
	
	//AddNotes
	public static String inserNotes(NotesObject objNotesObject)
	{
		String strXML = SOAP_HEADER+
							"<InsertNote xmlns=\"http://tempuri.org/\">" +
								"<CustomerId>"+objNotesObject.Customer_ID+"</CustomerId>" +
								"<PresellerId>"+objNotesObject.Emp_Id+"</PresellerId>" +
								"<Subject>"+objNotesObject.Note_Title+"</Subject>" +
								"<Description>"+objNotesObject.Note_Description+"</Description>" +
								"<Image>"+objNotesObject.image+"</Image>" +
							"</InsertNote>"+
						SOAP_FOOTER;
		
		return strXML;
	}

	 //AllRoles
	  public static String getAllRoles()
	  {
		String strXML = SOAP_HEADER + "<GetAllRoles xmlns=\"http://tempuri.org/\" />"+
		SOAP_FOOTER ;
		return strXML;
	  }
		
	//AllUsers
	public static String getAllUsers(String strSyncTime)
	{
		LogUtils.errorLog("getAllUsers - ", ""+strSyncTime);
		String strXML = SOAP_HEADER +
		  			   "<GetAllUsers xmlns=\"http://tempuri.org/\">"+
	      			   "<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
	      			   "</GetAllUsers>"+
				SOAP_FOOTER ;
	    return strXML;
	}
	
	
	//PresellerTargets
	public static String getPresellerTargets(String strSyncTime)
	{
		LogUtils.errorLog("getPresellerTargets - ", ""+strSyncTime);
		String strXML = SOAP_HEADER +
		  			   "<GetPreSalerAchievedTargetBySalesmancode xmlns=\"http://tempuri.org/\">"+
	      			   "<Salesmancode>"+strSyncTime+"</Salesmancode>"+
	      			   "</GetPreSalerAchievedTargetBySalesmancode>"+
				SOAP_FOOTER ;
	    return strXML;
	}
	
	// Customer History
	public static String getCustomerHistory(String EmpNo)
	{
		LogUtils.errorLog("getCustomerHistory - ", ""+EmpNo);
		String strXML = SOAP_HEADER +
		  			   "<getCustomerHistory xmlns=\"http://tempuri.org/\">"+
	      			   "<SalesManCode>"+EmpNo+"</SalesManCode>"+
	      			   "</getCustomerHistory>"+
				SOAP_FOOTER ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
	    return strXML;
	}
	
	// Customer History
	public static String getCustomerHistoryWithSync(String EmpNo,String LastSync)
	{
		LogUtils.errorLog("getCustomerHistorywithSync - ", "getCustomerHistorywithSync "+LastSync);
		String strXML = SOAP_HEADER +
		  			   "<getCustomerHistorywithSync xmlns=\"http://tempuri.org/\">"+
	      			   "<SalesManCode>"+EmpNo+"</SalesManCode>"+
	      			   "<LastSync>"+LastSync+"</LastSync>"+
	      			   "</getCustomerHistorywithSync>"+
				SOAP_FOOTER ;
		LogUtils.errorLog("strXML", "strXML"+strXML);
	    return strXML;
	}
	
	//Competetor Survey
	public static String getCompetitorDetail(String empNo, String lastSyncDate)
	{
		String strXML = SOAP_HEADER+
						"<GetCompetitorDetail xmlns=\"http://tempuri.org/\">"+
	    				"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
	    				"<EmpId>"+empNo+"</EmpId>"+
	    				"</GetCompetitorDetail>"+
						SOAP_FOOTER;
		LogUtils.errorLog("DeleteReceiptDetailsFromApp",""+strXML);
		return strXML;
	}
	
	public static String getAllMovement(String empNo, String lastSyncDate)
	{
		String strXML = SOAP_HEADER+
						"<GetAllMovements_Sync  xmlns=\"http://tempuri.org/\">"+
						"<UserCode>"+empNo+"</UserCode>"+
	    				"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
	    				"</GetAllMovements_Sync >"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getCorrectInprocess(String MovementCode)
	{
		String strXML = SOAP_HEADER+
						"<GetAppCorrectInProcessResponse  xmlns=\"http://tempuri.org/\">"+
						"<MovementCode>"+MovementCode+"</MovementCode>"+
	    				"</GetAppCorrectInProcessResponse>"+
						SOAP_FOOTER;
		return strXML;
	}
	public static String getActiveStatus(String empNo, String lsd, String lst)
	{
		String strXML = SOAP_HEADER+
						"<GetAppActiveStatus  xmlns=\"http://tempuri.org/\">"+
						"<UserCode>"+empNo+"</UserCode>"+
	    				"<lsd>"+lsd+"</lsd>"+
	    				"<lst>"+lst+"</lst>"+
	    				"</GetAppActiveStatus>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	// Customer History
	public static String getAllTransferDate(String salesmanCode,String LastSync)
	{
		String strXML = SOAP_HEADER +
		  			   "<GetAllTransfers xmlns=\"http://tempuri.org/\">"+
		  			   "<SalesmanCode>"+salesmanCode+"</SalesmanCode>"+
	      			   "<Type>"+1+"</Type>"+
	      			   "<LastSyncDate>"+LastSync+"</LastSyncDate>"+
	      			   "</GetAllTransfers>"+
				SOAP_FOOTER ;
	    return strXML;
	}
	//AllUsers
	
	public static String sendAllOrders(Vector<OrderDO> vecCompleteOrders, String strEmpId)
	{
		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<PostTrxDetailsFromXMLWithAuth xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>";
  							  		for(OrderDO orderDO : vecCompleteOrders)
  							  		{
  							  			if(TextUtils.isEmpty(orderDO.CurrencyCode))
  							  				orderDO.CurrencyCode = AppConstants.CURRENCY_CODE;
  							  			
								  		strOrder  = strOrder+"<TrxHeaderDco>" +
								  		"<OrderId></OrderId>" +
								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
								  		"<AppId>"+orderDO.strUUID+"</AppId>" +
								  		"<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>" +
								  		"<VisitCode>"+orderDO.VisitCode+"</VisitCode>" +
								  		"<EmpNo>"+orderDO.empNo+"</EmpNo>" +
								  		"<Site_Number>"+orderDO.CustomerSiteId+"</Site_Number>" +
								  		"<Site_Name>"+URLEncoder.encode(""+orderDO.strCustomerName)+"</Site_Name>" +
								  		"<Order_Date>"+orderDO.InvoiceDate+"</Order_Date>" +
								  		"<Order_Type>"+orderDO.orderType+"</Order_Type>" +
								  		"<OrderType>"+orderDO.orderSubType+"</OrderType>" +
								  		"<OrderBookingType>"+orderDO.orderSubType+"</OrderBookingType>" +
								  		"<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>" +
								  		"<PaymentType>"+orderDO.PaymentType+"</PaymentType>" +
								  		"<TrxReasonCode>"+orderDO.TrxReasonCode+"</TrxReasonCode>" +
								  		"<RemainingAmount>"+"0"+"</RemainingAmount>" +
								  		"<TotalAmount>"+orderDO.TotalAmount+"</TotalAmount>" +
								  		"<TotalDiscountAmount>"+orderDO.Discount+"</TotalDiscountAmount>" +
								  		"<TotalTAXAmount>"+"0"+"</TotalTAXAmount>" +
								  		"<ReferenceCode>"+"0"+"</ReferenceCode>" +
//								  		"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +      //orderDO.strCustomerSign
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +     //+orderDO.strPresellerSign
								  		"<Status>"+"0"+"</Status>" +
								  		"<VisitID>"+orderDO.VisitCode+"</VisitID>" +
								  		"<VehicleCode>"+orderDO.vehicleNo+"</VehicleCode>" +
								  		"<DeliveredBy>"+orderDO.salesmanCode+"</DeliveredBy>" +
								  		"<FreeNote>"+"N/A"+"</FreeNote>" +
								  		"<PreTrxCode>"+"0"+"</PreTrxCode>" +
								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
								  		"<ConsolidatedTrxCode>"+"0"+"</ConsolidatedTrxCode>" +
								  		"<BranchPlantCode>"+"0"+"</BranchPlantCode>" +
								  		"<PaymentCode>"+orderDO.PaymentCode+"</PaymentCode>" +
								  		"<ApproveByCode>"+"0"+"</ApproveByCode>" +
								  		"<ApprovedDate>"+orderDO.InvoiceDate+"</ApprovedDate>" +
								  		"<LPOCode>"+URLEncoder.encode(""+orderDO.LPOCode)+"</LPOCode>" +
								  		"<TrxSequence>"+orderDO.OrderId+"</TrxSequence>" +
								  		"<StampImage></StampImage>" +
								  		"<DeliveryDate>"+orderDO.DeliveryDate+"</DeliveryDate>" +
								  		"<StampDate>"+orderDO.InvoiceDate+"</StampDate>" +
								  		"<UserCreditAccountCode>"+"0"+"</UserCreditAccountCode>" +
								  		"<PushedOn>"+orderDO.InvoiceDate+"</PushedOn>" +
								  		"<SettlementCode>"+"0"+"</SettlementCode>" +
								  		"<ShippedQuantity>"+"0"+"</ShippedQuantity>" +
								  		"<CancelledQuantity>"+"0"+"</CancelledQuantity>" +
								  		"<InProcessQuantity>"+"0"+"</InProcessQuantity>"+ 
										"<TRANSACTION_TYPE_KEY>"+orderDO.TRANSACTION_TYPE_KEY+"</TRANSACTION_TYPE_KEY>"+ 
										"<TRANSACTION_TYPE_NAME>"+orderDO.TRANSACTION_TYPE_VALUE+"</TRANSACTION_TYPE_NAME>"+
										"<Batch_Source_Name>"+orderDO.Batch_Source_Name+"</Batch_Source_Name>"+ 
										"<Cust_Trx_Type_Name>"+orderDO.Trx_Type_Name+"</Cust_Trx_Type_Name>"+
										"<RoundOffValue>"+orderDO.roundOffVal+"</RoundOffValue>"+
										"<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
										"<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
										"<VatAmount>"+orderDO.VatAmount+"</VatAmount>"+
										"<TotalAmountWithVat>"+orderDO.TotalAmountWithVat+"</TotalAmountWithVat>"+
										"<ProrataTaxAmount>"+orderDO.ProrataTaxAmount+"</ProrataTaxAmount>"+
										"<TotalTax>"+orderDO.TotalTax+"</TotalTax>"+
								  		getProductDetails(orderDO.vecProductDO, orderDO)+
										getProductTAXDetailsNew(orderDO.vecProductDO,orderDO)+
								  		getProductPromotions(orderDO.vecProductDOPromotions)+
								  		getProductImages(orderDO.vecProductDO)+
    							  		"</TrxHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</PostTrxDetailsFromXMLWithAuth>"+
	      							  SOAP_FOOTER ;
	      					/*try {
								//LogUtils.convertRequestToFile(strXML);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
	    return strXML;
	}
	private static String getProductTAXDetailsNew(
			Vector<ProductDO>  arrTrxDetailsDOs, OrderDO orderDO ) {
		StringBuilder strXML = new StringBuilder();
		strXML.append("<SalesOrderTaxDcos>");

		if (orderDO != null && orderDO.arrOrderwiseAppliedTax.size() > 0) {
			// ArrayList<OrderWiseTaxViewDO> arrOrderwiseAppliedTax
			for (OrderWiseTaxViewDO orderwiseAppliedTaxDo : orderDO.arrOrderwiseAppliedTax) {
				{
					try {
						strXML.append("<SalesOrderTaxDco>")

								.append("<Id>")
								.append("0")
								.append("</Id>")
								.append("<UID>")
								.append(""+ StringUtils.getUniqueUUID())
								.append("</UID>")
								.append("<SalesOrderUID>")
								.append(""+orderDO.OrderId)
								.append("</SalesOrderUID>")
								.append("<SalesOrderLineUID>")
								.append("0")
								.append("</SalesOrderLineUID>")
								.append("<TaxUID>")
								.append(""+orderwiseAppliedTaxDo.TaxUID)
								.append("</TaxUID>")
								.append("<TaxSlabUID>")
								.append(""+orderwiseAppliedTaxDo.TaxSlabUID)
								.append("</TaxSlabUID>")
								.append("<TaxAmount>")
								.append(""+orderwiseAppliedTaxDo.TaxAmount)
								.append("</TaxAmount>")
								.append("<TaxName>")
								.append(""+orderwiseAppliedTaxDo.TaxName)
								.append("</TaxName>")
								.append("<ApplicableAt>")
								.append(""+orderwiseAppliedTaxDo.ApplicableAt)
								.append("</ApplicableAt>")
								.append("<DependentTaxUID>")
								.append(""+orderwiseAppliedTaxDo.DependentTaxUID)
								.append("</DependentTaxUID>")
								.append("<DependentTaxName>")
								.append("")
								.append("</DependentTaxName>")
								.append("<TaxCalculationType>")
								.append(""+orderwiseAppliedTaxDo.TaxCalculationType)
								.append("</TaxCalculationType>")
								.append("<BaseTaxRate>")
								.append(""+orderwiseAppliedTaxDo.BaseTaxRate)
								.append("</BaseTaxRate>")
								.append("<RangeStart>")
								.append(""+orderwiseAppliedTaxDo.RangeStart)
								.append("</RangeStart>")
								.append("<RangeEnd>")
								.append(""+orderwiseAppliedTaxDo.RangeEnd)
								.append("</RangeEnd>")
								.append("<TaxRate>")
								.append(""+orderwiseAppliedTaxDo.TaxRate)
								.append("</TaxRate>")
								.append("</SalesOrderTaxDco>");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (arrTrxDetailsDOs != null && arrTrxDetailsDOs.size() > 0) {
			for (ProductDO trxDetailsDO : arrTrxDetailsDOs) {
				ArrayList<ItemWiseTaxViewDO> arrAppliedTaxes =trxDetailsDO.arrAppliedTaxes;
				for(ItemWiseTaxViewDO itemWiseTaxViewDO:arrAppliedTaxes){
					try {
						strXML.append("<SalesOrderTaxDco>")

								.append("<Id>")
								.append("0")
								.append("</Id>")
								.append("<UID>")
								.append(""+""+StringUtils.getUniqueUUID())
								.append("</UID>")
								.append("<SalesOrderUID>")
								.append(""+orderDO.OrderId)
								.append("</SalesOrderUID>")
								.append("<SalesOrderLineUID>")
								.append(""+trxDetailsDO.LineNo)
								.append("</SalesOrderLineUID>")
								.append("<TaxUID>")
								.append(""+itemWiseTaxViewDO.TaxUID)
								.append("</TaxUID>")
								.append("<TaxSlabUID>")
								.append(""+itemWiseTaxViewDO.TaxSlabUID)
								.append("</TaxSlabUID>")
								.append("<TaxAmount>")
								.append(""+itemWiseTaxViewDO.TaxAmount)
								.append("</TaxAmount>")
								.append("<TaxName>")
								.append(""+itemWiseTaxViewDO.TaxName)
								.append("</TaxName>")
								.append("<ApplicableAt>")
								.append(""+itemWiseTaxViewDO.ApplicableAt)
								.append("</ApplicableAt>")
								.append("<DependentTaxUID>")
								.append(""+itemWiseTaxViewDO.DependentTaxUID)
								.append("</DependentTaxUID>")
								.append("<DependentTaxName>")
								.append("")
								.append("</DependentTaxName>")
								.append("<TaxCalculationType>")
								.append(""+itemWiseTaxViewDO.TaxCalculationType)
								.append("</TaxCalculationType>")
								.append("<BaseTaxRate>")
								.append(""+itemWiseTaxViewDO.BaseTaxRate)
								.append("</BaseTaxRate>")
								.append("<RangeStart>")
								.append(""+itemWiseTaxViewDO.RangeStart)
								.append("</RangeStart>")
								.append("<RangeEnd>")
								.append(""+itemWiseTaxViewDO.RangeEnd)
								.append("</RangeEnd>")
								.append("<TaxRate>")
								.append(""+itemWiseTaxViewDO.TaxRate)
								.append("</TaxRate>")
								.append("</SalesOrderTaxDco>");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		strXML.append("</SalesOrderTaxDcos>");
		return strXML.toString();
	}
	public static String sendAllLPOOrders(Vector<OrderDO> vecCompleteOrders, String strEmpId)
	{
		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<PostLPODetailsFromXMLWithAuth xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>";
  							  		for(OrderDO orderDO : vecCompleteOrders)
  							  		{
  							  			if(TextUtils.isEmpty(orderDO.CurrencyCode))
  							  				orderDO.CurrencyCode = AppConstants.CURRENCY_CODE;
  							  			
								  		strOrder  = strOrder+"<TrxHeaderDco>" +
								  		"<OrderId></OrderId>" +
								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
								  		"<AppId>"+orderDO.strUUID+"</AppId>" +
								  		"<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>" +
								  		"<VisitCode>"+orderDO.VisitCode+"</VisitCode>" +
								  		"<EmpNo>"+orderDO.empNo+"</EmpNo>" +
								  		"<Site_Number>"+orderDO.CustomerSiteId+"</Site_Number>" +
								  		"<Site_Name>"+URLEncoder.encode(""+orderDO.strCustomerName)+"</Site_Name>" +
								  		"<Order_Date>"+orderDO.InvoiceDate+"</Order_Date>" +
								  		"<Order_Type>"+orderDO.orderType+"</Order_Type>" +
								  		"<OrderType>"+orderDO.orderSubType+"</OrderType>" +
								  		"<OrderBookingType>"+orderDO.orderSubType+"</OrderBookingType>" +
								  		"<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>" +
								  		"<PaymentType>"+orderDO.PaymentType+"</PaymentType>" +
								  		"<TrxReasonCode>"+orderDO.TrxReasonCode+"</TrxReasonCode>" +
								  		"<RemainingAmount>"+"0"+"</RemainingAmount>" +
								  		"<TotalAmount>"+orderDO.TotalAmount+"</TotalAmount>" +
								  		"<TotalDiscountAmount>"+orderDO.Discount+"</TotalDiscountAmount>" +
								  		"<TotalTAXAmount>"+"0"+"</TotalTAXAmount>" +
								  		"<ReferenceCode>"+"0"+"</ReferenceCode>" +
//								  		"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +
//								  		"<CustomerSignature></CustomerSignature>" +
//								  		"<SalesmanSignature></SalesmanSignature>" +
								  		"<Status>"+"0"+"</Status>" +
								  		"<VisitID>"+orderDO.VisitCode+"</VisitID>" +
								  		"<VehicleCode>"+orderDO.vehicleNo+"</VehicleCode>" +
								  		"<DeliveredBy>"+orderDO.salesmanCode+"</DeliveredBy>" +
								  		"<FreeNote>"+"N/A"+"</FreeNote>" +
								  		"<PreTrxCode>"+"0"+"</PreTrxCode>" +
								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
								  		"<ConsolidatedTrxCode>"+"0"+"</ConsolidatedTrxCode>" +
								  		"<BranchPlantCode>"+"0"+"</BranchPlantCode>" +
								  		"<PaymentCode>"+orderDO.PaymentCode+"</PaymentCode>" +
								  		"<ApproveByCode>"+"0"+"</ApproveByCode>" +
								  		"<ApprovedDate>"+orderDO.InvoiceDate+"</ApprovedDate>" +
								  		"<LPOCode>"+URLEncoder.encode(""+orderDO.LPOCode)+"</LPOCode>" +
								  		"<TrxSequence>"+orderDO.OrderId+"</TrxSequence>" +
								  		"<StampImage></StampImage>" +
//								  		"<StampImage>"+URLEncoder.encode(""+orderDO.StampImage)+"</StampImage>" +
								  		"<DeliveryDate>"+orderDO.DeliveryDate+"</DeliveryDate>" +
								  		"<StampDate>"+orderDO.InvoiceDate+"</StampDate>" +
								  		"<UserCreditAccountCode>"+"0"+"</UserCreditAccountCode>" +
								  		"<PushedOn>"+orderDO.InvoiceDate+"</PushedOn>" +
								  		"<SettlementCode>"+"0"+"</SettlementCode>" +
								  		"<ShippedQuantity>"+"0"+"</ShippedQuantity>" +
								  		"<CancelledQuantity>"+"0"+"</CancelledQuantity>" +
								  		"<InProcessQuantity>"+"0"+"</InProcessQuantity>"+ 
										"<TRANSACTION_TYPE_KEY>"+orderDO.TRANSACTION_TYPE_KEY+"</TRANSACTION_TYPE_KEY>"+ 
										"<TRANSACTION_TYPE_NAME>"+orderDO.TRANSACTION_TYPE_VALUE+"</TRANSACTION_TYPE_NAME>"+
										"<Batch_Source_Name>"+orderDO.Batch_Source_Name+"</Batch_Source_Name>"+ 
										"<Cust_Trx_Type_Name>"+orderDO.Trx_Type_Name+"</Cust_Trx_Type_Name>"+
										"<RoundOffValue>"+orderDO.roundOffVal+"</RoundOffValue>"+
										"<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
								  		getProductDetails(orderDO.vecProductDO, orderDO)+
								  		getProductPromotions(orderDO.vecProductDOPromotions)+
								  		getProductImages(orderDO.vecProductDO)+
    							  		"</TrxHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</PostLPODetailsFromXMLWithAuth>"+
	      							  SOAP_FOOTER ;
	      					try {
								LogUtils.convertRequestToFile(strXML);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	    return strXML;
	}
	public static String updateLPOOrder(Vector<OrderDO> vecCompleteOrders, String strEmpId)
	{
		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<UpdateMTDeliveryStatus xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>";
  							  		for(OrderDO orderDO : vecCompleteOrders)
  							  		{
  							  			if(TextUtils.isEmpty(orderDO.CurrencyCode))
  							  				orderDO.CurrencyCode = AppConstants.CURRENCY_CODE;
								  		strOrder  = strOrder+"<TrxHeaderDco>" +
								  		"<OrderId></OrderId>" +
								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
								  		"<AppId>"+orderDO.strUUID+"</AppId>" +
								  		"<EmpNo>"+orderDO.empNo+"</EmpNo>" +
								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
//								  		"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +
//								  		"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +
								  		"<Status>"+"0"+"</Status>" +
								  		"<DeliveredBy>"+orderDO.salesmanCode+"</DeliveredBy>" +
										"<LPOStatus>"+AppStatus.LPO_STATUS_UPDATED+"</LPOStatus>"+
    							  		"</TrxHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</UpdateMTDeliveryStatus>"+
	      							  SOAP_FOOTER ;
	      					LogUtils.errorLog("strXML", ""+strXML);
	    return strXML;
	}
	
	public static String getHoldOrderStaus(String orderId, String appId)
	{
		String strXML = SOAP_HEADER + "<GetHoldOrderStatus xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>"+
								 "<TrxHeaderDco>"+	
  							  	 "<OrderId>"+orderId+"</OrderId>"+
  							     "<AppId>"+appId+"</AppId>"+
								 "</TrxHeaderDco>";
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</GetHoldOrderStatus>"+
	      							  SOAP_FOOTER ;
	      					LogUtils.errorLog("strXML", ""+strXML);
	    return strXML;
	}
	
	public static String completOnHoldOrdernew(OrderDO orderdo, String DateTime)
	{
		String strXML = SOAP_HEADER + 
								"<CompleteOnHoldOrder xmlns=\"http://tempuri.org/\">"+
  							  	"<OrderNumber>"+orderdo.OrderId+"</OrderNumber>"+
//  							  	"<CustomerSignature>"+URLEncoder.encode(""+orderdo.strCustomerSign)+"</CustomerSignature>" +
//  							  	"<SalesmanSignature>"+URLEncoder.encode(""+orderdo.strPresellerSign)+"</SalesmanSignature>" +
  							  	"<OrderDate>"+DateTime+"</OrderDate>"+
	      					    "</CompleteOnHoldOrder>"+
	      						SOAP_FOOTER ;
	    return strXML;
	}
	public static String postSignatureXML(OrderDO orderdo, String DateTime,String trxType,String signType)
	{
		String strXML = SOAP_HEADER +
								"<PostSignature  xmlns=\"http://tempuri.org/\">"+
  							  	"<TrxNo>"+orderdo.OrderId+"</TrxNo>"+
  							  	"<TrxType>"+trxType+"</TrxType>" +
  							  	"<SignatureType>"+signType+"</SignatureType>" +
  							  	"<ImagePath>"+orderdo.strCustomerSign+"</ImagePath>" +
	      					    "</PostSignature >"+
	      						SOAP_FOOTER ;
	    return strXML;
	}
	public static String completOnHoldOrder(String OrderId, String DateTime)
	{
		String strXML = SOAP_HEADER + 
								"<CompleteOnHoldOrder xmlns=\"http://tempuri.org/\">"+
  							  	"<OrderNumber>"+OrderId+"</OrderNumber>"+
  							  	"<CustomerSignature>"+""+"</CustomerSignature>" +
							  	"<SalesmanSignature>"+""+"</SalesmanSignature>" +
  							  	"<OrderDate>"+DateTime+"</OrderDate>"+
	      					    "</CompleteOnHoldOrder>"+
	      						SOAP_FOOTER ;
	    return strXML;
	}
	
	public static String postAssetCategory(AssetDo_New assetDo_New)
	{
		String strXML = SOAP_HEADER + 
								"<PostAssetCategory xmlns=\"http://tempuri.org/\">"+
								" <objAssetCategory>"+
								"<Id>"+assetDo_New.assetId+"</Id>"+
						        "<Site>"+assetDo_New.Site+"</Site>"+
						        "<Level1>"+assetDo_New.assetCatLevel1+"</Level1>"+
						        "<Level2>"+assetDo_New.assetCatLevel2+"</Level2>"+
						        "<Level3>"+assetDo_New.assetCatLevel3+"</Level3>"+
						        "<Level4>"+assetDo_New.assetCatLevel4+"</Level4>"+
						        "<Level5>"+assetDo_New.assetCatLevel5+"</Level5>"+
						        "<Status>"+assetDo_New.status+"</Status>"+
						        "<AssetName>"+assetDo_New.assetName+"</AssetName>"+
						        "<UserCode>"+assetDo_New.UserCode+"</UserCode>"+
						        "<ModifiedDate></ModifiedDate>"+
						        "<ModifiedTime></ModifiedTime>"+
						        "</objAssetCategory>"+
						        "</PostAssetCategory>"+
						        SOAP_FOOTER ;
				
		return strXML;
	}
	
//	 <objOrderImages>
//     <OrderImageDco xsi:nil="true" />
//     <OrderImageDco xsi:nil="true" />
//   </objOrderImages>
	public static String UpdateDeliveryStatus(List<AdvanceOrderDO> vecCompleteOrders, String strEmpId)
	{
		DecimalFormat df = new DecimalFormat("##.##");
		LogUtils.errorLog("strEmpId", "strEmpId - "+strEmpId);
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<UpdateDeliveryStatus xmlns=\"http://tempuri.org/\">";
				String strBody = "<objTrxHeaderDcos>";
  							  		for(AdvanceOrderDO orderDO : vecCompleteOrders)
  							  		{
								  		strOrder  = strOrder+"<TrxHeaderDco>" +
//								  		"<OrderId></OrderId>" +
								  		"<OrderNumber>"+orderDO.OrderId+"</OrderNumber>" +
								  		"<AppId>"+orderDO.AppId+"</AppId>" +
//								  		"<JourneyCode></JourneyCode>" +
//								  		"<VisitCode></VisitCode>" +
//								  		"<EmpNo></EmpNo>" +
//								  		"<Site_Number></Site_Number>" +
//								  		"<Order_Date></Order_Date>" +
//								  		"<Order_Type></Order_Type>" +
//								  		"<OrderType></OrderType>" +
//								  		"<OrderBookingType></OrderBookingType>" +
//								  		"<CurrencyCode></CurrencyCode>" +
//								  		"<PaymentType></PaymentType>" +
//								  		"<TrxReasonCode></TrxReasonCode>" +
//								  		"<RemainingAmount>"+"0"+"</RemainingAmount>" +
//								  		"<TotalAmount></TotalAmount>" +
//								  		"<TotalDiscountAmount></TotalDiscountAmount>" +
//								  		"<TotalTAXAmount>"+"0"+"</TotalTAXAmount>" +
//								  		"<ReferenceCode>"+"0"+"</ReferenceCode>" +
////								  	"<CustomerSignature>"+URLEncoder.encode(""+orderDO.strCustomerSign)+"</CustomerSignature>" +
////								  	"<SalesmanSignature>"+URLEncoder.encode(""+orderDO.strPresellerSign)+"</SalesmanSignature>" +
//								  		"<CustomerSignature></CustomerSignature>" +
//								  		"<SalesmanSignature></SalesmanSignature>" +
//								  		"<Status>"+"0"+"</Status>" +
//								  		"<VisitID></VisitID>" +
//								  		"<FreeNote>"+"N/A"+"</FreeNote>" +
//								  		"<PreTrxCode>"+"0"+"</PreTrxCode>" +
								  		"<TRXStatus>"+orderDO.TRXStatus+"</TRXStatus>" +
//								  		"<ConsolidatedTrxCode>"+"0"+"</ConsolidatedTrxCode>" +
//								  		"<BranchPlantCode>"+"0"+"</BranchPlantCode>" +
//								  		"<PaymentCode></PaymentCode>" +
//								  		"<ApproveByCode>"+"0"+"</ApproveByCode>" +
//								  		"<ApprovedDate></ApprovedDate>" +
//								  		"<LPOCode></LPOCode>" +
//								  		"<TrxSequence>"+orderDO.OrderId+"</TrxSequence>" +
//								  		"<StampImage></StampImage>" +
//								  		"<DeliveryDate></DeliveryDate>" +
//								  		"<StampDate></StampDate>" +
//								  		"<UserCreditAccountCode>"+"0"+"</UserCreditAccountCode>" +
//								  		"<PushedOn></PushedOn>" +
//								  		"<SettlementCode>"+"0"+"</SettlementCode>" +
//								  		"<ShippedQuantity>"+"0"+"</ShippedQuantity>" +
//								  		"<CancelledQuantity>"+"0"+"</CancelledQuantity>" +
//								  		"<InProcessQuantity>"+"0"+"</InProcessQuantity>"+ 
//								  		getProductDetails(orderDO.vecProductDO, orderDO)+
//							  			getProductPromotions(orderDO.vecProductDOPromotions)+
    							  		"</TrxHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objTrxHeaderDcos>"+
	      							  "</UpdateDeliveryStatus>"+
	      							  SOAP_FOOTER ;
	      					LogUtils.errorLog("strXML", ""+strXML);
	    return strXML;
	}
	
	private static String getProductDetails(Vector<ProductDO> vecProductDo, OrderDO orderDO)
	{
		int count = 1;
		String strXML = "";
		strXML = strXML + "<TrxDetails>";
		if(vecProductDo != null && vecProductDo.size() >0)
		{
			for (ProductDO productDO : vecProductDo)
			{
				if(productDO.LineNo == null || productDO.LineNo.length() <= 0)
					productDO.LineNo = ""+(vecProductDo.size() + count++);
				
				if(TextUtils.isEmpty(productDO.cases))
					productDO.cases = "0";
				
				productDO.Description  = URLEncoder.encode(""+productDO.Description);
				productDO.Description1 = URLEncoder.encode(""+productDO.Description1);
				productDO.remarks	   = URLEncoder.encode(""+productDO.remarks);
				productDO.LotNumber	   = URLEncoder.encode(""+productDO.LotNumber);
				
				String expDate = productDO.strExpiryDate;
				
				strXML = strXML + "<TrxDetailDco>"+
						"<LineNo>"+productDO.LineNo+"</LineNo>"+
						"<OrderNumber>"+productDO.OrderNo+"</OrderNumber>"+
						"<ItemCode>"+productDO.SKU+"</ItemCode>"+
						"<ReasonCode>"+(productDO.reason == null || productDO.reason.trim().equalsIgnoreCase("") ? "0" : productDO.reason) +"</ReasonCode>"+
						"<TrxDetailsNote></TrxDetailsNote>"+
						"<ItemType>"+productDO.ItemType+"</ItemType>"+
						"<BasePrice>"+productDO.itemPrice+"</BasePrice>"+
						"<UOM>"+productDO.UOM+"</UOM>"+
						"<Cases>"+productDO.cases+"</Cases>"+
						"<Units>"+productDO.units+"</Units>"+
						"<TotalUnits>0</TotalUnits>"+
						"<QuantityBU>0</QuantityBU>"+
						"<RequestedBU>0</RequestedBU>"+
						"<ApprovedBU>0</ApprovedBU>"+
						"<CollectedBU>0</CollectedBU>"+
						"<FinalBU>0</FinalBU>"+
						"<QuantitySU>0</QuantitySU>"+
						"<PriceDefaultLevel1>"+productDO.unitSellingPrice+"</PriceDefaultLevel1>"+
						"<PriceDefaultLevel2>0</PriceDefaultLevel2>"+
						"<PriceDefaultLevel3>0</PriceDefaultLevel3>"+
						"<PriceUsedLevel1>"+productDO.priceUsedLevel1+"</PriceUsedLevel1>"+
						"<PriceUsedLevel2>"+productDO.priceUsedLevel2+"</PriceUsedLevel2>"+
						"<PriceUsedLevel3>0</PriceUsedLevel3>"+
						"<TaxPercentage>"+productDO.TaxPercentage+"</TaxPercentage>"+
						"<TotalDiscountPercentage>0</TotalDiscountPercentage>"+
						"<TotalDiscountAmount>"+productDO.discountAmount+"</TotalDiscountAmount>"+
						"<CalculatedDiscountPercentage>0</CalculatedDiscountPercentage>"+
						"<CalculatedDiscountAmount>0</CalculatedDiscountAmount>"+
						"<UserDiscountPercentage>0</UserDiscountPercentage>"+
						"<UserDiscountAmount>0</UserDiscountAmount>"+
						"<ItemDescription>"+productDO.Description+"</ItemDescription>"+
						"<ItemAltDescription>"+productDO.Description1+"</ItemAltDescription>"+
						"<DistributionCode>0</DistributionCode>"+
						"<AffectedStock>0</AffectedStock>"+
						"<Status>0</Status>"+
						"<PromoID>"+productDO.ProductId+"</PromoID>"+
						"<PromoType>"+productDO.Description1+"</PromoType>"+
						"<TRXStatus>0</TRXStatus>"+
						"<ExpiryDate>"+expDate+"</ExpiryDate>"+
						"<RelatedLineID>"+0+"</RelatedLineID>"+
						"<RelatedItemCode>"+productDO.RelatedLineId+"</RelatedItemCode>"+
						"<ItemGroupLevel5></ItemGroupLevel5>"+
						"<TaxType>0</TaxType>"+
						"<SuggestedFreeBU>0</SuggestedFreeBU>"+
						"<PushedOn>"+orderDO.InvoiceDate+"</PushedOn>"+
						"<TrxReasonCode>"+productDO.reason+"</TrxReasonCode>"+
						"<EmptyJarQuantity>0</EmptyJarQuantity>"+
						"<PromotionLineNo>"+0+"</PromotionLineNo>"+
						"<PromotionCode>"+productDO.promoCode+"</PromotionCode>"+
						"<RecommendedQty>"+productDO.recomUnits+"</RecommendedQty>"+
						"<BatchNumber>"+productDO.BatchCode+"</BatchNumber>"+
						"<DiscountDiscription>"+productDO.discountDesc+"</DiscountDiscription>"+
						"<Remarks>"+productDO.remarks+"</Remarks>"+
						"<LotNo>"+productDO.LotNumber+"</LotNo>"+
						"<LineTaxAmount>"+productDO.LineTaxAmount+"</LineTaxAmount>"+
						"<ProrataTaxAmount>"+productDO.ProrataTaxAmount+"</ProrataTaxAmount>"+
						"<TotalTax>"+(productDO.LineTaxAmount+productDO.ProrataTaxAmount)+"</TotalTax>"+
						"<QuantityInStock>"+productDO.QuantityInStock+"</QuantityInStock>"+
						"<RefTrxCode>"+productDO.RefTrxCode+"</RefTrxCode>"+
						getDiscountDetails(productDO.arrDiscList)+
						"</TrxDetailDco>";	
			}
		}
		strXML = strXML +"</TrxDetails>";
		return strXML;
	}
	
	private static String getDiscountDetails(ArrayList<DiscountDO> vecDiscountDOs)
	{
		String strXML = "";
		if(vecDiscountDOs != null && vecDiscountDOs.size() >0)
		{
			strXML = strXML + "<objTrxDiscountDetailDcoLst>";
			for (DiscountDO discountDO : vecDiscountDOs)
			{
				strXML = strXML + "<TrxDiscountDetailDco>"+
						"<Quantity>"+discountDO.Quantity+"</Quantity>"+
						"<OrderNumber>"+discountDO.OrderNo+"</OrderNumber>"+
						"<Description>"+discountDO.description+"</Description>"+
						"<UOM>"+discountDO.UOM+"</UOM>"+
						"<DiscountLineNo>"+discountDO.lineNo+"</DiscountLineNo>"+
						"<ItemCode>"+discountDO.ItemCode+"</ItemCode>"+
						"<ItemLineNo>"+discountDO.ItemLineNo+"</ItemLineNo>"+
						"<DiscountAmount>"+discountDO.discount+"</DiscountAmount>"+
						"</TrxDiscountDetailDco>";	
			}
			strXML = strXML +"</objTrxDiscountDetailDcoLst>";
		}
		return strXML;
	}
	
	private static String getProductImages(Vector<ProductDO> vecProductDo)
	{
		String strXML = "";
		strXML = strXML + "<OrderImages>";
		if(vecProductDo != null && vecProductDo.size() >0)
		{
			for (ProductDO productDO : vecProductDo)
			{
				if(productDO.vecDamageImagesNew != null && productDO.vecDamageImagesNew.size() > 0)
				{
					for (DamageImageDO damageImageDO : productDO.vecDamageImagesNew) {
						strXML = strXML + "<OrderImageDco>"+
						"<OrderNo>"+damageImageDO.OrderNo+"</OrderNo>"+
						"<ItemCode>"+damageImageDO.ItemCode+"</ItemCode>"+
						"<LineNo>"+damageImageDO.LineNo+"</LineNo>"+
						"<ImagePath>"+damageImageDO.ImagePath+"</ImagePath>"+
						"<CapturedDate>"+damageImageDO.CapturedDate+"</CapturedDate>"+
						"</OrderImageDco>";
					}
				}
			}
		}
		strXML = strXML + "</OrderImages>";
		return strXML;
	}
	
	private static String getProductPromotions(Vector<ProductDO> vecProductDo)
	{
		String strXML = "";
		
		if(vecProductDo != null && vecProductDo.size() >0)
		{
			strXML = strXML + "<TrxPromotions>";
			for (ProductDO productDO : vecProductDo)
			{
				strXML = strXML + "<TrxPromotionDco>"+
						"<OrderNumber>"+productDO.OrderNo+"</OrderNumber>"+
						"<ItemCode>"+productDO.SKU+"</ItemCode>"+
						"<DiscountAmount>"+productDO.discountAmount+"</DiscountAmount>"+
						"<DiscountPercentage>"+productDO.Discount+"</DiscountPercentage>"+
						"<PromotionID>"+productDO.promotionId+"</PromotionID>"+
						"<Status>0</Status>"+
						"<OrderStatus>0</OrderStatus>"+
						"<PromotionType>"+productDO.promotionType+"</PromotionType>"+
						"<TrxDetailsLineNo></TrxDetailsLineNo>"+
						"<ItemType>"+productDO.ItemType+"</ItemType>"+
						"<IsStructural>"+productDO.IsStructural+"</IsStructural>"+
						"<PushedOn></PushedOn>"+
						"</TrxPromotionDco>";	
			}
			strXML = strXML + "</TrxPromotions>";
		}
		return strXML;
	}
	
	public static String createNewCustomerList(Vector<CustomerDO> vecNewCustomers)
	{
		String strCustomers="";
		
		for(CustomerDO customerDO :vecNewCustomers)
		{
			strCustomers = strCustomers+
//							 "&lt;Customer&gt;"+
//							 	"&lt;SiteId&gt;"+customerDO.customerSiteID+"&lt;/SiteId&gt;"+ 
//								"&lt;SALESMAN&gt;" +customerDO.presellerId+"&lt;/SALESMAN&gt;"+
//								"&lt;PARTY_NAME&gt;"+customerDO.customerName+"&lt;/PARTY_NAME&gt;"+
//								"&lt;REGION_CODE&gt;"+customerDO.regionCode+"&lt;/REGION_CODE&gt;"+ 
//								"&lt;REGION_DESCRIPTION&gt;"+customerDO.regionDescription+"&lt;/REGION_DESCRIPTION&gt;"+ 
//								"&lt;COUNTRY_CODE&gt;"+customerDO.countryCode+"&lt;/COUNTRY_CODE&gt;"+ 
//								"&lt;COUNTRY_DESCRIPTION&gt;"+customerDO.countryDescription+"&lt;/COUNTRY_DESCRIPTION&gt;"+ 
//								"&lt;ADDRESS1&gt;"+customerDO.customerStreet+"&lt;/ADDRESS1&gt;"+ 
//								"&lt;ADDRESS2&gt;"+customerDO.customerBuilding+"&lt;/ADDRESS2&gt;"+ 
//								"&lt;ADDRESS3&gt;"+customerDO.telephone_No+"&lt;/ADDRESS3&gt;"+ 
//								"&lt;ADDRESS4&gt;"+customerDO.fax_No+"&lt;/ADDRESS4&gt;"+ 
//								"&lt;PO_BOX_NUMBER&gt;"+customerDO.post_BoxNo+"&lt;/PO_BOX_NUMBER&gt;"+ 
//								"&lt;CITY&gt;"+customerDO.customerLocation+"&lt;/CITY&gt;"+
//								"&lt;PAYMENT_TYPE&gt;"+customerDO.payment_Type+"&lt;/PAYMENT_TYPE&gt;"+
//								"&lt;PAYMENT_TERM_CODE&gt;"+customerDO.payment_Terms_Code+"&lt;/PAYMENT_TERM_CODE&gt;"+ 
//								"&lt;PAYMENT_TERM_DESCRIPTION&gt;"+customerDO.payment_Terms+"&lt;/PAYMENT_TERM_DESCRIPTION&gt;"+ 
//								"&lt;CREDIT_LIMIT&gt;"+customerDO.credit_Limit+"&lt;/CREDIT_LIMIT&gt;"+ 
//								"&lt;GEO_CODE_X&gt;"+customerDO.Customer_Latitude+"&lt;/GEO_CODE_X&gt;"+ 
//								"&lt;GEO_CODE_Y&gt;"+customerDO.Customer_Longitude+"&lt;/GEO_CODE_Y&gt;" +
//								"&lt;EMAIL&gt;"+customerDO.email_Id+"&lt;/EMAIL&gt;" +	
//							    "&lt;CONTACTPERSONNAME&gt;"+customerDO.contactPersonName+"&lt;/CONTACTPERSONNAME&gt;"+
//							    "&lt;CONTACTPERSONMOBILENO&gt;"+customerDO.contactPersonMobileNumber+"&lt;/CONTACTPERSONMOBILENO&gt;"+
//							"&lt;/Customer&gt;";
						   "<Customer>"+
						   		"<SiteId>"+customerDO.customerSiteID+"</SiteId>"+ 
								"<SALESMAN>" +customerDO.presellerId+"</SALESMAN>"+
								"<PARTY_NAME>"+customerDO.customerName+"</PARTY_NAME>"+
								"<REGION_CODE>"+""+customerDO.regionCode+"</REGION_CODE>"+ 
								"<REGION_DESCRIPTION>"+""+customerDO.regionDescription+"</REGION_DESCRIPTION>"+ 
								"<COUNTRY_CODE>"+""+customerDO.countryCode+"</COUNTRY_CODE>"+ 
								"<COUNTRY_DESCRIPTION>"+""+customerDO.countryDescription+"</COUNTRY_DESCRIPTION>"+ 
								"<ADDRESS1>"+customerDO.customerStreet+"</ADDRESS1>"+ 
								"<ADDRESS2>"+customerDO.customerBuilding+"</ADDRESS2>"+ 
								"<ADDRESS3>"+customerDO.telephone_No+"</ADDRESS3>"+ 
								"<ADDRESS4>"+customerDO.fax_No+"</ADDRESS4>"+ 
								"<PO_BOX_NUMBER>"+customerDO.post_BoxNo+"</PO_BOX_NUMBER>"+ 
								"<CITY>"+customerDO.customerLocation+"</CITY>"+
								"<PAYMENT_TYPE>"+customerDO.payment_Type+"</PAYMENT_TYPE>"+
								"<PAYMENT_TERM_CODE>"+customerDO.payment_Terms_Code+"</PAYMENT_TERM_CODE>"+ 
								"<PAYMENT_TERM_DESCRIPTION>"+customerDO.payment_Terms+"</PAYMENT_TERM_DESCRIPTION>"+ 
								"<CREDIT_LIMIT>"+customerDO.credit_Limit+"</CREDIT_LIMIT>"+ 
								"<GEO_CODE_X>"+customerDO.Customer_Latitude+"</GEO_CODE_X>"+ 
								"<GEO_CODE_Y>"+customerDO.Customer_Longitude+"</GEO_CODE_Y>" +
								"<EMAIL>"+customerDO.email_Id+"</EMAIL>" +	
							    "<CONTACTPERSONNAME>"+customerDO.contactPersonName+"</CONTACTPERSONNAME>"+
							    "<CONTACTPERSONMOBILENO>"+customerDO.contactPersonMobileNumber+"</CONTACTPERSONMOBILENO>"+
							    "<AppKey>"+customerDO.strUUID+"</AppKey>"+
							"</Customer>";
		}
		String strXML = SOAP_HEADER +     
						"<InsertCustomer xmlns=\"http://tempuri.org/\">"+
							"<CustomerRequest>"+
							"<Customers>"+
								strCustomers+
							"</Customers>"+
							"</CustomerRequest>"+
						"</InsertCustomer>"+
	      				SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getAllNotesByUserAndCustomerId(String salesmanCode,String customerId, String strSyncTime)
	{
		LogUtils.errorLog("getAllNotesByUserAndCustomerId - ", ""+strSyncTime);
		String strXML = SOAP_HEADER +     
						"<GetNotes xmlns=\"http://tempuri.org/\">"+
	      				"<CustomerId>"+customerId+"</CustomerId>"+
	      				"<PresellerId>"+salesmanCode+"</PresellerId>"+
	      				"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
	      				"</GetNotes>"+
	      				SOAP_FOOTER ;
		return strXML;
	}
	public static String deleteNotes(String noteId)
	{
		String strXML = SOAP_HEADER +     
						"<DeleteNotes  xmlns=\"http://tempuri.org/\">"+
						"<NoteIds>"+noteId+"</NoteIds>"+
						"</DeleteNotes >"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String updateCustomerGeoLocation(String siteId, String lat, String lang)
	{
		String strXML = SOAP_HEADER +     
						"<UpdateClientLocation xmlns=\"http://tempuri.org/\">"+
						"<Site>"+siteId+"</Site>"+
						"<Latitude>"+lat+"</Latitude>"+
						"<Longitude>"+lang+"</Longitude>"+
						"</UpdateClientLocation>"+
						SOAP_FOOTER ;
		return strXML;
	}

	public static String getConfirmOrderDetailsforDA(String empNo,String currentDate)
	{
		String strXML = SOAP_HEADER +     
						"<getConfirmOrderDetailsforDA xmlns=\"http://tempuri.org/\">"+
						"<EmpNo>"+empNo+"</EmpNo>"+
						"<Date>"+currentDate+"</Date>"+
						"</getConfirmOrderDetailsforDA>"+
						SOAP_FOOTER ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	
	public static String getCustomerOrderHistoryBYDA (String empNo,String currentDate)
	{
		String strXML = SOAP_HEADER +     
						"<GetCustomerOrderHistoryBYDA xmlns=\"http://tempuri.org/\">"+
						"<DeliveryAgentId>"+empNo+"</DeliveryAgentId>"+
						"<Date>"+currentDate+"</Date>"+
						"</GetCustomerOrderHistoryBYDA>"+
						SOAP_FOOTER ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	
	
	public static String getDeliveryStatus(String empNo,String currentDate)
	{
		String strXML = SOAP_HEADER +     
						"<CheckDeliveryStatus xmlns=\"http://tempuri.org/\">"+
						"<EmpNo>"+empNo+"</EmpNo>"+
						"<Date>"+currentDate+"</Date>"+
						"</CheckDeliveryStatus>"+
						SOAP_FOOTER ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	public static String getAllReasons(String strSyncTime)
	{
		String strXML = SOAP_HEADER +     
						"<GetAllReasons xmlns=\"http://tempuri.org/\">"+
						"<LastSync>"+strSyncTime+"</LastSync>"+
						"</GetAllReasons>"+
						SOAP_FOOTER ;
		return strXML;
	}
	/**
	 * Method to update the Delivery status of orders by delivery agent 
	 * @return String
	 */
	public static String updateDeliveryStatus(Vector<DeliveryAgentOrderDetailDco> vecSalesOrders, String deliveredBy, boolean isEOT)
	{
		arrOrderNumbers = new ArrayList<String>();
		
		//need to change the object type here
		String strOrders = "";
		String strXML = SOAP_HEADER +     
		 				"<UpdateDeliveryStatus xmlns=\"http://tempuri.org/\">"+
						"<RequestString>"+
						"&lt;DeliveryStatusUpdateRequest&gt;"+
						"&lt;DeliveryDetails&gt;";
		
						for(DeliveryAgentOrderDetailDco objProduct : vecSalesOrders)
						{
							if(arrOrderNumbers != null && !arrOrderNumbers.contains(objProduct.blaseOrderNumber))
								arrOrderNumbers.add(objProduct.blaseOrderNumber);
							
							LogUtils.errorLog("blaseOrderNumber", "blaseOrderNumber - Sended"+objProduct.blaseOrderNumber);
							strOrders = strOrders+"&lt;DeliveryDetail&gt;"+
										"&lt;BLASE_ORDER_NO&gt;"+objProduct.blaseOrderNumber+"&lt;/BLASE_ORDER_NO&gt;"+
										"&lt;ITEM_CODE&gt;"+objProduct.itemCode+"&lt;/ITEM_CODE&gt;"+
										"&lt;BLASE_ORDER_STATUS&gt;"+"D"+"&lt;/BLASE_ORDER_STATUS&gt;"+
										"&lt;DELIVERED_QUANTITY&gt;"+objProduct.cases+"&lt;/DELIVERED_QUANTITY&gt;"+
										"&lt;ACTUAL_SHIP_DATE&gt;"+(!isEOT ? (CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"):(objProduct.actualShipedDate))+"&lt;/ACTUAL_SHIP_DATE&gt;"+
										"&lt;DeliveredBy&gt;"+deliveredBy+"&lt;/DeliveredBy&gt;"+
										"&lt;ORDER_INVOICE_NUMBER&gt;"+objProduct.orderInvoiceNumber+"&lt;/ORDER_INVOICE_NUMBER&gt;"+
										"&lt;/DeliveryDetail&gt;";
						}
			  strXML = 	strXML +strOrders+"&lt;/DeliveryDetails&gt;"+
			  					  		  "&lt;/DeliveryStatusUpdateRequest&gt;" +
			  					  		  "</RequestString>"+
			  					  		  "</UpdateDeliveryStatus>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	/**
	 * Method to update the Delivery status of orders by delivery agent 
	 * @return String
	 */
	public static String updateDeliveryStatus_New(Vector<DeliveryAgentOrderDetailDco> vecSalesOrders, String deliveredBy, boolean isEOT)
	{
		arrOrderNumbers = new ArrayList<String>();
		//need to change the object type here
		String strOrders = "";
		String strXML = SOAP_HEADER +     
		 				"<UpdateDeliveryStatus_New xmlns=\"http://tempuri.org/\">"+
						"<RequestString>"+
						"&lt;DeliveryStatusUpdateRequest&gt;"+
						"&lt;DeliveryDetails&gt;";
		
						for(DeliveryAgentOrderDetailDco objProduct : vecSalesOrders)
						{
							if(arrOrderNumbers != null && !arrOrderNumbers.contains(objProduct.blaseOrderNumber))
								arrOrderNumbers.add(objProduct.blaseOrderNumber);
							
//							String disString = ((StringUtils.getFloat(objProduct.recommendedQty) ==0 || objProduct.cases >= StringUtils.getFloat(objProduct.recommendedQty) || objProduct.cases >= StringUtils.getFloat(objProduct.actualQtyShiped)) ? objProduct.strDiscount : "0") ;
							
							String disString =objProduct.strDiscount  ;
							LogUtils.errorLog("blaseOrderNumber", "blaseOrderNumber - Sended"+objProduct.blaseOrderNumber);
							strOrders = strOrders+"&lt;DeliveryDetail&gt;"+
										"&lt;BLASE_ORDER_NO&gt;"+objProduct.blaseOrderNumber+"&lt;/BLASE_ORDER_NO&gt;"+
										"&lt;ITEM_CODE&gt;"+objProduct.itemCode+"&lt;/ITEM_CODE&gt;"+
										"&lt;BLASE_ORDER_STATUS&gt;"+"D"+"&lt;/BLASE_ORDER_STATUS&gt;"+
										"&lt;DELIVERED_QUANTITY&gt;"+objProduct.cases+"&lt;/DELIVERED_QUANTITY&gt;"+
										"&lt;ACTUAL_SHIP_DATE&gt;"+(!isEOT ? (CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"):((objProduct.actualShipedDate)))+"&lt;/ACTUAL_SHIP_DATE&gt;"+
										"&lt;DeliveredBy&gt;"+deliveredBy+"&lt;/DeliveredBy&gt;"+
										"&lt;ORDER_INVOICE_NUMBER&gt;"+objProduct.orderInvoiceNumber+"&lt;/ORDER_INVOICE_NUMBER&gt;"+
										"&lt;DISCOUNT&gt;"+(disString != null ? disString : "0")+"&lt;/DISCOUNT&gt;"+
										"&lt;/DeliveryDetail&gt;";
						}
			  strXML = 	strXML +strOrders+"&lt;/DeliveryDetails&gt;"+
			  					  		  "&lt;/DeliveryStatusUpdateRequest&gt;" +
			  					  		  "</RequestString>"+
			  					  		  "</UpdateDeliveryStatus_New>"+
						SOAP_FOOTER ;
			  LogUtils.errorLog("strXML", "strXML" + strXML);
		return strXML;
	}
	/**
	 * 
	 * @param strSyncTime
	 * @return String
	 */
	public static String getSurveyQuestion(String strSyncTime)
	{
		String strXML = SOAP_HEADER +     
						"<GetQuestionAnswer xmlns=\"http://tempuri.org/\">"+
						"<LastSync>"+strSyncTime+"</LastSync>"+
						"</GetQuestionAnswer>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public  static String generatePasscode(String preSellerId,String supervisorId)
	{

		String strXML = SOAP_HEADER +     
	    				"<GetPassCode xmlns=\"http://tempuri.org/\">"+
	    				"<SupervisorId>"+ supervisorId+"</SupervisorId>"+
	    				"<PreSellerId>"+ preSellerId+"</PreSellerId>"+
	    				"</GetPassCode>"+
						SOAP_FOOTER ;
		LogUtils.errorLog("strXML",""+strXML);
		return strXML;
	}
	
	
	//Check PassCode Status
	public static String insertEOT(String strEmpId, String strEOTType, String strReason, String strDateTime,String strOrders,String strInvoices, String strPayments, String signature)
	{
		String strXML = SOAP_HEADER+
		 				"<InsertEOT xmlns=\"http://tempuri.org/\">"+
	      				"<objEOTD>"+
	      				"<EmpNo>"+strEmpId+"</EmpNo>"+
	      				"<EOTType>"+strEOTType+"</EOTType>"+
	      				"<Reason>"+strReason+"</Reason>"+
	      				"<EOTTime>"+strDateTime+"</EOTTime>"+
	      				"<OrderNumber>"+strOrders+"</OrderNumber>"+
	      	            "<ReceiptNumber>"+strPayments+"</ReceiptNumber>"+
	      	            "<InvoiceNumber>"+strInvoices+"</InvoiceNumber>"+
//	      	            "<Signature></Signature>"+
	      	            "<Signature>"+URLEncoder.encode(""+signature)+"</Signature>"+
	      				"</objEOTD>"+
	      				"</InsertEOT>"+
						SOAP_FOOTER;
		LogUtils.errorLog("strXML - ", "strXML - "+strXML);
		return strXML;
	}
	//login request
	public static String getAllCategory(String strSyncTime)
	{
		String strXML = SOAP_HEADER+
						"<GetAllCategory xmlns=\"http://tempuri.org/\">"+
						"<LastSync>"+strSyncTime+"</LastSync>"+
						"</GetAllCategory>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getItemPricing(String strSyncTime)
	{
		String strXML = SOAP_HEADER+
	    				"<getPrice xmlns=\"http://tempuri.org/\">"+
	    				"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
	    				"</getPrice>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getFiveTopSellingItems(String empNo)
	{
		String strXML = SOAP_HEADER+
	    				"<GetTopFiveSellingItem xmlns=\"http://tempuri.org/\" >"+
	    				"<EmpNo>"+empNo+"</EmpNo>"+
	    				"</GetTopFiveSellingItem>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getSplashScreenDataforSync(String UserCode , String lsd, String lst)
	{
		LogUtils.errorLog("GetHHSplashScreenDataforSync","GetHHSplashScreenDataforSync "+ lsd);
		String strXML = SOAP_HEADER+
				
					"<GetHHSplashScreenDataforSync xmlns=\"http://tempuri.org/\">"+
					"<UserCode>"+UserCode+"</UserCode>"+
						"<lsd>"+lsd+"</lsd>"+
						"<lst>"+lst+"</lst>"+
						"</GetHHSplashScreenDataforSync>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getHouseholdMastersWithSync(String strSyncTime)
	{
		LogUtils.errorLog("GetHouseHoldMastersWithSync","GetHouseHoldMastersWithSync "+ strSyncTime);
		String strXML = SOAP_HEADER +
						"<GetHouseHoldMastersWithSync xmlns=\"http://tempuri.org/\">"+
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetHouseHoldMastersWithSync>"+ 
						SOAP_FOOTER;
      
		return strXML;
	}

	/**
	 * @author kishore
	 * Insert Recording
	 * @param site
	 * @param supervisorId
	 * @param recName
	 * @param filePath
	 * @return
	 */
	public static String insertRecording(String site, String supervisorId, String recName, String filePath)
	{
		String strXML = SOAP_HEADER +
							"<InsertRecording xmlns=\"http://tempuri.org/\">"+
							"<objRecording>"+
							"<Site>"+site+"</Site>"+
							"<SupervisorId>"+supervisorId+"</SupervisorId>"+
							"<RecordingName>"+recName+"</RecordingName>"+
							"<FilePath>"+filePath+"</FilePath>"+
							"</objRecording>"+
							"</InsertRecording>"+
						SOAP_FOOTER;
		LogUtils.errorLog("requestXML", strXML);
		return strXML;
	}
	
	/**
	 * Delete Recording
	 * @param recordingId
	 * @return
	 */
	public static String deleteRecording(String recordingId)
	{
		String strXML = SOAP_HEADER +
						"<DeleteRecording xmlns=\"http://tempuri.org/\">"+
						"<RecordingId>"+recordingId+"</RecordingId>"+
						"</DeleteRecording>"+
						SOAP_FOOTER;
		
		return strXML;
	}
	
	/**
	 * Update Recording
	 * @param recordingId
	 * @param newName
	 * @return
	 */
	public static String updateRecording(String recordingId, String newName)
	{
		String strXML = SOAP_HEADER +
						"<UpdateRecordingName xmlns=\"http://tempuri.org/\">"+
						"<RecordingId>"+recordingId+"</RecordingId>"+
						"<NewName>"+newName+"</NewName>"+
						"</UpdateRecordingName>"+
						SOAP_FOOTER;

		return strXML;
	}
	/**
	 * @param vecOptions
	 * @return String
	 */
	public static String postSurveyAnswer(String strCustomerSitId, String strPresellerId,String strSupervisorId, Vector<QuestionOptionDO> vecOptions)
	{
		String strBody ="";
		String strXML = SOAP_HEADER +     
		  				"<ImportCustomersSurvey xmlns=\"http://tempuri.org/\">"+
						"<CustomerSurveyRequest>"+
						"<CustomersSurvey>"+
						"<CustomerSurvey>"+
						"<Site>"+strCustomerSitId+"</Site>"+
						"<PresellerId>"+strPresellerId+"</PresellerId>"+
						"<SupervisorId>"+strSupervisorId+"</SupervisorId>"+
						"<Options>";
		
		 			for(QuestionOptionDO objQuestionOption : vecOptions)
		 			{
		 				strBody += "<Option>"+
		 		          		   "<QuestionId>"+objQuestionOption.questionId+"</QuestionId>"+
		 		          		   "<Question>"+objQuestionOption.questionDesc+"</Question>"+
		 		          		   "<OptionId>"+objQuestionOption.optionId+"</OptionId>"+
		 						   "<Option>"+objQuestionOption.optionDesc+"</Option>"+
		 		        		   "</Option>";
		 			}
		
		strXML = strXML+strBody+ "</Options>"+
	    						 "</CustomerSurvey>"+
								 "</CustomersSurvey>"+
	      						 "</CustomerSurveyRequest>"+
	    						 "</ImportCustomersSurvey>"+
								 SOAP_FOOTER ;
//		writeToSdcards(strXML);
		return strXML;
	}
	
//	public static void writeToSdcards(String strXML)
//	{
//		 try
//		 {
//			 File myFile = new File("/sdcard/request.xml");
//			 myFile.createNewFile();
//			 FileOutputStream fOut = new FileOutputStream(myFile);
//			 OutputStreamWriter myOutWriter = 	new OutputStreamWriter(fOut);
//			 myOutWriter.append(strXML);
//			 myOutWriter.close();
//			 fOut.close();
//		 } 
//		 catch (Exception e)
//		 {
//		 }
//	}
	/**
	 * Fetch all deleted items request
	 * @return String request.
	 */
	public static String getAllDeletedItems(String lastSync)
	{
		LogUtils.errorLog("GetAllHHDeletedItems","GetAllHHDeletedItems "+ lastSync);
		
		String strXML = SOAP_HEADER +
						"<GetAllHHDeletedItems  xmlns=\"http://tempuri.org/\">"+
						"<LastSync>"+lastSync+"</LastSync>"+
						"</GetAllHHDeletedItems >"+
						SOAP_FOOTER;
		
		return strXML;
	}
	
	public static String getAllPassCode(String preselerId)
	{
		LogUtils.errorLog("GetAllPassCode - ", "GetAllPassCode - "+preselerId);
		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<soap:Body>" +
						"<GetAllPassCode xmlns=\"http://tempuri.org/\">" +
							"<PreSellerId>"+preselerId+"</PreSellerId>" +
						"</GetAllPassCode>" +
					"</soap:Body>" +
				"</soap:Envelope>";
		return strXML;
	}
	
	public static String getAllVehicles(String empNo)
	{
		LogUtils.errorLog("getAllVehicles - ", "getAllVehicles - "+empNo);
		String strXML = SOAP_HEADER +
						"<getAllVehicles xmlns=\"http://tempuri.org/\">" +
							"<empNo>"+empNo+"</empNo>" +
						"</getAllVehicles>" +
						SOAP_FOOTER;
		return strXML;
	}
	
	public static String getAllAvailablePassCode(String empNo)
	{
		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<soap:Body>" +
						"<GetAllAvailablePassCode xmlns=\"http://tempuri.org/\">" +
							"<EmpId>"+empNo+"</EmpId>" +
						"</GetAllAvailablePassCode>" +
					"</soap:Body>" +
				"</soap:Envelope>";
		return strXML;
	}
	public static String getAllAvailableDAPassCode(String empNo)
	{
		String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<soap:Body>" +
						"<GetAllAvailableDAPassCodeRegionWise xmlns=\"http://tempuri.org/\">" +
							"<EmpId>"+empNo+"</EmpId>" +
						"</GetAllAvailableDAPassCodeRegionWise>" +
					"</soap:Body>" +
				"</soap:Envelope>";
		return strXML;
	}
	/**
	 * method to post the Scaned data
	 * @param objScanResultObject 
	 * @return String
	 */
	public static String importBarCodeInfo(ScanResultObject objScanResultObject)
	{
		String strXML = SOAP_HEADER +
						"<ImportBarCodeInfo xmlns=\"http://tempuri.org/\">"+
						"<BarcodeInfoRequest>"+
						"<Barcodes>"+
						"<Barcode>"+
						"<CustomerCode>"+objScanResultObject.customerSiteId+"</CustomerCode>"+
						"<Type>"+objScanResultObject.type+"</Type>"+
						"<Location>"+objScanResultObject.location+"</Location>"+
						"<BarCode>"+objScanResultObject.strBarcodeImage+"</BarCode>"+
						"<Comments>"+objScanResultObject.comments+"</Comments>"+
						"<ScanTime>"+objScanResultObject.time+"</ScanTime>"+
						"<ItemCode>"+objScanResultObject.productId+"</ItemCode>"+
						"<SalesmanCode>"+objScanResultObject.EmpId+"</SalesmanCode>"+
						"<Quantity>"+objScanResultObject.Quantity+"</Quantity>"+
						"</Barcode>"+
						"</Barcodes>"+
						"</BarcodeInfoRequest>"+
						"</ImportBarCodeInfo>"+
						SOAP_FOOTER;

		return strXML;
	}
	
	public static String securityCheckedCompleted(String truckNo)
	{
		String strBody = "";
		String strXML = SOAP_HEADER +
						"<SecurityCheckedCompleted xmlns=\"http://tempuri.org/\">"+
						"<TruckNumber>"+truckNo+"</TruckNumber>";
		strXML = strXML+strBody+
					"</SecurityCheckedCompleted>"+
			  SOAP_FOOTER;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	public static String logisticsCheckedCompleted(String truckNo)
	{
		String strBody = "";
		String strXML = SOAP_HEADER +
						"<LogisticsCheckCompleted xmlns=\"http://tempuri.org/\">"+
						"<OrderNumber>"+truckNo+"</OrderNumber>";
		strXML = strXML+strBody+
					"</LogisticsCheckCompleted>"+
			  SOAP_FOOTER;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	public static String getOrderPerDay(String salesManCode, String date)
	{
		String strXML = SOAP_HEADER +
						"<GetOrderPerDay xmlns=\"http://tempuri.org/\">"+
						"<SalesmanCode>"+salesManCode+"</SalesmanCode>"+
						"<FetchDate>"+date+"</FetchDate>"+
						"</GetOrderPerDay>"+
						SOAP_FOOTER;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	public static String getReceiptPerDay(String salesManCode, String date)
	{
		String strXML = SOAP_HEADER +
						"<GetReceiptPerDay xmlns=\"http://tempuri.org/\">"+
						"<SalesmanCode>"+salesManCode+"</SalesmanCode>"+
						"<FetchDate>"+date+"</FetchDate>"+
						"</GetReceiptPerDay>"+
						SOAP_FOOTER;
		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	public static String getJournyLogPerDay(String salesManCode)
	{
		String strXML = SOAP_HEADER +
						"<GetRoutePlanPerDay  xmlns=\"http://tempuri.org/\">"+
						"<SalesManCode>"+salesManCode+"</SalesManCode>"+
						"</GetRoutePlanPerDay >"+
						SOAP_FOOTER;

		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	public static String insertJournyLogPerDay(String salesManCode, String request)
	{
		String strXML = SOAP_HEADER +
						"<InsertRoutePlanPerDay   xmlns=\"http://tempuri.org/\">"+
						"<RequestString>"+request+"</RequestString>"+
						"<SalesManCode>"+salesManCode+"</SalesManCode>"+
						"</InsertRoutePlanPerDay  >"+
						SOAP_FOOTER;

		LogUtils.errorLog("strXML", "strXML"+strXML);
		return strXML;
	}
	
	 
	public static String prepareJournyPlanXml(ArrayList<MallsDetails> vecJourneyLog)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("&lt;JournyLogs&gt;");
		for(MallsDetails planDO :vecJourneyLog)
		{
			builder.append("&lt;JournyLog&gt;").
			append("&lt;DateOfJourney&gt;").append(planDO.dateofJorney).append("&lt;/DateOfJourney&gt;").
			append("&lt;PresellerId&gt;").append(planDO.presellerId).append("&lt;/PresellerId&gt;").
			append("&lt;CustomerSiteId&gt;").append(planDO.customerSiteId).append("&lt;/CustomerSiteId&gt;").
			append("&lt;CustomerPasscode&gt;").append(planDO.customerPasscode).append("&lt;/CustomerPasscode&gt;").
			append("&lt;Stop&gt;").append(planDO.Stop).append("&lt;/Stop&gt;").
			append("&lt;Distance&gt;").append(planDO.Distance).append("&lt;/Distance&gt;").
			append("&lt;TravelTime&gt;").append(planDO.TravelTime).append("&lt;/TravelTime&gt;").
			append("&lt;ArrivalTime&gt;").append(planDO.ActualArrivalTime).append("&lt;/ArrivalTime&gt;").
			append("&lt;SeviceTime&gt;").append(planDO.SeviceTime).append("&lt;/SeviceTime&gt;").
			append("&lt;ReasonForSkip&gt;").append(planDO.reasonForSkip).append("&lt;/ReasonForSkip&gt;").
			append("&lt;OutTime&gt;").append(planDO.ActualOutTime).append("&lt;/OutTime&gt;").
			append("&lt;TotalTimeAtOutLet&gt;").append(planDO.TotalTime).append("&lt;/TotalTimeAtOutLet&gt;").
			append("&lt;/JournyLog&gt;");
		}
		builder.append("&lt;/JournyLogs&gt;");
		
		return builder.toString();
	}
	
	//method to get all the EOT for supervisor
	public static String insertCustomerVisit(ArrayList<MallsDetails> vecJourneyLog,String empNo)
	{
		String strBody = "";
		String strXML =  SOAP_HEADER+
		 				"<InsertCustomerVisit xmlns=\"http://tempuri.org/\">" +
		 				"<ImportCustomerVisit>" +
		 				"<CustomerVisits>";
						
		for(MallsDetails planDO :vecJourneyLog)
		{
			strBody =strBody+"<CustomerVisit>" +
							 "<EmpNo>"+empNo+"</EmpNo>"+
   							 "<CustomerSiteId>"+planDO.customerSiteId+"</CustomerSiteId>"+
   							 "<Date>"+CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00"+"</Date>"+
   							 "<ArrivalTime>"+planDO.ActualArrivalTime+"</ArrivalTime>"+
   							 "<OutTime>"+(planDO.ActualOutTime!=null?planDO.ActualOutTime:planDO.ActualArrivalTime)+"</OutTime>"+
   							 "<TotalTimeInMins>"+(planDO.TotalTime!=null ? planDO.TotalTime : "1")+"</TotalTimeInMins>" +
   							 "</CustomerVisit>";
		}

		strXML = strXML	+
				 strBody+
				 "</CustomerVisits>" +
				 "</ImportCustomerVisit>"+
				"</InsertCustomerVisit>"+
				SOAP_FOOTER;
		
		LogUtils.errorLog("getJourneyDetails", "getJourneyDetails "+strXML);
		return strXML;
	}
	
	//method to get all the EOT for supervisor
	public static String getEOTDetails(String empNo, String currentDate)
	{
		String strXML =  SOAP_HEADER+
		 				"<getEOTDetails xmlns=\"http://tempuri.org/\">"+
						"<EmpNo>"+empNo+"</EmpNo>"+
						"<DATE>"+currentDate+"</DATE>"+
						"</getEOTDetails>"+
						SOAP_FOOTER;
		
		LogUtils.errorLog("getEOTDetails", "getEOTDetails "+strXML);
		return strXML;
	}
	
	public static String prepareLogisticMismatchXml(ArrayList<DeliveryAgentOrderDetailDco> vector, String reason, String orderNumber)
	{
		String strDiscrepency = ""; 
		for(DeliveryAgentOrderDetailDco obj : vector)
		{
			strDiscrepency += "<Discrepancy>" +
					"<OrderNumber>"+orderNumber+"</OrderNumber>" +
					"<OrderItem>"+obj.itemCode+"</OrderItem>" +
					"<ExpectedQuantity>"+obj.preCases+"</ExpectedQuantity>" +
					"<ActualQuantity>"+obj.cases+"</ActualQuantity>" +
					"</Discrepancy>";
		}
		String strXML = SOAP_HEADER +
				" <LogisticSupervisorMismatch xmlns=\"http://tempuri.org/\">" +
				"<LogisticSupervisorDiscrepancy>" +
				"<Reason>"+reason+"</Reason>" +
				"<Discrepancies>" +
				strDiscrepency +
				"</Discrepancies>" +
				"</LogisticSupervisorDiscrepancy>" +
				"</LogisticSupervisorMismatch>" +
				SOAP_FOOTER;
		
		LogUtils.errorLog("request XML", "strXML"+strXML);
		
		return strXML;
	}
	
	//login request
	public static String updatePasscodeStatus(String strPresellerId, String passcode)
	{
		String strXML = SOAP_HEADER+
				 		"<UpdateDAPassCodeStatus xmlns=\"http://tempuri.org/\">"+
	     				"<EmpNo>"+strPresellerId+"</EmpNo>"+
				 		"<PassCode>"+passcode+"</PassCode>"+
				 		"</UpdateDAPassCodeStatus>"+
						SOAP_FOOTER;
		
		Log.e("strXML - ", "strXML - "+strXML);
		return strXML;
	}
	
	public static String insertCustomerGeoCode(JourneyPlanDO journeyPlanDO , String EmpNo)
	{
		String strXML = SOAP_HEADER+
		 			"<InsertCustomerGeoCode xmlns=\"http://tempuri.org/\">"+
		 				"<CustomerGeoCodeRequest>"+
		 				"<Customers>"+
		 					"<Customer>"+
		 						"<SiteNumber>"+journeyPlanDO.site+"</SiteNumber>"+
		 						"<EmpNo>"+EmpNo+"</EmpNo>"+
		 						"<GEO_CODE_X>"+journeyPlanDO.geoCodeX+"</GEO_CODE_X>"+
		 						"<GEO_CODE_Y>"+journeyPlanDO.geoCodeY+"</GEO_CODE_Y>"+
		 					"</Customer>"+
		 				"</Customers>"+
		 			"</CustomerGeoCodeRequest>"+
		 		  "</InsertCustomerGeoCode>"+
						SOAP_FOOTER;
		LogUtils.errorLog("strXML", ""+strXML);
		return strXML;
	}
	public static String getPasscodeForDa(String EmpNo)
	{
		String strXML = SOAP_HEADER+
							"<GetAllAvailableDAPassCode xmlns=\"http://tempuri.org/\">"+
 							"<EmpId>"+EmpNo+"</EmpId>"+
 							"</GetAllAvailableDAPassCode>"+
	     				SOAP_FOOTER;
		LogUtils.errorLog("strXML", ""+strXML);
		return strXML;
	}
	
	public static String insertHHCustomer(Vector<NewCustomerDO> vecNewCustomerDO, String route)
	{
		
		String strHHCustomer = "";
		
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			NewCustomerDO obj = vecNewCustomerDO.get(i);
			
			strHHCustomer = strHHCustomer+"<HHCustomerDco>"+
								"<SiteId>"+obj.CustomerSiteId+"</SiteId>"+
								"<SALESMAN>"+obj.salesman+"</SALESMAN>"+
								"<SITE_NAME>"+obj.customerName+"</SITE_NAME>"+
								"<PARTY_NAME>"+obj.customerName+"</PARTY_NAME>"+
								"<REGION_CODE>"+obj.region+"</REGION_CODE>"+
								"<REGION_DESCRIPTION>"+obj.region+"</REGION_DESCRIPTION>"+
								"<COUNTRY_CODE>"+obj.countryName+"</COUNTRY_CODE>"+
								"<COUNTRY_DESCRIPTION>"+obj.countryDesc+"</COUNTRY_DESCRIPTION>"+
								"<ADDRESS1>"+obj.address1+"</ADDRESS1>"+
								"<ADDRESS2>"+obj.address2+"</ADDRESS2>"+
								"<ADDRESS3></ADDRESS3>"+
								"<ADDRESS4></ADDRESS4>"+
								"<PO_BOX_NUMBER></PO_BOX_NUMBER>"+
								"<CITY>"+obj.CITY+"</CITY>"+
								"<PAYMENT_TYPE></PAYMENT_TYPE>"+
								"<PAYMENT_TERM_CODE></PAYMENT_TERM_CODE>"+
								"<PAYMENT_TERM_DESCRIPTION></PAYMENT_TERM_DESCRIPTION>"+
								"<CREDIT_LIMIT>0</CREDIT_LIMIT>"+
								"<GEO_CODE_X>"+obj.latitude+"</GEO_CODE_X>"+
								"<GEO_CODE_Y>"+obj.longitude+"</GEO_CODE_Y>"+
								"<EMAIL>"+obj.email+"</EMAIL>"+
								"<CONTACTPERSONNAME>"+obj.contactPerson+"</CONTACTPERSONNAME>"+
								"<CONTACTPERSONMOBILENO>"+obj.mobileNo+"</CONTACTPERSONMOBILENO>"+
								"<AppKey>"+obj.AppUUID+"</AppKey>"+
								"<MOBILENO1>"+obj.landline+"</MOBILENO1>"+
								"<CountryId>"+obj.countryId+"</CountryId>"+
								"<DOB>"+obj.DOB+"</DOB>"+
								"<AnniversaryDate>"+obj.anniversary+"</AnniversaryDate>"+
								"<Source>"+route+"</Source>"+
								"<customerType>"+1+"</customerType>"+
								"<PASSCODE>"+12345+"</PASSCODE>"+ 
								"<Buyer>"+obj.buyerStatus+"</Buyer>"+ 
								"<CompetitionBrand>"+obj.competitionBrand+"</CompetitionBrand>"+ 
								"<SKU>"+obj.sku+"</SKU>"+ 
							"</HHCustomerDco>";
		}
		
		String strXML = SOAP_HEADER +
						"<InsertHHCustomerOffline xmlns=\"http://tempuri.org/\">"+
						"<objImportCustomer>"+
						"<Customers>"+
						strHHCustomer +
						"</Customers>"+
					    "</objImportCustomer>"+
					    "</InsertHHCustomerOffline>"+
						SOAP_FOOTER;
		
		LogUtils.errorLog("error", strXML);
		if(strXML.contains("&"))
			strXML.replace("&", "&amp;");
		
		return strXML;
	}
	
	public static String updateHHCustomerGEO(Vector<MallsDetails> vecNewCustomerDO)
	{
		String strHHCustomer = "";
		for(int i = 0; i < vecNewCustomerDO.size(); i++)
		{
			MallsDetails obj = vecNewCustomerDO.get(i);
			
			strHHCustomer = "<HHCustomerDco>"+
								"<SiteId>"+obj.customerSiteId+"</SiteId>"+
								"<GEO_CODE_X>"+obj.Latitude+"</GEO_CODE_X>"+
								"<GEO_CODE_Y>"+obj.Longitude+"</GEO_CODE_Y>"+
							"</HHCustomerDco>";
		}
		
		String strXML = SOAP_HEADER +
				 		"<UpdateHHCustomer xmlns=\"http://tempuri.org/\">"+
						"<objImportCustomer>"+
						"<Customers>"+
						strHHCustomer +
						"</Customers>"+
					    "</objImportCustomer>"+
					    "</UpdateHHCustomer>"+
						SOAP_FOOTER;
		
		LogUtils.errorLog("error", strXML);
		
		if(strXML.contains("&"))
			strXML.replace("&", "&amp;");
		
		return strXML;
	}
	
	public static String getSalesManLandmarkSynce(String salesmanCode, String lastSyncDate)
	{
		LogUtils.errorLog("GetSalesmanLandmarkWithSync - ", "GetSalesmanLandmarkWithSync "+lastSyncDate);
		String strXML = SOAP_HEADER +
						"<GetSalesmanLandmarkWithSync xmlns=\"http://tempuri.org/\">"+
						"<SalesmanCode>"+salesmanCode+"</SalesmanCode>"+
						"<LastSyncDate>"+lastSyncDate+"</LastSyncDate>"+
						"</GetSalesmanLandmarkWithSync>"+
						SOAP_FOOTER;
		return strXML;
	}
	
	/**
	 * Method to build request to get all Vehicles
	 * @return String
	 */
	public static String getVehicles(String empId , String lsd, String lst)
	{
		LogUtils.errorLog("getVehicles - ", "getVehicles - "+lsd+" - "+lst);
		String strXML = SOAP_HEADER + 
		 				"<GetVehicles xmlns=\"http://tempuri.org/\">"+
		 				"<UserCode>"+empId+"</UserCode>"+
		 				"<lsd>"+lsd+"</lsd>"+
		 				"<lst>"+lst+"</lst>"+
		 				"</GetVehicles>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	/**
	 * Method to build request to get all Inventory
	 * @param strSyncTime
	 * @return String
	 */
	public static String getVMInventory(String strEmpNo , String strSyncTime, String strCurrentDate)
	{
		LogUtils.errorLog("getVMInventory - ", "getVMInventory - "+strSyncTime);
		String strXML = SOAP_HEADER + 
						"<GetVMSalesmanInventory xmlns=\"http://tempuri.org/\">"+
	      				"<LastSyncDate></LastSyncDate>"+
	      				"<Empno>"+strEmpNo+"</Empno>"+
	      				"<Date>"+strCurrentDate+"</Date>"+
	      				"</GetVMSalesmanInventory>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	/**
	 * Method to build request to get all Vehicles
	 * @return String
	 */
	public static String getSequenceNoBySalesmanForHH(String strSalesmanNo)
	{
		LogUtils.errorLog("GetSequenceNoBySalesmanForHH - ", "GetSequenceNoBySalesmanForHH "+strSalesmanNo);
		String strXML = SOAP_HEADER + 
		 				"<GetAvailableTrxNos xmlns=\"http://tempuri.org/\">"+
		 				"<UserCode>"+strSalesmanNo+"</UserCode>"+
		 				"</GetAvailableTrxNos>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getHHSummary(String empNo, String strSyncTime, String currentdate)
	{
		LogUtils.errorLog("getAllRegions - ", ""+strSyncTime);
		String strXML =   SOAP_HEADER + 
						  "<GetVMSummary xmlns=\"http://tempuri.org/\">"+
					      "<LastSync>"+strSyncTime+"</LastSync>"+
					      "<EmpNo>"+empNo+"</EmpNo>"+
					      "<CurrentDate>"+currentdate+"</CurrentDate>"+
					      "</GetVMSummary>"+
						  SOAP_FOOTER ;
		return strXML;
	}
	public static String getAllHHCustomerDeletedItems(String empNo, String strSyncTime)
	{
		LogUtils.errorLog("GetAllHHCustomerDeletedItems - ", "GetAllHHCustomerDeletedItems - "+strSyncTime);
		String strXML =   SOAP_HEADER + 
							"<GetAllHHCustomerDeletedItems xmlns=\"http://tempuri.org/\">"+
							"<LastSync>"+strSyncTime+"</LastSync>"+
							"<Empno>"+empNo+"</Empno>"+
							"</GetAllHHCustomerDeletedItems>"+
						  SOAP_FOOTER ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String insertStock(String empNo, ArrayList<DeliveryAgentOrderDetailDco> arrayList)
	{
		String body = "";
		String strXML = SOAP_HEADER + 
				 		"<InsertStock xmlns=\"http://tempuri.org/\">"+
	      				"<objImportStock>"+
	      				"<Inventories>";
		for(DeliveryAgentOrderDetailDco detailDco : arrayList)
		{
			body = body + 
					"<VMSalesmanInventoryDco>"+
					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
					"<Date>"+CalendarUtils.getOrderPostDate()+"</Date>"+
					"<EmpNo>"+empNo+"</EmpNo>"+
					"<ItemCode>"+detailDco.itemCode+"</ItemCode>"+
					"<PrimaryQuantity>"+detailDco.preCases+"</PrimaryQuantity>"+
					"<SecondaryQuantity>"+detailDco.preUnits+"</SecondaryQuantity>"+
					"</VMSalesmanInventoryDco>";
		}
		strXML = 	strXML+body+ 
					"</Inventories>"+
					"</objImportStock>"+
	      			"</InsertStock>"+
				    SOAP_FOOTER ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String insertCheckInDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
	{
		String body = "";
		String strXML = SOAP_HEADER + 
				 		"<ImportCheckInDemandStock xmlns=\"http://tempuri.org/\">"+
	      				"<objImportStock>"+
	      				"<Inventories>";
		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
		{
			body = body + 
					"<VMSalesmanInventoryDco>"+
					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
					"<Date>"+checkInDemandInventoryDO.Date+"</Date>"+
					"<EmpNo>"+checkInDemandInventoryDO.EmpNo+"</EmpNo>"+
					"<ItemCode>"+checkInDemandInventoryDO.ItemCode+"</ItemCode>"+
					
//					"<PrimaryQuantity>"+detailDco.totalCases+"</PrimaryQuantity>"+
//					"<SecondaryQuantity>"+detailDco.totalCases+"</SecondaryQuantity>"+
					
					//Changed by awaneesh
					"<PrimaryQuantity>"+checkInDemandInventoryDO.PrimaryQuantity+"</PrimaryQuantity>"+
					"<SecondaryQuantity>"+checkInDemandInventoryDO.SecondaryQuantity+"</SecondaryQuantity>"+
					"</VMSalesmanInventoryDco>";
		}
		strXML = 	strXML+body+ 
					"</Inventories>"+
					"</objImportStock>"+
	      			"</ImportCheckInDemandStock>"+
				    SOAP_FOOTER ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String insertDemandStock(Vector<CheckInDemandInventoryDO> vecInDemandInventoryDOs)
	{
		String body = "";
		String strXML = SOAP_HEADER + 
				 		"<InsertStock xmlns=\"http://tempuri.org/\">"+
	      				"<objImportStock>"+
	      				"<Inventories>";
		for(CheckInDemandInventoryDO checkInDemandInventoryDO : vecInDemandInventoryDOs)
		{
			body = body + 
					"<VMSalesmanInventoryDco>"+
					"<VMSalesmanInventoryId>"+0+"</VMSalesmanInventoryId>"+
					"<Date>"+checkInDemandInventoryDO.Date+"</Date>"+
					"<EmpNo>"+checkInDemandInventoryDO.EmpNo+"</EmpNo>"+
					"<ItemCode>"+checkInDemandInventoryDO.ItemCode+"</ItemCode>"+
					
//					"<PrimaryQuantity>"+detailDco.totalCases+"</PrimaryQuantity>"+
//					"<SecondaryQuantity>"+detailDco.totalCases+"</SecondaryQuantity>"+
					
					//Changed by awaneesh
					"<PrimaryQuantity>"+checkInDemandInventoryDO.PrimaryQuantity+"</PrimaryQuantity>"+
					"<SecondaryQuantity>"+checkInDemandInventoryDO.SecondaryQuantity+"</SecondaryQuantity>"+
					"</VMSalesmanInventoryDco>";
		}
		strXML = 	strXML+body+ 
					"</Inventories>"+
					"</objImportStock>"+
	      			"</InsertStock>"+
				    SOAP_FOOTER ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String getAdvanceOrderByEmpNo(String empNo, String strSyncTime)
	{
		LogUtils.errorLog("GetAdvanceOrderByEmpNo - ", "GetAdvanceOrderByEmpNo - "+strSyncTime);
		String strXML =   	SOAP_HEADER + 
							"<GetAdvanceOrderByEmpNo xmlns=\"http://tempuri.org/\">"+
							"<EmpNo>"+empNo+"</EmpNo>"+
							"</GetAdvanceOrderByEmpNo>"+
							SOAP_FOOTER ;
		
		LogUtils.errorLog("strXML", "strXML - "+strXML);
		return strXML;
	}
	
	public static String updateReturnStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
	{
		String strXML =   	SOAP_HEADER + 
							"<UpdateReturnStock xmlns=\"http://tempuri.org/\">"+
							"<objImportStock>"+
							"<Inventories>"+getVMSalesmanInventoryDcos(vecOrdProduct, strEmpNo)+"</Inventories>"+
							"</objImportStock>"+
							"</UpdateReturnStock>"+
							SOAP_FOOTER ;
		return strXML;
	}
	
	private static String getVMSalesmanInventoryDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo)
	{
		String strXML = "";
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
		{
			strXML += "<VMSalesmanInventoryDco>"+
					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
					 "<Date>"+CalendarUtils.getOrderPostDate()+"</Date>"+
					 "<EmpNo>"+strEmpNo+"</EmpNo>"+
					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
					 "<PrimaryQuantity>0</PrimaryQuantity>"+
					 "<SecondaryQuantity>0</SecondaryQuantity>"+
					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
					 "<FeederEmpNo>0</FeederEmpNo>"+
					 "</VMSalesmanInventoryDco>";
		}
		return strXML;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String getVMSalesmanInventoryDcoINUpload(TransferInoutDO transferInoutDO)
	{
		String strXML = "";
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
		{
			strXML += "<VMSalesmanInventoryDco>"+
					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
					 "<EmpNo>"+transferInoutDO.fromEmpNo+"</EmpNo>"+
					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
					 "<PrimaryQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryQuantity>"+
					 "<SecondaryQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryQuantity>"+
					 "<PrimaryReturnQuantity>0</PrimaryReturnQuantity>"+
					 "<SecondaryReturnQuantity>0</SecondaryReturnQuantity>"+
					 "<FeederEmpNo>"+transferInoutDO.toEmpNo+"</FeederEmpNo>"+
					 "</VMSalesmanInventoryDco>";
		}
		return strXML;
	}
//	private static String getVMSalesmanInventoryDcoIN(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strInEmpNo)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
//		{
//			strXML += "<VMSalesmanInventoryDco>"+
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
//					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
//					 "<EmpNo>"+strEmpNo+"</EmpNo>"+
//					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
//					 "<PrimaryQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryQuantity>"+
//					 "<SecondaryQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryQuantity>"+
//					 "<PrimaryReturnQuantity>0</PrimaryReturnQuantity>"+
//					 "<SecondaryReturnQuantity>0</SecondaryReturnQuantity>"+
//					 "<FeederEmpNo>"+strInEmpNo+"</FeederEmpNo>"+
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML;
//	}
//	public static String UpdateTransferInStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strInEmpNo)
//	{
//		String strXML =   	SOAP_HEADER + 
//		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
//		"<objTransferInOrOutStock>"+
//		"<Inventories>"+getVMSalesmanInventoryDcoIN(vecOrdProduct, strEmpNo, strInEmpNo)+"</Inventories>"+
//		"</objTransferInOrOutStock>"+
//		"</UpdateTransferInOrOutStock>"+
//		SOAP_FOOTER ;
//		return strXML;
//	}
	
	
	public static String PostTransferOuts(Vector<TransferInoutDO> vecTransferInoutDOs)
	{
		String strXML =   	SOAP_HEADER + 
		"<PostTransfers xmlns=\"http://tempuri.org/\">"+
		"<objTransferHeaderDcos>"+
		getTransferHeaderDCO(vecTransferInoutDOs)+
		"</objTransferHeaderDcos>"+
		"</PostTransfers>"+
		SOAP_FOOTER ;
		return strXML;
	}
	
	private static String getTransferHeaderDCO(Vector<TransferInoutDO> vecTransferInoutDOs)
	{
		String strXML = "";
		for (TransferInoutDO transferInoutDO : vecTransferInoutDOs) 
		{
			String transferType = "";
			if(transferInoutDO.trnsferType.equalsIgnoreCase("IN"))
				transferType = "1";
			else
				transferType = "0";
			strXML += "<TransferHeaderDco>"+
			"<TransferId>"+transferInoutDO.InventoryUID+"</TransferId>"+
			"<SourceEmpCode>"+transferInoutDO.fromEmpNo+"</SourceEmpCode>"+
			"<DestinationEmpCode>"+transferInoutDO.toEmpNo+"</DestinationEmpCode>"+
			"<SourceVehicleCode>"+transferInoutDO.sourceVNO+"</SourceVehicleCode>"+
			"<DestinationVehicleCode>"+transferInoutDO.destVNO+"</DestinationVehicleCode>"+
			"<Date>"+transferInoutDO.Date+"</Date>"+
			"<Type>"+transferType+"</Type>"+
			"<OrderNumber>"+transferInoutDO.sourceOrderID+"</OrderNumber>"+
			"<SourceOrderNumber>"+transferInoutDO.destOrderID+"</SourceOrderNumber>"+
			"<Status>"+0+"</Status>"+
			"<TransferDetailDcos>"+
			getTransferDcos(transferInoutDO.vecOrderDetailDcos, transferInoutDO.InventoryUID, transferInoutDO.sourceOrderID)+
			"</TransferDetailDcos>"+
			"</TransferHeaderDco>";
		}
		return strXML;
	}
	
	private static String getTransferDcos(ArrayList<DeliveryAgentOrderDetailDco> vecOrderDetailDcos, String transferId, String orderNumber)
	{
		String strXML = "";
		for (DeliveryAgentOrderDetailDco detailDco:  vecOrderDetailDcos) 
		{
			strXML += "<TransferDetailDco>"+
			"<TransferDetailId>"+detailDco.transferDetailID+"</TransferDetailId>"+
			"<TransferId>"+transferId+"</TransferId>"+
			"<ItemCode>"+detailDco.itemCode+"</ItemCode>"+
			"<ReqestedCases>"+detailDco.preCases+"</ReqestedCases>"+
			"<ReqestedUnits>"+detailDco.preUnits+"</ReqestedUnits>"+
			"<TransferredCases>"+detailDco.preCases+"</TransferredCases>"+
			"<TransferredUnits>"+detailDco.preUnits+"</TransferredUnits>"+
			"<ReqestedQty>"+detailDco.totalCases+"</ReqestedQty>"+
			"<TransferredQty>"+detailDco.totalCases+"</TransferredQty>"+
			"<OrderNumber>"+orderNumber+"</OrderNumber>"+
			"</TransferDetailDco>";
		}
		return strXML;
	}
	public static String UpdateTransferInStockUpload(TransferInoutDO transferInoutDO)
	{
		String strXML =   	SOAP_HEADER + 
		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
		"<objTransferInOrOutStock>"+
		"<Inventories>"+getVMSalesmanInventoryDcoINUpload(transferInoutDO)+"</Inventories>"+
		"</objTransferInOrOutStock>"+
		"</UpdateTransferInOrOutStock>"+
		SOAP_FOOTER ;
		return strXML;
	}
	///////////////////////////////////////////////////////////////////////////////
	private static String getVMSalesmanInventoryDcoOutUpload(TransferInoutDO transferInoutDO)
	{
		String strXML = "";
		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : transferInoutDO.vecOrderDetailDcos) 
		{
			strXML += "<VMSalesmanInventoryDco>"+
					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
					 "<EmpNo>"+transferInoutDO.fromEmpNo+"</EmpNo>"+
					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
					 "<PrimaryQuantity>0</PrimaryQuantity>"+
					 "<SecondaryQuantity>0</SecondaryQuantity>"+
					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
					 "<FeederEmpNo>"+transferInoutDO.toEmpNo+"</FeederEmpNo>"+
					 "</VMSalesmanInventoryDco>";
		}
		return strXML;
	}
	
//	private static String getVMSalesmanInventoryDcoOut(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
//	{
//		String strXML = "";
//		for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecOrdProduct) 
//		{
//			strXML += "<VMSalesmanInventoryDco>"+
//					 "<VMSalesmanInventoryId></VMSalesmanInventoryId>"+	
//					 "<Date>"+CalendarUtils.getDeliverydate()+"</Date>"+
//					 "<EmpNo>"+strOutEmpNo+"</EmpNo>"+
//					 "<ItemCode>"+deliveryAgentOrderDetailDco.itemCode+"</ItemCode>"+
//					 "<PrimaryQuantity>0</PrimaryQuantity>"+
//					 "<SecondaryQuantity>0</SecondaryQuantity>"+
//					 "<PrimaryReturnQuantity>"+deliveryAgentOrderDetailDco.preCases+"</PrimaryReturnQuantity>"+
//					 "<SecondaryReturnQuantity>"+deliveryAgentOrderDetailDco.preUnits+"</SecondaryReturnQuantity>"+
//					 "<FeederEmpNo>"+strEmpNo+"</FeederEmpNo>"+
//					 "</VMSalesmanInventoryDco>";
//		}
//		return strXML;
//	}
	public static String UpdateTransferOutStockUpload(TransferInoutDO transferInoutDO)
	{
		String strXML =   	SOAP_HEADER + 
		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
		"<objTransferInOrOutStock>"+
		"<Inventories>"+getVMSalesmanInventoryDcoOutUpload(transferInoutDO)+"</Inventories>"+
		"</objTransferInOrOutStock>"+
		"</UpdateTransferInOrOutStock>"+
		SOAP_FOOTER ;
		return strXML;
	}
//	public static String UpdateTransferOutStock(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct, String strEmpNo, String strOutEmpNo)
//	{
//		String strXML =   	SOAP_HEADER + 
//		"<UpdateTransferInOrOutStock xmlns=\"http://tempuri.org/\">"+
//		"<objTransferInOrOutStock>"+
//		"<Inventories>"+getVMSalesmanInventoryDcoOut(vecOrdProduct, strEmpNo, strOutEmpNo)+"</Inventories>"+
//		"</objTransferInOrOutStock>"+
//		"</UpdateTransferInOrOutStock>"+
//		SOAP_FOOTER ;
//		return strXML;
//	}
	/**
	 * Method to build request to get all deleted Record
	 * @return String
	 */
	public static String getAllDeleteLogs(String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER) 
					.append("<GetAllDeleteLogs xmlns=\"http://tempuri.org/\">")
	     			.append("<lsd>").append(lsd).append("</lsd>")
	     			.append("<lst>").append(lst).append("</lst>")
	     			.append("</GetAllDeleteLogs>")
					.append(SOAP_FOOTER) ;
		
		return strXML.toString();
	}
	/**
	 * Method to build request to get all Inventory
	 * @return String
	 */
	public static String getAllPromotions(String strEmpNo , String lsd, String lst)
	{
		String strXML = SOAP_HEADER + 
						"<GetAllPromotions xmlns=\"http://tempuri.org/\">"+
	     				"<UserCode>"+strEmpNo+"</UserCode>"+
	     				"<lsd>"+lsd+"</lsd>"+
	     				"<lst>"+lst+"</lst>"+
	     				"</GetAllPromotions>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getAllPromotionsWithLastSynch(String strEmpNo , String lsd, String lst)
	{
		String strXML = SOAP_HEADER + 
						"<GetAllPromotions xmlns=\"http://tempuri.org/\">"+
	     				"<UserCode>"+strEmpNo+"</UserCode>"+
	     				"<lsd>"+lsd+"</lsd>"+
	     				"<lst>"+lsd+"</lst>"+
	     				"</GetAllPromotions>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String uploadLoadRequests(ArrayList<LoadRequestDO> vecLoad)
	{
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<PostStockMovements xmlns=\"http://tempuri.org/\">";
				String strBody = "<objMovementHeaderDcos>";
  							  		for(LoadRequestDO orderDO : vecLoad)
  							  		{
								  		strOrder  = strOrder+"<MovementHeaderDco>" +
								  		  "<MovementCode>"+orderDO.MovementCode+"</MovementCode>"+
								          "<PreMovementCode>"+orderDO.PreMovementCode+"</PreMovementCode>"+
								          "<AppMovementId>"+orderDO.AppMovementId+"</AppMovementId>"+
								          "<OrgCode>"+orderDO.OrgCode+"</OrgCode>"+
								          "<UserCode>"+orderDO.UserCode+"</UserCode>"+
								          "<WHKeeperCode>"+orderDO.UserCode+"</WHKeeperCode>"+
								          "<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>"+
								          "<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>"+
								          "<MovementDate>"+orderDO.MovementDate+"</MovementDate>"+
								          "<MovementNote>"+orderDO.MovementNote+"</MovementNote>"+
								          "<MovementType>"+orderDO.MovementType+"</MovementType>"+
								          "<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
								          "<DestinationVehicleCode>"+orderDO.DestinationVehicleCode+"</DestinationVehicleCode>"+
								          "<VisitID>"+orderDO.VisitID+"</VisitID>"+
								          "<CreatedOn>"+orderDO.CreatedOn+"</CreatedOn>"+
								          "<Amount>"+orderDO.Amount+"</Amount>"+
								          "<_MovementStatus>"+orderDO.MovementStatus+"</_MovementStatus>"+
								          "<MovementStatus>"+orderDO.MovementStatus+"</MovementStatus>"+
								          "<ProductType>"+orderDO.ProductType+"</ProductType>"+
										getLoadDetail(orderDO.vecItems, orderDO)+
    							  		"</MovementHeaderDco>";
  							  		}
	      					strBody = strBody+strOrder;
	      					strXML = strXML+strBody+
	      							  "</objMovementHeaderDcos>"+
	      							  "</PostStockMovements>"+
	      							  SOAP_FOOTER ;
	      					LogUtils.errorLog("strXML", ""+strXML);
	      					
	      					strXML = strXML.replace("&", "");
	    return strXML;
	}
	
	public static String uploadLPOOrderRequests(LoadRequestDO orderDO)
	{
		String strOrder  = ""; 
		String strXML = SOAP_HEADER + "<CreateLPO xmlns=\"http://tempuri.org/\">";
				String strBody = "";
				strOrder  = strOrder+"<objMovementHeaderDco>" +
				  		  "<MovementCode>"+orderDO.MovementCode+"</MovementCode>"+
				          "<PreMovementCode>"+orderDO.PreMovementCode+"</PreMovementCode>"+
				          "<AppMovementId>"+orderDO.AppMovementId+"</AppMovementId>"+
				          "<OrgCode>"+orderDO.OrgCode+"</OrgCode>"+
				          "<UserCode>"+orderDO.UserCode+"</UserCode>"+
				          "<WHKeeperCode>"+orderDO.UserCode+"</WHKeeperCode>"+
				          "<CurrencyCode>"+orderDO.CurrencyCode+"</CurrencyCode>"+
				          "<JourneyCode>"+orderDO.JourneyCode+"</JourneyCode>"+
				          "<MovementDate>"+orderDO.MovementDate+"</MovementDate>"+
				          "<MovementNote>"+orderDO.MovementNote+"</MovementNote>"+
				          "<MovementType>"+orderDO.MovementType+"</MovementType>"+
				          "<SourceVehicleCode>"+orderDO.SourceVehicleCode+"</SourceVehicleCode>"+
				          "<DestinationVehicleCode>"+orderDO.DestinationVehicleCode+"</DestinationVehicleCode>"+
				          "<VisitID>"+orderDO.VisitID+"</VisitID>"+
				          "<CreatedOn>"+orderDO.CreatedOn+"</CreatedOn>"+
				          "<Amount>"+orderDO.Amount+"</Amount>"+
				          "<_MovementStatus>"+orderDO.MovementStatus+"</_MovementStatus>"+
				          "<MovementStatus>"+orderDO.MovementStatus+"</MovementStatus>"+
				          "<ProductType>"+orderDO.ProductType+"</ProductType>"+
				          "<Site>"+orderDO.customerSite+"</Site>"+
				          getLoadDetail(orderDO.vecItems, orderDO)+
				  		 "</objMovementHeaderDco>";
      					 strBody = strBody+strOrder;
      					 strXML = strXML+strBody+
      							  "</CreateLPO>"+
      							  SOAP_FOOTER ;
      					 LogUtils.errorLog("strXML", ""+strXML);
      					
      					 strXML = strXML.replace("&", "");
	    return strXML;
	}

	private static String getLoadDetail(ArrayList<LoadRequestDetailDO> vecItems, LoadRequestDO loadRequestDO)
	{
		String strXML = "";
		strXML = strXML + "<MovementDetailDcos>";
		if(vecItems != null && vecItems.size() >0)
		{
			for (LoadRequestDetailDO productDO : vecItems)
			{
				productDO.ItemDescription = URLEncoder.encode(""+productDO.ItemDescription);
				productDO.ItemAltDescription = URLEncoder.encode(""+productDO.ItemAltDescription);
				
				strXML = strXML + "<MovementDetailDco>"+
						"<LineNo>"+productDO.LineNo+"</LineNo>"+
						"<MovementCode>"+productDO.MovementCode+"</MovementCode>"+
						"<ItemCode>"+productDO.ItemCode+"</ItemCode>"+
						"<OrgCode>"+productDO.OrgCode+"</OrgCode>"+
						"<ItemDescription>"+productDO.ItemDescription+"</ItemDescription>"+
						"<ItemAltDescription>"+productDO.ItemAltDescription+"</ItemAltDescription>"+
						"<UOM>"+productDO.UOM+"</UOM>"+
						"<QuantityLevel1>"+productDO.QuantityLevel1+"</QuantityLevel1>"+
						"<QuantityLevel2>"+productDO.QuantityLevel2+"</QuantityLevel2>"+
						"<QuantityLevel3>"+productDO.QuantityLevel3+"</QuantityLevel3>"+
						"<NonSellableQty>"+productDO.NonSellableQty+"</NonSellableQty>"+
						"<QuantityBU>"+productDO.QuantityBU+"</QuantityBU>"+
						"<CurrencyCode>"+productDO.CurrencyCode+"</CurrencyCode>"+
						"<PriceLevel1>"+productDO.PriceLevel1+"</PriceLevel1>"+
						"<PriceLevel2>"+productDO.PriceLevel2+"</PriceLevel2>"+
						"<PriceLevel3>"+productDO.PriceLevel3+"</PriceLevel3>"+
						"<MovementReasonCode>"+productDO.MovementReasonCode+"</MovementReasonCode>"+
						"<ExpiryDate>"+productDO.ExpiryDate+"</ExpiryDate>"+
						"<CreatedOn>"+productDO.CreatedOn+"</CreatedOn>"+
						"<MovementType>"+loadRequestDO.MovementType+"</MovementType>"+
						"<CancelledQuantity>"+productDO.CancelledQuantity+"</CancelledQuantity>"+
						"<InProcessQuantity>"+productDO.InProcessQuantity+"</InProcessQuantity>"+
						"<ShippedQuantity>"+productDO.ShippedQuantity+"</ShippedQuantity>"+
						"<DiscountDescription>"+productDO.DiscountDescription+"</DiscountDescription>"+
						"</MovementDetailDco>";	
			}
		}
		strXML = strXML + "</MovementDetailDcos>";
		return strXML;
	}
	
	public static String getApprovedMovements(String movementCode, String appMovementCode)
	{
		String strXML = SOAP_HEADER + 
						"<GetApprovedMovements xmlns=\"http://tempuri.org/\">"+
	     				"<MovementCode>"+movementCode+"</MovementCode>"+
	     				"<AppMovementCode>"+appMovementCode+"</AppMovementCode>"+
	     				"</GetApprovedMovements>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String postReasons(ArrayList<PostReasonDO> arrList)
	{
		String body = "";
		String strXML = SOAP_HEADER + 
					 		"<InsertSkippingReason xmlns=\"http://tempuri.org/\">"+
							"<SkippingReasonRequest>"+
					 		"<SkippingReasons>";
			
			for(PostReasonDO detailDco : arrList)
			{
				body = body + 
						"<SkippingReason>"+
						 "<PresellerId>"+detailDco.presellerId+"</PresellerId>"+
	            		 "<SkippingDate>"+detailDco.skippingDate+"</SkippingDate>"+
	            		 "<Reason>"+detailDco.reason+"</Reason>"+
	            		 "<SiteId>"+detailDco.customerSiteID+"</SiteId>"+
	            		 "<Type>"+detailDco.reasonType+"</Type>"+
						 "</SkippingReason>";
			}
			strXML = 	strXML+body+ 
						"</SkippingReasons>"+
						"</SkippingReasonRequest>"+
		      			"</InsertSkippingReason>"+
					    SOAP_FOOTER ;
			
			LogUtils.errorLog("strXML", "strXML - "+strXML);
			return strXML;
	}
	
	public static String getAssetMasters(String userCode , String lsd , String lst)
	{
		String strXML = SOAP_HEADER + 
						"<GetAssetMasters xmlns=\"http://tempuri.org/\">"+
	     				"<UserCode>"+userCode+"</UserCode>"+
	     				"<lsd>"+lsd+"</lsd>"+
	     				"<lst>"+lst+"</lst>"+
	     				"</GetAssetMasters>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getAllTasks(String SalesmanId, String synctime)
	{
		String strXML =  SOAP_HEADER+"<GetAllTask xmlns=\"http://tempuri.org/\">"+
						"<UserCode>"+SalesmanId+"</UserCode>"+
						"<LastSyncDate>"+synctime+"</LastSyncDate>"+
						"</GetAllTask>"+
						SOAP_FOOTER;;
		return strXML;
	}
	
	
	public static String postAsset(Vector<AssetTrackingDo> assetTrackingDos)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
			  .append("<PostAsset xmlns=\"http://tempuri.org/\">")
			  .append("<objAssetDcos>")
			  .append(getAssetTrackingXml(assetTrackingDos))
			  .append("</objAssetDcos>")
			  .append("</PostAsset>")
			  .append(SOAP_FOOTER);
			
			return buffer.toString();
	}
	
	public static String PostAssetServiceRequest (AssetServiceDO assetserDo)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
			  .append("<PostAssetServiceRequest xmlns=\"http://tempuri.org/\">")
			  .append("<objAssetServiceRequestDcos>")
			  .append(getAssetServiceRequestXml(assetserDo))
			  .append("</objAssetServiceRequestDcos>")
			  .append("</PostAssetServiceRequest>")
			  .append(SOAP_FOOTER);
			
			return buffer.toString();
	}
	
	private static String getAssetServiceRequestXml(AssetServiceDO assetserDo) 
	{
		
		StringBuffer buffer = new StringBuffer();
		
		
			
			buffer
			 	  .append("<AssetServiceRequestDco>")
			 	  .append("<AssetServiceRequestId>").append(assetserDo.assetServiceRequestId).append("</AssetServiceRequestId>")
			 	  .append("<UserCode>").append(assetserDo.userCode).append("</UserCode>")
			      .append("<SiteNo>").append(assetserDo.siteNo).append("</SiteNo>")
			      .append("<RequestDate>").append(assetserDo.requestDate).append("</RequestDate>")
			      .append("<RequestImage>").append(assetserDo.requestImage).append("</RequestImage>")
			      .append("<JourneyCode>").append(assetserDo.journeyCode).append("</JourneyCode>")
			      .append("<VisitCode>").append(assetserDo.visitCode).append("</VisitCode>")
			      .append("<Status>").append(assetserDo.status).append("</Status>")
			      .append("<Notes>").append(assetserDo.notes).append("</Notes>")
				  .append("<IsApproved>").append(assetserDo.isApproved).append("</IsApproved>")
				  .append("<ServiceRequestAppId>").append(assetserDo.serviceRequestAppId).append("</ServiceRequestAppId>")
				  .append("</AssetServiceRequestDco>");
			      
		return buffer.toString();
	}

	public static String postSurvey(CustomerSurveyDONew cusSurveyDo)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
		      .append("<PostSurvey xmlns=\"http://tempuri.org/\">")
		      .append("<objSurveyDcos>")
		      .append(getSurveyXml(cusSurveyDo))
		      .append("</objSurveyDcos>")
		      .append("</PostSurvey>")
		      .append(SOAP_FOOTER);
		
		LogUtils.errorLog("PostSurveyXML", buffer.toString());
		return buffer.toString();
		
	}

	public static String postAssetsRequest(String siteNo, String assetId, String dateTime, int quantity)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(SOAP_HEADER)
		      .append("<InsertAssetCustomer xmlns=\"http://tempuri.org/\">")
		      .append("<SiteNo>").append(siteNo).append("</SiteNo>")
		      .append("<AssetId>").append(assetId).append("</AssetId>")
		      .append("<InstalledOn>").append(dateTime).append("</InstalledOn>")
		       .append("<Quantity>").append(quantity).append("</Quantity>")
		      .append("</InsertAssetCustomer >")
		      .append(SOAP_FOOTER);
		
		
		LogUtils.errorLog("InsertAssetCustomer ", buffer.toString());
		return buffer.toString();
		
	}

	private static Object getSurveyXml(CustomerSurveyDONew CusSurveyDo)
	{

		StringBuffer buffer = new StringBuffer();
		
		
			buffer.append("<SurveyDco>") 
				  .append("<SurveyAppId>").append(CusSurveyDo.SurveyAppId).append("</SurveyAppId>")
				  
			      .append("<SurveyId>").append(CusSurveyDo.serveyId).append("</SurveyId>")
			      .append("<UserCode>").append(CusSurveyDo.userCode).append("</UserCode>")
			      .append("<ClientCode>").append(CusSurveyDo.clientCode).append("</ClientCode>")
			      .append("<Date>").append(CusSurveyDo.date).append("</Date>")
			      .append("<Latitude>").append(CusSurveyDo.lattitude).append("</Latitude>")
			      .append("<Longitude>").append(CusSurveyDo.longitude).append("</Longitude>")
			      .append("<UserRole>").append(CusSurveyDo.userRole).append("</UserRole>")
			      .append("<JourneyCode>").append(CusSurveyDo.journeyCode).append("</JourneyCode>")
			      .append("<VisitCode>").append(CusSurveyDo.visitCode).append("</VisitCode>")
			      .append("<SurveyResultDcos>")
			      .append(getSurveyResultXML(CusSurveyDo.srveyQues,CusSurveyDo.SurveyResultAppId,CusSurveyDo.resultServeyId,CusSurveyDo.serveyId))
			      .append("</SurveyResultDcos>")
			      .append("</SurveyDco>");
			      
		
		
		return buffer.toString();
	}

	private static Object getSurveyResultXML(Vector<SurveyQuestionDONew> srveyQues, String SurveyResultAppId, String resultServeyId, String serveyId) 
	{
		StringBuffer buffer = new StringBuffer();
		
		for(SurveyQuestionDONew suquesdo : srveyQues)
		{
			
			buffer
			 	  .append("<SurveyResultDco>")
			 	  .append("<SurveyResultAppId>").append(SurveyResultAppId).append("</SurveyResultAppId>")
			 	  .append("<SurveyResultId>").append(resultServeyId).append("</SurveyResultId>")
			      .append("<SurveyId>").append(serveyId).append("</SurveyId>")
			      .append("<QuestionId>").append(suquesdo.questionId).append("</QuestionId>")
			      .append("<Question>").append(suquesdo.question).append("</Question>")
			      .append("<OptionId>").append(suquesdo.optionId).append("</OptionId>")
			      .append("<Option>").append(suquesdo.option).append("</Option>")
				  .append("<Comment>").append(suquesdo.comments).append("</Comment>")
				  .append("</SurveyResultDco>");
			      
		}
		
		return buffer.toString();
	}

	private static String getAssetTrackingXml(Vector<AssetTrackingDo> assetTrackingDos)
	{
		StringBuffer buffer = new StringBuffer();
		
		for(AssetTrackingDo assetTrackingDo : assetTrackingDos)
		{
			String assetTrackingId = "0";
			if(!assetTrackingDo.assetTrackingId.contains("-"))
				assetTrackingId = assetTrackingDo.assetTrackingId;
			buffer.append("<AssetTrackingDco>")
				  .append("<AssetTrackingId>").append(0).append("</AssetTrackingId>")
				  .append("<AssetTrackingAppId>").append(assetTrackingDo.assetTrackingId).append("</AssetTrackingAppId>")
				  .append("<UserCode>").append(assetTrackingDo.userCode).append("</UserCode>")
				  .append("<SiteNo>").append(assetTrackingDo.siteNo).append("</SiteNo>")
				  .append("<VisitCode>").append(assetTrackingDo.visitedCode).append("</VisitCode>")
				  .append("<JourneyCode>").append(assetTrackingDo.journeyCode).append("</JourneyCode>")
				  .append("<Date>").append(assetTrackingDo.date).append("</Date>")
				  .append("<ImagePath>").append(assetTrackingDo.imagepath).append("</ImagePath>")
				  .append("<AssetTrackingDetailDcos>")
				  .append(getAssetTrackingDetailsXml(assetTrackingDo.vAssetTrackingDetailDos,assetTrackingId,assetTrackingDo.imagepath,assetTrackingDo.tempimagepath,assetTrackingDo.temperature))
				  .append("</AssetTrackingDetailDcos>")
				  .append("</AssetTrackingDco>");
		}
		return buffer.toString();
	}
	
	private static String getAssetTrackingDetailsXml(Vector<AssetTrackingDetailDo> vAssetTrackingDetailDos, String assetTrackingId, String imagepath, String tempimagepath, String temperature)
	{
		StringBuffer buffer = new StringBuffer();
		
		for(AssetTrackingDetailDo assetTrackingDetailDo : vAssetTrackingDetailDos)
		 {
			buffer.append("<AssetTrackingDetailDco>")
				  .append("<AssetId>").append(assetTrackingDetailDo.assetId).append("</AssetId>")
				  .append("<AssetTrackingDetailId>").append(0).append("</AssetTrackingDetailId>")
				  .append("<AssetTrackingDetailAppId>").append(assetTrackingDetailDo.assetTrackingDetailId).append("</AssetTrackingDetailAppId>")
				  .append("<AssetTrackingId>").append(assetTrackingId).append("</AssetTrackingId>")
				  .append("<ImagePath>").append(imagepath).append("</ImagePath>")
				  .append("<TempImagePath>").append(tempimagepath).append("</TempImagePath>")
				  .append("<Temparature>").append(temperature).append("</Temparature>")
           		  .append("<barCode>").append(assetTrackingDetailDo.barCode).append("</barCode>")
           		  .append("<ScanningTime>").append(assetTrackingDetailDo.scanningTime).append("</ScanningTime>")
           		  .append("</AssetTrackingDetailDco>");
		 }
		return buffer.toString();
	}
	
	public static String getStartJourney(JouneyStartDO journeyStartDO)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append("<PostJourneyDetails xmlns=\"http://tempuri.org/\">")
		.append("<objJourneyDco>");
			buffer.append("<JourneyDco>")
				  .append("<UserCode>").append(journeyStartDO.userCode).append("</UserCode>")
				  .append("<JourneyCode>").append(journeyStartDO.journeyCode).append("</JourneyCode>")
				  .append("<Date>").append(journeyStartDO.date).append("</Date>")
           		  .append("<StartTime>").append(journeyStartDO.startTime).append("</StartTime>")
           		  .append("<EndTime>").append(journeyStartDO.endTime).append("</EndTime>")
           		  .append("<TotalTimeInMins>").append(journeyStartDO.TotalTimeInMins).append("</TotalTimeInMins>")
				  .append("<JourneyAppId>").append(journeyStartDO.journeyAppId).append("</JourneyAppId>")
				  .append("<OdometerReading>").append(journeyStartDO.odometerReading).append("</OdometerReading>")
           		  .append("<IsVanStockVerified>").append(journeyStartDO.IsVanStockVerified).append("</IsVanStockVerified>")
           		  .append("<VerifiedBy>").append(journeyStartDO.VerifiedBy).append("</VerifiedBy>")
           		  .append("<OdometerReadingStart>").append(journeyStartDO.OdometerReadingStart).append("</OdometerReadingStart>")
           		  .append("<OdometerReadingEnd>").append(journeyStartDO.OdometerReadingEnd).append("</OdometerReadingEnd>")
           		  .append("<StoreKeeperSignatureStartDay>").append(journeyStartDO.StoreKeeperSignatureStartDay).append("</StoreKeeperSignatureStartDay>")
           		  .append("<SalesmanSignatureStartDay>").append(journeyStartDO.SalesmanSignatureStartDay).append("</SalesmanSignatureStartDay>")
           		  .append("<StoreKeeperSignatureEndDay>").append(journeyStartDO.StoreKeeperSignatureEndDay).append("</StoreKeeperSignatureEndDay>")
           		  .append("<SalesmanSignatureEndDay>").append(journeyStartDO.SalesmanSignatureEndDay).append("</SalesmanSignatureEndDay>")
           		  .append("<VehicleCode>").append(journeyStartDO.vehicleCode).append("</VehicleCode>")
           		  .append("</JourneyDco>");
		buffer.append("</objJourneyDco>")
		.append("</PostJourneyDetails>")
		.append(SOAP_FOOTER);
		
		Log.e("PostJourneyDetails"," PostJourneyDetails = "+buffer.toString());
		return buffer.toString();
	}
	public static String getAllLogsXml(Vector<LogDO> vecLogs)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append("<InsertVechileTracking xmlns=\"http://tempuri.org/\">");
		for(LogDO logDO : vecLogs)
		{
			buffer.append("<VechileTrackingDco>")
				  .append("<Type>").append(logDO.type).append("</Type>")
				  .append("<Key>").append(logDO.key).append("</Key>")
				  .append("<Data>").append(logDO.data).append("</Data>")
           		  .append("<DeviceDate>").append(logDO.deviceTime).append("</DeviceDate>")
           		  .append("<UserCode>").append(logDO.userId).append("</UserCode>")
           		  .append("</VechileTrackingDco>");
		}
		buffer.append("</InsertVechileTracking>")
		.append(SOAP_FOOTER);
		
		Log.e("PostJourneyDetails"," PostJourneyDetails = "+buffer.toString());
		return buffer.toString();
	}
	public static String getStartJournyStart(Vector<JouneyStartDO> vAssetTrackingDetailDos)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append("<PostJourneyDetails xmlns=\"http://tempuri.org/\">")
		.append("<objJourneyDco>");
		for(JouneyStartDO journeyStartDO : vAssetTrackingDetailDos)
		{
			buffer.append("<JourneyDco>")
				  .append("<UserCode>").append(journeyStartDO.userCode).append("</UserCode>")
				  .append("<JourneyCode>").append(journeyStartDO.journeyCode).append("</JourneyCode>")
				  .append("<Date>").append(journeyStartDO.date).append("</Date>")
           		  .append("<StartTime>").append(journeyStartDO.startTime).append("</StartTime>")
           		  .append("<EndTime>").append(journeyStartDO.endTime).append("</EndTime>")
           		  .append("<TotalTimeInMins>").append(journeyStartDO.TotalTimeInMins).append("</TotalTimeInMins>")
				  .append("<JourneyAppId>").append(journeyStartDO.journeyAppId).append("</JourneyAppId>")
				  .append("<OdometerReading>").append(journeyStartDO.odometerReading).append("</OdometerReading>")
           		  .append("<IsVanStockVerified>").append(journeyStartDO.IsVanStockVerified).append("</IsVanStockVerified>")
           		  .append("<VerifiedBy>").append(journeyStartDO.VerifiedBy).append("</VerifiedBy>")
           		  .append("<OdometerReadingStart>").append(journeyStartDO.OdometerReadingStart).append("</OdometerReadingStart>")
           		  .append("<OdometerReadingEnd>").append(journeyStartDO.OdometerReadingEnd).append("</OdometerReadingEnd>")
           		  .append("<StoreKeeperSignatureStartDay>").append(journeyStartDO.StoreKeeperSignatureStartDay).append("</StoreKeeperSignatureStartDay>")
           		  .append("<SalesmanSignatureStartDay>").append(journeyStartDO.SalesmanSignatureStartDay).append("</SalesmanSignatureStartDay>")
           		  .append("<StoreKeeperSignatureEndDay>").append(journeyStartDO.StoreKeeperSignatureEndDay).append("</StoreKeeperSignatureEndDay>")
           		  .append("<SalesmanSignatureEndDay>").append(journeyStartDO.SalesmanSignatureEndDay).append("</SalesmanSignatureEndDay>")
           		  .append("<VehicleCode>").append(journeyStartDO.vehicleCode).append("</VehicleCode>")
           		  .append("</JourneyDco>");
		}
		buffer.append("</objJourneyDco>")
		.append("</PostJourneyDetails>")
		.append(SOAP_FOOTER);
		
		Log.e("PostJourneyDetails"," PostJourneyDetails = "+buffer.toString());
		return buffer.toString();
	}
	
	public static String getCustomerVisitXML(Vector<CustomerVisitDO> vAssetTrackingDetailDos)
	{
		StringBuffer buffer = new StringBuffer(SOAP_HEADER);
		buffer.append(" <PostClientVisits  xmlns=\"http://tempuri.org/\">")
		.append("<objClientVisitDco>");
		for(CustomerVisitDO assetTrackingDetailDo : vAssetTrackingDetailDos)
		{
			buffer.append("<ClientVisitDco>")
				  .append("<CustomerVisitId>").append(assetTrackingDetailDo.CustomerVisitId).append("</CustomerVisitId>")
				  .append("<CustomerVisitAppId>").append(assetTrackingDetailDo.CustomerVisitAppId).append("</CustomerVisitAppId>")
				  .append("<UserCode>").append(assetTrackingDetailDo.UserCode).append("</UserCode>")
           		  .append("<JourneyCode>").append(assetTrackingDetailDo.JourneyCode).append("</JourneyCode>")
           		  .append("<VisitCode>").append(assetTrackingDetailDo.VisitCode).append("</VisitCode>")
           		  .append("<ClientCode>").append(assetTrackingDetailDo.ClientCode).append("</ClientCode>")
				  .append("<Date>").append(assetTrackingDetailDo.Date).append("</Date>")
				  .append("<ArrivalTime>").append(assetTrackingDetailDo.ArrivalTime).append("</ArrivalTime>")
           		  .append("<OutTime>").append(assetTrackingDetailDo.OutTime).append("</OutTime>")
           		  .append("<TotalTimeInMins>").append(assetTrackingDetailDo.TotalTimeInMins).append("</TotalTimeInMins>")
           		  .append("<Latitude>").append(assetTrackingDetailDo.Latitude).append("</Latitude>")
           		  .append("<Longitude>").append(assetTrackingDetailDo.Longitude).append("</Longitude>")
           		  .append("<IsProductiveCall>").append(assetTrackingDetailDo.IsProductiveCall).append("</IsProductiveCall>")
           		  .append("<TypeOfCall>").append(assetTrackingDetailDo.TypeOfCall).append("</TypeOfCall>")
           		  .append("<VehicleCode>").append(assetTrackingDetailDo.vehicleNo).append("</VehicleCode>")
           		  .append("<DriverCode>").append(assetTrackingDetailDo.UserCode).append("</DriverCode>")
           		  .append("</ClientVisitDco>");
		}
		buffer.append("</objClientVisitDco>")
		.append("</PostClientVisits >")
		.append(SOAP_FOOTER);
		return buffer.toString();
	}

	
	public static String postTask(Vector<MyActivityDO> vecMyActivityDOs)
	{
		String strXML =  SOAP_HEADER+
			"<InsertTaskOrder xmlns=\"http://tempuri.org/\">" +
			"<ImportHHTasks>"+
			"<HHTasks>"+
			getActivities(vecMyActivityDOs)+
			/*getServeys(vecCustomerSurveyDOs)+*/
			"</HHTasks>"+
			"</ImportHHTasks>"+
			"</InsertTaskOrder>"+
			SOAP_FOOTER;;
		return strXML;
	}
	
	public static String getActivities(Vector<MyActivityDO> vecMyActivityDOs)
	{
		String strXML = "";
		if(vecMyActivityDOs != null && vecMyActivityDOs.size() >0)
		{
			for (MyActivityDO myActivityDO : vecMyActivityDOs)
			{
				strXML += "<HHTaskDco>"+
				"<TaskId>"+myActivityDO.taskID+"</TaskId>" +
				"<Status>C</Status>"+
				"<TaskDetail1>"+myActivityDO.serverimagePath+"</TaskDetail1>"+
				"<TaskDetail2></TaskDetail2>"+
				"<Comment>"+myActivityDO.desccription+"</Comment>"+
				"</HHTaskDco>";
			}
		}
		return strXML;
	}
	
	public static String getAllSurveyMastersWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		String strXML = SOAP_HEADER + 
				"<GetSurveyMasters  xmlns=\"http://tempuri.org/\">"+
 				"<UserCode>"+strEmpNo+"</UserCode>"+
 				"<lsd>"+lsd+"</lsd>"+
 				"<lst>"+lst+"</lst>"+
 				"</GetSurveyMasters>"+
				SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getAllAckTasks(String SalesmanId, String strSyncTime)
	{
		String strXML =  SOAP_HEADER+"<GetAllAcknowledgedTask xmlns=\"http://tempuri.org/\">"+
						"<SalesManCode>"+SalesmanId+"</SalesManCode>"+
						"<LastSyncDate>"+strSyncTime+"</LastSyncDate>"+
						"</GetAllAcknowledgedTask>"+
						SOAP_FOOTER;;
		return strXML;
	}
	public static String getAllCustomersByUserIDWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		String strXML = SOAP_HEADER + 
				 		"<GetCustomersByUserId xmlns=\"http://tempuri.org/\">"+
						"<UserCode>"+strEmpNo+"</UserCode>"+
						"<lsd>"+lsd+"</lsd>"+
						"<lst>"+lst+"</lst>"+
						"</GetCustomersByUserId>"+
				SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getAllTrxHeaderForAppWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		String strXML = SOAP_HEADER + 
				"<GetTrxHeaderForApp xmlns=\"http://tempuri.org/\">"+
 				"<UserCode>"+strEmpNo+"</UserCode>"+
 				"<lsd>"+lsd+"</lsd>"+
 				"<lst>"+lst+"</lst>"+
 				"</GetTrxHeaderForApp>"+
				SOAP_FOOTER ;
		return strXML;
	}
	public static String getAllJPAndRouteDetailsWithLastSynch(String strEmpNo,String lsd, String lst) 
	{
		String strXML = SOAP_HEADER + 
				"<GetJPAndRouteDetails  xmlns=\"http://tempuri.org/\">"+
 				"<UserCode>"+strEmpNo+"</UserCode>"+
 				"<lsd>"+lsd+"</lsd>"+
 				"<lst>"+lst+"</lst>"+
 				"</GetJPAndRouteDetails >"+
				SOAP_FOOTER ;
		return strXML;
	}
	
	public static String deleteReceipt(String recept)
	{
		String strXML = SOAP_HEADER+
				 		"<DeleteReceipt xmlns=\"http://tempuri.org/\">"+
						"<ReceiptCode>"+recept+"</ReceiptCode>"+
						"</DeleteReceipt>"+
						SOAP_FOOTER;
		LogUtils.errorLog("DeleteReceipt",""+strXML);
		return strXML;
	}
	
	public static String registerGCMOnServer(String usedId, String gcmId)
	{
		String strXML = SOAP_HEADER+
				"<updateDeviceId  xmlns=\"http://tempuri.org/\">"+
				 "<UserCode>"+usedId+"</UserCode>"+
					"<GCMKey>"+gcmId+"</GCMKey>"+
				 "</updateDeviceId>"+
				SOAP_FOOTER;
				LogUtils.errorLog("DeleteReceiptDetailsFromApp",""+strXML);
				return strXML;
	}
	
	public static String sendAllInventory(Vector<InventoryDO> vecCompleteInventory)
	{
		
		String strOrder  = "", strOrderItem = ""; 
		String strXML = SOAP_HEADER + "<ImportInventory xmlns=\"http://tempuri.org/\">"+
									  "<Inventories>";
		
		String strBody =		  "<inventory>" ;
									for(InventoryDO inventoryDO : vecCompleteInventory)
									{
										strOrderItem ="";
										strOrder  = strOrder+"<InventoryDco>" +
										"<SiteNo>"+""+inventoryDO.site+"</SiteNo>" +
										"<CreatedBy>"+""+inventoryDO.createdBy+"</CreatedBy>" +
										"<CreatedOn>"+""+inventoryDO.date+"</CreatedOn>" +
										"<InventoryId>"+""+inventoryDO.inventoryId+"</InventoryId>" +
										"<InventoryDetails>" ; 
										for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
										{
											strOrderItem = strOrderItem+"<InventoryDetailsDco>"+
											"<ItemCode>"+""+inventoryDetailDO.itemCode+"</ItemCode>" +
											"<InventoryQuantity>"+inventoryDetailDO.inventoryQty+"</InventoryQuantity>" +
											"<RecomendedQuantity>"+inventoryDetailDO.recmQty+"</RecomendedQuantity>" +
											"</InventoryDetailsDco>";
										}
										strOrder = strOrder+strOrderItem+
										"</InventoryDetails>" +
										"</InventoryDco>";
									}
									strBody = strBody+strOrder+"</inventory>";
									
									strXML = strXML+strBody+
									"</Inventories>"+
									"</ImportInventory>"+
				SOAP_FOOTER ;
			LogUtils.errorLog("strXML", strXML);
	    return strXML;
	}
	
	public static String getClearDataPermission(String empNo)
	{
		StringBuilder strXML = new StringBuilder();
		strXML.append(SOAP_HEADER)
					.append("<GetClearDataPermission xmlns=\"http://tempuri.org/\">" )
					.append("<UserCode>").append(empNo).append("</UserCode>")
					.append("</GetClearDataPermission>")
	      				.append(SOAP_FOOTER) ;
	      				LogUtils.errorLog("strXML",""+strXML);
		return strXML.toString();
	}
	
	public static String getAppAccessStatus()
	{
		String strXML = SOAP_HEADER + 
				 		"<GetAppAccessStatus xmlns=\"http://tempuri.org/\" />"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String insertApplicationInsatallation(String deviceId, String dateTime, String version)
	{
		String strXML = SOAP_HEADER + 
				 		"<InsertApplicationInsatallation xmlns=\"http://tempuri.org/\">"+
	     				"<DeviceId>"+deviceId+"</DeviceId>"+
				 		"<Action>"+"SPLASH"+"</Action>"+
				 		"<PlatForm>"+"ANDROID"+"</PlatForm>"+
	     				"<ModuleName>"+version+"</ModuleName>"+
	     				"<DeviceDate>"+dateTime+"</DeviceDate>"+
						"</InsertApplicationInsatallation>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String getUserStatus(String DeviceId, String UserId, String ModuleName, String DeviceDate)
	{
		String strXML = SOAP_HEADER + 
						"<getUserStatus xmlns=\"http://tempuri.org/\">"+
	     				"<DeviceId>"+DeviceId+"</DeviceId>"+
	     				"<UserId>"+UserId+"</UserId>"+
	     				"<ModuleName>"+ModuleName+"</ModuleName>"+
	     				"<DeviceDate>"+DeviceDate+"</DeviceDate>"+
	     				"</getUserStatus>"+
						SOAP_FOOTER ;
		return strXML;
	}
	
	public static String insertLoginAction(LoginUserInfo loginUserInfo, String deviceId, String dateTime)
	{
		String strXML = SOAP_HEADER + 
				 		"<insertLoginAction xmlns=\"http://tempuri.org/\">"+
	      				"<DeviceId>"+deviceId+"</DeviceId>"+
	      				"<UserId>"+loginUserInfo.strUserId+"</UserId>"+
	      				"<UserName>"+loginUserInfo.strUserName+"</UserName>"+
	      				"<PlatForm>"+"ANDROID"+"</PlatForm>"+
	      				"<ModuleName>"+loginUserInfo.strRole+"</ModuleName>"+
	      				"<DeviceDate>"+dateTime+"</DeviceDate>"+
	      				"</insertLoginAction>"+
						SOAP_FOOTER ;
		return strXML;
	}
	public static String getVanStockLogDetail(String userCode, String lsd, String lst)
	{
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		.append("<GetVanStockLogDetail xmlns=\"http://tempuri.org/\">")
		.append("<UserCode>").append(userCode).append("</UserCode>")
		.append("<lsd>").append(lsd).append("</lsd>")
		.append("<lst>").append(lst).append("</lst>")
		.append("</GetVanStockLogDetail>")
		.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String VerifyIsUnloadApproved(String userCode,String vehicleCode) {
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		.append("<CompareERPVsMWStockByVehicleCode  xmlns=\"http://tempuri.org/\">")
		.append("<UserCode>").append(userCode).append("</UserCode>")
		.append("<VehicleCode>").append(vehicleCode).append("</VehicleCode>")
		.append("</CompareERPVsMWStockByVehicleCode >")
		.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String VerifyResetApproved(String usercode,String vehicleNo,String salesmanCode) {
		StringBuilder strXML = new StringBuilder();strXML.append(SOAP_HEADER)
		.append("<ResetVanstock   xmlns=\"http://tempuri.org/\">")
		.append("<UserCode>").append(usercode).append("</UserCode>")
		.append("<VehicleCode>").append(vehicleNo).append("</VehicleCode>")
		.append("<SalesmanCode>").append(salesmanCode).append("</SalesmanCode>")
		.append("</ResetVanstock>")
		.append(SOAP_FOOTER) ;
		return strXML.toString();
	}
	public static String appActiveRequest()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(SOAP_HEADER)
				.append("<GetAppStatus xmlns=\"http://tempuri.org/\">")
				.append("<AppName>").append("BASKIN").append("</AppName>" )
				.append("</GetAppStatus>")
				.append(SOAP_FOOTER);

		LogUtils.errorLog("CheckLogin",""+sb.toString());

		return sb.toString();
	}
}
