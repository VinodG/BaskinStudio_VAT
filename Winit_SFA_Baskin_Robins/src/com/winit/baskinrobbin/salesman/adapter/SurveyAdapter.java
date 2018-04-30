package com.winit.baskinrobbin.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;

public class SurveyAdapter extends BaseAdapter
{
	Context context;
	Vector<SurveyQuestionDONew> srveyQues;

	public SurveyAdapter(Context context, Vector<SurveyQuestionDONew> srveyQues) 
	{
		this.context = context;
		this.srveyQues = srveyQues;
	}
	public Vector<SurveyQuestionDONew> getResult() 
	{
		return srveyQues;
	}

	@Override
	public int getCount() 
	{
		return srveyQues.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
			convertView	= ((Activity) context).getLayoutInflater().inflate(R.layout.consumerservey_que_cell, null);
		
			
			TextView tvQuesNo 		= (TextView) convertView.findViewById(R.id.tvQuesNo);
			TextView tvQues 		= (TextView) convertView.findViewById(R.id.tvQues);
			final LinearLayout gvAnswer = (LinearLayout) convertView.findViewById(R.id.gvAnswer);
			final EditText etAnswer 			= (EditText) convertView.findViewById(R.id.etAnswer);
//			etAnswer.setEnabled(true);
//			etAnswer.setFocusable(true);
			tvQues.setText(srveyQues.get(position).question);
			int quesNo = position+1;
			tvQuesNo.setText(""+quesNo);
			int temp = srveyQues.get(position).srveyOpt.size();
			if(temp%2==0)
				temp = temp/2;
			else
				temp = (temp+1)/2;
//			etAnswer.requestFocus();
			
//			(srveyQues.get(position).comments = 
			if(srveyQues.get(position).comments!=null&&!srveyQues.get(position).comments.equalsIgnoreCase(""))
				{
					etAnswer.setText(srveyQues.get(position).comments);
					etAnswer.setSelection(etAnswer.length());
				
				}
			etAnswer.addTextChangedListener(new TextWatcher() 
			{
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
//					 srveyQues.get(position).comments = (String) s;
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) 
				{
					
				}
				
				@Override
				public void afterTextChanged(Editable s) 
				{
					 srveyQues.get(position).comments = s.toString();
					
				}
			});
			/*etAnswer.setOnFocusChangeListener(new OnFocusChangeListener() 
			{
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) 
				{
					if(!hasFocus)
					{
						if(((EditText) v).getText().toString()!=null&&!((EditText) v).getText().toString().equalsIgnoreCase(""))
						{
							srveyQues.get(position).comments =  ((EditText) v).getText().toString();
						}
						else
						{
							srveyQues.get(position).comments ="";
						}
					}
					
				}
			});*/
			for (int i = 0; i < temp; i++) 
			{
				LinearLayout llAnsOption 		=  (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.survey_answer_cell, null);
				TextView tvAns1 				= (TextView) llAnsOption.findViewById(R.id.tvAns1);
				TextView tvAns2 				= (TextView) llAnsOption.findViewById(R.id.tvAns2);
				ImageView ivAns1 				= (ImageView) llAnsOption.findViewById(R.id.ivAns1);
				ImageView ivAns2 				= (ImageView) llAnsOption.findViewById(R.id.ivAns2);
				tvAns1.setText( srveyQues.get(position).srveyOpt.get(i*2).option);
				if(srveyQues.get(position).srveyOpt.get(i*2).optionId.equalsIgnoreCase(srveyQues.get(position).optionId))
				{
					ivAns1.setImageResource(R.drawable.check_box_selected);
				}
				else
				{
					ivAns1.setImageResource(R.drawable.check_box);
				}
				if((i+1)*2 >srveyQues.get(position).srveyOpt.size())
					 llAnsOption.findViewById(R.id.llOption2).setVisibility(View.GONE);
				else
				{
					tvAns2.setText( srveyQues.get(position).srveyOpt.get(i*2+1).option);
					llAnsOption.findViewById(R.id.llOption2).setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{
							UnselctAllView(gvAnswer, v,	position);
						}
						
					});
					if(srveyQues.get(position).srveyOpt.get(i*2+1).optionId.equalsIgnoreCase(srveyQues.get(position).optionId))
					{
						ivAns2.setImageResource(R.drawable.check_box_selected);
					}
					else
					{
						ivAns2.setImageResource(R.drawable.check_box);
					}
	
				}
				llAnsOption.findViewById(R.id.llOption1).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						UnselctAllView(gvAnswer, v, position);
					}
				});
				
				gvAnswer.addView(llAnsOption);
			}
			
			((BaseActivity)context).setTypeFace((ViewGroup) convertView);
		return convertView;
	}
	private void UnselctAllView(LinearLayout gvAnswer, View v, int position) 
	{
		for (int j = 0; j < gvAnswer.getChildCount(); j++) 
		{
			LinearLayout llView = ((LinearLayout) gvAnswer.getChildAt(j));
			for (int i = 0; i < llView.getChildCount(); i++) 
			{
				LinearLayout llOpt = (LinearLayout) llView.getChildAt(i);
				ImageView ivAns 	= (ImageView) llOpt.getChildAt(0);
				if(llOpt==v)
				{
					 ivAns.setImageResource(R.drawable.check_box_selected);
					 srveyQues.get(position).optionId 	=  srveyQues.get(position).srveyOpt.get((2*j)+i).optionId;
					 srveyQues.get(position).option		=  srveyQues.get(position).srveyOpt.get((2*j)+i).option;
				}
				else
					ivAns.setImageResource(R.drawable.check_box);
			}
			
			
		}
		
	}

}
