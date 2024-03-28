package com.F2C.jwt.mongodb.models;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import lombok.Data;
@Data
public class QualityCheckApprovalForm {
	
	
	
	 @Id
private String id; //approveform id ---object id in mongodb
private String requestIdForm; //(request id fetch )
// Farm Information
private String farmLocation;
private double farmArea; // You can use appropriate units
// Crop Details //checkbox
private boolean cropTypes;
private boolean cropSubtype;
private boolean quantityAvailable;
private boolean organicOrInorganic;
// private List<byte[]> images;
private List<String> imageIds;
private List<String> docsIds;
//low medium high values
private String grainSize;
private String presenceOfDiscoloredGrains;
private String moistureContent;
private String aroma;
private String brokenGrains;
private String expectedTexture;
private String cropCondition;
private String weeviledGrains;
// Pesticide and Chemical Usage
// private List<String> pesticidesOrChemicals;
private boolean complianceWithSafetyRegulations;
private boolean artificialRipening;
// Harvesting and Handling
private Date harvestingDate;
// Certifications and Documents
// private String legalDocumentUrl; // URL to the uploaded document
// private List<String> imageUrls; // URLs to uploaded images
// Quality Checker's Information
// private User qualityChecker;
private Date dateOfInspection;
// private String qualityCheckerSignature;
// Final Decision
private boolean approvalStatus; // farmer side not reflecting
private String rejectionReason;
// Quality Checker's Recommendations
private String recommendedActions;
private String notesAndComments;
	
}
///////////////////////////////////////////////////////////


