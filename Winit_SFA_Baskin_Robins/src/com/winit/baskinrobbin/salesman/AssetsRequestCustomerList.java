package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerOrderDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.MallsDetails;

@SuppressLint("DefaultLocale") 
public class AssetsRequestCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearchText;
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private boolean isHistory;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
			isHistory = getIntent().getExtras().getBoolean("isHistory");
		
		intialiseControls();
		setTypeFace(llCusomerList);
		
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
				
				Intent intent	=	new Intent(AssetsRequestCustomerList.this,CustomerAssetsListActivity_New.class);
				intent.putExtra("mallsDetails", mallsDetails);
				startActivity(intent);
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
						JourneyPlanDO obj 	= arrayListEvent.get(index);
						String strText 		= (obj).siteName;
						String strText1 	= (obj).site;
						String strText2 	= (obj).partyName;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) 
								|| strText1.toLowerCase().contains(s.toString().toLowerCase())
								|| strText2.toLowerCase().contains(s.toString().toLowerCase()))
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
	
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoRecorFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
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
			
			/*tvSitenameTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCustomerSite.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvRoleName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvBalanceAmountShow.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			
			tvBalanceAmountShow.setText(mallsDetails.addresss1);
			tvRoleName.setText(mallsDetails.siteName + " ("+mallsDetails.partyName+")");
			tvCustomerSite.setText(mallsDetails.customerId);
			
			convertView.setTag(mallsDetails);
			tvRoleName.setPadding(0, 4, 0, 4);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			setTypeFace((ViewGroup) convertView);
			return convertView;
			
		}
		
		private void refresh(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			arrListCustomers = sortedCustomerList(arrayListEvent);
			notifyDataSetChanged();
		}
		
		private ArrayList<JourneyPlanDO> sortedCustomerList(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			synchronized (arrayListEvent) {
				if (arrayListEvent.size() > 0) {
				    Collections.sort(arrayListEvent, new Comparator<JourneyPlanDO>() {
				        @Override
				        public int compare(final JourneyPlanDO object1, final JourneyPlanDO object2) {
				            return object1.siteName.compareTo(object2.siteName);
				        }
				       } );
				   }
			}
			
			return arrayListEvent;
		}
	}
	
	private int order_type =-1;
	private void showOptionDialog(final MallsDetails mallsDetails)
	{
		final String strArray[]= new String[2];
		
		strArray[0] = "Cash Sales Order";
		strArray[1] = "Advance Order";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setSingleChoiceItems(strArray, -1, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				String str =  strArray[which];
				if(str.equalsIgnoreCase("Advance Order"))
					order_type = AppStatus.ADVANCE_ORDER_TYPE;
				else
					order_type = AppStatus.SALES_ORDER_TYPE;
				
				Intent intent = new Intent(AssetsRequestCustomerList.this, PresellerCaptureInventry.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("order_type", order_type);
				startActivity(intent);
				
//				Intent intent = new Intent(SalesmanCustomerList.this, SalesManAdvanceTakeOrder.class);
//				intent.putExtra("order_type", order_type);
//				intent.putExtra("mallsDetails", mallsDetails);
//				startActivity(intent);
			}
		});
		
		builder.create().show();
	}
	
	 /**
     * Method to check the Sales Order is Generated or not for the particular customer on current dateofJorney
     * @param customerName
     * @param currentDate
     * @return boolean
     */
    private boolean isOrderGenerated(String customerID , String currentDate)
    {
    	//creating object of CustomerOrderDA class 
    	CustomerOrderDA objCustomerOrderBL = new CustomerOrderDA();
    	//getting the Sales order information
    	if(objCustomerOrderBL.isCustomerOrderGenerated(customerID, currentDate))
    		return true;
    	return false;
    }
}
