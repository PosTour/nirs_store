package ru.bmstu.nirs.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.nirs.store.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
