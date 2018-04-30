package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.Vector;

@SuppressWarnings("serial")
public class MaintainStateDO implements Serializable 
{
	public Vector<OrderDO> vecOrderDO 			= 	new Vector<OrderDO>();
	public Vector<PostPaymentDO> vecPaymentDO 	= 	new Vector<PostPaymentDO>();
}	
