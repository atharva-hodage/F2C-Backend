package com.F2C.jwt.mongodb.services.impl;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.F2C.jwt.mongodb.models.Images;
import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.models.UserProfilePictureResponse;
import com.F2C.jwt.mongodb.models.UserProfileResponse;
import com.F2C.jwt.mongodb.repository.ImageRepository;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService{
	@Autowired
	private UserRepository userRepository;
	
	 @Autowired
	    private ImageRepository imageRepository;
	 @Override
	    public UserProfileResponse getUserProfile(String userId) {
	        Optional<User> userOptional = userRepository.findById(userId);

	        if (userOptional.isPresent()) {
	            User user = userOptional.get();
	            return new UserProfileResponse(
	                user.getfirstName(),
	                user.getlastName(),
	                user.getphoneNo(),
	                user.getEmail(),
	                user.getaddress()
	                // Add more fields as needed
	            );
	        } else {
	            throw new CustomEntityNotFoundException("User not found with ID: " + userId);
	        }
	    }
	@Override
	public String saveProfilePicture(String userId, MultipartFile file) throws IOException {
		Images image = new Images();
        image.setImage(file.getBytes());
        image = imageRepository.save(image);
        String imageId = image.getId();
        // Save the file path to the user profile or database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomEntityNotFoundException("User with ID " + userId + " not found."));

        //user.setProfilePicture(image);
        user.setImageId(imageId);
        userRepository.save(user);

        return "/photos/" + image.getId();  
	}
	@Override
	public UserProfilePictureResponse getProfilePictures(String userId) {
	    Optional<User> userOptional = userRepository.findById(userId);
	    List<byte[]> profilePictureBytesList = new ArrayList<>();

	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        String imageId = user.getImageId();

	        // Check if imageId is not null or empty before querying the database
	        if (imageId != null && !imageId.isEmpty()) {
	            Optional<Images> imageOptional = imageRepository.findById(imageId);
	            
	            if (imageOptional.isPresent()) {
	                Images image = imageOptional.get();
	                profilePictureBytesList.add(image.getImage());
	                return new UserProfilePictureResponse(user.getId(), profilePictureBytesList);
	            }
	        }
	    }

	    return null;
	}



}
