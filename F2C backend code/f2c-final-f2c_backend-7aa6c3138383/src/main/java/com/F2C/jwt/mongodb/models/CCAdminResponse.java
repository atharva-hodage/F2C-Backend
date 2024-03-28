package com.F2C.jwt.mongodb.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
@Data
public class CCAdminResponse {
	
	String reqForQCCC; // this is for request id
	String farmerName;
	String CCEmployeeName;
	String QCAssignedName;
	
	String handledCC;
	String ccAvailable;
String notificationMessage; // Add this field for notification message

	String handledQC;
	String qcAvailable;
	
	//crop details verification
String cropName;
String cropSubType;
Long quantityAvailable;
Boolean organicInOrganic;
	
private Date qcAssignedDate; // Add this field to store the QC assigned date
	private List<QualityCheckApprovalForm> approvalForms = new ArrayList<>();
	
	
	
}


