package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.Base64;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.port.PrinterConnectorArabic;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.dataobject.InventoryObject;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class VerifyReturnItemFromVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader ;
	private Button btnOrdersheetVerify,btnOrdersheetReport, btnPrint, btnFinish;
	private OrderSheetAdapter ordersheetadapter;
	private ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
	private TextView tvItemCode, tvCases, tvUnits, tvOrdersheetHeader, tvNoItemFound, tvItemList;
	private ListView lvItemList ; 
	private ArrayList<Integer> isClicked;
	private ArrayList<Integer> isClickedReturn;
	private ImageView ivCheckAllItems;
	private MyView myViewManager; 
	private Paint mPaint;
	private ListView lvInventoryItems;
	private TextView tvUnitsReturn, tvCasesReturn, tvItemCodeReturn, tvItemListReturn, tvOrdersheetHeaderReturn;
	private LinearLayout llItemHeaderReturn, llReturnInventory, llUnloadStock;
	private ImageView ivCheckAllItemsReturn;
	private CustomeListAdapter customeListAdapter;
	private Vector<InventoryObject> vecInventoryItems;
	private boolean isVarifcationSignatureDone;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{ 
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.item_list_to_verify_new, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		
		setTypeFace(llOrderSheet);
		
		isClicked		=	new ArrayList<Integer>();
		isClickedReturn =	new ArrayList<Integer>();
		preference.saveBooleanInPreference("invoiceprinted", false);
		preference.commitPreference();
		
		lvItemList.setVerticalScrollBarEnabled(false);
		lvItemList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvItemList.setCacheColorHint(0);
		lvItemList.setFadingEdgeLength(0);
		lvItemList.setAdapter(ordersheetadapter = new OrderSheetAdapter(new ArrayList<DeliveryAgentOrderDetailDco>()));
		llOrderListView.addView(lvItemList, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		tvOrdersheetHeader.setText("Unload Stock Verification");
		tvOrdersheetHeaderReturn.setText("Returned Inventory");
		
		lvInventoryItems.setAdapter(customeListAdapter = new CustomeListAdapter(new Vector<InventoryObject>()));
		
		if(getIntent().getExtras()!=null)
		{           
			vecOrdProduct = (ArrayList<DeliveryAgentOrderDetailDco>)getIntent().getExtras().get("updatededDate");
		}
		//showing Loader
		showLoader(getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				vecInventoryItems = new OrderDetailsDA().getReturnInventoryQty(CalendarUtils.getOrderPostDate(), AppConstants.RETURNORDER);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						ordersheetadapter.refresh(vecOrdProduct);
						if(vecInventoryItems != null && vecInventoryItems.size()>0)
						{
							if(vecOrdProduct == null || vecOrdProduct.size()< 0)
								llUnloadStock.setVisibility(View.GONE);
							customeListAdapter.refresh(vecInventoryItems);
						}
						else
						{
							llReturnInventory.setVisibility(View.GONE);
						}
						hideLoader();
					}
				});
			}
		}).start();
		
		ivCheckAllItems.setTag("unchecked");
		ivCheckAllItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("unchecked"))
				{
					v.setTag("checked");
					((ImageView)v).setImageResource(R.drawable.check_hover);
					isClicked.clear();
					for(int j = 0; j < vecOrdProduct.size() ; j++)
					{
						isClicked.add(j);
					}
					ordersheetadapter.refreshAll();
				}
				else
				{
					v.setTag("unchecked");
					((ImageView)v).setImageResource(R.drawable.check_normal);
					isClicked.clear();
					ordersheetadapter.refresh();
				}
			}
		});
		
		ivCheckAllItemsReturn.setTag("unchecked");
		ivCheckAllItemsReturn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("unchecked"))
				{
					v.setTag("checked");
					((ImageView)v).setImageResource(R.drawable.check_hover);
					isClickedReturn.clear();
					for(int j = 0; j < vecInventoryItems.size() ; j++)
					{
						isClickedReturn.add(j);
					}
					customeListAdapter.refreshAll();
				}
				else
				{
					v.setTag("unchecked");
					((ImageView)v).setImageResource(R.drawable.check_normal);
					isClickedReturn.clear();
					customeListAdapter.refresh();
				}
			}
		});
		
		//Button order sheet verify
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isAllVerified())
					showSignatureDialog();
				else
					showCustomDialog(VerifyReturnItemFromVehicle.this, "Alert !", "Please verify all items.", "OK", null, null, false);
			}
		});
		
		btnOrdersheetReport.setText("STOCK VERIFICATION");
		
		//Button order sheet Report
		btnOrdersheetReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				ShowOptionPopupForPrinter(VerifyReturnItemFromVehicle.this,new PrintPopup() {
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
								intent=new Intent(VerifyReturnItemFromVehicle.this, WoosimPrinterActivity.class);
							else if(selectedPrinter==AppConstants.DOTMATRIX)
								intent=new Intent(VerifyReturnItemFromVehicle.this, PrinterConnectorArabic.class);
							intent.putExtra("itemforVerification", vecOrdProduct);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_STOCK_UNLOAD);
							startActivityForResult(intent, 1000);
							hideLoader();
						}



					}
				});
