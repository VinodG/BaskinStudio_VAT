package com.winit.baskinrobbin.printer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.TrxHeaderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;



import com.winit.baskinrobbin.salesman.utilities.NumberToEnglish;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;



/**
 * BluetoothConnectMenu
 * @author Abdul Raheem Khan && Abhishek Srivastava
 * @version 2011. 12. 21.
 */
public class HPPrinterUtils {

	private Context context;
	private  BluetoothAdapter mBluetoothAdapter;
	boolean stopWorker;
	private  BluetoothSocket mmSocket;
	private InputStream 	mmInputStream;
	private OutputStream    mmOutputStream;
	private  boolean status ;
	private Preference preference;
	private DecimalFormat deffAmt,amountFormate;
	private String userMobileNumber="";
	private String userVehicleNumber="";
	private boolean isTestMode = true;
	
	
	public HPPrinterUtils(Context context)
	{
		this.context=context;
		preference = new Preference(context);
		deffAmt = new DecimalFormat("##.##");
		deffAmt.setMinimumFractionDigits(2);
		deffAmt.setMaximumFractionDigits(2);
		
		amountFormate  = new DecimalFormat("#,##,##,##,###.###");
		amountFormate.setMinimumFractionDigits(2);
		amountFormate.setMaximumFractionDigits(2);
	}
	
	
	public boolean Connect(BluetoothDevice mmDevice)
	{
		
		enableBlueTooth();
		
		
		if(mmDevice!=null)
		status=	establishedConnection(mmDevice);
		else
		{
			status=false;
		}

		return status;
	}
	
	/*
	 * Close the connection to bluetooth printer.
	 */
	@SuppressLint("NewApi")
	void closeBT() throws IOException {
	    try {
	        stopWorker = true;
	        if(mmOutputStream != null)
	        	mmOutputStream.close();
	        if(mmInputStream != null)
	        	mmInputStream.close();
	        if(mmSocket != null && mmSocket.isConnected())
	        	mmSocket.close();
	    } catch (NullPointerException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	public void PrintSlip(String Data)  throws IOException
	{
		try 
		{
			if(mmOutputStream!=null)
			{
				Data ="(s0p12.3h10v4s5B" + Data; 
				mmOutputStream.write(27);
				mmOutputStream.write(38);
				mmOutputStream.write(108);
				mmOutputStream.write(53); // 54 - small line spacing, 53 - medium line spacing, 52 - 
				mmOutputStream.write(68);
				mmOutputStream.write(27);
				mmOutputStream.write(Data.getBytes("ASCII"));
				closeBT();
			} 
			else{
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
					}
				});
			}
		} 
		catch (NullPointerException e) 
		{
		    e.printStackTrace();
		} catch (Exception e) 
		{
		    e.printStackTrace();
		}
	}

	private boolean establishedConnection(BluetoothDevice mmDevice)
	{
		boolean status=true;
			
		try 
		{
			// Standard SerialPortService ID
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			//create socket 
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		   // To initiate the outgoing connection.
			mmSocket.connect();
			// Get output stream handle
		    mmOutputStream = mmSocket.getOutputStream();
		    // Get input stream handle
		    mmInputStream = mmSocket.getInputStream();
		   // beginListenForData();
		}
		catch (NullPointerException e) 
		{
	    	status=false;
	        e.printStackTrace();
		} 
		catch (Exception e) 
		{
	    	status=false;
	        e.printStackTrace();
		}
		return status;
	}
	
	//For Printing Sales Order
private float totalSalesOrderPrice;
private int totalSalesOrderItem;
private int maxSalesOrderCount = 25;
private int maxSalesOrderBottom =10;
//private int printLineCount = 0;
//private boolean isPrintSaleOrderPageFinished=false;


public void baskinRobin(Vector<ProductDO> vecSalesOrderProducts,float totalPrice,String type,String strOrderId ,String LPO ,JourneyPlanDO mallsDetails )
{
	
	
	
	int totalPages = (vecSalesOrderProducts.size()+ maxSalesOrderCount-1)/maxSalesOrderCount; 

	try
	{
		 if(mmOutputStream!=null|| isTestMode)
		 {
			
			 for(int pages=0; pages < totalPages;pages++)
			 {
				 
				    printSalesOrderInner(vecSalesOrderProducts,totalPrice,type,strOrderId ,LPO ,mallsDetails ,totalPages,pages);
				   // printSalesOrderInner();
			    //	 printCreditNoteInner(mallsDetails,trxHeaderDO,pages,totalPages,str,labelHeader,trxCode,label);
			 }
				 
			 
			 closeBT();
		} 
		else
		{
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
					}
				});
			}
	}
	catch (Exception e) 
	{
	 e.printStackTrace();
	}


	
}









public String  printSalesOrderInner(Vector<ProductDO> vecSalesOrderProducts,float totalPrice,String type,String strOrderId ,String LPO ,JourneyPlanDO mallsDetails ,int totalPage ,int pagecount) throws UnsupportedEncodingException, IOException
{
	
//	  StringBuilder  strInvoice = new StringBuilder();
//	  String strAddress 	= getAdress(mallsDetails);
	//  String mydate = CalendarUtils.parseDate(CalendarUtils.DATE_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, trxHeaderDO.trxDate);
	//  int pageNo = pagecount+1;
	int pageNo = pagecount+1;
	  String formateAddress			= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$-20.20s\r\n"; 
	  String formateAddress2		= "%1$3.3s %2$-72.72s\r\n";
	  String formateInvoice			= "%1$5.5s %2$-18.18s %3$-26.26s %4$-4.4s %5$8.8s %6$9.9s\r\n";
	  String formateTotal		= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$20.20s\r\n";
	  
	  String header3		 = "%1$3.3s %2$-29.29s %3$-10.10s %4$-7.7s %5$-19.19s  %6$-6.6s \r\n"; 
	  String formateInvoice3 = "%1$4.4s %2$-10.10s %3$-32.32s %4$-4.4s %5$5.5s %6$7.7s %7$9.9s\r\n";
	  
	  
	  
	  String formateamount = "%1$3.3s %2$-49.49s   %3$5.5s %4$7.7s %5$9.9s \r\n";
	  
	  String amountHeader=  "%1$3.3s %2$60.60s  %3$10.10s";
	  
	 String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	  
	  
	  String bill="RICH & FRESH SUPERMARKET L.L.C.(1999)";
	  String invoiceNo="1150250301";
	  String InvoiceDate="21-MAY-2015";
	  String salesManName="Anwar Puthiya Keloth ";
	  String paymentDueDate="";
	/*  String address1="MUROOR ROAD (27254)";
	  String address2="NEAR AL WAROOD SCHOOL";*/
	  
	  String partyName = "" ,invNo="",address1="",address2="",address3="",address4="";
	  
	  
	  for(int k=0;k<10;k++)
	  {
		  
		  writeText("\r\n");
	  }
	  
	  
	 if( mallsDetails!=null&&!mallsDetails.partyName.equalsIgnoreCase(""))
	     partyName=mallsDetails.partyName;
	 
	 if( strOrderId!=null&&!strOrderId.equalsIgnoreCase(""))
		 invNo=strOrderId;
	  
	  int index =30;
	  writeText(String.format(header3,"",partyName,"","",strOrderId,""));
	  
	  if( mallsDetails!=null&&!mallsDetails.addresss1.equalsIgnoreCase(""))
		  address1=mallsDetails.addresss1;
	  
	  if( mallsDetails!=null&&!mallsDetails.addresss2.equalsIgnoreCase(""))
		  address2=mallsDetails.addresss2;
	  
	  if( mallsDetails!=null&&!mallsDetails.addresss3.equalsIgnoreCase(""))
		  address3=mallsDetails.addresss3;
	 
	  if( mallsDetails!=null&&!mallsDetails.addresss4.equalsIgnoreCase(""))
		  address4=mallsDetails.addresss4;
	  
	  
	 // if(address.length()>40)
	  /*if (address1.length() > 30)
	    {
			 writeText(String.format(header3,"", mallsDetails.siteName.substring(30),"","",InvoiceDate,""));
			 writeText(String.format(header3,"",address1.substring(30),"","","",""));
		} else
		{
			 writeText(String.format(header3,"", address1,"","",InvoiceDate,""));
		}*/
	  
	  InvoiceDate =mydate.substring(0, mydate.lastIndexOf(":"));
	  
	  if(mallsDetails.siteName.length()>30)
	  {
		  writeText(String.format(header3,"", mallsDetails.siteName.substring(30),"","",InvoiceDate,""));
		  writeText(String.format(header3,"",address1+address2,"","","",""));
		  writeText(String.format(header3,"",address3+address4,"","","",""));
	  }
	  else
	  {
		  writeText(String.format(header3,"", mallsDetails.siteName,"","",InvoiceDate,""));
		  writeText(String.format(header3,"",address1+address2,"","","",""));
		  writeText(String.format(header3,"",address3+address4,"","","",""));
	  }  
	
	  
	    writeText(String.format(header3,"", mallsDetails.salesmanName,"","",paymentDueDate,""));
	    
	    
	    writeText("\r\n \r\n");
	    
	    totalPrice    = 0;
	   int totalDiscount = 0;
	  // writeText(String.format(formateInvoice3,"01","110020026","Cookies N Cream 500 ml (8X1)","8","PCS","20.00","152.00"));
	   for(int  i = pagecount*maxSalesOrderCount,count = 0 ; i < vecSalesOrderProducts.size()&& count < maxSalesOrderCount; i++,count++)
	   {
		    ProductDO product = vecSalesOrderProducts.get(i);
		    totalPrice 		+= vecSalesOrderProducts.get(i).invoiceAmount;
		    totalDiscount   += vecSalesOrderProducts.get(i).discountAmount*StringUtils.getFloat(vecSalesOrderProducts.get(i).preUnits);
		
		    writeText(String.format(formateInvoice3,""+(i+1),product.SKU,product.Description,product.preUnits,product.UOM,product.itemPrice,product.invoiceAmount));
	   }
	   
	   
	   if((pageNo  == totalPage))
		 {
			   writeText("\r\n");
			  int spAmount =pageNo*maxSalesOrderCount-vecSalesOrderProducts.size();
			  for(int i =0 ;i <= spAmount ;i++)
				  writeText("\r\n");
		     
			  DecimalFormat amountFormate  =  new DecimalFormat("##.##");
			  amountFormate.setMinimumFractionDigits(2);
			  amountFormate.setMaximumFractionDigits(2);
			  
			  
			  writeText(String.format(formateamount,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice)),"","",deffAmt.format(totalPrice+totalDiscount)));
			  writeText(String.format(formateamount,"","","","",deffAmt.format(totalDiscount)));
			  writeText(String.format(formateamount,"","","","",deffAmt.format(totalPrice)));
			
		 }

	   
	   
	return "";
	  
	  
	 
		 
 }



