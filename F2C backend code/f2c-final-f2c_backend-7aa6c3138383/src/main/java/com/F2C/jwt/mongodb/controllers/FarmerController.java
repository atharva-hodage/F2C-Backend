package com.F2C.jwt.mongodb.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.CropEarningsResponse;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.ImageResponse;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.Images;
import com.F2C.jwt.mongodb.models.Order;
import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.OrderResponse;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.ImageRepository;
import com.F2C.jwt.mongodb.repository.PaginationRepository;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.FarmerService;
import com.F2C.jwt.mongodb.services.QCandAdminService;
//import com.F2C.jwt.mongodb.services.QCandAdminService;
import com.F2C.jwt.mongodb.services.TwilioService;

import io.jsonwebtoken.io.IOException;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;

@CrossOrigin(origins = "http://localhost:3000/* ", maxAge = 3600)
@RestController
@RequestMapping("/api/rolef")
public class FarmerController {

	@Autowired
	private FarmerService farmerService;

	@Autowired
	private QCandAdminService qcandAdminService;

	@Autowired
	private TwilioService twilioService;

	@Autowired
	private PaginationRepository pageRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository imageRepository;

	// 1
	@PostMapping("/otplogin")
	@PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public void sendOtpForLogin(@RequestParam String phoneNo) {
		farmerService.sendOtpForLogin(phoneNo);
	}

	// 2
	@PostMapping("/verify-otp")
	@PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public String verifyOtpForLogin(@RequestParam String phoneNo, @RequestParam String otp) {
		return farmerService.verifyOtpForLogin(phoneNo, otp);
	}

