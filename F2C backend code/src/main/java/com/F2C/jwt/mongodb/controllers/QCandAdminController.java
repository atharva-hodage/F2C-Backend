package com.F2C.jwt.mongodb.controllers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.Images;
import com.F2C.jwt.mongodb.models.QualityCheckApprovalForm;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.models.UserProfilePictureResponse;
import com.F2C.jwt.mongodb.models.UserProfileResponse;
import com.F2C.jwt.mongodb.payload.response.MessageResponse;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
//import com.F2C.jwt.mongodb.models.CropDetails;
//import com.F2C.jwt.mongodb.models.User;
//import com.F2C.jwt.mongodb.repository.PaginationRepository;
import com.F2C.jwt.mongodb.services.FarmerService;
import com.F2C.jwt.mongodb.services.NotificationService;
import com.F2C.jwt.mongodb.services.QCandAdminService;
import com.F2C.jwt.mongodb.services.UserProfileService;
//import com.F2C.jwt.mongodb.services.TwilioService;
//
//import io.jsonwebtoken.io.IOException;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import com.F2C.jwt.mongodb.services.impl.CustomEntityNotFoundException;

@CrossOrigin(origins = "http://localhost:3000/*", maxAge = 3600)
@RestController
@RequestMapping("/api/QCAdmin")
public class QCandAdminController {
	   @Autowired
	 private  NotificationService notificationService;

	 
	  
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FarmerService farmerService;

	@Autowired
	private QCandAdminService qcAndAdminService;
	
	@Autowired
	UserProfileService userProfileService;
	
	

	// 1
	// cc available free set userId as farmer id //useful and working
	@PostMapping("/setEmptyRequestFieldCCQC/{userId}")
	public ResponseEntity<User> setEmptyRequestFieldCCQC(@PathVariable("userId") String userId) {
		User user = qcAndAdminService.setEmptyRequestFieldCCQC(userId);
		return ResponseEntity.ok(user);
	}

	// 2 View all requests ---> allocated farmer requests list
	//@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/allocated-farmer-requests/{adminUserId}")
	public ResponseEntity<List<CCToQCReq>> viewAllocatedFarmerRequestsForAdmin(@PathVariable String adminUserId) {
		List<CCToQCReq> allocatedRequests = qcAndAdminService.getAllocatedFarmerRequestsForAdmin(adminUserId);
		System.out.print(allocatedRequests);
		
		return ResponseEntity.ok(allocatedRequests);
	}

	// 3 --single farmer view //userId is farmerId 
	@GetMapping("/viewSingleRequest/{userId}/{requestId}")
	public ResponseEntity<CCToQCReq> viewSingleRequest(@PathVariable("userId") String userId,
			@PathVariable("requestId") String requestId) {
		// List<CropDetails> allCropDetails = farmerService.getCropDetailsForFarmers();
		CCToQCReq request = farmerService.viewSingleRequest(userId, requestId);
		//System.out.print("Hi");
		//System.out.print(request);
		return ResponseEntity.ok(request);
	}

	

	// 4
	// to view all quality checkers
	//@PreAuthorize("admin")
	  @CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/quality-checkers")
	public ResponseEntity<List<User>> getAllQualityCheckers() {
		List<User> qualityCheckers = qcAndAdminService.getAllQualityCheckers();
		
		return ResponseEntity.ok(qualityCheckers);
	}

	// 5
	// to set quality checker as free
	// In response CC available is null
	// @PreAuthorize("QUALITYCHECK")
	@PostMapping("/set-qc-available/{userId}")
	public ResponseEntity<User> setQcAvailable(@PathVariable String userId) {
		User updatedUser = qcAndAdminService.setEmptyRequestFieldQC(userId);
		return ResponseEntity.ok(updatedUser);
	}

	// 6
	// @PreAuthorize("admin")
	@GetMapping("/free-qcs")
	public ResponseEntity<List<User>> findFreeQCsByLocationAndStatus(@RequestParam(name = "location") String location) {
		List<User> freeQCs = qcAndAdminService.findFreeQCsByAddress(location);
		return ResponseEntity.ok(freeQCs);
	}

