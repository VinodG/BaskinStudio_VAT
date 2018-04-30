package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SurveyAssignmentTypeDO;

public class SurveyAssignmentTypeDA
{

	/**
	 * Method to Insert the SurveyAssignmentType information in to SurveyAssignmentType  Table
	 * @param Assetinfo
	 */
	
	public boolean insertSurveyAssignmentType(Vector<SurveyAssignmentTypeDO> vecSurveyAssignmentType)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyAssignmentType WHERE code =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyAssignmentType(code, type) VALUES(?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyAssignmentType SET type = ? where code= ?");
				
				if(vecSurveyAssignmentType!=null && vecSurveyAssignmentType.size()>0)
				{
					
					for(SurveyAssignmentTypeDO surveyassignmenttypedo : vecSurveyAssignmentType)
					{
						stmtSelectRec.bindString(1, surveyassignmenttypedo.code);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyassignmenttypedo.type);
							stmtUpdate.bindString(2, surveyassignmenttypedo.code);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyassignmenttypedo.code);
							stmtInsert.bindString(2, surveyassignmenttypedo.type);
							stmtInsert.executeInsert();
						}
						
					}
				}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
			
		}
		
	}
	
	/* For Inserting Single Record */
	
	public boolean insertSurveyAssignmentType(SurveyAssignmentTypeDO surveyassignmenttypedo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyAssignmentType WHERE code =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyAssignmentType(code, type) VALUES(?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyAssignmentType SET type = ? where code= ?");
				
					if(surveyassignmenttypedo !=null)
					{
						stmtSelectRec.bindString(1, surveyassignmenttypedo.code);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyassignmenttypedo.type);
							stmtUpdate.bindString(2, surveyassignmenttypedo.code);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyassignmenttypedo.code);
							stmtInsert.bindString(2, surveyassignmenttypedo.type);
							stmtInsert.executeInsert();
						}
						
						
						
					}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
			
		}
		
	}
	
	public Vector<SurveyAssignmentTypeDO> getAllSurveyAssignmentType()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SurveyAssignmentTypeDO obj = null;
			Vector<SurveyAssignmentTypeDO> vecsurveyassignmenttype = new Vector<SurveyAssignmentTypeDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblSurveyAssignmentType";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new SurveyAssignmentTypeDO();
						obj.code 			      = cursor.getString(0);
						obj.type		          = cursor.getString(1);
						vecsurveyassignmenttype.add(obj);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
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
			
			return vecsurveyassignmenttype;
		}
		
	}
	
	
}
