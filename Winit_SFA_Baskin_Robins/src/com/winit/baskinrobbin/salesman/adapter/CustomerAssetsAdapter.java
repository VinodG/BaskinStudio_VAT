package com.winit.baskinrobbin.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.CustomerAssetsDetailsActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;

public class CustomerAssetsAdapter extends BaseAdapter
{
	Vector<AssetDO> vecAssets;
	Vector<AssetDO> vecSelectedItems = new Vector<AssetDO>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	private String siteName = "";
	public CustomerAssetsAdapter(Context context , Vector<AssetDO> vecAssets,String siteName) 
	{
		this.vecAssets = vecAssets;
		this.context= context;
		this.siteName = siteName ; 
	}
	@Override
	public int getCount() 
	{
		if(vecAssets != null && vecAssets.size() > 0)
			return vecAssets.size();
		return 0;
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
		final AssetDO objiItem = vecAssets.get(position);
		convertView			   	   = (LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.assets_list_cell, null);
		
		TextView tvItemDescription = (TextView)convertView.findViewById(R.id.tvItemDescription);
		TextView tvItemCode 	   = (TextView)convertView.findViewById(R.id.tvItemCode);
		final ImageView cbList1    = (ImageView)convertView.findViewById(R.id.cbList1);
		final ImageView iv_freezer    = (ImageView)convertView.findViewById(R.id.iv_freezer);
		
		cbList1.setId(position);
		tvItemCode.setText(objiItem.name);
		tvItemDescription.setText(objiItem.assetType);
		if(vecSelectedItems.contains(objiItem))
			cbList1.setImageResource(R.drawable.visible);
		else
			cbList1.setImageResource(R.drawable.lite);
		
		if(objiItem.assetType.equalsIgnoreCase("Freezer"))
		{
			iv_freezer.setBackgroundResource(R.drawable.freez);
		}
		else if(objiItem.assetType.equalsIgnoreCase("Chiller"))
		{
			iv_freezer.setBackgroundResource(R.drawable.chiller);
		}
		
		convertView.setTag(objiItem);
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context, CustomerAssetsDetailsActivity.class);
				intent.putExtra("asset", (AssetDO)v.getTag());
				intent.putExtra("siteName", siteName);
				context.startActivity(intent);
			}
		});
		
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	public Vector<AssetDO> getSelectedItems()
	{
		return vecSelectedItems;
	}
	public void refresh(Vector<AssetDO> vecTemp) 
	{
		this.vecAssets = vecTemp;
		notifyDataSetChanged();
	}
	
	public void setCheckedAsset(int index)
	{
		vecSelectedItems.add(vecAssets.get(index));
		notifyDataSetChanged();
	}
}
