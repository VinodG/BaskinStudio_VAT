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

import com.winit.baskinrobbin.salesman.AssetDetailActivity_New;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;

public class CustomerAssetsAdapter_New extends BaseAdapter
{
	Vector<AssetDo_New> vecAssets;
	Vector<AssetDo_New> vecSelectedItems = new Vector<AssetDo_New>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	private String siteName = "";
	public CustomerAssetsAdapter_New(Context context , Vector<AssetDo_New> vecAssets,String siteName) 
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
		final AssetDo_New objiItem = vecAssets.get(position);
		convertView			   	   = (LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.assets_list_cell_new, null);
		
		TextView tvAssetName = (TextView)convertView.findViewById(R.id.tvAssetName);
		
		tvAssetName.setText(objiItem.assetName);
		
		convertView.setTag(objiItem);
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context, AssetDetailActivity_New.class);
				intent.putExtra("asset", (AssetDo_New)v.getTag());
				intent.putExtra("siteName", siteName);
				context.startActivity(intent);
			}
		});
		
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	public Vector<AssetDo_New> getSelectedItems()
	{
		return vecSelectedItems;
	}
	public void refresh(Vector<AssetDo_New> vecTemp) 
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
