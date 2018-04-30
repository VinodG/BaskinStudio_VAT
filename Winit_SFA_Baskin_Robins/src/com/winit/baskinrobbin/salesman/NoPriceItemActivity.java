package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.NonPriceItemsDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class NoPriceItemActivity extends BaseActivity {

	private LinearLayout llNonInvoicedItemsList;
	private TextView  tvNoRecorFound,tvUsername,tvDate,tvVehicleCode/*,tvNoOfItems*/;
	private ListView lvItems;
	private EditText etSearch;
	private ImageView ivSearchCross;
	private ArrayList<NonPriceItemsDO> vecNonInvoiced;
	private NonInvoicedAdapter userAgencyAdapter;
	private Vector<String> vecFilters = new Vector<String>();
	@Override
	public void initialize() {
		llNonInvoicedItemsList = (LinearLayout)inflater.inflate(R.layout.nopricelist_layout, null);
		llBody.addView(llNonInvoicedItemsList,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		intialiseControls();

		showLoader(getString(R.string.please_wait));
		
	//	setTypeFaceRobotoNormal(llNonInvoicedItemsList);
		
		vecNonInvoiced  = new ArrayList<NonPriceItemsDO>();
		lvItems.setCacheColorHint(0);
		lvItems.setVerticalFadingEdgeEnabled(false);
		lvItems.setSelector(R.color.transparent);
		lvItems.setVerticalScrollBarEnabled(false);
		lvItems.setDivider(null);
		userAgencyAdapter = new NonInvoicedAdapter(vecNonInvoiced);
		lvItems.setAdapter(userAgencyAdapter);
		
		tvUsername.setText(""+preference.getStringFromPreference(Preference.USER_NAME, ""));
		tvDate.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
		tvVehicleCode.setText("Vehicle Code : "+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, ""));
		
		loadUnInvoicedItemsList();
		
		etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				ArrayList<NonPriceItemsDO> arrayTemp = new ArrayList<NonPriceItemsDO>();
				for(int index = 0; vecNonInvoiced != null && index < vecNonInvoiced.size(); index++)
				{
					NonPriceItemsDO obj 	= (NonPriceItemsDO) vecNonInvoiced.get(index);
					String strText 		= (obj).item;
					String strText1 	= (obj).description;
					
					
					if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strText1.toLowerCase().contains(s.toString().toLowerCase()))
						arrayTemp.add(obj);
				}
				
				if(arrayTemp != null && arrayTemp.size() > 0)
				{
					userAgencyAdapter.refresh(arrayTemp);
					tvNoRecorFound.setVisibility(View.GONE);
					lvItems.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoRecorFound.setVisibility(View.VISIBLE);
					lvItems.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
	}
	
	public void intialiseControls()
	{
		lvItems 			= (ListView)llNonInvoicedItemsList.findViewById(R.id.lvpricevariation);
		etSearch			= (EditText)llNonInvoicedItemsList.findViewById(R.id.etSearch);
		tvNoRecorFound		= (TextView)llNonInvoicedItemsList.findViewById(R.id.tvNoRecorFound);
		tvUsername		=	(TextView)llNonInvoicedItemsList.findViewById(R.id.tvUsername);
		tvDate			=	(TextView)llNonInvoicedItemsList.findViewById(R.id.tvDate);
		tvVehicleCode	=	(TextView)llNonInvoicedItemsList.findViewById(R.id.tvVehicleCode);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		
		vecFilters.add("ItemDescription");
		vecFilters.add("ItemCode");
	}
	public void loadUnInvoicedItemsList()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{				
				vecNonInvoiced = new CommonDA().getAllNonPriceItems();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						
						if(vecNonInvoiced != null && vecNonInvoiced.size() > 0)
						{
							userAgencyAdapter.refresh(vecNonInvoiced);
						}
						else{
							tvNoRecorFound.setText("No Record Available");
							tvNoRecorFound.setVisibility(View.VISIBLE);	
						}
					
						hideLoader();
					}
				});
			}
		}).start();
	}
	public class NonInvoicedAdapter extends BaseAdapter {


		private ArrayList<NonPriceItemsDO> vecUserAgencyTargets;

		public NonInvoicedAdapter(ArrayList<NonPriceItemsDO> vecUserAgencyTargets)
		{
			this.vecUserAgencyTargets = vecUserAgencyTargets;

		}

		@Override
		public int getCount() {

			if (vecUserAgencyTargets != null && vecUserAgencyTargets.size() > 0)
				return vecUserAgencyTargets.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = (LinearLayout)inflater.inflate( R.layout.item_list_add_stock, null);
				
				viewHolder.tvProductKey = (EditText) convertView .findViewById(R.id.tvProductKey);
				viewHolder.tvItemDes = (EditText) convertView .findViewById(R.id.tvVendorName);
			

				viewHolder.tvItemBatchCode = (EditText) convertView .findViewById(R.id.tvItemBatchCode);
				viewHolder.etUOM = (EditText) convertView .findViewById(R.id.etUOM);
				viewHolder.etQt = (EditText) convertView .findViewById(R.id.etQt);
				viewHolder.etTotalQt = (EditText) convertView .findViewById(R.id.etTotalQt);
				viewHolder.etInvoice1 = (EditText) convertView .findViewById(R.id.etInvoice1);
				viewHolder.ivAcceptCheckItems = (ImageView) convertView .findViewById(R.id.ivAcceptCheckItems);
				
				viewHolder.tvItemBatchCode.setVisibility(View.GONE);
				viewHolder.etUOM.setVisibility(View.GONE);
				viewHolder.etQt.setVisibility(View.GONE);
				viewHolder.etTotalQt.setVisibility(View.GONE);
				viewHolder.etInvoice1.setVisibility(View.GONE);
				viewHolder.ivAcceptCheckItems.setVisibility(View.GONE);
				
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();
			
			NonPriceItemsDO userAgencyTargetDO = vecUserAgencyTargets.get(position);
			
			viewHolder.tvItemBatchCode.setText(""+userAgencyTargetDO.item);
			viewHolder.tvItemDes.setText(""+userAgencyTargetDO.description);
			
		//	setTypeFaceRobotoBold((ViewGroup)convertView);
			return convertView;
		}
		
		public void refresh(ArrayList<NonPriceItemsDO> vecUserAgencyTargets)
		{
			this.vecUserAgencyTargets = vecUserAgencyTargets;
			notifyDataSetChanged();
		}
		
		public class ViewHolder {
			TextView tvProductKey,tvItemDes,tvItemBatchCode;
			EditText etUOM,etQt,etTotalQt,etInvoice1;
			ImageView  ivAcceptCheckItems;
		}

		
	}
	

}
