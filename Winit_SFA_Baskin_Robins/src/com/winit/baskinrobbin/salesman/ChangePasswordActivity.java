package com.winit.baskinrobbin.salesman;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class ChangePasswordActivity extends BaseActivity
{
	private Button btnPassChange,btnCancel;
	private TextView tvDownloadPlanogramHeader,tvConfirmPasswordLabel,tvNewPasswordLabel,tvOldPasswordLabel;
	private EditText et_OldPassword,et_NewPassword,et_ConfirmPassword;
	private LinearLayout llChangePassword;
	private String strOldPassword, strNewPassword, strConfirmPassword, strUserName = "";
	
	@Override
	public void initialize() 
	{
		//inflate the change-password layout
		llChangePassword			=	(LinearLayout)inflater.inflate(R.layout.changepassword,null);
		llBody.addView(llChangePassword,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		//getting current user's UserID
		strUserName = preference.getStringFromPreference(Preference.USER_NAME, "");
		
		intialiseControls();
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		/*tvDownloadPlanogramHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvConfirmPasswordLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNewPasswordLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOldPasswordLabel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnPassChange.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCancel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnPassChange.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				strOldPassword		=	et_OldPassword.getText().toString();
				strNewPassword		=	et_NewPassword.getText().toString();
				strConfirmPassword	=	et_ConfirmPassword.getText().toString();
				
				if(strOldPassword.equals("")||strNewPassword.equals("")||strConfirmPassword.equals(""))
				{
					showCustomDialog(ChangePasswordActivity.this, getResources().getString(R.string.warning), "Please enter all the fields.", getResources().getString(R.string.OK), null,"");
					et_OldPassword.requestFocus();
				}
				else if(!strNewPassword.equals(strConfirmPassword))
				{
					showCustomDialog(ChangePasswordActivity.this, getResources().getString(R.string.warning), "Please enter same password.", getResources().getString(R.string.OK), null,"");
					et_ConfirmPassword.requestFocus();
				}
				else
				{
//					Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_changed_successfully_),Toast.LENGTH_SHORT).show();
//				}
					showLoader(getResources().getString(R.string.loading));
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							final boolean responce = new ConnectionHelper(ChangePasswordActivity.this).sendRequest(ChangePasswordActivity.this,BuildXMLRequest.changePasswordRequest(preference.getStringFromPreference(Preference.USER_ID, ""), strOldPassword, strNewPassword), ServiceURLs.CHANGE_PASSWORD, preference);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									if(responce)
										showCustomDialog(ChangePasswordActivity.this,getString(R.string.successful), "Password changed successfully.", "OK", null, "Change_successfully");
//										Toast.makeText(ChangePasswordActivity.this, "Password changed successfully.",Toast.LENGTH_SHORT).show();
									else
										showCustomDialog(ChangePasswordActivity.this, getString(R.string.warning), "Unable to change Password. Please try again later.", "OK", null, "Change_Unable");
//										Toast.makeText(ChangePasswordActivity.this, "Unable to change Password. Please try again later.",Toast.LENGTH_SHORT).show();
								}
							});
						}
					}).start();
				}
			}	
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
				overridePendingTransition(R.anim.hold, R.anim.push_up_out);
			}
		});
		
		setTypeFace(llChangePassword);
	}
	public void onButtonYesClick(String from) 
	{
		if(from.equalsIgnoreCase("Change_successfully"))
			finish();
		else
			super.onButtonYesClick(from);	
	};
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_GOTO_SETTINGS_FINISH);
		registerReceiver(FinishReceiver, filter);
	}
	BroadcastReceiver FinishReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equalsIgnoreCase(AppConstants.ACTION_GOTO_SETTINGS_FINISH))
				finish();
		}
	};
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unregisterReceiver(FinishReceiver);
	}
	/** initializing all the Controls  of ChangePasswordActivity **/
	public void intialiseControls()
	{
		// all the fields from the changepassword.xml is taken here
		tvDownloadPlanogramHeader	=	(TextView)llChangePassword.findViewById(R.id.tvDownloadPlanogramHeader);
		tvConfirmPasswordLabel		=	(TextView)llChangePassword.findViewById(R.id.tvConfirmPasswordLabel);
		tvOldPasswordLabel			=	(TextView)llChangePassword.findViewById(R.id.tvOldPasswordLabel);
		tvNewPasswordLabel			=	(TextView)llChangePassword.findViewById(R.id.tvNewPasswordLabel);
		et_OldPassword				=	(EditText)llChangePassword.findViewById(R.id.etOldPassword);
		et_NewPassword				=	(EditText)llChangePassword.findViewById(R.id.etNewPassword1);
		et_ConfirmPassword			=	(EditText)llChangePassword.findViewById(R.id.etConfirmPassword);
		btnPassChange 				=	(Button)llChangePassword.findViewById(R.id.btnPassChange);
		btnCancel					=	(Button)llChangePassword.findViewById(R.id.btnCancel);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
}
