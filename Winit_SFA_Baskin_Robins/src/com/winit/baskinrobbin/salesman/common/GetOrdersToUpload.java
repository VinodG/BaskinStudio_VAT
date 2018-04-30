package com.winit.baskinrobbin.salesman.common;

import java.util.Vector;

import android.content.Context;
import android.text.TextUtils;

import com.winit.baskinrobbin.parsers.InsertOrdersParser;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.DamageImageDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetOrdersToUpload 
{
	private Context mContext;
	private Preference preference;
	private int TYPE = AppStatus.TODAY_DATA;
	
	public GetOrdersToUpload(Context context, Preference preference, int TYPE)
	{
		this.mContext 	= context;
		this.preference = preference;
		this.TYPE 		= TYPE;
	}
	
	public boolean uploadOrders(String empNo)
	{
		boolean isUploaded = true;
		final Vector<OrderDO> vecSalesOrders  		= 	new CommonDA().getAllSalesOrderToPost(empNo, TYPE);
		final InsertOrdersParser insertOrdersParser = 	new InsertOrdersParser(mContext);
        if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (OrderDO orderDO : vecSalesOrders) 
	    	{
				if(orderDO.vecProductDO != null && orderDO.vecProductDO.size() > 0)
				{
					for (ProductDO pDo : orderDO.vecProductDO)
					{
						if(pDo.vecDamageImagesNew != null && pDo.vecDamageImagesNew.size()>0)
							for (DamageImageDO damageImageDO : pDo.vecDamageImagesNew)
								damageImageDO.ImagePath = new UploadImage().uploadImage(mContext, damageImageDO.ImagePath, ServiceURLs.ORDER_IMAGES, true);
					}
				}
				
				Vector<OrderDO> vec = new Vector<OrderDO>();
				vec.add(orderDO);
				
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				
				connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.sendAllOrders(vec, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXMLWithAuth, preference);
			}
        }
        
        isUploaded = new CommonDA().getAllSalesUnuploaded(empNo, TYPE);
        return isUploaded;
	}
	
	private void uploadImages()
	{
		Vector<DamageImageDO> vecImages  = new CommonDA().getDamageIMagePic(AppStatus.STATUS_INSERTED, AppConstants.ORDER_LIMIT);
		
		if(vecImages != null && vecImages.size() > 0)
		{
			for(DamageImageDO damageImageDO : vecImages)
			{
				damageImageDO.ImagePath = new UploadImage().uploadImage(mContext, damageImageDO.ImagePath, ServiceURLs.ORDER_IMAGES, true);
				if(!TextUtils.isEmpty(damageImageDO.ImagePath))
					new CommonDA().updateImageStatus(damageImageDO, AppStatus.STATUS_UPLOADED);
			}
		}
		
//		vecImages  = new CommonDA().getDamageIMagePic(AppConstants.STATUS_UPLOADED, AppConstants.IMAGE_LIMIT);
//		if(vecImages != null && vecImages.size() >0)
//		{
//			ConnectionHelper connectionHelper = new ConnectionHelper(null);
//			connectionHelper.sendRequest_Bulk(mContext,BuildXMLRequest.sendAllOrders(vec, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXMLWithAuth, preference);
//		}
	}
}
