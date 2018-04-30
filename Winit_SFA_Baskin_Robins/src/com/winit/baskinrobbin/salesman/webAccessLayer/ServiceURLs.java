package com.winit.baskinrobbin.salesman.webAccessLayer;


public class ServiceURLs 
{
	public static String URL_Live						=   "http://83.111.232.51/PRODSFAVAT/services.asmx";
	public static String URL_Test						=   "http://83.111.232.48/BRSFAT2/services.asmx";
	public static String MAIN_URL 	 	 				= 	"http://192.168.2.55/BRSFANEW/Services.asmx";
	public static String UPLOAD_DATABASE_URL 			= 	"";
	public static String IMAGE_MAIN_URL 				=	"";
//This is for internal wifi connection Don't delete this
	public static final String MAIN_LOCAL_URL 			= 	"http://192.168.2.55/BRSFANEW/Services.asmx";
	public static String IMAGE_LOCAL_URL				=	"http://192.168.2.55/BRSFANEW/";

	//*****************Test***********************************************
//	public static final String MAIN_GLOBAL_URL 			= 	"http://83.111.232.48/BRSFAT2/services.asmx";
//	public static String IMAGE_GLOBAL_URL 				=	"http://83.111.232.48/BRSFAT2/";




	//********************Live**************************************************************
	public static final String MAIN_GLOBAL_URL 			= 	"http://83.111.232.51/PRODSFAVAT/services.asmx";
	public static String IMAGE_GLOBAL_URL 				=	"http://83.111.232.51/PRODSFAVAT/";

	//********************Local Chandan**************************************************************
//	public static final String MAIN_GLOBAL_URL 			= 	"http://192.168.154.33/BaskinUserSetup/services.asmx";
//	public static String IMAGE_GLOBAL_URL 				=	"http://192.168.154.33/BaskinUserSetup/";




	///**************Local************************************************
//	public static final String MAIN_LOCAL_URL 			= 	"http://83.111.232.51/PRODSFAVAT/services.asmx";
//	public static final String IMAGE_LOCAL_URL 			= 	"http://83.111.232.51/PRODSFAVAT/";

	///****************this is use for app locking and unlocking please dont comment it (Vikash)******************/
	public static final String GETAPPSTATUS_URL			=	"http://dev.winitmobile.com/winitapps/services.asmx";
	/*************************************************************************************************/


	public static final String DEV_URL 	 	 			= 	"http://208.109.154.54/BaskinRobbinsUsersLogging/Services/BRUserLoggingService.asmx";
	public static final String SOAPAction 				=    "http://tempuri.org/";
	public static final String GetVersionDetails 	 = 	"GetVersionDetails";
	public static final String GetMasterDataFile	 	 =	"GetMasterDataFile";
	public static final String GET_DISCOUNTS	 		 =	"GetDiscounts";
	public static final String GET_CUSTOMER_SITE 		 = 	"GetCustomersByUserId";
	public static final String LOGIN_METHOD 			 = 	"CheckLogin";
	public static final String GET_MESSAGE 				 =	"getMessages";
	public static final String GET_ALL_ITEMS 			 =	"GetAllItems";
	public static final String GET_ALL_ROLES 			 =	"GetAllRoles";
	public static final String GET_ALL_USERS 			 =	"GetAllUsers";
	public static final String INSERT_NOTE 				 =	"InsertNote";
	public static final String CHANGE_PASSWORD 			 = 	"ChangePassword";
	public static final String INSERT_MESSAGE 			 = 	"InsertMessage";
	public static final String GET_PENDING_SALES_INVOICE =	"GetPendingSalesInvoice";
	public static final String InsertFreeItem 			 = 	"InsertFreeItem";
	public static final String CompleteOnHoldOrder		 = 	"CompleteOnHoldOrder";
	
	//Device Activation 
	public static final String GetUserDeviceStatus		 			 = 	"GetUserDeviceStatus";
	public static final String GetUserDeviceStatusByPasscode		 = 	"GetUserDeviceStatusByPasscode";
	
	
	public static final String ResetVanstock = 	"ResetVanstock"; 

