package com.example.zensai.scheduler;

import com.example.zensai.domain.OrderItem;
import com.example.zensai.domain.Orders;
import com.example.zensai.domain.OrderStatus;
import com.example.zensai.repository.OrderRepository;
import com.example.zensai.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReservationSweeper {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void releaseExpiredReservations() {
        List<Orders> expiredOrders = orderRepository.findExpiredOrders(OrderStatus.RESERVED, LocalDateTime.now());

        for (Orders order : expiredOrders) {
            log.info("Releasing expired order reservation: {}", order.getId());

            for (OrderItem item : order.getItems()) {
                inventoryService.releaseReservation(item.getProduct().getId(), item.getQuantity());
            }

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}