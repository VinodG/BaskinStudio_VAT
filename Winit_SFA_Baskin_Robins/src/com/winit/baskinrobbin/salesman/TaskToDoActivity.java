package com.winit.baskinrobbin.salesman;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TaskToDoDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.TaskToDoDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class TaskToDoActivity extends BaseActivity{

	private LinearLayout llTasktodo, llBottomButtons;
	private TextView tv_customername,tv_tasktodo,tv_caldate, tvNoRecords;
	private ImageView img_cal;
	private ListView lv_tasktodo;
	private  TasktodoAdapter taskAdap;
	private String camera_imagepath;
	public static String APPFOLDERNAME ="Baskin";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "BaskinImages";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	private int  CAMERA_REQUESTCODE = 2;
	private Vector<TaskToDoDO> vecToDoDOs ;
	private Button btnStartDay;
	private View  tempView;
	private final int DATE_DIALOG_ID = 0;
	private TaskToDoDO taskToDoDO;
	private boolean isEdit = false;
	private Bundle bundle;
	private JourneyPlanDO object;
	
	@Override
	public void initialize() 
	{
		llTasktodo = (LinearLayout) inflater.inflate(R.layout.tasktodo, null);
		
		tv_customername = (TextView) llTasktodo.findViewById(R.id.tv_customername);
		tv_tasktodo = (TextView) llTasktodo.findViewById(R.id.tv_tasktodo);
		tv_caldate  = (TextView) llTasktodo.findViewById(R.id.tv_caldate);
		img_cal  = (ImageView) llTasktodo.findViewById(R.id.img_cal);
		lv_tasktodo = (ListView) llTasktodo.findViewById(R.id.lv_tasktodo);
		btnStartDay= (Button) llTasktodo.findViewById(R.id.btnStartDay);
		tvNoRecords = (TextView) llTasktodo.findViewById(R.id.tvNoRecords);
		llBottomButtons = (LinearLayout) llTasktodo.findViewById(R.id.llBottomButtons);
		
		llBody.addView(llTasktodo, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		lv_tasktodo.setCacheColorHint(0);
		lv_tasktodo.setFadingEdgeLength(0);
		
		bundle		= getIntent().getExtras();
		
		if(bundle != null)
			object = (JourneyPlanDO)bundle.getSerializable("object");
		
		llBottomButtons.setVisibility(View.GONE);
		
		try
		{
			new File(APPMEDIAFOLDERPATH).mkdirs();
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		
//		lv_tasktodo.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) 
//			{
//				taskToDoDO = vecToDoDOs.get(arg2);
//				if(taskToDoDO.TaskName.equalsIgnoreCase(AppConstants.Task_Title3))
//				{
//					Intent in = new Intent(TaskToDoActivity.this, ConsumerBehaviourSurveyActivity.class);
//					in.putExtra("taskId", taskToDoDO.TaskID);
//					in.putExtra("taskName", taskToDoDO.TaskName);
//					startActivity(in);
//				}
//				else
//				{
//					if (checkAppMediaFolder())
//					{
//						camera_imagepath = APPMEDIAFOLDERPATH+ "P&G"+ System.currentTimeMillis()+ ".jpg";
//						Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
//						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//						intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
//						startActivityForResult(intent,CAMERA_REQUESTCODE);
//						
//					}
//				}
//			}
//		});
		
		btnStartDay.setText(" Capture Competitor Activity ");
		btnStartDay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Intent in = new Intent(TaskToDoActivity.this, CapturedCompetitorActivity.class);
				in.putExtra("object", object);
				startActivity(in);
			}
		});
		
		tv_caldate.setClickable(false);
		tv_caldate.setEnabled(false);
		tv_caldate.setText(CalendarUtils.getCurrentDate());
		tv_caldate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				img_cal.performClick();
			}
		});
		
		img_cal.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					tempView = v;
					showDialog(DATE_DIALOG_ID);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		tv_customername.setText(object.siteName);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		setTypeFace(llTasktodo);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		loadData(CalendarUtils.getCurrentDateAsString());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		//getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 0);
		  int cyear 	= 	c.get(Calendar.YEAR);
		  int cmonth 	= 	c.get(Calendar.MONTH);
		  int cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  int myHour 	= 	c.get(Calendar.HOUR_OF_DAY);
		  int myMinute 	= 	c.get(Calendar.MINUTE);
		    
		  switch (id) 
		  {
			  case DATE_DIALOG_ID:
				  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
		  }
		  return null;
	}
	
	/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	  {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
			  if(tempView != null)
			  {
				  String strDate = "", strMonth ="";
				  if(monthOfYear <9)
					  strMonth = "0"+(monthOfYear+1);
				  else
					  strMonth = ""+(monthOfYear+1);
		    		
				  if(dayOfMonth <10)
					  strDate = "0"+(dayOfMonth);
				  else
					  strDate = ""+(dayOfMonth);
		    		
				  tv_caldate.setText(strDate+" "+CalendarUtils.getMonthAsString(monthOfYear)+" "+year);
			  
				  loadData(year+"-"+strMonth+"-"+strDate);
			  }
		  }
	  };
	  
	public class TasktodoAdapter extends BaseAdapter
	{
		Vector<TaskToDoDO> vecToDo;

		public TasktodoAdapter(Vector<TaskToDoDO> vecToDoDOs)
		{
			this.vecToDo = vecToDoDOs;
			
		}

		@Override
		public int getCount()
		{
			if(vecToDo!=null && vecToDo.size()>0)
			{
				return vecToDo.size();
			}
			else
			return 0;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(final int pos, View view11, ViewGroup arg2)
		{
			TextView  tv_listtitle, tv_listdesc;
			ImageView img_listimage,img_listleftarrow;
			LinearLayout view1 = null;
			if(vecToDo.get(pos).Status.equalsIgnoreCase("C"))
			{
				view1 = (LinearLayout) getLayoutInflater().inflate(R.layout.tasktodo_list_cell, null);
				AppConstants.isTaskCompleted = true;
			}
			else
			{
				view1 = (LinearLayout) getLayoutInflater().inflate(R.layout.tasktodo_list_cell_new, null);
			}
			tv_listtitle = (TextView) view1.findViewById(R.id.tv_listtitle);
			tv_listdesc = (TextView) view1.findViewById(R.id.tv_listdesc);
			img_listimage = (ImageView) view1.findViewById(R.id.img_listimage);
			img_listleftarrow = (ImageView) view1.findViewById(R.id.img_listleftarrow);
			ImageView ivCompleted = (ImageView) view1.findViewById(R.id.ivCompleted);
			
			ImageView ivEdit = (ImageView) view1.findViewById(R.id.ivEdit);
			
			if(vecToDo.get(pos).TaskName.equalsIgnoreCase(AppConstants.Task_Title1))
			{
				img_listimage.setImageResource(R.drawable.cam_1);
				tv_listtitle.setTextColor(Color.parseColor("#000000"));
			}
			else if(vecToDo.get(pos).TaskName.equalsIgnoreCase(AppConstants.Task_Title2))
			{
				img_listimage.setImageResource(R.drawable.cam_2);
				tv_listtitle.setTextColor(Color.parseColor("#4BB5E4"));
			}
			else if(vecToDo.get(pos).TaskName.equalsIgnoreCase(AppConstants.Task_Title3))
			{
				img_listimage.setImageResource(R.drawable.icon_1);
				tv_listtitle.setTextColor(Color.parseColor("#ED920D"));
			}
			tv_listtitle.setText(vecToDo.get(pos).TaskName);
			tv_listdesc.setText(vecToDo.get(pos).TaskDesc);
			
			if(vecToDo.get(pos).Status.equalsIgnoreCase("C"))
			{
				ivCompleted.setVisibility(View.VISIBLE);
				if(vecToDo.get(pos).IsAcknowledge.equalsIgnoreCase("true"))
				{
					ivEdit.setVisibility(View.INVISIBLE);
					ivEdit.setEnabled(false);
					ivEdit.setClickable(false);
				}
				else
				{
					ivEdit.setVisibility(View.INVISIBLE);
					ivEdit.setEnabled(true);
					ivEdit.setClickable(true);
				}
			}
			else
			{
				ivCompleted.setVisibility(View.GONE);
				ivEdit.setVisibility(View.GONE);
			}
			
			ivEdit.setTag(vecToDo.get(pos));
			ivEdit.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					taskToDoDO = (TaskToDoDO)v.getTag();
					if(taskToDoDO.TaskName.equalsIgnoreCase(AppConstants.Task_Title3))
					{
//						Intent in = new Intent(TaskToDoActivity.this, ConsumerBehaviourSurveyActivity.class);
						Intent in = new Intent(TaskToDoActivity.this, ConsumerBehaviourSurveyActivityNew.class);
						in.putExtra("taskId", taskToDoDO.TaskID);
						in.putExtra("taskName", taskToDoDO.TaskName);
						in.putExtra("object", object);
						in.putExtra("isEdit", true);
						startActivity(in);
					}
					else
					{
						String strTitle = taskToDoDO.TaskName;
						Intent in = new Intent(TaskToDoActivity.this, CaptureShelfPhotoActivity.class);
						in.putExtra("taskId", taskToDoDO.TaskID);
						in.putExtra("taskName", taskToDoDO.TaskName);
						in.putExtra("strTitle", strTitle);
						in.putExtra("isEdit", true);
						in.putExtra("object", object);
						startActivity(in);
						
//						if (checkAppMediaFolder())
//						{
//							isEdit = true;
//							camera_imagepath = APPMEDIAFOLDERPATH+ "P&G"+ System.currentTimeMillis()+ ".jpg";
//							Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
//							Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//							intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
//							startActivityForResult(intent,CAMERA_REQUESTCODE);
//							
//						}
					}
				}
			});
			view1.setTag(vecToDo.get(pos));
			view1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					if(((TaskToDoDO)v.getTag()).Status.equalsIgnoreCase("C"))
					{
						AppConstants.isTaskCompleted = true;
//						if(pos==2)
//						{
//							
//							Intent in = new Intent(TaskToDoActivity.this, MyActivities.class);
////							in.putExtra("taskId", taskToDoDO.TaskID);
////							in.putExtra("taskName", taskToDoDO.TaskName);
//							startActivity(in);
//						}
//						else
//						{
							Intent in = new Intent(TaskToDoActivity.this, MyActivities.class);
							in.putExtra("object", object);
//							in.putExtra("taskId", taskToDoDO.TaskID);
//							in.putExtra("taskName", taskToDoDO.TaskName);
//							in.putExtra("strTitle", taskToDoDO.TaskName);
							startActivity(in);
//						}
							
						
						
					}
					else
					{
						taskToDoDO = (TaskToDoDO)v.getTag();
						if(taskToDoDO.TaskName.equalsIgnoreCase(AppConstants.Task_Title3))
						{
//							Intent in = new Intent(TaskToDoActivity.this, ConsumerBehaviourSurveyActivity.class);
							Intent in = new Intent(TaskToDoActivity.this, ConsumerBehaviourSurveyActivityNew.class);
							in.putExtra("taskId", taskToDoDO.TaskID);
							in.putExtra("taskName", taskToDoDO.TaskName);
							in.putExtra("object", object);
							startActivity(in);
						}
						else
						{
							if (checkAppMediaFolder())
							{
								camera_imagepath = APPMEDIAFOLDERPATH+ "baskin"+ System.currentTimeMillis()+ ".jpg";
								Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
								Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
								startActivityForResult(intent,CAMERA_REQUESTCODE);
								
							}
						}
					}
				}
			});
			
			setTypeFace(view1);
			return view1;
		}
	}
	
	public boolean createAppFolder()
	{
		return AppConstants.APPFOLDER.mkdirs();
	}
	public boolean createAppMediaFolder()
	{
		return AppConstants.APPMEDIAFOLDER.mkdirs();
	}
	public boolean checkAppMediaFolder()
	{
		if(AppConstants.APPMEDIAFOLDER.exists())
		{
			return true;
		}
		else
		{
			if(AppConstants.APPFOLDER.exists())
			{
				return createAppMediaFolder();
			}
			else
			{
				checkAppFolder();
				return createAppMediaFolder();
			}
		}
		
	}
	
	public boolean checkAppFolder()
	{
		if(AppConstants.APPFOLDER.exists())
		{
			return true;
		}
		else
		{
			return createAppFolder();
		}
	}
	
