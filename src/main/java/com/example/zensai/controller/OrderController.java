package com.example.zensai.controller;

import com.example.zensai.domain.Orders;
import com.example.zensai.dto.CheckoutRequest;
import com.example.zensai.dto.OrderResponse;
import com.example.zensai.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> checkout(
            @RequestBody CheckoutRequest request) {

        Orders order = orderService.initiateCheckout(
                request.getCartItems()
        );

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setExpiresAt(order.getExpiresAt());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}