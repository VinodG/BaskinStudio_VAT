package com.winit.baskinrobbin.salesman.utilities;

import java.util.ArrayList;
import java.util.Collections;

import com.winit.baskinrobbin.salesman.dataobject.BaseComparableDO;
import com.winit.baskinrobbin.salesman.dataobject.TitleObj;

public class CollectionUtils
{
	public static String FORWARD_SLASH = "/";
	public static String IPHAN = "-";
	public enum SortedType
	{
		DEFAULT,
		DATE,
		DATETIME
	}
	public static ArrayList<BaseComparableDO> createSortedCollection(ArrayList<BaseComparableDO> listObj,SortedType sortBy ,boolean reverse ,boolean resultWithHeadings)
	{
		listObj.trimToSize();
		BaseComparableDO.comparableStringType = sortBy;
		Collections.sort(listObj); 
		BaseComparableDO.comparableStringType = SortedType.DEFAULT;
		
		if(reverse)
		{
			Collections.reverse(listObj);
			
		}
		
		if(resultWithHeadings)
		{
			ArrayList<BaseComparableDO> sortedWithHeading = new ArrayList<BaseComparableDO>();
			Object prvObjType = null, curObjType = null;
			
			for(BaseComparableDO baseDO :listObj)
			{
				if(sortBy == SortedType.DATE || sortBy == SortedType.DATETIME)
				{
					curObjType = baseDO.getValue(sortBy);
					
					if(prvObjType != null && !curObjType.toString().equalsIgnoreCase(prvObjType.toString()))
					{
//						String[] arr = curObjType.toString().split(IPHAN);
						
						TitleObj titleObj = new TitleObj();
						titleObj.rawStr =  curObjType.toString();
						
						sortedWithHeading.add(titleObj);
					
					}
					else if(prvObjType == null)
					{
//						String[] arr = curObjType.toString().split(IPHAN);
						
						TitleObj titleObj = new TitleObj();
						
						titleObj.rawStr =  curObjType.toString();
						
						sortedWithHeading.add(titleObj);
					}
					
					prvObjType = curObjType;
				}
				
				sortedWithHeading.add(baseDO);
			}
			
			return sortedWithHeading;
		}
		
		return listObj;
	}
}
