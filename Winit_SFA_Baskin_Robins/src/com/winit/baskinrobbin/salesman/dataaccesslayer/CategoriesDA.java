package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;

public class CategoriesDA 
{
	public HashMap<String, CategoryDO> getCategoryList()
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		HashMap<String, CategoryDO> hmCategories = null;
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory where CategoryId in (select DISTINCT Category from tblProducts) order by CategoryName asc", null);
			
			if(cursor.moveToFirst())
			{
				hmCategories = new HashMap<String, CategoryDO>();
				AppConstants.vecCategories = new Vector<CategoryDO>();
				 
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					AppConstants.vecCategories.add(objCategory);
					hmCategories.put(objCategory.categoryId, objCategory);
					
				}while(cursor.moveToNext());
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
		return hmCategories;
		}
	}
	
	public HashMap<String, CategoryDO> getCategoryListForReturn()
	{
		synchronized(MyApplication.MyLock) 
		{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		HashMap<String, CategoryDO> hmCategories = null;
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory  where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0)) order by CategoryName asc", null);
			
			if(cursor.moveToFirst())
			{
				hmCategories = new HashMap<String, CategoryDO>();
				AppConstants.vecCategories = new Vector<CategoryDO>();
				 
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					AppConstants.vecCategories.add(objCategory);
					hmCategories.put(objCategory.categoryName, objCategory);
					
				}while(cursor.moveToNext());
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
		return hmCategories;
		}
	}
	
	public Vector<CategoryDO> getAllCategory()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<CategoryDO> vector = new Vector<CategoryDO>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select * from tblCategory order by CategoryName asc", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						CategoryDO objCategory = new CategoryDO();
						objCategory.categoryId = cursor.getString(0);
						objCategory.categoryName = cursor.getString(1);
						vector.add(objCategory);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAllCategoryName()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("Select * from tblCategory WHERE CategoryId IN (SELECT DISTINCT Category FROM tblProducts) Order By CategoryName", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAvailableCategory()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0)) order by CategoryName asc", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAvailableNonSealableItemCategory()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where NonSellableQuantity!= 0)) order by CategoryName asc", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAvailableCategory_WithPricing(String pricing)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				String query = "Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0 AND ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+pricing+"' and IsExpired='False'))) order by CategoryName asc";
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAvailableCategory_WithLPOPricing(String pricing)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				String query = "Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock WHERE ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+pricing+"' and IsExpired='False'))) order by CategoryName asc";
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<String> getAvailableCategory_WithPricing_Return(String pricing)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				String query = "Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+pricing+"' and IsExpired='False')) order by CategoryName asc";
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
	
	public Vector<CategoryDO> getAllCategoryforFOC(String strPricingClass, HashMap<String, ProductDO> hmSelectedItems, 
			int orderType, HashMap<String, HHInventryQTDO> hmInventory)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<CategoryDO> vector = new Vector<CategoryDO>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor    = mDatabase.rawQuery("Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0)) order by CategoryName asc", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						CategoryDO categoryDO   = new CategoryDO();
						categoryDO.categoryId   = cursor.getString(0);
						categoryDO.categoryName = cursor.getString(1);
						
						categoryDO.vecProducts  = new ProductsDA().getProductsDetailsByCategoryId_FOC(categoryDO.categoryName, strPricingClass, hmSelectedItems, orderType, hmInventory);
						if(categoryDO.vecProducts != null && categoryDO.vecProducts.size() > 0)
							vector.add(categoryDO);
						
					}while(cursor.moveToNext());
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
			return vector;
		}
	}
}
