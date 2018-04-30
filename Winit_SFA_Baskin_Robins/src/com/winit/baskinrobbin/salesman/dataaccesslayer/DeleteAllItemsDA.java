package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SyncLogDO;

/***
 * This is used to delete expired items from module tables.
 * @author Sivaprasad.
 *
 */
public class DeleteAllItemsDA {
	
	enum ModuleName
	{
		MODULE_CATEGORY,
		MODULE_ITEM,
		MODULE_IMAGE_GALLERY,
		MODULE_BLASEUSER,
		MODULE_CUSTOMER,
		MODULE_CUSTOMERGROUP,
		MODULE_PRICING,
		MODULE_COUNTRY,
		MODULE_SOURCE,
		MODULE_LANDMARK,
		MODULE_CUSTOMER_TYPE
		
	}
	
	private final String MODULE_CATEGORY 		= "Category";
	private final String MODULE_ITEM     		= "Item";
	private final String MODULE_IMAGE_GALLERY   = "ImageGallery";
	private final String MODULE_BLASEUSER     	= "BlaseUser";
	private final String MODULE_CUSTOMER   		= "Customer";
	private final String MODULE_CUSTOMERGROUP 	= "CustomerGroup";
	private final String MODULE_PRICING			= "Pricing";
	
	private final String MODULE_COUNTRY     	= "Country";
	private final String MODULE_SOURCE   		= "Source";
	private final String MODULE_LANDMARK 		= "SalesmanLandmark";
	private final String MODULE_CUSTOMER_TYPE	= "CustomerType";
	
	
	// table names variables
	private final String TABLE_CATEGORY 		= "tblCategory";
	private final String TABLE_ITEM     		= "tblProducts";
	private final String TABLE_IMAGE_GALLERY   	= "tblProductImages";
	private final String TABLE_BLASEUSER    	= "tblUsers";
	private final String TABLE_CUSTOMER 		= "tblCustomerSites";
	private final String TABLE_CUSTOMERGROUP	= "tblCustomerGroup";
	private final String TABLE_PRICING			= "tblPricing";
	
	private final String TABLE_COUNTRY     		= "tblCountry";
	private final String TABLE_SOURCE   		= "tblSource";
	private final String TABLE_LANDMARK 		= "tblLandMark";
	private final String TABLE_CUSTOMER_TYPE	= "tblCustomerType";
	
	private final String ARGUMENT = " =?";
	private final String WHERE_CLAUSE = " where ";
	private final String TAG = "DeleteAllItemsDA";
	
	// entity field name variables
	private final String FIELD_CATEGORY = "CategoryId";
	private final String FIELD_ITEM     = "SKU";
	private final String FIELD_IMAGE_GALLERY   = "ImageGalleryId";
	private final String FIELD_BLASEUSER = "EmpNo";
	private final String FIELD_CUSTOMER  = "CustomerSiteId";
	private final String FIELD_CUSTOMERGROUP   = "COMPANYID"+ARGUMENT+" and CUSTPRICECLASS"+ARGUMENT+" and CUSTOMER_SITE_ID";
	
	private final String FIELD_PRICING   = "ITEMCODE"+ARGUMENT+" and CUSTOMERPRICINGKEY";
	
	private final String FIELD_COUNTRY     = "CountryId";
	private final String FIELD_SOURCE     = "Id";
	private final String FIELD_LANDMARK     = "LandmarkId";
	private final String FIELD_CUSTOMER_TYPE     = "CustomerTypeId";
	
