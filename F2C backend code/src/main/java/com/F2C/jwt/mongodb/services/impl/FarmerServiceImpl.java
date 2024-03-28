package com.F2C.jwt.mongodb.services.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.F2C.jwt.mongodb.models.CCAdminResponse;
import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.ImageResponseForm;
import com.F2C.jwt.mongodb.models.Images;
import com.F2C.jwt.mongodb.models.Order;
import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.OrderResponse;
import com.F2C.jwt.mongodb.models.QualityCheckApprovalForm;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.ImageRepository;
import com.F2C.jwt.mongodb.repository.PaginationRepository;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.FarmerService;
import com.F2C.jwt.mongodb.services.QCandAdminService;
import com.F2C.jwt.mongodb.services.TwilioService;
//import com.mongo.example.Exception.CustomEntityNotFoundException;

@Service
public class FarmerServiceImpl implements FarmerService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private PaginationRepository pageRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TwilioService twilioService;

	@Autowired
	private QCandAdminService qcandAdminService;

//	@Autowired
//	private FarmerService userService;

	@Override
	public User getUserById(String id) {
		return userRepository.findById(id).orElse(null);
	}

	private String generateOtp() {
		return new DecimalFormat("000000").format(new Random().nextInt(999999));
		// ...
	}

	// userId is farmerID //taken from srushti
	@Override // not working
	public CCToQCReq viewSingleRequest(String userId, String requestId) {
		Optional<User> userOptional = userRepository.findById(userId);
		// User userOptional = userRepository.findById(userId).orElse(null);
		System.out.println(requestId);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			System.out.println("user" + user);

			for (CCToQCReq request : user.getRequestList()) {
				System.out.print(request.getRequestId());
				if (requestId.equals(request.getRequestId())) {

					System.out.println(requestId);

					request.setRequestId(requestId);
					request.setFarmerId(request.getFarmerId()); // Set the farmerId from the database
					request.setFarmerName(user.getfirstName() + " " + user.getlastName());
					request.setFarmerAddress(user.getaddress());
					request.setFarmerContact(user.getphoneNo());
					request.setFarmerEmail(user.getEmail());

					request.setCropId(request.getCropId()); // Set the cropId from the database
					// request.setFarmerAddress(request.getFarmerAddress());
					System.out.println("in loop");
					return request;
				}
			}
		}
		return null;
	}

	public boolean changeHandledCCStatus(String userId, String requestId, String status) {
		Optional<User> list = userRepository.findById(userId);
		User user = list.get();
		CCToQCReq request = new CCToQCReq();
		for (CCToQCReq request1 : user.getRequestList()) {
			if (requestId.equals(request1.getRequestId())) {

				// request = request1;
				if (status.equals("true")) {
					request1.setIsHandledByCC(true);
				} else {
					request1.setIsHandledByCC(false);
				}
				// List<CCToQCReq> list1= user.getRequestList();
				// list1.remove(request1);
				// list1.add(request);
				// user.setRequestList(list1);
				userRepository.save(user);
			}
		}
		return request.getIsHandledByCC();
	}

	public List<CCToQCReq> viewRequest(String userId) {
		Optional<User> list = userRepository.findById(userId);
		User user = list.get();
		List<CCToQCReq> requestList = user.getRequestList();
		return requestList;
	}

	public boolean changeRequestStatus(String userId, String status) {
		Optional<User> list = userRepository.findById(userId);
		User user = list.get();
		if (status.equals("true"))
			user.setRequestCreated(true);
		else
			user.setRequestCreated(false);

		userRepository.save(user);
		boolean updatedStatus = user.getRequestCreated();
		return updatedStatus;
	}

	public String setEmptyRequestField(String userId, String cropId) {
		Optional<User> list = userRepository.findById(userId);

		User user = list.get();
		CCToQCReq request = new CCToQCReq();
		request.setRequestId(UUID.randomUUID().toString());

		String reqId = request.getRequestId();

		request.setFarmerId(userId);
		request.setFarmerName(user.getfirstName() + " " + user.getlastName());
		request.setFarmerAddress(user.getaddress());
		System.out.println("Farmer Address" + request.getFarmerAddress());
		request.setFarmerContact(user.getphoneNo());
		request.setCropId(cropId);

		// Here crop details that is crop name extra are set to CCToQCReq .
		List<CropDetails> cropdetailslist = user.getCropDetails();
		for (CropDetails crop : cropdetailslist) {
			if (crop.getCropId().equals(cropId)) {
				request.setCropName(crop.getCropName());
				break; // Exit the loop once you've found the matching cropId
			}
		}
		List<CCToQCReq> requestList = user.getRequestList();
		if (requestList.isEmpty()) {
			List<CCToQCReq> newRequestList = new ArrayList();
			newRequestList.add(request);
			user.setRequestList(newRequestList);
			user = userRepository.save(user);
		} else {
			requestList.add(request);
			user.setRequestList(requestList);
			user = userRepository.save(user);
		}
		User user1 = qcandAdminService.assignCCEmployee(userId, reqId);
		return reqId;

	}

	@Override
	public String sendOtpForLogin(String phoneNo) {
		Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String otp = generateOtp();
			twilioService.sendOtp(phoneNo, otp);
			user.setOtp(otp);
			userRepository.save(user);
			return otp;
		} else {
			throw new RuntimeException("Phone number not found");
		}
	}

	@Override
	public String verifyOtpForLogin(String phoneNo, String otp) {
		Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);
		if (userOptional.isPresent()) {
			User user = userOptional.get();

			if (otp.equals(user.getOtp())) {
				user.setOtp(null);
				userRepository.save(user);
				return "Otp Valid";
			} else {
				throw new RuntimeException("Invalid OTP");
			}
		} else {
			throw new RuntimeException("Phone number not found");
		}
	}

	@Override
	public String sendOtpForForgotPassword(String phoneNo) {
		Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String otp = generateOtp();
			twilioService.sendOtp(phoneNo, otp);
			user.setOtp(otp);
			userRepository.save(user);
			return otp;
		} else {
			throw new RuntimeException("Phone number not found");
		}
	}

	@Override
	public String verifyOtpForForgotPassword(String phoneNo, String newPassword) {
		Optional<User> userOptional = userRepository.findByPhoneNo(phoneNo);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setPassword(newPassword);
			userRepository.save(user);
			return user.getPassword();
		} else {
			throw new RuntimeException("ResetPass Failed");
		}
	}

	@Override
	public String savePhotoAndLinkToFarmer(String id, MultipartFile file) throws IOException {
		Optional<User> userOptional = userRepository.findById(id);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Images image = new Images();
			image.setImage(file.getBytes());
			image = imageRepository.save(image);
			user.setImageId(image.getId());
			userRepository.save(user);
			return "/photos/" + user.getImageId();
		} else {
			throw new CustomEntityNotFoundException("Farmer with ID " + id + " not found.");
		}
	}
