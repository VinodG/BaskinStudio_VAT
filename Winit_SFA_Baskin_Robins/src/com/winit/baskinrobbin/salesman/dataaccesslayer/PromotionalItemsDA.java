package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.GetAllDeleteLogDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PresellerPassCodeDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionAssignmentsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionAssignmentsTypeDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionBrandDiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionBundledItemDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionDetailsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionInstantItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionOfferItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionOrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionOrderItemsDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionTypesDO;
import com.winit.baskinrobbin.salesman.dataobject.PromotionsDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class PromotionalItemsDA
{
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserPresellerPasscode(Vector<PresellerPassCodeDO> vecPresellerPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblVMPassCode (VMPasscodeId, PresellerId, Passcode, IsUsed) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblVMPassCode");
				for(int i = 0; vecPresellerPassCodeDO !=null && i < vecPresellerPassCodeDO.size(); i++)
				{
					PresellerPassCodeDO objPresellerPassCodeDO = vecPresellerPassCodeDO.get(i);
					
					sqLiteStatement.bindLong(1, objPresellerPassCodeDO.presellerPasscodeId);
					sqLiteStatement.bindString(2, objPresellerPassCodeDO.presellerId);
					sqLiteStatement.bindLong(3, objPresellerPassCodeDO.passCode);
					sqLiteStatement.bindString(4, String.valueOf(objPresellerPassCodeDO.isUsed));
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
	
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserDAPasscode(Vector<NameIDDo> vecPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblDAPasscode (PasscodeId, EmpId, Passcode, IsUsed) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblDAPasscode");
				for(int i = 0; vecPassCodeDO !=null && i < vecPassCodeDO.size(); i++)
				{
					NameIDDo objNameIDDo = vecPassCodeDO.get(i);
					
					sqLiteStatement.bindString(1, objNameIDDo.strId);
					sqLiteStatement.bindString(2, objNameIDDo.strName);
					sqLiteStatement.bindString(3, objNameIDDo.strType);
					sqLiteStatement.bindString(4, ""+false);
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}

	public boolean insertPromotionData(
			Vector<PromotionsDO> vecPromotionsDO,
			Vector<PromotionOrderItemsDO> vecPromotionOrderItemsDO,
			Vector<PromotionOfferItemsDO> vecPromotionOfferItemsDO,
			Vector<PromotionAssignmentsDO> vecPromotionAssignmentsDO,
			Vector<PromotionDetailsDO> vecPromotionDetailsDOs,
			String empNo) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				
				if(vecPromotionsDO != null && vecPromotionsDO.size() > 0)
					inserPromotionDO(objSqLiteDatabase, vecPromotionsDO);
				
				if(vecPromotionOrderItemsDO != null && vecPromotionOrderItemsDO.size() > 0)
					inserPromotionOrderItemDO(objSqLiteDatabase, vecPromotionOrderItemsDO);
				
				if(vecPromotionOfferItemsDO != null && vecPromotionOfferItemsDO.size() > 0)
					insertPromotionOfferItemDO(objSqLiteDatabase, vecPromotionOfferItemsDO);
				
				if(vecPromotionAssignmentsDO != null && vecPromotionAssignmentsDO.size() > 0)
					inserPromotionAssignMentDO(objSqLiteDatabase, vecPromotionAssignmentsDO);
				
				if(vecPromotionDetailsDOs != null && vecPromotionDetailsDOs.size() > 0)
					inserPromotionDetailsDO(objSqLiteDatabase, vecPromotionDetailsDOs);
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
		return true;
	}
	
	public boolean inserPromotionTypes(SQLiteDatabase objSqLiteDatabase, Vector<PromotionTypesDO> vecPromotionTypesDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionType (PromotionTypeId, Description) VALUES(?,?)");
				objSqLiteDatabase.execSQL("delete from tblPromotionType");
				for(int i = 0; vecPromotionTypesDOs !=null && i < vecPromotionTypesDOs.size(); i++)
				{
					PromotionTypesDO proTypesDO = vecPromotionTypesDOs.get(i);
					
					sqLiteStatement.bindString(1, proTypesDO.PromotionTypeId);
					sqLiteStatement.bindString(2, proTypesDO.Description);
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	//using
	public boolean inserPromotionDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionsDO> vecPromotionDos)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotion (PromotionId, PromotionTypeId, Description," +
						"AltDescription, FactsheetCode, StartDate, EndDate, IsActive, VisibilityCheckRequired, MinInvoice, Discount) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotion WHERE PromotionId=?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotion SET PromotionTypeId=?, Description=?," +
						"AltDescription=?, FactsheetCode=?, StartDate=?, EndDate=?, IsActive=?,VisibilityCheckRequired=?, MinInvoice=?, Discount=? WHERE PromotionId=?");
				
				for(PromotionsDO promotionsDO :  vecPromotionDos)
				{
					stmtSelect.bindString(1, promotionsDO.PromotionId);
					long count = stmtSelect.simpleQueryForLong();
					if(count <=0 )
					{
						stmtInsert.bindString(1, promotionsDO.PromotionId);
						stmtInsert.bindString(2, promotionsDO.PromotionTypeId);
						stmtInsert.bindString(3, promotionsDO.Description);
						stmtInsert.bindString(4, promotionsDO.Description);
						stmtInsert.bindString(5, "");
						stmtInsert.bindString(6, promotionsDO.StartDate);
						stmtInsert.bindString(7, promotionsDO.EndDate);
						stmtInsert.bindString(8, promotionsDO.IsActive);
						stmtInsert.bindString(9, promotionsDO.VisibilityCheckRequired);
						stmtInsert.bindString(10,promotionsDO.MinInvoice);
						stmtInsert.bindString(11,promotionsDO.Discount);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionsDO.PromotionTypeId);
						stmtUpdate.bindString(2, promotionsDO.Description);
						stmtUpdate.bindString(3, promotionsDO.Description);
						stmtUpdate.bindString(4, "");
						stmtUpdate.bindString(5, promotionsDO.StartDate);
						stmtUpdate.bindString(6, promotionsDO.EndDate);
						stmtUpdate.bindString(7, promotionsDO.IsActive);
						stmtUpdate.bindString(8, promotionsDO.VisibilityCheckRequired);
						stmtUpdate.bindString(9, promotionsDO.MinInvoice);
						stmtUpdate.bindString(10,promotionsDO.Discount);
						stmtUpdate.bindString(11,promotionsDO.PromotionId);
						stmtUpdate.execute();
					}
				}
				
				stmtSelect.close();
				stmtInsert.close();
				stmtUpdate.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	//using
	public boolean inserPromotionDetailsDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionDetailsDO> vecPromotionDetailsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionDetail " +
						"(PromotionDetailId, PromotionId,Code) " +
						"VALUES(?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionDetail WHERE PromotionDetailId=?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionDetail SET PromotionId=?, Code=? WHERE PromotionDetailId=?");
				
				
				for(int i = 0; vecPromotionDetailsDOs !=null && i < vecPromotionDetailsDOs.size(); i++)
				{
					PromotionDetailsDO promotionsDetailsDO = vecPromotionDetailsDOs.get(i);
					
					stmtSelect.bindString(1, promotionsDetailsDO.PromotionDetailId);
					long count = stmtSelect.simpleQueryForLong();
					if(count==0)
					{
						sqLiteStatement.bindString(1, promotionsDetailsDO.PromotionDetailId);
						sqLiteStatement.bindString(2, promotionsDetailsDO.PromotionId);
						sqLiteStatement.bindString(3, promotionsDetailsDO.Code);
						sqLiteStatement.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionsDetailsDO.PromotionId);
						stmtUpdate.bindString(2, promotionsDetailsDO.Code);
						stmtUpdate.bindString(3, promotionsDetailsDO.PromotionDetailId);
						stmtUpdate.execute();
					}
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public boolean inserPromotionOrderItemDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionOrderItemsDO> vecPromotionOrdItemsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				 
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionOrderItem " +
						"(PromotionOrderItemId,PromotionDetailId,OrderType,Quantity,Value, UOM, ItemCode) " +
						"VALUES(?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionOrderItem WHERE PromotionOrderItemId = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionOrderItem " +
						"SET PromotionDetailId=?,OrderType=?,Quantity=?,Value=?,UOM=?, ItemCode=? WHERE PromotionOrderItemId =?");
				
				for(PromotionOrderItemsDO promotionOrderItemsDO : vecPromotionOrdItemsDOs)
				{
					stmtSelect.bindString(1, promotionOrderItemsDO.PromotionOrderItemId);
					long count = stmtSelect.simpleQueryForLong();
					if(count <= 0)
					{
						stmtInsert.bindString(1, promotionOrderItemsDO.PromotionOrderItemId);
						stmtInsert.bindString(2, promotionOrderItemsDO.PromotionId);
						stmtInsert.bindString(3, promotionOrderItemsDO.OrderType);
						stmtInsert.bindString(4, promotionOrderItemsDO.Quantity);
						stmtInsert.bindString(5, promotionOrderItemsDO.Value);
						stmtInsert.bindString(6, promotionOrderItemsDO.UOM);
						stmtInsert.bindString(7, promotionOrderItemsDO.ItemCode);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionOrderItemsDO.PromotionId);
						stmtUpdate.bindString(2, promotionOrderItemsDO.OrderType);
						stmtUpdate.bindString(3, promotionOrderItemsDO.Quantity);
						stmtUpdate.bindString(4, promotionOrderItemsDO.Value);
						stmtUpdate.bindString(5, promotionOrderItemsDO.UOM);
						stmtUpdate.bindString(6, promotionOrderItemsDO.ItemCode);
						stmtUpdate.bindString(7, promotionOrderItemsDO.PromotionOrderItemId);
						stmtUpdate.execute();
					}
				}
				stmtSelect.close();
				stmtUpdate.close();
				stmtInsert.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public boolean insertPromotionOfferItemDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionOfferItemsDO> vecPromotionfOfferItemsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionOfferItem " +
						"(PromotionOfferItemId,PromotionDetailId,OfferType,Quantity,UOM,MaxQuantity,Discount, ItemCode)" +
						"VALUES(?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionOfferItem WHERE PromotionOfferItemId = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionOfferItem " +
						"SET PromotionDetailId=?,OfferType=?,Quantity=?,UOM=?,MaxQuantity=?,Discount=?, ItemCode=? WHERE PromotionOfferItemId =?");
				
				for(PromotionOfferItemsDO promotionOfferItemsDO : vecPromotionfOfferItemsDOs)
				{
					stmtSelect.bindString(1, promotionOfferItemsDO.PromotionOfferItemId);
					long count = stmtSelect.simpleQueryForLong();
					if(count <= 0)
					{
						stmtInsert.bindString(1, promotionOfferItemsDO.PromotionOfferItemId);
						stmtInsert.bindString(2, promotionOfferItemsDO.PromotionId);
						stmtInsert.bindString(3, promotionOfferItemsDO.OfferType);
						stmtInsert.bindString(4, promotionOfferItemsDO.Quantity);
						stmtInsert.bindString(5, promotionOfferItemsDO.UOM);
						stmtInsert.bindString(6, promotionOfferItemsDO.MaxQuantity);	
						stmtInsert.bindString(7, promotionOfferItemsDO.Discount);
						stmtInsert.bindString(8, promotionOfferItemsDO.ItemCode);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionOfferItemsDO.PromotionId);
						stmtUpdate.bindString(2, promotionOfferItemsDO.OfferType);
						stmtUpdate.bindString(3, promotionOfferItemsDO.Quantity);
						stmtUpdate.bindString(4, promotionOfferItemsDO.UOM);
						stmtUpdate.bindString(5, promotionOfferItemsDO.MaxQuantity);
						stmtUpdate.bindString(6, promotionOfferItemsDO.Discount);	
						stmtUpdate.bindString(7, promotionOfferItemsDO.ItemCode);
						stmtUpdate.bindString(8, promotionOfferItemsDO.PromotionOfferItemId);
						stmtUpdate.execute();
					}
				}
				stmtInsert.close();
				stmtSelect.close();
				stmtUpdate.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	public boolean inserPromotionInstantItemDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionInstantItemsDO> vecPromotionInstantItemsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionInstantItem " +
						"(PromotionInstantItemId,PromotionDetailId,ItemCode,OrderType,Ordered,OrderUOM,OfferItemCode,OfferType,Offered,OfferUOM) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionInstantItem WHERE PromotionInstantItemId=?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionInstantItem SET PromotionDetailId=?,ItemCode=?,OrderType=?,Ordered=?,OrderUOM=?," +
									"OfferItemCode=?,OfferType=?,Offered=?,OfferUOM=? WHERE PromotionInstantItemId=?");
				
				for(int i = 0; vecPromotionInstantItemsDOs !=null && i < vecPromotionInstantItemsDOs.size(); i++)
				{
					PromotionInstantItemsDO promotionInstantItemsDO = vecPromotionInstantItemsDOs.get(i);
					
					stmtSelect.bindString(1, promotionInstantItemsDO.PromotionInstantItemId);
					long count = stmtSelect.simpleQueryForLong();
					if(count==0)
					{
						sqLiteStatement.bindString(1, promotionInstantItemsDO.PromotionInstantItemId);
						sqLiteStatement.bindString(2, promotionInstantItemsDO.PromotionDetailId);
						sqLiteStatement.bindString(3, promotionInstantItemsDO.ItemCode);
						sqLiteStatement.bindString(4, promotionInstantItemsDO.OrderType);
						sqLiteStatement.bindString(5, promotionInstantItemsDO.Ordered);
						sqLiteStatement.bindString(6, promotionInstantItemsDO.OrderUOM);
						sqLiteStatement.bindString(7, promotionInstantItemsDO.OfferItemCode);
						sqLiteStatement.bindString(8, promotionInstantItemsDO.OfferType);
						sqLiteStatement.bindString(9, promotionInstantItemsDO.Offered);
						sqLiteStatement.bindString(10, promotionInstantItemsDO.OfferUOM);
						sqLiteStatement.executeInsert();
					}
					else
					{
						
						stmtUpdate.bindString(1, promotionInstantItemsDO.PromotionDetailId);
						stmtUpdate.bindString(2, promotionInstantItemsDO.ItemCode);
						stmtUpdate.bindString(3, promotionInstantItemsDO.OrderType);
						stmtUpdate.bindString(4, promotionInstantItemsDO.Ordered);
						stmtUpdate.bindString(5, promotionInstantItemsDO.OrderUOM);
						stmtUpdate.bindString(6, promotionInstantItemsDO.OfferItemCode);
						stmtUpdate.bindString(7, promotionInstantItemsDO.OfferType);
						stmtUpdate.bindString(8, promotionInstantItemsDO.Offered);
						stmtUpdate.bindString(9, promotionInstantItemsDO.OfferUOM);
						stmtUpdate.bindString(10, promotionInstantItemsDO.PromotionInstantItemId);

						stmtUpdate.execute();
					}
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	//Using
	public boolean inserPromotionAssignMentDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionAssignmentsDO> vecPromotionAssignmentsDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionAssignment " +
						"(PromotionAssignmentId,PromotionId,Code,AssignmentType) " +
						"VALUES(?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionAssignment WHERE PromotionAssignmentId = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionAssignment " +
						"SET PromotionId =?,Code =?,AssignmentType =? WHERE PromotionAssignmentId = ?");
				for(PromotionAssignmentsDO promotionAssignmentsDO : vecPromotionAssignmentsDOs)
				{
					stmtSelect.bindString(1, promotionAssignmentsDO.PromotionAssignmentId);
					long count = stmtSelect.simpleQueryForLong();
					if(count <= 0)
					{
						stmtInsert.bindString(1, promotionAssignmentsDO.PromotionAssignmentId);
						stmtInsert.bindString(2, promotionAssignmentsDO.PromotionId);
						stmtInsert.bindString(3, promotionAssignmentsDO.Code);
						stmtInsert.bindString(4, promotionAssignmentsDO.AssignmentType);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionAssignmentsDO.PromotionId);
						stmtUpdate.bindString(2, promotionAssignmentsDO.Code);
						stmtUpdate.bindString(3, promotionAssignmentsDO.AssignmentType);
						stmtUpdate.bindString(4, promotionAssignmentsDO.PromotionAssignmentId);
						stmtUpdate.execute();
					}
				}
				stmtInsert.close();
				stmtUpdate.close();
				stmtSelect.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	//using
	public boolean inserPromotionAssignMentTypeDO(SQLiteDatabase objSqLiteDatabase, Vector<PromotionAssignmentsTypeDO> vecPromotionAssignmentsTypeDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionAssignmentType " +
						"(AssignmentType,Description) " +
						"VALUES(?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionAssignmentType WHERE AssignmentType = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionAssignmentType " +
						"SET Description=? WHERE AssignmentType = ?");
				for(PromotionAssignmentsTypeDO promotionAssignmentsTypeDO : vecPromotionAssignmentsTypeDOs)
				{
					stmtSelect.bindString(1, promotionAssignmentsTypeDO.AssignmentType);
					long count = stmtSelect.simpleQueryForLong();
					if(count < 0)
					{
						stmtInsert.bindString(1, promotionAssignmentsTypeDO.AssignmentType);
						stmtInsert.bindString(2, promotionAssignmentsTypeDO.Description);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, promotionAssignmentsTypeDO.Description);
						stmtUpdate.bindString(2, promotionAssignmentsTypeDO.AssignmentType);
						stmtUpdate.execute();
					}
				}
				stmtInsert.close();
				stmtUpdate.close();
				stmtSelect.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
