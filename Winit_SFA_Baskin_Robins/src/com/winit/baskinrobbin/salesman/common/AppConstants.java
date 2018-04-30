package com.winit.baskinrobbin.salesman.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;

import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ScanResultObject;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;

public class AppConstants 
{
    public static final int CHAR_IN_LINE = 126;
    public static int DIVICE_WIDTH, DIVICE_HEIGHT;
	public static String ACTION_SQLITE_FILE_DOWNLOAD	=	"com.winit.baskinrobbin.salesman.ACTION_SQLITE_FILE_DOWNLOAD";
	public static boolean isServeyCompleted = false;
	public static boolean isDeviceVerificationNeeded=false;
	
	public static String ACTION_LOAD_REFRESH			=	"com.winit.baskinrobbin.salesman.ACTION.LOAD_REFRESH";
	public static String ACTION_LOGOUT					=	"com.winit.baskinrobbin.salesman.ACTION.LOGOUT";
	public static String ACTION_HOUSE_LIST				=	"com.winit.baskinrobbin.salesman.ACTION_HOUSE_LIST_NEW";
	public static String ACTION_HOUSE_LIST_NEW			=	"com.winit.baskinrobbin.salesman.ACTION_HOUSE_LIST";
	public static String ACTION_GOTO_TELEORDERS			=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_TELEORDERS";
	public static String ACTION_GOTO_SETTINGS_FINISH	=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_SETTINGS_FINISH";
	public static String ACTION_GOTO_AR					=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_AR";
	public static String ACTION_GOTO_CRLMAIN			=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_CRLMAIN";
	public static String ACTION_GOTO_CRL				=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_CRL";
	public static String ACTION_FINISH_LIST				=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_HOME";
	public static String ACTION_GOTO_JOURNEY			=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_JOURNEY";
	public static String ACTION_GOTO_HOME1				=	"com.winit.baskinrobbin.salesman.ACTION_GOTO_HOME1";
	public static String ACTION_CASH_SALES				=	"com.winit.baskinrobbin.salesman.ACTION_CASH_SALES";
	
	public static final String TRX_KEY_SALES	= "INV";
	public static final String TRX_KEY_RETURN	= "CM";
	
	public static final String IBS_Sales_K	= "SALES";
	public static final String IBS_Sales_V	= "IBS Sales";
	
	public static final String IBS_Returns_K	= "RETURNS";
	public static final String IBS_Returns_V	= "IBS Returns";
	
	public static final String IBS_Replacement_Receipts_K	= "IBS Replacement Receipts";
	public static final String IBS_Replacement_Receipts_V	= "REPLACEMENT_RECEIPTS";
	
	public static final String IBS_Replacement_Issues_K	= "REPLACEMENT_RETURNS";
	public static final String IBS_Replacement_Issues_V	= "IBS Replacement Issues";
	
	public static final String Payment_Type	= "Credit";
	public static final String MO_PRINT	= "Move Order";
	public static final String PAYMENT_NOTE_CASH   = "CASH";
	public static final String PAYMENT_NOTE_CHEQUE = "CHEQUE";
	public static final String UPLOAD_DEBUG_TITILE = "Upload Debug";
	
	//======================Added For Return Order on 11th DEC 2017
	public final static String SETTINGS_LIMIT_RETURN_ORDER_DAYS = "LIMIT_RETURN_ORDER_DAYS";
	public final static String SETTINGS_LIMIT_RETURN_ORDER_INVOICE = "Return Order Inv Limit";

	public static long TIME_30_DAYS = -2592000000L;
//	public static String DeviceID = "358529060906571";
	public static final int CALL_FROM_LOGIN = 1;
	/*
	 * Version Management Constants
	 */
	public final static int MAJOR_APP_UPDATE = 102;
	public final static int NORMAL_APP_UPDATE = 101;
	public final static int MINOR_APP_UPDATE = 100;
	
