package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.BarChartView;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerHistoryBL;
import com.winit.baskinrobbin.salesman.dataobject.NewBarDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

/**
 * GraphViewDemo creates some dummy data to demonstrate the CustomerHistory component.
 * @author Arno den Hond
 *
 */
public class GraphViewDemo extends Activity 
{
	private TextView tvCustomer;
	private LinearLayout llGraph;
	
	ArrayList<Integer> arrPrevious, arrCurrent;
	
	private CustomerHistoryBL objCustomerHistoryBL;
	private String customerSiteID = "";
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphview);
		tvCustomer=(TextView)findViewById(R.id.tvCustomer);
		llGraph  =(LinearLayout)findViewById(R.id.llGraph);
		objCustomerHistoryBL = new CustomerHistoryBL();
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.size()>0)
		{
			tvCustomer.setText(" "+bundle.getString("Customer"));
			customerSiteID = ""+bundle.getString("customerSiteID");
		}
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
				final NewBarDO barDO = getCustomerHistoryChart(customerSiteID);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(barDO != null)
						{
							BarChartView chartView = new BarChartView(GraphViewDemo.this,barDO, getWindowManager().getDefaultDisplay().getWidth()-450, getWindowManager().getDefaultDisplay().getHeight()-100);
							llGraph.addView(chartView, new LayoutParams(getWindowManager().getDefaultDisplay().getWidth()-50 , getWindowManager().getDefaultDisplay().getHeight()-100));
						}
					}
				});
			}
		}).start();
	}
	
	public NewBarDO getCustomerHistoryChart(String CustomerSiteId)
	{
		Vector<NewBarDO> vecnBarDOs = objCustomerHistoryBL.getCustomerHistory(CustomerSiteId);
		arrPrevious = new ArrayList<Integer>();
		arrCurrent = new ArrayList<Integer>();
		int max = 0;//vecnBarDOs.get(0).previousMonth_Graph_Value;
		
		int j = 0;
		if(vecnBarDOs != null && vecnBarDOs.size() > 0)
		{
			for(int i = 0; i < 12 ; i++)
			{
				if(i < vecnBarDOs.size())
				{
					if(max < vecnBarDOs.get(i).previousMonth_Graph_Value)
					{
						max = vecnBarDOs.get(i).previousMonth_Graph_Value;
					}
					if(max < vecnBarDOs.get(i).currentMonth_Graph_Value)
					{
						max = vecnBarDOs.get(i).currentMonth_Graph_Value;
					}
				}
				if(i == vecnBarDOs.get(j).MONTH-1)
				{
					arrPrevious.add(vecnBarDOs.get(j).previousMonth_Graph_Value);
					arrCurrent.add(vecnBarDOs.get(j).currentMonth_Graph_Value);
					j++;
				}
				else
				{
					arrPrevious.add(0);
					arrCurrent.add(0);
				}
				
			}
			LogUtils.errorLog("max", "max "+max);
			
			final NewBarDO barDO = new NewBarDO();
			barDO.MAX_VALUE = max;
			barDO.DIFFERENCE = 5000;
			barDO.PREVIOUS_MONTH_COLOR = Color.RED;
			barDO.CURRENT_MONTH_COLOR = Color.GREEN;
			barDO.arrcurrentMonth = arrCurrent;
			barDO.arrpreviousMonth = arrPrevious;
			
			return barDO;
		}
		return null;
	}
	
}