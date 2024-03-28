package com.F2C.jwt.mongodb.models;

import java.util.List;




import lombok.Data;
@Data
public class CropDetails {
	
	

	private String cropId;
	
	private String cropName;
	
	private Long cropQuantity;
	
	private String description;
	
	private List<String> imageIds;
	
	private Double cropRetailPrice;
	
	private Double cropWholesalePrice;
	
	private String cropSubType;
	
	//organic inorganic 
	private boolean perishable;
	
	//true fault 
	private boolean approvalStatus;
	
	private boolean published;
	
	
	public CropDetails(String cropName, String description, String cropSubType, Double cropRetailPrice, Double cropWholesalePrice,
			List<String> imageIds, boolean approvalStatus, boolean published) {
		this.cropName = cropName;
		this.description = description;
		this.cropSubType = cropSubType;
		this.cropRetailPrice = cropRetailPrice;
		this.cropWholesalePrice = cropWholesalePrice;
		this.imageIds = imageIds;
		this.approvalStatus = approvalStatus;
		this.published = published;
	}


	public CropDetails() {
		// TODO Auto-generated constructor stub
	}
	public boolean getPublished() {
		return published;
	}
}
