package com.F2C.jwt.mongodb.models;

import java.util.List;

import lombok.Data;

@Data
public class CartItem {

	    //@Id
	    private String cartItemId;
	    private List<CropDetails> cropDetailsList;
	    private Double cartItemPrice = 0.0;
	    private Long cartItemQuantity = 0L;
	   
	}