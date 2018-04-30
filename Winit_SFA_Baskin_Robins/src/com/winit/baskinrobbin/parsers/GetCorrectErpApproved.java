package com.winit.baskinrobbin.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetCorrectErpApproved extends BaseHandler {

	LoadRequestDetailDO loadRequestDetailDO = null;
	private LoadRequestDO loadRequestDO = null;
	private SynLogDO synLogDO = new SynLogDO();
	private boolean isUpdated;
	private String MovementCode="",tempMovementcode="";
	private int PARSE_TYPE = -1;
	private static final int TYPE_MOVEMENT_DETAILS = 1;
	

	public GetCorrectErpApproved(Context context,String MovementCode) {
		super(context);
		this.MovementCode=MovementCode;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = true;
		currentValue = new StringBuilder();

		if (localName.equalsIgnoreCase("MovementDetails")){
			PARSE_TYPE = TYPE_MOVEMENT_DETAILS;
			loadRequestDO = new LoadRequestDO();
		}

		else if (localName.equalsIgnoreCase("MovementDetailDco"))
			loadRequestDetailDO = new LoadRequestDetailDO();

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentElement = false;
		if (localName.equalsIgnoreCase("Status"))
			synLogDO.action = currentValue.toString();

		else if (localName.equalsIgnoreCase("ServerTime"))
			synLogDO.TimeStamp = currentValue.toString();

		else if (localName.equalsIgnoreCase("ModifiedDate"))
			synLogDO.UPMJ = currentValue.toString();

		else if (localName.equalsIgnoreCase("ModifiedTime"))
			synLogDO.UPMT = currentValue.toString();

		switch (PARSE_TYPE) {
		
		case TYPE_MOVEMENT_DETAILS:
			if (localName.equalsIgnoreCase("MovementCode")) {
				loadRequestDetailDO.MovementCode = currentValue.toString();
				tempMovementcode=currentValue.toString();
			} else if (localName.equalsIgnoreCase("ItemCode"))
				loadRequestDetailDO.ItemCode = currentValue.toString();

			else if (localName.equalsIgnoreCase("InProcessQuantity"))
				loadRequestDetailDO.InProcessQuantity = StringUtils.getFloat(currentValue.toString());

			else if (localName.equalsIgnoreCase("MovementDetailDco")) {
				loadRequestDO.vecItems.add(loadRequestDetailDO);
			}
			else if(localName.equalsIgnoreCase("GetAppCorrectInProcessResponseResult"))
			{
				updateCorrectInprocess(loadRequestDO);
			}
			break;

		default:
			break;
		} 
		
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (currentElement)
			currentValue.append(new String(ch, start, length));
	}

	private void updateCorrectInprocess(LoadRequestDO loadrequestDO) {
		if(tempMovementcode.equalsIgnoreCase(MovementCode)){
			isUpdated=new InventoryDA().updateInprocess(loadrequestDO);

			if(isUpdated){
				synLogDO.entity = ServiceURLs.GetAppCorrectInProcessResponse;
				new SynLogDA().insertSynchLog(synLogDO);
			}
		}
		
	}
}
