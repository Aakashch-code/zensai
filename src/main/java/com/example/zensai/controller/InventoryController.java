package com.example.zensai.controller;

import com.example.zensai.dto.InventoryResponse;
import com.example.zensai.dto.UpdateStockRequest;
import com.example.zensai.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventory(productId)
        );
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable Long productId,
            @RequestBody UpdateStockRequest request) {

        return ResponseEntity.ok(
                inventoryService.addStock(
                        productId,
                        request.getQuantity()
                )
        );
    }
}