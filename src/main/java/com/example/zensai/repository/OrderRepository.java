package com.example.zensai.repository;

import com.example.zensai.domain.Orders;
import com.example.zensai.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.status = :status AND o.expiresAt <= :now")
    List<Orders> findExpiredOrders(@Param("status") OrderStatus status, @Param("now") LocalDateTime now);
}