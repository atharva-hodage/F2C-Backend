package com.F2C.jwt.mongodb.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import com.F2C.jwt.mongodb.config.NotificationHandler;
import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.Images;

import com.F2C.jwt.mongodb.models.QualityCheckApprovalForm;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.ImageRepository;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.NotificationService;
import com.F2C.jwt.mongodb.services.QCandAdminService;

@Service
public class QCandAdminServiceImpl implements QCandAdminService {
	 private final SimpMessagingTemplate messagingTemplate;
	 
	 public QCandAdminServiceImpl(SimpMessagingTemplate messagingTemplate) {
	        this.messagingTemplate = messagingTemplate;
	    }
@Autowired
private NotificationService notificationService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	

	@Autowired
	private ImageRepository imageRepository;
	
	
	public User changeCCAvailable(String ccId,String status) {
		Optional<User> list = userRepository.findById(ccId);
		User user =list.get();
		
		user.setCcAvailable(status);
		user = userRepository.save(user);
		return user;
	}
	public List<User> availableEmployees(){
		Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
		Role role = adminRole.get();
		List<User> list2 = new ArrayList();
		List<User> list = userRepository.findByRoles(role);
		for(User user : list) {
			String status = user.getCcAvailable();
			if(status.equals("free")) {
				list2.add(user);
			}
			
		}
		
		
		return list2;
	}
	
	
	public User setEmptyRequestFieldCCQC(String userId) {
		Optional<User> list = userRepository.findById(userId);
		User user = list.get();
		//CCToQCReq request = new CCToQCReq();
		//request.setRequestId(UUID.randomUUID().toString());
		//request.setFarmerId(userId);
		//request.setCropId(cropId);
		//request.setCcAvailable("free");  
		user.setCcAvailable("free");
		//List <CCToQCReq> requestList = new ArrayList();
		//requestList.add(request);
		//user.setRequestList(requestList);
		user = userRepository.save(user);
		return user;
		
	}
	
	/*
	public User assignCCEmployee(String userId,String requestId) {
		Optional<User> list = userRepository.findById(userId);
		User farmer = list.get();
		CCToQCReq request = farmer.getRequestList()
                .stream()
                .filter(req -> req.getRequestId().equals(requestId))
                .findFirst()
                .orElseThrow(() -> new CustomEntityNotFoundException("Request not found"));
		
		 List<User> availableEmployee = availableEmployees();

		//User admin=null;
		
		if (!availableEmployee.isEmpty()) {
            // Implement round-robin logic
            int currentIndex = -1;
            if (request.getAssignedCCId() != null) {
                // Find the index of the currently assigned employee
                for (int i = 0; i < availableEmployee.size(); i++) {
                    if (availableEmployee.get(i).getId().equals(request.getAssignedCCId())) {
                        
                    	//admin = availableEmployee.get(i);
                    			currentIndex = i;
                    			//admin.setCcAvailable("busy");
                               // request.setHandledCC("processing");
                        break;
                    }
                }
            }
            
            int nextIndex = (currentIndex + 1) % availableEmployee.size();
            User nextEmployee = availableEmployee.get(nextIndex);
            System.out.println(nextEmployee.getCcAvailable());
            nextEmployee.getAllocatedRequests().add(request);
            nextEmployee.setRequestCreated(true);
            nextEmployee.setCcAvailable("busy");
            System.out.println(nextEmployee.getCcAvailable());
            userRepository.save(nextEmployee);
           // request.setHandledCC("processing");
            // Assign the request to the next available employee
            request.setAssignedCCId(nextEmployee.getId());
           request.setHandledCC("processing");
           request.setIsHandledByCC(true);
            userRepository.save(farmer);  
            
          // NotificationHandler.sendNotificationToEmployee(nextEmployee.getId(), request);
           //System.out.println(notification);
		}
		return farmer;
		}
	*/
	
	 public User assignCCEmployee(String userId,String requestId) {
		 Optional<User> list = userRepository.findById(userId);
		 User farmer = list.get();
		 CCToQCReq request = farmer.getRequestList()
		                .stream()
		                .filter(req -> req.getRequestId().equals(requestId))
               .findFirst()
		                  .orElseThrow(() -> new CustomEntityNotFoundException("Request not found"));
		 
		   List<User> availableEmployee = availableEmployees();
		  
		  if (!availableEmployee.isEmpty()) {
		 // Implement round-robin logic
	            int currentIndex = -1;
		           if (request.getAssignedCCId() != null) {
		 // Find the index of the currently assigned employee
		               for (int i = 0; i < availableEmployee.size(); i++) {
		                if (availableEmployee.get(i).getId().equals(request.getAssignedCCId())) {
		 
		 //admin = availableEmployee.get(i);
		 currentIndex = i;
		 //admin.setCcAvailable("busy");
		 // request.setHandledCC("processing");
		 break;
		                }}}     
	       int nextIndex = (currentIndex + 1) % availableEmployee.size();
		  
		  
	 
	           User nextEmployee = availableEmployee.get(nextIndex);
	            System.out.println(nextEmployee.getCcAvailable());
 
		        nextEmployee.getAllocatedRequests().add(request);
		 
		            nextEmployee.setCcAvailable("busy");
		            System.out.println(nextEmployee.getCcAvailable());
		          
           userRepository.save(nextEmployee);
 
		 
	            request.setAssignedCCId(nextEmployee.getId());
		           request.setHandledCC("processing");
		           request.setIsHandledByCC(true);
		  
 
	             request.setFarmerAddress(farmer.getaddress());  
		       request.setFarmerName(farmer.getfirstName()+" " +farmer.getlastName());
		           request.setFarmerContact(farmer.getphoneNo()); //phone number added 
	 
		            nextEmployee.getAllocatedRequests().add(request);
		  
		             userRepository.save(farmer);  
		 
		         //   NotificationHandler.sendNotificationToEmployee(nextEmployee.getId(), request);
		  } 
		  return farmer;}
	
