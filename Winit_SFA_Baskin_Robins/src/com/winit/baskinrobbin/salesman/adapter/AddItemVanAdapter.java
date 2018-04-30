package com.winit.baskinrobbin.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;

public class AddItemVanAdapter extends BaseAdapter
{
	Vector<VanLoadDO> vecSearchedItems;
	Vector<VanLoadDO> vecSelectedItems = new Vector<VanLoadDO>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	public AddItemVanAdapter(Vector<VanLoadDO> vecSearchedItems,Context context) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
	}
	@Override
	public int getCount() 
	{
		return vecSearchedItems.size();
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

	public void selectAll(ImageView ivSelectAll)
	{
		this.ivSelectAll = ivSelectAll;
		vecSelectedItems.clear();
		if(!isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.check_hover);
			vecSelectedItems = (Vector<VanLoadDO>) vecSearchedItems.clone();
			isAllSelected = true;
		}
		else if(isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.check_normal);
			isAllSelected = false;
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final VanLoadDO objiItem   = vecSearchedItems.get(position);
		if(convertView == null)
			convertView			   = (LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.result_cell, null);
		
		TextView tvItemDescription = (TextView)convertView.findViewById(R.id.tvItemDescription);
		TextView tvItemCode 	   = (TextView)convertView.findViewById(R.id.tvItemCode);
		final ImageView cbList1    = (ImageView)convertView.findViewById(R.id.cbList1);
		
		tvItemCode.setText(objiItem.ItemCode);
		tvItemDescription.setText(objiItem.Description);
		tvItemDescription.setTextColor(context.getResources().getColor(R.color.list_header));
		tvItemCode.setTextColor(context.getResources().getColor(R.color.list_header));
		if(vecSelectedItems.contains(objiItem))
		{
			cbList1.setImageResource(R.drawable.check_hover);
			cbList1.setTag("1");
		}
		else
		{
			cbList1.setImageResource(R.drawable.check_normal);
			cbList1.setTag("0");
		}
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(ivSelectAll!=null)
				{
					ivSelectAll.setImageResource(R.drawable.check_normal);
					isAllSelected = false;
				}
					
				if(cbList1.getTag().toString().equalsIgnoreCase("0"))
				{
					vecSelectedItems.add(objiItem);
					cbList1.setImageResource(R.drawable.check_hover);
					cbList1.setTag("1");
				}
				else
				{
					vecSelectedItems.remove(objiItem);
					cbList1.setImageResource(R.drawable.check_normal);
					cbList1.setTag("0");
				}
			}
		});
		
		((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	
	public Vector<VanLoadDO> getSelectedItems()
	{
		return vecSelectedItems;
	}
	
	public void refresh(Vector<VanLoadDO> vecTemp) 
	{
		this.vecSearchedItems = vecTemp;
		notifyDataSetChanged();
	}
}
