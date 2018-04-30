package com.winit.baskinrobbin.salesman.common;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView 
{
	private Context context;
	private boolean isScrollEnable = true, isScrolled = true;
	public CustomScrollView(Context context, LinearLayout llMain)
	{
		super(context);
		this.context = context;
		this.addView(llMain, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.setScrollbarFadingEnabled(false);
		this.setVerticalScrollBarEnabled(false);
	}
	 @Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		 if (!isScrollEnable)
			 return false;
		 else 
		 {
			isScrolled = true;
		    return super.onInterceptTouchEvent(ev);
		 }
	}
	 
    public void setScrollable(boolean scrollable)
    {
        isScrollEnable = scrollable;
    }
    public boolean IsScrollable()
    {
        return isScrollEnable;
    }
    public boolean isScrolled()
    {
        return isScrolled;
    }
    public void setScrolled(boolean isScrolled)
	{
		this.isScrolled = isScrolled;
	}
}
