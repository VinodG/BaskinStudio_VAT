package com.winit.baskinrobbin.salesman.dataaccesslayer;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.databaseaccess.DatabaseHelper;

public class ClearDataDA {

	public boolean isCanDoClearData() {
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement stmtCount = null;
			boolean isCanDoClearData = true;
			String queryPendingOrder = "Select count(*) from tblOrderHeader where Status=0";
			String queryPendingPayment = "Select count(*) from tblPaymentHeader where Status=0";
			String queryPendingCustomerVisit = "Select count(*) from tblCustomerVisit where Status=0";
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				stmtCount = sqLiteDatabase.compileStatement(queryPendingOrder);
				long count = stmtCount.simpleQueryForLong();
				if (count > 0) {
					isCanDoClearData = false;
				}
				if (isCanDoClearData) {
					stmtCount = sqLiteDatabase
							.compileStatement(queryPendingPayment);
					count = stmtCount.simpleQueryForLong();
					if (count > 0) {
						isCanDoClearData = false;
					}
				}
				if (isCanDoClearData) {
					stmtCount = sqLiteDatabase
							.compileStatement(queryPendingCustomerVisit);
					count = stmtCount.simpleQueryForLong();
					if (count > 0) {
						isCanDoClearData = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return isCanDoClearData;
		}
	}

	public void clearData() {
		
	}
}
