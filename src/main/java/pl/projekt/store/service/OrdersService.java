package pl.projekt.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import pl.projekt.store.model.Orders;
import pl.projekt.store.model.Products;
import pl.projekt.store.repository.OrdersRepository;
import pl.projekt.store.repository.ProductsRepository;

@Service
public class OrdersService {

    public OrdersService(OrdersRepository ordersRepository, ProductsRepository productsRepository) {
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
    }

    private final OrdersRepository ordersRepository;

    private final ProductsRepository productsRepository;

    public List<Orders> findAllOrders() {
        return ordersRepository.findAll();
    }

    public Orders createOrder(Long userid, BigDecimal price, Orders.OrderState status) {
        Orders order = new Orders();
        order.setUserId(userid);
        order.setTotalPrice(price);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now());
        return ordersRepository.save(order);
    }

    public BigDecimal calculateOrderPrice(List<Long> productIds) {
        List<Products> products = productsRepository.findAllById(productIds);

        return products.stream()
                .map(Products::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); 
    }

    public boolean areProductsAvailable(List<Long> productIds) {
        for (Long productId : productIds) {
            Products product = productsRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produkt o ID " + productId + " nie istnieje"));

            if (product.getStock() < 0) {
                return false;
            }
        }
        return true; 
    }

    public Optional<Orders> getOrderById(Long id) {
        return ordersRepository.findById(id);
    }

    public void deleteOrder(Long orderId) {
        Orders existingOrder = ordersRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Zamówienie nie istnieje."));
        ordersRepository.delete(existingOrder);
    }

}
