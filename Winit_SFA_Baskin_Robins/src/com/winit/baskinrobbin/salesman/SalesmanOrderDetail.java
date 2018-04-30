package com.winit.baskinrobbin.salesman;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.content.Intent;
import android.text.TextUtils;
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
import com.winit.baskinrobbin.parsers.OrderDeleteParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.DiscountDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class SalesmanOrderDetail extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llItemHeader, llPaymentLayout, llTotalValue ,llAmtwithTax,llrndoff,llTotalinvoiceAmt;
	private TextView tvHead;
	private ListView lvReturnorder;
	private orderDetailAdapter orListAdapter;
	private Button btnAddNewOrder,btnModify, btnDelete, btnRefreshOrder, btnPrint;
	public static Vector<ProductDO> vecOrdProduct;
	private OrderDO objOrders;
	private JourneyPlanDO mallsDetails;
	private String receiptNumber = "", errorItems = "";
	private TextView tvtvOrderNoVal, tvtvOrderDateVal, tvDeliveryDateVal, tvOrderTypeVal, tvOrderStatusVal, 
					 tvTotalAmtHVal, tvBatch_SourceNameValue, tvCustTrxTypeNameValue,tvRoundoffVAl,tvTotalAmtAfterRoundoff;
	private float totalDiscount = 0.0f, totalIPrice = 0.0f;
	private HashMap<String, DiscountDO> hmPromoDisc;
	float totalTaxAmt=0;
	///private int precision=0;
	double taxAmt=0.0f;
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout
		llOrder_List = (LinearLayout)getLayoutInflater().inflate(R.layout.order_details_list, null);
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
		{
			btnPrint.setVisibility(View.GONE);
			if(objOrders.pushStatus == 10 || objOrders.pushStatus == 3)
			{
				btnRefreshOrder.setVisibility(View.VISIBLE);
				btnRefreshOrder.setText("Preview");
				btnRefreshOrder.setTag("Preview");
			}
			else
			{
				btnRefreshOrder.setVisibility(View.VISIBLE);
				btnRefreshOrder.setText("Refresh Order");
				btnRefreshOrder.setTag("Refresh Order");
			}
		}
		
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
									Intent intent = new Intent(SalesmanOrderDetail.this, SalesManTakeOrder.class);
									intent.putExtra("orderid", objOrders);
									intent.putExtra("mallsDetails", mallsDetails);
									startActivity(intent);
								}
								else
									showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Payment has been made for this invoice with receipt number - "+receiptNumber +". Please delete the receipt before modifying invoice.", "Delete Receipt", "Cancel", "DeleteReceipt");
								hideLoader();
							}
						});
					}
				}).start();
			}
		});
		
		btnRefreshOrder.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if(arg0.getTag().toString().equalsIgnoreCase("Refresh Order"))
				{
					if(objOrders.OrderId != null && !objOrders.OrderId.trim().equalsIgnoreCase(""))
					{
						showLoader("Please wait...");
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								uploadData(AppStatus.POST_ORDER, AppStatus.TODAY_DATA);
								final int isHoldeOrderAproved = getHoldOrderStatus(objOrders.OrderId, objOrders.strUUID);
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										hideLoader();
										if(isHoldeOrderAproved == 10)
											showCustomDialog(SalesmanOrderDetail.this, "Messagse", "Your On-Hold order is approved.", "OK", null, "approved");
										else if(isHoldeOrderAproved == -10)
											showCustomDialog(SalesmanOrderDetail.this, "Warning!", "Your On-Hold order is rejected.", "OK", null, "rejected");
										else
											showCustomDialog(SalesmanOrderDetail.this, "Warning!", "This order is in On-Hold status, Please wait for approvement.", "OK", null, "");
									}
								});
							}
						}).start();
					}
				}
				else
					gotoPrviewScreen();
			}
		});
		
		if(objOrders != null)
		{
			tvtvOrderNoVal.setText(objOrders.OrderId+"");
			tvtvOrderDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.InvoiceDate)+"");
			tvDeliveryDateVal.setText(CalendarUtils.getFormatedDatefromString(objOrders.DeliveryDate)+"");
			
			if(objOrders.pushStatus == -10)
				tvOrderStatusVal.setText("REJECTED");
			else if(objOrders.TRXStatus.equalsIgnoreCase(AppStatus.TRX_STATUS_HOLD))
				tvOrderStatusVal.setText("ON-HOLD");
			else if(objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				tvOrderStatusVal.setText("RECEIVED");
			else
				tvOrderStatusVal.setText("DELIVERED");
			
			tvBatch_SourceNameValue.setText(objOrders.Batch_Source_Name+"");
			tvCustTrxTypeNameValue.setText(objOrders.Trx_Type_Name+"");
//			tvTotalAmtHVal.setText(diffAmt.format(objOrders.TotalAmount)+""); // Here it will Show without VAT
			tvTotalAmtHVal.setText(diffAmt.format(objOrders.TotalAmountWithVat)+"");
			tvRoundoffVAl.setText(diffAmt.format(objOrders.roundOffVal));
			tvTotalAmtAfterRoundoff.setText(diffAmt.format(objOrders.TotalAmountWithVat+objOrders.roundOffVal));

			if(objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)){
				llAmtwithTax.setVisibility(View.GONE);
				llTotalinvoiceAmt.setVisibility(View.GONE);
				llrndoff.setVisibility(View.GONE);
			}

			if(objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder))
				tvOrderTypeVal.setText("Sales Order");
			else if(objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
				tvOrderTypeVal.setText("Replacement Order");
			else if(objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				tvOrderTypeVal.setText("Return Order");
			else 
				tvOrderTypeVal.setText(objOrders.orderType+"");
		}
		
		if((objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)))
			tvHead.setText("Order Details");
		else if((objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)))
			tvHead.setText("Replacement Order Details");
		else
			tvHead.setText("Payment details ");
		
		loadData();
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShowOptionPopup();
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

		llAmtwithTax		=	(LinearLayout)llOrder_List.findViewById(R.id.llAmtwithTax);
		llrndoff			=	(LinearLayout)llOrder_List.findViewById(R.id.llrndoff);
		llTotalinvoiceAmt	=	(LinearLayout)llOrder_List.findViewById(R.id.llTotalinvoiceAmt);

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
		tvTotalAmtHVal		=	(TextView)llOrder_List.findViewById(R.id.tvTotalAmtHVal);
		tvBatch_SourceNameValue =	(TextView)llOrder_List.findViewById(R.id.tvBatch_SourceNameValue);
		tvCustTrxTypeNameValue	=	(TextView)llOrder_List.findViewById(R.id.tvCustTrxTypeNameValue);
		tvRoundoffVAl	=	(TextView)llOrder_List.findViewById(R.id.tvRoundoffVAl);// Added To Show the Round off value in header
		tvTotalAmtAfterRoundoff	=	(TextView)llOrder_List.findViewById(R.id.tvTotalAmtAfterRoundoff);// Added To Show the Round off value in header
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
		lvReturnorder 		= 	new ListView(SalesmanOrderDetail.this);
		if(objOrders!=null && (objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))))
			llItemHeader.setVisibility(View.VISIBLE);
		else if(objOrders != null )
		{  
			llPaymentLayout.setVisibility(View.VISIBLE);
			
			if(llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			LinearLayout llPayment 			= (LinearLayout) inflater.inflate(R.layout.payment_detail_cell, null);
			//getting Id's
			TextView tvInvoiceNumber 		=	(TextView)llPayment.findViewById(R.id.tvInvoiceNumber);
			TextView tvAmountCell 			=	(TextView)llPayment.findViewById(R.id.tvAmountCell);
			TextView tvPaidAmount			=	(TextView)llPayment.findViewById(R.id.tvPaidAmount);
			TextView tvPaymentType			=	(TextView)llPayment.findViewById(R.id.tvPaymentType);
			TextView tvPaymentTypeTitle		=	(TextView)llPayment.findViewById(R.id.tvPaymentTypeTitle);
			
			tvPaymentType.setText(objOrders.orderType);
			tvInvoiceNumber.setText("Invoice number: "+objOrders.InvoiceNumber);
			tvAmountCell.setText(""+objOrders.TotalAmount);
			tvPaidAmount.setText(""+objOrders.BalanceAmount);
			
			llLayoutMiddle.addView(llPayment , LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		}
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnPrint.setVisibility(View.VISIBLE);
		blockMenu();
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
			ProductDO orderDetail = vecOrderList.get(position);
			
			//inflate invoice_list_cell layout here
			if(convertView == null)
				convertView			=	(LinearLayout)getLayoutInflater().inflate(R.layout.invoice_list_cell_new,null);
			
			//getting id's here
			TextView tvProductKey	=	(TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName	=	(TextView)convertView.findViewById(R.id.tvVendorName);
			EditText etInvoice1		=	(EditText)convertView.findViewById(R.id.etInvoice1);
			EditText etInvoice2		=	(EditText)convertView.findViewById(R.id.etInvoice2);
			
			EditText NetAmt			=	(EditText)convertView.findViewById(R.id.NetAmt);
			EditText TotalAmt		=	(EditText)convertView.findViewById(R.id.TotalAmt);
			EditText TotalTAXAmt		=	(EditText)convertView.findViewById(R.id.TotalTAXAmt);
			EditText tvDisAmt		=	(EditText)convertView.findViewById(R.id.tvDisAmt);
			TextView tvReason		=	(TextView)convertView.findViewById(R.id.tvReason);
			
			convertView.setTag(orderDetail);
			
			tvProductKey.setText(orderDetail.SKU);
			tvVendorName.setText(orderDetail.SKU+" / "+orderDetail.Description);
			etInvoice1.setText(orderDetail.UOM);
			etInvoice2.setText(""+orderDetail.units);
			
			NetAmt.setText(""+diffAmt.format(Math.abs(orderDetail.itemPrice)));
			TotalAmt.setText(""+diffAmt.format(orderDetail.DiscountAmt));
			TotalTAXAmt.setText(""+diffAmt.format(orderDetail.LineTaxAmount)); //Added For TAX
			tvDisAmt.setText(""+diffAmt.format(Math.abs(orderDetail.invoiceAmount)));
			
			etInvoice1.setEnabled(false);
			etInvoice2.setEnabled(false);
			NetAmt.setEnabled(false);
			TotalAmt.setEnabled(false);
			TotalTAXAmt.setEnabled(false);
			tvDisAmt.setEnabled(false);
			
			if(objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
			{
				tvReason.setVisibility(View.VISIBLE);
				tvReason.setText("Reason : "+orderDetail.reason +" | Lot No. : "+orderDetail.LotNumber +" | Exp. : "+CalendarUtils.getFormatedDatefromString(orderDetail.strExpiryDate));
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT , (int)(65 * BaseActivity.px)));
			}
			else
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT , (int)(55 * BaseActivity.px)));
			
			setTypeFace((ViewGroup)convertView);
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
			Intent intent = new Intent(SalesmanOrderDetail.this, SalesManTakeOrder.class);
			intent.putExtra("orderid", objOrders);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("DeleteReceipt"))
		{
			deleteReceipt(receiptNumber);
		}
		else if(from.equalsIgnoreCase("approved"))
		{
			btnRefreshOrder.setText("Preview");
			btnRefreshOrder.setTag("Preview");
		}
		else if(from.equalsIgnoreCase("rejected"))
		{
			onButtonYesClick("served");
		}
	}
	
	/*@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
			super.onBackPressed();
	}*/
	
	int count = 0;
	boolean isDeleted = false;
	public void deleteReceipt(final String receiptNumber)
	{
		showLoader(getResources().getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String status = new CommonDA().getReceiptStatus(receiptNumber);
				count = 0;
				if(status != null && status.equalsIgnoreCase("1"))
				{
					if(isNetworkConnectionAvailable(SalesmanOrderDetail.this))
					{
						OrderDeleteParser orderDelete = new OrderDeleteParser(SalesmanOrderDetail.this);
						new ConnectionHelper(SalesmanOrderDetail.this).sendRequest_Bulk(SalesmanOrderDetail.this, BuildXMLRequest.deleteReceipt(receiptNumber),orderDelete,ServiceURLs.DELETE_RECEIPT_FROMAPP, preference);
						if(orderDelete.getOrderDeleteStatus())
							count = 0;
						else
							count = -1;
					}
					else
						count = 10;
				}
				
				if(count == 0)
				{
					isDeleted = new CommonDA().updatePendingInvoices(receiptNumber);
					isDeleted = new CommonDA().deleteReceipt(receiptNumber);
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(count == 10)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), getString(R.string.no_internet), getResources().getString(R.string.OK), null, "");
						else if(isDeleted)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.successful), "Receipt deleted successfully.", getResources().getString(R.string.OK), null, "deleted",false);
						if(count == 1)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Receipt is already pushed to oracle.", getResources().getString(R.string.OK), null, "");
						else if(count == -1)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Error occured while deleting Receipt. Please try again.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private Vector<ProductDO> getMainProducts()
	{
		Vector<ProductDO> vecMainProducts = new Vector<ProductDO>();
		
		if(vecOrdProduct != null && vecOrdProduct.size() > 0)
		{
			for (ProductDO orderDetailsDO : vecOrdProduct) 
			{
				ProductDO productDO = new ProductDO(); 
				productDO.SKU = orderDetailsDO.SKU;
				productDO.preUnits = orderDetailsDO.units+"";
				
				vecMainProducts.add(productDO);
			}
		}
		
		return vecMainProducts;
	}
	
	private void loadData()
	{
		if(objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) 
				|| (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)
				|| objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)) )
		{
			showLoader(getResources().getString(R.string.loading));
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					//precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
					vecOrdProduct	=	new OrderDetailsDA().getOrderDetails(objOrders, null);
					hmPromoDisc	    = new CaptureInventryDA().getPromoDisocunt(mallsDetails.site);
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
	}
	
	private void gotoPrviewScreen()
	{
		showLoader("Please wait..");
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				errorItems = "";
				HashMap<String, HHInventryQTDO> hmInventory  = new OrderDetailsDA().getAvailInventoryQtys_Temp(objOrders.orderType);
				HashMap<String, Float> hmConversion = new OrderDetailsDA().getUOMFactor();
				
				if(objOrders != null && objOrders.OrderId != null)
					AppConstants.hmCapturedInventory  = new CaptureInventryDA().getOrder_LPO_Detail(objOrders.OrderId, hmInventory, CaptureInventryDA.CONFIRM_HOLD_ORDER, mallsDetails.priceList, objOrders.orderSubType, hmConversion);
				
				if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
				{
					Set<String> set = AppConstants.hmCapturedInventory.keySet();
					int count = 1;
					for(String key : set)
					{
						Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
						for(ProductDO objProductDO : vecOrderedProduct)
						{
							objProductDO.LineNo 			=  ""+count++;
							objProductDO.quantityBU 		=   getQuantityBU(objProductDO, hmConversion);
							
							boolean isAvail = isInventoryAvail(objProductDO, hmInventory);
							if(!isAvail)
								errorItems = errorItems+objProductDO.SKU+", ";
							
							if(!objProductDO.isPromotional)
							{
								DiscountDO temp 		= new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
								DiscountDO objDiscount  = getApplicableDiscounts(objProductDO, mallsDetails, hmPromoDisc,temp);
								if(objDiscount == null)
									objDiscount = new DiscountDO();
								
								if(temp != null)
								{
									objDiscount.perCaseValue = temp.perCaseValue;
									objDiscount.TaxGroupCode = temp.TaxGroupCode;
									objDiscount.TaxPercentage = temp.TaxPercentage;
								}
								else
								{
									objDiscount.perCaseValue  = 0+"";
									objDiscount.TaxGroupCode  = 0+"";
									objDiscount.TaxPercentage = 0;
								}
								
								objProductDO.discountPercent = objDiscount.discount;
								if(objDiscount.discountType == AppStatus.DISCOUNT_PERCENTAGE)
									objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100);
								else
									objProductDO.discountAmount = StringUtils.getDouble(objProductDO.preUnits) * objDiscount.discount;
								
								objProductDO.TaxGroupCode		=	objDiscount.TaxGroupCode;
								objProductDO.TaxPercentage		=	objDiscount.TaxPercentage;
								objProductDO.inventoryQty		=	0;	
								objProductDO.itemPrice  		= 	StringUtils.getDouble(objDiscount.perCaseValue);//objDiscount.fPricePerCase + objDiscount.fDiscountAmt;
								objProductDO.totalPrice 		= 	StringUtils.getDouble(objProductDO.preUnits) * StringUtils.getFloat(objDiscount.perCaseValue);
								objProductDO.unitSellingPrice 	= 	StringUtils.getFloat(objDiscount.perCaseValue) - StringUtils.getFloat(objDiscount.perCaseValue)* (objDiscount.discount/100);
								objProductDO.invoiceAmount 		= 	StringUtils.getDouble(diffAmt.format(objProductDO.unitSellingPrice*StringUtils.getFloat(objProductDO.preUnits)));
								
								objProductDO.discountDesc  		= 	objDiscount.description;
								
								totalDiscount 		+=	objProductDO.discountAmount;
								totalIPrice 		+= 	objProductDO.invoiceAmount;
								
								if(objDiscount.discountType == AppStatus.DISCOUNT_PERCENTAGE)
									objProductDO.DiscountAmt =  StringUtils.getFloat(objDiscount.perCaseValue)*objDiscount.discount/100;
								else
									objProductDO.DiscountAmt =  objDiscount.discount;
								
								objProductDO.Discount = objDiscount.discount;
								objProductDO.discountType = objDiscount.discountType;
								
								calculatedDiscountOnText(objProductDO);
							}
							else
							{
								objProductDO.discountPercent 	=   0;
								objProductDO.discountAmount  	=   0.0;
								
								objProductDO.TaxGroupCode		=   "";
								objProductDO.TaxPercentage		=	0;
								objProductDO.inventoryQty		=	0;	
								objProductDO.itemPrice  		= 	0;
								objProductDO.totalPrice 		= 	0.0;
								objProductDO.unitSellingPrice 	= 	0;
								objProductDO.invoiceAmount 		= 	0.0;
								objProductDO.discountDesc  		= 	"";
								objProductDO.DiscountAmt 		=  	0;
								objProductDO.Discount 			= 	0;
								objProductDO.discountType 		= 	0;
							}
						}
					}
				}
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						
						if(TextUtils.isEmpty(errorItems))
						{
							Intent intent = new Intent(SalesmanOrderDetail.this, SalesmanOrderPreview.class);
							/*intent.putExtra("TotalPrice", totalIPrice);
							intent.putExtra("Discount", totalDiscount);
							intent.putExtra("objOrder",objOrders);
							intent.putExtra("mallsDetails",mallsDetails);
							intent.putExtra("isConfirmOnHoldOrder",true);
							intent.putExtra("isFromHold",true);
							*//*if(vecOrdProduct!=null && vecOrdProduct.size()>0)
							  intent.putExtra("totalTaxAmt",vecOrdProduct.get(0).TotalTax);//added on 03JAN
							//===============================================================*//*
							intent.putExtra("isReturnRequest", true);
							intent.putExtra("TotalOrderPrice",totalIPrice);
							calculateTotalTax(vecOrdProduct);
							intent.putExtra("VatAmount", objOrders.VatAmount);
							intent.putExtra("TotalAmountWithVat",totalIPrice);
							intent.putExtra("ProrataTaxAmount", "0");
							intent.putExtra("TotalTax", totalIPrice);*/

							//===============================================================


							intent.putExtra("isReturnRequest", true);
							intent.putExtra("TotalPrice", ""+totalIPrice);
							intent.putExtra("TotalOrderPrice", ""+(totalIPrice+totalDiscount-objOrders.VatAmount));
							intent.putExtra("Discount", ""+totalDiscount);
							intent.putExtra("VatAmount", ""+objOrders.VatAmount);
							intent.putExtra("TotalAmountWithVat", ""+totalIPrice);
							intent.putExtra("ProrataTaxAmount", "0");
							intent.putExtra("TotalTax", ""+totalIPrice);
							intent.putExtra("objOrder",objOrders);
							intent.putExtra("mallsDetails",mallsDetails);
							intent.putExtra("forHoldOrder",true);
							intent.putExtra("vecOrdProduct",vecOrdProduct);

							//===============================================================
							startActivityForResult(intent, 1000);
							finish();
						}
						else 
						{
							if(errorItems.contains(","))
								errorItems = errorItems.substring(0, errorItems.lastIndexOf(","));
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "For the following item(s) : " +
									"\n"+errorItems+"\navailable quantity is less than ordered quantity. Please goto 'Load Management' and make load request to deliver this order.", "OK", null, "");
						}
					}

					private double calculateTotalTax( Vector<ProductDO> vecOrdProduct) {
					taxAmt=0.0f;

					if(vecOrdProduct!=null) {
						for (ProductDO productDO : vecOrdProduct)
							taxAmt = taxAmt + productDO.LineTaxAmount;
					}
							return taxAmt;
					}


				});
				
			}
		}).start();
	}
	
	PaymentHeaderDO paymentHeaderDO;
	private void performPrinting(final String copy)
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
					paymentHeaderDO = new PaymentDetailDA().getPaymentDetailByInvoiceNumber(objOrders.OrderId);
				HashMap<String,String> hmArabic = new CommonDA().getAllArabicItems(objOrders.OrderId);
				for (ProductDO productDO : vecOrdProduct)
					productDO.Description1=hmArabic.get(productDO.SKU);


				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						ShowOptionPopupForPrinter(SalesmanOrderDetail.this,new PrintPopup() {
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
										intent=new Intent(SalesmanOrderDetail.this, WoosimPrinterActivity.class);
									else if(selectedPrinter==AppConstants.DOTMATRIX)
										intent=new Intent(SalesmanOrderDetail.this, PrinterConnectorArabic.class);
									intent.putExtra("CALLFROM", CONSTANTOBJ.ORDER_SUMMARY);


									intent.putExtra("totalPrice", totalIPrice);
									intent.putExtra("copy", copy);
									intent.putExtra("orderDo",    objOrders);
									intent.putExtra("mallsDetails", mallsDetails);
									intent.putExtra("paymentHeaderDO", paymentHeaderDO);
									startActivity(intent);
									hideLoader();
								}



							}
						});
