package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;

public class FreeDeliveryCustomerList  extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearchText;
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private boolean isHistory;
	
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
			isHistory = getIntent().getExtras().getBoolean("isHistory");
		intialiseControls();
		
		tvListViewHeader.setText("Customers");
		
		objCustomerDetailsBL = new CustomerDetailsDA();
		loadCustomerList();
		lvCustomerList.setCacheColorHint(0);
		lvCustomerList.setFadingEdgeLength(0);
		lvCustomerList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
		adapter = new CustomerListAdapter(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(adapter);
		lvCustomerList.setDivider(null);
		
		lvCustomerList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				if(arrayListEvent != null && arrayListEvent.size() >0 )
				{
					String strCustomerNmae = arrayListEvent.get(arg2).siteName;
					Intent intent = new Intent(FreeDeliveryCustomerList.this, FreeDeliveryActivity.class);
					intent.putExtra("SiteName", strCustomerNmae);
					startActivity(intent);
				}
			}
		});
		
		//functionality for the search edit text
		etSearchText.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(!s.toString().equalsIgnoreCase(""))
				{
					ArrayList<JourneyPlanDO> arrayTemp = new ArrayList<JourneyPlanDO>();
					for(int index = 0; arrayListEvent != null && index < arrayListEvent.size(); index++)
					{
						JourneyPlanDO obj = (JourneyPlanDO) arrayListEvent.get(index);
						String strText = ((JourneyPlanDO)obj).siteName;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()))
							arrayTemp.add(obj);
					}
					adapter.refresh(arrayTemp);
				}
				else
					adapter.refresh(arrayListEvent);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			
			@Override
			public void afterTextChanged(Editable s) 
			{}
		});
		
		setTypeFace(llCusomerList);
		
	}
	
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		tvListViewHeader	= (TextView)llCusomerList.findViewById(R.id.tvListViewHeader);
		lvCustomerList 		= (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearchText		= (EditText)llCusomerList.findViewById(R.id.etSearchText);
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		*/
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public void loadCustomerList()
	{
		arrayListEvent = new ArrayList<JourneyPlanDO>();
		showLoader("Loading customers...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				arrayListEvent = objCustomerDetailsBL.getJourneyPlanForTeleOrder(null);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(adapter!=null)
							adapter.refresh(arrayListEvent);
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	public class CustomerListAdapter extends BaseAdapter
	{
		ArrayList<JourneyPlanDO> arrListCustomers;
		public CustomerListAdapter(ArrayList<JourneyPlanDO> arrayListEvent)
		{
			arrListCustomers = arrayListEvent;
		}
		@Override
		public int getCount() 
		{
			if(arrListCustomers != null)
				return arrListCustomers.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			JourneyPlanDO mallsDetails = (JourneyPlanDO) arrListCustomers.get(position);
			
			if(convertView ==null)
				convertView					=	(LinearLayout)getLayoutInflater().inflate(R.layout.customerhitorypopup, null);
			
			TextView tvRoleName				=	(TextView)convertView.findViewById(R.id.tvCustomerName);
			TextView tvCustomerSite			=	(TextView)convertView.findViewById(R.id.tvCustomerSite);
			TextView tvBalanceAmountShow	=	(TextView)convertView.findViewById(R.id.tvBalanceAmountShow);
			TextView tvSitenameTitle		=	(TextView)convertView.findViewById(R.id.tvSitenameTitle);
			
			tvRoleName.setTextSize(15);
			tvRoleName.setGravity(Gravity.CENTER_VERTICAL);
			
			/*tvSitenameTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCustomerSite.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvRoleName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvBalanceAmountShow.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			*/
			tvBalanceAmountShow.setText(mallsDetails.addresss1);
			tvRoleName.setText(mallsDetails.siteName);
			tvCustomerSite.setText(mallsDetails.customerId);
			
			convertView.setTag(mallsDetails);
			tvRoleName.setPadding(0, 4, 0, 4);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			return convertView;
		}
		
		private void refresh(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			arrListCustomers = arrayListEvent;
			notifyDataSetChanged();
		}
	}
}
