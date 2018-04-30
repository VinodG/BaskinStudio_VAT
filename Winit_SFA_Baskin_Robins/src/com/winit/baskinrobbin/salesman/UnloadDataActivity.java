package com.winit.baskinrobbin.salesman;

import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.CustomBuilder;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CommonDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.VehicleDA;
import com.winit.baskinrobbin.salesman.dataobject.UnUploadedDataDO;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;

public class UnloadDataActivity extends BaseActivity
{
	private LinearLayout llUnloadDataScreen, llTitleView;
	private TextView tvListViewHeader, tvSelectType, tvTypeValue, tvOrderId, tvStatus, tvNoRecorFound; 
	private ListView lvMiddle;
	private UnloadDataAdapter adapter;
	private Button btnStockUnload;
	private final String STR_SALES_ORDER = "Sales Order";
	private final String STR_FREE_DELIVERY_ORDER = "Free Delivery Order";
	private final String STR_ADVANCE_ORDER = "Advance Order";
	private final String STR_NEW_CUSTOMER = "New Customers";
	private final String STR_PAYMENTS = "Payments";
	private final String STR_AGENT_ORDER_DETAILAS = "Agent Order Details";
	private final String STR_JOURNEY_LOGS = "Journey Logs";
	private CommonDA commonDA ;
	
