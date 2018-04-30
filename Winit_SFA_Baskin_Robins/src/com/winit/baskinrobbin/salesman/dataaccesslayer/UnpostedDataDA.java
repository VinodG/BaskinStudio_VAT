package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.CheckDBHelper;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;


public class UnpostedDataDA 
{
	public Vector<PaymentHeaderDO> getAllPaymentsUnload(Context mContext, String path)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<PaymentHeaderDO> vecPaymentHeaderDOs = new Vector<PaymentHeaderDO>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase  =  new CheckDBHelper(mContext, path).openDataBase();
				String strQuery =  "SELECT * FROM tblPaymentHeader WHERE Status = 0";
				
				cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						PaymentHeaderDO uploadedDataDO	= 	new PaymentHeaderDO();
						uploadedDataDO.Status = "" ;
						vecPaymentHeaderDOs.add(uploadedDataDO);
					}
					while(cursor.moveToNext());
				}
				
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecPaymentHeaderDOs;
		}
	}
}
