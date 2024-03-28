package com.F2C.jwt.mongodb.services;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.F2C.jwt.mongodb.models.UserProfilePictureResponse;
import com.F2C.jwt.mongodb.models.UserProfileResponse;

@Service
public interface UserProfileService {
	UserProfileResponse getUserProfile(String userId);

	String saveProfilePicture(String userId, MultipartFile file) throws IOException;

	UserProfilePictureResponse getProfilePictures(String userId);

}
