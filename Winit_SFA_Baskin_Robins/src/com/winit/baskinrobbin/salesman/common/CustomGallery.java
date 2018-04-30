package com.winit.baskinrobbin.salesman.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CustomGallery extends Gallery
{
	int offSetX,offSetY,movX,movY;
	boolean isFired;
	
	HorizontalScrollListener listener;
	
	public CustomGallery(Context context)
	{
		super(context);
		super.setFadingEdgeLength(0);
		super.setAnimationCacheEnabled(true);
		super.setUnselectedAlpha(100);
		super.setSpacing(20);
	}
	public CustomGallery(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) 
	{
		return true;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		int rawX = (int)event.getRawX();
		int rawY = (int)event.getRawY();
		
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			isFired = false;
			offSetX = (int)event.getRawX();
			offSetY	= (int)event.getRawY();
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			movX = rawX - offSetX;
			movY = rawY - offSetY;
			
			if(movX >2 || movX < -2 && !isFired)
			{
				isFired = true;
				
			}
			if(movY > 2 || movY < -2 && !isFired)
			{
				isFired = true;
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP && listener!=null)
		{
			if(isFired)
			{
				if(movX > 10)
					listener.galleryUpdate(this.getSelectedItemPosition());
				else if(movX < -10)
					listener.galleryUpdate(this.getSelectedItemPosition());
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	public void setListener(HorizontalScrollListener listener)
	{
		this.listener = listener;
	}
}
