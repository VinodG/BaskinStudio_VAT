package com.winit.baskinrobbin.salesman;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;

public class AssetDetailActivity_New extends BaseActivity
{
	private LinearLayout llAssetRequest;
	private TextView  tvAssetName,tvSelectAssetsLevel1,tvSelectAssetsLevel2,tvSelectAssetsLevel3,tvSelectAssetsLevel4,tvSelectAssetsLevel5;
	private String strReqSiteNumber;
	private Button btnSubmit;
	private AssetDo_New assetdo;
	@Override
	public void initialize() 
	{
		
		llAssetRequest		=	(LinearLayout)inflater.inflate(R.layout.assets_detail_new, null);
		
		llBody.addView(llAssetRequest,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		intialiseControls();
//		bindControls();
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		setTypeFace(llAssetRequest);
		if(getIntent().hasExtra("SiteNo"))
		{
			strReqSiteNumber =  getIntent().getStringExtra("SiteNo");
		}
		if(getIntent().hasExtra("asset"))
			assetdo = (AssetDo_New) getIntent().getSerializableExtra("asset");
		
		if(assetdo != null)
		{
			tvAssetName.setText(assetdo.assetName);
			tvSelectAssetsLevel1.setText(assetdo.assetCatLevel1);
			tvSelectAssetsLevel2.setText(assetdo.assetCatLevel2);
			tvSelectAssetsLevel3.setText(assetdo.assetCatLevel3);
			tvSelectAssetsLevel4.setText(assetdo.assetCatLevel4);
			tvSelectAssetsLevel5.setText(assetdo.assetCatLevel5);
			
			
		}
		
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
	}
	
	
	private void intialiseControls() 
	{
		tvSelectAssetsLevel1		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel1);
		tvSelectAssetsLevel2		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel2);
		tvSelectAssetsLevel3		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel3);
		tvSelectAssetsLevel4		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel4);
		tvSelectAssetsLevel5		=	(TextView)llAssetRequest.findViewById(R.id.tvSelectAssetsLevel5);
		tvAssetName					=   (TextView)llAssetRequest.findViewById(R.id.tvAssetName);
		
		
		btnSubmit			=   (Button)llAssetRequest.findViewById(R.id.btnSubmit);
	}
	
	
}
