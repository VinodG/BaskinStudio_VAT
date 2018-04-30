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

import com.google.zxing.client.android.CaptureActivity;
import com.winit.baskinrobbin.parsers.AssetStatusParser;
import com.winit.baskinrobbin.salesman.adapter.CustomerAssetsAdapter;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetCustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetTrackingDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetTrackingDetailDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDetailDo;
import com.winit.baskinrobbin.salesman.dataobject.AssetTrackingDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class CustomerAssetsListActivity extends BaseActivity implements OnClickListener
{

	private LinearLayout llAssetsList,llAssetBottom;
	private ListView lvAssets;
	private TextView tvNoAssets;
	private Button btnAssetScan,btnAssetSubmit,btnAssetRequest;
	private Vector<AssetDO> vecAssets;
	private CustomerAssetsAdapter adapter;
	private JourneyPlanDO journeyPlanDO;
	private String site = "1111";
	private String siteName = "";
	@Override
	public void initialize() 
	{
		llAssetsList			=	(LinearLayout)inflater.inflate(R.layout.assets_list,null);
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
		
		vecAssets = getCustomerAssets(site);
		if(vecAssets != null && vecAssets.size() > 0)
		{
			lvAssets.setVisibility(View.VISIBLE);
			llAssetBottom.setVisibility(View.VISIBLE);
			tvNoAssets.setVisibility(View.GONE);
		}
		
		adapter = new CustomerAssetsAdapter(this, vecAssets,siteName);
		lvAssets.setAdapter(adapter);
		lvAssets.setDividerHeight(0);
		
		setTypeFace(llAssetsList);
	}
	
	private void initializeControls()
	{
		lvAssets   	= (ListView)llAssetsList.findViewById(R.id.lvAssets);
		tvNoAssets 	= (TextView)llAssetsList.findViewById(R.id.tvNoAssets);
		llAssetBottom   	= (LinearLayout)llAssetsList.findViewById(R.id.llBottom);
		btnAssetScan 	= (Button)llAssetsList.findViewById(R.id.btnAssetScan);
		btnAssetSubmit 	= (Button)llAssetsList.findViewById(R.id.btnAssetSubmit);
		btnAssetRequest = (Button)llAssetsList.findViewById(R.id.btnAssetRequest);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btnAssetScan:
				performScan();
//				checkAsset(new ScanResultObject());
				break;
			case R.id.btnAssetSubmit:
				performSubmit();
				break;
			case R.id.btnAssetRequest:
				performAddRequest();
				break;
		}
	}
	
	private void performAddRequest() 
	{
		Intent intent	=	new Intent(CustomerAssetsListActivity.this,AssetRequestActivity_New.class);
		intent.putExtra("SiteNo", journeyPlanDO.site);
		startActivity(intent);
		
		
	}
	private void performScan()
	{
		AppConstants.objScanResultObject = null;
		Intent intent	=	new Intent(CustomerAssetsListActivity.this,CaptureActivity.class);
		startActivityForResult(intent, AppStatus.REQUEST_CODE);
	}

	private void performSubmit()
	{
		AppConstants.assetbarcodeimagePath = null;
		AppConstants.assettempimagePath = null;
		final ConnectionHelper connectionHelper = new ConnectionHelper(this);
		
		final Vector<AssetTrackingDo> assetTrackingDos = new AssetTrackingDA().getAllAssetTrackings();
		
		
		if(assetTrackingDos != null && assetTrackingDos.size() > 0)
		{
			showLoader(getString(R.string.loading));
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					final AssetStatusParser assetStatusParser 	= new AssetStatusParser(CustomerAssetsListActivity.this);
					connectionHelper.sendRequest(CustomerAssetsListActivity.this,BuildXMLRequest.postAsset(assetTrackingDos), assetStatusParser, ServiceURLs.PostAsset, preference);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() 
						{
							hideLoader();
							showCustomDialog(CustomerAssetsListActivity.this, "Successful!", "Asset Scan has been excecuted successfully.", getString(R.string.OK), null, "success");
						}
					});
				}
			}).start();
		}
		else
		{
			showCustomDialog(CustomerAssetsListActivity.this, "Alert !", "There are no scanned assets.", "OK", null, "");
		}
		
	}
	
	private Vector<AssetDO> getCustomerAssets(String site) 
	{
		Vector<AssetDO> vec = new Vector<AssetDO>();
		if(journeyPlanDO != null)
			vec = new AssetCustomerDA().getAllAssetsByCustomer(site);
		return vec;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(AppConstants.objScanResultObject!=null)
			checkAsset(AppConstants.objScanResultObject);
		else
			showCustomDialog(CustomerAssetsListActivity.this, "Alert !", "Unable to scan the barcode, Please try again.", "OK", null, "");
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void checkAsset(ScanResultObject objScanResultObject)
	{
		boolean isexist = false;
		if(vecAssets != null && vecAssets.size() > 0)
		{
//			objScanResultObject.barcodeId = vecAssets.get(0).barCode;
//			objScanResultObject.time = CalendarUtils.getCurrentDateTime();
			
			for (AssetDO assetDO : vecAssets) 
			{
				if(assetDO.barCode.equalsIgnoreCase(objScanResultObject.barcodeId))
				{
					isexist = true;
					int index = vecAssets.indexOf(assetDO);
					adapter.setCheckedAsset(index);
					
					AssetTrackingDo assetTrackingDo = new AssetTrackingDo();
					AssetTrackingDetailDo assetTrackingDetailDo = new AssetTrackingDetailDo();
					
					SurveyCustomerDeatislDO	cusdetailDo = new SurveyCustomerDeatislDO();
					cusdetailDo = new CustomerDA().getCustomerSurveyDetails(preference.getStringFromPreference(preference.EMP_NO, ""));
					
					if(cusdetailDo.visitCode.contains("-"))
						cusdetailDo.visitCode = cusdetailDo.visitCode.replace("-", "");
					if(cusdetailDo.visitCode.contains("T"))
						cusdetailDo.visitCode = cusdetailDo.visitCode.replace("T", "");
					if(cusdetailDo.visitCode.contains(":"))
						cusdetailDo.visitCode = cusdetailDo.visitCode.replace(":", "");
					assetTrackingDo = new AssetTrackingDA().getAssetTracking(assetDO.siteNo,CalendarUtils.getCurrentDateAsString());
					
					if(assetTrackingDo == null)
					{
						assetTrackingDo = new AssetTrackingDo();
						assetTrackingDo.assetTrackingId = StringUtils.getUniqueUUID();
						assetTrackingDo.siteNo = assetDO.siteNo;
						assetTrackingDo.userCode = preference.getStringFromPreference(Preference.EMP_NO, "");
						assetTrackingDo.date = CalendarUtils.getCurrentDateTime();
						assetTrackingDo.journeyCode = CalendarUtils.getCurrentDateAsString();
						assetTrackingDo.visitedCode = cusdetailDo.visitCode;
						assetTrackingDo.isUploaded = "0";
						assetTrackingDo.status = "0";
						new AssetTrackingDA().insertAssetTracking(assetTrackingDo);
					}
					else
					{
						assetTrackingDo.isUploaded = "0";
						new AssetTrackingDA().updateTrackingIsUpload(assetTrackingDo.assetTrackingId, assetTrackingDo.isUploaded);
					}
					
					assetTrackingDetailDo.assetTrackingDetailId = StringUtils.getUniqueUUID();
					assetTrackingDetailDo.barCode = assetDO.barCode;
					assetTrackingDetailDo.assetId = assetDO.assetId;
					assetTrackingDetailDo.scanningTime = CalendarUtils.getCurrentDateTime();
					assetTrackingDetailDo.assetTrackingId = assetTrackingDo.assetTrackingId;
					assetTrackingDetailDo.isUploaded = "0";
					assetTrackingDetailDo.status = "0";
					assetTrackingDetailDo.imagepath = assetDO.imagePath;
					
					new AssetTrackingDetailDA().insertAssetTrackingDetail(assetTrackingDetailDo);
					
				}
			}
		}
		
		if(!isexist)
		{
			showCustomDialog(CustomerAssetsListActivity.this, "Alert !", "There is no Freezer or Chiller exists with this barcode.", "OK", null, "");
		}
		
		
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
