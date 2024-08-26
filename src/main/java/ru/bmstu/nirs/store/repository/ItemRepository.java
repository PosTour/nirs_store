package ru.bmstu.nirs.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.nirs.store.domain.Category;
import ru.bmstu.nirs.store.domain.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemsByCategory(Category category);
}
