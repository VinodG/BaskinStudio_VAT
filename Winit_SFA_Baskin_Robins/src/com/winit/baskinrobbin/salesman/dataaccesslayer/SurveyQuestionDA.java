package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SurveryOptionDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;

public class SurveyQuestionDA
{

	/**
	 * Method to Insert the SurveyQuestion information in to SurveyQuestion Table
	 * @param Assetinfo
	 */
	
	public boolean insertSurveyQuestion(Vector<SurveyQuestionDO> vecSurveyQuestion)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyQuestion WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyQuestion(questionId, question, createdBy, modifiedBy, modifiedDate, modifiedTime) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyQuestion SET question = ?, createdBy = ?, modifiedBy = ?, modifiedDate = ?, modifiedTime =? where questionId= ?");
				
				if(vecSurveyQuestion!=null && vecSurveyQuestion.size()>0)
				{
					
					for(SurveyQuestionDO surveyquestiondo : vecSurveyQuestion)
					{
						stmtSelectRec.bindString(1, surveyquestiondo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyquestiondo.question);
							stmtUpdate.bindString(2, surveyquestiondo.createdBy);
							stmtUpdate.bindString(3, surveyquestiondo.modifiedBy);
							stmtUpdate.bindString(4, surveyquestiondo.modifiedDate);
							stmtUpdate.bindString(5, surveyquestiondo.modifiedTime);
							stmtUpdate.bindString(6, surveyquestiondo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyquestiondo.questionId);
							stmtInsert.bindString(2, surveyquestiondo.question);
							stmtInsert.bindString(3, surveyquestiondo.createdBy);
							stmtInsert.bindString(4, surveyquestiondo.modifiedBy);
							stmtInsert.bindString(5, surveyquestiondo.modifiedDate);
							stmtInsert.bindString(6, surveyquestiondo.modifiedTime);
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
	
	public boolean insertSurveyQuestion(SurveyQuestionDO surveyquestiondo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyQuestion WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyQuestion(questionId, question, createdBy, modifiedBy, modifiedDate, modifiedTime) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyQuestion SET question = ?, createdBy = ?, modifiedBy = ?, modifiedDate = ?, modifiedTime =? where questionId= ?");
				
					if(surveyquestiondo !=null)
					{
						stmtSelectRec.bindString(1, surveyquestiondo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyquestiondo.question);
							stmtUpdate.bindString(2, surveyquestiondo.createdBy);
							stmtUpdate.bindString(3, surveyquestiondo.modifiedBy);
							stmtUpdate.bindString(4, surveyquestiondo.modifiedDate);
							stmtUpdate.bindString(5, surveyquestiondo.modifiedTime);
							stmtUpdate.bindString(6, surveyquestiondo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyquestiondo.questionId);
							stmtInsert.bindString(2, surveyquestiondo.question);
							stmtInsert.bindString(3, surveyquestiondo.createdBy);
							stmtInsert.bindString(4, surveyquestiondo.modifiedBy);
							stmtInsert.bindString(5, surveyquestiondo.modifiedDate);
							stmtInsert.bindString(6, surveyquestiondo.modifiedTime);
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
	
	public Vector<SurveyQuestionDO> getAllSurveyQuestions()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SurveyQuestionDO obj = null;
			Vector<SurveyQuestionDO> vecsurveyquestiondo = new Vector<SurveyQuestionDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblSurveyQuestion";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new SurveyQuestionDO();
						obj.questionId 			  = cursor.getString(0);
						obj.question			  = cursor.getString(1);
						obj.createdBy		 	  = cursor.getString(2);
						obj.modifiedBy 		      = cursor.getString(3);
						obj.modifiedDate		  = cursor.getString(4);
						obj.modifiedTime 	      = cursor.getString(5);
						vecsurveyquestiondo.add(obj);
						
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
			 
			return vecsurveyquestiondo;
		}
	}
	public Vector<SurveyQuestionDONew> getSurveyQuestions(String clientCode)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SurveyQuestionDONew obj = null;
			Vector<SurveyQuestionDONew> vecsurveyquestiondo = new Vector<SurveyQuestionDONew>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT SA.QuestionID , SA.AssignmentType, SQ.Question From tblSurveyAssignment SA inner join tblSurveyQuestion SQ on SQ.QuestionId = SA.QuestionID where SA.Code = "+clientCode;
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new SurveyQuestionDONew();
						obj.questionId 			  = cursor.getString(0);
						obj.assignmentType	 	  = cursor.getString(1);
						obj.question			  = cursor.getString(2);
						obj.srveyOpt = getSurveyOption(mDatabase, obj.questionId);
						vecsurveyquestiondo.add(obj);
						
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
			
			return vecsurveyquestiondo;
		}
	}
	
	
	public Vector<SurveryOptionDO> getSurveyOption(SQLiteDatabase mDatabase, String questionId)
	{
		synchronized (MyApplication.MyLock) {
			
			Cursor cursor = null;
			SurveryOptionDO obj = null;
			Vector<SurveryOptionDO> vecsurveyoptiondo = new Vector<SurveryOptionDO>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT OptionId, Option From tblSurveyOption where QuestionId = "+questionId;
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj 					= new SurveryOptionDO();
						obj.optionId 			= cursor.getString(0);
						obj.option			  	= cursor.getString(1);
						vecsurveyoptiondo.add(obj);
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			
			return vecsurveyoptiondo;
		}
	}
	
}
