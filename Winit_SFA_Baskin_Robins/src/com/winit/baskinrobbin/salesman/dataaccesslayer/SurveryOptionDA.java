package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SurveryOptionDO;

public class SurveryOptionDA
{

	/**
	 * Method to Insert the Survey information in to SurveyOption Table
	 * @param Assetinfo
	 */
	
	public boolean insertSurveyOption(Vector<SurveryOptionDO> vecSurveyOptionDo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveryOption WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveryOption(questionId, optionId, option, modifiedDate, modifiedTime) VALUES(?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveryOption SET optionId = ?, option = ?, modifiedDate = ?, modifiedTime = ? where questionId= ?");
				
				if(vecSurveyOptionDo!=null && vecSurveyOptionDo.size()>0)
				{
					
					for(SurveryOptionDO surveyoptiondo : vecSurveyOptionDo)
					{
						stmtSelectRec.bindString(1, surveyoptiondo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyoptiondo.optionId);
							stmtUpdate.bindString(2, surveyoptiondo.option);
							stmtUpdate.bindString(3, surveyoptiondo.modifiedDate);
							stmtUpdate.bindString(4, surveyoptiondo.modifiedTime);
							stmtUpdate.bindString(5, surveyoptiondo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyoptiondo.questionId);
							stmtInsert.bindString(2, surveyoptiondo.optionId);
							stmtInsert.bindString(3, surveyoptiondo.option);
							stmtInsert.bindString(4, surveyoptiondo.modifiedDate);
							stmtInsert.bindString(5, surveyoptiondo.modifiedTime);
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
	
	public boolean insertSurveyOption(SurveryOptionDO surveyoptiondo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveryOption WHERE questionId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblSurveryOption(questionId, optionId, option, modifiedDate, modifiedTime) VALUES(?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblSurveryOption SET optionId = ?, option = ?, modifiedDate = ?, modifiedTime = ? where questionId= ?");
				
					if(surveyoptiondo !=null)
					{
						stmtSelectRec.bindString(1, surveyoptiondo.questionId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, surveyoptiondo.optionId);
							stmtUpdate.bindString(2, surveyoptiondo.option);
							stmtUpdate.bindString(3, surveyoptiondo.modifiedDate);
							stmtUpdate.bindString(4, surveyoptiondo.modifiedTime);
							stmtUpdate.bindString(5, surveyoptiondo.questionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, surveyoptiondo.questionId);
							stmtInsert.bindString(2, surveyoptiondo.optionId);
							stmtInsert.bindString(3, surveyoptiondo.option);
							stmtInsert.bindString(4, surveyoptiondo.modifiedDate);
							stmtInsert.bindString(5, surveyoptiondo.modifiedTime);
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
	
	public Vector<SurveryOptionDO> getAllSurveryOptions()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			SurveryOptionDO obj = null;
			Vector<SurveryOptionDO> vecSurveyOption = new Vector<SurveryOptionDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblSurveryOption";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new SurveryOptionDO();
						obj.questionId 	 = cursor.getString(0);
						obj.optionId	 = cursor.getString(1);
						obj.option		 = cursor.getString(2);
						obj.modifiedDate = cursor.getString(3);
						obj.modifiedTime = cursor.getString(4);
						vecSurveyOption.add(obj);
						
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
			
			return vecSurveyOption;
		}
		
	}
	
	
}