//	@Override
//	public User updateUser(String userId, String firstName, String lastName, String phoneNo, String email,
//			MultipartFile file) throws IOException {
//		Optional<User> farm = userRepository.findById(userId);
//		User farmer1 = farm.get();
//		Images image = new Images();
//		if (!firstName.isEmpty()) {
//			farmer1.setfirstName(firstName);
//		} else {
//			farmer1.setfirstName(farmer1.getfirstName());
//		}
//
//		if (!lastName.isEmpty()) {
//			farmer1.setlastName(lastName);
//		} else {
//			farmer1.setlastName(farmer1.getlastName());
//		}
////
////		if (!addresses.isEmpty()) {
////			farmer1.setAddresses(addresses);
////		} else {
////			farmer1.setAddresses(farmer1.getAddresses());
////		}
////		if (!aadharNo.isEmpty()) {
////			farmer1.setAadharNo(aadharNo);
////		} else {
////			farmer1.setAadharNo(farmer1.getAadharNo());
////		}
//
//		if (!email.isEmpty()) {
//			farmer1.setEmail(email);
//		} else {
//			farmer1.setEmail(farmer1.getEmail());
//		}
//
//		// farmer1.setPhoneNo(phoneNo);
//
//		image.setImage(file.getBytes());
//		image = imageRepository.save(image);
//
//		farmer1.setImageId(image.getId());
//		userRepository.save(farmer1);
//		return farmer1;
//	}

	// updateProduct is when farmer first time add crop detail

	// when farmer wants to edit crop details

	@Override
	public User updateProductData(String userId, String cropId, String cropName, String cropSubType,
			Double cropRetailPrice, Double cropWholesalePrice, String Description, Long cropQuantity,
			MultipartFile file, String perishable, String status) throws IOException {
		Optional<User> farm = userRepository.findById(userId);
		User farmer1 = farm.get();

		Images img = new Images();
		List<CropDetails> list = farmer1.getCropDetails();
		CropDetails cropDetails = new CropDetails();
		for (CropDetails crops : farmer1.getCropDetails()) {
			if (cropId.equals(crops.getCropId())) {
//    		System.out.println(" Farmer Details ");
//    		System.out.println(crops.getCropId());
//        	System.out.println(crops.getCropName());
//        	System.out.println(crops.getCropPrice());
//        	System.out.println(crops.getCropSubType());
//        	System.out.println(crops.getCropQuantity());
//        	System.out.println("\n");
			}
		}
		for (CropDetails crop : farmer1.getCropDetails()) {
			if (cropId.equals(crop.getCropId())) {
				cropDetails = crop;
				if (!cropName.isEmpty()) {
					cropDetails.setCropName(cropName);
				} else {
					cropDetails.setCropName(crop.getCropName());
				}

				if (cropRetailPrice != null && cropRetailPrice != 0) {
					cropDetails.setCropRetailPrice(cropRetailPrice);
					// Set it to null if it's empty
				} else {
					cropDetails.setCropRetailPrice(crop.getCropRetailPrice());
				}
				if (cropWholesalePrice != null && cropWholesalePrice != 0) {
					cropDetails.setCropRetailPrice(cropWholesalePrice);
					// Set it to null if it's empty
				} else {
					cropDetails.setCropWholesalePrice(crop.getCropWholesalePrice());
				}

				if (cropQuantity != null) {
					cropDetails.setCropQuantity(cropQuantity);
				} else {
					cropDetails.setCropQuantity(crop.getCropQuantity());

				}

				if (!cropSubType.isEmpty()) {
					cropDetails.setCropSubType(cropSubType);
				} else {
					cropDetails.setCropSubType(crop.getCropSubType());
				}
				if (status.equals("true")) {
					boolean productStatus = true;
					cropDetails.setApprovalStatus(productStatus);
				} else {
					boolean productStatus = false;
					cropDetails.setApprovalStatus(productStatus);
				}
				if (perishable.equals("true")) {
					boolean productPerish = true;
					cropDetails.setApprovalStatus(productPerish);
				} else {
					boolean productPerish = false;
					cropDetails.setApprovalStatus(productPerish);
				}
				img.setImage(file.getBytes());
				img = imageRepository.save(img);

				List<String> list2 = new ArrayList<String>();
				list2.add(img.getId());
				cropDetails.setImageIds(list2);

				farmer1 = userRepository.save(farmer1);
				return farmer1;
			}
		}

		return farmer1;
	}

	// publish status
	public boolean updatePublishStatus(String farmerId, String cropId) {
		// boolean published=false;
		Optional<User> farm = userRepository.findById(farmerId);
		User farmer1 = farm.get();
		// List<CropDetails> list = farmer1.getCropDetails();
		CropDetails cropDetails = new CropDetails();
		for (CropDetails crop : farmer1.getCropDetails()) {
			if (cropId.equals(crop.getCropId())) {

				cropDetails = crop;
				cropDetails.setPublished(true);
				System.out.println(cropDetails.getPublished());
				userRepository.save(farmer1);
				return true;
			}
		}

		return false;
	}

	// when farmer want to add another crop
	@Override
	public User addNewProduct(String userId, String cropName, String cropSubType, Double cropRetailPrice,
			Double cropWholesalePrice, String Description, Long cropQuantity, MultipartFile[] files, String perishable,
			String status) throws IOException {
		Optional<User> farm = userRepository.findById(userId);
		User farmer1 = farm.get();
		CropDetails cropDetails = new CropDetails();

		List<CropDetails> list = farmer1.getCropDetails();
		List<String> imageIds = new ArrayList<>();
		if (list == null) {
			// Initialize the list if it's null
			list = new ArrayList<>();
			farmer1.setCropDetails(list);
		}
		if (list.isEmpty()) {
			cropDetails.setCropId(UUID.randomUUID().toString());
			cropDetails.setCropName(cropName);
			cropDetails.setCropQuantity(cropQuantity);
			cropDetails.setDescription(Description);

			cropDetails.setCropRetailPrice(cropRetailPrice);
			cropDetails.setCropWholesalePrice(cropWholesalePrice);
			cropDetails.setCropSubType(cropSubType);

			for (MultipartFile file : files) {
				try {
					Images img = new Images();
					img.setImage(file.getBytes());
					img = imageRepository.save(img);
					imageIds.add(img.getId());

				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			cropDetails.setImageIds(imageIds);
			list.add(cropDetails);
			farmer1.setCropDetails(list);

		} else {
			cropDetails.setCropId(UUID.randomUUID().toString());
			cropDetails.setCropName(cropName);
			cropDetails.setCropRetailPrice(cropRetailPrice);
			cropDetails.setCropWholesalePrice(cropWholesalePrice);
			cropDetails.setCropSubType(cropSubType);
			cropDetails.setCropQuantity(cropQuantity);
			cropDetails.setDescription(Description);
			for (MultipartFile file : files) {
				try {
					Images img = new Images();
					img.setImage(file.getBytes());
					img = imageRepository.save(img);
					imageIds.add(img.getId());

				} catch (IOException e) {

					e.printStackTrace();
				}
			}

			if (status.equals("true")) {
				boolean productStatus = true;
				cropDetails.setApprovalStatus(productStatus);
			} else {
				boolean productStatus = false;
				cropDetails.setApprovalStatus(productStatus);
			}
			if (perishable.equals("true")) {
				boolean productPerish = true;
				cropDetails.setApprovalStatus(productPerish);
			} else {
				boolean productPerish = false;
				cropDetails.setApprovalStatus(productPerish);
			}
			cropDetails.setImageIds(imageIds);
			list.add(cropDetails);
			farmer1.setCropDetails(list);
		}
		farmer1 = userRepository.save(farmer1);
		System.out.println(farmer1.getCropDetails());
		return farmer1;
	}

	@Override
	public List<CropDetails> findCropDetailsFarmer(String userId) {
		Optional<User> farm = userRepository.findById(userId);
		User farmer1 = farm.get();
		List<CropDetails> list = farmer1.getCropDetails();
		return list;
	}

	// find crop detail by id
	@Override
	public CropDetails findCropDetails(String userId, String cropId) {
		Optional<User> farm = userRepository.findById(userId);
		User farmer1 = farm.get();

		CropDetails cropDetails = new CropDetails();
		List<CropDetails> list = farmer1.getCropDetails();
		for (CropDetails crops : farmer1.getCropDetails()) {
			if (cropId.equals(crops.getCropId())) {
//    		System.out.println(" Farmer Details ");
//    		System.out.println(crops.getCropId());
//        	System.out.println(crops.getCropName());
//        	System.out.println(crops.getCropPrice());
				System.out.println(crops.getCropSubType());
//        	System.out.println(crops.getCropQuantity());
				// cropDetails.setCropId(cropId);
				cropDetails.setCropName(crops.getCropName());
				cropDetails.setCropSubType(crops.getCropSubType());
				cropDetails.setCropRetailPrice(crops.getCropRetailPrice());
				cropDetails.setCropWholesalePrice(crops.getCropWholesalePrice());
				cropDetails.setCropQuantity(crops.getCropQuantity());

				cropDetails.setImageIds(crops.getImageIds());
				System.out.println("\n");
				return cropDetails;
			}
		}

		return cropDetails;
	}

	// delete the specific crop
	@Override
	public User deleteCropDetails(String userId, String cropId) {
		Optional<User> farm = userRepository.findById(userId);
		User farmer1 = farm.get();
		farmer1.getCropDetails().removeIf(cropDetails -> cropDetails.getCropId().equals(cropId));
		farmer1 = userRepository.save(farmer1);

		return farmer1;
	}

	@Override
	public User updateFarmerProfile(String userId, String firstName, String lastName, String email, String address,
			String phoneNo, MultipartFile file) throws IOException {
		Optional<User> optionalFarmer = userRepository.findById(userId);
		if (!optionalFarmer.isPresent()) {
			throw new CustomEntityNotFoundException("Farmer with ID " + userId + " not found.");
		}

		User farmer = optionalFarmer.get();
		System.out.println(farmer.getfirstName() + " " + farmer.getlastName() + " " + farmer.getEmail() + " "
				+ farmer.getaddress() + " " + farmer.getImageId() + " " + farmer.getphoneNo() + " ");
		if (firstName != null && !firstName.isEmpty()) {
			farmer.setfirstName(firstName);
		}

		if (lastName != null && !lastName.isEmpty()) {
			farmer.setlastName(lastName);
		}

		if (email != null && !email.isEmpty()) {
			farmer.setEmail(email);
		}

		if (address != null && !address.isEmpty()) {
			farmer.setaddress(address);
		}
		if (phoneNo != null && !phoneNo.isEmpty()) {
			farmer.setphoneNo(phoneNo);
		}

		// if (file != null) {
//			Images image = new Images();
//			image.setImage(file.getBytes());
//			image = imageRepository.save(image);
//			if (farmer.getImageId() != null) {
//	            imageRepository.deleteById(farmer.getImageId());
//	        }
//			farmer.setImageId(image.getId());
		// }
		if (file != null && !file.isEmpty()) {
			Images image = new Images();
			image.setImage(file.getBytes());
			image = imageRepository.save(image);
			System.out.println(image.getId());
			String imageId = image.getId();
			farmer.setImageId(imageId);
		}
		return userRepository.save(farmer);
	}

	@Override
	public List<User> getUsersByRole(Role role) {
		return userRepository.findByRoles(role);
	}

	@Override
	public List<CropDetails> getCropDetailsForFarmers() {
		List<CropDetails> allCropDetails = new ArrayList<>();

		Optional<Role> farmerRole = roleRepository.findByName(ERole.ROLE_FARMER);
		if (farmerRole.isPresent()) {
			List<User> farmers = getUsersByRole(farmerRole.get());
			for (User farmer : farmers) {
				List<CropDetails> cropDetails = farmer.getCropDetails();
				if (cropDetails != null) {
//				System.out.println(cropDetails);
//				System.out.println(" ");
					allCropDetails.addAll(cropDetails);
				}
			}
		}

		return allCropDetails;
	}

	public Page<User> getUsersWithCropDetailsPaginated(Pageable pageable) {
		return pageRepository.findAll(pageable);
	}

	public Page<User> getUsersWithRoleByName(List<User> allFarmers, Pageable pageable) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		List<User> pageList;

		if (allFarmers.size() < startItem) {
			pageList = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, allFarmers.size());
			pageList = allFarmers.subList(startItem, toIndex);
		}

		return new PageImpl<>(pageList, pageable, allFarmers.size());
	}

	public List<User> getAllUsersWithCropDetails() {
		return userRepository.findAllWithCropDetails();
	}