/*public void printSalesOrderInner(JourneyPlanDO mallsDetails,TrxHeaderDO trxHeaderDO,int pagecount,int totalPage,String str,String labelHeader,String trxCode, String label) throws UnsupportedEncodingException, IOException
{
	
//	  StringBuilder  strInvoice = new StringBuilder();
//	  String strAddress 	= getAdress(mallsDetails);
	  String mydate = CalendarUtils.parseDate(CalendarUtils.DATE_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, trxHeaderDO.trxDate);
	  int pageNo = pagecount+1;
	  String formateAddress			= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$-20.20s\r\n"; 
	  String formateAddress2		= "%1$3.3s %2$-72.72s\r\n";
	  String formateInvoice			= "%1$5.5s %2$-18.18s %3$-26.26s %4$-4.4s %5$8.8s %6$9.9s\r\n";
	  String formateTotal		= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$20.20s\r\n"; 
	  
	  //	strInvoice.append("\r\n \r\n \r\n \r\n");
//	  	   strInvoice.append(""+labelHeader+str);
	  	int lenth = (""+labelHeader+str).length();
	  	int pos = (76-lenth)/2;
	  	String formateHeader			= "%1$3.3s %2$-"+pos+"."+pos+"s %3$-"+lenth+"."+lenth+"s\r\n";
	  	writeText(String.format(formateHeader,"","",""+labelHeader+str));
	  	writeText(getSeprator());
	  	String strCustomer = "";
	  	if(!TextUtils.isEmpty(mallsDetails.clientBranchCode))
	  		strCustomer = mallsDetails.siteName+" ["+mallsDetails.customerSiteName+"]";
	  	else
	  		strCustomer = mallsDetails.siteName;
	  	writeText(String.format(formateAddress,"","Customer Name",": "+strCustomer,""+label,": "+trxCode));
	  	if(strCustomer.length()>28)
	  		strCustomer = mallsDetails.siteName.substring(28);
	  	writeText(String.format(formateAddress2,"",""+strCustomer));
		String strCustomerCode = "";
	  	 if(!trxHeaderDO.clientCode.equals(trxHeaderDO.clientBranchCode))
	  		strCustomerCode = trxHeaderDO.clientCode+" ["+trxHeaderDO.clientBranchCode+"]";
		else
			strCustomerCode = trxHeaderDO.clientCode;
	  	writeText(getSeprator());
	  	writeText(String.format(formateAddress,"","Customer No",": "+strCustomerCode,"Date",": "+mydate));
	  	writeText(getSeprator());
	  	
	  	 writeText(String.format(formateInvoice,"S.No","Product Code","Model/Color","Qty","U.Price","Amount"));
		 for(int  i = pagecount*maxSalesOrderCount,count = 0 ; i < trxHeaderDO.arrTrxDetailsDOs.size()|| count > maxSalesOrderCount; i++,count++)
		 {
			    TrxDetailsDO trxDeatils = trxHeaderDO.arrTrxDetailsDOs.get(i);
			    String price = ""+deffAmt.format(trxDeatils.priceUsedLevel1);
			    String amount = ""+deffAmt.format((trxDeatils.priceUsedLevel1*trxDeatils.quantityLevel1) - trxDeatils.totalDiscountAmount);
			    totalSalesOrderPrice += (trxDeatils.priceUsedLevel1*trxDeatils.quantityLevel1) - trxDeatils.totalDiscountAmount;
			    totalSalesOrderItem +=trxDeatils.quantityLevel1;
			    writeText(String.format(formateInvoice,""+i,""+trxDeatils.itemCode,""+trxDeatils.itemDescription,""+trxDeatils.quantityLevel1,""+price,""+amount));
		 }
		 
		 if(pageNo == totalPage)
		 {
			  writeText("\r\n");
			  int spAmount =pageNo*maxSalesOrderCount-trxHeaderDO.arrTrxDetailsDOs.size();
			  for(int i =0 ;i <= spAmount ;i++)
				  writeText("\r\n");
			  DecimalFormat amountFormate  =  new DecimalFormat("##.##");
			  amountFormate.setMinimumFractionDigits(2);
			  amountFormate.setMaximumFractionDigits(2);
			  writeText(getSeprator());
			  writeText(String.format(formateTotal,"","Total Qty",": "+deffAmt.format(totalSalesOrderItem),"Total",": "+deffAmt.format(totalSalesOrderPrice)));
			  writeText(getSeprator());
			  writeText(String.format(formateTotal,"","CRS",": "+deffAmt.format(totalSalesOrderPrice),"",""));
			  writeText(getSeprator());
			  writeText("\r\n");
		 }
		 else
		 {
			 writeText("\r\n");
			 writeText("\r\n");
			 writeText("\r\n");
		 } 
		 for(int i =0 ;i<maxSalesOrderBottom ;i++)
		 {
			 writeText("\r\n");
		 }
		 
 }*/
	/*//For Printing Payment Detail
	private int maxPaymentDetailCount = 9;
	private int maxPaymentDetailBottom = 33;
	private float totalPaymentDetailAmount;
	public void printPaymentDetail(JourneyPlanDO mallsDetails, PaymentHeaderDO objPaymentDO,ArrayList<PendingInvicesDO> arrInvoiceNumbers,String strReceiptNo,String str)
	{
		try
		{
			totalPaymentDetailAmount  = 0;
			int totalPages = (arrInvoiceNumbers.size()+maxPaymentDetailCount-1)/maxPaymentDetailCount; 
			 if(mmOutputStream!=null || isTestMode)
			 {
				 StringBuilder strPayment = new StringBuilder();
				 for(int pages=0; pages < totalPages;pages++)
				 {
					 strPayment.append(printPaymentDetailInner(mallsDetails,objPaymentDO,arrInvoiceNumbers,strReceiptNo,pages,totalPages,str));
				 }
				 Log.e("Payment", strPayment.toString());
				 closeBT();
			} 
			else
			{
					((Activity)context).runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
		}
		catch (Exception e) 
		{
		 e.printStackTrace();
		}
		
	}
	 public String printPaymentDetailInner(JourneyPlanDO mallsDetails, PaymentHeaderDO objPaymentDO,ArrayList<PendingInvicesDO> arrInvoiceNumbers,String strReceiptNo,int pagecount,int totalPages,String str) throws IOException
	 {
		 StringBuilder strPayment = new StringBuilder();
		  String mydate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, objPaymentDO.paymentDate);
		  boolean isCashPayment = true;
		  boolean isChequePayment = true;
		  if((objPaymentDO.vecPaymentDetails !=null && objPaymentDO.vecPaymentDetails.size()>0)&&(!objPaymentDO.vecPaymentDetails.get(0).PaymentTypeCode.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)))
		  {
			 isCashPayment = false;
			 String checkDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, objPaymentDO.vecPaymentDetails.get(0).ChequeDate);
			 if(dateDiffence(mydate,checkDate)>=1)
				isChequePayment = false;
		  }
		  int pageNo = pagecount+1;
		  
		  String formateAddress			= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$-20.20s\r\n"; 
		  String formateAddress2		= "%1$3.3s %2$-72.72s\r\n";
		  String formatePayment			= "%1$5.5s %2$-20.20s %3$-20.20s %4$-16.16s %5$10.10s\r\n";
		  String formateTotal			= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$20.20s\r\n"; 
		  String formatePaymentType		= "%1$3.3s %2$-14.14s %3$-28.28s %4$-7.7s %5$20.20s\r\n"; 
		  
		  strPayment.append("\r\n \r\n \r\n \r\n");
		  String labelHeader = "";
		   if(isCashPayment)
			  labelHeader=  "CASH RECEIPT";
			else 
			{
				if(isChequePayment)
					labelHeader =  "CHEQUE RECEIPT";
				else
					labelHeader =  "PDC RECEIPT";
			}
		   
			int lenth = (""+labelHeader+str).length();
		  	int pos = (76-lenth)/2;
		  	String formateHeader			= "%1$3.3s %2$-"+pos+"."+pos+"s %3$-"+lenth+"."+lenth+"s\r\n";
		  	strPayment.append(String.format(formateHeader,"","",""+labelHeader+str));
		  	strPayment.append(getSeprator());
//		   strPayment.append(""+labelHeader+str);
		  	String strCustomer = "";
		  	if(!TextUtils.isEmpty(mallsDetails.clientBranchCode))
		  		strCustomer = mallsDetails.siteName+" ["+mallsDetails.customerSiteName+"]";
		  	else
		  		strCustomer = mallsDetails.siteName;
		  	strPayment.append(String.format(formateAddress,"","Customer Name",": "+strCustomer,""+"Rcpt. No",": "+strReceiptNo));
		  	if(strCustomer.length()>28)
		  		strCustomer = mallsDetails.siteName.substring(28);
		  	strPayment.append(String.format(formateAddress2,"",""+strCustomer));
			String strCustomerCode = "";
		  	 if(!TextUtils.isEmpty(mallsDetails.clientBranchCode))
		  		strCustomerCode = mallsDetails.site+" ["+mallsDetails.clientBranchCode+"]";
			else
				strCustomerCode = mallsDetails.site;
		  	strPayment.append(String.format(formateAddress,"","Customer No",": "+strCustomerCode,"Date",": "+mydate));
		  	strPayment.append(getSeprator());
		  	
		  	strPayment.append(String.format(formatePayment,"S.No","Invoice No","Invoice Date","Remark","Amount"));
		  	strPayment.append(getSeprator());
		  	for(int  i = pagecount*maxPaymentDetailCount, count = 0 ; i < arrInvoiceNumbers.size() && count < maxPaymentDetailCount  ; i++, count++)
		  {
			   totalPaymentDetailAmount =  totalPaymentDetailAmount+StringUtils.getFloat(arrInvoiceNumbers.get(i).payingAmount);
			   String amount = ""+deffAmt.format(StringUtils.getFloat(arrInvoiceNumbers.get(i).payingAmount));
			   String invoiceDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, arrInvoiceNumbers.get(i).invoiceDate);
			   strPayment.append(String.format(formatePayment,"S.No",""+arrInvoiceNumbers.get(i).invoiceNo,""+invoiceDate,"",""+amount));
		  }
		  if(pageNo==totalPages)
		  {
			   int spAmount =pageNo*maxPaymentDetailCount-arrInvoiceNumbers.size();
			   for(int i =0 ;i <= spAmount ;i++)
				   strPayment.append("\r\n");
			   
			   if(isCashPayment)
			   {
				   strPayment.append("\r\n");
				   strPayment.append("\r\n");
			   }
			   else
			   {
				     PaymentDetailDO payment = objPaymentDO.vecPaymentDetails.get(0);
				     String checkDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, payment.ChequeDate);
				     String strPaymentType = ""; 
			   		 if(isChequePayment)
			   			strPaymentType 	= "CHQ";
					 else
						strPaymentType	= "PDC";
					  strPayment.append(String.format(formatePaymentType,"",strPaymentType+" No",": "+payment.ChequeNo,"Bank",": "+payment.BankName));
					  strPayment.append(String.format(formatePaymentType,"",strPaymentType+" Dt",": "+checkDate,"Branch",": "+payment.branchName));
				}
			   DecimalFormat amountFormate  =  new DecimalFormat("##.##");
			   amountFormate.setMinimumFractionDigits(2);
			   amountFormate.setMaximumFractionDigits(2);
			   strPayment.append(getSeprator());
//			   String amountInWord = new NumberUtils().convertNumtoLetter(StringUtils.getFloat(amountFormate.format(totalPaymentDetailAmount)));
			   strPayment.append(String.format(formateTotal,"","","","Total",": "+deffAmt.format(totalPaymentDetailAmount)));
			   strPayment.append(getSeprator());
		  }
		  else
		  {
			   strPayment.append("\r\n");
			   strPayment.append("\r\n");
			   strPayment.append("\r\n");
			   strPayment.append("\r\n");
		  }
		   for(int i =0 ;i<maxPaymentDetailBottom ;i++)
		   {
			  strPayment.append("\r\n");
		   }
		  return strPayment.toString();
	 }
	  //For Printing Payment Summary
		private float totalPaymentSummeryPrice;
		private float totalPaymentCashPrice;
		private float totalPaymentCheckPrice;
		private float totalPaymentPDCPrice;
		private int maxPaymentSummeryCount = 22;
		private int outerCount=0;
		private int innerCount=0;
		public String strTotalPage = "#STRING_TOTAL_PAGE#";
		public boolean isPageFinished = false;
		public boolean isRecieptNoPrinted = true;
		public boolean isInnerLoopPrinted = true;
		public void  printPaymentSummary( Vector<Customer_InvoiceDO> veCollection ,String fromDate ,String toDate )
		 {
			if (veCollection != null && veCollection.size()>0) 
			{
				totalPaymentSummeryPrice =0;
				totalPaymentCashPrice=0;
				totalPaymentCheckPrice=0;
				totalPaymentPDCPrice=0;
				outerCount=0;
				innerCount=0;
				int totalPages = 0;
				for(Customer_InvoiceDO obj : veCollection)
					totalPages +=obj.vecPaymentDetailDOs.size();
				totalPages = (totalPages + maxPaymentSummeryCount -1)/maxPaymentSummeryCount;
				int pages=0;
				if(mmOutputStream!=null)
				{
					try 
					{
						do
						{
							isPageFinished = false;
							printPaymentSummaryInner(veCollection,pages,fromDate,toDate,totalPages );
							pages++;
						}while(isPageFinished);
						closeBT();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				else{
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		 }
		public void  printPaymentSummaryInner(Vector<Customer_InvoiceDO> veCollection,int pagecount ,String fromDate ,String toDate,int totalPages) throws UnsupportedEncodingException, IOException
		 {
			String mobNo="";
			if(userMobileNumber!=null && !userMobileNumber.equalsIgnoreCase("") && !userMobileNumber.equalsIgnoreCase("null"))
				mobNo = " - "+userMobileNumber;
			  String title              = "PAYMENTS RECEIVED SUMMARY";
			  String formatDate			= "%160.160s\r\n";
			  String header 			= " Sls.Man: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" ) "+" - "+ userVehicleNumber+ mobNo+"";
			  String currDate 				= CalendarUtils.getCurrentSynchDateTime();
			  int count = 0;
			  
			  int pos = (int)((165-1.67*title.length())/2);
			  	 writeCommands("&l1O");
				 writeCommands("&l5D");
				 writeCommands("(s0p12.3h10v4s-7B");
				 writeText(""+String.format(formatDate,currDate)+"");
				 writeCommands("(s1p9h14v-7B");
				 writeTextWithPosition(""+pos,""+title+"\r\n \r\n");
				 writeCommands("(s1p12h10v-7B");
				 
			  String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
				
			  String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
				
			  writeText("Period: From "+stratDate+" to "+endDate);
			  writeTextWithPosition("80",""+header+"\r\n \r\n");
			  writeCommands("(s1p12h10v1B");
			  writeText("Doc No");
			  writeTextWithPosition("30", "Code".toUpperCase());
			  writeTextWithPosition("56", "Name".toUpperCase());
			  writeTextWithPosition("130", "Payment Type".toUpperCase());
			  writeTextWithPosition("190", "Inv No".toUpperCase());
			  writeTextWithPosition("220", "Amt Paid".toUpperCase());
			  writeCommands("(s1p12h10v-7B");
			  writehorinzontaldividerLandscapeHeader();
			 
			 for(int  i = outerCount; i < veCollection.size(); i++,outerCount++)
			 {
				 Log.e("Printed", ""+i);
				Customer_InvoiceDO customerInvoiceDO = veCollection.get(i);
				if(customerInvoiceDO.vecPaymentDetailDOs != null)
				{
					for(int j=innerCount; j <customerInvoiceDO.vecPaymentDetailDOs.size(); j++,count++,innerCount++ )
					{
						if(count > maxPaymentSummeryCount)
						{
							isPageFinished = true;
							break;
						}
						else
						{
							isPageFinished = false;
							PaymentDetailDO objDetailDO 	    = 	customerInvoiceDO.vecPaymentDetailDOs.get(j);
							totalPaymentSummeryPrice = totalPaymentSummeryPrice + StringUtils.getFloat(objDetailDO.invoiceAmount);
							String ChequeNo = "";
							if(customerInvoiceDO.paymentType.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE))
							{
								String date = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT,customerInvoiceDO.reciptDate);
								 String checkDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, customerInvoiceDO.chequeDate);
								String branch = "";
								if(customerInvoiceDO.branch != null && !customerInvoiceDO.branch.equalsIgnoreCase("")&& !customerInvoiceDO.branch.equalsIgnoreCase("null") )
								{
									branch = "/"+customerInvoiceDO.branch;
								}
							     if (dateDiffence(date,checkDate)>=AppConstants.PDC_DATE_CONSTANT)
							     {
							    	 totalPaymentPDCPrice = totalPaymentPDCPrice + StringUtils.getFloat(objDetailDO.invoiceAmount);
										ChequeNo = "PDC "+customerInvoiceDO.chequeNo+"/"+ checkDate  +"/"+customerInvoiceDO.bankName+branch;
							     }
							     else
							     {
							    	 totalPaymentCheckPrice = totalPaymentCheckPrice + StringUtils.getFloat(objDetailDO.invoiceAmount);
							    	 ChequeNo = "Cheque "+customerInvoiceDO.chequeNo+"/"+ checkDate  +"/"+customerInvoiceDO.bankName+branch; 
							     }
								
							}
							else
							{
								totalPaymentCashPrice = totalPaymentCashPrice + StringUtils.getFloat(objDetailDO.invoiceAmount);
								ChequeNo = "Cash";
							}
							writeText(""+customerInvoiceDO.receiptNo);
							if(!TextUtils.isEmpty(customerInvoiceDO.clientBranchCode))
							{
								writeTextWithPosition(""+30, ""+customerInvoiceDO.customerSiteId+" ["+customerInvoiceDO.clientBranchCode+"]");
								String strCustomerName =  ""+customerInvoiceDO.siteName+" ["+customerInvoiceDO.cashcustomerSiteName+"]";
								if(strCustomerName.length()>34)
									strCustomerName = strCustomerName.substring(0, 34);
								writeTextWithPosition(""+56,strCustomerName);
							}
							else
							{
								writeTextWithPosition(""+30, ""+customerInvoiceDO.customerSiteId);
								String strCustomerName =  ""+customerInvoiceDO.siteName;
								if(strCustomerName.length()>34)
									strCustomerName = strCustomerName.substring(0, 34);
								writeTextWithPosition(""+56,strCustomerName);
							}
							if(ChequeNo.length()>32)
								ChequeNo = ChequeNo.substring(0, 32);
							writeTextWithPosition(""+130, ""+ChequeNo);
							writeTextWithPosition(""+190, ""+objDetailDO.invoiceNumber);
							String StrAmt = ""+amountFormate.format(StringUtils.getFloat(objDetailDO.invoiceAmount));
							int strAmtPos = (int) (240 - StrAmt.length()*1.67);
							writeTextWithPosition(""+strAmtPos, ""+StrAmt+"\n");
						}
					 }
					
				}
				
		
				if(isPageFinished)
					break;
				else
					innerCount = 0;
			}
			 int pageNo = pagecount+1;
			 
			 if(!isPageFinished)
			 {
				 int spAmount =maxPaymentSummeryCount-count;
				  for(int i = 0 ;i <= spAmount ;i++)
				  {
					  writeText("\r\n");
				  }
				  writeText("\r\n");
				  writehorinzontaldividerLandscapeHeader();
				  
				  writeTextWithPosition(""+180, "TOTAL CASH RECEIVED :");
				  String strtotalPaymentCashPrice = ""+amountFormate.format(totalPaymentCashPrice);
				  int totalPaymentCashPricePos = (int) (241 - strtotalPaymentCashPrice.length()*1.67);
				  writeTextWithPosition(""+totalPaymentCashPricePos, ""+strtotalPaymentCashPrice+"\n");
				  
				  writeTextWithPosition(""+188, "CURR-CHQs TOTAL :");
				  String strtotalPaymentCheckPrice = ""+amountFormate.format(totalPaymentCheckPrice);
				  int totalPaymentCheckPricePos = (int) (241 - strtotalPaymentCheckPrice.length()*1.67);
				  writeTextWithPosition(""+totalPaymentCheckPricePos, ""+strtotalPaymentCheckPrice+"\n");
				  
				  writeTextWithPosition(""+199, "PDCs TOTAL :");
				  String strtotalPaymentPDCPrice = ""+amountFormate.format(totalPaymentPDCPrice);
				  int totalPaymentPDCPricePos = (int) (241 - strtotalPaymentPDCPrice.length()*1.67);
				  writeTextWithPosition(""+totalPaymentPDCPricePos, ""+strtotalPaymentPDCPrice+"\n");
				  
				  writeTextWithPosition(""+208, "TOTAL :");
				  String strtotalPaymentSummeryPrice = ""+amountFormate.format(totalPaymentSummeryPrice);
				  int totalPaymentSummeryPricePos = (int) (241 - strtotalPaymentSummeryPrice.length()*1.67);
				  writeTextWithPosition(""+totalPaymentSummeryPricePos, ""+strtotalPaymentSummeryPrice+"\n");
				  
			 }
			 else
			 {
				 writeText("\r\n");
				 writeText("\r\n");
				 writeText("\r\n");
				 writeText("\r\n \r\n \r\n");
			 }
			 writeText("\r\n");
			 String strpage = "page # "+pageNo+"/"+totalPages;
			 int strpagePos =  (int) (242 -(1.67*strpage.length()));
			 writeTextWithPosition(""+strpagePos,strpage +"\n");
		 }
		 //For Printing Log Report Summary
		private int maxLogReportSummeryCount = 21;
		public void  printLogReportSummary( TrxLogHeaders trxLogHeaders, TrxLogHeaders trxMonthDetails ,String fromDate ,String toDate,int isMTD )
		 {
			if(mmOutputStream!=null)
			{
				if (trxLogHeaders != null && trxLogHeaders.vecTrxLogDetailsDO!= null && trxLogHeaders.vecTrxLogDetailsDO.size()>0) 
				{
					try 
					{
						int totalPages = (trxLogHeaders.vecTrxLogDetailsDO.size()+maxLogReportSummeryCount-1)/maxLogReportSummeryCount;
						for(int pages=0; pages < totalPages;pages++)
						{
							printLogReportSummaryInner(trxLogHeaders,trxMonthDetails,pages,totalPages,fromDate,toDate,isMTD );
						}
						closeBT();
					}
					catch (UnsupportedEncodingException e) 
					{
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
				
			}
			
			else{
				((Activity)context).runOnUiThread(new Runnable() 
				{
					@Override
					public void run() {
						Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
					}
				});
			}
		 }
		public void printLogReportSummaryInner(TrxLogHeaders trxLogHeaders, TrxLogHeaders trxMonthDetails,int pagecount,int totalPages ,String fromDate ,String toDate,int isMTD) throws UnsupportedEncodingException, IOException
		 {
			String mobNo="";
			if(userMobileNumber!=null && !userMobileNumber.equalsIgnoreCase("") && !userMobileNumber.equalsIgnoreCase("null"))
			{
				mobNo = " - "+userMobileNumber;
			}
			  String title              = "LOG REPORT SUMMARY";
			  String formatDate			= "%1$160.160s\r\n";
			  String header 			= "Sls.Man: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" ) "+ mobNo+"                         ";
			  String date 				= CalendarUtils.getCurrentSynchDateTime();
			  
			  	 int pos = (int)((165-1.67*title.length())/2);
			  	 writeCommands("&l1O");
				 writeCommands("&l5D");
				 writeCommands("(s0p12.3h10v4s-7B");
				 writeText(""+String.format(formatDate,date)+"");
				 writeCommands("(s1p9h14v-7B");
				 writeTextWithPosition(""+pos,""+title+"\r\n \r\n");
				 writeCommands("(s1p12h10v-7B");
			  String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
			  String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
			  writeText("Period: From "+stratDate+" to "+endDate);
			  writeTextWithPosition("80",""+header+"\r\n \r\n");
			  writeCommands("(s1p12h10v1B");
			  writeText("Code");
			  writeTextWithPosition("20", "Customer Name".toUpperCase());
			  writeTextWithPosition("100", "As per JP".toUpperCase());
			  writeTextWithPosition("120", "Type".toUpperCase());
			  writeTextWithPosition("150", "Doc No".toUpperCase());
			  writeTextWithPosition("177", "Amount".toUpperCase());
			  writeTextWithPosition("216", "Time Stamp".toUpperCase());
			  writeCommands("(s1p12h10v-7B");
			 // writehorinzontaldividerLandscapeHeader();

			 for(int  i = pagecount*maxLogReportSummeryCount, count = 0 ; i < trxLogHeaders.vecTrxLogDetailsDO.size() && count < maxLogReportSummeryCount  ; i++, count++)
			 {
				TrxLogDetailsDO trxLogDetailsDO = trxLogHeaders.vecTrxLogDetailsDO.get(i);
				if(trxLogDetailsDO != null)
				{
					String strDocNo = "";
					if(trxLogDetailsDO.DocumentNumber!=null && trxLogDetailsDO.DocumentNumber.equalsIgnoreCase(""))
						strDocNo = "N/A";
					else
						strDocNo = ""+trxLogDetailsDO.DocumentNumber;
					String strAmount = "";
					if((""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount))).equalsIgnoreCase(""))
						strAmount = ""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount));
					else
						strAmount = "0.00";
					int amtPos = (int) (193 - strAmount.length()*1.67);
					String strAsperJP = trxLogDetailsDO.IsJp.equalsIgnoreCase("True")?"Yes":"No";
					String timeStamp = CalendarUtils.parseDate(CalendarUtils.DATE_PATTERN, CalendarUtils.SYNCH_DATE_TIME_PATTERN, trxLogDetailsDO.TimeStamp);
					writeText(""+trxLogDetailsDO.CustomerCode);
					String strName = ""+trxLogDetailsDO.CustomerName;
					if(strName.length()>37)
						strName = strName.substring(0, 37);
					  writeTextWithPosition("20",strName );
					  writeTextWithPosition("100", ""+strAsperJP);
					  writeTextWithPosition("120", ""+trxLogDetailsDO.TrxType);
					  writeTextWithPosition("150", ""+strDocNo);
					  writeTextWithPosition(""+amtPos, ""+strAmount);
					  writeTextWithPosition("202", ""+timeStamp+"\n");
				}
			 }
			 int pageNo = pagecount+1;
			 
			 if(pageNo==totalPages)
			 {
				  int invoicesizoe = trxLogHeaders.vecTrxLogDetailsDO.size()-pagecount*maxLogReportSummeryCount;
				  int spAmount =maxLogReportSummeryCount-invoicesizoe;
				  for(int i = 0 ;i <= spAmount ;i++)
				  {
					  writeText("\r\n");
				  }
				  writehorinzontaldividerLandscape();
				  writeVerticalDivider(""+0);
				  writeCommands("(s1p12h10v1B");
				  writeTextWithPosition("55", "TODAY'S");
				  writeVerticalDivider(""+117);
				  writeCommands("(s1p12h10v1B");
				  writeTextWithPosition("180", "MTD'S");
				  writeCommands("(s1p12h10v-7B");
				  writeVerticalDivider(""+237);
				  writehorinzontaldividerLandscape();
				 if(isMTD==0)
				 {
					 writeFooterText("SALES", "AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalSales)), "SCHEDULED CALL", ""+trxLogHeaders.TotalScheduledCalls,"SALES", "AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalSales)), "SCHEDULED CALL", ""+trxMonthDetails.TotalScheduledCalls);
					 writeFooterText("CREDIT NOTE","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCreditNotes)), "ACTUAL CALLS",""+trxLogHeaders.TotalActualCalls, "CREDIT NOTE","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCreditNotes)), "ACTUAL CALLS", ""+trxMonthDetails.TotalActualCalls);
					 writeFooterText("COLLECTION", "AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCollections)), "PRODUCTIVE CALLS",""+trxLogHeaders.TotalProductiveCalls, "COLLECTION","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCollections)), "PRODUCTIVE CALLS", ""+trxMonthDetails.TotalProductiveCalls);
					 writeFooterText("ACTUAL CALL (JP)",""+trxLogHeaders.TotalActualCallsPlanned, "PRODUCTIVE CALL(JP)", ""+trxLogHeaders.TotalProductiveCallsPlanned, "ACTUAL CALL (JP)",""+trxMonthDetails.TotalActualCallsPlanned, "PRODUCTIVE CALL(JP)", ""+trxMonthDetails.TotalProductiveCallsPlanned);
				 }
				 else
				 {
					 writeFooterText("SALES", "AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalSales)), "SCHEDULED CALL", ""+trxMonthDetails.TotalScheduledCalls,"SALES", "AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalSales)), "SCHEDULED CALL", ""+trxLogHeaders.TotalScheduledCalls);
					 writeFooterText("CREDIT NOTE","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCreditNotes)), "ACTUAL CALLS",""+trxMonthDetails.TotalActualCalls, "CREDIT NOTE","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCreditNotes)), "ACTUAL CALLS", ""+trxLogHeaders.TotalActualCalls);
					 writeFooterText("COLLECTION", "AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCollections)), "PRODUCTIVE CALLS",""+trxMonthDetails.TotalProductiveCalls, "COLLECTION","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCollections)), "PRODUCTIVE CALLS", ""+trxLogHeaders.TotalProductiveCalls);
					 writeFooterText("ACTUAL CALL (JP)",""+trxMonthDetails.TotalActualCallsPlanned, "PRODUCTIVE CALL(JP)", ""+trxMonthDetails.TotalProductiveCallsPlanned, "ACTUAL CALL (JP)",""+trxLogHeaders.TotalActualCallsPlanned, "PRODUCTIVE CALL(JP)", ""+trxLogHeaders.TotalProductiveCallsPlanned);
				 }
				 writeVerticalDivider(""+0);
				 writeVerticalDivider(""+63);
				 writeVerticalDivider(""+117);
				 writeVerticalDivider(""+184);
				 writeVerticalDivider(""+237);
				 writehorinzontaldividerLandscape();
				  writeText("\r\n");
			 }
			 else
			 {
				 writeText("\r\n \r\n \r\n \r\n \r\n \r\n \r\n \r\n \r\n");
			 }
			 String strpage = "page # "+pageNo+"/"+totalPages;
			 int strpagePos =  (int) (242 -(1.67*strpage.length()));
			 writeTextWithPosition(""+strpagePos,strpage +"\n");
		 }
		public void sortByDate(Vector<Customer_InvoiceDO> vec){
			Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			    @Override
			   public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) 
			    {
			        return s1.chequeDate.compareTo(s2.chequeDate);
			    }
			});
		}
		public void sortByInvoiceReciept(Vector<Customer_InvoiceDO> vec){
			Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			    @Override
			   public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) 
			    {
			        return s1.receiptNo.compareTo(s2.receiptNo);
			    }
			});
		}
		 // For Printing Full Van Stock Summary
		private int maxFullVanStockInventorySummaryCount = 40;
		private int totalFullVanStockInventorySummaryInventory;
		private int totalSalbleVanStockInventorySummaryInventory;
		private int totalNonVanStockInventorySummaryInventory;
		public void printFullVanStockInventorySummary(ArrayList<VanLoadDO> vecArrayList)
		{
			try 
			{
				int totalPages = (vecArrayList.size()+maxFullVanStockInventorySummaryCount-1)/maxFullVanStockInventorySummaryCount;
				totalFullVanStockInventorySummaryInventory = 0;
				totalSalbleVanStockInventorySummaryInventory = 0;
				totalNonVanStockInventorySummaryInventory	= 0;
				if(mmOutputStream!=null || isTestMode)
				{
					StringBuilder strStockInv = new StringBuilder();
					for(int pages=0; pages < totalPages;pages++)
					{
						strStockInv.append(printFullVanStockInventorySummaryInner(vecArrayList,pages,totalPages));
					}
					Log.e("Van Stock ", strStockInv.toString());
					closeBT();
				} 
				else{
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
			catch (NullPointerException e) 
			{
			    e.printStackTrace();
			} catch (Exception e) 
			{
			    e.printStackTrace();
			}
			
		}
		public String printFullVanStockInventorySummaryInner(ArrayList<VanLoadDO> vecArrayList,int pagecount,int totalPages) throws UnsupportedEncodingException, IOException
		{
			 String formatDate  		= "%1$126.126s \r\n";
			 String header 			= "Full Van Stock Summary Report for "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" ) "+" - "+ userVehicleNumber;
			 String seprator			= "____________________________________________________________________________________________________________________________________________________";
			 String formatSeprator 	= "%1$-76.76s \r\n";
			 String date = CalendarUtils.getCurrentSynchDateTime();
			 
		 	String formatHeader  		= "%1$-76.76s \r\n";
		  	String formateStockInv	= "%1$5.5s %2$-18.18s %3$-18.18s %4$-7.7s %5$7.7s %6$7.7s %7$7.7s\r\n";
		    String formateTotal		= "%1$5.5s %2$-18.18s %3$-18.18s %4$-7.7s %5$7.7s %6$7.7s %7$7.7s\r\n";
		    StringBuilder strStockInv = new StringBuilder();
		    strStockInv.append("\r\n \r\n \r\n \r\n");
//		  	strStockInv.append(""+header);
			int lenth = (""+header).length();
		  	int pos = (76-lenth)/2;
		  	String formateHeader			= "%1$3.3s %2$-"+pos+"."+pos+"s %3$-"+lenth+"."+lenth+"s\r\n";
		  	strStockInv.append(String.format(formateHeader,"","",""+header));
		  	strStockInv.append(getSeprator());
		  	strStockInv.append(String.format(formateStockInv,"S.No","Product Code","Model/Color","UOM","S. QTY","N. QTY","T. QTY"));
		  	strStockInv.append(getSeprator());
		  	strStockInv.append("\r\n");
			  	
				for(int  i = pagecount*maxFullVanStockInventorySummaryCount, count = 0 ; i < vecArrayList.size() && count < maxFullVanStockInventorySummaryCount  ; i++, count++)
			    {
					VanLoadDO vanLoadDO=vecArrayList.get(i);
					int srNUmber = i+1;
					strStockInv.append(String.format(formateStockInv,""+srNUmber,""+vanLoadDO.ItemCode,""+vanLoadDO.Description,""+TrxDetailsDO.getItemUomLevel3(),""+(int)vanLoadDO.SellableQuantity,""+(int)vanLoadDO.NonSellableQuantity,""+(int)vanLoadDO.TotalQuantity));
					totalFullVanStockInventorySummaryInventory = totalFullVanStockInventorySummaryInventory+(int)vanLoadDO.TotalQuantity;
					totalSalbleVanStockInventorySummaryInventory = totalSalbleVanStockInventorySummaryInventory+(int)vanLoadDO.SellableQuantity;
					totalNonVanStockInventorySummaryInventory = totalNonVanStockInventorySummaryInventory+(int)vanLoadDO.NonSellableQuantity;
			    }
			 int pageNo = pagecount+1;
			 if(pageNo==totalPages)
			 {
				 int spAmount = pageNo*maxFullVanStockInventorySummaryCount - vecArrayList.size();
				  for(int i = 1 ;i <= spAmount ;i++)
				  {
					  strStockInv.append("\r\n");
				  }
				  strStockInv.append(getSeprator());
				  strStockInv.append(String.format(formateTotal,"","Total :","","",""+totalSalbleVanStockInventorySummaryInventory,""+totalNonVanStockInventorySummaryInventory,""+totalFullVanStockInventorySummaryInventory));
				  strStockInv.append(getSeprator());
				  strStockInv.append("\r\n");
			 }
			 else
			 {
				 strStockInv.append("\r\n");
				 strStockInv.append("\r\n");
			 }
			 String page =""+"Page # "+pageNo+"/"+totalPages;
			 strStockInv.append("\r\n");
			 strStockInv.append("\r\n");
			 return strStockInv.toString();
		 }
		
		
		 // For Printing Full Van Stock Summary
		private int maxSalesmanKBICount = 22;
		public void printSalesmanKBI(Vector<Object> vecArrayList,String toDate)
		{
			try 
			{
				int totalPages = (vecArrayList.size()+maxSalesmanKBICount-2)/maxSalesmanKBICount;
				Vector<UserAgencyTargetDO> vecAgencyTargetDOs=(Vector<UserAgencyTargetDO>) vecArrayList.get(0);
				int dayCalls[] = (int[])vecArrayList.get(1);
				int monthCalls[] = (int[])vecArrayList.get(2);
				float totalDayAchived=(Float) vecArrayList.get(3);
				float totalMonthAchived =(Float) vecArrayList.get(4);
				
				float jpAdherance=(Float) vecArrayList.get(5);
				float timeGone=(Float) vecArrayList.get(6);
				int uniqueCalls = (Integer) vecArrayList.get(7);
				int zeroBills=(Integer) vecArrayList.get(8);
				int  workingDays=(Integer) vecArrayList.get(9);
				int balanceDays=(Integer) vecArrayList.get(10);
				
				if(mmOutputStream!=null)
				{
					for(int pages=0; pages < totalPages;pages++)
					{
						printSalesmanKBIInner(vecAgencyTargetDOs,dayCalls,monthCalls,pages,totalPages,toDate,jpAdherance,timeGone,uniqueCalls,zeroBills,workingDays,balanceDays);
					}
					closeBT();
				} 
				else{
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
			catch (NullPointerException e) 
			{
			    e.printStackTrace();
			} catch (Exception e) 
			{
			    e.printStackTrace();
			}
			
		}
		public void printSalesmanKBIInner(Vector<UserAgencyTargetDO> vecAgencyTargetDOs,int dayCalls[],int monthCalls[],int pagecount,int totalPages,String toDate, float jpAdherance, float timeGone, int uniqueCalls, int zeroBills, int workingDays, int balanceDays) throws UnsupportedEncodingException, IOException
		{
			 String formatDate  		= "%1$126.126s \r\n";
			 String header 			= "Salesman Key Business Indicators Report for "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" ) "+" - "+ userVehicleNumber;
			 String seprator			= "____________________________________________________________________________________________________________________________________________________";
			 String formatSeprator 	= "%1$-76.76s \r\n";
			 String date = CalendarUtils.getCurrentSynchDateTime();
			 writeCommands("&l5D");
			 writeCommands("(s0p12.3h10v4s-7B");
			 writeText(""+String.format(formatDate,date)+"\r\n");
			 writeCommands("(s1p9h14v-7B");
			 int pos = (int)((150-1.67*header.length())/2);
			 writeTextWithPosition(""+pos,header+"\r\n");
			 writeText("\r\n");
			 writeCommands("(s1p12h10v1B");
			 writeTextWithPosition("2", "Date".toUpperCase()); 
			 writeTextWithPosition("15",":" );
			 String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
			 writeTextWithPosition(""+17,endDate +" \r \n" );
			 writeCommands("(s1p12h10v-7B");
			 writehorinzontaldivider();
			 writeVerticalDivider(""+0);
			 writeCommands("(s1p12h10v1B");
			 writeTextWithPosition("41", "TODAY's".toUpperCase());
			 writeCommands("(s1p12h10v-7B");
			 writeVerticalDivider(""+97);
			 writeCommands("(s1p12h10v1B");
			 writeTextWithPosition("140", "MTD's".toUpperCase());
			 writeCommands("(s1p12h10v-7B");
			 writeVerticalDivider(""+190);
			 writeText("\r\n");
			 
			 writeVerticalDivider(""+0);
			 writeVerticalDivider(""+97);
			 writeVerticalDivider(""+190);
			 writehorinzontaldivider();
			 
			 writeHeaderText("SCHEDULED CALLS".toUpperCase(), ""+dayCalls[0], "SCHEDULED CALLS".toUpperCase(), ""+monthCalls[0]);
			 writeHeaderText("ACTUAL CALLS".toUpperCase(), ""+dayCalls[1], "ACTUAL CALLS".toUpperCase(), ""+monthCalls[1]);
			 writeHeaderText("PRODUCTIVE CALLS".toUpperCase(), ""+dayCalls[2], "PRODUCTIVE CALLS".toUpperCase(), ""+monthCalls[2]);
			 writeHeaderText("ACTUAL CALLS AS PER JP".toUpperCase(), ""+dayCalls[3], "ACTUAL CALLS AS PER JP".toUpperCase(), ""+monthCalls[3]);
			 writeHeaderText("PRODUCTIVE CALLS AS PER JP".toUpperCase(), ""+dayCalls[4], "PRODUCTIVE CALLS AS PER JP".toUpperCase(), ""+monthCalls[4]);
			
			 writeVerticalDivider(""+0);
			 writeVerticalDivider(""+97);
			 writeVerticalDivider(""+190);
			 writehorinzontaldivider();
			 writeCommands("(s1p12h10v-7B");
			 writehorinzontaldivider();
			 writeVerticalDivider(""+0);
			 writeCommands("(s1p12h10v1B");
			 writeTextWithPosition("92", "MTD's".toUpperCase());
			 writeCommands("(s1p12h10v-7B");
			 writeVerticalDivider(""+190);
			 writeText("\r\n");
			 
			 writeVerticalDivider(""+0);
			 writeVerticalDivider(""+190);
			 writehorinzontaldivider();
			 
			 writeHeaderText("JP Adherence".toUpperCase(), ""+deffAmt.format((jpAdherance))+" %", "ZERO BILLS".toUpperCase(), ""+zeroBills);
			 writeHeaderText("Time Gone".toUpperCase(), ""+deffAmt.format((timeGone))+" %", "Working Days".toUpperCase(), ""+workingDays);
			 writeHeaderText("UNIQUE CALLS".toUpperCase(), ""+uniqueCalls, "Balance Days".toUpperCase(), ""+balanceDays);
			
			 writeVerticalDivider(""+0);
			 writeVerticalDivider(""+97);
			 writeVerticalDivider(""+190);
			 writehorinzontaldivider();
			 
			 writeText("\r\n");
			 
			 writeCommands("(s1p12h10v1B");
			 writeText("AGENCY");	 
//			 writeTextWithPosition("10", "AGENCY"); 
			 writeTextWithPosition("72", "TARGET");
			 writeTextWithPosition("94", "DAY SALES");
			 writeTextWithPosition("122", "MTD SALES");
			 writeTextWithPosition("148", "ACHIEVED");
			 writeTextWithPosition("172", "BALANCE \n");
			 
			 writeCommands("(s0p12.3h10v4s7B");
			 writeTextWithPosition("51", "(AED)");
			  writeTextWithPosition("68", "(AED)");
			 writeTextWithPosition("87", "(AED)");
			 writeTextWithPosition("104", "(%)");
			 writeTextWithPosition("119", "(AED)");
			 
			 writeCommands("(s0p10h9v-7B");
			 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
			 writeCommands("(s1p12h10v-7B");
				for(int  i = pagecount*maxSalesmanKBICount, count = 0 ; i < (vecAgencyTargetDOs.size()-1) && count < maxSalesmanKBICount  ; i++, count++)
			    {
					UserAgencyTargetDO userAgencyTargetDO=vecAgencyTargetDOs.get(i);
//					int srNUmber = i+1;
//					 writeText(""+srNUmber);
					String strAgency  = ""+userAgencyTargetDO.AliasName;
					if(strAgency.length() > 30)
						strAgency = strAgency.substring(0, 30);
					 writeText(""+strAgency);
					 
					 String Target = "";
					 if(userAgencyTargetDO.Target>0)
						 Target =""+deffAmt.format(userAgencyTargetDO.Target);
					 else
						 Target ="N/A";
					int moveToPosTarget = (int) (87 -(1.67*Target.length()));
					writeTextWithPosition(""+moveToPosTarget,Target+"");
					 
					String dayAcheived = "";
					if(userAgencyTargetDO.dayAcheived>0)
						dayAcheived =""+deffAmt.format(userAgencyTargetDO.dayAcheived); 
					else
						dayAcheived ="N/A";
					int moveToPosdayAcheived = (int) (115 -(1.67*dayAcheived.length()));
					writeTextWithPosition(""+moveToPosdayAcheived,dayAcheived+"");
						
					String monthlyAcheived = "";
					if(userAgencyTargetDO.monthlyAcheived>0)
						monthlyAcheived =""+deffAmt.format(userAgencyTargetDO.monthlyAcheived);
					else
						monthlyAcheived ="N/A";
					int moveToPosmonthlyAcheived = (int) (143 -(1.67*monthlyAcheived.length()));
					writeTextWithPosition(""+moveToPosmonthlyAcheived,monthlyAcheived+"");
					
					float percentage =0.0f;
					if(userAgencyTargetDO.Target>0)
						percentage = (float) ((userAgencyTargetDO.monthlyAcheived/userAgencyTargetDO.Target)*100);
					String 	strPercentage = ""+deffAmt.format((percentage));
					int moveToPosPercentage = (int) (167 -(1.67*strPercentage.length()));
					writeTextWithPosition(""+moveToPosPercentage,strPercentage+"");
					
					float balance=(float) (userAgencyTargetDO.Target-userAgencyTargetDO.monthlyAcheived);
					String balanceAmt = ""+deffAmt.format((Math.abs(balance))); // ""+deffAmt.format(Math.abs(balance));
					int moveToPosbalance = (int) (190 -(1.67*balanceAmt.length()));
					writeTextWithPosition(""+moveToPosbalance,balanceAmt+"\n");
			    }
			 int pageNo = pagecount+1;
			 if(pageNo==totalPages)
			 {
				 int spAmount = pageNo*maxSalesmanKBICount+1 - vecAgencyTargetDOs.size();
				  for(int i = 1 ;i <= spAmount ;i++)
				  {
					  writeText("\r\n");
				  }
				  
				 writeCommands("(s0p10h9v-7B");
				 writeText(""+String.format(formatSeprator,seprator));
				 writeCommands("(s1p12h10v1B");
				 UserAgencyTargetDO userAgencyTargetDO=vecAgencyTargetDOs.get((vecAgencyTargetDOs.size()-1));
				 writeText(""+userAgencyTargetDO.AliasName);
					 
				 String Target = "";
				 if(userAgencyTargetDO.Target>0)
					Target =""+deffAmt.format(userAgencyTargetDO.Target);
				 else
					Target ="N/A";
				 int moveToPosTarget = (int) (85 -(1.67*Target.length()));
				 writeTextWithPosition(""+moveToPosTarget,Target+"");
					 
				 String dayAcheived = "";
				 if(userAgencyTargetDO.dayAcheived>0)
					dayAcheived =""+deffAmt.format(userAgencyTargetDO.dayAcheived); 
				 else
					dayAcheived ="N/A";
				 int moveToPosdayAcheived = (int) (114 -(1.67*dayAcheived.length()));
				 writeTextWithPosition(""+moveToPosdayAcheived,dayAcheived+"");
						
				String monthlyAcheived = "";
				if(userAgencyTargetDO.monthlyAcheived>0)
					monthlyAcheived =""+deffAmt.format(userAgencyTargetDO.monthlyAcheived);
				else
					monthlyAcheived ="N/A";
				int moveToPosmonthlyAcheived = (int) (141 -(1.67*monthlyAcheived.length()));
				writeTextWithPosition(""+moveToPosmonthlyAcheived,monthlyAcheived+"");
				
				float percentage =0.0f;
				if(userAgencyTargetDO.Target>0)
					percentage = (float) ((userAgencyTargetDO.monthlyAcheived / userAgencyTargetDO.Target)*100);
				String 	strPercentage = ""+deffAmt.format((percentage))+" %";
				int moveToPosPercentage = (int) (165 -(1.67*strPercentage.length()));
				writeTextWithPosition(""+moveToPosPercentage,strPercentage+"");
				
				float balance=(float) (userAgencyTargetDO.Target-userAgencyTargetDO.monthlyAcheived);
				String balanceAmt = ""+deffAmt.format(Math.abs(balance)); // ""+deffAmt.format(Math.abs(balance));
				int moveToPosbalance = (int) (188 -(1.67*balanceAmt.length()));
				writeTextWithPosition(""+moveToPosbalance,balanceAmt+"\n");
				writeCommands("(s1p12h10v-7B");
				writeText("\r\n");
			 }
			 else
			 {
				 writeText("\r\n");
				 writeText("\r\n");
				 writeText("\r\n");
			 }
			 String page =""+"Page # "+pageNo+"/"+totalPages;
			 int moveToPosTotalPage = (int) (192 -(1.67*page.length()));
			 writeTextWithPosition(""+moveToPosTotalPage,page+"\r\n");
		 }
	 */
	
	public String getSeprator()
	{
		 String seprator			= "-----------------------------------------------------------------------------------------";
		 String formatSeprator 	= "%1$-80.80s \r \n";
		return String.format(formatSeprator,seprator);
	}
		//For Printing Pending Invoices Summary
		private int maxPendingPaymentSummaryCount = 35;
