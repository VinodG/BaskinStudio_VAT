package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.location.Location;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class UpdateCustomerDetail extends BaseActivity implements LocationResult
{
	//initializing variables
	private TextView tvContactDetailHeader, tvCustomerName,tvCustomerNameVal, tvSiteId, tvSiteIdVal, tvLongitude, 
					 tvLongitudeDisplay, tvLatitude, tvLatitudeDisplay;
	private Button btnCaptureLocation,btnUpdate;
	
	private LinearLayout  llHouseContactDetail;
	private JourneyPlanDO objMallsDetails;
	private LocationUtility locationUtility;
	private StartTimer startTimer;
	private boolean isUpdated = false;
	
	@Override
	public void initialize()
	{
		llHouseContactDetail = (LinearLayout) inflater.inflate(R.layout.update_contact_detail, null);
		
		intialisecontrols();
		setTypeFace(llHouseContactDetail);
		tvContactDetailHeader.setText("Update Customer Details");
		if(getIntent().getExtras()!=null)
			objMallsDetails = (JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
		
		if(objMallsDetails != null)
		{
			tvLatitudeDisplay.setText(String.valueOf(objMallsDetails.geoCodeX));
			tvLongitudeDisplay.setText(String.valueOf(objMallsDetails.geoCodeY));
			tvCustomerNameVal.setText(objMallsDetails.siteName);
			tvSiteIdVal.setText(objMallsDetails.site);
		}

		locationUtility  = new LocationUtility(UpdateCustomerDetail.this);
		btnCaptureLocation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(startTimer != null)
					startTimer.cancel();
				
				startTimer = new StartTimer(20000, 10000);
				startTimer.start();
				
				if(isNetworkConnectionAvailable(UpdateCustomerDetail.this) || isGPSEnable(UpdateCustomerDetail.this))
				{
					showLoader(getString(R.string.Capturing_please_wait));
					locationUtility .getLocation(UpdateCustomerDetail.this);
				}
				else
					showCustomDialog(UpdateCustomerDetail.this, getString(R.string.warning), "Please check your internet connection.", getString(R.string.OK), null, "");
			}
		});
		btnUpdate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				isUpdated = false;
				
				if(tvLatitudeDisplay.getText() == null || tvLatitudeDisplay.getText().toString().equalsIgnoreCase("") || tvLongitudeDisplay.getText() == null || tvLongitudeDisplay.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(UpdateCustomerDetail.this, getString(R.string.warning), "Please capture the location.", getString(R.string.OK), null, "");
				else if(!isNetworkConnectionAvailable(UpdateCustomerDetail.this))
					showCustomDialog(UpdateCustomerDetail.this, getString(R.string.warning), getString(R.string.no_internet), getString(R.string.OK), null, "");
				else
				{
					showLoader("Updating...");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							objMallsDetails.geoCodeX 	= ""+StringUtils.getFloat(tvLatitudeDisplay.getText().toString());
							objMallsDetails.geoCodeY 	= ""+StringUtils.getFloat(tvLongitudeDisplay.getText().toString());
							String emp = preference.getStringFromPreference(Preference.EMP_NO, "");
							
							if(isNetworkConnectionAvailable(UpdateCustomerDetail.this))
							{
								ConnectionHelper connectionHelper = new ConnectionHelper(UpdateCustomerDetail.this);
								isUpdated = connectionHelper.sendRequest(UpdateCustomerDetail.this,BuildXMLRequest.insertCustomerGeoCode(objMallsDetails, emp), ServiceURLs.InsertCustomerGeoCode, preference);
								
								if(isUpdated)
									isUpdated = new CustomerDetailsDA().updateCustomerSiteGEOLocation(objMallsDetails);
							}
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									if(isUpdated)
										showCustomDialog(UpdateCustomerDetail.this, getString(R.string.successful), "Customer details has been updated successfully.", "OK", null, "updated");
									else
										showCustomDialog(UpdateCustomerDetail.this, getString(R.string.warning), "Error occurred while updating customer detail, Please try again.", "OK", null, "");
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		
		//Inflating layout
		llBody.addView(llHouseContactDetail, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	//getting id's and set type-faces
	public void intialisecontrols()
	{
		btnUpdate	   			= 	(Button)llHouseContactDetail.findViewById(R.id.btnUpdate);
		btnCaptureLocation	   	= 	(Button)llHouseContactDetail.findViewById(R.id.btnCaptureLocation);
		tvContactDetailHeader 	= 	(TextView)llHouseContactDetail.findViewById(R.id.tvContactDetailHeader);
		tvCustomerName  		= 	(TextView)llHouseContactDetail.findViewById(R.id.tvCustomerName);
		tvCustomerNameVal  		= 	(TextView)llHouseContactDetail.findViewById(R.id.tvCustomerNameVal);
		tvSiteId   				= 	(TextView)llHouseContactDetail.findViewById(R.id.tvSiteId);
		tvSiteIdVal  		 	= 	(TextView)llHouseContactDetail.findViewById(R.id.tvSiteIdVal);
		tvLongitude  		 	= 	(TextView)llHouseContactDetail.findViewById(R.id.tvLongitude);
		tvLongitudeDisplay  	= 	(TextView)llHouseContactDetail.findViewById(R.id.tvLongitudeDisplay);
		tvLatitude  		 	= 	(TextView)llHouseContactDetail.findViewById(R.id.tvLatitude);
		tvLatitudeDisplay  		= 	(TextView)llHouseContactDetail.findViewById(R.id.tvLatitudeDisplay);
		
		
	/*	btnUpdate.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCaptureLocation.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvContactDetailHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerNameVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSiteId.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSiteIdVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLongitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLongitudeDisplay.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLatitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLatitudeDisplay.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
   }
	
	@Override
	public void onButtonYesClick(String from)
	{
		if(from.equalsIgnoreCase("updated"))
		{
			setResult(5000);
			Intent intent = new Intent(UpdateCustomerDetail.this, SalesmanCheckIn.class);
			intent.putExtra("mallsDetails", objMallsDetails);
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			if(startTimer != null)
				startTimer.cancel();
			
			locationUtility.stopGpsLocUpdation();
			preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, ""+Double.parseDouble(diffLagLong.format(loc.getLatitude())));
			preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, ""+Double.parseDouble(diffLagLong.format(loc.getLongitude())));
			preference.commitPreference();
			
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run()
				{
					hideLoader();
					tvLatitudeDisplay.setText(""+preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 99.9999));
					tvLongitudeDisplay.setText(""+preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 99.9999));
				}
			});
		}
	}
    
    public class StartTimer extends CountDownTimer
	{
		public StartTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			hideLoader();
			locationUtility.stopGpsLocUpdation();
			showCustomDialog(UpdateCustomerDetail.this, "Sorry !", "Unable to capture your current location. Please check your location/GPS settings and try again. ", getString(R.string.OK),  null, "");
		}
		@Override
		public void onTick(long millisUntilFinished) 
		{
			LogUtils.errorLog("millisUntilFinished", ""+millisUntilFinished);
		}
	}
}
