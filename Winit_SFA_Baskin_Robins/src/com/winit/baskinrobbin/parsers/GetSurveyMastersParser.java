package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveryOptionDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyAssignmentDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyAssignmentTypeDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SurveyQuestionDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.SynLogDA;
import com.winit.baskinrobbin.salesman.dataobject.SurveryOptionDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyAssignmentDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyAssignmentTypeDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyQuestionDO;
import com.winit.baskinrobbin.salesman.dataobject.SynLogDO;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class GetSurveyMastersParser extends BaseHandler
{

	
	private Vector<SurveyAssignmentTypeDO> vecsAssignmentTypeDO;
	private SurveyAssignmentTypeDO objAssignmentTypeDO;
	
	private Vector<SurveyAssignmentDO> vecsSurveyAssignmentDO;
	private SurveyAssignmentDO objSurveyAssignmentDO;
	
	private Vector<SurveyQuestionDO> vecsSurveyQuestionDO;
	private SurveyQuestionDO objSurveyQuestionDO;
	
	private Vector<SurveryOptionDO> vecsSurveryOptionDO;
	private SurveryOptionDO objSurveryOptionDO;
	
	private final int AssignmentType= 2, SurveyAssignment= 4,SurveyQuestionDO= 5, SurveryOption= 7;

	private int SELECTED_TYPE=0;
	private String empNo;
	SynLogDO synLogDO = new SynLogDO();
	public GetSurveyMastersParser(Context context, String strEmpNo) 
	{
		super(context); 
		this.empNo = strEmpNo;
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("SurveyAssignmentTypes"))
		{
			vecsAssignmentTypeDO = new Vector<SurveyAssignmentTypeDO>();
		}
		else if(localName.equalsIgnoreCase("SurveyAssignmentTypeDco"))
		{
			SELECTED_TYPE = AssignmentType;
			objAssignmentTypeDO = new SurveyAssignmentTypeDO();
		}
		else if(localName.equalsIgnoreCase("SurveyAssignments"))
		{
			vecsSurveyAssignmentDO = new Vector<SurveyAssignmentDO>();
		}
		else if(localName.equalsIgnoreCase("SurveyAssignmentDco"))
		{
			SELECTED_TYPE = SurveyAssignment;
			objSurveyAssignmentDO = new SurveyAssignmentDO();
		}
		
		else if(localName.equalsIgnoreCase("SurveyQuestions"))
		{
			vecsSurveyQuestionDO = new Vector<SurveyQuestionDO>();
		}
		else if(localName.equalsIgnoreCase("SurveyQuestionDco"))
		{
			SELECTED_TYPE = SurveyQuestionDO;
			objSurveyQuestionDO = new SurveyQuestionDO();
		}
		
		else if(localName.equalsIgnoreCase("SurveyOptions"))
		{
			vecsSurveryOptionDO = new Vector<SurveryOptionDO>();
		}
		else if(localName.equalsIgnoreCase("SurveyOptionDco"))
		{
			SELECTED_TYPE = SurveryOption;
			objSurveryOptionDO = new SurveryOptionDO();
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ServerTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMJ =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime")  && SELECTED_TYPE <= 0)
		{
			synLogDO.UPMT =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status")  && SELECTED_TYPE <= 0)
		{
			synLogDO.action =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GetSurveyMastersResult"))
		{
			insertSurvayDetailsData( vecsAssignmentTypeDO, vecsSurveyAssignmentDO, vecsSurveyQuestionDO, vecsSurveryOptionDO);
			synLogDO.entity = ServiceURLs.GetSurveyMasters;
			new SynLogDA().insertSynchLog(synLogDO);
		}
		else
		{
			switch (SELECTED_TYPE)
			{
				case AssignmentType:
					if(localName.equalsIgnoreCase("Code"))
					{
						objAssignmentTypeDO.code = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("Type"))
					{
						objAssignmentTypeDO.type = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("SurveyAssignmentTypeDco"))
					{
						vecsAssignmentTypeDO.add(objAssignmentTypeDO);
					}
					break;
					
				case SurveyAssignment:
					
					if(localName.equalsIgnoreCase("QuestionId"))
					{
						objSurveyAssignmentDO.questionId = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("AssignmentType"))
					{
						objSurveyAssignmentDO.assignmentType = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("Code"))
					{
						objSurveyAssignmentDO.code = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("SurveryAssignmentId"))
					{
						objSurveyAssignmentDO.surveyAssignmentId = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("SurveyAssignmentDco"))
					{
						vecsSurveyAssignmentDO.add(objSurveyAssignmentDO);
					}
					break;
					
			case SurveyQuestionDO:
					
					if(localName.equalsIgnoreCase("QuestionId"))
					{
						objSurveyQuestionDO.questionId = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("Question"))
					{
						objSurveyQuestionDO.question = currentValue.toString();
					}
					else if(localName.equalsIgnoreCase("CreatedBy"))
					{
					}
					else if(localName.equalsIgnoreCase("ModifiedBy"))
					{
					}
					else if(localName.equalsIgnoreCase("SurveyQuestionDco"))
					{
						vecsSurveyQuestionDO.add(objSurveyQuestionDO);
					}
					break;
					
			case SurveryOption:
				
				if(localName.equalsIgnoreCase("QuestionId"))
				{
					objSurveryOptionDO.questionId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("OptionId"))
				{
					objSurveryOptionDO.optionId = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Option"))
				{
					objSurveryOptionDO.option = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("SurveyOptionDco"))
				{
					vecsSurveryOptionDO.add(objSurveryOptionDO);
				}
				break;
					
				default:
					break;
			}
		}
	}
	
	private boolean insertSurvayDetailsData(Vector<SurveyAssignmentTypeDO> vecsAssignmentTypeDO,Vector<SurveyAssignmentDO> vecsSurveyAssignmentDO,
			Vector<SurveyQuestionDO> vecsSurveyQuestionDO,Vector<SurveryOptionDO> vecsSurveryOptionDO) 
	{
		try 
		{
			if(vecsAssignmentTypeDO!=null&&vecsAssignmentTypeDO.size()>0)
				new SurveyAssignmentTypeDA().insertSurveyAssignmentType(vecsAssignmentTypeDO);
			if(vecsSurveyAssignmentDO!=null&&vecsSurveyAssignmentDO.size()>0)
				new SurveyAssignmentDA().insertSurveyAssignment(vecsSurveyAssignmentDO);
			if(vecsSurveyQuestionDO!=null&&vecsSurveyQuestionDO.size()>0)
				new SurveyQuestionDA().insertSurveyQuestion(vecsSurveyQuestionDO);
			if(vecsSurveryOptionDO!=null&&vecsSurveryOptionDO.size()>0)
				new SurveryOptionDA().insertSurveyOption(vecsSurveryOptionDO);
		} 
		catch (Exception e) 
		{
			return false;
		}
		return true;
		
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	

}
