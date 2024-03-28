package com.F2C.jwt.mongodb.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.Images;
import com.F2C.jwt.mongodb.models.Order;
import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.OrderResponse;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
//import com.mongo.example.collection.Farmer;

@Service
public interface FarmerService {
	
	String sendOtpForLogin(String phoneNo);

	String verifyOtpForLogin(String phoneNo, String otp);

	String sendOtpForForgotPassword(String phoneNo);

	String verifyOtpForForgotPassword(String phoneNo, String newPassword);
	
	 User updateFarmerProfile(String userId, String firstName, String lastName, String email,String address, String phoneNo, MultipartFile file) throws IOException;
		

	String savePhotoAndLinkToFarmer(String farmerId, MultipartFile file) throws IOException;
	User addNewProduct(String userId, String cropName, String cropSubType,Double cropRetailPrice,Double cropWholesalePrice, String Description,
			Long cropQuantity, MultipartFile[] files, String perishable, String status) throws IOException;
	User updateProductData(String userId, String cropId, String cropName, String cropSubType, Double cropRetailPrice,Double cropWholesalePrice, String Description,
			Long cropQuantity, MultipartFile file, String perishable, String status)
			throws IOException;
	
	User deleteCropDetails(String userId, String cropId);
	
	CropDetails findCropDetails(String userId, String cropId);
	
	List<CropDetails> findCropDetailsFarmer(String userId);
	
	
	List<CropDetails> getCropDetailsForFarmers();

	String setEmptyRequestField(String userId,String cropId); 
	  
	
	
	Page<User> getUsersWithCropDetailsPaginated(Pageable pageable);
	  
	  
	User getUserById(String id);
	
	
	 Map<String, Map<String, String>> getCropEarningsByFarmerId(String farmerId);
	  List<Order> getOrdersByFarmerId(String farmerId);
	  Order getOrderById(String farmerId, String orderId);
	  OrderItem getOrderItemById(String farmerId, String orderId, String orderItemId); 
    boolean changeRequestStatus(String userId,String status);
    List<User> getUsersByRole(Role role);
    boolean changeHandledCCStatus(String userId,String requestId,String status);
	
    
    
    //new 
    Page<User> getUsersWithRoleByName(List<User> allFarmers, Pageable pageable);
    List<User> getAllUsersWithCropDetails();
	
    String getCropNameById(String cropId);

     CCToQCReq viewSingleRequest(String userId,String requestId); 
     List<CCToQCReq> viewRequest(String userId); 
     Optional<User> findUserByCropId(String cropId);
 	boolean updatePublishStatus(String farmerId,String cropId);
    Images saveImage(MultipartFile image, Double latitude, Double longitude) throws IOException;
//    List<User> findFarmersByCropId(String cropId);

	List<OrderResponse> getOrdersByFarmersId(String farmerId);
	
	
	
	///////////////////////////////

	List<CCToQCReq> getApprovedOrRejectedRequests(String farmerId);

	List<CCAdminResponse> getQualityCheckerApprovalForms(String requestId);

	List<ImageResponseForm> getApprovalForms(String requestId);
}
//
//User updateProduct(String userId, String cropName, String cropSubType, Double cropRetailPrice,Double cropWholesalePrice, String Description,
//		Long cropQuantity, MultipartFile file, String perishable, String status) throws IOException;