	// 7
	// asignment of farmer to quality checker
//	 @PreAuthorize("Admin")
//	@PostMapping("/assignqctofarmer/{requestId}/{qcId}")
//	public ResponseEntity<CCAdminResponse> assignQCToFarmer(@PathVariable String requestId, @PathVariable String qcId) {
//	    CCAdminResponse ccAdminResponse = qcAndAdminService.assignQCToFarmer(requestId, qcId);
//
////	    if (ccAdminResponse != null) {
////	        // Send notification to the quality checker
////	        String userId = ccAdminResponse.getQCAssignedName();
////	        String message = String.format(
////	            "You have a new request to check the quality of crop %s from %s (Request ID: %s).",
////	            ccAdminResponse.getCropName(),
////	            ccAdminResponse.getFarmerName(),
////	            ccAdminResponse.getReqForQCCC()
////	        );
////
////	        try {
////	            notificationService.sendNotification(userId, message);
////	            System.out.println("Notification sent successfully to QC: " + userId);
////	        } catch (Exception e) {
////	            System.err.println("Failed to send notification to QC: " + userId);
////	            e.printStackTrace();  // Print the stack trace for detailed error information
////	        }
//
//	        return ResponseEntity.status(HttpStatus.OK).body(ccAdminResponse);
////	    } else {
////	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//	    }
	    
	@PostMapping("/assignqctofarmer/{requestId}/{qcId}")
	public ResponseEntity<CCAdminResponse> assignQCToFarmer(@PathVariable String requestId, @PathVariable String qcId) {
	    System.out.println("Received requestId: " + requestId);
	    System.out.println("Received qcId: " + qcId);

	    CCAdminResponse ccAdminResponse = qcAndAdminService.assignQCToFarmer(requestId, qcId);
	    System.out.println("\n\n\n\n CC Admin Response  "+ccAdminResponse+"\n\n\n");



	    return ResponseEntity.ok(ccAdminResponse);
	    } 
	
    @GetMapping("/notificationHistory/{qcId}")
    public ResponseEntity<List<Map<String, Object>>> getNotificationHistory(@PathVariable String qcId) {
        List<Map<String, Object>> notificationHistory = qcAndAdminService.getNotificationHistory(qcId);
        return ResponseEntity.ok(notificationHistory);
    }

	// to view all requests on qc portal
	// 8
	@GetMapping("/view-all-requests/{qcId}")
	public List<CCAdminResponse> viewAllRequests(@PathVariable("qcId") String qcId) {
		// Call the service method to get all requests for the QC
		return qcAndAdminService.getQCDashboardData(qcId);
	}

