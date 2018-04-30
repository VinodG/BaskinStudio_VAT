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

public class OrderPreviewAdapter_FreeDelivery extends BaseAdapter
{
	private Context context;
	private Vector<ProductDO> vecOrderPreview =  new Vector<ProductDO>();
	private boolean isJar;
	
	@SuppressWarnings("unchecked")
	public OrderPreviewAdapter_FreeDelivery(Context context, Vector<ProductDO> vecOrderedProduct)
	{
		this.context 		 = context;
		
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderedProduct.clone();
		else
			this.vecOrderPreview = null;
	}

	@SuppressWarnings("unchecked")
	public OrderPreviewAdapter_FreeDelivery(Context context, Vector<ProductDO> vecOrderedProduct, boolean isJar)
	{
		this.context 		 = context;
		
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderedProduct.clone();
		else
			this.vecOrderPreview = null;
		this.isJar = isJar;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.preview_order_list_cell_freedelivery, null);
		
		//getting Id's
		TextView tvOrderedItemName 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemName);
		TextView tvOrderedItemDesc 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemDesc);
		EditText etQuantity 		 = (EditText)convertView.findViewById(R.id.etOrderedQuantity);
		EditText etCases 	 		 = (EditText)convertView.findViewById(R.id.etOrderedCases);
		
		EditText etPrice 	 		 = (EditText)convertView.findViewById(R.id.etPrice);
		EditText etTotalPrice 	 	 = (EditText)convertView.findViewById(R.id.etTotalPrice);
		EditText etInvoiceAmount 	 = (EditText)convertView.findViewById(R.id.etInvoiceAmount);
		EditText etDiscount 	 	 = (EditText)convertView.findViewById(R.id.etDiscount);
		
		if(isJar)
		{
			etQuantity.setVisibility(View.GONE);
			etDiscount.setVisibility(View.GONE);
		}
		etCases.setVisibility(View.VISIBLE);
		etTotalPrice.setVisibility(View.GONE);
		
		/*//setting type-faces here
		tvOrderedItemName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etQuantity.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOrderedItemDesc.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
		etPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etTotalPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etInvoiceAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etDiscount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		//setting texts
		tvOrderedItemName.setText(objItem.SKU);
		
		if(objItem.preUnits != null && !objItem.preUnits.equalsIgnoreCase(""))
			etQuantity.setText(objItem.preUnits);
		else
			etQuantity.setText("0");
		
		if(objItem.UOM != null && !objItem.UOM.equalsIgnoreCase(""))
			etCases.setText(objItem.UOM);
		else
			etCases.setText("0");
		
		etDiscount.setText(""+objItem.Discount);
		etPrice.setText(""+objItem.itemPrice);
		etTotalPrice.setText(""+objItem.totalPrice);
		etInvoiceAmount.setText(""+objItem.invoiceAmount);
		tvOrderedItemDesc.setText(((""+objItem.Description)).trim());
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
