package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.Vector;
@SuppressWarnings("serial")
public class CustomerSurveyDONew implements Serializable
{
	public String serveyId = "";
	public String SurveyAppId = "";
	public String SurveyResultAppId="";
	public String userCode = "";
	public String clientCode = "";
	public String date = "";
	public String lattitude = "";
	public String longitude = "";
	public String userRole = "";
	public String journeyCode = "";
	public String visitCode = "";
	public String resultServeyId = "";
	public String status = "";
	public String isUploaded = "";
	
	public Vector<SurveyQuestionDONew> srveyQues ;
	
}