//	public Map<String, Map<String, String>> getCropEarningsByFarmerId(String farmerId) {
//		Optional<User> userOptional = userRepository.findById(farmerId);
//
//		if (userOptional.isPresent()) {
//			User farmer = userOptional.get();
//			List<Order> orders = farmer.getOrders();
//
//			Map<String, Map<String, Double>> cropEarningsMap = new HashMap<>();
//			Map<String, Double> overallSummary = new HashMap<>();
//
//			// Create a DateTimeFormatter for the desired date format
//			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//			
//			for (Order order : orders) {
//				List<OrderItem> orderItems = order.getItems();
//				String deliveryDate = order.getProbableDeliveryDateTime().format(dateFormatter);
//				Map<String, Double> cropPriceMap = cropEarningsMap.computeIfAbsent(deliveryDate, k -> new HashMap<>());
//
//				for (OrderItem orderItem : orderItems) {
//					String cropId = orderItem.getCropId();
//					Double itemPrice = orderItem.getItemPrice();
//					String cropName = getCropNameById(cropId);
//
//					// Update the total earnings for the crop
//					cropPriceMap.put(cropName, cropPriceMap.getOrDefault(cropName, 0.0) + itemPrice);
//					overallSummary.put(cropName, overallSummary.getOrDefault(cropName, 0.0) + itemPrice);
//				}
//			}
//
//			// Convert the inner map values to strings
//			Map<String, Map<String, String>> result = new HashMap<>();
//			for (Map.Entry<String, Map<String, Double>> entry : cropEarningsMap.entrySet()) {
//				Map<String, String> cropMap = entry.getValue().entrySet().stream()
//						.collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
//				cropMap.put("NetTotal",
//						String.valueOf(entry.getValue().values().stream().mapToDouble(Double::doubleValue).sum()));
//				result.put(entry.getKey(), cropMap);
//			}
//
//			// Add overall summary to the result map
//			Map<String, String> overallSummaryMap = overallSummary.entrySet().stream()
//					.collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
//			overallSummaryMap.put("NetTotal",
//					String.valueOf(overallSummary.values().stream().mapToDouble(Double::doubleValue).sum()));
//			result.put("OverallSummary", overallSummaryMap);
//
//			return result;
//		}
//
//		return Collections.emptyMap();
//	}

	public Map<String, Map<String, String>> getCropEarningsByFarmerId(String farmerId) {
	    Optional<User> userOptional = userRepository.findById(farmerId);

	    if (userOptional.isPresent()) {
	        User farmer = userOptional.get();
	        List<Order> orders = farmer.getOrders();

	        Map<String, Map<String, Double>> cropEarningsMap = new HashMap<>();
	        Map<String, Double> overallSummary = new HashMap<>();

	        // Create a DateTimeFormatter for the desired date format
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        for (Order order : orders) {
	            List<OrderItem> orderItems = order.getItems();
	            String deliveryDate = order.getProbableDeliveryDateTime().format(dateFormatter);
	            Map<String, Double> cropPriceMap = cropEarningsMap.computeIfAbsent(deliveryDate, k -> new HashMap<>());

	            for (OrderItem orderItem : orderItems) {
	                String cropId = orderItem.getCropId();
	                Double itemPrice = orderItem.getItemPrice();
	                long quantity = order.getTotalQuantity(); // Get the quantity from the order item
	                Double itemTotalPrice = itemPrice * quantity; // Calculate the total price for the item
	                String cropName = getCropNameById(cropId);

	                // Update the total earnings for the crop
	                cropPriceMap.put(cropName, cropPriceMap.getOrDefault(cropName, 0.0) + itemTotalPrice);
	                overallSummary.put(cropName, overallSummary.getOrDefault(cropName, 0.0) + itemTotalPrice);
	            }
	        }

	        // Convert the inner map values to strings
	        Map<String, Map<String, String>> result = new HashMap<>();
	        for (Map.Entry<String, Map<String, Double>> entry : cropEarningsMap.entrySet()) {
	            Map<String, String> cropMap = entry.getValue().entrySet().stream()
	                    .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
	            cropMap.put("NetTotal",
	                    String.valueOf(entry.getValue().values().stream().mapToDouble(Double::doubleValue).sum()));
	            result.put(entry.getKey(), cropMap);
	        }

	        // Add overall summary to the result map
	        Map<String, String> overallSummaryMap = overallSummary.entrySet().stream()
	                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
	        overallSummaryMap.put("NetTotal",
	                String.valueOf(overallSummary.values().stream().mapToDouble(Double::doubleValue).sum()));
	        result.put("OverallSummary", overallSummaryMap);

	        return result;
	    }

	    return Collections.emptyMap();
	}

	@Override
	public String getCropNameById(String cropId) {
		Optional<User> userWithCrop = userRepository.findByCropDetailsCropId(cropId);
		return userWithCrop.map(user -> {
			for (CropDetails cropDetails : user.getCropDetails()) {
				if (cropId.equals(cropDetails.getCropId())) {
					return cropDetails.getCropName();
				}
			}
			return null; // Crop not found in user's crop details
		}).orElse(null); // User not found with the given cropId
	}

	@Override
	public Images saveImage(MultipartFile image, Double latitude, Double longitude) throws IOException {
		Images newImage = new Images();
		newImage.setImage(image.getBytes());
		newImage.setLatitude(latitude);
		newImage.setLongitude(longitude);
		return imageRepository.save(newImage);
	}

	public Optional<User> findUserByCropId(String cropId) {
		return userRepository.findByCropDetailsCropId(cropId);
	}

	@Override
	public List<Order> getOrdersByFarmerId(String farmerId) {
		Optional<User> list = userRepository.findById(farmerId);

		User farmer = list.get();
//          User farmer = getUserById(farmerId);
		if (farmer != null) {
			return farmer.getOrders();
		}
		return Collections.emptyList();
	}

	@Override
	public Order getOrderById(String farmerId, String orderId) {
		Optional<User> list = userRepository.findById(farmerId);

		User farmer = list.get();
		if (farmer != null) {
			for (Order order : farmer.getOrders()) {
				if (order.getOrderId().equals(orderId)) {
					return order;
				}
			}
		}
		return null; // Order not found or doesn't belong to the consumer
	}

	@Override
	public OrderItem getOrderItemById(String farmerId, String orderId, String orderItemId) {
		Optional<User> list = userRepository.findById(farmerId);

		User farmer = list.get();
		if (farmer != null) {
			for (Order order : farmer.getOrders()) {
				if (order.getOrderId().equals(orderId)) {
					for (OrderItem orderItem : order.getItems()) {
						if (orderItem.getOrderItemId().equals(orderItemId)) {
							return orderItem;
						}
					}
				}
			}
		}
		return null; // Order item not found or does not belong to the specified order

	}

	@Override
	public List<OrderResponse> getOrdersByFarmersId(String farmerId) {
		System.out.println("Farmer ID " +farmerId);
		User farmer = getUserById(farmerId);
		System.out.println("Farmer First Name"+farmer.getfirstName());
		
		 if (farmer != null) {
 	        List<Order> orders = farmer.getOrders();
 	        return convertToOrderResponseList(orders);
 	    }
 	    return Collections.emptyList();
	}

	public List<OrderResponse> convertToOrderResponseList(List<Order> orders) {
		List<OrderResponse> orderResponses = new ArrayList<>();

		for (Order order : orders) {
	        OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getOrderId());
            orderResponse.setOrderDateTime(order.getOrderDateTime());
            orderResponse.setProbableDeliveryDateTime(order.getProbableDeliveryDateTime());
            orderResponse.setAddress(order.getAddress());
            orderResponse.setOrderStatus(order.getOrderStatus());
            orderResponse.setTotalAmount(order.getTotalAmount());
            orderResponse.setTotalQuantity(order.getTotalQuantity());

			// Assuming Order can have multiple OrderItems
			if (order.getItems() != null) {
				for (OrderItem orderItem : order.getItems()) {
					orderResponse.setOrderItemId(orderItem.getOrderItemId());
					orderResponse.setPaymentStatus(orderItem.getPaymentStatus());
					orderResponse.setCropId(orderItem.getCropId());
					String cropId = orderItem.getCropId();
					CropDetails cropDetails = getCropDetailsById(cropId);
					if (cropDetails != null) {
						if (cropDetails.getCropName() != null && !cropDetails.getCropName().isEmpty()) {
							orderResponse.setCropName(cropDetails.getCropName());
						}
//                        else {
//                            // Handle the case where cropName is null or empty
//                            orderResponse.setCropName("Unknown Crop");
//                        }

						orderResponse.setDescription(cropDetails.getDescription());
					}
//                    } else {
//                        // Handle the case where cropDetails is null
//                        orderResponse.setCropName("Unknown Crop");
//                        orderResponse.setDescription("Unknown Description");
//                    }
					// orderResponse.setCropName(cropDetails.getCropName());

					orderResponse.setQuantity(orderItem.getQuantity());
					orderResponse.setItemPrice(orderItem.getItemPrice());
					orderResponse.setUserId(orderItem.getUserId());
					orderResponse.setUserName(orderItem.getUserName());
					orderResponse.setUserAddress(orderItem.getUserAddress());
					orderResponse.setUserContact(orderItem.getUserContact());
					orderResponse.setFarmerId(orderItem.getFarmerId());
					orderResponse.setFarmerName(orderItem.getFarmerName());
					orderResponse.setFarmerAddress(orderItem.getFarmerAddress());
					orderResponse.setFarmerContact(orderItem.getFarmerContact());
					orderResponse.setRazorpay_payment_Id(orderItem.getRazorpay_payment_Id());
					orderResponse.setRazorpay_order_Id(orderItem.getRazorpay_order_Id());

					// Add the OrderResponse object to the list
					orderResponses.add(orderResponse);
				}
			}
		}

		return orderResponses;
	}

	public CropDetails getCropDetailsById(String cropId) {

		// Iterate through farmers to find the crop by cropId
		Optional<Role> role = roleRepository.findByName(ERole.ROLE_FARMER);
		Role rol1 = role.get();

		List<User> farmers = userRepository.findByRoles(rol1);
		for (User farmer : farmers) {
			List<CropDetails> cropDetailsList = farmer.getCropDetails();
			if (cropDetailsList != null) {
				for (CropDetails cropDetails : cropDetailsList) {
					if (cropDetails.getCropId().equals(cropId)) {
						return cropDetails; // Return the matching crop
					}
				}
			}
		}
		return null; // Crop not found

	}

