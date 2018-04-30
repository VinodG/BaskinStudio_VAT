package com.winit.baskinrobbin.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.AddItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class SalesmanCheckInDemand extends BaseActivity
{
	//Initialization and declaration of variables
	private LinearLayout llOrderSheet,llOrderListView ,llDate;
	private Button btnOrdersheetVerify,btnOrdersheetReport, btnAdd;
	private InventoryItemAdapter inventoryItemAdapter;
	private ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
	private TextView tvItemCode, tvUnits, tvOrdersheetHeader, tvNoItemFound, tvItemList, tvGatePass,
					 tvdate, tvDateValue;
	private ImageView ivCheckAllItems, ivImage;
	private Vector<String> vecCategory;
	private Vector<DeliveryAgentOrderDetailDco> vecSearchedItemd;
	private Paint mPaint;
	private AddItemAdapter adapter;
	private TextView tempView;
	private int DATE_DIALOG_ID = 0;
	private  int cyear, cmonth, cday;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.add_stock_inventory, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		//function for getting id's and setting type-faces 
		intialiseControls();
		setTypeFace(llOrderSheet);
		tvOrdersheetHeader.setText("Check-In Demand");
		
		inventoryItemAdapter = new InventoryItemAdapter(vecOrdProduct);
		//showing Loader
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecOrdProduct = new CommonDA().getCheckInDemandInventory(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getDeliverydate());
				
				AppConstants.vecCategories = new CategoriesDA().getAllCategory();
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
						hideLoader();
					}
				});
			}
		}).start();
		
		btnOrdersheetVerify.setText("Submit ");
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecOrdProduct != null && vecOrdProduct.size() > 0)
					showCustomDialog(SalesmanCheckInDemand.this, getString(R.string.warning), "Are you sure you want to confirm the check in demand stock load?", "Yes", "No", "stock");
				else
					showCustomDialog(SalesmanCheckInDemand.this, getString(R.string.warning), "Please add atleast one item in stock.", "Ok", null, "");
			}
		});
		
		btnOrdersheetReport.setText("Print");
		btnOrdersheetReport.setVisibility(View.GONE);
		//Button order sheet Report
		btnOrdersheetReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(SalesmanCheckInDemand.this, WoosimPrinterActivity.class);
//				intent.putExtra("itemforVerification", vecOrdProduct);
//				intent.putExtra("CALLFROM", CONSTANTOBJ.LOAD_VERIFICATION);
//				startActivityForResult(intent, 1000);
				showToast("Print functionality is in progress.");
				//Harcoded
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
		tvDateValue.setClickable(false);
		tvDateValue.setEnabled(false);
		tvDateValue.setFocusable(false);
		tvDateValue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				//while tapping on the List Cell to hide the keyboard first
				tempView = (TextView) v;
				showDialog(DATE_DIALOG_ID);
			}
		});
	}
	
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting id's
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetReport		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetReport);
		
		btnAdd					=	(Button)llOrderSheet.findViewById(R.id.btnAdd);
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
//		tvCases					=	(TextView)llOrderSheet.findViewById(R.id.tvCases);
		tvUnits					=	(TextView)llOrderSheet.findViewById(R.id.tvUnits);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		tvItemList				=	(TextView)llOrderSheet.findViewById(R.id.tvItemList);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		ivImage					=	(ImageView)llOrderSheet.findViewById(R.id.ivImage);
		tvGatePass				=	(TextView)llOrderSheet.findViewById(R.id.tvGatePass);
		
//		llDate					=	(LinearLayout)llOrderSheet.findViewById(R.id.llDate);
//		tvdate					=	(TextView)llOrderSheet.findViewById(R.id.tvdate);
//		tvDateValue				=	(TextView)llOrderSheet.findViewById(R.id.tvDateValue);
		
