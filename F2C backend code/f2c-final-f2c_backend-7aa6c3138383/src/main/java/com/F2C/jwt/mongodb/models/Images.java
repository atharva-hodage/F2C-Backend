package com.F2C.jwt.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;

@Data
@Document(collection = "images")
public class Images {
	  @Id
	    private String id;

	    private byte[] image;
	    
	    private Double latitude;

	    private Double longitude;
	    private byte[] doc;

}
