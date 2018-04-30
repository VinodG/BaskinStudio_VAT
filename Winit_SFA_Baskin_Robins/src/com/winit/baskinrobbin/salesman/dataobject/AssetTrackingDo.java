package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;


@SuppressWarnings("serial")
public class AssetTrackingDo extends BaseComparableDO
{
	public String assetTrackingId="";
	public String userCode="";
	public String siteNo = "";
	public String visitedCode = "";
	public String journeyCode = "";
	public String date = "";
	public String imagepath = "";
	public String temperature = "";
	public String tempimagepath = "";
	public Vector<AssetTrackingDetailDo> vAssetTrackingDetailDos;
//	public String modifiedDate = "";
//	public String modifiedTime = "";
	public String isUploaded = "";
	public String status = "";
	public String assetTrackingAppId="";
}
