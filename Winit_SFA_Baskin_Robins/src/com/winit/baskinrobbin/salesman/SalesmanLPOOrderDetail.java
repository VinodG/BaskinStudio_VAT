package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class SalesmanLPOOrderDetail extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llItemHeader, llPaymentLayout, llTotalValue;
	private TextView tvHead;
	private ListView lvReturnorder;
	private orderDetailAdapter orListAdapter;
	private Button btnAddNewOrder,btnModify, btnDelete, btnRefreshOrder, btnPrint;
	private ArrayList<ProductDO> vecOrdProduct;
	private OrderDO objOrders;
	private JourneyPlanDO mallsDetails;
	private String receiptNumber = "";
	private TextView tvtvOrderNoVal, tvtvOrderDateVal, tvDeliveryDateVal, tvOrderTypeVal, tvOrderStatusVal, tvBatch_SourceNameValue, tvCustTrxTypeNameValue;
	private boolean isFromCustomerCheckIn = false;
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout
		llOrder_List = (LinearLayout)getLayoutInflater().inflate(R.layout.lpo_order_details_list, null);
		llBody.addView(llOrder_List, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		//getting data from Intent
		if(getIntent().getExtras() != null)
		{
			objOrders 		= 	(OrderDO) getIntent().getSerializableExtra("orderid");
			mallsDetails 	=	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			
			if(getIntent().getExtras().containsKey("isFromCustomerCheckIn"))
				isFromCustomerCheckIn = getIntent().getExtras().getBoolean("isFromCustomerCheckIn");
		}
		
		//function for getting id'ss and setting type-faces
		intialiseControls();
		
		setTypeFace(llOrder_List);
		lvReturnorder.setFadingEdgeLength(0);
		lvReturnorder.setDividerHeight(0);
		lvReturnorder.setVerticalScrollBarEnabled(false);
		lvReturnorder.setAdapter(orListAdapter);
		llLayoutMiddle.addView(lvReturnorder , LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//click event for button AddNewOrder 
		
		btnAddNewOrder.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.check5), null, null, null);
		
		if(objOrders != null 
				&& isFromCustomerCheckIn
				&& objOrders.DeliveryDate.contains(CalendarUtils.getCurrentDateAsString())
				&& (objOrders.LPOStatus.equalsIgnoreCase(AppStatus.LPO_STATUS_APROOVED+"")))
		{
			btnPrint.setVisibility(View.GONE);
			btnRefreshOrder.setVisibility(View.VISIBLE);
			btnRefreshOrder.setText("Confirm");
			btnRefreshOrder.setTag("Confirm");
		}
		else
			btnPrint.setVisibility(View.VISIBLE);
		
		btnAddNewOrder.setText(" Finish ");
		btnAddNewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnDelete.setVisibility(View.GONE);
		btnModify.setVisibility(View.GONE);
		
		btnRefreshOrder.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				gotoPrviewScreen();
			}
		});
		
		if(objOrders != null)
		{
			tvtvOrderNoVal.setText(objOrders.OrderId+"");
			tvtvOrderDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.InvoiceDate)+"");
			tvDeliveryDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.DeliveryDate)+"");
			tvOrderStatusVal.setText(objOrders.TRXStatus+"");
			tvBatch_SourceNameValue.setText(objOrders.Batch_Source_Name+"");
			tvCustTrxTypeNameValue.setText(objOrders.Trx_Type_Name+"");
			
			tvOrderTypeVal.setText(AppConstants.LPO_ORDER);
		}
		
		tvHead.setText(AppConstants.LPO_ORDER+" Detail");
		
		loadData();
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(vecOrdProduct != null)
				{
					ShowOptionPopupForPrinter(SalesmanLPOOrderDetail.this,new PrintPopup() {
						@Override
						public void selectedOption(int selectedPrinter)
						{

							Intent intent=null;
							if(selectedPrinter==AppConstants.CANCEL)
							{
								hideLoader();

							}
							else
							{
								if(selectedPrinter==AppConstants.WOOSIM)
									intent=new Intent(SalesmanLPOOrderDetail.this, WoosimPrinterActivity.class);
								else if(selectedPrinter==AppConstants.DOTMATRIX)
									intent=new Intent(SalesmanLPOOrderDetail.this, PrinterConnectorArabic.class);
								intent.putExtra("CALLFROM", CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY);
								intent.putExtra("orderDo",    objOrders);
								intent.putExtra("mallsDetails", mallsDetails);
								intent.putExtra("array", vecOrdProduct);
								startActivity(intent);
								hideLoader();
							}



						}
					});
//					Intent intent = new Intent(SalesmanLPOOrderDetail.this, WoosimPrinterActivity.class);
//					intent.putExtra("CALLFROM", CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY);
//					intent.putExtra("orderDo",    objOrders);
//					intent.putExtra("mallsDetails", mallsDetails);
//					intent.putExtra("array", vecOrdProduct);
//					startActivity(intent);
				}
			}
		});
	}
	
	/** initializing all the Controls  of DeliveryAgentOrderList class **/
	public void intialiseControls()
	{
		//getting id's
		tvHead 				= 	(TextView)llOrder_List.findViewById(R.id.tvHead);
		llAddNewOrder		=	(LinearLayout)llOrder_List.findViewById(R.id.llAddNewOrder);
		llLayoutMiddle 		=	(LinearLayout)llOrder_List.findViewById(R.id.llLayoutMiddle);
		
		llTotalValue		=	(LinearLayout)llOrder_List.findViewById(R.id.llTotalValue);
		btnModify			=	(Button)llOrder_List.findViewById(R.id.btnModify);
		llItemHeader		=	(LinearLayout)llOrder_List.findViewById(R.id.llItemHeader);
		llPaymentLayout		=	(LinearLayout)llOrder_List.findViewById(R.id.llPaymentLayout);
		TextView tvItemCode	= 	(TextView)llOrder_List.findViewById(R.id.tvItemCode);
		TextView tvUnits	= 	(TextView)llOrder_List.findViewById(R.id.tvUnits);
		TextView tvCases	= 	(TextView)llOrder_List.findViewById(R.id.tvCases);
		btnDelete			=	(Button)llOrder_List.findViewById(R.id.btnGRVNote);
		btnPrint			=	(Button)llOrder_List.findViewById(R.id.btnPrint);
		
		tvtvOrderNoVal		=	(TextView)llOrder_List.findViewById(R.id.tvtvOrderNoVal);
		tvtvOrderDateVal	=	(TextView)llOrder_List.findViewById(R.id.tvtvOrderDateVal);
		tvDeliveryDateVal	=	(TextView)llOrder_List.findViewById(R.id.tvDeliveryDateVal);
		tvOrderTypeVal		=	(TextView)llOrder_List.findViewById(R.id.tvOrderTypeVal);
		tvOrderStatusVal	=	(TextView)llOrder_List.findViewById(R.id.tvOrderStatusVal);
		tvBatch_SourceNameValue =	(TextView)llOrder_List.findViewById(R.id.tvBatch_SourceNameValue);
		tvCustTrxTypeNameValue	=	(TextView)llOrder_List.findViewById(R.id.tvCustTrxTypeNameValue);
		
		btnRefreshOrder		=	(Button)llOrder_List.findViewById(R.id.btnRefreshOrder);
		
		Button btnCollectPayment	=	(Button)llOrder_List.findViewById(R.id.btnCollectPayment);
		btnAddNewOrder = (Button)llOrder_List.findViewById(R.id.btnAddNewOrder);
		
		llOrder_List. findViewById(R.id.viewDividerOne).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llMonthForfarmance).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.viewDividerTwo).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llRoutePerformance).setVisibility(View.GONE);
		
		//Setting visibility for the Bottom button
		llAddNewOrder.setVisibility(View.VISIBLE);
		btnDelete.setVisibility(View.VISIBLE);
		btnCollectPayment.setVisibility(View.GONE);
		btnModify.setVisibility(View.VISIBLE);
		//setting type-faces
		btnDelete.setText(" Delete Order ");
		
		orListAdapter 		= 	new orderDetailAdapter(vecOrdProduct);
		lvReturnorder 		= 	new ListView(SalesmanLPOOrderDetail.this);
		if(objOrders!=null && (objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))))
			llItemHeader.setVisibility(View.VISIBLE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnPrint.setVisibility(View.VISIBLE);
	}
	
	public class orderDetailAdapter extends BaseAdapter
	{
		ArrayList<ProductDO> vecOrderList;
		public orderDetailAdapter(ArrayList<ProductDO> vecOrderList)
		{
			this.vecOrderList = vecOrderList;
		}
		
		@Override
		public int getCount() 
		{
			if(vecOrderList == null)
				return 0;
			return vecOrderList.size();
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
			ProductDO orderDetail = vecOrderList.get(position);
			
			if(convertView == null)
				convertView				= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_add_stock,null);
			
			TextView tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
			TextView evUOM				= (TextView)convertView.findViewById(R.id.etUOM);
			EditText etQt				= (EditText)convertView.findViewById(R.id.etQt);
			ImageView ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
			
			ivAcceptCheckItems.setVisibility(View.GONE);
			tvProductKey.setText(orderDetail.SKU);
			tvVendorName.setText(orderDetail.Description);
			
			convertView.setTag(orderDetail);
			
			evUOM.setText(orderDetail.UOM);
			etQt.setText(""+diffStock.format(StringUtils.getFloat(orderDetail.units)));
			
			setTypeFace((ViewGroup)convertView);
			return convertView;
		}
		
		public void refresh(ArrayList<ProductDO> vecOrderList)
		{
			this.vecOrderList = vecOrderList;
			notifyDataSetChanged();
		}
	}	
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
			super.onBackPressed();
	}
	
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Vector<ProductDO> vecTemp = new OrderDetailsDA().getOrderDetails(objOrders, null);
				
				if(vecTemp != null)
					vecOrdProduct = new ArrayList<ProductDO>(vecTemp);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(orListAdapter != null)
							orListAdapter.refresh(vecOrdProduct);
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void gotoPrviewScreen()
	{
		showLoader("Please wait..");
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				HashMap<String, HHInventryQTDO> hmInventory  = new OrderDetailsDA().getAvailInventoryQtys_Temp("");
				HashMap<String, Float> hmConversion = new OrderDetailsDA().getUOMFactor();
				
				if(objOrders != null && objOrders.OrderId != null)
					AppConstants.hmCapturedInventory  = new CaptureInventryDA().getOrder_LPO_Detail1(objOrders.OrderId, objOrders.orderSubType, hmConversion);
			
				final Vector<ProductDO> vecVanDiffProduct = checkVanQty(hmInventory);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(vecVanDiffProduct != null && vecVanDiffProduct.size() > 0)
						{
							showVanQtyDiffPopup(vecVanDiffProduct);
						}
						else
						{
							Intent intent = new Intent(SalesmanLPOOrderDetail.this, SalesmanOrderPreview.class);
							intent.putExtra("objOrder",objOrders);
							intent.putExtra("mallsDetails",mallsDetails);
							intent.putExtra("isLPOOrderDelivery",true);
							startActivityForResult(intent, 1000);
						}
					}
				});
			}
		}).start();
	}
	
	private Vector<ProductDO> checkVanQty(HashMap<String, HHInventryQTDO> hmInventory)
	{
		Vector<ProductDO> vecProductDOs= new Vector<ProductDO>();
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
		{
			Set<String> keys = AppConstants.hmCapturedInventory.keySet();
			for (String string : keys) 
			{
				Vector<ProductDO> vecProductDOs2 = AppConstants.hmCapturedInventory.get(string);
				for (ProductDO productDO : vecProductDOs2)
				{
					if(!isInventoryAvail(productDO, hmInventory))
					{
						float availQty = 0;
						String key = productDO.SKU + productDO.UOM;
						
						if(hmInventory.containsKey(key))
							availQty = hmInventory.get(key).totalQt;
						
						productDO.preUnits = ""+availQty;
						vecProductDOs.add(productDO);
					}
				}
			}
		}
		return vecProductDOs;
	}
	
	CustomDialog vanQtyDiffPopup;
	private void showVanQtyDiffPopup(Vector<ProductDO> vecProductDOs)
	{
		if(vanQtyDiffPopup != null && vanQtyDiffPopup.isShowing())
			vanQtyDiffPopup.dismiss();
		
		View vvVanDiffQty 		= inflater.inflate(R.layout.van_qty_diff_popup, null);
		
		vanQtyDiffPopup = new CustomDialog(SalesmanLPOOrderDetail.this, vvVanDiffQty, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		vanQtyDiffPopup.setCancelable(false);
		
		Button btnYesPopup = (Button) vvVanDiffQty.findViewById(R.id.btnYesPopup);
		Button btnNoPopup  = (Button) vvVanDiffQty.findViewById(R.id.btnNoPopup);
		ListView lvVanQtyDiff = (ListView) vvVanDiffQty.findViewById(R.id.lvVanQtyDiff);
		
		lvVanQtyDiff.setAdapter(new VanQtyDiffAdapter(vecProductDOs));
		
		btnYesPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (vanQtyDiffPopup!= null && vanQtyDiffPopup.isShowing())
					vanQtyDiffPopup.dismiss();
				
				Intent intent = new Intent(SalesmanLPOOrderDetail.this, SalesmanOrderPreview.class);
				intent.putExtra("objOrder",objOrders);
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("isConfirmLPOOrder",true);
				intent.putExtra("isLPOOrderDelivery",true);
				startActivityForResult(intent, 1000);
			}
		});
		btnNoPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (vanQtyDiffPopup!= null && vanQtyDiffPopup.isShowing())
					vanQtyDiffPopup.dismiss();
			}
		});
		try 
		{
			setTypeFace((LinearLayout)vvVanDiffQty);
			if (!vanQtyDiffPopup.isShowing())
				vanQtyDiffPopup.show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public class VanQtyDiffAdapter extends BaseAdapter
	{
		private Vector<ProductDO> vecProductDOs;
		public VanQtyDiffAdapter(Vector<ProductDO> vecProductDOs) 
		{
			this.vecProductDOs = vecProductDOs;
		}

		@Override
		public int getCount() 
		{
			if(vecProductDOs != null && vecProductDOs.size() > 0)
			return vecProductDOs.size();
			
			return 0;
		}
		public void refresh()
		{
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return 0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) 
		{
			ProductDO productDO = vecProductDOs.get(arg0);
			LinearLayout llLayout 	= (LinearLayout)inflater.inflate(R.layout.ven_diff_qty_cell, null);
			TextView tvItemCode		= (TextView)llLayout.findViewById(R.id.tvItemCode);
			TextView tvItemDesc		= (TextView)llLayout.findViewById(R.id.tvItemDesc);
			TextView tvOrderedQty	= (TextView)llLayout.findViewById(R.id.tvOrderedQty);
			TextView tvVanQty		= (TextView)llLayout.findViewById(R.id.tvVanQty);
			TextView tvUOM			= (TextView)llLayout.findViewById(R.id.tvUOM);
			
			tvUOM.setText(productDO.UOM);
			tvItemCode.setText(productDO.SKU);
			tvItemDesc.setText(productDO.Description1);
			tvOrderedQty.setText(productDO.lpoOrderedUnit);
			tvVanQty.setText(productDO.preUnits);
			
			setTypeFace(llLayout);
			return llLayout;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == 900)
		{
			setResult(900);
			finish();
		}
	}
}
