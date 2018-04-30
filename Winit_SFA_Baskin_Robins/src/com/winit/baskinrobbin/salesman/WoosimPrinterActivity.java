package com.winit.baskinrobbin.salesman;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataobject.BaseComparableDO;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.EOTSummaryPrintDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentInvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.NumberToEnglish;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.woosim.bt.WoosimPrinter;

public class WoosimPrinterActivity extends Activity implements OnClickListener 
{
	private static String EXTRA_DEVICE_ADDRESS = "device_address";
	private final static byte DATA = 0x44, ETX = 0x03, EOT = 0x04, NACK	= 0x15, MSR_FAIL = 0x4d, ACK  = 0x06;;
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter, mNewDevicesArrayAdapter;
	private static String address, type = "";
	private WoosimPrinter woosim;
	private Button btnOpen, btnClose, btnPrint2, btnPrint3, btnMSR23, btnMSR123, btnCardCancel, btnFinish, btnPrintImg1, btnPrintImg2, btnReprint , btnFinishPrint;
	private CheckBox cheProtocol;
	private EditText editTrack1, editTrack2, editTrack3;
	public ProgressDialog progressdialog;
	private TextView tvPrintHeader;
	private final static String EUC_KR = "EUC-KR";///*"EUC-KR"*/;
	private byte[] extractdata = new byte[300], cardData;
	private Vector<ProductDO> vecSalesOrderProducts;
	private float totalPrice, totalDiscount, roundOffVal,totalTaxAmt,totNetAmt;
	private CONSTANTOBJ CALLFROM; 
	private boolean isPrinted = false, isPDUser=false;
	private Preference preference;
	private String cardDetail="",cardName = "",strCardName = "", strReceiptNo,strMovementId, strSelectedDateToPrint, strOrderId = "",LPO;
	private int movementType;
	public ArrayList<BaseComparableDO> customerList;
	private JourneyPlanDO mallsDetails;
	private PaymentHeaderDO objPaymentDO;
	private ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice;
	private OrderDO orderDO;
	private ArrayList<VanLoadDO>vecOrdProduct;
	private DecimalFormat deffAmt, deffStock;
	private String curencyCode = "AED", from, INV  = "%1$-10.10s %2$-38.38s\r\n";
	private LoadRequestDO loadRequestDO ;
	private ArrayList<InventoryObject> arrInventory;
	private EOTSummaryPrintDO eotSummaryPrintDO;
	private Customer_InvoiceDO customer_InvoiceDO;
	public String TRN="";
	boolean isFromOS=false;
	String TrxReasonCode="";
	public String cityCode="";
	public int noOfRoundingOffdigits=2;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		
		preference = new Preference(WoosimPrinterActivity.this);
		noOfRoundingOffdigits=new CommonDA().getRoundOffValueFromDatabaseBasedonCountry(preference.getStringFromPreference(Preference.CURRENCY_CODE,""));
		if(noOfRoundingOffdigits==0)
			noOfRoundingOffdigits=2;
		deffAmt = new DecimalFormat("##.###");
		deffAmt.setMinimumFractionDigits(noOfRoundingOffdigits);
		deffAmt.setMaximumFractionDigits(noOfRoundingOffdigits);
		
		deffStock = new DecimalFormat("##.###");
		deffStock.setMinimumFractionDigits(noOfRoundingOffdigits);
		deffStock.setMaximumFractionDigits(noOfRoundingOffdigits);
//		deffStock = new DecimalFormat("##.###");
//		deffStock.setMinimumFractionDigits(3);
//		deffStock.setMaximumFractionDigits(3);

