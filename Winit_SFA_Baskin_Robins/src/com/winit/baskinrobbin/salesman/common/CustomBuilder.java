package com.winit.baskinrobbin.salesman.common;

import java.util.Vector;

import android.R.color;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.dataobject.CompBrandDO;
import com.winit.baskinrobbin.salesman.dataobject.CompCategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.EmployeeDo;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;

/**
 * Description of class : To create custom Alert Dialog Box Single Choice Item.
 */
public class CustomBuilder 
{
	private Context context;
	private CustomDialog customDialog;
	private ListView listView;
	private TextView tvNoSearchFound;
	private FilterListAdapter filterListAdapter;
	private Vector<View> vecVisibleCountryCells;
	private Vector<Object> vecData;
	private Object selObj;
	private OnClickListener listener;
	private String title;
	private boolean isCancellable;
	private boolean isSearchInvisible = false;
	private boolean isAsset = false;
	
	/**
	 *  Constructor 
	 * @param context
	 * @param title
	 * @param isCancellable
	 */
	public CustomBuilder(Context context, String title, boolean isCancellable)
	{
		this.context = context;
		this.title = title;
		this.isCancellable = isCancellable;
	}
	
	public CustomBuilder(Context context, String title, boolean isCancellable, boolean isAsset)
	{
		this.context = context;
		this.title = title;
		this.isCancellable = isCancellable;
		this.isAsset = isAsset;
	}
	/**
	 * Method to set Single Choice Items 
	 * @param vecData
	 * @param selObj
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void setSingleChoiceItems(Object vecData, Object selObj, OnClickListener listener)
	{
		this.vecData  = (Vector<Object>)vecData;
		this.selObj   = selObj;
		this.listener = listener;
		if(selObj == null)
			this.selObj = new Object();
	}
	
	@SuppressWarnings("unchecked")
	public void setSingleChoiceItems(Object vecData, Object selObj, OnClickListener listener, boolean isSearchInvisible)
	{
		this.vecData  = (Vector<Object>)vecData;
		this.selObj   = selObj;
		this.listener = listener;
		this.isSearchInvisible = isSearchInvisible;
		if(selObj == null)
			this.selObj = new Object();
	}
	
	//Methof to show the Single Choice Itemsm Dialog
	public void show()
	{
		if(vecData == null)
			return;
		
		vecVisibleCountryCells = new Vector<View>();
		
		//Inflating the country_popup Layout
		View mView 		= LayoutInflater.from(context).inflate(R.layout.custom_builder, null);
		customDialog 	= new CustomDialog(context, mView, (new Preference(context).getIntFromPreference("DEVICE_DISPLAY_WIDTH",320) - 50), (new Preference(context).getIntFromPreference("DEVICE_DISPLAY_HEIGHT",480) -(new Preference(context).getIntFromPreference("DEVICE_DISPLAY_HEIGHT",480)/4)), isCancellable);
		
		//Finding the ID's
		TextView tvTitleBuider 		= (TextView) mView.findViewById(R.id.tvTitleBuider);
		final EditText etSelectItem = (EditText) mView.findViewById(R.id.etSelectItem);
		
		if(isSearchInvisible)
			etSelectItem.setVisibility(View.GONE);
		else
			etSelectItem.setVisibility(View.VISIBLE);
		
		
		etSelectItem.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTitleBuider.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTitleBuider.setText(title);
		etSelectItem.setHint(title);
		tvNoSearchFound = (TextView) mView.findViewById(R.id.tvNoSearchFound);
		tvNoSearchFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		//ListView
		listView = (ListView) mView.findViewById(R.id.lvSelectCountry);
		listView.setDivider(context.getResources().getDrawable(R.drawable.dot_seperator));
		listView.setFadingEdgeLength(0);
		listView.setCacheColorHint(0);
		listView.setVerticalScrollBarEnabled(false);
		listView.setSmoothScrollbarEnabled(true);
		
		
		filterListAdapter = new FilterListAdapter(vecData);
		listView.setSelector(color.transparent);
		//Setting the Adapter
		listView.setAdapter(filterListAdapter);
		
		//Functionality for listView Item Click
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				for(int i = 0; i < vecVisibleCountryCells.size(); i++)
				{
					View visibleCountryCell = vecVisibleCountryCells.get(i);
					((ImageView)visibleCountryCell.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.check1);
				}
			    
				((ImageView)view.findViewById(R.id.ivSelected)).setBackgroundResource(R.drawable.uncheck);
				
				listener.onClick(CustomBuilder.this, view.getTag());
			}
		});
		//Functionality for etSelectItem
		etSelectItem.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!s.toString().equalsIgnoreCase(""))
				{
					Vector<Object> vecTemp = new Vector<Object>();
					for(int i = 0; vecData != null && i < vecData.size(); i++)
					{
						Object obj = vecData.get(i);
						String field = "";
						
						//Comparing the Objects
						if(obj instanceof NameIDDo)
							field = ((NameIDDo)obj).strName;
						
						else if(obj instanceof CompBrandDO)
							field = ((CompBrandDO)obj).Brand;
						
						else if(obj instanceof CompCategoryDO)
							field = ((CompCategoryDO)obj).Category;
						
						else if(obj instanceof VehicleDO)
							field = ((VehicleDO)obj).VEHICLE_NO;
						
						else if(obj instanceof AssetDO)
							field = ""+((AssetDO)obj).name;
					
						else if(obj instanceof String)
							field = ((String)obj);
						
						else if(obj instanceof ProductDO)
							field = ((ProductDO)obj).SKU+((ProductDO)obj).Description;
						
						if(field.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecData.get(i));
					}
					if(vecTemp.size() > 0)
					{
						tvNoSearchFound.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
						filterListAdapter.refresh(vecTemp);
					}
					else
					{
						tvNoSearchFound.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
				}
				else
				{
					tvNoSearchFound.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					filterListAdapter.refresh(vecData);
				}
			}
		});
		
		customDialog.show();
	}
	
	public void dismiss()
	{
		customDialog.dismiss();
	}
	
	private class FilterListAdapter extends BaseAdapter
	{
		private Vector<Object> vecData;

		public FilterListAdapter(Vector<Object> vecData) 
		{
			this.vecData = vecData;
		}

		@Override
		public int getCount() 
		{
			if(vecData == null)
				return 0;
			else 
				return vecData.size();
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
			Object obj = vecData.get(position);
			
			//Inflating the country_cell Layout
			if(convertView == null)
			{
				convertView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.custom_builder_cell, null);
				vecVisibleCountryCells.add(convertView);
			}
			
			//Finding the Id's
			TextView tvSelectUrCountry  = (TextView)convertView.findViewById(R.id.tvSelectName);
			TextView tvCount	 		= (TextView)convertView.findViewById(R.id.tvCount);
			ImageView ivSelected = (ImageView)convertView.findViewById(R.id.ivSelected);
			
			String name = "", count = ""; boolean isShowAsSelected = false;
			//in Case of OrderListDO
			if(obj instanceof NameIDDo)
			{
				NameIDDo objNameIDDo = ((NameIDDo)obj);
				name  				 = objNameIDDo.strName;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof NameIDDo && ((NameIDDo)selObj).strName == objNameIDDo.strName)
					isShowAsSelected = true;
			}
			else if(obj instanceof CompBrandDO)
			{
				CompBrandDO compBrandDO = ((CompBrandDO)obj);
				name  				 = compBrandDO.Brand;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof CompBrandDO && ((CompBrandDO)selObj).Brand == compBrandDO.Brand)
					isShowAsSelected = true;
			}
			else if(obj instanceof CompCategoryDO)
			{
				CompCategoryDO compCategoryDO = ((CompCategoryDO)obj);
				name  				 = compCategoryDO.Category;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof CompCategoryDO && ((CompCategoryDO)selObj).Category == compCategoryDO.Category)
					isShowAsSelected = true;
			}
			else if(obj instanceof VehicleDO)
			{
				VehicleDO vehicleDO = ((VehicleDO)obj);
				name = ((VehicleDO)obj).VEHICLE_NO;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof VehicleDO && ((VehicleDO)selObj).VEHICLE_NO ==  vehicleDO.VEHICLE_NO)
					isShowAsSelected = true;
			}
			else if(obj instanceof EmployeeDo)
			{
				EmployeeDo employeeDo = ((EmployeeDo)obj);
				name = ((EmployeeDo)obj).strEmpName;
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof EmployeeDo && ((EmployeeDo)selObj).strEmpName ==  employeeDo.strEmpName)
					isShowAsSelected = true;
			}
			/*else if(obj instanceof JourneyPlanDO)
			{
				JourneyPlanDO customerDo = ((JourneyPlanDO)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				name = ""+customerDo.site+" - "+customerDo.siteName+" - "+customerDo.city+" L";
				if(selObj instanceof JourneyPlanDO && ((JourneyPlanDO)selObj).site == customerDo.site)
					isShowAsSelected = true;
			}*/
			else if(obj instanceof AssetDO)
			{
				AssetDO assets = ((AssetDO)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				name = ""+assets.name+" - "+assets.assetType+" - "+assets.capacity;
				if(selObj instanceof AssetDO && ((AssetDO)selObj).assetId == assets.assetId)
					isShowAsSelected = true;
			}
			else if(obj instanceof ProductDO)
			{
				ProductDO productDO = ((ProductDO)obj);
				name = (productDO).SKU +"("+productDO.Description+")";
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof ProductDO && ((ProductDO)selObj).SKU == productDO.SKU)
					isShowAsSelected = true;
			}
			else if(obj instanceof String)
			{
				name = ((String)obj);
				tvCount.setVisibility(View.GONE);
				ivSelected.setVisibility(View.VISIBLE);
				if(selObj instanceof String && ((String)selObj) ==  name)
					isShowAsSelected = true;
			}
			tvSelectUrCountry.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			tvSelectUrCountry.setText(name);
			tvCount.setText("Order ("+count+")");
			
			if(isShowAsSelected)
				ivSelected.setBackgroundResource(R.drawable.uncheck);
			else 
				ivSelected.setBackgroundResource(R.drawable.check1);
			
			convertView.setTag(obj);
			
			if(isAsset)
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(50 * BaseActivity.px)));
			else 
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(30 * BaseActivity.px)));
			return convertView;
		}
		//Method to refresh the List View
		private void refresh(Vector<Object> vecData) 
		{
			this.vecData = vecData;
			this.notifyDataSetChanged();
		}
	}
	
	public interface OnClickListener 
	{
		public void onClick(CustomBuilder builder, Object selectedObject);
	}
}
