package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.BarcodeScanInfoDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;

public class AssetScanListActivity extends BaseActivity
{
	//declaration of variables
	private LinearLayout llAssetScanList;
	private ListView lvAssetscanList;
	private ImageView ivbar_code;
	private AssetScanAdapter assetscanadapter;
	private TextView tvAssetScanItemName, tvAssetScanStoreName, tvAssetScanDate, tvNoOrderFound, tvItemListHeader;
	private Button btnSubmited;
	private Vector<ScanResultObject> vecScanResultList;
	private BarcodeScanInfoDA barcodeScanInfoBL;
	
	@Override
	public void initialize() 
	{
		//inflate the asset-scan-list layout
		llAssetScanList = (LinearLayout)inflater.inflate(R.layout.assetscanlist,null);
		intialiseControls();
		llBody.addView(llAssetScanList,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		//initializing the Objects 
		barcodeScanInfoBL = new BarcodeScanInfoDA();
		vecScanResultList = new Vector<ScanResultObject>();
		
		showLoader(getResources().getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				vecScanResultList = barcodeScanInfoBL.getBarcodeScanList(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID,""));
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						assetscanadapter = new AssetScanAdapter(vecScanResultList);
						lvAssetscanList.setAdapter(assetscanadapter);
						hideLoader();
					}
				});
			}
		}).start();
		
		btnSubmited.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(vecScanResultList != null && vecScanResultList.size() > 0)
					showCustomDialog(AssetScanListActivity.this, getResources().getString(R.string.successful), "Scanned items submitted successfully.", getResources().getString(R.string.OK), null, "finish");
				else
					showCustomDialog(AssetScanListActivity.this, getResources().getString(R.string.warning), "There is no item in list to submit.", getResources().getString(R.string.OK), null, "");
			}
		});
		
		setTypeFace(llAssetScanList);
	}
	
	public void intialiseControls()
	{
		//getting all id's of the controls 
		lvAssetscanList  =	(ListView)llAssetScanList.findViewById(R.id.lvAssetscanList);
		tvNoOrderFound	 =	(TextView)llAssetScanList.findViewById(R.id.tvNoOrderFound);
		tvItemListHeader =	(TextView)llAssetScanList.findViewById(R.id.tvSelectedItemListHeader);
		btnSubmited		 =	(Button)llAssetScanList.findViewById(R.id.btnSubmited);
		/*
		tvNoOrderFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemListHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSubmited.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		lvAssetscanList.setCacheColorHint(0);
		lvAssetscanList.setScrollbarFadingEnabled(true);
		lvAssetscanList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
	}
	private class AssetScanAdapter extends BaseAdapter 
	{
		Vector<ScanResultObject> vecScanItemList;
		public AssetScanAdapter(Vector<ScanResultObject> vecScanItemList)
		{
			this.vecScanItemList = vecScanItemList;
			
			if(this.vecScanItemList != null && this.vecScanItemList.size() > 0)
				tvNoOrderFound.setVisibility(View.GONE);
			else
				tvNoOrderFound.setVisibility(View.VISIBLE);
		}
		@Override
		public int getCount() 
		{
			return vecScanItemList.size();
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
			if(convertView==null)
				convertView			=	(LinearLayout)getLayoutInflater().inflate(R.layout.assetscanlist_cell,null);
			
			ScanResultObject obj	=	 vecScanItemList.get(position);
			ivbar_code				=	(ImageView)convertView.findViewById(R.id.ivbar_code);
			tvAssetScanItemName		=	(TextView)convertView.findViewById(R.id.tvAssetScanItemName);
			tvAssetScanStoreName	=	(TextView)convertView.findViewById(R.id.tvAssetScanStoreName);
			tvAssetScanDate			=	(TextView)convertView.findViewById(R.id.tvAssetScanDate);
			
			tvAssetScanItemName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvAssetScanStoreName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvAssetScanDate.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			if(obj.barcodeImage!=null)
				ivbar_code.setImageBitmap(BitmapFactory.decodeByteArray(obj.barcodeImage , 0,obj.barcodeImage.length));
			
			tvAssetScanItemName.setText(""+obj.type);
			tvAssetScanStoreName.setText(""+obj.location);
			tvAssetScanDate.setText(""+obj.time);
			return convertView;
		}
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("finish"))
		{
			finish();
		}
	}
}
