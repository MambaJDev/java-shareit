package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoResponse getItemById(Long userId, Long itemId);

    List<ItemDtoResponse> getAll(Long userId);

    List<ItemDto> searchItems(Long userId, String text);

    CommentDtoResponse addComment(Long userId, Long itemId, CommentDto commentDto);
}