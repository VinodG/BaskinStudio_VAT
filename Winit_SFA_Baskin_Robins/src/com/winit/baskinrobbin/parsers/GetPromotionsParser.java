package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.PromotionalItemsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.PromotionAssignmentsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionDetailsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionOfferItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionOrderItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionsDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetPromotionsParser extends BaseHandler
{
	private Vector<PromotionsDO> vecPromotionsDO ;
	private PromotionsDO promotionsDO;
	
	private Vector<PromotionOrderItemsDO> vecPromotionOrderItemsDO;
	private PromotionOrderItemsDO promotionOrderItemsDO;
	private Vector<PromotionOfferItemsDO> vecPromotionOfferItemsDO;
	private PromotionOfferItemsDO promotionOfferItemsDO;
	private Vector<PromotionAssignmentsDO> vecPromotionAssignmentsDO;
	private PromotionAssignmentsDO promotionAssignmentsDO;
	private PromotionDetailsDO promotionDetailsDO;
	private Vector<PromotionDetailsDO> vectorPromoDetail;
	private final int Promotions= 2, PromotionDetail= 3, PromotionOrderItems= 4, PromotionOfferItems= 5, PromotionAssignments= 7;
	private int SELECTED_TYPE;
	private String empNo;
	private SynLogDO synLogDO = new SynLogDO();
	
	public GetPromotionsParser(Context context) 
	{
		super(context);
	}
	
	public GetPromotionsParser(Context context, String empNo) 
	{
		super(context);
		this.empNo = empNo;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("Promotions"))
			vecPromotionsDO = new Vector<PromotionsDO>();
		
		else if(localName.equalsIgnoreCase("PromotionDco"))
		{
			SELECTED_TYPE = Promotions;
			promotionsDO = new PromotionsDO();
		}
		
		else if(localName.equalsIgnoreCase("PromotionOrderItems"))
			vecPromotionOrderItemsDO = new Vector<PromotionOrderItemsDO>();
		
		else if(localName.equalsIgnoreCase("PromotionOrderItemDco"))
		{
			SELECTED_TYPE = PromotionOrderItems;
			promotionOrderItemsDO = new PromotionOrderItemsDO();
		}
		
		else if(localName.equalsIgnoreCase("PromotionOfferItems"))
			vecPromotionOfferItemsDO = new Vector<PromotionOfferItemsDO>();
		
		else if(localName.equalsIgnoreCase("PromotionOfferItemDco"))
		{
			SELECTED_TYPE = PromotionOfferItems;
			promotionOfferItemsDO = new PromotionOfferItemsDO();
		}
		
		else if(localName.equalsIgnoreCase("PromotionAssignments"))
			vecPromotionAssignmentsDO = new Vector<PromotionAssignmentsDO>();
		
		else if(localName.equalsIgnoreCase("PromotionAssignmentDco"))
		{
			SELECTED_TYPE = PromotionAssignments;
			promotionAssignmentsDO = new PromotionAssignmentsDO();
		}
		
		else if(localName.equalsIgnoreCase("PromotionDetails"))
			vectorPromoDetail = new Vector<PromotionDetailsDO>();
		
		else if(localName.equalsIgnoreCase("PromotionDetailDco"))
		{
			SELECTED_TYPE = PromotionDetail;
			promotionDetailsDO = new PromotionDetailsDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ServerTime"))
			synLogDO.TimeStamp =  currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedDate")  && SELECTED_TYPE <= 0)
			synLogDO.UPMJ =  currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedTime")  && SELECTED_TYPE <= 0)
			synLogDO.UPMT =  currentValue.toString();
		
		else if(localName.equalsIgnoreCase("Status")  && SELECTED_TYPE <= 0)
			synLogDO.action =  currentValue.toString();
		
		else if(localName.equalsIgnoreCase("GetAllPromotionsResult"))
		{
			if(insertPromotionalData(vecPromotionsDO, vecPromotionOrderItemsDO, vecPromotionOfferItemsDO, vecPromotionAssignmentsDO , vectorPromoDetail))
			{
				preference.commitPreference();
				synLogDO.entity = ServiceURLs.GetAllPromotions;
				new SynLogDA().insertSynchLog(synLogDO);
			}
		}
		
		switch (SELECTED_TYPE)
		{
			case Promotions:
				
				if(localName.equalsIgnoreCase("PromotionId"))
					promotionsDO.PromotionId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionTypeId"))
					promotionsDO.PromotionTypeId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Description"))
					promotionsDO.Description = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("StartDate"))
					promotionsDO.StartDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("EndDate"))
					promotionsDO.EndDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("IsActive"))
					promotionsDO.IsActive = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VisibilityCheckRequired"))
					promotionsDO.VisibilityCheckRequired = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("MinInvoice"))
					promotionsDO.MinInvoice = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Discount"))
					promotionsDO.Discount = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionDco"))
					vecPromotionsDO.add(promotionsDO);
				
				break;
				
			case PromotionOrderItems:
				
				if(localName.equalsIgnoreCase("PromotionOrderItemId"))
					promotionOrderItemsDO.PromotionOrderItemId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionDetailId"))
					promotionOrderItemsDO.PromotionId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemCode"))
					promotionOrderItemsDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("OrderType"))
					promotionOrderItemsDO.OrderType = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Quantity"))
					promotionOrderItemsDO.Quantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					promotionOrderItemsDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Value"))
					promotionOrderItemsDO.Value = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionOrderItemDco"))
					vecPromotionOrderItemsDO.add(promotionOrderItemsDO);
				
				break;
				
		case PromotionOfferItems:
				
				if(localName.equalsIgnoreCase("PromotionOfferItemId"))
					promotionOfferItemsDO.PromotionOfferItemId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionDetailId"))
					promotionOfferItemsDO.PromotionId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemCode"))
					promotionOfferItemsDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("OfferType"))
					promotionOfferItemsDO.OfferType = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Quantity"))
					promotionOfferItemsDO.Quantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					promotionOfferItemsDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("MaxQuantity"))
					promotionOfferItemsDO.MaxQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Discount"))
					promotionOfferItemsDO.Discount = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PromotionOfferItemDco"))
					vecPromotionOfferItemsDO.add(promotionOfferItemsDO);
				
				break;
				
		case PromotionAssignments:
			
			if(localName.equalsIgnoreCase("PromotionAssignmentId"))
				promotionAssignmentsDO.PromotionAssignmentId = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("PromotionId"))
				promotionAssignmentsDO.PromotionId = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("Code"))
				promotionAssignmentsDO.Code = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("AssignmentType"))
				promotionAssignmentsDO.AssignmentType = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("PromotionAssignmentDco"))
				vecPromotionAssignmentsDO.add(promotionAssignmentsDO);
			
			break;
			
		case PromotionDetail:
			
			if(localName.equalsIgnoreCase("PromotionDetailId"))
				promotionDetailsDO.PromotionDetailId = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("PromotionId"))
				promotionDetailsDO.PromotionId = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("Code"))
				promotionDetailsDO.Code = currentValue.toString();
			
			else if(localName.equalsIgnoreCase("PromotionDetailDco"))
				vectorPromoDetail.add(promotionDetailsDO);
			
			break;	
				
			default:
				break;
		}
	}
	
	private boolean insertPromotionalData(
			Vector<PromotionsDO> vecPromotionsDO,
			Vector<PromotionOrderItemsDO> vecPromotionOrderItemsDO,
			Vector<PromotionOfferItemsDO> vecPromotionOfferItemsDO,
			Vector<PromotionAssignmentsDO> vecPromotionAssignmentsDO,
			Vector<PromotionDetailsDO>vectorPromoDetail)
	{
		return new PromotionalItemsDA().insertPromotionData(vecPromotionsDO,
				 vecPromotionOrderItemsDO, vecPromotionOfferItemsDO, vecPromotionAssignmentsDO, vectorPromoDetail, empNo);
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
