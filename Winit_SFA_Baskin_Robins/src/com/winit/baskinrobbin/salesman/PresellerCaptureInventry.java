package com.winit.baskinrobbin.salesman;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.AddRecomendedItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.CustomDialog;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDO;
import com.winit.baskinrobbin.salesman.dataobject.InventoryDetailDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class PresellerCaptureInventry extends BaseActivity
{
	//declaration of variables Search Item
	private LinearLayout llCapture_Inventory;
	private TextView tvAll, tvTopSelling, tvLu, tvCI, tvNoOrder;
	private Button btnSort, btnSubmit, btnCancel, btnAddIetem, btnAddItems;
	private ScrollView svForCaptureInventoryItemd;
	private CaptureInventryDA objCaptureInventryBL;
	private CaptureInventaryItems captureInventaryItems;
	private boolean alreadyPreviousOrdersLoaded;
	private static final int ALL_SKUS = 4;
	private int filterCurrentMode = ALL_SKUS;
	private AddRecomendedItemAdapter addRecomendedItemAdapter;
	private LinearLayout llLayoutMiddle, llTopSellingItems;
	private HashMap<String, Vector<ProductDO>> hmSelectedItems;
	private Vector<ProductDO> vecSearchedItemd;
	private String orderedItems ="",orderedItemsList = "";
	private Vector<String> vecCategory;
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	
	@Override
	public void initialize()
	{
		llCapture_Inventory = 	(LinearLayout)getLayoutInflater().inflate(R.layout.captureinventory, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		if(AppConstants.hmCapturedInventory!=null && AppConstants.hmCapturedInventory.size()>0)
			AppConstants.hmCapturedInventory.clear();
		
		if(getIntent().getExtras() != null)
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		
		AppConstants.CheckIN = true;
		preference.saveLongInPreference(Preference.TOTAL_TIME_TO_SERVE, CalendarUtils.getCurrentTimeInMilli());
		preference.saveBooleanInPreference("recommendedorderprinted", false);
		preference.commitPreference();
		
		intialiseControls();
		
		setTypeFace(llCapture_Inventory);
		
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName + " ("+mallsDetails.partyName+")");
		
		svForCaptureInventoryItemd.setKeepScreenOn(true);
		svForCaptureInventoryItemd.setSmoothScrollingEnabled(true);
		objCaptureInventryBL = new CaptureInventryDA();
		
		btnCancel.setText("Skip");
		btnSort.setVisibility(View.GONE);
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(btnCheckOut != null)
					btnCheckOut.performClick();
			}
		});
		captureInventaryItems = new CaptureInventaryItems(null);
		tvCI.setText("Capture Inventory");
		
		
		onViewClickListners();
		
		showItems(filterCurrentMode);
	}
	
	private void intialiseControls() 
	{
		llLayoutMiddle				=	(LinearLayout)llCapture_Inventory.findViewById(R.id.llLayoutMiddle);
		btnSubmit					= 	(Button)llCapture_Inventory.findViewById(R.id.btnSubmit);
		btnCancel					= 	(Button)llCapture_Inventory.findViewById(R.id.btnCancel);
		btnSort 					= 	(Button)llCapture_Inventory.findViewById(R.id.btnSort);
		tvLu						= 	(TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvCI						= 	(TextView)llCapture_Inventory.findViewById(R.id.tvCI);
		tvNoOrder					= 	(TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
		svForCaptureInventoryItemd	= 	(ScrollView)llCapture_Inventory.findViewById(R.id.svForCaptureInventoryItemd);
		llTopSellingItems			=	(LinearLayout)llCapture_Inventory.findViewById(R.id.llTopSellingItems);
		
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddItems			= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		btnAddIetem.setVisibility(View.GONE);
		tvNoOrder.setText("Please capture inventory by clicking on Add Item.");
	}
	
	public void ShowDeleteButton(Vector<Item> vecItems)
	{
		for(int i = 0; vecItems!= null && i <vecItems.size() ; i++)
		{
			Button btnDelete = (Button)findViewById(i);
			
			if(btnDelete != null)
			{
				btnDelete.setVisibility(View.GONE);
				btnDelete.setTag("false");
			}
		}
	}
	
	public void onViewClickListners()
	{
		btnAddItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showAddNewSkuPopUp();
			}
		});
		btnAddIetem.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showAddNewSkuPopUp();
			}
		});
		
		btnSubmit.setText("Submit");
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				btnSubmit.setClickable(false);
				btnSubmit.setEnabled(false);
				
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run() 
					{
						btnSubmit.setClickable(true);
						btnSubmit.setEnabled(true);
					}
				} ,500);
				
				final InventoryDO inventoryDO 	= new InventoryDO();
				showLoader(getResources().getString(R.string.validating_please_wait));
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						inventoryDO.site 			= mallsDetails.site;
						inventoryDO.date 			= CalendarUtils.getCurrentDateTime();
						inventoryDO.uplaodStatus	= 0;
						inventoryDO.createdBy		= preference.getStringFromPreference(Preference.EMP_NO, "");
						inventoryDO.inventoryAppId 	= StringUtils.getUniqueUUID();
						
						Vector<String> vecCategoryIds = new Vector<String>();
						if(hmSelectedItems == null || hmSelectedItems.size() == 0)
						{
							hideLoader();
							AppConstants.isRecomendedChanged = true;
							onButtonYesClick("SubmitInventory");
						}
						else
						{
							if(AppConstants.hmCapturedInventory == null)
								AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
							else
								AppConstants.hmCapturedInventory.clear();
							
							AppConstants.hmCapturedInventory = (HashMap<String, Vector<ProductDO>>) hmSelectedItems.clone();
							
							Set<String> set = AppConstants.hmCapturedInventory.keySet();
							Iterator<String> iterator = set.iterator();
							while(iterator.hasNext())
								vecCategoryIds.add(iterator.next());
							
							StringBuffer itemsStr = new StringBuffer();
							
							for(int i=0; i<vecCategoryIds.size(); i++)
							{
								Vector<ProductDO> vecItems = AppConstants.hmCapturedInventory.get(vecCategoryIds.get(i));
								
								for(ProductDO objProductDO : vecItems)
								{
									if(objProductDO.units.equalsIgnoreCase(""))
									{
										itemsStr.append(objProductDO.SKU + ", ");
									}
									objProductDO.isReccomended = true;
									objProductDO.inventoryQty= StringUtils.getFloat(objProductDO.units);
									
									if(hmInventory != null && hmInventory.containsKey(objProductDO.SKU))
									{
										objProductDO.preUnits = ""+(int)Math.abs(Math.floor((objProductDO.orderedUnits - StringUtils.getFloat(objProductDO.units))*1.5));
										
										HHInventryQTDO hhInventryQTDO = hmInventory.get(objProductDO.SKU);
										
										float availQty = hhInventryQTDO.totalQt;
										if(availQty < StringUtils.getFloat(objProductDO.preUnits))
											objProductDO.preUnits = ""+(int)availQty;
										
										objProductDO.strExpiryDate = hhInventryQTDO.expiryDate;
										objProductDO.BatchCode     = hhInventryQTDO.batchCode;
									}
									else
										objProductDO.preUnits = "0";
									
									if(StringUtils.getFloat(objProductDO.preUnits) <= 0 )
										objProductDO.preUnits = ""+objProductDO.UnitsPerCases;
									
									objProductDO.preCases = "0";
									
									objProductDO.recomCases = objProductDO.preCases;
									objProductDO.recomUnits = StringUtils.getFloat(objProductDO.preUnits);
									
									InventoryDetailDO inventoryDetailDO = new InventoryDetailDO();
									inventoryDetailDO.itemCode 			= objProductDO.SKU;
									inventoryDetailDO.inventoryQty 		= StringUtils.getFloat(objProductDO.units);
									inventoryDetailDO.recmQty 			= StringUtils.getFloat(objProductDO.preUnits);
									inventoryDO.vecInventoryDOs.add(inventoryDetailDO);
								}
							}
							
							String itms = itemsStr.toString();
							
							if(itms.endsWith(", "))
								itms = itms.substring(0, itms.length() - 2);
							
							final String itemsList = itms;
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									
									if(itemsList != null && !itemsList.equalsIgnoreCase(""))
									{
										String strMsg = "You have not modified the quantity of following item(s), Do you want to continue to the next page? \n"+itemsList;
										skipPopup(strMsg, inventoryDO);
									}
									else
									{
										submitCapturedInventory(inventoryDO);
										onButtonYesClick("SubmitInventory");
									}
								}
							});
						}
					}
				}).start();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				btnSubmit.performClick();
			}
		});
	}
	
	public void showItems(int filterMode)
	{
		if(filterMode == ALL_SKUS)
		{
			if(alreadyPreviousOrdersLoaded)
			{
				llLayoutMiddle.setVisibility(View.VISIBLE);
				llTopSellingItems.setVisibility(View.GONE);
				
				if(hmSelectedItems == null || hmSelectedItems.size() == 0)
				{
					llLayoutMiddle.removeAllViews();
					
					tvNoOrder.setText("Please capture inventory by clicking on Add Item.");
					tvNoOrder.setVisibility(View.VISIBLE);
				}
			}
			else
				loadPreviousOrderItems();
		}
	}
	
	public void loadPreviousOrderItems()
	{
		showLoader(getResources().getString(R.string.please_wait));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(AppConstants.hmCateogories == null)
					AppConstants.hmCateogories =  new CategoriesDA().getCategoryList();
				
				if(hmSelectedItems != null && hmSelectedItems.size()>0)
					hmSelectedItems.clear();
				
				//getting the customer's pricing key 
				hmSelectedItems 		=	objCaptureInventryBL.getLast6MonthOrdersByCustomer(mallsDetails.site, mallsDetails.priceList,"");
				hmInventory 			= 	new OrderDetailsDA().getAvailInventoryQtys_Temp("");
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						llTopSellingItems.setVisibility(View.GONE);
						svForCaptureInventoryItemd.setVisibility(View.VISIBLE);
						
						llLayoutMiddle.removeAllViews();
						
						if(hmSelectedItems == null || hmSelectedItems.size() == 0)
						{
							tvNoOrder.setText("Please capture inventory by clicking on Add Item.");
							tvNoOrder.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoOrder.setVisibility(View.GONE);
							captureInventaryItems.refresh(hmSelectedItems,llLayoutMiddle, filterCurrentMode);
						}
						alreadyPreviousOrdersLoaded = true;
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	public class CaptureInventaryItems
	{
		private HashMap<String, Vector<ProductDO>> hmItems;
		private Vector<String> vecCategoryIds;
		private View view;
		
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			int groupPosition = -1;
			int childPosition = -1;
			public TextChangeListener(String type, int groupPosition, int childPosition)
			{
				LogUtils.infoLog("TextChangeListener","TextChangeListener");
				this.type = type;
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
			}
	
			@Override
			public void afterTextChanged(Editable s) 
			{
				ProductDO objItem = null;
				if(view != null)
				{
					objItem = (ProductDO) view.getTag();
				}
				if(objItem != null)
				{
					objItem.units = s.toString();
				}
			}
		}
		
		class FocusChangeListener implements OnFocusChangeListener
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					view = v;
				}
			}
		}
		public CaptureInventaryItems(HashMap<String, Vector<ProductDO>> hmItems)
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
			}
		}
	
		public LinearLayout getChildView(int groupPosition, int mode)
		{
			LinearLayout llChildViews = new LinearLayout(getApplicationContext());
			llChildViews.setOrientation(1);
			for(int childPosition = 0;childPosition<  hmItems.get(vecCategoryIds.get(groupPosition)).size();childPosition++)
			{
				final ProductDO objProduct = hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				if(mode == ALL_SKUS)
				{
					LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
					TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
					TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
					final TextView evCases		= (TextView)convertView.findViewById(R.id.evCases);
					final EditText evUnits		= (EditText)convertView.findViewById(R.id.evUnits);
					
					evCases.setId((groupPosition*1000)+childPosition);
					evUnits.setId((groupPosition*1000)+(childPosition+1000));
					objProduct.etUnits =  evUnits;
					
					tvHeaderText.setText(objProduct.SKU);
					tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
					tvDescription.setText(objProduct.Description);
					tvDescription.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
					evUnits.setTag(objProduct);
					
					if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
						evCases.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.arrow_2));
					
					if(objProduct.units.equalsIgnoreCase("") || objProduct.units.equalsIgnoreCase("0"))
						evUnits.setText("");
					else
						evUnits.setText(objProduct.units);
						
					setActionInViews(evUnits, true);
					
					evUnits.setOnFocusChangeListener(new FocusChangeListener());
					
					evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
					tvHeaderText.setTag(childPosition);
					convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(40 * px)));
					convertView.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							
						}
					});
					
					evCases.setText(objProduct.UOM);
					evCases.setTag(objProduct.UOM);
					
					if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
					evCases.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							if(objProduct.vecUOM != null && objProduct.vecUOM.size() > 0)
							{
								CustomBuilder customDialog = new CustomBuilder(PresellerCaptureInventry.this, "Select UOM", true);
								customDialog.setSingleChoiceItems(objProduct.vecUOM, evCases.getTag(), new CustomBuilder.OnClickListener() 
								{
									@Override
									public void onClick(CustomBuilder builder, Object selectedObject) 
									{
										evCases.setText((String)selectedObject);
										evCases.setTag((String)selectedObject);
										objProduct.UOM =(String)selectedObject; 
										builder.dismiss();
									}
								});
								customDialog.show();
							}
						}
					});
					llChildViews.addView(convertView);
				}
				else
				{
					LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.topselling_cell,null);
					TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
					TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				
					tvHeaderText.setText(objProduct.SKU);
					tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
					tvDescription.setText(objProduct.Description);
					tvDescription.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
					
					llChildViews.addView(convertView);
				}
			}
			return llChildViews;
		}
		
		//Method to refresh the List View
		private void refresh(HashMap<String, Vector<ProductDO>> hmItems,LinearLayout llLayoutMiddle, int mode) 
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				getView(llLayoutMiddle, mode);
			}
		}
		
		public void getView(LinearLayout llLayoutMiddle, int mode) 
		{
			for(int position = 0 ; vecCategoryIds!=null && position<vecCategoryIds.size();position++)
			{
				String strCategory;
//				CategoryDO objCategoryDO = AppConstants.hmSubCateogories.get(vecCategoryIds.get(position));
				
				int childItemsSize = hmItems.get(vecCategoryIds.get(position)).size();
				try 
				{
					strCategory = hmItems.get(vecCategoryIds.get(position)).get(0).CategoryId;
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					strCategory = "TISSUE";
				}
				
				final View convertView				=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llCode 			= 	(LinearLayout)convertView.findViewById(R.id.llCode);
				final LinearLayout llBottomLayout 	= 	(LinearLayout)convertView.findViewById(R.id.llBottomLayout);
				TextView tvCode 					= 	(TextView)convertView.findViewById(R.id.tvCode);
				TextView tvCases 					= 	(TextView)convertView.findViewById(R.id.tvCases);
				final ImageView ivArrow				= 	(ImageView)convertView.findViewById(R.id.ivArrow);
				TextView tvUnits 					= 	(TextView)convertView.findViewById(R.id.tvUnits);
				
				if(mode == ALL_SKUS)
				{
					tvCases.setVisibility(View.VISIBLE);
					tvUnits.setVisibility(View.VISIBLE);
				}
				else
				{
					tvCases.setVisibility(View.GONE);
					tvUnits.setVisibility(View.GONE);
				}
				
				tvCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				TextView tvTitleText	= (TextView)convertView.findViewById(R.id.tvInventryText);
				TextView tvNo			= (TextView)convertView.findViewById(R.id.tvNo);
				tvNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvTitleText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				
				if(!strCategory.equalsIgnoreCase("") && childItemsSize > 0)
				{
					tvTitleText.setText(strCategory);
					tvNo.setText("("+childItemsSize+" Sub Products)");
				}
				else
					tvNo.setText("(0 Sub Products)");
				
				llBottomLayout.setTag(position);
				llBottomLayout.setVisibility(View.GONE);
				llBottomLayout.addView(getChildView(StringUtils.getInt(llBottomLayout.getTag().toString()), mode), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				convertView.setTag("closed");
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if(v.getTag().toString().equalsIgnoreCase("closed"))
						{
							ivArrow.setImageResource(R.drawable.arro2);
							v.setTag("open");
							if(llBottomLayout.getChildCount()> 0)
							{
								llCode.setVisibility(View.VISIBLE);
								llBottomLayout.setVisibility(View.VISIBLE);
							}
						}
						else
						{
							v.setTag("closed");
							llBottomLayout.setVisibility(View.GONE);
							ivArrow.setImageResource(R.drawable.arro);
							llCode.setVisibility(View.GONE);
						}
					}
				});
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						convertView.performClick();
					}
				}, 100);
				llLayoutMiddle.addView(convertView);
			}
		}
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("SubmitInventory"))
		{
			AppConstants.isRecomendedChanged = true;
			Intent intent = new Intent(PresellerCaptureInventry.this, SalesManTakeOrder.class);
			intent.putExtra("mallsDetails",mallsDetails);
			startActivity(intent);
		}
	}
	
	private void submitCapturedInventory(final InventoryDO inventoryDO)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				new CaptureInventryDA().insertInventory(inventoryDO);
			}
		}).start();
	}

	public void showAddNewSkuPopUp()
	{
		AppConstants.isRecomendedChanged = true;
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(PresellerCaptureInventry.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		objAddNewSKUDialog.show();
		
		TextView tvItemCodeLabel			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItemCodeLabel);
		TextView tvItem_DescriptionLabel	=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvItem_DescriptionLabel);
		TextView tvAdd_New_SKU_Item			=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvAdd_New_SKU_Item);
		
		final EditText etCategory	 		=	(EditText)objAddNewSKUDialog.findViewById(R.id.etCategory);
		final EditText etSearch	 			=	(EditText)objAddNewSKUDialog.findViewById(R.id.etSearch);
		final ImageView cbList 				=	(ImageView)objAddNewSKUDialog.findViewById(R.id.cbList);
		final LinearLayout llResult 		=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llResult);
		final LinearLayout llBottomButtons 	=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llBottomButtons);
		final ListView lvPopupList		 	=	(ListView)objAddNewSKUDialog.findViewById(R.id.lvPopupList);
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		Button btnAdd 						=	(Button)objAddNewSKUDialog.findViewById(R.id.btnAdd);
		Button btnCancel 					=	(Button)objAddNewSKUDialog.findViewById(R.id.btnCancel);
		final TextView tvNoItemFound		=	(TextView)objAddNewSKUDialog.findViewById(R.id.tvNoItemFound);
		
		final LinearLayout llList			=	(LinearLayout)objAddNewSKUDialog.findViewById(R.id.llList);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		vecCategory = new CategoriesDA().getAllCategoryName();
		
		final Button btnSearch = new Button(PresellerCaptureInventry.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(PresellerCaptureInventry.this, "Select Category", true);
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
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(s.toString()!=null)
				{
					Vector<ProductDO> vecTemp = new Vector<ProductDO>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						ProductDO obj = (ProductDO) vecSearchedItemd.get(index);
						String strText = ((ProductDO)obj).SKU;
						String strDes = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() > 0 && addRecomendedItemAdapter!= null)
					{
						addRecomendedItemAdapter.refresh(vecTemp);
						tvNoItemFound.setVisibility(View.GONE);
						lvPopupList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoItemFound.setVisibility(View.VISIBLE);
						lvPopupList.setVisibility(View.GONE);
					}
				}
				else if(addRecomendedItemAdapter!= null)
					addRecomendedItemAdapter.refresh(vecSearchedItemd);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		cbList.setTag("False");
		cbList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
			}
		});
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(etCategory.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(etCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(PresellerCaptureInventry.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<ProductDO>();
					else
						vecSearchedItemd.clear();
					orderedItems ="";
					orderedItemsList = "";
					if(hmSelectedItems!=null)
					{
						Set<String> set = hmSelectedItems.keySet();
						Iterator<String> iterator = set.iterator();
						Vector<String> vecCategoryIds = new Vector<String>();
						while(iterator.hasNext())
							vecCategoryIds.add(iterator.next());
						
						for(int i=0; i<vecCategoryIds.size(); i++)
						{
							Vector<ProductDO> vecOrderedProduct = hmSelectedItems.get(vecCategoryIds.get(i));
							for(ProductDO objProductDO : vecOrderedProduct)
							{
								orderedItems = orderedItems + "'"+objProductDO.SKU+"',";
							}
						}
						if(orderedItems.contains(","))
							orderedItemsList = orderedItems.substring(0, orderedItems.lastIndexOf(","));
						else
							orderedItemsList = orderedItems;
					}
					final ProductsDA objItemDetailBL = new ProductsDA(); 
					showLoader(getResources().getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId = "", catgName = etCategory.getText().toString();
							
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
//							vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(catgId, orderedItemsList, mallsDetails.priceList, 0,false, null);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									llResult.setVisibility(View.VISIBLE);
									if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
									{
										cbList.setVisibility(View.VISIBLE);
										tvNoItemFound.setVisibility(View.GONE);
										llBottomButtons.setVisibility(View.VISIBLE);
										lvPopupList.setAdapter(addRecomendedItemAdapter = new AddRecomendedItemAdapter(vecSearchedItemd,PresellerCaptureInventry.this));
									}
									else
									{
										lvPopupList.setAdapter(addRecomendedItemAdapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),PresellerCaptureInventry.this));
										cbList.setVisibility(View.INVISIBLE);
										tvNoItemFound.setVisibility(View.VISIBLE);
										llBottomButtons.setVisibility(View.GONE);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				AppConstants.isRecomendedChanged =true;
				Vector<ProductDO> veciItems= new Vector<ProductDO>();
				if(addRecomendedItemAdapter != null)
					veciItems = addRecomendedItemAdapter.getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
				{
					if(hmSelectedItems == null)
						hmSelectedItems = new HashMap<String, Vector<ProductDO>>();
					
					for(int i=0; veciItems != null && i < veciItems.size(); i++)
					{
						ProductDO objProduct = veciItems.get(i);
						Vector<ProductDO> vecProducts = hmSelectedItems.get(objProduct.CategoryId);
						if(vecProducts == null)
						{
							vecProducts = new Vector<ProductDO>(); 
							vecProducts.add(objProduct);
							hmSelectedItems.put(objProduct.CategoryId, vecProducts);
						}
						else
							vecProducts.add(objProduct);
					}
					
					if(captureInventaryItems != null)
					{
						tvNoOrder.setVisibility(View.GONE);
						captureInventaryItems.refresh(hmSelectedItems, llLayoutMiddle, filterCurrentMode);
					}
					
					objAddNewSKUDialog.dismiss();
				}
				else
					showCustomDialog(PresellerCaptureInventry.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	private void skipPopup(String mesg, final InventoryDO inventoryDO)
	{
		View view = inflater.inflate(R.layout.custom_common_popup_new, null);
		final CustomDialog mCustomDialog = new CustomDialog(PresellerCaptureInventry.this, view, preference
				.getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
		TextView tvMessage = (TextView) view
				.findViewById(R.id.tvMessagePopup);
		TextView tvMessage1 = (TextView) view.findViewById(R.id.tvMessagePopup1);
		Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
		Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
		Button btnSkip = (Button) view.findViewById(R.id.btnSkip);
		
		tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvMessage1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnYes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSkip.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvMessage1.setVisibility(View.VISIBLE);
		tvMessage.setVisibility(View.GONE);
		tvTitle.setText("Warning");
		tvMessage1.setText(mesg);
		
		btnYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
				submitCapturedInventory(inventoryDO);
				onButtonYesClick("SubmitInventory");
			}
		});
		btnNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCustomDialog.dismiss();
			}
		});
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
}