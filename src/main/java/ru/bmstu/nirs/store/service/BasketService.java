package ru.bmstu.nirs.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Basket;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.domain.Order;
import ru.bmstu.nirs.store.domain.User;
import ru.bmstu.nirs.store.repository.BasketRepository;
import ru.bmstu.nirs.store.repository.UserRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Basket viewBasket() {
        var basket = getCurrentBasket();
        setItemsQuantity(basket);
        setTotalAmount(basket);
        return basket;
    }

    @Transactional(readOnly = true)
    public Basket findById(int id) {
        var basket = basketRepository.findById(id).get();
        setItemsQuantity(basket);
        setTotalAmount(basket);
        return basket;
    }

    @Transactional(readOnly = true)
    public Basket findByUserId(int id) {
        var user = userService.findById(id);
        var basketOpt = basketRepository.findBasketByUser(user.get());

        if (basketOpt.isPresent()) {
            var basket = basketOpt.get();
            setItemsQuantity(basket);
            setTotalAmount(basket);
            return basket;
        } else {
            var newBasket = new Basket(user.get());
            basketRepository.save(newBasket);
            newBasket.setTotalAmount(BigDecimal.ZERO);
            return newBasket;
        }
    }

    @Transactional(readOnly = true)
    public void setItemsQuantity(Basket basket) {
        var basketId = basket.getId();
        Map<Item, Integer> quantities = new HashMap<>();

        basket.getItems().forEach(item -> {
            var quantity = jdbcTemplate.queryForObject(
                    "SELECT quantity FROM basket_item WHERE basket_id = ? AND item_id = ?",
                    new Object[]{basketId, item.getId()},
                    Integer.class
            );
            quantities.put(item, quantity != null ? quantity : 0);
        });

        basket.setQuantities(quantities);
    }

    @Transactional(readOnly = true)
    public void setTotalAmount(Basket basket) {
        basket.setTotalAmount(BigDecimal.ZERO);
        basket.getItems().forEach(item -> {
            var quantity = jdbcTemplate.queryForObject(
                    "SELECT quantity FROM basket_item WHERE basket_id=? AND item_id=?",
                    new Object[]{basket.getId(), item.getId()},
                    Integer.class
            );
            basket.setTotalAmount(basket.getTotalAmount().add(item.getSellingPrice().multiply(BigDecimal.valueOf(quantity))));
        });
    }

    public void update(int id, Basket updatedBasket) {
        var optBasket = basketRepository.findById(id);
        if (optBasket.isPresent()) {
            var basket = optBasket.get();
            updatedBasket.setId(id);

            basket.getQuantities().forEach((key, value) -> {
                var newQuantity = updatedBasket.getQuantities().getOrDefault(key, 0);
                if (!Objects.equals(newQuantity, value)) {
                    if (newQuantity != 0) {
                        if (newQuantity > value) {
                            itemService.decreaseStock(key.getId(), newQuantity - value);
                        } else {
                            itemService.increaseStock(key.getId(), value - newQuantity);
                        }
                        jdbcTemplate.update(
                                "UPDATE basket_item SET quantity=? WHERE basket_id=? AND item_id=?",
                                value, id, key.getId());
                    } else {
                        itemService.increaseStock(key.getId(), value);
                        jdbcTemplate.update(
                                "DELETE FROM basket_item WHERE basket_id=? AND item_id=?",
                                id, key.getId());
                    }
                }
            });

            updatedBasket.getQuantities().forEach((key, value) -> {
                if (!basket.getQuantities().containsKey(key)) {
                    itemService.increaseStock(key.getId(), value);
                    jdbcTemplate.update(
                            "INSERT INTO basket_item (basket_id, item_id, quantity) VALUES (?, ?, ?)",
                            id, key.getId(), value);
                }
            });

            basketRepository.save(updatedBasket);
        }
    }

    public void addItem(int id) {
        var basket = getCurrentBasket();

        if (basket != null) {
            if (basket.getItems().stream().anyMatch(basketItem -> basketItem.getId() == id)) {
                jdbcTemplate.update(
                        "UPDATE basket_item SET quantity = quantity + 1 WHERE basket_id=? AND item_id=?",
                        basket.getId(), id);
            } else {
                jdbcTemplate.update(
                        "INSERT INTO basket_item (basket_id, item_id, quantity) VALUES (?, ?, 1)",
                        basket.getId(), id);
            }
        }
    }

    public void updateItemQuantity(int itemId, int quantity) {
        var basket = getCurrentBasket();
        if (basket != null) {
            setItemsQuantity(basket);

            jdbcTemplate.update(
                    "UPDATE basket_item SET quantity = ? WHERE basket_id=? AND item_id=?",
                    quantity, basket.getId(), itemId);
            basketRepository.save(basket);
        }

    }

    public void clear(int id) {
        var basketOpt = basketRepository.findById(id);
        if (basketOpt.isPresent()) {
            var basket = basketOpt.get();
            basket.setItems(null);
            basket.setTotalAmount(BigDecimal.valueOf(0));
            jdbcTemplate.update(
                    "DELETE FROM basket_item WHERE basket_id=?",
                    id);
            basketRepository.save(basket);
        }
    }

    public Basket getCurrentBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username).get();

            return basketRepository.findBasketByUser(user).orElse(null);
        }
        return null;
    }
}