package ru.bmstu.nirs.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.domain.Order;
import ru.bmstu.nirs.store.repository.OrderRepository;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;
    private final BasketService basketService;

    public void save(String phone, String city, String address) {
        var basket = basketService.getCurrentBasket().get();
        basketService.setItemsQuantity(basket);

        var order = new Order(clientService.findByPhone(phone).get(),
                city,
                address,
                new Date(),
                basket.getTotalAmount());

        order.setQuantities(basket.getQuantities());
        orderRepository.save(order);

        order.getQuantities().forEach((key, value) -> {
            itemService.decreaseStock(key.getId(), value);
            jdbcTemplate.update(
                    "INSERT INTO order_item (order_id, item_id, quantity) VALUES (?, ?, ?)",
                    order.getId(), key.getId(), value);
        });

        basketService.clear(basket.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByClientId(int id) {
        var client = clientService.findById(id);
        return orderRepository.findOrdersByCustomer(client.get());
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public void setItemsQuantity(Order order) {
        var orderId = order.getId();
        Map<Item, Integer> quantities = new HashMap<>();

        order.getItems().forEach(item -> {
            var quantity = jdbcTemplate.queryForObject(
                    "SELECT quantity FROM order_item WHERE order_id=? AND item_id=?",
                    new Object[]{orderId, item.getId()},
                    Integer.class
            );
            quantities.put(item, quantity != null ? quantity : 0);
        });

        order.setQuantities(quantities);
    }

    public void update(int id, Order updatedOrder) {
        var optOrder = orderRepository.findById(id);
        if (optOrder.isPresent()) {
            var order = optOrder.get();
            updatedOrder.setId(id);
            updatedOrder.setOrderDate(order.getOrderDate());

            order.getQuantities().forEach((key, value) -> {
                var newQuantity = updatedOrder.getQuantities().getOrDefault(key, 0);
                if (!Objects.equals(newQuantity, value)) {
                    if (newQuantity != 0) {
                        if (newQuantity > value) {
                            itemService.decreaseStock(key.getId(), newQuantity - value);
                        } else {
                            itemService.increaseStock(key.getId(), value - newQuantity);
                        }
                        jdbcTemplate.update(
                                "UPDATE order_item SET quantity=? WHERE order_id=? AND item_id=?",
                                value, id, key.getId());
                    } else {
                        itemService.increaseStock(key.getId(), value);
                        jdbcTemplate.update(
                                "DELETE FROM order_item WHERE order_id=? AND item_id=?",
                                id, key.getId());
                    }
                }
            });

            updatedOrder.getQuantities().forEach((key, value) -> {
                if (!order.getQuantities().containsKey(key)) {
                    itemService.increaseStock(key.getId(), value);
                    jdbcTemplate.update(
                            "INSERT INTO order_item (order_id, item_id, quantity) VALUES (?, ?, ?)",
                            id, key.getId(), value);
                }
            });

            orderRepository.save(updatedOrder);
        }
    }

    public void delete(int id) {
        orderRepository.deleteById(id);
    }
}
