package com.F2C.jwt.mongodb.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;

@Document(collection = "users")
@Data
public class User {
	@Id
	private String id;

	@NotBlank
	private String firstName;

	private String lastName;

	@NotBlank
	@Size(max = 20)
	private String phoneNo;

	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	@DBRef
	private Set<Role> roles = new HashSet<>();

	private String otp;

	private String address;

	//@DBRef
	//private String imageId;
	@Transient 
	private String imageId;

	private List<CropDetails> cropDetails;
	
	private boolean requestCreated;


	private List<CCToQCReq> requestList= new ArrayList<>();
	
	
	//For call center admin to view allocated requests 
	private List<CCToQCReq> allocatedRequests = new ArrayList<>();
	
	///   in collection , direct
	private String ccAvailable;
	private String qcAvailable;
	
	private List<CCAdminResponse> ccAdminResponses = new ArrayList<>();
	private Cart cart;
	private List<Order> orders = new ArrayList<>();

	public String getfirstName() {
		return firstName;
	}

	public void setfirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getlastName() {
		return lastName;
	}

	public void setlastName(String lastName) {
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getphoneNo() {
		return phoneNo;
	}

	public void setphoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public boolean getRequestCreated() {
		return requestCreated;
	}

	public void getRequestCreated(boolean requestCreated) {
		this.requestCreated = requestCreated;
	}

	public User(String phoneNo, String email, String firstName, String lastName, String address, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNo = phoneNo;
		this.email = email;
		this.password = password;
		this.address = address;

	}

//	public User(String id, @NotBlank String firstName, String lastName, @NotBlank @Size(max = 20) String phoneNo,
//			@Size(max = 50) @Email String email, @NotBlank @Size(max = 120) String password, Set<Role> roles,
//			String otp, String address, String imageId, List<CropDetails> cropDetails) {
//		super();
//		this.id = id;
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.phoneNo = phoneNo;
//		this.email = email;
//		this.password = password;
//		this.roles = roles;
//		this.otp = otp;
//		this.address = address;
//		this.imageId = imageId;
//		this.cropDetails = cropDetails;
//	}

//	public Farmer(String farmer_id, @NotBlank String firstName, String lastName,
//			@NotBlank @Size(max = 12) String phoneNo, @Size(max = 50) @Email String email,
//			@NotBlank @Size(max = 120) String password, Set<Role> roles, String otp, String address, String imageId,
//			List<CropDetails> cropDetails) {
//		super();
//		this.farmer_id = farmer_id;
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.phoneNo = phoneNo;
//		this.email = email;
//		this.password = password;
//		this.roles = roles;
//		this.otp = otp;
//		this.address = address;
//		this.imageId = imageId;
//		this.cropDetails = cropDetails;
//	}
}
