package com.winit.baskinrobbin.salesman;
import java.util.ArrayList;
import java.util.Vector;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;


public class PendingInvoices extends BaseActivity
{
	//declaration of variables
	private LinearLayout llArCollection,llMiddleLayout,llNxt, llInvoice;
	private TextView tvTotalAmount, tvTotalAmountValue , tvTotalAmountDue, tvTotalDueValue ,tvAmountDue, tvUnpaid, tvARHead ,
				     tvCreditLimit, tvAmount, tvSitename, tvAddress, tvCreditLimitValue, tvCreditTime, tvCreditTimeValue, 
				     tvNoPendingInvoices, tvTotalAmt, tvPaidAmt;
	private  TextView tvAmt1,tvAmt2,tvAmt3;
	private Button btnNxt, btnAddInvoices,btnOnAccount;
	private UnPaidInvoiceAdapter objUnPaidInvoiceAdapter;
	private ListView lvUnPaidInvoices;
	private EditText etSearchText;
	private float  amountDue = 0 , selectedAmount = 0,captureamt=0;
	private ARCollectionDA arCollectionBL;
	private Vector<PendingInvicesDO> vecPendingInvoices;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers ;
	private View etInvoiceNumber = null;
	private CustomDialog tempcustomDialogs;
	private JourneyPlanDO mallsDetails;
	private boolean isARCollection = false, isPartial;
	private float fToPay, roundOff;
	private String strOrderId, LPO;
	private final String ON_ACCOUNT_PAYMENT = "ON ACCOUNT PAYMENT";
	
	@Override
	public void initialize()
	{
		llArCollection 		= (LinearLayout)getLayoutInflater().inflate(R.layout.ar_collection, null);
		llBody.addView(llArCollection,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		intialiseControls();
		setTypeFace(llArCollection);
		//this vector will hold all the invoice selected from the AR collection and current invoice number
		arrInvoiceNumbers 	= new ArrayList<PendingInvicesDO>();
		arCollectionBL		= new ARCollectionDA();
		
		//getting extras
		if(getIntent().getExtras()!= null)
		{
			mallsDetails		= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isARCollection 		= 	getIntent().getExtras().getBoolean("AR");
			fToPay 				= 	getIntent().getExtras().getFloat("fToPay");
			LPO				    =	getIntent().getExtras().getString("LPO");
			strOrderId	        =	getIntent().getExtras().getString("OrderId");
			roundOff	        =	getIntent().getExtras().getFloat("roundOffVal");
		}

		if(TextUtils.isEmpty(curencyCode))
			curencyCode = mallsDetails.currencyCode;
		
		vecPendingInvoices  = new Vector<PendingInvicesDO>();
		lvUnPaidInvoices = new ListView(PendingInvoices.this);
		lvUnPaidInvoices.setCacheColorHint(0);
		lvUnPaidInvoices.setVerticalFadingEdgeEnabled(false);
		lvUnPaidInvoices.setSelector(R.color.transparent);
		lvUnPaidInvoices.setVerticalScrollBarEnabled(false);
		lvUnPaidInvoices.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		objUnPaidInvoiceAdapter = new UnPaidInvoiceAdapter(new Vector<PendingInvicesDO>());
		lvUnPaidInvoices.setAdapter(objUnPaidInvoiceAdapter);
		
		showLoader(getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				vecPendingInvoices  = arCollectionBL.getPendingInvoices(mallsDetails, strOrderId,"ARCollection");
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
					if(vecPendingInvoices.size()>0){
						llHeader.setVisibility(View.VISIBLE);
						objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
						showAmount();	
					}else{
						llHeader.setVisibility(View.GONE);
						tvNoPendingInvoices.setVisibility(View.VISIBLE);
					}
					hideLoader();	
					}
				});
			}
		}).start();
		
		//llNxt.setVisibility(View.VISIBLE);