	public final static int VER_CHANGED 	= 100;
	public final static int VER_NOT_CHANGED = 101;
	public final static int VER_NO_BUTTON_CLICK = 102;
	public final static int VER_DO_EOT = 103;
	public final static int VER_DO_EOT_ADEOT = 104;
	public final static int VER_DO_ADEOT = 105;
	public final static int VER_UNABLE_TO_UPGRADE = 106;
	
	
	public static String baskinLogoPath = "";
	public static String CategoryIconsPath;
	public static String productCatalogPath;
	public boolean isDayStarted = false;
	public static String DATABASE_PATH 	= "";//data/data/com.winit.baskinrobbin/
	public static String DATABASE_NAME 	= "salesman.sqlite";
	public static Typeface Helvetica_LT_57_Condensed,Helvetica_LT_Condensed_Bold;
	
	public static String JOURNEY_CALL 			= "Journey Call";
	
	public static String presellerOptionAll[]   = {"Journey Plan","AR Collection","Stock Inventory","Return Inventory"/*,"Add New Customer"*/,"Order Summary", "Payment Summary","Product Catalog"/*,"Competitor Survey"*/,"Today`s Dashboard",/*"Tasks",*//*"Product Catalog","Customer History",*/  "EOT",/*"Transfer In","Transfer Out","Free Delivery","Return Empty Jar",*/ "Free Delivery", "Settings","Asset Request","About Application","Logout","footer"};
	public static int presellerOptionLoogsAll[] = {R.drawable.journey_plan,R.drawable.arcollection,R.drawable.invontery, R.drawable.return_icon/*,R.drawable.register*/,R.drawable.order_summary,R.drawable.a4,R.drawable.catalog/*,R.drawable.copetitor_survey*/,R.drawable.order_summary,/*R.drawable.task,*//*R.drawable.catalog,R.drawable.customer_history,*/ R.drawable.eot,/*R.drawable.transfer_in,R.drawable.transfer_out,R.drawable.free_delivery,R.drawable.empty_jar,*/R.drawable.free_delivery, R.drawable.settings_menu,R.drawable.chiller, R.drawable.about,R.drawable.logout_menu,R.drawable.footer};

//	public static String presellerOptionAll[]   = {"Journey Plan","AR Collection","Stock Inventory","Return Inventory","Add New Customer","Order Summary", "Payment Summary","Product Catalog","Competitor Survey","Today`s Dashboard",/*"Tasks",*//*"Product Catalog","Customer History",*/  "EOT",/*"Transfer In","Transfer Out","Free Delivery","Return Empty Jar",*/ "Free Delivery", "Settings"/*,"Assets"*/,"Asset Request","About Application","Logout","footer"};
//	public static int presellerOptionLoogsAll[] = {R.drawable.journey_plan,R.drawable.arcollection,R.drawable.invontery, R.drawable.return_icon,R.drawable.register,R.drawable.order_summary,R.drawable.a4,R.drawable.catalog,R.drawable.copetitor_survey,R.drawable.order_summary,/*R.drawable.task,*//*R.drawable.catalog,R.drawable.customer_history,*/ R.drawable.eot,/*R.drawable.transfer_in,R.drawable.transfer_out,R.drawable.free_delivery,R.drawable.empty_jar,*/R.drawable.free_delivery, R.drawable.settings_menu,/*R.drawable.chiller,*/R.drawable.chiller,R.drawable.about,R.drawable.logout_menu,R.drawable.footer};
	
	
	public static String presellerOptionGT[]   = {"Journey Plan","Load Management","Sales Order","AR Collection","Stock Inventory", "Add New Customer","Order Summary", "Payment Summary","Product Catalog","Today`s Dashboard", "EOT", "Settings", "About Application","Logout","footer"};
	public static int presellerOptionLoogsGT[] = {R.drawable.journey_plan,R.drawable.invontery,R.drawable.sales_order,R.drawable.arcollection,R.drawable.invontery, R.drawable.register,R.drawable.order_summary,R.drawable.a4,R.drawable.catalog,R.drawable.order_summary, R.drawable.eot, R.drawable.settings_menu, R.drawable.about,R.drawable.logout_menu,R.drawable.footer};
	