//				Intent intent = new Intent(VerifyReturnItemFromVehicle.this, BluetoothFilePrinter.class);
//				Intent intent = new Intent(VerifyReturnItemFromVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_STOCK_UNLOAD);
//				startActivityForResult(intent, 1000);
			}
		});
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				setResult(1111);
				finish();
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(VerifyReturnItemFromVehicle.this, "Print functionality is in progress.", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(VerifyReturnItemFromVehicle.this, WoosimPrinterActivity.class);
				intent.putExtra("itemforVerification", vecOrdProduct);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_RETURN);
				startActivityForResult(intent, 1000);
			}
		});
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids 
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		tvCases					=	(TextView)llOrderSheet.findViewById(R.id.tvCases);
		tvUnits					=	(TextView)llOrderSheet.findViewById(R.id.tvUnits);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		
		btnPrint				=	(Button)llOrderSheet.findViewById(R.id.btnPrint);
		btnFinish				=	(Button)llOrderSheet.findViewById(R.id.btnFinish);
		
		
		lvInventoryItems		=	(ListView)llOrderSheet.findViewById(R.id.lvInventoryItems);
		
		tvUnitsReturn			=	(TextView)llOrderSheet.findViewById(R.id.tvUnitsReturn);
		tvCasesReturn			=	(TextView)llOrderSheet.findViewById(R.id.tvCasesReturn);
		tvItemCodeReturn		=	(TextView)llOrderSheet.findViewById(R.id.tvItemCodeReturn);
		tvItemListReturn		=	(TextView)llOrderSheet.findViewById(R.id.tvItemListReturn);
		tvOrdersheetHeaderReturn=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeaderReturn);
		
		llItemHeaderReturn		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeaderReturn);
		llReturnInventory		=	(LinearLayout)llOrderSheet.findViewById(R.id.llReturnInventory);
		llUnloadStock			=	(LinearLayout)llOrderSheet.findViewById(R.id.llUnloadStock);
		
		ivCheckAllItemsReturn	=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItemsReturn);
		
		//setting type-faces
		/*tvUnitsReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCasesReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemListReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOrdersheetHeaderReturn.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvOrdersheetHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetReport.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemList.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnFinish.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		lvItemList = new ListView(this);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        tvCases.setText("UOM");
        tvUnits.setText("Units");
	}
	
	public class OrderSheetAdapter extends BaseAdapter
	{
		ArrayList<DeliveryAgentOrderDetailDco> vecOrder;
		private boolean isRefresh = false;
		public OrderSheetAdapter(ArrayList<DeliveryAgentOrderDetailDco> vec)
		{
			this.vecOrder=vec;
		}
		public void refresh() 
		{
			notifyDataSetChanged();
		}
		public void refreshAll() 
		{
			isRefresh = true;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() 
		{
			return vecOrder.size();
		}
		@Override
		public Object getItem(int position) 
		{
			return position;
		}
		@Override
		public long getItemId(int position) 
		{
			return 0;
		}
		public void refresh(ArrayList<DeliveryAgentOrderDetailDco> vec)
		{
			this.vecOrder = vec;
			notifyDataSetChanged();
			if(vecOrder != null && vecOrder.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				llItemHeader.setVisibility(View.VISIBLE);
			}
			else
			{
				tvNoItemFound.setVisibility(View.VISIBLE);
				llItemHeader.setVisibility(View.GONE);
			}
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			DeliveryAgentOrderDetailDco ordPro = vecOrder.get(position);
			//inflate invoice_list_cell layout here
			if(convertView == null)
				convertView			     =	(LinearLayout)getLayoutInflater().inflate(R.layout.item_list_verify_cell,null);
			//getting id's here
			LinearLayout llCellClick	 =	(LinearLayout)convertView.findViewById(R.id.llCellClick);
			TextView tvProductKey		 =	(TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName		 =	(TextView)convertView.findViewById(R.id.tvVendorName);
			EditText etInvoice1			 =	(EditText)convertView.findViewById(R.id.etInvoice1);
			EditText etCases		 	 =	(EditText)convertView.findViewById(R.id.etCases);
			
			final ImageView ivAcceptCheck=	(ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
			
			tvProductKey.setText(ordPro.itemCode);
			
			tvVendorName.setText(""+ordPro.itemDescription);
			
			if(!isRefresh)
			{
				if(ordPro.preUnits != 0)
					etInvoice1.setText(""+diffStock.format(ordPro.preUnits));
				else
					etInvoice1.setText(""+0);
			}
			
			etCases.setText(""+ordPro.strUOM);
			ivAcceptCheck.setVisibility(View.VISIBLE);
			
			//Setting Type-faces here
			tvProductKey.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvVendorName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			etInvoice1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			etCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			//setting Enabled false to Edit Text
			
			if(isClicked != null && isClicked.contains(position))
				ivAcceptCheck.setImageResource(R.drawable.check_hover);
			else
				ivAcceptCheck.setImageResource(R.drawable.check_normal);
			
			etInvoice1.setEnabled(false);
			etInvoice1.setFocusable(false);
			etInvoice1.setFocusableInTouchMode(false);
			etCases.setEnabled(false);
			etCases.setFocusable(false);
			etCases.setFocusableInTouchMode(false);
			
			ivAcceptCheck.setTag(position);
			llCellClick.setTag(position);
			//Click event for llLayout
			llCellClick.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_hover);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_normal);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.check_hover);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.check_normal);
					}
				}
			});
			//Click event for ivAcceptCheck
			ivAcceptCheck.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClicked.contains(mPosition))
					{
						isClicked.add(mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_hover);
					}
					else
					{
						isClicked.remove((Integer)mPosition);
						ivAcceptCheck.setImageResource(R.drawable.check_normal);
					}
					if(isAllVerified())
					{
						ivCheckAllItems.setTag("checked");
						ivCheckAllItems.setImageResource(R.drawable.check_hover);
					}
					else
					{
						ivCheckAllItems.setTag("unchecked");
						ivCheckAllItems.setImageResource(R.drawable.check_normal);
					}
				}
			});
			
			return convertView;
		}
	}	
	
	private boolean isAllVerified()
	{
		if(isClicked.size() == vecOrdProduct.size())
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("verify"))
		{
			btnPrint.setVisibility(View.VISIBLE);
			btnFinish.setVisibility(View.VISIBLE);
			btnOrdersheetVerify.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard.isShown())
			TopBarMenuClick();
		else if(btnOrdersheetVerify.getVisibility() == View.VISIBLE)
			btnOrdersheetVerify.performClick();
		else
			btnFinish.performClick();
	}
	
	private void showSignatureDialog()
	{
		final Dialog dialog 			= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor, null);
		LinearLayout llSignSupervisor 	= (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnClear 				= (Button)llSignature.findViewById(R.id.btnClear);
		
		dialog.addContentView(llSignature,new LayoutParams(LayoutParams.FILL_PARENT, (int)(320 * px)));
		dialog.show();
		
		myViewManager  = new MyView(this);
		myViewManager.setDrawingCacheEnabled(true);
		myViewManager.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , (int)(300 * px)));
		myViewManager.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignSupervisor.addView(myViewManager);
		isVarifcationSignatureDone = false;
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isVarifcationSignatureDone)
				{
					showLoader("Loading...");
					
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							new VehicleDA().updateInventoryUnload(vecOrdProduct);
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									dialog.dismiss();
									showCustomDialog(VerifyReturnItemFromVehicle.this, getString(R.string.verified), getString(R.string.return_stock_verified_in_the_van_as_per_the_order), getString(R.string.OK), null, "verify");
									uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);		
									hideLoader();
								}
							});
						}
					}).start();
					
					
				}
				else
					showCustomDialog(VerifyReturnItemFromVehicle.this, getResources().getString(R.string.warning), "Please take warehouse manager's signature.", "OK", null, "");
			}
		});
		
		btnClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isVarifcationSignatureDone = false;
				if(myViewManager != null)
					myViewManager.clearCanvas();
			}
		});
	}
	
	public String bitMapToString(Bitmap bitmap)
	{
		String temp = "";
		if(bitmap != null)
		{
			 ByteArrayOutputStream baos=new  ByteArrayOutputStream();
	         bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
	         byte [] b		=	baos.toByteArray();
	         temp			=	Base64.encodeToString(b, Base64.DEFAULT);
		}
		return temp;
    }
	
	public class MyView extends View 
	{
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        int width = 480, height = 800;
        
        public MyView(Context c)
        {
            super(c);
            Display display = 	getWindowManager().getDefaultDisplay(); 
            int width 		= 	display.getWidth();
            int height 		= 	display.getHeight();
            if(mBitmap != null)
            	mBitmap.recycle();
            
            mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas 		= 	new Canvas(mBitmap);
            mPath 			= 	new Path();
            mBitmapPaint	= 	new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(2);
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
        	isVarifcationSignatureDone = true;
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) 
        {
           isVarifcationSignatureDone = true;
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
	     }
        
	    
	    public void clearCanvas()
	    {
	    	mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
	        mCanvas 		= 	new Canvas(mBitmap);
	    	invalidate();
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
	
	public class CustomeListAdapter extends BaseAdapter
	{
		
		Vector<InventoryObject> vecInventoryItems;
		public CustomeListAdapter(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
		}
		@Override
		public int getCount() 
		{
			return vecInventoryItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecInventoryItems.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		public void refresh() 
		{
			notifyDataSetChanged();
		}
		public void refreshAll() 
		{
			notifyDataSetChanged();
		}
		
		private boolean isAllVerified()
		{
			if(isClickedReturn.size() == vecInventoryItems.size())
			{
				return true;
			}
			return false;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			InventoryObject inventoryObject = vecInventoryItems.get(position);
			if(convertView == null)
				convertView = inflater.inflate(R.layout.inventory_qty_cell, null);
			
			TextView tvItemCodeText = (TextView)convertView.findViewById(R.id.tvItemCodeText);
			TextView tvDescription 	= (TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvTotalQty 	= (TextView)convertView.findViewById(R.id.tvTotalQty);
			TextView tvDeliveredQty = (TextView)convertView.findViewById(R.id.tvDeliveredQty);
			TextView tvAvailQty 	= (TextView)convertView.findViewById(R.id.tvAvailQty);
			TextView tvUOM 			= (TextView)convertView.findViewById(R.id.tvUOM);
			final ImageView ivCheckItems	= (ImageView)convertView.findViewById(R.id.ivCheckItems);
			tvItemCodeText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvUOM.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvTotalQty.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			ivCheckItems.setVisibility(View.VISIBLE);
			tvDeliveredQty.setVisibility(View.GONE);
			tvAvailQty.setVisibility(View.GONE);
			
			if(isClickedReturn != null && isClickedReturn.contains(position))
				ivCheckItems.setImageResource(R.drawable.check_hover);
			else
				ivCheckItems.setImageResource(R.drawable.check_normal);
			tvItemCodeText.setText(inventoryObject.itemCode);
			tvDescription.setText(inventoryObject.itemDescription);
			tvTotalQty.setText(""+(inventoryObject.PrimaryQuantity >= 0 ? inventoryObject.PrimaryQuantity : 0));
			tvUOM.setText(""+inventoryObject.UOM);
			
			
			ivCheckItems.setTag(position);
			convertView.setTag(position);
			//Click event for llLayout
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					ivCheckItems.performClick();
				}
			});
			//Click event for ivAcceptCheck
			ivCheckItems.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int mPosition = StringUtils.getInt(v.getTag().toString());
					if(!isClickedReturn.contains(mPosition))
					{
						isClickedReturn.add(mPosition);
						ivCheckItems.setImageResource(R.drawable.check_hover);
					}
					else
					{
						isClickedReturn.remove((Integer)mPosition);
						ivCheckItems.setImageResource(R.drawable.check_normal);
					}
					if(isAllVerified())
					{
						ivCheckAllItemsReturn.setTag("checked");
						ivCheckAllItemsReturn.setImageResource(R.drawable.check_hover);
					}
					else
					{
						ivCheckAllItemsReturn.setTag("unchecked");
						ivCheckAllItemsReturn.setImageResource(R.drawable.check_normal);
					}
				}
			});
			
			return convertView;
		}
		public void refresh(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
			if(vecInventoryItems!=null && vecInventoryItems.size()>0)
			{
				lvInventoryItems.setVisibility(View.VISIBLE);
				notifyDataSetChanged();
			}
			else
			{
				lvInventoryItems.setVisibility(View.GONE);
			}
		}
	}
}
