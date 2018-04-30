package com.citizen.port;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.citizen.port.android.BluetoothPort;
import com.honeywell.mobility.print.LinePrinter;
import com.honeywell.mobility.print.LinePrinterException;
import com.honeywell.mobility.print.PrinterException;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.SalesmanOrderDetail;
import com.winit.baskinrobbin.salesman.SalesmanOrderPreview;
import com.winit.baskinrobbin.salesman.SalesmanReplacementOrderDetail;
import com.winit.baskinrobbin.salesman.SalesmanReplacementPreview;
import com.winit.baskinrobbin.salesman.SalesmanReturnOrderPreview;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.MasterDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.UserInfoDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.EOTSummaryPrintDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.LoadRequestDO;
import com.winit.baskinrobbin.salesman.dataobject.OrderDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.TrxHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.UOMConversionFactorDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

/**
 * BluetoothConnectMenu
 * @author Abdul Raheem Khan
 * @version 2011. 12. 21.
 */

//Collection
public class PrinterConnectorArabic  extends BaseActivity
{
	private static final String TAG = "BluetoothConnectMenu";
    private static final int REQUEST_ENABLE_BT = 2;
    private LinearLayout llOrderPreview;
	ArrayAdapter<String> adapter;
	private BluetoothAdapter mBluetoothAdapter;
	private Vector<BluetoothDevice> remoteDevices;
	private BroadcastReceiver searchFinish;
	private BroadcastReceiver searchStart;
	private BroadcastReceiver discoveryResult;
	private Context context;
//	private HPPrinterUtils printerUtils;
	private DOTPrinterUtilsArabic printerUtils;
	private  BluetoothAdapter mBAdap;
	
	private String fromDate,toDate;
	private int isMTD=0;
//	private TrxLogHeaders trxMonthDetails;
	private boolean isPreview = false;
	private int PRINT_TYPE = AppConstants.PRINT_TYPE_WITH_PRICE;
	private int PRINT_TYPE_SALES = AppConstants.PRINT_TYPE_WITH_PRICE;
	Vector<Customer_InvoiceDO> vecCollection;
	String userSiteId;
	// UI
	private EditText btAddrBox;
	private Button connectButton,btnFinish,btnReprint,searchButton,btnClose;
	private ListView list;
	// BT
	private BluetoothPort bluetoothPort;
	private CONSTANTOBJ CALLFROM;
	private JourneyPlanDO mallsDetails;
//	private TrxHeaderDO trxHeaderDO;
	private OrderDO trxHeaderDO;
	private OrderDO trxHeaderDO_Dupliacte;
//	private TrxHeaderDO trxHeaderDO_Dupliacte;
	private PaymentHeaderDO objPaymentDO;
	private String strReceiptNo = "";
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers ;
	private ArrayList<JourneyPlanDO> vecJourneyPlanDO;
	private boolean status =false;
	private BluetoothDevice btDev;
	public ArrayList<VanLoadDO> vecOrdProduct;
	public LoadRequestDO loadRequestDO;
	private AssetDO AssetDco;
	public static ArrayList<VanLoadDO> vecsFullVanProduct;
	public ArrayList<VanLoadDO> vecNonSaleableProduct;
	private String userMobileNumber="",userVehicleNumber="N/A";
	private String str="";
	private boolean isFromPendingInvoice = false,isUnload,isCollected,isNewReportPrint;
	private String movementId = "";
	private Vector<Object> vecObjects = new Vector<Object>();
	private ArrayList<PaymentHeaderDO> vecPaymentHeaderDO;
	private HashMap<String,UOMConversionFactorDO> hmUomFactor = new HashMap<String,UOMConversionFactorDO>();
	private HashMap<String,Vector<String>> hmUomDetails = new HashMap<String, Vector<String>>();
	Object obj[];
	//==================newlyadded for REPORTS Print
	private Vector<TrxHeaderDO> vecTrxHeaderDo = new Vector<TrxHeaderDO>();
	private Vector<TrxHeaderDO> vecTrxShortAgeInvoices = new Vector<TrxHeaderDO>();
	private Vector<PaymentHeaderDO> vecPayHeaderDo = new Vector<PaymentHeaderDO>();
	//stockVarienceRoute="",
	public String TRN="";
	String cityCode="";

	private ArrayList<InventoryObject> arrInventory;
	private String from="";
	//-------------------------------
	private Vector<ProductDO> vecSalesOrderProducts;
	private static String address, type = "",strSelectedDateToPrint="";
	public OrderDO orderDO;
	String strOrderId = "";
	float roundOffVal=0.0f;
	private boolean isPrinted = false, isPDUser=false;
	public     String LPO = "";
	private float totalPrice=0.0f;
	private ArrayList<Customer_InvoiceDO> arrayListCustomerInvoice;
    private Customer_InvoiceDO customer_InvoiceDO;
	String TrxReasonCode="";
	boolean isReceiptPrinted=false;
	public static int PAGE_INDEX=0;

