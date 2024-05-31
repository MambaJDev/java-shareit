package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Set<Item>> items = new HashMap<>();
    private static Long idGenerator = 1L;

    @Override
    public Item save(Long userId, Item item) {
        item.setId(idGenerator++);
        item.setOwner(userId);
        items.compute(userId, (ownerId, ownerItems) -> {
            if (ownerItems == null) {
                ownerItems = new HashSet<>();
            }
            ownerItems.add(item);
            return ownerItems;
        });
        log.info("User с id = {} добавил Item c id = {}", userId, item.getId());
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (items.get(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        Item itemForUpdate = items.get(userId).stream()
                .filter(earchItem -> earchItem.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        log.info("User с id = {} обновил Item c id = {}", userId, item.getId());
        return itemForUpdate;
    }

    @Override
    public Item getItemById(Long itemId) {
        Item itemById = getAllExistItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        log.info("Получен Item с id = {} ", itemId);
        return itemById;
    }

    @Override
    public List<Item> getAll(Long userId) {
        List<Item> itemList = new ArrayList<>(items.get(userId));
        log.info("Получен список Item Юзера с id = {}. Размер списка = {}", userId, itemList.size());
        return itemList;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> itemList = getAllExistItems().stream()
                .filter(item -> item.getAvailable() && (
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
        log.info("Получен список доступных Item содержащих в названии или описании = '{}'. Размер списка = {}", text, itemList.size());
        return itemList;
    }

    private List<Item> getAllExistItems() {
        return items.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}