package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CompetitorDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.CompBrandDO;
import com.winit.baskinrobbin.salesman.dataobject.CompCategoryDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetCompetitorDetail extends BaseHandler
{
	private Vector<CompCategoryDO> vecCompCategory ;
	private CompCategoryDO compCategoryDO;
	private Vector<CompBrandDO> vecBrandDO ;
	private CompBrandDO brandDO;
	private String empNo;
	private final int TYPE_CAT = 1, TYPE_BRAND = 2;
	private int PARSE_TYPE = -1;
	public GetCompetitorDetail(Context context) 
	{
		super(context);
	}
	
	public GetCompetitorDetail(Context context, String empNo) 
	{
		super(context);
		this.empNo = empNo;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("CompetitorCategories"))
			vecCompCategory = new Vector<CompCategoryDO>();
		
		else if(localName.equalsIgnoreCase("CompetitorCategoryDco"))
		{
			PARSE_TYPE = TYPE_CAT;
			compCategoryDO = new CompCategoryDO();
		}
		
		else if(localName.equalsIgnoreCase("CompetitorBrands"))
			vecBrandDO = new Vector<CompBrandDO>();
		
		else if(localName.equalsIgnoreCase("CompetitorBrandDco"))
		{
			PARSE_TYPE = TYPE_BRAND;
			brandDO = new CompBrandDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(PARSE_TYPE == -1 && localName.equalsIgnoreCase("CurrentTime"))
			preference.saveStringInPreference(ServiceURLs.GetCompetitorDetail+empNo+Preference.LAST_SYNC_TIME, currentValue.toString());
		
		else if(localName.equalsIgnoreCase("competitorDetail"))
		{
			if(insertCompetitorDetail(vecCompCategory, vecBrandDO))
				preference.commitPreference();
		}
		
		switch (PARSE_TYPE) 
		{
			case TYPE_CAT:
				if(localName.equalsIgnoreCase("CompetitorCategoryId"))
					compCategoryDO.CategoryId = currentValue.toString();
				else if(localName.equalsIgnoreCase("Category"))
					compCategoryDO.Category = currentValue.toString();
				else if(localName.equalsIgnoreCase("IsActive"))
					compCategoryDO.IsActive = currentValue.toString();
				else if(localName.equalsIgnoreCase("CompetitorCategoryDco"))
					vecCompCategory.add(compCategoryDO);
				break;
				
			case TYPE_BRAND:
				if(localName.equalsIgnoreCase("CompetitorBrandId"))
					brandDO.BrandId = currentValue.toString();
				else if(localName.equalsIgnoreCase("Brand"))
					brandDO.Brand = currentValue.toString();
				else if(localName.equalsIgnoreCase("IsActive"))
					brandDO.IsActive = currentValue.toString();
				else if(localName.equalsIgnoreCase("CompetitorBrandDco"))
					vecBrandDO.add(brandDO);
				break;
				
			default:
				break;
		}
	}
	
	private boolean insertCompetitorDetail( Vector<CompCategoryDO> vecCompCategory, Vector<CompBrandDO> vecBrandDO)
	{
		return new CompetitorDetailDA().insertData(vecCompCategory, vecBrandDO);
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
