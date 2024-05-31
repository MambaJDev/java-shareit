package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public Item save(Long userId, ItemShort itemShort) {
        checkUserIsPresent(userId);
        return itemRepository.save(userId, itemMapper.toItem(itemShort));
    }

    @Override
    public Item update(Long userId, Long itemId, ItemShort itemShort) {
        checkUserIsPresent(userId);
        return itemRepository.update(userId, itemId, itemMapper.toItem(itemShort));
    }

    @Override
    public Item getItemById(Long userId, Long itemId) {
        checkUserIsPresent(userId);
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getAll(Long userId) {
        checkUserIsPresent(userId);
        return itemRepository.getAll(userId);
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        checkUserIsPresent(userId);
        if (text.isEmpty()) {
            log.info("Параметр поиска пустой. Получен пустой список Item");
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text);
    }

    private void checkUserIsPresent(Long userId) {
        if (userRepository.getUserById(userId) == null) {
            log.info("User с ID = {} не найден", userId);
            throw new IllegalArgumentException("User not found");
        }
    }
}