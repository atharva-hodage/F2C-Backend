package com.F2C.jwt.mongodb.models;

import java.util.List;

import lombok.Data;
//Created this by Pranjali for profile management display picture 
@Data
public class UserProfilePictureResponse {

    public UserProfilePictureResponse(String id, List<byte[]> profilePictureBytesList2) {
		// TODO Auto-generated constructor stub
	}

	private String userId;
    
    private List<byte[]> profilePictureBytesList;

    // Getters and setters

   
}