//		LinearLayout llTotalValLayout=	(LinearLayout)llOrderSheet.findViewById(R.id.llTotalValLayout);
//		llTotalValLayout.setVisibility(View.GONE);
		
		//setting type-faces
		/*tvOrdersheetHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnOrdersheetReport.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemList.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAdd.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvGatePass.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvdate.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDateValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		tvUnits.setVisibility(View.GONE);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		ivCheckAllItems.setVisibility(View.GONE);
		btnAdd.setVisibility(View.VISIBLE);
		llDate.setVisibility(View.VISIBLE);
		ivImage.setVisibility(View.VISIBLE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
//        tvCases.setVisibility(View.VISIBLE);
//        tvCases.setText("Cases");
        tvGatePass.setText("Units");
        tvDateValue.setText(CalendarUtils.getDeliverydate());
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("stock"))
			uploadStock();
		else if(from.equalsIgnoreCase("captured"))
			finish();
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesmanCheckInDemand.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		objAddNewSKUDialog.show();
		
		TextView tvItemCodeLabel			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItemCodeLabel);
		TextView tvItem_DescriptionLabel	=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItem_DescriptionLabel);
		TextView tvAdd_New_SKU_Item			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvAdd_New_SKU_Item);
		
		final EditText etCategory	 		=	(EditText)objAddNewSKUDialog.findViewById(R.id.etCategory);
		final EditText etSearch	 			=	(EditText)objAddNewSKUDialog.findViewById(R.id.etSearch);
		
		final ImageView cbList 				=	(ImageView)objAddNewSKUDialog.findViewById(R.id.cbList);
		final ListView lvPopupList		 	=	(ListView)objAddNewSKUDialog.findViewById(R.id.lvPopupList);
		
		final LinearLayout llList			=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llList);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
		/*tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		vecCategory = new Vector<String>();
		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		final Button btnSearch = new Button(SalesmanCheckInDemand.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				etCategory.setClickable(false);
				etCategory.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						etCategory.setClickable(true);
						etCategory.setEnabled(true);
					}
				}, 500);
				
				CustomBuilder builder = new CustomBuilder(SalesmanCheckInDemand.this, "Select Category", true);
				builder.setSingleChoiceItems(vecCategory, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						String str = (String) selectedObject;
			    		builder.dismiss();
		    			builder.dismiss();
		    			((TextView)v).setText(str);
						((TextView)v).setTag(str);
						
						btnSearch.performClick();
				    }
				}); 
				builder.show();
			}
		});
		
		cbList.setTag("False");
		cbList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(adapter != null)
					adapter.selectAll(cbList);
			}
		});
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etSearch.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(SalesmanCheckInDemand.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
					showLoader(getResources().getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId = "", catgName = etCategory.getText().toString(), orderedItems = "";
							
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
							
							for(int i=0; i<vecOrdProduct.size(); i++)
							{
								for(DeliveryAgentOrderDetailDco objProductDO : vecOrdProduct)
									orderedItems = orderedItems + "'"+objProductDO.itemCode+"',";
							}
							
//							vecSearchedItemd = new ProductsDA().getProductsByCategoryId_(catgId, orderedItems);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
									{
										cbList.setVisibility(View.VISIBLE);
										tvNoItemFound.setVisibility(View.GONE);
										lvPopupList.setAdapter(adapter = new AddItemAdapter(vecSearchedItemd,SalesmanCheckInDemand.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddItemAdapter( new Vector<DeliveryAgentOrderDetailDco>(),SalesmanCheckInDemand.this));
										cbList.setVisibility(View.INVISIBLE);
										tvNoItemFound.setVisibility(View.VISIBLE);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					Vector<DeliveryAgentOrderDetailDco> vecTemp = new Vector<DeliveryAgentOrderDetailDco>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						DeliveryAgentOrderDetailDco obj = (DeliveryAgentOrderDetailDco) vecSearchedItemd.get(index);
						String strText = ((DeliveryAgentOrderDetailDco)obj).itemCode;
						String strDesc = ((DeliveryAgentOrderDetailDco)obj).itemDescription;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDesc.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() >0 && adapter!= null)
					{
						adapter.refresh(vecTemp);
						tvNoItemFound.setVisibility(View.GONE);
						lvPopupList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoItemFound.setVisibility(View.VISIBLE);
						lvPopupList.setVisibility(View.GONE);
					}
				}
				else if(adapter!= null)
					adapter.refresh(vecSearchedItemd);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Vector<DeliveryAgentOrderDetailDco> veciItems= new Vector<DeliveryAgentOrderDetailDco>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
				{
					if(vecOrdProduct == null)
						vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
					
					vecOrdProduct.addAll(veciItems);
					
					objAddNewSKUDialog.dismiss();
					inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
				}
				else
				{
					showCustomDialog(SalesmanCheckInDemand.this, "Warning !", "Please select Items.", "OK", null, "");
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				objAddNewSKUDialog.dismiss();
			}
		});
	}
	
	public class InventoryItemAdapter
	{
		ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
		private View view;
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			public TextChangeListener(String type)
			{
				LogUtils.infoLog("TextChangeListener","TextChangeListener");
				this.type = type;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				DeliveryAgentOrderDetailDco objItem = null;
				if(view != null)
					objItem = (DeliveryAgentOrderDetailDco) view.getTag();
				
				if(type.equalsIgnoreCase("case"))
				{
					if(objItem != null)
					{
//						if(StringUtils.getFloat(s.toString()) < objItem.advnCases)
//						{
//							showToast("You have to take atleast "+objItem.advnCases+" Cases of advance order.");
//							objItem.etCases.setText(objItem.advnCases+"");
//							objItem.preCases 	= 	objItem.advnCases;
//							objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
//							objItem.totalCases  = 	StringUtils.getFloat(decimalFormat.format(objItem.totalCases));
//						}
//						else
//						{
//							objItem.preCases 	= 	StringUtils.getFloat(s.toString());
//							objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
//							objItem.totalCases  = 	StringUtils.getFloat(decimalFormat.format(objItem.totalCases));
//						}
						
						objItem.preCases 	= 	StringUtils.getFloat(s.toString());
						objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
						objItem.totalCases  = 	StringUtils.getFloat(diffStock.format(objItem.totalCases));
					}
				}
				else
				{
					if(objItem != null)
					{
//						if(StringUtils.getFloat(s.toString()) < objItem.advnPcs)
//						{
//							showToast("You have to take atleast "+objItem.advnPcs+" Units of advance order.");
//							objItem.etUnits.setText(objItem.advnPcs+"");
//							objItem.preUnits 	= 	objItem.advnPcs;
//							objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
//							objItem.totalCases  = 	StringUtils.getFloat(decimalFormat.format(objItem.totalCases));
//						}
//						else
//						{
//							objItem.preUnits 	= 	StringUtils.getInt(s.toString());
//							objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
//							objItem.totalCases  = 	StringUtils.getFloat(decimalFormat.format(objItem.totalCases));
//						}
						
						objItem.preUnits 	= 	StringUtils.getInt(s.toString());
						objItem.totalCases 	= 	objItem.preCases +((float)objItem.preUnits/(float)objItem.unitPerCase);
						objItem.totalCases  = 	StringUtils.getFloat(diffStock.format(objItem.totalCases));
					}
				}
			}
	
			@Override
			public void afterTextChanged(Editable s) 
			{
			}
		}
		
		class FocusChangeListener implements OnFocusChangeListener
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
					view = v;
			}
		}
		
		public InventoryItemAdapter(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct)
		{
			this.vecOrdProduct = vecOrdProduct;
		}
	
		public ArrayList<DeliveryAgentOrderDetailDco> getModifiedData()
		{
			return vecOrdProduct;
		}

		public void getChildView(LinearLayout llLayoutMiddle)
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			for(DeliveryAgentOrderDetailDco productDO : vecOrdProduct)
			{
				LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.demand_inventory_cell_new,null);
				
				TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
				TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				TextView evCases			= (TextView)convertView.findViewById(R.id.evCases);
				TextView evUnits			= (TextView)convertView.findViewById(R.id.evUnits);
				
				tvHeaderText.setText(productDO.itemCode);
//				tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvDescription.setText(productDO.itemDescription);
//				tvDescription.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				
				evCases.setTag(productDO);
				evUnits.setTag(productDO);
				
				productDO.etCases = (EditText) evCases;
				productDO.etUnits = (EditText) evUnits;
				if(productDO.checkInCases > 0)
					evCases.setText(""+productDO.checkInCases);
				else
					evCases.setText("");
				
				if(productDO.checkInPcs > 0)
					evUnits.setText(""+productDO.checkInPcs);
				else if(productDO.itemType != null && productDO.itemType.equalsIgnoreCase("2"))
				{
					evUnits.setEnabled(false);
					evUnits.setFocusable(false);
					evUnits.setFocusableInTouchMode(false);
					evUnits.setText("");
					evUnits.setHint("");
				}
				else
					evUnits.setText("");
				
				evCases.setOnFocusChangeListener(new FocusChangeListener());
				evCases.addTextChangedListener(new TextChangeListener("case"));
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("unit"));
				
				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(40 * px)));
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						
					}
				});
				llLayoutMiddle.addView(convertView);
			}
		}
		
		//Method to refresh the List View
		private void refresh(ArrayList<DeliveryAgentOrderDetailDco> vecOrdProducts,LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			this.vecOrdProduct = vecOrdProducts;
			if(vecOrdProduct != null && vecOrdProducts.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				llLayoutMiddle.setVisibility(View.VISIBLE);
				getChildView(llLayoutMiddle);
			}
			else
			{
				tvNoItemFound.setVisibility(View.VISIBLE);
				llLayoutMiddle.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	  {
		  //getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 1);
		  
		  cyear 	= 	c.get(Calendar.YEAR);
		  cmonth 	= 	c.get(Calendar.MONTH);
		  cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	  }
		/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
	  {
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar currentCal = Calendar.getInstance();
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
		    	
			  if(!currentCal.after(tempCal) && tempCal.compareTo(currentCal) > 0)
			  {
				  if(tempView != null)
				  {
					  String strDate = "", strMonth ="";
					  if(monthOfYear <9)
						  strMonth = "0"+(monthOfYear+1);
					  else
						  strMonth = ""+(monthOfYear+1);
			    		
					  if(dayOfMonth <10)
						  strDate = "0"+(dayOfMonth);
					  else
						  strDate = ""+(dayOfMonth);
			    		
					  ((TextView)tempView).setText(year+"-"+strMonth+"-"+strDate);
				  }
			  }
			  else
			  {
				  showCustomDialog(SalesmanCheckInDemand.this, getResources().getString(R.string.warning), "Selected date should be greater than current date.", getResources().getString(R.string.OK), null, "");
			  }
		  }
	  };
	  
	int status = 0;
	private void uploadStock()
	{
		showLoader("Uploading data...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				status = 0;
				String date = tvDateValue.getText().toString();
				boolean isUploaded = new CommonDA().insertCheckInDemandInventory(preference.getStringFromPreference(Preference.EMP_NO, ""),date, vecOrdProduct, 0);
				if(isUploaded)
					status = 1;
				else
					status = 0;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(status == 1)
							showCustomDialog(SalesmanCheckInDemand.this, getString(R.string.successful), "Check in demand has been captured successfully. ", "OK", null, "captured");
						else 
							showCustomDialog(SalesmanCheckInDemand.this, "Alert !", "Unable to upload data, please try again.", "OK", null, "");
						uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
