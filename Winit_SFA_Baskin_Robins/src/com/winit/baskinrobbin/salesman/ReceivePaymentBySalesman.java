package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.CustomScrollView;
import com.winit.baskinrobbin.salesman.common.OfflineDA;
import com.winit.baskinrobbin.salesman.common.OfflineDA.OfflineDataType;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class ReceivePaymentBySalesman extends BaseActivity
{
	//declaration of variables
	private LinearLayout llReceivePayment, llCheque,llCustomerSignature, llPaymentLayout, llCustomer_Signature ,llCash, 
						 llAddPaymentLayout, llInvoiceNumbers, llRemainingAmount,ll_invoiceDetails;
	
	private MyView viewAgent;
	
	private TextView tvRecieveHead, tvCheque, tvCash ,tvSelectBanks, tvDate, tvAmount, tvCurrencyType, tvTotalAmountValue
					 ,tvDateTitle, tvSelCurrencyType, tvSelAmount, tvCashDateTitle, tvCashSelect, tvCashSelAmount, 
					 tvCashSelCurrencyType, tvRemaining, tvRemainingAED, tvSignatureTitle, tvMinusCash, tvMinusCheck, tvSelectRmn, tvSelectRmnV;
	
	private EditText  etCheque_Details , etTotalAmount, etChequeAmount, etCashAmount, etRemainingAmount;
	private Button btnConfirm_Payment, btnCancel , btnPrint, btnPaymentSignClear, btnContinue, btnPrintInvoice;
	private final int lastthickValue=2;
	private Paint  mPaint;
	private final int DATE_DIALOG_ID = 0;
	private String selectedAmount = "",strDate = "", strChequeDate = "";
	private CustomScrollView customScrollView;
	private Vector<NameIDDo> vecBankName, vecRmnV;
	private boolean isSigned = false, isFromArCollection = false, isPaymentDone = false;
	private float totalPayAmount = 0, totalInvAmt = 0,  roundOff,totalOnaccPay=0;
	private String strReceiptNo = "";
	private JourneyPlanDO mallsDetails;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers;
	private PaymentHeaderDO paymentHeaderDO;
	private String PAYMENT_TYPE = "CHEQUE", CASH = "CASH", CHEQUE = "CHEQUE", ENTERED_TYPE/*, SEL_TYPE = CHEQUE*/;
	private LinearLayout llBankName;
	private EditText etBankName;
	private boolean isCheque=false,isCash=false,onAccount=false;
	public String strOrderId = "", LPO = "";
	public boolean enablePrintUnits;
	TextView tvAmountDueInvoice;
	private Object sync_payment = new Object();
	private Boolean isPaymentProcessingStarted = false;

	@Override 
	public void initialize() 
	{
		//inflate the receive_payment layout
		llReceivePayment		=	(LinearLayout)inflater.inflate(R.layout.receive_payment_main, null);
		llPaymentLayout			=	(LinearLayout)inflater.inflate(R.layout.preseller_receive_payment, null);
		
		isSigned 		= false;
		isPaymentDone 	= false;
	
		preference.removeFromPreference(Preference.RECIEPT_NO);
		preference.commitPreference();
		//=======================================================

//		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry("BHD");
		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry(preference.getStringFromPreference(Preference.CURRENCY_CODE,""));
		if(noOfRoundingOffdigits==0)
			noOfRoundingOffdigits=2;
		diffAmt = new DecimalFormat("##.##");
		diffAmt.setMinimumFractionDigits(noOfRoundingOffdigits);
		diffAmt.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffStock = new DecimalFormat("##.##");
		diffStock.setMinimumFractionDigits(0);
		diffStock.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffPreview = new DecimalFormat("##.##");
		diffPreview.setMinimumFractionDigits(0);
		diffPreview.setMaximumFractionDigits(noOfRoundingOffdigits);

		diffLagLong = new DecimalFormat("#.#####");
		//==========================================
		intialiseControls();
		if(getIntent().getExtras() != null)
		{
			isFromArCollection 	=	 getIntent().getExtras().getBoolean("arcollection");
			
			selectedAmount	   	= 	""+getIntent().getExtras().getFloat("selectedAmount");
			float invoiceAmount	=	 getIntent().getExtras().getFloat("invoiceAmount");
			roundOff	  	 	= 	 getIntent().getExtras().getFloat("roundOffVal");
			
			strOrderId	=	 getIntent().getExtras().getString("OrderId");
			LPO			=	 getIntent().getExtras().getString("LPO");
			
			onAccount=getIntent().getExtras().getBoolean("onAccount");
			
			if(selectedAmount.trim().equalsIgnoreCase("NaN"))
				selectedAmount  = 	""+invoiceAmount;
			
			
			mallsDetails  		= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			arrInvoiceNumbers  	= 	(ArrayList<PendingInvicesDO>) getIntent().getExtras().get("InvoiceNumbers");
			
			if(mallsDetails.currencyCode != null && mallsDetails.currencyCode.length() > 0)
				curencyCode = mallsDetails.currencyCode;
			
		if(arrInvoiceNumbers!=null){
			ll_invoiceDetails.setVisibility(View.VISIBLE);
			for(PendingInvicesDO obj : arrInvoiceNumbers)
			{
				LinearLayout llInvoiceLayout			=	(LinearLayout)inflater.inflate(R.layout.preseller_invoice_list, null);
				TextView tvSelect_Pre_sold_OrderValue	=	(TextView)llInvoiceLayout.findViewById(R.id.tvSelect_Pre_sold_OrderValue);
				TextView tvSelectAmount					=	(TextView)llInvoiceLayout.findViewById(R.id.tvSelectAmount);
				final EditText etAmount					=	(EditText)llInvoiceLayout.findViewById(R.id.etAmount);
				final TextView tvInvoiceType			=	(TextView)llInvoiceLayout.findViewById(R.id.tvInvoiceType);
				
				etAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvSelectAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvSelect_Pre_sold_OrderValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvSelect_Pre_sold_OrderValue.setText(""+obj.invoiceNo);
				
				tvInvoiceType.setText(obj.INV_TYPE);
				
				tvSelectAmount.setText(""+diffAmt.format(StringUtils.getFloat(obj.balance)));
				etAmount.setText(""+diffAmt.format(StringUtils.getFloat(obj.balance)));
				
				etAmount.setTag(obj);
				etAmount.setVisibility(View.GONE);
				totalPayAmount += StringUtils.getFloat(obj.balance);
				llInvoiceNumbers.addView(llInvoiceLayout,LayoutParams.FILL_PARENT, (int)(35 * BaseActivity.px));
			}
		}
			
			
			totalPayAmount = StringUtils.getFloat(diffAmt.format(totalPayAmount));
			if(totalPayAmount >= 0)
			{
				totalInvAmt = totalPayAmount;
				tvTotalAmountValue.setText(curencyCode+" "+totalInvAmt);
				etTotalAmount.setText(""+diffAmt.format(totalInvAmt));
				etRemainingAmount.setText(""+diffAmt.format(totalInvAmt));
				etCashAmount.setText(""+diffAmt.format(totalInvAmt));
			}
		}
		
		//setting current date in format 
		//getting current dateofJorney from Calendar
	    Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month  	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    strDate    	= 	CalendarUtils.getMonthFromNumber(month+1)+" "+day+CalendarUtils.getDateNotation(day)+", "+year;
	     
		viewAgent = new MyView(ReceivePaymentBySalesman.this);
		viewAgent.setDrawingCacheEnabled(true);
		viewAgent.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);

		if(viewAgent != null)
			llCustomerSignature.addView(viewAgent, new android.widget.FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lastthickValue);
       
        showLoader(getString(R.string.please_wait));
        new Thread(new Runnable()
        {
			@Override
			public void run() 
			{
				vecBankName = new CommonDA().getAllBanksNew();
				vecRmnV     = new CommonDA().getAllReceiptMethodNames();
				enablePrintUnits = new SettingsDA().getSettingsValue(AppConstants.EnablePrintUnits);

				 runOnUiThread(new Runnable()
				 {
					@Override
					public void run()
					{
						tvAmountDueInvoice.setText("Amount ("+curencyCode+")");
						tvCurrencyType.setText(""+curencyCode);
						tvCashSelCurrencyType.setText(""+curencyCode);
						hideLoader();
					}
				});
			}
		}).start();
       
		tvCheque.setOnClickListener(new OnClickListener()
		{
			@Override 
			public void onClick(View v) 
			{
				if(!isCheque || (!tvSelectRmnV.getText().toString().equalsIgnoreCase(AppConstants.GICC_DXB_Cheque) 
						&& !tvSelectRmnV.getText().toString().equalsIgnoreCase(AppConstants.GICC_DXB_PDC)))
				{
//					if(ENTERED_TYPE != null /*&& ENTERED_TYPE.equalsIgnoreCase(CASH)*/)//commented to display remaining amount in both the cases
//						llRemainingAmount.setVisibility(View.VISIBLE);
//					else
//						llRemainingAmount.setVisibility(View.VISIBLE);
					if(!isCheque)
					{
						tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.checked1),null, null, null);
						llCheque.setVisibility(View.VISIBLE);
						isCheque = true;
						tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked1), null, null, null);
						isCash = false;
					}
					else
					{
						CheckClick();
					}
					
					tvDate.setBackgroundResource(R.drawable.spinner_disabled_holo_light);
					tvDate.setText(strChequeDate);
				}
			}
		});
		
		tvCash.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isCash)
				{
					tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.checked1), null, null, null);
					llCash.setVisibility(View.VISIBLE);
					isCash = true;
					CheckClick();
				}
				else
				{
					tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked1), null, null, null);
					isCash = false;
