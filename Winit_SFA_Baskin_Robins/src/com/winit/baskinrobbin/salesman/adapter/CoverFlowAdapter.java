package com.winit.baskinrobbin.salesman.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Rotate3dAnimation;

public class CoverFlowAdapter extends BaseAdapter{

	Context con;
	int[] images;
	String[] info;
	public CoverFlowAdapter(Context productCatalogActivity, int[] images,String[] info)
	{
		this.con = productCatalogActivity;
		this.images = images;
		this.info = info;
	}

	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) 
	{
		if(view == null)
//		{
			view = ((Activity)con).getLayoutInflater().inflate(R.layout.coverflow_cell, null);
			ImageView iv 	= (ImageView) view.findViewById(R.id.ivCoverFlowChild);
			TextView tv 	= (TextView) view.findViewById(R.id.tvInfo);
			final LinearLayout llInfo			= (LinearLayout) view.findViewById(R.id.llInfo);
			LinearLayout llCoverImage			= (LinearLayout) view.findViewById(R.id.llCoverImage);
			iv.setImageResource(images[pos]);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			tv.setText(info[pos]);
			
			int devic_width 	= AppConstants.DIVICE_WIDTH;
			if(devic_width == 1080)
				((LinearLayout)(view)).setLayoutParams(new Gallery.LayoutParams(AppConstants.DIVICE_WIDTH/5 + 50, AppConstants.DIVICE_HEIGHT/6));
			else
				((LinearLayout)(view)).setLayoutParams(new Gallery.LayoutParams(AppConstants.DIVICE_WIDTH/3 + 50, AppConstants.DIVICE_HEIGHT/4 + 30));
			
			BitmapDrawable b = (BitmapDrawable)(iv.getDrawable());
			b.setAntiAlias(true);
			final View convertView = view;
			llCoverImage.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if(llInfo.isShown())
						applyRotation(true, 0, -90, convertView);
					else
						applyRotation(false, 0, -90, convertView);
				}
			});
			llInfo.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					applyRotation(true, 0, -90, convertView);
				}
			});
			
//		}
		
		return view;
	}
	
	private void applyRotation(boolean isInfoShown, float start, float end, View llRotateView) 
	{
		// Find the center of the container
		final float centerX = llRotateView.getWidth() / 2.0f;
		final float centerY = llRotateView.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(isInfoShown, llRotateView));


		View image =  llRotateView.findViewById(R.id.llCoverImage);

//		ImageView temp = (ImageView) llRotateView.findViewById(R.id.ivTemp);
        	 image.startAnimation(rotation);
        	
//        image.startAnimation(rotation);
		
//		llRotateView.startAnimation(rotation);
	}

	private final class DisplayNextView implements Animation.AnimationListener 
	{
		private boolean isInfoShown;
		private View llRotateView;
		private DisplayNextView(boolean isInfoShown, View llRotateView) 
		{
			this.isInfoShown = isInfoShown;
			this.llRotateView =llRotateView;
		}

		public void onAnimationStart(Animation animation) 
		{
			
		}

		public void onAnimationEnd(Animation animation) 
		{
			llRotateView.post(new SwapViews(isInfoShown, llRotateView));
		}

		public void onAnimationRepeat(Animation animation) 
		{
		}
	}
	
	 private final class SwapViews implements Runnable 
	 {
	        private boolean isInfoShown;
	        View llRotateView;
	        public SwapViews(boolean isInfoShown, View llRotateView) 
	        {
	        	this.isInfoShown 		= isInfoShown;
	            this.llRotateView 	= llRotateView;
	        }

	        public void run() 
	        {
	            final float centerX = llRotateView.getWidth() / 2.0f;
	            final float centerY = llRotateView.getHeight() / 2.0f;
	            Rotate3dAnimation rotation;
	            
	            View image  =  llRotateView.findViewById(R.id.llCoverImage);
//	            View llParentInfo =  llRotateView.findViewById(R.id.llInfo);
	            
	            View scInfo 	=  llRotateView.findViewById(R.id.scInfo);
//	            View llInfo 	=  llRotateView.findViewById(R.id.llInfo);
	            View iv 	= llRotateView.findViewById(R.id.ivCoverFlowChild);
//	            View tv 	= llRotateView.findViewById(R.id.tvInfo);
	            if (isInfoShown) 
	            {
//	            	image.setVisibility(View.VISIBLE);
//	            	llParentInfo.setVisibility(View.GONE);
	            	
	            	scInfo.setVisibility(View.GONE);
//	            	tv.setVisibility(View.GONE);
	            	iv.setVisibility(View.VISIBLE);
//		            llInfo.setVisibility(View.GONE);
//	            	
	                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
	            } 
	            else 
	            {
//	            	image.setVisibility(View.GONE);
//	            	llParentInfo.setVisibility(View.VISIBLE);	
	            	
	            	scInfo.setVisibility(View.VISIBLE);
//	            	tv.setVisibility(View.VISIBLE);
	            	iv.setVisibility(View.GONE);
//		            llInfo.setVisibility(View.VISIBLE);
	                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
	            }

	            rotation.setDuration(500);
	            rotation.setFillAfter(true);
	            rotation.setInterpolator(new DecelerateInterpolator());
	            rotation.setAnimationListener(new AnimationListener() 
	            {
					@Override
					public void onAnimationStart(Animation animation) 
					{	}
					
					@Override
					public void onAnimationRepeat(Animation animation)
					{	}
					
					@Override
					public void onAnimationEnd(Animation animation) 
					{
						
					}
				});
	            image.startAnimation(rotation);
	            	
	        }
	    }
}