/*	*/
		btnNxt.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int count 	= 0;
				count 		= getCheckedItemsCount();
				
			    if( count > 0)
				{
					if(selectedAmount < 0)
						showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Return invoice amount should not be greater than sales invoice amount.",getString(R.string.OK),null, "");
					
					else if(isCreditCustomer(mallsDetails) && selectedAmount < fToPay)
						showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Please make payment of "+fToPay+" "+curencyCode+".", getString(R.string.OK),null,  "");
					
					else if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() >= vecPendingInvoices.size())
					{
						if(!isPartial)
						{
							Intent intent = new Intent(PendingInvoices.this, ReceivePaymentBySalesman.class);
							intent.putExtra("arcollection", isARCollection);
							intent.putExtra("selectedAmount", selectedAmount);
							intent.putExtra("InvoiceNumbers", arrInvoiceNumbers);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("strPaymentType", "Pending Invoice");
							intent.putExtra("OrderId", strOrderId);
							intent.putExtra("LPO", LPO);
							intent.putExtra("roundOffVal", roundOff);
							startActivityForResult(intent, 1000);
	
						}
						else
							showPassCodeDialog(null, "Normal", false);
					}
					else
						showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Do you want to skip unselected invoices?",getString(R.string.Yes),getString(R.string.No), "skip");
				}
				else if(selectedAmount == 0 && count < 0)
					showCustomDialog(PendingInvoices.this, getString(R.string.warning), "Invoice amount can not be Zero.", getString(R.string.OK),null,  "");
				else
					showCustomDialog(PendingInvoices.this, getString(R.string.warning),"Please select atleast one invoice to make payment.", getString(R.string.OK),null,  "");
			}
		});
		
		tvTotalDueValue.setText(curencyCode+"   "+amountDue);
		llMiddleLayout.addView(lvUnPaidInvoices,  new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		
		/*if(vecPendingInvoices.size() > 0)
		{
			tvNoPendingInvoices.setVisibility(View.GONE);
			//llNxt.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoPendingInvoices.setVisibility(View.VISIBLE);
			//llNxt.setVisibility(View.GONE);
		}*/
		
		btnAddInvoices.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showAddInvoicePopup();
			}
		});
		btnOnAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						getAllPendingItemsCount();
						if(selectedAmount<0)
							selectedAmount = 0;
						Intent intent = new Intent(PendingInvoices.this, ReceivePaymentBySalesman.class);
						intent.putExtra("mallsDetails", mallsDetails);
						intent.putExtra("onAccount",true);
						startActivityForResult(intent, 1000);
			}
		});
		
		if(mallsDetails != null)
		{
			tvSitename.setText(mallsDetails.siteName);
			tvAddress.setText(mallsDetails.addresss1 +", "+mallsDetails.addresss2);
		}
		
		btnAddInvoices.setVisibility(View.GONE);
		
		etSearchText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(s.toString()!=null)
				{
					Vector<PendingInvicesDO> vecTemp = new Vector<PendingInvicesDO>();
					for(PendingInvicesDO pendingInvicesDO : vecPendingInvoices)
					{
						String strText 		= (pendingInvicesDO).invoiceNo;
						String strText1 	= (pendingInvicesDO).invoiceDateToShow;
						String strText2 	= (pendingInvicesDO).INV_TYPE;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase())
								|| strText1.toLowerCase().contains(s.toString().toLowerCase())
								|| strText2.toLowerCase().contains(s.toString().toLowerCase()))
							
							vecTemp.add(pendingInvicesDO);
					}
					if(vecTemp!=null)
						objUnPaidInvoiceAdapter.refreshList(vecTemp);
				}
				else
					objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
				
				showAmount();
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
		if(!mallsDetails.CREDIT_LEVEL.equalsIgnoreCase(AppConstants.CREDIT_LEVEL_ACCOUNT)){
			btnOnAccount.setVisibility(View.GONE);
		}
	}

	//function for initializing variables
	private void intialiseControls() 
	{
		llMiddleLayout 		= (LinearLayout)llArCollection.findViewById(R.id.llMiddleLayout);
		tvTotalAmount 		= (TextView)llArCollection.findViewById(R.id.tvTotalAmount);
		tvTotalAmountDue	= (TextView)llArCollection.findViewById(R.id.tvTotalAmountDue);
		tvAmountDue			= (TextView)llArCollection.findViewById(R.id.tvAmountDue);
		tvUnpaid			= (TextView)llArCollection.findViewById(R.id.tvUnPaid);
		tvARHead			= (TextView)llArCollection.findViewById(R.id.tvARHead);
		tvTotalAmountValue  = (TextView)llArCollection.findViewById(R.id.tvTotalAmountValue);
		tvTotalDueValue     = (TextView)llArCollection.findViewById(R.id.tvTotalDueValue);
		tvCreditLimit		= (TextView)llArCollection.findViewById(R.id.tvCredit_Limit);
		tvCreditLimitValue	= (TextView)llArCollection.findViewById(R.id.tvCredit_Limit_value);
		tvCreditTime		= (TextView)llArCollection.findViewById(R.id.tvCredit_Time);
		tvCreditTimeValue	= (TextView)llArCollection.findViewById(R.id.tvCredit_Time_Value);
		tvNoPendingInvoices	= (TextView)llArCollection.findViewById(R.id.tvNoPendingInvoice);
		tvAmount			= (TextView)llArCollection.findViewById(R.id.tvAmount);
		tvTotalAmt			= (TextView)llArCollection.findViewById(R.id.tvTotalAmt);
		tvPaidAmt			= (TextView)llArCollection.findViewById(R.id.tvPaidAmt);
		etSearchText		= (EditText)llArCollection.findViewById(R.id.etSearchText);
		
		tvSitename			= (TextView)llArCollection.findViewById(R.id.tvSitename);
		tvAddress			= (TextView)llArCollection.findViewById(R.id.tvAddress);
		
		llNxt				= (LinearLayout)llArCollection.findViewById(R.id.llNxt);
		llHeader			= (LinearLayout)llArCollection.findViewById(R.id.llHeader);
		btnNxt 				= (Button)llArCollection.findViewById(R.id.btnNxt);
		btnAddInvoices		= (Button)llArCollection.findViewById(R.id.btnAddInvoices);
		
		btnOnAccount		= (Button)llArCollection.findViewById(R.id.btnOnAccount);
		llInvoice			= (LinearLayout)llArCollection.findViewById(R.id.llInvoice);

		tvAmt1			= (TextView) llArCollection.findViewById(R.id.tvAmt1);
		tvAmt2			= (TextView) llArCollection.findViewById(R.id.tvAmt2);
		tvAmt3			= (TextView) llArCollection.findViewById(R.id.tvAmt3);
		
		//setting type-face on controls 
		llInvoice.setVisibility(View.VISIBLE);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		blockMenu();
	}
	
	public class UnPaidInvoiceAdapter extends ArrayAdapter<PendingInvicesDO>
	{
		
		private Vector<PendingInvicesDO> vctPendingInvicesDOs;
		public UnPaidInvoiceAdapter(Vector<PendingInvicesDO> vecPendingInvicesDOs) 
		{
			super(PendingInvoices.this, 0, vecPendingInvicesDOs);
			
			vctPendingInvicesDOs = vecPendingInvicesDOs;
		}

		public Vector<PendingInvicesDO> getMofiedVector() 
		{
			return vctPendingInvicesDOs;
		}
		
		@Override
		public int getCount() 
		{
			/*if(vctPendingInvicesDOs.size() > 0)
			{
				tvNoPendingInvoices.setVisibility(View.GONE);
				//llNxt.setVisibility(View.VISIBLE);
				return vctPendingInvicesDOs.size();
			}
			else
			{
				tvNoPendingInvoices.setVisibility(View.VISIBLE);
				//llNxt.setVisibility(View.GONE);
				return 0;
			}*/
			return vctPendingInvicesDOs.size();
		}
		public void refresh()
		{
			notifyDataSetChanged();
		}

		@Override
		public PendingInvicesDO getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return 0;
		}

		@Override
		public View getView(final int arg0, View llLayout, ViewGroup arg2) 
		{
			final PendingInvicesDO pendingInvicesDO = vctPendingInvicesDOs.get(arg0);
			
			llLayout 					= (LinearLayout)getLayoutInflater().inflate(R.layout.ar_colloection_cell, null);
			Button btnArrow				= (Button)llLayout.findViewById(R.id.btnArrow);
			TextView tvAmount			= (TextView)llLayout.findViewById(R.id.tvAmount);
			TextView tvDescription		= (TextView)llLayout.findViewById(R.id.tvDescription);
			TextView tvDueDate			= (TextView)llLayout.findViewById(R.id.tvDueDate);
			TextView tvHeaderText		= (TextView)llLayout.findViewById(R.id.tvHeaderText);
			TextView tvInvType			= (TextView)llLayout.findViewById(R.id.tvInvType);
			TextView tvInvoiceAmt		= (TextView)llLayout.findViewById(R.id.tvInvoiceAmt);
			final ImageView ivDelete	= (ImageView)llLayout.findViewById(R.id.ivDelete);
			final EditText etEnterAmount= (EditText)llLayout.findViewById(R.id.etEnterAmount);
			final ImageView ivCheck		= (ImageView)llLayout.findViewById(R.id.ivCheck);
			
			tvDueDate.setVisibility(View.GONE);
			btnArrow.setVisibility(View.GONE);
			
			etEnterAmount.setTag(pendingInvicesDO);
			
			if(isCreditCustomer(mallsDetails) && StringUtils.getFloat(pendingInvicesDO.balance) > 0)
				etEnterAmount.setEnabled(true);
			else
				setActionInViews(etEnterAmount, false);
			if(pendingInvicesDO.invoiceNo.equalsIgnoreCase(PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT)){
				tvAmount.setText("N/A");
				tvInvoiceAmt.setText("N/A");
				etEnterAmount.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance)));
			}
			else if(pendingInvicesDO.invoiceNo.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
			{
				tvAmount.setText("");
				etEnterAmount.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance)));
				tvInvoiceAmt.setText("");
			}
			else if(StringUtils.getFloat(pendingInvicesDO.balance) < 0 || (pendingInvicesDO.DOC_TYPE != null && pendingInvicesDO.DOC_TYPE.equalsIgnoreCase(AppConstants.RETURN_INV_CODE)))
			{
//				if(isCreditCustomer(mallsDetails))
//				{
//					setActionInViews(llLayout, false);
//					ivCheck.setVisibility(View.INVISIBLE);
//				}
				
				setActionInViews(etEnterAmount, false);
				etEnterAmount.setText(diffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance)));
				tvAmount.setText(Html.fromHtml("<font color = #454545> "+"&nbsp;&nbsp;&nbsp;</font>"+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.lastbalance))));
				tvInvoiceAmt.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
			}
			else 
			{
				if(!TextUtils.isEmpty(strOrderId) && strOrderId.equalsIgnoreCase(pendingInvicesDO.invoiceNo))
				{
					pendingInvicesDO.isNewleyAdded = true;
					setActionInViews(llLayout, false);
					setActionInViews(ivCheck, false);
					
					showAmount();
				}
				
				tvInvoiceAmt.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.totalAmount)));
				tvAmount.setText(Html.fromHtml("<font color = #454545> "+"&nbsp;&nbsp;&nbsp;</font>"+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.lastbalance))));
				etEnterAmount.setText(""+diffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance)));
			}
			
			tvInvType.setText(pendingInvicesDO.INV_TYPE);
			tvDescription.setText(pendingInvicesDO.invoiceDateToShow);
			
			if(pendingInvicesDO.invoiceNo.equalsIgnoreCase(AppConstants.OPEN_CREDIT) || pendingInvicesDO.DOC_TYPE.equalsIgnoreCase(AppConstants.RETURN_INV_CODE))
				tvHeaderText.setTextColor(getResources().getColor(R.color.green));
			else if(pendingInvicesDO.IsOutStanding.equalsIgnoreCase("true"))
				tvHeaderText.setTextColor(getResources().getColor(R.color.red));
			else
				tvHeaderText.setTextColor(getResources().getColor(R.color.list_middle));
			
			tvHeaderText.setText(pendingInvicesDO.invoiceNo);
			
			if(pendingInvicesDO.isNewleyAdded)
				ivCheck.setBackgroundResource(R.drawable.check_hover);
			else
				ivCheck.setBackgroundResource(R.drawable.check_normal);
			
			ivCheck.setTag(arg0);
			llLayout.setTag(arg0);
			llLayout.setTag(pendingInvicesDO);
			if(pendingInvicesDO.invoiceNo.equalsIgnoreCase(PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT)){
				llLayout.setClickable(false);
				llLayout.setEnabled(false);
				ivCheck.setClickable(false);
				ivCheck.setEnabled(false);
			}else{
				llLayout.setClickable(true);
				llLayout.setEnabled(true);
				ivCheck.setClickable(true);
				ivCheck.setEnabled(true);
			}
			
			
			llLayout.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					ivCheck.performClick();
				}
			});
			
			ivCheck.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(ivDelete.getVisibility() == View.VISIBLE && pendingInvicesDO.isDeleteOpen)
						loadAnimation(ivDelete, false);
					
					if(!pendingInvicesDO.isNewleyAdded)
					{
						pendingInvicesDO.isNewleyAdded = true;
						ivCheck.setBackgroundResource(R.drawable.check_hover);
					}
					else
					{
						pendingInvicesDO.isNewleyAdded = false;
						ivCheck.setBackgroundResource(R.drawable.check_normal);
					}
					showAmount();
				}
			});
			
			etEnterAmount.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence charsequence, int i, int j, int k)
				{
					PendingInvicesDO pendingInvicesDO = (PendingInvicesDO) etEnterAmount.getTag();
					pendingInvicesDO.balance = charsequence.toString();
					
					if(StringUtils.getFloat(pendingInvicesDO.balance) > StringUtils.getFloat(pendingInvicesDO.lastbalance))
					{
						pendingInvicesDO.balance = pendingInvicesDO.lastbalance;
						etEnterAmount.setText(pendingInvicesDO.balance);
					}
					if(pendingInvicesDO.isNewleyAdded)
						showAmount();
				}
				
				@Override
				public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) 
				{
				}
				
				@Override
				public void afterTextChanged(Editable editable) 
				{
				}
			});
			
			llLayout.setOnLongClickListener(new OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v) 
				{
					if(pendingInvicesDO.invoiceNo.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
					{
						pendingInvicesDO.isDeleteOpen = false;
						if(ivDelete.getVisibility() == View.GONE)
							loadAnimation(ivDelete, true);
						else
							loadAnimation(ivDelete, false);
						
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run() 
							{
								pendingInvicesDO.isDeleteOpen = true;								
							}
						}, 1000);
					}
						
					return false;
				}
			});
			
			ivDelete.setTag(pendingInvicesDO);
			ivDelete.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v) 
				{
					showLoader("Please wait...");
					new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							PendingInvicesDO pendingInvicesDO = (PendingInvicesDO) v.getTag();
							
							if(vecPendingInvoices != null && vecPendingInvoices.contains(pendingInvicesDO))
							{
								btnAddInvoices.setVisibility(View.GONE);
								vecPendingInvoices.remove(pendingInvicesDO);
							}
							
							objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
							showAmount();
							
						}
					}, 300);
				}
			});
			
			setTypeFace((LinearLayout)llLayout);
			return llLayout;
		}
		
		public void refreshList(Vector<PendingInvicesDO> vctPendingInvicesDOs)
		{
			this.vctPendingInvicesDOs = vctPendingInvicesDOs;
			notifyDataSetChanged();
			
			/*if(vecPendingInvoices.size() > 0)
			{
				llHeader.setVisibility(View.VISIBLE);
				tvNoPendingInvoices.setVisibility(View.GONE);
			
			}else{
				llHeader.setVisibility(View.GONE);
				tvNoPendingInvoices.setVisibility(View.VISIBLE);
				
			}*/
			
		}
	}
	
	private int getCheckedItemsCount()
	{
		int count 		  = 	0;
		arrInvoiceNumbers = 	new ArrayList<PendingInvicesDO>();
		selectedAmount 	  = 	0;
		isPartial 		  = 	false;
		 
		 if(vecPendingInvoices != null && vecPendingInvoices.size() > 0)
		 {
			 for(PendingInvicesDO pendingInvicesDO :  vecPendingInvoices)
			 {
				 if(pendingInvicesDO.isNewleyAdded && StringUtils.getFloat(pendingInvicesDO.balance) != 0)
				 {
					 count++;
					 selectedAmount = selectedAmount+StringUtils.getFloat(pendingInvicesDO.balance);
					 if(!pendingInvicesDO.invoiceNo.equalsIgnoreCase(""))
						 	arrInvoiceNumbers.add(pendingInvicesDO);
				 }
				 
				 if(StringUtils.getFloat(pendingInvicesDO.balance) != StringUtils.getFloat(pendingInvicesDO.lastbalance))
					 isPartial = true;
			 }
		 }
		 
		return count;
	}
	
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("skip"))
		{
			Intent intent = new Intent(PendingInvoices.this, ReceivePaymentBySalesman.class);
			intent.putExtra("arcollection", isARCollection);
			intent.putExtra("selectedAmount", selectedAmount);
			intent.putExtra("InvoiceNumbers", arrInvoiceNumbers);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("strPaymentType", "Pending Invoice");
			intent.putExtra("OrderId", strOrderId);
			intent.putExtra("LPO", LPO);
			intent.putExtra("roundOffVal", roundOff);
			startActivityForResult(intent, 1000);
		}
		else if(from.equalsIgnoreCase("Unallocated"))
		{
			PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
			pendingInvicesDO.invoiceNo 		= "Unallocated";
			pendingInvicesDO.balance 		= ((EditText)etInvoiceNumber).getText().toString();
			pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
			pendingInvicesDO.invoiceDate 	= CalendarUtils.getOrderPostDate();
			pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
			pendingInvicesDO.isNewleyAdded	=	true;
			pendingInvicesDO.orderId 		= " ";
			vecPendingInvoices.add(pendingInvicesDO);
			if (tempcustomDialogs!=null && tempcustomDialogs.isShowing())
				tempcustomDialogs.dismiss();
			
			objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
		}
	}
	
	@Override
	public void onButtonNoClick(String from)
	{
		super.onButtonNoClick(from);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == 1000 && resultCode == AppStatus.PAYMENT_DONE)
		{
			setResult(AppStatus.PAYMENT_DONE);
			finish();
		}
		else if(requestCode == 1000 && resultCode == RESULT_OK)
		{
			setResult(RESULT_OK);
			finish();
		}
		else if(resultCode == 900)
		{
			setResult(900);
			finish();
		}
			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showAddInvoicePopup()
	{
		View view = inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialogs = new CustomDialog(PendingInvoices.this, view, preference	.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,LayoutParams.WRAP_CONTENT, true);
		customDialogs.setCancelable(true);
		TextView tvTitle 				= (TextView) view.findViewById(R.id.tvTitlePopup);
		Button btnOkPopup 				= (Button) 	 view.findViewById(R.id.btnOkPopup);
		final EditText etEnterValue 	= (EditText) view.findViewById(R.id.etEnterValue);
		EditText etEnterReasons			= (EditText) view.findViewById(R.id.etEnterReason);
		final EditText etAmountdecimal	= (EditText) view.findViewById(R.id.etAmountdecimal);
		etEnterReasons.setVisibility(View.GONE);
		etEnterValue.setVisibility(View.GONE);
		
		TextView tvSelectReason		= (TextView) view.findViewById(R.id.tvSelectReason);
		tvSelectReason.setVisibility(View.GONE);
		
		etAmountdecimal.setVisibility(View.VISIBLE);
		etAmountdecimal.setHint("Enter amount");
		
		int maxLength = 10;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		etAmountdecimal.setFilters(FilterArray);
		etEnterValue.setText(""+AppConstants.OPEN_CREDIT);
		
		tvTitle.setText(" Add Open Credit ");
		btnOkPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(etAmountdecimal.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(PendingInvoices.this,getString(R.string.warning), "Please enter amount.", getString(R.string.OK), null, "");
				else
				{
					String invoiceNumber = etEnterValue.getText().toString().trim();
					PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
					pendingInvicesDO.invoiceNo 		= invoiceNumber;
					pendingInvicesDO.balance 		= etAmountdecimal.getText().toString();
					pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
					pendingInvicesDO.INV_TYPE		= AppConstants.OPEN_CREDIT;
					pendingInvicesDO.invoiceDate 	= CalendarUtils.getOrderPostDate();
					pendingInvicesDO.invoiceDateToShow	= CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
					pendingInvicesDO.orderId 		= "";
					pendingInvicesDO.isNewleyAdded	=	true;
					vecPendingInvoices.add(pendingInvicesDO);
					if (customDialogs.isShowing())
						customDialogs.dismiss();
					
					objUnPaidInvoiceAdapter.refreshList(vecPendingInvoices);
					showAmount();
					btnAddInvoices.setVisibility(View.GONE);
				}
			}
		});
		if (!customDialogs.isShowing())
			customDialogs.show();
	}
	
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String from, boolean isCheckOut)
	{
		onButtonYesClick("skip");
	}
	
	@Override
	public void onBackPressed() 
	{
		setResult(AppStatus.PAYMENT_CANCEL);
		finish();
	}
	
	float totAmount, selAmt; 
	private void showAmount()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				selAmt 		= 0;
				totAmount 	= 0;
				
				Vector<PendingInvicesDO> vecPendingInvoices = objUnPaidInvoiceAdapter.getMofiedVector();
				for(PendingInvicesDO pendingInvicesDO : vecPendingInvoices)
					if(pendingInvicesDO.isNewleyAdded)
					selAmt += StringUtils.getFloat(pendingInvicesDO.balance);
				
				for(PendingInvicesDO pendingInvicesDO : vecPendingInvoices)
					if(!pendingInvicesDO.invoiceNo.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
						totAmount += StringUtils.getFloat(pendingInvicesDO.lastbalance);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(totAmount<0){
							totAmount=0;
						}
						tvTotalAmt.setText("Total Outstanding : "+ curencyCode+ " "+diffAmt.format(totAmount));
						tvPaidAmt.setText("Amount to Pay : "+ curencyCode+ " "+diffAmt.format(selAmt));

						//added on 02JAN
						tvAmt1.setText(""+curencyCode);
						tvAmt2.setText(""+curencyCode);
						tvAmt3.setText(""+curencyCode);
					}
				});
			}
		}).start();
	}
	private void getAllPendingItemsCount()
	{
		selectedAmount 	  = 	0;
		for(int i = 0; i < vecPendingInvoices.size() ; i++)
		{
			if(StringUtils.getFloat(vecPendingInvoices.get(i).balance) != 0)
			{
				if(vecPendingInvoices.get(i).INV_TYPE.equalsIgnoreCase(AppConstants.RETURN_INV))
					selectedAmount = selectedAmount-StringUtils.getFloat(diffAmt.format( StringUtils.getFloat(vecPendingInvoices.get(i).balance)));
				else
					selectedAmount = selectedAmount+StringUtils.getFloat(diffAmt.format( StringUtils.getFloat(vecPendingInvoices.get(i).balance)));

			}
		}
	}
	
	private void showPaymentModePopup(final Intent intent, final float selectedAmount) {/*

		View view = inflater.inflate(R.layout.payment_mode_popup, null);
		
		final CustomDialog customDialog = new CustomDialog(PendingInvoices.this, view);
		customDialog.setCancelable(true);
		
		Button btn_CashPayment = (Button) view.findViewById(R.id.btn_CashPayment);
		Button btn_ChequePayment = (Button) view.findViewById(R.id.btn_ChequePayment);
		
		
		
		btn_CashPayment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent.putExtra("paymode",AppConstants.PAYMENT_NOTE_CASH);
				float totalOrderAmnt        = 0;
				if(orderDO!=null)
					totalOrderAmnt = StringUtils.getFloat(diffAmt.format((StringUtils.getFloat(diffAmt.format(orderDO.totalAmount)) - (StringUtils.getFloat(diffAmt.format(orderDO.totalDiscountAmount)) ))));
				if(selectedAmount <=totalOrderAmnt)
					intent.putExtra("isCashOnly", true);
				
				if(isExceed || isFromPayment)
					startActivityForResult(intent, 5000);
				else
					startActivityForResult(intent, 1000);
				
				customDialog.dismiss();
			}
		});
		
		
		btn_ChequePayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				float totalOrderAmnt        = 0;
				if(orderDO!=null)
					totalOrderAmnt = StringUtils.getFloat(diffAmt.format((StringUtils.getFloat(diffAmt.format(orderDO.totalAmount)) - (StringUtils.getFloat(diffAmt.format(orderDO.totalDiscountAmount)) ))));
				if(selectedAmount <= totalOrderAmnt){
					showToast("Not allowed for payment...");
				}else{
					
					intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CHEQUE);
					if(isExceed || isFromPayment)
						startActivityForResult(intent, 5000);
					else
						startActivityForResult(intent, 1000);
					
				}
				customDialog.dismiss();
			}
		});
		
		
		
		if(!customDialog.isShowing())
			customDialog.showCustomDialog();
		
	*/}
}
