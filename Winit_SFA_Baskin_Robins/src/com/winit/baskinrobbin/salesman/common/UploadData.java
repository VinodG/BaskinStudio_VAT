package com.winit.baskinrobbin.salesman.common;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.winit.baskinrobbin.parsers.AssetStatusListParser;
import com.winit.baskinrobbin.parsers.BooleanParser;
import com.winit.baskinrobbin.parsers.GetAppActiveParser;
import com.winit.baskinrobbin.parsers.ImageUploadParser;
import com.winit.baskinrobbin.parsers.InsertOrdersParser;
import com.winit.baskinrobbin.parsers.InsertOrdersParserAdvance;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TransferInOutDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.AdvanceOrderDO;
import com.winit.baskinrobbin.salesman.dataobject.AssetDo_New;
import com.winit.baskinrobbin.salesman.dataobject.CustomerVisitDO;
import com.winit.baskinrobbin.salesman.dataobject.DamageImageDO;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.JouneyStartDO;
import com.winit.baskinrobbin.salesman.dataobject.LogDO;
import com.winit.baskinrobbin.salesman.dataobject.MallsDetails;
import com.winit.baskinrobbin.salesman.dataobject.MyActivityDO;
import com.winit.baskinrobbin.salesman.dataobject.NameIDDo;
import com.winit.baskinrobbin.salesman.dataobject.PostReasonDO;
import com.winit.baskinrobbin.salesman.dataobject.TransferInoutDO;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UploadData extends IntentService 
{
	public final int STARTED    = 0;
	public static final int END = 1;
	private Preference preference;
	private CommonDA commonDA;
	private int TYPE = 0, UPLOAD_DATA = 0;
	public static boolean isRunning = false;
	private static UploadDataListener uploadDataListener;
	
	public UploadData() 
	{
		super("UploadData");
	}
	
	public static void setListener(UploadDataListener listener)
	{
		uploadDataListener = listener;
	}
	
	private void uploadAll()
	{
		uploadLoadRequest();
		uploadUnLoadRequest();
		uploadCustomer();
		
		updateUploadedCustomer();
		uploadOrders();
		uploadAdvanceOrder();
		
		uploadPayments();
		
		postJournyLogOnSever(preference.getStringFromPreference(Preference.EMP_NO, ""));
		
//		uploadTransferInOut(); // Commented Because there is no table tblTransferInOut
		
//		uploadCheckINDemandInventory();
		uploadSkipReason();
		uploadJourneyStart();
		uploadMyActivities();
		
		uploadCustomerVisit();
		
		uplaodPasscode();
//		uploadInventory(); // Commented Because there is no table tblInventory 
		
		completeOnHoldOrder();
		
		uploadLPOOrders();
		
		uploadAsset();
		
		updateLpoOrder();
		uploadLog();
		postOrderImages();
		postOrderSalesImages();
		postPaymentImages();
	}

	private void uploadLog() {
			final Vector<LogDO> vecLogs  		= 	new JourneyPlanDA().getAllLogs();
			boolean isUpload=false;
	        if(vecLogs != null && vecLogs.size() > 0)
			{
	        	
	        	BooleanParser booleanParser = new BooleanParser(UploadData.this);
				new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.getAllLogsXml(vecLogs), booleanParser, ServiceURLs.ShipStockMovementsFromXML,preference);
				if((Integer)booleanParser.getData()>0)
					isUpload=true;
				
				if(isUpload){
					for(LogDO logDO : vecLogs)
						new JourneyPlanDA().deleteLog(logDO.logId); 
				}
			}
	        
	}


