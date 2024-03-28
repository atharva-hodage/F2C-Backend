package com.F2C.jwt.mongodb.models;
import java.util.List;
import lombok.Data;
@Data
public class ImageResponseForm {
	
	
	//private List<byte[]> images;
	
	 private String requestId;
	
	 private String farmLocation;
private double farmArea;
	
	private List<byte[]> imgs;
	private List<byte[]> docs;
	private List<String> imageListIds;
	private List<String> docListIds;
}


