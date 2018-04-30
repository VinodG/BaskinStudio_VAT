package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class CheckInOptionActivity extends BaseActivity
{
	private LinearLayout llCheckINOption, llPricing;
	private JourneyPlanDO mallsDetails ;
	private TextView tvHeadTitle, tvCreditLimitVal, tvAvailableLimitVal, tvOutstandingVal, tvOverdueVal,tv_overDuelimit,tv_outstandinglimit,tv_Avail_limit,tv_creditlimit;
	private LinearLayout btnTakeNewOrder, btnOrderList, btnPendinInvoices, btnAssetScan,llReturnOrder, btnReplacements,
						 btnPaymentSummary, llLPOOrder,btnNoPriceitem;
	private ImageView    ivTakeOrderSap,sepTakeNewOrder,sepReturnNewOrder,sepOrderlist,seppendingInvoice,
						 sepAssetScan, ivReturnOrder, ivlpoOrder;
	private Vector<PendingInvicesDO> vecPendingInvoices;
	private Vector<NameIDDo> vecVehicle;
	@Override
	public void initialize()
	{
		llCheckINOption = (LinearLayout) inflater.inflate(R.layout.custom_popup_check_in, null);
		llBody.addView(llCheckINOption, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		AppConstants.isTaskCompleted = false;
		AppConstants.isServeyCompleted = false;
		
		if(getIntent().getExtras() != null)
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		
		initializeControles();
		
		tvHeadTitle.setText(mallsDetails.siteName  + " ("+mallsDetails.partyName+")");
		
		if(mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			llPricing.setVisibility(View.GONE);
		else if(mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			llPricing.setVisibility(View.VISIBLE);
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equals(AppConstants.SALESMAN_GT))
		{
			ivTakeOrderSap.setVisibility(View.VISIBLE);
			btnTakeNewOrder.setVisibility(View.VISIBLE);
			llReturnOrder.setVisibility(View.VISIBLE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			btnTakeNewOrder.setVisibility(View.GONE);
			btnPendinInvoices.setVisibility(View.GONE);
			sepTakeNewOrder.setVisibility(View.VISIBLE);
			seppendingInvoice.setVisibility(View.GONE);
		}
		else
		{
			ivTakeOrderSap.setVisibility(View.GONE);
			btnTakeNewOrder.setVisibility(View.GONE);
		}
		
		llReturnOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = null;
				intent  =	new Intent(CheckInOptionActivity.this,  SalesManTakeReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("from", "checkINOption");
				startActivity(intent);
			}
		});
		
		btnTakeNewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CheckInOptionActivity.this, SalesManTakeOrder.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("from", "checkin");
				startActivity(intent);
//				isHoldOrderToConfirm(true);
			}
		});
		
		llLPOOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if(vecVehicle == null || vecVehicle.size() == 0)
				{
					showLoader("Please wait...");
					new Thread(new Runnable() {
						@Override
						public void run() 
						{
							vecVehicle	= new CommonDA().getAllSubInventories(preference.getStringFromPreference(Preference.CURRENT_VEHICLE, ""), AppStatus.LOAD_STOCK);
							runOnUiThread(new Runnable() 
							{
								public void run() 
								{
									hideLoader();
									showWareHouse(vecVehicle);
								}
							});
						}
					}).start();
				}
				else
					showWareHouse(vecVehicle);
			}
		});
		
		btnOrderList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent =	new Intent(CheckInOptionActivity.this,  OrderSummary.class);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("isFromCustomerCheckIn", true);
				startActivity(intent);
			}
		});
		btnPendinInvoices.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
				intent.putExtra("arcollection", true);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("fromMenu", true);
				intent.putExtra("AR", true);
				if(mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
					intent.putExtra("isCredit", true);
				startActivity(intent);
			}
		});
		btnAssetScan.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CheckInOptionActivity.this, CustomerAssetsListActivity.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				startActivity(intent);
			}
		});
		
		btnReplacements.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						final int passcodeEnable = new SettingsDA().getSettingsByName(AppConstants.IS_REPLACE_ENABLE);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								if(passcodeEnable == AppStatus.ENABLE || passcodeEnable == AppStatus.NOT_AVAIL)
								{
									Intent intent =	new Intent(CheckInOptionActivity.this, SalesManTakeReplacementOrder.class);
									intent.putExtra("mallsDetails", mallsDetails);
									intent.putExtra("from", "replacement");
									startActivity(intent);
								}
								else
									showCustomDialog(CheckInOptionActivity.this, getString(R.string.warning), "Replacement process has been blocked by administrator, please contact administrator.", "OK", null, "");
							}
						});
					}
				}).start();
			}
		});
		
		btnPaymentSummary.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent =	new Intent(CheckInOptionActivity.this, CustomerInvoiceActivity.class);
				intent.putExtra("mallsDetails", mallsDetails);
				startActivity(intent);
			}
		});
		
		btnNoPriceitem.setOnClickListener(new OnClickListener() {		
			
			@Override		
			public void onClick(View v) {		
				Intent intent =	new Intent(CheckInOptionActivity.this, NoPriceItemActivity.class);		
				intent.putExtra("mallsDetails", mallsDetails);		
				startActivity(intent);		
						
			}		
		});	
		
		setTypeFace(llCheckINOption);
	}
	
	private void showWareHouse(Vector<NameIDDo> vecVehicle)
	{
		if(vecVehicle != null && vecVehicle.size() > 0)
		{
			CustomBuilder builder = new CustomBuilder(CheckInOptionActivity.this, "Select Warehouse", true);
			builder.setSingleChoiceItems(vecVehicle, null, new CustomBuilder.OnClickListener() 
			{
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject) 
				{
					NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
					String lpoVehicle = ""+ObjNameIDDo.strName;
					builder.dismiss();
					
					Intent intent = new Intent(CheckInOptionActivity.this, SalesManTakeOrder.class);
					intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
					intent.putExtra("mallsDetails",mallsDetails);
					intent.putExtra("from", "checkin");
					intent.putExtra("isLPOOrder", true);
					intent.putExtra("lpoVehicle", lpoVehicle);
					startActivity(intent);
	    		}
		   }); 
		   builder.show();
		}
		else
			showCustomDialog(CheckInOptionActivity.this, "Warning!", "Warehouse is not mapped.", "OK", null, "");
	}
	
	private void checkforCreditDays(final float availLimit) 
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				vecPendingInvoices = new ARCollectionDA().getPendingInvoices(mallsDetails, "","");
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						showPendingInvoicePopup(vecPendingInvoices);
					}
				});
			}
		}).start();
	}

	private void initializeControles()
	{
		tvHeadTitle			= (TextView) llCheckINOption.findViewById(R.id.tvHeadTitle);
		btnTakeNewOrder		= (LinearLayout) llCheckINOption.findViewById(R.id.btnTakeNewOrder);
		btnOrderList		= (LinearLayout) llCheckINOption.findViewById(R.id.btnOrderList);
		btnPendinInvoices	= (LinearLayout) llCheckINOption.findViewById(R.id.btnPendinInvoices);
		btnAssetScan		= (LinearLayout) llCheckINOption.findViewById(R.id.btnAssetScan);
		llReturnOrder		= (LinearLayout) llCheckINOption.findViewById(R.id.llReturnOrder);
		btnPaymentSummary	= (LinearLayout) llCheckINOption.findViewById(R.id.btnPaymentSummary);
		btnNoPriceitem	= (LinearLayout) llCheckINOption.findViewById(R.id.btnNoPriceitem);
		
		ivTakeOrderSap		= (ImageView) llCheckINOption.findViewById(R.id.ivTakeOrderSap);
		btnReplacements		= (LinearLayout) llCheckINOption.findViewById(R.id.btnReplacements);
		sepTakeNewOrder		= (ImageView) llCheckINOption.findViewById(R.id.sepTakeNewOrder);
		sepReturnNewOrder	= (ImageView) llCheckINOption.findViewById(R.id.sepReturnNewOrder);
		sepOrderlist		= (ImageView) llCheckINOption.findViewById(R.id.sepOrderlist);
		seppendingInvoice	= (ImageView) llCheckINOption.findViewById(R.id.seppendingInvoice);
		sepAssetScan		= (ImageView) llCheckINOption.findViewById(R.id.sepAssetScan);
		ivReturnOrder		= (ImageView) llCheckINOption.findViewById(R.id.ivReturnOrder);
		
		tvCreditLimitVal	= (TextView) llCheckINOption.findViewById(R.id.tvCreditLimitVal);
		tvAvailableLimitVal = (TextView) llCheckINOption.findViewById(R.id.tvAvailableLimitVal);
		tvOutstandingVal 	= (TextView) llCheckINOption.findViewById(R.id.tvOutstandingVal);
		tvOverdueVal 		= (TextView) llCheckINOption.findViewById(R.id.tvOverdueVal);
		llPricing			= (LinearLayout) llCheckINOption.findViewById(R.id.llPricing);
		
		llLPOOrder			= (LinearLayout) llCheckINOption.findViewById(R.id.llLPOOrder);
		ivlpoOrder			= (ImageView) llCheckINOption.findViewById(R.id.ivlpoOrder);
		
		tv_overDuelimit	= (TextView) llCheckINOption.findViewById(R.id.tv_overDuelimit);
		tv_outstandinglimit	= (TextView) llCheckINOption.findViewById(R.id.tv_outstandinglimit);
		tv_Avail_limit	= (TextView) llCheckINOption.findViewById(R.id.tv_Avail_limit);
		tv_creditlimit	= (TextView) llCheckINOption.findViewById(R.id.tv_creditlimit);

		
		llLPOOrder.setVisibility(View.VISIBLE);
		ivlpoOrder.setVisibility(View.VISIBLE);
		
		tv_overDuelimit.setText("Overdue ("+curencyCode+")");
		tv_outstandinglimit.setText("Outstanding ("+curencyCode+")");
		tv_Avail_limit.setText("Avail. Limit ("+curencyCode+")");
		tv_creditlimit.setText("Credit Limit ("+curencyCode+")");

		btnReplacements.setVisibility(View.GONE);

	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		AppConstants.isServeyCompleted = false;
		
		if(mallsDetails!= null 
				&& mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			loadCreditCustomerLimit();
		
//		isHoldOrderToConfirm(false);
	}
	
	@Override
	public void onBackPressed() 
	{
		showCustomDialog(this, getString(R.string.warning), "Do you want to check out?", "Yes", "No", "checkout");
	}
	
	private void loadCreditCustomerLimit()
	{
		showLoader("Checking credit limit...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final CustomerCreditLimitDo creditLimit = 	new CustomerDA().getCustomerCreditLimit(mallsDetails);
				final float overDue 					= 	new CustomerDA().getOverDueAmount(mallsDetails); 
				final int value 						= 	new SettingsDA().getSettingsByName(AppConstants.CREDIT_DAYS_WHILE_ORDER);
				
				if((value == AppStatus.ENABLE && overDue > 0) 
						|| (creditLimit != null && StringUtils.getFloat(creditLimit.availbleLimit) <= 0))
					if(creditLimit != null && StringUtils.getFloat(creditLimit.outStandingAmount) > 0)
						checkforCreditDays(StringUtils.getFloat(creditLimit.availbleLimit));
				
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						hideLoader();
						if(creditLimit != null)
						{
							tvCreditLimitVal.setText(""+diffAmt.format(StringUtils.getFloat(creditLimit.creditLimit)));
							tvAvailableLimitVal.setText(""+diffAmt.format(StringUtils.getFloat(creditLimit.availbleLimit)));
							tvOutstandingVal.setText(""+diffAmt.format(StringUtils.getFloat(creditLimit.outStandingAmount)));
							tvOverdueVal.setText(""+diffAmt.format(StringUtils.getFloat(""+overDue)));
						}
					}
				});
			}
		}).start();
	}
	
