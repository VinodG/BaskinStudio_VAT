package com.winit.baskinrobbin.salesman;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class ConsumerBehaviourSurveyActivity extends BaseActivity{
	private LinearLayout llConsumersurvey;
	private ImageView iv_uncheck1a,iv_uncheck1b,iv_uncheck1c,iv_uncheck1d,iv_uncheck2,iv_uncheck3a,iv_uncheck3b,iv_uncheck3c,iv_uncheck3d;
	private TextView tv_answer1a,tv_answer1b,tv_answer1c,tv_answer1d,tv_answer3a,tv_answer3b,tv_answer3c,tv_answer3d;
	private EditText et_answer4a,et_answer4b,et_answer4c,et_answer4d;
	private boolean /*statepressed1a,statepressed1b,statepressed1c,statepressed1d,*/statepressed2,statepressed3a,statepressed3b,statepressed3c,statepressed3d = false;
	private CustomerSurveyDO  cussurveydo;
	private Button btnsubmit;
	private int i,j,k,l=0;
	private String taskId = "", taskName ="";
	private String errorMessage = "";
	private boolean isValid = true ;
	private boolean isedit = false;
	private ScrollView scScroll;
	private JourneyPlanDO journeyPlanDO;
	
	@Override
	public void initialize() 
	{
		
		llConsumersurvey = (LinearLayout) inflater.inflate(R.layout.consumersurvey, null);
		
		iv_uncheck1a = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck1a);
		iv_uncheck1b = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck1b);
		iv_uncheck1c = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck1c);
		iv_uncheck1d = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck1d);
		iv_uncheck2  = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck2);
		iv_uncheck3a = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck3a);
		iv_uncheck3b = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck3b);
		iv_uncheck3c = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck3c);
		iv_uncheck3d = (ImageView) llConsumersurvey.findViewById(R.id.iv_uncheck3d);
		tv_answer1a  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer1a);
		tv_answer1b  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer1b);
		tv_answer1c  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer1c);
		tv_answer1d  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer1d);
		tv_answer3a  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer3a);
		tv_answer3b  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer3b);
		tv_answer3c  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer3c);
		tv_answer3d  = (TextView) llConsumersurvey.findViewById(R.id.tv_answer3d);
		et_answer4a  = (EditText) llConsumersurvey.findViewById(R.id.et_answer4a);
		et_answer4b  = (EditText) llConsumersurvey.findViewById(R.id.et_answer4b);
		et_answer4c  = (EditText) llConsumersurvey.findViewById(R.id.et_answer4c);
		et_answer4d  = (EditText) llConsumersurvey.findViewById(R.id.et_answer4d);
		
		scScroll	 = (ScrollView) llConsumersurvey.findViewById(R.id.scScroll);
		
		btnsubmit  	 = (Button) llConsumersurvey.findViewById(R.id.btnsubmit);
		
		taskId = getIntent().getExtras().getString("taskId");
		taskName  = getIntent().getStringExtra("taskName");
		journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("object");
		isedit	  = getIntent().getBooleanExtra("isEdit", false);
		cussurveydo = new CustomerSurveyDO();
		
		llBody.addView(llConsumersurvey, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		tv_answer1a.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				iv_uncheck1a.performClick();
			}
		});
		
		tv_answer1b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck1b.performClick();
			}
		});
		tv_answer1c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck1c.performClick();
			}
		});
		tv_answer1d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck1d.performClick();
			}
		});
		tv_answer3a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck3a.performClick();
			}
		});
		tv_answer3b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck3b.performClick();
			}
		});
		tv_answer3c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck3c.performClick();
			}
		});
		tv_answer3d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				iv_uncheck3d.performClick();
			}
		});
		
		

		
		iv_uncheck1a.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				if(!cussurveydo.Olay)
				{
					iv_uncheck1a.setImageResource(R.drawable.check_box_selected);
					cussurveydo.Olay = true;
					i++;
				}
				else
				{
					iv_uncheck1a.setImageResource(R.drawable.check_box);
					cussurveydo.Olay = false;
					i--; 
					
				}
				
			}
		});
		
		iv_uncheck1b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				if(!cussurveydo.Pantene)
				{
					iv_uncheck1b.setImageResource(R.drawable.check_box_selected);
					cussurveydo.Pantene = true;
					i++;
				}
				else
				{
					iv_uncheck1b.setImageResource(R.drawable.check_box);
					cussurveydo.Pantene = false;
					i--; 
				}
			}
		});
		iv_uncheck1c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				if(!cussurveydo.Lakme)
				{
					iv_uncheck1c.setImageResource(R.drawable.check_box_selected);
					cussurveydo.Lakme = true;
					i++;
				}
				else
				{
					iv_uncheck1c.setImageResource(R.drawable.check_box);
					cussurveydo.Lakme = false;
					i--; 
					
				}
				
			}
		});
		
		iv_uncheck1d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				
				if(!cussurveydo.Elle18)
				{
					iv_uncheck1d.setImageResource(R.drawable.check_box_selected);
					cussurveydo.Elle18 = true;
					i++;
				}
				else
				{
					iv_uncheck1d.setImageResource(R.drawable.check_box);
					cussurveydo.Elle18 = false;
					i--; 
					
				}
				
			}
		});
		iv_uncheck2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				if(!statepressed2)
				{
					iv_uncheck2.setImageResource(R.drawable.agree);
					statepressed2 = true;
					cussurveydo.Agree = true;
					j++;
				}
				else
				{
					iv_uncheck2.setImageResource(R.drawable.disagree);
					statepressed2 = false;
					cussurveydo.Agree = false;
					j--;
					
				}
				
				
			}
		});
		iv_uncheck3a.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				
				if(!statepressed3a)
				{
					iv_uncheck3a.setImageResource(R.drawable.radio_btn_selected);
					iv_uncheck3b.setImageResource(R.drawable.radio_btn);
					iv_uncheck3c.setImageResource(R.drawable.radio_btn);
					iv_uncheck3d.setImageResource(R.drawable.radio_btn);
					
					statepressed3a = true;
					k++;
					statepressed3b = false;
					statepressed3c = false;
					statepressed3d = false;
					
					cussurveydo.spent=AppStatus.AEDBelow2000;
				}
				else
				{
					iv_uncheck3a.setImageResource(R.drawable.radio_btn);
					statepressed3a = false;
					k--;
					cussurveydo.spent=AppStatus.AEDZero;
					
				}
				

				
			}
		});
		iv_uncheck3b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0)
			{

				if(!statepressed3b)
				{
					iv_uncheck3b.setImageResource(R.drawable.radio_btn_selected);
					iv_uncheck3a.setImageResource(R.drawable.radio_btn);
					iv_uncheck3c.setImageResource(R.drawable.radio_btn);
					iv_uncheck3d.setImageResource(R.drawable.radio_btn);
					
					statepressed3b = true;
					statepressed3a = false;
					statepressed3c = false;
					statepressed3d = false;
					k++;
					cussurveydo.spent=AppStatus.AEDBelow3000;
				}
				else
				{
					iv_uncheck3b.setImageResource(R.drawable.radio_btn);
					statepressed3b = false;
					k--;
					cussurveydo.spent=AppStatus.AEDZero;
				}

			}
		});
		iv_uncheck3c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				if(!statepressed3c)
				{
					iv_uncheck3c.setImageResource(R.drawable.radio_btn_selected);
					iv_uncheck3a.setImageResource(R.drawable.radio_btn);
					iv_uncheck3b.setImageResource(R.drawable.radio_btn);
					iv_uncheck3d.setImageResource(R.drawable.radio_btn);
					statepressed3c = true;
					statepressed3a = false;
					statepressed3b = false;
					statepressed3d = false;
					k++;
					cussurveydo.spent=AppStatus.AEDBelow1000;
				}
				else
				{
					iv_uncheck3c.setImageResource(R.drawable.radio_btn);
					statepressed3c = false;
					k--;
					cussurveydo.spent=AppStatus.AEDZero;
					
				}
				
				
				
			}
		});
		iv_uncheck3d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{

				if(!statepressed3d)
				{
					iv_uncheck3d.setImageResource(R.drawable.radio_btn_selected);
					iv_uncheck3a.setImageResource(R.drawable.radio_btn);
					iv_uncheck3b.setImageResource(R.drawable.radio_btn);
					iv_uncheck3c.setImageResource(R.drawable.radio_btn);
					statepressed3d = true;
					statepressed3a = false;
					statepressed3b = false;
					statepressed3c = false;
					k++;
					cussurveydo.spent=AppStatus.AEDAbove4000;
				}
				else
				{
					iv_uncheck3d.setImageResource(R.drawable.radio_btn);
					statepressed3d = false;
					k--;
					cussurveydo.spent=AppStatus.AEDZero;
					
				}
				
				
			
			}
		});
		
		btnsubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
				
				if(cussurveydo != null)
				{
					showLoader("Please wait...");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							cussurveydo.serveyId = System.currentTimeMillis()+"";
							cussurveydo.tskId = taskId+"";
							cussurveydo.isVerified = "false";
							cussurveydo.isPushStatus = "0";
							cussurveydo.date = CalendarUtils.getCurrentDate();
							cussurveydo.taskName = taskName;
							cussurveydo.strCusomerName = journeyPlanDO.siteName;
							
							cussurveydo.Brand1 = et_answer4a.getText().toString();
							cussurveydo.Brand2 = et_answer4b.getText().toString();
							cussurveydo.Brand3 = et_answer4c.getText().toString();
							cussurveydo.Brand4 = et_answer4d.getText().toString();
							
							boolean isQestFour = false;
							
							if(!cussurveydo.Brand1.equalsIgnoreCase(""))
							{
								isQestFour = true;
								l++;
							}
							if(!cussurveydo.Brand2.equalsIgnoreCase(""))
							{
								isQestFour = true;
								l++;
							}
							if(!cussurveydo.Brand3.equalsIgnoreCase(""))
							{
								isQestFour = true;
								l++;
							}
							if(!cussurveydo.Brand4.equalsIgnoreCase(""))
							{
								isQestFour = true;
								l++;
							}
							
							if(!isQestFour)
								l = 0;
							isValid = true ;
							if(i==0)
							{
								errorMessage = "Please answer question 1";
								isValid = false;
							}
							
							else if(k==0)
							{
								errorMessage = "Please answer question 3";
								isValid = false;
							}
							else if(l==0)
							{
								errorMessage = "Please answer question 4";
								isValid = false;
							}else
							{
								errorMessage = "Your task has been submitted successfully.";
							}
							
							if(isValid || isedit)
							{
//								new MyActivitiesDA().insertCustomerServey(cussurveydo);
								new MyActivitiesDA().updateTask(taskId, CalendarUtils.getCurrentDateAsString());
							}
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run()
								{
									hideLoader();
									if(isValid || isedit)
										showCustomDialog(ConsumerBehaviourSurveyActivity.this, "Success!", "You have executed the task. The details have been submitted to your manager for review.", getString(R.string.OK), null, "success");
									else
										showCustomDialog(ConsumerBehaviourSurveyActivity.this, "Alert!", errorMessage, getString(R.string.OK), null, " ");
									
									uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
								}
							});
						}
					}).start();
				}
			}
		});
		
		if(isedit)
		{
			showLoader("Please wait");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					cussurveydo = new MyActivitiesDA().getServeyByTaskID(taskId);
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							hideLoader() ;
							if(cussurveydo.Olay)
							{
								iv_uncheck1a.setImageResource(R.drawable.check_box_selected);
							}
							if(cussurveydo.Pantene)
							{
								iv_uncheck1b.setImageResource(R.drawable.check_box_selected);
							}
							if(cussurveydo.Lakme)
							{
								iv_uncheck1c.setImageResource(R.drawable.check_box_selected);
							}
							if(cussurveydo.Elle18)
							{
								iv_uncheck1d.setImageResource(R.drawable.check_box_selected);
							}
							if(cussurveydo.Agree)
							{
								iv_uncheck2.setImageResource(R.drawable.agree);
							}
							if(cussurveydo.spent == AppStatus.AEDBelow2000)
							{
								iv_uncheck3a.setImageResource(R.drawable.radio_btn_selected);
							}
							if(cussurveydo.spent == AppStatus.AEDBelow3000)
							{
								iv_uncheck3b.setImageResource(R.drawable.radio_btn_selected);
							}
							if(cussurveydo.spent == AppStatus.AEDBelow1000)
							{
								iv_uncheck3c.setImageResource(R.drawable.radio_btn_selected);
							}
							if(cussurveydo.spent == AppStatus.AEDAbove4000)
							{
								iv_uncheck3d.setImageResource(R.drawable.radio_btn_selected);
							}
							if(!cussurveydo.Brand1.equalsIgnoreCase(" "))
							{
								et_answer4a.setText(cussurveydo.Brand1);
							}
							if(!cussurveydo.Brand2.equalsIgnoreCase(" "))
							{
								et_answer4b.setText(cussurveydo.Brand2);
							}
							if(!cussurveydo.Brand3.equalsIgnoreCase(" "))
							{
								et_answer4c.setText(cussurveydo.Brand2);
							}
							if(!cussurveydo.Brand4.equalsIgnoreCase(" "))
							{
								et_answer4d.setText(cussurveydo.Brand2);
							}
						}
					});
				}
			}).start();
			
		}
		btnCheckOut.setVisibility(View.VISIBLE);
	}
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
			finish();
		}
	}
	
//	@Override
//	public void onBackPressed() 
//	{
//		super.btnCheckOut.performClick();
//	}
}
