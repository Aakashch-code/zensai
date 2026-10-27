package com.example.zensai.service;

import com.example.zensai.domain.*;
import com.example.zensai.repository.OrderRepository;
import com.example.zensai.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentTransactionRepository paymentRepo;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    public void simulateSuccessfulPayment(Long orderId) {
        String gatewayReference = "mock_pi_" + UUID.randomUUID().toString().substring(0, 8);
        processPaymentWebhook(gatewayReference, "PAYMENT_SUCCESS", orderId);
    }

    public void simulateFailedPayment(Long orderId) {
        String gatewayReference = "mock_err_" + UUID.randomUUID().toString().substring(0, 8);
        processPaymentWebhook(gatewayReference, "PAYMENT_FAILED", orderId);
    }

    public void processPaymentWebhook(String gatewayReference, String eventType, Long orderId) {
        PaymentTransaction transaction = paymentRepo.findByGatewayReference(gatewayReference)
                .orElseGet(() -> createPendingTransaction(orderId, gatewayReference));

        if (transaction.getStatus() == PaymentStatus.SUCCESS ||
                transaction.getStatus() == PaymentStatus.FAILED) {
            log.info("Webhook already processed for gateway ref: {}", gatewayReference);
            return;
        }

        Orders order = transaction.getOrder();

        if ("PAYMENT_SUCCESS".equals(eventType)) {
            handleSuccess(transaction, order);
        } else if ("PAYMENT_FAILED".equals(eventType)) {
            handleFailure(transaction, order);
        }
    }

    private void handleSuccess(PaymentTransaction transaction, Orders order) {
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setUpdatedAt(LocalDateTime.now());

        order.setStatus(OrderStatus.PAID);

        paymentRepo.save(transaction);
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            inventoryService.confirmPurchase(item.getProduct().getId(), item.getQuantity());
        }

        log.info("Order {} successfully paid and stock confirmed.", order.getId());
    }

    private void handleFailure(PaymentTransaction transaction, Orders order) {
        transaction.setStatus(PaymentStatus.FAILED);
        transaction.setUpdatedAt(LocalDateTime.now());

        order.setStatus(OrderStatus.CANCELLED);

        paymentRepo.save(transaction);
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            inventoryService.releaseReservation(item.getProduct().getId(), item.getQuantity());
        }

        log.info("Order {} failed. Stock released.", order.getId());
    }

    private PaymentTransaction createPendingTransaction(Long orderId, String gatewayReference) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Order ID"));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setAmount(order.getTotalAmount());
        transaction.setGatewayReference(gatewayReference);

        return paymentRepo.save(transaction);
    }
}