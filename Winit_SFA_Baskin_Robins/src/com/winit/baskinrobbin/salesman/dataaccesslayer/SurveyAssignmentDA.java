package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SurveyAssignmentDO;

public class SurveyAssignmentDA
{

	/**
	 * Method to Insert the SurveyAssignment information in to SurveyAssignment Table
	 * @param Assetinfo
	 */
	
	public boolean insertSurveyAssignment(Vector<SurveyAssignmentDO> vecSurveyAssignment)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyAssignment WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyAssignment(questionId, assignmentType, code, modifiedDate, modifiedTime, surveyAssignmentId) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyAssignment SET assignmentType = ?, code = ?, modifiedDate = ?, modifiedTime = ?, surveyAssignmentId =? where questionId= ?");
				
				if(vecSurveyAssignment!=null && vecSurveyAssignment.size()>0)
				{
					
					for(SurveyAssignmentDO surveyassignmentdo : vecSurveyAssignment)
					{
						stmtSelectRec.bindString(1, surveyassignmentdo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyassignmentdo.assignmentType);
							stmtUpdate.bindString(2, surveyassignmentdo.code);
							stmtUpdate.bindString(3, surveyassignmentdo.modifiedDate);
							stmtUpdate.bindString(4, surveyassignmentdo.modifiedTime);
							stmtUpdate.bindString(5, surveyassignmentdo.surveyAssignmentId);
							stmtUpdate.bindString(6, surveyassignmentdo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyassignmentdo.questionId);
							stmtInsert.bindString(2, surveyassignmentdo.assignmentType);
							stmtInsert.bindString(3, surveyassignmentdo.code);
							stmtInsert.bindString(4, surveyassignmentdo.modifiedDate);
							stmtInsert.bindString(5, surveyassignmentdo.modifiedTime);
							stmtInsert.bindString(6, surveyassignmentdo.surveyAssignmentId);
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
	
	public boolean insertSurveyAssignment(SurveyAssignmentDO surveyassignmentdo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyAssignment WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyAssignment(questionId, assignmentType, code, modifiedDate, modifiedTime, surveyAssignmentId) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyAssignment SET assignmentType = ?, code = ?, modifiedDate = ?, modifiedTime = ?, surveyAssignmentId =? where questionId= ?");
				
					if(surveyassignmentdo !=null)
					{
						stmtSelectRec.bindString(1, surveyassignmentdo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyassignmentdo.assignmentType);
							stmtUpdate.bindString(2, surveyassignmentdo.code);
							stmtUpdate.bindString(3, surveyassignmentdo.modifiedDate);
							stmtUpdate.bindString(4, surveyassignmentdo.modifiedTime);
							stmtUpdate.bindString(5, surveyassignmentdo.surveyAssignmentId);
							stmtUpdate.bindString(6, surveyassignmentdo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyassignmentdo.questionId);
							stmtInsert.bindString(2, surveyassignmentdo.assignmentType);
							stmtInsert.bindString(3, surveyassignmentdo.code);
							stmtInsert.bindString(4, surveyassignmentdo.modifiedDate);
							stmtInsert.bindString(5, surveyassignmentdo.modifiedTime);
							stmtInsert.bindString(6, surveyassignmentdo.surveyAssignmentId);
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
	
	public Vector<SurveyAssignmentDO> getAllSurveyAssignment()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SurveyAssignmentDO obj = null;
			Vector<SurveyAssignmentDO> vecsurveyassignment = new Vector<SurveyAssignmentDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblSurveyAssignment";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new SurveyAssignmentDO();
						obj.questionId 			  = cursor.getString(0);
						obj.assignmentType		 = cursor.getString(1);
						obj.code		 		 = cursor.getString(2);
						obj.modifiedDate 		 = cursor.getString(3);
						obj.modifiedTime		 = cursor.getString(4);
						obj.surveyAssignmentId 	 = cursor.getString(5);
						vecsurveyassignment.add(obj);
						
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
			
			return vecsurveyassignment;
		}
		
	}
}
