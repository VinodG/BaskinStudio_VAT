package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.parsers.PostAssetCustomerParser;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetCustomerDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class AssetRequestActivity extends BaseActivity
{
	private LinearLayout llAssetRequest;
	private TextView /*tvSelectCustomers*/ tvSelectAssets, tvSelectDate,tvAssetRequestTitle;
//	private final int DATE_DIALOG_ID = 0;
	private String strReqDate,strReqSiteNumber,strReqAssest="";
	private int quntityReq;
	private EditText etQuantity;
//	private Vector<JourneyPlanDO> vecsCustomers;
	private Vector<AssetDO> vecsAssets;
	private Button btnSubmit;
	@Override
	public void initialize() 
	{
		
		llAssetRequest		=	(LinearLayout)inflater.inflate(R.layout.assets_request, null);
		
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
	     strReqDate = CalendarUtils.getCurrentDateTime();
	     tvSelectDate.setText(strReqDate);
    	 vecsAssets = new AssetCustomerDA().getAllAssetsRequest();
		
	}
//	private void getAssetsDetails(JourneyPlanDO customersDo)
//	{
//		if(customersDo != null)
//			
//	}
	private void bindControls() 
	{
		tvSelectAssets.setTag(-1);
		tvSelectAssets.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(AssetRequestActivity.this, "Select Asset", true, true);
				builder.setSingleChoiceItems(vecsAssets, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						AssetDO ObjNameIDDo = (AssetDO) selectedObject;
						tvSelectAssets.setText(""+ObjNameIDDo.name+" - "+ObjNameIDDo.assetType+" - "+ObjNameIDDo.capacity+" L");
						tvSelectAssets.setTag(ObjNameIDDo);
						strReqAssest = ObjNameIDDo.assetId;
						builder.dismiss();
		    		}
			   }); 
				builder.show();
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
				if(strReqAssest.equalsIgnoreCase(""))
				{
					showCustomDialog(AssetRequestActivity.this, getString(R.string.warning), "Please select atleast one Asset.", getString(R.string.OK), null, "");
				}
				else if(etQuantity.getText().toString()!=null&&!etQuantity.getText().toString().equalsIgnoreCase(""))
				{
					quntityReq = Integer.parseInt(etQuantity.getText().toString());
					if(quntityReq>=1)
						postAssetRequest();
					else
						showCustomDialog(AssetRequestActivity.this, getString(R.string.warning), "Assest quantity should be more than one.", getString(R.string.OK), null, "");
				}
				else
					showCustomDialog(AssetRequestActivity.this, getString(R.string.warning), "Please enter quantity.", getString(R.string.OK), null, "");
				
				
			}
		});
	}
	private void postAssetRequest() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				final PostAssetCustomerParser postAssetCustomerParser  = new PostAssetCustomerParser(AssetRequestActivity.this);
		        new ConnectionHelper(null).sendRequest(AssetRequestActivity.this,BuildXMLRequest.postAssetsRequest(strReqSiteNumber,strReqAssest,strReqDate,quntityReq), postAssetCustomerParser, ServiceURLs.InsertAssetCustomer, preference);
			
		        runOnUiThread(new Runnable() 
				{
					@Override
					public void run()
					{
						hideLoader();
						showCustomDialog(AssetRequestActivity.this, "Success!", "Your Asset Request has been Submitted.", getString(R.string.OK), null, "success");
						
					}
				});
			
			
			}
			
			
		}).start();
	}
	private void intialiseControls() 
	{
//		tvSelectCustomers	=	(TextView)llAssetRequest.findViewById(R.id.tvSelectCustomers);
		tvSelectAssets		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssets);
		tvSelectDate		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectDate);
		etQuantity			=	(EditText)llAssetRequest.findViewById(R.id.etQuantity);
		tvAssetRequestTitle =	(TextView)llAssetRequest.findViewById(R.id.tvAssetRequestTitle);
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
}
