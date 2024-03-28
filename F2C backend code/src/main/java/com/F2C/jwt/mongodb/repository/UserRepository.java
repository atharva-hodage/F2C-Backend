package com.F2C.jwt.mongodb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Query;

import com.F2C.jwt.mongodb.models.Role;
import com.F2C.jwt.mongodb.models.User;

public interface UserRepository extends MongoRepository<User, String> {
	  Optional<User> findByPhoneNo(String phoneNo);

	  Boolean existsByPhoneNo(String phoneNo);

	  Boolean existsByEmail(String email);
	 // User findByPhoneNo1(String phoneNo);
	  
	  Optional<User> findById(String id);
	  
	  List<User> findByRoles(Role role);

	  //assign farmer to QC ( RequestId)
	Optional<User> findUserByRequestListRequestId(String requestId);

	Optional<User> findUserByCcAdminResponsesReqForQCCC(String requestId);

	//new for pagination
	  @Query(value = "{'cropDetails': {$ne: null}}")
	    List<User> findAllWithCropDetails();
	  
	  
	  
//	    List<User> findByCropDetailsCropId(String cropId);

	
	    Optional<User> findByCropDetailsCropId(String cropId);
	//  List<User> findByAddressAndQcAvailable(String address, String qcAvailable);
	  
	  //for crop name and crop details in request info 
	 // Optional<User> findByRequestId(String requestId);
	
	  
	  
}
