package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ClearDataDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;


public class ResetActivity extends BaseActivity {

	LinearLayout llreset;
	Button btnSubmit;
	@Override
	public void initialize() {
		llreset = (LinearLayout)inflater.inflate(R.layout.activity_device_activation,null); 
		llBody.addView(llreset,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llHeader.setVisibility(View.GONE);
		intializeControls();
		btnSubmit.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) 
			{
				//show popup for reset permission
				showCustomDialog(ResetActivity.this, "Warning !", "Do you want to reset the stock ?", "Yes", "No", "ResetStock", true);
			}
		});
	}
	private void intializeControls() {
		btnSubmit					=	(Button)llreset.findViewById(R.id.btnSubmit);
	}
	
	private boolean isCanDoClearData=true;
	private boolean isDataCleared=false;
	private boolean isErrorWhilUploading=false;
	
	protected void clearData() {
		final ClearDataDA clearDataDA=new ClearDataDA();
		isCanDoClearData=true;
		isDataCleared=false;
		isErrorWhilUploading=false;
		try {
			if(isNetworkConnectionAvailable(this)){
				showLoader(getString(R.string.please_wait));
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
						try {
							
//							isClearDataAllowed=new ConnectionHelper(ResetActivity.this).sendRequest(ResetActivity.this,BuildXMLRequest.getClearDataPermission(preference.getStringFromPreference(Preference.EMP_NO, "")), ServiceURLs.GetClearDataPermission, preference);
//							if(isClearDataAllowed)
//							{
//								new UploadSQLite().uploadSQLite(ManageSpaceActivity.this, ServiceURLs.UploadSQLiteFromSettings, new UploadDataListener() {
//									@Override
//									public void updateStatus(int status, String message) {
//										if(status == AppStatus.DEFAULT && !TextUtils.isEmpty(message))
//										{
//											isErrorWhilUploading = true;
//										}
//										else
//											isErrorWhilUploading = false;
//									}
//								});
								isErrorWhilUploading = uploadDatabaseIntoServer(ServiceURLs.UploadSQLiteFromSettings);
									
//							}
							if(!isErrorWhilUploading)
							{
								isCanDoClearData = clearDataDA.isCanDoClearData();
								if(isCanDoClearData)
								{
									clearApplicationData();
									preference.clearPreferences();
									DisplayMetrics displaymetrics = new DisplayMetrics();
									getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
									preference.saveIntInPreference("DEVICE_DISPLAY_WIDTH", displaymetrics.widthPixels);
									preference.saveIntInPreference("DEVICE_DISPLAY_HEIGHT",displaymetrics.heightPixels);
									preference.commitPreference();
									isDataCleared=true;
								}
								else
								{
									
								}	
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								//hideLoader();
//								if(!isClearDataAllowed){
//									showCustomDialog(ResetActivity.this, "Alert!", "You don't have access permissions to use clear data option.", "OK", null, "");
//								}else{
									if(isDataCleared){
										//showLoader("Resetting....");
										//hideLoader();
										
										Intent intentBrObj = new Intent();
										intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
										sendBroadcast(intentBrObj);
										
//										Intent intent=new Intent(ResetActivity.this,LoginAcivity.class);
//										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//										startActivity(intent);
//										
									}else if(!isCanDoClearData){
										Toast.makeText(ResetActivity.this, "Please try after sometime.", Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(ResetActivity.this, "Error while clearing the data, please check your internet connection and try again.", Toast.LENGTH_LONG).show();
									}
									
									finish();
//								}
							}
						});
//					}
//				}).start();
			}else{
				showCustomDialog(ResetActivity.this, "Alert!", getString(R.string.no_internet), "OK", null, "error_msg");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onButtonNoClick(String from) {
		/*super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("ResetStock")){
			Intent intent=new Intent(ResetActivity.this,LoginAcivity.class);
			startActivity(intent);
			finish();
		}*/
	}
	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("ResetStock")){
			if(isNetworkConnectionAvailable(ResetActivity.this))
				doStockReset();
			else
			{
				hideLoader();
				showCustomDialog(ResetActivity.this, getString(R.string.warning),getString(R.string.no_internet) , getString(R.string.OK), null, "");
			}
		}
		else if(from.equalsIgnoreCase("error_msg")){
			finish();
		}
		
	}
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			btnMenu.performClick();
		else 
			btnLoginLogout.performClick();
	}
	
	private void doStockReset(){
		showLoader("Resetting....");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				try 
				{
					//get the vehicle no 
					String vehicleNO= new VehicleDA().getVehicleNo(preference.getStringFromPreference(preference.EMP_NO, ""));
					BooleanParser booleanParser = new BooleanParser(ResetActivity.this);
					new ConnectionHelper(null).sendRequest(ResetActivity.this,BuildXMLRequest.VerifyResetApproved(preference.getStringFromPreference(preference.ADMIN_CODE, ""),vehicleNO,preference.getStringFromPreference(preference.EMP_NO, "")), booleanParser, ServiceURLs.ResetVanstock,preference);
					int resposne = (Integer) booleanParser.getData();
					if(resposne==1){
						clearData();
//						hideLoader();
					}
					else
					{
//						showCustomDialog(ResetActivity.this, getString(R.string.warning), booleanParser.getServerMessage(), getString(R.string.OK), null, "error_msg",false);
						Intent intent=new Intent();
						intent.putExtra("error_reset_stock", booleanParser.getServerMessage());
						intent.setAction(AppConstants.ACTION_LOGOUT);
						sendBroadcast(intent);
					}
				}
				catch (Exception e)
				{
				e.printStackTrace();	// TODO: handle exception
//				hideLoader();
				}
				finally{
					hideLoader();
				}
			}
		}).start();
	}
	
}
