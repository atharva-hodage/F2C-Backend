package com.F2C.jwt.mongodb.controllers;


import com.F2C.jwt.mongodb.services.RazorpayService;
//import com.payment.service.RazorpayService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins="http://localhost:3000")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

   // @PreAuthorize("consumer")
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestParam int amount) {
        try {
            String orderId = razorpayService.createOrder(amount);
            return ResponseEntity.ok(orderId);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestParam String orderId, @RequestParam String paymentId, @RequestParam String signature,@RequestParam String userId,@RequestParam String billingId,@RequestParam String cropId) {
        try {
            razorpayService.verifyPayment(orderId, paymentId, signature,userId,billingId,cropId);
            return ResponseEntity.ok("Payment verified successfully");
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    @PostMapping("/verify")
//    public ResponseEntity<String> verifyPayment(@RequestParam String orderId, @RequestParam String paymentId, @RequestParam String signature) {
//        try {
//            razorpayService.verifyPayment(orderId, paymentId, signature);
//            return ResponseEntity.ok("Payment verified successfully");
//        } catch (RazorpayException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}