package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.winit.baskinrobbin.parsers.PostSurveyStatusParser;
import com.winit.baskinrobbin.salesman.adapter.SurveyAdapter;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyQuestionDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyResultDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDONew;
import com.winit.baskinrobbin.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class ConsumerBehaviourSurveyActivityNew extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{
	private LinearLayout llConsumersurvey;
	private CustomerSurveyDONew  cussurveydo;
	private Button btnsubmit;
	private String errorMessage = "";
	private boolean isValid = false ;
	private TextView tv_instore;
	private ListView lvServeyQue;
	private String taskId = "";
	private double latti, longi;
	private String userRole="";
    private LocationRequest locationRequest;
	
	private LocationClient locationClient;
	
	private String lattitude,longitude;
	
	SurveyCustomerDeatislDO cusdetailDo;
	Location loc;
//	private Vector<SurveyQuestionDONew> vecsurResult;
	//	private ScrollView scScroll;
	@Override
	public void initialize() 
	{
		
		llConsumersurvey = (LinearLayout) inflater.inflate(R.layout.consumersurvey_new, null);
		llBody.addView(llConsumersurvey, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		initializeControlls();
		taskId	  = getIntent().getStringExtra("taskId");
		locationRequest = LocationRequest.create();
		
		locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
		
		locationRequest.setInterval(5000);
		
		locationClient = new LocationClient(this, this, this);
		
		setTypeFace(llConsumersurvey);
		
		cusdetailDo = new SurveyCustomerDeatislDO();
		cusdetailDo = new CustomerDA().getCustomerSurveyDetails(preference.getStringFromPreference(preference.EMP_NO, ""));
		
		if(cusdetailDo.visitCode.contains("-"))
			cusdetailDo.visitCode = cusdetailDo.visitCode.replace("-", "");
		if(cusdetailDo.visitCode.contains("T"))
			cusdetailDo.visitCode = cusdetailDo.visitCode.replace("T", "");
		if(cusdetailDo.visitCode.contains(":"))
			cusdetailDo.visitCode = cusdetailDo.visitCode.replace(":", "");
		
		Log.e("Clientcode", ""+cusdetailDo.clientCode);
		Log.e("VisitCode", ""+cusdetailDo.visitCode);
		
		userRole = new UserInfoDA().getUserRole(preference.getStringFromPreference(preference.EMP_NO, ""));
		
		Log.e("UserRole", ""+userRole);
		
		initializeServeyObject();
		
		final SurveyAdapter surveyAdapter = new SurveyAdapter(this, cussurveydo.srveyQues);
		lvServeyQue.setAdapter(surveyAdapter);
		
		btnsubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				cussurveydo.srveyQues = surveyAdapter.getResult();
				isValid = getValidation(cussurveydo.srveyQues);
				if(isValid)
				{
					showLoader("Please Wait your Survey is submitting...");
					new Thread(new Runnable() {
						
						@Override
						public void run() 
						{
							new SurveyResultDA().insertSurvey(cussurveydo);
							new SurveyResultDA().insertSurveyResult(cussurveydo);
//							new MyActivitiesDA().updateTask(taskId, CalendarUtils.getCurrentDateAsString());
//							uploadData();
							if(cussurveydo != null)
							{
								final PostSurveyStatusParser postSurveyStatusParser 	= new PostSurveyStatusParser(ConsumerBehaviourSurveyActivityNew.this);
								new ConnectionHelper(null).sendRequest(ConsumerBehaviourSurveyActivityNew.this, BuildXMLRequest.postSurvey(cussurveydo), postSurveyStatusParser, ServiceURLs.PostSurvey, preference);
							}
							runOnUiThread(new  Runnable()
							{
								public void run() 
								{
									hideLoader();
									showCustomDialog(ConsumerBehaviourSurveyActivityNew.this, "Successful!", "Survey has been excecuted successfully.", getString(R.string.OK), null, "success");
								}
							});
						}
					}).start();
				}
				else 
				{
					showCustomDialog(ConsumerBehaviourSurveyActivityNew.this, "Alert!", errorMessage, getString(R.string.OK), null, "");
				}
			}
		});
		
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	private boolean getValidation(Vector<SurveyQuestionDONew> vecsurResult2) 
	{
		boolean result = true;
		if(vecsurResult2 != null && vecsurResult2.size() >0 )
		{
			for (int i = 0; i < vecsurResult2.size(); i++) 
			{
				if(vecsurResult2.get(i).option.equalsIgnoreCase(""))
				{
					i = i+1;
					errorMessage = "Please answer question "+i+".";
					result = false;
					break;
				}
				else if(vecsurResult2.get(i).comments.trim().equalsIgnoreCase(""))
				{
					i = i+1;
					errorMessage = "Please enter your comment for question "+i+".";
					result = false;
					break;
				}
			}
		}
		else
		{
			errorMessage = "No survey report available to execute.";
			result = false;
		}
		return result;
	}
	@SuppressWarnings("static-access")
	private void initializeServeyObject() 
	{
		cussurveydo 						= 	new CustomerSurveyDONew();
		cussurveydo.serveyId 				= 	"0";
		cussurveydo.isUploaded              =	"0";
		cussurveydo.status                  =	"0";
		cussurveydo.SurveyAppId 			= 	StringUtils.getUniqueUUID();
		if(cussurveydo.SurveyAppId.contains("-"))
			cussurveydo.SurveyAppId = cussurveydo.SurveyAppId.replace("-", "");
		cussurveydo.SurveyResultAppId 		= 	StringUtils.getUniqueUUID();
		if(cussurveydo.SurveyResultAppId.contains("-"))
			cussurveydo.SurveyResultAppId = cussurveydo.SurveyResultAppId.replace("-", "");
		cussurveydo.clientCode				=	cusdetailDo.clientCode;
		cussurveydo.lattitude 				= 	lattitude;
		cussurveydo.longitude			    =	longitude;
		cussurveydo.date 					= 	CalendarUtils.getCurrentDateTime();
		cussurveydo.userCode 				=	preference.getStringFromPreference(preference.EMP_NO, "");
		cussurveydo.userRole 				= 	userRole;
		cussurveydo.visitCode 				= 	cusdetailDo.visitCode;
		cussurveydo.journeyCode 			= 	CalendarUtils.getCurrentDateAsString();
		cussurveydo.resultServeyId			=	"0";
		SurveyQuestionDA surveyQuesDa 		= 	new SurveyQuestionDA();
		cussurveydo.srveyQues 				= 	surveyQuesDa.getSurveyQuestions(preference.getStringFromPreference(preference.EMP_NO, ""));
		
	
	}
	
	private void initializeControlls() 
	{
		tv_instore 		= (TextView) llConsumersurvey.findViewById(R.id.tv_instore);
		lvServeyQue 	= (ListView) llConsumersurvey.findViewById(R.id.lvServeyQue);
		btnsubmit  	 	= (Button) llConsumersurvey.findViewById(R.id.btnsubmit);
	}
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
//			setResult(2222);
			AppConstants.isServeyCompleted = true;
			finish();
//			showOrderCompletePopup();
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
		}
	}
	@Override
	public void onConnected(Bundle arg0) 
	{

		loc = locationClient.getLastLocation();

		if(loc != null)
		{
			
			latti = loc.getLatitude();
			longi = loc.getLongitude();
			
			lattitude = String.valueOf(latti);
			longitude = String.valueOf(longi);
			
			if(cussurveydo != null)
			{
				cussurveydo.lattitude = lattitude;
				cussurveydo.longitude =	longitude;
			}
			Log.e(lattitude, lattitude);
			Log.e(longitude, longitude);
		}
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		locationClient.connect();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		locationClient.disconnect();
	}
	
	
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(ConsumerBehaviourSurveyActivityNew.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1		  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Survey Completed");
		tv_poptitle1.setText("Successfully");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment		  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		
		tv_poptitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.collection_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_collectpayment.setClickable(false);
		btn_popup_collectpayment.setEnabled(false);
		
		btn_popup_print.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_print.setClickable(false);
		btn_popup_print.setEnabled(false);
		
		
		
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("served");
			}
		});
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
