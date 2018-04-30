package com.winit.baskinrobbin.salesman.common;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView
{
	private Context context;
	private boolean mScrollable = true, isScrolled = true;
	public CustomListView(Context context)
	{
		super(context);
		this.context = context;
	}
	 @Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		 if (!mScrollable)
			 return false;
		 else 
		 {
			 isScrolled = true;
		    return super.onInterceptTouchEvent(ev);
		 }
	}
	 
    public void setScrollable(boolean scrollable)
    {
        mScrollable = scrollable;
    }
    
    public boolean IsScrollable()
    {
        return mScrollable;
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
