package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSite_NewDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetTrxHeaderForApp  extends BaseHandler
{
	private CustomerSite_NewDO customerDO;
	private OrderDO orderDO;
	private ProductDO productDO;
	private final int TrxHeader= 4, TrxHeaderDetails= 5;
	private int SELECTED_TYPE;
	private String empNo;
	private SynLogDO syncLogDO = new SynLogDO();
	
	public GetTrxHeaderForApp(Context context, String strEmpNo) 
	{
		super(context);
		this.empNo = strEmpNo;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		
		if(localName.equalsIgnoreCase("TrxHeaders"))
		{
			customerDO = new CustomerSite_NewDO();
		}
		else if(localName.equalsIgnoreCase("TrxHeaderDco"))
		{
			SELECTED_TYPE = TrxHeader;
			orderDO = new OrderDO();
			customerDO.vecOrderDO.add(orderDO);
		}
		else if(localName.equalsIgnoreCase("TrxDetailDco"))
		{
			productDO = new ProductDO();
			SELECTED_TYPE = TrxHeaderDetails;
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ServerTime"))
			preference.saveStringInPreference(Preference.GetSurveyMasters , currentValue.toString());
		
		else if(localName.equalsIgnoreCase("ModifiedDate")  && SELECTED_TYPE <= 0)
			syncLogDO.UPMJ = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedTime")  && SELECTED_TYPE <= 0)
			syncLogDO.UPMT = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("GetTrxHeaderForAppResult"))
		{
			if(insertTrxHeaderForAppData(customerDO))
			{
				syncLogDO.entity = ServiceURLs.GetTrxHeaderForApp;
				new SynLogDA().insertSynchLog(syncLogDO);
			}
		}
		else if(orderDO == null && localName.equalsIgnoreCase("Status"))
		{
			syncLogDO.action =  currentValue.toString();
		}
		else
			
		switch (SELECTED_TYPE)
		{
			case TrxHeader:
				
				if(localName.equalsIgnoreCase("OrderNumber"))
				{
					orderDO.OrderId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("AppId"))
				{
					orderDO.strUUID = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("JourneyCode"))
				{
					orderDO.JourneyCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("VisitCode"))
				{
					orderDO.VisitCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("EmpNo"))
				{
					orderDO.empNo = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Site_Number"))
				{
					orderDO.CustomerSiteId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Site_Name"))
				{
					orderDO.strCustomerName = currentValue.toString();
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
				else if(localName.equalsIgnoreCase("CurrencyCode"))
				{
					orderDO.CurrencyCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("PaymentType"))
				{
					orderDO.PaymentType = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TrxReasonCode"))
				{
					orderDO.TrxReasonCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TotalAmount"))
				{
					orderDO.TotalAmount =StringUtils.getDouble(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("TotalDiscountAmount"))
				{
					orderDO.Discount =StringUtils.getDouble(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("TotalTAXAmount"))
				{
					orderDO.TotalTaxAmt = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					orderDO.pushStatus = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("VisitID"))
				{
					orderDO.VisitCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("FreeNote"))
				{
					orderDO.freeDeliveryResion = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TRXStatus"))
				{
					orderDO.TRXStatus = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("PaymentCode"))
				{
					orderDO.PaymentCode = currentValue.toString();
				}
				
				else if(localName.equalsIgnoreCase("TRANSACTION_TYPE_KEY"))
				{
					orderDO.TRANSACTION_TYPE_KEY = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("TRANSACTION_TYPE_NAME"))
				{
					orderDO.TRANSACTION_TYPE_VALUE = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Batch_Source_Name"))
				{
					orderDO.Batch_Source_Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Cust_Trx_Type_Name"))
				{
					orderDO.Trx_Type_Name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("LPOCode"))
				{
					orderDO.LPOCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("DeliveryDate"))
				{
					orderDO.DeliveryDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("StampDate"))
				{
					orderDO.StampDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("LPOStatus"))
				{
					orderDO.LPOStatus = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("RoundOffValue"))
				{
					orderDO.strRoundOffVal = currentValue.toString();
				}
				//===============Added For VAT===================
				else if(localName.equalsIgnoreCase("VatAmount"))
				{orderDO.VatAmount = StringUtils.getDouble(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalAmountWithVat"))
				{orderDO.TotalAmountWithVat = StringUtils.getDouble(currentValue.toString());}
				else if(localName.equalsIgnoreCase("ProrataTaxAmount"))
				{orderDO.ProrataTaxAmount = StringUtils.getDouble(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TotalTax"))
				{orderDO.TotalTax = StringUtils.getDouble(currentValue.toString());}
				else if(localName.equalsIgnoreCase("TrxHeaderDco"))
				{
					customerDO.vecOrderDO.add(orderDO);
				}
				break;
				
		case TrxHeaderDetails:
			if(localName.equalsIgnoreCase("LineNo"))
			{
				productDO.LineNo = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("OrderNumber"))
			{
				productDO.OrderNo = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ItemCode"))
			{
				productDO.SKU  = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ItemType"))
			{
				productDO.ItemType = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("BasePrice"))
			{
				productDO.itemPrice = StringUtils.getFloat(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("UOM"))
			{
				productDO.UOM = currentValue.toString();
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
				productDO.totalCases = StringUtils.getFloat(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("QuantityBU"))
			{
				productDO.quantityBU = StringUtils.getInt(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("PriceDefaultLevel1"))
			{
				productDO.unitSellingPrice = StringUtils.getFloat(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("PriceUsedLevel1"))
			{
				productDO.totalPrice = StringUtils.getDouble(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("PriceUsedLevel2"))
			{
				productDO.invoiceAmount = StringUtils.getDouble(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("TaxPercentage"))
			{
				productDO.TaxPercentage = StringUtils.getFloat(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("TotalDiscountPercentage"))
			{
				productDO.Discount = StringUtils.getFloat(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("TotalDiscountAmount"))
			{
				productDO.discountAmount = StringUtils.getDouble(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("UserDiscountAmount"))
			{
			}
			else if(localName.equalsIgnoreCase("ItemDescription"))
			{
				productDO.Description = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("ExpiryDate"))
			{
				productDO.strExpiryDate = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("RelatedLineID"))
			{
				productDO.RelatedLineId =  currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("TrxReasonCode"))
			{
				productDO.reason = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("PromotionLineNo"))
			{
				productDO.PromoLineNo = StringUtils.getInt(currentValue.toString());
			}
			else if(localName.equalsIgnoreCase("LotNumber"))
			{
				productDO.LotNumber = currentValue.toString();
			}
//===============Added For VAT===================
			else if(localName.equalsIgnoreCase("LineTaxAmount"))
			{productDO.LineTaxAmount = StringUtils.getDouble(currentValue.toString());}
			else if(localName.equalsIgnoreCase("ProrataTaxAmount"))
			{productDO.ProrataTaxAmount = StringUtils.getDouble(currentValue.toString());}
			else if(localName.equalsIgnoreCase("TotalTax"))
			{productDO.TotalTax = StringUtils.getDouble(currentValue.toString());}

			else if(localName.equalsIgnoreCase("QuantityInStock"))
			{productDO.QuantityInStock = StringUtils.getFloat(currentValue.toString());}
			else if(localName.equalsIgnoreCase("RefTrxCode"))
			{productDO.RefTrxCode = (currentValue.toString());}
			else if(localName.equalsIgnoreCase("TrxDetailDco"))
			{
				orderDO.vecProductDO.add(productDO);
			}
			
			break;
				
			default:
				break;
		}
	}
	
	private boolean insertTrxHeaderForAppData(CustomerSite_NewDO customer) 
	{
		Vector<CustomerSite_NewDO> vecCustomer = new Vector<CustomerSite_NewDO>();
		vecCustomer.add(customer);
		new OrderDA().insertAdvanceOrderDetails(vecCustomer);
		// insertIntoDataBase
		return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
