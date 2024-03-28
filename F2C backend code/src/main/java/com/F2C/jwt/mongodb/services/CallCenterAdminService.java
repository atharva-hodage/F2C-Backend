package com.F2C.jwt.mongodb.services;

public interface CallCenterAdminService {
	//public List<User> getAllQualityCheckers();
    void sendNotification(String userId, String message);

}
