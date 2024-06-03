package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Long userId, Item item);

    Item update(Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> getAll(Long userId);

    List<Item> searchItems(String text);
}