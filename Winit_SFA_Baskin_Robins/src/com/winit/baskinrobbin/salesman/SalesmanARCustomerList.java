package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

@SuppressLint("DefaultLocale") 
public class SalesmanARCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearchText;
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private HashMap<String, CustomerCreditLimitDo> hmCreditLimits;
	
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		intialiseControls();
		setTypeFace(llCusomerList);
		
		tvListViewHeader.setText("Customers");
		
		objCustomerDetailsBL = new CustomerDetailsDA();
		loadCustomerList();
		lvCustomerList.setCacheColorHint(0);
		lvCustomerList.setFadingEdgeLength(0);
		lvCustomerList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvCustomerList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
		adapter = new CustomerListAdapter(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(adapter);
		
		lvCustomerList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				hideKeyBoard(etSearchText);
				if(arg1 != null)
				{
					JourneyPlanDO JourneyPlanDO  = (JourneyPlanDO) arg1.getTag();
					
					if(TextUtils.isEmpty(curencyCode))
					{
						preference.saveStringInPreference(Preference.CURRENCY_CODE, JourneyPlanDO.currencyCode);
						preference.commitPreference();
					}
					
					Intent intent = new Intent(SalesmanARCustomerList.this, PendingInvoices.class);
					intent.putExtra("AR", true);
					intent.putExtra("mallsDetails", JourneyPlanDO);
					intent.putExtra("fromMenu", true);
					if(JourneyPlanDO.customerType!= null && JourneyPlanDO.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
						intent.putExtra("isCredit", true);
					startActivity(intent);
				}
			}
		});
		
		etSearchText.setHint("Search by Site Name/ Site Id");
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
						JourneyPlanDO obj 	= (JourneyPlanDO) arrayListEvent.get(index);
						String strText 		= (obj).siteName + (obj).addresss1+ (obj).addresss2;
						String strText1 	= (obj).site;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) 
							|| strText1.toLowerCase().contains(s.toString().toLowerCase()))
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
	}
	
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		tvListViewHeader	= (TextView)llCusomerList.findViewById(R.id.tvListViewHeader);
		lvCustomerList 		= (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearchText		= (EditText)llCusomerList.findViewById(R.id.etSearchText);
		tvNoRecorFound		= (TextView)llCusomerList.findViewById(R.id.tvNoRecorFound);
	
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
			
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.ar_callistviewcellxml_cell, null);
			
			TextView tvSiteName		=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvPaymentType 	= 	(TextView)convertView.findViewById(R.id.tvPaymentType);
			TextView tvCreditLimit	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
			TextView tvAddress		=	(TextView)convertView.findViewById(R.id.tvAddress);
			View sideView 			=	(View)convertView.findViewById(R.id.sideView);
			sideView.setVisibility(View.INVISIBLE);	
			
			tvSiteName.setText(mallsDetails.siteName);
			tvAddress.setText(mallsDetails.addresss1 +", "+mallsDetails.addresss2);
			
			float overDue = 0;
			String key = "";
			
			if(mallsDetails.CREDIT_LEVEL != null && mallsDetails.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT))
				key = mallsDetails.customerId + AppConstants.CREDIT_LEVEL_ACCOUNT;
			else
				key = mallsDetails.site + AppConstants.CREDIT_LEVEL_SITE;
			
			if(hmCreditLimits != null && hmCreditLimits.containsKey(key))
				overDue = StringUtils.getFloat(hmCreditLimits.get(key).outStandingAmount);
			
			tvCreditLimit .setText(diffAmt.format(overDue));
			tvPaymentType    .setText(mallsDetails.customerType);
		
			convertView.setTag(mallsDetails);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			setTypeFace((ViewGroup) convertView);
			
			return convertView;
		}
		
		private void refresh(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			arrListCustomers = arrayListEvent;
			notifyDataSetChanged();
		}
		
		private void refresh() 
		{
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{	
				hmCreditLimits	= new CustomerDA().getCreditLimits(); 
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(adapter!=null)
							adapter.refresh();
					}
				});
			}
		}).start();
	}
}