//						Intent intent = new Intent(SalesmanOrderDetail.this, WoosimPrinterActivity.class);
//						intent.putExtra("CALLFROM", CONSTANTOBJ.ORDER_SUMMARY);
//						intent.putExtra("totalPrice", totalIPrice);
//						intent.putExtra("copy", copy);
//						intent.putExtra("orderDo",    objOrders);
//						intent.putExtra("mallsDetails", mallsDetails);
//						intent.putExtra("paymentHeaderDO", paymentHeaderDO);
//						startActivity(intent);
//						hideLoader();
					}
				});
			}
		}).start();
	}
	
	protected void ShowOptionPopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_language, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanOrderDetail.this, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		final Button btn_popup_English	  = (Button) view.findViewById(R.id.btn_popup_English);
		final Button btn_popup_Arabic	  = (Button) view.findViewById(R.id.btn_popup_Arabic);
		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);

		setTypeFace((ViewGroup)view);
		tv_poptitle.setText("Select Print Type");
		btn_popup_English.setText("Customer Copy");
		btn_popup_Arabic.setText("Duplicate Copy");
		
		btn_popup_English.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final int passcodeEnable = new SettingsDA().getSettingsByName(AppConstants.PASSCODE_CUSTOMER_COPY);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								if(passcodeEnable == AppStatus.ENABLE)
									showPassCodeDialog(null, "", false);
								else
									performPrinting(AppConstants.CUSTOMER_COPY);
							}
						});
					}
				}).start();
				
				mCustomDialog.dismiss();
			}
		});

		btn_popup_Arabic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				performPrinting(AppConstants.DUPLICATE_COPY);
				mCustomDialog.dismiss();
			}
		});
		
		btn_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
			}
		});

		setTypeFace((LinearLayout)view);
		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}
	}
	
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String fromText, boolean isCheckOut)
	{
		performPrinting(AppConstants.CUSTOMER_COPY);
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