	//9 View single request on QC  
	@GetMapping("/{requestId}")
	public ResponseEntity<CCToQCReq> viewRequestById(@PathVariable String requestId) {
		CCToQCReq request = qcAndAdminService.viewRequestById(requestId);

		if (request != null) {
			return ResponseEntity.ok(request);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	

	// 11
	@PostMapping("/changeHandledCCStatus/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean changeHandledCCStatus(@PathVariable String userId, @RequestParam String requestId,
			@RequestParam String status) {
//		boolean status1 = farmerService.changeRequestStatus(userId, status);
		boolean status1 = farmerService.changeHandledCCStatus(userId, requestId, status);
		return status1;
	}

	// 12
	@GetMapping("/currentAllEmployeeStatus")
	public ResponseEntity<List<CCAdminResponse>> currentAllEmployeeStatus() {
		List<CCAdminResponse> responseList = qcAndAdminService.currentAllEmployeeStatus();
		return ResponseEntity.ok(responseList);
	}

	// 13
	@PostMapping("/changeCCAvailable/{ccId}")
	public ResponseEntity<User> changeCCAvailable(@PathVariable("ccId") String ccId, @RequestParam String status) {
		User user = qcAndAdminService.changeCCAvailable(ccId, status);
		return ResponseEntity.ok(user);
	}
	
	//14
	@GetMapping("/availableEmployees")
	// @PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<List<User>> availableEmployees() {
		List<User> user = qcAndAdminService.availableEmployees();
		return ResponseEntity.ok(user);
	}

	// 15
	@PostMapping("/changeRequestCreatedStatus/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public boolean changeRequestStatus(@PathVariable String userId, @RequestParam String status) {
		boolean status1 = farmerService.changeRequestStatus(userId, status);
		return status1;
	}
	
	
	
	// 16
	
	/*
	@PostMapping("/quality-check-approve/{qcId}/{requestId}")
	@PreAuthorize("hasRole('QUALITYCHECK')")
    public ResponseEntity<String> saveQualityCheckApprovalForm(
    		
    		@PathVariable String qcId,
    		@PathVariable String requestId,
            @RequestParam String farmLocation,
            @RequestParam Double farmArea,
          
            @RequestParam boolean  cropTypes,
            @RequestParam boolean cropSubtype,
            @RequestParam boolean quantityAvailable,
            @RequestParam boolean organicOrInorganic,
            
            @RequestParam MultipartFile[] files,
            @RequestParam MultipartFile[] docsFiles,
            // @RequestParam("files") MultipartFile[] files,
            //@RequestPart("docsFiles") MultipartFile[] docsFiles,
            
            @RequestParam String grainSize, 
            @RequestParam String presenceOfDiscoloredGrains,
            @RequestParam String moistureContent,
            @RequestParam String aroma,
            @RequestParam String brokenGrains,
            @RequestParam String expectedTexture,
            @RequestParam String cropCondition,
            @RequestParam String weeviledGrains,
            
            @RequestParam boolean complianceWithSafetyRegulations,
            @RequestParam  boolean artificialRipening, 
            
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  Date harvestingDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfInspection,
            
            @RequestParam String rejectionReason,
            @RequestParam String recommendedActions
            )
	//@RequestParam String notesAndComments
	{
        try {
        	
        	//System.out.println("start");
        	
            qcAndAdminService.saveQualityCheckApprovalForm(qcId, requestId, farmLocation, farmArea,  
                    cropTypes, cropSubtype, quantityAvailable, organicOrInorganic, files, docsFiles,  grainSize, presenceOfDiscoloredGrains , moistureContent
           ,aroma,brokenGrains,expectedTexture, cropCondition, weeviledGrains,  complianceWithSafetyRegulations, artificialRipening,harvestingDate,dateOfInspection, rejectionReason,recommendedActions);
            
           // System.out.println("stop");   //,notesAndComments
            return ResponseEntity.ok("Quality Check Approval Form saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save Quality Check Approval Form.");
        }
    }
    */
	@PostMapping("/quality-check-approve/{qcId}/{requestId}")
	@PreAuthorize("hasRole('QUALITYCHECK')")
    public ResponseEntity<String> saveQualityCheckApprovalForm(
    		
    		@PathVariable String qcId,
    		@PathVariable String requestId,
            @RequestParam String farmLocation,
            @RequestParam Double farmArea,
          
            @RequestParam boolean  cropTypes,
            @RequestParam boolean cropSubtype,
            @RequestParam boolean quantityAvailable,
            @RequestParam boolean organicOrInorganic,
            
            @RequestParam MultipartFile[] files,
            @RequestParam MultipartFile[] docsFiles,
            // @RequestParam("files") MultipartFile[] files,
            //@RequestPart("docsFiles") MultipartFile[] docsFiles,
            
            @RequestParam String grainSize, 
            @RequestParam String presenceOfDiscoloredGrains,
            @RequestParam String moistureContent,
            @RequestParam String aroma,
            @RequestParam String brokenGrains,
            @RequestParam String expectedTexture,
            @RequestParam String cropCondition,
            @RequestParam String weeviledGrains,
            
            @RequestParam boolean complianceWithSafetyRegulations,
            @RequestParam  boolean artificialRipening, 
            
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  Date harvestingDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOfInspection,
            
            @RequestParam String rejectionReason,
            @RequestParam String recommendedActions
            )
	//@RequestParam String notesAndComments
	{
        try {
        	
        	//System.out.println("start");
        	
        	boolean approved = qcAndAdminService.saveQualityCheckApprovalForm(qcId, requestId, farmLocation, farmArea,  
                    cropTypes, cropSubtype, quantityAvailable, organicOrInorganic, files, docsFiles,  grainSize, presenceOfDiscoloredGrains , moistureContent
           ,aroma,brokenGrains,expectedTexture, cropCondition, weeviledGrains,  complianceWithSafetyRegulations, artificialRipening,harvestingDate,dateOfInspection, rejectionReason,recommendedActions);
            
           // System.out.println("stop");   //,notesAndComments
        	if (approved) {
				return ResponseEntity.ok("Request approved successfully.");
			} else {
				return ResponseEntity.notFound().build();
			}
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save Quality Check Approval Form.");
        }
    }
	
	
	// 10
	/*
		@PostMapping("/approve/{requestId}")
		public ResponseEntity<String> approveRequest(@PathVariable String requestId) {
			boolean approved = qcAndAdminService.approveRequest(requestId);

			if (approved) {
				return ResponseEntity.ok("Request approved successfully.");
			} else {
				return ResponseEntity.notFound().build();
			}
		}
		*/
		
		/*
		 	@PostMapping("/approve/{requestId}")
		public ResponseEntity<String> approveRequest(@PathVariable String requestId) {
			boolean approved = qcAndAdminService.approveRequest(requestId);

			if (approved) {
				return ResponseEntity.ok("Request approved successfully.");
			} else {
				return ResponseEntity.notFound().build();
			}
		}
		 */
	
	
	// 17
	//17 -------> Work Response for QC Admin
	
		@GetMapping("/qc-work-responses/{userId}")
		@PreAuthorize("hasRole('QUALITYCHECK')")
	public ResponseEntity<List<CCAdminResponse>> getQualityCheckerResponsesWithApprovalForms(@PathVariable String userId) {
	List<CCAdminResponse> responses = qcAndAdminService.getQualityCheckerResponsesWithApprovalForms(userId);
	if (responses != null && !responses.isEmpty()) {
	return ResponseEntity.ok(responses);
	} else {
	return ResponseEntity.notFound().build();
	}
	}

	// 17 cc-work-responses
		
		// 2 View all requests ---> allocated farmer requests list
		
		//@PreAuthorize("hasRole('ADMIN')")
		@GetMapping("/assigned-farmer-requests/{adminUserId}")		
		public ResponseEntity<List<CCToQCReq>> viewassignedFarmerRequestsForAdmin(@PathVariable String adminUserId) {
			List<CCToQCReq> allocatedRequests = qcAndAdminService.getAssignedFarmerRequestsForAdmin(adminUserId);
			System.out.print(allocatedRequests);
			
			 // Filter the list to include only requests with non-null assignedQCId
		    List<CCToQCReq> filteredRequests = allocatedRequests.stream()
		            .filter(request -> request.getAssignedQCId() != null)
		            .collect(Collectors.toList());

		    System.out.print(filteredRequests);
			
			return ResponseEntity.ok(allocatedRequests);
		}
		
		

		   // 18
			
				@GetMapping("/QCViewApprovalForm/{requestId}")
				@PreAuthorize("hasRole('QUALITYCHECK')")
			    public ResponseEntity<List<CCAdminResponse>> getQualityCheckerApprovalForms(@PathVariable String requestId) {
			        List<CCAdminResponse> responses = qcAndAdminService.getQualityCheckerApprovalForms(requestId);
			        if (responses != null && !responses.isEmpty()) {
			            return ResponseEntity.ok(responses);
			        } else {
			            return ResponseEntity.notFound().build();
			        }
			    }
				
				
				
				
				// 19
				
				@GetMapping("/getformwithimg/{requestId}")
			    public ResponseEntity<List<ImageResponseForm>> getFormsWithImages(@PathVariable String requestId) {
			        List<ImageResponseForm> responseForms = qcAndAdminService.getFormsWithImages(requestId);

			        if (responseForms.isEmpty()) {
			            return ResponseEntity.notFound().build();
			        }

			        return ResponseEntity.ok(responseForms);
			    }
				
			//  pass reqid and docs id 
					@GetMapping("/getdocpdf/{requestId}")
				    public ResponseEntity<byte[]> getdocumentPDF(@PathVariable String requestId) {
				        byte[] responseForms = qcAndAdminService.getDocumentPDF(requestId);

				        if (responseForms==null) {
				            return ResponseEntity.notFound().build();
				        }

				        return ResponseEntity.ok(responseForms);
				    }
				
			
				// 20
				@GetMapping("/search/{keyword}/{userId}")//{keyword}/{userId}
				//@PreAuthorize("hasRole('QUALITYCHECK')")
		        public List<CCAdminResponse> searchUsers(@PathVariable String keyword, @PathVariable String userId) {
		            return qcAndAdminService.searchUsersByFarmOrCropName(keyword, userId);
		        }
		      
			
				// 21
		        @GetMapping("/{userId}/sort")
		    	@PreAuthorize("hasRole('QUALITYCHECK')")
		        public ResponseEntity<List<CCAdminResponse>> getCCAdminResponses(
		                @PathVariable String userId,
		                @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
		            List<CCAdminResponse> sortedCCAdminResponses = qcAndAdminService.getSortedCCAdminResponses(userId, sortOrder);
		            return new ResponseEntity<>(sortedCCAdminResponses, HttpStatus.OK);
		        }
				

				
			
			// pranjali 
			    
			    @GetMapping("/profile/{userId}")
				public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
					try {
						UserProfileResponse userProfileResponse = userProfileService.getUserProfile(userId);
						return ResponseEntity.ok(userProfileResponse);
					} catch (CustomEntityNotFoundException e) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body(new MessageResponse("User not found with ID: " + userId));
					}
				}

				@PostMapping("/saveProfilePicture/{userId}")
				public ResponseEntity<String> saveProfilePicture(@PathVariable String userId,
						@RequestParam("file") MultipartFile file) {

					try {
						String photoPath = userProfileService.saveProfilePicture(userId, file);
						return ResponseEntity.ok("Profile picture saved successfully. ");
					} catch (IOException e) {
						return ResponseEntity.status(500).body("Error saving profile picture: " + e.getMessage());
					}
				}

				@GetMapping("/getProfilePictures/{userId}")
				public ResponseEntity<UserProfilePictureResponse> getProfilePictures(@PathVariable String userId) {
					try {
						UserProfilePictureResponse profilePictureResponse = userProfileService.getProfilePictures(userId);
						if (profilePictureResponse != null) {
							return ResponseEntity.ok(profilePictureResponse);
						} else {
							return ResponseEntity.notFound().build();
						}
					} catch (Exception e) {
						return ResponseEntity.status(500).build();
					}
				}
	
	
		
}
