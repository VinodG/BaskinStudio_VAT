package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDONew;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;

public class SurveyResultDA 
{
	public boolean insertSurvey(CustomerSurveyDONew surveyDo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurvey WHERE SurveyId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurvey(SurveyId, UserCode, ClientCode, Date, Latitude, Longitude,UserRole,JourneyCode,VisitCode,Status,IsUploaded) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurvey SET UserCode = ?, ClientCode = ?, Date = ?, Latitude = ?, Longitude = ?,UserRole = ?,JourneyCode=?, VisitCode =?, Status =?, IsUploaded =? where SurveyId= ?");
				
				if(surveyDo!=null)
				{
					stmtSelectRec.bindString(1, surveyDo.serveyId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					
						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyDo.userCode);
							stmtUpdate.bindString(2, surveyDo.clientCode);
							stmtUpdate.bindString(3, surveyDo.date);
							stmtUpdate.bindString(4, surveyDo.lattitude);
							stmtUpdate.bindString(5, surveyDo.longitude);
							stmtUpdate.bindString(6, surveyDo.userRole);
							stmtUpdate.bindString(7, surveyDo.journeyCode);
							stmtUpdate.bindString(8, surveyDo.visitCode);
							stmtUpdate.bindString(9, surveyDo.status);
							stmtUpdate.bindString(10, surveyDo.isUploaded);
							stmtUpdate.bindString(11, surveyDo.serveyId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyDo.serveyId);
							stmtInsert.bindString(2, surveyDo.userCode);
							stmtInsert.bindString(3, surveyDo.clientCode);
							stmtInsert.bindString(4, surveyDo.date);
							stmtInsert.bindString(5, surveyDo.lattitude);
							stmtInsert.bindString(6, surveyDo.longitude);
							stmtInsert.bindString(7, surveyDo.userRole);
							stmtInsert.bindString(8, surveyDo.journeyCode);
							stmtInsert.bindString(9, surveyDo.visitCode);
							stmtInsert.bindString(10, surveyDo.status);
							stmtInsert.bindString(11, surveyDo.isUploaded);
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
	
	
	
	public boolean insertSurveyResult(CustomerSurveyDONew cussurveydo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyResult WHERE surveyResultid =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveyResult(SurveyResultId, SurveyId, QuestionId, Question, OptionId, Option,Comment,Status,IsUploaded) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyResult SET SurveyId = ?, QuestionId = ?, Question = ?, OptionId = ?, Option = ?,Comment = ?, Status= ?, IsUploaded =? where SurveyResultId= ?");
				
				if(cussurveydo.srveyQues!=null &&cussurveydo.srveyQues.size()>0)
				{
					
					stmtSelectRec.bindString(1, cussurveydo.resultServeyId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					for(SurveyQuestionDONew surveyQuesDo :cussurveydo.srveyQues)
					{
						
						if(countRec >0)
						{
							stmtUpdate.bindString(1, cussurveydo.serveyId);
							stmtUpdate.bindString(2, surveyQuesDo.questionId);
							stmtUpdate.bindString(3, surveyQuesDo.question);
							stmtUpdate.bindString(4, surveyQuesDo.optionId);
							stmtUpdate.bindString(5, surveyQuesDo.option);
							stmtUpdate.bindString(6, surveyQuesDo.comments);
							stmtUpdate.bindString(7, surveyQuesDo.status);
							stmtUpdate.bindString(8, surveyQuesDo.isUploaded);
							stmtUpdate.bindString(9, cussurveydo.resultServeyId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, cussurveydo.resultServeyId);
							stmtInsert.bindString(2, cussurveydo.serveyId);
							stmtInsert.bindString(3, surveyQuesDo.questionId);
							stmtInsert.bindString(4, surveyQuesDo.question);
							stmtInsert.bindString(5, surveyQuesDo.optionId);
							stmtInsert.bindString(6, surveyQuesDo.option);
							stmtInsert.bindString(7, surveyQuesDo.comments);
							stmtInsert.bindString(8, surveyQuesDo.status);
							stmtInsert.bindString(9, surveyQuesDo.isUploaded);
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
	
	
	public void updateSurvey(Vector<CustomerSurveyDONew> vecsSurveyStatusDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurvey SET SurveyId = ?, IsUploaded = ?, Status = ? WHERE SurveyAppId = ?");
				
				for (CustomerSurveyDONew cussurDo : vecsSurveyStatusDOs) 
				{
					stmtUpdate.bindString(1, cussurDo.serveyId);
					stmtUpdate.bindString(2, "1");
					stmtUpdate.bindString(3, cussurDo.status);
					stmtUpdate.bindString(4, cussurDo.SurveyAppId);
					stmtUpdate.execute();
					updateSurveyResult(cussurDo.srveyQues);
				}
				
				stmtUpdate.close();
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



	public void updateSurveyResult(Vector<SurveyQuestionDONew> srveyQues) 
	{

		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveyResult SET SurveyResultId = ?, IsUploaded = ?, Status = ? WHERE SurveyResultAppId = ?");
				
				for (SurveyQuestionDONew surqueDo : srveyQues) 
				{
					stmtUpdate.bindString(1, surqueDo.serveyresultId);
					stmtUpdate.bindString(2, "1");
					stmtUpdate.bindString(3, surqueDo.status);
					stmtUpdate.bindString(4, surqueDo.SurveyResultAppId);
					stmtUpdate.execute();
				}
				
				stmtUpdate.close();
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

}