	public List<CCAdminResponse> currentAllEmployeeStatus() {
		 List<CCAdminResponse> responseList = new ArrayList<>();
		 
		    Optional<Role> farmerRole = roleRepository.findByName(ERole.ROLE_FARMER);
		    Role role = farmerRole.get();
		    List<User> list = userRepository.findByRoles(role);

		    for (User user : list) {
		        if (user.getRequestCreated()) {
		            List<CCToQCReq> requestList = user.getRequestList();

		            for (CCToQCReq req : requestList) {
		                if (req.getIsHandledByCC()) {
		                    CCAdminResponse response = new CCAdminResponse(); 
		                    
		                    Optional<User> farmerList = userRepository.findById(req.getFarmerId());  
		                    User farmer = farmerList.get();
		                    String name = farmer.getfirstName() + " " + farmer.getlastName();
		                    response.setFarmerName(name);

		                    Optional<User> ccList1 = userRepository.findById(req.getAssignedCCId());  
		                    User ccAdmin1 = ccList1.get();
		                    response.setCcAvailable(ccAdmin1.getCcAvailable());
		                    String ccname = ccAdmin1.getfirstName() + " " + ccAdmin1.getlastName();
		                    response.setCCEmployeeName(ccname);

		                    response.setHandledCC(req.getHandledCC());
		                    
		                    responseList.add(response);
		                }
		            }
		        }
		    }

		    return responseList;
	}
	
	public List<CCToQCReq> getAllocatedFarmerRequestsForAdmin(String adminUserId) {
	    // Retrieve the Call Center Admin user by ID
	    User adminUser = userRepository.findById(adminUserId)
	            .orElseThrow(() -> new CustomEntityNotFoundException("Admin user not found"));

	    // Check if the user has the ROLE_ADMIN role
	    /*
	    if (adminUser.getRoles().stream().noneMatch(role -> role.getName() == ERole.ROLE_ADMIN)) {
	        throw new CustomEntityNotFoundException("User is not a Call Center Admin");
	    }
	    */
	    System.out.println(adminUserId);
	    //System.out.println(adminUser.getFirstName());

	    // Return the allocated requests for the Call Center Admin
	    List<CCToQCReq> ans = adminUser.getAllocatedRequests();
//	    for(CCToQCReq)
	    return ans;
	}
	