    // Set up Bluetooth.
	private void bluetoothSetup()
	{
		// Initialize
		clearBtDevData();
		bluetoothPort = BluetoothPort.getInstance();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) 
		{
		    // Device does not support Bluetooth
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) 
		{
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
		}	
	}
	
	private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
	private static final String fileName = dir + "//BTPrinter";
	private String lastConnAddr;
	private void loadSettingFile()
	{
		int rin = 0;
		char [] buf = new char[128];
		try
		{	
			FileReader fReader = new FileReader(fileName);
			rin = fReader.read(buf);
			if(rin > 0)
			{
				lastConnAddr = new String(buf,0,rin);
				btAddrBox.setText(lastConnAddr);
			}
			fReader.close();
		}
		catch (FileNotFoundException e)
		{
			Log.i(TAG, "Connection history not exists.");
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}	
	}
	
	private void saveSettingFile()
	{
		try
		{
			File tempDir = new File(dir);
			if(!tempDir.exists())
			{
				tempDir.mkdir();
			}
			FileWriter fWriter = new FileWriter(fileName);
			if(lastConnAddr != null)
				fWriter.write(lastConnAddr);
			fWriter.close();
		}
		catch (FileNotFoundException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}	
	}
	
	// clear device data used list.
	private void clearBtDevData()
	{
		remoteDevices = new Vector<BluetoothDevice>();
	}	
	
	//edit_checkinoption
	// add paired device to list
	private void addPairedDevices()
	{
		BluetoothDevice pairedDevice;
		Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
		if(remoteDevices == null)
			remoteDevices = new Vector<BluetoothDevice>();
		while(iter.hasNext())
		{
			pairedDevice = iter.next();
			remoteDevices.add(pairedDevice);
			adapter.add(pairedDevice.getName().trim() +"\n["+pairedDevice.getAddress().trim()+"] [Paired]");			
		}
	}
	
	@Override
	public void initialize() 
	{
		
//		setLanguage(AppConstants.langEnglish);
		if(getIntent().hasExtra("ISNEWREPORT")){
			isNewReportPrint = true;
		}

		printerUtils 		= new DOTPrinterUtilsArabic(PrinterConnectorArabic.this,preference.getStringFromPreference(Preference.CURRENCY_CODE, ""));
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.bluetooth_menu, null);
		llBody.addView(llOrderPreview,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// Setting
		btAddrBox 		= (EditText) llOrderPreview.findViewById(R.id.EditTextAddressBT);
		connectButton 	= (Button) llOrderPreview.findViewById(R.id.ButtonConnectBT);
		searchButton 	= (Button) llOrderPreview.findViewById(R.id.ButtonSearchBT);
		btnReprint 		= (Button) llOrderPreview.findViewById(R.id.btnReprint);
		btnFinish 		= (Button) llOrderPreview.findViewById(R.id.btnFinish);
		btnClose 		= (Button) llOrderPreview.findViewById(R.id.btnClose);
		list 			= (ListView) llOrderPreview.findViewById(R.id.ListView01);
		adapter = new ArrayAdapter<String>(this, R.layout.bluetooth_cell);
		list.setAdapter(adapter);
		context = this;
		CALLFROM 			  = (CONSTANTOBJ) getIntent().getExtras().get("CALLFROM");
		lockDrawer("PrinterConnectorArabic");

		mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
		if(mallsDetails!=null)
			cityCode=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);
		if(cityCode.equalsIgnoreCase("AE")){
			TRN=new SettingsDA().getSettingTRN("TRN-181");
		}else if(cityCode.equalsIgnoreCase("SA"))
		{
			TRN=new SettingsDA().getSettingTRN("TRN-619");
		}

		String cityCode="";
		mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
		if(mallsDetails!=null)
			cityCode=new CustomerDA().getCustomerCurrencyCode(mallsDetails.site);

		if(cityCode.equalsIgnoreCase("AE")){
			TRN=new SettingsDA().getSettingTRN("TRN-181");
		}else if(cityCode.equalsIgnoreCase("SA"))
		{
			TRN=new SettingsDA().getSettingTRN("TRN-619");
		}

		switch (CALLFROM) 
		{
			case PRINT_SALES:

				str = "";
				if(getIntent().hasExtra("isPreview"))
					isPreview  = getIntent().getExtras().getBoolean("isPreview");
				if(getIntent().hasExtra("print_type"))
					PRINT_TYPE  = getIntent().getExtras().getInt("print_type");
				trxHeaderDO_Dupliacte = trxHeaderDO;
			break;
			case ORDER_SUMMARY:

				if(SalesmanOrderDetail.vecOrdProduct != null)
					vecSalesOrderProducts  = 	(Vector<ProductDO>) SalesmanOrderDetail.vecOrdProduct.clone();

				type				  = 	getIntent().getExtras().getString("copy");
				mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
				orderDO			  	  =		(OrderDO) getIntent().getExtras().get("orderDo");
				objPaymentDO	  	  =		(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
				addArabicDetails(mallsDetails);

				if(orderDO != null)
				{
					strOrderId        =     orderDO.OrderId;
					roundOffVal       =     orderDO.roundOffVal;
				}

				if(objPaymentDO != null)
					strReceiptNo = objPaymentDO.ReceiptId;

				checkIsPDCustomer(mallsDetails);
				break;


			
			default:
				break;
		}
		
		new Thread(new Runnable() 
		{


			@Override
			public void run() 
			{
				showLoader("Loading...");
//				switch (CALLFROM)
//				{
//
//					case PRINT_INVENTORY:
////						hmUomFactor  = (HashMap<String, UOMConversionFactorDO>) obj[1];
//						break;
//
//					default:
//						break;
//				}
//				userMobileNumber=new UserInfoDA().getUserMobileNumber(preference.getStringFromPreference(Preference.EMP_NO, ""));
				Vector<VehicleDO> vecTruckList = 	new VehicleDA().getTruckListByDelievryAgentId(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getOrderPostDate());
				if(vecTruckList!=null && vecTruckList.size()>0)
					userVehicleNumber = vecTruckList.get(0).VEHICLE_NO;
//				userSiteId = new CommonDA().getCompanySite(preference.getStringFromPreference(Preference.EMP_NO, ""));
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						loadSettingFile();
						bluetoothSetup();
						addPairedDevices();
					/*	printerUtils.setOrgDetails(userSiteId);
						printerUtils.setMobileNumber(userMobileNumber);
						printerUtils.setVehicleNumber(userVehicleNumber);
						printerUtils.setSignature(salesmanSign,customerSign,logisticSign);*/
					}
				});
			}
		}).start();
		
		
		btAddrBox.setVisibility(View.GONE);
		connectButton.setVisibility(View.GONE);
		btnMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(isFromPendingInvoice)
					showCustomDialog(PrinterConnectorArabic.this, getString(R.string.warning), "Please complete current payment process.", getString(R.string.OK), null, "");
			}	
		});
		
		btnClose.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				performPageOut();
			}
		});
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				performPageOut();
				finish();
				
			}
		});
		btnReprint.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				str = "Reprint";
				if(status)
				{
					performPageOut();
						
					try 
					{
						btConn(btDev);
					}
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		});
		
		// Connect, Disconnect -- Button
		connectButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				if(!bluetoothPort.isConnected()) // Connect routine.
				{
					try
					{
						btConn(mBluetoothAdapter.getRemoteDevice(btAddrBox.getText().toString()));
					}
					catch(IllegalArgumentException e)
					{
						// Bluetooth Address Format [OO:OO:OO:OO:OO:OO]
						Log.e(TAG,e.getMessage(),e);
						 AlertView.showAlert(e.getMessage(), context);
						return;	
					}
					catch (IOException e)
					{
						Log.e(TAG,e.getMessage(),e);
						AlertView.showAlert(e.getMessage(), context);
						return;
					}
				}
				else // Disconnect routine.
				{
					// Always run. 
					btDisconn();
				}
			}
		});		
		// Search Button
		searchButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(mBluetoothAdapter == null)
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if(mBluetoothAdapter == null)
					return;
				if (!mBluetoothAdapter.isDiscovering())
				{	
					clearBtDevData();
					adapter.clear();
					mBluetoothAdapter.startDiscovery();	
				}
				else
				{	
					mBluetoothAdapter.cancelDiscovery();
				}
			}
		});				
		// Bluetooth Device List
		
		// Connect - click the List item.
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				btDev = remoteDevices.elementAt(arg2);
				try
				{
					if(mBluetoothAdapter.isDiscovering())
					{
						mBluetoothAdapter.cancelDiscovery();
					}
					
					btAddrBox.setText(btDev.getAddress());
					lastConnAddr = btDev.getAddress();
					saveSettingFile();
					btConn(btDev);
				}
				catch (IOException e)
				{
					AlertView.showAlert(e.getMessage(), context);
					return;
				}
			}
		});
		
		// UI - Event Handler.
		// Search device, then add List.
		discoveryResult = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				String key;
				BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(remoteDevices == null)
					remoteDevices = new Vector<BluetoothDevice>();
				if(remoteDevice != null)
				{
					if(remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED)
					{
						key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"]";
					}
					else
					{
						key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"] [Paired]";
					}
					remoteDevices.add(remoteDevice);
					adapter.add(key);
				}
			}
		};
		registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		searchStart = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				connectButton.setEnabled(false);
				btAddrBox.setEnabled(false);
				searchButton.setText(getResources().getString(R.string.bt_stop_search_btn));
			}
		};
		registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		searchFinish = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				connectButton.setEnabled(true);
				btAddrBox.setEnabled(true);
				searchButton.setText(getResources().getString(R.string.bt_search_btn));
			}
		};
		registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//		printSlip();
	}
	
	
	public void setLanguage(String selectedLanguage) {
        Locale locale = null;
        try {
            if (selectedLanguage != null && !selectedLanguage.equalsIgnoreCase("")) {
                if (selectedLanguage.equalsIgnoreCase(AppConstants.langEnglish)) {
                    locale = new Locale("en_US");
                } else {
//                	locale = new Locale("ar_EG");
                    locale = new Locale("ar");
                }
                DisplayMetrics dm = getResources().getDisplayMetrics();
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources()
                        .updateConfiguration(config, dm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		String selectedLanguage="";
		 selectedLanguage = preference.getStringFromPreference(Preference.LANGUAGE, "");
		/*if(selectedLanguage.equalsIgnoreCase(AppConstants.langEnglish))
		setLanguage(AppConstants.langEnglish);
		else
		setLanguage(AppConstants.langArabic);*/
		performPageOut();
		finish();
	}
	@Override
	protected void onDestroy()
	{
		try
		{
			saveSettingFile();
			if (bluetoothPort!=null) {
				
				bluetoothPort.disconnect();
			}
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
//		if((hThread != null) && (hThread.isAlive()))
//		{
//			hThread.interrupt();
//			hThread = null;
//		}	
		unregisterReceiver(searchFinish);
		unregisterReceiver(searchStart);
		unregisterReceiver(discoveryResult);
		super.onDestroy();
	}
	
	private connTask connectionTask;
	// Bluetooth Connection method.
	private void btConn(final BluetoothDevice btDev) throws IOException
	{
		if((connectionTask != null) && (connectionTask.getStatus() == AsyncTask.Status.RUNNING))
		{
			connectionTask.cancel(true);
			if(!connectionTask.isCancelled())
				connectionTask.cancel(true);
			connectionTask = null;
		}
		connectionTask = new connTask();
		connectionTask.execute(btDev);
	}
	// Bluetooth Disconnection method.
	private void btDisconn()
	{
		try
		{
			bluetoothPort.disconnect();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
//		if((hThread != null) && (hThread.isAlive()))
//			hThread.interrupt();
		// UI
		connectButton.setText(getResources().getString(R.string.dev_conn_btn));
		list.setEnabled(true);
		btAddrBox.setEnabled(true);
		searchButton.setEnabled(true);
		Toast toast = Toast.makeText(context, getResources().getString(R.string.bt_disconn_msg), Toast.LENGTH_SHORT);
		toast.show();
	}
	
	// Bluetooth Connection Task.
	class connTask extends AsyncTask<BluetoothDevice, Void, Integer>
	{
		@Override
		protected void onPreExecute()
		{
			showLoader("Connecting...");
		}
		
		@Override
		protected Integer doInBackground(BluetoothDevice... params)
		{			
//			boolean status=true;
			LinePrinter linePrinter = null;
			//enableBlueTooth();
			if(params[0] != null)
			{
				try 
				{
					
					String sResult = null;
					String sPrinterID = "P6824";
					String sMacAddr = lastConnAddr;
					String sDocNumber = "1234567890";
					String sPrinterURI = "bt://" + params[0].getAddress();
//					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//					// create socket Secure Connection
//					//				 mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//					// create socket InSecure Connection
//					mmSocket = params[0].createInsecureRfcommSocketToServiceRecord(uuid);
//					// To initiate the outgoing connection.
//					mmSocket.connect();
					LinePrinter.ExtraSettings exSettings = new LinePrinter.ExtraSettings();
					 exSettings.setContext(context);
								
					linePrinter = new LinePrinter(AppConstants.jsonCmdAttribStr,sPrinterID,sPrinterURI,exSettings);
//					linePrinter.addPrintProgressListener(progressListener);
					printerUtils.setLinePrinter(linePrinter);

					int numtries = 0;
					int maxretry = 2;
					while(numtries < maxretry)
					{
						try{
							linePrinter.connect();  // Connects to the printer
							status = true;
							break;
						}
						catch(LinePrinterException ex){
							numtries++;
							status = false;
//							Thread.sleep(1000);
							Thread.sleep(8000);
						} catch (PrinterException e) {
							// TODO Auto-generated catch block
							status = false;
							e.printStackTrace();
						}
					}
					
					if (numtries == maxretry) linePrinter.connect();//Final retry
					
					int[] results = linePrinter.getStatus();
					if (results != null)
					{
						for (int err = 0; err < results.length; err++)
						{
							if (results[err] == 223)
							{
								// Paper out.
							}
							else if (results[err] == 227)
							{
								// Lid open.
							}
						}
					}
					
				}
				catch (NullPointerException e) 
				{
					status=false;
					e.printStackTrace();
				} 
				catch (Exception e) 
				{
					status=false;
					e.printStackTrace();
				}
			}
			
//			if(isNewReportPrint)
////				printerOtherUtils.Connect(linePrinter);
//			else
				printerUtils.Connect(linePrinter);
			
			Integer retVal = null;
			retVal = Integer.valueOf(0);
			
//			if(status)	
//			{
//				printSlip();
//				
//			}
//			else	
//			{
//				hideLoader();
//				showCustomDialog(context, getResources().getString(R.string.bt_conn_fail_msg), getResources().getString(R.string.dev_check_msg), ""+getString(R.string.OK), "", "Print Invoices", true);
//			}
			
			return retVal;
		}
		
		void enableBlueTooth()
		{

			mBAdap = BluetoothAdapter.getDefaultAdapter();


			if(mBAdap == null) 
			{
				//	         myLabel.setText("No bluetooth adapter available");
				Toast.makeText(context, "No bluetooth  available", Toast.LENGTH_LONG).show();
			}
			else if(mBAdap!= null && !mBAdap.isEnabled()) 
			{
				//	         Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				Toast.makeText(context, "Enable Bluetooth ", Toast.LENGTH_LONG).show();

			}

		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			try
			{
				if(status)	// Connection success.
				{
					connectButton.setText(getResources().getString(R.string.dev_disconn_btn));
					list.setEnabled(false);
					btAddrBox.setEnabled(false);
					searchButton.setEnabled(false);
					Toast toast = Toast.makeText(context, getResources().getString(R.string.bt_conn_msg), Toast.LENGTH_SHORT);
					toast.show();
					printSlip();
					
					btnReprint.setVisibility(View.VISIBLE);
					btnFinish.setVisibility(View.VISIBLE);
					searchButton.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
					
				}
				else	// Connection failed.
				{
					hideLoader();
					showCustomDialog(context, getResources().getString(R.string.bt_conn_fail_msg), getResources().getString(R.string.dev_check_msg), ""+getString(R.string.OK), "", "Print Invoices", true);
				}
				super.onPostExecute(result);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				hideLoader();
			}
		}
	}
	public void sortByDate(Vector<Customer_InvoiceDO> vec){
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
		    @Override
		   public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) {
		    	return s2.reciptDate.compareToIgnoreCase(s1.reciptDate);
		    }
		});
	}
	String printdata = "";
	boolean isVertical=false;
	private static final String PRINTER_LOCK = "PRINTER_LOCK";
	private void printSlip()
	{
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run()
			{
				showLoader("Printing...");
				PAGE_INDEX=0;
				new Thread(new Runnable() 
				{
					boolean isNewPriceApplicable = false;
					@Override
					public void run() 
					{
						synchronized (PRINTER_LOCK) 
						{
							switch (CALLFROM) {
								case PRINT_SALES:
									if (SalesmanOrderPreview.vecMainProducts != null)
										vecSalesOrderProducts = (Vector<ProductDO>) SalesmanOrderPreview.vecMainProducts.clone();

									totalPrice = getIntent().getExtras().getFloat("totalPrice");
									type = "Invoice Preview";
									strOrderId = getIntent().getExtras().getString("OrderId");
									LPO = getIntent().getExtras().getString("LPO");
									roundOffVal = getIntent().getExtras().getFloat("roundOffVal");
									mallsDetails = (JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
									if(TextUtils.isEmpty(strOrderId))
										strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
									OrderDO ord  = new CommonDA().getSalesOrder(strOrderId);


									checkIsPDCustomer(mallsDetails);
									if (isPDUser)
//										PrintMoveOrder();
//										printerUtils.PrintMoveOrder(vecSalesOrderProducts, totalPrice, type, strOrderId, LPO, roundOffVal, mallsDetails);
										printerUtils.  PrintMoveOrder( vecSalesOrderProducts,   type,   strOrderId,   LPO,   roundOffVal,   mallsDetails,   0.0f,  0);

									else {

										checkIsPDCustomer(mallsDetails);
										printerUtils.printPaymentInVoiceReceipt1(true,false, ord, mallsDetails, roundOffVal, vecSalesOrderProducts, TRN,type,       objPaymentDO, 0, 0.0f, 0.0f, 0.0f,0.0f);

//										printerUtils.PrintSalesOrder(vecSalesOrderProducts, type, strOrderId, LPO, roundOffVal, mallsDetails, 0.0f, 0.0f, 0, TRN, 0, 0);
									}

									break;
								case PRINT_SALES_RETURN:
									vecSalesOrderProducts = (Vector<ProductDO>) SalesmanReturnOrderPreview.vecMainProducts.clone();
									totalPrice = getIntent().getExtras().getFloat("totalPrice");
									type = "Invoice Preview";
									strOrderId = getIntent().getExtras().getString("OrderId");
									mallsDetails = (JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
									from = getIntent().getExtras().getString("from");
									LPO = getIntent().getExtras().getString("LPO");
									TrxReasonCode			  = 	getIntent().getExtras().getString("TrxReasonCode");
									checkIsPDCustomer(mallsDetails);
									addArabicDetails(mallsDetails);
									HashMap<String,String> hmArabic = new CommonDA().getAllArabicItems(strOrderId);
									for (ProductDO productDO : vecSalesOrderProducts)
										productDO.Description1=hmArabic.get(productDO.SKU);
									orderDO = new CommonDA().getSalesOrder(strOrderId);
									printerUtils.printRetOrder(TRN,false, mallsDetails,orderDO,vecSalesOrderProducts,TrxReasonCode,0,0,0,0);
//									printerUtils.printReturnOrder(vecSalesOrderProducts,   type, strOrderId, mallsDetails, from, LPO,0.0f,0.0f,0,TRN,0,0);
									break;
								case PRINT_SALES_REPLACE:
//						      	Have  to Write for This from SalesmanReturnOrderPreview
									vecSalesOrderProducts = (Vector<ProductDO>) SalesmanReplacementPreview.vecMainProducts.clone();
									totalPrice = getIntent().getExtras().getFloat("totalPrice");
									type = "Invoice Preview";
									strOrderId = getIntent().getExtras().getString("OrderId");
									mallsDetails = (JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
									from = getIntent().getExtras().getString("from");

									checkIsPDCustomer(mallsDetails);
//									printerUtils.printReplacementOrder(vecSalesOrderProducts, totalPrice, type, strOrderId, mallsDetails, from);
									printerUtils.printReplacementOrder(vecSalesOrderProducts, totalPrice, type, strOrderId, mallsDetails, from,0);
									break;
								case PAYMENT_INVOICE_RECEIPT:
									strOrderId = getIntent().getExtras().getString("OrderId");
									if (SalesmanOrderPreview.vecMainProducts != null)
										vecSalesOrderProducts = (Vector<ProductDO>) SalesmanOrderPreview.vecMainProducts.clone();
									HashMap<String,String> hmA  = new CommonDA().getAllArabicItems(strOrderId);
									for (ProductDO productDO : vecSalesOrderProducts)
										productDO.Description1=hmA.get(productDO.SKU);
									totalPrice = getIntent().getExtras().getFloat("totalAmount");
									strReceiptNo = getIntent().getExtras().getString("strReceiptNo");
									mallsDetails = (JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
									objPaymentDO = (PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");

									LPO = getIntent().getExtras().getString("LPO");
									roundOffVal = getIntent().getExtras().getFloat("roundOffVal");
									type = "";
									if(TextUtils.isEmpty(strOrderId))
										strOrderId = preference.getStringFromPreference(Preference.ORDER_NO, "");
									OrderDO or  = new CommonDA().getSalesOrder(strOrderId);
									checkIsPDCustomer(mallsDetails);
									isReceiptPrinted =printerUtils.printPaymentInVoiceReceipt1(  true,false,   or,   mallsDetails,     roundOffVal, vecSalesOrderProducts,   TRN,  type,       objPaymentDO, 0 , 0.0f,0.0f,0.0f ,0.0f) ;
									if(!isReceiptPrinted )
										printerUtils.printPaymentInVoiceReceipt2(   mallsDetails, type,       objPaymentDO,0);
									break;
								case PAYMENT_RECEIPT:
									totalPrice			  = 	getIntent().getExtras().getFloat("totalAmount");
									type				  = 	"Invoice Preview";
									strReceiptNo		  =		getIntent().getExtras().getString("strReceiptNo");
									mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									objPaymentDO  	  	  = 	(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
									checkIsPDCustomer(mallsDetails);
									printerUtils.printPaymentReceipt("Receipt Detail",type, strReceiptNo,mallsDetails,objPaymentDO);
									break;

								case PRINT_INVENTORY:
									arrInventory =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vec");
									from  		 = "Stock Inventory";
									printerUtils.printInventory(arrInventory,from,CALLFROM,0.0f,0.0f,0.0f,0.0f,0);
//									arrInventory 		  =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vec");
//									from = getIntent().getExtras().getString("type");
//
//									if(from != null && from.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
//										from = "Replaced Inventory";
//									else
//										from = "Returned Inventory";
//									printerUtils.printStockInventorySummary(arrInventory,vecOrdProduct,CALLFROM,"",false,"",hmUomFactor);
									break;
//								case PRINT_VERIFY_INVENTOTY:
//									printerUtils.printStockInventorySummary(vecOrdProduct,CALLFROM,""+movementId,isUnload,"",hmUomFactor);
//									break;
								case LPO_DELIVERY_NOTE:
								case LPO_DELIVERY_NOTE_SUMMARY:
									ArrayList<ProductDO>arr  = (ArrayList<ProductDO>) getIntent().getExtras().get("array");
									vecSalesOrderProducts = 	new Vector<ProductDO>(arr);
									mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									orderDO			  	  =		(OrderDO) getIntent().getExtras().get("orderDo");
//									for (int x= 0;x<6;x++)
//										vecSalesOrderProducts.addAll(vecSalesOrderProducts);

									if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE)
										printerUtils.printLPOOrder(false,vecSalesOrderProducts,mallsDetails,orderDO,0,0.0f);
									else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY)
										printerUtils.printLPOOrder(true,vecSalesOrderProducts,mallsDetails,orderDO,0,0.0f);

									break;

								case ORDER_SUMMARY:
									HashMap<String,String> hmAra  = new CommonDA().getAllArabicItems(orderDO.OrderId);

									if(objPaymentDO == null)
									{
										if(vecSalesOrderProducts!=null && vecSalesOrderProducts.size()>0)
											orderDO.vecProductDO=vecSalesOrderProducts;

										for (ProductDO productDO : vecSalesOrderProducts)
											productDO.Description1=hmAra.get(productDO.SKU);
//										for (int i = 0;i<=1;i++)
//										{
//											orderDO.vecProductDO.addAll(orderDO.vecProductDO);
//										}
										if(orderDO.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
										{
											for (ProductDO productDO : vecSalesOrderProducts)
												productDO.Description1=hmAra.get(productDO.SKU);
//											for (int i = 0;i<=2;i++)
//											{
//												vecSalesOrderProducts.addAll( vecSalesOrderProducts);
//											}
//											vecSalesOrderProducts.add(vecSalesOrderProducts.get(0));
//											vecSalesOrderProducts.add(vecSalesOrderProducts.get(0));
//											isFromOS=true;
//											PrintReturnOrderNew();
											printerUtils.printRetOrder(TRN,true, mallsDetails,orderDO,orderDO.vecProductDO,TrxReasonCode,0,0,0,0);
										}else {
											for (ProductDO productDO : vecSalesOrderProducts)
												productDO.Description1=hmAra.get(productDO.SKU);
											printerUtils.printPaymentInVoiceReceipt1(  false,true,   orderDO,   mallsDetails,     roundOffVal, vecSalesOrderProducts,   TRN, type,       objPaymentDO,  0 , 0.0f,0.0f,0.0f ,0.0f) ;
										}
									}
									else {
										if(vecSalesOrderProducts!=null && vecSalesOrderProducts.size()>0)
											orderDO.vecProductDO=vecSalesOrderProducts;
										for (ProductDO productDO : vecSalesOrderProducts)
											productDO.Description1=hmAra.get(productDO.SKU);
										isReceiptPrinted =printerUtils.printPaymentInVoiceReceipt1(   true, true,   orderDO,   mallsDetails,     roundOffVal, vecSalesOrderProducts,   TRN,type,       objPaymentDO,   0 , 0.0f,0.0f,0.0f ,0.0f);
										if(!isReceiptPrinted )
											printerUtils.printPaymentInVoiceReceipt2( mallsDetails, type,       objPaymentDO,0);
									}

									/*if(objPaymentDO == null)
										printerUtils.printOrderSummary(orderDO,mallsDetails,type,roundOffVal);
									else
										printerUtils.printPaymentInVoiceReceipt("Receipt Detail",orderDO,mallsDetails,type,roundOffVal,vecSalesOrderProducts,objPaymentDO);
 */
									break;
								case PRINT_RETURN_INVENTORY:
									arrInventory 		  =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vec");
									from  				  =  getIntent().getExtras().getString("type");

									if(from != null && from.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
										from = "Replaced Inventory";
									else
										from = "Returned Inventory";
//									printerUtils.printReturnLoad(from , arrInventory );
									printerUtils.printReturnLoad(from , arrInventory,0);
									break;
								case PAYMENT_SUMMARY:
									arrayListCustomerInvoice =  (ArrayList<Customer_InvoiceDO>) getIntent().getExtras().getSerializable("arrayList");
									strSelectedDateToPrint	 =  getIntent().getExtras().getString("strSelectedDateToPrint");
									type	 				 =  getIntent().getExtras().getString("type");
									checkIsPDCustomer(null);
									if(type != null && type.equalsIgnoreCase(AppConstants.ACCOUNT_COPY))
//										printARPaymentSummary();
//										printerUtils.printARPaymentSummary(from , arrayListCustomerInvoice,strSelectedDateToPrint,type,0);
										printerUtils.printARPaymentSummary(from , arrayListCustomerInvoice,strSelectedDateToPrint,type,0);
									else
										printerUtils.printCollectionPaymentSummary(from , arrayListCustomerInvoice,strSelectedDateToPrint,type);
//									printCollectionPaymentSummary();

									break;
								case PAYMENT_SEP_SUMMARY:
									mallsDetails  		=  (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									customer_InvoiceDO	=  (Customer_InvoiceDO) getIntent().getExtras().get("object");
									//below loop for testing (need to remove)
//									for (int x= 0;x<6;x++)
//										customer_InvoiceDO.vecPaymentDetailDOs.addAll(customer_InvoiceDO.vecPaymentDetailDOs);
									printerUtils.printPaymentReceiptSummary(mallsDetails,customer_InvoiceDO,0);

									break;
								case PRINT_LOAD_REQUEST:

									vecOrdProduct 		  =  (ArrayList<VanLoadDO>) getIntent().getExtras().getSerializable("array");
									loadRequestDO		  =  (LoadRequestDO) getIntent().getExtras().getSerializable("loadRequestDO");
									String strMovementId=loadRequestDO.MovementCode;
									int  movementType= StringUtils.getInt(loadRequestDO.MovementType);
									checkIsPDCustomer(null);
//									for (int x= 0;x<60;x++)
//										vecOrdProduct.add(vecOrdProduct.get(0));
									printerUtils.printLoadInventory(vecOrdProduct,loadRequestDO,strMovementId,movementType,0);


									break;
								case PRINT_SALES_REPLACE_SUMMARY:


									vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanReplacementOrderDetail.vecOrdProduct.clone();
									totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");
									type				  = 	"Invoice Preview";
									strOrderId			  = 	getIntent().getExtras().getString("OrderId");
									mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									from			      = 	getIntent().getExtras().getString("from");

									checkIsPDCustomer(mallsDetails);
									printerUtils.printReplacementOrderSummary(vecSalesOrderProducts, type,strOrderId,mallsDetails,from, 0,isPDUser );


									break;
								case PRINT_EOT_SUMMARY:
									EOTSummaryPrintDO eotSummaryPrintDO =  (EOTSummaryPrintDO) getIntent().getExtras().getSerializable("EOTSummaryPrintDO");
									type	 		  =  getIntent().getExtras().getString("type");
									checkIsPDCustomer(null);
									printerUtils.printEOTSummary(eotSummaryPrintDO,type);
									break;
								case PRINT_VERIFY_ITEMS_IN_VEHICLE:
									vecOrdProduct 		  =  (ArrayList<VanLoadDO>) getIntent().getExtras().getSerializable("itemforVerification");
									strMovementId=getIntent().getExtras().getString("strMovementNo");
									movementType= StringUtils.getInt(getIntent().getExtras().getString("movementType"));
									checkIsPDCustomer(null);
									printerUtils.PrintLoad(vecOrdProduct,strMovementId,movementType,0.0f,0);
									break;
								default:
									printdata ="";
									break;
							}

						}
						try 
						{
							Thread.sleep(8000);
						} 
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						try {
							printerUtils.closeBT();
//							printerOtherUtils.closeBT();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runOnUiThread(new Runnable() 
						{
							public void run() 
							{
								hideLoader();
							}
						});
					}

					
				}).start();

			}
		}, 5000);
	
	}
	
	private void performPageOut(){
		
//		if(isNewReportPrint)
//			printerOtherUtils.Pageout();
//		else
			printerUtils.Pageout();
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
	
	
}
