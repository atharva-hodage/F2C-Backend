package com.F2C.jwt.mongodb.models;
import lombok.Data;
@Data
public class CCToQCReq {
	String requestId;
	String cropId;
	String farmerId;
	
	//This i want o be updated in that
	String farmerName;
	
	String farmerAddress;
	String farmerContact;
	String farmerEmail;
	// cc
	String assignedCCId;
	String handledCC;
	boolean isHandledByCC;
	
	//crop details verification
String cropName;
String cropSubType;
Long quantityAvailable;
Boolean organicInOrganic;
	
	// qc
	String assignedQCId;
	String handledQC;
	boolean isHandledByQC;
	
	public boolean getIsHandledByCC() {
		return isHandledByCC;
	}
	public boolean getIsHandledByQC() {
		return isHandledByQC;
	}
	public void setIsHandledByCC(boolean isHandledByCC) {
		this.isHandledByCC=isHandledByCC;
	}
	public void setIsHandledByQC(boolean isHandledByQC) {
		this.isHandledByQC=isHandledByQC;
	}
	
}

