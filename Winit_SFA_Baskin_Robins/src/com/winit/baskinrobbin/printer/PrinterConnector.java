package com.winit.baskinrobbin.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.SalesmanOrderPreview;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SettingsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.Customer_InvoiceDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.PaymentHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.PendingInvicesDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.dataobject.TrxHeaderDO;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.dataobject.VehicleDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;





public class PrinterConnector  extends BaseActivity 
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
	private String selectedDate;
	private Context context;
	private HPPrinterUtils printerUtils;
	private HashMap<String,Customer_InvoiceDO> hmChequeDetails;
	private HashMap<String,Customer_InvoiceDO> hmCashDetails;
	private String fromDate,toDate;
	private int isMTD=0;
	Vector<Customer_InvoiceDO> vecCollection;
	
	// UI
	private EditText btAddrBox;
	private Button connectButton,btnFinish,btnReprint,searchButton,btnClose;
	private ListView list;
	private Vector<ProductDO> vecSalesOrderProducts;
	// BT
	private BluetoothPort bluetoothPort;
	private String isCashSelected= "";
	private boolean isSingleMode= false;
	private boolean isCustomerSummary= false;
	private String customerId="";
	private CONSTANTOBJ CALLFROM;
	private JourneyPlanDO mallsDetails;
	private TrxHeaderDO trxHeaderDO;
	private ArrayList<PaymentHeaderDO> arrPaymentDO;
	private String strReceiptNo;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers ;
	private boolean status =false;
	private BluetoothDevice btDev;
	public ArrayList<VanLoadDO> vecOrdProduct;
	public static ArrayList<VanLoadDO> vecsFullVanProduct;
	public ArrayList<VanLoadDO> vecNonSaleableProduct;
	private String userMobileNumber="",userVehicleNumber="N/A";
	private String str="",from="";
	private boolean isFromPendingInvoice = false;
	private String movementId = "";
	private Vector<Object> vecObjects = new Vector<Object>();
	
	private float totalPrice;
	private static String address, type = "";
	private String  strOrderId = "",LPO;
	private int precision;
	
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
		while(iter.hasNext())
		{
			pairedDevice = iter.next();
			remoteDevices.add(pairedDevice);
			adapter.add(pairedDevice.getName() +"\n["+pairedDevice.getAddress()+"] [Paired]");			
		}
	}
	
	@Override
	public void initialize() 
	{

		printerUtils 		= new HPPrinterUtils(PrinterConnector.this);
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.bluetooth_menu, null);
		llBody.addView(llOrderPreview,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// Setting
		btAddrBox 		= (EditText) llOrderPreview.findViewById(R.id.EditTextAddressBT);
		connectButton 	= (Button) llOrderPreview.findViewById(R.id.ButtonConnectBT);
		searchButton 	= (Button) llOrderPreview.findViewById(R.id.ButtonSearchBT);
		btnReprint 		= (Button) llOrderPreview.findViewById(R.id.btnReprint);
		btnFinish 		= (Button) llOrderPreview.findViewById(R.id.btnFinish);
		btnClose 		= (Button) llOrderPreview.findViewById(R.id.btnClose);
		list 			= (ListView) llOrderPreview.findViewById(R.id.ListView01);
		context = this;
		CALLFROM 			  = (CONSTANTOBJ) getIntent().getExtras().get("CALLFROM");
		//lockDrawer("PrinterConnector");
		switch (CALLFROM) 
		{
			case PRINT_SALES:
				if(SalesmanOrderPreview.vecMainProducts != null)
					vecSalesOrderProducts = 	(Vector<ProductDO>) SalesmanOrderPreview.vecMainProducts.clone();
					
				totalPrice			  = 	getIntent().getExtras().getFloat("totalPrice");	
				type				  = 	"Invoice Preview";
				strOrderId			  = 	getIntent().getExtras().getString("OrderId");
				LPO			 	 	  =		getIntent().getExtras().getString("LPO");
				mallsDetails  		  =     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
				break;
			/*case PAYMENT_RECEIPT:
				arrInvoiceNumbers = (ArrayList<PendingInvicesDO>) getIntent().getExtras().getSerializable("arrInvoiceNumbers");
				strReceiptNo 	= getIntent().getExtras().getString("strReceiptNo");
				mallsDetails  	= (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
				arrPaymentDO 	= (ArrayList<PaymentHeaderDO>) getIntent().getExtras().get("arrlist");
				if(getIntent().hasExtra("str"))
					 str  = getIntent().getExtras().getString("str");
			break;*/
			/*case PRINT_LOG_INVENTORY:
			 selectedDate = getIntent().getExtras().getString("selectedDate");
			break;
			case PRINT_VERIFY_INVENTOTY:
				movementId = getIntent().getExtras().getString("movementId");
			break;
			case PAYMENT_SUMMARY:
				fromDate  		=     getIntent().getExtras().getString("fromDate");
				toDate  		=     getIntent().getExtras().getString("toDate");
				isSingleMode 	= 	  getIntent().getExtras().getBoolean("isSingleMode");
				isCashSelected 	=	  getIntent().getExtras().getString("isCashSelected");
				isCustomerSummary 	= getIntent().getExtras().getBoolean("isCustomerSummary");
				customerId  	= getIntent().getExtras().getString("customerId");
			break;
			case PRINT_LOG_REPORT:
				fromDate  		=     getIntent().getExtras().getString("fromDate");
				toDate  		=     getIntent().getExtras().getString("toDate");
				isMTD			= 	  getIntent().getExtras().getInt("isMTD");
			break;
			case PRINT_SCORE_CARD:
				toDate  		=     getIntent().getExtras().getString("toDate");
			break;
			case PRINT_PENDING_INVOICE:
				isFromPendingInvoice = true;
				mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
			break;
			case PRINT_CUSTOMER_STATEMENT:
				mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
				fromDate  		=     getIntent().getExtras().getString("fromDate");
				toDate  		=     getIntent().getExtras().getString("toDate");
			break;
			case PRINT_CUSTOMER_PAYMENT_SUMMARY:
				mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
			break;
			case PRINT_ORDER_SUMMARY:
				mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
			break;*/
			
			default:
				break;
		}
		adapter = new ArrayAdapter<String>(this, R.layout.bluetooth_cell);
		list.setAdapter(adapter);
		new Thread(new Runnable() 
		{


			@Override
			public void run() 
			{
				showLoader("Loading...");
				switch (CALLFROM) 
				{
					case PRINT_INVENTORY:
						precision=new SettingsDA().getSettingsByName(AppConstants.RoundOffDecimals);
						vecOrdProduct =	new VehicleDA().getAllItemToVerify(precision);
						break;
					/*case PRINT_NON_SALEABLE_INVENTORY:
						vecNonSaleableProduct= new VehicleDA().getAllItemToVerifyNonSellable();
					break;
					case PRINT_LOG_INVENTORY:
						vecOrdProduct=new VehicleDA().getVanStockLog(selectedDate);
					break;
					case PRINT_VERIFY_INVENTOTY:
						vecOrdProduct=new VehicleDA().getAllItemToVerifyByMovementID(movementId);
					break;
					case PRINT_FULL_VANSTOCK_INVENTORY:
						vecsFullVanProduct = new VehicleDA().getFullStock();
					break;
					case PAYMENT_SUMMARY:
						vecCollection =new Vector<Customer_InvoiceDO>();
						vecCollection = new PaymentSummeryDA().getPaymentSummaryForPrint(fromDate, toDate,isCustomerSummary,isSingleMode,isCashSelected, customerId);
					break;
					case PRINT_LOG_REPORT:
						trxMonthDetails = new TransactionsLogsDA().getCurrentMonthDetails(fromDate, toDate,isMTD);
						Vector<TrxLogHeaders> vecTrxHeaderDOs;
						vecTrxHeaderDOs = new TransactionsLogsDA().getTrxLogHeaders(fromDate,toDate,isMTD);
						trxLogHeaders = vecTrxHeaderDOs.get(0);
					break;
					case PRINT_PENDING_INVOICE:
						arrInvoiceNumbers = new ARCollectionDA().getPendingInvoices_(mallsDetails.customerId, null);
					break;
					case PRINT_SCORE_CARD:
						vecObjects = new ScoreCardDA().getScoreCard(toDate, preference.getStringFromPreference(Preference.EMP_NO, ""));
					break;
					case PRINT_CUSTOMER_STATEMENT:
						Vector<CustomerStatmentDO> arrDetails =  new Vector<CustomerStatmentDO>();
						arrDetails = new OrderDA().getCustomerStatementPrint(mallsDetails.site);
						vecDetails = new ArrayList<CustomerStatmentDO>();
						vecDetails.addAll(arrDetails);
					break;*/
					default:
						break;
				}
			//	userMobileNumber=new UserInfoDA().getUserMobileNumber(preference.getStringFromPreference(Preference.EMP_NO, ""));
				Vector<VehicleDO> vecTruckList = 	new VehicleDA().getTruckListByDelievryAgentId(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getOrderPostDate());
				if(vecTruckList!=null && vecTruckList.size()>0)
					userVehicleNumber = vecTruckList.get(0).VEHICLE_NO;
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						loadSettingFile();
						bluetoothSetup();
						addPairedDevices();
						//printerUtils.setMobileNumber(userMobileNumber);
						//printerUtils.setVehicleNumber(userVehicleNumber);
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
					showCustomDialog(PrinterConnector.this, "Warning !", "Please complete current payment process.", getString(R.string.OK), null, ""); 
			}	
		});
		
		btnClose.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				printerUtils.Pageout();
			}
		});
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				printerUtils.Pageout();
				finish();
				
			}
		});
		btnReprint.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View arg0) 
			{
				str = " - Reprint";
				if(status)
				{
						printerUtils.Pageout();
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
						Log.e(TAG,e.getMessage(),e);
						return;	
					}
					catch (IOException e)
					{
						Log.e(TAG,e.getMessage(),e);
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
				searchButton.setText("Stop");
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
				searchButton.setText("Refresh");				
			}
		};
		registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//		printSlip();
	}
	
	@Override
	protected void onDestroy()
	{
		try
		{
			saveSettingFile();
			bluetoothPort.disconnect();
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
		connectButton.setText("Connect");
		list.setEnabled(true);
		btAddrBox.setEnabled(true);
		searchButton.setEnabled(true);
		Toast toast = Toast.makeText(context, "Bluetooth Disconnected", Toast.LENGTH_SHORT);
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
			status=printerUtils.Connect(params[0]);
			Integer retVal = null;
			retVal = Integer.valueOf(0);
			return retVal;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			hideLoader();
			try
			{
				if(status)	// Connection success.
				{
					connectButton.setText("Disconnect");
					list.setEnabled(false);
					btAddrBox.setEnabled(false);
					searchButton.setEnabled(false);
					Toast toast = Toast.makeText(context, "Bluetooth Connected", Toast.LENGTH_SHORT);
					toast.show();
					printSlip();
					btnReprint.setVisibility(View.VISIBLE);
					btnFinish.setVisibility(View.VISIBLE);
					searchButton.setVisibility(View.GONE);
					list.setVisibility(View.INVISIBLE);
					
				}
				else	// Connection failed.
				{
					showCustomDialog(context, "Bluetooth Connection Failed", "Check the device status or settings.", "OK", "", "Print Invoices", true);
				}
				super.onPostExecute(result);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
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
	private void printSlip()
	{
		
		showLoader("Printing...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
				switch (CALLFROM) 
				{
					case PRINT_SALES:
						printerUtils.baskinRobin(vecSalesOrderProducts,totalPrice,type,strOrderId , LPO ,mallsDetails );
						break;
						
					/*case CONSTANTOBJ.PRINT_SALES:
						if(isPDUser)
							PrintMoveOrder();
						else
							PrintSalesOrder();
						break;
						
						
					case CONSTANTOBJ.LPO_DELIVERY_NOTE:
							PrintSalesOrder();
						break;
						
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
						
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
						
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
					case CONSTANTOBJ.PRINT_SALES:
						PrintSalesOrder();
					break;
					
							
							
							else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE)
							
									
							else if(CALLFROM == CONSTANTOBJ.LPO_DELIVERY_NOTE_SUMMARY)
								printLPOOrder(true);
							
							else if(CALLFROM == CONSTANTOBJ.ORDER_SUMMARY)
							{
								if(objPaymentDO == null)
									printOrderSummary();
								else
									printPaymentInVoiceReceipt("Receipt Detail");
							}
							
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_RECEIPT)
								printPaymentReceipt("Receipt Detail");
							
							else if(CALLFROM == CONSTANTOBJ.PAYMENT_INVOICE_RECEIPT)
								printPaymentInVoiceReceipt("Receipt Detail");
							
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
								PrintReturnOrder();
							
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
						*/
						
						
					/*case PAYMENT_RECEIPT:
						for(PaymentHeaderDO objPaymentDO : arrPaymentDO)
						//printerUtils.printPaymentDetail(mallsDetails, objPaymentDO, arrInvoiceNumbers, strReceiptNo,str);
					break;
					
					case PRINT_INVENTORY:
						printerUtils.printStockInventorySummary(vecOrdProduct,CALLFROM,"");
						break;
						
					case PRINT_NON_SALEABLE_INVENTORY:
						printerUtils.printStockInventorySummary(vecNonSaleableProduct,CALLFROM,"");
						break;
						
					case PRINT_LOG_INVENTORY:
						printerUtils.printStockInventorySummary(vecOrdProduct,CALLFROM,selectedDate);
						break;
						
					case PRINT_VERIFY_INVENTOTY:
						printerUtils.printStockInventorySummary(vecOrdProduct,CALLFROM,"");
						break;
						
					case PRINT_FULL_VANSTOCK_INVENTORY:
						printerUtils.printFullVanStockInventorySummary(vecsFullVanProduct);
						break;
						
					case PAYMENT_SUMMARY:
						isVertical = true;
						printerUtils.printPaymentSummary(vecCollection,fromDate,toDate);
					break;
					
					case PRINT_LOG_REPORT:
						printerUtils.printLogReportSummary(trxLogHeaders,trxMonthDetails, fromDate, toDate,isMTD);
						break;
						
					case PRINT_PENDING_INVOICE:
						printerUtils.printPendingPaymentSummary(arrInvoiceNumbers,mallsDetails);
					break;
					
					case PRINT_SCORE_CARD:
						printerUtils.printSalesmanKBI(vecObjects,toDate);
					break;
					
					case PRINT_CUSTOMER_STATEMENT:
						printerUtils.printCustomerStatement(mallsDetails,vecDetails,fromDate,toDate);
					break;
					
					case PRINT_CUSTOMER_PAYMENT_SUMMARY:
//						printdata=printerUtils.printPaymentSummaryOfCustomer(mallsDetails,new ArrayList<String>(),"","");
					break;
					
					case PRINT_ORDER_SUMMARY:
//						printdata=printerUtils.printCustomerStatement(mallsDetails);
					break;
					
					case PRINT_SAMPLE:
						printdata="Anil jain";
						break;*/
						
					default:
						printdata ="";
					break;
				}
				Log.e("print", printdata);
			
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
}
