package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item save(Long userId, ItemShort itemShort);

    Item update(Long userId, Long itemId, ItemShort itemShort);

    Item getItemById(Long userId, Long itemId);

    List<Item> getAll(Long userId);

    List<Item> searchItems(Long userId, String text);
}