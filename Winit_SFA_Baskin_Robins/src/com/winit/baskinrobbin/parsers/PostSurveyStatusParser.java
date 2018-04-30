package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyResultDA;
import com.winit.baskinrobbin.salesman.dataobject.CustomerSurveyDONew;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDONew;

public class PostSurveyStatusParser extends BaseHandler
{
	private Vector<CustomerSurveyDONew> vecsSurveyStatusDOs;
	private CustomerSurveyDONew surveyStatusDO;
	private Vector<SurveyQuestionDONew> vecsrveyQues;
	private SurveyQuestionDONew srveyquedo;
	private boolean issurveyStatus = false , isSurveyResult = false;
	private String Status,message,count;
	
	
	
	public PostSurveyStatusParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("SurveyStatusList"))
		{
			vecsSurveyStatusDOs = new Vector<CustomerSurveyDONew>();
		}
		else if(localName.equalsIgnoreCase("SurveyStatusDco"))
		{
			surveyStatusDO = new CustomerSurveyDONew();
			issurveyStatus = true;
		}
		else if(localName.equalsIgnoreCase("SurveyResultStatusDcos"))
		{
			surveyStatusDO.srveyQues = new Vector<SurveyQuestionDONew>();
		}
		else if(localName.equalsIgnoreCase("SurveyResultStatusDco"))
		{
			srveyquedo = new SurveyQuestionDONew();
			isSurveyResult = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		 if(localName.equalsIgnoreCase("SurveyId")) 
		{
			 surveyStatusDO.serveyId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SurveyAppId"))
		{
			surveyStatusDO.SurveyAppId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			if(issurveyStatus)
				surveyStatusDO.status = currentValue.toString();
			else if(isSurveyResult)
				srveyquedo.status = currentValue.toString();
			else
			{
				Status = currentValue.toString();
			}
				
		}
		else if(localName.equalsIgnoreCase("SurveyResultId"))
		{
			srveyquedo.serveyresultId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SurveyResultAppId"))
		{
			srveyquedo.SurveyResultAppId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SurveyResultStatusDco"))
		{
			surveyStatusDO.srveyQues.add(srveyquedo);
		}
		else if(localName.equalsIgnoreCase("SurveyStatusDco"))
		{
			vecsSurveyStatusDOs.add(surveyStatusDO);
		}
		else if(localName.equalsIgnoreCase("SurveyStatusList"))
		{
			updateDataBase(vecsSurveyStatusDOs);
		}
		 
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private void updateDataBase(Vector<CustomerSurveyDONew> vecsSurveyStatusDOs) 
	{
		
		new SurveyResultDA().updateSurvey(vecsSurveyStatusDOs);
	}
	
	
}
