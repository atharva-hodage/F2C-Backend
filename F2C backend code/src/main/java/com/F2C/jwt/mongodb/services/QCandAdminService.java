package com.F2C.jwt.mongodb.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

//import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.QualityCheckApprovalForm;
import com.F2C.jwt.mongodb.models.User;

@Service
public interface QCandAdminService {
	User changeCCAvailable(String ccId, String status);

	List<User> availableEmployees();

	User setEmptyRequestFieldCCQC(String userId);

	User assignCCEmployee(String userId, String requestId);

	List<CCAdminResponse> currentAllEmployeeStatus();

	List<CCToQCReq> getAllocatedFarmerRequestsForAdmin(String adminUserId);

//show all qc
	public List<User> getAllQualityCheckers();

//set QC available
	User setEmptyRequestFieldQC(String userId);

//to retrieve Quality Checkers by location and qcAvailable status.
	public List<User> findFreeQCsByAddress(String address);

//to assign QC to farmer and update data in farmers request
	public CCAdminResponse assignQCToFarmer(String requestId, String qcId);

//to view request on QC portal
	List<CCAdminResponse> getQCDashboardData(String qcID);

	public CCToQCReq viewRequestById(String requestId);

// 16
//to approve the farmers request on QC portal
// boolean approveRequest(String requestId);
	boolean saveQualityCheckApprovalForm(String qcId, String requestId, String farmLocation, Double farmArea,
			boolean cropTypes, boolean cropSubtype, boolean quantityAvailable, boolean organicOrInorganic,
			MultipartFile[] files, MultipartFile[] docsFiles, String grainSize, String presenceOfDiscoloredGrains,
			String moistureContent, String aroma, String brokenGrains, String expectedTexture, String cropCondition,
			String weeviledGrains, boolean complianceWithSafetyRegulations, boolean artificialRipening,
			Date harvestingDate, Date dateOfInspection, String rejectionReason, String recommendedActions);

//, String notesAndComments
// ?????????????? 2
	List<CCAdminResponse> getQualityCheckerResponsesWithApprovalForms(String userId);

	List<CCAdminResponse> getQualityCheckerApprovalForms(String requestId);

//ImageResponseForm getFormWithImages(String requestIdForm);
	List<ImageResponseForm> getFormsWithImages(String requestId);

//QualityCheckApprovalForm getQualityCheckApprovalForm(String requestId);
	byte[] getDocumentPDF(String requestId);
    void sendNotificationToQC(String userId, String message);

//
	List<CCAdminResponse> searchUsersByFarmOrCropName(String keyword, String userId);

	List<CCAdminResponse> getSortedCCAdminResponses(String userId, String sortOrder);

	List<Map<String, Object>> getNotificationHistory(String qcId);

	List<CCToQCReq> getAssignedFarmerRequestsForAdmin(String adminUserId);
}
