package com.winit.baskinrobbin.salesman.dataobject;

import java.util.Vector;

@SuppressWarnings("serial")
public class SurveyQuestionDONew extends BaseObject
{
	public String SurveyResultAppId = "";
	public String serveyresultId = "";
	public String assignmentType = "";
	public String questionId = "";
	public String question = "";
	public String optionId = "";
	public String option = "";
	public String comments = "";
	public String status = "";
	public String isUploaded = "";
	
	public Vector<SurveryOptionDO> srveyOpt ;
}
