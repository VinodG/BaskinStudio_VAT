package com.winit.baskinrobbin.salesman;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.pinch.ListOrderFragment;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

public class OrderSummary extends BaseActivity implements OnPageChangeListener
{
	//declaration of variables
	private final int DATE_DIALOG_ID = 0;
	private LinearLayout llDeliveryStatus;
	private TextView tvNoOrderFound, tvTitleofStatus;
	private String strSelectedDate = "";
	private TextView tvDeliveryStatusDate;
	private Button btnCalDelivery;
	private boolean isInvoice = false;
	private String[] tabsName = {AppConstants.HHOrder,AppConstants.HOLD_ORDER, AppConstants.LPO_ORDER,AppConstants.RETURNORDER/*,AppConstants.REPLACEMETORDER*/};
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private JourneyPlanDO mallsDetails;
	private HashMap<String, Vector<OrderDO>> hmOrders;
	private int pos = 0;
	private boolean isFromCustomerCheckIn = false;
	private RelativeLayout rlTo,rlFrom;
	private TextView tvFromDate,tvToDate;
	private String fromDate,toDate;
	private final int START_DATE_DIALOG_ID_FROM = 1, START_DATE_DIALOG_ID_TO = 2;
	private int monthFrom, yearFrom, dayFrom, monthTo, yearTo, dayTo;
	
