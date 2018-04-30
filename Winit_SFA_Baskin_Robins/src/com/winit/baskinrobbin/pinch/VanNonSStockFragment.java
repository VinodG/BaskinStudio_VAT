package com.winit.baskinrobbin.pinch;

import java.util.Vector;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.WoosimPrinterActivity;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

@SuppressLint("ValidFragment")
public class VanNonSStockFragment extends Fragment
{
	private LinearLayout llMain;
	private TextView tvInvQtyTitle, tvItemCode, tvTotalQty, tvUOM_Title, tvResultOfSearch;
	private ListView lvInventoryItems;
	private Vector<InventoryObject> vecInventoryItems;
	private CustomeListAdapter customeListAdapter;
	private Button btnPrint;
	private Context context;
	private EditText etSearch;
	
	public VanNonSStockFragment(Context context, Vector<InventoryObject> vecInventoryObjects) {
		this.vecInventoryItems = vecInventoryObjects;
		this.context = context;
	}
	public VanNonSStockFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.return_inventory_qty, null);
		
		intialiseControls();
		((BaseActivity)context).setTypeFace(llMain);
		
		lvInventoryItems.setCacheColorHint(0);
		lvInventoryItems.setDivider(null);
		lvInventoryItems.setSelector(color.transparent);
		lvInventoryItems.setAdapter(customeListAdapter = new CustomeListAdapter(vecInventoryItems));
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(vecInventoryItems == null || vecInventoryItems.size() == 0)
					((BaseActivity)context).showCustomDialog(context, "Alert!", "There is no item to print.", "OK", "", "");
				else	
				{
					((BaseActivity)context).ShowOptionPopupForPrinter(context,new BaseActivity.PrintPopup() {
						@Override
						public void selectedOption(int selectedPrinter)
						{

							Intent intent=null;
							if(selectedPrinter== AppConstants.CANCEL)
							{
								((BaseActivity)context).hideLoader();

							}
							else
							{
								if(selectedPrinter== AppConstants.WOOSIM)
									intent=new Intent(context, WoosimPrinterActivity.class);
								else if(selectedPrinter==AppConstants.DOTMATRIX)
									intent=new Intent(context, PrinterConnectorArabic.class);
								intent.putExtra("vec", vecInventoryItems);
								intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_RETURN_INVENTORY);
								startActivityForResult(intent, 1000);
							}
						}
					});
//					Intent intent = new Intent(context, WoosimPrinterActivity.class);
//					intent.putExtra("vec", vecInventoryItems);
//					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_RETURN_INVENTORY);
//					startActivityForResult(intent, 1000);
				}
			}
		});
		
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString()!=null || s.length() > 0)
				{
					Vector<InventoryObject> vecTemp = new Vector<InventoryObject>();
					for(int index = 0; vecInventoryItems != null && index < vecInventoryItems.size(); index++)
					{
						InventoryObject obj = vecInventoryItems.get(index);
						String strText =  obj.itemCode;
						String strDes =  ""+obj.itemDescription;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecInventoryItems.get(index));
					}
					customeListAdapter.refresh(vecTemp);
				}
				else if(customeListAdapter!= null)
					customeListAdapter.refresh(vecInventoryItems);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		return llMain;
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvInvQtyTitle		=	(TextView)llMain.findViewById(R.id.tvInvQtyTitle);
		tvTotalQty			=	(TextView)llMain.findViewById(R.id.tvTotalQty);
		tvItemCode			=	(TextView)llMain.findViewById(R.id.tvItemCode);
		lvInventoryItems	=	(ListView)llMain.findViewById(R.id.lvInventoryItems);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvUOM_Title			=	(TextView)llMain.findViewById(R.id.tvUOM_Title);
		btnPrint			=	(Button)llMain.findViewById(R.id.btnPrint);
		etSearch			=	(EditText)llMain.findViewById(R.id.etSearch);
		
		tvInvQtyTitle.setText("Return Inventory");
	}
	
	public class CustomeListAdapter extends BaseAdapter
	{
		Vector<InventoryObject> vecInventoryItems;
		public CustomeListAdapter(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
		}
		@Override
		public int getCount() 
		{
			if(vecInventoryItems!=null && vecInventoryItems.size()>0)
			{
				lvInventoryItems.setVisibility(View.VISIBLE);
				tvResultOfSearch.setVisibility(View.GONE);
			}
			else
			{
				lvInventoryItems.setVisibility(View.GONE);
				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
			
			return vecInventoryItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecInventoryItems.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			InventoryObject inventoryObject = vecInventoryItems.get(position);
			if(convertView == null)
				convertView = ((BaseActivity)context).inflater.inflate(R.layout.inventory_qty_cell, null);
			
			TextView tvItemCodeText = (TextView)convertView.findViewById(R.id.tvItemCodeText);
			TextView tvDescription 	= (TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvTotalQty 	= (TextView)convertView.findViewById(R.id.tvTotalQty);
			TextView tvDeliveredQty = (TextView)convertView.findViewById(R.id.tvDeliveredQty);
			TextView tvAvailQty 	= (TextView)convertView.findViewById(R.id.tvAvailQty);
			TextView tvUOM 			= (TextView)convertView.findViewById(R.id.tvUOM);
			TextView tvUnitPrecase 	= (TextView)convertView.findViewById(R.id.tvUnitPrecase);
			
			tvDeliveredQty.setVisibility(View.GONE);
			tvAvailQty.setVisibility(View.GONE);
			tvUnitPrecase.setVisibility(View.VISIBLE);
			
			tvUnitPrecase.setText("Reason : "+inventoryObject.reason +" | "+CalendarUtils.getFormatedDatefromString(inventoryObject.expiryDate));
			tvItemCodeText.setText(inventoryObject.itemCode);
			tvDescription.setText(inventoryObject.itemDescription);
			tvTotalQty.setText(""+(inventoryObject.PrimaryQuantity >= 0 ? inventoryObject.PrimaryQuantity : 0));
			tvUOM.setText(""+inventoryObject.UOM);
			
			((BaseActivity)context).setTypeFace((ViewGroup) convertView);
			return convertView;
		}
		
		public void refresh(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
			notifyDataSetChanged();
		}
	}
}
