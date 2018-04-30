package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.DiscountMasterDO;
import com.winit.baskinrobbin.salesman.dataobject.DiscountPromoDO;

public class DiscountDA 
{
	public boolean insertDiscounts(Vector<DiscountMasterDO> vecDiscountMasterDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
//				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblDiscountMaster WHERE DiscountId = ?");
				
				
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblDiscountMaster (DiscountId, SiteNumber, Level, Code, DiscountType, Discount, UOM, MinQty, MaxQty, Name, Description, Discount_Modifier) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblDiscountMaster SET SiteNumber=?, Level=?, Code=?, DiscountType=?, Discount=?, UOM=?, MinQty=?, MaxQty=?,Name = ?, Description=?, Discount_Modifier=? WHERE DiscountId = ?");
				 //Need to change the Column values.
				for(DiscountMasterDO  discountMasterDO : vecDiscountMasterDOs)
				{
//					stmtSelectRec.bindString(1, discountMasterDO.DiscountId);
//					
//					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
						stmtUpdate.bindString(1, discountMasterDO.Site_Number);
						stmtUpdate.bindString(2, discountMasterDO.Level);
						stmtUpdate.bindString(3, discountMasterDO.Code);
						stmtUpdate.bindString(4, discountMasterDO.DiscountType);
						stmtUpdate.bindString(5, discountMasterDO.Discount);
						stmtUpdate.bindString(6, discountMasterDO.UOM);
						stmtUpdate.bindString(7, discountMasterDO.MinQty);
						stmtUpdate.bindString(8, discountMasterDO.MaxQty);
						stmtUpdate.bindString(9, discountMasterDO.Name);
						stmtUpdate.bindString(10, discountMasterDO.Description);
						stmtUpdate.bindString(11, discountMasterDO.Discount_Modifier);
						stmtUpdate.bindString(12, discountMasterDO.DiscountId);
//						stmtUpdate.execute();
//					}
//					else
					if(stmtUpdate.executeUpdateDelete() <=0)
					{
						stmtInsert.bindString(1, discountMasterDO.DiscountId);
						stmtInsert.bindString(2, discountMasterDO.Site_Number);
						stmtInsert.bindString(3, discountMasterDO.Level);
						stmtInsert.bindString(4, discountMasterDO.Code);
						stmtInsert.bindString(5, discountMasterDO.DiscountType);
						stmtInsert.bindString(6, discountMasterDO.Discount);
						stmtInsert.bindString(7, discountMasterDO.UOM);
						stmtInsert.bindString(8, discountMasterDO.MinQty);
						stmtInsert.bindString(9, discountMasterDO.MaxQty);
						stmtInsert.bindString(10, discountMasterDO.Name);
						stmtInsert.bindString(11, discountMasterDO.Description);
						stmtInsert.bindString(12, discountMasterDO.Discount_Modifier);
						stmtInsert.executeInsert();
					}
				}
				
				
//				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
	
	
	public boolean insertPromoDiscounts(Vector<DiscountPromoDO> vecDiscountPromoDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
//				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblPromoDiscountDetail WHERE SiteNumber = ? AND Code=?");
				
				
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblPromoDiscountDetail (DiscountId, SiteNumber, Level, Code, DiscountType, Discount, UOM, MinQty, MaxQty, Name, Description, Discount_Modifier) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblPromoDiscountDetail SET DiscountId=?, Level=?, DiscountType=?, Discount=?, UOM=?, MinQty=?, MaxQty=?, Name=?, Description=?, Discount_Modifier=? WHERE SiteNumber = ? AND Code=?");
				 //Need to change the Column values.
				for(DiscountPromoDO  discountPromoDO : vecDiscountPromoDOs)
				{
//					stmtSelectRec.bindString(1, discountPromoDO.SiteNumber);
//					stmtSelectRec.bindString(2, discountPromoDO.Code);
//					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
						stmtUpdate.bindString(1, discountPromoDO.DiscountId);
						stmtUpdate.bindString(2, discountPromoDO.Level);
						stmtUpdate.bindString(3, discountPromoDO.DiscountType);
						stmtUpdate.bindString(4, discountPromoDO.Discount);
						stmtUpdate.bindString(5, discountPromoDO.UOM);
						stmtUpdate.bindString(6, discountPromoDO.MinQty);
						stmtUpdate.bindString(7, discountPromoDO.MaxQty);
						stmtUpdate.bindString(8, discountPromoDO.Name);
						stmtUpdate.bindString(9, discountPromoDO.Description);
						stmtUpdate.bindString(10, discountPromoDO.Discount_Modifier);
						stmtUpdate.bindString(11, discountPromoDO.SiteNumber);
						stmtUpdate.bindString(12, discountPromoDO.Code);
//						stmtUpdate.execute();
//					}
//					else
					if(stmtUpdate.executeUpdateDelete() <=0)
					{
						stmtInsert.bindString(1, discountPromoDO.DiscountId);
						stmtInsert.bindString(2, discountPromoDO.SiteNumber);
						stmtInsert.bindString(3, discountPromoDO.Level);
						stmtInsert.bindString(4, discountPromoDO.Code);
						stmtInsert.bindString(5, discountPromoDO.DiscountType);
						stmtInsert.bindString(6, discountPromoDO.Discount);
						stmtInsert.bindString(7, discountPromoDO.UOM);
						stmtInsert.bindString(8, discountPromoDO.MinQty);
						stmtInsert.bindString(9, discountPromoDO.MaxQty);
						stmtInsert.bindString(10, discountPromoDO.Name);
						stmtInsert.bindString(11, discountPromoDO.Description);
						stmtInsert.bindString(12, discountPromoDO.Discount_Modifier);
						stmtInsert.executeInsert();
					}
				}
				
				
//				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
}
