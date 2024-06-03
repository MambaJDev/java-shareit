package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        checkUserIsPresent(userId);
        Item item = itemRepository.save(userId, itemMapper.toItem(itemDto));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        checkUserIsPresent(userId);
        Item item = itemRepository.getItemById(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new IllegalArgumentException("User is not owner of item");
        }
        itemMapper.updateItemFromItemDto(itemDto, item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        checkUserIsPresent(userId);
        Item item = itemRepository.getItemById(itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        checkUserIsPresent(userId);
        return itemRepository.getAll(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        checkUserIsPresent(userId);
        if (text.isEmpty()) {
            log.info("Параметр поиска пустой. Получен пустой список Item");
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkUserIsPresent(Long userId) {
        if (userRepository.getUserById(userId) == null) {
            log.info("User с ID = {} не найден", userId);
            throw new IllegalArgumentException("User not found");
        }
    }
}