package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.UploadData;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataobject.JouneyStartDO;
import com.winit.baskinrobbin.salesman.dataobject.VerifyRequestDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class OdometerReadingActivity extends BaseActivity
{
	private LinearLayout llOdometerReading, llEndDay, llStartDay, llTotalValue;
	
	private TextView tvLu, tvStartDay, tvTotal, tvtvTotalValue, tvEndDayTimeValuew, tvEndDayTime, tvEndDayReading,
					tvEndOfDay, tvStartDayTimeValuew, tvStartDayTime, tvStartDayReading, tvStartDayTimeValuewResion,
					tvEndDayTimeValuewResion;

	private EditText etEndDayReading, etStartDayReading;
	private Button btnFinish;
	private boolean isStartDay = false;
	private String imagePath, imagePathDriver;
	
	@Override
	public void initialize()
	{
		llOdometerReading = (LinearLayout) getLayoutInflater().inflate(R.layout.odometer_reading, null);
		llBody.addView(llOdometerReading, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	
		initializeControles();
		//tblJourney
		if(getIntent().getExtras() != null)
		{
			isStartDay = getIntent().getExtras().getBoolean("isStartDay");
			
			imagePath = getIntent().getExtras().getString("image_path");
			imagePathDriver = getIntent().getExtras().getString("image_path_driver");
		}
		
		if(isStartDay)
		{
			llEndDay.setVisibility(View.GONE);
			llTotalValue.setVisibility(View.GONE);
			tvStartDayTimeValuew.setText(CalendarUtils.getCurrentTime().split(" ")[0]);
			tvStartDayTimeValuewResion.setText(CalendarUtils.getCurrentTime().split(" ")[1]);
		}
		else
		{
			if(preference.getStringFromPreference(Preference.STARTDAY_TIME, "") != null && !preference.getStringFromPreference(Preference.STARTDAY_TIME, "").equalsIgnoreCase(""))
			{
				if(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ").length > 1)
				{
					tvStartDayTimeValuew.setText(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ")[0]);
					tvStartDayTimeValuewResion.setText(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ")[1]);
				}
			}
			
			if(preference.getStringFromPreference(Preference.ENDAY_TIME, "") != null && !preference.getStringFromPreference(Preference.ENDAY_TIME, "").equalsIgnoreCase(""))
			{
				if(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ").length > 1)
				{
					tvEndDayTimeValuew.setText(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ")[0]);
					tvEndDayTimeValuewResion.setText(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ")[1]);
				}
			}
		}
		
		if(!isStartDay)
		{
			etStartDayReading.setEnabled(false);
			etStartDayReading.setFocusable(false);
			etStartDayReading.setFocusableInTouchMode(false);
			etStartDayReading.setCursorVisible(false);
			etStartDayReading.setSingleLine(false);
			etStartDayReading.clearFocus();
		}
		
		tvEndDayTimeValuew.setText(CalendarUtils.getCurrentTime().split(" ")[0]);
		tvEndDayTimeValuewResion.setText(CalendarUtils.getCurrentTime().split(" ")[1]);
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isStartDay)
				{
					if(!etStartDayReading.getText().toString().equalsIgnoreCase(""))
					{
						startJouney();
						Intent intent = new Intent(OdometerReadingActivity.this,SalesmanCustomerList.class);
						intent.putExtra("Latitude", 25.522);
						intent.putExtra("Longitude", 78.522);
						startActivity(intent);
						finish();
					}
					else
						Toast.makeText(OdometerReadingActivity.this, "Please enter Start Day Odometer Reading.", Toast.LENGTH_LONG).show();
				}
				else
				{
					if(!etEndDayReading.getText().toString().equalsIgnoreCase(""))
					{
						if(StringUtils.getInt(etEndDayReading.getText().toString().trim())>StringUtils.getInt(etStartDayReading.getText().toString().trim()))
							startJouney();
						else
							showCustomDialog(OdometerReadingActivity.this, "Alert!", "End Day Odometer reading should be greater than Start Day Odometer reading.", "OK", null, "");
					}
					else
						Toast.makeText(OdometerReadingActivity.this, "Please enter End Day Odometer Reading.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		int startValue = preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0);
		int endValue   = preference.getIntFromPreference(Preference.ENDDAY_VALUE, 0);
		if(startValue > 0)
			etStartDayReading.setText(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0)+"");
		
		if(endValue >0)
		{
			etEndDayReading.setText(preference.getIntFromPreference(Preference.ENDDAY_VALUE, 0)+"");
			
			if(endValue > startValue)
				tvtvTotalValue.setText(endValue - startValue+" KM");
		}
		
		etStartDayReading.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(s.toString().trim() != null && !s.toString().trim().equals(""))
				{
					preference.saveIntInPreference(Preference.STARTDAY_VALUE, StringUtils.getInt(s.toString().trim()));
					preference.saveStringInPreference(Preference.STARTDAY_TIME, tvStartDayTimeValuew.getText().toString()+" "+tvStartDayTimeValuewResion.getText().toString());
					preference.commitPreference();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
				
			} 
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				
			}
		});
		
		etEndDayReading.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(s.toString().trim() != null && !s.toString().trim().equals(""))
				{
					tvtvTotalValue.setText(StringUtils.getInt(s.toString().trim())- StringUtils.getInt(etStartDayReading.getText().toString())+" KM");
					preference.saveIntInPreference(Preference.ENDDAY_VALUE, StringUtils.getInt(s.toString().trim()));
					preference.saveStringInPreference(Preference.ENDAY_TIME, tvEndDayTimeValuew.getText().toString()+" "+tvEndDayTimeValuewResion.getText().toString());
					preference.commitPreference();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{
			}
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		setTypeFace(llOdometerReading);
	}
	
	private void startJouney()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				final JouneyStartDO j = new JouneyStartDO();
				j.date = CalendarUtils.getCurrentDateAsString();
				if(isStartDay)
				{
					j.startTime = j.endTime =  CalendarUtils.getCurrentDateTime();
					preference.saveBooleanInPreference(Preference.IsStockVerified, true);
					preference.saveStringInPreference(Preference.STARTDAY_TIME_ACTUAL, CalendarUtils.getCurrentDateTime());
					preference.commitPreference();
				}
				else 
				{
					j.endTime = CalendarUtils.getCurrentDateTime();
					j.startTime = CalendarUtils.getOdometerDate(preference.getStringFromPreference(Preference.STARTDAY_TIME_ACTUAL, ""));
				}
				j.IsVanStockVerified = "1";
				j.journeyAppId = StringUtils.getUniqueUUID();
				j.journeyCode = preference.getStringFromPreference(Preference.EMP_NO, "")+CalendarUtils.getCurrentDateAsString();
				
				j.odometerReading = etStartDayReading.getText().toString();
				if(isStartDay){
					j.TotalTimeInMins = 0;
				}else{
					j.TotalTimeInMins=(int) CalendarUtils.getDifferenceInMinutes(j.endTime,j.startTime);
					//j.TotalTimeInMins=StringUtils.getInt(j.endTime)-StringUtils.getInt(j.startTime);
				}
				
				j.userCode = preference.getStringFromPreference(Preference.EMP_NO, "");
				j.VerifiedBy = "";
				
				if(isStartDay)
				{
					j.StoreKeeperSignatureStartDay  = imagePath;
					j.SalesmanSignatureStartDay  	= imagePathDriver;
				}
				else
				{
					j.StoreKeeperSignatureEndDay = imagePath;
					j.SalesmanSignatureEndDay    = imagePathDriver;
				}
				j.OdometerReadingStart = etStartDayReading.getText().toString(); 
				j.OdometerReadingEnd = etEndDayReading.getText().toString(); 
				j.vehicleCode = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
//				j.OdometerReadingEnd = 0+"";
				Log.e("imagePath","imagePath = "+imagePath);
				
				if(isStartDay){
					j.endTime="1900-01-01";
					new JourneyPlanDA().insertJourneyStarts(j);
					preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
					preference.commitPreference();
				}
				else
					new JourneyPlanDA().updaateJourneyEndsnew(j);
				
//				String server_path = new UploadImage().uploadImage(OdometerReadingActivity.this, imagePath, ServiceURLs.PostTrxDetailsFromXMLWithAuth, true);
//				
//				if(server_path != null)
//					new JourneyPlanDA().updateJourneyStartSignature(server_path, j.journeyAppId);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
					    	uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
							
						if(!isStartDay)
						{
							setResult(5000);
							finish();
						}
					}
				});
			}
		}).start();
	}
	
	private void initializeControles()
	{
		tvLu = (TextView) llOdometerReading.findViewById(R.id.tvLu);
		tvStartDay  = (TextView) llOdometerReading.findViewById(R.id.tvStartDay);
		btnFinish	= (Button) llOdometerReading.findViewById(R.id.btnFinish);
	
		tvTotal					= (TextView) llOdometerReading.findViewById(R.id.tvTotal);
		tvtvTotalValue			= (TextView) llOdometerReading.findViewById(R.id.tvtvTotalValue);
		tvEndDayTimeValuew		= (TextView) llOdometerReading.findViewById(R.id.tvEndDayTimeValuew);
		tvEndDayTime			= (TextView) llOdometerReading.findViewById(R.id.tvEndDayTime);
		tvEndDayReading			= (TextView) llOdometerReading.findViewById(R.id.tvEndDayReading);
		tvEndOfDay				= (TextView) llOdometerReading.findViewById(R.id.tvEndOfDay);
		tvStartDayTimeValuew	= (TextView) llOdometerReading.findViewById(R.id.tvStartDayTimeValuew);
		tvStartDayTime			= (TextView) llOdometerReading.findViewById(R.id.tvStartDayTime);
		tvStartDayReading		= (TextView) llOdometerReading.findViewById(R.id.tvStartDayReading);			
		etEndDayReading			= (EditText) llOdometerReading.findViewById(R.id.etEndDayReading);
		etStartDayReading		= (EditText) llOdometerReading.findViewById(R.id.etStartDayReading);
		
		tvStartDayTimeValuewResion	= (TextView) llOdometerReading.findViewById(R.id.tvStartDayTimeValuewResion);
		tvEndDayTimeValuewResion	= (TextView) llOdometerReading.findViewById(R.id.tvEndDayTimeValuewResion);
		
		llEndDay				= (LinearLayout) llOdometerReading.findViewById(R.id.llEndDay);
		llStartDay				= (LinearLayout) llOdometerReading.findViewById(R.id.llStartDay);
		llTotalValue			= (LinearLayout) llOdometerReading.findViewById(R.id.llTotalValue);
		
		blockMenu();
	}
	/*private int uploadstartday(JouneyStartDO journey) 
	{
		try
		{
			if(journey != null)
			{
			if(new ConnectionHelper(null).sendRequest(OdometerReadingActivity.this,BuildXMLRequest.getStartJourney(journey), ServiceURLs.PostJourneyDetails, preference))
				new JourneyPlanDA().updateJourneyStartUploadStatus(true, journey.journeyAppId);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}*/
}
