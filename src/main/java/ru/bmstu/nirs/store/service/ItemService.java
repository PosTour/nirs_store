package ru.bmstu.nirs.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.nirs.store.domain.Item;
import ru.bmstu.nirs.store.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    @Autowired
    public ItemService(ItemRepository itemRepository, CategoryService categoryService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
    }

    public void save(Item item) {
        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> findById(int id) {
        return itemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> findByCategoryId(int id) {
        var category = categoryService.findById(id).get();
        return itemRepository.findItemsByCategory(category);
    }

    public void update(int id, Item updatedItem) {
        var item = itemRepository.findById(id);
        if (item.isPresent()) {
            updatedItem.setId(id);
            itemRepository.save(updatedItem);
        }
    }

    public void increaseStock(int id, int amount) {
        var item = itemRepository.findById(id);
        if (item.isPresent()) {
            item.get().setQuantity(item.get().getQuantity() + amount);
            itemRepository.save(item.get());
        }
    }

    public void decreaseStock(int id, int amount) {
        var item = itemRepository.findById(id);
        if (item.isPresent()) {
            item.get().setQuantity(item.get().getQuantity() - amount);
            itemRepository.save(item.get());
        }
    }

    public void delete(int id) {
        itemRepository.deleteById(id);
    }
}