//					etCashAmount.setText("");
//					llCash.setVisibility(View.GONE);
					
//					tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked),null, null, null);
//					llCheque.setVisibility(View.VISIBLE);
//					isCheque = true;
				}
				tvCashSelect.setText(""+strDate);
				tvCashSelect.setTag(""+CalendarUtils.getOrderPostDate());
//				llCheque.setVisibility(View.GONE);
				tvCashSelect.setEnabled(false);
				tvCashSelect.setClickable(false);
//				SEL_TYPE = CASH;
			}
		});
		
		tvSelectBanks.setTag(-1);
		tvSelectBanks.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(ReceivePaymentBySalesman.this, "Select Bank", true);
				builder.setSingleChoiceItems(vecBankName, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
						tvSelectBanks.setText(""+ObjNameIDDo.strName);
						tvSelectBanks.setTag(ObjNameIDDo);
						
						llBankName.setVisibility(View.GONE);
						
						builder.dismiss();
		    		}
			   }); 
				builder.show();
			}
		});



		btnConfirm_Payment.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isPaymentProcessingStarted = false;
				synchronized (sync_payment)
				{
					if(!isPaymentProcessingStarted) {

						disableView(btnConfirm_Payment);

						boolean isValid = getValidateEnteredData();
						if (!onAccount) {
							if (isValid) {
								float totalAmount = StringUtils.getFloat(diffAmt.format(Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString()))));
								float remAmount = totalAmount - (totalInvAmt);

								if (remAmount > 0)
									showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Do you want to pay remaining amount of AED " + remAmount + " as Open Credit?", "Yes", "No", AppConstants.OPEN_CREDIT);
								else
									performPaymentProcess();
							}

						} else {
							if (isValid)
								performPaymentProcess();
						}
						isPaymentProcessingStarted = true;
					}
				}
			}
		});
		
		/*btnContinue.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showOrderCompletePopup();
			}
		});*/
		btnContinue.setText("Finish ");
		btnContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//showOrderCompletePopup();
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
				sendBroadcast(intentBrObj);
			}
		});
		
		if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			btnCancel.setVisibility(View.GONE);
		
		btnCancel.setVisibility(View.GONE);
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Are you sure you want to cancel the payment process?", getString(R.string.Yes), getString(R.string.No), "paymentprocess");
			}
		});
			
		tvDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		//Button for Print
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!isFromArCollection && mallsDetails.customerType!= null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				{

					if(enablePrintUnits && SalesmanOrderPreview.vecMainProducts!=null && SalesmanOrderPreview.vecMainProducts.size()>0){
						for(ProductDO productDO : SalesmanOrderPreview.vecMainProducts){
							productDO.units = productDO.preUnits;
						}
					}
					ShowOptionPopupForPrinter(ReceivePaymentBySalesman.this,new PrintPopup() {
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
									intent=new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
								else if(selectedPrinter==AppConstants.DOTMATRIX)
									intent=new Intent(ReceivePaymentBySalesman.this, PrinterConnectorArabic.class);
								intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT);
								intent.putExtra("totalAmount", totalPayAmount);
								intent.putExtra("strReceiptNo", strReceiptNo);
								intent.putExtra("selectedAmount", selectedAmount);
								intent.putExtra("paymentHeaderDO", paymentHeaderDO);
								addArabicDetails(mallsDetails);
								intent.putExtra("mallsDetails", mallsDetails);
								intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
								intent.putExtra("OrderId", strOrderId);
								intent.putExtra("LPO", LPO);


								addArabicDetails(mallsDetails);
								intent.putExtra("roundOffVal", roundOff);
								startActivityForResult(intent, 1000);

								hideLoader();
							}



						}
					});
