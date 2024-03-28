package com.F2C.jwt.mongodb.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {
    @Value("${twilio.AccountSID}")
    private String AccountSid;

    @Value("${twilio.AuthToken}")
    private String AuthToken;

    @Value("${twilio.phoneNumber}")
    private String phoneNumber;

    public void sendOtp(String phoneNo, String otp) {
        Twilio.init(AccountSid, AuthToken);	
        
        String pno="+91";
        pno+=phoneNo;
        System.out.println(pno);
        Message.creator(
                new PhoneNumber(pno),
                new PhoneNumber(phoneNumber),
                "Your OTP is: " + otp
        ).create();
    }
}
