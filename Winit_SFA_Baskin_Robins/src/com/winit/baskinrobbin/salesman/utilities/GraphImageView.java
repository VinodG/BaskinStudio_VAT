package com.winit.baskinrobbin.salesman.utilities;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.Preference;

public class GraphImageView extends View
{
	private Context context;
	private Matrix matrix;
	private Bitmap backGround, arrow;
	public Preference preference;
	
	Paint paint_needle, paint_text;
	GraphImageView customImageView;
//	String priceValue;
	String curencyCode;
	public static float minAngle = (float) 0;
	public static float maxAngle = (float) 180;
	public static float exact_angle = 0; 
	private float density;
	
	private static final boolean DEBUG = false;
	
	int width,height;
	
	Matrix matrix_needle;
	
	public static final int MAX_ANGLE = 360;
	public static final double PI_360 = (2 * Math.PI) / 360;
	
	private DecimalFormat decimalFormat;
	
	
	public GraphImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	
	public GraphImageView(Context context) 
	{
		super(context);
		init();
	}
	
	public void init()
	{
		releaseImageResources();
		customImageView 				= this;
		preference 						= 		new Preference(context);
		curencyCode						= preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
		BitmapFactory.Options options 	= new BitmapFactory.Options();
		options.inDither 				= true;

		arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arriow, options);
		backGround = BitmapFactory.decodeResource(getResources(), R.drawable.b1, options);
		
		paint_needle = new Paint();
		paint_needle.setStyle(Paint.Style.FILL);
		paint_needle.setAntiAlias(true);
		
		paint_text = new Paint();
		paint_text.setColor(Color.WHITE);
		paint_text.setAntiAlias(true);
		paint_text.setStyle(Paint.Style.FILL_AND_STROKE);
		paint_text.setTextSize(40);
		
		matrix_needle = new Matrix();
		
