package ru.bmstu.nirs.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.nirs.store.domain.Client;
import ru.bmstu.nirs.store.domain.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findOrdersByCustomer(Client client);
}
