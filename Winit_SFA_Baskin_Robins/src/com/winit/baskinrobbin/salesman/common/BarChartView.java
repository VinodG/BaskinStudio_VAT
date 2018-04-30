package com.winit.baskinrobbin.salesman.common;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.winit.baskinrobbin.salesman.dataobject.NewBarDO;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class BarChartView extends View
{

	private Paint arcPaint, arcPaint1;
	private int DEVICE_HIGHT ;
	private int DEVICE_WIDTH ;
	
	
	ArrayList<Integer> arrCurrentMonthResult = new ArrayList<Integer>();
	
	ArrayList<Integer> currentMonth_Graph_Value;// [] = {5000,7000,6000,13000,14000,18000 ,5000,7000,6000,13000,14000,18000};
	
	ArrayList<Integer> arrPreviousMonthResult = new ArrayList<Integer>();
	
	ArrayList<Integer> previousMonth_Graph_Value;// [] = {13000,14000,18000 ,5000,7000,6000 ,13000,14000,18000,13000,14000,18000};
	
//	ArrayList<Integer> arrPreviousMonthLineResult = new ArrayList<Integer>();
//	
//	int[] previousMonth_Line_Value ;//[] = {13000,14000,18000 ,5000,7000,6000 ,13000,14000,18000,13000,14000,18000};
//	
//	ArrayList<Integer> arrCurrentMonthLineResult = new ArrayList<Integer>();
//	
//	int[] currentMonth_Line_Value ;//[] = {5000,7000,6000,13000,14000,18000 ,5000,7000,6000,13000,14000,18000};
	
	int MAX_VALUE, DIFFERENCE, CURRENT_MONTH_COLOR, PREVIOUS_MONTH_COLOR;
	
	
	public BarChartView(Context context, NewBarDO barDO, int width, int height) 
	{
		super(context);
		
		LogUtils.errorLog("width", ""+width);
		LogUtils.errorLog("height", ""+height);
		
		this.previousMonth_Graph_Value 	= 	barDO.arrpreviousMonth;
//		this.previousMonth_Line_Value 	= 	barDO.previousMonth_Line_Value;
		this.currentMonth_Graph_Value 	= 	barDO.arrcurrentMonth;
//		this.currentMonth_Line_Value 	= 	barDO.currentMonth_Line_Value;
		this.MAX_VALUE 					= 	barDO.MAX_VALUE;
		this.DIFFERENCE 				= 	barDO.DIFFERENCE;
		this.PREVIOUS_MONTH_COLOR 		= 	barDO.PREVIOUS_MONTH_COLOR;
		this.CURRENT_MONTH_COLOR 		= 	barDO.CURRENT_MONTH_COLOR;
		
		DEVICE_HIGHT = height;
		DEVICE_WIDTH = width;
		
		initialise();
	}
	public BarChartView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initialise();
	}
	private void initialise()
	{
		arcPaint = new Paint();
		arcPaint.setDither(true);
		arcPaint.setColor(PREVIOUS_MONTH_COLOR);
		arcPaint.setAntiAlias(true);
		
		arcPaint1 = new Paint();
		arcPaint1.setDither(true);
		arcPaint1.setColor(CURRENT_MONTH_COLOR);
		arcPaint1.setAntiAlias(true);
		
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		int max_Value = MAX_VALUE;
		int min_Value = 0;
		int difference = DIFFERENCE;
		
		int Bound_Top_x,Bound_Top_y, Bound_Center_x, Bound_Center_y, Bound_Right_x, Bound_Right_y;
		
		Bound_Top_x = 60;
		Bound_Top_y = 10;
		Bound_Center_x = 60;
		Bound_Center_y = DEVICE_HIGHT - 100;
		Bound_Right_x = DEVICE_WIDTH - 20;
		Bound_Right_y = DEVICE_HIGHT - 100;
		
		drawValuePoints(canvas, max_Value,min_Value,difference,Bound_Top_y,Bound_Center_y,Bound_Top_x , Bound_Center_x);
		
		drawBounderies(canvas,Bound_Top_x,Bound_Top_y, Bound_Center_x, Bound_Center_y, Bound_Right_x, Bound_Right_y);
		
		drawMonths(canvas,Bound_Top_x,Bound_Top_y, Bound_Center_x, Bound_Center_y, Bound_Right_x, Bound_Right_y);
//		drawVerticalLines(canvas,Bound_Top_x,Bound_Top_y, Bound_Center_x, Bound_Center_y, Bound_Right_x, Bound_Right_y);
	}
	
	
