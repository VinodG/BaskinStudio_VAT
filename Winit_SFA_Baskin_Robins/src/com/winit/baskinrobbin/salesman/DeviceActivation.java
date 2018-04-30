package com.winit.baskinrobbin.salesman;

import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class DeviceActivation extends BaseActivity{

	 /** Called when the activity is first created. Order_Table*/
		private LinearLayout llDeviceActivation;
		private ConnectionHelper coh;
		private EditText etPassCode,etVehicleCode;
		private Button btnSubmit;
		private String deviceID = "";
		private BooleanParser booleanParser;
		@Override
		public void initialize() 
		{
			llDeviceActivation = (LinearLayout)inflater.inflate(R.layout.activity_device_activation, null);
			llBody.addView(llDeviceActivation,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			llHeader.setVisibility(View.GONE);
			
			TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			deviceID = telephonyManager.getDeviceId();
			coh = new ConnectionHelper(null);
			etPassCode = (EditText) findViewById(R.id.etPassCode);
			etVehicleCode = (EditText) findViewById(R.id.etVehicleCode);
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			etPassCode.setVisibility(View.GONE);
			btnSubmit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final String passcode = etPassCode.getText().toString();
					final String vehicleCode = etVehicleCode.getText().toString();
					/*if(TextUtils.isEmpty(passcode)){
						showCustomDialog(DeviceActivation.this, getString(R.string.warning),"Please enter passcode", getString(R.string.OK), null, "",false);
					}else*/ 
					if(TextUtils.isEmpty(vehicleCode)){
						showCustomDialog(DeviceActivation.this, getString(R.string.warning), "Please enter vehiclecode", getString(R.string.OK), null, "",false);
					}
					else{
						if(!isNetworkConnectionAvailable(DeviceActivation.this)){
				        		showCustomDialog(DeviceActivation.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
						}else{
							
							showLoader(getString(R.string.please_wait));
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									
									booleanParser	= new BooleanParser(DeviceActivation.this);
									coh.sendRequest(DeviceActivation.this, BuildXMLRequest.validatePassCode(deviceID,passcode,vehicleCode), booleanParser, ServiceURLs.GetUserDeviceStatusByPasscode, preference);
									int resposne = (Integer) booleanParser.getData();
									if(resposne == 1)
										showCustomDialog(DeviceActivation.this, getString(R.string.warning), booleanParser.getServerMessage().toString(), getString(R.string.OK), null, "device_active");
									else
										showCustomDialog(DeviceActivation.this, getString(R.string.warning), booleanParser.getServerMessage().toString(), getString(R.string.OK), null, "");
									hideLoader();
								}
							}).start();
						}
					}
				}
			});
			//lockDrawer("DeviceActivation");
		}
		
		@Override
		public void onButtonYesClick(String from) {
			
			if(from.equalsIgnoreCase("device_active")){
				
				preference.saveBooleanInPreference(Preference.IS_ADMINISTRATOR, false);
				preference.saveStringInPreference(Preference.USER_ID, etVehicleCode.getText().toString());
				preference.commitPreference();
				Intent intent = new Intent(DeviceActivation.this,LoginAcivity.class);
				startActivity(intent);
				finish();
			}
		}
		@Override
		public void onButtonNoClick(String from) {
			
			
		}

}