//					Intent intent = new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
//					intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT);
//					intent.putExtra("totalAmount", totalPayAmount);
//					intent.putExtra("strReceiptNo", strReceiptNo);
//					intent.putExtra("selectedAmount", selectedAmount);
//					intent.putExtra("paymentHeaderDO", paymentHeaderDO);
//					intent.putExtra("mallsDetails", mallsDetails);
//					intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
//					intent.putExtra("OrderId", strOrderId);
//					intent.putExtra("LPO", LPO);
//					intent.putExtra("roundOffVal", roundOff);
//					startActivityForResult(intent, 1000);
				}
				else
				{
					ShowOptionPopupForPrinter(ReceivePaymentBySalesman.this,new PrintPopup() {
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
									intent=new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
								else if(selectedPrinter==AppConstants.DOTMATRIX)
									intent=new Intent(ReceivePaymentBySalesman.this, PrinterConnectorArabic.class);
								intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
								intent.putExtra("totalAmount", totalPayAmount);
								intent.putExtra("strReceiptNo", strReceiptNo);
								intent.putExtra("selectedAmount", selectedAmount);
								intent.putExtra("paymentHeaderDO", paymentHeaderDO);
								intent.putExtra("mallsDetails", mallsDetails);
								intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
								startActivityForResult(intent, 1000);

								hideLoader();
							}



						}
					});
