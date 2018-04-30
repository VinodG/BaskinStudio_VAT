package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.GetAllMovements;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class NotificationActivity extends BaseActivity 
{
	private Button btnYesPopup, btnNoPopup;
	private TextView tvMsg, tvTitlePopup;
	private LinearLayout llOrder_List;
	private LinearLayout llnotification;
	private static final int NOTIFICATION_TYPE_LOAD = 100;
	private static final int UPLOAD_DEBUG = 101;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		llOrder_List = (LinearLayout)getLayoutInflater().inflate(R.layout.notification_popup, null);
		setContentView(llOrder_List);
		
		btnYesPopup = (Button)findViewById(R.id.btnYesPopup);
		btnNoPopup = (Button)findViewById(R.id.btnNoPopup);
		btnNoPopup.setVisibility(View.GONE);
		btnYesPopup.setText(getString(R.string.OK)) ;
		
		tvTitlePopup = (TextView)findViewById(R.id.tvTitlePopup);
		tvMsg = (TextView)findViewById(R.id.tvMessagePopup);
		
		 String msg = getIntent().getExtras().getString("message");
		final String title = getIntent().getExtras().getString("title");
		final int notificationType = getIntent().getExtras().getInt("notificationType");
		
		tvTitlePopup.setText(title);
		tvMsg.setText(msg);
		
		btnYesPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!isNetworkConnectionAvailable(NotificationActivity.this))
					showCustomDialog(NotificationActivity.this,"Alert !", getString(R.string.no_internet), "OK", null, "");
				else
				{
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							String empNo 		= preference.getStringFromPreference(Preference.EMP_NO, "");
						    String strTitile= new SettingsDA().getSettingByName(AppConstants.UPLOAD_DEBUG_TITILE);
//							final boolean isAllUploaded = new GetOrdersToUpload(NotificationActivity.this, preference, AppStatus.TODAY_DATA).uploadOrders(empNo);
							
								
							if(notificationType == NOTIFICATION_TYPE_LOAD || title.equalsIgnoreCase("Movement Status")){
									loadAllMovements_Sync("Refreshing data...",empNo);
									sendBroadcast(new Intent(AppConstants.ACTION_LOAD_REFRESH));
									finish();
							}
							else if(title.equalsIgnoreCase(strTitile)){
								uploadDatabaseIntoSDCARD();
								finish();
							}
							
							runOnUiThread(new  Runnable() {
								public void run() {
									hideLoader();
									tvMsg.setText("");
									finish();
									
								}
							});
						
						}
					}).start();
				}
				
			}
		});
		
		setTypeFace(llOrder_List);
	}
	
	public void setTypeFace(ViewGroup group) 
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
	             ((TextView)v).setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	         else if(v instanceof ViewGroup)
	        	 setTypeFace((ViewGroup)v);
	     }
	}
	public void loadAllMovements_Sync(String mgs, String empNo)
	{
		showLoader(mgs);
		GetAllMovements getAllMovements = new GetAllMovements(NotificationActivity.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);
		
		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		new ConnectionHelper(null).sendRequest(NotificationActivity.this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus, preference);
		hideLoader();
	}

	@Override
	public void initialize() {}
}
