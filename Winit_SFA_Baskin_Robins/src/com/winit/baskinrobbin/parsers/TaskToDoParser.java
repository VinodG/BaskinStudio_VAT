package com.winit.baskinrobbin.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.TaskToDoDA;
import com.winit.baskinrobbin.salesman.dataobject.TaskToDoDO;

public class TaskToDoParser extends BaseHandler
{
	private String  Status = "";
	private String count= "";
	private Vector<TaskToDoDO> vecToDoDOs;
	private TaskToDoDO taskToDoDO;
	private boolean isStatus = false;
	public TaskToDoParser(Context context) 
	{
		super(context);
	}
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Tasks"))
		{
			vecToDoDOs = new Vector<TaskToDoDO>();
		}
		else if(localName.equalsIgnoreCase("TaskDco"))
		{
			isStatus = true;
			taskToDoDO = new TaskToDoDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(Preference.GetAllTask, currentValue.toString().toString());
			preference.saveStringInPreference(Preference.GetAllAcknowledgedTask, currentValue.toString().toString());
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("TaskId"))
		{
			taskToDoDO.TaskID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("RouteCustomerId"))
		{
			taskToDoDO.routeCustomerID = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TaskName"))
		{
			taskToDoDO.TaskName = currentValue.toString();
			if(taskToDoDO.TaskName.equalsIgnoreCase("Capture Self Photo"))
				taskToDoDO.TaskDesc = AppConstants.Task1;
			else if(taskToDoDO.TaskName.equalsIgnoreCase("Competitor Promotions"))
				taskToDoDO.TaskDesc = AppConstants.Task2;
			else if(taskToDoDO.TaskName.equalsIgnoreCase("In store - Consumer Behaviour Survey"))
				taskToDoDO.TaskDesc = AppConstants.Task3;
				
		}
		else if(localName.equalsIgnoreCase("TaskAssignOn"))
		{
			taskToDoDO.TaskDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status") && isStatus)
		{
			taskToDoDO.Status = /*currentValue.toString();*/"P";
		}
		else if(localName.equalsIgnoreCase("IsAcknowledge"))
		{
			taskToDoDO.IsAcknowledge = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AcknowledgeOn"))
		{
			taskToDoDO.AcknowledgeOn = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Rating"))
		{
			taskToDoDO.Rating = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SITE"))
		{
			taskToDoDO.SITE = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TaskDco"))
		{
			vecToDoDOs.add(taskToDoDO);
		}
		else if(localName.equalsIgnoreCase("Tasks"))
		{
			new TaskToDoDA().insertTaskToDo(vecToDoDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public String getChengedPassword()
	{
		return Status;
	}
	public String getId()
	{
		return count;
	}
}
