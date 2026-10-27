package com.example.zensai.service;

import com.example.zensai.domain.Inventory;
import com.example.zensai.domain.Product;
import com.example.zensai.dto.InventoryResponse;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.repository.InventoryRepository;
import com.example.zensai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product ID: " + productId));

        return toResponse(inventory);
    }

    public InventoryResponse addStock(Long productId, Integer quantityToAdd) {
        validateQuantity(quantityToAdd);

        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setAvailableQuantity(0);
                    newInventory.setReservedQuantity(0);

                    return newInventory;
                });

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantityToAdd);

        return toResponse(inventoryRepository.save(inventory));
    }

    public void reserveStock(Long productId, Integer quantityToReserve) {
        validateQuantity(quantityToReserve);

        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product"));

        if (inventory.getAvailableQuantity() < quantityToReserve) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantityToReserve);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantityToReserve);

        inventoryRepository.save(inventory);
    }

    public void confirmPurchase(Long productId, Integer quantityPurchased) {
        validateQuantity(quantityPurchased);

        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (inventory.getReservedQuantity() < quantityPurchased) {
            throw new IllegalArgumentException("Reserved stock is insufficient");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantityPurchased);

        inventoryRepository.save(inventory);
    }

    public void releaseReservation(Long productId, Integer quantityToRelease) {
        validateQuantity(quantityToRelease);

        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (inventory.getReservedQuantity() < quantityToRelease) {
            throw new IllegalArgumentException("Reserved stock is insufficient");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantityToRelease);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantityToRelease);

        inventoryRepository.save(inventory);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProduct().getId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}