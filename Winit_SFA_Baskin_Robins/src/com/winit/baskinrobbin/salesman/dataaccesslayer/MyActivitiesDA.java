package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDO;
import com.winit.baskinrobbin.salesman.dataobject.EditDO;
import com.winit.baskinrobbin.salesman.dataobject.MyActivityDO;
import com.winit.baskinrobbin.salesman.dataobject.SalesTargetDO;
import com.winit.baskinrobbin.salesman.dataobject.TaskToDoDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class MyActivitiesDA
{
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertUserInfo(MyActivityDO	myActivityDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblMyActivities WHERE taskID =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblMyActivities (activityId, taskID, isVerified, pushStatus, desccription, customerSiteID, salemanCode, latitude, langitude, strDate, strTaskName, strCustomerName) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement  stmtUpdate		= objSqliteDB.compileStatement("Update tblMyActivities set activityId = ?, isVerified = ?, pushStatus = ?, desccription = ?, customerSiteID = ?, salemanCode = ?, latitude = ?, langitude = ?, strDate = ?, strTaskName = ?, strCustomerName = ? where taskID =?");
				
				SQLiteStatement stmtImages = objSqliteDB.compileStatement("INSERT INTO tblMissionImages (MissionId,ImagePath,ServerImagePath) VALUES(?,?,?)");
				SQLiteStatement stmtUpdateImages = objSqliteDB.compileStatement("Update tblMissionImages SET ImagePath = ?,ServerImagePath = ? where MissionId = ?");
				if(myActivityDO!=null)
				{
					stmtSelectRec.bindString(1, myActivityDO.taskID);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec >0)
					{
						stmtUpdate.bindString(1,myActivityDO.activityId);
						stmtUpdate.bindString(2,myActivityDO.isVerified);
						stmtUpdate.bindString(3,myActivityDO.pushStatus);
						stmtUpdate.bindString(4,myActivityDO.desccription);
						stmtUpdate.bindString(5,myActivityDO.customerSiteID);
						stmtUpdate.bindString(6,myActivityDO.salemanCode);
						stmtUpdate.bindString(7,myActivityDO.latitude);
						stmtUpdate.bindString(8,myActivityDO.langitude);
						stmtUpdate.bindString(9,myActivityDO.strDate);
						stmtUpdate.bindString(10,myActivityDO.taskName);
						stmtUpdate.bindString(11,myActivityDO.strCustomerName);
						stmtUpdate.bindString(12,myActivityDO.taskID);
						stmtUpdate.execute();
						
						stmtUpdateImages.bindString(1,myActivityDO.imagePath);
						stmtUpdateImages.bindString(2,myActivityDO.serverimagePath);
						stmtUpdateImages.bindString(3,myActivityDO.taskID);
						stmtUpdateImages.execute();
					}
					else
					{
						stmtInsert.bindString(1,myActivityDO.activityId);
						stmtInsert.bindString(2,myActivityDO.taskID);
						stmtInsert.bindString(3,myActivityDO.isVerified);
						stmtInsert.bindString(4,myActivityDO.pushStatus);
						stmtInsert.bindString(5,myActivityDO.desccription);
						stmtInsert.bindString(6,myActivityDO.customerSiteID);
						stmtInsert.bindString(7,myActivityDO.salemanCode);
						stmtInsert.bindString(8,myActivityDO.latitude);
						stmtInsert.bindString(9,myActivityDO.langitude);
						stmtInsert.bindString(10,myActivityDO.strDate);
						stmtInsert.bindString(11,myActivityDO.taskName);
						stmtInsert.bindString(12,myActivityDO.strCustomerName);
						stmtInsert.executeInsert();
						
						stmtImages.bindString(1,myActivityDO.taskID);
						stmtImages.bindString(2,myActivityDO.imagePath);
						stmtImages.bindString(3,myActivityDO.serverimagePath);
						stmtImages.executeInsert();
					}
					
					stmtImages.close();
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
					stmtUpdateImages.close();
				}
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
	
	public Vector<SalesTargetDO> getTargetData()
	{
		synchronized(MyApplication.MyLock) 
		{
			DecimalFormat dfff = new DecimalFormat("##.##");
			dfff.setMaximumFractionDigits(2);
			dfff.setMinimumFractionDigits(2);
			
			String strQuery  = 	"SELECT * FROM tblPresellerTargetByCat LIMIT 10";
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			Vector<SalesTargetDO> vec = new Vector<SalesTargetDO>();
			try
			{
				objSqliteDB 		= 	DatabaseHelper.openDataBase();
				cursor 	 		 	= 	objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						SalesTargetDO nameIDDo 	= new SalesTargetDO();
						nameIDDo.TargetId 		= cursor.getString(0);
						nameIDDo.target 		= dfff.format(cursor.getFloat(1));
						nameIDDo.achived 		= dfff.format(cursor.getFloat(2));
						nameIDDo.cat 			= cursor.getString(3);
						nameIDDo.pending 		= dfff.format(StringUtils.getFloat(nameIDDo.target) - StringUtils.getFloat(nameIDDo.achived));
						vec.add(nameIDDo);
					}
					while(cursor.moveToNext());
					
				 }	
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return vec;
		}
	}
	public void updateRating(Vector<TaskToDoDO> vecToDoDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate1 = null, stmtUpdate2 = null, stmtUpdate3 = null;
				stmtUpdate1 = objSqliteDB.compileStatement("UPDATE tblMyActivities SET isVerified = ?, rating = ? WHERE taskID = ?");
				stmtUpdate2 = objSqliteDB.compileStatement("UPDATE tblCustomerSurvey SET isVerified = ?, rating = ? WHERE taskId = ?");
				stmtUpdate3 = objSqliteDB.compileStatement("UPDATE tblTaskToDo SET IsAcknowledge = ?, Rating = ? WHERE taskId = ?");
				for (TaskToDoDO taskToDoDO : vecToDoDOs) 
				{
					if(taskToDoDO.TaskName.equalsIgnoreCase(AppConstants.Task_Title3))
					{
						stmtUpdate2.bindString(1, taskToDoDO.IsAcknowledge);
						stmtUpdate2.bindString(2, taskToDoDO.Rating);
						stmtUpdate2.bindString(3, taskToDoDO.TaskID);
						stmtUpdate2.execute();
						
					}
					else
					{
						stmtUpdate1.bindString(1, taskToDoDO.IsAcknowledge);
						stmtUpdate1.bindString(2, taskToDoDO.Rating);
						stmtUpdate1.bindString(3, taskToDoDO.TaskID);
						stmtUpdate1.execute();
					}
					
					stmtUpdate3.bindString(1, taskToDoDO.IsAcknowledge);
					stmtUpdate3.bindString(2, taskToDoDO.Rating);
					stmtUpdate3.bindString(3, taskToDoDO.TaskID);
					stmtUpdate3.execute();
				}
				stmtUpdate1.close();
				stmtUpdate2.close();
				stmtUpdate3.close();
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
	
	public void updateTask(String strTaskID, String strDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblTaskToDo SET Status = ? WHERE taskId = ?");
				
				stmtUpdate.bindString(1, "C");
				stmtUpdate.bindString(2, strTaskID);
				stmtUpdate.execute();
				
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
	public void updateActivities(Vector<MyActivityDO> vecActivityDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblMyActivities SET pushStatus = 1 WHERE taskID = ?"); 

				for (MyActivityDO myActivityDO : vecActivityDOs) 
				{
					stmtUpdate.bindString(1, myActivityDO.taskID);
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
	public void insertCustomerServey(CustomerSurveyDO  cussurveydo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSurvey WHERE taskID =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSurvey (surveyId, taskId, olay, pantene, elle18, isAgree, spent, brand1, brand2, brand3, brand4, isVerified, isPushStatus, date, latitud, langitude, taskName, strCustomerName, lakme) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCustomerSurvey set surveyId = ?, olay = ?, pantene = ?, elle18 = ?, isAgree = ?, spent = ?, brand1 = ?, brand2 = ?, brand3 = ?, brand4 = ?, isVerified = ?, isPushStatus = ?, date = ?, latitud = ?, langitude = ?, taskName = ?, strCustomerName = ?, lakme = ? where taskId = ?");

				if(cussurveydo!=null)
				{
					stmtSelectRec.bindString(1, cussurveydo.tskId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec >0)
					{
						stmtUpdate.bindString(1,cussurveydo.serveyId);
						stmtUpdate.bindString(2,cussurveydo.Olay+"");
						stmtUpdate.bindString(3,cussurveydo.Pantene+"");
						stmtUpdate.bindString(4,cussurveydo.Elle18+"");
						stmtUpdate.bindString(5,cussurveydo.Agree+"");
						stmtUpdate.bindLong(6,cussurveydo.spent);
						stmtUpdate.bindString(7,cussurveydo.Brand1);
						stmtUpdate.bindString(8,cussurveydo.Brand2);
						stmtUpdate.bindString(9,cussurveydo.Brand3);
						stmtUpdate.bindString(10,cussurveydo.Brand4);
						stmtUpdate.bindString(11,cussurveydo.isVerified);
						stmtUpdate.bindString(12,cussurveydo.isPushStatus);
						stmtUpdate.bindString(13,cussurveydo.date);
						stmtUpdate.bindString(14,cussurveydo.latitud);
						stmtUpdate.bindString(15,cussurveydo.langitude);
						stmtUpdate.bindString(16,cussurveydo.taskName);
						stmtUpdate.bindString(17,cussurveydo.strCusomerName);
						stmtUpdate.bindString(18,cussurveydo.Lakme+"");
						stmtUpdate.bindString(19,cussurveydo.tskId);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1,cussurveydo.serveyId);
						stmtInsert.bindString(2,cussurveydo.tskId);
						stmtInsert.bindString(3,cussurveydo.Olay+"");
						stmtInsert.bindString(4,cussurveydo.Pantene+"");
						stmtInsert.bindString(5,cussurveydo.Elle18+"");
						stmtInsert.bindString(6,cussurveydo.Agree+"");
						stmtInsert.bindLong(7,cussurveydo.spent);
						stmtInsert.bindString(8,cussurveydo.Brand1);
						stmtInsert.bindString(9,cussurveydo.Brand2);
						stmtInsert.bindString(10,cussurveydo.Brand3);
						stmtInsert.bindString(11,cussurveydo.Brand4);
						stmtInsert.bindString(12,cussurveydo.isVerified);
						stmtInsert.bindString(13,cussurveydo.isPushStatus);
						stmtInsert.bindString(14,cussurveydo.date);
						stmtInsert.bindString(15,cussurveydo.latitud);
						stmtInsert.bindString(16,cussurveydo.langitude);
						stmtInsert.bindString(17,cussurveydo.taskName);
						stmtInsert.bindString(18,cussurveydo.strCusomerName);
						stmtInsert.bindString(19,cussurveydo.Lakme+"");
						stmtInsert.executeInsert();
					}
					stmtInsert.close();
					stmtUpdate.close();
					stmtSelectRec.close();
				}
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
	
	public void updateCustomerServey(Vector<CustomerSurveyDO> vecCustomerSurveyDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomerSurvey SET isPushStatus = 1 WHERE taskId = ?"); 

				for (CustomerSurveyDO customerSurveyDO : vecCustomerSurveyDOs) 
				{
					stmtUpdate.bindString(1, customerSurveyDO.tskId);
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
	public Vector<CustomerSurveyDO> getUnUploadedServey()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<CustomerSurveyDO> vecCustomerSurveyDOs = new Vector<CustomerSurveyDO>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblCustomerSurvey where isPushStatus = 0";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						CustomerSurveyDO customerSurveyDO = new CustomerSurveyDO();
						
						customerSurveyDO.serveyId		= cursor.getString(0);
						customerSurveyDO.tskId			= cursor.getString(1);
						customerSurveyDO.Olay			= StringUtils.getBoolean(cursor.getString(2));
						customerSurveyDO.Pantene		= StringUtils.getBoolean(cursor.getString(3));
						customerSurveyDO.Elle18			= StringUtils.getBoolean(cursor.getString(4));
						customerSurveyDO.Lakme			= StringUtils.getBoolean(cursor.getString(19));
						customerSurveyDO.Agree			= StringUtils.getBoolean(cursor.getString(5));
						customerSurveyDO.spent			= StringUtils.getInt(cursor.getString(6));
						customerSurveyDO.Brand1			= cursor.getString(7);
						customerSurveyDO.Brand2			= cursor.getString(8);
						customerSurveyDO.Brand3			= cursor.getString(9);
						customerSurveyDO.Brand4			= cursor.getString(10);
						customerSurveyDO.isVerified		= cursor.getString(11);
						customerSurveyDO.isPushStatus   = cursor.getString(12);
						customerSurveyDO.date			= cursor.getString(13);
						customerSurveyDO.latitud		= cursor.getString(14);
						customerSurveyDO.langitude		= cursor.getString(15);
						customerSurveyDO.taskName		= cursor.getString(16);
						vecCustomerSurveyDOs.add(customerSurveyDO);
					} while (cursor.moveToNext());
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return vecCustomerSurveyDOs;
		}
	}
	
	public CustomerSurveyDO getServeyByTaskID(String taskId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			CustomerSurveyDO customerSurveyDO = null ;;
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblCustomerSurvey where taskId = '"+taskId+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
						customerSurveyDO = new CustomerSurveyDO();
						
						customerSurveyDO.serveyId		= cursor.getString(0);
						customerSurveyDO.tskId			= cursor.getString(1);
						customerSurveyDO.Olay			= StringUtils.getBoolean(cursor.getString(2));
						customerSurveyDO.Pantene		= StringUtils.getBoolean(cursor.getString(3));
						customerSurveyDO.Elle18			= StringUtils.getBoolean(cursor.getString(4));
						customerSurveyDO.Lakme			= StringUtils.getBoolean(cursor.getString(19));
						customerSurveyDO.Agree			= StringUtils.getBoolean(cursor.getString(5));
						customerSurveyDO.spent			= StringUtils.getInt(cursor.getString(6));
						customerSurveyDO.Brand1			= cursor.getString(7);
						customerSurveyDO.Brand2			= cursor.getString(8);
						customerSurveyDO.Brand3			= cursor.getString(9);
						customerSurveyDO.Brand4			= cursor.getString(10);
						customerSurveyDO.isVerified		= cursor.getString(11);
						customerSurveyDO.isPushStatus   = cursor.getString(12);
						customerSurveyDO.date			= cursor.getString(13);
						customerSurveyDO.latitud		= cursor.getString(14);
						customerSurveyDO.langitude		= cursor.getString(15);
						customerSurveyDO.taskName		= cursor.getString(16);
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return customerSurveyDO;
		}
	}
	
	public EditDO getNote(String tskId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			EditDO editDO = new EditDO();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select desccription from tblMyActivities where taskID = '"+tskId+"'";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					editDO.strNote = cursor.getString(0);
					editDO.imagePath = getMissionImages(mDatabase, tskId);
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return editDO;
		}
	}
	
	public Vector<MyActivityDO> getServeyActivities()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<MyActivityDO> vecMActivityDOs = new Vector<MyActivityDO>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblCustomerSurvey";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						MyActivityDO activityDO = new MyActivityDO();
						activityDO.activityId		= cursor.getString(0);
						activityDO.taskID			= cursor.getString(1);
						activityDO.isVerified		= cursor.getString(11);
						activityDO.taskName	 		= cursor.getString(16);
						activityDO.rating	 		= StringUtils.getInt(cursor.getString(17));
						activityDO.strCustomerName	 		= cursor.getString(18);
						activityDO.strDate	 		= cursor.getString(13);
						vecMActivityDOs.add(activityDO);
					} while (cursor.moveToNext());
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return vecMActivityDOs;
		}
	}
	public Vector<MyActivityDO> getAllUnUploadedActivities()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<MyActivityDO> vecMActivityDOs = new Vector<MyActivityDO>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblMyActivities where pushStatus = 0";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						MyActivityDO activityDO = new MyActivityDO();
						
						activityDO.activityId     = cursor.getString(0);
						activityDO.taskID	      = cursor.getString(1);
						activityDO.isVerified     = cursor.getString(2);
						activityDO.pushStatus     = cursor.getString(3);
						activityDO.desccription   = cursor.getString(4);
						activityDO.customerSiteID = cursor.getString(5);
						activityDO.salemanCode	  = cursor.getString(6);
						
						activityDO.latitude	  = cursor.getString(7);
						activityDO.langitude  = cursor.getString(8);
						activityDO.strDate	  = cursor.getString(9);
						activityDO.taskName	  = cursor.getString(10);
						
						activityDO.imagePath	  = getMissionImages(mDatabase, activityDO.taskID);
						activityDO.serverimagePath	  = getMissionServerImages(mDatabase, activityDO.taskID);
						
						vecMActivityDOs.add(activityDO);
					} while (cursor.moveToNext());
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return vecMActivityDOs;
		}
	}
	
	public Vector<MyActivityDO> getAllActivities()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<MyActivityDO> vecMActivityDOs = new Vector<MyActivityDO>();
			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "select * from tblMyActivities";
				cursor = mDatabase.rawQuery(query, null);
				if (cursor.moveToFirst()) 
				{
					do 
					{
						MyActivityDO activityDO = new MyActivityDO();
						
						activityDO.activityId     = cursor.getString(0);
						activityDO.taskID	      = cursor.getString(1);
						activityDO.isVerified     = cursor.getString(2);
						activityDO.pushStatus     = cursor.getString(3);
						activityDO.desccription   = cursor.getString(4);
						activityDO.customerSiteID = cursor.getString(5);
						activityDO.salemanCode	  = cursor.getString(6);
						activityDO.latitude	  = cursor.getString(7);
						activityDO.langitude  = cursor.getString(8);
						activityDO.strDate	  = cursor.getString(9);
						activityDO.taskName	  = cursor.getString(10);
						activityDO.rating	  = StringUtils.getInt(cursor.getString(11));
						activityDO.strCustomerName	  = cursor.getString(12);
						activityDO.imagePath	  = getMissionImages(mDatabase, activityDO.taskID);
						
						vecMActivityDOs.add(activityDO);
					} while (cursor.moveToNext());
					
				}
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				vecMActivityDOs.addAll(getServeyActivities());
			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (mDatabase != null)
					mDatabase.close();
			}
			return vecMActivityDOs;
		}
	}
	public String getMissionImages(SQLiteDatabase mDatabase, String activityId)
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor1 = null;
			String path = "";
			try {
				if(mDatabase == null)
					mDatabase = DatabaseHelper.openDataBase();

				String query = "select ImagePath from tblMissionImages where MissionId = '"+activityId+"'";
						
				cursor1 = mDatabase.rawQuery(query, null);
				if (cursor1.moveToFirst()) 
				{
					path = cursor1.getString(0);
				}
				if (cursor1 != null && !cursor1.isClosed())
					cursor1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (cursor1 != null && !cursor1.isClosed())
					cursor1.close();
			}
			return path;
		}
	}
	
	public String getMissionServerImages(SQLiteDatabase mDatabase, String activityId)
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor1 = null;
			String path = "";
			try {
				if(mDatabase == null)
					mDatabase = DatabaseHelper.openDataBase();

				String query = "select ServerImagePath from tblMissionImages where MissionId = '"+activityId+"'";
						
				cursor1 = mDatabase.rawQuery(query, null);
				if (cursor1.moveToFirst()) 
				{
					path = cursor1.getString(0);
				}
				if (cursor1 != null && !cursor1.isClosed())
					cursor1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (cursor1 != null && !cursor1.isClosed())
					cursor1.close();
			}
			return path;
		}
	}
}
