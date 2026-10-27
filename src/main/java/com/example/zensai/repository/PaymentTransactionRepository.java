package com.example.zensai.repository;

import com.example.zensai.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByGatewayReference(String gatewayReference);
    Optional<PaymentTransaction> findByOrderId(Long orderId);
}