package com.example.zensai.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class InventoryResponse {
    private Long productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;
}