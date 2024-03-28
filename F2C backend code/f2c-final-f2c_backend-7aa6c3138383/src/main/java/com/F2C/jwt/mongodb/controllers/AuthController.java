package com.F2C.jwt.mongodb.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.F2C.jwt.mongodb.models.CCToQCReq;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.payload.request.LoginRequest;
import com.F2C.jwt.mongodb.payload.request.SignupRequest;
import com.F2C.jwt.mongodb.payload.response.JwtResponse;
import com.F2C.jwt.mongodb.payload.response.MessageResponse;
import com.F2C.jwt.mongodb.repository.RoleRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.security.JwtUtils;
import com.F2C.jwt.mongodb.security.TokenBasedAuthentication;
import com.F2C.jwt.mongodb.services.FarmerService;
import com.F2C.jwt.mongodb.services.QCandAdminService;
import com.F2C.jwt.mongodb.services.UserDetailsImpl;
import com.F2C.jwt.mongodb.services.impl.UserDetailsServiceImpl;

//@CrossOrigin(origins ="http://localhost:8080/*", maxAge = 3600)

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	private QCandAdminService qcAndAdminService;
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private FarmerService farmerService;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getphoneNo(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}
	  
	@PostMapping("/loginViaOTP")
	public ResponseEntity<?> LoginViaOTP(@RequestParam String phoneNo ) {
       String generateOTP = farmerService.sendOtpForLogin(phoneNo);
       
       return ResponseEntity.ok("OTP has been sent to your phone number"+generateOTP);
		
		
	}
	

@PostMapping("/signin-otp")
public ResponseEntity<?> authenticateUserWithOTP(@RequestParam String phoneNo, @RequestParam String otp) {
    // Validate OTP
	String otpValid = farmerService.verifyOtpForLogin(phoneNo, otp);
	
    if (!otpValid.equals("Otp Valid")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid OTP");
    }

    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(phoneNo);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    String jwt = jwtUtils.generateJwtToken(authenticationToken);

    return ResponseEntity.ok(jwt+"\n"+userDetails.getUsername()+"");
}



	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByPhoneNo(signUpRequest.getphoneNo())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: phoneNo is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(
				signUpRequest.getphoneNo(), 
				 signUpRequest.getEmail(),
				 signUpRequest.getfirstName(),
				 signUpRequest.getlastName(),
				 signUpRequest.getaddress(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		
		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_CONSUMER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "farmer":
					Role farmRole = roleRepository.findByName(ERole.ROLE_FARMER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(farmRole);

					break;
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "qualitycheck":
					Role modRole = roleRepository.findByName(ERole.ROLE_QUALITYCHECK)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_CONSUMER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		
		
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	


}