//	[tblPromotionBrandDiscount]
	//using
	public boolean inserPromotionBrandDiscount(SQLiteDatabase objSqLiteDatabase, Vector<PromotionBrandDiscountDO> vecPromotionAssignmentsTypeDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionBrandDiscount " +
						"(PromotionBrandDiscountId,PromotionId,ItemGroupCode,ItemGroupLevelId,Discount) " +
						"VALUES(?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionBrandDiscount WHERE PromotionBrandDiscountId = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionBrandDiscount " +
						"SET PromotionId=?,ItemGroupCode=?,ItemGroupLevelId=?,Discount=? WHERE PromotionBrandDiscountId = ?");
				for(PromotionBrandDiscountDO promotionAssignmentsTypeDO : vecPromotionAssignmentsTypeDOs)
				{
					stmtSelect.bindLong(1, promotionAssignmentsTypeDO.promotionBrandDiscountId);
					long count = stmtSelect.simpleQueryForLong();
					if(count < 0)
					{
						stmtInsert.bindLong(1, promotionAssignmentsTypeDO.promotionBrandDiscountId);
						stmtInsert.bindLong(2, promotionAssignmentsTypeDO.PromotionId);
						stmtInsert.bindString(3, promotionAssignmentsTypeDO.ItemGroupCode);
						stmtInsert.bindString(4, promotionAssignmentsTypeDO.ItemGroupLevelId);
						stmtInsert.bindString(5, promotionAssignmentsTypeDO.Discount);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindLong(1, promotionAssignmentsTypeDO.PromotionId);
						stmtUpdate.bindString(2, promotionAssignmentsTypeDO.ItemGroupCode);
						stmtUpdate.bindString(3, promotionAssignmentsTypeDO.ItemGroupLevelId);
						stmtUpdate.bindString(4, promotionAssignmentsTypeDO.Discount);
						stmtUpdate.bindLong(5, promotionAssignmentsTypeDO.promotionBrandDiscountId);
						stmtUpdate.execute();
					}
				}
				stmtInsert.close();
				stmtUpdate.close();
				stmtSelect.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
