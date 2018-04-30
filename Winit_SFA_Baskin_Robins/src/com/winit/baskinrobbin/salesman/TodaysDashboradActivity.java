package com.winit.baskinrobbin.salesman;

import java.text.NumberFormat;
import java.util.Calendar;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerHistoryBL;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class TodaysDashboradActivity extends BaseActivity {
	private LinearLayout llTodaysDashBoard,llbillOutlets,llTotalbills;
	private TextView tvtodaysdashboardHeader,tv_scheduledoutletsValue,tv_scheduledoutlets,tv_coveredoutletsValue,tv_coveredoutlets,tv_percoverageValue,tv_percoverage,tv_totalcallsmadeValue,tv_totalcallsmade,tv_productivecallsValue,tv_productivecalls,tv_productiveValueper,tv_productiveper,tv_billstotalcallsValue,tv_billstotal,tv_totalbillmadeValue,tv_totalbillmade,tv_perbillproductiveValue,tv_perbillproductive,tv_effectivecoveredoutletsValue,tv_effectivecoveredoutlets,tv_effectivebilloutletsValue,tv_effectivebilloutlets,tv_effectivepercoverageValue,tv_effectivepercoverage;
	
	
	//Outlet values
	String scheduledoutletvalue ="",coveredoutletValue="",percentageofcoveragevalue = "";
	//Calls Values
	String totalcallsmadevalue ="",productivecallsValue="",percentageofproductivityvalue = "";
	//Bills Values
	String billstotalcallsValue ="",totalbillsmadeValue="",percentageofbillproductivityvalue = "";
	//Calls Values
	String effectivecoveredoutletevalue ="",effectivebilloutletValue="",percentageofeffectivecoverageyvalue = "";
			
			
	@Override
	public void initialize() 
	{
		
		llTodaysDashBoard = (LinearLayout) getLayoutInflater().inflate(R.layout.todaysdashboardnew, null);
		llBody.addView(llTodaysDashBoard, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializecontrols();
		setTypeFace(llTodaysDashBoard);
		
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
		{
			llTotalbills.setVisibility(View.GONE);
			llbillOutlets.setVisibility(View.GONE);
		}
		
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				String fBalance = new CustomerHistoryBL().getCountOfSurvedCustomer(true);
				String dBalance = new CustomerHistoryBL().getCountOfSurvedCustomer(false);
				
				String fProdcutiveOrder = ""+new OrderDA().getProductiveOrders();
				
				String fProdcutiveBills = ""+new PaymentDetailDA().getProductivePayments(true);
				
				String fProdcutiveBillsOutlets = ""+new PaymentDetailDA().getProductivePayments(false);
				
				
				NumberFormat formatter = NumberFormat.getNumberInstance();
				formatter.setMinimumFractionDigits(2);
				formatter.setMaximumFractionDigits(2);
				
				Calendar c = Calendar.getInstance();
				int date = c.get(Calendar.DAY_OF_MONTH);
				long timeStamp = c.getTimeInMillis();
				String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
				
				scheduledoutletvalue = ""+new CustomerDetailsDA().getJourneyPlanCount(timeStamp, date, day, preference.getStringFromPreference(Preference.EMP_NO,""));
				coveredoutletValue = ""+fBalance;
				percentageofcoveragevalue = ""+getPerValue(coveredoutletValue,scheduledoutletvalue);
				
				totalcallsmadevalue = dBalance;
				productivecallsValue = fProdcutiveOrder;
				percentageofproductivityvalue = ""+getPerValue(productivecallsValue,totalcallsmadevalue);
				
				billstotalcallsValue = dBalance;
				totalbillsmadeValue = fProdcutiveBills;
				percentageofbillproductivityvalue = ""+getPerValue(totalbillsmadeValue,billstotalcallsValue);
				
				effectivecoveredoutletevalue = coveredoutletValue;
				effectivebilloutletValue = fProdcutiveBillsOutlets;
				percentageofeffectivecoverageyvalue = ""+getPerValue(effectivebilloutletValue,effectivecoveredoutletevalue);
				
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						setOutlets(scheduledoutletvalue,coveredoutletValue,percentageofcoveragevalue);
						
						setCalls(totalcallsmadevalue,productivecallsValue,percentageofproductivityvalue);
								
						setBills(billstotalcallsValue,totalbillsmadeValue,percentageofbillproductivityvalue);
						
						setEffectiveCoverageValues(effectivecoveredoutletevalue,effectivebilloutletValue,percentageofeffectivecoverageyvalue);
						
					}
				});
			}
		}).start();
		
		
		
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public String getPerValue(String firstValue, String LastValue)
	{
		String output = "0";
		try 
		{
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMinimumFractionDigits(2);
			formatter.setMaximumFractionDigits(2);
			if(!firstValue.equalsIgnoreCase("0"))
			{
				if( StringUtils.getFloat(LastValue) <= 0)
					LastValue = "1";
				
				output = formatter.format(StringUtils.getFloat(firstValue)/ StringUtils.getFloat(LastValue) * 100);
				
				if(output.contains("100"))
					output = "100";
			}
		} 
		catch (Exception e) 
		{
			output = "0";
		}
		
		output = output.replace("NaN", "0");
		output = output.length() > 5 ? output.substring(0,5):output;
		
		return output.endsWith(".") == true ? output.substring(0,output.length()-1):output;
	}

	//setting Outlet values
	private void setOutlets(String scheduledoutletvalue,String coveredoutletValue, String percentageofcoverage) 
	{

		tv_scheduledoutletsValue.setText(scheduledoutletvalue);
		tv_coveredoutletsValue.setText(coveredoutletValue);
		tv_percoverageValue.setText(percentageofcoverage);
	}
	
	
	//setting Calls values
	private void setCalls(String totalcallsmadevalue,String productivecallsValue, String percentageofproductivityvalue) 
	{

			
		tv_totalcallsmadeValue.setText(totalcallsmadevalue);
		tv_productivecallsValue.setText(productivecallsValue);
		tv_productiveValueper.setText(percentageofproductivityvalue);
	}
	
	//setting Bills values
	private void setBills(String tv_billstotalcallsValue2,String totalbillsmadeValue, String percentageofbillproductivityvalue) 
	{
		tv_billstotalcallsValue.setText(tv_billstotalcallsValue2);
		tv_totalbillmadeValue.setText(totalbillsmadeValue);
		tv_perbillproductiveValue.setText(percentageofbillproductivityvalue);
			
	}
	
	
	//setting Effective coverage values
	private void setEffectiveCoverageValues(String effectivecoveredoutletevalue,String effectivebilloutletValue,String percentageofeffectivecoverageyvalue) 
	{
		
		tv_effectivecoveredoutletsValue.setText(effectivecoveredoutletevalue);
		tv_effectivebilloutletsValue.setText(effectivebilloutletValue);
		tv_effectivepercoverageValue.setText(percentageofeffectivecoverageyvalue);
		
	}

	
	private void initializecontrols() 
	{
		tvtodaysdashboardHeader		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tvtodaysdashboardHeader);
		tv_scheduledoutletsValue	 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_scheduledoutletsValue);
		tv_scheduledoutlets			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_scheduledoutlets);
		tv_coveredoutletsValue		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_coveredoutletsValue);
		tv_coveredoutlets			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_coveredoutlets);
		tv_percoverageValue			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_percoverageValue);
		tv_percoverage				 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_percoverage);
		tv_totalcallsmadeValue		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_totalcallsmadeValue);
		tv_totalcallsmade			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_totalcallsmade);
		tv_productivecallsValue		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_productivecallsValue);
		tv_productivecalls			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_productivecalls);
		tv_productiveValueper		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_productiveValueper);
		tv_productiveper			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_productiveper);
		tv_billstotalcallsValue		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_billstotalcallsValue);
		tv_billstotal				 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_billstotal);
		tv_totalbillmadeValue		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_totalbillmadeValue);
		tv_totalbillmade			 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_totalbillmade);
		tv_perbillproductiveValue	 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_perbillproductiveValue);
		tv_perbillproductive		 	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_perbillproductive);
		tv_effectivecoveredoutletsValue = (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivecoveredoutletsValue);
		tv_effectivecoveredoutlets		= (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivecoveredoutlets);
		tv_effectivebilloutletsValue	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivebilloutletsValue);
		tv_effectivebilloutlets			= (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivebilloutlets);
		tv_effectivepercoverageValue	= (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivepercoverageValue);
		tv_effectivepercoverage			= (TextView) llTodaysDashBoard.findViewById(R.id.tv_effectivepercoverage);
		
		llbillOutlets					= (LinearLayout) llTodaysDashBoard.findViewById(R.id.llbillOutlets);
		llTotalbills					= (LinearLayout) llTodaysDashBoard.findViewById(R.id.llTotalbills);
	}

}