//	private void drawVerticalLines(Canvas canvas, int boundTopX, int boundTopY,	int boundCenterX, int boundCenterY, int boundRightX, int boundRightY)
//	{
//		int range = boundTopX - boundCenterX;
//		
//		for(int ranges = 0 ; ranges < range ;ranges = ranges+1000)
//		{
//			canvas.drawLine(boundTopX - 20, boundTopY - ranges, boundTopX, boundTopY - ranges, arcPaint);
//		}
//	}
	
	private void drawValuePoints(Canvas canvas, int max_Value, int min_Value, int difference, int Bound_Top_y, int Bound_Center_y,int Bound_Top_x ,int Bound_Center_x) 
	{
		int total_Bounds = (int) Math.ceil((float)(max_Value - min_Value)/difference);
		int total_Space = Bound_Center_y - Bound_Top_y;
		
		int pointRatio = DIFFERENCE/10;
		
		LogUtils.errorLog("total_Bounds", "total_Bounds"+total_Bounds);
		
		if(total_Bounds <= 0)
			total_Bounds = 1;
		
		int bound_value = total_Space/total_Bounds;
		
		int x1,x2,y1,y2;
		
		TextPaint paint = new TextPaint();
		float width = paint.measureText(""+(min_Value));
		float height  = paint.getTextSize();
		
		// Draw Center Line
		canvas.drawLine(Bound_Top_x, Bound_Center_y, Bound_Top_x - 10, Bound_Center_y, arcPaint);
		
		canvas.drawText(""+(min_Value),Bound_Top_x - width - 20   ,Bound_Center_y + height/2 , arcPaint);
		
		x1 = Bound_Center_y;
		int value = min_Value;
		
		int cur_x = x1;
		int pre_x = x1;
		
		for(int i =0 ; i < total_Bounds ; i++)
		{
			x1 = x1 - bound_value;
			canvas.drawLine(Bound_Top_x, x1, Bound_Top_x - 10, x1, arcPaint);
			
			value = value + difference;
			
//			if(value > max_Value)
//			{
//				value = max_Value;
//			}
			width = paint.measureText(""+(value));
			height  = paint.getTextSize();
			canvas.drawText(""+(value),Bound_Top_x - width - 20   ,x1 + height/2 , arcPaint);
			
			pre_x = cur_x;
			cur_x = x1;
			LogUtils.errorLog("pre_x", "pre_x "+i+" "+pre_x);
			LogUtils.errorLog("cur_x", "cur_x "+i+" "+cur_x);
			
			
			if(i == total_Bounds-1)
			{
				for(int j =0 ; j < currentMonth_Graph_Value.size() ; j ++)
				{
					if(currentMonth_Graph_Value.get(j) <= value )
					{
						LogUtils.errorLog("Top", "Top "+i+" "+x1);
						LogUtils.errorLog("value", "value "+i+" "+value);
						
						int result = Bound_Center_y -((pre_x  -  cur_x)/10 )* ((currentMonth_Graph_Value.get(j) - 0)/pointRatio);
						
						LogUtils.errorLog("new", "new "+i+" "+result);
						
						arrCurrentMonthResult.add(result);
					}
				}
				
				for(int j =0 ; j < previousMonth_Graph_Value.size() ; j ++)
				{
					if(previousMonth_Graph_Value.get(j) <= value )
					{
						LogUtils.errorLog("Top", "Top "+i+" "+x1);
						LogUtils.errorLog("value", "value "+i+" "+value);
						
						
						int result = Bound_Center_y -((pre_x  -  cur_x)/10 )* ((previousMonth_Graph_Value.get(j) - 0)/pointRatio);
						
						LogUtils.errorLog("new", "new "+i+" "+result);
						
						
						arrPreviousMonthResult.add(result);
//						canvas.drawLine(Bound_Top_x, result, Bound_Top_x - 10, result, arcPaint1);
					}
				}
				
			}
		}
	}
	
	
	private void drawBounderies(Canvas canvas, int Bound_Top_x,int Bound_Top_y, int Bound_Center_x, int Bound_Center_y, int Bound_Right_x, int Bound_Right_y) 
	{
		// vertical Line
		canvas.drawLine(Bound_Top_x, Bound_Top_y, Bound_Center_x, Bound_Center_y, arcPaint);
		
		// Horizontal Line
		canvas.drawLine(Bound_Center_x, Bound_Center_y, DEVICE_WIDTH+400, Bound_Right_y, arcPaint);
	}
	
	
	private void drawMonths(Canvas canvas, int Bound_Top_x,int Bound_Top_y, int Bound_Center_x, int Bound_Center_y, int Bound_Right_x, int Bound_Right_y) 
	{
		
//		int height [] = {100, 90, 50, 10, 20, 30, 100, 10, 50, 80,20};
		String Month [] = {"JAN","FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SPT", "OCT", "NOV","DEC"};
		
		int Max_Width = DEVICE_WIDTH-100;
		
		int layout_Diff = 10;
		int Layout_size = Max_Width/12 - (layout_Diff*2);
		
		LogUtils.errorLog("Layout_size", ""+Layout_size);
		int x1,x2,y1,y2, dx, dx1;
		
		int curr_previous = 0;
		int curr_Current = 0;
		
		x1 = Bound_Center_x+layout_Diff + Layout_size;
		y1 = Bound_Center_y;
		x2 = Bound_Center_x+layout_Diff + Layout_size;
		y2 = Bound_Center_y+10;
		dx = Bound_Center_x+layout_Diff;
		dx1 = x1 + Layout_size;
		
		
		curr_previous = dx + Layout_size/2;
		curr_Current = x1 + Layout_size / 2;
		
		canvas.drawLine(x1, y1, x2, y2, arcPaint);
		canvas.drawRect(dx, arrPreviousMonthResult.get(0), x2, Bound_Center_y, arcPaint);
		canvas.drawRect(x1, arrCurrentMonthResult.get(0), dx1, Bound_Center_y, arcPaint1);
		
		TextPaint paint = new TextPaint();
		float width = paint.measureText("JAN");

		
		canvas.drawText("JAN",x1-(width)/2 ,DEVICE_HIGHT - 80 , arcPaint);
		
		
		canvas.drawRect(DEVICE_WIDTH/4, DEVICE_HIGHT - 70, DEVICE_WIDTH/4+10, DEVICE_HIGHT - 60, arcPaint);
		canvas.drawText("Previous",DEVICE_WIDTH/4+20 ,DEVICE_HIGHT - 60 , arcPaint);
		
		canvas.drawRect(DEVICE_WIDTH/2, DEVICE_HIGHT - 70, DEVICE_WIDTH/2+10, DEVICE_HIGHT - 60, arcPaint1);
		canvas.drawText("Current",DEVICE_WIDTH/2+20 ,DEVICE_HIGHT - 60 , arcPaint1);
		
		for(int i =1 ; i < arrPreviousMonthResult.size() ; i++)
		{
			
			x1 = x1 + Layout_size + layout_Diff + Layout_size;
			y1 = Bound_Center_y;
			x2 = x2 + Layout_size + layout_Diff + Layout_size;
			y2 = Bound_Center_y+10;
			dx = x1 - Layout_size;
			dx1 = x1 + Layout_size;
			
			curr_previous = dx + Layout_size /2;
			
			curr_Current = x1 + Layout_size / 2;
			
			// Draw Center Line
			canvas.drawLine(x1, y1, x2, y2, arcPaint);
			
			// Draw Previous month rect
			canvas.drawRect(dx, arrPreviousMonthResult.get(i), x2, Bound_Center_y, arcPaint);
			
			// Draw current month rect
			canvas.drawRect(x1, arrCurrentMonthResult.get(i), dx1, Bound_Center_y, arcPaint1);
			
			if(i<Month.length)
			{
				width = paint.measureText(Month[i]);
			// Draw month text
				canvas.drawText(Month[i],x1-(width)/2 ,DEVICE_HIGHT - 80 , arcPaint);
			}
		}
	}
}
