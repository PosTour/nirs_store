package ru.bmstu.nirs.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.nirs.store.domain.Basket;
import ru.bmstu.nirs.store.domain.Client;
import ru.bmstu.nirs.store.domain.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Optional<Basket> findBasketByCustomer(Client client);
}
