package com.F2C.jwt.mongodb.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Cart {
	// @Id
	private String cartId;

	private List<CartItem> cropItems = new ArrayList<>();

	private Double finalPrice = 0.0;
	private Long finalQuantity = 0L;
	// private Double totalPrice;
	// private Long cartFinalQuantity;

}

