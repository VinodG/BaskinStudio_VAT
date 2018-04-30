package com.winit.baskinrobbin.salesman;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.adapter.AddRecomendedItemAdapter;
import com.winit.baskinrobbin.salesman.common.Add_new_SKU_Dialog;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CategoriesDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.ProductsDA;
import com.winit.baskinrobbin.salesman.dataobject.CategoryDO;
import com.winit.baskinrobbin.salesman.dataobject.HHInventryQTDO;
import com.winit.baskinrobbin.salesman.dataobject.Item;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.ProductDO;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

@SuppressLint("DefaultLocale")
public class SalesManFreeDeliveryAddItem extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave;
	private TextView tvLu, tvHeaderText, tvName, tvRegionCode;
	private EditText etName;
	private TextView etRegionCode;
	private Button  btnAddIetem, btnAddItems, btnCancel1, btnConfirmOrder; 
	private TextView etDiscValue , tvNoOrder;
	private TextView evTotalValue;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd;
	private TextView tvDisHeader;
	private CaptureInventaryAdapter adapterForCapture;
	private AddRecomendedItemAdapter adapter;
	private float totPrice = 0.0f, totalDiscount = 0.0f;
	public static Vector<ProductDO> vecRecommended ;
	private String orderedItems ="",orderedItemsList = "";
	private LinearLayout llLayoutMiddle; 
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, Vector<String>> hmUOMFactors;
	public  HashMap<String, Vector<ProductDO>> hmCapturedInventoryFreeDelivery;
	
	@Override
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.free_delivery_item, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		intialiseControls();
		
		setTypeFace(llCapture_Inventory);
		mallsDetails = new JourneyPlanDO();
		
		llBottomButtons.setVisibility(View.GONE);
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.VISIBLE);
		etDiscValue.setEnabled(true);
		etDiscValue.setFocusable(true);
		
		btnCancel1.setText("Cancel");
		btnCancel1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		btnConfirmOrder.setText(" Confirm Order ");
		btnConfirmOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(etName.getText().toString().equals(""))
					showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.warning), "Please enter name.", getString(R.string.OK), null, "");
				else if(etRegionCode.getText().toString().equals(""))
					showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.warning), "Please select reason.", getString(R.string.OK), null, "");
				else
				{
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							final boolean isAvail  = getAddedItemAvailability(hmCapturedInventoryFreeDelivery);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									mallsDetails.siteName = etName.getText().toString();
									mallsDetails.site = System.currentTimeMillis()+"";
									mallsDetails.freeDeliveryResion = etRegionCode.getText().toString();
									
									if(hmCapturedInventoryFreeDelivery == null || hmCapturedInventoryFreeDelivery.size() <= 0 )
										showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
									else if(!isAvail)
										showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
									else
									{
										AppConstants.hmCapturedInventoryFreeDelivery = hmCapturedInventoryFreeDelivery;
										Intent intent = new Intent(SalesManFreeDeliveryAddItem.this, SalesmanFreeDeliveryOrderPreview.class);
										intent.putExtra("isReturnRequest", true);
										intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
										intent.putExtra("Discount", etDiscValue.getText().toString().trim());
										intent.putExtra("mallsDetails",mallsDetails);
										startActivityForResult(intent, 1000);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		onViewClickListners();
		//getting the customer's pricing key 
		
		if(hmCapturedInventoryFreeDelivery != null)
			hmCapturedInventoryFreeDelivery.clear();
		else 
			hmCapturedInventoryFreeDelivery = new HashMap<String, Vector<ProductDO>>();
		
		if(AppConstants.vecCategories == null)
			AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
		
		if(hmCapturedInventoryFreeDelivery != null && hmCapturedInventoryFreeDelivery.size() > 0)
		{
			tvNoOrder.setVisibility(View.GONE);
			calTotPrices(true);
		}
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		etDiscValue.setText(""+diffAmt.format(totalDiscount));
		if(hmCapturedInventoryFreeDelivery == null || hmCapturedInventoryFreeDelivery.size() == 0)
		{
			evTotalValue.setText("0");
			etDiscValue.setText("0");
		}
		else
		{
			etDiscValue.setText(""+diffAmt.format(totalDiscount));
		}
		
//		IntentFilter filterForJourney = new IntentFilter();
//		filterForJourney.addAction(AppConstants.ACTION_GOTO_TELEORDERS);
//		registerReceiver(GotoTeleOrders, filterForJourney);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hmInventory = new OrderDetailsDA().getAvailInventoryQtys_Temp("");
				hmUOMFactors = new OrderDetailsDA().getPricingUOM(mallsDetails.priceList, AppStatus.SALES_ORDER_TYPE);
				if(AppConstants.vecCategories == null)
					AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
					}
				});
			}
		}).start();
		
		etRegionCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				showOptionDialog();
			}
		});
		
		setTypeFace(llCapture_Inventory);
	}
	
	
	
	
	
	public void intialiseControls()
	{
		llLayoutMiddle		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llLayoutMiddle);
		llTotalValue		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llTotalValue);
		btnConfirmOrder 	= (Button)llCapture_Inventory.findViewById(R.id.btnFinalize);
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddItems			= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		evTotalValue		= (TextView)llCapture_Inventory.findViewById(R.id.evTotalValue);
		btnCancel1 			= (Button)llCapture_Inventory.findViewById(R.id.btnSave);
		tvLu				= (TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvNoOrder			= (TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
		
		tvName				= (TextView)llCapture_Inventory.findViewById(R.id.tvName);
		tvRegionCode		= (TextView)llCapture_Inventory.findViewById(R.id.tvRegionCode);
		etName				= (EditText)llCapture_Inventory.findViewById(R.id.etName);
		etRegionCode		= (TextView)llCapture_Inventory.findViewById(R.id.etRegionCode);
		
		etDiscValue			= (TextView)llCapture_Inventory.findViewById(R.id.etDiscValue);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		tvHeaderText		= (TextView)llCapture_Inventory.findViewById(R.id.tvHeaderText);
		
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		/*tvNoOrder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnConfirmOrder.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddIetem.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnAddItems.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		btnAddIetem.setTextColor(Color.WHITE);
		/*tvLu.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDisHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCancel1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvRegionCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etRegionCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		llTotalValue.setVisibility(View.GONE);  
		btnAddItems.setVisibility(View.GONE);
		
//		if(mallsDetails != null)
//			tvLu.setText(mallsDetails.SiteName);
		
		btnAddIetem.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}

	private void calTotPrices(boolean isLoaderShown)
	{
		if(isLoaderShown)
		showLoader(getString(R.string.loading));
		totPrice = 0.0f;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Set<String> set = hmCapturedInventoryFreeDelivery.keySet();
				Iterator<String> iterator = set.iterator();
				Vector<String> vecCategoryIds = new Vector<String>();
				while(iterator.hasNext())
					vecCategoryIds .add(iterator.next());
				int count =0;
				for(int i=0; i<vecCategoryIds.size(); i++)
				{
					Vector<ProductDO> vecOrderedProduct = hmCapturedInventoryFreeDelivery.get(vecCategoryIds.get(i));
					for(ProductDO objProductDO : vecOrderedProduct)
					{
//						DiscountDO temp = new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
						
						count ++;
						//here
						objProductDO.inventoryQty		=	0;	
						objProductDO.itemPrice  		= 	0;
						objProductDO.totalPrice 		= 	0.0;
						objProductDO.unitSellingPrice 	= 	0;
						objProductDO.invoiceAmount 		= 	0.0;
						objProductDO.LineNo 			= 	""+count;
						objProductDO.Discount 			=   0;//100;
						
					}
				}
				totalDiscount = totalDiscount/count;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						adapterForCapture = new CaptureInventaryAdapter(hmCapturedInventoryFreeDelivery);
						etDiscValue.setText(""+diffAmt.format(totalDiscount));
						evTotalValue.setText(diffAmt.format(totPrice));
					}
				});
			}
		}).start();
		
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		if(hmCapturedInventoryFreeDelivery != null && hmCapturedInventoryFreeDelivery.size() > 0)
			tvNoOrder.setVisibility(View.GONE);
		else
			tvNoOrder.setVisibility(View.VISIBLE);
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
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManFreeDeliveryAddItem.this);
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
		
		/*tvNoItemFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		vecCategory = new Vector<String>();
//		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
//			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
				vecCategory = new CategoriesDA().getAvailableCategory();
		final Button btnSearch = new Button(SalesManFreeDeliveryAddItem.this);
		etCategory.setTag(-1);
		etCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManFreeDeliveryAddItem.this, "Select Category", true);
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
						String strDesc = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDesc.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
					{
						adapter.refresh(vecTemp);
						lvPopupList.setVisibility(View.VISIBLE);
						tvNoItemFound.setVisibility(View.GONE);
					}
					else
					{
						lvPopupList.setVisibility(View.GONE);
						tvNoItemFound.setVisibility(View.VISIBLE);
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
					showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
				}
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<ProductDO>();
					else
						vecSearchedItemd.clear();
					orderedItems ="";
					orderedItemsList = "";
					if(hmCapturedInventoryFreeDelivery!=null)
					{
						Set<String> set = hmCapturedInventoryFreeDelivery.keySet();
						Iterator<String> iterator = set.iterator();
						Vector<String> vecCategoryIds = new Vector<String>();
						while(iterator.hasNext())
							vecCategoryIds.add(iterator.next());
						
						for(int i=0; i<vecCategoryIds.size(); i++)
						{
							Vector<ProductDO> vecOrderedProduct = hmCapturedInventoryFreeDelivery.get(vecCategoryIds.get(i));
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
					showLoader(getString(R.string.loading));
					
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
							//Need to change
							vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(null, catgId, orderedItemsList, null,hmUOMFactors, true, hmInventory);
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
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManFreeDeliveryAddItem.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManFreeDeliveryAddItem.this));
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
				Vector<ProductDO> veciItems= new Vector<ProductDO>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
				{
//					boolean isAvail = checkAvailibility(veciItems);
//					if(isAvail)
//					{
						tvNoOrder.setVisibility(View.GONE);
						if(hmCapturedInventoryFreeDelivery == null)
							hmCapturedInventoryFreeDelivery = new HashMap<String, Vector<ProductDO>>();
						
						for(int i=0; veciItems != null && i < veciItems.size(); i++)
						{
							ProductDO objProduct = veciItems.get(i);
							Vector<ProductDO> vecProducts = hmCapturedInventoryFreeDelivery.get(objProduct.CategoryId);
							if(vecProducts == null)
							{
								vecProducts = new Vector<ProductDO>(); 
								vecProducts.add(objProduct);
								hmCapturedInventoryFreeDelivery.put(objProduct.CategoryId, vecProducts);
							}
							else
								vecProducts.add(objProduct);
						}
				
						objAddNewSKUDialog.dismiss();
						
						calTotPrices(true);
//					}
//					else
//						showCustomDialog(SalesManTakeOrder.this, "Warning !", "Selected Items are not available in inventory.", "OK", null, "");
				}
				else
					showCustomDialog(SalesManFreeDeliveryAddItem.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	public class CaptureInventaryAdapter
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
				handleText(type, s);
			}

			@Override
			public void afterTextChanged(Editable s) 
			{
//				handleText(type, s);
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
				else
				{
					view = null;
				}
			}
		}
		
		private void handleText(String type, CharSequence s)
		{
			ProductDO objItem = null;
			if(view != null)
			{
				objItem = (ProductDO) view.getTag();
			}
			
			if(objItem != null)
			{
				objItem.preUnits = s.toString();
				objItem.totalCases = StringUtils.getFloat(objItem.preCases)+(StringUtils.getFloat(objItem.preUnits)/objItem.UnitsPerCases);
			
				if(!isInventoryAvail(objItem, hmInventory))
				{
					objItem.preUnits = "0";
					objItem.totalCases = StringUtils.getFloat(objItem.preCases);
					showToast("Entered quantity should not be greater than inventory quantity.");
					if(objItem.etUnits != null)
					{
						objItem.etUnits.setText("");    
						objItem.etUnits.setHint("0");
					}
				}
			}
		}
		
		public CaptureInventaryAdapter(HashMap<String, Vector<ProductDO>> hmItems)
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				
				for (String string : set) {
					vecCategoryIds.add(string);
				}
				getGroupView(llLayoutMiddle);
			}
		}

		@SuppressWarnings("deprecation")
		public View getChildView(int groupPosition) 
		{
			final LinearLayout llChildViews = new LinearLayout(getApplicationContext());
			llChildViews.setOrientation(1);
			for(int childPosition =0;childPosition<hmItems.get(vecCategoryIds.get(groupPosition)).size();childPosition++)
			{
				final ProductDO objProduct = hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				
				view = null;
				final LinearLayout convertView= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
				TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
				TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				final Button btnDelete		= (Button)convertView.findViewById(R.id.btnDelete);
				LinearLayout llClickToDownLoad			= (LinearLayout)convertView.findViewById(R.id.llClickToDownLoad);
				TextView evCases			= (TextView)convertView.findViewById(R.id.evCases);
				EditText evUnits			= (EditText)convertView.findViewById(R.id.evUnits);
				objProduct.etUnits			=  evUnits;
				tvHeaderText.setText(objProduct.SKU);
//				tvHeaderText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvDescription.setText(objProduct.Description);
				tvDescription.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				
				evCases.setTag(objProduct);
				evUnits.setTag(objProduct);
				
				evCases.setText(objProduct.UOM);
				if(objProduct.preUnits.equalsIgnoreCase("") || objProduct.preUnits.equalsIgnoreCase("0"))
				{
					objProduct.preUnits = objProduct.preUnits.toString();
					evUnits.setText("");
				}
				else
					evUnits.setText(objProduct.preUnits);
				
				setActionInViews(evUnits, true);
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
				
				tvHeaderText.setTag(childPosition);
				convertView.setTag(objProduct);
				
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(40 * px)));
				llClickToDownLoad.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View arg0) 
					{
					}
				});
				
				((LinearLayout)(btnDelete.getParent())).setTag(childPosition);
				btnDelete.setTag(groupPosition);
				btnDelete.setVisibility(View.GONE);
				btnDelete.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
		
						int groupPosition = StringUtils.getInt(v.getTag().toString());
						hmItems.get(vecCategoryIds.get(groupPosition)).remove(objProduct);
						TextView view= (TextView) ((LinearLayout)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(0)).getChildAt(1);
						if(view !=null)
						{
							((TextView)view).setText("("+ hmItems.get(vecCategoryIds.get(groupPosition)).size() +" Sub Products)");
						}
						if(hmItems.get(vecCategoryIds.get(groupPosition)).size() == 0)
						{
							((ImageView)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(1)).setImageResource(R.drawable.arro);
							((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(1)).setVisibility(View.GONE);
						}
						llChildViews.removeView(convertView);
//						if(hmItems != null)
//						adapterForCapture.refresh(hmItems);
						
						handleText("","");
	//					adapterForCapture.onGroupExpanded((Integer)v.getTag());
					}
				});
				setTypeFace(convertView);
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(40 * px)));
				llChildViews.addView(convertView);
				
			}
			return llChildViews;
		}


		public void getGroupView(LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle!=null)
				llLayoutMiddle.removeAllViews();
			for(int groupPosition = 0;groupPosition<vecCategoryIds.size();groupPosition++)
			{
				String strCategory = "";
				CategoryDO objCategoryDO = null;
				if(AppConstants.hmCateogories != null && vecCategoryIds != null)
					objCategoryDO = AppConstants.hmCateogories.get(vecCategoryIds.get(groupPosition));
				
				int childItemsSize = 0;
				
				if(vecCategoryIds != null)
					childItemsSize = hmItems.get(vecCategoryIds.get(groupPosition)).size();
				else 
					childItemsSize = hmItems.size();
				
				try 
				{
					if(objCategoryDO != null)
						strCategory =objCategoryDO.categoryName;
					else
						strCategory = vecCategoryIds.get(groupPosition);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					strCategory = "Others";
				}
				final LinearLayout convertView		=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llBottomLayout 		= (LinearLayout)convertView.findViewById(R.id.llBottomLayout);
				final LinearLayout llCode = (LinearLayout)convertView.findViewById(R.id.llCode);
				TextView tvCode 	= (TextView)convertView.findViewById(R.id.tvCode);
				TextView tvCases 	= (TextView)convertView.findViewById(R.id.tvCases);
				final ImageView ivArrow	= (ImageView)convertView.findViewById(R.id.ivArrow);
				TextView tvUnits 	= (TextView)convertView.findViewById(R.id.tvUnits);
				
			/*	tvCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvCases.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				tvUnits.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
				
				TextView tvTitleText	= (TextView)convertView.findViewById(R.id.tvInventryText);
				TextView tvNo			= (TextView)convertView.findViewById(R.id.tvNo);
//				tvNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//				tvTitleText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				
				if(childItemsSize > 0 && objCategoryDO != null)
				{
					tvNo.setText("("+ childItemsSize +" Sub Products)");
					tvTitleText.setText(objCategoryDO.categoryName);
				}
				else if(!strCategory.equalsIgnoreCase("") && childItemsSize > 0)
				{
					tvTitleText.setText(strCategory);
					tvNo.setText("("+childItemsSize+" Sub Products)");
				}
				else
					tvNo.setText("(0 Sub Products)");
				
				llBottomLayout.setTag(groupPosition);
				llBottomLayout.setVisibility(View.GONE);
				llBottomLayout.addView(getChildView(groupPosition));
				convertView.setTag("closed");
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if( vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString())) != null && vecCategoryIds.size() > 0 && hmItems.get(vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString()))).size()>0)
						{
							if(v.getTag().toString().equalsIgnoreCase("closed"))
							{
								ivArrow.setImageResource(R.drawable.arro2);
								v.setTag("open");
								if(llBottomLayout.getChildCount() > 0)
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
					}
				});
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						convertView.performClick();
					}
				}, 100);

				setTypeFace(convertView);
				llLayoutMiddle.addView(convertView);
			}
		}

		//Method to refresh the List View
		private void refresh(HashMap<String, Vector<ProductDO>> hmItems) 
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				if(llLayoutMiddle != null)
					getGroupView(llLayoutMiddle);
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			super.onBackPressed();
		}
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("validate"))
		{
			Intent intent = new Intent(SalesManFreeDeliveryAddItem.this, SalesManFreeDeliveryAddItem.class);
			intent.putExtra("name",""+getString(R.string.Recommended_Order) );
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("alreadyprinted"))
		{
			showLoader(getString(R.string.your_recommended_order_is_printing));
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
					showCustomDialog(SalesManFreeDeliveryAddItem.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 1000 && resultCode == 1000)
			finish();
		else if(requestCode == 200)
		{
			if(hmCapturedInventoryFreeDelivery != null && adapterForCapture != null)
			{
				adapterForCapture.refresh(hmCapturedInventoryFreeDelivery);
				calTotPrices(false);
				tvNoOrder.setVisibility(View.GONE);
			}
			else
				tvNoOrder.setVisibility(View.VISIBLE);
		}
		else if(resultCode == 20000)
    	{
			showCustomDialog(SalesManFreeDeliveryAddItem.this, getString(R.string.successful), getString(R.string.your_recommended_order_printed), getString(R.string.OK), null , "");
    	}
		else if(resultCode == 10000)
		{
			finish();
		}
	}
	
	private boolean getAddedItemAvailability(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isAvail = false;
		Set<String> set = hmCapturedInventoryFreeDelivery.keySet();
		for(String strKey : set)
		{
			Vector<ProductDO> vecOrderedProduct = hmCapturedInventoryFreeDelivery.get(strKey);
			if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
			{
				for(ProductDO objProductDO : vecOrderedProduct)
				{
					if(StringUtils.getFloat(objProductDO.preUnits) > 0)
					{
						isAvail = true;
						break;
					}
				}
			}
		}
		return isAvail;
	}
	
	private void showOptionDialog()
	{
		Vector<String> vecStrings = new CommonDA().getFreeDeliveryResion();
		CustomBuilder builder = new CustomBuilder(SalesManFreeDeliveryAddItem.this, "Select Reason", true);
		builder.setSingleChoiceItems(vecStrings, "", new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				String str = (String) selectedObject;
				etRegionCode.setText(str);
				builder.dismiss();
		    }
		}); 
		builder.show();
	}
}