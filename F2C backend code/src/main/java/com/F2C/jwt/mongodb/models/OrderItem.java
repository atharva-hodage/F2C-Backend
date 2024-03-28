package com.F2C.jwt.mongodb.models;

import lombok.Data;

@Data
public class OrderItem {
	//@Id
	private String orderItemId;
	private String cropId;
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
	 private String signature;
	 private String paymentStatus;
}