	public static final String CompareERPVsMWStockByVehicleCodeResponse = 	"CompareERPVsMWStockByVehicleCodeResponse"; 
	public static final String PostTrxDetailsFromXMLWithAuth = 	"PostTrxDetailsFromXMLWithAuth"; 
	public static final String PostLPODetailsFromXMLWithAuth = 	"PostLPODetailsFromXMLWithAuth";
	public static final String PostFreeItemXMLWithAuth		 = 	"PostFreeItemXMLWithAuth";
	public static final String UpdateDeliveryStatus 		 = 	"UpdateDeliveryStatus";
	public static final String PostAssetCategory 		 	 = 	"PostAssetCategory";
	public static final String GetAllDeleteLogs 		 	 = 	"GetAllDeleteLogs";
	public static final String UpdateClientLocation  		 = 	"UpdateClientLocation";
	
	public static final String GetHoldOrderStatus			 = 	"GetHoldOrderStatus";
	
	public static final String GetAllTransfers 		    = 	"GetAllTransfers";
	public static final String GET_NOTES 				= 	"GetNotes";
	public static final String DELETE_NOTES 			=	"DeleteNotes"; 
	public static final String DELETE_MESSAGE			=	"DeleteMessages"; 
	public static final String GET_CUSTOMERS_BY_USER_ID	=	"GetCustomersByUserId";
	public static final String GET_TOP_SELLING			=	"GetTopSellingItems";
	
	public static final String INSERT_PAYMENT			=	"InsertPayment";
	
	public static final String INSERT_HH_RECEIPT_COUPON =	"InsertReciptOnlineCoupons";
	
	public static final String GET_ALL_AVAILABLE_PASSCODE =	"GetAllAvailablePassCode";
	public static final String GetAllDataSync	= "GetAllDataSync";
	public static final String GET_ALL_LOCATIONS		=	"GetAllLocations";
	public static final String DEFAULT_SYNC_TIME		=	"1900-01-01T00:00:00";
	public static final String OTHERS 					= 	"OTHERS";
	public static final String GET_ALL_REASONS 			=	"GetAllReasons";
	public static final String INSERT_EOT				=	"InsertEOT";
	public static final String GETPASSCODE				=	"GetPassCode";
	public static final String GET_ALL_CATEGORY		    =	"GetAllCategory";
	public static final String GET_ALL_DELETED_ITEMS    =   "GetAllHHDeletedItems";
	public static final String GET_ALL_PASS_CODE        =   "GetAllPassCode";
	public static final String GET_CUSTOMER_HISTORY	    =   "getCustomerHistory";
	public static final String GET_CUSTOMER_HISTORY_WITH_SYNC = "getCustomerHistorywithSync";  
	public static final String UPDATE_PASSCODE_STATUS   =   "UpdateDAPassCodeStatus";
	
	public static final String GET_SPLASH_SCREEN_DATA_FOR_SYNC 	= "GetHHSplashScreenDataforSync";
	public static final String GetHouseHoldMastersWithSync 		= "GetHouseHoldMastersWithSync";
	public static final String GetSalesmanLandmarkWithSync 		= "GetSalesmanLandmarkWithSync";
	public static final String GET_All_PRICE_WITH_SYNC		    = "GetAllHHPriceWithSync";
	public static final String INSERTHH_CUSTOMER_OFFLINE  = "InsertHHCustomerOffline";
	public static final String UpdateHHCustomer 		  = "UpdateHHCustomer";
	public static final String GET_ALL_VEHICLES 		  = "GetVehicles";
	public static final String GET_SEQUENCE_NO			  = "GetAvailableTrxNos";
	public static final String GET_HH_SUMMARY			  = "GetVMSummary";
	public static final String GET_HH_DELETED_CUSTOMERS	  = "GetAllHHCustomerDeletedItems";
	public static final String InsertStock	 			  = "InsertStock";
	public static final String ImportCheckInDemandStock	  = "ImportCheckInDemandStock";
	
