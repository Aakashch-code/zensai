package com.example.zensai.controller;

import com.example.zensai.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock/payments")
@RequiredArgsConstructor
public class MockPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout/{orderId}/success")
    public ResponseEntity<String> simulateSuccessfulPayment(
            @PathVariable Long orderId) {

        paymentService.simulateSuccessfulPayment(orderId);

        return ResponseEntity.ok(
                "Mock payment SUCCESS simulated for Order ID: "
                        + orderId
                        + ". Stock permanently deducted."
        );
    }

    @PostMapping("/checkout/{orderId}/fail")
    public ResponseEntity<String> simulateFailedPayment(
            @PathVariable Long orderId) {

        paymentService.simulateFailedPayment(orderId);

        return ResponseEntity.ok(
                "Mock payment FAILED simulated for Order ID: "
                        + orderId
                        + ". Stock reservation released."
        );
    }
}