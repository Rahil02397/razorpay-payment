package com.razorpay.razorpay;
import com.razorpay.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;


    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderDetails orderDetails) {
        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount",orderDetails.getAmount());
            orderRequest.put("currency",orderDetails.getCurrency());
            orderRequest.put("receipt", UUID.randomUUID().toString());
            JSONObject notes = new JSONObject();
            notes.put(orderDetails.getNoteSubject(),orderDetails.getNoteContent());
            JSONObject notes1 = orderRequest.put("notes", notes);
            com.razorpay.Order order1 = razorpay.orders.create(orderRequest);
            return new ResponseEntity<>(order1.get("id").toString(), HttpStatus.CREATED);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating order");
        }
    }

    @PostMapping("/payment-success")
    public ResponseEntity<?> paymentSuccess(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            JSONObject jsonObject = new JSONObject(payload);
            String orderId = jsonObject.getString("razorpay_order_id");
            String paymentId = jsonObject.getString("razorpay_payment_id");
            String signatureToVerify = orderId + "|" + paymentId;
            boolean isValidSignature = Utils.verifySignature(signatureToVerify, signature, apiSecret);
            if (isValidSignature) {
                // Payment success logic
                return ResponseEntity.ok("Payment successful");
            } else {
                return ResponseEntity.status(400).body("Invalid signature");
            }
        } catch (RazorpayException | JSONException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing payment");
        }
    }
}
