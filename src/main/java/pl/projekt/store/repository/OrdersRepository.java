package pl.projekt.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.store.model.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByUserId(Long userId);
    Optional<Orders> findByOrderId(Long orderId);
}