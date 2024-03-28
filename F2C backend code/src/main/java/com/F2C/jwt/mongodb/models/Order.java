package com.F2C.jwt.mongodb.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Order {
	@Id
	private String orderId;
	private LocalDateTime orderDateTime; // Replace LocalDate with LocalDateTime
	private LocalDateTime probableDeliveryDateTime; // Added field for probable delivery date =+3 
	private String address;
	private List<OrderItem> items;
	private OrderStatus orderStatus; //To maintain order status 
	private Double totalAmount;
	//total quantity
	 private Long totalQuantity;
	 
//	public String getCropId() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	

}
