package com.example.zensai.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CheckoutRequest {
    // A map representing the cart: Product ID -> Quantity
    private Map<Long, Integer> cartItems;
}