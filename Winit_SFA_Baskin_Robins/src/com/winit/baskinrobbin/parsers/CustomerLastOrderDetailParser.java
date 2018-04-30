package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSite_NewDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class CustomerLastOrderDetailParser extends BaseHandler
{
	private Vector<CustomerSite_NewDO> vecSalesManCustomerDetailDOs;
	private CustomerSite_NewDO customerSiteNewDO;
	private OrderDO orderDO;
	private ProductDO productDO;
	private boolean isOrderDetail =false, isOrderheader =false;
	private String empNo = "", salesmanCode = "";
	
	public CustomerLastOrderDetailParser(Context context,  String empNo, String salesmanCode) 
	{
		super(context);
		this.empNo 			= 	empNo;
		this.salesmanCode 	= 	salesmanCode;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement 	= true;
		currentValue 	= new StringBuilder();

		if (localName.equals("Customers"))
		{
			vecSalesManCustomerDetailDOs  = new Vector<CustomerSite_NewDO>();
		}
		else if (localName.equals("CustomerDco"))
		{
			customerSiteNewDO = new CustomerSite_NewDO();
		}		
		else if(localName.equalsIgnoreCase("objTrxHeader"))
		{
			customerSiteNewDO.vecOrderDO = new Vector<OrderDO>();
		}
		else if(localName.equalsIgnoreCase("TrxHeaderDco"))
		{
			isOrderDetail = false;
			orderDO = new OrderDO();
			isOrderheader = true;
		}
		else if(localName.equalsIgnoreCase("objTrxDetails"))
		{
			isOrderDetail =true;
			orderDO.vecProductDO = new Vector<ProductDO>();
			isOrderheader = false;
		}
		else if(localName.equalsIgnoreCase("TrxDetailDco"))
		{
			isOrderDetail = true;
			productDO = new ProductDO();
		}
	}

	/** Called when tag closing ( ex:- <event>AndroidPeople</event> 
	 * -- </name> )*/
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement = false;
		
		if(localName.equalsIgnoreCase("CustomerSiteId"))
		{
			customerSiteNewDO.CustomerSiteId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteName"))
		{
			customerSiteNewDO.SiteName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrderId")  && !isOrderDetail)
		{
			orderDO.OrderId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrderNumber"))
		{
			orderDO.OrderId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppId"))
		{
			orderDO.strUUID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EmpNo"))
		{
			orderDO.empNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Site_Number"))
		{
			orderDO.CustomerSiteId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Order_Date"))
		{
			orderDO.InvoiceDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Order_Type"))
		{
			orderDO.orderType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrderType"))
		{
			orderDO.orderSubType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentType"))
		{
			orderDO.PaymentType = currentValue.toString();
		}
		else if(isOrderheader && localName.equalsIgnoreCase("Status"))
		{
			orderDO.pushStatus = StringUtils.getInt(currentValue.toString());
		}
		else if(isOrderheader && localName.equalsIgnoreCase("TRXStatus"))
		{
			orderDO.TRXStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentCode"))
		{
			orderDO.PaymentCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DeliveryDate"))
		{
			orderDO.DeliveryDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("StampDate"))
		{
			orderDO.StampDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("StampImage"))
		{
			orderDO.StampImage = currentValue.toString();
		}
		
		/////////////////////////////////////////////////////////////////
		else if(localName.equalsIgnoreCase("TRANSACTION_TYPE_NAME"))
		{
			orderDO.TRANSACTION_TYPE_VALUE = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TRANSACTION_TYPE_KEY"))
		{
			orderDO.TRANSACTION_TYPE_KEY = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Batch_Source_Name"))
		{
			orderDO.Batch_Source_Name = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Cust_Trx_Type_Name"))
		{
			orderDO.Trx_Type_Name = currentValue.toString();
		}
		/////////////////////////////////////////////////////////////////
		else if(localName.equalsIgnoreCase("LineNo"))
		{
			productDO.LineNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrderNumber") && isOrderDetail)
		{
			productDO.OrderNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode"))
		{
			productDO.SKU = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemDescription"))
		{
			productDO.Description = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Cases"))
		{
			productDO.preCases = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Units"))
		{
			productDO.preUnits = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TotalUnits"))
		{
			productDO.totalCases = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ItemType"))
		{
			productDO.ItemType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BasePrice"))
		{
			productDO.itemPrice 		= StringUtils.getFloat(currentValue.toString());
			productDO.unitSellingPrice  = productDO.itemPrice;
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			productDO.UOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("QuantityBU"))
		{
			productDO.totalCases = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PriceUsedLevel1"))
		{
			productDO.totalPrice = StringUtils.getDouble(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PriceUsedLevel2"))
		{
			productDO.invoiceAmount = StringUtils.getDouble(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalDiscountAmount") && isOrderDetail)
		{
			productDO.DiscountAmt = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ExpiryDate"))
		{
			productDO.strExpiryDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TrxReasonCode"))
		{
			productDO.reason = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Remarks"))
		{
			productDO.remarks = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LotNo"))
		{
			productDO.LotNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TrxHeaderDco"))
		{
			customerSiteNewDO.vecOrderDO.add(orderDO);
		}
		else if(localName.equalsIgnoreCase("TrxDetailDco"))
		{
			isOrderDetail = false;
			orderDO.vecProductDO.add(productDO);
		}
		else if(localName.equalsIgnoreCase("objTrxDetails"))
		{
			isOrderDetail=false;
		}
		/////////////////////////////////////////////////////////
		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			vecSalesManCustomerDetailDOs.add(customerSiteNewDO);
		}
		else if(localName.equalsIgnoreCase("Customers"))
		{
			if(vecSalesManCustomerDetailDOs != null && vecSalesManCustomerDetailDOs.size() > 0)
			{
				new OrderDA().insertAdvanceOrderDetails(vecSalesManCustomerDetailDOs);
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
