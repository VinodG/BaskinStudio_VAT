package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;

public class RecomendedAddStock extends BaseActivity{

	private LinearLayout llRecomendedStocks;
	private TextView tvRecomendedItem, tvYourOwnItem, tvTitle;
	private Button btnFinish;
	private int LOAD_STOCK = 0;
	private VehicleDO vehicleDO;
	private String date;
	private boolean isCheckInDemand = false;
	@Override
	public void initialize()
	{
		llRecomendedStocks = (LinearLayout) inflater.inflate(R.layout.recomendqt, null);
		llBody.addView(llRecomendedStocks, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	
		initializeControles();
		setTypeFace(llRecomendedStocks);
		
		if(getIntent().getExtras() != null)
		{
			LOAD_STOCK = getIntent().getExtras().getInt("load_type");
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
			if(getIntent().getExtras().containsKey("date"))
				date = getIntent().getExtras().getString("date");
			if(getIntent().getExtras().containsKey("isCheckInDemand"))
				isCheckInDemand = getIntent().getExtras().getBoolean("isCheckInDemand");
		}
		
		tvRecomendedItem.setVisibility(View.GONE);
		
		tvRecomendedItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
			 	Intent intent = new Intent(RecomendedAddStock.this, AddNewLoadRequest.class);
				intent.putExtra("load_type", LOAD_STOCK);
				intent.putExtra("isRecomended", true);
				intent.putExtra("object", vehicleDO);
				intent.putExtra("date", date);
				intent.putExtra("checkInDemand", isCheckInDemand);
				startActivityForResult(intent, 1111);
				
//				Intent intent = new Intent(RecomendedAddStock.this, LoadRequestActivity.class);
//				intent.putExtra("load_type", AppConstants.LOAD_STOCK);
//				intent.putExtra("isRecomended", true);
//				startActivity(intent);
			}
		});
		tvYourOwnItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(RecomendedAddStock.this, AddNewLoadRequest.class);
				intent.putExtra("load_type", LOAD_STOCK);
				intent.putExtra("object", vehicleDO);
				intent.putExtra("date", date);
				intent.putExtra("checkInDemand", isCheckInDemand);
				startActivityForResult(intent, 1111);
//				Intent intent = new Intent(RecomendedAddStock.this, LoadRequestActivity.class);
//				intent.putExtra("load_type", AppConstants.LOAD_STOCK);
//				startActivity(intent);
			}
		});
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});	
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	private void initializeControles()
	{
		tvRecomendedItem = (TextView) llRecomendedStocks.findViewById(R.id.tvRecomendedItem);
		tvYourOwnItem	 = (TextView) llRecomendedStocks.findViewById(R.id.tvYourOwnItem);
		btnFinish		 = (Button) llRecomendedStocks.findViewById(R.id.btnFinish);
		tvTitle			 = (TextView) llRecomendedStocks.findViewById(R.id.tvTitle);
		
		/*tvRecomendedItem.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvYourOwnItem.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinish.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);	*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
	}
}
