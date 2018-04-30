package com.winit.baskinrobbin.salesman;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FreeDeliveryActivity extends BaseActivity
{
	private LinearLayout llFreeDelivery;
	private TextView tvListViewHeader;
	private String SiteName = "";
	private Button btnAddItems, btnSave;
	private TextView tvCustomerName;
	@Override
	public void initialize()
	{
		llFreeDelivery = (LinearLayout) inflater.inflate(R.layout.free_delivery_list, null);
		llBody.addView(llFreeDelivery,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
		{
			SiteName = getIntent().getExtras().getString("SiteName");
		}
		intialiseControls();
		
		tvCustomerName.setText(SiteName);
		
		setTypeFace(llFreeDelivery);
	}
	private void intialiseControls() 
	{
		tvListViewHeader	= (TextView)llFreeDelivery.findViewById(R.id.tvListViewHeader);
		tvCustomerName		= (TextView)llFreeDelivery.findViewById(R.id.tvCustomerName);
		btnAddItems			= (Button)llFreeDelivery.findViewById(R.id.btnAddItems);
		btnSave				= (Button)llFreeDelivery.findViewById(R.id.btnSave);
		
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		btnAddItems.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSave.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
}