		density = getResources().getDisplayMetrics().density;
	}
	
	public void setUpCustomIV()
	{
		int height = backGround.getHeight();
		int width = backGround.getWidth();
		height += 0.40f* height; 
		width += 0.20f * width;
		LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(width, height);
		params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		this.setLayoutParams(params);
	}
	
	
	int wOffset;
	int hOffset;
	float needleBottomVisibleSemiCircleOffset;
	float bottomSemiCircleOffset;
	float centerValueOffset;
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
		wOffset = canvas.getWidth() / 2;
		hOffset = canvas.getHeight() / 6;
		needleBottomVisibleSemiCircleOffset = canvas.getHeight() * 0.07f;
		bottomSemiCircleOffset = canvas.getHeight()/10f;
		centerValueOffset = canvas.getHeight() * 0.4f;
		
		drawBg(canvas);
		drawAchived(canvas);
		drawTarget(canvas);
		drawInnerArc(canvas);
		drawNeedle(canvas);
		drawText(canvas);
		
		if(DEBUG)
		Log.d("View Properties", "gethHeight() = "+getHeight() +
				" getWidth() = "+getWidth());
		if(DEBUG)
		Log.d("Canvas Properties", "canvas.gethHeight() = "+canvas.getHeight() +
				" canvas.getWidth() = "+canvas.getWidth());
	}
	
	private long delay = 10000;
	private static final int HANDLER_WHAT = 1000;
	
	private static int j, tempStartAngle, tempSweepAngle;
	private CustomHandler h;
	private CustomThread th;
	
	
	public void onDestroy()
	{
		if(h != null)
			h.removeMessages(HANDLER_WHAT);
		if(th  != null && th.isAlive())
		{
			th.terminate();
			th = null;
		}
	}
	
	private static class CustomHandler extends Handler
	{
		private GraphImageView cImageView;
		public CustomHandler(GraphImageView cImageView)
		{
			this.cImageView = cImageView;
		}
		
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Bundle b = msg.getData();
			int startAngle = b.getInt("start_angle",tempStartAngle);
			int sweepAngle = b.getInt("sweep_angle",tempSweepAngle);
			int step = b.getInt("step", j);
			cImageView.calculateAngleOfDeviation(startAngle, sweepAngle, step);
		}
	}
	
	private class CustomThread extends Thread
	{
		private float startAngle, stopAngle;
		private CustomHandler h;
		public volatile boolean running = true;
		
		public CustomThread(float startAngle, float stopAngle, CustomHandler h)
		{
			this.startAngle = startAngle;
			this.stopAngle = stopAngle;
			this.h = h;
		}
		
		public void terminate() {
	        running = false;
	    }
		
		@Override
		public void run()
		{
			super.run();
			
			j = 0;
			tempStartAngle = 0;
			tempSweepAngle = 0;
			
			for (int i = (int)startAngle; i <= stopAngle && running; i++, j++)
			{
				try 
				{
					if(i <= stopAngle)
					{
						Thread.sleep(15);
					
						Message msg = new Message();
						Bundle b = new Bundle();
						b.putInt("step", j);
						b.putInt("start_angle", tempStartAngle = i);
						b.putInt("sweep_angle", tempSweepAngle = (int)stopAngle - i);
						
						msg.setData(b);
						msg.what = HANDLER_WHAT;
						if(running)
							h.sendMessage(msg);
						
					}

				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private float inital, maxValue, achieved, target;
	private float angleAchieved;
	
	/**
	 * For drawing a Semi Pie Chart with Minimum = 'initial',
	 * Maximum = 'maxValue', Target = 'target' and the user's 
	 * Achieved at 'achieved'.
	 * @param initial
	 * @param maxValue
	 * @param achieved
	 * @param target
	 */
	public void moveNeedle(float initial, float maxValue, float achieved, float target) 
	{
		if(!(maxValue >= initial && maxValue >= achieved && maxValue >= target
			&& achieved >= initial	&& target >= initial))
		{
			throw new IllegalArgumentException("Please send proper values.");
		}
		
		decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMinimumFractionDigits(2);
		this.inital = initial;
		this.maxValue = maxValue;
		this.achieved = achieved;
		this.target = target;
		
		float difference = maxValue - initial;
		angleAchieved = (achieved/difference) * 180 + 180;
		angleTarget = (target/difference) * 180 + 180;
		
		float startAngle = 180;
		float stopAngle = angleAchieved;
		
		if(h == null)
		{
			h = new CustomHandler(customImageView);
		}
		else
		{
			h.removeMessages(HANDLER_WHAT);
		}
		h.postDelayed(null, delay);
		
		if(th != null && th.isAlive())
		{
			th.terminate();
			th = null;
		}
		th = new CustomThread(startAngle, stopAngle, h);
		th.start();
	}
	
	public void drawBg(Canvas canvas)
	{
		canvas.drawBitmap(backGround, wOffset - backGround.getWidth() / 2, hOffset/*- speedo_meter.getHeight() / 2*/, null);
	}
	
	public void drawAchived(Canvas canvas)
	{
		paint_text.setColor(Color.WHITE);
		paint_text.setTextSize(15);
		paint_text.setTextAlign(Align.CENTER);
		paint_text.setColor(Color.parseColor("#ccE290B6"));
		
		if(startAngle + sweepAngle > 0)
		{
			canvas.drawArc(new RectF(
					(getWidth()/2) - (backGround.getWidth()/2), 
					hOffset, 
					getWidth() - (getWidth()/2) + (backGround.getWidth()/2), 
					hOffset + 2*getHeight() - 2*(getHeight() - backGround.getHeight())
					), startAngle, sweepAngle + 1f, true, paint_text);
			
			canvas.drawArc(new RectF(
					(getWidth()/2) - (backGround.getWidth()/2), 
					hOffset, 
					getWidth() - (getWidth()/2) + (backGround.getWidth()/2), 
					hOffset + 2*getHeight() - 2*(getHeight() - backGround.getHeight())
					), startAngle + sweepAngle, angleTarget - (startAngle + sweepAngle), true, paint_text);
		}
	}
	
	public void drawTarget(Canvas canvas)
	{
		paint_text.setColor(Color.parseColor("#cc2188D5"));
		canvas.drawArc(new RectF(
				(getWidth()/2) - (backGround.getWidth()/2), 
				hOffset, 
				(getWidth()/2) + (backGround.getWidth()/2), 
				hOffset + backGround.getWidth()
				), angleTarget , MAX_ANGLE - angleTarget, true, paint_text);
	}
	
	public void drawInnerArc(Canvas canvas)
	{
		paint_text.setColor(Color.parseColor("#ffffff"));
		canvas.drawArc(new RectF(
				(getWidth()/2) - ((backGround.getWidth()/2) * 0.36860068f) - 1f , 					// Left
				hOffset + backGround.getHeight() - 0.36860068f * (backGround.getWidth()/2) - 2.2f, 	// Top	
				(getWidth()/2) + ((backGround.getWidth()/2) * 0.36860068f) + 1f,  					// Right
				hOffset + backGround.getHeight() + 0.36860068f * (backGround.getWidth()/2) + 2.2f// Bottom	
				), 180f, 180f, true, paint_text);
	}
	
	public void drawNeedle(Canvas canvas)
	{
		matrix_needle.reset();
		matrix_needle.setTranslate(wOffset - backGround.getWidth()/2 - needleBottomVisibleSemiCircleOffset, 
				hOffset + backGround.getHeight() - arrow.getHeight()/2);
		matrix_needle.postRotate(step, wOffset, hOffset + backGround.getHeight());
		canvas.drawBitmap(arrow, matrix_needle, paint_needle);
	}
	
	public void drawText(Canvas canvas)
	{
		// Text Drawing.
		paint_text.setColor(Color.parseColor("#0464B5"));
		paint_text.setTextSize(28 * density);
		paint_text.setTypeface(Typeface.DEFAULT_BOLD);
		paint_text.setTextAlign(Align.CENTER);
		
		if(step > 0)
		{
			canvas.drawText((int)((step/180)*100)+"%"/*"68%"*/, (float)((canvas.getWidth() / 2)),
					(float)(hOffset + backGround.getHeight() - (0.37f * 0.30) * (backGround.getWidth()/2)), paint_text);
		}
		
		paint_text.setColor(Color.GRAY);
		paint_text.setTextSize(14 * density);
		paint_text.setTextAlign(Align.CENTER);
		canvas.drawText("Achieved", (float)((canvas.getWidth() / 2)),
		(float)(hOffset + backGround.getHeight()), paint_text);
		
		// Initial Value
		paint_text.setColor(Color.parseColor("#6C6C6C"));
		paint_text.setTextAlign(Align.LEFT);
		canvas.drawText(decimalFormat.format(inital)+"", (float)((canvas.getWidth() / 2) - backGround.getWidth()/2),
				(float)(hOffset + backGround.getHeight() + 1.5f * needleBottomVisibleSemiCircleOffset), paint_text);
		
		// Max Value
		paint_text.setTextAlign(Align.RIGHT);
		canvas.drawText(decimalFormat.format(maxValue)+"", (float)((canvas.getWidth()/2) + backGround.getWidth()/2),
				(float)(hOffset + backGround.getHeight() + 1.5f * needleBottomVisibleSemiCircleOffset), paint_text);
		
		// Achieved MTD Text painting.
		paint_text.setTextAlign(Align.CENTER);
		paint_text.setTypeface(Typeface.DEFAULT);
		paint_text.setTextSize(10 * density);
		
		if(angleAchieved >= 180)
		{
			
			if(angleAchieved >= 180 && angleAchieved < 180 + 30)
			{
				paint_text.setTextAlign(Align.LEFT);
			}
			else if(angleAchieved >= 180 + 30 && angleAchieved < 360 - 30)
			{
				paint_text.setTextAlign(Align.CENTER);
			}
			else if(angleAchieved >= 360 - 30 && angleAchieved <= 360)
			{
				paint_text.setTextAlign(Align.RIGHT);
			}
			
			canvas.drawLine((float)(getWidth()/2 + (backGround.getWidth()/2) * Math.cos((360 - angleAchieved)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2) * Math.sin((360 - angleAchieved)* PI_360)),
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .55f)) * Math.cos((360 - angleAchieved)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .55f)) * Math.sin((360 - angleAchieved)* PI_360)), 
					paint_text);
			canvas.drawText("Achieved", 
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .80f)) * Math.cos((360 - angleAchieved) * PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleAchieved)* PI_360) ),
					paint_text);
		
			canvas.drawText("MTD", 
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .80f)) * Math.cos((360 - angleAchieved) * PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleAchieved)* PI_360) + (needleBottomVisibleSemiCircleOffset * 3 / 4) ),
					paint_text);
		
			canvas.drawText(decimalFormat.format(achieved)+" AED",
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .80f)) * Math.cos((360 - angleAchieved) * PI_360)-10),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleAchieved)* PI_360) + (needleBottomVisibleSemiCircleOffset+15)), paint_text);
		}
		
		// Projected MTD Text painting.
		if(angleTarget >= 180)
		{
			if(angleTarget >= 180 && angleTarget < 180 + 30)
			{
				paint_text.setTextAlign(Align.LEFT);
			}
			else if(angleTarget >= 180 + 30 && angleTarget < 360 - 30)
			{
				paint_text.setTextAlign(Align.CENTER);
			}
			else if(angleTarget >= 360 - 30 && angleTarget <= 360)
			{
				paint_text.setTextAlign(Align.RIGHT);
			}
			
			canvas.drawLine((float)(getWidth()/2 + (backGround.getWidth()/2) * Math.cos((360 - angleTarget)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2) * Math.sin((360 - angleTarget)* PI_360)),
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .55f)) * Math.cos((360 - angleTarget)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .30f)) * Math.sin((360 - angleTarget)* PI_360)), 
					paint_text);
			
			canvas.drawText("Projected", (float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset * .60f)) * Math.cos((360 - angleTarget)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleTarget)* PI_360)), paint_text);
			
			canvas.drawText("MTD", (float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset* .60f)) * Math.cos((360 - angleTarget)* PI_360)),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleTarget)* PI_360) + (needleBottomVisibleSemiCircleOffset * 3 / 4)), paint_text);
	
		
			canvas.drawText(decimalFormat.format(target)+" AED", 
					(float)(getWidth()/2 + (backGround.getWidth()/2 + (hOffset* .60f)) * Math.cos((360 - angleTarget)* PI_360)-10),
					(float)(hOffset + backGround.getHeight() - (backGround.getWidth()/2 + (hOffset * .80f)) * Math.sin((360 - angleTarget)* PI_360) + (needleBottomVisibleSemiCircleOffset)+20), paint_text);
		}
		
		float diff = target - achieved;
		if(diff > 0)
		{
			paint_text.setTextAlign(Align.RIGHT);
			paint_text.setTypeface(Typeface.DEFAULT_BOLD);
			paint_text.setColor(Color.WHITE);
			
			paint_text.setTextSize(12 * density);
			RectF oval = 
					new RectF(
							(getWidth()/2) - 0.78f * (backGround.getHeight()), 
							hOffset + 0.22f * (backGround.getHeight()), 
							(getWidth()/2) + 0.78f * (backGround.getHeight()), 
							hOffset +  backGround.getHeight() + 0.78f * (backGround.getHeight())
							);
			
			Path path = new Path();
			path.addArc(oval, (angleTarget + angleAchieved)/2 - 100, 200);
			paint_text.setTextAlign(Align.CENTER);
			canvas.drawTextOnPath(decimalFormat.format(diff) + "", path, 0, 0, paint_text);
			paint_text.setTextSize(10 * density);
			paint_text.setColor(Color.BLACK);
			path.reset();
			oval.set((getWidth()/2) - 0.70f * (backGround.getHeight()), 
							hOffset + 0.3f * (backGround.getHeight()), 
							(getWidth()/2) + 0.70f * (backGround.getHeight()), 
							hOffset +  backGround.getHeight() + 0.70f * (backGround.getHeight())
							);
			path.addArc(oval, (angleTarget + angleAchieved)/2 - 100, 200);
			
			canvas.drawTextOnPath(curencyCode, path, 0, 0, paint_text);
			
		}
	}
	
	private float startAngle;
	private float sweepAngle;
	private float angleTarget;
	private float step;
	
	public void calculateAngleOfDeviation(int startAngle, int sweepAngle, int step) 
	{
		this.startAngle = startAngle; 
		this.sweepAngle = sweepAngle; 
		this.step = step;
		customImageView.invalidate();
	}
	
	public void releaseImageResources() {
		if (backGround != null)
		{
			backGround.recycle();
			backGround = null; 
		}
		if (arrow != null)
		{
			arrow.recycle();
			arrow = null;
		}
	}
}