	@Override
	public void initialize() 
	{
		//inflate the manage_staff layout
		llDeliveryStatus	=	(LinearLayout) inflater.inflate(R.layout.order_summary,null);
		llBody.addView(llDeliveryStatus ,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
		
		if(getIntent().getExtras() != null )
		{
			isInvoice    = getIntent().getExtras().getBoolean("isInvoice");
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			if(getIntent().getExtras().containsKey("isFromCustomerCheckIn"))
				isFromCustomerCheckIn = getIntent().getExtras().getBoolean("isFromCustomerCheckIn");
		}
		
		btnCalDelivery.setVisibility(View.VISIBLE);
		if(isInvoice)
			tvTitleofStatus.setText("Invoice Summary");
		else
			tvTitleofStatus.setText("Order Summary");
		
		//Setting Date
		Calendar c 	= 	Calendar.getInstance();
		
	    int year 	= 	c.get(Calendar.YEAR);
	    int month 	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    
		tvDeliveryStatusDate.setText(" "+CalendarUtils.getMonthAsString(month)+""+day+CalendarUtils.getDateNotation(day)+", "+year);
		strSelectedDate = year  + "-" + ((month+1) < 10 ? "0"+(month+1) : (month+1)) + "-" + (day < 10 ? "0"+day : day);
//		loadOrderList(strSelectedDate);
		
		btnCalDelivery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		setTypeFace(llDeliveryStatus);
		
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
	public void intialiseControls() {
		// all the fields from the manage_staff.xml is taken here
		tvNoOrderFound = (TextView) llDeliveryStatus.findViewById(R.id.tvNoOrderFound);
		tvTitleofStatus = (TextView) llDeliveryStatus.findViewById(R.id.tvTitleofStatus);
		tvDeliveryStatusDate = (TextView) llDeliveryStatus.findViewById(R.id.tvDeliveryStatusDate);
		btnCalDelivery = (Button) llDeliveryStatus.findViewById(R.id.btnCalDelivery);

		tabs = (PagerSlidingTabStrip) llDeliveryStatus.findViewById(R.id.tabs);
		pager = (ViewPager) llDeliveryStatus.findViewById(R.id.pager);

		rlTo = (RelativeLayout) llDeliveryStatus.findViewById(R.id.rlTo);
		rlFrom = (RelativeLayout) llDeliveryStatus.findViewById(R.id.rlFrom);
		tvFromDate = (TextView) llDeliveryStatus.findViewById(R.id.tvFromDate);
		tvToDate = (TextView) llDeliveryStatus.findViewById(R.id.tvToDate);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(OrderSummary.this);

		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		Calendar c = Calendar.getInstance();
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		c.add(Calendar.DAY_OF_MONTH, -1);
		monthFrom = c.get(Calendar.MONTH);
		yearFrom = c.get(Calendar.YEAR);
		dayFrom = c.get(Calendar.DAY_OF_MONTH);

		String selectedDate = CalendarUtils.getOrderSummaryDate(yearFrom,monthFrom,dayFrom);

		fromDate = selectedDate;
		tvFromDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
		tvFromDate.setTag(selectedDate);

		selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);

		toDate = selectedDate;
		tvToDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
		tvToDate.setTag(selectedDate);
		bindDetailes();
	}
		private void bindDetailes(){
			rlTo.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{

					new DatePickerDialog(OrderSummary.this, new DatePickerDialog.OnDateSetListener()
					{
						@Override
						public void onDateSet(DatePicker view, int yearSel, int monthOfYear,int dayOfMonth)
						{

							String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);

							if(!selectedDate.equalsIgnoreCase(toDate))
							{
								if(CalendarUtils.getDiffBtwDatesInDays(fromDate,selectedDate) >= 0)
								{
									yearTo = yearSel;
									monthTo = monthOfYear;
									dayTo = dayOfMonth;

									toDate = selectedDate;
									tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
									tvToDate.setTag(toDate);
									loadOrderList();
								}
								else
									showCustomDialog(OrderSummary.this,"Alert",getString(R.string.to_date_should_not_be_lesser_than_from_date),getString(R.string.OK),null,null);
							}


						}
					}, yearTo, monthTo, dayTo).show();

				}
			});
			rlFrom.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					new DatePickerDialog(OrderSummary.this, new DatePickerDialog.OnDateSetListener()
					{
						@Override
						public void onDateSet(DatePicker view, int yearSel, int monthOfYear,int dayOfMonth)
						{

							String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);

							if(!selectedDate.equalsIgnoreCase(fromDate))
							{

								if(CalendarUtils.getDiffBtwDatesInDays(selectedDate,toDate) >= 0)
								{
									yearFrom = yearSel;
									monthFrom = monthOfYear;
									dayFrom = dayOfMonth;


									fromDate = selectedDate;
									tvFromDate.setText(CalendarUtils.getFormatedDatefromString(fromDate));
									tvFromDate.setTag(fromDate);
									loadOrderList();
								}
								else
									showCustomDialog(OrderSummary.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);

							}


						}
					}, yearFrom, monthFrom, dayFrom).show();
				}
			});
		}

	
	@Override
    protected Dialog onCreateDialog(int id) 
    {
		//getting current dateofJorney from Calendar
	    Calendar c 	= 	Calendar.getInstance();
	    int cyear 	= 	c.get(Calendar.YEAR);
	    int cmonth 	= 	c.get(Calendar.MONTH);
	    int cday 	=	c.get(Calendar.DAY_OF_MONTH);
	    
	    switch (id) 
	    {
		    case DATE_DIALOG_ID:
		    	return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	    }
	    return null;
    }
    
	/** method for dateofJorney picker **/
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	    {
			Calendar tempCalendar 	= Calendar.getInstance();
			tempCalendar.set(year, monthOfYear, dayOfMonth);
	    	tvDeliveryStatusDate.setText(" "+CalendarUtils.getMonthAsString(monthOfYear)+""+dayOfMonth+CalendarUtils.getDateNotation(dayOfMonth)+", "+year);
    		strSelectedDate = year  + "-" + ((monthOfYear+1) < 10 ? "0"+(monthOfYear+1) : (monthOfYear+1)) + "-" + (dayOfMonth < 10 ? "0"+dayOfMonth : dayOfMonth);
    		loadOrderList();
	    }
    };
	
	public void loadOrderList()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				hmOrders =  new CommonDA().getDeliveryStatusOrderList(fromDate,toDate, mallsDetails);
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
			{
				if(tabsName[position].equalsIgnoreCase(AppConstants.HHOrder))
					name = AppConstants.HHOrder.replace("HH", "Sales");
				else if(tabsName[position].equalsIgnoreCase(AppConstants.RETURNORDER))
					name = AppConstants.RETURNORDER.replace("HH", "");
				else
					name = tabsName[position];
			}
			
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
			return new ListOrderFragment(OrderSummary.this, hmOrders.get(tabsName[position]), mallsDetails, isFromCustomerCheckIn);
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
		loadOrderList();
		super.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 900)
			finish();
	}
}