	@Override
	public void initialize() 
	{
		llUnloadDataScreen = (LinearLayout)inflater.inflate(R.layout.unload_data_screen, null);
		llBody.addView(llUnloadDataScreen, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	
		commonDA = new CommonDA();
		initializeControles();
		
		adapter = new UnloadDataAdapter(new Vector<UnUploadedDataDO>());
		lvMiddle.setAdapter(adapter);
		lvMiddle.setDividerHeight(0);
		tvTypeValue.setText(STR_SALES_ORDER);
		loadDataByType(STR_SALES_ORDER);
		
		tvTypeValue.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				showTypeOptionDialog(tvTypeValue);
			}
		});
		
		btnStockUnload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
				Toast.makeText(UnloadDataActivity.this, "Uploading", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
		setTypeFace(llUnloadDataScreen);
	}
	
	private void initializeControles()
	{
		lvMiddle		 = (ListView)llUnloadDataScreen.findViewById(R.id.lvMiddle);
		tvListViewHeader = (TextView)llUnloadDataScreen.findViewById(R.id.tvListViewHeader);
		tvSelectType	 = (TextView)llUnloadDataScreen.findViewById(R.id.tvSelectType);
		tvTypeValue		 = (TextView)llUnloadDataScreen.findViewById(R.id.tvTypeValue);
		tvOrderId	     = (TextView)llUnloadDataScreen.findViewById(R.id.tvOrderId);
		tvStatus		 = (TextView)llUnloadDataScreen.findViewById(R.id.tvStatus);
		tvNoRecorFound	 = (TextView)llUnloadDataScreen.findViewById(R.id.tvNoRecorFound);
		llTitleView		 = (LinearLayout)llUnloadDataScreen.findViewById(R.id.llTitleView);
		btnStockUnload	 = (Button)llUnloadDataScreen.findViewById(R.id.btnStockUnload);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	private Vector<UnUploadedDataDO> vecUploadedDataDOs_New;
	private String strOrderIdType = "";
	private void loadDataByType(final String type)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				showLoader("Please wait...");
				if(type.equalsIgnoreCase(STR_SALES_ORDER))
				{
					strOrderIdType = "Order Id";
					vecUploadedDataDOs_New = commonDA.getAllSalesOrderUnupload(preference.getStringFromPreference(Preference.EMP_NO, ""));
				}
				else if(type.equalsIgnoreCase(STR_ADVANCE_ORDER))
				{
					strOrderIdType = "Order Id";
					vecUploadedDataDOs_New = commonDA.getAllAdvanceDeliveryUnuploadData(preference.getStringFromPreference(Preference.EMP_NO, ""));
				}
				else if(type.equalsIgnoreCase(STR_FREE_DELIVERY_ORDER))
				{
					strOrderIdType = "Order Id";
					vecUploadedDataDOs_New = commonDA.getAllFreeDeliveryUnuploadData(preference.getStringFromPreference(Preference.EMP_NO, ""));
				}
				else if(type.equalsIgnoreCase(STR_PAYMENTS))
				{
					strOrderIdType = "Receipt No.";
					vecUploadedDataDOs_New = commonDA.getAllPaymentsUnload(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getOrderPostDate());
				}
				else if(type.equalsIgnoreCase(STR_AGENT_ORDER_DETAILAS))
				{
					strOrderIdType = "Salesman Inventory Id";
					vecUploadedDataDOs_New = new VehicleDA().getAllItemUnUpload(CalendarUtils.getOrderPostDate());
				}
				else if(type.equalsIgnoreCase(STR_JOURNEY_LOGS))
				{
					strOrderIdType = "Customer Site Id";
					vecUploadedDataDOs_New = new CustomerDetailsDA().getJournyLogUnUpload(preference.getStringFromPreference(Preference.EMP_NO, ""),CalendarUtils.getOrderPostDate());
				}
				else if(type.equalsIgnoreCase(STR_NEW_CUSTOMER))
				{
					strOrderIdType = "Customer Site Id";
					vecUploadedDataDOs_New = commonDA.getNewCustomerUnUpload();
				}
				
				runOnUiThread(new Runnable()  {
					
					@Override
					public void run() {
						hideLoader();
						if(vecUploadedDataDOs_New != null && vecUploadedDataDOs_New.size()>0)
						{
							if(type.equalsIgnoreCase(STR_ADVANCE_ORDER))
								adapter.refreshList(vecUploadedDataDOs_New, true);
							else
								adapter.refreshList(vecUploadedDataDOs_New);
							
							tvOrderId.setText(strOrderIdType);
							
							tvNoRecorFound.setVisibility(View.GONE);
							lvMiddle.setVisibility(View.VISIBLE);
							llTitleView.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoRecorFound.setVisibility(View.VISIBLE);
							lvMiddle.setVisibility(View.GONE);
							llTitleView.setVisibility(View.GONE);
						}
					}
				});
			}
		}).start();
	}
	
	private void showTypeOptionDialog(final TextView tvTextView)
	{
		String strType = null;
		if(tvTextView.getTag() != null)
			strType = (String) tvTextView.getTag();
		
		CustomBuilder builder = new CustomBuilder(UnloadDataActivity.this, "Select Employee", true);
		builder.setSingleChoiceItems(getTypeStrings(), strType, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				String strType = (String) selectedObject;
				tvTextView.setText(strType);
				tvTextView.setTag(strType);
				loadDataByType(strType);
				builder.dismiss();
		    }
		}); 
		builder.show();
	}
	
	private Vector<String> getTypeStrings()
	{
		Vector<String> vecStrings = new Vector<String>();
		String[] type = getResources().getStringArray(R.array.type);
		for(int i = 0; i < type.length; i++)
		{
			vecStrings.add(type[i]);
		}
		return vecStrings;
	}
	
	private class UnloadDataAdapter extends BaseAdapter
	{
		private Vector<UnUploadedDataDO> vecUploadedDataDOs;
		private boolean isAdvanceSalesOrder = false;
		public UnloadDataAdapter(Vector<UnUploadedDataDO> vecUploadedDataDOs)
		{
			this.vecUploadedDataDOs = vecUploadedDataDOs;
		}
		public UnloadDataAdapter(Vector<UnUploadedDataDO> vecUploadedDataDOs, boolean isAdvanceSalesOrder)
		{
			this.vecUploadedDataDOs = vecUploadedDataDOs;
			this.isAdvanceSalesOrder = isAdvanceSalesOrder;
		}
		
		public void refreshList(Vector<UnUploadedDataDO> vecUploadedDataDOs)
		{
			this.vecUploadedDataDOs = vecUploadedDataDOs;
			notifyDataSetChanged();
		}
		public void refreshList(Vector<UnUploadedDataDO> vecUploadedDataDOs, boolean isAdvanceSalesOrder)
		{
			this.vecUploadedDataDOs = vecUploadedDataDOs;
			this.isAdvanceSalesOrder = isAdvanceSalesOrder;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() 
		{
			return vecUploadedDataDOs.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView						=	(LinearLayout)inflater.inflate(R.layout.delivery_status_new, null);
			TextView tvOrderNo				=	(TextView)convertView.findViewById(R.id.tvOrderNoStatus);
			TextView tvSatus				=	(TextView)convertView.findViewById(R.id.tvSatusStatus);
			tvSatus.setVisibility(View.VISIBLE);
			
			final UnUploadedDataDO uploadedDataDO = vecUploadedDataDOs.get(position);
			if(isAdvanceSalesOrder && uploadedDataDO.status == 1)
				tvSatus.setText("Pending");
			else if(isAdvanceSalesOrder && uploadedDataDO.status == 2)
				tvSatus.setText("Success");
			else if(uploadedDataDO.status != 0)
				tvSatus.setText("Success");
			else	
				tvSatus.setText("Pending");
				
			tvOrderNo.setText(""+uploadedDataDO.strId);
			
			return convertView;
		}
	}
}
