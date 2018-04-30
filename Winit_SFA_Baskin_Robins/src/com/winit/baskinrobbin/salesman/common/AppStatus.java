package com.winit.baskinrobbin.salesman.common;


public class AppStatus 
{
	public static final int DEFAULT = 100;
	public static final int ONHOLD_ORDER = 1;
	public static final int MODIFY_ORDER = 2;
	public static int LOAD_STOCK   = 1;
	public static int UNLOAD_STOCK = 2;
	public static int UNLOAD_S_STOCK = 3;
	public static int UNLOAD_COLLECTED_STOCK = 10;

	public static int STORE_SIGN_START  = 1;
	public static int STORE_SIGN_END 	= 2;
	public static int SALES_SIGN_START  = 3;
	public static int SALES_SIGN_END 	= 4;
	public static int OLD_COIN_BOX_SCAN_REQUEST_CODE = 100000000;
	public static int NEW_COIN_BOX_SCAN_REQUEST_CODE = 200;
	public static int VM_PHOTOGRAPH_REQUEST_CODE = 300;
	public static int REQUEST_CODE = 0;
	public static int invoiceNo = 10;
    public static int customerSiteno = 10;
    public static final int ALL   = -1;
    public static final int POST_ORDER   = 1;
    public static final int POST_RECIEPT = 2;
    public static final int POST_LOAD_REQUEST = 3;
    public static final int POST_CUSTOMER = 4;
    public static final int POST_FREE_DELIVERY = 6;
    public static final int POST_RETURNSTOCK = 7;
    public static final int POST_JOURNEY_LOG = 8;
    public static final int POST_CUSTOMER_VISIT = 9;
    public static final int POST_COMPETET_ONHOLD_ORDER = 10;
    public static final int POST_LPO_ORDER = 11;
    public static final int POST_ASSET_REQUEST = 12;
    public static final int UPDATE_LPO_ORDER = 13;
    public static final int POST_UNLOAD_REQUEST = 14;
    public static final int POST_LOG = 15;
    
    public static final int SALES_ORDER_TYPE = 1, ADVANCE_ORDER_TYPE =0, REPLACEMENT_ORDER_TYPE = 2;
    public static int  AEDBelow2000 = 1;
	public static int  AEDBelow3000 = 2;
	public static int  AEDBelow1000 = 3;
	public static int  AEDAbove4000 = 4;
	public static int  AEDZero = 0;
	public static final int DISCOUNT_ALL_ITEM=0;
	public static final int DISCOUNT_ITEM=1;
	public static final int DISCOUNT_CATEGORY=2;
	public static final int DISCOUNT_BRAND=3;
	public static final int DISCOUNT_PERCENTAGE = 0;
	public static final int DISCOUNT_AMOUNT = 1;
	public static final int APPROVED_MOVE_CODE = 1;
	public static final String TRX_STATUS_DELIVRED  = "D";
	public static final String TRX_STATUS_HH_TYPE  = "HH order";
	public static final String TRX_STATUS_HOLD	    = "H";
	public static final String TRX_STATUS_REJECT	    = "R";
	public static final String TRX_STATUS_ADVANCE	= "E";
	public static final String TRX_STATUS_PND	    = "PND";
	public static final int PAYMENT_DONE 			= 10000;
	public static final int PAYMENT_CANCEL 			= 100;
	public static final String FOC_ITEM_TYPE 		= "FOC ITEM";
	public static final int ENABLE = 1, NOT_AVAIL = -1, TOTAL_PROGRESS = 100;
	public static final int ALL_DATA = 1, TODAY_DATA = 0;
	public static final String ALL_DATA_KEY = "ALL_DATA";
	public static final int STATUS_INSERTED = 0, STATUS_UPLOADED = 1, STATUS_UPDATED = 2;
	public static final int IMAGE_LIMIT     = 10;
	
	public static final int LPO_STATUS_CREATED      = -1;
	public static final int LPO_STATUS_APROOVED     = 0;
	public static final int LPO_STATUS_DELIVERED    = 1;
	public static final int LPO_STATUS_UPDATED	    = 2;
	
	public static final int SPLASH_SCREEN_TIME	    = 180000;
	
}
