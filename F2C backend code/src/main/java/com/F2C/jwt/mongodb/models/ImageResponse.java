package com.F2C.jwt.mongodb.models;

import java.util.List;

import lombok.Data;

//import lombok.Data;

@Data
public class ImageResponse {
	//added by me 
	private String cropId;
private String cropName;
private Long cropQuantity;
private String description;
private List<byte[]> images;
private Double cropRetailPrice;
private Double cropWholesalePrice;
private String cropSubType;
private boolean organic;
private boolean published;
}