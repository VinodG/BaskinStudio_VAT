/*package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CONSTANTOBJ;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.VanLoadDO;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
public class VerifyOnHandStock extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader ;
	private Button btnOrdersheetVerify,btnOrdersheetReport,btnPrint, btnContinue;
	private OrderSheetAdapter ordersheetadapter;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView tvItemCode, tvCases, tvUnits, tvOrdersheetHeader, tvNoItemFound, tvItemList,
	 				 tvUsername, tvDate, tvVehicleCode;
	private ListView lvItemList ; 
	private ArrayList<Integer> isClicked;
	private ImageView ivCheckAllItems;
	private MyView myViewManager;
	private MyView myViewDriver;
	private Paint mPaint;
	private boolean isMenu;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.item_list_to_verify_latest, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		if(getIntent().getExtras()!=null)
		{
			vecOrdProduct = (ArrayList<VanLoadDO>)getIntent().getExtras().get("updatededDate");
			isMenu = getIntent().getExtras().getBoolean("isMenu");
		}
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		
		isClicked		=	new ArrayList<Integer>();
		
		lvItemList.setVerticalScrollBarEnabled(false);
		lvItemList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvItemList.setCacheColorHint(0);
		lvItemList.setFadingEdgeLength(0);
		lvItemList.setAdapter(ordersheetadapter = new OrderSheetAdapter(new ArrayList<VanLoadDO>()));
		llOrderListView.addView(lvItemList, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		tvOrdersheetHeader.setText("Stock Verification");
		loadData();
		
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
		
		//Button order sheet verify
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				btnOrdersheetVerify.setClickable(false);
				btnOrdersheetVerify.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						btnOrdersheetVerify.setClickable(true);
						btnOrdersheetVerify.setEnabled(true);
					}
				}, 500);
				if(isAllVerified())
					showSignatureDialog();
				else
					showCustomDialog(VerifyOnHandStock.this, "Alert !", "Please select all items.", "OK", null, null);
			}
		});
		btnOrdersheetReport.setText("STOCK VERIFICATION");
		//Button order sheet Report
		btnOrdersheetReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(VerifyItemInVehicle.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.LOAD_VERIFICATION);
//				startActivityForResult(intent, 1000);
//				showToast("Print functionality is in progress.");
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if(preference.getbooleanFromPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false) || isMenu)
				{
					setResult(1111);
					finish();
				}
				else
				{
					Intent intent = new Intent(VerifyOnHandStock.this, OdometerReadingActivity.class);
					intent.putExtra("isStartDay", true);
					intent.putExtra("image_path", filepath);
					intent.putExtra("image_path_driver", filepathDriver);
					startActivity(intent);
					setResult(1111);
					finish();
				}
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(VerifyOnHandStock.this, WoosimPrinterActivity.class);
				intent.putExtra("itemforVerification", vecOrdProduct);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_VERIFY_ITEMS_IN_VEHICLE);
				startActivityForResult(intent, 1000);
			}
		});
		
		setTypeFace(llOrderSheet);
	}
	*//** initializing all the Controls  of DeliveryVerifyItemList class **//*
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
		btnContinue				=	(Button)llOrderSheet.findViewById(R.id.btnContinue);
		
		tvUsername				=	(TextView)llOrderSheet.findViewById(R.id.tvUsername);
		tvDate					=	(TextView)llOrderSheet.findViewById(R.id.tvDate);
		tvVehicleCode			=	(TextView)llOrderSheet.findViewById(R.id.tvVehicleCode);
		
		if(preference.getbooleanFromPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false))
			btnContinue.setText("Finish");
		else
			btnContinue.setText("Continue");
		
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
        
        tvUsername.setText(""+preference.getStringFromPreference(Preference.USER_NAME, ""));
		tvDate.setText(""+CalendarUtils.getFormatedDatefromString(CalendarUtils.getOrderPostDate()));
		tvVehicleCode.setText("Vehicle Code : "+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, ""));
		
		blockMenu();
		
		tvCases.setText("UOM");
		tvUnits.setText("QTY");
	}
	
	public class OrderSheetAdapter extends BaseAdapter
	{
		ArrayList<VanLoadDO> vecOrder;
		private boolean isCheckVisible = true;
		public OrderSheetAdapter(ArrayList<VanLoadDO> vec)
		{
			this.vecOrder=vec;
		}
		public void refresh() 
		{
			notifyDataSetChanged();
		}
		public void refresh(boolean isCheckVisible) 
		{
			this.isCheckVisible = isCheckVisible;
			notifyDataSetChanged();
		}
		public void refreshAll() 
		{
			notifyDataSetChanged();
		}
		@Override
		public int getCount() 
		{
			if(vecOrder != null && vecOrder.size() > 0)
				return vecOrder.size();
			else
				return 0;
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
		public void refresh(ArrayList<VanLoadDO> vec)
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
			VanLoadDO ordPro = vecOrder.get(position);
			//inflate invoice_list_cell layout here
			if(convertView == null)
				convertView			     =	(LinearLayout)getLayoutInflater().inflate(R.layout.item_list_verify_cell,null);
			//getting id's here
			LinearLayout llCellClick	 =	(LinearLayout)convertView.findViewById(R.id.llCellClick);
			TextView tvProductKey		 =	(TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName		 =	(TextView)convertView.findViewById(R.id.tvVendorName);
			EditText etQty				 =	(EditText)convertView.findViewById(R.id.etQty);
			EditText etUOM		 		 =	(EditText)convertView.findViewById(R.id.etUOM);
	
			final ImageView ivAcceptCheck=	(ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
			
			if(isCheckVisible)
				ivAcceptCheck.setVisibility(View.VISIBLE);
			else
				ivAcceptCheck.setVisibility(View.GONE);
			
			tvProductKey.setText(ordPro.ItemCode);
			tvVendorName.setText(""+ordPro.Description);
			
			if(ordPro.SellableQuantity > 0)
				etQty.setText(""+diffStock.format(ordPro.SellableQuantity));
			else
				etQty.setText(""+0);
			
			etUOM.setText(""+ordPro.UOM);
			//Setting Type-faces here
			if(isClicked != null && isClicked.contains(position))
				ivAcceptCheck.setImageResource(R.drawable.check_hover);
			else
				ivAcceptCheck.setImageResource(R.drawable.check_normal);
			
			setActionInViews(etQty, false);
			setActionInViews(etUOM, false);
			
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
			
			setTypeFace((LinearLayout)convertView);
			return convertView;
		}
	}
	
	private boolean isAllVerified()
	{
		if(isClicked.size() == vecOrdProduct.size())
			return true;
		
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
			btnContinue.setVisibility(View.VISIBLE);
			btnOrdersheetVerify.setVisibility(View.GONE);
			ivCheckAllItems.setVisibility(View.GONE);
			ordersheetadapter.refresh(false);
			
			if(preference.getbooleanFromPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false))
				btnContinue.setText("Finish");
			else
				btnContinue.setText("Continue");
		}
	}
	private String filepath =null, filepathDriver = null;;
	private boolean isVarifcationSignatureDone = false;
	private boolean isVarSignDoneDriver = false;
	@SuppressWarnings("deprecation")
	private void showSignatureDialog()
	{
		final Dialog dialog 			= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		final LinearLayout llSignSupervisor = (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout)llSignature.findViewById(R.id.llSignDriver);
	
		Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnSKCear 				= (Button)llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear 			= (Button)llSignature.findViewById(R.id.btnDriverCear);
		
		dialog.addContentView(llSignature,new LayoutParams(LayoutParams.FILL_PARENT, (int)(420 * px)));
		dialog.show();
		
		isVarifcationSignatureDone = false;
		isVarSignDoneDriver = false;
		
		myViewManager  = new MyView(this, false);
		myViewManager.setDrawingCacheEnabled(true);
		myViewManager.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , (int)(180 * px)));
		myViewManager.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignSupervisor.addView(myViewManager);
		
		
		myViewDriver  = new MyView(this, true);
		myViewDriver.setDrawingCacheEnabled(true);
		myViewDriver.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT , (int)(180 * px)));
		myViewDriver.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignDriver.addView(myViewDriver);
		
		setTypeFace(llSignature);
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(!isVarifcationSignatureDone && !isVarSignDoneDriver)
				{
					showCustomDialog(VerifyOnHandStock.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
				}
				else if(!isVarifcationSignatureDone)
					showCustomDialog(VerifyOnHandStock.this, getString(R.string.warning), "Please take store keeper's signature.", "OK", null, "");
				else if(!isVarSignDoneDriver)
					showCustomDialog(VerifyOnHandStock.this, getString(R.string.warning), "Please sign before submitting the stock verification.", "OK", null, "");
				else	
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							Bitmap bitmap = getBitmap(myViewManager);
							if(bitmap != null)
							{
								filepath = BitmapsUtiles.saveVerifySignature(bitmap);
							}
							
							bitmap = getBitmap(myViewDriver);
							if(bitmap != null)
								filepathDriver = BitmapsUtiles.saveVerifySignature(bitmap);
							
							final boolean isUpdated =  new VehicleDA().updateVMInventoryStatus(vecOrdProduct);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									dialog.dismiss();
									if(isUpdated)
									{
										preference.saveStringInPreference(Preference.IsStockVerifiedToday, CalendarUtils.getOrderPostDate());
										preference.commitPreference();
										showCustomDialog(VerifyOnHandStock.this, getString(R.string.verified), getString(R.string.stock_verified_in_the_van_as_per_the_order),
												getString(R.string.OK), null, "verify", false);
									}
									else
										showCustomDialog(VerifyOnHandStock.this, getString(R.string.warning), "Error occurred while verifying. Please try again.", getString(R.string.OK), null, "");
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnDriverCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				isVarifcationSignatureDone = false;
				isVarSignDoneDriver = false;
				if(myViewDriver != null)
					myViewDriver.clearCanvas();
			}
		});
		
		btnSKCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isVarifcationSignatureDone = false;
//				isVarSignDoneDriver = false;
				if(myViewManager != null)
					myViewManager.clearCanvas();
			}
		});
	}
	
//	private boolean uploadStock()
//	{
//		boolean isUploaded = false;
//		if(isNetworkConnectionAvailable(VerifyItemInVehicle.this))
//		{
//			ConnectionHelper connectionHelper 	  = new ConnectionHelper(VerifyItemInVehicle.this);
//			isUploaded = connectionHelper.sendRequest(BuildXMLRequest.insertStock(preference.getStringFromPreference(Preference.EMP_NO, ""),vecOrdProduct), ServiceURLs.InsertStock);
//		}
//		return isUploaded;
//	}
	
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
        private boolean isDriver = false;
        
        @SuppressWarnings("deprecation")
		public MyView(Context c, boolean isDriver)
        {
            super(c);
            Display display = 	getWindowManager().getDefaultDisplay(); 
            width 			= 	display.getWidth();
            height 			= 	display.getHeight();
            this.isDriver = isDriver; 
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
                	if(this.isDriver)
                		isVarSignDoneDriver = true;
                	else
                		isVarifcationSignatureDone = true;
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
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				vecOrdProduct	=	new VehicleDA().getAllItemToVerify();
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						ordersheetadapter.refresh(vecOrdProduct);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
*/