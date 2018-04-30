package com.winit.baskinrobbin.salesman;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.CoverFlowAdapter;
import com.winit.baskirobin.coverflow.CoverFlow;
import com.winit.baskirobin.salesman.viewpager.extensions.PagerSlidingTabStrip;

public class ProductCatalogActivity extends BaseActivity{

	private LinearLayout llProductCatalog;
	private CoverFlow coverFlow;
	private CoverFlowAdapter coverFlowAdapter;
	private String[] TITLES = {"Frozen Products", "Packing Items", "Ready Packs", "Rotator"};
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private TextView tvLu, tvOrderAttTitle, tvOrderAttValue, tvCallUsTitle, tvCallUsValue;
	private TextView tvItemName;
	
	
	private int[] imagesfrozen = {R.drawable.frozen1,R.drawable.frozen2,R.drawable.frozen3,R.drawable.frozen4,R.drawable.frozen5,R.drawable.frozen6,R.drawable.frozen7,R.drawable.frozen8,R.drawable.frozen9,R.drawable.frozen10};
	private String[] stringFrojen = {"Assorted Tubs","Jamoca Tub","CG Cotton Candy RP","CG Chocolate RP","CG Choc Mousse Roy RP","CG Jamoca Alm Fudge RP","CG Mint Chocolate Chip RP","CG Pralines N Cream RP","CG Vanilla RP","CG World Class Choco RP"};
	
	private int[] imagesPacket = {R.drawable.packing1,R.drawable.packing2,R.drawable.packing3,R.drawable.packing4,R.drawable.packing5,R.drawable.packing6,R.drawable.packing7,R.drawable.packing8,R.drawable.packing9,R.drawable.packing10};
	private String[] stringPacket = {"6 inch Cake Box IBS","8 inch Cake Box IBS","9 inch Cake Box IBS","Cup.Paper-3-1/2 Oz Scoop","Cotton Candy 500 ML (8x1)","Choc Chip 1 Ltr (8x1)","Jamoca 500 ML (8x1)","Chocolate 500 ML (8x1)","Choc Mouss Royale 1 Ltr (8x1)","Cookies N Cream 1 Ltr (8x1)"};
	
	private int[] imagesReadyPack = {R.drawable.readypack1,R.drawable.readypack2,R.drawable.readypack3,R.drawable.readypack4,R.drawable.readypack5,R.drawable.readypack6,R.drawable.readypack7,R.drawable.readypack8,R.drawable.readypack9,R.drawable.readypack10};
	private String[] stringReadyPack = {"CG Cotton Candy RP","Choc Mousse Royale 500 ML (8x1)","CG Vanilla RP","CG World Class Choco RP","Pralines N Cream 2 Ltr (4x1)","Vanilla 2 Ltr (4x1)","Very Berry Strawberry 2 Ltr (4x1)","Chocolate Chip 1 Ltr (8x1)","Old Fashioned Buter Pecan 500 ML (8x1)","Hokey Pokey 500 ML (8x1)"};
	
	private int[] imagesRotator = {R.drawable.rotator1,R.drawable.rotator2,R.drawable.rotator3,R.drawable.rotator4,R.drawable.rotator5,R.drawable.rotator6,R.drawable.rotator7,R.drawable.rotator8,R.drawable.rotator9,R.drawable.rotator10};
	private String[] stringRotator = {"Cup.Paper -5Oz 1 Scoop Pink Sundae","Cup.Paper-3-1/2 Oz Scoop ","Pralines N Cream 500 ML (8x1)","Vanilla 120 ML (48x1)","Jamoca 500 ML (8x1)","Old Fashioned Buter Pecan 500 ML (8x1)","Chocolate 120 ML (48x1)","Verry Berry Strawberry 120 ML (48x1)","Choc Mousse Royale 500 ML (8x1)","Hokey Pokey 500 ML (8x1)"};
	private int[] commonImg;
	private String[] commonString;
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		
		llProductCatalog = (LinearLayout) inflater.inflate(R.layout.product_catlog, null);
		
		tabs = (PagerSlidingTabStrip)llProductCatalog.findViewById(R.id.tabs);
		tvLu = (TextView)llProductCatalog.findViewById(R.id.tvLu);
		llBody.addView(llProductCatalog, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		tvLu.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvOrderAttTitle	= (TextView)llProductCatalog.findViewById(R.id.tvOrderAttTitle);
		tvOrderAttValue	= (TextView)llProductCatalog.findViewById(R.id.tvOrderAttValue);
		tvCallUsTitle	= (TextView)llProductCatalog.findViewById(R.id.tvCallUsTitle);
		tvCallUsValue	= (TextView)llProductCatalog.findViewById(R.id.tvCallUsValue);
		
		tvItemName = (TextView)llProductCatalog.findViewById(R.id.tvItemDesc);
		
//		tvItemName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		
		/*tvOrderAttTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOrderAttValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCallUsTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCallUsValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		setTypeFace(llProductCatalog);
		
		pager = (ViewPager) llProductCatalog.findViewById(R.id.pager);
		adapter = new MyPagerAdapter();
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(TITLES.length-1);
	
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
	
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		
		pager.setEnabled(false);
		pager.setClickable(false);
		
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) 
			{
				tvItemName.setText(commonString[0]);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				
			}
		});
		
		pager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		tvOrderAttValue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://www.galadarigroup.com/albums/galadari-ice-cream-co-ltd-l-l-c/"));
				startActivity(i);
			}
		});
		
		tvCallUsValue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String uri = "tel:"+"8008595339";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
	}
	
	private class MyPagerAdapter extends PagerAdapter 
	{

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}
		
		

		@Override
		public Object instantiateItem(final View collection, final int position)
		{
            LinearLayout llFlightStatusContent = getProductCatlogView(position);
            ((ViewPager) collection).addView(llFlightStatusContent, position);
            return llFlightStatusContent;
		}

		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((LinearLayout) view);
        }



		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view == ((LinearLayout) object);
		}
	}
	private LinearLayout getProductCatlogView(int postion)
	{
		LinearLayout llCatLOgView = (LinearLayout) getLayoutInflater().inflate(R.layout.productcatalognew, null);
		
		coverFlow = (CoverFlow) llCatLOgView.findViewById(R.id.coverFlow);
		coverFlow.setSpacing(0);
		coverFlow.setSelection(0, true);
		coverFlow.setAnimationDuration(1000);
		
		if(postion == 0)
		{
			commonImg = imagesfrozen;
			commonString = stringFrojen;
		}
		else if(postion == 1)
		{
			commonImg = imagesPacket;
			commonString = stringPacket;
		}
		else if(postion == 2)
		{
			commonImg = imagesReadyPack;
			commonString = stringReadyPack;
		}
		else if(postion == 3)
		{
			commonImg = imagesRotator;
			commonString = stringRotator;
		}
			
		tvItemName.setText(commonString[0]);
		coverFlowAdapter = new CoverFlowAdapter(ProductCatalogActivity.this, commonImg,commonString);
		coverFlow.setAdapter(coverFlowAdapter);
		
		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				tvItemName.setText(commonString[arg2]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		return llCatLOgView;
	}
}
