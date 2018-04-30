package com.winit.baskinrobbin.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.SalesmanOrderPreview;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.NonSellableItemDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.dataobject.VanStockDO;
import com.winit.baskinrobbin.salesman.dataobject.WareHouseStockDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllMovements extends BaseHandler
{
	private final int TYPE_MOVEMENT_HEADER = 1, TYPE_MOVEMENT_DETAILS = 2, TYPE_VAN_LOAD = 3,
					  WAREHOUSE_QTY = 4, NON_SALEABLE_QTY = 6;
	private int PARSE_TYPE = -1;
	private HashMap<String, LoadRequestDO> hashMap = new HashMap<String, LoadRequestDO>();
	private LoadRequestDO loadRequestDO;
	private LoadRequestDetailDO loadRequestDetailDO;
	private String movementCode2 = "", movementCode1 = "";
	private Vector<VanStockDO> vecVanStockDOs;
	private VanStockDO vanStockDO;
	private WareHouseStockDO wareHouseStockDO;
	private Vector<WareHouseStockDO> vecWStockDO;
	private NonSellableItemDO nonSellableItemDO;
	private Vector<NonSellableItemDO> vecNonSellableItem;
	private SynLogDO synLogDO = new SynLogDO();
	private boolean isInserted;
	public GetAllMovements(Context context, String empNo) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("VanStocks"))
		{
			PARSE_TYPE = TYPE_VAN_LOAD;
			vecVanStockDOs = new Vector<VanStockDO>();
		}
		
		else if(localName.equalsIgnoreCase("VanStockDco"))
			vanStockDO = new VanStockDO();
		
		else if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			PARSE_TYPE = TYPE_MOVEMENT_HEADER;
			hashMap = new HashMap<String, LoadRequestDO>();
		}
		
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
			loadRequestDO = new LoadRequestDO();
		
		else if(localName.equalsIgnoreCase("MovementDetails"))
			PARSE_TYPE = TYPE_MOVEMENT_DETAILS;
		
		else if(localName.equalsIgnoreCase("MovementDetailDco"))
			loadRequestDetailDO = new LoadRequestDetailDO();
		
		else if(localName.equalsIgnoreCase("WareHouseStocks"))
		{
			vecWStockDO = new Vector<WareHouseStockDO>();
			PARSE_TYPE  = WAREHOUSE_QTY;
		}
		
		else if(localName.equalsIgnoreCase("WareHouseStockDco"))
			wareHouseStockDO = new WareHouseStockDO();
		
		else if(localName.equalsIgnoreCase("NonSellableItems"))
		{
			vecNonSellableItem  = new Vector<NonSellableItemDO>();
			PARSE_TYPE  		= NON_SALEABLE_QTY;
		}
		
		else if(localName.equalsIgnoreCase("NonSellableItemDco"))
			nonSellableItemDO = new NonSellableItemDO();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		/*if(localName.equalsIgnoreCase("ServerTime"))
			preference.saveStringInPreference(ServiceURLs.GetAllMovements_Sync+empNo+Preference.LAST_SYNC_TIME, currentValue.toString());
		else */
		if(localName.equalsIgnoreCase("Status"))
			synLogDO.action = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ServerTime"))
			synLogDO.TimeStamp = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedDate"))
			synLogDO.UPMJ = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedTime"))
			synLogDO.UPMT = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("GetAppActiveStatusResult"))
		{
			insertMovementDetailsDetail();
		}
		switch (PARSE_TYPE) 
		{
			case TYPE_VAN_LOAD:
				
				if(localName.equalsIgnoreCase("ItemCode"))
					vanStockDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("OrgCode"))
					vanStockDO.OrgCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					vanStockDO.UserCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("QuantityEach"))
					vanStockDO.QuantityEach = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("AvailableQuantity"))
					vanStockDO.AvailableQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("SellableQuantity"))
					vanStockDO.SellableQuantity = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("NonSellableQuantity"))
					vanStockDO.NonSellableQuantity = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("ReturnedQuantity"))
					vanStockDO.ReturnedQuantity = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("LPOQuantity"))
					vanStockDO.LPOQty = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("TotalQuantity"))
					vanStockDO.TotalQuantity = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("BatchNumber"))
					vanStockDO.BatchNumber = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ExpiryDate"))
					vanStockDO.ExpiryDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					vanStockDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VanStockDco"))
					vecVanStockDOs.add(vanStockDO);
				
				break;
				
			case TYPE_MOVEMENT_HEADER:
				
				if(localName.equalsIgnoreCase("MovementCode"))
				{
					movementCode1 = currentValue.toString();
					loadRequestDO.MovementCode = currentValue.toString();
					
					/*********Need To Check*********/
					loadRequestDO.PreMovementCode=	""+loadRequestDO.MovementCode;
					loadRequestDO.Status	     = "1";
					
					loadRequestDO.ApproveByCode  =	"0";
					loadRequestDO.ISFromPC		 =	"0";
					loadRequestDO.OperatorCode	 =	"0";
					loadRequestDO.IsDummyCount	 =	"0";
					/**************************************/
				}
				
				else if(localName.equalsIgnoreCase("OrgCode"))
					loadRequestDO.OrgCode		=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					loadRequestDO.UserCode		=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("WHKeeperCode"))
					loadRequestDO.WHKeeperCode	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("CurrencyCode"))
					loadRequestDO.CurrencyCode	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("JourneyCode"))
					loadRequestDO.JourneyCode	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("MovementDate"))
				{
					loadRequestDO.MovementDate	=	currentValue.toString();
					
					/*********Need To Check*********/
					loadRequestDO.ApprovedDate	=	loadRequestDO.MovementDate;
					loadRequestDO.JDETRXNumber	=	loadRequestDO.MovementCode;
					loadRequestDO.ISStampDate	=	loadRequestDO.MovementDate;
					loadRequestDO.PushedOn		=	loadRequestDO.MovementDate;
					loadRequestDO.ModifiedOn	=	loadRequestDO.MovementDate;
					/**************************************/
				}
				
				else if(localName.equalsIgnoreCase("ModifiedOn"))
					loadRequestDO.ModifiedOn	=	currentValue.toString();
				else if(localName.equalsIgnoreCase("MovementNote"))
					loadRequestDO.MovementNote	=	currentValue.toString();
				else if(localName.equalsIgnoreCase("MovementType"))
					loadRequestDO.MovementType	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("SourceVehicleCode"))
					loadRequestDO.SourceVehicleCode	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("DestinationVehicleCode"))
					loadRequestDO.DestinationVehicleCode	=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VisitID"))
					loadRequestDO.VisitID			=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("CreatedOn"))
					loadRequestDO.CreatedOn			=	currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Amount"))
					loadRequestDO.Amount			=	StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("_MovementStatus"))
				{
				}
				else if(localName.equalsIgnoreCase("IsVerified"))
				{
					loadRequestDO.IsVarified=currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("MovementStatus"))
				{
					loadRequestDO.MovementStatus=currentValue.toString();
					/*if(currentValue.toString().equalsIgnoreCase("100"))
						loadRequestDO.MovementStatus =	"Collected";
					else if(currentValue.toString().equalsIgnoreCase("101"))
						loadRequestDO.MovementStatus =	"Approved";
					else if(currentValue.toString().equalsIgnoreCase("-13"))
						loadRequestDO.MovementStatus =	"Rejected";
					else if(currentValue.toString().equalsIgnoreCase("99"))
						loadRequestDO.MovementStatus =	"Pending from EBS";
					else
						loadRequestDO.MovementStatus =	"Pending";*/
				}
				else if(localName.equalsIgnoreCase("MovementHeaderDco"))
					hashMap.put(movementCode1, loadRequestDO);
				
				break;
				
			case TYPE_MOVEMENT_DETAILS:
				
				if(localName.equalsIgnoreCase("LineNo"))
					loadRequestDetailDO.LineNo = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("MovementCode"))
				{
					movementCode2 = currentValue.toString();
					/*********Need To Check*********/
					//loadRequestDetailDO.MovementStatus	=	"Approved";
					loadRequestDetailDO.Status			=	"Approved";
					/**************************************/
					loadRequestDetailDO.MovementCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ItemCode"))
					loadRequestDetailDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("OrgCode"))
					loadRequestDetailDO.OrgCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemDescription"))
					loadRequestDetailDO.ItemDescription = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemAltDescription"))
					loadRequestDetailDO.ItemAltDescription = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					loadRequestDetailDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("QuantityLevel1"))
					loadRequestDetailDO.QuantityLevel1 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("QuantityLevel2"))
					loadRequestDetailDO.QuantityLevel2 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("QuantityLevel3"))
					loadRequestDetailDO.QuantityLevel3 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("NonSellableQty"))
					loadRequestDetailDO.NonSellableQty = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("QuantityBU"))
					loadRequestDetailDO.QuantityBU = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("QuantitySU"))
					loadRequestDetailDO.QuantitySU = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("CurrencyCode"))
					loadRequestDetailDO.CurrencyCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("PriceLevel1"))
					loadRequestDetailDO.PriceLevel1 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("PriceLevel2"))
					loadRequestDetailDO.PriceLevel2 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("PriceLevel3"))
					loadRequestDetailDO.PriceLevel3 = StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("MovementReasonCode"))
					loadRequestDetailDO.MovementReasonCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ExpiryDate"))
					loadRequestDetailDO.ExpiryDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("CreatedOn"))
					loadRequestDetailDO.CreatedOn = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("MovementType"))
				{
				}
				else if(localName.equalsIgnoreCase("_MovementStatus"))
				{
				
				}
				else if(localName.equalsIgnoreCase("MovementStatus"))
				{
					loadRequestDetailDO.MovementStatus=currentValue.toString();
				}
				
				else if(localName.equalsIgnoreCase("CancelledQuantity"))
					loadRequestDetailDO.CancelledQuantity =	 StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("InProcessQuantity"))
					loadRequestDetailDO.InProcessQuantity =	 StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("ShippedQuantity"))
					loadRequestDetailDO.ShippedQuantity =	 StringUtils.getFloat(currentValue.toString());
				
				else if(localName.equalsIgnoreCase("MovementDetailDco"))
				{
					if(hashMap.containsKey(movementCode2))
					{
						LoadRequestDO requestDO = hashMap.get(movementCode2);
						if(requestDO.vecItems == null)
							requestDO.vecItems = new ArrayList<LoadRequestDetailDO>();
						requestDO.vecItems.add(loadRequestDetailDO);
						hashMap.put(movementCode2, requestDO);
					}
				}
				
				break;
				
			case WAREHOUSE_QTY:
				
				if(localName.equalsIgnoreCase("WareHouseStockId"))
					wareHouseStockDO.WareHouseStockId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemCode"))
					wareHouseStockDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("OrgCode"))
					wareHouseStockDO.OrgCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					wareHouseStockDO.UserCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("QuantityEach"))
					wareHouseStockDO.QuantityEach = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("AvailableQuantity"))
					wareHouseStockDO.AvailableQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					wareHouseStockDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("SellableQuantity"))
					wareHouseStockDO.SellableQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("NonSellableQuantity"))
					wareHouseStockDO.NonSellableQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ReturnedQuantity"))
					wareHouseStockDO.ReturnedQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("TotalQuantity"))
					wareHouseStockDO.TotalQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("LPOQuantity"))
					wareHouseStockDO.LPOQuantity = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("BatchNumber"))
					wareHouseStockDO.BatchNumber = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ExpiryDate"))
					wareHouseStockDO.ExpiryDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VehicleCode"))
					wareHouseStockDO.VehicleCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("WareHouseStockDco"))
					vecWStockDO.add(wareHouseStockDO);
				
				break;
				
			case NON_SALEABLE_QTY:
				
				if(localName.equalsIgnoreCase("NonSellableItemId"))
					nonSellableItemDO.NonSellableItemId = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Organization_Id"))
					nonSellableItemDO.Organization_Id = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("VehicleCode"))
					nonSellableItemDO.VehicleCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ItemCode"))
					nonSellableItemDO.ItemCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UserCode"))
					nonSellableItemDO.UserCode = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("Reason"))
					nonSellableItemDO.Reason = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UOM"))
					nonSellableItemDO.UOM = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("BatchNumber"))
					nonSellableItemDO.BatchNumber = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ReceivedQty"))
					nonSellableItemDO.ReceivedQty = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("UnloadedQty"))
					nonSellableItemDO.UnloadedQty = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("ExpiryDate"))
					nonSellableItemDO.ExpiryDate = currentValue.toString();
				
				else if(localName.equalsIgnoreCase("NonSellableItemDco"))
					vecNonSellableItem.add(nonSellableItemDO);
				
				break;
				
			default:
				break;
		}
	}
	
	private void insertMovementDetailsDetail()
	{
		InventoryDA inventoryDA = new InventoryDA();
		isInserted=inventoryDA.insertMovementDetailsDetail(hashMap,vecVanStockDOs,vecNonSellableItem,vecWStockDO);
		if(hashMap.size()>0){
			LogDO logDO=logAppActive(hashMap, preference.getStringFromPreference(Preference.EMP_NO,""));
			boolean isFailed=new CustomerDA().insertLog(logDO);
			if(!isFailed)
				if(((BaseActivity)context).isNetworkConnectionAvailable(context)){
					
					boolean isUpload=((BaseActivity)context).uploadLogData(logDO);	
					if(isUpload){
						new JourneyPlanDA().deleteLog(logDO.logId); 	
				}
			}
		}
		/*isInserted=inventoryDA.insertLoadRequests(hashMap,preference.getStringFromPreference(Preference.EMP_NO,""));
		inventoryDA.insertVanLoad(vecVanStockDOs);
		inventoryDA.insertUpdateNonSaleableStock(vecNonSellableItem);
		inventoryDA.insertWareHouseQty(vecWStockDO);*/
		if(isInserted){
			synLogDO.entity = ServiceURLs.GetAppActiveStatus;
			new SynLogDA().insertSynchLog(synLogDO);
//			 preference.saveBooleanInPreference(Preference.IS_SYNC_FAILED, true);
//		     preference.commitPreference();
		}
	}
	private LogDO logAppActive(HashMap<String, LoadRequestDO> hashMap,String userId){
		LogDO logDO=new LogDO();
		logDO.userId=userId;
		logDO.type="Inprocess";
		logDO.key=ServiceURLs.GetAppAccessStatus;
		String data="";
		Set<String> keys = hashMap.keySet();
		for(String Key:keys){
			data+="movmentcode :"+Key;
			ArrayList<LoadRequestDetailDO> vecItems=hashMap.get(Key).vecItems;
			for(LoadRequestDetailDO loadDetail:vecItems){
				data+="itemcode : "+loadDetail.ItemCode+"InprocessQty : "+loadDetail.InProcessQuantity+"ShippedQty : "+loadDetail.ShippedQuantity+"cancelledQty : "+loadDetail.CancelledQuantity+"\n";
			}
			data+="\n";
		}
		logDO.data=data;
		logDO.deviceTime=CalendarUtils.getCurrentDateTime();
		return logDO;
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }

	public boolean getStatus(){
	return isInserted;
	}
	
}
