package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class TransferInoutDO implements Serializable
{
	public ArrayList<DeliveryAgentOrderDetailDco> vecOrderDetailDcos;
	public String InventoryUID   = "";
	public String fromEmpNo		 = "";
	public String toEmpNo 		 = "";
	public String trnsferType    = "";
	public String transferStatus = "";
	public String sourceVNO		 = "";
	public String destVNO		 = "";
	public String Date		 	 = "";
	public String customerName   = "";
	public String sourceOrderID  = "";	
	public String destOrderID    = "";	
}
