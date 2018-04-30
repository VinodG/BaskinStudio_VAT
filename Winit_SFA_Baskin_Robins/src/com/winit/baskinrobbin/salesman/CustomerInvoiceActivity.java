package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.pinch.ListPaymentFragment;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

@SuppressLint("InflateParams")
public class CustomerInvoiceActivity extends BaseActivity implements OnPageChangeListener
{
	private Button btnFinish,btnPrint_Summarry;
	private TextView tvPreviewHeadDate, tvTotalCollection,tvNoPayment;
	private ImageView ivDateSelect;
	private LinearLayout llCustomerInvoice;
	private static final int DATE_DIALOG_ID = 0;
	private String strSelectedDateToPrint = "";
	private HashMap<String, String> hsTotal;
	private String[] tabsName = {"CASH","CHEQUE"};
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private JourneyPlanDO mallsDetails;
	private HashMap<String, ArrayList<Customer_InvoiceDO>> hmPayments;
	
	@Override
	public void initialize() 
	{
		llCustomerInvoice   = (LinearLayout)inflater.inflate(R.layout.payment_summary, null);
		llBody.addView(llCustomerInvoice , LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras() != null)
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		
		intialiseControls();
		
		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnPrint_Summarry.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(hmPayments == null || hmPayments.size() <= 0)
					showCustomDialog(CustomerInvoiceActivity.this, "Alert!", "There is no summary to print.", "OK", "", "");
				else	
					ShowOptionPopup();
			}
		});
		
		ivDateSelect.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		///if(hmPayments!=null && hmPayments.size()>0){
			
			refreshList(CalendarUtils.getOrderPostDate());
