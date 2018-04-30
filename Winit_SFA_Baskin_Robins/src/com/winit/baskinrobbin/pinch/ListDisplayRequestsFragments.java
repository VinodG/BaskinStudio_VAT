package com.winit.baskinrobbin.pinch;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.AddNewLoadRequest;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.LoadRequestActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class ListDisplayRequestsFragments extends Fragment{
	public LayoutInflater inflater;
	private Context mContext;
	private ArrayList<LoadRequestDO> arrLoadRequest;
	private int tabposition;
	private TextView tvNoDataFound;	
	private LinearLayout llMain;
	private ListView lvRequestList;
	public LoadViewRequestAdapter loadrequestAdapter;
	public int loadtype;
	public VehicleDO vehicleDO;
	private boolean isUnload = false,isEditable=false;
	public ListDisplayRequestsFragments(Context context,int position,ArrayList<LoadRequestDO> arrLoadRequest ,int loadtype,VehicleDO vehicleDO,boolean isUnload,boolean isEditable) {
		this.mContext=context;
		this.tabposition=position;
		this.arrLoadRequest=arrLoadRequest;
		this.inflater=inflater 				= 		((BaseActivity) context).getLayoutInflater();
		this.loadtype=loadtype;
		this.vehicleDO=vehicleDO;
		this.isUnload=isUnload;
		this.isEditable=isEditable;
	}
	public ListDisplayRequestsFragments(){
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 	= (LinearLayout) inflater.inflate(R.layout.listview,null);
		initializeControls();
		return llMain;
	}
	
	private void initializeControls()
	{
		tvNoDataFound		=	(TextView)llMain.findViewById(R.id.tvNoDataFound);
		lvRequestList			=	(ListView)llMain.findViewById(R.id.lv);
		lvRequestList.setCacheColorHint(0);
		lvRequestList.setScrollbarFadingEnabled(true);

		loadrequestAdapter = new LoadViewRequestAdapter(new ArrayList<LoadRequestDO>());
		lvRequestList.setAdapter(loadrequestAdapter);

		if(arrLoadRequest!=null && arrLoadRequest.size()>0)
		{
			loadrequestAdapter.refreshList(arrLoadRequest);
//			lvRequestList.setOnItemClickListener(new OnItemClickListener()
//			{
//				@Override
//				public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
//				{ 
//					/*Intent intent = new Intent(mContext,ComplaintDetailsActivity.class);
//					intent.putExtra("vecComplaintsList",vecComplaintsList.get(position));
//					intent.putExtra("position_Value", tabposition);
//					intent.putExtra("Site_Id",site_Id);
//					intent.putExtra("from", from);
//					intent.putExtra("siteName",siteName);
//					startActivity(intent)*/;
//				}
//			});

			tvNoDataFound.setVisibility(View.GONE);
			lvRequestList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoDataFound.setVisibility(View.VISIBLE);
			lvRequestList.setVisibility(View.GONE);
		}
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
				tvNoDataFound.setVisibility(View.GONE);
				return arrayList.size();
			}
			else
				tvNoDataFound.setVisibility(View.VISIBLE);
			
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
					
					Intent intent = new Intent(mContext, AddNewLoadRequest.class);

					if(loadRequestDO.MovementType.equalsIgnoreCase(AppStatus.UNLOAD_COLLECTED_STOCK+"")){
						loadtype = AppStatus.UNLOAD_COLLECTED_STOCK;
					}
					intent.putExtra("load_type", loadtype);
					intent.putExtra("object", loadRequestDO);
					intent.putExtra("isEditable", isEditable);
					//intent.putExtra("isFromItemClick", true);

					intent.putExtra("isfrom", "EBSApproved");
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("isUnload", isUnload);
					startActivity(intent);
				}
			});
			((BaseActivity )mContext).setTypeFace((ViewGroup) convertView);
			return convertView;
		}
	}


}