	// 3
	@PostMapping("/forgot-password")
	// @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public void sendOtpForForgotPassword(@RequestParam String phoneNo) {
		farmerService.sendOtpForForgotPassword(phoneNo);
	}

	// 4
	@PostMapping("/verify-forgot-password-otp")
	// @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public void verifyOtpForForgotPassword(@RequestParam String phoneNo, @RequestParam String newPassword) {
		farmerService.verifyOtpForForgotPassword(phoneNo, newPassword);
	}

	// Profile management 5 and 6

	// 5 update farmer by id
	@PutMapping("/updateFarmer/{userId}")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<User> updateFarmerProfile(@PathVariable("userId") String userId,
			@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName,
			@RequestParam(required = false) String email, @RequestParam(required = false) String address,
			@RequestParam(required = false) String phoneNo, @RequestParam(required = false) MultipartFile file)
			throws IOException, java.io.IOException {

		User updatedUser = farmerService.updateFarmerProfile(userId, firstName, lastName, email, address, phoneNo,
				file);
		return ResponseEntity.ok(updatedUser);
	}

	// 6
	@PutMapping("/{farmerId}/photo")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<String> updateFarmerPhoto(@PathVariable("farmerId") String farmerId,
			@RequestParam MultipartFile file) throws java.io.IOException {
		try {
			String imageLink = farmerService.savePhotoAndLinkToFarmer(farmerId, file);
			return ResponseEntity.ok("Image uploaded and linked to the farmer successfully. Image Link: " + imageLink);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
		}
	}

	////////////////// **********************************************////////////////////////////////////////

	// 7
	// http://localhost:8080/api/rolef/addNewFarmProduct/64f5853daac9a36ed8c4dc45
	@PostMapping("/addNewFarmProduct/{farmerId}")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<User> addNewFarmProduct(@PathVariable("farmerId") String farmerId,
			@RequestParam String cropName, @RequestParam String cropSubType, @RequestParam Double cropRetailPrice,
			@RequestParam Double cropWholesalePrice, @RequestParam Long cropQuantity, @RequestParam String Description,
			@RequestParam MultipartFile[] files, @RequestParam String perishable, @RequestParam String status)
			throws IOException, java.io.IOException {
		User farmer = farmerService.addNewProduct(farmerId, cropName, cropSubType, cropRetailPrice, cropWholesalePrice,
				Description, cropQuantity, files, perishable, status);
		return ResponseEntity.ok(farmer);
	}

	@PostMapping("/uploadImage")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<String> uploadImage(@RequestPart("image") MultipartFile image,
			@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude)
			throws java.io.IOException {
		try {
			Images savedImage = farmerService.saveImage(image, latitude, longitude);
			return ResponseEntity.ok("Image uploaded successfully. Image ID: " + savedImage.getId());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed.");
		}
	}

	// 8 update product by id
	@PreAuthorize("hasRole('FARMER')")
	@PutMapping("/updateProductById/{farmerId}/{cropId}")
	public ResponseEntity<User> updateFarmProductById(@PathVariable String farmerId, @PathVariable String cropId,
			@RequestParam String cropName, @RequestParam String cropSubType, @RequestParam String Description,
			@RequestParam Double cropRetailPrice, @RequestParam Double cropWholesalePrice,
			@RequestParam long cropQuantity, @RequestParam MultipartFile file, @RequestParam String perishable,
			@RequestParam String status) throws IOException, java.io.IOException {
//	        user.setId(id);
		User updatedUser = farmerService.updateProductData(farmerId, cropId, cropName, cropSubType, cropRetailPrice,
				cropWholesalePrice, Description, cropQuantity, file, perishable, status);
		return ResponseEntity.ok(updatedUser);
	}

	// 9 deleter specific cropdetails

	@DeleteMapping("/deleteCropDetails/{farmerId}/{cropId}")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<User> deleteCropDetails(@PathVariable String farmerId, @PathVariable String cropId) {
		User farmer = farmerService.deleteCropDetails(farmerId, cropId);
		return ResponseEntity.ok(farmer);
	}

	// 10 get specific crop detail for farmer
	@PreAuthorize("hasRole('FARMER')")
	@GetMapping("/getCropDetails/{farmerId}/{cropId}")
	public ResponseEntity<CropDetails> findCropDetails(@PathVariable String farmerId, @PathVariable String cropId) {
		CropDetails cropDetails = farmerService.findCropDetails(farmerId, cropId);
		return ResponseEntity.ok(cropDetails);
	}

	// 11
	@PreAuthorize("hasRole('FARMER')")
	@GetMapping("/getCropDetailsFarmer/{farmerId}")
	public ResponseEntity<List<CropDetails>> findCropDetailsFarmer(@PathVariable String farmerId) {
		List<CropDetails> cropDetails = farmerService.findCropDetailsFarmer(farmerId);
		return ResponseEntity.ok(cropDetails);
	}

	// 12
	@PreAuthorize("hasRole('FARMER')")
	@GetMapping("/getAllCropDetailsForFarmers")
	public ResponseEntity<List<CropDetails>> getAllCropDetailsForFarmers() {
		List<CropDetails> allCropDetails = farmerService.getCropDetailsForFarmers();
		return ResponseEntity.ok(allCropDetails);
	}

	// 12 + extra
	@PreAuthorize("hasRole('FARMER')")
	@PutMapping("/publish/{farmerId}/{cropId}")
	public ResponseEntity<Boolean> publishFarmProductById(@PathVariable String farmerId, @PathVariable String cropId)
			throws IOException, java.io.IOException {

		boolean published = farmerService.updatePublishStatus(farmerId, cropId);
		return ResponseEntity.ok(published);
	}
	//////////////////////////////////////////

	// Farmer send request user Id is farmer id here CCtoQCReq class get initilize
	// 13
	@PreAuthorize("hasRole('FARMER')")
	@PostMapping("/setEmptyRequestField/{userId}/{cropId}")
	public ResponseEntity<String> setEmptyFieldsRequest(@PathVariable("userId") String userId,
			@PathVariable("cropId") String cropId) {
		System.out.println("user : " + userId);
		System.out.println("crop :" + cropId);
		String reqid = farmerService.setEmptyRequestField(userId, cropId);

		return ResponseEntity.ok(reqid);
	}

	// 14
	// Here CC Employee set as free
	@PostMapping("/assignCCEmployee/{userId}")
	public ResponseEntity<User> assignCCEmployee(@PathVariable("userId") String userId,
			@RequestParam String requestId) {
		User user = qcandAdminService.assignCCEmployee(userId, requestId);
		return ResponseEntity.ok(user);
	}

