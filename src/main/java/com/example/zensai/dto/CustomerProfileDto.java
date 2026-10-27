package com.example.zensai.dto;

import lombok.Data;

@Data
public class CustomerProfileDto {
    private String phoneNumber;
    private String shippingAddress;
    private String billingAddress;
}