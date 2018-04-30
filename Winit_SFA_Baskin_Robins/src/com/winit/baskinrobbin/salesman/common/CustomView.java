
package com.winit.baskinrobbin.salesman.common;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.AbsoluteLayout;

@SuppressWarnings("deprecation")
public class CustomView extends AbsoluteLayout
{
	private int left,top,right,bottom;
	public String titleName;
	public Bitmap bitmap;
	public int y;
	public CustomView(Context context)
	{
		super(context);
		left = 0;
		top = 0;
		right = 60;
		bottom = 20;
		titleName = "new point";
		y=22;
	}
	
}
