package com.winit.baskinrobbin.salesman;

import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutApplicationActivity extends BaseActivity
{

	private LinearLayout llAssetsList ;
	@Override
	public void initialize() 
	{
		llAssetsList			=	(LinearLayout)inflater.inflate(R.layout.about_application,null);
		llBody.addView(llAssetsList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		setTypeFace(llAssetsList);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	private void initializeControls()
	{
		TextView tvCurrentversion	=   (TextView)llAssetsList.findViewById(R.id.tvCurrentversion);
		 if(ServiceURLs.MAIN_GLOBAL_URL.equalsIgnoreCase(ServiceURLs.URL_Live))
			 tvCurrentversion.setText(getString(R.string.app_name_ver_live));
	  else if(ServiceURLs.MAIN_GLOBAL_URL.equalsIgnoreCase(ServiceURLs.URL_Test))
		  tvCurrentversion.setText(getString(R.string.app_name_ver_test));
	}
}
