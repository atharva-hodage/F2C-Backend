package com.F2C.jwt.mongodb.models;

import lombok.Data;

//Created this by Pranjali for profile management 

@Data
public class UserProfileResponse {
	 private String firstName;
	    private String lastName;
	    private String phoneNo;
	    private String email;
	    private String address;
	    public UserProfileResponse(String firstName, String lastName, String phoneNo, String email, String address) {
	        this.firstName = firstName;
	        this.lastName = lastName;
	        this.phoneNo = phoneNo;
	        this.email = email;
	        this.address = address;
	    }
}
