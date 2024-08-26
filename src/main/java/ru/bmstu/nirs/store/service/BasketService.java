package ru.bmstu.nirs.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Basket;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.repository.BasketRepository;

import java.util.*;

@Service
@Transactional
public class BasketService {

    private final BasketRepository basketRepository;
    private final ClientService clientService;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BasketService(BasketRepository basketRepository, ClientService clientService, ItemService itemService, JdbcTemplate jdbcTemplate) {
        this.basketRepository = basketRepository;
        this.clientService = clientService;
        this.itemService = itemService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Optional<Basket> findById(int id) {
        return basketRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Basket findByClientId(int id) {
        var client = clientService.findById(id);
        var basket = basketRepository.findBasketByCustomer(client.get());

        if (basket.isPresent()) {
            return basket.get();
        } else {
            var newBasket = new Basket(client.get());
            basketRepository.save(newBasket);
            return newBasket;
        }
    }

    @Transactional(readOnly = true)
    public void setItemsQuantity(Basket basket) {
        var basketId = basket.getId();
        Map<Item, Integer> quantities = new HashMap<>();

        basket.getItems().forEach(item -> {
            var quantity = jdbcTemplate.query("SELECT FROM basket_item WHERE basket_id=? AND item_id=?",
                    new Object[]{basketId, item.getId()},
                    new BeanPropertyRowMapper<>(Integer.class)).getFirst();
            quantities.put(item, quantity);
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

    public void addItem(int id, Basket basket) {
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

    public void delete(int id) {
        basketRepository.deleteById(id);
    }
}
