package com.winit.baskinrobbin.pinch;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.AddNewLoadRequest;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

@SuppressLint("ValidFragment")
public class LoadRequestFragment extends Fragment
{
	
	private LinearLayout llLoadRequestScreen;
	private TextView tvCode, tvDate, tvQty, tvOrdersheetHeader, tvNoItemFound;
	private ListView lvLoadRequest;
	private Button btnAdd, btnFinish;
	private int load_type;
	private ArrayList<LoadRequestDO> arrayList;
	private LoadViewRequestAdapter loadViewRequestAdapter;
	private VehicleDO vehicleDO;
	private String date = "";
	private boolean isUnload = false, isSummary = false;
	private LayoutInflater inflater;
	private LinearLayout llBottomBtn;
	
	@SuppressLint("ValidFragment")
	public LoadRequestFragment(int load_type,VehicleDO vecVehicleDO,String date,
			boolean isUnload,boolean isSummary,ArrayList<LoadRequestDO> arrayList) 
	{
		this.load_type = load_type;
		this.vehicleDO = vecVehicleDO;
		this.date = date;
		this.isUnload = isUnload;
		this.isSummary = isSummary;
		this.arrayList = arrayList;
	}

	public LoadRequestFragment(){
		  
	 }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		llLoadRequestScreen = (LinearLayout) inflater.inflate(R.layout.load_request_screen, null);
		this.inflater = inflater;
		loadViewRequestAdapter = new LoadViewRequestAdapter(arrayList);
		initializeControles();
		
		return llLoadRequestScreen;
	}
	
	private void initializeControles()
	{
		tvNoItemFound		= (TextView)llLoadRequestScreen.findViewById(R.id.tvNoItemFound);
		tvCode				= (TextView)llLoadRequestScreen.findViewById(R.id.tvCode);
		tvDate				= (TextView)llLoadRequestScreen.findViewById(R.id.tvDate);
		tvQty				= (TextView)llLoadRequestScreen.findViewById(R.id.tvQty);
		tvOrdersheetHeader	= (TextView)llLoadRequestScreen.findViewById(R.id.tvOrdersheetHeader);
		lvLoadRequest		= (ListView)llLoadRequestScreen.findViewById(R.id.lvLoadRequest);
		llBottomBtn			= (LinearLayout)llLoadRequestScreen.findViewById(R.id.llBottomBtn);
		
		btnAdd				= (Button)llLoadRequestScreen.findViewById(R.id.btnAdd);
		btnFinish			= (Button)llLoadRequestScreen.findViewById(R.id.btnFinish);
		
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
			tvOrdersheetHeader.setText("Load View Request");
		}
		
		llBottomBtn.setVisibility(View.GONE);
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (isVisibleToUser && loadViewRequestAdapter != null) {
				loadViewRequestAdapter.refreshList(arrayList);
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
					Intent intent = new Intent(getActivity(), AddNewLoadRequest.class);
					intent.putExtra("load_type", load_type);
					intent.putExtra("object", loadRequestDO);
					intent.putExtra("isEditable", false);
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("isUnload", isUnload);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	
}