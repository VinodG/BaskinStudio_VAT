package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Item implements Serializable
{
	public String Item_Number = "";
	public String Item_Description ="" ;
	
	public String cases ="" ;
	public String units ="";
	
	public String precases = "";
	public String preunits = "";
	
	public String Selling_Price =""; 
	
	public String Category ="";
	
	public String productCode;
	public String productDesc;
	public String productId;
}
