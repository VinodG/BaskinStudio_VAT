package com.winit.baskinrobbin.salesman;

import java.util.HashMap;
import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.pinch.VanNonSStockFragment;
import com.winit.baskinrobbin.pinch.VanStockFragment;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

public class SalesmanVanStock extends BaseActivity implements OnPageChangeListener
{
	//declaration of variables
	private LinearLayout llStock;
	private TextView tvTitle;
	private String[] tabsName = {AppConstants.SELLABLE,AppConstants.NON_SELLABLE};
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private HashMap<String, Vector<InventoryObject>> hmInevntory;
	private int pos = 0;
	
	@Override
	public void initialize() 
	{
		//inflate the manage_staff layout
		llStock	=	(LinearLayout) inflater.inflate(R.layout.salesman_stock_inventory,null);
		llBody.addView(llStock ,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
		
		setTypeFace(llStock);
		
		tabs.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				pos = arg0;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
	}
	
	/** initializing all the Controls  of SupervisorManageStaff class **/
	public void intialiseControls()
	{
		// all the fields from the manage_staff.xml is taken here
		tvTitle	 =	(TextView)llStock.findViewById(R.id.tvTitle);
		tabs 	 = 	(PagerSlidingTabStrip) llStock.findViewById(R.id.tabs);
		pager 	 = 	(ViewPager) llStock.findViewById(R.id.pager);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(SalesmanVanStock.this);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public void loadStockInventory()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//Vector<InventoryObject> vecInventoryItems    = new OrderDetailsDA().getInventoryQty(CalendarUtils.getOrderPostDate());
				Vector<InventoryObject> vecInventoryItems    = new OrderDetailsDA().getInventoryQty();
				Vector<InventoryObject> vecNonInventoryItems = new OrderDetailsDA().getReturnInventoryQtyNew(diffStock, diffAmt);
				
				hmInevntory = new HashMap<String, Vector<InventoryObject>>();
				hmInevntory.put(AppConstants.SELLABLE, vecInventoryItems);
				hmInevntory.put(AppConstants.NON_SELLABLE, vecNonInventoryItems);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsName);
						pager.setAdapter(adapter);
						pager.setCurrentItem(pos);
						tabs.setViewPager(pager);
					}
				});
			}
		}).start();
	}
	
	public class CategoryPagerAdapter extends FragmentStatePagerAdapter 
	{
		private String[] tabsName;
		
		public CategoryPagerAdapter(FragmentManager fm, String[] tabsName)
		{
			super(fm);
			this.tabsName = tabsName;
		}
		public void refresh() 
		{
			notifyDataSetChanged();
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			String name = "N/A";
			
			if(tabsName != null && tabsName.length > 0)
				name = tabsName[position];
			
			return name.toUpperCase();
		}
		
		@Override
		public int getCount() 
		{
			if(tabsName == null || tabsName.length <= 0)
				return 0;
			
			return tabsName.length;
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
			case 0:
				return new VanStockFragment(SalesmanVanStock.this, hmInevntory.get(tabsName[position]));

			case 1:
				return new VanNonSStockFragment(SalesmanVanStock.this, hmInevntory.get(tabsName[position]));
				
			default:
				return new VanStockFragment(SalesmanVanStock.this, hmInevntory.get(tabsName[position]));
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		hideKeyBoard(llBody);
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) 
	{
	}
	
	@Override
	public void onPageSelected(int arg0)
	{
		refreshPager(arg0);
		hideKeyBoard(llBody);
	}
	
	public void refreshPager(int position)
	{
	}
	
	@Override
	protected void onResume() 
	{
		loadStockInventory();
		super.onResume();
	}
}
