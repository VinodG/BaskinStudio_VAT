package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class SalesmanReplacementOrderDetail extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llItemHeader, llPaymentLayout, llTotalValue;
	private TextView tvHead;
	private ListView lvReturnorder;
	private orderDetailAdapter orListAdapter;
	private Button btnAddNewOrder,btnModify, btnDelete, btnRefreshOrder, btnPrint;
	public static Vector<ProductDO> vecOrdProduct;
	private OrderDO objOrders;
	private JourneyPlanDO mallsDetails;
	private String receiptNumber = "";
	private float totalInvoicedPrice = 0;
	private TextView tvtvOrderNoVal, tvtvOrderDateVal, tvDeliveryDateVal, tvOrderTypeVal, tvOrderStatusVal, 
					 tvTotalAmtHVal, tvBatch_SourceNameValue, tvCustTrxTypeNameValue;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout
		llOrder_List = (LinearLayout)getLayoutInflater().inflate(R.layout.replacement_order_details_list, null);
		llBody.addView(llOrder_List, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		vecOrdProduct = new Vector<ProductDO>();
		
		//getting data from Intent
		if(getIntent().getExtras() != null)
		{
			objOrders 		= 	(OrderDO) getIntent().getSerializableExtra("orderid");
			mallsDetails 	=	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		}
		
		//function for getting id'ss and setting type-faces
		intialiseControls();
		
		setTypeFace(llOrder_List);
		lvReturnorder.setFadingEdgeLength(0);
		lvReturnorder.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvReturnorder.setVerticalScrollBarEnabled(false);
		lvReturnorder.setAdapter(orListAdapter);
		llLayoutMiddle.addView(lvReturnorder , LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//click event for button AddNewOrder 
		
		btnAddNewOrder.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.check5), null, null, null);
		
		
		if(objOrders != null && objOrders.TRXStatus.equalsIgnoreCase("H"))
			btnRefreshOrder.setVisibility(View.VISIBLE);
		
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
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT)
			&& (!objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || !objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)))
			btnModify.setVisibility(View.VISIBLE);
		else
			btnModify.setVisibility(View.GONE);
		
		btnModify.setVisibility(View.GONE);
		btnModify.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader("Loading...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						receiptNumber = new CommonDA().getOrderStatus(objOrders.OrderId);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(receiptNumber == null || receiptNumber.length() <= 0)
								{
									Intent intent = new Intent(SalesmanReplacementOrderDetail.this, SalesManTakeOrder.class);
									intent.putExtra("orderid", objOrders);
									intent.putExtra("mallsDetails", mallsDetails);
									startActivity(intent);
								}
								else
									showCustomDialog(SalesmanReplacementOrderDetail.this, getString(R.string.warning), "Payment has been made for this invoice with receipt number - "+receiptNumber +". Please delete the receipt before modifying invoice.", "Delete Receipt", "Cancel", "DeleteReceipt");
								
								hideLoader();
							}
						});
					}
				}).start();
			}
		});
		
		if(objOrders != null)
		{
			tvtvOrderNoVal.setText(objOrders.OrderId+"");
			tvtvOrderDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.InvoiceDate)+"");
			tvDeliveryDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.DeliveryDate)+"");
			tvOrderStatusVal.setText("DELIVERED");
			tvBatch_SourceNameValue.setText(objOrders.Batch_Source_Name+"");
			tvCustTrxTypeNameValue.setText(objOrders.Trx_Type_Name+"");
			
			tvOrderTypeVal.setText("Replacement Order");
		}
		
		tvHead.setText("Replacement Order Details");
		
		loadData();
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShowOptionPopupForPrinter(SalesmanReplacementOrderDetail.this,new PrintPopup() {
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
								intent=new Intent(SalesmanReplacementOrderDetail.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(SalesmanReplacementOrderDetail.this, PrinterConnectorArabic.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_REPLACE_SUMMARY);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("OrderId", objOrders.OrderId);
							intent.putExtra("postDate", objOrders.InvoiceDate.split("T")[0]);
							startActivityForResult(intent, 1000);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(SalesmanReplacementOrderDetail.this, WoosimPrinterActivity.class);
//    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_REPLACE_SUMMARY);
//    			intent.putExtra("mallsDetails", mallsDetails);
//    			intent.putExtra("OrderId", objOrders.OrderId);
//    			intent.putExtra("postDate", objOrders.InvoiceDate.split("T")[0]);
//				startActivityForResult(intent, 1000);
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
		btnDelete			=	(Button)llOrder_List.findViewById(R.id.btnGRVNote);
		btnPrint			=	(Button)llOrder_List.findViewById(R.id.btnPrint);
		
		tvtvOrderNoVal		=	(TextView)llOrder_List.findViewById(R.id.tvtvOrderNoVal);
		tvtvOrderDateVal	=	(TextView)llOrder_List.findViewById(R.id.tvtvOrderDateVal);
		tvDeliveryDateVal	=	(TextView)llOrder_List.findViewById(R.id.tvDeliveryDateVal);
		tvOrderTypeVal		=	(TextView)llOrder_List.findViewById(R.id.tvOrderTypeVal);
		tvOrderStatusVal	=	(TextView)llOrder_List.findViewById(R.id.tvOrderStatusVal);
		tvTotalAmtHVal		=	(TextView)llOrder_List.findViewById(R.id.tvTotalAmtHVal);
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
		lvReturnorder 		= 	new ListView(SalesmanReplacementOrderDetail.this);
		
		llItemHeader.setVisibility(View.VISIBLE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnPrint.setVisibility(View.VISIBLE);
	}
	
	public class orderDetailAdapter extends BaseAdapter
	{
		Vector<ProductDO> vecOrderList;
		public orderDetailAdapter(Vector<ProductDO> vecOrderList)
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

			//getting current object form the Vector
			final ProductDO objItem = vecOrdProduct.get(position);
			
			//inflating the preview_order_list_cell layout for list Cell
			if(convertView == null)
				convertView = inflater.inflate(R.layout.replace_order_preview_list_cell, null);
			
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
			
			etQuantity.setText(""+objItem.units);
			
			etCases.setText(objItem.UOM);
			
			setTypeFace((ViewGroup) convertView);
			return convertView;
		
		}
		
		public void refresh(Vector<ProductDO> vecOrderList)
		{
			this.vecOrderList = vecOrderList;
			notifyDataSetChanged();
		}
	}	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("deleted"))
		{
			Intent intent = new Intent(SalesmanReplacementOrderDetail.this, SalesManTakeOrder.class);
			intent.putExtra("orderid", objOrders);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("rejected"))
		{
			onButtonYesClick("served");
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
	
	private void getTotalAmountWithDiscountAfterDelivery(Vector<ProductDO> vecdata)
	{
		float strTotalAmount  = 0.0f;
		
		if(vecdata != null && vecdata.size() > 0)
		{
			for(ProductDO detailsDO : vecdata)
			{
				strTotalAmount +=  detailsDO.invoiceAmount;
			}
			tvTotalAmtHVal.setText(mallsDetails.currencyCode+" "+diffAmt.format(strTotalAmount));
			totalInvoicedPrice = strTotalAmount;
		}
	}
	
	private void loadData()
	{
		if(objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)) )
		{
			showLoader(getResources().getString(R.string.loading));
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					vecOrdProduct	=	new OrderDetailsDA().getOrderDetails(objOrders, null);
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							if(orListAdapter != null)
								orListAdapter.refresh(vecOrdProduct);
							getTotalAmountWithDiscountAfterDelivery(vecOrdProduct);
							hideLoader();
						}
					});
				}
			}).start();
		}
	}
}