//	private void uploadGRVImages()
//	{
//		returnOrderDA = new ReturnOrderDA();
//		arrAllDamagedImages = new ReturnOrderDA().getAllDamagedImages();
//		for (DamageImageDO damageImageDO : arrAllDamagedImages) {
//			boolean isError=false;
//			InputStream is = null;
//			try {
//				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost httppost = new HttpPost(String.format(
//						ServiceURLs.IMAGE_GLOBAL_URL_FULL,
//						DamageImageDO.getModule()));
//				File filePath = new File(damageImageDO.ImagePath);
//                String str="";
//				if (filePath.exists()) {
//					Log.e("uplaod", "called");
//					MultipartEntity mpEntity = new MultipartEntity();
//					ContentBody cbFile = new FileBody(filePath, "image/png");
//
//					mpEntity.addPart("FileName", cbFile);
//					httppost.setEntity(mpEntity);
//
//					HttpResponse response;
//					response = httpclient.execute(httppost);
//					HttpEntity resEntity = response.getEntity();
//					is = resEntity.getContent();
//
//				}
//				String serverUrl = parseImageUploadResponse(UploadData.this, is);
//				damageImageDO.status=DamageImageDO.getImageUploadStatus();
//				returnOrderDA.updateDamagedImageStatus(damageImageDO, serverUrl);
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//				isError=true;
//			} catch (IOException e) {
//				e.printStackTrace();
//				isError=true;
//			} catch (Exception e) {
//				e.printStackTrace();
//				isError=true;
//			}finally{
//				if(isError){
//					damageImageDO.status=DamageImageDO.getError();
//					returnOrderDA.updateDamagedImageStatus(damageImageDO, damageImageDO.ImagePath);
//				}
//			}
//
//		}
//	}
	public static String parseImageUploadResponse(Context context,
												  InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ImageUploadParser handler = new ImageUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public void updateUploadedCustomer()
	{
		ArrayList<NameIDDo> arrayList = new CommonDA().getUnpostedCustomerId();
		if(arrayList != null && arrayList.size() > 0)
			new CommonDA().updateCreatedCustomers(arrayList);
	}
	
	public boolean isNetworkConnectionAvailable() 
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) UploadData.this.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		
		return isNetworkConnectionAvailable;
	}
	
	private void uploadTransferInOut()
	{
		TransferInOutDA transferInOutDA = new TransferInOutDA();
		Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
		vecTransferInoutDOs = transferInOutDA.getUnuploadedTransferData();
		if(vecTransferInoutDOs != null && vecTransferInoutDOs.size() > 0)
		{
			if(new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.PostTransferOuts(vecTransferInoutDOs), ServiceURLs.PostTransferOuts, preference))
				transferInOutDA.updateTransferInOUTStatusNew(vecTransferInoutDOs, "Y");
		}
	}
	
