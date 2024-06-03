package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> searchItems(Long userId, String text);
}