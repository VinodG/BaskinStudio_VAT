package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.SurveyOptionDO;

public class SurveyDA 
{
	public boolean insertRouteClient(Vector<SurveyOptionDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveryOption WHERE QuestionId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSurveryOption(QuestionId,OptionId,Otpion,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblSurveryOption SET OptionId=?,Otpion=?,ModifiedDate=?,ModifiedTime=?  WHERE QuestionId = ?");
				 
				for(int i=0;i<vecRoute.size();i++)
				{
					SurveyOptionDO userJourneyPlan = vecRoute.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.questionId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindLong(1, userJourneyPlan.OptionId);
							stmtUpdate.bindString(2, userJourneyPlan.Otpion);
							stmtUpdate.bindString(3, userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(4, userJourneyPlan.ModifiedTime);
							stmtUpdate.bindLong(5, userJourneyPlan.questionId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.questionId);
							stmtInsert.bindLong(2, userJourneyPlan.OptionId);
							stmtInsert.bindString(3, userJourneyPlan.Otpion);
							stmtInsert.bindString(4, userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(5, userJourneyPlan.ModifiedTime);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}
}
