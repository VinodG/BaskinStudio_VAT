package com.winit.baskinrobbin.pinch;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.SalesmanLPOOrderDetail;
import com.winit.baskinrobbin.salesman.SalesmanOrderDetail;
import com.winit.baskinrobbin.salesman.SalesmanReplacementOrderDetail;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

@SuppressLint("ValidFragment")
public class ListOrderFragment extends Fragment
{
	private ListView lvOrderList;
	private OrderListAdapter orderListAdapter;
	private Vector<OrderDO> vecOrderList;
	private Context context;
	private TextView tvNoDataFound;
	private LinearLayout llMain;
	private JourneyPlanDO mallsDetails;
	private boolean isFromCustomerCheckIn = false;
	public ListOrderFragment(Context context, Vector<OrderDO> vecOrderList, JourneyPlanDO mallsDetails, boolean isFromCustomerCheckIn) {
		this.vecOrderList = vecOrderList;
		this.context = context;
		this.mallsDetails = mallsDetails;
		this.isFromCustomerCheckIn = isFromCustomerCheckIn;
	}
	
	public ListOrderFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 	= (LinearLayout) inflater.inflate(R.layout.listview,null);
		initializeControls();
		if(context!=null)
			((BaseActivity)context).setTypeFace(llMain);
		return llMain;
	}
	
	private void initializeControls()
	{
		
		tvNoDataFound		=	(TextView)llMain.findViewById(R.id.tvNoDataFound);
		lvOrderList			=	(ListView)llMain.findViewById(R.id.lv);
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setScrollbarFadingEnabled(true);
		
		if(vecOrderList!=null && vecOrderList.size()>0)
		{
			orderListAdapter = new OrderListAdapter(vecOrderList);
			lvOrderList.setAdapter(orderListAdapter);
					
			tvNoDataFound.setVisibility(View.GONE);
			lvOrderList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoDataFound.setVisibility(View.VISIBLE);
			lvOrderList.setVisibility(View.GONE);
		}
	}
	
	@SuppressLint("NewApi")
	public class OrderListAdapter extends BaseAdapter
	{
		private Vector<OrderDO> vecOrderList;
		public OrderListAdapter(Vector<OrderDO> vecOrderList) 
		{
			this.vecOrderList = vecOrderList;
		}
		@Override
		public int getCount() 
		{
			if(vecOrderList!=null && vecOrderList.size()>0)
				return vecOrderList.size();
			
			return 0;
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
		public void refresh(Vector<OrderDO> vecOrder)
		{
			this.vecOrderList=vecOrder;
			notifyDataSetChanged();
		}
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			OrderDO objOrderList = (OrderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					=	(LinearLayout)LayoutInflater.from(context).inflate(R.layout.delivery_status, null);
			
			TextView tvOrderNo				=	(TextView)convertView.findViewById(R.id.tvOrderNoStatus);
			TextView tvCustomerName			=	(TextView)convertView.findViewById(R.id.tvCustomerNameStatus);
			TextView tvCustomerLocation		=	(TextView)convertView.findViewById(R.id.tvCustomerLocationStatus);
			TextView tvSatus				=	(TextView)convertView.findViewById(R.id.tvSatusStatus);
			
			View sideView					=	convertView.findViewById(R.id.sideView);
			
			sideView.setVisibility(View.VISIBLE);
			tvSatus.setVisibility(View.GONE);
			
			tvCustomerLocation.setText("Order Date : "+CalendarUtils.getFormatedDatefromString(objOrderList.InvoiceDate));
			
			if(objOrderList.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				tvOrderNo.setText("Return Order No.: "+objOrderList.OrderId);
				
			else if(objOrderList.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
				tvOrderNo.setText("Replacement Order No.: "+objOrderList.OrderId);
			
			else if(objOrderList.orderSubType != null && objOrderList.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER))
			{
				tvOrderNo.setText("LPO Order No.: "+objOrderList.OrderId);
				tvCustomerLocation.setText("Delivery Date : "+CalendarUtils.getFormatedDatefromString(objOrderList.DeliveryDate));
			}
			else if(objOrderList.TRXStatus.equalsIgnoreCase("H"))
				tvOrderNo.setText("On-Hold Order No.: "+objOrderList.OrderId);
			else
				tvOrderNo.setText("Sales Order No.: "+objOrderList.OrderId);
			
			tvCustomerName.setText(objOrderList.strCustomerName + ", "+objOrderList.strAddress1);
			
			if(objOrderList.orderSubType != null && objOrderList.orderSubType.equalsIgnoreCase(AppConstants.FREE_DELIVERY_ORDER))
				tvCustomerLocation.setVisibility(View.GONE);
			else
				tvCustomerLocation.setVisibility(View.VISIBLE);
			
			convertView.setTag(objOrderList);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					
					final OrderDO objOrderList  = (OrderDO) v.getTag();
					((BaseActivity)context).showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							if(mallsDetails == null)
							{
								ArrayList<JourneyPlanDO> vec = new CustomerDetailsDA().getJourneyPlanForTeleOrder(objOrderList.CustomerSiteId);
								if(vec != null && vec.size() > 0)
									mallsDetails = vec.get(0);
								
								mallsDetails.isServed = "0";
							}
							else
								mallsDetails.isServed = "1";
								
							((BaseActivity)context).runOnUiThread(new Runnable()
						    {
								@Override
								public void run() 
								{
									((BaseActivity)context).hideLoader();
									
									if(objOrderList.orderSubType != null 
											&& objOrderList.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER) && 
											StringUtils.getInt(objOrderList.LPOStatus) != AppStatus.LPO_STATUS_UPDATED)
									{
										Intent intent = new Intent(context, SalesmanLPOOrderDetail.class);
										intent.putExtra("orderid", objOrderList);
										intent.putExtra("mallsDetails", mallsDetails);
										intent.putExtra("isFromCustomerCheckIn", isFromCustomerCheckIn);
										startActivityForResult(intent, 100);
									}
									else
									{
										if(objOrderList.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
										{
											Intent intent = new Intent(context, SalesmanReplacementOrderDetail.class);
											intent.putExtra("mallsDetails", mallsDetails);
											intent.putExtra("orderid", objOrderList);
											startActivityForResult(intent, 100);
										}
										else
										{
											Intent intent = new Intent(context, SalesmanOrderDetail.class);
											intent.putExtra("mallsDetails", mallsDetails);
											intent.putExtra("orderid", objOrderList);
											startActivityForResult(intent, 100);
										}
									}
								}
							});
						}
					}).start();
				}
			});
			
			
			if(objOrderList.pushStatus == 1 || objOrderList.pushStatus == 2||objOrderList.pushStatus == 10){
				if(objOrderList.TRXStatus.equals("H")){
					tvSatus.setVisibility(View.VISIBLE);
					if(objOrderList.pushStatus == 1 || objOrderList.pushStatus == 2)
						tvSatus.setText("Pending");
					else if(objOrderList.pushStatus == 10){
						tvSatus.setText("Approved");
						tvSatus.setBackground(getResources().getDrawable(R.drawable.status_green));
					}
				}else{
					tvSatus.setVisibility(View.GONE);
				}
				sideView.setBackgroundColor(getResources().getColor(R.color.customer_served));
			}
			else if(objOrderList.pushStatus == -10){
				tvSatus.setVisibility(View.VISIBLE);
				tvSatus.setText("Rejected");
				tvSatus.setBackground(getResources().getDrawable(R.drawable.status_red));
				sideView.setBackgroundColor(getResources().getColor(R.color.ash_gry));
			}
			else
				sideView.setBackgroundColor(getResources().getColor(R.color.red));
			
			((BaseActivity)context).setTypeFace((ViewGroup) convertView);
			
			return convertView;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 900)
			((Activity)context).finish();
	}
}