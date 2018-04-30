package com.winit.baskinrobbin.salesman;

import java.util.Calendar;
import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.CustumProgressBar;
import com.winit.baskinrobbin.salesman.utilities.GraphImageView;

public class VehicleList extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llTruck_List, llLayoutMiddle, llSubheader,llMonthForfarmance;
	private TextView tvHead, tvDeliveryDateValue, tvDateOfJourney, tvNoOrderFound,tvFirstSunDate,tvSecondSunDate,tvThirdSunDate,tvFourthSunDate,tvTargetAchived,tvFirstSunTarget,tvSecondSunTarget,tvThirdSunTarget,tvFourthSunTarget;
	private ListView lvTruckList;
	private TruckListAdapter orListAdapter;
	private Vector<VehicleDO> vecTruckList;
	private GraphImageView graphImageView;
	private CustumProgressBar customProgressBar;
	private int []Dates=new int[2];
	int startDate,endDate;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout to show the Trucks list
		llTruck_List = (LinearLayout)getLayoutInflater().inflate(R.layout.delivery_agent_order_list, null);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		//function for getting id's and setting type-faces
		initializeLayout();
		llLayoutMiddle.addView(lvTruckList , LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		llBody.addView(llTruck_List, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		blockMenu();
//		Toast.makeText(getApplicationContext(), "Huuuuag", Toast.LENGTH_LONG).show();
		setTypeFace(llTruck_List);
		/*
		if(preference.getIntFromPreference(Preference.SYNC_STATUS, 0)==0){
			showCustomDialog(VehicleList.this, getString(R.string.no_connectivity), "Data Transfer not done for current login \n If required Please go to settings and try again. \n ", getString(R.string.OK), null, "Syncnow");
		}*/
	}
	
	/** initializing all the Controls  of DeliveryAgentTruckList class **/
	public void initializeLayout()
	{
		//getting id's
		tvHead 				= 	(TextView)llTruck_List.findViewById(R.id.tvHead);
		tvDeliveryDateValue	= 	(TextView)llTruck_List.findViewById(R.id.tvDeliveryDateValue);
		tvDateOfJourney		= 	(TextView)llTruck_List.findViewById(R.id.tvDateOfJourney);
		tvNoOrderFound		= 	(TextView)llTruck_List.findViewById(R.id.tvNoOrderFound);
		llLayoutMiddle 		=	(LinearLayout)llTruck_List.findViewById(R.id.llLayoutMiddle);
		llSubheader			=	(LinearLayout)llTruck_List.findViewById(R.id.llDateSelector);
		
		graphImageView 		= 	(GraphImageView)llTruck_List. findViewById(R.id.imageView);
		customProgressBar	= 	(CustumProgressBar) llTruck_List.findViewById(R.id.customProgressBar);
		tvFirstSunDate		=   (TextView) llTruck_List.findViewById(R.id.tvFirstSunDate);
		tvSecondSunDate		=   (TextView) llTruck_List.findViewById(R.id.tvSecondSunDate);
		tvThirdSunDate		=   (TextView) llTruck_List.findViewById(R.id.tvThirdSunDate);
		tvFourthSunDate		=   (TextView) llTruck_List.findViewById(R.id.tvFourthSunDate);
		tvTargetAchived		=   (TextView) llTruck_List.findViewById(R.id.tvTargetAchived);
		tvFirstSunTarget	=   (TextView) llTruck_List.findViewById(R.id.tvFirstSunTarget);
		tvSecondSunTarget	=   (TextView) llTruck_List.findViewById(R.id.tvSecondSunTarget);
		tvThirdSunTarget	=   (TextView) llTruck_List.findViewById(R.id.tvThirdSunTarget);
		tvFourthSunTarget	=   (TextView) llTruck_List.findViewById(R.id.tvFourthSunTarget);
		llMonthForfarmance  =   (LinearLayout) llTruck_List.findViewById(R.id.llMonthForfarmance);
		
		Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month 	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    
		//setting Text and type-faces
		tvHead.setText("Vehicle List");
		tvDateOfJourney.setText(CalendarUtils.getMonthFromNumber(month+1)+" "+day+CalendarUtils.getDateNotation(day)+", "+year);
		tvDeliveryDateValue.setText("Vehicle List");
		llSubheader.setVisibility(View.GONE);
		tvDeliveryDateValue.setVisibility(View.GONE);
		
		//setting visibilities 
		tvDateOfJourney.setVisibility(View.VISIBLE);
		
		//creating object of the Adapter class
		orListAdapter 		= 	new TruckListAdapter(new Vector<VehicleDO>());
		//initializing the List View and setting some properties
		lvTruckList 		= 	new ListView(VehicleList.this);
		lvTruckList.setFadingEdgeLength(0);
		lvTruckList.setDivider(null);
		lvTruckList.setSelector(R.color.transparent);
		lvTruckList.setVerticalScrollBarEnabled(false);
		lvTruckList.setAdapter(orListAdapter);
		SetRoutePerformance(0, 20000, 13000);
		//Thread to load the data 
		loadData();
	}
	
	/*setting route and month performance of Salesman*/
	private void SetRoutePerformance(int initial, int target, int achived) 
	{
		int [] targetAcheivement;
		try
		{
			targetAcheivement = new UserInfoDA().getUserTargetAndAcheive();
			graphImageView.setUpCustomIV();
			Calendar c = Calendar.getInstance();
			int targetValue = (targetAcheivement[0]/c.getActualMaximum(Calendar.DAY_OF_MONTH))*c.get(Calendar.DAY_OF_MONTH);
			graphImageView.moveNeedle(0, targetAcheivement[0],  targetAcheivement[1], targetValue);
			graphImageView.setVisibility(View.VISIBLE);
			llMonthForfarmance.setVisibility(View.VISIBLE);
			
			if(targetAcheivement[0] == 0 || targetAcheivement[1] == 0)
			{
				graphImageView.setVisibility(View.GONE);
				llMonthForfarmance.setVisibility(View.GONE);
			}
		}
		catch(Exception e)
		{
			graphImageView.setVisibility(View.GONE);
			llMonthForfarmance.setVisibility(View.GONE);
			e.printStackTrace();
		}
		
		int fisrtTarget,secondTarget,thirdTarget,FourthTarget;
		int maxx = (target*121)/100;
		fisrtTarget 	= target/4;
		secondTarget 	= fisrtTarget*2;
		thirdTarget 	= fisrtTarget*3;
		FourthTarget 	= target;
		tvFirstSunTarget.setText(""+fisrtTarget);
		tvSecondSunTarget.setText(""+secondTarget);
		tvThirdSunTarget.setText(""+thirdTarget);
		tvFourthSunTarget.setText(""+FourthTarget);
		tvTargetAchived.setText(""+achived);
		customProgressBar.moveProgress(initial, target, achived, maxx);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH,1);
		c.set(Calendar.DAY_OF_WEEK_IN_MONTH,Calendar.SUNDAY);
		
		tvFirstSunDate.setText("6th Jul");
		c.add(Calendar.DATE, 7);
		tvSecondSunDate.setText("13th Jul");
		c.add(Calendar.DATE, 14);
		tvThirdSunDate.setText("20th Jul");
		c.add(Calendar.DATE, 21);
		tvFourthSunDate.setText("27th Jul");
	}


	/**
	 * Adapter class for the List view
	 */
	public class TruckListAdapter extends BaseAdapter
	{
		//vector to get the main vector
		private Vector<VehicleDO> vecTruckList;
		//constructor for the TruckListAdapter class having Vector parameter
		public TruckListAdapter(Vector<VehicleDO> vecTruckList) 
		{
			this.vecTruckList = vecTruckList;
		}

		@Override
		public int getCount() 
		{
			return vecTruckList.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			//getting the current Object
			VehicleDO objTrucks  	= 	 vecTruckList.get(position);
			//inflating the mng_stf_cell Layout
			convertView 			=	 (LinearLayout)getLayoutInflater().inflate(R.layout.mng_stf_cell, null);
			//getting Id's from mng_stf_cell Layout
			TextView tvOerder		= 	 (TextView)convertView.findViewById(R.id.tvMngStfName);
			TextView tvOerderId		= 	 (TextView)convertView.findViewById(R.id.tvMngStfMemberId);
			
			//setting text
			tvOerder.setText("Vehicle No. ");
			tvOerderId.setText(""+objTrucks.VEHICLE_NO);
			//setting the Type-face
			tvOerder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvOerderId.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			//click event for convertView
			convertView.setTag(objTrucks);
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					//getting the object of NameIDDo class by Tag
					final VehicleDO objTrucks 	=  (VehicleDO) v.getTag();
					
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
//							String rout_Code = new CommonDA().getRouteCode(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
//							objTrucks.ROUTE  = rout_Code;
							preference.saveStringInPreference(Preference.ROUTE_CODE, objTrucks.ROUTE);
							preference.commitPreference();
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
									Intent intent = new Intent(VehicleList.this, AddStockInVehicle.class);
									preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
									preference.commitPreference();
									intent.putExtra("object", objTrucks);
									startActivity(intent);
									
									/*if(new VehicleDA().isAnyItemAvail(CalendarUtils.getOrderPostDate()) <= 0 || new VehicleDA().isAnyItemToVerify(CalendarUtils.getOrderPostDate()))
									{
										if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false) || preference.getbooleanFromPreference(Preference.IsStockVerified, false))
										{
											Intent intent = new Intent(VehicleList.this, PresellerJourneyPlan.class);
											intent.putExtra("Latitude", 25.522);
											intent.putExtra("Longitude", 78.522);
											preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
											preference.commitPreference();
											startActivity(intent);
										}
										else
										{
											preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
											Intent intent = new Intent(VehicleList.this, AddStockInVehicle.class);
											preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
											preference.commitPreference();
											intent.putExtra("object", objTrucks);
											startActivity(intent);
										}
									}
									else if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0)
									{
										preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
										Intent intent = new Intent(VehicleList.this, AddStockInVehicle.class);
										preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
										preference.commitPreference();
										intent.putExtra("object", objTrucks);
										startActivity(intent);
										preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
										Intent intent = new Intent(VehicleList.this, OdometerReadingActivity.class);
										preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
										preference.commitPreference();
										intent.putExtra("object", objTrucks);
										intent.putExtra("isStartDay", true);
										startActivity(intent);
									}*/
									/*else
									{
										Intent intent = new Intent(VehicleList.this, PresellerJourneyPlan.class);
										intent.putExtra("Latitude", 25.522);
										intent.putExtra("Longitude", 78.522);
										preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
										preference.commitPreference();
										startActivity(intent);
									}*/
								}
							});
						}
					}).start();
				}
			});
			//setting LayoutParams to  convertView (List Cell)
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(45 * BaseActivity.px)));
			return convertView;	
		}
		/**
		 * Method to refresh the List View
		 * @param vecTruckList
		 */
		public void refresh(Vector<VehicleDO> vecTruckList)
		{
			this.vecTruckList = vecTruckList;
			notifyDataSetChanged();
			
			if(this.vecTruckList != null && this.vecTruckList.size() > 0)
				tvNoOrderFound.setVisibility(View.GONE);
			else
				tvNoOrderFound.setVisibility(View.VISIBLE);
		}
	}
	
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			btnMenu.performClick();
		else 
			btnLoginLogout.performClick();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		preference.removeFromPreference(Preference.LAST_CUSTOMER_SITE_ID);
		preference.removeFromPreference(Preference.CUSTOMER_NAME);
		preference.commitPreference();
		
		if(btnBack!=null)
			btnBack.setVisibility(View.GONE);
		if(vecTruckList == null || vecTruckList.size()==0)
		{
			loadData();
		}
	}
	
	private void loadData() 
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//to get the Truck list to be delivered
				//get the endstartday and end day for the vehicle for the vehicle
				
				vecTruckList = 	new VehicleDA().getTruckListByDelievryAgentId(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getOrderPostDate());
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
					    orListAdapter.refresh(vecTruckList);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