//		}
//		else{
//			tvNoPayment.setVisibility(View.VISIBLE);
//		}
		setTypeFace(llCustomerInvoice);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		//getting current dateofJorney from Calendar
	     Calendar c = 	Calendar.getInstance();
	     int year 	= 	c.get(Calendar.YEAR);
	     int month  = 	c.get(Calendar.MONTH);
	     int day 	=	c.get(Calendar.DAY_OF_MONTH);
	     
		switch (id)
		{
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this,  DateSetListener,  year, month, day);
		}
		return null;
	}
	private DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int pickerYear, int pickerMonthOfYear,int pickerDayOfMonth) 
		{
			String strMonth =  (pickerMonthOfYear+1) < 10 ? "0"+(pickerMonthOfYear+1) : (pickerMonthOfYear+1)+"";
	    	String strDay   =  pickerDayOfMonth < 10 ? "0"+pickerDayOfMonth : pickerDayOfMonth+"";
	    	String strDate  =  pickerYear+"-"+strMonth+"-"+strDay;
	    	
			refreshList(strDate);
			strSelectedDateToPrint = strDay+" "+CalendarUtils.getMonthFromNumber(pickerMonthOfYear+1)+", "+pickerYear;
			tvPreviewHeadDate.setText(""+strSelectedDateToPrint);
		}
	};
	private void refreshList(final String strSelectedDate)
	{
		if(hmPayments!=null && hmPayments.size()>0)
			hmPayments.clear();
		tvNoPayment.setVisibility(View.GONE);
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				PaymentSummeryDA paymentSummaryDA=new PaymentSummeryDA();
				hmPayments=paymentSummaryDA.getCustomerInvoice(strSelectedDate, mallsDetails);
				hsTotal=paymentSummaryDA.getTotalAmount(true,strSelectedDate, mallsDetails);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						setTotalAmount();
						hideLoader();
						if(hmPayments!=null && hmPayments.size()>0){
							adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsName);
							pager.setAdapter(adapter);
							tabs.setViewPager(pager);	
						}
						else
							tvNoPayment.setVisibility(View.VISIBLE);
						
					}
				});
			}
		}).start();
	}

	private void intialiseControls()
	{
		btnFinish 			 = 	(Button) llCustomerInvoice.findViewById(R.id.btnFinish);
		btnPrint_Summarry 	 =	(Button) llCustomerInvoice.findViewById(R.id.btnPrint_Summarry);
		ivDateSelect 		 = 	(ImageView) llCustomerInvoice.findViewById(R.id.ivDateSelect);
		tvPreviewHeadDate	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPreviewHeadDate);
		tvTotalCollection 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollection);
		tvNoPayment 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvNoPayment);
		
		tabs 	 			= 	(PagerSlidingTabStrip) llCustomerInvoice.findViewById(R.id.tabs);
		pager 				= 	(ViewPager) llCustomerInvoice.findViewById(R.id.pager);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(CustomerInvoiceActivity.this);
		
		Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month  	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	     
	     strSelectedDateToPrint = (day < 10 ? "0"+day : day)+" "+CalendarUtils.getMonthFromNumber(month+1)+", "+year;
	     tvPreviewHeadDate.setText(""+strSelectedDateToPrint);
	     
	     btnCheckOut.setVisibility(View.GONE);
		 ivLogOut.setVisibility(View.GONE);
	}

	private void setTotalAmount()
	{
		float total = 0.0f;
		
		if(hsTotal != null && hsTotal.size() > 0)
		{
			Set<String> keys = hsTotal.keySet();
			for (String key : keys)
			{
				total = total+StringUtils.getFloat(hsTotal.get(key));
			}
		 }
		 tvTotalCollection.setText("Total Collection : "+total+" "+curencyCode);
	}
	
	private void printSummary(final String type)
	{
		if(hmPayments != null && hmPayments.size() > 0)
		{
			final ArrayList<Customer_InvoiceDO> arr = new ArrayList<Customer_InvoiceDO>();
			
			Set<String> keys = hmPayments.keySet();
			
			for(String key : keys)
				arr.addAll(hmPayments.get(key));
			ShowOptionPopupForPrinter(CustomerInvoiceActivity.this,new BaseActivity.PrintPopup() {
				@Override
				public void selectedOption(int selectedPrinter)
				{

					Intent intent=null;
					if(selectedPrinter== AppConstants.CANCEL)
					{
						hideLoader();

					}
					else
					{
						if(selectedPrinter== AppConstants.WOOSIM)
							intent=new Intent(CustomerInvoiceActivity.this, WoosimPrinterActivity.class);
						else if(selectedPrinter==AppConstants.DOTMATRIX)
							intent=new Intent(CustomerInvoiceActivity.this, PrinterConnectorArabic.class);
						intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
						intent.putExtra("arrayList", arr);
						intent.putExtra("strSelectedDateToPrint", strSelectedDateToPrint);
						intent.putExtra("type", type);
						startActivityForResult(intent, 1000);

					}
				}
			});
			
//			Intent intent = new Intent(CustomerInvoiceActivity.this, WoosimPrinterActivity.class);
//			intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
//			intent.putExtra("arrayList", arr);
//			intent.putExtra("strSelectedDateToPrint", strSelectedDateToPrint);
//			intent.putExtra("type", type);
//			startActivityForResult(intent, 1000);
		}
		else
			showCustomDialog(CustomerInvoiceActivity.this, getResources().getString(R.string.warning), "There is no payment summary to print.", getResources().getString(R.string.OK), null, "");
	}
	
	protected void ShowOptionPopup()
	{

		View view = inflater.inflate(R.layout.custom_popup_language, null);
		final CustomDialog mCustomDialog = new CustomDialog(CustomerInvoiceActivity.this, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		final Button btn_popup_English	  = (Button) view.findViewById(R.id.btn_popup_English);
		final Button btn_popup_Arabic	  = (Button) view.findViewById(R.id.btn_popup_Arabic);
		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);

		setTypeFace((ViewGroup)view);
		tv_poptitle.setText("Select Print Type");
		btn_popup_English.setText("Accounts Copy");
		btn_popup_Arabic.setText("Collection Department Copy");
		
		btn_popup_English.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				printSummary(AppConstants.ACCOUNT_COPY);
				mCustomDialog.dismiss();
			}
		});

		btn_popup_Arabic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				printSummary(AppConstants.COLLECTION_COPY);
				mCustomDialog.dismiss();
			}
		});
		
		btn_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
			}
		});
		setTypeFace((LinearLayout)view);
		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}
	}
	
	public class CategoryPagerAdapter extends FragmentStatePagerAdapter 
	{
		private  String[] tabsName;
		
		public CategoryPagerAdapter(FragmentManager fm,String[] tabsName)
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
			if(tabsName != null && tabsName.length > 0)
				return tabsName[position];
			
			return "N/A";
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
            if(hmPayments!=null)
			return new ListPaymentFragment(CustomerInvoiceActivity.this, hmPayments.get(tabsName[position]), hsTotal, curencyCode);
            else
            	return null;
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
		hideKeyBoard(llBody);
		refreshPager(arg0);
	}
	public void refreshPager(int position)
	{
		
	}
}
