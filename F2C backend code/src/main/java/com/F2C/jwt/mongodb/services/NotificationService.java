package com.F2C.jwt.mongodb.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(String userId, String message) {
        String destination = "/user/" + userId + "/queue/notification";
        messagingTemplate.convertAndSend(destination, message);
    }
}