///////////////////////////////////////////

	// 15
	@GetMapping("/crops")
	@PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getCropDetails(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "4") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Map<String, Object> response = new HashMap<>();
		Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_FARMER);
		Role role = adminRole.get();

		List<User> allFarmers = userRepository.findByRoles(role);
		List<CropDetails> allCropDetails = new ArrayList<>();

		for (User user : allFarmers) {
			if (user != null && user.getCropDetails() != null && !user.getCropDetails().isEmpty()) {
				allCropDetails.addAll(user.getCropDetails());
			}
		}

		int totalItems = allCropDetails.size();
		int totalPages = (int) Math.ceil((double) totalItems / size);

		int start = page * size;
		int end = Math.min(start + size, totalItems);

		List<CropDetails> cropDetailsPage = allCropDetails.subList(start, end);

		List<ImageResponse> imageResponseList = new ArrayList<>();

		for (CropDetails crop : cropDetailsPage) {
			if (crop.getPublished()) {
				ImageResponse imgr = new ImageResponse();
				imgr.setCropId(crop.getCropId());
				imgr.setCropName(crop.getCropName());
				imgr.setCropQuantity(crop.getCropQuantity());
				imgr.setDescription(crop.getDescription());
				imgr.setCropRetailPrice(crop.getCropRetailPrice());
				imgr.setCropSubType(crop.getCropSubType());
				imgr.setCropWholesalePrice(crop.getCropWholesalePrice());
				System.out.print(crop.getDescription());

				imgr.setPublished(crop.getPublished());

				// Initialize a list to store image bytes
				List<byte[]> imageBytesList = new ArrayList<>();

				for (String id : crop.getImageIds()) {
					Optional<Images> image = imageRepository.findById(id);
					if (image.isPresent()) {
						Images img = image.get();
						imageBytesList.add(img.getImage());
					}
				}

				imgr.setImages(imageBytesList);
				imageResponseList.add(imgr);
				System.out.println("Image Response: " + imgr);
			}
		}

		response.put("crops", imageResponseList);
		System.out.println("List of Crops: " + imageResponseList);
		response.put("currentPage", page);
		response.put("totalItems", totalItems);
		response.put("totalPages", totalPages);

		return ResponseEntity.ok(response);
	}

	// 16

	@GetMapping("/search")
	@PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> searchCrops(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "4") int size, @RequestParam(required = false) String query) {
		Pageable pageable = PageRequest.of(page, size);
		Map<String, Object> response = new HashMap<>();

		// Fetch all users with crop details
		List<User> allUsersWithCropDetails = farmerService.getAllUsersWithCropDetails();

		List<CropDetails> allCropDetails = allUsersWithCropDetails.stream()
				.filter(user -> user.getCropDetails() != null) // Null check for cropDetails
				.flatMap(user -> user.getCropDetails().stream()).filter(crop -> isCropMatchingQuery(crop, query))
				.collect(Collectors.toList());

		int totalItems = allCropDetails.size();
		int totalPages = (int) Math.ceil((double) totalItems / size);

		int start = page * size;
		int end = Math.min(start + size, totalItems);
		List<CropDetails> cropDetailsPage = new ArrayList<>();
		if (start < end) {
			cropDetailsPage = allCropDetails.subList(start, end);
		}

		List<ImageResponse> imageResponseList = new ArrayList<>();

		for (CropDetails crop : cropDetailsPage) {
			if (crop.getPublished()) {
				ImageResponse imgr = new ImageResponse();
				imgr.setCropId(crop.getCropId());
				imgr.setCropName(crop.getCropName());
				imgr.setCropQuantity(crop.getCropQuantity());
				imgr.setCropRetailPrice(crop.getCropRetailPrice());
				imgr.setCropSubType(crop.getCropSubType());
				imgr.setCropWholesalePrice(crop.getCropWholesalePrice());
				imgr.setDescription(crop.getDescription());
				imgr.setPublished(crop.getPublished());

				// Initialize a list to store image bytes
				List<byte[]> imageBytesList = new ArrayList<>();

				for (String id : crop.getImageIds()) {
					Optional<Images> image = imageRepository.findById(id);
					if (image.isPresent()) {
						Images img = image.get();
						imageBytesList.add(img.getImage());
					}
				}

				imgr.setImages(imageBytesList);
				imageResponseList.add(imgr);
				System.out.println("Image Response: " + imgr);
			}
		}

		response.put("crops", imageResponseList);
		System.out.println("List of Crops: " + imageResponseList);
		response.put("currentPage", page);
		response.put("totalItems", totalItems);
		response.put("totalPages", totalPages);

		return ResponseEntity.ok(response);
	}

	// 17
	private boolean isCropMatchingQuery(CropDetails crop, String query) {
		if (query == null) {
			return true; // Include all crops if no query is provided
		}

		String lowercaseQuery = query.trim().toLowerCase();

		return crop.getCropName().toLowerCase().contains(lowercaseQuery)
				|| crop.getCropSubType().toLowerCase().contains(lowercaseQuery);
	}
	
	
	
	
	
	@GetMapping("/filter")
	@PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	public ResponseEntity<Map<String, Object>> getCropDetails(
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "4") int size,
	    @RequestParam(required = false) String cropNameFilter,
	    @RequestParam(required = false) String cropSubTypeFilter,
	    @RequestParam(required = false) Double minWholesalePrice,
	    @RequestParam(required = false) Double maxWholesalePrice,
	    @RequestParam(required = false) Double minRetailPrice,
	    @RequestParam(required = false) Double maxRetailPrice) {
	    Pageable pageable = PageRequest.of(page, size);
	    Map<String, Object> response = new HashMap<>();
	    
	    Optional<Role> adminRole = roleRepository.findByName(ERole.ROLE_FARMER);
	    Role role = adminRole.get();
	    
	    List<User> allFarmers = userRepository.findByRoles(role);
	    List<CropDetails> allCropDetails = new ArrayList<>();

	    for (User user : allFarmers) {
	        if (user != null && user.getCropDetails() != null && !user.getCropDetails().isEmpty()) {
	            allCropDetails.addAll(user.getCropDetails());
	        }
	    }

	    List<CropDetails> filteredCropDetails = allCropDetails.stream()
	        .filter(crop -> (cropNameFilter == null || crop.getCropName().equalsIgnoreCase(cropNameFilter))
	            && (cropSubTypeFilter == null || crop.getCropSubType().equalsIgnoreCase(cropSubTypeFilter))
	            && (minWholesalePrice == null || crop.getCropWholesalePrice() >= minWholesalePrice)
	            && (maxWholesalePrice == null || crop.getCropWholesalePrice() <= maxWholesalePrice)
	            && (minRetailPrice == null || crop.getCropRetailPrice() >= minRetailPrice)
	            && (maxRetailPrice == null || crop.getCropRetailPrice() <= maxRetailPrice))
	        .collect(Collectors.toList());

	    int totalItems = filteredCropDetails.size();
	    int totalPages = (int) Math.ceil((double) totalItems / size);

	    int start = page * size;
	    int end = Math.min(start + size, totalItems);

	    List<CropDetails> cropDetailsPage = filteredCropDetails.subList(start, end);

	    List<ImageResponse> imageResponseList = new ArrayList<>();

	    for (CropDetails crop : cropDetailsPage) {
	        if (crop.getPublished()) {
	            ImageResponse imgr = new ImageResponse();
	            imgr.setCropId(crop.getCropId());
	            imgr.setCropName(crop.getCropName());
	            imgr.setCropQuantity(crop.getCropQuantity());
	            imgr.setDescription(crop.getDescription());
	            imgr.setCropRetailPrice(crop.getCropRetailPrice());
	            imgr.setCropSubType(crop.getCropSubType());
	            imgr.setCropWholesalePrice(crop.getCropWholesalePrice());
	            System.out.print(crop.getDescription());

	            imgr.setPublished(crop.getPublished());

	            List<byte[]> imageBytesList = new ArrayList<>();

	            for (String id : crop.getImageIds()) {
	                Optional<Images> image = imageRepository.findById(id);
	                if (image.isPresent()) {
	                    Images img = image.get();
	                    imageBytesList.add(img.getImage());
	                }
	            }

	            imgr.setImages(imageBytesList);
	            imageResponseList.add(imgr);
	            System.out.println("Image Response: " + imgr);
	        }
	    }

	    response.put("crops", imageResponseList);
	    System.out.println("List of Crops: " + imageResponseList);
	    response.put("currentPage", page);
	    response.put("totalItems", totalItems);
	    response.put("totalPages", totalPages);

	    return ResponseEntity.ok(response);
	}
	

	   @GetMapping("/crop-earnings/{farmerId}")
	   @PreAuthorize("hasRole('FARMER')")
	    public ResponseEntity<Map<String, Map<String, String>>> getCropEarningsByFarmerId(@PathVariable String farmerId) {
	        Map<String, Map<String, String>> cropEarnings = farmerService.getCropEarningsByFarmerId(farmerId);

	        if (cropEarnings.isEmpty()) {
	            // Return 404 if the farmer or crop earnings are not found
	            return ResponseEntity.notFound().build();
	        }

	        // Return the crop earnings map in the response body
	        return ResponseEntity.ok(cropEarnings);
	    }
	   
	   
	   @GetMapping("/getApprovedOrRejectedRequests/{farmerId}")
	    @PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<List<CCToQCReq>> getApprovedOrRejectedRequests(@PathVariable String farmerId) {
	    List<CCToQCReq> approvedOrRejectedRequests = farmerService.getApprovedOrRejectedRequests(farmerId);

	    // Filter requests that are either approved or rejected
	    approvedOrRejectedRequests = approvedOrRejectedRequests.stream()
	            .filter(request -> {
	            	String handledQC = request.getHandledQC();
                   return handledQC != null && ("approved".equalsIgnoreCase(handledQC)
                           || "rejected".equalsIgnoreCase(handledQC));
               })
	            .collect(Collectors.toList());

	    if (!approvedOrRejectedRequests.isEmpty()) {
	        return ResponseEntity.ok(approvedOrRejectedRequests);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	 
	 @GetMapping("/FViewApprovalForm/{requestId}")
	    @PreAuthorize("hasRole('FARMER')")
	    public ResponseEntity<List<CCAdminResponse>> getQualityCheckerApprovalForms(@PathVariable String requestId) {
	        List<CCAdminResponse> responses = farmerService.getQualityCheckerApprovalForms(requestId);
	        if (responses != null && !responses.isEmpty()) {
	            return ResponseEntity.ok(responses);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
	
	
	//@PreAuthorize("hasRole('FARMER')")
	//@GetMapping("/getApprovalForms/{requestId}/{farmerId}")
	
	
	@GetMapping("/getformwithimg/{requestId}")
	@PreAuthorize("hasRole('FARMER')")
   public ResponseEntity<List<ImageResponseForm>> getApprovalForms(@PathVariable String requestId) {
       List<ImageResponseForm> approvalForms = farmerService.getApprovalForms(requestId);

       if (approvalForms.isEmpty()) {
           return ResponseEntity.notFound().build();
       }

       return ResponseEntity.ok(approvalForms);
   }

	
//	@GetMapping("/view-all-orders/{farmerId}")
//	@PreAuthorize("hasRole('FARMER')")
//	public ResponseEntity<List<Order>> viewOrders(@PathVariable String farmerId) {
//		List<Order> orders = farmerService.getOrdersByFarmerId(farmerId);
//		return ResponseEntity.ok(orders);
//	}
	@GetMapping("/view-all-orders/{farmerId}")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<Double> viewOrders(@PathVariable String farmerId) {
	    System.out.println(farmerId);

	    List<Order> orders = farmerService.getOrdersByFarmerId(farmerId);

	    // Calculate total amount sum
	    double totalAmountSum = orders.stream()
	            .mapToDouble(Order::getTotalAmount)
	            .sum();
	    
	    System.out.println(totalAmountSum);
	    return ResponseEntity.ok(totalAmountSum);
	    
	}

	//13 //order summary 
	// view single order
	@GetMapping("/{farmerId}/view-order/{orderId}")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<Order> viewSingleOrder(@PathVariable String farmerId, @PathVariable String orderId) {
		Order order = farmerService.getOrderById(farmerId, orderId);
		if (order != null) {
			return ResponseEntity.ok(order);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	// notification
			@GetMapping("/notificationf/{farmerId}")
			@PreAuthorize("hasRole('FARMER')")
			public ResponseEntity<List<OrderResponse>> notifcation(@PathVariable String farmerId) {
				System.out.println("Farmer ID " +farmerId);
			    List<OrderResponse> orderResponses = farmerService.getOrdersByFarmersId(farmerId);
			    System.out.println("\n\n\n\n This is OrderResponses "+orderResponses);
			    return ResponseEntity.ok(orderResponses);
			}
		
	@GetMapping("/orders/items")
	@PreAuthorize("hasRole('FARMER')")
	public ResponseEntity<OrderItem> getOrderItemById(
	        @RequestParam String farmerId,
	        @RequestParam String orderId,
	        @RequestParam String orderItemId) {
	    OrderItem orderItem = farmerService.getOrderItemById(farmerId, orderId, orderItemId);
	    if (orderItem != null) {
	        return ResponseEntity.ok(orderItem);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	
	// View- all- orders
		@GetMapping("/viewallorderf/{farmerId}")
		@PreAuthorize("hasRole('FARMER')")
		public ResponseEntity<List<OrderResponse>> viewOrders1(@PathVariable String farmerId) {
			System.out.println("Farmer ID " +farmerId);
		    List<OrderResponse> orderResponses = farmerService.getOrdersByFarmersId(farmerId);
		    System.out.println("\n\n\n\n This is OrderResponses "+orderResponses);
		    return ResponseEntity.ok(orderResponses);
		}
	
//	   @GetMapping("/byCropId/{cropId}")
//	    public List<User> findFarmersByCropId(@PathVariable String cropId) {
//	        return farmerService.findFarmersByCropId(cropId);
//	    }

	/*
	 * Old prajwals apis ///Search and display variations //15
	 * 
	 * @GetMapping("/crops")
	 * 
	 * @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	 * public ResponseEntity<Map<String, Object>> getCropDetails(
	 * 
	 * @RequestParam(defaultValue = "0") int page,
	 * 
	 * @RequestParam(defaultValue = "4") int size) { Pageable pageable =
	 * PageRequest.of(page, size); Map<String, Object> response = new HashMap<>();
	 * 
	 * Page<User> users = farmerService.getUsersWithCropDetailsPaginated(pageable);
	 * List<CropDetails> cropDetailsList = users.getContent().stream() .flatMap(user
	 * -> user.getCropDetails().stream()) .collect(Collectors.toList());
	 * 
	 * response.put("crops", cropDetailsList); response.put("currentPage",
	 * users.getNumber()); response.put("totalItems", users.getTotalElements());
	 * response.put("totalPages", users.getTotalPages());
	 * 
	 * return ResponseEntity.ok(response); }
	 * 
	 * //16 //for admin
	 * 
	 * @GetMapping("/userinfowithcrop")
	 * 
	 * @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	 * public ResponseEntity<Page<User>> getUsersForAdmin(@RequestParam(defaultValue
	 * = "0") int page,
	 * 
	 * @RequestParam(defaultValue = "4") int size) { Pageable pageable =
	 * PageRequest.of(page, size); //Map<String, Object> response = new HashMap<>();
	 * //List<CropDetails> crops = farmerService.getCropDetailsForFarmers();
	 * Page<User> users = farmerService.getUsersWithCropDetailsPaginated(pageable);
	 * 
	 * // return ResponseEntity.ok(users); return ResponseEntity.ok(users); //
	 * return new ResponseEntity<>(response, HttpStatus.OK); // List<CropDetails>
	 * cropDetailsList = users.getContent().stream() // .flatMap(user ->
	 * user.getCropDetails().stream()) // .collect(Collectors.toList()); //
	 * response.put("crops", cropDetailsList); // response.put("currentPage",
	 * users.getNumber()); // response.put("totalItems", users.getTotalElements());
	 * // response.put("totalPages", users.getTotalPages()); // return
	 * ResponseEntity.ok(cropDetailsList); // return new ResponseEntity<>(response,
	 * HttpStatus.OK);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //17
	 * 
	  @GetMapping("/filter/cropname")
	  
	  @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	  public ResponseEntity<Map<String, Object>> getCropDetails(
	  
	  @RequestParam(defaultValue = "0") int page,
	  
	  @RequestParam(defaultValue = "4") int size,
	  
	  @RequestParam(required = false) String cropNameFilter,
	  
	  @RequestParam(required = false) String cropSubTypeFilter,
	  
	  @RequestParam(required = false, defaultValue = "false") boolean
	  combinedFilter,
	  
	  @RequestParam(required = false) Integer minQuantity,
	  
	  @RequestParam(required = false) Integer maxQuantity,
	  
	 @RequestParam(required = false) String sortBy,
	  
	  @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
	  
	  Pageable pageable;
	 
	  // Handle sorting if (sortBy != null) { Sort sort = Sort.by(sortBy); if
	  ("desc".equalsIgnoreCase(sortOrder)) { sort = sort.descending(); } pageable =
	  PageRequest.of(page, size, sort); } else { pageable = PageRequest.of(page,
	  size); }
	 
	  Map<String, Object> response = new HashMap<>();
	  
	 Page<User> users = farmerService.getUsersWithCropDetailsPaginated(pageable);
	 
	 List<CropDetails> cropDetailsList = users.getContent().stream() .flatMap(user
	  -> user.getCropDetails().stream()) .filter(crop -> (cropNameFilter == null ||
	  crop.getCropName().equalsIgnoreCase(cropNameFilter)) && (cropSubTypeFilter ==
	  null || crop.getCropSubType().equalsIgnoreCase(cropSubTypeFilter)) &&
	  (!combinedFilter || (cropNameFilter != null && cropSubTypeFilter != null &&
	  crop.getCropName().equalsIgnoreCase(cropNameFilter) &&
	  crop.getCropSubType().equalsIgnoreCase(cropSubTypeFilter))) && (minQuantity
	  == null || crop.getCropQuantity() >= minQuantity) && (maxQuantity == null ||
	  crop.getCropQuantity() <= maxQuantity)) .collect(Collectors.toList());
	  
	  response.put("crops", cropDetailsList); response.put("currentPage",
	  users.getNumber()); response.put("totalItems", users.getTotalElements());
	  response.put("totalPages", users.getTotalPages());
	  
	 return ResponseEntity.ok(response); }
	 
	 
	 * //18
	 * 
	 * @GetMapping("/search")
	 * 
	 * @PreAuthorize("hasRole('FARMER') or hasRole('CONSUMER') or hasRole('ADMIN')")
	 * public ResponseEntity<Map<String, Object>> searchCrops(
	 * 
	 * @RequestParam(defaultValue = "0") int page,
	 * 
	 * @RequestParam(defaultValue = "4") int size,
	 * 
	 * @RequestParam(required = false) String query) { Pageable pageable =
	 * PageRequest.of(page, size); Map<String, Object> response = new HashMap<>();
	 * 
	 * Page<User> users = farmerService.getUsersWithCropDetailsPaginated(pageable);
	 * 
	 * List<CropDetails> cropDetailsList = users.getContent().stream() .flatMap(user
	 * -> user.getCropDetails().stream()) .filter(crop -> isCropMatchingQuery(crop,
	 * query)) .collect(Collectors.toList());
	 * 
	 * System.out.println("CropDetails count: " + cropDetailsList.size());
	 * 
	 * response.put("crops", cropDetailsList); response.put("currentPage",
	 * users.getNumber()); response.put("totalItems", users.getTotalElements());
	 * response.put("totalPages", users.getTotalPages());
	 * 
	 * return ResponseEntity.ok(response); } //19 private boolean
	 * isCropMatchingQuery(CropDetails crop, String query) { if (query == null) {
	 * return true; // Include all crops if no query is provided }
	 * 
	 * String lowercaseQuery = query.trim().toLowerCase();
	 * 
	 * return crop.getCropName().toLowerCase().contains(lowercaseQuery) ||
	 * crop.getCropSubType().toLowerCase().contains(lowercaseQuery); }
	 * 
	 */

}