	public static String presellerOptionPD[]   = {"Journey Plan"/*,"AR Collection"*/,"Stock Inventory","Return Inventory"/*,"Add New Customer"*/,"Order Summary"/*, "Payment Summary"*/,"Product Catalog"/*,"Competitor Survey"*/,"Today`s Dashboard",/*"Tasks",*//*"Product Catalog","Customer History",*/  "EOT",/*"Transfer In","Transfer Out","Free Delivery","Return Empty Jar",*/ "Free Delivery", "Settings","Asset Request", "About Application","Logout","footer"};
	public static int presellerOptionLogosPD[] = {R.drawable.journey_plan/*,R.drawable.arcollection*/,R.drawable.invontery, R.drawable.return_icon/*,R.drawable.register*/,R.drawable.order_summary/*,R.drawable.a4*/,R.drawable.catalog/*,R.drawable.copetitor_survey*/,R.drawable.order_summary,/*R.drawable.task,*//*R.drawable.catalog,R.drawable.customer_history,*/ R.drawable.eot,/*R.drawable.transfer_in,R.drawable.transfer_out,R.drawable.free_delivery,R.drawable.empty_jar,*/R.drawable.free_delivery, R.drawable.settings_menu,R.drawable.chiller, R.drawable.about,R.drawable.logout_menu,R.drawable.footer};

	
	public static ScanResultObject objScanResultObject;
    public static Vector<CategoryDO> vecCategories; 
	public static String RETURN_REASONS = "Return request reason";
    public static HashMap<String, CategoryDO> hmCateogories;
	public static String VendingMachineName = "";
    public static ProductDO objItem;
    public static boolean isRecomendedChanged = false;
    public static String SUB_CHANEL = "Grocery_123";
    
    //for Device Varification 
    public static final String SETTINGS_IS_DEVICE_VERIFICATION_NEEDED	=	"IsIMEIMandatory";
    
    public static boolean isTaskCompleted = false;
    
    public static final String SALESMAN_GT 	= "Salesman";
    public static final String SALESMAN_MT 	= "Modern Trade";
    public static final String SALESMAN_PD 	= "Parlour Delivery";
    public static final String SALESMAN_AM 	= "Asset Manager";
    public static final String PROMO_TYPE_RANGE 	= "RP";
    
    public static HashMap<String, Vector<ProductDO>> hmCapturedInventory;
    
    public static HashMap<String, Vector<ProductDO>> hmDiscout;
    
    public static HashMap<String, Vector<ProductDO>> hmCapturedInventoryAdvance;
    
    public static HashMap<String, Vector<ProductDO>> hmCapturedInventoryFreeDelivery;
//    
    public static String HHOrder  = "HH order";
    public static String APPORDER = "App order";
    public static String TELEORDER = "Tele order";
    public static String CURRENTORDER = "Current Order";
    public static String FREE_DELIVERY_ORDER = "Free Delivery";
	public static long TIME_FOR_BACKGROUND_TASK = 10*60000;
	public static String CATEGORY	=	"4G";
	public static String CURRENCY_CODE = "AED";
	 
	public static final String USER_TYPE_VANSALES = "";
	public static final String USER_TYPE_ADMIN = "Super Admin";
	
	public static boolean CheckIN;
	public static boolean isSumeryVisited;
	public static String SKIPPED_CUSTOMERS;
	public static ArrayList<String> skippedCustomerSitIds;
	public static final String Movement	=	"Movement";
	public static final String Customer	=	"Customer";
	public static final String Order	=	"Order";
	public static final String Receipt	=	"Receipt";
	public static final String Return	=	"Return";
	
