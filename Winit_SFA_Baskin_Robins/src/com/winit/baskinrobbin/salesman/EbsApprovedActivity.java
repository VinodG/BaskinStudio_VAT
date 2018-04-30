/*package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Vector;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.InventoryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.preference.Preference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EbsApprovedActivity extends BaseActivity
{
	private LinearLayout llLoadRequestScreen;
	private TextView tvCode, tvDate, tvQty, tvOrdersheetHeader, tvNoItemFound;
	private ListView lvLoadRequest;
	private Button btnContinue;
	private int load_type;
	private ArrayList<LoadRequestDO> arrayList;
	private LoadViewRequestAdapter loadViewRequestAdapter;
	private VehicleDO vehicleDO;
	private String date = "";
	private boolean isUnload = false;
	Vector<NameIDDo> vecReasons = new Vector<NameIDDo>();
	private PostReasonDO objPostReasonDO ;	
	private Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
	private String filepath =null, filepathDriver = null;

	
	@Override
	public void initialize()
	{
		llLoadRequestScreen = (LinearLayout) inflater.inflate(R.layout.ebs_approved, null);
		llBody.addView(llLoadRequestScreen, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
		{
			load_type = getIntent().getExtras().getInt("load_type");
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
			if(getIntent().getExtras().containsKey("date"))
				date = getIntent().getExtras().getString("date");
			
			if(getIntent().getExtras().containsKey("isUnload"))
				isUnload = getIntent().getExtras().getBoolean("isUnload");
		}
		
		loadViewRequestAdapter = new LoadViewRequestAdapter(arrayList);
		initializeControles();
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		setTypeFace(llLoadRequestScreen);
		btnMenu.setVisibility(View.INVISIBLE);
		btnContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(arrayList != null && arrayList.size() > 0)	{
					  showCustomDialog(EbsApprovedActivity.this,getString(R.string.warning), "Following movement not yet shipped.Do you want to continue ?.", "Yes", "No", "ebsapproved");
					Intent intent = new Intent(EbsApprovedActivity.this, VerifyOnHandStock.class);
					intent.putExtra("updatededDate", arrayList);
					intent.putExtra("isMenu", false);
					startActivityForResult(intent, 1111);
				}else{
					
					if(vecOrdProduct!=null && vecOrdProduct.size()>0){
						
					}else{
						showCustomDialog(EbsApprovedActivity.this, getString(R.string.warning), "There is no item in your inventory.So can't start your Day", "OK", null, "");	
					}
				}
				
//				finish();
				if(arrayList!=null && arrayList.size() > 0)
				  showCustomDialog(EbsApprovedActivity.this,getString(R.string.warning), "Following movement not yet shipped.Do you want to continue ?.", "Yes", "No", "ebsapproved");
				else{
//					Intent intent=new Intent(EbsApprovedActivity.this,SalesmanCustomerList.class);
//					startActivity(intent);
					
				if(vecOrdProduct != null && vecOrdProduct.size() > 0)
					onButtonYesClick("verify");
				 else
					showCustomDialog(EbsApprovedActivity.this, getString(R.string.warning), "There is no item in your inventory.", "OK", null, "");
				}
			
			}
		});
		loadData();
	}
	
	private ArrayList<VanLoadDO> vecOrdProduct;
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecOrdProduct				=	new VehicleDA().getAllItemToVerify();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	
	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("ebsapproved"))
		{
			showSkipReason();
			
			
			
			
		}else if(from.equalsIgnoreCase("verify")){
			Intent intent = new Intent(EbsApprovedActivity.this, VerifyOnHandStock.class);
			intent.putExtra("isMenu", false);
			startActivityForResult(intent, 1111);
			finish();
		}
	}
	
	
	private void showSkipReason() 
	{
		   vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_EbsLOad_SKIP);
		CustomBuilder builder = new CustomBuilder(EbsApprovedActivity.this, "Select Reason", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
	    		builder.dismiss();
	    		new Thread(new Runnable() 
	    		{
					@Override
					public void run() 
					{
						objPostReasonDO 			   = new PostReasonDO();
						objPostReasonDO.customerSiteID = preference.getStringFromPreference(preference.CUSTOMER_SITE_ID, "");
						objPostReasonDO.presellerId    = ""+preference.getStringFromPreference(preference.EMP_NO, "");
						objPostReasonDO.reason         = ""+objNameIDDo.strName;
						objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
						objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
						objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
						vecPostReasons.add(objPostReasonDO);

						if(vecPostReasons != null && vecPostReasons.size() > 0)
						{
							new CommonDA().insertAllReasons(vecPostReasons);
							uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								doContinue();
							}
						});
					}
				}).start();
		    }
	   }); 
		builder.show();
	}


	private void doContinue() 
	{
		String verfiedDate = preference.getStringFromPreference(com.winit.baskinrobbin.salesman.common.Preference.IsStockVerifiedToday,"");
		if(CalendarUtils.getOrderPostDate().equalsIgnoreCase(verfiedDate)){
			Intent intent=new Intent(EbsApprovedActivity.this,SalesmanCustomerList.class);
			startActivity(intent);
			finish();
		}else{
			Intent intent = new Intent(EbsApprovedActivity.this, VerifyOnHandStock.class);
			intent.putExtra("updatededDate", arrayList);
			intent.putExtra("isMenu", false);
			startActivityForResult(intent, 1111);
			finish();
			
			Intent intent = new Intent(EbsApprovedActivity.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			intent.putExtra("image_path", filepath);
			intent.putExtra("image_path_driver", filepathDriver);
			startActivity(intent);
			setResult(1111);
			finish();
		}	
	}
	
	
	private void initializeControles()
	{
		tvNoItemFound		= (TextView)llLoadRequestScreen.findViewById(R.id.tvNoItemFound);
		tvCode				= (TextView)llLoadRequestScreen.findViewById(R.id.tvCode);
		tvDate				= (TextView)llLoadRequestScreen.findViewById(R.id.tvDate);
		tvQty				= (TextView)llLoadRequestScreen.findViewById(R.id.tvQty);
		tvOrdersheetHeader	= (TextView)llLoadRequestScreen.findViewById(R.id.tvOrdersheetHeader);
		lvLoadRequest		= (ListView)llLoadRequestScreen.findViewById(R.id.lvLoadRequest);
		
		btnContinue         = (Button)llLoadRequestScreen.findViewById(R.id.btnContinue);
		lvLoadRequest.setAdapter(loadViewRequestAdapter);
		tvNoItemFound.setVisibility(View.GONE);
		
		if(load_type == AppStatus.UNLOAD_STOCK || load_type == AppStatus.UNLOAD_S_STOCK)
		{
			tvCode.setText("Unload Req. Code");
			tvOrdersheetHeader.setText("Unload View Request");
		}
		else
		{
			tvCode.setText("Load Req. Code");
			tvOrdersheetHeader.setText("Ebs Approved List");
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				arrayList = new InventoryDA().getAllApproved(""+load_type);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						
						if(arrayList != null && arrayList.size() >0)
						{
							
							if(loadViewRequestAdapter != null)
								loadViewRequestAdapter.refreshList(arrayList);
						}else{
							finish();
						}
					}
				});
			}
		}).start();
	}
	
	private class LoadViewRequestAdapter extends BaseAdapter
	{
		private ArrayList<LoadRequestDO> arrayList;
		public LoadViewRequestAdapter(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
		}

		public void refreshList(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount()
		{
			if(arrayList != null && arrayList.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				return arrayList.size();
			}
			else
				tvNoItemFound.setVisibility(View.VISIBLE);
			
			return 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final LoadRequestDO loadRequestDO = arrayList.get(position);
		
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.item_load_view_stock, null);
			
			TextView tvCode		= (TextView)convertView.findViewById(R.id.tvCode);
			TextView tvDate		= (TextView)convertView.findViewById(R.id.tvDate);
			TextView tvQty		= (TextView)convertView.findViewById(R.id.tvQty);
			View sideView		= (View)convertView.findViewById(R.id.sideView);
			
			sideView.setVisibility(View.VISIBLE);
			
			tvCode.setText(loadRequestDO.MovementCode);
			
			if(loadRequestDO.MovementDate.contains("-"))
				tvDate.setText(CalendarUtils.getFormatedDatefromString(loadRequestDO.MovementDate));
			else
				tvDate.setText(CalendarUtils.getFormatedDatefromString_(loadRequestDO.MovementDate));
			
			tvQty.setText(""+loadRequestDO.MovementStatus);
			
			if(!loadRequestDO.Status.equalsIgnoreCase("N"))
				sideView.setBackgroundColor(getResources().getColor(R.color.customer_served));
			else
				sideView.setBackgroundColor(getResources().getColor(R.color.red));
			
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(EbsApprovedActivity.this, AddNewLoadRequest.class);
					intent.putExtra("load_type", load_type);
					intent.putExtra("object", loadRequestDO);
					//intent.putExtra("isEditable", false);
					intent.putExtra("isFromItemClick", true);
					intent.putExtra("isfrom", "EBSApproved");
					
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("isUnload", isUnload);
					startActivity(intent);
//					finish();
				}
			});
			setTypeFace((ViewGroup) convertView);
			return convertView;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(1111);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
	}
}
*/