package com.winit.baskinrobbin.salesman.common;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;

import com.winit.baskinrobbin.salesman.R;


public class Add_SKU_Dialog extends Dialog
{
	public Add_SKU_Dialog(Context context) 
	{
		super(context,R.style.Dialog);
		LayoutInflater inflater =getLayoutInflater();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout llEntirechrt = (LinearLayout) inflater.inflate(R.layout.add_sku_popup, null);
		this.setCancelable(true);
		setContentView(llEntirechrt, new LayoutParams(new Preference(context).getIntFromPreference("DEVICE_DISPLAY_WIDTH",320),LayoutParams.WRAP_CONTENT));
	}
}