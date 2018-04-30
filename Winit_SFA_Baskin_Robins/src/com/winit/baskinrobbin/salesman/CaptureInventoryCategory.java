package com.winit.baskinrobbin.salesman;


import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.ProductFragment;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

public class CaptureInventoryCategory extends BaseActivity 
{
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private JourneyPlanDO mallsDetails;
	private Button btnSubmit, btnCancel;
	private LinearLayout llLayout;
	private Vector<CategoryDO> vecCategory;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, Float> hmConversion; 
	private HashMap<String, ProductDO> hmSelectedItems;
	
	
	@Override
	public void initialize() 
	{
		llLayout 		= (LinearLayout) inflater.inflate(R.layout.activity_main1, null);
		tabs 	 		= (PagerSlidingTabStrip)llLayout.findViewById(R.id.tabs);
		pager 	 		= (ViewPager) llLayout.findViewById(R.id.pager);
		
		btnSubmit 	 	= (Button) llLayout.findViewById(R.id.btnSubmit);
		btnCancel 		= (Button) llLayout.findViewById(R.id.btnCancel);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		llBody.addView(llLayout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		setTypeFace(llLayout);
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails   = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			
			hmInventory    = (HashMap<String, HHInventryQTDO>) getIntent().getExtras().get("hmInventory");
			hmConversion   = (HashMap<String, Float>) getIntent().getExtras().get("hmConversion");
			hmSelectedItems= (HashMap<String, ProductDO>) getIntent().getExtras().get("hmSelectedItems");
			
			if(hmSelectedItems == null)
				hmSelectedItems = new HashMap<String, ProductDO>();
		}
		loadData();
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(hmSelectedItems != null && hmSelectedItems.size() > 0)
						{
							Set<String> keys = hmSelectedItems.keySet();
							for(String key : keys)
							{
								ProductDO productDO = hmSelectedItems.get(key);
								productDO.isPromotional = true;
								productDO.etUnits = null;
							}
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								Intent intent = new Intent();
								intent.putExtra("hmSelectedItems", hmSelectedItems);
								setResult(1000, intent);
								finish();
							}
						});
					}
				}).start();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	private void loadData() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				//precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
				vecCategory = new CategoriesDA().getAllCategoryforFOC(mallsDetails.priceList, hmSelectedItems, AppStatus.SALES_ORDER_TYPE, hmInventory);	
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								adapter  = new CategoryPagerAdapter(getSupportFragmentManager(), vecCategory);
								pager.setAdapter(adapter);
								tabs.setViewPager(pager);
								hideLoader();
							}
						});
					}
				});
			}
		}).start();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) 
	{
		super.onRestoreInstanceState(savedInstanceState);
	}

	public class CategoryPagerAdapter extends FragmentPagerAdapter 
	{
		private Vector<CategoryDO> vectorMaincategory;
		public CategoryPagerAdapter(FragmentManager fm, Vector<CategoryDO> vectorMaincat)
		{
			super(fm);
			this.vectorMaincategory = vectorMaincat;
		}
		public void refresh(Vector<CategoryDO> vectorMaincategory) 
		{
			this.vectorMaincategory = vectorMaincategory;
			notifyDataSetChanged();
		}
		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(vectorMaincategory.get(position) != null)
				return vectorMaincategory.get(position).categoryName;
			return "Personal Care";
		}
		@Override
		public int getCount() 
		{
			if(vectorMaincategory == null || vectorMaincategory.size() <= 0)
				return 0;
			return vectorMaincategory.size();
		}
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}
		@Override
		public Fragment getItem(int position)
		{
			CategoryDO categoryDO = vectorMaincategory.get(position);
			return new ProductFragment(CaptureInventoryCategory.this, mallsDetails, categoryDO, hmConversion, hmInventory, hmSelectedItems);
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
}