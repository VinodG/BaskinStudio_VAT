package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TransferInOutDA;
import com.winit.baskinrobbin.salesman.dataobject.SyncLogDO;
import com.winit.baskinrobbin.salesman.dataobject.TransferDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.TransferInoutDO;

public class GetAllTransfers extends BaseHandler{

	private Vector<TransferInoutDO> vecTransferInoutDOs;
	private Vector<TransferDetailDO> vecTransferDetailDOs ;
	private TransferInoutDO transferInoutDO;
	private TransferDetailDO transferDetailDO;
	private SyncLogDO objSyncLogDO;
	private boolean isStatus = false, isTransferDetails;
	
	public GetAllTransfers(Context context) {
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = true;
		currentValue=new StringBuilder();  
		
		if(localName.equalsIgnoreCase("TransferHeaders"))
		{
			vecTransferInoutDOs = new Vector<TransferInoutDO>();
		}
		else if(localName.equalsIgnoreCase("TransferHeaderDco"))
		{
			isStatus = true;
			transferInoutDO = new TransferInoutDO();
		}
		else if(localName.equalsIgnoreCase("TransferDetails"))
		{
			isTransferDetails = true;
			vecTransferDetailDOs = new Vector<TransferDetailDO>();
		}
		else if(localName.equalsIgnoreCase("TransferDetailDco"))
		{
			transferDetailDO = new TransferDetailDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentElement = false;
		if(localName.equalsIgnoreCase("ServerTime"))
			preference.saveStringInPreference(Preference.GET_ALL_TRANSFER_SYNCHTIME, currentValue.toString());
		else if(localName.equalsIgnoreCase("TransferId"))
		{
			if(isTransferDetails)
				transferDetailDO.InventoryUID = currentValue.toString();
			else
				transferInoutDO.InventoryUID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SourceEmpCode"))
		{
			transferInoutDO.fromEmpNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DestinationEmpCode"))
		{
			transferInoutDO.toEmpNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SourceVehicleCode"))
		{
			transferInoutDO.sourceVNO = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DestinationVehicleCode"))
		{
			transferInoutDO.destVNO = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Date"))
		{
			transferInoutDO.Date = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Type"))
		{
			transferInoutDO.trnsferType = "IN";
		}
		else if(localName.equalsIgnoreCase("Status") && isStatus)
		{
			transferInoutDO.transferStatus = "N";
		}
		else if(localName.equalsIgnoreCase("TransferHeaderDco"))
		{
			vecTransferInoutDOs.add(transferInoutDO);
		}
		else if(localName.equalsIgnoreCase("TransferDetailId"))
		{
			transferDetailDO.transferDetailID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode"))
		{
			transferDetailDO.itemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ReqestedCases"))
		{
			transferDetailDO.cases = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ReqestedUnits"))
		{
			transferDetailDO.units = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OrderNumber"))
		{
			transferInoutDO.sourceOrderID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SourceOrderNumber"))
		{
			transferInoutDO.destOrderID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TransferDetailDco"))
		{
			vecTransferDetailDOs.add(transferDetailDO);
		}
		else if(localName.equalsIgnoreCase("GetAllTransfersResult"))
		{
			boolean isInsetred = false;
			
			isInsetred = new TransferInOutDA().insertTransferInOutNew(vecTransferInoutDOs);
			isInsetred = new TransferInOutDA().insertTransferInOutDetailsNew(vecTransferDetailDOs);
			
			if(isInsetred)
				preference.commitPreference();
		}
		
//		else if(localName.equalsIgnoreCase("ReqestedUnits"))
//		{
//			deliveryAgentOrderDetailDco.preUnits = StringUtils.getInt(currentValue.toString());
//		}
//		else if(localName.equalsIgnoreCase("TransferDetailDco"))
//		{
//			transferInoutDO.vecOrderDetailDcos.add(deliveryAgentOrderDetailDco);
//		}
		
//		else if(localName.equalsIgnoreCase("TransferHeaders"))
//		{
//			new TransferInOutDA().insertTransferInOutNew(vecTransferInoutDOs);
//		}
	}
	
	public Vector<TransferInoutDO> getTraferDate()
	{
		return vecTransferInoutDOs;
	}
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
