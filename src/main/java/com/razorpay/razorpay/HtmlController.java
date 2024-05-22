package com.razorpay.razorpay;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {


    // http://localhost:8080/payments-page
    @GetMapping("/payments-page")
    public String viewPaymentPage(){
        return "payment-page";
    }
}