//	private void isHoldOrderToConfirm(final boolean performAction)
//	{
//		if(performAction)
//			showLoader("Please wait...");
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run() 
//			{
//				final String order_ID = new CommonDA().isHoldOrderIsThere(CalendarUtils.getOrderPostDate(), mallsDetails.site);
//				
//				runOnUiThread(new Runnable()
//				{
//					@Override
//					public void run() 
//					{
//						if(!TextUtils.isEmpty(order_ID))
//							showCustomDialog(CheckInOptionActivity.this, getString(R.string.warning), "Hold Order no : "+order_ID +" not confirmed, please go to order list and confirm.", getString(R.string.OK), "", "confirm");
//						else if(performAction)
//						{
//							Intent intent = new Intent(CheckInOptionActivity.this, SalesManTakeOrder.class);
//							intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//							intent.putExtra("mallsDetails",mallsDetails);
//							intent.putExtra("from", "checkin");
//							startActivity(intent);
//						}
//						
//						hideLoader();
//					}
//				});
//			}
//		}).start();
//	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		
		if(from.equalsIgnoreCase("confirm"))
			btnOrderList.performClick();
	}
	
	CustomDialog pendingInvoicesPopup;
	public void showPendingInvoicePopup(Vector<PendingInvicesDO> vecPendingInvoices)
	{
		if(pendingInvoicesPopup != null && pendingInvoicesPopup.isShowing())
			pendingInvoicesPopup.dismiss();
		
		View pendingInvoices 		= inflater.inflate(R.layout.pending_invoice_popup, null);
		
		pendingInvoicesPopup = new CustomDialog(CheckInOptionActivity.this, pendingInvoices, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		
		pendingInvoicesPopup.setCancelable(false);
		
		ListView lvpendingInvoices 	= (ListView) pendingInvoices.findViewById(R.id.lvPendingInvoices);
		Button btnCheckOut 			= (Button) pendingInvoices.findViewById(R.id.btnNoPopup);
		Button btnOk 				= (Button) pendingInvoices.findViewById(R.id.btnYesPopup);
		TextView tvMessagePopup 	= (TextView) pendingInvoices.findViewById(R.id.tvMessagePopup);
		TextView tvNoData 			= (TextView) pendingInvoices.findViewById(R.id.tvNoData);

		String availLimit = ""+tvAvailableLimitVal.getText().toString();
		String overdue    = ""+tvOverdueVal.getText().toString();
		
		if(mallsDetails.Max_Days_Past_Due > 0)
			tvMessagePopup.setText("This customer is having available credit limit as : "+mallsDetails.currencyCode+" "+availLimit +"" +
							   " and overdue amount as "+mallsDetails.currencyCode+ " "+overdue +". " +
							   	"This customer is having "+mallsDetails.Max_Days_Past_Due+" days as additional credit days. Please press 'Continue' button to continue.");
		else
			tvMessagePopup.setText("This customer is having available credit limit as : "+mallsDetails.currencyCode+" "+availLimit +"" +
					   " and overdue amount as "+mallsDetails.currencyCode+ " "+overdue +". " +
					   	"While placing order you have to place order as ON HOLD. Do you want to collect payment?");
		if(vecPendingInvoices == null || vecPendingInvoices.size() <= 0)
		{
			tvNoData.setVisibility(View.VISIBLE);
			lvpendingInvoices.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoData.setVisibility(View.GONE);
			lvpendingInvoices.setAdapter(new PendingInvoiceAdapter(vecPendingInvoices));
		}
		
		if(mallsDetails.Max_Days_Past_Due > 0)
			btnOk.setVisibility(View.GONE);
		else
			btnOk.setVisibility(View.VISIBLE);
		
		btnOk.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				pendingInvoicesPopup.dismiss();
				v.setClickable(false);
				v.setEnabled(false);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						v.setClickable(true);
						v.setEnabled(true);
					}
				}, 400);
				Intent intent = new Intent(CheckInOptionActivity.this, PendingInvoices.class);
				intent.putExtra("arcollection", true);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("fromMenu", true);
				intent.putExtra("AR", true);
				if(isCreditCustomer(mallsDetails))
					intent.putExtra("isCredit", true);
				startActivity(intent);
			}
		});
		
		if(mallsDetails.Max_Days_Past_Due > 0)
			btnCheckOut.setText("Continue");
		else
			btnCheckOut.setText("Skip");
		
		btnCheckOut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				pendingInvoicesPopup.dismiss();
			}
		});
		
		try 
		{
			setTypeFace((LinearLayout)pendingInvoices);
			if (!pendingInvoicesPopup.isShowing())
				pendingInvoicesPopup.show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	public class PendingInvoiceAdapter extends BaseAdapter
	{
		private Vector<PendingInvicesDO> vctPendingInvicesDOs;
		public PendingInvoiceAdapter(Vector<PendingInvicesDO> vecPendingInvicesDOs) 
		{
			vctPendingInvicesDOs = vecPendingInvicesDOs;
		}

		@Override
		public int getCount() 
		{
			if(vctPendingInvicesDOs != null && vctPendingInvicesDOs.size() > 0)
				return vctPendingInvicesDOs.size();
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
			PendingInvicesDO pendingInvicesDO = vctPendingInvicesDOs.get(arg0);
			LinearLayout llLayout 	= (LinearLayout)getLayoutInflater().inflate(R.layout.pending_invoice_cell, null);
			TextView tvAmount		= (TextView)llLayout.findViewById(R.id.tvAmount);
			TextView tvBalance		= (TextView)llLayout.findViewById(R.id.tvBalance);
			TextView tvDueDate		= (TextView)llLayout.findViewById(R.id.tvDueDate);
			TextView tvInvoiceName	= (TextView)llLayout.findViewById(R.id.tvInvoiceName);
			TextView tvInvoiceDate	= (TextView)llLayout.findViewById(R.id.tvInvoiceDate);
			TextView tvInvoiceType	= (TextView)llLayout.findViewById(R.id.tvInvoiceType);
			
			tvInvoiceDate.setText(CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate));
			tvBalance.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance)));
			tvAmount.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
			tvInvoiceName.setText(pendingInvicesDO.invoiceNo);
			tvDueDate.setText(CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate));
			
			if(StringUtils.getFloat(pendingInvicesDO.balance) < 0)
				tvInvoiceName.setTextColor(getResources().getColor(R.color.green));
			else if(pendingInvicesDO.IsOutStanding.equalsIgnoreCase("true"))
				tvInvoiceName.setTextColor(getResources().getColor(R.color.red));
			
			if(StringUtils.getFloat(pendingInvicesDO.balance) < 0)
				tvInvoiceType.setText(AppConstants.RETURN_INV);
			else
				tvInvoiceType.setText(AppConstants.SALES_INV);
			
			setTypeFace(llLayout);
			return llLayout;
		}
	}
}
