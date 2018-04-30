package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetCategoryLevelDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetDA_New;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class AssetRequestActivity_New extends BaseActivity
{
	private LinearLayout llAssetRequest;
	private TextView  tvSelectAssetsLevel1,tvSelectAssetsLevel2,tvSelectAssetsLevel3,tvSelectAssetsLevel4,tvSelectAssetsLevel5;
	private String strAssetName,strAssetCatLevel1,strAssetCatLevel2,strAssetCatLevel3,strAssetCatLevel4,strAssetCatLevel5,strReqSiteNumber;
	private EditText etAssetName;
	private Vector<NameIDDo> vecAssetLevel1,vecAssetLevel2,vecAssetLevel3,vecAssetLevel4,vecAssetLevel5;
	private Button btnSubmit;
	@Override
	public void initialize() 
	{
		
		llAssetRequest		=	(LinearLayout)inflater.inflate(R.layout.assets_request_new, null);
		
		llBody.addView(llAssetRequest,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
		bindControls();
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		setTypeFace(llAssetRequest);
		if(getIntent().hasExtra("SiteNo"))
		{
			strReqSiteNumber =  getIntent().getStringExtra("SiteNo");
		}
	     vecAssetLevel1 = new AssetCategoryLevelDA().getAllAssetLevel1();
		
	}
//	private void getAssetsDetails(JourneyPlanDO customersDo)
//	{
//		if(customersDo != null)
//			
//	}
	private void bindControls() 
	{
		tvSelectAssetsLevel1.setTag(-1);
		tvSelectAssetsLevel1.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
					CustomBuilder builder = new CustomBuilder(AssetRequestActivity_New.this, "Select Asset Category Level 1", true, true);
					builder.setSingleChoiceItems(vecAssetLevel1, v.getTag(), new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							ClearFromlevel1();
							NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
							tvSelectAssetsLevel1.setText(""+ObjNameIDDo.strName);
							tvSelectAssetsLevel1.setTag(ObjNameIDDo);
							vecAssetLevel2 = new AssetCategoryLevelDA().getAllAssetLevel2(ObjNameIDDo.strName);
							builder.dismiss();
						}
					}); 
					builder.show();
			}
		});
		
		tvSelectAssetsLevel2.setTag(-1);
		tvSelectAssetsLevel2.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecAssetLevel2 != null && vecAssetLevel2.size() > 0)
				{
					CustomBuilder builder = new CustomBuilder(AssetRequestActivity_New.this, "Select Asset Category Level 2", true, true);
					builder.setSingleChoiceItems(vecAssetLevel2, v.getTag(), new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							ClearFromlevel2();
							NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
							tvSelectAssetsLevel2.setText(""+ObjNameIDDo.strName);
							tvSelectAssetsLevel2.setTag(ObjNameIDDo);
							vecAssetLevel3 = new AssetCategoryLevelDA().getAllAssetLevel3(ObjNameIDDo.strName);
							
							builder.dismiss();
						}
					}); 
					builder.show();
				}
				else
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Category Level 1.", getString(R.string.OK), null, "");
				}
			}
		});
		
		tvSelectAssetsLevel3.setTag(-1);
		tvSelectAssetsLevel3.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecAssetLevel3 != null && vecAssetLevel3.size() > 0)
				{
					CustomBuilder builder = new CustomBuilder(AssetRequestActivity_New.this, "Select Asset Category Level 3", true, true);
					builder.setSingleChoiceItems(vecAssetLevel3, v.getTag(), new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							ClearFromlevel3();
							NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
							tvSelectAssetsLevel3.setText(""+ObjNameIDDo.strName);
							tvSelectAssetsLevel3.setTag(ObjNameIDDo);
							vecAssetLevel4 = new AssetCategoryLevelDA().getAllAssetLevel4(ObjNameIDDo.strName);
							
							builder.dismiss();
						}
					}); 
					builder.show();
				}
				else
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Category Level 2.", getString(R.string.OK), null, "");
				}
			}
		});
		
		tvSelectAssetsLevel4.setTag(-1);
		tvSelectAssetsLevel4.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecAssetLevel4 != null && vecAssetLevel4.size() > 0)
				{
					CustomBuilder builder = new CustomBuilder(AssetRequestActivity_New.this, "Select Asset Category Level 4", true, true);
					builder.setSingleChoiceItems(vecAssetLevel4, v.getTag(), new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							ClearFromlevel4();
							NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
							tvSelectAssetsLevel4.setText(""+ObjNameIDDo.strName);
							tvSelectAssetsLevel4.setTag(ObjNameIDDo);
							vecAssetLevel5 = new AssetCategoryLevelDA().getAllAssetLevel5(ObjNameIDDo.strName);
							
							builder.dismiss();
						}
					}); 
					builder.show();
				}
				else
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Category Level 3.", getString(R.string.OK), null, "");
				}
			}
		});
		
		tvSelectAssetsLevel5.setTag(-1);
		tvSelectAssetsLevel5.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecAssetLevel5 != null && vecAssetLevel5.size() > 0)
				{
					CustomBuilder builder = new CustomBuilder(AssetRequestActivity_New.this, "Select Asset Category Level 5", true, true);
					builder.setSingleChoiceItems(vecAssetLevel5, v.getTag(), new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
							tvSelectAssetsLevel5.setText(""+ObjNameIDDo.strName);
							tvSelectAssetsLevel5.setTag(ObjNameIDDo);
							builder.dismiss();
						}
					}); 
					builder.show();
				}
				else
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Category Level 4.", getString(R.string.OK), null, "");
				}
			}
		});
	/*	tvSelectDate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});*/
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				strAssetName = etAssetName.getText().toString();
				strAssetCatLevel1 = tvSelectAssetsLevel1.getText().toString();
				strAssetCatLevel2 = tvSelectAssetsLevel2.getText().toString();
				strAssetCatLevel3 = tvSelectAssetsLevel3.getText().toString();
				strAssetCatLevel4 = tvSelectAssetsLevel4.getText().toString();
				strAssetCatLevel5 = tvSelectAssetsLevel5.getText().toString();
				
				
				if(strAssetName != null && strAssetName.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please enter Asset Name.", getString(R.string.OK), null, "");
				}
				else if(strAssetCatLevel1 != null && strAssetCatLevel1.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Asset Category Level 1.", getString(R.string.OK), null, "");
				}
				else if(strAssetCatLevel2 != null && strAssetCatLevel2.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Asset Category Level 2.", getString(R.string.OK), null, "");
				}
				else if(strAssetCatLevel3 != null && strAssetCatLevel3.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Asset Category Level 3.", getString(R.string.OK), null, "");
				}
				else if(strAssetCatLevel4 != null && strAssetCatLevel4.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Asset Category Level 4.", getString(R.string.OK), null, "");
				}
				else if(strAssetCatLevel5 != null && strAssetCatLevel5.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity_New.this, getString(R.string.warning), "Please select Asset Category Level 5.", getString(R.string.OK), null, "");
				}
				else
				{
					AssetDo_New assNew = new AssetDo_New();
					assNew.assetId = StringUtils.getUniqueUUID();
					assNew.assetName = strAssetName;
					assNew.assetCatLevel1 = strAssetCatLevel1;
					assNew.assetCatLevel2 = strAssetCatLevel2;
					assNew.assetCatLevel3 = strAssetCatLevel3;
					assNew.assetCatLevel4 = strAssetCatLevel4;
					assNew.assetCatLevel5 = strAssetCatLevel5;
					assNew.status = "0";
					
					postAssetRequest(assNew);
					
//					showCustomDialog(AssetRequestActivity_New.this, "Success!", "Your Asset Request has been Submitted.", getString(R.string.OK), null, "success");
				}
				
			}
		});
	}
	private void postAssetRequest(final AssetDo_New assNew) 
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
//				final PostAssetCustomerParser postAssetCustomerParser  = new PostAssetCustomerParser(AssetRequestActivity_New.this);
//		        new ConnectionHelper(null).sendRequest(AssetRequestActivity_New.this,BuildXMLRequest.postAssetsRequest(strReqSiteNumber,strReqAssest,strReqDate,quntityReq), postAssetCustomerParser, ServiceURLs.InsertAssetCustomer, preference);
			
				new AssetDA_New().insertAsset(assNew, strReqSiteNumber, preference.getStringFromPreference(Preference.EMP_NO, ""));
		        runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						hideLoader();
						uploadData(AppStatus.POST_ASSET_REQUEST, AppStatus.TODAY_DATA);
						showCustomDialog(AssetRequestActivity_New.this, "Success!", "Your Asset Request has been Submitted.", getString(R.string.OK), null, "success");
						
					}
				});
			
			
			}
			
			
		}).start();
	}
	private void intialiseControls() 
	{
		tvSelectAssetsLevel1		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel1);
		tvSelectAssetsLevel2		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel2);
		tvSelectAssetsLevel3		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel3);
		tvSelectAssetsLevel4		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel4);
		tvSelectAssetsLevel5		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel5);
		
		etAssetName					=   (EditText)llAssetRequest.findViewById(R.id.etAssetName);
		
		
		btnSubmit			=   (Button)llAssetRequest.findViewById(R.id.btnSubmit);
	}
	/*@Override
	protected Dialog onCreateDialog(int id) 
    {
		//getting current dateofJorney from Calendar
	     Calendar c = 	Calendar.getInstance();
	     int cyear 	= 	c.get(Calendar.YEAR);
	     int cmonth = 	c.get(Calendar.MONTH);
	     int cday 	=	c.get(Calendar.DAY_OF_MONTH);
	     
	     switch (id) 
	     {
		     case DATE_DIALOG_ID:
		      	return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	     }
		 return null;
	  }
		*/
	/** method for date of Request picker **//*
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	    {
	    	//getting current date from Calendar
		     Calendar currentCal = 	Calendar.getInstance();
		     int cyear 			 = 	currentCal.get(Calendar.YEAR);
		     int cmonth 		 = 	currentCal.get(Calendar.MONTH);
		     int cday 			 =	currentCal.get(Calendar.DAY_OF_MONTH);
		     currentCal.set(cyear, cmonth, cday);
		     Calendar selectedCal = Calendar.getInstance();
		     selectedCal.set(year, monthOfYear, dayOfMonth);
		     tvSelectDate.setTag(year+"-"+((monthOfYear+1)< 10?"0"+(monthOfYear+1):(monthOfYear+1))+"-"+((dayOfMonth)<10?"0"+(dayOfMonth):(dayOfMonth)));
	    	 strReqDate = CalendarUtils.getMonthFromNumber(monthOfYear+1)+" "+dayOfMonth+CalendarUtils.getDateNotation(dayOfMonth)+", "+year;
	    	 tvSelectDate.setText(strReqDate);
	    }
    };
*/
	
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
			finish();
		}
		
	}
	
	private void ClearFromlevel1()
	{
		vecAssetLevel2 = new Vector<NameIDDo>();
		vecAssetLevel3= new Vector<NameIDDo>();
		vecAssetLevel4= new Vector<NameIDDo>();
		vecAssetLevel5= new Vector<NameIDDo>();
		tvSelectAssetsLevel2.setText("");
		tvSelectAssetsLevel3.setText("");
		tvSelectAssetsLevel4.setText("");
		tvSelectAssetsLevel5.setText("");
	}
	private void ClearFromlevel2()
	{
		vecAssetLevel3= new Vector<NameIDDo>();
		vecAssetLevel4= new Vector<NameIDDo>();
		vecAssetLevel5= new Vector<NameIDDo>();
		tvSelectAssetsLevel3.setText("");
		tvSelectAssetsLevel4.setText("");
		tvSelectAssetsLevel5.setText("");
	}
	private void ClearFromlevel3()
	{
		vecAssetLevel4= new Vector<NameIDDo>();
		vecAssetLevel5= new Vector<NameIDDo>();
		tvSelectAssetsLevel4.setText("");
		tvSelectAssetsLevel5.setText("");
	}
	private void ClearFromlevel4()
	{
		vecAssetLevel5= new Vector<NameIDDo>();
		tvSelectAssetsLevel5.setText("");
	}
}
