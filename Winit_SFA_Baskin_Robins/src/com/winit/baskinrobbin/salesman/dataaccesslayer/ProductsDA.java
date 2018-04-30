package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.databaseaccess.DictionaryEntry;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductsDO;
import com.winit.baskinrobbin.salesman.dataobject.UOMConversionFactorDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class ProductsDA 
{
	//Need to change the method 
	public boolean insertProducts(Vector<ProductsDO> vecProductsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
				
//			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblProducts WHERE ItemCode = ?");
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblProducts (ProductId, ItemCode, Category, Description,UnitPerCase,ItemBatchCode,UOM,CaseBarCode,UnitBarCode,ItemType,PricingKey,Brand, SecondaryUOM, TaxGroupCode,TaxPercentage, LOT_CONTROL_CODE, LOT_CONTROL_NAME) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblProducts SET " +
					"ProductId =? , ItemCode =?, Category =?, Description = ?, UnitPerCase= ?, ItemBatchCode = ?," +
					" UOM = ? , CaseBarCode = ?, UnitBarCode = ? , ItemType = ? , PricingKey = ?, Brand = ?, SecondaryUOM =?,TaxGroupCode=?,TaxPercentage=?, LOT_CONTROL_CODE =?, LOT_CONTROL_NAME =? WHERE ItemCode = ?");
			
			SQLiteStatement stmtInsertInProductImages = objSqliteDB.compileStatement("INSERT INTO tblProductImages (ImageGalleryId, ItemCode, ImagePath) VALUES(?,?,?)");
			SQLiteStatement stmtUpdateProductImages = objSqliteDB.compileStatement("UPDATE tblProductImages SET ItemCode =? , ImagePath =? WHERE ImageGalleryId = ?");
//			SQLiteStatement stmtSelectProductImage = objSqliteDB.compileStatement("SELECT COUNT(*) from tblProductImages WHERE ImageGalleryId = ?");
			
			 for(int i=0;i<vecProductsDOs.size();i++)
			 {
				ProductsDO productsDO = vecProductsDOs.get(i);
//				stmtSelectRec.bindString(1, productsDO.SKU);
					
//				long countRec = stmtSelectRec.simpleQueryForLong();
//				if(countRec != 0)
//				{					
					if(productsDO != null )
					{
						stmtUpdate.bindString(1, productsDO.ProductId);
						stmtUpdate.bindString(2, productsDO.SKU);
						stmtUpdate.bindString(3, productsDO.CategoryId);
						stmtUpdate.bindString(4, productsDO.ItemDesc);
						stmtUpdate.bindLong  (5, productsDO.UnitsPerCases);
						stmtUpdate.bindString(6, productsDO.BatchCode);
						stmtUpdate.bindString(7, productsDO.UOM);
						stmtUpdate.bindString(8, productsDO.CaseBarCode);
						stmtUpdate.bindString(9, productsDO.UnitBarCode);
						stmtUpdate.bindString(10, productsDO.ItemType);
						stmtUpdate.bindString(11, productsDO.PricingKey);
						stmtUpdate.bindString(12, productsDO.Brand);
						stmtUpdate.bindString(13, productsDO.secondryUOM);
						stmtUpdate.bindString(14, productsDO.TaxGroupCode);
						stmtUpdate.bindString(15, productsDO.TaxPercentage);
						stmtUpdate.bindString(16, productsDO.LOT_CONTROL_CODE);
						stmtUpdate.bindString(17, productsDO.LOT_CONTROL_NAME);
						stmtUpdate.bindString(18, productsDO.SKU);
						
						for(int index = 0;productsDO.vecProductImages!=null && index<productsDO.vecProductImages.size();index++)
						{
//							long countRecord = stmtSelectProductImage.simpleQueryForLong();
//							stmtSelectProductImage.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
//							if(countRecord!=0)
//							{
								stmtUpdateProductImages.bindString(1, productsDO.SKU);
								stmtUpdateProductImages.bindString(2, productsDO.vecProductImages.get(index).strName);
								stmtUpdateProductImages.bindDouble(3, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
//								stmtUpdateProductImages.execute();
//							}
//							else
							if(stmtUpdateProductImages.executeUpdateDelete()<=0)
							{
								stmtInsertInProductImages.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
								stmtInsertInProductImages.bindString(2, productsDO.SKU);
								stmtInsertInProductImages.bindString(3, productsDO.vecProductImages.get(index).strName);
								stmtInsertInProductImages.executeInsert();
							}
						}
//						stmtUpdate.execute();
//					}
//				}
//				else
				if(stmtUpdate.executeUpdateDelete()<=0)
				{
//					if(productsDO != null )
//					{
						stmtInsert.bindString(1, productsDO.ProductId);
						stmtInsert.bindString(2, productsDO.SKU);
						stmtInsert.bindString(3, productsDO.CategoryId);
						stmtInsert.bindString(4, productsDO.ItemDesc);
						stmtInsert.bindLong  (5, productsDO.UnitsPerCases);
						stmtInsert.bindString(6, productsDO.BatchCode);
						stmtInsert.bindString(7, productsDO.UOM);
						stmtInsert.bindString(8, productsDO.CaseBarCode);
						stmtInsert.bindString(9, productsDO.UnitBarCode);
						stmtInsert.bindString(10, productsDO.ItemType);
						stmtInsert.bindString(11, productsDO.PricingKey);
						stmtInsert.bindString(12, productsDO.Brand);
						stmtInsert.bindString(13, productsDO.secondryUOM);
						stmtInsert.bindString(14, productsDO.TaxGroupCode);
						stmtInsert.bindString(15, productsDO.TaxPercentage);
						stmtInsert.bindString(16, productsDO.LOT_CONTROL_CODE);
						stmtInsert.bindString(17, productsDO.LOT_CONTROL_NAME);
						
						for(int index = 0;productsDO.vecProductImages!=null && index<productsDO.vecProductImages.size();index++)
						{
							stmtInsertInProductImages.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
							stmtInsertInProductImages.bindString(2, productsDO.SKU);
							stmtInsertInProductImages.bindString(3, productsDO.vecProductImages.get(index).strName);
							stmtInsertInProductImages.executeInsert();
						}
						stmtInsert.executeInsert();
					}
				}
			 }
			 
//			 stmtSelectRec.close();
			 stmtInsert.close();
			 stmtUpdate.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null)
			{
				objSqliteDB.close();
				return true;
			}
		}
		return true;
		}
	}
	public HashMap<String,UOMConversionFactorDO> getUOMConversionByVectorOfItem(ArrayList<VanLoadDO> vecOrdProduct){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorsUoms=null;
			
			
			HashMap<String,UOMConversionFactorDO> hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				
				for (int i = 0; i < vecOrdProduct.size(); i++) {
					String query= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where UOM='"+vecOrdProduct.get(i).UOM+"' AND ItemCode='"+vecOrdProduct.get(i).ItemCode+"'";
					
					cursorsUoms=sqLiteDatabase.rawQuery(query, null);
					if(cursorsUoms.moveToFirst()){
						do {
							UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
							conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
							conversionFactorDO.UOM			=	cursorsUoms.getString(1);
							conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
							conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
							
							String key = conversionFactorDO.ItemCode;
							hashArrUoms.put(key,conversionFactorDO);
						} while (cursorsUoms.moveToNext());
					}
					if(cursorsUoms!=null && !cursorsUoms.isClosed())
						cursorsUoms.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorsUoms!=null && !cursorsUoms.isClosed())
					cursorsUoms.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return hashArrUoms;
		}
	}
	public Vector<ProductDO> getProductsDetailsByCategoryId(String barCode, String catgId, String orderedItemsList, String strPricingClass, 
			HashMap<String, Vector<String>> hmUOMFactors, boolean fromVanstock, HashMap<String, HHInventryQTDO> hmInventory)
	{
		synchronized(MyApplication.MyLock) 
		{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery  = "";
			if(barCode == null)
				strQuery  = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.Category like '"+catgId+"%' AND tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')";
			else
				strQuery  = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.UnitBarCode = '"+barCode+"' AND tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')";
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.Description = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.Description1 =  data[i][3].value.toString(); 
					if(data[i][32].value != null)
						productsDO.CategoryId =data[i][32].value.toString(); 
					if(data[i][18].value != null)
						productsDO.BatchCode = data[i][18].value.toString();
					if(data[i][11].value != null)
						productsDO.UOM =  data[i][11].value.toString();
					
					productsDO.primaryUOM = productsDO.UOM;
					
					if(data[i][13].value != null)
						productsDO.CaseBarCode = data[i][13].value.toString();
					if(data[i][14].value != null)
						productsDO.UnitBarCode =data[i][14].value.toString();
					if(data[i][15].value != null)
						productsDO.ItemType =  data[i][15].value.toString();
					if(data[i][17].value != null)
						productsDO.PricingKey = data[i][17].value.toString();
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();
					
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();
					
					if(data[i][30].value != null)
						productsDO.LOT_CONTROL_CODE = data[i][30].value.toString();
					
					productsDO.preCases = "";
					productsDO.preUnits = "";
					
					if(hmUOMFactors.containsKey(productsDO.SKU))
						productsDO.vecUOM = hmUOMFactors.get(productsDO.SKU);
					else if(productsDO.CategoryId != null && productsDO.CategoryId.contains(AppConstants.TUB))
						productsDO.vecUOM = hmUOMFactors.get(AppConstants.ITEM_CODE);
					
					if(productsDO.vecUOM == null || productsDO.vecUOM.size() <= 0)
					{
						productsDO.vecUOM = new Vector<String>();
						productsDO.vecUOM.add(productsDO.UOM);
					}
					else
					{
						if(!productsDO.vecUOM.contains(productsDO.UOM))
							productsDO.UOM = productsDO.vecUOM.get(0);
					}
					
					if(fromVanstock)
					{
						if( hmInventory != null && (hmInventory.containsKey(productsDO.SKU+productsDO.UOM) || hmInventory.containsKey(productsDO.SKU)))
							vector.add(productsDO);
					}
					else
						vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
		}
	}
	
	//====================================Added For VAT on 11th DEC 2017===============================
	public Vector<ProductDO> getProductsDetailsByCategoryIdNew(String barCode, String catgId, String orderedItemsList, String strPricingClass,
			HashMap<String, Vector<String>> hmUOMFactors, boolean fromVanstock, HashMap<String, HHInventryQTDO> hmInventory)
	{
		synchronized(MyApplication.MyLock)
		{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery  = "";
			if(barCode == null)
				strQuery  = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.Category like '"+catgId+"%' AND tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')";
			else
				strQuery  = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.UnitBarCode = '"+barCode+"' AND tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')";
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString();
					if(data[i][2].value != null)
						productsDO.Description = data[i][2].value.toString();
					if(data[i][3].value != null)
						productsDO.Description1 =  data[i][3].value.toString();
					if(data[i][32].value != null)
						productsDO.CategoryId =data[i][32].value.toString();
					if(data[i][18].value != null)
						productsDO.BatchCode = data[i][18].value.toString();
					if(data[i][11].value != null)
						productsDO.UOM =  data[i][11].value.toString();

					productsDO.primaryUOM = productsDO.UOM;

					if(data[i][13].value != null)
						productsDO.CaseBarCode = data[i][13].value.toString();
					if(data[i][14].value != null)
						productsDO.UnitBarCode =data[i][14].value.toString();
					if(data[i][15].value != null)
						productsDO.ItemType =  data[i][15].value.toString();
					if(data[i][17].value != null)
						productsDO.PricingKey = data[i][17].value.toString();
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();

					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();

					if(data[i][30].value != null)
						productsDO.LOT_CONTROL_CODE = data[i][30].value.toString();

					productsDO.preCases = "";
					productsDO.preUnits = "";

					if(hmUOMFactors.containsKey(productsDO.SKU))
						productsDO.vecUOM = hmUOMFactors.get(productsDO.SKU);
					else if(productsDO.CategoryId != null && productsDO.CategoryId.contains(AppConstants.TUB))
						productsDO.vecUOM = hmUOMFactors.get(AppConstants.ITEM_CODE);

					if(productsDO.vecUOM == null || productsDO.vecUOM.size() <= 0)
					{
						productsDO.vecUOM = new Vector<String>();
						productsDO.vecUOM.add(productsDO.UOM);
					}
					else
					{
						if(!productsDO.vecUOM.contains(productsDO.UOM))
							productsDO.UOM = productsDO.vecUOM.get(0);
					}

					if(fromVanstock)
					{
						if( hmInventory != null && (hmInventory.containsKey(productsDO.SKU+productsDO.UOM) || hmInventory.containsKey(productsDO.SKU)))
							vector.add(productsDO);
					}
					else
						vector.add(productsDO);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return vector;
		}
	}


	public Vector<ProductDO> getProductsDetailsByCategoryId_FOC(String catgId, String strPricingClass,
			HashMap<String, ProductDO> hmSelectedItems, int orderType, HashMap<String, HHInventryQTDO> hmInventory)
	{
		synchronized(MyApplication.MyLock) 
		{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery  = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.Category like '"+catgId+"%' and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')  AND tp.ItemCode IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0)";
			
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.Description = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.Description1 =  data[i][3].value.toString(); 
					if(data[i][32].value != null)
						productsDO.CategoryId =data[i][32].value.toString(); 
					if(data[i][18].value != null)
						productsDO.BatchCode = data[i][18].value.toString();
					if(data[i][11].value != null)
						productsDO.UOM =  data[i][11].value.toString();
					
					productsDO.primaryUOM = productsDO.UOM;
					
					if(data[i][13].value != null)
						productsDO.CaseBarCode = data[i][13].value.toString();
					if(data[i][14].value != null)
						productsDO.UnitBarCode =data[i][14].value.toString();
					if(data[i][15].value != null)
						productsDO.ItemType =  data[i][15].value.toString();
					if(data[i][17].value != null)
						productsDO.PricingKey = data[i][17].value.toString();
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();
					
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();
					
					if(data[i][30].value != null)
						productsDO.LOT_CONTROL_CODE = data[i][30].value.toString();
					
					productsDO.preCases = "";
					productsDO.preUnits = "";
					
					if(hmSelectedItems != null && hmSelectedItems.containsKey(productsDO.SKU))
						productsDO = hmSelectedItems.get(productsDO.SKU);
					
					productsDO.vecUOM = getPricingUOM(productsDO.CategoryId, productsDO.SKU, strPricingClass, orderType);
					
					if(productsDO.vecUOM != null && productsDO.vecUOM.size() > 0)
						productsDO.UOM = productsDO.vecUOM.get(0);
						
					if(hmInventory != null && (hmInventory.containsKey(productsDO.SKU) || hmInventory.containsKey(productsDO.SKU+productsDO.UOM)))
						vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
		}
	}
	public Vector<String> getPricingUOMForVanload(String category, String itemcode, String pricing, int orderType, 
			HashMap<String, Vector<String>> hmAddedItems)
	{
		synchronized(MyApplication.MyLock) 
		{
		Vector<String> vector = new Vector<String>();
		try
		{
			SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
			
			String strQuery = "SELECT UOM FROM tblUOMFactor WHERE ITEMCODE = '"+itemcode+"'";
			
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c.moveToFirst())
			{
				do
				{
					String UOM = c.getString(0);
					
					if(hmAddedItems != null && hmAddedItems.containsKey(itemcode))
					{
						Vector<String> vecUOM = hmAddedItems.get(itemcode);
						if(!vecUOM.contains(UOM))
							vector.add(UOM);
					}
					else
						vector.add(UOM);
				}
				while(c.moveToNext());
			}
			else if(category != null && category.contains(AppConstants.TUB))
				vector = getPricingUOMForVanload("", AppConstants.ITEM_CODE, pricing, orderType, hmAddedItems);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
		}
	}
	
	public Vector<String> getPricingUOM(String category, String itemcode, String pricing, int orderType)
	{
		synchronized(MyApplication.MyLock) 
		{
		Vector<String> vector = new Vector<String>();
		try
		{
			SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
			
			String strQuery ="";
			
			if(orderType == AppStatus.REPLACEMENT_ORDER_TYPE)
				strQuery = "SELECT UOM FROM tblUOMFactor WHERE ITEMCODE = '"+itemcode+"'";
			else
				strQuery = "SELECT UOM FROM tblPricing WHERE ITEMCODE = '"+itemcode+"' AND CUSTOMERPRICINGKEY ='"+pricing+"'";
			
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c.moveToFirst())
			{
				do
				{
					vector.add(c.getString(0));
				}
				while(c.moveToNext());
			}
			else if(category != null && category.contains(AppConstants.TUB))
				vector = getPricingUOM("", AppConstants.ITEM_CODE, pricing, orderType);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
		}
	}
	
	public Vector<ProductDO> getProductsDetailsByCategoryId_Return(String catgId, String sku, String desc,String orderedItemsList, String strPricingClass)
	{
		synchronized(MyApplication.MyLock) 
		{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery = "SELECT * FROM tblProducts where CategoryId like '"+catgId+"%' and SKU like '"+sku+"%' and Description like '"+desc+"%' and SKU NOT IN ("+orderedItemsList+") and SKU IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"')";
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.CategoryId = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.Description =  data[i][3].value.toString(); 
					if(data[i][4].value != null)
						productsDO.UnitsPerCases = StringUtils.getInt(data[i][4].value.toString()); 
					if(data[i][5].value != null)
						productsDO.BatchCode = data[i][5].value.toString();
					if(data[i][6].value != null)
						productsDO.UOM =  data[i][6].value.toString();
					if(data[i][7].value != null)
						productsDO.CaseBarCode = data[i][7].value.toString();
					if(data[i][8].value != null)
						productsDO.UnitBarCode =data[i][8].value.toString();
					if(data[i][9].value != null)
						productsDO.ItemType =  data[i][9].value.toString();
					if(data[i][10].value != null)
						productsDO.PricingKey = data[i][10].value.toString();
					if(data[i][11].value != null)
						productsDO.brand = data[i][11].value.toString();
					if(data[i][12].value != null)
						productsDO.secondaryUOM = data[i][12].value.toString();
					
					productsDO.preCases = "";
					productsDO.preUnits = "";
					
					productsDO.totalCases = StringUtils.getFloat(productsDO.preCases) + StringUtils.getInt(productsDO.preUnits)/productsDO.UnitsPerCases;
					
					vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
			return vector;
		}
	}
	
	
	public Vector<VanLoadDO> getProductsVanByCategoryId(String catgId, int load_type, HashMap<String, Vector<String>> hmAddedItems)
	{
		synchronized(MyApplication.MyLock) 
		{
		VanLoadDO productsDO;
		Vector<VanLoadDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			
			String strQuery = "";
			
			if(load_type == AppStatus.UNLOAD_STOCK)
				strQuery = "SELECT * FROM tblProducts where Category like '"+catgId+"%' AND ItemCode IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0)";
			else
				strQuery = "SELECT * FROM tblProducts where Category like '"+catgId+"%'";
			
			LogUtils.errorLog("Awa--strQuery :", strQuery);
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<VanLoadDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new VanLoadDO();
					productsDO.id = StringUtils.getUniqueUUID();
					
					if(data[i][1].value != null)
						productsDO.ItemCode = data[i][1].value.toString(); 
					
					if(data[i][9].value != null)
						productsDO.CategoryId = data[i][9].value.toString(); 
					if(data[i][2].value != null)
						productsDO.Description =  data[i][2].value.toString(); 
					if(data[i][16].value != null)
						productsDO.UnitsPerCases = StringUtils.getInt(data[i][16].value.toString()); 
					if(data[i][6].value != null)
						productsDO.UOM =  data[i][11].value.toString();
					if(data[i][10].value != null)
						productsDO.customerPriceClass = data[i][10].value.toString();
					
					if(data[i][9].value != null)
						productsDO.itemType = data[i][15].value.toString();
					
					productsDO.SellableQuantity = 0;
					productsDO.TotalQuantity = 0;
					
					productsDO.vecUOM = getPricingUOMForVanload(productsDO.CategoryId, productsDO.ItemCode, "", AppStatus.REPLACEMENT_ORDER_TYPE, hmAddedItems);
						
					if(productsDO.vecUOM != null && productsDO.vecUOM.size() > 0)
					{
						if(!productsDO.vecUOM.contains(productsDO.UOM))
							productsDO.UOM = productsDO.vecUOM.get(0);
						
						vector.add(productsDO);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return vector;
		}
	}
	
	
	public VanLoadDO getProductsVanByBarCode(String strBarCode, int load_type)
	{
		synchronized(MyApplication.MyLock) 
		{
		VanLoadDO productsDO = null;
		try
		{
			DictionaryEntry [][] data = null;
			
			String strQuery = "";
			
			if(load_type == AppStatus.UNLOAD_STOCK)
				strQuery = "SELECT * FROM tblProducts where UnitBarCode like '"+strBarCode+"%' AND ItemCode IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0)";
			else
				strQuery = "SELECT * FROM tblProducts where UnitBarCode like '"+strBarCode+"%'";
			
			LogUtils.errorLog("Awa--strQuery :", strQuery);
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				productsDO = new VanLoadDO();
				if(data[0][1].value != null)
					productsDO.ItemCode = data[0][1].value.toString(); 
				
				if(data[0][9].value != null)
					productsDO.CategoryId = data[0][9].value.toString(); 
				if(data[0][2].value != null)
					productsDO.Description =  data[0][2].value.toString(); 
				if(data[0][16].value != null)
					productsDO.UnitsPerCases = StringUtils.getInt(data[0][16].value.toString()); 
				if(data[0][6].value != null)
					productsDO.UOM =  data[0][11].value.toString();
				if(data[0][10].value != null)
					productsDO.customerPriceClass = data[0][10].value.toString();
				
				if(data[0][9].value != null)
					productsDO.itemType = data[0][15].value.toString();
				
				productsDO.SellableQuantity = 0;
				productsDO.TotalQuantity = 0;
				
				productsDO.vecUOM = getPricingUOM(productsDO.CategoryId, productsDO.ItemCode, "", AppStatus.REPLACEMENT_ORDER_TYPE);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return productsDO;
		}
	}
	public Vector<VanLoadDO> getProductsUnload(int load_type, DecimalFormat diffQTY, DecimalFormat diffStock)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase sqLiteDatabase = null;
		Cursor cursor = null;
		Vector<VanLoadDO> vector = null;
		try
		{
			String strQuery = "";
			sqLiteDatabase = DatabaseHelper.openDataBase();
			String movementType = "2";
			if(load_type == AppStatus.UNLOAD_S_STOCK){
				movementType = "3";
				strQuery="SELECT tvm.ItemCode,(tvm.SellableQuantity+tvm.ReturnedQuantity), " +
						"TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason,TF.Factor  FROM tblProducts TP " +
						"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND " +
						"TF.UOM = TP.UOM WHERE SellableQuantity+ReturnedQuantity > 0";	
				
//				strQuery = "SELECT tvm.ItemCode,(tvm.SellableQuantity-( Select Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
//						"INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode AND  THD.MovementType='3'  Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' ) AND TMD.ItemCode= tvm.ItemCode  GROUP BY TMD .ItemCode) )/ TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason  FROM tblProducts TP " +
//						"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE SellableQuantity > 0";

//				strQuery = "SELECT tvm.ItemCode,tvm.SellableQuantity / TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason FROM tblProducts TP INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode "+
//						"INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE SellableQuantity > 0";
			}
			else
				strQuery = "SELECT tvm.ItemCode,(tvm.ReceivedQty-tvm.UnloadedQty), TP.Description, TP.Category,TP.UOM, "
						+ "TP.ItemType,tvm.ExpiryDate,tvm.Reason,TF.Factor " +
						   "FROM tblProducts TP INNER JOIN tblNonSellableItems tvm on tvm.ItemCode = TP.ItemCode " +
						   "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE (tvm.ReceivedQty - tvm.UnloadedQty)> 0";
			
//				strQuery = "SELECT tvm.ItemCode,(tvm.ReceivedQty - tvm.UnloadedQty)/ TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,tvm.ExpiryDate,tvm.Reason " +
//						   "FROM tblProducts TP INNER JOIN tblNonSellableItems tvm on tvm.ItemCode = TP.ItemCode " +
//						   "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE (tvm.ReceivedQty - tvm.UnloadedQty)> 0";
			
			LogUtils.errorLog("Awa--strQuery :", strQuery);
			
			String query = "Select TMD.ItemCode,Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
						   "INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode "+
						   "AND  THD.MovementType='"+movementType+"'  "+
						   "Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  "+
						   "where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR "
						   + "TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' )  GROUP BY TMD .ItemCode";
			cursor	=	sqLiteDatabase.rawQuery(query, null);
			HashMap<String, Double> hmItemPending = new HashMap<String, Double>();
			if(cursor.moveToFirst())
			{
				do{
					hmItemPending.put(cursor.getString(0),cursor.getDouble(1));
				}
				while (cursor.moveToNext());
			}
			
			if(cursor != null && !cursor.isClosed())
				cursor.close();
			
			
			cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
			if(cursor.moveToFirst())
			{
				vector = new Vector<VanLoadDO>();
				do
				{
					VanLoadDO productsDO 		=  	new VanLoadDO();
					productsDO.ItemCode 		=  	cursor.getString(0); 
					productsDO.SellableQuantity =  	StringUtils.getFloat(diffStock.format(cursor.getFloat(1))); 
					double conversion 			= 	cursor.getDouble(8); 
					
					if(hmItemPending.containsKey(productsDO.ItemCode)){
						
						double unloadQty = hmItemPending.get(productsDO.ItemCode);
						
						if(unloadQty>0){
							
							if(productsDO.SellableQuantity>unloadQty){
								productsDO.SellableQuantity  = (float) (productsDO.SellableQuantity -unloadQty);
								unloadQty = 0;
							}
							else{
								unloadQty = unloadQty-productsDO.SellableQuantity;
								productsDO.SellableQuantity = 0;
							}
							
							hmItemPending.put(productsDO.ItemCode, unloadQty);
						}
					}
					
					if(conversion>1)
						productsDO.SellableQuantity	= (float) (productsDO.SellableQuantity/conversion);
					productsDO.Description 		=  	cursor.getString(2); 
					productsDO.CategoryId 		= 	cursor.getString(3); 
					productsDO.UnitsPerCases 	= 	1; 
					productsDO.UOM 				=  	cursor.getString(4); 
					productsDO.itemType 		= 	cursor.getString(5); 
					
					productsDO.ExpiryDate 		= 	cursor.getString(6); 
					productsDO.reason 			= 	cursor.getString(7); 
					
					productsDO.vecUOM 			= 	getPricingUOM(productsDO.CategoryId, productsDO.ItemCode, "", AppStatus.REPLACEMENT_ORDER_TYPE);
					productsDO.TotalQuantity = 0;
					
					if(StringUtils.getFloat(diffQTY.format(productsDO.SellableQuantity)) > 0)
						vector.add(productsDO);
				}
				while (cursor.moveToNext());
			}
			
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null && !cursor.isClosed())
				cursor.close();
			
			if(sqLiteDatabase != null)
				sqLiteDatabase.close();
		}
		return vector;
		}
	}
	
	public Vector<VanLoadDO> getProductsUnload(int load_type, DecimalFormat diffQTY, DecimalFormat diffStock,int precision)
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase sqLiteDatabase = null;
		Cursor cursor = null;
		Vector<VanLoadDO> vector = null;
		try
		{
			String strQuery = "";
			sqLiteDatabase = DatabaseHelper.openDataBase();
			String movementType = "2";
			if(load_type == AppStatus.UNLOAD_S_STOCK){
				movementType = "3";
				boolean eanbleReturn =new SettingsDA().getSettingsValue(AppConstants.EnableReturnQty,sqLiteDatabase);
				
				if(eanbleReturn){
					
					strQuery="SELECT tvm.ItemCode,(tvm.SellableQuantity+tvm.ReturnedQuantity), " +
							"TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason,TF.Factor  FROM tblProducts TP " +
							"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND " +
							"TF.UOM = TP.UOM WHERE SellableQuantity+ReturnedQuantity > 0";	
				}else{
					
					strQuery="SELECT tvm.ItemCode,(tvm.SellableQuantity), " +
							"TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason,TF.Factor  FROM tblProducts TP " +
							"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND " +
							"TF.UOM = TP.UOM WHERE SellableQuantity> 0";	
				}
				
//				strQuery = "SELECT tvm.ItemCode,(tvm.SellableQuantity-( Select Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
//						"INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode AND  THD.MovementType='3'  Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' ) AND TMD.ItemCode= tvm.ItemCode  GROUP BY TMD .ItemCode) )/ TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason  FROM tblProducts TP " +
//						"INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE SellableQuantity > 0";

//				strQuery = "SELECT tvm.ItemCode,tvm.SellableQuantity / TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,'' ExpiryDate, '' AS Reason FROM tblProducts TP INNER JOIN tblVanStock tvm on tvm.ItemCode = TP.ItemCode "+
//						"INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE SellableQuantity > 0";
			}
			else
				strQuery = "SELECT tvm.ItemCode,(tvm.ReceivedQty-tvm.UnloadedQty), TP.Description, TP.Category,TP.UOM, "
						+ "TP.ItemType,tvm.ExpiryDate,tvm.Reason,TF.Factor " +
						   "FROM tblProducts TP INNER JOIN tblNonSellableItems tvm on tvm.ItemCode = TP.ItemCode " +
						   "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE (tvm.ReceivedQty - tvm.UnloadedQty)> 0";
			
//				strQuery = "SELECT tvm.ItemCode,(tvm.ReceivedQty - tvm.UnloadedQty)/ TF.Factor, TP.Description, TP.Category,TP.UOM, TP.ItemType,tvm.ExpiryDate,tvm.Reason " +
//						   "FROM tblProducts TP INNER JOIN tblNonSellableItems tvm on tvm.ItemCode = TP.ItemCode " +
//						   "INNER JOIN tblUOMFactor TF ON TF.ItemCode = TP.ItemCode AND TF.UOM = TP.UOM WHERE (tvm.ReceivedQty - tvm.UnloadedQty)> 0";
			
			LogUtils.errorLog("Awa--strQuery :", strQuery);
	//Opened for unload nonsaleable	start
		/*	String query = "Select TMD.ItemCode,Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
						   "INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode "+
						   "AND  THD.MovementType='"+movementType+"'  "+
						   "Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  "+
						   "where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR "
						   + "TMD.MovementStatus = '99' OR TMD.MovementStatus = '101' )  GROUP BY TMD .ItemCode";
			*/
			String query = "Select TMD.ItemCode,Sum(TMD.QuantityLevel1*TUF .Factor) FROM  tblMovementDetail TMD  " +
					"INNER JOIN tblMovementHeader  THD ON  TMD.MovementCode =THD.MovementCode "+
					"AND  THD.MovementType='"+movementType+"'  "+
					"Inner Join tblUOMFactor TUF ON TUF.UOM = TMD.UOM  AND  TUF.ItemCode = TMD.ItemCode  "+
					"where  (TMD.MovementStatus = '1'  OR TMD.MovementStatus = 'Pending' OR "
					+ "THD.MovementStatus = '99' OR THD.MovementStatus = '101' )  GROUP BY TMD .ItemCode";
			cursor	=	sqLiteDatabase.rawQuery(query, null);
			HashMap<String, Double> hmItemPending = new HashMap<String, Double>();
			if(cursor.moveToFirst())
			{
				do{
					hmItemPending.put(cursor.getString(0),cursor.getDouble(1));
				}
				while (cursor.moveToNext());
			}
			
			if(cursor != null && !cursor.isClosed())
				cursor.close();
			
			//Opened for unload nonsaleable	end	
			cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
			if(cursor.moveToFirst())
			{
				vector = new Vector<VanLoadDO>();
				do
				{
					VanLoadDO productsDO 		=  	new VanLoadDO();
					productsDO.ItemCode 		=  	cursor.getString(0); 
					//productsDO.SellableQuantity =  	StringUtils.getFloat(diffStock.format(cursor.getFloat(1))); 
					productsDO.SellableQuantity =  	(float) StringUtils.round(cursor.getString(1), precision);//(diffStock.format(cursor.getFloat(1))); 
					double conversion 			= 	cursor.getDouble(8); 
					//Opened for unload nonsaleable	start		
					if(hmItemPending.containsKey(productsDO.ItemCode)){
						
						double unloadQty = hmItemPending.get(productsDO.ItemCode);
						
						if(unloadQty>0){
							
							if(productsDO.SellableQuantity>unloadQty){
								productsDO.SellableQuantity  = (float) StringUtils.round((productsDO.SellableQuantity -unloadQty)+"",precision);
								//productsDO.SellableQuantity  = (float) (productsDO.SellableQuantity -unloadQty);
								unloadQty = 0;
							}
							else{
								unloadQty = unloadQty-productsDO.SellableQuantity;
								productsDO.SellableQuantity = 0;
							}
							
							hmItemPending.put(productsDO.ItemCode, unloadQty);
						}
					}
				//Opened for unload nonsaleable	end
					if(conversion>1)
						productsDO.SellableQuantity	= (float) StringUtils.round((productsDO.SellableQuantity/conversion)+"",precision);
						//productsDO.SellableQuantity	= (float) (productsDO.SellableQuantity/conversion);
					productsDO.Description 		=  	cursor.getString(2); 
					productsDO.CategoryId 		= 	cursor.getString(3); 
					productsDO.UnitsPerCases 	= 	1; 
					productsDO.UOM 				=  	cursor.getString(4); 
					productsDO.itemType 		= 	cursor.getString(5); 
					
					productsDO.ExpiryDate 		= 	cursor.getString(6); 
					productsDO.reason 			= 	cursor.getString(7); 
					
					productsDO.vecUOM 			= 	getPricingUOM(productsDO.CategoryId, productsDO.ItemCode, "", AppStatus.REPLACEMENT_ORDER_TYPE);
					productsDO.TotalQuantity = 0;
					
					if(StringUtils.getFloat(diffQTY.format(productsDO.SellableQuantity)) > 0)
						vector.add(productsDO);
				}
				while (cursor.moveToNext());
			}
			
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null && !cursor.isClosed())
				cursor.close();
			
			if(sqLiteDatabase != null)
				sqLiteDatabase.close();
		}
		return vector;
		}
	}
	
	public boolean checkUnload(boolean isSalable)
	{
		synchronized(MyApplication.MyLock) 
		{
		boolean isAvailable = false;
		SQLiteDatabase objSqliteDB =null;
		try
		{
			String strQuery = "";
			Cursor cursor = null;
			
			objSqliteDB = DatabaseHelper.openDataBase();
			
			if(isSalable)
				strQuery = "SELECT  COUNT(*) FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE IFNULL(IsSellableUnload ,0)=1)";
			else
				strQuery = "SELECT  COUNT(*) FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE IFNULL(IsNonSellableUnload ,0)=1)";
				
			cursor = objSqliteDB.rawQuery(strQuery, null);
			
			if(cursor.moveToFirst())
			{
				if(cursor.getInt(0) >  0)
					isAvailable = true;
				else
					isAvailable = false;
			}
			cursor.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return isAvailable;
		}
	}
	
	public void updateUnlodStatus(ArrayList<VanLoadDO> vecOrdProduct)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate;
				 stmtUpdate = objSqliteDB.compileStatement("Update tblVanStock set IsNonSellableUnload = '0', IsSellableUnload = '0' WHERE ItemCode = ?");
				 
				 for (VanLoadDO vanLoadDO : vecOrdProduct) 
				 {
					 stmtUpdate.bindString(1, vanLoadDO.ItemCode);
					 stmtUpdate.execute();
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
		}
	}
}