//					Intent intent = new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
//					intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
//					intent.putExtra("totalAmount", totalPayAmount);
//					intent.putExtra("strReceiptNo", strReceiptNo);
//					intent.putExtra("selectedAmount", selectedAmount);
//					intent.putExtra("paymentHeaderDO", paymentHeaderDO);
//					intent.putExtra("mallsDetails", mallsDetails);
//					intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
//					startActivityForResult(intent, 1000);
				}
			}
		});
		
		btnPaymentSignClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isSigned = false;
				llCustomerSignature.removeAllViews();
				viewAgent = new MyView(ReceivePaymentBySalesman.this);
				viewAgent.setDrawingCacheEnabled(true);
				viewAgent.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llCustomerSignature.addView(viewAgent, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		});
		
		//Adding the Layouts
		customScrollView  = new CustomScrollView(this, llPaymentLayout);
		llAddPaymentLayout.addView(customScrollView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		llBody.addView(llReceivePayment,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		
		super.btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(isPaymentDone)
				{
					btnContinue.performClick();
				}
				else
					showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Are you sure you want to cancel the payment process?", getString(R.string.Yes), getString(R.string.No), "paymentprocess");
			}
		});
		
		tvCash.performClick();
	
		etTotalAmount.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				float amt = StringUtils.getFloat(""+s.toString());
				if(amt < totalInvAmt)
					etTotalAmount.setError("Entered amount should not be less than AED "+totalInvAmt+" .");
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
		
		setTypeFace(llReceivePayment);
		
		if(mallsDetails.channelCode != null && mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			tvSignatureTitle.setText("Received By");
		else
			tvSignatureTitle.setText(getString(R.string.Customer_Signature));
		
		if(StringUtils.getFloat(selectedAmount) <= 0 )
		{
			tvCheque.setClickable(false);
			etCashAmount.setText("0");
			etCashAmount.setEnabled(false);
			etCashAmount.setFocusable(false);
			etCashAmount.setFocusableInTouchMode(false);
		}
		
		
		tvSelectRmnV.setTag(-1);
		tvSelectRmnV.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(ReceivePaymentBySalesman.this, "Select Receipt Method Name", true);
				builder.setSingleChoiceItems(vecRmnV, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
						tvSelectRmnV.setText(""+ObjNameIDDo.strName);
						tvSelectRmnV.setTag(ObjNameIDDo);
						
						if(tvSelectRmnV.getText().toString().contains(AppConstants.GICC_DXB_Cheque) 
								|| tvSelectRmnV.getText().toString().contains(AppConstants.GICC_DXB_PDC))
						{
							isCheque = false;
							setActionInViews(tvCheque, true);
							tvCheque.performClick();
							setActionInViews(tvCash, false);
							setActionInViews(tvCheque, false);
						}
						else
						{
							isCash = false;
							setActionInViews(tvCash, true);
							tvCash.performClick();
							setActionInViews(tvCheque, false);
							setActionInViews(tvCash, false);
						}
						builder.dismiss();
		    		}
			   }); 
				builder.show();
			}
		});
		
		setActionInViews(tvCash, false);
		setActionInViews(tvCheque, false);
		
		if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			//setActionInViews(etTotalAmount, false);
			btnCheckOut.setVisibility(View.GONE);
		}
	}
	
	private PaymentDetailDO getChequeDetail(String totalAmt) 
	{
		PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
		NameIDDo nameIDDo 				= 	(NameIDDo) tvSelectBanks.getTag();
		paymentDetailDO.RowStatus 		= 	"1";
		paymentDetailDO.ReceiptNo		= 	strReceiptNo;
		paymentDetailDO.LineNo			= 	"2";
		paymentDetailDO.PaymentTypeCode	= 	CHEQUE;
		paymentDetailDO.BankCode		= 	nameIDDo.strId;
		paymentDetailDO.BankName		= 	tvSelectBanks.getText().toString();
		
		if(paymentDetailDO.BankName.contains("&"))
			paymentDetailDO.BankName 	= paymentDetailDO.BankName.replace("&", "");
		
		paymentDetailDO.ChequeDate		= 	tvDate.getTag().toString();
		paymentDetailDO.ChequeNo		= 	etCheque_Details.getText().toString();
		paymentDetailDO.CCNo			= 	"";
		paymentDetailDO.CCExpiry		= 	"";
		paymentDetailDO.PaymentStatus	= 	"0";
		paymentDetailDO.PaymentNote		= 	CHEQUE;
		
		paymentDetailDO.UserDefinedBankName	= paymentDetailDO.BankName;
	
		paymentDetailDO.Status			= 	"0";
		paymentDetailDO.Amount			= 	""+totalAmt;
		return paymentDetailDO;
	}

	private PaymentDetailDO getCashDetail(String totalAmt)
	{
		PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
		paymentDetailDO.RowStatus 		= 	"1";
		paymentDetailDO.ReceiptNo		= 	strReceiptNo;
		paymentDetailDO.LineNo			= 	"1";
		paymentDetailDO.PaymentTypeCode	= 	CASH;
		paymentDetailDO.BankCode		= 	"";
		paymentDetailDO.ChequeDate		= 	"";
		paymentDetailDO.ChequeNo		= 	"";
		paymentDetailDO.CCNo			= 	"";
		paymentDetailDO.CCExpiry		= 	"";
		paymentDetailDO.PaymentStatus	= 	"0";
		paymentDetailDO.PaymentNote		= 	CASH;
		paymentDetailDO.UserDefinedBankName= "";
		paymentDetailDO.Status			= 	"0";
		paymentDetailDO.Amount			= 	""+totalAmt;	
		return paymentDetailDO;
	}
	
	/** initializing all the Controls  of ReceivePaymentByPreseller class **/
	public void intialiseControls()
	{
		// all the fields from the receive_payment.xml is taken here
		llRemainingAmount				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llRemainingAmount);
		ll_invoiceDetails               =	(LinearLayout)llPaymentLayout.findViewById(R.id.ll_invoiceDetails);
		llCheque						=	(LinearLayout)llPaymentLayout.findViewById(R.id.llCheque);
		llBankName						=	(LinearLayout)llPaymentLayout.findViewById(R.id.llBankName);
		llInvoiceNumbers				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llInvoiceNumbers);
		tvCheque						=	(TextView)llPaymentLayout.findViewById(R.id.tvCheque);
		tvCash							=	(TextView)llPaymentLayout.findViewById(R.id.tvCash);
		tvSelectBanks					=	(TextView)llPaymentLayout.findViewById(R.id.tvSelectBanks);
		etCheque_Details				=	(EditText)llPaymentLayout.findViewById(R.id.etCheque_Details);
		etBankName						=	(EditText)llPaymentLayout.findViewById(R.id.etBankName);
		
		tvSelectRmn						=	(TextView)llPaymentLayout.findViewById(R.id.tvSelectRmn);
		tvSelectRmnV					=	(TextView)llPaymentLayout.findViewById(R.id.tvSelectRmnV);
		
		tvMinusCash						=	(TextView)llPaymentLayout.findViewById(R.id.tvMinusCash);
		tvMinusCheck					=	(TextView)llPaymentLayout.findViewById(R.id.tvMinusCheck);
		TextView tvChequeNumber			=	(TextView)llPaymentLayout.findViewById(R.id.tvChequeNumber);
		
		llCustomerSignature				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llCustomerSignature);
		tvDate							=	(TextView)llPaymentLayout.findViewById(R.id.tvDateSelect);
		TextView tvPayment_Details		=	(TextView)llPaymentLayout.findViewById(R.id.tvPayment_Details);
		TextView tvMode					=	(TextView)llPaymentLayout.findViewById(R.id.tvModePayment);
		tvSignatureTitle				=	(TextView)llPaymentLayout.findViewById(R.id.tvSignatureTitle);
		TextView tvBankName				=	(TextView)llPaymentLayout.findViewById(R.id.tvBankName);
		TextView tvEnterBankName		=	(TextView)llPaymentLayout.findViewById(R.id.tvEnterBankName);
		
		tvDateTitle						=	(TextView)llPaymentLayout.findViewById(R.id.tvDateTitle);
		llCash 							= 	(LinearLayout)llPaymentLayout.findViewById(R.id.llCash);
		llCustomer_Signature 			= 	(LinearLayout)llPaymentLayout.findViewById(R.id.llCustomer_Signature);
		tvAmount						= 	(TextView)llPaymentLayout.findViewById(R.id.tvAmount);
		etTotalAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.ettvAmount);
		tvCurrencyType					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCurrencyType);
		btnPaymentSignClear				= 	(Button)llPaymentLayout.findViewById(R.id.btnPaymentSignClear);
		TextView tvUnPaidInvoice		= 	(TextView)llPaymentLayout.findViewById(R.id.tvUnPaidInvoice);
