package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;
import com.winit.baskinrobbin.salesman.dataobject.JouneyStartDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.RouteClientDO;
import com.winit.baskinrobbin.salesman.dataobject.RouteDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class JourneyPlanDA 
{
	private String defaultDate1="0001-01-01T00:00:00";
	private String defaultDate2="1900-01-01";
	
	public String[] getStartEndtime(String journeyCode) {
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String dates[]=new String[2];
			try {
				objSqliteDB = DatabaseHelper.openDataBase();
			//	String query= "SELECT StartTime,EndTime FROM tblJourney WHERE JourneyCode='"+journeyCode+"'  ORDER BY  EndTime DESC limit 1";
				String query= "SELECT StartTime,EndTime FROM tblJourney WHERE UserCode='"+journeyCode+"'  ORDER BY  StartTime DESC limit 1";
				cursor 	=  objSqliteDB.rawQuery(query, null);
				
				if(cursor != null && cursor.moveToFirst())
				{
				     do{
						
							/*date=cursor.getString(1).split("T");
							dates[1]=date[0];*/
							dates[0]=cursor.getString(0);
							dates[1]=cursor.getString(1);
					}while (cursor.moveToNext());
				   
				}
			} catch (Exception e) {
					e.printStackTrace();	
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return dates;
		}
	}
	public boolean insertJourneyPlan(Vector<JourneyDO> vecJourneyPlanDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblJourneyPlan WHERE JourneyPlanId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourneyPlan(JourneyPlanId,UserCode,Name,Description,StartDate, EndDate,ModifiedDate,ModifiedTime,LifeCycle,CreatedBy,ModifiedBy) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourneyPlan SET UserCode=?,Name=?,Description=?,StartDate=?, EndDate=?,ModifiedDate=?,ModifiedTime=?,LifeCycle=?,CreatedBy=?,ModifiedBy=? WHERE JourneyPlanId = ?");
				 
				for(int i=0;i<vecJourneyPlanDOs.size();i++)
				{
					JourneyDO userJourneyPlan = vecJourneyPlanDOs.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.journeyPlanId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.UserCode);
							stmtUpdate.bindString(2, userJourneyPlan.Name);
							stmtUpdate.bindString(3, userJourneyPlan.Description);
							stmtUpdate.bindString(4, userJourneyPlan.StartDate);
							stmtUpdate.bindString(5, userJourneyPlan.EndDate);
							stmtUpdate.bindString(6, userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(7, userJourneyPlan.ModifiedTime);
							stmtUpdate.bindString(8, userJourneyPlan.LifeCycle);
							stmtUpdate.bindString(9, userJourneyPlan.CreatedBy);
							stmtUpdate.bindString(10, userJourneyPlan.ModifiedBy);
							stmtUpdate.bindLong(11, userJourneyPlan.journeyPlanId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.journeyPlanId);
							stmtInsert.bindString(2, userJourneyPlan.UserCode);
							stmtInsert.bindString(3, userJourneyPlan.Name);
							stmtInsert.bindString(4, userJourneyPlan.Description);
							stmtInsert.bindString(5, userJourneyPlan.StartDate);
							stmtInsert.bindString(6, userJourneyPlan.EndDate);
							stmtInsert.bindString(7, userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(8, userJourneyPlan.ModifiedTime);
							stmtInsert.bindString(9, userJourneyPlan.LifeCycle);
							stmtInsert.bindString(10, userJourneyPlan.CreatedBy);
							stmtInsert.bindString(11, userJourneyPlan.ModifiedBy);
							
							
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
	
	public boolean insertRoute(Vector<RouteDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblRoute WHERE RouteId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblRoute(RouteId,Name,Description,JourneyPlanId,StartDay, Status,IsClone,CloneForRouteId,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblRoute SET Name=?, Description=?,JourneyPlanId=?,StartDate=?, Status=?,IsClone=?,CloneForRouteId=?,ModifiedDate=?,ModifiedTime=?  WHERE RouteId = ?");
				 
				for(int i=0;i<vecRoute.size();i++)
				{
					RouteDO userJourneyPlan = vecRoute.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.routeId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.Name);
							stmtUpdate.bindString(2, userJourneyPlan.Description);
							stmtUpdate.bindString(3, userJourneyPlan.JourneyPlanId);
							stmtUpdate.bindString(4, userJourneyPlan.StartDay);
							stmtUpdate.bindString(5, userJourneyPlan.Status);
							stmtUpdate.bindString(6, userJourneyPlan.IsClone);
							stmtUpdate.bindString(7, userJourneyPlan.CloneForRouteId);
							stmtUpdate.bindString(8, userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(9, userJourneyPlan.ModifiedTime);
							stmtUpdate.bindLong(10, userJourneyPlan.routeId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.routeId);
							stmtInsert.bindString(2, userJourneyPlan.Name);
							stmtInsert.bindString(3, userJourneyPlan.Description);
							stmtInsert.bindString(4, userJourneyPlan.JourneyPlanId);
							stmtInsert.bindString(5, userJourneyPlan.StartDay);
							stmtInsert.bindString(6, userJourneyPlan.Status);
							stmtInsert.bindString(7, userJourneyPlan.IsClone);
							stmtInsert.bindString(8, userJourneyPlan.CloneForRouteId);
							stmtInsert.bindString(9, userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(10, userJourneyPlan.ModifiedTime);
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
	
	
	public boolean insertRouteClient(Vector<RouteClientDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblRouteClient WHERE RouteClientId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblRouteClient(RouteClientId,RouteId,ClientCode,TimeIn,TimeOut,Sequence, Status,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblRouteClient SET RouteId=?,ClientCode=?,TimeIn=?,TimeOut=?,Sequence=?, Status=?,ModifiedDate=?,ModifiedTime=?  WHERE RouteClientId = ?");
				 
				for(int i=0;i<vecRoute.size();i++)
				{
					RouteClientDO userJourneyPlan = vecRoute.get(i);
					stmtSelectRec.bindLong(1, userJourneyPlan.RouteClientId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindLong(1, userJourneyPlan.RouteId);
							stmtUpdate.bindString(2, userJourneyPlan.ClientCode);
							stmtUpdate.bindString(3, userJourneyPlan.TimeIn);
							stmtUpdate.bindString(4, userJourneyPlan.TimeOut);
							stmtUpdate.bindString(5, userJourneyPlan.Sequence);
							stmtUpdate.bindString(6, userJourneyPlan.Status);
							stmtUpdate.bindString(7, ""+userJourneyPlan.ModifiedDate);
							stmtUpdate.bindString(8, ""+userJourneyPlan.ModifiedTime);
							stmtUpdate.bindLong(9, userJourneyPlan.RouteClientId);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindLong(1, userJourneyPlan.RouteClientId);
							stmtInsert.bindLong(2, userJourneyPlan.RouteId);
							stmtInsert.bindString(3, userJourneyPlan.ClientCode);
							stmtInsert.bindString(4, userJourneyPlan.TimeIn);
							stmtInsert.bindString(5, userJourneyPlan.TimeOut);
							stmtInsert.bindString(6, userJourneyPlan.Sequence);
							stmtInsert.bindString(7, userJourneyPlan.Status);
							stmtInsert.bindString(8, ""+userJourneyPlan.ModifiedDate);
							stmtInsert.bindString(9, ""+userJourneyPlan.ModifiedTime);
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

	


	public boolean insertJourneyStarts(JouneyStartDO journtyStartDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourney(JourneyId, UserCode,JourneyCode,Date,StartTime,EndTime, TotalTimeInMins," +
													"OdometerReading,IsVanStockVerified,VerifiedBy,JourneyAppId, OdometerReadingStart, OdometerReadingEnd, StoreKeeperSignatureStartDay, SalesmanSignatureStartDay, StoreKeeperSignatureEndDay, SalesmanSignatureEndDay, VehicleCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				

				stmtInsert.bindString(1, journtyStartDo.journeyAppId);
				stmtInsert.bindString(2, journtyStartDo.userCode);
				stmtInsert.bindString(3, journtyStartDo.journeyCode);
				stmtInsert.bindString(4, journtyStartDo.date);
				stmtInsert.bindString(5, journtyStartDo.startTime);
				stmtInsert.bindString(6, journtyStartDo.endTime);
				stmtInsert.bindLong(7, journtyStartDo.TotalTimeInMins);
				stmtInsert.bindString(8, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(9, journtyStartDo.IsVanStockVerified);
				stmtInsert.bindString(10, journtyStartDo.VerifiedBy);
				stmtInsert.bindString(11, journtyStartDo.journeyAppId);
				stmtInsert.bindString(12, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(13, "");
				stmtInsert.bindString(14, journtyStartDo.StoreKeeperSignatureStartDay);
				stmtInsert.bindString(15, journtyStartDo.SalesmanSignatureStartDay);
				stmtInsert.bindString(16, "");
				stmtInsert.bindString(17, "");
				stmtInsert.bindString(18, journtyStartDo.vehicleCode);
				
			/*	stmtInsert.bindString(1, journtyStartDo.journeyAppId);
				stmtInsert.bindString(2, journtyStartDo.userCode);
				stmtInsert.bindString(3, journtyStartDo.journeyCode);
				stmtInsert.bindString(4, journtyStartDo.date);
				stmtInsert.bindString(5, journtyStartDo.startTime);
				stmtInsert.bindString(6, journtyStartDo.endTime);
				stmtInsert.bindLong(7, journtyStartDo.TotalTimeInMins);
				stmtInsert.bindString(8, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(9, journtyStartDo.IsVanStockVerified);
				stmtInsert.bindString(10, journtyStartDo.VerifiedBy);
				stmtInsert.bindString(11, journtyStartDo.journeyAppId);
				stmtInsert.bindString(12, journtyStartDo.OdometerReadingStart);
				stmtInsert.bindString(13, journtyStartDo.OdometerReadingEnd);
				stmtInsert.bindString(14, journtyStartDo.StoreKeeperSignatureStartDay);
				stmtInsert.bindString(15, journtyStartDo.SalesmanSignatureStartDay);
				stmtInsert.bindString(16, journtyStartDo.StoreKeeperSignatureEndDay);
				stmtInsert.bindString(17, journtyStartDo.SalesmanSignatureEndDay);
				stmtInsert.bindString(18, journtyStartDo.vehicleCode);*/
				stmtInsert.executeInsert();
				stmtInsert.close();
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
	
	public boolean updaateJourneyEndsnew(JouneyStartDO journtyStartDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourney SET UserCode =?,EndTime =?, TotalTimeInMins =?," +
													"OdometerReading =?, OdometerReadingEnd =?, Status = ?, StoreKeeperSignatureEndDay =?, SalesmanSignatureEndDay = ?, IsVanStockVerified=? WHERE EndTime='"+defaultDate1+"' OR EndTime='"+defaultDate2+"'");  
				int count=0;
				Cursor cursor = objSqliteDB.rawQuery("SELECT  COUNT(*) FROM tblJourney where EndTime='"+defaultDate1+"' OR EndTime='"+defaultDate2+"'", null);
				if(cursor!= null && cursor.moveToNext())
					count = cursor.getInt(0);
				cursor.close();
				if(count>0){
					stmtUpdate.bindString(1, journtyStartDo.userCode);
					stmtUpdate.bindString(2, journtyStartDo.endTime);
					stmtUpdate.bindLong(3, journtyStartDo.TotalTimeInMins);
					stmtUpdate.bindString(4, journtyStartDo.odometerReading);
					stmtUpdate.bindString(5, journtyStartDo.OdometerReadingEnd);
					stmtUpdate.bindString(6, "N");
					stmtUpdate.bindString(7, journtyStartDo.StoreKeeperSignatureEndDay);
					stmtUpdate.bindString(8, journtyStartDo.SalesmanSignatureEndDay);
					stmtUpdate.bindString(9, journtyStartDo.IsVanStockVerified);
					stmtUpdate.execute();
				}
				
				
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
	public boolean updaateJourneyEnds(JouneyStartDO journtyStartDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourney(JourneyId, UserCode,JourneyCode,Date,StartTime,EndTime, TotalTimeInMins," +
				"OdometerReading,IsVanStockVerified,VerifiedBy,JourneyAppId, OdometerReadingStart, OdometerReadingEnd, StoreKeeperSignatureStartDay, SalesmanSignatureStartDay, StoreKeeperSignatureEndDay, SalesmanSignatureEndDay, VehicleCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourney SET UserCode =?,Date =?,StartTime =?, EndTime =?, TotalTimeInMins =?," +
													"OdometerReading =?,OdometerReadingStart = ?, OdometerReadingEnd =?, Status = ?, StoreKeeperSignatureEndDay =?, SalesmanSignatureEndDay = ? WHERE JourneyCode =?");  
				
				int count = 0;
				Cursor cursor = objSqliteDB.rawQuery("SELECT COUNT(*) from tblJourney", null);
				if(cursor!= null && cursor.moveToNext())
					count = cursor.getInt(0);
				cursor.close();
				if(count>0)
				{
					stmtUpdate.bindString(1, journtyStartDo.userCode);
					stmtUpdate.bindString(2, journtyStartDo.date);
					stmtUpdate.bindString(3, journtyStartDo.startTime);
					stmtUpdate.bindString(4, journtyStartDo.endTime);
					stmtUpdate.bindLong(5, journtyStartDo.TotalTimeInMins);
					stmtUpdate.bindString(6, journtyStartDo.odometerReading);
					stmtUpdate.bindString(7, journtyStartDo.OdometerReadingStart);
					stmtUpdate.bindString(8, journtyStartDo.OdometerReadingEnd);
					stmtUpdate.bindString(9, "N");
					stmtUpdate.bindString(10, journtyStartDo.StoreKeeperSignatureEndDay);
					stmtUpdate.bindString(11, journtyStartDo.SalesmanSignatureEndDay);
					stmtUpdate.bindString(12, journtyStartDo.journeyCode);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1, journtyStartDo.journeyAppId);
					stmtInsert.bindString(2, journtyStartDo.userCode);
					stmtInsert.bindString(3, journtyStartDo.journeyCode);
					stmtInsert.bindString(4, journtyStartDo.date);
					stmtInsert.bindString(5, journtyStartDo.startTime);
					stmtInsert.bindString(6, journtyStartDo.endTime);
					stmtInsert.bindLong(7, journtyStartDo.TotalTimeInMins);
					stmtInsert.bindString(8, journtyStartDo.OdometerReadingStart);
					stmtInsert.bindString(9, journtyStartDo.IsVanStockVerified);
					stmtInsert.bindString(10, journtyStartDo.VerifiedBy);
					stmtInsert.bindString(11, journtyStartDo.journeyAppId);
					stmtInsert.bindString(12, journtyStartDo.OdometerReadingStart);
					stmtInsert.bindString(13, journtyStartDo.OdometerReadingEnd);
					stmtInsert.bindString(14, ""+journtyStartDo.StoreKeeperSignatureStartDay);
					stmtInsert.bindString(15, ""+journtyStartDo.SalesmanSignatureStartDay);
					stmtInsert.bindString(16, ""+journtyStartDo.StoreKeeperSignatureEndDay);
					stmtInsert.bindString(17, ""+journtyStartDo.SalesmanSignatureEndDay);
					stmtInsert.bindString(18, journtyStartDo.vehicleCode);
					stmtInsert.executeInsert();
				}
				stmtUpdate.close();
				stmtInsert.close();
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
	
	public void updateJourneyStartSignature(int type, String VerifiedSignature, String journeyAppId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<JouneyStartDO> vecJourney = new Vector<JouneyStartDO>();
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				if(type == AppStatus.STORE_SIGN_START)
					cv.put("StoreKeeperSignatureStartDay", VerifiedSignature);
				else if(type == AppStatus.STORE_SIGN_END)
					cv.put("StoreKeeperSignatureEndDay", VerifiedSignature);
				if(type == AppStatus.SALES_SIGN_START)
					cv.put("SalesmanSignatureStartDay", VerifiedSignature);
				else if(type == AppStatus.SALES_SIGN_END)
					cv.put("SalesmanSignatureEndDay", VerifiedSignature);
				
				objSqliteDB.update("tblJourney", cv, "JourneyAppId = ?", new String[]{journeyAppId});
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
	
	public Vector<LogDO> getAllLogs()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<LogDO> vecLogs = new Vector<LogDO>();
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//String q = "Select * from tblJourney where IFNULL(Status, 'N') = 'N' limit 1";
				String q = "Select VehicleTrkingId,Type,Key,Data,USERCODE,DeviceDate from tblVehicleTracking where IFNULL(Status, 'N') = 'N' ORDER BY  DATE DESC ";
				Cursor cursor = objSqliteDB.rawQuery(q, null);
				
				if(cursor!=null)
				{
					while(cursor.moveToNext())
					{
						LogDO logDO  = new LogDO();
						logDO.logId=cursor.getString(0);
						logDO.type=cursor.getString(1);
						logDO.key=cursor.getString(2);
						logDO.data=cursor.getString(3);
						logDO.userId=cursor.getString(4);
						logDO.deviceTime=cursor.getString(5);
						vecLogs.add(logDO);
					}
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
			return vecLogs;
		}
	}
	public Vector<JouneyStartDO> getJourneyStart()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<JouneyStartDO> vecJourney = new Vector<JouneyStartDO>();
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//String q = "Select * from tblJourney where IFNULL(Status, 'N') = 'N' limit 1";
				String q = "Select * from tblJourney where IFNULL(Status, 'N') = 'N' ORDER BY  DATE DESC  limit 1 ";
				Cursor c = objSqliteDB.rawQuery(q, null);
				
				if(c!=null)
				{
					while(c.moveToNext())
					{
						JouneyStartDO jouneyStartDO  = new JouneyStartDO();
						jouneyStartDO.userCode = c.getString(1);
						jouneyStartDO.journeyCode = c.getString(2);
						jouneyStartDO.date = c.getString(3);
						jouneyStartDO.startTime = c.getString(4);
						jouneyStartDO.endTime = c.getString(5);
						jouneyStartDO.TotalTimeInMins = c.getInt(6);
						jouneyStartDO.odometerReading = c.getString(7);
						jouneyStartDO.IsVanStockVerified = c.getString(8);
						jouneyStartDO.VerifiedBy = c.getString(9);
						jouneyStartDO.journeyAppId = c.getString(10);
						jouneyStartDO.OdometerReadingStart = c.getString(13);
						jouneyStartDO.OdometerReadingEnd = c.getString(14);
						jouneyStartDO.StoreKeeperSignatureStartDay = c.getString(15);
						jouneyStartDO.SalesmanSignatureStartDay = c.getString(16);
						jouneyStartDO.StoreKeeperSignatureEndDay = c.getString(17);
						jouneyStartDO.SalesmanSignatureEndDay = c.getString(18);
						jouneyStartDO.vehicleCode = c.getString(19);
						vecJourney.add(jouneyStartDO);
					}
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
			return vecJourney;
		}
	}
	
	public void updateJourneyStartUploadStatus(boolean isUploaded, String journeyAppId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				ContentValues cv = new ContentValues();
				if(isUploaded)
					cv.put("Status", "Y");
				else
					cv.put("Status", "N");
				objSqliteDB.update("tblJourney", cv, "JourneyAppId = ?", new String[]{journeyAppId});
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
	
	public void deleteLog(String VehicleTrkingId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblVehicleTracking where VehicleTrkingId ='"+VehicleTrkingId+"'");
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
	public boolean insertDailyRouteClient(Vector<RouteClientDO> vecRoute)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
//				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblDailyJourneyPlan WHERE UserCode=? AND ClientCode = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblDailyJourneyPlan(UserCode,ClientCode,JourneyDate,RouteClientId,StartTime,EndTime," +
																				"Sequence,VisitStatus,VisitCode,IsDeleted,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				
								 
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblDailyJourneyPlan SET JourneyDate=?,RouteClientId=?,StartTime=?,EndTime=?," +
																				"Sequence=?,IsDeleted=?,ModifiedDate=?,ModifiedTime=? WHERE UserCode=? AND ClientCode = ?");
				 
				for(RouteClientDO userJourneyPlan : vecRoute)
				{
//					stmtSelectRec.bindString(1, userJourneyPlan.UserCode);
//					stmtSelectRec.bindString(2, userJourneyPlan.ClientCode);
					
//					long countRec = stmtSelectRec.simpleQueryForLong();
//					if(countRec != 0)
//					{	
						stmtUpdate.bindString(1, ""+userJourneyPlan.JourneyDate);
						stmtUpdate.bindString(2, ""+userJourneyPlan.RouteClientId);
						stmtUpdate.bindString(3, ""+userJourneyPlan.TimeIn);
						stmtUpdate.bindString(4, ""+userJourneyPlan.TimeOut);
						stmtUpdate.bindString(5, ""+userJourneyPlan.Sequence);
						stmtUpdate.bindString(6, ""+userJourneyPlan.IsDeleted);
						stmtUpdate.bindString(7, ""+userJourneyPlan.ModifiedDate);
						stmtUpdate.bindString(8, ""+userJourneyPlan.ModifiedTime);
						stmtUpdate.bindString(9, ""+userJourneyPlan.UserCode);
						stmtUpdate.bindString(10,""+userJourneyPlan.ClientCode);
//						stmtUpdate.execute();
//					}
//					else
					if(stmtUpdate.executeUpdateDelete()<=0)
					{
						stmtInsert.bindString(1, ""+userJourneyPlan.UserCode);
						stmtInsert.bindString(2, ""+userJourneyPlan.ClientCode);
						stmtInsert.bindString(3, ""+userJourneyPlan.JourneyDate);
						stmtInsert.bindString(4, ""+userJourneyPlan.RouteClientId);
						stmtInsert.bindString(5, ""+userJourneyPlan.TimeIn);
						stmtInsert.bindString(6, ""+userJourneyPlan.TimeOut);
						stmtInsert.bindString(7, ""+userJourneyPlan.Sequence);
						stmtInsert.bindString(8, ""+userJourneyPlan.Status);
						stmtInsert.bindString(9, ""+userJourneyPlan.VisitCode);
						stmtInsert.bindString(10, ""+userJourneyPlan.IsDeleted);
						stmtInsert.bindString(11, ""+userJourneyPlan.ModifiedDate);
						stmtInsert.bindString(12, ""+userJourneyPlan.ModifiedTime);
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
	public int isDayStarted(String stringFromPreference) {

		synchronized (MyApplication.MyLock)
		{
			int dayStarted = 0;
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT OdometerReadingStart FROM tblJourney WHERE EndTime='"+defaultDate1+"' OR EndTime='"+defaultDate2+"'  AND UserCode='"+stringFromPreference+"'";
				
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					dayStarted = cursor.getInt(0);
				}else{
					dayStarted=0;
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			return dayStarted;
		}
	
	}
}
