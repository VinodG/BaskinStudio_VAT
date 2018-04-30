package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetCustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetTypeDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetCustomerDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetTypeDo;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAssetMastersParser extends BaseHandler
{
	private AssetDO assetDO;
	private Vector<AssetDO> vecasset;
	private Vector<AssetTypeDo> vecAssetTypeDos;
	private AssetTypeDo assetTypeDo;
	private Vector<AssetCustomerDo> vecAssetCustomerDos;
	private AssetCustomerDo assetCustomerDo;
	private SynLogDO synLogDO;
//	private boolean isAssetType = false , isAsset = false , isAssetCustomer = false;
	private final int AssetType= 2, Asset= 4,AssetCustomer= 5, SynLog=0;

	private int SELECTED_TYPE;

	public GetAssetMastersParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("GetAssetMastersResult"))
		{
			SELECTED_TYPE = SynLog;
			synLogDO = new SynLogDO();
//			synLogDO.TimeStamp = CalendarUtils.getCurrentSynchDateTime();
		}
		else if(localName.equalsIgnoreCase("Assets"))
		{
			vecasset = new Vector<AssetDO>();
		}
		else if(localName.equalsIgnoreCase("AssetDco"))
		{
			SELECTED_TYPE = Asset;
			assetDO = new AssetDO();
//			isAsset = true;
		}
		else if(localName.equalsIgnoreCase("AssetTypes"))
		{
			vecAssetTypeDos = new Vector<AssetTypeDo>();
		}
		else if(localName.equalsIgnoreCase("AssetTypeDco"))
		{
			SELECTED_TYPE = AssetType;
			assetTypeDo = new AssetTypeDo();
//			isAssetType = true;
		}
		else if(localName.equalsIgnoreCase("AssetCustomers"))
		{
			vecAssetCustomerDos = new Vector<AssetCustomerDo>();
		}
		else if(localName.equalsIgnoreCase("AssetCustomerDco"))
		{
			SELECTED_TYPE = AssetCustomer;
//			isAssetCustomer = true;
			assetCustomerDo = new AssetCustomerDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("GetAssetMastersResult"))
		{
			saveDataIntoDatatbase(vecasset, vecAssetTypeDos, vecAssetCustomerDos);
			new SynLogDA().insertSynchLog(synLogDO);
		}
		else
		{
			switch (SELECTED_TYPE) 
			{
			case SynLog:
				if(localName.equalsIgnoreCase("Status"))
				{
					synLogDO.action = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ServerTime"))
				{
					synLogDO.TimeStamp = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					synLogDO.UPMJ = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					synLogDO.UPMT = currentValue.toString();
				}
				break;
			case Asset:
				if(localName.equalsIgnoreCase("AssetId"))
				{
					assetDO.assetId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("BarCode"))
				{
					assetDO.barCode = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("AssetType"))
				{
					assetDO.assetType = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Name"))
				{
					assetDO.name = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Capacity"))
				{
					assetDO.capacity = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ImagePath"))
				{
					assetDO.imagePath = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("InstallationDate"))
				{
					assetDO.installationDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("LastServiceDate"))
				{
					assetDO.lastServiceDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					assetDO.modifiedDate = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					assetDO.modifiedTime = currentValue.toString();
				}

				else if(localName.equalsIgnoreCase("AssetDco"))
				{
					vecasset.add(assetDO);
				}

				break;
			case AssetType:
				if(localName.equalsIgnoreCase("AssetTypeId"))
				{
					assetTypeDo.assetTypeId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Code"))
				{
					assetTypeDo.code = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedDate"))
				{
				}
				else if(localName.equalsIgnoreCase("ModifiedTime"))
				{
				}
				else if(localName.equalsIgnoreCase("AssetTypeDco"))
				{
					vecAssetTypeDos.add(assetTypeDo);
				}
				break;
			case AssetCustomer:
				
				if(localName.equalsIgnoreCase("AssetCustomerId"))
				{
					assetCustomerDo.assetCustomerId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("AssetId"))
				{
					assetCustomerDo.assetId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("SiteNo"))
				{
					assetCustomerDo.siteNo = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("ModifiedDate"))
				{
				}
				else if(localName.equalsIgnoreCase("ModifiedTime"))
				{
				}
				else if(localName.equalsIgnoreCase("InstallationDate"))
				{
				}
				else if(localName.equalsIgnoreCase("IsActive"))
				{
				}
				else if(localName.equalsIgnoreCase("AssetCustomerDco"))
				{
					vecAssetCustomerDos.add(assetCustomerDo);
				}
				
				break;

			default:
				break;
			}
		}
		
//		if(localName.equalsIgnoreCase("Status"))
//		{
//			synLogDO.action = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("Message"))
//		{
//		}
//		else if(localName.equalsIgnoreCase("Count"))
//		{
//		}
//		else if(localName.equalsIgnoreCase("ServerTime"))
//		{
//			synLogDO.TimeStamp = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("ModifiedDate"))
//		{
//			if(isAsset)
//				assetDO.modifiedDate = currentValue.toString();
//			else if(isAssetType)
//				assetTypeDo.modifiedDate = currentValue.toString();
//			else if(isAssetCustomer)
//				assetCustomerDo.modifiedDate = currentValue.toString();
//			else
//				synLogDO.UPMJ = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("ModifiedTime"))
//		{
//			if(isAsset)
//				assetDO.modifiedTime = currentValue.toString();
//			else if(isAssetType)
//				assetTypeDo.modifiedTime = currentValue.toString();
//			else if(isAssetCustomer)
//				assetCustomerDo.modifiedTime = currentValue.toString();
//			else
//				synLogDO.UPMT = currentValue.toString();
//		}
//		//  Start AssetDos 
//		
//		else if(localName.equalsIgnoreCase("AssetId"))
//		{
//			 if(isAsset)
//				 assetDO.assetId = currentValue.toString();
//			 else if(isAssetCustomer)
//				 assetCustomerDo.assetId = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("BarCode"))
//		{
//			assetDO.barCode = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("AssetType"))
//		{
//			assetDO.assetType = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("Name"))
//		{
//			if(isAsset)
//				assetDO.name = currentValue.toString();
//			else if(isAssetType)
//				assetTypeDo.name = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("Capacity"))
//		{
//			assetDO.capacity = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("ImagePath"))
//		{
//			assetDO.imagePath = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("InstallationDate"))
//		{
//			assetDO.installationDate = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("LastServiceDate"))
//		{
//			assetDO.lastServiceDate = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("AssetDco"))
//		{
//			vecasset.add(assetDO);
//			isAsset = false;
//		}
//		else if(localName.equalsIgnoreCase("Assets"))
//		{
//		}
//		 
//		 // End AssetDos
//		 // Start AssetTypeDos
//		 
//		else if(localName.equalsIgnoreCase("AssetTypeId"))
//		{
//			assetTypeDo.assetTypeId = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("Code"))
//		{
//			assetTypeDo.code = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("AssetTypeDco"))
//		{
//			vecAssetTypeDos.add(assetTypeDo);
//			isAssetType = false;
//		}
//		else if(localName.equalsIgnoreCase("AssetTypes"))
//		{
//		}
		 
		 // End AssetTypeDos
		 // Start AssetCustomerDos
		 
//		else if(localName.equalsIgnoreCase("AssetCustomerId"))
//		{
//			assetCustomerDo.assetCustomerId = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("SiteNo"))
//		{
//			assetCustomerDo.siteNo = currentValue.toString();
//		}
//		else if(localName.equalsIgnoreCase("AssetCustomerDco"))
//		{
//			vecAssetCustomerDos.add(assetCustomerDo);
//			isAssetCustomer = false;
//		}
//		else if(localName.equalsIgnoreCase("AssetCustomers"))
//		{
//		}
		 
		 // End AssetCustomerDos
//		 
//		else if(localName.equalsIgnoreCase("GetAssetMastersResult"))
//		{
//			saveDataIntoDatatbase(vecasset, vecAssetTypeDos, vecAssetCustomerDos);
//		}
		 
		 
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveDataIntoDatatbase(Vector<AssetDO> vecAssetDOs,Vector<AssetTypeDo> vecAssetTypeDos, Vector<AssetCustomerDo> veAssetCustomerDos)
	{
		new AssetDA().insertAsset(vecAssetDOs);
		new AssetTypeDA().insertAssetTypes(vecAssetTypeDos);
		new AssetCustomerDA().insertAssetCustomer(veAssetCustomerDos);
		synLogDO.entity = ServiceURLs.GetAssetMasters;
		new SynLogDA().insertSynchLog(synLogDO);
		return true;
	}
}