//	    public List<User> findFarmersByCropId(String cropId) {
//	        return userRepository.findByCropDetailsCropId(cropId);
//	    }
	

	@Override
	public List<CCToQCReq> getApprovedOrRejectedRequests(String farmerId) {
	    User farmer = userRepository.findById(farmerId).orElse(null);

	    if (farmer != null) {
	        List<CCToQCReq> allRequests = farmer.getRequestList();

	        // Filter requests that are either approved or rejected
	        return allRequests.stream()
	                .filter(request -> {
	                    String handledQC = request.getHandledQC();
	                    return handledQC != null && ("approved".equalsIgnoreCase(handledQC)
	                            || "rejected".equalsIgnoreCase(handledQC));
	                })
	                .collect(Collectors.toList());
	    } else {
	        return Collections.emptyList();
	    }
	}
	
	
	 @Override
	    public List<CCAdminResponse> getQualityCheckerApprovalForms(String requestId) {
	        List<User> users = userRepository.findAll();

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

	
	
	@Override
    public List<ImageResponseForm> getApprovalForms(String requestId) {
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
                            }
                        }

                        // Set the document data list in your response object
                        response1.setDocs(docList);

                        responseForms.add(response1);
                    }
                }
            }
        }

        return responseForms;
    }
	
	
}

