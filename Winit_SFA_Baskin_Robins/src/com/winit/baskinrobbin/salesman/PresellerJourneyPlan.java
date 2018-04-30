package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.common.Rotate3dAnimation;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerOrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.BaseComparableDO;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;

public class PresellerJourneyPlan extends BaseActivity implements  ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llMain, llTimeTitle, llSearch, llMap;
	@SuppressWarnings("unused")
	private TextView tvSortBy, tvJourneyPlanDate, tvJourneyPlanDateValue, tvResultOfSearch, tvSeprator, tvTime, tvCustomers;
	private Button btnGlobe, btnTopCalIcon, btnAdvance;
	private EditText etSearch;
	private ListView lvEvents;
	private Intent CheckIN = null;
	private String strCurrentDate, strSelectedDate;
	private Vector<NameIDDo> vecReasons;
	private GoogleMap mapview;
	private CustomCalEventsAdapter objCustomCalEventsAdapter;
	private LocationUtility  locationUtility = null;
	private CustomerDetailsDA objCustomerDetailsBL;
	private ViewGroup flContainer;
	private PopupWindow popupWindow;
	private ArrayList<String> vecServedCustomer,vecServedCustomerWithDataNotPosted;
	private boolean isOnResume = false;
	private Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
	private PostReasonDO objPostReasonDO ;	
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private Vector<Marker> veMarkers;
	private HashMap<String, CustomerCreditLimitDo> hmCreditLimits;
	private HashMap<String, Float> hmOverDue;
	private Button btnCustomers;
	
	@Override
	public void initialize() 
	{
		llMain				=	(LinearLayout) inflater.inflate(R.layout.calenderviewxml, null);
		llBody.addView(llMain,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
		setTypeFace(llMain);
		
		strCurrentDate 			= 	CalendarUtils.getCurrentDateAsString();
		locationUtility 		= 	new LocationUtility(this);
		objCustomerDetailsBL 	=   new CustomerDetailsDA();
		
		//inflate the calendar-view-xml layout
		vecServedCustomer = new ArrayList<String>();	
		vecServedCustomerWithDataNotPosted = new ArrayList<String>();
		
		LogUtils.errorLog("", "5 "+preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false));
		
		lvEvents.setCacheColorHint(0);
		lvEvents.setFadingEdgeLength(0);
		lvEvents.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvEvents.setSelector(R.drawable.list_item_selected);
		
		loadCustomerList();
		
		lvEvents.setAdapter(objCustomCalEventsAdapter = new CustomCalEventsAdapter(new ArrayList<JourneyPlanDO>()));
		
		btnGlobe.setTag("list");
		
		Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month 	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    String strMonth = "", strDate = "";
	    
	    if(month < 9)
	    	strMonth = "0"+(month+1);
		else
			strMonth = ""+(month+1);
		
		if(day < 10)
			strDate = "0"+(day);
		else
			strDate = ""+(day);
		
		//creating the required Date format
		strSelectedDate = year+"-"+strMonth+"-"+strDate;
	    tvJourneyPlanDateValue.setText(" "+CalendarUtils.getMonthAsString(month)+""+day+CalendarUtils.getDateNotation(day)+", "+year);
	    
	    if(preference.getStringFromPreference(Preference.DAY_VARIFICATION, "").equals(""))
	    {
		    preference.saveStringInPreference(Preference.DAY_VARIFICATION, CalendarUtils.getCurrentDate());
		    preference.commitPreference();
	    }
	    
	    tvSortBy.setTag("sortby");
		btnGlobe.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(v.getTag().toString().equalsIgnoreCase("list"))
				{
					hideKeyBoard(v);
					llTimeTitle.setVisibility(View.GONE);
					llSearch.setVisibility(View.GONE);
					llMap.setVisibility(View.VISIBLE);
					btnTopCalIcon.setVisibility(View.GONE);
					tvSeprator.setVisibility(View.INVISIBLE);
					tvSortBy.setVisibility(View.GONE);
					tvSortBy.setTag("searchmap");
					tvResultOfSearch.setVisibility(View.GONE);
					tvSortBy.setText(getResources().getString(R.string.Search_Map));
					v.setTag("Map");
					v.setBackgroundResource(R.drawable.list_icon_btn_click);
				}
				else
				{
					llTimeTitle.setVisibility(View.VISIBLE);
					llSearch.setVisibility(View.VISIBLE);
					etSearch.setText(null);
					llMap.setVisibility(View.GONE);
					btnTopCalIcon.setVisibility(View.GONE);
					tvSeprator.setVisibility(View.VISIBLE);
					tvSortBy.setVisibility(View.GONE);
					tvSortBy.setTag("sortby");
					tvSortBy.setText(getResources().getString(R.string.Sort_By));
					v.setTag("list");
					v.setBackgroundResource(R.drawable.btn_globe_click);
					if(objCustomCalEventsAdapter == null)
						lvEvents.setAdapter(objCustomCalEventsAdapter = new CustomCalEventsAdapter(arrayListEvent));
					else
						objCustomCalEventsAdapter.refresh(arrayListEvent);
				}
			}
		});
		
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					ArrayList <JourneyPlanDO> vecTemp = new ArrayList<JourneyPlanDO>();
					for(int index = 0; arrayListEvent != null && index < arrayListEvent.size(); index++)
					{
						JourneyPlanDO obj 	= (JourneyPlanDO) arrayListEvent.get(index);
						String strText 		= (obj).siteName + (obj).addresss1+ (obj).addresss2;
						String strSite 		= (obj).site;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase())
								|| strSite.toLowerCase().contains(s.toString().toLowerCase()))
							
							vecTemp.add(arrayListEvent.get(index));
					}
					if(vecTemp!=null)
						objCustomCalEventsAdapter.refresh(vecTemp);
				}
				else
				{
					objCustomCalEventsAdapter.refresh(arrayListEvent);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
			{
				
			}
			@Override
			public void afterTextChanged(Editable s) 
			{
				
			}
		});
		
		Bundle bundle = getIntent().getExtras();
		final Boolean isFromLogin = bundle != null && bundle.containsKey("isFromLogin") && bundle.getBoolean("isFromLogin");
		if(isFromLogin)
		{
		}
		
		if(btnBack != null)
			btnBack.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		setUpMap();
		
		btnAdvance.setVisibility(View.GONE);
		btnAdvance.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
				{
					showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
				}
				else
				{
				}
			}
		});
		
		btnCustomers.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Customer list 
				Intent intent = new Intent(PresellerJourneyPlan.this, SalesmanCustomerList.class);
				intent.putExtra("isHistory", false);
				startActivity(intent);
			}
		});
	}
	@Override	
	protected void onResume() 
	{
		super.onResume();
		AppConstants.CheckIN = false;
		isOnResume = true;
//		hideKeyBoard(llMain);
		boolean isAllServed = isAllServed();
		if(etSearch!=null)
			etSearch.setText(null);
		if( isAllServed && !AppConstants.isSumeryVisited && preference.getbooleanFromPreference("isReasonGiven", false) && !preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		{
			showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.end_of_trip), getResources().getString(R.string.you_have_taken_five_minute_more) , getResources().getString(R.string.Yes), getResources().getString(R.string.No), "allPresellerCustomer");
			preference.saveBooleanInPreference("isAllServed", true);
			preference.saveBooleanInPreference("isReasonGiven", false);
			preference.commitPreference();
		}
		else if(isAllServed && !preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		{
			showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.end_of_trip), getResources().getString(R.string.you_successfully_served_all_customers), getResources().getString(R.string.Yes), getResources().getString(R.string.No), "allPresellerCustomer");
			preference.saveBooleanInPreference("isAllServed", true);
			preference.saveStringInPreference("EOTReason", "");
			preference.saveStringInPreference("EOTType", "");
			preference.commitPreference();
		}
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{	
				hmOverDue						   = new CustomerDA().getOverDueAmount(); 
				hmCreditLimits					   = new CustomerDA().getCreditLimits(); 
				vecServedCustomer 				   = objCustomerDetailsBL.getServedCustomerList(preference.getStringFromPreference(Preference.EMP_NO, ""), preference);
				vecServedCustomerWithDataNotPosted = objCustomerDetailsBL.getOrderTobePost(preference.getStringFromPreference(Preference.EMP_NO, ""));		
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						loadCustomerList();
						if(objCustomCalEventsAdapter!=null)
							objCustomCalEventsAdapter.refresh();
					}
				});
			}
		}).start();
	}

	/** initializing all the Controls  of PresellerJourneyPlan class **/
	public void intialiseControls()
	{
		flContainer			= 	(ViewGroup) llMain.findViewById(R.id.flContainer);

		// Since we are caching large views, we want to keep their cache
        // between each animation
        flContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);

        llMap				=	(LinearLayout)findViewById(R.id.llMap);
        llMap.setVisibility(View.GONE);
		
		llSearch			= (LinearLayout)llMain.findViewById(R.id.llSearch);
		llTimeTitle			= (LinearLayout)llMain.findViewById(R.id.llTimTitle);
		
		tvTime				= (TextView)llMain.findViewById(R.id.tvTime);
		tvCustomers			= (TextView)llMain.findViewById(R.id.tvCustomers);
		lvEvents			=	(ListView)llMain.findViewById(R.id.lvEvents);
		tvSeprator			=	(TextView)llMain.findViewById(R.id.tvSeprator);
		tvSortBy			=	(TextView)llMain.findViewById(R.id.tvSortBy);
		tvJourneyPlanDate	=	(TextView)llMain.findViewById(R.id.tvJourneyPlanDate);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvJourneyPlanDateValue = (TextView)llMain.findViewById(R.id.tvJourneyPlanDateValue);
		btnGlobe			=	(Button)llMain.findViewById(R.id.btnGlobe);
		btnTopCalIcon		=	(Button)llMain.findViewById(R.id.btnTopCalIcon);
		btnAdvance			=	(Button)llMain.findViewById(R.id.btnAdvance);
		etSearch			=	(EditText)llMain.findViewById(R.id.etSearch);
		
		btnCustomers		=	(Button)llMain.findViewById(R.id.btnCustomers);
		
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		btnTopCalIcon.setVisibility(View.GONE);
	}
	
	public void loadCustomerList()
	{
		strCurrentDate 		= 	CalendarUtils.getCurrentDateAsString();
		strSelectedDate		= 	strCurrentDate;
		
		if(arrayListEvent == null || !preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(strCurrentDate))
		{
			arrayListEvent = new ArrayList<JourneyPlanDO>();
			preference.saveBooleanInPreference("isAllServed", false);
			preference.commitPreference();
		}
		
		if(!preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase("")&&!preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(strCurrentDate) && !isOnResume)
		{
			preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
			preference.commitPreference();
		}
		
		if(arrayListEvent.size() == 0)
		{
			Calendar c = Calendar.getInstance();
			int date = c.get(Calendar.DAY_OF_MONTH);
			long timeStamp = c.getTimeInMillis();
			String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
			arrayListEvent = objCustomerDetailsBL.getJourneyPlan(timeStamp, date, day, preference.getStringFromPreference(Preference.EMP_NO, "") );
			preference.saveStringInPreference(Preference.LAST_JOURNEY_DATE, strCurrentDate);
			preference.commitPreference();
		}
		
		addMarkers(arrayListEvent);
		if(objCustomCalEventsAdapter!=null)
			objCustomCalEventsAdapter.refresh(arrayListEvent);
	}
	
	private boolean isAllServed()
	{
		int count = new CustomerDetailsDA().getServedCustomerCount(strSelectedDate,preference.getStringFromPreference("UserID", ""));
		if(arrayListEvent != null && count!=0 && count == arrayListEvent.size())
			return true;
		
		return false;
	}
	
	public class CustomCalEventsAdapter extends BaseAdapter 
	{
		private ArrayList<JourneyPlanDO> vecMyAdapterEvent;
		
		public CustomCalEventsAdapter(ArrayList<JourneyPlanDO> vecMyAdapterEvent)
		{
			this.vecMyAdapterEvent = vecMyAdapterEvent;
			if(vecMyAdapterEvent.size() == 0 && flContainer != null && tvResultOfSearch != null)
				tvResultOfSearch.setVisibility(View.VISIBLE);
			else
				tvResultOfSearch.setVisibility(View.GONE);
		}
		
		@Override
		public int getCount()
		{
			if(vecMyAdapterEvent != null)
				return vecMyAdapterEvent.size();
			return 0;
		}
		private void refresh()
		{
			this.notifyDataSetChanged();
		}
		private void refresh(ArrayList<JourneyPlanDO> vecMyAdapterEvent)
		{
			this.vecMyAdapterEvent = vecMyAdapterEvent;
			if(vecMyAdapterEvent != null && vecMyAdapterEvent.size() == 0 && flContainer != null && tvResultOfSearch != null)
				tvResultOfSearch.setVisibility(View.VISIBLE);
			else
				tvResultOfSearch.setVisibility(View.GONE);
			this.notifyDataSetChanged();
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
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			BaseComparableDO oBaseComparableDO = vecMyAdapterEvent.get(position);
		
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.callistviewcellxml_cell, null);
			
			LinearLayout llCredit	=	(LinearLayout)convertView.findViewById(R.id.llCredit);
			TextView tvSiteName		=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvSiteID		=	(TextView)convertView.findViewById(R.id.tvSiteID);
			TextView tvPaymentType 	= 	(TextView)convertView.findViewById(R.id.tvPaymentType);
			TextView tvCreditLimit	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
			TextView tvInOutTime	=	(TextView)convertView.findViewById(R.id.tvInOutTime);
			TextView tvAddress		=	(TextView)convertView.findViewById(R.id.tvAddress);
			
			TextView tvDuePayment	=	(TextView)convertView.findViewById(R.id.tvDuePayment);
			View sideView 			=	(View)convertView.findViewById(R.id.sideView);
			
			if(oBaseComparableDO instanceof JourneyPlanDO)
			{
				final JourneyPlanDO objMallsDetails	=	(JourneyPlanDO)oBaseComparableDO;
				objMallsDetails.mPosition = position;
				
				tvSiteName.setText(objMallsDetails.siteName);
				tvAddress.setText(objMallsDetails.addresss1 +", "+objMallsDetails.addresss2);
				
				if(objMallsDetails.customerType != null && objMallsDetails.customerType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
					llCredit.setVisibility(View.GONE);
				else
				{
					llCredit.setVisibility(View.VISIBLE);
					float availLimit = 0 , overDue = 0;
					String key = "";
					
					if(objMallsDetails.CREDIT_LEVEL != null && objMallsDetails.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT))
						key = objMallsDetails.customerId + AppConstants.CREDIT_LEVEL_ACCOUNT;
					else
						key = objMallsDetails.site + AppConstants.CREDIT_LEVEL_SITE;
					
					if(hmCreditLimits != null && hmCreditLimits.containsKey(key))
						availLimit = StringUtils.getFloat(hmCreditLimits.get(key).availbleLimit);
					
					if(hmOverDue != null && hmOverDue.containsKey(key))
						overDue = hmOverDue.get(key);
					
					tvCreditLimit.setText(diffAmt.format(availLimit));
					tvDuePayment .setText(diffAmt.format(overDue));
				}
				
				tvSiteID     	 .setText(objMallsDetails.site);
				tvPaymentType    .setText(objMallsDetails.customerType);
				
				String strTime = objMallsDetails.timeIn;
				tvInOutTime.setText(strTime+"");
				sideView.setLayoutParams(new LinearLayout.LayoutParams((int)(5*px),LayoutParams.FILL_PARENT));
				convertView.setTag(objMallsDetails);
				
				if(vecServedCustomer.contains(objMallsDetails.site) && !vecServedCustomerWithDataNotPosted.contains(objMallsDetails.site))
				{
					sideView.setVisibility(View.VISIBLE);
					sideView.setBackgroundColor(getResources().getColor(R.color.customer_served));
				}
				else if(vecServedCustomerWithDataNotPosted.contains(objMallsDetails.site))
				{
					sideView.setVisibility(View.VISIBLE);
					sideView.setBackgroundColor(getResources().getColor(R.color.red));
				}
				else
					sideView.setVisibility(View.INVISIBLE);
				
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(final View arg0)
					{
						preference.saveStringInPreference(Preference.CURRENCY_CODE, objMallsDetails.currencyCode);
						preference.commitPreference();
						
						AppConstants.SKIPPED_CUSTOMERS = "";
						if(AppConstants.skippedCustomerSitIds !=null && AppConstants.skippedCustomerSitIds.size()>0)
							AppConstants.skippedCustomerSitIds.clear();
						((LinearLayout)arg0).setClickable(false);
						final JourneyPlanDO obj	= 	(JourneyPlanDO) arg0.getTag();
						Calendar c = Calendar.getInstance();
						int date = c.get(Calendar.DAY_OF_MONTH);
						long timeStamp = c.getTimeInMillis();
						String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
						
						int count  = 	 objCustomerDetailsBL.getServedCustomerCount(obj.stop, day, obj.userID, date, timeStamp, CalendarUtils.getCurrentDateAsString());
						
						if(obj != null)
						{
							preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, objMallsDetails.site);
							if(TextUtils.isEmpty(curencyCode))
								preference.saveStringInPreference(Preference.CURRENCY_CODE, objMallsDetails.currencyCode);
							preference.commitPreference();
						}
				
						if(count == 1)
						{
							Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
							intent.putExtra("mallsDetails", obj);
							intent.putExtra("strTypeOfCall", "Journey Call");
							startActivity(intent);
						}
						else if(AppConstants.skippedCustomerSitIds != null && AppConstants.skippedCustomerSitIds.size() > 0)
						{
							ArrayList<String> temp = new ArrayList<String>();
							for(String s : AppConstants.skippedCustomerSitIds)
							{
								if(vecServedCustomer.contains(s))
								{
									temp.add(s);
								}
							}
							
							ArrayList<String> tempName = new ArrayList<String>();
							for(String s : AppConstants.skippedCustomerSitIds)
							{
								if(vecServedCustomer.contains(s))
								{
									tempName.add(s);
								}
							}
							for(String s: temp)
								AppConstants.skippedCustomerSitIds.remove(s);
							
							if(AppConstants.skippedCustomerSitIds.size()>0)
							{
								showSkipJourneyPlanPopup(getResources().getString(R.string.warning), AppConstants.SKIPPED_CUSTOMERS);
								CheckIN  =	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
								CheckIN.putExtra("mallsDetails", obj);
								CheckIN.putExtra("isCustomer", false);
								CheckIN.putExtra("strDate", strCurrentDate);
							}
							else
							{
								Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
								intent.putExtra("mallsDetails", obj);
								intent.putExtra("isCustomer", false);
								intent.putExtra("strDate", strCurrentDate);
								startActivity(intent);
							}
						}
						else
						{
							Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
							intent.putExtra("mallsDetails", obj);
							intent.putExtra("isCustomer", false);
							intent.putExtra("strDate", strCurrentDate);
							startActivity(intent);
						}
						
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								((LinearLayout)arg0).setClickable(true);
							}
						}, 300);
					}
				});
			}
			setTypeFace((ViewGroup) convertView);
			return convertView;	
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100)
		{
			objCustomCalEventsAdapter.refresh(arrayListEvent);
		}
	}
	
	/**
     * Setup a new 3D rotation on the container view.
     *
     * @param position the item that was clicked to show a picture, or -1 to show the list
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(int position, float start, float end)
    {
        // Find the center of the container
        final float centerX = flContainer.getWidth() / 2.0f;
        final float centerY = flContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));

        flContainer.startAnimation(rotation);
    }
	
    
    /**
     * This class listens for the end of the first half of the animation.
     * It than posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;

        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            flContainer.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }
    
    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable 
    {
        private final int mPosition;

        public SwapViews(int position) 
        {
            mPosition = position;
        }

        public void run() 
        {
            final float centerX = flContainer.getWidth() / 2.0f;
            final float centerY = flContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;
            
            if (mPosition > -1) 
            {
                lvEvents.setVisibility(View.GONE);
                llMap.setVisibility(View.VISIBLE);
                llMap.requestFocus();
                rotation = new Rotate3dAnimation(-90, 0, centerX, centerY, 310.0f, false);
            } 
            else 
            {
            	llMap.setVisibility(View.GONE);
                lvEvents.setVisibility(View.VISIBLE);
                lvEvents.requestFocus();
                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
            }

            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            rotation.setAnimationListener(new AnimationListener() 
            {
				@Override
				public void onAnimationStart(Animation animation) 
				{	}
				
				@Override
				public void onAnimationRepeat(Animation animation)
				{	}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					if (mPosition > -1) 
					 {
						llMap.postInvalidate();
					 }
				}
			});
            flContainer.startAnimation(rotation);
        }
    }
    
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		locationUtility.stopGpsLocUpdation();
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			btnMenu.performClick();
		else if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
		else
			super.onBackPressed();
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
    
	@Override
	public void onConnectionException(Object msg) 
	{
	}
	
	public void showSkipJourneyPlanPopup(String title, String strMessage)
	{
		if (customDialog != null && customDialog.isShowing())
			customDialog.dismiss();

		View view 					= 	inflater.inflate(R.layout.skip_journey_plan_popup_, null);
		customDialog 				= 	new CustomDialog(PresellerJourneyPlan.this, view, preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40, LayoutParams.WRAP_CONTENT, true);
		TextView tvTitle 			= 	(TextView) view.findViewById(R.id.tvHead);
		TextView tvCustomerList		= 	(TextView) view.findViewById(R.id.tvCustomerList);
		TextView tvMessage			= 	(TextView) view.findViewById(R.id.tvMessages);
		Button btnYesPopup			=   (Button) view.findViewById(R.id.btnYesPopup);	
		Button btnNoPopup			=   (Button) view.findViewById(R.id.btnNoPopup);
		
		setTypeFace((ViewGroup) view);
//		tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTitle.setText(title);
//		tvCustomerList.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerList.setText(strMessage);
//		tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnYesPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnNoPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		btnYesPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(customDialog != null)
					customDialog.dismiss();
				
	    		vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_JOURNEY_PLAN);
	    		CustomBuilder builder = new CustomBuilder(PresellerJourneyPlan.this, "Select Reason", true);
	    		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener() 
	    		{
	    			@Override
	    			public void onClick(CustomBuilder builder, Object selectedObject) 
	    			{
	    				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
	    				showLoader(getResources().getString(R.string.please_wait));
	    	    		builder.dismiss();
	    	    		new Thread(new Runnable() 
	    	    		{
							@Override
							public void run() 
							{
								for(String strSiteId :  AppConstants.skippedCustomerSitIds)
								{
									objPostReasonDO 			   = new PostReasonDO();
									objPostReasonDO.customerSiteID = strSiteId;
				    				objPostReasonDO.presellerId    = preference.getStringFromPreference(Preference.EMP_NO, "");
				    				objPostReasonDO.reason         = ""+objNameIDDo.strName;
				    				objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
				    				objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
				    				objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				    				vecPostReasons.add(objPostReasonDO);
								}
								
								final int passcodeEnable = new SettingsDA().getSettingsByName(AppConstants.PASSCODE_SKIP_CUSTOMER);
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										if(CheckIN != null)
					    	    		{
											if(passcodeEnable == AppStatus.ENABLE)
												showPassCodeDialog(objNameIDDo, "normal", false);
											else
												performPasscodeAction(objNameIDDo, "normal", false);
					    	    		}
										hideLoader();
									}
								});
							}
						}).start();
	    		    }
	    	   }); 
	    		builder.show();
			}
		});
		
		btnNoPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(customDialog != null)
					customDialog.dismiss();
			}
		});
		
		if(customDialog != null && !customDialog.isShowing())
			customDialog.show();
	}
	
	private void setUpMap()
	{
		if (mapview == null) {
			mapview = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1))
					.getMap();
			if(mapview != null)
			{
				mapview.setMyLocationEnabled(true);
				mapview.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
		            @Override
		            public void onInfoWindowClick(Marker marker)
		            {
		            	if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
						{
							showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
						}
						else
						{
							JourneyPlanDO mallsDetails = arrayListEvent.get(veMarkers.indexOf(marker));
			            	Intent intent = new Intent(PresellerJourneyPlan.this, SalesmanCheckIn.class);
			            	intent.putExtra("mallsDetails", mallsDetails);
							startActivity(intent);
						}
		            	
		            }
		        });
				
				mapview.setInfoWindowAdapter(new InfoWindowAdapter() {
					
					@Override
					public View getInfoWindow(Marker marker) {
						JourneyPlanDO mallsDetails = arrayListEvent.get(veMarkers.indexOf(marker));
						ContextThemeWrapper cw = new ContextThemeWrapper(
	                            getApplicationContext(), R.style.Transparent);
	                    // AlertDialog.Builder b = new AlertDialog.Builder(cw);
	                    LayoutInflater inflater = (LayoutInflater) cw
	                            .getSystemService(LAYOUT_INFLATER_SERVICE);
						 View v = inflater.inflate(R.layout.map_popup, null);
			             TextView tvName = (TextView) v.findViewById(R.id.tvLocationTitle);
			             TextView tvTime = (TextView) v.findViewById(R.id.tvLocationAddressLine1);
			             
			             v.setLayoutParams(new LinearLayout.LayoutParams(preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 720)*2/3, LayoutParams.WRAP_CONTENT));
			             tvName.setText(mallsDetails.siteName);
			             tvTime.setText(mallsDetails.addresss2 +","+ mallsDetails.addresss3);
			             return v;
					}
					@Override
					public View getInfoContents(Marker marker) {
			             return null;
					}
				});
			}
		}
	}
	
	
	private void addMarkers(final ArrayList<JourneyPlanDO> vecmallsDetails)
	{
		if(vecmallsDetails != null && vecmallsDetails.size() > 0)
		{
			runOnUiThread(new Runnable() 
			{
				@Override
				public void run()
				{
					if(mapview != null)
					{	
						veMarkers = new Vector<Marker>();
						mapview.clear();
						for (final JourneyPlanDO mallsDetails : vecmallsDetails) 
						{
							LatLng latLang = new LatLng(StringUtils.getFloat(mallsDetails.geoCodeX),StringUtils.getFloat(mallsDetails.geoCodeY));
							Marker marker = mapview.addMarker(new MarkerOptions()
							.position(latLang)
							.title(mallsDetails.siteName)
							.snippet(mallsDetails.addresss2+", "+mallsDetails.addresss3)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
							veMarkers.add(marker);
						}
						LatLng latLang = new LatLng(StringUtils.getFloat(vecmallsDetails.get(0).geoCodeX),StringUtils.getFloat(vecmallsDetails.get(0).geoCodeY));
						mapview.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 10.0f));
					}
				}
			});
		}
	}
	
	@Override
	public void performPasscodeAction(final NameIDDo objNameIDDo, String from, boolean isCheckout)
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(vecPostReasons != null && vecPostReasons.size() > 0)
				{
					new CommonDA().insertAllReasons(vecPostReasons);
					uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(CheckIN != null)
							CheckIN.putExtra("reason", objNameIDDo.strName);
						startActivityForResult(CheckIN, 0);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
