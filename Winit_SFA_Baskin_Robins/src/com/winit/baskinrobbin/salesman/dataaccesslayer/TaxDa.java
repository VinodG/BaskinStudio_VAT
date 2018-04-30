package com.winit.baskinrobbin.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.OrgDO;
import com.winit.baskinrobbin.salesman.dataobject.SalesOrderTaxDivisionDO;
import com.winit.baskinrobbin.salesman.dataobject.SalesOrganizationDO;
import com.winit.baskinrobbin.salesman.dataobject.TaxDo;
import com.winit.baskinrobbin.salesman.dataobject.TaxGroupDo;
import com.winit.baskinrobbin.salesman.dataobject.TaxGroupTaxesDo;
import com.winit.baskinrobbin.salesman.dataobject.TaxSkuMapDo;
import com.winit.baskinrobbin.salesman.dataobject.TaxSlabDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import java.util.Vector;

/**
 * Created by rajeshc on 12/7/2017.
 */
public class TaxDa extends BaseDA {

    public boolean insertTax(Vector<TaxDo> vecTaxDos) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();

                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO Tax "
                        + "(UID, Name, ApplicableAt, DependentTaxUID,TaxCalculationType,BaseTaxRate,Status,ValidFrom,ValidUpto,ss,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(12) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE Tax "
                        + " SET Name =?,ApplicableAt=?,DependentTaxUID=?,TaxCalculationType=?,BaseTaxRate=?,Status=?,ValidFrom=?,ValidUpto=?,ss=?,"
                        + "CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from Tax where UID = ? ");

                for (TaxDo taxDo : vecTaxDos) {

                    selectStmt.bindString(1, taxDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, taxDo.Name);
                        stmtUpdate.bindString(2, taxDo.ApplicableAt);
                        stmtUpdate.bindString(3, taxDo.DependentTaxUID);
                        stmtUpdate.bindString(4, taxDo.TaxCalculationType);
                        stmtUpdate.bindString(5, taxDo.BaseTaxRate + "");
                        stmtUpdate.bindString(6, taxDo.Status);
                        stmtUpdate.bindString(7, taxDo.ValidFrom);
                        stmtUpdate.bindString(8, taxDo.ValidUpto);
                        stmtUpdate.bindString(9, taxDo.ss + "");
                        stmtUpdate.bindString(10, taxDo.CreatedTime + "");
                        stmtUpdate.bindString(11, taxDo.ModifiedTime + "");
                        stmtUpdate.bindString(12, taxDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, taxDo.UID);
                        stmtInsert.bindString(2, taxDo.Name);
                        stmtInsert.bindString(3, taxDo.ApplicableAt);
                        stmtInsert.bindString(4, taxDo.DependentTaxUID);
                        stmtInsert.bindString(5, taxDo.TaxCalculationType);
                        stmtInsert.bindString(6, taxDo.BaseTaxRate + "");
                        stmtInsert.bindString(7, taxDo.Status);
                        stmtInsert.bindString(8, taxDo.ValidFrom);
                        stmtInsert.bindString(9, taxDo.ValidUpto);
                        stmtInsert.bindString(10, taxDo.ss + "");
                        stmtInsert.bindString(11, taxDo.CreatedTime + "");
                        stmtInsert.bindString(12, taxDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public boolean insertTaxGroupDos(Vector<TaxGroupDo> vecTaxGroupDos) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();

                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO TaxGroup "
                        + "(UID, Name, Description,ss,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(6) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE TaxGroup "
                        + " SET Name =?,Description=?,ss=?,"
                        + "CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from TaxGroup where UID = ? ");

                for (TaxGroupDo taxGroupDo : vecTaxGroupDos) {

                    selectStmt.bindString(1, taxGroupDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, taxGroupDo.Name);
                        stmtUpdate.bindString(2, taxGroupDo.Description);
                        stmtUpdate.bindString(3, taxGroupDo.ss + "");
                        stmtUpdate.bindString(4, taxGroupDo.CreatedTime + "");
                        stmtUpdate.bindString(5, taxGroupDo.ModifiedTime + "");
                        stmtUpdate.bindString(6, taxGroupDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, taxGroupDo.UID);
                        stmtInsert.bindString(2, taxGroupDo.Name);
                        stmtInsert.bindString(3, taxGroupDo.Description);
                        stmtInsert.bindString(4, taxGroupDo.ss + "");
                        stmtInsert.bindString(5, taxGroupDo.CreatedTime + "");
                        stmtInsert.bindString(6, taxGroupDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public boolean insertTaxGroupTaxesDos(Vector<TaxGroupTaxesDo> vecTaxGroupTaxesDos) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();

                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO TaxGroupTaxes "
                        + "(UID, TaxGroupUID, TaxUID,ss,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(6) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE TaxGroupTaxes "
                        + " SET TaxGroupUID =?,TaxUID=?,ss=?,"
                        + "CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from TaxGroupTaxes where UID = ? ");

                for (TaxGroupTaxesDo taxGroupTaxesDo : vecTaxGroupTaxesDos) {

                    selectStmt.bindString(1, taxGroupTaxesDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, taxGroupTaxesDo.TaxGroupUID);
                        stmtUpdate.bindString(2, taxGroupTaxesDo.TaxUID);
                        stmtUpdate.bindString(3, taxGroupTaxesDo.ss + "");
                        stmtUpdate.bindString(4, taxGroupTaxesDo.CreatedTime + "");
                        stmtUpdate.bindString(5, taxGroupTaxesDo.ModifiedTime + "");
                        stmtUpdate.bindString(6, taxGroupTaxesDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, taxGroupTaxesDo.UID);
                        stmtInsert.bindString(2, taxGroupTaxesDo.TaxGroupUID);
                        stmtInsert.bindString(3, taxGroupTaxesDo.TaxUID);
                        stmtInsert.bindString(4, taxGroupTaxesDo.ss + "");
                        stmtInsert.bindString(5, taxGroupTaxesDo.CreatedTime + "");
                        stmtInsert.bindString(6, taxGroupTaxesDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }

    public boolean insertTaxSkuMapDos(Vector<TaxSkuMapDo> vecTaxSkuMapDos) {
        synchronized(MyApplication.MyLock)
        {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();

                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO TaxSkuMap "
                        + "(UID, SKUUID, TaxUID,ss,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(6) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE TaxSkuMap "
                        + " SET SKUUID =?,TaxUID=?,ss=?,"
                        + "CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from TaxSkuMap where UID = ? ");

                for (TaxSkuMapDo taxSkuMapDo : vecTaxSkuMapDos) {

                    selectStmt.bindString(1, taxSkuMapDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, taxSkuMapDo.SKUUID);
                        stmtUpdate.bindString(2, taxSkuMapDo.TaxUID);
                        stmtUpdate.bindString(3, taxSkuMapDo.ss + "");
                        stmtUpdate.bindString(4, taxSkuMapDo.CreatedTime + "");
                        stmtUpdate.bindString(5, taxSkuMapDo.ModifiedTime + "");
                        stmtUpdate.bindString(6, taxSkuMapDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, taxSkuMapDo.UID);
                        stmtInsert.bindString(2, taxSkuMapDo.SKUUID);
                        stmtInsert.bindString(3, taxSkuMapDo.TaxUID);
                        stmtInsert.bindString(4, taxSkuMapDo.ss + "");
                        stmtInsert.bindString(5, taxSkuMapDo.CreatedTime + "");
                        stmtInsert.bindString(6, taxSkuMapDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }

    public boolean insertTaxSlabDos(Vector<TaxSlabDo> vecTaxSlabDos) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();
                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO TaxSlab "
                        + "(UID, TaxUID, RangeStart,RangeEnd,TaxRate,Status,ValidFrom,ValidUpTo,ss,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(11) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE TaxSlab "
                        + " SET TaxUID =?,RangeStart=?,RangeEnd=?,TaxRate=?,Status=?,ValidFrom=?,ValidUpTo=?,ss=?"
                        + "CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from TaxSlab where UID = ? ");

                for (TaxSlabDo taxSlabDo : vecTaxSlabDos) {

                    selectStmt.bindString(1, taxSlabDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, taxSlabDo.TaxUID);
                        stmtUpdate.bindString(2, taxSlabDo.RangeStart+"");
                        stmtUpdate.bindString(3, taxSlabDo.RangeEnd+"");
                        stmtUpdate.bindString(4, taxSlabDo.TaxRate+"");
                        stmtUpdate.bindString(5, taxSlabDo.Status+"");
                        stmtUpdate.bindString(6, taxSlabDo.ValidFrom);
                        stmtUpdate.bindString(7, taxSlabDo.ValidUpTo);
                        stmtUpdate.bindString(8, taxSlabDo.ss + "");
                        stmtUpdate.bindString(9, taxSlabDo.CreatedTime + "");
                        stmtUpdate.bindString(10, taxSlabDo.ModifiedTime + "");
                        stmtUpdate.bindString(11, taxSlabDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, taxSlabDo.UID);
                        stmtInsert.bindString(2, taxSlabDo.TaxUID);
                        stmtInsert.bindString(3, taxSlabDo.RangeStart+"");
                        stmtInsert.bindString(4, taxSlabDo.RangeEnd+"");
                        stmtInsert.bindString(5, taxSlabDo.TaxRate+"");
                        stmtInsert.bindString(6, taxSlabDo.Status+"");
                        stmtInsert.bindString(7, taxSlabDo.ValidFrom);
                        stmtInsert.bindString(8, taxSlabDo.ValidUpTo);
                        stmtInsert.bindString(9, taxSlabDo.ss + "");
                        stmtInsert.bindString(10, taxSlabDo.CreatedTime + "");
                        stmtInsert.bindString(11, taxSlabDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public boolean insertOrg(Vector<OrgDO> vecOrgDo) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();

                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO Org "
                        + "(Id,UID, TaxUID, Code,Name,ParentUID,CountryUID,TaxGroupUID,CreatedTime,ModifiedTime) "
                        + "VALUES(" + getColumnsValueByCount(10) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE Org "
                        + " SET Id=?,UID=?, TaxUID=?, Code=?,Name=?,ParentUID=?,CountryUID=?,TaxGroupUID=?,CreatedTime=?,ModifiedTime=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from Org where UID = ? ");

                for (OrgDO orgDo : vecOrgDo) {

                    selectStmt.bindString(1, orgDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, ""+orgDo.Id);
                        stmtUpdate.bindString(2, orgDo.UID+"");
                        stmtUpdate.bindString(3, orgDo.TaxUID+"");
                        stmtUpdate.bindString(4, orgDo.Code+"");
                        stmtUpdate.bindString(5, orgDo.Name+"");
                        stmtUpdate.bindString(6, orgDo.ParentUID);
                        stmtUpdate.bindString(7, orgDo.CountryUID);
                        stmtUpdate.bindString(8, orgDo.TaxGroupUID + "");
                        stmtUpdate.bindString(9, orgDo.CreatedTime + "");
                        stmtUpdate.bindString(10, orgDo.ModifiedTime + "");
                        stmtUpdate.bindString(11, orgDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, ""+orgDo.Id);
                        stmtInsert.bindString(2, orgDo.UID+"");
                        stmtInsert.bindString(3, orgDo.TaxUID+"");
                        stmtInsert.bindString(4, orgDo.Code+"");
                        stmtInsert.bindString(5, orgDo.Name+"");
                        stmtInsert.bindString(6, orgDo.ParentUID);
                        stmtInsert.bindString(7, orgDo.CountryUID);
                        stmtInsert.bindString(8, orgDo.TaxGroupUID + "");
                        stmtInsert.bindString(9, orgDo.CreatedTime + "");
                        stmtInsert.bindString(10, orgDo.ModifiedTime + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public boolean insertSalesOrderTax(Vector<SalesOrderTaxDivisionDO> vecOrgDo) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();
                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO SalesOrderTax "
                        + "(Id,UID, SalesOrderUID, SalesOrderLineUID,TaxUID,TaxSlabUID,TaxAmount,TaxName,ApplicableAt,DependentTaxUID,DependentTaxName," +
                        "TaxCalculationType,TaxCalculationType,BaseTaxRate,RangeStart,RangeEnd,TaxRate) "
                        + "VALUES(" + getColumnsValueByCount(16) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE SalesOrderTax "
                        + " SET Id=?,UID=?, SalesOrderUID=?, SalesOrderLineUID=?,TaxUID=?,TaxSlabUID=?,TaxAmount=?,TaxName=?,ApplicableAt=?,DependentTaxUID=?" +
                        ",DependentTaxName=?,TaxCalculationType=?,TaxCalculationType=?,BaseTaxRate=?,RangeStart=?,RangeEnd=?,TaxRate=? WHERE UID = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from SalesOrderTax where UID = ? ");

                for (SalesOrderTaxDivisionDO orgDo : vecOrgDo) {

                    selectStmt.bindString(1, orgDo.UID);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, ""+orgDo.Id);
                        stmtUpdate.bindString(2, orgDo.UID+"");
                        stmtUpdate.bindString(3, orgDo.SalesOrderUID+"");
                        stmtUpdate.bindString(4, orgDo.SalesOrderLineUID+"");
                        stmtUpdate.bindString(5, orgDo.TaxUID+"");
                        stmtUpdate.bindString(6, orgDo.TaxSlabUID);
                        stmtUpdate.bindString(7, orgDo.TaxAmount+"");
                        stmtUpdate.bindString(8, orgDo.TaxName + "");
                        stmtUpdate.bindString(9, orgDo.ApplicableAt + "");
                        stmtUpdate.bindString(10, orgDo.DependentTaxUID + "");
                        stmtUpdate.bindString(11, orgDo.DependentTaxName);
                        stmtUpdate.bindString(12, orgDo.TaxCalculationType);
                        stmtUpdate.bindString(13, ""+orgDo.BaseTaxRate);
                        stmtUpdate.bindString(14, ""+orgDo.RangeStart);
                        stmtUpdate.bindString(15, ""+orgDo.RangeEnd);
                        stmtUpdate.bindString(16, ""+orgDo.TaxRate);
                        stmtUpdate.bindString(17, orgDo.UID);
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, ""+orgDo.Id);
                        stmtInsert.bindString(2, orgDo.UID+"");
                        stmtInsert.bindString(3, orgDo.SalesOrderUID+"");
                        stmtInsert.bindString(4, orgDo.SalesOrderLineUID+"");
                        stmtInsert.bindString(5, orgDo.TaxUID+"");
                        stmtInsert.bindString(6, orgDo.TaxSlabUID);
                        stmtInsert.bindString(7, orgDo.TaxAmount+"");
                        stmtInsert.bindString(8, orgDo.TaxName + "");
                        stmtInsert.bindString(9, orgDo.ApplicableAt + "");
                        stmtInsert.bindString(10, orgDo.DependentTaxUID + "");
                        stmtInsert.bindString(11, orgDo.DependentTaxName);
                        stmtInsert.bindString(12, orgDo.TaxCalculationType);
                        stmtInsert.bindString(13, ""+orgDo.BaseTaxRate);
                        stmtInsert.bindString(14, ""+orgDo.RangeStart);
                        stmtInsert.bindString(15, ""+orgDo.RangeEnd);
                        stmtInsert.bindString(16, ""+orgDo.TaxRate);
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public boolean insertSalesOrg(Vector<SalesOrganizationDO> vecOrgDo) {
        synchronized(MyApplication.MyLock)         {
            SQLiteDatabase objSqliteDB = null;
            try {
                objSqliteDB = DatabaseHelper.openDataBase();
                SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSalesOrganization "
                        + "(SalesOrgId,Description,Code,isActive,CurrencyCode,IsFullSyncInWifi,ERPInstance,IsVatEnabled ) "
                        + " VALUES(" + getColumnsValueByCount(8) + ")");
                SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSalesOrganization SET SalesOrgId=?,Description=?, Code=?, isActive=?,CurrencyCode=?,IsFullSyncInWifi=?,ERPInstance=?,IsVatEnabled=?  WHERE SalesOrgId = ?");

                SQLiteStatement selectStmt = objSqliteDB.compileStatement("select count(*) from tblSalesOrganization where SalesOrgId = ? ");

                for (SalesOrganizationDO orgDo : vecOrgDo) {

                    selectStmt.bindString(1, ""+orgDo.SalesOrgId);

                    if (selectStmt.simpleQueryForLong() > 0) {
                        stmtUpdate.bindString(1, ""+orgDo.SalesOrgId);
                        stmtUpdate.bindString(2, orgDo.Description+"");
                        stmtUpdate.bindString(3, orgDo.Code+"");
                        stmtUpdate.bindString(4, orgDo.isActive+"");
                        stmtUpdate.bindString(5, orgDo.CurrencyCode+"");
                        stmtUpdate.bindString(6, orgDo.IsFullSyncInWifi+"");
                        stmtUpdate.bindString(7, orgDo.ERPInstance+"");
                        stmtUpdate.bindString(8, orgDo.IsVatEnabled + "");
                        stmtUpdate.bindString(9, orgDo.SalesOrgId + "");
                        stmtUpdate.executeUpdateDelete();
                    } else {
                        stmtInsert.bindString(1, ""+orgDo.SalesOrgId);
                        stmtInsert.bindString(2, orgDo.Description+"");
                        stmtInsert.bindString(3, orgDo.Code+"");
                        stmtInsert.bindString(4, orgDo.isActive+"");
                        stmtInsert.bindString(5, orgDo.CurrencyCode+"");
                        stmtInsert.bindString(6, orgDo.IsFullSyncInWifi+"");
                        stmtInsert.bindString(7, orgDo.ERPInstance+"");
                        stmtInsert.bindString(8, orgDo.IsVatEnabled + "");
                        stmtInsert.executeInsert();
                    }
                }
                selectStmt.close();
                stmtUpdate.close();
                stmtInsert.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            finally {
                if (objSqliteDB != null) {
                    objSqliteDB.close();
                }
            }
            return true;
        }
    }
    public  boolean getActivationStatusInSalesOrgForTAX(String code){
        synchronized(MyApplication.MyLock)  {
            SQLiteDatabase mDatabase = null;
            Cursor cursor = null;
            Boolean status=false;
            try {

                mDatabase = DatabaseHelper.openDataBase();

                String query = "select IsVatEnabled from tblSalesOrganization where Code='"+code+"'";
                cursor = mDatabase.rawQuery(query, null);

                if(cursor.moveToFirst())
                {
                    SalesOrganizationDO obj = new SalesOrganizationDO();
                    status=obj.IsVatEnabled = StringUtils.getBoolean(cursor.getString(0));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            finally{
                if(cursor!=null && !cursor.isClosed())
                    cursor.close();

                if(mDatabase !=null)
                    mDatabase.close();

            }
            return status;
        }
    }

}
