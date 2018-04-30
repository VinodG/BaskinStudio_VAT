package com.winit.baskinrobbin.pinch;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.WoosimPrinterActivity;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

@SuppressLint("ValidFragment")
public class ListPaymentFragment extends Fragment
{
	private ListView lvOrderList;
	private InvoiceAdapter invoiceAdapter;
	private ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice;
	private Context context;
	private TextView tvNoDataFound;
	private LinearLayout llMain;
	private HashMap<String, String> hsTotal;
	private String curencyCode; 
	
	public ListPaymentFragment(Context context, ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice, HashMap<String, String> hsTotal, String curencyCode)
	{
		this.arrayListCustomerInvoice = arrayListCustomerInvoice;
		this.context = context;
		this.hsTotal = hsTotal;
		this.curencyCode = curencyCode;
	}
	public ListPaymentFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 	= (LinearLayout) inflater.inflate(R.layout.listview,null);
		initializeControls();
		
		((BaseActivity)context).setTypeFace(llMain);
		return llMain;
	}
	
	private void initializeControls()
	{
		
		tvNoDataFound		=	(TextView)llMain.findViewById(R.id.tvNoDataFound);
		lvOrderList			=	(ListView)llMain.findViewById(R.id.lv);
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setScrollbarFadingEnabled(true);
		
		if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
		{
			invoiceAdapter = new InvoiceAdapter(arrayListCustomerInvoice);
			lvOrderList.setAdapter(invoiceAdapter);
					
			tvNoDataFound.setVisibility(View.GONE);
			lvOrderList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoDataFound.setVisibility(View.VISIBLE);
			lvOrderList.setVisibility(View.GONE);
		}
	}
	
	class InvoiceAdapter extends BaseAdapter
	{
		private ArrayList<Customer_InvoiceDO> vecCustomerInvoiceDOs;
		
		public InvoiceAdapter(ArrayList<Customer_InvoiceDO> vecCustomerInvoiceDOs) 
		{
			this.vecCustomerInvoiceDOs = vecCustomerInvoiceDOs;
		}

		@Override
		public int getCount()
		{
			if(vecCustomerInvoiceDOs != null && vecCustomerInvoiceDOs.size() > 0)
				return vecCustomerInvoiceDOs.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
				convertView = LayoutInflater.from(context).inflate(R.layout.customer_invoice_cell, null);
			
			Customer_InvoiceDO object = vecCustomerInvoiceDOs.get(position);
			
			TextView tvPaymentType          = (TextView) convertView.findViewById(R.id.tvPaymentType);
			TextView tvCustomerName         = (TextView) convertView.findViewById(R.id.tvCustomerName);
			TextView tvCustomerSiteId       = (TextView) convertView.findViewById(R.id.tvCustomerSiteId);
			TextView tvTotalInvoiceAmmount  = (TextView) convertView.findViewById(R.id.tvTotalInvoiceAmmount);
			LinearLayout llInvoiceDetails 	= (LinearLayout) convertView.findViewById(R.id.llInvoiceDetails);
			TextView tvDocNo 				= (TextView) convertView.findViewById(R.id.tvDocNo);
			TextView tvPaymentDocNo 		= (TextView) convertView.findViewById(R.id.tvPaymentDocNo);
			TextView tvCouponNo				= (TextView) convertView.findViewById(R.id.tvCouponNo);
			View sideView					= (View) convertView.findViewById(R.id.sideView);
			ImageView ivPrint				= (ImageView) convertView.findViewById(R.id.ivPrint);
			
			String strTotal = "";
			if(hsTotal != null && hsTotal.size() > 0 && hsTotal.containsKey(object.reciptType))
				strTotal = hsTotal.get(object.reciptType);
			
			tvPaymentType.setText(object.reciptType.toUpperCase() + (strTotal != "" ? "     Total Amount: "+strTotal+curencyCode+"" : ""));
			
			tvCustomerName.setText(object.siteName);
			tvCustomerSiteId.setText("("+object.customerSiteId+")");
			
			if(object.receiptNo != null && !object.receiptNo.equalsIgnoreCase(""))
				tvDocNo.setText("Receipt No. : "+object.receiptNo);
			else
				tvDocNo.setText("Receipt No. : "+object.payment_Id);
			
			tvPaymentType.setVisibility(View.GONE);
			
			tvPaymentDocNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			if(object.reciptType.equalsIgnoreCase("cheque"))
				tvPaymentDocNo.setText("Cheque No. : "+object.chequeNo);
			else if(object.reciptType.equalsIgnoreCase("cash"))
				tvPaymentDocNo.setVisibility(View.GONE);
			else if(object.reciptType.equalsIgnoreCase("credit"))
				tvPaymentDocNo.setText("XXXX-XXXX-XXXX-XXXX-"+object.creditCardNo.substring(object.creditCardNo.length()-4));
			
			tvTotalInvoiceAmmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			if(!object.reciptType.equalsIgnoreCase("coupon"))
			{
				tvTotalInvoiceAmmount.setText(curencyCode+" "+object.invoiceTotal);
				tvCouponNo.setVisibility(View.GONE);
			}
			else
			{
				tvTotalInvoiceAmmount.setText(curencyCode+" "+object.couponAmount);
				tvCouponNo.setVisibility(View.VISIBLE);
				tvCouponNo.setText("Coupon No - "+object.couponNo);
				tvDocNo.setText("Receipt No. : "+object.payment_Id);
			}
			
			
			if(object.status == 1 || object.status == 2)
				sideView.setBackgroundColor(getResources().getColor(R.color.customer_served));
			else
				sideView.setBackgroundColor(getResources().getColor(R.color.red));
			
			llInvoiceDetails.removeAllViews();
			for(int i=0; object.vecPaymentDetailDOs != null &&  i<object.vecPaymentDetailDOs.size(); i++ )
			{
				 PaymentDetailDO objDetailDO 	    = 	object.vecPaymentDetailDOs.get(i);
				 LinearLayout llPaymentDetailCell 	= 	(LinearLayout) LayoutInflater.from(context).inflate(R.layout.payment_details_cell, null);
				 TextView tvInvoiceNumber 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceNumber);
				 TextView tvInvoiceAmmount 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceAmmount);
				 TextView tvInvoiceAmmountLabel		= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceAmmountLabel);
				 TextView tvInvoiceNumberLabel		= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceNumberLabel);
				 tvInvoiceNumber.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				 tvInvoiceAmmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				 
				 tvInvoiceAmmountLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				 tvInvoiceNumberLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				 
				 float amt = StringUtils.getFloat(objDetailDO.invoiceAmount);
				 String INV_TYPE = "";
				 
				 if(objDetailDO.invoiceNumber.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
					 INV_TYPE = AppConstants.OPEN_CREDIT;
				 else if(amt > 0)
					 INV_TYPE = AppConstants.SALES_INV;
				 else
					 INV_TYPE = AppConstants.RETURN_INV;
				 
				 tvInvoiceAmmountLabel.setText(INV_TYPE+"");
				 tvInvoiceAmmount.setText(""+amt);
				 tvInvoiceNumber.setText(objDetailDO.invoiceNumber);
				 
				 llInvoiceDetails.addView(llPaymentDetailCell, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			}
			
			ivPrint.setTag(object);
			ivPrint.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					final Customer_InvoiceDO object = (Customer_InvoiceDO) v.getTag(); 
					((BaseActivity)context).showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							final ArrayList<JourneyPlanDO> vec = new CustomerDetailsDA().getJourneyPlanForTeleOrder(object.customerSiteId);
							
							((BaseActivity)context).runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									if(vec != null && vec.size() > 0)
									{
										((BaseActivity)context).ShowOptionPopupForPrinter(context,new BaseActivity.PrintPopup() {
											@Override
											public void selectedOption(int selectedPrinter)
											{

												Intent intent=null;
												if(selectedPrinter==AppConstants.CANCEL)
												{
													((BaseActivity)context).hideLoader();

												}
												else
												{
													if(selectedPrinter==AppConstants.WOOSIM)
														intent=new Intent(context, WoosimPrinterActivity.class);
													else if(selectedPrinter==AppConstants.DOTMATRIX)
														intent=new Intent(context, PrinterConnectorArabic.class);
													intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SEP_SUMMARY);
													intent.putExtra("object", object);
													intent.putExtra("mallsDetails", vec.get(0));
													startActivityForResult(intent, 1000);
													((BaseActivity)context).hideLoader();
												}

											}
										});
//										Intent intent = new Intent(context, WoosimPrinterActivity.class);
//										intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SEP_SUMMARY);
//										intent.putExtra("object", object);
//										intent.putExtra("mallsDetails", vec.get(0));
//										startActivityForResult(intent, 1000);
									}
									
									((BaseActivity)context).hideLoader();
								}
							});
							
						}
					}).start();
				}
			});
			
			((BaseActivity)context).setTypeFace((ViewGroup) convertView);
			return convertView;
		}
		
		public void refresh(ArrayList<Customer_InvoiceDO> vecCustomerInvoiceDOs)
		{
			this.vecCustomerInvoiceDOs = vecCustomerInvoiceDOs;
			
			if(vecCustomerInvoiceDOs != null && vecCustomerInvoiceDOs.size() > 0)
				tvNoDataFound.setVisibility(View.GONE);
			else
				tvNoDataFound.setVisibility(View.VISIBLE);
			
			invoiceAdapter.notifyDataSetChanged();
		}
	}
}