		curencyCode = preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
		showLoader("Searching devices...");
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
			// hide pricing 
			isPDUser = true;
		else
			isPDUser = false;
		
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				initialzeViews();
			}
		}, 100);
	}
	
	private void initialzeViews()
	{
		showLoader("Configuring Connection...");

		new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
//				String strTRN=preference.getStringFromPreference(Preference.ORG_CODE,"");
//				int orgCODE=StringUtils.getInt(strTRN);
//				if(orgCODE == 181 || orgCODE == 619)
//				TRN=new SettingsDA().getSettingTRN("TRN-181");
//				else
//				TRN=new SettingsDA().getSettingTRN("TRN-619");
				mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
				if(mallsDetails!=null)
				 cityCode=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);

			if(cityCode.equalsIgnoreCase("AE")){
				TRN=new SettingsDA().getSettingTRN("TRN-181");
			}else if(cityCode.equalsIgnoreCase("SA"))
			{
				TRN=new SettingsDA().getSettingTRN("TRN-619");
			}


				runOnUiThread(new Runnable() 
				{
					@SuppressWarnings("unchecked")
					@Override
					public void run() 
					{
						//Initializing the WoosimPrinter object
						woosim = new WoosimPrinter();
						woosim.setHandle(acthandler);
						//getting vector from intent
						if(getIntent().getExtras() != null)
						{
							//this variable will get the the call from 
							CALLFROM 			  = (CONSTANTOBJ) getIntent().getExtras().get("CALLFROM");
							//condition to print the Sales Order
							if(CALLFROM == CONSTANTOBJ.NEW_CUSTOMER)
							{
								mallsDetails     =   (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								type 			  =   "New Customer registration Form";
								
								checkIsPDCustomer(mallsDetails);
							}
							else if(CALLFROM == CONSTANTOBJ.PRINT_SALES)
							{
								if(SalesmanOrderPreview.vecMainProducts != null)
									vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanOrderPreview.vecMainProducts.clone();
									
								totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");	
								type				  = 	"Invoice Preview";
								strOrderId			  = 	getIntent().getExtras().getString("OrderId");
								LPO			 	 	  =		getIntent().getExtras().getString("LPO");
								roundOffVal			  =		getIntent().getExtras().getFloat("roundOffVal");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
							
								checkIsPDCustomer(mallsDetails);
							}
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_RECEIPT)
							{
								totalPrice			  = 	getIntent().getExtras().getFloat("totalAmount");	
								type				  = 	"Invoice Preview";
								strReceiptNo		  =		getIntent().getExtras().getString("strReceiptNo");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								objPaymentDO  	  	  = 	(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
								checkIsPDCustomer(mallsDetails);
							}
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT)
							{
								if(SalesmanOrderPreview.vecMainProducts != null)
									vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanOrderPreview.vecMainProducts.clone();
								HashMap<String,String> hmArabic = new CommonDA().getAllArabicItems(strOrderId);
								for (ProductDO productDO : vecSalesOrderProducts)
									productDO.Description1=hmArabic.get(productDO.SKU);
								
								totalPrice			  = 	getIntent().getExtras().getFloat("totalAmount");	
								strReceiptNo		  =		getIntent().getExtras().getString("strReceiptNo");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								objPaymentDO  	  	  = 	(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
								strOrderId			  = 	getIntent().getExtras().getString("OrderId");
								LPO				  	  = 	getIntent().getExtras().getString("LPO");
								roundOffVal			  =		getIntent().getExtras().getFloat("roundOffVal");
								type				  = 	"";
								checkIsPDCustomer(mallsDetails);
							}
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_SUMMARY)
							{
								arrayListCustomerInvoice =  (ArrayList<Customer_InvoiceDO>) getIntent().getExtras().getSerializable("arrayList");
								strSelectedDateToPrint	 =  getIntent().getExtras().getString("strSelectedDateToPrint");
								type	 				 =  getIntent().getExtras().getString("type");
								checkIsPDCustomer(null);
							}
							else if(CALLFROM == CONSTANTOBJ.PRINT_EOT_SUMMARY)
							{
								eotSummaryPrintDO =  (EOTSummaryPrintDO) getIntent().getExtras().getSerializable("EOTSummaryPrintDO");
								type	 		  =  getIntent().getExtras().getString("type");
								checkIsPDCustomer(null);
							}
							else if(CALLFROM == CONSTANTOBJ.ORDER_SUMMARY)
							{
								if(SalesmanOrderDetail.vecOrdProduct != null)
									vecSalesOrderProducts  = 	(Vector<ProductDO>) SalesmanOrderDetail.vecOrdProduct.clone();
								
								type				  = 	getIntent().getExtras().getString("copy");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								orderDO			  	  =		(OrderDO) getIntent().getExtras().get("orderDo");
								objPaymentDO	  	  =		(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
								
								if(orderDO != null)
								{
									strOrderId        =     orderDO.OrderId;
									roundOffVal       =     orderDO.roundOffVal;
								}
								
								if(objPaymentDO != null)
									strReceiptNo = objPaymentDO.ReceiptId;
								
								checkIsPDCustomer(mallsDetails);
							}	
							else if(CALLFROM == CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE)
							{
								vecOrdProduct 		  =  (ArrayList<VanLoadDO>) getIntent().getExtras().getSerializable("itemforVerification");
								strMovementId=getIntent().getExtras().getString("strMovementNo");
								movementType=StringUtils.getInt(getIntent().getExtras().getString("movementType"));
								checkIsPDCustomer(null);
							}
							
							else if(CALLFROM == CONSTANTOBJ.PRINT_LOAD_REQUEST)
							{
								vecOrdProduct 		  =  (ArrayList<VanLoadDO>) getIntent().getExtras().getSerializable("array");
								loadRequestDO		  =  (LoadRequestDO) getIntent().getExtras().getSerializable("loadRequestDO");
								strMovementId=loadRequestDO.MovementCode;
								movementType=StringUtils.getInt(loadRequestDO.MovementType);
								checkIsPDCustomer(null);
							}
							
							else if(CALLFROM == CONSTANTOBJ.PRINT_VERIFY_STOCK_UNLOAD)
							{
//						      	Have  to Write for This // from VerifyReturnItemFromVehicle
								checkIsPDCustomer(null);
							}
							else if(CALLFROM == CONSTANTOBJ.PRINT_RETURN_INVENTORY)
							{
								arrInventory 		  =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vec");
								from  				  =  getIntent().getExtras().getString("type");
								
								if(from != null && from.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
									from = "Replaced Inventory";
								else
									from = "Returned Inventory";
							}
							else if(CALLFROM == CONSTANTOBJ.PRINT_INVENTORY)
							{
								arrInventory =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vec");
								from  		 = "Stock Inventory";
							}
							else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_RETURN)
							{
//						      	Have  to Write for This from SalesmanReturnOrderPreview
								vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanReturnOrderPreview.vecMainProducts.clone();
								totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");	
								type				  = 	"Invoice Preview";
								strOrderId			  = 	getIntent().getExtras().getString("OrderId");
								TrxReasonCode			  = 	getIntent().getExtras().getString("TrxReasonCode");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								from			      = 	getIntent().getExtras().getString("from");
								LPO			 	 	  =		getIntent().getExtras().getString("LPO");
								checkIsPDCustomer(mallsDetails);
							}
							
							else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_REPLACE)
							{
//						      	Have  to Write for This from SalesmanReturnOrderPreview
								vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanReplacementPreview.vecMainProducts.clone();
								totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");	
								type				  = 	"Invoice Preview";
								strOrderId			  = 	getIntent().getExtras().getString("OrderId");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								from			      = 	getIntent().getExtras().getString("from");
							
								checkIsPDCustomer(mallsDetails);
							}
							
							else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_REPLACE_SUMMARY)
							{
//						      	Have  to Write for This from SalesmanReturnOrderPreview
								vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanReplacementOrderDetail.vecOrdProduct.clone();
								totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");	
								type				  = 	"Invoice Preview";
								strOrderId			  = 	getIntent().getExtras().getString("OrderId");
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								from			      = 	getIntent().getExtras().getString("from");
							
								checkIsPDCustomer(mallsDetails);
							}
							
							else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE)
							{
								vecSalesOrderProducts 	= 	(Vector<ProductDO>) SalesmanLPOOrderPreview.vecMainProducts.clone();
								orderDO			  		= 	(OrderDO) getIntent().getExtras().get("OrderId");
								mallsDetails  		  	=   (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
							}
							else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY)
							{
								ArrayList<ProductDO>arr  = (ArrayList<ProductDO>) getIntent().getExtras().get("array");
								vecSalesOrderProducts = 	new Vector<ProductDO>(arr);
								mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								orderDO			  	  =		(OrderDO) getIntent().getExtras().get("orderDo");
							}
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_SEP_SUMMARY)
							{
								mallsDetails  		=  (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
								customer_InvoiceDO	=  (Customer_InvoiceDO) getIntent().getExtras().get("object");
							}
						}
						else
							checkIsPDCustomer(null);
						
						hideLoader();
						showDeviceLists(true);
					}
				});				
			}
		}).start();
	}
	
	public void checkIsPDCustomer(JourneyPlanDO objJourneyPlanDO)
	{
		try 
		{
			if(objJourneyPlanDO != null)
			{
				if(objJourneyPlanDO.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
					// hide pricing 
					isPDUser = true;
				else
					isPDUser = false;
			}
			else
			{
				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_PD))
					isPDUser = true;
				else
					isPDUser = false;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			isPDUser = false;
		}
	}
	
	
	public void showDeviceLists(final boolean isPaired)
	{
		setContentView(R.layout.device_list);
		setResult(Activity.RESULT_CANCELED);
	
		mPairedDevicesArrayAdapter 	= 	new ArrayAdapter<String>(WoosimPrinterActivity.this,	R.layout.device_name);
		mNewDevicesArrayAdapter 	= 	new ArrayAdapter<String>(WoosimPrinterActivity.this, R.layout.device_name);
	
		/* Find and set up the ListView for paired devices*/
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setCacheColorHint(0);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);
	
		 /*Find and set up the ListView for newly discovered devices*/
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setCacheColorHint(0);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);
	
		/* Register for broadcasts when a device is discovered*/
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		WoosimPrinterActivity.this.registerReceiver(mReceiver, filter);
	
		/* Register for broadcasts when discovery has finished*/
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		WoosimPrinterActivity.this.registerReceiver(mReceiver, filter);
	
		/* Get the local Blue-tooth adapter*/
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	
		if(mBtAdapter!=null)
		{
			doDiscovery();
			// Get a set of currently paired devices
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
			
			// If there are paired devices, add each one to the ArrayAdapter
			if (pairedDevices.size() > 0) 
			{
				findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
				for (BluetoothDevice device : pairedDevices) 
				{
					mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					if(device.getName().equalsIgnoreCase("woosim") && isPaired)
					{
						mBtAdapter.cancelDiscovery();
						// Get the device MAC address, which is the last 17 chars in the
						// View
						address = device.getAddress();
						// Create the result Intent and include the MAC address
						Intent intent = new Intent();
						intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
						System.out.println("address" + address);
						setContentView(R.layout.woosim);
						createButton();
						// Set result and finish this Activity
						setResult(Activity.RESULT_OK, intent);
						btnOpen.performClick();
						break;
					}
				}
			} 
		}
		else
			hideLoader();
	}
	
	public Handler acthandler = new Handler() 
	{

		public void handleMessage(Message msg)
		{
			if (msg.what == DATA) 
			{
				LogUtils.errorLog("+++Activity+++", "******0x01");
				Object obj1 = msg.obj;
				cardData = (byte[]) obj1;
				ToastMessage();
				hideLoader();
				
				if(cardDetail.equalsIgnoreCase("") || cardName.equalsIgnoreCase("") || strCardName.equalsIgnoreCase(""))
				{
					showCustomDialog(WoosimPrinterActivity.this, "Sorry", "Card is not swiped successfully.Do you want swipe again?", "Yes", "No", "cardswipeAgain", false);
					LogUtils.errorLog("+++Activity+++", "MSRFAIL: [" + msg.arg1 + "]: ");
					editTrack1.setText("MSRFAIL");
					
				}
				else
				{
					showCustomDialog(WoosimPrinterActivity.this, "Thankyou", "Your card swiped successfully.", "Ok", null, "cardswipesuccessfully", false);
				}
				
			} 
			else if (msg.what == MSR_FAIL)
			{
				hideLoader();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry", "Card is not swiped successfully.Do you want swipe again?", "Yes", "No", "cardswipe");
				LogUtils.errorLog("+++Activity+++", "MSRFAIL: [" + msg.arg1 + "]: ");
				editTrack1.setText("MSRFAIL");
			} 
			else if (msg.what == EOT)
			{
				LogUtils.errorLog("+++Activity+++", "******EOT");
				editTrack1.setText("EOT");
			}
			else if (msg.what == ETX) 
			{
				LogUtils.errorLog("+++Activity+++", "******ETX");
				editTrack2.setText("ETX");
			}
			else if (msg.what == NACK) 
			{
				LogUtils.errorLog("+++Activity+++", "******NACK");
				editTrack3.setText("NACK");
			} 
			else if (msg.what == ACK) 
			{
				LogUtils.errorLog("+++Activity+++", "******ACK");
				editTrack3.setText("ACK");
			}
		}
	};

	private void ToastMessage() 
	{
		byte[] track1Data = new byte[76];
		byte[] track2Data = new byte[37];
		byte[] track3Data = new byte[104];

		int dataLength = woosim.extractCardData(cardData, extractdata);
		int i = 0, j = 0, k = 0;
		if (dataLength == 76) {
			LogUtils.errorLog("dataLength == 76", "dataLength == 76");
			for (i = 0; i < 76; i++) {
				track1Data[i] = extractdata[i];
			}
			String str = new String(track1Data);
			editTrack1.setText(str);
			cardDetail =str;

			
			editTrack2.setText("No Data");
			editTrack3.setText("No Data");
		} else if (dataLength == 37) {
			LogUtils.errorLog("dataLength == 37", "dataLength == 37");
			for (i = 0; i < 37; i++) {
				track2Data[i] = extractdata[i];
			}
			String str = new String(track2Data);
			editTrack1.setText("No Data");
			editTrack2.setText(str);
			editTrack3.setText("No Data");
		} else if (dataLength == 104) {
			LogUtils.errorLog("dataLength == 104", "dataLength == 104");
			for (i = 0; i < 104; i++) {
				track3Data[i] = extractdata[i];
			}
			String str = new String(track3Data);
			editTrack1.setText("No Data");
			editTrack2.setText("No Data");
			editTrack3.setText(str);
		}
		// 1,2track
		else if (dataLength == 113) {
			LogUtils.errorLog("+++Activitiy+++", "dataLength: " + dataLength);
			for (i = 0; i < 113; i++) {
				if (i < 76) {
					track1Data[i] = extractdata[i];
				} else {
					track2Data[j++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = new String(track2Data);
			String str3 = "No Data";
			cardName = str1;
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
		}
		// 1,3track
		else if (dataLength == 180) {
			for (i = 0; i < 180; i++) {
				if (i < 76) {
					track1Data[i] = extractdata[i];
				} else {
					track3Data[j++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = "No Data";
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
		}
		// 2,3track
		else if (dataLength == 141) 
		{
			for (i = 0; i < 141; i++)
			{
				if (i < 37) 
				{
					track2Data[i] = extractdata[i];
				} 
				else 
				{
					track3Data[j++] = extractdata[i];
				}
			}

			String str1 = "No Data";
			String str2 = new String(track2Data);
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
			
		}
		// 1,2,3track
		else if (dataLength == 217) 
		{
			for (i = 0; i < 217; i++) 
			{
				if (i < 76) 
				{
					track1Data[i] = extractdata[i];
				} 
				else if (i >= 76 && i < 113)
				{
					track2Data[j++] = extractdata[i];
				}
				else 
				{
					track3Data[k++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = new String(track2Data);
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
			LogUtils.errorLog("str2",""+str2);
			
		}
		if(cardName != null && cardName.contains("^") && cardName.contains("/") )
		{
			strCardName = cardName.substring(cardName.indexOf("^")+1, cardName.indexOf("/"));
			if(strCardName != null)
				strCardName = strCardName.trim();
		}
	}
	/* Method to print the generated sales order in 4 inch mode*/
	 
	private void PrintSalesOrder()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		String strType="RECEIPT DETAIL";
		
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String formatForAddress = "%1$-4.4s %2$-41.41s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String LINE 			= "%1$-4.4s %2$-78.78s\r\n";
//		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
//		String format1 			= "%1$-4.4s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
		String format1 			= "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-6.6s %11$-7.7s \r\n";
//		String formatPriceNormal= "%1$-10.10s %2$46s";
		String formatPriceNormal= "%1$-4.4s %2$-46.46s \r\n" ;
		String formatPriceNormal01= "%1$-8.8s %2$-50.50s \r\n";
//		String formater			= "%1$-10.10s";
		String formater			= "%1$-4.4s";
//		String formatPriceBold  = "%1$-10.10s %2$-17s";
		String formatPriceBold  = "%1$-4.4s %2$-17s";
//		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
		String price 			= "%1$-4.4s %2$-41.41s %3$38.38s\r\n";

		String formatInvoice 	= "%1$10.10s %2$15.15s %3$15.15s %4$15.15s %5$15.15s\r\n";
		String totalAmt			= "%1$-10.10s %2$-38.38s %3$31.31s\r\n";

		if(mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
//			woosim.saveSpool(EUC_KR, String.format(formatHeader,"","CASH INVOICE",""), 0x11, true);
			woosim.saveSpool(EUC_KR, String.format(formatHeader,"","TAX INVOICE",""), 0x11, true);
		else
//			woosim.saveSpool(EUC_KR, String.format(formatHeader,"","CREDIT INVOICE",""), 0x11, true);
			woosim.saveSpool(EUC_KR, String.format(formatHeader,"","TAX INVOICE",""), 0x11, true);

		if(cityCode.equalsIgnoreCase("SA"))
			printHeaderForSAR();
		else if(cityCode.equalsIgnoreCase("AE"))
			printHeader();

		if(TextUtils.isEmpty(strOrderId))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");

		if(TRN.equalsIgnoreCase(""))
		woosim.saveSpool(EUC_KR, String.format(INV,"","N/A"), 0x09, true);
		else
		woosim.saveSpool(EUC_KR, String.format(INV,"",""+TRN), 0x09, true);
		woosim.saveSpool(EUC_KR, String.format(INV,"","Tax Invoice No: "+strOrderId), 0x09, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.partyName,mallsDetails.siteName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
		
		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Invoice No: "+strOrderId,"Invoice Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		if (mallsDetails.VatNumber.equalsIgnoreCase(""))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer TRN#:N/A","Invoice Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		else
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer TRN#:"+mallsDetails.VatNumber,"Invoice Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		
		if(!TextUtils.isEmpty(LPO))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "LPO : "+LPO), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), "Delivery/Supply Date :"+CalendarUtils.getCurrentDate()), 0, true);
		}else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Sales Person Mobile : "+ printMobileNumber(mallsDetails)), 0, true);

//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivery/Supply Date :"+CalendarUtils.getCurrentDate() , " "), 0, true);

		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		String lines = "==============================================================================================\r\n";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
//		woosim.saveSpool(EUC_KR,String.format(format1,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","TAX","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format1,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","VAT","VAT","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format1,"",""," ","","","","","","Rate","Amount",""), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		totalDiscount  = 0;
		totalTaxAmt=0;
		totNetAmt=0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			ProductDO productDO = vecSalesOrderProducts.get(i);
			totalPrice         += (productDO.invoiceAmount-productDO.LineTaxAmount);
			totNetAmt         += (productDO.invoiceAmount);
			totalTaxAmt         += productDO.LineTaxAmount;
//			totalDiscount      += productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits);
			double discount=0;

				if(mallsDetails.customerType !=null && (( mallsDetails.customerType.equalsIgnoreCase("CASH") && !(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT )  )))
//						||				 (  mallsDetails.customerType.equalsIgnoreCase("CREDIT") &&  CALLFROM == CONSTANTOBJ.PRINT_SALES ) ))
//				if(mallsDetails.customerType !=null && (( mallsDetails.customerType.equalsIgnoreCase("CASH") && !(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT )  )||
//				 (  mallsDetails.customerType.equalsIgnoreCase("CREDIT") &&  CALLFROM == CONSTANTOBJ.PRINT_SALES ) ))
					discount = productDO.DiscountAmt;
				else if(productDO.quantityBU==0)
                    discount = productDO.DiscountAmt;
				else
					discount = productDO.DiscountAmt * StringUtils.getFloat(productDO.preUnits);
			totalDiscount+=discount;



//			 =productDO.DiscountAmt*StringUtils.getDouble(productDO.units);

			woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(discount),/*productDO.DiscountAmt*/""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
//			woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt*StringUtils.getDouble(productDO.units)),/*productDO.DiscountAmt*/""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
//			woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,"",""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt/**StringUtils.getDouble(productDO.units)*/),/*productDO.DiscountAmt*/""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
			woosim.saveSpool(EUC_KR,String.format(formatPriceNormal01,"",""+productDO.Description), 0, false);
		}

		
		/*woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC%","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice    = 0;
		totalDiscount = 0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			totalPrice 		+= vecSalesOrderProducts.get(i).invoiceAmount;
			totalDiscount   += vecSalesOrderProducts.get(i).discountAmount;
//			totalDiscount   += vecSalesOrderProducts.get(i).discountAmount*StringUtils.getFloat(vecSalesOrderProducts.get(i).preUnits);
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).preUnits,vecSalesOrderProducts.get(i).UOM,""+deffAmt.format(vecSalesOrderProducts.get(i).itemPrice),vecSalesOrderProducts.get(i).Discount,""+deffAmt.format(vecSalesOrderProducts.get(i).invoiceAmount)), 0, false);
		}
	*/	
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Gross Amount",deffAmt.format(totalPrice+totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Discount Amount",deffAmt.format(totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total VAT Amount",deffAmt.format(totalTaxAmt)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Round Off Amount",deffAmt.format(roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		
		String line = "----------------------------------------------------------------------------------\r";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(totalPrice + roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(totNetAmt + roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatPriceBold,"","Amount in Words:"), 0, true);
		
		
//		woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice + roundOffVal)))+"\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totNetAmt + roundOffVal)))+"\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);//commented on 03JAN
		if(cityCode.equalsIgnoreCase("AE"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else if(cityCode.equalsIgnoreCase("SA"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Jumeirah Trading Co. Ltd.\r\n"), 0, true);


		try 
		{
			woosim.saveSpool(EUC_KR, "", 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			woosim.saveSpool(EUC_KR,"", 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);

		if(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT || CALLFROM ==CONSTANTOBJ.ORDER_SUMMARY){

			woosim.saveSpool(EUC_KR, "                                      "+strType.toUpperCase()+"   \r\n", 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

			if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
				strReceiptNo = objPaymentDO.ReceiptId;

			if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
				strReceiptNo =   preference.getStringFromPreference(Preference.RECIEPT_NO, "");

			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Receipt Number: "+strReceiptNo , "Date : "+mydate), 0, false);

			if(objPaymentDO!=null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0)
			{
				for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
				{
					if(paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE"))
					{
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type            : ",paymentDetailDO.PaymentTypeCode), 0, true);
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque No               : ",paymentDetailDO.ChequeNo), 0, false);
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bank Name               : ",paymentDetailDO.BankName), 0, false);
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque Date             : ",CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)), 0, false);
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                  : ",paymentDetailDO.Amount+" "+curencyCode+""), 0, false);
					}
					else
					{
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type            : ",paymentDetailDO.PaymentTypeCode), 0, true);
						woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                  : ",paymentDetailDO.Amount), 0, false);
						woosim.saveSpool(EUC_KR, "\r\n", 0, false);
					}
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				}
			}

			float ftBalDue = 0f ;
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);

			woosim.saveSpool(EUC_KR, String.format(formatInvoice,"","Invoice Number","Total Amount","Paid Amount", "Balance Amount"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			if(objPaymentDO.vecPaymentInvoices != null)
				for( int i = 0 ; i < objPaymentDO.vecPaymentInvoices.size() ; i++ )
				{
					PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
					float ftTotal   = 	StringUtils.getFloat(paymentInvoiceDO.totalAmt);
					float ftPaid    = 	StringUtils.getFloat(paymentInvoiceDO.Amount);
					float balance   =   ftTotal - ftPaid;
					ftBalDue 	   +=   ftPaid;

					woosim.saveSpool(EUC_KR,String.format(formatInvoice, "", paymentInvoiceDO.TrxCode ,deffAmt.format(ftTotal), deffAmt.format(ftPaid),deffAmt.format(balance)), 0, false);
				}
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			woosim.saveSpool(EUC_KR, String.format(totalAmt, "", "Total ", deffAmt.format(ftBalDue)+" "+mallsDetails.currencyCode+""+" \r\n"), 0x8, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","Customer Signature"), 0, true);

			try
			{
				woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
				String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
				woosim.printBitmap(sign1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}


		}else {

			woosim.saveSpool(EUC_KR, String.format(LINE, "", "REMARKS"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "1. Received complete invoiced quantity in good condition."), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "2. Official receipt is mandatory for payments."), 0, false);

			if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)) {
//				woosim.saveSpool(EUC_KR, String.format(LINE, "", "3. Check should be issued in favor of �Galadari Ice Cream Co. Ltd. (L.L.C)�."), 0, false);
				if(cityCode.equalsIgnoreCase("AE"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else if(cityCode.equalsIgnoreCase("SA"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Jumeirah Trading Co. Ltd.\r\n"), 0, true);
			}
		}
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	private void PrintReturnOrder()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
//		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
//		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
//
//		String formatPriceBold  = "%1$-10.10s %2$-17s";
//		String formatPriceNormal= "%1$-10.10s %2$46s";
//		String formater			= "%1$-10.10s";
//		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-4.4s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-4.4s %2$-78.78s\r\n";
		String format 			= "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-5.5s %11$-7.7s \r\n";
		String formatReason     = "%1$-4.4s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";

		String formatPriceBold  = "%1$-4.4s %2$-17s";
		String formatPriceNormal= "%1$-4.4s %2$46s";
		String formater			= "%1$-4.4s";
		String price 			= "%1$-4.4s %2$-41.41s %3$38.38s\r\n";

//		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","RETURN ORDER",""), 0x11, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Tax Credit Note",""), 0x11, true);

		printHeader();
		
		if(strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		
//		woosim.saveSpool(EUC_KR, String.format(INV,"","GRV No: "+strOrderId), 0x09, true);
		woosim.saveSpool(EUC_KR, String.format(INV,"","N/A"), 0x09, true);
		woosim.saveSpool(EUC_KR, String.format(INV,"","Tax Credit Note#: "+strOrderId), 0x09, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,mallsDetails.partyName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
		
		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","GRV No: "+strOrderId,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		if (mallsDetails.VatNumber.equalsIgnoreCase(""))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN#: N/A","Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		else
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN#: "+mallsDetails.VatNumber,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		
		if(!TextUtils.isEmpty(LPO))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Customer GRV No : "+LPO), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), ""), 0, true);
		}
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Customer GRV No : : "+LPO), 0, true);
		
		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		String lines = "====================================================================================\r\n";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
//		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","VAT","VAT","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format,"","","","","","","","","Rate","Amount","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		totalDiscount  = 0;
		double totalTAX=0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			ProductDO productDO = vecSalesOrderProducts.get(i);
			totalPrice         += productDO.invoiceAmount;
			totalDiscount      += productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits);
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),productDO.SKU,productDO.Description,""+productDO.preUnits,productDO.UOM,""+deffAmt.format(productDO.itemPrice),productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits),""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
			
			woosim.saveSpool(EUC_KR, String.format(formatReason,"","Ex. Date: "+vecSalesOrderProducts.get(i).strExpiryDate,"Reason: "+vecSalesOrderProducts.get(i).reason, "Lot. No.: "+vecSalesOrderProducts.get(i).LotNumber), 0, true);
			totalTAX+=productDO.LineTaxAmount;
		}
		/*woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC%","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		totalDiscount = 0;
		
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			totalPrice += vecSalesOrderProducts.get(i).invoiceAmount;
			totalDiscount   += vecSalesOrderProducts.get(i).discountAmount;
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).preUnits,vecSalesOrderProducts.get(i).UOM,""+deffAmt.format(vecSalesOrderProducts.get(i).itemPrice),vecSalesOrderProducts.get(i).Discount,""+deffAmt.format(vecSalesOrderProducts.get(i).invoiceAmount)), 0, false);
			
			woosim.saveSpool(EUC_KR, String.format(formatReason,"","Ex. Date: "+vecSalesOrderProducts.get(i).strExpiryDate,"Reason: "+vecSalesOrderProducts.get(i).reason, "Lot. No.: "+vecSalesOrderProducts.get(i).LotNumber), 0, true);
		}*/
		
		String line = "----------------------------------------------------------------------------------\r";
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Gross Amount",deffAmt.format(totalPrice+totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Discount Amount",deffAmt.format(totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Tax Amount",deffAmt.format(totalTAX)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(totalPrice)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatPriceBold,"","Amount in Words:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice)))+"\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		if(cityCode.equalsIgnoreCase("AE"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else if(cityCode.equalsIgnoreCase("SA"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Jumeirah Trading Co. Ltd.\r\n"), 0, true);
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;



	}
	private void PrintReturnOrderNew()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;

//		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
//		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
//
//		String formatPriceBold  = "%1$-10.10s %2$-17s";
//		String formatPriceNormal= "%1$-10.10s %2$46s";
//		String formater			= "%1$-10.10s";
//		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-4.4s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-4.4s %2$-78.78s\r\n";
		String format 			= "%1$-4.4s %2$-3.3s %3$-14.14s %4$-13.13s %5$9.9s %6$-9.9s %7$-9.9s %8$-7.7s %9$-7.7s   \r\n";
		String format01 			= "%1$-4.4s %2$-3.3s %3$-11.11s %4$-11.11s %5$3.3s %6$3.3s %7$9.9s %8$-9.9s %9$-9.9s %10$-7.7s %11$-7.7s   \r\n";
		String format1 			= "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-5.5s  \r\n";
		String formatReason     = "%1$-4.4s %2$-35.35s %3$-21.21s %4$-18.18s\r\n";

		String formatPriceBold  = "%1$-4.4s %2$-17s";
		String formatPriceNormal= "%1$-4.4s %2$46s";
		String formater			= "%1$-4.4s";
		String price 			= "%1$-4.4s %2$-41.41s %3$38.38s\r\n";

//		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","RETURN ORDER",""), 0x11, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Tax Credit Note",""), 0x11, true);
        if(isFromOS)
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Duplicate Invoice",""), 0, true);

//		printHeader();

		if(cityCode.equalsIgnoreCase("SA"))
			printHeaderForSAR();
		else if(cityCode.equalsIgnoreCase("AE"))
			printHeader();

		if(strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");

//		woosim.saveSpool(EUC_KR, String.format(INV,"","GRV No: "+strOrderId), 0x09, true);
		if(TRN.equalsIgnoreCase(""))
		woosim.saveSpool(EUC_KR, String.format(INV,"","N/A"), 0x09, true);
		else
		woosim.saveSpool(EUC_KR, String.format(INV,"",""+TRN), 0x09, true);
		woosim.saveSpool(EUC_KR, String.format(INV,"","Tax Credit Note#: "+strOrderId), 0x09, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;

		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,mallsDetails.partyName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);

		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);

		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);

		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);

		if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);

		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);

		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);

		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","GRV No: "+strOrderId,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN#: N/A","Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		if (mallsDetails.VatNumber.equalsIgnoreCase("")) {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "TRN#: N/A","Return Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		} else{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "TRN#:" + mallsDetails.VatNumber,"Return Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		}

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

		if(!TextUtils.isEmpty(LPO))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Customer GRV No : "+LPO), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), ""), 0, true);
		}
		else {
			if(isFromOS)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Sub-Inventory : " + subInventory, "Customer GRV No : " + orderDO.LPOCode ), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), ""), 0, true);
		}


		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		String lines = "====================================================================================\r\n";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

//		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format01,"","SN#","ItemCode","Invoice","UOM","QTY","Original","Revised ","Difference","VAT","VAT"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format01,"","","Desc","Ref#","","","Amount","Amount","CN Amount","Rate","Amount"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

		totalPrice  = 0;
		totalDiscount  = 0;
		double totalTAX=0;
		double GrossAmnt=0.0;
		String detailAmount="";
		String detailDisc="";
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			ProductDO productDO = vecSalesOrderProducts.get(i);
			detailAmount=new CaptureInventryDA().getOriginalAmount(productDO.SKU,productDO.RefTrxCode);
			detailDisc=new CaptureInventryDA().getOriginalDisc(productDO.SKU,productDO.RefTrxCode);
			double CNAmt=StringUtils.getDouble(productDO.preUnits)*(productDO.itemPrice);
			totalPrice         += productDO.invoiceAmount;
			totalDiscount      += productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits);
//			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),productDO.SKU,productDO.Description,""+productDO.preUnits,productDO.UOM,""+deffAmt.format(productDO.itemPrice),productDO.DiscountAmt*StringUtils.getFloat(productDO.preUnits),""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.TaxPercentage)+"%",""+deffAmt.format(productDO.invoiceAmount)), 0, false);
			if(isFromOS) {
//				woosim.saveSpool(EUC_KR, String.format(format, "", "" + (i + 1), productDO.Description, "" + productDO.RefTrxCode, "" + deffAmt.format(StringUtils.getDouble(detailAmount)-StringUtils.getDouble(detailDisc)), "" + deffAmt.format(StringUtils.getDouble(detailAmount) - productDO.invoiceAmount), "" + deffAmt.format(productDO.invoiceAmount), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount)), 0, false);
//				woosim.saveSpool(EUC_KR, String.format(format, "", "" + (i + 1), productDO.SKU, "" + productDO.RefTrxCode, "" + deffAmt.format(StringUtils.getDouble(detailAmount)-productDO.TotalTax), "" + deffAmt.format((StringUtils.getDouble(detailAmount)-productDO.TotalTax)-CNAmt), "" + deffAmt.format(CNAmt), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount)), 0, false);
//				woosim.saveSpool(EUC_KR, String.format(format01, "", "" + (i + 1), productDO.SKU, "" + productDO.RefTrxCode,""+productDO.UOM,""+productDO.preUnits, "" + deffAmt.format(StringUtils.getDouble(detailAmount)), "" + deffAmt.format(CNAmt+productDO.LineTaxAmount), "" + deffAmt.format((StringUtils.getDouble(detailAmount)-CNAmt-productDO.LineTaxAmount)), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount)), 0, false);
				woosim.saveSpool(EUC_KR, String.format(format01, "", "" + (i + 1), productDO.SKU, "" + productDO.RefTrxCode,""+productDO.UOM,""+productDO.preUnits, "" + deffAmt.format(StringUtils.getDouble(detailAmount)), "" + deffAmt.format((StringUtils.getDouble(detailAmount)-CNAmt-productDO.LineTaxAmount)), "" + deffAmt.format(CNAmt+productDO.LineTaxAmount), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount)), 0, false);
				totalTAX+=productDO.LineTaxAmount;
			}
			else {
//				woosim.saveSpool(EUC_KR, String.format(format01, "", "" + (i + 1), productDO.SKU, "" + productDO.RefTrxCode,""+productDO.UOM,""+productDO.preUnits, "" + deffAmt.format(StringUtils.getDouble(detailAmount)), "" + deffAmt.format(CNAmt+productDO.OriginalLineTaxAmount), "" + deffAmt.format(StringUtils.getDouble(detailAmount)-(CNAmt+productDO.OriginalLineTaxAmount)), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.OriginalLineTaxAmount)), 0, false);
				woosim.saveSpool(EUC_KR, String.format(format01, "", "" + (i + 1), productDO.SKU, "" + productDO.RefTrxCode,""+productDO.UOM,""+productDO.preUnits, "" + deffAmt.format(StringUtils.getDouble(detailAmount)), "" + deffAmt.format(StringUtils.getDouble(detailAmount)-(CNAmt+productDO.OriginalLineTaxAmount)), "" + deffAmt.format(CNAmt+productDO.OriginalLineTaxAmount), "" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.OriginalLineTaxAmount)), 0, false);
				totalTAX+=productDO.OriginalLineTaxAmount;
			}

			woosim.saveSpool(EUC_KR, String.format(formatReason,"",""+productDO.Description,"Ex. Date: "+vecSalesOrderProducts.get(i).strExpiryDate,"Lot. No.: "+vecSalesOrderProducts.get(i).LotNumber), 0, true);
//			totalTAX+=productDO.LineTaxAmount;
			GrossAmnt+=CNAmt;
		}
		/*woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC%","AMOUNT"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

		totalPrice  = 0;
		totalDiscount = 0;

		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			totalPrice += vecSalesOrderProducts.get(i).invoiceAmount;
			totalDiscount   += vecSalesOrderProducts.get(i).discountAmount;
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).preUnits,vecSalesOrderProducts.get(i).UOM,""+deffAmt.format(vecSalesOrderProducts.get(i).itemPrice),vecSalesOrderProducts.get(i).Discount,""+deffAmt.format(vecSalesOrderProducts.get(i).invoiceAmount)), 0, false);

			woosim.saveSpool(EUC_KR, String.format(formatReason,"","Ex. Date: "+vecSalesOrderProducts.get(i).strExpiryDate,"Reason: "+vecSalesOrderProducts.get(i).reason, "Lot. No.: "+vecSalesOrderProducts.get(i).LotNumber), 0, true);
		}*/

		String line = "----------------------------------------------------------------------------------\r";

		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
//		woosim.saveSpool(EUC_KR, String.format(price,"","Total Gross Amount",deffAmt.format(totalPrice+totalDiscount+totalTAX)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Gross Amount",deffAmt.format(GrossAmnt)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Discount Amount",deffAmt.format(totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Tax Amount",deffAmt.format(totalTAX)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(GrossAmnt+totalTAX)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);

		woosim.saveSpool(EUC_KR, String.format(formatPriceBold,"","Amount in Words:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice)))+"\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		if(cityCode.equalsIgnoreCase("AE"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else if(cityCode.equalsIgnoreCase("SA"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Jumeirah Trading Co. Ltd.\r\n"), 0, true);
		try
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		if(isFromOS) {
                woosim.saveSpool(EUC_KR, String.format(LINE,"","Reason for Credit Note:"+orderDO.TrxReasonCode), 0, true);
            }else {
			if (vecSalesOrderProducts.size() > 0)
				woosim.saveSpool(EUC_KR, String.format(LINE, "", "Reason for Credit Note:" + TrxReasonCode), 0, true);
		}


		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	private void PrintReplacementOrder()
	{
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-15s %2$-4s %3$-25.25s %4$-25.25s %5$-5.5s %6$-5.5s \r\n";
		String formatNewDesc 	= "%1$-15s %2$-25.25s %3$-25.25s \r\n";
		String formater			= "%1$-10.10s";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","REPLACEMENT ORDER",""), 0x11, true);
		
		printHeader();
		
		if(strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Repl. No: "+strOrderId), 0x09, true);
		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,mallsDetails.partyName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
		
		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
		
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Repl. No: "+strOrderId,"Repl. Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Sales Person Mobile : "+ printMobileNumber(mallsDetails)), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
//		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","UOM","QTY"), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format,"", "SR#","RECEIPT ITEM CODE & DESC","ISSUE ITEM CODE & DESC","UOM","QTY"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).RelatedLineId,vecSalesOrderProducts.get(i).UOM,""+vecSalesOrderProducts.get(i).preUnits), 0, false);
			woosim.saveSpool(EUC_KR,String.format(formatNewDesc,"",vecSalesOrderProducts.get(i).Description,vecSalesOrderProducts.get(i).Description1), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		if(isPDUser)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receiverd By","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","1. Received complete invoiced quantity in good condition."), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","2. Official receipt is mandatory for payments."), 0, false);
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	
	private void PrintReplacementOrderSummary()
	{
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-15s %2$-4s %3$-25.25s %4$-25.25s %5$-5.5s %6$-5.5s \r\n";
		String formatNewDesc 	= "%1$-15s %2$-25.25s %3$-25.25s \r\n";
		String formater			= "%1$-10.10s";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","REPLACEMENT ORDER",""), 0x11, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"DUPLICATE COPY","",""), 0, true);
		
		printHeader();
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Repl. No: "+strOrderId), 0x09, true);
		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,mallsDetails.partyName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
		
		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Repl. No: "+strOrderId,"Repl. Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);

		
		totalPrice  = 0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
		{
			ProductDO productDO = vecSalesOrderProducts.get(i);
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),productDO.SKU,productDO.RelatedLineId,productDO.UOM,""+productDO.units), 0, false);
			woosim.saveSpool(EUC_KR,String.format(formatNewDesc,"",productDO.Description,productDO.Description1), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		if(isPDUser)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receiverd By","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","1. Received complete invoiced quantity in good condition."), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","2. Official receipt is mandatory for payments."), 0, false);
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	/* Method to print the generated sales order in 4 inch mode for PDUSer*/
	private void PrintMoveOrder()
	{
		String formatHeader 	= "%1$-19.19s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-15.15s %4$-40.40s %5$-7.7s %6$-7.7s\r\n";
		String formater			= "%1$-10.10s";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","MOVE ORDER",""), 0x11, true);
		
		printHeader();
		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,mallsDetails.partyName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
		
		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
		if(strOrderId == null || strOrderId.equalsIgnoreCase(""))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Move Order No: "+strOrderId,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","UOM","QTY"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
			woosim.saveSpool(EUC_KR,String.format(format,"", ""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,vecSalesOrderProducts.get(i).UOM,""+vecSalesOrderProducts.get(i).preUnits), 0, false);
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receiverd By","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","1. Received complete invoiced quantity in good condition."), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","2. Official receipt is mandatory for payments."), 0, false);
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	private void printOrderSummary()
	{
		if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
		{
			isFromOS=true;
			PrintReturnOrderNew();
		}
		else {
			byte[] init = {0x1b, '@'};
			woosim.controlCommand(init, init.length);
			totalPrice = 0.0f;

//		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
//		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
//		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
//		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
//		String format1 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-20.20s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$6.6s %10$8.8s \r\n";
//		String formatPriceBold  = "%1$-10.10s %2$-17s";
//		String formatPriceNormal= "%1$-10.10s %2$46s";
//		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
//		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
			String formatHeader = "%1$-16.16s %2$-41.41s  %3$-3.3s";
			String formatForAddress = "%1$-4.4s %2$-41.41s %3$-38.38s\r\n";
			String LINE = "%1$-4.4s %2$-78.78s\r\n";
			String format = "%1$-4.4s %2$-4s %3$-10.10s %4$-25.25s %5$5.5s %6$-4.4s %7$6.6s %8$6.6s %9$8.8s \r\n";
			String format1 = "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-6.6s %11$-7.7s \r\n";
//		String format2 			= "%1$-4.4s %2$-3.3s %3$-10.10s %4$-18.18s %5$-4.4s %6$-4.4s %7$-6.6s %8$-6.6s %9$-6.6s %10$-4.4s %11$-7.7s \r\n";
			String formatPriceBold = "%1$-4.4s %2$-17s";
			String formatPriceNormal = "%1$-4.4s %2$-50.50s";
			String formatPriceNorma0l = "%1$-17.17s %2$-40.40s \r\n";
			String price = "%1$-4.4s %2$-41.41s %3$38.38s\r\n";
			String formatReason = "%1$-4.4s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";

			if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
//			woosim.saveSpool(EUC_KR, String.format(formatHeader,"","RETURN ORDER",""), 0x11, true);
				woosim.saveSpool(EUC_KR, String.format(formatHeader, "", "Tax Credit Note", ""), 0x11, true);

			else {
				if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
//				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","CASH INVOICE",""), 0x11, true);
					woosim.saveSpool(EUC_KR, String.format(formatHeader, "", "TAX INVOICE", ""), 0x11, true);
				else
//				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","CREDIT INVOICE",""), 0x11, true);
					woosim.saveSpool(EUC_KR, String.format(formatHeader, "", "TAX INVOICE", ""), 0x11, true);
			}

			if (type == null)
				type = AppConstants.DUPLICATE_COPY;

			woosim.saveSpool(EUC_KR, String.format(formatHeader, "" + type.toUpperCase(), "", ""), 0, true);

//			printHeader();

			if(cityCode.equalsIgnoreCase("SA"))
				printHeaderForSAR();
			else if(cityCode.equalsIgnoreCase("AE"))
				printHeader();

			if(TRN.equalsIgnoreCase(""))
			woosim.saveSpool(EUC_KR, String.format(INV, "", "N/A"), 0x09, true);
			else
			woosim.saveSpool(EUC_KR, String.format(INV, "", ""+TRN), 0x09, true);
			if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				woosim.saveSpool(EUC_KR, String.format(INV, "", "Tax Credit Note#: " + orderDO.OrderId), 0x09, true);
			else
//			woosim.saveSpool(EUC_KR, String.format(INV,"","Invoice No: "+orderDO.OrderId), 0x09, true);
				woosim.saveSpool(EUC_KR, String.format(INV, "", "Tax Invoice No: " + orderDO.OrderId), 0x09, true);

			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "BILL TO:", "SHIP TO:"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.partyName, mallsDetails.siteName), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Customer No: " + mallsDetails.customerId, "Site No: " + mallsDetails.site), 0, false);

			if (mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.addresss1, mallsDetails.addresss1), 0, false);

			if (mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.addresss2, mallsDetails.addresss2), 0, false);

			if (mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.addresss3, mallsDetails.addresss3), 0, false);

			if (mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.addresss4, mallsDetails.addresss4), 0, false);

			if (mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.poNumber, mallsDetails.poNumber), 0, false);

			if (mallsDetails.city != null && mallsDetails.city.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.city, mallsDetails.city + "\r\n"), 0, false);

			String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";
			if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)) {
				if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName), 0, true);
				else
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Collected By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""), 0, true);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","GRV No: "+strOrderId,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
//				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "TRN#: N/A", "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM), 0, true);
				if (mallsDetails.VatNumber.equalsIgnoreCase(""))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "TRN#: N/A", "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM), 0, true);
				else
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "TRN#:"+mallsDetails.VatNumber, "Order Date: " + mydate.substring(0, mydate.lastIndexOf(":")) + AM_PM), 0, true);

				String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");

				if (!TextUtils.isEmpty(LPO)) {
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Sub-Inventory : " + subInventory, "Customer GRV No : " + LPO), 0, true);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Sales Person Mobile : " + printMobileNumber(mallsDetails), ""), 0, true);
				} else
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Sub-Inventory : " + subInventory, "Customer GRV No : : " + orderDO.LPOCode), 0, true);
			} else {


				if (mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "Salesman : " + mallsDetails.salesmanName), 0, true);
				else
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Delivered By: " + preference.getStringFromPreference(Preference.USER_NAME, ""), ""), 0, true);

				if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "GRV No: " + orderDO.OrderId, "GRV Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)), 0, true);
				else {
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Invoice No: "+orderDO.OrderId,"Invoice Date: "+CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)), 0, true);
					if(mallsDetails.VatNumber.equalsIgnoreCase(""))
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Customer TRN#:N/A", "Invoice Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)), 0, true);
					else
					woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Customer TRN#: " + mallsDetails.VatNumber, "Invoice Date: " + CalendarUtils.getFormatedDatefromStringWithTime(orderDO.InvoiceDate)), 0, true);
				}

				String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Sub-Inventory : " + subInventory, "Sales Person Mobile : " + printMobileNumber(mallsDetails)), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Delivery|Supply Date :"+CalendarUtils.getCurrentDate(), "LPO : " + orderDO.LPOCode), 0, true);
			}

			woosim.saveSpool(EUC_KR, "\r\n", 1, false);
			String lines = "==============================================================================================\r\n";
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);