	public static final String RETURNORDER = "HH Return Order";
	public static final String REJECTED = "Rejected Order";
	public static final String REPLACEMETORDER = "Replace Order";
	public static final String LPO_ORDER 	= "LPO Order";
	public static final String HOLD_ORDER 	= "HOLD Order";
	public static final String MOVE_ORDER 	= "MOVE Order";
	public static final String TRNS_TYPE_IN = "IN";
	public static final String TRNS_TYPE_OUT = "OUT";
	public static String SKIP_JOURNEY_PLAN = "Skip Customer";
	public static String SKIP_EbsLOad_SKIP = "Load Skip Reason";
	public static String RoundOffDecimals = "RoundOffDecimals";
	public static String SyncSeconds = "SyncSeconds";
	public static String EnableReturnQty = "EnableReturnQty";
	public static String EnablePrintUnits = "EnablePrintUnits";
	
	
	public static float DEVICE_DENSITY = 0;
	public static int DEVICE_WIDTH = 0;
	public static int DEVICE_HEIGHT = 0;
	
	public static String APPFOLDERNAME ="Baskin";
	public static String APPMEDIAFOLDERNAME = "BaskinImages";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	public static File   APPFOLDER = new File(APPFOLDERPATH);
	public static File   APPMEDIAFOLDER = new File(APPMEDIAFOLDERPATH);

	public static final int MAX_IMAGE_SIZE = 700;
	public static String imagePath = "";
	public static Bitmap assetbarcodeimagePath;
	public static Bitmap assettempimagePath;
	public static int DENISITY;
	public static String Task1 = "Capture real photo of the shelf to validate the accuracy of the plan";
	public static String Task2 = "Capture Competitor Promotions & Marketing initiatives";
	public static String Task3 = "Consumer behaviour survey for Frozen products under brand building strategy";
	
	public static String Task_Title1 = "Capture Shelf Photo";
	public static String Task_Title2 = "Competitor Promotions";
	public static String Task_Title3 = "In store - Consumer Behaviour Survey";
	
	/*public static final String APPROVED_MOVEMENT_STATUS = "Approved";
	public static final String COLLECTED_MOVEMENT_STATUS = "Collected";*/
	
	public static final String APPROVED_MOVEMENT_STATUS = "Approved from EBS";
	public static final String COLLECTED_MOVEMENT_STATUS = "Shipped";
//	public static final String ASSET_SYNCH_ENTITY = "AssetMaster";
	
	
	public static final String CUSTOMER_CHANNEL_MODERN  = "19.MODERN TRADE";
	public static final String CUSTOMER_CHANNEL_GENERAL = "19.GENERAL TRADE";
	public static final String CUSTOMER_CHANNEL_HORECA  = "19.HORECA";
	public static final String CUSTOMER_CHANNEL_PARLOUR = "19.DELIVERY SERVICE";
	
	public static final String CUSTOMER_CHANNEL_MODERN_NEW = "MODERN TRADE";
	
	
	public static final String CUSTOMER_TYPE_CREDIT = "CREDIT";
	public static final String CUSTOMER_TYPE_CASH 	= "CASH";
	
	public static final int MAXIMUM_DISATNCE_OUTLET = 2000;
	
	public static final String ACTION_NOTIFICATION  = "com.winit.baskinrobbin.salesman.NOTIFICATION";
	public static final String ERROR_NOTIFICATION   = "com.winit.baskinrobbin.salesman.ERROR";
	public static int GCMRegistrationAttempts ;
	public static boolean IS_BR_NETWORK_REACHABLE ;
	public final static String SENDER_ID = "715019169923";
	public final static int MaximumGCMRegistrationAttempts = 3;
	
	public static final String CUSTOMER_SIGN = "customer";
	public static final String SALESMAN_SIGN = "salesman";
	
	public static final String ACCOUNT_COPY    = "ACCOUNT_COPY";
	public static final String COLLECTION_COPY = "COLLECTION_COPY";
	public static final String CUSTOMER_COPY   = "Customer Copy";
	public static final String DUPLICATE_COPY  = "Duplicate Copy";
	
