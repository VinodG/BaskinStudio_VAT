package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

@SuppressWarnings("serial")
public class CategoryDO extends BaseObject
{
	public String categoryId   = "";
	public String categoryName = "";
	public String childCount   = "";
	public String categoryIcon = "";
	public Vector<ProductDO> vecProducts = null ;
}
