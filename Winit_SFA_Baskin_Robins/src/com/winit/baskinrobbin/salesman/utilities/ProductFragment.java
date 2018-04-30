package com.winit.baskinrobbin.salesman.utilities;

import java.util.HashMap;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;

@SuppressLint("ValidFragment")
public class ProductFragment extends Fragment
{
	private CategoryDO categoryDO;
	private JourneyPlanDO mallsDetail;
	private HashMap<String, HHInventryQTDO> hmInventory;
	public Context mContext;
	private HashMap<String, Float> hmConversion; 
	private HashMap<String, ProductDO>hmSelectedItems;
	
	
	@SuppressLint("ValidFragment")
	public ProductFragment(Context mContext, JourneyPlanDO mallsDetails, CategoryDO categoryDO, HashMap<String, Float> hmConversion, HashMap<String, HHInventryQTDO> hmInventory, HashMap<String, ProductDO>hmSelectedItems)
	{
		this.mContext 		 = 	mContext;
		this.categoryDO 	 = 	categoryDO;
		this.hmConversion 	 = 	hmConversion;
		this.hmInventory 	 = 	hmInventory;
		this.hmSelectedItems = 	hmSelectedItems;
		mallsDetail 		 =	mallsDetails;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		//inflate the order-sheet layout
		LinearLayout llOrderSheet   = (LinearLayout)inflater.inflate(R.layout.foc_product, null);
		ListView lvLoadStock   		= (ListView) llOrderSheet.findViewById(R.id.lvLoadStock);
		TextView tvNoItemFound  	= (TextView) llOrderSheet.findViewById(R.id.tvNoItemFound);
		EditText etSearch  			= (EditText) llOrderSheet.findViewById(R.id.etSearch);
		
		final InventoryItemAdapter inventoryItemAdapter = new InventoryItemAdapter(mContext, categoryDO.vecProducts, inflater, tvNoItemFound);
		lvLoadStock.setAdapter(inventoryItemAdapter);
		
		((BaseActivity)mContext).setTypeFace((LinearLayout)llOrderSheet);
		
		etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					Vector<ProductDO> vecTemp = new Vector<ProductDO>();
					
					for(ProductDO productDO : categoryDO.vecProducts)
					{
						String strText 		= (productDO).SKU;
						String strText1		= (productDO).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase())
								|| strText1.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(productDO);
					}
					if(vecTemp!=null)
						inventoryItemAdapter.refresh(vecTemp, inflater);
				}
				else
					inventoryItemAdapter.refresh(categoryDO.vecProducts, inflater);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		return llOrderSheet;
	}

	class InventoryItemAdapter extends ArrayAdapter<ProductDO>
	{
		private Vector<ProductDO> vecOrdProduct;
		private View view;
		private LayoutInflater inflater;
		private TextView textView;
		class TextChangeListener implements TextWatcher
		{
			public TextChangeListener()
			{
				LogUtils.infoLog("TextChangeListener","TextChangeListener");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				ProductDO objItem = null;
				if(view != null)
					objItem = (ProductDO) view.getTag();
				
				if(objItem != null)
				{
					int qty = StringUtils.getInt(s.toString());
					objItem.preUnits = ""+qty;
					objItem.quantityBU = ((BaseActivity)mContext).getQuantityBU(objItem, hmConversion);
					//objItem.quantityBU = ((BaseActivity)mContext).getQuantityBU(objItem, hmConversion,precision);
					
					if(qty > 0 && !((BaseActivity)mContext).isInventoryAvail(objItem, hmInventory))
					{
						objItem.preUnits = "0";
						((BaseActivity)mContext).showToast("Entered quantity should not be greater than available quantity.");
						if(objItem.etUnits != null)
							objItem.etUnits.setText("");
						
						objItem.quantityBU = ((BaseActivity)mContext).getQuantityBU(objItem, hmConversion);
						//objItem.quantityBU = ((BaseActivity)mContext).getQuantityBU(objItem, hmConversion,precision);
					}
					else
						objItem.preUnits = ""+StringUtils.getInt(s.toString());
					
					if(StringUtils.getInt(objItem.preUnits) > 0)
						hmSelectedItems.put(objItem.SKU, objItem);
					else
						hmSelectedItems.remove(objItem.SKU);
				}
			}
	
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
		}
		
		class FocusChangeListener implements OnFocusChangeListener
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
					view = v;
			}
		}
		
		public InventoryItemAdapter(Context context, Vector<ProductDO> vecOrdProduct, LayoutInflater layoutInflater, TextView textView)
		{
		    super(context, 0, vecOrdProduct);
		    this.inflater = layoutInflater;
			this.vecOrdProduct = vecOrdProduct;
			this.textView = textView;
		}
	
		public Vector<ProductDO> getModifiedData()
		{
			return vecOrdProduct;
		}
	
		
		//Method to refresh the List View
		void refresh(Vector<ProductDO> vecOrdProducts, LayoutInflater layoutInflater) 
		{
			this.inflater = layoutInflater;
			this.vecOrdProduct = vecOrdProducts;
			notifyDataSetChanged();
		}
	
		@Override
		public int getCount()
		{
			if(vecOrdProduct == null || vecOrdProduct.size() <= 0)
			{
				textView.setVisibility(View.VISIBLE);
				return 0;
			}
			else
				textView.setVisibility(View.GONE);
				
			return vecOrdProduct.size();
		}
	
		@Override
		public ProductDO getItem(int position) 
		{
			return null;
		}
	
		@Override
		public long getItemId(int position)
		{
			return 0;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ProductDO productDO 	= vecOrdProduct.get(position);
			
			convertView					= (LinearLayout) inflater.inflate(R.layout.inventory_cell_ohq,null);
			
			final TextView tvHeaderText	= (TextView)convertView.findViewById(R.id.tvHeaderText);
			TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
			
			final TextView evUOM		= (TextView)convertView.findViewById(R.id.evCases);
			final EditText etQt			= (EditText)convertView.findViewById(R.id.evUnits);
			
			if(productDO.SKU.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).SKU))
				etQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			
			String ohqty = "";
			if(hmInventory != null && hmInventory.containsKey(productDO.SKU + productDO.UOM))
			{	
				HHInventryQTDO qty 	= 	hmInventory.get(productDO.SKU + productDO.UOM);
				ohqty 				= 	"OHQ : " + ((BaseActivity)mContext).diffStock.format(qty.totalQt) +" "+qty.UOM;
			}
			
			tvHeaderText.setText(productDO.SKU +" # "+ohqty);
			tvDescription.setText(productDO.Description);
			
			etQt.setTag(productDO);
			evUOM.setTag(productDO);
			
			productDO.etUnits = etQt;
			
			etQt.setClickable(true);
			etQt.setEnabled(true);
			etQt.setFocusableInTouchMode(true);
			
			if(StringUtils.getFloat(productDO.preUnits) > 0)
				etQt.setText(""+productDO.preUnits);
			else
				etQt.setText("");
			
			evUOM.setText(productDO.UOM);
			evUOM.setTag(productDO.UOM);
			
			if(productDO.vecUOM != null && productDO.vecUOM.size() > 0)
				evUOM.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.arrow_2));
			else
				evUOM.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			
			evUOM.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if(productDO.vecUOM != null && productDO.vecUOM.size() > 0)
					{
						CustomBuilder customDialog = new CustomBuilder(mContext, "Select UOM", true);
						customDialog.setSingleChoiceItems(productDO.vecUOM, evUOM.getTag(), new CustomBuilder.OnClickListener() 
						{
							@Override
							public void onClick(CustomBuilder builder, Object selectedObject) 
							{
								String lastTag = ""+evUOM.getTag().toString();
								evUOM.setText((String)selectedObject);
								evUOM.setTag((String)selectedObject);
								productDO.UOM =(String)selectedObject;
								
								if(!lastTag.equalsIgnoreCase(productDO.UOM))
								{
									productDO.preUnits = "0";
									
									String ohqty = "";
									if(hmInventory != null && hmInventory.containsKey(productDO.SKU + productDO.UOM))
									{	
										HHInventryQTDO qty 	= 	hmInventory.get(productDO.SKU + productDO.UOM);
										ohqty 				= 	"OHQ : " + ((BaseActivity)mContext).diffStock.format(qty.totalQt) +" "+qty.UOM;
									}
									productDO.etUnits.setText("");
									tvHeaderText.setText(productDO.SKU +" # "+ohqty);
									productDO.quantityBU = ((BaseActivity)mContext).getQuantityBU(productDO, hmConversion);
									//productDO.quantityBU = ((BaseActivity)mContext).getQuantityBU(productDO, hmConversion,precision);
								}
								builder.dismiss();
							}
						});
						customDialog.show();
					}
				}
			});
			
			etQt.setOnFocusChangeListener(new FocusChangeListener());
			etQt.addTextChangedListener(new TextChangeListener());
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
				}
			});
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(45 * ((BaseActivity)mContext).px)));
			((BaseActivity)mContext).setTypeFace((LinearLayout)convertView);
			return convertView;
		}
	}
}