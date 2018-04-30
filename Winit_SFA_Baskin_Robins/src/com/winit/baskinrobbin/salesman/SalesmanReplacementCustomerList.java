package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;

@SuppressLint("DefaultLocale") public class SalesmanReplacementCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearchText;
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
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
				hideKeyBoard(etSearchText);
				JourneyPlanDO mallsDetails = (JourneyPlanDO)arg1.getTag();
				
				preference.saveStringInPreference(Preference.LAST_CUSTOMER_SITE_ID, mallsDetails.site);
				preference.commitPreference();
				Intent intent =	new Intent(SalesmanReplacementCustomerList.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("name",""+getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("from", "replacement");
				intent.putExtra("isMenu", true);
				startActivity(intent);
			}
		});
		etSearchText.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
						String string = ((JourneyPlanDO)obj).site;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || string.toLowerCase().contains(s.toString().toLowerCase()))
							arrayTemp.add(obj);
					}
					
					if(arrayTemp != null && arrayTemp.size() > 0)
					{
						adapter.refresh(arrayTemp);
						tvNoRecorFound.setVisibility(View.GONE);
						lvCustomerList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoRecorFound.setVisibility(View.VISIBLE);
						lvCustomerList.setVisibility(View.GONE);
					}
				}
				else
				{
					if(arrayListEvent != null && arrayListEvent.size() > 0)
					{
						adapter.refresh(arrayListEvent);
						tvNoRecorFound.setVisibility(View.GONE);
						lvCustomerList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoRecorFound.setVisibility(View.VISIBLE);
						lvCustomerList.setVisibility(View.GONE);
					}                         
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			@Override
			public void afterTextChanged(Editable s) 
			{}
		});
		
		etSearchText.setHint("Search by Site Name/ Site Id");
	}
	
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		tvListViewHeader	= (TextView)llCusomerList.findViewById(R.id.tvListViewHeader);
		lvCustomerList 		= (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearchText		= (EditText)llCusomerList.findViewById(R.id.etSearchText);
		tvNoRecorFound		= (TextView)llCusomerList.findViewById(R.id.tvNoRecorFound);
	
		//setting type face
		tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoRecorFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
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

		@SuppressWarnings("deprecation")
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
			
			tvSitenameTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCustomerSite.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvRoleName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvBalanceAmountShow.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			tvBalanceAmountShow.setText(mallsDetails.addresss1);
			tvRoleName.setText(mallsDetails.siteName);
			tvCustomerSite.setText(mallsDetails.site);
			
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
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		loadCustomerList();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(etSearchText != null)
					etSearchText.setText("");
				
			}
		}, 100);
	}
}
