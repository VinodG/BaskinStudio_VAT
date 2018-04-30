package com.winit.baskinrobbin.salesman.dataobject;

import java.util.ArrayList;

import android.widget.Switch;

@SuppressWarnings("serial")
public class LoadRequestDO extends BaseComparableDO {
	public String MovementCode = "";
	public String PreMovementCode = "";
	public String AppMovementId = "";
	public String OrgCode = "";
	public String UserCode = "";
	public String WHKeeperCode = "";
	public String CurrencyCode = "";
	public String JourneyCode = "";
	public String MovementDate = "";
	public String MovementNote = "";
	public String MovementType = "";
	public String SourceVehicleCode = "";
	public String DestinationVehicleCode = "";
	public String Status = "";
	public String VisitID = "";
	public String MovementStatus = "";
	public String CreatedOn = "";
	public String ApproveByCode = "";
	public String ApprovedDate = "";
	public String JDETRXNumber = "";
	public String ISStampDate = "";
	public String ISFromPC = "";
	public String OperatorCode = "";
	public String IsDummyCount = "";
	public float Amount = 0;
	public String ModifiedDate = "";
	public String ModifiedTime = "";
	public String PushedOn = "";
	public String ModifiedOn = "";
	public String ProductType = "";
	public String customerSite = "";
	public String IsVarified = "";
	public String temStatus = "History";
	public static final String STATUS_PENDING = "Pending";
	public static final String STATUS_HISTORY = "History";
	public static final String STATUS_EBSAPPROVED = "Approved from EBS";
	
	public double geoLatitude = 0.0;
    public double geoLongitude = 0.0;

    
    public static final String STATUS_APPROVED_FROM_ERP = "Approved from EBS";
    public static final String STATUS_PENDING_FROM_ERP = "Pending from EBS";
    public static final String STATUS_SHIPPED = "Shipped";
    public static final String STATUS_PENDING_MW = "Pending";
    public static final String STATUS_REJECTED = "Rejected";
    
    public static final int MOVEMENT_STATUS_PENDING_FROM_ERP = 102;
    public static final int MOVEMENT_STATUS_APPROVED_FROM_EBS = 103;
    public static final int MOVEMENT_STATUS_APPROVED_FROM_ERP = 101;
	public static final int MOVEMENT_STATUS_APPROVED_VERIFY = 100;
	public static final int MOVEMENT_STATUS_APPROVED		= 99;
	public static final int MOVEMENT_STATUS_PENDING			= 2;
	public static final int MOVEMENT_STATUS_SAVED			= 1;
	public static final int MOVEMENT_STATUS_CANCELLED			= -110;
	public static final int MOVEMENT_STATUS_REJECTED		=-13;
	
	public ArrayList<LoadRequestDetailDO> vecItems = new ArrayList<LoadRequestDetailDO>();

	public  String getMovementStatus(int status) {
		switch (status) {
		case 100:
			MovementStatus =	STATUS_SHIPPED;
			break;
		/*case 99:
			MovementStatus =	"Pending from EBS";	
			break;*/
		case -13:
			MovementStatus =	STATUS_REJECTED;
			break;
		case 99:
			MovementStatus =	STATUS_PENDING_MW;
			break;
		case 101:
			MovementStatus =	STATUS_APPROVED_FROM_ERP;
			break;
		case 102:
			MovementStatus =	STATUS_PENDING_FROM_ERP;
			break;
		case 103:
			MovementStatus =	STATUS_APPROVED_FROM_ERP;
			break;
		default:
			MovementStatus=STATUS_SHIPPED;
			break;
		}
		return MovementStatus;

	}
}