	/**
	 * Delete the expired items from the modules.
	 * @param vecSynLogs
	 */
	public void deleteEntitys(Vector<SyncLogDO> vecSynLogs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				SQLiteDatabase objSqliteDB = DatabaseHelper.openDataBase();
				
				
				for(SyncLogDO objSyncLog : vecSynLogs)
				{
					ModuleName name = getModuleEnum(objSyncLog.module);
					if(name!=null)
					{
						SQLiteStatement deleteEntity = objSqliteDB.compileStatement(getQueryString(name)); 
						
						if(objSyncLog.entityId != null)
						{
							deleteEntity.bindString(1, objSyncLog.entityId);
							if(name == ModuleName.MODULE_CUSTOMERGROUP)
							{
								deleteEntity.bindString(2, objSyncLog.entityId2);
								deleteEntity.bindString(3, objSyncLog.entityId3);
							}
							else if(name == ModuleName.MODULE_PRICING)
							{
								deleteEntity.bindString(2, objSyncLog.entityId2);
							}
							deleteEntity.execute();
						}
						deleteEntity.close();
					}
				}
				
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
		}
	}
	
	/***
	 * Find the enum name of the module.
	 * @param moduleName
	 * @return Enum ModuleName
	 */
	private ModuleName getModuleEnum(String moduleName)
	{
		if(moduleName.equalsIgnoreCase(MODULE_CATEGORY))
			return ModuleName.MODULE_CATEGORY;
		else if(moduleName.equalsIgnoreCase(MODULE_ITEM))
			return ModuleName.MODULE_ITEM;
		else if(moduleName.equalsIgnoreCase(MODULE_IMAGE_GALLERY))
			return ModuleName.MODULE_IMAGE_GALLERY;
		
		else if(moduleName.equalsIgnoreCase(MODULE_BLASEUSER))
			return ModuleName.MODULE_BLASEUSER;
		else if(moduleName.equalsIgnoreCase(MODULE_CUSTOMER))
			return ModuleName.MODULE_CUSTOMER;
		else if(moduleName.equalsIgnoreCase(MODULE_CUSTOMERGROUP))
			return ModuleName.MODULE_CUSTOMERGROUP;
		else if(moduleName.equalsIgnoreCase(MODULE_PRICING))
			return ModuleName.MODULE_PRICING;
		else if(moduleName.equalsIgnoreCase(MODULE_COUNTRY))
			return ModuleName.MODULE_COUNTRY;
		else if(moduleName.equalsIgnoreCase(MODULE_CUSTOMER_TYPE))
			return ModuleName.MODULE_CUSTOMER_TYPE;
		else if(moduleName.equalsIgnoreCase(MODULE_LANDMARK))
			return ModuleName.MODULE_LANDMARK;
		else if(moduleName.equalsIgnoreCase(MODULE_SOURCE))
			return ModuleName.MODULE_SOURCE;
		return null;
	}
	
	/***
	 * Binding the query string for given module.
	 * @param moduleName
	 * @return String Delete query string of the module.
	 */
	private String getQueryString(ModuleName moduleName)
	{
		StringBuilder builder = new StringBuilder("Delete from ");
		builder.append(getTable(moduleName));
		builder.append(WHERE_CLAUSE);
		builder.append(getEntityField(moduleName));
		builder.append(ARGUMENT);

		return builder.toString();
	}
	
	/***
	 * Get the table name of the module.
	 * @param moduleName
	 * @return String table name
	 */
	private String getTable(ModuleName moduleName)
	{
		switch(moduleName)
		{
		case MODULE_CATEGORY :
			return TABLE_CATEGORY;
		case MODULE_ITEM :
			return TABLE_ITEM;
		case MODULE_IMAGE_GALLERY:
			return TABLE_IMAGE_GALLERY;
		case MODULE_BLASEUSER:
			return TABLE_BLASEUSER;
		case MODULE_CUSTOMER:
			return TABLE_CUSTOMER;
		case MODULE_CUSTOMERGROUP:
			return TABLE_CUSTOMERGROUP;
		case MODULE_PRICING:
			return TABLE_PRICING;
		case MODULE_COUNTRY:
			return TABLE_COUNTRY;
		case MODULE_CUSTOMER_TYPE:
			return TABLE_CUSTOMER_TYPE;
		case MODULE_LANDMARK:
			return TABLE_LANDMARK;
		case MODULE_SOURCE:
			return TABLE_SOURCE;
		}
		
		return "";
		
	}
	
	/***
	 * Get the filed name of the module.
	 * @param moduleName
	 * @return String field name
	 */
	private String getEntityField(ModuleName moduleName)
	{
		switch(moduleName)
		{
		case MODULE_CATEGORY :
			return FIELD_CATEGORY;
		case MODULE_ITEM :
			return FIELD_ITEM;
		case MODULE_IMAGE_GALLERY:
			return FIELD_IMAGE_GALLERY;
		case MODULE_BLASEUSER:
			return FIELD_BLASEUSER;
		case MODULE_CUSTOMER:
			return FIELD_CUSTOMER;
		case MODULE_CUSTOMERGROUP:
			return FIELD_CUSTOMERGROUP;
		case MODULE_PRICING:
			return FIELD_PRICING;
		case MODULE_COUNTRY:
			return FIELD_COUNTRY;
		case MODULE_CUSTOMER_TYPE:
			return FIELD_CUSTOMER_TYPE;
		case MODULE_LANDMARK:
			return FIELD_LANDMARK;
		case MODULE_SOURCE:
			return FIELD_SOURCE;
		}
		
		return "";
	}

}