//	private void uploadCheckINDemandInventory() 
//	{
//		Vector<CheckInDemandInventoryDO> vector = commonDA.getCheckINDemandInventory();
//		if(vector != null && vector.size() > 0)
//		{
//			ConnectionHelper connectionHelper 	  = new ConnectionHelper(null);
//			boolean isUploaded = connectionHelper.sendRequest(UploadData.this,BuildXMLRequest.insertCheckInDemandStock(vector), ServiceURLs.ImportCheckInDemandStock, preference);
//		
//			if(isUploaded)
//				commonDA.UpdateDemandInventory(vector, 1);
//		}
//	}
	
	public boolean uploadOrders()
	{
		sendUpdates(STARTED, "Uploading orders...");
		
		boolean isUploaded = new GetOrdersToUpload(UploadData.this, preference, UPLOAD_DATA).uploadOrders(preference.getStringFromPreference(Preference.EMP_NO, ""));
        return true;
	}
	
	public boolean uploadLPOOrders()
	{
		sendUpdates(STARTED, "Uploading lpo orders...");
		
		boolean isUploaded = new GetLPOOrdersToUpload(UploadData.this, preference, UPLOAD_DATA).uploadOrders(preference.getStringFromPreference(Preference.EMP_NO, ""));
        return true;
	}
	
	public boolean updateLpoOrder()
	{
		sendUpdates(STARTED, "Updating lpo orders...");
		
		boolean isUploaded = new GetLPOOrdersToUpdate(UploadData.this, preference, UPLOAD_DATA).uploadOrders(preference.getStringFromPreference(Preference.EMP_NO, ""));
		if(isUploaded)
			uploadPayments();
        return true;
	}
	
	
	public boolean completeOnHoldOrder()
	{
		sendUpdates(STARTED, "Uploading on-hold orders...");
		boolean isUploaded = new CompleteOnHoldOrder(UploadData.this, preference, UPLOAD_DATA).uploadOrders(preference.getStringFromPreference(Preference.EMP_NO, ""));
        return true;
	}
	public boolean postOrderImages()
	{
		sendUpdates(STARTED, "Uploading on-hold orders...");
		boolean isUploaded = new CompleteOnHoldOrder(UploadData.this, preference, UPLOAD_DATA).uploadImagesOrders(preference.getStringFromPreference(Preference.EMP_NO, ""),"Order","Customer");
        return true;
	}
	public boolean postOrderSalesImages()
	{
		sendUpdates(STARTED, "Uploading on-hold orders...");
		boolean isUploaded = new CompleteOnHoldOrder(UploadData.this, preference, UPLOAD_DATA).uploadSalesmanImagesOrders(preference.getStringFromPreference(Preference.EMP_NO, ""),"Order","Salesman");
        return true;
	}
	public boolean postPaymentImages()
	{
		sendUpdates(STARTED, "Uploading on-hold orders...");
		boolean isUploaded = new CompleteOnHoldOrder(UploadData.this, preference, UPLOAD_DATA).uploadPaymetImagesOrders(preference.getStringFromPreference(Preference.EMP_NO, ""),"Payment","Customer");
        return true;
	}

	public boolean completeLPO()
	{
		sendUpdates(STARTED, "Uploading LPO orders...");
		final Vector<String> vecSalesOrders  	= 	commonDA.getLPOToDeliver(preference.getStringFromPreference(Preference.EMP_NO, ""));
		
		if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
			for (String string : vecSalesOrders)
			{
				final InsertOrdersParser completeOnHoldOrderParser 	= new InsertOrdersParser(UploadData.this);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(UploadData.this,BuildXMLRequest.completOnHoldOrder(string, CalendarUtils.getCurrentDateTime()), completeOnHoldOrderParser, ServiceURLs.CompleteOnHoldOrder, preference);
			}
		}
        return true;
	}
	
	private boolean uploadAdvanceOrder()
	{
		List<AdvanceOrderDO> vecSalesOrders 	= 	new Vector<AdvanceOrderDO>();
		vecSalesOrders  						= 	commonDA.getAllAdvanceOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
		
		if(vecSalesOrders.size() > 5)
		{
			vecSalesOrders = vecSalesOrders.subList(0, 5);
		}
		final InsertOrdersParserAdvance insertOrdersParser 	= new InsertOrdersParserAdvance(UploadData.this);
        if(vecSalesOrders != null && vecSalesOrders.size() > 0)
		{
        	ConnectionHelper connectionHelper = new ConnectionHelper(null);
        	connectionHelper.sendRequest_Bulk(UploadData.this,BuildXMLRequest.UpdateDeliveryStatus(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.UpdateDeliveryStatus, preference);
        }
        return true;
	}
	
	public boolean uploadPayments()
	{
		sendUpdates(STARTED, "Uploading payments...");
		String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
		boolean isPaymentsUploaded = new GetPaymentsToUpload(UploadData.this, preference, TYPE).uploadPayments(empNo);
        return isPaymentsUploaded;
	}
	
	public boolean uploadCustomer()
	{
//		sendUpdates(STARTED, "Uploading customers...");
//		Vector<NewCustomerDO> vector = new CommonDA().getNewCustomerToUpload();
//		final InsertCustomerParser insertCustomerParser = new InsertCustomerParser(UploadData.this);
//        final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
//        if(vector !=null && vector.size() > 0)
//        {
//        	String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
//			connectionHelper.sendRequest_Bulk(UploadData.this,BuildXMLRequest.insertHHCustomer(vector, route_code), insertCustomerParser, ServiceURLs.INSERTHH_CUSTOMER_OFFLINE, preference);
//        }
        return true;
	}
	
	
	public boolean uploadAsset()
	{
		final Vector<AssetDo_New> vecAssets  	= 	commonDA.getAllAssetsToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
		
		if(vecAssets != null && vecAssets.size() > 0)
		{
			for (AssetDo_New assetDo_New : vecAssets)
			{
				final AssetStatusListParser assetStatusListParser 	= new AssetStatusListParser(UploadData.this);
				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				connectionHelper.sendRequest_Bulk(UploadData.this,BuildXMLRequest.postAssetCategory(assetDo_New), assetStatusListParser, ServiceURLs.PostAssetCategory, preference);
			}
		}
        return true;
	}
	
	public boolean uploadStock()
	{
		ArrayList<DeliveryAgentOrderDetailDco> arrayList = new VehicleDA().getAllItemToUpload(CalendarUtils.getOrderPostDate());
        final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
        if(arrayList !=null && arrayList.size() > 0)
        {
        	boolean isUploaded = connectionHelper.sendRequest(UploadData.this,BuildXMLRequest.insertStock(preference.getStringFromPreference(Preference.EMP_NO, ""),arrayList), ServiceURLs.InsertStock, preference);
        	if(isUploaded)
        		new VehicleDA().updateVMInventoryFromService();
        }
        return true;
	}
	
	public void postJournyLogOnSever(final String salesmanCode)
	{
		sendUpdates(STARTED, "Uploading journey log...");
		ArrayList<MallsDetails> vecJournyLog  =		new CustomerDetailsDA().getJournyLog(salesmanCode,CalendarUtils.getOrderPostDate());
		if(vecJournyLog != null && vecJournyLog.size() > 0)
		{
			 final ConnectionHelper connectionHelper = new ConnectionHelper(null);
			 boolean isSubmitted = connectionHelper.sendRequest(UploadData.this,BuildXMLRequest.insertCustomerVisit(vecJournyLog,preference.getStringFromPreference(Preference.EMP_NO, "")), ServiceURLs.InsertCustomerVisit, preference);
			 if(isSubmitted)
				 new CustomerDetailsDA().updateJourneyLogStatus();
		}
	}
	
	private void uploadLoadRequest() 
	{
		sendUpdates(STARTED, "Uploading movements...");
		final String strEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
		boolean isUploaded    = new GetMovementToUpload(UploadData.this, preference).getMovementToUpload(strEmpNo);
	}
	
	private void uploadUnLoadRequest() 
	{
		sendUpdates(STARTED, "Uploading movements...");
		final String strEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
		boolean isUploaded    = new GetMovementToUpload(UploadData.this, preference).getMovementToUploadUnload(strEmpNo);
	}
//	private void uploadLPOOrder() 
//	{
//		sendUpdates(STARTED, "Uploading LPO...");
//		ArrayList<LoadRequestDO> vecLoad = new InventoryDA().getAllLPOOrder();
//		
//		if(vecLoad != null && vecLoad.size() > 0)
//		{
//			for (LoadRequestDO loadRequestDO : vecLoad) 
//			{
//				final CreateLPOParser insertLoadParser 	= new CreateLPOParser(UploadData.this);
//				new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.uploadLPOOrderRequests(loadRequestDO), insertLoadParser, ServiceURLs.PostStockMovements, preference);
//			}
//		}
//	}
	
	private void uploadSkipReason()
	{
		ArrayList<PostReasonDO> vecArrayList = new CommonDA().getSkipReasonsToPost();

		if(vecArrayList != null && vecArrayList.size() > 0)
		{
			if(new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.postReasons(vecArrayList), ServiceURLs.InsertSkippingReason, preference))
			{
				new CommonDA().updateSkipReasonNew(vecArrayList, CalendarUtils.getCurrentDateAsString());
			}
		}
	}
	
	public void uploadJourneyStart()
	{
		final Vector<JouneyStartDO> vecJourneyStart = new JourneyPlanDA().getJourneyStart();
		
		boolean isSuccess = true, isSuccessDriver = true;
		for(JouneyStartDO journey : vecJourneyStart)
		{ 
			if(journey.StoreKeeperSignatureStartDay != null) 
			{
				if(new File(journey.StoreKeeperSignatureStartDay).exists())
				{
					String server_path = new UploadImage().uploadImage(UploadData.this, journey.StoreKeeperSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
					if(server_path != null && server_path.length() > 0)
					{
						journey.StoreKeeperSignatureStartDay = server_path;
						new JourneyPlanDA().updateJourneyStartSignature(AppStatus.STORE_SIGN_START, server_path, journey.journeyAppId);
					}
					else
						isSuccess = false;
				}
				if(new File(journey.StoreKeeperSignatureEndDay).exists())
				{
					String server_path = new UploadImage().uploadImage(UploadData.this, journey.StoreKeeperSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
					if(server_path != null && server_path.length() > 0)
					{
						journey.StoreKeeperSignatureEndDay = server_path;
						new JourneyPlanDA().updateJourneyStartSignature(AppStatus.STORE_SIGN_END, server_path, journey.journeyAppId);
					}
					else
						isSuccessDriver = true;
				}
				if(new File(journey.SalesmanSignatureStartDay).exists())
				{
					String server_path = new UploadImage().uploadImage(UploadData.this, journey.SalesmanSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
					if(server_path != null && server_path.length() > 0)
					{
						journey.SalesmanSignatureStartDay = server_path;
						new JourneyPlanDA().updateJourneyStartSignature(AppStatus.SALES_SIGN_START, server_path, journey.journeyAppId);
					}
					else
						isSuccessDriver = false;
				}
				if(new File(journey.SalesmanSignatureEndDay).exists())
				{
					String server_path = new UploadImage().uploadImage(UploadData.this, journey.SalesmanSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
					if(server_path != null && server_path.length() > 0)
					{
						journey.SalesmanSignatureEndDay = server_path;
						new JourneyPlanDA().updateJourneyStartSignature(AppStatus.SALES_SIGN_END, server_path, journey.journeyAppId);
					}
					else
						isSuccessDriver = true;
				}
			}
		}
		if(isSuccess && isSuccessDriver && vecJourneyStart != null && vecJourneyStart.size()>0)
		{
			if(new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.getStartJournyStart(vecJourneyStart), ServiceURLs.PostJourneyDetails, preference))
			{
				for(JouneyStartDO journey : vecJourneyStart)
					new JourneyPlanDA().updateJourneyStartUploadStatus(true, journey.journeyAppId); 
			}
		}
	}
	
	public void uploadCustomerVisit()
	{
		sendUpdates(STARTED, "Uploading customer visits...");
		Vector<CustomerVisitDO> vecCusotmerVisit = new CustomerDA().getCustomerVisit();
		if(vecCusotmerVisit.size()>0 && new ConnectionHelper(null).sendRequest(UploadData.this,BuildXMLRequest.getCustomerVisitXML(vecCusotmerVisit), ServiceURLs.PostClientVisits, preference))
		{
			for(CustomerVisitDO journey : vecCusotmerVisit)
				new CustomerDA().updateCustomerVisitUploadStatus(true, journey.CustomerVisitAppId); 
		}
	}

	private void uploadMyActivities()
	{ 
//		 Commented part is previous static survey..we are not using that now..so that part is commented.
		
		Vector<MyActivityDO> vecMyActivityDOs = new MyActivitiesDA().getAllUnUploadedActivities();
	
		if((vecMyActivityDOs != null && vecMyActivityDOs.size() > 0) /*|| (vecCustomerSurveyDOs != null && vecCustomerSurveyDOs.size() > 0)*/) 
		{
			final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
			boolean isPost = connectionHelper.sendRequest(UploadData.this,BuildXMLRequest.postTask(vecMyActivityDOs/*, vecCustomerSurveyDOs*/), ServiceURLs.InsertTaskOrder, preference);
		
			if(isPost)
				new MyActivitiesDA().updateActivities(vecMyActivityDOs);
		}
	}
	private void uplaodPasscode()
	{
		String passcode = new CommonDA().getUsedPasscode();
		if(passcode != null && passcode.length() > 0)
		{
			ConnectionHelper connectionHelper = new ConnectionHelper(null);
	        boolean isUpdated = connectionHelper.sendRequest(UploadData.this, BuildXMLRequest.updatePasscodeStatus(preference.getStringFromPreference(Preference.EMP_NO, ""), passcode),ServiceURLs.UPDATE_PASSCODE_STATUS, preference);
	        if(isUpdated)
	        	new CommonDA().deletePasscode(passcode);
		}
	}
	
//	public boolean uploadInventory()
//	{
//		Vector<InventoryDO> vecInventory 	= 	new Vector<InventoryDO>();
//		CommonDA commonDA 					= 	new CommonDA();
//		vecInventory  						= 	commonDA.getAllInventory(preference.getStringFromPreference(Preference.EMP_NO, ""));
//		final InsertInventoryParser insertInventoryParser 	= new InsertInventoryParser(UploadData.this);
//        if(vecInventory != null && vecInventory.size() > 0)
//		{
//        	ConnectionHelper connectionHelper = new ConnectionHelper(null);
//        	connectionHelper.sendRequest(UploadData.this, BuildXMLRequest.sendAllInventory(vecInventory), insertInventoryParser, ServiceURLs.INSERT_INVENTORY, preference);
//        }
//        return true;
//	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		sendUpdates(STARTED, "Uploading data...");
			
		TYPE 		= 	intent.getExtras().getInt("TYPE");
		UPLOAD_DATA = 	intent.getExtras().getInt(""+AppStatus.ALL_DATA_KEY, AppStatus.TODAY_DATA);
		
		preference 	= 	new Preference(this);
		commonDA 	= 	new CommonDA();
		isRunning 	= 	true;
		
		try 
		{
			if(isNetworkConnectionAvailable())
			{
				GetAppActiveParser getAppActiveParser 	= new GetAppActiveParser(this);
				new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.appActiveRequest(), getAppActiveParser, ServiceURLs.GetAppStatus,preference);
				String status = getAppActiveParser.getAppStatus();
				if(TextUtils.isEmpty(status) || status.equalsIgnoreCase("True")) {
					switch (TYPE) {
						case AppStatus.POST_ORDER:
							uploadOrders();
							completeOnHoldOrder();
							postOrderImages();
							postOrderSalesImages();
							break;

						case AppStatus.POST_RECIEPT:
							uploadPayments();
							postPaymentImages();

							break;

						case AppStatus.POST_LOAD_REQUEST:
							uploadLoadRequest();
							break;

						case AppStatus.POST_UNLOAD_REQUEST:
							uploadUnLoadRequest();
							break;

						case AppStatus.POST_LPO_ORDER:
							uploadLPOOrders();
							postOrderImages();
							postOrderSalesImages();
							break;

						case AppStatus.POST_CUSTOMER:
							uploadCustomer();
							break;

						case AppStatus.POST_JOURNEY_LOG:
							postJournyLogOnSever(preference.getStringFromPreference(Preference.EMP_NO, ""));
							break;

						case AppStatus.POST_CUSTOMER_VISIT:
							uploadCustomerVisit();
							break;

						case AppStatus.POST_COMPETET_ONHOLD_ORDER:
							completeOnHoldOrder();
							postOrderImages();
							postOrderSalesImages();
							break;

						case AppStatus.POST_ASSET_REQUEST:
							uploadAsset();
							break;
						case AppStatus.UPDATE_LPO_ORDER:
							updateLpoOrder();
							break;
						case AppStatus.POST_LOG:
							uploadLog();
							break;
						default:
							uploadAll();
							break;
					}
				}
				else{

				}
			}
		} 
		catch (Exception e)
		{
			sendUpdates(END, "Error");
			isRunning = false;
			e.printStackTrace();
		}
		isRunning = false;
		
		sendUpdates(END, "Completed...");
	}
	
	@Override
	public void onDestroy() 
	{
		isRunning = false;
		super.onDestroy();
		sendUpdates(END, "Completed...");
	}
	
	private void sendUpdates(int STATUS, String message)
	{
		try
		{
			if(uploadDataListener != null)
				uploadDataListener.updateStatus(STATUS, ""+message);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
