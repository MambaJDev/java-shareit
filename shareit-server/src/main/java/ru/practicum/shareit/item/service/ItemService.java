package ru.practicum.shareit.item.service;


import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoResponse getItemById(Long userId, Long itemId);

    List<ItemDtoResponse> getAll(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(Long userId, String text, Integer from, Integer size);

    CommentDtoResponse addComment(Long userId, Long itemId, CommentDto commentDto);
}