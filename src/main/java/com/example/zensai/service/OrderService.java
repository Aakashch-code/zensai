package com.example.zensai.service;

import com.example.zensai.domain.*;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.repository.CustomerRepository;
import com.example.zensai.repository.OrderRepository;
import com.example.zensai.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService extends SecuredService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;

    public Orders initiateCheckout(Map<Long, Integer> cartItems) {

        UUID userId = currentUserId();

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Orders order = new Orders();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.RESERVED);
        order.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            inventoryService.reserveStock(productId, quantity);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtPurchase(product.getPrice());

            order.getItems().add(item);

            total += product.getPrice() * quantity;
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }
}