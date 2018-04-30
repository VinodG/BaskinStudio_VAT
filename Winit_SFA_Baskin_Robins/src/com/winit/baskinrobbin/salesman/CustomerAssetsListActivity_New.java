package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.CustomerAssetsAdapter_New;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetDA_New;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;

public class CustomerAssetsListActivity_New extends BaseActivity implements OnClickListener
{

	private LinearLayout llAssetsList,llAssetBottom;
	private ListView lvAssets;
	private TextView tvNoAssets;
	private Button btnAssetScan,btnAssetSubmit,btnAssetRequest;
	private Vector<AssetDo_New> vecAssets;
	private CustomerAssetsAdapter_New adapter;
	private JourneyPlanDO journeyPlanDO;
	private String site = "1111";
	private String siteName = "";
	@Override
	public void initialize() 
	{
		llAssetsList			=	(LinearLayout)inflater.inflate(R.layout.assets_list_new,null);
		llBody.addView(llAssetsList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		if(getIntent().hasExtra("mallsDetails"))
		{
			journeyPlanDO = (JourneyPlanDO) getIntent().getSerializableExtra("mallsDetails");
			site = journeyPlanDO.site;
			siteName = journeyPlanDO.siteName;
		}
		
		btnAssetScan.setOnClickListener(this);
		btnAssetSubmit.setOnClickListener(this);
		btnAssetRequest.setOnClickListener(this);
		
		vecAssets = new Vector<AssetDo_New>();
		adapter = new CustomerAssetsAdapter_New(this, vecAssets,siteName);
		lvAssets.setAdapter(adapter);
		lvAssets.setDividerHeight(0);
		
		setTypeFace(llAssetsList);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		vecAssets = getCustomerAssets(site);
		if(vecAssets != null && vecAssets.size() > 0)
		{
			lvAssets.setVisibility(View.VISIBLE);
			llAssetBottom.setVisibility(View.VISIBLE);
			tvNoAssets.setVisibility(View.GONE);
		}
		if(adapter != null)
		{
			adapter.refresh(vecAssets);
		}
		
	}
	
	private void initializeControls()
	{
		lvAssets   	= (ListView)llAssetsList.findViewById(R.id.lvAssets);
		tvNoAssets 	= (TextView)llAssetsList.findViewById(R.id.tvNoAssets);
		llAssetBottom   	= (LinearLayout)llAssetsList.findViewById(R.id.llBottom);
		btnAssetScan 	= (Button)llAssetsList.findViewById(R.id.btnAssetScan);
		btnAssetSubmit 	= (Button)llAssetsList.findViewById(R.id.btnAssetSubmit);
		btnAssetRequest = (Button)llAssetsList.findViewById(R.id.btnAssetRequest);
		
		lvAssets.setCacheColorHint(0);
		lvAssets.setFadingEdgeLength(0);
		lvAssets.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btnAssetScan:
//				performScan();
//				checkAsset(new ScanResultObject());
				break;
			case R.id.btnAssetSubmit:
//				performSubmit();
				break;
			case R.id.btnAssetRequest:
				performAddRequest();
				break;
		}
	}
	
	private void performAddRequest() 
	{
		Intent intent	=	new Intent(CustomerAssetsListActivity_New.this,AssetRequestActivity_New.class);
		intent.putExtra("SiteNo", journeyPlanDO.site);
		startActivity(intent);
		
		
	}

	private Vector<AssetDo_New> getCustomerAssets(String site) 
	{
		Vector<AssetDo_New> vec = new Vector<AssetDo_New>();
		if(journeyPlanDO != null)
			vec = new AssetDA_New().getAllAssetsByCustomer(preference.getStringFromPreference(Preference.EMP_NO, ""),site);
		return vec;
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
			finish();
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		AppConstants.assetbarcodeimagePath = null;
		AppConstants.assettempimagePath = null;
	}

}
