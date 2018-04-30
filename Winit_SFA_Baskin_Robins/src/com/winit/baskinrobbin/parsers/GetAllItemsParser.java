package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.ProductsDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllItemsParser extends BaseHandler
{
	private ProductsDO productsDO;
	private Vector<ProductsDO> vector;
	private ProductsDA productsBL;
	private NameIDDo nameIDDo;
	
	public GetAllItemsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Items"))
		{
			vector = new Vector<ProductsDO>();
			productsBL = new ProductsDA();
		}
		else if(localName.equalsIgnoreCase("ItemDco"))
		{
			productsDO = new ProductsDO();
		}
		else if(localName.equalsIgnoreCase("ImageGallery"))
		{
			productsDO.vecProductImages = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("ImageGalleryDco"))
		{
			nameIDDo = new NameIDDo();
		}
	}
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_ITEMS+Preference.LAST_SYNC_TIME, currentValue.toString());
			LogUtils.errorLog("CurrentTime", "AllItems - "+currentValue.toString());
		}
		
		else if(localName.equalsIgnoreCase("ItemCode"))
		{
			productsDO.SKU = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemDesc"))
		{
			productsDO.ItemDesc = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GroupId"))
		{
			productsDO.GroupId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CompanyId"))
		{
			productsDO.CompanyId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Category"))
		{
			productsDO.CategoryId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Brand"))
		{
			productsDO.Brand = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			productsDO.UOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SECONDARY_UOM"))
		{
			productsDO.secondryUOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CaseBarCode"))
		{
			productsDO.CaseBarCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UnitBarCode"))
		{
			productsDO.UnitBarCode = currentValue.toString();	
		}
		else if(localName.equalsIgnoreCase("ItemType"))
		{
			productsDO.ItemType = currentValue.toString();	
		}
		else if(localName.equalsIgnoreCase("TaxGroupCode"))
		{
			productsDO.TaxGroupCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TaxPercentage"))
		{
			productsDO.TaxPercentage = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UnitPerCase"))
		{
			productsDO.UnitsPerCases = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PricingKey"))
		{
			productsDO.PricingKey = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemBatchCode"))
		{
			productsDO.BatchCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemId"))
		{
			productsDO.ProductId =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LOT_CONTROL_CODE"))
		{
			productsDO.LOT_CONTROL_CODE =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LOT_CONTROL_NAME"))
		{
			productsDO.LOT_CONTROL_NAME =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ImageGalleryId"))
		{
			nameIDDo.strId =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ImagePath"))
		{
//			if(currentValue.contains("../"))
//				currentValue = currentValue.replace("../", ServiceURLs.IMAGE_MAIN_URL);
			String path = currentValue.toString();
			if(path.contains("/ProductCatalogImages/"))
				path = path.substring(path.indexOf("/ProductCatalogImages/"));
			
			nameIDDo.strName = path;
		}
		else if(localName.equalsIgnoreCase("ImageGalleryDco"))
		{
			productsDO.vecProductImages.add(nameIDDo);
		}
		else if(localName.equalsIgnoreCase("ItemDco"))
		{
			vector.add(productsDO);
			if(vector.size()>AppConstants.SYNC_COUNT)
			{
				saveIntoProductTable(vector);
				LogUtils.errorLog("ItemDco",""+vector.size());
				vector.clear();
			}
		}
		else if(localName.equalsIgnoreCase("Items"))
		{
			if(saveIntoProductTable(vector))
				preference.commitPreference();
		}
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	private boolean saveIntoProductTable(Vector<ProductsDO> vector)
	{
		if(vector.size() > 0)
			return productsBL.insertProducts(vector);
		else
			return false;
	}
}