//	[PromotionBundleItemId] [int] IDENTITY(1,1) NOT NULL,
//	[PromotionDetailId] [int] NULL,
//	[ItemCode] [varchar](50) NULL,
//	[OrderType] [int] NULL,
//	[Ordered] [float] NULL,
//	[OrderUOM] [varchar](50) NULL,
//	[OfferItemCode] [varchar](50) NULL,
//	[OfferType] [int] NULL,
//	[Offered] [float] NULL,
//	[OfferUOM] [varchar](50) NULL,
//	[ModifiedDate] [int] NULL,
//	[ModifiedTime] [int] NULL)
	//tblPromotionBundledItem
	//using
	public boolean inserPromotionBundledItem(SQLiteDatabase objSqLiteDatabase, Vector<PromotionBundledItemDO> vecPromotionAssignmentsTypeDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			try
			{
				if(objSqLiteDatabase == null)
					objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert = objSqLiteDatabase.compileStatement("INSERT INTO tblPromotionBundledItem " +
						"(PromotionBundleItemId,PromotionDetailId,ItemCode,OrderType,Ordered,OrderUOM,OfferItemCode,OfferType,Offered,OfferUOM) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtSelect = objSqLiteDatabase.compileStatement("SELECT COUNT(*) FROM tblPromotionBundledItem WHERE PromotionBundleItemId = ?");
				
				SQLiteStatement stmtUpdate = objSqLiteDatabase.compileStatement("UPDATE tblPromotionBundledItem " +
						"SET PromotionDetailId=?,ItemCode=?,OrderType=?,Ordered=?,OrderUOM=?,OfferItemCode=?,OfferType=?,Offered=?,OfferUOM=?  WHERE PromotionBundleItemId = ?");
				for(PromotionBundledItemDO promotionAssignmentsTypeDO : vecPromotionAssignmentsTypeDOs)
				{
					stmtSelect.bindLong(1, promotionAssignmentsTypeDO.PromotionBundleItemId);
					long count = stmtSelect.simpleQueryForLong();
					if(count < 0)
					{
						stmtInsert.bindLong(1, promotionAssignmentsTypeDO.PromotionBundleItemId);
						stmtInsert.bindLong(2, promotionAssignmentsTypeDO.PromotionDetailId);
						stmtInsert.bindString(3, promotionAssignmentsTypeDO.ItemCode);
						stmtInsert.bindLong(4, promotionAssignmentsTypeDO.OrderType);
						stmtInsert.bindString(5, promotionAssignmentsTypeDO.Ordered);
						stmtInsert.bindString(6, promotionAssignmentsTypeDO.OrderUOM);
						stmtInsert.bindString(7, promotionAssignmentsTypeDO.OfferItemCode);
						stmtInsert.bindLong(8, promotionAssignmentsTypeDO.OfferType);
						stmtInsert.bindString(9, promotionAssignmentsTypeDO.Offered);
						stmtInsert.bindString(10, promotionAssignmentsTypeDO.OfferUOM);
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindLong(1, promotionAssignmentsTypeDO.PromotionDetailId);
						stmtUpdate.bindString(2, promotionAssignmentsTypeDO.ItemCode);
						stmtUpdate.bindLong(3, promotionAssignmentsTypeDO.OrderType);
						stmtUpdate.bindString(4, promotionAssignmentsTypeDO.Ordered);
						stmtUpdate.bindString(5, promotionAssignmentsTypeDO.OrderUOM);
						stmtUpdate.bindString(6, promotionAssignmentsTypeDO.OfferItemCode);
						stmtUpdate.bindLong(7, promotionAssignmentsTypeDO.OfferType);
						stmtUpdate.bindString(8, promotionAssignmentsTypeDO.Offered);
						stmtUpdate.bindString(9, promotionAssignmentsTypeDO.OfferUOM);
						stmtUpdate.bindLong(10, promotionAssignmentsTypeDO.PromotionBundleItemId);
						stmtUpdate.execute();
					}
				}
				stmtInsert.close();
				stmtUpdate.close();
				stmtSelect.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////
	public HashMap<String, ArrayList<ProductDO>> getPromotionItems() 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorOffer =null, cursorDetails 	= 	null;
			ArrayList<ProductDO> vector 		=	new ArrayList<ProductDO>();
			
			HashMap<String, ArrayList<ProductDO>> hmOfferItems = new HashMap<String, ArrayList<ProductDO>>();
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query1 = "SELECT OI.PromotionDetailId,OI.ItemCode,OI.Quantity * TP.Multiplier AS Quantity, OI.UOM, TP.LineNo,MP.AltDescription, "+
								"TP.ItemCode,MP.PromotionID,TP.ItemCode FROM tblPromotionOfferItem OI INNER JOIN tblTempPromotionDetail TP ON TP.PromotionId = OI.PromotionDetailId " +
								"INNER JOIN tblPromotion MP ON OI.PromotionDetailId  IN (SELECT PromotionDetailId FROM tblPromotionDetail TT WHERE MP.PromotionId = TT.PromotionId) " +
								"GROUP BY OI.ItemCode, OI.Quantity * TP.Multiplier, TP.ItemCode, OI.UOM";
				
				LogUtils.errorLog("query", query1);
				cursorOffer = sqLiteDatabase.rawQuery(query1, null);
				if(cursorOffer.moveToFirst())
				{
					do
					{
						String query2 = "SELECT CT.CategoryName,TP.Description,TP.UnitPerCase,TP.ItemBatchCode,TP.UOM,TP.CaseBarCode,TP.UnitBarCode,TP.ItemType,TP.PricingKey," +
								"TP.Brand,TP.SecondaryUOM,TP.GroupId FROM tblProducts TP INNER JOIN tblCategory CT ON CT.CategoryId =TP.Category WHERE TP.ItemCode='"+cursorOffer.getString(1)+"'";
						LogUtils.errorLog("query", query2);
						cursorDetails= sqLiteDatabase.rawQuery(query2, null);
						if(cursorDetails.moveToFirst())
						{
							ProductDO promoProductDO 		=   new ProductDO();
							promoProductDO.promotionId 		= 	cursorOffer.getString(0);
							promoProductDO.SKU 				= 	cursorOffer.getString(1);
							
							promoProductDO.CategoryId 		= 	cursorDetails.getString(0);
							promoProductDO.Description 		= 	cursorDetails.getString(1);
							promoProductDO.UnitsPerCases 	= 	cursorDetails.getInt(2);
							
							promoProductDO.PromoLineNo 		= 	cursorOffer.getInt(6);
							promoProductDO.promoCode 		= 	cursorOffer.getString(7);
							
							if(cursorOffer.getString(3).equalsIgnoreCase("PCS"))
							{
								promoProductDO.preUnits			=	cursorOffer.getString(2);
								promoProductDO.totalCases		=	StringUtils.getFloat(promoProductDO.preUnits);
							}
							else
							{
								promoProductDO.preUnits			=	cursorOffer.getString(2);
								promoProductDO.totalCases		=	StringUtils.getFloat(promoProductDO.preUnits);
							}
							
							promoProductDO.BatchCode 		= 	cursorDetails.getString(3);
							promoProductDO.UOM 				= 	cursorDetails.getString(4);
							promoProductDO.CaseBarCode 		= 	cursorDetails.getString(5);
							promoProductDO.UnitBarCode 		= 	cursorDetails.getString(6);
							promoProductDO.ItemType 		= 	cursorDetails.getString(7);
							promoProductDO.PricingKey 		= 	cursorDetails.getString(8);
							
							promoProductDO.brand 			= 	cursorDetails.getString(9);
							promoProductDO.secondaryUOM 	= 	cursorDetails.getString(10);
							promoProductDO.TaxGroupCode 	= 	"0";
							promoProductDO.TaxPercentage 	= 	0;
							
							promoProductDO.units 			= 	promoProductDO.preUnits;
							promoProductDO.cases 			= 	promoProductDO.preCases;
							
							if(cursorOffer.getString(4).equalsIgnoreCase("false"))
								promoProductDO.itemPrice    = 0;
							else
								promoProductDO.itemPrice 	= 0;
							
							promoProductDO.casePrice 		= promoProductDO.itemPrice;
							promoProductDO.Discount  		= 0;
							promoProductDO.unitSellingPrice = promoProductDO.itemPrice;
							promoProductDO.totalPrice		= promoProductDO.unitSellingPrice * promoProductDO.totalCases;
							promoProductDO.invoiceAmount	= promoProductDO.unitSellingPrice * promoProductDO.totalCases;
							
							promoProductDO.recomUnits		= 0;
							promoProductDO.recomCases 		= "0";
							promoProductDO.isMusthave 		= false;
							promoProductDO.isPromotional 	= true;
							
							String strItemCode 				= cursorOffer.getString(8);
							/*********************************************************/
							if(hmOfferItems.containsKey(strItemCode))
							{
								vector = hmOfferItems.get(strItemCode);
								if(vector == null)
									vector = new ArrayList<ProductDO>();
								
								boolean isAdded = false;
								
								for(ProductDO promoProduct : vector)
								{
									if(promoProduct.SKU.equalsIgnoreCase(promoProductDO.SKU))
									{
										promoProduct.preCases 	=   ""+Math.floor(StringUtils.getFloat(promoProduct.preCases)+StringUtils.getFloat(promoProductDO.preCases));
										promoProduct.preUnits 	=   ""+(int)Math.floor(StringUtils.getFloat(promoProduct.preUnits)+StringUtils.getFloat(promoProductDO.preUnits));
										promoProduct.totalCases = 	StringUtils.getFloat(promoProduct.preCases) + (StringUtils.getFloat(promoProduct.preUnits)/promoProduct.UnitsPerCases);
										isAdded = true;
										break;
									}
								}
								if(!isAdded)
								{
									vector = hmOfferItems.get(strItemCode);
									if(vector == null)
										vector = new ArrayList<ProductDO>();
									
									vector.add(promoProductDO);
									hmOfferItems.put(strItemCode, vector);
								}
							}
							else
							{
								vector = hmOfferItems.get(strItemCode);
								if(vector == null)
									vector = new ArrayList<ProductDO>();
								
								vector.add(promoProductDO);
								hmOfferItems.put(strItemCode, vector);
							}
						}
						
						if(cursorOffer != null && !cursorOffer.isClosed())
							cursorDetails.close();
					}
					while(cursorOffer.moveToNext());
				}
				if(cursorOffer != null && !cursorOffer.isClosed())
					cursorOffer.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursorOffer != null && !cursorOffer.isClosed())
					cursorOffer.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return hmOfferItems;
		}
	}
	
	public void getPromotionInTemp(String salesmanCode,String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase=null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				sqLiteDatabase.execSQL("DELETE FROM tblTempPromotions");
				
				String query =  "INSERT INTO tblTempPromotions(PromotionID, PromotionType, FactSheet, StartDate, EndDate, MinInvoice, Discount," +
						"VisibilityCheckRequired) "+
						"SELECT DISTINCT H.PromotionID, AssignmentType, Code, H.StartDate, H.EndDate,H.Description, H.Description,H.Description " +
						"FROM tblPromotion H INNER JOIN tblPromotionAssignment A ON H.PromotionID = A.PromotionID WHERE (H.IsActive = 'true' OR H.IsActive = 'True') " +
						"AND" +
						" ( "+
						"(A.AssignmentType = '0'  AND A.Code =  (SELECT SubChannelCode FROM tblCustomer WHERE Site= '"+siteId+"' LIMIT 1))"+
						" OR "+ 
						"(A.AssignmentType = '1' AND A.Code =  (SELECT RegionCode FROM tblCustomer WHERE Site='"+siteId+"' LIMIT 1))"+ 
						" OR "+
						"(A.AssignmentType = '3' AND A.Code =  '"+siteId+"')"+
						" ) " +
						"AND Datetime(H.EndDate) > date('now')";
				
				sqLiteDatabase.execSQL(query);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public boolean insertOrderItemsInTemp(HashMap<String, Vector<ProductDO>> hmOrderItems, String orderId, String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblTempOrderItems (OrderId,SiteId,ItemCode,Cases,Units,ItemType,Status,Lineno) VALUES(?,?,?,?,?,?,?,?)");
				objSqLiteDatabase.execSQL("delete from tblTempOrderItems");
				objSqLiteDatabase.execSQL("delete from tblTempPromotionDetail");
				
				Set<String> keys = hmOrderItems.keySet();
				for(String key : keys)
				{
					Vector<ProductDO> vector = hmOrderItems.get(key);
					if(vector != null )
					{
						for(ProductDO productDO : vector)
						{
							sqLiteStatement.bindString(1, orderId);
							sqLiteStatement.bindString(2, siteId);
							sqLiteStatement.bindString(3, productDO.SKU);
							sqLiteStatement.bindString(4, ""+productDO.preUnits);
							sqLiteStatement.bindString(5, ""+(productDO.preUnits));
							sqLiteStatement.bindString(6, "O");
							sqLiteStatement.bindString(7, "1");
							sqLiteStatement.bindString(8, ""+productDO.LineNo);
							sqLiteStatement.executeInsert();
						}
					}
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	public boolean insertOrderItemsInTemp_New(HashMap<String, Vector<ProductDO>> hmOrderItems, String orderId, String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			Cursor cursor = null;
			Vector<PromotionOrderDO> vecPromo 		 = new Vector<PromotionOrderDO>();
			SQLiteStatement slLiteStatement  = null;
			try
			{
				objSqLiteDatabase 	= DatabaseHelper.openDataBase();
				String insertQuery 	= "INSERT INTO tblTempPromotionDetail(PromotionId, PromotionOrderItemId, Quantity, ItemCode, Cases, Units,LineNo,multiplier) VALUES (?,?,?,?,?,?,?,?)";
				slLiteStatement 	= objSqLiteDatabase.compileStatement(insertQuery);
				
				Set<String> keys  	= hmOrderItems.keySet();
				for(String key : keys)
				{
					Vector<ProductDO> vector = hmOrderItems.get(key);
					if(vector != null )
					{
						for(ProductDO productDO : vector)
						{
							float remainingQty= 0;
							String strInsert  =  "SELECT I.PromotionDetailId, I.PromotionOrderItemId, I.Quantity,I.UOM, O.ItemCode, O.Cases, O.Units,O.LINENO FROM " +
									             "tblTempPromotions P INNER JOIN tblPromotionOrderItem I ON I.PromotionDetailId IN (SELECT PromotionDetailId FROM tblPromotionDetail TT WHERE P.PromotionId = TT.PromotionId) " +
									             "INNER JOIN tblTempOrderItems O ON O.ItemCode = I.ItemCode WHERE I.ItemCode = '"+productDO.SKU+"' AND " +
									             "CAST(O.Units AS FLOAT) >= CAST(I.Quantity AS FLOAT)  ORDER BY CAST(I.Quantity AS INT) DESC";
							
							cursor = objSqLiteDatabase.rawQuery(strInsert, null);
							if(cursor.moveToFirst())
							{
								do
								{
									PromotionOrderDO promoProductDO   	= 	new PromotionOrderDO();
									promoProductDO.PromotionId 			= 	cursor.getString(0);
									promoProductDO.PromotionOrderItemId = 	cursor.getString(1);
									
									promoProductDO.UOM					=   cursor.getString(3);
									promoProductDO.ItemCode				=   cursor.getString(4);
									promoProductDO.LineNo				=   cursor.getString(7);
									
									if(promoProductDO.UOM.equalsIgnoreCase("PCS"))
									{
										promoProductDO.Units		=	(int)cursor.getFloat(6);
										promoProductDO.Quantity		=	(int)cursor.getFloat(2);
										
										if(remainingQty > 0)
											promoProductDO.Units 	= 	(int)remainingQty;
										
										promoProductDO.multiplier   =   (int)(promoProductDO.Units/promoProductDO.Quantity);
										if(promoProductDO.multiplier > 0)
										{
											remainingQty 			=   promoProductDO.Units - (promoProductDO.Quantity * promoProductDO.multiplier);
											vecPromo.add(promoProductDO);
											insertPromoOrderItems(slLiteStatement, promoProductDO);
										}
									}
									else
									{
										promoProductDO.Units		=	(int)cursor.getFloat(6);
										promoProductDO.Quantity		=	(int)cursor.getFloat(2);
										
										if(remainingQty > 0)
											promoProductDO.Units 	= 	(int)remainingQty;
										
										promoProductDO.multiplier   =   (int)(promoProductDO.Units/promoProductDO.Quantity);
										if(promoProductDO.multiplier > 0)
										{
											remainingQty 			=   promoProductDO.Units - (promoProductDO.Quantity * promoProductDO.multiplier);
											vecPromo.add(promoProductDO);
											insertPromoOrderItems(slLiteStatement, promoProductDO);
										}
									}
									remainingQty = 0;
								}
								while (cursor.moveToNext() && remainingQty > 0);
							}
						}
					}
				}
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	private void insertPromoOrderItems(SQLiteStatement slLiteStatement, PromotionOrderDO promoProductDO)
	{
		try 
		{
			slLiteStatement.bindString(1, promoProductDO.PromotionId);
			slLiteStatement.bindString(2, promoProductDO.PromotionOrderItemId);
			slLiteStatement.bindString(3, ""+promoProductDO.Quantity);
			slLiteStatement.bindString(4, promoProductDO.ItemCode);
			slLiteStatement.bindString(5, ""+promoProductDO.Cases);
			slLiteStatement.bindString(6, ""+promoProductDO.Units);
			slLiteStatement.bindString(7, promoProductDO.LineNo);
			slLiteStatement.bindString(8, ""+promoProductDO.multiplier);
			slLiteStatement.executeInsert();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public boolean deleteRecords(Vector<GetAllDeleteLogDO> vecAllDeleteLogDOs) {
		synchronized (vecAllDeleteLogDOs) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				for(GetAllDeleteLogDO getAllDeleteLogDO:vecAllDeleteLogDOs){
					String query=null;
					if(getAllDeleteLogDO.Action!=null && getAllDeleteLogDO.Action.equalsIgnoreCase(GetAllDeleteLogDO.Action_DELETE)){
						if(getAllDeleteLogDO.Module!=null){
//							if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION)){
//								query="delete from tblPromotion where PromotionId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_DETAIL)){
//								query="delete from tblPromotionDetail where PromotionDetailId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_ASSIGNMENT)){
//								query="delete from tblPromotionAssignment where PromotionAssignmentId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_BRAND_DISCOUNT)){
//								query="delete from tblPromotionBrandDiscount where PromotionBrandDiscountId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_BUNDLED_ITEM)){
//								query="delete from tblPromotionBundledItem where PromotionBundleItemId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_INSTANT_ITEM)){
//								query="delete from tblPromotionInstantItem where PromotionInstantItemId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_OFFER_ITEM)){
//								query="delete from tblPromotionOfferItem where PromotionOfferItemId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_PROMOTION_ORDER_ITEM)){
//								query="delete from tblPromotionOrderItem where PromotionOrderItemId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_CUSTOMER_DCO)){
//								query="delete from tblCustomerDoc where CustomerCode='"+getAllDeleteLogDO.EntityId+"' and ImagePath='"+getAllDeleteLogDO.EntityId2+"'";
//							}
//							else
							if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_DAILYJOURNEYPLAN))
							{
								query="delete from tblDailyJourneyPlan where UserCode='"+getAllDeleteLogDO.EntityId+"' and ClientCode='"+getAllDeleteLogDO.EntityId2+"' and JourneyDate like '"+getAllDeleteLogDO.EntityId3+"%'";
							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_ITEM)){
//								query="delete from tblProducts where ItemCode='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_GROUP_SELLING_SKU_CLASSIFICTION)){
//								query="delete from tblGroupSellingSKUClassification where SellingSKUClassificationId='"+getAllDeleteLogDO.EntityId+"' and SellingSKUId='"+getAllDeleteLogDO.EntityId2+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_GROUP_SELLING_SKU)){
//								query="delete from tblSellingSKU where SellingSKUId='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_CUSTOMER)){
//								query="delete from tblCustomer where Site ='"+getAllDeleteLogDO.EntityId+"'";
//							}
//							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_SPCAIL_PRICE)){
//								query="delete from tblSpecialPricingItem where SpecialPricingItemId ='"+getAllDeleteLogDO.EntityId+"'";
//							}
							else if(getAllDeleteLogDO.Module.equalsIgnoreCase(GetAllDeleteLogDO.MODULE_DISCOUNT_MASTER)){
								query="delete from tblDiscountMaster where DiscountId ='"+getAllDeleteLogDO.EntityId+"'";
							}
						}
					}
					if(!TextUtils.isEmpty(query)){
						sqLiteDatabase.execSQL(query);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return true;
		}
	}
	
}
