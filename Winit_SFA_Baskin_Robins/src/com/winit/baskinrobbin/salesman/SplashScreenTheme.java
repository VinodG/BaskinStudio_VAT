package com.winit.baskinrobbin.salesman;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CustomGallery;
import com.winit.baskinrobbin.salesman.common.HorizontalScrollListener;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class SplashScreenTheme extends BaseActivity implements HorizontalScrollListener
{
	private LinearLayout llSplashScreenTheme;
	private CustomGallery gvTheme;
	private int splashBg[] = {R.drawable.sp};
	private CustomGalleryAdapter customGalleryAdapter;
	private TextView tvSettings;
	private LinearLayout llDot;
	private HorizontalScrollView hsvDots;
	private int position =0;
	private boolean isFirstTime=true;
	
	@Override
	public void initialize()
	{
		 llSplashScreenTheme		=	(LinearLayout) inflater.inflate(R.layout.splashscreentheme, null);
		 llBody.addView(llSplashScreenTheme,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		 intialiseControls();
		 setTypeFace(llSplashScreenTheme);
		 tvSettings.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		 customGalleryAdapter = new CustomGalleryAdapter();
		 gvTheme.setAdapter(customGalleryAdapter);
		 gvTheme.setListener(this);
		 gvTheme.setSpacing(20);
		 gvTheme.setOnItemClickListener(new OnItemClickListener()
		 {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				preference.saveIntInPreference("CurrentSplash", (position+1));
				preference.commitPreference();
				isFirstTime =true;
				customGalleryAdapter.refresh();
	      		
			}
		});
		 
		 for(int i=0;splashBg!=null && i < splashBg.length ; i++)
		 {
			 ImageView ivNewImage = new ImageView(this);
			 ivNewImage.setBackgroundResource(R.drawable.dot_news);
			 if(i == (preference.getIntFromPreference("CurrentSplash", 1)-1))
			 {
				 ivNewImage.setBackgroundResource(R.drawable.dot_news_hover);
			 }
			 ivNewImage.setId(i);
			 ivNewImage.setOnClickListener(new OnClickListener()
			 {				
				 @Override
				 public void onClick(View v)
				 {
					 for(int j = 0; j <  splashBg.length; j++)
					 {
						 ((ImageView)findViewById(j)).setBackgroundResource(R.drawable.dot_news);
					 }
					 v.setBackgroundResource(R.drawable.dot_news_hover);
					 galleryUpdate(v.getId());
					 gvTheme.setSelection(v.getId());
				 }
			 });
				
			 LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			 params1.setMargins(3, 3, 3, 5);
			 llDot.addView(ivNewImage, params1);
		 }
	}
	@Override
	protected void onResume() 
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_GOTO_SETTINGS_FINISH);
		registerReceiver(FinishReceiver, filter);
	}
	BroadcastReceiver FinishReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equalsIgnoreCase(AppConstants.ACTION_GOTO_SETTINGS_FINISH))
				finish();
		}
	};
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unregisterReceiver(FinishReceiver);
	}
	private void intialiseControls() 
	{
		gvTheme		=	(CustomGallery)llSplashScreenTheme.findViewById(R.id.gvTheme);
		llDot 		= 	(LinearLayout)llSplashScreenTheme.findViewById(R.id.llDots);
		tvSettings	= 	(TextView)llSplashScreenTheme.findViewById(R.id.tvSettings);
		hsvDots		= 	(HorizontalScrollView)llSplashScreenTheme.findViewById(R.id.hsvDots);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public class CustomGalleryAdapter extends BaseAdapter
	{

		private int selectedTheme;
		public CustomGalleryAdapter() 
		{
			selectedTheme = preference.getIntFromPreference("CurrentSplash", 1);
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					gvTheme.setSelection(selectedTheme-1);
				}
			},200);
		}
		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return splashBg.length;
		}

		public void refresh()
		{
			selectedTheme = preference.getIntFromPreference("CurrentSplash", 1);
			notifyDataSetChanged();
		}
		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView					=	(LinearLayout)inflater.inflate(R.layout.splash_theme_cell, null);
			ImageView ivTheme			=	(ImageView)convertView.findViewById(R.id.ivTheme);
//			ImageView ivSelectedTheme	=	(ImageView)convertView.findViewById(R.id.ivSelectedTheme);
			ivTheme.setImageResource(splashBg[position]);
			if(position ==(selectedTheme-1)  && isFirstTime)
			{
				gvTheme.setSelection(position);
			}
			if(position ==(selectedTheme-1))
			{
				isFirstTime = false;
//				ivSelectedTheme.setImageResource(R.drawable.radioclick);
			}
			else
			{
//				ivSelectedTheme.setImageResource(R.drawable.radionor);
			}
			return convertView;
		}
	}

	@Override
	public void galleryUpdate(int position)
	{
		LogUtils.infoLog("position",""+position);
		this.position = position;
//		friendlyGallery.setSelection(position);
//		friendlyGallery.scrollTo(0, 0);
		if( llDot != null &&  llDot.getChildCount() > 0 )
		{
			for(int j=0; j <  llDot.getChildCount(); j++)
			{
				try
				{
					if(j == position)
					{
						((ImageView)llDot.getChildAt(j)).setBackgroundResource(R.drawable.dot_news_hover);
					}
					else
					{
						((ImageView)llDot.getChildAt(j)).setBackgroundResource(R.drawable.dot_news);
					}
					
					if(position > 11 && position < 22)
					{
						hsvDots.scrollTo(preference.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320)/2, 0);
					}
						hsvDots.scrollTo(0, 0);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
