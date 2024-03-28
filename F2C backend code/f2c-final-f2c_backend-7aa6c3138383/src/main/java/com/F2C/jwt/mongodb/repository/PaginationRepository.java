package com.F2C.jwt.mongodb.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.F2C.jwt.mongodb.models.CropDetails;
import com.F2C.jwt.mongodb.models.ERole;
import com.F2C.jwt.mongodb.models.User;

public interface PaginationRepository extends MongoRepository<User, String> {
	 Page<User> findByCropDetails_Published(boolean published, Pageable pageable);
	 Page<User> findByRolesName(ERole roleName, Pageable pageable);
	 Page<User> findAll(Pageable pageable);
//	    @Aggregation(pipeline = {
//	        "{ $unwind: '$cropDetails' }",
//	        "{ $match: { 'cropDetails.cropName': { $regex: ?0, $options: 'i' } } }",
//	        "{ $group: { _id: '$_id', cropDetails: { $push: '$cropDetails' }, otherFields: { $first: '$$ROOT' } } }",
//	        "{ $replaceRoot: { newRoot: { $mergeObjects: ['$otherFields', { cropDetails: '$cropDetails' }] } } }"
//	    })
//	    List<User> findByCropDetails_CropDetails_CropNameContainingIgnoreCase(String cropName, Pageable pageable);
	}

