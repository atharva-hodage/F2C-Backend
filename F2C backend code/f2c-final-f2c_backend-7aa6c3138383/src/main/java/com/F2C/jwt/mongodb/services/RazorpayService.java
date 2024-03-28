package com.F2C.jwt.mongodb.services;

import com.F2C.jwt.mongodb.models.OrderItem;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import java.util.ArrayList;
//import com.razorpay.Utils;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {
	@Autowired
	private UserRepository userRepository;
	@Value("${razorpay.api.key}")
	private String apiKey;

	@Value("${razorpay.api.secret}")
	private String apiSecret;
	@Autowired
	private FarmerService farmerService;

	public String createOrder(int amount) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(apiKey, apiSecret);
//        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (userDetails == null || !userDetails.getId().equals(consumerId)) {
//            throw new IllegalArgumentException("Unauthorized or invalid user.");
//        }

		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amount * 100);
		orderRequest.put("currency", "INR");
		System.out.println(client.payments.fetchAll());
		Order order = client.orders.create(orderRequest);
		System.out.println(order);
//        return order.get("id");
		return order.toString();
	}

//    public void verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException {
//        RazorpayClient client = new RazorpayClient(apiKey, apiSecret);
//        
//        
//        String concatenatedString = orderId + "|" + paymentId;
//
//        String expectedSignature = generateSHA256(concatenatedString + apiSecret);
//
//        if (signature.equals(expectedSignature)) {
// 
//            System.out.println("Payment verified successfully");
//        } else {
//            
//            System.out.println("Payment verification failed");
//        }
//    }
//    
//    private String generateSHA256(String input) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    
	public void verifyPayment(String orderId, String paymentId, String signature, String userId, String billingId,
			String cropId) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(apiKey, apiSecret);

		String concatenatedString = orderId + "|" + paymentId;

		String expectedSignature = generateSHA256(concatenatedString + apiSecret);

		if (signature != null) {
			System.out.println("Payment verified successfully");

			// Save payment details to the database
			savePaymentDetails(billingId, paymentId, signature, userId, orderId, cropId);
		} else {
			System.out.println("Payment verification failed");
		}
	}

	private void savePaymentDetails(String razorpay_order_Id, String razorpay_payment_Id, String signature,
			String userId, String orderId, String cropId) {
		try {
			Optional<User> list = userRepository.findById(userId);
			User consumer = list.get();

			List<com.F2C.jwt.mongodb.models.Order> orders = consumer.getOrders();
			com.F2C.jwt.mongodb.models.Order order = orders.stream().filter(o -> o.getOrderId().equals(orderId))
					.findFirst().orElse(null);
			Optional<User> farmerList = farmerService.findUserByCropId(cropId);
			User farmer = farmerList.get();
			System.out.println(farmer.getfirstName());
//			
			OrderItem orderItem = new OrderItem();
			OrderItem orderItemconsumer = new OrderItem();
//			com.F2C.jwt.mongodb.models.Order orderFarmer = new com.F2C.jwt.mongodb.models.Order();
//			orderFarmer = order;
			List<OrderItem> odItemListconsumer = order.getItems();
			for (OrderItem orderitem : odItemListconsumer) {
				if (orderitem.getCropId().equals(cropId)) {

					orderitem.setRazorpay_order_Id(razorpay_order_Id);
					orderitem.setRazorpay_payment_Id(razorpay_payment_Id);
					orderitem.setSignature(signature);
					orderItemconsumer = orderitem;// it is consumer list to store all payment details
					orderItem = orderitem;// it is farmers list to store all payment details
					orderItem.setUserId(userId);
					orderItem.setUserName(consumer.getfirstName() + consumer.getlastName());
					orderItem.setUserContact(consumer.getphoneNo());
					orderItem.setUserAddress(consumer.getaddress());
					orderItem.setPaymentStatus("Amount Paid");
					
					orderItemconsumer.setFarmerId(farmer.getId());
					orderItemconsumer.setFarmerName(farmer.getfirstName() + farmer.getlastName());
					orderItemconsumer.setFarmerContact(farmer.getphoneNo());
					orderItemconsumer.setFarmerAddress(farmer.getaddress());
					orderItemconsumer.setPaymentStatus("Amount Paid");
					odItemListconsumer.remove(orderitem);
					orders.remove(order);
					break;
				}
				
			}

			odItemListconsumer.add(orderItemconsumer);
			order.setItems(odItemListconsumer);
			orders.add(order);
			consumer.setOrders(orders);
			
			userRepository.save(consumer);

			List<com.F2C.jwt.mongodb.models.Order> ordersFarmerList = farmer.getOrders();
			System.out.println("List of Farmer's Orders" + ordersFarmerList);
			com.F2C.jwt.mongodb.models.Order orderFarmer = ordersFarmerList.stream()
					.filter(o -> o.getOrderId().equals(orderId)).findFirst().orElse(null);
//			System.out.println("Orderfarmer" +orderFarmer);
////			orderFarmer = order;
//			List<OrderItem> odItemList = orderFarmer.getItems();
//					
//			
//			
//
//			odItemList.add(orderItem);
//			orderFarmer.setItems(odItemList);
//			ordersFarmerList.add(orderFarmer);
//			farmer.setOrders(ordersFarmerList);
//			userRepository.save(farmer);

			if (orderFarmer == null) {
				// Create a new order for the farmer
				orderFarmer = new com.F2C.jwt.mongodb.models.Order();
				orderFarmer.setOrderId(orderId);
			

				List<OrderItem> newOrderItemList = new ArrayList<>();
				newOrderItemList.add(orderItem);
				orderFarmer.setOrderDateTime(order.getOrderDateTime());
				orderFarmer.setProbableDeliveryDateTime(order.getProbableDeliveryDateTime());
				orderFarmer.setAddress(order.getAddress());
				orderFarmer.setOrderStatus(order.getOrderStatus());
				orderFarmer.setTotalAmount(order.getTotalAmount());
				orderFarmer.setTotalQuantity(order.getTotalQuantity());

				orderFarmer.setItems(newOrderItemList);
				ordersFarmerList.add(orderFarmer);
				farmer.setOrders(ordersFarmerList);
				userRepository.save(farmer);
//				System.out.println("Payment details saved successfully");
			} else {
				// OrderFarmer is not null, proceed with adding the item to the existing order
				List<OrderItem> odItemList = orderFarmer.getItems();
				odItemList.add(orderItem);
				orderFarmer.setOrderDateTime(order.getOrderDateTime());
				orderFarmer.setProbableDeliveryDateTime(order.getProbableDeliveryDateTime());
				orderFarmer.setAddress(order.getAddress());
				orderFarmer.setOrderStatus(order.getOrderStatus());
				orderFarmer.setTotalAmount(order.getTotalAmount());
				orderFarmer.setTotalQuantity(order.getTotalQuantity());

				orderFarmer.setItems(odItemList);
				ordersFarmerList.add(orderFarmer);
				farmer.setOrders(ordersFarmerList);
				userRepository.save(farmer);
//				System.out.println("Payment details saved successfully");
			}

			System.out.println("Payment details saved successfully");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error saving payment details");
		}
	}

	private String generateSHA256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}