	public static final String GICC_DXB_Cheque  = "Cheque";
	public static final String GICC_DXB_PDC 	= "PDC";
	
	//Hard coded as per the Baskin logic given by the Thaiyab 
	public static final String TUB 			= 	"TUB";
	public static final String ITEM_CODE	= 	"110010001";
	
	
	public static final String RETURN_INV 			= 	"Credit Note";
	public static final String SALES_INV  			= 	"Sales Invoice";
	public static final String OPEN_CREDIT			= 	"OPEN_CREDIT";
	
	public static final String RETURN_INV_CODE 		= 	"R";
	public static final String SALES_INV_CODE 	 	= 	"I";
	public static final String OPEN_CREDIT_CODE		= 	"O";
	public static final String CREDIT_LEVEL_ACCOUNT	= 	"ACCOUNT";
	public static final String CREDIT_LEVEL_SITE   	= 	"SITE";
	public static final String PASSCODE			 	= 	"112233";
	
	public static final int ORDER_LIMIT 			= 	 5;
	public static final int SERVER_ERROR 			= 	 -100;
	//Settings Hard coded Names
	public static final String CREDIT_DAYS_WHILE_ORDER	= 	"CreditDaysWhileOrder";
	public static final String PASSCODE_CHECKOUT	 	= 	"CheckOutPasscode";
	public static final String PASSCODE_CUSTOMER_COPY 	= 	"CustomerCopyPasscode";
	public static final String PASSCODE_GRV			 	= 	"GRVPasscode";
	public static final String IS_REPLACE_ENABLE	 	= 	"IsReplacementEnable";
	public static final String PASSCODE_REPLACEMENT	 	= 	"ReplacementPasscode";
	public static final String PASSCODE_SKIP_CUSTOMER 	= 	"SkipCustomerPasscode";
	public static final String BASKIN_ADDRESS	 		= 	"BaskinAddress";
	public static final String BASKIN_FACEBOOK	 		= 	"BaskinFacebook";
	public static final String BASKIN_TWITTER	 		= 	"BaskinTwitter";
	
	public static final String NON_SELLABLE	 			= 	"NON-SELLABLE";
	public static final String SELLABLE	 				= 	"SELLABLE";
	public static final int SYNC_COUNT = 400;
	public static final String SAME_REPLACE = "Replace Same Item";
	// newly added for DOTmatrix printer
//	public static final int MAX_PRINT_LENGTH = 63;
	public static final int MAX_PRINT_LENGTH = 63;
	public static final int PAGE_HEIGHT = 70;
//	public static final int PRINTABLE_PAGE_HEIGHT = 60;
	public static final int PRINTABLE_PAGE_HEIGHT = 59;
	public static final int PAGE_GAP = 10;

	public static final String langEnglish = "English";
	public static final String langArabic = "Arabic";
	public static String jsonCmdAttribStr="";
	public static String base64LogoPng="";
	public static final int TRX_TYPE_RETURN_ORDER   	= 4;
	public static final int TRX_TYPE_MISSED_ORDER   	= 5;
	//	public final static String[] strAllFilter={"Today","week","Last 4 weeks"};
	public final static String[] strAllFilter={"Today","week","Month"};
	public static final int PrintAll = 3;
	public static final int Print_All_ExceptFoc = 1;
	public static final int Print_FOC = 2;

	public static final int PRINT_TYPE_WITH_PRICE = 1;
	public static final int PRINT_TYPE_WITHOUT_PRICE = 2;

	public static final String NUMBER_OF_DECIMALS = "NUMBER_OF_DECIMALS";
	public static int NUMBER_OF_DECIMALS_TO_ROUND		=	2;
	public static int PDC_DATE_CONSTANT=1;
	public static final int WOOSIM=1;
	public static final int DOTMATRIX=2;
	public static final int CANCEL=3;
}
