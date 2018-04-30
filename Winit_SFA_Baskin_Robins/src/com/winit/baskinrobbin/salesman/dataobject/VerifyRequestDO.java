package com.winit.baskinrobbin.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class VerifyRequestDO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String movementCode 		= "";
	public String movementType		= "";
	public String movementStatus 	= "";
	public String logisticSignature = "";
	public String salesmanSignature = "";
	public ArrayList<VanLoadDO> vecVanLodDOs;
}
