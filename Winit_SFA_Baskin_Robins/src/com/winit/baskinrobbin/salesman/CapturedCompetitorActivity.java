package com.winit.baskinrobbin.salesman;


import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.dataaccesslayer.CompetitorDetailDA;
import com.winit.baskinrobbin.salesman.dataobject.CompDetailDO;

public class CapturedCompetitorActivity extends BaseActivity
{
	//declaration of variables
	private LinearLayout llLayout;
	private ListView lvList;
	private CompetitorDetailAdapter competitorDetailAdapter;
	private TextView tvNoOrderFound, tvItemListHeader;
	private Button btnCancel, btnAdd;
	
	@Override
	public void initialize() 
	{
		//inflate the asset-scan-list layout
		llLayout = (LinearLayout)inflater.inflate(R.layout.competitor_detail_list,null);
		intialiseControls();
		llBody.addView(llLayout,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CapturedCompetitorActivity.this, CaptureCompetitorDetail.class);
				startActivity(intent);
			}
		});
		setTypeFace(llLayout);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public void intialiseControls()
	{
		//getting all id's of the controls 
		lvList			 =	(ListView)llLayout.findViewById(R.id.lvList);
		tvNoOrderFound	 =	(TextView)llLayout.findViewById(R.id.tvNoproductfound);
		tvItemListHeader =	(TextView)llLayout.findViewById(R.id.tvHeader);
		btnCancel		 =	(Button)llLayout.findViewById(R.id.btnCancel);
		btnAdd			 =	(Button)llLayout.findViewById(R.id.btnAdd);
		
		/*tvNoOrderFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemListHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCancel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAdd.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		lvList.setCacheColorHint(0);
		lvList.setScrollbarFadingEnabled(true);
		lvList.setSelector(getResources().getDrawable(R.drawable.dot_seperator));
		competitorDetailAdapter = new CompetitorDetailAdapter(null);
		lvList.setAdapter(competitorDetailAdapter);
	}
	
	private class CompetitorDetailAdapter extends BaseAdapter 
	{
		Vector<CompDetailDO> vecCompDetailDO;
		
		public CompetitorDetailAdapter(Vector<CompDetailDO> vecCompDetailDO)
		{
			this.vecCompDetailDO = vecCompDetailDO;
		}
		
		public void refreshList(Vector<CompDetailDO> vecCompDetailDO)
		{
			this.vecCompDetailDO = vecCompDetailDO;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() 
		{
			if(vecCompDetailDO != null && vecCompDetailDO.size() > 0 )
			{
				tvNoOrderFound.setVisibility(View.GONE);
				return vecCompDetailDO.size();
			}
			else
				tvNoOrderFound.setVisibility(View.VISIBLE);
			
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
			if(convertView==null)
				convertView			=	(LinearLayout)getLayoutInflater().inflate(R.layout.competitor_cell,null);
			
			CompDetailDO obj		=	 vecCompDetailDO.get(position);
			
			TextView tvBrand			=	(TextView)convertView.findViewById(R.id.tvBrand);
			TextView tvBrandVal			=	(TextView)convertView.findViewById(R.id.tvBrandVal);
			TextView tvCategory			=	(TextView)convertView.findViewById(R.id.tvCategory);
			TextView tvCategoryVal		=	(TextView)convertView.findViewById(R.id.tvCategoryVal);
			TextView tvPrice			=	(TextView)convertView.findViewById(R.id.tvPrice);
			TextView tvPriceVal			=	(TextView)convertView.findViewById(R.id.tvPriceVal);
			TextView tvDescription		=	(TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvDescriptionVal	=	(TextView)convertView.findViewById(R.id.tvDescriptionVal);
			
			
		/*	
			tvBrand.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvBrandVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCategoryVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvPriceVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescriptionVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			
			tvBrandVal.setText(obj.BrandId);
			tvCategoryVal.setText(obj.CategoryId);
			tvPriceVal.setText(obj.price +" "+obj.currency);
			tvDescriptionVal.setText(obj.description);
			
			setTypeFace((ViewGroup) convertView);
			return convertView;
		}
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("finish"))
		{
			finish();
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		loadData();
	}
	
	private void loadData()
	{
		showLoader(getResources().getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final Vector<CompDetailDO> vecCompDetailDO = new CompetitorDetailDA().getCapturedData();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(competitorDetailAdapter != null)
							competitorDetailAdapter.refreshList(vecCompDetailDO);
						
						hideLoader();
					}
				});
			}
		}).start();
	}
}
