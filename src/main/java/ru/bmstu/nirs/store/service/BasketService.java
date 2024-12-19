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
    public Optional<Basket> findById(int id) {
        return basketRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Basket findByUserId(int id) {
        var user = userService.findById(id);
        var basket = basketRepository.findBasketByUser(user.get());

        if (basket.isPresent()) {
            return basket.get();
        } else {
            var newBasket = new Basket(user.get());
            basketRepository.save(newBasket);
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
        var basketOpt = getCurrentBasket();

        if (basketOpt.isPresent()) {
            var basket = basketOpt.get();
            if (basket.getItems().stream().anyMatch(basketItem -> basketItem.getId() == id)) {
                jdbcTemplate.update(
                        "UPDATE basket_item SET quantity = quantity + 1 WHERE basket_id=? AND item_id=?",
                        basket.getId(), id);
            } else {
                jdbcTemplate.update(
                        "INSERT INTO basket_item (basket_id, item_id, quantity) VALUES (?, ?, 1)",
                        basket.getId(), id);
            }
            basket.setTotalAmount(basket.getTotalAmount().add(itemService.findById(id).get().getSellingPrice()));
        }
    }

    public void updateItemQuantity(int itemId, int quantity) {
        var basketOpt = getCurrentBasket();
        if (basketOpt.isPresent()) {
            var basket = basketOpt.get();
            var item = itemService.findById(itemId).get();
            setItemsQuantity(basket);
            var prevQuantity = basket.getQuantities().get(item);

            jdbcTemplate.update(
                    "UPDATE basket_item SET quantity = ? WHERE basket_id=? AND item_id=?",
                    quantity, basket.getId(), itemId);
            if (prevQuantity > quantity) {
                basket.setTotalAmount(basket.getTotalAmount().subtract(item.getSellingPrice().multiply(BigDecimal.valueOf(prevQuantity - quantity))));
            } else {
                basket.setTotalAmount(basket.getTotalAmount().add(item.getSellingPrice().multiply(BigDecimal.valueOf(quantity - prevQuantity))));
            }
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

    public Optional<Basket> getCurrentBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = authentication.getName();
            User user = userRepository.findUserByUsername(username).get();
            return basketRepository.findBasketByUser(user);
        }
        return Optional.empty();
    }
}