/*
 * @Override public User updateProduct(String userId, String cropName, String
 * cropSubType, Double cropRetailPrice,Double cropWholesalePrice, String
 * Description, Long cropQuantity, MultipartFile file, String perishable, String
 * status) throws IOException { Optional<User> farm =
 * userRepository.findById(userId); User farmer1 = farm.get(); CropDetails
 * cropDetails = new CropDetails(); Images img = new Images(); List<CropDetails>
 * list = new ArrayList<CropDetails>();
 * cropDetails.setCropId(UUID.randomUUID().toString());
 * 
 * cropDetails.setCropName(cropName);
 * cropDetails.setCropWholesalePrice(cropRetailPrice);
 * cropDetails.setCropRetailPrice(cropWholesalePrice);
 * cropDetails.setCropSubType(cropSubType);
 * cropDetails.setCropQuantity(cropQuantity); if(status.equals("true")) {
 * boolean productStatus = true; cropDetails.setApprovalStatus(productStatus); }
 * else { boolean productStatus = false;
 * cropDetails.setApprovalStatus(productStatus); } if(perishable.equals("true"))
 * { boolean productPerish = true; cropDetails.setApprovalStatus(productPerish);
 * } else { boolean productPerish = false;
 * cropDetails.setApprovalStatus(productPerish); }
 * //cropDetails.setPerishable(perishable);
 * cropDetails.setDescription(Description);
 * 
 * img.setImage(file.getBytes()); img = imageRepository.save(img); List<String>
 * list1 = new ArrayList<String>(); list1.add(img.getId());
 * cropDetails.setImageIds(list1);
 * 
 * list.add(cropDetails); farmer1.setCropDetails(list); farmer1 =
 * userRepository.save(farmer1);
 * 
 * return farmer1; }
 */