//		TextView tvAmountDueInvoice		= 	(TextView)llPaymentLayout.findViewById(R.id.tvAmountDueInvoice);
		 tvAmountDueInvoice		= 	(TextView)llPaymentLayout.findViewById(R.id.tvAmountDueInvoice);
		tvTotalAmountValue				= 	(TextView)llPaymentLayout.findViewById(R.id.tvTotalAmountValue);
		TextView tvTotalAmountText		= 	(TextView)llPaymentLayout.findViewById(R.id.tvTotalAmountText);
		
		tvCashDateTitle					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashDateTitle);
		tvCashSelect					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelect);
		tvCashSelAmount					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelAmount);
		tvCashSelCurrencyType			= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelCurrencyType);
		
		tvRemaining						= 	(TextView)llPaymentLayout.findViewById(R.id.tvRemaining);
		tvRemainingAED					= 	(TextView)llPaymentLayout.findViewById(R.id.tvRemainingAED);
		etRemainingAmount				= 	(EditText)llPaymentLayout.findViewById(R.id.etRemainingAmount);
		
		etCashAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.etCashAmount);
		etChequeAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.etChequeAmount);
		tvSelCurrencyType				= 	(TextView)llPaymentLayout.findViewById(R.id.tvSelCurrencyType);
		tvSelAmount						= 	(TextView)llPaymentLayout.findViewById(R.id.tvSelAmount);
		
		//Main layout
		llAddPaymentLayout				= 	(LinearLayout)llReceivePayment.findViewById(R.id.llAddPaymentLayout);
		tvRecieveHead					=	(TextView)llReceivePayment.findViewById(R.id.tvRecieveHead);
		btnConfirm_Payment				=	(Button)llReceivePayment.findViewById(R.id.btnConfirm_Payment);
		btnCancel						=	(Button)llReceivePayment.findViewById(R.id.btnCancel);
		btnPrint						=	(Button)llReceivePayment.findViewById(R.id.btnPrint);
		
		btnContinue						=	(Button)llReceivePayment.findViewById(R.id.btnContinue);
		btnPrintInvoice					=	(Button)llReceivePayment.findViewById(R.id.btnPrintInvoice);
		
		etCashAmount.setFocusable(false);
		etCashAmount.setFocusableInTouchMode(false);
		etCashAmount.setCursorVisible(false);
		etCashAmount.setSingleLine(false);
		
		btnPrint.setVisibility(View.GONE);
		btnPrintInvoice.setVisibility(View.GONE);
		
		blockMenu();
	}
	
	public class MyView extends View 
	{
		private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        
        public MyView(Context c)
        {
            super(c);
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();
            int height = display.getHeight();
            
            if(mBitmap != null)
            {
            	mBitmap.recycle();
            	mBitmap = null;
            	System.gc();
            }
            
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(lastthickValue);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y)
        {
        	customScrollView.setScrollable(false);
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        
        private void touch_move(float x, float y) 
        {
          float dx = Math.abs(x - mX);
          float dy = Math.abs(y - mY);
          if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
          {
	           mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	           mX = x;
	           mY = y;
          }
        }
        
	  private void touch_up()
	  {
	      mPath.lineTo(mX, mY);
	      mCanvas.drawPath(mPath, mPaint);
	      mPath.reset();
	      customScrollView.setScrollable(true);
	      isSigned = true;
	   }
   
        
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
             x = event.getX();
             y = event.getY();
            
            switch (event.getAction()) 
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        	return true;
       }
   }
	@Override
	protected Dialog onCreateDialog(int id) 
    {
		//getting current dateofJorney from Calendar
	     Calendar c = 	Calendar.getInstance();
	     int cyear 	= 	c.get(Calendar.YEAR);
	     int cmonth = 	c.get(Calendar.MONTH);
	     int cday 	=	c.get(Calendar.DAY_OF_MONTH);
	     
	     switch (id) 
	     {
		     case DATE_DIALOG_ID:
		      	return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	     }
		 return null;
	  }
		
	/** method for dateofJorney picker **/
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	    {
	    	//getting current date from Calendar
		     Calendar currentCal = 	Calendar.getInstance();
		     int cyear 			 = 	currentCal.get(Calendar.YEAR);
		     int cmonth 		 = 	currentCal.get(Calendar.MONTH);
		     int cday 			 =	currentCal.get(Calendar.DAY_OF_MONTH);
		     currentCal.set(cyear, cmonth, cday);
		     //selected date 
		     Calendar selectedCal = Calendar.getInstance();
		     selectedCal.set(year, monthOfYear, dayOfMonth);
//		     if(currentCal.after(selectedCal))
//		     {
//		    	 showCustomDialog(ReceivePaymentByPreseller.this, getString(R.string.warning), "Date should not be before current date.", getString(R.string.OK), null, "");
//		     }
//		     else
//		     {
	    	 tvDate.setTag(year+"-"+((monthOfYear+1)< 10?"0"+(monthOfYear+1):(monthOfYear+1))+"-"+((dayOfMonth)<10?"0"+(dayOfMonth):(dayOfMonth)));
	    	 strChequeDate = CalendarUtils.getMonthFromNumber(monthOfYear+1)+" "+dayOfMonth+CalendarUtils.getDateNotation(dayOfMonth)+", "+year;
	    	 tvDate.setText(strChequeDate);
//		     }
	    }
    };
    
  
    @Override
    public void onButtonYesClick(String from)
    {
    	super.onButtonYesClick(from);
    	if(from.equalsIgnoreCase("confirm"))
    	{
    		if(!isFromArCollection)
    			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
    		else
    		{
    			Intent intentBrObj = new Intent();
    			intentBrObj.setAction(AppConstants.ACTION_GOTO_AR);
    			sendBroadcast(intentBrObj);
    		}
    	}
    	else if(from.equalsIgnoreCase("dashboard"))
    	{
    		setResult(900);
			finish();
    	}
    	else if(from.equalsIgnoreCase("served"))
		{
    		if(isFromArCollection)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    		else
    		{
    			setResult(AppStatus.PAYMENT_DONE);
    			finish();
    		}
		}
    	else if(from.equalsIgnoreCase("scroll"))
		{
			if(customScrollView.isScrolled())
			{
				customScrollView.setScrollable(false);
				customScrollView.scrollBy(0, llCustomer_Signature.getTop());
			}
		}
    	else if(from.equalsIgnoreCase("paymentprocess"))
		{
    		finish();
		}
    	else if(from.equalsIgnoreCase("ReturnRequest"))
		{
    		Intent intent =	new Intent(ReceivePaymentBySalesman.this,  SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
    	else if(from.equalsIgnoreCase(AppConstants.OPEN_CREDIT))
    		performPaymentProcess();
    	/*else if(from.equalsIgnoreCase("PrintSuccess"))
		{
		 showOrderCompletePopup();
		}*/
    }
    
    @Override
    public void onButtonNoClick(String from)
    {
    	super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("isFromArCollectiontrue"))
		{
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), getString(R.string.you_served_this_customer_successfully_another_order), getString(R.string.Yes), getString(R.string.No), "served");
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
		}
		else if(from.equalsIgnoreCase("askCredit"))
		{
			if(mallsDetails.paymentType.equalsIgnoreCase("CASH"))
				tvCash.performClick();
			else
				tvCheque.performClick();
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			showCustomDialog(ReceivePaymentBySalesman.this,getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served",false);
		}
		
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 2222)
    	{
    		AppConstants.isServeyCompleted = true;
    	}
    	else if(resultCode == 20000)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), getString(R.string.Your_payment_detail_has_been_prited_successfully), getString(R.string.OK), null , "");
    	//showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), getString(R.string.Your_payment_detail_has_been_prited_successfully), getString(R.string.OK), null , "PrintSuccess");
    	else if(resultCode == -10000)
    	{
    		if(mallsDetails.paymentType.equalsIgnoreCase("CASH"))
    		{
    			tvCheque.setVisibility(View.GONE);
    			tvCash.performClick();
    		}
    		else
    			tvCheque.performClick();
    		
    	}
    	else if(resultCode == -20000){
    		showOrderCompletePopup();
    	}
    }

	@Override
	public void onConnectionException(Object msg) 
	{
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPaymentDone)
			showOrderCompletePopup();
		else
			finish();
	}

	private void resetLayoutVisibility()
	{
		btnConfirm_Payment.setEnabled(true);
		btnConfirm_Payment.setClickable(true);
		
		
		btnContinue.setVisibility(View.VISIBLE);
		btnConfirm_Payment.setVisibility(View.GONE);
//		btnPrint.setVisibility(View.VISIBLE);
		
		btnCancel.setVisibility(View.GONE);
		
		etCashAmount.setEnabled(false);
		etCashAmount.setClickable(false);
		etCashAmount.setFocusableInTouchMode(false);
		
		etCheque_Details.setEnabled(false);
		etCheque_Details.setClickable(false);
		etCheque_Details.setFocusableInTouchMode(false);
		
		etChequeAmount.setEnabled(false);
		etChequeAmount.setClickable(false);
		etChequeAmount.setFocusableInTouchMode(false);
		
		tvSelectBanks.setEnabled(false);
		tvSelectBanks.setClickable(false);
		
		tvDate.setEnabled(false);
		tvDate.setClickable(false);
		
		btnPaymentSignClear.setEnabled(false);
		btnPaymentSignClear.setClickable(false);
		
		showOrderCompletePopup();
	}
	
	private boolean getValidateEnteredData()
	{
		boolean isValid = false;
		float totalAmount;
		
		totalAmount 	= 	StringUtils.getFloat(diffAmt.format(Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", "")))));
		
		if(!isCash && !isCheque)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select atleast one payment mode.", getString(R.string.OK), null, "");
		
		else if((isCheque)  && etCheque_Details.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter cheque number.", getString(R.string.OK), null, "");
		
		else if((isCheque )  && tvSelectBanks.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select bank name.", getString(R.string.OK), null, "");
		
		else if((isCheque)  && tvSelectBanks.getText().toString().equalsIgnoreCase("Other") && etBankName.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter bank name.", getString(R.string.OK), null, "");
		
		else if((isCheque) && tvDate.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select cheque date.", getString(R.string.OK), null, "");
		
		else if(totalAmount < (totalInvAmt))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Entered amount is less than invoice amount. Please enter correct amount.", getString(R.string.OK), null, "");
		
		else if(tvSelectRmnV.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select receipt method.", getString(R.string.OK), null, "");
		else if(!isSigned)
		{
			if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please take Receiver's signature.", getString(R.string.OK), null, "");
			else
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please take Customer's signature.", getString(R.string.OK), null, "");
		}else if(onAccount){
			totalOnaccPay=StringUtils.getFloat(etTotalAmount.getText().toString());
			if(!(totalOnaccPay>0)){
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter some amount to do payment.", getString(R.string.OK), null, "");
			}
			else{
				isValid = true; 
				
				if(isCash)
					PAYMENT_TYPE = CASH;
				else if(isCheque)
					PAYMENT_TYPE = CHEQUE;
			}
		}
		else 
		{
			isValid = true; 
			
			if(isCash)
				PAYMENT_TYPE = CASH;
			else if(isCheque)
				PAYMENT_TYPE = CHEQUE;
		}
		
		return isValid;
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(ReceivePaymentBySalesman.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);
		
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1	  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Payment");
		tv_poptitle1.setText("Successful");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment= (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		
		tv_poptitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
			
		//if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			btn_popup_collectpayment.setVisibility(View.GONE);
		
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				//mCustomDialog.dismiss();
				btnPrint.performClick();
			}
		});
		
		btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_collectpayment.setClickable(false);
		btn_popup_collectpayment.setEnabled(false);
		
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("served");
			}
		});
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	
	private void CheckClick()
	{
		tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked1),null, null, null);
		etCheque_Details.setText("");
		tvSelectBanks.setText("");
		tvSelectBanks.setTag(-1);
		etBankName.setText("");
		tvDate.setTag("");
		tvDate.setText("");
		etChequeAmount.setText("");
		llCheque.setVisibility(View.GONE);
		isCheque = false;
	}
	
	private void performPaymentProcess() 
	{
		disableView(btnConfirm_Payment);
		showLoader(getString(R.string.please_wait));
		
		Thread th = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				isPaymentDone = false;
				strReceiptNo = new OfflineDA().getNextSequenceNumber(OfflineDataType.PAYMENT);
				
				if(TextUtils.isEmpty(mallsDetails.currencyCode))
					mallsDetails.currencyCode = curencyCode;
				
				if(TextUtils.isEmpty(strReceiptNo) || (!TextUtils.isEmpty(strOrderId) && (SalesmanOrderPreview.vecMainProducts == null || SalesmanOrderPreview.vecMainProducts.size() <= 0))){
					isPaymentDone = false;
				}
				else
				{
					paymentHeaderDO						= 	new PaymentHeaderDO();
					paymentHeaderDO.AppPaymentId 		=  StringUtils.getUniqueUUID();
					paymentHeaderDO.RowStatus 			=  "1";
					paymentHeaderDO.ReceiptId 			=  strReceiptNo;
					paymentHeaderDO.PreReceiptId 		=  strReceiptNo;
					paymentHeaderDO.PaymentDate 		=  CalendarUtils.getCurrentDateTime();
					paymentHeaderDO.SiteId 				=  mallsDetails.site;
					paymentHeaderDO.EmpNo 				=  preference.getStringFromPreference(Preference.EMP_NO, "");
					paymentHeaderDO.Amount 				=  ""+StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", ""));
					paymentHeaderDO.CurrencyCode 		=  mallsDetails.currencyCode+"";
					paymentHeaderDO.Rate 				=  "1";
					paymentHeaderDO.VisitCode 			=  ""+mallsDetails.VisitCode;
					paymentHeaderDO.PaymentStatus 		=  "0";
					paymentHeaderDO.Status 				= 	isFromArCollection ? "0" : "-1";
					paymentHeaderDO.AppPayementHeaderId	= 	paymentHeaderDO.AppPaymentId;
					paymentHeaderDO.PaymentType 		=	"Collection";
					paymentHeaderDO.salesmanCode 		=	mallsDetails.salesmanCode;
					paymentHeaderDO.vehicleNo 			=	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					
					paymentHeaderDO.Receipt_Method_Name	=	tvSelectRmnV.getText().toString();
					
					//Customer Signature
					Bitmap bitmap 					= getBitmap(viewAgent);
					ByteArrayOutputStream stream 	= new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					paymentHeaderDO.CustomerSignature = Base64.encodeBytes(stream.toByteArray());
					storeImage(bitmap, AppConstants.CUSTOMER_SIGN);
					
					if(PAYMENT_TYPE.equalsIgnoreCase(CASH))
						paymentHeaderDO.vecPaymentDetails.add(getCashDetail(paymentHeaderDO.Amount ));
					else if(PAYMENT_TYPE.equalsIgnoreCase(CHEQUE))
						paymentHeaderDO.vecPaymentDetails.add(getChequeDetail(paymentHeaderDO.Amount ));
					else
					{
						paymentHeaderDO.vecPaymentDetails.add(getCashDetail(paymentHeaderDO.Amount ));
						paymentHeaderDO.vecPaymentDetails.add(getChequeDetail(paymentHeaderDO.Amount ));
					}
				if(!onAccount){
					float remAmt = StringUtils.getFloat(paymentHeaderDO.Amount) - totalInvAmt;
					if(remAmt > 0)
					{
						
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= AppConstants.OPEN_CREDIT;;
						pendingInvicesDO.balance 		= ""+(remAmt);
						pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
						pendingInvicesDO.INV_TYPE		= AppConstants.OPEN_CREDIT;
						pendingInvicesDO.invoiceDate 	= CalendarUtils.getOrderPostDate();
						pendingInvicesDO.invoiceDateToShow	= CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
						pendingInvicesDO.orderId 		= "";
						pendingInvicesDO.isNewleyAdded	=	true;
						arrInvoiceNumbers.add(pendingInvicesDO);
					}
					
					for(PendingInvicesDO objInvicesDO : arrInvoiceNumbers)
					{
						PaymentInvoiceDO paymentInvoiceDO = new PaymentInvoiceDO();
						
						paymentInvoiceDO.RowStatus 		= 	"1";
						paymentInvoiceDO.ReceiptId		= 	strReceiptNo;
						paymentInvoiceDO.TrxCode		= 	objInvicesDO.invoiceNo;
						paymentInvoiceDO.TrxType		= 	objInvicesDO.INV_TYPE;
						paymentInvoiceDO.totalAmt		= 	objInvicesDO.lastbalance;
						paymentInvoiceDO.Amount			= 	objInvicesDO.balance;
						paymentInvoiceDO.CurrencyCode	= 	curencyCode+"";
						paymentInvoiceDO.Rate			= 	"1";
						paymentInvoiceDO.PaymentStatus	= 	"0";
						paymentInvoiceDO.PaymentType	= 	PAYMENT_TYPE;
						paymentInvoiceDO.CashDiscount 	= 	"0";
						paymentInvoiceDO.Ebs_Ref_No 	= 	objInvicesDO.ebs_ref_no;
						
						paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
					}
				}else{
					 
					
					  PaymentInvoiceDO paymentInvoiceDO = new PaymentInvoiceDO();
				      paymentInvoiceDO.RowStatus   =  "1";
				      paymentInvoiceDO.ReceiptId  =  strReceiptNo;
				      paymentInvoiceDO.TrxCode  =  PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT;
				      paymentInvoiceDO.TrxType        =  PaymentInvoiceDO.TRX_CODE_ON_ACCOUNT_PAYMENT;
				      paymentInvoiceDO.Amount   =  ""+StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", ""));
				      paymentInvoiceDO.CurrencyCode =  curencyCode+"";
				      paymentInvoiceDO.Rate   =  "1";
				      paymentInvoiceDO.PaymentStatus =  "0";
				      paymentInvoiceDO.PaymentType =  PAYMENT_TYPE;
				      paymentInvoiceDO.CashDiscount  =  "0";
				      paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
				}
				
					
					boolean isInserted = new PaymentDetailDA().insertPaymentDetails(paymentHeaderDO , preference.getStringFromPreference(Preference.EMP_NO, ""), preference);
					
						if(isInserted)
						{
							if(!onAccount){
								if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() > 0)
									isPaymentDone = new PaymentDetailDA().updatePaymentStatus(mallsDetails, arrInvoiceNumbers, "D", strReceiptNo);
							}else{
								isPaymentDone=true;
							}
									
							new CustomerDA().updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
							
							if(isNetworkConnectionAvailable(ReceivePaymentBySalesman.this))
							{
								//if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
									uploadData(AppStatus.POST_RECIEPT, AppStatus.TODAY_DATA);
							}
						}
					}
				

			}
		});
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// perform post payment operations on UI thread.
		if(isPaymentDone && !isFromArCollection /*&& mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)*/)
		{
			Intent intentBrObj = new Intent();
			intentBrObj.setAction(AppConstants.ACTION_CASH_SALES);
			sendBroadcast(intentBrObj);
		}
		setActionInViews(etTotalAmount, false);
		btnConfirm_Payment.setEnabled(true);
		btnConfirm_Payment.setClickable(true);
		hideLoader();

		if(isPaymentDone)
			resetLayoutVisibility();
		else if(TextUtils.isEmpty(strReceiptNo))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Payment sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
		else
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Error occurred while placing order. Please go back to dashboard and try again.", getString(R.string.OK), null, "dashboard", false);
	}
}
