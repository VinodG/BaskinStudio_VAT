package com.winit.baskinrobbin.salesman.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.CaptureDamagedItemImage;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.SalesManTakeReturnOrder.DatePickerListner;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class AddRecomendedItemAdapter extends ArrayAdapter<ProductDO>
{
	private Vector<ProductDO> vecSearchedItems;
	private Context context;
	private ImageView ivSelectAll;
	private boolean isReturn = false;
	private DatePickerListner datePicker;
	private LocationUtility locationUtility;
	private String lat, lang;
	
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context) 
	{
		super(context, 0, vecSearchedItems);
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
	}
	
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context, boolean isReturn) 
	{
		super(context, 0, vecSearchedItems);
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isReturn = isReturn;
	}
	
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context, boolean isReturn, String from, DatePickerListner datePicker) 
	{
		super(context, 0, vecSearchedItems);
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isReturn = isReturn;
		this.datePicker = datePicker;
		
		locationUtility  = new LocationUtility(context);
		locationUtility.getLocation(new LocationResult() {
			
			@Override
			public void gotLocation(Location loc)
			{
				if(loc!=null)
				{
					DecimalFormat df = new DecimalFormat("#.#####");
					lat	 = df.format(loc.getLatitude());
					lang = df.format(loc.getLongitude());
				}
			}
		});
	}
	
	@Override
	public int getCount() 
	{
		if(vecSearchedItems != null && vecSearchedItems.size() >0)
			return vecSearchedItems.size();
		else
			return 0;
	}

	@Override
	public ProductDO getItem(int position) 
	{
		return vecSearchedItems.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ProductDO objiItems   = 	vecSearchedItems.get(position);
		final ViewHolder viewHolder;
		
		if(convertView == null)
		{
			viewHolder 			   			= 	new ViewHolder();
			convertView			   			= 	(LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.result_cell_return_order_hidden_item, null);
			
			viewHolder.tvItemDescription   	= 	(TextView)convertView.findViewById(R.id.tvItemDescription);
			viewHolder.tvItemCode 	     	= 	(TextView)convertView.findViewById(R.id.tvItemCode);
			viewHolder.cbItem      		 	= 	(CheckBox)convertView.findViewById(R.id.cbItem);
			viewHolder.llReason  			= 	(LinearLayout)convertView.findViewById(R.id.llReason);
			
			viewHolder.llExpiryDate			= 	(LinearLayout)convertView.findViewById(R.id.llExpiryDate);
			viewHolder.llCaptureImage		= 	(LinearLayout)convertView.findViewById(R.id.llCaptureImage);
		
			viewHolder.tvExpiryDate			= 	(TextView)convertView.findViewById(R.id.tvExpiryDate);
			viewHolder.etRemark				= 	(EditText)convertView.findViewById(R.id.etRemark);	
			viewHolder.btnCaptureImages		= 	(Button)convertView.findViewById(R.id.btnCaptureImages);
		
			viewHolder.llLayout	 			= 	(LinearLayout)convertView.findViewById(R.id.llLayout);
			viewHolder.llLotNumber	 		= 	(LinearLayout)convertView.findViewById(R.id.llLotNumber);
			viewHolder.etLotNumber	 	 	= 	(EditText)convertView.findViewById(R.id.etLotNumber);
		
			viewHolder.rgSellsbleState 		= 	(RadioGroup) convertView.findViewById(R.id.rgSellsbleState);
		
			viewHolder.rgNonSellableOptions = 	(RadioGroup) convertView.findViewById(R.id.rgNonSellableOptions);
			viewHolder.rbExpired 			= 	(RadioButton) convertView.findViewById(R.id.rbExpired);
			viewHolder.rbDamaged 			= 	(RadioButton) convertView.findViewById(R.id.rbDamaged);
			viewHolder.rbSoonToExpired 		= 	(RadioButton) convertView.findViewById(R.id.rbSoonToExpired);
			viewHolder.rbGoodCondition 		= 	(RadioButton) convertView.findViewById(R.id.rbGoodCondition);
			viewHolder.rbSellable 			= 	(RadioButton) convertView.findViewById(R.id.rbSellable);
			viewHolder.rbNonSellable 		= 	(RadioButton) convertView.findViewById(R.id.rbNonSellable);
			
			convertView.setTag(viewHolder);
		}
		else
			viewHolder = (ViewHolder) convertView.getTag();
		
		viewHolder.productDO  =  objiItems;
		maintainState(viewHolder);
		
		viewHolder.tvItemCode.setText(viewHolder.productDO.SKU);
		viewHolder.tvItemDescription.setText(viewHolder.productDO.Description);
		if(isReturn){
			viewHolder.tvItemDescription.setText(viewHolder.productDO.Description+", "+"\nOrd No :"+viewHolder.productDO.OrderNo +"  Qty:"+viewHolder.productDO.QuantityInStock);
		}
		viewHolder.tvItemDescription.setTextColor(context.getResources().getColor(R.color.list_header));
		viewHolder.tvItemCode.setTextColor(context.getResources().getColor(R.color.list_header));
		
		viewHolder.tvExpiryDate.setText(viewHolder.productDO.strExpiryDate);
		viewHolder.etRemark.setText(viewHolder.productDO.remarks);
		viewHolder.etLotNumber.setText(viewHolder.productDO.LotNumber);
		
		viewHolder.btnCaptureImages.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent objIntent = new Intent(context, CaptureDamagedItemImage.class);
				objIntent.putExtra("vecImagePaths", viewHolder.productDO.vecDamageImages);
				objIntent.putExtra("position", position);
				objIntent.putExtra("itemCode", viewHolder.productDO.SKU);
				objIntent.putExtra("desc", viewHolder.productDO.Description);
				objIntent.putExtra("lat", lat);
				objIntent.putExtra("long", lang);
				((Activity)context).startActivityForResult(objIntent, 500);
			}
		});
		
		((BaseActivity)context).setActionInViews(viewHolder.cbItem, false);
		
		viewHolder.etRemark.setTag(viewHolder.productDO);
		viewHolder.etRemark.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				viewHolder.productDO.remarks = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		viewHolder.etLotNumber.setTag(viewHolder.productDO);
		viewHolder.etLotNumber.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				viewHolder.productDO.LotNumber = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		viewHolder.llExpiryDate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				viewHolder.tvExpiryDate.performClick();
			}
		});
		
		viewHolder.tvExpiryDate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				datePicker.setDate(viewHolder.tvExpiryDate, viewHolder.productDO);
			}
		});
		
		viewHolder.llLayout.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				viewHolder.productDO.isReccomended = true;
				if(ivSelectAll!=null)
					ivSelectAll.setImageResource(R.drawable.check_normal);
				viewHolder.cbItem.setChecked(!viewHolder.cbItem.isChecked());
			}
		});
		
		viewHolder.cbItem.setTag(viewHolder.productDO);
		viewHolder.cbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				ProductDO objiItem = (ProductDO) buttonView.getTag();
				if(objiItem.isReccomended)
				{
					if(isChecked)
					{
						viewHolder.productDO.isSelected = true;
						viewHolder.llReason.setVisibility(isReturn?View.VISIBLE:View.GONE);
					}
					else
					{
						viewHolder.productDO.isSelected = false;
						viewHolder.llReason.setVisibility(View.GONE);
					}
				}
			}
		});
		
		viewHolder.rgSellsbleState.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				if(StringUtils.getInt(viewHolder.rgSellsbleState.getTag().toString()) != 0)
				{
					clearTVExpiry(viewHolder.tvExpiryDate, viewHolder.productDO);
					viewHolder.productDO.reason = "";
				}
				
				viewHolder.rgSellsbleState.setTag(1);
				
				switch (checkedId)
				{
				case R.id.rbSellable:
					
					viewHolder.rbExpired.setChecked(false); viewHolder.rbDamaged.setChecked(false); viewHolder.rbSoonToExpired.setChecked(false);
					viewHolder.rgNonSellableOptions.setVisibility(View.GONE);
					viewHolder.productDO.reason = context.getResources().getString(R.string.good_condition);
					viewHolder.llCaptureImage.setVisibility(View.GONE);
					
					break;
					
				case R.id.rbNonSellable:
					
					viewHolder.rgNonSellableOptions.setVisibility(View.VISIBLE);
					viewHolder.llCaptureImage.setVisibility(View.VISIBLE);
					
					break;
				default:
					break;
				}
			}
		});
		
		if(isReturn)
		{
			viewHolder.rbGoodCondition.setVisibility(View.GONE);
			viewHolder.llCaptureImage.setVisibility(View.VISIBLE);
			viewHolder.llLotNumber.setVisibility(View.VISIBLE);
		}
		
		viewHolder.rbExpired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(StringUtils.getInt(buttonView.getTag().toString()) != 0)
						clearTVExpiry(viewHolder.tvExpiryDate, viewHolder.productDO);
					buttonView.setTag(1);
					
					viewHolder.productDO.reason = context.getResources().getString(R.string.ReturnReason1);
					viewHolder.rbDamaged.setChecked(false);
					viewHolder.rbSoonToExpired.setChecked(false);
				}
			}
		});
		
		viewHolder.rbDamaged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(StringUtils.getInt(buttonView.getTag().toString()) != 0)
						clearTVExpiry(viewHolder.tvExpiryDate, viewHolder.productDO);
					buttonView.setTag(1);
					
					viewHolder.llCaptureImage.setVisibility(View.VISIBLE);
					viewHolder.productDO.reason = context.getResources().getString(R.string.ReturnReason3);
					viewHolder.rbExpired.setChecked(false);
					viewHolder.rbSoonToExpired.setChecked(false);
				}
			}
		});
		
		viewHolder.rbSoonToExpired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					if(StringUtils.getInt(buttonView.getTag().toString()) != 0)
						clearTVExpiry(viewHolder.tvExpiryDate, viewHolder.productDO);
					
					buttonView.setTag(1);
					viewHolder.llCaptureImage.setVisibility(View.VISIBLE);
					viewHolder.productDO.reason = context.getResources().getString(R.string.ReturnReason2);
					viewHolder.rbExpired.setChecked(false);
					viewHolder.rbDamaged.setChecked(false);
				}
			}
		});
		
		((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	
	private void maintainState(ViewHolder ViewHolder) 
	{
		if(ViewHolder.productDO.isSelected)
		{
			ViewHolder.rgSellsbleState.setTag(0);
			ViewHolder.productDO.isSelected = true;
			ViewHolder.productDO.isReccomended = false;
			ViewHolder.cbItem.setChecked(true);
			ViewHolder.llReason.setVisibility(isReturn?View.VISIBLE:View.GONE);
			
			if(ViewHolder.productDO.reason.equalsIgnoreCase(context.getResources().getString(R.string.good_condition)))
			{
				ViewHolder.rbSellable.setChecked(true);
				ViewHolder.rgNonSellableOptions.setVisibility(View.GONE);
				ViewHolder.productDO.reason = context.getResources().getString(R.string.good_condition);
				ViewHolder.llCaptureImage.setVisibility(View.GONE);
			}
			else
			{
				ViewHolder.rbExpired.setChecked(false);
				ViewHolder.rbSoonToExpired.setChecked(false);
				ViewHolder.rbDamaged.setChecked(false);
				
				ViewHolder.rbNonSellable.setChecked(true);
				ViewHolder.rgNonSellableOptions.setVisibility(View.VISIBLE);
				ViewHolder.llCaptureImage.setVisibility(View.VISIBLE);
				
				if(ViewHolder.productDO.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason1)))
				{
					ViewHolder.rbExpired.setTag(0);
					ViewHolder.rbExpired.setChecked(true);
				}
				else if(ViewHolder.productDO.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason2)))
				{
					ViewHolder.rbSoonToExpired.setTag(0);
					ViewHolder.rbSoonToExpired.setChecked(true);
				}
				else if(ViewHolder.productDO.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason3)))
				{
					ViewHolder.rbDamaged.setTag(0);
					ViewHolder.rbDamaged.setChecked(true);
				}
			}
		}
		else
		{
			ViewHolder.productDO.isSelected = false;
			ViewHolder.productDO.isReccomended = false;
			ViewHolder.cbItem.setChecked(false);
			ViewHolder.llReason.setVisibility(View.GONE);
			
			ViewHolder.rbSoonToExpired.setTag(1);
			ViewHolder.rbExpired.setTag(1);
			ViewHolder.rbDamaged.setTag(1);
			
			ViewHolder.rgSellsbleState.setTag(1);
		}		
	}

	private void clearTVExpiry(TextView tvExpiryDate, ProductDO productDO)
	{
		productDO.strExpiryDate = "";
		tvExpiryDate.setText("");
	}
	
	public Vector<ProductDO> getSelectedItems()
	{
		Vector<ProductDO> vecSelectedItems = new Vector<ProductDO>();
		if(vecSearchedItems != null)
		{
			for(ProductDO productDO : vecSearchedItems)
				if(productDO.isSelected)
					vecSelectedItems.add(productDO);
		}
		return vecSelectedItems;
	}
	
	public void setItems(int pos, ArrayList<String> vecImagePaths)
	{
		vecSearchedItems.get(pos).vecDamageImages = vecImagePaths;
	}
	
	public void refresh(Vector<ProductDO> vecSearchedItemd)
	{
		this.vecSearchedItems = vecSearchedItemd;
		notifyDataSetChanged();
	}
	
	public interface DamageItemProduct
    {
	   public void getProducts(int position, ProductDO productDO);
    }
	
	class ViewHolder
	{
		ProductDO productDO;
		TextView tvItemDescription,tvItemCode, tvExpiryDate;
		CheckBox cbItem ;
		LinearLayout llReason, llExpiryDate, llCaptureImage, llLotNumber, llLayout;
		EditText etRemark, etLotNumber;
		Button btnCaptureImages;
		RadioGroup rgSellsbleState, rgNonSellableOptions;
		RadioButton rbExpired, rbDamaged, rbSoonToExpired, rbGoodCondition, rbSellable, rbNonSellable;
	}
}