//		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","AMOUNT"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(format1, "", "SR#", "ITEM CODE", "ITEM", "QTY", "UOM", "RATE", "DISC", "VAT", "VAT ", "AMOUNT"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(format1, "", "", "", "DESCRIPTION", "", "", "", "", "RATE", "AMOUNT", ""), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);

			totalPrice = 0;
			totalDiscount = 0;
			totalTaxAmt = 0;
			totNetAmt=0;
			for (int i = 0; i < vecSalesOrderProducts.size(); i++) {
				ProductDO productDO = vecSalesOrderProducts.get(i);
				totalDiscount += productDO.DiscountAmt;
				totalPrice += (productDO.invoiceAmount - productDO.LineTaxAmount);
				totNetAmt += (productDO.invoiceAmount);
				totalTaxAmt += productDO.LineTaxAmount;

//			woosim.saveSpool(EUC_KR,String.format(format1,"",""+(i+1),productDO.SKU,productDO.Description,""+productDO.units,productDO.UOM,""+deffAmt.format(productDO.itemPrice),""+deffAmt.format(productDO.DiscountAmt),/*productDO.DiscountAmt*/""+deffAmt.format(productDO.LineTaxAmount),""+deffAmt.format(productDO.invoiceAmount)), 0, false);
				woosim.saveSpool(EUC_KR, String.format(format1, "", "" + (i + 1), productDO.SKU, "", "" + productDO.units, productDO.UOM, "" + deffAmt.format(productDO.itemPrice), "" + deffAmt.format(productDO.DiscountAmt),/*productDO.DiscountAmt*/"" + deffAmt.format(productDO.TaxPercentage) + "%", "" + deffAmt.format(productDO.LineTaxAmount), "" + deffAmt.format(productDO.invoiceAmount)), 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatPriceNorma0l, "", ""+productDO.Description), 0, false);

				if (orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
					woosim.saveSpool(EUC_KR, String.format(formatReason, "", "Ex. Date: " + productDO.strExpiryDate, "Reason: " + productDO.reason, "Lot. No.: " + productDO.LotNumber), 0, true);
			}

			String line = "------------------------------------------------------------------------------------------\r";
			woosim.saveSpool(EUC_KR, "\r\n", 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);
			if (!(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))) {
				woosim.saveSpool(EUC_KR, String.format(price, "", "Total Gross Amount", deffAmt.format(totalPrice + totalDiscount) + " " + mallsDetails.currencyCode + "  \r"), 0, false);
				woosim.saveSpool(EUC_KR, String.format(price, "", "Total Discount Amount", deffAmt.format(totalDiscount) + " " + mallsDetails.currencyCode + "  \r"), 0, false);
			}
			woosim.saveSpool(EUC_KR, String.format(price, "", "Total VAT Amount", deffAmt.format(totalTaxAmt) + " " + mallsDetails.currencyCode + "  \r"), 0, false);
			if (!(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))) {
				woosim.saveSpool(EUC_KR, String.format(price, "", "Round Off Amount", deffAmt.format(roundOffVal) + " " + mallsDetails.currencyCode + "  \r"), 0, false);
			}
			woosim.saveSpool(EUC_KR, String.format(LINE, "", line), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(totalPrice + roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
			woosim.saveSpool(EUC_KR, String.format(price, "", "Total Amount", deffAmt.format(totNetAmt + roundOffVal) + " " + mallsDetails.currencyCode + "  \r"), 0x8, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", line), 0, true);

			woosim.saveSpool(EUC_KR, String.format(formatPriceBold, "", "Amount in Words:"), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice)))+"\r\n", 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatPriceNormal, "", new NumberToEnglish().changeCurrencyToWords("" + deffAmt.format(totNetAmt + roundOffVal))) + "\r\n", 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);

