package com.example.zensai.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long orderId;
    private Double totalAmount;
    private String status;
    private LocalDateTime expiresAt;
}