	public List<User> getAllQualityCheckers() {
	    // Fetch the ROLE_QUALITYCHECK role from the repository
	    Optional<Role> qualityCheckRole = roleRepository.findByName(ERole.ROLE_QUALITYCHECK);

	    // Check if the role was found
	    if (qualityCheckRole.isPresent()) {
	        // Assuming you have a UserRepository injected
	        return userRepository.findByRoles(qualityCheckRole.get());
	    } else {
	        // Handle the case where the ROLE_QUALITYCHECK role is not found (throw an exception or return an empty list)
	        return Collections.emptyList(); // Return an empty list for now
	    }
	}
	
	
	//to set QC as free  
	@Override
	public User setEmptyRequestFieldQC(String userId) {
	    Optional<User> userOptional = userRepository.findById(userId);
	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        user.setQcAvailable("free");
	        return userRepository.save(user);
	    } else {
	        throw new CustomEntityNotFoundException("User not found with ID: " + userId);
	    }
	}
	
	
	//to display qc who are free and match location 
	 @Override
	    public List<User> findFreeQCsByAddress(String address) {
	        // Retrieve all users from the repository
	        List<User> allUsers = userRepository.findAll();

	        // Filter the users to find QCs with matching address and "free" status
	        return allUsers.stream()
	            .filter(user -> user.getaddress().equals(address) && "free".equals(user.getQcAvailable()))
	            .toList(); // You can use .collect(Collectors.toList()) in Java 8
	    }
	 
	 @Override
	 public CCAdminResponse assignQCToFarmer(String requestId, String qcId) {
	     // Retrieve the user (farmer) by requestId
	     Optional<User> userOptional = userRepository.findUserByRequestListRequestId(requestId);
	     if (userOptional.isPresent()) {
	         User farmer = userOptional.get();
	         // Find the request by requestId
	         CCToQCReq requestToUpdate = farmer.getRequestList().stream()
	                 .filter(req -> req.getRequestId().equals(requestId))
	                 .findFirst()
	                 .orElse(null);

	         Optional<User> qcOptional = userRepository.findById(qcId);
	         if (qcOptional.isPresent()) {
	             User qcUser = qcOptional.get();

	             //25 oct
	             String cropName = requestToUpdate.getCropName();
	             String cropSubType = requestToUpdate.getCropSubType();
	             Long quanity = requestToUpdate.getQuantityAvailable();
	             Boolean OrganicOrNot = requestToUpdate.getOrganicInOrganic();
	             System.out.print("Hiii");
	             System.out.print(requestToUpdate);
	             if (requestToUpdate != null) {
	                 // Update the assigned QC ID, handledQC, and isHandledByQC for the specific request
	                 requestToUpdate.setHandledCC("Completed"); //change
	                 requestToUpdate.setAssignedQCId(qcId);
	                 requestToUpdate.setHandledQC("processing");
	                 requestToUpdate.setIsHandledByQC(false);
	                 requestToUpdate.setIsHandledByCC(true);
	                 // Check if the farmer's assigned CC ID is not null
	                 if (requestToUpdate.getAssignedCCId() != null) {
	                     // Retrieve the CC admin by ID
	                     Optional<User> ccAdminOptional = userRepository.findById(requestToUpdate.getAssignedCCId());
	                     if (ccAdminOptional.isPresent()) {
	                         User ccAdmin = ccAdminOptional.get();

	                         //To set call center admin status as free
	                         ccAdmin.setCcAvailable("free");
	                         // Create a CCAdminResponse object and populate it with data
	                         CCAdminResponse ccAdminResponse = new CCAdminResponse();

	                         // Set the QC assigned date to the current date and time----31 oct
	                         ccAdminResponse.setQcAssignedDate(new Date());
	                         // Set reqForQCCC to the requestId
	                         ccAdminResponse.setReqForQCCC(requestId);
	                         ccAdminResponse.setFarmerName(farmer.getfirstName() + " " + farmer.getlastName());
	                         ccAdminResponse.setCCEmployeeName(ccAdmin.getfirstName() + " " + ccAdmin.getlastName());
	                         // Fetch QC name based on qcId (You need to implement this logic)
	                         String qcName = fetchQCName(qcId); // Replace with actual logic
	                         ccAdminResponse.setQCAssignedName(qcName);
	                         ccAdminResponse.setHandledCC("Completed"); //change by me
	                         ccAdminResponse.setCcAvailable("free"); //change by me
	                         ccAdminResponse.setHandledQC("processing");
	                         ccAdminResponse.setQcAvailable("busy");

	                         //to set crop details in ccadminResponse list --- 25th oct
	                         ccAdminResponse.setCropName(cropName);
	                         ccAdminResponse.setCropSubType(cropSubType);
	                         ccAdminResponse.setQuantityAvailable(quanity);
	                         ccAdminResponse.setOrganicInOrganic(OrganicOrNot);
	                         
	                         String notificationMessage = String.format(
	                                 "You have a new request to check the quality of crop %s from %s (Request ID: %s).",
	                                 ccAdminResponse.getCropName(),
	                                 ccAdminResponse.getFarmerName(),
	                                 ccAdminResponse.getReqForQCCC()
	                         );
	                        // String notificationMessage="You have a new request to check the quality of crop"+ccAdminResponse.getCropName();
	                         ccAdminResponse.setNotificationMessage(notificationMessage);
	                         //to add crop details in CC Admin Response
	                         // ccAdminResponse.setCropName(qcName);
	                         System.out.println("notification message"+notificationMessage);
	                         System.out.println("ccresponce message"+ccAdminResponse);

	                         // Add the CCAdminResponse to the CC admin's list
	                         ccAdmin.getCcAdminResponses().add(ccAdminResponse);
	                         qcUser.getCcAdminResponses().add(ccAdminResponse);
	                         ///extra
	                         // Rebuild the requestList for the farmer with the updated request
	                         List<CCToQCReq> updatedRequestList = farmer.getRequestList().stream()
	                                 .map(req -> req.getRequestId().equals(requestId) ? requestToUpdate : req)
	                                 .collect(Collectors.toList());
	                         // Update the farmer's requestList
	                         farmer.setRequestList(updatedRequestList);

	                         //// Rebuild the updatedAllocatedRequestList for ccAdmin with the updated CCToQC req at cC admin side
	                         List<CCToQCReq> updatedAllocatedRequestList = ccAdmin.getAllocatedRequests().stream()
	                                 .map(allocreq -> allocreq.getRequestId().equals(requestId) ? requestToUpdate : allocreq)
	                                 .collect(Collectors.toList());

	                        

	                         // Save the updated CC admin user object and farmer user object
	                         userRepository.save(qcUser);
	                         userRepository.save(ccAdmin);
	                         userRepository.save(farmer);

	                         // Update QC available status in QC collection
//	                         Optional<User> qcOptional = userRepository.findById(qcId);
//	                         if (qcOptional.isPresent()) {
//	                             User qcUser = qcOptional.get();
//	                             qcUser.setQcAvailable("busy");
//	                             // Add the CCAdminResponse to the QC user's list
//	                             qcUser.getCcAdminResponses().add(ccAdminResponse);
//	                             userRepository.save(qcUser);
//	                         }
//	                         ; // Return the CCAdminResponse
	                         return ccAdminResponse;
	                     }
	                 }
	             }
	         }
	     }
	     return null; // Return null if the operation was not successful
	 }

	
	 //for fetch name
	 private String fetchQCName(String qcId) {
		 // Implement logic to fetch QC name based on qcId
		 Optional<User> qcUserOptional = userRepository.findById(qcId);
		 if (qcUserOptional.isPresent()) {
		 User qcUser = qcUserOptional.get();
		 String qcName = qcUser.getfirstName() + " " + qcUser.getlastName(); // Assuming firstName and lastName are QC's name fields
		 return qcName;
		 } else {
		 // Handle the case where QC with qcId is not found
		 return "QC Name Not Found";
		 }
		}
	

	 
	    public List<CCAdminResponse> getQCDashboardData(String qcID) {
	        // Fetch the QC user from the repository (adjust this based on your criteria)
	        User qcUser = userRepository.findById( qcID).orElse(null);

	        if (qcUser != null) {
	            // Extract CCAdminResponse list from the QC user
	            List<CCAdminResponse> ccAdminResponses = qcUser.getCcAdminResponses();

	            return ccAdminResponses;
	        } else {
	            return null;
	        }
	    }
	    
	    
	 
	    @Override
	    public CCToQCReq viewRequestById(String requestId) {
	        Optional<User> userOptional = userRepository.findUserByRequestListRequestId(requestId);

	        if (userOptional.isPresent()) {
	            User farmer = userOptional.get();

	            // Find the request by requestId
	            return farmer.getRequestList().stream()
	                .filter(req -> req.getRequestId().equals(requestId))
	                .findFirst()
	                .orElse(null);
	        }

	        return null; // Return null if the request is not found
	    }


     // approved method merge in saveform
	 // Define getQualityCheckApprovalFormByRequestId method outside of the approveRequest method

	    public QualityCheckApprovalForm getQualityCheckApprovalFormByRequestId(List<CCAdminResponse> ccAdminResponses, String requestId) {
	        for (CCAdminResponse response : ccAdminResponses) {
	            List<QualityCheckApprovalForm> approvalForms = response.getApprovalForms();
	            for (QualityCheckApprovalForm form : approvalForms) {
	                if (form.getRequestIdForm().equals(requestId)) {
	                    return form;
	                }
	            }
	        }
	        return null;
	    }



	
		
		// 16
		public boolean saveQualityCheckApprovalForm(String qcId, String requestId, String farmLocation, Double farmArea, boolean cropTypes, boolean cropSubtype,boolean quantityAvailable,
                boolean organicOrInorganic,  MultipartFile[] files ,  MultipartFile[] docsFiles, String grainSize,String presenceOfDiscoloredGrains , String moistureContent
                   ,String aroma,String brokenGrains,String expectedTexture, String cropCondition, String weeviledGrains, boolean complianceWithSafetyRegulations, boolean artificialRipening, Date harvestingDate, Date dateOfInspection,  String rejectionReason, String recommendedActions ) {
            // , String  notesAndComments
			
			Optional<User> userOptional = userRepository.findById(qcId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<CCAdminResponse> ccAdminResponses = user.getCcAdminResponses();
                
                List<String> imageIds = new ArrayList<>();
                List<String> docsIds = new ArrayList<>();
                
                // Find the relevant CCAdminResponse based on the requestId
                CCAdminResponse response = findCCAdminResponseByRequestId(ccAdminResponses, requestId);
               // System.out.print(response+" first start ..................................."+"/n");
                if (response != null) {
                    QualityCheckApprovalForm approvalForm = new QualityCheckApprovalForm();
                    approvalForm.setId(UUID.randomUUID().toString());
                    approvalForm.setRequestIdForm(requestId);
                    approvalForm.setFarmLocation(farmLocation);
                    approvalForm.setFarmArea(farmArea);
                    approvalForm.setCropTypes(cropTypes);
                    approvalForm.setCropSubtype(cropSubtype);
                    approvalForm.setQuantityAvailable(quantityAvailable);
                    approvalForm.setOrganicOrInorganic(organicOrInorganic);
                    
                    for (MultipartFile file : files) {
                        try {
                            Images img = new Images();
                            
                            img.setImage(file.getBytes());   
                            //img.setImg(file.getBytes()); 
                            
                            img = imageRepository.save(img);
                            imageIds.add(img.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //cropDetails.setImageIds(imageIds);
                  approvalForm.setImageIds(imageIds);
                  
                  for (MultipartFile filed : docsFiles) {
  	 				try {
  	 					Images img = new Images();
  	 					
  	 					img.setDoc(filed.getBytes());
  	 					
  	 					img = imageRepository.save(img);
  	 					docsIds.add(img.getId());
  	 				} catch (IOException e) {
  	 					e.printStackTrace();
  	 				}
  	 			}
  	
  	              approvalForm.setDocsIds(docsIds);
                  
                  approvalForm.setGrainSize(grainSize);
                  approvalForm.setAroma(aroma);
                  approvalForm.setMoistureContent(moistureContent);
                  approvalForm.setPresenceOfDiscoloredGrains(presenceOfDiscoloredGrains);
                  approvalForm.setCropCondition(cropCondition);
                  approvalForm.setExpectedTexture(expectedTexture);
                    approvalForm.setBrokenGrains(brokenGrains);
                    approvalForm.setWeeviledGrains(weeviledGrains);
                    
                    approvalForm.setComplianceWithSafetyRegulations(complianceWithSafetyRegulations);
                    approvalForm.setArtificialRipening(artificialRipening);
                    approvalForm.setHarvestingDate(harvestingDate);
                    approvalForm.setDateOfInspection(dateOfInspection);
                  
                   // approvalForm.setNotesAndComments(notesAndComments);
                    approvalForm.setRecommendedActions(recommendedActions);
                    approvalForm.setRejectionReason(rejectionReason);
                   // System.out.println(approvalForm+" hiiiiiiiiiiiiiiiiiiiiiii");
                    System.out.println(" hiiiiiiiiiiiiiiiiiiiiiii");
                    
                    // Calculate weight based on criteria
               	 int weight = 0;
               	 if (grainSize.equals("High")) {
               	 weight += 5;
               	 } else if (grainSize.equals("Medium")) {
               	 weight += 3;
               	 } else if (grainSize.equals("Low")) {
               	 weight += 1;
               	 }
               	 if (presenceOfDiscoloredGrains.equals("High")) {
               	 weight += 1;
               	 } else if (presenceOfDiscoloredGrains.equals("Medium")) {
               	 weight += 3;
               	 } else if (presenceOfDiscoloredGrains.equals("Low")) {
               	 weight += 5;
               	 }
               	 if (moistureContent.equals("High")) {
               	 weight += 1;
               	 } else if (moistureContent.equals("Medium")) {
               	 weight += 3;
               	 } else if (moistureContent.equals("Low")) {
               	 weight += 5;
               	 }
               	 if (aroma.equals("High")) {
               	 weight += 5;
               	 } else if (aroma.equals("Medium")) {
               	 weight += 3;
               	 } else if (aroma.equals("Low")) {
               	 weight += 1;
               	 }
               	 if (brokenGrains.equals("High")) {
               	 weight += 1;
               	 } else if (brokenGrains.equals("Medium")) {
               	 weight += 3;
               	 } else if (brokenGrains.equals("Low")) {
               	 weight += 5;
               	 }
               	 if (expectedTexture.equals("High")) {
               	 weight += 5;
               	 } else if (expectedTexture.equals("Medium")) {
               	 weight += 3;
               	 } else if (expectedTexture.equals("Low")) {
               	 weight += 1;
               	 }
               	 if (cropCondition.equals("High")) {
               	 weight += 5;
               	 } else if (cropCondition.equals("Medium")) {
               	 weight += 3;
               	 } else if (cropCondition.equals("Low")) {
               	 weight += 1;
               	 }
               	
               	 if (weeviledGrains.equals("High")) {
               	 weight += 1;
               	 } else if (cropCondition.equals("Medium")) {
               	 weight += 3;
               	 } else if (cropCondition.equals("Low")) {
               	 weight += 5;
               	 }
               	 
               
               	System.out.println(weight +" weight ");
               	approvalForm.setApprovalStatus(weight > 20); 
               	boolean approvalstate = approvalForm.isApprovalStatus();
               	
               	// use in Handled request - approved or reject both
                 
               	System.out.println("byeeeee");
               	
                    List<QualityCheckApprovalForm> approvalForms = response.getApprovalForms();
                    
                    //System.out.println(approvalForms +"byeeeee");
                    
                    approvalForms.add(approvalForm);
                    response.setApprovalForms(approvalForms);
                    
                    ////// 
                    userRepository.save(user);
                    
                    
                    //// added
                    String qcId1 = "";
        	        String ccId = "";
        	        String cropId = "";

        	        // Retrieve the user (farmer) by requestId
        	        Optional<User> userOptional1 = userRepository.findUserByRequestListRequestId(requestId);

        	        if (userOptional.isPresent()) {
        	            User farmer = userOptional1.get();

        	            Optional<CCToQCReq> requestOptional = farmer.getRequestList().stream()
        	                    .filter(req -> req.getRequestId().equals(requestId))
        	                    .findFirst();

        	            if (requestOptional.isPresent()) {
        	                CCToQCReq requestToUpdate = requestOptional.get();
        	                
        	                System.out.println("start 1");

        	          
        	               
        	                if (approvalstate) {
        	                        // If approvalStatus is true, set the properties
        	                        requestToUpdate.setHandledQC("approved");
        	                        requestToUpdate.setIsHandledByQC(true);
        	                        System.out.println("approved");
        	                    }
        	           else {
        	            	requestToUpdate.setHandledQC("rejected");
	                        requestToUpdate.setIsHandledByQC(false);  // true
	                        System.out.println("rejected");
        	            	
    	                }

         System.out.println(requestToUpdate);
         
        	                System.out.println("end 1");
        	                    userRepository.save(farmer);

        	                    // extra done on 3 oct by pranjali
        	                    // Update or add CCAdminResponse using the requestId
        	                    qcId = requestToUpdate.getAssignedQCId();
        	                    ccId = requestToUpdate.getAssignedCCId();
        	                    cropId = requestToUpdate.getCropId();

        	                    //updateOrAddCCAdminResponse(requestId, qcId, ccId);
        	                    updateOrAddCCAdminResponse(requestId, qcId, ccId, approvalForm);
        	                    updateCropApproval(requestId, cropId,approvalForm);
        	                    System.out.println("\n end 1");
        	               
        	                  // userRepository.save(user);         // not need
        	                    return true; // Request approved successfully
        	                }
        	        
        	            }
        	   
        	        }
            
                }
        	       
            
			return false;
			
        	    }

                     
               
		// change handedbyoc to from processing to approve and reject. 
		
		private CCAdminResponse findCCAdminResponseByRequestId(List<CCAdminResponse> responses, String requestId) {
            for (CCAdminResponse response : responses) {
                if (requestId.equals(response.getReqForQCCC())) {
                    return response;
                }}return null;
                
		}
		
		
		
		// filled approval form above, change status by calling methid inside that	
		// Helper method to update cropdetails aproval status in farmer side
		// Helper method to create or update CCAdminResponse //done on 4th oct to update
		// in QC and CC
		
	// setting QC free by button and method  ?????????????????
		
		private void updateOrAddCCAdminResponse(String requestId, String qcId, String ccId, QualityCheckApprovalForm approvalForm) {
			System.out.println(" \n updateOrAddCCAdminResponse called");
			// Optional<User> userOptional =
			// userRepository.findUserByRequestListRequestId(requestId);
			Optional<User> userOptional = userRepository.findById(qcId);
			Optional<User> userOptionalCC = userRepository.findById(ccId);
			if (userOptional.isPresent()) {

				User qc = userOptional.get();
				// System.out.println("\n qc" + qc);
				
				Optional<CCAdminResponse> requestOptional = qc.getCcAdminResponses().stream()
						.filter(req -> req.getReqForQCCC().equals(requestId)).findFirst();
				//System.out.println(requestOptional);
				//System.out.println("\n " + );
				if (requestOptional.isPresent()) {

					// Find the corresponding CCAdminResponse object by requestId within the User's data structure
					CCAdminResponse ccAdminResponseToUpdate = requestOptional.get();
					
	System.out.println("\n " +ccAdminResponseToUpdate);

					
					  if (approvalForm.isApprovalStatus()) {
			                ccAdminResponseToUpdate.setHandledQC("Approved");
			                ccAdminResponseToUpdate.setQcAvailable("free");
			                qc.setQcAvailable("free");
			                System.out.println("newly added "+ ccAdminResponseToUpdate.getHandledQC());
			                //System.out.println("approved");
	                    }
	               else {
	            	   ccAdminResponseToUpdate.setHandledQC("Rejected");
		                qc.setQcAvailable("free");
                          System.out.println("rejected");
                       }
					
					// Update CCAdminResponse fields with data from CCToQCReq and farmer
					//ccAdminResponseToUpdate.setHandledQC("Approved");
					//qc.setQcAvailable("free");
					
	System.out.println(ccAdminResponseToUpdate);
			
				}

	System.out.println("Before save - QC: " + qc);
				 userRepository.save(qc);
	System.out.println("After save - QC: " + userRepository.findById(qc.getId()).orElse(null));

				
			}

			if (userOptionalCC.isPresent()) {

				User cc = userOptional.get();
				//System.out.println("\n cc" + cc);
				
				Optional<CCAdminResponse> requestOptional = cc.getCcAdminResponses().stream()
						.filter(req -> req.getReqForQCCC().equals(requestId)).findFirst();
				
				//System.out.println(requestOptional);
				//System.out.println("\n " + );
				
				if (requestOptional.isPresent()) {

					// Find the corresponding CCAdminResponse object by requestId within the User's
					// data structure
					CCAdminResponse ccAdminResponseToUpdate = requestOptional.get();
					//System.out.println("\n " +ccAdminResponseToUpdate);
  System.out.println("\n " +ccAdminResponseToUpdate);
					

					  if (approvalForm.isApprovalStatus()) {
			                ccAdminResponseToUpdate.setHandledQC("Approved");
			               // cc.setQcAvailable("free");
			                System.out.println("approved");
	                    }
	               else {
	            	   ccAdminResponseToUpdate.setHandledQC("Rejected");
		                //cc.setQcAvailable("free");
                        System.out.println("rejected");
                     }

					// Update CCAdminResponse fields with data from CCToQCReq and farmer
					//ccAdminResponseToUpdate.setHandledQC("Approved");
					  
	System.out.println(ccAdminResponseToUpdate);

				}
			
	System.out.println("Before save - CC: " + cc);
				userRepository.save(cc);
	System.out.println("After save - CC: " + userRepository.findById(cc.getId()).orElse(null));
			}
			
			
		}
		
		private void updateCropApproval(String requestId, String cropId,QualityCheckApprovalForm approvalForm) {
			// Retrieve the user (farmer) by requestId
			Optional<User> userOptional = userRepository.findUserByRequestListRequestId(requestId);
			System.out.println("\n updateCropApproval called");
			
			if (userOptional.isPresent()) {
				User farmer = userOptional.get();

				Optional<CropDetails> cropDetails = farmer.getCropDetails().stream()
						.filter(crops -> crops.getCropId().equals(cropId)).findFirst();

			//	System.out.println(cropDetails);
				if (cropDetails.isPresent()) {

					CropDetails cropToUpdate = cropDetails.get();
	System.out.print("\n " +cropToUpdate);
					

					  if (approvalForm.isApprovalStatus()) {
						  cropToUpdate.setApprovalStatus(true);
			                System.out.println("approved");
	                    }
	               else {
	            	   cropToUpdate.setApprovalStatus(false);
                        System.out.println("rejected");
                     }
					
					//cropToUpdate.setApprovalStatus(true);
	System.out.println(cropToUpdate);
				}

				userRepository.save(farmer);
			}

		}

		
		///////  17
		
		@Override
		 public List<CCAdminResponse> getQualityCheckerResponsesWithApprovalForms(String userId) {
		 User user = userRepository.findById(userId).orElse(null);
		 if (user != null) {
		 List<CCAdminResponse> qualityCheckerResponses = user.getCcAdminResponses();
		 // Filter responses with non-empty approvalForms
		 List<CCAdminResponse> responsesWithApprovalForms = qualityCheckerResponses.stream()
		 .filter(response -> !response.getApprovalForms().isEmpty())
		 .collect(Collectors.toList());
		 return responsesWithApprovalForms;
		 } else {
		 return Collections.emptyList();
		 }
		 }
		
	///////  17   cc-work-responses

		
			@Override
		    public List<CCToQCReq> getAssignedFarmerRequestsForAdmin(String adminUserId) {
		        User adminUser = userRepository.findById(adminUserId)
		                .orElseThrow(() -> new CustomEntityNotFoundException("Admin user not found"));

		        //System.out.println(adminUserId);	   
			   // List<CCToQCReq> ans = adminUser.getAllocatedRequests();
		        // Filter the allocated requests to include only those with non-null assignedQCId
		        List<CCToQCReq> filteredRequests = adminUser.getAllocatedRequests().stream()
		                .filter(request -> request.getAssignedQCId() != null)
		                .collect(Collectors.toList());

		        return filteredRequests;
		        // return ans;
		    }

//18
		
		@Override
	    public List<CCAdminResponse> getQualityCheckerApprovalForms(String requestId) {
	        List<User> users = userRepository.findAll(); // You may want to fetch users in a different way.

	        List<CCAdminResponse> responsesWithApprovalForms = new ArrayList<>();

	        for (User user : users) {
	            List<CCAdminResponse> qualityCheckerResponses = user.getCcAdminResponses();
	            for (CCAdminResponse response : qualityCheckerResponses) {
	                if (response.getReqForQCCC().equals(requestId) && !response.getApprovalForms().isEmpty()) {
	                    responsesWithApprovalForms.add(response);
	                }
	            }
	        }

	        return responsesWithApprovalForms;
	    }
		
		
		

		
		// 19
		public List<ImageResponseForm> getFormsWithImages(String requestId) {
		    List<ImageResponseForm> responseForms = new ArrayList<>();

		    List<User> users = userRepository.findAll();

		    for (User user : users) {
		        List<CCAdminResponse> qualityCheckerResponses = user.getCcAdminResponses();
		        for (CCAdminResponse response : qualityCheckerResponses) {
		            if (response.getReqForQCCC().equals(requestId)) {
		                List<QualityCheckApprovalForm> approvalForms = response.getApprovalForms();
		                for (QualityCheckApprovalForm form : approvalForms) {
		                    ImageResponseForm response1 = new ImageResponseForm();
		                    response1.setRequestId(form.getRequestIdForm());
		                    response1.setFarmLocation(form.getFarmLocation());
		                    response1.setFarmArea(form.getFarmArea());

		                    List<byte[]> imgList = new ArrayList<>();
		                    for (String imageId : form.getImageIds()) {
		                        Optional<Images> imageOptional = imageRepository.findById(imageId);
		                        if (imageOptional.isPresent()) {
		                            Images image = imageOptional.get();
		                            imgList.add(image.getImage());
		                        }
		                    }
		                    response1.setImgs(imgList);

		                   
		                    
		                 // Initialize a list to store document data
		                    List<byte[]> docList = new ArrayList<>();

		                    for (String docId : form.getDocsIds()) {
		                        // Fetch document data by ID
		                        Optional<Images> docOptional = imageRepository.findById(docId);
		                        
		                        if (docOptional.isPresent()) {
		                            Images doc = docOptional.get();
		                            docList.add(doc.getDoc());
		                            System.out.println(doc);
		                        }
		                    }
		                    
//		                    	System.out.println(docList);
		                    // Set the document data list in your response object
		                    response1.setDocs(docList);
		                    
		                    responseForms.add(response1);

		                }
		            }
		        }
		    }

		    return responseForms;
		}

		// 20 display on next page , link to button by passing reqid only. 
		
		
		
		
		// 21
		public byte[] getDocumentPDF(String requestId) {
		   

		    List<User> users = userRepository.findAll();
		   byte[] ans=null;
		    for (User user : users) {
		        List<CCAdminResponse> qualityCheckerResponses = user.getCcAdminResponses();
		        for (CCAdminResponse response : qualityCheckerResponses) {
		            if (response.getReqForQCCC().equals(requestId)) {
		                List<QualityCheckApprovalForm> approvalForms = response.getApprovalForms();
		                for (QualityCheckApprovalForm form : approvalForms) {
	                
		                    List<byte[]> docList = new ArrayList<>();

		                    for (String docId : form.getDocsIds()) {
		                      
		                        Optional<Images> docOptional = imageRepository.findById(docId);
		                        
		                        if (docOptional.isPresent()) {
		                            Images doc = docOptional.get();
		                            ans=doc.getDoc();
		                            
		                            docList.add(doc.getDoc());
		                            System.out.println(doc);
		                        }
		                    }
//		                    ans.addAll(docList);
                    	
		                    
		                }
		            }
		        }
		    }

		    return ans;
		}
		
	/////////////////////////////////////////////////////////////////////
		
		

		@Override
	      
        public List<CCAdminResponse> searchUsersByFarmOrCropName(String keyword, String userId) {
              User user = userRepository.findById(userId).orElse(null);
              if (user == null) {
                  return Collections.emptyList();
              }
              List<CCAdminResponse> matchingResponses = new ArrayList<>();
              for (CCAdminResponse ccAdminResponse : user.getCcAdminResponses()) {
                  if (ccAdminResponse.getCropName().toLowerCase().contains(keyword.toLowerCase())) {
                      matchingResponses.add(ccAdminResponse);
                    //  break; // No need to check further once a matching user is found
                  }
              }
              return matchingResponses;
          }
  
  
//working sort 

@Override
public List<CCAdminResponse> getSortedCCAdminResponses(String userId, String sortOrder) {
  User user = userRepository.findById(userId).orElse(null);
  List<CCAdminResponse> ccAdminResponses = user.getCcAdminResponses();
  // Sort based on qcAssignedDate with handling null values
  if ("asc".equalsIgnoreCase(sortOrder)) {
      ccAdminResponses.sort(Comparator.comparing(CCAdminResponse::getQcAssignedDate, Comparator.nullsFirst(Comparator.naturalOrder())));
  } else if ("desc".equalsIgnoreCase(sortOrder)) {
      ccAdminResponses.sort(Comparator.comparing(CCAdminResponse::getQcAssignedDate, Comparator.nullsLast(Comparator.reverseOrder())));
  } else {
      throw new IllegalArgumentException("Invalid sortOrder. Use 'asc' or 'desc'.");
  }
  return ccAdminResponses;
}



@Override
public void sendNotificationToQC(String userId, String message) {
    messagingTemplate.convertAndSendToUser(userId, "/topic/notification", message);
	
}
@Override
public List<Map<String, Object>> getNotificationHistory(String qcId) {
    Optional<User> qcOptional = userRepository.findById(qcId);
    if (qcOptional.isPresent()) {
        User qcUser = qcOptional.get();
        List<Map<String, Object>> notificationHistory = qcUser.getCcAdminResponses().stream()
                .map(response -> {
                    Map<String, Object> notificationMap = new HashMap<>();
                    notificationMap.put("notificationMessage", response.getNotificationMessage());

                    // Format the qcAssignedDate as a string
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = dateFormat.format(response.getQcAssignedDate());
                    notificationMap.put("qcAssignedDate", formattedDate);

                    return notificationMap;
                })
                .collect(Collectors.toList());
        return notificationHistory;
    }
    return Collections.emptyList();
}

}

//////////////////////////////////////////////////






