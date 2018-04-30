package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetAllCategories extends BaseHandler
{
	private CategoryDO objCategoryDO;
	private Vector<CategoryDO> vecCategories;

	public GetAllCategories(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Categories"))
		{
			vecCategories = new Vector<CategoryDO>();
		}
		else if(localName.equalsIgnoreCase("CategoryDco"))
		{
			objCategoryDO = new CategoryDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_ALL_CATEGORY+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CODE"))
		{
			objCategoryDO.categoryId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("NAME"))
		{
			objCategoryDO.categoryName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Icon"))
		{
			String path = currentValue.toString();
			if(path.contains("../"))
				path = path.replace("../", ServiceURLs.IMAGE_MAIN_URL);
			objCategoryDO.categoryIcon = path;
		}
		else if(localName.equalsIgnoreCase("CategoryDco"))
		{
			vecCategories.add(objCategoryDO);
		}
		else if(localName.equalsIgnoreCase("Categories"))
		{
			if(vecCategories.size() > 0)
			{
				if(saveDataIntoDatatbase(vecCategories))
					preference.commitPreference();
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveDataIntoDatatbase(Vector<CategoryDO> vector)
	{
		return new CommonDA().insertAllCategories(vector);
	}
}
