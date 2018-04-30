package com.winit.baskinrobbin.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;

public class ReplacementPreviewAdapter extends BaseAdapter
{
	private Context context;
	private Vector<ProductDO> vecOrderPreview =  new Vector<ProductDO>();
	
	@SuppressWarnings("unchecked")
	public ReplacementPreviewAdapter(Context context, Vector<ProductDO> vecOrderedProduct)
	{
		this.context 		 = context;
		
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderedProduct.clone();
		else
			this.vecOrderPreview = null;
	}

	@Override
	public int getCount() 
	{
		if(vecOrderPreview != null && vecOrderPreview.size() > 0)
			return vecOrderPreview.size();
		
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		//getting current object form the Vector
		final ProductDO objItem = vecOrderPreview.get(position);
		
		//inflating the preview_order_list_cell layout for list Cell
		if(convertView == null)
			convertView = LayoutInflater.from(context).inflate(R.layout.replace_order_preview_list_cell, null);
		
		//getting Id's
		TextView tvOrderedItemName 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemName);
		TextView tvOrderedItemDesc 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemDesc);
		TextView tvIssueItemcode 	 = (TextView)convertView.findViewById(R.id.tvIssueItemcode);
		TextView tvIssueItemcodeDesc = (TextView)convertView.findViewById(R.id.tvIssueItemcodeDesc);
		EditText etQuantity 		 = (EditText)convertView.findViewById(R.id.etOrderedQuantity);
		EditText etCases 	 		 = (EditText)convertView.findViewById(R.id.etOrderedCases);
		
		//setting texts
		tvOrderedItemName.setText(objItem.SKU);
		tvOrderedItemDesc.setText(objItem.Description);
		
		tvIssueItemcode.setText(objItem.RelatedLineId);
		tvIssueItemcodeDesc.setText(objItem.Description1);
		
		if(objItem.preUnits != null && !objItem.preUnits.equalsIgnoreCase(""))
			etQuantity.setText(objItem.preUnits);
		else
			etQuantity.setText("0");
		
		etCases.setText(objItem.UOM);
		
		((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	
	/**
	 * method to refresh the List View
	 * @param vecOrderPreview
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Vector<ProductDO> vecOrderPreview)
	{
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderPreview.clone();
		else
			this.vecOrderPreview = null;
		
		this.notifyDataSetChanged();
	}
}