	public static final String GetAdvanceOrderByEmpNo	  = "GetAdvanceOrderByEmpNo";
	public static final String UpdateReturnStock		  = "UpdateReturnStock";
	public static final String UpdateTransferInOrOutStock = "UpdateTransferInOrOutStock";
	public static final String PostTransferOuts 		  = "PostTransfers";
	public static final String InsertCustomerVisit  	  = "InsertCustomerVisit";
	public static final String GetCompetitorDetail		  = "GetCompetitorDetail";
	public static final String GetAllPromotions 	      = "GetAllPromotions";
	public static final String PostStockMovements 	      = "PostStockMovements";
	public static final String PostSurvey 	     		  = "PostSurvey";
	public static final String PostAssetServiceRequest 	  = "PostAssetServiceRequest";
	public static final String GetApprovedMovements	      = "GetApprovedMovements";
	public static final String InsertSkippingReason	      = "InsertSkippingReason";
	public static final String GetAllMovements_Sync		  = "GetAllMovements_Sync";
	public static final String GetAppActiveStatus		  = "GetAppActiveStatus";
	public static final String GetAppCorrectInProcessResponse		  = "GetAppCorrectInProcessResponse";
	public static final String GetAssetMasters 	      	  = "GetAssetMasters";
	public static final String PostAsset	 	      	  = "PostAsset";
	
	public static final String GetTrxHeaderForApp		  = "GetTrxHeaderForApp";
	public static final String GetJPAndRouteDetails		  = "GetJPAndRouteDetails";
	public static final String GetSurveyMasters 	      = "GetSurveyMasters";
	
	public static final String InsertVechileTracking	  = "InsertVechileTracking";
	public static final String PostJourneyDetails	 	  = "PostJourneyDetails";
	public static final String PostClientVisits	 	      = "PostClientVisits";
	public static final String InsertAssetCustomer	 	  = "InsertAssetCustomer";
	public static final String DELETE_RECEIPT_FROMAPP 	  = "DeleteReceipt";
	
	public static final String updateDeviceId 	  		  = "updateDeviceId";
	
	// Signature Upload Module
	public static final String stockverifiedsignature 	  = "stockverifiedsignature";
	public static final String eotsignature 	  		  = "eotsignature";
	public static final String assetservicerequest 	      = "assetservicerequest";
	public static final String ORDER_IMAGES 	      	  = "orderimage";
	public static final String INSERT_INVENTORY 		  = "ImportInventory";
	public static final String GET_DA_PASSCODE 			  = "GetAllAvailableDAPassCode";
	
	public static final String GetAllTask 		   		  = "GetAllTask";
	public static final String InsertTaskOrder 			  = "InsertTaskOrder";
	public static final String GetAllAcknowledgedTask 	  = "GetAllAcknowledgedTask";
	public static final String InsertCustomerGeoCode 	  = "InsertCustomerGeoCode";
	
	// SQLite Upload Module
	public static final String UploadSQLiteFromSettings   = "UploadSQLiteFromSettings";
	public static final String UploadDeliveryLogFromSettings   = "UploadDeliveryLogFromSettings";
	public static final String UploadVanstockLogFromSettings   = "UploadVanstockLogFromSettings";
	public static final String UploadSQLiteAfterEOT 	  = "UploadSQLiteAfterEOT";

	public static final String GetClearDataPermission 	  = "GetClearDataPermission";
	public static final String UpdateMTDeliveryStatus 	  = "UpdateMTDeliveryStatus";
	
	public static final String GetAppAccessStatus 		  = "GetAppAccessStatus";
	
	public static final String InsertApplicationInsatallation = "InsertApplicationInsatallation";
	public static final String getUserStatus		 		  = "getUserStatus";
	public static final String insertLoginAction			  = "insertLoginAction";
//newly Added By Vikash for app lock
	public static final String GetAppStatus 			 = 	"GetAppStatus";
	//=============method names==================================================================//
	public static final String Hello = "Hello";
	public static final String ShipStockMovementsFromXML = "ShipStockMovementsFromXML";
	public static final String GetVanStockLogDetail = "GetVanStockLogDetail";
	public static final String GetPendingInvoiceDataByCode = "GetPendingInvoiceDataByCode";
	public static final String PostSignature = "PostSignature";

}