//		private int maxPendingPaymentSummaryBottom = 3;
		private float totalPendingPaymentSummaryPrice;
		/*public void printPendingPaymentSummary(ArrayList<PendingInvicesDO> arrInvoiceNumbers, JourneyPlanDO mallsDetails)
		 {
			int totalPages = (arrInvoiceNumbers.size()+maxPendingPaymentSummaryCount-1)/maxPendingPaymentSummaryCount;
			totalPendingPaymentSummaryPrice = 0;
			try 
			{
				if(mmOutputStream!=null)
				{
					for(int pages=0; pages < totalPages;pages++)
					{
						 printPendingPaymentSummaryInner(arrInvoiceNumbers,mallsDetails,pages,totalPages);
					}
					closeBT();
				} 
				else{
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
			} 
			catch (NullPointerException e) 
			{
			    e.printStackTrace();
			} catch (Exception e) 
			{
			    e.printStackTrace();
			}
		 }
		public void printPendingPaymentSummaryInner(ArrayList<PendingInvicesDO> arrInvoiceNumbers, JourneyPlanDO mallsDetails,int pagecount,int totalPages) throws UnsupportedEncodingException, IOException
		{
				String header = "Pending Payment List";
			  String formatHeader  		= "%1$-76.76s \r\n";
			  String formatDate  		= "%1$126.126s \r\n";
			  String seprator			= "____________________________________________________________________________________________________________________________________________________";
			  String formatSeprator 	= "%1$-76.76s \r\n";
			  int pos = (int)((130-1.67*header.length())/2);
			 String date = CalendarUtils.getCurrentSynchDateTime();
			 writeCommands("&l5D");
			 writeCommands("(s0p12.3h10v4s-7B");
			 writeText(""+String.format(formatDate,date)+"\r\n");
			 writeCommands("(s1p9h14v-7B");
			 writeTextWithPosition(""+pos,""+String.format(formatHeader,header)+"\r\n");
			 writeCommands("(s1p12h10v-7B");
			 writeText("Cust Name");
			 writeTextWithPosition("18", ": "+mallsDetails.siteName);
			 writeTextWithPosition("94", "Cust No");
//			 writeTextWithPosition("108", ": "+mallsDetails.site);
			 if(TextUtils.isEmpty(mallsDetails.clientBranchCode))
				  writeTextWithPosition("108",": "+mallsDetails.site);
			  else
					writeTextWithPosition("108",": "+mallsDetails.site+" ["+mallsDetails.clientBranchCode+"]");
			 writeTextWithPosition("146","Tel No");
			 String Mobile = "N/A";
			 if(mallsDetails.mobileNo1!=null && !mallsDetails.mobileNo1.equalsIgnoreCase(""))
				 Mobile = mallsDetails.mobileNo1;
			 writeTextWithPosition("158",": "+Mobile+"\r \n");
			 if(!TextUtils.isEmpty(mallsDetails.clientBranchCode))
				 writeTextWithPosition("20", ""+mallsDetails.customerSiteName);
			 writeTextWithPosition("94", "S. Org");
			 writeTextWithPosition("108",": "+preference.getStringFromPreference(Preference.USER_ID, ""));
			 writeTextWithPosition("146","Van No");
			 String userVehical = "";
			 if(userVehicleNumber!=null && !userVehicleNumber.equalsIgnoreCase(""))
			   userVehical = userVehicleNumber;
			 writeTextWithPosition("158",": "+userVehical);
			 writeText("\r\n");
			 String slName = "";
			 if(userMobileNumber!=null && !userMobileNumber.equalsIgnoreCase(""))
				 slName =preference.getStringFromPreference(Preference.USER_NAME, "")+" - "+userMobileNumber;
			 else
				 slName =preference.getStringFromPreference(Preference.USER_NAME, "");
			 writeText( "S. Name");
			 writeTextWithPosition("18", ": "+slName+"\n");
			 writeText("\r\n");
			 writeCommands("(s1p12h10v1B");
			 writeText("S1#".toUpperCase());
			 writeTextWithPosition("12", "Trx Type".toUpperCase()); 
			 writeTextWithPosition("40", "Trx No".toUpperCase()); 
			 writeTextWithPosition("95", "Trx Dt".toUpperCase()); 
			 writeTextWithPosition("162", "Amount (AED)".toUpperCase());
			 writeCommands("(s0p10h9v-7B");
			 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
			 writeCommands("(s1p12h10v-7B");
			
			 for(int  i = pagecount*maxPendingPaymentSummaryCount, count = 0 ; i < arrInvoiceNumbers.size() && count < maxPendingPaymentSummaryCount  ; i++, count++)
			    {
					PendingInvicesDO pendingInvicesDO=arrInvoiceNumbers.get(i);
					int srNUmber = i+1;
					String invoieDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, pendingInvicesDO.invoiceDate);
					String amount = ""+deffAmt.format(StringUtils.getFloat(pendingInvicesDO.totalAmount));
					totalPendingPaymentSummaryPrice = totalPendingPaymentSummaryPrice+StringUtils.getFloat(pendingInvicesDO.totalAmount);
					writeText(""+srNUmber);	 
					writeTextWithPosition("12", ""+PendingInvicesDO.getPendingInvoiceType(pendingInvicesDO.INV_TYPE));
					writeTextWithPosition("40", ""+pendingInvicesDO.invoiceNo); 
					writeTextWithPosition("95", ""+invoieDate);
					int amtWidth = 191;
					if(StringUtils.getFloat(pendingInvicesDO.totalAmount) < 0)
						amtWidth = 191;
					else
						amtWidth = 190;
					int moveToPosAmount= (int) (amtWidth -(1.67*amount.length()));
					writeTextWithPosition(""+moveToPosAmount,amount+"\r\n");
			    }
			 int pageNo = pagecount+1;
			 if(pageNo==totalPages)
			 {
				 int invoicesizoe = arrInvoiceNumbers.size()-pagecount*maxPendingPaymentSummaryCount;
				 int spAmount =maxPendingPaymentSummaryCount-invoicesizoe;
				  for(int i = 1 ;i <= spAmount ;i++)
				  {
					  writeText("\r\n");
				  }
				 writeCommands("(s0p10h9v-7B");
				 writeText(""+String.format(formatSeprator,seprator));
				 writeCommands("(s1p12h10v-7B");
				 String totalqty ="Total (AED) : "+deffAmt.format(totalPendingPaymentSummaryPrice);
				 int moveToPosTotalQTY = (int) (191 -(1.67*totalqty.length()));
				 writeTextWithPosition(""+moveToPosTotalQTY,totalqty+"\r\n");
				 writeText("\r\n");
				 writeText("\r\n");
				 writeText("\r\n");
			 }
			 else
			 {
				 writeText("\r\n \r\n \r\n \r\n \r\n");
			 }
			 String page =""+"Page # "+pageNo+"/"+totalPages;
			 int moveToPosTotalPage = (int) (192 -(1.67*page.length()));
			 writeTextWithPosition(""+moveToPosTotalPage,page+"\r\n");
		}

		private int maxCustomerStatementCount = 35;
		private float totalCustomerStatementPrice;
		public boolean isCustomerStatementPageFinished = false;
		public void printCustomerStatement( JourneyPlanDO mallsDetails, ArrayList<CustomerStatmentDO> vecDetails,String fromDate,String toDate)
		 {
			totalCustomerStatementPrice = 0;
			int totalPages = (vecDetails.size()+maxCustomerStatementCount-1)/maxCustomerStatementCount; 
			
			
			try 
			{
				if(mmOutputStream!=null)
				{
					for(int pages=0; pages < totalPages;pages++)
					{
						 printCustomerStatementInner(mallsDetails,vecDetails,fromDate,toDate,pages,totalPages);
					}
					closeBT();
				} 
				else{
					((Activity)context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "Device out reach", Toast.LENGTH_LONG).show();
						}
					});
				}
			} 
			catch (NullPointerException e) 
			{
			    e.printStackTrace();
			} catch (Exception e) 
			{
			    e.printStackTrace();
			}
		 }
		public void printCustomerStatementInner( JourneyPlanDO mallsDetails, ArrayList<CustomerStatmentDO> vecDetails,String fromDate,String toDate,int pagecount, int totalPages) throws UnsupportedEncodingException, IOException
		{
			 String header  		= "Customer Statement";
			 String formatHeader  		= "%1$-76.76s \r\n";
			 String formatDate  		= "%1$126.126s \r\n";
			 String seprator			= "____________________________________________________________________________________________________________________________________________________";
			 String formatSeprator 	= "%1$-76.76s \r\n";
			 int pos = (int)((140-1.67*header.length())/2);
			 String date = CalendarUtils.getCurrentSynchDateTime();
			 writeCommands("&l5D");
			 writeCommands("(s0p12.3h10v4s-7B");
			 writeText(""+String.format(formatDate,date)+"\r\n");
			 writeCommands("(s1p9h14v-7B");
			 writeTextWithPosition(""+pos,""+header+"\r\n");
			 writeCommands("(s1p12h10v-7B");
			 writeText( "Cust Name");
			 writeTextWithPosition("18", ": "+mallsDetails.siteName);
			 writeTextWithPosition("94", "Cust No");
//			 writeTextWithPosition("108", ": "+mallsDetails.site);
			 
			 if(TextUtils.isEmpty(mallsDetails.clientBranchCode))
				  writeTextWithPosition("108",": "+mallsDetails.site);
			  else
					writeTextWithPosition("108",": "+mallsDetails.site+" ["+mallsDetails.clientBranchCode+"]");
			 writeTextWithPosition("146","Tel No");
			 String Mobile = "N/A";
			 if(mallsDetails.mobileNo1!=null && !mallsDetails.mobileNo1.equalsIgnoreCase(""))
				 Mobile = mallsDetails.mobileNo1;
			 writeTextWithPosition("158",": "+Mobile+"\r \n");
			 
			 if(!TextUtils.isEmpty(mallsDetails.clientBranchCode))
				 writeTextWithPosition("20", ""+mallsDetails.customerSiteName);
			 writeTextWithPosition("94", "S. Org");
			 writeTextWithPosition("108",": "+preference.getStringFromPreference(Preference.USER_ID, ""));
			 writeTextWithPosition("146","Van No");
			 String userVehical = "";
			 if(userVehicleNumber!=null && !userVehicleNumber.equalsIgnoreCase(""))
			   userVehical = userVehicleNumber;
			 writeTextWithPosition("158",": "+userVehical+"\n");
			 String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
				
			 String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
			
			 String slName = "";
			 if(userMobileNumber!=null && !userMobileNumber.equalsIgnoreCase(""))
				 slName =preference.getStringFromPreference(Preference.USER_NAME, "")+" - "+userMobileNumber;
			 else
				 slName =preference.getStringFromPreference(Preference.USER_NAME, "");
			 writeText( "S. Name");
			 writeTextWithPosition("18", ": "+slName);
			 
			 writeTextWithPosition( "94","Period");
			 writeTextWithPosition("108", ": "+stratDate+" to "+endDate +"\n");
			 writeText("\r\n");
			 writeCommands("(s1p12h10v1B");
			 writeText("S1#".toUpperCase());	 
			 writeTextWithPosition("12", "Trx Type".toUpperCase()); 
			 writeTextWithPosition("40", "Trx No".toUpperCase()); 
			 writeTextWithPosition("95", "Trx Dt".toUpperCase()); 
			 writeTextWithPosition("162", "Amount (AED)".toUpperCase());
			 writeCommands("(s0p10h9v-7B");
			 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
			 writeCommands("(s1p12h10v-7B");
			
			 for(int  i = pagecount*maxCustomerStatementCount, count = 0 ; i < vecDetails.size() && count < maxCustomerStatementCount  ; i++, count++)
			 {
				 CustomerStatmentDO customerStatmentDO = vecDetails.get(i);
				 float amount = StringUtils.getFloat(customerStatmentDO.amount);
				 totalCustomerStatementPrice =totalCustomerStatementPrice +amount;
				 String invoiceDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, customerStatmentDO.trxDate);
				 int srNUmber = i+1;
				 writeText(""+srNUmber);	 
				 writeTextWithPosition("12", ""+customerStatmentDO.invoiceType); 
				 writeTextWithPosition("40", ""+customerStatmentDO.trxNumber);
				 writeTextWithPosition("95", ""+invoiceDate);
				 
				 int amtWidth = 191;
					if(amount < 0)
						amtWidth = 191;
					else
						amtWidth = 190;
				 String strAmount = ""+ amount;
				 int moveToPosAmount = (int) (amtWidth -(1.67*strAmount.length()));
				 writeTextWithPosition(""+moveToPosAmount,amount+"\r\n");
			 }
			 int pageNo = pagecount+1;
			 if(pageNo==totalPages)
			 {
				 int invoicesizoe = vecDetails.size()-pagecount*maxCustomerStatementCount;
				 int spAmount =maxCustomerStatementCount-invoicesizoe;
				  for(int i = 1 ;i <= spAmount ;i++)
				  {
					  writeText("\r\n");
				  }
				 writeCommands("(s0p10h9v-7B");
				 writeText(""+String.format(formatSeprator,seprator));
				 writeCommands("(s1p12h10v-7B");
				 String totalqty ="Total As of Today (AED) : "+deffAmt.format(totalCustomerStatementPrice);
				 int moveToPosTotalQTY = (int) (192 -(1.67*totalqty.length()));
				 writeTextWithPosition(""+moveToPosTotalQTY,totalqty+"\r\n");
				 writeText("\r\n \r\n");
			 }
			 else
			 {
				 writeText("\r\n \r\n \r\n \r\n");
			 }
			 String page =""+"Page # "+pageNo+"/"+totalPages;
			 int moveToPosTotalPage = (int) (192 -(1.67*page.length()));
			 writeTextWithPosition(""+moveToPosTotalPage,page+"\r\n");
			 writeText("\r\n");
		}*/
	public void writeTextWithPosition(String moveToPosition, String strText) throws UnsupportedEncodingException, IOException
	{
		mmOutputStream.write(27);
		String strCommands = "&a"+moveToPosition+"C";
		mmOutputStream.write(strCommands.getBytes("ASCII"));
		mmOutputStream.write(strText.getBytes("ASCII"));
	}
	public void writeCommands(String strCommands) throws UnsupportedEncodingException, IOException
	{
		mmOutputStream.write(27);
		mmOutputStream.write(strCommands.getBytes("ASCII"));
	}
	public void writeText(String strText) throws UnsupportedEncodingException, IOException
	{
		mmOutputStream.write(strText.getBytes("ASCII"));
	}
	public void writehorinzontaldivider() throws UnsupportedEncodingException, IOException
	{
		 String formatSeprator 	= "%1$-136.136s \r\n";
		 String seprator			= "  ____________________________________________________________________________________________________________________________________________________";
		 writeCommands("(s1p10h9v-7B");
		 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
		 writeCommands("(s1p12h10v4s0B");
	}
	public void writehorinzontaldividerLandscape() throws UnsupportedEncodingException, IOException
	{
		 String formatSeprator 	= "%1$-169.169s \r\n";
		 String seprator			= "  ____________________________________________________________________________________________________________________________________________________________________________________________";
		 writeCommands("(s1p10h9v-7B");
		 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
		 writeCommands("(s1p12h10v4s0B");
	}
	public void writehorinzontaldividerLandscapeHeader() throws UnsupportedEncodingException, IOException
	{
		 String formatSeprator 	= "%1$-169.169s \r\n";
		 String seprator			= "____________________________________________________________________________________________________________________________________________________________________________________________";
		 writeCommands("(s1p10h9v-7B");
		 writeTextWithPosition(""+0,""+String.format(formatSeprator,seprator));
		 writeCommands("(s1p12h10v4s0B");
	}
	public void writeVerticalDivider(String moveToPosition) throws UnsupportedEncodingException, IOException
	{
		mmOutputStream.write(27);
		String strCommands = "&a"+moveToPosition+"C";
		mmOutputStream.write(strCommands.getBytes("ASCII"));
		writeCommands("(s1p9h14v-7B");
		mmOutputStream.write("|".getBytes("ASCII"));
		writeCommands("(s1p12h10v4s0B");
	}
	public void writeHeaderText(String strOne, String strTwo,String strThree,String strFour)
	{
		
		try 
		{
			writeVerticalDivider(""+0);
			
			writeTextWithPosition("5", ""+strOne);
			writeTextWithPosition("60",":" );
			int strTwoPos =  (int) (87 -(1.67*strTwo.length()));
			writeTextWithPosition(""+strTwoPos,strTwo );
			
			writeVerticalDivider(""+97);
			 
			writeTextWithPosition("102", ""+strThree);
			writeTextWithPosition("157",":" );
			int strFourPos =  (int) (184 -(1.67*strFour.length()));
			writeTextWithPosition(""+strFourPos,strFour );
			
			writeVerticalDivider(""+190);
			writeText("\n");
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void writeFooterText(String strOne, String strTwo,String strThree,String strFour,String strFive, String strSix,String strSeven,String strEight)
	{
		
		try 
		{
			writeVerticalDivider(""+0);
			
			writeTextWithPosition("2", ""+strOne);
			writeTextWithPosition("35",":" );
			int strTwoPos =  (int) (60 -(1.67*strTwo.length()));
			writeTextWithPosition(""+strTwoPos,strTwo );
			writeVerticalDivider(""+63);
			writeTextWithPosition("65", ""+strThree);
			writeTextWithPosition("105",":" );
			int strFourPos =  (int) (115 -(1.67*strFour.length()));
			writeTextWithPosition(""+strFourPos,strFour );
			
			writeVerticalDivider(""+117);
			
			writeTextWithPosition("119", ""+strFive);
			writeTextWithPosition("152",":" );
			int strSixPos =  (int) (180 -(1.67*strSix.length()));
			writeTextWithPosition(""+strSixPos,strSix );
			writeVerticalDivider(""+184);
			writeTextWithPosition("186", ""+strSeven);
			writeTextWithPosition("225",":" );
			int strEightPos =  (int) (237 -(1.67*strEight.length()));
			writeTextWithPosition(""+strEightPos,strEight );
			writeVerticalDivider(""+237);
			writeText("\n");
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void sort(Vector<String> vec)
	{
		Collections.sort(vec, new Comparator<String>() {
		    @Override
		   public int compare(String s1, String s2) 
		    {
		    	return s1.compareToIgnoreCase(s2);
		    }
		});
	}
	 public String getAdress(JourneyPlanDO mallsDetails)
		{
			String addString = "";
			if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
				addString +=mallsDetails.addresss1+" ";
			
			if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
				addString +=mallsDetails.addresss2+" ";
			
			if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
				addString +=mallsDetails.addresss3+" ";
			
			if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
				addString +=mallsDetails.addresss4+" ";
			
			if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
				addString +=mallsDetails.poNumber+" ";
			
			if(mallsDetails.city != null && mallsDetails.city.length() > 0)
				addString +=mallsDetails.city;
			
			
			return addString;
			
		}
	 
	void enableBlueTooth()
	{

		 mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


	     if(mBluetoothAdapter == null) 
	     {
//	         myLabel.setText("No bluetooth adapter available");
	         Toast.makeText(context, "No bluetooth  available", Toast.LENGTH_LONG).show();
	     }
	     else if(mBluetoothAdapter!= null && !mBluetoothAdapter.isEnabled()) 
	     {
//	         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	         Toast.makeText(context, "Enable Bluetooth ", Toast.LENGTH_LONG).show();
	       
	     }

	}
	
	
	
	public void Pageout()
	{
		   try {
			closeBT();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	public void setMobileNumber(String userMobileNumber) {
		this.userMobileNumber=userMobileNumber;
	}
	public void setVehicleNumber(String userVehicleNumber) {
		this.userVehicleNumber=userVehicleNumber;
	}
}
