package com.F2C.jwt.mongodb.models;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
@Data
public class OrderResponse {
	private String orderId;
	private LocalDateTime orderDateTime; // Replace LocalDate with LocalDateTime
	private LocalDateTime probableDeliveryDateTime; // Added field for probable delivery date =+3
	private String address;
	private OrderStatus orderStatus; // To maintain order status
	private Double totalAmount;
	private Long totalQuantity;
	private String orderItemId;
	private String cropId;
	private String cropName;
	private List<byte[]> images;
	private String description;
	private Long quantity;
	private Double itemPrice;
	private String userId;
	private String userName;
	private String userAddress;
	private String userContact;
	private String farmerId;
	private String farmerName;
	private String farmerAddress;
	private String farmerContact;
	private String razorpay_payment_Id;
	private String razorpay_order_Id;
	private String paymentStatus;
	
	
	
	
	
}
