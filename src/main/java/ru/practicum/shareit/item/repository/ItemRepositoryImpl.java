package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idGenerator = 1L;

    @Override
    public Item save(Long userId, Item item) {
        item.setId(idGenerator++);
        item.setOwner(userId);
        items.put(item.getId(), item);
        log.info("User с id = {} добавил Item c id = {}", userId, item.getId());
        return item;
    }

    @Override
    public Item update(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = items.get(itemId);
        log.info("Получен Item с id = {} ", itemId);
        return item;
    }

    @Override
    public List<Item> getAll(Long userId) {
        List<Item> itemList = items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
        log.info("Получен список Item Юзера с id = {}. Размер списка = {}", userId, itemList.size());
        return itemList;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> itemList = items.values().stream()
                .filter(item -> item.getAvailable() && (
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
        log.info("Получен список доступных Item содержащих в названии или описании = '{}'. Размер списка = {}", text, itemList.size());
        return itemList;
    }
}