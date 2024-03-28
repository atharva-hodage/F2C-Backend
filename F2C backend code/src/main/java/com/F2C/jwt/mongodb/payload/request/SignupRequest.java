package com.F2C.jwt.mongodb.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class SignupRequest {
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String firstName;

	private String lastName;
	
	@NotBlank
	@Size(min = 3, max = 20)
	private String phoneNo;


	@Size(max = 50)
	@Email
	private String email;
	
	private String address;
	
	@NotBlank
	@Size(min = 6, max = 40)
	private String password;

	private Set<String> roles;

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
	
	public String getlastName() {
		return lastName;
	}

	public void setlastName(String lastName) {
		this.lastName = lastName;
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

	public Set<String> getRoles() {
		return this.roles;
	}

	public void setRole(Set<String> roles) {
		this.roles = roles;
	}

//	public FSignupRequest(@NotBlank @Size(min = 3, max = 20) String firstName, String lastName,
//			@NotBlank @Size(min = 3, max = 20) String phoneNo, @Size(max = 50) @Email String email, String address,
//			@NotBlank @Size(min = 6, max = 40) String password, Set<String> roles) {
//		super();
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.phoneNo = phoneNo;
//		this.email = email;
//		this.address = address;
//		this.password = password;
//		this.roles = roles;
//	}
//
//	public FSignupRequest() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
}
