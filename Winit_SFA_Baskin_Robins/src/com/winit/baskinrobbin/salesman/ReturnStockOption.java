package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;

public class ReturnStockOption extends BaseActivity
{
	//declaration of variables
	private LinearLayout llSummaryofDay;
	private TextView tvDaySummary, tvSellable, tvNonSellable;
	private boolean isNormal;
	private VehicleDO vehicleDO;

	@Override
	public void initialize() 
	{
		//inflate the summary-of-day layout
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.return_stock_option, null);
		llBody.addView(llSummaryofDay,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		intialiseControls();
		
		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("isNormal"))
			isNormal = getIntent().getExtras().getBoolean("isNormal");
		
		if(getIntent().getExtras()!= null && getIntent().getExtras().containsKey("object"))
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
		
		setTypeFace(llSummaryofDay);
		
		tvSellable.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(ReturnStockOption.this, LoadRequestActivity.class);
				
				intent.putExtra("load_type", AppStatus.UNLOAD_S_STOCK);
				if(isNormal)
				{
					intent.putExtra("object", vehicleDO);
					intent.putExtra("isUnload", true);
				}
				else
				{
					intent.putExtra("isUnload", true);
				}
				startActivity(intent);
			}
		});
		
		tvNonSellable.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(ReturnStockOption.this, LoadRequestActivity.class);
				intent.putExtra("load_type", AppStatus.UNLOAD_STOCK);
				intent.putExtra("isUnload", true);
				startActivity(intent);
			}
		});
	}
	
	public void intialiseControls()
	{
		//getting Id's of TextView
		tvDaySummary		=	(TextView)llSummaryofDay.findViewById(R.id.tvDaySummary);
		tvSellable			=	(TextView)llSummaryofDay.findViewById(R.id.tvSellable);
		tvNonSellable		=	(TextView)llSummaryofDay.findViewById(R.id.tvNonSellable);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnMenu.setVisibility(View.GONE);
		 setActionInViews(btnLogo, false);
			setActionInViews(llMenu, false);
			setActionInViews(btnMenu, false);
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			setResult(2000);
			finish();
		}
	}
}
