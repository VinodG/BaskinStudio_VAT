package com.winit.baskinrobbin.salesman.dataaccesslayer;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScanResultObject implements Serializable
{
	public int rowId;
	public String type;
	public String location;
	public String time;
	public byte[] barcodeImage;
	public String comments;
	public String productId;
	public String EmpId;
	public String customerSiteId = "";
	public String Quantity = "20";
	public String strBarcodeImage = "";
	public String couponNo = "";
	public String barcodeId = "";
}