//			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Customer Signature", "Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);

			if(cityCode.equalsIgnoreCase("AE"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		else if(cityCode.equalsIgnoreCase("SA"))
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Jumeirah Trading Co. Ltd.\r\n"), 0, true);

			woosim.saveSpool(EUC_KR, "\r\n", 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);

			woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "REMARKS"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, true);

			if (!(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))) {
				woosim.saveSpool(EUC_KR, String.format(LINE, "", "1. Received complete invoiced quantity in good condition."), 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE, "", "2. Official receipt is mandatory for payments."), 0, false);

				if (mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
					woosim.saveSpool(EUC_KR, String.format(LINE, "", "3. Check should be issued in favor of �Galadari Ice Cream Co. Ltd. (L.L.C)�."), 0, false);
			}

			byte[] ff = {0x0c};
			woosim.controlCommand(ff, 1);
			byte[] lf = {0x0a};
			woosim.controlCommand(lf, lf.length);
			printFooter();
			woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
			woosim.printSpool(true);
			cardData = null;
			isPrinted = true;
		}

	}
	
	private void printLPOOrder(boolean isSummary)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-35.35s %5$-5.5s %6$8.8s \r\n";
		String formater			= "%1$-10.10s";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","DELIVERY NOTE",""), 0x11, true);
		
		printHeader();
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Invoice No: "+orderDO.OrderId), 0x09, true);
		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.partyName,mallsDetails.siteName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
		
		if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);

		if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
		
		if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
		
		if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
		
		if(mallsDetails.city != null && mallsDetails.city.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Invoice No: "+orderDO.OrderId,"Delivery Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
		
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		
		if(!TextUtils.isEmpty(orderDO.LPOCode))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "LPO No: "+orderDO.LPOCode), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), ""), 0, true);
		}else
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Sales Person Mobile : "+ printMobileNumber(mallsDetails)), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		String lines = "====================================================================================\r\n";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","UOM","QTY"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		if(!isSummary)
			for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
				woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).UOM,vecSalesOrderProducts.get(i).preUnits), 0, false);
		else
			for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
				woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).UOM,vecSalesOrderProducts.get(i).units), 0, false);
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
		
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","REMARKS"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","1. Received complete invoiced quantity in good condition."), 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","2. Official receipt is mandatory for payments."), 0, false);
		
		if(mallsDetails != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			woosim.saveSpool(EUC_KR, String.format(LINE,"","3. Check should be issued in favor of �Galadari Ice Cream Co. Ltd. (L.L.C)�."), 0, false);
	
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	//  Payment Receipt
	 
	public void printPaymentReceipt(String strType)
	{
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String formatInvoice 	= "%1$10.10s %2$15.15s %3$15.15s %4$15.15s %5$15.15s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String formater			= "%1$-10.10s";
		String totalAmt			= "%1$-10.10s %2$-38.38s %3$31.31s\r\n";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","PAYMENT RECEIPT",""), 0x11, true);
		
//		printHeader();


		if(cityCode.equalsIgnoreCase("SA"))
			printHeaderForSAR();
		else if(cityCode.equalsIgnoreCase("AE"))
			printHeader();


		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;
		
		if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo =   preference.getStringFromPreference(Preference.RECIEPT_NO, "");
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Receipt Number: "+strReceiptNo), 0x09, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Site Name: "+mallsDetails.partyName,"Customer Name: "+mallsDetails.siteName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Site No: "+mallsDetails.site,"Customer No: "+mallsDetails.customerId), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss1,"Receipt Number: "+strReceiptNo), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss2,"Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss3, "Date: "+mydate), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Salesman : "+mallsDetails.salesmanName,""), 0, true);
		
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, ""), 0, true);
		
		woosim.saveSpool(EUC_KR, "                                      "+strType.toUpperCase()+"   \r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		if(objPaymentDO!=null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0)
		{
			for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
			{
				if(paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE"))
				{
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type           : ",paymentDetailDO.PaymentTypeCode), 0, true);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque No              : ",paymentDetailDO.ChequeNo), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bank Name              : ",paymentDetailDO.BankName), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque Date            : ",CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                 : ",paymentDetailDO.Amount+" "+curencyCode+""), 0, false);
				}
				else
				{
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type           : ",paymentDetailDO.PaymentTypeCode), 0, true);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                 : ",paymentDetailDO.Amount+" "+curencyCode+""), 0, false);
					woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				}
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
		}
		
		float ftBalDue = 0f ;
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
	
		woosim.saveSpool(EUC_KR, String.format(formatInvoice,"","Invoice Number","Total Amount","Paid Amount", "Balance Amount"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		if(objPaymentDO != null)
		for( int i = 0 ; i < objPaymentDO.vecPaymentInvoices.size() ; i++ )
		{
			PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
			
			float ftTotal   = 	StringUtils.getFloat(paymentInvoiceDO.totalAmt);
			float ftPaid    = 	StringUtils.getFloat(paymentInvoiceDO.Amount);
			float balance   =   ftTotal - ftPaid;
			ftBalDue 	   +=   ftPaid;
			
			woosim.saveSpool(EUC_KR,String.format(formatInvoice, "", paymentInvoiceDO.TrxCode ,deffAmt.format(ftTotal), deffAmt.format(ftPaid),deffAmt.format(balance)), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(totalAmt, "", "Total ", deffAmt.format(ftBalDue)+" "+mallsDetails.currencyCode+""+" \r\n"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","Customer Signature"), 0, true);
		
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	
//  Payment Receipt
	public void printPaymentReceiptSummary()
	{
		String strType          = "Receipt Detail";
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String formatInvoice 	= "%1$10.10s %2$15.15s %3$15.15s %4$15.15s %5$15.15s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String formater			= "%1$-10.10s";
		String totalAmt			= "%1$-10.10s %2$-38.38s %3$31.31s\r\n";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","PAYMENT RECEIPT",""), 0x11, true);
		
		printHeader();
		
		if(TextUtils.isEmpty(customer_InvoiceDO.payment_Id))
			strReceiptNo = customer_InvoiceDO.payment_Id;
		else
			strReceiptNo = customer_InvoiceDO.receiptNo;
		
		if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
		{
			if(customer_InvoiceDO.vecPaymentDetailDOs != null && customer_InvoiceDO.vecPaymentDetailDOs.size() > 0 && customer_InvoiceDO.vecPaymentDetailDOs.get(0) != null )
				strReceiptNo =   new PaymentDetailDA().getReceiptNo(customer_InvoiceDO.vecPaymentDetailDOs.get(0).invoiceNumber);
		}
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Receipt Number: "+strReceiptNo), 0x09, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Site Name: "+mallsDetails.partyName,"Customer Name: "+mallsDetails.siteName), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Site No: "+mallsDetails.site,"Customer No: "+mallsDetails.customerId), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss1,"Receipt Number: "+strReceiptNo), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss2,"Collected By: "+preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", mallsDetails.addresss3, "Date: "+CalendarUtils.getFormatedDatefromStringWithTime(customer_InvoiceDO.reciptDate)), 0, false);
		
		if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Salesman : "+mallsDetails.salesmanName,""), 0, true);
		
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, ""), 0, true);
		
		woosim.saveSpool(EUC_KR, "                                      "+strType.toUpperCase()+"   \r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		if(customer_InvoiceDO.reciptType.equalsIgnoreCase("CHEQUE"))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type           : ",customer_InvoiceDO.reciptType), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque No              : ",customer_InvoiceDO.chequeNo), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bank Name              : ",customer_InvoiceDO.bankName), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque Date            : ",CalendarUtils.getFormatedDatefromStringPrint(customer_InvoiceDO.chequeDate)), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                 : ",customer_InvoiceDO.totalVal+" "+curencyCode+""), 0, false);
		}
		else
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type           : ",customer_InvoiceDO.reciptType), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                 : ",customer_InvoiceDO.totalVal+" "+curencyCode+""), 0, false);
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		float ftBalDue = 0f ;
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
	
		woosim.saveSpool(EUC_KR, String.format(formatInvoice,"","Invoice Number","Total Amount","Paid Amount", "Balance Amount"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		if(customer_InvoiceDO.vecPaymentDetailDOs != null)
		for( int i = 0 ; i < customer_InvoiceDO.vecPaymentDetailDOs.size() ; i++ )
		{
			PaymentDetailDO paymentDetailDO = customer_InvoiceDO.vecPaymentDetailDOs.get(i);
			float ftTotal   = 	StringUtils.getFloat(paymentDetailDO.invoiceAmount);
			float ftPaid    = 	StringUtils.getFloat(paymentDetailDO.invoiceAmount);
			float balance   =   ftTotal - ftPaid;
			ftBalDue 	   +=   ftPaid;
			
			woosim.saveSpool(EUC_KR,String.format(formatInvoice, "", paymentDetailDO.invoiceNumber ,deffAmt.format(ftTotal), deffAmt.format(ftPaid),deffAmt.format(balance)), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(totalAmt, "", "Total ", deffAmt.format(ftBalDue)+" "+mallsDetails.currencyCode+""+" \r\n"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","Customer Signature"), 0, true);
		
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	public void printPaymentInVoiceReceipt(String strType)
	{
		String formatHeader 	= "%1$-16.16s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String formatInvoice 	= "%1$10.10s %2$15.15s %3$15.15s %4$15.15s %5$15.15s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String formater			= "%1$-10.10s";
		String totalAmt			= "%1$-10.10s %2$-38.38s %3$31.31s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$-5.5s %6$-4.4s %7$-6.6s %8$-6.6s %9$-8.8s \r\n";
		String formatPriceBold  = "%1$-10.10s %2$-17s";
		String price 			= "%1$-10.10s %2$-38.38s %3$38.38s\r\n";
		String formatPriceNormal= "%1$-10.10s %2$46s";
		String lines 			= "====================================================================================\r\n";
		
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","CASH INVOICE",""), 0x11, true);
		
		if(!TextUtils.isEmpty(type)) 
			woosim.saveSpool(EUC_KR, String.format(formatHeader,""+type.toUpperCase(),"",""), 0, true);
		
		printHeader();
	
		if(TextUtils.isEmpty(strOrderId))
			strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Invoice No: "+strOrderId), 0x09, true);
				
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		
		if(vecSalesOrderProducts != null && vecSalesOrderProducts.size() > 0)
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","BILL TO:","SHIP TO:"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.partyName,mallsDetails.siteName), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer No: "+mallsDetails.customerId,"Site No: "+mallsDetails.site), 0, false);
			
			if(mallsDetails.addresss1 != null && mallsDetails.addresss1.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1), 0, false);
			
			if(mallsDetails.addresss2 != null && mallsDetails.addresss2.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss2,mallsDetails.addresss2), 0, false);
			
			if(mallsDetails.addresss3 != null && mallsDetails.addresss3.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss3,mallsDetails.addresss3), 0, false);
			
			if(mallsDetails.addresss4 != null && mallsDetails.addresss4.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss4,mallsDetails.addresss4), 0, false);
			
			if(mallsDetails.poNumber != null && mallsDetails.poNumber.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.poNumber,mallsDetails.poNumber), 0, false);
			
			if(mallsDetails.city != null && mallsDetails.city.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.city,mallsDetails.city+"\r\n"), 0, false);
			
			if(mallsDetails.salesmanName != null && mallsDetails.salesmanName.length() > 0)
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Salesman : "+mallsDetails.salesmanName), 0, true);
			else
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Delivered By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""), 0, true);
			
			if(strOrderId == null || strOrderId.equalsIgnoreCase(""))
				strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
			
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Invoice No: "+strOrderId,"Order Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM), 0, true);
			
			String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
			
			if(!TextUtils.isEmpty(LPO))
			{
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "LPO : "+LPO), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sales Person Mobile : "+ printMobileNumber(mallsDetails), ""), 0, true);
			}else
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, "Sales Person Mobile : "+ printMobileNumber(mallsDetails)), 0, true);
			
			woosim.saveSpool(EUC_KR, "\r\n", 1, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			
			woosim.saveSpool(EUC_KR, "\r\n", 1, false);
			woosim.saveSpool(EUC_KR, "                                      ORDER DETAILS\r\n", 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			
		  
			woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","DESCRIPTION","QTY","UOM","RATE","DISC","AMOUNT"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			
			totalPrice    = 0;
			totalDiscount = 0;
			if(!TextUtils.isEmpty(type) && (type.trim().equalsIgnoreCase(AppConstants.DUPLICATE_COPY)||type.trim().equalsIgnoreCase(AppConstants.CUSTOMER_COPY))){
				
				for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
				{
					totalPrice 		+= vecSalesOrderProducts.get(i).invoiceAmount;
					totalDiscount   += vecSalesOrderProducts.get(i).discountAmount;
					woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).preUnits,vecSalesOrderProducts.get(i).UOM,""+deffAmt.format(vecSalesOrderProducts.get(i).itemPrice),deffAmt.format(vecSalesOrderProducts.get(i).discountAmount),""+deffAmt.format(vecSalesOrderProducts.get(i).invoiceAmount)), 0, false);
				}
			}else{
				for( int i = 0 ; i < vecSalesOrderProducts.size() ; i++ )
				{
					totalPrice 		+= vecSalesOrderProducts.get(i).invoiceAmount;
					totalDiscount   += vecSalesOrderProducts.get(i).DiscountAmt*StringUtils.getFloat(vecSalesOrderProducts.get(i).preUnits);
					woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecSalesOrderProducts.get(i).SKU,vecSalesOrderProducts.get(i).Description,""+vecSalesOrderProducts.get(i).preUnits,vecSalesOrderProducts.get(i).UOM,""+deffAmt.format(vecSalesOrderProducts.get(i).itemPrice),deffAmt.format(vecSalesOrderProducts.get(i).DiscountAmt*StringUtils.getFloat(vecSalesOrderProducts.get(i).preUnits)),""+deffAmt.format(vecSalesOrderProducts.get(i).invoiceAmount)), 0, false);
				}
			}
			
			woosim.saveSpool(EUC_KR, "\r\n", 0, true);
			woosim.saveSpool(EUC_KR, String.format(price,"","Total Gross Amount",deffAmt.format(totalPrice+totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(price,"","Total Discount Amount",deffAmt.format(totalDiscount)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(price,"","Round Off Amount",deffAmt.format(roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0, false);
			
			String line = "----------------------------------------------------------------------------------\r";
			woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
			woosim.saveSpool(EUC_KR, String.format(price,"","Total Amount",deffAmt.format(totalPrice + roundOffVal)+" "+mallsDetails.currencyCode+"  \r"), 0x8, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",line), 0, true);
			
			woosim.saveSpool(EUC_KR, String.format(formatPriceBold,"","Amount in Words:"), 0, true);
			
			
			woosim.saveSpool(EUC_KR, String.format(formatPriceNormal,"",new NumberToEnglish().changeCurrencyToWords(""+deffAmt.format(totalPrice + roundOffVal)))+"\r\n", 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Customer Signature","Galadari Ice Cream Co. Ltd. (L.L.C)\r\n"), 0, true);
			try 
			{
				woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
				String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
				woosim.printBitmap(sign1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try 
			{
				woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
				String sign1 = preference.getStringFromPreference(AppConstants.SALESMAN_SIGN, "");
				woosim.printBitmap(sign1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			woosim.saveSpool(EUC_KR, "\r\n", 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		}
		woosim.saveSpool(EUC_KR, "                                      "+strType.toUpperCase()+"   \r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo = objPaymentDO.ReceiptId;
		
		if(strReceiptNo == null || strReceiptNo.equalsIgnoreCase(""))
			strReceiptNo =   preference.getStringFromPreference(Preference.RECIEPT_NO, "");
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"", "Receipt Number: "+strReceiptNo , "Date : "+mydate), 0, false);
		
		if(objPaymentDO!=null && objPaymentDO.vecPaymentDetails != null && objPaymentDO.vecPaymentDetails.size() > 0)
		{
			for(PaymentDetailDO paymentDetailDO : objPaymentDO.vecPaymentDetails)
			{
				if(paymentDetailDO.PaymentTypeCode.equalsIgnoreCase("CHEQUE"))
				{
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type            : ",paymentDetailDO.PaymentTypeCode), 0, true);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque No               : ",paymentDetailDO.ChequeNo), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bank Name               : ",paymentDetailDO.BankName), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cheque Date             : ",CalendarUtils.getFormatedDatefromStringPrint(paymentDetailDO.ChequeDate)), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                  : ",paymentDetailDO.Amount+" "+curencyCode+""), 0, false);
				}
				else
				{
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Payment Type            : ",paymentDetailDO.PaymentTypeCode), 0, true);
					woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Amount                  : ",paymentDetailDO.Amount), 0, false);
					woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				}
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
		}
		
		float ftBalDue = 0f ;
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
	
		woosim.saveSpool(EUC_KR, String.format(formatInvoice,"","Invoice Number","Total Amount","Paid Amount", "Balance Amount"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		if(objPaymentDO.vecPaymentInvoices != null)
		for( int i = 0 ; i < objPaymentDO.vecPaymentInvoices.size() ; i++ )
		{
			PaymentInvoiceDO paymentInvoiceDO = objPaymentDO.vecPaymentInvoices.get(i);
			float ftTotal   = 	StringUtils.getFloat(paymentInvoiceDO.totalAmt);
			float ftPaid    = 	StringUtils.getFloat(paymentInvoiceDO.Amount);
			float balance   =   ftTotal - ftPaid;
			ftBalDue 	   +=   ftPaid;
			
			woosim.saveSpool(EUC_KR,String.format(formatInvoice, "", paymentInvoiceDO.TrxCode ,deffAmt.format(ftTotal), deffAmt.format(ftPaid),deffAmt.format(balance)), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(totalAmt, "", "Total ", deffAmt.format(ftBalDue)+" "+mallsDetails.currencyCode+""+" \r\n"), 0x8, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","Customer Signature"), 0, true);
		
		try 
		{
			woosim.saveSpool(EUC_KR, String.format(formater, ""), 0, false);
			String sign1 = preference.getStringFromPreference(AppConstants.CUSTOMER_SIGN, "");
			woosim.printBitmap(sign1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	public void onClick(View v)
	{
		if (v.getId() == R.id.btn_open)
		{
			int reVal = woosim.BTConnection(address, cheProtocol.isChecked());
			if (reVal == 1)
			{
				Toast t = Toast.makeText(this, "SUCCESS CONNECTION!", Toast.LENGTH_SHORT);
//				if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//				{
//					btnFinishPrint.setVisibility(View.GONE);
//					btnReprint.setVisibility(View.GONE);
//					btnMSR23.performClick();
//				}
//				else
//				{
					btnFinishPrint.setVisibility(View.VISIBLE);
					btnReprint.setVisibility(View.VISIBLE);
					btnPrint3.performClick();
//				}
				t.show();
			} 
			else if (reVal == -2) 
			{
				Toast t = Toast.makeText(this, "NOT CONNECTED",		Toast.LENGTH_SHORT);
				t.show();
				//showCustomDialog(WoosimPrinterActivity.this, "Sorry !", "Printer not connected, search for printer?", " Search Printer ", "Finish", "printer", false);
				showCustomDialog(WoosimPrinterActivity.this, "Sorry !", "Printer not connected, search for printer?", " Search Printer ", "Finish", "", false);
			} 
			else if (reVal == -5)
			{
				Toast t = Toast.makeText(this, "DEVICE IS NOT BONDED",	Toast.LENGTH_SHORT);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry !", "Printer not paired. Please pair and try again.", "OK", "", "", false);
			} 
			else if (reVal == -6) 
			{
				Toast t = Toast.makeText(this, "ALREADY CONNECTED",	Toast.LENGTH_SHORT);
				t.show();
//				if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//				{
//					btnMSR23.performClick();
//				}
//				else
//				{
					btnPrint3.performClick();
//				}
			} 
			else if (reVal == -8)
			{
				Toast t = Toast	.makeText(this,"Please enable your Bluetooth and re-run this program!",	Toast.LENGTH_LONG);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry !", "Printer not connected, search for printer?", " Search Printer ", "Finish", "printer", false);
			} 
			else 
			{
				Toast t = Toast.makeText(this, "ELSE", Toast.LENGTH_SHORT);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry !", "Printer not connected, search for printer?", " Search Printer ", "Finish", "printer", false);
			}

		}
		if (v.getId() == R.id.btn_close)
		{
			closeForm();
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

		}
		if (v.getId() == R.id.btn_print2inch) 
		{
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

		}
		if (v.getId() == R.id.btn_print3inch) 
		{
			if(CALLFROM == CONSTANTOBJ.PRINT_SALES)
			{
				if(isPDUser)
					PrintMoveOrder();
				else
					PrintSalesOrder();
			}
			
			else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE)
				printLPOOrder(false);
					
			else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY)
				printLPOOrder(true);
			
			else if(CALLFROM == CONSTANTOBJ.ORDER_SUMMARY)
			{
				if(objPaymentDO == null)
					printOrderSummary();
				else
//					printPaymentInVoiceReceipt("Receipt Detail");
					PrintSalesOrder();
			}
			
			else if(CALLFROM == CONSTANTOBJ.PAYMENT_RECEIPT)
				printPaymentReceipt("Receipt Detail");
			
			else if(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT)
//				printPaymentInVoiceReceipt("Receipt Detail");
				PrintSalesOrder();
			
			else if(CALLFROM == CONSTANTOBJ.PAYMENT_SUMMARY)
			{
				if(type != null && type.equalsIgnoreCase(AppConstants.ACCOUNT_COPY))
					printARPaymentSummary();
				else
					printCollectionPaymentSummary();
			}
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_EOT_SUMMARY)
				printEOTSummary();
			
			else if(CALLFROM == CONSTANTOBJ.NEW_CUSTOMER)
				printNewCustomerDetail(type);
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE)
				PrintLoad();
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_RETURN_INVENTORY)
				PrintReturnLoad(from);
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_INVENTORY)
				PrintInventory(from);
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_LOAD_REQUEST)
				PrintLoadInventory();
		
			else if(CALLFROM == CONSTANTOBJ.PRINT_VERIFY_STOCK_UNLOAD)
				Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_RETURN)
				//PrintReturnOrder();
				PrintReturnOrderNew();

			else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_REPLACE)
				PrintReplacementOrder();
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_SALES_REPLACE_SUMMARY)
				PrintReplacementOrderSummary();
			
			else if(CALLFROM == CONSTANTOBJ.PAYMENT_SEP_SUMMARY)
				printPaymentReceiptSummary();
			
			else if(CALLFROM == CONSTANTOBJ.TRANSFER_IN)
				Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
			
			else if(CALLFROM == CONSTANTOBJ.TRANSFER_OUT)
				Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
			
			else if(CALLFROM == CONSTANTOBJ.PRINT_FREEDELIVERY)
				Toast.makeText(getApplicationContext(), "Not Implemented", Toast.LENGTH_SHORT).show();
			
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");
		}
		if (v.getId() == R.id.btn_msr23)
		{
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			byte[] track23 = { 0x1b, 0x4d, 0x45 };
			woosim.controlCommand(track23, track23.length);
			woosim.printSpool(true);
			showLoader("Please Swipe Your Card");
		}

		
		if (v.getId() == R.id.btn_msr123) 
		{
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			byte[] track123 = { 0x1b, 0x4d, 0x46 };
			woosim.controlCommand(track123, track123.length);
			woosim.printSpool(true);
		}

		if (v.getId() == R.id.btn_cardcancel)
		{

			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			woosim.cardCancel();

		}
		if (v.getId() == R.id.btn_finish)
		{
			try
			{
				if(isPrinted)
					setResult(20000);
				else
					setResult(Activity.RESULT_OK);
				finish();
				
				if(woosim != null)
					woosim.BTDisConnection();
			} 
			catch (Exception e)
			{
			}
			
			hideLoader();
		}
		
		if(v.getId() == R.id.btn_printimg2)
		{
		}
		if(v.getId() == R.id.btnReprint)
		{
			btnPrint3.performClick();
		}
		if(v.getId() == R.id.btnFinishPrint)
		{
			btnFinish.performClick();
		}
	}

	public void printNewCustomerDetail(String strType)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String regData = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();
		regData = dateFormat.format(dt);
		String formatHeader = "";
		if(strType.equalsIgnoreCase("Customer Detail"))
			formatHeader = "%1$-8.8s %2$-25.25s  %3$-8.8s";
		else
			formatHeader = "%1$-1.1s %2$-40.40s  %3$-1.1s";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"",type.toUpperCase(),""), 0x11, true);
		
		printHeader();

//		woosim.saveSpool(EUC_KR, "                "+type.toUpperCase()+" \r\n", 0, false);
		woosim.saveSpool(EUC_KR, "-------------------------------------------------------------------\r\n", 0, true);
		woosim.saveSpool(EUC_KR, "-------------------------------------------------------------------\r\n", 0, true);
	  
		String format = "%1$-25s%2$-2.2s%3$-37.37s \r\n";
		woosim.saveSpool(EUC_KR,String.format(format,"Customer Name","",mallsDetails.siteName), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Customer Number","",mallsDetails.customerId), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Site ID","",mallsDetails.site), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"addresss1","",mallsDetails.addresss1), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"addresss2","",mallsDetails.addresss2), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"City","",mallsDetails.city), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Geo Code X","",mallsDetails.geoCodeX), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Geo Code Y","",mallsDetails.geoCodeY), 0, false);
		
		if(mallsDetails.addresss1.contains(","))
		{
			String str[] = mallsDetails.addresss1.split(",");
			woosim.saveSpool(EUC_KR,String.format(format,"Building","",str[0].trim()), 0, false);
			woosim.saveSpool(EUC_KR,String.format(format,"Villa Name","",str[1].trim()), 0, false);
		}
		else
		{
			woosim.saveSpool(EUC_KR,String.format(format,"Building","",mallsDetails.addresss1), 0, false);
			woosim.saveSpool(EUC_KR,String.format(format,"Villa Name","",mallsDetails.addresss1), 0, false);
		}
		woosim.saveSpool(EUC_KR,String.format(format,"Customer Type","",mallsDetails.paymentType), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Payment Type","",mallsDetails.paymentTermCode), 0, false);
		woosim.saveSpool(EUC_KR,String.format(format,"Email Id","",mallsDetails.email), 0, false);
		
		//code for the Mandatory documents
		woosim.saveSpool(EUC_KR, "\r\n==============================================================\r\n", 0, true);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	private void closeForm() 
	{
		try 
		{
			woosim.BTDisConnection();
			Toast t = Toast.makeText(this, "CLOSE", Toast.LENGTH_SHORT);
			t.show();
		}
		catch (Exception e) 
		{
		}
	}

	protected void onDestroy()
	{
		super.onDestroy();
		if (mBtAdapter != null)
		{
			mBtAdapter.cancelDiscovery();
		}
		this.unregisterReceiver(mReceiver);
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() 
	{
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
		{
			mBtAdapter.cancelDiscovery();
			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			address 	= info.substring(info.length() - 17);
			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
			System.out.println("address" + address);
			setContentView(R.layout.woosim);
			createButton();
			btnOpen.performClick();
			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
		}
	};

	private void createButton()
	{
		btnOpen 		= (Button) findViewById(R.id.btn_open);
		
		btnClose 		= (Button) findViewById(R.id.btn_close);
		btnPrint2 		= (Button) findViewById(R.id.btn_print2inch);
		btnPrint3 		= (Button) findViewById(R.id.btn_print3inch);
		btnMSR23 		= (Button) findViewById(R.id.btn_msr23);
		btnMSR123 		= (Button) findViewById(R.id.btn_msr123);
		btnCardCancel 	= (Button) findViewById(R.id.btn_cardcancel);
		btnFinish 		= (Button) findViewById(R.id.btn_finish);
		btnPrintImg1 	= (Button) findViewById(R.id.btn_printimg1);
		btnPrintImg2 	= (Button) findViewById(R.id.btn_printimg2);
		cheProtocol 	= (CheckBox) findViewById(R.id.che_protocol);
		editTrack1 		= (EditText) findViewById(R.id.edit1);
		editTrack2 		= (EditText) findViewById(R.id.edit2);
		editTrack3 		= (EditText) findViewById(R.id.edit3);
		btnReprint 		= (Button) findViewById(R.id.btnReprint);
		btnFinishPrint 	= (Button) findViewById(R.id.btnFinishPrint);
		tvPrintHeader	= (TextView) findViewById(R.id.tvPrintHeader);
		
		btnClose.setOnClickListener(this);
		btnOpen.setOnClickListener(this);
		btnPrint2.setOnClickListener(this);
		btnPrint3.setOnClickListener(this);
		btnMSR23.setOnClickListener(this);
		btnMSR123.setOnClickListener(this);
		btnCardCancel.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		btnPrintImg1.setOnClickListener(this);
		btnPrintImg2.setOnClickListener(this);
		cheProtocol.setOnClickListener(this);
		editTrack1.setOnClickListener(this);
		editTrack2.setOnClickListener(this);
		editTrack3.setOnClickListener(this);
		btnReprint.setOnClickListener(this);
		btnFinishPrint.setOnClickListener(this);
		
		
		btnReprint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinishPrint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvPrintHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) 
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// When discovery is finished, change the Activity title
			} 
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED	.equals(action))
			{
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) 
				{
					String noDevices = getResources().getText(R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	// Start device discover with the BluetoothAdapter
	 
	private void doDiscovery() 
	{
		// Indicate scanning in the title
		if(mBtAdapter!=null)
		{
			if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF)
			{
				mBtAdapter.enable();
				showLoader("Please wait Turning on your bluetooth");
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						showDeviceLists(true);
					}
				},10000);
			}
			setProgressBarIndeterminateVisibility(true);
			setTitle(R.string.scanning);
			// Turn on sub-title for new devices
			findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
			// If we're already discovering, stop it
			if (mBtAdapter.isDiscovering())
			{
				mBtAdapter.cancelDiscovery();
			}
			// Request discover from BluetoothAdapter
			mBtAdapter.startDiscovery();
		}
	}

	
	public boolean onKeyDown(int keyConde, KeyEvent event) 
	{
		if ((keyConde == KeyEvent.KEYCODE_BACK)) 
		{
			try 
			{
				woosim.BTDisConnection();
			} 
			catch (Exception e)
			{
			}
			/*if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
			{
				setResult(-10000);
				finish();
			}
			else*/ if(CALLFROM ==CONSTANTOBJ.PAYMENT_RECEIPT)
			{
				setResult(1000);
				finish();
			}
			else
			{
				setResult(10000);
				finish();
			}
		}
		return false;
	}
	
	private void printAsset()
	{
		int imgSize_1, imgSize_2, imgSize_3, imgSize_4, imgSize;

		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		try 
		{
			AssetManager am = getApplicationContext().getAssets();
			InputStream is = am.open("masafilogo.bmp" ,	AssetManager.ACCESS_BUFFER);
			int t = is.available();
			byte[] bmpfile = new byte[t];
			for(int i = 0; i < t; i++)
				bmpfile[i] = (byte)is.read();
			
			imgSize_1 = bmpfile[5];
			imgSize_2 = bmpfile[4];
			imgSize_3 = bmpfile[3];
			imgSize_4 = bmpfile[2];
			if (imgSize_1 < 0)
				imgSize_1 += 256;
			else if (imgSize_2 < 0)
				imgSize_2 += 256;
			else if (imgSize_3 < 0)
				imgSize_3 += 256;
			else if (imgSize_4 < 0)
				imgSize_4 += 256;

			imgSize_1 = imgSize_1 << 24;
			imgSize_2 = imgSize_2 << 16;
			imgSize_3 = imgSize_3 << 8;
			imgSize = imgSize_1 | imgSize_2 | imgSize_3 | imgSize_4;

			woosim.printBitmap(bmpfile, imgSize);

		} 
		catch (IOException e) 
		{
			LogUtils.errorLog("PRINTBITMAP", "Error while printing bitmap");
			e.printStackTrace();
		}
	}
	
	public CustomDialog customDialog;
	//For showing Dialog message.
	class RunshowCustomDialogs implements Runnable
	{
		private String strTitle;//Title of the dialog 
		private String strMessage;// Message to be shown in dialog 
		private String firstBtnName;
		private String secondBtnName;
		private String from;
		private boolean isCancelable;
		
		public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable) 
		{
			this.strTitle   	= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			if(from != null)
				this.from			= from;
			else
				this.from= "";
			 this.isCancelable = isCancelable;
		}

		@Override
		public void run() 
		{
			
			View view = getLayoutInflater().inflate(R.layout.custom_common_popup, null);
			if(customDialog!=null && customDialog.isShowing())
				customDialog.dismiss();
			customDialog = new CustomDialog(WoosimPrinterActivity.this, view, new Preference(WoosimPrinterActivity.this).getIntFromPreference("DEVICE_DISPLAY_WIDTH",320) - 40, LayoutParams.WRAP_CONTENT, true);
			customDialog.setCancelable(isCancelable);
			TextView tvTitle 	  = (TextView)view.findViewById(R.id.tvTitlePopup);
			TextView tvMessage 	  = (TextView)view.findViewById(R.id.tvMessagePopup);
			Button btnYes 		  = (Button) view.findViewById(R.id.btnYesPopup);
			Button btnNo 		  = (Button) view.findViewById(R.id.btnNoPopup);
			
			tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			btnYes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			btnNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			tvTitle.setText(""+strTitle);
			tvMessage.setText(""+strMessage);
			btnYes.setText(""+firstBtnName);
			
			if(secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
				btnNo.setText(""+secondBtnName);
			else
				btnNo.setVisibility(View.GONE);
			
			btnYes.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					customDialog.dismiss();
					if(from.equalsIgnoreCase("cardswipe"))
					{
						showLoader("Please swipe card...");
//						btnMSR23.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipeAgain"))
					{
						btnFinishPrint.setVisibility(View.GONE);
						btnReprint.setVisibility(View.GONE);
						btnMSR23.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipesuccessfully"))
					{
						try
						{
							woosim.BTDisConnection();
						} 
						catch (Exception e) 
						{
						}
						
						Intent intent = new Intent();
						intent.putExtra("obj", objPaymentDO);
						setResult(10000,intent);
						finish();
					}
					else
					{
						showDeviceLists(false);
					}
				}
			});
			btnNo.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					customDialog.dismiss();
					if(from.equalsIgnoreCase("cardswipe"))
					{
						setResult(-10000);
						btnFinish.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipeAgain"))
					{
						woosim.BTDisConnection();
						setResult(-10000);
						finish();
					}
					/*else if(from.equalsIgnoreCase("printer"))
					{
						setResult(-20000);
						finish();
					}*/
					else
					{
						showDeviceLists(false);
					}
				}
			});
			if(!customDialog.isShowing())
				customDialog.show();
		}
	}
	
	/* Method to Show the alert dialog */
	public void showCustomDialog(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage, firstBtnName, secondBtnName, from, true));
	}
	/* Method to Show the alert dialog */
	public void showCustomDialog(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage, firstBtnName, secondBtnName, from, isCancelable));
	}
	
	// This is  to show the loading progress dialog when some other functionality is taking place.
	class RunShowLoader implements Runnable
	{
		private String strMsg;
		private String title;
		
		public RunShowLoader(String strMsg, String title) 
		{
			this.strMsg = strMsg;
			this.title = title;
		}
		
		@Override
		public void run() 
		{
			try
			{
				if(progressdialog == null ||(progressdialog != null && !progressdialog.isShowing()))
				{
					progressdialog = ProgressDialog.show(WoosimPrinterActivity.this, title, strMsg);
				}
			}
			catch(Exception e)
			{progressdialog = null;}
		}
	}
	/** For hiding progress dialog (Loader ). **/
	public void hideLoader()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					if(progressdialog != null && progressdialog.isShowing())
						progressdialog.dismiss();
					progressdialog = null;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	// This method is  to show the loading progress dialog when some other functionality is taking place. **//*
	public void showLoader(String msg)
	{
		runOnUiThread(new RunShowLoader(msg, ""));
	}
	
	
	private void printHeader()
	{
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		try 
		{
			woosim.saveSpool(EUC_KR, "     ", 1, false);
			woosim.printBitmap(AppConstants.baskinLogoPath+"/logo.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try 
		{
			woosim.saveSpool(EUC_KR, "       ", 1, false);
			
			String imgPath 		= 	Environment.getExternalStorageDirectory()+"/Baskin/address.bmp";
			Bitmap originalImg 	= 	BitmapFactory.decodeFile(imgPath);
			int value 			= 	woosim.printConvertedImage(2, originalImg, imgPath);
			
			if(value != 1)
				woosim.printBitmap(AppConstants.baskinLogoPath+"/address.bmp");
		}
		catch (Exception e)
		{
			try 
			{
				woosim.printBitmap(AppConstants.baskinLogoPath+"/address.bmp");
			} 
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		woosim.saveSpool(EUC_KR, "\r\n", 0x11, true);
	}
	private void printHeaderForSAR()
	{
		woosim.saveSpool(EUC_KR, "\r\n", 1, false);
		try
		{
			woosim.saveSpool(EUC_KR, "     ", 1, false);
			woosim.printBitmap(AppConstants.baskinLogoPath+"/logo.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		/*String LINE 			= "%1$-4.4s %2$-65.65s\r\n";
		String formatForAddress = "%1$-4.4s %2$-30.30s %3$-30.30s\r\n";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",""), 0, true);

		try
		{
			woosim.saveSpool(EUC_KR, "     ", 1, false);
			woosim.printBitmap("");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/

		try
		{
			woosim.saveSpool(EUC_KR, "\r\n", 1, false);

			String imgPath 		= 	Environment.getExternalStorageDirectory()+"/Baskin/SAR_Address.bmp";
			Bitmap originalImg 	= 	BitmapFactory.decodeFile(imgPath);
			int value 			= 	woosim.printConvertedImage(1, originalImg, imgPath);

			if(value != 1)
				woosim.printBitmap(AppConstants.baskinLogoPath+"/SAR_Address.bmp");
		}
		catch (Exception e)
		{
			try
			{
				woosim.printBitmap(AppConstants.baskinLogoPath+"/SAR_Address.bmp");
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Jumeirah Trading Co. Ltd.",""+ getApplicationContext().getResources().getString(R.string.adress_1)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","P.B.1401. Dammam-31431, K.S.A",""+getApplicationContext().getResources().getString(R.string.adress_2)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Tel: +966 3 8343410-Ext 2505,",""+getApplicationContext().getResources().getString(R.string.adress_3)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Fax 1: +966 3 8092563 (Direct),",""+getApplicationContext().getResources().getString(R.string.adress_4)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Fax 2: +966 3 8350019 (General),",""+getApplicationContext().getResources().getString(R.string.adress_5)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Mob:+966 565170378",""+getApplicationContext().getResources().getString(R.string.adress_6)), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","",""+getApplicationContext().getResources().getString(R.string.adress_7)), 0, true);



		woosim.saveSpool(EUC_KR, "\r\n", 0x11, true);
	}

	private void printFooter()
	{
		String facebook = preference.getStringFromPreference(AppConstants.BASKIN_FACEBOOK, "");
		String twitter  = preference.getStringFromPreference(AppConstants.BASKIN_TWITTER, "");
		
		String format1 = "%1$-10.10s %2$-50.50s\r\n";
		String format2 = "%1$-10.10s";
		woosim.saveSpool(EUC_KR, "                 ", 0, true);
		woosim.saveSpool(EUC_KR, String.format(format1, "", "Thank You\r\n"), 0, true);
		
		woosim.saveSpool(EUC_KR, "                  ", 0, true);
		try
		{
			woosim.saveSpool(EUC_KR, String.format(format2, ""), 0, true);
			woosim.printBitmap(AppConstants.baskinLogoPath+"/facebook.bmp");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, " - "+facebook+"   \r\n", 0, true);
		woosim.saveSpool(EUC_KR, "                  ", 0, true);
		try
		{
			woosim.saveSpool(EUC_KR, String.format(format2, ""), 0, true);
			woosim.printBitmap(AppConstants.baskinLogoPath+"/twitter.bmp");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, " - "+twitter+"    \r\n", 0, true);
		woosim.saveSpool(EUC_KR, "\r\n\r\n", 0, false);
	}
	
	
	  /*Payment Receipt*/
	
	public void printARPaymentSummary()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		
		String formatHeader 	= "%1$-16.16s %2$-41.41s %3$-3.3s";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-25.25s %3$-10.10s %4$-14.14s %5$-10.10s %6$12.12s\r\n";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String lines 			= "------------------------------------------------------------------------------------\r\n";
		String singleLine       = "------------------------------------------------------------------------------------";
		String dividerLine      = "|                                    |";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","RECEIPT SUMMARY",""), 0x11, true);
		
		printHeader();
		
		String mydate 		= 	java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","User Name:"+preference.getStringFromPreference(Preference.USER_NAME, ""),"Emp No.: "+preference.getStringFromPreference(Preference.EMP_NO, "")), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected Date: "+(strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate),"Print Date: "+mydate), 0, false);
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);	  

		boolean isCash = false, isCheque = false;
		if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
		{
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}
		
		String currencyCode = "";
		if(isCheque)
		{
			woosim.saveSpool(EUC_KR,String.format(format,"","Site Name","Site No.","Receipt No.","Type","Amount"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			Float strTotalAmount = 0f;
			if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
			{
				for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
				{
					if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					{
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)), 0, false);
						
						if(currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}
				
				if(strTotalAmount > 0)
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
					woosim.saveSpool(EUC_KR, String.format(format,"","Cheque Total: ","","",currencyCode,deffAmt.format(strTotalAmount)), 0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				}
			}
		}
		if(isCash)
		{
			woosim.saveSpool(EUC_KR,String.format(format,"","Site Name","Site No.","Receipt No.","Type","Amount"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			Float strTotalCashAmount = 0f;
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
				{
					float amount 		= StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)), 0, false);
					
					if(currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if(strTotalCashAmount > 0)
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				woosim.saveSpool(EUC_KR, String.format(format,"","Cash Total: ","","",currencyCode,deffAmt.format(strTotalCashAmount)), 0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","AR Department","Salesman"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
		
	}
	
	public void printEOTSummary()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		
		String formatStock 		= "%1$-10.10s %2$-4s %3$-10.10s %4$-40.40s %5$-10.10s %6$-10.10s\r\n";
		String formatInvoice	= "%1$-10.10s %2$-3.3s %3$-15.15s %4$-43.43s %5$10.10s\r\n";
		String formatHeader 	= "%1$-16.16s %2$-41.47s %3$-3.3s";
		String formatHeaderN 	= "%1$-1.1s %2$-47.41s %3$-3.3s";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-25.25s %3$-10.10s %4$-14.14s %5$-10.10s %6$12.12s\r\n";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String formatNonSeleble	= "%1$-10.10s %2$-4s %3$-10.10s %4$-40.40s %5$-10.10s %6$-10.10s \r\n";
		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
		String formatReplacement= "%1$-10.10s %2$-4.4s %3$-11.11s %4$-20.20s %5$-20.20s %6$-5.5s %7$5.5s\r\n";
		String formatNewDesc 	= "%1$-10.10s %2$-15.15s %3$-25.25s %4$-25.25s\r\n";
		String lines 			= "------------------------------------------------------------------------------------\r\n";
		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","EOT SUMMARY",""), 0x11, true);
//		woosim.saveSpool(EUC_KR, String.format(formatHeaderN,"",""+mydate,""), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeaderN,"",""+CalendarUtils.getCurrentDate()+" "+CalendarUtils.getCurrentTime(),""), 0x09, true);
		//woosim.saveSpool(EUC_KR, String.format(formatHeader,""+CalendarUtils.getCurrentDate(),""+CalendarUtils.getCurrentTime(),""), 0x09, true);
		
//		printHeader();
		if(cityCode.equalsIgnoreCase("SA"))
			printHeaderForSAR();
		else if(cityCode.equalsIgnoreCase("AE"))
			printHeader();

		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","User Name:"+preference.getStringFromPreference(Preference.USER_NAME, ""),"Emp No.: "+preference.getStringFromPreference(Preference.EMP_NO, "")), 0, false);

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, ""), 0, true);
		
//		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
//		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);	

		String SiteNo=new OrderDA().getSiteForEOT(preference.getStringFromPreference(Preference.USER_ID, ""));
		String Curr=new OrderDA().getCurrencyForEOT(SiteNo);



		String currencyCode = "";
		if(eotSummaryPrintDO != null)
		{
			HashMap<String, Vector<OrderDO>> hmOrders = eotSummaryPrintDO.hmOrders;
			if(hmOrders != null && hmOrders.size() >0)
			{
				
				Set<String> keys = hmOrders.keySet();
				int count = 0;
				if(keys != null && keys.size() > 0)
				{
					
					for (String string : keys)
					{
						String invoiceType = "";
						String TITLE = "";
						
						if(string.equalsIgnoreCase(AppConstants.HHOrder))
						{
							invoiceType = "Sales Order";
							TITLE = "                    SALES ORDER SUMMARY";
						}
						else if(string.equalsIgnoreCase(AppConstants.RETURNORDER))
						{
							invoiceType = "Return Order";
							TITLE = "                    RETURN ORDER SUMMARY";
						}
						else if(string.equalsIgnoreCase(AppConstants.LPO_ORDER))
						{
							invoiceType = string;
							TITLE = "                    LPO ORDER SUMMARY";
						}
						else if(string.equalsIgnoreCase(AppConstants.HOLD_ORDER))
						{
							invoiceType = string;
							TITLE = "                    HOLD ORDER SUMMARY";
						}
						
						Vector<OrderDO> vecOrderDOs = hmOrders.get(string);
						double taxAmt=0.0;
						if(vecOrderDOs != null && vecOrderDOs.size() >0)
						{

							if(!string.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
							{
								float totalAmt = 0;
								woosim.saveSpool(EUC_KR, "\r\n", 0, false);
								woosim.saveSpool(EUC_KR, String.format(formatHeader,"",TITLE+"",""), 0, true);
								woosim.saveSpool(EUC_KR, "\r\n", 0, false);
								woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
								woosim.saveSpool(EUC_KR,String.format(formatInvoice,"","SR#","INVOICE NUMBER","CUSTOMER NAME","AMOUNT"), 0, true);
								count = 0;
								
								for (OrderDO orderDO : vecOrderDOs) 
								{
									count++;
									double onlyTaxAmt =0.0;
									if (string.equalsIgnoreCase(AppConstants.RETURNORDER))
									{
										onlyTaxAmt=-orderDO.VatAmount;
									}
									else {
										onlyTaxAmt = orderDO.VatAmount;

									}
									taxAmt += onlyTaxAmt;
									totalAmt += orderDO.TotalAmount;
//									woosim.saveSpool(EUC_KR, String.format(formatInvoice,"",count+"",orderDO.OrderId+"", orderDO.strCustomerName+"",deffAmt.format(orderDO.TotalAmount)+""), 0, false);
									woosim.saveSpool(EUC_KR, String.format(formatInvoice,"",count+"",orderDO.OrderId+"", orderDO.strCustomerName+"",deffAmt.format(orderDO.TotalAmount+onlyTaxAmt)+""), 0, false);

								}


								if(currencyCode == null || currencyCode.length() <= 0)
									currencyCode = "AED";

								currencyCode=Curr;
								if(totalAmt > 0)
								{
									woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
//									woosim.saveSpool(EUC_KR, String.format(format,"","Total Amount: ","","",currencyCode,deffAmt.format(totalAmt)), 0, true);
//									woosim.saveSpool(EUC_KR, String.format(format,"","TAX Amount: ","","",currencyCode,deffAmt.format(taxAmt)), 0, true);
//									woosim.saveSpool(EUC_KR, String.format(format,"","Grand Total : ","","",currencyCode,deffAmt.format(totalAmt+taxAmt)), 0, true);
									woosim.saveSpool(EUC_KR, String.format(format,"","Total Amount: ","","",currencyCode,deffAmt.format(totalAmt)), 0, true);
									woosim.saveSpool(EUC_KR, String.format(format,"","TAX Amount: ","","",currencyCode,deffAmt.format(taxAmt)), 0, true);
									woosim.saveSpool(EUC_KR, String.format(format,"","Grand Total : ","","",currencyCode,deffAmt.format(totalAmt+taxAmt)), 0, true);
								}
							}
						}
					}
				}
			}
			
			if(eotSummaryPrintDO.vecReplaceOrder != null && eotSummaryPrintDO.vecReplaceOrder.size() > 0)
			{
				String TITLE = "                REPLACEMENT ORDER SUMMARY";
				
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"",TITLE+"",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				woosim.saveSpool(EUC_KR,String.format(formatReplacement,"", "SR#","INVOICE NO.","RECEIPT ITEM DESC","ISSUE ITEM DESC","UOM","QTY"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				int count = 0;
				for(ProductDO productDO : eotSummaryPrintDO.vecReplaceOrder)
				{
					count++;
					woosim.saveSpool(EUC_KR,String.format(formatReplacement,"",""+count,""+productDO.OrderNo,productDO.SKU,productDO.RelatedLineId,productDO.UOM,""+productDO.units), 0, false);
					woosim.saveSpool(EUC_KR,String.format(formatNewDesc,"","",productDO.Description,productDO.Description1), 0, false);
				}
				
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
			
			
			if(eotSummaryPrintDO.hmPayments != null && eotSummaryPrintDO.hmPayments.size() > 0)
			{
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","                    RECEIPT SUMMARY",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				ArrayList<Customer_InvoiceDO> arr = new ArrayList<Customer_InvoiceDO>();
				Set<String> keys = eotSummaryPrintDO.hmPayments.keySet();
				for(String key : keys)
				arr.addAll(eotSummaryPrintDO.hmPayments.get(key));
				
				boolean isCash = false, isCheque = false;
				if(arr!=null && arr.size()>0)
				{
					for(Customer_InvoiceDO cuInvoiceDO : arr)
					{
						if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
						isCheque = true;
						else if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
						isCash = true;
					}
				}
				
				float totalCollections = 0;
				if(isCheque)
				{
					woosim.saveSpool(EUC_KR,String.format(format,"","Site Name","Site No.","Receipt No.","Type","Amount"), 0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
					float strTotalAmount = 0f;
					if(arr!=null && arr.size()>0)
					{
						for(Customer_InvoiceDO cuInvoiceDO : arr)
						{
							if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
							{
								float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
								strTotalAmount += amount;
								woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)), 0, false);
								
								if(currencyCode == null || currencyCode.length() <= 0)
									currencyCode = cuInvoiceDO.currencyCode;
							}
						}
				
						if(strTotalAmount > 0)
						{
							totalCollections += strTotalAmount;
							woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
							woosim.saveSpool(EUC_KR, String.format(format,"","Cheque Total: ","","",currencyCode,deffAmt.format(strTotalAmount)), 0, true);
							woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
						}
					}
				}
				if(isCash)
				{
					woosim.saveSpool(EUC_KR,String.format(format,"","Site Name","Site No.","Receipt No.","Type","Amount"), 0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
					float strTotalCashAmount = 0f;
					for(Customer_InvoiceDO cuInvoiceDO : arr)
					{
						if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
						{
							float amount 		= StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
							strTotalCashAmount += amount;
							woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.siteName,cuInvoiceDO.customerSiteId,cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,deffAmt.format(amount)), 0, false);
							
							if(currencyCode == null || currencyCode.length() <= 0)
								currencyCode = cuInvoiceDO.currencyCode;
						}
					}
					if(strTotalCashAmount > 0)
					{
						totalCollections += strTotalCashAmount;
						woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
						woosim.saveSpool(EUC_KR, String.format(format,"","Cash Total: ","","",currencyCode,deffAmt.format(strTotalCashAmount)), 0, true);
						woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
					}
				}
				
				if(totalCollections > 0)
				{
					woosim.saveSpool(EUC_KR, String.format(format,"","Total Collections: ","","",currencyCode,deffAmt.format(totalCollections)), 0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				}
			}
			
			if(eotSummaryPrintDO.vecInventoryItems != null && eotSummaryPrintDO.vecInventoryItems.size() > 0)
			{
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","                    STOCK SUMMARY",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				woosim.saveSpool(EUC_KR,String.format(formatStock,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);
				
				totalPrice  = 0;
				int count = 0;
				for (InventoryObject vnLoad : eotSummaryPrintDO.vecInventoryItems) {
					count++;
					woosim.saveSpool(EUC_KR,String.format(formatStock,"",""+count,vnLoad.itemCode,vnLoad.itemDescription,vnLoad.UOM,""+deffStock.format(vnLoad.availQty)), 0, false);
				}
					
				
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
			
			if(eotSummaryPrintDO.vecNonInventoryItems != null && eotSummaryPrintDO.vecNonInventoryItems.size() > 0)
			{
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","                NON SALEABLE STOCK SUMMARY",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				woosim.saveSpool(EUC_KR,String.format(formatNonSeleble,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);
				for( int i = 0 ; eotSummaryPrintDO.vecNonInventoryItems!=null && i < eotSummaryPrintDO.vecNonInventoryItems.size() ; i++ )
				{
					woosim.saveSpool(EUC_KR,String.format(formatNonSeleble,"",""+(i+1),eotSummaryPrintDO.vecNonInventoryItems.get(i).itemCode,eotSummaryPrintDO.vecNonInventoryItems.get(i).itemDescription,eotSummaryPrintDO.vecNonInventoryItems.get(i).UOM,""+deffStock.format(eotSummaryPrintDO.vecNonInventoryItems.get(i).PrimaryQuantity)), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatReason,"","Exp. Date: "+eotSummaryPrintDO.vecNonInventoryItems.get(i).expiryDate,"Reason: "+eotSummaryPrintDO.vecNonInventoryItems.get(i).reason, ""), 0, true);
				}
				
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
			
			/*if(eotSummaryPrintDO.vecSalableInventoryItems != null && eotSummaryPrintDO.vecSalableInventoryItems.size() > 0)
			{
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","                    STOCK SUMMARY",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				woosim.saveSpool(EUC_KR,String.format(formatStock,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);
				
				totalPrice  = 0;
				int count = 0;
				for (InventoryObject vnLoad : eotSummaryPrintDO.vecSalableInventoryItems) {
					count++;
					woosim.saveSpool(EUC_KR,String.format(formatStock,"",""+count,vnLoad.itemCode,vnLoad.itemDescription,vnLoad.UOM,""+deffStock.format(vnLoad.availQty)), 0, false);
				}
					
				
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}*/
		/*	if(eotSummaryPrintDO.vecSalableInventoryItems != null && eotSummaryPrintDO.vecSalableInventoryItems.size() > 0)
			{
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatHeader,"","                    SALEABLE STOCK SUMMARY",""), 0, true);
				woosim.saveSpool(EUC_KR, "\r\n", 0, false);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				
				woosim.saveSpool(EUC_KR,String.format(formatNonSeleble,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);
				for( int i = 0 ; eotSummaryPrintDO.vecSalableInventoryItems!=null && i < eotSummaryPrintDO.vecSalableInventoryItems.size() ; i++ )
				{
					woosim.saveSpool(EUC_KR,String.format(formatNonSeleble,"",""+(i+1),eotSummaryPrintDO.vecSalableInventoryItems.get(i).itemCode,eotSummaryPrintDO.vecSalableInventoryItems.get(i).itemDescription,eotSummaryPrintDO.vecSalableInventoryItems.get(i).UOM,""+deffStock.format(eotSummaryPrintDO.vecSalableInventoryItems.get(i).PrimaryQuantity)), 0, false);
					woosim.saveSpool(EUC_KR, String.format(formatReason,"","Exp. Date: "+eotSummaryPrintDO.vecSalableInventoryItems.get(i).expiryDate,"Reason: "+eotSummaryPrintDO.vecSalableInventoryItems.get(i).reason, ""), 0, true);
				}
				
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}*/
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		}

//		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
		
	}
	
	public void printCollectionPaymentSummary()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		
		String formatHeader 	= "%1$-16.16s %2$-41.41s %3$-3.3s";
		String LINE 			= "%1$-10.10s %2$-78.78s\r\n";
		String format 			= "%1$-10.10s %2$-12.12s %3$-6.6s %4$-10.10s %5$-11.11s %6$-20.20s %7$12.12s\r\n";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String lines 			= "-----------------------------------------------------------------------------------------\r\n";
		String singleLine       = "-----------------------------------------------------------------------------------------";
		String dividerLine      = "|                                    |";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","RECEIPT SUMMARY",""), 0x11, true);
		
		printHeader();
		
		String mydate 		= 	java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","User Name:"+preference.getStringFromPreference(Preference.USER_NAME, ""),"Emp No.: "+preference.getStringFromPreference(Preference.EMP_NO, "")), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collected Date: "+(strSelectedDateToPrint != null ? strSelectedDateToPrint : mydate),"Print Date: "+mydate), 0, false);
		
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);	  

		boolean isCash = false, isCheque = false;
		if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
		{
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					isCheque = true;
				else if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
					isCash = true;
			}
		}
		
		String currencyCode = "";
		if(isCheque)
		{
			woosim.saveSpool(EUC_KR,String.format(format,"","Receipt No.","Type","Cheque No.","Dated","Bank Name","Amount"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			Float strTotalAmount = 0f;
			if(arrayListCustomerInvoice!=null && arrayListCustomerInvoice.size()>0)
			{
				for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
				{
					if(cuInvoiceDO.reciptType.equalsIgnoreCase("cheque"))
					{
						float amount = StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
						strTotalAmount += amount;
						woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,cuInvoiceDO.chequeNo,cuInvoiceDO.chequeDate,cuInvoiceDO.bankName,deffAmt.format(amount)), 0, false);
						
						if(currencyCode == null || currencyCode.length() <= 0)
							currencyCode = cuInvoiceDO.currencyCode;
					}
				}
				
				if(strTotalAmount > 0)
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
					woosim.saveSpool(EUC_KR, String.format(format,"","Cheque Total: ","","","",currencyCode,deffAmt.format(strTotalAmount)), 0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				}
			}
		}
				
		if(isCash)
		{
			woosim.saveSpool(EUC_KR,String.format(format,"","Receipt No.","Type","","","","Amount"), 0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			Float strTotalCashAmount = 0f;
			for(Customer_InvoiceDO cuInvoiceDO : arrayListCustomerInvoice)
			{
				if(cuInvoiceDO.reciptType.equalsIgnoreCase("Cash"))
				{
					float amount 		= StringUtils.getFloat(cuInvoiceDO.invoiceTotal);
					strTotalCashAmount += amount;
					woosim.saveSpool(EUC_KR,String.format(format,"",cuInvoiceDO.receiptNo,cuInvoiceDO.reciptType,"","","",deffAmt.format(amount)), 0, false);
					
					if(currencyCode == null || currencyCode.length() <= 0)
						currencyCode = cuInvoiceDO.currencyCode;
				}
			}
			if(strTotalCashAmount > 0)
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
				woosim.saveSpool(EUC_KR, String.format(format,"","Cash Total: ","","","",currencyCode,deffAmt.format(strTotalCashAmount)), 0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
			}
		}
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Collection Head","Salesman"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",dividerLine,dividerLine), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",singleLine,singleLine), 0, true);
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
		
	}
	
	/*
	 * Method to print the Load in 4 inch mode
	 */
	private void PrintLoad()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-40.40s %5$-10.10s %6$-10.10s \r\n";
		String lines 			= "====================================================================================\r\n";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","STOCK VERIFICATION ",""), 0x11, true);
			
		printHeader();

		
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		
		if(strMovementId == null || strMovementId.equalsIgnoreCase(""))
			strMovementId =   "";
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Movement Code: "+strMovementId), 0x09, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Verified By: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Date: "+mydate.substring(0, mydate.lastIndexOf(":"))), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);

		totalPrice  = 0;
		if(movementType!=AppStatus.LOAD_STOCK){
			for( int i = 0 ; vecOrdProduct!=null && i < vecOrdProduct.size() ; i++ )
				woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecOrdProduct.get(i).ItemCode,vecOrdProduct.get(i).Description,vecOrdProduct.get(i).UOM,""+deffAmt.format(vecOrdProduct.get(i).SellableQuantity)), 0, false);
		}
		else{
			for( int i = 0 ; vecOrdProduct!=null && i < vecOrdProduct.size() ; i++ )
				woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecOrdProduct.get(i).ItemCode,vecOrdProduct.get(i).Description,vecOrdProduct.get(i).UOM,""+deffAmt.format(vecOrdProduct.get(i).ShippedQuantity)), 0, false);
			//woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),vecOrdProduct.get(i).ItemCode,vecOrdProduct.get(i).Description,vecOrdProduct.get(i).UOM,""+deffAmt.format(vecOrdProduct.get(i).SellableQuantity)), 0, false);
		}
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	
	/*
	 * Method to print the Load in 4 inch mode
	 */
	private void PrintReturnLoad(String header)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-40.40s %5$-10.10s %6$-10.10s \r\n";
		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
		String lines 			= "------------------------------------------------------------------------------------\r\n";
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"",header,""), 0x11, true);
			
		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","User Code: "+preference.getStringFromPreference(Preference.USER_ID, ""),"Date: "+mydate.substring(0, mydate.lastIndexOf(":"))), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Quantity"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		for( int i = 0 ; arrInventory!=null && i < arrInventory.size() ; i++ )
		{
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),arrInventory.get(i).itemCode,arrInventory.get(i).itemDescription,arrInventory.get(i).UOM,""+deffAmt.format(arrInventory.get(i).PrimaryQuantity)), 0, false);
			woosim.saveSpool(EUC_KR, String.format(formatReason,"","Ex. Date: "+arrInventory.get(i).expiryDate,"Reason: "+arrInventory.get(i).reason, ""), 0, true);
		}
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	/*
	 * Method to print the Load in 4 inch mode
	 */
	private void PrintInventory(String header)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-79.79s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-9.9s %4$-25.25s %5$-4.4s %6$10.10s %7$10.10s %8$10.10s\r\n";
		String lines 			= "------------------------------------------------------------------------------------\r\n";
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"",header,""), 0x11, true);
			
		printHeader();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","User Code: "+preference.getStringFromPreference(Preference.USER_ID, ""),"Date: "+mydate.substring(0, mydate.lastIndexOf(":"))), 0, true);
		
		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Sub-Inventory : "+ subInventory, ""), 0, true);
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","Item Code","Item Description","UOM","Total Qty", "Dlvrd Qty","Avail Qty"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		totalPrice  = 0;
		for( int i = 0 ; arrInventory!=null && i < arrInventory.size() ; i++ )
		{
			InventoryObject invObject = arrInventory.get(i);
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(i+1),invObject.itemCode,invObject.itemDescription,invObject.UOM,""+deffAmt.format(invObject.availCases >= 0 ? invObject.availCases : 0), ""+deffAmt.format(invObject.deliveredCases >= 0 ? invObject.deliveredCases : 0),""+deffAmt.format(invObject.availQty >= 0 ? invObject.availQty : 0)), 0, false);
		}
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	/*
	 * Method to print the Load in 4 inch mode
	 */
	private void PrintLoadInventory()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		String formatForAddress = "%1$-10.10s %2$-38.38s %3$-38.38s\r\n";
		String LINE 			= "%1$-10.10s %2$-75.75s\r\n";
		String format 			= "%1$-10.10s %2$-4s %3$-10.10s %4$-25.25s %5$10.10s %6$10.10s %7$10.10s \r\n";
		String formatReason     = "%1$-10.10s %2$-25.25s %3$-25.25s %4$-25.25s\r\n";
		String lines 			= "====================================================================================\r\n";
		
		String strTitle = "";
		
		if(loadRequestDO.MovementType.equalsIgnoreCase(""+AppStatus.LOAD_STOCK))
			strTitle = "Load Request";
		else
			strTitle = "Unload Request";
			
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"",""+strTitle,""), 0x11, true);
			
		printHeader();

		String subInventory = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
	
		if(strMovementId == null || strMovementId.equalsIgnoreCase(""))
			strMovementId =   "";
		
		woosim.saveSpool(EUC_KR, String.format(INV,"","Movement Code: "+strMovementId), 0x09, true);
		
		String date = "";
		if(loadRequestDO.MovementDate.contains("-"))
			date = CalendarUtils.getFormatedDatefromString(loadRequestDO.MovementDate);
		else
			date = CalendarUtils.getFormatedDatefromString_(loadRequestDO.MovementDate);
		
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Requested No: "+loadRequestDO.MovementCode, "Date : "+ date), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Requested By: "+preference.getStringFromPreference(Preference.USER_NAME, ""), "Sub-Inventory : "+ subInventory), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
	  
		woosim.saveSpool(EUC_KR,String.format(format,"","SR#","ITEM CODE","ITEM DESCRIPTION","UOM","Req. Qty", "App. Qty"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		totalPrice  = 0;
		int count   = 1;
		for(VanLoadDO vanLoadDO : vecOrdProduct)
		{
			float qty = 0;
			//if(loadRequestDO != null && (loadRequestDO.MovementStatus.contains(""+AppConstants.APPROVED_MOVEMENT_STATUS) || loadRequestDO.MovementStatus.contains("100")))
				if(loadRequestDO != null && (loadRequestDO.MovementStatus.contains(LoadRequestDO.STATUS_APPROVED_FROM_ERP) || loadRequestDO.MovementStatus.contains(LoadRequestDO.STATUS_SHIPPED)))
				if(movementType!=AppStatus.LOAD_STOCK)
					qty = vanLoadDO.inProccessQty;
				else
					qty = vanLoadDO.ShippedQuantity;
			
			woosim.saveSpool(EUC_KR,String.format(format,"",""+(count++), vanLoadDO.ItemCode, vanLoadDO.Description, vanLoadDO.UOM,""+deffStock.format(vanLoadDO.SellableQuantity), ""+deffStock.format(qty)), 0, false);
			if(loadRequestDO.MovementType.equalsIgnoreCase(""+AppStatus.UNLOAD_STOCK))
				woosim.saveSpool(EUC_KR, String.format(formatReason,"","Ex. Date: "+vanLoadDO.ExpiryDate,"Reason: "+vanLoadDO.reason, ""), 0, true);
		}
		
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Storekeeper Signature","Salesman Signature\r\n"), 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n\r\n", 0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, true);
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	/*
	 * Method to print the Load in 4 inch mode
	 */
	private void printFormats()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		totalPrice = 0.0f;
		
		String formatHeader 	= "%1$-14.14s %2$-41.41s  %3$-3.3s";
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0x12",""), 0x12, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0x11",""), 0x11, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0x10",""), 0x10, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0x09",""), 0x09, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0x08",""), 0x08, true);
		
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0xa",""), 0xa, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0xb",""), 0xb, true);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Load Request - 0xc",""), 0xc, true);
		
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		printFooter();
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	
	private String printMobileNumber(JourneyPlanDO journeyPlanDO)
	{
		if(journeyPlanDO !=  null && !TextUtils.isEmpty(journeyPlanDO.SalesPersonMobileNumber))
			return journeyPlanDO.SalesPersonMobileNumber;
		return "N/A";
	}
}