//	private void updateUserProfPic()
//	{
//		if(!AppConstants.imagePath.equalsIgnoreCase("") && iv_pic!=null)
//		{
//			
//			Bitmap bitmap = Bitmap.createScaledBitmap(getbitmap(AppConstants.imagePath),120, 120, true);
//			iv_pic.setImageBitmap(bitmap);
//		}
//	}
//	
	private String getRealPathFromURI(Uri contentUri) 
	{
		String[] proj = { MediaStore.Video.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		
		 if (resultCode == RESULT_OK && requestCode == CAMERA_REQUESTCODE)
		{
			switch (resultCode) 
			{
			case RESULT_CANCELED:
				Log.i("Camera", "User cancelled");
				break;

			case RESULT_OK:
				
				if(taskToDoDO != null)
				{
					String strTitle = taskToDoDO.TaskName;
					AppConstants.imagePath = camera_imagepath;
					Intent in = new Intent(TaskToDoActivity.this, CaptureShelfPhotoActivity.class);
					in.putExtra("imagepath", AppConstants.imagePath);
					in.putExtra("taskId", taskToDoDO.TaskID);
					in.putExtra("taskName", taskToDoDO.TaskName);
					in.putExtra("strTitle", strTitle);
					in.putExtra("isEdit", isEdit);
					in.putExtra("object", object);
					startActivity(in);
					
					isEdit = false;
				}
				break;
			}

		}
		
	}
	private void loadData(final String strdate)
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				vecToDoDOs = new TaskToDoDA().getTaskToDO(object.site, strdate);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(vecToDoDOs != null && vecToDoDOs.size()>0)
						{
							taskAdap = new TasktodoAdapter(vecToDoDOs);
							lv_tasktodo.setAdapter(taskAdap);
							
							tvNoRecords.setVisibility(View.GONE);
							lv_tasktodo.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoRecords.setVisibility(View.VISIBLE);
							lv_tasktodo.setVisibility(View.GONE);
						}
						hideLoader();
					}
				});
			}
		}).start();
	}
	
//	@Override
//	public void onBackPressed() 
//	{
//		super.btnCheckOut.performClick();
//	}
}
