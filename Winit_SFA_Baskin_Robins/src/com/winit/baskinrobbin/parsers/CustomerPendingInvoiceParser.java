package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerOrdersDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class CustomerPendingInvoiceParser extends BaseHandler
{

	private CustomerOrdersDO customerPendingInvoices;
	public Vector<CustomerOrdersDO> vecCustomerPendingInvoices;
	private String customerCode="";
	private CustomerDetailsDA customerDetailsDA;
	private int completedCount = 0;
	private boolean isForFirst = true;
//	private boolean isToupdate;
	public CustomerPendingInvoiceParser(Context context/*, boolean isToupdate*/) 
	{
		super(context);
//		this.isToupdate = isToupdate;
		// TODO Auto-generated constructor stub
	}
	public CustomerPendingInvoiceParser(Context context, boolean isToupdate) 
	{
		super(context);
//		this.isToupdate = isToupdate;
		// TODO Auto-generated constructor stub
	}
	public CustomerPendingInvoiceParser(Context context, String customerCode) 
	{
		super(context);
		this.customerCode = customerCode;
//		this.isToupdate = isToupdate;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement = true;
		currentValue = new StringBuilder();
				
		if(localName.equalsIgnoreCase("PendingSalesInvoices"))
		{
			vecCustomerPendingInvoices = new Vector<CustomerOrdersDO>();
			customerDetailsDA = new CustomerDetailsDA();
		}
		else if(localName.equalsIgnoreCase("PendingSalesInvoiceDco"))
		{
			customerPendingInvoices = new CustomerOrdersDO();
		}
	}

	/** Called when tag closing ( ex:- <event>AndroidPeople</event> 
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement = false;
		
		/** set value */ 
		if (localName.equals("CurrentTime"))
		{
			SynLogDO synLogDO = new SynLogDO();
			synLogDO.TimeStamp =  currentValue.toString();
			synLogDO.entity		= ServiceURLs.GET_PENDING_SALES_INVOICE;
			new SynLogDA().insertSynchLog(synLogDO);
//			preference.saveStringInPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		
		else if(localName.equalsIgnoreCase("CUSTOMER_TRX_ID"))
		{
			customerPendingInvoices.orderId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CUSTOMER_ID"))
		{
			customerPendingInvoices.customerId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SITE_NUMBER"))
		{
			customerPendingInvoices.siteNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("INVOICE_NUMBER"))
		{
			customerPendingInvoices.invoiceNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("INVOICE_DATE"))
		{
			customerPendingInvoices.invoiceDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("INVOICE_AMOUNT"))
		{
			customerPendingInvoices.invoiceAmount = currentValue.toString();
		}	
		else if(localName.equalsIgnoreCase("BALANCE_AMOUNT"))
		{
			customerPendingInvoices.balanceAmount = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SALESMANCODE"))
		{
			customerPendingInvoices.salesManCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TRANS_TYPE_NAME"))
		{
			customerPendingInvoices.transTypeName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IS_OVERDUE"))
		{
			customerPendingInvoices.IsOutStanding = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DOC_TYPE"))
		{
			customerPendingInvoices.Doc_Type = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DUE_DATE"))
		{
			customerPendingInvoices.Due_Date = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("REFERENCE_DOCUMENT"))
		{
			customerPendingInvoices.Reference_Document = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ERPReference"))
		{
			customerPendingInvoices.ebs_ref_no = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PendingSalesInvoiceDco"))
		{
			vecCustomerPendingInvoices.add(customerPendingInvoices);completedCount++;
			if(vecCustomerPendingInvoices.size()>=AppConstants.SYNC_COUNT){
			//	customerDetailsDA.insertAllCustomerPendingInvoices(vecCustomerPendingInvoices,customerCode,isForFirst);
				customerDetailsDA.insertAllPendingInvoices(vecCustomerPendingInvoices,customerCode,isForFirst);
				vecCustomerPendingInvoices.clear();
				isForFirst = false;
				LogUtils.errorLog("completedCount",""+completedCount);
			}
		}
		else if(localName.equalsIgnoreCase("PendingSalesInvoices"))
		{
			//if(customerDetailsDA.insertAllCustomerPendingInvoices(vecCustomerPendingInvoices,customerCode,isForFirst)){
			if(customerDetailsDA.insertAllPendingInvoices(vecCustomerPendingInvoices,customerCode,isForFirst)){
				isForFirst = false;
				preference.commitPreference();
			}
		}
	}

	/** Called to get tag characters ( ex:- <event>AndroidPeople</event> 
	 * -- to get event Character ) */
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
	}
}
