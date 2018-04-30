package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.TrxHeaderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class CaptureInventryDA 
{
	public static final int LAST_ORDER = 2;
	public static final int MODIFY = 4;
	public static final int DELIVERY = 3;
	public static final int CONFIRM_HOLD_ORDER = 5;
	
	//Need to change
	public HashMap<String, Vector<ProductDO>> getOrder_LPO_Detail(String orderId, HashMap<String, HHInventryQTDO> hmInventory, int TYPE, String priceClass, String orderType, HashMap<String, Float> hmConversion)
	{
		//
		String strPreviousOrder = "Select distinct P.ProductId, P.ItemCode, P.Description,P.Category, P.Brand,OD.UOM,P.SecondaryUOM,P.CaseBarCode,P.UnitBarCode,"+
								  "P.ItemType,P.UnitPerCase,P.PricingKey,TC.CategoryName, OD.Cases,OD.Units,OD.ItemType from tblProducts P, tblCategory TC, tblOrderDetail OD" +
								  " where P.Category<>'' and P.ItemCode=OD.ItemCode and TC.CategoryId=P.Category and OD.OrderNo = '%s'";
		
		String lastOrderQry 	= String.format(strPreviousOrder, orderId);

		SQLiteDatabase mDatabase = null;
		Cursor cursor =null;
		HashMap<String, Vector<ProductDO>> hmProducts =null;
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			cursor     = mDatabase.rawQuery(lastOrderQry, null);
			hmProducts = parseCursor(cursor, TYPE, hmInventory, priceClass, orderType, hmConversion);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return hmProducts;
	}
	
	//===========
	public HashMap<String, Vector<ProductDO>> getOrder_LPO_Detail1(String orderId, String orderType, HashMap<String, Float> hmConversion)
	{
		//
		String strPreviousOrder = "Select distinct P.ProductId, P.ItemCode, P.Description,P.Category, P.Brand,OD.UOM,P.SecondaryUOM,P.CaseBarCode,P.UnitBarCode,"+
								  "P.ItemType,P.UnitPerCase,P.PricingKey,TC.CategoryName, OD.Cases,OD.Units,OD.ItemType, "+
								  "OD.ItemPrice, OD.UnitSellingPrice, OD.PriceUsedLevel2, OD.PriceUsedLevel2, OD.TotalDiscountAmount from tblProducts P, tblCategory TC, tblOrderDetail OD" +
								  " where P.Category<>'' and P.ItemCode=OD.ItemCode and TC.CategoryId=P.Category and OD.OrderNo = '%s'";
		
		String lastOrderQry 	= String.format(strPreviousOrder, orderId);

		SQLiteDatabase mDatabase = null;
		Cursor cursor =null;
		HashMap<String, Vector<ProductDO>> hmProducts =null;
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			cursor     = mDatabase.rawQuery(lastOrderQry, null);
			hmProducts = parseCursorNew(cursor, orderType, hmConversion);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return hmProducts;
	}
	
	public HashMap<String, Vector<ProductDO>> parseCursorNew(Cursor cursor, String orderType, HashMap<String, Float> hmConversion)
	{
		HashMap<String, Vector<ProductDO>> hmProducts = null;
		try
		{
			if(cursor.moveToFirst())
			{
				hmProducts = new HashMap<String, Vector<ProductDO>>();
				do
				{
					ProductDO objProduct 	= new ProductDO();
					objProduct.ProductId 	= cursor.getString(cursor.getColumnIndex("ProductId"));
					objProduct.SKU 			= cursor.getString(cursor.getColumnIndex("ItemCode"));
					objProduct.CategoryId 	= cursor.getString(cursor.getColumnIndex("CategoryName"));
					
					//If product not contains categoryId, Inserting 'Others' as categoryId
					if(objProduct.CategoryId == null || objProduct.CategoryId.equalsIgnoreCase(""))
						objProduct.CategoryId = "Others";
					
					objProduct.Description = cursor.getString(cursor.getColumnIndex("Description"));
					objProduct.UnitsPerCases = cursor.getInt(cursor.getColumnIndex("UnitPerCase"));
					objProduct.BatchCode = cursor.getString(cursor.getColumnIndex("UnitBarCode"));
					objProduct.UOM = cursor.getString(cursor.getColumnIndex("UOM"));
					objProduct.CaseBarCode = cursor.getString(cursor.getColumnIndex("CaseBarCode"));
					objProduct.UnitBarCode = cursor.getString(cursor.getColumnIndex("UnitBarCode"));
					objProduct.ItemType = cursor.getString(cursor.getColumnIndex("ItemType"));
					objProduct.ItemSubType = cursor.getString(cursor.getColumnIndex("ItemType"));
					objProduct.PricingKey = cursor.getString(cursor.getColumnIndex("PricingKey"));
					objProduct.brand = cursor.getString(cursor.getColumnIndex("Brand"));
					objProduct.secondaryUOM = cursor.getString(cursor.getColumnIndex("SecondaryUOM"));
					
					objProduct.TaxGroupCode = "0";
					objProduct.TaxPercentage = 0;
					
					objProduct.OrderType = orderType;
						
						
					objProduct.cases 		= cursor.getString(cursor.getColumnIndex("Cases"));
					objProduct.preCases 	= cursor.getString(cursor.getColumnIndex("Cases"));
					objProduct.preUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
					objProduct.ActpreUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
					objProduct.recomUnits 	= 0;
					objProduct.recomCases 	= "0";
					
					objProduct.lpoOrderedUnit = ""+objProduct.preUnits;
//					float factor   = getUOMFactor(hmConversion, objProduct);
//					float availQty = hmInventory.get(objProduct.SKU + objProduct.UOM).totalQt;
//					if(StringUtils.getFloat(objProduct.preUnits) > availQty )
//					{
//						objProduct.totalCases = availQty;
//						objProduct.preCases =  String.valueOf(objProduct.totalCases);
//						objProduct.preUnits = ""+availQty;
//					}
					
						
					objProduct.isMusthave 	= false;
					objProduct.isAdvanceOrder = false;
					objProduct.Discount 	= cursor.getFloat(cursor.getColumnIndex("TotalDiscountAmount"));
					objProduct.DiscountAmt 	= cursor.getFloat(cursor.getColumnIndex("TotalDiscountAmount"))/StringUtils.getFloat(objProduct.preUnits);
					objProduct.totalPrice   =  0.0;
					objProduct.invoiceAmount = 0.0;
					objProduct.totalCases	= StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits) / objProduct.UnitsPerCases;
					
					objProduct.quantityBU = getQuantityBU(objProduct, hmConversion);
					
					objProduct.unitSellingPrice = cursor.getFloat(cursor.getColumnIndex("UnitSellingPrice"));
					objProduct.itemPrice    	= cursor.getFloat(cursor.getColumnIndex("ItemPrice"));
					
//					objProduct.TaxPercentage	= cursor.getFloat(cursor.getColumnIndex("TotalDiscountAmount"));
					objProduct.invoiceAmount 	= objProduct.unitSellingPrice * StringUtils.getDouble(objProduct.preUnits);
					
					String key = "";
					if(objProduct.isPromotional)
						key = AppStatus.FOC_ITEM_TYPE;
					else
						key = objProduct.CategoryId;
					
					Vector<ProductDO> vecProducts = hmProducts.get(key);
					
					if(vecProducts == null)
					{
						vecProducts = new Vector<ProductDO>(); 
						vecProducts.add(objProduct);
						hmProducts.put(key, vecProducts);
					}
					else
					{
						vecProducts.add(objProduct);
						hmProducts.put(key, vecProducts);
					}
				}while(cursor.moveToNext());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return hmProducts;
	}
	
	///////////////
	public HashMap<String, Vector<ProductDO>> parseCursor(Cursor cursor, int itemsType, HashMap<String, HHInventryQTDO> hmInventory, String strPriceClass, String orderType, HashMap<String, Float> hmConversion)
	{
		HashMap<String, Vector<ProductDO>> hmProducts = null;
		try
		{
			if(cursor.moveToFirst())
			{
				hmProducts = new HashMap<String, Vector<ProductDO>>();
				do
				{
					ProductDO objProduct 	= new ProductDO();
					objProduct.ProductId 	= cursor.getString(cursor.getColumnIndex("ProductId"));
					objProduct.SKU 			= cursor.getString(cursor.getColumnIndex("ItemCode"));
					objProduct.CategoryId 	= cursor.getString(cursor.getColumnIndex("CategoryName"));
					
					//If product not contains categoryId, Inserting 'Others' as categoryId
					if(objProduct.CategoryId == null || objProduct.CategoryId.equalsIgnoreCase(""))
						objProduct.CategoryId = "Others";
					
					objProduct.Description = cursor.getString(cursor.getColumnIndex("Description"));
					objProduct.UnitsPerCases = cursor.getInt(cursor.getColumnIndex("UnitPerCase"));
					objProduct.BatchCode = cursor.getString(cursor.getColumnIndex("UnitBarCode"));
					objProduct.UOM = cursor.getString(cursor.getColumnIndex("UOM"));
					objProduct.CaseBarCode = cursor.getString(cursor.getColumnIndex("CaseBarCode"));
					objProduct.UnitBarCode = cursor.getString(cursor.getColumnIndex("UnitBarCode"));
					objProduct.ItemType = cursor.getString(cursor.getColumnIndex("ItemType"));
					objProduct.ItemSubType = cursor.getString(cursor.getColumnIndex("ItemType"));
					objProduct.PricingKey = cursor.getString(cursor.getColumnIndex("PricingKey"));
					objProduct.brand = cursor.getString(cursor.getColumnIndex("Brand"));
					objProduct.secondaryUOM = cursor.getString(cursor.getColumnIndex("SecondaryUOM"));
					
					objProduct.TaxGroupCode = "0";
					objProduct.TaxPercentage = 0;
					
					objProduct.OrderType = orderType;
					
					switch (itemsType) 
					{
					case MODIFY:
						
						objProduct.cases 		= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preCases 	= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						objProduct.ActpreUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						objProduct.recomUnits 	= 0;
						objProduct.recomCases 	= "0";
						objProduct.totalCases	= StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits) / objProduct.UnitsPerCases;
						
						HHInventryQTDO hhInventryQTDO = hmInventory.get(objProduct.SKU + objProduct.UOM);
						hhInventryQTDO.totalQt = hhInventryQTDO.totalQt + StringUtils.getFloat(objProduct.ActpreUnits);
							
						objProduct.isMusthave 	= false;
						objProduct.isAdvanceOrder = false;
						objProduct.Discount 	=  0;
						objProduct.DiscountAmt 	=  0;
						objProduct.totalPrice =  0.0;
						objProduct.invoiceAmount = 0.0;
						
						if(StringUtils.getFloat(objProduct.preUnits) != 0)
						{
							objProduct.unitSellingPrice = objProduct.invoiceAmount / objProduct.totalCases;
							objProduct.itemPrice    	= objProduct.totalPrice / objProduct.totalCases;
						}
						else
						{
							objProduct.unitSellingPrice = 0;
							objProduct.itemPrice    = 0;
						}
						break;
						
					case DELIVERY:
						
						objProduct.cases 		= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preCases 	= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						objProduct.ActpreUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						objProduct.recomUnits 	= 0;
						objProduct.recomCases 	= "0";
						
						
						float factor   = getUOMFactor(hmConversion, objProduct);
						float availQty = hmInventory.get(objProduct.SKU + objProduct.UOM).totalQt;
						if(StringUtils.getFloat(objProduct.preUnits) > availQty )
						{
							objProduct.totalCases = availQty;
							objProduct.preCases =  String.valueOf(objProduct.totalCases);
							objProduct.preUnits = ""+availQty;
						}
							
						objProduct.isMusthave 	= false;
						objProduct.isAdvanceOrder = false;
						objProduct.Discount 	=  0;
						objProduct.DiscountAmt 	=  0;
						objProduct.totalPrice =  0.0;
						objProduct.invoiceAmount = 0.0;
						objProduct.totalCases	= StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits) / objProduct.UnitsPerCases;
						
						if(StringUtils.getFloat(objProduct.preUnits) != 0)
						{
							objProduct.unitSellingPrice = objProduct.invoiceAmount / objProduct.totalCases;
							objProduct.itemPrice    	= objProduct.totalPrice / objProduct.totalCases;
						}
						else
						{
							objProduct.unitSellingPrice = 0;
							objProduct.itemPrice    = 0;
						}
						break;
						
						
					case CONFIRM_HOLD_ORDER:
						
						objProduct.cases 		= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preCases 	= cursor.getString(cursor.getColumnIndex("Cases"));
						objProduct.preUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						objProduct.ActpreUnits 	= cursor.getString(cursor.getColumnIndex("Units"));
						
						String ItemType 	= cursor.getString(cursor.getColumnIndex("ItemType"));
						
						if(ItemType.equalsIgnoreCase("F"))
							objProduct.isPromotional = true;
						
						objProduct.recomUnits 	= 0;
						objProduct.recomCases 	= "0";
						objProduct.isMusthave 	= false;
						objProduct.isAdvanceOrder = false;
						objProduct.Discount 	=  0;
						objProduct.DiscountAmt 	=  0;
						objProduct.totalPrice =  0.0;
						objProduct.invoiceAmount = 0.0;
						
						if(StringUtils.getFloat(objProduct.preUnits) != 0 && !objProduct.isPromotional)
						{
							objProduct.unitSellingPrice = objProduct.invoiceAmount / objProduct.totalCases;
							objProduct.itemPrice    	= objProduct.totalPrice / objProduct.totalCases;
						}
						else
						{
							objProduct.unitSellingPrice = 0;
							objProduct.itemPrice    = 0;
						}
						break;
						
					case LAST_ORDER:
						
						objProduct.cases 		= "0";
						objProduct.orderedCases = cursor.getFloat(cursor.getColumnIndex("CasesDelivered"));
						objProduct.units 		= "0";
						objProduct.orderedUnits = cursor.getInt(cursor.getColumnIndex("UnitsDelivered"));
						objProduct.recomUnits 	=  0;
						objProduct.recomCases 	= "0";
						objProduct.totalCases	= StringUtils.getFloat(objProduct.preCases) + StringUtils.getFloat(objProduct.preUnits) / objProduct.UnitsPerCases;
						objProduct.isMusthave 	= false;
						objProduct.isAdvanceOrder = false;
						objProduct.Discount 	=  0;
						objProduct.DiscountAmt 	=  0;
						objProduct.totalPrice 	=  0.0;
						objProduct.invoiceAmount= 0.0;
						objProduct.unitSellingPrice = 0;
						objProduct.itemPrice    = 0;
						
						break;

					default:
						break;
					}
					
					String key = "";
					if(objProduct.isPromotional)
						key = AppStatus.FOC_ITEM_TYPE;
					else
						key = objProduct.CategoryId;
					
					Vector<ProductDO> vecProducts = hmProducts.get(key);
					
					if(vecProducts == null)
					{
						vecProducts = new Vector<ProductDO>(); 
						vecProducts.add(objProduct);
						hmProducts.put(key, vecProducts);
					}
					else
					{
						vecProducts.add(objProduct);
						hmProducts.put(key, vecProducts);
					}
					
					objProduct.vecUOM = getPricingUOM(objProduct.SKU, strPriceClass, null);
				}while(cursor.moveToNext());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return hmProducts;
	}
	
	
	public float getCreditLimit(String CustomersiteId)
	{
		String strCreditLimt = "Select CREDIT_LIMIT from tblCustomerSites where CustomersiteId ='"+CustomersiteId+"'";
		
		float creditLimit = 0;
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		try
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor = mDatabase.rawQuery(strCreditLimt, null);
			
			if(cursor != null && cursor.moveToFirst())
				creditLimit  = StringUtils.getFloat(cursor.getString(0));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return creditLimit;
	}
	
	public HashMap<String, DiscountDO> getPromoDisocunt(String siteNumber)
	{
		HashMap<String, DiscountDO> hmMap = new HashMap<String, DiscountDO>();
		Cursor cursor = null;
		SQLiteDatabase mDatabase = null;
		try
		{
			mDatabase 			=   DatabaseHelper.openDataBase();
			
			String strItemPrice =   "SELECT Code, Level, DiscountType, Discount, Discount_Modifier FROM tblPromoDiscountDetail "
					      + "WHERE SiteNumber = '"+siteNumber+"' ORDER BY DiscountId DESC";
			
			cursor 				= mDatabase.rawQuery(strItemPrice, null);
			if(cursor.moveToFirst())
			{
				do
				{
					DiscountDO objDiscount 		= new DiscountDO();
					objDiscount.ItemCode		= cursor.getString(0);
					objDiscount.level 			= cursor.getString(1);
					objDiscount.discountType 	= cursor.getInt(2);
					objDiscount.discount 		= cursor.getFloat(3);
					objDiscount.description 	= cursor.getString(4);
					if(hmMap.containsKey(objDiscount.ItemCode))
					{
						DiscountDO objDiscount1 = hmMap.get(objDiscount.ItemCode);
						if(objDiscount1!=null)
						{
							objDiscount.discount +=objDiscount1.discount;
						}
					}
					hmMap.put(objDiscount.ItemCode, objDiscount);
				}
				while(cursor.moveToNext());
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
			if(mDatabase != null)
				mDatabase.close();
		}
		return hmMap;
	}
	
	public DiscountDO getDisocunt(String siteNumber, String CATID, String SKU)
	{
		DiscountDO objDiscount = null;
		Cursor cursor = null;
		SQLiteDatabase mDatabase = null;
		SQLiteStatement sqLiteStatement = null;
		try
		{
			String strItemPrice =   "SELECT COUNT(*) FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"'";
			mDatabase 			=   DatabaseHelper.openDataBase();
			sqLiteStatement     =   mDatabase.compileStatement(strItemPrice);
			float discount 	  	= 0.0f;
			String strDisc ="";
			long count 			= 	sqLiteStatement.simpleQueryForLong();
			
			sqLiteStatement.close();
			
			if(count > 0)
			{
				strItemPrice =   "SELECT Level, DiscountType, Sum(Discount), Discount_Modifier FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"' AND "+
						         "Level = '1' AND CODE = '"+SKU+"' ORDER BY DiscountId DESC";
				
				cursor 				= mDatabase.rawQuery(strItemPrice, null);
				if(cursor.moveToFirst())
				{
					objDiscount 				= new DiscountDO();
					objDiscount.level 			= cursor.getString(0);
					objDiscount.discountType 	= cursor.getInt(1);
					discount					+= cursor.getFloat(2);
					objDiscount.discount 		= discount;
					objDiscount.description 	= cursor.getString(3);
					strDisc = objDiscount.description;
				}
				// Comented As Per Thaiyab Request
//				else
//				{
					if(cursor != null)
						cursor.close();
					
					strItemPrice =   "SELECT Level, DiscountType, Sum(Discount),Discount_Modifier FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"' AND "+
							         "Level = '2' AND CODE = '"+CATID+"' ORDER BY DiscountId DESC";
					
					cursor 				= mDatabase.rawQuery(strItemPrice, null);
					
					if(cursor.moveToFirst())
					{
						objDiscount 				= new DiscountDO();
						objDiscount.level 			= cursor.getString(0);
						objDiscount.discountType 	= cursor.getInt(1);
						discount					+= cursor.getFloat(2);
						objDiscount.discount 		= discount;
						objDiscount.description 	= cursor.getString(3);
						if(TextUtils.isEmpty(strDisc))
						{
							if(!TextUtils.isEmpty(objDiscount.description))
								strDisc = strDisc +", "+objDiscount.description;
							else
							objDiscount.description = strDisc;
						}
					}
					// Comented As Per Thaiyab Request
//					else
//					{
						if(cursor != null)
							cursor.close();
						
						strItemPrice =   "SELECT Level, DiscountType, Discount,Discount_Modifier FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"' AND "+
								         "Level = '0' AND CODE = 'ALL' ORDER BY DiscountId DESC";
						
						cursor 				= mDatabase.rawQuery(strItemPrice, null);
						if(cursor.moveToFirst())
						{
							objDiscount 				= new DiscountDO();
							objDiscount.level	 		= cursor.getString(0);
							objDiscount.discountType 	= cursor.getInt(1);
							discount					+= cursor.getFloat(2);
							objDiscount.discount 		= discount;
							objDiscount.description 	= cursor.getString(3);
							if(TextUtils.isEmpty(strDisc))
							{
								if(!TextUtils.isEmpty(objDiscount.description))
									strDisc = strDisc +", "+objDiscount.description;
								else
								objDiscount.description = strDisc;
							}
						}
//					}
//				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return objDiscount;
	}
	
	public int getDisocuntCount(SQLiteDatabase mDatabase, String siteNumber, String CATID, String SKU)
	{
		Cursor cursor = null;
		int Count = 0;
		try
		{
			if(mDatabase == null)
				mDatabase 	= DatabaseHelper.openDataBase();
		
			String strItemPrice =   "SELECT COUNT(*) FROM tblDiscountMaster WHERE SiteNumber = '"+siteNumber+"' AND "+
									"( "+
									"Level = '0' AND CODE = 'ALL' "+
									"OR "+
									"Level = '1' AND CODE = '"+SKU+"' "+
									"OR "+
									"Level = '2' AND CODE = '"+CATID+"' "+
									")";
			
			cursor 				= mDatabase.rawQuery(strItemPrice, null);
			if(cursor.moveToFirst())
			{
				Count = cursor.getInt(0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
		}
		return Count;
	}
	
	
	public DiscountDO getCaseVAlueAndTax(String sku, String pricingKey, String UOM)
	{
		DiscountDO objDiscount = null;
		Cursor cursor = null;
		SQLiteDatabase mDatabase = null;
		try
		{
			mDatabase 			= DatabaseHelper.openDataBase();
		    
			String strItemPrice = "select PRICECASES, TaxGroupCode, TaxPercentage from tblPricing where " +
								  "ITEMCODE = '"+sku+"' and CUSTOMERPRICINGKEY = '"+pricingKey+"' AND UOM='"+UOM+"'";
			
			cursor 				= mDatabase.rawQuery(strItemPrice, null);
			if(cursor.moveToFirst())
			{
				objDiscount = new DiscountDO();
				objDiscount.perCaseValue = cursor.getString(0);
				objDiscount.TaxGroupCode = cursor.getString(1);
				objDiscount.TaxPercentage = StringUtils.getFloat(cursor.getString(2));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return objDiscount;
	}
	
	public String getCustomerPricingKey(String strCustomerSiteId)
	{
		Cursor cursor = null;
		String strPricingKey = "206";
		SQLiteDatabase mDatabase = null;
		
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			String query="SELECT CUSTPRICECLASS from tblCustomerGroup where CUSTOMER_SITE_ID='"+strCustomerSiteId+"'";
			cursor = mDatabase.rawQuery(query, null);
			if(cursor != null && cursor.moveToFirst())
				strPricingKey = cursor.getString(0);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor !=  null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		
		return strPricingKey; 
	}
	
	public float getPendingAmount(String customerSiteId)
	{ 
		 SQLiteDatabase sqLiteDatabase =  null;
		 Cursor cursor = null;
		 try 
		 {
			 sqLiteDatabase =  DatabaseHelper.openDataBase();
			 cursor = sqLiteDatabase.rawQuery("Select Sum(BalanceAmount) from tblPendingInvoices where CustomersiteId ='"+customerSiteId+"'", null);
			 if(cursor.moveToFirst())
			 {
				return StringUtils.getFloat(cursor.getString(0));
			 }
		 } 
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(cursor!=null)
				 cursor.close();
			 if(sqLiteDatabase!=null)
				 sqLiteDatabase.close();
		 }
		 return 0;
	}
	
	public HashMap<String, Vector<ProductDO>> getLast6MonthOrdersByCustomer(String site, String priceClass, String orderType) 
	{
		String strPreviousOrder = "Select distinct P.ProductId, P.ItemCode, P.Description,P.Category, P.Brand,P.UOM,P.SecondaryUOM,P.CaseBarCode,P.UnitBarCode,"+
								  "P.ItemType,P.UnitPerCase,P.PricingKey,TC.CategoryName, OD.CasesDelivered,OD.UnitsDelivered from tblProducts P, tblCategory TC, tblInvoiceOrderDetails OD" +
								  " where P.Category<>'' and P.ItemCode=OD.SKU and TC.CategoryId=P.Category and " +
								  "OD.OrderId IN(SELECT OrderId FROM tblInvoiceOrders WHERE CustomerSiteId = "+site+") AND " +
								  "OD.SKU IN(select DISTINCT ITEMCODE from tblVanStock where SellableQuantity > 0) GROUP BY P.ItemCode";

		SQLiteDatabase mDatabase = null;
		Cursor cursor =null;
		HashMap<String, Vector<ProductDO>> hmProducts =null;
		try
		{
			mDatabase  = DatabaseHelper.openDataBase();
			cursor     = mDatabase.rawQuery(strPreviousOrder, null);
			hmProducts = parseCursor(cursor, LAST_ORDER, null, priceClass, orderType, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return hmProducts;
	}
	
	public void insertInventory(InventoryDO inventoryDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInventory (InventoryId, SiteNo, uploadStatus, Status, CreatedOn, CreatedBy) VALUES(?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblInventoryDetails (InventoryDetailsId, InventoryId, ItemCode, InventoryQuantity, RecomendedQuantity) VALUES(?,?,?,?,?)");
				if(inventoryDO != null)
				{
					stmtInsert.bindString(1, inventoryDO.inventoryAppId);
					stmtInsert.bindString(2, inventoryDO.site);
					stmtInsert.bindString(3, ""+inventoryDO.uplaodStatus);
					stmtInsert.bindString(4, ""+inventoryDO.uplaodStatus);
					stmtInsert.bindString(5, ""+inventoryDO.date);
					stmtInsert.bindString(6, inventoryDO.createdBy);
					
					int count = 1;
					for(InventoryDetailDO inventoryDetailDO : inventoryDO.vecInventoryDOs)
					{
						stmtInsertOrder.bindString(1, ""+(count++));
						stmtInsertOrder.bindString(2, inventoryDO.inventoryAppId);
						stmtInsertOrder.bindString(3, inventoryDetailDO.itemCode);
						stmtInsertOrder.bindString(4, ""+inventoryDetailDO.inventoryQty);
						stmtInsertOrder.bindString(5, ""+inventoryDetailDO.recmQty);
						stmtInsertOrder.executeInsert();
					}
					stmtInsert.executeInsert();
					stmtInsert.close();
				}
				stmtInsertOrder.close();
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
		}
	}
	
	public Vector<String> getPricingUOM(String itemcode, String pricing, SQLiteDatabase sqlLite)
	{
		Vector<String> vector = new Vector<String>();
		try
		{
			if(sqlLite == null || !sqlLite.isOpen())
				sqlLite = DatabaseHelper.openDataBase();
			
			String strQuery = "SELECT UOM FROM tblPricing WHERE  ITEMCODE = '"+itemcode+"' AND CUSTOMERPRICINGKEY ='"+pricing+"'";
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c.moveToFirst())
			{
				do
				{
					vector.add(c.getString(0));
				}
				while(c.moveToNext());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public float getUOMFactor(HashMap<String, Float> hmConversion, ProductDO objItem)
	{
		String key = objItem.SKU + objItem.UOM;
		if(key.contains("GAL"))
			key = key.replace("GAL", "GLN");
		
		float factor = 1;
		if(hmConversion != null && hmConversion.size() > 0 && hmConversion.containsKey(key))
			factor = hmConversion.get(key);
		else if(objItem.CategoryId != null && objItem.CategoryId.contains(AppConstants.TUB))
		{
			key 	= AppConstants.ITEM_CODE + objItem.UOM;
			factor  = hmConversion.get(key);
		}
		
		return factor;
	}
	
	public int getQuantityBU(ProductDO objItem, HashMap<String, Float> hmConversion) 
	{
		String key      =  objItem.SKU+objItem.UOM;
		int quantityBU  =  0;
		if(hmConversion != null && hmConversion.containsKey(key))
		{
			float factor = hmConversion.get(key);
			quantityBU   = Math.round(StringUtils.getFloat(objItem.preUnits) * factor);
		}
		return quantityBU;
	}


	/*public String  getDisocuntOnitems(String orderId) {

		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInventory (InventoryId, SiteNo, uploadStatus, Status, CreatedOn, CreatedBy) VALUES(?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsertOrder = objSqliteDB.compileStatement("INSERT INTO tblInventoryDetails (InventoryDetailsId, InventoryId, ItemCode, InventoryQuantity, RecomendedQuantity) VALUES(?,?,?,?,?)");
				
				stmtInsertOrder.close();
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
		}
	
		return null;
	}*/

	//======================================================Added on 11th DEC 2017 for Return Order
	public Vector<ProductDO> getReturnItems(String catId, String orderedItemsList, JourneyPlanDO objJourneyPlan, boolean isStockCheck, String userCode, int daysLIMIT, int invoiceLIMIT)
	{
		synchronized(MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursorProducts =null;
			Vector<ProductDO> vectDetails = new Vector<ProductDO>();
			try
			{
				mDatabase  			=   DatabaseHelper.openDataBase();
				String strQuery 	= 	null,subQuery="";

				/*if(invoiceLIMIT>0)
					subQuery = "limit "+invoiceLIMIT;
				else*/
					subQuery = "";

				strQuery
						= "SELECT DISTINCT TD.LineNo,TH.OrderId, TD.ItemCode,TD.ItemType,TD.ItemDescription,TD.ItemPrice," +
						"TD.UnitSellingPrice, TD.UOM,TD.Cases,TD.Units, TD.QuantityBU," +
						"TD.PriceUsedLevel1,TD.PriceUsedLevel2,TD.TrxReasonCode"
						+ ", TD.LineTaxAmount, TD.ProrataTaxAmount,TD.TotalTax,TD.QuantityInstock,TD.TotalDiscountAmount,TD.TaxPercentage ,TD.RelatedLineID,TD.RefTrxCode "
						+ " FROM tblOrderDetail TD "
						+ "inner join tblProducts PM ON TD.ItemCode = PM.ItemCode AND PM.Category='"+catId+"' AND TD. ItemType!='F'  "
						+ "INNER JOIN tblCustomer TC "
						+ "INNER JOIN tblOrderHeader TH ON TH.OrderId = TD.OrderNo "
						+ "WHERE  TH.OrderId in "
						+ "(SELECT OrderId FROM tblOrderHeader where  "
						+ " SiteNo = '"+objJourneyPlan.site+"'  "
						+ "AND TRXStatus ='"+AppStatus.TRX_STATUS_DELIVRED+"'  AND  OrderType='"+AppStatus.TRX_STATUS_HH_TYPE+"' "
//                        " AND Date('now')>=Date(julianday(OrderDate)+ "+daysLIMIT+") "
						+ " ORDER BY OrderDate DESC "+subQuery+") group by TD.ItemCode,TD.OrderNo, TD.LineNo ORDER BY TH.OrderDate DESC";


				cursorProducts  = 	mDatabase.rawQuery(strQuery, null);
				if(cursorProducts!=null && cursorProducts.moveToFirst())
					vectDetails =   parseReturnItems(cursorProducts, objJourneyPlan.priceList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursorProducts!=null && !cursorProducts.isClosed())
					cursorProducts.close();

				if(mDatabase!=null)
					mDatabase.close();
			}
			return vectDetails;
		}
	}
	public Vector<ProductDO> parseReturnItems(Cursor cursor,String strPriceClass)
	{
		Vector<ProductDO> vecProducts = new Vector<ProductDO>();
		try
		{
			if(cursor.moveToFirst())
			{
				do
				{
					ProductDO trxDetailDo 					=	new ProductDO();
					trxDetailDo.LineNo 						= 	cursor.getString(0);
					trxDetailDo.OrderNo 					= 	cursor.getString(1);
					trxDetailDo.SKU 				        = 	cursor.getString(2);
					trxDetailDo.ItemType 					=   cursor.getString(3);
					trxDetailDo.Description					= 	cursor.getString(4);
					trxDetailDo.preCases					=   cursor.getString(5);
					trxDetailDo.unitSellingPrice			=   StringUtils.getDoubleFromFloatWithoutError(cursor.getFloat(6));
					trxDetailDo.UOM							=   cursor.getString(7);
					trxDetailDo.cases						=   cursor.getString(8);
					trxDetailDo.units						=   cursor.getString(9);
					trxDetailDo.quantityBU					=   cursor.getFloat(10);
					trxDetailDo.priceUsedLevel1		 		=   cursor.getDouble(11);
					trxDetailDo.priceUsedLevel2	 			=   cursor.getDouble(12);
					trxDetailDo.TrxReasonCode				=   cursor.getString(13);
					trxDetailDo.LineTaxAmount				=   cursor.getString(14)!=null?StringUtils.getDouble(cursor.getString(14)):0;
					trxDetailDo.ProrataTaxAmount			=   cursor.getString(15)!=null?StringUtils.getDouble(cursor.getString(15)):0;
					trxDetailDo.TotalTax					=   cursor.getString(16)!=null?StringUtils.getDouble(cursor.getString(16)):0;
					trxDetailDo.OriginalLineTaxAmount				=   StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(cursor.getString(14)));
					trxDetailDo.OriginalProrataTaxAmount			=   StringUtils.getFloat(cursor.getString(15));
					trxDetailDo.OriginalTotalTax					=   StringUtils.getFloat(cursor.getString(16));
					trxDetailDo.QuantityInStock				=   StringUtils.getFloat(cursor.getString(17));
					trxDetailDo.DiscountAmt					=   StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(cursor.getString(18)));
					trxDetailDo.DiscountAmtReturnBackup					=   StringUtils.getDoubleFromFloatWithoutError(StringUtils.getFloat(cursor.getString(18)));
					trxDetailDo.TaxPercentage				=   StringUtils.getFloat(cursor.getString(19));
					trxDetailDo.RelatedLineId				=	(trxDetailDo.LineNo);
					trxDetailDo.RefTrxCode					=	trxDetailDo.OrderNo;

					if(trxDetailDo.QuantityInStock>0 && StringUtils.getFloat(trxDetailDo.units)>0)
						vecProducts.add(trxDetailDo);

				}while(cursor.moveToNext());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vecProducts;
	}


    public String getOriginalAmount(String Itemcode, String OrderNO )
    {
        SQLiteDatabase mDatabase = null;
		String detailAmt="";
        Vector<String> vector = new Vector<String>();
        try
        {
            mDatabase  			=   DatabaseHelper.openDataBase();

            //String strQuery = "SELECT TotalAmountWithVat FROM tblOrderHeader WHERE  OrderNo='"+OrderNO+"' AND ITEMCODE = '"+Itemcode+"'";
			String strQuery = "SELECT TotalAmountWithVat FROM tblOrderHeader WHERE  OrderId='"+OrderNO+"'";
            Cursor c=mDatabase.rawQuery(strQuery, null);
            if(c.moveToFirst())
            {
                do
                {
//                    vector.add(c.getString(0));
					detailAmt=c.getString(0);
                }

                while(c.moveToNext());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return detailAmt;
    }//on dec 25
    public String getOriginalDisc(String Itemcode, String OrderNO )
    {
        SQLiteDatabase mDatabase = null;
		String detailAmt="";
        Vector<String> vector = new Vector<String>();
        try
        {
            mDatabase  			=   DatabaseHelper.openDataBase();

            String strQuery = "SELECT LineTaxAmount FROM tblOrderDetail WHERE  OrderNo='"+OrderNO+"' AND ITEMCODE = '"+Itemcode+"'";
            Cursor c=mDatabase.rawQuery(strQuery, null);
            if(c.moveToFirst())
            {
                do
                {
//                    vector.add(c.getString(0));
					detailAmt=c.getString(0);
                }

                while(c.moveToNext());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return detailAmt;
    }//on dec 25

}
