package ru.bmstu.nirs.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Category;
import ru.bmstu.nirs.store.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    public void delete(int id) {
        categoryRepository.deleteById(id);
    }
}
