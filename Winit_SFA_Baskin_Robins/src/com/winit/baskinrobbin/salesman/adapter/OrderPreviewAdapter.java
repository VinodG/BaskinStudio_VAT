package com.winit.baskinrobbin.salesman.adapter;

import java.text.DecimalFormat;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;


public class OrderPreviewAdapter extends BaseAdapter
{
	private Context context;
	private Vector<ProductDO> vecOrderPreview =  new Vector<ProductDO>();
	private boolean isNormalOrder;
	private boolean isReturnPreview;
	private DecimalFormat diffPreview;
	private boolean isAdvanceOrder = false;
	private String channelCode = "";
	
	@SuppressWarnings("unchecked")
	public OrderPreviewAdapter(Context context, Vector<ProductDO> vecOrderedProduct, boolean isJar, boolean isNormalOrder, String channelCode)
	{
		this.context 		 = context;
		
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderedProduct.clone();
		else
			this.vecOrderPreview = null;
		
		this.isNormalOrder 	= isNormalOrder;
		this.channelCode = channelCode;
	}
	
	@SuppressWarnings("unchecked")
	public OrderPreviewAdapter(Context context, Vector<ProductDO> vecOrderedProduct, boolean isJar, boolean isNormalOrder, boolean isReturnPreview, String channelCode)
	{
		this.context 		 = context;
		
		if(vecOrderPreview != null)
			this.vecOrderPreview = (Vector<ProductDO>) vecOrderedProduct.clone();
		else
			this.vecOrderPreview = null;
		
		this.isNormalOrder 	 = isNormalOrder;
		this.isReturnPreview = isReturnPreview;
		this.channelCode = channelCode;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.preview_order_list_cell, null);
		
		diffPreview = new DecimalFormat("##.##");
		diffPreview.setMinimumFractionDigits(0);
		diffPreview.setMaximumFractionDigits(2);
		
		//getting Id's
		TextView tvOrderedItemName 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemName);
		TextView tvOrderedItemDesc 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemDesc);
		EditText etQuantity 		 = (EditText)convertView.findViewById(R.id.etOrderedQuantity);
		EditText etCases 	 		 = (EditText)convertView.findViewById(R.id.etOrderedCases);
		
		EditText etPrice 	 		 = (EditText)convertView.findViewById(R.id.etPrice);
		EditText etTotalPrice 	 	 = (EditText)convertView.findViewById(R.id.etTotalPrice);
		EditText etTaxVal 	 	 	 = (EditText)convertView.findViewById(R.id.etTaxVal); //============VAT==============
		EditText etInvoiceAmount 	 = (EditText)convertView.findViewById(R.id.etInvoiceAmount);
		EditText etDiscount 	 	 = (EditText)convertView.findViewById(R.id.etDiscount);
		
		if(isReturnPreview)
		{
			etDiscount.setVisibility(View.VISIBLE);
			etTotalPrice.setVisibility(View.VISIBLE);
			etTaxVal.setVisibility(View.VISIBLE);
			etPrice.setVisibility(View.VISIBLE);
			etInvoiceAmount.setVisibility(View.VISIBLE);
		}
		
		if(channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			etPrice.setVisibility(View.GONE);
			etTotalPrice.setVisibility(View.GONE);
			etTaxVal.setVisibility(View.GONE);
			etInvoiceAmount.setVisibility(View.GONE);
			etDiscount.setVisibility(View.GONE);
			etQuantity.setLayoutParams(new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
		}
		
		//setting texts
		tvOrderedItemName.setText(objItem.SKU);
		
		if(objItem.preUnits != null && !objItem.preUnits.equalsIgnoreCase(""))
			etQuantity.setText(objItem.preUnits);
		else
			etQuantity.setText("0");
		
		etCases.setText(objItem.UOM);
		
		
		if(objItem.isPromotional || objItem.ItemSubType.equalsIgnoreCase("F"))
		{
			tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.star), null);
		}
		else
			tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		
		if(isNormalOrder || isReturnPreview)
		{
			etDiscount.setText(""+ diffPreview.format(objItem.DiscountAmt));
			etPrice.setText(""+diffPreview.format(objItem.itemPrice));
			if(isReturnPreview)
				etTaxVal.setText(""+diffPreview.format( (objItem.LineTaxAmount/StringUtils.getFloat(objItem.units) )* StringUtils.getFloat(objItem.preUnits)) );//==========Added For VAT
			else
				etTaxVal.setText(""+diffPreview.format(objItem.LineTaxAmount));  //==========Added For VAT
			etTotalPrice.setText(""+diffPreview.format(objItem.itemPrice*StringUtils.getFloat(objItem.preUnits)));
//			etInvoiceAmount.setText(""+diffPreview.format(objItem.invoiceAmount));
			etInvoiceAmount.setText(""+StringUtils.round(""+objItem.invoiceAmount,((BaseActivity)context).noOfRoundingOffdigits));
			tvOrderedItemDesc.setText(((""+objItem.Description)).trim());
		}
		else
		{
			etDiscount.setText(""+ diffPreview.format(objItem.DiscountAmt/StringUtils.getFloat(objItem.preUnits)));
			etPrice.setText(""+diffPreview.format(objItem.unitSellingPrice/StringUtils.getFloat(objItem.preUnits)));
			etTotalPrice.setText(""+diffPreview.format(objItem.unitSellingPrice));
			etTaxVal.setText(""+diffPreview.format(objItem.LineTaxAmount));  //==========Added For VAT
//			etInvoiceAmount.setText(""+diffPreview.format(objItem.invoiceAmount));
			etInvoiceAmount.setText(""+StringUtils.round(""+objItem.invoiceAmount,((BaseActivity)context).noOfRoundingOffdigits));
			tvOrderedItemDesc.setText(((""+objItem.Description)).trim());
		}
		
		if(isAdvanceOrder)
			etDiscount.setText(""+ objItem.Discount);
		
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
